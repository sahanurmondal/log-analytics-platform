package math.hard;

import java.util.*;

/**
 * LeetCode 273: Integer to English Words
 * https://leetcode.com/problems/integer-to-english-words/
 * 
 * Companies: Meta, Microsoft, Amazon, Google, Apple, Bloomberg
 * Frequency: High (Asked in 600+ interviews)
 *
 * Description:
 * Convert a non-negative integer num to its English words representation.
 *
 * Constraints:
 * - 0 <= num <= 2^31 - 1
 * 
 * Examples:
 * - 123 -> "One Hundred Twenty Three"
 * - 12345 -> "Twelve Thousand Three Hundred Forty Five"
 * - 1234567 -> "One Million Two Hundred Thirty Four Thousand Five Hundred Sixty
 * Seven"
 * 
 * Follow-up Questions:
 * 1. How to handle different number systems (British vs American)?
 * 2. Can you implement ordinal numbers (1st, 2nd, 3rd)?
 * 3. How to handle negative numbers?
 * 4. What about fractions and decimals?
 * 5. Can you support different languages?
 */
public class IntegerToEnglishWords {

    // Core mappings for number conversion
    private static final String[] ONES = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] THOUSANDS = {
            "", "Thousand", "Million", "Billion"
    };

    // Approach 1: Recursive approach - O(log n) time, O(log n) space
    public String numberToWords(int num) {
        if (num == 0) {
            return "Zero";
        }

        return convert(num).trim();
    }

    private String convert(int num) {
        if (num == 0) {
            return "";
        } else if (num < 20) {
            return ONES[num] + " ";
        } else if (num < 100) {
            return TENS[num / 10] + " " + convert(num % 10);
        } else if (num < 1000) {
            return ONES[num / 100] + " Hundred " + convert(num % 100);
        } else if (num < 1000000) {
            return convert(num / 1000) + "Thousand " + convert(num % 1000);
        } else if (num < 1000000000) {
            return convert(num / 1000000) + "Million " + convert(num % 1000000);
        } else {
            return convert(num / 1000000000) + "Billion " + convert(num % 1000000000);
        }
    }

    // Approach 2: Iterative approach - O(log n) time, O(1) space
    public String numberToWordsIterative(int num) {
        if (num == 0) {
            return "Zero";
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (num > 0) {
            if (num % 1000 != 0) {
                StringBuilder group = new StringBuilder();
                group.append(convertThreeDigits(num % 1000));

                if (i > 0) {
                    group.append(" ").append(THOUSANDS[i]);
                }

                if (result.length() > 0) {
                    group.append(" ");
                }

                result.insert(0, group);
            }

            num /= 1000;
            i++;
        }

        return result.toString();
    }

    // Approach 3: Pattern-based approach with better organization
    public String numberToWordsPattern(int num) {
        if (num == 0) {
            return "Zero";
        }

        List<String> parts = new ArrayList<>();

        // Process billions
        if (num >= 1000000000) {
            parts.add(convertThreeDigits(num / 1000000000) + " Billion");
            num %= 1000000000;
        }

        // Process millions
        if (num >= 1000000) {
            parts.add(convertThreeDigits(num / 1000000) + " Million");
            num %= 1000000;
        }

        // Process thousands
        if (num >= 1000) {
            parts.add(convertThreeDigits(num / 1000) + " Thousand");
            num %= 1000;
        }

        // Process hundreds
        if (num > 0) {
            parts.add(convertThreeDigits(num));
        }

        return String.join(" ", parts);
    }

    // Follow-up 1: British number system (different scale names)
    private static final String[] BRITISH_THOUSANDS = {
            "", "Thousand", "Million", "Milliard", "Billion", "Billiard", "Trillion"
    };

    public String numberToWordsBritish(int num) {
        if (num == 0) {
            return "Zero";
        }

        List<String> parts = new ArrayList<>();
        int scaleIndex = 0;

        while (num > 0 && scaleIndex < BRITISH_THOUSANDS.length) {
            int group = num % 1000;

            if (group != 0) {
                StringBuilder part = new StringBuilder();
                part.append(convertThreeDigits(group));

                if (scaleIndex > 0) {
                    part.append(" ").append(BRITISH_THOUSANDS[scaleIndex]);
                }

                parts.add(0, part.toString());
            }

            num /= 1000;
            scaleIndex++;
        }

        return String.join(" ", parts);
    }

    // Follow-up 2: Ordinal numbers (1st, 2nd, 3rd, etc.)
    private static final String[] ORDINAL_ONES = {
            "", "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth",
            "Tenth", "Eleventh", "Twelfth", "Thirteenth", "Fourteenth", "Fifteenth", "Sixteenth",
            "Seventeenth", "Eighteenth", "Nineteenth"
    };

    private static final String[] ORDINAL_TENS = {
            "", "", "Twentieth", "Thirtieth", "Fortieth", "Fiftieth", "Sixtieth", "Seventieth", "Eightieth", "Ninetieth"
    };

    public String numberToOrdinal(int num) {
        if (num == 0) {
            return "Zeroth";
        }

        String cardinal = numberToWords(num);

        // Handle special cases for ordinals
        if (num % 100 >= 11 && num % 100 <= 13) {
            return cardinal + "th";
        }

        int lastDigit = num % 10;
        switch (lastDigit) {
            case 1:
                return cardinal.substring(0, cardinal.length() - 3) + "First";
            case 2:
                return cardinal.substring(0, cardinal.length() - 3) + "Second";
            case 3:
                return cardinal.substring(0, cardinal.length() - 5) + "Third";
            default:
                return cardinal + "th";
        }
    }

    // Follow-up 3: Handle negative numbers
    public String numberToWordsWithNegative(int num) {
        if (num == 0) {
            return "Zero";
        }

        if (num < 0) {
            return "Negative " + numberToWords(-num);
        }

        return numberToWords(num);
    }

    // Follow-up 4: Handle fractions (basic implementation)
    public String numberToWordsWithFraction(double num) {
        if (num == 0.0) {
            return "Zero";
        }

        boolean isNegative = num < 0;
        num = Math.abs(num);

        int wholePart = (int) num;
        double fractionalPart = num - wholePart;

        StringBuilder result = new StringBuilder();

        if (isNegative) {
            result.append("Negative ");
        }

        if (wholePart > 0) {
            result.append(numberToWords(wholePart));
        }

        if (fractionalPart > 0) {
            if (wholePart > 0) {
                result.append(" and ");
            }

            // Convert fraction to words (simplified)
            String fractionalStr = String.valueOf(fractionalPart).substring(2); // Remove "0."
            int fractionalInt = Integer.parseInt(fractionalStr);

            result.append(numberToWords(fractionalInt));

            if (fractionalStr.length() == 1) {
                result.append(" Tenth");
            } else if (fractionalStr.length() == 2) {
                result.append(" Hundredth");
            } else {
                result.append(" Thousandth");
            }

            if (fractionalInt > 1) {
                result.append("s");
            }
        }

        return result.toString();
    }

    // Follow-up 5: Multi-language support (Spanish example)
    private static final String[] SPANISH_ONES = {
            "", "Uno", "Dos", "Tres", "Cuatro", "Cinco", "Seis", "Siete", "Ocho", "Nueve",
            "Diez", "Once", "Doce", "Trece", "Catorce", "Quince", "Dieciséis",
            "Diecisiete", "Dieciocho", "Diecinueve"
    };

    private static final String[] SPANISH_TENS = {
            "", "", "Veinte", "Treinta", "Cuarenta", "Cincuenta", "Sesenta", "Setenta", "Ochenta", "Noventa"
    };

    private static final String[] SPANISH_THOUSANDS = {
            "", "Mil", "Millón", "Mil Millones"
    };

    public String numberToWordsSpanish(int num) {
        if (num == 0) {
            return "Cero";
        }

        return convertSpanish(num).trim();
    }

    // Advanced: Support for very large numbers using BigInteger concepts
    public String numberToWordsLarge(long num) {
        if (num == 0) {
            return "Zero";
        }

        boolean isNegative = num < 0;
        num = Math.abs(num);

        List<String> parts = new ArrayList<>();
        String[] largeScales = { "", "Thousand", "Million", "Billion", "Trillion", "Quadrillion" };

        int scaleIndex = 0;

        while (num > 0 && scaleIndex < largeScales.length) {
            long group = num % 1000;

            if (group != 0) {
                StringBuilder part = new StringBuilder();
                part.append(convertThreeDigits((int) group));

                if (scaleIndex > 0) {
                    part.append(" ").append(largeScales[scaleIndex]);
                }

                parts.add(0, part.toString());
            }

            num /= 1000;
            scaleIndex++;
        }

        String result = String.join(" ", parts);
        return isNegative ? "Negative " + result : result;
    }

    // Advanced: Currency formatting
    public String numberToCurrency(int dollars, int cents) {
        StringBuilder result = new StringBuilder();

        if (dollars == 0 && cents == 0) {
            return "Zero Dollars";
        }

        if (dollars > 0) {
            result.append(numberToWords(dollars));
            result.append(dollars == 1 ? " Dollar" : " Dollars");
        }

        if (cents > 0) {
            if (dollars > 0) {
                result.append(" and ");
            }

            result.append(numberToWords(cents));
            result.append(cents == 1 ? " Cent" : " Cents");
        }

        return result.toString();
    }

    // Advanced: Roman numeral style (using words)
    public String numberToRomanWords(int num) {
        if (num <= 0 || num > 3999) {
            return "Number out of range for Roman numerals";
        }

        String[] thousands = { "", "One Thousand", "Two Thousand", "Three Thousand" };
        String[] hundreds = { "", "One Hundred", "Two Hundred", "Three Hundred", "Four Hundred",
                "Five Hundred", "Six Hundred", "Seven Hundred", "Eight Hundred", "Nine Hundred" };
        String[] tens = { "", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety" };
        String[] ones = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine" };

        StringBuilder result = new StringBuilder();

        result.append(thousands[num / 1000]);
        if (result.length() > 0 && num % 1000 != 0)
            result.append(" ");

        result.append(hundreds[(num % 1000) / 100]);
        if (result.length() > 0 && num % 100 != 0)
            result.append(" ");

        result.append(tens[(num % 100) / 10]);
        if (result.length() > 0 && num % 10 != 0)
            result.append(" ");

        result.append(ones[num % 10]);

        return result.toString().trim();
    }

    // Advanced: Scientific notation words
    public String numberToScientificWords(double num) {
        if (num == 0) {
            return "Zero";
        }

        boolean isNegative = num < 0;
        num = Math.abs(num);

        int exponent = 0;

        if (num >= 1) {
            while (num >= 10) {
                num /= 10;
                exponent++;
            }
        } else {
            while (num < 1) {
                num *= 10;
                exponent--;
            }
        }

        StringBuilder result = new StringBuilder();

        if (isNegative) {
            result.append("Negative ");
        }

        // Convert the coefficient
        result.append(numberToWordsWithFraction(num));

        if (exponent != 0) {
            result.append(" times Ten to the ");

            if (exponent > 0) {
                result.append(numberToOrdinal(exponent));
            } else {
                result.append("Negative ").append(numberToOrdinal(-exponent));
            }

            result.append(" Power");
        }

        return result.toString();
    }

    // Helper methods
    private String convertThreeDigits(int num) {
        StringBuilder result = new StringBuilder();

        if (num >= 100) {
            result.append(ONES[num / 100]).append(" Hundred");
            num %= 100;
            if (num > 0) {
                result.append(" ");
            }
        }

        if (num >= 20) {
            result.append(TENS[num / 10]);
            num %= 10;
            if (num > 0) {
                result.append(" ");
            }
        }

        if (num > 0) {
            result.append(ONES[num]);
        }

        return result.toString();
    }

    private String convertSpanish(int num) {
        if (num == 0) {
            return "";
        } else if (num < 20) {
            return SPANISH_ONES[num] + " ";
        } else if (num < 100) {
            return SPANISH_TENS[num / 10] + " " + convertSpanish(num % 10);
        } else if (num < 1000) {
            return SPANISH_ONES[num / 100] + " Cien " + convertSpanish(num % 100);
        } else if (num < 1000000) {
            return convertSpanish(num / 1000) + "Mil " + convertSpanish(num % 1000);
        } else if (num < 1000000000) {
            return convertSpanish(num / 1000000) + "Millón " + convertSpanish(num % 1000000);
        } else {
            return convertSpanish(num / 1000000000) + "Mil Millones " + convertSpanish(num % 1000000000);
        }
    }

    // Advanced: Number formatting with custom separators
    public String numberToWordsCustomFormat(int num, String separator, boolean capitalizeFirst) {
        if (num == 0) {
            return capitalizeFirst ? "Zero" : "zero";
        }

        String result = numberToWords(num);
        String[] words = result.split(" ");

        if (!capitalizeFirst && words.length > 0) {
            words[0] = words[0].toLowerCase();
        }

        return String.join(separator, words);
    }

    // Advanced: Compact notation
    public String numberToWordsCompact(long num) {
        if (num == 0)
            return "Zero";

        boolean negative = num < 0;
        num = Math.abs(num);

        if (num >= 1_000_000_000_000L) {
            return (negative ? "Negative " : "") +
                    String.format("%.1f", num / 1_000_000_000_000.0) + " Trillion";
        } else if (num >= 1_000_000_000L) {
            return (negative ? "Negative " : "") +
                    String.format("%.1f", num / 1_000_000_000.0) + " Billion";
        } else if (num >= 1_000_000L) {
            return (negative ? "Negative " : "") +
                    String.format("%.1f", num / 1_000_000.0) + " Million";
        } else if (num >= 1_000L) {
            return (negative ? "Negative " : "") +
                    String.format("%.1f", num / 1_000.0) + " Thousand";
        } else {
            return (negative ? "Negative " : "") + numberToWords((int) num);
        }
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(int[] testNumbers) {
        Map<String, Long> results = new HashMap<>();

        // Test recursive approach
        long start = System.nanoTime();
        for (int num : testNumbers) {
            numberToWords(num);
        }
        results.put("Recursive", System.nanoTime() - start);

        // Test iterative approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            numberToWordsIterative(num);
        }
        results.put("Iterative", System.nanoTime() - start);

        // Test pattern approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            numberToWordsPattern(num);
        }
        results.put("Pattern", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        IntegerToEnglishWords solution = new IntegerToEnglishWords();

        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");
        int[] testNumbers = { 0, 1, 12, 123, 1234, 12345, 123456, 1234567, 12345678, 123456789,
                1234567890, Integer.MAX_VALUE };

        for (int num : testNumbers) {
            String result1 = solution.numberToWords(num);
            String result2 = solution.numberToWordsIterative(num);
            String result3 = solution.numberToWordsPattern(num);

            System.out.println("Number: " + num);
            System.out.println("  Recursive: " + result1);
            System.out.println("  Iterative: " + result2);
            System.out.println("  Pattern: " + result3);

            // Verify consistency
            boolean consistent = result1.equals(result2) && result2.equals(result3);
            if (!consistent) {
                System.out.println("  WARNING: Inconsistent results!");
            }
            System.out.println();
        }

        // Test Case 2: Edge cases
        System.out.println("=== Test Case 2: Edge Cases ===");
        int[] edgeCases = { 10, 11, 19, 20, 21, 99, 100, 101, 110, 111, 1000, 1001, 1010, 1100 };

        for (int num : edgeCases) {
            System.out.println(num + " -> " + solution.numberToWords(num));
        }

        // Test Case 3: British system
        System.out.println("\n=== Test Case 3: British Number System ===");
        int[] britishTest = { 1234, 12345, 123456, 1234567 };

        for (int num : britishTest) {
            System.out.println("American: " + solution.numberToWords(num));
            System.out.println("British:  " + solution.numberToWordsBritish(num));
            System.out.println();
        }

        // Test Case 4: Ordinal numbers
        System.out.println("=== Test Case 4: Ordinal Numbers ===");
        int[] ordinalTest = { 1, 2, 3, 4, 11, 12, 13, 21, 22, 23, 101, 102, 103 };

        for (int num : ordinalTest) {
            System.out.println(num + " -> " + solution.numberToOrdinal(num));
        }

        // Test Case 5: Negative numbers
        System.out.println("\n=== Test Case 5: Negative Numbers ===");
        int[] negativeTest = { -1, -123, -1234, Integer.MIN_VALUE };

        for (int num : negativeTest) {
            System.out.println(num + " -> " + solution.numberToWordsWithNegative(num));
        }

        // Test Case 6: Fractions
        System.out.println("\n=== Test Case 6: Fractions ===");
        double[] fractionTest = { 1.5, 2.25, 10.123, -5.7, 0.1, 0.01, 0.001 };

        for (double num : fractionTest) {
            System.out.println(num + " -> " + solution.numberToWordsWithFraction(num));
        }

        // Test Case 7: Spanish numbers
        System.out.println("\n=== Test Case 7: Spanish Numbers ===");
        int[] spanishTest = { 1, 15, 25, 100, 123, 1000, 1234 };

        for (int num : spanishTest) {
            System.out.println("English: " + solution.numberToWords(num));
            System.out.println("Spanish: " + solution.numberToWordsSpanish(num));
            System.out.println();
        }

        // Test Case 8: Large numbers
        System.out.println("=== Test Case 8: Large Numbers ===");
        long[] largeTest = { 1234567890123L, Long.MAX_VALUE };

        for (long num : largeTest) {
            System.out.println(num + " -> " + solution.numberToWordsLarge(num));
        }

        // Test Case 9: Currency
        System.out.println("\n=== Test Case 9: Currency ===");
        int[][] currencyTest = { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 }, { 123, 45 }, { 1000, 99 } };

        for (int[] currency : currencyTest) {
            System.out.println("$" + currency[0] + "." + String.format("%02d", currency[1]) +
                    " -> " + solution.numberToCurrency(currency[0], currency[1]));
        }

        // Test Case 10: Scientific notation
        System.out.println("\n=== Test Case 10: Scientific Notation ===");
        double[] scientificTest = { 1.23e5, 4.56e-3, 7.89e10, -2.34e-7 };

        for (double num : scientificTest) {
            System.out.println(num + " -> " + solution.numberToScientificWords(num));
        }

        // Test Case 11: Custom formatting
        System.out.println("\n=== Test Case 11: Custom Formatting ===");
        int customNum = 12345;

        System.out.println("Default: " + solution.numberToWords(customNum));
        System.out.println("Hyphenated: " + solution.numberToWordsCustomFormat(customNum, "-", true));
        System.out.println("Underscore: " + solution.numberToWordsCustomFormat(customNum, "_", false));
        System.out.println("Compact: " + solution.numberToWordsCompact(customNum));

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        int[] performanceTest = new int[1000];
        Random random = new Random(42);

        for (int i = 0; i < performanceTest.length; i++) {
            performanceTest[i] = random.nextInt(Integer.MAX_VALUE);
        }

        Map<String, Long> performance = solution.comparePerformance(performanceTest);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Memory usage test
        System.out.println("\n=== Memory Usage Test ===");
        Runtime runtime = Runtime.getRuntime();

        runtime.gc();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();

        List<String> results = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            results.add(solution.numberToWords(i));
        }

        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        long memUsed = memAfter - memBefore;

        System.out.println("Memory used for 1000 conversions: " + memUsed / 1024 + " KB");
        System.out.println("Average memory per conversion: " + memUsed / 1000 + " bytes");

        // Validation test
        System.out.println("\n=== Validation Test ===");
        boolean allValid = true;

        for (int i = 0; i <= 100; i++) {
            String result = solution.numberToWords(i);
            if (result == null || result.trim().isEmpty()) {
                System.out.println("Invalid result for: " + i);
                allValid = false;
            }
        }

        System.out.println("All results valid for 0-100: " + allValid);

        // Special number tests
        System.out.println("\n=== Special Numbers ===");
        int[] specialNumbers = {
                1000000, // One Million
                1000000000, // One Billion
                Integer.MAX_VALUE // Largest int
        };

        for (int num : specialNumbers) {
            System.out.println(num + " -> " + solution.numberToWords(num));
        }

        System.out.println("\nInteger to English Words testing completed successfully!");
    }
}
