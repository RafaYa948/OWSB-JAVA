package models;

public class User {
    private String userId;
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String role;

    public static final String ROLE_INVENTORY_MANAGER = "INV";
    public static final String ROLE_PURCHASE_MANAGER = "PUR";
    public static final String ROLE_FINANCE_MANAGER = "FIN";
    public static final String ROLE_SALES_MANAGER = "SAL";
    public static final String ROLE_ADMINISTRATOR = "ADMN";

    public User() {}

    public User(String userId, String username, String password, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one number and one special character");
        }
        this.password = password;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':"\\\\|,.<>/?].*");
        return hasNumber && hasSpecial;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role.equals(ROLE_INVENTORY_MANAGER) ||
            role.equals(ROLE_PURCHASE_MANAGER) ||
            role.equals(ROLE_FINANCE_MANAGER) ||
            role.equals(ROLE_SALES_MANAGER) ||
            role.equals(ROLE_ADMINISTRATOR)) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    public boolean validatePassword() {
        return password != null && confirmPassword != null && password.equals(confirmPassword);
    }
}