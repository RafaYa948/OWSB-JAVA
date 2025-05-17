// sales/SalesDataEntryPage.java
package sales;

import admin.UIBase;
import database.DatabaseHelper;
import models.SalesEntry;
import models.User;
import models.SystemLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.LineBorder;

public class SalesDataEntryPage extends UIBase {

    private final User currentUser;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField dateField, itemCodeField, quantityField;

    public SalesDataEntryPage(User user) {
        super("Sales Data Entry");
        this.currentUser = user;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadSalesEntries();
        }
        super.setVisible(visible);
    }

    public void loadSalesEntries() {
        try {
            // Check if currentUser is null
            if (currentUser == null) {
                System.out.println("Warning: Current user is null in loadSalesEntries");
                return; // Don't try to load entries if user is null
            }

            DatabaseHelper dbHelper = new DatabaseHelper();
            // In a real implementation, we would load entries from the database
            List<SalesEntry> allEntries = new ArrayList<>();

            // Create some sample entries for demonstration
            allEntries.add(new SalesEntry("SALE001", LocalDate.now(), "ITEM001", "Office Chair", 5, "Furniture", 199.99, 999.95, currentUser.getUserId()));
            allEntries.add(new SalesEntry("SALE002", LocalDate.now().minusDays(1), "ITEM002", "Desk Lamp", 10, "Electronics", 49.99, 499.90, currentUser.getUserId()));

            updateTable(allEntries);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading sales entries: " + ex.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<SalesEntry> entries) {
        if (tableModel == null) {
            // Initialize the table model if it hasn't been created yet
            String[] columnNames = {"Date", "Item ID", "Item Name", "Quantity", "Category", "Price per unit", "Total price"};
            tableModel = new DefaultTableModel(columnNames, 0);
        }

        tableModel.setRowCount(0);

        if (currentUser == null || entries == null) {
            return; // Don't try to update the table if user or entries are null
        }

        for (SalesEntry entry : entries) {
            if (entry != null && entry.getSalesManagerId() != null &&
                    entry.getSalesManagerId().equals(currentUser.getUserId())) {

                Object[] rowData = {
                        entry.getDate(),
                        entry.getItemId(),
                        entry.getItemName() != null ? entry.getItemName() : "",
                        entry.getQuantity(),
                        entry.getCategory() != null ? entry.getCategory() : "",
                        entry.getPricePerUnit(),
                        entry.getTotalPrice()
                };
                tableModel.addRow(rowData);
            }
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

        JPanel salesEntryItem = createMenuItem("Sales Data Entry", true);

        menuPanel.add(dashboardItem);
        menuPanel.add(salesEntryItem);
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
                    SalesDataEntryPage.this,
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

        JLabel title = new JLabel("Sales Data Entry");
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

        // Use GridLayout for better alignment of form fields
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Enter Sales Data"));

        dateField = new JTextField(10);
        itemCodeField = new JTextField(10);
        quantityField = new JTextField(10);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Item Code:"));
        formPanel.add(itemCodeField);
        formPanel.add(new JLabel("Sales Quantity:"));
        formPanel.add(quantityField);

        // Add some padding around the form panel
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formContainer.add(formPanel, BorderLayout.CENTER);

        contentPanel.add(formContainer, BorderLayout.NORTH);

        // Rest of the method remains the same...
        String[] columnNames = {"Date", "Item ID", "Item Name", "Quantity", "Category", "Price per unit", "Total price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3 || column == 5 || column == 6) {
                    return Object.class;
                }
                return String.class;
            }
        };

        salesTable = new JTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getTableHeader().setBackground(new Color(240, 240, 240));
        salesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        salesTable.setRowHeight(30);
        salesTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton createButton = new JButton("Create Entry");
        styleButton(createButton);
        createButton.addActionListener(e -> handleCreateEntry());
        buttonsPanel.add(createButton);

        JButton editButton = new JButton("Edit Entry");
        styleButton(editButton);
        editButton.addActionListener(e -> handleEditEntry());
        buttonsPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Entry");
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> handleDeleteEntry());
        buttonsPanel.add(deleteButton);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton);
        saveButton.addActionListener(e -> handleSaveEntries());
        buttonsPanel.add(saveButton);

        JButton resetButton = new JButton("Reset");
        styleButton(resetButton);
        resetButton.addActionListener(e -> handleResetForm());
        buttonsPanel.add(resetButton);

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
        button.setPreferredSize(new Dimension(120, 40)); // Adjusted size for more buttons
    }


    private void handleCreateEntry() {
        String dateStr = dateField.getText().trim();
        String itemCode = itemCodeField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (dateStr.isEmpty() || itemCode.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date, Item Code, and Quantity are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr);
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be positive.");
            }

            // In a real system, you would fetch item details (name, price, category) from the database using itemCode
            // For this example, we'll use placeholders or simplified logic.
            DatabaseHelper dbHelper = new DatabaseHelper();
            String itemName = ""; // Fetch from item database
            String category = ""; // Determine based on item
            double pricePerUnit = 0.0; // Fetch from item database

            try {
                models.Item item = dbHelper.getItemByCode(itemCode);
                if (item != null) {
                    itemName = item.getItemName();
                    // You would need to add price and category to your Item model and item.txt file
                    // For now, using placeholder values
                    pricePerUnit = 10.0; // Placeholder
                    category = "General"; // Placeholder
                } else {
                    JOptionPane.showMessageDialog(this, "Item Code not found.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching item details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            double totalPrice = quantity * pricePerUnit;

            // Generate a unique entry ID. A more robust method might be needed.
            String entryId = "SALE" + System.currentTimeMillis();

            SalesEntry newEntry = new SalesEntry(entryId, date, itemCode, itemName, quantity, category, pricePerUnit, totalPrice, currentUser.getUserId());

            // Add to the table model. Saving to file happens with "Save".
            tableModel.addRow(new Object[]{
                    newEntry.getDate(),
                    newEntry.getItemId(),
                    newEntry.getItemName(),
                    newEntry.getQuantity(),
                    newEntry.getCategory(),
                    newEntry.getPricePerUnit(),
                    newEntry.getTotalPrice()
            });

            JOptionPane.showMessageDialog(this, "Entry added to table. Click 'Save' to save to file.", "Info", JOptionPane.INFORMATION_MESSAGE);
            logSystemAction(SystemLog.ACTION_CREATE, "Created new sales entry for item: " + itemCode);
            handleResetForm(); // Clear form after adding to table

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleEditEntry() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get data from the selected row
        Object dateObj = tableModel.getValueAt(selectedRow, 0);
        Object itemCodeObj = tableModel.getValueAt(selectedRow, 1);
        Object itemNameObj = tableModel.getValueAt(selectedRow, 2);
        Object quantityObj = tableModel.getValueAt(selectedRow, 3);
        Object categoryObj = tableModel.getValueAt(selectedRow, 4);
        Object pricePerUnitObj = tableModel.getValueAt(selectedRow, 5);
        Object totalPriceObj = tableModel.getValueAt(selectedRow, 6);


        // Populate a form or dialog for editing
        JTextField editDateField = new JTextField(dateObj != null ? dateObj.toString() : "");
        JTextField editItemCodeField = new JTextField(itemCodeObj != null ? itemCodeObj.toString() : "");
        JTextField editItemNameField = new JTextField(itemNameObj != null ? itemNameObj.toString() : "");
        JTextField editQuantityField = new JTextField(quantityObj != null ? quantityObj.toString() : "");
        JTextField editCategoryField = new JTextField(categoryObj != null ? categoryObj.toString() : "");
        JTextField editPricePerUnitField = new JTextField(pricePerUnitObj != null ? pricePerUnitObj.toString() : "");
        // Total price will be calculated


        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(editDateField);
        panel.add(new JLabel("Item Code:"));
        panel.add(editItemCodeField);
        panel.add(new JLabel("Item Name:"));
        panel.add(editItemNameField);
        panel.add(new JLabel("Sales Quantity:"));
        panel.add(editQuantityField);
        panel.add(new JLabel("Category:"));
        panel.add(editCategoryField);
        panel.add(new JLabel("Price per unit:"));
        panel.add(editPricePerUnitField);


        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Sales Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Get updated values from fields
                String newDateStr = editDateField.getText().trim();
                String newItemCode = editItemCodeField.getText().trim();
                String newItemName = editItemNameField.getText().trim();
                String newQuantityStr = editQuantityField.getText().trim();
                String newCategory = editCategoryField.getText().trim();
                String newPricePerUnitStr = editPricePerUnitField.getText().trim();

                if (newDateStr.isEmpty() || newItemCode.isEmpty() || newQuantityStr.isEmpty() || newItemName.isEmpty() || newCategory.isEmpty() || newPricePerUnitStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate newDate = LocalDate.parse(newDateStr);
                int newQuantity = Integer.parseInt(newQuantityStr);
                double newPricePerUnit = Double.parseDouble(newPricePerUnitStr);
                double newTotalPrice = newQuantity * newPricePerUnit;

                // Update the table model directly
                tableModel.setValueAt(newDate, selectedRow, 0);
                tableModel.setValueAt(newItemCode, selectedRow, 1);
                tableModel.setValueAt(newItemName, selectedRow, 2);
                tableModel.setValueAt(newQuantity, selectedRow, 3);
                tableModel.setValueAt(newCategory, selectedRow, 4);
                tableModel.setValueAt(newPricePerUnit, selectedRow, 5);
                tableModel.setValueAt(newTotalPrice, selectedRow, 6);


                JOptionPane.showMessageDialog(this, "Entry updated in table. Click 'Save' to save to file.", "Info", JOptionPane.INFORMATION_MESSAGE);
                logSystemAction(SystemLog.ACTION_UPDATE, "Edited sales entry for item: " + newItemCode);

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for quantity or price.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleDeleteEntry() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this sales entry?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // In a real application, you would also need to delete this from the data file.
            // For this example, we are only removing it from the table model.
            String itemId = (String) tableModel.getValueAt(selectedRow, 1); // Get item ID before deleting
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Entry deleted from table. Click 'Save' to update file.", "Success", JOptionPane.INFORMATION_MESSAGE);
            logSystemAction(SystemLog.ACTION_DELETE, "Deleted sales entry for item: " + itemId);
        }
    }

    private void handleSaveEntries() {
        // This method should iterate through the table model,
        // reconstruct the list of SalesEntry objects, and write them back to the sales_entry.txt file.

        // Placeholder: Since DatabaseHelper doesn't support writing SalesEntry yet.
        JOptionPane.showMessageDialog(this, "Save functionality for Sales Entries is not fully implemented in DatabaseHelper.", "Information", JOptionPane.INFORMATION_MESSAGE);
        logSystemAction(SystemLog.ACTION_UPDATE, "Attempted to save sales entries");
    }


    private void handleResetForm() {
        dateField.setText("");
        itemCodeField.setText("");
        quantityField.setText("");
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