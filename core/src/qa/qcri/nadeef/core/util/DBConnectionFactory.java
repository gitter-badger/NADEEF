/*
 * Copyright (C) Qatar Computing Research Institute, 2013.
 * All rights reserved.
 */

package qa.qcri.nadeef.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.SQLDialect;
import qa.qcri.nadeef.core.datamodel.CleanPlan;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Creates DB connection.
 */
public class DBConnectionFactory {

    // <editor-fold desc="Public methods">
    /**
     * Get the JDBC connection based on the dialect.
     * @param dialect Database type.
     * @param url Database URL.
     * @param userName login user name.
     * @param password Login user password.
     * @return JDBC connection.
     */
    public static Connection createConnection(
            SQLDialect dialect,
            String url,
            String userName,
            String password
    ) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Connection conn = null;

        String driverName = getDriverName(dialect);
        Class.forName(driverName).newInstance();
        conn = DriverManager.getConnection(url, userName, password);
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Gets source table JDBC connection from a clean plan.
     * @param plan
     * @return
     */
    public static Connection createSourceTableConnection(CleanPlan plan)
            throws
                ClassNotFoundException,
                SQLException,
                InstantiationException,
                IllegalAccessException {
        return createConnection(
                plan.getSqlDialect(),
                plan.getSourceUrl(),
                plan.getSourceTableUserName(),
                plan.getSourceTableUserPassword()
        );
    }

    // </editor-fold>

    private static String getDriverName(SQLDialect dialect) {
        switch (dialect) {
            case POSTGRES:
                return "org.postgresql.Driver";
            default:
                throw new NotImplementedException();
        }
    }
}
