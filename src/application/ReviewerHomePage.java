package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class ReviewerHomePage {
    private Stage stage;
    private DatabaseHelper db;
    private String user;

    public ReviewerHomePage(Stage s, DatabaseHelper d, String u) {
        this.stage = s;
        this.db = d;
        this.user = u;
    }

    public void show(Stage stage) {
        VBox root = new VBox(10, new Label("Reviewer Home Page for " + user));
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");
        stage.setScene(new Scene(root, 400, 200));
        stage.setTitle("Reviewer Home");
    }
}
