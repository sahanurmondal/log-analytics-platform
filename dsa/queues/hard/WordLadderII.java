package queues.hard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * LeetCode 126: Word Ladder II
 * https://leetcode.com/problems/word-ladder-ii/
 *
 * Description:
 * A transformation sequence from word beginWord to word endWord using a
 * dictionary wordList is a sequence of words.
 * Return all the shortest transformation sequences from beginWord to endWord.
 *
 * Constraints:
 * - 1 <= beginWord.length <= 5
 * - endWord.length == beginWord.length
 * - 1 <= wordList.length <= 500
 * - wordList[i].length == beginWord.length
 *
 * Follow-up:
 * - Can you solve it using BFS with backtracking?
 * - Can you optimize memory usage?
 */
public class WordLadderII {
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord))
            return result;

        // BFS to build parent map
        Map<String, List<String>> parents = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(beginWord);
        visited.add(beginWord);
        boolean found = false;

        while (!queue.isEmpty() && !found) {
            int size = queue.size();
            Set<String> currentLevel = new HashSet<>();

            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                char[] chars = word.toCharArray();

                for (int j = 0; j < chars.length; j++) {
                    char original = chars[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original)
                            continue;
                        chars[j] = c;
                        String newWord = new String(chars);

                        if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                            parents.computeIfAbsent(newWord, k -> new ArrayList<>()).add(word);
                            currentLevel.add(newWord);
                            if (newWord.equals(endWord))
                                found = true;
                        }
                    }
                    chars[j] = original;
                }
            }

            visited.addAll(currentLevel);
            queue.addAll(currentLevel);
        }

        if (found) {
            List<String> path = new ArrayList<>();
            path.add(endWord);
            dfs(endWord, beginWord, parents, path, result);
        }

        return result;
    }

    private void dfs(String word, String beginWord, Map<String, List<String>> parents,
            List<String> path, List<List<String>> result) {
        if (word.equals(beginWord)) {
            List<String> copy = new ArrayList<>(path);
            java.util.Collections.reverse(copy);
            result.add(copy);
            return;
        }

        if (parents.containsKey(word)) {
            for (String parent : parents.get(word)) {
                path.add(parent);
                dfs(parent, beginWord, parents, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        WordLadderII solution = new WordLadderII();
        List<String> wordList1 = java.util.Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        System.out.println(solution.findLadders("hit", "cog", wordList1)); // [["hit","hot","dot","dog","cog"],["hit","hot","lot","log","cog"]]

        List<String> wordList2 = java.util.Arrays.asList("hot", "dot", "dog", "lot", "log");
        System.out.println(solution.findLadders("hit", "cog", wordList2)); // []

        // Edge Case: Direct path
        System.out.println(solution.findLadders("hit", "hot", java.util.Arrays.asList("hot"))); // [["hit","hot"]]
    }
}
