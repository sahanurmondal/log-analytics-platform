package binarysearch.hard;

/**
 * LeetCode 1231: Divide Chocolate
 * https://leetcode.com/problems/divide-chocolate/
 *
 * Description:
 * You have one chocolate bar that consists of some chunks. Each chunk has its
 * own sweetness given by the array sweetness.
 * You want to share the chocolate with your k friends so you start cutting the
 * chocolate bar into k + 1 pieces using k cuts,
 * each piece consists of some consecutive chunks.
 * Being generous, you will eat the piece with the minimum total sweetness and
 * give the other pieces to your friends.
 * Find the maximum total sweetness of the piece you can get by cutting the
 * chocolate optimally.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg, Uber
 * Difficulty: Hard
 * Asked: 2023-2024 (Medium Frequency)
 *
 * Constraints:
 * - 0 <= k < sweetness.length <= 10^4
 * - 1 <= sweetness[i] <= 10^5
 *
 * Follow-ups:
 * - What if you want to maximize the minimum sweetness among all pieces?
 * - Can you solve this with greedy approach?
 * - How would you handle negative sweetness values?
 */
public class DivideChocolate {

    // Binary Search on Answer - O(n * log(sum)) time, O(1) space
    public int maximizeSweetness(int[] sweetness, int k) {
        int left = getMinSweetness(sweetness);
        int right = getTotalSweetness(sweetness);

        while (left < right) {
            int mid = left + (right - left + 1) / 2; // Use ceiling to avoid infinite loop

            if (canDivide(sweetness, k, mid)) {
                left = mid; // Try for higher sweetness
            } else {
                right = mid - 1; // Reduce target sweetness
            }
        }

        return left;
    }

    private boolean canDivide(int[] sweetness, int k, int minSweetness) {
        int pieces = 0;
        int currentSum = 0;

        for (int sweet : sweetness) {
            currentSum += sweet;

            if (currentSum >= minSweetness) {
                pieces++;
                currentSum = 0;

                if (pieces > k) {
                    return true; // We can make k+1 pieces
                }
            }
        }

        return false;
    }

    private int getMinSweetness(int[] sweetness) {
        int min = sweetness[0];
        for (int sweet : sweetness) {
            min = Math.min(min, sweet);
        }
        return min;
    }

    private int getTotalSweetness(int[] sweetness) {
        int total = 0;
        for (int sweet : sweetness) {
            total += sweet;
        }
        return total;
    }

    // Alternative implementation with different bounds
    public int maximizeSweetnessAlt(int[] sweetness, int k) {
        int left = 1; // Minimum possible sweetness
        int right = getTotalSweetness(sweetness) / (k + 1); // Upper bound estimate

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (canDivideAlt(sweetness, k, mid)) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return right;
    }

    private boolean canDivideAlt(int[] sweetness, int k, int minSweetness) {
        int pieces = 1; // Start with 1 piece
        int currentSum = 0;

        for (int sweet : sweetness) {
            currentSum += sweet;

            if (currentSum >= minSweetness) {
                pieces++;
                currentSum = 0;
            }
        }

        return pieces >= k + 1;
    }

    // Greedy approach for comparison - O(n * k) time
    public int maximizeSweetnessGreedy(int[] sweetness, int k) {
        // Try all possible minimum sweetness values
        int maxResult = 0;

        for (int minTarget = 1; minTarget <= getTotalSweetness(sweetness); minTarget++) {
            if (canDivide(sweetness, k, minTarget)) {
                maxResult = minTarget;
            } else {
                break; // No point checking higher values
            }
        }

        return maxResult;
    }

    // Dynamic Programming approach - O(n^2 * k) time, O(n * k) space
    public int maximizeSweetnessDP(int[] sweetness, int k) {
        int n = sweetness.length;

        // Prefix sums for easy range sum calculation
        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + sweetness[i];
        }

        // dp[i][j] = maximum minimum sweetness using i pieces from first j chunks
        int[][] dp = new int[k + 2][n + 1];

        // Base case: using 1 piece
        for (int j = 1; j <= n; j++) {
            dp[1][j] = prefixSum[j];
        }

        // Fill the DP table
        for (int pieces = 2; pieces <= k + 1; pieces++) {
            for (int j = pieces; j <= n; j++) {
                for (int prev = pieces - 1; prev < j; prev++) {
                    int currentPieceSweetness = prefixSum[j] - prefixSum[prev];
                    dp[pieces][j] = Math.max(dp[pieces][j],
                            Math.min(dp[pieces - 1][prev], currentPieceSweetness));
                }
            }
        }

        return dp[k + 1][n];
    }

    // Find the actual division points
    public int[] findDivisionPoints(int[] sweetness, int k) {
        int targetSweetness = maximizeSweetness(sweetness, k);
        java.util.List<Integer> cuts = new java.util.ArrayList<>();

        int currentSum = 0;
        for (int i = 0; i < sweetness.length; i++) {
            currentSum += sweetness[i];

            if (currentSum >= targetSweetness && cuts.size() < k) {
                cuts.add(i);
                currentSum = 0;
            }
        }

        return cuts.stream().mapToInt(i -> i).toArray();
    }

    // Get all piece sweetness values
    public int[] getAllPieceSweetness(int[] sweetness, int k) {
        int[] cuts = findDivisionPoints(sweetness, k);
        int[] pieces = new int[k + 1];

        int start = 0;
        for (int i = 0; i < cuts.length; i++) {
            for (int j = start; j <= cuts[i]; j++) {
                pieces[i] += sweetness[j];
            }
            start = cuts[i] + 1;
        }

        // Last piece
        for (int j = start; j < sweetness.length; j++) {
            pieces[k] += sweetness[j];
        }

        return pieces;
    }

    // Handle edge case where k = 0 (no cuts)
    public int maximizeSweetnessNoFriends(int[] sweetness) {
        return getTotalSweetness(sweetness);
    }

    // Validate that the solution is correct
    public boolean validateSolution(int[] sweetness, int k, int result) {
        int[] pieces = getAllPieceSweetness(sweetness, k);

        // Check if we have k+1 pieces
        if (pieces.length != k + 1)
            return false;

        // Check if minimum piece has the expected sweetness
        int minPiece = pieces[0];
        for (int piece : pieces) {
            minPiece = Math.min(minPiece, piece);
        }

        return minPiece == result;
    }

    // Binary search with detailed logging
    public int maximizeSweetnessWithLogging(int[] sweetness, int k) {
        System.out.println("Sweetness array: " + java.util.Arrays.toString(sweetness));
        System.out.println("Number of friends (k): " + k);

        int left = getMinSweetness(sweetness);
        int right = getTotalSweetness(sweetness);

        System.out.println("Search range: [" + left + ", " + right + "]");

        int iteration = 0;
        while (left < right) {
            iteration++;
            int mid = left + (right - left + 1) / 2;
            boolean canDivideResult = canDivide(sweetness, k, mid);

            System.out.println("Iteration " + iteration + ": mid=" + mid + ", canDivide=" + canDivideResult);

            if (canDivideResult) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        System.out.println("Final result: " + left);
        return left;
    }

    public static void main(String[] args) {
        DivideChocolate solution = new DivideChocolate();

        // Test Case 1: [1,2,3,4,5,6,7,8,9], k = 5
        int[] sweetness1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        System.out.println(solution.maximizeSweetness(sweetness1, 5)); // Expected: 6

        // Test Case 2: [5,6,7,8,9,1,2,3,4], k = 8
        int[] sweetness2 = { 5, 6, 7, 8, 9, 1, 2, 3, 4 };
        System.out.println(solution.maximizeSweetness(sweetness2, 8)); // Expected: 1

        // Test Case 3: [1,2,2,1,2,2,1,2,2], k = 2
        int[] sweetness3 = { 1, 2, 2, 1, 2, 2, 1, 2, 2 };
        System.out.println(solution.maximizeSweetness(sweetness3, 2)); // Expected: 5

        // Test Case 4: No friends (k = 0)
        int[] sweetness4 = { 1, 2, 3, 4, 5 };
        System.out.println(solution.maximizeSweetnessNoFriends(sweetness4)); // Expected: 15

        // Test Case 5: Single chunk
        int[] sweetness5 = { 10 };
        System.out.println(solution.maximizeSweetness(sweetness5, 0)); // Expected: 10

        // Test Case 6: All same sweetness
        int[] sweetness6 = { 3, 3, 3, 3, 3, 3 };
        System.out.println(solution.maximizeSweetness(sweetness6, 2)); // Expected: 6

        // Test alternative approach
        System.out.println("Alternative: " + solution.maximizeSweetnessAlt(sweetness1, 5)); // Expected: 6

        // Test DP approach (small input only due to time complexity)
        int[] small = { 1, 2, 3, 4 };
        System.out.println("DP: " + solution.maximizeSweetnessDP(small, 1)); // Expected: 4

        // Test greedy approach (small input only)
        System.out.println("Greedy: " + solution.maximizeSweetnessGreedy(small, 1)); // Expected: 4

        // Test division points
        int[] cuts = solution.findDivisionPoints(sweetness1, 5);
        System.out.println("Division points: " + java.util.Arrays.toString(cuts));

        // Test all piece sweetness
        int[] pieces = solution.getAllPieceSweetness(sweetness1, 5);
        System.out.println("All pieces: " + java.util.Arrays.toString(pieces));

        // Test validation
        int result = solution.maximizeSweetness(sweetness1, 5);
        boolean isValid = solution.validateSolution(sweetness1, 5, result);
        System.out.println("Solution validation: " + isValid); // Expected: true

        // Test with logging
        System.out.println("\nDetailed execution:");
        solution.maximizeSweetnessWithLogging(new int[] { 1, 2, 3, 4 }, 1);

        // Edge cases
        int[] edge1 = { 1, 1, 1, 1, 1 };
        System.out.println("All ones: " + solution.maximizeSweetness(edge1, 2)); // Expected: 1

        int[] edge2 = { 10, 1, 1, 1, 1 };
        System.out.println("One large chunk: " + solution.maximizeSweetness(edge2, 1)); // Expected: 4

        // Large test case
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++) {
            large[i] = i + 1;
        }

        long startTime = System.currentTimeMillis();
        int largeResult = solution.maximizeSweetness(large, 99);
        long endTime = System.currentTimeMillis();
        System.out.println("Large test result: " + largeResult + " (time: " + (endTime - startTime) + "ms)");

        // Performance comparison
        startTime = System.currentTimeMillis();
        solution.maximizeSweetness(sweetness1, 5);
        long binaryTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        solution.maximizeSweetnessGreedy(sweetness1, 5);
        long greedyTime = System.currentTimeMillis() - startTime;

        System.out.println("Binary search time: " + binaryTime + "ms");
        System.out.println("Greedy time: " + greedyTime + "ms");
    }
}
