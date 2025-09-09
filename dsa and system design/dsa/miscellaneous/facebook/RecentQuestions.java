package miscellaneous.facebook;

import java.util.*;

/**
 * Recent Facebook/Meta Interview Questions (2021-2024)
 * Compiled from LeetCode Discuss, GeeksforGeeks, and interview experiences
 */
public class RecentQuestions {

    /**
     * Question: Given a string s, remove duplicate letters so that every letter
     * appears once and only once.
     * You must make sure your result is the smallest in lexicographical order among
     * all possible results.
     * 
     * Company: Facebook/Meta
     * Difficulty: Medium
     * Asked: Frequently in 2023-2024
     */
    public String removeDuplicateLetters(String s) {
        Map<Character, Integer> lastOccurrence = new HashMap<>();
        Set<Character> seen = new HashSet<>();
        Stack<Character> stack = new Stack<>();

        // Record last occurrence of each character
        for (int i = 0; i < s.length(); i++) {
            lastOccurrence.put(s.charAt(i), i);
        }

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (seen.contains(c))
                continue;

            // Remove characters that are lexicographically larger and will appear later
            while (!stack.isEmpty() && stack.peek() > c && lastOccurrence.get(stack.peek()) > i) {
                seen.remove(stack.pop());
            }

            stack.push(c);
            seen.add(c);
        }

        StringBuilder result = new StringBuilder();
        for (char c : stack) {
            result.append(c);
        }

        return result.toString();
    }

    /**
     * Question: Design a data structure that follows the constraints of a Least
     * Recently Used (LRU) cache.
     * 
     * Company: Facebook/Meta
     * Difficulty: Medium
     * Asked: Very frequently in 2022-2024
     */
    class LRUCache {
        class Node {
            int key, value;
            Node prev, next;

            Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        private Map<Integer, Node> cache;
        private Node head, tail;
        private int capacity;

        public LRUCache(int capacity) {
            this.capacity = capacity;
            cache = new HashMap<>();
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            if (cache.containsKey(key)) {
                Node node = cache.get(key);
                moveToHead(node);
                return node.value;
            }
            return -1;
        }

        public void put(int key, int value) {
            if (cache.containsKey(key)) {
                Node node = cache.get(key);
                node.value = value;
                moveToHead(node);
            } else {
                Node newNode = new Node(key, value);

                if (cache.size() >= capacity) {
                    Node tail = removeTail();
                    cache.remove(tail.key);
                }

                cache.put(key, newNode);
                addToHead(newNode);
            }
        }

        private void addToHead(Node node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void moveToHead(Node node) {
            removeNode(node);
            addToHead(node);
        }

        private Node removeTail() {
            Node lastNode = tail.prev;
            removeNode(lastNode);
            return lastNode;
        }
    }

    /**
     * Question: Given an array of integers and a target sum, find all unique
     * combinations
     * where the candidate numbers sum to target. Each number may only be used once.
     * 
     * Company: Facebook/Meta
     * Difficulty: Medium
     * Asked: Multiple times in 2023-2024
     */
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int target, int start, List<Integer> combination,
            List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(combination));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (i > start && candidates[i] == candidates[i - 1])
                continue;
            if (candidates[i] > target)
                break;

            combination.add(candidates[i]);
            backtrack(candidates, target - candidates[i], i + 1, combination, result);
            combination.remove(combination.size() - 1);
        }
    }

    public static void main(String[] args) {
        RecentQuestions solution = new RecentQuestions();

        // Test remove duplicate letters
        System.out.println(solution.removeDuplicateLetters("bcabc")); // "abc"
        System.out.println(solution.removeDuplicateLetters("cbacdcbc")); // "acdb"

        // Test LRU Cache
        LRUCache lru = solution.new LRUCache(2);
        lru.put(1, 1);
        lru.put(2, 2);
        System.out.println(lru.get(1)); // 1
        lru.put(3, 3);
        System.out.println(lru.get(2)); // -1

        // Test combination sum
        int[] candidates = { 10, 1, 2, 7, 6, 1, 5 };
        System.out.println(solution.combinationSum2(candidates, 8));
    }
}
