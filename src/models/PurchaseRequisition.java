package models;

import java.time.LocalDate;

public class PurchaseRequisition {
    private String requisitionId;
    private String itemCode;
    private String itemName; 
    private int quantity;
    private LocalDate requiredDate;
    private String salesManagerId;
    private String status; 
    
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";
    
    public PurchaseRequisition() {
    }
    
    public PurchaseRequisition(String requisitionId, String itemCode, int quantity, 
                              LocalDate requiredDate, String salesManagerId, String status) {
        this.requisitionId = requisitionId;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.requiredDate = requiredDate;
        this.salesManagerId = salesManagerId;
        this.status = status;
    }
    
    
    public PurchaseRequisition(String requisitionId, String itemCode, String itemName,
                              int quantity, LocalDate requiredDate, String salesManagerId, String status) {
        this.requisitionId = requisitionId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.requiredDate = requiredDate;
        this.salesManagerId = salesManagerId;
        this.status = status;
    }
    
    public String getRequisitionId() {
        return requisitionId;
    }
    
    public void setRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
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
    }
    
    public LocalDate getRequiredDate() {
        return requiredDate;
    }
    
    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
    }
    
    public String getSalesManagerId() {
        return salesManagerId;
    }
    
    public void setSalesManagerId(String salesManagerId) {
        this.salesManagerId = salesManagerId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "PurchaseRequisition [requisitionId=" + requisitionId + ", itemCode=" + itemCode + 
               ", quantity=" + quantity + ", requiredDate=" + requiredDate + 
               ", salesManagerId=" + salesManagerId + ", status=" + status + "]";
    }
    
    public boolean validateData() {
        return requisitionId != null && !requisitionId.trim().isEmpty() &&
               itemCode != null && !itemCode.trim().isEmpty() &&
               quantity > 0 &&
               requiredDate != null &&
               salesManagerId != null && !salesManagerId.trim().isEmpty() &&
               status != null && !status.trim().isEmpty();
    }
}