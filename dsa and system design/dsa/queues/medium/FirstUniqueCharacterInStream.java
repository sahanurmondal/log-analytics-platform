package queues.medium;

/**
 * LeetCode 387: First Unique Character in a String (Stream version)
 * https://leetcode.com/problems/first-unique-character-in-a-string/
 *
 * Description:
 * Design a data structure to find the first unique character in a stream of
 * characters.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of lowercase English letters
 *
 * Follow-up:
 * - Can you solve it in O(1) time for each query?
 * - Can you extend to support removal of characters?
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * LeetCode 387: First Unique Character in a String (Stream version)
 * https://leetcode.com/problems/first-unique-character-in-a-string/
 *
 * Description:
 * Design a data structure to find the first unique character in a stream of
 * characters.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of lowercase English letters
 *
 * Follow-up:
 * - Can you solve it in O(1) time for each query?
 * - Can you extend to support removal of characters?
 */
public class FirstUniqueCharacterInStream {
    private Queue<Character> queue;
    private Map<Character, Integer> count;

    public FirstUniqueCharacterInStream() {
        this.queue = new LinkedList<>();
        this.count = new HashMap<>();
    }

    public void add(char c) {
        queue.offer(c);
        count.put(c, count.getOrDefault(c, 0) + 1);
    }

    public char getFirstUnique() {
        // Remove non-unique characters from front of queue
        while (!queue.isEmpty() && count.get(queue.peek()) > 1) {
            queue.poll();
        }

        return queue.isEmpty() ? '\0' : queue.peek();
    }

    public static void main(String[] args) {
        FirstUniqueCharacterInStream stream = new FirstUniqueCharacterInStream();
        stream.add('a');
        System.out.println(stream.getFirstUnique()); // 'a'
        stream.add('b');
        System.out.println(stream.getFirstUnique()); // 'a'
        stream.add('a');
        System.out.println(stream.getFirstUnique()); // 'b'
        stream.add('b');
        System.out.println(stream.getFirstUnique()); // '\0' (no unique)
        // Edge Case: All same characters
        stream.add('c');
        stream.add('c');
        System.out.println(stream.getFirstUnique()); // '\0'
        // Edge Case: New unique after duplicates
        stream.add('d');
        System.out.println(stream.getFirstUnique()); // 'd'
    }
}
