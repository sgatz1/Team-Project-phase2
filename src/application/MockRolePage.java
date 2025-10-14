package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * RoleRouter (formerly MockRolePage)
 * Redirects the user to the correct home page based on stored role.
 */
public class MockRolePage {

    private Stage stage;
    private DatabaseHelper db;
    private String username;

    public MockRolePage(Stage s, DatabaseHelper d, String u, String role) {
        this.stage = s;
        this.db = d;
        this.username = u;

        System.out.println("MockRolePage opened for User: " + u + " (role: " + role + ")");

        // redirect to correct home page
        if (role == null || role.trim().isEmpty()) {
            // if no role is stored, show role selection
            RoleSelectionPage rs = new RoleSelectionPage(db, username);
            rs.show(stage);
            return;
        }

        switch (role.toLowerCase()) {
            case "admin":
                AdminHomePage aPage = new AdminHomePage(db);
                aPage.show(stage);
                break;
            case "student":
                UserHomePage uPage = new UserHomePage(stage, db, username);
                uPage.show(stage);
                break;
            case "reviewer":
                ReviewerHomePage rPage = new ReviewerHomePage(stage, db, username);
                rPage.show(stage);
                break;
            case "staff":
                StaffHomePage sPage = new StaffHomePage(stage, db, username);
                sPage.show(stage);
                break;
            default:
                VBox root = new VBox(10, new Label("Unknown role: " + role));
                root.setStyle("-fx-alignment: center; -fx-padding: 20;");
                stage.setScene(new Scene(root, 400, 200));
                stage.setTitle("Unknown Role");
        }
    }
}
