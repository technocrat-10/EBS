import java.sql.*;

public class setupDB {
    public static void main(String[] args) {
        try {
            System.out.println("Setting up database tables...");
            
            // Create database connection
            conn c = new conn();
            System.out.println("Connected to database");
            
            try {
                // Create user table
                System.out.println("Creating user table...");
                c.s.execute("CREATE TABLE user (" +
                            "username TEXT(50) PRIMARY KEY, " +
                            "password TEXT(50), " +
                            "role TEXT(20))");
                System.out.println("User table created");
            } catch (Exception e) {
                System.out.println("User table already exists or couldn't be created: " + e.getMessage());
            }
            
            try {
                // Add admin user
                System.out.println("Adding admin user...");
                c.s.execute("INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin')");
                System.out.println("Admin user added");
            } catch (Exception e) {
                System.out.println("Admin user already exists or couldn't be added: " + e.getMessage());
                
                // Try updating the admin password if it exists
                try {
                    c.s.execute("UPDATE user SET password = 'admin' WHERE username = 'admin'");
                    System.out.println("Admin password updated");
                } catch (Exception e2) {
                    System.out.println("Couldn't update admin password: " + e2.getMessage());
                }
            }
            
            try {
                // Create customer table
                System.out.println("Checking/creating customer table...");
                c.s.execute("CREATE TABLE customer (" +
                            "meter_no TEXT(20) PRIMARY KEY, " +
                            "name TEXT(100), " +
                            "address TEXT(200), " +
                            "city TEXT(50), " +
                            "state TEXT(50), " +
                            "email TEXT(100), " +
                            "phone TEXT(20))");
                System.out.println("Customer table created");
            } catch (Exception e) {
                System.out.println("Customer table already exists or couldn't be created: " + e.getMessage());
            }
            
            try {
                // Create bill table
                System.out.println("Checking/creating bill table...");
                c.s.execute("CREATE TABLE bill (" +
                            "bill_id COUNTER PRIMARY KEY, " +
                            "meter_no TEXT(20), " +
                            "month TEXT(20), " +
                            "year INT, " +
                            "units INT, " +
                            "total_bill DECIMAL, " +
                            "status TEXT(20))");
                System.out.println("Bill table created");
            } catch (Exception e) {
                System.out.println("Bill table already exists or couldn't be created: " + e.getMessage());
            }
            
            System.out.println("Database setup complete!");
            
            // Check if admin user exists
            ResultSet rs = c.s.executeQuery("SELECT * FROM user WHERE username = 'admin'");
            if (rs.next()) {
                System.out.println("Admin user exists with password: " + rs.getString("password"));
            } else {
                System.out.println("Warning: Admin user does not exist in the database!");
            }
            
        } catch (Exception e) {
            System.out.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 