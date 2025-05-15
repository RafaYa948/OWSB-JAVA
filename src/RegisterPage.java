import database.DatabaseHelper;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import models.User;

public class RegisterPage extends UIBase {
    public static final String TITLE = "User Registration";

    private static final String USERNAME_PLACEHOLDER = "Username";
    private static final String EMAIL_PLACEHOLDER = "Email";
    private static final String PASSWORD_PLACEHOLDER = "Password";
    private static final String CONFIRM_PASSWORD_PLACEHOLDER = "Confirm Password";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private final DatabaseHelper dbHelper;

    public RegisterPage() {
        super(TITLE);
        dbHelper = new DatabaseHelper();
    }

    private static void addPlaceholderStyle(JTextComponent textComponent, String placeholder, Color placeholderColor, Color defaultForegroundColor) {
        textComponent.setText(placeholder);
        textComponent.setForeground(placeholderColor);

        char defaultEchoChar = 0;
        if (textComponent instanceof JPasswordField) {
            defaultEchoChar = ((JPasswordField) textComponent).getEchoChar();
            ((JPasswordField) textComponent).setEchoChar((char) 0);
        }
        final char finalDefaultEchoChar = defaultEchoChar;

        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textComponent.getText().equals(placeholder) && textComponent.getForeground().equals(placeholderColor)) {
                    textComponent.setText("");
                    textComponent.setForeground(defaultForegroundColor);
                    if (textComponent instanceof JPasswordField) {
                        ((JPasswordField) textComponent).setEchoChar(finalDefaultEchoChar);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textComponent.getText().isEmpty()) {
                    textComponent.setText(placeholder);
                    textComponent.setForeground(placeholderColor);
                    if (textComponent instanceof JPasswordField) {
                        ((JPasswordField) textComponent).setEchoChar((char) 0);
                    }
                }
            }
        });
    }


    @Override
    protected void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBackground(Color.WHITE);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(RegisterPage.TITLE, SwingConstants.CENTER); 
        titleLabel.setFont(headerFont);
        titleLabel.setForeground(primaryColor);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel subtitleLabel = new JLabel("Don't have an account yet? Register to get started!", SwingConstants.CENTER);
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(Color.DARK_GRAY);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); 
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formContainerPanel = new JPanel(new GridBagLayout());
        formContainerPanel.setBackground(Color.WHITE);
        rootPanel.add(formContainerPanel, BorderLayout.CENTER);

        JPanel actualFormPanel = new JPanel(new GridBagLayout());
        actualFormPanel.setBackground(panelColor);
        actualFormPanel.setBorder(formPanelBorder);

        
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.weightx = 1.0;
        gbcForm.insets = new Insets(8, 5, 8, 5);
        
        
        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        gbcForm.gridwidth = 2;
        String[] roles = { User.ROLE_INVENTORY_MANAGER, User.ROLE_PURCHASE_MANAGER, 
                         User.ROLE_FINANCE_MANAGER, User.ROLE_SALES_MANAGER };
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(inputFont);
        roleComboBox.setBorder(INPUT_FIELD_BORDER);
        roleComboBox.insertItemAt("Select Your Role", 0);
        roleComboBox.setSelectedIndex(0);
        actualFormPanel.add(roleComboBox, gbcForm);
        
        
        gbcForm.gridy = 1;
        gbcForm.gridwidth = 1;
        usernameField = new JTextField();
        usernameField.setFont(inputFont);
        usernameField.setBorder(INPUT_FIELD_BORDER);
        addPlaceholderStyle(usernameField, USERNAME_PLACEHOLDER, placeholderColor, 
                         UIManager.getColor("TextField.foreground"));
        actualFormPanel.add(usernameField, gbcForm);
        
        gbcForm.gridx = 1;
        emailField = new JTextField();
        emailField.setFont(inputFont);
        emailField.setBorder(INPUT_FIELD_BORDER);
        addPlaceholderStyle(emailField, EMAIL_PLACEHOLDER, placeholderColor, 
                         UIManager.getColor("TextField.foreground"));
        actualFormPanel.add(emailField, gbcForm);
        
        
        gbcForm.gridx = 0;
        gbcForm.gridy = 2;
        passwordField = new JPasswordField();
        passwordField.setFont(inputFont);
        passwordField.setBorder(INPUT_FIELD_BORDER);
        addPlaceholderStyle(passwordField, PASSWORD_PLACEHOLDER, placeholderColor, 
                         UIManager.getColor("PasswordField.foreground"));
        actualFormPanel.add(passwordField, gbcForm);
        
        gbcForm.gridx = 1;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(inputFont);
        confirmPasswordField.setBorder(INPUT_FIELD_BORDER);
        addPlaceholderStyle(confirmPasswordField, CONFIRM_PASSWORD_PLACEHOLDER, placeholderColor, 
                         UIManager.getColor("PasswordField.foreground"));
        actualFormPanel.add(confirmPasswordField, gbcForm);
        
        
        gbcForm.gridx = 0;
        gbcForm.gridy = 3;
        gbcForm.gridwidth = 2;
        gbcForm.insets = new Insets(12, 5, 8, 5); 
        JButton registerButton = new JButton("Register");
        registerButton.setFont(buttonTextFont);
        registerButton.setBackground(primaryColor);
        registerButton.setForeground(Color.WHITE);
        actualFormPanel.add(registerButton, gbcForm);

        GridBagConstraints formContainerGBC = new GridBagConstraints();
        formContainerGBC.anchor = GridBagConstraints.CENTER;
        formContainerGBC.weighty = 0.0;
        formContainerGBC.fill = GridBagConstraints.NONE;
        formContainerPanel.add(actualFormPanel, formContainerGBC);
        
        JPanel bottomLinksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomLinksPanel.setBackground(Color.WHITE);
        bottomLinksPanel.setBorder(BorderFactory.createEmptyBorder(15,0,0,0)); 
        
        JLabel backToLoginLabel = new JLabel("<html><u>Already have an account? Login</u></html>");
        backToLoginLabel.setFont(smallLinkFont);
        backToLoginLabel.setForeground(primaryColor);
        backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(LoginPage::new);
            }
        });
        bottomLinksPanel.add(backToLoginLabel);
        rootPanel.add(bottomLinksPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);

        registerButton.addActionListener(e -> performRegistration());
    }

    private void performRegistration() {
        String username = usernameField.getText();
        if (username.equals(USERNAME_PLACEHOLDER) && usernameField.getForeground().equals(placeholderColor)) username = "";
        
        String email = emailField.getText();
        if (email.equals(EMAIL_PLACEHOLDER) && emailField.getForeground().equals(placeholderColor)) email = "";

        String password = new String(passwordField.getPassword());
        if (passwordField.getEchoChar() == (char)0 && password.equals(PASSWORD_PLACEHOLDER)) password = "";
        
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (confirmPasswordField.getEchoChar() == (char)0 && confirmPassword.equals(CONFIRM_PASSWORD_PLACEHOLDER)) confirmPassword = "";

        String selectedRole = "";
        if (roleComboBox.getSelectedIndex() > 0) { 
            selectedRole = (String) roleComboBox.getSelectedItem();
        }

        if (username.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedRole.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields including role selection are required.", "Error", JOptionPane.ERROR_MESSAGE);
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
            newUser.setRole(selectedRole);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            dbHelper.registerUser(newUser);
            JOptionPane.showMessageDialog(this, "Registration successful! User ID: " + newUser.getUserId() + "\nPlease login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            dispose();
            SwingUtilities.invokeLater(LoginPage::new);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        addPlaceholderStyle(usernameField, USERNAME_PLACEHOLDER, placeholderColor, UIManager.getColor("TextField.foreground"));
        addPlaceholderStyle(emailField, EMAIL_PLACEHOLDER, placeholderColor, UIManager.getColor("TextField.foreground"));
        addPlaceholderStyle(passwordField, PASSWORD_PLACEHOLDER, placeholderColor, UIManager.getColor("PasswordField.foreground"));
        addPlaceholderStyle(confirmPasswordField, CONFIRM_PASSWORD_PLACEHOLDER, placeholderColor, UIManager.getColor("PasswordField.foreground"));
        roleComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterPage::new);
    }
}