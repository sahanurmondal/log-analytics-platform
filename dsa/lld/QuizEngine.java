package lld;

import java.util.*;

/**
 * LLD #80-81: Quiz/Exam Engine with Adaptive Difficulty
 * 
 * Design Patterns:
 * 1. Strategy Pattern - Different question types and scoring strategies
 * 2. Factory Pattern - Question creation
 * 3. State Pattern - Quiz state management
 * 4. Template Method - Quiz flow
 * 5. Observer Pattern - Progress tracking
 * 
 * Adaptive Difficulty: Adjusts question difficulty based on performance
 */

enum QuestionType { MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, FILL_BLANK }
enum DifficultyLevel { EASY, MEDIUM, HARD }
enum QuizState { NOT_STARTED, IN_PROGRESS, COMPLETED, PAUSED }

abstract class Question {
    protected String id;
    protected String questionText;
    protected QuestionType type;
    protected DifficultyLevel difficulty;
    protected int points;
    protected String category;
    
    public Question(String id, String questionText, QuestionType type, DifficultyLevel difficulty, int points) {
        this.id = id;
        this.questionText = questionText;
        this.type = type;
        this.difficulty = difficulty;
        this.points = points;
    }
    
    public abstract boolean checkAnswer(String userAnswer);
    public abstract List<String> getOptions();
    
    public String getId() { return id; }
    public String getQuestionText() { return questionText; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public int getPoints() { return points; }
}

class MultipleChoiceQuestion extends Question {
    private List<String> options;
    private String correctAnswer;
    
    public MultipleChoiceQuestion(String id, String text, List<String> options, String correct, DifficultyLevel diff, int points) {
        super(id, text, QuestionType.MULTIPLE_CHOICE, diff, points);
        this.options = options;
        this.correctAnswer = correct;
    }
    
    @Override
    public boolean checkAnswer(String userAnswer) {
        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }
    
    @Override
    public List<String> getOptions() {
        return new ArrayList<>(options);
    }
}

class TrueFalseQuestion extends Question {
    private boolean correctAnswer;
    
    public TrueFalseQuestion(String id, String text, boolean correct, DifficultyLevel diff, int points) {
        super(id, text, QuestionType.TRUE_FALSE, diff, points);
        this.correctAnswer = correct;
    }
    
    @Override
    public boolean checkAnswer(String userAnswer) {
        String normalized = userAnswer.trim().toLowerCase();
        boolean answer = normalized.equals("true") || normalized.equals("t") || normalized.equals("yes");
        return answer == correctAnswer;
    }
    
    @Override
    public List<String> getOptions() {
        return Arrays.asList("True", "False");
    }
}

// Strategy Pattern - Scoring Strategy
interface ScoringStrategy {
    int calculateScore(List<QuestionResult> results);
}

class StandardScoring implements ScoringStrategy {
    @Override
    public int calculateScore(List<QuestionResult> results) {
        return results.stream()
                .filter(QuestionResult::isCorrect)
                .mapToInt(r -> r.getQuestion().getPoints())
                .sum();
    }
}

class PartialCreditScoring implements ScoringStrategy {
    @Override
    public int calculateScore(List<QuestionResult> results) {
        int total = 0;
        for (QuestionResult result : results) {
            if (result.isCorrect()) {
                total += result.getQuestion().getPoints();
            } else if (result.getTimeSpent() < 30000) { // Attempted quickly
                total += result.getQuestion().getPoints() / 4; // 25% partial credit
            }
        }
        return total;
    }
}

class QuestionResult {
    private Question question;
    private String userAnswer;
    private boolean correct;
    private long timeSpent; // milliseconds
    
    public QuestionResult(Question question, String userAnswer, boolean correct, long timeSpent) {
        this.question = question;
        this.userAnswer = userAnswer;
        this.correct = correct;
        this.timeSpent = timeSpent;
    }
    
    public Question getQuestion() { return question; }
    public boolean isCorrect() { return correct; }
    public long getTimeSpent() { return timeSpent; }
}

// Adaptive Difficulty Manager
class AdaptiveDifficultyManager {
    private static final int WINDOW_SIZE = 5;
    private Deque<Boolean> recentResults;
    private DifficultyLevel currentLevel;
    
    public AdaptiveDifficultyManager() {
        this.recentResults = new LinkedList<>();
        this.currentLevel = DifficultyLevel.MEDIUM;
    }
    
    // MAIN ALGORITHM: Adjust difficulty based on performance
    public void recordResult(boolean correct) {
        recentResults.add(correct);
        if (recentResults.size() > WINDOW_SIZE) {
            recentResults.removeFirst();
        }
        
        if (recentResults.size() == WINDOW_SIZE) {
            double accuracy = recentResults.stream().filter(r -> r).count() / (double) WINDOW_SIZE;
            
            if (accuracy >= 0.8 && currentLevel != DifficultyLevel.HARD) {
                currentLevel = DifficultyLevel.HARD;
            } else if (accuracy >= 0.6 && accuracy < 0.8 && currentLevel != DifficultyLevel.MEDIUM) {
                currentLevel = DifficultyLevel.MEDIUM;
            } else if (accuracy < 0.4 && currentLevel != DifficultyLevel.EASY) {
                currentLevel = DifficultyLevel.EASY;
            }
        }
    }
    
    public DifficultyLevel getCurrentLevel() {
        return currentLevel;
    }
}

class QuestionBank {
    private Map<DifficultyLevel, List<Question>> questionsByDifficulty;
    private Map<String, Integer> usedQuestions; // Track usage frequency
    
    public QuestionBank() {
        questionsByDifficulty = new HashMap<>();
        for (DifficultyLevel level : DifficultyLevel.values()) {
            questionsByDifficulty.put(level, new ArrayList<>());
        }
        usedQuestions = new HashMap<>();
    }
    
    public void addQuestion(Question question) {
        questionsByDifficulty.get(question.getDifficulty()).add(question);
    }
    
    public Question getQuestion(DifficultyLevel level) {
        List<Question> questions = questionsByDifficulty.get(level);
        if (questions.isEmpty()) return null;
        
        // Get least used question
        Question selected = questions.stream()
                .min(Comparator.comparingInt(q -> usedQuestions.getOrDefault(q.getId(), 0)))
                .orElse(null);
        
        if (selected != null) {
            usedQuestions.put(selected.getId(), usedQuestions.getOrDefault(selected.getId(), 0) + 1);
        }
        
        return selected;
    }
}

public class QuizEngine {
    private QuestionBank questionBank;
    private List<Question> currentQuiz;
    private List<QuestionResult> results;
    private int currentQuestionIndex;
    private QuizState state;
    private ScoringStrategy scoringStrategy;
    private AdaptiveDifficultyManager adaptiveManager;
    private boolean adaptiveMode;
    private long questionStartTime;
    
    public QuizEngine(boolean adaptiveMode) {
        this.questionBank = new QuestionBank();
        this.currentQuiz = new ArrayList<>();
        this.results = new ArrayList<>();
        this.state = QuizState.NOT_STARTED;
        this.scoringStrategy = new StandardScoring();
        this.adaptiveMode = adaptiveMode;
        if (adaptiveMode) {
            this.adaptiveManager = new AdaptiveDifficultyManager();
        }
    }
    
    public void startQuiz(int numberOfQuestions) {
        currentQuiz.clear();
        results.clear();
        currentQuestionIndex = 0;
        state = QuizState.IN_PROGRESS;
        
        // Generate quiz questions
        for (int i = 0; i < numberOfQuestions; i++) {
            DifficultyLevel level = adaptiveMode && adaptiveManager != null
                    ? adaptiveManager.getCurrentLevel()
                    : DifficultyLevel.MEDIUM;
            
            Question q = questionBank.getQuestion(level);
            if (q != null) {
                currentQuiz.add(q);
            }
        }
        
        questionStartTime = System.currentTimeMillis();
    }
    
    // MAIN ALGORITHM: Submit answer and get next question
    public boolean submitAnswer(String answer) {
        if (state != QuizState.IN_PROGRESS || currentQuestionIndex >= currentQuiz.size()) {
            return false;
        }
        
        Question currentQuestion = currentQuiz.get(currentQuestionIndex);
        long timeSpent = System.currentTimeMillis() - questionStartTime;
        boolean correct = currentQuestion.checkAnswer(answer);
        
        // Record result
        results.add(new QuestionResult(currentQuestion, answer, correct, timeSpent));
        
        // Update adaptive difficulty
        if (adaptiveMode && adaptiveManager != null) {
            adaptiveManager.recordResult(correct);
        }
        
        // Move to next question
        currentQuestionIndex++;
        
        if (currentQuestionIndex >= currentQuiz.size()) {
            state = QuizState.COMPLETED;
        } else {
            questionStartTime = System.currentTimeMillis();
        }
        
        return correct;
    }
    
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < currentQuiz.size()) {
            return currentQuiz.get(currentQuestionIndex);
        }
        return null;
    }
    
    public int calculateFinalScore() {
        return scoringStrategy.calculateScore(results);
    }
    
    public Map<String, Object> getQuizSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalQuestions", currentQuiz.size());
        summary.put("correctAnswers", results.stream().filter(QuestionResult::isCorrect).count());
        summary.put("score", calculateFinalScore());
        summary.put("accuracy", results.isEmpty() ? 0 : 
                (results.stream().filter(QuestionResult::isCorrect).count() / (double) results.size()) * 100);
        summary.put("avgTimePerQuestion", 
                results.stream().mapToLong(QuestionResult::getTimeSpent).average().orElse(0) / 1000.0);
        return summary;
    }
    
    public void setScoringStrategy(ScoringStrategy strategy) {
        this.scoringStrategy = strategy;
    }
    
    public QuestionBank getQuestionBank() { return questionBank; }
    public QuizState getState() { return state; }
    public int getCurrentQuestionNumber() { return currentQuestionIndex + 1; }
    public int getTotalQuestions() { return currentQuiz.size(); }
    
    public static void main(String[] args) {
        // Create quiz engine with adaptive difficulty
        QuizEngine engine = new QuizEngine(true);
        
        // Populate question bank
        engine.getQuestionBank().addQuestion(new MultipleChoiceQuestion(
                "q1", "What is 2+2?", 
                Arrays.asList("3", "4", "5", "6"), "4",
                DifficultyLevel.EASY, 10
        ));
        
        engine.getQuestionBank().addQuestion(new TrueFalseQuestion(
                "q2", "Java is a compiled language", true,
                DifficultyLevel.MEDIUM, 15
        ));
        
        engine.getQuestionBank().addQuestion(new MultipleChoiceQuestion(
                "q3", "Time complexity of binary search?",
                Arrays.asList("O(n)", "O(log n)", "O(n^2)", "O(1)"), "O(log n)",
                DifficultyLevel.HARD, 20
        ));
        
        // Start quiz
        engine.startQuiz(3);
        
        // Simulate answering
        while (engine.getCurrentQuestion() != null) {
            Question q = engine.getCurrentQuestion();
            System.out.println("\nQ" + engine.getCurrentQuestionNumber() + ": " + q.getQuestionText());
            System.out.println("Difficulty: " + q.getDifficulty());
            System.out.println("Options: " + q.getOptions());
            
            // Simulate answer (first option)
            String answer = q.getOptions().get(0);
            boolean correct = engine.submitAnswer(answer);
            System.out.println("Answer: " + answer + " - " + (correct ? "Correct!" : "Wrong!"));
        }
        
        // Show summary
        System.out.println("\n=== Quiz Summary ===");
        engine.getQuizSummary().forEach((key, value) -> 
                System.out.println(key + ": " + value));
    }
}

/*
 * INTERVIEW QUESTIONS & ANSWERS:
 * 
 * Q1: How does adaptive difficulty work?
 * A: Track recent answers (sliding window). Calculate accuracy.
 *    If accuracy > 80%: increase difficulty
 *    If accuracy < 40%: decrease difficulty
 *    Keeps quiz challenging but not frustrating.
 * 
 * Q2: How to prevent question repetition?
 * A: Track usage count for each question. Always select least-used question
 *    from the appropriate difficulty level. Use LRU cache for better distribution.
 * 
 * Q3: How would you implement different scoring strategies?
 * A: Strategy pattern. StandardScoring: binary correct/wrong.
 *    PartialCreditScoring: award partial points for attempt/speed.
 *    TimeBonusScoring: bonus points for fast correct answers.
 * 
 * Q4: How to handle short answer/essay questions?
 * A: Use fuzzy matching (Levenshtein distance) for short answers.
 *    For essays: keyword matching, sentiment analysis, or manual grading workflow.
 *    Store "requires manual grading" flag.
 * 
 * Q5: How would you implement timed quizzes?
 * A: Track start time per question and overall quiz.
 *    Auto-submit when time expires. Use Timer/ScheduledExecutorService.
 * 
 * Q6: How to handle quiz pause/resume?
 * A: Store current state (question index, time elapsed) when pausing.
 *    Restore state when resuming. Don't count pause time in quiz duration.
 * 
 * Q7: How would you implement question categories/tags?
 * A: Add category field to Question. Index questions by category.
 *    Allow filtering/searching by category. Generate category-specific quizzes.
 * 
 * Q8: How to detect cheating in online quizzes?
 * A: Track time patterns (too fast = possible cheating).
 *    Randomize question order per student. Use question pools.
 *    Monitor tab switches, copy-paste events. Proctoring integration.
 * 
 * Q9: How would you implement quiz analytics?
 * A: Store all QuestionResult objects. Analyze:
 *    - Most missed questions (need review)
 *    - Average time per question type
 *    - Difficulty vs performance correlation
 *    - Learning curve over multiple attempts
 * 
 * Q10: How to scale for millions of users?
 * A: Stateless quiz engine. Store state in Redis/database.
 *    Cache questions in CDN. Use message queue for result processing.
 *    Shard question bank by category. Load balance quiz servers.
 */
