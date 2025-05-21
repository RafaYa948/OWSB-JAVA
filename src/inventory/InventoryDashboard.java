package view.inventory;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import models.PurchaseOrder;

import java.awt.*;
import java.util.List;

public class InventoryDashboard extends JFrame {

    private String username;
    private InventoryManager inventoryManager;
    private JPanel contentArea; // Dynamic center panel

    public InventoryDashboard(String username, InventoryManager inventoryManager) {
        this.username = username;
        this.inventoryManager = inventoryManager;

        setTitle("Inventory Manager Dashboard - " + username);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Role: Inventory Manager"));
        left.add(Box.createHorizontalStrut(20));
        left.add(new JLabel("User: " + username));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose(); // Close dashboard
            // Optionally go back to login or main menu if needed
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(logoutBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {
            "View Current Stock",
            "Update Stock From PO",
            "Manual Stock Adjustment",
            "Generate Stock Report",
            "View Stock History",
            "View System Logs"
        };

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(btn);

            switch (item) {
                case "View Current Stock":
                    btn.addActionListener(e -> showCurrentStock());
                    break;
                case "Update Stock From PO":
                    btn.addActionListener(e -> showPOUpdate());
                    break;
                case "Manual Stock Adjustment":
                    btn.addActionListener(e -> showManualAdjustment());
                    break;
                case "Generate Stock Report":
                    btn.addActionListener(e -> showStockReport());
                    break;
                case "View Stock History":
                    btn.addActionListener(e -> showStockHistory());
                    break;
                case "View System Logs":
                    btn.addActionListener(e -> showSystemLogs());
                    break;
            }
        }

        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content Area
        contentArea = new JPanel(new BorderLayout());
        contentArea.add(new JLabel("<html><h2>Welcome, " + username + "</h2><p>Select an action from the sidebar.</p></html>", SwingConstants.CENTER), BorderLayout.CENTER);
        mainPanel.add(contentArea, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void updateMainPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showCurrentStock() {
        JPanel stockPanel = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter By:"));
        JCheckBox lowStockCheck = new JCheckBox("Low Stock");
        filterPanel.add(lowStockCheck);

        String[] columns = {"Item Code", "Item Name", "Current Quantity"};
        Object[][] data = {
            {"ITM001", "Flour", 50},
            {"ITM002", "Sugar", 8},
            {"ITM003", "Rice", 30}
        };

        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if ((int) getValueAt(row, 2) < 10) {
                    c.setBackground(Color.PINK);
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };

        stockPanel.add(filterPanel, BorderLayout.NORTH);
        stockPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        updateMainPanel(stockPanel);
    }

    private void showPOUpdate() {
        JPanel poPanel = new JPanel(new BorderLayout());

        String[] columns = {"PO ID", "Item Code", "Item Name", "Ordered Qty", "Received Qty"};
        Object[][] data = {
            {"PO001", "ITM001", "Flour", 100, 0},
            {"PO002", "ITM002", "Sugar", 50, 0},
            {"PO003", "ITM003", "Rice", 200, 0}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton updateBtn = new JButton("Confirm Receipt");
        updateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String poId = table.getValueAt(selectedRow, 0).toString();
                String itemCode = table.getValueAt(selectedRow, 1).toString();
                int receivedQty;

                try {
                    String input = JOptionPane.showInputDialog(this, "Enter received quantity:");
                    if (input == null) return;
                    receivedQty = Integer.parseInt(input);
                    if (receivedQty <= 0) throw new NumberFormatException();

                    boolean success = inventoryManager.updateStockFromPO(poId, itemCode, receivedQty);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Stock updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update stock. Item may not exist.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity entered.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row first.");
            }
        });

        poPanel.add(scrollPane, BorderLayout.CENTER);
        poPanel.add(updateBtn, BorderLayout.SOUTH);
        updateMainPanel(poPanel);
    }

    private void showManualAdjustment() {
        JPanel adjustmentPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JLabel itemCodeLabel = new JLabel("Item Code:");
        JTextField itemCodeField = new JTextField();

        JLabel quantityLabel = new JLabel("New Quantity:");
        JTextField quantityField = new JTextField();

        JButton updateBtn = new JButton("Update Stock");

        JLabel statusLabel = new JLabel(" ");

        updateBtn.addActionListener(e -> {
            String code = itemCodeField.getText().trim();
            String qtyText = quantityField.getText().trim();

            try {
                int qty = Integer.parseInt(qtyText);
                boolean success = inventoryManager.manualStockAdjustment(code, qty);

                if (success) {
                    statusLabel.setText("Stock updated successfully!");
                    statusLabel.setForeground(Color.GREEN);
                } else {
                    statusLabel.setText("Item not found or invalid quantity.");
                    statusLabel.setForeground(Color.RED);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid quantity format.");
                statusLabel.setForeground(Color.RED);
            }
        });

        adjustmentPanel.add(itemCodeLabel);
        adjustmentPanel.add(itemCodeField);
        adjustmentPanel.add(quantityLabel);
        adjustmentPanel.add(quantityField);
        adjustmentPanel.add(updateBtn);
        adjustmentPanel.add(statusLabel);

        updateMainPanel(adjustmentPanel);
    }

    private void showStockReport() {
        JPanel reportPanel = new JPanel(new BorderLayout());

        String[] columns = {"Item Code", "Item Name", "Supplier ID", "Quantity"};
        Object[][] data = {
            {"ITM001", "Flour", "SUP001", 50},
            {"ITM002", "Sugar", "SUP002", 8},
            {"ITM003", "Rice", "SUP003", 30}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        reportPanel.add(scrollPane, BorderLayout.CENTER);
        updateMainPanel(reportPanel);
    }

    private void showStockHistory() {
        JPanel historyPanel = new JPanel(new BorderLayout());

        String[] columns = {"Date", "Item Code", "Item Name", "Quantity Change", "Remarks"};
        Object[][] data = {
            {"2025-05-20", "ITM001", "Flour", +50, "Initial stock"},
            {"2025-05-21", "ITM002", "Sugar", -5, "Sold to customer"},
            {"2025-05-21", "ITM003", "Rice", +30, "PO received"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        historyPanel.add(scrollPane, BorderLayout.CENTER);
        updateMainPanel(historyPanel);
    }

    private void showSystemLogs() {
        JPanel logsPanel = new JPanel(new BorderLayout());

        String[] columns = {"Timestamp", "User", "Action", "Details"};
        Object[][] data = {
            {"2025-05-21 09:00", "inventoryManager1", "Login", "Successful login"},
            {"2025-05-21 09:15", "inventoryManager1", "Stock Update", "Added 50 units of ITM001"},
            {"2025-05-21 09:30", "inventoryManager2", "Manual Adjustment", "Reduced 5 units of ITM002"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        logsPanel.add(scrollPane, BorderLayout.CENTER);
        updateMainPanel(logsPanel);
    }
}
