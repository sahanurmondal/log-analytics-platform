package tries.hard;

/**
 * LeetCode 244: Shortest Word Distance II
 * https://leetcode.com/problems/shortest-word-distance-ii/
 *
 * Description:
 * Design a data structure that will be initialized with a string array, and
 * then it should answer queries of the shortest distance between two different
 * strings from the array.
 *
 * Constraints:
 * - 1 <= wordsDict.length <= 3 * 10^4
 * - 1 <= wordsDict[i].length <= 10
 * - wordsDict[i] consists of lowercase English letters
 * - word1 and word2 are in wordsDict
 * - word1 != word2
 *
 * Follow-up:
 * - Can you optimize for multiple queries?
 * - Can you use a trie to group similar words?
 * - Can you handle updates to the word dictionary?
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 244: Shortest Word Distance II
 * https://leetcode.com/problems/shortest-word-distance-ii/
 *
 * Description:
 * Design a data structure that will be initialized with a string array, and
 * then it should answer queries of the shortest distance between two different
 * strings from the array.
 *
 * Constraints:
 * - 1 <= wordsDict.length <= 3 * 10^4
 * - 1 <= wordsDict[i].length <= 10
 * - wordsDict[i] consists of lowercase English letters
 * - word1 and word2 are in wordsDict
 * - word1 != word2
 *
 * Follow-up:
 * - Can you optimize for multiple queries?
 * - Can you use a trie to group similar words?
 * - Can you handle updates to the word dictionary?
 */
public class ShortestWordDistance {
    private Map<String, List<Integer>> locations;

    public ShortestWordDistance(String[] wordsDict) {
        this.locations = new HashMap<>();
        for (int i = 0; i < wordsDict.length; i++) {
            locations.computeIfAbsent(wordsDict[i], k -> new ArrayList<>()).add(i);
        }
    }

    public int shortest(String word1, String word2) {
        List<Integer> loc1 = locations.get(word1);
        List<Integer> loc2 = locations.get(word2);
        int minDistance = Integer.MAX_VALUE;
        int i = 0, j = 0;
        while (i < loc1.size() && j < loc2.size()) {
            minDistance = Math.min(minDistance, Math.abs(loc1.get(i) - loc2.get(j)));
            if (loc1.get(i) < loc2.get(j)) {
                i++;
            } else {
                j++;
            }
        }
        return minDistance;
    }

    public static void main(String[] args) {
        String[] wordsDict = { "practice", "makes", "perfect", "coding", "makes" };
        ShortestWordDistance wordDistance = new ShortestWordDistance(wordsDict);

        System.out.println(wordDistance.shortest("coding", "practice")); // 3
        System.out.println(wordDistance.shortest("makes", "coding")); // 1

        // Edge Case: Words at boundaries
        String[] wordsDict2 = { "a", "b", "c", "d", "e" };
        ShortestWordDistance wd2 = new ShortestWordDistance(wordsDict2);
        System.out.println(wd2.shortest("a", "e")); // 4

        // Edge Case: Adjacent words
        System.out.println(wd2.shortest("a", "b")); // 1

        // Edge Case: Same word multiple times
        String[] wordsDict3 = { "a", "b", "a", "b", "a" };
        ShortestWordDistance wd3 = new ShortestWordDistance(wordsDict3);
        System.out.println(wd3.shortest("a", "b")); // 1

        // Edge Case: Large dictionary
        String[] large = new String[30000];
        for (int i = 0; i < 30000; i++) {
            large[i] = "word" + (i % 1000);
        }
        ShortestWordDistance wdLarge = new ShortestWordDistance(large);
        System.out.println(wdLarge.shortest("word0", "word1")); // Should be computed efficiently
    }
}
