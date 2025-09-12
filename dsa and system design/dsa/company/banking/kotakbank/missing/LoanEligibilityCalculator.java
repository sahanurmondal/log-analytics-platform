package company.banking.kotakbank.missing;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Banking Loan Eligibility Calculator System
 * 
 * Problem: Design a comprehensive loan eligibility assessment system that
 * evaluates
 * customer applications based on multiple criteria including income, credit
 * history,
 * existing obligations, collateral, and regulatory requirements.
 * 
 * Features:
 * 1. Multi-criteria eligibility assessment (DTI, credit score, income
 * stability)
 * 2. EMI calculation with various interest rate models
 * 3. Loan amount estimation based on customer profile
 * 4. Risk assessment and interest rate determination
 * 5. Regulatory compliance checks (RBI guidelines)
 * 6. Pre-qualification and final approval workflows
 * 
 * Input:
 * - LoanApplication - Customer details, income, expenses, credit history
 * - LoanType - Personal, home, car, business loan
 * - BankPolicies - Current interest rates, DTI limits, minimum criteria
 * 
 * Output:
 * - EligibilityResult - Approved/rejected with detailed reasoning
 * - LoanTerms - Interest rate, EMI, tenure options
 * - RiskAssessment - Risk category and recommended conditions
 * 
 * Example:
 * Input: income=₹80,000/month, credit_score=750, existing_emi=₹15,000
 * Output: Eligible for ₹25,00,000 home loan at 8.5% for 20 years, EMI=₹21,455
 * 
 * Banking Context:
 * - Risk management and NPA reduction
 * - Regulatory compliance (RBI guidelines)
 * - Customer acquisition and retention
 * - Portfolio diversification
 */
public class LoanEligibilityCalculator {

    // Loan types
    public enum LoanType {
        PERSONAL(0.12, 7, 25000, 2000000, 0.5), // 12%, max 7 years, min 25k, max 20L, DTI 50%
        HOME(0.085, 30, 100000, 50000000, 0.6), // 8.5%, max 30 years, min 1L, max 5Cr, DTI 60%
        CAR(0.095, 7, 50000, 2500000, 0.55), // 9.5%, max 7 years, min 50k, max 25L, DTI 55%
        BUSINESS(0.11, 10, 500000, 10000000, 0.4), // 11%, max 10 years, min 5L, max 1Cr, DTI 40%
        GOLD(0.09, 3, 10000, 1000000, 0.7); // 9%, max 3 years, min 10k, max 10L, DTI 70%

        public final double baseInterestRate;
        public final int maxTenureYears;
        public final double minLoanAmount;
        public final double maxLoanAmount;
        public final double maxDTI;

        LoanType(double baseInterestRate, int maxTenureYears, double minLoanAmount,
                double maxLoanAmount, double maxDTI) {
            this.baseInterestRate = baseInterestRate;
            this.maxTenureYears = maxTenureYears;
            this.minLoanAmount = minLoanAmount;
            this.maxLoanAmount = maxLoanAmount;
            this.maxDTI = maxDTI;
        }
    }

    public enum IncomeType {
        SALARIED(1.0), // 100% consideration
        BUSINESS(0.8), // 80% consideration due to variability
        PROFESSIONAL(0.9), // 90% consideration
        RENTAL(0.7), // 70% consideration
        PENSION(0.95); // 95% consideration

        public final double stabilityFactor;

        IncomeType(double stabilityFactor) {
            this.stabilityFactor = stabilityFactor;
        }
    }

    public enum RiskCategory {
        LOW("AAA", 0.0, "Prime customer with excellent creditworthiness"),
        MEDIUM("AA", 0.005, "Good customer with minor risk factors"),
        HIGH("A", 0.015, "Acceptable risk with additional conditions"),
        VERY_HIGH("BBB", 0.025, "High risk requiring strong collateral"),
        REJECT("D", 0.0, "Application rejected due to high risk");

        public final String grade;
        public final double riskPremium;
        public final String description;

        RiskCategory(String grade, double riskPremium, String description) {
            this.grade = grade;
            this.riskPremium = riskPremium;
            this.description = description;
        }
    }

    /**
     * Comprehensive loan application class
     */
    public static class LoanApplication {
        // Personal Details
        public final String applicantId;
        public final int age;
        public final String employment;
        public final IncomeType incomeType;
        public final int workExperienceYears;

        // Financial Details
        public final double monthlyIncome;
        public final double monthlyExpenses;
        public final double existingEMIs;
        public final int creditScore;
        public final double currentSavings;
        public final double requestedAmount;
        public final LoanType loanType;
        public final int requestedTenureYears;

        // Additional Factors
        public final boolean hasCollateral;
        public final double collateralValue;
        public final boolean hasCoApplicant;
        public final double coApplicantIncome;
        public final List<String> bankingHistory; // Previous defaults, etc.

        public LoanApplication(String applicantId, int age, String employment, IncomeType incomeType,
                int workExperienceYears, double monthlyIncome, double monthlyExpenses,
                double existingEMIs, int creditScore, double currentSavings,
                double requestedAmount, LoanType loanType, int requestedTenureYears,
                boolean hasCollateral, double collateralValue, boolean hasCoApplicant,
                double coApplicantIncome, List<String> bankingHistory) {
            this.applicantId = applicantId;
            this.age = age;
            this.employment = employment;
            this.incomeType = incomeType;
            this.workExperienceYears = workExperienceYears;
            this.monthlyIncome = monthlyIncome;
            this.monthlyExpenses = monthlyExpenses;
            this.existingEMIs = existingEMIs;
            this.creditScore = creditScore;
            this.currentSavings = currentSavings;
            this.requestedAmount = requestedAmount;
            this.loanType = loanType;
            this.requestedTenureYears = requestedTenureYears;
            this.hasCollateral = hasCollateral;
            this.collateralValue = collateralValue;
            this.hasCoApplicant = hasCoApplicant;
            this.coApplicantIncome = coApplicantIncome;
            this.bankingHistory = new ArrayList<>(bankingHistory);
        }
    }

    /**
     * Comprehensive eligibility result
     */
    public static class EligibilityResult {
        public final boolean isEligible;
        public final double approvedAmount;
        public final double interestRate;
        public final int approvedTenureYears;
        public final double emi;
        public final RiskCategory riskCategory;
        public final List<String> conditions;
        public final List<String> rejectionReasons;
        public final Map<String, Double> scores;

        public EligibilityResult(boolean isEligible, double approvedAmount, double interestRate,
                int approvedTenureYears, double emi, RiskCategory riskCategory,
                List<String> conditions, List<String> rejectionReasons,
                Map<String, Double> scores) {
            this.isEligible = isEligible;
            this.approvedAmount = approvedAmount;
            this.interestRate = interestRate;
            this.approvedTenureYears = approvedTenureYears;
            this.emi = emi;
            this.riskCategory = riskCategory;
            this.conditions = new ArrayList<>(conditions);
            this.rejectionReasons = new ArrayList<>(rejectionReasons);
            this.scores = new HashMap<>(scores);
        }

        @Override
        public String toString() {
            if (isEligible) {
                return String.format("APPROVED: ₹%.0f at %.2f%% for %d years, EMI: ₹%.0f (%s risk)",
                        approvedAmount, interestRate * 100, approvedTenureYears, emi, riskCategory.grade);
            } else {
                return "REJECTED: " + String.join(", ", rejectionReasons);
            }
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public static boolean isEligible(double annualIncome, double monthlyDebtPayments, int creditScore) {
        double dti = (monthlyDebtPayments * 12) / Math.max(1.0, annualIncome);
        return dti < 0.4 && creditScore >= 650;
    }

    /**
     * Legacy EMI calculation method
     */
    public static double estimateEMI(double principal, double annualRate, int months) {
        double r = annualRate / 12.0 / 100.0;
        if (r == 0)
            return principal / months;
        return (principal * r * Math.pow(1 + r, months)) / (Math.pow(1 + r, months) - 1);
    }

    /**
     * Comprehensive loan eligibility assessment
     */
    public static EligibilityResult assessEligibility(LoanApplication application) {
        Map<String, Double> scores = new HashMap<>();
        List<String> rejectionReasons = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        // 1. Basic eligibility checks
        if (!checkBasicEligibility(application, rejectionReasons)) {
            return new EligibilityResult(false, 0, 0, 0, 0, RiskCategory.REJECT,
                    conditions, rejectionReasons, scores);
        }

        // 2. Calculate various scores
        double creditScore = calculateCreditScore(application, scores);
        double incomeScore = calculateIncomeScore(application, scores);
        double stabilityScore = calculateStabilityScore(application, scores);
        double dtiScore = calculateDTIScore(application, scores);

        // 3. Overall risk assessment
        double overallScore = (creditScore * 0.3 + incomeScore * 0.25 +
                stabilityScore * 0.25 + dtiScore * 0.2);
        scores.put("Overall Score", overallScore);

        RiskCategory riskCategory = determineRiskCategory(overallScore, application);

        if (riskCategory == RiskCategory.REJECT) {
            rejectionReasons.add("Overall risk score too low: " + String.format("%.1f", overallScore));
            return new EligibilityResult(false, 0, 0, 0, 0, riskCategory,
                    conditions, rejectionReasons, scores);
        }

        // 4. Calculate loan terms
        double approvedAmount = calculateApprovedAmount(application, overallScore);
        double interestRate = calculateInterestRate(application, riskCategory);
        int tenure = Math.min(application.requestedTenureYears, application.loanType.maxTenureYears);
        double emi = estimateEMI(approvedAmount, interestRate * 100, tenure * 12);

        // 5. Add conditions based on risk
        addRiskBasedConditions(application, riskCategory, conditions);

        return new EligibilityResult(true, approvedAmount, interestRate, tenure, emi,
                riskCategory, conditions, rejectionReasons, scores);
    }

    private static boolean checkBasicEligibility(LoanApplication app, List<String> rejectionReasons) {
        boolean eligible = true;

        // Age criteria
        if (app.age < 21 || app.age > 65) {
            rejectionReasons.add("Age must be between 21-65 years");
            eligible = false;
        }

        // Minimum work experience
        int minExperience = app.incomeType == IncomeType.SALARIED ? 2 : 3;
        if (app.workExperienceYears < minExperience) {
            rejectionReasons.add("Insufficient work experience: " + app.workExperienceYears + " years");
            eligible = false;
        }

        // Minimum income
        double minIncome = app.loanType == LoanType.PERSONAL ? 25000 : 35000;
        if (app.monthlyIncome < minIncome) {
            rejectionReasons.add("Income below minimum threshold: ₹" + app.monthlyIncome);
            eligible = false;
        }

        // Credit score minimum
        if (app.creditScore < 600) {
            rejectionReasons.add("Credit score too low: " + app.creditScore);
            eligible = false;
        }

        // Banking history check
        if (app.bankingHistory.contains("DEFAULT") || app.bankingHistory.contains("FRAUD")) {
            rejectionReasons.add("Adverse banking history found");
            eligible = false;
        }

        return eligible;
    }

    private static double calculateCreditScore(LoanApplication app, Map<String, Double> scores) {
        double score = 0.0;

        if (app.creditScore >= 800)
            score = 100.0;
        else if (app.creditScore >= 750)
            score = 90.0;
        else if (app.creditScore >= 700)
            score = 80.0;
        else if (app.creditScore >= 650)
            score = 70.0;
        else
            score = 50.0;

        scores.put("Credit Score", score);
        return score;
    }

    private static double calculateIncomeScore(LoanApplication app, Map<String, Double> scores) {
        double effectiveIncome = app.monthlyIncome * app.incomeType.stabilityFactor;
        if (app.hasCoApplicant) {
            effectiveIncome += app.coApplicantIncome * 0.5; // 50% weightage for co-applicant
        }

        double score = Math.min(100.0, (effectiveIncome / 100000.0) * 50.0 + 50.0);
        scores.put("Income Score", score);
        return score;
    }

    private static double calculateStabilityScore(LoanApplication app, Map<String, Double> scores) {
        double score = 50.0; // Base score

        // Work experience bonus
        score += Math.min(30.0, app.workExperienceYears * 3.0);

        // Income type stability
        score += app.incomeType.stabilityFactor * 20.0;

        // Savings to income ratio
        double savingsRatio = app.currentSavings / (app.monthlyIncome * 12);
        score += Math.min(20.0, savingsRatio * 100.0);

        scores.put("Stability Score", Math.min(100.0, score));
        return Math.min(100.0, score);
    }

    private static double calculateDTIScore(LoanApplication app, Map<String, Double> scores) {
        double proposedEMI = estimateEMI(app.requestedAmount,
                app.loanType.baseInterestRate * 100,
                app.requestedTenureYears * 12);

        double totalEMI = app.existingEMIs + proposedEMI;
        double dti = totalEMI / app.monthlyIncome;

        double score;
        if (dti <= 0.3)
            score = 100.0;
        else if (dti <= 0.4)
            score = 80.0;
        else if (dti <= 0.5)
            score = 60.0;
        else if (dti <= 0.6)
            score = 40.0;
        else
            score = 20.0;

        scores.put("DTI Score", score);
        scores.put("DTI Ratio", dti);
        return score;
    }

    private static RiskCategory determineRiskCategory(double overallScore, LoanApplication app) {
        if (overallScore >= 85 && app.creditScore >= 750)
            return RiskCategory.LOW;
        else if (overallScore >= 75 && app.creditScore >= 700)
            return RiskCategory.MEDIUM;
        else if (overallScore >= 65 && app.creditScore >= 650)
            return RiskCategory.HIGH;
        else if (overallScore >= 55)
            return RiskCategory.VERY_HIGH;
        else
            return RiskCategory.REJECT;
    }

    private static double calculateApprovedAmount(LoanApplication app, double score) {
        double baseAmount = Math.min(app.requestedAmount, app.loanType.maxLoanAmount);

        // Adjust based on DTI
        double maxAffordableEMI = app.monthlyIncome * app.loanType.maxDTI - app.existingEMIs;
        double maxAffordableAmount = calculateLoanAmount(maxAffordableEMI,
                app.loanType.baseInterestRate,
                app.requestedTenureYears);

        // Apply score-based adjustment
        double scoreAdjustment = score / 100.0;
        double approvedAmount = Math.min(baseAmount, maxAffordableAmount) * scoreAdjustment;

        // Collateral consideration
        if (app.hasCollateral && app.loanType == LoanType.HOME) {
            approvedAmount = Math.min(approvedAmount, app.collateralValue * 0.85); // 85% LTV
        }

        return Math.max(app.loanType.minLoanAmount, approvedAmount);
    }

    private static double calculateLoanAmount(double emi, double annualRate, int years) {
        double r = annualRate / 12.0;
        int n = years * 12;
        if (r == 0)
            return emi * n;
        return emi * ((Math.pow(1 + r, n) - 1) / (r * Math.pow(1 + r, n)));
    }

    private static double calculateInterestRate(LoanApplication app, RiskCategory riskCategory) {
        double baseRate = app.loanType.baseInterestRate;
        double finalRate = baseRate + riskCategory.riskPremium;

        // Customer relationship discounts
        if (app.bankingHistory.contains("PREMIUM_CUSTOMER")) {
            finalRate -= 0.005; // 0.5% discount
        }

        // Collateral discounts
        if (app.hasCollateral) {
            finalRate -= 0.0025; // 0.25% discount
        }

        return Math.max(baseRate * 0.8, finalRate); // Minimum 80% of base rate
    }

    private static void addRiskBasedConditions(LoanApplication app, RiskCategory riskCategory,
            List<String> conditions) {
        switch (riskCategory) {
            case LOW:
                conditions.add("Standard documentation required");
                break;
            case MEDIUM:
                conditions.add("Salary account to be maintained with bank");
                break;
            case HIGH:
                conditions.add("Co-applicant required");
                conditions.add("Life insurance coverage mandatory");
                break;
            case VERY_HIGH:
                conditions.add("Additional collateral required");
                conditions.add("Guarantor required");
                conditions.add("Monthly income proof verification");
                break;
            case REJECT:
                conditions.add("Application cannot be processed due to high risk");
                break;
        }

        if (app.age > 55) {
            conditions.add("Health insurance mandatory");
        }
    }

    /**
     * Follow-up 1: Bulk loan processing for multiple applications
     */
    public static Map<String, EligibilityResult> processBulkApplications(List<LoanApplication> applications) {
        Map<String, EligibilityResult> results = new HashMap<>();

        for (LoanApplication app : applications) {
            try {
                EligibilityResult result = assessEligibility(app);
                results.put(app.applicantId, result);
            } catch (Exception e) {
                results.put(app.applicantId, new EligibilityResult(false, 0, 0, 0, 0,
                        RiskCategory.REJECT, Arrays.asList(),
                        Arrays.asList("Processing error: " + e.getMessage()), new HashMap<>()));
            }
        }

        return results;
    }

    /**
     * Follow-up 2: Loan portfolio analysis
     */
    public static PortfolioAnalysis analyzePortfolio(List<LoanApplication> applications) {
        Map<String, EligibilityResult> results = processBulkApplications(applications);

        int totalApplications = applications.size();
        int approvedCount = (int) results.values().stream().filter(r -> r.isEligible).count();
        double approvalRate = (double) approvedCount / totalApplications;

        double totalApprovedAmount = results.values().stream()
                .filter(r -> r.isEligible)
                .mapToDouble(r -> r.approvedAmount)
                .sum();

        Map<RiskCategory, Long> riskDistribution = results.values().stream()
                .collect(Collectors.groupingBy(r -> r.riskCategory, Collectors.counting()));

        Map<LoanType, Long> loanTypeDistribution = applications.stream()
                .collect(Collectors.groupingBy(a -> a.loanType, Collectors.counting()));

        return new PortfolioAnalysis(totalApplications, approvedCount, approvalRate,
                totalApprovedAmount, riskDistribution, loanTypeDistribution);
    }

    /**
     * Follow-up 3: Interest rate optimization
     */
    public static Map<String, Double> optimizeInterestRates(List<LoanApplication> applications,
            double targetApprovalRate) {
        Map<String, Double> optimizedRates = new HashMap<>();

        for (LoanType loanType : LoanType.values()) {
            double currentRate = loanType.baseInterestRate;
            double bestRate = currentRate;
            double bestApprovalRate = 0.0;

            // Test different rates
            for (double rate = currentRate - 0.02; rate <= currentRate + 0.02; rate += 0.002) {
                double approvalRate = simulateApprovalRate(applications, loanType, rate);

                if (Math.abs(approvalRate - targetApprovalRate) < Math.abs(bestApprovalRate - targetApprovalRate)) {
                    bestRate = rate;
                    bestApprovalRate = approvalRate;
                }
            }

            optimizedRates.put(loanType.name(), bestRate);
        }

        return optimizedRates;
    }

    private static double simulateApprovalRate(List<LoanApplication> applications,
            LoanType targetType, double testRate) {
        long targetTypeCount = applications.stream()
                .filter(app -> app.loanType == targetType)
                .count();

        if (targetTypeCount == 0)
            return 0.0;

        // This is a simplified simulation - in practice, you'd adjust the base rate
        // and recalculate eligibility for all applications of this type
        return 0.7; // Placeholder
    }

    // Portfolio analysis result class
    public static class PortfolioAnalysis {
        public final int totalApplications;
        public final int approvedApplications;
        public final double approvalRate;
        public final double totalApprovedAmount;
        public final Map<RiskCategory, Long> riskDistribution;
        public final Map<LoanType, Long> loanTypeDistribution;

        public PortfolioAnalysis(int totalApplications, int approvedApplications, double approvalRate,
                double totalApprovedAmount, Map<RiskCategory, Long> riskDistribution,
                Map<LoanType, Long> loanTypeDistribution) {
            this.totalApplications = totalApplications;
            this.approvedApplications = approvedApplications;
            this.approvalRate = approvalRate;
            this.totalApprovedAmount = totalApprovedAmount;
            this.riskDistribution = new HashMap<>(riskDistribution);
            this.loanTypeDistribution = new HashMap<>(loanTypeDistribution);
        }

        @Override
        public String toString() {
            return String.format("Portfolio: %d apps, %.1f%% approved, ₹%.0f total amount",
                    totalApplications, approvalRate * 100, totalApprovedAmount);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Banking Loan Eligibility Calculator System ===\n");

        // Test Case 1: Legacy methods for backward compatibility
        System.out.println("1. Legacy Methods Test:");
        boolean eligible = isEligible(600000, 10000, 700);
        double emi = estimateEMI(500000, 7.5, 60);
        System.out.printf("Legacy eligibility: %s%n", eligible);
        System.out.printf("Legacy EMI: ₹%.0f%n", emi);

        // Test Case 2: Comprehensive loan applications
        System.out.println("\n2. Comprehensive Loan Assessment:");

        // High-quality applicant
        LoanApplication goodApplicant = new LoanApplication(
                "CUST001", 35, "Software Engineer", IncomeType.SALARIED, 8,
                120000, 45000, 12000, 780, 1500000, 2500000, LoanType.HOME, 20,
                true, 3000000, false, 0, Arrays.asList("PREMIUM_CUSTOMER"));

        EligibilityResult goodResult = assessEligibility(goodApplicant);
        System.out.printf("Good applicant: %s%n", goodResult);
        System.out.printf("  Conditions: %s%n", goodResult.conditions);

        // Marginal applicant
        LoanApplication marginalApplicant = new LoanApplication(
                "CUST002", 28, "Marketing Executive", IncomeType.SALARIED, 3,
                45000, 25000, 8000, 680, 200000, 800000, LoanType.PERSONAL, 5,
                false, 0, true, 30000, Arrays.asList());

        EligibilityResult marginalResult = assessEligibility(marginalApplicant);
        System.out.printf("Marginal applicant: %s%n", marginalResult);

        // High-risk applicant
        LoanApplication riskApplicant = new LoanApplication(
                "CUST003", 45, "Business Owner", IncomeType.BUSINESS, 2,
                80000, 60000, 25000, 620, 100000, 1500000, LoanType.BUSINESS, 7,
                false, 0, false, 0, Arrays.asList("LATE_PAYMENT"));

        EligibilityResult riskResult = assessEligibility(riskApplicant);
        System.out.printf("Risky applicant: %s%n", riskResult);
        System.out.printf("  Rejection reasons: %s%n", riskResult.rejectionReasons);

        // Test Case 3: Different loan types comparison
        System.out.println("\n3. Loan Type Comparison for Same Applicant:");
        LoanApplication baseApplicant = new LoanApplication(
                "CUST004", 32, "Manager", IncomeType.SALARIED, 6,
                75000, 35000, 5000, 720, 800000, 1000000, LoanType.PERSONAL, 5,
                false, 0, false, 0, Arrays.asList());

        for (LoanType loanType : LoanType.values()) {
            LoanApplication testApp = new LoanApplication(
                    baseApplicant.applicantId, baseApplicant.age, baseApplicant.employment,
                    baseApplicant.incomeType, baseApplicant.workExperienceYears,
                    baseApplicant.monthlyIncome, baseApplicant.monthlyExpenses,
                    baseApplicant.existingEMIs, baseApplicant.creditScore,
                    baseApplicant.currentSavings, baseApplicant.requestedAmount,
                    loanType, Math.min(5, loanType.maxTenureYears),
                    baseApplicant.hasCollateral, baseApplicant.collateralValue,
                    baseApplicant.hasCoApplicant, baseApplicant.coApplicantIncome,
                    baseApplicant.bankingHistory);

            EligibilityResult result = assessEligibility(testApp);
            System.out.printf("  %s: %s%n", loanType,
                    result.isEligible
                            ? String.format("₹%.0f at %.2f%%", result.approvedAmount, result.interestRate * 100)
                            : "REJECTED");
        }

        // Test Case 4: Bulk processing
        System.out.println("\n4. Bulk Loan Processing:");
        List<LoanApplication> bulkApplications = Arrays.asList(goodApplicant, marginalApplicant, riskApplicant);
        Map<String, EligibilityResult> bulkResults = processBulkApplications(bulkApplications);

        bulkResults.forEach(
                (id, result) -> System.out.printf("  %s: %s%n", id, result.isEligible ? "APPROVED" : "REJECTED"));

        // Test Case 5: Portfolio analysis
        System.out.println("\n5. Portfolio Analysis:");
        PortfolioAnalysis analysis = analyzePortfolio(bulkApplications);
        System.out.printf("  %s%n", analysis);
        System.out.printf("  Risk distribution: %s%n", analysis.riskDistribution);
        System.out.printf("  Loan type distribution: %s%n", analysis.loanTypeDistribution);

        // Test Case 6: Interest rate optimization
        System.out.println("\n6. Interest Rate Optimization:");
        Map<String, Double> optimizedRates = optimizeInterestRates(bulkApplications, 0.75);
        optimizedRates
                .forEach((loanType, rate) -> System.out.printf("  %s: %.2f%% (optimized)%n", loanType, rate * 100));

        // Test Case 7: Edge cases and stress testing
        System.out.println("\n7. Edge Cases:");

        // Very young applicant
        LoanApplication youngApplicant = new LoanApplication(
                "CUST005", 20, "Intern", IncomeType.SALARIED, 0,
                15000, 8000, 0, 650, 10000, 100000, LoanType.PERSONAL, 3,
                false, 0, false, 0, Arrays.asList());

        EligibilityResult youngResult = assessEligibility(youngApplicant);
        System.out.printf("  Young applicant (20 years): %s%n",
                youngResult.isEligible ? "APPROVED" : "REJECTED - " + youngResult.rejectionReasons.get(0));

        // Very high income applicant
        LoanApplication richApplicant = new LoanApplication(
                "CUST006", 40, "CEO", IncomeType.SALARIED, 15,
                500000, 100000, 0, 850, 10000000, 50000000, LoanType.HOME, 25,
                true, 60000000, false, 0, Arrays.asList("PREMIUM_CUSTOMER"));

        EligibilityResult richResult = assessEligibility(richApplicant);
        System.out.printf("  High-income applicant: %s%n", richResult);

        // Test Case 8: EMI affordability analysis
        System.out.println("\n8. EMI Affordability Analysis:");
        double[] loanAmounts = { 500000, 1000000, 2500000, 5000000 };
        int[] tenures = { 5, 10, 15, 20, 25 };

        System.out.println("  Loan Amount vs Tenure EMI Matrix (at 8.5%):");
        System.out.print("Amount\\Tenure");
        for (int tenure : tenures) {
            System.out.printf("%8d", tenure);
        }
        System.out.println();

        for (double amount : loanAmounts) {
            System.out.printf("₹%7.0fK", amount / 1000);
            for (int tenure : tenures) {
                double emiAmount = estimateEMI(amount, 8.5, tenure * 12);
                System.out.printf("%8.0f", emiAmount);
            }
            System.out.println();
        }

        System.out.println("\n=== Test Completed Successfully ===");
    }
}
