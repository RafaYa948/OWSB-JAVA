package admin;

import database.DatabaseHelper;
import models.Stock;
import models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class StockReportsPage extends UIBase {
    private final User currentUser;
    private JTable stockTable;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private DatabaseHelper dbHelper;
    private JPanel statsPanel;  
    private static final String[] TABLE_COLUMNS = {
            "Item Code", "Item Name", "Quantity", "Location", "Last Updated", "Status"
    };

    public StockReportsPage(User currentUser) {
        super("Stock Reports");
        this.currentUser = currentUser;
        this.dbHelper = new DatabaseHelper();
    }

    @Override
    protected void initUI() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper();
        }

        
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
        loadStockData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Stock Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(11, 61, 145));
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterStock());

        
        JLabel filterLabel = new JLabel("Status:");
        String[] statusOptions = {"All", Stock.STATUS_IN_STOCK, Stock.STATUS_LOW_STOCK, Stock.STATUS_OUT_OF_STOCK};
        statusFilter = new JComboBox<>(statusOptions);
        statusFilter.addActionListener(e -> filterStock());

        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(11, 61, 145));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadStockData());

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

        DefaultTableModel model = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stockTable = new JTable(model);
        stockTable.setFillsViewportHeight(true);
        stockTable.setRowHeight(30);
        stockTable.getTableHeader().setReorderingAllowed(false);
        stockTable.getTableHeader().setBackground(new Color(240, 240, 240));
        stockTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        stockTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        stockTable.setSelectionBackground(new Color(232, 242, 254));
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockTable.setShowGrid(true);
        stockTable.setGridColor(new Color(230, 230, 230));

        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        stockTable.setRowSorter(sorter);

        
        stockTable.getColumnModel().getColumn(5).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
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
                    case Stock.STATUS_OUT_OF_STOCK -> {
                        label.setForeground(new Color(220, 53, 69));
                        label.setBackground(new Color(248, 215, 218));
                    }
                    case Stock.STATUS_LOW_STOCK -> {
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

        
        stockTable.getColumnModel().getColumn(0).setPreferredWidth(100);  
        stockTable.getColumnModel().getColumn(1).setPreferredWidth(200);  
        stockTable.getColumnModel().getColumn(2).setPreferredWidth(80);   
        stockTable.getColumnModel().getColumn(3).setPreferredWidth(120);  
        stockTable.getColumnModel().getColumn(4).setPreferredWidth(100);  
        stockTable.getColumnModel().getColumn(5).setPreferredWidth(100);  

        JScrollPane scrollPane = new JScrollPane(stockTable);
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
                        "Statistics",
                        SwingConstants.LEFT,
                        SwingConstants.TOP,
                        new Font("SansSerif", Font.BOLD, 14)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        
        JLabel totalItemsLabel = createStatLabel("Total Items: 0");
        JLabel inStockLabel = createStatLabel("In Stock: 0");
        JLabel lowStockLabel = createStatLabel("Low Stock: 0");
        JLabel outOfStockLabel = createStatLabel("Out of Stock: 0");

        panel.add(totalItemsLabel);
        panel.add(createVerticalSeparator());
        panel.add(inStockLabel);
        panel.add(createVerticalSeparator());
        panel.add(lowStockLabel);
        panel.add(createVerticalSeparator());
        panel.add(outOfStockLabel);

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

    private void loadStockData() {
        try {
            List<Stock> stockList = dbHelper.getAllStock();
            updateTable(stockList);
            updateStats(stockList);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading stock data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void filterStock() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        try {
            List<Stock> allStock = dbHelper.getAllStock();
            List<Stock> filteredStock = allStock.stream()
                    .filter(stock ->
                            (searchText.isEmpty() ||
                                    stock.getItemCode().toLowerCase().contains(searchText) ||
                                    stock.getItemName().toLowerCase().contains(searchText) ||
                                    stock.getLocation().toLowerCase().contains(searchText)) &&
                                    ("All".equals(selectedStatus) || stock.getStatus().equals(selectedStatus)))
                    .toList();

            updateTable(filteredStock);
            updateStats(filteredStock);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering stock data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateTable(List<Stock> stockList) {
        DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
        model.setRowCount(0);

        for (Stock stock : stockList) {
            model.addRow(new Object[]{
                    stock.getItemCode(),
                    stock.getItemName(),
                    stock.getQuantity(),
                    stock.getLocation(),
                    stock.getLastUpdated(),
                    stock.getStatus()
            });
        }
    }

    private void updateStats(List<Stock> stockList) {
        int totalItems = stockList.size();
        int inStock = 0;
        int lowStock = 0;
        int outOfStock = 0;

        for (Stock stock : stockList) {
            switch (stock.getStatus()) {
                case Stock.STATUS_IN_STOCK -> inStock++;
                case Stock.STATUS_LOW_STOCK -> lowStock++;
                case Stock.STATUS_OUT_OF_STOCK -> outOfStock++;
            }
        }

        
        for (Component component : statsPanel.getComponents()) {
            if (component instanceof JLabel label) {
                String text = label.getText();
                if (text.startsWith("Total Items:")) {
                    label.setText("Total Items: " + totalItems);
                } else if (text.startsWith("In Stock:")) {
                    label.setText("In Stock: " + inStock);
                } else if (text.startsWith("Low Stock:")) {
                    label.setText("Low Stock: " + lowStock);
                } else if (text.startsWith("Out of Stock:")) {
                    label.setText("Out of Stock: " + outOfStock);
                }
            }
        }
    }
}