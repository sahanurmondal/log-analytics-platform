# Problem 76: Chess Engine Skeleton (Strategy + Factory + Command Pattern)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a chess engine skeleton supporting board representation, move generation, move validation, and basic game rules enforcement.

**Assumptions / Scope:**
- Standard 8x8 chess board with traditional piece setup
- All standard chess pieces with their unique movement rules
- Move validation including check detection
- Legal move generation for all pieces
- Basic game state management (turn tracking, move history)
- Support for special moves: castling, en passant, pawn promotion
- Scale: Single game instance, real-time move validation
- Out of scope: AI/minimax algorithm, online multiplayer, time controls

**Non-Functional Goals:**
- Move validation in < 10ms
- Generate all legal moves in < 50ms
- Memory efficient board representation
- Extensible for adding new piece types
- Support undo/redo operations

### 2. Core Requirements

**Functional:**
- Initialize standard chess board
- Validate moves according to piece rules
- Detect check, checkmate, and stalemate
- Generate all legal moves for current position
- Track game state (current turn, move history)
- Support castling (kingside/queenside)
- Handle en passant capture
- Implement pawn promotion
- Provide move history for undo/redo

**Non-Functional:**
- **Performance**: O(N) move generation where N = number of pieces
- **Correctness**: 100% accurate rule enforcement
- **Extensibility**: Easy to add variants or new pieces
- **Testability**: Clear separation of concerns
- **Memory**: O(1) space for board state

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Efficient Move Generation**
- **Problem**: Generate all legal moves quickly for various piece types
- **Solution**: Strategy pattern for piece-specific movement algorithms
- **Algorithm**:
```
For sliding pieces (Rook, Bishop, Queen):
1. Define direction vectors (e.g., Rook: ↑↓←→, Bishop: ↗↘↖↙)
2. For each direction:
   - Slide in direction until hitting board edge, own piece, or opponent piece
   - Add all valid positions to move list
   - Stop at first piece encountered

Time: O(7) max per sliding piece (7 squares in any direction)
Space: O(M) where M = number of legal moves
```

**Challenge 2: Check Detection**
- **Problem**: Determine if king is under attack after any move
- **Solution**: Make-unmake technique with attack validation
- **Algorithm**:
```
To validate if move is legal:
1. Make the move on board (temporarily)
2. Find king position for current player
3. For each opponent piece:
   - Generate its possible moves
   - If any move targets king position → return false (illegal)
4. Unmake the move
5. Return true if king is safe

Time: O(N) where N = opponent pieces
Space: O(1) using board mutation
```

**Challenge 3: Special Move Handling**
- **Problem**: Castling requires checking multiple conditions
- **Solution**: Dedicated validation for special moves
- **Algorithm**:
```
Castling validation:
1. Check king hasn't moved (track hasMoved flag)
2. Check rook hasn't moved
3. Verify no pieces between king and rook
4. Ensure king is not in check
5. Verify king doesn't pass through attacked square
6. Ensure king doesn't land on attacked square

En Passant:
1. Track last move in game state
2. If last move was pawn advancing 2 squares
3. Allow adjacent enemy pawn to capture "in passing"
4. Remove captured pawn from board
```

### 4. Design Patterns Applied

**Pattern 1: Strategy Pattern**
- **Usage**: Different movement strategies for each piece type
- **Why**: Each piece has unique movement rules
- **Implementation**:
```
MovementStrategy interface with getPossibleMoves()
Concrete strategies: PawnMovement, KnightMovement, SlidingMovement
Each piece holds a reference to its strategy
Encapsulates variation in movement algorithms
```

**Pattern 2: Factory Pattern**
- **Usage**: PieceFactory for creating pieces with correct strategies
- **Why**: Centralized piece creation with proper initialization
- **Implementation**:
```
PieceFactory.createPiece(type, color)
Returns Piece with appropriate MovementStrategy
Ensures consistency in piece creation
Easy to extend with new piece types
```

**Pattern 3: Command Pattern**
- **Usage**: Move as a command object
- **Why**: Encapsulate move for validation, history, undo/redo
- **Implementation**:
```
Move class stores: from, to, piece, captured
Makes move undoable by storing previous state
Enables move history and replay
Supports validation as a transaction
```

**Pattern 4: Singleton (Optional)**
- **Usage**: Board instance for single game
- **Why**: Ensure single source of truth for board state
- **Trade-off**: Less flexible for multiple games, but simpler

### 5. Class Structure

```
Position
├── row, col
└── isValid(), equals(), hashCode()

PieceType enum: PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
Color enum: WHITE, BLACK

MovementStrategy interface
├── getPossibleMoves(from, board)
└── Implementations:
    ├── PawnMovement
    ├── KnightMovement
    ├── SlidingMovement (Rook/Bishop/Queen)
    └── KingMovement

Piece
├── type, color, strategy, hasMoved
└── getPossibleMoves()

PieceFactory
└── createPiece(type, color) → Piece

Move (Command)
├── from, to, piece, captured
└── execute(), undo()

Board
├── board[][] (8x8 array)
├── kingPositions (Map<Color, Position>)
├── initializeBoard()
├── getPiece(), isEmpty(), isAlly(), isOpponent()
├── makeMove(), undoMove()
├── isInCheck(), getAllLegalMoves()
└── Methods for special moves

MoveValidator
└── isLegal(move, board)

MoveGenerator
└── generateAllMoves(board, color)

ChessEngine (Main Controller)
├── board, validator, generator
├── currentTurn, moveHistory
├── makeMove()
├── getLegalMoves()
├── isCheckmate(), isStalemate(), isDraw()
└── Main game loop
```

### 6. Algorithm Deep Dive

**Algorithm 1: Legal Move Generation**
```
Function generateAllMoves(board, color):
    moves = []
    
    For each row in [0..7]:
        For each col in [0..7]:
            piece = board[row][col]
            
            If piece exists AND piece.color == color:
                possibleMoves = piece.strategy.getPossibleMoves(position, board)
                
                For each toPosition in possibleMoves:
                    move = Move(position, toPosition)
                    
                    // Validate move doesn't leave king in check
                    If isLegalMove(move, board):
                        moves.add(move)
    
    Return moves

Time: O(N * M) where N = pieces, M = avg moves per piece
Space: O(K) where K = total legal moves
```

**Algorithm 2: Sliding Piece Movement (Rook/Bishop/Queen)**
```
Function getSlidingMoves(from, directions, board):
    moves = []
    
    For each direction in directions:
        row = from.row + direction.row
        col = from.col + direction.col
        
        While (row, col) is valid:
            If square is empty:
                moves.add(Position(row, col))
            Else:
                If square has opponent piece:
                    moves.add(Position(row, col))  // Capture
                Break  // Stop sliding
            
            row += direction.row
            col += direction.col
    
    Return moves

Time: O(D * L) where D = directions, L = max distance (7)
Space: O(M) where M = moves found
```

**Algorithm 3: Check Detection**
```
Function isInCheck(board, color):
    kingPos = board.getKingPosition(color)
    opponentColor = opposite(color)
    
    For each position on board:
        piece = board[position]
        
        If piece AND piece.color == opponentColor:
            moves = piece.getPossibleMoves(position, board)
            
            If kingPos in moves:
                Return true  // King is under attack
    
    Return false

Time: O(N) where N = opponent pieces
Space: O(1)
```

**Algorithm 4: Checkmate Detection**
```
Function isCheckmate(board, color):
    // Must be in check to be checkmate
    If NOT isInCheck(board, color):
        Return false
    
    // Try to find any legal move
    legalMoves = generateAllMoves(board, color)
    
    Return legalMoves.isEmpty()

Stalemate: Same logic but NOT in check
Time: O(N * M) for move generation
```

### 7. Edge Cases & Handling

1. **Castling through check**: Validate king doesn't pass through attacked square
2. **En passant timing**: Only valid immediately after opponent's pawn double-move
3. **Pawn promotion**: Automatically or allow user choice (Queen/Rook/Bishop/Knight)
4. **Fifty-move rule**: Track half-moves without capture or pawn move
5. **Threefold repetition**: Hash board positions to detect repetition
6. **Insufficient material**: King vs King, King+Bishop vs King, etc.
7. **Double check**: King must move (cannot block or capture)
8. **Pinned pieces**: Piece cannot move if it exposes king to check

### 8. Complexity Analysis

**Time Complexities:**
- Initialize board: O(1) - fixed 32 pieces
- Make move: O(1) - direct array access
- Validate single move: O(N) - check detection with N opponent pieces
- Generate all legal moves: O(N * M) - N pieces, M avg moves per piece
- Check detection: O(N) - scan all opponent pieces
- Checkmate detection: O(N * M) - must generate all moves

**Space Complexities:**
- Board storage: O(1) - fixed 8x8 = 64 squares
- Move history: O(M) - M moves played
- Legal moves list: O(K) - K legal moves in position
- Position hashing: O(H) - H unique positions for repetition detection

**Optimization Opportunities:**
- Use bitboards (64-bit integers) instead of 2D array
- Pre-compute attack tables for knights and kings
- Incremental update of attacked squares
- Move ordering for alpha-beta pruning (AI)
- Transposition tables for position caching

### 9. Important Interview Questions & Answers

**Q1: How do you represent the chess board efficiently?**
**A:** For basic engine, 2D array (8x8) is simple and readable. For high-performance engines:
- **Bitboards**: 64-bit integers where each bit represents a square
- Use separate bitboard for each piece type and color
- Fast bitwise operations for move generation
- Trade-off: More complex but 10-100x faster for AI search

**Q2: How do you validate if a move leaves the king in check?**
**A:** Use "make-unmake" technique:
1. Temporarily make the move on the board
2. Scan all opponent pieces to see if any can attack the king
3. Undo the move
4. Return validity based on whether king was attacked
This is O(N) but necessary for legal move validation.

**Q3: How would you implement castling?**
**A:** Check five conditions:
1. King hasn't moved (track `hasMoved` flag)
2. Chosen rook hasn't moved
3. No pieces between king and rook
4. King is not currently in check
5. King doesn't pass through or land on attacked square
Must check conditions in order for efficiency.

**Q4: How to detect checkmate vs stalemate?**
**A:** 
- Generate all legal moves for current player
- If no legal moves available:
  - If king is in check → **Checkmate** (loss)
  - If king is NOT in check → **Stalemate** (draw)
- Difference is solely the check status

**Q5: How would you optimize move generation for a chess AI?**
**A:** Multiple optimizations:
1. **Bitboards**: Fast bitwise operations
2. **Move ordering**: Search captures and checks first (alpha-beta)
3. **Transposition tables**: Cache evaluated positions
4. **Iterative deepening**: Reuse previous search results
5. **Quiescence search**: Extend search at tactical positions
6. **Null move pruning**: Skip moves to find refutations faster

**Q6: How to handle en passant and pawn promotion?**
**A:** 
- **En passant**: Store last move in game state. If last move was pawn advancing 2 squares, allow adjacent enemy pawns to capture "in passing" on next turn only. Remove captured pawn from board.
- **Pawn promotion**: When pawn reaches rank 8 (white) or rank 1 (black), replace with Queen (default) or allow user to choose piece. Update board and piece type.

**Q7: How would you implement undo/redo?**
**A:** Use Command pattern:
- Store complete move history (List<Move>)
- Each Move stores: from, to, piece, captured piece, special flags
- **Undo**: Pop last move, reverse the operation, restore captured piece
- **Redo**: Keep separate redo stack, replay undone moves
- Store enough state to fully reconstruct position

**Q8: How to evaluate board position for AI?**
**A:** Material + Positional evaluation:
- **Material**: P=1, N=3, B=3, R=5, Q=9 (pawn units)
- **Position**: Center control, king safety, pawn structure
- **Mobility**: Number of legal moves (more = better)
- **Piece coordination**: Pieces supporting each other
- Use piece-square tables for positional bonuses

**Q9: What's the time complexity of move generation?**
**A:** O(N * M) where:
- N = number of pieces on board (max 32, typically 16-20)
- M = average moves per piece (varies: knight=8 max, queen=27 max)
- In practice: ~30-50 legal moves in middle game position
- With validation (check): Each move costs O(N) → O(N² * M) worst case

**Q10: How would you scale this for multiplayer online chess?**
**A:** Architecture changes:
1. **Separation**: Game logic (backend) vs UI (frontend)
2. **Communication**: WebSockets for real-time updates
3. **Validation**: Server-side move validation (never trust client)
4. **State**: Store games in database (PostgreSQL/MongoDB)
5. **Concurrency**: Lock game state during move validation
6. **Timeouts**: Track time per player, auto-forfeit on timeout
7. **Reconnection**: Allow players to reconnect and resume
8. **Matchmaking**: Separate service for pairing players

**Q11: How do you handle the 50-move rule and threefold repetition?**
**A:** 
- **50-move rule**: Track halfmove counter. Reset on any pawn move or capture. If reaches 100 halfmoves → draw claimable.
- **Threefold repetition**: Hash each board position (Zobrist hashing). Store in Map<Hash, Count>. If same position occurs 3 times → draw claimable.
- Both are *claimable* draws (player can choose to claim).

**Q12: What's the difference between pseudo-legal and legal moves?**
**A:**
- **Pseudo-legal**: Moves that follow piece movement rules but may leave king in check
- **Legal**: Pseudo-legal moves that don't leave own king in check
- Performance trade-off: Generate pseudo-legal first (fast), then filter to legal (slower but necessary)

### 10. Extensions & Variations

**Chess960 (Fischer Random Chess):**
- Randomize back rank piece positions
- Castling rules adapt to piece positions
- 960 possible starting positions

**Variants:**
- **Three-check**: First to give 3 checks wins
- **Atomic**: Captures cause explosions destroying nearby pieces
- **Horde**: One side has many pawns vs normal pieces
- **Crazyhouse**: Captured pieces can be dropped back on board

**Performance Enhancements:**
- **Magic Bitboards**: Fast sliding piece move generation
- **Parallel search**: Multi-threaded position evaluation
- **Opening book**: Pre-computed best moves for opening positions
- **Endgame tablebases**: Perfect play with few pieces

### 11. Testing Strategy

**Unit Tests:**
- Individual piece movement (each piece type)
- Special moves (castling, en passant, promotion)
- Check detection from various positions
- Checkmate and stalemate scenarios

**Integration Tests:**
- Complete game playthrough
- Undo/redo throughout game
- All special moves in combination
- Edge cases (perpetual check, etc.)

**Performance Tests:**
- Move generation under time constraints
- Large move history (100+ moves)
- Position evaluation speed

**Validation Tests:**
- Validate against known chess positions (PGN files)
- Test against chess puzzles (mate in N moves)
- Regression tests for bug fixes

### 12. Production Considerations

**Logging:**
- Log all moves with timestamps (PGN format)
- Log illegal move attempts
- Performance metrics (move generation time)

**Monitoring:**
- Track average moves per game
- Monitor for illegal states
- Alert on validation errors

**Error Handling:**
- Graceful handling of illegal moves
- Timeout handling for long computations
- Recovery from corrupted game state

**Security:**
- Validate all inputs (positions in range)
- Rate limit move requests
- Prevent manipulation of game state

---

**Related Problems:**
- Problem 77: TicTacToe Engine
- Problem 78: Snake Game Engine
- Problem 82: Text Editor Undo/Redo
- Problem 85: Game Session State Manager

**Difficulty:** Hard

**Tags:** #Strategy #Factory #Command #GameLogic #MoveGeneration #StateManagement
