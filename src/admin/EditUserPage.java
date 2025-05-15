package admin;

import models.User;
import database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;

public class EditUserPage extends UIBase {

    private User currentUser;
    private ManageUsersPage parentPage;
    private User userToEdit;
    private JTextField[] fields;
    private JComboBox<String> roleComboBox;

    public EditUserPage(User currentUser, ManageUsersPage parentPage, User userToEdit) {
        super("Update User Information");
        
        this.currentUser = currentUser;
        this.parentPage = parentPage;
        
        
        try {
            if (userToEdit != null) {
                DatabaseHelper dbHelper = new DatabaseHelper();
                User freshUser = dbHelper.getUserById(userToEdit.getUserId());
                
                
                this.userToEdit = (freshUser != null) ? freshUser : userToEdit;
            } else {
                this.userToEdit = userToEdit;
            }
        } catch (Exception e) {
            
            this.userToEdit = userToEdit;
            System.out.println("Warning: Could not load fresh user data: " + e.getMessage());
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
            if (parentPage != null) {
                parentPage.setVisible(true);
            }
        });

        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Update User Information");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(11, 61, 145));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        
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

        String[] labels = {"User ID", "Name", "Email", "Password", "Role"};
        fields = new JTextField[4];
        
        
        String[] roles = {
            User.ROLE_ADMINISTRATOR,
            User.ROLE_INVENTORY_MANAGER,
            User.ROLE_PURCHASE_MANAGER,
            User.ROLE_FINANCE_MANAGER,
            User.ROLE_SALES_MANAGER
        };

        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setPreferredSize(new Dimension(300, 35));
        
        
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

            if (i == 4) {
                formPanel.add(roleComboBox, gbc);
            } else {
                JTextField field;
                if (i == 3) {
                    field = new JPasswordField();
                } else {
                    field = new JTextField();
                }
                field.setFont(new Font("SansSerif", Font.PLAIN, 14));
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                field.setPreferredSize(new Dimension(300, 35));
                fields[i] = field;
                formPanel.add(field, gbc);
            }
        }

        
        if (userToEdit != null) {
            fields[0].setEditable(false);
            fields[0].setText(userToEdit.getUserId());
            fields[1].setText(userToEdit.getUsername());
            fields[2].setText(userToEdit.getEmail());
            fields[3].setText(userToEdit.getPassword());
            roleComboBox.setSelectedItem(userToEdit.getRole());
        }

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton submitButton = new JButton("Update");
        submitButton.setBackground(new Color(11, 61, 145));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setPreferredSize(new Dimension(150, 40));
        submitButton.addActionListener(e -> {
            try {
                String username = fields[1].getText().trim();
                String email = fields[2].getText().trim();
                String password = fields[3] instanceof JPasswordField
                        ? new String(((JPasswordField) fields[3]).getPassword())
                        : fields[3].getText();
                String role = (String) roleComboBox.getSelectedItem();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
                    JOptionPane.showMessageDialog(this,
                            "All fields are required.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper();
                User updatedUser = new User(userToEdit.getUserId(), username, password, email, role);
                dbHelper.updateUser(updatedUser);

                JOptionPane.showMessageDialog(this,
                        "User updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                if (parentPage != null) {
                    parentPage.loadUsers();
                    dispose();
                    parentPage.setVisible(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(120, 120, 120));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setPreferredSize(new Dimension(150, 40));
        resetButton.addActionListener(e -> {
            if (userToEdit != null) {
                fields[1].setText(userToEdit.getUsername());
                fields[2].setText(userToEdit.getEmail());
                fields[3].setText(userToEdit.getPassword());
                roleComboBox.setSelectedItem(userToEdit.getRole());
            }
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);

        
        root.add(headerPanel, BorderLayout.NORTH);
        root.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        
        setContentPane(root);
        
        
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
    }
}