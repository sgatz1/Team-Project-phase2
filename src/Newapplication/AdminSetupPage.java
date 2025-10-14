package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// this screen is only used once to make the very first admin
public class AdminSetupPage {

    private DatabaseHelper db; // lets me talk to the database

    public AdminSetupPage(DatabaseHelper d) {
        db = d;
    }

    public void show(Stage stage) {
        // main layout for the page
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment:center; -fx-padding:20;");

        // text and input boxes
        Label lbl = new Label("Set up the initial Admin account");

        TextField txtUser = new TextField();
        txtUser.setPromptText("Enter admin username");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Enter admin password");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Enter admin email");

        Button btnCreate = new Button("Create Admin");
        Label lblStatus = new Label();

        // button makes the admin when clicked
        btnCreate.setOnAction(e -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText().trim();
            String em = txtEmail.getText().trim();

            //Username validation 
            boolean uservalid = false;

            String usernamevalid = UserNameRecognizer.checkForValidUserName(u);
            System.out.println(usernamevalid);

            if (usernamevalid.contains("*** ERROR ***")) {
                System.out.println("Username is Invalid");
            } else {
                System.out.println("Username is Valid");
                uservalid = true;
            }

            
            //Password validation 
            boolean passvalid = false;
            String passwordvalid = PasswordEvaluator.evaluatePassword(p);
            System.out.println(passwordvalid);

            if (passwordvalid.contains("conditions were not satisfied") ||
                    passwordvalid.contains("*** Error *** An invalid character has been found!")) {
                System.out.println("Password is Invalid");
            } else {
                System.out.println("Password is Valid");
                passvalid = true;
            }

        
            // make sure all fields are filled
            if (u.isEmpty() || p.isEmpty() || em.isEmpty()) {
                lblStatus.setText("All fields required.");
                return;
            }
            
            // username and password most both be valid
            if (!uservalid || !passvalid) {
                lblStatus.setText("Username or Password is invalid.");
                return;
            }
            

            // try to add the new admin user
            boolean ok = db.register(u, p, em);
            if (ok) {
                db.addRole(u, "admin");
                lblStatus.setText("Admin created. Please login again.");
                SetupLoginSelectionPage next = new SetupLoginSelectionPage(db);
                next.show(stage);
            } else {
                lblStatus.setText("Failed to create admin.");
            }
        });

        // put everything into the layout
        layout.getChildren().addAll(lbl, txtUser, txtPass, txtEmail, btnCreate, lblStatus);

        // show the window
        stage.setScene(new Scene(layout, 800, 400));
        stage.setTitle("Admin Setup");
    }
}
