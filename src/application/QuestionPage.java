package application;

import application.privileges.Privileges;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

// student portal and admin portal


 // asking, editing, replying and so on..


 // deletion and editing procedures are maintained here

public class QuestionPage {

    private final QuestionManager manager;
    private final DatabaseHelper db;
    private final String username;
    private final Privileges privileges;


    public QuestionPage(DatabaseHelper db, String username) {
        this.db = db;
        this.username = username;
        this.manager = new QuestionManager(db, username);
        this.privileges = new Privileges(db, username);
    }


    public void openPage() {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));

        Label titleLabel = new Label("Ask a Question");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TextField titleField = new TextField();
        titleField.setPromptText("Enter title");

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Enter your question details here");

        // --- Buttons ---
        Button addBtn = new Button("Add Question");
        Button editBtn = new Button("Edit Selected");
        Button deleteBtn = new Button("Delete Selected");
        Button showBtn = new Button("Show All");
        Button viewRepliesBtn = new Button("View Replies");

        // search criteria control
        TextField searchField = new TextField();
        searchField.setPromptText("Search keyword");
        Button searchBtn = new Button("Search");

        // -ojur list view
        ListView<Question> questionList = new ListView<>();
        questionList.setPrefHeight(250);

        // adding questions
        addBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String body = bodyArea.getText().trim();

            if (title.isEmpty() || body.isEmpty()) {
                showAlert("Missing Info", "Please enter both a title and question body.");
                return;
            }

            Question q = new Question(title, body, username);
            manager.addQuestion(q);
            titleField.clear();
            bodyArea.clear();
            showAlert("Success", "Question added successfully.");
            refreshList(questionList);
        });

        //editing questions that were submitted
        editBtn.setOnAction(e -> {
            Question selected = questionList.getSelectionModel().getSelectedItem();
            if (selected == null || selected.getTitle().startsWith("No ")) {
                showAlert("No Selection", "Please select a valid question to edit.");
                return;
            }

            //  author, admin, staff instruction call procedures
            if (!privileges.canModifyQuestion(selected.getAuthor())) {
                showAlert("Permission Denied", "You can only edit your own question or be Staff/Admin/Instructor.");
                return;
            }

            TextInputDialog dialog = new TextInputDialog(selected.getTitle());
            dialog.setTitle("Edit Question Title");
            dialog.setHeaderText("Editing: " + selected.getTitle());
            dialog.setContentText("Enter new title:");
            dialog.showAndWait().ifPresent(newTitle -> {
                manager.updateQuestionTitle(selected.getTitle(), newTitle);
                showAlert("Updated", "Question title updated successfully.");
                refreshList(questionList);
            });
        });

        //deleting hte selected 
        deleteBtn.setOnAction(e -> {
            Question selected = questionList.getSelectionModel().getSelectedItem();
            if (selected == null || selected.getTitle().startsWith("No ")) {
                showAlert("No Selection", "Please select a valid question to delete.");
                return;
            }

            //   author or staff/admin/instructor can delete
            if (!privileges.canModifyQuestion(selected.getAuthor())) {
                showAlert("Permission Denied", "You can only delete your own question or be Staff/Admin/Instructor.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete this question?");
            confirm.setContentText(selected.getTitle());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    manager.deleteQuestion(selected.getTitle());
                    showAlert("Deleted", "Question deleted successfully.");
                    refreshList(questionList);
                }
            });
        });

        // this shows us all Q's submitted
        showBtn.setOnAction(e -> refreshList(questionList));

        // question search 'engine'
        searchBtn.setOnAction(e -> {
            String key = searchField.getText().trim();
            questionList.getItems().clear();

            if (key.isEmpty()) {
                showAlert("Missing Keyword", "Please enter a keyword to search.");
                return;
            }

            ArrayList<Question> results = manager.search(key);
            if (results.isEmpty()) {
                questionList.getItems().add(new Question("No results found.", "", ""));
            } else {
                questionList.getItems().addAll(results);
            }
        });

        //  this is to view and see replies
        viewRepliesBtn.setOnAction(e -> {
            Question selected = questionList.getSelectionModel().getSelectedItem();
            if (selected == null || selected.getTitle().startsWith("No ")) {
                showAlert("No Selection", "Please select a question to view replies.");
                return;
            }

            int qid = -1;
            var all = db.getAllQuestions();
            for (String[] q : all) {
                if (q[2].equals(selected.getTitle()) && q[1].equals(selected.getAuthor())) {
                    qid = Integer.parseInt(q[0]);
                    break;
                }
            }

            if (qid == -1) {
                showAlert("Error", "Could not find this question in the database.");
                return;
            }

            new AnswerPage(db, qid, selected.getTitle(), username).show();
        });

        // ui layout for replies,search,butns, delete,add,view and so on
        HBox topButtons = new HBox(10, addBtn, editBtn, deleteBtn, showBtn, viewRepliesBtn);
        HBox searchBox = new HBox(10, searchField, searchBtn);
        topButtons.setPadding(new Insets(5, 0, 5, 0));

        root.getChildren().addAll(
                titleLabel,
                new Label("Title:"), titleField,
                new Label("Body:"), bodyArea,
                topButtons,
                new Label("Search:"), searchBox,
                new Label("Questions:"), questionList
        );

        Scene scene = new Scene(root, 650, 600);
        stage.setTitle("Q&A Portal — " + username);
        stage.setScene(scene);
        stage.show();

        refreshList(questionList);
    }

    private void refreshList(ListView<Question> listView) {
        listView.getItems().clear();
        ArrayList<Question> all = manager.getAllQuestions();
        if (all.isEmpty()) {
            listView.getItems().add(new Question("No questions yet.", "", ""));
        } else {
            listView.getItems().addAll(all);
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
