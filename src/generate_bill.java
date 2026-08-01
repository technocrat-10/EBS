import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.Calendar;

public class generate_bill extends JFrame implements ActionListener{
    JLabel l1, l2, l3;
    JTextArea t1;
    JButton b1, b2;
    Choice c1, c2;
    JPanel p1;
    
    generate_bill(){
        setSize(600, 900);
        setLayout(new BorderLayout());

        p1 = new JPanel();
        p1.setLayout(new FlowLayout());

        l1 = new JLabel("Generate Bill");
        l1.setFont(new Font("Tahoma", Font.BOLD, 24));
        
        l2 = new JLabel("Meter No");
        l3 = new JLabel("Month");

        c1 = new Choice();
        c2 = new Choice();

        // Load meter numbers from database
        try {
            conn connection = new conn();
            ResultSet rs = connection.s.executeQuery("SELECT meter_no FROM customer");
            while(rs.next()) {
                c1.add(rs.getString("meter_no"));
            }
            
            // If no customers, add a default value for testing
            if (c1.getItemCount() == 0) {
                c1.add("No Meter Numbers");
            }
        } catch(Exception e) {
            e.printStackTrace();
            c1.add("Error loading");
        }

        c2.add("January");
        c2.add("February");
        c2.add("March");
        c2.add("April");
        c2.add("May");
        c2.add("June");
        c2.add("July");
        c2.add("August");
        c2.add("September");
        c2.add("October");
        c2.add("November");
        c2.add("December");

        t1 = new JTextArea(50, 20);
        JScrollPane jsp = new JScrollPane(t1);
        t1.setFont(new Font("Senserif", Font.PLAIN, 18));
        t1.setEditable(false);

        b1 = new JButton("Generate Bill");
        b2 = new JButton("Cancel");
        
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);

        p1.add(l1);
        p1.add(l2);
        p1.add(c1);
        p1.add(l3);
        p1.add(c2);
        add(p1, "North");

        add(jsp, "Center");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(b1);
        buttonPanel.add(b2);
        add(buttonPanel, "South");

        b1.addActionListener(this);
        b2.addActionListener(this);

        setLocation(350, 40);
    }
    
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == b1) {
            try {
                conn c = new conn();
                String meter = c1.getSelectedItem();
                String month = c2.getSelectedItem();
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                
                // Clear previous content
                t1.setText("");
                
                // Using string concatenation with + operator, compatible with all Java versions
                String billHeader = "\tReliance Power Limited\nELECTRICITY BILL FOR THE MONTH OF " + 
                                    month + " " + currentYear + "\n\n\n";
                t1.setText(billHeader);

                // Get customer details from customer table
                ResultSet rs = c.s.executeQuery("SELECT * FROM customer WHERE meter_no='" + meter + "'");

                if(rs.next()){
                    t1.append("\n    Customer Name:     " + rs.getString("name"));
                    t1.append("\n    Meter Number:      " + rs.getString("meter_no"));
                    t1.append("\n    Address:           " + rs.getString("address"));
                    t1.append("\n    State:             " + rs.getString("state"));
                    t1.append("\n    City:              " + rs.getString("city"));
                    t1.append("\n    Email:             " + rs.getString("email"));
                    t1.append("\n    Phone Number:      " + rs.getString("phone"));
                    t1.append("\n-------------------------------------------------------------");
                    t1.append("\n");
                } else {
                    t1.append("\n    Customer not found for meter number: " + meter);
                    return;
                }

                // Add fixed charges
                t1.append("\n    Meter Rent:        Rs.50");
                t1.append("\n    MCB Rent:          Rs.12");
                t1.append("\n    Service Tax:       Rs.102");
                t1.append("\n    GST@9%:            Rs.20");
                t1.append("\n");
                t1.append("---------------------------------------------------------------");
                t1.append("\n\n");

                // Get bill details from bill table
                rs = c.s.executeQuery("SELECT * FROM bill WHERE meter_no='" + meter + "' AND month='" + month + "'");

                if(rs.next()){
                    t1.append("\n    Current Month:     " + rs.getString("month"));
                    t1.append("\n    Year:              " + rs.getString("year"));
                    t1.append("\n    Units Consumed:    " + rs.getString("units"));
                    t1.append("\n    Total Charges:     Rs." + rs.getString("total_bill"));
                    t1.append("\n    Payment Status:    " + rs.getString("status"));
                    t1.append("\n---------------------------------------------------------------");
                    t1.append("\n    TOTAL PAYABLE:     Rs." + rs.getString("total_bill"));
                } else {
                    t1.append("\n    No bill found for this meter number and month");
                    t1.append("\n    Please calculate bill first from the Calculate Bill menu");
                }

            } catch(Exception e){
                t1.setText("Error generating bill: " + e.getMessage());
                e.printStackTrace();
            }
        } else if(ae.getSource() == b2) {
            this.setVisible(false);
        }
    }

    public static void main(String[] args){
        new generate_bill().setVisible(true);
    }
}/*  */