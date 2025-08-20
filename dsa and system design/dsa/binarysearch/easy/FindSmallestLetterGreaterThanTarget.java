package binarysearch.easy;

/**
 * LeetCode 744: Find Smallest Letter Greater Than Target
 * https://leetcode.com/problems/find-smallest-letter-greater-than-target/
 *
 * Description:
 * You are given an array of characters letters that is sorted in non-decreasing
 * order, and a character target.
 * There are at least two different characters in letters.
 * Return the smallest character in letters that is lexicographically greater
 * than target.
 * If such a character does not exist, return the first character in letters.
 *
 * Companies: Google, Microsoft, Amazon, Apple, LinkedIn, Bloomberg
 * Difficulty: Easy
 * Asked: 2023-2024 (Medium Frequency)
 *
 * Constraints:
 * - 2 <= letters.length <= 10^4
 * - letters[i] is a lowercase English letter
 * - letters is sorted in non-decreasing order
 * - letters contains at least two different letters
 * - target is a lowercase English letter
 *
 * Follow-ups:
 * - What if the array contains uppercase letters too?
 * - Can you solve this with O(1) space complexity?
 * - How would you handle Unicode characters?
 */
public class FindSmallestLetterGreaterThanTarget {

    // Binary Search - O(log n) time, O(1) space
    public char nextGreatestLetter(char[] letters, char target) {
        int left = 0;
        int right = letters.length - 1;

        // If target is greater than or equal to the last letter, return first letter
        if (target >= letters[right]) {
            return letters[0];
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (letters[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return letters[left];
    }

    // Template pattern approach
    public char nextGreatestLetterTemplate(char[] letters, char target) {
        int left = 0;
        int right = letters.length;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (letters[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        // If left equals length, wrap around to first element
        return letters[left % letters.length];
    }

    // Alternative implementation with explicit bounds checking
    public char nextGreatestLetterExplicit(char[] letters, char target) {
        int left = 0;
        int right = letters.length - 1;
        char result = letters[0]; // Default to first letter

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (letters[mid] > target) {
                result = letters[mid];
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    // Linear search for comparison - O(n) time
    public char nextGreatestLetterLinear(char[] letters, char target) {
        for (char letter : letters) {
            if (letter > target) {
                return letter;
            }
        }
        return letters[0]; // Wrap around
    }

    // Using built-in binary search
    public char nextGreatestLetterBuiltin(char[] letters, char target) {
        int index = java.util.Arrays.binarySearch(letters, (char) (target + 1));

        if (index < 0) {
            index = -index - 1; // Convert to insertion point
        }

        return letters[index % letters.length];
    }

    // Handle duplicates explicitly
    public char nextGreatestLetterNoDuplicates(char[] letters, char target) {
        int left = 0;
        int right = letters.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (letters[mid] <= target) {
                left = mid + 1;
            } else {
                if (mid == 0 || letters[mid - 1] <= target) {
                    return letters[mid];
                }
                right = mid - 1;
            }
        }

        return letters[0];
    }

    // Find all letters greater than target
    public java.util.List<Character> findAllGreaterLetters(char[] letters, char target) {
        java.util.List<Character> result = new java.util.ArrayList<>();

        for (char letter : letters) {
            if (letter > target) {
                result.add(letter);
            }
        }

        return result;
    }

    // Find the count of letters greater than target
    public int countGreaterLetters(char[] letters, char target) {
        int left = 0;
        int right = letters.length - 1;
        int count = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (letters[mid] > target) {
                count = letters.length - mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return count;
    }

    // Handle mixed case letters (extension)
    public char nextGreatestLetterMixedCase(char[] letters, char target) {
        int left = 0;
        int right = letters.length - 1;

        // Convert to lowercase for comparison
        target = Character.toLowerCase(target);

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (Character.toLowerCase(letters[mid]) <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left < letters.length ? letters[left] : letters[0];
    }

    // Recursive approach
    public char nextGreatestLetterRecursive(char[] letters, char target) {
        return nextGreatestLetterRecursiveHelper(letters, target, 0, letters.length - 1);
    }

    private char nextGreatestLetterRecursiveHelper(char[] letters, char target, int left, int right) {
        if (left >= letters.length) {
            return letters[0];
        }

        if (left == right) {
            return letters[left] > target ? letters[left] : letters[0];
        }

        int mid = left + (right - left) / 2;

        if (letters[mid] <= target) {
            return nextGreatestLetterRecursiveHelper(letters, target, mid + 1, right);
        } else {
            char rightResult = nextGreatestLetterRecursiveHelper(letters, target, left, mid);
            return rightResult;
        }
    }

    // Find position of next greatest letter
    public int findNextGreatestPosition(char[] letters, char target) {
        int left = 0;
        int right = letters.length - 1;

        if (target >= letters[right]) {
            return 0; // Wrap around to first position
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (letters[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    public static void main(String[] args) {
        FindSmallestLetterGreaterThanTarget solution = new FindSmallestLetterGreaterThanTarget();

        // Test Case 1: ["c","f","j"], target = "a"
        char[] letters1 = { 'c', 'f', 'j' };
        System.out.println(solution.nextGreatestLetter(letters1, 'a')); // Expected: 'c'

        // Test Case 2: ["c","f","j"], target = "c"
        System.out.println(solution.nextGreatestLetter(letters1, 'c')); // Expected: 'f'

        // Test Case 3: ["c","f","j"], target = "d"
        System.out.println(solution.nextGreatestLetter(letters1, 'd')); // Expected: 'f'

        // Test Case 4: ["c","f","j"], target = "g"
        System.out.println(solution.nextGreatestLetter(letters1, 'g')); // Expected: 'j'

        // Test Case 5: ["c","f","j"], target = "j" (wrap around)
        System.out.println(solution.nextGreatestLetter(letters1, 'j')); // Expected: 'c'

        // Test Case 6: ["c","f","j"], target = "k" (wrap around)
        System.out.println(solution.nextGreatestLetter(letters1, 'k')); // Expected: 'c'

        // Test Case 7: Array with duplicates
        char[] letters2 = { 'a', 'a', 'b', 'b', 'c', 'c' };
        System.out.println(solution.nextGreatestLetter(letters2, 'a')); // Expected: 'b'

        // Test Case 8: Single character repeated
        char[] letters3 = { 'x', 'x', 'y', 'y' };
        System.out.println(solution.nextGreatestLetter(letters3, 'z')); // Expected: 'x'

        // Test template approach
        System.out.println("Template: " + solution.nextGreatestLetterTemplate(letters1, 'a')); // Expected: 'c'

        // Test explicit approach
        System.out.println("Explicit: " + solution.nextGreatestLetterExplicit(letters1, 'a')); // Expected: 'c'

        // Test linear approach
        System.out.println("Linear: " + solution.nextGreatestLetterLinear(letters1, 'a')); // Expected: 'c'

        // Test builtin approach
        System.out.println("Builtin: " + solution.nextGreatestLetterBuiltin(letters1, 'a')); // Expected: 'c'

        // Test recursive approach
        System.out.println("Recursive: " + solution.nextGreatestLetterRecursive(letters1, 'a')); // Expected: 'c'

        // Test find all greater letters
        java.util.List<Character> allGreater = solution.findAllGreaterLetters(letters1, 'd');
        System.out.println("All greater than 'd': " + allGreater); // Expected: [f, j]

        // Test count greater letters
        System.out.println("Count greater than 'd': " + solution.countGreaterLetters(letters1, 'd')); // Expected: 2

        // Test find position
        System.out.println("Position of next greatest: " + solution.findNextGreatestPosition(letters1, 'a')); // Expected:
                                                                                                              // 0

        // Edge cases
        char[] edge1 = { 'a', 'b' };
        System.out.println("Edge case 1: " + solution.nextGreatestLetter(edge1, 'z')); // Expected: 'a'

        char[] edge2 = { 'a', 'b' };
        System.out.println("Edge case 2: " + solution.nextGreatestLetter(edge2, 'a')); // Expected: 'b'

        // Large test case
        char[] large = new char[10000];
        for (int i = 0; i < 10000; i++) {
            large[i] = (char) ('a' + (i % 26));
        }

        long startTime = System.currentTimeMillis();
        char largeResult = solution.nextGreatestLetter(large, 'm');
        long endTime = System.currentTimeMillis();
        System.out.println("Large test: " + largeResult + " (time: " + (endTime - startTime) + "ms)"); // Expected: 'n'

        // Performance comparison
        startTime = System.currentTimeMillis();
        solution.nextGreatestLetterLinear(large, 'm');
        long linearTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        solution.nextGreatestLetter(large, 'm');
        long binaryTime = System.currentTimeMillis() - startTime;

        System.out.println("Linear time: " + linearTime + "ms, Binary search time: " + binaryTime + "ms");

        // Test with all same letters
        char[] same = { 'x', 'x', 'x', 'x' };
        System.out.println("All same letters: " + solution.nextGreatestLetter(same, 'w')); // Expected: 'x'
        System.out.println("All same letters (greater): " + solution.nextGreatestLetter(same, 'y')); // Expected: 'x'
    }
}
