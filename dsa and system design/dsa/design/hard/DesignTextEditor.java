package design.hard;

/**
 * LeetCode 2296: Design a Text Editor
 * https://leetcode.com/problems/design-a-text-editor/
 *
 * Description: Design a text editor with a cursor that can do the following:
 * - Add text to where the cursor is.
 * - Delete characters to the left of the cursor.
 * - Move the cursor either left or right.
 * - Get characters to the left and right of the cursor.
 * 
 * Constraints:
 * - 1 <= text.length, k <= 40
 * - text consists of lowercase English letters
 * - At most 2 * 10^4 calls in total will be made
 *
 * Follow-up:
 * - Can you make all operations O(1) amortized?
 * 
 * Time Complexity: O(1) amortized for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class DesignTextEditor {

    private StringBuilder left;
    private StringBuilder right;

    public DesignTextEditor() {
        left = new StringBuilder();
        right = new StringBuilder();
    }

    public void addText(String text) {
        left.append(text);
    }

    public int deleteText(int k) {
        int deleted = Math.min(k, left.length());
        left.setLength(left.length() - deleted);
        return deleted;
    }

    public String cursorLeft(int k) {
        int moves = Math.min(k, left.length());
        for (int i = 0; i < moves; i++) {
            char c = left.charAt(left.length() - 1);
            left.deleteCharAt(left.length() - 1);
            right.insert(0, c);
        }

        int start = Math.max(0, left.length() - 10);
        return left.substring(start);
    }

    public String cursorRight(int k) {
        int moves = Math.min(k, right.length());
        for (int i = 0; i < moves; i++) {
            char c = right.charAt(0);
            right.deleteCharAt(0);
            left.append(c);
        }

        int start = Math.max(0, left.length() - 10);
        return left.substring(start);
    }

    public static void main(String[] args) {
        DesignTextEditor textEditor = new DesignTextEditor();
        textEditor.addText("leetcode");
        System.out.println(textEditor.deleteText(4)); // Expected: 4
        textEditor.addText("practice");
        System.out.println(textEditor.cursorRight(3)); // Expected: "etpractice"
        System.out.println(textEditor.cursorLeft(8)); // Expected: "leet"
        System.out.println(textEditor.deleteText(10)); // Expected: 4
        System.out.println(textEditor.cursorLeft(2)); // Expected: ""
        System.out.println(textEditor.cursorRight(6)); // Expected: "practi"
    }
}
