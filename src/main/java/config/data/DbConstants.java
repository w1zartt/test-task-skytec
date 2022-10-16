package config.data;

public class DbConstants {
    public static final String JDBC_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:~/test";
    public static final String USER = "sa";
    public static final String PASS = "";
    public static final String INIT_CLAN_TABLE_QUERY =
            "DROP TABLE IF EXISTS clan; CREATE TABLE clan " +
            "(id bigint AUTO_INCREMENT primary key, " +
            " name text, " +
            " gold int); " +
            "insert into clan(name, gold) values ('clan1', 100)";
    public static String INIT_USER_TABLE_QUERY =
            "DROP TABLE IF EXISTS player; CREATE TABLE player " +
                    "(id IDENTITY primary key, " +
                    " name text NOT NULL, " +
                    "gold int, " +
                    "rank VARCHAR(50)); " +
                    "insert into player (name, gold, rank) values ('Dima', 400, 'LEAD')";

}
