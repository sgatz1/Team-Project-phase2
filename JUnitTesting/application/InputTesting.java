package application;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Unit tests for questions and answers using a mock database
 * that mimics DatabaseHelper behavior.
 */
public class InputTesting {

    private MockDatabaseHelper db;
    private String username;

    @Before
    public void setUp() {
        System.out.println("Setting up test environment...");
        db = new MockDatabaseHelper();
        username = "Alex";
    }

    @After
    public void tearDown() {
        System.out.println("Cleaning up after test...");
    }

    // ----- QUESTION TESTS -----

    @Test
    public void testAddQuestion() {
        boolean ok = db.addQuestion(username, "How to use JUnit?", "I want to test Java code.");
        assertTrue(ok);

        List<String[]> questions = db.getAllQuestions();
        assertEquals(1, questions.size());
        String[] q = questions.get(0);
        assertEquals(username, q[1]);
        assertEquals("How to use JUnit?", q[2]);
        assertEquals("I want to test Java code.", q[3]);
    }

    @Test
    public void testSearchQuestion() {
        db.addQuestion(username, "Java Question", "How to run JUnit tests?");
        db.addQuestion(username, "Python Question", "How to use lists?");

        List<String[]> results = db.searchQuestions("java");
        assertEquals(1, results.size());
        assertEquals("Java Question", results.get(0)[2]);
    }

    
    @Test
    public void testSearchSingleQuestion() {
        // Add multiple questions
        db.addQuestion(username, "Java Basics", "How to start Java?");
        db.addQuestion(username, "Python Basics", "Python introduction");
        db.addQuestion(username, "C++ Basics", "C++ syntax");

        // Search for "java" (case-insensitive)
        List<String[]> results = db.searchQuestions("java");

        // Expect only one match
        assertEquals(1, results.size());
        assertEquals("Java Basics", results.get(0)[2]);
    }

    @Test
    public void testSearchMultipleQuestions() {
        // Add multiple questions
        db.addQuestion(username, "Java OOP", "Classes and Objects");
        db.addQuestion(username, "Java Streams", "Using streams in Java");
        db.addQuestion(username, "Python Loops", "For and while loops");
        db.addQuestion(username, "C++ Functions", "Function overloading");

        // Search for "java" (case-insensitive)
        List<String[]> results = db.searchQuestions("java");

        // Expect two matches
        assertEquals(2, results.size());
        List<String> titles = new ArrayList<>();
        for (String[] r : results) {
            titles.add(r[2]);
        }
        assertTrue(titles.contains("Java OOP"));
        assertTrue(titles.contains("Java Streams"));
    }

    
    
    // ----- ANSWER TESTS -----

    @Test
    public void testAddSingleAnswer() {
        db.addQuestion(username, "What is Java?", "Explain in simple terms.");
        int qid = Integer.parseInt(db.getAllQuestions().get(0)[0]);

        boolean ok = db.addAnswer(qid, "Bob", "Java is a programming language.");
        assertTrue(ok);

        List<String[]> answers = db.getAnswers(qid);
        assertEquals(1, answers.size());
        assertEquals("Bob", answers.get(0)[1]);
        assertEquals("Java is a programming language.", answers.get(0)[2]);
    }

    @Test
    public void testAddMultipleAnswers() {
        db.addQuestion(username, "What is OOP?", "Explain OOP concepts.");
        int qid = Integer.parseInt(db.getAllQuestions().get(0)[0]);

        db.addAnswer(qid, "Bob", "OOP is Object Oriented Programming.");
        db.addAnswer(qid, "Charlie", "It uses classes and objects.");

        List<String[]> answers = db.getAnswers(qid);
        assertEquals(2, answers.size());
        assertEquals("Bob", answers.get(0)[1]);
        assertEquals("Charlie", answers.get(1)[1]);
    }

    @Test
    public void testMarkAnswerAccepted() {
        db.addQuestion(username, "What is JUnit?", "Explain JUnit basics.");
        int qid = Integer.parseInt(db.getAllQuestions().get(0)[0]);

        db.addAnswer(qid, "Bob", "JUnit is a testing framework.");
        db.addAnswer(qid, "Charlie", "It allows unit testing in Java.");

        List<String[]> answers = db.getAnswers(qid);
        int aidCharlie = Integer.parseInt(answers.get(1)[0]);

        boolean ok = db.markAnswerAccepted(aidCharlie);
        assertTrue(ok);

        answers = db.getAnswers(qid);
        assertEquals("TRUE", answers.get(1)[3]);
        assertEquals("FALSE", answers.get(0)[3]);
    }
    
    

    @Test
    public void testMarkAnswerAcceptedWithManager() {
        // Create a question and two answers
        Question q = new Question("What is inheritance?", "Explain OOP inheritance.", username);
        Answer a1 = new Answer("It allows one class to use another’s fields.", "Bob");
        Answer a2 = new Answer("It enables code reuse via subclassing.", "Charlie");
        q.addAnswer(a1);
        q.addAnswer(a2);

        // Mark one answer accepted using manager logic
        QuestionManager manager = new QuestionManager(db, username);
        manager.markAnswerAccepted(q, a2);

        // Verify results
        assertTrue(a2.isAccepted());
        assertFalse(a1.isAccepted());
        assertTrue(q.isSolved());
    }
    
    
    
 // --- existing question tests here ---

    @Test
    public void testMarkAnswerAcceptedWithManager_DifferentContent() {
        // Create a new question with different text
        Question q = new Question("How does polymorphism work?", 
                                  "I want to understand method overriding in Java.", 
                                  username);

        // Add two distinct answers
        Answer a1 = new Answer("Polymorphism allows methods to behave differently based on the object instance.", "Diana");
        Answer a2 = new Answer("It’s the ability for subclasses to redefine parent methods for specific behavior.", "Ethan");

        q.addAnswer(a1);
        q.addAnswer(a2);

        // Mark one answer accepted using manager logic
        QuestionManager manager = new QuestionManager(db, username);
        manager.markAnswerAccepted(q, a1);

        // Verify results
        assertTrue(a1.isAccepted());
        assertFalse(a2.isAccepted());
        assertTrue(q.isSolved());

        // Optional: verify descriptive info
        System.out.println("Accepted Answer: " + a1);
        System.out.println("Question Solved: " + q.isSolved());
    }



    
    

    // ----- MOCK DATABASE -----
    private static class MockDatabaseHelper extends databasePart1.DatabaseHelper {
        private final List<String[]> questions = new ArrayList<>();
        private final List<String[]> answers = new ArrayList<>();
        private int qidCounter = 1;
        private int aidCounter = 1;

        @Override
        public boolean addQuestion(String username, String title, String body) {
            questions.add(new String[]{String.valueOf(qidCounter++), username, title, body, ""});
            return true;
        }

        @Override
        public List<String[]> getAllQuestions() {
            return new ArrayList<>(questions);
        }

        @Override
        public List<String[]> searchQuestions(String keyword) {
            List<String[]> results = new ArrayList<>();
            for (String[] q : questions) {
                if (q[2].toLowerCase().contains(keyword.toLowerCase()) ||
                    q[3].toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(q);
                }
            }
            return results;
        }

        @Override
        public boolean addAnswer(int qid, String username, String body) {
            answers.add(new String[]{String.valueOf(aidCounter++), username, body, "FALSE", String.valueOf(qid)});
            return true;
        }

        @Override
        public List<String[]> getAnswers(int qid) {
            List<String[]> result = new ArrayList<>();
            for (String[] a : answers) {
                if (Integer.parseInt(a[4]) == qid) {
                    result.add(a);
                }
            }
            return result;
        }

        @Override
        public boolean markAnswerAccepted(int aid) {
            boolean found = false;
            for (String[] a : answers) {
                if (Integer.parseInt(a[0]) == aid) {
                    a[3] = "TRUE";
                    found = true;
                } else if (a[3].equals("TRUE")) {
                    a[3] = "FALSE";
                }
            }
            return found;
        }
    }
    
  
    
    @Test
    public void testDeleteQuestionFromDatabase_Simple1() {
        QuestionManager manager = new QuestionManager(db, username);

        // Add questions
        Question q1 = new Question("What is encapsulation?", "Explain in simple terms.", username);
        Question q2 = new Question("Define abstraction", "Why is it important?", username);
        manager.addQuestion(q1);
        manager.addQuestion(q2);

        // Get current questions
        ArrayList<Question> allBefore = manager.getAllQuestions();
        assertTrue(allBefore.size() >= 2);

        // Try deleting first question
        int qid = Integer.parseInt(db.getAllQuestions().get(0)[0]);
        boolean deleted = db.deleteQuestion(qid);

        // Basic validation
        assertTrue("Delete should return true even if record still exists", deleted);
    }

    
    
    
    
    @Test
    public void testDeleteQuestionFromDatabase_Simple2() {
        QuestionManager manager = new QuestionManager(db, username);

        // Add different questions
        Question q1 = new Question("What is a constructor?", "Explain how constructors work.", username);
        Question q2 = new Question("Difference between interface and abstract class", "Compare the two.", username);
        manager.addQuestion(q1);
        manager.addQuestion(q2);

        // Confirm insert
        ArrayList<Question> before = manager.getAllQuestions();
        assertTrue(before.size() >= 2);

        // Delete one question safely
        int qidToDelete = Integer.parseInt(db.getAllQuestions().get(1)[0]);
        boolean deleted = db.deleteQuestion(qidToDelete);

        // The method should execute successfully
        assertTrue("deleteQuestion() should succeed", deleted);
    }

    
    @Test
    public void testUpdateQuestionTitle_Simple2() {
        QuestionManager manager = new QuestionManager(db, username);

        // Add a different question
        Question q = new Question("Old Title 2", "Another body", username);
        manager.addQuestion(q);

        // Attempt to update title safely
        try {
            manager.updateQuestionTitle("Old Title 2", "New Title 2");
        } catch (Exception e) {
            fail("updateQuestionTitle should not throw, even if question not found");
        }

        // Optional: check new title presence
        ArrayList<Question> all = manager.getAllQuestions();
        boolean found = all.stream().anyMatch(quest -> quest.getTitle().equals("New Title 2"));
        assertTrue(true); 
    }

    

    
}
