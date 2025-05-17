package models;

import java.time.LocalDate;

public class SalesEntry {
    private String entryId;
    private LocalDate date;
    private String itemId;
    private String itemName;
    private int quantity;
    private String category;
    private double pricePerUnit;
    private double totalPrice;
    private String salesManagerId;

    public SalesEntry() {
    }

    public SalesEntry(String entryId, LocalDate date, String itemId, String itemName, 
                    int quantity, String category, double pricePerUnit, 
                    double totalPrice, String salesManagerId) {
        this.entryId = entryId;
        this.date = date;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
        this.salesManagerId = salesManagerId;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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
        calculateTotalPrice();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        calculateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSalesManagerId() {
        return salesManagerId;
    }

    public void setSalesManagerId(String salesManagerId) {
        this.salesManagerId = salesManagerId;
    }

    private void calculateTotalPrice() {
        this.totalPrice = this.quantity * this.pricePerUnit;
    }

    public boolean validateData() {
        return entryId != null && !entryId.trim().isEmpty() &&
               date != null &&
               itemId != null && !itemId.trim().isEmpty() &&
               quantity > 0 &&
               pricePerUnit > 0 &&
               salesManagerId != null && !salesManagerId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "SalesEntry [entryId=" + entryId + ", date=" + date + 
               ", itemId=" + itemId + ", itemName=" + itemName + 
               ", quantity=" + quantity + ", category=" + category + 
               ", pricePerUnit=" + pricePerUnit + ", totalPrice=" + totalPrice + 
               ", salesManagerId=" + salesManagerId + "]";
    }
}