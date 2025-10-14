package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// this page lets you pick if you want to make an account or login
public class SetupLoginSelectionPage {

    private DatabaseHelper db; // database connection

    public SetupLoginSelectionPage(DatabaseHelper d) {
        db = d;
    }

    public void show(Stage stage) {
        // buttons
        Button btnSetup = new Button("SetUp");
        Button btnLogin = new Button("Login");

        // what happens if you click setup
        btnSetup.setOnAction(a -> {
            SetupAccountPage page = new SetupAccountPage(db);
            page.show(stage);
        });

        // what happens if you click login
        btnLogin.setOnAction(a -> {
            UserLoginPage page = new UserLoginPage(db);
            page.show(stage);
        });

        // layout
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(btnSetup, btnLogin);

        // make window
        stage.setScene(new Scene(layout, 800, 400));
        stage.setTitle("Account Setup");
        stage.show();
    }
}
