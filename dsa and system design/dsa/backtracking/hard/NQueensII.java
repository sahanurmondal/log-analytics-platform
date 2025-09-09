package backtracking.hard;

import java.util.*;

/**
 * LeetCode 52: N-Queens II
 * URL: https://leetcode.com/problems/n-queens-ii/
 * Difficulty: Hard
 * Companies: Google, Facebook, Amazon, Microsoft, Apple
 * Frequency: High
 *
 * Description:
 * The n-queens puzzle is the problem of placing n chess queens on an n×n
 * chessboard
 * such that no two queens attack each other. Given an integer n, return the
 * number
 * of distinct solutions to the n-queens puzzle.
 *
 * Constraints:
 * - 1 <= n <= 9
 *
 * Follow-up Questions:
 * 1. Can you optimize using bit manipulation?
 * 2. What's the pattern for different n values?
 * 3. Can you solve without recursion?
 * 4. How to optimize for very large n?
 */
public class NQueensII {

    // Approach 1: Standard backtracking with sets - O(n!)
    public int totalNQueens(int n) {
        return backtrack(0, n, new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    private int backtrack(int row, int n, Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2) {
        if (row == n)
            return 1;

        int count = 0;
        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || diag1.contains(row - col) || diag2.contains(row + col)) {
                continue;
            }

            cols.add(col);
            diag1.add(row - col);
            diag2.add(row + col);

            count += backtrack(row + 1, n, cols, diag1, diag2);

            cols.remove(col);
            diag1.remove(row - col);
            diag2.remove(row + col);
        }

        return count;
    }

    // Approach 2: Optimized bit manipulation - O(n!) with better constants
    public int totalNQueensBit(int n) {
        return backtrackBit(0, 0, 0, 0, n);
    }

    private int backtrackBit(int row, int cols, int diag1, int diag2, int n) {
        if (row == n)
            return 1;

        int count = 0;
        int available = ((1 << n) - 1) & (~(cols | diag1 | diag2));

        while (available != 0) {
            int pos = available & (-available); // Get rightmost set bit
            available ^= pos; // Remove this bit

            count += backtrackBit(row + 1, cols | pos, (diag1 | pos) << 1, (diag2 | pos) >> 1, n);
        }

        return count;
    }

    // Approach 3: Memoized version (though limited benefit for this problem)
    public int totalNQueensMemo(int n) {
        Map<String, Integer> memo = new HashMap<>();
        return backtrackMemo(0, n, new HashSet<>(), new HashSet<>(), new HashSet<>(), memo);
    }

    private int backtrackMemo(int row, int n, Set<Integer> cols, Set<Integer> diag1,
            Set<Integer> diag2, Map<String, Integer> memo) {
        if (row == n)
            return 1;

        String key = row + ":" + cols.toString() + ":" + diag1.toString() + ":" + diag2.toString();
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int count = 0;
        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || diag1.contains(row - col) || diag2.contains(row + col)) {
                continue;
            }

            cols.add(col);
            diag1.add(row - col);
            diag2.add(row + col);

            count += backtrackMemo(row + 1, n, cols, diag1, diag2, memo);

            cols.remove(col);
            diag1.remove(row - col);
            diag2.remove(row + col);
        }

        memo.put(key, count);
        return count;
    }

    // Approach 4: Array-based for better performance
    public int totalNQueensArray(int n) {
        boolean[] cols = new boolean[n];
        boolean[] diag1 = new boolean[2 * n - 1];
        boolean[] diag2 = new boolean[2 * n - 1];

        return backtrackArray(0, n, cols, diag1, diag2);
    }

    private int backtrackArray(int row, int n, boolean[] cols, boolean[] diag1, boolean[] diag2) {
        if (row == n)
            return 1;

        int count = 0;
        for (int col = 0; col < n; col++) {
            int d1 = row + col;
            int d2 = row - col + n - 1;

            if (cols[col] || diag1[d1] || diag2[d2]) {
                continue;
            }

            cols[col] = diag1[d1] = diag2[d2] = true;
            count += backtrackArray(row + 1, n, cols, diag1, diag2);
            cols[col] = diag1[d1] = diag2[d2] = false;
        }

        return count;
    }

    // Follow-up 2: Get pattern analysis for different n values
    public Map<Integer, Integer> getNQueensPattern(int maxN) {
        Map<Integer, Integer> pattern = new HashMap<>();

        for (int n = 1; n <= maxN; n++) {
            pattern.put(n, totalNQueens(n));
        }

        return pattern;
    }

    // Follow-up 3: Iterative approach using stack
    public int totalNQueensIterative(int n) {
        if (n == 0)
            return 1;

        Stack<State> stack = new Stack<>();
        stack.push(new State(0, new HashSet<>(), new HashSet<>(), new HashSet<>()));

        int count = 0;

        while (!stack.isEmpty()) {
            State state = stack.pop();

            if (state.row == n) {
                count++;
                continue;
            }

            for (int col = 0; col < n; col++) {
                if (!state.cols.contains(col) &&
                        !state.diag1.contains(state.row - col) &&
                        !state.diag2.contains(state.row + col)) {

                    Set<Integer> newCols = new HashSet<>(state.cols);
                    Set<Integer> newDiag1 = new HashSet<>(state.diag1);
                    Set<Integer> newDiag2 = new HashSet<>(state.diag2);

                    newCols.add(col);
                    newDiag1.add(state.row - col);
                    newDiag2.add(state.row + col);

                    stack.push(new State(state.row + 1, newCols, newDiag1, newDiag2));
                }
            }
        }

        return count;
    }

    // Helper class for iterative approach
    private static class State {
        int row;
        Set<Integer> cols, diag1, diag2;

        State(int row, Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2) {
            this.row = row;
            this.cols = cols;
            this.diag1 = diag1;
            this.diag2 = diag2;
        }
    }

    // Performance comparison method
    public void compareApproaches(int n) {
        System.out.println("Comparing approaches for N=" + n);

        long start, end;

        start = System.currentTimeMillis();
        int result1 = totalNQueens(n);
        end = System.currentTimeMillis();
        System.out.println("Standard: " + result1 + " in " + (end - start) + "ms");

        start = System.currentTimeMillis();
        int result2 = totalNQueensBit(n);
        end = System.currentTimeMillis();
        System.out.println("Bitwise: " + result2 + " in " + (end - start) + "ms");

        start = System.currentTimeMillis();
        int result3 = totalNQueensArray(n);
        end = System.currentTimeMillis();
        System.out.println("Array: " + result3 + " in " + (end - start) + "ms");
    }

    public static void main(String[] args) {
        NQueensII solution = new NQueensII();

        // Test Case 1: Basic cases
        System.out.println("Test 1: " + solution.totalNQueens(4));
        // Expected: 2

        // Test Case 2: Trivial case
        System.out.println("Test 2: " + solution.totalNQueens(1));
        // Expected: 1

        // Test Case 3: Classic 8-queens
        System.out.println("Test 3: " + solution.totalNQueens(8));
        // Expected: 92

        // Test Case 4: Bit manipulation approach
        System.out.println("Test 4 (Bit): " + solution.totalNQueensBit(8));
        // Expected: 92

        // Test Case 5: No solution cases
        System.out.println("Test 5: " + solution.totalNQueens(2));
        // Expected: 0

        // Test Case 6: Another no solution case
        System.out.println("Test 6: " + solution.totalNQueens(3));
        // Expected: 0

        // Test Case 7: Array-based approach
        System.out.println("Test 7 (Array): " + solution.totalNQueensArray(8));
        // Expected: 92

        // Test Case 8: Memoized approach
        System.out.println("Test 8 (Memo): " + solution.totalNQueensMemo(4));
        // Expected: 2

        // Test Case 9: Pattern analysis
        System.out.println("Test 9 (Pattern): " + solution.getNQueensPattern(8));
        // Expected: {1=1, 2=0, 3=0, 4=2, 5=10, 6=4, 7=40, 8=92}

        // Test Case 10: Iterative approach
        System.out.println("Test 10 (Iterative): " + solution.totalNQueensIterative(4));
        // Expected: 2

        // Test Case 11: Large N performance
        long start = System.currentTimeMillis();
        int result11 = solution.totalNQueens(9);
        long end = System.currentTimeMillis();
        System.out.println("Test 11 (N=9): " + result11 + " in " + (end - start) + "ms");
        // Expected: 352

        // Test Case 12: Consistency check
        boolean consistent = solution.totalNQueens(5) == solution.totalNQueensBit(5);
        System.out.println("Test 12 (Consistency): " + consistent);
        // Expected: true

        // Test Case 13: Performance comparison
        System.out.println("Test 13 (Performance):");
        solution.compareApproaches(8);

        // Test Case 14: Edge case - N=0
        System.out.println("Test 14: " + solution.totalNQueensIterative(0));
        // Expected: 1

        // Test Case 15: All approaches for N=5
        System.out.println("Test 15 (All N=5): " +
                solution.totalNQueens(5) + " = " +
                solution.totalNQueensBit(5) + " = " +
                solution.totalNQueensArray(5) + " = " +
                solution.totalNQueensIterative(5));
        // Expected: 10 = 10 = 10 = 10
    }
}
