package graphs.medium;

import java.util.*;

/**
 * LeetCode 997: Find the Town Judge
 * https://leetcode.com/problems/find-the-town-judge/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Find the town judge, who trusts nobody and is trusted by
 * everyone else.
 *
 * Constraints:
 * - 1 <= n <= 1000
 * - 0 <= trust.length <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve this with a single array?
 * 2. How would you handle a graph with multiple judges?
 * 3. Can you solve this using a map instead of arrays?
 */
public class FindTheTownJudge {

    // Approach 1: Two Arrays (In-degree and Out-degree) - O(n + E) time, O(n) space
    public int findJudge(int n, int[][] trust) {
        if (n == 1 && trust.length == 0) {
            return 1;
        }

        int[] inDegree = new int[n + 1];
        int[] outDegree = new int[n + 1];

        for (int[] t : trust) {
            outDegree[t[0]]++;
            inDegree[t[1]]++;
        }

        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == n - 1 && outDegree[i] == 0) {
                return i;
            }
        }

        return -1;
    }

    // Approach 2: Single Array - O(n + E) time, O(n) space
    public int findJudgeSingleArray(int n, int[][] trust) {
        if (n == 1 && trust.length == 0) {
            return 1;
        }

        int[] trustScores = new int[n + 1];

        for (int[] t : trust) {
            trustScores[t[0]]--; // Person trusts someone
            trustScores[t[1]]++; // Person is trusted
        }

        for (int i = 1; i <= n; i++) {
            if (trustScores[i] == n - 1) {
                return i;
            }
        }

        return -1;
    }

    // Follow-up 2: Handle multiple judges
    public List<Integer> findMultipleJudges(int n, int[][] trust) {
        if (n == 1 && trust.length == 0) {
            return Collections.singletonList(1);
        }

        int[] inDegree = new int[n + 1];
        int[] outDegree = new int[n + 1];

        for (int[] t : trust) {
            outDegree[t[0]]++;
            inDegree[t[1]]++;
        }

        List<Integer> judges = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == n - 1 && outDegree[i] == 0) {
                judges.add(i);
            }
        }

        return judges;
    }

    // Follow-up 3: Use a map
    public int findJudgeWithMap(int n, int[][] trust) {
        if (n == 1 && trust.length == 0) {
            return 1;
        }

        Map<Integer, Integer> trustScores = new HashMap<>();

        for (int[] t : trust) {
            trustScores.put(t[0], trustScores.getOrDefault(t[0], 0) - 1);
            trustScores.put(t[1], trustScores.getOrDefault(t[1], 0) + 1);
        }

        for (int i = 1; i <= n; i++) {
            if (trustScores.getOrDefault(i, 0) == n - 1) {
                return i;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        FindTheTownJudge ftj = new FindTheTownJudge();

        // Test case 1: LeetCode example 1
        int[][] trust1 = { { 1, 2 } };
        System.out.println("Town judge 1: " + ftj.findJudge(2, trust1)); // 2

        // Test case 2: LeetCode example 2
        int[][] trust2 = { { 1, 3 }, { 2, 3 } };
        System.out.println("Town judge 2: " + ftj.findJudge(3, trust2)); // 3

        // Test case 3: No judge
        int[][] trust3 = { { 1, 3 }, { 2, 3 }, { 3, 1 } };
        System.out.println("Town judge 3: " + ftj.findJudge(3, trust3)); // -1

        // Test case 4: Single array approach
        System.out.println("Town judge (single array): " + ftj.findJudgeSingleArray(3, trust2)); // 3

        // Test case 5: Multiple judges (not possible in LeetCode problem)
        // This would require a different problem setup, but we can test the method
        System.out.println("Multiple judges: " + ftj.findMultipleJudges(3, trust2)); // [3]

        // Test case 6: Map approach
        System.out.println("Town judge (map): " + ftj.findJudgeWithMap(3, trust2)); // 3

        // Test case 7: Single person
        int[][] trust7 = {};
        System.out.println("Town judge 7: " + ftj.findJudge(1, trust7)); // 1
    }
}
