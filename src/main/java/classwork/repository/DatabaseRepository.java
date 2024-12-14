package classwork.repository;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseRepository {
    private static String URL;
    private static String USER ;
    private static String PASSWORD ;

    static {
        try (FileInputStream input = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
            URL = properties.getProperty("db.url");
        } catch (Exception e) {
            System.err.println("Failed to load credentials: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
