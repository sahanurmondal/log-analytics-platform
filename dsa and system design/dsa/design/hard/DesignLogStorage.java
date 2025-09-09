package design.hard;

import java.util.*;

/**
 * LeetCode 635: Design Log Storage System
 * https://leetcode.com/problems/design-log-storage-system/
 *
 * Description: You are given several logs, where each log contains a unique ID
 * and timestamp.
 * Timestamp is a string that has the following format:
 * Year:Month:Day:Hour:Minute:Second.
 * 
 * Constraints:
 * - 1 <= id <= 500
 * - 2000 <= Year <= 2017
 * - 1 <= Month <= 12
 * - 1 <= Day <= 31
 * - 0 <= Hour <= 23
 * - 0 <= Minute, Second <= 59
 * - granularity is one of ["Year", "Month", "Day", "Hour", "Minute", "Second"]
 * - At most 500 calls will be made to put and retrieve
 *
 * Follow-up:
 * - Can you optimize for range queries?
 * 
 * Time Complexity: O(1) for put, O(n) for retrieve
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class DesignLogStorage {

    private List<Log> logs;
    private Map<String, Integer> granularityMap;

    class Log {
        int id;
        String timestamp;

        Log(int id, String timestamp) {
            this.id = id;
            this.timestamp = timestamp;
        }
    }

    public DesignLogStorage() {
        logs = new ArrayList<>();
        granularityMap = new HashMap<>();
        granularityMap.put("Year", 4);
        granularityMap.put("Month", 7);
        granularityMap.put("Day", 10);
        granularityMap.put("Hour", 13);
        granularityMap.put("Minute", 16);
        granularityMap.put("Second", 19);
    }

    public void put(int id, String timestamp) {
        logs.add(new Log(id, timestamp));
    }

    public List<Integer> retrieve(String start, String end, String granularity) {
        int index = granularityMap.get(granularity);
        String startTruncated = start.substring(0, index);
        String endTruncated = end.substring(0, index);

        List<Integer> result = new ArrayList<>();

        for (Log log : logs) {
            String logTruncated = log.timestamp.substring(0, index);
            if (logTruncated.compareTo(startTruncated) >= 0 &&
                    logTruncated.compareTo(endTruncated) <= 0) {
                result.add(log.id);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        DesignLogStorage logSystem = new DesignLogStorage();
        logSystem.put(1, "2017:01:01:23:59:59");
        logSystem.put(2, "2017:01:01:22:59:59");
        logSystem.put(3, "2016:01:01:00:00:00");

        System.out.println(logSystem.retrieve("2016:01:01:01:01:01", "2017:01:01:23:00:00", "Year"));
        // Expected: [1, 2, 3]

        System.out.println(logSystem.retrieve("2016:01:01:01:01:01", "2017:01:01:23:00:00", "Hour"));
        // Expected: [1, 2]
    }
}
