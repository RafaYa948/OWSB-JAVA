package admin;

import database.DatabaseHelper;
import models.SystemLog;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SystemLogsPage extends UIBase {
    private final User currentUser;
    private JTable logsTable;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> actionFilter;
    private List<SystemLog> logsList;
    private DefaultTableModel tableModel;
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] TABLE_COLUMNS = {
            "Log ID", "User ID", "Username", "Action", "Details", "Timestamp", "Role"
    };

    public SystemLogsPage(User currentUser) {
        super("System Logs");
        this.currentUser = currentUser;
        this.logsList = new ArrayList<>();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadLogs();
        }
        super.setVisible(visible);
    }

    private void loadLogs() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            logsList = dbHelper.getAllSystemLogs();

            if (logsList == null) {
                logsList = new ArrayList<>();
            }

            updateTable(logsList);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading system logs: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    protected void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose();
            new DashboardPage(currentUser).setVisible(true);
        });
        backButtonPanel.add(backButton);
        mainPanel.add(backButtonPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);

        
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("System Logs", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(11, 61, 145));
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtersPanel.setBackground(Color.WHITE);
        filtersPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterLogs());

        
        JLabel actionLabel = new JLabel("Action:");
        String[] actionOptions = {"All", SystemLog.ACTION_LOGIN, SystemLog.ACTION_LOGOUT,
                SystemLog.ACTION_CREATE, SystemLog.ACTION_UPDATE,
                SystemLog.ACTION_DELETE, SystemLog.ACTION_VIEW};
        actionFilter = new JComboBox<>(actionOptions);
        actionFilter.addActionListener(e -> filterLogs());

        
        JLabel roleLabel = new JLabel("Role:");
        String[] roleOptions = {"All", User.ROLE_ADMINISTRATOR, User.ROLE_INVENTORY_MANAGER,
                User.ROLE_PURCHASE_MANAGER, User.ROLE_FINANCE_MANAGER,
                User.ROLE_SALES_MANAGER};
        roleFilter = new JComboBox<>(roleOptions);
        roleFilter.addActionListener(e -> filterLogs());

        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(11, 61, 145));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadLogs());

        filtersPanel.add(searchLabel);
        filtersPanel.add(searchField);
        filtersPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filtersPanel.add(actionLabel);
        filtersPanel.add(actionFilter);
        filtersPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filtersPanel.add(roleLabel);
        filtersPanel.add(roleFilter);
        filtersPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filtersPanel.add(refreshButton);

        headerPanel.add(filtersPanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logsTable = new JTable(tableModel);
        logsTable.setFillsViewportHeight(true);
        logsTable.setRowHeight(30);
        logsTable.getTableHeader().setReorderingAllowed(false);
        logsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        logsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        logsTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logsTable.setSelectionBackground(new Color(232, 242, 254));
        logsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logsTable.setShowGrid(true);
        logsTable.setGridColor(new Color(230, 230, 230));

        
        logsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        
        logsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  
        logsTable.getColumnModel().getColumn(2).setPreferredWidth(100); 
        logsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  
        logsTable.getColumnModel().getColumn(4).setPreferredWidth(250); 
        logsTable.getColumnModel().getColumn(5).setPreferredWidth(150); 
        logsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  

        
        logsTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(table.getBackground());
                switch (value.toString()) {
                    case SystemLog.ACTION_CREATE -> {
                        label.setForeground(new Color(40, 167, 69));
                        label.setBackground(new Color(212, 237, 218));
                    }
                    case SystemLog.ACTION_DELETE -> {
                        label.setForeground(new Color(220, 53, 69));
                        label.setBackground(new Color(248, 215, 218));
                    }
                    case SystemLog.ACTION_UPDATE -> {
                        label.setForeground(new Color(255, 145, 0));
                        label.setBackground(new Color(255, 243, 205));
                    }
                    default -> {
                        label.setForeground(Color.BLACK);
                        label.setBackground(Color.WHITE);
                    }
                }
            }

            return label;
        });

        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void filterLogs() {
        String searchText = searchField.getText().toLowerCase();
        String selectedAction = (String) actionFilter.getSelectedItem();
        String selectedRole = (String) roleFilter.getSelectedItem();

        try {
            List<SystemLog> filteredList = logsList.stream()
                    .filter(log ->
                            (searchText.isEmpty() ||
                                    log.getLogId().toLowerCase().contains(searchText) ||
                                    log.getUsername().toLowerCase().contains(searchText) ||
                                    log.getDetails().toLowerCase().contains(searchText)) &&
                                    ("All".equals(selectedAction) || log.getAction().equals(selectedAction)) &&
                                    ("All".equals(selectedRole) || log.getUserRole().equals(selectedRole)))
                    .toList();

            updateTable(filteredList);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering logs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateTable(List<SystemLog> logs) {
        tableModel.setRowCount(0);

        for (SystemLog log : logs) {
            try {
                tableModel.addRow(new Object[]{
                        log.getLogId(),
                        log.getUserId(),
                        log.getUsername(),
                        log.getAction(),
                        log.getDetails(),
                        log.getTimestamp().format(displayFormatter),
                        log.getUserRole()
                });
            } catch (Exception e) {
                System.out.println("Warning: Error formatting row data for log " + log.getLogId());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User("ADMIN001", "admin", "password", "admin@example.com", User.ROLE_ADMINISTRATOR);
            SystemLogsPage page = new SystemLogsPage(testUser);
            page.setVisible(true);
        });
    }
}