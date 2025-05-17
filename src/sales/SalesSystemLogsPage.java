// sales/SalesSystemLogsPage.java
package sales;

import admin.UIBase;
import database.DatabaseHelper;
import models.SystemLog;
import models.User;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.LineBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class SalesSystemLogsPage extends UIBase {
    private final User currentUser;
    private JTable logsTable;
    private JTextField searchField;
    private JComboBox<String> actionFilter;
    private List<SystemLog> logsList;
    private DefaultTableModel tableModel;
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] TABLE_COLUMNS = {
            "Log ID", "Action", "Details", "Timestamp"
    };

    public SalesSystemLogsPage(User user) {
        super("Sales System Logs");
        this.currentUser = user;
        this.logsList = new ArrayList<>();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && currentUser != null) {
            loadLogs();
        }
        super.setVisible(visible);
    }

    private void loadLogs() {
        try {
            DatabaseHelper db = new DatabaseHelper();
            List<SystemLog> allLogs = db.getAllSystemLogs();
            tableModel.setRowCount(0);

            for (SystemLog log : allLogs) {
                // Sales Manager should only see logs related to their actions
                if (log.getUserId().equals(currentUser.getUserId())) {
                    tableModel.addRow(new Object[]{
                            log.getLogId(),
                            log.getAction(),
                            log.getDetails(),
                            log.getTimestamp().format(displayFormatter)
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load logs: " + e.getMessage());
        }
    }


    @Override
    protected void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);


        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);


        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
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

        JPanel logsItem = createMenuItem("View System Logs", true);

        menuPanel.add(dashboardItem);
        menuPanel.add(logsItem);
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
                    SalesSystemLogsPage.this,
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

        JLabel title = new JLabel("System Logs");
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
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        menuItem.add(menuLabel, BorderLayout.CENTER);

        return menuItem;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);


        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtersPanel.setBackground(Color.WHITE);
        filtersPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterLogs());


        JLabel actionLabel = new JLabel("Action:");
        // Filter options relevant to Sales Manager actions
        String[] actionOptions = {"All", SystemLog.ACTION_LOGIN, SystemLog.ACTION_LOGOUT,
                SystemLog.ACTION_CREATE, SystemLog.ACTION_UPDATE, SystemLog.ACTION_DELETE, SystemLog.ACTION_VIEW};
        actionFilter = new JComboBox<>(actionOptions);
        actionFilter.addActionListener(e -> filterLogs());


        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton); // Reuse styleButton
        refreshButton.addActionListener(e -> loadLogs());

        filtersPanel.add(searchLabel);
        filtersPanel.add(searchField);
        filtersPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filtersPanel.add(actionLabel);
        filtersPanel.add(actionFilter);
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
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        logsTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        logsTable.getColumnModel().getColumn(3).setPreferredWidth(150);


        logsTable.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
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


        try {
            // Filter based on current user and selected action/search text
            List<SystemLog> dbLogs = new DatabaseHelper().getAllSystemLogs(); // Get fresh logs
            List<SystemLog> filteredList = dbLogs.stream()
                    .filter(log ->
                            log.getUserId().equals(currentUser.getUserId()) && // Only show logs for the current user
                                    (searchText.isEmpty() ||
                                            log.getLogId().toLowerCase().contains(searchText) ||
                                            log.getDetails().toLowerCase().contains(searchText)) &&
                                    ("All".equals(selectedAction) || log.getAction().equals(selectedAction)))
                    .toList();

            updateTable(filteredList);
        } catch (IOException ex) {
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
                        log.getAction(),
                        log.getDetails(),
                        log.getTimestamp().format(displayFormatter)
                });
            } catch (Exception e) {
                System.out.println("Warning: Error formatting row data for log " + log.getLogId());
                e.printStackTrace();
            }
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(120, 120, 120));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40)); // Adjusted size
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