package application;

/**
 * This is a simple class to hold user data.
 */
public class User {

    // basic info for a user
    private String userName;
    private String password;
    private String email;

    // make a user object
    public User(String u, String p, String e) {
        userName = u;
        password = p;
        email = e;
    }

    // get methods (read info)
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    // set methods (change info)
    public void setEmail(String e) {
        email = e;
    }
}
