package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SetupAccountPage {
    private final DatabaseHelper databaseHelper;

    public SetupAccountPage(DatabaseHelper db) {
        this.databaseHelper = db;
    }

    public void show(Stage primaryStage) {
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        Button setupButton = new Button("Setup");

        setupButton.setOnAction(a -> {
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();
            String email = emailField.getText().trim();
            String code = inviteCodeField.getText().trim();

            if (userName.isEmpty() || password.isEmpty() || email.isEmpty() || code.isEmpty()) {
                errorLabel.setText("All fields required.");
                return;
            }

            // === Username and Password validation - Mustafa
            boolean uservalid = false;
            boolean passvalid = false;

            String usernamevalid = UserNameRecognizer.checkForValidUserName(userName);
            System.out.println(usernamevalid);

            if (usernamevalid.contains("*** ERROR ***")) {
                System.out.println("Username is Invalid");
            } else {
                System.out.println("Username is Valid");
                uservalid = true;
            }

            String passwordvalid = PasswordEvaluator.evaluatePassword(password);
            System.out.println(passwordvalid);

            if (passwordvalid.contains("conditions were not satisfied") ||
                    passwordvalid.contains("*** Error *** An invalid character has been found!")) {
                System.out.println("Password is Invalid");
            } else {
                System.out.println("Password is Valid");
                passvalid = true;
            }

            // stops here if input validation fails
            if (!uservalid || !passvalid) {
                errorLabel.setText("Invalid username or password. See console for details.");
                return;
            }

            if (!databaseHelper.validateInvitationCode(code)) {
                errorLabel.setText("Please enter a valid invitation code.");
                return;
            }

            boolean success = databaseHelper.register(userName, password, email);
            if (success) {
                new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
            } else {
                errorLabel.setText("Failed to register user.");
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, emailField, inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
