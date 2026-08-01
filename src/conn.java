import java.sql.*;

public class conn
{
    Connection c;
    Statement s;
    
    public conn()
    {
        try
        {
            // Check if the database file exists, if not create it
            java.io.File dbFile = new java.io.File("ebs.mdb");
            String dbPath = dbFile.getAbsolutePath();
            System.out.println("Database path: " + dbPath);
            
            if (!dbFile.exists()) {
                System.out.println("Database file not found, creating new database file");
                createNewAccessDB(dbPath);
            }
            
            // Try multiple connection string formats
            boolean connected = false;
            
            // Try different connection strings
            String[] connectionStrings = {
                // Standard with quotes around path
                "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=\"" + dbPath + "\"",
                // Standard without quotes
                "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath,
                // Try older DSN style
                "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath,
                // Try with DriverID parameter
                "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath + ";DriverID=22",
                // Try newer Access driver
                "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + dbPath
            };
            
            for (String url : connectionStrings) {
                try {
                    // Load the JDBC-ODBC Bridge driver
                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                    System.out.println("Trying connection string: " + url);
                    c = DriverManager.getConnection(url);
                    
                    // If we get here, connection was successful
                    s = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    System.out.println("Connected to database successfully using: " + url);
                    connected = true;
                    break;
                }
                catch (Exception e) {
                    System.out.println("Connection failed with: " + e.getMessage());
                    // Continue to next connection string
                }
            }
            
            // If we couldn't connect with any of the strings
            if (!connected) {
                System.out.println("All connection attempts failed. Please check your Access installation.");
                throw new Exception("Failed to connect to database after trying all connection strings.");
            }
            
            // Create tables if they don't exist
            createTablesIfNeeded();
        }
        catch(Exception e)
        {
            System.out.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            
            // Show more detailed error message about Access connectivity
            System.out.println("\nPlease ensure the following:");
            System.out.println("1. Microsoft Access or Microsoft Access Database Engine is installed on your system");
            System.out.println("2. You're using Java 7 (JDBC-ODBC Bridge was removed in Java 8+)");
            System.out.println("3. The Access database file 'ebs.mdb' is in the same directory as the application");
            System.out.println("4. You have 32-bit Java installed if you have 32-bit Access drivers or 64-bit Java for 64-bit drivers");
        }
    }
    
    private void createNewAccessDB(String dbPath) {
        System.out.println("Attempting to create a new Access database file");
        try {
            // We can't directly create MDB files in Java, so we'll tell the user to do it
            System.out.println("Java cannot directly create Access database files.");
            System.out.println("Please create an empty Access database named 'ebs.mdb' in this folder.");
            System.out.println("Then run the application again.");
            
            // Exit the application as we can't proceed without a database
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error creating database file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTablesIfNeeded() {
        try {
            if (c == null || s == null) {
                System.out.println("Cannot create tables - database connection not established");
                return;
            }
            
            DatabaseMetaData meta = c.getMetaData();
            
            // Check and create customer table
            if (!tableExists("customer")) {
                s.execute("CREATE TABLE customer (" +
                        "meter_no TEXT(20) PRIMARY KEY, " +
                        "name TEXT(100), " +
                        "address TEXT(200), " +
                        "city TEXT(50), " +
                        "state TEXT(50), " +
                        "email TEXT(100), " +
                        "phone TEXT(20))");
                System.out.println("Customer table created");
            }
            
            // Check and create bill table
            if (!tableExists("bill")) {
                s.execute("CREATE TABLE bill (" +
                        "bill_id COUNTER PRIMARY KEY, " +
                        "meter_no TEXT(20), " +
                        "month TEXT(20), " +
                        "year INT, " +
                        "units INT, " +
                        "total_bill DECIMAL, " +
                        "status TEXT(20))");
                System.out.println("Bill table created");
            }
            
            // Check and create user table
            if (!tableExists("user")) {
                s.execute("CREATE TABLE user (" +
                        "username TEXT(50) PRIMARY KEY, " +
                        "password TEXT(50), " +
                        "role TEXT(20))");
                
                // Insert default admin user
                s.execute("INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin')");
                System.out.println("User table created with default admin user");
            }
        } catch (Exception e) {
            System.out.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean tableExists(String tableName) {
        try {
            if (c == null) {
                System.out.println("Cannot check if table exists - database connection not established");
                return false;
            }
            
            DatabaseMetaData meta = c.getMetaData();
            ResultSet rs = meta.getTables(null, null, tableName, new String[] {"TABLE"});
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (Exception e) {
            System.out.println("Error checking if table exists: " + e.getMessage());
            return false;
        }
    }
    
    // Utility method to check if connection is valid
    public boolean isConnected() {
        return (c != null);
    }
}
