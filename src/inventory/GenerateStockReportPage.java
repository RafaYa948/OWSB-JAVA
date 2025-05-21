package inventory;

import inventory.InventoryManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateStockReportPage {
    private InventoryManager inventoryManager;

    public GenerateStockReportPage(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void generateReport() {
        String report = inventoryManager.generateStockReport();
        System.out.println("\n--- STOCK REPORT ---");
        System.out.println(report);
    }

    public void exportReportToFile(String filename) {
        String report = inventoryManager.generateStockReport();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(report);
            System.out.println("Report exported successfully to " + filename);
        } catch (IOException e) {
            System.out.println("Error exporting report: " + e.getMessage());
        }
    }
}
