package strings.medium;

import java.util.*;

/**
 * LeetCode 38: Count and Say
 * https://leetcode.com/problems/count-and-say/
 * 
 * Companies: Facebook, Amazon
 * Frequency: Medium
 *
 * Description: The count-and-say sequence is a sequence of digit strings
 * defined by the recursive formula.
 *
 * Constraints:
 * - 1 <= n <= 30
 * 
 * Follow-up Questions:
 * 1. Can you optimize for large n?
 * 2. Can you reverse the process?
 * 3. Can you find patterns in the sequence?
 */
public class CountAndSay {

    // Approach 1: Iterative string building (O(4^n) time)
    public String countAndSay(int n) {
        String result = "1";
        for (int i = 1; i < n; i++) {
            result = getNext(result);
        }
        return result;
    }

    // Follow-up 1: Optimized with StringBuilder
    public String countAndSayOptimized(int n) {
        StringBuilder result = new StringBuilder("1");
        for (int i = 1; i < n; i++) {
            result = getNextOptimized(result);
        }
        return result.toString();
    }

    // Follow-up 2: Reverse the process (decode)
    public String decode(String encoded) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < encoded.length(); i += 2) {
            int count = encoded.charAt(i) - '0';
            char digit = encoded.charAt(i + 1);
            for (int j = 0; j < count; j++) {
                result.append(digit);
            }
        }
        return result.toString();
    }

    // Follow-up 3: Get sequence up to n terms
    public List<String> getSequence(int n) {
        List<String> sequence = new ArrayList<>();
        String current = "1";
        for (int i = 0; i < n; i++) {
            sequence.add(current);
            if (i < n - 1)
                current = getNext(current);
        }
        return sequence;
    }

    // Helper methods
    private String getNext(String s) {
        StringBuilder sb = new StringBuilder();
        char current = s.charAt(0);
        int count = 1;

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == current) {
                count++;
            } else {
                sb.append(count).append(current);
                current = s.charAt(i);
                count = 1;
            }
        }
        sb.append(count).append(current);
        return sb.toString();
    }

    private StringBuilder getNextOptimized(StringBuilder s) {
        StringBuilder sb = new StringBuilder();
        char current = s.charAt(0);
        int count = 1;

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == current) {
                count++;
            } else {
                sb.append(count).append(current);
                current = s.charAt(i);
                count = 1;
            }
        }
        sb.append(count).append(current);
        return sb;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CountAndSay solution = new CountAndSay();

        // Test case 1: Basic cases
        for (int i = 1; i <= 5; i++) {
            System.out.println("n=" + i + ": " + solution.countAndSay(i));
        }

        // Test case 2: Optimized version
        System.out.println("\nOptimized version:");
        System.out.println("n=5: " + solution.countAndSayOptimized(5));

        // Test case 3: Decode
        String encoded = "312211";
        System.out.println("\nDecode '" + encoded + "': " + solution.decode(encoded));

        // Test case 4: Get full sequence
        System.out.println("\nFirst 6 terms:");
        List<String> sequence = solution.getSequence(6);
        for (int i = 0; i < sequence.size(); i++) {
            System.out.println((i + 1) + ": " + sequence.get(i));
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("n=1: " + solution.countAndSay(1));
        System.out.println("n=30: " + solution.countAndSay(30).length() + " characters");

        // Stress test
        System.out.println("\nStress test:");
        long start = System.nanoTime();
        String result = solution.countAndSayOptimized(20);
        long end = System.nanoTime();
        System.out.println("n=20 length: " + result.length() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
