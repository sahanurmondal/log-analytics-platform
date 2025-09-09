package greedy.hard;

/**
 * LeetCode 1753: Maximum Score From Removing Stones
 * https://leetcode.com/problems/maximum-score-from-removing-stones/
 * 
 * Companies: Amazon, Google, Microsoft
 * Frequency: Medium (Asked in 25+ interviews)
 *
 * Description:
 * You are playing a solitaire game with three piles of stones of sizes a, b,
 * and c respectively.
 * Each turn you choose two different non-empty piles, take one stone from each,
 * and add 1 point to your score.
 * The game stops when there are fewer than two non-empty piles (which means
 * there are at most 1 stone left in total).
 * Given three integers a, b and c, return the maximum score you can get.
 *
 * Constraints:
 * - 1 <= a, b, c <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you solve it without simulation?
 * 2. What if we have k piles instead of 3?
 * 3. Can you prove the optimal strategy mathematically?
 */
public class MaximumScoreFromRemovingStones {

    // Approach 1: Mathematical Solution - O(1) time, O(1) space
    public int maximumScore(int a, int b, int c) {
        // Sort the piles to make largest pile 'c'
        int[] piles = { a, b, c };
        java.util.Arrays.sort(piles);

        // If largest pile >= sum of other two, we can only remove sum of smaller two
        // Otherwise, we can remove (total sum) / 2 pairs
        if (piles[2] >= piles[0] + piles[1]) {
            return piles[0] + piles[1];
        } else {
            return (piles[0] + piles[1] + piles[2]) / 2;
        }
    }

    // Approach 2: Greedy Simulation with PriorityQueue - O(n log k) time, O(k)
    // space
    public int maximumScoreSimulation(int a, int b, int c) {
        java.util.PriorityQueue<Integer> maxHeap = new java.util.PriorityQueue<>((x, y) -> y - x);
        if (a > 0)
            maxHeap.offer(a);
        if (b > 0)
            maxHeap.offer(b);
        if (c > 0)
            maxHeap.offer(c);

        int score = 0;

        while (maxHeap.size() >= 2) {
            int first = maxHeap.poll();
            int second = maxHeap.poll();

            score++;

            if (first - 1 > 0)
                maxHeap.offer(first - 1);
            if (second - 1 > 0)
                maxHeap.offer(second - 1);
        }

        return score;
    }

    // Approach 3: Optimized Greedy - O(1) time, O(1) space
    public int maximumScoreOptimized(int a, int b, int c) {
        int max = Math.max(Math.max(a, b), c);
        int sum = a + b + c;

        // Key insight: We can never exceed (sum / 2) or (sum - max)
        return Math.min(sum / 2, sum - max);
    }

    // Follow-up: Extend to k piles
    public int maximumScoreKPiles(int[] piles) {
        java.util.PriorityQueue<Integer> maxHeap = new java.util.PriorityQueue<>((x, y) -> y - x);
        int totalSum = 0;

        for (int pile : piles) {
            if (pile > 0) {
                maxHeap.offer(pile);
                totalSum += pile;
            }
        }

        if (maxHeap.size() < 2)
            return 0;

        int maxPile = maxHeap.peek();

        // If max pile >= sum of all others, limited by others
        if (maxPile >= totalSum - maxPile) {
            return totalSum - maxPile;
        } else {
            return totalSum / 2;
        }
    }

    // Follow-up: Show the actual moves
    public java.util.List<String> getOptimalMoves(int a, int b, int c) {
        java.util.List<String> moves = new java.util.ArrayList<>();
        java.util.PriorityQueue<int[]> maxHeap = new java.util.PriorityQueue<>((x, y) -> y[0] - x[0]);

        if (a > 0)
            maxHeap.offer(new int[] { a, 0 }); // {count, pile_id}
        if (b > 0)
            maxHeap.offer(new int[] { b, 1 });
        if (c > 0)
            maxHeap.offer(new int[] { c, 2 });

        String[] pileNames = { "A", "B", "C" };

        while (maxHeap.size() >= 2) {
            int[] first = maxHeap.poll();
            int[] second = maxHeap.poll();

            moves.add("Remove from pile " + pileNames[first[1]] + " and pile " + pileNames[second[1]]);

            if (first[0] - 1 > 0)
                maxHeap.offer(new int[] { first[0] - 1, first[1] });
            if (second[0] - 1 > 0)
                maxHeap.offer(new int[] { second[0] - 1, second[1] });
        }

        return moves;
    }

    public static void main(String[] args) {
        MaximumScoreFromRemovingStones solution = new MaximumScoreFromRemovingStones();

        // Test Case 1: Basic example
        System.out.println("Basic 1: " + solution.maximumScore(2, 4, 6)); // 6

        // Test Case 2: Another basic
        System.out.println("Basic 2: " + solution.maximumScore(4, 4, 6)); // 7

        // Test Case 3: All equal
        System.out.println("All equal: " + solution.maximumScore(5, 5, 5)); // 7

        // Test Case 4: One large pile
        System.out.println("One large: " + solution.maximumScore(1, 1, 10)); // 2

        // Test Case 5: Two zeros
        System.out.println("Two zeros: " + solution.maximumScore(1, 8, 8)); // 8

        // Test Case 6: Edge minimum
        System.out.println("Minimum: " + solution.maximumScore(1, 1, 1)); // 1

        // Test approaches comparison
        System.out.println("Simulation: " + solution.maximumScoreSimulation(2, 4, 6)); // 6
        System.out.println("Optimized: " + solution.maximumScoreOptimized(2, 4, 6)); // 6

        // Test Case 7: Large numbers
        System.out.println("Large: " + solution.maximumScore(100000, 100000, 100000)); // 150000

        // Test Case 8: K piles follow-up
        System.out.println("4 piles: " + solution.maximumScoreKPiles(new int[] { 2, 4, 6, 8 })); // 10

        // Test Case 9: Show moves
        java.util.List<String> moves = solution.getOptimalMoves(2, 2, 3);
        System.out.println("Optimal moves for (2,2,3):");
        for (int i = 0; i < Math.min(3, moves.size()); i++) {
            System.out.println("  " + (i + 1) + ". " + moves.get(i));
        }

        // Test Case 10: Edge cases
        System.out.println("Edge case: " + solution.maximumScore(20, 3, 2)); // 5
    }
}
