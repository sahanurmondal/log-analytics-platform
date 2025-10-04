package design.medium;

import java.util.*;

/**
 * LeetCode 2034: Stock Price Fluctuation
 * https://leetcode.com/problems/stock-price-fluctuation/
 *
 * Description: You are given a stream of records about a particular stock.
 * Each record contains a timestamp and the corresponding price of the stock at
 * that timestamp.
 * 
 * Constraints:
 * - 1 <= timestamp, price <= 10^9
 * - At most 10^5 calls will be made in total to update, current, maximum, and
 * minimum
 *
 * Follow-up:
 * - Can you handle updates efficiently?
 * 
 * Time Complexity: O(log n) for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon
 */
public class DesignStockPrice {

    private Map<Integer, Integer> timestampToPrice;
    private TreeMap<Integer, Integer> priceCount;
    private int latestTimestamp;

    public DesignStockPrice() {
        timestampToPrice = new HashMap<>();
        priceCount = new TreeMap<>();
        latestTimestamp = 0;
    }

    public void update(int timestamp, int price) {
        // Remove old price if exists
        if (timestampToPrice.containsKey(timestamp)) {
            int oldPrice = timestampToPrice.get(timestamp);
            priceCount.put(oldPrice, priceCount.get(oldPrice) - 1);
            if (priceCount.get(oldPrice) == 0) {
                priceCount.remove(oldPrice);
            }
        }

        // Add new price
        timestampToPrice.put(timestamp, price);
        priceCount.put(price, priceCount.getOrDefault(price, 0) + 1);
        latestTimestamp = Math.max(latestTimestamp, timestamp);
    }

    public int current() {
        return timestampToPrice.get(latestTimestamp);
    }

    public int maximum() {
        return priceCount.lastKey();
    }

    public int minimum() {
        return priceCount.firstKey();
    }

    // Alternative implementation using PriorityQueues
    static class StockPriceAlternative {
        private Map<Integer, Integer> timestampToPrice;
        private PriorityQueue<int[]> maxHeap; // {price, timestamp}
        private PriorityQueue<int[]> minHeap; // {price, timestamp}
        private int latestTimestamp;

        public StockPriceAlternative() {
            timestampToPrice = new HashMap<>();
            maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);
            minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
            latestTimestamp = 0;
        }

        public void update(int timestamp, int price) {
            timestampToPrice.put(timestamp, price);
            maxHeap.offer(new int[] { price, timestamp });
            minHeap.offer(new int[] { price, timestamp });
            latestTimestamp = Math.max(latestTimestamp, timestamp);
        }

        public int current() {
            return timestampToPrice.get(latestTimestamp);
        }

        public int maximum() {
            while (!maxHeap.isEmpty()) {
                int[] top = maxHeap.peek();
                if (timestampToPrice.get(top[1]) == top[0]) {
                    return top[0];
                }
                maxHeap.poll();
            }
            return -1;
        }

        public int minimum() {
            while (!minHeap.isEmpty()) {
                int[] top = minHeap.peek();
                if (timestampToPrice.get(top[1]) == top[0]) {
                    return top[0];
                }
                minHeap.poll();
            }
            return -1;
        }
    }

    public static void main(String[] args) {
        DesignStockPrice stockPrice = new DesignStockPrice();
        stockPrice.update(1, 10);
        stockPrice.update(2, 5);
        System.out.println(stockPrice.current()); // Expected: 5
        System.out.println(stockPrice.maximum()); // Expected: 10
        stockPrice.update(1, 3);
        System.out.println(stockPrice.maximum()); // Expected: 5
        stockPrice.update(4, 2);
        System.out.println(stockPrice.minimum()); // Expected: 2

        // Test alternative implementation
        StockPriceAlternative alt = new StockPriceAlternative();
        alt.update(1, 10);
        alt.update(2, 5);
        System.out.println("Alt current: " + alt.current()); // Expected: 5
        System.out.println("Alt maximum: " + alt.maximum()); // Expected: 10
    }
}
