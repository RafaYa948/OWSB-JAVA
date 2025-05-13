package database;

import models.User;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class DatabaseHelper {
    private static final String USER_FILE_PATH = "src/database/user.txt";
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
            if (!data[0].equals(userId)) {
                updatedLines.add(lines.get(i));
            } else {
                userFound = true;
            }
        }

        if (!userFound) {
            throw new IllegalArgumentException("User not found");
        }

        Files.write(Paths.get(USER_FILE_PATH), updatedLines);
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
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getRole() == null || !isValidRole(user.getRole())) {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    private boolean isValidRole(String role) {
        return role.equals(User.ROLE_INVENTORY_MANAGER) ||
               role.equals(User.ROLE_PURCHASE_MANAGER) ||
               role.equals(User.ROLE_FINANCE_MANAGER) ||
               role.equals(User.ROLE_SALES_MANAGER) ||
               role.equals(User.ROLE_ADMINISTRATOR);
    }
}