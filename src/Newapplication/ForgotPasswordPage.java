package application;

import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * Temporary placeholder for ForgotPasswordPage.
 * Exists only to satisfy compiler requirements.
 */
public class ForgotPasswordPage {

    private DatabaseHelper db;

    public ForgotPasswordPage(DatabaseHelper d) {
        this.db = d;
        System.out.println("ForgotPasswordPage created (placeholder only).");
    }

    // Added show() method so UserLoginPage can call fp.show(stage)
    public void show(Stage stage) {
        System.out.println("ForgotPasswordPage opened (placeholder only).");
    }
}
