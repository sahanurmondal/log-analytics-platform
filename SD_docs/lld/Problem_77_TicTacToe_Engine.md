/**
* LLD #77: TicTacToe Engine
*
* Design Patterns Used:
* 1. State Pattern - Game state management (Playing, Won, Draw)
* 2. Strategy Pattern - Different win detection strategies (3x3, NxN)
* 3. Template Method - Game flow template
*
* Why These Patterns?
* - State: Clean separation of game states and transitions
* - Strategy: Support different board sizes and win conditions
* - Template Method: Standardize game flow while allowing customization
*
* Key Components:
* - Board: NxN grid representation
* - Player: Player identification and symbol
* - WinDetector: Checks win conditions (rows, cols, diagonals)
* - GameState: Current state of the game
*
* Time Complexity: O(N) for win detection where N is board size
* Space Complexity: O(N²) for board storage
  */

enum CellState {
EMPTY, X, O
}

enum GameStateType {
PLAYING, X_WON, O_WON, DRAW
}

class Player {
String name;
CellState symbol;

    public Player(String name, CellState symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}

// State Pattern - Different game states
interface GameState {
GameStateType getType();
boolean canMakeMove();
String getStatusMessage();
}

class PlayingState implements GameState {
public GameStateType getType() { return GameStateType.PLAYING; }
public boolean canMakeMove() { return true; }
public String getStatusMessage() { return "Game in progress"; }
}

class WonState implements GameState {
CellState winner;

    public WonState(CellState winner) {
        this.winner = winner;
    }
    
    public GameStateType getType() {
        return winner == CellState.X ? GameStateType.X_WON : GameStateType.O_WON;
    }
    public boolean canMakeMove() { return false; }
    public String getStatusMessage() { return winner + " wins!"; }
}

class DrawState implements GameState {
public GameStateType getType() { return GameStateType.DRAW; }
public boolean canMakeMove() { return false; }
public String getStatusMessage() { return "Game is a draw!"; }
}

// Strategy Pattern - Win Detection
interface WinDetector {
boolean checkWin(CellState[][] board, int row, int col, CellState player);
}

class StandardWinDetector implements WinDetector {
private int boardSize;

    public StandardWinDetector(int boardSize) {
        this.boardSize = boardSize;
    }
    
    @Override
    public boolean checkWin(CellState[][] board, int row, int col, CellState player) {
        // MAIN ALGORITHM: Check all directions from last move
        return checkRow(board, row, player) ||
               checkColumn(board, col, player) ||
               checkDiagonal(board, player) ||
               checkAntiDiagonal(board, player);
    }
    
    private boolean checkRow(CellState[][] board, int row, CellState player) {
        for (int col = 0; col < boardSize; col++) {
            if (board[row][col] != player) return false;
        }
        return true;
    }
    
    private boolean checkColumn(CellState[][] board, int col, CellState player) {
        for (int row = 0; row < boardSize; row++) {
            if (board[row][col] != player) return false;
        }
        return true;
    }
    
    private boolean checkDiagonal(CellState[][] board, CellState player) {
        for (int i = 0; i < boardSize; i++) {
            if (board[i][i] != player) return false;
        }
        return true;
    }
    
    private boolean checkAntiDiagonal(CellState[][] board, CellState player) {
        for (int i = 0; i < boardSize; i++) {
            if (board[i][boardSize - 1 - i] != player) return false;
        }
        return true;
    }
}

class TicTacToeBoard {
private int size;
private CellState[][] cells;
private int movesCount;

    public TicTacToeBoard(int size) {
        this.size = size;
        this.cells = new CellState[size][size];
        this.movesCount = 0;
        initializeBoard();
    }
    
    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = CellState.EMPTY;
            }
        }
    }
    
    public boolean makeMove(int row, int col, CellState player) {
        if (!isValidMove(row, col)) {
            return false;
        }
        
        cells[row][col] = player;
        movesCount++;
        return true;
    }
    
    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < size && 
               col >= 0 && col < size && 
               cells[row][col] == CellState.EMPTY;
    }
    
    public boolean isFull() {
        return movesCount == size * size;
    }
    
    public CellState[][] getCells() {
        return cells;
    }
    
    public int getSize() {
        return size;
    }
    
    public void reset() {
        initializeBoard();
        movesCount = 0;
    }
    
    public void display() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(cells[i][j] == CellState.EMPTY ? "." : cells[i][j]);
                if (j < size - 1) System.out.print(" | ");
            }
            System.out.println();
            if (i < size - 1) {
                System.out.println("-".repeat(size * 4 - 1));
            }
        }
    }
}

public class TicTacToeEngine {
private TicTacToeBoard board;
private Player player1;
private Player player2;
private Player currentPlayer;
private GameState gameState;
private WinDetector winDetector;
private List<String> moveHistory;

    public TicTacToeEngine(int boardSize) {
        this.board = new TicTacToeBoard(boardSize);
        this.player1 = new Player("Player 1", CellState.X);
        this.player2 = new Player("Player 2", CellState.O);
        this.currentPlayer = player1;
        this.gameState = new PlayingState();
        this.winDetector = new StandardWinDetector(boardSize);
        this.moveHistory = new ArrayList<>();
    }
    
    // MAIN ALGORITHM: Make move and update game state
    public boolean makeMove(int row, int col) {
        if (!gameState.canMakeMove()) {
            System.out.println("Game is over: " + gameState.getStatusMessage());
            return false;
        }
        
        if (!board.makeMove(row, col, currentPlayer.symbol)) {
            System.out.println("Invalid move!");
            return false;
        }
        
        // Record move
        moveHistory.add(currentPlayer.name + " -> (" + row + "," + col + ")");
        
        // Check win condition
        if (winDetector.checkWin(board.getCells(), row, col, currentPlayer.symbol)) {
            gameState = new WonState(currentPlayer.symbol);
            return true;
        }
        
        // Check draw condition
        if (board.isFull()) {
            gameState = new DrawState();
            return true;
        }
        
        // Switch player
        switchPlayer();
        return true;
    }
    
    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
    
    public void displayBoard() {
        board.display();
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void reset() {
        board.reset();
        currentPlayer = player1;
        gameState = new PlayingState();
        moveHistory.clear();
    }
    
    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }
    
    public static void main(String[] args) {
        TicTacToeEngine game = new TicTacToeEngine(3);
        
        // Simulate a game
        System.out.println("Starting TicTacToe Game!");
        game.displayBoard();
        
        // X wins scenario
        game.makeMove(0, 0); // X
        game.makeMove(1, 0); // O
        game.makeMove(0, 1); // X
        game.makeMove(1, 1); // O
        game.makeMove(0, 2); // X wins!
        
        System.out.println("\nFinal Board:");
        game.displayBoard();
        System.out.println("\nGame Status: " + game.getGameState().getStatusMessage());
        
        System.out.println("\nMove History:");
        game.getMoveHistory().forEach(System.out::println);
    }
}

/*
* IMPORTANT INTERVIEW QUESTIONS & ANSWERS:
*
* Q1: How do you detect a win efficiently?
* A: After each move, only check the row, column, and diagonals (if applicable)
*    containing the last move. Don't scan entire board. Time: O(N) where N = board size.
*
* Q2: How would you scale this to a larger board (e.g., 15x15 with 5 in a row)?
* A: Use sliding window approach. After each move, check windows of size 5 in
*    all 4 directions (horizontal, vertical, 2 diagonals). Time: O(1) per move.
*
* Q3: How do you optimize win detection for very large boards?
* A: Track count of consecutive pieces in each direction from each cell.
*    When placing a piece, update counts in 4 directions. Check if any count >= target.
*    Time: O(1) per move, Space: O(N²) for counts.
*
* Q4: How would you implement an AI opponent?
* A: Start with Minimax algorithm with alpha-beta pruning:
*    - Max player tries to maximize score
*    - Min player tries to minimize score
*    - Evaluate board positions (win=+10, loss=-10, draw=0)
*    - Prune branches that can't improve result
*    - For TicTacToe, perfect play possible (always draw)
*
* Q5: How to handle undo/redo operations?
* A: Use Command pattern with move history stack:
*    - Each move is a Command object
*    - Undo: pop from history, restore board state
*    - Redo: keep separate redo stack
*    - Store enough info to reverse moves
*
* Q6: How would you support different win conditions (e.g., 4 in a row on 6x6)?
* A: Use Strategy pattern for WinDetector. Pass win length as parameter.
*    Check windows of specified length in all directions.
*
* Q7: How to implement online multiplayer?
* A: Separate game logic from UI:
*    - Server hosts game state
*    - Players send moves via WebSocket/REST
*    - Server validates and broadcasts moves
*    - Handle disconnections and timeouts
*    - Synchronize game state across clients
*
* Q8: What's the state space complexity of TicTacToe?
* A: For 3x3: approximately 5,478 valid positions (many impossible)
*    Each cell can be X, O, or empty: 3^9 = 19,683 theoretical states
*    But many violate rules (too many Xs, impossible positions)
*
* Q9: How would you implement tournament mode with multiple games?
* A: Create Game class as separate entity:
*    - Tournament manages multiple Game instances
*    - Track wins/losses for each player
*    - Implement Swiss system or round-robin
*    - Store game history and statistics
*
* Q10: How to make the game more challenging/interesting?
* A: Variations:
*    - 3D TicTacToe (3x3x3 cube)
*    - Quantum TicTacToe (superposition of moves)
*    - Ultimate TicTacToe (9 boards in 3x3 grid)
*    - Time limits per move
*    - Random cell disabling each turn
       */
