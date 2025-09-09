package stacks.medium;

import java.util.*;

/**
 * LeetCode 735: Asteroid Collision
 * https://leetcode.com/problems/asteroid-collision/
 * 
 * Companies: Google, Amazon
 * Frequency: High
 *
 * Description: Given an array of integers representing asteroids in a row, for each asteroid, the absolute value represents its size, and the sign represents its direction (positive = right, negative = left). Asteroids moving in opposite directions may collide. Return the state of the asteroids after all collisions.
 *
 * Constraints:
 * - 2 <= asteroids.length <= 10^4
 * - -1000 <= asteroids[i] <= 1000
 * - asteroids[i] != 0
 *
 * ASCII Art:
 * [5, 10, -5] → [5, 10]
 * →5 →10 ←5 →5 →10
 * (10 > 5, so -5 explodes)
 *
 * [8, -8] → []
 * →8 ←8 (8 == 8, both explode)
 *
 * Follow-up:
 * - Can you handle 3D collisions?
 * - Can you optimize for large arrays?
 */
public class AsteroidCollision {

    // Approach 1: Stack simulation
    public int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();
        for (int a : asteroids) {
            while (!stack.isEmpty() && a < 0 && stack.peek() > 0) {
                int top = stack.peek();
                if (top < -a) stack.pop();
                else if (top == -a) { stack.pop(); a = 0; }
                else { a = 0; }
            }
            if (a != 0) stack.push(a);
        }
        int[] res = new int[stack.size()];
        for (int i = res.length - 1; i >= 0; i--) res[i] = stack.pop();
        return res;
    }

    // Follow-up 1: Number of collisions
    public int countCollisions(int[] asteroids) {
        int collisions = 0;
        Stack<Integer> stack = new Stack<>();
        for (int a : asteroids) {
            while (!stack.isEmpty() && a < 0 && stack.peek() > 0) {
                collisions++;
                int top = stack.peek();
                if (top < -a) stack.pop();
                else if (top == -a) { stack.pop(); a = 0; }
                else { a = 0; }
            }
            if (a != 0) stack.push(a);
        }
        return collisions;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        AsteroidCollision solution = new AsteroidCollision();

        // Test case 1: Basic case
        int[] asteroids1 = {5,10,-5};
        System.out.println("Test 1 - asteroids: " + Arrays.toString(asteroids1) + " Expected: [5,10]");
        System.out.println("Result: " + Arrays.toString(solution.asteroidCollision(asteroids1)));

        // Test case 2: All left
        int[] asteroids2 = {-2,-1,-3};
        System.out.println("\nTest 2 - All left:");
        System.out.println("Result: " + Arrays.toString(solution.asteroidCollision(asteroids2)));

        // Test case 3: All right
        int[] asteroids3 = {2,1,3};
        System.out.println("\nTest 3 - All right:");
        System.out.println("Result: " + Arrays.toString(solution.asteroidCollision(asteroids3)));

        // Test case 4: Collisions count
        int[] asteroids4 = {8,-8};
        System.out.println("\nTest 4 - Collisions count:");
        System.out.println(solution.countCollisions(asteroids4));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " + Arrays.toString(solution.asteroidCollision(new int[]{})));
        System.out.println("Single asteroid: " + Arrays.toString(solution.asteroidCollision(new int[]{1})));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int[] result = solution.asteroidCollision(large);
        long end = System.nanoTime();
        System.out.println("Result length: " + result.length + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
