package application;

public class Answer {
    private String text;
    private String author;
    private boolean accepted;

    public Answer(String text, String author) {
        this.text = text;
        this.author = author;
        this.accepted = false;
    }

    // getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getAuthor() { return author; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    @Override
    public String toString() {
        return "Answer by " + author + ": " + text;
    }
}
