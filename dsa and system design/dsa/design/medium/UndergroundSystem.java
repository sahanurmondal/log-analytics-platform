package design.medium;

import java.util.*;

/**
 * LeetCode 1396: Design Underground System
 * https://leetcode.com/problems/design-underground-system/
 *
 * Description: An underground railway system is keeping track of customer
 * travel times between different stations.
 * 
 * Constraints:
 * - 1 <= id, t <= 10^6
 * - 1 <= stationName.length, startStation.length, endStation.length <= 10
 * - All strings consist of uppercase and lowercase English letters and digits
 * - There will be at most 2 * 10^4 calls in total to checkIn, checkOut, and
 * getAverageTime
 * - Answers within 10^-5 of the actual value will be accepted
 *
 * Follow-up:
 * - Can you handle concurrent access?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n + m) where n is customers, m is unique routes
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class UndergroundSystem {

    class CheckInInfo {
        String stationName;
        int time;

        CheckInInfo(String stationName, int time) {
            this.stationName = stationName;
            this.time = time;
        }
    }

    class TravelInfo {
        double totalTime;
        int count;

        TravelInfo(double totalTime, int count) {
            this.totalTime = totalTime;
            this.count = count;
        }

        void addTrip(int time) {
            totalTime += time;
            count++;
        }

        double getAverage() {
            return totalTime / count;
        }
    }

    private Map<Integer, CheckInInfo> checkInMap;
    private Map<String, TravelInfo> travelMap;

    public UndergroundSystem() {
        checkInMap = new HashMap<>();
        travelMap = new HashMap<>();
    }

    public void checkIn(int id, String stationName, int t) {
        checkInMap.put(id, new CheckInInfo(stationName, t));
    }

    public void checkOut(int id, String stationName, int t) {
        CheckInInfo checkIn = checkInMap.get(id);
        checkInMap.remove(id);

        String route = checkIn.stationName + "-" + stationName;
        int travelTime = t - checkIn.time;

        travelMap.computeIfAbsent(route, k -> new TravelInfo(0, 0))
                .addTrip(travelTime);
    }

    public double getAverageTime(String startStation, String endStation) {
        String route = startStation + "-" + endStation;
        return travelMap.get(route).getAverage();
    }

    public static void main(String[] args) {
        UndergroundSystem undergroundSystem = new UndergroundSystem();
        undergroundSystem.checkIn(45, "Leyton", 3);
        undergroundSystem.checkIn(32, "Paradise", 8);
        undergroundSystem.checkIn(27, "Leyton", 10);
        undergroundSystem.checkOut(45, "Waterloo", 15);
        undergroundSystem.checkOut(27, "Waterloo", 20);
        undergroundSystem.checkOut(32, "Cambridge", 22);
        System.out.println(undergroundSystem.getAverageTime("Paradise", "Cambridge")); // Expected: 14.0
        System.out.println(undergroundSystem.getAverageTime("Leyton", "Waterloo")); // Expected: 11.0
    }
}
