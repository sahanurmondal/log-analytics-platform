package tries.medium;

/**
 * LeetCode 677: Map Sum Pairs
 * https://leetcode.com/problems/map-sum-pairs/
 *
 * Description:
 * Design a map that allows you to do the following:
 * - Maps a string key to a given value.
 * - Returns the sum of the values that have a key with a prefix equal to a
 * given string.
 *
 * Constraints:
 * - 1 <= key.length, prefix.length <= 50
 * - key and prefix consist of only lowercase English letters
 * - 1 <= val <= 1000
 * - At most 50 calls will be made to insert and sum
 *
 * Follow-up:
 * - Can you optimize the sum operation using prefix sums?
 * - Can you handle key updates efficiently?
 * - Can you extend to support key deletion?
 */
public class MapSum {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        int sum;
    }

    private TrieNode root = new TrieNode();
    private java.util.Map<String, Integer> map = new java.util.HashMap<>();

    public MapSum() {
        root = new TrieNode();
    }

    public void insert(String key, int val) {
        int delta = val - map.getOrDefault(key, 0);
        map.put(key, val);
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            if (node.children[c - 'a'] == null)
                node.children[c - 'a'] = new TrieNode();
            node = node.children[c - 'a'];
            node.sum += delta;
        }
    }

    public int sum(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (node.children[c - 'a'] == null)
                return 0;
            node = node.children[c - 'a'];
        }
        return node.sum;
    }

    public static void main(String[] args) {
        MapSum mapSum = new MapSum();

        mapSum.insert("apple", 3);
        System.out.println(mapSum.sum("ap")); // 3

        mapSum.insert("app", 2);
        System.out.println(mapSum.sum("ap")); // 5

        // Edge Case: Update existing key
        mapSum.insert("apple", 5);
        System.out.println(mapSum.sum("ap")); // 7

        // Edge Case: No matching prefix
        System.out.println(mapSum.sum("xyz")); // 0

        // Edge Case: Empty prefix
        System.out.println(mapSum.sum("")); // Sum of all values

        // Edge Case: Single character
        mapSum.insert("a", 10);
        System.out.println(mapSum.sum("a")); // 17

        // Edge Case: Overlapping keys
        mapSum.insert("application", 1);
        mapSum.insert("apply", 2);
        System.out.println(mapSum.sum("app")); // 10 (2+5+1+2)
    }
}
