import java.sql.*;

public class FixUserTable {
    public static void main(String[] args) {
        try {
            System.out.println("Checking and Fixing User Table");
            System.out.println("=============================");
            
            // Connect to the database
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=ebs.mdb";
            Connection conn = DriverManager.getConnection(url, "", "");
            Statement stmt = conn.createStatement();
            
            System.out.println("Connected to the database successfully");
            
            // Check if 'user' table exists
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "user", null);
            
            if (tables.next()) {
                System.out.println("User table exists. Checking for admin user...");
                
                // Check if admin user exists
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user WHERE username='admin'");
                rs.next();
                int adminCount = rs.getInt(1);
                rs.close();
                
                if (adminCount == 0) {
                    System.out.println("Admin user doesn't exist. Adding admin user...");
                    stmt.executeUpdate("INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin')");
                    System.out.println("Admin user added successfully");
                } else {
                    System.out.println("Admin user already exists");
                }
                
                // Check table structure
                System.out.println("\nUser table columns:");
                ResultSet columns = metaData.getColumns(null, null, "user", null);
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    System.out.println(columnName + " | " + dataType);
                }
                columns.close();
            } else {
                System.out.println("User table doesn't exist. Creating it...");
                
                try {
                    // Create user table
                    stmt.executeUpdate("CREATE TABLE user (" +
                        "username VARCHAR(50) PRIMARY KEY, " +
                        "password VARCHAR(50), " +
                        "role VARCHAR(20))");
                    System.out.println("User table created successfully");
                    
                    // Add admin user
                    stmt.executeUpdate("INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin')");
                    System.out.println("Admin user added");
                    
                    System.out.println("\nUser table created with columns:");
                    ResultSet columns = metaData.getColumns(null, null, "user", null);
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String dataType = columns.getString("TYPE_NAME");
                        System.out.println(columnName + " | " + dataType);
                    }
                    columns.close();
                } catch (SQLException e) {
                    System.out.println("Error creating user table: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            tables.close();
            stmt.close();
            conn.close();
            
            System.out.println("\nUser table check/fix completed!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 