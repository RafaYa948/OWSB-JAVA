package sales;

import admin.UIBase;
import database.DatabaseHelper;
import models.PurchaseRequisition;
import models.User;
import models.SystemLog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ViewPurchaseRequisitionsPage extends UIBase {

    private final User currentUser;
    private JTable requisitionsTable;
    private DefaultTableModel tableModel;
    private List<PurchaseRequisition> requisitionsList;
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ViewPurchaseRequisitionsPage(User user) {
        super("View Purchase Requisitions");
        this.currentUser = user;
        requisitionsList = new ArrayList<>();
        initTableModel();
    }

    private void initTableModel() {
        String[] columnNames = {"PR ID", "Item Code", "Item Name", "Quantity", "Required Date", "Status"};
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
            }

            System.out.println("Loaded " + requisitionsList.size() + " requisitions");

            if (currentUser != null) {
                System.out.println("Current user: " + currentUser.getUsername() + ", ID: " + currentUser.getUserId() + ", Role: " + currentUser.getRole());
            } else {
                System.out.println("Current user is null");
            }

            filterRequisitions(null);

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
        if (tableModel == null) {
            initTableModel();
        }

        tableModel.setRowCount(0);

        if (requisitionsList == null) {
            return;
        }

        System.out.println("Loading all requisitions (ignoring filter) - list size: " + requisitionsList.size());

        // Show all requisitions regardless of status
        for (PurchaseRequisition requisition : requisitionsList) {
            if (requisition != null) {
                try {
                    // Get item name if not set
                    String itemName = requisition.getItemName();
                    if (itemName == null || itemName.isEmpty()) {
                        try {
                            DatabaseHelper db = new DatabaseHelper();
                            models.Item item = db.getItemByCode(requisition.getItemCode());
                            if (item != null) {
                                itemName = item.getItemName();
                            } else {
                                itemName = ""; // Default if item not found
                            }
                        } catch (Exception e) {
                            itemName = ""; // Default on error
                        }
                    }

                    // Format date safely
                    String dateStr = "";
                    LocalDate requiredDate = requisition.getRequiredDate();
                    if (requiredDate != null) {
                        try {
                            dateStr = requiredDate.format(displayDateFormatter);
                        } catch (Exception e) {
                            // If formatting fails, use toString() as fallback
                            dateStr = requiredDate.toString();
                        }
                    }

                    Object[] rowData = {
                            requisition.getRequisitionId(),
                            requisition.getItemCode(),
                            itemName,
                            requisition.getQuantity(),
                            dateStr,
                            requisition.getStatus()
                    };
                    tableModel.addRow(rowData);

                    System.out.println("Added requisition: " + requisition.getRequisitionId());
                } catch (Exception e) {
                    System.out.println("Error adding row for requisition: " +
                            (requisition.getRequisitionId() != null ? requisition.getRequisitionId() : "unknown") +
                            " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Table now has " + tableModel.getRowCount() + " rows");

        // Force table to refresh
        if (requisitionsTable != null) {
            requisitionsTable.revalidate();
            requisitionsTable.repaint();
        }
    }

    @Override
    protected void initUI() {
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

        JPanel requisitionsItem = createMenuItem("View Requisitions", true);

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
            int response = JOptionPane.showConfirmDialog(
                    ViewPurchaseRequisitionsPage.this,
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
                : "User";

        JLabel userLabel = new JLabel(displayName + " ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);

        userLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
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

        JLabel title = new JLabel("View Purchase Requisitions");
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

        // Make sure we have a table model
        if (tableModel == null) {
            initTableModel();
        }

        // Create table with the model
        requisitionsTable = new JTable(tableModel);
        requisitionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requisitionsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        requisitionsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        requisitionsTable.setRowHeight(30);
        requisitionsTable.setGridColor(Color.LIGHT_GRAY);

        // Set column widths to ensure visibility
        requisitionsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // PR ID
        requisitionsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Item Code
        requisitionsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Item Name
        requisitionsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Quantity
        requisitionsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Required Date
        requisitionsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status

        // Add color coding for different status values
        requisitionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    try {
                        if (column == 5) { // Status column
                            String status = value != null ? value.toString() : "";
                            if ("Approved".equals(status)) {
                                c.setBackground(new Color(230, 255, 230)); // Light green
                                c.setForeground(new Color(0, 100, 0));     // Dark green
                            } else if ("Rejected".equals(status)) {
                                c.setBackground(new Color(255, 230, 230)); // Light red
                                c.setForeground(new Color(180, 0, 0));     // Dark red
                            } else { // Pending
                                c.setBackground(new Color(255, 255, 230)); // Light yellow
                                c.setForeground(new Color(180, 100, 0));   // Orange/brown
                            }
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    } catch (Exception e) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        // Put the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(requisitionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.setPreferredSize(new Dimension(600, 400)); // Set preferred size
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // No filter button or button panel

        return contentPanel;
    }


    private void styleButton(JButton button) {
        button.setBackground(new Color(120, 120, 120));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
    }

    private void goBackToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            SalesDashboardPage dashboard = new SalesDashboardPage(currentUser);
            dashboard.setVisible(true);
        });
    }

    private void logSystemAction(String action, String details) {
        try {
            if (currentUser == null) return;

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