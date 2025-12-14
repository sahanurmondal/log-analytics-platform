package strings.medium;

/**
 * LeetCode 6: ZigZag Conversion
 *
 * The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows
 * like this:
 *
 * P   A   H   N
 * A P L S I I G
 * Y   I   R
 *
 * Read line by line: "PAHNAPLSIIGYR"
 *
 * Write the code that will take a string and make this conversion given a number of rows:
 * string convert(string s, int numRows);
 *
 * Example 1:
 * Input: s = "PAYPALISHIRING", numRows = 3
 * Output: "PAHNAPLSIIGYR"
 *
 * Example 2:
 * Input: s = "PAYPALISHIRING", numRows = 4
 * Output: "PINALSIGYAHRPI"
 */
public class ZigZagConversion {

    /**
     * Solution: Simulate Zigzag Pattern
     * Time: O(n), Space: O(n) for storing rows
     *
     * Key insight: Characters move down, then diagonally up
     * Row transitions:
     * - Rows 0 to numRows-1: moving down (row++)
     * - Rows numRows-1 to 0: moving up (row--)
     * - Pattern repeats
     */
    public String convert(String s, int numRows) {
        if (numRows == 1) return s;
        if (numRows == 2) return convertSpecial(s);

        // Create arrays to store characters for each row
        StringBuilder[] rows = new StringBuilder[numRows];
        for (int i = 0; i < numRows; i++) {
            rows[i] = new StringBuilder();
        }

        int currentRow = 0;
        int direction = 1; // 1 for down, -1 for up

        for (char c : s.toCharArray()) {
            rows[currentRow].append(c);

            // Change direction at top and bottom rows
            if (currentRow == 0) {
                direction = 1;
            } else if (currentRow == numRows - 1) {
                direction = -1;
            }

            currentRow += direction;
        }

        // Concatenate all rows
        StringBuilder result = new StringBuilder();
        for (StringBuilder row : rows) {
            result.append(row);
        }

        return result.toString();
    }

    /**
     * Special case optimization for numRows = 2
     */
    private String convertSpecial(String s) {
        StringBuilder result = new StringBuilder();

        // Even indices (row 0)
        for (int i = 0; i < s.length(); i += 2) {
            result.append(s.charAt(i));
        }

        // Odd indices (row 1)
        for (int i = 1; i < s.length(); i += 2) {
            result.append(s.charAt(i));
        }

        return result.toString();
    }

    /**
     * Mathematical Solution
     * Time: O(n), Space: O(1) excluding output
     *
     * Calculate cycle length and directly access characters
     * Cycle = 2 * (numRows - 1)
     *
     * For each row, we can calculate which characters belong to it
     */
    public String convertMath(String s, int numRows) {
        if (numRows == 1) return s;

        int cycle = 2 * (numRows - 1);
        StringBuilder result = new StringBuilder();

        for (int row = 0; row < numRows; row++) {
            for (int i = row; i < s.length(); i += cycle) {
                result.append(s.charAt(i));

                // Middle rows have additional character in between
                if (row > 0 && row < numRows - 1) {
                    int nextPos = i + cycle - 2 * row;
                    if (nextPos < s.length()) {
                        result.append(s.charAt(nextPos));
                    }
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        ZigZagConversion solution = new ZigZagConversion();

        // Test case 1
        System.out.println(solution.convert("PAYPALISHIRING", 3));
        // Expected: "PAHNAPLSIIGYR"

        // Test case 2
        System.out.println(solution.convert("PAYPALISHIRING", 4));
        // Expected: "PINALSIGYAHRPI"

        // Test case 3
        System.out.println(solution.convert("A", 1)); // "A"

        // Test case 4
        System.out.println(solution.convert("AB", 1)); // "AB"

        // Test case 5
        System.out.println(solution.convert("ABC", 2)); // "ACB"
    }
}

