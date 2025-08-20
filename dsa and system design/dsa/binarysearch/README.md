# Binary Search Problems

This directory contains binary search problems from LeetCode, organized by difficulty level.

## Problems List

### Easy (10 problems)
1. [Binary Search (704)](easy/BinarySearch.java)
2. [Search Insert Position (35)](easy/SearchInsertPosition.java)
3. [Sqrt(x) (69)](easy/SqrtX.java)
4. [First Bad Version (278)](easy/FirstBadVersion.java)
5. [Valid Perfect Square (367)](easy/ValidPerfectSquare.java)
6. [Arranging Coins (441)](easy/ArrangingCoins.java)
7. [Find Smallest Letter Greater Than Target (744)](easy/FindSmallestLetterGreaterThanTarget.java)
8. [Guess Number Higher or Lower (374)](easy/GuessNumberHigherOrLower.java)
9. [Binary Search Extended](easy/BinarySearchExtended.java)
10. [First Bad Version Extended](easy/FirstBadVersionExtended.java)

### Medium (14 problems)
11. [Find First and Last Position of Element in Sorted Array (34)](medium/FindFirstAndLastPosition.java)
12. [Search in Rotated Sorted Array (33)](medium/SearchInRotatedSortedArray.java)
13. [Search in Rotated Sorted Array II (81)](medium/SearchInRotatedSortedArrayII.java)
14. [Find Minimum in Rotated Sorted Array (153)](medium/FindMinimumInRotatedSortedArray.java)
15. [Find Peak Element (162)](medium/FindPeakElement.java)
16. [Single Element in a Sorted Array (540)](medium/SingleElementInSortedArray.java)
17. [Search a 2D Matrix II (240)](medium/SearchA2DMatrixII.java)
18. [Koko Eating Bananas (875)](medium/KokoEatingBananas.java)
19. [Capacity To Ship Packages Within D Days (1011)](medium/CapacityToShipPackagesWithinDDays.java)
20. [Time Based Key-Value Store (981)](medium/TimeBasedKeyValueStore.java)
21. [Find First and Last Position Extended](medium/FindFirstAndLastPositionExtended.java)
22. [Find Minimum in Rotated Sorted Array Extended](medium/FindMinimumInRotatedSortedArrayExtended.java)
23. [Find Peak Element Extended](medium/FindPeakElementExtended.java)
24. [Koko Eating Bananas Extended](medium/KokoEatingBananasExtended.java)

### Hard (6 problems)
25. [Median of Two Sorted Arrays (4)](hard/MedianOfTwoSortedArrays.java)
26. [Split Array Largest Sum (410)](hard/SplitArrayLargestSum.java)
27. [Find in Mountain Array (1095)](hard/FindInMountainArray.java)
28. [Divide Chocolate (1231)](hard/DivideChocolate.java)
29. [Super Egg Dropping (887)](hard/SuperEggDropping.java)
30. [Merge k Sorted Lists (23)](hard/MergeKSortedLists.java)

## Problem Categories

### Classic Binary Search
- Binary Search (704), Search Insert Position (35), Find Smallest Letter Greater Than Target (744), Guess Number Higher or Lower (374)

### Binary Search on Answer
- Sqrt(x) (69), Koko Eating Bananas (875), Capacity To Ship Packages Within D Days (1011), Split Array Largest Sum (410), Divide Chocolate (1231)

### Rotated Sorted Arrays
- Search in Rotated Sorted Array (33), Search in Rotated Sorted Array II (81), Find Minimum in Rotated Sorted Array (153)

### Find Range/Boundaries
- Find First and Last Position (34), First Bad Version (278)

### Peak Finding
- Find Peak Element (162), Find in Mountain Array (1095)

### Sorted Matrix/2D Arrays
- Search a 2D Matrix II (240), Median of Two Sorted Arrays (4)

### Mathematical Applications
- Sqrt(x) (69), Valid Perfect Square (367), Arranging Coins (441)

### Single Element/Unique Finding
- Single Element in a Sorted Array (540)

### Data Structure Design
- Time Based Key-Value Store (981)

### Advanced Applications
- Super Egg Dropping (887), Merge k Sorted Lists (23)

## Key Concepts

### 1. Standard Binary Search Template
```java
int left = 0, right = nums.length - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) left = mid + 1;
    else right = mid - 1;
}
```

### 2. Binary Search on Answer
```java
int left = minPossible, right = maxPossible;
while (left < right) {
    int mid = left + (right - left) / 2;
    if (isValid(mid)) right = mid;
    else left = mid + 1;
}
```

### 3. Find First/Last Occurrence
```java
// Find first occurrence
while (left < right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] >= target) right = mid;
    else left = mid + 1;
}
```

### 4. Rotated Array Search
```java
// Determine which part is sorted
if (nums[left] <= nums[mid]) {
    // Left part is sorted
    if (target >= nums[left] && target < nums[mid]) {
        right = mid - 1;
    } else {
        left = mid + 1;
    }
} else {
    // Right part is sorted
    // Similar logic for right part
}
```

## Company Tags

### Most Frequently Asked
- **Google**: 15 problems
- **Microsoft**: 14 problems
- **Amazon**: 13 problems
- **Facebook (Meta)**: 12 problems
- **Apple**: 10 problems

### Other Companies
- Bloomberg, Adobe, Uber, LinkedIn, Netflix, Airbnb, DoorDash, ByteDance, Twitter, Spotify

## Difficulty Distribution
- **Easy**: 10 problems (33%)
- **Medium**: 14 problems (47%)
- **Hard**: 6 problems (20%)

## Time Complexity Patterns
- **O(log n)**: Most binary search problems
- **O(log(min(m,n)))**: Median of Two Sorted Arrays
- **O(m + n)**: Search a 2D Matrix II
- **O(n * log(max_value))**: Binary search on answer problems

## Study Path Recommendations

### Beginner Level (Start Here)
1. Binary Search (704)
2. Search Insert Position (35)
3. Sqrt(x) (69)
4. First Bad Version (278)

### Intermediate Level
1. Find First and Last Position (34)
2. Search in Rotated Sorted Array (33)
3. Find Peak Element (162)
4. Koko Eating Bananas (875)

### Advanced Level
1. Median of Two Sorted Arrays (4)
2. Split Array Largest Sum (410)
3. Find in Mountain Array (1095)
4. Super Egg Dropping (887)

## Implementation Features

### Each Problem Includes:
- ✅ Multiple solution approaches (5-8 different methods)
- ✅ Comprehensive test cases with edge cases
- ✅ Company tags and frequency information
- ✅ Clickable LeetCode URLs
- ✅ Time and space complexity analysis
- ✅ Follow-up questions and variations
- ✅ Performance comparisons
- ✅ Detailed comments and explanations

### Code Quality Standards:
- Clean, readable implementations
- Proper error handling
- Edge case coverage
- Performance optimizations
- Interview-ready format
- Extensive validation methods

## Recent Updates (2023-2024)
- Added comprehensive implementations for high-frequency interview problems
- Enhanced with multiple solution approaches per problem
- Improved test coverage and edge case handling
- Added performance benchmarking and comparisons
- Updated company tags based on latest interview trends

## Notes
- All problems are implemented with O(log n) or better time complexity where possible
- Binary search variations are covered extensively
- Focus on interview preparation with real company questions
- Each file contains 200-400 lines of comprehensive implementation
