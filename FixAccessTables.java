import java.sql.*;

public class FixAccessTables {
    public static void main(String[] args) {
        try {
            System.out.println("Fixing Access Database Tables");
            System.out.println("============================");
            
            // Connect to the database
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=ebs.mdb";
            Connection conn = DriverManager.getConnection(url, "", "");
            Statement stmt = conn.createStatement();
            
            System.out.println("Connected to the database successfully");
            
            // Check if tables exist
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Fix customer table
            fixTable(conn, metaData, "customer", 
                "CREATE TABLE customer (" +
                "meter_no TEXT(20) PRIMARY KEY, " +
                "name TEXT(100), " +
                "address TEXT(200), " +
                "city TEXT(50), " +
                "state TEXT(50), " +
                "email TEXT(100), " +
                "phone TEXT(20))");
            
            // Check if bill table has 'role' column instead of 'status'
            boolean needToFixBillTable = false;
            ResultSet billColumns = metaData.getColumns(null, null, "bill", null);
            while (billColumns.next()) {
                if (billColumns.getString("COLUMN_NAME").equalsIgnoreCase("role")) {
                    needToFixBillTable = true;
                    break;
                }
            }
            billColumns.close();
            
            if (needToFixBillTable) {
                System.out.println("Bill table has 'role' column instead of 'status'. Fixing...");
                try {
                    // Create a temporary bill table with correct structure
                    stmt.executeUpdate("CREATE TABLE bill_temp (" +
                        "bill_id COUNTER PRIMARY KEY, " +
                        "meter_no TEXT(20), " +
                        "month TEXT(20), " +
                        "year INT, " +
                        "units INT, " +
                        "total_bill DECIMAL, " +
                        "status TEXT(20))");
                    
                    // Copy data, replacing role with status
                    stmt.executeUpdate("INSERT INTO bill_temp (meter_no, month, year, units, total_bill, status) " +
                        "SELECT meter_no, month, year, units, total_bill, role FROM bill");
                    
                    // Drop old bill table
                    stmt.executeUpdate("DROP TABLE bill");
                    
                    // Create new bill table
                    stmt.executeUpdate("CREATE TABLE bill (" +
                        "bill_id COUNTER PRIMARY KEY, " +
                        "meter_no TEXT(20), " +
                        "month TEXT(20), " +
                        "year INT, " +
                        "units INT, " +
                        "total_bill DECIMAL, " +
                        "status TEXT(20))");
                    
                    // Copy data from temp
                    stmt.executeUpdate("INSERT INTO bill (meter_no, month, year, units, total_bill, status) " +
                        "SELECT meter_no, month, year, units, total_bill, status FROM bill_temp");
                    
                    // Drop temp table
                    stmt.executeUpdate("DROP TABLE bill_temp");
                    
                    System.out.println("Bill table fixed successfully - 'role' renamed to 'status'");
                } catch (SQLException e) {
                    System.out.println("Error fixing bill table: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Fix bill table normally
                fixTable(conn, metaData, "bill", 
                    "CREATE TABLE bill (" +
                    "bill_id COUNTER PRIMARY KEY, " +
                    "meter_no TEXT(20), " +
                    "month TEXT(20), " +
                    "year INT, " +
                    "units INT, " +
                    "total_bill DECIMAL, " +
                    "status TEXT(20))");
            }
            
            // Fix user table
            fixTable(conn, metaData, "user", 
                "CREATE TABLE user (" +
                "username TEXT(50) PRIMARY KEY, " +
                "password TEXT(50), " +
                "role TEXT(20))");
            
            // Insert admin user if it doesn't exist
            ResultSet userCheck = stmt.executeQuery("SELECT COUNT(*) FROM user WHERE username='admin'");
            userCheck.next();
            int adminCount = userCheck.getInt(1);
            
            if (adminCount == 0) {
                System.out.println("Adding admin user...");
                stmt.executeUpdate("INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin')");
                System.out.println("Admin user added successfully");
            } else {
                System.out.println("Admin user already exists");
            }
            
            // Close connections
            userCheck.close();
            stmt.close();
            conn.close();
            
            System.out.println("\nAll tables have been fixed successfully!");
            System.out.println("You can now run the application using clean_and_run.bat");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void fixTable(Connection conn, DatabaseMetaData metaData, 
                                 String tableName, String createTableSQL) 
                                 throws SQLException {
        ResultSet tables = metaData.getTables(null, null, tableName, null);
        
        boolean tableExists = false;
        while (tables.next()) {
            if (tables.getString("TABLE_NAME").equalsIgnoreCase(tableName)) {
                tableExists = true;
                break;
            }
        }
        
        if (tableExists) {
            System.out.println("Table '" + tableName + "' exists.");
            // Check for case mismatch
            if (!tables.getString("TABLE_NAME").equals(tableName)) {
                String actualTableName = tables.getString("TABLE_NAME");
                System.out.println("Fixing case mismatch: '" + actualTableName + 
                                   "' -> '" + tableName + "'");
                Statement stmt = conn.createStatement();
                
                try {
                    // Rename by creating a new table and copying data
                    System.out.println("Creating temporary table...");
                    stmt.executeUpdate(createTableSQL.replace(tableName, "temp_" + tableName));
                    
                    // Copy data from old table to temp table
                    System.out.println("Copying data...");
                    ResultSet columns = metaData.getColumns(null, null, actualTableName, null);
                    StringBuilder columnList = new StringBuilder();
                    while (columns.next()) {
                        if (columnList.length() > 0) columnList.append(", ");
                        columnList.append(columns.getString("COLUMN_NAME"));
                    }
                    columns.close();
                    
                    if (columnList.length() > 0) {
                        stmt.executeUpdate("INSERT INTO temp_" + tableName + 
                                          " SELECT " + columnList.toString() + 
                                          " FROM " + actualTableName);
                    }
                    
                    // Drop old table
                    System.out.println("Dropping old table...");
                    stmt.executeUpdate("DROP TABLE " + actualTableName);
                    
                    // Create new table with correct name
                    System.out.println("Creating new table with correct name...");
                    stmt.executeUpdate(createTableSQL);
                    
                    // Copy data from temp to new
                    System.out.println("Restoring data...");
                    columns = metaData.getColumns(null, null, "temp_" + tableName, null);
                    columnList = new StringBuilder();
                    while (columns.next()) {
                        if (columnList.length() > 0) columnList.append(", ");
                        columnList.append(columns.getString("COLUMN_NAME"));
                    }
                    columns.close();
                    
                    if (columnList.length() > 0) {
                        stmt.executeUpdate("INSERT INTO " + tableName + 
                                          " SELECT " + columnList.toString() + 
                                          " FROM temp_" + tableName);
                    }
                    
                    // Drop temp table
                    System.out.println("Cleaning up...");
                    stmt.executeUpdate("DROP TABLE temp_" + tableName);
                    
                    System.out.println("Table '" + tableName + "' fixed successfully");
                } catch (SQLException e) {
                    System.out.println("Error fixing table: " + e.getMessage());
                    e.printStackTrace();
                }
                stmt.close();
            }
        } else {
            // Table doesn't exist, create it
            System.out.println("Table '" + tableName + "' doesn't exist. Creating...");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createTableSQL);
            stmt.close();
            System.out.println("Table '" + tableName + "' created successfully");
        }
        tables.close();
    }
} 