package design.easy;

/**
 * LeetCode 1603: Design Parking System
 * https://leetcode.com/problems/design-parking-system/
 *
 * Description: Design a parking system for a parking lot. The parking lot has
 * three kinds of parking spaces: big, medium, and small, with a fixed number of
 * slots for each size.
 * 
 * Constraints:
 * - 0 <= big, medium, small <= 1000
 * - carType is 1, 2, or 3
 * - At most 1000 calls will be made to addCar
 *
 * Follow-up:
 * - Can you optimize space usage?
 * 
 * Time Complexity: O(1) for addCar
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Amazon
 */
public class ParkingSystem {

    private int[] spaces;

    public ParkingSystem(int big, int medium, int small) {
        spaces = new int[] { big, medium, small };
    }

    public boolean addCar(int carType) {
        if (spaces[carType - 1] > 0) {
            spaces[carType - 1]--;
            return true;
        }
        return false;
    }

    // Alternative implementation - Separate variables
    static class ParkingSystemSeparate {
        private int big, medium, small;

        public ParkingSystemSeparate(int big, int medium, int small) {
            this.big = big;
            this.medium = medium;
            this.small = small;
        }

        public boolean addCar(int carType) {
            switch (carType) {
                case 1:
                    if (big > 0) {
                        big--;
                        return true;
                    }
                    break;
                case 2:
                    if (medium > 0) {
                        medium--;
                        return true;
                    }
                    break;
                case 3:
                    if (small > 0) {
                        small--;
                        return true;
                    }
                    break;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        ParkingSystem parkingSystem = new ParkingSystem(1, 1, 0);
        System.out.println(parkingSystem.addCar(1)); // Expected: true
        System.out.println(parkingSystem.addCar(2)); // Expected: true
        System.out.println(parkingSystem.addCar(3)); // Expected: false
        System.out.println(parkingSystem.addCar(1)); // Expected: false
    }
}
