package design.hard;

import java.util.*;

/**
 * LeetCode 2502: Design Memory Allocator
 * https://leetcode.com/problems/design-memory-allocator/
 *
 * Description: You are given an integer n representing the size of a 0-indexed
 * memory array.
 * All memory units are initially free.
 * 
 * Constraints:
 * - 1 <= n, size, mID <= 1000
 * - At most 1000 calls will be made to allocate and free
 *
 * Follow-up:
 * - Can you optimize for large memory sizes?
 * 
 * Time Complexity: O(n) for allocate/free
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Microsoft
 */
public class DesignMemoryAllocator {

    private int[] memory;
    private int size;

    public DesignMemoryAllocator(int n) {
        this.size = n;
        this.memory = new int[n];
    }

    public int allocate(int size, int mID) {
        for (int i = 0; i <= this.size - size; i++) {
            if (canAllocate(i, size)) {
                // Allocate memory
                for (int j = i; j < i + size; j++) {
                    memory[j] = mID;
                }
                return i;
            }
        }
        return -1;
    }

    private boolean canAllocate(int start, int size) {
        for (int i = start; i < start + size; i++) {
            if (memory[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public int free(int mID) {
        int freed = 0;
        for (int i = 0; i < size; i++) {
            if (memory[i] == mID) {
                memory[i] = 0;
                freed++;
            }
        }
        return freed;
    }

    // Alternative optimized implementation using segments
    static class MemoryAllocatorOptimized {
        class Segment {
            int start, end, mID;

            Segment(int start, int end, int mID) {
                this.start = start;
                this.end = end;
                this.mID = mID;
            }
        }

        private int totalSize;
        private TreeMap<Integer, Segment> allocated; // start -> segment

        public MemoryAllocatorOptimized(int n) {
            this.totalSize = n;
            this.allocated = new TreeMap<>();
        }

        public int allocate(int size, int mID) {
            int start = 0;

            for (Map.Entry<Integer, Segment> entry : allocated.entrySet()) {
                Segment seg = entry.getValue();
                if (seg.start - start >= size) {
                    // Found free space
                    allocated.put(start, new Segment(start, start + size - 1, mID));
                    return start;
                }
                start = seg.end + 1;
            }

            // Check space after last segment
            if (totalSize - start >= size) {
                allocated.put(start, new Segment(start, start + size - 1, mID));
                return start;
            }

            return -1;
        }

        public int free(int mID) {
            int freed = 0;
            Iterator<Map.Entry<Integer, Segment>> it = allocated.entrySet().iterator();

            while (it.hasNext()) {
                Segment seg = it.next().getValue();
                if (seg.mID == mID) {
                    freed += seg.end - seg.start + 1;
                    it.remove();
                }
            }

            return freed;
        }
    }

    public static void main(String[] args) {
        DesignMemoryAllocator allocator = new DesignMemoryAllocator(10);
        System.out.println(allocator.allocate(1, 1)); // Expected: 0
        System.out.println(allocator.allocate(1, 2)); // Expected: 1
        System.out.println(allocator.allocate(1, 3)); // Expected: 2
        System.out.println(allocator.free(2)); // Expected: 1
        System.out.println(allocator.allocate(3, 4)); // Expected: 1
        System.out.println(allocator.allocate(1, 1)); // Expected: 4
        System.out.println(allocator.allocate(1, 1)); // Expected: 5
        System.out.println(allocator.free(1)); // Expected: 3
    }
}
