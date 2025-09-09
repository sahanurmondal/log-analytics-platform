package backtracking.medium;

import java.util.*;

/**
 * LeetCode 93: Restore IP Addresses
 * https://leetcode.com/problems/restore-ip-addresses/
 *
 * Description: A valid IP address consists of exactly four integers separated
 * by single dots.
 * Given a string s containing only digits, return all possible valid IP
 * addresses.
 * 
 * Constraints:
 * - 1 <= s.length <= 20
 * - s consists of digits only
 *
 * Follow-up:
 * - Can you validate IP segments efficiently?
 * - What about IPv6 addresses?
 * 
 * Time Complexity: O(3^4) = O(81)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Backtracking: Try all valid segment combinations
 * 2. Validation: Check segment validity (0-255, no leading zeros)
 * 3. Pruning: Early termination for invalid cases
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class RestoreIPAddresses {

    // Main optimized solution - Backtracking
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        if (s.length() < 4 || s.length() > 12)
            return result;

        backtrack(s, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(String s, int start, List<String> current, List<String> result) {
        if (current.size() == 4) {
            if (start == s.length()) {
                result.add(String.join(".", current));
            }
            return;
        }

        // Try segments of length 1, 2, 3
        for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
            String segment = s.substring(start, start + len);

            if (isValidSegment(segment)) {
                current.add(segment);
                backtrack(s, start + len, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    private boolean isValidSegment(String segment) {
        if (segment.length() == 0 || segment.length() > 3)
            return false;

        // No leading zeros unless segment is "0"
        if (segment.length() > 1 && segment.charAt(0) == '0')
            return false;

        int value = Integer.parseInt(segment);
        return value >= 0 && value <= 255;
    }

    // Alternative solution - Iterative approach
    public List<String> restoreIpAddressesIterative(String s) {
        List<String> result = new ArrayList<>();
        int n = s.length();

        if (n < 4 || n > 12)
            return result;

        // Try all possible positions for dots
        for (int i = 1; i <= 3 && i < n; i++) {
            for (int j = i + 1; j <= i + 3 && j < n; j++) {
                for (int k = j + 1; k <= j + 3 && k < n; k++) {
                    String seg1 = s.substring(0, i);
                    String seg2 = s.substring(i, j);
                    String seg3 = s.substring(j, k);
                    String seg4 = s.substring(k);

                    if (isValidSegment(seg1) && isValidSegment(seg2) &&
                            isValidSegment(seg3) && isValidSegment(seg4)) {
                        result.add(seg1 + "." + seg2 + "." + seg3 + "." + seg4);
                    }
                }
            }
        }

        return result;
    }

    // Follow-up optimization - With pruning
    public List<String> restoreIpAddressesOptimized(String s) {
        List<String> result = new ArrayList<>();
        if (s.length() < 4 || s.length() > 12)
            return result;

        backtrackOptimized(s, 0, 0, "", result);
        return result;
    }

    private void backtrackOptimized(String s, int start, int segments, String current, List<String> result) {
        if (segments == 4) {
            if (start == s.length()) {
                result.add(current.substring(1)); // Remove leading dot
            }
            return;
        }

        // Pruning: remaining characters must fit in remaining segments
        int remaining = s.length() - start;
        int segmentsLeft = 4 - segments;
        if (remaining < segmentsLeft || remaining > segmentsLeft * 3)
            return;

        for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
            String segment = s.substring(start, start + len);

            if (isValidSegment(segment)) {
                backtrackOptimized(s, start + len, segments + 1, current + "." + segment, result);
            }
        }
    }

    public static void main(String[] args) {
        RestoreIPAddresses solution = new RestoreIPAddresses();

        // Test Case 1: Normal case
        System.out.println(solution.restoreIpAddresses("25525511135")); // Expected: ["255.255.11.135","255.255.111.35"]

        // Test Case 2: Another valid case
        System.out.println(solution.restoreIpAddresses("0000")); // Expected: ["0.0.0.0"]

        // Test Case 3: No valid IP
        System.out.println(solution.restoreIpAddresses("101023")); // Expected:
                                                                   // ["1.0.10.23","1.0.102.3","10.1.0.23","10.10.2.3","101.0.2.3"]

        // Test Case 4: Too short
        System.out.println(solution.restoreIpAddresses("123")); // Expected: []

        // Test Case 5: Too long
        System.out.println(solution.restoreIpAddresses("1234567890123456789")); // Expected: []

        // Test Case 6: Leading zeros
        System.out.println(solution.restoreIpAddresses("010010")); // Expected: ["0.10.0.10","0.100.1.0"]

        // Test Case 7: All same digits
        System.out.println(solution.restoreIpAddresses("1111")); // Expected: ["1.1.1.1"]

        // Test Case 8: Test iterative approach
        System.out.println(solution.restoreIpAddressesIterative("25525511135").size()); // Expected: 2

        // Test Case 9: Edge numbers
        System.out.println(solution.restoreIpAddresses("255255255255")); // Expected: ["255.255.255.255"]

        // Test Case 10: Invalid large segments
        System.out.println(solution.restoreIpAddresses("256256256256")); // Expected: []
    }
}
