package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 978: Longest Turbulent Subarray
 * https://leetcode.com/problems/longest-turbulent-subarray/
 *
 * Description:
 * Given an integer array arr, return the length of a maximum size turbulent
 * subarray of arr.
 * A subarray is turbulent if the comparison sign flips between each adjacent
 * pair of elements in the subarray.
 *
 * Constraints:
 * - 1 <= arr.length <= 4 * 10^4
 * - 0 <= arr[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - What if we need to find all turbulent subarrays?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class LongestTurbulentSubarray {

    // Approach 1: Two Pointers - O(n) time, O(1) space
    public int maxTurbulenceSize(int[] arr) {
        int n = arr.length;
        if (n <= 1)
            return n;

        int maxLength = 1;
        int left = 0;

        while (left < n - 1) {
            int right = left;

            // Skip equal elements
            while (right < n - 1 && arr[right] == arr[right + 1]) {
                right++;
            }

            if (right == n - 1) {
                maxLength = Math.max(maxLength, right - left + 1);
                break;
            }

            // Find turbulent sequence starting from right
            while (right < n - 1 &&
                    ((arr[right] < arr[right + 1] && right == left) ||
                            (arr[right] > arr[right + 1] && right == left) ||
                            (arr[right - 1] < arr[right] && arr[right] > arr[right + 1]) ||
                            (arr[right - 1] > arr[right] && arr[right] < arr[right + 1]))) {
                right++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
            left = right;
        }

        return maxLength;
    }

    // Approach 2: DP with States - O(n) time, O(1) space
    public int maxTurbulenceSizeDP(int[] arr) {
        int n = arr.length;
        if (n <= 1)
            return n;

        int inc = 1; // Length ending with increasing trend
        int dec = 1; // Length ending with decreasing trend
        int maxLength = 1;

        for (int i = 1; i < n; i++) {
            if (arr[i] > arr[i - 1]) {
                inc = dec + 1;
                dec = 1;
            } else if (arr[i] < arr[i - 1]) {
                dec = inc + 1;
                inc = 1;
            } else {
                inc = dec = 1;
            }

            maxLength = Math.max(maxLength, Math.max(inc, dec));
        }

        return maxLength;
    }

    // Approach 3: Sliding Window - O(n) time, O(1) space
    public int maxTurbulenceSizeWindow(int[] arr) {
        int n = arr.length;
        if (n <= 1)
            return n;

        int maxLength = 1;
        int start = 0;

        for (int end = 1; end < n; end++) {
            boolean validTurbulent = true;

            if (end == start + 1) {
                if (arr[start] == arr[end]) {
                    start = end;
                }
            } else {
                int prev = compare(arr[end - 2], arr[end - 1]);
                int curr = compare(arr[end - 1], arr[end]);

                if (prev == 0 || curr == 0 || prev == curr) {
                    start = end - 1;
                    if (arr[end - 1] == arr[end]) {
                        start = end;
                    }
                }
            }

            maxLength = Math.max(maxLength, end - start + 1);
        }

        return maxLength;
    }

    private int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    // Approach 4: Greedy - O(n) time, O(1) space
    public int maxTurbulenceSizeGreedy(int[] arr) {
        int n = arr.length;
        if (n <= 1)
            return n;

        int maxLength = 1;
        int currentLength = 1;

        for (int i = 1; i < n; i++) {
            if (arr[i] == arr[i - 1]) {
                currentLength = 1;
            } else if (i == 1 ||
                    (arr[i - 2] < arr[i - 1]) == (arr[i - 1] < arr[i])) {
                currentLength = 2;
            } else {
                currentLength++;
            }

            maxLength = Math.max(maxLength, currentLength);
        }

        return maxLength;
    }

    // Approach 5: Get All Turbulent Subarrays - O(n^2) time, O(n) space
    public java.util.List<int[]> getAllTurbulentSubarrays(int[] arr) {
        java.util.List<int[]> result = new java.util.ArrayList<>();
        int n = arr.length;

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (isTurbulent(arr, i, j)) {
                    result.add(new int[] { i, j });
                }
            }
        }

        return result;
    }

    private boolean isTurbulent(int[] arr, int start, int end) {
        if (end - start < 2)
            return true;

        for (int i = start + 1; i < end; i++) {
            int prev = compare(arr[i - 1], arr[i]);
            int next = compare(arr[i], arr[i + 1]);

            if (prev == 0 || next == 0 || prev == next) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        LongestTurbulentSubarray solution = new LongestTurbulentSubarray();

        System.out.println("=== Longest Turbulent Subarray Test Cases ===");

        // Test Case 1: Example from problem
        int[] arr1 = { 9, 4, 2, 10, 7, 8, 8, 1, 9 };
        System.out.println("Test 1 - Array: " + Arrays.toString(arr1));
        System.out.println("Two Pointers: " + solution.maxTurbulenceSize(arr1));
        System.out.println("DP: " + solution.maxTurbulenceSizeDP(arr1));
        System.out.println("Sliding Window: " + solution.maxTurbulenceSizeWindow(arr1));
        System.out.println("Greedy: " + solution.maxTurbulenceSizeGreedy(arr1));
        System.out.println("Expected: 5\n");

        // Test Case 2: All equal
        int[] arr2 = { 4, 4, 4, 4 };
        System.out.println("Test 2 - Array: " + Arrays.toString(arr2));
        System.out.println("DP: " + solution.maxTurbulenceSizeDP(arr2));
        System.out.println("Expected: 1\n");

        // Test Case 3: Already turbulent
        int[] arr3 = { 100, 7, 200, 5, 300 };
        System.out.println("Test 3 - Array: " + Arrays.toString(arr3));
        System.out.println("DP: " + solution.maxTurbulenceSizeDP(arr3));
        System.out.println("Expected: 5\n");

        performanceTest();
    }

    private static void performanceTest() {
        LongestTurbulentSubarray solution = new LongestTurbulentSubarray();

        int[] largeArray = new int[40000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000000000);
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxTurbulenceSize(largeArray);
        long end = System.nanoTime();
        System.out.println("Two Pointers: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxTurbulenceSizeDP(largeArray);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxTurbulenceSizeGreedy(largeArray);
        end = System.nanoTime();
        System.out.println("Greedy: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
