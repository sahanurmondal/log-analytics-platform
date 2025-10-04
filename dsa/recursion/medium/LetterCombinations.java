package recursion.medium;

import java.util.*;

/**
 * LeetCode 17: Letter Combinations of a Phone Number
 * https://leetcode.com/problems/letter-combinations-of-a-phone-number/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple, Uber, Bloomberg
 * Frequency: Very High (Asked in 800+ interviews)
 *
 * Description:
 * Given a string containing digits from 2-9 inclusive, return all possible
 * letter combinations
 * that the number could represent. Return the answer in any order.
 *
 * A mapping of digits to letters (just like on the telephone buttons) is given
 * below.
 * Note that 1 does not map to any letters.
 * 
 * 2: abc, 3: def, 4: ghi, 5: jkl, 6: mno, 7: pqrs, 8: tuv, 9: wxyz
 *
 * Constraints:
 * - 0 <= digits.length <= 4
 * - digits[i] is a digit in the range ['2', '9'].
 * 
 * Follow-up Questions:
 * 1. How would you handle custom digit-to-letter mappings?
 * 2. Can you implement iterative solutions?
 * 3. How to handle very large input efficiently?
 * 4. What about generating combinations in lexicographical order?
 * 5. How to implement with different traversal orders?
 */
public class LetterCombinations {

    // Approach 1: Recursive Backtracking - O(4^n) time, O(n) space
    public static List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();

        if (digits == null || digits.length() == 0) {
            return result;
        }

        String[] mapping = {
                "", // 0
                "", // 1
                "abc", // 2
                "def", // 3
                "ghi", // 4
                "jkl", // 5
                "mno", // 6
                "pqrs", // 7
                "tuv", // 8
                "wxyz" // 9
        };

        backtrack(result, mapping, digits, 0, new StringBuilder());
        return result;
    }

    private static void backtrack(List<String> result, String[] mapping, String digits,
            int index, StringBuilder current) {
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }

        String letters = mapping[digits.charAt(index) - '0'];
        for (char letter : letters.toCharArray()) {
            current.append(letter);
            backtrack(result, mapping, digits, index + 1, current);
            current.deleteCharAt(current.length() - 1);
        }
    }

    // Approach 2: Iterative using Queue - O(4^n) time, O(4^n) space
    public static List<String> letterCombinationsIterative(String digits) {
        if (digits == null || digits.length() == 0) {
            return new ArrayList<>();
        }

        String[] mapping = {
                "", // 0
                "", // 1
                "abc", // 2
                "def", // 3
                "ghi", // 4
                "jkl", // 5
                "mno", // 6
                "pqrs", // 7
                "tuv", // 8
                "wxyz" // 9
        };

        Queue<String> queue = new LinkedList<>();
        queue.offer("");

        for (int i = 0; i < digits.length(); i++) {
            String letters = mapping[digits.charAt(i) - '0'];
            int size = queue.size();

            for (int j = 0; j < size; j++) {
                String current = queue.poll();
                for (char letter : letters.toCharArray()) {
                    queue.offer(current + letter);
                }
            }
        }

        return new ArrayList<>(queue);
    }

    // Approach 3: Using StringBuilder for efficiency
    public static List<String> letterCombinationsOptimized(String digits) {
        List<String> result = new ArrayList<>();

        if (digits == null || digits.length() == 0) {
            return result;
        }

        String[] mapping = {
                "", // 0
                "", // 1
                "abc", // 2
                "def", // 3
                "ghi", // 4
                "jkl", // 5
                "mno", // 6
                "pqrs", // 7
                "tuv", // 8
                "wxyz" // 9
        };

        generateCombinations(digits, 0, new StringBuilder(), mapping, result);
        return result;
    }

    private static void generateCombinations(String digits, int index, StringBuilder current,
            String[] mapping, List<String> result) {
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }

        String letters = mapping[digits.charAt(index) - '0'];
        int currentLength = current.length();

        for (char letter : letters.toCharArray()) {
            current.append(letter);
            generateCombinations(digits, index + 1, current, mapping, result);
            current.setLength(currentLength); // More efficient than deleteCharAt
        }
    }

    // Follow-up 1: Custom digit-to-letter mappings
    public static class CustomLetterCombinations {
        private Map<Character, String> customMapping;

        public CustomLetterCombinations(Map<Character, String> mapping) {
            this.customMapping = new HashMap<>(mapping);
        }

        public List<String> letterCombinations(String digits) {
            List<String> result = new ArrayList<>();

            if (digits == null || digits.length() == 0) {
                return result;
            }

            backtrack(digits, 0, new StringBuilder(), result);
            return result;
        }

        private void backtrack(String digits, int index, StringBuilder current, List<String> result) {
            if (index == digits.length()) {
                result.add(current.toString());
                return;
            }

            char digit = digits.charAt(index);
            String letters = customMapping.getOrDefault(digit, "");

            if (letters.isEmpty()) {
                // Skip this digit if no mapping
                backtrack(digits, index + 1, current, result);
                return;
            }

            for (char letter : letters.toCharArray()) {
                current.append(letter);
                backtrack(digits, index + 1, current, result);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    // Follow-up 2: Multiple iterative approaches
    public static class IterativeSolutions {

        // Using nested loops (limited to known depth)
        public static List<String> letterCombinationsNestedLoops(String digits) {
            List<String> result = new ArrayList<>();

            if (digits == null || digits.length() == 0) {
                return result;
            }

            String[] mapping = {
                    "", // 0
                    "", // 1
                    "abc", // 2
                    "def", // 3
                    "ghi", // 4
                    "jkl", // 5
                    "mno", // 6
                    "pqrs", // 7
                    "tuv", // 8
                    "wxyz" // 9
            };

            // This approach only works for known small lengths
            if (digits.length() == 1) {
                String letters = mapping[digits.charAt(0) - '0'];
                for (char c : letters.toCharArray()) {
                    result.add(String.valueOf(c));
                }
            } else if (digits.length() == 2) {
                String letters1 = mapping[digits.charAt(0) - '0'];
                String letters2 = mapping[digits.charAt(1) - '0'];
                for (char c1 : letters1.toCharArray()) {
                    for (char c2 : letters2.toCharArray()) {
                        result.add("" + c1 + c2);
                    }
                }
            }
            // ... extend for more lengths as needed

            return result;
        }

        // Using stack-based approach
        public static List<String> letterCombinationsStack(String digits) {
            List<String> result = new ArrayList<>();

            if (digits == null || digits.length() == 0) {
                return result;
            }

            String[] mapping = {
                    "", // 0
                    "", // 1
                    "abc", // 2
                    "def", // 3
                    "ghi", // 4
                    "jkl", // 5
                    "mno", // 6
                    "pqrs", // 7
                    "tuv", // 8
                    "wxyz" // 9
            };

            Stack<StackFrame> stack = new Stack<>();
            stack.push(new StackFrame("", 0));

            while (!stack.isEmpty()) {
                StackFrame frame = stack.pop();

                if (frame.index == digits.length()) {
                    result.add(frame.current);
                    continue;
                }

                String letters = mapping[digits.charAt(frame.index) - '0'];
                for (char letter : letters.toCharArray()) {
                    stack.push(new StackFrame(frame.current + letter, frame.index + 1));
                }
            }

            return result;
        }

        private static class StackFrame {
            String current;
            int index;

            StackFrame(String current, int index) {
                this.current = current;
                this.index = index;
            }
        }
    }

    // Follow-up 3: Memory-efficient for large inputs
    public static class MemoryEfficientSolutions {

        // Generator-like approach using iterator
        public static Iterator<String> letterCombinationsIterator(String digits) {
            if (digits == null || digits.length() == 0) {
                return Collections.emptyIterator();
            }

            return new LetterCombinationIterator(digits);
        }

        private static class LetterCombinationIterator implements Iterator<String> {
            private String[] mapping = {
                    "", // 0
                    "", // 1
                    "abc", // 2
                    "def", // 3
                    "ghi", // 4
                    "jkl", // 5
                    "mno", // 6
                    "pqrs", // 7
                    "tuv", // 8
                    "wxyz" // 9
            };

            private String digits;
            private int[] indices;
            private boolean hasNext;

            public LetterCombinationIterator(String digits) {
                this.digits = digits;
                this.indices = new int[digits.length()];
                this.hasNext = true;

                // Check if any digit has no mapping
                for (char digit : digits.toCharArray()) {
                    if (mapping[digit - '0'].isEmpty()) {
                        hasNext = false;
                        break;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public String next() {
                if (!hasNext) {
                    throw new NoSuchElementException();
                }

                // Generate current combination
                StringBuilder current = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    String letters = mapping[digits.charAt(i) - '0'];
                    current.append(letters.charAt(indices[i]));
                }

                // Generate next indices
                int carry = 1;
                for (int i = digits.length() - 1; i >= 0 && carry > 0; i--) {
                    String letters = mapping[digits.charAt(i) - '0'];
                    indices[i] += carry;

                    if (indices[i] >= letters.length()) {
                        indices[i] = 0;
                        carry = 1;
                    } else {
                        carry = 0;
                    }
                }

                if (carry > 0) {
                    hasNext = false;
                }

                return current.toString();
            }
        }
    }

    // Follow-up 4: Lexicographical order generation
    public static List<String> letterCombinationsLexicographical(String digits) {
        List<String> result = new ArrayList<>();

        if (digits == null || digits.length() == 0) {
            return result;
        }

        String[] mapping = {
                "", // 0
                "", // 1
                "abc", // 2
                "def", // 3
                "ghi", // 4
                "jkl", // 5
                "mno", // 6
                "pqrs", // 7
                "tuv", // 8
                "wxyz" // 9
        };

        generateLexicographical(digits, 0, new StringBuilder(), mapping, result);
        return result;
    }

    private static void generateLexicographical(String digits, int index, StringBuilder current,
            String[] mapping, List<String> result) {
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }

        String letters = mapping[digits.charAt(index) - '0'];
        // Letters are already in alphabetical order in our mapping
        for (char letter : letters.toCharArray()) {
            current.append(letter);
            generateLexicographical(digits, index + 1, current, mapping, result);
            current.deleteCharAt(current.length() - 1);
        }
    }

    // Follow-up 5: Different traversal orders
    public static class TraversalOrders {

        // Breadth-first traversal
        public static List<String> letterCombinationsBFS(String digits) {
            if (digits == null || digits.length() == 0) {
                return new ArrayList<>();
            }

            String[] mapping = {
                    "", // 0
                    "", // 1
                    "abc", // 2
                    "def", // 3
                    "ghi", // 4
                    "jkl", // 5
                    "mno", // 6
                    "pqrs", // 7
                    "tuv", // 8
                    "wxyz" // 9
            };

            Queue<String> queue = new LinkedList<>();
            queue.offer("");

            for (char digit : digits.toCharArray()) {
                String letters = mapping[digit - '0'];
                int size = queue.size();

                for (int i = 0; i < size; i++) {
                    String current = queue.poll();
                    for (char letter : letters.toCharArray()) {
                        queue.offer(current + letter);
                    }
                }
            }

            return new ArrayList<>(queue);
        }

        // Depth-first with different orders
        public static List<String> letterCombinationsDFSReverse(String digits) {
            List<String> result = new ArrayList<>();

            if (digits == null || digits.length() == 0) {
                return result;
            }

            String[] mapping = {
                    "", // 0
                    "", // 1
                    "abc", // 2
                    "def", // 3
                    "ghi", // 4
                    "jkl", // 5
                    "mno", // 6
                    "pqrs", // 7
                    "tuv", // 8
                    "wxyz" // 9
            };

            backtrackReverse(result, mapping, digits, 0, new StringBuilder());
            return result;
        }

        private static void backtrackReverse(List<String> result, String[] mapping, String digits,
                int index, StringBuilder current) {
            if (index == digits.length()) {
                result.add(current.toString());
                return;
            }

            String letters = mapping[digits.charAt(index) - '0'];
            // Traverse in reverse order
            for (int i = letters.length() - 1; i >= 0; i--) {
                current.append(letters.charAt(i));
                backtrackReverse(result, mapping, digits, index + 1, current);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    // Advanced: Parallel processing for large inputs
    public static class ParallelLetterCombinations {

        public static List<String> letterCombinationsParallel(String digits) {
            if (digits == null || digits.length() == 0) {
                return new ArrayList<>();
            }

            if (digits.length() <= 2) {
                // Use regular approach for small inputs
                return letterCombinations(digits);
            }

            // Split the problem
            int mid = digits.length() / 2;
            String left = digits.substring(0, mid);
            String right = digits.substring(mid);

            // This is a simplified version - in practice, you'd use actual parallel
            // processing
            List<String> leftCombinations = letterCombinations(left);
            List<String> rightCombinations = letterCombinations(right);

            // Combine results
            List<String> result = new ArrayList<>();
            for (String leftComb : leftCombinations) {
                for (String rightComb : rightCombinations) {
                    result.add(leftComb + rightComb);
                }
            }

            return result;
        }
    }

    // Advanced: With caching for repeated calls
    public static class CachedLetterCombinations {
        private static Map<String, List<String>> cache = new HashMap<>();

        public static List<String> letterCombinationsWithCache(String digits) {
            if (cache.containsKey(digits)) {
                return new ArrayList<>(cache.get(digits));
            }

            List<String> result = letterCombinations(digits);
            cache.put(digits, new ArrayList<>(result));
            return result;
        }

        public static void clearCache() {
            cache.clear();
        }

        public static int getCacheSize() {
            return cache.size();
        }
    }

    // Performance testing
    public static void performanceTest() {
        System.out.println("=== Performance Testing ===");

        String testDigits = "2345";

        // Test recursive approach
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            letterCombinations(testDigits);
        }
        long recursiveTime = System.nanoTime() - start;

        // Test iterative approach
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            letterCombinationsIterative(testDigits);
        }
        long iterativeTime = System.nanoTime() - start;

        // Test optimized approach
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            letterCombinationsOptimized(testDigits);
        }
        long optimizedTime = System.nanoTime() - start;

        System.out.println("Recursive: " + recursiveTime / 1_000_000 + " ms");
        System.out.println("Iterative: " + iterativeTime / 1_000_000 + " ms");
        System.out.println("Optimized: " + optimizedTime / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        System.out.println("Input: \"23\"");
        System.out.println("Output: " + letterCombinations("23"));

        System.out.println("\nInput: \"\"");
        System.out.println("Output: " + letterCombinations(""));

        System.out.println("\nInput: \"2\"");
        System.out.println("Output: " + letterCombinations("2"));

        // Test Case 2: Compare different approaches
        System.out.println("\n=== Test Case 2: Compare Approaches ===");

        String testInput = "234";

        System.out.println("Recursive: " + letterCombinations(testInput));
        System.out.println("Iterative: " + letterCombinationsIterative(testInput));
        System.out.println("Optimized: " + letterCombinationsOptimized(testInput));

        // Verify all approaches give same results
        List<String> recursive = letterCombinations(testInput);
        List<String> iterative = letterCombinationsIterative(testInput);
        List<String> optimized = letterCombinationsOptimized(testInput);

        Collections.sort(recursive);
        Collections.sort(iterative);
        Collections.sort(optimized);

        System.out.println("All approaches give same result: " +
                (recursive.equals(iterative) && iterative.equals(optimized)));

        // Test Case 3: Custom mappings
        System.out.println("\n=== Test Case 3: Custom Mappings ===");

        Map<Character, String> customMapping = new HashMap<>();
        customMapping.put('2', "xyz");
        customMapping.put('3', "123");
        customMapping.put('4', "!");

        CustomLetterCombinations customSolution = new CustomLetterCombinations(customMapping);
        System.out.println("Custom mapping for \"234\": " + customSolution.letterCombinations("234"));

        // Test Case 4: Stack-based iterative
        System.out.println("\n=== Test Case 4: Stack-based Iterative ===");

        System.out.println("Stack approach for \"23\": " +
                IterativeSolutions.letterCombinationsStack("23"));

        // Test Case 5: Memory-efficient iterator
        System.out.println("\n=== Test Case 5: Memory-efficient Iterator ===");

        Iterator<String> iterator = MemoryEfficientSolutions.letterCombinationsIterator("23");
        System.out.print("Iterator results for \"23\": ");
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();

        // Test Case 6: Lexicographical order
        System.out.println("\n=== Test Case 6: Lexicographical Order ===");

        List<String> lexOrder = letterCombinationsLexicographical("23");
        System.out.println("Lexicographical order for \"23\": " + lexOrder);

        // Verify it's actually in lexicographical order
        boolean isLexOrder = true;
        for (int i = 1; i < lexOrder.size(); i++) {
            if (lexOrder.get(i - 1).compareTo(lexOrder.get(i)) > 0) {
                isLexOrder = false;
                break;
            }
        }
        System.out.println("Is in lexicographical order: " + isLexOrder);

        // Test Case 7: Different traversal orders
        System.out.println("\n=== Test Case 7: Different Traversal Orders ===");

        System.out.println("BFS order for \"23\": " + TraversalOrders.letterCombinationsBFS("23"));
        System.out.println("DFS reverse for \"23\": " + TraversalOrders.letterCombinationsDFSReverse("23"));

        // Test Case 8: Parallel processing
        System.out.println("\n=== Test Case 8: Parallel Processing ===");

        System.out.println("Parallel for \"2345\": " + ParallelLetterCombinations.letterCombinationsParallel("2345"));

        // Test Case 9: Caching
        System.out.println("\n=== Test Case 9: Caching ===");

        System.out.println("First call (no cache): " + CachedLetterCombinations.letterCombinationsWithCache("23"));
        System.out.println("Second call (from cache): " + CachedLetterCombinations.letterCombinationsWithCache("23"));
        System.out.println("Cache size: " + CachedLetterCombinations.getCacheSize());

        CachedLetterCombinations.clearCache();
        System.out.println("Cache size after clear: " + CachedLetterCombinations.getCacheSize());

        // Test Case 10: Edge cases and validation
        System.out.println("\n=== Test Case 10: Edge Cases ===");

        // Long input
        System.out.println("Input \"2345\" (should have 3*3*3*3 = 81 combinations):");
        List<String> longResult = letterCombinations("2345");
        System.out.println("Number of combinations: " + longResult.size());

        // Input with 7 and 9 (have 4 letters each)
        System.out.println("\nInput \"79\" (should have 4*4 = 16 combinations):");
        List<String> sevenNine = letterCombinations("79");
        System.out.println("Combinations: " + sevenNine);
        System.out.println("Count: " + sevenNine.size());

        // Maximum length input
        System.out.println("\nInput \"2222\" (should have 3^4 = 81 combinations):");
        List<String> maxLength = letterCombinations("2222");
        System.out.println("Number of combinations: " + maxLength.size());

        // Verify no duplicates
        Set<String> uniqueResults = new HashSet<>(maxLength);
        System.out.println("All combinations unique: " + (uniqueResults.size() == maxLength.size()));

        // Test Case 11: Error handling
        System.out.println("\n=== Test Case 11: Error Handling ===");

        try {
            Iterator<String> emptyIterator = MemoryEfficientSolutions.letterCombinationsIterator("");
            if (emptyIterator.hasNext()) {
                emptyIterator.next();
            } else {
                System.out.println("Empty iterator correctly has no elements");
            }
        } catch (Exception e) {
            System.out.println("Exception with empty iterator: " + e.getMessage());
        }

        // Performance testing
        performanceTest();

        System.out.println("\nLetter Combinations testing completed successfully!");
    }
}
