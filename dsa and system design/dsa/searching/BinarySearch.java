package searching;

/**
 * LeetCode 704: Binary Search
 * Search target in sorted array
 * Time: O(log n), Space: O(1)
 */
public class BinarySearch {
    
    // Standard binary search
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return -1;
    }
    
    // Recursive binary search
    public int searchRecursive(int[] nums, int target) {
        return binarySearchHelper(nums, target, 0, nums.length - 1);
    }
    
    private int binarySearchHelper(int[] nums, int target, int left, int right) {
        if (left > right) return -1;
        
        int mid = left + (right - left) / 2;
        
        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            return binarySearchHelper(nums, target, mid + 1, right);
        } else {
            return binarySearchHelper(nums, target, left, mid - 1);
        }
    }
    
    // Find first occurrence
    public int searchFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                result = mid;
                right = mid - 1; // Continue searching left
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    // Find last occurrence
    public int searchLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                result = mid;
                left = mid + 1; // Continue searching right
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    // Search in rotated sorted array
    public int searchRotated(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) return mid;
            
            // Left half is sorted
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            // Right half is sorted
            else {
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        
        return -1;
    }
    
    // Find peak element
    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] > nums[mid + 1]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        
        return left;
    }
    
    // Search insert position
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return left;
    }
    
    public static void main(String[] args) {
        BinarySearch solution = new BinarySearch();
        
        // Basic binary search
        int[] nums1 = {-1,0,3,5,9,12};
        System.out.println("Search 9: " + solution.search(nums1, 9)); // 4
        System.out.println("Search 2: " + solution.search(nums1, 2)); // -1
        
        // Edge cases
        System.out.println("Single element [5], target 5: " + solution.search(new int[]{5}, 5)); // 0
        System.out.println("Single element [5], target -5: " + solution.search(new int[]{5}, -5)); // -1
        
        // First and last occurrence
        int[] nums2 = {5,7,7,8,8,10};
        System.out.println("First 8: " + solution.searchFirst(nums2, 8)); // 3
        System.out.println("Last 8: " + solution.searchLast(nums2, 8)); // 4
        System.out.println("First 6: " + solution.searchFirst(nums2, 6)); // -1
        
        // Rotated array
        int[] nums3 = {4,5,6,7,0,1,2};
        System.out.println("Rotated search 0: " + solution.searchRotated(nums3, 0)); // 4
        System.out.println("Rotated search 3: " + solution.searchRotated(nums3, 3)); // -1
        
        // Peak element
        int[] nums4 = {1,2,3,1};
        System.out.println("Peak element: " + solution.findPeakElement(nums4)); // 2
        
        // Insert position
        int[] nums5 = {1,3,5,6};
        System.out.println("Insert 5: " + solution.searchInsert(nums5, 5)); // 2
        System.out.println("Insert 2: " + solution.searchInsert(nums5, 2)); // 1
        System.out.println("Insert 7: " + solution.searchInsert(nums5, 7)); // 4
        
        // Performance test
        int[] large = new int[1000000];
        for (int i = 0; i < large.length; i++) {
            large[i] = i * 2;
        }
        
        long start = System.currentTimeMillis();
        int result = solution.search(large, 999998);
        long end = System.currentTimeMillis();
        System.out.println("Binary search in 1M array: " + result + " in " + (end - start) + "ms");
        
        // Compare with linear search
        start = System.currentTimeMillis();
        int linearResult = -1;
        for (int i = 0; i < large.length; i++) {
            if (large[i] == 999998) {
                linearResult = i;
                break;
            }
        }
        end = System.currentTimeMillis();
        System.out.println("Linear search in 1M array: " + linearResult + " in " + (end - start) + "ms");
    }
}
