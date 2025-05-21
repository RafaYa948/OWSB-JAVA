package inventory;

import controller.InventoryManager;
import java.util.List;

public class ViewStockHistoryPage {
    private InventoryManager inventoryManager;

    public ViewStockHistoryPage(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void displayHistory(int limit) {
        List<String> history = inventoryManager.getStockHistory(limit);
        System.out.println("\n--- STOCK CHANGE HISTORY (Last " + limit + " entries) ---");
        System.out.println("Date | Item Code | Old Qty | New Qty | Action");
        System.out.println("----------------------------------------------");
        
        for (String entry : history) {
            String[] parts = entry.split(",");
            if (parts.length >= 5) {
                System.out.printf("%s | %s | %s | %s | %s\n", 
                                parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        }
    }
}