package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * The ReviewerHomePage class represents the home page for a reviewer user.
 * It displays a simple interface showing the reviewer's username and basic layout.
 */
public class ReviewerHomePage {

    // Reference to the main application window (stage)
    private Stage stage;

    // Reference to the database helper used for database interactions
    private DatabaseHelper db;

    // The username of the currently logged-in reviewer
    private String user;

    /**
     * Constructor initializes the ReviewerHomePage with the necessary components.
     *
     * @param s The primary Stage where the scene will be displayed
     * @param d DatabaseHelper instance for database operations
     * @param u The username of the reviewer
     */
    public ReviewerHomePage(Stage s, DatabaseHelper d, String u) {
        this.stage = s;
        this.db = d;
        this.user = u;
    }

    /**
     * Displays the reviewer's home page on the provided stage.
     * Shows a simple label identifying the current reviewer.
     *
     * @param stage The Stage where this page will be shown
     */
    public void show(Stage stage) {
        // Create a vertical box layout with spacing of 10 pixels
        // and include a label showing the reviewer's name
        VBox root = new VBox(10, new Label("Reviewer Home Page for " + user));

        // Center the content and add padding around the layout
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Create and set a new scene for the stage with fixed size
        stage.setScene(new Scene(root, 400, 200));

        // Set the window title
        stage.setTitle("Reviewer Home");
    }
}
