package finance;

import admin.UIBase;
import database.DatabaseHelper;
import models.PurchaseOrder;
import models.User;
import models.SystemLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Cursor;
import java.time.LocalDateTime;

public class FinanceProcessPaymentsPage extends UIBase {

    private final User currentUser;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private static final String STATUS_COMPLETED = "Completed";
    private static final String STATUS_PAID = "Completed";

    public FinanceProcessPaymentsPage(User user) {
        super("Process Payments");
        this.currentUser = user;
        initUI();
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createTopBar(), BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(root);
        loadApprovedOrders();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, APP_WINDOW_HEIGHT));
        sidebar.setBackground(Color.WHITE);

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        JLabel logo = new JLabel("Finance Dept", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 16));
        logo.setForeground(primaryColor);
        logoPanel.add(logo, BorderLayout.CENTER);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);

        JPanel dashboard = createMenuItem("Dashboard", false);
        dashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new FinanceDashboardPage(currentUser).setVisible(true);
            }
        });

        JPanel thisPage = createMenuItem("Process Payments", true);

        menuPanel.add(dashboard);
        menuPanel.add(thisPage);
        menuPanel.add(Box.createVerticalGlue());
        sidebar.add(menuPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(90, 90, 90));
        logout.setForeground(Color.WHITE);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setFocusPainted(false);
        logout.setPreferredSize(new Dimension(100, 30));
        logout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });
        bottom.add(logout);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setBackground(new Color(180, 180, 180));

        JLabel bell = new JLabel("\uD83D\uDD14");
        userPanel.add(bell);

         JLabel userLabel = new JLabel("User");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userPanel.add(userLabel);

        top.add(userPanel, BorderLayout.NORTH);

        JLabel title = new JLabel("Process Payments", SwingConstants.CENTER);
        title.setFont(headerFont);
        title.setForeground(primaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        top.add(title, BorderLayout.SOUTH);
        return top;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new String[]{"Order ID", "Item", "Qty", "Amount", "Supplier ID", "Status"}, 0);
        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
       JButton markPaid = new JButton("Mark as Paid");
       
        markPaid.setBackground(new Color(90, 90, 90));
        markPaid.setForeground(Color.WHITE);
        markPaid.setFocusPainted(false);
        JButton simulate = new JButton("Simulate Payment");
        simulate.setBackground(new Color(90, 90, 90));
        simulate.setForeground(Color.WHITE);
        simulate.setFocusPainted(false);

                markPaid.addActionListener(e -> markAsPaid());
               
   
        simulate.addActionListener(new java.awt.event.ActionListener() {
     public void actionPerformed(java.awt.event.ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(FinanceProcessPaymentsPage.this, "Please select an order to simulate payment.");
            return;
        }

        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        String item = (String) tableModel.getValueAt(selectedRow, 1);
        String amount = (String) tableModel.getValueAt(selectedRow, 3);
        String supplier = (String) tableModel.getValueAt(selectedRow, 4);

        
        JOptionPane.showMessageDialog(FinanceProcessPaymentsPage.this,
                "Simulating payment for Order " + orderId + "...\nPlease wait.");

        
        try (BufferedWriter log = new BufferedWriter(new FileWriter("src/database/finance_log.txt", true))) {
            String timestamp = java.time.LocalDateTime.now().toString();
            log.write("[" + timestamp + "] Simulated payment for Order " + orderId +
                    " | Item: " + item +
                    " | Amount: " + amount +
                    " | Supplier: " + supplier +
                    " | User: " + currentUser.getUsername());
            log.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(FinanceProcessPaymentsPage.this, "Failed to write simulation log: " + ex.getMessage());
        }

       
        JOptionPane.showMessageDialog(FinanceProcessPaymentsPage.this,
                "Payment simulation complete for Order " + orderId + ".\n(No real payment was processed.)",
                "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
        logSystemAction("View", "Simulated payment for PO " + orderId);
        
    }
        });

        buttons.add(markPaid);
        buttons.add(simulate);

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void loadApprovedOrders() {
        try {
            DatabaseHelper db = new DatabaseHelper();
            List<PurchaseOrder> orders = db.getAllPurchaseOrders();
            tableModel.setRowCount(0);

            for (PurchaseOrder po : orders) {
                if (STATUS_COMPLETED.equalsIgnoreCase(po.getStatus())) {
                    tableModel.addRow(new Object[]{
                        po.getOrderId(),
                        po.getItemName(),
                        po.getQuantity(),
                        String.format("RM %.2f", po.getTotalAmount()),
                        po.getSupplierId(),
                        po.getStatus()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading approved POs: " + e.getMessage());
        }
    }

    private void markAsPaid() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an approved PO to mark as paid.");
            return;
        }

        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        updateStatusDirectly(orderId, STATUS_COMPLETED);
        JOptionPane.showMessageDialog(this, "Purchase Order " + orderId + " marked as Paid.");
        loadApprovedOrders();
    }

    private JPanel createMenuItem(String title, boolean isSelected) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(200, 40));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(isSelected ? new Color(210, 210, 255) : Color.WHITE);

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        panel.add(label);

        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return panel;
    }

   
    private void updateStatusDirectly(String orderId, String newStatus) {
    File file = new File("src/database/purchase_order.txt");
    List<String> updatedLines = new ArrayList<>();
    String[] updatedOrder = null;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length > 0 && parts[0].equals(orderId)) {
                parts[10] = newStatus; 
                updatedOrder = parts;
                line = String.join(",", parts);
            }
            updatedLines.add(line);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error reading PO file: " + e.getMessage());
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        for (String updatedLine : updatedLines) {
            writer.write(updatedLine);
            writer.newLine();
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error writing PO file: " + e.getMessage());
        return;
    }

   
    if (updatedOrder != null) {
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("src/database/finance_log.txt", true))) {
            String timestamp = java.time.LocalDateTime.now().toString();
            String log = String.format("[%s] User %s marked PO %s as PAID - Amount: %s",
                    timestamp, currentUser.getUsername(), updatedOrder[0], updatedOrder[3]);
            logWriter.write(log);
            logWriter.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to finance log: " + e.getMessage());
        }
    }
    }
    private void logSystemAction(String action, String details) {
        try {
    DatabaseHelper db = new DatabaseHelper();
    SystemLog log = new SystemLog(
        "LOG" + System.currentTimeMillis(),
        currentUser.getUserId(),
        currentUser.getUsername(),
        action,
        details,
        LocalDateTime.now(),
        currentUser.getRole()
    );
    db.addSystemLog(log);
} catch (IOException e) {
    System.out.println("Failed to log system action: " + e.getMessage());
}
    }
}
