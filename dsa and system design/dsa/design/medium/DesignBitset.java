package design.medium;

/**
 * LeetCode 2166: Design Bitset
 * https://leetcode.com/problems/design-bitset/
 *
 * Description: A Bitset is a data structure that compactly stores bits.
 * 
 * Constraints:
 * - 1 <= size <= 10^5
 * - 0 <= idx < size
 * - At most 10^5 calls will be made in total to fix, unfix, flip, all, one,
 * count, and toString
 *
 * Follow-up:
 * - Can you optimize flip operation to O(1)?
 * 
 * Time Complexity: O(1) for all operations except toString which is O(n)
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class DesignBitset {

    private boolean[] bits;
    private int size;
    private int oneCount;
    private boolean flipped;

    public DesignBitset(int size) {
        this.size = size;
        this.bits = new boolean[size];
        this.oneCount = 0;
        this.flipped = false;
    }

    public void fix(int idx) {
        boolean actualValue = bits[idx] ^ flipped;
        if (!actualValue) {
            bits[idx] = !bits[idx];
            oneCount++;
        }
    }

    public void unfix(int idx) {
        boolean actualValue = bits[idx] ^ flipped;
        if (actualValue) {
            bits[idx] = !bits[idx];
            oneCount--;
        }
    }

    public void flip() {
        flipped = !flipped;
        oneCount = size - oneCount;
    }

    public boolean all() {
        return oneCount == size;
    }

    public boolean one() {
        return oneCount > 0;
    }

    public int count() {
        return oneCount;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            boolean actualValue = bits[i] ^ flipped;
            sb.append(actualValue ? '1' : '0');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DesignBitset bs = new DesignBitset(5);
        bs.fix(3);
        bs.fix(1);
        bs.flip();
        System.out.println(bs.all()); // Expected: false
        bs.unfix(0);
        bs.flip();
        System.out.println(bs.one()); // Expected: true
        bs.unfix(0);
        System.out.println(bs.count()); // Expected: 2
        System.out.println(bs.toString()); // Expected: "01010"
    }
}
