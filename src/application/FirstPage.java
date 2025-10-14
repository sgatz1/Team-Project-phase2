package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FirstPage {
    private final DatabaseHelper databaseHelper;

    public FirstPage(DatabaseHelper db) {
        this.databaseHelper = db;
    }

    public void show(Stage stage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label msg = new Label("Hello..You are the first person here.\nPlease select continue to setup administrator access");
        Button btn = new Button("Continue");

        btn.setOnAction(e -> new AdminSetupPage(databaseHelper).show(stage));

        layout.getChildren().addAll(msg, btn);
        stage.setScene(new Scene(layout, 800, 400));
        stage.setTitle("First Page");
        stage.show();
    }
}
