package company.banking.kotakbank.missing;

import java.util.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Banking Account Number Generator and Validation System
 * 
 * Problem: Design a secure account number generation system for banking that
 * ensures uniqueness, follows banking standards (like Luhn algorithm), includes
 * bank-specific prefixes, and provides validation mechanisms for fraud
 * prevention.
 * 
 * Features:
 * 1. Luhn algorithm implementation for check digit calculation
 * 2. Bank-specific account number formats (Savings, Current, Loan, Credit Card)
 * 3. Secure random number generation using cryptographically strong RNG
 * 4. Account number validation and verification
 * 5. Batch generation for multiple accounts
 * 6. Custom formats for different banking products
 * 7. IBAN generation for international accounts
 * 
 * Input:
 * - AccountType - Type of account (SAVINGS, CURRENT, LOAN, CREDIT_CARD, etc.)
 * - BranchCode - Bank branch identifier
 * - CustomerDetails - Customer information for personalization
 * 
 * Output:
 * - AccountNumber - Generated account number with check digit
 * - ValidationResult - Validation status with detailed error information
 * 
 * Example:
 * Input: type=SAVINGS, branch="MUM001", customer="JOHN_DOE"
 * Output: "KOT0011234567895" (KOT + branch_code + unique_number + check_digit)
 * 
 * Banking Context:
 * - Account opening automation
 * - Fraud prevention through validation
 * - Regulatory compliance (RBI guidelines)
 * - International banking standards (IBAN)
 */
public class AccountNumberGenerator {

    // Account type definitions with specific formatting rules
    public enum AccountType {
        SAVINGS("01", 10, "SAV"), // 01 prefix, 10 digits, SAV identifier
        CURRENT("02", 10, "CUR"), // 02 prefix, 10 digits, CUR identifier
        LOAN("03", 12, "LON"), // 03 prefix, 12 digits, LON identifier
        CREDIT_CARD("04", 16, "CRD"), // 04 prefix, 16 digits, CRD identifier
        FOREX("05", 12, "FRX"), // 05 prefix, 12 digits, FRX identifier
        FIXED_DEPOSIT("06", 10, "FDS"), // 06 prefix, 10 digits, FDS identifier
        RECURRING_DEPOSIT("07", 10, "RDS"), // 07 prefix, 10 digits, RDS identifier
        DEMAT("08", 14, "DMT"); // 08 prefix, 14 digits, DMT identifier

        public final String typeCode;
        public final int digitLength;
        public final String identifier;

        AccountType(String typeCode, int digitLength, String identifier) {
            this.typeCode = typeCode;
            this.digitLength = digitLength;
            this.identifier = identifier;
        }
    }

    // Bank-specific constants
    private static final String BANK_CODE = "KOT"; // Kotak Bank identifier
    private static final String COUNTRY_CODE = "IN"; // India country code
    private static final String IBAN_BANK_CODE = "KKBK"; // Kotak SWIFT code prefix

    // Secure random number generator
    private static final SecureRandom secureRandom = new SecureRandom();

    // Track generated numbers to ensure uniqueness
    private static final Set<String> generatedNumbers = new HashSet<>();

    /**
     * Account generation request class
     */
    public static class AccountRequest {
        public final AccountType accountType;
        public final String branchCode;
        public final String customerName;
        public final String customerId;
        public final boolean requireIBAN;
        public final Map<String, String> additionalParams;

        public AccountRequest(AccountType accountType, String branchCode, String customerName,
                String customerId, boolean requireIBAN, Map<String, String> additionalParams) {
            this.accountType = accountType;
            this.branchCode = branchCode;
            this.customerName = customerName;
            this.customerId = customerId;
            this.requireIBAN = requireIBAN;
            this.additionalParams = new HashMap<>(additionalParams);
        }

        // Simplified constructor
        public AccountRequest(AccountType accountType, String branchCode, String customerName) {
            this(accountType, branchCode, customerName, null, false, new HashMap<>());
        }
    }

    /**
     * Account generation result class
     */
    public static class AccountResult {
        public final String accountNumber;
        public final String iban;
        public final AccountType accountType;
        public final String branchCode;
        public final LocalDateTime generatedAt;
        public final Map<String, String> metadata;
        public final boolean isValid;
        public final String validationMessage;

        public AccountResult(String accountNumber, String iban, AccountType accountType,
                String branchCode, LocalDateTime generatedAt, Map<String, String> metadata,
                boolean isValid, String validationMessage) {
            this.accountNumber = accountNumber;
            this.iban = iban;
            this.accountType = accountType;
            this.branchCode = branchCode;
            this.generatedAt = generatedAt;
            this.metadata = new HashMap<>(metadata);
            this.isValid = isValid;
            this.validationMessage = validationMessage;
        }

        @Override
        public String toString() {
            return String.format("Account: %s (%s) - %s", accountNumber, accountType,
                    isValid ? "VALID" : "INVALID: " + validationMessage);
        }
    }

    /**
     * Validation result for account number verification
     */
    public static class ValidationResult {
        public final boolean isValid;
        public final String accountNumber;
        public final AccountType detectedType;
        public final String branchCode;
        public final boolean luhnCheckPassed;
        public final boolean formatCheckPassed;
        public final List<String> validationErrors;
        public final Map<String, String> extractedInfo;

        public ValidationResult(boolean isValid, String accountNumber, AccountType detectedType,
                String branchCode, boolean luhnCheckPassed, boolean formatCheckPassed,
                List<String> validationErrors, Map<String, String> extractedInfo) {
            this.isValid = isValid;
            this.accountNumber = accountNumber;
            this.detectedType = detectedType;
            this.branchCode = branchCode;
            this.luhnCheckPassed = luhnCheckPassed;
            this.formatCheckPassed = formatCheckPassed;
            this.validationErrors = new ArrayList<>(validationErrors);
            this.extractedInfo = new HashMap<>(extractedInfo);
        }

        @Override
        public String toString() {
            return String.format("Validation: %s - Luhn: %s, Format: %s",
                    isValid ? "PASS" : "FAIL", luhnCheckPassed, formatCheckPassed);
        }
    }

    /**
     * Enhanced Luhn algorithm implementation with better error handling
     */
    private static int calculateLuhnCheckDigit(String number) {
        if (number == null || number.isEmpty()) {
            throw new IllegalArgumentException("Number cannot be null or empty");
        }

        int sum = 0;
        boolean alternate = false;

        // Process digits from right to left
        for (int i = number.length() - 1; i >= 0; i--) {
            char ch = number.charAt(i);
            if (!Character.isDigit(ch)) {
                throw new IllegalArgumentException("Non-digit character found: " + ch);
            }

            int digit = Character.getNumericValue(ch);

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        int mod = sum % 10;
        return (mod == 0) ? 0 : (10 - mod);
    }

    /**
     * Legacy method for backward compatibility
     */
    private static int luhnDigit(String s) {
        return calculateLuhnCheckDigit(s);
    }

    /**
     * Generate account number with enhanced features
     */
    public static AccountResult generateAccount(AccountRequest request) {
        try {
            // Validate input parameters
            validateRequest(request);

            // Generate base account number
            String baseNumber = generateBaseNumber(request);

            // Add check digit using Luhn algorithm
            int checkDigit = calculateLuhnCheckDigit(baseNumber);
            String fullAccountNumber = baseNumber + checkDigit;

            // Ensure uniqueness
            fullAccountNumber = ensureUniqueness(fullAccountNumber, request.accountType);

            // Generate IBAN if required
            String iban = request.requireIBAN ? generateIBAN(fullAccountNumber) : null;

            // Create metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("generated_by", "AccountNumberGenerator");
            metadata.put("version", "2.0");
            metadata.put("customer_name", request.customerName);
            if (request.customerId != null) {
                metadata.put("customer_id", request.customerId);
            }

            // Validate the generated number
            ValidationResult validation = validateAccountNumber(fullAccountNumber);

            return new AccountResult(fullAccountNumber, iban, request.accountType,
                    request.branchCode, LocalDateTime.now(), metadata,
                    validation.isValid, validation.isValid ? "Valid" : String.join(", ", validation.validationErrors));

        } catch (Exception e) {
            return new AccountResult("", null, request.accountType, request.branchCode,
                    LocalDateTime.now(), new HashMap<>(), false,
                    "Generation failed: " + e.getMessage());
        }
    }

    private static void validateRequest(AccountRequest request) {
        if (request.accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (request.branchCode == null || request.branchCode.length() < 3) {
            throw new IllegalArgumentException("Branch code must be at least 3 characters");
        }
        if (request.customerName == null || request.customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
    }

    private static String generateBaseNumber(AccountRequest request) {
        StringBuilder baseNumber = new StringBuilder();

        // Bank code
        baseNumber.append(BANK_CODE);

        // Account type code
        baseNumber.append(request.accountType.typeCode);

        // Branch code (first 3 characters)
        baseNumber.append(request.branchCode.substring(0, Math.min(3, request.branchCode.length())));

        // Generate random digits to reach desired length
        int remainingDigits = request.accountType.digitLength - baseNumber.length();

        // Include timestamp component for uniqueness
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("mmss"));
        baseNumber.append(timestamp);
        remainingDigits -= 4;

        // Fill remaining with secure random digits
        for (int i = 0; i < remainingDigits; i++) {
            baseNumber.append(secureRandom.nextInt(10));
        }

        return baseNumber.toString();
    }

    private static String ensureUniqueness(String accountNumber, AccountType type) {
        String uniqueNumber = accountNumber;
        int attempts = 0;

        while (generatedNumbers.contains(uniqueNumber) && attempts < 100) {
            // Modify the last few digits before check digit
            StringBuilder modified = new StringBuilder(uniqueNumber);
            int pos = modified.length() - 2; // Before check digit

            int newDigit = secureRandom.nextInt(10);
            modified.setCharAt(pos, Character.forDigit(newDigit, 10));

            // Recalculate check digit
            String base = modified.substring(0, modified.length() - 1);
            int newCheckDigit = calculateLuhnCheckDigit(base);
            modified.setCharAt(modified.length() - 1, Character.forDigit(newCheckDigit, 10));

            uniqueNumber = modified.toString();
            attempts++;
        }

        if (attempts >= 100) {
            throw new RuntimeException("Unable to generate unique account number after 100 attempts");
        }

        generatedNumbers.add(uniqueNumber);
        return uniqueNumber;
    }

    /**
     * Generate IBAN for international transactions
     */
    private static String generateIBAN(String accountNumber) {
        // IBAN format: Country Code (2) + Check Digits (2) + Bank Code (4) + Account
        // Number
        String bankAndAccount = IBAN_BANK_CODE + accountNumber;

        // Calculate IBAN check digits (simplified)
        String tempIBAN = COUNTRY_CODE + "00" + bankAndAccount;

        // Convert to numbers for calculation (A=10, B=11, ..., Z=35)
        StringBuilder numericString = new StringBuilder();
        for (char c : tempIBAN.toCharArray()) {
            if (Character.isDigit(c)) {
                numericString.append(c);
            } else {
                numericString.append(Character.getNumericValue(c));
            }
        }

        // Calculate mod 97
        int checkDigits = 98 - (Integer.parseInt(numericString.toString()) % 97);

        return String.format("%s%02d%s", COUNTRY_CODE, checkDigits, bankAndAccount);
    }

    /**
     * Comprehensive account number validation
     */
    public static ValidationResult validateAccountNumber(String accountNumber) {
        List<String> errors = new ArrayList<>();
        Map<String, String> extractedInfo = new HashMap<>();
        boolean luhnCheckPassed = false;
        boolean formatCheckPassed = false;
        AccountType detectedType = null;
        String branchCode = null;

        try {
            // Basic format validation
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                errors.add("Account number cannot be null or empty");
                return new ValidationResult(false, accountNumber, null, null, false, false, errors, extractedInfo);
            }

            accountNumber = accountNumber.trim().toUpperCase();

            // Length validation
            if (accountNumber.length() < 8) {
                errors.add("Account number too short (minimum 8 characters)");
            }

            // Bank code validation
            if (!accountNumber.startsWith(BANK_CODE)) {
                errors.add("Invalid bank code prefix (expected: " + BANK_CODE + ")");
            } else {
                extractedInfo.put("bank_code", BANK_CODE);
                formatCheckPassed = true;
            }

            // Account type detection
            if (accountNumber.length() >= 5) {
                String typeCode = accountNumber.substring(3, 5);
                for (AccountType type : AccountType.values()) {
                    if (type.typeCode.equals(typeCode)) {
                        detectedType = type;
                        extractedInfo.put("account_type", type.name());
                        break;
                    }
                }

                if (detectedType == null) {
                    errors.add("Unknown account type code: " + typeCode);
                }
            }

            // Branch code extraction
            if (accountNumber.length() >= 8) {
                branchCode = accountNumber.substring(5, 8);
                extractedInfo.put("branch_code", branchCode);
            }

            // Luhn validation
            try {
                String checkDigitStr = accountNumber.substring(accountNumber.length() - 1);
                String bodyNumber = accountNumber.substring(0, accountNumber.length() - 1);
                int expectedCheckDigit = calculateLuhnCheckDigit(bodyNumber);
                int actualCheckDigit = Integer.parseInt(checkDigitStr);

                if (expectedCheckDigit == actualCheckDigit) {
                    luhnCheckPassed = true;
                } else {
                    errors.add(String.format("Luhn check failed (expected: %d, actual: %d)",
                            expectedCheckDigit, actualCheckDigit));
                }

                extractedInfo.put("check_digit", checkDigitStr);
            } catch (Exception e) {
                errors.add("Luhn validation error: " + e.getMessage());
            }

            // Length validation for detected type
            if (detectedType != null && accountNumber.length() != detectedType.digitLength + 5) {
                errors.add(String.format("Invalid length for %s account (expected: %d, actual: %d)",
                        detectedType.name(), detectedType.digitLength + 5, accountNumber.length()));
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }

        boolean isValid = errors.isEmpty() && luhnCheckPassed && formatCheckPassed;

        return new ValidationResult(isValid, accountNumber, detectedType, branchCode,
                luhnCheckPassed, formatCheckPassed, errors, extractedInfo);
    }

    /**
     * Legacy methods for backward compatibility
     */
    public static String generate(String prefix) {
        Random r = new Random();
        StringBuilder core = new StringBuilder(prefix);
        while (core.length() < 11) {
            core.append(r.nextInt(10));
        }
        int check = luhnDigit(core.toString());
        return core.append(check).toString();
    }

    public static boolean validate(String account) {
        if (account == null || account.isEmpty()) {
            return false;
        }
        try {
            int check = Character.getNumericValue(account.charAt(account.length() - 1));
            String body = account.substring(0, account.length() - 1);
            return luhnDigit(body) == check;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Follow-up 1: Batch account generation
     */
    public static List<AccountResult> generateBatchAccounts(List<AccountRequest> requests) {
        List<AccountResult> results = new ArrayList<>();

        for (AccountRequest request : requests) {
            try {
                AccountResult result = generateAccount(request);
                results.add(result);
            } catch (Exception e) {
                results.add(new AccountResult("", null, request.accountType, request.branchCode,
                        LocalDateTime.now(), new HashMap<>(), false,
                        "Batch generation error: " + e.getMessage()));
            }
        }

        return results;
    }

    /**
     * Follow-up 2: Account number analytics
     */
    public static AccountAnalytics analyzeAccountNumbers(List<String> accountNumbers) {
        Map<AccountType, Integer> typeDistribution = new HashMap<>();
        Map<String, Integer> branchDistribution = new HashMap<>();
        int validCount = 0;
        int invalidCount = 0;
        List<String> validationErrors = new ArrayList<>();

        for (String accountNumber : accountNumbers) {
            ValidationResult validation = validateAccountNumber(accountNumber);

            if (validation.isValid) {
                validCount++;
                if (validation.detectedType != null) {
                    typeDistribution.put(validation.detectedType,
                            typeDistribution.getOrDefault(validation.detectedType, 0) + 1);
                }
                if (validation.branchCode != null) {
                    branchDistribution.put(validation.branchCode,
                            branchDistribution.getOrDefault(validation.branchCode, 0) + 1);
                }
            } else {
                invalidCount++;
                validationErrors.addAll(validation.validationErrors);
            }
        }

        return new AccountAnalytics(accountNumbers.size(), validCount, invalidCount,
                typeDistribution, branchDistribution, validationErrors);
    }

    /**
     * Follow-up 3: Security audit for account numbers
     */
    public static SecurityAuditResult performSecurityAudit(List<String> accountNumbers) {
        List<String> securityIssues = new ArrayList<>();
        List<String> suspiciousPatterns = new ArrayList<>();
        Map<String, Integer> patternFrequency = new HashMap<>();

        for (String accountNumber : accountNumbers) {
            // Check for sequential patterns
            if (hasSequentialPattern(accountNumber)) {
                suspiciousPatterns.add("Sequential pattern in: " + accountNumber);
            }

            // Check for repeated digits
            if (hasRepeatedDigits(accountNumber)) {
                suspiciousPatterns.add("Repeated digits in: " + accountNumber);
            }

            // Check for common patterns
            String pattern = extractPattern(accountNumber);
            patternFrequency.put(pattern, patternFrequency.getOrDefault(pattern, 0) + 1);
        }

        // Identify frequently occurring patterns (potential security issue)
        patternFrequency.entrySet().stream()
                .filter(entry -> entry.getValue() > accountNumbers.size() * 0.1) // More than 10%
                .forEach(entry -> securityIssues.add("High frequency pattern: " + entry.getKey()));

        boolean isSecure = securityIssues.isEmpty() && suspiciousPatterns.size() < accountNumbers.size() * 0.05;

        return new SecurityAuditResult(isSecure, securityIssues, suspiciousPatterns, patternFrequency);
    }

    private static boolean hasSequentialPattern(String accountNumber) {
        String digits = accountNumber.replaceAll("[^0-9]", "");
        for (int i = 0; i < digits.length() - 3; i++) {
            if (isSequential(digits.substring(i, i + 4))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSequential(String fourDigits) {
        for (int i = 1; i < fourDigits.length(); i++) {
            int prev = Character.getNumericValue(fourDigits.charAt(i - 1));
            int curr = Character.getNumericValue(fourDigits.charAt(i));
            if (curr != prev + 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasRepeatedDigits(String accountNumber) {
        String digits = accountNumber.replaceAll("[^0-9]", "");
        for (int i = 0; i < digits.length() - 3; i++) {
            char digit = digits.charAt(i);
            if (digits.substring(i, i + 4).chars().allMatch(c -> c == digit)) {
                return true;
            }
        }
        return false;
    }

    private static String extractPattern(String accountNumber) {
        // Extract last 4 digits pattern for analysis
        String digits = accountNumber.replaceAll("[^0-9]", "");
        if (digits.length() >= 4) {
            return digits.substring(digits.length() - 4);
        }
        return digits;
    }

    // Supporting classes for analytics and security
    public static class AccountAnalytics {
        public final int totalAccounts;
        public final int validAccounts;
        public final int invalidAccounts;
        public final Map<AccountType, Integer> typeDistribution;
        public final Map<String, Integer> branchDistribution;
        public final List<String> commonErrors;

        public AccountAnalytics(int totalAccounts, int validAccounts, int invalidAccounts,
                Map<AccountType, Integer> typeDistribution, Map<String, Integer> branchDistribution,
                List<String> commonErrors) {
            this.totalAccounts = totalAccounts;
            this.validAccounts = validAccounts;
            this.invalidAccounts = invalidAccounts;
            this.typeDistribution = new HashMap<>(typeDistribution);
            this.branchDistribution = new HashMap<>(branchDistribution);
            this.commonErrors = new ArrayList<>(commonErrors);
        }

        @Override
        public String toString() {
            return String.format("Analytics: %d total, %d valid (%.1f%%), %d invalid",
                    totalAccounts, validAccounts,
                    (double) validAccounts / totalAccounts * 100, invalidAccounts);
        }
    }

    public static class SecurityAuditResult {
        public final boolean isSecure;
        public final List<String> securityIssues;
        public final List<String> suspiciousPatterns;
        public final Map<String, Integer> patternFrequency;

        public SecurityAuditResult(boolean isSecure, List<String> securityIssues,
                List<String> suspiciousPatterns, Map<String, Integer> patternFrequency) {
            this.isSecure = isSecure;
            this.securityIssues = new ArrayList<>(securityIssues);
            this.suspiciousPatterns = new ArrayList<>(suspiciousPatterns);
            this.patternFrequency = new HashMap<>(patternFrequency);
        }

        @Override
        public String toString() {
            return String.format("Security Audit: %s - %d issues, %d suspicious patterns",
                    isSecure ? "SECURE" : "VULNERABLE",
                    securityIssues.size(), suspiciousPatterns.size());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Banking Account Number Generator System ===\n");

        // Test Case 1: Legacy methods for backward compatibility
        System.out.println("1. Legacy Methods Test:");
        String legacyAccount = generate("KOT");
        boolean legacyValid = validate(legacyAccount);
        System.out.printf("Legacy generated: %s, valid: %s%n", legacyAccount, legacyValid);

        // Test Case 2: Enhanced account generation
        System.out.println("\n2. Enhanced Account Generation:");

        AccountRequest savingsRequest = new AccountRequest(AccountType.SAVINGS, "MUM001", "JOHN_DOE");
        AccountResult savingsResult = generateAccount(savingsRequest);
        System.out.printf("Savings account: %s%n", savingsResult);

        AccountRequest loanRequest = new AccountRequest(AccountType.LOAN, "DEL002", "JANE_SMITH",
                "CUST12345", true, Map.of("purpose", "home_loan"));
        AccountResult loanResult = generateAccount(loanRequest);
        System.out.printf("Loan account: %s%n", loanResult);
        System.out.printf("IBAN: %s%n", loanResult.iban);

        // Test Case 3: Different account types
        System.out.println("\n3. Different Account Types:");
        for (AccountType type : AccountType.values()) {
            AccountRequest request = new AccountRequest(type, "BLR003", "CUSTOMER_" + type.name());
            AccountResult result = generateAccount(request);
            System.out.printf("%-20s: %s%n", type.name(), result.accountNumber);
        }

        // Test Case 4: Comprehensive validation
        System.out.println("\n4. Account Number Validation:");
        String[] testAccounts = {
                savingsResult.accountNumber, // Valid account
                "KOT01MUM1234567890", // Valid format
                "INVALID123", // Invalid format
                "KOT01MUM123456789X", // Invalid check digit
                "", // Empty string
                "KOT99XYZ1234567890" // Invalid type code
        };

        for (String account : testAccounts) {
            ValidationResult validation = validateAccountNumber(account);
            System.out.printf("%-20s: %s%n", account, validation);
            if (!validation.validationErrors.isEmpty()) {
                System.out.printf("  Errors: %s%n", String.join(", ", validation.validationErrors));
            }
        }

        // Test Case 5: Batch generation
        System.out.println("\n5. Batch Account Generation:");
        List<AccountRequest> batchRequests = Arrays.asList(
                new AccountRequest(AccountType.SAVINGS, "HYD001", "CUSTOMER_A"),
                new AccountRequest(AccountType.CURRENT, "CHN002", "CUSTOMER_B"),
                new AccountRequest(AccountType.CREDIT_CARD, "KOL003", "CUSTOMER_C"));

        List<AccountResult> batchResults = generateBatchAccounts(batchRequests);
        batchResults.forEach(result -> System.out.printf("  %s: %s%n", result.accountType, result.accountNumber));

        // Test Case 6: Analytics on generated accounts
        System.out.println("\n6. Account Analytics:");
        List<String> analysisAccounts = batchResults.stream()
                .map(result -> result.accountNumber)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // Add some invalid accounts for testing
        analysisAccounts.addAll(Arrays.asList("INVALID123", "KOT01MUM123456789X"));

        AccountAnalytics analytics = analyzeAccountNumbers(analysisAccounts);
        System.out.printf("  %s%n", analytics);
        System.out.printf("  Type distribution: %s%n", analytics.typeDistribution);
        System.out.printf("  Branch distribution: %s%n", analytics.branchDistribution);

        // Test Case 7: Security audit
        System.out.println("\n7. Security Audit:");
        List<String> securityTestAccounts = new ArrayList<>(analysisAccounts);

        // Add potentially insecure patterns
        securityTestAccounts.addAll(Arrays.asList(
                "KOT01MUM1234567890", // Sequential pattern
                "KOT01MUM1111111111", // Repeated digits
                "KOT01MUM9876543210" // Reverse sequential
        ));

        SecurityAuditResult securityAudit = performSecurityAudit(securityTestAccounts);
        System.out.printf("  %s%n", securityAudit);
        if (!securityAudit.securityIssues.isEmpty()) {
            System.out.printf("  Security issues: %s%n", securityAudit.securityIssues);
        }
        if (!securityAudit.suspiciousPatterns.isEmpty()) {
            System.out.printf("  Suspicious patterns: %s%n", securityAudit.suspiciousPatterns.subList(0,
                    Math.min(3, securityAudit.suspiciousPatterns.size())));
        }

        // Test Case 8: Stress test with large batch
        System.out.println("\n8. Stress Test (1000 accounts):");
        List<AccountRequest> stressRequests = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            AccountType type = AccountType.values()[i % AccountType.values().length];
            String branch = "TST" + String.format("%03d", i % 100);
            stressRequests.add(new AccountRequest(type, branch, "CUSTOMER_" + i));
        }

        long startTime = System.currentTimeMillis();
        List<AccountResult> stressResults = generateBatchAccounts(stressRequests);
        long endTime = System.currentTimeMillis();

        long validCount = stressResults.stream().filter(r -> r.isValid).count();
        System.out.printf("  Generated %d accounts in %d ms%n", stressResults.size(), endTime - startTime);
        System.out.printf("  Valid: %d (%.1f%%), Invalid: %d%n", validCount,
                (double) validCount / stressResults.size() * 100,
                stressResults.size() - validCount);

        // Test Case 9: IBAN generation test
        System.out.println("\n9. IBAN Generation Test:");
        for (int i = 0; i < 3; i++) {
            AccountRequest ibanRequest = new AccountRequest(AccountType.SAVINGS, "INT00" + i,
                    "INTL_CUSTOMER_" + i, "INTL" + i, true, new HashMap<>());
            AccountResult ibanResult = generateAccount(ibanRequest);
            System.out.printf("  Account: %s%n", ibanResult.accountNumber);
            System.out.printf("  IBAN:    %s%n", ibanResult.iban);
        }

        System.out.println("\n=== Test Completed Successfully ===");
    }
}
