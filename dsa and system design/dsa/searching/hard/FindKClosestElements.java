package searching.hard;

import java.util.*;

/**
 * LeetCode 658: Find K Closest Elements
 * https://leetcode.com/problems/find-k-closest-elements/
 * 
 * Companies: Google, Facebook, Amazon
 * Frequency: High
 *
 * Description: Given a sorted array, find the k closest elements to a target
 * value.
 *
 * Constraints:
 * - 1 <= k <= arr.length
 * - 1 <= arr.length <= 10^4
 * - Absolute value of elements and x is at most 10^4
 * 
 * Follow-up Questions:
 * 1. Can you handle duplicates efficiently?
 * 2. What if the array is not sorted?
 * 3. Can you find k closest pairs?
 */
public class FindKClosestElements {

    // Approach 1: Binary search + two pointers - O(log n + k) time, O(1) space
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        if (arr == null || arr.length == 0 || k <= 0)
            return new ArrayList<>();

        int left = 0, right = arr.length - k;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (x - arr[mid] > arr[mid + k] - x) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = left; i < left + k; i++) {
            result.add(arr[i]);
        }
        return result;
    }

    // Approach 2: Binary search + expand around center - O(log n + k) time, O(1)
    // space
    public List<Integer> findClosestElementsExpand(int[] arr, int k, int x) {
        if (arr == null || arr.length == 0 || k <= 0)
            return new ArrayList<>();

        int pos = findInsertPosition(arr, x);
        int left = pos - 1, right = pos;

        while (k-- > 0) {
            if (left < 0) {
                right++;
            } else if (right >= arr.length) {
                left--;
            } else if (x - arr[left] <= arr[right] - x) {
                left--;
            } else {
                right++;
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = left + 1; i < right; i++) {
            result.add(arr[i]);
        }
        return result;
    }

    private int findInsertPosition(int[] arr, int x) {
        int left = 0, right = arr.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] < x) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    // Approach 3: Heap-based solution - O(n log k) time, O(k) space
    public List<Integer> findClosestElementsHeap(int[] arr, int k, int x) {
        PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> {
            int diff = Integer.compare(Math.abs(b - x), Math.abs(a - x));
            return diff != 0 ? diff : Integer.compare(b, a);
        });

        for (int num : arr) {
            heap.offer(num);
            if (heap.size() > k) {
                heap.poll();
            }
        }

        List<Integer> result = new ArrayList<>(heap);
        Collections.sort(result);
        return result;
    }

    // Follow-up 1: Handle duplicates efficiently
    public List<Integer> findClosestWithDuplicates(int[] arr, int k, int x) {
        // Find all occurrences of x first
        int firstX = findFirst(arr, x);
        int lastX = findLast(arr, x);

        if (firstX != -1) {
            int xCount = lastX - firstX + 1;
            if (xCount >= k) {
                List<Integer> result = new ArrayList<>();
                for (int i = 0; i < k; i++) {
                    result.add(x);
                }
                return result;
            }

            // Expand around all x's
            int left = firstX - 1, right = lastX + 1;
            int remaining = k - xCount;

            while (remaining > 0) {
                if (left < 0) {
                    right++;
                } else if (right >= arr.length) {
                    left--;
                } else if (x - arr[left] <= arr[right] - x) {
                    left--;
                } else {
                    right++;
                }
                remaining--;
            }

            List<Integer> result = new ArrayList<>();
            for (int i = left + 1; i < right; i++) {
                result.add(arr[i]);
            }
            return result;
        }

        return findClosestElements(arr, k, x);
    }

    private int findFirst(int[] arr, int target) {
        int left = 0, right = arr.length - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) {
                result = mid;
                right = mid - 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    private int findLast(int[] arr, int target) {
        int left = 0, right = arr.length - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) {
                result = mid;
                left = mid + 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    // Follow-up 2: Unsorted array
    public List<Integer> findClosestUnsorted(int[] arr, int k, int x) {
        PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> {
            int diff = Integer.compare(Math.abs(a - x), Math.abs(b - x));
            return diff != 0 ? diff : Integer.compare(a, b);
        });

        for (int num : arr) {
            heap.offer(num);
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < k && !heap.isEmpty(); i++) {
            result.add(heap.poll());
        }
        return result;
    }

    // Follow-up 3: Find k closest pairs
    public List<int[]> findKClosestPairs(int[] arr1, int[] arr2, int k) {
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[0] + a[1], b[0] + b[1]));

        Set<String> visited = new HashSet<>();
        heap.offer(new int[] { arr1[0], arr2[0], 0, 0 });
        visited.add("0,0");

        List<int[]> result = new ArrayList<>();

        while (k-- > 0 && !heap.isEmpty()) {
            int[] curr = heap.poll();
            result.add(new int[] { curr[0], curr[1] });

            int i = curr[2], j = curr[3];

            if (i + 1 < arr1.length && !visited.contains((i + 1) + "," + j)) {
                heap.offer(new int[] { arr1[i + 1], arr2[j], i + 1, j });
                visited.add((i + 1) + "," + j);
            }

            if (j + 1 < arr2.length && !visited.contains(i + "," + (j + 1))) {
                heap.offer(new int[] { arr1[i], arr2[j + 1], i, j + 1 });
                visited.add(i + "," + (j + 1));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindKClosestElements solution = new FindKClosestElements();

        // Test case 1: Basic case
        int[] arr1 = { 1, 2, 3, 4, 5 };
        System.out.println("Test 1 - Basic case (k=4, x=3):");
        System.out.println("Expected: [1,2,3,4], Got: " + solution.findClosestElements(arr1, 4, 3));
        System.out.println("Expand approach: " + solution.findClosestElementsExpand(arr1, 4, 3));
        System.out.println("Heap approach: " + solution.findClosestElementsHeap(arr1, 4, 3));

        // Test case 2: Target not in array
        int[] arr2 = { 1, 2, 3, 4, 5 };
        System.out.println("\nTest 2 - Target not in array (k=4, x=-1):");
        System.out.println("Expected: [1,2,3,4], Got: " + solution.findClosestElements(arr2, 4, -1));

        // Test case 3: Target larger than all elements
        System.out.println("\nTest 3 - Target larger than all (k=3, x=10):");
        System.out.println("Expected: [3,4,5], Got: " + solution.findClosestElements(arr2, 3, 10));

        // Test case 4: Duplicates
        int[] arr3 = { 1, 1, 1, 10, 10, 10 };
        System.out.println("\nTest 4 - Duplicates (k=1, x=9):");
        System.out.println("Expected: [10], Got: " + solution.findClosestElements(arr3, 1, 9));

        // Test case 5: All elements same distance
        int[] arr4 = { 1, 3 };
        System.out.println("\nTest 5 - Same distance (k=1, x=2):");
        System.out.println("Expected: [1], Got: " + solution.findClosestElements(arr4, 1, 2));

        // Edge case: k equals array length
        System.out.println("\nEdge case - k equals array length:");
        System.out.println("Expected: [1,2,3,4,5], Got: " + solution.findClosestElements(arr1, 5, 3));

        // Edge case: Single element
        int[] arr5 = { 42 };
        System.out.println("\nEdge case - Single element:");
        System.out.println("Expected: [42], Got: " + solution.findClosestElements(arr5, 1, 50));

        // Follow-up 1: Handle duplicates
        int[] arr6 = { 0, 0, 1, 2, 3, 3, 4, 7, 7, 8 };
        System.out.println("\nFollow-up 1 - Handle duplicates (k=3, x=5):");
        System.out.println("Got: " + solution.findClosestWithDuplicates(arr6, 3, 5));

        // Follow-up 2: Unsorted array
        int[] arr7 = { 3, 1, 4, 1, 5, 9, 2, 6 };
        System.out.println("\nFollow-up 2 - Unsorted array (k=4, x=5):");
        System.out.println("Got: " + solution.findClosestUnsorted(arr7, 4, 5));

        // Follow-up 3: K closest pairs
        int[] arr8 = { 1, 7, 11 };
        int[] arr9 = { 2, 4, 6 };
        System.out.println("\nFollow-up 3 - K closest pairs (k=3):");
        List<int[]> pairs = solution.findKClosestPairs(arr8, arr9, 3);
        for (int[] pair : pairs) {
            System.out.print("[" + pair[0] + "," + pair[1] + "] ");
        }
        System.out.println();

        // Performance test
        int[] largeArr = new int[10000];
        for (int i = 0; i < largeArr.length; i++) {
            largeArr[i] = i;
        }
        long startTime = System.currentTimeMillis();
        solution.findClosestElements(largeArr, 1000, 5000);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (10k elements, k=1000): " + (endTime - startTime) + "ms");
    }
}
