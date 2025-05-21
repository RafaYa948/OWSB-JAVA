package inventory;

import models.Item;

public class InventoryItem {
    private Item item;
    private int quantity;
    private static final int LOW_STOCK_THRESHOLD = 10;

    public InventoryItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public String getItemCode() {
        return item.getItemCode();
    }

    public String getItemName() {
        return item.getItemName();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isLowStock() {
        return quantity < LOW_STOCK_THRESHOLD;
    }

    public Item getItem() {
        return item;
    }
}
