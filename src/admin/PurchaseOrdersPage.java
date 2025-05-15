package admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import models.PurchaseOrder;
import models.User;
import database.DatabaseHelper;

public class PurchaseOrdersPage extends UIBase {
    
    private final User currentUser;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private List<PurchaseOrder> ordersList;
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    
    public PurchaseOrdersPage(User user) {
        super("Purchase Orders");
        this.currentUser = user;
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadOrders();
        }
        super.setVisible(visible);
    }

    public void loadOrders() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            ordersList = dbHelper.getAllPurchaseOrders();
            
            if (ordersList == null) {
                ordersList = new ArrayList<>();
            } else {
                filterOrders(null);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            ordersList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Error loading orders: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            ordersList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void filterOrders(String status) {
        tableModel.setRowCount(0);
        for (PurchaseOrder order : ordersList) {
            if (order != null && (status == null || order.getStatus().equals(status))) {
                try {
                    // Create default formatters if the class ones are null
                    NumberFormat currFormatter = (currencyFormatter != null) ?
                            currencyFormatter : NumberFormat.getCurrencyInstance();

                    DateTimeFormatter dateFormatter = (displayDateFormatter != null) ?
                            displayDateFormatter : DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    Object[] rowData = {
                            order.getOrderId(),
                            order.getRequisitionId(),
                            order.getItemCode(),
                            order.getItemName() != null ? order.getItemName() : "",
                            Integer.valueOf(order.getQuantity()),
                            currFormatter.format(order.getUnitPrice()),
                            currFormatter.format(order.getTotalAmount()),
                            order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "",
                            order.getExpectedDeliveryDate() != null ? order.getExpectedDeliveryDate().format(dateFormatter) : "",
                            order.getSupplierId(),
                            order.getStatus()
                    };
                    tableModel.addRow(rowData);
                } catch (Exception e) {
                    System.out.println("Warning: Error formatting row data for order " + order.getOrderId());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void initUI() {
        ordersList = new ArrayList<>();
        
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

        JLabel placeholder = new JLabel("OWSB", SwingConstants.CENTER);
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
                goBackToDashboard();
            }
        });
        
        JPanel ordersItem = createMenuItem("Purchase Orders", true);
        
        menuPanel.add(dashboardItem);
        menuPanel.add(ordersItem);
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
            int response = JOptionPane.showConfirmDialog(
                PurchaseOrdersPage.this,
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
                           : "Admin";
        
        JLabel userLabel = new JLabel(displayName + " ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);
        
        topContainer.add(userPanel, BorderLayout.NORTH);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("Purchase Orders");
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
        
        if (!isSelected) {
            menuItem.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuItem.setBackground(new Color(240, 240, 240));
                }
                
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuItem.setBackground(Color.WHITE);
                }
            });
        }
        
        return menuItem;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        String[] columnNames = {
            "Order ID", "Requisition ID", "Item Code", "Item Name", "Quantity", 
            "Unit Price", "Total Amount", "Order Date", "Expected Delivery", 
            "Supplier ID", "Status"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) { 
                    return Integer.class;
                }
                return String.class;
            }
        };
    
        ordersTable = new JTable(tableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getTableHeader().setBackground(new Color(240, 240, 240));
        ordersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        ordersTable.setRowHeight(30);
        ordersTable.setGridColor(Color.LIGHT_GRAY);
        
        
        ordersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(80);  
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(150); 
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(70);  
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100); 
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(100); 
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(100); 
        ordersTable.getColumnModel().getColumn(8).setPreferredWidth(120); 
        ordersTable.getColumnModel().getColumn(9).setPreferredWidth(100); 
        ordersTable.getColumnModel().getColumn(10).setPreferredWidth(80); 
        
        
        ordersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    try {
                        String status = (String) table.getValueAt(row, 10); 
                        
                        if (PurchaseOrder.STATUS_COMPLETED.equals(status)) {
                            c.setBackground(new Color(230, 255, 230)); 
                        } else if (PurchaseOrder.STATUS_CANCELLED.equals(status)) {
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
    
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
    
        JButton filterBtn = new JButton("Filter by: All Status ▼");
        styleButton(filterBtn);
        createFilterPopup(filterBtn);
        buttonsPanel.add(filterBtn);
        
        JButton detailsButton = new JButton("View Details");
        styleButton(detailsButton);
        detailsButton.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select an order to view details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                String orderId = String.valueOf(ordersTable.getValueAt(selectedRow, 0));
                String requisitionId = String.valueOf(ordersTable.getValueAt(selectedRow, 1));
                String itemCode = String.valueOf(ordersTable.getValueAt(selectedRow, 2));
                String itemName = String.valueOf(ordersTable.getValueAt(selectedRow, 3));
                int quantity = Integer.parseInt(String.valueOf(ordersTable.getValueAt(selectedRow, 4)));
                String unitPrice = String.valueOf(ordersTable.getValueAt(selectedRow, 5));
                String totalAmount = String.valueOf(ordersTable.getValueAt(selectedRow, 6));
                String orderDate = String.valueOf(ordersTable.getValueAt(selectedRow, 7));
                String expectedDelivery = String.valueOf(ordersTable.getValueAt(selectedRow, 8));
                String supplierId = String.valueOf(ordersTable.getValueAt(selectedRow, 9));
                String status = String.valueOf(ordersTable.getValueAt(selectedRow, 10));
                
                StringBuilder details = new StringBuilder();
                details.append("Order ID: ").append(orderId).append("\n\n");
                details.append("Requisition ID: ").append(requisitionId).append("\n");
                details.append("Item Code: ").append(itemCode).append("\n");
                details.append("Item Name: ").append(itemName).append("\n");
                details.append("Quantity: ").append(quantity).append("\n");
                details.append("Unit Price: ").append(unitPrice).append("\n");
                details.append("Total Amount: ").append(totalAmount).append("\n");
                details.append("Order Date: ").append(orderDate).append("\n");
                details.append("Expected Delivery: ").append(expectedDelivery).append("\n");
                details.append("Supplier ID: ").append(supplierId).append("\n");
                details.append("Status: ").append(status).append("\n");
                
                JTextArea textArea = new JTextArea(details.toString());
                textArea.setEditable(false);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
                textArea.setBackground(UIManager.getColor("Panel.background"));
                textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JScrollPane scrollPane2 = new JScrollPane(textArea);
                scrollPane2.setPreferredSize(new Dimension(400, 350));
                
                JOptionPane.showMessageDialog(this,
                    scrollPane2,
                    "Purchase Order Details",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error displaying details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(detailsButton);
        
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
    
        return contentPanel;
    }
    
    private void createFilterPopup(JButton filterBtn) {
        JPopupMenu filterMenu = new JPopupMenu();
        filterMenu.setBackground(Color.WHITE);
    
        String[] statuses = {
            "All Status",
            PurchaseOrder.STATUS_PENDING,
            PurchaseOrder.STATUS_COMPLETED,
            PurchaseOrder.STATUS_CANCELLED
        };
    
        for (String status : statuses) {
            JMenuItem item = new JMenuItem(status);
            item.setFont(new Font("SansSerif", Font.PLAIN, 14));
            item.setBackground(Color.WHITE);
            item.addActionListener(e -> {
                filterOrders(status.equals("All Status") ? null : status);
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
            DashboardPage dashboard = new DashboardPage(currentUser);
            dashboard.setVisible(true);
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User("ADMIN001", "admin", "password", "admin@example.com", User.ROLE_ADMINISTRATOR);
            PurchaseOrdersPage ordersPage = new PurchaseOrdersPage(testUser);
            ordersPage.setVisible(true);
        });
    }
}