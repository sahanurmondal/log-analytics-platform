package greedy.medium;

import java.util.List;

/**
 * LeetCode 763: Partition Labels
 * https://leetcode.com/problems/partition-labels/
 *
 * Description:
 * Given a string S, partition it into as many parts as possible so that each
 * letter appears in at most one part, and return a list of integers
 * representing the size of these parts.
 *
 * Constraints:
 * - 1 <= S.length <= 500
 * - S consists of lowercase English letters.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 763: Partition Labels
 * https://leetcode.com/problems/partition-labels/
 *
 * Description:
 * Given a string S, partition it into as many parts as possible so that each
 * letter appears in at most one part, and return a list of integers
 * representing the size of these parts.
 *
 * Constraints:
 * - 1 <= S.length <= 500
 * - S consists of lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it using greedy approach with last occurrence tracking?
 * - Can you extend to handle Unicode characters?
 */
public class PartitionLabels {
    public List<Integer> partitionLabels(String S) {
        List<Integer> result = new ArrayList<>();
        if (S == null || S.length() == 0)
            return result;

        // Find last occurrence of each character
        int[] lastOccurrence = new int[26];
        for (int i = 0; i < S.length(); i++) {
            lastOccurrence[S.charAt(i) - 'a'] = i;
        }

        int start = 0, end = 0;
        for (int i = 0; i < S.length(); i++) {
            end = Math.max(end, lastOccurrence[S.charAt(i) - 'a']);

            // If we've reached the end of current partition
            if (i == end) {
                result.add(end - start + 1);
                start = i + 1;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        PartitionLabels solution = new PartitionLabels();
        System.out.println(solution.partitionLabels("ababcbacadefegdehijhklij")); // [9,7,8]
        // Edge Case: All unique
        System.out.println(solution.partitionLabels("abcdef")); // [1,1,1,1,1,1]
        // Edge Case: All same
        System.out.println(solution.partitionLabels("aaaaa")); // [5]
        // Edge Case: Empty string
        System.out.println(solution.partitionLabels("")); // []
    }
}
