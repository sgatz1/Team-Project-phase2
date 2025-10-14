package application.privileges;

import databasePart1.DatabaseHelper;
import java.util.List;

public class Privileges {

    private final DatabaseHelper db;
    private final String username;
    private final List<String> roles;

    public Privileges(DatabaseHelper db, String username) {
        this.db = db;
        this.username = username;
        this.roles = db.getUserRoles(username);
    }

    private boolean hasRole(String... roleNames) {
        if (roles == null) return false;
        for (String userRole : roles) {
            if (userRole == null) continue;
            String normalized = userRole.trim().toLowerCase();
            for (String target : roleNames) {
                if (normalized.equals(target.toLowerCase()) ||
                        normalized.contains(target.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return hasRole("admin", "administrator");
    }

    public boolean isStaff() {
        return hasRole("staff");
    }

    public boolean isInstructor() {
        return hasRole("instructor", "teacher");
    }

    public boolean isPrivilegedUser() {
        return isAdmin() || isStaff() || isInstructor();
    }

    public boolean canModifyQuestion(String author) {
        return isPrivilegedUser() || author.equalsIgnoreCase(username);
    }

    public boolean canDeleteReply(String author) {
        return isPrivilegedUser() || author.equalsIgnoreCase(username);
    }

    public boolean canMarkAccepted() {
        return isPrivilegedUser();
    }
}
