package admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import models.User;
import database.DatabaseHelper;

public class ManageUsersPage extends UIBase {
    
    private final User currentUser;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private List<User> usersList;
    
    public ManageUsersPage(User user) {
        super("Manage User");
        this.currentUser = user;
    }
    
    /**
     * This method is called after initUI to ensure the UI is fully initialized
     * before trying to populate the table
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadUsers(); // Load users before setting visible
        }
        super.setVisible(visible);
    }
    
    private void loadUsers() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            usersList = dbHelper.getAllUsers();
            
            if (usersList == null) {
                usersList = new ArrayList<>();
                System.out.println("User list is null!");
            } else {
                System.out.println("Loaded " + usersList.size() + " users");
                
                // Populate the table if it exists
                if (tableModel != null) {
                    tableModel.setRowCount(0); // Clear existing rows
                    
                    for (User user : usersList) {
                        if (user != null) {
                            Object[] rowData = {
                                user.getUserId(),
                                user.getUsername(),
                                user.getPassword(),
                                user.getEmail(),
                                user.getRole()
                            };
                            tableModel.addRow(rowData);
                            System.out.println("Added user to table: " + user.getUserId() + " - " + user.getUsername());
                        }
                    }
                    
                    System.out.println("Table now has " + tableModel.getRowCount() + " rows");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            usersList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            usersList = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void initUI() {
        // Initialize the user list to avoid null pointer
        usersList = new ArrayList<>();
        
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        
        // Create the sidebar (left navigation panel)
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);
        
        // Create the top bar with user info
        JPanel topBar = createTopBar();
        root.add(topBar, BorderLayout.NORTH);
        
        // Create the main content panel
        JPanel contentPanel = createContentPanel();
        root.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(root);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, APP_WINDOW_HEIGHT));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        // Logo panel at the top
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        try {
            File logoFile = new File("src/admin/icon.png");
            if (logoFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(logoFile.getAbsolutePath());
                Image image = originalIcon.getImage();
                Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                
                JLabel logoLabel = new JLabel(scaledIcon, SwingConstants.CENTER);
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                logoPanel.add(logoLabel, BorderLayout.CENTER);
            } else {
                JLabel placeholder = new JLabel("Our System Logo", SwingConstants.CENTER);
                placeholder.setFont(new Font("Serif", Font.BOLD, 16));
                placeholder.setForeground(new Color(11, 61, 145));
                placeholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                logoPanel.add(placeholder, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel placeholder = new JLabel("Our System Logo", SwingConstants.CENTER);
            placeholder.setFont(new Font("Serif", Font.BOLD, 16));
            placeholder.setForeground(new Color(11, 61, 145));
            placeholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            logoPanel.add(placeholder, BorderLayout.CENTER);
        }
        
        sidebar.add(logoPanel, BorderLayout.NORTH);
        
        // Menu items panel
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        
        // Dashboard menu item
        JPanel dashboardItem = createMenuItem("Dashboard", false);
        dashboardItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                goBackToDashboard();
            }
        });
        
        // Manage User menu item (selected)
        JPanel manageUserItem = createMenuItem("Manage User", true);
        
        menuPanel.add(dashboardItem);
        menuPanel.add(manageUserItem);
        menuPanel.add(Box.createVerticalGlue());
        
        sidebar.add(menuPanel, BorderLayout.CENTER);
        
        // Logout button at the bottom
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
        
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(
                    ManageUsersPage.this,
                    "Are you sure you want to log out?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
        
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        
        return sidebar;
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
        
        if (!isSelected) {
            menuItem.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    menuItem.setBackground(new Color(240, 240, 240));
                }
                
                public void mouseExited(MouseEvent evt) {
                    menuItem.setBackground(Color.WHITE);
                }
            });
        }
        
        return menuItem;
    }
    
    private JPanel createTopBar() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.WHITE);
        
        // User info panel at the top right
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
        
        topContainer.add(userPanel, BorderLayout.NORTH);
        
        // Page title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("Manage User");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(11, 61, 145));
        
        headerPanel.add(title);
        
        topContainer.add(headerPanel, BorderLayout.SOUTH);
        
        return topContainer;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table model with column names - include all the fields
        String[] columnNames = {"User ID", "Username", "Password", "Email", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table
        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.getTableHeader().setBackground(new Color(240, 240, 240));
        usersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        usersTable.setRowHeight(30);
        usersTable.setGridColor(Color.LIGHT_GRAY);
        
        // Set column widths
        setColumnWidths(usersTable);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Filter button
        JButton filterBtn = createActionButton("Filter by ▼", new Color(120, 120, 120));
        
        // Add User button
        JButton addButton = createActionButton("Add User", new Color(120, 120, 120));
        addButton.addActionListener(e -> showAddUserDialog());
        
        // Delete User button
        JButton deleteButton = createActionButton("Delete User", new Color(120, 120, 120));
        deleteButton.addActionListener(e -> deleteSelectedUser());
        
        // Edit User button
        JButton editButton = createActionButton("Edit User", new Color(120, 120, 120));
        editButton.addActionListener(e -> editSelectedUser());
        
        // Refresh button
        JButton refreshButton = createActionButton("Refresh", new Color(120, 120, 120));
        refreshButton.addActionListener(e -> refreshUserList());
        
        buttonsPanel.add(filterBtn);
        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(refreshButton);
        
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    private void refreshUserList() {
        loadUsers();
    }
    
    private void setColumnWidths(JTable table) {
        TableColumn column;
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) { // User ID
                column.setPreferredWidth(80);
            } else if (i == 1) { // Username
                column.setPreferredWidth(120);
            } else if (i == 2) { // Password
                column.setPreferredWidth(120);
            } else if (i == 3) { // Email
                column.setPreferredWidth(200);
            } else if (i == 4) { // Role
                column.setPreferredWidth(150);
            }
        }
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void goBackToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            DashboardPage dashboard = new DashboardPage(currentUser);
            dashboard.setVisible(true);
        });
    }
    
    private void showAddUserDialog() {
        // Placeholder for add user functionality
        JOptionPane.showMessageDialog(this, 
            "Add User functionality would go here.", 
            "Add User", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) usersTable.getValueAt(selectedRow, 0);
        String username = (String) usersTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user '" + username + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                dbHelper.deleteUser(userId);
                
                // Refresh the table
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this,
                    "User deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting user: " + ex.getMessage(),
                    "Delete Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSelectedUser() {
        // Placeholder for edit user functionality
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String username = (String) usersTable.getValueAt(selectedRow, 1);
        JOptionPane.showMessageDialog(this, 
            "Edit User functionality for '" + username + "' would go here.", 
            "Edit User", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}