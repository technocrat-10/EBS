import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class pay_bill extends JFrame implements ActionListener {
    JLabel l1, l2, l3, l4, l5;
    JTextField t1;
    Choice c1, c2;
    JButton b1, b2;
    JPanel p;
    
    pay_bill() {
        setLayout(null);
        
        p = new JPanel();
        p.setLayout(null);
        p.setBounds(30, 30, 650, 300);
        p.setBackground(Color.WHITE);
        
        l1 = new JLabel("Pay Electricity Bill");
        l1.setBounds(120, 10, 400, 30);
        l1.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(l1);
        
        l2 = new JLabel("Meter Number");
        l2.setBounds(35, 80, 200, 20);
        p.add(l2);
        
        c1 = new Choice();
        
        try {
            conn connection = new conn();
            ResultSet rs = connection.s.executeQuery("SELECT meter_no FROM customer");
            while(rs.next()) {
                c1.add(rs.getString("meter_no"));
            }
            
            if (c1.getItemCount() == 0) {
                c1.add("No Meter Numbers");
            }
        } catch(Exception e) {
            e.printStackTrace();
            c1.add("Error loading");
        }
        
        c1.setBounds(250, 80, 200, 20);
        p.add(c1);
        
        l3 = new JLabel("Month");
        l3.setBounds(35, 140, 200, 20);
        p.add(l3);
        
        c2 = new Choice();
        c2.setBounds(250, 140, 200, 20);
        
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
        
        p.add(c2);
        
        l4 = new JLabel("Amount");
        l4.setBounds(35, 200, 200, 20);
        p.add(l4);
        
        t1 = new JTextField();
        t1.setBounds(250, 200, 200, 20);
        t1.setEditable(false);
        p.add(t1);
        
        l5 = new JLabel("(If amount shows '0', bill doesn't exist or is already paid)");
        l5.setBounds(35, 230, 450, 20);
        l5.setForeground(Color.RED);
        l5.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p.add(l5);
        
        ItemListener updateAmount = new ItemListener() {
            public void itemStateChanged(ItemEvent ae) {
                try {
                    conn connection = new conn();
                    String meter = c1.getSelectedItem();
                    String month = c2.getSelectedItem();
                    
                    String query = "SELECT * FROM bill WHERE meter_no='" + meter + "' AND month='" + month + "'";
                    ResultSet rs = connection.s.executeQuery(query);
                    
                    if(rs.next()) {
                        String status = rs.getString("status");
                        String amount = rs.getString("total_bill");
                        
                        if(status.equals("Paid")) {
                            t1.setText("0 (Already Paid)");
                            b1.setEnabled(false);
                        } else {
                            t1.setText(amount);
                            b1.setEnabled(true);
                        }
                    } else {
                        t1.setText("0 (No bill found)");
                        b1.setEnabled(false);
                    }
                } catch(Exception e) {
                    t1.setText("Error");
                    e.printStackTrace();
                }
            }
        };
        
        c1.addItemListener(updateAmount);
        c2.addItemListener(updateAmount);
        
        b1 = new JButton("Pay");
        b1.setBounds(100, 260, 100, 25);
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        p.add(b1);
        
        b2 = new JButton("Back");
        b2.setBounds(230, 260, 100, 25);
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);
        b2.addActionListener(this);
        p.add(b2);
        
        add(p);
        
        getContentPane().setBackground(Color.WHITE);
        setSize(750, 400);
        setLocation(400, 200);
        
        if(c1.getItemCount() > 0) {
            updateAmount.itemStateChanged(null);
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == b1) {
            try {
                conn connection = new conn();
                
                String meterNo = c1.getSelectedItem();
                String month = c2.getSelectedItem();
                
                String query = "UPDATE bill SET status='Paid' WHERE meter_no='" + meterNo + "' AND month='" + month + "' AND status='Not Paid'";
                int rs = connection.s.executeUpdate(query);
                
                if(rs > 0) {
                    JOptionPane.showMessageDialog(null, "Bill Paid Successfully");
                    t1.setText("0 (Already Paid)");
                    b1.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Bill payment failed or already paid");
                }
                
            } catch(Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else if(ae.getSource() == b2) {
            this.setVisible(false);
        }
    }
    
    public static void main(String[] args) {
        new pay_bill().setVisible(true);
    }
}