package arrays.easy;

/**
 * LeetCode 118 / 119: Pascal's Triangle & Pascal's Triangle II
 * https://leetcode.com/problems/pascals-triangle/
 * https://leetcode.com/problems/pascals-triangle-ii/
 *
 * Problem summary:
 * - generate(numRows): return the first numRows rows of Pascal's triangle.
 * - getRow(rowIndex): return the rowIndex-th (0-indexed) row of Pascal's
 * triangle.
 *
 * Input / Output examples:
 * - generate(1) -> [[1]]
 * - generate(2) -> [[1], [1,1]]
 * - generate(5) -> [[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]
 *
 * - getRow(0) -> [1]
 * - getRow(1) -> [1,1]
 * - getRow(3) -> [1,3,3,1]
 * - getRow(5) -> [1,5,10,10,5,1]
 *
 * Follow-ups / discussion points:
 * 1) Can you compute a single row in O(k) time and O(1) extra space
 * (output-only)?
 * - Use iterative in-place updates from end to start (this file implements O(k)
 * space).
 * 2) Can you compute the k-th row using combinatorics (nCr) in O(k) time?
 * - Yes — compute successive binomial coefficients: C(k,0)=1 and
 * C(k,i)=C(k,i-1)*(k-i+1)/i.
 * - Watch out for overflow: use long or BigInteger for large k or modular
 * arithmetic for modulo answers.
 * 3) Streaming generation: generate rows lazily (Iterator/Stream) to avoid
 * storing all rows.
 * 4) Memory trade-offs: generate only required rows when numRows is large.
 *
 * Constraints reference: 1 <= numRows <= 30 (LeetCode), typical interview
 * constraints may be larger.
 *
 * Time Complexity: O(numRows^2) to generate all rows
 * Space Complexity: O(numRows^2) for the result (generate), O(k) for getRow
 */

public class PascalsTriangle {
    /**
     * Generate the first numRows of Pascal's triangle.
     *
     * Input: numRows (int)
     * Output: List of rows (List<List<Integer>>) where each row is a list of
     * integers
     *
     * Example:
     * generate(5) -> [[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]
     *
     * Follow-ups:
     * - If numRows is extremely large, avoid storing all rows: return an
     * Iterator/Stream of rows.
     * - Use long/BigInteger for values if numbers may overflow int.
     */
    public java.util.List<java.util.List<Integer>> generate(int numRows) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        if (numRows <= 0)
            return res;

        java.util.List<Integer> row = new java.util.ArrayList<>();
        row.add(1);
        res.add(new java.util.ArrayList<>(row));

        for (int r = 1; r < numRows; r++) {
            java.util.List<Integer> prev = res.get(r - 1);
            java.util.List<Integer> cur = new java.util.ArrayList<>();
            cur.add(1);
            for (int i = 1; i < prev.size(); i++) {
                cur.add(prev.get(i - 1) + prev.get(i));
            }
            cur.add(1);
            res.add(cur);
        }

        return res;
    }

    /**
     * LeetCode 119: Pascal's Triangle II (get specific row)
     * Returns the rowIndex-th (0-indexed) row of Pascal's triangle.
     * Uses O(k) space by updating from the end.
     *
     * Input: rowIndex (0-indexed int)
     * Output: the rowIndex-th row as a List<Integer>
     *
     * Example:
     * getRow(3) -> [1,3,3,1]
     *
     * Follow-ups:
     * - Compute row using binomial coefficients in O(k) time and O(1) extra space
     * (besides output).
     * - Handle large rowIndex by using long/BigInteger for intermediate
     * multiplication/division.
     *
     * Time Complexity: O(k^2)
     * Space Complexity: O(k)
     */
    public java.util.List<Integer> getRow(int rowIndex) {
        java.util.List<Integer> row = new java.util.ArrayList<>();
        for (int i = 0; i <= rowIndex; i++) {
            row.add(0);
        }
        row.set(0, 1);

        for (int i = 1; i <= rowIndex; i++) {
            // update from end to start
            for (int j = i; j >= 1; j--) {
                int val = row.get(j) + row.get(j - 1);
                row.set(j, val);
            }
        }

        return row;
    }

    /**
     * Compute the k-th row using multiplicative binomial coefficient formula in
     * O(k) time.
     * Returns a list of longs to reduce immediate overflow for moderate k.
     *
     * Example: getRowBinomialLong(5) -> [1,5,10,10,5,1]
     */
    public java.util.List<Long> getRowBinomialLong(int rowIndex) {
        java.util.List<Long> row = new java.util.ArrayList<>();
        long c = 1L;
        row.add(c);
        for (int i = 1; i <= rowIndex; i++) {
            // c = c * (n - i + 1) / i
            c = c * (rowIndex - i + 1) / i;
            row.add(c);
        }
        return row;
    }

    /**
     * Compute the k-th row using BigInteger to avoid overflow for large indices.
     * Example: getRowBinomialBigInteger(50) -> big integer row
     */
    public java.util.List<java.math.BigInteger> getRowBinomialBigInteger(int rowIndex) {
        java.util.List<java.math.BigInteger> row = new java.util.ArrayList<>();
        java.math.BigInteger c = java.math.BigInteger.ONE;
        row.add(c);
        for (int i = 1; i <= rowIndex; i++) {
            c = c.multiply(java.math.BigInteger.valueOf(rowIndex - i + 1))
                    .divide(java.math.BigInteger.valueOf(i));
            row.add(c);
        }
        return row;
    }

    /**
     * Lazily generate rows as a Stream to avoid holding all rows in memory at once.
     * Useful when callers want to process rows one-by-one.
     */
    public java.util.stream.Stream<java.util.List<Integer>> streamGenerate(int numRows) {
        final java.util.Iterator<java.util.List<Integer>> it = new java.util.Iterator<>() {
            int produced = 0;
            java.util.List<Integer> prev = null;

            @Override
            public boolean hasNext() {
                return produced < numRows;
            }

            @Override
            public java.util.List<Integer> next() {
                if (prev == null) {
                    prev = java.util.Collections.singletonList(1);
                } else {
                    java.util.List<Integer> cur = new java.util.ArrayList<>();
                    cur.add(1);
                    for (int i = 1; i < prev.size(); i++) {
                        cur.add(prev.get(i - 1) + prev.get(i));
                    }
                    cur.add(1);
                    prev = cur;
                }
                produced++;
                return new java.util.ArrayList<>(prev);
            }
        };

        return java.util.stream.StreamSupport.stream(
                java.util.Spliterators.spliteratorUnknownSize(it, 0), false);
    }

    public static void main(String[] args) {
        PascalsTriangle sol = new PascalsTriangle();

        // Tests for generate()
        System.out.println("generate - zero rows -> " + sol.generate(0)); // [] edge-case: 0
        System.out.println("generate - one row  -> " + sol.generate(1)); // [[1]]
        System.out.println("generate - two rows -> " + sol.generate(2)); // [[1],[1,1]]
        System.out.println("generate - five rows -> " + sol.generate(5)); // standard
        System.out.println("generate - max 30 rows (size) -> " + sol.generate(30).size()); // stress

        // Tests for getRow() (in-place O(k^2) approach)
        System.out.println("getRow - row 0 -> " + sol.getRow(0)); // [1]
        System.out.println("getRow - row 1 -> " + sol.getRow(1)); // [1,1]
        System.out.println("getRow - row 3 -> " + sol.getRow(3)); // [1,3,3,1]
        System.out.println("getRow - row 5 -> " + sol.getRow(5)); // [1,5,10,10,5,1]

        // Tests for binomial (O(k) time)
        System.out.println("getRowBinomialLong - row 5 -> " + sol.getRowBinomialLong(5));
        System.out.println("getRowBinomialLong - row 30 -> " + sol.getRowBinomialLong(30));

        // BigInteger variant for large rows (demonstration)
        System.out.println("getRowBinomialBigInteger - row 50 -> " + sol.getRowBinomialBigInteger(50));

        // Stream generation: print first 5 rows with labels
        System.out.println("streamGenerate - first 5 rows:");
        sol.streamGenerate(5).forEach(r -> System.out.println("  row -> " + r));
    }
}
