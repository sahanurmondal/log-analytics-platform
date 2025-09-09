package graphs.medium;

import java.util.*;

/**
 * LeetCode 127: Word Ladder
 * https://leetcode.com/problems/word-ladder/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description: Find shortest transformation sequence from beginWord to endWord.
 *
 * Constraints:
 * - 1 <= beginWord.length <= 10
 * - endWord.length == beginWord.length
 * - 1 <= wordList.length <= 5000
 * 
 * Follow-up Questions:
 * 1. Can you use bidirectional BFS?
 * 2. Can you return all shortest paths?
 * 3. Can you optimize for memory usage?
 */
public class WordLadder {
    // Approach 1: BFS - O(M^2 * N) time, O(M^2 * N) space
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord))
            return 0;

        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        int steps = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                char[] arr = word.toCharArray();
                for (int j = 0; j < arr.length; j++) {
                    char original = arr[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        arr[j] = c;
                        String newWord = new String(arr);
                        if (newWord.equals(endWord))
                            return steps + 1;
                        if (wordSet.remove(newWord)) {
                            queue.offer(newWord);
                        }
                    }
                    arr[j] = original;
                }
            }
            steps++;
        }
        return 0;
    }

    // Approach 2: Bidirectional BFS - O(M^2 * N) time, better in practice
    public int ladderLengthBidirectional(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord))
            return 0;

        Set<String> beginSet = new HashSet<>();
        Set<String> endSet = new HashSet<>();
        beginSet.add(beginWord);
        endSet.add(endWord);

        int steps = 1;
        while (!beginSet.isEmpty() && !endSet.isEmpty()) {
            if (beginSet.size() > endSet.size()) {
                Set<String> temp = beginSet;
                beginSet = endSet;
                endSet = temp;
            }

            Set<String> nextSet = new HashSet<>();
            for (String word : beginSet) {
                char[] arr = word.toCharArray();
                for (int i = 0; i < arr.length; i++) {
                    char original = arr[i];
                    for (char c = 'a'; c <= 'z'; c++) {
                        arr[i] = c;
                        String newWord = new String(arr);
                        if (endSet.contains(newWord))
                            return steps + 1;
                        if (wordSet.remove(newWord)) {
                            nextSet.add(newWord);
                        }
                    }
                    arr[i] = original;
                }
            }
            beginSet = nextSet;
            steps++;
        }
        return 0;
    }

    // Follow-up: Find all shortest paths
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord))
            return result;

        // BFS to build graph
        Map<String, List<String>> graph = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        visited.add(beginWord);
        boolean found = false;

        while (!queue.isEmpty() && !found) {
            Set<String> currentLevel = new HashSet<>();
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                char[] arr = word.toCharArray();

                for (int j = 0; j < arr.length; j++) {
                    char original = arr[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        arr[j] = c;
                        String newWord = new String(arr);

                        if (wordSet.contains(newWord)) {
                            if (newWord.equals(endWord))
                                found = true;
                            if (!visited.contains(newWord)) {
                                if (!currentLevel.contains(newWord)) {
                                    currentLevel.add(newWord);
                                    queue.offer(newWord);
                                }
                                graph.computeIfAbsent(word, k -> new ArrayList<>()).add(newWord);
                            }
                        }
                    }
                    arr[j] = original;
                }
            }
            visited.addAll(currentLevel);
        }

        // DFS to find all paths
        dfs(beginWord, endWord, graph, new ArrayList<>(), result);
        return result;
    }

    private void dfs(String word, String endWord, Map<String, List<String>> graph,
            List<String> path, List<List<String>> result) {
        path.add(word);
        if (word.equals(endWord)) {
            result.add(new ArrayList<>(path));
        } else {
            for (String next : graph.getOrDefault(word, Collections.emptyList())) {
                dfs(next, endWord, graph, path, result);
            }
        }
        path.remove(path.size() - 1);
    }

    public static void main(String[] args) {
        WordLadder wl = new WordLadder();
        List<String> wordList1 = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");

        // Basic case
        System.out.println(wl.ladderLength("hit", "cog", wordList1)); // 5

        // No path
        List<String> wordList2 = Arrays.asList("hot", "dot", "dog", "lot", "log");
        System.out.println(wl.ladderLength("hit", "cog", wordList2)); // 0

        // Bidirectional BFS
        System.out.println(wl.ladderLengthBidirectional("hit", "cog", wordList1)); // 5

        // All shortest paths
        System.out.println(wl.findLadders("hit", "cog", wordList1));

        // Edge cases
        System.out.println(wl.ladderLength("a", "c", Arrays.asList("a", "b", "c"))); // 2
        System.out.println(wl.ladderLength("hot", "dog", Arrays.asList("hot", "dog"))); // 0
    }
}
