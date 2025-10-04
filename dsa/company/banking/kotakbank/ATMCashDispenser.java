package company.banking.kotakbank;

import java.util.*;

/**
 * Kotak Bank SDE3 Interview Question #5
 * 
 * Problem: Banking System Design - ATM Cash Dispensing
 * LeetCode Equivalent: Combination Sum / Coin Change variants
 * Source: TryExponent Banking Interview Questions
 * 
 * Banking Context:
 * Design an ATM cash dispensing system that can dispense the requested amount
 * using the available denominations with minimum number of notes.
 * Consider scenarios like insufficient cash, invalid amounts, and optimization.
 * 
 * Interview Focus:
 * - Greedy vs Dynamic Programming approaches
 * - ATM hardware constraints and cash management
 * - Error handling and edge cases
 * - System design for distributed ATM network
 * 
 * Difficulty: Medium
 * Expected Time: 30-40 minutes
 * 
 * Follow-up Questions:
 * 1. How would you handle concurrent ATM transactions?
 * 2. What if certain denominations run out of cash?
 * 3. How would you implement cash replenishment algorithms?
 * 4. How would you design for a network of 10,000+ ATMs?
 */
public class ATMCashDispenser {

    /**
     * ATM Cash Cartridge - represents cash available in each denomination
     */
    public static class CashCartridge {
        int denomination;
        int availableNotes;
        int maxCapacity;

        public CashCartridge(int denomination, int availableNotes, int maxCapacity) {
            this.denomination = denomination;
            this.availableNotes = availableNotes;
            this.maxCapacity = maxCapacity;
        }

        public boolean canDispense(int notesRequired) {
            return availableNotes >= notesRequired;
        }

        public void dispenseNotes(int notesRequired) {
            if (canDispense(notesRequired)) {
                availableNotes -= notesRequired;
            } else {
                throw new IllegalStateException("Insufficient notes in cartridge: " + denomination);
            }
        }

        public int getTotalValue() {
            return denomination * availableNotes;
        }

        @Override
        public String toString() {
            return String.format("₹%d: %d notes (₹%d total)",
                    denomination, availableNotes, getTotalValue());
        }
    }

    /**
     * Cash Dispense Result
     */
    public static class DispenseResult {
        boolean success;
        Map<Integer, Integer> dispensedNotes;
        String errorMessage;
        int totalAmount;
        int totalNotes;

        public DispenseResult(boolean success, Map<Integer, Integer> dispensedNotes,
                String errorMessage, int totalAmount) {
            this.success = success;
            this.dispensedNotes = dispensedNotes != null ? dispensedNotes : new HashMap<>();
            this.errorMessage = errorMessage;
            this.totalAmount = totalAmount;
            this.totalNotes = this.dispensedNotes.values().stream().mapToInt(Integer::intValue).sum();
        }

        @Override
        public String toString() {
            if (!success) {
                return "❌ Transaction Failed: " + errorMessage;
            }

            StringBuilder result = new StringBuilder("✅ Cash Dispensed Successfully:\n");
            for (Map.Entry<Integer, Integer> entry : dispensedNotes.entrySet()) {
                result.append(String.format("   ₹%d × %d = ₹%d\n",
                        entry.getKey(), entry.getValue(),
                        entry.getKey() * entry.getValue()));
            }
            result.append(String.format("   Total: ₹%d (%d notes)", totalAmount, totalNotes));
            return result.toString();
        }
    }

    // ATM Configuration
    private final List<CashCartridge> cartridges;
    private final int minDispenseAmount;
    private final int maxDispenseAmount;
    private final Set<Integer> validDenominations;

    public ATMCashDispenser() {
        this.cartridges = new ArrayList<>();
        this.minDispenseAmount = 100; // Minimum ₹100
        this.maxDispenseAmount = 50000; // Maximum ₹50,000
        this.validDenominations = new HashSet<>();

        // Initialize with Indian currency denominations
        initializeATM();
    }

    private void initializeATM() {
        // Indian ATM typically dispenses these denominations
        cartridges.add(new CashCartridge(2000, 50, 100)); // ₹2000 notes
        cartridges.add(new CashCartridge(500, 200, 300)); // ₹500 notes
        cartridges.add(new CashCartridge(200, 100, 200)); // ₹200 notes
        cartridges.add(new CashCartridge(100, 500, 800)); // ₹100 notes

        // Sort cartridges by denomination (descending) for greedy approach
        cartridges.sort((a, b) -> Integer.compare(b.denomination, a.denomination));

        // Build valid denominations set
        for (CashCartridge cartridge : cartridges) {
            validDenominations.add(cartridge.denomination);
        }
    }

    /**
     * Approach 1: Greedy Algorithm (Optimal for most ATM scenarios)
     * Time Complexity: O(n) where n is number of denominations
     * Space Complexity: O(n) for result storage
     * 
     * Banking Application: Fast cash dispensing for regular transactions
     */
    public DispenseResult dispenseGreedy(int amount) {
        // Validation checks
        if (!isValidAmount(amount)) {
            return new DispenseResult(false, null,
                    "Invalid amount. Must be between ₹" + minDispenseAmount +
                            " and ₹" + maxDispenseAmount + " in multiples of ₹100",
                    0);
        }

        if (!hasSufficientCash(amount)) {
            return new DispenseResult(false, null,
                    "Insufficient cash in ATM. Available: ₹" + getTotalCashAvailable(), 0);
        }

        Map<Integer, Integer> dispensePlan = new HashMap<>();
        int remainingAmount = amount;

        // Greedy approach: Use largest denominations first
        for (CashCartridge cartridge : cartridges) {
            if (remainingAmount == 0)
                break;

            int notesNeeded = remainingAmount / cartridge.denomination;
            int notesToDispense = Math.min(notesNeeded, cartridge.availableNotes);

            if (notesToDispense > 0) {
                dispensePlan.put(cartridge.denomination, notesToDispense);
                remainingAmount -= notesToDispense * cartridge.denomination;
            }
        }

        if (remainingAmount > 0) {
            return new DispenseResult(false, null,
                    "Cannot dispense exact amount with available denominations", 0);
        }

        // Execute the dispensing
        for (Map.Entry<Integer, Integer> entry : dispensePlan.entrySet()) {
            int denomination = entry.getKey();
            int notesToDispense = entry.getValue();

            for (CashCartridge cartridge : cartridges) {
                if (cartridge.denomination == denomination) {
                    cartridge.dispenseNotes(notesToDispense);
                    break;
                }
            }
        }

        return new DispenseResult(true, dispensePlan, null, amount);
    }

    /**
     * Approach 2: Dynamic Programming (Optimal note count)
     * Time Complexity: O(amount * denominations)
     * Space Complexity: O(amount)
     * 
     * Banking Application: Optimal dispensing when note conservation is priority
     */
    public DispenseResult dispenseOptimal(int amount) {
        if (!isValidAmount(amount)) {
            return new DispenseResult(false, null,
                    "Invalid amount. Must be between ₹" + minDispenseAmount +
                            " and ₹" + maxDispenseAmount + " in multiples of ₹100",
                    0);
        }

        // DP array: dp[i] = minimum notes needed for amount i
        int[] dp = new int[amount + 1];
        int[] parent = new int[amount + 1]; // For reconstruction
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int amt = 1; amt <= amount; amt++) {
            for (CashCartridge cartridge : cartridges) {
                int denom = cartridge.denomination;
                if (denom <= amt && cartridge.availableNotes > 0 && dp[amt - denom] != Integer.MAX_VALUE) {
                    if (dp[amt - denom] + 1 < dp[amt]) {
                        dp[amt] = dp[amt - denom] + 1;
                        parent[amt] = denom;
                    }
                }
            }
        }

        if (dp[amount] == Integer.MAX_VALUE) {
            return new DispenseResult(false, null,
                    "Cannot dispense amount with available denominations", 0);
        }

        // Reconstruct solution
        Map<Integer, Integer> dispensePlan = new HashMap<>();
        int currentAmount = amount;

        while (currentAmount > 0) {
            int denomination = parent[currentAmount];
            dispensePlan.put(denomination, dispensePlan.getOrDefault(denomination, 0) + 1);
            currentAmount -= denomination;
        }

        // Validate and execute dispensing
        for (Map.Entry<Integer, Integer> entry : dispensePlan.entrySet()) {
            int denomination = entry.getKey();
            int notesToDispense = entry.getValue();

            for (CashCartridge cartridge : cartridges) {
                if (cartridge.denomination == denomination) {
                    if (!cartridge.canDispense(notesToDispense)) {
                        return new DispenseResult(false, null,
                                "Insufficient ₹" + denomination + " notes", 0);
                    }
                    cartridge.dispenseNotes(notesToDispense);
                    break;
                }
            }
        }

        return new DispenseResult(true, dispensePlan, null, amount);
    }

    /**
     * ATM Status and Management
     */
    public void printATMStatus() {
        System.out.println("🏧 ATM CASH STATUS");
        System.out.println("=".repeat(40));

        int totalCash = 0;
        int totalNotes = 0;

        for (CashCartridge cartridge : cartridges) {
            System.out.println(cartridge);
            totalCash += cartridge.getTotalValue();
            totalNotes += cartridge.availableNotes;
        }

        System.out.println("-".repeat(40));
        System.out.printf("Total Cash: ₹%,d (%d notes)\n", totalCash, totalNotes);

        // Cash level warnings
        for (CashCartridge cartridge : cartridges) {
            double fillPercentage = (cartridge.availableNotes * 100.0) / cartridge.maxCapacity;
            if (fillPercentage < 20) {
                System.out.printf("⚠️  Low cash warning: ₹%d cartridge at %.1f%% capacity\n",
                        cartridge.denomination, fillPercentage);
            }
        }
    }

    private boolean isValidAmount(int amount) {
        return amount >= minDispenseAmount &&
                amount <= maxDispenseAmount &&
                amount % 100 == 0; // Must be multiple of 100
    }

    private boolean hasSufficientCash(int amount) {
        return getTotalCashAvailable() >= amount;
    }

    private int getTotalCashAvailable() {
        return cartridges.stream().mapToInt(CashCartridge::getTotalValue).sum();
    }

    /**
     * Replenish ATM cash (for maintenance operations)
     */
    public void replenishCash(int denomination, int additionalNotes) {
        for (CashCartridge cartridge : cartridges) {
            if (cartridge.denomination == denomination) {
                int newTotal = cartridge.availableNotes + additionalNotes;
                if (newTotal <= cartridge.maxCapacity) {
                    cartridge.availableNotes = newTotal;
                    System.out.printf("✅ Replenished ₹%d cartridge with %d notes\n",
                            denomination, additionalNotes);
                } else {
                    System.out.printf("❌ Cannot replenish: Exceeds maximum capacity of %d notes\n",
                            cartridge.maxCapacity);
                }
                return;
            }
        }
        System.out.printf("❌ Invalid denomination: ₹%d\n", denomination);
    }

    /**
     * Test ATM functionality with various scenarios
     */
    public void testATMFunctionality() {
        System.out.println("🏧 TESTING ATM CASH DISPENSER");
        System.out.println("=".repeat(50));

        printATMStatus();
        System.out.println();

        // Test scenarios
        int[] testAmounts = { 500, 1300, 2700, 5500, 15000, 99, 100000 };
        String[] scenarios = {
                "Simple withdrawal",
                "Mixed denominations",
                "Multiple large notes",
                "Complex combination",
                "Large amount",
                "Invalid amount (below minimum)",
                "Amount exceeds ATM capacity"
        };

        for (int i = 0; i < testAmounts.length; i++) {
            System.out.println("Test " + (i + 1) + ": " + scenarios[i] + " - ₹" + testAmounts[i]);
            System.out.println("-".repeat(30));

            // Test both approaches
            System.out.println("Greedy Approach:");
            DispenseResult greedyResult = dispenseGreedy(testAmounts[i]);
            System.out.println(greedyResult);

            System.out.println("\nOptimal Approach:");
            // Reset ATM state for fair comparison
            initializeATM();
            DispenseResult optimalResult = dispenseOptimal(testAmounts[i]);
            System.out.println(optimalResult);

            System.out.println("\n" + "=".repeat(50) + "\n");

            // Reset for next test
            initializeATM();
        }
    }

    /**
     * Banking system integration discussion
     */
    public void discussBankingIntegration() {
        System.out.println("🏦 BANKING SYSTEM INTEGRATION");
        System.out.println("=".repeat(50));

        System.out.println("1. REAL-TIME ACCOUNT VALIDATION:");
        System.out.println("   - Account balance verification");
        System.out.println("   - Daily withdrawal limit checks");
        System.out.println("   - Account status validation (active/blocked)");
        System.out.println("   - PIN verification and attempt tracking");

        System.out.println("\n2. TRANSACTION PROCESSING:");
        System.out.println("   - Real-time debit from customer account");
        System.out.println("   - Transaction logging and audit trail");
        System.out.println("   - Network communication with bank servers");
        System.out.println("   - Rollback mechanisms for failed transactions");

        System.out.println("\n3. SECURITY MEASURES:");
        System.out.println("   - End-to-end encryption for all communications");
        System.out.println("   - Card skimming detection algorithms");
        System.out.println("   - Suspicious activity monitoring");
        System.out.println("   - Physical tampering detection");

        System.out.println("\n4. OPERATIONAL CONSIDERATIONS:");
        System.out.println("   - Cash forecasting and replenishment scheduling");
        System.out.println("   - Remote monitoring and diagnostics");
        System.out.println("   - Fault tolerance and offline mode capabilities");
        System.out.println("   - Regulatory compliance and reporting");

        System.out.println("\n5. SCALABILITY AND PERFORMANCE:");
        System.out.println("   - Network latency optimization");
        System.out.println("   - Load balancing across ATM networks");
        System.out.println("   - Caching strategies for frequent operations");
        System.out.println("   - Disaster recovery and business continuity");
    }

    public static void main(String[] args) {
        System.out.println("🏦 KOTAK BANK SDE3 INTERVIEW QUESTION");
        System.out.println("📋 Problem: ATM Cash Dispensing System");
        System.out.println("🔗 LeetCode Equivalent: Coin Change + System Design");
        System.out.println();

        ATMCashDispenser atm = new ATMCashDispenser();
        atm.testATMFunctionality();
        atm.discussBankingIntegration();

        System.out.println("\n💡 INTERVIEW TIPS:");
        System.out.println("1. Discuss both algorithmic approaches and their trade-offs");
        System.out.println("2. Consider real-world ATM constraints and edge cases");
        System.out.println("3. Address security, reliability, and regulatory requirements");
        System.out.println("4. Think about system design for large ATM networks");
        System.out.println("5. Consider operational aspects like cash replenishment");
        System.out.println("6. Discuss integration with core banking systems");
    }
}
