package design.hard;

import java.util.*;

/**
 * LeetCode 729, 731, 732: My Calendar I, II, III
 * https://leetcode.com/problems/my-calendar-i/
 * https://leetcode.com/problems/my-calendar-ii/
 * https://leetcode.com/problems/my-calendar-iii/
 *
 * Description: Design calendar systems with different booking constraints
 * 
 * Constraints:
 * - 0 <= start < end <= 10^9
 * - At most 1000 calls will be made to book
 *
 * Follow-up:
 * - Can you optimize for range queries?
 * 
 * Time Complexity: O(n) for book operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class DesignCalendar {

    // Calendar I - No double booking
    static class MyCalendarI {
        private List<int[]> bookings;

        public MyCalendarI() {
            bookings = new ArrayList<>();
        }

        public boolean book(int start, int end) {
            for (int[] booking : bookings) {
                if (Math.max(start, booking[0]) < Math.min(end, booking[1])) {
                    return false; // Overlap found
                }
            }
            bookings.add(new int[] { start, end });
            return true;
        }
    }

    // Calendar II - At most double booking
    static class MyCalendarII {
        private List<int[]> bookings;
        private List<int[]> overlaps;

        public MyCalendarII() {
            bookings = new ArrayList<>();
            overlaps = new ArrayList<>();
        }

        public boolean book(int start, int end) {
            // Check if it would cause triple booking
            for (int[] overlap : overlaps) {
                if (Math.max(start, overlap[0]) < Math.min(end, overlap[1])) {
                    return false;
                }
            }

            // Add overlaps with existing bookings
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

    // Calendar III - Count maximum concurrent bookings
    static class MyCalendarIII {
        private TreeMap<Integer, Integer> events;

        public MyCalendarIII() {
            events = new TreeMap<>();
        }

        public int book(int start, int end) {
            events.put(start, events.getOrDefault(start, 0) + 1);
            events.put(end, events.getOrDefault(end, 0) - 1);

            int maxBookings = 0;
            int currentBookings = 0;

            for (int count : events.values()) {
                currentBookings += count;
                maxBookings = Math.max(maxBookings, currentBookings);
            }

            return maxBookings;
        }
    }

    // Optimized Calendar I using TreeSet
    static class MyCalendarITreeSet {
        private TreeSet<int[]> bookings;

        public MyCalendarITreeSet() {
            bookings = new TreeSet<>((a, b) -> a[0] - b[0]);
        }

        public boolean book(int start, int end) {
            int[] event = new int[] { start, end };
            int[] floor = bookings.floor(event);
            int[] ceiling = bookings.ceiling(event);

            if ((floor != null && floor[1] > start) ||
                    (ceiling != null && ceiling[0] < end)) {
                return false;
            }

            bookings.add(event);
            return true;
        }
    }

    public static void main(String[] args) {
        // Test Calendar I
        MyCalendarI cal1 = new MyCalendarI();
        System.out.println(cal1.book(10, 20)); // Expected: true
        System.out.println(cal1.book(15, 25)); // Expected: false
        System.out.println(cal1.book(20, 30)); // Expected: true

        // Test Calendar II
        MyCalendarII cal2 = new MyCalendarII();
        System.out.println(cal2.book(10, 20)); // Expected: true
        System.out.println(cal2.book(50, 60)); // Expected: true
        System.out.println(cal2.book(10, 40)); // Expected: true
        System.out.println(cal2.book(5, 15)); // Expected: false
        System.out.println(cal2.book(5, 10)); // Expected: true
        System.out.println(cal2.book(25, 55)); // Expected: true

        // Test Calendar III
        MyCalendarIII cal3 = new MyCalendarIII();
        System.out.println(cal3.book(10, 20)); // Expected: 1
        System.out.println(cal3.book(50, 60)); // Expected: 1
        System.out.println(cal3.book(10, 40)); // Expected: 2
        System.out.println(cal3.book(5, 15)); // Expected: 3
        System.out.println(cal3.book(5, 10)); // Expected: 3
        System.out.println(cal3.book(25, 55)); // Expected: 3
    }
}
