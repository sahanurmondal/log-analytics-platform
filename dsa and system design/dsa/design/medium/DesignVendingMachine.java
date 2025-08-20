package design.medium;

import java.util.*;

/**
 * Design Vending Machine
 *
 * Description: Design a vending machine that supports:
 * - Product inventory management
 * - Coin/bill acceptance and change calculation
 * - Product selection and dispensing
 * - Admin operations
 * 
 * Constraints:
 * - Support multiple products
 * - Handle various denominations
 * - Calculate optimal change
 *
 * Follow-up:
 * - How to handle maintenance mode?
 * - Remote monitoring capabilities?
 * 
 * Time Complexity: O(1) for most operations, O(n) for change calculation
 * Space Complexity: O(products + denominations)
 * 
 * Company Tags: System Design Interview
 */
public class DesignVendingMachine {

    enum MachineState {
        IDLE, COLLECTING_PAYMENT, DISPENSING, OUT_OF_ORDER, MAINTENANCE
    }

    enum TransactionResult {
        SUCCESS, INSUFFICIENT_FUNDS, PRODUCT_UNAVAILABLE, EXACT_CHANGE_REQUIRED, CANCELLED
    }

    class Product {
        String productId;
        String name;
        int price; // in cents
        int quantity;
        int maxCapacity;

        Product(String productId, String name, int price, int maxCapacity) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.maxCapacity = maxCapacity;
            this.quantity = 0;
        }

        boolean isAvailable() {
            return quantity > 0;
        }

        boolean dispense() {
            if (quantity > 0) {
                quantity--;
                return true;
            }
            return false;
        }

        void restock(int amount) {
            quantity = Math.min(maxCapacity, quantity + amount);
        }
    }

    class CoinInventory {
        Map<Integer, Integer> coins; // denomination -> count

        CoinInventory() {
            coins = new HashMap<>();
            // Initialize with common US denominations (in cents)
            coins.put(25, 0); // quarters
            coins.put(10, 0); // dimes
            coins.put(5, 0); // nickels
            coins.put(1, 0); // pennies
            coins.put(100, 0); // dollars
            coins.put(500, 0); // five dollars
            coins.put(1000, 0); // ten dollars
            coins.put(2000, 0); // twenty dollars
        }

        void addCoin(int denomination, int count) {
            coins.put(denomination, coins.getOrDefault(denomination, 0) + count);
        }

        boolean hasSufficientChange(int changeAmount) {
            return calculateChange(changeAmount) != null;
        }

        Map<Integer, Integer> calculateChange(int changeAmount) {
            Map<Integer, Integer> change = new HashMap<>();

            // Sort denominations in descending order
            List<Integer> denominations = new ArrayList<>(coins.keySet());
            denominations.sort(Collections.reverseOrder());

            for (int denomination : denominations) {
                int availableCoins = coins.get(denomination);
                int coinsNeeded = Math.min(changeAmount / denomination, availableCoins);

                if (coinsNeeded > 0) {
                    change.put(denomination, coinsNeeded);
                    changeAmount -= coinsNeeded * denomination;
                }
            }

            return changeAmount == 0 ? change : null;
        }

        void dispenseChange(Map<Integer, Integer> change) {
            for (Map.Entry<Integer, Integer> entry : change.entrySet()) {
                int denomination = entry.getKey();
                int count = entry.getValue();
                coins.put(denomination, coins.get(denomination) - count);
            }
        }

        int getTotalValue() {
            return coins.entrySet().stream()
                    .mapToInt(entry -> entry.getKey() * entry.getValue())
                    .sum();
        }
    }

    class Transaction {
        String transactionId;
        String productId;
        int paidAmount;
        int changeAmount;
        TransactionResult result;
        long timestamp;

        Transaction(String productId) {
            this.transactionId = UUID.randomUUID().toString();
            this.productId = productId;
            this.paidAmount = 0;
            this.changeAmount = 0;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private Map<String, Product> products;
    private CoinInventory coinInventory;
    private MachineState currentState;
    private Transaction currentTransaction;
    private List<Transaction> transactionHistory;
    private boolean exactChangeOnly;

    public DesignVendingMachine() {
        products = new HashMap<>();
        coinInventory = new CoinInventory();
        currentState = MachineState.IDLE;
        currentTransaction = null;
        transactionHistory = new ArrayList<>();
        exactChangeOnly = false;
    }

    // Admin operations
    public void addProduct(String productId, String name, int price, int maxCapacity) {
        products.put(productId, new Product(productId, name, price, maxCapacity));
    }

    public void restockProduct(String productId, int quantity) {
        Product product = products.get(productId);
        if (product != null) {
            product.restock(quantity);
        }
    }

    public void loadCoins(int denomination, int count) {
        coinInventory.addCoin(denomination, count);
        updateExactChangeStatus();
    }

    private void updateExactChangeStatus() {
        // Check if we can make change for common amounts
        int[] testAmounts = { 5, 10, 15, 20, 25, 50, 75 };
        exactChangeOnly = false;

        for (int amount : testAmounts) {
            if (!coinInventory.hasSufficientChange(amount)) {
                exactChangeOnly = true;
                break;
            }
        }
    }

    // Customer operations
    public List<Product> getAvailableProducts() {
        return products.values().stream()
                .filter(Product::isAvailable)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public TransactionResult selectProduct(String productId) {
        if (currentState != MachineState.IDLE) {
            return TransactionResult.CANCELLED;
        }

        Product product = products.get(productId);
        if (product == null || !product.isAvailable()) {
            return TransactionResult.PRODUCT_UNAVAILABLE;
        }

        currentTransaction = new Transaction(productId);
        currentState = MachineState.COLLECTING_PAYMENT;

        return TransactionResult.SUCCESS;
    }

    public TransactionResult insertCoin(int denomination) {
        if (currentState != MachineState.COLLECTING_PAYMENT || currentTransaction == null) {
            return TransactionResult.CANCELLED;
        }

        currentTransaction.paidAmount += denomination;

        Product product = products.get(currentTransaction.productId);
        if (currentTransaction.paidAmount >= product.price) {
            return processPurchase();
        }

        return TransactionResult.SUCCESS; // Continue collecting payment
    }

    private TransactionResult processPurchase() {
        Product product = products.get(currentTransaction.productId);
        int changeAmount = currentTransaction.paidAmount - product.price;

        // Check if exact change is required and customer didn't provide exact amount
        if (exactChangeOnly && changeAmount > 0) {
            refundTransaction();
            return TransactionResult.EXACT_CHANGE_REQUIRED;
        }

        // Check if we can make change
        if (changeAmount > 0 && !coinInventory.hasSufficientChange(changeAmount)) {
            refundTransaction();
            return TransactionResult.EXACT_CHANGE_REQUIRED;
        }

        // Dispense product
        if (!product.dispense()) {
            refundTransaction();
            return TransactionResult.PRODUCT_UNAVAILABLE;
        }

        // Add payment to inventory
        coinInventory.addCoin(currentTransaction.paidAmount, 1);

        // Dispense change
        if (changeAmount > 0) {
            Map<Integer, Integer> change = coinInventory.calculateChange(changeAmount);
            if (change != null) {
                coinInventory.dispenseChange(change);
                currentTransaction.changeAmount = changeAmount;
            }
        }

        currentTransaction.result = TransactionResult.SUCCESS;
        transactionHistory.add(currentTransaction);

        currentState = MachineState.DISPENSING;

        // Simulate dispensing delay
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                currentState = MachineState.IDLE;
                currentTransaction = null;
            }
        }, 2000);

        updateExactChangeStatus();
        return TransactionResult.SUCCESS;
    }

    public TransactionResult cancelTransaction() {
        if (currentTransaction != null) {
            refundTransaction();
            return TransactionResult.CANCELLED;
        }
        return TransactionResult.SUCCESS;
    }

    private void refundTransaction() {
        // In a real machine, this would return the coins to the coin return
        currentTransaction.result = TransactionResult.CANCELLED;
        transactionHistory.add(currentTransaction);

        currentState = MachineState.IDLE;
        currentTransaction = null;
    }

    public int getRemainingPayment() {
        if (currentTransaction == null)
            return 0;

        Product product = products.get(currentTransaction.productId);
        return Math.max(0, product.price - currentTransaction.paidAmount);
    }

    // Status and reporting
    public MachineState getCurrentState() {
        return currentState;
    }

    public boolean isExactChangeOnly() {
        return exactChangeOnly;
    }

    public Map<String, Object> getMachineStatus() {
        Map<String, Object> status = new HashMap<>();

        status.put("state", currentState);
        status.put("exactChangeOnly", exactChangeOnly);
        status.put("availableProducts", getAvailableProducts().size());
        status.put("totalCashValue", coinInventory.getTotalValue());
        status.put("transactionCount", transactionHistory.size());

        if (currentTransaction != null) {
            status.put("currentTransaction", Map.of(
                    "productId", currentTransaction.productId,
                    "paidAmount", currentTransaction.paidAmount,
                    "remainingPayment", getRemainingPayment()));
        }

        return status;
    }

    public Map<String, Object> getInventoryReport() {
        Map<String, Object> report = new HashMap<>();

        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product product : products.values()) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("productId", product.productId);
            productInfo.put("name", product.name);
            productInfo.put("price", product.price);
            productInfo.put("quantity", product.quantity);
            productInfo.put("maxCapacity", product.maxCapacity);
            productInfo.put("needsRestock", product.quantity < product.maxCapacity * 0.2);

            productList.add(productInfo);
        }

        report.put("products", productList);
        report.put("coinInventory", coinInventory.coins);
        report.put("totalCashValue", coinInventory.getTotalValue());

        return report;
    }

    public List<Transaction> getTransactionHistory(int limit) {
        int fromIndex = Math.max(0, transactionHistory.size() - limit);
        return new ArrayList<>(transactionHistory.subList(fromIndex, transactionHistory.size()));
    }

    public static void main(String[] args) throws InterruptedException {
        DesignVendingMachine machine = new DesignVendingMachine();

        // Setup products
        machine.addProduct("A1", "Coca Cola", 125, 10); // $1.25
        machine.addProduct("A2", "Pepsi", 125, 10);
        machine.addProduct("B1", "Chips", 150, 8); // $1.50
        machine.addProduct("B2", "Cookies", 200, 6); // $2.00

        // Stock products
        machine.restockProduct("A1", 5);
        machine.restockProduct("A2", 5);
        machine.restockProduct("B1", 4);
        machine.restockProduct("B2", 3);

        // Load coins
        machine.loadCoins(25, 20); // quarters
        machine.loadCoins(10, 15); // dimes
        machine.loadCoins(5, 10); // nickels
        machine.loadCoins(1, 25); // pennies

        System.out.println("Machine Status: " + machine.getMachineStatus());
        System.out.println("Available Products:");
        for (Product product : machine.getAvailableProducts()) {
            System.out.println("- " + product.name + " ($" + product.price / 100.0 + ") x" + product.quantity);
        }

        // Simulate purchase
        System.out.println("\nSimulating purchase of Coca Cola...");
        TransactionResult result = machine.selectProduct("A1");
        System.out.println("Product selected: " + result);

        System.out.println("Remaining payment: $" + machine.getRemainingPayment() / 100.0);

        // Insert coins (quarters)
        machine.insertCoin(25);
        machine.insertCoin(25);
        machine.insertCoin(25);
        machine.insertCoin(25);
        machine.insertCoin(25);

        result = machine.insertCoin(25); // $1.50 total, should complete purchase
        System.out.println("Purchase result: " + result);

        // Wait for dispensing
        Thread.sleep(3000);

        System.out.println("\nFinal Machine Status: " + machine.getMachineStatus());
        System.out.println("Transaction History: " + machine.getTransactionHistory(5).size() + " transactions");

        // Show inventory report
        System.out.println("\nInventory Report:");
        Map<String, Object> inventory = machine.getInventoryReport();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> productList = (List<Map<String, Object>>) inventory.get("products");
        for (Map<String, Object> product : productList) {
            System.out.println(
                    "- " + product.get("name") + ": " + product.get("quantity") + "/" + product.get("maxCapacity"));
        }
        System.out.println("Total cash: $" + (Integer) inventory.get("totalCashValue") / 100.0);
    }
}
