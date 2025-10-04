# Google L5/L6 Level Interview Preparation Strategy

## 📁 Local Code Repository Overview
This preparation strategy includes **400+ LeetCode problems** with local implementations organized by topic:

```
📂 src/
├── 📁 arrays/           - Array manipulation, two pointers, sliding window
├── 📁 strings/          - String processing, pattern matching
├── 📁 trees/            - Binary trees, BST, tree traversals
├── 📁 graphs/           - Graph algorithms, DFS, BFS, topological sort
├── 📁 dp/               - Dynamic programming (linear, grid, string, advanced)
├── 📁 backtracking/     - Recursive problem solving
├── 📁 heap/             - Priority queues, heap operations
├── 📁 linkedlist/       - Linked list manipulations
├── 📁 stacks/           - Stack operations, monotonic stacks
├── 📁 design/           - System design problems (LRU, LFU, etc.)
├── 📁 binarysearch/     - Binary search variations
├── 📁 tries/            - Trie data structure, prefix operations
├── 📁 slidingwindow/    - Window-based algorithms
└── 📁 intervals/        - Interval scheduling, merging
```

**🔗 Format**: Each problem links to both LeetCode and local implementation
- **LeetCode Link**: Original problem statement
- **📁 Code**: Local Java implementation with optimizations

## 🔍 Quick Navigation
- [Array & String Problems](#array--string-problems-40-problems) (40 problems)
- [Trees](#trees-30-problems) (30 problems)  
- [Graphs](#graphs-25-problems) (25 problems)
- [Backtracking](#backtracking-20-problems) (20 problems)
- [Dynamic Programming](#dynamic-programming-30-problems) (30 problems)
- [Heaps & Priority Queue](#heaps--priority-queue-15-problems) (15 problems)
- [System Design Problems](#expert-system-design-problems-extended) (50+ problems)
- [Google Favorites](#google-favorite-problems) (20 problems)

## Overview
This comprehensive preparation strategy is designed for Google L5/L6 level positions and equivalent MAANG company interviews. The timeline assumes 3-6 months of dedicated preparation.

## Your Current Profile Assessment
- ✅ **DSA**: Decent knowledge, need practice on medium/hard problems
- ✅ **System Design**: Basic understanding, struggles with interviews
- ❌ **Cloud/DevOps**: Limited hands-on experience
- ✅ **Backend**: Solid Java, Spring Boot, RDBMS experience
- ⚠️ **Behavioral**: Need structured preparation

---

## 1. DSA Preparation (40% of Interview Weight)

### Phase 1: Foundation Strengthening (4-6 weeks)

#### Week 1-2: Core Problem Patterns
```
Daily Target: 3-4 problems (2 medium, 1-2 hard)
Focus Areas:
- Array/String manipulation
- Two pointers technique
- Sliding window
- Hash maps and sets
```

**Must-Do Problems (200 Essential Problems):**

#### Array & String Problems (40 problems)
- [1. Two Sum](https://leetcode.com/problems/two-sum/) | [📁 Code](../arrays/easy/TwoSum.java)
- [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | [📁 Code](../strings/medium/LongestSubstringWithoutRepeatingCharacters.java)
- [11. Container With Most Water](https://leetcode.com/problems/container-with-most-water/) | [📁 Code](../arrays/medium/ContainerWithMostWater.java)
- [15. 3Sum](https://leetcode.com/problems/3sum/) | [📁 Code](../arrays/medium/ThreeSum.java)
- [16. 3Sum Closest](https://leetcode.com/problems/3sum-closest/) | [📁 Code](../arrays/medium/ThreeSumClosest.java)
- [18. 4Sum](https://leetcode.com/problems/4sum/) | [📁 Code](../arrays/medium/FourSum.java)
- [26. Remove Duplicates from Sorted Array](https://leetcode.com/problems/remove-duplicates-from-sorted-array/) | [📁 Code](../arrays/easy/RemoveDuplicatesFromSortedArray.java)
- [27. Remove Element](https://leetcode.com/problems/remove-element/) | [📁 Code](../arrays/easy/RemoveElement.java)
- [42. Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | [📁 Code](../arrays/hard/TrappingRainWater.java)
- [53. Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) | [📁 Code](../dp/linear/sequence/MaximumSubarray.java)
- [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/) | [📁 Code](../intervals/medium/MergeIntervals.java)
- [57. Insert Interval](https://leetcode.com/problems/insert-interval/) | [📁 Code](../intervals/medium/InsertInterval.java)
- [66. Plus One](https://leetcode.com/problems/plus-one/) | [📁 Code](../arrays/easy/PlusOne.java)
- [75. Sort Colors](https://leetcode.com/problems/sort-colors/) | [📁 Code](../sorting/medium/SortColors.java)
- [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/) | [📁 Code](../slidingwindow/hard/MinimumWindowSubstring.java)
- [80. Remove Duplicates from Sorted Array II](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/) | [📁 Code](../arrays/medium/RemoveDuplicatesFromSortedArrayII.java)
- [88. Merge Sorted Array](https://leetcode.com/problems/merge-sorted-array/) | [📁 Code](../arrays/easy/MergeSortedArray.java)
- [121. Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | [📁 Code](../arrays/easy/BestTimeToBuyAndSellStock.java)
- [122. Best Time to Buy and Sell Stock II](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) | [📁 Code](../dp/stock_trading/BestTimeToBuyAndSellStockII.java)
- [128. Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | [📁 Code](../arrays/hard/LongestConsecutiveSequence.java)
- [136. Single Number](https://leetcode.com/problems/single-number/) | [📁 Code](../arrays/easy/SingleNumber.java)
- [152. Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | [📁 Code](../dp/linear/sequence/MaximumProductSubarray.java)
- [167. Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | [📁 Code](../arrays/medium/TwoSumII.java)
- [169. Majority Element](https://leetcode.com/problems/majority-element/) | [📁 Code](../arrays/easy/MajorityElement.java)
- [189. Rotate Array](https://leetcode.com/problems/rotate-array/) | [📁 Code](../arrays/easy/RotateArray.java)
- [209. Minimum Size Subarray Sum](https://leetcode.com/problems/minimum-size-subarray-sum/) | [📁 Code](../slidingwindow/medium/MinimumSizeSubarraySum.java)
- [217. Contains Duplicate](https://leetcode.com/problems/contains-duplicate/) | [📁 Code](../arrays/easy/ContainsDuplicate.java)
- [238. Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/) | [📁 Code](../arrays/medium/ProductOfArrayExceptSelf.java)
- [268. Missing Number](https://leetcode.com/problems/missing-number/) | [📁 Code](../arrays/easy/MissingNumber.java)
- [283. Move Zeroes](https://leetcode.com/problems/move-zeroes/) | [📁 Code](../arrays/easy/MoveZeroes.java)
- [287. Find the Duplicate Number](https://leetcode.com/problems/find-the-duplicate-number/) | [📁 Code](../arrays/medium/FindTheDuplicateNumber.java)
- [309. Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | [📁 Code](../dp/stock_trading/BestTimeToBuyAndSellStockWithCooldown.java)
- [347. Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | [📁 Code](../heap/medium/TopKFrequentElements.java)
- [414. Third Maximum Number](https://leetcode.com/problems/third-maximum-number/) | [📁 Code](../arrays/easy/ThirdMaximumNumber.java)
- [448. Find All Numbers Disappeared in an Array](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/) | [📁 Code](../arrays/easy/FindAllNumbersDisappearedInArray.java)
- [485. Max Consecutive Ones](https://leetcode.com/problems/max-consecutive-ones/) | [📁 Code](../arrays/easy/MaxConsecutiveOnes.java)
- [560. Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/) | [📁 Code](../arrays/medium/SubarraySumEqualsK.java)
- [566. Reshape the Matrix](https://leetcode.com/problems/reshape-the-matrix/) | [📁 Code](../matrix/easy/ReshapeTheMatrix.java)
- [674. Longest Continuous Increasing Subsequence](https://leetcode.com/problems/longest-continuous-increasing-subsequence/) | [📁 Code](../arrays/easy/LongestContinuousIncreasingSubsequence.java)
- [695. Max Area of Island](https://leetcode.com/problems/max-area-of-island/) | [📁 Code](../grid/medium/MaxAreaOfIsland.java)

#### Trees (30 problems)
- [94. Binary Tree Inorder Traversal](https://leetcode.com/problems/binary-tree-inorder-traversal/) | [📁 Code](../trees/easy/BinaryTreeInorderTraversal.java)
- [98. Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) | [📁 Code](../binarysearchtree/ValidateBinarySearchTree.java)
- [100. Same Tree](https://leetcode.com/problems/same-tree/) | [📁 Code](../trees/easy/SameTree.java)
- [101. Symmetric Tree](https://leetcode.com/problems/symmetric-tree/) | [📁 Code](../trees/medium/SymmetricTree.java)
- [102. Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/) | [📁 Code](../trees/medium/BinaryTreeLevelOrderTraversal.java)
- [103. Binary Tree Zigzag Level Order Traversal](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/) | [📁 Code](../trees/medium/BinaryTreeZigzagLevelOrderTraversal.java)
- [104. Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/) | [📁 Code](../trees/medium/FindMaximumDepthOfBinaryTree.java)
- [105. Construct Binary Tree from Preorder and Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) | [📁 Code](../trees/medium/ConstructBinaryTreeFromPreorderAndInorderTraversal.java)
- [108. Convert Sorted Array to Binary Search Tree](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/) | [📁 Code](../binarysearchtree/easy/ConvertSortedArrayToBinarySearchTree.java)
- [110. Balanced Binary Tree](https://leetcode.com/problems/balanced-binary-tree/) | [📁 Code](../trees/easy/BalancedBinaryTree.java)
- [111. Minimum Depth of Binary Tree](https://leetcode.com/problems/minimum-depth-of-binary-tree/) | [📁 Code](../trees/easy/MinimumDepthOfBinaryTree.java)
- [112. Path Sum](https://leetcode.com/problems/path-sum/) | [📁 Code](../trees/easy/PathSum.java)
- [114. Flatten Binary Tree to Linked List](https://leetcode.com/problems/flatten-binary-tree-to-linked-list/) | [📁 Code](../trees/medium/FlattenBinaryTreeToLinkedList.java)
- [116. Populating Next Right Pointers in Each Node](https://leetcode.com/problems/populating-next-right-pointers-in-each-node/) | [📁 Code](../trees/medium/PopulatingNextRightPointersInEachNode.java)
- [124. Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | [📁 Code](../trees/hard/BinaryTreeMaximumPathSum.java)
- [144. Binary Tree Preorder Traversal](https://leetcode.com/problems/binary-tree-preorder-traversal/) | [📁 Code](../trees/easy/BinaryTreePreorderTraversal.java)
- [145. Binary Tree Postorder Traversal](https://leetcode.com/problems/binary-tree-postorder-traversal/) | [📁 Code](../trees/medium/BinaryTreePostorderTraversal.java)
- [173. Binary Search Tree Iterator](https://leetcode.com/problems/binary-search-tree-iterator/) | [📁 Code](../binarysearchtree/medium/BSTIterator.java)
- [199. Binary Tree Right Side View](https://leetcode.com/problems/binary-tree-right-side-view/) | [📁 Code](../trees/medium/BinaryTreeRightSideView.java)
- [226. Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree/) | [📁 Code](../trees/medium/InvertBinaryTree.java)
- [230. Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | [📁 Code](../binarysearchtree/medium/KthSmallestElementInBST.java)
- [235. Lowest Common Ancestor of a Binary Search Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | [📁 Code](../binarysearchtree/medium/LowestCommonAncestorOfBST.java)
- [236. Lowest Common Ancestor of a Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/) | [📁 Code](../trees/medium/FindLCAOfBinaryTree.java)
- [297. Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) | [📁 Code](../trees/hard/SerializeAndDeserializeBinaryTree.java)
- [337. House Robber III](https://leetcode.com/problems/house-robber-iii/) | [📁 Code](../dp/tree/HouseRobberIII.java)
- [437. Path Sum III](https://leetcode.com/problems/path-sum-iii/) | [📁 Code](../trees/medium/PathSumIII.java)
- [543. Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/) | [📁 Code](../trees/easy/DiameterOfBinaryTree.java)
- [572. Subtree of Another Tree](https://leetcode.com/problems/subtree-of-another-tree/) | [📁 Code](../trees/easy/SubtreeOfAnotherTree.java)
- [617. Merge Two Binary Trees](https://leetcode.com/problems/merge-two-binary-trees/) | [📁 Code](../trees/easy/MergeTwoBinaryTrees.java)
- [863. All Nodes Distance K in Binary Tree](https://leetcode.com/problems/all-nodes-distance-k-in-binary-tree/) | [📁 Code](../trees/medium/AllNodesDistanceKInBinaryTree.java)

#### Graphs (25 problems)
- [127. Word Ladder](https://leetcode.com/problems/word-ladder/) | [📁 Code](../graphs/medium/WordLadder.java)
- [130. Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) | [📁 Code](../graphs/medium/SurroundedRegions.java)
- [133. Clone Graph](https://leetcode.com/problems/clone-graph/) | [📁 Code](../graphs/medium/CloneGraph.java)
- [200. Number of Islands](https://leetcode.com/problems/number-of-islands/) | [📁 Code](../graphs/medium/NumberOfIslands.java)
- [207. Course Schedule](https://leetcode.com/problems/course-schedule/) | [📁 Code](../graphs/medium/CourseSchedule.java)
- [210. Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) | [📁 Code](../graphs/medium/CourseScheduleII.java)
- [269. Alien Dictionary](https://leetcode.com/problems/alien-dictionary/) | [📁 Code](../graphs/hard/AlienDictionary.java)
- [310. Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/) | [📁 Code](../graphs/medium/MinimumHeightTrees.java)
- [323. Number of Connected Components in an Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | [📁 Code](../graphs/medium/FindConnectedComponents.java)
- [399. Evaluate Division](https://leetcode.com/problems/evaluate-division/) | [📁 Code](../graphs/medium/EvaluateDivision.java)
- [417. Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow/) | [📁 Code](../graphs/medium/PacificAtlanticWaterFlow.java)
- [490. The Maze](https://leetcode.com/problems/the-maze/) | [📁 Code](../graphs/medium/TheMaze.java)
- [542. 01 Matrix](https://leetcode.com/problems/01-matrix/) | [📁 Code](../grid/medium/ZeroOneMatrix.java)
- [547. Number of Provinces](https://leetcode.com/problems/number-of-provinces/) | [📁 Code](../graphs/medium/NumberOfProvinces.java)
- [684. Redundant Connection](https://leetcode.com/problems/redundant-connection/) | [📁 Code](../graphs/medium/RedundantConnection.java)
- [733. Flood Fill](https://leetcode.com/problems/flood-fill/) | [📁 Code](../grid/medium/FloodFill.java)
- [743. Network Delay Time](https://leetcode.com/problems/network-delay-time/) | [📁 Code](../graphs/medium/NetworkDelayTime.java)
- [785. Is Graph Bipartite?](https://leetcode.com/problems/is-graph-bipartite/) | [📁 Code](../graphs/medium/IsGraphBipartite.java)
- [787. Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | [📁 Code](../graphs/medium/CheapestFlightsWithinKStops.java)
- [841. Keys and Rooms](https://leetcode.com/problems/keys-and-rooms/) | [📁 Code](../graphs/medium/KeysAndRooms.java)
- [994. Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) | [📁 Code](../grid/medium/RottenOranges.java)
- [1091. Shortest Path in Binary Matrix](https://leetcode.com/problems/shortest-path-in-binary-matrix/) | [📁 Code](../grid/hard/ShortestPathInBinaryMatrix.java)
- [1584. Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/) | [📁 Code](../graphs/hard/MinimumCostToConnectAllPoints.java)
- [1631. Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort/) | [📁 Code](../graphs/medium/PathWithMinimumEffort.java)
- [2101. Detonate the Maximum Bombs](https://leetcode.com/problems/detonate-the-maximum-bombs/) | [📁 Code](../graphs/medium/DetonateTheMaximumBombs.java)

#### Backtracking (20 problems)
- [17. Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) | [📁 Code](../backtracking/LetterCombinationsOfPhoneNumber.java)
- [22. Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) | [📁 Code](../backtracking/medium/GenerateParentheses.java)
- [37. Sudoku Solver](https://leetcode.com/problems/sudoku-solver/) | [📁 Code](../backtracking/hard/SudokuSolver.java)
- [39. Combination Sum](https://leetcode.com/problems/combination-sum/) | [📁 Code](../backtracking/CombinationSum.java)
- [40. Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) | [📁 Code](../backtracking/medium/CombinationSumII.java)
- [46. Permutations](https://leetcode.com/problems/permutations/) | [📁 Code](../backtracking/Permutations.java)
- [47. Permutations II](https://leetcode.com/problems/permutations-ii/) | [📁 Code](../backtracking/medium/PermutationsII.java)
- [51. N-Queens](https://leetcode.com/problems/n-queens/) | [📁 Code](../backtracking/NQueens.java)
- [52. N-Queens II](https://leetcode.com/problems/n-queens-ii/) | [📁 Code](../grid/hard/NQueensII.java)
- [77. Combinations](https://leetcode.com/problems/combinations/) | [📁 Code](../backtracking/Combinations.java)
- [78. Subsets](https://leetcode.com/problems/subsets/) | [📁 Code](../backtracking/Subsets.java)
- [79. Word Search](https://leetcode.com/problems/word-search/) | [📁 Code](../backtracking/WordSearch.java)
- [90. Subsets II](https://leetcode.com/problems/subsets-ii/) | [📁 Code](../backtracking/SubsetsII.java)
- [93. Restore IP Addresses](https://leetcode.com/problems/restore-ip-addresses/) | [📁 Code](../backtracking/RestoreIPAddresses.java)
- [131. Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/) | [📁 Code](../backtracking/PalindromePartitioning.java)
- [212. Word Search II](https://leetcode.com/problems/word-search-ii/) | [📁 Code](../grid/hard/WordSearchII.java)
- [216. Combination Sum III](https://leetcode.com/problems/combination-sum-iii/) | [📁 Code](../backtracking/medium/CombinationSumIII.java)
- [526. Beautiful Arrangement](https://leetcode.com/problems/beautiful-arrangement/) | [📁 Code](../backtracking/medium/BeautifulArrangement.java)
- [698. Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) | [📁 Code](../backtracking/medium/PartitionToKEqualSumSubsets.java)
- [1219. Path with Maximum Gold](https://leetcode.com/problems/path-with-maximum-gold/) | [📁 Code](../backtracking/medium/PathWithMaximumGold.java)

#### Dynamic Programming (30 problems)
- [53. Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) | [📁 Code](../dp/linear/sequence/MaximumSubarray.java)
- [62. Unique Paths](https://leetcode.com/problems/unique-paths/) | [📁 Code](../dp/grid/path_counting/UniquePaths.java)
- [63. Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | [📁 Code](../dp/grid/path_counting/UniquePathsWithObstacles.java)
- [64. Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | [📁 Code](../dp/grid/optimization/MinimumPathSum.java)
- [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | [📁 Code](../dp/linear/basic/ClimbingStairs.java)
- [72. Edit Distance](https://leetcode.com/problems/edit-distance/) | [📁 Code](../dp/string/matching/EditDistance.java)
- [91. Decode Ways](https://leetcode.com/problems/decode-ways/) | [📁 Code](../dp/state_machine/DecodeWays.java)
- [115. Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | [📁 Code](../dp/string/subsequence/DistinctSubsequences.java)
- [139. Word Break](https://leetcode.com/problems/word-break/) | [📁 Code](../dp/string/matching/WordBreak.java)
- [152. Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | [📁 Code](../dp/linear/sequence/MaximumProductSubarray.java)
- [198. House Robber](https://leetcode.com/problems/house-robber/) | [📁 Code](../dp/linear/optimization/HouseRobber.java)
- [213. House Robber II](https://leetcode.com/problems/house-robber-ii/) | [📁 Code](../dp/linear/optimization/HouseRobberII.java)
- [221. Maximal Square](https://leetcode.com/problems/maximal-square/) | [📁 Code](../grid/medium/MaximalSquare.java)
- [279. Perfect Squares](https://leetcode.com/problems/perfect-squares/) | [📁 Code](../dp/mathematical/PerfectSquares.java)
- [300. Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) | [📁 Code](../dp/linear/sequence/LongestIncreasingSubsequence.java)
- [322. Coin Change](https://leetcode.com/problems/coin-change/) | [📁 Code](../dp/knapsack/CoinChange.java)
- [329. Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | [📁 Code](../matrix/medium/LongestIncreasingPathInMatrix.java)
- [338. Counting Bits](https://leetcode.com/problems/counting-bits/) | [📁 Code](../dp/mathematical/CountingBits.java)
- [354. Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/) | [📁 Code](../sorting/hard/RussianDollEnvelopes.java)
- [377. Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) | [📁 Code](../dp/knapsack/CombinationSumIV.java)
- [416. Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | [📁 Code](../dp/knapsack/PartitionEqualSubsetSum.java)
- [494. Target Sum](https://leetcode.com/problems/target-sum/) | [📁 Code](../dp/knapsack/TargetSum.java)
- [516. Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) | [📁 Code](../dp/string/subsequence/LongestPalindromicSubsequence.java)
- [518. Coin Change 2](https://leetcode.com/problems/coin-change-2/) | [📁 Code](../dp/knapsack/CoinChangeII.java)
- [647. Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | [📁 Code](../dp/string/palindrome/PalindromicSubstrings.java)
- [673. Number of Longest Increasing Subsequence](https://leetcode.com/problems/number-of-longest-increasing-subsequence/) | [📁 Code](../dp/linear/sequence/NumberOfLongestIncreasingSubsequence.java)
- [746. Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | [📁 Code](../dp/linear/basic/MinCostClimbingStairs.java)
- [983. Minimum Cost For Tickets](https://leetcode.com/problems/minimum-cost-for-tickets/) | [📁 Code](../dp/mathematical/MinimumCostForTickets.java)
- [1143. Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | [📁 Code](../dp/string/subsequence/LongestCommonSubsequence.java)
- [1235. Maximum Profit in Job Scheduling](https://leetcode.com/problems/maximum-profit-in-job-scheduling/) | [📁 Code](../dp/advanced/MaximumProfitInJobScheduling.java)

#### Heaps & Priority Queue (15 problems)
- [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | [📁 Code](../linkedlist/hard/MergeKSortedLists.java)
- [215. Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | [📁 Code](../heap/medium/KthLargestElementInArray.java)
- [253. Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/) | [📁 Code](../intervals/medium/MeetingRoomsII.java)
- [264. Ugly Number II](https://leetcode.com/problems/ugly-number-ii/) | [📁 Code](../heap/medium/UglyNumberII.java)
- [295. Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | [📁 Code](../heap/hard/FindMedianFromDataStream.java)
- [347. Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | [📁 Code](../heap/medium/TopKFrequentElements.java)
- [373. Find K Pairs with Smallest Sums](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/) | [📁 Code](../heap/medium/FindKPairsWithSmallestSums.java)
- [378. Kth Smallest Element in a Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) | [📁 Code](../grid/medium/KthSmallestInSortedMatrix.java)
- [451. Sort Characters By Frequency](https://leetcode.com/problems/sort-characters-by-frequency/) | [📁 Code](../sorting/medium/SortCharactersByFrequency.java)
- [621. Task Scheduler](https://leetcode.com/problems/task-scheduler/) | [📁 Code](../greedy/medium/TaskScheduler.java)
- [692. Top K Frequent Words](https://leetcode.com/problems/top-k-frequent-words/) | [📁 Code](../heap/medium/TopKFrequentWords.java)
- [703. Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) | [📁 Code](../heap/easy/KthLargestElementInStream.java)
- [767. Reorganize String](https://leetcode.com/problems/reorganize-string/) | [📁 Code](../greedy/medium/ReorganizeString.java)
- [973. K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/) | [📁 Code](../sorting/medium/KClosestPointsToOrigin.java)
- [1642. Furthest Building You Can Reach](https://leetcode.com/problems/furthest-building-you-can-reach/) | [📁 Code](../heap/medium/FurthestBuildingYouCanReach.java)

#### Additional Array & String Problems (40 problems)
- [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | [📁 Code](../strings/medium/LongestPalindromicSubstring.java)
- [7. Reverse Integer](https://leetcode.com/problems/reverse-integer/) | [📁 Code](../math/easy/ReverseInteger.java)
- [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/) | [📁 Code](../strings/medium/StringToInteger.java)
- [12. Integer to Roman](https://leetcode.com/problems/integer-to-roman/) | [📁 Code](../strings/medium/IntegerToRoman.java)
- [13. Roman to Integer](https://leetcode.com/problems/roman-to-integer/) | [📁 Code](../strings/easy/RomanToInteger.java)
- [14. Longest Common Prefix](https://leetcode.com/problems/longest-common-prefix/) | [📁 Code](../strings/easy/LongestCommonPrefix.java)
- [19. Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | [📁 Code](../linkedlist/medium/RemoveNthNodeFromEndOfList.java)
- [20. Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | [📁 Code](../stacks/easy/ValidParentheses.java)
- [21. Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | [📁 Code](../linkedlist/easy/MergeTwoSortedLists.java)
- [28. Implement strStr()](https://leetcode.com/problems/implement-strstr/) | [📁 Code](../strings/easy/ImplementStrStr.java)
- [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | [📁 Code](../binarysearch/medium/SearchInRotatedSortedArray.java)
- [34. Find First and Last Position of Element in Sorted Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) | [📁 Code](../binarysearch/medium/FindFirstAndLastPositionOfElementInSortedArray.java)
- [35. Search Insert Position](https://leetcode.com/problems/search-insert-position/) | [📁 Code](../binarysearch/easy/SearchInsertPosition.java)
- [36. Valid Sudoku](https://leetcode.com/problems/valid-sudoku/) | [📁 Code](../grid/medium/ValidSudoku.java)
- [38. Count and Say](https://leetcode.com/problems/count-and-say/) | [📁 Code](../strings/medium/CountAndSay.java)
- [43. Multiply Strings](https://leetcode.com/problems/multiply-strings/) | [📁 Code](../strings/medium/MultiplyStrings.java)
- [49. Group Anagrams](https://leetcode.com/problems/group-anagrams/) | [📁 Code](../strings/medium/GroupAnagrams.java)
- [58. Length of Last Word](https://leetcode.com/problems/length-of-last-word/) | [📁 Code](../strings/easy/LengthOfLastWord.java)
- [67. Add Binary](https://leetcode.com/problems/add-binary/) | [📁 Code](../strings/easy/AddBinary.java)
- [69. Sqrt(x)](https://leetcode.com/problems/sqrtx/) | [📁 Code](../binarysearch/easy/Sqrt.java)
- [125. Valid Palindrome](https://leetcode.com/problems/valid-palindrome/) | [📁 Code](../strings/easy/ValidPalindrome.java)
- [151. Reverse Words in a String](https://leetcode.com/problems/reverse-words-in-a-string/) | [📁 Code](../strings/medium/ReverseWordsInString.java)
- [165. Compare Version Numbers](https://leetcode.com/problems/compare-version-numbers/) | [📁 Code](../strings/medium/CompareVersionNumbers.java)
- [171. Excel Sheet Column Number](https://leetcode.com/problems/excel-sheet-column-number/) | [📁 Code](../math/easy/ExcelSheetColumnNumber.java)
- [179. Largest Number](https://leetcode.com/problems/largest-number/) | [📁 Code](../sorting/medium/LargestNumber.java)
- [202. Happy Number](https://leetcode.com/problems/happy-number/) | [📁 Code](../math/easy/HappyNumber.java)
- [204. Count Primes](https://leetcode.com/problems/count-primes/) | [📁 Code](../math/medium/CountPrimes.java)
- [242. Valid Anagram](https://leetcode.com/problems/valid-anagram/) | [📁 Code](../arrays/easy/ValidAnagram.java)
- [273. Integer to English Words](https://leetcode.com/problems/integer-to-english-words/) | [📁 Code](../strings/hard/IntegerToEnglishWords.java)
- [289. Game of Life](https://leetcode.com/problems/game-of-life/) | [📁 Code](../grid/medium/GameOfLife.java)
- [316. Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters/) | [📁 Code](../stacks/medium/RemoveDuplicateLetters.java)
- [340. Longest Substring with At Most K Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/) | [📁 Code](../slidingwindow/medium/LongestSubstringWithAtMostKDistinctCharacters.java)
- [394. Decode String](https://leetcode.com/problems/decode-string/) | [📁 Code](../stacks/medium/DecodeString.java)
- [415. Add Strings](https://leetcode.com/problems/add-strings/) | [📁 Code](../strings/easy/AddStrings.java)
- [443. String Compression](https://leetcode.com/problems/string-compression/) | [📁 Code](../strings/medium/StringCompression.java)
- [468. Validate IP Address](https://leetcode.com/problems/validate-ip-address/) | [📁 Code](../strings/medium/ValidateIPAddress.java)
- [647. Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | [📁 Code](../dp/string/palindrome/PalindromicSubstrings.java)
- [680. Valid Palindrome II](https://leetcode.com/problems/valid-palindrome-ii/) | [📁 Code](../strings/easy/ValidPalindromeII.java)
- [819. Most Common Word](https://leetcode.com/problems/most-common-word/) | [📁 Code](../strings/easy/MostCommonWord.java)
- [844. Backspace String Compare](https://leetcode.com/problems/backspace-string-compare/) | [📁 Code](../strings/easy/BackspaceStringCompare.java)

#### Linked List Problems (20 problems)
- [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | [📁 Code](../linkedlist/medium/AddTwoNumbers.java)
- [19. Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | [📁 Code](../linkedlist/medium/RemoveNthNodeFromEndOfList.java)
- [21. Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | [📁 Code](../linkedlist/easy/MergeTwoSortedLists.java)
- [24. Swap Nodes in Pairs](https://leetcode.com/problems/swap-nodes-in-pairs/) | [📁 Code](../linkedlist/medium/SwapNodesInPairs.java)
- [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | [📁 Code](../linkedlist/hard/ReverseNodesInKGroup.java)
- [61. Rotate List](https://leetcode.com/problems/rotate-list/) | [📁 Code](../linkedlist/medium/RotateList.java)
- [82. Remove Duplicates from Sorted List II](https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/) | [📁 Code](../linkedlist/medium/RemoveDuplicatesFromSortedListII.java)
- [83. Remove Duplicates from Sorted List](https://leetcode.com/problems/remove-duplicates-from-sorted-list/) | [📁 Code](../linkedlist/easy/RemoveDuplicatesFromSortedList.java)
- [86. Partition List](https://leetcode.com/problems/partition-list/) | [📁 Code](../linkedlist/medium/PartitionList.java)
- [92. Reverse Linked List II](https://leetcode.com/problems/reverse-linked-list-ii/) | [📁 Code](../linkedlist/medium/ReverseLinkedListII.java)
- [138. Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) | [📁 Code](../linkedlist/medium/CopyListWithRandomPointer.java)
- [141. Linked List Cycle](https://leetcode.com/problems/linked-list-cycle/) | [📁 Code](../linkedlist/easy/LinkedListCycle.java)
- [142. Linked List Cycle II](https://leetcode.com/problems/linked-list-cycle-ii/) | [📁 Code](../linkedlist/medium/LinkedListCycleII.java)
- [143. Reorder List](https://leetcode.com/problems/reorder-list/) | [📁 Code](../linkedlist/medium/ReorderList.java)
- [147. Insertion Sort List](https://leetcode.com/problems/insertion-sort-list/) | [📁 Code](../linkedlist/medium/InsertionSortList.java)
- [148. Sort List](https://leetcode.com/problems/sort-list/) | [📁 Code](../sorting/medium/SortList.java)
- [160. Intersection of Two Linked Lists](https://leetcode.com/problems/intersection-of-two-linked-lists/) | [📁 Code](../linkedlist/easy/IntersectionOfTwoLinkedLists.java)
- [203. Remove Linked List Elements](https://leetcode.com/problems/remove-linked-list-elements/) | [📁 Code](../linkedlist/easy/RemoveLinkedListElements.java)
- [206. Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | [📁 Code](../linkedlist/easy/ReverseLinkedList.java)
- [234. Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list/) | [📁 Code](../linkedlist/easy/PalindromeLinkedList.java)

#### Stack & Queue Problems (15 problems)
- [20. Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | [📁 Code](../stacks/easy/ValidParentheses.java)
- [32. Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/) | [📁 Code](../stacks/hard/LongestValidParentheses.java)
- [71. Simplify Path](https://leetcode.com/problems/simplify-path/) | [📁 Code](../stacks/medium/SimplifyPath.java)
- [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | [📁 Code](../stacks/hard/LargestRectangleInHistogram.java)
- [85. Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle/) | [📁 Code](../stacks/hard/MaximalRectangle.java)
- [150. Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | [📁 Code](../stacks/medium/EvaluateReversePolishNotation.java)
- [155. Min Stack](https://leetcode.com/problems/min-stack/) | [📁 Code](../design/easy/MinStack.java)
- [224. Basic Calculator](https://leetcode.com/problems/basic-calculator/) | [📁 Code](../stacks/hard/BasicCalculator.java)
- [227. Basic Calculator II](https://leetcode.com/problems/basic-calculator-ii/) | [📁 Code](../stacks/medium/BasicCalculatorII.java)
- [232. Implement Queue using Stacks](https://leetcode.com/problems/implement-queue-using-stacks/) | [📁 Code](../queues/medium/ImplementQueueUsingStacks.java)
- [239. Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | [📁 Code](../slidingwindow/hard/SlidingWindowMaximum.java)
- [316. Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters/) | [📁 Code](../stacks/medium/RemoveDuplicateLetters.java)
- [394. Decode String](https://leetcode.com/problems/decode-string/) | [📁 Code](../stacks/medium/DecodeString.java)
- [496. Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/) | [📁 Code](../stacks/easy/NextGreaterElementI.java)
- [503. Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/) | [📁 Code](../stacks/medium/NextGreaterElementII.java)

#### Binary Search Problems (15 problems)
- [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | [📁 Code](../binarysearch/medium/SearchInRotatedSortedArray.java)
- [34. Find First and Last Position of Element in Sorted Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) | [📁 Code](../binarysearch/medium/FindFirstAndLastPositionOfElementInSortedArray.java)
- [35. Search Insert Position](https://leetcode.com/problems/search-insert-position/) | [📁 Code](../binarysearch/easy/SearchInsertPosition.java)
- [69. Sqrt(x)](https://leetcode.com/problems/sqrtx/) | [📁 Code](../binarysearch/easy/Sqrt.java)
- [74. Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/) | [📁 Code](../matrix/medium/SearchA2DMatrix.java)
- [81. Search in Rotated Sorted Array II](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/) | [📁 Code](../binarysearch/medium/SearchInRotatedSortedArrayII.java)
- [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | [📁 Code](../binarysearch/medium/FindMinimumInRotatedSortedArray.java)
- [154. Find Minimum in Rotated Sorted Array II](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array-ii/) | [📁 Code](../binarysearch/hard/FindMinimumInRotatedSortedArrayII.java)
- [162. Find Peak Element](https://leetcode.com/problems/find-peak-element/) | [📁 Code](../binarysearch/medium/FindPeakElement.java)
- [278. First Bad Version](https://leetcode.com/problems/first-bad-version/) | [📁 Code](../binarysearch/easy/FirstBadVersion.java)
- [287. Find the Duplicate Number](https://leetcode.com/problems/find-the-duplicate-number/) | [📁 Code](../arrays/medium/FindTheDuplicateNumber.java)
- [367. Valid Perfect Square](https://leetcode.com/problems/valid-perfect-square/) | [📁 Code](../binarysearch/easy/ValidPerfectSquare.java)
- [374. Guess Number Higher or Lower](https://leetcode.com/problems/guess-number-higher-or-lower/) | [📁 Code](../binarysearch/easy/GuessNumberHigherOrLower.java)
- [704. Binary Search](https://leetcode.com/problems/binary-search/) | [📁 Code](../binarysearch/easy/BinarySearch.java)
- [1011. Capacity To Ship Packages Within D Days](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/) | [📁 Code](../binarysearch/medium/CapacityToShipPackagesWithinDDays.java)

#### Advanced Graph Problems (20 problems)
- [269. Alien Dictionary](https://leetcode.com/problems/alien-dictionary/)
- [310. Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/)
- [329. Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/)
- [332. Reconstruct Itinerary](https://leetcode.com/problems/reconstruct-itinerary/)
- [399. Evaluate Division](https://leetcode.com/problems/evaluate-division/)
- [444. Sequence Reconstruction](https://leetcode.com/problems/sequence-reconstruction/)
- [490. The Maze](https://leetcode.com/problems/the-maze/)
- [505. The Maze II](https://leetcode.com/problems/the-maze-ii/)
- [721. Accounts Merge](https://leetcode.com/problems/accounts-merge/)
- [743. Network Delay Time](https://leetcode.com/problems/network-delay-time/)
- [787. Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/)
- [802. Find Eventual Safe States](https://leetcode.com/problems/find-eventual-safe-states/)
- [886. Possible Bipartition](https://leetcode.com/problems/possible-bipartition/)
- [1192. Critical Connections in a Network](https://leetcode.com/problems/critical-connections-in-a-network/)
- [1245. Tree Diameter](https://leetcode.com/problems/tree-diameter/)
- [1334. Find the City With the Smallest Number of Neighbors at a Threshold Distance](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/)
- [1368. Minimum Cost to Make at Least One Valid Path in a Grid](https://leetcode.com/problems/minimum-cost-to-make-at-least-one-valid-path-in-a-grid/)
- [1514. Path with Maximum Probability](https://leetcode.com/problems/path-with-maximum-probability/)
- [1631. Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort/)
- [1697. Checking Existence of Edge Length Limited Paths](https://leetcode.com/problems/checking-existence-of-edge-length-limited-paths/)

#### Advanced Dynamic Programming (25 problems)
- [10. Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/)
- [44. Wildcard Matching](https://leetcode.com/problems/wildcard-matching/)
- [87. Scramble String](https://leetcode.com/problems/scramble-string/)
- [97. Interleaving String](https://leetcode.com/problems/interleaving-string/)
- [123. Best Time to Buy and Sell Stock III](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/)
- [140. Word Break II](https://leetcode.com/problems/word-break-ii/)
- [174. Dungeon Game](https://leetcode.com/problems/dungeon-game/)
- [188. Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/)
- [264. Ugly Number II](https://leetcode.com/problems/ugly-number-ii/)
- [312. Burst Balloons](https://leetcode.com/problems/burst-balloons/)
- [403. Frog Jump](https://leetcode.com/problems/frog-jump/)
- [410. Split Array Largest Sum](https://leetcode.com/problems/split-array-largest-sum/)
- [446. Arithmetic Slices II - Subsequence](https://leetcode.com/problems/arithmetic-slices-ii-subsequence/)
- [464. Can I Win](https://leetcode.com/problems/can-i-win/)
- [474. Ones and Zeroes](https://leetcode.com/problems/ones-and-zeroes/)
- [486. Predict the Winner](https://leetcode.com/problems/predict-the-winner/)
- [514. Freedom Trail](https://leetcode.com/problems/freedom-trail/)
- [576. Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/)
- [688. Knight Probability in Chessboard](https://leetcode.com/problems/knight-probability-in-chessboard/)
- [689. Maximum Sum of 3 Non-Overlapping Subarrays](https://leetcode.com/problems/maximum-sum-of-3-non-overlapping-subarrays/)
- [741. Cherry Pickup](https://leetcode.com/problems/cherry-pickup/)
- [877. Stone Game](https://leetcode.com/problems/stone-game/)
- [887. Super Egg Drop](https://leetcode.com/problems/super-egg-drop/)
- [943. Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/)
- [1140. Stone Game II](https://leetcode.com/problems/stone-game-ii/)

#### Advanced Tree Problems (20 problems)
- [95. Unique Binary Search Trees II](https://leetcode.com/problems/unique-binary-search-trees-ii/)
- [96. Unique Binary Search Trees](https://leetcode.com/problems/unique-binary-search-trees/)
- [99. Recover Binary Search Tree](https://leetcode.com/problems/recover-binary-search-tree/)
- [106. Construct Binary Tree from Inorder and Postorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/)
- [109. Convert Sorted List to Binary Search Tree](https://leetcode.com/problems/convert-sorted-list-to-binary-search-tree/)
- [117. Populating Next Right Pointers in Each Node II](https://leetcode.com/problems/populating-next-right-pointers-in-each-node-ii/)
- [129. Sum Root to Leaf Numbers](https://leetcode.com/problems/sum-root-to-leaf-numbers/)
- [222. Count Complete Tree Nodes](https://leetcode.com/problems/count-complete-tree-nodes/)
- [250. Count Univalue Subtrees](https://leetcode.com/problems/count-univalue-subtrees/)
- [285. Inorder Successor in BST](https://leetcode.com/problems/inorder-successor-in-bst/)
- [298. Binary Tree Longest Consecutive Sequence](https://leetcode.com/problems/binary-tree-longest-consecutive-sequence/)
- [314. Binary Tree Vertical Order Traversal](https://leetcode.com/problems/binary-tree-vertical-order-traversal/)
- [331. Verify Preorder Serialization of a Binary Tree](https://leetcode.com/problems/verify-preorder-serialization-of-a-binary-tree/)
- [426. Convert Binary Search Tree to Sorted Doubly Linked List](https://leetcode.com/problems/convert-binary-search-tree-to-sorted-doubly-linked-list/)
- [449. Serialize and Deserialize BST](https://leetcode.com/problems/serialize-and-deserialize-bst/)
- [450. Delete Node in a BST](https://leetcode.com/problems/delete-node-in-a-bst/)
- [508. Most Frequent Subtree Sum](https://leetcode.com/problems/most-frequent-subtree-sum/)
- [652. Find Duplicate Subtrees](https://leetcode.com/problems/find-duplicate-subtrees/)
- [968. Binary Tree Cameras](https://leetcode.com/problems/binary-tree-cameras/)
- [987. Vertical Order Traversal of a Binary Tree](https://leetcode.com/problems/vertical-order-traversal-of-a-binary-tree/)

#### Advanced Backtracking Problems (15 problems)
- [140. Word Break II](https://leetcode.com/problems/word-break-ii/)
- [254. Factor Combinations](https://leetcode.com/problems/factor-combinations/)
- [267. Palindrome Permutation II](https://leetcode.com/problems/palindrome-permutation-ii/)
- [282. Expression Add Operators](https://leetcode.com/problems/expression-add-operators/)
- [291. Word Pattern II](https://leetcode.com/problems/word-pattern-ii/)
- [301. Remove Invalid Parentheses](https://leetcode.com/problems/remove-invalid-parentheses/)
- [320. Generalized Abbreviation](https://leetcode.com/problems/generalized-abbreviation/)
- [351. Android Unlock Patterns](https://leetcode.com/problems/android-unlock-patterns/)
- [489. Robot Room Cleaner](https://leetcode.com/problems/robot-room-cleaner/)
- [679. 24 Game](https://leetcode.com/problems/24-game/)
- [784. Letter Case Permutation](https://leetcode.com/problems/letter-case-permutation/)
- [996. Number of Squareful Arrays](https://leetcode.com/problems/number-of-squareful-arrays/)
- [1079. Letter Tile Possibilities](https://leetcode.com/problems/letter-tile-possibilities/)
- [1593. Split a String Into the Max Number of Unique Substrings](https://leetcode.com/problems/split-a-string-into-the-max-number-of-unique-substrings/)
- [1681. Minimum Incompatibility](https://leetcode.com/problems/minimum-incompatibility/)

#### Math & Number Theory (10 problems)
- [149. Max Points on a Line](https://leetcode.com/problems/max-points-on-a-line/)
- [166. Fraction to Recurring Decimal](https://leetcode.com/problems/fraction-to-recurring-decimal/)
- [172. Factorial Trailing Zeroes](https://leetcode.com/problems/factorial-trailing-zeroes/)
- [258. Add Digits](https://leetcode.com/problems/add-digits/)
- [263. Ugly Number](https://leetcode.com/problems/ugly-number/)
- [319. Bulb Switcher](https://leetcode.com/problems/bulb-switcher/)
- [326. Power of Three](https://leetcode.com/problems/power-of-three/)
- [365. Water and Jug Problem](https://leetcode.com/problems/water-and-jug-problem/)
- [372. Super Pow](https://leetcode.com/problems/super-pow/)
- [829. Consecutive Numbers Sum](https://leetcode.com/problems/consecutive-numbers-sum/)

#### Google-Specific Advanced Problems (20 problems)
- [224. Basic Calculator](https://leetcode.com/problems/basic-calculator/) | [📁 Code](../stacks/hard/BasicCalculator.java)
- [227. Basic Calculator II](https://leetcode.com/problems/basic-calculator-ii/) | [📁 Code](../stacks/medium/BasicCalculatorII.java)
- [271. Encode and Decode Strings](https://leetcode.com/problems/encode-and-decode-strings/) | [📁 Code](../strings/medium/EncodeAndDecodeStrings.java)
- [273. Integer to English Words](https://leetcode.com/problems/integer-to-english-words/) | [📁 Code](../strings/hard/IntegerToEnglishWords.java)
- [315. Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/) | [📁 Code](../arrays/hard/CountOfSmallerNumbersAfterSelf.java)
- [336. Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs/) | [📁 Code](../tries/hard/PalindromePairs.java)
- [407. Trapping Rain Water II](https://leetcode.com/problems/trapping-rain-water-ii/) | [📁 Code](../grid/hard/TrappingRainWater2D.java)
- [449. Serialize and Deserialize BST](https://leetcode.com/problems/serialize-and-deserialize-bst/) | [📁 Code](../binarysearchtree/medium/SerializeAndDeserializeBST.java)
- [493. Reverse Pairs](https://leetcode.com/problems/reverse-pairs/) | [📁 Code](../arrays/hard/ReversePairs.java)
- [588. Design In-Memory File System](https://leetcode.com/problems/design-in-memory-file-system/) | [📁 Code](../tries/hard/DesignInMemoryFileSystem.java)
- [642. Design Search Autocomplete System](https://leetcode.com/problems/design-search-autocomplete-system/) | [📁 Code](../tries/hard/AutocompleteSystem.java)
- [681. Next Closest Time](https://leetcode.com/problems/next-closest-time/) | [📁 Code](../strings/medium/NextClosestTime.java)
- [686. Repeated String Match](https://leetcode.com/problems/repeated-string-match/) | [📁 Code](../strings/medium/RepeatedStringMatch.java)
- [759. Employee Free Time](https://leetcode.com/problems/employee-free-time/) | [📁 Code](../intervals/hard/EmployeeFreeTime.java)
- [843. Guess the Word](https://leetcode.com/problems/guess-the-word/) | [📁 Code](../miscellaneous/hard/GuessTheWord.java)
- [857. Minimum Cost to Hire K Workers](https://leetcode.com/problems/minimum-cost-to-hire-k-workers/) | [📁 Code](../greedy/hard/MinimumCostToHireKWorkers.java)
- [900. RLE Iterator](https://leetcode.com/problems/rle-iterator/) | [📁 Code](../miscellaneous/medium/RLEIterator.java)
- [939. Minimum Area Rectangle](https://leetcode.com/problems/minimum-area-rectangle/) | [📁 Code](../geometry/medium/MinimumAreaRectangle.java)
- [1007. Minimum Domino Rotations For Equal Row](https://leetcode.com/problems/minimum-domino-rotations-for-equal-row/) | [📁 Code](../arrays/medium/MinimumDominoRotationsForEqualRow.java)
- [1123. Lowest Common Ancestor of Deepest Leaves](https://leetcode.com/problems/lowest-common-ancestor-of-deepest-leaves/) | [📁 Code](../trees/medium/LowestCommonAncestorOfDeepestLeaves.java)

#### Trie & String Matching (10 problems)
- [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) | [📁 Code](../tries/medium/ImplementTrie.java)
- [211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | [📁 Code](../tries/medium/AddAndSearchWord.java)
- [212. Word Search II](https://leetcode.com/problems/word-search-ii/) | [📁 Code](../grid/hard/WordSearchII.java)
- [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/) | [📁 Code](../strings/hard/ShortestPalindrome.java)
- [336. Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs/) | [📁 Code](../tries/hard/PalindromePairs.java)
- [421. Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/) | [📁 Code](../bitmanipulation/medium/MaximumXOROfTwoNumbersInArray.java)
- [472. Concatenated Words](https://leetcode.com/problems/concatenated-words/) | [📁 Code](../tries/hard/ConcatenatedWords.java)
- [588. Design In-Memory File System](https://leetcode.com/problems/design-in-memory-file-system/) | [📁 Code](../tries/hard/DesignInMemoryFileSystem.java)
- [642. Design Search Autocomplete System](https://leetcode.com/problems/design-search-autocomplete-system/) | [📁 Code](../tries/hard/AutocompleteSystem.java)
- [1032. Stream of Characters](https://leetcode.com/problems/stream-of-characters/) | [📁 Code](../tries/medium/StreamOfCharacters.java)

---

## 2. System Design Preparation (30% of Interview Weight)

### Phase 1: Fundamentals (3-4 weeks)

#### Week 1: Core Concepts
```
Daily Study: 2-3 hours
Topics:
- Scalability principles
- CAP theorem
- ACID properties
- Database sharding and replication
- Load balancing strategies
```

**Resources:**
- "Designing Data-Intensive Applications" by Martin Kleppmann
- [High Scalability](http://highscalability.com/)
- [AWS Architecture Center](https://aws.amazon.com/architecture/)
- [Google Cloud Architecture Framework](https://cloud.google.com/architecture)

#### Week 2: System Components
```
Daily Study: 2-3 hours
Topics:
- Microservices architecture
- API Gateway patterns
- Message queues (Kafka, RabbitMQ)
- Caching strategies (Redis, Memcached)
- CDN and geographic distribution
```

#### Week 3: Database Design
```
Daily Study: 2-3 hours
Topics:
- SQL vs NoSQL trade-offs
- Database indexing strategies
- Denormalization techniques
- Consistency models
- Backup and recovery
```

#### Week 4: Distributed Systems
```
Daily Study: 2-3 hours
Topics:
- Consensus algorithms (Raft, Paxos)
- Event sourcing and CQRS
- Distributed transactions
- Circuit breaker pattern
- Rate limiting
```

### Phase 2: Practice Design Problems (4-6 weeks)

#### Core System Design Problems
```
Weekly Target: 2-3 complete designs
Practice Format:
- 45 minutes: Initial design
- 15 minutes: Deep dive questions
- 30 minutes: Review and improvement
```

**Must-Practice Systems:**
1. **URL Shortener (TinyURL)**
   - Focus: Basic system design principles
   - Key concepts: Hashing, database design, caching
   - Related LeetCode: [535. Encode and Decode TinyURL](https://leetcode.com/problems/encode-and-decode-tinyurl/)

2. **Social Media Feed (Twitter/Facebook)**
   - Focus: Timeline generation, fan-out strategies
   - Key concepts: Push vs pull models, caching layers
   - Related LeetCode: [355. Design Twitter](https://leetcode.com/problems/design-twitter/)

3. **Chat System (WhatsApp/Slack)**
   - Focus: Real-time communication, message delivery
   - Key concepts: WebSockets, message queues, online presence

4. **Video Streaming (YouTube/Netflix)**
   - Focus: Content delivery, video processing
   - Key concepts: CDN, video encoding, recommendation systems

5. **Ride Sharing (Uber/Lyft)**
   - Focus: Location services, real-time matching
   - Key concepts: Geospatial indexing, real-time updates

6. **Search Engine (Google Search)**
   - Focus: Large-scale data processing, ranking
   - Key concepts: Crawling, indexing, ranking algorithms
   - Related LeetCode: [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/)

7. **E-commerce Platform (Amazon)**
   - Focus: Inventory management, order processing
   - Key concepts: Distributed transactions, consistency

8. **Cloud Storage (Google Drive/Dropbox)**
   - Focus: File storage, synchronization
   - Key concepts: Metadata management, conflict resolution

### Phase 3: Advanced Topics (2-3 weeks)

#### Google-Specific System Design
```
Focus Areas:
- Large-scale data processing (MapReduce, BigQuery)
- Real-time analytics systems
- Global-scale applications
- Machine learning systems integration
```

**Advanced Problems:**
- Design Google Maps
- Design Google Photos
- Design Gmail
- Design Google AdWords

#### System Design Interview Framework
```
1. Requirements Gathering (5-10 minutes)
   - Functional requirements
   - Non-functional requirements
   - Scale estimation

2. High-Level Design (10-15 minutes)
   - Core components
   - Data flow
   - API design

3. Detailed Design (15-20 minutes)
   - Database schema
   - Microservices breakdown
   - Technology choices

4. Scale and Optimization (10-15 minutes)
   - Bottleneck identification
   - Scaling strategies
   - Monitoring and alerting
```

---

## 3. Tech Stack & Backend Mastery (20% of Interview Weight)

### Phase 1: Java & Spring Boot Excellence (2-3 weeks)

#### Advanced Java Concepts
```
Daily Study: 1-2 hours
Topics:
- JVM internals and garbage collection
- Concurrency and parallel processing
- Memory management optimization
- Performance profiling tools
```

**Hands-On Projects:**
- High-performance REST API with Spring Boot
- Microservices with Spring Cloud
- Reactive programming with Spring WebFlux

#### Spring Boot Mastery
```
Advanced Topics:
- Custom auto-configuration
- Actuator and metrics
- Security implementation
- Testing strategies (unit, integration, contract)
```

### Phase 2: Database & Performance (2-3 weeks)

#### Database Optimization
```
Daily Practice: 1-2 hours
Topics:
- Query optimization techniques
- Index design strategies
- Connection pooling
- Database monitoring
```

**Practical Exercises:**
- Optimize slow queries
- Design database schemas for scale
- Implement database migrations
- Set up replication and sharding

#### Performance Engineering
```
Focus Areas:
- Application performance monitoring
- Memory leak detection
- Load testing strategies
- Caching implementation
```

### Phase 3: Cloud & DevOps Fundamentals (3-4 weeks)

#### Cloud Platforms (Choose one: AWS/GCP)
```
Daily Study: 1-2 hours
Essential Services:
- Compute: EC2/Compute Engine, Lambda/Cloud Functions
- Storage: S3/Cloud Storage, RDS/Cloud SQL
- Networking: VPC, Load Balancers, CDN
- Monitoring: CloudWatch/Stackdriver
```

**Hands-On Labs:**
- Deploy Spring Boot app to cloud
- Set up auto-scaling
- Configure monitoring and alerting
- Implement CI/CD pipeline

#### DevOps Basics
```
Tools to Learn:
- Docker containerization
- Kubernetes orchestration
- CI/CD with Jenkins/GitHub Actions
- Infrastructure as Code (Terraform)
```

**Practical Projects:**
- Containerize your applications
- Set up CI/CD pipeline
- Deploy to Kubernetes cluster
- Implement monitoring and logging

---

## 4. Behavioral & Managerial Preparation (10% of Interview Weight)

### Leadership & Impact Stories

#### STAR Method Framework
```
Situation: Context and background
Task: Your responsibility
Action: What you did (be specific)
Result: Outcome and impact (quantify if possible)
```

#### Required Story Categories

**1. Technical Leadership**
- Led a complex technical project
- Mentored junior developers
- Made architectural decisions
- Resolved technical conflicts

**2. Problem Solving**
- Debugged a critical production issue
- Optimized system performance
- Solved a complex algorithmic problem
- Handled technical debt

**3. Collaboration & Communication**
- Worked with cross-functional teams
- Influenced without authority
- Handled disagreements
- Presented technical concepts to non-technical stakeholders

**4. Innovation & Growth**
- Introduced new technologies
- Improved development processes
- Learned new skills quickly
- Scaled systems or teams

**5. Failure & Learning**
- Project that didn't go as planned
- Technical decision you'd make differently
- Mistake you made and learned from
- Received difficult feedback

### Google-Specific Behavioral Themes

#### Googleyness
- Intellectual humility
- Comfort with ambiguity
- Collaborative nature
- User focus

#### Leadership Principles
- Think big and act small
- Deliver results
- Bias for action
- Learn and be curious

### Mock Interview Practice
```
Weekly Schedule:
- 2 technical phone screens
- 1 system design interview
- 1 behavioral interview
- 1 coding interview with peer
```

---

## 5. Additional Critical Areas

### Algorithm Competition & Problem Solving

#### Competitive Programming
```
Platforms:
- Codeforces (rating target: 1400+)
- AtCoder (rating target: 1200+)
- Google Code Jam (practice rounds)
```

#### Mathematical Foundations
```
Topics:
- Number theory
- Combinatorics
- Probability
- Graph theory
```

### Software Engineering Principles

#### Clean Code & Architecture
```
Study Materials:
- "Clean Code" by Robert Martin
- "Effective Java" by Joshua Bloch
- "Building Microservices" by Sam Newman
```

#### Testing & Quality
```
Practices:
- Test-driven development
- Code coverage analysis
- Performance testing
- Security testing
```

### Domain-Specific Knowledge

#### Google Products & Technologies
```
Research Areas:
- Google's infrastructure (Borg, Spanner, BigTable)
- Machine learning at scale
- Search and ranking algorithms
- Distributed systems papers
```

#### Industry Trends
```
Stay Updated On:
- Cloud-native architectures
- Container orchestration
- Serverless computing
- AI/ML integration
```

---

## Timeline & Milestones

### Month 1-2: Foundation Building
- [ ] Complete DSA fundamentals
- [ ] System design core concepts
- [ ] Java/Spring Boot mastery
- [ ] Basic behavioral stories

### Month 3-4: Skill Development
- [ ] Advanced DSA problems
- [ ] System design practice
- [ ] Cloud/DevOps basics
- [ ] Mock interviews start

### Month 5-6: Interview Preparation
- [ ] Daily coding practice
- [ ] System design mastery
- [ ] Behavioral interview polish
- [ ] Company-specific research

### Final Month: Interview Ready
- [ ] Mock interview simulations
- [ ] Problem-solving speed
- [ ] System design confidence
- [ ] Behavioral story refinement

---

## Daily Schedule Template

### Weekdays (3-4 hours)
```
06:00 - 07:00: DSA practice (1-2 problems)
07:00 - 08:00: System design study
19:00 - 20:00: Tech stack/project work
20:00 - 20:30: Behavioral prep/reading
```

### Weekends (6-8 hours)
```
09:00 - 12:00: Extended DSA practice
13:00 - 15:00: System design problems
15:30 - 17:30: Hands-on projects
18:00 - 19:00: Mock interviews
19:00 - 20:00: Review and planning
```

---

## Resources & Tools

### Books
- ["Cracking the Coding Interview" by Gayle McDowell](https://www.amazon.com/Cracking-Coding-Interview-Programming-Questions/dp/1664154093)
- ["System Design Interview" by Alex Xu](https://www.amazon.com/System-Design-Interview-insiders-Second/dp/B08CMF2CQF)
- ["Designing Data-Intensive Applications" by Martin Kleppmann](https://www.amazon.com/Designing-Data-Intensive-Applications-Reliable-Maintainable/dp/1449373321)
- ["Elements of Programming Interviews" by Adnan Aziz](https://www.amazon.com/Elements-Programming-Interviews-Java-Insiders/dp/1517671272)

### Online Platforms
- [LeetCode Premium](https://leetcode.com/subscribe/)
- [System Design Primer (GitHub)](https://github.com/donnemartin/system-design-primer)
- [Educative.io System Design Courses](https://www.educative.io/courses/grokking-the-system-design-interview)
- [Pramp - Mock Interviews](https://www.pramp.com/)
- [InterviewBit](https://www.interviewbit.com/)

### Google-Specific Resources
- [Google Engineering Practices](https://google.github.io/eng-practices/)
- [Google Research Papers](https://research.google/pubs/)
- [Google Cloud Architecture Center](https://cloud.google.com/architecture/)
- [Google Developers](https://developers.google.com/)

### Practice Partners
- Find study groups or interview buddies
- Join coding communities (Reddit, Discord)
- Participate in virtual study sessions
- Seek mentorship from L5+ engineers

### Tracking Progress
- Maintain a problem-solving journal
- Track system design practice sessions
- Record mock interview feedback
- Monitor improvement metrics

---

## Additional LeetCode Problem Categories

#### Google Favorite Problems
- [4. Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/) | [📁 Code](../arrays/hard/MedianOfTwoSortedArrays.java)
- [10. Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | [📁 Code](../dp/string/matching/RegularExpressionMatching.java)
- [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | [📁 Code](../linkedlist/hard/MergeKSortedLists.java)
- [30. Substring with Concatenation of All Words](https://leetcode.com/problems/substring-with-concatenation-of-all-words/) | [📁 Code](../slidingwindow/hard/SubstringWithConcatenationOfAllWords.java)
- [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/) | [📁 Code](../intervals/medium/MergeIntervals.java)
- [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/) | [📁 Code](../slidingwindow/hard/MinimumWindowSubstring.java)
- [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | [📁 Code](../stacks/hard/LargestRectangleInHistogram.java)
- [128. Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | [📁 Code](../arrays/hard/LongestConsecutiveSequence.java)
- [155. Min Stack](https://leetcode.com/problems/min-stack/) | [📁 Code](../design/easy/MinStack.java)
- [162. Find Peak Element](https://leetcode.com/problems/find-peak-element/) | [📁 Code](../binarysearch/medium/FindPeakElement.java)
- [227. Basic Calculator II](https://leetcode.com/problems/basic-calculator-ii/) | [📁 Code](../stacks/medium/BasicCalculatorII.java)
- [253. Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/) | [📁 Code](../intervals/medium/MeetingRoomsII.java)
- [273. Integer to English Words](https://leetcode.com/problems/integer-to-english-words/) | [📁 Code](../strings/hard/IntegerToEnglishWords.java)
- [340. Longest Substring with At Most K Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/) | [📁 Code](../slidingwindow/medium/LongestSubstringWithAtMostKDistinctCharacters.java)
- [394. Decode String](https://leetcode.com/problems/decode-string/) | [📁 Code](../stacks/medium/DecodeString.java)
- [415. Add Strings](https://leetcode.com/problems/add-strings/) | [📁 Code](../strings/easy/AddStrings.java)
- [443. String Compression](https://leetcode.com/problems/string-compression/) | [📁 Code](../strings/medium/StringCompression.java)
- [588. Design In-Memory File System](https://leetcode.com/problems/design-in-memory-file-system/) | [📁 Code](../tries/hard/DesignInMemoryFileSystem.java)
- [681. Next Closest Time](https://leetcode.com/problems/next-closest-time/) | [📁 Code](../strings/medium/NextClosestTime.java)
- [844. Backspace String Compare](https://leetcode.com/problems/backspace-string-compare/) | [📁 Code](../strings/easy/BackspaceStringCompare.java)

### String Processing & Manipulation
- [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/)
- [6. Zigzag Conversion](https://leetcode.com/problems/zigzag-conversion/)
- [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/)
- [12. Integer to Roman](https://leetcode.com/problems/integer-to-roman/)
- [13. Roman to Integer](https://leetcode.com/problems/roman-to-integer/)
- [14. Longest Common Prefix](https://leetcode.com/problems/longest-common-prefix/)
- [28. Implement strStr()](https://leetcode.com/problems/implement-strstr/)
- [49. Group Anagrams](https://leetcode.com/problems/group-anagrams/)
- [125. Valid Palindrome](https://leetcode.com/problems/valid-palindrome/)
- [151. Reverse Words in a String](https://leetcode.com/problems/reverse-words-in-a-string/)

### Design Problems
**Note: All design problems have been extracted to a separate comprehensive guide:**
- **[Complete Design Problems Collection](./DESIGN_PROBLEMS_COLLECTION.md)** - 65+ problems with detailed categorization

This collection includes:
- Data Structure Design Problems (30 problems)
- Tree & Graph Design Problems (15 problems) 
- Application Design Problems (20 problems)
- Categorized by difficulty: Easy (5), Medium (45), Hard (15)
- Problem-solving approaches and design patterns
- Practice strategy and mock interview preparation

### Expert System Design Problems (Extended)
- [146. LRU Cache](https://leetcode.com/problems/lru-cache/) | [📁 Code](../design/LRUCache.java)
- [155. Min Stack](https://leetcode.com/problems/min-stack/) | [📁 Code](../design/easy/MinStack.java)
- [173. Binary Search Tree Iterator](https://leetcode.com/problems/binary-search-tree-iterator/) | [📁 Code](../binarysearchtree/medium/BSTIterator.java)
- [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) | [📁 Code](../tries/medium/ImplementTrie.java)
- [211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | [📁 Code](../tries/medium/AddAndSearchWord.java)
- [225. Implement Stack using Queues](https://leetcode.com/problems/implement-stack-using-queues/) | [📁 Code](../queues/medium/ImplementStackUsingQueues.java)
- [232. Implement Queue using Stacks](https://leetcode.com/problems/implement-queue-using-stacks/) | [📁 Code](../queues/medium/ImplementQueueUsingStacks.java)
- [284. Peeking Iterator](https://leetcode.com/problems/peeking-iterator/) | [📁 Code](../design/medium/PeekingIterator.java)
- [341. Flatten Nested List Iterator](https://leetcode.com/problems/flatten-nested-list-iterator/) | [📁 Code](../design/medium/FlattenNestedListIterator.java)
- [355. Design Twitter](https://leetcode.com/problems/design-twitter/) | [📁 Code](../design/medium/DesignTwitter.java)
- [380. Insert Delete GetRandom O(1)](https://leetcode.com/problems/insert-delete-getrandom-o1/) | [📁 Code](../design/medium/InsertDeleteGetRandomO1.java)
- [460. LFU Cache](https://leetcode.com/problems/lfu-cache/) | [📁 Code](../design/hard/LFUCache.java)
- [535. Encode and Decode TinyURL](https://leetcode.com/problems/encode-and-decode-tinyurl/) | [📁 Code](../design/medium/EncodeAndDecodeTinyURL.java)
- [588. Design In-Memory File System](https://leetcode.com/problems/design-in-memory-file-system/) | [📁 Code](../tries/hard/DesignInMemoryFileSystem.java)
- [642. Design Search Autocomplete System](https://leetcode.com/problems/design-search-autocomplete-system/) | [📁 Code](../tries/hard/AutocompleteSystem.java)
- [706. Design HashMap](https://leetcode.com/problems/design-hashmap/) | [📁 Code](../design/easy/DesignHashMap.java)
- [707. Design Linked List](https://leetcode.com/problems/design-linked-list/) | [📁 Code](../design/medium/DesignLinkedList.java)
- [716. Max Stack](https://leetcode.com/problems/max-stack/) | [📁 Code](../design/easy/MaxStack.java)
- [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/)
- [211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/)
- [307. Range Sum Query - Mutable](https://leetcode.com/problems/range-sum-query-mutable/)
- [308. Range Sum Query 2D - Mutable](https://leetcode.com/problems/range-sum-query-2d-mutable/)
- [348. Design Tic-Tac-Toe](https://leetcode.com/problems/design-tic-tac-toe/)
- [362. Design Hit Counter](https://leetcode.com/problems/design-hit-counter/)
- [379. Design Phone Directory](https://leetcode.com/problems/design-phone-directory/)
- [381. Insert Delete GetRandom O(1) - Duplicates allowed](https://leetcode.com/problems/insert-delete-getrandom-o1-duplicates-allowed/)
- [432. All O`one Data Structure](https://leetcode.com/problems/all-oone-data-structure/)
- [449. Serialize and Deserialize BST](https://leetcode.com/problems/serialize-and-deserialize-bst/)
- [450. Delete Node in a BST](https://leetcode.com/problems/delete-node-in-a-bst/)
- [497. Random Point in Non-overlapping Rectangles](https://leetcode.com/problems/random-point-in-non-overlapping-rectangles/)
- [528. Random Pick with Weight](https://leetcode.com/problems/random-pick-with-weight/)
- [622. Design Circular Queue](https://leetcode.com/problems/design-circular-queue/)
- [631. Design Excel Sum Formula](https://leetcode.com/problems/design-excel-sum-formula/)
- [635. Design Log Storage System](https://leetcode.com/problems/design-log-storage-system/)
- [641. Design Circular Deque](https://leetcode.com/problems/design-circular-deque/)
- [705. Design HashSet](https://leetcode.com/problems/design-hashset/)
- [715. Range Module](https://leetcode.com/problems/range-module/)
- [729. My Calendar I](https://leetcode.com/problems/my-calendar-i/)
- [731. My Calendar II](https://leetcode.com/problems/my-calendar-ii/)
- [732. My Calendar III](https://leetcode.com/problems/my-calendar-iii/)
- [895. Maximum Frequency Stack](https://leetcode.com/problems/maximum-frequency-stack/)
- [900. RLE Iterator](https://leetcode.com/problems/rle-iterator/)
- [901. Online Stock Span](https://leetcode.com/problems/online-stock-span/)
- [1146. Snapshot Array](https://leetcode.com/problems/snapshot-array/)
- [1166. Design File System](https://leetcode.com/problems/design-file-system/)
- [1206. Design Skiplist](https://leetcode.com/problems/design-skiplist/)
- [1244. Design A Leaderboard](https://leetcode.com/problems/design-a-leaderboard/)
- [1268. Search Suggestions System](https://leetcode.com/problems/search-suggestions-system/)
- [1348. Tweet Counts Per Frequency](https://leetcode.com/problems/tweet-counts-per-frequency/)
- [1352. Product of the Last K Numbers](https://leetcode.com/problems/product-of-the-last-k-numbers/)
- [1381. Design a Stack With Increment Operation](https://leetcode.com/problems/design-a-stack-with-increment-operation/)
- [1472. Design Browser History](https://leetcode.com/problems/design-browser-history/)
- [1500. Design a File Sharing System](https://leetcode.com/problems/design-a-file-sharing-system/)
- [1570. Dot Product of Two Sparse Vectors](https://leetcode.com/problems/dot-product-of-two-sparse-vectors/)
- [1603. Design Parking System](https://leetcode.com/problems/design-parking-system/)
- [1622. Fancy Sequence](https://leetcode.com/problems/fancy-sequence/)
- [1628. Design an Expression Tree With Evaluate Function](https://leetcode.com/problems/design-an-expression-tree-with-evaluate-function/)
- [1656. Design an Ordered Stream](https://leetcode.com/problems/design-an-ordered-stream/)
- [1670. Design Front Middle Back Queue](https://leetcode.com/problems/design-front-middle-back-queue/)
- [1825. Finding MK Average](https://leetcode.com/problems/finding-mk-average/)
- [1912. Design Movie Rental System](https://leetcode.com/problems/design-movie-rental-system/)
- [2013. Detect Squares](https://leetcode.com/problems/detect-squares/)
- [2034. Stock Price Fluctuation](https://leetcode.com/problems/stock-price-fluctuation/)
- [2166. Design Bitset](https://leetcode.com/problems/design-bitset/)
- [2254. Design Video Sharing Platform](https://leetcode.com/problems/design-video-sharing-platform/)
- [2276. Count Integers in Intervals](https://leetcode.com/problems/count-integers-in-intervals/)
- [2286. Booking Concert Tickets in Groups](https://leetcode.com/problems/booking-concert-tickets-in-groups/)
- [2296. Design a Text Editor](https://leetcode.com/problems/design-a-text-editor/)

#### Hard Problems for L6+ Level (Extended)
- [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/)
- [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/)
- [32. Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/)
- [41. First Missing Positive](https://leetcode.com/problems/first-missing-positive/)
- [44. Wildcard Matching](https://leetcode.com/problems/wildcard-matching/)
- [65. Valid Number](https://leetcode.com/problems/valid-number/)
- [72. Edit Distance](https://leetcode.com/problems/edit-distance/)
- [85. Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle/)
- [87. Scramble String](https://leetcode.com/problems/scramble-string/)
- [123. Best Time to Buy and Sell Stock III](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/)
- [188. Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/)
- [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/)
- [282. Expression Add Operators](https://leetcode.com/problems/expression-add-operators/)
- [301. Remove Invalid Parentheses](https://leetcode.com/problems/remove-invalid-parentheses/)
- [312. Burst Balloons](https://leetcode.com/problems/burst-balloons/)
- [329. Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/)
- [407. Trapping Rain Water II](https://leetcode.com/problems/trapping-rain-water-ii/)
- [410. Split Array Largest Sum](https://leetcode.com/problems/split-array-largest-sum/)
- [424. Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/)
- [493. Reverse Pairs](https://leetcode.com/problems/reverse-pairs/)

#### Additional Core Algorithm Problems (50 problems)
- [4. Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/)
- [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/)
- [124. Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/)
- [128. Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/)
- [140. Word Break II](https://leetcode.com/problems/word-break-ii/)
- [149. Max Points on a Line](https://leetcode.com/problems/max-points-on-a-line/)
- [164. Maximum Gap](https://leetcode.com/problems/maximum-gap/)
- [174. Dungeon Game](https://leetcode.com/problems/dungeon-game/)
- [212. Word Search II](https://leetcode.com/problems/word-search-ii/)
- [218. The Skyline Problem](https://leetcode.com/problems/the-skyline-problem/)
- [233. Number of Digit One](https://leetcode.com/problems/number-of-digit-one/)
- [239. Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/)
- [248. Strobogrammatic Number III](https://leetcode.com/problems/strobogrammatic-number-iii/)
- [265. Paint House II](https://leetcode.com/problems/paint-house-ii/)
- [269. Alien Dictionary](https://leetcode.com/problems/alien-dictionary/)
- [297. Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/)
- [315. Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/)
- [321. Create Maximum Number](https://leetcode.com/problems/create-maximum-number/)
- [327. Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/)
- [336. Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs/)
- [352. Data Stream as Disjoint Intervals](https://leetcode.com/problems/data-stream-as-disjoint-intervals/)
- [358. Rearrange String k Distance Apart](https://leetcode.com/problems/rearrange-string-k-distance-apart/)
- [363. Max Sum of Rectangle No Larger Than K](https://leetcode.com/problems/max-sum-of-rectangle-no-larger-than-k/)
- [381. Insert Delete GetRandom O(1) - Duplicates allowed](https://leetcode.com/problems/insert-delete-getrandom-o1-duplicates-allowed/)
- [403. Frog Jump](https://leetcode.com/problems/frog-jump/)
- [411. Minimum Unique Word Abbreviation](https://leetcode.com/problems/minimum-unique-word-abbreviation/)
- [428. Serialize and Deserialize N-ary Tree](https://leetcode.com/problems/serialize-and-deserialize-n-ary-tree/)
- [431. Encode N-ary Tree to Binary Tree](https://leetcode.com/problems/encode-n-ary-tree-to-binary-tree/)
- [440. K-th Smallest in Lexicographical Order](https://leetcode.com/problems/k-th-smallest-in-lexicographical-order/)
- [446. Arithmetic Slices II - Subsequence](https://leetcode.com/problems/arithmetic-slices-ii-subsequence/)
- [465. Optimal Account Balancing](https://leetcode.com/problems/optimal-account-balancing/)
- [472. Concatenated Words](https://leetcode.com/problems/concatenated-words/)
- [480. Sliding Window Median](https://leetcode.com/problems/sliding-window-median/)
- [488. Zuma Game](https://leetcode.com/problems/zuma-game/)
- [502. IPO](https://leetcode.com/problems/ipo/)
- [514. Freedom Trail](https://leetcode.com/problems/freedom-trail/)
- [517. Super Washing Machines](https://leetcode.com/problems/super-washing-machines/)
- [546. Remove Boxes](https://leetcode.com/problems/remove-boxes/)
- [564. Find the Closest Palindrome](https://leetcode.com/problems/find-the-closest-palindrome/)
- [568. Maximum Vacation Days](https://leetcode.com/problems/maximum-vacation-days/)
- [591. Tag Validator](https://leetcode.com/problems/tag-validator/)
- [600. Non-negative Integers without Consecutive Ones](https://leetcode.com/problems/non-negative-integers-without-consecutive-ones/)
- [629. K Inverse Pairs Array](https://leetcode.com/problems/k-inverse-pairs-array/)
- [632. Smallest Range Covering Elements from K Lists](https://leetcode.com/problems/smallest-range-covering-elements-from-k-lists/)
- [664. Strange Printer](https://leetcode.com/problems/strange-printer/)
- [668. Kth Smallest Number in Multiplication Table](https://leetcode.com/problems/kth-smallest-number-in-multiplication-table/)
- [675. Cut Off Trees for Golf Event](https://leetcode.com/problems/cut-off-trees-for-golf-event/)
- [679. 24 Game](https://leetcode.com/problems/24-game/)
- [683. K Empty Slots](https://leetcode.com/problems/k-empty-slots/)
- [685. Redundant Connection II](https://leetcode.com/problems/redundant-connection-ii/)
- [689. Maximum Sum of 3 Non-Overlapping Subarrays](https://leetcode.com/problems/maximum-sum-of-3-non-overlapping-subarrays/)

#### Additional String & Array Problems (40 problems)
- [159. Longest Substring with At Most Two Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/)
- [163. Missing Ranges](https://leetcode.com/problems/missing-ranges/)
- [186. Reverse Words in a String II](https://leetcode.com/problems/reverse-words-in-a-string-ii/)
- [243. Shortest Word Distance](https://leetcode.com/problems/shortest-word-distance/)
- [244. Shortest Word Distance II](https://leetcode.com/problems/shortest-word-distance-ii/)
- [245. Shortest Word Distance III](https://leetcode.com/problems/shortest-word-distance-iii/)
- [246. Strobogrammatic Number](https://leetcode.com/problems/strobogrammatic-number/)
- [247. Strobogrammatic Number II](https://leetcode.com/problems/strobogrammatic-number-ii/)
- [249. Group Shifted Strings](https://leetcode.com/problems/group-shifted-strings/)
- [251. Flatten 2D Vector](https://leetcode.com/problems/flatten-2d-vector/)
- [252. Meeting Rooms](https://leetcode.com/problems/meeting-rooms/)
- [253. Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/)
- [259. 3Sum Smaller](https://leetcode.com/problems/3sum-smaller/)
- [261. Graph Valid Tree](https://leetcode.com/problems/graph-valid-tree/)
- [266. Palindrome Permutation](https://leetcode.com/problems/palindrome-permutation/)
- [270. Closest Binary Search Tree Value](https://leetcode.com/problems/closest-binary-search-tree-value/)
- [271. Encode and Decode Strings](https://leetcode.com/problems/encode-and-decode-strings/)
- [272. Closest Binary Search Tree Value II](https://leetcode.com/problems/closest-binary-search-tree-value-ii/)
- [274. H-Index](https://leetcode.com/problems/h-index/)
- [275. H-Index II](https://leetcode.com/problems/h-index-ii/)
- [276. Paint Fence](https://leetcode.com/problems/paint-fence/)
- [277. Find the Celebrity](https://leetcode.com/problems/find-the-celebrity/)
- [280. Wiggle Sort](https://leetcode.com/problems/wiggle-sort/)
- [281. Zigzag Iterator](https://leetcode.com/problems/zigzag-iterator/)
- [286. Walls and Gates](https://leetcode.com/problems/walls-and-gates/)
- [288. Unique Word Abbreviation](https://leetcode.com/problems/unique-word-abbreviation/)
- [293. Flip Game](https://leetcode.com/problems/flip-game/)
- [294. Flip Game II](https://leetcode.com/problems/flip-game-ii/)
- [296. Best Meeting Point](https://leetcode.com/problems/best-meeting-point/)
- [298. Binary Tree Longest Consecutive Sequence](https://leetcode.com/problems/binary-tree-longest-consecutive-sequence/)
- [302. Smallest Rectangle Enclosing Black Pixels](https://leetcode.com/problems/smallest-rectangle-enclosing-black-pixels/)
- [305. Number of Islands II](https://leetcode.com/problems/number-of-islands-ii/)
- [311. Sparse Matrix Multiplication](https://leetcode.com/problems/sparse-matrix-multiplication/)
- [314. Binary Tree Vertical Order Traversal](https://leetcode.com/problems/binary-tree-vertical-order-traversal/)
- [317. Shortest Distance from All Buildings](https://leetcode.com/problems/shortest-distance-from-all-buildings/)
- [325. Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)
- [339. Nested List Weight Sum](https://leetcode.com/problems/nested-list-weight-sum/)
- [346. Moving Average from Data Stream](https://leetcode.com/problems/moving-average-from-data-stream/)
- [356. Line Reflection](https://leetcode.com/problems/line-reflection/)
- [359. Logger Rate Limiter](https://leetcode.com/problems/logger-rate-limiter/)

#### Advanced Tree & Graph Problems (35 problems)
- [250. Count Univalue Subtrees](https://leetcode.com/problems/count-univalue-subtrees/)
- [285. Inorder Successor in BST](https://leetcode.com/problems/inorder-successor-in-bst/)
- [333. Largest BST Subtree](https://leetcode.com/problems/largest-bst-subtree/)
- [366. Find Leaves of Binary Tree](https://leetcode.com/problems/find-leaves-of-binary-tree/)
- [426. Convert Binary Search Tree to Sorted Doubly Linked List](https://leetcode.com/problems/convert-binary-search-tree-to-sorted-doubly-linked-list/)
- [428. Serialize and Deserialize N-ary Tree](https://leetcode.com/problems/serialize-and-deserialize-n-ary-tree/)
- [431. Encode N-ary Tree to Binary Tree](https://leetcode.com/problems/encode-n-ary-tree-to-binary-tree/)
- [444. Sequence Reconstruction](https://leetcode.com/problems/sequence-reconstruction/)
- [510. Inorder Successor in BST II](https://leetcode.com/problems/inorder-successor-in-bst-ii/)
- [536. Construct Binary Tree from String](https://leetcode.com/problems/construct-binary-tree-from-string/)
- [545. Boundary of Binary Tree](https://leetcode.com/problems/boundary-of-binary-tree/)
- [549. Binary Tree Longest Consecutive Sequence II](https://leetcode.com/problems/binary-tree-longest-consecutive-sequence-ii/)
- [582. Kill Process](https://leetcode.com/problems/kill-process/)
- [834. Sum of Distances in Tree](https://leetcode.com/problems/sum-of-distances-in-tree/)
- [865. Smallest Subtree with all the Deepest Nodes](https://leetcode.com/problems/smallest-subtree-with-all-the-deepest-nodes/)
- [889. Construct Binary Tree from Preorder and Postorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-postorder-traversal/)
- [951. Flip Equivalent Binary Trees](https://leetcode.com/problems/flip-equivalent-binary-trees/)
- [958. Check Completeness of a Binary Tree](https://leetcode.com/problems/check-completeness-of-a-binary-tree/)
- [971. Flip Binary Tree To Match Preorder Traversal](https://leetcode.com/problems/flip-binary-tree-to-match-preorder-traversal/)
- [979. Distribute Coins in Binary Tree](https://leetcode.com/problems/distribute-coins-in-binary-tree/)
- [1104. Path In Zigzag Labelled Binary Tree](https://leetcode.com/problems/path-in-zigzag-labelled-binary-tree/)
- [1110. Delete Nodes And Return Forest](https://leetcode.com/problems/delete-nodes-and-return-forest/)
- [1123. Lowest Common Ancestor of Deepest Leaves](https://leetcode.com/problems/lowest-common-ancestor-of-deepest-leaves/)
- [1130. Minimum Cost Tree From Leaf Values](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/)
- [1145. Binary Tree Coloring Game](https://leetcode.com/problems/binary-tree-coloring-game/)
- [1161. Maximum Level Sum of a Binary Tree](https://leetcode.com/problems/maximum-level-sum-of-a-binary-tree/)
- [1302. Deepest Leaves Sum](https://leetcode.com/problems/deepest-leaves-sum/)
- [1315. Sum of Nodes with Even-Valued Grandparent](https://leetcode.com/problems/sum-of-nodes-with-even-valued-grandparent/)
- [1325. Delete Leaves With a Given Value](https://leetcode.com/problems/delete-leaves-with-a-given-value/)
- [1339. Maximum Product of Splitted Binary Tree](https://leetcode.com/problems/maximum-product-of-splitted-binary-tree/)
- [1367. Linked List in Binary Tree](https://leetcode.com/problems/linked-list-in-binary-tree/)
- [1372. Longest ZigZag Path in a Binary Tree](https://leetcode.com/problems/longest-zigzag-path-in-a-binary-tree/)
- [1448. Count Good Nodes in Binary Tree](https://leetcode.com/problems/count-good-nodes-in-binary-tree/)
- [1457. Pseudo-Palindromic Paths in a Binary Tree](https://leetcode.com/problems/pseudo-palindromic-paths-in-a-binary-tree/)
- [1530. Number of Good Leaf Nodes Pairs](https://leetcode.com/problems/number-of-good-leaf-nodes-pairs/)

#### Advanced Dynamic Programming Problems (40 problems)
- [115. Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/)
- [132. Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/)
- [188. Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/)
- [256. Paint House](https://leetcode.com/problems/paint-house/)
- [265. Paint House II](https://leetcode.com/problems/paint-house-ii/)
- [276. Paint Fence](https://leetcode.com/problems/paint-fence/)
- [303. Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/)
- [304. Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/)
- [309. Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/)
- [343. Integer Break](https://leetcode.com/problems/integer-break/)
- [357. Count Numbers with Unique Digits](https://leetcode.com/problems/count-numbers-with-unique-digits/)
- [368. Largest Divisible Subset](https://leetcode.com/problems/largest-divisible-subset/)
- [413. Arithmetic Slices](https://leetcode.com/problems/arithmetic-slices/)
- [467. Unique Substrings in Wraparound String](https://leetcode.com/problems/unique-substrings-in-wraparound-string/)
- [472. Concatenated Words](https://leetcode.com/problems/concatenated-words/)
- [583. Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/)
- [639. Decode Ways II](https://leetcode.com/problems/decode-ways-ii/)
- [712. Minimum ASCII Delete Sum for Two Strings](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/)
- [714. Best Time to Buy and Sell Stock with Transaction Fee](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/)
- [718. Maximum Length of Repeated Subarray](https://leetcode.com/problems/maximum-length-of-repeated-subarray/)
- [740. Delete and Earn](https://leetcode.com/problems/delete-and-earn/)
- [741. Cherry Pickup](https://leetcode.com/problems/cherry-pickup/)
- [764. Largest Plus Sign](https://leetcode.com/problems/largest-plus-sign/)
- [790. Domino and Tromino Tiling](https://leetcode.com/problems/domino-and-tromino-tiling/)
- [799. Champagne Tower](https://leetcode.com/problems/champagne-tower/)
- [801. Minimum Swaps To Make Sequences Increasing](https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing/)
- [805. Split Array With Same Average](https://leetcode.com/problems/split-array-with-same-average/)
- [813. Largest Sum of Averages](https://leetcode.com/problems/largest-sum-of-averages/)
- [818. Race Car](https://leetcode.com/problems/race-car/)
- [823. Binary Trees With Factors](https://leetcode.com/problems/binary-trees-with-factors/)
- [837. New 21 Game](https://leetcode.com/problems/new-21-game/)
- [879. Profitable Schemes](https://leetcode.com/problems/profitable-schemes/)
- [920. Number of Music Playlists](https://leetcode.com/problems/number-of-music-playlists/)
- [931. Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/)
- [935. Knight Dialer](https://leetcode.com/problems/knight-dialer/)
- [940. Distinct Subsequences II](https://leetcode.com/problems/distinct-subsequences-ii/)
- [956. Tallest Billboard](https://leetcode.com/problems/tallest-billboard/)
- [960. Delete Columns to Make Sorted III](https://leetcode.com/problems/delete-columns-to-make-sorted-iii/)
- [1039. Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/)

#### Advanced Math & Bit Manipulation (25 problems)
- [89. Gray Code](https://leetcode.com/problems/gray-code/)
- [137. Single Number II](https://leetcode.com/problems/single-number-ii/)
- [190. Reverse Bits](https://leetcode.com/problems/reverse-bits/)
- [191. Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/)
- [231. Power of Two](https://leetcode.com/problems/power-of-two/)
- [260. Single Number III](https://leetcode.com/problems/single-number-iii/)
- [268. Missing Number](https://leetcode.com/problems/missing-number/)
- [318. Maximum Product of Word Lengths](https://leetcode.com/problems/maximum-product-of-word-lengths/)
- [342. Power of Four](https://leetcode.com/problems/power-of-four/)
- [371. Sum of Two Integers](https://leetcode.com/problems/sum-of-two-integers/)
- [389. Find the Difference](https://leetcode.com/problems/find-the-difference/)
- [393. UTF-8 Validation](https://leetcode.com/problems/utf-8-validation/)
- [397. Integer Replacement](https://leetcode.com/problems/integer-replacement/)
- [401. Binary Watch](https://leetcode.com/problems/binary-watch/)
- [405. Convert a Number to Hexadecimal](https://leetcode.com/problems/convert-a-number-to-hexadecimal/)
- [421. Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/)
- [461. Hamming Distance](https://leetcode.com/problems/hamming-distance/)
- [476. Number Complement](https://leetcode.com/problems/number-complement/)
- [477. Total Hamming Distance](https://leetcode.com/problems/total-hamming-distance/)
- [693. Binary Number with Alternating Bits](https://leetcode.com/problems/binary-number-with-alternating-bits/)
- [762. Prime Number of Set Bits in Binary Representation](https://leetcode.com/problems/prime-number-of-set-bits-in-binary-representation/)
- [868. Binary Gap](https://leetcode.com/problems/binary-gap/)
- [898. Bitwise ORs of Subarrays](https://leetcode.com/problems/bitwise-ors-of-subarrays/)
- [1009. Complement of Base 10 Integer](https://leetcode.com/problems/complement-of-base-10-integer/)
- [1318. Minimum Flips to Make a OR b Equal to c](https://leetcode.com/problems/minimum-flips-to-make-a-or-b-equal-to-c/)

---

## Updated Weekly Problem Distribution Plan

### Month 1: Foundation Building (Weeks 1-4, 100 problems)
- **Week 1**: Arrays & Basic Strings (25 problems)
- **Week 2**: Linked Lists & Stack/Queue (25 problems)  
- **Week 3**: Binary Search & Two Pointers (25 problems)
- **Week 4**: Basic Trees & Graphs (25 problems)

### Month 2: Intermediate Mastery (Weeks 5-8, 100 problems)
- **Week 5**: Advanced Array/String + Sliding Window (25 problems)
- **Week 6**: Tree Algorithms & BST (25 problems)
- **Week 7**: Graph Algorithms (DFS/BFS/Topological) (25 problems)
- **Week 8**: Basic Dynamic Programming (25 problems)

### Month 3: Advanced Topics (Weeks 9-12, 100 problems)
- **Week 9**: Advanced Dynamic Programming (25 problems)
- **Week 10**: Backtracking & Recursion (25 problems)
- **Week 11**: Heaps & Priority Queues (25 problems)
- **Week 12**: Math & Bit Manipulation (25 problems)

### Month 4: Expert Level (Weeks 13-16, 100 problems)
- **Week 13**: Design Problems (25 problems)
- **Week 14**: Advanced Tree & Graph Problems (25 problems)
- **Week 15**: Core Algorithm Challenges (25 problems)
- **Week 16**: Google-Specific Hard Problems (25 problems)

### Month 5: Advanced Design & Complex Problems (Weeks 17-20, 100 problems)
- **Week 17**: Advanced Design Problems (25 problems)
- **Week 18**: Complex String & Array Problems (25 problems)
- **Week 19**: Advanced Tree Algorithms (25 problems)
- **Week 20**: Advanced DP & Optimization (25 problems)

### Month 6: Interview Simulation & Mastery (Weeks 21-24, 100 problems)
- **Week 21**: Mixed Hard Problems (25 problems)
- **Week 22**: System Design Integration (25 problems)
- **Week 23**: Mock Interview Problems (25 problems)
- **Week 24**: Company-Specific Preparation (25 problems)

## Total Problem Count: 600 Must-Do Problems

This comprehensive list now includes **600 essential problems** covering all major algorithmic patterns, data structures, and design challenges commonly tested in Google L5/L6 interviews. The problems are strategically distributed across 6 months to ensure thorough preparation and mastery.

---

## Problem Solving Strategy

### Daily Practice Routine
1. **Warm-up (10 min)**: Review previous day's problems
2. **New Problem (60 min)**: Solve 3-4 new problems
3. **Review (20 min)**: Analyze time/space complexity
4. **Implementation (20 min)**: Code clean solution
5. **Testing (15 min)**: Edge cases and validation

### Problem Analysis Framework
1. **Understand**: Read problem 2-3 times, clarify constraints
2. **Examples**: Work through examples manually
3. **Approach**: Identify pattern and algorithm
4. **Implement**: Write clean, optimized code
5. **Test**: Verify with edge cases
6. **Optimize**: Improve time/space complexity

### Advanced Problem Categories Mastery

#### Design Pattern Problems
- Focus on scalable, maintainable solutions
- Consider real-world system constraints
- Practice explaining design decisions
- Implement clean, readable code

#### Algorithm Optimization
- Master time/space complexity analysis
- Practice multiple solution approaches
- Focus on edge case handling
- Understand when to use specific algorithms

#### System Integration
- Connect algorithmic solutions to system design
- Consider distributed system implications
- Practice explaining scalability trade-offs
- Understand performance bottlenecks

---

Good luck with your preparation! 🚀
