package admin;

import database.DatabaseHelper;
import models.Financial;
import models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FinancialReportsPage extends UIBase {
    private final User currentUser;
    private JTable financialTable;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JPanel statsPanel;
    private List<Financial> financialList;
    private DefaultTableModel tableModel;
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    private static final String[] TABLE_COLUMNS = {
            "Order ID", "Item Name", "Quantity", "Unit Price", "Total Amount", "Order Date", "Supplier ID", "PM ID", "Status"
    };

    public FinancialReportsPage(User currentUser) {
        super("Financial Reports");
        this.currentUser = currentUser;
        this.financialList = new ArrayList<>();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadFinancialData();
        }
        super.setVisible(visible);
    }

    private void loadFinancialData() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            financialList = dbHelper.getAllFinancialReports();

            if (financialList == null) {
                financialList = new ArrayList<>();
            }

            updateTable(financialList);
            updateStats(financialList);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading financial data: " + ex.getMessage(),
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

        statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Financial Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(11, 61, 145));
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterFinancial());

        JLabel filterLabel = new JLabel("Status:");
        String[] statusOptions = {"All", Financial.STATUS_PAID, Financial.STATUS_PENDING, Financial.STATUS_OVERDUE};
        statusFilter = new JComboBox<>(statusOptions);
        statusFilter.addActionListener(e -> filterFinancial());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(11, 61, 145));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadFinancialData());

        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(filterLabel);
        controlPanel.add(statusFilter);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(refreshButton);

        headerPanel.add(controlPanel, BorderLayout.CENTER);
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

        financialTable = new JTable(tableModel);
        financialTable.setFillsViewportHeight(true);
        financialTable.setRowHeight(30);
        financialTable.getTableHeader().setReorderingAllowed(false);
        financialTable.getTableHeader().setBackground(new Color(240, 240, 240));
        financialTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        financialTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        financialTable.setSelectionBackground(new Color(232, 242, 254));
        financialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        financialTable.setShowGrid(true);
        financialTable.setGridColor(new Color(230, 230, 230));

        
        financialTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        
        financialTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        financialTable.getColumnModel().getColumn(1).setPreferredWidth(150); 
        financialTable.getColumnModel().getColumn(2).setPreferredWidth(70);  
        financialTable.getColumnModel().getColumn(3).setPreferredWidth(100); 
        financialTable.getColumnModel().getColumn(4).setPreferredWidth(100); 
        financialTable.getColumnModel().getColumn(5).setPreferredWidth(100); 
        financialTable.getColumnModel().getColumn(6).setPreferredWidth(80);  
        financialTable.getColumnModel().getColumn(7).setPreferredWidth(70);  
        financialTable.getColumnModel().getColumn(8).setPreferredWidth(80);  

        
        financialTable.getColumnModel().getColumn(8).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
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
                    case Financial.STATUS_OVERDUE -> {
                        label.setForeground(new Color(220, 53, 69));
                        label.setBackground(new Color(248, 215, 218));
                    }
                    case Financial.STATUS_PENDING -> {
                        label.setForeground(new Color(255, 145, 0));
                        label.setBackground(new Color(255, 243, 205));
                    }
                    default -> {
                        label.setForeground(new Color(40, 167, 69));
                        label.setBackground(new Color(212, 237, 218));
                    }
                }
            }

            return label;
        });

        JScrollPane scrollPane = new JScrollPane(financialTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Financial Summary",
                        SwingConstants.LEFT,
                        SwingConstants.TOP,
                        new Font("SansSerif", Font.BOLD, 14)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel totalOrdersLabel = createStatLabel("Total Orders: 0");
        JLabel totalAmountLabel = createStatLabel("Total Amount: $0.00");
        JLabel paidLabel = createStatLabel("Paid: 0");
        JLabel pendingLabel = createStatLabel("Pending: 0");
        JLabel overdueLabel = createStatLabel("Overdue: 0");

        panel.add(totalOrdersLabel);
        panel.add(createVerticalSeparator());
        panel.add(totalAmountLabel);
        panel.add(createVerticalSeparator());
        panel.add(paidLabel);
        panel.add(createVerticalSeparator());
        panel.add(pendingLabel);
        panel.add(createVerticalSeparator());
        panel.add(overdueLabel);

        return panel;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return label;
    }

    private JSeparator createVerticalSeparator() {
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        separator.setForeground(new Color(200, 200, 200));
        return separator;
    }

    private void filterFinancial() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        try {
            List<Financial> filteredList = financialList.stream()
                    .filter(financial ->
                            (searchText.isEmpty() ||
                                    financial.getOrderId().toLowerCase().contains(searchText) ||
                                    financial.getItemName().toLowerCase().contains(searchText) ||
                                    financial.getSupplierId().toLowerCase().contains(searchText)) &&
                                    ("All".equals(selectedStatus) || financial.getStatus().equals(selectedStatus)))
                    .toList();

            updateTable(filteredList);
            updateStats(filteredList);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateTable(List<Financial> financialList) {
        tableModel.setRowCount(0);

        for (Financial financial : financialList) {
            try {
                String unitPrice = currencyFormatter.format(financial.getUnitPrice());
                String totalAmount = currencyFormatter.format(financial.getTotalAmount());

                tableModel.addRow(new Object[]{
                        financial.getOrderId(),
                        financial.getItemName(),
                        financial.getQuantity(),
                        unitPrice,
                        totalAmount,
                        financial.getOrderDate(),
                        financial.getSupplierId(),
                        financial.getPurchaseManagerId(),
                        financial.getStatus()
                });
            } catch (Exception e) {
                System.out.println("Warning: Error formatting row data for financial record " + financial.getOrderId());
                e.printStackTrace();
            }
        }
    }

    private void updateStats(List<Financial> financialList) {
        int totalOrders = financialList.size();
        double totalAmount = 0;
        int paid = 0;
        int pending = 0;
        int overdue = 0;

        for (Financial financial : financialList) {
            totalAmount += financial.getTotalAmount();
            switch (financial.getStatus()) {
                case Financial.STATUS_PAID -> paid++;
                case Financial.STATUS_PENDING -> pending++;
                case Financial.STATUS_OVERDUE -> overdue++;
            }
        }

        for (Component component : statsPanel.getComponents()) {
            if (component instanceof JLabel label) {
                String text = label.getText();
                if (text.startsWith("Total Orders:")) {
                    label.setText("Total Orders: " + totalOrders);
                } else if (text.startsWith("Total Amount:")) {
                    label.setText("Total Amount: " + currencyFormatter.format(totalAmount));
                } else if (text.startsWith("Paid:")) {
                    label.setText("Paid: " + paid);
                } else if (text.startsWith("Pending:")) {
                    label.setText("Pending: " + pending);
                } else if (text.startsWith("Overdue:")) {
                    label.setText("Overdue: " + overdue);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User("ADMIN001", "admin", "password", "admin@example.com", User.ROLE_ADMINISTRATOR);
            FinancialReportsPage page = new FinancialReportsPage(testUser);
            page.setVisible(true);
        });
    }
}