package lld;

import java.util.*;

/**
 * LLD #76: Chess Engine Skeleton
 * 
 * Design Patterns Used:
 * 1. Strategy Pattern - Different piece movement strategies
 * 2. Factory Pattern - Piece creation
 * 3. Singleton Pattern - Board instance
 * 4. Command Pattern - Move execution and validation
 * 
 * Why These Patterns?
 * - Strategy: Each chess piece has different movement rules
 * - Factory: Centralized piece creation with proper initialization
 * - Command: Encapsulates move as object for validation, undo/redo
 * 
 * Key Components:
 * - Board: 8x8 grid representation
 * - Pieces: Different piece types with movement strategies
 * - Move: Encapsulates a chess move
 * - MoveValidator: Validates legal moves
 * - MoveGenerator: Generates all possible moves
 * 
 * Time Complexity: O(N) for move generation where N is number of pieces
 * Space Complexity: O(1) for board, O(M) for move list where M is possible moves
 */

class Position {
    int row, col;
    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

enum PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
}

enum Color {
    WHITE, BLACK
}

// Strategy Pattern - Movement Strategy
interface MovementStrategy {
    List<Position> getPossibleMoves(Position from, Board board);
}

class Piece {
    PieceType type;
    Color color;
    MovementStrategy strategy;
    boolean hasMoved;
    
    public Piece(PieceType type, Color color, MovementStrategy strategy) {
        this.type = type;
        this.color = color;
        this.strategy = strategy;
        this.hasMoved = false;
    }
    
    public List<Position> getPossibleMoves(Position from, Board board) {
        return strategy.getPossibleMoves(from, board);
    }
}

// Concrete Strategies
class PawnMovement implements MovementStrategy {
    @Override
    public List<Position> getPossibleMoves(Position from, Board board) {
        // MAIN ALGORITHM: Pawn movement
        List<Position> moves = new ArrayList<>();
        Piece piece = board.getPiece(from);
        int direction = piece.color == Color.WHITE ? -1 : 1;
        
        // Forward move
        Position forward = new Position(from.row + direction, from.col);
        if (forward.isValid() && board.isEmpty(forward)) {
            moves.add(forward);
            
            // Initial two-square move
            if (!piece.hasMoved) {
                Position doubleForward = new Position(from.row + 2 * direction, from.col);
                if (doubleForward.isValid() && board.isEmpty(doubleForward)) {
                    moves.add(doubleForward);
                }
            }
        }
        
        // Diagonal captures
        int[] cols = {from.col - 1, from.col + 1};
        for (int col : cols) {
            Position capture = new Position(from.row + direction, col);
            if (capture.isValid() && board.isOpponent(capture, piece.color)) {
                moves.add(capture);
            }
        }
        
        return moves;
    }
}

class KnightMovement implements MovementStrategy {
    @Override
    public List<Position> getPossibleMoves(Position from, Board board) {
        // MAIN ALGORITHM: Knight L-shaped moves
        List<Position> moves = new ArrayList<>();
        int[][] offsets = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        
        Piece piece = board.getPiece(from);
        for (int[] offset : offsets) {
            Position to = new Position(from.row + offset[0], from.col + offset[1]);
            if (to.isValid() && !board.isAlly(to, piece.color)) {
                moves.add(to);
            }
        }
        return moves;
    }
}

class SlidingMovement implements MovementStrategy {
    int[][] directions;
    
    public SlidingMovement(int[][] directions) {
        this.directions = directions;
    }
    
    @Override
    public List<Position> getPossibleMoves(Position from, Board board) {
        // MAIN ALGORITHM: Sliding pieces (Rook, Bishop, Queen)
        List<Position> moves = new ArrayList<>();
        Piece piece = board.getPiece(from);
        
        for (int[] dir : directions) {
            int row = from.row + dir[0];
            int col = from.col + dir[1];
            
            while (row >= 0 && row < 8 && col >= 0 && col < 8) {
                Position to = new Position(row, col);
                if (board.isEmpty(to)) {
                    moves.add(to);
                } else {
                    if (board.isOpponent(to, piece.color)) {
                        moves.add(to);
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
        return moves;
    }
}

class KingMovement implements MovementStrategy {
    @Override
    public List<Position> getPossibleMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] offsets = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        
        Piece piece = board.getPiece(from);
        for (int[] offset : offsets) {
            Position to = new Position(from.row + offset[0], from.col + offset[1]);
            if (to.isValid() && !board.isAlly(to, piece.color)) {
                moves.add(to);
            }
        }
        return moves;
    }
}

// Factory Pattern
class PieceFactory {
    public static Piece createPiece(PieceType type, Color color) {
        MovementStrategy strategy;
        switch (type) {
            case PAWN: strategy = new PawnMovement(); break;
            case KNIGHT: strategy = new KnightMovement(); break;
            case ROOK: strategy = new SlidingMovement(new int[][]{{0,1},{0,-1},{1,0},{-1,0}}); break;
            case BISHOP: strategy = new SlidingMovement(new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}}); break;
            case QUEEN: strategy = new SlidingMovement(new int[][]{{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}}); break;
            case KING: strategy = new KingMovement(); break;
            default: throw new IllegalArgumentException("Unknown piece type");
        }
        return new Piece(type, color, strategy);
    }
}

// Command Pattern
class Move {
    Position from, to;
    Piece piece;
    Piece captured;
    
    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
    }
}

class Board {
    private Piece[][] board;
    private Map<Color, Position> kingPositions;
    
    public Board() {
        board = new Piece[8][8];
        kingPositions = new HashMap<>();
        initializeBoard();
    }
    
    private void initializeBoard() {
        // Setup standard chess position
        // Back rank
        PieceType[] backRank = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, 
                                PieceType.QUEEN, PieceType.KING, PieceType.BISHOP, 
                                PieceType.KNIGHT, PieceType.ROOK};
        
        for (int col = 0; col < 8; col++) {
            board[0][col] = PieceFactory.createPiece(backRank[col], Color.BLACK);
            board[1][col] = PieceFactory.createPiece(PieceType.PAWN, Color.BLACK);
            board[6][col] = PieceFactory.createPiece(PieceType.PAWN, Color.WHITE);
            board[7][col] = PieceFactory.createPiece(backRank[col], Color.WHITE);
        }
        
        kingPositions.put(Color.WHITE, new Position(7, 4));
        kingPositions.put(Color.BLACK, new Position(0, 4));
    }
    
    public Piece getPiece(Position pos) {
        return board[pos.row][pos.col];
    }
    
    public boolean isEmpty(Position pos) {
        return board[pos.row][pos.col] == null;
    }
    
    public boolean isAlly(Position pos, Color color) {
        Piece piece = getPiece(pos);
        return piece != null && piece.color == color;
    }
    
    public boolean isOpponent(Position pos, Color color) {
        Piece piece = getPiece(pos);
        return piece != null && piece.color != color;
    }
    
    public void makeMove(Move move);
    public void undoMove(Move move);
    public boolean isInCheck(Color color);
    public List<Move> getAllLegalMoves(Color color);
}

class MoveValidator {
    public boolean isLegal(Move move, Board board) {
        // MAIN ALGORITHM: Move validation
        // 1. Check if piece exists
        Piece piece = board.getPiece(move.from);
        if (piece == null) return false;
        
        // 2. Check if move is in possible moves
        List<Position> possibleMoves = piece.getPossibleMoves(move.from, board);
        if (!possibleMoves.contains(move.to)) return false;
        
        // 3. Check if move leaves king in check
        board.makeMove(move);
        boolean inCheck = board.isInCheck(piece.color);
        board.undoMove(move);
        
        return !inCheck;
    }
}

class MoveGenerator {
    public List<Move> generateAllMoves(Board board, Color color) {
        // MAIN ALGORITHM: Generate all legal moves
        List<Move> moves = new ArrayList<>();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position from = new Position(row, col);
                Piece piece = board.getPiece(from);
                
                if (piece != null && piece.color == color) {
                    List<Position> positions = piece.getPossibleMoves(from, board);
                    for (Position to : positions) {
                        Move move = new Move(from, to);
                        moves.add(move);
                    }
                }
            }
        }
        
        return moves;
    }
}

public class ChessEngine {
    private Board board;
    private MoveValidator validator;
    private MoveGenerator generator;
    private Color currentTurn;
    private List<Move> moveHistory;
    
    public ChessEngine() {
        this.board = new Board();
        this.validator = new MoveValidator();
        this.generator = new MoveGenerator();
        this.currentTurn = Color.WHITE;
        this.moveHistory = new ArrayList<>();
    }
    
    public boolean makeMove(Position from, Position to) {
        Move move = new Move(from, to);
        move.piece = board.getPiece(from);
        
        if (validator.isLegal(move, board)) {
            board.makeMove(move);
            moveHistory.add(move);
            currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
            return true;
        }
        return false;
    }
    
    public List<Move> getLegalMoves() {
        return generator.generateAllMoves(board, currentTurn);
    }
    
    public boolean isCheckmate();
    public boolean isStalemate();
    public boolean isDraw();
    
    public static void main(String[] args) {
        ChessEngine engine = new ChessEngine();
        
        // Example: e2-e4 (pawn move)
        boolean success = engine.makeMove(new Position(6, 4), new Position(4, 4));
        System.out.println("Move successful: " + success);
        
        // Get all legal moves for current player
        List<Move> moves = engine.getLegalMoves();
        System.out.println("Legal moves available: " + moves.size());
    }
}

/*
 * IMPORTANT INTERVIEW QUESTIONS & ANSWERS:
 * 
 * Q1: How do you represent the chess board efficiently?
 * A: 2D array (8x8) for simplicity. For advanced engines, bitboards (64-bit integers)
 *    are more efficient for move generation and position evaluation.
 * 
 * Q2: How do you validate if a move leaves the king in check?
 * A: Make the move temporarily, scan all opponent pieces to see if any can attack
 *    the king's position, then undo the move. This is the "make-unmake" technique.
 * 
 * Q3: How would you implement castling?
 * A: Check: (1) King and rook haven't moved, (2) No pieces between them,
 *    (3) King not in check, (4) King doesn't pass through or land on attacked square.
 * 
 * Q4: How to detect checkmate vs stalemate?
 * A: Generate all legal moves for current player. If no legal moves:
 *    - If in check → Checkmate
 *    - If not in check → Stalemate
 * 
 * Q5: How would you optimize move generation for a chess AI?
 * A: (1) Use bitboards, (2) Alpha-beta pruning, (3) Move ordering (check captures first),
 *    (4) Transposition tables, (5) Iterative deepening.
 * 
 * Q6: How to handle en passant and pawn promotion?
 * A: En passant: Track last move, check if pawn moved two squares. Store this state.
 *    Promotion: When pawn reaches last rank, replace with chosen piece (Queen default).
 * 
 * Q7: How would you implement undo/redo?
 * A: Use Command pattern. Store move history with captured pieces and game state.
 *    For undo: pop last move, restore board. For redo: replay the move.
 * 
 * Q8: How to evaluate board position for AI?
 * A: Material count (piece values), positional factors (center control, king safety),
 *    mobility (number of legal moves), pawn structure, piece coordination.
 * 
 * Q9: What's the time complexity of move generation?
 * A: O(N) where N is number of pieces on board. For each piece, checking moves is O(1)
 *    for knights/kings, O(7) max for sliding pieces (queen/rook/bishop).
 * 
 * Q10: How would you scale this for multiplayer online chess?
 * A: (1) Separate game logic from UI, (2) Use WebSockets for real-time updates,
 *     (3) Server validates all moves, (4) Store game state in database,
 *     (5) Implement timeouts and reconnection handling.
 */
