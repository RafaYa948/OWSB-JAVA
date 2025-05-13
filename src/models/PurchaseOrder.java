package models;

import java.time.LocalDate;

public class PurchaseOrder {
    private String orderId;
    private String requisitionId;
    private String itemCode;
    private String itemName;
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private String supplierId;
    private String purchaseManagerId;
    private String status;
    
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";
    
    public PurchaseOrder() {
    }
    
    public PurchaseOrder(String orderId, String requisitionId, String itemCode, int quantity, 
                         double unitPrice, double totalAmount, LocalDate orderDate, 
                         LocalDate expectedDeliveryDate, String supplierId, 
                         String purchaseManagerId, String status) {
        this.orderId = orderId;
        this.requisitionId = requisitionId;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.supplierId = supplierId;
        this.purchaseManagerId = purchaseManagerId;
        this.status = status;
    }
    
    public PurchaseOrder(String orderId, String requisitionId, String itemCode, String itemName,
                         int quantity, double unitPrice, double totalAmount, LocalDate orderDate, 
                         LocalDate expectedDeliveryDate, String supplierId, 
                         String purchaseManagerId, String status) {
        this.orderId = orderId;
        this.requisitionId = requisitionId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.supplierId = supplierId;
        this.purchaseManagerId = purchaseManagerId;
        this.status = status;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getPurchaseManagerId() {
        return purchaseManagerId;
    }
    
    public void setPurchaseManagerId(String purchaseManagerId) {
        this.purchaseManagerId = purchaseManagerId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "PurchaseOrder [orderId=" + orderId + ", requisitionId=" + requisitionId + 
               ", itemCode=" + itemCode + ", quantity=" + quantity + 
               ", unitPrice=" + unitPrice + ", totalAmount=" + totalAmount +
               ", orderDate=" + orderDate + ", expectedDeliveryDate=" + expectedDeliveryDate +
               ", supplierId=" + supplierId + ", purchaseManagerId=" + purchaseManagerId +
               ", status=" + status + "]";
    }
    
    public boolean validateData() {
        return orderId != null && !orderId.trim().isEmpty() &&
               requisitionId != null && !requisitionId.trim().isEmpty() &&
               itemCode != null && !itemCode.trim().isEmpty() &&
               quantity > 0 &&
               unitPrice > 0 &&
               totalAmount > 0 &&
               orderDate != null &&
               expectedDeliveryDate != null &&
               supplierId != null && !supplierId.trim().isEmpty() &&
               purchaseManagerId != null && !purchaseManagerId.trim().isEmpty() &&
               status != null && !status.trim().isEmpty();
    }
}