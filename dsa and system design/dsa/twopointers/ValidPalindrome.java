package twopointers;

/**
 * LeetCode 125: Valid Palindrome
 * Check if string is palindrome considering only alphanumeric characters
 * Two pointers technique
 */
public class ValidPalindrome {
    
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
            
            // Compare characters (case insensitive)
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            
            left++;
            right--;
        }
        
        return true;
    }
    
    // Alternative: Clean string first then check
    public boolean isPalindromeClean(String s) {
        StringBuilder cleaned = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                cleaned.append(Character.toLowerCase(c));
            }
        }
        
        String cleanStr = cleaned.toString();
        return cleanStr.equals(new StringBuilder(cleanStr).reverse().toString());
    }
    
    // Using regex
    public boolean isPalindromeRegex(String s) {
        String cleaned = s.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }
    
    public static void main(String[] args) {
        ValidPalindrome solution = new ValidPalindrome();
        
        // Test cases
        System.out.println(solution.isPalindrome("A man, a plan, a canal: Panama")); // true
        System.out.println(solution.isPalindrome("race a car")); // false
        System.out.println(solution.isPalindrome(" ")); // true
        
        // Edge cases
        System.out.println(solution.isPalindrome("")); // true
        System.out.println(solution.isPalindrome("a")); // true
        System.out.println(solution.isPalindrome("Aa")); // true
        System.out.println(solution.isPalindrome("ab")); // false
        
        // Numbers and special characters
        System.out.println(solution.isPalindrome("A1B2b1a")); // true
        System.out.println(solution.isPalindrome("0P")); // false
        System.out.println(solution.isPalindrome(".,;")); // true (no alphanumeric)
        
        // Compare approaches
        String test = "A man, a plan, a canal: Panama";
        System.out.println("Two pointers: " + solution.isPalindrome(test));
        System.out.println("Clean first: " + solution.isPalindromeClean(test));
        System.out.println("Regex: " + solution.isPalindromeRegex(test));
    }
}
