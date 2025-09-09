package strings.hard;

import java.util.*;

/**
 * LeetCode 68: Text Justification
 * https://leetcode.com/problems/text-justification/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given an array of strings words and a width maxWidth, format the
 * text such that each line has exactly maxWidth characters and is fully
 * justified.
 *
 * Constraints:
 * - 1 <= words.length <= 300
 * - 1 <= words[i].length <= 20
 * - 1 <= maxWidth <= 100
 * 
 * Follow-up Questions:
 * 1. Can you handle different justification styles?
 * 2. Can you justify to center alignment?
 * 3. Can you optimize for large inputs?
 */
public class TextJustification {

    // Approach 1: Line by line justification - O(n) time
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            List<String> line = new ArrayList<>();
            int lineLength = 0;

            // Collect words for current line
            while (i < words.length && lineLength + words[i].length() + line.size() <= maxWidth) {
                line.add(words[i]);
                lineLength += words[i].length();
                i++;
            }

            // Justify the line
            if (i == words.length || line.size() == 1) {
                // Last line or single word line - left justify
                result.add(leftJustify(line, maxWidth));
            } else {
                // Regular line - full justify
                result.add(fullJustifyLine(line, maxWidth));
            }
        }

        return result;
    }

    // Follow-up 1: Left justification
    public List<String> leftJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            List<String> line = new ArrayList<>();
            int lineLength = 0;

            while (i < words.length && lineLength + words[i].length() + line.size() <= maxWidth) {
                line.add(words[i]);
                lineLength += words[i].length();
                i++;
            }

            result.add(leftJustify(line, maxWidth));
        }

        return result;
    }

    // Follow-up 2: Center justification
    public List<String> centerJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            List<String> line = new ArrayList<>();
            int lineLength = 0;

            while (i < words.length && lineLength + words[i].length() + line.size() <= maxWidth) {
                line.add(words[i]);
                lineLength += words[i].length();
                i++;
            }

            result.add(centerJustifyLine(line, maxWidth));
        }

        return result;
    }

    // Follow-up 3: Right justification
    public List<String> rightJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            List<String> line = new ArrayList<>();
            int lineLength = 0;

            while (i < words.length && lineLength + words[i].length() + line.size() <= maxWidth) {
                line.add(words[i]);
                lineLength += words[i].length();
                i++;
            }

            result.add(rightJustifyLine(line, maxWidth));
        }

        return result;
    }

    // Helper methods
    private String fullJustifyLine(List<String> words, int maxWidth) {
        int totalChars = words.stream().mapToInt(String::length).sum();
        int totalSpaces = maxWidth - totalChars;
        int gaps = words.size() - 1;

        if (gaps == 0)
            return leftJustify(words, maxWidth);

        int spacesPerGap = totalSpaces / gaps;
        int extraSpaces = totalSpaces % gaps;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size() - 1; i++) {
            sb.append(words.get(i));
            for (int j = 0; j < spacesPerGap; j++)
                sb.append(' ');
            if (i < extraSpaces)
                sb.append(' ');
        }
        sb.append(words.get(words.size() - 1));

        return sb.toString();
    }

    private String leftJustify(List<String> words, int maxWidth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < words.size() - 1)
                sb.append(' ');
        }
        while (sb.length() < maxWidth)
            sb.append(' ');
        return sb.toString();
    }

    private String centerJustifyLine(List<String> words, int maxWidth) {
        String leftJustified = leftJustify(words, maxWidth);
        int contentLength = leftJustified.trim().length();
        int padding = (maxWidth - contentLength) / 2;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++)
            sb.append(' ');
        sb.append(leftJustified.trim());
        while (sb.length() < maxWidth)
            sb.append(' ');

        return sb.toString();
    }

    private String rightJustifyLine(List<String> words, int maxWidth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            if (i > 0)
                sb.append(' ');
            sb.append(words.get(i));
        }
        while (sb.length() < maxWidth)
            sb.insert(0, ' ');
        return sb.toString();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        TextJustification solution = new TextJustification();

        // Test case 1: Basic case
        String[] words1 = { "This", "is", "an", "example", "of", "text", "justification." };
        int maxWidth1 = 16;
        System.out.println("Test 1 - Full justify:");
        List<String> result1 = solution.fullJustify(words1, maxWidth1);
        for (String line : result1) {
            System.out.println("'" + line + "'");
        }

        // Test case 2: Left justify
        System.out.println("\nTest 2 - Left justify:");
        List<String> result2 = solution.leftJustify(words1, maxWidth1);
        for (String line : result2) {
            System.out.println("'" + line + "'");
        }

        // Test case 3: Center justify
        System.out.println("\nTest 3 - Center justify:");
        List<String> result3 = solution.centerJustify(words1, maxWidth1);
        for (String line : result3) {
            System.out.println("'" + line + "'");
        }

        // Test case 4: Right justify
        System.out.println("\nTest 4 - Right justify:");
        List<String> result4 = solution.rightJustify(words1, maxWidth1);
        for (String line : result4) {
            System.out.println("'" + line + "'");
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        String[] singleWord = { "Hello" };
        System.out.println("Single word: '" + solution.fullJustify(singleWord, 10).get(0) + "'");

        String[] longWord = { "verylongword" };
        System.out.println("Long word: '" + solution.fullJustify(longWord, 15).get(0) + "'");

        // Stress test
        System.out.println("\nStress test:");
        String[] manyWords = new String[300];
        Arrays.fill(manyWords, "test");
        long start = System.nanoTime();
        List<String> result = solution.fullJustify(manyWords, 50);
        long end = System.nanoTime();
        System.out.println("Lines: " + result.size() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
