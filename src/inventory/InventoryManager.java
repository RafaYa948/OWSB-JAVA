package controller;

import inventory.InventoryItem;
import models.Item;
import models.PurchaseOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManager {

    private List<InventoryItem> items;
    private List<String> stockHistory;  // Store history entries as CSV string
    private static final int LOW_STOCK_THRESHOLD = 10;

    public InventoryManager() {
        items = new ArrayList<>();
        stockHistory = new ArrayList<>();
    }

    public List<InventoryItem> getAllItems() {
        return items;
    }

    public List<InventoryItem> getLowStockItems() {
        return items.stream()
            .filter(item -> item.getQuantity() < LOW_STOCK_THRESHOLD)
            .collect(Collectors.toList());
    }

    public boolean manualStockAdjustment(String itemCode, int newQuantity) {
        InventoryItem item = findItemByCode(itemCode);
        if (item != null) {
            int oldQty = item.getQuantity();
            item.setQuantity(newQuantity);
            addStockHistoryEntry(itemCode, oldQty, newQuantity, "Manual Adjustment");
            return true;
        }
        return false;
    }

    public boolean updateStockFromPO(String poId, String itemCode, int receivedQuantity) {
        InventoryItem item = findItemByCode(itemCode);
        if (item != null) {
            int oldQty = item.getQuantity();
            item.setQuantity(oldQty + receivedQuantity);
            addStockHistoryEntry(itemCode, oldQty, item.getQuantity(), "PO Receipt ID:" + poId);
            return true;
        }
        return false;
    }

    public String generateStockReport() {
        StringBuilder report = new StringBuilder();
        report.append(String.format("%-10s | %-20s | %-10s\n", "Item Code", "Item Name", "Quantity"));
        report.append("-------------------------------------------------\n");
        for (InventoryItem item : items) {
            report.append(String.format("%-10s | %-20s | %-10d\n",
                item.getItemCode(), item.getItemName(), item.getQuantity()));
        }
        return report.toString();
    }

    public List<String> getStockHistory(int limit) {
        int start = Math.max(stockHistory.size() - limit, 0);
        return stockHistory.subList(start, stockHistory.size());
    }

    private InventoryItem findItemByCode(String code) {
        for (InventoryItem item : items) {
            if (item.getItemCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    // Add new item with initial quantity
    public boolean addItem(Item item, int initialQuantity) {
        if (findItemByCode(item.getItemCode()) == null) {
            InventoryItem invItem = new InventoryItem(item, initialQuantity);
            items.add(invItem);
            return true;
        }
        return false; // Duplicate itemCode not allowed
    }

    private void addStockHistoryEntry(String itemCode, int oldQty, int newQty, String action) {
        String date = java.time.LocalDate.now().toString();
        String entry = String.join(",", date, itemCode, String.valueOf(oldQty), String.valueOf(newQty), action);
        stockHistory.add(entry);
    }
}
