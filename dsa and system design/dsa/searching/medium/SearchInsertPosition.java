package searching.medium;

/**
 * LeetCode 35: Search Insert Position
 * https://leetcode.com/problems/search-insert-position/
 *
 * Description:
 * Given a sorted array of distinct integers and a target value, return the
 * index if the target is found. If not, return the index where it would be if
 * it were inserted in order.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^4 <= nums[i], target <= 10^4
 *
 * Follow-ups:
 * 1. Can you handle duplicates?
 * 2. Can you find the closest value?
 * 3. Can you insert and keep sorted?
 */
public class SearchInsertPosition {
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return left;
    }

    // Follow-up 1: Handle duplicates (insert after last duplicate)
    public int searchInsertAfterDuplicates(int[] nums, int target) {
        int left = 0, right = nums.length - 1, res = nums.length;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) left = mid + 1;
            else {
                res = mid;
                right = mid - 1;
            }
        }
        return res;
    }

    // Follow-up 2: Find closest value
    public int searchClosest(int[] nums, int target) {
        int left = 0, right = nums.length - 1, closest = -1, minDiff = Integer.MAX_VALUE;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int diff = Math.abs(nums[mid] - target);
            if (diff < minDiff) {
                minDiff = diff;
                closest = mid;
            }
            if (nums[mid] == target) break;
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return closest;
    }

    // Follow-up 3: Insert and keep sorted
    public int[] insertAndKeepSorted(int[] nums, int target) {
        int idx = searchInsert(nums, target);
        int[] res = new int[nums.length + 1];
        System.arraycopy(nums, 0, res, 0, idx);
        res[idx] = target;
        System.arraycopy(nums, idx, res, idx + 1, nums.length - idx);
        return res;
    }

    public static void main(String[] args) {
        SearchInsertPosition solution = new SearchInsertPosition();
        int[] nums1 = {1,3,5,6};
        System.out.println("Basic: " + solution.searchInsert(nums1, 5)); // 2
        System.out.println("Not found: " + solution.searchInsert(nums1, 2)); // 1
        System.out.println("Insert at end: " + solution.searchInsert(nums1, 7)); // 4
        System.out.println("Insert at start: " + solution.searchInsert(nums1, 0)); // 0
        int[] nums2 = {1,2,2,2,3,4};
        System.out.println("After duplicates: " + solution.searchInsertAfterDuplicates(nums2, 2)); // 4
        System.out.println("Closest to 4: " + solution.searchClosest(nums1, 4)); // 2
        System.out.println("Insert and keep sorted: " + java.util.Arrays.toString(solution.insertAndKeepSorted(nums1, 4))); // [1,3,4,5,6]
    }
}
