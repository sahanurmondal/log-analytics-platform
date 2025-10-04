package linkedlist.easy;

import java.util.*;

/**
 * LeetCode 1290: Convert Binary Number in a Linked List to Integer
 * https://leetcode.com/problems/convert-binary-number-in-a-linked-list-to-integer/
 * 
 * Companies: Amazon, Google, Microsoft, Meta, Apple, Adobe
 * Frequency: Medium (Asked in 300+ interviews)
 *
 * Description:
 * Given head which is a reference node to a singly-linked list. The value of
 * each
 * node in the linked list is either 0 or 1. The linked list holds the binary
 * representation of a number.
 *
 * Return the decimal value of the number in the linked list.
 *
 * Constraints:
 * - The Linked List is not empty.
 * - Number of nodes will not exceed 30.
 * - Each node's value is either 0 or 1.
 * 
 * Follow-up Questions:
 * 1. What if the binary number is very large (beyond int/long)?
 * 2. Can you convert decimal back to binary linked list?
 * 3. How to handle different bases (not just binary)?
 * 4. What about arithmetic operations on binary linked lists?
 * 5. Can you implement without calculating the decimal value?
 */
public class ConvertBinaryNumberToInteger {

    // Definition for singly-linked list
    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            ListNode current = this;
            while (current != null) {
                sb.append(current.val);
                if (current.next != null) {
                    sb.append(" -> ");
                }
                current = current.next;
            }
            return sb.toString();
        }
    }

    // Approach 1: Bit shifting - O(n) time, O(1) space
    public int getDecimalValue(ListNode head) {
        int result = 0;

        while (head != null) {
            result = (result << 1) + head.val;
            head = head.next;
        }

        return result;
    }

    // Approach 2: Using powers of 2 - O(n) time, O(1) space
    public int getDecimalValuePower(ListNode head) {
        int length = getLength(head);
        int result = 0;
        int power = length - 1;

        while (head != null) {
            if (head.val == 1) {
                result += Math.pow(2, power);
            }
            power--;
            head = head.next;
        }

        return result;
    }

    // Approach 3: Recursive - O(n) time, O(n) space
    public int getDecimalValueRecursive(ListNode head) {
        int length = getLength(head);
        return getDecimalHelper(head, length - 1);
    }

    private int getDecimalHelper(ListNode head, int power) {
        if (head == null) {
            return 0;
        }

        int currentValue = head.val * (1 << power);
        return currentValue + getDecimalHelper(head.next, power - 1);
    }

    // Approach 4: Using StringBuilder and Integer.parseInt - O(n) time, O(n) space
    public int getDecimalValueString(ListNode head) {
        StringBuilder binary = new StringBuilder();

        while (head != null) {
            binary.append(head.val);
            head = head.next;
        }

        return Integer.parseInt(binary.toString(), 2);
    }

    // Approach 5: Two-pass (calculate length first) - O(n) time, O(1) space
    public int getDecimalValueTwoPass(ListNode head) {
        int length = getLength(head);
        int result = 0;
        ListNode current = head;

        for (int i = length - 1; i >= 0; i--) {
            if (current.val == 1) {
                result |= (1 << i);
            }
            current = current.next;
        }

        return result;
    }

    // Follow-up 1: Handle very large binary numbers (using BigInteger)
    public java.math.BigInteger getDecimalValueBig(ListNode head) {
        StringBuilder binary = new StringBuilder();

        while (head != null) {
            binary.append(head.val);
            head = head.next;
        }

        return new java.math.BigInteger(binary.toString(), 2);
    }

    // Follow-up 1: Handle very large binary numbers (using bit manipulation with
    // long)
    public long getDecimalValueLong(ListNode head) {
        long result = 0;

        while (head != null) {
            result = (result << 1) + head.val;
            head = head.next;
        }

        return result;
    }

    // Follow-up 2: Convert decimal back to binary linked list
    public ListNode decimalToBinary(int decimal) {
        if (decimal == 0) {
            return new ListNode(0);
        }

        // Convert to binary string
        String binary = Integer.toBinaryString(decimal);

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (char bit : binary.toCharArray()) {
            current.next = new ListNode(bit - '0');
            current = current.next;
        }

        return dummy.next;
    }

    // Follow-up 2: Convert decimal to binary linked list (bit manipulation)
    public ListNode decimalToBinaryBitwise(int decimal) {
        if (decimal == 0) {
            return new ListNode(0);
        }

        List<Integer> bits = new ArrayList<>();

        while (decimal > 0) {
            bits.add(decimal & 1);
            decimal >>= 1;
        }

        // Reverse to get MSB first
        Collections.reverse(bits);

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (int bit : bits) {
            current.next = new ListNode(bit);
            current = current.next;
        }

        return dummy.next;
    }

    // Follow-up 3: Convert from any base
    public int getValueFromBase(ListNode head, int base) {
        int result = 0;

        while (head != null) {
            result = result * base + head.val;
            head = head.next;
        }

        return result;
    }

    // Follow-up 3: Convert decimal to any base linked list
    public ListNode decimalToBase(int decimal, int base) {
        if (decimal == 0) {
            return new ListNode(0);
        }

        List<Integer> digits = new ArrayList<>();

        while (decimal > 0) {
            digits.add(decimal % base);
            decimal /= base;
        }

        // Reverse to get most significant digit first
        Collections.reverse(digits);

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (int digit : digits) {
            current.next = new ListNode(digit);
            current = current.next;
        }

        return dummy.next;
    }

    // Follow-up 4: Add two binary numbers represented as linked lists
    public ListNode addBinary(ListNode l1, ListNode l2) {
        // Convert to decimal, add, then convert back
        int num1 = getDecimalValue(l1);
        int num2 = getDecimalValue(l2);
        int sum = num1 + num2;

        return decimalToBinary(sum);
    }

    // Follow-up 4: Add binary numbers directly (bit by bit)
    public ListNode addBinaryDirect(ListNode l1, ListNode l2) {
        // Reverse both lists to start from LSB
        ListNode rev1 = reverseList(l1);
        ListNode rev2 = reverseList(l2);

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        int carry = 0;

        while (rev1 != null || rev2 != null || carry > 0) {
            int sum = carry;

            if (rev1 != null) {
                sum += rev1.val;
                rev1 = rev1.next;
            }

            if (rev2 != null) {
                sum += rev2.val;
                rev2 = rev2.next;
            }

            current.next = new ListNode(sum % 2);
            carry = sum / 2;
            current = current.next;
        }

        // Reverse result to get MSB first
        return reverseList(dummy.next);
    }

    // Follow-up 4: Multiply two binary numbers
    public ListNode multiplyBinary(ListNode l1, ListNode l2) {
        int num1 = getDecimalValue(l1);
        int num2 = getDecimalValue(l2);
        int product = num1 * num2;

        return decimalToBinary(product);
    }

    // Follow-up 5: Compare two binary numbers without converting to decimal
    public int compareBinary(ListNode l1, ListNode l2) {
        int len1 = getLength(l1);
        int len2 = getLength(l2);

        // Different lengths
        if (len1 != len2) {
            return Integer.compare(len1, len2);
        }

        // Same length, compare bit by bit
        while (l1 != null && l2 != null) {
            if (l1.val != l2.val) {
                return Integer.compare(l1.val, l2.val);
            }
            l1 = l1.next;
            l2 = l2.next;
        }

        return 0; // Equal
    }

    // Advanced: Binary increment operation
    public ListNode incrementBinary(ListNode head) {
        int decimal = getDecimalValue(head);
        return decimalToBinary(decimal + 1);
    }

    // Advanced: Binary increment without conversion
    public ListNode incrementBinaryDirect(ListNode head) {
        ListNode reversed = reverseList(head);
        ListNode current = reversed;
        int carry = 1;

        while (current != null && carry > 0) {
            int sum = current.val + carry;
            current.val = sum % 2;
            carry = sum / 2;

            if (carry == 0)
                break;

            if (current.next == null && carry > 0) {
                current.next = new ListNode(carry);
                carry = 0;
            }

            current = current.next;
        }

        return reverseList(reversed);
    }

    // Advanced: Binary left shift (multiply by 2^k)
    public ListNode leftShift(ListNode head, int k) {
        int decimal = getDecimalValue(head);
        int shifted = decimal << k;
        return decimalToBinary(shifted);
    }

    // Advanced: Binary right shift (divide by 2^k)
    public ListNode rightShift(ListNode head, int k) {
        int decimal = getDecimalValue(head);
        int shifted = decimal >> k;
        return decimalToBinary(shifted);
    }

    // Helper methods
    private int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }

    private ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        return prev;
    }

    // Helper: Create binary linked list from string
    public static ListNode createBinaryList(String binary) {
        if (binary == null || binary.isEmpty()) {
            return null;
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (char bit : binary.toCharArray()) {
            if (bit == '0' || bit == '1') {
                current.next = new ListNode(bit - '0');
                current = current.next;
            }
        }

        return dummy.next;
    }

    // Helper: Create binary linked list from array
    public static ListNode createBinaryList(int[] bits) {
        if (bits == null || bits.length == 0) {
            return null;
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (int bit : bits) {
            current.next = new ListNode(bit);
            current = current.next;
        }

        return dummy.next;
    }

    // Helper: Convert linked list to binary string
    public String toBinaryString(ListNode head) {
        StringBuilder sb = new StringBuilder();

        while (head != null) {
            sb.append(head.val);
            head = head.next;
        }

        return sb.toString();
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(ListNode head) {
        Map<String, Long> results = new HashMap<>();

        // Test bit shifting approach
        long start = System.nanoTime();
        getDecimalValue(cloneList(head));
        results.put("BitShift", System.nanoTime() - start);

        // Test power approach
        start = System.nanoTime();
        getDecimalValuePower(cloneList(head));
        results.put("Power", System.nanoTime() - start);

        // Test recursive approach
        start = System.nanoTime();
        getDecimalValueRecursive(cloneList(head));
        results.put("Recursive", System.nanoTime() - start);

        // Test string approach
        start = System.nanoTime();
        getDecimalValueString(cloneList(head));
        results.put("String", System.nanoTime() - start);

        // Test two-pass approach
        start = System.nanoTime();
        getDecimalValueTwoPass(cloneList(head));
        results.put("TwoPass", System.nanoTime() - start);

        return results;
    }

    private ListNode cloneList(ListNode head) {
        if (head == null)
            return null;

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (head != null) {
            current.next = new ListNode(head.val);
            current = current.next;
            head = head.next;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        ConvertBinaryNumberToInteger solution = new ConvertBinaryNumberToInteger();

        // Test Case 1: Standard examples
        System.out.println("=== Test Case 1: Standard Examples ===");

        String[] binaryStrings = { "101", "0", "1", "1101", "10101" };

        for (String binary : binaryStrings) {
            ListNode head = createBinaryList(binary);
            int result = solution.getDecimalValue(head);
            int expected = Integer.parseInt(binary, 2);

            System.out.println("Binary: " + binary + " -> Decimal: " + result +
                    " (Expected: " + expected + ")");
        }

        // Test Case 2: Compare all approaches
        System.out.println("\n=== Test Case 2: Compare All Approaches ===");
        ListNode testHead = createBinaryList("1101");

        int bitShift = solution.getDecimalValue(testHead);
        int power = solution.getDecimalValuePower(testHead);
        int recursive = solution.getDecimalValueRecursive(testHead);
        int string = solution.getDecimalValueString(testHead);
        int twoPass = solution.getDecimalValueTwoPass(testHead);

        System.out.println("Binary: " + solution.toBinaryString(testHead));
        System.out.println("Bit Shift: " + bitShift);
        System.out.println("Power: " + power);
        System.out.println("Recursive: " + recursive);
        System.out.println("String: " + string);
        System.out.println("Two Pass: " + twoPass);

        boolean allSame = bitShift == power && power == recursive &&
                recursive == string && string == twoPass;
        System.out.println("All approaches consistent: " + allSame);

        // Follow-up 1: Large numbers
        System.out.println("\n=== Follow-up 1: Large Numbers ===");

        // Create a 30-bit number (near maximum constraint)
        StringBuilder largeBinary = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            largeBinary.append(i % 2);
        }

        ListNode largeHead = createBinaryList(largeBinary.toString());
        long largeLong = solution.getDecimalValueLong(largeHead);
        java.math.BigInteger largeBig = solution.getDecimalValueBig(largeHead);

        System.out.println("30-bit binary: " + largeBinary.toString());
        System.out.println("As long: " + largeLong);
        System.out.println("As BigInteger: " + largeBig);

        // Follow-up 2: Decimal to binary conversion
        System.out.println("\n=== Follow-up 2: Decimal to Binary ===");

        int[] decimals = { 5, 13, 0, 1, 255 };

        for (int decimal : decimals) {
            ListNode binaryList = solution.decimalToBinary(decimal);
            ListNode bitwiseList = solution.decimalToBinaryBitwise(decimal);

            String binaryStr = solution.toBinaryString(binaryList);
            String bitwiseStr = solution.toBinaryString(bitwiseList);

            System.out.println("Decimal " + decimal + " -> Binary: " + binaryStr +
                    " (Bitwise: " + bitwiseStr + ")");

            // Verify round-trip conversion
            int backToDecimal = solution.getDecimalValue(binaryList);
            System.out.println("Round-trip verification: " + (decimal == backToDecimal));
        }

        // Follow-up 3: Different bases
        System.out.println("\n=== Follow-up 3: Different Bases ===");

        // Base 8 (octal)
        ListNode octalList = createBinaryList(new int[] { 1, 2, 3 }); // 123 in base 8
        int octalValue = solution.getValueFromBase(octalList, 8);
        System.out.println("123 in base 8 = " + octalValue + " in decimal");

        // Convert decimal back to base 8
        ListNode backToOctal = solution.decimalToBase(octalValue, 8);
        System.out.println("Back to base 8: " + solution.toBinaryString(backToOctal));

        // Base 16 (hexadecimal)
        ListNode hexList = createBinaryList(new int[] { 1, 10, 15 }); // 1AF in base 16
        int hexValue = solution.getValueFromBase(hexList, 16);
        System.out.println("1,10,15 in base 16 = " + hexValue + " in decimal");

        // Follow-up 4: Binary arithmetic
        System.out.println("\n=== Follow-up 4: Binary Arithmetic ===");

        ListNode bin1 = createBinaryList("101"); // 5
        ListNode bin2 = createBinaryList("110"); // 6

        ListNode sum = solution.addBinary(bin1, bin2);
        ListNode directSum = solution.addBinaryDirect(bin1, bin2);
        ListNode product = solution.multiplyBinary(bin1, bin2);

        System.out.println("101 + 110 = " + solution.toBinaryString(sum));
        System.out.println("101 + 110 (direct) = " + solution.toBinaryString(directSum));
        System.out.println("101 * 110 = " + solution.toBinaryString(product));

        // Verify arithmetic
        int val1 = solution.getDecimalValue(bin1);
        int val2 = solution.getDecimalValue(bin2);
        int sumVal = solution.getDecimalValue(sum);
        int prodVal = solution.getDecimalValue(product);

        System.out.println("Verification: " + val1 + " + " + val2 + " = " + sumVal +
                " (" + (val1 + val2 == sumVal) + ")");
        System.out.println("Verification: " + val1 + " * " + val2 + " = " + prodVal +
                " (" + (val1 * val2 == prodVal) + ")");

        // Follow-up 5: Compare without conversion
        System.out.println("\n=== Follow-up 5: Compare Without Conversion ===");

        ListNode comp1 = createBinaryList("101");
        ListNode comp2 = createBinaryList("110");
        ListNode comp3 = createBinaryList("101");

        int cmp1 = solution.compareBinary(comp1, comp2);
        int cmp2 = solution.compareBinary(comp1, comp3);

        System.out.println("Compare 101 vs 110: " + cmp1 + " (negative means first < second)");
        System.out.println("Compare 101 vs 101: " + cmp2 + " (zero means equal)");

        // Advanced: Binary operations
        System.out.println("\n=== Advanced: Binary Operations ===");

        ListNode original = createBinaryList("1010"); // 10

        ListNode incremented = solution.incrementBinary(original);
        ListNode incrementedDirect = solution.incrementBinaryDirect(createBinaryList("1010"));
        ListNode leftShifted = solution.leftShift(original, 2);
        ListNode rightShifted = solution.rightShift(original, 1);

        System.out.println("Original: " + solution.toBinaryString(original) + " (" +
                solution.getDecimalValue(original) + ")");
        System.out.println("Incremented: " + solution.toBinaryString(incremented) + " (" +
                solution.getDecimalValue(incremented) + ")");
        System.out.println("Incremented (direct): " + solution.toBinaryString(incrementedDirect) + " (" +
                solution.getDecimalValue(incrementedDirect) + ")");
        System.out.println("Left shift 2: " + solution.toBinaryString(leftShifted) + " (" +
                solution.getDecimalValue(leftShifted) + ")");
        System.out.println("Right shift 1: " + solution.toBinaryString(rightShifted) + " (" +
                solution.getDecimalValue(rightShifted) + ")");

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");

        ListNode performanceHead = createBinaryList("101010101010101010101010101010");
        Map<String, Long> performance = solution.comparePerformance(performanceHead);

        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Single bit
        ListNode singleZero = createBinaryList("0");
        ListNode singleOne = createBinaryList("1");

        System.out.println("Single 0: " + solution.getDecimalValue(singleZero));
        System.out.println("Single 1: " + solution.getDecimalValue(singleOne));

        // All zeros
        ListNode allZeros = createBinaryList("0000");
        System.out.println("All zeros: " + solution.getDecimalValue(allZeros));

        // All ones
        ListNode allOnes = createBinaryList("1111");
        System.out.println("All ones: " + solution.getDecimalValue(allOnes));

        // Maximum 30-bit number
        String maxBinary = "1".repeat(30);
        ListNode maxHead = createBinaryList(maxBinary);
        long maxValue = solution.getDecimalValueLong(maxHead);
        System.out.println("Maximum 30-bit: " + maxValue);

        System.out.println("\nBinary Number Conversion testing completed successfully!");
    }
}
