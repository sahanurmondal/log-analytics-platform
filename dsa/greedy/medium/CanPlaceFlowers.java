package greedy.medium;

/**
 * LeetCode 605: Can Place Flowers
 * https://leetcode.com/problems/can-place-flowers/
 *
 * Description:
 * Given a flowerbed (array) and a number n, return if n flowers can be planted
 * without violating the no-adjacent-flowers rule.
 *
 * Constraints:
 * - 1 <= flowerbed.length <= 2 * 10^4
 * - flowerbed[i] is 0 or 1
 * - 0 <= n <= flowerbed.length
 */
/**
 * LeetCode 605: Can Place Flowers
 * https://leetcode.com/problems/can-place-flowers/
 *
 * Description:
 * Given a flowerbed (array) and a number n, return if n flowers can be planted
 * without violating the no-adjacent-flowers rule.
 *
 * Constraints:
 * - 1 <= flowerbed.length <= 2 * 10^4
 * - flowerbed[i] is 0 or 1
 * - 0 <= n <= flowerbed.length
 *
 * Follow-up:
 * - Can you solve it in O(1) space without modifying the array?
 * - Can you handle circular flowerbeds?
 */
public class CanPlaceFlowers {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int count = 0;
        for (int i = 0; i < flowerbed.length; i++) {
            if (flowerbed[i] == 0) {
                boolean leftEmpty = (i == 0) || (flowerbed[i - 1] == 0);
                boolean rightEmpty = (i == flowerbed.length - 1) || (flowerbed[i + 1] == 0);

                if (leftEmpty && rightEmpty) {
                    flowerbed[i] = 1; // Plant flower
                    count++;
                    if (count >= n)
                        return true;
                }
            }
        }
        return count >= n;
    }

    public static void main(String[] args) {
        CanPlaceFlowers solution = new CanPlaceFlowers();
        System.out.println(solution.canPlaceFlowers(new int[] { 1, 0, 0, 0, 1 }, 1)); // true
        System.out.println(solution.canPlaceFlowers(new int[] { 1, 0, 0, 0, 1 }, 2)); // false
        // Edge Case: All zeros
        System.out.println(solution.canPlaceFlowers(new int[] { 0, 0, 0, 0, 0 }, 3)); // true
        // Edge Case: n == 0
        System.out.println(solution.canPlaceFlowers(new int[] { 1, 0, 0, 0, 1 }, 0)); // true
        // Edge Case: Large input
        int[] large = new int[20000];
        for (int i = 0; i < 20000; i++)
            large[i] = 0;
        System.out.println(solution.canPlaceFlowers(large, 10000)); // true
    }
}
