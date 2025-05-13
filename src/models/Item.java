package models;

public class Item {
    private String itemCode;
    private String itemName;
    private String supplierId;
    
    public Item() {
    }
    
    public Item(String itemCode, String itemName, String supplierId) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.supplierId = supplierId;
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
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public boolean validateItemData() {
        return itemCode != null && !itemCode.trim().isEmpty() &&
               itemName != null && !itemName.trim().isEmpty() &&
               supplierId != null && !supplierId.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "Item [itemCode=" + itemCode + ", itemName=" + itemName + ", supplierId=" + supplierId + "]";
    }
}