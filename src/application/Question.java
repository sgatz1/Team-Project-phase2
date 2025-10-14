package application;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String title;
    private String body;
    private String author;
    private boolean solved;
    private List<Answer> answers;

    public Question(String title, String body, String author) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.solved = false;
        this.answers = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getAuthor() { return author; }
    public boolean isSolved() { return solved; }
    public List<Answer> getAnswers() { return answers; }

    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }
    public void setSolved(boolean solved) { this.solved = solved; }

    public void addAnswer(Answer a) { answers.add(a); }

    @Override
    public String toString() {
        return title + " — by " + author;
    }
}
