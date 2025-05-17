package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import models.Item;
import models.PurchaseOrder;
import models.PurchaseRequisition;
import models.User;
import models.Stock;
import models.Financial;
import models.SystemLog;
import models.SalesEntry;
import models.Supplier;

public class DatabaseHelper {
    private static final String DATA_DIRECTORY = "src/database";
    private static final String USERS_FILE = DATA_DIRECTORY + "/user.txt";
    private static final String ITEMS_FILE = DATA_DIRECTORY + "/item.txt";
    private static final String FINANCIAL_FILE = DATA_DIRECTORY + "/financial.txt";
    private static final String STOCK_FILE = DATA_DIRECTORY + "/stock.txt";
    private static final String REQUISITIONS_FILE = DATA_DIRECTORY + "/purchase_requisition.txt";
    private static final String PURCHASE_ORDERS_FILE = DATA_DIRECTORY + "/purchase_order.txt";
    private static final String SYSTEM_LOGS_FILE = DATA_DIRECTORY + "/system_logs.txt";
    private static final String SALES_ENTRY_FILE = DATA_DIRECTORY + "/sales_entry.txt";
    private static final String SUPPLIERS_FILE = DATA_DIRECTORY + "/supplier.txt";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public DatabaseHelper() {
        createDataDirectoryIfNeeded();
    }

    private void createDataDirectoryIfNeeded() {
        File dataDir = new File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // --- User Methods ---
    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            writeUsersToFile(users);
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String userId = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    String email = parts[3];
                    String role = parts[4];

                    User user = new User(userId, username, password, email, role);
                    users.add(user);
                }
            }
        }
        return users;
    }

    public User getUserById(String userId) throws IOException {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public User getUserByUsername(String username) throws IOException {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public User validateUser(String username, String password) throws IOException {
        return authenticate(username, password);
    }

    public User authenticate(String username, String password) throws IOException {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void registerUser(User user) throws IOException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<User> users = getAllUsers();

        for (User existingUser : users) {
            if (existingUser.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("User ID already exists: " + user.getUserId());
            }
            if (existingUser.getUsername().equals(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + user.getUsername());
            }
        }

        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(generateUserId());
        }

        users.add(user);
        writeUsersToFile(users);
    }

    private String generateUserId() throws IOException {
        List<User> users = getAllUsers();
        int maxId = 0;
        for (User user : users) {
            String idNumberStr = user.getUserId().replaceAll("[^0-9]", ""); // Extract only digits
            try {
                int id = Integer.parseInt(idNumberStr);
                maxId = Math.max(maxId, id);
            } catch (NumberFormatException e) {
                // Ignore users with non-standard IDs for generation purposes
            }
        }
        // This might need adjustment based on the actual ID format used for different roles
        return "U" + String.format("%03d", maxId + 1);
    }


    public void updateUser(User user) throws IOException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<User> users = getAllUsers();
        boolean found = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("User not found: " + user.getUserId());
        }

        writeUsersToFile(users);
    }

    public void deleteUser(String userId) throws IOException {
        List<User> users = getAllUsers();
        boolean removed = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(userId)) {
                users.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        writeUsersToFile(users);
    }

    private void writeUsersToFile(List<User> users) throws IOException {
        File file = new File(USERS_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("userId,username,password,email,role");
            writer.newLine();

            for (User user : users) {
                writer.write(String.format("%s,%s,%s,%s,%s",
                        user.getUserId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getRole()
                ));
                writer.newLine();
            }
        }
    }

    // --- Item Methods ---
    public List<Item> getAllItems() throws IOException {
        List<Item> items = new ArrayList<>();
        File file = new File(ITEMS_FILE);

        if (!file.exists()) {
            writeItemsToFile(items);
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String itemCode = parts[0];
                    String itemName = parts[1];
                    String supplierId = parts[2];

                    Item item = new Item(itemCode, itemName, supplierId);
                    items.add(item);
                } else {
                    System.err.println("Skipping malformed item line: " + line);
                }
            }
        }
        return items;
    }

    public Item getItemByCode(String itemCode) throws IOException {
        List<Item> items = getAllItems();
        for (Item item : items) {
            if (item.getItemCode().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }

    public void addItem(Item item) throws IOException {
        if (item == null || !item.validateItemData()) {
            throw new IllegalArgumentException("Invalid item data");
        }

        List<Item> items = getAllItems();

        for (Item existingItem : items) {
            if (existingItem.getItemCode().equals(item.getItemCode())) {
                throw new IllegalArgumentException("Item code already exists: " + item.getItemCode());
            }
        }

        items.add(item);
        writeItemsToFile(items);
    }

    public void createItem(Item item) throws IOException {
        addItem(item);
    }

    public void updateItem(Item item) throws IOException {
        if (item == null || !item.validateItemData()) {
            throw new IllegalArgumentException("Invalid item data");
        }

        List<Item> items = getAllItems();
        boolean found = false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemCode().equals(item.getItemCode())) {
                items.set(i, item);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Item not found: " + item.getItemCode());
        }

        writeItemsToFile(items);
    }

    public void deleteItem(String itemCode) throws IOException {
        List<Item> items = getAllItems();
        boolean removed = false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemCode().equals(itemCode)) {
                items.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Item not found: " + itemCode);
        }

        writeItemsToFile(items);
    }

    private void writeItemsToFile(List<Item> items) throws IOException {
        File file = new File(ITEMS_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("itemCode,itemName,supplierId");
            writer.newLine();

            for (Item item : items) {
                writer.write(String.format("%s,%s,%s",
                        item.getItemCode(),
                        item.getItemName(),
                        item.getSupplierId()
                ));
                writer.newLine();
            }
        }
    }

    // --- Purchase Requisition Methods ---
    public List<PurchaseRequisition> getAllPurchaseRequisitions() throws IOException {
        List<PurchaseRequisition> requisitions = new ArrayList<>();
        File file = new File(REQUISITIONS_FILE);

        if (!file.exists()) {
            writeRequisitionsToFile(requisitions);
            return requisitions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        String requisitionId = parts[0];
                        String itemCode = parts[1];
                        int quantity = Integer.parseInt(parts[2]);
                        LocalDate requiredDate = LocalDate.parse(parts[3], DATE_FORMATTER);
                        String salesManagerId = parts[4];
                        String status = parts[5];

                        String itemName = "";
                        if (parts.length > 6) {
                            itemName = parts[6];
                        } else {
                            try {
                                Item item = getItemByCode(itemCode);
                                if (item != null) {
                                    itemName = item.getItemName();
                                }
                            } catch (IOException e) {
                                System.err.println("Error fetching item name for requisition " + requisitionId + ": " + e.getMessage());
                            }
                        }

                        PurchaseRequisition requisition = new PurchaseRequisition(
                                requisitionId, itemCode, itemName, quantity, requiredDate, salesManagerId, status
                        );

                        requisitions.add(requisition);
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.err.println("Skipping invalid requisition line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed requisition line: " + line);
                }
            }
        }
        return requisitions;
    }

    public PurchaseRequisition getPurchaseRequisitionById(String requisitionId) throws IOException {
        List<PurchaseRequisition> requisitions = getAllPurchaseRequisitions();
        for (PurchaseRequisition requisition : requisitions) {
            if (requisition.getRequisitionId().equals(requisitionId)) {
                return requisition;
            }
        }
        return null;
    }

    public void addPurchaseRequisition(PurchaseRequisition requisition) throws IOException {
        if (requisition == null || !requisition.validateData()) {
            throw new IllegalArgumentException("Invalid requisition data");
        }

        List<PurchaseRequisition> requisitions = getAllPurchaseRequisitions();

        for (PurchaseRequisition existing : requisitions) {
            if (existing.getRequisitionId().equals(requisition.getRequisitionId())) {
                throw new IllegalArgumentException("Requisition ID already exists: " + requisition.getRequisitionId());
            }
        }

        requisitions.add(requisition);
        writeRequisitionsToFile(requisitions);
    }

    public void updatePurchaseRequisition(PurchaseRequisition requisition) throws IOException {
        if (requisition == null || !requisition.validateData()) {
            throw new IllegalArgumentException("Invalid requisition data");
        }

        List<PurchaseRequisition> requisitions = getAllPurchaseRequisitions();
        boolean found = false;

        for (int i = 0; i < requisitions.size(); i++) {
            if (requisitions.get(i).getRequisitionId().equals(requisition.getRequisitionId())) {
                requisitions.set(i, requisition);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Requisition not found: " + requisition.getRequisitionId());
        }

        writeRequisitionsToFile(requisitions);
    }

    public void deletePurchaseRequisition(String requisitionId) throws IOException {
        List<PurchaseRequisition> requisitions = getAllPurchaseRequisitions();
        boolean removed = false;

        for (int i = 0; i < requisitions.size(); i++) {
            if (requisitions.get(i).getRequisitionId().equals(requisitionId)) {
                requisitions.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Requisition not found: " + requisitionId);
        }

        writeRequisitionsToFile(requisitions);
    }

    private void writeRequisitionsToFile(List<PurchaseRequisition> requisitions) throws IOException {
        File file = new File(REQUISITIONS_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("requisitionId,itemCode,quantity,requiredDate,salesManagerId,status,itemName");
            writer.newLine();

            for (PurchaseRequisition requisition : requisitions) {
                writer.write(String.format("%s,%s,%d,%s,%s,%s,%s",
                        requisition.getRequisitionId(),
                        requisition.getItemCode(),
                        requisition.getQuantity(),
                        requisition.getRequiredDate().format(DATE_FORMATTER),
                        requisition.getSalesManagerId(),
                        requisition.getStatus(),
                        requisition.getItemName() != null ? requisition.getItemName() : ""
                ));
                writer.newLine();
            }
        }
    }

    // --- Purchase Order Methods ---
    public List<PurchaseOrder> getAllPurchaseOrders() throws IOException {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        File file = new File(PURCHASE_ORDERS_FILE);

        if (!file.exists()) {
            writePurchaseOrdersToFile(purchaseOrders);
            return purchaseOrders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 11) {
                    try {
                        String orderId = parts[0];
                        String requisitionId = parts[1];
                        String itemCode = parts[2];
                        int quantity = Integer.parseInt(parts[3]);
                        double unitPrice = Double.parseDouble(parts[4]);
                        double totalAmount = Double.parseDouble(parts[5]);
                        LocalDate orderDate = LocalDate.parse(parts[6], DATE_FORMATTER);
                        LocalDate expectedDeliveryDate = LocalDate.parse(parts[7], DATE_FORMATTER);
                        String supplierId = parts[8];
                        String purchaseManagerId = parts[9];
                        String status = parts[10];

                        String itemName = "";
                        try {
                            Item item = getItemByCode(itemCode);
                            if (item != null) {
                                itemName = item.getItemName();
                            }
                        } catch (IOException e) {
                            System.err.println("Error fetching item name for order " + orderId + ": " + e.getMessage());
                        }


                        PurchaseOrder purchaseOrder = new PurchaseOrder(
                                orderId, requisitionId, itemCode, itemName, quantity, unitPrice, totalAmount,
                                orderDate, expectedDeliveryDate, supplierId, purchaseManagerId, status
                        );

                        purchaseOrders.add(purchaseOrder);
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.err.println("Skipping invalid purchase order line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed purchase order line: " + line);
                }
            }
        }
        return purchaseOrders;
    }

    public PurchaseOrder getPurchaseOrderById(String orderId) throws IOException {
        List<PurchaseOrder> purchaseOrders = getAllPurchaseOrders();
        for (PurchaseOrder order : purchaseOrders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public void updatePurchaseOrder(PurchaseOrder order) throws IOException {
        if (order == null || !order.validateData()) {
            throw new IllegalArgumentException("Invalid purchase order data");
        }

        List<PurchaseOrder> orders = getAllPurchaseOrders();
        boolean found = false;

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                orders.set(i, order);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Purchase order not found: " + order.getOrderId());
        }

        writePurchaseOrdersToFile(orders);
    }

    public void addPurchaseOrder(PurchaseOrder order) throws IOException {
        if (order == null || !order.validateData()) {
            throw new IllegalArgumentException("Invalid purchase order data");
        }

        List<PurchaseOrder> orders = getAllPurchaseOrders();

        for (PurchaseOrder existing : orders) {
            if (existing.getOrderId().equals(order.getOrderId())) {
                throw new IllegalArgumentException("Purchase order ID already exists: " + order.getOrderId());
            }
        }

        orders.add(order);
        writePurchaseOrdersToFile(orders);
    }

    public void deletePurchaseOrder(String orderId) throws IOException {
        List<PurchaseOrder> orders = getAllPurchaseOrders();
        boolean removed = false;

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(orderId)) {
                orders.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Purchase order not found: " + orderId);
        }

        writePurchaseOrdersToFile(orders);
    }

    private void writePurchaseOrdersToFile(List<PurchaseOrder> orders) throws IOException {
        File file = new File(PURCHASE_ORDERS_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("orderId,requisitionId,itemCode,quantity,unitPrice,totalAmount,orderDate,expectedDeliveryDate,supplierId,purchaseManagerId,status");
            writer.newLine();

            for (PurchaseOrder order : orders) {
                writer.write(String.format("%s,%s,%s,%d,%.2f,%.2f,%s,%s,%s,%s,%s",
                        order.getOrderId(),
                        order.getRequisitionId(),
                        order.getItemCode(),
                        order.getQuantity(),
                        order.getUnitPrice(),
                        order.getTotalAmount(),
                        order.getOrderDate().format(DATE_FORMATTER),
                        order.getExpectedDeliveryDate().format(DATE_FORMATTER),
                        order.getSupplierId(),
                        order.getPurchaseManagerId(),
                        order.getStatus()
                ));
                writer.newLine();
            }
        }
    }

    // --- Stock Methods ---
    public List<Stock> getAllStock() throws IOException {
        List<Stock> stockList = new ArrayList<>();
        File file = new File(STOCK_FILE);

        if (!file.exists()) {
            writeStockToFile(stockList);
            return stockList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        String itemCode = parts[0];
                        String itemName = parts[1];
                        int quantity = Integer.parseInt(parts[2]);
                        String location = parts[3];
                        String lastUpdated = parts[4];
                        String status = parts[5];

                        Stock stock = new Stock(itemCode, itemName, quantity, location, lastUpdated, status);
                        stockList.add(stock);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid stock line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed stock line: " + line);
                }
            }
        }
        return stockList;
    }

    public void updateStock(Stock stock) throws IOException {
        if (stock == null || !stock.validateData()) {
            throw new IllegalArgumentException("Invalid stock data");
        }

        List<Stock> stockList = getAllStock();
        boolean found = false;

        for (int i = 0; i < stockList.size(); i++) {
            if (stockList.get(i).getItemCode().equals(stock.getItemCode())) {
                stockList.set(i, stock);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Stock item not found: " + stock.getItemCode());
        }

        writeStockToFile(stockList);
    }

    private void writeStockToFile(List<Stock> stockList) throws IOException {
        File file = new File(STOCK_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("itemCode,itemName,quantity,location,lastUpdated,status");
            writer.newLine();

            for (Stock stock : stockList) {
                writer.write(String.format("%s,%s,%d,%s,%s,%s",
                        stock.getItemCode(),
                        stock.getItemName(),
                        stock.getQuantity(),
                        stock.getLocation(),
                        stock.getLastUpdated(),
                        stock.getStatus()
                ));
                writer.newLine();
            }
        }
    }


    // --- Financial Methods ---
    public List<Financial> getAllFinancialReports() throws IOException {
        List<Financial> financialList = new ArrayList<>();
        File file = new File(FINANCIAL_FILE);

        if (!file.exists()) {
            writeFinancialToFile(financialList);
            return financialList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    try {
                        String orderId = parts[0];
                        String itemCode = parts[1];
                        String itemName = parts[2];
                        int quantity = Integer.parseInt(parts[3]);
                        double unitPrice = Double.parseDouble(parts[4]);
                        double totalAmount = Double.parseDouble(parts[5]);
                        String orderDate = parts[6];
                        String supplierId = parts[7];
                        String purchaseManagerId = parts[8];
                        String status = parts[9];

                        Financial financial = new Financial(
                                orderId, itemCode, itemName, quantity, unitPrice, totalAmount,
                                orderDate, supplierId, purchaseManagerId, status
                        );
                        financialList.add(financial);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid financial line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed financial line: " + line);
                }
            }
        }
        return financialList;
    }

    public void updateFinancial(Financial financial) throws IOException {
        if (financial == null || !financial.validateData()) {
            throw new IllegalArgumentException("Invalid financial data");
        }

        List<Financial> financialList = getAllFinancialReports();
        boolean found = false;

        for (int i = 0; i < financialList.size(); i++) {
            if (financialList.get(i).getOrderId().equals(financial.getOrderId())) {
                financialList.set(i, financial);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Financial record not found: " + financial.getOrderId());
        }

        writeFinancialToFile(financialList);
    }

    private void writeFinancialToFile(List<Financial> financialList) throws IOException {
        File file = new File(FINANCIAL_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("orderId,itemCode,itemName,quantity,unitPrice,totalAmount,orderDate,supplierId,purchaseManagerId,status");
            writer.newLine();

            for (Financial financial : financialList) {
                writer.write(String.format("%s,%s,%s,%d,%.2f,%.2f,%s,%s,%s,%s",
                        financial.getOrderId(),
                        financial.getItemCode(),
                        financial.getItemName(),
                        financial.getQuantity(),
                        financial.getUnitPrice(),
                        financial.getTotalAmount(),
                        financial.getOrderDate(),
                        financial.getSupplierId(),
                        financial.getPurchaseManagerId(),
                        financial.getStatus()
                ));
                writer.newLine();
            }
        }
    }

    // --- System Log Methods ---
    public List<SystemLog> getAllSystemLogs() throws IOException {
        List<SystemLog> logs = new ArrayList<>();
        File file = new File(SYSTEM_LOGS_FILE);

        if (!file.exists()) {
            writeSystemLogsToFile(logs);
            return logs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    try {
                        String logId = parts[0];
                        String userId = parts[1];
                        String username = parts[2];
                        String action = parts[3];
                        String details = parts[4];
                        LocalDateTime timestamp = LocalDateTime.parse(parts[5], LOG_DATE_FORMATTER);
                        String userRole = parts[6];

                        SystemLog log = new SystemLog(logId, userId, username, action, details, timestamp, userRole);
                        logs.add(log);
                    } catch (DateTimeParseException e) {
                        System.err.println("Skipping invalid system log line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed system log line: " + line);
                }
            }
        }
        return logs;
    }

    public void addSystemLog(SystemLog log) throws IOException {
        if (log == null || !log.validateData()) {
            throw new IllegalArgumentException("Invalid log data");
        }

        List<SystemLog> logs = getAllSystemLogs();
        logs.add(log);
        writeSystemLogsToFile(logs);
    }

    private void writeSystemLogsToFile(List<SystemLog> logs) throws IOException {
        File file = new File(SYSTEM_LOGS_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("logId,userId,username,action,details,timestamp,userRole");
            writer.newLine();

            for (SystemLog log : logs) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                        log.getLogId(),
                        log.getUserId(),
                        log.getUsername(),
                        log.getAction(),
                        log.getDetails(),
                        log.getTimestamp().format(LOG_DATE_FORMATTER),
                        log.getUserRole()
                ));
                writer.newLine();
            }
        }
    }

    // --- Sales Entry Methods ---
    public List<SalesEntry> getAllSalesEntries() throws IOException {
        List<SalesEntry> salesEntries = new ArrayList<>();
        File file = new File(SALES_ENTRY_FILE);

        if (!file.exists()) {
            writeSalesEntriesToFile(salesEntries);
            return salesEntries;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    try {
                        String entryId = parts[0];
                        LocalDate date = LocalDate.parse(parts[1], DATE_FORMATTER);
                        String itemId = parts[2];
                        String itemName = parts[3];
                        int quantity = Integer.parseInt(parts[4]);
                        String category = parts[5];
                        double pricePerUnit = Double.parseDouble(parts[6]);
                        double totalPrice = Double.parseDouble(parts[7]);
                        String salesManagerId = parts[8];

                        SalesEntry salesEntry = new SalesEntry(
                                entryId, date, itemId, itemName, quantity,
                                category, pricePerUnit, totalPrice, salesManagerId
                        );
                        salesEntries.add(salesEntry);
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.err.println("Skipping invalid sales entry line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed sales entry line: " + line);
                }
            }
        }
        return salesEntries;
    }

    public void addSalesEntry(SalesEntry salesEntry) throws IOException {
        if (salesEntry == null || !salesEntry.validateData()) {
            throw new IllegalArgumentException("Invalid sales entry data");
        }

        List<SalesEntry> salesEntries = getAllSalesEntries();
        salesEntries.add(salesEntry);
        writeSalesEntriesToFile(salesEntries);
    }

    public void updateSalesEntry(SalesEntry updatedSalesEntry) throws IOException {
        if (updatedSalesEntry == null || !updatedSalesEntry.validateData()) {
            throw new IllegalArgumentException("Invalid sales entry data");
        }

        List<SalesEntry> salesEntries = getAllSalesEntries();
        boolean found = false;

        for (int i = 0; i < salesEntries.size(); i++) {
            if (salesEntries.get(i).getEntryId().equals(updatedSalesEntry.getEntryId())) {
                salesEntries.set(i, updatedSalesEntry);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Sales entry not found: " + updatedSalesEntry.getEntryId());
        }

        writeSalesEntriesToFile(salesEntries);
    }

    public void deleteSalesEntry(String entryId) throws IOException {
        List<SalesEntry> salesEntries = getAllSalesEntries();
        boolean removed = false;

        for (int i = 0; i < salesEntries.size(); i++) {
            if (salesEntries.get(i).getEntryId().equals(entryId)) {
                salesEntries.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Sales entry not found: " + entryId);
        }

        writeSalesEntriesToFile(salesEntries);
    }


    private void writeSalesEntriesToFile(List<SalesEntry> salesEntries) throws IOException {
        File file = new File(SALES_ENTRY_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("entryId,date,itemId,itemName,quantity,category,pricePerUnit,totalPrice,salesManagerId");
            writer.newLine();

            for (SalesEntry entry : salesEntries) {
                writer.write(String.format("%s,%s,%s,%s,%d,%s,%.2f,%.2f,%s",
                        entry.getEntryId(),
                        entry.getDate().format(DATE_FORMATTER),
                        entry.getItemId(),
                        entry.getItemName() != null ? entry.getItemName() : "",
                        entry.getQuantity(),
                        entry.getCategory() != null ? entry.getCategory() : "",
                        entry.getPricePerUnit(),
                        entry.getTotalPrice(),
                        entry.getSalesManagerId()
                ));
                writer.newLine();
            }
        }
    }

    // --- Supplier Methods ---
    public List<Supplier> getAllSuppliers() throws IOException {
        List<Supplier> suppliers = new ArrayList<>();
        File file = new File(SUPPLIERS_FILE);

        if (!file.exists()) {
            writeSuppliersToFile(suppliers);
            return suppliers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                // Expecting at least 9 parts: id, name, contactPerson, contactNumber, email, address, suppliedItems, lastOrderDate, status
                if (parts.length >= 9) {
                    try {
                        String supplierId = parts[0];
                        String supplierName = parts[1];
                        String contactPerson = parts[2];
                        String contactNumber = parts[3];
                        String email = parts[4];

                        // Reconstruct the address field - it's the parts between index 5 and (length - 3)
                        StringBuilder addressBuilder = new StringBuilder();
                        for (int i = 5; i < parts.length - 3; i++) {
                            addressBuilder.append(parts[i]);
                            if (i < parts.length - 4) {
                                addressBuilder.append(",");
                            }
                        }
                        String address = addressBuilder.toString();


                        String suppliedItems = parts[parts.length - 3];
                        String lastOrderDateStr = parts[parts.length - 2];
                        String status = parts[parts.length - 1];

                        LocalDate lastOrderDate = null;
                        try {
                            // Trim any leading/trailing whitespace from the date string
                            lastOrderDate = LocalDate.parse(lastOrderDateStr.trim(), DATE_FORMATTER);
                        } catch (DateTimeParseException e) {
                            System.err.println("Skipping invalid supplier line (date format): " + line + " - " + e.getMessage());
                            continue; // Skip this line if date is invalid
                        }


                        Supplier supplier = new Supplier(
                                supplierId, supplierName, contactPerson, contactNumber, email,
                                address, suppliedItems, lastOrderDate, status
                        );
                        suppliers.add(supplier);
                    } catch (Exception e) {
                        System.err.println("Skipping malformed supplier line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed supplier line (incorrect number of fields): " + line);
                }
            }
        }
        return suppliers;
    }

    public Supplier getSupplierById(String supplierId) throws IOException {
        List<Supplier> suppliers = getAllSuppliers();
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierId().equals(supplierId)) {
                return supplier;
            }
        }
        return null;
    }

    public void addSupplier(Supplier supplier) throws IOException {
        if (supplier == null || !supplier.validateData()) {
            throw new IllegalArgumentException("Invalid supplier data");
        }

        List<Supplier> suppliers = getAllSuppliers();

        for (Supplier existing : suppliers) {
            if (existing.getSupplierId().equals(supplier.getSupplierId())) {
                throw new IllegalArgumentException("Supplier ID already exists: " + supplier.getSupplierId());
            }
        }
        if (supplier.getSupplierId() == null || supplier.getSupplierId().isEmpty() || !supplier.getSupplierId().startsWith("SUP")) {
            supplier.setSupplierId(generateSupplierId());
        }


        suppliers.add(supplier);
        writeSuppliersToFile(suppliers);
    }

    private String generateSupplierId() throws IOException {
        List<Supplier> suppliers = getAllSuppliers();
        int maxId = 0;
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierId() != null && supplier.getSupplierId().startsWith("SUP")) {
                try {
                    int id = Integer.parseInt(supplier.getSupplierId().substring(3));
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore suppliers with non-standard IDs
                }
            }
        }
        return "SUP" + String.format("%03d", maxId + 1);
    }


    public void updateSupplier(Supplier updatedSupplier) throws IOException {
        if (updatedSupplier == null || !updatedSupplier.validateData()) {
            throw new IllegalArgumentException("Invalid supplier data");
        }

        List<Supplier> suppliers = getAllSuppliers();
        boolean found = false;

        for (int i = 0; i < suppliers.size(); i++) {
            if (suppliers.get(i).getSupplierId().equals(updatedSupplier.getSupplierId())) {
                suppliers.set(i, updatedSupplier);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Supplier not found: " + updatedSupplier.getSupplierId());
        }

        writeSuppliersToFile(suppliers);
    }

    public void deleteSupplier(String supplierId) throws IOException {
        List<Supplier> suppliers = getAllSuppliers();
        boolean removed = false;

        for (int i = 0; i < suppliers.size(); i++) {
            if (suppliers.get(i).getSupplierId().equals(supplierId)) {
                suppliers.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Supplier not found: " + supplierId);
        }

        writeSuppliersToFile(suppliers);
    }


    private void writeSuppliersToFile(List<Supplier> suppliers) throws IOException {
        File file = new File(SUPPLIERS_FILE);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("supplierId,supplierName,contactPerson,contactNumber,email,address,suppliedItems,lastOrderDate,status");
            writer.newLine();

            for (Supplier supplier : suppliers) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        supplier.getSupplierId(),
                        supplier.getSupplierName(),
                        supplier.getContactPerson() != null ? supplier.getContactPerson() : "",
                        supplier.getContactNumber() != null ? supplier.getContactNumber() : "",
                        supplier.getEmail() != null ? supplier.getEmail() : "",
                        supplier.getAddress() != null ? supplier.getAddress() : "",
                        supplier.getSuppliedItems() != null ? supplier.getSuppliedItems() : "",
                        supplier.getLastOrderDate() != null ? supplier.getLastOrderDate().format(DATE_FORMATTER) : "",
                        supplier.getStatus() != null ? supplier.getStatus() : ""
                ));
                writer.newLine();
            }
        }
    }

}