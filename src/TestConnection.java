import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            
            // Load the JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded successfully");
            
            // Connect to the database
            Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost/ebs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root", "1234");
            System.out.println("Connected to database successfully!");
            
            // Check if tables exist
            DatabaseMetaData metaData = c.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "customer", null);
            
            if (tables.next()) {
                System.out.println("Customer table exists");
            } else {
                System.out.println("Customer table does not exist! Creating tables...");
                createTables(c);
            }
            
            // Close connection
            c.close();
            System.out.println("Connection test completed successfully");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTables(Connection c) {
        try {
            Statement s = c.createStatement();
            
            // Create customer table
            s.execute("CREATE TABLE IF NOT EXISTS customer (" +
                      "meter_no VARCHAR(20) PRIMARY KEY," +
                      "name VARCHAR(100)," +
                      "address VARCHAR(200)," +
                      "city VARCHAR(50)," +
                      "state VARCHAR(50)," +
                      "email VARCHAR(100)," +
                      "phone VARCHAR(20)" +
                      ")");
            
            // Create bill table
            s.execute("CREATE TABLE IF NOT EXISTS bill (" +
                      "bill_id INT AUTO_INCREMENT PRIMARY KEY," +
                      "meter_no VARCHAR(20)," +
                      "month VARCHAR(20)," +
                      "year INT," +
                      "units INT," +
                      "total_bill DECIMAL(10,2)," +
                      "status VARCHAR(20)," +
                      "FOREIGN KEY (meter_no) REFERENCES customer(meter_no)" +
                      ")");
            
            // Create user table
            s.execute("CREATE TABLE IF NOT EXISTS user (" +
                      "username VARCHAR(50) PRIMARY KEY," +
                      "password VARCHAR(50)," +
                      "role VARCHAR(20)" +
                      ")");
            
            // Insert default admin user
            s.execute("INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin')");
            
            System.out.println("Tables created successfully");
        } catch (Exception e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
} 