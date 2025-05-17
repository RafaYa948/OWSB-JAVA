// sales/CreateRequisitionPage.java
package sales;

import admin.UIBase;
import database.DatabaseHelper;
import models.PurchaseRequisition;
import models.User;
import models.Item; // Assuming Item model is needed to fetch supplier ID
import models.SystemLog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class CreateRequisitionPage extends UIBase {

    private final User currentUser;
    private JTextField itemCodeField, quantityField, deliveryDateField, supplierCodeField;

    public CreateRequisitionPage(User user) {
        super("Create Purchase Requisition");
        this.currentUser = user;
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

        JPanel createRequisitionItem = createMenuItem("Create Requisition", true);

        menuPanel.add(dashboardItem);
        menuPanel.add(createRequisitionItem);
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
                    CreateRequisitionPage.this,
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

        JLabel title = new JLabel("Create Purchase Requisition");
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
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.weightx = 1.0;

        JLabel itemCodeLabel = new JLabel("Item Code:");
        styleLabel(itemCodeLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(itemCodeLabel, gbc);

        itemCodeField = new JTextField(20);
        styleTextField(itemCodeField);
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(itemCodeField, gbc);

        JLabel quantityLabel = new JLabel("Required Quantity:");
        styleLabel(quantityLabel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(quantityLabel, gbc);

        quantityField = new JTextField(20);
        styleTextField(quantityField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(quantityField, gbc);

        JLabel deliveryDateLabel = new JLabel("Delivery Date (YYYY-MM-DD):");
        styleLabel(deliveryDateLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(deliveryDateLabel, gbc);

        deliveryDateField = new JTextField(20);
        styleTextField(deliveryDateField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        contentPanel.add(deliveryDateField, gbc);

        JLabel supplierCodeLabel = new JLabel("Supplier Code:");
        styleLabel(supplierCodeLabel);
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(supplierCodeLabel, gbc);

        supplierCodeField = new JTextField(20);
        styleTextField(supplierCodeField);
        supplierCodeField.setEditable(false); // Supplier Code will be auto-populated
        gbc.gridx = 1;
        gbc.gridy = 3;
        contentPanel.add(supplierCodeField, gbc);

        // Add action listener to itemCodeField to auto-populate supplierCodeField
        itemCodeField.addActionListener(e -> populateSupplierCode());


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton);
        saveButton.addActionListener(e -> handleSaveRequisition());
        buttonPanel.add(saveButton);

        JButton resetButton = new JButton("Reset");
        styleButton(resetButton);
        resetButton.addActionListener(e -> handleResetForm());
        buttonPanel.add(resetButton);


        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(buttonPanel, gbc);

        return contentPanel;
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(120, 120, 120));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
    }


    private void populateSupplierCode() {
        String itemCode = itemCodeField.getText().trim();
        if (itemCode.isEmpty()) {
            supplierCodeField.setText("");
            return;
        }

        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            Item item = dbHelper.getItemByCode(itemCode);
            if (item != null && item.getSupplierId() != null) {
                supplierCodeField.setText(item.getSupplierId());
            } else {
                supplierCodeField.setText("Supplier not found for this item");
            }
        } catch (IOException ex) {
            supplierCodeField.setText("Error fetching supplier");
            ex.printStackTrace();
        }
    }

    private void handleSaveRequisition() {
        String itemCode = itemCodeField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String deliveryDateStr = deliveryDateField.getText().trim();
        String supplierCode = supplierCodeField.getText().trim();

        if (itemCode.isEmpty() || quantityStr.isEmpty() || deliveryDateStr.isEmpty() || supplierCode.isEmpty() || supplierCode.equals("Supplier not found for this item") || supplierCode.equals("Error fetching supplier")) {
            JOptionPane.showMessageDialog(this, "All fields are required and valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be positive.");
            }
            LocalDate requiredDate = LocalDate.parse(deliveryDateStr);

            // Generate a unique requisition ID. A more robust method might be needed.
            String requisitionId = "REQ" + System.currentTimeMillis();

            PurchaseRequisition newRequisition = new PurchaseRequisition(
                    requisitionId,
                    itemCode,
                    quantity,
                    requiredDate,
                    currentUser.getUserId(),
                    PurchaseRequisition.STATUS_PENDING // New requisitions start as Pending
            );

            // Attempt to fetch item name for completeness, though not strictly required by the model constructor used here
            DatabaseHelper dbHelper = new DatabaseHelper();
            try {
                Item item = dbHelper.getItemByCode(itemCode);
                if (item != null) {
                    newRequisition.setItemName(item.getItemName());
                }
            } catch (IOException ex) {
                System.out.println("Could not fetch item name for requisition: " + ex.getMessage());
                newRequisition.setItemName("Unknown Item"); // Set a default or handle as appropriate
            }


            dbHelper.addPurchaseRequisition(newRequisition);
            JOptionPane.showMessageDialog(this, "Purchase Requisition created successfully!\nID: " + requisitionId, "Success", JOptionPane.INFORMATION_MESSAGE);
            logSystemAction(SystemLog.ACTION_CREATE, "Created purchase requisition: " + requisitionId);
            handleResetForm(); // Clear form after saving

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid delivery date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error saving requisition: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void handleResetForm() {
        itemCodeField.setText("");
        quantityField.setText("");
        deliveryDateField.setText("");
        supplierCodeField.setText("");
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