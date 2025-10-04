package linkedlist;

public class RandomListNode {
    public int val;
    public RandomListNode next, random;

    public RandomListNode(int val) {
        this.val = val;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        RandomListNode curr = this;
        while (curr != null) {
            sb.append(curr.val).append("(").append(curr.random == null ? "null" : curr.random.val).append(")->");
            curr = curr.next;
        }
        return sb.append("null").toString();
    }
}
