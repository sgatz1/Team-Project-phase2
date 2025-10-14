package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Simple welcome page after login for users with one role
 * allows our users or user to  go to  Selection or Logout.
 */
public class WelcomeLoginPage {

    private DatabaseHelper db; // database helper

    public WelcomeLoginPage(DatabaseHelper d) {
        db = d;
    }

    public void show(Stage stage, String user) {
        // welcome message
        Label lblWelcome = new Label("Welcome, " + user + "!");

        // buttons
        Button btnRole = new Button("Choose Role");
        Button btnLogout = new Button("Logout");

        // go to role selection page
        btnRole.setOnAction(e -> {
            RoleSelectionPage rolePage = new RoleSelectionPage(db, user);
            rolePage.show(stage);
        });

        // logout goes back to login/setup
        btnLogout.setOnAction(e -> {
            SetupLoginSelectionPage sel = new SetupLoginSelectionPage(db);
            sel.show(stage);
        });

        // layout
        VBox layout = new VBox(10, lblWelcome, btnRole, btnLogout);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        // make scene
        stage.setScene(new Scene(layout, 800, 400));
        stage.setTitle("Welcome");
        stage.show();
    }
}
