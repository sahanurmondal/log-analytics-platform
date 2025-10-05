package unionfind.hard;

import unionfind.UnionFind;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 990: Satisfiability of Equality Equations
 * https://leetcode.com/problems/satisfiability-of-equality-equations/
 *
 * Description:
 * You are given an array of strings equations that represent relationships
 * between variables
 * where each string equations[i] is of length 4 and takes one of two different
 * forms:
 * "xi==yi" or "xi!=yi".
 * Here, xi and yi are lowercase letters (not necessarily different) that
 * represent one-letter variable names.
 * Return true if it is possible to assign integers to variable names so as to
 * satisfy all the given equations,
 * or false otherwise.
 *
 * Constraints:
 * - 1 <= equations.length <= 500
 * - equations[i].length == 4
 * - equations[i][0] is a lowercase letter
 * - equations[i][1] is either '=' or '!'
 * - equations[i][2] is '='
 * - equations[i][3] is a lowercase letter
 */
public class SatisfiabilityEquations {

    public boolean equationsPossible(String[] equations) {
        UnionFind uf = new UnionFind(26);
        List<int[]> inequalities = new ArrayList<>();
        // Process equality equations first
        for (String eq : equations) {
            int x = eq.charAt(0) - 'a';
            int y = eq.charAt(3) - 'a';
            if (eq.charAt(1) == '=') {
                uf.union(x, y);
            }else{
                inequalities.add(new int[]{x,y});
            }
        }

        for (int[] ineq : inequalities) {
            if (uf.connected(ineq[0], ineq[1])) return false;
        }

        return true;
    }

    public static void main(String[] args) {
        SatisfiabilityEquations solution = new SatisfiabilityEquations();

        // Test case 1
        String[] equations1 = { "a==b", "b!=a" };
        System.out.println(solution.equationsPossible(equations1)); // false

        // Test case 2
        String[] equations2 = { "b==a", "a==b" };
        System.out.println(solution.equationsPossible(equations2)); // true

        // Test case 3
        String[] equations3 = { "a==b", "b==c", "a==c" };
        System.out.println(solution.equationsPossible(equations3)); // true
    }
}
