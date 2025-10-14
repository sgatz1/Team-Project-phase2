package application;

import java.util.ArrayList;
import java.util.List;

/**
 * The Question class represents a question posted by a user in the application.
 * It stores information such as the title, body, author, and associated answers.
 */
public class Question {

    // Title of the question
    private String title;

    // Main content or description of the question
    private String body;

    // Username of the person who created the question
    private String author;

    // Indicates whether the question has been marked as solved
    private boolean solved;

    // List of answers associated with this question
    private List<Answer> answers;

    /**
     * Constructor initializes a new Question object with a title, body, and author.
     * By default, a new question is not solved and has an empty list of answers.
     *
     * @param title   The title of the question
     * @param body    The main content or body of the question
     * @param author  The username of the question's author
     */
    public Question(String title, String body, String author) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.solved = false;              // Default value when question is created
        this.answers = new ArrayList<>(); // Initialize the list to hold answers
    }

    // Getter methods for accessing question properties
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getAuthor() { return author; }
    public boolean isSolved() { return solved; }
    public List<Answer> getAnswers() { return answers; }

    // Setter methods for modifying question properties
    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }
    public void setSolved(boolean solved) { this.solved = solved; }

    /**
     * Adds a new answer to the list of answers for this question.
     *
     * @param a The Answer object to add
     */
    public void addAnswer(Answer a) { answers.add(a); }

    /**
     * Returns a short text representation of the question,
     * including the title and the author's name.
     *
     * @return String representation of the question
     */
    @Override
    public String toString() {
        return title + " — by " + author;
    }
}
