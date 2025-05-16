package finance;

import admin.UIBase;
import database.DatabaseHelper;
import models.PurchaseOrder;
import models.User;
import finance.FinanceDashboardPage;
import models.SystemLog;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.awt.Desktop;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;

public class FinanceGenerateReportsPage extends UIBase {

    private final User currentUser;
    private JTable paidTable;
    private DefaultTableModel tableModel;
    private static final String STATUS_PAID = "Paid";
    private static final String STATUS_COMPLETED = "Completed";
    private static final String STATUS_PENDING = "Pending";
    private JLabel approvedLabel, paidLabel, outstandingLabel, expenditureLabel;

    public FinanceGenerateReportsPage(User user) {
        super("Generate Financial Reports");
        this.currentUser = user;
        logSystemAction("ViewlogS", "Opened FinanceGenerateReportsPage");
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createTopBar(), BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(root);
        updateSummary();
        
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

        JPanel thisPage = createMenuItem("Generate Financial Reports", true);

        menuPanel.add(dashboard);
        menuPanel.add(thisPage);
        menuPanel.add(Box.createVerticalGlue());

        sidebar.add(menuPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(120, 120, 120));
        logout.setForeground(Color.WHITE);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        JLabel bell = new JLabel("🔔");
        userPanel.add(bell);

        JLabel userLabel = new JLabel("User");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userPanel.add(userLabel);
            
        

        top.add(userPanel, BorderLayout.NORTH);

        JLabel title = new JLabel("Generate Financial Reports", SwingConstants.CENTER);
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

        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        summaryPanel.setBackground(Color.WHITE);

        approvedLabel = createSummaryLabel();
        paidLabel = createSummaryLabel();
        outstandingLabel = createSummaryLabel();
        expenditureLabel = createSummaryLabel();

        summaryPanel.add(new JLabel("Approved POs:", SwingConstants.RIGHT));
        summaryPanel.add(approvedLabel);
        summaryPanel.add(new JLabel("Paid POs:", SwingConstants.RIGHT));
        summaryPanel.add(paidLabel);
        summaryPanel.add(new JLabel("Outstanding Payments:", SwingConstants.RIGHT));
        summaryPanel.add(outstandingLabel);
        summaryPanel.add(new JLabel("Total Expenditure:", SwingConstants.RIGHT));
        summaryPanel.add(expenditureLabel);

        JButton generateBtn = new JButton("Generate Report");
        generateBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateBtn.setPreferredSize(new Dimension(200, 40));
        generateBtn.setBackground(new Color(90, 90, 90));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);

        generateBtn.addActionListener(e -> {
             try {
        DatabaseHelper db = new DatabaseHelper();
        List<PurchaseOrder> orders = db.getAllPurchaseOrders();

        FileWriter writer = new FileWriter("finance_report.txt");
        writer.write("PAID PURCHASE ORDERS:\n");
        writer.write("--------------------------------------------------\n");

        for (PurchaseOrder po : orders) {
            System.out.println("Checking: " + po.getOrderId() + " = " + po.getStatus());
            if (po.getStatus() != null && po.getStatus().trim().equalsIgnoreCase("Paid")) {
                writer.write("Order ID: " + po.getOrderId() + "\n");
                writer.write("Item Name: " + po.getItemName() + "\n");
                writer.write("Quantity: " + po.getQuantity() + "\n");
                writer.write("Unit Price: RM " + String.format("%.2f", po.getUnitPrice()) + "\n");
                writer.write("Total Amount: RM " + String.format("%.2f", po.getTotalAmount()) + "\n");
                writer.write("Order Date: " + po.getOrderDate() + "\n");
                writer.write("Supplier ID: " + po.getSupplierId() + "\n");
                writer.write("Status: " + po.getStatus() + "\n");
                writer.write("--------------------------------------------------\n");
            }
        }

        writer.close();
        JOptionPane.showMessageDialog(this, "Report generated successfully.");

    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage());
    }
});
        
        
        JButton viewBtn = new JButton("View Report");
    viewBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
    viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    viewBtn.setPreferredSize(new Dimension(200, 40));
    viewBtn.setBackground(new Color(70, 130, 180));
    viewBtn.setForeground(Color.WHITE);
    viewBtn.setFocusPainted(false);

viewBtn.addActionListener(e -> {
    try {
        Desktop.getDesktop().open(new File("finance_report.txt"));
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Unable to open file.");
    }
});

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.add(summaryPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(generateBtn);
        btnPanel.add(viewBtn); 

        panel.add(center, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel createSummaryLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        return label;
    }

    private void updateSummary() {
     try {
            DatabaseHelper db = new DatabaseHelper();
            List<PurchaseOrder> orders = db.getAllPurchaseOrders();

            int approvedCount = 0;
            int paidCount = 0;
            double outstanding = 0;
            double totalSpent = 0;
            double totalcompleted = 0;

            FileWriter writer = new FileWriter("finance_report.txt");

            writer.write("APPROVED PURCHASE ORDERS:\n");
            writer.write("--------------------------------------------------\n");
            for (PurchaseOrder po : orders) {
               if (STATUS_COMPLETED.equalsIgnoreCase(po.getStatus())) {
                   System.out.println("Matched PAID order: " + po.getOrderId());
                    approvedCount++;
                    totalSpent += po.getTotalAmount();
                    writer.write("Order ID: " + po.getOrderId() + "\n");
                    writer.write("Requisition ID: " + po.getRequisitionId() + "\n");
                    writer.write("Item Code: " + po.getItemCode() + "\n");
                    writer.write("Item Name: " + po.getItemName() + "\n");
                    writer.write("Quantity: " + po.getQuantity() + "\n");
                    writer.write("Unit Price: RM " + String.format("%.2f", po.getUnitPrice()) + "\n");
                    writer.write("Total Amount: RM " + String.format("%.2f", po.getTotalAmount()) + "\n");
                    writer.write("Order Date: " + po.getOrderDate() + "\n");
                    writer.write("Supplier ID: " + po.getSupplierId() + "\n");
                    writer.write("Status: Approved\n");
                    writer.write("--------------------------------------------------\n");
                }
            }

            writer.write("\nPAID PURCHASE ORDERS:\n");
            writer.write("--------------------------------------------------\n");
            for (PurchaseOrder po : orders) {
                if (po.getStatus() != null && po.getStatus().trim().equalsIgnoreCase(STATUS_PAID)) {
                    paidCount++;
                    totalcompleted += po.getTotalAmount();
                    writer.write("Order ID: " + po.getOrderId() + "\n");
                    writer.write("Requisition ID: " + po.getRequisitionId() + "\n");
                    writer.write("Item Code: " + po.getItemCode() + "\n");
                    writer.write("Item Name: " + po.getItemName() + "\n");
                    writer.write("Quantity: " + po.getQuantity() + "\n");
                    writer.write("Unit Price: RM " + String.format("%.2f", po.getUnitPrice()) + "\n");
                    writer.write("Total Amount: RM " + String.format("%.2f", po.getTotalAmount()) + "\n");
                    writer.write("Order Date: " + po.getOrderDate() + "\n");
                    writer.write("Supplier ID: " + po.getSupplierId() + "\n");
                    writer.write("Status: Paid\n");
                    writer.write("--------------------------------------------------\n");
                }
            }

            writer.write("\nOUTSTANDING PAYMENTS:\n");
            writer.write("--------------------------------------------------\n");
            for (PurchaseOrder po : orders) {
                if (po.getStatus() != null && po.getStatus().trim().equalsIgnoreCase(STATUS_PENDING)) {
                    outstanding += po.getTotalAmount();
                    writer.write("Order ID: " + po.getOrderId() + "\n");
                    writer.write("Requisition ID: " + po.getRequisitionId() + "\n");
                    writer.write("Item Code: " + po.getItemCode() + "\n");
                    writer.write("Item Name: " + po.getItemName() + "\n");
                    writer.write("Quantity: " + po.getQuantity() + "\n");
                    writer.write("Unit Price: RM " + String.format("%.2f", po.getUnitPrice()) + "\n");
                    writer.write("Total Amount: RM " + String.format("%.2f", po.getTotalAmount()) + "\n");
                    writer.write("Order Date: " + po.getOrderDate() + "\n");
                    writer.write("Supplier ID: " + po.getSupplierId() + "\n");
                    writer.write("Status: Pending\n");
                    writer.write("--------------------------------------------------\n");
                }
            }

            writer.write("\nSUMMARY:\n");
            writer.write("--------------------------------------------------\n");
            writer.write(String.format("Approved POs: %d\n", approvedCount));
            writer.write(String.format("Paid POs: %d\n", paidCount));
            writer.write(String.format("Outstanding Payments: RM %.2f\n", outstanding));
            writer.write(String.format("Total Expenditure: RM %.2f\n", totalSpent));

            writer.close();

            approvedLabel.setText(String.valueOf(approvedCount));
            paidLabel.setText(String.valueOf(paidCount));
            outstandingLabel.setText("RM " + String.format("%.2f", outstanding));
            expenditureLabel.setText("RM " + String.format("%.2f", totalSpent));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to generate report: " + e.getMessage());
        }
    }



    private JPanel createMenuItem(String text, boolean selected) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(selected ? new Color(230, 230, 230) : Color.WHITE);
        item.setMaximumSize(new Dimension(200, 50));
        item.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, 16));
        item.add(label, BorderLayout.CENTER);

        return item;
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