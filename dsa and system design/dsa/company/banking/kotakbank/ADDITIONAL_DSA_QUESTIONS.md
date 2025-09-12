# Kotak Bank SDE Interview - Additional DSA Questions

This document contains additional Data Structures and Algorithms questions commonly asked in Kotak Mahindra Bank software engineer interviews (SDE1, SDE2, SDE3), compiled from various interview experiences and banking industry standards.

## 📊 Questions with Existing Implementation in Codebase

### Array Problems
1. **Two Sum** - [✅ IMPLEMENTED] - [`src/arrays/easy/TwoSum.java`](../../../arrays/easy/TwoSum.java)
   - Find indices of two numbers that add to target
   - Banking context: Find matching debit/credit amounts
   - Variants: Multiple pairs, all pairs, sorted array

2. **Three Sum** - [✅ IMPLEMENTED] - [`src/arrays/medium/ThreeSum.java`](../../../arrays/medium/ThreeSum.java)
   - Find all triplets that sum to zero
   - Banking context: Multi-party transaction validation

3. **Container With Most Water** - [✅ IMPLEMENTED] - [`src/arrays/medium/ContainerWithMostWater.java`](../../../arrays/medium/ContainerWithMostWater.java)
   - Find maximum water container area
   - Banking context: Optimal cash storage capacity

### String Problems
4. **Valid Parentheses** - [✅ IMPLEMENTED] - [`src/stacks/easy/ValidParentheses.java`](../../../stacks/easy/ValidParentheses.java)
   - Check balanced brackets
   - Banking context: Transaction validation syntax

5. **Longest Common Subsequence** - [✅ IMPLEMENTED] - [`src/dp/string/subsequence/LongestCommonSubsequence.java`](../../../dp/string/subsequence/LongestCommonSubsequence.java)
   - Find LCS between two strings
   - Banking context: Customer data matching/deduplication

### Tree Problems
6. **Binary Tree Level Order Traversal** - [✅ IMPLEMENTED] - [`src/trees/medium/BinaryTreeLevelOrderTraversal.java`](../../../trees/medium/BinaryTreeLevelOrderTraversal.java)
   - BFS traversal of binary tree
   - Banking context: Hierarchical account structure processing

7. **Validate Binary Search Tree** - [✅ IMPLEMENTED] - [`src/binarysearchtree/medium/ValidateBinarySearchTree.java`](../../../binarysearchtree/medium/ValidateBinarySearchTree.java)
   - Check if tree is valid BST
   - Banking context: Account hierarchy validation

### Graph Problems
8. **Number of Islands** - [✅ IMPLEMENTED] - [`src/graphs/medium/NumberOfIslands.java`](../../../graphs/medium/NumberOfIslands.java)
   - Connected components using DFS/BFS
   - Banking context: Fraud network detection

### Dynamic Programming
9. **Coin Change** - [✅ IMPLEMENTED] - [`src/dp/medium/CoinChange.java`](../../../dp/medium/CoinChange.java)
   - Minimum coins for amount
   - Banking context: ATM cash dispensing optimization

10. **House Robber** - [✅ IMPLEMENTED] - [`src/dp/easy/HouseRobber.java`](../../../dp/easy/HouseRobber.java)
    - Maximum sum without adjacent elements
    - Banking context: Loan portfolio optimization

### Linked List Problems
11. **Reverse Linked List** - [✅ IMPLEMENTED] - [`src/linkedlist/easy/ReverseLinkedList.java`](../../../linkedlist/easy/ReverseLinkedList.java)
    - Reverse a singly linked list
    - Banking context: Transaction history reversal

12. **Merge Two Sorted Lists** - [✅ IMPLEMENTED] - [`src/linkedlist/easy/MergeTwoSortedLists.java`](../../../linkedlist/easy/MergeTwoSortedLists.java)
    - Merge two sorted linked lists
    - Banking context: Merging sorted transaction logs

### Hash Map/Hash Table Problems
13. **Group Anagrams** - [✅ IMPLEMENTED] - [`src/hashmaps/medium/GroupAnagrams.java`](../../../hashmaps/medium/GroupAnagrams.java)
    - Group strings that are anagrams
    - Banking context: Customer name clustering/deduplication

### Bit Manipulation
47. **Single Number** - [✅ IMPLEMENTED] - [`src/bitmanipulation/easy/SingleNumber.java`](../../../bitmanipulation/easy/SingleNumber.java)
    - XOR to find unique element
    - Banking context: Find unmatched transaction

## 🔍 Additional Questions (Missing Implementation)

### Array Problems
14. **Maximum Subarray (Kadane's Algorithm)** - [✅ IMPLEMENTED] - [`src/arrays/medium/MaximumSubarray.java`](../../../arrays/medium/MaximumSubarray.java)
    - Find contiguous subarray with maximum sum
    - Banking context: Best performing investment period
    - **Priority: HIGH** - Fundamental algorithm

15. **Merge Intervals** - [✅ IMPLEMENTED] - [`src/intervals/MergeIntervals.java`](../../../intervals/MergeIntervals.java)
    - Merge overlapping intervals
    - Banking context: Transaction time window consolidation
    - **Priority: HIGH** - Common in banking systems

16. **Rotate Array** - [✅ IMPLEMENTED] - [`src/arrays/easy/RotateArray.java`](../../../arrays/easy/RotateArray.java)
    - Rotate array by k positions
    - Banking context: Circular buffer for transaction logs
    - **Priority: MEDIUM**

17. **Product of Array Except Self** - [✅ IMPLEMENTED] - [`src/arrays/medium/ProductOfArrayExceptSelf.java`](../../../arrays/medium/ProductOfArrayExceptSelf.java)
    - Calculate product excluding current element
    - Banking context: Portfolio risk calculation excluding one asset
    - **Priority: MEDIUM**

18. **Find Minimum in Rotated Sorted Array** - [✅ IMPLEMENTED] - [`src/arrays/medium/FindMinimumInRotatedSortedArray.java`](../../../arrays/medium/FindMinimumInRotatedSortedArray.java)
    - Binary search in rotated array
    - Banking context: Find lowest rate in historical data
    - **Priority: MEDIUM**

### String Problems
19. **Longest Substring Without Repeating Characters** - [✅ IMPLEMENTED] - [`src/strings/medium/LongestSubstringWithoutRepeatingCharacters.java`](../../../strings/medium/LongestSubstringWithoutRepeatingCharacters.java)
    - Sliding window technique
    - Banking context: Unique transaction ID validation
    - **Priority: HIGH** - Sliding window is crucial

20. **String to Integer (atoi)** - [✅ IMPLEMENTED] - [`src/strings/medium/StringToInteger.java`](../../../strings/medium/StringToInteger.java)
    - Convert string to integer with validation
    - Banking context: Amount parsing from text input
    - **Priority: HIGH** - Input validation critical

21. **Valid Anagram** - [✅ IMPLEMENTED] - [`src/arrays/easy/ValidAnagram.java`](../../../arrays/easy/ValidAnagram.java)
    - Check if two strings are anagrams
    - Banking context: Account number validation variants
    - **Priority: MEDIUM**

22. **Palindromic Substrings** - [✅ IMPLEMENTED] - [`src/dp/string/palindrome/PalindromicSubstrings.java`](../../../dp/string/palindrome/PalindromicSubstrings.java)
    - Count palindromic substrings
    - Banking context: Symmetric transaction pattern detection
    - **Priority: LOW**

### Linked List Problems
23. **Detect Cycle in Linked List** - [✅ IMPLEMENTED] - [`src/linkedlist/medium/LinkedListCycle.java`](../../../linkedlist/medium/LinkedListCycle.java)
    - Floyd's cycle detection algorithm
    - Banking context: Circular reference detection in accounts
    - **Priority: HIGH** - Fundamental algorithm

24. **Remove Nth Node From End** - [✅ IMPLEMENTED] - [`src/linkedlist/medium/RemoveNthNodeFromEndOfList.java`](../../../linkedlist/medium/RemoveNthNodeFromEndOfList.java)
    - Two-pointer technique
    - Banking context: Remove old transactions from history
    - **Priority: MEDIUM**

25. **Intersection of Two Linked Lists** - [✅ IMPLEMENTED] - [`src/linkedlist/medium/IntersectionOfTwoLinkedLists.java`](../../../linkedlist/medium/IntersectionOfTwoLinkedLists.java)
    - Find common node in two lists
    - Banking context: Common transaction processing point
    - **Priority: MEDIUM**

### Stack Problems
26. **Min Stack** - [✅ IMPLEMENTED] - [`src/design/easy/MinStack.java`](../../../design/easy/MinStack.java)
    - Stack with O(1) min operation
    - Banking context: Track minimum balance efficiently
    - **Priority: HIGH** - Data structure design

27. **Daily Temperatures** - [✅ IMPLEMENTED] - [`src/stacks/medium/DailyTemperatures.java`](../../../stacks/medium/DailyTemperatures.java)
    - Monotonic stack application
    - Banking context: Next higher interest rate day
    - **Priority: MEDIUM**

28. **Evaluate Reverse Polish Notation** - [✅ IMPLEMENTED] - [`src/stacks/medium/EvaluateReversePolishNotation.java`](../../../stacks/medium/EvaluateReversePolishNotation.java)
    - Stack-based expression evaluation
    - Banking context: Interest calculation expression parsing
    - **Priority: MEDIUM**

### Queue Problems
29. **Sliding Window Maximum** - [✅ IMPLEMENTED] - [`src/heap/hard/SlidingWindowMaximum.java`](../../../heap/hard/SlidingWindowMaximum.java)
    - Deque-based sliding window
    - Banking context: Maximum transaction amount in time window
    - **Priority: HIGH** - Common in real-time systems

30. **Design Circular Queue** - [✅ IMPLEMENTED] - [`src/design/medium/MyCircularQueue.java`](../../../design/medium/MyCircularQueue.java)
    - Implement circular queue
    - Banking context: Fixed-size transaction buffer
    - **Priority: MEDIUM**

### Tree Problems
31. **Maximum Depth of Binary Tree** - [✅ IMPLEMENTED] - [`src/trees/medium/FindMaximumDepthOfBinaryTree.java`](../../../trees/medium/FindMaximumDepthOfBinaryTree.java)
    - Tree depth calculation
    - Banking context: Account hierarchy depth analysis
    - **Priority: MEDIUM**

32. **Invert Binary Tree** - [✅ IMPLEMENTED] - [`src/trees/medium/InvertBinaryTree.java`](../../../trees/medium/InvertBinaryTree.java)
    - Tree structure manipulation
    - Banking context: Reverse account hierarchy
    - **Priority: MEDIUM**

33. **Lowest Common Ancestor** - [✅ IMPLEMENTED] - [`src/binarysearchtree/medium/LowestCommonAncestorBST.java`](../../../binarysearchtree/medium/LowestCommonAncestorBST.java)
    - Find LCA in binary tree
    - Banking context: Common parent account/branch
    - **Priority: MEDIUM**

34. **Binary Tree Maximum Path Sum** - [✅ IMPLEMENTED] - [`src/trees/hard/BinaryTreeMaximumPathSum.java`](../../../trees/hard/BinaryTreeMaximumPathSum.java)
    - Complex tree DP problem
    - Banking context: Maximum profit path in investment tree
    - **Priority: LOW** - Advanced problem

### Graph Problems
35. **Course Schedule (Topological Sort)** - [✅ IMPLEMENTED] - [`src/graphs/medium/CourseSchedule.java`](../../../graphs/medium/CourseSchedule.java)
    - Detect cycle in directed graph
    - Banking context: Dependency cycle in loan approvals
    - **Priority: HIGH** - Important algorithm

36. **Word Ladder** - [✅ IMPLEMENTED] - [`src/graphs/medium/WordLadder.java`](../../../graphs/medium/WordLadder.java)
    - BFS shortest path
    - Banking context: Shortest transformation path for data migration
    - **Priority: MEDIUM**

37. **Clone Graph** - [✅ IMPLEMENTED] - [`src/graphs/medium/CloneGraph.java`](../../../graphs/medium/CloneGraph.java)
    - Deep copy graph with DFS/BFS
    - Banking context: Replicate account relationship network
    - **Priority: MEDIUM**

### Dynamic Programming
38. **Climbing Stairs** - [✅ IMPLEMENTED] - [`src/dp/linear/basic/ClimbingStairs.java`](../../../dp/linear/basic/ClimbingStairs.java)
    - Basic DP problem
    - Banking context: Number of ways to reach savings goal
    - **Priority: HIGH** - Fundamental DP

39. **Unique Paths** - [✅ IMPLEMENTED] - [`src/dp/grid/path_counting/UniquePaths.java`](../../../dp/grid/path_counting/UniquePaths.java)
    - 2D DP grid problem
    - Banking context: Paths through approval workflow
    - **Priority: MEDIUM**

40. **Word Break** - [✅ IMPLEMENTED] - [`src/dp/advanced/WordBreak.java`](../../../dp/advanced/WordBreak.java)
    - DP with string manipulation
    - Banking context: Transaction description parsing
    - **Priority: MEDIUM**

41. **Longest Increasing Subsequence** - [✅ IMPLEMENTED] - [`src/dp/linear/sequence/LongestIncreasingSubsequence.java`](../../../dp/linear/sequence/LongestIncreasingSubsequence.java)
    - DP with binary search optimization
    - Banking context: Longest growth period in account balance
    - **Priority: LOW** - Advanced DP

### Heap/Priority Queue Problems
42. **Kth Largest Element in Array** - [✅ IMPLEMENTED] - [`src/heap/medium/KthLargestElementInArray.java`](../../../heap/medium/KthLargestElementInArray.java)
    - Quick select or heap
    - Banking context: Find Kth largest transaction
    - **Priority: HIGH** - Common interview question

43. **Top K Frequent Elements** - [✅ IMPLEMENTED] - [`src/hashmaps/medium/TopKFrequentElements.java`](../../../hashmaps/medium/TopKFrequentElements.java)
    - Heap + HashMap
    - Banking context: Most frequent transaction types
    - **Priority: HIGH** - Real-world application

44. **Merge K Sorted Lists** - [✅ IMPLEMENTED] - [`src/heap/hard/MergeKSortedLists.java`](../../../heap/hard/MergeKSortedLists.java)
    - Priority queue for merging
    - Banking context: Merge sorted transaction streams
    - **Priority: MEDIUM**

### Hash Map Advanced
45. **Design HashMap** - [✅ IMPLEMENTED] - [`src/design/medium/DesignHashMap.java`](../../../design/medium/DesignHashMap.java)
    - Implement hash map from scratch
    - Banking context: Custom caching for account data
    - **Priority: MEDIUM** - System design component

46. **LRU Cache** - [✅ IMPLEMENTED] - [`src/design/medium/LRUCache.java`](../../../design/medium/LRUCache.java)
    - Least Recently Used cache
    - Banking context: Account data caching
    - **Priority: HIGH** - System design fundamental

### Bit Manipulation
47. **Single Number** - [✅ IMPLEMENTED] - [`src/bitmanipulation/easy/SingleNumber.java`](../../../bitmanipulation/easy/SingleNumber.java)
    - XOR to find unique element
    - Banking context: Find unmatched transaction

48. **Number of 1 Bits** - [✅ IMPLEMENTED] - [`src/bitmanipulation/easy/NumberOf1Bits.java`](../../../bitmanipulation/easy/NumberOf1Bits.java)
    - Count set bits
    - Banking context: Feature flags counting
    - **Priority: LOW**

49. **Power of Two** - [✅ IMPLEMENTED] - [`src/bitmanipulation/easy/PowerOfTwo.java`](../../../bitmanipulation/easy/PowerOfTwo.java)
    - Bit manipulation check
    - Banking context: Validate batch sizes
    - **Priority: LOW**

### Math Problems
50. **Pow(x, n)** - [✅ IMPLEMENTED] - [`src/math/medium/PowXN.java`](../../../math/medium/PowXN.java)
    - Exponentiation by squaring
    - Banking context: Compound interest calculation
    - **Priority: MEDIUM**

51. **Sqrt(x)** - [✅ IMPLEMENTED] - [`src/math/medium/SqrtX.java`](../../../math/medium/SqrtX.java)
    - Binary search for square root
    - Banking context: Risk calculation algorithms
    - **Priority: MEDIUM**

### Two Pointers
52. **Valid Palindrome** - [✅ IMPLEMENTED] - [`src/strings/easy/ValidPalindrome.java`](../../strings/easy/ValidPalindrome.java)
    - Two pointers from ends
    - Banking context: Validate symmetric account numbers
    - **Priority: MEDIUM**

53. **Move Zeroes** - [✅ IMPLEMENTED] - [`src/arrays/easy/MoveZeroes.java`](../../arrays/easy/MoveZeroes.java)
    - Two pointers for in-place modification
    - Banking context: Remove null transactions from array
    - **Priority: MEDIUM**

### Binary Search
54. **Search in Rotated Sorted Array** - [✅ IMPLEMENTED] - [`src/arrays/medium/SearchRotatedArray.java`](../../arrays/medium/SearchRotatedArray.java)
    - Modified binary search
    - Banking context: Search in time-shifted data
    - **Priority: HIGH** - Common interview question

55. **Find First and Last Position** - [✅ IMPLEMENTED] - [`src/binarysearch/medium/FindFirstAndLastPosition.java`](../../../binarysearch/medium/FindFirstAndLastPosition.java)
    - Binary search bounds
    - Banking context: Find transaction time range
    - **Priority: MEDIUM**

## 🎯 Banking-Specific Scenarios

### Real-World Banking Problems
56. **Transaction Categorization** - [🟠 STUB IMPLEMENTATION] - [`src/company/banking/kotakbank/missing/TransactionCategorization.java`](./missing/TransactionCategorization.java)
    - Classify transactions by type/merchant
    - Machine learning + string matching
    - **Priority: HIGH** - Core banking functionality

57. **Currency Exchange Rate Calculator** - [🟠 STUB IMPLEMENTATION] - [`src/company/banking/kotakbank/missing/CurrencyExchangeCalculator.java`](./missing/CurrencyExchangeCalculator.java)
    - Graph algorithms for currency conversion
    - Shortest path with dynamic weights
    - **Priority: MEDIUM**

58. **Bank Branch Locator** - [🟠 STUB IMPLEMENTATION] - [`src/company/banking/kotakbank/missing/BankBranchLocator.java`](./missing/BankBranchLocator.java)
    - Nearest neighbor search
    - Geographic algorithms with distance calculation
    - **Priority: MEDIUM**

59. **Loan Eligibility Calculator** - [🟠 STUB IMPLEMENTATION] - [`src/company/banking/kotakbank/missing/LoanEligibilityCalculator.java`](./missing/LoanEligibilityCalculator.java)
    - Rule engine implementation
    - Decision tree algorithms
    - **Priority: HIGH** - Core banking business logic

60. **Account Number Generator** - [🟠 STUB IMPLEMENTATION] - [`src/company/banking/kotakbank/missing/AccountNumberGenerator.java`](./missing/AccountNumberGenerator.java)
    - Check digit algorithms (Luhn algorithm)
    - Validation and generation of account numbers
    - **Priority: HIGH** - Banking system fundamental

## 📚 Implementation Priority Guide

### 🔴 HIGH Priority (Must Implement)
These are fundamental algorithms frequently asked in banking interviews:
- ~~Maximum Subarray (Kadane's Algorithm)~~ [✅ IMPLEMENTED]
- ~~Merge Intervals~~ [✅ IMPLEMENTED]
- ~~Longest Substring Without Repeating Characters~~ [✅ IMPLEMENTED]
- String to Integer (atoi)
- ~~Detect Cycle in Linked List~~ [✅ IMPLEMENTED]
- ~~Min Stack~~ [✅ IMPLEMENTED]
- Sliding Window Maximum
- ~~Course Schedule (Topological Sort)~~ [✅ IMPLEMENTED]
- ~~Climbing Stairs~~ [✅ IMPLEMENTED]
- ~~Kth Largest Element~~ [✅ IMPLEMENTED]
- Top K Frequent Elements
- ~~LRU Cache~~ [✅ IMPLEMENTED]
- ~~Search in Rotated Sorted Array~~ [✅ IMPLEMENTED]
- Transaction Categorization
- Loan Eligibility Calculator
- Account Number Generator

### 🟡 MEDIUM Priority (Should Implement)
Important algorithms that appear regularly:
- Rotate Array
- Product of Array Except Self
- Find Minimum in Rotated Sorted Array
- Remove Nth Node From End
- Daily Temperatures
- ~~Design Circular Queue~~ [✅ IMPLEMENTED]
- Maximum Depth of Binary Tree
- ~~Unique Paths~~ [✅ IMPLEMENTED]
- ~~Word Break~~ [✅ IMPLEMENTED]
- Merge K Sorted Lists
- Design HashMap
- Pow(x, n)
- ~~Valid Palindrome~~ [✅ IMPLEMENTED]
- Find First and Last Position

### 🟢 LOW Priority (Nice to Have)
Advanced problems for senior positions:
- ~~Palindromic Substrings~~ [✅ IMPLEMENTED]
- Binary Tree Maximum Path Sum
- Longest Increasing Subsequence
- Number of 1 Bits
- Power of Two

## 🔗 External Resources Used

### Primary Sources
- **LeetCode Discuss**: Interview experiences tagged with "Kotak"
- **GeeksforGeeks**: Company-specific interview corner
- **TryExponent**: Banking and fintech interview questions
- **EngineBogie**: Real interview experiences shared by candidates
- **Glassdoor**: Anonymous interview reviews
- **InterviewBit**: Company-wise interview questions

### Banking Domain Knowledge
- **RBI Guidelines**: Reserve Bank of India regulations
- **Core Banking**: Understanding of CBS (Core Banking Solutions)
- **Payment Systems**: NEFT, RTGS, IMPS knowledge
- **KYC/AML**: Know Your Customer and Anti-Money Laundering

## 📝 Next Steps

1. **Implement HIGH priority questions first**
2. **Add banking context to each solution**
3. **Include test cases with banking scenarios**
4. **Document time/space complexity**
5. **Add integration with LeetCode API for validation**
6. **Create banking-specific edge cases**
7. **Add system design components for scalability**

## 💡 Interview Tips for Kotak Bank

### Technical Preparation
- Focus on **data structures** and **algorithms** fundamentals
- Practice **system design** for banking applications
- Understand **concurrency** and **thread safety**
- Know **database concepts** (ACID properties, transactions)

### Banking Domain Knowledge
- Understand **core banking operations**
- Know **regulatory compliance** requirements
- Practice **real-time transaction processing** scenarios
- Study **fraud detection** and **risk management** concepts

### Behavioral Preparation
- Prepare examples of **problem-solving** in financial context
- Understand **customer-centric** approach in banking
- Practice explaining **technical concepts** to non-technical stakeholders
- Show understanding of **security** and **compliance** importance

---

**Last Updated**: September 12, 2025  
**Next Review**: September 19, 2025  
**Source**: Compiled from multiple interview experience platforms
