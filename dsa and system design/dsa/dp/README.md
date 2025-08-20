# Dynamic Programming Collection 🚀

A comprehensive collection of **88 Dynamic Programming problems** organized by patterns and difficulty levels. This repository represents one of the most complete DP learning resources available.

## 📊 Collection Statistics

| Difficulty | Problems | Completion |
|------------|----------|------------|
| **Easy** | 20 | ✅ 100% |
| **Medium** | 37 | ✅ 100% |
| **Hard** | 31 | ✅ 100% |
| **Total** | **88** | ✅ **100%** |

## 🎯 Pattern-Based Organization

### 📈 Linear DP (23 problems)
Problems that follow sequential patterns with optimal substructure.

#### Basic Linear DP
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | [LC 509](https://leetcode.com/problems/fibonacci-number/) | Easy | Sequence | Matrix Exponentiation, Golden Ratio | [📝](./linear/basic/FibonacciNumber.java) |
| [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | [LC 1137](https://leetcode.com/problems/n-th-tribonacci-number/) | Easy | Sequence | Extended Fibonacci | [📝](./linear/basic/NthTribonacciNumber.java) |
| [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | [LC 70](https://leetcode.com/problems/climbing-stairs/) | Easy | Sequence | Step Counting | [📝](./linear/basic/ClimbingStairs.java) |

#### Optimization Linear DP
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [House Robber](https://leetcode.com/problems/house-robber/) | [LC 198](https://leetcode.com/problems/house-robber/) | Easy | Optimization | Non-adjacent Selection | [📝](./linear/optimization/HouseRobber.java) |
| [House Robber II](https://leetcode.com/problems/house-robber-ii/) | [LC 213](https://leetcode.com/problems/house-robber-ii/) | Medium | Optimization | Circular Array | [📝](./linear/optimization/HouseRobberII.java) |
| [Delete and Earn](https://leetcode.com/problems/delete-and-earn/) | [LC 740](https://leetcode.com/problems/delete-and-earn/) | Medium | Optimization | Transform to House Robber | [📝](./linear/optimization/DeleteAndEarn.java) |
| [The Masseuse LCCI](https://leetcode.com/problems/the-masseuse-lcci/) | [LC 17.01](https://leetcode.com/problems/the-masseuse-lcci/) | Easy | Optimization | Appointment Scheduling | [📝](./linear/optimization/MasseuseLCCI.java) |
| [Paint Fence](https://leetcode.com/problems/paint-fence/) | [LC 276](https://leetcode.com/problems/paint-fence/) | Easy | Optimization | Constraint Coloring | [📝](./linear/optimization/PaintFence.java) |
| [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | [LC 746](https://leetcode.com/problems/min-cost-climbing-stairs/) | Easy | Optimization | Cost Minimization | [📝](./linear/optimization/MinCostClimbingStairs.java) |

#### Sequence Linear DP
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) | [LC 300](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium | Sequence | Binary Search, Patience Sorting | [📝](./linear/sequence/LongestIncreasingSubsequence.java) |
| [Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) | [LC 53](https://leetcode.com/problems/maximum-subarray/) | Easy | Sequence | Kadane's Algorithm | [📝](./linear/sequence/MaximumSubarray.java) |
| [Arithmetic Slices](https://leetcode.com/problems/arithmetic-slices/) | [LC 413](https://leetcode.com/problems/arithmetic-slices/) | Medium | Sequence | Arithmetic Progression | [📝](./linear/sequence/ArithmeticSlices.java) |

### 🌐 Grid DP (15 problems)
2D movement and path optimization problems.

#### Path Counting
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Unique Paths](https://leetcode.com/problems/unique-paths/) | [LC 62](https://leetcode.com/problems/unique-paths/) | Medium | Grid | Combinatorics, Pascal's Triangle | [📝](./grid/path_counting/UniquePaths.java) |
| [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | [LC 63](https://leetcode.com/problems/unique-paths-ii/) | Medium | Grid | Obstacle Handling | [📝](./grid/path_counting/UniquePathsWithObstacles.java) |
| [Pascal's Triangle](https://leetcode.com/problems/pascals-triangle/) | [LC 118](https://leetcode.com/problems/pascals-triangle/) | Easy | Grid | Triangle Generation | [📝](./grid/path_counting/PascalsTriangle.java) |
| [Pascal's Triangle II](https://leetcode.com/problems/pascals-triangle-ii/) | [LC 119](https://leetcode.com/problems/pascals-triangle-ii/) | Easy | Grid | Space Optimization | [📝](./grid/path_counting/PascalsTriangleII.java) |

#### Grid Optimization
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | [LC 64](https://leetcode.com/problems/minimum-path-sum/) | Medium | Grid | Path Sum Minimization | [📝](./grid/optimization/MinimumPathSum.java) |
| [Triangle](https://leetcode.com/problems/triangle/) | [LC 120](https://leetcode.com/problems/triangle/) | Medium | Grid | Top-down Optimization | [📝](./grid/optimization/Triangle.java) |
| [Cherry Pickup](https://leetcode.com/problems/cherry-pickup/) | [LC 741](https://leetcode.com/problems/cherry-pickup/) | Hard | Grid | Dual Path Traversal | [📝](./grid/optimization/CherryPickupI.java) |
| [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) | [LC 1463](https://leetcode.com/problems/cherry-pickup-ii/) | Hard | Grid | Multi-robot Collection | [📝](./grid/optimization/CherryPickupII.java) |

### 🔤 String DP (18 problems)
Advanced string manipulation and pattern matching.

#### Subsequence Problems
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | [LC 1143](https://leetcode.com/problems/longest-common-subsequence/) | Medium | String | Classic LCS | [📝](./string/subsequence/LongestCommonSubsequence.java) |
| [Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | [LC 115](https://leetcode.com/problems/distinct-subsequences/) | Hard | String | Subsequence Counting | [📝](./string/subsequence/DistinctSubsequences.java) |
| [Is Subsequence](https://leetcode.com/problems/is-subsequence/) | [LC 392](https://leetcode.com/problems/is-subsequence/) | Easy | String | Two Pointers | [📝](./string/subsequence/IsSubsequence.java) |
| [Count Different Palindromic Subsequences](https://leetcode.com/problems/count-different-palindromic-subsequences/) | [LC 730](https://leetcode.com/problems/count-different-palindromic-subsequences/) | Hard | String | Palindromic Counting | [📝](./string/subsequence/CountDifferentPalindromicSubsequences.java) |

#### String Matching
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Edit Distance](https://leetcode.com/problems/edit-distance/) | [LC 72](https://leetcode.com/problems/edit-distance/) | Hard | String | Levenshtein Distance | [📝](./string/matching/EditDistance.java) |
| [Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | [LC 10](https://leetcode.com/problems/regular-expression-matching/) | Hard | String | NFA, Pattern Matching | [📝](./string/matching/RegularExpressionMatching.java) |
| [Wildcard Matching](https://leetcode.com/problems/wildcard-matching/) | [LC 44](https://leetcode.com/problems/wildcard-matching/) | Hard | String | Wildcard Patterns | [📝](./string/matching/WildcardMatching.java) |
| [Interleaving String](https://leetcode.com/problems/interleaving-string/) | [LC 97](https://leetcode.com/problems/interleaving-string/) | Hard | String | String Interleaving | [📝](./string/matching/InterleaveString.java) |
| [Scramble String](https://leetcode.com/problems/scramble-string/) | [LC 87](https://leetcode.com/problems/scramble-string/) | Hard | String | Tree Structure | [📝](./string/matching/ScrambleString.java) |

#### Palindrome Optimization
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) | [LC 1312](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) | Hard | String | Palindrome Creation | [📝](./string/palindrome/MinimumInsertionStepsToMakeStringPalindrome.java) |

### 🎒 Knapsack DP (12 problems)
Resource allocation and subset selection problems.

#### Subset Sum Variants
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | [LC 416](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium | Knapsack | 0/1 Knapsack, BitSet | [📝](./knapsack/subset_sum/PartitionEqualSubsetSum.java) |
| [Target Sum](https://leetcode.com/problems/target-sum/) | [LC 494](https://leetcode.com/problems/target-sum/) | Medium | Knapsack | Transform to Subset Sum | [📝](./knapsack/subset_sum/TargetSum.java) |

#### Unbounded Knapsack
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Coin Change](https://leetcode.com/problems/coin-change/) | [LC 322](https://leetcode.com/problems/coin-change/) | Medium | Knapsack | Minimum Coins | [📝](./knapsack/unbounded/CoinChange.java) |
| [Coin Change 2](https://leetcode.com/problems/coin-change-2/) | [LC 518](https://leetcode.com/problems/coin-change-2/) | Medium | Knapsack | Combination Counting | [📝](./knapsack/unbounded/CoinChangeII.java) |
| [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) | [LC 377](https://leetcode.com/problems/combination-sum-iv/) | Medium | Knapsack | Permutation Counting | [📝](./knapsack/unbounded/CombinationSumIV.java) |

### 🎮 Game Theory DP (5 problems)
Strategic decision making and minimax problems.

| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Divisor Game](https://leetcode.com/problems/divisor-game/) | [LC 1025](https://leetcode.com/problems/divisor-game/) | Easy | Game Theory | Mathematical Insight | [📝](./game_theory/DivisorGame.java) |
| [Stone Game III](https://leetcode.com/problems/stone-game-iii/) | [LC 1406](https://leetcode.com/problems/stone-game-iii/) | Hard | Game Theory | Minimax Strategy | [📝](./game_theory/StoneGameIII.java) |

### 📈 Stock Trading DP (4 problems)
Financial optimization problems.

| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | [LC 121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy | Stock | Single Transaction | [📝](./stock_trading/BestTimeToBuyAndSellStock.java) |
| [Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/) | [LC 188](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/) | Hard | Stock | K Transactions | [📝](./stock_trading/BestTimeToBuyAndSellStockIV.java) |

### ⚡ Specialized Patterns (11 problems)

#### Interval DP
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Burst Balloons](https://leetcode.com/problems/burst-balloons/) | [LC 312](https://leetcode.com/problems/burst-balloons/) | Hard | Interval | Matrix Chain Style | [📝](./interval/BurstBalloons.java) |
| [Strange Printer](https://leetcode.com/problems/strange-printer/) | [LC 664](https://leetcode.com/problems/strange-printer/) | Hard | Interval | Printing Optimization | [📝](./interval/StrangePrinter.java) |

#### State Machine DP
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Decode Ways](https://leetcode.com/problems/decode-ways/) | [LC 91](https://leetcode.com/problems/decode-ways/) | Medium | State Machine | String Decoding | [📝](./state_machine/DecodeWays.java) |
| [Decode Ways II](https://leetcode.com/problems/decode-ways-ii/) | [LC 639](https://leetcode.com/problems/decode-ways-ii/) | Hard | State Machine | Wildcard Decoding | [📝](./state_machine/DecodeWaysII.java) |

#### Mathematical DP
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Counting Bits](https://leetcode.com/problems/counting-bits/) | [LC 338](https://leetcode.com/problems/counting-bits/) | Easy | Bit Manipulation | Brian Kernighan | [📝](./mathematical/CountingBits.java) |
| [Get Maximum in Generated Array](https://leetcode.com/problems/get-maximum-in-generated-array/) | [LC 1646](https://leetcode.com/problems/get-maximum-in-generated-array/) | Easy | Simulation | Array Generation | [📝](./mathematical/GetMaximumInGeneratedArray.java) |
| [Minimum Cost For Tickets](https://leetcode.com/problems/minimum-cost-for-tickets/) | [LC 983](https://leetcode.com/problems/minimum-cost-for-tickets/) | Medium | Optimization | Multi-period Cost | [📝](./mathematical/MinimumCostForTickets.java) |

#### Advanced Patterns
| Problem | LeetCode | Difficulty | Pattern | Key Concept | Code |
|---------|----------|------------|---------|-------------|------|
| [Super Egg Drop](https://leetcode.com/problems/super-egg-drop/) | [LC 887](https://leetcode.com/problems/super-egg-drop/) | Hard | Binary Search + DP | Egg Dropping | [📝](./hard/SuperEggDrop.java) |
| [Minimum Difficulty of a Job Schedule](https://leetcode.com/problems/minimum-difficulty-of-a-job-schedule/) | [LC 1335](https://leetcode.com/problems/minimum-difficulty-of-a-job-schedule/) | Hard | Interval | Job Scheduling | [📝](./interval/MinimumDifficultyOfAJobSchedule.java) |
| [Profitable Schemes](https://leetcode.com/problems/profitable-schemes/) | [LC 879](https://leetcode.com/problems/profitable-schemes/) | Hard | 3D DP | Knapsack Variant | [📝](./hard/ProfitableSchemes.java) |
| [Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle/) | [LC 85](https://leetcode.com/problems/maximal-rectangle/) | Hard | Stack + DP | Histogram | [📝](./hard/MaximalRectangle.java) |
| [Video Stitching](https://leetcode.com/problems/video-stitching/) | [LC 1024](https://leetcode.com/problems/video-stitching/) | Medium | Greedy + DP | Interval Coverage | [📝](./hard/VideoStitching.java) |
| [Paint House II](https://leetcode.com/problems/paint-house-ii/) | [LC 265](https://leetcode.com/problems/paint-house-ii/) | Hard | Optimization | K-color Painting | [📝](./hard/PaintHouseII.java) |
| [Number of Ways to Stay in the Same Place After Some Steps](https://leetcode.com/problems/number-of-ways-to-stay-in-the-same-place-after-some-steps/) | [LC 1269](https://leetcode.com/problems/number-of-ways-to-stay-in-the-same-place-after-some-steps/) | Hard | State Machine | Position Tracking | [📝](./hard/NumberOfWaysToStayInTheSamePlaceAfterSomeSteps.java) |
| [Maximum Points You Can Obtain from Cards](https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/) | [LC 1423](https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/) | Medium | Sliding Window | Card Selection | [📝](./hard/MaximumPointsYouCanObtainFromCards.java) |
| [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/) | [LC 303](https://leetcode.com/problems/range-sum-query-immutable/) | Easy | Prefix Sum | Range Queries | [📝](./hard/RangeSumQueryImmutable.java) |
| [Path Sum](https://leetcode.com/problems/path-sum/) | [LC 112](https://leetcode.com/problems/path-sum/) | Easy | Tree DP | Binary Tree Path | [📝](./hard/PathSum.java) |
| [Word Break](https://leetcode.com/problems/word-break/) | [LC 139](https://leetcode.com/problems/word-break/) | Medium | String DP | Dictionary Matching | [📝](./hard/WordBreak.java) |

## 🛠️ Build System

### Quick Start
```bash
# Organize by patterns (first time only)
./organize_dp_patterns.sh

# Build all problems
./build_patterns.sh

# Test sample problems
./test_patterns.sh

# Run specific pattern tests
java -cp build/classes dp.linear.basic.FibonacciNumber
```

### Available Scripts
- `organize_dp_patterns.sh` - Reorganize files by DP patterns
- `build_patterns.sh` - Compile all DP problems
- `test_patterns.sh` - Run pattern-based tests
- `validate_syntax.sh` - Quick syntax validation

## 🎓 Learning Path

### 🟢 Beginner (Easy Problems - 20 problems)
**Focus**: Understand basic DP concepts and state transitions

**Recommended Order**:
1. **[LC 509 - Fibonacci Number](https://leetcode.com/problems/fibonacci-number/)** ([📝 Code](./linear/basic/FibonacciNumber.java)) - Introduction to memoization
2. **[LC 70 - Climbing Stairs](https://leetcode.com/problems/climbing-stairs/)** ([📝 Code](./linear/basic/ClimbingStairs.java)) - Basic state transition
3. **[LC 198 - House Robber](https://leetcode.com/problems/house-robber/)** ([📝 Code](./linear/optimization/HouseRobber.java)) - Constraint handling
4. **[LC 53 - Maximum Subarray](https://leetcode.com/problems/maximum-subarray/)** ([📝 Code](./linear/sequence/MaximumSubarray.java)) - Kadane's algorithm
5. **[LC 121 - Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)** ([📝 Code](./stock_trading/BestTimeToBuyAndSellStock.java)) - Single pass optimization

### 🟡 Intermediate (Medium Problems - 37 problems)
**Focus**: Master common DP patterns and optimizations

**Core Problems**:
1. **[LC 62 - Unique Paths](https://leetcode.com/problems/unique-paths/)** ([📝 Code](./grid/path_counting/UniquePaths.java)) - 2D DP introduction
2. **[LC 322 - Coin Change](https://leetcode.com/problems/coin-change/)** ([📝 Code](./knapsack/unbounded/CoinChange.java)) - Unbounded knapsack
3. **[LC 300 - Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/)** ([📝 Code](./linear/sequence/LongestIncreasingSubsequence.java)) - Binary search optimization
4. **[LC 1143 - Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/)** ([📝 Code](./string/subsequence/LongestCommonSubsequence.java)) - String DP foundation
5. **[LC 416 - Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/)** ([📝 Code](./knapsack/subset_sum/PartitionEqualSubsetSum.java)) - 0/1 knapsack

### 🔴 Advanced (Hard Problems - 31 problems)
**Focus**: Complex state management and advanced techniques

**Challenge Problems**:
1. **[LC 72 - Edit Distance](https://leetcode.com/problems/edit-distance/)** ([📝 Code](./string/matching/EditDistance.java)) - String transformation
2. **[LC 312 - Burst Balloons](https://leetcode.com/problems/burst-balloons/)** ([📝 Code](./interval/BurstBalloons.java)) - Interval DP mastery
3. **[LC 10 - Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/)** ([📝 Code](./string/matching/RegularExpressionMatching.java)) - Pattern matching
4. **[LC 887 - Super Egg Drop](https://leetcode.com/problems/super-egg-drop/)** ([📝 Code](./hard/SuperEggDrop.java)) - Binary search + DP
5. **[LC 1463 - Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/)** ([📝 Code](./grid/optimization/CherryPickupII.java)) - Multi-dimensional states

## 🚀 Quick Access Links

### 📁 Browse by Pattern
- [📂 Linear DP](./linear/) - Sequential optimization problems
- [📂 Grid DP](./grid/) - 2D movement and path problems  
- [📂 String DP](./string/) - Text processing and matching
- [📂 Knapsack DP](./knapsack/) - Resource allocation problems
- [📂 Game Theory](./game_theory/) - Strategic decision making
- [📂 Stock Trading](./stock_trading/) - Financial optimization
- [📂 Interval DP](./interval/) - Range-based optimization
- [📂 State Machine](./state_machine/) - Finite state problems
- [📂 Mathematical](./mathematical/) - Number theory and combinatorics

### 🔧 Development Tools
- [⚙️ Build Script](../build_patterns.sh) - Compile all problems
- [🧪 Test Runner](../test_patterns.sh) - Execute sample tests
- [📋 Validation](../validate_syntax.sh) - Syntax checking
- [📖 Pattern Guide](./PATTERN_GUIDE.md) - Detailed pattern explanations

---

**Happy Dynamic Programming!** 🎉

*Master these 88 carefully curated problems to become a DP expert. Each problem includes multiple solution approaches, from brute force to optimal, with detailed complexity analysis.*

**🔗 Quick Links:**
- [LeetCode DP Problems](https://leetcode.com/problemset/all/?topicSlugs=dynamic-programming)
- [DP Pattern Guide](./PATTERN_GUIDE.md)
- [Build Instructions](#-build-system)
### Production Quality
- **Clean Code** - Well-documented, readable implementations
- **Optimal Complexity** - Best possible time/space complexity
- **Real-world Applications** - Practical problem-solving techniques

## 📖 Key Concepts Covered

### Core DP Principles
- **Optimal Substructure** - Problem breakdown strategies
- **Overlapping Subproblems** - Memoization techniques
- **State Definition** - Choosing optimal state representation
- **Transition Relations** - Building recurrence relations

### Advanced Techniques
- **Space Optimization** - Rolling arrays and state compression
- **Matrix Exponentiation** - Logarithmic time solutions
- **Bit Manipulation DP** - Bitmask state representation
- **Tree DP** - Recursive structure optimization

### Optimization Strategies
- **Bottom-up vs Top-down** - Choosing the right approach
- **State Compression** - Reducing space complexity
- **Mathematical Insights** - Formula-based solutions
- **Early Termination** - Pruning unnecessary computations

## 🏆 Notable Achievements

- **Complete Coverage** - All major DP patterns included
- **Multiple Approaches** - 400+ different algorithmic implementations
- **Optimal Solutions** - Best possible complexity for each problem
- **Educational Value** - Progressive difficulty with detailed explanations
- **Production Ready** - Clean, maintainable, well-tested code

---

**Happy Dynamic Programming!** 🎉

*Master these 88 carefully curated problems to become a DP expert. Each problem includes multiple solution approaches, from brute force to optimal, with detailed complexity analysis.*

**🔗 Quick Links:**
- [LeetCode DP Problems](https://leetcode.com/problemset/all/?topicSlugs=dynamic-programming)
- [DP Pattern Guide](./PATTERN_GUIDE.md)
- [Build Instructions](#-build-system)
