package searching.hard;

/**
 * LeetCode 849: Maximize Distance to Closest Person
 * https://leetcode.com/problems/maximize-distance-to-closest-person/
 * 
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description: Find the seat that maximizes distance to the closest person.
 *
 * Constraints:
 * - 2 <= seats.length <= 2 * 10^4
 * - seats[i] is 0 or 1
 * - At least one seat is empty and at least one seat is occupied
 * 
 * Follow-up Questions:
 * 1. Can you return all optimal seats?
 * 2. What if we want to seat k people optimally?
 * 3. Can you handle circular seating?
 */
public class MaximizeDistanceToClosestPerson {

    // Approach 1: Two pass - O(n) time, O(1) space
    public int maxDistToClosest(int[] seats) {
        int n = seats.length;
        int maxDist = 0;

        for (int i = 0; i < n; i++) {
            if (seats[i] == 0) {
                int dist = getMinDistance(seats, i);
                maxDist = Math.max(maxDist, dist);
            }
        }
        return maxDist;
    }

    private int getMinDistance(int[] seats, int pos) {
        int leftDist = Integer.MAX_VALUE, rightDist = Integer.MAX_VALUE;

        // Find closest person to the left
        for (int i = pos - 1; i >= 0; i--) {
            if (seats[i] == 1) {
                leftDist = pos - i;
                break;
            }
        }

        // Find closest person to the right
        for (int i = pos + 1; i < seats.length; i++) {
            if (seats[i] == 1) {
                rightDist = i - pos;
                break;
            }
        }

        return Math.min(leftDist, rightDist);
    }

    // Approach 2: Group consecutive zeros - O(n) time, O(1) space
    public int maxDistToClosestOptimized(int[] seats) {
        int n = seats.length;
        int maxDist = 0;
        int i = 0;

        while (i < n) {
            if (seats[i] == 1) {
                i++;
                continue;
            }

            int start = i;
            while (i < n && seats[i] == 0) {
                i++;
            }
            int end = i - 1;

            int segmentLength = end - start + 1;
            int dist;

            if (start == 0 || end == n - 1) {
                // Edge segment
                dist = segmentLength;
            } else {
                // Middle segment
                dist = (segmentLength + 1) / 2;
            }

            maxDist = Math.max(maxDist, dist);
        }

        return maxDist;
    }

    // Approach 3: Precompute distances - O(n) time, O(n) space
    public int maxDistToClosestPrecompute(int[] seats) {
        int n = seats.length;
        int[] leftDist = new int[n];
        int[] rightDist = new int[n];

        // Compute distances to nearest person on the left
        int lastPerson = -1;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                lastPerson = i;
                leftDist[i] = 0;
            } else {
                leftDist[i] = lastPerson == -1 ? Integer.MAX_VALUE : i - lastPerson;
            }
        }

        // Compute distances to nearest person on the right
        lastPerson = -1;
        for (int i = n - 1; i >= 0; i--) {
            if (seats[i] == 1) {
                lastPerson = i;
                rightDist[i] = 0;
            } else {
                rightDist[i] = lastPerson == -1 ? Integer.MAX_VALUE : lastPerson - i;
            }
        }

        int maxDist = 0;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 0) {
                int dist = Math.min(leftDist[i], rightDist[i]);
                maxDist = Math.max(maxDist, dist);
            }
        }

        return maxDist;
    }

    // Follow-up 1: Return all optimal seats
    public java.util.List<Integer> getAllOptimalSeats(int[] seats) {
        int maxDist = maxDistToClosest(seats);
        java.util.List<Integer> optimalSeats = new java.util.ArrayList<>();

        for (int i = 0; i < seats.length; i++) {
            if (seats[i] == 0) {
                int dist = getMinDistance(seats, i);
                if (dist == maxDist) {
                    optimalSeats.add(i);
                }
            }
        }
        return optimalSeats;
    }

    // Follow-up 2: Seat k people optimally
    public int[] seatKPeople(int[] seats, int k) {
        int[] result = new int[k];
        int[] seatsCopy = seats.clone();

        for (int person = 0; person < k; person++) {
            int bestSeat = -1;
            int maxDist = 0;

            for (int i = 0; i < seatsCopy.length; i++) {
                if (seatsCopy[i] == 0) {
                    int dist = getMinDistance(seatsCopy, i);
                    if (dist > maxDist) {
                        maxDist = dist;
                        bestSeat = i;
                    }
                }
            }

            if (bestSeat != -1) {
                result[person] = bestSeat;
                seatsCopy[bestSeat] = 1;
            }
        }

        return result;
    }

    // Follow-up 3: Circular seating
    public int maxDistToClosestCircular(int[] seats) {
        int n = seats.length;
        int maxDist = 0;

        for (int i = 0; i < n; i++) {
            if (seats[i] == 0) {
                int dist = getMinDistanceCircular(seats, i);
                maxDist = Math.max(maxDist, dist);
            }
        }
        return maxDist;
    }

    private int getMinDistanceCircular(int[] seats, int pos) {
        int n = seats.length;
        int minDist = n;

        for (int dist = 1; dist <= n / 2; dist++) {
            int left = (pos - dist + n) % n;
            int right = (pos + dist) % n;

            if (seats[left] == 1 || seats[right] == 1) {
                minDist = dist;
                break;
            }
        }

        return minDist;
    }

    public static void main(String[] args) {
        MaximizeDistanceToClosestPerson solution = new MaximizeDistanceToClosestPerson();

        // Test case 1: Basic case
        int[] seats1 = { 1, 0, 0, 0, 1, 0, 1 };
        System.out.println("Test 1 - Basic case:");
        System.out.println("Expected: 2, Got: " + solution.maxDistToClosest(seats1));
        System.out.println("Optimized: " + solution.maxDistToClosestOptimized(seats1));
        System.out.println("Precompute: " + solution.maxDistToClosestPrecompute(seats1));

        // Test case 2: Edge position optimal
        int[] seats2 = { 1, 0, 0, 0 };
        System.out.println("\nTest 2 - Edge optimal:");
        System.out.println("Expected: 3, Got: " + solution.maxDistToClosest(seats2));

        // Test case 3: Both edges empty
        int[] seats3 = { 0, 0, 1, 0, 0 };
        System.out.println("\nTest 3 - Both edges empty:");
        System.out.println("Expected: 2, Got: " + solution.maxDistToClosest(seats3));

        // Test case 4: Adjacent people
        int[] seats4 = { 1, 1, 0, 1 };
        System.out.println("\nTest 4 - Adjacent people:");
        System.out.println("Expected: 1, Got: " + solution.maxDistToClosest(seats4));

        // Test case 5: Single empty seat between people
        int[] seats5 = { 1, 0, 1 };
        System.out.println("\nTest 5 - Single seat between:");
        System.out.println("Expected: 1, Got: " + solution.maxDistToClosest(seats5));

        // Edge case: Only two seats
        int[] seats6 = { 1, 0 };
        System.out.println("\nEdge case - Two seats:");
        System.out.println("Expected: 1, Got: " + solution.maxDistToClosest(seats6));

        // Follow-up 1: All optimal seats
        System.out.println("\nFollow-up 1 - All optimal seats:");
        System.out.println("Optimal positions: " + solution.getAllOptimalSeats(seats1));

        // Follow-up 2: Seat k people
        System.out.println("\nFollow-up 2 - Seat 2 people:");
        int[] kSeats = solution.seatKPeople(new int[] { 1, 0, 0, 0, 0, 1 }, 2);
        System.out.println("Seat positions: " + java.util.Arrays.toString(kSeats));

        // Follow-up 3: Circular seating
        int[] seats7 = { 0, 1, 0, 0, 0 };
        System.out.println("\nFollow-up 3 - Circular seating:");
        System.out.println("Expected: 2, Got: " + solution.maxDistToClosestCircular(seats7));

        // Performance test
        int[] largeSeats = new int[20000];
        for (int i = 0; i < largeSeats.length; i += 100) {
            largeSeats[i] = 1;
        }
        long startTime = System.currentTimeMillis();
        solution.maxDistToClosestOptimized(largeSeats);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (20k seats): " + (endTime - startTime) + "ms");
    }
}
