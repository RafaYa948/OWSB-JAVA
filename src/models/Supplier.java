package models;

import java.time.LocalDate;
import java.util.UUID;

public class Supplier {
    private String supplierId;
    private String supplierName;
    private String contactPerson;
    private String contactNumber;
    private String email;
    private String address;
    private String suppliedItems;
    private LocalDate lastOrderDate;
    private String status;
    
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";
    
    public Supplier() {
        this.supplierId = "SUP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public Supplier(String supplierId, String supplierName, String suppliedItems) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.suppliedItems = suppliedItems;
        this.status = STATUS_ACTIVE;
    }
    
    public Supplier(String supplierId, String supplierName, String contactPerson, 
                  String contactNumber, String email, String address, 
                  String suppliedItems, LocalDate lastOrderDate, String status) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.suppliedItems = suppliedItems;
        this.lastOrderDate = lastOrderDate;
        this.status = status;
    }
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getSuppliedItems() {
        return suppliedItems;
    }
    
    public void setSuppliedItems(String suppliedItems) {
        this.suppliedItems = suppliedItems;
    }
    
    public LocalDate getLastOrderDate() {
        return lastOrderDate;
    }
    
    public void setLastOrderDate(LocalDate lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean validateData() {
        return supplierId != null && !supplierId.trim().isEmpty() &&
               supplierName != null && !supplierName.trim().isEmpty() &&
               suppliedItems != null;
    }
    
    @Override
    public String toString() {
        return "Supplier [supplierId=" + supplierId + ", supplierName=" + supplierName + 
               ", contactPerson=" + contactPerson + ", contactNumber=" + contactNumber + 
               ", email=" + email + ", address=" + address + 
               ", suppliedItems=" + suppliedItems + ", lastOrderDate=" + lastOrderDate + 
               ", status=" + status + "]";
    }
}