import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class calculate_bill extends JFrame implements ActionListener
{
    JLabel l1,l2,l3,l4,l5;
    JTextField t1;
    Choice c1,c2;
    JButton b1,b2;
    JPanel p;
    calculate_bill(){

        p = new JPanel();
        p.setLayout(new GridLayout(4,2,30,30));
        p.setBackground(Color.WHITE);

        l1 = new JLabel("Calculate Electricity Bill");
        l2 = new JLabel("Meter No");
        l3 = new JLabel("Units Cosumed");
        l5 = new JLabel("Month");

        t1 = new JTextField();

        c1 = new Choice();
        
        try {
            conn connection = new conn();
            ResultSet rs = connection.s.executeQuery("SELECT * FROM customer");
            while(rs.next()) {
                c1.add(rs.getString("meter_no"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        c2 = new Choice();
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

        b1 = new JButton("Submit");
        b2 = new JButton("Cancel");

        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);

        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("images/hicon2.jpg"));
        Image i2 = i1.getImage().getScaledInstance(180, 270,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        l4 = new JLabel(i3);



        l1.setFont(new Font("Senserif",Font.PLAIN,26));
        //Move the label to center
        l1.setHorizontalAlignment(JLabel.CENTER);



        p.add(l2);
        p.add(c1);
        p.add(l5);
        p.add(c2);
        p.add(l3);
        p.add(t1);
        p.add(b1);
        p.add(b2);

        setLayout(new BorderLayout(30,30));

        add(l1,"North");
        add(p,"Center");
        add(l4,"West");


        b1.addActionListener(this);
        b2.addActionListener(this);

        getContentPane().setBackground(Color.WHITE);
        setSize(650,500);
        setLocation(350,220);
    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == b1) {
            String meter_no = c1.getSelectedItem();
            String month = c2.getSelectedItem();
            String units = t1.getText();
            
            if(units.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter the number of units");
                return;
            }
            
            int units_consumed = Integer.parseInt(units);
            int current_year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            
            // Calculate bill based on units (₹7 per unit + fixed charges)
            int unit_cost = units_consumed * 7;
            int fixed_charges = 50 + 12 + 102 + 20 + 50; // Service, Meter rent, MCB charges, etc.
            int total_bill = unit_cost + fixed_charges;
            
            String status = "Not Paid";
            
            try {
                conn connection = new conn();
                // The correct SQL query with all required fields
                String query = "INSERT INTO bill (meter_no, month, year, units, total_bill, status) " + 
                               "VALUES ('" + meter_no + "', '" + month + "', " + current_year + 
                               ", " + units_consumed + ", " + total_bill + ", '" + status + "')";
                               
                connection.s.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Bill Generated Successfully");
                t1.setText("");
            } catch(Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else if(ae.getSource() == b2) {
            this.setVisible(false);
        }
    }

    public static void main(String[] args){
        new calculate_bill().setVisible(true);
    }
}