import java.sql.*;

public class VerifyDatabaseSetup {
    public static void main(String[] args) {
        try {
            System.out.println("Electricity Billing System - Database Verification");
            System.out.println("===============================================");
            
            // Connect to the database
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=ebs.mdb";
            Connection conn = DriverManager.getConnection(url, "", "");
            Statement stmt = conn.createStatement();
            
            System.out.println("Connected to the database successfully");
            DatabaseMetaData metaData = conn.getMetaData();
            
            boolean allGood = true;
            
            // Check customer table
            System.out.println("\nChecking customer table...");
            allGood = checkTable(metaData, stmt, "customer", 
                    new String[]{"meter_no", "name", "address", "city", "state", "email", "phone"},
                    new String[]{"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR"}) && allGood;
            
            // Check bill table
            System.out.println("\nChecking bill table...");
            allGood = checkTable(metaData, stmt, "bill", 
                    new String[]{"bill_id", "meter_no", "month", "year", "units", "total_bill", "status"},
                    new String[]{"COUNTER", "VARCHAR", "VARCHAR", "DOUBLE", "DOUBLE", "DOUBLE", "VARCHAR"}) && allGood;
            
            // Check user table
            System.out.println("\nChecking user table...");
            allGood = checkTable(metaData, stmt, "user", 
                    new String[]{"username", "password", "role"}, 
                    new String[]{"VARCHAR", "VARCHAR", "VARCHAR"}) && allGood;
            
            // Check for admin user
            System.out.println("\nChecking admin user...");
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user WHERE username='admin'");
            rs.next();
            int adminCount = rs.getInt(1);
            rs.close();
            
            if (adminCount > 0) {
                System.out.println("✓ Admin user exists");
            } else {
                System.out.println("✗ Admin user not found!");
                allGood = false;
            }
            
            // Check if we have any test data
            System.out.println("\nChecking for test data...");
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM customer");
            rs.next();
            int customerCount = rs.getInt(1);
            rs.close();
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bill");
            rs.next();
            int billCount = rs.getInt(1);
            rs.close();
            
            System.out.println("Customers: " + customerCount);
            System.out.println("Bills: " + billCount);
            
            if (customerCount == 0) {
                System.out.println("! No customer records found. Consider adding test data.");
            }
            
            if (billCount == 0) {
                System.out.println("! No bill records found. Consider adding test data.");
            }
            
            // Summary
            System.out.println("\n=== Database Verification Summary ===");
            if (allGood) {
                System.out.println("✓ All database tables are correctly set up!");
                System.out.println("The application should now work properly.");
            } else {
                System.out.println("✗ There are issues with the database setup.");
                System.out.println("Please fix the reported issues and run this verification again.");
            }
            
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean checkTable(DatabaseMetaData metaData, Statement stmt, 
                                     String tableName, String[] expectedColumns, 
                                     String[] expectedTypes) throws SQLException {
        
        // Check if table exists
        ResultSet tables = metaData.getTables(null, null, tableName, null);
        boolean tableExists = tables.next();
        tables.close();
        
        if (!tableExists) {
            System.out.println("✗ Table '" + tableName + "' does not exist!");
            return false;
        }
        
        System.out.println("✓ Table '" + tableName + "' exists");
        
        // Check column structure
        boolean columnsOk = true;
        
        // Get all columns
        ResultSet columns = metaData.getColumns(null, null, tableName, null);
        int columnCount = 0;
        System.out.println("  Columns:");
        
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String dataType = columns.getString("TYPE_NAME");
            
            System.out.println("    - " + columnName + " (" + dataType + ")");
            columnCount++;
            
            // Check if column is expected
            boolean columnFound = false;
            for (int i = 0; i < expectedColumns.length; i++) {
                if (columnName.equalsIgnoreCase(expectedColumns[i])) {
                    columnFound = true;
                    
                    // Check data type
                    if (!dataType.equalsIgnoreCase(expectedTypes[i])) {
                        System.out.println("      ! Wrong data type: expected " + 
                                          expectedTypes[i] + ", found " + dataType);
                        columnsOk = false;
                    }
                    
                    break;
                }
            }
            
            if (!columnFound) {
                System.out.println("      ! Unexpected column");
            }
        }
        columns.close();
        
        // Check for missing columns
        for (String expectedColumn : expectedColumns) {
            ResultSet columnCheck = metaData.getColumns(null, null, tableName, expectedColumn);
            boolean columnExists = columnCheck.next();
            columnCheck.close();
            
            if (!columnExists) {
                System.out.println("    ✗ Missing column: " + expectedColumn);
                columnsOk = false;
            }
        }
        
        if (columnCount != expectedColumns.length) {
            System.out.println("  ! Column count mismatch: expected " + 
                              expectedColumns.length + ", found " + columnCount);
            columnsOk = false;
        }
        
        if (columnsOk) {
            System.out.println("  ✓ All columns are present with correct types");
        } else {
            System.out.println("  ✗ There are issues with columns in table '" + tableName + "'");
        }
        
        return columnsOk;
    }
} 