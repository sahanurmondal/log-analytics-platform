package strings.medium;

/**
 * LeetCode 696: Count Binary Substrings
 *
 * Given a binary string s, return the number of non-empty substrings that have the same
 * number of consecutive 0's and 1's, and all the 0's and 1's in these substrings are grouped
 * consecutively.
 *
 * Example 1:
 * Input: s = "00110011"
 * Output: 6
 * Explanation:
 * Substrings: "0011", "01", "1100", "10", "0011", "01"
 *
 * Example 2:
 * Input: s = "10101"
 * Output: 4
 * Explanation:
 * Substrings: "10", "01", "10", "01"
 */
public class CountBinarySubstrings {

    /**
     * Solution: Count Groups
     * Time: O(n), Space: O(1)
     *
     * Key insight: We need groups of consecutive same characters
     * If we have groups [0,0] followed by [1,1], we can form min(groupSize1, groupSize2) substrings
     *
     * Example: "00110011"
     * Groups: [2 zeros, 2 ones, 2 zeros, 2 ones]
     * Valid substrings:
     * - Between group 1 (2 zeros) and group 2 (2 ones): min(2,2) = 2 substrings
     * - Between group 2 (2 ones) and group 3 (2 zeros): min(2,2) = 2 substrings
     * - Between group 3 (2 zeros) and group 4 (2 ones): min(2,2) = 2 substrings
     * Total: 6
     */
    public int countBinarySubstrings(String s) {
        int count = 0;
        int prevGroupSize = 0;
        int currentGroupSize = 1;

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                // Same character, increase current group size
                currentGroupSize++;
            } else {
                // Different character, we found a boundary
                // Count valid substrings formed by previous and current group
                count += Math.min(prevGroupSize, currentGroupSize);

                // Move to next group
                prevGroupSize = currentGroupSize;
                currentGroupSize = 1;
            }
        }

        // Don't forget the last group
        count += Math.min(prevGroupSize, currentGroupSize);

        return count;
    }

    /**
     * Alternative: Using groups array
     * Time: O(n), Space: O(n)
     */
    public int countBinarySubstringsV2(String s) {
        int[] groups = new int[s.length()];
        int idx = 0;
        groups[0] = 1;

        // First, count group sizes
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                groups[idx]++;
            } else {
                groups[++idx] = 1;
            }
        }

        // Count valid substrings
        int count = 0;
        for (int i = 0; i < idx; i++) {
            count += Math.min(groups[i], groups[i + 1]);
        }

        return count;
    }

    /**
     * One-pass solution for clarity
     * Time: O(n), Space: O(1)
     */
    public int countBinarySubstringsOnePass(String s) {
        int count = 0;
        int prev = 0;
        int curr = 1;

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != s.charAt(i - 1)) {
                // Group changed
                count += Math.min(prev, curr);
                prev = curr;
                curr = 1;
            } else {
                // Same group
                curr++;
            }
        }

        // Add the last valid substring count
        count += Math.min(prev, curr);

        return count;
    }

    public static void main(String[] args) {
        CountBinarySubstrings solution = new CountBinarySubstrings();

        // Test case 1
        System.out.println(solution.countBinarySubstrings("00110011")); // 6

        // Test case 2
        System.out.println(solution.countBinarySubstrings("10101"));    // 4

        // Test case 3
        System.out.println(solution.countBinarySubstrings("00110"));    // 3
        // Groups: 2 zeros, 2 ones, 1 zero
        // min(2,2)=2, min(2,1)=1 -> total 3

        // Test case 4
        System.out.println(solution.countBinarySubstrings("0011"));     // 1

        // Test case 5
        System.out.println(solution.countBinarySubstrings("01"));       // 1
    }
}

