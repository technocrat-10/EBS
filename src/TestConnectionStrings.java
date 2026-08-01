import java.sql.*;
import java.io.File;

public class TestConnectionStrings {
    public static void main(String[] args) {
        System.out.println("Testing Access Database Connection Strings");
        System.out.println("=========================================");
        System.out.println("Java version: " + System.getProperty("java.version"));
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
        try {
            System.out.println("Loading JDBC-ODBC Bridge driver...");
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            System.out.println("Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: JDBC-ODBC Bridge driver not found");
            System.out.println("This indicates you are not using Java 7 or earlier.");
            System.out.println("Java 8 and later removed support for the JDBC-ODBC Bridge.");
            return;
        }
        
        // Get the absolute path to the database
        String dbPath = dbFile.getAbsolutePath();
        
        // Try different connection string formats
        String[] connectionStrings = {
            // Standard format
            "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath,
            
            // Alternative format with driver ID
            "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath + ";DriverID=22",
            
            // Format with ODBC DSN
            "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath,
            
            // Try with different casing
            "jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};dbq=" + dbPath,
            
            // Try with quotes
            "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=\"" + dbPath + "\"",
            
            // Try with additional parameters
            "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath + ";READONLY=false",
            
            // Try with Access 2007-2013 driver if installed
            "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + dbPath
        };
        
        // Test each connection string
        for (int i = 0; i < connectionStrings.length; i++) {
            System.out.println("\nTrying connection string #" + (i+1) + ":");
            System.out.println(connectionStrings[i]);
            
            try {
                Connection conn = DriverManager.getConnection(connectionStrings[i]);
                System.out.println("SUCCESS! Connected successfully with connection string #" + (i+1));
                
                // List tables to verify connection is working
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet rs = meta.getTables(null, null, null, new String[] {"TABLE"});
                System.out.println("Tables in database:");
                int tableCount = 0;
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    System.out.println("  - " + tableName);
                    tableCount++;
                }
                
                if (tableCount == 0) {
                    System.out.println("  (No tables found)");
                }
                
                System.out.println("\nUSE THIS CONNECTION STRING IN conn.java:");
                System.out.println(connectionStrings[i]);
                
                // Close connection
                rs.close();
                conn.close();
                return;
            } catch (SQLException e) {
                System.out.println("Failed: " + e.getMessage());
            }
        }
        
        System.out.println("\nNone of the connection strings worked.");
        System.out.println("Please ensure the Microsoft Access Database Engine is installed.");
        System.out.println("Make sure it matches your Java's architecture (32-bit or 64-bit).");
    }
} 