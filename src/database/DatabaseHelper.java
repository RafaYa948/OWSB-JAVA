package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import models.Item;
import models.PurchaseOrder;
import models.PurchaseRequisition;
import models.User;

public class DatabaseHelper {
    private static final String USERS_FILE = "src/database/user.txt";
    private static final String ITEMS_FILE = "src/database/item.txt";
    private static final String REQUISITIONS_FILE = "src/database/purchase_requisition.txt";
    private static final String PURCHASE_ORDERS_FILE = "src/database/purchase_order.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public DatabaseHelper() {
        createDataDirectoryIfNeeded();
    }
    
    private void createDataDirectoryIfNeeded() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }
    
    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        
        if (!file.exists()) {
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

    public void createItem(Item item) throws IOException {
        addItem(item);
    }

    public User validateUser(String username, String password) throws IOException {
        return authenticate(username, password);
    }

    public void registerUser(User user) throws IOException {
        String userId = generateUserId();
        user.setUserId(userId);
        addUser(user);
    }

    private String generateUserId() throws IOException {
        List<User> users = getAllUsers();
        int maxId = 0;
        for (User user : users) {
            try {
                int id = Integer.parseInt(user.getUserId().substring(1));
                maxId = Math.max(maxId, id);
            } catch (NumberFormatException e) {}
        }
        return "U" + String.format("%03d", maxId + 1);
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
    
    public User authenticate(String username, String password) throws IOException {
        List<User> users = getAllUsers();
        
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        
        return null;
    }
    
    public void addUser(User user) throws IOException {
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
        
        users.add(user);
        writeUsersToFile(users);
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
    
    public List<Item> getAllItems() throws IOException {
        List<Item> items = new ArrayList<>();
        File file = new File(ITEMS_FILE);
        
        if (!file.exists()) {
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
    
    public List<PurchaseRequisition> getAllPurchaseRequisitions() throws IOException {
        List<PurchaseRequisition> requisitions = new ArrayList<>();
        File file = new File(REQUISITIONS_FILE);
        
        if (!file.exists()) {
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
                        } catch (Exception e) {
                        }
                    }
                    
                    PurchaseRequisition requisition = new PurchaseRequisition(
                        requisitionId, itemCode, itemName, quantity, requiredDate, salesManagerId, status
                    );
                    
                    requisitions.add(requisition);
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
    
    public List<PurchaseOrder> getAllPurchaseOrders() throws IOException {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        File file = new File(PURCHASE_ORDERS_FILE);
        
        if (!file.exists()) {
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
                    } catch (Exception e) {
                    }
                    
                    PurchaseOrder purchaseOrder = new PurchaseOrder(
                        orderId, requisitionId, itemCode, itemName, quantity, unitPrice, totalAmount,
                        orderDate, expectedDeliveryDate, supplierId, purchaseManagerId, status
                    );
                    
                    purchaseOrders.add(purchaseOrder);
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
}