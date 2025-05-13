import database.DatabaseHelper;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import models.User;

public class RegisterPage extends UIBase {
    public static final String TITLE = "User Registration";
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private final DatabaseHelper dbHelper;

    public RegisterPage() {
        super(TITLE); // Pass title to UIBase
        dbHelper = new DatabaseHelper();
    }

    @Override
    protected void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBackground(Color.WHITE);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Consistent padding

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel(TITLE, SwingConstants.CENTER);
        titleLabel.setFont(headerFont); // Use UIBase font
        titleLabel.setForeground(primaryColor); // Use UIBase color
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0)); // Margin below header
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        
        // --- CENTRAL FORM CONTAINER ---
        JPanel formContainerPanel = new JPanel(new GridBagLayout());
        formContainerPanel.setBackground(Color.WHITE);
        rootPanel.add(formContainerPanel, BorderLayout.CENTER);

        // --- ACTUAL FORM PANEL ---
        JPanel actualFormPanel = new JPanel(new GridBagLayout());
        actualFormPanel.setBackground(panelColor); // Use UIBase color
        actualFormPanel.setBorder(formPanelBorder); // Use UIBase border

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8); // Consistent insets
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2; // Label column weight
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(inputFont); // Use UIBase font
        actualFormPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8; // Field column weight
        usernameField = new JTextField(20);
        usernameField.setFont(inputFont);
        usernameField.setBorder(INPUT_FIELD_BORDER); // Use UIBase border
        actualFormPanel.add(usernameField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(inputFont);
        actualFormPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(inputFont);
        passwordField.setBorder(INPUT_FIELD_BORDER);
        actualFormPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(inputFont);
        actualFormPanel.add(confirmPasswordLabel, gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(inputFont);
        confirmPasswordField.setBorder(INPUT_FIELD_BORDER);
        actualFormPanel.add(confirmPasswordField, gbc);

        // Email
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(inputFont);
        actualFormPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(inputFont);
        emailField.setBorder(INPUT_FIELD_BORDER);
        actualFormPanel.add(emailField, gbc);

        // Role
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(inputFont);
        actualFormPanel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        String[] roles = {
            User.ROLE_INVENTORY_MANAGER, User.ROLE_PURCHASE_MANAGER,
            User.ROLE_FINANCE_MANAGER, User.ROLE_SALES_MANAGER
        };
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(inputFont);
        roleComboBox.setBorder(INPUT_FIELD_BORDER); // Consistent border
        actualFormPanel.add(roleComboBox, gbc);
        
        formContainerPanel.add(actualFormPanel, new GridBagConstraints()); // Add actual form to centering panel

        // --- BUTTON PANEL ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Increased gap
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20,0,10,0)); // Margin above buttons

        JButton registerButton = new JButton("Register");
        registerButton.setFont(buttonTextFont); // Corrected: Use UIBase font
        registerButton.setBackground(primaryColor);
        registerButton.setForeground(Color.WHITE);
        // Scaling handled by WindowManager via UIBase

        JButton backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setFont(buttonTextFont); // Corrected: Use UIBase font
        // Scaling handled by WindowManager via UIBase
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);
        
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel);

        // --- ACTION LISTENERS ---
        registerButton.addActionListener(e -> performRegistration());
        backToLoginButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(LoginPage::new);
        });
    }

    private void performRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        try {
            newUser.setPassword(password); 
            newUser.setConfirmPassword(confirmPassword);
            newUser.setEmail(email);
            newUser.setRole(role);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            dbHelper.registerUser(newUser);
            JOptionPane.showMessageDialog(this, "Registration successful! User ID: " + newUser.getUserId(), "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            dispose();
            SwingUtilities.invokeLater(LoginPage::new);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Hint: Consider proper logging
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        roleComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterPage::new);
    }
}
