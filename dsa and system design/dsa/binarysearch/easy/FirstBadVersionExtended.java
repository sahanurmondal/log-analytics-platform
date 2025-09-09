package binarysearch.easy;

/**
 * LeetCode 278: First Bad Version
 * https://leetcode.com/problems/first-bad-version/
 *
 * Description:
 * You are a product manager and currently leading a team to develop a new
 * product.
 * Unfortunately, the latest version of your product fails the quality check.
 * Since each version is developed based on the previous version, all the
 * versions after a bad version are also bad.
 * Suppose you have n versions [1, 2, ..., n] and you want to find out the first
 * bad one,
 * which causes all the following ones to be bad.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Uber,
 * Pinterest
 * Difficulty: Easy
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= bad <= n <= 2^31 - 1
 *
 * Follow-ups:
 * - What if API calls are expensive and you want to minimize them?
 * - How would you handle if the API might return inconsistent results?
 * - Can you solve this with ternary search?
 */
public class FirstBadVersionExtended {

    private int firstBadVersion = 4; // Simulated first bad version for testing

    // The isBadVersion API (given by LeetCode)
    private boolean isBadVersion(int version) {
        return version >= firstBadVersion;
    }

    // Binary Search solution - O(log n) time
    public int firstBadVersion(int n) {
        int left = 1;
        int right = n;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (isBadVersion(mid)) {
                // Current version is bad, so first bad is at mid or before
                right = mid;
            } else {
                // Current version is good, so first bad is after mid
                left = mid + 1;
            }
        }

        return left; // left == right at this point
    }

    // Alternative implementation with left <= right
    public int firstBadVersionAlternative(int n) {
        int left = 1;
        int right = n;
        int result = n;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (isBadVersion(mid)) {
                result = mid; // This could be the answer
                right = mid - 1; // Search for earlier bad version
            } else {
                left = mid + 1; // Search in the right half
            }
        }

        return result;
    }

    // Recursive approach
    public int firstBadVersionRecursive(int n) {
        return firstBadVersionRecursiveHelper(1, n);
    }

    private int firstBadVersionRecursiveHelper(int left, int right) {
        if (left >= right) {
            return left;
        }

        int mid = left + (right - left) / 2;

        if (isBadVersion(mid)) {
            return firstBadVersionRecursiveHelper(left, mid);
        } else {
            return firstBadVersionRecursiveHelper(mid + 1, right);
        }
    }

    // Exponential search + Binary search (for very large n)
    public int firstBadVersionExponential(int n) {
        // First find the range using exponential search
        int bound = 1;
        while (bound <= n && !isBadVersion(bound)) {
            bound *= 2;
        }

        // Binary search in the found range
        int left = bound / 2;
        int right = Math.min(bound, n);

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (isBadVersion(mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    // API call counter for testing optimization
    private int apiCalls = 0;

    private boolean isBadVersionWithCounter(int version) {
        apiCalls++;
        return version >= firstBadVersion;
    }

    public int firstBadVersionOptimized(int n) {
        apiCalls = 0;
        int left = 1;
        int right = n;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (isBadVersionWithCounter(mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        System.out.println("API calls made: " + apiCalls);
        return left;
    }

    public static void main(String[] args) {
        FirstBadVersionExtended solution = new FirstBadVersionExtended();

        // Test Case 1: n = 5, bad = 4
        solution.firstBadVersion = 4;
        System.out.println(solution.firstBadVersion(5)); // Expected: 4

        // Test Case 2: n = 1, bad = 1
        solution.firstBadVersion = 1;
        System.out.println(solution.firstBadVersion(1)); // Expected: 1

        // Test Case 3: n = 10, bad = 1 (all bad)
        solution.firstBadVersion = 1;
        System.out.println(solution.firstBadVersion(10)); // Expected: 1

        // Test Case 4: n = 10, bad = 10 (only last is bad)
        solution.firstBadVersion = 10;
        System.out.println(solution.firstBadVersion(10)); // Expected: 10

        // Test alternative implementation
        solution.firstBadVersion = 4;
        System.out.println("Alternative: " + solution.firstBadVersionAlternative(5)); // Expected: 4

        // Test recursive version
        System.out.println("Recursive: " + solution.firstBadVersionRecursive(5)); // Expected: 4

        // Test optimized version with API call counter
        System.out.println("Optimized: " + solution.firstBadVersionOptimized(100)); // Expected: 4

        // Test exponential search version
        System.out.println("Exponential: " + solution.firstBadVersionExponential(100)); // Expected: 4

        // Large test case
        solution.firstBadVersion = 1702766719;
        System.out.println("Large test: " + solution.firstBadVersion(2126753390)); // Expected: 1702766719
    }
}
