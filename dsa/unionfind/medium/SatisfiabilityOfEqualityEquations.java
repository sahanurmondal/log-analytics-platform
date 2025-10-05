package unionfind.medium;

import unionfind.UnionFind;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 990: Satisfiability of Equality Equations
 * https://leetcode.com/problems/satisfiability-of-equality-equations/
 *
 * Description:
 * You are given an array of strings equations that represent relationships
 * between variables.
 * Each string equations[i] is of length 4 and takes one of two different forms:
 * "xi==yi" or "xi!=yi". Return true if it is possible to assign integers to
 * variable names
 * so as to satisfy all the given equations, or false otherwise.
 *
 * Constraints:
 * - 1 <= equations.length <= 500
 * - equations[i].length == 4
 * - equations[i][0] is a lowercase letter
 * - equations[i][1] is either '=' or '!'
 * - equations[i][2] is '='
 * - equations[i][3] is a lowercase letter
 *
 * Visual Example:
 * Input: equations = ["a==b","b!=c","c==a"]
 * 
 * If a==b and c==a, then a==c
 * But we also have b!=c, which means a!=c
 * This is a contradiction!
 * 
 * Output: false
 *
 * Follow-up:
 * - Can you solve it using graph coloring?
 * - How would you handle more complex equations?
 */
public class SatisfiabilityOfEqualityEquations {

    public boolean equationsPossible(String[] equations) {
        UnionFind uf = new UnionFind(26);
        List<int[]> inequalities = new ArrayList<>();
        for (String eq : equations) {
            int x = eq.charAt(0) - 'a';
            int y = eq.charAt(3) - 'a';
            if (eq.charAt(1) == '=') {
                uf.union(x, y);
            } else {
                inequalities.add(new int[]{x, y});
            }
        }
        for (int[] ineq : inequalities) {
            if (uf.connected(ineq[0], ineq[1])) return false;
        }
        return true;
    }


    public static void main(String[] args) {
        SatisfiabilityOfEqualityEquations solution = new SatisfiabilityOfEqualityEquations();

        // Test case 1: Contradiction
        System.out.println(solution.equationsPossible(new String[] { "a==b", "b!=c", "c==a" })); // false

        // Test case 2: Valid equations
        System.out.println(solution.equationsPossible(new String[] { "b==a", "a==b" })); // true

        // Test case 3: Self inequality
        System.out.println(solution.equationsPossible(new String[] { "a==a", "a!=a" })); // false

        // Test case 4: Complex valid case
        System.out.println(solution.equationsPossible(new String[] { "c==c", "b==d", "x!=z" })); // true

        // Test case 5: Chain of equalities with inequality
        System.out.println(solution.equationsPossible(new String[] { "a==b", "b==c", "a!=c" })); // false
    }
}
