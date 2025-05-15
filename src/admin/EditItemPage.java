package admin;

import models.Item;
import models.User;
import database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;

public class EditItemPage extends UIBase {

    private final User currentUser;
    private final ManageItemsPage parentPage;
    private Item itemToEdit;
    private JTextField[] fields;

    public EditItemPage(User currentUser, ManageItemsPage parentPage, Item itemToEdit) {
        super("Edit Item");
        this.currentUser = currentUser;
        this.parentPage = parentPage;
        
        
        try {
            if (itemToEdit != null) {
                DatabaseHelper dbHelper = new DatabaseHelper();
                Item freshItem = dbHelper.getItemByCode(itemToEdit.getItemCode());
                
                
                this.itemToEdit = (freshItem != null) ? freshItem : itemToEdit;
            } else {
                this.itemToEdit = itemToEdit;
            }
        } catch (Exception e) {
            
            this.itemToEdit = itemToEdit;
            System.out.println("Warning: Could not load fresh item data: " + e.getMessage());
        }
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setForeground(new Color(11, 61, 145));
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose();
            parentPage.setVisible(true);
        });

        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Edit Item");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(11, 61, 145));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JScrollPane formPanel = createFormPanel();

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JScrollPane createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.weightx = 1.0;

        String[] labels = {"Item Code", "Item Name", "Supplier ID"};
        fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i;

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(new Color(60, 60, 60));
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 2;

            JTextField field = new JTextField();
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            field.setPreferredSize(new Dimension(300, 35));
            fields[i] = field;
            formPanel.add(field, gbc);
        }

        
        if (itemToEdit != null) {
            fields[0].setText(itemToEdit.getItemCode());
            fields[0].setEditable(false); 
            fields[1].setText(itemToEdit.getItemName());
            fields[2].setText(itemToEdit.getSupplierId());
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton submitButton = new JButton("Update Item");
        styleButton(submitButton, new Color(11, 61, 145));
        submitButton.addActionListener(e -> handleSubmit());

        JButton resetButton = new JButton("Reset");
        styleButton(resetButton, new Color(120, 120, 120));
        resetButton.addActionListener(e -> handleReset());

        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);

        return new JScrollPane(formPanel);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void handleSubmit() {
        try {
            String itemCode = fields[0].getText().trim();
            String itemName = fields[1].getText().trim();
            String supplierId = fields[2].getText().trim();

            if (itemCode.isEmpty() || itemName.isEmpty() || supplierId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "All fields are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper();
            Item updatedItem = new Item(itemCode, itemName, supplierId);
            dbHelper.updateItem(updatedItem);

            JOptionPane.showMessageDialog(this,
                "Item updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            parentPage.loadItems();
            dispose();
            parentPage.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Update Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReset() {
        if (itemToEdit != null) {
            fields[0].setText(itemToEdit.getItemCode());
            fields[1].setText(itemToEdit.getItemName());
            fields[2].setText(itemToEdit.getSupplierId());
        }
    }
}