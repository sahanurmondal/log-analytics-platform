package sorting.hard;

/**
 * GeeksforGeeks: Count Inversions
 * https://www.geeksforgeeks.org/counting-inversions/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Count the number of inversions in an array.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 *
 * Follow-ups:
 * 1. Can you return the list of inversion pairs?
 * 2. Can you count inversions in a subarray?
 * 3. Can you count inversions online?
 */
public class CountInversions {
    public int countInversions(int[] arr) {
        return mergeSort(arr, 0, arr.length - 1, new int[arr.length]);
    }

    private int mergeSort(int[] arr, int left, int right, int[] temp) {
        if (left >= right)
            return 0;
        int mid = left + (right - left) / 2;
        int inv = mergeSort(arr, left, mid, temp) + mergeSort(arr, mid + 1, right, temp);
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j])
                temp[k++] = arr[i++];
            else {
                temp[k++] = arr[j++];
                inv += mid - i + 1;
            }
        }
        while (i <= mid)
            temp[k++] = arr[i++];
        while (j <= right)
            temp[k++] = arr[j++];
        for (i = left; i <= right; i++)
            arr[i] = temp[i];
        return inv;
    }

    // Follow-up 1: Return list of inversion pairs
    public java.util.List<int[]> inversionPairs(int[] arr) {
        java.util.List<int[]> res = new java.util.ArrayList<>();
        for (int i = 0; i < arr.length; i++)
            for (int j = i + 1; j < arr.length; j++)
                if (arr[i] > arr[j])
                    res.add(new int[] { i, j });
        return res;
    }

    // Follow-up 2: Count inversions in subarray [l, r]
    public int countInversionsSubarray(int[] arr, int l, int r) {
        int[] sub = java.util.Arrays.copyOfRange(arr, l, r + 1);
        return countInversions(sub);
    }

    // Follow-up 3: Count inversions online (using BIT)
    public int countInversionsOnline(int[] arr) {
        int n = arr.length;
        int[] sorted = arr.clone();
        java.util.Arrays.sort(sorted);
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < n; i++)
            map.put(sorted[i], i + 1);
        int[] bit = new int[n + 2];
        int inv = 0;
        for (int i = n - 1; i >= 0; i--) {
            int idx = map.get(arr[i]);
            for (int j = idx - 1; j > 0; j -= j & -j)
                inv += bit[j];
            for (int j = idx; j <= n; j += j & -j)
                bit[j]++;
        }
        return inv;
    }

    public static void main(String[] args) {
        CountInversions solution = new CountInversions();
        int[] arr = { 2, 4, 1, 3, 5 };
        System.out.println(solution.countInversions(arr.clone())); // 3
        System.out.println(solution.inversionPairs(arr)); // [[0,2],[1,2],[1,3]]
        System.out.println(solution.countInversionsSubarray(arr, 1, 3)); // 2
        System.out.println(solution.countInversionsOnline(arr.clone())); // 3
    }
}
