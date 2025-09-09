package tries.medium;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 1023: Camelcase Matching
 * https://leetcode.com/problems/camelcase-matching/
 *
 * Description:
 * Given an array of strings queries and a string pattern, return a boolean
 * array answer where answer[i] is true if queries[i] matches pattern, and false
 * otherwise.
 *
 * Constraints:
 * - 1 <= pattern.length <= 100
 * - 1 <= queries.length <= 1000
 * - 1 <= queries[i].length <= 100
 * - pattern and queries[i] consist of English letters
 *
 * Follow-up:
 * - Can you solve it using a trie for pattern matching?
 * - Can you optimize for multiple pattern queries?
 * - Can you extend to support more complex patterns?
 */
public class CamelcaseMatching {
    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> result = new ArrayList<>();
        for (String query : queries) {
            result.add(isMatch(query, pattern));
        }
        return result;
    }

    private boolean isMatch(String query, String pattern) {
        int i = 0, j = 0;
        while (i < query.length() && j < pattern.length()) {
            if (query.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            } else if (Character.isLowerCase(query.charAt(i))) {
                i++;
            } else {
                return false;
            }
        }
        while (i < query.length()) {
            if (Character.isUpperCase(query.charAt(i)))
                return false;
            i++;
        }
        return j == pattern.length();
    }

    public static void main(String[] args) {
        CamelcaseMatching solution = new CamelcaseMatching();

        System.out.println(solution.camelMatch(
                new String[] { "FooBar", "ForceFeedBack", "FootBall", "FrameBuffer", "ForceFeedBack" }, "FB"));
        // [true,true,false,true,true]

        System.out.println(solution.camelMatch(
                new String[] { "FooBar", "ForceFeedBack", "FootBall", "FrameBuffer", "ForceFeedBack" }, "FoBa"));
        // [true,false,true,false,false]

        System.out.println(solution.camelMatch(
                new String[] { "FooBar", "ForceFeedBack", "FootBall", "FrameBuffer", "ForceFeedBack" }, "FoBaT"));
        // [false,false,false,false,false]

        // Edge Case: Exact match
        System.out.println(solution.camelMatch(new String[] { "CompetitiveProgramming" }, "CompetitiveProgramming"));
        // [true]

        // Edge Case: Pattern longer than query
        System.out.println(solution.camelMatch(new String[] { "Fb" }, "Facebook"));
        // [false]

        // Edge Case: No uppercase in pattern
        System.out.println(solution.camelMatch(new String[] { "FooBar" }, "foo"));
        // [false]

        // Edge Case: All lowercase
        System.out.println(solution.camelMatch(new String[] { "foobar" }, "foobar"));
        // [true]
    }
}
