package bitmanipulation.hard;

import java.util.*;

/**
 * LeetCode 1178: Number of Valid Words for Each Puzzle
 * https://leetcode.com/problems/number-of-valid-words-for-each-puzzle/
 *
 * Description: With respect to a given puzzle string, a word is valid if both
 * the following conditions are satisfied:
 * - word contains the first letter of puzzle.
 * - For each letter in word, that letter is in puzzle.
 * For example, if the puzzle is "abcdefg", then valid words are "faced",
 * "cabbage", and "baggage"; while invalid words are "beefed" (doesn't include
 * "a") and "based" (includes "s" which isn't in the puzzle).
 * Return an array answer, where answer[i] is the number of words in the given
 * word list that are valid with respect to the puzzle puzzles[i].
 * 
 * Constraints:
 * - 1 <= words.length <= 10^5
 * - 4 <= words[i].length <= 50
 * - 1 <= puzzles.length <= 10^4
 * - puzzles[i].length == 7
 * - words[i][j], puzzles[i][j] are English lowercase letters
 * - Each puzzles[i] doesn't contain repeated characters
 *
 * Follow-up:
 * - Can you use bitmasks to represent character sets?
 * - What about using Trie or HashMap?
 * 
 * Time Complexity: O(W + P * 2^7) where W is total length of words, P is number
 * of puzzles
 * Space Complexity: O(W)
 * 
 * Company Tags: Google
 */
public class NumberOfValidWordsForEachPuzzle {

    // Main optimized solution - Bitmask with HashMap
    public List<Integer> findNumOfValidWords(String[] words, String[] puzzles) {
        Map<Integer, Integer> wordMaskCount = new HashMap<>();

        // Convert words to bitmasks and count occurrences
        for (String word : words) {
            int mask = 0;
            for (char c : word.toCharArray()) {
                mask |= 1 << (c - 'a');
            }
            wordMaskCount.put(mask, wordMaskCount.getOrDefault(mask, 0) + 1);
        }

        List<Integer> result = new ArrayList<>();

        // For each puzzle, find valid words
        for (String puzzle : puzzles) {
            int puzzleMask = 0;
            for (char c : puzzle.toCharArray()) {
                puzzleMask |= 1 << (c - 'a');
            }

            char firstChar = puzzle.charAt(0);
            int firstCharMask = 1 << (firstChar - 'a');
            int count = 0;

            // Iterate through all subsets of puzzle that contain first character
            for (int subMask = puzzleMask; subMask > 0; subMask = (subMask - 1) & puzzleMask) {
                if ((subMask & firstCharMask) != 0) {
                    count += wordMaskCount.getOrDefault(subMask, 0);
                }
            }

            result.add(count);
        }

        return result;
    }

    // Alternative solution - Trie approach
    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        int count = 0;
    }

    public List<Integer> findNumOfValidWordsTrie(String[] words, String[] puzzles) {
        TrieNode root = new TrieNode();

        // Build Trie with word masks
        for (String word : words) {
            int mask = 0;
            for (char c : word.toCharArray()) {
                mask |= 1 << (c - 'a');
            }

            TrieNode node = root;
            for (int i = 0; i < 26; i++) {
                if ((mask & (1 << i)) != 0) {
                    if (node.children[i] == null) {
                        node.children[i] = new TrieNode();
                    }
                    node = node.children[i];
                }
            }
            node.count++;
        }

        List<Integer> result = new ArrayList<>();

        for (String puzzle : puzzles) {
            char firstChar = puzzle.charAt(0);
            int count = dfsCount(root, puzzle, 0, firstChar, false);
            result.add(count);
        }

        return result;
    }

    private int dfsCount(TrieNode node, String puzzle, int index, char firstChar, boolean hasFirst) {
        if (node == null)
            return 0;

        int count = 0;
        if (hasFirst) {
            count += node.count;
        }

        for (int i = index; i < puzzle.length(); i++) {
            char c = puzzle.charAt(i);
            boolean newHasFirst = hasFirst || (c == firstChar);
            count += dfsCount(node.children[c - 'a'], puzzle, i + 1, firstChar, newHasFirst);
        }

        return count;
    }

    public static void main(String[] args) {
        NumberOfValidWordsForEachPuzzle solution = new NumberOfValidWordsForEachPuzzle();

        String[] words1 = { "aaaa", "asas", "able", "ability", "actt", "actor", "access" };
        String[] puzzles1 = { "aboveyz", "abrodyz", "abslute", "absoryz", "actresz", "gaswxyz" };
        System.out.println(solution.findNumOfValidWords(words1, puzzles1)); // Expected: [1,1,3,2,4,0]

        String[] words2 = { "apple", "pleas", "please" };
        String[] puzzles2 = { "aelwxyz", "aelpxyz", "aelpsxy", "saelpxy", "xaelpsy" };
        System.out.println(solution.findNumOfValidWords(words2, puzzles2)); // Expected: [0,1,3,2,0]
    }
}
