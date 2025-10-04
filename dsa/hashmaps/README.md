# Hash Map & Set Problems

## Problem List (Grouped by Pattern/Algorithm)

### Basic Hashing & Frequency Counting
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Two Sum | [LeetCode 1](https://leetcode.com/problems/two-sum/) | [TwoSum.java](./medium/TwoSum.java) |
| Valid Anagram | [LeetCode 242](https://leetcode.com/problems/valid-anagram/) | [ValidAnagram.java](./medium/ValidAnagram.java) |
| Group Anagrams | [LeetCode 49](https://leetcode.com/problems/group-anagrams/) | [GroupAnagrams.java](./medium/GroupAnagrams.java) |
| First Unique Character in a String | [LeetCode 387](https://leetcode.com/problems/first-unique-character-in-a-string/) | [FindFirstUniqueCharacter.java](./medium/FindFirstUniqueCharacter.java) |
| Majority Element | [LeetCode 169](https://leetcode.com/problems/majority-element/) | [FindMajorityElement.java](./medium/FindMajorityElement.java) |
| Intersection of Two Arrays | [LeetCode 349](https://leetcode.com/problems/intersection-of-two-arrays/) | [FindIntersectionOfTwoArrays.java](./medium/FindIntersectionOfTwoArrays.java) |
| Intersection of Two Arrays II | [LeetCode 350](https://leetcode.com/problems/intersection-of-two-arrays-ii/) | [FindIntersectionOfTwoArraysII.java](./medium/FindIntersectionOfTwoArraysII.java) |
| Missing Number | [LeetCode 268](https://leetcode.com/problems/missing-number/) | [FindMissingNumber.java](./medium/FindMissingNumber.java) |
| Find All Duplicates in an Array | [LeetCode 442](https://leetcode.com/problems/find-all-duplicates-in-an-array/) | [FindAllDuplicatesInArray.java](./medium/FindAllDuplicatesInArray.java) |
| Longest Palindrome | [LeetCode 409](https://leetcode.com/problems/longest-palindrome/) | [FindLongestPalindrome.java](./medium/FindLongestPalindrome.java) |

### Sliding Window
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Longest Substring Without Repeating Characters | [LeetCode 3](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | [FindLongestSubstringWithoutRepeatingCharacters.java](./medium/FindLongestSubstringWithoutRepeatingCharacters.java) |
| Minimum Window Substring | [LeetCode 76](https://leetcode.com/problems/minimum-window-substring/) | [FindMinimumWindowSubstring.java](./medium/FindMinimumWindowSubstring.java) |
| Longest Repeating Character Replacement | [LeetCode 424](https://leetcode.com/problems/longest-repeating-character-replacement/) | [FindLongestSubstringWithSameLettersAfterReplacement.java](./hard/FindLongestSubstringWithSameLettersAfterReplacement.java) |
| Find All Anagrams in a String | [LeetCode 438](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | [FindAllAnagramsInAString.java](./medium/FindAllAnagramsInAString.java) |
| Substring with Concatenation of All Words | [LeetCode 30](https://leetcode.com/problems/substring-with-concatenation-of-all-words/) | [FindAllSubstringsWithConcatenationOfAllWords.java](./medium/FindAllSubstringsWithConcatenationOfAllWords.java) |
| Longest Substring with At Most K Distinct Characters | [LeetCode 340](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/) | [FindLongestSubstringWithAtMostKDistinctCharacters.java](./hard/FindLongestSubstringWithAtMostKDistinctCharacters.java) |
| Longest Substring with At Most Two Distinct Characters | [LeetCode 159](https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/) | [LongestSubstringWithAtMostTwoDistinctCharacters.java](./hard/LongestSubstringWithAtMostTwoDistinctCharacters.java) |
| Minimum Size Subarray Sum | [LeetCode 209](https://leetcode.com/problems/minimum-size-subarray-sum/) | [SmallestSubarrayWithSumGreaterThanK.java](./hard/SmallestSubarrayWithSumGreaterThanK.java) |
| Longest Substring with At Least K Repeating Characters | [LeetCode 395](https://leetcode.com/problems/longest-substring-with-at-least-k-repeating-characters/) | [LongestSubstringWithKRepeatingCharacters.java](./hard/LongestSubstringWithKRepeatingCharacters.java) |

### Prefix Sum
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Subarray Sum Equals K | [LeetCode 560](https://leetcode.com/problems/subarray-sum-equals-k/) | [SubarraySumEqualsK.java](./medium/SubarraySumEqualsK.java) |
| Find All Subarrays with Zero Sum | [Variation of 560](https://leetcode.com/problems/subarray-sum-equals-k/) | [FindAllSubarraysWithZeroSum.java](./hard/FindAllSubarraysWithZeroSum.java) |
| Find Subarrays With Equal Sum | [LeetCode 2395](https://leetcode.com/problems/find-subarrays-with-equal-sum/) | [FindSubarraysWithEqualSum.java](./medium/FindSubarraysWithEqualSum.java) |

### Heaps (Priority Queues)
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Top K Frequent Elements | [LeetCode 347](https://leetcode.com/problems/top-k-frequent-elements/) | [TopKFrequentElements.java](./medium/TopKFrequentElements.java) |

### Advanced/Multi-concept
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Longest Consecutive Sequence | [LeetCode 128](https://leetcode.com/problems/longest-consecutive-sequence/) | [LongestConsecutiveSequence.java](./medium/LongestConsecutiveSequence.java) |
| Find Duplicate Subtrees | [LeetCode 652](https://leetcode.com/problems/find-duplicate-subtrees/) | [FindDuplicateSubtrees.java](./medium/FindDuplicateSubtrees.java) |

## Company Tags

- **Google**: 1, 3, 49, 76, 128, 169, 242, 340, 347, 424, 560
- **Facebook**: 1, 3, 49, 76, 128, 169, 242, 347, 438, 560
- **Amazon**: 1, 3, 49, 76, 128, 169, 242, 347, 424, 438, 560
- **Microsoft**: 1, 3, 49, 76, 128, 169, 242, 347, 424, 560
- **Apple**: 1, 3, 49, 76, 128, 169, 242, 347, 424, 560

## Patterns

- **Frequency Counting**: Using HashMaps or arrays to count occurrences. (Two Sum, Valid Anagram, Majority Element)
- **Sliding Window**: Using two pointers to maintain a "window" over a data structure. (Longest Substring..., Minimum Window..., Anagrams in String)
- **Prefix Sum**: Pre-calculating sums to quickly find the sum of a subarray. (Subarray Sum Equals K)
- **Set for Uniqueness**: Using HashSets to keep track of unique elements or check for existence in O(1). (Longest Consecutive Sequence, Intersection)
- **Heap for Ordering**: Using PriorityQueues to find top K elements. (Top K Frequent Elements)
- **Serialization**: Converting a complex structure (like a tree) into a string to use as a HashMap key. (Duplicate Subtrees)
