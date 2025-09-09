package binarysearch.hard;

/**
 * LeetCode 1095: Find in Mountain Array
 * https://leetcode.com/problems/find-in-mountain-array/
 *
 * Description:
 * You may recall that an array arr is a mountain array if and only if:
 * - arr.length >= 3
 * - There exists some i with 0 < i < arr.length - 1 such that:
 * - arr[0] < arr[1] < ... < arr[i - 1] < arr[i]
 * - arr[i] > arr[i + 1] > ... > arr[arr.length - 1]
 * Given a mountain array mountainArr, return the minimum index such that
 * mountainArr.get(index) == target.
 * If such an index does not exist, return -1.
 * You cannot access the mountain array directly. You may only access the array
 * using a MountainArray interface.
 * Calls to MountainArray.get(index) are expensive, so try to minimize the
 * number of calls.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg
 * Difficulty: Hard
 * Asked: 2023-2024 (Medium Frequency)
 *
 * Constraints:
 * - 3 <= mountain_arr.length() <= 10^4
 * - 0 <= target <= 10^9
 * - 0 <= mountain_arr.get(i) <= 10^9
 * - MountainArray.get(index) returns the element of the array at index
 * - MountainArray.length() returns the length of the array
 * - Submissions making more than 100 calls to MountainArray.get will be judged
 * Wrong Answer
 *
 * Follow-ups:
 * - Can you solve this with exactly 3 binary searches?
 * - What if there are multiple targets? Find all indices?
 * - How would you handle a valley array (opposite of mountain)?
 */
public class FindInMountainArray {

    // MountainArray interface (provided by LeetCode)
    interface MountainArray {
        public int get(int index);

        public int length();
    }

    // Mock implementation for testing
    static class MockMountainArray implements MountainArray {
        private int[] arr;
        private int callCount = 0;

        public MockMountainArray(int[] arr) {
            this.arr = arr;
        }

        public int get(int index) {
            callCount++;
            System.out.println("API call #" + callCount + ": get(" + index + ") = " + arr[index]);
            return arr[index];
        }

        public int length() {
            return arr.length;
        }

        public int getCallCount() {
            return callCount;
        }
    }

    // Three binary searches approach - O(log n) time, minimal API calls
    public int findInMountainArray(int target, MountainArray mountainArr) {
        int n = mountainArr.length();

        // Step 1: Find the peak index
        int peakIndex = findPeak(mountainArr);

        // Step 2: Search in the ascending part (left side)
        int leftResult = binarySearchAscending(mountainArr, target, 0, peakIndex);
        if (leftResult != -1) {
            return leftResult; // Return the leftmost occurrence
        }

        // Step 3: Search in the descending part (right side)
        return binarySearchDescending(mountainArr, target, peakIndex + 1, n - 1);
    }

    private int findPeak(MountainArray mountainArr) {
        int left = 0;
        int right = mountainArr.length() - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (mountainArr.get(mid) < mountainArr.get(mid + 1)) {
                // Peak is on the right side
                left = mid + 1;
            } else {
                // Peak is on the left side (including mid)
                right = mid;
            }
        }

        return left;
    }

    private int binarySearchAscending(MountainArray mountainArr, int target, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = mountainArr.get(mid);

            if (midVal == target) {
                return mid;
            } else if (midVal < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    private int binarySearchDescending(MountainArray mountainArr, int target, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = mountainArr.get(mid);

            if (midVal == target) {
                return mid;
            } else if (midVal > target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    // Optimized version with caching to minimize API calls
    public int findInMountainArrayCached(int target, MountainArray mountainArr) {
        java.util.Map<Integer, Integer> cache = new java.util.HashMap<>();
        return findInMountainArrayCachedHelper(target, mountainArr, cache);
    }

    private int findInMountainArrayCachedHelper(int target, MountainArray mountainArr,
            java.util.Map<Integer, Integer> cache) {
        int n = mountainArr.length();

        // Find peak with caching
        int peakIndex = findPeakCached(mountainArr, cache);

        // Search left side
        int leftResult = binarySearchAscendingCached(mountainArr, target, 0, peakIndex, cache);
        if (leftResult != -1) {
            return leftResult;
        }

        // Search right side
        return binarySearchDescendingCached(mountainArr, target, peakIndex + 1, n - 1, cache);
    }

    private int get(MountainArray mountainArr, int index, java.util.Map<Integer, Integer> cache) {
        return cache.computeIfAbsent(index, mountainArr::get);
    }

    private int findPeakCached(MountainArray mountainArr, java.util.Map<Integer, Integer> cache) {
        int left = 0;
        int right = mountainArr.length() - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (get(mountainArr, mid, cache) < get(mountainArr, mid + 1, cache)) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private int binarySearchAscendingCached(MountainArray mountainArr, int target, int left, int right,
            java.util.Map<Integer, Integer> cache) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = get(mountainArr, mid, cache);

            if (midVal == target) {
                return mid;
            } else if (midVal < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    private int binarySearchDescendingCached(MountainArray mountainArr, int target, int left, int right,
            java.util.Map<Integer, Integer> cache) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = get(mountainArr, mid, cache);

            if (midVal == target) {
                return mid;
            } else if (midVal > target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    // Find all occurrences of target
    public java.util.List<Integer> findAllInMountainArray(int target, MountainArray mountainArr) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        java.util.Map<Integer, Integer> cache = new java.util.HashMap<>();
        int n = mountainArr.length();

        int peakIndex = findPeakCached(mountainArr, cache);

        // Find all in ascending part
        java.util.List<Integer> leftResults = findAllAscending(mountainArr, target, 0, peakIndex, cache);
        result.addAll(leftResults);

        // Find all in descending part
        java.util.List<Integer> rightResults = findAllDescending(mountainArr, target, peakIndex + 1, n - 1, cache);
        result.addAll(rightResults);

        result.sort(Integer::compareTo);
        return result;
    }

    private java.util.List<Integer> findAllAscending(MountainArray mountainArr, int target, int left, int right,
            java.util.Map<Integer, Integer> cache) {
        java.util.List<Integer> result = new java.util.ArrayList<>();

        // Find first occurrence
        int first = findFirstAscending(mountainArr, target, left, right, cache);
        if (first == -1)
            return result;

        // Find all consecutive occurrences
        for (int i = first; i <= right && get(mountainArr, i, cache) == target; i++) {
            result.add(i);
        }

        return result;
    }

    private java.util.List<Integer> findAllDescending(MountainArray mountainArr, int target, int left, int right,
            java.util.Map<Integer, Integer> cache) {
        java.util.List<Integer> result = new java.util.ArrayList<>();

        // Find first occurrence
        int first = findFirstDescending(mountainArr, target, left, right, cache);
        if (first == -1)
            return result;

        // Find all consecutive occurrences
        for (int i = first; i <= right && get(mountainArr, i, cache) == target; i++) {
            result.add(i);
        }

        return result;
    }

    private int findFirstAscending(MountainArray mountainArr, int target, int left, int right,
            java.util.Map<Integer, Integer> cache) {
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = get(mountainArr, mid, cache);

            if (midVal == target) {
                result = mid;
                right = mid - 1; // Continue searching left for first occurrence
            } else if (midVal < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    private int findFirstDescending(MountainArray mountainArr, int target, int left, int right,
            java.util.Map<Integer, Integer> cache) {
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = get(mountainArr, mid, cache);

            if (midVal == target) {
                result = mid;
                right = mid - 1; // Continue searching left for first occurrence
            } else if (midVal > target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Validation method to check if array is mountain
    public boolean isMountainArray(MountainArray mountainArr) {
        int n = mountainArr.length();
        if (n < 3)
            return false;

        int peakIndex = findPeak(mountainArr);

        // Peak should not be at the ends
        return peakIndex > 0 && peakIndex < n - 1;
    }

    public static void main(String[] args) {
        FindInMountainArray solution = new FindInMountainArray();

        // Test Case 1: [1,2,3,4,5,3,1], target = 3
        MockMountainArray mountain1 = new MockMountainArray(new int[] { 1, 2, 3, 4, 5, 3, 1 });
        System.out.println("Test 1 - Target 3:");
        int result1 = solution.findInMountainArray(3, mountain1);
        System.out.println("Result: " + result1 + " (Expected: 2)");
        System.out.println("API calls: " + mountain1.getCallCount() + "\n");

        // Test Case 2: [0,1,2,4,2,1], target = 3
        MockMountainArray mountain2 = new MockMountainArray(new int[] { 0, 1, 2, 4, 2, 1 });
        System.out.println("Test 2 - Target 3:");
        int result2 = solution.findInMountainArray(3, mountain2);
        System.out.println("Result: " + result2 + " (Expected: -1)");
        System.out.println("API calls: " + mountain2.getCallCount() + "\n");

        // Test Case 3: [1,2,3,4,5,3,1], target = 5
        MockMountainArray mountain3 = new MockMountainArray(new int[] { 1, 2, 3, 4, 5, 3, 1 });
        System.out.println("Test 3 - Target 5 (peak):");
        int result3 = solution.findInMountainArray(5, mountain3);
        System.out.println("Result: " + result3 + " (Expected: 4)");
        System.out.println("API calls: " + mountain3.getCallCount() + "\n");

        // Test Case 4: [1,2,3,4,5,3,1], target = 1
        MockMountainArray mountain4 = new MockMountainArray(new int[] { 1, 2, 3, 4, 5, 3, 1 });
        System.out.println("Test 4 - Target 1 (multiple occurrences):");
        int result4 = solution.findInMountainArray(1, mountain4);
        System.out.println("Result: " + result4 + " (Expected: 0)");
        System.out.println("API calls: " + mountain4.getCallCount() + "\n");

        // Test Case 5: Cached version
        MockMountainArray mountain5 = new MockMountainArray(new int[] { 1, 2, 3, 4, 5, 3, 1 });
        System.out.println("Test 5 - Cached version:");
        int result5 = solution.findInMountainArrayCached(3, mountain5);
        System.out.println("Result: " + result5 + " (Expected: 2)");
        System.out.println("API calls: " + mountain5.getCallCount() + "\n");

        // Test Case 6: Large mountain array
        int[] largeMountain = new int[1000];
        for (int i = 0; i < 500; i++) {
            largeMountain[i] = i;
        }
        for (int i = 500; i < 1000; i++) {
            largeMountain[i] = 1000 - i - 1;
        }
        MockMountainArray mountain6 = new MockMountainArray(largeMountain);
        System.out.println("Test 6 - Large mountain array:");
        int result6 = solution.findInMountainArray(250, mountain6);
        System.out.println("Result: " + result6 + " (Expected: 250)");
        System.out.println("API calls: " + mountain6.getCallCount() + "\n");

        // Test Case 7: Find all occurrences
        MockMountainArray mountain7 = new MockMountainArray(new int[] { 1, 2, 3, 3, 5, 3, 3, 1 });
        System.out.println("Test 7 - Find all occurrences of 3:");
        java.util.List<Integer> allResults = solution.findAllInMountainArray(3, mountain7);
        System.out.println("Results: " + allResults + " (Expected: [2, 3, 5, 6])");
        System.out.println("API calls: " + mountain7.getCallCount() + "\n");

        // Test Case 8: Validation
        MockMountainArray mountain8 = new MockMountainArray(new int[] { 1, 2, 3, 4, 5, 3, 1 });
        System.out.println("Test 8 - Is mountain array:");
        boolean isValid = solution.isMountainArray(mountain8);
        System.out.println("Is valid mountain: " + isValid + " (Expected: true)");

        // Test Case 9: Edge cases
        MockMountainArray mountain9 = new MockMountainArray(new int[] { 0, 5, 3, 1 });
        System.out.println("\nTest 9 - Small mountain:");
        int result9 = solution.findInMountainArray(1, mountain9);
        System.out.println("Result: " + result9 + " (Expected: 3)");
        System.out.println("API calls: " + mountain9.getCallCount());
    }
}
