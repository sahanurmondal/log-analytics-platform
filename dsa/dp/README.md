# Dynamic Programming Problems

This directory contains dynamic programming problems from LeetCode, organized by pattern-based categories for educational progression.

## 📚 DP Patterns & Algorithms Guide

### 🎯 When to Use Dynamic Programming
Use DP when:
- Problem has **optimal substructure** (optimal solution uses optimal solutions of subproblems)
- Problem has **overlapping subproblems** (same subproblems computed multiple times)
- Need to find **optimal value** (min/max) or **count ways**
- Problem involves **choices** at each step

### 🔑 DP Patterns & Time Complexities

#### 1️⃣ **Linear DP** - O(n)
**When to use**: Sequential decision-making on 1D array
- **Fibonacci Pattern**: f(n) = f(n-1) + f(n-2)
- **House Robber Pattern**: Can't take adjacent elements
- **Kadane's Algorithm**: Maximum subarray sum
- **Use cases**: Climbing stairs, cost optimization, subsequences
- **Space optimization**: Often reducible to O(1) using variables

#### 2️⃣ **Grid DP** - O(m×n)
**When to use**: 2D matrix with path-based problems
- **Path Counting**: Count unique paths in grid
- **Path Optimization**: Find min/max cost path
- **Use cases**: Robot paths, minimum path sum, triangle
- **Space optimization**: Can reduce to O(n) using rolling array

#### 3️⃣ **Knapsack DP** - O(n×capacity)
**When to use**: Selection problems with constraints
- **0/1 Knapsack**: Each item used once or not at all
- **Unbounded Knapsack**: Infinite supply of items
- **Bounded Knapsack**: Limited quantity per item
- **Use cases**: Subset sum, coin change, partition problems
- **Variants**: Target sum, last stone weight

#### 4️⃣ **String DP** - O(n×m) or O(n²)
**When to use**: Sequence comparison or transformation
- **LCS Pattern**: Longest Common Subsequence - O(n×m)
- **Edit Distance**: Transform one string to another - O(n×m)
- **Palindrome DP**: Check/count palindromic substrings - O(n²)
- **Use cases**: Text diff, spell checker, regex matching

#### 5️⃣ **Interval DP** - O(n³)
**When to use**: Problems on contiguous segments
- **Matrix Chain Multiplication**: Optimal parenthesization
- **Burst Balloons**: Optimal order to burst
- **Palindrome Partitioning**: Minimum cuts needed
- **Use cases**: Breaking problems, merging problems

#### 6️⃣ **Stock Trading DP** - O(n×k)
**When to use**: Buy/sell optimization problems
- **State Machine DP**: Track buy/sell/cooldown states
- **Transaction Limits**: At most k transactions
- **Use cases**: Stock trading with various constraints

#### 7️⃣ **State Machine DP** - O(n×states)
**When to use**: Problems with distinct states and transitions
- **Finite States**: Define possible states at each step
- **Transitions**: Rules for moving between states
- **Use cases**: Paint house, best time to buy/sell stock

#### 8️⃣ **Game Theory DP** - O(n) to O(n³)
**When to use**: Two-player optimal strategy games
- **Minimax**: Maximize own score, minimize opponent's
- **Nim Game**: Take-turns strategy
- **Use cases**: Stone game, predict winner

#### 9️⃣ **Mathematical DP** - Varies
**When to use**: Number theory and combinatorics
- **Counting Problems**: Ways to achieve result
- **Number Factorization**: Breaking numbers optimally
- **Use cases**: Integer break, perfect squares

#### 🔟 **Advanced DP** - O(n²) to O(2ⁿ×n)
**When to use**: Complex optimization problems
- **Bitmask DP**: Track visited states - O(2ⁿ×n)
- **Digit DP**: Count numbers with constraints
- **Tree DP**: Optimization on tree structures
- **Use cases**: TSP, subset problems, tree diameter

### 🎨 DP Implementation Approaches

#### **Top-Down (Memoization)**
```java
// Recursive with cache
Map<String, Integer> memo = new HashMap<>();
int dp(int i) {
    if (memo.containsKey(i)) return memo.get(i);
    int result = /* compute */;
    memo.put(i, result);
    return result;
}
```
**Pros**: Intuitive, only computes needed states
**Cons**: Recursion overhead, stack space

#### **Bottom-Up (Tabulation)**
```java
// Iterative with array
int[] dp = new int[n];
dp[0] = base;
for (int i = 1; i < n; i++) {
    dp[i] = /* compute from dp[i-1], etc. */;
}
```
**Pros**: No recursion, better cache locality
**Cons**: May compute unnecessary states

#### **Space-Optimized**
```java
// Use variables instead of array (when possible)
int prev2 = base1, prev1 = base2;
for (int i = 2; i < n; i++) {
    int curr = prev1 + prev2;
    prev2 = prev1;
    prev1 = curr;
}
```
**Pros**: O(1) space, very efficient
**Cons**: Only works for certain patterns

### 🚀 DP Problem-Solving Steps
1. **Identify**: Does problem have optimal substructure?
2. **Define State**: What variables uniquely identify a subproblem?
3. **Recurrence**: How to compute dp[i] from smaller subproblems?
4. **Base Cases**: What are the simplest subproblems?
5. **Compute Order**: Which direction to fill the DP table?
6. **Optimize Space**: Can we reduce memory usage?

### ⚡ Common DP Optimizations
- **Rolling Array**: For 2D DP, use only 2 rows instead of full matrix
- **State Compression**: Use bitmask instead of arrays for small sets
- **Monotonic Queue**: Optimize range queries (sliding window maximum)
- **Matrix Exponentiation**: For large n with linear recurrence - O(k³ log n)

---

## 🎯 Complete DP Algorithms with Pseudocode

### 1. **Kadane's Algorithm** (Maximum Subarray Sum)
**Time Complexity**: O(n) | **Space Complexity**: O(1)

**Problem**: Find maximum sum of contiguous subarray

**Pseudocode**:
```
function maxSubarraySum(arr, n):
    maxEndingHere = arr[0]
    maxSoFar = arr[0]
    
    for i from 1 to n-1:
        maxEndingHere = max(arr[i], maxEndingHere + arr[i])
        maxSoFar = max(maxSoFar, maxEndingHere)
    
    return maxSoFar
```

**Recurrence**: `dp[i] = max(arr[i], dp[i-1] + arr[i])`

---

### 2. **Longest Increasing Subsequence (LIS)**
**Time Complexity**: O(n²) DP, O(n log n) Binary Search | **Space Complexity**: O(n)

**Problem**: Find length of longest increasing subsequence

**Pseudocode** (DP Approach):
```
function LIS(arr, n):
    dp[n] = {1, 1, 1, ..., 1}  // Initialize all to 1
    
    for i from 1 to n-1:
        for j from 0 to i-1:
            if arr[j] < arr[i]:
                dp[i] = max(dp[i], dp[j] + 1)
    
    return max(dp)
```

**Pseudocode** (Binary Search - Optimal):
```
function LIS_Optimized(arr, n):
    tails = []  // stores smallest tail of all increasing subsequences
    
    for num in arr:
        pos = binarySearch(tails, num)  // Find insertion position
        if pos == len(tails):
            tails.append(num)
        else:
            tails[pos] = num
    
    return len(tails)
```

**Recurrence**: `dp[i] = max(dp[j] + 1)` for all `j < i` where `arr[j] < arr[i]`

---

### 3. **Longest Common Subsequence (LCS)**
**Time Complexity**: O(m × n) | **Space Complexity**: O(m × n), can be optimized to O(min(m,n))

**Problem**: Find length of longest common subsequence between two strings

**Pseudocode**:
```
function LCS(str1, str2, m, n):
    dp[m+1][n+1]  // Initialize to 0
    
    for i from 1 to m:
        for j from 1 to n:
            if str1[i-1] == str2[j-1]:
                dp[i][j] = dp[i-1][j-1] + 1
            else:
                dp[i][j] = max(dp[i-1][j], dp[i][j-1])
    
    return dp[m][n]
```

**Recurrence**:
```
dp[i][j] = dp[i-1][j-1] + 1                    if str1[i] == str2[j]
         = max(dp[i-1][j], dp[i][j-1])         otherwise
```

---

### 4. **Edit Distance (Levenshtein Distance)**
**Time Complexity**: O(m × n) | **Space Complexity**: O(m × n), can be optimized to O(min(m,n))

**Problem**: Minimum operations (insert/delete/replace) to convert str1 to str2

**Pseudocode**:
```
function editDistance(str1, str2, m, n):
    dp[m+1][n+1]
    
    // Base cases
    for i from 0 to m: dp[i][0] = i
    for j from 0 to n: dp[0][j] = j
    
    for i from 1 to m:
        for j from 1 to n:
            if str1[i-1] == str2[j-1]:
                dp[i][j] = dp[i-1][j-1]  // No operation needed
            else:
                dp[i][j] = 1 + min(
                    dp[i-1][j],      // Delete
                    dp[i][j-1],      // Insert
                    dp[i-1][j-1]     // Replace
                )
    
    return dp[m][n]
```

**Recurrence**:
```
dp[i][j] = dp[i-1][j-1]                                if str1[i] == str2[j]
         = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])   otherwise
```

---

### 5. **0/1 Knapsack Problem**
**Time Complexity**: O(n × W) | **Space Complexity**: O(n × W), can be optimized to O(W)

**Problem**: Maximize value with weight constraint (each item used once)

**Pseudocode**:
```
function knapsack(weights, values, n, W):
    dp[n+1][W+1]  // Initialize to 0
    
    for i from 1 to n:
        for w from 0 to W:
            if weights[i-1] <= w:
                dp[i][w] = max(
                    values[i-1] + dp[i-1][w - weights[i-1]],  // Include item
                    dp[i-1][w]                                 // Exclude item
                )
            else:
                dp[i][w] = dp[i-1][w]
    
    return dp[n][W]
```

**Recurrence**:
```
dp[i][w] = max(values[i] + dp[i-1][w-weights[i]], dp[i-1][w])  if weights[i] <= w
         = dp[i-1][w]                                            otherwise
```

**Space-Optimized (1D array)**:
```
function knapsack_optimized(weights, values, n, W):
    dp[W+1] = {0, 0, ..., 0}
    
    for i from 0 to n-1:
        for w from W down to weights[i]:  // Reverse iteration!
            dp[w] = max(dp[w], values[i] + dp[w - weights[i]])
    
    return dp[W]
```

---

### 6. **Unbounded Knapsack**
**Time Complexity**: O(n × W) | **Space Complexity**: O(W)

**Problem**: Same as 0/1 but each item can be used unlimited times

**Pseudocode**:
```
function unboundedKnapsack(weights, values, n, W):
    dp[W+1] = {0, 0, ..., 0}
    
    for w from 1 to W:
        for i from 0 to n-1:
            if weights[i] <= w:
                dp[w] = max(dp[w], values[i] + dp[w - weights[i]])
    
    return dp[W]
```

**Note**: Forward iteration (unlike 0/1 knapsack) allows reusing items

---

### 7. **Coin Change (Minimum Coins)**
**Time Complexity**: O(n × amount) | **Space Complexity**: O(amount)

**Problem**: Minimum coins needed to make amount

**Pseudocode**:
```
function coinChange(coins, amount):
    dp[amount+1] = {0, ∞, ∞, ..., ∞}
    
    for a from 1 to amount:
        for coin in coins:
            if coin <= a:
                dp[a] = min(dp[a], 1 + dp[a - coin])
    
    return dp[amount] if dp[amount] != ∞ else -1
```

**Recurrence**: `dp[amount] = min(1 + dp[amount - coin])` for all valid coins

---

### 8. **Coin Change II (Count Ways)**
**Time Complexity**: O(n × amount) | **Space Complexity**: O(amount)

**Problem**: Count number of ways to make amount

**Pseudocode**:
```
function coinChangeWays(coins, amount):
    dp[amount+1] = {1, 0, 0, ..., 0}  // dp[0] = 1
    
    for coin in coins:
        for a from coin to amount:
            dp[a] += dp[a - coin]
    
    return dp[amount]
```

**Key Difference**: Iterate coins in outer loop to avoid counting permutations

---

### 9. **Matrix Chain Multiplication**
**Time Complexity**: O(n³) | **Space Complexity**: O(n²)

**Problem**: Find minimum scalar multiplications needed

**Pseudocode**:
```
function matrixChainOrder(dims, n):
    dp[n][n]  // Initialize to 0
    
    // L is chain length
    for L from 2 to n:
        for i from 1 to n-L+1:
            j = i + L - 1
            dp[i][j] = ∞
            
            for k from i to j-1:
                cost = dp[i][k] + dp[k+1][j] + dims[i-1] * dims[k] * dims[j]
                dp[i][j] = min(dp[i][j], cost)
    
    return dp[1][n-1]
```

**Recurrence**: `dp[i][j] = min(dp[i][k] + dp[k+1][j] + cost)` for all `i ≤ k < j`

---

### 10. **Longest Palindromic Substring**
**Time Complexity**: O(n²) | **Space Complexity**: O(n²), can be O(1) with expand-around-center

**Problem**: Find longest palindromic substring

**Pseudocode** (DP Approach):
```
function longestPalindrome(s, n):
    dp[n][n] = false  // Initialize
    maxLen = 1
    start = 0
    
    // All single characters are palindromes
    for i from 0 to n-1:
        dp[i][i] = true
    
    // Check for length 2
    for i from 0 to n-2:
        if s[i] == s[i+1]:
            dp[i][i+1] = true
            start = i
            maxLen = 2
    
    // Check for lengths 3 and above
    for length from 3 to n:
        for i from 0 to n-length:
            j = i + length - 1
            
            if s[i] == s[j] and dp[i+1][j-1]:
                dp[i][j] = true
                start = i
                maxLen = length
    
    return s.substring(start, start + maxLen)
```

**Recurrence**: `dp[i][j] = (s[i] == s[j]) AND dp[i+1][j-1]`

---

### 11. **Partition Equal Subset Sum**
**Time Complexity**: O(n × sum) | **Space Complexity**: O(sum)

**Problem**: Can array be partitioned into two equal sum subsets?

**Pseudocode**:
```
function canPartition(nums):
    totalSum = sum(nums)
    if totalSum % 2 != 0: return false
    
    target = totalSum / 2
    dp[target+1] = {true, false, false, ..., false}
    
    for num in nums:
        for s from target down to num:
            dp[s] = dp[s] OR dp[s - num]
    
    return dp[target]
```

**Recurrence**: `dp[s] = dp[s] OR dp[s-num]` (subset sum variation)

---

### 12. **Word Break**
**Time Complexity**: O(n² × m) where m is avg word length | **Space Complexity**: O(n)

**Problem**: Can string be segmented into dictionary words?

**Pseudocode**:
```
function wordBreak(s, wordDict):
    n = len(s)
    dp[n+1] = {true, false, false, ..., false}
    
    for i from 1 to n:
        for j from 0 to i-1:
            if dp[j] and s[j:i] in wordDict:
                dp[i] = true
                break
    
    return dp[n]
```

**Recurrence**: `dp[i] = true` if exists `j < i` such that `dp[j] = true` and `s[j:i]` is in dictionary

---

### 13. **House Robber**
**Time Complexity**: O(n) | **Space Complexity**: O(1)

**Problem**: Maximize robbery amount without robbing adjacent houses

**Pseudocode**:
```
function rob(nums):
    if len(nums) == 0: return 0
    if len(nums) == 1: return nums[0]
    
    prev2 = nums[0]
    prev1 = max(nums[0], nums[1])
    
    for i from 2 to n-1:
        current = max(prev1, nums[i] + prev2)
        prev2 = prev1
        prev1 = current
    
    return prev1
```

**Recurrence**: `dp[i] = max(dp[i-1], nums[i] + dp[i-2])`

---

### 14. **Climbing Stairs with Variable Steps**
**Time Complexity**: O(n × k) where k is max steps | **Space Complexity**: O(n)

**Problem**: Count ways to reach top with k-step variations

**Pseudocode**:
```
function climbStairs(n, k):
    dp[n+1] = {1, 0, 0, ..., 0}
    
    for i from 1 to n:
        for j from 1 to min(i, k):
            dp[i] += dp[i-j]
    
    return dp[n]
```

**Recurrence**: `dp[i] = sum(dp[i-j])` for `j = 1 to k`

---

### 15. **Minimum Path Sum in Grid**
**Time Complexity**: O(m × n) | **Space Complexity**: O(n)

**Problem**: Find path from top-left to bottom-right with minimum sum

**Pseudocode**:
```
function minPathSum(grid, m, n):
    dp[m][n]
    dp[0][0] = grid[0][0]
    
    // Initialize first row
    for j from 1 to n-1:
        dp[0][j] = dp[0][j-1] + grid[0][j]
    
    // Initialize first column
    for i from 1 to m-1:
        dp[i][0] = dp[i-1][0] + grid[i][0]
    
    for i from 1 to m-1:
        for j from 1 to n-1:
            dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])
    
    return dp[m-1][n-1]
```

**Recurrence**: `dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])`

---

### 16. **Longest Palindromic Subsequence**
**Time Complexity**: O(n²) | **Space Complexity**: O(n²)

**Problem**: Find length of longest palindromic subsequence

**Pseudocode**:
```
function longestPalindromeSubseq(s):
    n = len(s)
    dp[n][n]
    
    // Every single character is a palindrome
    for i from 0 to n-1:
        dp[i][i] = 1
    
    // Build table
    for length from 2 to n:
        for i from 0 to n-length:
            j = i + length - 1
            
            if s[i] == s[j]:
                dp[i][j] = dp[i+1][j-1] + 2
            else:
                dp[i][j] = max(dp[i+1][j], dp[i][j-1])
    
    return dp[0][n-1]
```

**Recurrence**:
```
dp[i][j] = dp[i+1][j-1] + 2              if s[i] == s[j]
         = max(dp[i+1][j], dp[i][j-1])   otherwise
```

---

### 17. **Egg Drop Problem**
**Time Complexity**: O(k × n²) | **Space Complexity**: O(k × n)

**Problem**: Minimum trials needed in worst case with k eggs and n floors

**Pseudocode**:
```
function eggDrop(k, n):
    dp[k+1][n+1]
    
    // Base cases
    for i from 1 to k: dp[i][1] = 1  // 1 floor needs 1 trial
    for j from 1 to n: dp[1][j] = j  // 1 egg needs j trials for j floors
    
    for eggs from 2 to k:
        for floors from 2 to n:
            dp[eggs][floors] = ∞
            
            for x from 1 to floors:
                // Egg breaks: dp[eggs-1][x-1]
                // Egg doesn't break: dp[eggs][floors-x]
                trials = 1 + max(dp[eggs-1][x-1], dp[eggs][floors-x])
                dp[eggs][floors] = min(dp[eggs][floors], trials)
    
    return dp[k][n]
```

**Optimized**: O(k × n × log n) using binary search

---

### 18. **Burst Balloons**
**Time Complexity**: O(n³) | **Space Complexity**: O(n²)

**Problem**: Maximize coins collected by bursting balloons optimally

**Pseudocode**:
```
function maxCoins(nums):
    n = len(nums)
    balloons = [1] + nums + [1]  // Add boundary balloons
    dp[n+2][n+2]  // Initialize to 0
    
    for length from 1 to n:
        for left from 1 to n-length+1:
            right = left + length - 1
            
            for i from left to right:
                // Burst balloon i last in range [left, right]
                coins = balloons[left-1] * balloons[i] * balloons[right+1]
                coins += dp[left][i-1] + dp[i+1][right]
                dp[left][right] = max(dp[left][right], coins)
    
    return dp[1][n]
```

**Key Insight**: Think of which balloon to burst **last** in each range

---

### 19. **Regular Expression Matching**
**Time Complexity**: O(m × n) | **Space Complexity**: O(m × n)

**Problem**: Match string with pattern containing '.' and '*'

**Pseudocode**:
```
function isMatch(s, p):
    m, n = len(s), len(p)
    dp[m+1][n+1] = false
    dp[0][0] = true
    
    // Handle patterns like a*, a*b*, a*b*c*
    for j from 2 to n:
        if p[j-1] == '*':
            dp[0][j] = dp[0][j-2]
    
    for i from 1 to m:
        for j from 1 to n:
            if p[j-1] == '*':
                // Two cases: use * or ignore pattern
                dp[i][j] = dp[i][j-2]  // Don't use *
                
                if p[j-2] == s[i-1] or p[j-2] == '.':
                    dp[i][j] = dp[i][j] OR dp[i-1][j]  // Use *
            else:
                if p[j-1] == s[i-1] or p[j-1] == '.':
                    dp[i][j] = dp[i-1][j-1]
    
    return dp[m][n]
```

---

### 20. **Traveling Salesman Problem (TSP) - Bitmask DP**
**Time Complexity**: O(n² × 2ⁿ) | **Space Complexity**: O(n × 2ⁿ)

**Problem**: Find shortest path visiting all cities exactly once

**Pseudocode**:
```
function tsp(dist, n):
    dp[1 << n][n]  // Initialize to ∞
    dp[1][0] = 0   // Start at city 0
    
    for mask from 1 to (1 << n) - 1:
        for u from 0 to n-1:
            if not (mask & (1 << u)): continue
            
            for v from 0 to n-1:
                if mask & (1 << v): continue
                
                newMask = mask | (1 << v)
                dp[newMask][v] = min(dp[newMask][v], dp[mask][u] + dist[u][v])
    
    // Find minimum cost to return to start
    result = ∞
    for i from 1 to n-1:
        result = min(result, dp[(1<<n)-1][i] + dist[i][0])
    
    return result
```

**Recurrence**: `dp[mask][i] = min(dp[mask^(1<<i)][j] + dist[j][i])` for all `j` in `mask`

---

## 🔬 Advanced DP Techniques

### **Convex Hull Trick** - O(n log n) or O(n)
Used for optimizing DP with linear functions. Maintains convex hull of lines to answer queries efficiently.

**Use Case**: When recurrence involves `min/max` over linear functions

### **Divide and Conquer Optimization** - O(n² log n) → O(n log n)
When DP recurrence satisfies monotonicity condition: `opt[i][j] ≤ opt[i][j+1]`

**Use Case**: 2D DP problems with monotonic optimal split point

### **Knuth Optimization** - O(n³) → O(n²)
When quadrangle inequality holds: `cost[a][c] + cost[b][d] ≤ cost[a][d] + cost[b][c]`

**Use Case**: Problems like optimal binary search tree

### **Monotonic Queue Optimization** - O(nk) → O(n)
Maintain deque of candidate optimal values within sliding window

**Use Case**: Sliding window maximum/minimum in DP transitions

---

## Problems List

### Linear DP - Basic
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Fibonacci Number | [LeetCode 509](https://leetcode.com/problems/fibonacci-number/) | [FibonacciNumber.java](./linear/basic/FibonacciNumber.java) |
| Climbing Stairs | [LeetCode 70](https://leetcode.com/problems/climbing-stairs/) | [ClimbingStairs.java](./linear/basic/ClimbingStairs.java) |
| N-th Tribonacci Number | [LeetCode 1137](https://leetcode.com/problems/n-th-tribonacci-number/) | [NthTribonacciNumber.java](./linear/basic/NthTribonacciNumber.java) |
`
### Linear DP - Optimization
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| House Robber | [LeetCode 198](https://leetcode.com/problems/house-robber/) | [HouseRobber.java](./linear/optimization/HouseRobber.java) |
| House Robber II | [LeetCode 213](https://leetcode.com/problems/house-robber-ii/) | [HouseRobberII.java](./linear/optimization/HouseRobberII.java) |
| Delete and Earn | [LeetCode 740](https://leetcode.com/problems/delete-and-earn/) | [DeleteAndEarn.java](./linear/optimization/DeleteAndEarn.java) |
| The Masseuse LCCI | [LeetCode 17.01](https://leetcode.com/problems/the-masseuse-lcci/) | [MasseuseLCCI.java](./linear/optimization/MasseuseLCCI.java) |
| Min Cost Climbing Stairs | [LeetCode 746](https://leetcode.com/problems/min-cost-climbing-stairs/) | [MinCostClimbingStairs.java](./linear/optimization/MinCostClimbingStairs.java) |
| Paint Fence | [LeetCode 276](https://leetcode.com/problems/paint-fence/) | [PaintFence.java](./linear/optimization/PaintFence.java) |
| Paint House | [LeetCode 256](https://leetcode.com/problems/paint-house/) | [PaintHouse.java](./linear/optimization/PaintHouse.java) |

### Linear DP - Sequence
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Maximum Subarray | [LeetCode 53](https://leetcode.com/problems/maximum-subarray/) | [MaximumSubarray.java](./linear/sequence/MaximumSubarray.java) |
| Longest Increasing Subsequence | [LeetCode 300](https://leetcode.com/problems/longest-increasing-subsequence/) | [LongestIncreasingSubsequence.java](./linear/sequence/LongestIncreasingSubsequence.java) |
| Maximum Product Subarray | [LeetCode 152](https://leetcode.com/problems/maximum-product-subarray/) | [MaximumProductSubarray.java](./linear/sequence/MaximumProductSubarray.java) |
| Arithmetic Slices | [LeetCode 413](https://leetcode.com/problems/arithmetic-slices/) | [ArithmeticSlices.java](./linear/sequence/ArithmeticSlices.java) |
| Longest String Chain | [LeetCode 1048](https://leetcode.com/problems/longest-string-chain/) | [LongestStringChain.java](./linear/sequence/LongestStringChain.java) |
| Number of Longest Increasing Subsequence | [LeetCode 673](https://leetcode.com/problems/number-of-longest-increasing-subsequence/) | [NumberOfLongestIncreasingSubsequence.java](./linear/sequence/NumberOfLongestIncreasingSubsequence.java) |

### Grid DP - Path Counting
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Unique Paths | [LeetCode 62](https://leetcode.com/problems/unique-paths/) | [UniquePaths.java](./grid/path_counting/UniquePaths.java) |
| Unique Paths II | [LeetCode 63](https://leetcode.com/problems/unique-paths-ii/) | [UniquePathsWithObstacles.java](./grid/path_counting/UniquePathsWithObstacles.java) |
| Pascal's Triangle | [LeetCode 118](https://leetcode.com/problems/pascals-triangle/) | [PascalsTriangle.java](./grid/path_counting/PascalsTriangle.java) |
| Pascal's Triangle II | [LeetCode 119](https://leetcode.com/problems/pascals-triangle-ii/) | [PascalsTriangleII.java](./grid/path_counting/PascalsTriangleII.java) |

### Grid DP - Optimization
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Minimum Path Sum | [LeetCode 64](https://leetcode.com/problems/minimum-path-sum/) | [MinimumPathSum.java](./grid/optimization/MinimumPathSum.java) |
| Minimum Falling Path Sum | [LeetCode 931](https://leetcode.com/problems/minimum-falling-path-sum/) | [MinimumFallingPathSum.java](./grid/optimization/MinimumFallingPathSum.java) |
| Cherry Pickup | [LeetCode 741](https://leetcode.com/problems/cherry-pickup/) | [CherryPickupI.java](./grid/optimization/CherryPickupI.java) |
| Cherry Pickup II | [LeetCode 1463](https://leetcode.com/problems/cherry-pickup-ii/) | [CherryPickupII.java](./grid/optimization/CherryPickupII.java) |

### String DP - Subsequence
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Longest Common Subsequence | [LeetCode 1143](https://leetcode.com/problems/longest-common-subsequence/) | [LongestCommonSubsequence.java](./string/subsequence/LongestCommonSubsequence.java) |
| Distinct Subsequences | [LeetCode 115](https://leetcode.com/problems/distinct-subsequences/) | [DistinctSubsequences.java](./string/subsequence/DistinctSubsequences.java) |
| Is Subsequence | [LeetCode 392](https://leetcode.com/problems/is-subsequence/) | [IsSubsequence.java](./string/subsequence/IsSubsequence.java) |
| Longest Palindromic Subsequence | [LeetCode 516](https://leetcode.com/problems/longest-palindromic-subsequence/) | [LongestPalindromicSubsequence.java](./string/subsequence/LongestPalindromicSubsequence.java) |
| Count Different Palindromic Subsequences | [LeetCode 730](https://leetcode.com/problems/count-different-palindromic-subsequences/) | [CountDifferentPalindromicSubsequences.java](./string/subsequence/CountDifferentPalindromicSubsequences.java) |

### String DP - Matching
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Edit Distance | [LeetCode 72](https://leetcode.com/problems/edit-distance/) | [EditDistance.java](./string/matching/EditDistance.java) |
| Regular Expression Matching | [LeetCode 10](https://leetcode.com/problems/regular-expression-matching/) | [RegularExpressionMatching.java](./string/matching/RegularExpressionMatching.java) |
| Wildcard Matching | [LeetCode 44](https://leetcode.com/problems/wildcard-matching/) | [WildcardMatching.java](./string/matching/WildcardMatching.java) |
| Interleaving String | [LeetCode 97](https://leetcode.com/problems/interleaving-string/) | [InterleaveString.java](./string/matching/InterleaveString.java) |
| Scramble String | [LeetCode 87](https://leetcode.com/problems/scramble-string/) | [ScrambleString.java](./string/matching/ScrambleString.java) |

### String DP - Palindrome
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Palindromic Substrings | [LeetCode 647](https://leetcode.com/problems/palindromic-substrings/) | [PalindromicSubstrings.java](./string/palindrome/PalindromicSubstrings.java) |
| Minimum Insertion Steps to Make a String Palindrome | [LeetCode 1312](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) | [MinimumInsertionStepsToMakeStringPalindrome.java](./string/palindrome/MinimumInsertionStepsToMakeStringPalindrome.java) |
| Palindrome Partitioning II | [LeetCode 132](https://leetcode.com/problems/palindrome-partitioning-ii/) | [PalindromePartitioningII.java](./string/palindrome/PalindromePartitioningII.java) |
| Palindrome Partitioning III | [LeetCode 1278](https://leetcode.com/problems/palindrome-partitioning-iii/) | [PalindromePartitioningIII.java](./string/palindrome/PalindromePartitioningIII.java) |

### Knapsack DP - Subset Sum
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Partition Equal Subset Sum | [LeetCode 416](https://leetcode.com/problems/partition-equal-subset-sum/) | [PartitionEqualSubsetSum.java](./knapsack/subset_sum/PartitionEqualSubsetSum.java) |
| Target Sum | [LeetCode 494](https://leetcode.com/problems/target-sum/) | [TargetSum.java](./knapsack/subset_sum/TargetSum.java) |

### Knapsack DP - Unbounded
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Coin Change | [LeetCode 322](https://leetcode.com/problems/coin-change/) | [CoinChange.java](./knapsack/unbounded/CoinChange.java) |
| Coin Change 2 | [LeetCode 518](https://leetcode.com/problems/coin-change-2/) | [CoinChangeII.java](./knapsack/unbounded/CoinChangeII.java) |
| Combination Sum IV | [LeetCode 377](https://leetcode.com/problems/combination-sum-iv/) | [CombinationSumIV.java](./knapsack/unbounded/CombinationSumIV.java) |

### Knapsack DP - Other
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| 0/1 Knapsack Problem | [Knapsack Pattern](https://leetcode.com/problems/partition-equal-subset-sum/) | [KnapsackProblem.java](./knapsack/KnapsackProblem.java) |

### Game Theory DP
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Divisor Game | [LeetCode 1025](https://leetcode.com/problems/divisor-game/) | [DivisorGame.java](./game_theory/DivisorGame.java) |
| Stone Game II | [LeetCode 1140](https://leetcode.com/problems/stone-game-ii/) | [StoneGameII.java](./game_theory/StoneGameII.java) |
| Stone Game III | [LeetCode 1406](https://leetcode.com/problems/stone-game-iii/) | [StoneGameIII.java](./game_theory/StoneGameIII.java) |

### Stock Trading DP
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Best Time to Buy and Sell Stock | [LeetCode 121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | [BestTimeToBuyAndSellStock.java](./stock_trading/BestTimeToBuyAndSellStock.java) |
| Best Time to Buy and Sell Stock IV | [LeetCode 188](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/) | [BestTimeToBuyAndSellStockIV.java](./stock_trading/BestTimeToBuyAndSellStockIV.java) |
| Best Time to Buy and Sell Stock with Cooldown | [LeetCode 309](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | [BestTimeToBuyAndSellStockWithCooldown.java](./stock_trading/BestTimeToBuyAndSellStockWithCooldown.java) |
| Best Time to Buy and Sell Stock with Transaction Fee | [LeetCode 714](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/) | [BestTimeToBuyAndSellStockWithTransactionFee.java](./stock_trading/BestTimeToBuyAndSellStockWithTransactionFee.java) |

### Interval DP
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Burst Balloons | [LeetCode 312](https://leetcode.com/problems/burst-balloons/) | [BurstBalloons.java](./interval/BurstBalloons.java) |
| Strange Printer | [LeetCode 664](https://leetcode.com/problems/strange-printer/) | [StrangePrinter.java](./interval/StrangePrinter.java) |
| Minimum Difficulty of a Job Schedule | [LeetCode 1335](https://leetcode.com/problems/minimum-difficulty-of-a-job-schedule/) | [MinimumDifficultyOfAJobSchedule.java](./interval/MinimumDifficultyOfAJobSchedule.java) |

### State Machine DP
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Decode Ways | [LeetCode 91](https://leetcode.com/problems/decode-ways/) | [DecodeWays.java](./state_machine/DecodeWays.java) |
| Decode Ways II | [LeetCode 639](https://leetcode.com/problems/decode-ways-ii/) | [DecodeWaysII.java](./state_machine/DecodeWaysII.java) |

### Mathematical DP
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Counting Bits | [LeetCode 338](https://leetcode.com/problems/counting-bits/) | [CountingBits.java](./mathematical/CountingBits.java) |
| Get Maximum in Generated Array | [LeetCode 1646](https://leetcode.com/problems/get-maximum-in-generated-array/) | [GetMaximumInGeneratedArray.java](./mathematical/GetMaximumInGeneratedArray.java) |
| Minimum Cost For Tickets | [LeetCode 983](https://leetcode.com/problems/minimum-cost-for-tickets/) | [MinimumCostForTickets.java](./mathematical/MinimumCostForTickets.java) |

### Advanced Patterns
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Super Egg Drop | [LeetCode 887](https://leetcode.com/problems/super-egg-drop/) | [SuperEggDrop.java](./advanced/SuperEggDrop.java) |
| Count All Valid Pickup and Delivery Options | [LeetCode 1359](https://leetcode.com/problems/count-all-valid-pickup-and-delivery-options/) | [CountAllValidPickupAndDeliveryOptions.java](./advanced/CountAllValidPickupAndDeliveryOptions.java) |
| Count Numbers with Unique Digits | [LeetCode 357](https://leetcode.com/problems/count-numbers-with-unique-digits/) | [CountNumbersWithUniqueDigits.java](./advanced/CountNumbersWithUniqueDigits.java) |
| Count Square Submatrices with All Ones | [LeetCode 1277](https://leetcode.com/problems/count-square-submatrices-with-all-ones/) | [CountSquareSubmatricesWithAllOnes.java](./advanced/CountSquareSubmatricesWithAllOnes.java) |
| Count Submatrices with All Ones | [LeetCode 1504](https://leetcode.com/problems/count-submatrices-with-all-ones/) | [CountSubmatricesWithAllOnes.java](./advanced/CountSubmatricesWithAllOnes.java) |
| Count Vowels Permutation | [LeetCode 1220](https://leetcode.com/problems/count-vowels-permutation/) | [CountVowelsPermutation.java](./advanced/CountVowelsPermutation.java) |
| Count Ways to Build Staircases | [Staircase Pattern](https://leetcode.com/problems/climbing-stairs/) | [CountWaysToBuildStaircases.java](./advanced/CountWaysToBuildStaircases.java) |
| Distinct Subsequences II | [LeetCode 940](https://leetcode.com/problems/distinct-subsequences-ii/) | [DistinctSubsequencesII.java](./advanced/DistinctSubsequencesII.java) |
| Domino and Tromino Tiling | [LeetCode 790](https://leetcode.com/problems/domino-and-tromino-tiling/) | [DominoAndTrominoTiling.java](./advanced/DominoAndTrominoTiling.java) |
| Jump Game II | [LeetCode 45](https://leetcode.com/problems/jump-game-ii/) | [JumpGameII.java](./advanced/JumpGameII.java) |
| Longest Alternating Subsequence | [LeetCode 1911](https://leetcode.com/problems/longest-alternating-subsequence/) | [LongestAlternatingSubsequence.java](./advanced/LongestAlternatingSubsequence.java) |
| Longest Arithmetic Subsequence | [LeetCode 1027](https://leetcode.com/problems/longest-arithmetic-subsequence/) | [LongestArithmeticSubsequence.java](./advanced/LongestArithmeticSubsequence.java) |
| Longest Bitonic Subsequence | [Bitonic Pattern](https://leetcode.com/problems/longest-increasing-subsequence/) | [LongestBitonicSubsequence.java](./advanced/LongestBitonicSubsequence.java) |
| Longest Turbulent Subarray | [LeetCode 978](https://leetcode.com/problems/longest-turbulent-subarray/) | [LongestTurbulentSubarray.java](./advanced/LongestTurbulentSubarray.java) |
| Maximal Rectangle | [LeetCode 85](https://leetcode.com/problems/maximal-rectangle/) | [MaximalRectangle.java](./advanced/MaximalRectangle.java) |
| Maximum Length of Repeated Subarray | [LeetCode 718](https://leetcode.com/problems/maximum-length-of-repeated-subarray/) | [MaximumLengthOfRepeatedSubarray.java](./advanced/MaximumLengthOfRepeatedSubarray.java) |
| Maximum Points You Can Obtain from Cards | [LeetCode 1423](https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/) | [MaximumPointsYouCanObtainFromCards.java](./advanced/MaximumPointsYouCanObtainFromCards.java) |
| Maximum Profit in Job Scheduling | [LeetCode 1235](https://leetcode.com/problems/maximum-profit-in-job-scheduling/) | [MaximumProfitInJobScheduling.java](./advanced/MaximumProfitInJobScheduling.java) |
| Maximum Subarray Sum with One Deletion | [LeetCode 1186](https://leetcode.com/problems/maximum-subarray-sum-with-one-deletion/) | [MaximumSubarraySumWithOneDeletion.java](./advanced/MaximumSubarraySumWithOneDeletion.java) |
| Maximum Sum Circular Subarray | [LeetCode 918](https://leetcode.com/problems/maximum-sum-circular-subarray/) | [MaximumSumCircularSubarray.java](./advanced/MaximumSumCircularSubarray.java) |
| Maximum Sum of Three Non-Overlapping Subarrays | [LeetCode 689](https://leetcode.com/problems/maximum-sum-of-3-non-overlapping-subarrays/) | [MaximumSumOfThreeNonOverlappingSubarrays.java](./advanced/MaximumSumOfThreeNonOverlappingSubarrays.java) |
| Maximum Vacation Days | [LeetCode 568](https://leetcode.com/problems/maximum-vacation-days/) | [MaximumVacationDays.java](./advanced/MaximumVacationDays.java) |
| Minimum Number of Removals to Make Mountain Array | [LeetCode 1671](https://leetcode.com/problems/minimum-number-of-removals-to-make-mountain-array/) | [MinimumNumberOfRemovalsToMakeMountainArray.java](./advanced/MinimumNumberOfRemovalsToMakeMountainArray.java) |
| Minimum Swaps to Make Sequences Increasing | [LeetCode 801](https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing/) | [MinimumSwapsToMakeSequencesIncreasing.java](./advanced/MinimumSwapsToMakeSequencesIncreasing.java) |
| Number of Submatrices That Sum to Target | [LeetCode 1074](https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/) | [NumberOfSubmatricesThatSumToTarget.java](./advanced/NumberOfSubmatricesThatSumToTarget.java) |
| Number of Ways to Stay in the Same Place After Some Steps | [LeetCode 1269](https://leetcode.com/problems/number-of-ways-to-stay-in-the-same-place-after-some-steps/) | [NumberOfWaysToStayInSamePlace.java](./advanced/NumberOfWaysToStayInSamePlace.java) |
| Paint House II | [LeetCode 265](https://leetcode.com/problems/paint-house-ii/) | [PaintHouseII.java](./advanced/PaintHouseII.java) |
| Partition Array for Maximum Sum | [LeetCode 1043](https://leetcode.com/problems/partition-array-for-maximum-sum/) | [PartitionArrayForMaximumSum.java](./advanced/PartitionArrayForMaximumSum.java) |
| Path Sum | [LeetCode 112](https://leetcode.com/problems/path-sum/) | [PathSum.java](./advanced/PathSum.java) |
| Profitable Schemes | [LeetCode 879](https://leetcode.com/problems/profitable-schemes/) | [ProfitableSchemes.java](./advanced/ProfitableSchemes.java) |
| Range Sum Query - Immutable | [LeetCode 303](https://leetcode.com/problems/range-sum-query-immutable/) | [RangeSumQueryImmutable.java](./advanced/RangeSumQueryImmutable.java) |
| Remove Boxes | [LeetCode 546](https://leetcode.com/problems/remove-boxes/) | [RemoveBoxes.java](./advanced/RemoveBoxes.java) |
| Video Stitching | [LeetCode 1024](https://leetcode.com/problems/video-stitching/) | [VideoStitching.java](./advanced/VideoStitching.java) |
| Word Break | [LeetCode 139](https://leetcode.com/problems/word-break/) | [WordBreak.java](./advanced/WordBreak.java) |
| Word Break II | [LeetCode 140](https://leetcode.com/problems/word-break-ii/) | [WordBreakII.java](./advanced/WordBreakII.java) |
| Arithmetic Slices | [LeetCode 413](https://leetcode.com/problems/arithmetic-slices/) | [ArithmeticSlices.java](./advanced/ArithmeticSlices.java) |
| Best Time to Buy and Sell Stock IV | [LeetCode 188](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/) | [BestTimeToBuyAndSellStockIV.java](./advanced/BestTimeToBuyAndSellStockIV.java) |
| Climbing Stairs | [LeetCode 70](https://leetcode.com/problems/climbing-stairs/) | [ClimbingStairs.java](./advanced/ClimbingStairs.java) |
| Distinct Subsequences | [LeetCode 115](https://leetcode.com/problems/distinct-subsequences/) | [DistinctSubsequences.java](./advanced/DistinctSubsequences.java) |
| Min Cost Climbing Stairs | [LeetCode 746](https://leetcode.com/problems/min-cost-climbing-stairs/) | [MinCostClimbingStairs.java](./advanced/MinCostClimbingStairs.java) |
| Unique Paths | [LeetCode 62](https://leetcode.com/problems/unique-paths/) | [UniquePaths.java](./advanced/UniquePaths.java) |

## Problem Categories

### Linear DP Patterns
- Basic Sequences: Fibonacci (509), Climbing Stairs (70), Tribonacci (1137)
- Optimization: House Robber (198, 213), Delete and Earn (740), Paint House (256)
- Subsequences: LIS (300), Maximum Subarray (53), Arithmetic Slices (413)

### Grid-Based DP
- Path Counting: Unique Paths (62, 63), Pascal's Triangle (118, 119)
- Grid Optimization: Minimum Path Sum (64), Cherry Pickup (741, 1463)

### String DP
- LCS Variants: Longest Common Subsequence (1143), Edit Distance (72)
- Pattern Matching: Regular Expression (10), Wildcard Matching (44)
- Palindromes: Palindromic Substrings (647), Palindrome Partitioning (132)

### Knapsack Variants
- 0/1 Knapsack: Partition Equal Subset Sum (416), Target Sum (494)
- Unbounded: Coin Change (322, 518), Combination Sum IV (377)

### Specialized Patterns
- Game Theory: Stone Game variants, Divisor Game (1025)
- Stock Trading: Multiple buy/sell scenarios with constraints
- Interval DP: Burst Balloons (312), Matrix Chain Multiplication style
- State Machine: Decode Ways (91, 639), finite state automation

## Core Algorithms & Techniques Used

### 1. Basic Dynamic Programming (Memoization & Tabulation)
**Problems**: Fibonacci Number, Climbing Stairs, House Robber, Coin Change
- **Blog**: [Dynamic Programming - GeeksforGeeks](https://www.geeksforgeeks.org/dynamic-programming/)
- **LeetCode**: [DP Study Guide](https://leetcode.com/discuss/study-guide/458695/Dynamic-Programming-Patterns)
- **LeetCode**: [Must Do DP Problems](https://leetcode.com/discuss/general-discussion/1050391/Must-do-dynamic-programming-problems-category-wise-with-solutions)
- **Medium**: [DP for Beginners](https://medium.com/@codingfreak/dynamic-programming-for-dummies-aba22778bbef)
- **TopCoder**: [DP Tutorial](https://www.topcoder.com/thrive/articles/Dynamic%20Programming:%20From%20Novice%20to%20Advanced)
- **HackerEarth**: [DP Tutorial](https://www.hackerearth.com/practice/algorithms/dynamic-programming/introduction-to-dynamic-programming-1/tutorial/)

### 2. Linear DP Patterns
**Problems**: Maximum Subarray (Kadane's), Longest Increasing Subsequence, House Robber variants
- **Blog**: [Linear DP Patterns - GeeksforGeeks](https://www.geeksforgeeks.org/solve-dynamic-programming-problem/)
- **LeetCode**: [Linear DP Guide](https://leetcode.com/discuss/study-guide/1308617/Dynamic-Programming-Patterns)

### 3. Grid DP & 2D Matrix Problems
**Problems**: Unique Paths, Minimum Path Sum, Cherry Pickup, Pascal's Triangle
- **Blog**: [Grid DP - GeeksforGeeks](https://www.geeksforgeeks.org/count-possible-paths-top-left-bottom-right-nxm-matrix/)
- **LeetCode**: [Grid DP Patterns](https://leetcode.com/discuss/study-guide/1308617/Dynamic-Programming-Patterns)

### 4. String DP Algorithms
**Problems**: Longest Common Subsequence, Edit Distance, Regular Expression Matching, Palindrome problems
- **Blog**: [String DP - GeeksforGeeks](https://www.geeksforgeeks.org/longest-common-subsequence-dp-4/)
- **CP-Algorithms**: [String DP](https://cp-algorithms.com/string/)
- **LeetCode**: [String DP Guide](https://leetcode.com/discuss/study-guide/2001789/Collections-of-Important-String-questions-Pattern)

### 5. Knapsack DP Variations
**Problems**: 0/1 Knapsack, Unbounded Knapsack, Coin Change, Partition Equal Subset Sum
- **Blog**: [Knapsack DP - GeeksforGeeks](https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10/)
- **LeetCode**: [Knapsack Patterns](https://leetcode.com/discuss/study-guide/1200320/Thief-with-a-knapsack-a-series-of-crimes)

### 6. Interval DP (Matrix Chain Multiplication Style)
**Problems**: Burst Balloons, Strange Printer, Matrix Chain Multiplication
- **Blog**: [Interval DP - GeeksforGeeks](https://www.geeksforgeeks.org/matrix-chain-multiplication-dp-8/)
- **CP-Algorithms**: [Interval DP](https://cp-algorithms.com/dynamic_programming/divide-and-conquer-dp.html)

### 7. Tree DP
**Problems**: Path Sum, House Robber III, Maximum Path Sum in Binary Tree
- **Blog**: [Tree DP - GeeksforGeeks](https://www.geeksforgeeks.org/dynamic-programming-trees-set-1/)

### 8. Game Theory DP (Minimax)
**Problems**: Stone Game variants, Divisor Game, Predict the Winner
- **Blog**: [Game Theory DP - GeeksforGeeks](https://www.geeksforgeeks.org/optimal-strategy-for-a-game-dp-31/)
- **CP-Algorithms**: [Game Theory](https://cp-algorithms.com/game_theory/)

### 9. State Machine DP
**Problems**: Decode Ways, Stock Trading problems, State transitions
- **Blog**: [State Machine DP - GeeksforGeeks](https://www.geeksforgeeks.org/state-machine-based-dp-problems/)

### 10. Mathematical DP & Bit Manipulation
**Problems**: Counting Bits, Catalan Numbers (Generate Parentheses), Pascal's Triangle
- **Blog**: [Mathematical DP - GeeksforGeeks](https://www.geeksforgeeks.org/mathematical-algorithms/)
- **Blog**: [Bit Manipulation DP](https://www.geeksforgeeks.org/bits-manipulation-important-tactics/)

### 11. Space Optimization Techniques
**Problems**: Most DP problems can be space-optimized using rolling arrays
- **Blog**: [Space Optimization in DP](https://www.geeksforgeeks.org/space-optimized-solution-lcs/)

### 12. Advanced DP Optimizations
**Problems**: Divide and Conquer DP, Convex Hull Optimization, Monotonic Deque
- **CP-Algorithms**: [DP Optimizations](https://cp-algorithms.com/dynamic_programming/)

## Key Patterns & Templates

### 1. Basic DP Template (Top-down Memoization)
```java
Map<String, Integer> memo = new HashMap<>();

public int dpSolution(int[] arr, int index, int target) {
    String key = index + "," + target;
    if (memo.containsKey(key)) return memo.get(key);
    
    // Base cases
    if (index >= arr.length) return target == 0 ? 1 : 0;
    
    // Recursive choices
    int include = dpSolution(arr, index + 1, target - arr[index]);
    int exclude = dpSolution(arr, index + 1, target);
    
    int result = include + exclude;
    memo.put(key, result);
    return result;
}
```

### 2. Bottom-up DP Template
```java
public int dpSolution(int[] arr, int target) {
    int n = arr.length;
    int[][] dp = new int[n + 1][target + 1];
    
    // Base case
    dp[0][0] = 1;
    
    for (int i = 1; i <= n; i++) {
        for (int j = 0; j <= target; j++) {
            dp[i][j] = dp[i-1][j]; // exclude
            if (j >= arr[i-1]) {
                dp[i][j] += dp[i-1][j - arr[i-1]]; // include
            }
        }
    }
    return dp[n][target];
}
```

### 3. Linear DP Template (Kadane's Style)
```java
public int maxSubarray(int[] nums) {
    int maxSoFar = nums[0];
    int maxEndingHere = nums[0];
    
    for (int i = 1; i < nums.length; i++) {
        maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
    }
    return maxSoFar;
}
```

### 4. Grid DP Template
```java
public int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    
    // Initialize first row and column
    for (int i = 0; i < m; i++) dp[i][0] = 1;
    for (int j = 0; j < n; j++) dp[0][j] = 1;
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[i][j] = dp[i-1][j] + dp[i][j-1];
        }
    }
    return dp[m-1][n-1];
}
```

### 5. String DP Template (LCS)
```java
public int longestCommonSubsequence(String text1, String text2) {
    int m = text1.length(), n = text2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (text1.charAt(i-1) == text2.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
    }
    return dp[m][n];
}
```

## Company Tags Frequency

### Most Frequently Asked (40+ problems)
- **Amazon**: 100 problems
- **Microsoft**: 85 problems
- **Facebook (Meta)**: 75 problems
- **Google**: 70 problems

### Other Companies
- Apple, Netflix, Adobe, Uber, LinkedIn, ByteDance, Bloomberg, Twitter, Airbnb

## Difficulty Distribution
- **Linear DP - Basic**: 3 problems (Easy level)
- **Linear DP - Optimization**: 7 problems (Easy to Medium)
- **Linear DP - Sequence**: 6 problems (Medium level)
- **Grid DP**: 8 problems (Easy to Medium)
- **String DP**: 14 problems (Medium to Hard)
- **Knapsack DP**: 6 problems (Medium level)
- **Specialized Patterns**: 15 problems (Medium to Hard)
- **Advanced Patterns**: 41 problems (Hard level)

## Time Complexity Patterns
- **Linear DP**: O(n) for most single-dimension problems
- **Grid DP**: O(m × n) for 2D grid problems
- **String DP**: O(m × n) for most string comparison problems
- **Knapsack**: O(n × target) for subset/knapsack problems
- **Interval DP**: O(n³) for most interval-based problems
- **Tree DP**: O(n) for tree traversal problems

## Study Path Recommendations

### Beginner Level (Start Here)
1. Fibonacci Number (509) - Introduction to memoization
2. Climbing Stairs (70) - Basic state transition
3. House Robber (198) - Constraint handling
4. Maximum Subarray (53) - Kadane's algorithm

### Intermediate Level
1. Unique Paths (62) - 2D DP introduction
2. Coin Change (322) - Unbounded knapsack
3. Longest Increasing Subsequence (300) - Binary search optimization
4. Longest Common Subsequence (1143) - String DP foundation

### Advanced Level
1. Edit Distance (72) - String transformation
2. Burst Balloons (312) - Interval DP mastery
3. Regular Expression Matching (10) - Pattern matching
4. Cherry Pickup II (1463) - Multi-dimensional states

### Expert Level
1. Super Egg Drop (887) - Binary search + DP
2. Count All Valid Pickup and Delivery Options (1359) - Mathematical DP
3. Maximum Sum of Three Non-Overlapping Subarrays (689) - Complex optimization
4. Stone Game III (1406) - Game theory mastery

## Implementation Features

### Each Problem Includes:
- ✅ Multiple solution approaches (2-5 different methods)
- ✅ Comprehensive test cases with edge cases
- ✅ Company tags and frequency information
- ✅ Clickable LeetCode URLs (when available)
- ✅ Time and space complexity analysis
- ✅ Follow-up questions and variations
- ✅ Performance comparisons
- ✅ Detailed comments and explanations

### Code Quality Standards:
- Clean, readable implementations
- Proper package structure (dp.pattern.subpattern)
- Edge case coverage
- Space optimization when possible
- Interview-ready format
- Extensive validation methods
- Multiple algorithm approaches demonstrated

## Recent Updates (2024)
- Reorganized from difficulty-based to pattern-based structure
- Fixed all package declarations to match directory structure
- Enhanced with comprehensive problem inventory
- Added 100 actual implementations (vs previously claimed 88)
- Updated with pattern-based learning progression
- Improved educational value with progressive difficulty

## Notes
- Master basic DP concepts (memoization/tabulation) first
- Focus on understanding patterns rather than memorizing solutions
- Practice space optimization techniques for interview preparation
- Each pattern builds upon previous concepts
- DP is fundamental for optimization problems in interviews
- Each file contains 200-400 lines of comprehensive implementation
- Pattern-based organization provides better learning progression than 

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

### 🟢 Beginner (Linear DP - Basic Problems)
**Focus**: Understand basic DP concepts and state transitions

**Recommended Order**:
1. **[LC 509 - Fibonacci Number](https://leetcode.com/problems/fibonacci-number/)** ([📝 Code](./linear/basic/FibonacciNumber.java)) - Introduction to memoization
2. **[LC 70 - Climbing Stairs](https://leetcode.com/problems/climbing-stairs/)** ([📝 Code](./linear/basic/ClimbingStairs.java)) - Basic state transition
3. **[LC 198 - House Robber](https://leetcode.com/problems/house-robber/)** ([📝 Code](./linear/optimization/HouseRobber.java)) - Constraint handling
4. **[LC 53 - Maximum Subarray](https://leetcode.com/problems/maximum-subarray/)** ([📝 Code](./linear/sequence/MaximumSubarray.java)) - Kadane's algorithm
5. **[LC 121 - Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)** ([📝 Code](./stock_trading/BestTimeToBuyAndSellStock.java)) - Single pass optimization

### 🟡 Intermediate (Pattern-Based Learning)
**Focus**: Master common DP patterns and optimizations

**Core Problems**:
1. **[LC 62 - Unique Paths](https://leetcode.com/problems/unique-paths/)** ([📝 Code](./grid/path_counting/UniquePaths.java)) - 2D DP introduction
2. **[LC 322 - Coin Change](https://leetcode.com/problems/coin-change/)** ([📝 Code](./knapsack/unbounded/CoinChange.java)) - Unbounded knapsack
3. **[LC 300 - Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/)** ([📝 Code](./linear/sequence/LongestIncreasingSubsequence.java)) - Binary search optimization
4. **[LC 1143 - Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/)** ([📝 Code](./string/subsequence/LongestCommonSubsequence.java)) - String DP foundation
5. **[LC 416 - Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/)** ([📝 Code](./knapsack/subset_sum/PartitionEqualSubsetSum.java)) - 0/1 knapsack

### 🔴 Advanced (Complex Patterns & Multi-dimensional DP)
**Focus**: Complex state management and advanced techniques found in `advanced/` directory

**Challenge Problems**:
1. **[LC 72 - Edit Distance](https://leetcode.com/problems/edit-distance/)** ([📝 Code](./string/matching/EditDistance.java)) - String transformation
2. **[LC 312 - Burst Balloons](https://leetcode.com/problems/burst-balloons/)** ([📝 Code](./interval/BurstBalloons.java)) - Interval DP mastery
3. **[LC 10 - Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/)** ([📝 Code](./string/matching/RegularExpressionMatching.java)) - Pattern matching
4. **[LC 887 - Super Egg Drop](https://leetcode.com/problems/super-egg-drop/)** ([📝 Code](./advanced/SuperEggDrop.java)) - Binary search + DP
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


---

## 📋 Reorganization Summary

This collection has been restructured from a simple difficulty-based organization to a comprehensive pattern-based approach:

- **Total Problems**: 100 Java implementations (discovered vs previously claimed 88)
- **Organization**: Pattern-based directories for better learning progression  
- **Structure**: 9 main DP patterns with 15+ specialized subdirectories
- **Advanced Problems**: 41 complex problems consolidated in `advanced/` directory for focused study

*Master these 100 carefully curated problems to become a DP expert. Each problem is organized by pattern for progressive learning, from basic linear DP to advanced multi-dimensional problems.*

---

## 📋 Complete Problem Inventory

This appendix lists all 100 actual Java implementations found in the reorganized structure:

### 📈 Linear DP (16 problems)

#### Basic Linear DP (3 problems)
- ClimbingStairs
- FibonacciNumber  
- NthTribonacciNumber

#### Optimization Linear DP (7 problems)
- DeleteAndEarn
- HouseRobber
- HouseRobberII
- MasseuseLCCI
- MinCostClimbingStairs
- PaintFence
- PaintHouse

#### Sequence Linear DP (6 problems)
- ArithmeticSlices
- LongestIncreasingSubsequence
- LongestStringChain
- MaximumProductSubarray
- MaximumSubarray
- NumberOfLongestIncreasingSubsequence

### 🌐 Grid DP (8 problems)

#### Path Counting (4 problems)
- PascalsTriangle
- PascalsTriangleII
- UniquePaths
- UniquePathsWithObstacles

#### Grid Optimization (4 problems)
- CherryPickupI
- CherryPickupII
- MinimumFallingPathSum
- MinimumPathSum

### 🔤 String DP (14 problems)

#### Subsequence Problems (5 problems)
- CountDifferentPalindromicSubsequences
- DistinctSubsequences
- IsSubsequence
- LongestCommonSubsequence
- LongestPalindromicSubsequence

#### String Matching (5 problems)
- EditDistance
- InterleaveString
- RegularExpressionMatching
- ScrambleString
- WildcardMatching

#### Palindrome Optimization (4 problems)
- MinimumInsertionStepsToMakeStringPalindrome
- PalindromePartitioningII
- PalindromePartitioningIII
- PalindromicSubstrings

### 🎒 Knapsack DP (6 problems)

#### Subset Sum Variants (2 problems)
- PartitionEqualSubsetSum
- TargetSum

#### Unbounded Knapsack (3 problems)
- CoinChange
- CoinChangeII
- CombinationSumIV

#### Other Knapsack (1 problem)
- KnapsackProblem

### 🎮 Game Theory DP (3 problems)
- DivisorGame
- StoneGameII
- StoneGameIII

### 📈 Stock Trading DP (4 problems)
- BestTimeToBuyAndSellStock
- BestTimeToBuyAndSellStockIV
- BestTimeToBuyAndSellStockWithCooldown
- BestTimeToBuyAndSellStockWithTransactionFee

### ⚡ Specialized Patterns (49 problems)

#### Interval DP (3 problems)
- BurstBalloons
- MinimumDifficultyOfAJobSchedule
- StrangePrinter

#### State Machine DP (2 problems)
- DecodeWays
- DecodeWaysII

#### Mathematical DP (3 problems)
- CountingBits
- GetMaximumInGeneratedArray
- MinimumCostForTickets

#### Advanced Patterns (41 problems)
*Complex DP problems combining multiple patterns:*
- ArithmeticSlices
- BestTimeToBuyAndSellStockIV
- ClimbingStairs
- CountAllValidPickupAndDeliveryOptions
- CountNumbersWithUniqueDigits
- CountSquareSubmatricesWithAllOnes
- CountSubmatricesWithAllOnes
- CountVowelsPermutation
- CountWaysToBuildStaircases
- DistinctSubsequences
- DistinctSubsequencesII
- DominoAndTrominoTiling
- JumpGameII
- LongestAlternatingSubsequence
- LongestArithmeticSubsequence
- LongestBitonicSubsequence
- LongestTurbulentSubarray
- MaximalRectangle
- MaximumLengthOfRepeatedSubarray
- MaximumPointsYouCanObtainFromCards
- MaximumProfitInJobScheduling
- MaximumSubarraySumWithOneDeletion
- MaximumSumCircularSubarray
- MaximumSumOfThreeNonOverlappingSubarrays
- MaximumVacationDays
- MinCostClimbingStairs
- MinimumNumberOfRemovalsToMakeMountainArray
- MinimumSwapsToMakeSequencesIncreasing
- NumberOfSubmatricesThatSumToTarget
- NumberOfWaysToStayInSamePlace
- PaintHouseII
- PartitionArrayForMaximumSum
- PathSum
- ProfitableSchemes
- RangeSumQueryImmutable
- RemoveBoxes
- SuperEggDrop
- UniquePaths
- VideoStitching
- WordBreak
- WordBreakII
