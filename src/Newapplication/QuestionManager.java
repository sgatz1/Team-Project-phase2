package application;

import databasePart1.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

// this is our database backend manager
 // Handles add/search/update/delete using the persistent H2 db.

public class QuestionManager {

    private DatabaseHelper db;
    private String username;

    // constructor now requires the shared db and username
    public QuestionManager(DatabaseHelper db, String username) {
        this.db = db;
        this.username = username;
    }

    // creating a quesiton
    public void addQuestion(Question q) {
        db.addQuestion(username, q.getTitle(), q.getBody());
        System.out.println("Sucessfully saved question to DB: " + q.getTitle());
    }

    // reading our questions
    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> list = new ArrayList<>();
        List<String[]> rows = db.getAllQuestions();
        for (String[] r : rows) {
            Question q = new Question(r[2], r[3], r[1]); // title, body, author
            list.add(q);
        }
        return list;
    }

    // searching
    public ArrayList<Question> search(String keyword) {
        ArrayList<Question> list = new ArrayList<>();
        List<String[]> rows = db.searchQuestions(keyword);
        for (String[] r : rows) {
            Question q = new Question(r[2], r[3], r[1]);
            list.add(q);
        }
        return list;
    }

    // UPDATE
    public void updateQuestionTitle(String oldTitle, String newTitle) {
        //for basics we tried to keep the search engine consistent with match make searches.
        List<String[]> all = db.getAllQuestions();
        for (String[] q : all) {
            if (q[2].equalsIgnoreCase(oldTitle)) {
                db.updateQuestion(Integer.parseInt(q[0]), newTitle, q[3]);
                System.out.println("Updated The question title: " + oldTitle + " → " + newTitle);
                return;
            }
        }
    }

    // to delete
    public void deleteQuestion(String title) {
        List<String[]> all = db.getAllQuestions();
        for (String[] q : all) {
            if (q[2].equalsIgnoreCase(title)) {
                db.deleteQuestion(Integer.parseInt(q[0]));
                System.out.println("Deleted question: " + title);
                return;
            }
        }
    }

    // accpeting answer or state
    public void markAnswerAccepted(Question question, Answer answer) {
        for (Answer a : question.getAnswers()) {
            a.setAccepted(false);
        }
        answer.setAccepted(true);
        question.setSolved(true);
        System.out.println(" Accepted answer: " + answer.getText());
    }
}
