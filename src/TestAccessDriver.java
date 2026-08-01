import java.sql.*;
import java.io.File;

public class TestAccessDriver {
    public static void main(String[] args) {
        System.out.println("Testing Access Database Connection");
        System.out.println("==================================");
        
        try {
            // Print Java version
            System.out.println("Java version: " + System.getProperty("java.version"));
            System.out.println("Java home: " + System.getProperty("java.home"));
            System.out.println("Java architecture: " + System.getProperty("os.arch"));
            System.out.println();
            
            // Check if the database file exists
            File dbFile = new File("ebs.mdb");
            if (dbFile.exists()) {
                System.out.println("Database file found: " + dbFile.getAbsolutePath());
            } else {
                System.out.println("Database file not found!");
                System.out.println("Expected at: " + dbFile.getAbsolutePath());
                return;
            }
            
            // Try to load the JDBC-ODBC Bridge driver
            System.out.println("Loading JDBC-ODBC Bridge driver...");
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            System.out.println("Driver loaded successfully");
            
            // Try to connect to the database
            System.out.println("Connecting to database...");
            String dbPath = dbFile.getAbsolutePath();
            String url = "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath;
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connected successfully!");
            
            // List tables
            System.out.println("\nListing tables in database:");
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[] {"TABLE"});
            int tableCount = 0;
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("  - " + tableName);
                tableCount++;
            }
            
            if (tableCount == 0) {
                System.out.println("No tables found in database");
            }
            
            // Close connection
            rs.close();
            conn.close();
            System.out.println("\nTest completed successfully");
            
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: JDBC-ODBC Bridge driver not found");
            System.out.println("This indicates you are not using Java 7 or earlier.");
            System.out.println("Java 8 and later removed support for the JDBC-ODBC Bridge.");
            System.out.println("\nPlease install Java 7 to use this application.");
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to database");
            System.out.println("Message: " + e.getMessage());
            System.out.println("\nPossible solutions:");
            System.out.println("1. Install Microsoft Access Database Engine 2010");
            System.out.println("   (make sure to match 32-bit Java with 32-bit drivers)");
            System.out.println("2. Verify ebs.mdb exists and has read/write permissions");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 