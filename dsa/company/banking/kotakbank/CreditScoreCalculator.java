package company.banking.kotakbank;

import java.util.*;

/**
 * Kotak Bank SDE3 Interview Question #6
 * 
 * Problem: Customer Credit Score Calculator & Risk Assessment
 * LeetCode Equivalent: Weighted Sum + Multi-criteria Decision Making
 * Source: Banking Domain + TryExponent Financial Services Questions
 * 
 * Banking Context:
 * Design a credit scoring system that evaluates customer creditworthiness
 * based on multiple factors like income, credit history, existing loans,
 * payment behavior, and demographic data. The system should provide
 * risk categories and loan recommendations.
 * 
 * Interview Focus:
 * - Multi-factor scoring algorithms
 * - Risk assessment and categorization
 * - Data normalization and weighted scoring
 * - Machine learning integration potential
 * 
 * Difficulty: Medium-Hard
 * Expected Time: 35-45 minutes
 * 
 * Follow-up Questions:
 * 1. How would you handle missing or incomplete data?
 * 2. How would you update the model based on new market conditions?
 * 3. How would you ensure fairness and avoid bias in credit decisions?
 * 4. How would you scale this for millions of customers?
 */
public class CreditScoreCalculator {

    /**
     * Customer data representation
     */
    public static class CustomerProfile {
        String customerId;
        String name;
        int age;
        double monthlyIncome;
        int creditHistoryMonths;
        int numberOfExistingLoans;
        double totalExistingDebt;
        int latePayments;
        String employmentType; // "SALARIED", "BUSINESS", "FREELANCE"
        String educationLevel; // "GRADUATE", "POSTGRADUATE", "PROFESSIONAL"
        boolean hasCollateral;
        int accountAge; // months with bank
        double averageMonthlyBalance;

        public CustomerProfile(String customerId, String name, int age, double monthlyIncome,
                int creditHistoryMonths, int numberOfExistingLoans,
                double totalExistingDebt, int latePayments, String employmentType,
                String educationLevel, boolean hasCollateral, int accountAge,
                double averageMonthlyBalance) {
            this.customerId = customerId;
            this.name = name;
            this.age = age;
            this.monthlyIncome = monthlyIncome;
            this.creditHistoryMonths = creditHistoryMonths;
            this.numberOfExistingLoans = numberOfExistingLoans;
            this.totalExistingDebt = totalExistingDebt;
            this.latePayments = latePayments;
            this.employmentType = employmentType;
            this.educationLevel = educationLevel;
            this.hasCollateral = hasCollateral;
            this.accountAge = accountAge;
            this.averageMonthlyBalance = averageMonthlyBalance;
        }

        @Override
        public String toString() {
            return String.format("Customer: %s (ID: %s)\n" +
                    "Age: %d, Income: ₹%.2f, Credit History: %d months\n" +
                    "Existing Loans: %d, Total Debt: ₹%.2f, Late Payments: %d\n" +
                    "Employment: %s, Education: %s, Collateral: %s",
                    name, customerId, age, monthlyIncome, creditHistoryMonths,
                    numberOfExistingLoans, totalExistingDebt, latePayments,
                    employmentType, educationLevel, hasCollateral ? "Yes" : "No");
        }
    }

    /**
     * Credit score result with detailed breakdown
     */
    public static class CreditScoreResult {
        String customerId;
        int creditScore; // 300-850 scale
        String riskCategory; // "EXCELLENT", "GOOD", "FAIR", "POOR", "VERY_POOR"
        Map<String, Double> factorScores;
        String recommendation;
        double maxLoanAmount;
        double recommendedInterestRate;
        String reasoning;

        public CreditScoreResult(String customerId, int creditScore, String riskCategory,
                Map<String, Double> factorScores, String recommendation,
                double maxLoanAmount, double recommendedInterestRate, String reasoning) {
            this.customerId = customerId;
            this.creditScore = creditScore;
            this.riskCategory = riskCategory;
            this.factorScores = factorScores;
            this.recommendation = recommendation;
            this.maxLoanAmount = maxLoanAmount;
            this.recommendedInterestRate = recommendedInterestRate;
            this.reasoning = reasoning;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("🏦 CREDIT SCORE ASSESSMENT\n");
            result.append("=".repeat(40)).append("\n");
            result.append(String.format("Customer ID: %s\n", customerId));
            result.append(String.format("Credit Score: %d/850\n", creditScore));
            result.append(String.format("Risk Category: %s\n", riskCategory));
            result.append("\n📊 Factor Breakdown:\n");

            for (Map.Entry<String, Double> entry : factorScores.entrySet()) {
                result.append(String.format("   %s: %.1f/100\n", entry.getKey(), entry.getValue()));
            }

            result.append("\n💡 Recommendation: ").append(recommendation).append("\n");
            result.append(String.format("Max Loan Amount: ₹%.2f\n", maxLoanAmount));
            result.append(String.format("Recommended Interest Rate: %.2f%%\n", recommendedInterestRate));
            result.append("\n🤔 Reasoning: ").append(reasoning);

            return result.toString();
        }
    }

    // Scoring weights (must sum to 1.0)
    private static final Map<String, Double> FACTOR_WEIGHTS = Map.of(
            "Payment History", 0.35,
            "Income Stability", 0.25,
            "Credit Utilization", 0.15,
            "Credit History Length", 0.10,
            "Employment Profile", 0.08,
            "Banking Relationship", 0.07);

    // Risk categories and their score ranges
    private static final Map<String, int[]> RISK_CATEGORIES = Map.of(
            "EXCELLENT", new int[] { 750, 850 },
            "GOOD", new int[] { 700, 749 },
            "FAIR", new int[] { 650, 699 },
            "POOR", new int[] { 600, 649 },
            "VERY_POOR", new int[] { 300, 599 });

    /**
     * Main credit score calculation algorithm
     * Time Complexity: O(1) - fixed number of factors
     * Space Complexity: O(1) - fixed size data structures
     */
    public CreditScoreResult calculateCreditScore(CustomerProfile customer) {
        Map<String, Double> factorScores = new HashMap<>();

        // Calculate individual factor scores (0-100 scale)
        factorScores.put("Payment History", calculatePaymentHistoryScore(customer));
        factorScores.put("Income Stability", calculateIncomeStabilityScore(customer));
        factorScores.put("Credit Utilization", calculateCreditUtilizationScore(customer));
        factorScores.put("Credit History Length", calculateCreditHistoryScore(customer));
        factorScores.put("Employment Profile", calculateEmploymentScore(customer));
        factorScores.put("Banking Relationship", calculateBankingRelationshipScore(customer));

        // Calculate weighted average score
        double weightedScore = 0.0;
        for (Map.Entry<String, Double> entry : factorScores.entrySet()) {
            String factor = entry.getKey();
            Double score = entry.getValue();
            Double weight = FACTOR_WEIGHTS.get(factor);
            weightedScore += score * weight;
        }

        // Convert to 300-850 scale (standard credit score range)
        int creditScore = (int) Math.round(300 + (weightedScore / 100.0) * 550);
        creditScore = Math.max(300, Math.min(850, creditScore)); // Ensure bounds

        // Determine risk category
        String riskCategory = determineRiskCategory(creditScore);

        // Generate recommendations
        String recommendation = generateRecommendation(customer, creditScore, riskCategory);
        double maxLoanAmount = calculateMaxLoanAmount(customer, creditScore);
        double interestRate = calculateRecommendedInterestRate(customer, creditScore);
        String reasoning = generateReasoning(customer, factorScores, creditScore);

        return new CreditScoreResult(customer.customerId, creditScore, riskCategory,
                factorScores, recommendation, maxLoanAmount,
                interestRate, reasoning);
    }

    /**
     * Payment History Score (35% weight)
     * Analyzes late payments and payment consistency
     */
    private double calculatePaymentHistoryScore(CustomerProfile customer) {
        if (customer.creditHistoryMonths == 0) {
            return 50.0; // Neutral for new customers
        }

        // Base score starts at 100
        double score = 100.0;

        // Deduct points for late payments
        double latePaymentPenalty = (customer.latePayments * 100.0) / customer.creditHistoryMonths;
        score -= Math.min(latePaymentPenalty * 2, 60); // Max 60 point deduction

        // Bonus for long clean history
        if (customer.creditHistoryMonths > 24 && customer.latePayments == 0) {
            score += 10; // Bonus for excellent history
        }

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Income Stability Score (25% weight)
     * Evaluates income level and employment stability
     */
    private double calculateIncomeStabilityScore(CustomerProfile customer) {
        double score = 50.0; // Base score

        // Income level scoring (relative to typical ranges)
        if (customer.monthlyIncome >= 100000)
            score += 30;
        else if (customer.monthlyIncome >= 50000)
            score += 20;
        else if (customer.monthlyIncome >= 25000)
            score += 10;
        else if (customer.monthlyIncome < 15000)
            score -= 20;

        // Employment type stability
        switch (customer.employmentType) {
            case "SALARIED":
                score += 20;
                break;
            case "BUSINESS":
                score += 10;
                break;
            case "FREELANCE":
                score -= 10;
                break;
        }

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Credit Utilization Score (15% weight)
     * Analyzes debt-to-income ratio
     */
    private double calculateCreditUtilizationScore(CustomerProfile customer) {
        if (customer.monthlyIncome <= 0)
            return 0;

        double debtToIncomeRatio = customer.totalExistingDebt / (customer.monthlyIncome * 12);
        double score = 100.0;

        // Score based on debt-to-income ratio
        if (debtToIncomeRatio <= 0.1)
            score = 100;
        else if (debtToIncomeRatio <= 0.3)
            score = 80;
        else if (debtToIncomeRatio <= 0.5)
            score = 60;
        else if (debtToIncomeRatio <= 0.7)
            score = 40;
        else
            score = 20;

        // Penalty for too many existing loans
        if (customer.numberOfExistingLoans > 5) {
            score -= 20;
        } else if (customer.numberOfExistingLoans > 3) {
            score -= 10;
        }

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Credit History Length Score (10% weight)
     */
    private double calculateCreditHistoryScore(CustomerProfile customer) {
        int months = customer.creditHistoryMonths;

        if (months >= 60)
            return 100; // 5+ years
        if (months >= 36)
            return 80; // 3+ years
        if (months >= 24)
            return 60; // 2+ years
        if (months >= 12)
            return 40; // 1+ year
        if (months >= 6)
            return 20; // 6+ months
        return 0; // Less than 6 months
    }

    /**
     * Employment Profile Score (8% weight)
     */
    private double calculateEmploymentScore(CustomerProfile customer) {
        double score = 50.0;

        // Age factor (experience proxy)
        if (customer.age >= 40)
            score += 20;
        else if (customer.age >= 30)
            score += 10;
        else if (customer.age < 25)
            score -= 10;

        // Education level
        switch (customer.educationLevel) {
            case "PROFESSIONAL":
                score += 20;
                break;
            case "POSTGRADUATE":
                score += 15;
                break;
            case "GRADUATE":
                score += 10;
                break;
        }

        // Collateral availability
        if (customer.hasCollateral) {
            score += 10;
        }

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Banking Relationship Score (7% weight)
     */
    private double calculateBankingRelationshipScore(CustomerProfile customer) {
        double score = 50.0;

        // Account age with bank
        if (customer.accountAge >= 60)
            score += 25; // 5+ years
        else if (customer.accountAge >= 36)
            score += 20; // 3+ years
        else if (customer.accountAge >= 24)
            score += 15; // 2+ years
        else if (customer.accountAge >= 12)
            score += 10; // 1+ year

        // Average balance (banking relationship strength)
        if (customer.averageMonthlyBalance >= customer.monthlyIncome)
            score += 15;
        else if (customer.averageMonthlyBalance >= customer.monthlyIncome * 0.5)
            score += 10;
        else if (customer.averageMonthlyBalance < customer.monthlyIncome * 0.1)
            score -= 10;

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Determine risk category based on credit score
     */
    private String determineRiskCategory(int creditScore) {
        for (Map.Entry<String, int[]> entry : RISK_CATEGORIES.entrySet()) {
            int[] range = entry.getValue();
            if (creditScore >= range[0] && creditScore <= range[1]) {
                return entry.getKey();
            }
        }
        return "UNKNOWN";
    }

    /**
     * Generate personalized recommendation
     */
    private String generateRecommendation(CustomerProfile customer, int creditScore, String riskCategory) {
        switch (riskCategory) {
            case "EXCELLENT":
                return "Eligible for premium loan products with best interest rates";
            case "GOOD":
                return "Eligible for most loan products with competitive rates";
            case "FAIR":
                return "Eligible for standard loan products, consider improving payment history";
            case "POOR":
                return "Limited loan options available, recommend credit improvement program";
            case "VERY_POOR":
                return "Loan application may be declined, recommend secured loan options";
            default:
                return "Contact relationship manager for personalized consultation";
        }
    }

    /**
     * Calculate maximum loan amount based on profile
     */
    private double calculateMaxLoanAmount(CustomerProfile customer, int creditScore) {
        double baseAmount = customer.monthlyIncome * 60; // 5 years of income

        // Risk-based multiplier
        double multiplier = switch (determineRiskCategory(creditScore)) {
            case "EXCELLENT" -> 1.5;
            case "GOOD" -> 1.2;
            case "FAIR" -> 1.0;
            case "POOR" -> 0.6;
            case "VERY_POOR" -> 0.3;
            default -> 0.5;
        };

        // Adjust for existing debt
        double availableIncome = customer.monthlyIncome - (customer.totalExistingDebt / 12);
        if (availableIncome < customer.monthlyIncome * 0.4) {
            multiplier *= 0.5; // Reduce if high existing debt
        }

        return Math.max(0, baseAmount * multiplier);
    }

    /**
     * Calculate recommended interest rate
     */
    private double calculateRecommendedInterestRate(CustomerProfile customer, int creditScore) {
        double baseRate = 12.0; // Base interest rate

        // Risk-based adjustment
        double adjustment = switch (determineRiskCategory(creditScore)) {
            case "EXCELLENT" -> -3.0;
            case "GOOD" -> -1.5;
            case "FAIR" -> 0.0;
            case "POOR" -> 2.0;
            case "VERY_POOR" -> 4.0;
            default -> 2.0;
        };

        return Math.max(8.0, Math.min(20.0, baseRate + adjustment));
    }

    /**
     * Generate detailed reasoning for the score
     */
    private String generateReasoning(CustomerProfile customer, Map<String, Double> factorScores, int creditScore) {
        StringBuilder reasoning = new StringBuilder();

        // Find strongest and weakest factors
        String strongestFactor = factorScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
        String weakestFactor = factorScores.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .get().getKey();

        reasoning.append(String.format("Strongest factor: %s (%.1f/100). ",
                strongestFactor, factorScores.get(strongestFactor)));
        reasoning.append(String.format("Area for improvement: %s (%.1f/100). ",
                weakestFactor, factorScores.get(weakestFactor)));

        // Specific recommendations
        if (factorScores.get("Payment History") < 60) {
            reasoning.append("Focus on timely payments to improve credit score. ");
        }
        if (factorScores.get("Credit Utilization") < 60) {
            reasoning.append("Consider reducing existing debt burden. ");
        }
        if (factorScores.get("Banking Relationship") < 60) {
            reasoning.append("Strengthen banking relationship with regular transactions. ");
        }

        return reasoning.toString();
    }

    /**
     * Test credit scoring with various customer profiles
     */
    public void testCreditScoring() {
        System.out.println("🏦 TESTING CREDIT SCORE CALCULATOR");
        System.out.println("=".repeat(60));

        // Test customers with different profiles
        List<CustomerProfile> testCustomers = Arrays.asList(
                // Excellent customer
                new CustomerProfile("CUST001", "Rajesh Sharma", 35, 120000, 72, 1, 500000, 0,
                        "SALARIED", "POSTGRADUATE", true, 60, 150000),

                // Good customer
                new CustomerProfile("CUST002", "Priya Patel", 28, 65000, 36, 2, 300000, 1,
                        "SALARIED", "GRADUATE", false, 36, 80000),

                // Fair customer
                new CustomerProfile("CUST003", "Amit Kumar", 32, 45000, 24, 3, 400000, 3,
                        "BUSINESS", "GRADUATE", true, 24, 50000),

                // Poor customer
                new CustomerProfile("CUST004", "Sunita Singh", 26, 25000, 12, 4, 300000, 6,
                        "FREELANCE", "GRADUATE", false, 12, 20000),

                // Very poor customer
                new CustomerProfile("CUST005", "Ramesh Gupta", 24, 18000, 6, 5, 200000, 8,
                        "FREELANCE", "", false, 6, 5000));

        for (CustomerProfile customer : testCustomers) {
            System.out.println(customer);
            System.out.println("\n" + "-".repeat(40));

            CreditScoreResult result = calculateCreditScore(customer);
            System.out.println(result);

            System.out.println("\n" + "=".repeat(60) + "\n");
        }
    }

    /**
     * Banking system considerations
     */
    public void discussSystemImplementation() {
        System.out.println("🏦 CREDIT SCORING SYSTEM IMPLEMENTATION");
        System.out.println("=".repeat(60));

        System.out.println("1. DATA SOURCES & INTEGRATION:");
        System.out.println("   - Core banking system customer data");
        System.out.println("   - Credit bureau reports (CIBIL, Experian)");
        System.out.println("   - Internal transaction history");
        System.out.println("   - External verification services (income, employment)");

        System.out.println("\n2. REAL-TIME vs BATCH PROCESSING:");
        System.out.println("   - Real-time: Loan application processing");
        System.out.println("   - Batch: Periodic score updates, portfolio reviews");
        System.out.println("   - Event-driven: Score recalculation on significant events");

        System.out.println("\n3. MACHINE LEARNING ENHANCEMENT:");
        System.out.println("   - Feature engineering from transaction patterns");
        System.out.println("   - Ensemble models for improved accuracy");
        System.out.println("   - Model retraining with default outcomes");
        System.out.println("   - A/B testing for model validation");

        System.out.println("\n4. REGULATORY COMPLIANCE:");
        System.out.println("   - Fair lending practices and bias prevention");
        System.out.println("   - Model explainability for regulatory audits");
        System.out.println("   - Data privacy and consent management");
        System.out.println("   - Audit trails for all scoring decisions");

        System.out.println("\n5. SCALABILITY CONSIDERATIONS:");
        System.out.println("   - Microservices architecture for scoring components");
        System.out.println("   - Caching for frequently accessed scores");
        System.out.println("   - Event streaming for real-time updates");
        System.out.println("   - Database optimization for large customer base");
    }

    public static void main(String[] args) {
        System.out.println("🏦 KOTAK BANK SDE3 INTERVIEW QUESTION");
        System.out.println("📋 Problem: Credit Score Calculator & Risk Assessment");
        System.out.println("🔗 Domain: Banking Risk Management + Multi-criteria Algorithms");
        System.out.println();

        CreditScoreCalculator calculator = new CreditScoreCalculator();
        calculator.testCreditScoring();
        calculator.discussSystemImplementation();

        System.out.println("\n💡 INTERVIEW TIPS:");
        System.out.println("1. Discuss the importance of each scoring factor and its weight");
        System.out.println("2. Address bias prevention and fair lending practices");
        System.out.println("3. Consider real-time vs batch processing trade-offs");
        System.out.println("4. Think about machine learning integration opportunities");
        System.out.println("5. Address regulatory compliance and model explainability");
        System.out.println("6. Discuss scalability for millions of customers");
        System.out.println("7. Consider integration with external credit bureaus");
    }
}
