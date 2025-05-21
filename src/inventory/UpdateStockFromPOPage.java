package inventory;

import models.PurchaseOrder;  // corrected package name to plural 'models'
import controller.InventoryManager;

import java.util.List;  // import for List

public class UpdateStockFromPOPage {
    private InventoryManager inventoryManager;

    public UpdateStockFromPOPage(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void displayPOs(List<PurchaseOrder> pendingPOs) {
        System.out.println("\n--- PENDING PURCHASE ORDERS ---");
        System.out.println("PO ID | Item Code | Item Name | Ordered Qty");
        System.out.println("-------------------------------------------");
        
        for (PurchaseOrder po : pendingPOs) {
            System.out.printf("%-6s | %-10s | %-20s | %d\n", 
                              po.getOrderId(), po.getItemCode(), 
                              po.getItemName(), po.getQuantity());
        }
    }

    public void processPOReceipt(PurchaseOrder po, int receivedQuantity) {
        if (inventoryManager.updateStockFromPO(po.getOrderId(), po.getItemCode(), receivedQuantity)) {
            System.out.println("Stock updated successfully!");
        } else {
            System.out.println("Error: Item not found in inventory.");
        }
    }
}
