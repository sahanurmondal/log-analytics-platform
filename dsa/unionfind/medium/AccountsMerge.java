package unionfind.medium;

import unionfind.UnionFind;

import java.util.*;
import java.util.stream.IntStream;

/**
 * LeetCode 721: Accounts Merge
 * https://leetcode.com/problems/accounts-merge/
 * <p>
 * Description:
 * Given a list of accounts where each element accounts[i] is a list of strings,
 * where the first element accounts[i][0] is a name, and the rest of the
 * elements
 * are emails representing emails of the account.
 * <p>
 * Constraints:
 * - 1 <= accounts.length <= 1000
 * - 2 <= accounts[i].length <= 10
 * - 1 <= accounts[i][j].length <= 30
 * - accounts[i][0] consists of English letters.
 * - accounts[i][j] (for j > 0) is a valid email.
 * <p>
 * Visual Example:
 * Input: [["John","johnsmith@mail.com","john_newyork@mail.com"],
 * ["John","johnsmith@mail.com","john00@mail.com"],
 * ["Mary","mary@mail.com"],
 * ["John","johnnybravo@mail.com"]]
 * <p>
 * Output:
 * [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],
 * ["Mary","mary@mail.com"],
 * ["John","johnnybravo@mail.com"]]
 * <p>
 * Follow-up:
 * - Can you solve it using DFS as well?
 * - How would you handle real-time merging?
 */
public class AccountsMerge {

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        UnionFind uf = getUnionFind(accounts);

        // Group emails by root account
        Map<Integer, Set<String>> merged = new HashMap<>();
        IntStream.range(0, accounts.size()).forEach(i ->
                merged.computeIfAbsent(uf.find(i), k -> new TreeSet<>())
                        .addAll(accounts.get(i).stream().skip(1).toList()));


        // Build result
        List<List<String>> result = new ArrayList<>();
        for (int root : merged.keySet()) {
            List<String> account = new ArrayList<>();
            account.add(accounts.get(root).get(0)); // name
            account.addAll(merged.get(root)); // TreeSet is already sorted
            result.add(account);
        }

        return result;
    }

    private static UnionFind getUnionFind(List<List<String>> accounts) {
        int n = accounts.size();
        UnionFind uf = new UnionFind(n);
        Map<String, Integer> emailToIndex = new HashMap<>();
        IntStream.range(0, n).forEach(i ->
                accounts.get(i).stream().skip(1)
                        .forEach(email -> {
                            Integer prev = emailToIndex.putIfAbsent(email, i);
                            if (prev != null) uf.union(i, prev);
                        })
        );
        return uf;
    }

    public static void main(String[] args) {
        AccountsMerge solution = new AccountsMerge();

        // Test case 1: Multiple accounts to merge
        List<List<String>> accounts1 = List.of(
                List.of("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                List.of("John", "johnsmith@mail.com", "john00@mail.com"),
                List.of("Mary", "mary@mail.com"),
                List.of("John", "johnnybravo@mail.com")
        );
        System.out.println(solution.accountsMerge(accounts1));

        // Test case 2: No merging needed
        List<List<String>> accounts2 = List.of(
                List.of("Gabe", "Gabe0@m.co", "Gabe3@m.co", "Gabe1@m.co"),
                List.of("Kevin", "Kevin3@m.co", "Kevin5@m.co", "Kevin0@m.co"),
                List.of("Ethan", "Ethan5@m.co", "Ethan4@m.co", "Ethan0@m.co")
        );
        System.out.println(solution.accountsMerge(accounts2));
    }
}
