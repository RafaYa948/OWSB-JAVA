package models;

import java.time.LocalDate;

public class SalesReport {
    private String reportId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalSales;
    private int totalTransactions;
    private String salesManagerId;
    private LocalDate generatedDate;
    
    public SalesReport() {
    }
    
    public SalesReport(String reportId, LocalDate startDate, LocalDate endDate, 
                      double totalSales, int totalTransactions, 
                      String salesManagerId, LocalDate generatedDate) {
        this.reportId = reportId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalSales = totalSales;
        this.totalTransactions = totalTransactions;
        this.salesManagerId = salesManagerId;
        this.generatedDate = generatedDate;
    }
    
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public double getTotalSales() {
        return totalSales;
    }
    
    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }
    
    public int getTotalTransactions() {
        return totalTransactions;
    }
    
    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
    
    public String getSalesManagerId() {
        return salesManagerId;
    }
    
    public void setSalesManagerId(String salesManagerId) {
        this.salesManagerId = salesManagerId;
    }
    
    public LocalDate getGeneratedDate() {
        return generatedDate;
    }
    
    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }
    
    public boolean validateData() {
        return reportId != null && !reportId.trim().isEmpty() &&
               startDate != null &&
               endDate != null &&
               totalSales >= 0 &&
               totalTransactions >= 0 &&
               salesManagerId != null && !salesManagerId.trim().isEmpty() &&
               generatedDate != null;
    }
    
    @Override
    public String toString() {
        return "SalesReport [reportId=" + reportId + ", startDate=" + startDate + 
               ", endDate=" + endDate + ", totalSales=" + totalSales + 
               ", totalTransactions=" + totalTransactions + 
               ", salesManagerId=" + salesManagerId + 
               ", generatedDate=" + generatedDate + "]";
    }
}