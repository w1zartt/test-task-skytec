package config.data;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static config.data.DbConstants.*;

@Slf4j
public class H2DataBaseInitializer {

    public static void init() {
        log.info("Starting to init database");
        Connection conn = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        try {
            Class.forName(JDBC_DRIVER);
            log.info("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            log.info("Creating table in database...");
            stmt1 = conn.createStatement();
            stmt1.executeUpdate(INIT_CLAN_TABLE_QUERY);
            stmt1.close();
            stmt2 = conn.createStatement();
            stmt2.executeUpdate(INIT_USER_TABLE_QUERY);
            stmt2.close();
            log.info("Created table in given database...");
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt1 != null) stmt1.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        log.info("Initializing is finished");
    }
}
