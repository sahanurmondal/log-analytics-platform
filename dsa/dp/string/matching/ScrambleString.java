package dp.string.matching;

import java.util.*;

/**
 * LeetCode 87: Scramble String
 * https://leetcode.com/problems/scramble-string/
 *
 * Description:
 * We can scramble a string s to get a string t using the following algorithm:
 * 1. If the length of the string is 1, stop.
 * 2. If the length of the string is > 1, do the following:
 * - Split the string into two non-empty substrings at a random index, i.e., if
 * the string is s, divide it to x and y where s = x + y.
 * - Randomly decide to swap the two substrings or to keep them in the same
 * order. i.e., after this step, s may become s = x + y or s = y + x.
 * - Apply step 1 recursively on each of the two substrings x and y.
 * Given two strings s1 and s2 of the same length, return true if s2 is a
 * scrambled string of s1, otherwise, return false.
 *
 * Constraints:
 * - 1 <= s1.length, s2.length <= 30
 * - s1 and s2 consist of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What if we need to find the scrambling process?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class ScrambleString {

    // Approach 1: Recursive with Memoization - O(n^4) time, O(n^3) space
    public boolean isScramble(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;
        Map<String, Boolean> memo = new HashMap<>();
        return isScrambleHelper(s1, s2, memo);
    }

    private boolean isScrambleHelper(String s1, String s2, Map<String, Boolean> memo) {
        if (s1.equals(s2))
            return true;
        if (s1.length() != s2.length())
            return false;

        String key = s1 + "#" + s2;
        if (memo.containsKey(key))
            return memo.get(key);

        // Quick check: same character frequency
        if (!hasSameFrequency(s1, s2)) {
            memo.put(key, false);
            return false;
        }

        int n = s1.length();

        // Try all possible split points
        for (int i = 1; i < n; i++) {
            // Case 1: No swap
            if (isScrambleHelper(s1.substring(0, i), s2.substring(0, i), memo) &&
                    isScrambleHelper(s1.substring(i), s2.substring(i), memo)) {
                memo.put(key, true);
                return true;
            }

            // Case 2: With swap
            if (isScrambleHelper(s1.substring(0, i), s2.substring(n - i), memo) &&
                    isScrambleHelper(s1.substring(i), s2.substring(0, n - i), memo)) {
                memo.put(key, true);
                return true;
            }
        }

        memo.put(key, false);
        return false;
    }

    private boolean hasSameFrequency(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;

        int[] freq = new int[26];
        for (int i = 0; i < s1.length(); i++) {
            freq[s1.charAt(i) - 'a']++;
            freq[s2.charAt(i) - 'a']--;
        }

        for (int f : freq) {
            if (f != 0)
                return false;
        }

        return true;
    }

    // Approach 2: 3D DP - O(n^4) time, O(n^3) space
    public boolean isScrambleDP(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;

        int n = s1.length();
        boolean[][][] dp = new boolean[n][n][n + 1];

        // Base case: substrings of length 1
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j][1] = (s1.charAt(i) == s2.charAt(j));
            }
        }

        // Fill for lengths 2 to n
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                for (int j = 0; j <= n - len; j++) {
                    // Try all split points
                    for (int k = 1; k < len; k++) {
                        // Case 1: No swap
                        if (dp[i][j][k] && dp[i + k][j + k][len - k]) {
                            dp[i][j][len] = true;
                            break;
                        }

                        // Case 2: With swap
                        if (dp[i][j + len - k][k] && dp[i + k][j][len - k]) {
                            dp[i][j][len] = true;
                            break;
                        }
                    }
                }
            }
        }

        return dp[0][0][n];
    }

    // Approach 3: Optimized with Early Pruning - O(n^4) time, O(n^3) space
    public boolean isScrambleOptimized(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;
        if (s1.equals(s2))
            return true;

        Map<String, Boolean> memo = new HashMap<>();
        return isScrambleOptimizedHelper(s1, s2, memo);
    }

    private boolean isScrambleOptimizedHelper(String s1, String s2, Map<String, Boolean> memo) {
        if (s1.equals(s2))
            return true;
        if (s1.length() != s2.length())
            return false;
        if (s1.length() == 1)
            return false; // Already checked equality above

        String key = s1 + "#" + s2;
        if (memo.containsKey(key))
            return memo.get(key);

        // Quick frequency check
        int[] freq = new int[26];
        for (int i = 0; i < s1.length(); i++) {
            freq[s1.charAt(i) - 'a']++;
            freq[s2.charAt(i) - 'a']--;
        }

        for (int f : freq) {
            if (f != 0) {
                memo.put(key, false);
                return false;
            }
        }

        int n = s1.length();

        // Try all split points with early termination
        for (int i = 1; i < n; i++) {
            // Case 1: No swap
            boolean case1 = isScrambleOptimizedHelper(s1.substring(0, i), s2.substring(0, i), memo) &&
                    isScrambleOptimizedHelper(s1.substring(i), s2.substring(i), memo);

            if (case1) {
                memo.put(key, true);
                return true;
            }

            // Case 2: With swap
            boolean case2 = isScrambleOptimizedHelper(s1.substring(0, i), s2.substring(n - i), memo) &&
                    isScrambleOptimizedHelper(s1.substring(i), s2.substring(0, n - i), memo);

            if (case2) {
                memo.put(key, true);
                return true;
            }
        }

        memo.put(key, false);
        return false;
    }

    // Approach 4: BFS Approach - O(n^4) time, O(n^2) space
    public boolean isScrambleBFS(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;
        if (s1.equals(s2))
            return true;

        Queue<String[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(new String[] { s1, s2 });
        visited.add(s1 + "#" + s2);

        while (!queue.isEmpty()) {
            String[] curr = queue.poll();
            String str1 = curr[0], str2 = curr[1];

            if (str1.equals(str2))
                return true;
            if (str1.length() == 1)
                continue;

            // Try all split points
            for (int i = 1; i < str1.length(); i++) {
                // Case 1: No swap
                String left1 = str1.substring(0, i);
                String right1 = str1.substring(i);
                String left2 = str2.substring(0, i);
                String right2 = str2.substring(i);

                String key1 = left1 + "#" + left2;
                String key2 = right1 + "#" + right2;

                if (!visited.contains(key1) && hasSameFrequency(left1, left2)) {
                    visited.add(key1);
                    queue.offer(new String[] { left1, left2 });
                }

                if (!visited.contains(key2) && hasSameFrequency(right1, right2)) {
                    visited.add(key2);
                    queue.offer(new String[] { right1, right2 });
                }

                // Case 2: With swap
                String swapLeft2 = str2.substring(str2.length() - i);
                String swapRight2 = str2.substring(0, str2.length() - i);

                String key3 = left1 + "#" + swapLeft2;
                String key4 = right1 + "#" + swapRight2;

                if (!visited.contains(key3) && hasSameFrequency(left1, swapLeft2)) {
                    visited.add(key3);
                    queue.offer(new String[] { left1, swapLeft2 });
                }

                if (!visited.contains(key4) && hasSameFrequency(right1, swapRight2)) {
                    visited.add(key4);
                    queue.offer(new String[] { right1, swapRight2 });
                }
            }
        }

        return false;
    }

    // Approach 5: Get Scrambling Process - O(n^4) time, O(n^3) space
    public List<String> getScrambleProcess(String s1, String s2) {
        if (!isScramble(s1, s2))
            return new ArrayList<>();

        List<String> process = new ArrayList<>();
        getScrambleProcessHelper(s1, s2, process, new HashMap<>());
        return process;
    }

    private boolean getScrambleProcessHelper(String s1, String s2, List<String> process, Map<String, Boolean> memo) {
        if (s1.equals(s2)) {
            process.add("Final: " + s1 + " == " + s2);
            return true;
        }

        if (s1.length() != s2.length())
            return false;

        String key = s1 + "#" + s2;
        if (memo.containsKey(key))
            return memo.get(key);

        if (!hasSameFrequency(s1, s2)) {
            memo.put(key, false);
            return false;
        }

        int n = s1.length();

        for (int i = 1; i < n; i++) {
            // Case 1: No swap
            String left1 = s1.substring(0, i);
            String right1 = s1.substring(i);
            String left2 = s2.substring(0, i);
            String right2 = s2.substring(i);

            List<String> tempProcess = new ArrayList<>(process);
            tempProcess.add("Split " + s1 + " at " + i + ": (" + left1 + ", " + right1 + ") vs (" + left2 + ", "
                    + right2 + ")");

            if (getScrambleProcessHelper(left1, left2, tempProcess, memo) &&
                    getScrambleProcessHelper(right1, right2, tempProcess, memo)) {
                process.addAll(tempProcess.subList(process.size(), tempProcess.size()));
                memo.put(key, true);
                return true;
            }

            // Case 2: With swap
            String swapLeft2 = s2.substring(n - i);
            String swapRight2 = s2.substring(0, n - i);

            tempProcess = new ArrayList<>(process);
            tempProcess.add("Split " + s1 + " at " + i + " with swap: (" + left1 + ", " + right1 + ") vs (" + swapLeft2
                    + ", " + swapRight2 + ")");

            if (getScrambleProcessHelper(left1, swapLeft2, tempProcess, memo) &&
                    getScrambleProcessHelper(right1, swapRight2, tempProcess, memo)) {
                process.addAll(tempProcess.subList(process.size(), tempProcess.size()));
                memo.put(key, true);
                return true;
            }
        }

        memo.put(key, false);
        return false;
    }

    public static void main(String[] args) {
        ScrambleString solution = new ScrambleString();

        System.out.println("=== Scramble String Test Cases ===");

        // Test Case 1: Example from problem
        String s1_1 = "great", s2_1 = "rgeat";
        System.out.println("Test 1 - s1: \"" + s1_1 + "\", s2: \"" + s2_1 + "\"");
        System.out.println("Recursive: " + solution.isScramble(s1_1, s2_1));
        System.out.println("3D DP: " + solution.isScrambleDP(s1_1, s2_1));
        System.out.println("Optimized: " + solution.isScrambleOptimized(s1_1, s2_1));
        System.out.println("BFS: " + solution.isScrambleBFS(s1_1, s2_1));

        List<String> process1 = solution.getScrambleProcess(s1_1, s2_1);
        System.out.println("Scramble process:");
        for (String step : process1) {
            System.out.println("  " + step);
        }
        System.out.println("Expected: true\n");

        // Test Case 2: False case
        String s1_2 = "abcdef", s2_2 = "fecabd";
        System.out.println("Test 2 - s1: \"" + s1_2 + "\", s2: \"" + s2_2 + "\"");
        System.out.println("Recursive: " + solution.isScramble(s1_2, s2_2));
        System.out.println("Expected: false\n");

        // Test Case 3: Same string
        String s1_3 = "a", s2_3 = "a";
        System.out.println("Test 3 - s1: \"" + s1_3 + "\", s2: \"" + s2_3 + "\"");
        System.out.println("Recursive: " + solution.isScramble(s1_3, s2_3));
        System.out.println("Expected: true\n");

        performanceTest();
    }

    private static void performanceTest() {
        ScrambleString solution = new ScrambleString();

        String s1 = "abcdefghijklmnop";
        String s2 = "ponmlkjihgfedcba";

        System.out.println("=== Performance Test (String length: " + s1.length() + ") ===");

        long start = System.nanoTime();
        boolean result1 = solution.isScramble(s1, s2);
        long end = System.nanoTime();
        System.out.println("Recursive: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result2 = solution.isScrambleDP(s1, s2);
        end = System.nanoTime();
        System.out.println("3D DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result3 = solution.isScrambleOptimized(s1, s2);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
