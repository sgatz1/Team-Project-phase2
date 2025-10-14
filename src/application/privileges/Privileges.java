package application.privileges;

import databasePart1.DatabaseHelper;
import java.util.List;

/**
 * The Privileges class is responsible for managing user permissions and access levels.
 * It checks which roles a user has (e.g., admin, staff, instructor)
 * and determines whether they can perform certain actions within the system.
 */

public class Privileges {

    // Reference to a database helper for retrieving user information
    private final DatabaseHelper db;

    // The username of the current logged-in user
    private final String username;

    // A list of role names (e.g., "admin", "staff", "teacher") assigned to the user
    private final List<String> roles;

    /**
     * Constructor initializes the Privileges class with user data from the database.
     *
     * @param db        DatabaseHelper instance used to fetch user roles.
     * @param username  The username of the current user.
     */

    public Privileges(DatabaseHelper db, String username) {
        this.db = db;
        this.username = username;
        this.roles = db.getUserRoles(username);
    }

    /**
     * Helper method to check if the user has any of the specified roles.
     *
     * @param roleNames Variable-length list of role names to check against.
     * @return true if the user has at least one of the provided roles, false otherwise.
     */

    private boolean hasRole(String... roleNames) {
        if (roles == null) return false; // If no roles are assigned, return false
        for (String userRole : roles) {
            if (userRole == null) continue; // Skip null entries
            String normalized = userRole.trim().toLowerCase();
            for (String target : roleNames) {
                // Check for exact match or partial match (e.g., "administrator" //contains "admin")
                if (normalized.equals(target.toLowerCase()) ||
                        normalized.contains(target.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the user has administrator privileges.
     *
     * @return true if the user is an admin or administrator.
     */

    public boolean isAdmin() {
        return hasRole("admin", "administrator");
    }

    /**
     * Checks if the user has staff privileges.
     *
     * @return true if the user is staff.
     */

    public boolean isStaff() {
        return hasRole("staff");
    }

    /**
     * Checks if the user is an instructor or teacher.
     *
     * @return true if the user is an instructor or teacher.
     */

    public boolean isInstructor() {
        return hasRole("instructor", "teacher");
    }

    /**
     * Checks if the user is any type of privileged user (admin, staff, or instructor).
     *
     * @return true if the user has elevated privileges.
     */

    public boolean isPrivilegedUser() {
        return isAdmin() || isStaff() || isInstructor();
    }

    /**
     * Determines whether the user can modify a question.
     * Privileged users can always modify; regular users can only modify their own questions.
     *
     * @param author The username of the person who created the question.
     * @return true if modification is allowed.
     */

    public boolean canModifyQuestion(String author) {
        return isPrivilegedUser() || author.equalsIgnoreCase(username);
    }

    /**
     * Determines whether the user can delete a reply.
     * Privileged users can delete any reply; regular users can delete only their own.
     *
     * @param author The username of the person who posted the reply.
     * @return true if deletion is allowed.
     */

    public boolean canDeleteReply(String author) {
        return isPrivilegedUser() || author.equalsIgnoreCase(username);
    }

    /**
     * Determines whether the user can mark a response as accepted.
     * Only privileged users have this ability.
     *
     * @return true if the user can mark responses as accepted.
     */

    public boolean canMarkAccepted() {
        return isPrivilegedUser();
    }
}
