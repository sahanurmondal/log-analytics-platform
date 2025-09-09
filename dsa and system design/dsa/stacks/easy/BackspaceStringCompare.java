package stacks.easy;

import java.util.*;

/**
 * LeetCode 844: Backspace String Compare
 * https://leetcode.com/problems/backspace-string-compare/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, Apple, Bloomberg
 * Frequency: High (Asked in 800+ interviews)
 *
 * Description:
 * Given two strings s and t, return true if they are equal when both are typed
 * into empty text editors.
 * '#' means a backspace character.
 * 
 * Note that after backspacing an empty text, the text will continue to be
 * empty.
 * 
 * Constraints:
 * - 1 <= s.length, t.length <= 200
 * - s and t only contain lowercase letters and '#' characters.
 * 
 * Follow-up: Can you solve it in O(1) space?
 * 
 * Follow-up Questions:
 * 1. How would you handle multiple backspace characters (e.g., "##")?
 * 2. Can you solve this for streams of characters?
 * 3. What about different backspace characters or commands?
 * 4. How to track the actual differences in the strings?
 * 5. Can you solve with custom backspace counts?
 * 6. What about handling undo/redo operations?
 */
public class BackspaceStringCompare {

    // Approach 1: Stack - O(m + n) time, O(m + n) space
    public static boolean backspaceCompare(String s, String t) {
        return buildString(s).equals(buildString(t));
    }

    private static String buildString(String str) {
        Stack<Character> stack = new Stack<>();

        for (char c : str.toCharArray()) {
            if (c == '#') {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(c);
            }
        }

        return stack.toString();
    }

    // Approach 2: StringBuilder (more efficient than stack) - O(m + n) time, O(m +
    // n) space
    public static boolean backspaceCompareStringBuilder(String s, String t) {
        return buildStringBuilder(s).equals(buildStringBuilder(t));
    }

    private static String buildStringBuilder(String str) {
        StringBuilder sb = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (c == '#') {
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    // Approach 3: Two Pointers (Optimal - O(1) space) - O(m + n) time, O(1) space
    public static boolean backspaceCompareTwoPointers(String s, String t) {
        int i = s.length() - 1;
        int j = t.length() - 1;
        int skipS = 0, skipT = 0;

        while (i >= 0 || j >= 0) {
            // Find next valid character in s
            while (i >= 0) {
                if (s.charAt(i) == '#') {
                    skipS++;
                    i--;
                } else if (skipS > 0) {
                    skipS--;
                    i--;
                } else {
                    break;
                }
            }

            // Find next valid character in t
            while (j >= 0) {
                if (t.charAt(j) == '#') {
                    skipT++;
                    j--;
                } else if (skipT > 0) {
                    skipT--;
                    j--;
                } else {
                    break;
                }
            }

            // Compare characters
            if (i >= 0 && j >= 0 && s.charAt(i) != t.charAt(j)) {
                return false;
            }

            // If one string is exhausted and the other isn't
            if ((i >= 0) != (j >= 0)) {
                return false;
            }

            i--;
            j--;
        }

        return true;
    }

    // Approach 4: Iterator Pattern for cleaner two-pointer approach
    public static boolean backspaceCompareIterator(String s, String t) {
        BackspaceIterator iterS = new BackspaceIterator(s);
        BackspaceIterator iterT = new BackspaceIterator(t);

        while (iterS.hasNext() || iterT.hasNext()) {
            char charS = iterS.hasNext() ? iterS.next() : '\0';
            char charT = iterT.hasNext() ? iterT.next() : '\0';

            if (charS != charT) {
                return false;
            }
        }

        return true;
    }

    private static class BackspaceIterator {
        private String str;
        private int index;

        public BackspaceIterator(String str) {
            this.str = str;
            this.index = str.length() - 1;
        }

        public boolean hasNext() {
            int skip = 0;
            int current = index;

            while (current >= 0) {
                if (str.charAt(current) == '#') {
                    skip++;
                    current--;
                } else if (skip > 0) {
                    skip--;
                    current--;
                } else {
                    return true;
                }
            }

            return false;
        }

        public char next() {
            int skip = 0;

            while (index >= 0) {
                if (str.charAt(index) == '#') {
                    skip++;
                    index--;
                } else if (skip > 0) {
                    skip--;
                    index--;
                } else {
                    return str.charAt(index--);
                }
            }

            throw new NoSuchElementException();
        }
    }

    // Follow-up 1: Handle multiple backspace characters
    public static class MultipleBackspace {

        // Handle "##" as double backspace, "###" as triple backspace, etc.
        public static boolean compareWithMultipleBackspace(String s, String t) {
            return buildWithMultipleBackspace(s).equals(buildWithMultipleBackspace(t));
        }

        private static String buildWithMultipleBackspace(String str) {
            StringBuilder sb = new StringBuilder();
            int i = 0;

            while (i < str.length()) {
                char c = str.charAt(i);

                if (c == '#') {
                    // Count consecutive backspaces
                    int backspaces = 0;
                    while (i < str.length() && str.charAt(i) == '#') {
                        backspaces++;
                        i++;
                    }

                    // Remove characters
                    for (int j = 0; j < backspaces && sb.length() > 0; j++) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else {
                    sb.append(c);
                    i++;
                }
            }

            return sb.toString();
        }

        // Two pointer approach for multiple backspaces
        public static boolean compareMultipleBackspaceTwoPointer(String s, String t) {
            int i = s.length() - 1;
            int j = t.length() - 1;

            while (i >= 0 || j >= 0) {
                i = findNextValidChar(s, i);
                j = findNextValidChar(t, j);

                if (i >= 0 && j >= 0 && s.charAt(i) != t.charAt(j)) {
                    return false;
                }

                if ((i >= 0) != (j >= 0)) {
                    return false;
                }

                i--;
                j--;
            }

            return true;
        }

        private static int findNextValidChar(String str, int index) {
            int skip = 0;

            while (index >= 0) {
                if (str.charAt(index) == '#') {
                    skip++;
                    index--;
                } else if (skip > 0) {
                    skip--;
                    index--;
                } else {
                    break;
                }
            }

            return index;
        }
    }

    // Follow-up 2: Stream processing
    public static class StreamProcessor {
        private StringBuilder current;

        public StreamProcessor() {
            this.current = new StringBuilder();
        }

        public void processCharacter(char c) {
            if (c == '#') {
                if (current.length() > 0) {
                    current.deleteCharAt(current.length() - 1);
                }
            } else {
                current.append(c);
            }
        }

        public String getCurrentString() {
            return current.toString();
        }

        public boolean equals(StreamProcessor other) {
            return this.current.toString().equals(other.current.toString());
        }

        public void reset() {
            current.setLength(0);
        }

        // Process entire string
        public static StreamProcessor processString(String str) {
            StreamProcessor processor = new StreamProcessor();
            for (char c : str.toCharArray()) {
                processor.processCharacter(c);
            }
            return processor;
        }

        // Compare two streams
        public static boolean compareStreams(String s, String t) {
            StreamProcessor processorS = processString(s);
            StreamProcessor processorT = processString(t);
            return processorS.equals(processorT);
        }
    }

    // Follow-up 3: Different backspace characters or commands
    public static class CustomCommands {

        public enum Command {
            BACKSPACE('#'),
            DELETE_WORD('$'),
            CLEAR_ALL('@');

            private final char symbol;

            Command(char symbol) {
                this.symbol = symbol;
            }

            public char getSymbol() {
                return symbol;
            }
        }

        public static boolean compareWithCustomCommands(String s, String t, Command... commands) {
            return buildWithCommands(s, commands).equals(buildWithCommands(t, commands));
        }

        private static String buildWithCommands(String str, Command... commands) {
            StringBuilder sb = new StringBuilder();
            Set<Character> commandChars = new HashSet<>();

            for (Command cmd : commands) {
                commandChars.add(cmd.getSymbol());
            }

            for (char c : str.toCharArray()) {
                if (commandChars.contains(c)) {
                    processCommand(sb, c, commands);
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }

        private static void processCommand(StringBuilder sb, char commandChar, Command... commands) {
            for (Command cmd : commands) {
                if (cmd.getSymbol() == commandChar) {
                    switch (cmd) {
                        case BACKSPACE:
                            if (sb.length() > 0) {
                                sb.deleteCharAt(sb.length() - 1);
                            }
                            break;
                        case DELETE_WORD:
                            deleteLastWord(sb);
                            break;
                        case CLEAR_ALL:
                            sb.setLength(0);
                            break;
                    }
                    break;
                }
            }
        }

        private static void deleteLastWord(StringBuilder sb) {
            // Remove trailing spaces
            while (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ') {
                sb.deleteCharAt(sb.length() - 1);
            }

            // Remove last word
            while (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        // Compare with multiple custom commands
        public static boolean compareCustom(String s, String t) {
            return compareWithCustomCommands(s, t,
                    Command.BACKSPACE, Command.DELETE_WORD, Command.CLEAR_ALL);
        }
    }

    // Follow-up 4: Track actual differences
    public static class DifferenceTracker {

        public static class ComparisonResult {
            boolean isEqual;
            List<Difference> differences;
            String finalS;
            String finalT;

            public ComparisonResult(boolean isEqual, List<Difference> differences, String finalS, String finalT) {
                this.isEqual = isEqual;
                this.differences = differences;
                this.finalS = finalS;
                this.finalT = finalT;
            }

            @Override
            public String toString() {
                return String.format("Equal: %s, S: '%s', T: '%s', Differences: %s",
                        isEqual, finalS, finalT, differences);
            }
        }

        public static class Difference {
            int position;
            char expectedChar;
            char actualChar;

            public Difference(int position, char expectedChar, char actualChar) {
                this.position = position;
                this.expectedChar = expectedChar;
                this.actualChar = actualChar;
            }

            @Override
            public String toString() {
                return String.format("Pos %d: expected '%c', got '%c'", position, expectedChar, actualChar);
            }
        }

        public static ComparisonResult compareWithDifferences(String s, String t) {
            String finalS = buildStringBuilder(s);
            String finalT = buildStringBuilder(t);

            List<Difference> differences = new ArrayList<>();
            int minLength = Math.min(finalS.length(), finalT.length());

            // Compare character by character
            for (int i = 0; i < minLength; i++) {
                if (finalS.charAt(i) != finalT.charAt(i)) {
                    differences.add(new Difference(i, finalS.charAt(i), finalT.charAt(i)));
                }
            }

            // Handle length differences
            if (finalS.length() != finalT.length()) {
                String longer = finalS.length() > finalT.length() ? finalS : finalT;

                for (int i = minLength; i < longer.length(); i++) {
                    differences.add(new Difference(i, longer.charAt(i), '\0'));
                }
            }

            return new ComparisonResult(finalS.equals(finalT), differences, finalS, finalT);
        }

        private static String buildStringBuilder(String str) {
            StringBuilder sb = new StringBuilder();

            for (char c : str.toCharArray()) {
                if (c == '#') {
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    // Follow-up 5: Custom backspace counts
    public static class CustomBackspaceCounts {

        // Use digits to represent backspace count (e.g., "2" means backspace 2 times)
        public static boolean compareWithBackspaceCounts(String s, String t) {
            return buildWithBackspaceCounts(s).equals(buildWithBackspaceCounts(t));
        }

        private static String buildWithBackspaceCounts(String str) {
            StringBuilder sb = new StringBuilder();

            for (char c : str.toCharArray()) {
                if (Character.isDigit(c)) {
                    int backspaces = Character.getNumericValue(c);
                    for (int i = 0; i < backspaces && sb.length() > 0; i++) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }

        // Mixed commands: '#' for single backspace, digits for multiple
        public static boolean compareWithMixedCommands(String s, String t) {
            return buildWithMixedCommands(s).equals(buildWithMixedCommands(t));
        }

        private static String buildWithMixedCommands(String str) {
            StringBuilder sb = new StringBuilder();

            for (char c : str.toCharArray()) {
                if (c == '#') {
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else if (Character.isDigit(c)) {
                    int backspaces = Character.getNumericValue(c);
                    for (int i = 0; i < backspaces && sb.length() > 0; i++) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    // Follow-up 6: Undo/Redo operations
    public static class UndoRedoEditor {
        private List<String> history;
        private int currentIndex;

        public UndoRedoEditor() {
            this.history = new ArrayList<>();
            this.history.add(""); // Initial empty state
            this.currentIndex = 0;
        }

        public void type(char c) {
            String current = getCurrentState();
            String newState = current + c;
            addState(newState);
        }

        public void backspace() {
            String current = getCurrentState();
            if (current.length() > 0) {
                String newState = current.substring(0, current.length() - 1);
                addState(newState);
            }
        }

        public boolean undo() {
            if (currentIndex > 0) {
                currentIndex--;
                return true;
            }
            return false;
        }

        public boolean redo() {
            if (currentIndex < history.size() - 1) {
                currentIndex++;
                return true;
            }
            return false;
        }

        public String getCurrentState() {
            return history.get(currentIndex);
        }

        private void addState(String state) {
            // Remove any future states if we're not at the end
            while (history.size() > currentIndex + 1) {
                history.remove(history.size() - 1);
            }

            history.add(state);
            currentIndex++;
        }

        public List<String> getHistory() {
            return new ArrayList<>(history);
        }

        // Process string with undo/redo commands
        public static UndoRedoEditor processWithUndoRedo(String commands) {
            UndoRedoEditor editor = new UndoRedoEditor();

            for (char c : commands.toCharArray()) {
                switch (c) {
                    case '#':
                        editor.backspace();
                        break;
                    case 'U': // Undo
                        editor.undo();
                        break;
                    case 'R': // Redo
                        editor.redo();
                        break;
                    default:
                        editor.type(c);
                        break;
                }
            }

            return editor;
        }

        public static boolean compareWithUndoRedo(String s, String t) {
            UndoRedoEditor editorS = processWithUndoRedo(s);
            UndoRedoEditor editorT = processWithUndoRedo(t);
            return editorS.getCurrentState().equals(editorT.getCurrentState());
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(String s, String t, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("String lengths: s=" + s.length() + ", t=" + t.length() +
                    ", Iterations: " + iterations);

            // Stack approach
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                backspaceCompare(s, t);
            }
            long stackTime = System.nanoTime() - start;

            // StringBuilder approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                backspaceCompareStringBuilder(s, t);
            }
            long sbTime = System.nanoTime() - start;

            // Two pointers approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                backspaceCompareTwoPointers(s, t);
            }
            long twoPointerTime = System.nanoTime() - start;

            // Iterator approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                backspaceCompareIterator(s, t);
            }
            long iteratorTime = System.nanoTime() - start;

            System.out.println("Stack: " + stackTime / 1_000_000 + " ms");
            System.out.println("StringBuilder: " + sbTime / 1_000_000 + " ms");
            System.out.println("Two Pointers: " + twoPointerTime / 1_000_000 + " ms");
            System.out.println("Iterator: " + iteratorTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        String s1 = "ab#c", t1 = "ad#c";
        System.out.println("s = \"" + s1 + "\", t = \"" + t1 + "\"");
        System.out.println("Stack: " + backspaceCompare(s1, t1));
        System.out.println("StringBuilder: " + backspaceCompareStringBuilder(s1, t1));
        System.out.println("Two Pointers: " + backspaceCompareTwoPointers(s1, t1));
        System.out.println("Iterator: " + backspaceCompareIterator(s1, t1));

        String s2 = "ab##", t2 = "c#d#";
        System.out.println("\ns = \"" + s2 + "\", t = \"" + t2 + "\"");
        System.out.println("Stack: " + backspaceCompare(s2, t2));
        System.out.println("StringBuilder: " + backspaceCompareStringBuilder(s2, t2));
        System.out.println("Two Pointers: " + backspaceCompareTwoPointers(s2, t2));
        System.out.println("Iterator: " + backspaceCompareIterator(s2, t2));

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");

        // Empty strings after backspaces
        String s3 = "a##c", t3 = "#a#c";
        System.out.println("s = \"" + s3 + "\", t = \"" + t3 + "\"");
        System.out.println("Result: " + backspaceCompare(s3, t3));

        // Only backspaces
        String s4 = "a#c", t4 = "b";
        System.out.println("s = \"" + s4 + "\", t = \"" + t4 + "\"");
        System.out.println("Result: " + backspaceCompare(s4, t4));

        // Multiple consecutive backspaces
        String s5 = "bbbextm", t5 = "bbb#extm";
        System.out.println("s = \"" + s5 + "\", t = \"" + t5 + "\"");
        System.out.println("Result: " + backspaceCompare(s5, t5));

        // Backspace at beginning (should be ignored)
        String s6 = "#ab", t6 = "ab";
        System.out.println("s = \"" + s6 + "\", t = \"" + t6 + "\"");
        System.out.println("Result: " + backspaceCompare(s6, t6));

        // Test Case 3: Multiple backspace handling
        System.out.println("\n=== Test Case 3: Multiple Backspace Handling ===");

        String ms1 = "ab##c", mt1 = "a#c";
        System.out.println("Multiple backspace - s = \"" + ms1 + "\", t = \"" + mt1 + "\"");
        System.out.println("Standard: " + MultipleBackspace.compareWithMultipleBackspace(ms1, mt1));
        System.out.println("Two pointer: " + MultipleBackspace.compareMultipleBackspaceTwoPointer(ms1, mt1));

        // Test Case 4: Stream processing
        System.out.println("\n=== Test Case 4: Stream Processing ===");

        StreamProcessor processor1 = new StreamProcessor();
        StreamProcessor processor2 = new StreamProcessor();

        String stream1 = "ab#c";
        String stream2 = "ad#c";

        System.out.println("Processing streams: \"" + stream1 + "\" vs \"" + stream2 + "\"");

        for (char c : stream1.toCharArray()) {
            processor1.processCharacter(c);
            System.out.println("Stream 1 after '" + c + "': \"" + processor1.getCurrentString() + "\"");
        }

        for (char c : stream2.toCharArray()) {
            processor2.processCharacter(c);
            System.out.println("Stream 2 after '" + c + "': \"" + processor2.getCurrentString() + "\"");
        }

        System.out.println("Streams equal: " + processor1.equals(processor2));
        System.out.println("Compare using static method: " + StreamProcessor.compareStreams(stream1, stream2));

        // Test Case 5: Custom commands
        System.out.println("\n=== Test Case 5: Custom Commands ===");

        String cs1 = "hello$ world#d"; // $ deletes word, # deletes character
        String cs2 = "world";

        System.out.println("Custom commands - s = \"" + cs1 + "\", t = \"" + cs2 + "\"");
        System.out.println("With word deletion: " + CustomCommands.compareCustom(cs1, cs2));

        String cs3 = "abc@def"; // @ clears all
        String cs4 = "def";
        System.out.println("Clear all - s = \"" + cs3 + "\", t = \"" + cs4 + "\"");
        System.out.println("With clear all: " + CustomCommands.compareCustom(cs3, cs4));

        // Test Case 6: Difference tracking
        System.out.println("\n=== Test Case 6: Difference Tracking ===");

        String ds1 = "ab#c", dt1 = "ad#f";
        System.out.println("Tracking differences - s = \"" + ds1 + "\", t = \"" + dt1 + "\"");

        DifferenceTracker.ComparisonResult result = DifferenceTracker.compareWithDifferences(ds1, dt1);
        System.out.println("Result: " + result);

        // Test Case 7: Custom backspace counts
        System.out.println("\n=== Test Case 7: Custom Backspace Counts ===");

        String bs1 = "abc2d"; // "2" means backspace twice
        String bs2 = "ad";

        System.out.println("Backspace counts - s = \"" + bs1 + "\", t = \"" + bs2 + "\"");
        System.out.println("With counts: " + CustomBackspaceCounts.compareWithBackspaceCounts(bs1, bs2));

        String bs3 = "ab#c1"; // Mixed: # for single, 1 for one backspace
        String bs4 = "a";
        System.out.println("Mixed commands - s = \"" + bs3 + "\", t = \"" + bs4 + "\"");
        System.out.println("Mixed: " + CustomBackspaceCounts.compareWithMixedCommands(bs3, bs4));

        // Test Case 8: Undo/Redo operations
        System.out.println("\n=== Test Case 8: Undo/Redo Operations ===");

        UndoRedoEditor editor = new UndoRedoEditor();

        System.out.println("Typing sequence: a, b, c, backspace, undo, d");
        editor.type('a');
        System.out.println("After 'a': " + editor.getCurrentState());

        editor.type('b');
        System.out.println("After 'b': " + editor.getCurrentState());

        editor.type('c');
        System.out.println("After 'c': " + editor.getCurrentState());

        editor.backspace();
        System.out.println("After backspace: " + editor.getCurrentState());

        editor.undo();
        System.out.println("After undo: " + editor.getCurrentState());

        editor.type('d');
        System.out.println("After 'd': " + editor.getCurrentState());

        System.out.println("Full history: " + editor.getHistory());

        // Test undo/redo string processing
        String ur1 = "ab#U", ur2 = "a"; // U = undo
        System.out.println("\nUndo/Redo compare - s = \"" + ur1 + "\", t = \"" + ur2 + "\"");
        System.out.println("Result: " + UndoRedoEditor.compareWithUndoRedo(ur1, ur2));

        // Test Case 9: Large string performance
        System.out.println("\n=== Test Case 9: Large String Performance ===");

        StringBuilder largeSB = new StringBuilder();
        StringBuilder largeTB = new StringBuilder();

        Random random = new Random(42);
        int size = 1000;

        for (int i = 0; i < size; i++) {
            if (random.nextDouble() < 0.1) { // 10% chance of backspace
                largeSB.append('#');
                largeTB.append('#');
            } else {
                char c = (char) ('a' + random.nextInt(26));
                largeSB.append(c);
                largeTB.append(c);
            }
        }

        String largeS = largeSB.toString();
        String largeT = largeTB.toString();

        long start = System.currentTimeMillis();
        boolean largeResult = backspaceCompareTwoPointers(largeS, largeT);
        long end = System.currentTimeMillis();

        System.out.println("Large strings (size=" + size + " each): " + largeResult);
        System.out.println("Time: " + (end - start) + " ms");

        // Test Case 10: Stress test
        System.out.println("\n=== Test Case 10: Stress Test ===");

        int testCases = 1000;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int len1 = random.nextInt(20) + 1;
            int len2 = random.nextInt(20) + 1;

            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();

            for (int i = 0; i < len1; i++) {
                if (random.nextDouble() < 0.2) {
                    sb1.append('#');
                } else {
                    sb1.append((char) ('a' + random.nextInt(26)));
                }
            }

            for (int i = 0; i < len2; i++) {
                if (random.nextDouble() < 0.2) {
                    sb2.append('#');
                } else {
                    sb2.append((char) ('a' + random.nextInt(26)));
                }
            }

            String testS = sb1.toString();
            String testT = sb2.toString();

            // Test all approaches
            boolean result1 = backspaceCompare(testS, testT);
            boolean result2 = backspaceCompareTwoPointers(testS, testT);
            boolean result3 = backspaceCompareIterator(testS, testT);

            if (result1 == result2 && result2 == result3) {
                passed++;
            } else {
                System.out.println("Failed test: s=\"" + testS + "\", t=\"" + testT + "\"");
                System.out.println("Stack: " + result1 + ", TwoPointer: " + result2 +
                        ", Iterator: " + result3);
                break;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Performance comparison
        String perfS = "ab#cd##e#f";
        String perfT = "aef";
        PerformanceComparison.compareApproaches(perfS, perfT, 100000);

        System.out.println("\nBackspace String Compare testing completed successfully!");
    }
}
