package greedy.hard;

import java.util.*;

/**
 * Minimum Swaps to Make Strings Equal
 * 
 * LeetCode Problem: 1247. Minimum Swaps to Make Strings Equal
 * URL: https://leetcode.com/problems/minimum-swaps-to-make-strings-equal/
 * 
 * Company Tags: Microsoft, Amazon, Google, Facebook
 * Difficulty: Hard (Medium on LeetCode but includes complex variations)
 * 
 * Description:
 * You are given two strings s1 and s2 of equal length consisting of letters 'x'
 * and 'y' only. Your task is to make these strings equal by swapping
 * characters.
 * In one swap, you can swap any two characters from the same string (s1 or s2).
 * 
 * Return the minimum number of swaps required to make s1 and s2 equal, or -1 if
 * it is impossible.
 * 
 * Constraints:
 * - 1 <= s1.length == s2.length <= 1000
 * - s1[i], s2[i] are either 'x' or 'y'
 * 
 * Follow-ups:
 * 1. Can you solve with different approaches?
 * 2. What if we can swap between strings?
 * 3. What if strings have more than 2 characters?
 * 4. What if we want to track actual swaps made?
 * 5. What about optimizations for large inputs?
 */
public class MinimumNumberOfSwapsToMakeStringEqual {

    /**
     * Count mismatches approach - optimal greedy solution
     * Time: O(n), Space: O(1)
     */
    public int minimumSwap(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return -1;
        }

        int xy = 0; // s1[i] = 'x', s2[i] = 'y'
        int yx = 0; // s1[i] = 'y', s2[i] = 'x'

        // Count mismatches
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == 'x' && s2.charAt(i) == 'y') {
                xy++;
            } else if (s1.charAt(i) == 'y' && s2.charAt(i) == 'x') {
                yx++;
            }
        }

        // If total mismatches is odd, impossible to make equal
        if ((xy + yx) % 2 != 0) {
            return -1;
        }

        // Each pair of same type mismatches needs 1 swap
        // Each pair of different type mismatches needs 2 swaps
        return xy / 2 + yx / 2 + (xy % 2) * 2;
    }

    /**
     * Detailed tracking approach - shows which swaps are made
     * Time: O(n), Space: O(n) for tracking
     */
    public SwapResult minimumSwapWithTracking(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return new SwapResult(-1, new ArrayList<>());
        }

        List<Integer> xyPositions = new ArrayList<>(); // s1='x', s2='y'
        List<Integer> yxPositions = new ArrayList<>(); // s1='y', s2='x'

        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == 'x' && s2.charAt(i) == 'y') {
                xyPositions.add(i);
            } else if (s1.charAt(i) == 'y' && s2.charAt(i) == 'x') {
                yxPositions.add(i);
            }
        }

        if ((xyPositions.size() + yxPositions.size()) % 2 != 0) {
            return new SwapResult(-1, new ArrayList<>());
        }

        List<String> swaps = new ArrayList<>();
        int totalSwaps = 0;

        // Handle pairs of same type (1 swap each)
        for (int i = 0; i < xyPositions.size() - 1; i += 2) {
            int pos1 = xyPositions.get(i);
            int pos2 = xyPositions.get(i + 1);
            swaps.add("Swap s1[" + pos1 + "] with s1[" + pos2 + "]");
            totalSwaps++;
        }

        for (int i = 0; i < yxPositions.size() - 1; i += 2) {
            int pos1 = yxPositions.get(i);
            int pos2 = yxPositions.get(i + 1);
            swaps.add("Swap s1[" + pos1 + "] with s1[" + pos2 + "]");
            totalSwaps++;
        }

        // Handle remaining different type pairs (2 swaps each)
        if (xyPositions.size() % 2 == 1) {
            int xyPos = xyPositions.get(xyPositions.size() - 1);
            int yxPos = yxPositions.get(yxPositions.size() - 1);
            swaps.add("Swap s1[" + xyPos + "] with s1[" + yxPos + "]");
            swaps.add("Swap s2[" + xyPos + "] with s2[" + yxPos + "]");
            totalSwaps += 2;
        }

        return new SwapResult(totalSwaps, swaps);
    }

    /**
     * Mathematical approach - direct formula
     * Time: O(n), Space: O(1)
     */
    public int minimumSwapMath(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return -1;
        }

        int n = s1.length();
        int x1 = 0, y1 = 0; // Count of 'x' and 'y' in s1

        for (char c : s1.toCharArray()) {
            if (c == 'x')
                x1++;
            else
                y1++;
        }

        int x2 = 0, y2 = 0; // Count of 'x' and 'y' in s2

        for (char c : s2.toCharArray()) {
            if (c == 'x')
                x2++;
            else
                y2++;
        }

        // For strings to be made equal, total count of each character must be even
        if ((x1 + x2) % 2 != 0 || (y1 + y2) % 2 != 0) {
            return -1;
        }

        int mismatches = 0;
        for (int i = 0; i < n; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                mismatches++;
            }
        }

        return mismatches / 2;
    }

    /**
     * Follow-up 1: Alternative counting method
     * Time: O(n), Space: O(1)
     */
    public int minimumSwapAlternative(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return -1;
        }

        int[] count = new int[4]; // xx, xy, yx, yy

        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            if (c1 == 'x' && c2 == 'x')
                count[0]++;
            else if (c1 == 'x' && c2 == 'y')
                count[1]++;
            else if (c1 == 'y' && c2 == 'x')
                count[2]++;
            else
                count[3]++;
        }

        // Only xy and yx matter for swaps
        int xy = count[1];
        int yx = count[2];

        if ((xy + yx) % 2 != 0) {
            return -1;
        }

        return (xy + 1) / 2 + (yx + 1) / 2;
    }

    /**
     * Follow-up 2: Cross-string swaps allowed
     * Time: O(n), Space: O(1)
     */
    public int minimumSwapCrossString(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return -1;
        }

        int mismatches = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                mismatches++;
            }
        }

        // With cross-string swaps, each mismatch can be fixed with 1 swap
        return (mismatches + 1) / 2;
    }

    /**
     * Follow-up 3: Multiple character types (generalized)
     * Time: O(n), Space: O(k) where k is number of character types
     */
    public int minimumSwapGeneralized(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return -1;
        }

        Map<Character, Integer> diff = new HashMap<>();

        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            if (c1 != c2) {
                diff.put(c1, diff.getOrDefault(c1, 0) + 1);
                diff.put(c2, diff.getOrDefault(c2, 0) - 1);
            }
        }

        int swaps = 0;
        for (int count : diff.values()) {
            if (count > 0) {
                swaps += count;
            }
        }

        return swaps;
    }

    /**
     * Follow-up 4: Validation approach - verify result
     * Time: O(n), Space: O(n)
     */
    public ValidationResult minimumSwapWithValidation(String s1, String s2) {
        int swaps = minimumSwap(s1, s2);

        if (swaps == -1) {
            return new ValidationResult(swaps, false, "Impossible to make equal");
        }

        // Simulate the swaps to verify
        char[] arr1 = s1.toCharArray();
        char[] arr2 = s2.toCharArray();

        int actualSwaps = performSwaps(arr1, arr2);
        boolean valid = Arrays.equals(arr1, arr2);

        String message = valid ? "Validation successful" : "Validation failed";
        return new ValidationResult(swaps, valid && actualSwaps == swaps, message);
    }

    private int performSwaps(char[] s1, char[] s2) {
        List<Integer> xy = new ArrayList<>();
        List<Integer> yx = new ArrayList<>();

        for (int i = 0; i < s1.length; i++) {
            if (s1[i] == 'x' && s2[i] == 'y') {
                xy.add(i);
            } else if (s1[i] == 'y' && s2[i] == 'x') {
                yx.add(i);
            }
        }

        int swaps = 0;

        // Handle pairs of same type
        for (int i = 0; i < xy.size() - 1; i += 2) {
            int pos1 = xy.get(i);
            int pos2 = xy.get(i + 1);
            // Swap s1[pos1] with s1[pos2]
            char temp = s1[pos1];
            s1[pos1] = s1[pos2];
            s1[pos2] = temp;
            swaps++;
        }

        for (int i = 0; i < yx.size() - 1; i += 2) {
            int pos1 = yx.get(i);
            int pos2 = yx.get(i + 1);
            // Swap s1[pos1] with s1[pos2]
            char temp = s1[pos1];
            s1[pos1] = s1[pos2];
            s1[pos2] = temp;
            swaps++;
        }

        // Handle remaining different type pairs
        if (xy.size() % 2 == 1) {
            int xyPos = xy.get(xy.size() - 1);
            int yxPos = yx.get(yx.size() - 1);

            // First swap: s1[xy] with s1[yx]
            char temp = s1[xyPos];
            s1[xyPos] = s1[yxPos];
            s1[yxPos] = temp;
            swaps++;

            // Second swap: s2[xy] with s2[yx]
            temp = s2[xyPos];
            s2[xyPos] = s2[yxPos];
            s2[yxPos] = temp;
            swaps++;
        }

        return swaps;
    }

    /**
     * Follow-up 5: Optimized for very large inputs
     * Time: O(n), Space: O(1)
     */
    public int minimumSwapOptimized(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return -1;
        }

        int xy = 0, yx = 0;

        // Single pass counting
        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            xy += (c1 == 'x' && c2 == 'y') ? 1 : 0;
            yx += (c1 == 'y' && c2 == 'x') ? 1 : 0;
        }

        return ((xy + yx) & 1) != 0 ? -1 : (xy >> 1) + (yx >> 1) + ((xy & 1) << 1);
    }

    // Helper classes for detailed results
    static class SwapResult {
        int swaps;
        List<String> operations;

        SwapResult(int swaps, List<String> operations) {
            this.swaps = swaps;
            this.operations = operations;
        }

        @Override
        public String toString() {
            return "Swaps: " + swaps + ", Operations: " + operations;
        }
    }

    static class ValidationResult {
        int swaps;
        boolean isValid;
        String message;

        ValidationResult(int swaps, boolean isValid, String message) {
            this.swaps = swaps;
            this.isValid = isValid;
            this.message = message;
        }

        @Override
        public String toString() {
            return "Swaps: " + swaps + ", Valid: " + isValid + ", Message: " + message;
        }
    }

    public static void main(String[] args) {
        MinimumNumberOfSwapsToMakeStringEqual solution = new MinimumNumberOfSwapsToMakeStringEqual();

        System.out.println("=== Minimum Swaps to Make Strings Equal Test ===");

        // Test Case 1: Basic examples
        System.out.println("Basic examples:");
        System.out.println("\"xx\", \"yy\": " + solution.minimumSwap("xx", "yy")); // 1
        System.out.println("\"xy\", \"yx\": " + solution.minimumSwap("xy", "yx")); // 2
        System.out.println("\"xxyyxyxyxx\", \"xyyxyxxxyx\": " +
                solution.minimumSwap("xxyyxyxyxx", "xyyxyxxxyx")); // 4

        // Test Case 2: Already equal
        System.out.println("\nAlready equal:");
        System.out.println("\"xx\", \"xx\": " + solution.minimumSwap("xx", "xx")); // 0
        System.out.println("\"xyxy\", \"xyxy\": " + solution.minimumSwap("xyxy", "xyxy")); // 0

        // Test Case 3: Impossible cases
        System.out.println("\nImpossible cases:");
        System.out.println("\"x\", \"y\": " + solution.minimumSwap("x", "y")); // -1
        System.out.println("\"xxx\", \"yyy\": " + solution.minimumSwap("xxx", "yyy")); // -1

        // Test Case 4: Compare different approaches
        String s1 = "xyxyxyx", s2 = "yxyxyxy";
        System.out.println("\nCompare approaches for \"" + s1 + "\", \"" + s2 + "\":");
        System.out.println("Standard: " + solution.minimumSwap(s1, s2));
        System.out.println("Mathematical: " + solution.minimumSwapMath(s1, s2));
        System.out.println("Alternative: " + solution.minimumSwapAlternative(s1, s2));
        System.out.println("Optimized: " + solution.minimumSwapOptimized(s1, s2));

        // Test Case 5: Detailed tracking
        System.out.println("\nDetailed tracking:");
        SwapResult result = solution.minimumSwapWithTracking("xxxy", "yyyx");
        System.out.println("\"xxxy\", \"yyyx\": " + result);

        // Test Case 6: Cross-string swaps
        System.out.println("\nCross-string swaps allowed:");
        System.out.println("\"xy\", \"yx\": " + solution.minimumSwapCrossString("xy", "yx")); // 1
        System.out.println("\"xxxy\", \"yyyx\": " + solution.minimumSwapCrossString("xxxy", "yyyx")); // 2

        // Test Case 7: Generalized approach
        System.out.println("\nGeneralized approach:");
        System.out.println("\"abc\", \"bca\": " + solution.minimumSwapGeneralized("abc", "bca")); // 2
        System.out.println("\"abcd\", \"dcba\": " + solution.minimumSwapGeneralized("abcd", "dcba")); // 4

        // Test Case 8: Validation
        System.out.println("\nValidation:");
        ValidationResult validation = solution.minimumSwapWithValidation("xxxy", "yyyx");
        System.out.println("\"xxxy\", \"yyyx\": " + validation);

        // Test Case 9: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty strings: " + solution.minimumSwap("", "")); // 0
        System.out.println("Single x: " + solution.minimumSwap("x", "x")); // 0
        System.out.println("Single different: " + solution.minimumSwap("x", "y")); // -1

        // Test Case 10: Pattern analysis
        System.out.println("\nPattern analysis:");
        System.out.println("\"xyxy\", \"yxyx\": " + solution.minimumSwap("xyxy", "yxyx")); // 2
        System.out.println("\"xxyy\", \"yyxx\": " + solution.minimumSwap("xxyy", "yyxx")); // 2
        System.out.println("\"xyxx\", \"yxyy\": " + solution.minimumSwap("xyxx", "yxyy")); // 2

        // Test Case 11: Performance test
        System.out.println("\n=== Performance Test ===");
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        // Create large strings with balanced x and y counts - alternating pattern
        for (int i = 0; i < 500; i++) {
            sb1.append("xy");
            sb2.append("yx");
        }

        String largeS1 = sb1.toString();
        String largeS2 = sb2.toString();

        long startTime = System.currentTimeMillis();
        int result1 = solution.minimumSwap(largeS1, largeS2);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int result2 = solution.minimumSwapOptimized(largeS1, largeS2);
        long time2 = System.currentTimeMillis() - startTime;

        System.out.println("Standard (1000 chars): " + result1 + " (" + time1 + "ms)");
        System.out.println("Optimized (1000 chars): " + result2 + " (" + time2 + "ms)");

        // Test Case 12: Complex patterns
        System.out.println("\nComplex patterns:");
        String complex1 = "xyxyxyxy";
        String complex2 = "yxyxyxyx";
        System.out.println("\"" + complex1 + "\", \"" + complex2 + "\": " +
                solution.minimumSwap(complex1, complex2));

        // Test Case 13: All possible 2-char combinations
        System.out.println("\nAll 2-char combinations:");
        String[][] pairs = { { "xx", "xx" }, { "xx", "xy" }, { "xx", "yx" }, { "xx", "yy" },
                { "xy", "xy" }, { "xy", "yx" }, { "xy", "yy" },
                { "yx", "yx" }, { "yx", "yy" }, { "yy", "yy" } };

        for (String[] pair : pairs) {
            int swaps = solution.minimumSwap(pair[0], pair[1]);
            System.out.println("\"" + pair[0] + "\", \"" + pair[1] + "\": " + swaps);
        }
    }
}
