package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StaffHomePage {
    private final Stage stage;
    private final DatabaseHelper db;
    private final String username;

    public StaffHomePage(Stage stage, DatabaseHelper db, String username) {
        this.stage = stage;
        this.db = db;
        this.username = username;
    }

    public void show(Stage unused) {
        Label lblTitle = new Label("Staff / Instructor Home");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnQnA = new Button("Open Q&A Portal");
        btnQnA.setOnAction(e -> {
            QuestionPage qPage = new QuestionPage(db, username);
            qPage.openPage();
        });

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            WelcomeLoginPage w = new WelcomeLoginPage(db);
            w.show(stage, username);
        });

        VBox root = new VBox(15, lblTitle, btnQnA, btnLogout);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Staff Home");
    }
}
