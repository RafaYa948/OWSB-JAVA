package sales;

import admin.UIBase;
import database.DatabaseHelper;
import models.Supplier;
import models.User;
import models.SystemLog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ManageSuppliersPage extends UIBase {

    private final User currentUser;
    private JTable suppliersTable;
    private DefaultTableModel tableModel;
    private List<Supplier> suppliersList;

    public ManageSuppliersPage(User user) {
        super("Manage Suppliers");
        this.currentUser = user;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadSuppliers();
        }
        super.setVisible(visible);
    }

    public void loadSuppliers() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            suppliersList = dbHelper.getAllSuppliers();

            if (suppliersList == null) {
                suppliersList = new ArrayList<>();
            } else {
                filterSuppliers(null);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            suppliersList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Error loading suppliers: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterSuppliers(String status) {
        tableModel.setRowCount(0);
        for (Supplier supplier : suppliersList) {
            if (supplier != null && (status == null || supplier.getStatus().equals(status))) {
                Object[] rowData = {
                        supplier.getSupplierId(),
                        supplier.getSupplierName(),
                        supplier.getContactPerson(),
                        supplier.getContactNumber(),
                        supplier.getEmail(),
                        supplier.getSuppliedItems(),
                        supplier.getLastOrderDate(),
                        supplier.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    @Override
    protected void initUI() {
        suppliersList = new ArrayList<>();

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
        placeholder.setForeground(primaryColor);
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

        JPanel suppliersItem = createMenuItem("Manage Suppliers", true);

        menuPanel.add(dashboardItem);
        menuPanel.add(suppliersItem);
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
                    ManageSuppliersPage.this,
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

        JLabel title = new JLabel("Manage Suppliers");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(primaryColor);

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

        String[] columnNames = {
                "Supplier ID", "Name", "Contact Person", "Contact Number",
                "Email", "Supplied Items", "Last Order Date", "Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        suppliersTable = new JTable(tableModel);
        suppliersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suppliersTable.getTableHeader().setBackground(new Color(240, 240, 240));
        suppliersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        suppliersTable.setRowHeight(30);
        suppliersTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(suppliersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton filterBtn = new JButton("Filter by: All Status ▼");
        styleButton(filterBtn);
        createFilterPopup(filterBtn);
        buttonsPanel.add(filterBtn);

        JButton addButton = new JButton("Add Supplier");
        styleButton(addButton);
        addButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to add a new supplier?",
                    "Add Supplier Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                // Handle the add supplier action
                handleAddSupplier();
            }
        });
        buttonsPanel.add(addButton);

        JButton editButton = new JButton("Edit Supplier");
        styleButton(editButton);
        editButton.addActionListener(e -> {
            int selectedRow = suppliersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a supplier to edit.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String supplierId = (String) suppliersTable.getValueAt(selectedRow, 0);
            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                Supplier supplier = dbHelper.getSupplierById(supplierId);
                if (supplier != null) {
                    // Handle the edit supplier action
                    handleEditSupplier(supplier);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading supplier: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Supplier");
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> {
            int selectedRow = suppliersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a supplier to delete.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String supplierId = (String) suppliersTable.getValueAt(selectedRow, 0);
            String supplierName = (String) suppliersTable.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete supplier '" + supplierName + "'?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper();
                    dbHelper.deleteSupplier(supplierId);
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this,
                            "Supplier deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadSuppliers();
                    logSystemAction(SystemLog.ACTION_DELETE, "Deleted supplier: " + supplierName);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting supplier: " + ex.getMessage(),
                            "Delete Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonsPanel.add(deleteButton);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        return contentPanel;
    }

    private void createFilterPopup(JButton filterBtn) {
        JPopupMenu filterMenu = new JPopupMenu();
        filterMenu.setBackground(Color.WHITE);

        String[] statuses = {
                "All Status",
                Supplier.STATUS_ACTIVE,
                Supplier.STATUS_INACTIVE
        };

        for (String status : statuses) {
            JMenuItem item = new JMenuItem(status);
            item.setFont(new Font("SansSerif", Font.PLAIN, 14));
            item.setBackground(Color.WHITE);
            item.addActionListener(e -> {
                filterSuppliers(status.equals("All Status") ? null : status);
                filterBtn.setText("Filter by: " + status + " ▼");
            });
            filterMenu.add(item);
        }

        filterBtn.addActionListener(e -> filterMenu.show(filterBtn, 0, filterBtn.getHeight()));
    }

    // Fixed method to handle adding a supplier with the correct parameters
    private void handleAddSupplier() {
        // Create a form panel with fields for supplier details
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField supplierNameField = new JTextField();
        JTextField contactPersonField = new JTextField();
        JTextField contactNumberField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField(); // Added address field
        JTextField suppliedItemsField = new JTextField();
        JTextField lastOrderDateField = new JTextField();

        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                Supplier.STATUS_ACTIVE,
                Supplier.STATUS_INACTIVE
        });

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(supplierNameField);
        formPanel.add(new JLabel("Contact Person:"));
        formPanel.add(contactPersonField);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(contactNumberField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Address:")); // Added address label
        formPanel.add(addressField); // Added address field
        formPanel.add(new JLabel("Supplied Items:"));
        formPanel.add(suppliedItemsField);
        formPanel.add(new JLabel("Last Order Date (YYYY-MM-DD):"));
        formPanel.add(lastOrderDateField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Add Supplier",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            // Validate input
            if (supplierNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Supplier Name is required.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate date format if provided
            LocalDate lastOrderDate = null;
            String lastOrderDateStr = lastOrderDateField.getText().trim();
            if (!lastOrderDateStr.isEmpty()) {
                try {
                    lastOrderDate = LocalDate.parse(lastOrderDateStr);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format. Use YYYY-MM-DD.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Generate a unique supplier ID
            String supplierId = "SUP" + System.currentTimeMillis();

            // Create the supplier object with correct parameter count
            Supplier newSupplier = new Supplier(
                    supplierId,
                    supplierNameField.getText().trim(),
                    contactPersonField.getText().trim(),
                    contactNumberField.getText().trim(),
                    emailField.getText().trim(),
                    addressField.getText().trim(), // Added address
                    suppliedItemsField.getText().trim(),
                    lastOrderDate,
                    (String) statusComboBox.getSelectedItem()
            );

            // Add the supplier to the database
            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                dbHelper.addSupplier(newSupplier);

                // Reload suppliers to refresh the table
                loadSuppliers();

                JOptionPane.showMessageDialog(this,
                        "Supplier added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                logSystemAction(SystemLog.ACTION_CREATE, "Added new supplier: " + newSupplier.getSupplierName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error adding supplier: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Fixed method to handle editing a supplier with the correct parameters
    private void handleEditSupplier(Supplier supplier) {
        // Create a form panel with fields pre-filled with supplier details
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField supplierNameField = new JTextField(supplier.getSupplierName());
        JTextField contactPersonField = new JTextField(supplier.getContactPerson());
        JTextField contactNumberField = new JTextField(supplier.getContactNumber());
        JTextField emailField = new JTextField(supplier.getEmail());
        JTextField addressField = new JTextField(supplier.getAddress()); // Added address field
        JTextField suppliedItemsField = new JTextField(supplier.getSuppliedItems());

        String lastOrderDateStr = supplier.getLastOrderDate() != null
                ? supplier.getLastOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : "";

        JTextField lastOrderDateField = new JTextField(lastOrderDateStr);

        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                Supplier.STATUS_ACTIVE,
                Supplier.STATUS_INACTIVE
        });
        statusComboBox.setSelectedItem(supplier.getStatus());

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(supplierNameField);
        formPanel.add(new JLabel("Contact Person:"));
        formPanel.add(contactPersonField);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(contactNumberField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Address:")); // Added address label
        formPanel.add(addressField); // Added address field
        formPanel.add(new JLabel("Supplied Items:"));
        formPanel.add(suppliedItemsField);
        formPanel.add(new JLabel("Last Order Date (YYYY-MM-DD):"));
        formPanel.add(lastOrderDateField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Edit Supplier",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            // Validate input
            if (supplierNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Supplier Name is required.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate date format if provided
            LocalDate lastOrderDate = null;
            String newLastOrderDateStr = lastOrderDateField.getText().trim();
            if (!newLastOrderDateStr.isEmpty()) {
                try {
                    lastOrderDate = LocalDate.parse(newLastOrderDateStr);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format. Use YYYY-MM-DD.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Update the supplier object
            supplier.setSupplierName(supplierNameField.getText().trim());
            supplier.setContactPerson(contactPersonField.getText().trim());
            supplier.setContactNumber(contactNumberField.getText().trim());
            supplier.setEmail(emailField.getText().trim());
            supplier.setAddress(addressField.getText().trim()); // Added address update
            supplier.setSuppliedItems(suppliedItemsField.getText().trim());
            supplier.setLastOrderDate(lastOrderDate);
            supplier.setStatus((String) statusComboBox.getSelectedItem());

            // Update the supplier in the database
            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                dbHelper.updateSupplier(supplier);

                // Reload suppliers to refresh the table
                loadSuppliers();

                JOptionPane.showMessageDialog(this,
                        "Supplier updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                logSystemAction(SystemLog.ACTION_UPDATE, "Updated supplier: " + supplier.getSupplierName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error updating supplier: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
        SwingUtilities.invokeLater(() -> new SalesDashboardPage(currentUser).setVisible(true));
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