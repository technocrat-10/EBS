import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class customer_details extends JFrame implements ActionListener{

    JTable t1;
    JButton b1;
    String x[] = {"Name","Meter No","Address","City","State","Email","Phone"};
    String y[][] = new String[20][7];
    int i=0, j=0;
    
    customer_details(){
        super("Customer Details");
        setSize(1200,650);
        setLocation(200,200);

        try{
            conn c1 = new conn();
            
            // Check if connection was established
            if (!c1.isConnected()) {
                JOptionPane.showMessageDialog(this, 
                    "Database connection failed. Please check your Microsoft Access installation.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
                
                // Create empty table to avoid NullPointerException
                t1 = new JTable(new String[0][7], x);
            } else {
                String s1 = "select * from customer";
                ResultSet rs = c1.s.executeQuery(s1);
                while(rs.next()){
                    y[i][j++]=rs.getString("name");
                    y[i][j++]=rs.getString("meter_no");
                    y[i][j++]=rs.getString("address");
                    y[i][j++]=rs.getString("city");
                    y[i][j++]=rs.getString("state");
                    y[i][j++]=rs.getString("email");
                    y[i][j++]=rs.getString("phone");
                    i++;
                    j=0;
                }
                t1 = new JTable(y,x);
            }
        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading customer data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            
            // Create empty table to avoid NullPointerException
            t1 = new JTable(new String[0][7], x);
        }

        b1 = new JButton("Print");
        add(b1,"South");
        JScrollPane sp = new JScrollPane(t1);
        add(sp);
        b1.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent ae){
        try{
            t1.print();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        new customer_details().setVisible(true);
    }
}