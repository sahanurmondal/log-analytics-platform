package company.banking.kotakbank.missing;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Banking Currency Exchange Rate Calculator
 * 
 * Problem: Design a system to handle currency conversion for international
 * banking
 * transactions with real-time exchange rates, cross-currency conversion, and
 * rate history tracking.
 * 
 * Features:
 * 1. Direct currency conversion (USD -> INR)
 * 2. Cross-currency conversion via base currency (EUR -> JPY via USD)
 * 3. Historical rate tracking and analysis
 * 4. Rate spread calculation for banking margins
 * 5. Multi-step conversion path finding
 * 
 * Input:
 * - amount: double - Amount to convert
 * - fromCurrency: String - Source currency code (e.g., "USD")
 * - toCurrency: String - Target currency code (e.g., "INR")
 * - rates: Map<String, Double> - Exchange rates map
 * 
 * Output:
 * - ConversionResult - Contains converted amount, rate used, fees, etc.
 * 
 * Example:
 * Input: amount=100.0, from="USD", to="INR", rate=82.50
 * Output: ConversionResult{amount=8250.0, rate=82.50, fee=8.25, timestamp=...}
 * 
 * Banking Context:
 * - Cross-border remittances
 * - International trade finance
 * - Foreign exchange trading
 * - Multi-currency account management
 */
public class CurrencyExchangeCalculator {

    // Standard currency codes
    public static final String USD = "USD";
    public static final String EUR = "EUR";
    public static final String GBP = "GBP";
    public static final String JPY = "JPY";
    public static final String INR = "INR";
    public static final String CNY = "CNY";
    public static final String AUD = "AUD";
    public static final String CAD = "CAD";

    // Default base currency for cross conversions
    private static final String BASE_CURRENCY = USD;

    // Banking fee percentage (0.25% typical for banks)
    private static final double DEFAULT_FEE_PERCENTAGE = 0.0025;

    // Exchange rate cache with timestamps
    private static final Map<String, RateEntry> rateCache = new HashMap<>();

    // Historical rates for analysis
    private static final List<ConversionRecord> conversionHistory = new ArrayList<>();

    /**
     * Basic currency conversion with current market rates
     */
    public static double convert(double amount, String from, String to, Map<String, Double> rates) {
        if (from.equals(to)) {
            return amount;
        }

        String directKey = from + ":" + to;
        Double directRate = rates.get(directKey);

        if (directRate != null) {
            return amount * directRate;
        }

        throw new IllegalArgumentException("Exchange rate not available for " + directKey);
    }

    /**
     * Enhanced conversion with banking features
     */
    public static ConversionResult convertWithDetails(double amount, String from, String to,
            Map<String, Double> rates) {
        return convertWithDetails(amount, from, to, rates, DEFAULT_FEE_PERCENTAGE);
    }

    /**
     * Conversion with custom fee percentage
     */
    public static ConversionResult convertWithDetails(double amount, String from, String to,
            Map<String, Double> rates, double feePercentage) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (from.equals(to)) {
            return new ConversionResult(amount, 1.0, 0.0, from, to,
                    LocalDateTime.now(), "Same currency");
        }

        // Try direct conversion first
        String directKey = from + ":" + to;
        Double directRate = rates.get(directKey);

        if (directRate != null) {
            double convertedAmount = amount * directRate;
            double fee = convertedAmount * feePercentage;
            double finalAmount = convertedAmount - fee;

            ConversionResult result = new ConversionResult(finalAmount, directRate, fee,
                    from, to, LocalDateTime.now(), "Direct conversion");

            // Record in history
            conversionHistory.add(new ConversionRecord(amount, from, to, directRate, fee, LocalDateTime.now()));

            return result;
        }

        // Try cross-currency conversion via base currency
        return convertViaCrossCurrency(amount, from, to, rates, feePercentage);
    }

    /**
     * Cross-currency conversion via base currency (usually USD)
     */
    private static ConversionResult convertViaCrossCurrency(double amount, String from, String to,
            Map<String, Double> rates, double feePercentage) {
        String fromToBaseKey = from + ":" + BASE_CURRENCY;
        String baseToToKey = BASE_CURRENCY + ":" + to;

        Double fromToBaseRate = rates.get(fromToBaseKey);
        Double baseToToRate = rates.get(baseToToKey);

        if (fromToBaseRate != null && baseToToRate != null) {
            double effectiveRate = fromToBaseRate * baseToToRate;
            double convertedAmount = amount * effectiveRate;
            double fee = convertedAmount * feePercentage;
            double finalAmount = convertedAmount - fee;

            String path = String.format("Cross conversion: %s -> %s -> %s", from, BASE_CURRENCY, to);

            ConversionResult result = new ConversionResult(finalAmount, effectiveRate, fee,
                    from, to, LocalDateTime.now(), path);

            // Record in history
            conversionHistory.add(new ConversionRecord(amount, from, to, effectiveRate, fee, LocalDateTime.now()));

            return result;
        }

        throw new IllegalArgumentException("No conversion path found from " + from + " to " + to);
    }

    /**
     * Follow-up 1: Batch conversion for multiple currencies
     */
    public static Map<String, ConversionResult> batchConvert(double amount, String fromCurrency,
            List<String> toCurrencies, Map<String, Double> rates) {
        Map<String, ConversionResult> results = new HashMap<>();

        for (String toCurrency : toCurrencies) {
            try {
                ConversionResult result = convertWithDetails(amount, fromCurrency, toCurrency, rates);
                results.put(toCurrency, result);
            } catch (Exception e) {
                results.put(toCurrency, new ConversionResult(0.0, 0.0, 0.0, fromCurrency,
                        toCurrency, LocalDateTime.now(), "Error: " + e.getMessage()));
            }
        }

        return results;
    }

    /**
     * Follow-up 2: Find best conversion path through multiple currencies
     */
    public static ConversionPath findBestConversionPath(double amount, String from, String to,
            Map<String, Double> rates, List<String> availableCurrencies) {
        if (from.equals(to)) {
            return new ConversionPath(Arrays.asList(from), amount, 0.0);
        }

        // Use Dijkstra-like algorithm to find best path
        Map<String, Double> bestAmounts = new HashMap<>();
        Map<String, List<String>> bestPaths = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        bestAmounts.put(from, amount);
        bestPaths.put(from, Arrays.asList(from));
        queue.offer(from);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            double currentAmount = bestAmounts.get(current);

            for (String next : availableCurrencies) {
                if (!current.equals(next)) {
                    String key = current + ":" + next;
                    Double rate = rates.get(key);

                    if (rate != null) {
                        double newAmount = currentAmount * rate * (1 - DEFAULT_FEE_PERCENTAGE);

                        if (!bestAmounts.containsKey(next) || newAmount > bestAmounts.get(next)) {
                            bestAmounts.put(next, newAmount);
                            List<String> newPath = new ArrayList<>(bestPaths.get(current));
                            newPath.add(next);
                            bestPaths.put(next, newPath);
                            queue.offer(next);
                        }
                    }
                }
            }
        }

        if (bestAmounts.containsKey(to)) {
            double finalAmount = bestAmounts.get(to);
            double totalFees = amount - finalAmount;
            return new ConversionPath(bestPaths.get(to), finalAmount, totalFees);
        }

        throw new IllegalArgumentException("No conversion path found from " + from + " to " + to);
    }

    /**
     * Follow-up 3: Rate arbitrage detection
     */
    public static List<ArbitrageOpportunity> findArbitrageOpportunities(Map<String, Double> rates,
            List<String> currencies) {
        List<ArbitrageOpportunity> opportunities = new ArrayList<>();

        // Check triangular arbitrage (3-currency cycles)
        for (int i = 0; i < currencies.size(); i++) {
            for (int j = 0; j < currencies.size(); j++) {
                for (int k = 0; k < currencies.size(); k++) {
                    if (i != j && j != k && k != i) {
                        String curr1 = currencies.get(i);
                        String curr2 = currencies.get(j);
                        String curr3 = currencies.get(k);

                        String key1 = curr1 + ":" + curr2;
                        String key2 = curr2 + ":" + curr3;
                        String key3 = curr3 + ":" + curr1;

                        Double rate1 = rates.get(key1);
                        Double rate2 = rates.get(key2);
                        Double rate3 = rates.get(key3);

                        if (rate1 != null && rate2 != null && rate3 != null) {
                            double product = rate1 * rate2 * rate3;
                            if (product > 1.0) {
                                double profit = (product - 1.0) * 100; // percentage
                                opportunities.add(new ArbitrageOpportunity(
                                        Arrays.asList(curr1, curr2, curr3, curr1),
                                        Arrays.asList(rate1, rate2, rate3),
                                        profit));
                            }
                        }
                    }
                }
            }
        }

        return opportunities;
    }

    /**
     * Follow-up 4: Historical rate analysis
     */
    public static RateAnalysis analyzeConversionHistory(String fromCurrency, String toCurrency) {
        List<ConversionRecord> relevantRecords = conversionHistory.stream()
                .filter(r -> r.fromCurrency.equals(fromCurrency) && r.toCurrency.equals(toCurrency))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (relevantRecords.isEmpty()) {
            return new RateAnalysis(0.0, 0.0, 0.0, 0.0, 0);
        }

        double[] rates = relevantRecords.stream().mapToDouble(r -> r.rate).toArray();
        double avgRate = Arrays.stream(rates).average().orElse(0.0);
        double minRate = Arrays.stream(rates).min().orElse(0.0);
        double maxRate = Arrays.stream(rates).max().orElse(0.0);
        double totalFees = relevantRecords.stream().mapToDouble(r -> r.fee).sum();

        return new RateAnalysis(avgRate, minRate, maxRate, totalFees, relevantRecords.size());
    }

    // Supporting classes
    public static class ConversionResult {
        public final double convertedAmount;
        public final double exchangeRate;
        public final double fee;
        public final String fromCurrency;
        public final String toCurrency;
        public final LocalDateTime timestamp;
        public final String conversionPath;

        public ConversionResult(double convertedAmount, double exchangeRate, double fee,
                String fromCurrency, String toCurrency, LocalDateTime timestamp, String conversionPath) {
            this.convertedAmount = convertedAmount;
            this.exchangeRate = exchangeRate;
            this.fee = fee;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.timestamp = timestamp;
            this.conversionPath = conversionPath;
        }

        @Override
        public String toString() {
            return String.format("%.2f %s -> %.2f %s (Rate: %.4f, Fee: %.2f) [%s]",
                    convertedAmount + fee, fromCurrency, convertedAmount, toCurrency,
                    exchangeRate, fee, conversionPath);
        }
    }

    public static class ConversionRecord {
        public final double originalAmount;
        public final String fromCurrency;
        public final String toCurrency;
        public final double rate;
        public final double fee;
        public final LocalDateTime timestamp;

        public ConversionRecord(double originalAmount, String fromCurrency, String toCurrency,
                double rate, double fee, LocalDateTime timestamp) {
            this.originalAmount = originalAmount;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.rate = rate;
            this.fee = fee;
            this.timestamp = timestamp;
        }
    }

    public static class ConversionPath {
        public final List<String> path;
        public final double finalAmount;
        public final double totalFees;

        public ConversionPath(List<String> path, double finalAmount, double totalFees) {
            this.path = new ArrayList<>(path);
            this.finalAmount = finalAmount;
            this.totalFees = totalFees;
        }

        @Override
        public String toString() {
            return String.format("Path: %s -> Final: %.2f, Fees: %.2f",
                    String.join(" -> ", path), finalAmount, totalFees);
        }
    }

    public static class ArbitrageOpportunity {
        public final List<String> currencyPath;
        public final List<Double> rates;
        public final double profitPercentage;

        public ArbitrageOpportunity(List<String> currencyPath, List<Double> rates, double profitPercentage) {
            this.currencyPath = new ArrayList<>(currencyPath);
            this.rates = new ArrayList<>(rates);
            this.profitPercentage = profitPercentage;
        }

        @Override
        public String toString() {
            return String.format("Arbitrage: %s, Profit: %.2f%%",
                    String.join(" -> ", currencyPath), profitPercentage);
        }
    }

    public static class RateAnalysis {
        public final double averageRate;
        public final double minRate;
        public final double maxRate;
        public final double totalFeesCollected;
        public final int transactionCount;

        public RateAnalysis(double averageRate, double minRate, double maxRate,
                double totalFeesCollected, int transactionCount) {
            this.averageRate = averageRate;
            this.minRate = minRate;
            this.maxRate = maxRate;
            this.totalFeesCollected = totalFeesCollected;
            this.transactionCount = transactionCount;
        }

        @Override
        public String toString() {
            return String.format("Avg Rate: %.4f, Range: %.4f-%.4f, Total Fees: %.2f, Transactions: %d",
                    averageRate, minRate, maxRate, totalFeesCollected, transactionCount);
        }
    }

    public static class RateEntry {
        public final double rate;
        public final LocalDateTime timestamp;

        public RateEntry(double rate, LocalDateTime timestamp) {
            this.rate = rate;
            this.timestamp = timestamp;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Banking Currency Exchange Calculator ===\n");

        // Setup sample exchange rates
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD:INR", 82.50);
        rates.put("INR:USD", 0.0121);
        rates.put("USD:EUR", 0.85);
        rates.put("EUR:USD", 1.18);
        rates.put("EUR:INR", 97.12);
        rates.put("INR:EUR", 0.0103);
        rates.put("USD:GBP", 0.73);
        rates.put("GBP:USD", 1.37);
        rates.put("USD:JPY", 110.0);
        rates.put("JPY:USD", 0.0091);

        // Test Case 1: Basic conversion
        System.out.println("1. Basic Currency Conversion:");
        double amount = 1000.0;
        String from = "USD";
        String to = "INR";

        double basicResult = convert(amount, from, to, rates);
        System.out.printf("Basic: %.2f %s = %.2f %s%n", amount, from, basicResult, to);

        ConversionResult detailedResult = convertWithDetails(amount, from, to, rates);
        System.out.printf("Detailed: %s%n", detailedResult);

        // Test Case 2: Cross-currency conversion
        System.out.println("\n2. Cross-Currency Conversion (EUR -> INR via USD):");
        ConversionResult crossResult = convertWithDetails(500.0, "EUR", "INR", rates);
        System.out.printf("Cross conversion: %s%n", crossResult);

        // Test Case 3: Batch conversion
        System.out.println("\n3. Batch Conversion (1000 USD to multiple currencies):");
        List<String> targetCurrencies = Arrays.asList("INR", "EUR", "GBP", "JPY");
        Map<String, ConversionResult> batchResults = batchConvert(1000.0, "USD", targetCurrencies, rates);

        batchResults.forEach(
                (currency, result) -> System.out.printf("USD -> %s: %.2f%n", currency, result.convertedAmount));

        // Test Case 4: Best conversion path
        System.out.println("\n4. Best Conversion Path Analysis:");
        List<String> availableCurrencies = Arrays.asList("USD", "EUR", "INR", "GBP", "JPY");
        try {
            ConversionPath bestPath = findBestConversionPath(1000.0, "USD", "INR", rates, availableCurrencies);
            System.out.printf("Best path: %s%n", bestPath);
        } catch (Exception e) {
            System.out.println("Path finding error: " + e.getMessage());
        }

        // Test Case 5: Arbitrage opportunities
        System.out.println("\n5. Arbitrage Opportunity Detection:");
        List<ArbitrageOpportunity> arbitrage = findArbitrageOpportunities(rates,
                Arrays.asList("USD", "EUR", "INR"));

        if (arbitrage.isEmpty()) {
            System.out.println("No arbitrage opportunities found.");
        } else {
            arbitrage.forEach(System.out::println);
        }

        // Test Case 6: Historical analysis (after some conversions)
        System.out.println("\n6. Historical Rate Analysis:");

        // Perform some conversions to build history
        convertWithDetails(100.0, "USD", "INR", rates);
        convertWithDetails(200.0, "USD", "INR", rates);
        convertWithDetails(150.0, "USD", "INR", rates);

        RateAnalysis analysis = analyzeConversionHistory("USD", "INR");
        System.out.printf("USD->INR Analysis: %s%n", analysis);

        // Test Case 7: Edge cases
        System.out.println("\n7. Edge Cases:");

        // Same currency
        ConversionResult sameResult = convertWithDetails(100.0, "USD", "USD", rates);
        System.out.printf("Same currency: %s%n", sameResult);

        // Invalid conversion
        try {
            convert(100.0, "USD", "XYZ", rates);
        } catch (Exception e) {
            System.out.printf("Invalid currency error: %s%n", e.getMessage());
        }

        // Zero amount
        try {
            convertWithDetails(0.0, "USD", "INR", rates);
        } catch (Exception e) {
            System.out.printf("Zero amount error: %s%n", e.getMessage());
        }

        // Test Case 8: Fee calculation comparison
        System.out.println("\n8. Fee Structure Comparison:");
        double testAmount = 10000.0;
        double[] feeRates = { 0.001, 0.0025, 0.005 }; // 0.1%, 0.25%, 0.5%

        for (double feeRate : feeRates) {
            ConversionResult feeResult = convertWithDetails(testAmount, "USD", "INR", rates, feeRate);
            System.out.printf("Fee %.1f%%: Amount=%.2f, Fee=%.2f%n",
                    feeRate * 100, feeResult.convertedAmount, feeResult.fee);
        }

        System.out.println("\n=== Test Completed Successfully ===");
    }
}
