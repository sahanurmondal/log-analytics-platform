package stacks.medium;

import java.util.*;

/**
 * LeetCode 394: Decode String
 * https://leetcode.com/problems/decode-string/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, Apple, Bloomberg, Adobe
 * Frequency: Very High (Asked in 1000+ interviews)
 *
 * Description:
 * Given an encoded string, return its decoded string.
 * The encoding rule is: k[encoded_string], where the encoded_string inside the
 * square brackets
 * is being repeated exactly k times. Note that k is guaranteed to be a positive
 * integer.
 * 
 * You may assume that the input string is always valid; no extra white spaces,
 * square brackets
 * are well-formed, etc. Furthermore, you may assume that the original data does
 * not contain
 * any digits and that digits are only for those repeat numbers, k.
 * 
 * Constraints:
 * - 1 <= s.length <= 30
 * - s consists of lowercase English letters, digits, and square brackets '[]'
 * - s is guaranteed to be a valid input
 * - All the integers in s are in the range [1, 300]
 * 
 * Follow-up Questions:
 * 1. How would you handle nested encodings with different brackets?
 * 2. Can you solve this iteratively without recursion?
 * 3. What about handling invalid inputs gracefully?
 * 4. How to optimize for very large repeat counts?
 * 5. Can you handle multiple types of encoding patterns?
 * 6. What about parallel processing for large strings?
 */
public class DecodeString {

    // Approach 1: Stack-based solution - O(maxK * n) time, O(m) space
    public static String decodeString(String s) {
        Stack<Integer> countStack = new Stack<>();
        Stack<StringBuilder> stringStack = new Stack<>();
        StringBuilder currentString = new StringBuilder();
        int k = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0'); // Build multi-digit number
            } else if (c == '[') {
                // Push current state to stacks
                countStack.push(k);
                stringStack.push(currentString);

                // Reset for new level
                k = 0;
                currentString = new StringBuilder();
            } else if (c == ']') {
                // Pop and decode current level
                StringBuilder temp = currentString;
                currentString = stringStack.pop();
                int repeatCount = countStack.pop();

                // Repeat and append
                for (int i = 0; i < repeatCount; i++) {
                    currentString.append(temp);
                }
            } else {
                currentString.append(c);
            }
        }

        return currentString.toString();
    }

    // Approach 2: Recursive solution - O(maxK * n) time, O(n) space
    public static String decodeStringRecursive(String s) {
        int[] index = { 0 };
        return decodeHelper(s, index);
    }

    private static String decodeHelper(String s, int[] index) {
        StringBuilder result = new StringBuilder();
        int num = 0;

        while (index[0] < s.length()) {
            char c = s.charAt(index[0]);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '[') {
                index[0]++; // Skip '['
                String decoded = decodeHelper(s, index);

                // Repeat decoded string num times
                for (int i = 0; i < num; i++) {
                    result.append(decoded);
                }

                num = 0; // Reset number
            } else if (c == ']') {
                break; // End of current level
            } else {
                result.append(c);
            }

            index[0]++;
        }

        return result.toString();
    }

    // Approach 3: DFS with cleaner recursion - O(maxK * n) time, O(n) space
    public static String decodeStringDFS(String s) {
        return dfs(s, new int[] { 0 });
    }

    private static String dfs(String s, int[] i) {
        StringBuilder sb = new StringBuilder();
        int num = 0;

        while (i[0] < s.length()) {
            char c = s.charAt(i[0]);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '[') {
                i[0]++; // Move past '['
                String nested = dfs(s, i);

                for (int k = 0; k < num; k++) {
                    sb.append(nested);
                }

                num = 0;
            } else if (c == ']') {
                break;
            } else {
                sb.append(c);
            }

            i[0]++;
        }

        return sb.toString();
    }

    // Approach 4: Two-stack approach with better organization
    public static String decodeStringTwoStacks(String s) {
        Stack<String> stringStack = new Stack<>();
        Stack<Integer> numStack = new Stack<>();
        StringBuilder currentStr = new StringBuilder();
        int num = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '[') {
                stringStack.push(currentStr.toString());
                numStack.push(num);
                currentStr = new StringBuilder();
                num = 0;
            } else if (c == ']') {
                StringBuilder temp = new StringBuilder(stringStack.pop());
                int count = numStack.pop();

                for (int i = 0; i < count; i++) {
                    temp.append(currentStr);
                }

                currentStr = temp;
            } else {
                currentStr.append(c);
            }
        }

        return currentStr.toString();
    }

    // Follow-up 1: Handle different bracket types
    public static class DifferentBrackets {

        public static String decodeWithDifferentBrackets(String s) {
            Stack<Integer> countStack = new Stack<>();
            Stack<StringBuilder> stringStack = new Stack<>();
            Stack<Character> bracketStack = new Stack<>();
            StringBuilder currentString = new StringBuilder();
            int k = 0;

            Map<Character, Character> bracketPairs = Map.of(
                    ')', '(',
                    ']', '[',
                    '}', '{');

            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    k = k * 10 + (c - '0');
                } else if (c == '(' || c == '[' || c == '{') {
                    countStack.push(k);
                    stringStack.push(currentString);
                    bracketStack.push(c);

                    k = 0;
                    currentString = new StringBuilder();
                } else if (c == ')' || c == ']' || c == '}') {
                    if (!bracketStack.isEmpty() && bracketStack.peek() == bracketPairs.get(c)) {
                        StringBuilder temp = currentString;
                        currentString = stringStack.pop();
                        int repeatCount = countStack.pop();
                        bracketStack.pop();

                        for (int i = 0; i < repeatCount; i++) {
                            currentString.append(temp);
                        }
                    }
                } else {
                    currentString.append(c);
                }
            }

            return currentString.toString();
        }

        // Handle mixed encoding patterns
        public static String decodeWithPatterns(String s) {
            Stack<Integer> countStack = new Stack<>();
            Stack<StringBuilder> stringStack = new Stack<>();
            StringBuilder currentString = new StringBuilder();
            int k = 0;

            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    k = k * 10 + (c - '0');
                } else if (c == '[' || c == '(') {
                    countStack.push(k);
                    stringStack.push(currentString);

                    k = 0;
                    currentString = new StringBuilder();
                } else if (c == ']' || c == ')') {
                    StringBuilder temp = currentString;
                    currentString = stringStack.pop();
                    int repeatCount = countStack.pop();

                    for (int i = 0; i < repeatCount; i++) {
                        currentString.append(temp);
                    }
                } else {
                    currentString.append(c);
                }
            }

            return currentString.toString();
        }
    }

    // Follow-up 2: Iterative approach without explicit recursion
    public static class IterativeApproach {

        public static String decodeStringIterative(String s) {
            Queue<Character> queue = new LinkedList<>();
            for (char c : s.toCharArray()) {
                queue.offer(c);
            }

            return decodeQueue(queue);
        }

        private static String decodeQueue(Queue<Character> queue) {
            StringBuilder sb = new StringBuilder();
            int num = 0;

            while (!queue.isEmpty()) {
                char c = queue.poll();

                if (Character.isDigit(c)) {
                    num = num * 10 + (c - '0');
                } else if (c == '[') {
                    String decoded = decodeQueue(queue);

                    for (int i = 0; i < num; i++) {
                        sb.append(decoded);
                    }

                    num = 0;
                } else if (c == ']') {
                    break;
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }

        // Pure iterative with manual stack simulation
        public static String decodePureIterative(String s) {
            List<String> segments = new ArrayList<>();
            List<Integer> multipliers = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            int num = 0;

            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    num = num * 10 + (c - '0');
                } else if (c == '[') {
                    segments.add(current.toString());
                    multipliers.add(num);
                    current = new StringBuilder();
                    num = 0;
                } else if (c == ']') {
                    String segment = segments.remove(segments.size() - 1);
                    int mult = multipliers.remove(multipliers.size() - 1);

                    StringBuilder temp = new StringBuilder(segment);
                    for (int i = 0; i < mult; i++) {
                        temp.append(current);
                    }

                    current = temp;
                } else {
                    current.append(c);
                }
            }

            return current.toString();
        }
    }

    // Follow-up 3: Handle invalid inputs gracefully
    public static class ErrorHandling {

        public static class DecodeResult {
            String result;
            boolean isValid;
            String errorMessage;

            public DecodeResult(String result, boolean isValid, String errorMessage) {
                this.result = result;
                this.isValid = isValid;
                this.errorMessage = errorMessage;
            }

            @Override
            public String toString() {
                return isValid ? result : "Error: " + errorMessage;
            }
        }

        public static DecodeResult decodeWithValidation(String s) {
            try {
                if (s == null || s.isEmpty()) {
                    return new DecodeResult("", false, "Input is null or empty");
                }

                if (!isValidInput(s)) {
                    return new DecodeResult("", false, "Invalid input format");
                }

                String result = decodeString(s);
                return new DecodeResult(result, true, null);

            } catch (Exception e) {
                return new DecodeResult("", false, e.getMessage());
            }
        }

        private static boolean isValidInput(String s) {
            Stack<Character> brackets = new Stack<>();
            boolean hasNumber = false;

            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    hasNumber = true;
                } else if (c == '[') {
                    if (!hasNumber) {
                        return false; // No number before bracket
                    }
                    brackets.push(c);
                    hasNumber = false;
                } else if (c == ']') {
                    if (brackets.isEmpty() || brackets.pop() != '[') {
                        return false; // Mismatched brackets
                    }
                } else if (!Character.isLowerCase(c)) {
                    return false; // Invalid character
                }
            }

            return brackets.isEmpty(); // All brackets should be matched
        }

        public static DecodeResult decodeWithLimits(String s, int maxLength, int maxDepth) {
            if (s.length() > maxLength) {
                return new DecodeResult("", false, "Input too long");
            }

            int depth = calculateMaxDepth(s);
            if (depth > maxDepth) {
                return new DecodeResult("", false, "Nesting too deep");
            }

            return decodeWithValidation(s);
        }

        private static int calculateMaxDepth(String s) {
            int depth = 0;
            int maxDepth = 0;

            for (char c : s.toCharArray()) {
                if (c == '[') {
                    depth++;
                    maxDepth = Math.max(maxDepth, depth);
                } else if (c == ']') {
                    depth--;
                }
            }

            return maxDepth;
        }
    }

    // Follow-up 4: Optimize for large repeat counts
    public static class OptimizedForLargeNumbers {

        // Use string multiplication for large numbers
        public static String decodeOptimized(String s) {
            Stack<Integer> countStack = new Stack<>();
            Stack<StringBuilder> stringStack = new Stack<>();
            StringBuilder currentString = new StringBuilder();
            int k = 0;

            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    k = k * 10 + (c - '0');
                } else if (c == '[') {
                    countStack.push(k);
                    stringStack.push(currentString);

                    k = 0;
                    currentString = new StringBuilder();
                } else if (c == ']') {
                    StringBuilder temp = currentString;
                    currentString = stringStack.pop();
                    int repeatCount = countStack.pop();

                    // Optimized string multiplication
                    String repeated = multiplyString(temp.toString(), repeatCount);
                    currentString.append(repeated);
                } else {
                    currentString.append(c);
                }
            }

            return currentString.toString();
        }

        private static String multiplyString(String str, int count) {
            if (count == 0)
                return "";
            if (count == 1)
                return str;

            StringBuilder result = new StringBuilder(str.length() * count);

            // Use doubling for efficiency with very large counts
            String current = str;
            while (count > 0) {
                if (count % 2 == 1) {
                    result.append(current);
                }
                current = current + current;
                count /= 2;
            }

            return result.toString();
        }

        // Memory-efficient approach for very large results
        public static class LazyDecodedString {
            private String pattern;
            private int repeatCount;
            private List<LazyDecodedString> children;

            public LazyDecodedString(String pattern, int repeatCount) {
                this.pattern = pattern;
                this.repeatCount = repeatCount;
                this.children = new ArrayList<>();
            }

            public void addChild(LazyDecodedString child) {
                children.add(child);
            }

            public long calculateLength() {
                long length = pattern.length() * (long) repeatCount;

                for (LazyDecodedString child : children) {
                    length += child.calculateLength() * repeatCount;
                }

                return length;
            }

            public String materialize(int maxLength) {
                long totalLength = calculateLength();
                if (totalLength > maxLength) {
                    return "[Result too large: " + totalLength + " characters]";
                }

                StringBuilder result = new StringBuilder();
                for (int i = 0; i < repeatCount && result.length() < maxLength; i++) {
                    result.append(pattern);

                    for (LazyDecodedString child : children) {
                        if (result.length() >= maxLength)
                            break;
                        result.append(child.materialize(maxLength - result.length()));
                    }
                }

                return result.toString();
            }
        }
    }

    // Follow-up 5: Multiple encoding patterns
    public static class MultiplePatterns {

        // Handle both k[string] and string*k patterns
        public static String decodeMultiplePatterns(String s) {
            StringBuilder result = new StringBuilder();
            int i = 0;

            while (i < s.length()) {
                if (Character.isDigit(s.charAt(i))) {
                    // Handle k[string] pattern
                    int num = 0;
                    while (i < s.length() && Character.isDigit(s.charAt(i))) {
                        num = num * 10 + (s.charAt(i) - '0');
                        i++;
                    }

                    if (i < s.length() && s.charAt(i) == '[') {
                        i++; // Skip '['
                        int bracketCount = 1;
                        int start = i;

                        while (i < s.length() && bracketCount > 0) {
                            if (s.charAt(i) == '[')
                                bracketCount++;
                            if (s.charAt(i) == ']')
                                bracketCount--;
                            i++;
                        }

                        String inner = s.substring(start, i - 1);
                        String decoded = decodeMultiplePatterns(inner);

                        for (int j = 0; j < num; j++) {
                            result.append(decoded);
                        }
                    }
                } else if (Character.isLetter(s.charAt(i))) {
                    // Check for string*k pattern
                    int start = i;
                    while (i < s.length() && Character.isLetter(s.charAt(i))) {
                        i++;
                    }

                    String str = s.substring(start, i);

                    if (i < s.length() && s.charAt(i) == '*') {
                        i++; // Skip '*'
                        int num = 0;
                        while (i < s.length() && Character.isDigit(s.charAt(i))) {
                            num = num * 10 + (s.charAt(i) - '0');
                            i++;
                        }

                        for (int j = 0; j < num; j++) {
                            result.append(str);
                        }
                    } else {
                        result.append(str);
                    }
                } else {
                    i++; // Skip unknown characters
                }
            }

            return result.toString();
        }

        // Handle regex-like patterns
        public static String decodeRegexPatterns(String s) {
            // Handle patterns like a{3}, (ab){2}, etc.
            StringBuilder result = new StringBuilder();
            int i = 0;

            while (i < s.length()) {
                if (s.charAt(i) == '(') {
                    // Find matching closing parenthesis
                    int parenCount = 1;
                    int start = i + 1;
                    i++;

                    while (i < s.length() && parenCount > 0) {
                        if (s.charAt(i) == '(')
                            parenCount++;
                        if (s.charAt(i) == ')')
                            parenCount--;
                        i++;
                    }

                    String group = s.substring(start, i - 1);

                    // Check for repetition
                    if (i < s.length() && s.charAt(i) == '{') {
                        i++; // Skip '{'
                        int num = 0;
                        while (i < s.length() && Character.isDigit(s.charAt(i))) {
                            num = num * 10 + (s.charAt(i) - '0');
                            i++;
                        }
                        i++; // Skip '}'

                        String decoded = decodeRegexPatterns(group);
                        for (int j = 0; j < num; j++) {
                            result.append(decoded);
                        }
                    } else {
                        result.append(decodeRegexPatterns(group));
                    }
                } else if (Character.isLetter(s.charAt(i))) {
                    char c = s.charAt(i);
                    i++;

                    // Check for repetition
                    if (i < s.length() && s.charAt(i) == '{') {
                        i++; // Skip '{'
                        int num = 0;
                        while (i < s.length() && Character.isDigit(s.charAt(i))) {
                            num = num * 10 + (s.charAt(i) - '0');
                            i++;
                        }
                        i++; // Skip '}'

                        for (int j = 0; j < num; j++) {
                            result.append(c);
                        }
                    } else {
                        result.append(c);
                    }
                } else {
                    i++;
                }
            }

            return result.toString();
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(String s, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Input: \"" + s + "\", Iterations: " + iterations);

            // Stack approach
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                decodeString(s);
            }
            long stackTime = System.nanoTime() - start;

            // Recursive approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                decodeStringRecursive(s);
            }
            long recursiveTime = System.nanoTime() - start;

            // DFS approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                decodeStringDFS(s);
            }
            long dfsTime = System.nanoTime() - start;

            // Two stacks approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                decodeStringTwoStacks(s);
            }
            long twoStackTime = System.nanoTime() - start;

            System.out.println("Stack: " + stackTime / 1_000_000 + " ms");
            System.out.println("Recursive: " + recursiveTime / 1_000_000 + " ms");
            System.out.println("DFS: " + dfsTime / 1_000_000 + " ms");
            System.out.println("Two Stacks: " + twoStackTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        String s1 = "3[a]2[bc]";
        System.out.println("Input: \"" + s1 + "\"");
        System.out.println("Stack: " + decodeString(s1));
        System.out.println("Recursive: " + decodeStringRecursive(s1));
        System.out.println("DFS: " + decodeStringDFS(s1));
        System.out.println("Two Stacks: " + decodeStringTwoStacks(s1));

        String s2 = "2[abc]3[cd]ef";
        System.out.println("\nInput: \"" + s2 + "\"");
        System.out.println("Result: " + decodeString(s2));

        String s3 = "abc3[cd]xyz";
        System.out.println("Input: \"" + s3 + "\"");
        System.out.println("Result: " + decodeString(s3));

        // Test Case 2: Nested encodings
        System.out.println("\n=== Test Case 2: Nested Encodings ===");

        String nested1 = "2[a3[b]]";
        System.out.println("Input: \"" + nested1 + "\"");
        System.out.println("Result: " + decodeString(nested1));

        String nested2 = "3[a2[c]]";
        System.out.println("Input: \"" + nested2 + "\"");
        System.out.println("Result: " + decodeString(nested2));

        String nested3 = "2[abc3[cd]]";
        System.out.println("Input: \"" + nested3 + "\"");
        System.out.println("Result: " + decodeString(nested3));

        String complex = "3[a2[b3[c]]]";
        System.out.println("Complex: \"" + complex + "\"");
        System.out.println("Result: " + decodeString(complex));

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        // No encoding
        System.out.println("No encoding 'abc': " + decodeString("abc"));

        // Single character
        System.out.println("Single '1[a]': " + decodeString("1[a]"));

        // Large number
        System.out.println("Large number '10[a]': " + decodeString("10[a]"));

        // Multiple digits
        System.out.println("Multiple digits '100[a]': " + decodeString("100[a]"));

        // Empty brackets
        System.out.println("Empty brackets '2[]': " + decodeString("2[]"));

        // Test Case 4: Different bracket types
        System.out.println("\n=== Test Case 4: Different Bracket Types ===");

        String diff1 = "3(abc)2[def]";
        System.out.println("Mixed brackets: \"" + diff1 + "\"");
        System.out.println("Result: " + DifferentBrackets.decodeWithDifferentBrackets(diff1));

        String diff2 = "2{ab3[c]}";
        System.out.println("Curly brackets: \"" + diff2 + "\"");
        System.out.println("Result: " + DifferentBrackets.decodeWithPatterns(diff2));

        // Test Case 5: Iterative approaches
        System.out.println("\n=== Test Case 5: Iterative Approaches ===");

        String iter1 = "3[a2[b]]";
        System.out.println("Input: \"" + iter1 + "\"");
        System.out.println("Iterative: " + IterativeApproach.decodeStringIterative(iter1));
        System.out.println("Pure iterative: " + IterativeApproach.decodePureIterative(iter1));

        // Test Case 6: Error handling
        System.out.println("\n=== Test Case 6: Error Handling ===");

        String[] invalidInputs = {
                "", // Empty
                "2[", // Unmatched bracket
                "2]", // Wrong bracket order
                "[abc]", // No number
                "2[abc", // Missing closing bracket
                "abc]", // Extra closing bracket
                "2[a1b]" // Invalid character mix
        };

        for (String invalid : invalidInputs) {
            ErrorHandling.DecodeResult result = ErrorHandling.decodeWithValidation(invalid);
            System.out.println("\"" + invalid + "\" -> " + result);
        }

        // Test limits
        ErrorHandling.DecodeResult limitResult = ErrorHandling.decodeWithLimits("2[a3[b]]", 20, 5);
        System.out.println("With limits: " + limitResult);

        // Test Case 7: Optimized for large numbers
        System.out.println("\n=== Test Case 7: Large Numbers ===");

        String large1 = "1000[a]";
        System.out.println("Large repeat (1000[a]): length = " + decodeString(large1).length());

        String large2 = "3[2[a]5[b]]";
        System.out.println("Input: \"" + large2 + "\"");
        System.out.println("Optimized: " + OptimizedForLargeNumbers.decodeOptimized(large2));

        // Test lazy evaluation
        OptimizedForLargeNumbers.LazyDecodedString lazy = new OptimizedForLargeNumbers.LazyDecodedString("abc", 1000);
        System.out.println("Lazy string length: " + lazy.calculateLength());
        System.out.println("Materialized (first 50): " + lazy.materialize(50));

        // Test Case 8: Multiple patterns
        System.out.println("\n=== Test Case 8: Multiple Patterns ===");

        String multi1 = "3[abc]def*2";
        System.out.println("Mixed patterns: \"" + multi1 + "\"");
        System.out.println("Result: " + MultiplePatterns.decodeMultiplePatterns(multi1));

        String regex1 = "a{3}(bc){2}";
        System.out.println("Regex-like: \"" + regex1 + "\"");
        System.out.println("Result: " + MultiplePatterns.decodeRegexPatterns(regex1));

        // Test Case 9: Complex nested cases
        System.out.println("\n=== Test Case 9: Complex Cases ===");

        String[] complexCases = {
                "2[a]3[b2[c]]",
                "10[a2[b]]",
                "a2[b3[c4[d]]]e",
                "3[a2[b]c]",
                "2[abc]3[cd]ef",
                "100[leetcode]"
        };

        for (String complexCase : complexCases) {
            System.out.println("\"" + complexCase + "\" -> \"" + decodeString(complexCase) + "\"");
        }

        // Test Case 10: Performance comparison
        System.out.println("\n=== Test Case 10: Performance Comparison ===");

        String perfString = "3[a2[b]]2[c]";
        PerformanceComparison.compareApproaches(perfString, 10000);

        // Test Case 11: Stress test
        System.out.println("\n=== Test Case 11: Stress Test ===");

        Random random = new Random(42);
        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            String testString = generateRandomValidString(random, 10);

            try {
                String result1 = decodeString(testString);
                String result2 = decodeStringRecursive(testString);
                String result3 = decodeStringDFS(testString);

                if (result1.equals(result2) && result2.equals(result3)) {
                    passed++;
                } else {
                    System.out.println("Failed test: \"" + testString + "\"");
                    System.out.println("Stack: " + result1);
                    System.out.println("Recursive: " + result2);
                    System.out.println("DFS: " + result3);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Exception on: \"" + testString + "\" - " + e.getMessage());
                break;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        System.out.println("\nDecode String testing completed successfully!");
    }

    // Helper method for stress testing
    private static String generateRandomValidString(Random random, int maxLength) {
        StringBuilder sb = new StringBuilder();
        generateRandomHelper(sb, random, maxLength, 0);
        return sb.toString();
    }

    private static void generateRandomHelper(StringBuilder sb, Random random, int maxLength, int depth) {
        if (depth > 3 || sb.length() >= maxLength)
            return;

        int operations = random.nextInt(3) + 1;

        for (int i = 0; i < operations && sb.length() < maxLength; i++) {
            if (random.nextBoolean() && depth < 3) {
                // Add encoded section
                int count = random.nextInt(3) + 1;
                sb.append(count).append('[');
                generateRandomHelper(sb, random, maxLength - sb.length() - 1, depth + 1);
                sb.append(']');
            } else {
                // Add regular characters
                int charCount = random.nextInt(3) + 1;
                for (int j = 0; j < charCount && sb.length() < maxLength; j++) {
                    sb.append((char) ('a' + random.nextInt(5)));
                }
            }
        }
    }
}
