package tries.hard;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

/**
 * LeetCode 127: Word Ladder
 * https://leetcode.com/problems/word-ladder/
 *
 * Description:
 * A transformation sequence from word beginWord to word endWord using a
 * dictionary wordList is a sequence of words beginWord -> s1 -> s2 -> ... -> sk
 * such that every adjacent pair of words differs by a single letter.
 *
 * Constraints:
 * - 1 <= beginWord.length <= 10
 * - endWord.length == beginWord.length
 * - 1 <= wordList.length <= 5000
 * - wordList[i].length == beginWord.length
 * - beginWord, endWord, and wordList[i] consist of lowercase English letters
 * - beginWord != endWord
 * - All the strings in wordList are unique
 *
 * Follow-up:
 * - Can you use a trie to optimize word lookup?
 * - Can you use bidirectional BFS?
 * - Can you handle very large dictionaries efficiently?
 */
public class WordLadder {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord))
            return 0;
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        int steps = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                char[] arr = word.toCharArray();
                for (int j = 0; j < arr.length; j++) {
                    char old = arr[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        arr[j] = c;
                        String next = new String(arr);
                        if (next.equals(endWord))
                            return steps + 1;
                        if (wordSet.contains(next) && !visited.contains(next)) {
                            queue.offer(next);
                            visited.add(next);
                        }
                    }
                    arr[j] = old;
                }
            }
            steps++;
        }
        return 0;
    }

    public static void main(String[] args) {
        WordLadder solution = new WordLadder();

        List<String> wordList1 = java.util.Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        System.out.println(solution.ladderLength("hit", "cog", wordList1)); // 5

        List<String> wordList2 = java.util.Arrays.asList("hot", "dot", "dog", "lot", "log");
        System.out.println(solution.ladderLength("hit", "cog", wordList2)); // 0

        // Edge Case: Direct transformation
        List<String> wordList3 = java.util.Arrays.asList("hot");
        System.out.println(solution.ladderLength("hot", "hot", wordList3)); // Should handle gracefully

        // Edge Case: No path
        List<String> wordList4 = java.util.Arrays.asList("abc", "def");
        System.out.println(solution.ladderLength("abc", "def", wordList4)); // 0

        // Edge Case: Single character difference
        List<String> wordList5 = java.util.Arrays.asList("cat");
        System.out.println(solution.ladderLength("bat", "cat", wordList5)); // 2

        // Edge Case: Long transformation
        List<String> wordList6 = java.util.Arrays.asList("a", "b", "c");
        System.out.println(solution.ladderLength("a", "c", wordList6)); // 3
    }
}
