package view.inventory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CurrentStockPanel extends JPanel {
    public CurrentStockPanel() {
        setLayout(new BorderLayout());
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(new JCheckBox("Show Low Stock Only"));
        add(filterPanel, BorderLayout.NORTH);
        
        // Sample Data Table
        String[] columns = {"Item Code", "Item Name", "Current Qty"};
        Object[][] data = {
            {"ITM001", "Flour", 50},
            {"ITM002", "Sugar", 5},  // Will show in red
            {"ITM003", "Rice", 30}
        };
        
        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(
                TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if ((int)getValueAt(row, 2) < 10) { // Check quantity
                    c.setBackground(new Color(255, 200, 200));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Status Bar
        JLabel status = new JLabel("Last updated: " + new java.util.Date());
        add(status, BorderLayout.SOUTH);
    }
}