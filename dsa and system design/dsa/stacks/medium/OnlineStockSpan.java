package stacks.medium;

import java.util.*;

/**
 * LeetCode 901: Online Stock Span
 * https://leetcode.com/problems/online-stock-span/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Implement the StockSpanner class that calculates the span of
 * stock's price for the current day.
 *
 * Constraints:
 * - 1 <= price <= 10^5
 * - At most 10^4 calls to next()
 * 
 * Follow-up Questions:
 * 1. Can you optimize for large number of operations?
 * 2. Can you handle duplicate prices?
 * 3. Can you support rollback?
 */
public class OnlineStockSpan {

    private Stack<int[]> stack = new Stack<>();
    private int day = 0;

    public int next(int price) {
        int span = 1;
        while (!stack.isEmpty() && stack.peek()[0] <= price)
            span += stack.pop()[1];
        stack.push(new int[] { price, span });
        day++;
        return span;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        OnlineStockSpan solution = new OnlineStockSpan();

        // Test case 1: Basic case
        int[] prices = { 100, 80, 60, 70, 60, 75, 85 };
        System.out.println("Test 1 - prices: " + Arrays.toString(prices));
        for (int price : prices)
            System.out.print(solution.next(price) + " ");
        System.out.println();

        // Edge cases
        System.out.println("\nEdge cases:");
        OnlineStockSpan emptySpan = new OnlineStockSpan();
        System.out.println("Single price: " + emptySpan.next(50));
    }
}
