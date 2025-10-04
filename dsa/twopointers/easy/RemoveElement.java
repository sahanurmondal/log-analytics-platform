package twopointers.easy;

import java.util.*;

/**
 * LeetCode 27: Remove Element
 * https://leetcode.com/problems/remove-element/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple, Bloomberg
 * Frequency: Very High (Asked in 1800+ interviews)
 *
 * Description:
 * Given an integer array nums and an integer val, remove all occurrences of val
 * in nums in-place.
 * The order of the elements may be changed. Then return the number of elements
 * in nums which are not equal to val.
 * 
 * Consider the number of elements in nums which are not equal to val be k, to
 * get accepted, you need to do the following things:
 * - Change the array nums such that the first k elements of nums contain the
 * elements which are not equal to val.
 * - The remaining elements of nums are not important as well as the size of
 * nums.
 * - Return k.
 * 
 * Constraints:
 * - 0 <= nums.length <= 100
 * - 0 <= nums[i] <= 50
 * - 0 <= val <= 100
 * 
 * Follow-up Questions:
 * 1. What if you need to maintain the relative order of elements?
 * 2. How would you remove multiple different values efficiently?
 * 3. Can you implement removal with minimum number of operations?
 * 4. What about removing elements based on a condition/predicate?
 * 5. How to handle removal in a linked list?
 * 6. What about removing duplicates while keeping one occurrence?
 */
public class RemoveElement {

    // Approach 1: Two pointers (fast-slow) - O(n) time, O(1) space
    public static int removeElement(int[] nums, int val) {
        int slow = 0; // Points to position where next valid element should be placed

        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != val) {
                nums[slow] = nums[fast];
                slow++;
            }
        }

        return slow;
    }

    // Approach 2: Two pointers (left-right swap) - O(n) time, O(1) space, fewer
    // writes
    public static int removeElementSwap(int[] nums, int val) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            if (nums[left] == val) {
                // Swap with element from right
                nums[left] = nums[right];
                right--;
            } else {
                left++;
            }
        }

        return left;
    }

    // Approach 3: Count and shift - O(n) time, O(1) space
    public static int removeElementCountShift(int[] nums, int val) {
        int writeIndex = 0;
        int count = 0;

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[writeIndex++] = nums[i];
                count++;
            }
        }

        return count;
    }

    // Approach 4: Using ArrayList for dynamic removal - O(n) time, O(n) space
    public static int removeElementList(int[] nums, int val) {
        List<Integer> result = new ArrayList<>();

        for (int num : nums) {
            if (num != val) {
                result.add(num);
            }
        }

        // Copy back to original array
        for (int i = 0; i < result.size(); i++) {
            nums[i] = result.get(i);
        }

        return result.size();
    }

    // Approach 5: Recursive approach - O(n) time, O(n) space (due to recursion)
    public static int removeElementRecursive(int[] nums, int val) {
        return removeElementRecursiveHelper(nums, val, 0, 0);
    }

    private static int removeElementRecursiveHelper(int[] nums, int val, int index, int writePos) {
        if (index >= nums.length) {
            return writePos;
        }

        if (nums[index] != val) {
            nums[writePos] = nums[index];
            return removeElementRecursiveHelper(nums, val, index + 1, writePos + 1);
        } else {
            return removeElementRecursiveHelper(nums, val, index + 1, writePos);
        }
    }

    // Follow-up 1: Maintain relative order (stable removal)
    public static class StableRemoval {

        public static int removeElementStable(int[] nums, int val) {
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (nums[readIndex] != val) {
                    if (writeIndex != readIndex) {
                        nums[writeIndex] = nums[readIndex];
                    }
                    writeIndex++;
                }
            }

            return writeIndex;
        }

        public static int[] removeElementStableNewArray(int[] nums, int val) {
            List<Integer> result = new ArrayList<>();

            for (int num : nums) {
                if (num != val) {
                    result.add(num);
                }
            }

            return result.stream().mapToInt(Integer::intValue).toArray();
        }

        public static int removeElementStableMinMoves(int[] nums, int val) {
            int moves = 0;
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (nums[readIndex] != val) {
                    if (writeIndex != readIndex) {
                        nums[writeIndex] = nums[readIndex];
                        moves++;
                    }
                    writeIndex++;
                }
            }

            System.out.println("Number of moves: " + moves);
            return writeIndex;
        }
    }

    // Follow-up 2: Remove multiple values
    public static class MultipleValueRemoval {

        public static int removeMultipleValues(int[] nums, int[] valsToRemove) {
            Set<Integer> valSet = new HashSet<>();
            for (int val : valsToRemove) {
                valSet.add(val);
            }

            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (!valSet.contains(nums[readIndex])) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }

        public static int removeMultipleValuesSorted(int[] nums, int[] valsToRemove) {
            // Assumes valsToRemove is sorted
            Arrays.sort(valsToRemove);
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (Arrays.binarySearch(valsToRemove, nums[readIndex]) < 0) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }

        public static int removeValuesInRange(int[] nums, int minVal, int maxVal) {
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (nums[readIndex] < minVal || nums[readIndex] > maxVal) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }

        public static int removeEvenNumbers(int[] nums) {
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (nums[readIndex] % 2 != 0) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }
    }

    // Follow-up 3: Minimum operations approach
    public static class MinimumOperations {

        public static Result removeElementMinOps(int[] nums, int val) {
            int writeIndex = 0;
            int swaps = 0;
            int comparisons = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                comparisons++;
                if (nums[readIndex] != val) {
                    if (writeIndex != readIndex) {
                        nums[writeIndex] = nums[readIndex];
                        swaps++;
                    }
                    writeIndex++;
                }
            }

            return new Result(writeIndex, swaps, comparisons);
        }

        public static Result removeElementOptimizedSwap(int[] nums, int val) {
            int left = 0;
            int right = nums.length - 1;
            int swaps = 0;
            int comparisons = 0;

            while (left <= right) {
                comparisons++;
                if (nums[left] == val) {
                    if (left != right) {
                        nums[left] = nums[right];
                        swaps++;
                    }
                    right--;
                } else {
                    left++;
                }
            }

            return new Result(left, swaps, comparisons);
        }

        public static class Result {
            public final int newLength;
            public final int swaps;
            public final int comparisons;

            public Result(int newLength, int swaps, int comparisons) {
                this.newLength = newLength;
                this.swaps = swaps;
                this.comparisons = comparisons;
            }

            @Override
            public String toString() {
                return String.format("Length: %d, Swaps: %d, Comparisons: %d",
                        newLength, swaps, comparisons);
            }
        }
    }

    // Follow-up 4: Predicate-based removal
    public static class PredicateRemoval {

        @FunctionalInterface
        public interface RemovalPredicate {
            boolean shouldRemove(int value, int index);
        }

        public static int removeWithPredicate(int[] nums, RemovalPredicate predicate) {
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (!predicate.shouldRemove(nums[readIndex], readIndex)) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }

        @SuppressWarnings("unused")
        public static int removeNegatives(int[] nums) {
            return removeWithPredicate(nums, (value, index) -> value < 0);
        }

        @SuppressWarnings("unused")
        public static int removeAtEvenIndices(int[] nums) {
            return removeWithPredicate(nums, (value, index) -> index % 2 == 0);
        }

        @SuppressWarnings("unused")
        public static int removeGreaterThan(int[] nums, int threshold) {
            return removeWithPredicate(nums, (value, index) -> value > threshold);
        }

        @SuppressWarnings("unused")
        public static int removeDivisibleBy(int[] nums, int divisor) {
            return removeWithPredicate(nums, (value, index) -> value % divisor == 0);
        }

        // Complex predicates
        public static int removeComplexCondition(int[] nums) {
            return removeWithPredicate(nums, (value, index) -> {
                // Remove if: even value at odd index OR odd value at even index
                return (value % 2 == 0 && index % 2 == 1) ||
                        (value % 2 == 1 && index % 2 == 0);
            });
        }
    }

    // Follow-up 5: Linked list removal
    public static class LinkedListRemoval {

        public static class ListNode {
            int val;
            ListNode next;

            ListNode() {
            }

            ListNode(int val) {
                this.val = val;
            }

            ListNode(int val, ListNode next) {
                this.val = val;
                this.next = next;
            }
        }

        public static ListNode removeElements(ListNode head, int val) {
            // Create dummy node to handle edge cases
            ListNode dummy = new ListNode(0);
            dummy.next = head;

            ListNode current = dummy;

            while (current.next != null) {
                if (current.next.val == val) {
                    current.next = current.next.next; // Remove node
                } else {
                    current = current.next;
                }
            }

            return dummy.next;
        }

        public static ListNode removeElementsRecursive(ListNode head, int val) {
            if (head == null) {
                return null;
            }

            head.next = removeElementsRecursive(head.next, val);

            return head.val == val ? head.next : head;
        }

        public static int removeElementsInPlace(ListNode head, int val) {
            if (head == null) {
                return 0;
            }

            ListNode current = head;
            int count = 0;

            // Handle first node
            while (current != null && current.val == val) {
                current = current.next;
            }

            if (current != null) {
                count++;
                ListNode prev = current;
                current = current.next;

                while (current != null) {
                    if (current.val != val) {
                        count++;
                        prev = current;
                    } else {
                        prev.next = current.next;
                    }
                    current = current.next;
                }
            }

            return count;
        }

        public static ListNode arrayToList(int[] nums) {
            if (nums.length == 0) {
                return null;
            }

            ListNode head = new ListNode(nums[0]);
            ListNode current = head;

            for (int i = 1; i < nums.length; i++) {
                current.next = new ListNode(nums[i]);
                current = current.next;
            }

            return head;
        }

        public static int[] listToArray(ListNode head) {
            List<Integer> result = new ArrayList<>();

            while (head != null) {
                result.add(head.val);
                head = head.next;
            }

            return result.stream().mapToInt(Integer::intValue).toArray();
        }
    }

    // Follow-up 6: Remove duplicates while keeping one occurrence
    public static class DuplicateRemoval {

        public static int removeDuplicates(int[] nums) {
            if (nums.length <= 1) {
                return nums.length;
            }

            int writeIndex = 1;

            for (int readIndex = 1; readIndex < nums.length; readIndex++) {
                if (nums[readIndex] != nums[readIndex - 1]) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }

        public static int removeDuplicatesUnsorted(int[] nums) {
            Set<Integer> seen = new HashSet<>();
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < nums.length; readIndex++) {
                if (!seen.contains(nums[readIndex])) {
                    seen.add(nums[readIndex]);
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }

        public static int removeDuplicatesKeepTwo(int[] nums) {
            if (nums.length <= 2) {
                return nums.length;
            }

            int writeIndex = 2;

            for (int readIndex = 2; readIndex < nums.length; readIndex++) {
                if (nums[readIndex] != nums[writeIndex - 2]) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }

        public static int removeDuplicatesKeepN(int[] nums, int maxOccurrences) {
            if (nums.length <= maxOccurrences) {
                return nums.length;
            }

            int writeIndex = maxOccurrences;

            for (int readIndex = maxOccurrences; readIndex < nums.length; readIndex++) {
                if (nums[readIndex] != nums[writeIndex - maxOccurrences]) {
                    nums[writeIndex++] = nums[readIndex];
                }
            }

            return writeIndex;
        }
    }

    // Utility methods for testing
    public static void printArray(int[] nums, int length) {
        System.out.print("[");
        for (int i = 0; i < length; i++) {
            System.out.print(nums[i]);
            if (i < length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void printArray(int[] nums) {
        printArray(nums, nums.length);
    }

    public static int[] copyArray(int[] original) {
        return Arrays.copyOf(original, original.length);
    }

    public static boolean arraysEqual(int[] arr1, int len1, int[] arr2, int len2) {
        if (len1 != len2) {
            return false;
        }

        for (int i = 0; i < len1; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }

        return true;
    }

    // Performance comparison utility
    public static void compareApproaches(int[] nums, int val) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 100000;

        // Two pointers approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int[] copy = copyArray(nums);
            removeElement(copy, val);
        }
        end = System.nanoTime();
        System.out.println("Two pointers: " + (end - start) / 1_000_000 + " ms");

        // Swap approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int[] copy = copyArray(nums);
            removeElementSwap(copy, val);
        }
        end = System.nanoTime();
        System.out.println("Swap approach: " + (end - start) / 1_000_000 + " ms");

        // Count and shift
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int[] copy = copyArray(nums);
            removeElementCountShift(copy, val);
        }
        end = System.nanoTime();
        System.out.println("Count and shift: " + (end - start) / 1_000_000 + " ms");

        // ArrayList approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int[] copy = copyArray(nums);
            removeElementList(copy, val);
        }
        end = System.nanoTime();
        System.out.println("ArrayList: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] nums1 = { 3, 2, 2, 3 };
        int val1 = 3;

        int[] copy1 = copyArray(nums1);
        int newLength1 = removeElement(copy1, val1);

        System.out.println("Original: " + Arrays.toString(nums1));
        System.out.println("Remove " + val1 + ": ");
        printArray(copy1, newLength1);
        System.out.println("New length: " + newLength1);

        // Test Case 2: All approaches comparison
        System.out.println("\n=== Test Case 2: All Approaches Comparison ===");

        int[] nums2 = { 0, 1, 2, 2, 3, 0, 4, 2 };
        int val2 = 2;

        int[] copy2a = copyArray(nums2);
        int[] copy2b = copyArray(nums2);
        int[] copy2c = copyArray(nums2);
        int[] copy2d = copyArray(nums2);

        int len2a = removeElement(copy2a, val2);
        int len2b = removeElementSwap(copy2b, val2);
        int len2c = removeElementCountShift(copy2c, val2);
        int len2d = removeElementList(copy2d, val2);

        System.out.println("Original: " + Arrays.toString(nums2));
        System.out.println("Remove " + val2 + ":");
        System.out.print("Two pointers: ");
        printArray(copy2a, len2a);
        System.out.print("Swap method: ");
        printArray(copy2b, len2b);
        System.out.print("Count shift: ");
        printArray(copy2c, len2c);
        System.out.print("ArrayList: ");
        printArray(copy2d, len2d);

        System.out.println("All lengths equal: " + (len2a == len2b && len2b == len2c && len2c == len2d));

        // Test Case 3: Stable removal
        System.out.println("\n=== Test Case 3: Stable Removal ===");

        int[] nums3 = { 1, 2, 3, 2, 4, 2, 5 };
        int val3 = 2;

        int[] copy3a = copyArray(nums3);
        int[] copy3b = copyArray(nums3);

        int len3a = StableRemoval.removeElementStable(copy3a, val3);
        int len3b = StableRemoval.removeElementStableMinMoves(copy3b, val3);

        System.out.println("Original: " + Arrays.toString(nums3));
        System.out.print("Stable removal: ");
        printArray(copy3a, len3a);
        System.out.print("Min moves: ");
        printArray(copy3b, len3b);

        // Test Case 4: Multiple value removal
        System.out.println("\n=== Test Case 4: Multiple Value Removal ===");

        int[] nums4 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int[] valsToRemove = { 2, 4, 6, 8 };

        int[] copy4a = copyArray(nums4);
        int[] copy4b = copyArray(nums4);
        int[] copy4c = copyArray(nums4);

        int len4a = MultipleValueRemoval.removeMultipleValues(copy4a, valsToRemove);
        int len4b = MultipleValueRemoval.removeValuesInRange(copy4b, 3, 7);
        int len4c = MultipleValueRemoval.removeEvenNumbers(copy4c);

        System.out.println("Original: " + Arrays.toString(nums4));
        System.out.print("Remove [2,4,6,8]: ");
        printArray(copy4a, len4a);
        System.out.print("Remove range [3,7]: ");
        printArray(copy4b, len4b);
        System.out.print("Remove even numbers: ");
        printArray(copy4c, len4c);

        // Test Case 5: Minimum operations
        System.out.println("\n=== Test Case 5: Minimum Operations ===");

        int[] nums5 = { 3, 2, 2, 3, 1, 2, 4, 2 };
        int val5 = 2;

        int[] copy5a = copyArray(nums5);
        int[] copy5b = copyArray(nums5);

        MinimumOperations.Result result5a = MinimumOperations.removeElementMinOps(copy5a, val5);
        MinimumOperations.Result result5b = MinimumOperations.removeElementOptimizedSwap(copy5b, val5);

        System.out.println("Original: " + Arrays.toString(nums5));
        System.out.println("Min ops approach: " + result5a);
        System.out.println("Optimized swap: " + result5b);

        // Test Case 6: Predicate-based removal
        System.out.println("\n=== Test Case 6: Predicate-based Removal ===");

        int[] nums6 = { -3, -1, 0, 1, 2, 3, 4, 5 };

        int[] copy6a = copyArray(nums6);
        int[] copy6b = copyArray(nums6);
        int[] copy6c = copyArray(nums6);
        int[] copy6d = copyArray(nums6);

        int len6a = PredicateRemoval.removeNegatives(copy6a);
        int len6b = PredicateRemoval.removeAtEvenIndices(copy6b);
        int len6c = PredicateRemoval.removeGreaterThan(copy6c, 3);
        int len6d = PredicateRemoval.removeComplexCondition(copy6d);

        System.out.println("Original: " + Arrays.toString(nums6));
        System.out.print("Remove negatives: ");
        printArray(copy6a, len6a);
        System.out.print("Remove at even indices: ");
        printArray(copy6b, len6b);
        System.out.print("Remove > 3: ");
        printArray(copy6c, len6c);
        System.out.print("Remove complex condition: ");
        printArray(copy6d, len6d);

        // Test Case 7: Linked list removal
        System.out.println("\n=== Test Case 7: Linked List Removal ===");

        int[] listArray = { 1, 2, 6, 3, 4, 5, 6 };
        LinkedListRemoval.ListNode head = LinkedListRemoval.arrayToList(listArray);

        System.out.println("Original list: " + Arrays.toString(listArray));

        LinkedListRemoval.ListNode newHead = LinkedListRemoval.removeElements(head, 6);
        int[] resultArray = LinkedListRemoval.listToArray(newHead);

        System.out.println("After removing 6: " + Arrays.toString(resultArray));

        // Test Case 8: Duplicate removal
        System.out.println("\n=== Test Case 8: Duplicate Removal ===");

        int[] sorted = { 1, 1, 2, 2, 2, 3, 4, 4, 5 };
        int[] unsorted = { 4, 2, 1, 3, 2, 4, 1, 5, 3 };

        int[] copy8a = copyArray(sorted);
        int[] copy8b = copyArray(unsorted);
        int[] copy8c = copyArray(sorted);

        int len8a = DuplicateRemoval.removeDuplicates(copy8a);
        int len8b = DuplicateRemoval.removeDuplicatesUnsorted(copy8b);
        int len8c = DuplicateRemoval.removeDuplicatesKeepTwo(copy8c);

        System.out.println("Sorted original: " + Arrays.toString(sorted));
        System.out.print("Remove duplicates: ");
        printArray(copy8a, len8a);

        System.out.println("Unsorted original: " + Arrays.toString(unsorted));
        System.out.print("Remove duplicates: ");
        printArray(copy8b, len8b);

        System.out.println("Keep up to 2 occurrences: ");
        printArray(copy8c, len8c);

        // Test Case 9: Edge cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        // Empty array
        int[] empty = {};
        int emptyResult = removeElement(empty, 1);
        System.out.println("Empty array result: " + emptyResult);

        // Single element - remove
        int[] single1 = { 1 };
        int single1Result = removeElement(copyArray(single1), 1);
        System.out.println("Single element (remove): " + single1Result);

        // Single element - keep
        int[] single2 = { 1 };
        int single2Result = removeElement(copyArray(single2), 2);
        System.out.println("Single element (keep): " + single2Result);

        // All elements same - remove all
        int[] allSame1 = { 3, 3, 3, 3 };
        int allSame1Result = removeElement(copyArray(allSame1), 3);
        System.out.println("All same (remove all): " + allSame1Result);

        // All elements same - keep all
        int[] allSame2 = { 3, 3, 3, 3 };
        int allSame2Result = removeElement(copyArray(allSame2), 5);
        System.out.println("All same (keep all): " + allSame2Result);

        // Test Case 10: Performance comparison
        System.out.println("\n=== Test Case 10: Performance Comparison ===");

        int[] largeArray = new int[10000];
        Random random = new Random(42);
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = random.nextInt(50);
        }

        compareApproaches(largeArray, 25);

        // Test Case 11: Stress testing
        System.out.println("\n=== Test Case 11: Stress Testing ===");

        // Test with large array
        int[] stressArray = new int[100000];
        for (int i = 0; i < stressArray.length; i++) {
            stressArray[i] = i % 100;
        }

        long start = System.nanoTime();
        int stressResult = removeElement(copyArray(stressArray), 50);
        long end = System.nanoTime();

        System.out.println("Stress test (100K elements): " + stressResult +
                " remaining, Time: " + (end - start) / 1_000_000 + " ms");

        // Test worst case - remove every other element
        int[] worstCase = new int[10000];
        for (int i = 0; i < worstCase.length; i++) {
            worstCase[i] = i % 2; // Alternating 0s and 1s
        }

        start = System.nanoTime();
        int worstResult = removeElement(copyArray(worstCase), 0);
        end = System.nanoTime();

        System.out.println("Worst case (remove every other): " + worstResult +
                " remaining, Time: " + (end - start) / 1_000_000 + " ms");

        System.out.println("\nRemove Element testing completed successfully!");
    }
}
