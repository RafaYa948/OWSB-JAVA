package finance;

import admin.UIBase;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.io.IOException;
import java.time.LocalDate;
import models.SystemLog;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import models.PurchaseRequisition;
import models.User;
import database.DatabaseHelper;
import finance.FinanceDashboardPage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FinanceViewPurchaseRequisitionsPage extends UIBase {
    
    private final User currentUser;
    private JTable requisitionsTable;
    private DefaultTableModel tableModel;
    private List<PurchaseRequisition> requisitionsList;
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FinanceViewPurchaseRequisitionsPage(User user) {
        super("Purchase Requisitions");
        this.currentUser = user;
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadRequisitions();
        }
        super.setVisible(visible);
    }

    public void loadRequisitions() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            requisitionsList = dbHelper.getAllPurchaseRequisitions();
            
            if (requisitionsList == null) {
                requisitionsList = new ArrayList<>();
            } else {
                filterRequisitions(null);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            requisitionsList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Error loading requisitions: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            requisitionsList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterRequisitions(String status) {
        tableModel.setRowCount(0);
        for (PurchaseRequisition requisition : requisitionsList) {
            if (requisition != null && (status == null || requisition.getStatus().equals(status))) {
                
                try {
                    Object[] rowData = {
                        requisition.getRequisitionId(),
                        requisition.getItemCode(),
                        requisition.getItemName() != null ? requisition.getItemName() : "",
                        Integer.valueOf(requisition.getQuantity()),
                        requisition.getRequiredDate() != null ? requisition.getRequiredDate().format(displayDateFormatter) : "",
                        requisition.getSalesManagerId(),
                        requisition.getStatus()
                    };
                    tableModel.addRow(rowData);
                } catch (Exception e) {
                    System.out.println("Warning: Error formatting row data for requisition " + requisition.getRequisitionId());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void initUI() {
        requisitionsList = new ArrayList<>();
        
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);
        
        JPanel topBar = createTopBar();
        root.add(topBar, BorderLayout.NORTH);
        
        JPanel contentPanel = createContentPanel();
        root.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, APP_WINDOW_HEIGHT));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel placeholder = new JLabel("Our System Logo", SwingConstants.CENTER);
        placeholder.setFont(new Font("Serif", Font.BOLD, 16));
        placeholder.setForeground(new Color(11, 61, 145));
        placeholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        logoPanel.add(placeholder, BorderLayout.CENTER);

        sidebar.add(logoPanel, BorderLayout.NORTH);
        
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        
        JPanel dashboardItem = createMenuItem("Dashboard", false);
        dashboardItem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new FinanceDashboardPage(currentUser).setVisible(true);
            }
        });
        
        JPanel requisitionsItem = createMenuItem("Purchase Requisitions", true);
        
        menuPanel.add(dashboardItem);
        menuPanel.add(requisitionsItem);
        menuPanel.add(Box.createVerticalGlue());
        
        sidebar.add(menuPanel, BorderLayout.CENTER);
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBackground(Color.WHITE);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(120, 120, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutBtn.setPreferredSize(new Dimension(120, 35));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(120, 120, 120), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutBtn.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(FinanceViewPurchaseRequisitionsPage.this,
                "Are you sure you want to log out?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });
        
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }

    private JPanel createTopBar() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.WHITE);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setBackground(new Color(180, 180, 180));
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));
        
        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(bell);
        
        String displayName = (currentUser != null && currentUser.getUsername() != null && !currentUser.getUsername().isEmpty())
                           ? currentUser.getUsername()
                           : "Username user";
        
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
        
        topContainer.add(userPanel, BorderLayout.NORTH);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("Purchase Requisitions");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(11, 61, 145));
        
        headerPanel.add(title);
        
        topContainer.add(headerPanel, BorderLayout.SOUTH);
        
        return topContainer;
    }

    private JPanel createMenuItem(String text, boolean isSelected) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setBackground(isSelected ? new Color(230, 230, 230) : Color.WHITE);
        menuItem.setMaximumSize(new Dimension(200, 50));
        menuItem.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel menuLabel = new JLabel(text);
        menuLabel.setFont(new Font("Serif", Font.BOLD, 16));
        menuItem.add(menuLabel, BorderLayout.CENTER);
        
        return menuItem;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        String[] columnNames = {"Requisition ID", "Item Code", "Item Name", "Quantity", "Required Date", "Sales Manager", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) { 
                    return Integer.class;
                }
                return String.class;
            }
        };
    
        requisitionsTable = new JTable(tableModel);
        requisitionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requisitionsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        requisitionsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        requisitionsTable.setRowHeight(30);
        requisitionsTable.setGridColor(Color.LIGHT_GRAY);
        
        
        requisitionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    try {
                        String status = (String) table.getValueAt(row, 6); 
                        
                        if (PurchaseRequisition.STATUS_APPROVED.equals(status)) {
                            c.setBackground(new Color(230, 255, 230)); 
                        } else if (PurchaseRequisition.STATUS_REJECTED.equals(status)) {
                            c.setBackground(new Color(255, 230, 230)); 
                        } else {
                            c.setBackground(new Color(255, 255, 230)); 
                        }
                    } catch (Exception e) {
                        
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
    
        JScrollPane scrollPane = new JScrollPane(requisitionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
    
        JButton filterBtn = new JButton("Filter by: All Status ▼");
        styleButton(filterBtn);
        createFilterPopup(filterBtn);
        buttonsPanel.add(filterBtn);
        
        JButton detailsButton = new JButton("View Details");
        styleButton(detailsButton);
        detailsButton.addActionListener(e -> {
            int selectedRow = requisitionsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select a requisition to view details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                String requisitionId = String.valueOf(requisitionsTable.getValueAt(selectedRow, 0));
                String itemCode = String.valueOf(requisitionsTable.getValueAt(selectedRow, 1));
                String itemName = String.valueOf(requisitionsTable.getValueAt(selectedRow, 2));
                int quantity = Integer.parseInt(String.valueOf(requisitionsTable.getValueAt(selectedRow, 3)));
                String dateStr = String.valueOf(requisitionsTable.getValueAt(selectedRow, 4));
                String salesManagerId = String.valueOf(requisitionsTable.getValueAt(selectedRow, 5));
                String status = String.valueOf(requisitionsTable.getValueAt(selectedRow, 6));
                
                StringBuilder details = new StringBuilder();
                details.append("Requisition ID: ").append(requisitionId).append("\n\n");
                details.append("Item Code: ").append(itemCode).append("\n");
                details.append("Item Name: ").append(itemName).append("\n");
                details.append("Quantity: ").append(quantity).append("\n");
                details.append("Required Date: ").append(dateStr).append("\n");
                details.append("Sales Manager ID: ").append(salesManagerId).append("\n");
                details.append("Status: ").append(status).append("\n");
                
                JTextArea textArea = new JTextArea(details.toString());
                textArea.setEditable(false);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
                textArea.setBackground(UIManager.getColor("Panel.background"));
                textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JScrollPane scrollPane2 = new JScrollPane(textArea);
                scrollPane2.setPreferredSize(new Dimension(400, 300));
                
                JOptionPane.showMessageDialog(this,
                    scrollPane2,
                    "Requisition Details",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error displaying details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(detailsButton);
        
        
        if (currentUser != null && 
            (User.ROLE_PURCHASE_MANAGER.equals(currentUser.getRole()) || 
             User.ROLE_ADMINISTRATOR.equals(currentUser.getRole()))) {
                 
            JButton updateStatusButton = new JButton("Update Status");
            styleButton(updateStatusButton);
            updateStatusButton.addActionListener(e -> {
                int selectedRow = requisitionsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this,
                        "Please select a requisition to update status.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    String requisitionId = String.valueOf(requisitionsTable.getValueAt(selectedRow, 0));
                    String currentStatus = String.valueOf(requisitionsTable.getValueAt(selectedRow, 6));
                    
                    if (PurchaseRequisition.STATUS_APPROVED.equals(currentStatus) || 
                        PurchaseRequisition.STATUS_REJECTED.equals(currentStatus)) {
                        JOptionPane.showMessageDialog(this,
                            "This requisition has already been " + currentStatus.toLowerCase() + ".",
                            "Status Already Updated",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    String[] options = { "Approve", "Reject", "Cancel" };
                    int choice = JOptionPane.showOptionDialog(this,
                        "Update status for requisition " + requisitionId + ":",
                        "Update Status",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                    
                    if (choice == 0 || choice == 1) { 
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper();
                            PurchaseRequisition requisition = dbHelper.getPurchaseRequisitionById(requisitionId);
                            
                            if (requisition != null) {
                                requisition.setStatus(choice == 0 ? 
                                    PurchaseRequisition.STATUS_APPROVED : 
                                    PurchaseRequisition.STATUS_REJECTED);
                                
                                dbHelper.updatePurchaseRequisition(requisition);
                                
                                JOptionPane.showMessageDialog(this,
                                    "Requisition " + requisitionId + " has been " + 
                                    (choice == 0 ? "approved" : "rejected") + ".",
                                    "Status Updated",
                                    JOptionPane.INFORMATION_MESSAGE);
                                    
                                loadRequisitions(); 
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                "Error updating requisition status: " + ex.getMessage(),
                                "Update Failed",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error updating status: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonsPanel.add(updateStatusButton);
        } else {
            
            buttonsPanel.add(Box.createHorizontalStrut(10));
        }
    
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
    
        return contentPanel;
    }
    
    
    private void createFilterPopup(JButton filterBtn) {
        JPopupMenu filterMenu = new JPopupMenu();
        filterMenu.setBackground(Color.WHITE);
    
        String[] statuses = {
            "All Status",
            PurchaseRequisition.STATUS_PENDING,
            PurchaseRequisition.STATUS_APPROVED,
            PurchaseRequisition.STATUS_REJECTED
        };
    
        for (String status : statuses) {
            JMenuItem item = new JMenuItem(status);
            item.setFont(new Font("SansSerif", Font.PLAIN, 14));
            item.setBackground(Color.WHITE);
            item.addActionListener(e -> {
                filterRequisitions(status.equals("All Status") ? null : status);
                filterBtn.setText("Filter by: " + status + " ▼");
            });
            filterMenu.add(item);
        }
    
        filterBtn.addActionListener(e -> {
            filterMenu.show(filterBtn, 0, filterBtn.getHeight());
        });
    }
    
    private void styleButton(JButton button) {
        button.setBackground(new Color(120, 120, 120));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
    }

    private void goBackToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
                new FinanceDashboardPage(currentUser).setVisible(true);
        });
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