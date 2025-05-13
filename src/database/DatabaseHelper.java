package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import models.User;
import models.Item;

public class DatabaseHelper {
    private static final String USER_FILE_PATH = "src/database/user.txt";
    private static final String ITEM_FILE_PATH = "src/database/item.txt";
    private static final String CSV_DELIMITER = ",";

    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(USER_FILE_PATH));
        
        for (int i = 1; i < lines.size(); i++) {
            String[] data = lines.get(i).split(CSV_DELIMITER);
            if (data.length == 5) {
                users.add(new User(data[0], data[1], data[2], data[3], data[4]));
            }
        }
        return users;
    }
    
    public List<Item> getAllItems() throws IOException {
        List<Item> items = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(ITEM_FILE_PATH));
        
        for (int i = 1; i < lines.size(); i++) {
            String[] data = lines.get(i).split(CSV_DELIMITER);
            if (data.length == 3) {
                items.add(new Item(data[0], data[1], data[2]));
            }
        }
        return items;
    }

    private String generateNewUserId(String rolePrefix) throws IOException {
        List<User> users = getAllUsers();
        int maxNum = 0;
        for (User user : users) {
            if (user.getUserId().startsWith(rolePrefix)) {
                try {
                    String numPart = user.getUserId().substring(rolePrefix.length());
                    int currentNum = Integer.parseInt(numPart);
                    if (currentNum > maxNum) {
                        maxNum = currentNum;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return rolePrefix + String.format("%03d", maxNum + 1);
    }
    
    private String generateNewItemCode() throws IOException {
        List<Item> items = getAllItems();
        int maxNum = 0;
        String prefix = "ITEM";
        
        for (Item item : items) {
            if (item.getItemCode().startsWith(prefix)) {
                try {
                    String numPart = item.getItemCode().substring(prefix.length());
                    int currentNum = Integer.parseInt(numPart);
                    if (currentNum > maxNum) {
                        maxNum = currentNum;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return prefix + String.format("%03d", maxNum + 1);
    }

    public void registerUser(User user) throws IOException {
        if (!user.validatePassword()) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        String rolePrefix = user.getRole();
        if (rolePrefix == null || rolePrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty for User ID generation.");
        }
        
        String newUserId = generateNewUserId(rolePrefix);
        user.setUserId(newUserId);

        validateUserData(user); 

        List<User> existingUsers = getAllUsers();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            if (existingUser.getEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        String userLine = String.format("%s,%s,%s,%s,%s%n",
            user.getUserId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.getRole());
        Files.write(Paths.get(USER_FILE_PATH), userLine.getBytes(), StandardOpenOption.APPEND);
    }
    
    public void createItem(Item item) throws IOException {
        validateItemData(item);
        
        if (item.getItemCode() == null || item.getItemCode().trim().isEmpty()) {
            item.setItemCode(generateNewItemCode());
        }
        
        List<Item> existingItems = getAllItems();
        for (Item existingItem : existingItems) {
            if (existingItem.getItemCode().equals(item.getItemCode())) {
                throw new IllegalArgumentException("Item Code already exists");
            }
        }

        String itemLine = String.format("%s,%s,%s%n",
            item.getItemCode(),
            item.getItemName(),
            item.getSupplierId());
        Files.write(Paths.get(ITEM_FILE_PATH), itemLine.getBytes(), StandardOpenOption.APPEND);
    }
    
    public void createUser(User user) throws IOException {
        validateUserData(user);
        
        List<User> existingUsers = getAllUsers();
        for (User existingUser : existingUsers) {
            if (existingUser.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("User ID already exists");
            }
            if (existingUser.getUsername().equals(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
             if (existingUser.getEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        String userLine = String.format("%s,%s,%s,%s,%s%n",
            user.getUserId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.getRole());
        Files.write(Paths.get(USER_FILE_PATH), userLine.getBytes(), StandardOpenOption.APPEND);
    }

    public void deleteUser(String userId) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(USER_FILE_PATH));
        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;

        updatedLines.add(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            String[] data = lines.get(i).split(CSV_DELIMITER);
            if (data.length > 0 && !data[0].equals(userId)) {
                updatedLines.add(lines.get(i));
            } else if (data.length > 0 && data[0].equals(userId)) {
                userFound = true;
            } else {
                 updatedLines.add(lines.get(i));
            }
        }

        if (!userFound) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        Files.write(Paths.get(USER_FILE_PATH), updatedLines);
    }
    
    public void deleteItem(String itemCode) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(ITEM_FILE_PATH));
        List<String> updatedLines = new ArrayList<>();
        boolean itemFound = false;

        updatedLines.add(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            String[] data = lines.get(i).split(CSV_DELIMITER);
            if (data.length > 0 && !data[0].equals(itemCode)) {
                updatedLines.add(lines.get(i));
            } else if (data.length > 0 && data[0].equals(itemCode)) {
                itemFound = true;
            } else {
                updatedLines.add(lines.get(i));
            }
        }

        if (!itemFound) {
            throw new IllegalArgumentException("Item not found: " + itemCode);
        }

        Files.write(Paths.get(ITEM_FILE_PATH), updatedLines);
    }

    public User validateUser(String username, String password) throws IOException {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
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
    
    public Item getItemByCode(String itemCode) throws IOException {
        List<Item> items = getAllItems();
        for (Item item : items) {
            if (item.getItemCode().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }

    private void validateUserData(User user) {
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
         if (!user.isValidPassword(user.getPassword())) { 
             throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one number and one special character");
         }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getRole() == null || !isValidRole(user.getRole())) {
            throw new IllegalArgumentException("Invalid role");
        }
    }
    
    private void validateItemData(Item item) {
        if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (item.getSupplierId() == null || item.getSupplierId().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier ID cannot be empty");
        }
    }

    public void updateUser(User user) throws IOException {
        validateUserData(user);
        
        List<String> lines = Files.readAllLines(Paths.get(USER_FILE_PATH));
        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;
    
        updatedLines.add(lines.get(0));
        
        for (int i = 1; i < lines.size(); i++) {
            String[] data = lines.get(i).split(CSV_DELIMITER);
            if (data.length > 0) {
                if (data[0].equals(user.getUserId())) {
                    String userLine = String.format("%s,%s,%s,%s,%s",
                        user.getUserId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getRole());
                    updatedLines.add(userLine);
                    userFound = true;
                } else {
                    updatedLines.add(lines.get(i));
                }
            } else {
                updatedLines.add(lines.get(i));
            }
        }
    
        if (!userFound) {
            throw new IllegalArgumentException("User not found: " + user.getUserId());
        }
    
        Files.write(Paths.get(USER_FILE_PATH), updatedLines);
    }
    
    public void updateItem(Item item) throws IOException {
        validateItemData(item);
        
        List<String> lines = Files.readAllLines(Paths.get(ITEM_FILE_PATH));
        List<String> updatedLines = new ArrayList<>();
        boolean itemFound = false;
    
        updatedLines.add(lines.get(0));
        
        for (int i = 1; i < lines.size(); i++) {
            String[] data = lines.get(i).split(CSV_DELIMITER);
            if (data.length > 0) {
                if (data[0].equals(item.getItemCode())) {
                    String itemLine = String.format("%s,%s,%s",
                        item.getItemCode(),
                        item.getItemName(),
                        item.getSupplierId());
                    updatedLines.add(itemLine);
                    itemFound = true;
                } else {
                    updatedLines.add(lines.get(i));
                }
            } else {
                updatedLines.add(lines.get(i));
            }
        }
    
        if (!itemFound) {
            throw new IllegalArgumentException("Item not found: " + item.getItemCode());
        }
    
        Files.write(Paths.get(ITEM_FILE_PATH), updatedLines);
    }

    private boolean isValidRole(String role) {
        return role.equals(User.ROLE_INVENTORY_MANAGER) ||
               role.equals(User.ROLE_PURCHASE_MANAGER) ||
               role.equals(User.ROLE_FINANCE_MANAGER) ||
               role.equals(User.ROLE_SALES_MANAGER) ||
               role.equals(User.ROLE_ADMINISTRATOR);
    }
}