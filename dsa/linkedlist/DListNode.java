package linkedlist;

public class DListNode {
    public int val;
    public DListNode prev, next;

    public DListNode() {
    }

    public DListNode(int val) {
        this.val = val;
    }

    public DListNode(int val, DListNode next) {
        this.val = val;
        this.next = next;
        if (next != null) {
            next.prev = this;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DListNode curr = this;
        while (curr != null) {
            sb.append(curr.val);
            if (curr.next != null) {
                sb.append("<->");
            }
            curr = curr.next;
        }
        return sb.toString();
    }
}
