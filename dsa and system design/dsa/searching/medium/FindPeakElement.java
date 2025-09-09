package searching.medium;

/**
 * LeetCode 162: Find Peak Element
 * https://leetcode.com/problems/find-peak-element/
 *
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Find a peak element in an array.
 *
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - -2^31 <= nums[i] <= 2^31-1
 *
 * Follow-ups:
 * 1. Can you find all peak elements?
 * 2. Can you handle plateaus?
 * 3. Can you find the leftmost/rightmost peak?
 */
public class FindPeakElement {
    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid + 1])
                right = mid;
            else
                left = mid + 1;
        }
        return left;
    }

    // Follow-up 1: Find all peak elements
    public java.util.List<Integer> findAllPeaks(int[] nums) {
        java.util.List<Integer> peaks = new java.util.ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if ((i == 0 || nums[i] > nums[i - 1]) &&
                    (i == nums.length - 1 || nums[i] > nums[i + 1])) {
                peaks.add(i);
            }
        }
        return peaks;
    }

    // Follow-up 2: Handle plateaus (return all plateau peaks)
    public java.util.List<Integer> findPlateauPeaks(int[] nums) {
        java.util.List<Integer> peaks = new java.util.ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            boolean left = (i == 0 || nums[i] >= nums[i - 1]);
            boolean right = (i == nums.length - 1 || nums[i] >= nums[i + 1]);
            if (left && right)
                peaks.add(i);
        }
        return peaks;
    }

    // Follow-up 3: Leftmost peak
    public int findLeftmostPeak(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            if ((i == 0 || nums[i] > nums[i - 1]) &&
                    (i == nums.length - 1 || nums[i] > nums[i + 1])) {
                return i;
            }
        }
        return -1;
    }

    // Follow-up 3: Rightmost peak
    public int findRightmostPeak(int[] nums) {
        for (int i = nums.length - 1; i >= 0; i--) {
            if ((i == 0 || nums[i] > nums[i - 1]) &&
                    (i == nums.length - 1 || nums[i] > nums[i + 1])) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        FindPeakElement solution = new FindPeakElement();
        // Basic case
        int[] nums1 = { 1, 2, 3, 1 };
        System.out.println("Basic: " + solution.findPeakElement(nums1)); // 2
        // Edge: Single element
        int[] nums2 = { 10 };
        System.out.println("Single element: " + solution.findPeakElement(nums2)); // 0
        // Edge: All increasing
        int[] nums3 = { 1, 2, 3, 4, 5 };
        System.out.println("All increasing: " + solution.findPeakElement(nums3)); // 4
        // Edge: All decreasing
        int[] nums4 = { 5, 4, 3, 2, 1 };
        System.out.println("All decreasing: " + solution.findPeakElement(nums4)); // 0
        // Follow-up 1: All peaks
        System.out.println("All peaks: " + solution.findAllPeaks(nums1)); // [2]
        // Follow-up 2: Plateau peaks
        int[] nums5 = { 1, 2, 2, 2, 1 };
        System.out.println("Plateau peaks: " + solution.findPlateauPeaks(nums5)); // [1,2,3]
        // Follow-up 3: Leftmost peak
        System.out.println("Leftmost peak: " + solution.findLeftmostPeak(nums1)); // 2
        // Follow-up 3: Rightmost peak
        System.out.println("Rightmost peak: " + solution.findRightmostPeak(nums1)); // 2
    }
}
