package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Word Ladder II
 * 
 * Description:
 * Given two words and a word list, find all shortest transformation sequences
 * from beginWord to endWord.
 * 
 * Companies: Amazon, Google, Facebook
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class WordLadderII {

    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        Set<String> wordSet = new HashSet<>(wordList);

        if (!wordSet.contains(endWord)) {
            return result;
        }

        Map<String, List<String>> neighbors = new HashMap<>();
        Map<String, Integer> distance = new HashMap<>();

        bfs(beginWord, endWord, wordSet, neighbors, distance);

        if (!distance.containsKey(endWord)) {
            return result;
        }

        List<String> path = new ArrayList<>();
        path.add(beginWord);
        dfs(beginWord, endWord, neighbors, distance, path, result);

        return result;
    }

    private void bfs(String beginWord, String endWord, Set<String> wordSet,
            Map<String, List<String>> neighbors, Map<String, Integer> distance) {

        for (String word : wordSet) {
            neighbors.put(word, new ArrayList<>());
        }
        neighbors.put(beginWord, new ArrayList<>());

        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        distance.put(beginWord, 0);

        while (!queue.isEmpty()) {
            boolean foundEnd = false;

            Set<String> visitedThisLevel = new HashSet<>();
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                int currentDistance = distance.get(word);

                List<String> neighborWords = getNeighbors(word, wordSet);

                for (String neighbor : neighborWords) {
                    neighbors.get(word).add(neighbor);

                    if (!distance.containsKey(neighbor)) {
                        distance.put(neighbor, currentDistance + 1);
                        if (neighbor.equals(endWord)) {
                            foundEnd = true;
                        } else {
                            visitedThisLevel.add(neighbor);
                        }
                    }
                }
            }

            queue.addAll(visitedThisLevel);
            if (foundEnd)
                break;
        }
    }

    private void dfs(String currentWord, String endWord, Map<String, List<String>> neighbors,
            Map<String, Integer> distance, List<String> path, List<List<String>> result) {

        if (currentWord.equals(endWord)) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (String neighbor : neighbors.get(currentWord)) {
            if (distance.get(neighbor) == distance.get(currentWord) + 1) {
                path.add(neighbor);
                dfs(neighbor, endWord, neighbors, distance, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    private List<String> getNeighbors(String word, Set<String> wordSet) {
        List<String> neighbors = new ArrayList<>();
        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char originalChar = chars[i];

            for (char c = 'a'; c <= 'z'; c++) {
                if (c != originalChar) {
                    chars[i] = c;
                    String newWord = new String(chars);

                    if (wordSet.contains(newWord)) {
                        neighbors.add(newWord);
                    }
                }
            }

            chars[i] = originalChar;
        }

        return neighbors;
    }

    public static void main(String[] args) {
        WordLadderII solution = new WordLadderII();

        String beginWord = "hit";
        String endWord = "cog";
        List<String> wordList = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");

        List<List<String>> result = solution.findLadders(beginWord, endWord, wordList);
        System.out.println("Number of shortest paths: " + result.size());

        for (List<String> path : result) {
            System.out.println(path);
        }
    }
}
