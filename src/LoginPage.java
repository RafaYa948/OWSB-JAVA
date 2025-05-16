import admin.DashboardPage;
import database.DatabaseHelper;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import models.User;
import finance.FinanceDashboardPage;

public class LoginPage extends UIBase {
    public static final String APP_TITLE = "Automated Purchase Order Management System";
    private static final String USERNAME_PLACEHOLDER = "Username";
    private static final String PASSWORD_PLACEHOLDER = "Password";

    public LoginPage() {
        super(APP_TITLE);
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

        JLabel titleLabel = new JLabel(APP_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(headerFont); 
        titleLabel.setForeground(primaryColor);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("Login to your Account to get started or manage your inventory", SwingConstants.CENTER);
        subtitleLabel.setFont(subtitleFont); 
        subtitleLabel.setForeground(Color.DARK_GRAY);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

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
        gbcForm.gridx = 0;
        gbcForm.gridwidth = 1; 
        gbcForm.anchor = GridBagConstraints.CENTER; 
        
        gbcForm.gridy = 0; 
        gbcForm.insets = new Insets(15, 5, 10, 5); 

        JTextField userField = new JTextField(); 
        userField.setFont(inputFont); 
        userField.setBorder(INPUT_FIELD_BORDER); 
        addPlaceholderStyle(userField, USERNAME_PLACEHOLDER, placeholderColor, UIManager.getColor("TextField.foreground"));
        actualFormPanel.add(userField, gbcForm);

        gbcForm.gridy++; 
        gbcForm.insets = new Insets(10, 5, 10, 5); 

        JPasswordField passField = new JPasswordField();
        passField.setFont(inputFont); 
        passField.setBorder(INPUT_FIELD_BORDER); 
        addPlaceholderStyle(passField, PASSWORD_PLACEHOLDER, placeholderColor, UIManager.getColor("PasswordField.foreground"));
        actualFormPanel.add(passField, gbcForm);

        gbcForm.gridy++; 
        gbcForm.insets = new Insets(15, 5, 10, 5); 

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(buttonTextFont); 
        loginBtn.setBackground(primaryColor);
        loginBtn.setForeground(Color.WHITE);
        actualFormPanel.add(loginBtn, gbcForm);
        
        GridBagConstraints formContainerGBC = new GridBagConstraints();
        formContainerGBC.anchor = GridBagConstraints.CENTER; 
        formContainerGBC.weighty = 0.0; 
        formContainerGBC.fill = GridBagConstraints.NONE;
        formContainerPanel.add(actualFormPanel, formContainerGBC);

        JPanel linksPanel = new JPanel(new GridBagLayout());
        linksPanel.setBackground(Color.WHITE);
        linksPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        GridBagConstraints gbcLinks = new GridBagConstraints();
        gbcLinks.gridx = 0;
        gbcLinks.anchor = GridBagConstraints.CENTER;

        JLabel forgotLabel = new JLabel("Forgot Username or Password?");
        forgotLabel.setFont(smallLinkFont); 
        forgotLabel.setForeground(primaryColor);
        forgotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel createAccountLabel = new JLabel("<html>Create an Account</html>"); 
        createAccountLabel.setFont(smallLinkFont); 
        createAccountLabel.setForeground(primaryColor);
        createAccountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                SwingUtilities.invokeLater(RegisterPage::new);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                createAccountLabel.setText("<html><u>Create an Account</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                 createAccountLabel.setText("<html>Create an Account</html>");
            }
        });

        gbcLinks.gridy = 0;
        linksPanel.add(forgotLabel, gbcLinks);
        
        gbcLinks.gridy++;
        linksPanel.add(Box.createRigidArea(new Dimension(0, 8)), gbcLinks);
        
        gbcLinks.gridy++;
        linksPanel.add(createAccountLabel, gbcLinks);

        rootPanel.add(linksPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            if (username.equals(USERNAME_PLACEHOLDER) && userField.getForeground().equals(placeholderColor)) {
                username = "";
            }
            
            String password = new String(passField.getPassword());
            if (passField.getEchoChar() == (char)0 && password.equals(PASSWORD_PLACEHOLDER)) {
                password = "";
            }

            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and Password cannot be empty.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                User user = dbHelper.validateUser(username, password);
                if (user != null) {
                    
                    if (User.ROLE_ADMINISTRATOR.equals(user.getRole())) {
                        dispose(); 
                        SwingUtilities.invokeLater(() -> {
                            DashboardPage dashboard = new DashboardPage(user); 
                            dashboard.setVisible(true);
                        });
                    } else if (User.ROLE_INVENTORY_MANAGER.equals(user.getRole())) {
                        
                        JOptionPane.showMessageDialog(this,
                                "Welcome " + user.getUsername() + "!\nRole: Inventory Manager\nThis dashboard is not yet implemented.",
                                "Login Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (User.ROLE_PURCHASE_MANAGER.equals(user.getRole())) {
                        
                        JOptionPane.showMessageDialog(this,
                                "Welcome " + user.getUsername() + "!\nRole: Purchase Manager\nThis dashboard is not yet implemented.",
                                "Login Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (User.ROLE_FINANCE_MANAGER.equals(user.getRole())) {
                                 dispose();
                                 SwingUtilities.invokeLater(() -> {
                                   new finance.FinanceDashboardPage(user).setVisible(true);
    });
                    } else if (User.ROLE_SALES_MANAGER.equals(user.getRole())) {
                        
                        JOptionPane.showMessageDialog(this,
                                "Welcome " + user.getUsername() + "!\nRole: Sales Manager\nThis dashboard is not yet implemented.",
                                "Login Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        
                        JOptionPane.showMessageDialog(this,
                                "Welcome " + user.getUsername() + "!\nRole: " + user.getRole() + "\nThis role does not have a dashboard yet.",
                                "Login Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                    addPlaceholderStyle(passField, PASSWORD_PLACEHOLDER, placeholderColor, UIManager.getColor("PasswordField.foreground"));
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error accessing user database: " + ex.getMessage(),
                        "System Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}