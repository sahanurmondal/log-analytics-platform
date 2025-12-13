# NeetCode 250 - Comprehensive LeetCode Problem Guide

## 📚 Quick Navigation to All NeetCode 250 Topics

| **Easy Problems**                          | **Medium Problems**                            | **Hard Problems**                          | **Must-Do Categories**                 |
|--------------------------------------------|------------------------------------------------|--------------------------------------------|----------------------------------------|
| [🟢 Arrays Easy](#arrays-easy)             | [🟡 Arrays Medium](#arrays-medium)             | [🔴 Arrays Hard](#arrays-hard)             | [⭐ Critical 50](#critical-50-problems) |
| [🟢 Strings Easy](#strings-easy)           | [🟡 Strings Medium](#strings-medium)           | [🔴 Strings Hard](#strings-hard)           | [🔥 Top 75](#top-75-problems)          |
| [🟢 Linked Lists Easy](#linked-lists-easy) | [🟡 Linked Lists Medium](#linked-lists-medium) | [🔴 Linked Lists Hard](#linked-lists-hard) | [📊 Study Plan](#8-week-study-plan)    |
| [🟢 Trees Easy](#trees-easy)               | [🟡 Trees Medium](#trees-medium)               | [🔴 Trees Hard](#trees-hard)               |                                        |
| [🟢 Graphs Easy](#graphs-easy)             | [🟡 Graphs Medium](#graphs-medium)             | [🔴 Graphs Hard](#graphs-hard)             |                                        |
| [🟢 DP Easy](#dp-easy)                     | [🟡 DP Medium](#dp-medium)                     | [🔴 DP Hard](#dp-hard)                     |                                        |

---

## Critical 50 Problems (Interview Essential with Code Links)

| Seq | LC# | Problem                             | Code Link                                                                                                                     | Difficulty | Topic          |
|-----|-----|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|------------|----------------|
| 1   | 1   | Two Sum                             | [`easy/TwoSum.java`](./arrays/easy/TwoSum.java)                                                                               | Easy       | Arrays/Hash    |
| 2   | 217 | Contains Duplicate                  | [`easy/ContainsDuplicate.java`](./arrays/easy/ContainsDuplicate.java)                                                         | Easy       | Hash           |
| 3   | 242 | Valid Anagram                       | [`easy/ValidAnagram.java`](./arrays/easy/ValidAnagram.java)                                                                   | Easy       | Strings        |
| 4   | 49  | Group Anagrams                      | [`medium/GroupAnagrams.java`](./hashmaps/medium/GroupAnagrams.java)                                                           | Medium     | Hash           |
| 5   | 347 | Top K Frequent Elements             | [`medium/TopKFrequentElements.java`](./heap/medium/TopKFrequentElements.java)                                                 | Medium     | Heap           |
| 6   | 238 | Product of Array Except Self        | [`medium/ProductOfArrayExceptSelf.java`](./arrays/medium/ProductOfArrayExceptSelf.java)                                       | Medium     | Arrays         |
| 7   | 20  | Valid Parentheses                   | [`easy/ValidParentheses.java`](./stacks/medium/ValidParentheses.java)                                                         | Easy       | Stacks         |
| 8   | 21  | Merge Two Sorted Lists              | [`easy/MergeTwoSortedLists.java`](./linkedlist/easy/MergeTwoSortedLists.java)                                                 | Easy       | Linked Lists   |
| 9   | 206 | Reverse Linked List                 | [`medium/ReverseLinkedList.java`](./linkedlist/medium/ReverseLinkedList.java)                                                 | Easy       | Linked Lists   |
| 10  | 23  | Merge k Sorted Lists                | [`hard/MergeKSortedLists.java`](./heap/hard/MergeKSortedLists.java)                                                           | Hard       | Linked Lists   |
| 11  | 146 | LRU Cache                           | [`medium/LRUCache.java`](./design/medium/LRUCache.java)                                                                       | Medium     | Design         |
| 12  | 208 | Implement Trie                      | [`medium/Trie.java`](./tries/medium/Trie.java)                                                                                | Medium     | Trie           |
| 13  | 200 | Number of Islands                   | [`medium/NumberOfIslands.java`](./graphs/medium/NumberOfIslands.java)                                                         | Medium     | Graphs         |
| 14  | 3   | Longest Substring Without Repeating | [`medium/LongestSubstringWithoutRepeatingCharacters.java`](./hashmaps/medium/LongestSubstringWithoutRepeatingCharacters.java) | Medium     | Sliding Window |
| 15  | 5   | Longest Palindromic Substring       | [`medium/LongestPalindromicSubstring.java`](./strings/medium/LongestPalindromicSubstring.java)                                | Medium     | Strings        |
| 16  | 15  | 3Sum                                | [`medium/ThreeSum.java`](./arrays/medium/ThreeSum.java)                                                                       | Medium     | Arrays         |
| 17  | 11  | Container With Most Water           | [`medium/ContainerWithMostWater.java`](./arrays/medium/ContainerWithMostWater.java)                                           | Medium     | Two Pointers   |
| 18  | 33  | Search in Rotated Sorted Array      | [`medium/SearchRotatedArray.java`](./arrays/medium/SearchRotatedArray.java)                                                   | Medium     | Binary Search  |
| 19  | 53  | Maximum Subarray                    | [`medium/MaximumSubarray.java`](./dp/linear/sequence/MaximumSubarray.java)                                                    | Medium     | DP             |
| 20  | 198 | House Robber                        | [`medium/HouseRobber.java`](./dp/linear/optimization/HouseRobber.java)                                                        | Medium     | DP             |
| 21  | 70  | Climbing Stairs                     | [`linear/basic/ClimbingStairs.java`](./dp/linear/basic/ClimbingStairs.java)                                                   | Easy       | DP             |
| 22  | 322 | Coin Change                         | [`medium/CoinChange.java`](./dp/knapsack/unbounded/CoinChange.java)                                                           | Medium     | DP             |
| 23  | 300 | Longest Increasing Subsequence      | [`medium/LongestIncreasingSubsequence.java`](./dp/linear/sequence/LongestIncreasingSubsequence.java)                          | Medium     | DP             |
| 24  | 139 | Word Break                          | [`advanced/WordBreak.java`](./dp/advanced/WordBreak.java)                                                                     | Medium     | DP             |
| 25  | 207 | Course Schedule                     | [`medium/CourseSchedule.java`](./graphs/medium/CourseSchedule.java)                                                           | Medium     | Graphs         |
| 26  | 133 | Clone Graph                         | [`medium/CloneGraph.java`](./graphs/medium/CloneGraph.java)                                                                   | Medium     | Graphs         |
| 27  | 102 | Binary Tree Level Order             | [`medium/BinaryTreeLevelOrderTraversal.java`](./trees/medium/BinaryTreeLevelOrderTraversal.java)                              | Medium     | Trees          |
| 28  | 226 | Invert Binary Tree                  | [`medium/InvertBinaryTree.java`](./trees/medium/InvertBinaryTree.java)                                                        | Easy       | Trees          |
| 29  | 104 | Maximum Depth of Binary Tree        | [`medium/FindMaximumDepthOfBinaryTree.java`](./trees/medium/FindMaximumDepthOfBinaryTree.java)                                | Easy       | Trees          |
| 30  | 100 | Same Tree                           | [`easy/SameTree.java`](./trees/easy/SameTree.java)                                                                            | Easy       | Trees          |
| 31  | 124 | Binary Tree Maximum Path Sum        | [`hard/BinaryTreeMaximumPathSum.java`](./trees/hard/BinaryTreeMaximumPathSum.java)                                            | Hard       | Trees          |
| 32  | 297 | Serialize and Deserialize BT        | [`hard/SerializeAndDeserializeBinaryTree.java`](./trees/hard/SerializeAndDeserializeBinaryTree.java)                          | Hard       | Trees          |
| 33  | 127 | Word Ladder                         | [`hard/WordLadder.java`](./tries/hard/WordLadder.java)                                                                        | Hard       | Graphs         |
| 34  | 126 | Word Ladder II                      | [`hard/WordLadderII.java`](./queues/hard/WordLadderII.java)                                                                   | Hard       | Graphs         |
| 35  | 76  | Minimum Window Substring            | [`hard/MinimumWindowSubstring.java`](./miscellaneous/recent/MinimumWindowSubstring.java)                                      | Hard       | Sliding Window |
| 36  | 42  | Trapping Rain Water                 | [`hard/TrappingRainWater.java`](./arrays/hard/TrappingRainWater.java)                                                         | Hard       | Arrays         |
| 37  | 4   | Median of Two Sorted Arrays         | [`hard/MedianOfTwoSortedArrays.java`](./arrays/hard/MedianOfTwoSortedArrays.java)                                             | Hard       | Binary Search  |
| 38  | 10  | Regular Expression Matching         | [`hard/RegularExpressionMatching.java`](./dp/string/matching/RegularExpressionMatching.java)                                  | Hard       | DP             |
| 39  | 44  | Wildcard Matching                   | [`hard/WildcardMatching.java`](./dp/string/matching/WildcardMatching.java)                                                    | Hard       | DP             |
| 40  | 72  | Edit Distance                       | [`hard/EditDistance.java`](./dp/string/matching/EditDistance.java)                                                            | Hard       | DP             |
| 41  | 174 | Dungeon Game                        | [`hard/DungeonGame.java`](./dp/advanced/DungeonGame.java)                                                                     | Hard       | DP             |
| 42  | 312 | Burst Balloons                      | [`hard/BurstBalloons.java`](./dp/interval/BurstBalloons.java)                                                                 | Hard       | DP             |
| 43  | 84  | Largest Rectangle in Histogram      | [`hard/LargestRectangleInHistogram.java`](./arrays/hard/LargestRectangleInHistogram.java)                                     | Hard       | Stacks         |
| 44  | 739 | Daily Temperatures                  | [`medium/DailyTemperatures.java`](./stacks/medium/DailyTemperatures.java)                                                     | Medium     | Stacks         |
| 45  | 150 | Evaluate Reverse Polish Notation    | [`medium/EvaluateReversePolishNotation.java`](./stacks/medium/EvaluateReversePolishNotation.java)                             | Medium     | Stacks         |
| 46  | 721 | Accounts Merge                      | [`medium/AccountsMerge.java`](./unionfind/medium/AccountsMerge.java)                                                          | Medium     | Union Find     |
| 47  | 323 | Number of Connected Components      | [`medium/NumberOfConnectedComponents.java`](./graphs/medium/NumberOfConnectedComponents.java)                                 | Medium     | Union Find     |
| 48  | 128 | Longest Consecutive                 | [`medium/LongestConsecutive.java`](./arrays/medium/LongestConsecutive.java)                                                   | Medium     | Hash           |
| 49  | 567 | Permutation in String               | [`medium/PermutationInString.java`](./slidingwindow/medium/PermutationInString.java)                                          | Medium     | Sliding Window |
| 50  | 560 | Subarray Sum Equals K               | [`medium/SubarraySumEqualsK.java`](./arrays/medium/SubarraySumEqualsK.java)                                                   | Medium     | Hash           |

---

## 📊 Complete 250 Problems with LeetCode Links (Seq 51-250)

### Part 1: Seq 51-125 (With Code Links)

| Seq | LC#  | Problem                        | Code Link                                                                                                       | Diff | Category         |
|-----|------|--------------------------------|-----------------------------------------------------------------------------------------------------------------|------|------------------|
| 51  | 283  | Move Zeroes                    | [`easy/MoveZeroes.java`](./arrays/easy/MoveZeroes.java)                                                         | E    | Arrays           |
| 52  | 26   | Remove Duplicates              | [`easy/RemoveDuplicatesFromSortedArray.java`](./arrays/easy/RemoveDuplicatesFromSortedArray.java)               | E    | Arrays           |
| 53  | 169  | Majority Element               | [`easy/MajorityElement.java`](./arrays/easy/MajorityElement.java)                                               | E    | Arrays           |
| 54  | 88   | Merge Sorted Array             | [`easy/MergeSortedArray.java`](./arrays/easy/MergeSortedArray.java)                                             | E    | Arrays           |
| 55  | 66   | Plus One                       | [`easy/PlusOne.java`](./arrays/easy/PlusOne.java)                                                               | E    | Arrays           |
| 56  | 118  | Pascal's Triangle              | [`easy/PascalsTriangle.java`](./arrays/easy/PascalsTriangle.java)                                               | E    | Arrays           |
| 57  | 448  | Find Disappeared               | [`easy/FindAllNumbersDisappearedInArray.java`](./arrays/easy/FindAllNumbersDisappearedInArray.java)             | E    | Arrays           |
| 58  | 125  | Valid Palindrome               | [`easy/ValidPalindrome.java`](./strings/easy/ValidPalindrome.java)                                              | E    | Strings          |
| 59  | 242  | Valid Anagram                  | [`easy/ValidAnagram.java`](./arrays/easy/ValidAnagram.java)                                                     | E    | Strings          |
| 60  | 387  | First Unique Char              | [`FirstUniqueCharacterInStream.java`](./queues/medium/FirstUniqueCharacterInStream.java)                        | E    | Strings          |
| 61  | 14   | Longest Common Prefix          | [`easy/LongestCommonPrefix.java`](./strings/medium/LongestCommonPrefix.java)                                    | E    | Strings          |
| 62  | 13   | Roman to Integer               | [`easy/RomanToInteger.java`](./strings/medium/RomanToInteger.java)                                              | E    | Strings          |
| 63  | 205  | Isomorphic Strings             | [`easy/IsomorphicStrings.java`](./strings/easy/IsomorphicStrings.java)                                          | E    | Strings          |
| 64  | 290  | Word Pattern                   | [`easy/WordPattern.java`](./strings/easy/WordPattern.java)                                                      | E    | Strings          |
| 65  | 344  | Reverse String                 | [`easy/ReverseString.java`](./strings/easy/ReverseString.java)                                                  | E    | Strings          |
| 66  | 12   | Integer to Roman               | [`medium/IntegerToRoman.java`](./math/medium/IntegerToRoman.java)                                               | M    | Strings          |
| 67  | 165  | Compare Versions               | [`medium/CompareVersionNumbers.java`](./strings/medium/CompareVersionNumbers.java)                              | M    | Strings          |
| 68  | 43   | Multiply Strings               | [`medium/MultiplyStrings.java`](./math/medium/MultiplyStrings.java)                                             | M    | Strings          |
| 69  | 5    | Longest Palindrome Substring   | [`medium/LongestPalindromicSubstring.java`](./strings/medium/LongestPalindromicSubstring.java)                  | M    | Strings          |
| 70  | 6    | ZigZag Conversion              | [`medium/ZigZagConversion.java`](./strings/medium/ZigZagConversion.java)                                        | M    | Strings          |
| 71  | 271  | Encode Decode Strings          | [`medium/EncodeDecodeStrings.java`](./strings/medium/EncodeDecodeStrings.java)                                  | M    | Strings          |
| 72  | 214  | Shortest Palindrome            | [`hard/ShortestPalindrome.java`](./strings/hard/ShortestPalindrome.java)                                        | H    | Strings          |
| 73  | 28   | Find the Index strStr          | [`easy/ImplementStrStr.java`](./strings/easy/ImplementStrStr.java)                                              | E    | Strings          |
| 74  | 415  | Add Strings                    | [`easy/AddStrings.java`](./strings/easy/AddStrings.java)                                                        | E    | Strings          |
| 75  | 67   | Add Binary                     | [`easy/AddBinary.java`](./strings/easy/AddBinary.java)                                                          | E    | Strings          |
| 76  | 696  | Count Binary Substrings        | [`medium/CountBinarySubstrings.java`](./strings/medium/CountBinarySubstrings.java)                              | E    | Strings          |
| 77  | 338  | Counting Bits                  | [`mathematical/CountingBits.java`](./dp/mathematical/CountingBits.java)                                         | E    | DP               |
| 78  | 509  | Fibonacci Number               | [`easy/FibonacciNumber.java`](./dp/linear/basic/FibonacciNumber.java)                                           | E    | DP               |
| 79  | 746  | Min Cost Climbing Stairs       | [`advanced/MinCostClimbingStairs.java`](./dp/advanced/MinCostClimbingStairs.java)                               | E    | DP               |
| 80  | 303  | Range Sum Query                | [`easy/RangeSumQuery.java`](./dp/advanced/RangeSumQueryImmutable.java)                                          | E    | Arrays           |
| 81  | 304  | Range Sum Query 2D             | [`medium/RangeSumQuery2D.java`](./matrix/medium/RangeSumQuery2D.java)                                           | M    | Matrix           |
| 82  | 1    | Two Sum                        | [`easy/TwoSum.java`](./arrays/easy/TwoSum.java)                                                                 | E    | Hash             |
| 83  | 217  | Contains Duplicate             | [`easy/ContainsDuplicate.java`](./arrays/easy/ContainsDuplicate.java)                                           | E    | Hash             |
| 84  | 347  | Top K Frequent Elements        | [`medium/TopKFrequentElements.java`](./hashmaps/medium/TopKFrequentElements.java)                               | M    | Heap             |
| 85  | 238  | Product of Array Except Self   | [`medium/ProductOfArrayExceptSelf.java`](./arrays/medium/ProductOfArrayExceptSelf.java)                         | M    | Arrays           |
| 86  | 1047 | Remove All Adjacent Duplicates | [`easy/RemoveAllAdjacentDuplicatesInString.java`](./stacks/easy/RemoveAllAdjacentDuplicatesInString.java)       | E    | Stacks           |
| 87  | 394  | Decode String                  | [`medium/DecodeString.java`](./stacks/medium/DecodeString.java)                                                 | M    | Stacks           |
| 88  | 20   | Valid Parentheses              | [`easy/ValidParentheses.java`](./stacks/medium/ValidParentheses.java)                                           | E    | Stacks           |
| 89  | 84   | Largest Rectangle in Histogram | [`hard/LargestRectangleInHistogram.java`](./arrays/hard/LargestRectangleInHistogram.java)                       | H    | Stacks           |
| 90  | 35   | Search Insert Position         | [`easy/SearchInsertPosition.java`](./binarysearch/easy/SearchInsertPosition.java)                               | E    | Binary Search    |
| 91  | 704  | Binary Search                  | [`easy/BinarySearch.java`](./binarysearch/easy/BinarySearch.java)                                               | E    | Binary Search    |
| 92  | 278  | First Bad Version              | [`easy/FirstBadVersion.java`](./binarysearch/easy/FirstBadVersion.java)                                         | E    | Binary Search    |
| 93  | 34   | Find First and Last Position   | [`medium/FindFirstAndLastPosition.java`](./binarysearch/medium/FindFirstAndLastPosition.java)                   | M    | Binary Search    |
| 94  | 153  | Find Minimum in Rotated        | [`medium/FindMinimumInRotatedSortedArray.java`](./arrays/medium/FindMinimumInRotatedSortedArray.java)           | M    | Binary Search    |
| 95  | 33   | Search in Rotated Sorted Array | [`medium/SearchRotatedArray.java`](./arrays/medium/SearchRotatedArray.java)                                     | M    | Binary Search    |
| 96  | 162  | Find Peak Element              | [`medium/FindPeakElement.java`](./binarysearch/medium/FindPeakElement.java)                                     | M    | Binary Search    |
| 97  | 74   | Search a 2D Matrix             | [`medium/SearchA2DMatrix.java`](./matrix/medium/SearchA2DMatrix.java)                                           | M    | Binary Search    |
| 98  | 4    | Median of Two Sorted Arrays    | [`hard/MedianOfTwoSortedArrays.java`](./arrays/hard/MedianOfTwoSortedArrays.java)                               | H    | Binary Search    |
| 99  | 410  | Split Array Largest Sum        | [`hard/SplitArrayLargestSum.java`](./binarysearch/hard/SplitArrayLargestSum.java)                               | H    | Binary Search    |
| 100 | 69   | Sqrt(x)                        | [`easy/Sqrt.java`](./binarysearch/easy/Sqrt.java)                                                               | E    | Binary Search    |
| 101 | 367  | Valid Perfect Square           | [`easy/ValidPerfectSquare.java`](./binarysearch/easy/ValidPerfectSquare.java)                                   | E    | Binary Search    |
| 102 | 875  | Koko Eating Bananas            | [`medium/KokoEatingBananas.java`](./binarysearch/medium/KokoEatingBananas.java)                                 | M    | Binary Search    |
| 103 | 981  | Time Based Key-Value Store     | [`medium/TimeBasedKeyValueStore.java`](./binarysearch/medium/TimeBasedKeyValueStore.java)                       | M    | Binary Search    |
| 104 | 1011 | Capacity To Ship               | [`medium/CapacityToShipPackagesWithinDDays.java`](./binarysearch/medium/CapacityToShipPackagesWithinDDays.java) | M    | Binary Search    |
| 105 | 191  | Number of 1 Bits               | [`easy/NumberOf1Bits.java`](./bitmanipulation/easy/NumberOf1Bits.java)                                          | E    | Bit Manipulation |
| 106 | 371  | Sum of Two Integers            | [`medium/SumOfTwoIntegers.java`](./bitmanipulation/medium/SumOfTwoIntegers.java)                                | M    | Bit Manipulation |
| 107 | 201  | Bitwise AND of Range           | [`medium/BitwiseANDOfRange.java`](./bitmanipulation/medium/FindBitwiseANDOfNumbersRange.java)                   | M    | Bit Manipulation |
| 108 | 260  | Single Number III              | [`medium/SingleNumberIII.java`](./bitmanipulation/medium/SingleNumberIII.java)                                  | M    | Bit Manipulation |
| 109 | 231  | Power of Two                   | [`easy/PowerOfTwo.java`](./bitmanipulation/easy/PowerOfTwo.java)                                                | E    | Bit Manipulation |
| 110 | 268  | Missing Number                 | [`easy/MissingNumber.java`](./bitmanipulation/easy/MissingNumber.java)                                          | E    | Arrays           |
| 111 | 643  | Maximum Average Subarray       | [`easy/MaximumAverageSubarray.java`](./slidingwindow/medium/MaximumAverageSubarrayI.java)                       | E    | Arrays           |
| 112 | 122  | Best Time Buy Sell II          | [`easy/BestTimeToBuyAndSellStockII.java`](./arrays/easy/BestTimeToBuyAndSellStockII.java)                       | E    | Arrays           |
| 113 | 189  | Rotate Array                   | [`medium/RotateArray.java`](./arrays/easy/RotateArray.java)                                                     | M    | Arrays           |
| 114 | 151  | Reverse Words in String        | [`medium/ReverseWordsInString.java`](./strings/medium/ReverseWordsInString.java)                                | M    | Strings          |
| 115 | 8    | String to Integer atoi         | [`medium/StringToInteger.java`](./strings/medium/StringToInteger.java)                                          | M    | Strings          |
| 116 | 17   | Letter Combinations            | [`medium/LetterCombinationsPhoneNumber.java`](./backtracking/medium/LetterCombinationsPhoneNumber.java)         | M    | Backtracking     |
| 117 | 22   | Generate Parentheses           | [`medium/GenerateParentheses.java`](./backtracking/medium/GenerateParentheses.java)                             | M    | Backtracking     |
| 118 | 39   | Combination Sum                | [`medium/CombinationSum.java`](./backtracking/medium/CombinationSum.java)                                       | M    | Backtracking     |
| 119 | 40   | Combination Sum II             | [`medium/CombinationSumII.java`](./backtracking/medium/CombinationSumII.java)                                   | M    | Backtracking     |
| 120 | 216  | Combination Sum III            | [`medium/CombinationSumIII.java`](./backtracking/medium/CombinationSumIII.java)                                 | M    | Backtracking     |
| 121 | 79   | Word Search                    | [`medium/WordSearch.java`](./backtracking/medium/WordSearch.java)                                               | M    | Backtracking     |
| 122 | 212  | Word Search II                 | [`hard/WordSearchII.java`](./grid/hard/WordSearchII.java)                                                       | H    | Trie             |
| 123 | 78   | Subsets                        | [`medium/Subsets.java`](./backtracking/medium/Subsets.java)                                                     | M    | Backtracking     |
| 124 | 90   | Subsets II                     | [`medium/SubsetsII.java`](./backtracking/medium/SubsetsII.java)                                                 | M    | Backtracking     |
| 125 | 46   | Permutations                   | [`medium/Permutations.java`](./backtracking/medium/Permutations.java)                                           | M    | Backtracking     |

### Part 2: Seq 126-250 (Complete with Code Links)

| Seq | LC#  | Problem                      | Diff | Category       | Code Link                                                                                                                         |
|-----|------|------------------------------|------|----------------|-----------------------------------------------------------------------------------------------------------------------------------|
| 126 | 47   | Permutations II              | M    | Backtracking   | [`medium/PermutationsII.java`](./backtracking/medium/PermutationsII.java)                                                         |
| 127 | 37   | Sudoku Solver                | H    | Backtracking   | [`hard/SudokuSolver.java`](./arrays/hard/SudokuSolver.java)                                                                       |
| 128 | 51   | N-Queens                     | H    | Backtracking   | [`hard/NQueens.java`](./backtracking/hard/NQueens.java)                                                                           |
| 129 | 52   | N-Queens II                  | H    | Backtracking   | [`hard/NQueensII.java`](./backtracking/hard/NQueensII.java)                                                                       |
| 130 | 36   | Valid Sudoku                 | M    | Hash           | [`medium/ValidSudoku.java`](./arrays/medium/ValidSudoku.java)                                                                     |
| 131 | 438  | Find All Anagrams            | M    | Sliding Window | [`medium/FindAllAnagrams.java`](./slidingwindow/medium/FindAllAnagramsInString.java)                                              |
| 132 | 30   | Substring with Concatenation | H    | Strings        | [`hard/SubstringWithConcatenation.java`](./strings/hard/SubstringWithConcatenationOfAllWords.java)                                |
| 133 | 65   | Valid Number                 | H    | Strings        | [`hard/ValidNumber.java`](./strings/hard/ValidNumber.java)                                                                        |
| 134 | 68   | Text Justification           | H    | Strings        | [`hard/TextJustification.java`](./strings/hard/TextJustification.java)                                                            |
| 135 | 186  | Reverse Words String II      | M    | Strings        | [`medium/ReverseWordsInStringII.java`](./strings/medium/ReverseWordsInStringII.java)                                              |
| 136 | 155  | Min Stack                    | E    | Stacks         | [`easy/MinStack.java`](./design/easy/MinStack.java)                                                                               |
| 137 | 739  | Daily Temperatures           | M    | Stacks         | [`medium/DailyTemperatures.java`](./stacks/medium/DailyTemperatures.java)                                                         |
| 138 | 150  | Evaluate RPN                 | M    | Stacks         | [`medium/EvaluateRPN.java`](./stacks/medium/EvaluateRPN.java)                                                                     |
| 139 | 735  | Asteroid Collision           | M    | Stacks         | [`medium/AsteroidCollision.java`](./stacks/medium/AsteroidCollision.java)                                                         |
| 140 | 227  | Basic Calculator II          | M    | Stacks         | [`medium/BasicCalculatorII.java`](./stacks/medium/BasicCalculatorII.java)                                                         |
| 141 | 206  | Reverse Linked List          | E    | Linked Lists   | [`medium/ReverseLinkedList.java`](./linkedlist/medium/ReverseLinkedList.java)                                                     |
| 142 | 21   | Merge Two Sorted Lists       | E    | Linked Lists   | [`easy/MergeTwoSortedLists.java`](./linkedlist/easy/MergeTwoSortedLists.java)                                                     |
| 143 | 141  | Linked List Cycle            | E    | Linked Lists   | [`medium/LinkedListCycle.java`](./linkedlist/medium/LinkedListCycle.java)                                                         |
| 144 | 83   | Remove Duplicates Sorted LL  | E    | Linked Lists   | [`medium/RemoveDuplicatesFromSortedList.java`](./linkedlist/medium/RemoveDuplicatesFromSortedList.java)                           |
| 145 | 234  | Palindrome Linked List       | M    | Linked Lists   | [`medium/PalindromeLinkedList.java`](./linkedlist/medium/PalindromeLinkedList.java)                                               |
| 146 | 876  | Middle of Linked List        | E    | Linked Lists   | [`easy/MiddleOfLinkedList.java`](./linkedlist/easy/MiddleOfLinkedList.java)                                                       |
| 147 | 19   | Remove Nth Node              | M    | Linked Lists   | [`medium/RemoveNthNodeFromEnd.java`](./linkedlist/medium/RemoveNthNodeFromEndOfList.java)                                         |
| 148 | 2    | Add Two Numbers              | M    | Linked Lists   | [`medium/AddTwoNumbers.java`](./linkedlist/medium/AddTwoNumbers.java)                                                             |
| 149 | 142  | Linked List Cycle II         | M    | Linked Lists   | [`medium/LinkedListCycleII.java`](./linkedlist/medium/LinkedListCycleII.java)                                                     |
| 150 | 160  | Intersection of Two LL       | M    | Linked Lists   | [`medium/IntersectionOfTwoLinkedLists.java`](./linkedlist/medium/IntersectionOfTwoLinkedLists.java)                               |
| 151 | 143  | Reorder List                 | M    | Linked Lists   | [`medium/ReorderList.java`](./linkedlist/medium/ReorderList.java)                                                                 |
| 152 | 24   | Swap Nodes in Pairs          | M    | Linked Lists   | [`medium/SwapNodesInPairs.java`](./linkedlist/medium/SwapNodesInPairs.java)                                                       |
| 153 | 61   | Rotate List                  | M    | Linked Lists   | [`medium/RotateList.java`](./linkedlist/medium/RotateList.java)                                                                   |
| 154 | 138  | Copy List Random Pointer     | M    | Linked Lists   | [`medium/CopyListWithRandomPointer.java`](./linkedlist/medium/CopyListWithRandomPointer.java)                                     |
| 155 | 82   | Remove Duplicates II         | M    | Linked Lists   | [`medium/RemoveDuplicatesFromSortedListII.java`](./linkedlist/medium/RemoveDuplicatesFromSortedListII.java)                       |
| 156 | 430  | Flatten Multilevel LL        | M    | Linked Lists   | [`medium/FlattenMultilevelDoublyLinkedList.java`](./linkedlist/hard/FlattenMultilevelDoublyLinkedList.java)                       |
| 157 | 147  | Insertion Sort List          | M    | Linked Lists   | [`medium/InsertionSortList.java`](./linkedlist/hard/SortList.java)                                                                |
| 158 | 23   | Merge k Sorted Lists         | H    | Linked Lists   | [`hard/MergeKSortedLists.java`](./binarysearch/hard/MergeKSortedLists.java)                                                       |
| 159 | 25   | Reverse Nodes k-Group        | H    | Linked Lists   | [`hard/ReverseNodesInKGroup.java`](./linkedlist/hard/ReverseNodesInKGroup.java)                                                   |
| 160 | 146  | LRU Cache                    | M    | Design         | [`medium/LRUCache.java`](./design/medium/LRUCache.java)                                                                           |
| 161 | 460  | LFU Cache                    | H    | Design         | [`hard/LFUCache.java`](./design/hard/LFUCache.java)                                                                               |
| 162 | 208  | Implement Trie               | M    | Trie           | [`medium/Trie.java`](./tries/medium/Trie.java)                                                                                    |
| 163 | 211  | Add and Search Word          | M    | Trie           | [`medium/WordDictionary.java`](./design/medium/WordDictionary.java)                                                               |
| 164 | 226  | Invert Binary Tree           | E    | Trees          | [`medium/InvertBinaryTree.java`](./trees/medium/InvertBinaryTree.java)                                                            |
| 165 | 104  | Maximum Depth                | E    | Trees          | [`medium/MaximumDepthOfBinaryTree.java`](./trees/medium/FindMaximumDepthOfBinaryTree.java)                                        |
| 166 | 100  | Same Tree                    | E    | Trees          | [`easy/SameTree.java`](./trees/easy/SameTree.java)                                                                                |
| 167 | 572  | Subtree of Another Tree      | E    | Trees          | [`easy/SubtreeOfAnotherTree.java`](./trees/easy/SubtreeOfAnotherTree.java)                                                        |
| 168 | 235  | LCA of BST                   | E    | Trees          | [`easy/LowestCommonAncestorBST.java`](./binarysearchtree/medium/LowestCommonAncestorBST.java)                                     |
| 169 | 94   | Binary Tree Inorder          | E    | Trees          | [`easy/BinaryTreeInorderTraversal.java`](./trees/easy/BinaryTreeInorderTraversal.java)                                            |
| 170 | 144  | Binary Tree Preorder         | M    | Trees          | [`medium/BinaryTreePreorderTraversal.java`](./trees/medium/BinaryTreePreorderTraversal.java)                                      |
| 171 | 145  | Binary Tree Postorder        | M    | Trees          | [`medium/BinaryTreePostorderTraversal.java`](./trees/medium/BinaryTreePostorderTraversal.java)                                    |
| 172 | 98   | Validate BST                 | M    | Trees          | [`medium/ValidateBinarySearchTree.java`](./trees/medium/ValidateBinarySearchTree.java)                                            |
| 173 | 101  | Symmetric Tree               | E    | Trees          | [`medium/SymmetricTree.java`](./trees/medium/SymmetricTree.java)                                                                  |
| 174 | 102  | Level Order Traversal        | M    | Trees          | [`medium/BinaryTreeLevelOrderTraversal.java`](./trees/medium/BinaryTreeLevelOrderTraversal.java)                                  |
| 175 | 230  | Kth Smallest BST             | M    | Trees          | [`medium/KthSmallestElementInBST.java`](./binarysearchtree/medium/KthSmallestElementInBST.java)                                   |
| 176 | 105  | Construct BT Preorder        | M    | Trees          | [`medium/ConstructBinaryTreeFromPreorderAndInorder.java`](./trees/medium/ConstructBinaryTreeFromPreorderAndInorderTraversal.java) |
| 177 | 112  | Path Sum                     | E    | Trees          | [`easy/PathSum.java`](./dp/advanced/PathSum.java)                                                                                 |
| 178 | 113  | Path Sum II                  | M    | Trees          | [`medium/PathSumII.java`](./trees/medium/PathSumII.java)                                                                          |
| 179 | 236  | LCA                          | M    | Trees          | [`medium/LowestCommonAncestor.java`](./binarysearchtree/medium/LowestCommonAncestorBST.java)                                      |
| 180 | 199  | Binary Tree Right View       | M    | Trees          | [`medium/BinaryTreeRightSideView.java`](./trees/medium/BinaryTreeRightSideView.java)                                              |
| 181 | 222  | Count Complete Nodes         | M    | Trees          | [`medium/CountCompleteTreeNodes.java`](./trees/medium/CountCompleteTreeNodes.java)                                                |
| 182 | 116  | Populating Next Pointers     | M    | Trees          | [`medium/PopulatingNextRightPointersInEachNode.java`](./trees/medium/PopulatingNextRightPointersInEachNode.java)                  |
| 183 | 114  | Flatten BT to LL             | M    | Trees          | [`medium/FlattenBinaryTreeToLinkedList.java`](./linkedlist/medium/FlattenBinaryTreeToLinkedList.java)                             |
| 184 | 863  | All Nodes Distance K         | M    | Trees          | [`medium/AllNodesDistanceKInBinaryTree.java`](./trees/medium/FindAllNodesDistanceKInBinaryTree.java)                              |
| 185 | 103  | Zigzag Level Order           | M    | Trees          | [`medium/BinaryTreeZigzagLevelOrder.java`](./trees/medium/BinaryTreeZigzagLevelOrderTraversal.java)                               |
| 186 | 124  | Binary Tree Max Path Sum     | H    | Trees          | [`hard/BinaryTreeMaximumPathSum.java`](./trees/hard/BinaryTreeMaximumPathSum.java)                                                |
| 187 | 297  | Serialize Deserialize BT     | H    | Trees          | [`hard/SerializeAndDeserializeBinaryTree.java`](./trees/hard/SerializeAndDeserializeBinaryTree.java)                              |
| 188 | 968  | Binary Tree Cameras          | H    | Trees          | [`hard/BinaryTreeCameras.java`](./miscellaneous/recent/BinaryTreeCameras.java)                                                    |
| 189 | 99   | Recover Binary Search Tree   | H    | Trees          | [`hard/RecoverBinarySearchTree.java`](./binarysearchtree/hard/RecoverBinarySearchTree.java)                                       |
| 190 | 703  | Kth Largest Element          | E    | Heap           | [`easy/KthLargestElement.java`](./heap/medium/KthLargestElementInArray.java)                                                      |
| 191 | 295  | Find Median Data Stream      | H    | Heap           | [`hard/MedianFinder.java`](./miscellaneous/recent/MedianOfDataStream.java)                                                        |
| 192 | 200  | Number of Islands            | M    | Graphs         | [`medium/NumberOfIslands.java`](./graphs/medium/NumberOfIslands.java)                                                             |
| 193 | 695  | Max Area of Island           | M    | Graphs         | [`medium/MaxAreaOfIsland.java`](./graphs/easy/MaxAreaOfIsland.java)                                                               |
| 194 | 133  | Clone Graph                  | M    | Graphs         | [`medium/CloneGraph.java`](./graphs/medium/CloneGraph.java)                                                                       |
| 195 | 286  | Walls and Gates              | M    | Graphs         | [`medium/WallsAndGates.java`](./queues/hard/WallsAndGates.java)                                                                   |
| 196 | 207  | Course Schedule              | M    | Graphs         | [`medium/CourseSchedule.java`](./graphs/medium/CourseSchedule.java)                                                               |
| 197 | 210  | Course Schedule II           | M    | Graphs         | [`medium/CourseScheduleII.java`](./graphs/medium/CourseScheduleII.java)                                                           |
| 198 | 261  | Graph Valid Tree             | M    | Graphs         | [`medium/GraphValidTree.java`](./graphs/medium/GraphValidTree.java)                                                               |
| 199 | 323  | Number of Connected          | M    | Union Find     | [`medium/NumberOfConnectedComponents.java`](./unionfind/medium/NumberOfConnectedComponents.java)                                  |
| 200 | 721  | Accounts Merge               | M    | Union Find     | [`medium/AccountsMerge.java`](./unionfind/medium/AccountsMerge.java)                                                              |
| 201 | 417  | Pacific Atlantic Water       | M    | Graphs         | [`medium/PacificAtlanticWaterFlow.java`](./graphs/medium/PacificAtlanticWaterFlow.java)                                           |
| 202 | 694  | Number Distinct Islands      | M    | Graphs         | [`medium/NumberOfDistinctIslands.java`](./grid/medium/NumberOfDistinctIslands.java)                                               |
| 203 | 994  | Rotting Oranges              | M    | Graphs         | [`medium/RottingOranges.java`](./graphs/medium/RottingOranges.java)                                                               |
| 204 | 797  | All Paths Source Target      | M    | Graphs         | [`medium/AllPathsFromSourceToTarget.java`](./backtracking/medium/AllPathsFromSourceToTarget.java)                                 |
| 205 | 269  | Alien Dictionary             | H    | Graphs         | [`hard/AlienDictionary.java`](./graphs/hard/AlienDictionary.java)                                                                 |
| 206 | 127  | Word Ladder                  | H    | Graphs         | [`hard/WordLadder.java`](./graphs/medium/WordLadder.java)                                                                         |
| 207 | 126  | Word Ladder II               | H    | Graphs         | [`hard/WordLadderII.java`](./queues/hard/WordLadderII.java)                                                                       |
| 208 | 329  | Longest Increasing Path      | H    | Graphs         | [`hard/LongestIncreasingPath.java`](./miscellaneous/recent/LongestIncreasingPath.java)                                            |
| 209 | 310  | Minimum Height Trees         | H    | Graphs         | [`hard/MinimumHeightTrees.java`](./graphs/medium/MinimumHeightTrees.java)                                                         |
| 210 | 70   | Climbing Stairs              | E    | DP             | [`linear/basic/ClimbingStairs.java`](./dp/linear/basic/ClimbingStairs.java)                                                       |
| 211 | 198  | House Robber                 | M    | DP             | [`linear/optimization/HouseRobber.java`](./dp/linear/optimization/HouseRobber.java)                                               |
| 212 | 213  | House Robber II              | M    | DP             | [`linear/optimization/HouseRobberII.java`](./dp/linear/optimization/HouseRobberII.java)                                           |
| 213 | 53   | Maximum Subarray             | M    | DP             | [`linear/sequence/MaximumSubarray.java`](./dp/linear/sequence/MaximumSubarray.java)                                               |
| 214 | 152  | Maximum Product Subarray     | M    | DP             | [`linear/sequence/MaximumProductSubarray.java`](./dp/linear/sequence/MaximumProductSubarray.java)                                 |
| 215 | 322  | Coin Change                  | M    | DP             | [`knapsack/unbounded/CoinChange.java`](./dp/knapsack/unbounded/CoinChange.java)                                                   |
| 216 | 518  | Coin Change II               | M    | DP             | [`knapsack/unbounded/CoinChangeII.java`](./dp/knapsack/unbounded/CoinChangeII.java)                                               |
| 217 | 300  | Longest Increasing Subseq    | M    | DP             | [`linear/sequence/LongestIncreasingSubsequence.java`](./dp/linear/sequence/LongestIncreasingSubsequence.java)                     |
| 218 | 91   | Decode Ways                  | M    | DP             | [`state_machine/DecodeWays.java`](./dp/state_machine/DecodeWays.java)                                                             |
| 219 | 62   | Unique Paths                 | M    | DP             | [`grid/path_counting/UniquePaths.java`](./dp/grid/path_counting/UniquePaths.java)                                                 |
| 220 | 63   | Unique Paths II              | M    | DP             | [`grid/medium/UniquePathsII.java`](./grid/medium/UniquePathsII.java)                                                              |
| 221 | 64   | Minimum Path Sum             | M    | DP             | [`grid/optimization/MinimumPathSum.java`](./dp/grid/optimization/MinimumPathSum.java)                                             |
| 222 | 416  | Partition Equal Subset       | M    | DP             | [`knapsack/01/PartitionEqualSubsetSum.java`](./dp/knapsack/subset_sum/PartitionEqualSubsetSum.java)                               |
| 223 | 1143 | Longest Common Subseq        | M    | DP             | [`string/subsequence/LongestCommonSubsequence.java`](./dp/string/subsequence/LongestCommonSubsequence.java)                       |
| 224 | 121  | Best Time Buy Sell           | E    | DP             | [`stock_trading/BestTimeToBuyAndSellStock.java`](./dp/stock_trading/BestTimeToBuyAndSellStock.java)                               |
| 225 | 377  | Combination Sum IV           | M    | DP             | [`knapsack/unbounded/CombinationSumIV.java`](./dp/knapsack/unbounded/CombinationSumIV.java)                                       |
| 226 | 72   | Edit Distance                | H    | DP             | [`string/matching/EditDistance.java`](./dp/string/matching/EditDistance.java)                                                     |
| 227 | 312  | Burst Balloons               | H    | DP             | [`interval/BurstBalloons.java`](./dp/interval/BurstBalloons.java)                                                                 |
| 228 | 174  | Dungeon Game                 | H    | DP             | [`advanced/DungeonGame.java`](./dp/advanced/DungeonGame.java)                                                                     |
| 229 | 10   | Regular Expression           | H    | DP             | [`string/matching/RegularExpressionMatching.java`](./dp/string/matching/RegularExpressionMatching.java)                           |
| 230 | 44   | Wildcard Matching            | H    | DP             | [`string/matching/WildcardMatching.java`](./dp/string/matching/WildcardMatching.java)                                             |
| 231 | 354  | Russian Doll Envelopes       | H    | DP             | [`advanced/RussianDollEnvelopes.java`](./sorting/hard/RussianDollEnvelopes.java)                                                  |
| 232 | 128  | Longest Consecutive          | M    | Hash           | [`medium/LongestConsecutiveSequence.java`](./arrays/medium/LongestConsecutiveSequence.java)                                       |
| 233 | 15   | 3Sum                         | M    | Arrays         | [`medium/ThreeSum.java`](./arrays/medium/ThreeSum.java)                                                                           |
| 234 | 11   | Container Most Water         | M    | Two Pointers   | [`medium/ContainerWithMostWater.java`](./arrays/medium/ContainerWithMostWater.java)                                               |
| 235 | 76   | Minimum Window Substring     | H    | Sliding Window | [`hard/MinimumWindowSubstring.java`](./miscellaneous/recent/MinimumWindowSubstring.java)                                          |
| 236 | 42   | Trapping Rain Water          | H    | Arrays         | [`hard/TrappingRainWater.java`](./arrays/hard/TrappingRainWater.java)                                                             |
| 237 | 3    | Longest Substring No Repeat  | M    | Sliding Window | [`medium/LongestSubstringWithoutRepeatingCharacters.java`](./hashmaps/medium/LongestSubstringWithoutRepeatingCharacters.java)     |
| 238 | 567  | Permutation in String        | M    | Sliding Window | [`medium/PermutationInString.java`](./slidingwindow/medium/PermutationInString.java)                                              |
| 239 | 49   | Group Anagrams               | M    | Hash           | [`medium/GroupAnagrams.java`](./hashmaps/medium/GroupAnagrams.java)                                                               |
| 240 | 424  | Longest Repeating Char       | M    | Strings        | [`medium/LongestRepeatingCharacterReplacement.java`](./strings/medium/LongestRepeatingCharacterReplacement.java)                  |
| 241 | 560  | Subarray Sum Equals K        | M    | Hash           | [`medium/SubarraySumEqualsK.java`](./arrays/medium/SubarraySumEqualsK.java)                                                       |
| 242 | 48   | Rotate Image                 | M    | Matrix         | [`medium/RotateImage.java`](./arrays/medium/RotateImage.java)                                                                     |
| 243 | 54   | Spiral Matrix                | M    | Matrix         | [`medium/SpiralMatrix.java`](./arrays/medium/SpiralMatrix.java)                                                                   |
| 244 | 55   | Jump Game                    | M    | Greedy         | [`medium/JumpGame.java`](./arrays/medium/JumpGame.java)                                                                           |
| 245 | 31   | Next Permutation             | M    | Arrays         | [`medium/NextPermutation.java`](./arrays/medium/NextPermutation.java)                                                             |
| 246 | 73   | Set Matrix Zeroes            | M    | Matrix         | [`medium/SetMatrixZeroes.java`](./arrays/medium/SetMatrixZeroes.java)                                                             |
| 247 | 85   | Maximal Rectangle            | H    | Matrix         | [`hard/MaximalRectangle.java`](./arrays/hard/MaximalRectangle.java)                                                               |
| 248 | 84   | Largest Rectangle Histogram  | H    | Stacks         | [`hard/LargestRectangleInHistogram.java`](./arrays/hard/LargestRectangleInHistogram.java)                                         |
| 249 | 45   | Jump Game II                 | M    | Greedy         | [`medium/JumpGameII.java`](./dp/advanced/JumpGameII.java)                                                                         |
| 250 | 51   | N-Queens                     | H    | Backtracking   | [`hard/NQueens.java`](./backtracking/hard/NQueens.java)                                                                           |

---

## Top 75 Problems

### Arrays Easy

| #  | Problem                             | LeetCode                                                                       | Code Link                                                                                           |
|----|-------------------------------------|--------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| 1  | Two Sum                             | [1](https://leetcode.com/problems/two-sum/)                                    | [`easy/TwoSum.java`](./arrays/easy/TwoSum.java)                                                     |
| 2  | Best Time to Buy and Sell Stock     | [121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)          | [`easy/BestTimeToBuyAndSellStock.java`](./arrays/easy/BestTimeToBuyAndSellStock.java)               |
| 3  | Contains Duplicate                  | [217](https://leetcode.com/problems/contains-duplicate/)                       | [`easy/ContainsDuplicate.java`](./arrays/easy/ContainsDuplicate.java)                               |
| 4  | Move Zeroes                         | [283](https://leetcode.com/problems/move-zeroes/)                              | [`easy/MoveZeroes.java`](./arrays/easy/MoveZeroes.java)                                             |
| 5  | Remove Duplicates from Sorted Array | [26](https://leetcode.com/problems/remove-duplicates-from-sorted-array/)       | [`easy/RemoveDuplicatesFromSortedArray.java`](./arrays/easy/RemoveDuplicatesFromSortedArray.java)   |
| 6  | Majority Element                    | [169](https://leetcode.com/problems/majority-element/)                         | [`easy/MajorityElement.java`](./arrays/easy/MajorityElement.java)                                   |
| 7  | Merge Sorted Array                  | [88](https://leetcode.com/problems/merge-sorted-array/)                        | [`easy/MergeSortedArray.java`](./arrays/easy/MergeSortedArray.java)                                 |
| 8  | Plus One                            | [66](https://leetcode.com/problems/plus-one/)                                  | [`easy/PlusOne.java`](./arrays/easy/PlusOne.java)                                                   |
| 9  | Pascal's Triangle                   | [118](https://leetcode.com/problems/pascals-triangle/)                         | [`easy/PascalsTriangle.java`](./arrays/easy/PascalsTriangle.java)                                   |
| 10 | Find All Numbers Disappeared        | [448](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/) | [`easy/FindAllNumbersDisappearedInArray.java`](./arrays/easy/FindAllNumbersDisappearedInArray.java) |
| 11 | Valid Palindrome                    | [125](https://leetcode.com/problems/valid-palindrome/)                         | [`easy/ValidPalindrome.java`](./strings/easy/ValidPalindrome.java)                                  |
| 12 | Valid Anagram                       | [242](https://leetcode.com/problems/valid-anagram/)                            | [`easy/ValidAnagram.java`](./arrays/easy/ValidAnagram.java)                                         |

### Arrays Medium

| #  | Problem                                 | LeetCode                                                                           | Code Link                                                                                                                     |
|----|-----------------------------------------|------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| 1  | Product of Array Except Self            | [238](https://leetcode.com/problems/product-of-array-except-self/)                 | [`medium/ProductOfArrayExceptSelf.java`](./arrays/medium/ProductOfArrayExceptSelf.java)                                       |
| 2  | Subarray Sum Equals K                   | [560](https://leetcode.com/problems/subarray-sum-equals-k/)                        | [`medium/SubarraySumEqualsK.java`](./arrays/medium/SubarraySumEqualsK.java)                                                   |
| 3  | Rotate Image                            | [48](https://leetcode.com/problems/rotate-image/)                                  | [`medium/RotateImage.java`](./arrays/medium/RotateImage.java)                                                                 |
| 4  | Spiral Matrix                           | [54](https://leetcode.com/problems/spiral-matrix/)                                 | [`medium/SpiralMatrix.java`](./arrays/medium/SpiralMatrix.java)                                                               |
| 5  | Jump Game                               | [55](https://leetcode.com/problems/jump-game/)                                     | [`medium/JumpGame.java`](./arrays/medium/JumpGame.java)                                                                       |
| 6  | Maximum Product Subarray                | [152](https://leetcode.com/problems/maximum-product-subarray/)                     | [`medium/MaximumProductSubarray.java`](./arrays/medium/MaximumProductSubarray.java)                                           |
| 7  | Find Minimum in Rotated Sorted          | [153](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)         | [`medium/FindMinimumInRotatedSortedArray.java`](./arrays/medium/FindMinimumInRotatedSortedArray.java)                         |
| 8  | Set Matrix Zeroes                       | [73](https://leetcode.com/problems/set-matrix-zeroes/)                             | [`medium/SetMatrixZeroes.java`](./arrays/medium/SetMatrixZeroes.java)                                                         |
| 9  | 3Sum                                    | [15](https://leetcode.com/problems/3sum/)                                          | [`medium/ThreeSum.java`](./arrays/medium/ThreeSum.java)                                                                       |
| 10 | Container With Most Water               | [11](https://leetcode.com/problems/container-with-most-water/)                     | [`medium/ContainerWithMostWater.java`](./arrays/medium/ContainerWithMostWater.java)                                           |
| 11 | Next Permutation                        | [31](https://leetcode.com/problems/next-permutation/)                              | [`medium/NextPermutation.java`](./arrays/medium/NextPermutation.java)                                                         |
| 12 | Search in Rotated Sorted Array          | [33](https://leetcode.com/problems/search-in-rotated-sorted-array/)                | [`medium/SearchRotatedArray.java`](./arrays/medium/SearchRotatedArray.java)                                                   |
| 13 | Longest Substring Without Repeating     | [3](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | [`medium/LongestSubstringWithoutRepeatingCharacters.java`](./hashmaps/medium/LongestSubstringWithoutRepeatingCharacters.java) |
| 14 | Group Anagrams                          | [49](https://leetcode.com/problems/group-anagrams/)                                | [`medium/GroupAnagrams.java`](./hashmaps/medium/GroupAnagrams.java)                                                           |
| 15 | Longest Repeating Character Replacement | [424](https://leetcode.com/problems/longest-repeating-character-replacement/)      | [`medium/LongestRepeatingCharacterReplacement.java`](./strings/medium/LongestRepeatingCharacterReplacement.java)              |
| 16 | Longest Increasing Subsequence          | [300](https://leetcode.com/problems/longest-increasing-subsequence/)               | [`medium/LongestIncreasingSubsequence.java`](./dp/linear/sequence/LongestIncreasingSubsequence.java)                          |
| 17 | Coin Change                             | [322](https://leetcode.com/problems/coin-change/)                                  | [`medium/CoinChange.java`](./dp/knapsack/unbounded/CoinChange.java)                                                           |
| 18 | House Robber                            | [198](https://leetcode.com/problems/house-robber/)                                 | [`medium/HouseRobber.java`](./dp/linear/optimization/HouseRobber.java)                                                        |

### Arrays Hard

| # | Problem                        | LeetCode                                                            | Code Link                                                                                    |
|---|--------------------------------|---------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| 1 | First Missing Positive         | [41](https://leetcode.com/problems/first-missing-positive/)         | [`medium/FirstMissingPositive.java`](./arrays/medium/FirstMissingPositive.java)              |
| 2 | Median of Two Sorted Arrays    | [4](https://leetcode.com/problems/median-of-two-sorted-arrays/)     | [`hard/MedianOfTwoSortedArrays.java`](./arrays/hard/MedianOfTwoSortedArrays.java)            |
| 3 | Sliding Window Maximum         | [239](https://leetcode.com/problems/sliding-window-maximum/)        | [`hard/SlidingWindowMaximum.java`](./arrays/hard/SlidingWindowMaximum.java)                  |
| 4 | Trapping Rain Water            | [42](https://leetcode.com/problems/trapping-rain-water/)            | [`hard/TrappingRainWater.java`](./arrays/hard/TrappingRainWater.java)                        |
| 5 | Largest Rectangle in Histogram | [84](https://leetcode.com/problems/largest-rectangle-in-histogram/) | [`hard/LargestRectangleInHistogram.java`](./arrays/hard/LargestRectangleInHistogram.java)    |
| 6 | Maximal Rectangle              | [85](https://leetcode.com/problems/maximal-rectangle/)              | [`hard/MaximalRectangle.java`](./arrays/hard/MaximalRectangle.java)                          |
| 7 | Wildcard Matching              | [44](https://leetcode.com/problems/wildcard-matching/)              | [`hard/WildcardMatching.java`](./arrays/hard/WildcardMatching.java)                          |
| 8 | Regular Expression Matching    | [10](https://leetcode.com/problems/regular-expression-matching/)    | [`hard/RegularExpressionMatching.java`](./dp/string/matching/RegularExpressionMatching.java) |

### Linked Lists Easy

| # | Problem                            | LeetCode                                                                | Code Link                                                                                               |
|---|------------------------------------|-------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| 1 | Reverse Linked List                | [206](https://leetcode.com/problems/reverse-linked-list/)               | [`medium/ReverseLinkedList.java`](./linkedlist/medium/ReverseLinkedList.java)                           |
| 2 | Merge Two Sorted Lists             | [21](https://leetcode.com/problems/merge-two-sorted-lists/)             | [`easy/MergeTwoSortedLists.java`](./linkedlist/easy/MergeTwoSortedLists.java)                           |
| 3 | Linked List Cycle                  | [141](https://leetcode.com/problems/linked-list-cycle/)                 | [`medium/LinkedListCycle.java`](./linkedlist/medium/LinkedListCycle.java)                               |
| 4 | Remove Duplicates from Sorted List | [83](https://leetcode.com/problems/remove-duplicates-from-sorted-list/) | [`medium/RemoveDuplicatesFromSortedList.java`](./linkedlist/medium/RemoveDuplicatesFromSortedList.java) |
| 5 | Palindrome Linked List             | [234](https://leetcode.com/problems/palindrome-linked-list/)            | [`medium/PalindromeLinkedList.java`](./linkedlist/medium/PalindromeLinkedList.java)                     |
| 6 | Middle of Linked List              | [876](https://leetcode.com/problems/middle-of-the-linked-list/)         | [`easy/MiddleOfLinkedList.java`](./linkedlist/easy/MiddleOfLinkedList.java)                             |
| 7 | Remove Nth Node From End           | [19](https://leetcode.com/problems/remove-nth-node-from-end-of-list/)   | [`medium/RemoveNthNodeFromEndOfList.java`](./linkedlist/medium/RemoveNthNodeFromEndOfList.java)         |
| 8 | Intersection of Two Linked Lists   | [160](https://leetcode.com/problems/intersection-of-two-linked-lists/)  | [`medium/IntersectionOfTwoLinkedLists.java`](./linkedlist/medium/IntersectionOfTwoLinkedLists.java)     |

### Linked Lists Medium

| #  | Problem                               | LeetCode                                                                      | Code Link                                                                                                   |
|----|---------------------------------------|-------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| 1  | Add Two Numbers                       | [2](https://leetcode.com/problems/add-two-numbers/)                           | [`medium/AddTwoNumbers.java`](./linkedlist/medium/AddTwoNumbers.java)                                       |
| 2  | Linked List Cycle II                  | [142](https://leetcode.com/problems/linked-list-cycle-ii/)                    | [`medium/LinkedListCycleII.java`](./linkedlist/medium/LinkedListCycleII.java)                               |
| 3  | Reorder List                          | [143](https://leetcode.com/problems/reorder-list/)                            | [`medium/ReorderList.java`](./linkedlist/medium/ReorderList.java)                                           |
| 4  | Swap Nodes in Pairs                   | [24](https://leetcode.com/problems/swap-nodes-in-pairs/)                      | [`medium/SwapNodesInPairs.java`](./linkedlist/medium/SwapNodesInPairs.java)                                 |
| 5  | Rotate List                           | [61](https://leetcode.com/problems/rotate-list/)                              | [`medium/RotateList.java`](./linkedlist/medium/RotateList.java)                                             |
| 6  | Copy List with Random Pointer         | [138](https://leetcode.com/problems/copy-list-with-random-pointer/)           | [`medium/CopyListWithRandomPointer.java`](./linkedlist/medium/CopyListWithRandomPointer.java)               |
| 7  | Remove Duplicates from Sorted List II | [82](https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/)    | [`medium/RemoveDuplicatesFromSortedListII.java`](./linkedlist/medium/RemoveDuplicatesFromSortedListII.java) |
| 8  | Flatten a Multilevel Doubly LL        | [430](https://leetcode.com/problems/flatten-a-multilevel-doubly-linked-list/) | [`medium/FlattenMultilevelDoublyLinkedList.java`](./linkedlist/hard/FlattenMultilevelDoublyLinkedList.java) |
| 9  | Insertion Sort List                   | [147](https://leetcode.com/problems/insertion-sort-list/)                     | [`medium/InsertionSortList.java`](./linkedlist/hard/SortList.java)                                          |
| 10 | LRU Cache                             | [146](https://leetcode.com/problems/lru-cache/)                               | [`medium/LRUCache.java`](./design/medium/LRUCache.java)                                                     |

### Linked Lists Hard

| # | Problem                       | LeetCode                                                      | Code Link                                                                         |
|---|-------------------------------|---------------------------------------------------------------|-----------------------------------------------------------------------------------|
| 1 | Merge k Sorted Lists          | [23](https://leetcode.com/problems/merge-k-sorted-lists/)     | [`hard/MergeKSortedLists.java`](./heap/hard/MergeKSortedLists.java)               |
| 2 | Reverse Nodes in k-Group      | [25](https://leetcode.com/problems/reverse-nodes-in-k-group/) | [`hard/ReverseNodesInKGroup.java`](./linkedlist/hard/ReverseNodesInKGroup.java)   |
| 3 | LFU Cache                     | [460](https://leetcode.com/problems/lfu-cache/)               | [`hard/LFUCache.java`](./design/hard/LFUCache.java)                               |
| 4 | Merge Two Sorted Lists (Hard) | [21](https://leetcode.com/problems/merge-two-sorted-lists/)   | [`easy/MergeTwoSortedListsHard.java`](./linkedlist/easy/MergeTwoSortedLists.java) |

### Trees Easy

| # | Problem                       | LeetCode                                                                             | Code Link                                                                                     |
|---|-------------------------------|--------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| 1 | Invert Binary Tree            | [226](https://leetcode.com/problems/invert-binary-tree/)                             | [`medium/InvertBinaryTree.java`](./trees/medium/InvertBinaryTree.java)                        |
| 2 | Maximum Depth of Binary Tree  | [104](https://leetcode.com/problems/maximum-depth-of-binary-tree/)                   | [`medium/MaximumDepthOfBinaryTree.java`](./trees/medium/FindMaximumDepthOfBinaryTree.java)    |
| 3 | Same Tree                     | [100](https://leetcode.com/problems/same-tree/)                                      | [`easy/SameTree.java`](./trees/easy/SameTree.java)                                            |
| 4 | Subtree of Another Tree       | [572](https://leetcode.com/problems/subtree-of-another-tree/)                        | [`easy/SubtreeOfAnotherTree.java`](./trees/easy/SubtreeOfAnotherTree.java)                    |
| 5 | Lowest Common Ancestor (BST)  | [235](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | [`easy/LowestCommonAncestorBST.java`](./binarysearchtree/medium/LowestCommonAncestorBST.java) |
| 6 | Binary Tree Inorder Traversal | [94](https://leetcode.com/problems/binary-tree-inorder-traversal/)                   | [`easy/BinaryTreeInorderTraversal.java`](./trees/easy/BinaryTreeInorderTraversal.java)        |
| 7 | Valid Binary Search Tree      | [98](https://leetcode.com/problems/validate-binary-search-tree/)                     | [`medium/ValidateBinarySearchTree.java`](./trees/medium/ValidateBinarySearchTree.java)        |
| 8 | Symmetric Tree                | [101](https://leetcode.com/problems/symmetric-tree/)                                 | [`medium/SymmetricTree.java`](./trees/medium/SymmetricTree.java)                              |

### Trees Medium

| #  | Problem                           | LeetCode                                                                                        | Code Link                                                                                                                         |
|----|-----------------------------------|-------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| 1  | Binary Tree Level Order Traversal | [102](https://leetcode.com/problems/binary-tree-level-order-traversal/)                         | [`medium/BinaryTreeLevelOrderTraversal.java`](./trees/medium/BinaryTreeLevelOrderTraversal.java)                                  |
| 2  | Kth Smallest Element in BST       | [230](https://leetcode.com/problems/kth-smallest-element-in-a-bst/)                             | [`medium/KthSmallestElementInBST.java`](./binarysearchtree/medium/KthSmallestElementInBST.java)                                   |
| 3  | Construct Binary Tree             | [105](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) | [`medium/ConstructBinaryTreeFromPreorderAndInorder.java`](./trees/medium/ConstructBinaryTreeFromPreorderAndInorderTraversal.java) |
| 4  | Path Sum                          | [112](https://leetcode.com/problems/path-sum/)                                                  | [`easy/PathSum.java`](./dp/advanced/PathSum.java)                                                                                 |
| 5  | Path Sum II                       | [113](https://leetcode.com/problems/path-sum-ii/)                                               | [`medium/PathSumII.java`](./trees/medium/PathSumII.java)                                                                          |
| 6  | Lowest Common Ancestor            | [236](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/)                   | [`medium/LowestCommonAncestor.java`](./binarysearchtree/medium/LowestCommonAncestorBST.java)                                      |
| 7  | Right Side View                   | [199](https://leetcode.com/problems/binary-tree-right-side-view/)                               | [`medium/BinaryTreeRightSideView.java`](./trees/medium/BinaryTreeRightSideView.java)                                              |
| 8  | Count Complete Tree Nodes         | [222](https://leetcode.com/problems/count-complete-tree-nodes/)                                 | [`medium/CountCompleteTreeNodes.java`](./trees/medium/CountCompleteTreeNodes.java)                                                |
| 9  | Populating Next Right Pointers    | [116](https://leetcode.com/problems/populating-next-right-pointers-in-each-node/)               | [`medium/PopulatingNextRightPointersInEachNode.java`](./trees/medium/PopulatingNextRightPointersInEachNode.java)                  |
| 10 | Flatten Binary Tree to LL         | [114](https://leetcode.com/problems/flatten-binary-tree-to-linked-list/)                        | [`medium/FlattenBinaryTreeToLinkedList.java`](./linkedlist/medium/FlattenBinaryTreeToLinkedList.java)                             |
| 11 | All Nodes Distance K              | [863](https://leetcode.com/problems/all-nodes-distance-k-in-binary-tree/)                       | [`medium/FindAllNodesDistanceKInBinaryTree.java`](./trees/medium/FindAllNodesDistanceKInBinaryTree.java)                          |
| 12 | Binary Tree Zigzag Level Order    | [103](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/)                  | [`medium/BinaryTreeZigzagLevelOrderTraversal.java`](./trees/medium/BinaryTreeZigzagLevelOrderTraversal.java)                      |

### Trees Hard

| # | Problem                       | LeetCode                                                                    | Code Link                                                                                            |
|---|-------------------------------|-----------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| 1 | Binary Tree Maximum Path Sum  | [124](https://leetcode.com/problems/binary-tree-maximum-path-sum/)          | [`hard/BinaryTreeMaximumPathSum.java`](./trees/hard/BinaryTreeMaximumPathSum.java)                   |
| 2 | Serialize and Deserialize BT  | [297](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) | [`hard/SerializeAndDeserializeBinaryTree.java`](./trees/hard/SerializeAndDeserializeBinaryTree.java) |
| 3 | Binary Tree Cameras           | [968](https://leetcode.com/problems/binary-tree-cameras/)                   | [`hard/BinaryTreeCameras.java`](./miscellaneous/recent/BinaryTreeCameras.java)                       |
| 4 | Recover Binary Search Tree    | [99](https://leetcode.com/problems/recover-binary-search-tree/)             | [`hard/RecoverBinarySearchTree.java`](./binarysearchtree/hard/RecoverBinarySearchTree.java)          |
| 5 | Kth Largest Element in Stream | [703](https://leetcode.com/problems/kth-largest-element-in-a-stream/)       | [`easy/KthLargestElement.java`](./heap/medium/KthLargestElementInArray.java)                         |
| 6 | Median Finder                 | [295](https://leetcode.com/problems/find-median-from-data-stream/)          | [`hard/MedianFinder.java`](./miscellaneous/recent/MedianOfDataStream.java)                           |

### Graphs Easy

| # | Problem            | LeetCode                                                 | Code Link                                                             |
|---|--------------------|----------------------------------------------------------|-----------------------------------------------------------------------|
| 1 | Number of Islands  | [200](https://leetcode.com/problems/number-of-islands/)  | [`medium/NumberOfIslands.java`](./graphs/medium/NumberOfIslands.java) |
| 2 | Max Area of Island | [695](https://leetcode.com/problems/max-area-of-island/) | [`medium/MaxAreaOfIsland.java`](./graphs/easy/MaxAreaOfIsland.java)   |
| 3 | Clone Graph        | [133](https://leetcode.com/problems/clone-graph/)        | [`medium/CloneGraph.java`](./graphs/medium/CloneGraph.java)           |
| 4 | Walls and Gates    | [286](https://leetcode.com/problems/walls-and-gates/)    | [`medium/WallsAndGates.java`](./queues/hard/WallsAndGates.java)       |

### Graphs Medium

| #  | Problem                         | LeetCode                                                                                    | Code Link                                                                                      |
|----|---------------------------------|---------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| 1  | Course Schedule                 | [207](https://leetcode.com/problems/course-schedule/)                                       | [`medium/CourseSchedule.java`](./graphs/medium/CourseSchedule.java)                            |
| 2  | Course Schedule II              | [210](https://leetcode.com/problems/course-schedule-ii/)                                    | [`medium/CourseScheduleII.java`](./graphs/medium/CourseScheduleII.java)                        |
| 3  | Graph Valid Tree                | [261](https://leetcode.com/problems/graph-valid-tree/)                                      | [`medium/GraphValidTree.java`](./graphs/medium/GraphValidTree.java)                            |
| 4  | Number of Connected Components  | [323](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | [`medium/NumberOfConnectedComponents.java`](./unionfind/easy/NumberOfConnectedComponents.java) |
| 5  | Accounts Merge                  | [721](https://leetcode.com/problems/accounts-merge/)                                        | [`medium/AccountsMerge.java`](./unionfind/medium/AccountsMerge.java)                           |
| 6  | Pacific Atlantic Water Flow     | [417](https://leetcode.com/problems/pacific-atlantic-water-flow/)                           | [`medium/PacificAtlanticWaterFlow.java`](./graphs/medium/PacificAtlanticWaterFlow.java)        |
| 7  | Number of Distinct Islands      | [694](https://leetcode.com/problems/number-of-distinct-islands/)                            | [`medium/NumberOfDistinctIslands.java`](./grid/medium/NumberOfDistinctIslands.java)            |
| 8  | Rotting Oranges                 | [994](https://leetcode.com/problems/rotting-oranges/)                                       | [`medium/RottingOranges.java`](./graphs/medium/RottingOranges.java)                            |
| 9  | All Paths From Source to Target | [797](https://leetcode.com/problems/all-paths-from-source-to-target/)                       | [`medium/AllPathsSourceTarget.java`](./backtracking/medium/AllPathsFromSourceToTarget.java)    |
| 10 | Alien Dictionary                | [269](https://leetcode.com/problems/alien-dictionary/)                                      | [`hard/AlienDictionary.java`](./graphs/hard/AlienDictionary.java)                              |

### Graphs Hard

| # | Problem                 | LeetCode                                                                  | Code Link                                                                              |
|---|-------------------------|---------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| 1 | Word Ladder             | [127](https://leetcode.com/problems/word-ladder/)                         | [`hard/WordLadder.java`](./tries/hard/WordLadder.java)                                 |
| 2 | Word Ladder II          | [126](https://leetcode.com/problems/word-ladder-ii/)                      | [`hard/WordLadderII.java`](./queues/hard/WordLadderII.java)                            |
| 3 | Longest Increasing Path | [329](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | [`hard/LongestIncreasingPath.java`](./miscellaneous/recent/LongestIncreasingPath.java) |
| 4 | Minimum Height Trees    | [310](https://leetcode.com/problems/minimum-height-trees/)                | [`hard/MinimumHeightTrees.java`](./graphs/medium/MinimumHeightTrees.java)              |

### DP Easy

| # | Problem                  | LeetCode                                                           | Code Link                                                                         |
|---|--------------------------|--------------------------------------------------------------------|-----------------------------------------------------------------------------------|
| 1 | Climbing Stairs          | [70](https://leetcode.com/problems/climbing-stairs/)               | [`linear/basic/ClimbingStairs.java`](./dp/linear/basic/ClimbingStairs.java)       |
| 2 | Min Cost Climbing Stairs | [746](https://leetcode.com/problems/min-cost-climbing-stairs/)     | [`advanced/MinCostClimbingStairs.java`](./dp/advanced/MinCostClimbingStairs.java) |
| 3 | Fibonacci Number         | [509](https://leetcode.com/problems/fibonacci-number/)             | [`easy/Fibonacci.java`](./dp/linear/basic/FibonacciNumber.java)                   |
| 4 | Range Sum Query          | [303](https://leetcode.com/problems/range-sum-query-immutable/)    | [`easy/RangeSumQuery.java`](./dp/advanced/RangeSumQueryImmutable.java)            |
| 5 | Range Sum Query 2D       | [304](https://leetcode.com/problems/range-sum-query-2d-immutable/) | [`medium/RangeSumQuery2D.java`](./matrix/medium/RangeSumQuery2D.java)             |
| 6 | Counting Bits            | [338](https://leetcode.com/problems/counting-bits/)                | [`mathematical/CountingBits.java`](./dp/mathematical/CountingBits.java)           |

### DP Medium

| #  | Problem                         | LeetCode                                                              | Code Link                                                                                                     |
|----|---------------------------------|-----------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| 1  | House Robber                    | [198](https://leetcode.com/problems/house-robber/)                    | [`linear/optimization/HouseRobber.java`](./dp/linear/optimization/HouseRobber.java)                           |
| 2  | House Robber II                 | [213](https://leetcode.com/problems/house-robber-ii/)                 | [`linear/optimization/HouseRobberII.java`](./dp/linear/optimization/HouseRobberII.java)                       |
| 3  | Maximum Subarray                | [53](https://leetcode.com/problems/maximum-subarray/)                 | [`linear/sequence/MaximumSubarray.java`](./dp/linear/sequence/MaximumSubarray.java)                           |
| 4  | Maximum Product Subarray        | [152](https://leetcode.com/problems/maximum-product-subarray/)        | [`linear/sequence/MaximumProductSubarray.java`](./dp/linear/sequence/MaximumProductSubarray.java)             |
| 5  | Coin Change                     | [322](https://leetcode.com/problems/coin-change/)                     | [`knapsack/unbounded/CoinChange.java`](./dp/knapsack/unbounded/CoinChange.java)                               |
| 6  | Coin Change II                  | [518](https://leetcode.com/problems/coin-change-2/)                   | [`knapsack/unbounded/CoinChangeII.java`](./dp/knapsack/unbounded/CoinChangeII.java)                           |
| 7  | Longest Increasing Subsequence  | [300](https://leetcode.com/problems/longest-increasing-subsequence/)  | [`linear/sequence/LongestIncreasingSubsequence.java`](./dp/linear/sequence/LongestIncreasingSubsequence.java) |
| 8  | Decode Ways                     | [91](https://leetcode.com/problems/decode-ways/)                      | [`state_machine/DecodeWays.java`](./dp/state_machine/DecodeWays.java)                                         |
| 9  | Unique Paths                    | [62](https://leetcode.com/problems/unique-paths/)                     | [`grid/path_counting/UniquePaths.java`](./dp/grid/path_counting/UniquePaths.java)                             |
| 10 | Unique Paths II                 | [63](https://leetcode.com/problems/unique-paths-ii/)                  | [`grid/medium/UniquePathsII.java`](./grid/medium/UniquePathsII.java)                                          |
| 11 | Word Break                      | [139](https://leetcode.com/problems/word-break/)                      | [`advanced/WordBreak.java`](./dp/advanced/WordBreak.java)                                                     |
| 12 | Partition Equal Subset Sum      | [416](https://leetcode.com/problems/partition-equal-subset-sum/)      | [`knapsack/01/PartitionEqualSubsetSum.java`](./dp/knapsack/subset_sum/PartitionEqualSubsetSum.java)           |
| 13 | Minimum Path Sum                | [64](https://leetcode.com/problems/minimum-path-sum/)                 | [`grid/optimization/MinimumPathSum.java`](./dp/grid/optimization/MinimumPathSum.java)                         |
| 14 | Combination Sum IV              | [377](https://leetcode.com/problems/combination-sum-iv/)              | [`knapsack/unbounded/CombinationSumIV.java`](./dp/knapsack/unbounded/CombinationSumIV.java)                   |
| 15 | Longest Common Subsequence      | [1143](https://leetcode.com/problems/longest-common-subsequence/)     | [`string/subsequence/LongestCommonSubsequence.java`](./dp/string/subsequence/LongestCommonSubsequence.java)   |
| 16 | Best Time to Buy and Sell Stock | [121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | [`stock_trading/BestTimeToBuyAndSellStock.java`](./dp/stock_trading/BestTimeToBuyAndSellStock.java)           |

### DP Hard

| # | Problem                | LeetCode                                                         | Code Link                                                                                               |
|---|------------------------|------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| 1 | Edit Distance          | [72](https://leetcode.com/problems/edit-distance/)               | [`string/matching/EditDistance.java`](./dp/string/matching/EditDistance.java)                           |
| 2 | Burst Balloons         | [312](https://leetcode.com/problems/burst-balloons/)             | [`interval/BurstBalloons.java`](./dp/interval/BurstBalloons.java)                                       |
| 3 | Dungeon Game           | [174](https://leetcode.com/problems/dungeon-game/)               | [`advanced/DungeonGame.java`](./dp/advanced/DungeonGame.java)                                           |
| 4 | Regex Matching         | [10](https://leetcode.com/problems/regular-expression-matching/) | [`string/matching/RegularExpressionMatching.java`](./dp/string/matching/RegularExpressionMatching.java) |
| 5 | Wildcard Matching      | [44](https://leetcode.com/problems/wildcard-matching/)           | [`string/matching/WildcardMatching.java`](./dp/string/matching/WildcardMatching.java)                   |
| 6 | Russian Doll Envelopes | [354](https://leetcode.com/problems/russian-doll-envelopes/)     | [`advanced/RussianDollEnvelopes.java`](./sorting/hard/RussianDollEnvelopes.java)                        |

---

## 8-Week Study Plan

### Week 1-2: Arrays & Hash Maps (20 problems)

Focus on: Two pointers, prefix sums, hash tables

- Easy: Two Sum, Contains Duplicate, Valid Anagram, Move Zeroes
- Medium: Product of Array, 3Sum, Container, Group Anagrams
- Hard: Trapping Rain Water, Median of Two Arrays

### Week 3-4: Linked Lists & Stacks (15 problems)

Focus on: Pointer manipulation, monotonic stacks

- Easy: Reverse LL, Merge Lists, Valid Parentheses
- Medium: Add Two Numbers, LRU Cache, Daily Temps
- Hard: Merge k Lists, Largest Rectangle

### Week 5-6: Trees & Graphs (20 problems)

Focus on: DFS, BFS, tree properties, connectivity

- Easy: Invert Tree, Same Tree, Max Depth
- Medium: Level Order, Course Schedule, Clone Graph
- Hard: Binary Tree Max Path, Word Ladder

### Week 7-8: Dynamic Programming (15 problems)

Focus on: State transitions, memoization, optimization

- Easy: Climbing Stairs, Fibonacci
- Medium: Coin Change, House Robber, LIS
- Hard: Edit Distance, Dungeon Game, Regex Match

---

## Learning Resources

- **LeetCode**: https://leetcode.com/
- **NeetCode**: https://neetcode.io/
- **LeetCode Discuss**: https://leetcode.com/discuss/
- **GeeksforGeeks**: https://www.geeksforgeeks.org/
- **InterviewBit**: https://www.interviewbit.com/

---

**Last Updated**: December 2025
**Total Problems**: 250+


