package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

// this page shows the code that was generated for invites
public class InvitationPage {

    private DatabaseHelper db; // db object

    public InvitationPage(DatabaseHelper d) {
        db = d;
    }

    public void show(Stage stage) {
        // layout for the screen
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // text for the page
        Label lblInfo = new Label("Invitation Code Generated:");

        // ask db to make a code
        String code = db.generateInvitationCode();
        Label lblCode = new Label(code != null ? code : "Error generating code");

        // back button to go to login screen
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> {
            WelcomeLoginPage w = new WelcomeLoginPage(db);
            w.show(stage, "admin");
        });

        // add everything to layout
        layout.getChildren().addAll(lblInfo, lblCode, btnBack);

        // create scene and show it
        Scene scene = new Scene(layout, 800, 400);
        stage.setScene(scene);
        stage.setTitle("Invitation Page");
        stage.show();
    }
}
