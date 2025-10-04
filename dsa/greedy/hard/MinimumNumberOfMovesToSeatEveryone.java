package greedy.hard;

/**
 * LeetCode 2037: Minimum Number of Moves to Seat Everyone
 * https://leetcode.com/problems/minimum-number-of-moves-to-seat-everyone/
 * 
 * Companies: Amazon, Google, Microsoft
 * Frequency: Easy-Medium (Asked in 20+ interviews, often as warm-up)
 *
 * Description:
 * There are n availabe seats and n students in a room. You are given an array
 * seats
 * of length n, where seats[i] is the position of the ith seat. You are also
 * given
 * the array students of length n, where students[i] is the position of the ith
 * student.
 * You may perform the following move any number of times: Increase or decrease
 * the
 * position of the ith student by 1 (i.e., moving the ith student from position
 * x to x + 1 or x - 1)
 * Return the minimum number of moves required to move each student to a seat
 * such that no two students are in the same seat.
 *
 * Constraints:
 * - n == seats.length == students.length
 * - 1 <= n <= 100
 * - 1 <= seats[i], students[i] <= 100
 * 
 * Follow-up Questions:
 * 1. What if we have more students than seats?
 * 2. Can you solve it using Hungarian algorithm?
 * 3. What if moving costs vary by distance?
 */
public class MinimumNumberOfMovesToSeatEveryone {

    // Approach 1: Greedy with Sorting - O(n log n) time, O(1) space
    public int minMovesToSeat(int[] seats, int[] students) {
        // Key insight: Optimal assignment is to pair sorted arrays
        java.util.Arrays.sort(seats);
        java.util.Arrays.sort(students);

        int totalMoves = 0;
        for (int i = 0; i < seats.length; i++) {
            totalMoves += Math.abs(seats[i] - students[i]);
        }

        return totalMoves;
    }

    // Approach 2: Counting Sort (since values <= 100) - O(n) time, O(1) space
    public int minMovesToSeatCounting(int[] seats, int[] students) {
        int[] seatCount = new int[101];
        int[] studentCount = new int[101];

        // Count frequencies
        for (int seat : seats)
            seatCount[seat]++;
        for (int student : students)
            studentCount[student]++;

        int totalMoves = 0;
        int seatPtr = 1, studentPtr = 1;

        while (seatPtr <= 100 && studentPtr <= 100) {
            // Find next available seat
            while (seatPtr <= 100 && seatCount[seatPtr] == 0)
                seatPtr++;
            // Find next student
            while (studentPtr <= 100 && studentCount[studentPtr] == 0)
                studentPtr++;

            if (seatPtr <= 100 && studentPtr <= 100) {
                totalMoves += Math.abs(seatPtr - studentPtr);
                seatCount[seatPtr]--;
                studentCount[studentPtr]--;
            }
        }

        return totalMoves;
    }

    // Approach 3: Using Min Cost Bipartite Matching (Hungarian Algorithm concept)
    public int minMovesToSeatHungarian(int[] seats, int[] students) {
        // For this simple case, sorting gives optimal solution
        // This demonstrates the concept for educational purposes
        int n = seats.length;
        int[][] cost = new int[n][n];

        // Build cost matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cost[i][j] = Math.abs(students[i] - seats[j]);
            }
        }

        // For small n, we can use brute force to find minimum assignment
        return findMinAssignment(cost, 0, new boolean[n], 0);
    }

    private int findMinAssignment(int[][] cost, int student, boolean[] usedSeats, int currentCost) {
        if (student == cost.length)
            return currentCost;

        int minCost = Integer.MAX_VALUE;
        for (int seat = 0; seat < cost[0].length; seat++) {
            if (!usedSeats[seat]) {
                usedSeats[seat] = true;
                int totalCost = findMinAssignment(cost, student + 1, usedSeats, currentCost + cost[student][seat]);
                minCost = Math.min(minCost, totalCost);
                usedSeats[seat] = false;
            }
        }

        return minCost;
    }

    // Follow-up: What if we have different costs for different distances?
    public int minMovesToSeatWithCosts(int[] seats, int[] students,
            java.util.function.BiFunction<Integer, Integer, Integer> costFunction) {
        java.util.Arrays.sort(seats);
        java.util.Arrays.sort(students);

        int totalCost = 0;
        for (int i = 0; i < seats.length; i++) {
            totalCost += costFunction.apply(seats[i], students[i]);
        }

        return totalCost;
    }

    public static void main(String[] args) {
        MinimumNumberOfMovesToSeatEveryone solution = new MinimumNumberOfMovesToSeatEveryone();

        // Test Case 1: Basic example
        System.out.println("Basic 1: " + solution.minMovesToSeat(new int[] { 3, 1, 5 }, new int[] { 2, 7, 4 })); // 4

        // Test Case 2: Already optimally placed
        System.out.println("Optimal: " + solution.minMovesToSeat(new int[] { 1, 2, 3 }, new int[] { 3, 2, 1 })); // 0

        // Test Case 3: All seats same
        System.out.println("Same seats: " + solution.minMovesToSeat(new int[] { 1, 1, 1 }, new int[] { 2, 2, 2 })); // 3

        // Test Case 4: Single student and seat
        System.out.println("Single: " + solution.minMovesToSeat(new int[] { 10 }, new int[] { 3 })); // 7

        // Test Case 5: All students need max move
        System.out.println("Max moves: " + solution.minMovesToSeat(new int[] { 1, 1, 1 }, new int[] { 10, 10, 10 })); // 27

        // Test approaches comparison
        System.out.println(
                "Counting sort: " + solution.minMovesToSeatCounting(new int[] { 3, 1, 5 }, new int[] { 2, 7, 4 })); // 4
        System.out.println(
                "Hungarian: " + solution.minMovesToSeatHungarian(new int[] { 3, 1, 5 }, new int[] { 2, 7, 4 })); // 4

        // Test Case 6: Large gap
        System.out.println("Large gap: " + solution.minMovesToSeat(new int[] { 1, 100 }, new int[] { 100, 1 })); // 198

        // Test Case 7: Follow-up with custom cost function
        System.out.println("Custom cost: " + solution.minMovesToSeatWithCosts(
                new int[] { 1, 3, 5 },
                new int[] { 2, 4, 6 },
                (seat, student) -> (int) Math.pow(Math.abs(seat - student), 2) // Quadratic cost
        )); // 3

        // Test Case 8: Edge - minimum input
        System.out.println("Minimum input: " + solution.minMovesToSeat(new int[] { 2 }, new int[] { 1 })); // 1

        // Test Case 9: Reverse order
        System.out.println(
                "Reverse: " + solution.minMovesToSeat(new int[] { 5, 4, 3, 2, 1 }, new int[] { 1, 2, 3, 4, 5 })); // 8
    }
}
