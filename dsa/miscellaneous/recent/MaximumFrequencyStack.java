package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Maximum Frequency Stack with Time Complexity Optimization
 * 
 * Description:
 * Design a stack-like data structure that supports push and pop operations,
 * where pop returns the most frequent element. If tie, return the most recent
 * one.
 * 
 * Companies: Google, Amazon, Facebook
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class MaximumFrequencyStack {

    class FreqStack {
        private Map<Integer, Integer> freq;
        private Map<Integer, Stack<Integer>> freqToStack;
        private int maxFreq;

        public FreqStack() {
            freq = new HashMap<>();
            freqToStack = new HashMap<>();
            maxFreq = 0;
        }

        public void push(int val) {
            int currentFreq = freq.getOrDefault(val, 0) + 1;
            freq.put(val, currentFreq);

            freqToStack.computeIfAbsent(currentFreq, k -> new Stack<>()).push(val);
            maxFreq = Math.max(maxFreq, currentFreq);
        }

        public int pop() {
            if (maxFreq == 0)
                return -1;

            Stack<Integer> stack = freqToStack.get(maxFreq);
            int val = stack.pop();

            freq.put(val, freq.get(val) - 1);

            if (stack.isEmpty()) {
                maxFreq--;
            }

            return val;
        }

        public int peek() {
            if (maxFreq == 0)
                return -1;
            return freqToStack.get(maxFreq).peek();
        }

        public boolean isEmpty() {
            return maxFreq == 0;
        }
    }

    // Alternative implementation with priority queue
    class FreqStackPQ {
        private Map<Integer, Integer> freq;
        private PriorityQueue<int[]> pq; // [value, frequency, timestamp]
        private int timestamp;

        public FreqStackPQ() {
            freq = new HashMap<>();
            pq = new PriorityQueue<>((a, b) -> {
                if (a[1] != b[1])
                    return b[1] - a[1]; // Higher frequency first
                return b[2] - a[2]; // More recent first
            });
            timestamp = 0;
        }

        public void push(int val) {
            int currentFreq = freq.getOrDefault(val, 0) + 1;
            freq.put(val, currentFreq);
            pq.offer(new int[] { val, currentFreq, timestamp++ });
        }

        public int pop() {
            while (!pq.isEmpty()) {
                int[] top = pq.poll();
                int val = top[0];
                int freqAtTime = top[1];

                if (freq.get(val) == freqAtTime) {
                    freq.put(val, freq.get(val) - 1);
                    if (freq.get(val) == 0) {
                        freq.remove(val);
                    }
                    return val;
                }
            }
            return -1;
        }
    }

    public static void main(String[] args) {
        MaximumFrequencyStack solution = new MaximumFrequencyStack();

        FreqStack freqStack = solution.new FreqStack();

        freqStack.push(5);
        freqStack.push(7);
        freqStack.push(5);
        freqStack.push(7);
        freqStack.push(4);
        freqStack.push(5);

        System.out.println(freqStack.pop()); // 5 (freq=3)
        System.out.println(freqStack.pop()); // 7 (freq=2)
        System.out.println(freqStack.pop()); // 5 (freq=2)
        System.out.println(freqStack.pop()); // 4 (freq=1)
    }
}
