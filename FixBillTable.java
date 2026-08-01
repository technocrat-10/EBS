import java.sql.*;

public class FixBillTable {
    public static void main(String[] args) {
        try {
            System.out.println("Recreating Bill Table");
            System.out.println("====================");
            
            // Connect to the database
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=ebs.mdb";
            Connection conn = DriverManager.getConnection(url, "", "");
            Statement stmt = conn.createStatement();
            
            System.out.println("Connected to the database successfully");
            
            // Check if 'bill' table exists and drop it
            try {
                System.out.println("Dropping bill table if it exists...");
                stmt.executeUpdate("DROP TABLE bill");
                System.out.println("Old bill table dropped");
            } catch (SQLException e) {
                System.out.println("No bill table to drop or error occurred: " + e.getMessage());
            }
            
            // Create new bill table with correct structure
            System.out.println("Creating new bill table with correct structure...");
            try {
                // Use correct Access SQL data types
                stmt.executeUpdate("CREATE TABLE bill (" +
                    "bill_id COUNTER PRIMARY KEY, " +
                    "meter_no VARCHAR(20), " +
                    "month VARCHAR(20), " +
                    "year DOUBLE, " +
                    "units DOUBLE, " +
                    "total_bill DOUBLE, " +
                    "status VARCHAR(20))");
                System.out.println("Bill table created successfully with all required columns");
                
                // Verify table structure
                System.out.println("\nVerifying table structure:");
                ResultSet columns = conn.getMetaData().getColumns(null, null, "bill", null);
                
                System.out.println("Column Name | Data Type");
                System.out.println("----------------------");
                
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    System.out.println(columnName + " | " + dataType);
                }
                columns.close();
                
                // Add sample data for testing
                System.out.println("\nAdding sample data for testing...");
                stmt.executeUpdate("INSERT INTO bill (meter_no, month, year, units, total_bill, status) " +
                                  "VALUES ('1001', 'January', 2023, 100, 1500, 'Paid')");
                System.out.println("Sample data added successfully");
                
            } catch (SQLException e) {
                System.out.println("Error creating bill table: " + e.getMessage());
                e.printStackTrace();
            }
            
            stmt.close();
            conn.close();
            
            System.out.println("\nBill table recreation completed!");
            System.out.println("Now the application should work correctly with proper columns");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 