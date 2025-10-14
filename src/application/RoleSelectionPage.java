package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

 // RoleSelectionPage — visible role selection screen


 
 //for first-time users without a saved role.

public class RoleSelectionPage {

    private final DatabaseHelper db;
    private final String username;

    public RoleSelectionPage(DatabaseHelper d, String user) {
        this.db = d;
        this.username = user;
    }

    public void show(Stage stage) {
        Label lblTitle = new Label("Select Your Role");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label lblPrompt = new Label("Choose your role for this account:");

        ComboBox<String> cmbRoles = new ComboBox<>();
        cmbRoles.getItems().addAll("Admin", "Student", "Reviewer", "Staff");
        cmbRoles.setPromptText("Select a Role");

        Button btnConfirm = new Button("Confirm");
        Label lblStatus = new Label();

        btnConfirm.setOnAction(e -> {
            String selectedRole = cmbRoles.getValue();
            if (selectedRole == null) {
                lblStatus.setText("Please select a role before continuing.");
                return;
            }

            boolean ok = db.updateUserRole(username, selectedRole);
            if (ok) {
                lblStatus.setText("Role assigned: " + selectedRole);

                // redirect to correct page
                switch (selectedRole.toLowerCase()) {
                    case "admin":
                        AdminHomePage aPage = new AdminHomePage(db);
                        aPage.show(stage);  //  this fixes  only one parameter
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
                        lblStatus.setText("Unknown role selected.");
                }
            } else {
                lblStatus.setText("Database update failed.");
            }
        });

        VBox root = new VBox(15, lblTitle, lblPrompt, cmbRoles, btnConfirm, lblStatus);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.setTitle("Role Selection");
        stage.show();
    }
}
