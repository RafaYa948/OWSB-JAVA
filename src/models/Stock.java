package models;

public class Stock {
    private String itemCode;
    private String itemName;
    private int quantity;
    private String location;
    private String lastUpdated;
    private String status;

    public static final String STATUS_IN_STOCK = "In Stock";
    public static final String STATUS_LOW_STOCK = "Low Stock";
    public static final String STATUS_OUT_OF_STOCK = "Out of Stock";

    public Stock() {
    }

    public Stock(String itemCode, String itemName, int quantity, String location, String lastUpdated, String status) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.location = location;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updateStatus();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private void updateStatus() {
        if (this.quantity <= 0) {
            this.status = STATUS_OUT_OF_STOCK;
        } else if (this.quantity <= 10) {
            this.status = STATUS_LOW_STOCK;
        } else {
            this.status = STATUS_IN_STOCK;
        }
    }

    public boolean validateData() {
        return itemCode != null && !itemCode.trim().isEmpty() &&
                itemName != null && !itemName.trim().isEmpty() &&
                quantity >= 0 &&
                location != null && !location.trim().isEmpty() &&
                lastUpdated != null && !lastUpdated.trim().isEmpty() &&
                status != null && !status.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Stock [itemCode=" + itemCode + ", itemName=" + itemName +
                ", quantity=" + quantity + ", location=" + location +
                ", lastUpdated=" + lastUpdated + ", status=" + status + "]";
    }
}