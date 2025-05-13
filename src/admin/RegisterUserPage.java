package admin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class RegisterUserPage extends UIBase {
    
    public RegisterUserPage() {
        super("Register User");
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createTopBar(), BorderLayout.NORTH);
        root.add(createContentPanel(), BorderLayout.CENTER);
        
        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setPreferredSize(new Dimension(200, APP_WINDOW_HEIGHT));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        // Logo panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        JLabel logoLabel = new JLabel(new ImageIcon("logo.png"), SwingConstants.CENTER);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        
        // Menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        
        // Dashboard menu item
        final JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.setMaximumSize(new Dimension(200, 50));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        dashboardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel dashLabel = new JLabel("Dashboard");
        dashLabel.setFont(new Font("Serif", Font.BOLD, 16));
        dashboardPanel.add(dashLabel, BorderLayout.CENTER);
        
        dashboardPanel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { dashboardPanel.setBackground(new Color(240, 240, 240)); }
            public void mouseExited(MouseEvent evt) { dashboardPanel.setBackground(Color.WHITE); }
        });
        
        // Register User menu item (selected)
        final JPanel registerUserPanel = new JPanel(new BorderLayout());
        registerUserPanel.setBackground(new Color(240, 240, 240));
        registerUserPanel.setMaximumSize(new Dimension(200, 50));
        registerUserPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        registerUserPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel registerUserLabel = new JLabel("Register User");
        registerUserLabel.setFont(new Font("Serif", Font.BOLD, 16));
        registerUserPanel.add(registerUserLabel, BorderLayout.CENTER);
        
        menuPanel.add(dashboardPanel);
        menuPanel.add(registerUserPanel);
        menuPanel.add(Box.createVerticalGlue());
        
        // Logout panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBackground(new Color(100, 100, 100));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        final JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(120, 120, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutBtn.setPreferredSize(new Dimension(150, 35));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(true);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(logoutBtn.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        });
        
        logoutPanel.add(logoutBtn);
        
        navPanel.add(logoPanel, BorderLayout.NORTH);
        navPanel.add(menuPanel, BorderLayout.CENTER);
        navPanel.add(logoutPanel, BorderLayout.SOUTH);
        
        return navPanel;
    }

    private JPanel createTopBar() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.WHITE);
        
        // User panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setBackground(new Color(180, 180, 180));
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));
        
        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel userLabel = new JLabel("Username user ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        userPanel.add(bell);
        userPanel.add(userLabel);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("Register User");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(11, 61, 145));
        
        headerPanel.add(title, BorderLayout.WEST);
        
        topContainer.add(userPanel, BorderLayout.NORTH);
        topContainer.add(headerPanel, BorderLayout.SOUTH);
        
        return topContainer;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 0.3;
        
        // User ID field
        JLabel userIdLabel = createFormLabel("User ID");
        JTextField userIdField = createFormTextField();
        
        // Name field
        JLabel nameLabel = createFormLabel("Name");
        JTextField nameField = createFormTextField();
        
        // Email field
        JLabel emailLabel = createFormLabel("Email");
        JTextField emailField = createFormTextField();
        
        // Password field
        JLabel passwordLabel = createFormLabel("Password");
        JPasswordField passwordField = createFormPasswordField();
        
        // Role dropdown
        JLabel roleLabel = createFormLabel("Role");
        String[] roles = {"Select Role", "Admin", "User", "Manager", "Guest"};
        JComboBox<String> roleComboBox = createFormComboBox(roles);
        
        // Add components to form
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userIdLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(userIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.7;
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.7;
        formPanel.add(roleComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 30));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton registerBtn = createButton("Register", new Color(100, 100, 100));
        JButton resetBtn = createButton("Reset", new Color(100, 100, 100));
        
        buttonPanel.add(registerBtn);
        buttonPanel.add(resetBtn);
        
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(120, 120, 120));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        label.setPreferredSize(new Dimension(120, 40));
        return label;
    }
    
    private JTextField createFormTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setPreferredSize(new Dimension(250, 40));
        return field;
    }
    
    private JPasswordField createFormPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setPreferredSize(new Dimension(250, 40));
        return field;
    }
    
    private JComboBox<String> createFormComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(250, 40));
        return comboBox;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(button.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { new RegisterUserPage(); }
        });
    }
}