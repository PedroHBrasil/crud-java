package crud.java;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnector {

    private Properties conProps;
    private Connection con;

    DbConnector() {
        this.setDbConnection();
    }

    public Properties getConProps() {
        return this.conProps;
    }

    public Connection getCon() {
        return this.con;
    }

    protected void setDbConnection() {
        System.out.println("\nSetting up connection to database.");

        this.loadDbConnectionProps();

        String urlTemplate = "jdbc:%s://%s:%s/%s";

        String dbVendor = conProps.getProperty("db.vendor");
        String dbIp = conProps.getProperty("db.ip");
        String dbPort = conProps.getProperty("db.port");
        String dbName = conProps.getProperty("db.name");
        String dbUser = conProps.getProperty("db.username");
        String dbPassword = conProps.getProperty("db.password");
        String dbUrl = String.format(urlTemplate, dbVendor, dbIp, dbPort, dbName);

        System.out.println("Here are the parameters read from the database.properties file:");
        System.out.println("\t- Vendor: %s".formatted(dbVendor));
        System.out.println("\t- IP: %s".formatted(dbIp));
        System.out.println("\t- Port: %s".formatted(dbPort));
        System.out.println("\t- Name: %s".formatted(dbName));
        System.out.println("\t- User: %s".formatted(dbUser));

        try {
            this.con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("\nConnected to database.");

        } catch (SQLException e) {
            System.out.println("\nFailed to connect to database: " + e.getMessage());
        }
    }

    protected Properties loadDbConnectionProps() {
        this.conProps = new Properties();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database.properties");
            this.conProps.load(inputStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return this.conProps;
    }

    protected void closeDbConnection() {
        if (this.con != null) {
            try {
                this.con.close();
                System.out.println("\nClosed database connection.");
            } catch (SQLException e) {
                System.out.println("\nError while closing the connection: " + e.getMessage());
            }
        }
    }

}
