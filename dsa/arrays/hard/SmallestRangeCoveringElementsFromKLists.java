package arrays.hard;

import java.util.*;

/**
 * LeetCode 632: Smallest Range Covering Elements from K Lists
 * https://leetcode.com/problems/smallest-range-covering-elements-from-k-lists/
 *
 * Description:
 * You have k lists of sorted integers in non-decreasing order.
 * Find the smallest range that includes at least one number from each of the k
 * lists.
 *
 * Constraints:
 * - nums.length == k
 * - 1 <= k <= 3500
 * - 1 <= nums[i].length <= 50
 * - -10^5 <= nums[i][j] <= 10^5
 * - nums[i] is sorted in non-decreasing order
 *
 * Follow-up:
 * - Can you solve it using priority queue?
 * 
 * Time Complexity: O(n * log k) where n is total elements
 * Space Complexity: O(k)
 */
public class SmallestRangeCoveringElementsFromKLists {

    class Element {
        int value;
        int listIndex;
        int elementIndex;

        Element(int value, int listIndex, int elementIndex) {
            this.value = value;
            this.listIndex = listIndex;
            this.elementIndex = elementIndex;
        }
    }

    public int[] smallestRange(List<List<Integer>> nums) {
        PriorityQueue<Element> minHeap = new PriorityQueue<>((a, b) -> a.value - b.value);
        int max = Integer.MIN_VALUE;

        // Add first element from each list
        for (int i = 0; i < nums.size(); i++) {
            Element element = new Element(nums.get(i).get(0), i, 0);
            minHeap.offer(element);
            max = Math.max(max, nums.get(i).get(0));
        }

        int[] result = { minHeap.peek().value, max };

        while (minHeap.size() == nums.size()) {
            Element minElement = minHeap.poll();

            // Update result if current range is smaller
            if (max - minElement.value < result[1] - result[0]) {
                result[0] = minElement.value;
                result[1] = max;
            }

            // Add next element from the same list
            if (minElement.elementIndex + 1 < nums.get(minElement.listIndex).size()) {
                int nextValue = nums.get(minElement.listIndex).get(minElement.elementIndex + 1);
                minHeap.offer(new Element(nextValue, minElement.listIndex, minElement.elementIndex + 1));
                max = Math.max(max, nextValue);
            }
        }

        return result;
    }

    // Alternative solution - Sliding window with sorting
    public int[] smallestRangeSlidingWindow(List<List<Integer>> nums) {
        List<Element> allElements = new ArrayList<>();

        // Collect all elements with their list indices
        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < nums.get(i).size(); j++) {
                allElements.add(new Element(nums.get(i).get(j), i, j));
            }
        }

        // Sort by value
        allElements.sort((a, b) -> a.value - b.value);

        Map<Integer, Integer> count = new HashMap<>();
        int left = 0;
        int[] result = { allElements.get(0).value, allElements.get(allElements.size() - 1).value };

        for (int right = 0; right < allElements.size(); right++) {
            count.put(allElements.get(right).listIndex,
                    count.getOrDefault(allElements.get(right).listIndex, 0) + 1);

            while (count.size() == nums.size()) {
                if (allElements.get(right).value - allElements.get(left).value < result[1] - result[0]) {
                    result[0] = allElements.get(left).value;
                    result[1] = allElements.get(right).value;
                }

                int leftListIndex = allElements.get(left).listIndex;
                count.put(leftListIndex, count.get(leftListIndex) - 1);
                if (count.get(leftListIndex) == 0) {
                    count.remove(leftListIndex);
                }
                left++;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SmallestRangeCoveringElementsFromKLists solution = new SmallestRangeCoveringElementsFromKLists();

        // Test Case 1: Normal case
        List<List<Integer>> nums1 = Arrays.asList(
                Arrays.asList(4, 10, 15, 24, 26),
                Arrays.asList(0, 9, 12, 20),
                Arrays.asList(5, 18, 22, 30));
        System.out.println(Arrays.toString(solution.smallestRange(nums1))); // Expected: [20,24]

        // Test Case 2: Edge case - single element lists
        List<List<Integer>> nums2 = Arrays.asList(
                Arrays.asList(1),
                Arrays.asList(2),
                Arrays.asList(3));
        System.out.println(Arrays.toString(solution.smallestRange(nums2))); // Expected: [1,3]

        // Test Case 3: Corner case - overlapping ranges
        List<List<Integer>> nums3 = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(1, 2, 3),
                Arrays.asList(1, 2, 3));
        System.out.println(Arrays.toString(solution.smallestRange(nums3))); // Expected: [1,1]

        // Test Case 4: Negative numbers
        List<List<Integer>> nums4 = Arrays.asList(
                Arrays.asList(-5, -3, -1),
                Arrays.asList(-2, 0, 2),
                Arrays.asList(-1, 1, 3));
        System.out.println(Arrays.toString(solution.smallestRange(nums4))); // Expected: [-1,0] or similar

        // Test Case 5: Single list
        List<List<Integer>> nums5 = Arrays.asList(
                Arrays.asList(1, 2, 3, 4, 5));
        System.out.println(Arrays.toString(solution.smallestRange(nums5))); // Expected: [1,1]

        // Test Case 6: Different lengths
        List<List<Integer>> nums6 = Arrays.asList(
                Arrays.asList(1),
                Arrays.asList(2, 3, 4, 5, 6),
                Arrays.asList(7, 8));
        System.out.println(Arrays.toString(solution.smallestRange(nums6))); // Expected: [1,7]

        // Test Case 7: Large range
        List<List<Integer>> nums7 = Arrays.asList(
                Arrays.asList(-100000),
                Arrays.asList(0),
                Arrays.asList(100000));
        System.out.println(Arrays.toString(solution.smallestRange(nums7))); // Expected: [-100000,100000]

        // Test Case 8: Same values
        List<List<Integer>> nums8 = Arrays.asList(
                Arrays.asList(1, 1, 1),
                Arrays.asList(1, 1, 1));
        System.out.println(Arrays.toString(solution.smallestRange(nums8))); // Expected: [1,1]

        // Test Case 9: Sequential lists
        List<List<Integer>> nums9 = Arrays.asList(
                Arrays.asList(1, 3, 5),
                Arrays.asList(2, 4, 6));
        System.out.println(Arrays.toString(solution.smallestRange(nums9))); // Expected: [1,2]

        // Test Case 10: Complex case
        List<List<Integer>> nums10 = Arrays.asList(
                Arrays.asList(1, 10, 20),
                Arrays.asList(5, 15, 25),
                Arrays.asList(8, 18, 28));
        System.out.println(Arrays.toString(solution.smallestRange(nums10))); // Expected: [8,10]
    }
}