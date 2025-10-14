// The db we are using ensure it is cross-platform available and does not need excessive downloads to work,
// it locally loads everything and we can simply clean it for testing.
// User Table is attached to a live db view for admins

package databasePart1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// this class is for handling the database
public class DatabaseHelper {
    private Connection conn;

    public DatabaseHelper() {
        try {
            // load h2 database driver
            Class.forName("org.h2.Driver");

            // use a local, cross-platform relative path so it works anywhere
            // this will create a "database" folder inside your project directory automatically
            String dbPath = "./database/cse360db";

            conn = DriverManager.getConnection(
                    "jdbc:h2:file:" + dbPath + ";IFEXISTS=FALSE;AUTO_SERVER=TRUE;LOCK_TIMEOUT=10000",
                    "sa",
                    "Password"
            );
            System.out.println(" Using database at: " + new java.io.File(dbPath).getAbsolutePath());

            // ensure auto commit enabled for safety
            conn.setAutoCommit(true);

            // make tables if they dont exist
            initializeTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // helper to force commit changes
    public void saveChanges() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // creates the tables if missing
    private void initializeTables() throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS USERS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY," +
                "USERNAME VARCHAR(255) UNIQUE," +
                "PASSWORD VARCHAR(255)," +
                "EMAIL VARCHAR(255))");

        stmt.execute("CREATE TABLE IF NOT EXISTS ROLES (" +
                "USERNAME VARCHAR(255)," +
                "ROLE VARCHAR(255))");

        stmt.execute("CREATE TABLE IF NOT EXISTS INVITATIONS (" +
                "CODE VARCHAR(255) PRIMARY KEY," +
                "USED BOOLEAN DEFAULT FALSE)");

        // q&a tables for persistent questions and answers
        stmt.execute("CREATE TABLE IF NOT EXISTS QUESTIONS (" +
                "QID INT AUTO_INCREMENT PRIMARY KEY," +
                "USERNAME VARCHAR(255)," +
                "TITLE VARCHAR(255)," +
                "BODY VARCHAR(1000)," +
                "CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS ANSWERS (" +
                "AID INT AUTO_INCREMENT PRIMARY KEY," +
                "QID INT," +
                "USERNAME VARCHAR(255)," +
                "BODY VARCHAR(1000)," +
                "IS_ACCEPTED BOOLEAN DEFAULT FALSE," +
                "CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.close();
    }

    // add a new user
    public boolean register(String username, String password, String email) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO USERS (USERNAME, PASSWORD, EMAIL) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.executeUpdate();
            saveChanges();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // check login
    public boolean login(String username, String password) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM USERS WHERE USERNAME = ? AND PASSWORD = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            boolean ok = rs.next();
            rs.close();
            return ok;
        } catch (SQLException e) {
            return false;
        }
    }

    // give a user a role
    public void addRole(String username, String role) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ROLES (USERNAME, ROLE) VALUES (?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, role);
            ps.executeUpdate();
            saveChanges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // get roles for a user
    public List<String> getUserRoles(String username) {
        List<String> roles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ROLE FROM ROLES WHERE USERNAME = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("ROLE"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    // remove a user and their roles
    public void deleteUser(String username) {
        try {
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM ROLES WHERE USERNAME = ?")) {
                ps1.setString(1, username);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM USERS WHERE USERNAME = ?")) {
                ps2.setString(1, username);
                ps2.executeUpdate();
            }
            saveChanges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // safer version of getAllUsers
    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        try {
            PreparedStatement psUsers = conn.prepareStatement("SELECT USERNAME, EMAIL FROM USERS");
            ResultSet rsUsers = psUsers.executeQuery();

            while (rsUsers.next()) {
                String uname = rsUsers.getString("USERNAME");
                String email = rsUsers.getString("EMAIL");

                PreparedStatement psRoles = conn.prepareStatement("SELECT ROLE FROM ROLES WHERE USERNAME = ?");
                psRoles.setString(1, uname);
                ResultSet rsRoles = psRoles.executeQuery();

                List<String> roleList = new ArrayList<>();
                while (rsRoles.next()) {
                    roleList.add(rsRoles.getString("ROLE"));
                }
                rsRoles.close();
                psRoles.close();

                String roles = roleList.isEmpty() ? "(none)" : String.join(",", roleList);

                String name;
                if (email != null && email.contains("@")) {
                    name = email.substring(0, email.indexOf("@"));
                } else {
                    name = "(not set)";
                }

                users.add(new String[]{uname, name, email == null ? "" : email, roles});
            }

            rsUsers.close();
            psUsers.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // make a random invite code
    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 8);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO INVITATIONS (CODE, USED) VALUES (?, FALSE)")) {
            ps.setString(1, code);
            ps.executeUpdate();
            saveChanges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    // check if an invite code is valid
    public boolean validateInvitationCode(String code) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM INVITATIONS WHERE CODE = ? AND USED = FALSE")) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            boolean valid = rs.next();
            rs.close();
            if (valid) {
                try (PreparedStatement upd = conn.prepareStatement(
                        "UPDATE INVITATIONS SET USED = TRUE WHERE CODE = ?")) {
                    upd.setString(1, code);
                    upd.executeUpdate();
                    saveChanges();
                }
            }
            return valid;
        } catch (SQLException e) {
            return false;
        }
    }

    // reset password
    public boolean resetPassword(String username, String newPass) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ?")) {
            ps.setString(1, newPass);
            ps.setString(2, username);
            boolean ok = ps.executeUpdate() > 0;
            saveChanges();
            return ok;
        } catch (SQLException e) {
            return false;
        }
    }

    // get user email
    public String getUserEmail(String username) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT EMAIL FROM USERS WHERE USERNAME = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            String email = null;
            if (rs.next()) {
                email = rs.getString("EMAIL");
            }
            rs.close();
            return email;
        } catch (SQLException e) {
            return null;
        }
    }

    // update user email
    public boolean updateUserEmail(String username, String newEmail) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE USERS SET EMAIL = ? WHERE USERNAME = ?")) {
            ps.setString(1, newEmail);
            ps.setString(2, username);
            boolean ok = ps.executeUpdate() > 0;
            saveChanges();
            return ok;
        } catch (SQLException e) {
            return false;
        }
    }

    // check if db empty
    public boolean isDatabaseEmpty() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS CNT FROM USERS")) {
            return rs.next() && rs.getInt("CNT") == 0;
        } catch (SQLException e) {
            return true;
        }
    }

    // update user role
    public boolean updateUserRole(String username, String role) {
        try {
            PreparedStatement del = conn.prepareStatement("DELETE FROM ROLES WHERE USERNAME = ?");
            del.setString(1, username);
            del.executeUpdate();
            del.close();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO ROLES (USERNAME, ROLE) VALUES (?, ?)");
            ps.setString(1, username);
            ps.setString(2, role);
            ps.executeUpdate();
            ps.close();

            saveChanges();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Q&A section methods
    public boolean addQuestion(String username, String title, String body) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO QUESTIONS (USERNAME, TITLE, BODY) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.executeUpdate();
            saveChanges();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getAllQuestions() {
        List<String[]> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT QID, USERNAME, TITLE, BODY, CREATED_AT FROM QUESTIONS ORDER BY QID DESC")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("QID"),
                        rs.getString("USERNAME"),
                        rs.getString("TITLE"),
                        rs.getString("BODY"),
                        rs.getString("CREATED_AT")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> getQuestionsByUser(String username) {
        List<String[]> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT QID, USERNAME, TITLE, BODY, CREATED_AT FROM QUESTIONS WHERE USERNAME = ? ORDER BY QID DESC")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("QID"),
                        rs.getString("USERNAME"),
                        rs.getString("TITLE"),
                        rs.getString("BODY"),
                        rs.getString("CREATED_AT")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addAnswer(int qid, String username, String body) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ANSWERS (QID, USERNAME, BODY) VALUES (?, ?, ?)")) {
            ps.setInt(1, qid);
            ps.setString(2, username);
            ps.setString(3, body);
            ps.executeUpdate();
            saveChanges();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getAnswers(int qid) {
        List<String[]> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT AID, USERNAME, BODY, IS_ACCEPTED, CREATED_AT FROM ANSWERS WHERE QID = ? ORDER BY AID ASC")) {
            ps.setInt(1, qid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("AID"),
                        rs.getString("USERNAME"),
                        rs.getString("BODY"),
                        rs.getString("IS_ACCEPTED"),
                        rs.getString("CREATED_AT")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateQuestion(int qid, String newTitle, String newBody) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE QUESTIONS SET TITLE = ?, BODY = ? WHERE QID = ?")) {
            ps.setString(1, newTitle);
            ps.setString(2, newBody);
            ps.setInt(3, qid);
            boolean ok = ps.executeUpdate() > 0;
            saveChanges();
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuestion(int qid) {
        try {
            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM ANSWERS WHERE QID = ?");
            ps1.setInt(1, qid);
            ps1.executeUpdate();
            ps1.close();

            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM QUESTIONS WHERE QID = ?");
            ps2.setInt(1, qid);
            ps2.executeUpdate();
            ps2.close();

            saveChanges();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete an answer by AID
    public boolean deleteAnswer(int aid) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ANSWERS WHERE AID = ?")) {
            ps.setInt(1, aid);
            ps.executeUpdate();
            saveChanges();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAnswerAccepted(int aid) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE ANSWERS SET IS_ACCEPTED = TRUE WHERE AID = ?")) {
            ps.setInt(1, aid);
            boolean ok = ps.executeUpdate() > 0;
            saveChanges();
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> searchQuestions(String keyword) {
        List<String[]> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT QID, USERNAME, TITLE, BODY, CREATED_AT FROM QUESTIONS " +
                        "WHERE LOWER(TITLE) LIKE ? OR LOWER(BODY) LIKE ? ORDER BY QID DESC")) {
            String like = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("QID"),
                        rs.getString("USERNAME"),
                        rs.getString("TITLE"),
                        rs.getString("BODY"),
                        rs.getString("CREATED_AT")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
