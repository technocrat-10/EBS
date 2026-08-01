import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class LastBill extends JFrame implements ActionListener
{
    JLabel l1;
    JTextArea t1;
    JButton b1;
    Choice c1;
    JPanel p1;
    
    LastBill(){
        setSize(500,900);
        setLayout(new BorderLayout());

        p1 = new JPanel();

        l1 = new JLabel("Generate Bill");

        c1 = new Choice();
        
        // Load meter numbers from database
        try {
            conn connection = new conn();
            ResultSet rs = connection.s.executeQuery("SELECT meter_no FROM customer");
            while(rs.next()) {
                c1.add(rs.getString("meter_no"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        t1 = new JTextArea(50,15);
        JScrollPane jsp = new JScrollPane(t1);
        t1.setFont(new Font("Senserif",Font.ITALIC,18));

        b1 = new JButton("Generate Bill");

        p1.add(l1);
        p1.add(c1);
        add(p1,"North");

        add(jsp,"Center");
        add(b1,"South");

        b1.addActionListener(this);

        setLocation(350,40);
    }
    
    public void actionPerformed(ActionEvent ae){
        try{
            // Clear previous content
            t1.setText("");
            
            conn connection = new conn();
            String meterNo = c1.getSelectedItem();

            // Get customer details
            ResultSet rs = connection.s.executeQuery("SELECT * FROM customer WHERE meter_no='" + meterNo + "'");

            if(rs.next()){
                t1.append("\n    Customer Name:"+rs.getString("name"));
                t1.append("\n    Meter Number:  "+rs.getString("meter_no"));
                t1.append("\n    Address:            "+rs.getString("address"));
                t1.append("\n    State:                 "+rs.getString("state"));
                t1.append("\n    City:                   "+rs.getString("city"));
                t1.append("\n    Email:                "+rs.getString("email"));
                t1.append("\n    Phone Number:  "+rs.getString("phone"));
                t1.append("\n-------------------------------------------------------------");
                t1.append("\n");
            }

            t1.append("Details of the Last Bills\n\n\n");

            // Get bill details - using meter_no, not meter_number
            rs = connection.s.executeQuery("SELECT * FROM bill WHERE meter_no='" + meterNo + "' ORDER BY bill_id DESC");

            t1.append("       Month        Year        Units        Amount        Status\n");
            t1.append("----------------------------------------------------------------\n");
            
            while(rs.next()){
                t1.append("       " + rs.getString("month") + "        " +
                          rs.getString("year") + "         " +
                          rs.getString("units") + "           " +
                          rs.getString("total_bill") + "          " +
                          rs.getString("status") + "\n");
            }

        }catch(Exception e){
            t1.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new LastBill().setVisible(true);
    }
}