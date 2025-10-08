/**
* LLD #79: Minesweeper Engine
*
* Design Patterns Used:
* 1. State Pattern - Cell states (Hidden, Revealed, Flagged)
* 2. Strategy Pattern - Different difficulty levels
* 3. Observer Pattern - Game event notifications
* 4. Flyweight Pattern - Share immutable mine/number data
*
* Why These Patterns?
* - State: Clean cell state transitions
* - Strategy: Different board configurations for Easy/Medium/Hard
* - Observer: Notify UI of game events
* - Flyweight: Memory efficiency for large boards
    */

enum CellStatus { HIDDEN, REVEALED, FLAGGED }

enum Difficulty {
EASY(9, 9, 10),
MEDIUM(16, 16, 40),
HARD(30, 16, 99);

    int rows, cols, mines;
    
    Difficulty(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }
}

class Cell {
private boolean isMine;
private int adjacentMines;
private CellStatus status;

    public Cell() {
        this.isMine = false;
        this.adjacentMines = 0;
        this.status = CellStatus.HIDDEN;
    }
    
    // Getters and setters
    public boolean isMine() { return isMine; }
    public void setMine(boolean mine) { this.isMine = mine; }
    public int getAdjacentMines() { return adjacentMines; }
    public void setAdjacentMines(int count) { this.adjacentMines = count; }
    public CellStatus getStatus() { return status; }
    public void setStatus(CellStatus status) { this.status = status; }
}

interface MinesweeperObserver {
void onCellRevealed(int row, int col, int adjacentMines);
void onMineExploded(int row, int col);
void onGameWon(long timeElapsed);
void onFlagToggled(int row, int col, boolean flagged);
}

public class MinesweeperEngine {
private Cell[][] board;
private int rows, cols, totalMines;
private int revealedCells;
private boolean gameOver;
private boolean gameWon;
private boolean firstMove;
private long startTime;
private List<MinesweeperObserver> observers;

    private static final int[][] DIRECTIONS = {
        {-1,-1}, {-1,0}, {-1,1},
        {0,-1},          {0,1},
        {1,-1},  {1,0},  {1,1}
    };
    
    public MinesweeperEngine(Difficulty difficulty) {
        this(difficulty.rows, difficulty.cols, difficulty.mines);
    }
    
    public MinesweeperEngine(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = mines;
        this.board = new Cell[rows][cols];
        this.observers = new ArrayList<>();
        initializeBoard();
        reset();
    }
    
    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell();
            }
        }
    }
    
    // MAIN ALGORITHM: Place mines randomly avoiding first click
    private void placeMines(int excludeRow, int excludeCol) {
        Random random = new Random();
        int minesPlaced = 0;
        
        while (minesPlaced < totalMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);
            
            // Don't place mine at first click or already mined cell
            if ((row == excludeRow && col == excludeCol) || board[row][col].isMine()) {
                continue;
            }
            
            board[row][col].setMine(true);
            minesPlaced++;
        }
        
        // Calculate adjacent mines for all cells
        calculateAdjacentMines();
    }
    
    private void calculateAdjacentMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!board[i][j].isMine()) {
                    int count = countAdjacentMines(i, j);
                    board[i][j].setAdjacentMines(count);
                }
            }
        }
    }
    
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int[] dir : DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValid(newRow, newCol) && board[newRow][newCol].isMine()) {
                count++;
            }
        }
        return count;
    }
    
    // MAIN ALGORITHM: Reveal cell with cascade for empty cells
    public boolean revealCell(int row, int col) {
        if (!isValid(row, col) || gameOver) {
            return false;
        }
        
        Cell cell = board[row][col];
        
        // Handle first move - place mines avoiding this cell
        if (firstMove) {
            placeMines(row, col);
            firstMove = false;
            startTime = System.currentTimeMillis();
        }
        
        // Can't reveal flagged or already revealed cells
        if (cell.getStatus() != CellStatus.HIDDEN) {
            return false;
        }
        
        // Hit a mine - game over
        if (cell.isMine()) {
            cell.setStatus(CellStatus.REVEALED);
            gameOver = true;
            notifyMineExploded(row, col);
            revealAllMines();
            return false;
        }
        
        // Reveal cell
        revealCellRecursive(row, col);
        
        // Check win condition
        if (revealedCells == rows * cols - totalMines) {
            gameWon = true;
            gameOver = true;
            notifyGameWon();
        }
        
        return true;
    }
    
    // BFS/DFS cascade reveal for empty cells
    private void revealCellRecursive(int row, int col) {
        if (!isValid(row, col)) return;
        
        Cell cell = board[row][col];
        if (cell.getStatus() != CellStatus.HIDDEN || cell.isMine()) return;
        
        cell.setStatus(CellStatus.REVEALED);
        revealedCells++;
        notifyCellRevealed(row, col, cell.getAdjacentMines());
        
        // If no adjacent mines, reveal all neighbors
        if (cell.getAdjacentMines() == 0) {
            for (int[] dir : DIRECTIONS) {
                revealCellRecursive(row + dir[0], col + dir[1]);
            }
        }
    }
    
    public void toggleFlag(int row, int col) {
        if (!isValid(row, col) || gameOver) return;
        
        Cell cell = board[row][col];
        if (cell.getStatus() == CellStatus.REVEALED) return;
        
        if (cell.getStatus() == CellStatus.FLAGGED) {
            cell.setStatus(CellStatus.HIDDEN);
            notifyFlagToggled(row, col, false);
        } else {
            cell.setStatus(CellStatus.FLAGGED);
            notifyFlagToggled(row, col, true);
        }
    }
    
    private void revealAllMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isMine()) {
                    board[i][j].setStatus(CellStatus.REVEALED);
                }
            }
        }
    }
    
    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j].setMine(false);
                board[i][j].setAdjacentMines(0);
                board[i][j].setStatus(CellStatus.HIDDEN);
            }
        }
        revealedCells = 0;
        gameOver = false;
        gameWon = false;
        firstMove = true;
        startTime = 0;
    }
    
    private boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    
    // Observer pattern methods
    public void addObserver(MinesweeperObserver observer) {
        observers.add(observer);
    }
    
    private void notifyCellRevealed(int row, int col, int adjacentMines) {
        for (MinesweeperObserver obs : observers) {
            obs.onCellRevealed(row, col, adjacentMines);
        }
    }
    
    private void notifyMineExploded(int row, int col) {
        for (MinesweeperObserver obs : observers) {
            obs.onMineExploded(row, col);
        }
    }
    
    private void notifyGameWon() {
        long timeElapsed = System.currentTimeMillis() - startTime;
        for (MinesweeperObserver obs : observers) {
            obs.onGameWon(timeElapsed);
        }
    }
    
    private void notifyFlagToggled(int row, int col, boolean flagged) {
        for (MinesweeperObserver obs : observers) {
            obs.onFlagToggled(row, col, flagged);
        }
    }
    
    // Getters
    public boolean isGameOver() { return gameOver; }
    public boolean isGameWon() { return gameWon; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Cell getCell(int row, int col) { return board[row][col]; }
    
    public void displayBoard() {
        System.out.println("\nMinesweeper Board:");
        System.out.print("   ");
        for (int j = 0; j < cols; j++) {
            System.out.printf("%2d ", j);
        }
        System.out.println();
        
        for (int i = 0; i < rows; i++) {
            System.out.printf("%2d ", i);
            for (int j = 0; j < cols; j++) {
                Cell cell = board[i][j];
                if (cell.getStatus() == CellStatus.HIDDEN) {
                    System.out.print(" . ");
                } else if (cell.getStatus() == CellStatus.FLAGGED) {
                    System.out.print(" F ");
                } else if (cell.isMine()) {
                    System.out.print(" * ");
                } else {
                    int count = cell.getAdjacentMines();
                    System.out.printf(" %d ", count == 0 ? ' ' : count);
                }
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        MinesweeperEngine game = new MinesweeperEngine(Difficulty.EASY);
        
        game.addObserver(new MinesweeperObserver() {
            public void onCellRevealed(int row, int col, int adjacentMines) {
                System.out.println("Revealed: (" + row + "," + col + ") - Adjacent mines: " + adjacentMines);
            }
            
            public void onMineExploded(int row, int col) {
                System.out.println("BOOM! Mine exploded at (" + row + "," + col + ")");
            }
            
            public void onGameWon(long timeElapsed) {
                System.out.println("You WON! Time: " + (timeElapsed / 1000.0) + " seconds");
            }
            
            public void onFlagToggled(int row, int col, boolean flagged) {
                System.out.println("Flag " + (flagged ? "placed" : "removed") + " at (" + row + "," + col + ")");
            }
        });
        
        // Simulate game
        System.out.println("Starting Minesweeper!");
        game.displayBoard();
        
        game.revealCell(0, 0);
        game.displayBoard();
        
        game.toggleFlag(1, 1);
        game.displayBoard();
    }
}

/*
* INTERVIEW QUESTIONS & ANSWERS:
*
* Q1: How do you implement the cascade reveal for empty cells?
* A: Use BFS or DFS. When revealing a cell with 0 adjacent mines, recursively
*    reveal all 8 neighbors. Use visited check to avoid infinite loops.
*    Time: O(N) where N = cells revealed. Space: O(N) for recursion stack.
*
* Q2: How do you ensure first click never hits a mine?
* A: Delay mine placement until after first click. Place mines avoiding the
*    clicked cell (and optionally its neighbors for easier start).
*
* Q3: How to optimize for very large boards (100x100)?
* A: Use lazy evaluation. Only calculate adjacent mines when cells are revealed.
*    Use spatial data structures (quadtree) for large sparse boards.
*
* Q4: How would you implement "chord" feature (reveal neighbors when enough flags)?
* A: When clicking revealed number, count adjacent flags. If count equals the
*    number, reveal all unflagged neighbors. Risky if flags are wrong!
*
* Q5: How to detect when game is won?
* A: Track revealed cells. Win when: revealedCells == totalCells - totalMines.
*    Alternative: Track flagged mines count == totalMines AND all correct.
*
* Q6: How would you implement different difficulty levels?
* A: Use Strategy pattern. Each difficulty has board size and mine count.
*    Easy: 9x9 with 10 mines, Medium: 16x16 with 40, Hard: 30x16 with 99.
*
* Q7: How to implement undo feature?
* A: Store game state snapshots (Command pattern). Each action creates snapshot.
*    Challenging: Random mine placement makes undo non-deterministic.
*    Solution: Store RNG seed with first move.
*
* Q8: How would you implement custom board shapes (hexagonal)?
* A: Abstract coordinate system. Use strategy for neighbor calculation.
*    Hexagonal: 6 neighbors instead of 8. Use cube coordinates for hex grids.
*
* Q9: How to generate interesting/solvable boards?
* A: Check if board is solvable without guessing. Use constraint satisfaction.
*    Reject boards requiring probability-based guessing. Very computationally expensive.
*
* Q10: How to implement multiplayer competitive minesweeper?
* A: Shared board, players take turns or real-time. Track who reveals each cell.
*    Scoring: Points for revealing cells, penalty for hitting mines.
*    First to reveal most cells wins.
     */
