package lld;

import java.util.*;

/**
 * LLD #78: Snake Game Engine
 * 
 * Design Patterns Used:
 * 1. State Pattern - Game states (Playing, GameOver, Paused)
 * 2. Command Pattern - Direction commands
 * 3. Observer Pattern - Score and game event notifications
 * 
 * Why These Patterns?
 * - State: Clean state transitions and behavior
 * - Command: Encapsulate direction changes as commands
 * - Observer: Decouple game logic from UI updates
 * 
 * Key Components:
 * - Grid: Game board representation
 * - Snake: Linked list of body segments
 * - Food: Random food placement
 * - CollisionDetector: Wall and self-collision detection
 * 
 * Time Complexity: O(N) for collision detection where N is snake length
 * Space Complexity: O(W*H) for grid where W=width, H=height
 */

class Point {
    int x, y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);
    
    int dx, dy;
    
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    public Direction opposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return this;
        }
    }
}

enum SnakeGameState {
    PLAYING, PAUSED, GAME_OVER
}

// Observer Pattern
interface GameObserver {
    void onScoreUpdate(int score);
    void onGameOver(int finalScore);
    void onFoodEaten(Point position);
}

class Snake {
    private Deque<Point> body;
    private Direction currentDirection;
    
    public Snake(Point startPosition) {
        body = new LinkedList<>();
        body.add(startPosition);
        currentDirection = Direction.RIGHT;
    }
    
    public Point getHead() {
        return body.getFirst();
    }
    
    public Deque<Point> getBody() {
        return body;
    }
    
    public Direction getDirection() {
        return currentDirection;
    }
    
    public void setDirection(Direction newDirection) {
        // Cannot reverse direction
        if (newDirection != currentDirection.opposite()) {
            currentDirection = newDirection;
        }
    }
    
    // MAIN ALGORITHM: Move snake forward
    public Point move() {
        Point head = getHead();
        Point newHead = new Point(
            head.x + currentDirection.dx,
            head.y + currentDirection.dy
        );
        
        body.addFirst(newHead);
        Point tail = body.removeLast();
        return tail; // Return removed tail for potential restoration
    }
    
    public void grow() {
        // Add the last tail segment back
        Point tail = body.getLast();
        Point newTail = new Point(tail.x, tail.y);
        body.addLast(newTail);
    }
    
    public boolean checkSelfCollision() {
        Point head = getHead();
        int count = 0;
        for (Point segment : body) {
            if (segment.equals(head)) count++;
            if (count > 1) return true;
        }
        return false;
    }
    
    public int getLength() {
        return body.size();
    }
}

class GameGrid {
    private int width;
    private int height;
    private Set<Point> obstacles;
    
    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.obstacles = new HashSet<>();
    }
    
    public boolean isWithinBounds(Point p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }
    
    public boolean hasObstacle(Point p) {
        return obstacles.contains(p);
    }
    
    public void addObstacle(Point p) {
        obstacles.add(p);
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

class Food {
    private Point position;
    private Random random;
    
    public Food() {
        random = new Random();
    }
    
    public Point getPosition() {
        return position;
    }
    
    // MAIN ALGORITHM: Generate random food position
    public void spawn(GameGrid grid, Snake snake) {
        Set<Point> occupiedPositions = new HashSet<>(snake.getBody());
        List<Point> availablePositions = new ArrayList<>();
        
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Point p = new Point(x, y);
                if (!occupiedPositions.contains(p) && !grid.hasObstacle(p)) {
                    availablePositions.add(p);
                }
            }
        }
        
        if (!availablePositions.isEmpty()) {
            position = availablePositions.get(random.nextInt(availablePositions.size()));
        }
    }
}

class CollisionDetector {
    public boolean checkWallCollision(Point head, GameGrid grid) {
        return !grid.isWithinBounds(head);
    }
    
    public boolean checkSelfCollision(Snake snake) {
        return snake.checkSelfCollision();
    }
    
    public boolean checkFoodCollision(Point head, Food food) {
        return head.equals(food.getPosition());
    }
}

public class SnakeGameEngine {
    private GameGrid grid;
    private Snake snake;
    private Food food;
    private CollisionDetector collisionDetector;
    private SnakeGameState state;
    private int score;
    private List<GameObserver> observers;
    private long moveDelay; // milliseconds
    
    public SnakeGameEngine(int width, int height) {
        this.grid = new GameGrid(width, height);
        this.snake = new Snake(new Point(width / 2, height / 2));
        this.food = new Food();
        this.collisionDetector = new CollisionDetector();
        this.state = SnakeGameState.PLAYING;
        this.score = 0;
        this.observers = new ArrayList<>();
        this.moveDelay = 200; // Default: 200ms per move
        
        food.spawn(grid, snake);
    }
    
    // MAIN ALGORITHM: Game tick/update
    public void update() {
        if (state != SnakeGameState.PLAYING) {
            return;
        }
        
        // Move snake
        snake.move();
        Point head = snake.getHead();
        
        // Check collisions
        if (collisionDetector.checkWallCollision(head, grid) || 
            collisionDetector.checkSelfCollision(snake)) {
            gameOver();
            return;
        }
        
        // Check food collision
        if (collisionDetector.checkFoodCollision(head, food)) {
            snake.grow();
            score += 10;
            notifyScoreUpdate();
            notifyFoodEaten(head);
            food.spawn(grid, snake);
            
            // Increase speed as score increases
            if (score % 50 == 0) {
                moveDelay = Math.max(50, moveDelay - 20);
            }
        }
    }
    
    public void changeDirection(Direction newDirection) {
        if (state == SnakeGameState.PLAYING) {
            snake.setDirection(newDirection);
        }
    }
    
    public void pause() {
        if (state == SnakeGameState.PLAYING) {
            state = SnakeGameState.PAUSED;
        }
    }
    
    public void resume() {
        if (state == SnakeGameState.PAUSED) {
            state = SnakeGameState.PLAYING;
        }
    }
    
    private void gameOver() {
        state = SnakeGameState.GAME_OVER;
        notifyGameOver();
    }
    
    public void reset() {
        snake = new Snake(new Point(grid.getWidth() / 2, grid.getHeight() / 2));
        food.spawn(grid, snake);
        state = SnakeGameState.PLAYING;
        score = 0;
        moveDelay = 200;
        notifyScoreUpdate();
    }
    
    // Observer methods
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    
    private void notifyScoreUpdate() {
        for (GameObserver observer : observers) {
            observer.onScoreUpdate(score);
        }
    }
    
    private void notifyGameOver() {
        for (GameObserver observer : observers) {
            observer.onGameOver(score);
        }
    }
    
    private void notifyFoodEaten(Point position) {
        for (GameObserver observer : observers) {
            observer.onFoodEaten(position);
        }
    }
    
    // Getters
    public Snake getSnake() { return snake; }
    public Food getFood() { return food; }
    public SnakeGameState getState() { return state; }
    public int getScore() { return score; }
    public long getMoveDelay() { return moveDelay; }
    
    public void displayState() {
        char[][] display = new char[grid.getHeight()][grid.getWidth()];
        for (int i = 0; i < grid.getHeight(); i++) {
            Arrays.fill(display[i], '.');
        }
        
        // Draw snake
        for (Point segment : snake.getBody()) {
            if (segment.equals(snake.getHead())) {
                display[segment.y][segment.x] = 'H';
            } else {
                display[segment.y][segment.x] = 'S';
            }
        }
        
        // Draw food
        Point foodPos = food.getPosition();
        if (foodPos != null) {
            display[foodPos.y][foodPos.x] = 'F';
        }
        
        // Print
        for (char[] row : display) {
            System.out.println(new String(row));
        }
        System.out.println("Score: " + score + " | Snake Length: " + snake.getLength());
    }
    
    public static void main(String[] args) throws InterruptedException {
        SnakeGameEngine game = new SnakeGameEngine(20, 10);
        
        // Add observer
        game.addObserver(new GameObserver() {
            public void onScoreUpdate(int score) {
                System.out.println("Score updated: " + score);
            }
            
            public void onGameOver(int finalScore) {
                System.out.println("Game Over! Final score: " + finalScore);
            }
            
            public void onFoodEaten(Point position) {
                System.out.println("Food eaten at: (" + position.x + "," + position.y + ")");
            }
        });
        
        // Simulate game
        System.out.println("Initial State:");
        game.displayState();
        
        for (int i = 0; i < 5; i++) {
            Thread.sleep(300);
            game.update();
            System.out.println("\nAfter move " + (i + 1) + ":");
            game.displayState();
        }
    }
}

/*
 * IMPORTANT INTERVIEW QUESTIONS & ANSWERS:
 * 
 * Q1: How do you represent the snake efficiently?
 * A: Use Deque (double-ended queue). Add new head at front, remove tail from back.
 *    This gives O(1) for both operations. LinkedList provides this functionality.
 * 
 * Q2: How do you detect self-collision?
 * A: Check if new head position already exists in snake body.
 *    Optimization: Use HashSet to store body positions for O(1) lookup instead of O(N).
 * 
 * Q3: How do you prevent the snake from reversing direction instantly?
 * A: Check if new direction is opposite of current direction. If yes, ignore the input.
 *    This prevents the snake from immediately colliding with itself.
 * 
 * Q4: How do you spawn food randomly without overlapping snake?
 * A: Generate list of all unoccupied positions, then pick randomly from this list.
 *    Time: O(W*H) worst case, but ensures valid placement.
 * 
 * Q5: How would you implement progressive difficulty?
 * A: Increase snake speed as score increases (reduce moveDelay).
 *    Alternative: Add obstacles, increase board size, add multiple food items.
 * 
 * Q6: How to handle input buffering for smooth control?
 * A: Use queue to buffer direction changes. Process one direction per game tick.
 *    This prevents losing inputs when multiple keys pressed quickly.
 * 
 * Q7: How would you implement multiplayer snake?
 * A: Each player has separate Snake object. Check collisions between snakes.
 *    If heads collide: both die. If head hits other's body: attacker dies.
 *    Share same food or have separate food for each player.
 * 
 * Q8: How to implement obstacles/walls?
 * A: Store obstacles as Set<Point> in GameGrid. Check collision with obstacles
 *    same as wall collision. Can add obstacles dynamically as difficulty increases.
 * 
 * Q9: How would you implement power-ups?
 * A: Create PowerUp class similar to Food. Types: speed boost, slow down, shrink,
 *    invincibility. Use Strategy pattern for different power-up effects.
 * 
 * Q10: How to optimize rendering for large grids?
 * A: Only render changed cells (diff from previous frame). Use dirty rectangles.
 *    For web: use Canvas API with requestAnimationFrame for smooth rendering.
 */
