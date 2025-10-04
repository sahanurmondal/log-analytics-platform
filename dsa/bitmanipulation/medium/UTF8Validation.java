package bitmanipulation.medium;

/**
 * LeetCode 393: UTF-8 Validation
 * https://leetcode.com/problems/utf-8-validation/
 *
 * Description: Given an integer array data representing the data, return
 * whether it is a valid UTF-8 encoding.
 * 
 * Constraints:
 * - 1 <= data.length <= 2 * 10^4
 * - 0 <= data[i] <= 255
 *
 * Follow-up:
 * - Can you solve it using bit manipulation?
 * - What about the UTF-8 encoding rules?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook
 */
public class UTF8Validation {

    // Main optimized solution - Bit manipulation
    public boolean validUtf8(int[] data) {
        int count = 0; // Number of bytes to follow

        for (int c : data) {
            if (count == 0) {
                // Determine how many bytes this character has
                if ((c >> 5) == 0b110)
                    count = 1;
                else if ((c >> 4) == 0b1110)
                    count = 2;
                else if ((c >> 3) == 0b11110)
                    count = 3;
                else if ((c >> 7) != 0)
                    return false; // Invalid start byte
            } else {
                // Check if it's a valid continuation byte (10xxxxxx)
                if ((c >> 6) != 0b10)
                    return false;
                count--;
            }
        }

        return count == 0;
    }

    // Alternative solution - Using masks
    public boolean validUtf8Masks(int[] data) {
        int count = 0;
        int mask1 = 1 << 7;
        int mask2 = 1 << 6;

        for (int c : data) {
            if (count == 0) {
                int mask = 1 << 7;
                while ((mask & c) != 0) {
                    count++;
                    mask >>= 1;
                }

                if (count == 0)
                    continue;
                if (count == 1 || count > 4)
                    return false;
                count--; // Subtract 1 for the current byte
            } else {
                if ((c & mask1) == 0 || (c & mask2) != 0)
                    return false;
                count--;
            }
        }

        return count == 0;
    }

    public static void main(String[] args) {
        UTF8Validation solution = new UTF8Validation();

        System.out.println(solution.validUtf8(new int[] { 197, 130, 1 })); // Expected: true
        System.out.println(solution.validUtf8(new int[] { 235, 140, 4 })); // Expected: false
        System.out.println(solution.validUtf8(new int[] { 145 })); // Expected: false
        System.out.println(solution.validUtf8(new int[] { 230, 136, 145 })); // Expected: true
    }
}
