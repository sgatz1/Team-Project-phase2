package application;

import application.privileges.Privileges;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

// Replies window for any selected question.
 // allows our users  posting, refreshing, deleting (with privileges), and marking accepted.
 
public class AnswerPage {

    private final DatabaseHelper db;
    private final int questionId;
    private final String questionTitle;
    private final String username;

    public AnswerPage(DatabaseHelper db, int qid, String title, String username) {
        this.db = db;
        this.questionId = qid;
        this.questionTitle = title;
        this.username = username;
    }

    public void show() {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));

        Label lblTitle = new Label("Question: " + questionTitle);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ListView<String> replyList = new ListView<>();
        replyList.setPrefHeight(250);

        TextArea replyField = new TextArea();
        replyField.setPromptText("Write your reply here...");
        replyField.setWrapText(true);

        Button postBtn = new Button("Post Reply");
        Button refreshBtn = new Button("Refresh Replies");
        Button deleteBtn = new Button("Delete Selected");
        Button acceptBtn = new Button("Mark as Accepted *");
        Label status = new Label();

        // this ensures that the privledges file is checked upon entry
        Privileges privileges = new Privileges(db, username);
        boolean isAdmin = privileges.isAdmin();
        boolean isStaff = privileges.isStaff();

        // visible only for Admin/Staff
        acceptBtn.setVisible(isAdmin || isStaff);

        Runnable loadReplies = () -> {
            replyList.getItems().clear();
            List<String[]> replies = db.getAnswers(questionId);
            for (String[] a : replies) {
                String accepted = a[3].equalsIgnoreCase("TRUE") ? " * (Accepted)" : "";
                replyList.getItems().add("[" + a[0] + "] " + a[2] + " — by " + a[1] + accepted);
            }
        };
        loadReplies.run();

        // after  replies are sent down
        postBtn.setOnAction(e -> {
            String text = replyField.getText().trim();
            if (text.isEmpty()) {
                status.setText("Please write something.");
                return;
            }
            boolean ok = db.addAnswer(questionId, username, text);
            if (ok) {
                status.setText("Reply added.");
                replyField.clear();
                loadReplies.run();
            } else {
                status.setText("Failed to add reply.");
            }
        });

        // our refresh batch button option
        refreshBtn.setOnAction(e -> loadReplies.run());

        // deleting the selected option
        deleteBtn.setOnAction(e -> {
            int index = replyList.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                showAlert("Select a reply to delete first.");
                return;
            }

            List<String[]> replies = db.getAnswers(questionId);
            String[] chosen = replies.get(index);
            int aid = Integer.parseInt(chosen[0]);
            String author = chosen[1];

            // this is where the logic for user specifics sit
            if (author.equalsIgnoreCase(username) || isAdmin || isStaff) {
                boolean ok = db.deleteAnswer(aid);
                if (ok) {
                    showAlert("Reply deleted successfully.");
                    loadReplies.run();
                } else {
                    showAlert("Failed to delete reply.");
                }
            } else {
                showAlert("You don't have permission to delete this reply.");
            }
        });

        //accept
        acceptBtn.setOnAction(e -> {
            int index = replyList.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                showAlert("Select a reply first to mark accepted.");
                return;
            }
            List<String[]> replies = db.getAnswers(questionId);
            String[] chosen = replies.get(index);
            int aid = Integer.parseInt(chosen[0]);

            boolean ok = db.markAnswerAccepted(aid);
            if (ok) {
                showAlert("Marked as accepted answer!");
                loadReplies.run();
            } else {
                showAlert("Failed to mark answer.");
            }
        });

        // our layout
        HBox buttons = new HBox(10, postBtn, refreshBtn, deleteBtn, acceptBtn);
        root.getChildren().addAll(lblTitle, new Label("Replies:"), replyList, buttons, replyField, status);

        stage.setTitle("Replies for: " + questionTitle);
        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
