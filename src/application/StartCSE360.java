package application;

import databasePart1.DatabaseHelper;
import javafx.application.Application;
import javafx.stage.Stage;

// main class that starts the program
public class StartCSE360 extends Application {

    private static final DatabaseHelper db = new DatabaseHelper(); // database helper

    public static void main(String[] args) {
        // launch the javafx app
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // if no users are in the database yet, show first page
        if (db.isDatabaseEmpty()) {
            FirstPage page = new FirstPage(db);
            page.show(primaryStage);
        } else {
            // otherwise go to the setup/login selection screen
            SetupLoginSelectionPage page = new SetupLoginSelectionPage(db);
            page.show(primaryStage);
        }
    }
}
