package arrays.hard;

import java.util.*;

/**
 * LeetCode 68: Text Justification
 * https://leetcode.com/problems/text-justification/
 *
 * Description:
 * Given an array of strings words and a width maxWidth, format the text such
 * that each line has exactly maxWidth characters
 * and is fully (left and right) justified.
 *
 * Constraints:
 * - 1 <= words.length <= 300
 * - 1 <= words[i].length <= 20
 * - words[i] consists of only English letters and symbols
 * - 1 <= maxWidth <= 100
 * - words[i].length <= maxWidth
 *
 * Follow-up:
 * - Can you handle the edge cases properly?
 * 
 * Time Complexity: O(n * maxWidth)
 * Space Complexity: O(maxWidth)
 * 
 * Algorithm:
 * 1. Group words that fit in each line
 * 2. Distribute spaces evenly between words
 * 3. Handle last line with left justification only
 */
public class TextJustification {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            List<String> currentLine = new ArrayList<>();
            int totalLen = 0;

            // Collect words for current line
            while (i < words.length && totalLen + words[i].length() + currentLine.size() <= maxWidth) {
                currentLine.add(words[i]);
                totalLen += words[i].length();
                i++;
            }

            // Justify the line
            result.add(justify(currentLine, totalLen, maxWidth, i == words.length));
        }

        return result;
    }

    private String justify(List<String> words, int totalLen, int maxWidth, boolean isLastLine) {
        if (words.size() == 1 || isLastLine) {
            // Left justify
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < words.size(); i++) {
                sb.append(words.get(i));
                if (i < words.size() - 1)
                    sb.append(" ");
            }
            while (sb.length() < maxWidth) {
                sb.append(" ");
            }
            return sb.toString();
        }

        // Full justify
        int totalSpaces = maxWidth - totalLen;
        int gaps = words.size() - 1;
        int spacesPerGap = totalSpaces / gaps;
        int extraSpaces = totalSpaces % gaps;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < gaps) {
                for (int j = 0; j < spacesPerGap; j++) {
                    sb.append(" ");
                }
                if (i < extraSpaces) {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        TextJustification solution = new TextJustification();

        // Test Case 1: Normal case
        String[] words1 = { "This", "is", "an", "example", "of", "text", "justification." };
        System.out.println(solution.fullJustify(words1, 16));
        // Expected: ["This is an","example of text","justification. "]

        // Test Case 2: Edge case - single word per line
        String[] words2 = { "What", "must", "be", "acknowledgment", "shall", "be" };
        System.out.println(solution.fullJustify(words2, 16));
        // Expected: ["What must be","acknowledgment ","shall be "]

        // Test Case 3: Corner case - single word
        String[] words3 = { "Science", "is", "what", "we", "understand", "well", "enough", "to", "explain", "to", "a",
                "computer.", "Art", "is", "everything", "else", "we", "do" };
        System.out.println(solution.fullJustify(words3, 20));

        // Test Case 4: Large input - long words
        String[] words4 = { "a", "b", "c", "d", "e" };
        System.out.println(solution.fullJustify(words4, 3)); // Expected: ["a b","c d","e "]

        // Test Case 5: Minimum input - single word
        String[] words5 = { "Hello" };
        System.out.println(solution.fullJustify(words5, 10)); // Expected: ["Hello "]

        // Test Case 6: Special case - exact fit
        String[] words6 = { "Hello", "World" };
        System.out.println(solution.fullJustify(words6, 11)); // Expected: ["Hello World"]

        // Test Case 7: Boundary case - maximum width
        String[] words7 = { "a" };
        System.out.println(solution.fullJustify(words7, 1)); // Expected: ["a"]

        // Test Case 8: Multiple gaps
        String[] words8 = { "a", "b", "c" };
        System.out.println(solution.fullJustify(words8, 5)); // Expected: ["a b","c "]

        // Test Case 9: Last line left justified
        String[] words9 = { "The", "quick", "brown", "fox" };
        System.out.println(solution.fullJustify(words9, 15)); // Expected: ["The quick brown","fox "]

        // Test Case 10: Single character words
        String[] words10 = { "a", "b", "c", "d", "e", "f", "g" };
        System.out.println(solution.fullJustify(words10, 10)); // Expected: ["a b c d","e f g "]
    }
}
