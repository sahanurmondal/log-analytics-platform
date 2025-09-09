package design.medium;

import java.util.*;

/**
 * LeetCode 341: Flatten Nested List Iterator
 * https://leetcode.com/problems/flatten-nested-list-iterator/
 *
 * Description: You are given a nested list of integers nestedList. Each element
 * is either an integer or a list whose elements may also be integers or other
 * lists. Implement an iterator to flatten it.
 * 
 * Constraints:
 * - 1 <= nestedList.length <= 500
 * - The values of the integers in the nested list is in the range [-10^6, 10^6]
 *
 * Follow-up:
 * - Can you solve it without pre-processing the list?
 * 
 * Time Complexity: O(n) total for all next() calls
 * Space Complexity: O(d) where d is max depth
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class NestedIterator implements Iterator<Integer> {

    // Mock NestedInteger interface for compilation
    interface NestedInteger {
        boolean isInteger();

        Integer getInteger();

        List<NestedInteger> getList();
    }

    private Stack<NestedInteger> stack;

    public NestedIterator(List<NestedInteger> nestedList) {
        stack = new Stack<>();
        // Add elements in reverse order
        for (int i = nestedList.size() - 1; i >= 0; i--) {
            stack.push(nestedList.get(i));
        }
    }

    @Override
    public Integer next() {
        if (!hasNext()) {
            return null;
        }
        return stack.pop().getInteger();
    }

    @Override
    public boolean hasNext() {
        while (!stack.isEmpty()) {
            NestedInteger current = stack.peek();
            if (current.isInteger()) {
                return true;
            }

            // Current is a list, expand it
            stack.pop();
            List<NestedInteger> list = current.getList();
            for (int i = list.size() - 1; i >= 0; i--) {
                stack.push(list.get(i));
            }
        }
        return false;
    }

    // Alternative implementation - Pre-process approach
    static class NestedIteratorPreProcess implements Iterator<Integer> {
        private List<Integer> flattened;
        private int index;

        public NestedIteratorPreProcess(List<NestedInteger> nestedList) {
            flattened = new ArrayList<>();
            index = 0;
            flatten(nestedList);
        }

        private void flatten(List<NestedInteger> nestedList) {
            for (NestedInteger ni : nestedList) {
                if (ni.isInteger()) {
                    flattened.add(ni.getInteger());
                } else {
                    flatten(ni.getList());
                }
            }
        }

        @Override
        public Integer next() {
            return flattened.get(index++);
        }

        @Override
        public boolean hasNext() {
            return index < flattened.size();
        }
    }

    // Mock implementation for testing
    static class MockNestedInteger implements NestedInteger {
        private Integer value;
        private List<NestedInteger> list;

        public MockNestedInteger(int value) {
            this.value = value;
        }

        public MockNestedInteger(List<NestedInteger> list) {
            this.list = list;
        }

        @Override
        public boolean isInteger() {
            return value != null;
        }

        @Override
        public Integer getInteger() {
            return value;
        }

        @Override
        public List<NestedInteger> getList() {
            return list;
        }
    }

    public static void main(String[] args) {
        // Test case: [[1,1],2,[1,1]]
        List<NestedInteger> nestedList = Arrays.asList(
                new MockNestedInteger(Arrays.asList(
                        new MockNestedInteger(1),
                        new MockNestedInteger(1))),
                new MockNestedInteger(2),
                new MockNestedInteger(Arrays.asList(
                        new MockNestedInteger(1),
                        new MockNestedInteger(1))));

        NestedIterator iterator = new NestedIterator(nestedList);
        List<Integer> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        System.out.println(result); // Expected: [1, 1, 2, 1, 1]
    }
}
