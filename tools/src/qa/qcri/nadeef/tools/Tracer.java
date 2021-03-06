/*
 * QCRI, NADEEF LICENSE
 * NADEEF is an extensible, generalized and easy-to-deploy data cleaning platform built at QCRI.
 * NADEEF means "Clean" in Arabic
 *
 * Copyright (c) 2011-2013, Qatar Foundation for Education, Science and Community Development (on
 * behalf of Qatar Computing Research Institute) having its principle place of business in Doha,
 * Qatar with the registered address P.O box 5825 Doha, Qatar (hereinafter referred to as "QCRI")
 *
 * NADEEF has patent pending nevertheless the following is granted.
 * NADEEF is released under the terms of the MIT License, (http://opensource.org/licenses/MIT).
 */

package qa.qcri.nadeef.tools;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.log4j.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

/**
 * Tracer is a logging tool which is used for debugging / profiling / benchmarking purpose.
 */
public class Tracer {
    //<editor-fold desc="Private fields">
    private static boolean infoFlag;
    private static boolean verboseFlag;
    private static PrintStream console;
    private static String logFileName;
    private static Calendar calendar;
    private static DateFormat dateFormat;
    private static ConsoleAppender consoleAppender;
    private static String logPrefix;
    private Logger logger;

    //</editor-fold>

    static {
        infoFlag = true;
        verboseFlag = false;
        console = System.out;
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMddHHmmss");
        logFileName = dateFormat.format(calendar.getTime()) + ".txt";
        consoleAppender = new ConsoleAppender(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"));

        Properties properties = System.getProperties();
        if (properties.containsKey("debug")) {
            setVerbose(true);
        }
    }

    //<editor-fold desc="Tracer creation">
    private Tracer(Class classType) {
        Preconditions.checkNotNull(classType);
        logger = Logger.getLogger(classType);
    }

    /**
     * Creates a tracer class
     * @param classType input class type.
     * @return Tracer instance.
     */
    public static Tracer getTracer(Class classType) {
        return new Tracer(classType);
    }

    //</editor-fold>

    //<editor-fold desc="Public methods">
    /**
     * Initialize the logging directory.
     * @param outputPathName output logging directory.
     */
    public static void setLoggingDir(String outputPathName) {
        File outputPath = new File(outputPathName);

        if (!outputPath.exists() || !outputPath.isDirectory()) {
            console.println("Output path is not a valid directory.");
            return;
        }

        String outputFile = outputPath + File.separator + getLogFileName();
        try {
            PatternLayout layout = new PatternLayout("%-4r [%t] %-5p %c %x - %m%n");
            FileAppender logFile = new FileAppender(layout, outputFile);
            Logger.getRootLogger().addAppender(logFile);
            Logger.getRootLogger().setLevel(Level.INFO);
        } catch (IOException e) {
            Tracer tracer = getTracer(Tracer.class);
            tracer.info("Cannot open log file : " + getLogFileName());
        }
    }

    /**
     * Sets the logging file name prefix.
     * @param logPrefix_ logging file name prefix.
     */
    public static void setLoggingPrefix(String logPrefix_) {
        logPrefix = logPrefix_;
    }

    /**
     * Set the output stream.
     * @param console_ input console print stream.
     */
    public static void setConsole(PrintStream console_) {
        console = Preconditions.checkNotNull(console_);
    }

    /**
     * Print out info message.
     * @param msg info message.
     */
    public void info(String msg) {
        if (isInfoOn()) {
            console.println(msg);
        }
        logger.info(msg);
    }

    /**
     * Print out verbose message.
     * @param msg message.
     */
    public void verbose(String msg) {
        if (isVerboseOn()) {
            logger.debug(msg);
        }
    }

    /**
     * Print out error message.
     * @param message error message.
     * @param ex exceptions.
     */
    public void err(String message, Exception ex) {
        if (!Strings.isNullOrEmpty(message)) {
            console.println("Error: " + message);
        }

        if (ex != null) {
            console.println("Exception: " + ex.getClass().getName() + ": " + ex.getMessage());
            if (isVerboseOn()) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                console.print(sw.toString());
                try {
                    pw.close();
                    sw.close();
                } catch (Exception e) {}
            }
        }
        logger.error(message, ex);
    }

    /**
     * Print out error message.
     * @param message error message.
     */
    public void err(String message) {
        if (!Strings.isNullOrEmpty(message)) {
            console.println("Error: " + message);
        }
        logger.error(message);
    }

    //</editor-fold>

    //<editor-fold desc="Static methods">
    /**
     * Turn on / off info printing flag.
     * @param mode on / off.
     */
    public static void setInfo(boolean mode) {
        infoFlag = mode;
        Logger root = Logger.getRootLogger();
        if (mode) {
            root.addAppender(consoleAppender);
        } else {
            root.removeAppender(consoleAppender);
        }
    }

    /**
     * Turn on / off verbose printing flag.
     * @param mode on / off.
     */
    public static void setVerbose(boolean mode) {
        verboseFlag = mode;
        Logger root = Logger.getRootLogger();
        if (verboseFlag) {
            root.setLevel(Level.ALL);
            root.addAppender(consoleAppender);
        } else {
            root.removeAppender(consoleAppender);
        }
    }

    /**
     * Returns <code>True</code> when Info flag is on.
     * @return <code>True</code> when Info flag is on.
     */
    public static boolean isInfoOn() {
        return infoFlag;
    }

    /**
     * Returns <code>True</code> when Verbose flag is on.
     * @return <code>True</code> when Verbose flag is on.
     */
    public static boolean isVerboseOn() {
        return verboseFlag;
    }

    //</editor-fold>

    //<editor-fold desc="Private Helper">

    private static String getLogFileName() {
        return logPrefix + logFileName;
    }
    //</editor-fold>
}
