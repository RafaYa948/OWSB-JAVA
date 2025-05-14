package models;

import java.time.LocalDateTime;

public class SystemLog {
    private String logId;
    private String userId;
    private String username;
    private String action;
    private String details;
    private LocalDateTime timestamp;
    private String userRole;

    public static final String ACTION_LOGIN = "Login";
    public static final String ACTION_LOGOUT = "Logout";
    public static final String ACTION_CREATE = "Create";
    public static final String ACTION_UPDATE = "Update";
    public static final String ACTION_DELETE = "Delete";
    public static final String ACTION_VIEW = "View";

    public SystemLog() {
    }

    public SystemLog(String logId, String userId, String username, String action,
                     String details, String timestamp, String userRole) {
        this.logId = logId;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.parse(timestamp);
        this.userRole = userRole;
    }

    public SystemLog(String logId, String userId, String username, String action,
                     String details, LocalDateTime timestamp, String userRole) {
        this.logId = logId;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
        this.userRole = userRole;
    }

    // Getters and Setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public boolean validateData() {
        return logId != null && !logId.trim().isEmpty() &&
                userId != null && !userId.trim().isEmpty() &&
                username != null && !username.trim().isEmpty() &&
                action != null && !action.trim().isEmpty() &&
                timestamp != null &&
                userRole != null && !userRole.trim().isEmpty();
    }
}