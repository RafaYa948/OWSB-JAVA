package models;

public class Financial {
    private String orderId;
    private String itemCode;
    private String itemName;
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private String orderDate;
    private String supplierId;
    private String purchaseManagerId;
    private String status;

    public static final String STATUS_PAID = "Paid";
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_OVERDUE = "Overdue";

    public Financial() {
    }

    public Financial(String orderId, String itemCode, String itemName, int quantity,
                     double unitPrice, double totalAmount, String orderDate,
                     String supplierId, String purchaseManagerId, String status) {
        this.orderId = orderId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.supplierId = supplierId;
        this.purchaseManagerId = purchaseManagerId;
        this.status = status;
    }

    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.totalAmount = this.unitPrice * this.quantity;
    }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getPurchaseManagerId() { return purchaseManagerId; }
    public void setPurchaseManagerId(String purchaseManagerId) { this.purchaseManagerId = purchaseManagerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean validateData() {
        return orderId != null && !orderId.trim().isEmpty() &&
                itemCode != null && !itemCode.trim().isEmpty() &&
                itemName != null && !itemName.trim().isEmpty() &&
                quantity > 0 &&
                unitPrice > 0 &&
                totalAmount > 0 &&
                orderDate != null && !orderDate.trim().isEmpty() &&
                supplierId != null && !supplierId.trim().isEmpty() &&
                purchaseManagerId != null && !purchaseManagerId.trim().isEmpty() &&
                status != null && !status.trim().isEmpty();
    }
}