# Grid/2D Array Problems

Problems involving 2D grids/matrices with path finding, traversal, and manipulation algorithms.

## 📚 Grid Algorithms Guide

### 🎯 When to Use Grid Algorithms
Use grid algorithms when:
- Problem involves **2D matrix/grid** structure
- Need to find **paths**, **patterns**, or **regions** in a grid
- Problem involves **spreading**, **expansion**, or **traversal** in 2D space
- Dealing with **islands**, **mazes**, **games**, or **boards**

### 🔑 Grid Algorithms & Time Complexities

#### 1️⃣ **DFS on Grid** - O(m × n)
**When to use**:
- Find **connected regions** (islands, components)
- Explore all possible paths
- Fill/color regions
- Count areas/perimeters

**Implementation**: 4-directional or 8-directional recursion
```java
int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}}; // 4 directions
void dfs(int[][] grid, int i, int j) {
    if (out of bounds || visited) return;
    // mark visited
    for (int[] dir : dirs) {
        dfs(grid, i + dir[0], j + dir[1]);
    }
}
```
**Use cases**: Number of islands, flood fill, word search
**Space**: O(m × n) for recursion stack in worst case

#### 2️⃣ **BFS on Grid** - O(m × n)
**When to use**:
- Find **shortest path** in grid
- Level-by-level expansion (spreading problems)
- Multi-source BFS (from multiple starting points)

**Implementation**: Queue with coordinates
```java
Queue<int[]> queue = new LinkedList<>();
queue.offer(new int[]{startRow, startCol});
while (!queue.isEmpty()) {
    int[] curr = queue.poll();
    // explore 4 directions
}
```
**Use cases**: Shortest path in maze, rotting oranges, walls and gates
**Space**: O(m × n) for queue

#### 3️⃣ **Multi-Source BFS** - O(m × n)
**When to use**:
- Expand from **multiple sources** simultaneously
- Find distance to nearest source
- Spreading/propagation problems

**Implementation**: Add all sources to queue initially
```java
Queue<int[]> queue = new LinkedList<>();
for (all sources) queue.offer(source);
// BFS from all sources simultaneously
```
**Use cases**: Rotting oranges, 01 matrix, walls and gates
**Space**: O(m × n)

#### 4️⃣ **Dynamic Programming on Grid** - O(m × n)
**When to use**:
- Count **number of paths**
- Find **minimum/maximum path sum**
- Optimization problems on grid

**Implementation**: Bottom-up or top-down DP
```java
int[][] dp = new int[m][n];
dp[0][0] = grid[0][0];
for (int i = 0; i < m; i++) {
    for (int j = 0; j < n; j++) {
        dp[i][j] = /* combine from dp[i-1][j] and dp[i][j-1] */;
    }
}
```
**Use cases**: Unique paths, minimum path sum, dungeon game
**Space**: O(m × n), can optimize to O(n)

#### 5️⃣ **Backtracking on Grid** - O(4^(m×n)) worst case
**When to use**:
- Find **all possible paths/solutions**
- Constraint satisfaction problems
- Puzzle solving (Sudoku, N-Queens)

**Implementation**: DFS with state restoration
```java
boolean backtrack(int[][] grid, int i, int j) {
    if (goal) return true;
    // try all options
    for (option : options) {
        // make choice
        if (backtrack(grid, newI, newJ)) return true;
        // undo choice (backtrack)
    }
    return false;
}
```
**Use cases**: Word search, N-Queens, Sudoku solver
**Space**: O(m × n) for recursion

#### 6️⃣ **Dijkstra on Grid** - O(m × n × log(m × n))
**When to use**:
- Shortest path with **different costs** for cells
- Weighted grid problems

**Implementation**: Priority Queue with (cost, row, col)
```java
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
pq.offer(new int[]{0, startRow, startCol}); // {cost, row, col}
```
**Use cases**: Path with minimum effort, swim in rising water
**Space**: O(m × n)

#### 7️⃣ **Binary Search on Grid** - O(log(max-min) × m × n)
**When to use**:
- **Minimize/maximize** some value on grid
- "Can we achieve X?" type questions

**Implementation**: Binary search + BFS/DFS validation
```java
int left = min, right = max;
while (left < right) {
    int mid = left + (right - left) / 2;
    if (canAchieve(grid, mid)) right = mid;
    else left = mid + 1;
}
```
**Use cases**: Path with minimum effort, swim in rising water
**Space**: O(m × n)

#### 8️⃣ **Union-Find on Grid** - O(m × n × α(m×n))
**When to use**:
- Find **connected components** dynamically
- Check connectivity
- Merge regions

**Implementation**: Convert 2D to 1D index
```java
int getIndex(int i, int j, int cols) {
    return i * cols + j;
}
// Use Union-Find operations
```
**Use cases**: Number of islands II (dynamic), surrounded regions
**Space**: O(m × n)

#### 9️⃣ **Spiral/Diagonal Traversal** - O(m × n)
**When to use**:
- Traverse grid in **specific pattern**
- Matrix manipulation

**Implementation**: Direction vectors with boundaries
```java
int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}}; // right, down, left, up
int dir = 0;
while (elements remain) {
    // move in current direction
    // change direction when hitting boundary
}
```
**Use cases**: Spiral matrix, diagonal traverse
**Space**: O(1) for traversal, O(m × n) for result

#### 🔟 **Prefix Sum on Grid** - O(m × n) preprocessing, O(1) query
**When to use**:
- Answer **range sum queries** efficiently
- Matrix region sums

**Implementation**: 2D prefix sum array
```java
int[][] prefix = new int[m+1][n+1];
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        prefix[i][j] = grid[i-1][j-1] 
            + prefix[i-1][j] + prefix[i][j-1] 
            - prefix[i-1][j-1];
    }
}
// Range sum: prefix[r2][c2] - prefix[r1-1][c2] 
//            - prefix[r2][c1-1] + prefix[r1-1][c1-1]
```
**Use cases**: Matrix block sum, range sum query 2D
**Space**: O(m × n)

### 🎨 Common Grid Patterns

#### **4-Directional Movement**
```java
int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}}; // right, down, left, up
for (int[] dir : dirs) {
    int newRow = row + dir[0];
    int newCol = col + dir[1];
}
```

#### **8-Directional Movement**
```java
int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}, 
                {1,1}, {1,-1}, {-1,1}, {-1,-1}};
```

#### **Knight Moves (Chess)**
```java
int[][] dirs = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2},
                {1,-2}, {1,2}, {2,-1}, {2,1}};
```

#### **Boundary Check**
```java
boolean isValid(int i, int j, int m, int n) {
    return i >= 0 && i < m && j >= 0 && j < n;
}
```

### 🚀 Grid Problem-Solving Strategies

1. **Count Islands/Components**: DFS or BFS or Union-Find
2. **Shortest Path (Unweighted)**: BFS
3. **Shortest Path (Weighted)**: Dijkstra or Binary Search
4. **All Paths**: DFS with backtracking
5. **Fill Region**: DFS (flood fill)
6. **Multi-Source Expansion**: Multi-source BFS
7. **Path Counting**: DP (bottom-up)
8. **Path Optimization**: DP or Binary Search + BFS
9. **Pattern Search**: DFS with backtracking
10. **Matrix Manipulation**: Simulation with direction vectors

### ⚡ Common Optimizations

- **In-place marking**: Modify grid to mark visited (save space)
- **Bidirectional BFS**: Meet in middle for shortest path
- **Early termination**: Stop when target found
- **Space-optimized DP**: Use 1D array instead of 2D for DP
- **Visited set vs array**: Use set for sparse grids, array for dense
- **Direction arrays**: Cleaner code, easier to extend

## Problem List (Grouped by Difficulty)

### Medium
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Battleships In Board | [LeetCode Problem](https://leetcode.com/problems/battleships-in-board/) | [BattleshipsInBoard.java](./medium/BattleshipsInBoard.java) |
| Candy Crush | [LeetCode Problem](https://leetcode.com/problems/candy-crush/) | [CandyCrush.java](./medium/CandyCrush.java) |
| Chess Knight Probability | [LeetCode Problem](https://leetcode.com/problems/chess-knight-probability/) | [ChessKnightProbability.java](./medium/ChessKnightProbability.java) |
| Design Tic Tac Toe | [LeetCode Problem](https://leetcode.com/problems/design-tic-tac-toe/) | [DesignTicTacToe.java](./medium/DesignTicTacToe.java) |
| Diagonal Traverse | [LeetCode Problem](https://leetcode.com/problems/diagonal-traverse/) | [DiagonalTraverse.java](./medium/DiagonalTraverse.java) |
| Flood Fill | [LeetCode Problem](https://leetcode.com/problems/flood-fill/) | [FloodFill.java](./medium/FloodFill.java) |
| Game Of Life | [LeetCode Problem](https://leetcode.com/problems/game-of-life/) | [GameOfLife.java](./medium/GameOfLife.java) |
| Island Perimeter | [LeetCode Problem](https://leetcode.com/problems/island-perimeter/) | [IslandPerimeter.java](./medium/IslandPerimeter.java) |
| Kth Smallest In Sorted Matrix | [LeetCode Problem](https://leetcode.com/problems/kth-smallest-in-sorted-matrix/) | [KthSmallestInSortedMatrix.java](./medium/KthSmallestInSortedMatrix.java) |
| Matrix Block Sum | [LeetCode Problem](https://leetcode.com/problems/matrix-block-sum/) | [MatrixBlockSum.java](./medium/MatrixBlockSum.java) |
| Max Area Of Island | [LeetCode Problem](https://leetcode.com/problems/max-area-of-island/) | [MaxAreaOfIsland.java](./medium/MaxAreaOfIsland.java) |
| Maximal Square | [LeetCode Problem](https://leetcode.com/problems/maximal-square/) | [MaximalSquare.java](./medium/MaximalSquare.java) |
| Minesweeper Game | [LeetCode Problem](https://leetcode.com/problems/minesweeper-game/) | [MinesweeperGame.java](./medium/MinesweeperGame.java) |
| Minimum Path Sum | [LeetCode Problem](https://leetcode.com/problems/minimum-path-sum/) | [MinimumPathSum.java](./medium/MinimumPathSum.java) |
| Number Of Distinct Islands | [LeetCode Problem](https://leetcode.com/problems/number-of-distinct-islands/) | [NumberOfDistinctIslands.java](./medium/NumberOfDistinctIslands.java) |
| Pacific Atlantic Water Flow | [LeetCode Problem](https://leetcode.com/problems/pacific-atlantic-water-flow/) | [PacificAtlanticWaterFlow.java](./medium/PacificAtlanticWaterFlow.java) |
| Rotate Image | [LeetCode Problem](https://leetcode.com/problems/rotate-image/) | [RotateImage.java](./medium/RotateImage.java) |
| Rotten Oranges | [LeetCode Problem](https://leetcode.com/problems/rotten-oranges/) | [RottenOranges.java](./medium/RottenOranges.java) |
| Search A2DMatrix II | [LeetCode Problem](https://leetcode.com/problems/search-a2dmatrix-ii/) | [SearchA2DMatrixII.java](./medium/SearchA2DMatrixII.java) |
| Set Matrix Zeroes | [LeetCode Problem](https://leetcode.com/problems/set-matrix-zeroes/) | [SetMatrixZeroes.java](./medium/SetMatrixZeroes.java) |
| Shortest Bridge | [LeetCode Problem](https://leetcode.com/problems/shortest-bridge/) | [ShortestBridge.java](./medium/ShortestBridge.java) |
| Spiral Matrix | [LeetCode Problem](https://leetcode.com/problems/spiral-matrix/) | [SpiralMatrix.java](./medium/SpiralMatrix.java) |
| Spiral Matrix II | [LeetCode Problem](https://leetcode.com/problems/spiral-matrix-ii/) | [SpiralMatrixII.java](./medium/SpiralMatrixII.java) |
| Unique Paths | [LeetCode Problem](https://leetcode.com/problems/unique-paths/) | [UniquePaths.java](./medium/UniquePaths.java) |
| Unique Paths II | [LeetCode Problem](https://leetcode.com/problems/unique-paths-ii/) | [UniquePathsII.java](./medium/UniquePathsII.java) |
| Valid Sudoku | [LeetCode Problem](https://leetcode.com/problems/valid-sudoku/) | [ValidSudoku.java](./medium/ValidSudoku.java) |
| Word Search | [LeetCode Problem](https://leetcode.com/problems/word-search/) | [WordSearch.java](./medium/WordSearch.java) |
| Zero One Matrix | [LeetCode Problem](https://leetcode.com/problems/zero-one-matrix/) | [ZeroOneMatrix.java](./medium/ZeroOneMatrix.java) |

### Hard
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Alien Dictionary | [LeetCode Problem](https://leetcode.com/problems/alien-dictionary/) | [AlienDictionary.java](./hard/AlienDictionary.java) |
| Cherry Pickup | [LeetCode Problem](https://leetcode.com/problems/cherry-pickup/) | [CherryPickup.java](./hard/CherryPickup.java) |
| Cut Off Trees For Golf | [LeetCode Problem](https://leetcode.com/problems/cut-off-trees-for-golf/) | [CutOffTreesForGolf.java](./hard/CutOffTreesForGolf.java) |
| Escape The Maze | [LeetCode Problem](https://leetcode.com/problems/escape-the-maze/) | [EscapeTheMaze.java](./hard/EscapeTheMaze.java) |
| Jump Game IV | [LeetCode Problem](https://leetcode.com/problems/jump-game-iv/) | [JumpGameIV.java](./hard/JumpGameIV.java) |
| Minimum Moves To Reach Target | [LeetCode Problem](https://leetcode.com/problems/minimum-moves-to-reach-target/) | [MinimumMovesToReachTarget.java](./hard/MinimumMovesToReachTarget.java) |
| NQueens | [LeetCode Problem](https://leetcode.com/problems/nqueens/) | [NQueens.java](./hard/NQueens.java) |
| NQueens II | [LeetCode Problem](https://leetcode.com/problems/nqueens-ii/) | [NQueensII.java](./hard/NQueensII.java) |
| Number Of Paths With Max Score | [LeetCode Problem](https://leetcode.com/problems/number-of-paths-with-max-score/) | [NumberOfPathsWithMaxScore.java](./hard/NumberOfPathsWithMaxScore.java) |
| Race Car | [LeetCode Problem](https://leetcode.com/problems/race-car/) | [RaceCar.java](./hard/RaceCar.java) |
| Robot Room Cleaner | [LeetCode Problem](https://leetcode.com/problems/robot-room-cleaner/) | [RobotRoomCleaner.java](./hard/RobotRoomCleaner.java) |
| Shortest Path In Binary Matrix | [LeetCode Problem](https://leetcode.com/problems/shortest-path-in-binary-matrix/) | [ShortestPathInBinaryMatrix.java](./hard/ShortestPathInBinaryMatrix.java) |
| Shortest Path In Grid | [LeetCode Problem](https://leetcode.com/problems/shortest-path-in-grid/) | [ShortestPathInGrid.java](./hard/ShortestPathInGrid.java) |
| Shortest Path To Get All Keys | [LeetCode Problem](https://leetcode.com/problems/shortest-path-to-get-all-keys/) | [ShortestPathToGetAllKeys.java](./hard/ShortestPathToGetAllKeys.java) |
| Shortest Path Visiting All Nodes | [LeetCode Problem](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) | [ShortestPathVisitingAllNodes.java](./hard/ShortestPathVisitingAllNodes.java) |
| Sliding Puzzle | [LeetCode Problem](https://leetcode.com/problems/sliding-puzzle/) | [SlidingPuzzle.java](./hard/SlidingPuzzle.java) |
| Swim In Water | [LeetCode Problem](https://leetcode.com/problems/swim-in-water/) | [SwimInWater.java](./hard/SwimInWater.java) |
| Trapping Rain Water2D | [LeetCode Problem](https://leetcode.com/problems/trapping-rain-water2d/) | [TrappingRainWater2D.java](./hard/TrappingRainWater2D.java) |
| Word Search II | [LeetCode Problem](https://leetcode.com/problems/word-search-ii/) | [WordSearchII.java](./hard/WordSearchII.java) |
| Zuma Game | [LeetCode Problem](https://leetcode.com/problems/zuma-game/) | [ZumaGame.java](./hard/ZumaGame.java) |

## Core Algorithms & Techniques

### Key Patterns Used
- Pattern identification and implementation details
- Time and space complexity analysis
- Common approaches and optimizations

### Algorithm Categories
- **Time Complexity**: Various complexity patterns from O(1) to O(n²)
- **Space Complexity**: In-place vs auxiliary space solutions
- **Approach Types**: Iterative, recursive, and hybrid solutions

## Implementation Features

### Each Problem Includes:
- ✅ Multiple solution approaches when applicable
- ✅ Comprehensive test cases with edge cases
- ✅ Time and space complexity analysis
- ✅ Detailed comments and explanations
- ✅ Follow-up questions and variations

### Code Quality Standards:
- Clean, readable implementations
- Proper error handling
- Edge case coverage
- Performance optimizations
- Interview-ready format

## Study Recommendations

### Difficulty Progression:
1. **Easy**: Master fundamental concepts and basic implementations
2. **Medium**: Learn advanced techniques and optimization strategies  
3. **Hard**: Practice complex algorithms and edge case handling

### Key Focus Areas:
- Understanding core algorithms and data structures
- Pattern recognition and template usage
- Time/space complexity optimization
- Edge case identification and handling

## Notes
- Each implementation includes detailed explanations
- Focus on understanding patterns rather than memorizing solutions
- Practice multiple approaches for comprehensive understanding
- Test with various input scenarios for robust solutions
