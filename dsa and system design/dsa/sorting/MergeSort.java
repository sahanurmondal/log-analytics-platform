package sorting;

import java.util.*;

/**
 * Merge Sort Implementation
 * Stable, divide-and-conquer sorting algorithm
 * Time: O(n log n), Space: O(n)
 */
public class MergeSort {
    
    public void mergeSort(int[] arr) {
        if (arr.length <= 1) return;
        mergeSortHelper(arr, 0, arr.length - 1);
    }
    
    private void mergeSortHelper(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSortHelper(arr, left, mid);
            mergeSortHelper(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }
    
    private void merge(int[] arr, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        
        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];
        
        System.arraycopy(temp, 0, arr, left, temp.length);
    }
    
    // Bottom-up merge sort (iterative)
    public void mergeSortIterative(int[] arr) {
        int n = arr.length;
        
        for (int size = 1; size < n; size *= 2) {
            for (int left = 0; left < n - size; left += 2 * size) {
                int mid = left + size - 1;
                int right = Math.min(left + 2 * size - 1, n - 1);
                merge(arr, left, mid, right);
            }
        }
    }
    
    // Quick Sort for comparison
    public void quickSort(int[] arr) {
        quickSortHelper(arr, 0, arr.length - 1);
    }
    
    private void quickSortHelper(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSortHelper(arr, low, pi - 1);
            quickSortHelper(arr, pi + 1, high);
        }
    }
    
    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        
        swap(arr, i + 1, high);
        return i + 1;
    }
    
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    // Heap Sort
    public void heapSort(int[] arr) {
        int n = arr.length;
        
        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }
        
        // Extract elements from heap
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            heapify(arr, i, 0);
        }
    }
    
    private void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < n && arr[left] > arr[largest]) largest = left;
        if (right < n && arr[right] > arr[largest]) largest = right;
        
        if (largest != i) {
            swap(arr, i, largest);
            heapify(arr, n, largest);
        }
    }
    
    public static void main(String[] args) {
        MergeSort sorter = new MergeSort();
        
        // Test merge sort
        int[] arr1 = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("Original: " + Arrays.toString(arr1));
        sorter.mergeSort(arr1.clone());
        System.out.println("Merge Sort: " + Arrays.toString(arr1));
        
        // Test edge cases
        int[] arr2 = {};
        sorter.mergeSort(arr2);
        System.out.println("Empty array: " + Arrays.toString(arr2));
        
        int[] arr3 = {5};
        sorter.mergeSort(arr3);
        System.out.println("Single element: " + Arrays.toString(arr3));
        
        int[] arr4 = {3, 3, 3, 3};
        sorter.mergeSort(arr4);
        System.out.println("All same: " + Arrays.toString(arr4));
        
        int[] arr5 = {5, 4, 3, 2, 1};
        sorter.mergeSort(arr5);
        System.out.println("Reverse sorted: " + Arrays.toString(arr5));
        
        // Performance comparison
        int[] large = new int[100000];
        Random rand = new Random();
        for (int i = 0; i < large.length; i++) {
            large[i] = rand.nextInt(10000);
        }
        
        long start = System.currentTimeMillis();
        sorter.mergeSort(large.clone());
        long end = System.currentTimeMillis();
        System.out.println("Merge Sort (100k): " + (end - start) + "ms");
        
        start = System.currentTimeMillis();
        sorter.quickSort(large.clone());
        end = System.currentTimeMillis();
        System.out.println("Quick Sort (100k): " + (end - start) + "ms");
        
        start = System.currentTimeMillis();
        sorter.heapSort(large.clone());
        end = System.currentTimeMillis();
        System.out.println("Heap Sort (100k): " + (end - start) + "ms");
        
        start = System.currentTimeMillis();
        Arrays.sort(large.clone());
        end = System.currentTimeMillis();
        System.out.println("Arrays.sort (100k): " + (end - start) + "ms");
    }
}
