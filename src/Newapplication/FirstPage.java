package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The FirstPage class represents the initial screen shown when the application starts
 * and no users are yet set up. It provides a simple interface prompting the user
 * to begin the administrator setup process.
 */
public class FirstPage {

    // Reference to the DatabaseHelper, used for database interactions
    private final DatabaseHelper databaseHelper;

    /**
     * Constructor initializes the FirstPage with a DatabaseHelper instance.
     *
     * @param db  an instance of DatabaseHelper to be used for database operations
     */
    public FirstPage(DatabaseHelper db) {
        this.databaseHelper = db;
    }

    /**
     * Displays the first page on the given stage.
     * This page welcomes the user and provides a button to continue
     * to the administrator setup process.
     *
     * @param stage the primary Stage where the scene is displayed
     */
    public void show(Stage stage) {
        // Create a vertical box layout with spacing of 10 pixels between elements
        VBox layout = new VBox(10);
        // Apply basic alignment and padding styles to center content
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Create a label with a welcome message and instructions
        Label msg = new Label("Hello..You are the first person here.\nPlease select continue to setup administrator access");

        // Create a button to proceed to the Admin Setup page
        Button btn = new Button("Continue");

        // Define button action: when clicked, it opens the AdminSetupPage
        btn.setOnAction(e -> new AdminSetupPage(databaseHelper).show(stage));

        // Add all UI elements (label and button) to the layout container
        layout.getChildren().addAll(msg, btn);

        // Set the scene for the stage with the layout and window size
        stage.setScene(new Scene(layout, 800, 400));

        // Set the window title
        stage.setTitle("First Page");

        // Display the stage (window) to the user
        stage.show();
    }
}
