package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * User Home Page
 * - Shows current email for the logged-in user
 * - Lets user update their email
 * - Has a button to open the new Q&A Portal (for TP2)
 */
public class UserHomePage {

    private Stage stage;        // main window
    private DatabaseHelper db;  // connection to our database helper
    private String user;        // current logged-in username

    // constructor
    public UserHomePage(Stage s, DatabaseHelper d, String u) {
        stage = s;
        db = d;
        user = u;
    }

    // show() method builds and displays the page
    public void show(Stage unused) {
        // labels and text fields
        Label lblTitle = new Label("User Home");
        Label lblEmail = new Label("Current email: " + safe(db.getUserEmail(user)));

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("New email");

        // buttons
        Button btnUpdate = new Button("Update Email");
        Button btnBack = new Button("Back");
        Button btnQnA = new Button("Open Q&A Portal"); // new feature for TP2

        Label lblStatus = new Label();

        // --- Update Email Button ---
        btnUpdate.setOnAction(e -> {
            String newEmail = txtEmail.getText();
            if (newEmail == null || newEmail.trim().isEmpty()) {
                lblStatus.setText("Enter a valid email.");
                return;
            }

            boolean ok = db.updateUserEmail(user, newEmail.trim());
            if (ok) {
                lblStatus.setText("Email updated.");
                lblEmail.setText("Current email: " + safe(db.getUserEmail(user)));
                txtEmail.clear();
            } else {
                lblStatus.setText("Update failed.");
            }
        });

        // --- Q&A Portal Button ---
        btnQnA.setOnAction(e -> {
            //  pass the same db + username so it connects to the same persistent database
            QuestionPage qPage = new QuestionPage(db, user);
            qPage.openPage();
        });

        // --- Back Button ---
        btnBack.setOnAction(e -> {
            WelcomeLoginPage w = new WelcomeLoginPage(db);
            w.show(stage, user);
        });

        // layout setup
        VBox root = new VBox(10, lblTitle, lblEmail, txtEmail, btnUpdate, lblStatus, btnQnA, btnBack);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        // scene setup
        stage.setScene(new Scene(root, 800, 400));
        stage.setTitle("User Home");
    }

    // helper to safely print email (avoids null)
    private String safe(String s) {
        return (s == null ? "(none)" : s);
    }
}
