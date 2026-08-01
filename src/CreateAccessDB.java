import java.io.*;
import java.sql.*;

public class CreateAccessDB {
    
    public static void main(String[] args) {
        try {
            // Create Access database file
            String dbFilePath = "ebs.mdb";
            System.out.println("Creating Access database file: " + dbFilePath);
            
            // Load the JDBC-ODBC Bridge driver
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            
            // Create a DSN-less connection to the Access database
            String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + dbFilePath + ";DriverID=22;READONLY=false}";
            Connection conn = DriverManager.getConnection(url, "", "");
            Statement stmt = conn.createStatement();
            
            System.out.println("Connected to the database");
            
            // Create tables
            System.out.println("Creating tables...");
            
            // Create customer table
            stmt.execute("CREATE TABLE customer (" +
                        "meter_no VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "address VARCHAR(200), " +
                        "city VARCHAR(50), " +
                        "state VARCHAR(50), " +
                        "email VARCHAR(100), " +
                        "phone VARCHAR(20))");
            
            // Create bill table
            stmt.execute("CREATE TABLE bill (" +
                        "bill_id COUNTER PRIMARY KEY, " +
                        "meter_no VARCHAR(20), " +
                        "month VARCHAR(20), " +
                        "year INT, " +
                        "units INT, " +
                        "total_bill DECIMAL(10,2), " +
                        "status VARCHAR(20))");
            
            // Create user table
            stmt.execute("CREATE TABLE user (" +
                        "username VARCHAR(50) PRIMARY KEY, " +
                        "password VARCHAR(50), " +
                        "role VARCHAR(20))");
            
            // Insert default admin user
            stmt.execute("INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin')");
            
            System.out.println("Tables created successfully");
            
            // Close connections
            stmt.close();
            conn.close();
            
            System.out.println("Database setup completed successfully");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 