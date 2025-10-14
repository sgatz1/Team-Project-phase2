package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.util.List;

public class UserLoginPage {
    private DatabaseHelper db;

    public UserLoginPage(DatabaseHelper db) {
        this.db = db;
    }

    public void show(Stage stage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label message = new Label();

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> {
            String uname = usernameField.getText();
            String pw = passwordField.getText();

            if (db.login(uname, pw)) {
                try {
                    List<String> roles = db.getUserRoles(uname);
                    System.out.println("DEBUG roles for " + uname + ": " + roles);
                    if (roles.size() == 1) {
                        String role = roles.get(0);
                        if (role.equalsIgnoreCase("admin")) {
                            new AdminHomePage(db).show(stage);
                        } else {
                            //   this redirects us internally (db side)
                            new MockRolePage(stage, db, uname, role);
                        }
                    } else {
                        new RoleSelectionPage(db, uname).show(stage);
                    }
                } catch (Exception ex) {
                    message.setText("Error loading roles: " + ex.getMessage());
                }
            } else {
                message.setText("Invalid username or password.");
            }
        });

        // new forgot password button
        Button forgotBtn = new Button("Forgot Password?");
        forgotBtn.setOnAction(e -> {
            ForgotPasswordPage fp = new ForgotPasswordPage(db);
            fp.show(stage);
        });

        root.getChildren().addAll(usernameField, passwordField, loginBtn, forgotBtn, message);

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Login");
        stage.show();
    }
}
