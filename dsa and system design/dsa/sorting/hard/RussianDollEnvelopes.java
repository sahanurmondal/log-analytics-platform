package sorting.hard;

/**
 * LeetCode 354: Russian Doll Envelopes
 * https://leetcode.com/problems/russian-doll-envelopes/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a list of envelopes, find the maximum number of envelopes you can
 * Russian doll.
 *
 * Constraints:
 * - 1 <= envelopes.length <= 10^5
 * - 1 <= w, h <= 10^5
 *
 * Follow-ups:
 * 1. Can you return the sequence of envelopes?
 * 2. Can you optimize for large input?
 * 3. Can you handle envelopes with equal width or height?
 */
public class RussianDollEnvelopes {
    public int maxEnvelopes(int[][] envelopes) {
        java.util.Arrays.sort(envelopes, (a, b) -> a[0] == b[0] ? b[1] - a[1] : a[0] - b[0]);
        int[] dp = new int[envelopes.length];
        int len = 0;
        for (int[] env : envelopes) {
            int idx = java.util.Arrays.binarySearch(dp, 0, len, env[1]);
            if (idx < 0)
                idx = -(idx + 1);
            dp[idx] = env[1];
            if (idx == len)
                len++;
        }
        return len;
    }

    // Follow-up 1: Return sequence of envelopes
    public java.util.List<int[]> envelopeSequence(int[][] envelopes) {
        java.util.Arrays.sort(envelopes, (a, b) -> a[0] == b[0] ? b[1] - a[1] : a[0] - b[0]);
        int n = envelopes.length;
        int[] dp = new int[n], prev = new int[n];
        java.util.Arrays.fill(prev, -1);
        int len = 0, last = -1;
        for (int i = 0; i < n; i++) {
            int idx = java.util.Arrays.binarySearch(dp, 0, len, envelopes[i][1]);
            if (idx < 0)
                idx = -(idx + 1);
            dp[idx] = envelopes[i][1];
            prev[i] = idx > 0 ? dp[idx - 1] : -1;
            if (idx == len) {
                len++;
                last = i;
            }
        }
        java.util.List<int[]> seq = new java.util.ArrayList<>();
        for (int i = last; i >= 0; i--) {
            if (prev[i] == -1 || envelopes[i][1] == prev[i]) {
                seq.add(envelopes[i]);
                if (prev[i] == -1)
                    break;
            }
        }
        java.util.Collections.reverse(seq);
        return seq;
    }

    // Follow-up 2: Optimize for large input (already handled above)
    // Follow-up 3: Handle envelopes with equal width or height (already handled
    // above)

    public static void main(String[] args) {
        RussianDollEnvelopes solution = new RussianDollEnvelopes();
        int[][] envelopes = { { 5, 4 }, { 6, 4 }, { 6, 7 }, { 2, 3 } };
        System.out.println(solution.maxEnvelopes(envelopes)); // 3
        System.out.println(solution.envelopeSequence(envelopes)); // sequence
    }
}
