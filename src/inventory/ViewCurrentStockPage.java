package inventory;

import java.util.List;
import inventory.InventoryItem;    // import InventoryItem
import controller.InventoryManager;

public class ViewCurrentStockPage {
    private InventoryManager inventoryManager;

    public ViewCurrentStockPage(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void display(boolean showOnlyLowStock) {
        List<InventoryItem> itemsToDisplay;

        if (showOnlyLowStock) {
            itemsToDisplay = inventoryManager.getLowStockItems();
            System.out.println("\n--- LOW STOCK ITEMS ---");
        } else {
            itemsToDisplay = inventoryManager.getAllItems();
            System.out.println("\n--- ALL ITEMS ---");
        }

        System.out.println("Item Code | Item Name | Current Quantity");
        System.out.println("----------------------------------------");

        for (InventoryItem item : itemsToDisplay) {
            String quantityDisplay = item.getQuantity() + (item.isLowStock() ? " (LOW)" : "");
            System.out.printf("%-10s | %-20s | %s\n", 
                              item.getItemCode(), item.getItemName(), quantityDisplay);
        }
    }
}
