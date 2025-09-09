package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 1888: Minimum Number of Flips to Make Binary String Alternating
 * https://leetcode.com/problems/minimum-number-of-flips-to-make-the-binary-string-alternating/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, Apple
 * Frequency: High (Asked in 150+ interviews)
 *
 * Description: You are given a binary string s. You are allowed to perform two
 * types of operations:
 * 1. Type-1: Remove a character from the front of s and append it to the back
 * of s.
 * 2. Type-2: Pick any character in s and flip its value (0 to 1 or 1 to 0).
 * Return the minimum number of type-2 operations needed to make s alternating
 * after any number of type-1 operations.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s[i] is either '0' or '1'
 * 
 * Follow-up Questions:
 * 1. Can you handle the case with different alternating patterns (e.g., 01010,
 * 10101)?
 * 2. Can you find the minimum operations without any type-1 operations?
 * 3. Can you extend to strings with more than 2 characters (e.g., ABC pattern)?
 * 4. Can you handle the case where we want specific patterns at specific
 * positions?
 */
public class MinimumNumberOfFlipsToMakeBinaryStringAlternating {

    // Approach 1: Sliding window with pattern matching - O(n) time, O(1) space
    public int minFlips(String s) {
        int n = s.length();
        String doubled = s + s;
        String pattern1 = "", pattern2 = "";

        // Build alternating patterns
        for (int i = 0; i < 2 * n; i++) {
            pattern1 += (i % 2 == 0) ? '0' : '1';
            pattern2 += (i % 2 == 0) ? '1' : '0';
        }

        int diff1 = 0, diff2 = 0;
        int minFlips = Integer.MAX_VALUE;

        for (int i = 0; i < 2 * n; i++) {
            // Add current character difference
            if (doubled.charAt(i) != pattern1.charAt(i))
                diff1++;
            if (doubled.charAt(i) != pattern2.charAt(i))
                diff2++;

            // Remove character going out of window
            if (i >= n) {
                if (doubled.charAt(i - n) != pattern1.charAt(i - n))
                    diff1--;
                if (doubled.charAt(i - n) != pattern2.charAt(i - n))
                    diff2--;
            }

            // Update minimum when window size is n
            if (i >= n - 1) {
                minFlips = Math.min(minFlips, Math.min(diff1, diff2));
            }
        }

        return minFlips;
    }

    // Approach 2: Direct calculation without string concatenation - O(n) time, O(1)
    // space
    public int minFlipsOptimized(String s) {
        int n = s.length();
        int flips01 = 0, flips10 = 0; // flips needed for patterns starting with 0 and 1
        int minResult = Integer.MAX_VALUE;

        for (int i = 0; i < 2 * n; i++) {
            char current = s.charAt(i % n);

            // Check against pattern starting with '0' (01010...)
            if ((i % 2 == 0 && current == '1') || (i % 2 == 1 && current == '0')) {
                flips01++;
            }

            // Check against pattern starting with '1' (10101...)
            if ((i % 2 == 0 && current == '0') || (i % 2 == 1 && current == '1')) {
                flips10++;
            }

            // When we complete a window of size n, update result
            if (i >= n) {
                char removed = s.charAt((i - n) % n);
                int removedIndex = i - n;

                // Remove the contribution of the character going out of window
                if ((removedIndex % 2 == 0 && removed == '1') || (removedIndex % 2 == 1 && removed == '0')) {
                    flips01--;
                }
                if ((removedIndex % 2 == 0 && removed == '0') || (removedIndex % 2 == 1 && removed == '1')) {
                    flips10--;
                }
            }

            if (i >= n - 1) {
                minResult = Math.min(minResult, Math.min(flips01, flips10));
            }
        }

        return minResult;
    }

    // Approach 3: Prefix sum approach - O(n) time, O(n) space
    public int minFlipsPrefix(String s) {
        int n = s.length();
        int[] mismatch01 = new int[2 * n + 1]; // prefix sum of mismatches for pattern 01...
        int[] mismatch10 = new int[2 * n + 1]; // prefix sum of mismatches for pattern 10...

        String doubled = s + s;

        for (int i = 0; i < 2 * n; i++) {
            char current = doubled.charAt(i);

            mismatch01[i + 1] = mismatch01[i];
            mismatch10[i + 1] = mismatch10[i];

            // Pattern 01010...
            if ((i % 2 == 0 && current == '1') || (i % 2 == 1 && current == '0')) {
                mismatch01[i + 1]++;
            }

            // Pattern 10101...
            if ((i % 2 == 0 && current == '0') || (i % 2 == 1 && current == '1')) {
                mismatch10[i + 1]++;
            }
        }

        int minFlips = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int flips01 = mismatch01[i + n] - mismatch01[i];
            int flips10 = mismatch10[i + n] - mismatch10[i];
            minFlips = Math.min(minFlips, Math.min(flips01, flips10));
        }

        return minFlips;
    }

    // Follow-up 1: Handle different alternating patterns
    public int minFlipsCustomPattern(String s, String pattern) {
        int n = s.length();
        int patternLen = pattern.length();
        String doubled = s + s;

        int minFlips = Integer.MAX_VALUE;

        for (int start = 0; start < n; start++) {
            int flips = 0;
            for (int i = 0; i < n; i++) {
                char expected = pattern.charAt(i % patternLen);
                if (doubled.charAt(start + i) != expected) {
                    flips++;
                }
            }
            minFlips = Math.min(minFlips, flips);
        }

        return minFlips;
    }

    // Follow-up 2: Minimum operations without any type-1 operations
    public int minFlipsNoRotation(String s) {
        int n = s.length();
        int flips01 = 0, flips10 = 0;

        for (int i = 0; i < n; i++) {
            char current = s.charAt(i);

            // Pattern 01010...
            if ((i % 2 == 0 && current == '1') || (i % 2 == 1 && current == '0')) {
                flips01++;
            }

            // Pattern 10101...
            if ((i % 2 == 0 && current == '0') || (i % 2 == 1 && current == '1')) {
                flips10++;
            }
        }

        return Math.min(flips01, flips10);
    }

    // Follow-up 3: Extend to strings with more than 2 characters
    public int minFlipsMultiChar(String s, char[] alphabet) {
        int n = s.length();
        int k = alphabet.length;
        int minFlips = Integer.MAX_VALUE;

        // Try all possible starting characters
        for (int startChar = 0; startChar < k; startChar++) {
            String doubled = s + s;

            for (int rotation = 0; rotation < n; rotation++) {
                int flips = 0;
                for (int i = 0; i < n; i++) {
                    char expected = alphabet[(startChar + i) % k];
                    if (doubled.charAt(rotation + i) != expected) {
                        flips++;
                    }
                }
                minFlips = Math.min(minFlips, flips);
            }
        }

        return minFlips;
    }

    // Follow-up 4: Specific patterns at specific positions
    public int minFlipsWithConstraints(String s, Map<Integer, Character> constraints) {
        int n = s.length();
        String doubled = s + s;
        int minFlips = Integer.MAX_VALUE;

        for (int rotation = 0; rotation < n; rotation++) {
            boolean validRotation = true;

            // Check if this rotation satisfies all constraints
            for (Map.Entry<Integer, Character> constraint : constraints.entrySet()) {
                int pos = constraint.getKey();
                char required = constraint.getValue();
                if (doubled.charAt(rotation + pos) != required) {
                    validRotation = false;
                    break;
                }
            }

            if (!validRotation)
                continue;

            // Count flips needed for alternating pattern
            int flips01 = 0, flips10 = 0;
            for (int i = 0; i < n; i++) {
                char current = doubled.charAt(rotation + i);

                if ((i % 2 == 0 && current == '1') || (i % 2 == 1 && current == '0')) {
                    flips01++;
                }
                if ((i % 2 == 0 && current == '0') || (i % 2 == 1 && current == '1')) {
                    flips10++;
                }
            }

            minFlips = Math.min(minFlips, Math.min(flips01, flips10));
        }

        return minFlips == Integer.MAX_VALUE ? -1 : minFlips;
    }

    // Helper method: Get all possible alternating strings after rotations
    public List<String> getAllAlternatingStrings(String s) {
        List<String> result = new ArrayList<>();
        int n = s.length();
        String doubled = s + s;

        for (int rotation = 0; rotation < n; rotation++) {
            String rotated = doubled.substring(rotation, rotation + n);

            // Try both patterns
            StringBuilder pattern1 = new StringBuilder();
            StringBuilder pattern2 = new StringBuilder();

            for (int i = 0; i < n; i++) {
                pattern1.append(i % 2 == 0 ? '0' : '1');
                pattern2.append(i % 2 == 0 ? '1' : '0');
            }

            result.add("Rotation " + rotation + ": " + rotated +
                    " -> Pattern1: " + pattern1 +
                    " -> Pattern2: " + pattern2);
        }

        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MinimumNumberOfFlipsToMakeBinaryStringAlternating solution = new MinimumNumberOfFlipsToMakeBinaryStringAlternating();

        // Test case 1: Basic case
        String s1 = "111000";
        System.out.println("Test 1 - Input: " + s1 + " Expected: 2");
        System.out.println("Approach 1: " + solution.minFlips(s1));
        System.out.println("Approach 2: " + solution.minFlipsOptimized(s1));
        System.out.println("Approach 3: " + solution.minFlipsPrefix(s1));

        // Test case 2: Already alternating
        String s2 = "010";
        System.out.println("\nTest 2 - Input: " + s2 + " Expected: 0 (already alternating)");
        System.out.println("Result: " + solution.minFlips(s2));

        // Test case 3: Single character
        String s3 = "1";
        System.out.println("\nTest 3 - Input: " + s3 + " Expected: 0 (single character)");
        System.out.println("Result: " + solution.minFlips(s3));

        // Test case 4: All same characters
        String s4 = "1111";
        System.out.println("\nTest 4 - Input: " + s4 + " Expected: 2 (all same)");
        System.out.println("Result: " + solution.minFlips(s4));

        // Test case 5: Complex case
        String s5 = "01001001101";
        System.out.println("\nTest 5 - Input: " + s5 + " Expected: 2 (complex case)");
        System.out.println("Result: " + solution.minFlips(s5));

        // Test case 6: Even length all zeros
        String s6 = "0000";
        System.out.println("\nTest 6 - Input: " + s6 + " Expected: 2 (even length all zeros)");
        System.out.println("Result: " + solution.minFlips(s6));

        // Test case 7: Odd length all zeros
        String s7 = "00000";
        System.out.println("\nTest 7 - Input: " + s7 + " Expected: 2 (odd length all zeros)");
        System.out.println("Result: " + solution.minFlips(s7));

        // Test case 8: Large input with pattern
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append(i % 3 == 0 ? '1' : '0'); // Pattern: 100100100...
        }
        String s8 = sb.toString();
        System.out.println("\nTest 8 - Large input (1000 chars): Pattern 100100...");
        long startTime = System.nanoTime();
        int result8 = solution.minFlips(s8);
        long endTime = System.nanoTime();
        System.out.println("Result: " + result8 + " (Time: " + (endTime - startTime) / 1_000_000 + " ms)");

        // Test Follow-ups
        System.out.println("\nFollow-up tests:");

        // Custom pattern
        System.out.println("Custom pattern '01': " + solution.minFlipsCustomPattern(s1, "01"));
        System.out.println("Custom pattern '10': " + solution.minFlipsCustomPattern(s1, "10"));

        // No rotation
        System.out.println("No rotation allowed: " + solution.minFlipsNoRotation(s1));

        // Multi-character alphabet
        char[] alphabet = { 'A', 'B', 'C' };
        String multiChar = "AABBAACCA";
        System.out.println("Multi-char pattern: " + solution.minFlipsMultiChar(multiChar, alphabet));

        // With constraints
        Map<Integer, Character> constraints = new HashMap<>();
        constraints.put(0, '0');
        constraints.put(2, '1');
        System.out.println("With constraints (pos 0='0', pos 2='1'): " +
                solution.minFlipsWithConstraints("111000", constraints));

        // Edge case: Two characters alternating
        String s9 = "10";
        System.out.println("\nEdge case - Two chars alternating: " + s9);
        System.out.println("Result: " + solution.minFlips(s9));

        // Edge case: Two characters same
        String s10 = "11";
        System.out.println("\nEdge case - Two chars same: " + s10);
        System.out.println("Result: " + solution.minFlips(s10));

        // Edge case: Very long string with specific pattern
        StringBuilder longPattern = new StringBuilder();
        for (int i = 0; i < 50000; i++) {
            longPattern.append((i / 1000) % 2); // Changes every 1000 characters
        }
        String s11 = longPattern.toString();
        System.out.println("\nEdge case - Very long string (50000 chars) with block pattern:");
        startTime = System.nanoTime();
        int result11 = solution.minFlipsOptimized(s11);
        endTime = System.nanoTime();
        System.out.println("Result: " + result11 + " (Time: " + (endTime - startTime) / 1_000_000 + " ms)");

        // Debugging: Show all possible alternating strings for small input
        String debug = "1100";
        System.out.println("\nDebugging - All rotations for: " + debug);
        List<String> allStrings = solution.getAllAlternatingStrings(debug);
        for (String str : allStrings) {
            System.out.println(str);
        }

        // Stress test: Random binary strings
        System.out.println("\nStress test - Random binary strings:");
        Random random = new Random(42);
        for (int len : new int[] { 10, 100, 1000 }) {
            StringBuilder randomStr = new StringBuilder();
            for (int i = 0; i < len; i++) {
                randomStr.append(random.nextInt(2));
            }
            String randomBinary = randomStr.toString();

            startTime = System.nanoTime();
            int randomResult = solution.minFlips(randomBinary);
            endTime = System.nanoTime();

            System.out.println("Length " + len + ": " + randomResult +
                    " flips (Time: " + (endTime - startTime) / 1_000_000 + " ms)");
        }
    }
}
