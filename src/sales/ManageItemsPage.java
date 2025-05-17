// sales/ManageItemsPage.java
package sales;

import database.DatabaseHelper;
import models.Item;
import models.User;
import models.SystemLog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ManageItemsPage extends UIBase {

    private final User currentUser;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private List<Item> itemsList;

    public ManageItemsPage(User user) {
        super("Manage Items");
        this.currentUser = user;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadItems();
        }
        super.setVisible(visible);
    }

    public void loadItems() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            itemsList = dbHelper.getAllItems();

            if (itemsList == null) {
                itemsList = new ArrayList<>();
            }
            updateTable(itemsList);

        } catch (IOException ex) {
            ex.printStackTrace();
            itemsList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Error loading items: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            itemsList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Item> items) {
        tableModel.setRowCount(0);
        for (Item item : items) {
            if (item != null) {
                // Note: Stock Quantity and Price per unit are not in the Item model
                // and are not available in the provided data files.
                // Assuming these columns might be populated from other sources or are placeholders for this assignment.
                // For now, they are left blank.
                Object[] rowData = {
                        item.getItemCode(),
                        item.getItemName(),
                        item.getSupplierId(),
                        "", // Stock Quantity (Placeholder)
                        ""  // Price per unit (Placeholder)
                };
                tableModel.addRow(rowData);
            }
        }
    }

    @Override
    protected void initUI() {
        itemsList = new ArrayList<>();

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

        JPanel manageItemsItem = createMenuItem("Manage Items", true);

        menuPanel.add(dashboardItem);
        menuPanel.add(manageItemsItem);
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
                    ManageItemsPage.this,
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

        JLabel title = new JLabel("Manage Items");
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

        String[] columnNames = {"Item Code", "Item Name", "Supplier ID", "Stock Quantity", "Price per unit"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only Stock Quantity and Price per unit editable if needed for Sales role
                // Based on assignment, Sales Manager can Add/Save/Delete/Edit.
                // We will allow editing for these fields if data is available.
                // For simplicity, assuming direct edit on table is intended.
                return column == 3 || column == 4;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3 || column == 4) { // Stock Quantity and Price per unit
                    return Object.class; // Use Object for flexibility with empty strings or numbers
                }
                return String.class;
            }
        };


        itemsTable = new JTable(tableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        itemsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        itemsTable.setRowHeight(30);
        itemsTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton addButton = new JButton("Add Item");
        styleButton(addButton);
        addButton.addActionListener(e -> handleAddItem());
        buttonsPanel.add(addButton);

        JButton editButton = new JButton("Edit Item");
        styleButton(editButton);
        editButton.addActionListener(e -> handleEditItem());
        buttonsPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Item");
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> handleDeleteItem());
        buttonsPanel.add(deleteButton);

        JButton saveButton = new JButton("Save Changes");
        styleButton(saveButton);
        saveButton.addActionListener(e -> handleSaveChanges());
        buttonsPanel.add(saveButton);


        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(120, 120, 120));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void goBackToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            SalesDashboardPage dashboard = new SalesDashboardPage(currentUser);
            dashboard.setVisible(true);
        });
    }

    private void handleAddItem() {
        // For sales role, adding might be a simpler form or integrated differently.
        // Based on the admin AddItemPage, a separate page is used.
        // Implementing a simple input dialog for now, a dedicated page class would be better.
        JTextField itemNameField = new JTextField();
        JTextField supplierIdField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText().trim();
            String supplierId = supplierIdField.getText().trim();

            if (itemName.isEmpty() || supplierId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Item Name and Supplier ID cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                // Generate a simple item code for the new item. A more robust method might be needed.
                String newItemCode = "ITEM" + (dbHelper.getAllItems().size() + 1);
                Item newItem = new Item(newItemCode, itemName, supplierId);
                dbHelper.addItem(newItem);
                JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logSystemAction(SystemLog.ACTION_CREATE, "Added new item: " + newItemCode);
                loadItems(); // Refresh the table
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEditItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemCode = (String) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 1);
        String supplierId = (String) tableModel.getValueAt(selectedRow, 2);
        // You might also get Stock Quantity and Price per unit if available

        JTextField itemNameField = new JTextField(itemName);
        JTextField supplierIdField = new JTextField(supplierId);
        // Add fields for Stock Quantity and Price per unit if applicable

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Item Code: " + itemCode)); // Show Item Code but not editable
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierIdField);
        // Add labels and fields for Stock Quantity and Price per unit

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newItemName = itemNameField.getText().trim();
            String newSupplierId = supplierIdField.getText().trim();
            // Get updated Stock Quantity and Price per unit

            if (newItemName.isEmpty() || newSupplierId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Item Name and Supplier ID cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                Item updatedItem = new Item(itemCode, newItemName, newSupplierId);
                // Set Stock Quantity and Price per unit on the updatedItem object if applicable
                dbHelper.updateItem(updatedItem);
                JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logSystemAction(SystemLog.ACTION_UPDATE, "Updated item: " + itemCode);
                loadItems(); // Refresh the table
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemCode = (String) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete item '" + itemName + "' (" + itemCode + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                dbHelper.deleteItem(itemCode);

                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this,
                        "Item deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                logSystemAction(SystemLog.ACTION_DELETE, "Deleted item: " + itemCode);
                loadItems(); // Refresh the list
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting item: " + ex.getMessage(),
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSaveChanges() {
        // This method would typically iterate through the table model
        // to get any edited data (e.g., Stock Quantity, Price per unit)
        // and save it back to the database or relevant data file.
        // Since Stock Quantity and Price per unit are placeholders for now,
        // this implementation is basic.

        // Example: Assuming the user can edit Stock Quantity (column 3) and Price per unit (column 4)
        // You would need to fetch the corresponding Item object to update it fully.

        JOptionPane.showMessageDialog(this, "Save Changes functionality to be implemented.", "Information", JOptionPane.INFORMATION_MESSAGE);
        logSystemAction(SystemLog.ACTION_UPDATE, "Attempted to save item changes");
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