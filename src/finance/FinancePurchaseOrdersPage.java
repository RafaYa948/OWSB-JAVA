package finance;

import admin.UIBase;
import database.DatabaseHelper;
import models.PurchaseOrder;
import models.User;
import models.SystemLog;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FinancePurchaseOrdersPage extends UIBase {

    private final User currentUser;
    private JTable poTable;
    private DefaultTableModel tableModel;
    private List<PurchaseOrder> orders;

    public FinancePurchaseOrdersPage(User user) {
        super("Approve/Modify Purchase Orders");
        this.currentUser = user;
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel sidebar = createSidebar();
        JPanel topBar = createTopBar();
        JPanel content = createMainContent();

        root.add(sidebar, BorderLayout.WEST);
        root.add(topBar, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);
        loadPendingOrders();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, APP_WINDOW_HEIGHT));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel logo = new JLabel("Finance Dept", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 16));
        logo.setForeground(primaryColor);
        logoPanel.add(logo, BorderLayout.CENTER);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JPanel dashboardItem = createMenuItem("Dashboard", false);
        dashboardItem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new FinanceDashboardPage(currentUser).setVisible(true);
            }
        });

        JPanel thisPageItem = createMenuItem("Purchase Orders", true);

        menuPanel.add(dashboardItem);
        menuPanel.add(thisPageItem);
        menuPanel.add(Box.createVerticalGlue());

        sidebar.add(menuPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(120, 120, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setPreferredSize(new Dimension(120, 35));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setFocusPainted(false);

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Log out?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        bottom.add(logoutBtn);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
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

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setBackground(new Color(180, 180, 180));
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));

        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userPanel.add(bell);

        String displayName = (currentUser != null && currentUser.getUsername() != null && !currentUser.getUsername().isEmpty())
                ? currentUser.getUsername()
                : "User";

        JLabel userLabel = new JLabel(displayName + " ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);
        userLabel.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        dispose();
        new admin.MyProfilePage(currentUser).setVisible(true);
    }
});

        top.add(userPanel, BorderLayout.NORTH);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Approve/Modify Purchase Orders");
        title.setFont(headerFont);
        title.setForeground(primaryColor);
        titlePanel.add(title);

        top.add(titlePanel, BorderLayout.SOUTH);
        return top;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] cols = {"Order ID", "Requisition ID", "Item Code", "Item Name",
                "Quantity", "Unit Price", "Total", "Order Date", "Supplier ID"};

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        poTable = new JTable(tableModel);
        poTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String orderId = (String) table.getValueAt(row, 0);
        PurchaseOrder po = orders.stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (po != null) {
            switch (po.getStatus()) {
                case PurchaseOrder.STATUS_PENDING:
                    c.setBackground(new Color(255, 250, 205)); 
                    break;
                case PurchaseOrder.STATUS_COMPLETED:
                    c.setBackground(new Color(224, 255, 224)); 
                    break;
                case PurchaseOrder.STATUS_CANCELLED:
                    c.setBackground(new Color(255, 224, 224)); 
                    break;
                default:
                    c.setBackground(Color.WHITE);
                    break;
            }
        }

        if (isSelected) {
            c.setBackground(new Color(173, 216, 230)); 
        }

        return c;
    }
});
        poTable.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(poTable);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton approve = createActionButton("Approve", () -> updateStatus(PurchaseOrder.STATUS_COMPLETED));
        JButton reject = createActionButton("Reject", () -> updateStatus(PurchaseOrder.STATUS_CANCELLED));
        JButton modify = createActionButton("Modify Quantity", this::modifyQuantity);
        JButton supplier = createActionButton("Change Supplier", this::changeSupplier);

        buttonPanel.add(approve);
        buttonPanel.add(reject);
        buttonPanel.add(modify);
        buttonPanel.add(supplier);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createActionButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.setBackground(new Color(80, 80, 80));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void loadPendingOrders() {
        try {
            DatabaseHelper db = new DatabaseHelper();
            orders = db.getAllPurchaseOrders();
            tableModel.setRowCount(0);
            for (PurchaseOrder po : orders) {
                if (PurchaseOrder.STATUS_PENDING.equals(po.getStatus())) {
                    tableModel.addRow(new Object[]{
                            po.getOrderId(), po.getRequisitionId(), po.getItemCode(), po.getItemName(),
                            po.getQuantity(), po.getUnitPrice(), po.getTotalAmount(),
                            po.getOrderDate(), po.getSupplierId()
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void updateStatus(String status) {
        int row = poTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a PO.");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);

        try {
            DatabaseHelper db = new DatabaseHelper();
            PurchaseOrder po = db.getPurchaseOrderById(id);
            if (po != null) {
                po.setStatus(status);
                db.updatePurchaseOrder(po);
                JOptionPane.showMessageDialog(this, "Status updated.");
                loadPendingOrders();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage());
        }
    }

    private void modifyQuantity() {
        int row = poTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a PO.");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        String qtyStr = JOptionPane.showInputDialog(this, "New quantity:");

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();

            DatabaseHelper db = new DatabaseHelper();
            PurchaseOrder po = db.getPurchaseOrderById(id);
            if (po != null) {
                po.setQuantity(qty);
                po.setTotalAmount(po.getUnitPrice() * qty);
                db.updatePurchaseOrder(po);
                JOptionPane.showMessageDialog(this, "Quantity updated.");
                loadPendingOrders();
               SystemLog log = new SystemLog(
    "LOG" + System.currentTimeMillis(),
    currentUser.getUserId(),
    currentUser.getUsername(),
    SystemLog.ACTION_UPDATE,
    "Modified quantity for " + po.getOrderId() + " to " + qty,
    LocalDateTime.now(),
    currentUser.getRole()
);

db.addSystemLog(log); 
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
        }
    }

    private void changeSupplier() {
        int row = poTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a PO.");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        String newId = JOptionPane.showInputDialog(this, "New Supplier ID:");

        try {
            if (newId == null || newId.isBlank()) return;

            DatabaseHelper db = new DatabaseHelper();
            PurchaseOrder po = db.getPurchaseOrderById(id);
            if (po != null) {
                po.setSupplierId(newId.trim());
                db.updatePurchaseOrder(po);
                JOptionPane.showMessageDialog(this, "Supplier updated.");
                loadPendingOrders();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage());
        }
    }
}
