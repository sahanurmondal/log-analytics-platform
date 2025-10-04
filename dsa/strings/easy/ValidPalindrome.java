package strings.easy;

/**
 * LeetCode 125: Valid Palindrome
 * https://leetcode.com/problems/valid-palindrome/
 *
 * Description: A phrase is a palindrome if, after converting all uppercase
 * letters into lowercase letters
 * and removing all non-alphanumeric characters, it reads the same forward and
 * backward.
 * 
 * Constraints:
 * - 1 <= s.length <= 2 * 10^5
 * - s consists only of printable ASCII characters
 *
 * Follow-up:
 * - What if we only consider alphabetic characters?
 * - Can you solve it without extra space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Use two pointers from start and end
 * 2. Skip non-alphanumeric characters
 * 3. Compare characters case-insensitively
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class ValidPalindrome {

    // Main optimized solution - Two pointers
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            // Skip non-alphanumeric characters from left
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }

            // Skip non-alphanumeric characters from right
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            // Compare characters case-insensitively
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    // Alternative solution - Using StringBuilder
    public boolean isPalindromeStringBuilder(String s) {
        StringBuilder cleaned = new StringBuilder();

        // Clean the string
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                cleaned.append(Character.toLowerCase(c));
            }
        }

        // Check palindrome
        String cleanedStr = cleaned.toString();
        String reversed = cleaned.reverse().toString();

        return cleanedStr.equals(reversed);
    }

    // Follow-up optimization - Only alphabetic characters
    public boolean isPalindromeAlphaOnly(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            while (left < right && !Character.isLetter(s.charAt(left))) {
                left++;
            }

            while (left < right && !Character.isLetter(s.charAt(right))) {
                right--;
            }

            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    public static void main(String[] args) {
        ValidPalindrome solution = new ValidPalindrome();

        // Test Case 1: Normal case
        System.out.println(solution.isPalindrome("A man, a plan, a canal: Panama")); // Expected: true

        // Test Case 2: Edge case - not palindrome
        System.out.println(solution.isPalindrome("race a car")); // Expected: false

        // Test Case 3: Corner case - empty string
        System.out.println(solution.isPalindrome(" ")); // Expected: true

        // Test Case 4: Single character
        System.out.println(solution.isPalindrome("a")); // Expected: true

        // Test Case 5: Numbers included
        System.out.println(solution.isPalindrome("0P")); // Expected: false

        // Test Case 6: Special case - all special characters
        System.out.println(solution.isPalindrome(".,;")); // Expected: true

        // Test Case 7: Mixed case palindrome
        System.out.println(solution.isPalindrome("Madam")); // Expected: true

        // Test Case 8: Numbers and letters
        System.out.println(solution.isPalindrome("12321")); // Expected: true

        // Test Case 9: Complex palindrome
        System.out.println(solution.isPalindrome("Was it a car or a cat I saw?")); // Expected: true

        // Test Case 10: Long string
        System.out.println(solution.isPalindrome("abcdefghijklmnopqrstuvwxyzzyxwvutsrqponmlkjihgfedcba")); // Expected:
                                                                                                           // true
    }
}
