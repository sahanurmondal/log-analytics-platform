package intervals.medium;

import java.util.*;

/**
 * LeetCode 729: My Calendar I
 * https://leetcode.com/problems/my-calendar-i/
 * 
 * Companies: Amazon, Google, Microsoft, Meta, Apple, Adobe
 * Frequency: High (Asked in 500+ interviews)
 *
 * Description:
 * You are implementing a program to use as your calendar. We can add a new
 * event
 * if adding the event will not cause a double booking.
 *
 * A double booking happens when two events have some non-empty intersection
 * (i.e., some moment is common to both events.).
 *
 * The event can be represented as a pair of integers start and end that
 * represents
 * a booking on the half open interval [start, end), the range of real numbers x
 * such that start <= x < end.
 * 
 * Implement the MyCalendar class:
 * - MyCalendar() Initializes the calendar object.
 * - boolean book(int start, int end) Returns true if the event can be added to
 * the
 * calendar successfully without causing a double booking. Otherwise, return
 * false
 * and do not add the event to the calendar.
 *
 * Constraints:
 * - 0 <= start < end <= 10^9
 * - At most 1000 calls will be made to book.
 * 
 * Follow-up Questions:
 * 1. Can you implement My Calendar II (allow at most 2 overlaps)?
 * 2. What if we need to support range updates?
 * 3. How to optimize for large number of bookings?
 * 4. Can you implement with different data structures?
 * 5. How to handle recurring events?
 */
public class MyCalendarI {

    // Approach 1: Simple List - O(n) time, O(n) space
    static class MyCalendarList {
        private List<int[]> bookings;

        public MyCalendarList() {
            this.bookings = new ArrayList<>();
        }

        public boolean book(int start, int end) {
            for (int[] booking : bookings) {
                // Check for overlap: max(start1, start2) < min(end1, end2)
                if (Math.max(start, booking[0]) < Math.min(end, booking[1])) {
                    return false;
                }
            }
            bookings.add(new int[] { start, end });
            return true;
        }

        public List<int[]> getBookings() {
            return new ArrayList<>(bookings);
        }
    }

    // Approach 2: TreeMap - O(log n) time, O(n) space
    static class MyCalendarTreeMap {
        private TreeMap<Integer, Integer> bookings;

        public MyCalendarTreeMap() {
            this.bookings = new TreeMap<>();
        }

        public boolean book(int start, int end) {
            Integer prevStart = bookings.floorKey(start);
            Integer nextStart = bookings.ceilingKey(start);

            // Check overlap with previous booking
            if (prevStart != null && bookings.get(prevStart) > start) {
                return false;
            }

            // Check overlap with next booking
            if (nextStart != null && nextStart < end) {
                return false;
            }

            bookings.put(start, end);
            return true;
        }

        public TreeMap<Integer, Integer> getBookings() {
            return new TreeMap<>(bookings);
        }
    }

    // Approach 3: Balanced BST (custom implementation) - O(log n) time, O(n) space
    static class MyCalendarBST {
        private TreeNode root;

        class TreeNode {
            int start, end;
            TreeNode left, right;

            TreeNode(int start, int end) {
                this.start = start;
                this.end = end;
            }
        }

        public MyCalendarBST() {
            this.root = null;
        }

        public boolean book(int start, int end) {
            if (root == null) {
                root = new TreeNode(start, end);
                return true;
            }
            return insert(root, start, end);
        }

        private boolean insert(TreeNode node, int start, int end) {
            // Check for overlap
            if (Math.max(start, node.start) < Math.min(end, node.end)) {
                return false;
            }

            if (start >= node.end) {
                // Insert to the right
                if (node.right == null) {
                    node.right = new TreeNode(start, end);
                    return true;
                }
                return insert(node.right, start, end);
            } else {
                // Insert to the left
                if (node.left == null) {
                    node.left = new TreeNode(start, end);
                    return true;
                }
                return insert(node.left, start, end);
            }
        }

        public List<int[]> getBookings() {
            List<int[]> result = new ArrayList<>();
            inorderTraversal(root, result);
            return result;
        }

        private void inorderTraversal(TreeNode node, List<int[]> result) {
            if (node != null) {
                inorderTraversal(node.left, result);
                result.add(new int[] { node.start, node.end });
                inorderTraversal(node.right, result);
            }
        }
    }

    // Approach 4: Segment Tree - O(log n) time, O(n) space
    static class MyCalendarSegmentTree {
        private Map<Integer, Integer> tree;
        private Set<Integer> coords;

        public MyCalendarSegmentTree() {
            this.tree = new HashMap<>();
            this.coords = new HashSet<>();
        }

        public boolean book(int start, int end) {
            coords.add(start);
            coords.add(end);

            List<Integer> sortedCoords = new ArrayList<>(coords);
            Collections.sort(sortedCoords);

            int startIdx = Collections.binarySearch(sortedCoords, start);
            int endIdx = Collections.binarySearch(sortedCoords, end);

            // Check if any segment in [startIdx, endIdx) is already booked
            for (int i = startIdx; i < endIdx; i++) {
                if (tree.getOrDefault(i, 0) > 0) {
                    return false;
                }
            }

            // Book the segments
            for (int i = startIdx; i < endIdx; i++) {
                tree.put(i, tree.getOrDefault(i, 0) + 1);
            }

            return true;
        }
    }

    // Follow-up 1: My Calendar II (allow at most 2 overlaps)
    static class MyCalendarTwo {
        private List<int[]> bookings;
        private List<int[]> overlaps;

        public MyCalendarTwo() {
            this.bookings = new ArrayList<>();
            this.overlaps = new ArrayList<>();
        }

        public boolean book(int start, int end) {
            // Check if this booking would create a triple overlap
            for (int[] overlap : overlaps) {
                if (Math.max(start, overlap[0]) < Math.min(end, overlap[1])) {
                    return false;
                }
            }

            // Add new overlaps with existing bookings
            for (int[] booking : bookings) {
                int overlapStart = Math.max(start, booking[0]);
                int overlapEnd = Math.min(end, booking[1]);
                if (overlapStart < overlapEnd) {
                    overlaps.add(new int[] { overlapStart, overlapEnd });
                }
            }

            bookings.add(new int[] { start, end });
            return true;
        }
    }

    // Follow-up 3: Optimized for large number of bookings
    static class MyCalendarOptimized {
        private TreeMap<Integer, Integer> bookings;
        private TreeMap<Integer, Integer> prefixSum;

        public MyCalendarOptimized() {
            this.bookings = new TreeMap<>();
            this.prefixSum = new TreeMap<>();
        }

        public boolean book(int start, int end) {
            prefixSum.put(start, prefixSum.getOrDefault(start, 0) + 1);
            prefixSum.put(end, prefixSum.getOrDefault(end, 0) - 1);

            int activeBookings = 0;
            for (Map.Entry<Integer, Integer> entry : prefixSum.entrySet()) {
                activeBookings += entry.getValue();
                if (activeBookings > 1 && entry.getKey() < end) {
                    // Revert changes
                    prefixSum.put(start, prefixSum.get(start) - 1);
                    prefixSum.put(end, prefixSum.get(end) + 1);
                    if (prefixSum.get(start) == 0)
                        prefixSum.remove(start);
                    if (prefixSum.get(end) == 0)
                        prefixSum.remove(end);
                    return false;
                }
            }

            return true;
        }
    }

    // Follow-up 5: Recurring events
    static class MyCalendarRecurring {
        private List<int[]> oneTimeBookings;
        private List<RecurringEvent> recurringBookings;

        static class RecurringEvent {
            int start, end, interval;
            int repeatCount;

            RecurringEvent(int start, int end, int interval, int repeatCount) {
                this.start = start;
                this.end = end;
                this.interval = interval;
                this.repeatCount = repeatCount;
            }

            List<int[]> getOccurrences() {
                List<int[]> occurrences = new ArrayList<>();
                for (int i = 0; i < repeatCount; i++) {
                    int eventStart = start + i * interval;
                    int eventEnd = end + i * interval;
                    occurrences.add(new int[] { eventStart, eventEnd });
                }
                return occurrences;
            }
        }

        public MyCalendarRecurring() {
            this.oneTimeBookings = new ArrayList<>();
            this.recurringBookings = new ArrayList<>();
        }

        public boolean book(int start, int end) {
            return bookInternal(start, end, false);
        }

        public boolean bookRecurring(int start, int end, int interval, int repeatCount) {
            RecurringEvent event = new RecurringEvent(start, end, interval, repeatCount);

            // Check all occurrences
            for (int[] occurrence : event.getOccurrences()) {
                if (!bookInternal(occurrence[0], occurrence[1], true)) {
                    return false;
                }
            }

            recurringBookings.add(event);
            return true;
        }

        private boolean bookInternal(int start, int end, boolean dryRun) {
            // Check against one-time bookings
            for (int[] booking : oneTimeBookings) {
                if (Math.max(start, booking[0]) < Math.min(end, booking[1])) {
                    return false;
                }
            }

            // Check against recurring bookings
            for (RecurringEvent recurring : recurringBookings) {
                for (int[] occurrence : recurring.getOccurrences()) {
                    if (Math.max(start, occurrence[0]) < Math.min(end, occurrence[1])) {
                        return false;
                    }
                }
            }

            if (!dryRun) {
                oneTimeBookings.add(new int[] { start, end });
            }

            return true;
        }
    }

    // Advanced: Calendar with priorities
    static class MyCalendarPriority {
        private TreeMap<Integer, PriorityEvent> bookings;

        static class PriorityEvent {
            int start, end, priority;
            String title;

            PriorityEvent(int start, int end, int priority, String title) {
                this.start = start;
                this.end = end;
                this.priority = priority;
                this.title = title;
            }
        }

        public MyCalendarPriority() {
            this.bookings = new TreeMap<>();
        }

        public boolean book(int start, int end, int priority, String title) {
            List<Integer> conflictingKeys = new ArrayList<>();

            for (Map.Entry<Integer, PriorityEvent> entry : bookings.entrySet()) {
                PriorityEvent event = entry.getValue();
                if (Math.max(start, event.start) < Math.min(end, event.end)) {
                    if (priority <= event.priority) {
                        return false; // Cannot override higher or equal priority
                    }
                    conflictingKeys.add(entry.getKey());
                }
            }

            // Remove conflicting lower priority events
            for (Integer key : conflictingKeys) {
                bookings.remove(key);
            }

            bookings.put(start, new PriorityEvent(start, end, priority, title));
            return true;
        }

        public List<PriorityEvent> getSchedule() {
            return new ArrayList<>(bookings.values());
        }
    }

    // Helper methods
    public static boolean hasOverlap(int start1, int end1, int start2, int end2) {
        return Math.max(start1, start2) < Math.min(end1, end2);
    }

    public static int[] getOverlapInterval(int start1, int end1, int start2, int end2) {
        if (!hasOverlap(start1, end1, start2, end2)) {
            return null;
        }
        return new int[] { Math.max(start1, start2), Math.min(end1, end2) };
    }

    // Performance comparison
    public static Map<String, Long> comparePerformance(int[][] bookings) {
        Map<String, Long> results = new HashMap<>();

        // Test List approach
        MyCalendarList listCalendar = new MyCalendarList();
        long start = System.nanoTime();
        for (int[] booking : bookings) {
            listCalendar.book(booking[0], booking[1]);
        }
        results.put("List", System.nanoTime() - start);

        // Test TreeMap approach
        MyCalendarTreeMap treeMapCalendar = new MyCalendarTreeMap();
        start = System.nanoTime();
        for (int[] booking : bookings) {
            treeMapCalendar.book(booking[0], booking[1]);
        }
        results.put("TreeMap", System.nanoTime() - start);

        // Test BST approach
        MyCalendarBST bstCalendar = new MyCalendarBST();
        start = System.nanoTime();
        for (int[] booking : bookings) {
            bstCalendar.book(booking[0], booking[1]);
        }
        results.put("BST", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        System.out.println("=== Test Case 1: Basic MyCalendar ===");

        // Test List implementation
        MyCalendarList calendar = new MyCalendarList();

        int[][] testBookings = {
                { 10, 20 }, { 15, 25 }, { 20, 30 }, { 5, 15 }, { 25, 35 }
        };

        for (int[] booking : testBookings) {
            boolean result = calendar.book(booking[0], booking[1]);
            System.out.println("Book [" + booking[0] + ", " + booking[1] + ") -> " + result);
        }

        System.out.println("Final bookings: ");
        for (int[] booking : calendar.getBookings()) {
            System.out.println("[" + booking[0] + ", " + booking[1] + ")");
        }

        // Test Case 2: Compare implementations
        System.out.println("\n=== Test Case 2: Compare Implementations ===");

        MyCalendarList listCal = new MyCalendarList();
        MyCalendarTreeMap treeCal = new MyCalendarTreeMap();
        MyCalendarBST bstCal = new MyCalendarBST();

        int[][] compareBookings = { { 10, 20 }, { 15, 25 }, { 20, 30 } };

        for (int[] booking : compareBookings) {
            boolean list = listCal.book(booking[0], booking[1]);
            boolean tree = treeCal.book(booking[0], booking[1]);
            boolean bst = bstCal.book(booking[0], booking[1]);

            System.out.println("Book [" + booking[0] + ", " + booking[1] + ") -> " +
                    "List: " + list + ", Tree: " + tree + ", BST: " + bst);
        }

        // Follow-up 1: My Calendar II
        System.out.println("\n=== Follow-up 1: My Calendar II ===");

        MyCalendarTwo calendar2 = new MyCalendarTwo();
        int[][] testBookings2 = {
                { 10, 20 }, { 50, 60 }, { 10, 40 }, { 5, 15 }, { 5, 10 }, { 25, 55 }
        };

        for (int[] booking : testBookings2) {
            boolean result = calendar2.book(booking[0], booking[1]);
            System.out.println("Book [" + booking[0] + ", " + booking[1] + ") -> " + result);
        }

        // Follow-up 5: Recurring events
        System.out.println("\n=== Follow-up 5: Recurring Events ===");

        MyCalendarRecurring recurringCal = new MyCalendarRecurring();

        // Book a one-time event
        System.out.println("One-time [10, 20): " + recurringCal.book(10, 20));

        // Book recurring event (every 7 days, 3 times)
        System.out.println("Recurring [30, 40) every 7 days, 3 times: " +
                recurringCal.bookRecurring(30, 40, 7, 3));

        // Try to book conflicting event
        System.out.println("Conflicting [35, 45): " + recurringCal.book(35, 45));

        // Advanced: Priority calendar
        System.out.println("\n=== Advanced: Priority Calendar ===");

        MyCalendarPriority priorityCal = new MyCalendarPriority();

        System.out.println("Low priority meeting [10, 20): " +
                priorityCal.book(10, 20, 1, "Team Meeting"));
        System.out.println("High priority meeting [15, 25): " +
                priorityCal.book(15, 25, 3, "CEO Meeting"));
        System.out.println("Medium priority meeting [18, 22): " +
                priorityCal.book(18, 22, 2, "Client Call"));

        System.out.println("\nFinal schedule:");
        for (MyCalendarPriority.PriorityEvent event : priorityCal.getSchedule()) {
            System.out.println("[" + event.start + ", " + event.end + ") - " +
                    event.title + " (Priority: " + event.priority + ")");
        }

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");

        int[][] performanceBookings = new int[100][2];
        Random random = new Random(42);

        for (int i = 0; i < 100; i++) {
            int start = random.nextInt(1000);
            int end = start + random.nextInt(50) + 1;
            performanceBookings[i] = new int[] { start, end };
        }

        Map<String, Long> performance = comparePerformance(performanceBookings);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        MyCalendarList edgeCalendar = new MyCalendarList();

        // Adjacent intervals (should not overlap)
        System.out.println("Adjacent [10, 20) and [20, 30): " +
                edgeCalendar.book(10, 20) + ", " + edgeCalendar.book(20, 30));

        // Same start time
        System.out.println("Same start [30, 40) and [30, 50): " +
                edgeCalendar.book(30, 40) + ", " + edgeCalendar.book(30, 50));

        // Completely contained
        System.out.println("Container [50, 100) and contained [60, 70): " +
                edgeCalendar.book(50, 100) + ", " + edgeCalendar.book(60, 70));

        // Single point interval
        System.out.println("Single point [100, 101): " + edgeCalendar.book(100, 101));

        // Very large interval
        System.out.println("Large [0, 1000000): " + edgeCalendar.book(0, 1000000));

        // Helper methods testing
        System.out.println("\n=== Helper Methods ===");

        System.out.println("Overlap [10, 20) and [15, 25): " +
                hasOverlap(10, 20, 15, 25));
        System.out.println("Overlap [10, 20) and [20, 30): " +
                hasOverlap(10, 20, 20, 30));

        int[] overlap = getOverlapInterval(10, 20, 15, 25);
        if (overlap != null) {
            System.out.println("Overlap interval: [" + overlap[0] + ", " + overlap[1] + ")");
        }

        System.out.println("\nMyCalendar testing completed successfully!");
    }
}
