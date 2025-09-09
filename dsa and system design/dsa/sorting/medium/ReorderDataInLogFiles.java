package sorting.medium;

import java.util.*;

/**
 * LeetCode 937: Reorder Data in Log Files
 * URL: https://leetcode.com/problems/reorder-data-in-log-files/
 * Difficulty: Medium
 * Companies: Amazon, Google, Microsoft
 * Frequency: High
 */
public class ReorderDataInLogFiles {
    public String[] reorderLogFiles(String[] logs) {
        Arrays.sort(logs, (a, b) -> {
            String[] splitA = a.split(" ", 2);
            String[] splitB = b.split(" ", 2);

            boolean isDigitA = Character.isDigit(splitA[1].charAt(0));
            boolean isDigitB = Character.isDigit(splitB[1].charAt(0));

            // Both are letter logs
            if (!isDigitA && !isDigitB) {
                int comp = splitA[1].compareTo(splitB[1]);
                if (comp != 0)
                    return comp;
                return splitA[0].compareTo(splitB[0]);
            }

            // One letter log, one digit log
            if (!isDigitA && isDigitB)
                return -1;
            if (isDigitA && !isDigitB)
                return 1;

            // Both are digit logs - maintain original order
            return 0;
        });

        return logs;
    }

    // Alternative approach with separate lists
    public String[] reorderLogFilesAlt(String[] logs) {
        List<String> letterLogs = new ArrayList<>();
        List<String> digitLogs = new ArrayList<>();

        for (String log : logs) {
            if (Character.isDigit(log.split(" ")[1].charAt(0))) {
                digitLogs.add(log);
            } else {
                letterLogs.add(log);
            }
        }

        letterLogs.sort((a, b) -> {
            String[] splitA = a.split(" ", 2);
            String[] splitB = b.split(" ", 2);
            int comp = splitA[1].compareTo(splitB[1]);
            if (comp != 0)
                return comp;
            return splitA[0].compareTo(splitB[0]);
        });

        letterLogs.addAll(digitLogs);
        return letterLogs.toArray(new String[0]);
    }

    public static void main(String[] args) {
        ReorderDataInLogFiles solution = new ReorderDataInLogFiles();

        System.out.println(Arrays.toString(solution.reorderLogFiles(
                new String[] { "dig1 8 1 5 1", "let1 art can", "dig2 3 6", "let2 own kit dig", "let3 art zero" })));
        // ["let1 art can","let3 art zero","let2 own kit dig","dig1 8 1 5 1","dig2 3 6"]

        System.out.println(Arrays.toString(solution.reorderLogFiles(
                new String[] { "a1 9 2 3 1", "g1 act car", "zo4 4 7", "ab1 off key dog", "a8 act zoo" })));
        // ["g1 act car","a8 act zoo","ab1 off key dog","a1 9 2 3 1","zo4 4 7"]

        System.out.println(Arrays.toString(solution.reorderLogFiles(
                new String[] { "1 n u", "r 527", "j 893", "6 14", "6 82" }))); // ["1 n u","r 527","j 893","6 14","6
                                                                               // 82"]

        System.out.println(Arrays.toString(solution.reorderLogFiles(
                new String[] { "a1 9 2 3 1", "g1 act car" }))); // ["g1 act car","a1 9 2 3 1"]

        System.out.println(Arrays.toString(solution.reorderLogFiles(
                new String[] { "t kvr", "r 3 1", "i 403", "7 so", "t 54" }))); // ["7 so","t kvr","r 3 1","i 403","t
                                                                               // 54"]
    }
}
