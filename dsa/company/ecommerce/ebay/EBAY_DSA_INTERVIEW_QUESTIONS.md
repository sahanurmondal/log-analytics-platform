# eBay DSA Interview Questions (2023-2025)

## 📚 Quick Navigation to eBay Problem Categories

| **Core Data Structures** | **Algorithms** | **Advanced Topics** | **Domain-Specific** |
|--------------------------|----------------|---------------------|----------------------|
| [📊 Arrays](#arrays) | [🔍 Binary Search](#binary-search) | [🎯 Dynamic Programming](#dynamic-programming) | [🛒 E-Commerce](#ecommerce-domain) |
| [🔗 Linked List](#linked-list) | [🔄 Sorting](#sorting) | [📐 Graph Algorithms](#graph-algorithms) | [📦 Inventory](#inventory-management) |
| [📚 Stacks](#stacks) | [🔎 Search](#searching) | [🌳 Tree Algorithms](#tree-algorithms) | [💳 Payment](#payment-processing) |
| [🚶 Queues](#queues) | [🔄 Two Pointers](#two-pointers) | [🗺️ Hashing](#hashing) | [🚚 Logistics](#logistics-shipping) |
| [🌳 Trees](#trees) | [🪟 Sliding Window](#sliding-window) | [📋 Strings](#strings-manipulation) | [⭐ Recommendations](#recommendation-system) |
| [🗺️ Hash Maps](#hash-maps) | [🔗 Union Find](#union-find) | [⏰ Intervals](#intervals-scheduling) | |

---

## Arrays

### Easy Level

#### 1. **Two Sum** 
- **LeetCode**: [#1](https://leetcode.com/problems/two-sum/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: Hash Map, Two Pass
- **Your Code**: [`dsa/arrays/easy/TwoSum.java`](../../../arrays/easy/TwoSum.java)
- **Related to eBay**: Product price comparison, matching buyer-seller budget

```java
// Pseudocode
// Use HashMap to store value -> index mapping
// For each number, check if (target - number) exists in map
// Time: O(n), Space: O(n)
```

---

#### 2. **Best Time to Buy and Sell Stock**
- **LeetCode**: [#121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Frequently (2024-2025)
- **Topic**: Array Traversal, State Machine
- **Your Code**: [`dsa/arrays/easy/BestTimeToBuyAndSellStock.java`](../../../arrays/easy/BestTimeToBuyAndSellStock.java)
- **Related to eBay**: Dynamic pricing, profit optimization for sellers

```java
// Pseudocode
// Track minimum price seen so far
// For each price, calculate profit if sold at current price
// Update maximum profit
// Time: O(n), Space: O(1)
```

---

#### 3. **Move Zeroes**
- **LeetCode**: [#283](https://leetcode.com/problems/move-zeroes/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Two Pointers, Array Manipulation
- **Your Code**: [`dsa/arrays/easy/MoveZeroes.java`](../../../arrays/easy/MoveZeroes.java)
- **Related to eBay**: Sorting items by availability status

```java
// Pseudocode
// Two pointer approach: one for non-zero, one for traverse
// Swap non-zero elements to the front
// Time: O(n), Space: O(1)
```

---

#### 4. **Contains Duplicate**
- **LeetCode**: [#217](https://leetcode.com/problems/contains-duplicate/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Hash Set
- **Your Code**: [`dsa/arrays/easy/ContainsDuplicate.java`](../../../arrays/easy/ContainsDuplicate.java)
- **Related to eBay**: Duplicate listing detection, inventory validation

```java
// Pseudocode
// Use HashSet to track seen elements
// If element already in set, return true
// Time: O(n), Space: O(n)
```

---

#### 5. **Majority Element**
- **LeetCode**: [#169](https://leetcode.com/problems/majority-element/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Array, Bit Manipulation, Divide-Conquer
- **Your Code**: [`dsa/arrays/easy/MajorityElement.java`](../../../arrays/easy/MajorityElement.java)
- **Related to eBay**: Finding most popular product category, dominant seller

```java
// Pseudocode
// Boyer-Moore Voting Algorithm
// Track candidate and count
// If count reaches 0, switch candidate
// Final candidate is majority element
// Time: O(n), Space: O(1)
```

---

### Medium Level

#### 1. **Container With Most Water**
- **LeetCode**: [#11](https://leetcode.com/problems/container-with-most-water/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2024-2025)
- **Topic**: Two Pointers
- **Your Code**: [`dsa/arrays/medium/ContainerWithMostWater.java`](../../../arrays/medium/ContainerWithMostWater.java)
- **Related to eBay**: Warehouse optimization, maximizing storage capacity

```java
// Pseudocode
// Two pointers: left=0, right=n-1
// Calculate area and track max
// Move pointer with smaller height inward
// Time: O(n), Space: O(1)
```

---

#### 2. **Product of Array Except Self**
- **LeetCode**: [#238](https://leetcode.com/problems/product-of-array-except-self/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: Prefix/Suffix, Array Manipulation
- **Your Code**: [`dsa/arrays/medium/ProductOfArrayExceptSelf.java`](../../../arrays/medium/ProductOfArrayExceptSelf.java)
- **Related to eBay**: Price indexing, cross-product calculations

```java
// Pseudocode
// Create two arrays: prefix and suffix products
// For each index, multiply prefix[i-1] * suffix[i+1]
// Time: O(n), Space: O(1) excluding output
```

---

#### 3. **3Sum**
- **LeetCode**: [#15](https://leetcode.com/problems/3sum/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2024-2025)
- **Topic**: Two Pointers, Sorting
- **Your Code**: [`dsa/arrays/medium/ThreeSum.java`](../../../arrays/medium/ThreeSum.java)
- **Related to eBay**: Finding price combinations, bundle deals

```java
// Pseudocode
// Sort array
// For each element, find two sum of remaining elements
// Use two pointers to find pairs
// Time: O(n^2), Space: O(1) excluding output
```

---

#### 4. **Subarray Sum Equals K**
- **LeetCode**: [#560](https://leetcode.com/problems/subarray-sum-equals-k/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Hash Map, Prefix Sum
- **Your Code**: [`dsa/arrays/medium/SubarraySumEqualsK.java`](../../../arrays/medium/SubarraySumEqualsK.java)
- **Related to eBay**: Revenue tracking, sales quota calculations

```java
// Pseudocode
// Use HashMap to store cumulative sum frequencies
// For each element, check if (sum - k) exists in map
// Time: O(n), Space: O(n)
```

---

#### 5. **Rotate Image**
- **LeetCode**: [#48](https://leetcode.com/problems/rotate-image/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Array Manipulation, Matrix
- **Your Code**: [`dsa/arrays/medium/RotateImage.java`](../../../arrays/medium/RotateImage.java)
- **Related to eBay**: Image processing, product image rotation

```java
// Pseudocode
// Transpose matrix (swap along diagonal)
// Reverse each row
// Time: O(n^2), Space: O(1)
```

---

#### 6. **Search in Rotated Sorted Array**
- **LeetCode**: [#33](https://leetcode.com/problems/search-in-rotated-sorted-array/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: Binary Search, Array
- **Your Code**: [`dsa/arrays/medium/SearchRotatedArray.java`](../../../arrays/medium/SearchRotatedArray.java)
- **Related to eBay**: Price range searches in sorted inventory

```java
// Pseudocode
// Binary search with rotation awareness
// Determine which half is sorted
// Check if target is in sorted half
// Time: O(log n), Space: O(1)
```

---

### Hard Level

#### 1. **Trapping Rain Water**
- **LeetCode**: [#42](https://leetcode.com/problems/trapping-rain-water/)
- **Difficulty**: Hard
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: Two Pointers, Dynamic Programming
- **Your Code**: [`dsa/arrays/hard/TrappingRainWater.java`](../../../arrays/hard/TrappingRainWater.java)
- **Related to eBay**: Warehouse storage optimization, space utilization

```java
// Pseudocode
// For each position, calculate water trapped
// Water = min(leftMax, rightMax) - height
// Precompute leftMax and rightMax arrays
// Time: O(n), Space: O(n)
```

---

#### 2. **Sliding Window Maximum**
- **LeetCode**: [#239](https://leetcode.com/problems/sliding-window-maximum/)
- **Difficulty**: Hard
- **Asked at eBay**: ✅ Sometimes (2023-2025)
- **Topic**: Deque, Sliding Window
- **Your Code**: [`dsa/arrays/hard/SlidingWindowMaximum.java`](../../../arrays/hard/SlidingWindowMaximum.java)
- **Related to eBay**: Maximum price in time window, trending products

```java
// Pseudocode
// Use deque to maintain maximum in sliding window
// Store indices in deque, remove outdated indices
// Time: O(n), Space: O(k)
```

---

---

## Linked List

### Easy Level

#### 1. **Reverse Linked List**
- **LeetCode**: [#206](https://leetcode.com/problems/reverse-linked-list/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Linked List, Iteration/Recursion
- **Your Code**: [`dsa/linkedlist/medium/ReverseLinkedList.java`](../../../linkedlist/medium/ReverseLinkedList.java)
- **Related to eBay**: Order reversal, payment history reversal

```java
// Pseudocode
// Three pointers: prev, curr, next
// Reverse the pointer direction iteratively
// Time: O(n), Space: O(1)
```

---

#### 2. **Merge Two Sorted Lists**
- **LeetCode**: [#21](https://leetcode.com/problems/merge-two-sorted-lists/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Linked List Merge
- **Your Code**: [`dsa/linkedlist/easy/MergeTwoSortedLists.java`](../../../linkedlist/easy/MergeTwoSortedLists.java)
- **Related to eBay**: Merging seller inventories, combining order lists

```java
// Pseudocode
// Compare nodes from both lists
// Attach smaller node to result
// Continue until one list exhausted
// Attach remaining list
// Time: O(n+m), Space: O(1)
```

---

### Medium Level

#### 1. **Add Two Numbers**
- **LeetCode**: [#2](https://leetcode.com/problems/add-two-numbers/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: Linked List Arithmetic
- **Your Code**: [`dsa/linkedlist/medium/AddTwoNumbers.java`](../../../linkedlist/medium/AddTwoNumbers.java)
- **Related to eBay**: Large number calculations, order total computation

```java
// Pseudocode
// Traverse both lists, add digits with carry
// Create new node with sum % 10
// Pass carry to next iteration
// Time: O(max(m,n)), Space: O(max(m,n))
```

---

#### 2. **Remove Nth Node From End**
- **LeetCode**: [#19](https://leetcode.com/problems/remove-nth-node-from-end-of-list/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Two Pointers, Linked List
- **Your Code**: [`dsa/linkedlist/medium/RemoveKthNodeFromEnd.java`](../../../linkedlist/medium/RemoveKthNodeFromEnd.java)
- **Related to eBay**: Removing outdated items from listings

```java
// Pseudocode
// Use two pointers with n gap between them
// Fast pointer moves n steps first
// Then both move together until fast reaches end
// Time: O(n), Space: O(1)
```

---

#### 3. **Reorder List**
- **LeetCode**: [#143](https://leetcode.com/problems/reorder-list/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: Linked List Manipulation
- **Your Code**: [`dsa/linkedlist/medium/ReorderList.java`](../../../linkedlist/medium/ReorderList.java)
- **Related to eBay**: Reordering items in recommendations, priority listing

```java
// Pseudocode
// Find middle using fast/slow pointers
// Reverse second half
// Merge two halves alternately
// Time: O(n), Space: O(1)
```

---

---

## Stacks

### Easy Level

#### 1. **Valid Parentheses**
- **LeetCode**: [#20](https://leetcode.com/problems/valid-parentheses/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Stack
- **Your Code**: [`dsa/stacks/easy/ValidParentheses.java`](../../../stacks/easy/ValidParentheses.java)
- **Related to eBay**: Query validation, formatting checks

```java
// Pseudocode
// Use stack to match closing brackets with opening
// For '(' push to stack, for ')' check and pop
// Stack should be empty at end
// Time: O(n), Space: O(n)
```

---

### Medium Level

#### 1. **Evaluate Reverse Polish Notation**
- **LeetCode**: [#150](https://leetcode.com/problems/evaluate-reverse-polish-notation/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: Stack, Expression Evaluation
- **Your Code**: [`dsa/stacks/medium/EvaluateRPN.java`](../../../stacks/medium/EvaluateRPN.java)
- **Related to eBay**: Price calculation expressions, discount formula evaluation

```java
// Pseudocode
// Push numbers onto stack
// For operators, pop two operands and apply
// Push result back
// Final stack top is answer
// Time: O(n), Space: O(n)
```

---

#### 2. **Largest Rectangle in Histogram**
- **LeetCode**: [#84](https://leetcode.com/problems/largest-rectangle-in-histogram/)
- **Difficulty**: Hard
- **Asked at eBay**: ✅ Sometimes (2023-2025)
- **Topic**: Stack, Histogram
- **Your Code**: [`dsa/arrays/hard/LargestRectangleInHistogram.java`](../../../arrays/hard/LargestRectangleInHistogram.java)
- **Related to eBay**: Maximizing warehouse layout, storage optimization

```java
// Pseudocode
// Use monotonic stack of heights
// For each bar, calculate area with it as minimum
// Pop taller bars and calculate their areas
// Time: O(n), Space: O(n)
```

---

---

## Queues

### Easy Level

#### 1. **Number of Recent Calls**
- **LeetCode**: [#933](https://leetcode.com/problems/number-of-recent-calls/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Queue, Sliding Window
- **Your Code**: [`dsa/queues/easy/RecentCounter.java`](../../../queues/easy/RecentCounter.java)
- **Related to eBay**: API request throttling, activity tracking

```java
// Pseudocode
// Use queue to store recent timestamps
// For each request, remove timestamps older than 3000ms
// Time: O(n), Space: O(n)
```

---

### Medium Level

#### 1. **Design a Circular Queue**
- **LeetCode**: [#622](https://leetcode.com/problems/design-circular-queue/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: Queue Implementation
- **Your Code**: [`dsa/queues/medium/CircularQueue.java`](../../../queues/medium/CircularQueue.java)
- **Related to eBay**: Fixed-size order processing buffer

```java
// Pseudocode
// Circular array with head and tail pointers
// Wrap around when reaching end
// Track size to distinguish empty from full
// Time: O(1), Space: O(k)
```

---

---

## Trees

### Easy Level

#### 1. **Maximum Depth of Binary Tree**
- **LeetCode**: [#104](https://leetcode.com/problems/maximum-depth-of-binary-tree/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: DFS, Tree Traversal
- **Your Code**: [`dsa/trees/easy/MaximumDepth.java`](../../../trees/easy/MaximumDepth.java)
- **Related to eBay**: Category hierarchy depth analysis

```java
// Pseudocode
// DFS to traverse all paths
// For each node, return 1 + max(left, right)
// Time: O(n), Space: O(h)
```

---

#### 2. **Invert Binary Tree**
- **LeetCode**: [#226](https://leetcode.com/problems/invert-binary-tree/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Tree Manipulation
- **Your Code**: [`dsa/trees/easy/InvertBinaryTree.java`](../../../trees/easy/InvertBinaryTree.java)
- **Related to eBay**: Hierarchical data transformation

```java
// Pseudocode
// For each node, swap left and right children
// Recursively invert subtrees
// Time: O(n), Space: O(h)
```

---

### Medium Level

#### 1. **Binary Tree Level Order Traversal**
- **LeetCode**: [#102](https://leetcode.com/problems/binary-tree-level-order-traversal/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: BFS, Queue
- **Your Code**: [`dsa/trees/medium/LevelOrderTraversal.java`](../../../trees/medium/LevelOrderTraversal.java)
- **Related to eBay**: Category level analysis, hierarchical reporting

```java
// Pseudocode
// BFS with queue
// Process nodes level by level
// Time: O(n), Space: O(w) where w is max width
```

---

#### 2. **Path Sum**
- **LeetCode**: [#112](https://leetcode.com/problems/path-sum/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: DFS, Backtracking
- **Your Code**: [`dsa/trees/easy/PathSum.java`](../../../trees/easy/PathSum.java)
- **Related to eBay**: Budget path matching, price path finding

```java
// Pseudocode
// DFS from root to leaf
// Subtract node value from target
// Check if any root-to-leaf path equals target
// Time: O(n), Space: O(h)
```

---

#### 3. **Lowest Common Ancestor of BST**
- **LeetCode**: [#235](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: BST, Tree Navigation
- **Your Code**: [`dsa/binarysearchtree/easy/LowestCommonAncestor.java`](../../../binarysearchtree/easy/LowestCommonAncestor.java)
- **Related to eBay**: Category hierarchy common parent, organizational structure

```java
// Pseudocode
// BST property: value at each node
// If both p and q < node, go left
// If both p and q > node, go right
// Otherwise, node is LCA
// Time: O(log n), Space: O(1)
```

---

---

## Hash Maps

### Easy Level

#### 1. **Two Sum**
- Already covered in [Arrays → Easy → Two Sum](#1-two-sum)

---

#### 2. **Valid Anagram**
- **LeetCode**: [#242](https://leetcode.com/problems/valid-anagram/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Hash Map, Character Count
- **Your Code**: [`dsa/hashmaps/easy/ValidAnagram.java`](../../../hashmaps/easy/ValidAnagram.java)
- **Related to eBay**: Product name comparison, listing title validation

```java
// Pseudocode
// Count character frequencies in both strings
// Compare frequency maps
// Time: O(n), Space: O(1) assuming fixed alphabet
```

---

### Medium Level

#### 1. **Group Anagrams**
- **LeetCode**: [#49](https://leetcode.com/problems/group-anagrams/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Hash Map, Sorting
- **Your Code**: [`dsa/hashmaps/medium/GroupAnagrams.java`](../../../hashmaps/medium/GroupAnagrams.java)
- **Related to eBay**: Finding duplicate listings with different names

```java
// Pseudocode
// Sort characters in each string as key
// Group strings with same sorted form
// Time: O(n * k log k), Space: O(n*k)
```

---

#### 2. **LRU Cache**
- **LeetCode**: [#146](https://leetcode.com/problems/lru-cache/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: Hash Map, Doubly Linked List
- **Your Code**: [`dsa/design/medium/LRUCache.java`](../../../design/medium/LRUCache.java)
- **Related to eBay**: Product page caching, seller inventory caching

```java
// Pseudocode
// HashMap for O(1) access
// Doubly linked list for O(1) eviction
// Move accessed items to front
// Evict least recently used (tail)
// Time: O(1), Space: O(capacity)
```

---

---

## Sorting

### Easy Level

#### 1. **Merge Sorted Array**
- **LeetCode**: [#88](https://leetcode.com/problems/merge-sorted-array/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Two Pointers, Merge
- **Your Code**: [`dsa/arrays/easy/MergeSortedArray.java`](../../../arrays/easy/MergeSortedArray.java)
- **Related to eBay**: Combining sorted price lists, merging seller catalogs

```java
// Pseudocode
// Two pointers starting from end
// Compare and place larger element at end of first array
// Work backwards to avoid overwriting
// Time: O(m+n), Space: O(1)
```

---

---

## Binary Search

### Easy Level

#### 1. **Binary Search**
- **LeetCode**: [#704](https://leetcode.com/problems/binary-search/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Binary Search
- **Your Code**: [`dsa/binarysearch/easy/BinarySearch.java`](../../../binarysearch/easy/BinarySearch.java)
- **Related to eBay**: Finding products in sorted catalog

```java
// Pseudocode
// left = 0, right = n-1
// While left <= right: mid = (left+right)/2
// If target equals mid, return mid
// If target > mid, left = mid+1, else right = mid-1
// Time: O(log n), Space: O(1)
```

---

### Medium Level

#### 1. **Search in Rotated Sorted Array**
- Already covered in [Arrays → Medium → Search in Rotated Sorted Array](#6-search-in-rotated-sorted-array)

---

#### 2. **Find First and Last Position of Element in Sorted Array**
- **LeetCode**: [#34](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Binary Search
- **Your Code**: [`dsa/binarysearch/medium/FindFirstLastPosition.java`](../../../binarysearch/medium/FindFirstLastPosition.java)
- **Related to eBay**: Finding price range for products

```java
// Pseudocode
// Two binary searches: one for leftmost, one for rightmost
// For leftmost: move right even if found to explore left
// For rightmost: move left even if found to explore right
// Time: O(log n), Space: O(1)
```

---

---

## Two Pointers

### Easy Level

#### 1. **Valid Palindrome**
- **LeetCode**: [#125](https://leetcode.com/problems/valid-palindrome/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Two Pointers, String
- **Your Code**: [`dsa/twopointers/easy/ValidPalindrome.java`](../../../twopointers/easy/ValidPalindrome.java)
- **Related to eBay**: Product SKU validation

```java
// Pseudocode
// Two pointers: left and right
// Skip non-alphanumeric characters
// Compare characters (case-insensitive)
// Time: O(n), Space: O(1)
```

---

### Medium Level

#### 1. **Container With Most Water**
- Already covered in [Arrays → Medium → Container With Most Water](#1-container-with-most-water)

---

#### 2. **3Sum**
- Already covered in [Arrays → Medium → 3Sum](#3-3sum)

---

---

## Sliding Window

### Medium Level

#### 1. **Longest Substring Without Repeating Characters**
- **LeetCode**: [#3](https://leetcode.com/problems/longest-substring-without-repeating-characters/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: Sliding Window, Hash Map
- **Your Code**: [`dsa/slidingwindow/medium/LongestSubstring.java`](../../../slidingwindow/medium/LongestSubstring.java)
- **Related to eBay**: Analyzing search queries, finding patterns

```java
// Pseudocode
// Sliding window with HashSet for characters in window
// Expand right pointer, add character
// If duplicate found, shrink from left until duplicate gone
// Track maximum window size
// Time: O(n), Space: O(min(m,n)) where m is charset size
```

---

#### 2. **Minimum Window Substring**
- **LeetCode**: [#76](https://leetcode.com/problems/minimum-window-substring/)
- **Difficulty**: Hard
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: Sliding Window, Hash Map
- **Your Code**: [`dsa/slidingwindow/hard/MinimumWindowSubstring.java`](../../../slidingwindow/hard/MinimumWindowSubstring.java)
- **Related to eBay**: Finding optimal order fulfillment window

```java
// Pseudocode
// Two pointers with character frequency maps
// Expand window until all characters covered
// Shrink window from left to minimize
// Track minimum window
// Time: O(n), Space: O(1) for fixed alphabet
```

---

---

## Dynamic Programming

### Easy Level

#### 1. **Climbing Stairs**
- **LeetCode**: [#70](https://leetcode.com/problems/climbing-stairs/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: DP, Recursion
- **Your Code**: [`dsa/dp/easy/ClimbingStairs.java`](../../../dp/easy/ClimbingStairs.java)
- **Related to eBay**: Multi-step checkout flow analysis, warehouse layout paths

```java
// Pseudocode
// dp[i] = dp[i-1] + dp[i-2]
// Base case: dp[1]=1, dp[2]=2
// Time: O(n), Space: O(n) or O(1) with rolling variables
```

---

#### 2. **House Robber**
- **LeetCode**: [#198](https://leetcode.com/problems/house-robber/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: DP, Optimization
- **Your Code**: [`dsa/dp/medium/HouseRobber.java`](../../../dp/medium/HouseRobber.java)
- **Related to eBay**: Maximizing seller selection, inventory picking strategy

```java
// Pseudocode
// dp[i] = max(dp[i-1], dp[i-2] + nums[i])
// Either skip current or take current and add to two-back
// Time: O(n), Space: O(n) or O(1)
```

---

### Medium Level

#### 1. **Longest Increasing Subsequence**
- **LeetCode**: [#300](https://leetcode.com/problems/longest-increasing-subsequence/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: DP, Binary Search
- **Your Code**: [`dsa/dp/medium/LongestIncreasingSubsequence.java`](../../../dp/medium/LongestIncreasingSubsequence.java)
- **Related to eBay**: Tracking product price trends, sales growth analysis

```java
// Pseudocode
// dp[i] = longest increasing subsequence ending at i
// For each i, check all j < i where nums[j] < nums[i]
// dp[i] = max(dp[j]) + 1
// Time: O(n^2), Space: O(n)
```

---

#### 2. **Coin Change**
- **LeetCode**: [#322](https://leetcode.com/problems/coin-change/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: DP, BFS
- **Your Code**: [`dsa/dp/medium/CoinChange.java`](../../../dp/medium/CoinChange.java)
- **Related to eBay**: Minimizing number of refunds, optimal discount combinations

```java
// Pseudocode
// dp[i] = minimum coins to make amount i
// For each amount, try all coins
// dp[amount] = min(dp[amount], dp[amount-coin] + 1)
// Time: O(amount * coins), Space: O(amount)
```

---

---

## Strings Manipulation

### Easy Level

#### 1. **Reverse String**
- **LeetCode**: [#344](https://leetcode.com/problems/reverse-string/)
- **Difficulty**: Easy
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: String, Two Pointers
- **Your Code**: [`dsa/strings/easy/ReverseString.java`](../../../strings/easy/ReverseString.java)
- **Related to eBay**: Log reversal, order history reversal

```java
// Pseudocode
// Two pointers from start and end
// Swap characters and move inward
// Time: O(n), Space: O(1)
```

---

### Medium Level

#### 1. **Longest Palindromic Substring**
- **LeetCode**: [#5](https://leetcode.com/problems/longest-palindromic-substring/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: DP, String, Expand Around Center
- **Your Code**: [`dsa/strings/medium/LongestPalindromicSubstring.java`](../../../strings/medium/LongestPalindromicSubstring.java)
- **Related to eBay**: Finding symmetric patterns in data

```java
// Pseudocode
// Expand around center approach
// For each possible center (odd and even length)
// Expand left and right while characters match
// Time: O(n^2), Space: O(1)
```

---

---

## Graph Algorithms

### Medium Level

#### 1. **Number of Islands**
- **LeetCode**: [#200](https://leetcode.com/problems/number-of-islands/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Frequently (2023-2025)
- **Topic**: DFS/BFS, Graph, Connected Components
- **Your Code**: [`dsa/graphs/medium/NumberOfIslands.java`](../../../graphs/medium/NumberOfIslands.java)
- **Related to eBay**: Warehouse zone clustering, connected facility groups

```java
// Pseudocode
// DFS/BFS for each unvisited land cell
// Mark all connected cells as visited
// Increment island count
// Time: O(m*n), Space: O(m*n)
```

---

#### 2. **Course Schedule**
- **LeetCode**: [#207](https://leetcode.com/problems/course-schedule/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2024-2025)
- **Topic**: Topological Sort, Cycle Detection
- **Your Code**: [`dsa/graphs/medium/CourseSchedule.java`](../../../graphs/medium/CourseSchedule.java)
- **Related to eBay**: Dependency resolution, task ordering in fulfillment

```java
// Pseudocode
// Build adjacency list and in-degree array
// Topological sort using Kahn's algorithm
// If all courses can be taken, no cycle exists
// Time: O(V+E), Space: O(V+E)
```

---

#### 3. **Clone Graph**
- **LeetCode**: [#133](https://leetcode.com/problems/clone-graph/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: DFS/BFS, Graph Cloning
- **Your Code**: [`dsa/graphs/medium/CloneGraph.java`](../../../graphs/medium/CloneGraph.java)
- **Related to eBay**: Replicating network structures, data duplication

```java
// Pseudocode
// DFS/BFS with HashMap to track visited and cloned nodes
// For each node, create clone and recursively clone neighbors
// Time: O(V+E), Space: O(V)
```

---

---

## Union Find

### Medium Level

#### 1. **Number of Islands II**
- **LeetCode**: [#305](https://leetcode.com/problems/number-of-islands-ii/)
- **Difficulty**: Hard
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: Union-Find, Dynamic Connectivity
- **Your Code**: [`dsa/unionfind/hard/NumberOfIslandsII.java`](../../../unionfind/hard/NumberOfIslandsII.java)
- **Related to eBay**: Dynamic warehouse zone merging

```java
// Pseudocode
// Union-Find with path compression and union by rank
// For each new land, union with adjacent land
// Count connected components
// Time: O(k * α(mn)), Space: O(mn)
```

---

---

## Hashing

### Medium Level

#### 1. **Insert Delete GetRandom O(1)**
- **LeetCode**: [#380](https://leetcode.com/problems/insert-delete-getrandom-o1/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Common (2023-2025)
- **Topic**: Hash Map, Array
- **Your Code**: [`dsa/hashmaps/medium/RandomizedSet.java`](../../../hashmaps/medium/RandomizedSet.java)
- **Related to eBay**: Random product selection, inventory randomization

```java
// Pseudocode
// HashMap for O(1) value -> index lookup
// Array for O(1) random access
// On delete, swap with last element and remove
// Time: O(1), Space: O(n)
```

---

---

## Intervals Scheduling

### Medium Level

#### 1. **Interval List Intersections**
- **LeetCode**: [#986](https://leetcode.com/problems/interval-list-intersections/)
- **Difficulty**: Medium
- **Asked at eBay**: ✅ Sometimes (2024-2025)
- **Topic**: Two Pointers, Intervals
- **Your Code**: [`dsa/intervals/medium/IntervalIntersections.java`](../../../intervals/medium/IntervalIntersections.java)
- **Related to eBay**: Shipping window overlaps, availability matching

```java
// Pseudocode
// Two pointers for both lists
// Find overlap: max(start1, start2) to min(end1, end2)
// Advance pointer with smaller end
// Time: O(m+n), Space: O(1)
```

---

---

## E-Commerce Domain

### 1. **Product Search & Recommendations**

#### Design a Product Search System
- **Related Problems**:
  - Autocomplete with Weighted Words ([LeetCode #642](https://leetcode.com/problems/design-search-autocomplete-system/))
  - **Your Code**: [`dsa/design/medium/SearchAutocomplete.java`](../../../design/medium/SearchAutocomplete.java)

```java
// Pseudocode
// Trie for prefix matching
// Priority queue or frequency map for sorting
// Track search history
// Time: O(m * log(n)) where m is results, n is history
```

---

### 2. **Inventory Management**

#### Low Stock Notification System
- **Problem**: Find products with inventory below threshold
- **Approach**: Priority queue or sorted list

```java
// Pseudocode
// Min heap of (stock_level, product_id)
// Query for products below threshold
// Update stock levels dynamically
// Time: O(log n) per update, O(k) to get all below threshold
```

---

### 3. **Price Optimization**

#### Dynamic Pricing Algorithm
- **Problem**: Adjust prices based on demand
- **Approach**: DP, Greedy

```java
// Pseudocode
// Track historical prices and demand
// DP to find optimal price points
// Consider competitive pricing
// Time: O(n^2), Space: O(n)
```

---

### 4. **Order Processing**

#### Order Fulfillment Pipeline
- **Related Problems**:
  - Reorganize String ([LeetCode #767](https://leetcode.com/problems/reorganize-string/))
  - Course Schedule ([LeetCode #207](#2-course-schedule))

```java
// Pseudocode
// Process orders in optimal sequence
// Minimize wait time using scheduling
// Respect dependencies between tasks
```

---

### 5. **Seller Rating System**

#### Find Top K Sellers
- **Related Problems**:
  - Top K Frequent Elements ([LeetCode #347](https://leetcode.com/problems/top-k-frequent-elements/))
  
```java
// Pseudocode
// Min heap of size k
// Track ratings and frequencies
// Use bucket sort or quickselect for efficiency
// Time: O(n log k), Space: O(k)
```

---

### 6. **Fraud Detection**

#### Detect Suspicious Purchase Patterns
- **Related Problems**:
  - Subarray Sum Equals K ([LeetCode #560](#4-subarray-sum-equals-k))
  - Longest Substring Without Repeating ([LeetCode #3](#1-longest-substring-without-repeating-characters))

```java
// Pseudocode
// Pattern matching using sliding window
// Hash maps to detect duplicate behaviors
// Threshold-based alerting
```

---

## Inventory Management

### 1. **Stock Level Management**

```java
// Pseudocode
// Track stock: initial, current, reserved
// Update on sale, return, restock
// Alert when below minimum threshold
```

---

### 2. **Warehouse Capacity**

```java
// Pseudocode
// Multiple warehouse locations
// Optimize storage using bin packing
// Minimize transport between warehouses
// Related: Trapping Rain Water, Container With Most Water
```

---

## Payment Processing

### 1. **Payment Gateway Integration**

```java
// Pseudocode
// Queue for payment requests
// Retry logic with exponential backoff
// Idempotency using request ID
// Related: Queue, LRU Cache, Hash Map
```

---

### 2. **Transaction Settlement**

```java
// Pseudocode
// Batch payments using intervals
// Matching buy/sell transactions (2Sum)
// Reconciliation using hash maps
```

---

## Logistics & Shipping

### 1. **Route Optimization**

```java
// Pseudocode
// Graph problem: finding shortest paths
// Multiple delivery points
// Time-window constraints
// Related: Topological Sort, Dijkstra's
```

---

### 2. **Delivery Time Prediction**

```java
// Pseudocode
// Historical data analysis
// Dynamic programming for pattern matching
// Machine learning features from DSA problems
```

---

## Recommendation System

### 1. **Collaborative Filtering**

```java
// Pseudocode
// Build user-item interaction matrix
// Find similar users using distance metrics
// Recommend items from similar users
// Related: Graph problems, LRU Cache
```

---

### 2. **Content-Based Recommendations**

```java
// Pseudocode
// Extract product features
// Calculate similarity between products
// Rank by similarity and user preference
// Related: Hash Maps, Sorting, DP
```

---

## Key Interview Tips for eBay

1. **Clarify E-Commerce Context**: Ask how the DSA problem applies to eBay's domain
2. **Scalability**: Discuss how solutions scale to millions of products/sellers
3. **Consistency**: Talk about data consistency in distributed systems
4. **Trade-offs**: Always mention space vs time complexity
5. **Optimization**: Can you further optimize after first solution?
6. **Real Examples**: Reference actual eBay features and problems

---

## Resources

- **LeetCode Discuss**: https://leetcode.com/discuss/
- **eBay Engineering Blog**: https://innovation.ebaytech.com/tech-blog/
- **Glassdoor eBay Questions**: Search "eBay" on Glassdoor
- **InterviewBit**: https://www.interviewbit.com/

---

## Summary Statistics

**Total Problems Covered**: 60+
**Easy**: 20+
**Medium**: 30+
**Hard**: 10+
**E-Commerce Specific**: 10+

**Most Frequently Asked**:
1. Two Sum / Subarray Sum Equals K
2. Container With Most Water
3. Product of Array Except Self
4. LRU Cache
5. Binary Tree Level Order Traversal
6. Course Schedule / Topological Sort
7. Longest Substring Without Repeating

**Key Skills**:
- Hash Maps & Sets
- Two Pointers
- Sliding Window
- BFS/DFS
- DP & Memoization
- Binary Search
- Design Patterns

---

**Last Updated**: December 2025
**Hiring Season Covered**: 2023-2025

