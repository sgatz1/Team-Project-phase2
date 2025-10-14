package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The StaffHomePage class represents the home interface for staff or instructors.
 * It provides navigation options for staff users, such as accessing the Q&A portal
 * or logging out of the application.
 */
public class StaffHomePage {

    // Reference to the main stage (application window)
    private final Stage stage;

    // Reference to the database helper for interacting with the database
    private final DatabaseHelper db;

    // The username of the logged-in staff or instructor
    private final String username;

    /**
     * Constructor initializes the staff home page with the required data.
     *
     * @param stage     The primary stage where the scene will be displayed
     * @param db        DatabaseHelper instance for database access
     * @param username  The username of the currently logged-in staff member
     */
    public StaffHomePage(Stage stage, DatabaseHelper db, String username) {
        this.stage = stage;
        this.db = db;
        this.username = username;
    }

    /**
     * Displays the staff home page on the given stage.
     * Provides buttons to navigate to the Q&A portal or log out.
     *
     * @param unused  (Unused parameter) kept for consistency with other page methods
     */
    public void show(Stage unused) {
        // Create a title label for the page
        Label lblTitle = new Label("Staff / Instructor Home");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Button to open the Q&A portal page
        Button btnQnA = new Button("Open Q&A Portal");
        btnQnA.setOnAction(e -> {
            // Create and open the QuestionPage for the current user
            QuestionPage qPage = new QuestionPage(db, username);
            qPage.openPage();
        });

        // Button to log out and return to the welcome/login page
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            // Create and display the WelcomeLoginPage, passing the database and username
            WelcomeLoginPage w = new WelcomeLoginPage(db);
            w.show(stage, username);
        });

        // Layout container for all UI elements, with 15px spacing
        VBox root = new VBox(15, lblTitle, btnQnA, btnLogout);
        root.setPadding(new Insets(20));             // Add padding around content
        root.setStyle("-fx-alignment: center;");     // Center all components in layout

        // Set up the scene with dimensions and assign it to the stage
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Staff Home");                // Set the window title
    }
}
