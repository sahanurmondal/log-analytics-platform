package design.medium;

import java.util.*;

/**
 * LeetCode 1472: Design Browser History
 * https://leetcode.com/problems/design-browser-history/
 *
 * Description: You have a browser of one tab where you start on the homepage
 * and you can visit another url, get back in the history number of steps or
 * move forward in the history number of steps.
 * 
 * Constraints:
 * - 1 <= homepage.length <= 20
 * - 1 <= url.length <= 20
 * - 1 <= steps <= 100
 * - homepage and url consist of '.' or lower case English letters
 * - At most 5000 calls will be made to visit, back, and forward
 *
 * Follow-up:
 * - Can you implement this using a stack or array?
 * 
 * Time Complexity: O(1) for visit, O(min(steps, n)) for back/forward
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class BrowserHistory {

    private List<String> history;
    private int currentIndex;

    public BrowserHistory(String homepage) {
        history = new ArrayList<>();
        history.add(homepage);
        currentIndex = 0;
    }

    public void visit(String url) {
        // Clear forward history
        while (history.size() > currentIndex + 1) {
            history.remove(history.size() - 1);
        }

        history.add(url);
        currentIndex++;
    }

    public String back(int steps) {
        currentIndex = Math.max(0, currentIndex - steps);
        return history.get(currentIndex);
    }

    public String forward(int steps) {
        currentIndex = Math.min(history.size() - 1, currentIndex + steps);
        return history.get(currentIndex);
    }

    // Alternative implementation using doubly linked list
    static class BrowserHistoryLinkedList {
        class Node {
            String url;
            Node prev, next;

            Node(String url) {
                this.url = url;
            }
        }

        private Node current;

        public BrowserHistoryLinkedList(String homepage) {
            current = new Node(homepage);
        }

        public void visit(String url) {
            Node newNode = new Node(url);
            current.next = newNode;
            newNode.prev = current;
            current = newNode;
        }

        public String back(int steps) {
            while (steps > 0 && current.prev != null) {
                current = current.prev;
                steps--;
            }
            return current.url;
        }

        public String forward(int steps) {
            while (steps > 0 && current.next != null) {
                current = current.next;
                steps--;
            }
            return current.url;
        }
    }

    public static void main(String[] args) {
        BrowserHistory browserHistory = new BrowserHistory("leetcode.com");
        browserHistory.visit("google.com");
        browserHistory.visit("facebook.com");
        browserHistory.visit("youtube.com");
        System.out.println(browserHistory.back(1)); // Expected: "facebook.com"
        System.out.println(browserHistory.back(1)); // Expected: "google.com"
        System.out.println(browserHistory.forward(1)); // Expected: "facebook.com"
        browserHistory.visit("linkedin.com");
        System.out.println(browserHistory.forward(2)); // Expected: "linkedin.com"
        System.out.println(browserHistory.back(2)); // Expected: "google.com"
        System.out.println(browserHistory.back(7)); // Expected: "leetcode.com"
    }
}
