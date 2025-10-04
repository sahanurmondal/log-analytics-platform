package design.hard;

import java.util.*;

/**
 * LeetCode 631: Design Excel Sum Formula
 * https://leetcode.com/problems/design-excel-sum-formula/
 *
 * Description: Design the basic function of Excel and implement the function of
 * the sum formula.
 * 
 * Constraints:
 * - 1 <= height <= 26
 * - 'A' <= width <= 'Z'
 * - 1 <= row <= height
 * - 'A' <= column <= width
 * - -100 <= val <= 100
 * - 1 <= strs.length <= 100
 * - strs[i] consists of digits, uppercase English letters, and ':'
 * - At most 100 calls will be made to each function
 *
 * Follow-up:
 * - Can you handle circular dependencies?
 * 
 * Time Complexity: O(number of dependent cells) for sum operations
 * Space Complexity: O(H * W + dependencies)
 * 
 * Company Tags: Google
 */
public class Excel {

    class Cell {
        int value;
        Set<String> formula;

        Cell(int value) {
            this.value = value;
            this.formula = new HashSet<>();
        }
    }

    private Cell[][] sheet;
    private Map<String, Set<String>> dependents; // cell -> cells that depend on it
    private int height;
    private char width;

    public Excel(int height, char width) {
        this.height = height;
        this.width = width;
        this.sheet = new Cell[height + 1][width - 'A' + 1];
        this.dependents = new HashMap<>();

        // Initialize all cells with 0
        for (int r = 1; r <= height; r++) {
            for (int c = 0; c <= width - 'A'; c++) {
                sheet[r][c] = new Cell(0);
            }
        }
    }

    public void set(int row, char column, int val) {
        String cellKey = column + String.valueOf(row);
        Cell cell = sheet[row][column - 'A'];

        // Clear previous formula
        clearFormula(cellKey);

        cell.value = val;
        cell.formula.clear();

        // Update dependent cells
        updateDependents(cellKey);
    }

    public int get(int row, char column) {
        return sheet[row][column - 'A'].value;
    }

    public int sum(int row, char column, String[] strs) {
        String cellKey = column + String.valueOf(row);
        Cell cell = sheet[row][column - 'A'];

        // Clear previous formula
        clearFormula(cellKey);

        // Parse new formula
        Set<String> newFormula = new HashSet<>();
        for (String str : strs) {
            if (str.contains(":")) {
                // Range like "A1:B2"
                String[] parts = str.split(":");
                String start = parts[0];
                String end = parts[1];

                char startCol = start.charAt(0);
                int startRow = Integer.parseInt(start.substring(1));
                char endCol = end.charAt(0);
                int endRow = Integer.parseInt(end.substring(1));

                for (char c = startCol; c <= endCol; c++) {
                    for (int r = startRow; r <= endRow; r++) {
                        String refCell = c + String.valueOf(r);
                        newFormula.add(refCell);
                        dependents.computeIfAbsent(refCell, k -> new HashSet<>()).add(cellKey);
                    }
                }
            } else {
                // Single cell like "A1"
                newFormula.add(str);
                dependents.computeIfAbsent(str, k -> new HashSet<>()).add(cellKey);
            }
        }

        cell.formula = newFormula;
        calculateValue(cellKey);

        return cell.value;
    }

    private void clearFormula(String cellKey) {
        Cell cell = sheet[Integer.parseInt(cellKey.substring(1))][cellKey.charAt(0) - 'A'];

        for (String refCell : cell.formula) {
            Set<String> deps = dependents.get(refCell);
            if (deps != null) {
                deps.remove(cellKey);
                if (deps.isEmpty()) {
                    dependents.remove(refCell);
                }
            }
        }
    }

    private void calculateValue(String cellKey) {
        int row = Integer.parseInt(cellKey.substring(1));
        char col = cellKey.charAt(0);
        Cell cell = sheet[row][col - 'A'];

        if (cell.formula.isEmpty()) {
            return; // Value was set directly
        }

        int sum = 0;
        for (String refCell : cell.formula) {
            int refRow = Integer.parseInt(refCell.substring(1));
            char refCol = refCell.charAt(0);
            sum += sheet[refRow][refCol - 'A'].value;
        }

        cell.value = sum;
    }

    private void updateDependents(String cellKey) {
        Set<String> deps = dependents.get(cellKey);
        if (deps == null)
            return;

        for (String depCell : deps) {
            calculateValue(depCell);
            updateDependents(depCell); // Recursive update
        }
    }

    public static void main(String[] args) {
        Excel excel = new Excel(3, 'C');
        excel.set(1, 'A', 2);
        System.out.println(excel.sum(3, 'C', new String[] { "A1", "A1:B2" })); // Expected: 2
        excel.set(2, 'B', 2);
        System.out.println(excel.get(3, 'C')); // Expected: 4
    }
}
