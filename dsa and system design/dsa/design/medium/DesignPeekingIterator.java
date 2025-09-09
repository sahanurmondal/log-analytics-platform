package design.medium;

import java.util.*;

/**
 * LeetCode 284: Peeking Iterator
 * https://leetcode.com/problems/peeking-iterator/
 *
 * Description: Design an iterator that supports the peek operation on an
 * existing iterator.
 * 
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - 1 <= nums[i] <= 1000
 * - At most 1000 calls will be made to next and hasNext
 * - At most 500 calls will be made to peek
 *
 * Follow-up:
 * - Can you implement it without using extra space?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Apple
 */
public class DesignPeekingIterator implements Iterator<Integer> {

    private Iterator<Integer> iterator;
    private Integer peekedValue;
    private boolean hasPeeked;

    public DesignPeekingIterator(Iterator<Integer> iterator) {
        this.iterator = iterator;
        this.hasPeeked = false;
    }

    public Integer peek() {
        if (!hasPeeked) {
            peekedValue = iterator.next();
            hasPeeked = true;
        }
        return peekedValue;
    }

    @Override
    public Integer next() {
        if (hasPeeked) {
            Integer result = peekedValue;
            hasPeeked = false;
            peekedValue = null;
            return result;
        }
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return hasPeeked || iterator.hasNext();
    }

    // Alternative implementation using queue
    static class PeekingIteratorQueue implements Iterator<Integer> {
        private Queue<Integer> queue;

        public PeekingIteratorQueue(Iterator<Integer> iterator) {
            queue = new LinkedList<>();
            while (iterator.hasNext()) {
                queue.offer(iterator.next());
            }
        }

        public Integer peek() {
            return queue.peek();
        }

        @Override
        public Integer next() {
            return queue.poll();
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }
    }

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(1, 2, 3);
        DesignPeekingIterator peekingIterator = new DesignPeekingIterator(nums.iterator());

        System.out.println(peekingIterator.next()); // Expected: 1
        System.out.println(peekingIterator.peek()); // Expected: 2
        System.out.println(peekingIterator.next()); // Expected: 2
        System.out.println(peekingIterator.next()); // Expected: 3
        System.out.println(peekingIterator.hasNext()); // Expected: false

        // Test queue implementation
        PeekingIteratorQueue queueIterator = new PeekingIteratorQueue(Arrays.asList(4, 5, 6).iterator());
        System.out.println("Queue - peek: " + queueIterator.peek()); // Expected: 4
        System.out.println("Queue - next: " + queueIterator.next()); // Expected: 4
        System.out.println("Queue - hasNext: " + queueIterator.hasNext()); // Expected: true
    }
}
