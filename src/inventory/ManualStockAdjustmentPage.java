package inventory;

import controller.InventoryManager;

public class ManualStockAdjustmentPage {
    private InventoryManager inventoryManager;

    public ManualStockAdjustmentPage(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void displayItemDetails(String itemCode) {
        for (InventoryItem item : inventoryManager.getAllItems()) {
            if (item.getItemCode().equals(itemCode)) {
                System.out.println("\n--- ITEM DETAILS ---");
                System.out.println("Item Code: " + item.getItemCode());
                System.out.println("Item Name: " + item.getItemName());
                System.out.println("Current Quantity: " + item.getQuantity());
                return;
            }
        }
        System.out.println("Item not found.");
    }

    public void adjustStock(String itemCode, int newQuantity) {
        if (inventoryManager.manualStockAdjustment(itemCode, newQuantity)) {
            System.out.println("Stock adjusted successfully!");
        } else {
            System.out.println("Error: Item not found.");
        }
    }
}
