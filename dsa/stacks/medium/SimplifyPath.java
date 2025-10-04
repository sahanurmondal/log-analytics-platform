package stacks.medium;

import java.util.*;

/**
 * LeetCode 71: Simplify Path
 * https://leetcode.com/problems/simplify-path/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a string path, simplify it to its canonical path.
 *
 * Constraints:
 * - 1 <= path.length <= 3000
 * - path consists of English letters, '/', '.', and '..'
 * 
 * Follow-up Questions:
 * 1. Can you handle Windows-style paths?
 * 2. Can you optimize for large paths?
 * 3. Can you handle invalid paths?
 */
public class SimplifyPath {

    // Approach 1: Stack-based path simplification
    public String simplifyPath(String path) {
        String[] parts = path.split("/");
        Stack<String> stack = new Stack<>();
        for (String part : parts) {
            if (part.equals("") || part.equals("."))
                continue;
            if (part.equals("..")) {
                if (!stack.isEmpty())
                    stack.pop();
            } else {
                stack.push(part);
            }
        }
        return "/" + String.join("/", stack);
    }

    // Follow-up 1: Windows-style path (not implemented for brevity)
    // Comprehensive test cases
    public static void main(String[] args) {
        SimplifyPath solution = new SimplifyPath();

        // Test case 1: Basic case
        String path1 = "/home/";
        System.out.println("Test 1 - path: " + path1 + " Expected: /home");
        System.out.println("Result: " + solution.simplifyPath(path1));

        // Test case 2: Parent directory
        String path2 = "/a/./b/../../c/";
        System.out.println("\nTest 2 - Parent directory:");
        System.out.println("Result: " + solution.simplifyPath(path2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Root: " + solution.simplifyPath("/"));
        System.out.println("Multiple slashes: " + solution.simplifyPath("/a//b////c/d//././/.."));
    }
}
