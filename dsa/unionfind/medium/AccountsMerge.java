package unionfind.medium;

import java.util.*;

/**
 * LeetCode 721: Accounts Merge
 * https://leetcode.com/problems/accounts-merge/
 *
 * Description:
 * Given a list of accounts where each element accounts[i] is a list of strings,
 * where the first element accounts[i][0] is a name, and the rest of the
 * elements
 * are emails representing emails of the account.
 *
 * Constraints:
 * - 1 <= accounts.length <= 1000
 * - 2 <= accounts[i].length <= 10
 * - 1 <= accounts[i][j].length <= 30
 * - accounts[i][0] consists of English letters.
 * - accounts[i][j] (for j > 0) is a valid email.
 *
 * Visual Example:
 * Input: [["John","johnsmith@mail.com","john_newyork@mail.com"],
 * ["John","johnsmith@mail.com","john00@mail.com"],
 * ["Mary","mary@mail.com"],
 * ["John","johnnybravo@mail.com"]]
 * 
 * Output:
 * [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],
 * ["Mary","mary@mail.com"],
 * ["John","johnnybravo@mail.com"]]
 *
 * Follow-up:
 * - Can you solve it using DFS as well?
 * - How would you handle real-time merging?
 */
public class AccountsMerge {

    class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        int n = accounts.size();
        UnionFind uf = new UnionFind(n);

        // Map email to account index
        Map<String, Integer> emailToIndex = new HashMap<>();

        // Build the union-find structure
        for (int i = 0; i < n; i++) {
            List<String> account = accounts.get(i);
            for (int j = 1; j < account.size(); j++) {
                String email = account.get(j);
                if (emailToIndex.containsKey(email)) {
                    uf.union(i, emailToIndex.get(email));
                } else {
                    emailToIndex.put(email, i);
                }
            }
        }

        // Group emails by root account
        Map<Integer, List<String>> merged = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            merged.putIfAbsent(root, new ArrayList<>());
            for (int j = 1; j < accounts.get(i).size(); j++) {
                merged.get(root).add(accounts.get(i).get(j));
            }
        }

        // Build result
        List<List<String>> result = new ArrayList<>();
        for (int root : merged.keySet()) {
            List<String> emails = merged.get(root);
            Collections.sort(emails);

            // Remove duplicates
            List<String> uniqueEmails = new ArrayList<>();
            String prev = "";
            for (String email : emails) {
                if (!email.equals(prev)) {
                    uniqueEmails.add(email);
                    prev = email;
                }
            }

            List<String> account = new ArrayList<>();
            account.add(accounts.get(root).get(0)); // name
            account.addAll(uniqueEmails);
            result.add(account);
        }

        return result;
    }

    public static void main(String[] args) {
        AccountsMerge solution = new AccountsMerge();

        // Test case 1: Multiple accounts to merge
        List<List<String>> accounts1 = Arrays.asList(
                Arrays.asList("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                Arrays.asList("John", "johnsmith@mail.com", "john00@mail.com"),
                Arrays.asList("Mary", "mary@mail.com"),
                Arrays.asList("John", "johnnybravo@mail.com"));
        System.out.println(solution.accountsMerge(accounts1));

        // Test case 2: No merging needed
        List<List<String>> accounts2 = Arrays.asList(
                Arrays.asList("Gabe", "Gabe0@m.co", "Gabe3@m.co", "Gabe1@m.co"),
                Arrays.asList("Kevin", "Kevin3@m.co", "Kevin5@m.co", "Kevin0@m.co"),
                Arrays.asList("Ethan", "Ethan5@m.co", "Ethan4@m.co", "Ethan0@m.co"));
        System.out.println(solution.accountsMerge(accounts2));
    }
}
