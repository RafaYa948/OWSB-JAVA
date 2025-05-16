package admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Item;
import models.User;
import database.DatabaseHelper;

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
            } else {
                filterItems(null);
            }
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

    private void filterItems(String supplierId) {
        tableModel.setRowCount(0);
        for (Item item : itemsList) {
            if (item != null && (supplierId == null || item.getSupplierId().equals(supplierId))) {
                Object[] rowData = {
                    item.getItemCode(),
                    item.getItemName(),
                    item.getSupplierId()
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
                           : "Admin";
        
        JLabel userLabel = new JLabel(displayName + " ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);
        
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
    
        String[] columnNames = {"Item Code", "Item Name", "Supplier ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
    
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
    
        JButton filterBtn = new JButton("Filter by: All Items ▼");
        styleButton(filterBtn);
        createFilterPopup(filterBtn);
        buttonsPanel.add(filterBtn);
    
        JButton addButton = new JButton("Add Item");
        styleButton(addButton);
        addButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to add a new item?",
                "Add Item Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                setVisible(false);
                SwingUtilities.invokeLater(() -> {
                    AddItemPage addPage = new AddItemPage(currentUser, this);
                    addPage.setVisible(true);
                });
            }
        });
        buttonsPanel.add(addButton);
    
        JButton editButton = new JButton("Edit Item");
        styleButton(editButton);
        editButton.addActionListener(e -> {
            int selectedRow = itemsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select an item to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String itemCode = (String) itemsTable.getValueAt(selectedRow, 0);
            String itemName = (String) itemsTable.getValueAt(selectedRow, 1);
            String supplierId = (String) itemsTable.getValueAt(selectedRow, 2);
            
            Item selectedItem = new Item(itemCode, itemName, supplierId);
            
            setVisible(false);
            SwingUtilities.invokeLater(() -> {
                EditItemPage editPage = new EditItemPage(currentUser, this, selectedItem);
                editPage.setVisible(true);
            });
        });
        buttonsPanel.add(editButton);
    
        JButton deleteButton = new JButton("Delete Item");
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> {
            int selectedRow = itemsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select an item to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            String itemCode = (String) itemsTable.getValueAt(selectedRow, 0);
            String itemName = (String) itemsTable.getValueAt(selectedRow, 1);
    
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete item '" + itemName + "'?",
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
    
                    loadItems();
    
                } catch (IOException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error deleting item: " + ex.getMessage(),
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonsPanel.add(deleteButton);
    
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
    
        return contentPanel;
    }
    
    private void createFilterPopup(JButton filterBtn) {
        JPopupMenu filterMenu = new JPopupMenu();
        filterMenu.setBackground(Color.WHITE);
    
        List<String> supplierIds = new ArrayList<>();
        supplierIds.add("All Items");
        
        
        if (itemsList != null) {
            for (Item item : itemsList) {
                if (!supplierIds.contains(item.getSupplierId())) {
                    supplierIds.add(item.getSupplierId());
                }
            }
        }
    
        for (String supplierId : supplierIds) {
            JMenuItem item = new JMenuItem(supplierId);
            item.setFont(new Font("SansSerif", Font.PLAIN, 14));
            item.setBackground(Color.WHITE);
            item.addActionListener(e -> {
                filterItems(supplierId.equals("All Items") ? null : supplierId);
                filterBtn.setText("Filter by: " + supplierId + " ▼");
            });
            filterMenu.add(item);
        }
    
        filterBtn.addActionListener(e -> {
            filterMenu.show(filterBtn, 0, filterBtn.getHeight());
        });
    }
    
    private void styleButton(JButton button) {
        button.setBackground(new Color(120, 120, 120));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
    }

    private void goBackToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            DashboardPage dashboard = new DashboardPage(currentUser);
            dashboard.setVisible(true);
        });
    }
    
    public void launchAddItemPage() {
        setVisible(false);
        SwingUtilities.invokeLater(() -> {
            AddItemPage addPage = new AddItemPage(currentUser, this);
            addPage.setVisible(true);
        });
    }
    
    public void launchEditItemPage(Item item) {
        setVisible(false);
        SwingUtilities.invokeLater(() -> {
            EditItemPage editPage = new EditItemPage(currentUser, this, item);
            editPage.setVisible(true);
        });
    }
}