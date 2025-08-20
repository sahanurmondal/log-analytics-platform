package recursion.hard;

/**
 * LeetCode 247: Strobogrammatic Number II
 * https://leetcode.com/problems/strobogrammatic-number-ii/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Return all strobogrammatic numbers of length n.
 *
 * Constraints:
 * - 1 <= n <= 14
 *
 * Follow-ups:
 * 1. Can you count such numbers?
 * 2. Can you generate numbers with custom constraints?
 * 3. Can you optimize for large n?
 */
public class StrobogrammaticNumberII {
    public java.util.List<String> findStrobogrammatic(int n) {
        return helper(n, n);
    }

    private java.util.List<String> helper(int n, int total) {
        if (n == 0)
            return java.util.Arrays.asList("");
        if (n == 1)
            return java.util.Arrays.asList("0", "1", "8");
        java.util.List<String> list = helper(n - 2, total);
        java.util.List<String> res = new java.util.ArrayList<>();
        for (String s : list) {
            if (n != total)
                res.add("0" + s + "0");
            res.add("1" + s + "1");
            res.add("6" + s + "9");
            res.add("8" + s + "8");
            res.add("9" + s + "6");
        }
        return res;
    }

    // Follow-up 1: Count such numbers
    public int countStrobogrammatic(int n) {
        return findStrobogrammatic(n).size();
    }

    // Follow-up 2: Generate with custom constraints (e.g., no leading zeros)
    public java.util.List<String> findStrobogrammaticNoLeadingZero(int n) {
        java.util.List<String> res = new java.util.ArrayList<>();
        for (String s : findStrobogrammatic(n)) {
            if (n == 1 || s.charAt(0) != '0')
                res.add(s);
        }
        return res;
    }

    // Follow-up 3: Optimize for large n (not needed for n <= 14)

    public static void main(String[] args) {
        StrobogrammaticNumberII solution = new StrobogrammaticNumberII();
        System.out.println(solution.findStrobogrammatic(2)); // ["11","69","88","96"]
        System.out.println(solution.countStrobogrammatic(2)); // 4
        System.out.println(solution.findStrobogrammaticNoLeadingZero(2)); // ["11","69","88","96"]
    }
}
