package design.hard;

import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Design Blockchain System
 *
 * Description: Design a simplified blockchain that supports:
 * - Block creation and mining
 * - Transaction validation and recording
 * - Proof of work consensus
 * - Chain validation and integrity
 * 
 * Constraints:
 * - Immutable transaction history
 * - Cryptographic security
 * - Distributed consensus simulation
 *
 * Follow-up:
 * - How to handle forks and consensus?
 * - Smart contract execution?
 * 
 * Time Complexity: O(2^difficulty) for mining, O(1) for validation
 * Space Complexity: O(blocks * transactions)
 * 
 * Company Tags: Blockchain companies, Financial institutions
 */
public class DesignBlockchain {

    class Transaction {
        String transactionId;
        String from;
        String to;
        double amount;
        long timestamp;
        String signature;

        Transaction(String from, String to, double amount) {
            this.transactionId = UUID.randomUUID().toString();
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
            this.signature = generateSignature();
        }

        private String generateSignature() {
            return DesignBlockchain.calculateHash(from + to + amount + timestamp);
        }

        boolean isValid() {
            return signature.equals(generateSignature()) && amount > 0;
        }

        @Override
        public String toString() {
            return String.format("TX[%s: %s -> %s: %.2f]",
                    transactionId.substring(0, 8), from, to, amount);
        }
    }

    class Block {
        int index;
        String previousHash;
        long timestamp;
        List<Transaction> transactions;
        int nonce;
        String hash;
        String merkleRoot;

        Block(int index, String previousHash, List<Transaction> transactions) {
            this.index = index;
            this.previousHash = previousHash;
            this.timestamp = System.currentTimeMillis();
            this.transactions = new ArrayList<>(transactions);
            this.nonce = 0;
            this.merkleRoot = calculateMerkleRoot();
            this.hash = calculateHash();
        }

        private String calculateMerkleRoot() {
            if (transactions.isEmpty()) {
                return DesignBlockchain.calculateHash("");
            }

            List<String> hashes = new ArrayList<>();
            for (Transaction tx : transactions) {
                hashes.add(tx.signature);
            }

            while (hashes.size() > 1) {
                List<String> newHashes = new ArrayList<>();
                for (int i = 0; i < hashes.size(); i += 2) {
                    String left = hashes.get(i);
                    String right = i + 1 < hashes.size() ? hashes.get(i + 1) : left;
                    newHashes.add(DesignBlockchain.calculateHash(left + right));
                }
                hashes = newHashes;
            }

            return hashes.get(0);
        }

        String calculateHash() {
            return DesignBlockchain.calculateHash(index + previousHash + timestamp + merkleRoot + nonce);
        }

        void mineBlock(int difficulty) {
            String target = "0".repeat(difficulty);

            System.out.println("Mining block " + index + "...");
            long startTime = System.currentTimeMillis();

            while (!hash.substring(0, difficulty).equals(target)) {
                nonce++;
                hash = calculateHash();
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Block " + index + " mined in " + (endTime - startTime) + "ms");
            System.out.println("Hash: " + hash);
            System.out.println("Nonce: " + nonce);
        }

        boolean isValid(String expectedPreviousHash, int difficulty) {
            if (!hash.equals(calculateHash())) {
                return false;
            }

            if (!previousHash.equals(expectedPreviousHash)) {
                return false;
            }

            if (!hash.substring(0, difficulty).equals("0".repeat(difficulty))) {
                return false;
            }

            if (!merkleRoot.equals(calculateMerkleRoot())) {
                return false;
            }

            return transactions.stream().allMatch(Transaction::isValid);
        }

        @Override
        public String toString() {
            return String.format("Block[%d] Hash: %s, Txs: %d",
                    index, hash.substring(0, 10), transactions.size());
        }
    }

    class Wallet {
        String address;
        double balance;
        List<Transaction> transactionHistory;

        Wallet(String address, double initialBalance) {
            this.address = address;
            this.balance = initialBalance;
            this.transactionHistory = new ArrayList<>();
        }

        boolean canSend(double amount) {
            return balance >= amount;
        }

        void addTransaction(Transaction transaction) {
            transactionHistory.add(transaction);

            if (transaction.from.equals(address)) {
                balance -= transaction.amount;
            }
            if (transaction.to.equals(address)) {
                balance += transaction.amount;
            }
        }
    }

    private List<Block> chain;
    private List<Transaction> pendingTransactions;
    private Map<String, Wallet> wallets;
    private int difficulty;
    private double miningReward;

    public DesignBlockchain() {
        chain = new ArrayList<>();
        pendingTransactions = new ArrayList<>();
        wallets = new HashMap<>();
        difficulty = 4;
        miningReward = 10.0;

        // Create genesis block
        createGenesisBlock();
    }

    private void createGenesisBlock() {
        Block genesis = new Block(0, "0", new ArrayList<>());
        genesis.hash = genesis.calculateHash();
        chain.add(genesis);
    }

    private static String calculateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hash", e);
        }
    }

    public void createWallet(String address, double initialBalance) {
        wallets.put(address, new Wallet(address, initialBalance));
    }

    public boolean createTransaction(String from, String to, double amount) {
        Wallet fromWallet = wallets.get(from);
        Wallet toWallet = wallets.get(to);

        if (fromWallet == null || toWallet == null) {
            System.out.println("Wallet not found");
            return false;
        }

        if (!fromWallet.canSend(amount)) {
            System.out.println("Insufficient balance");
            return false;
        }

        Transaction transaction = new Transaction(from, to, amount);
        pendingTransactions.add(transaction);

        System.out.println("Transaction created: " + transaction);
        return true;
    }

    public void minePendingTransactions(String minerAddress) {
        // Add mining reward transaction
        Transaction rewardTransaction = new Transaction("SYSTEM", minerAddress, miningReward);
        pendingTransactions.add(rewardTransaction);

        // Create new block
        String previousHash = getLatestBlock().hash;
        Block newBlock = new Block(chain.size(), previousHash, pendingTransactions);

        // Mine the block
        newBlock.mineBlock(difficulty);

        // Add block to chain
        chain.add(newBlock);

        // Update wallet balances
        for (Transaction tx : newBlock.transactions) {
            if (wallets.containsKey(tx.from) && !tx.from.equals("SYSTEM")) {
                wallets.get(tx.from).addTransaction(tx);
            }
            if (wallets.containsKey(tx.to)) {
                wallets.get(tx.to).addTransaction(tx);
            }
        }

        // Clear pending transactions
        pendingTransactions.clear();

        System.out.println("Block mined and added to chain");
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public double getBalance(String address) {
        Wallet wallet = wallets.get(address);
        return wallet != null ? wallet.balance : 0.0;
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.isValid(previousBlock.hash, difficulty)) {
                System.out.println("Invalid block at index " + i);
                return false;
            }
        }

        return true;
    }

    public List<Transaction> getTransactionHistory(String address) {
        Wallet wallet = wallets.get(address);
        return wallet != null ? new ArrayList<>(wallet.transactionHistory) : new ArrayList<>();
    }

    public void printChain() {
        System.out.println("\n=== Blockchain ===");
        for (Block block : chain) {
            System.out.println(block);
            for (Transaction tx : block.transactions) {
                System.out.println("  " + tx);
            }
            System.out.println();
        }
    }

    public void printWallets() {
        System.out.println("\n=== Wallet Balances ===");
        for (Wallet wallet : wallets.values()) {
            System.out.println(wallet.address + ": " + wallet.balance);
        }
    }

    public Map<String, Object> getBlockchainStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalBlocks", chain.size());
        stats.put("totalWallets", wallets.size());
        stats.put("pendingTransactions", pendingTransactions.size());
        stats.put("difficulty", difficulty);
        stats.put("miningReward", miningReward);
        stats.put("isValid", isChainValid());

        int totalTransactions = chain.stream()
                .mapToInt(block -> block.transactions.size())
                .sum();
        stats.put("totalTransactions", totalTransactions);

        return stats;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, difficulty);
    }

    public static void main(String[] args) {
        DesignBlockchain blockchain = new DesignBlockchain();

        // Create wallets
        blockchain.createWallet("Alice", 100.0);
        blockchain.createWallet("Bob", 50.0);
        blockchain.createWallet("Charlie", 25.0);
        blockchain.createWallet("Miner", 0.0);

        System.out.println("Initial balances:");
        blockchain.printWallets();

        // Create transactions
        blockchain.createTransaction("Alice", "Bob", 30.0);
        blockchain.createTransaction("Bob", "Charlie", 20.0);
        blockchain.createTransaction("Alice", "Charlie", 10.0);

        // Mine block
        blockchain.minePendingTransactions("Miner");

        System.out.println("\nAfter first block:");
        blockchain.printWallets();

        // More transactions
        blockchain.createTransaction("Charlie", "Alice", 15.0);
        blockchain.createTransaction("Bob", "Miner", 5.0);

        // Mine another block
        blockchain.minePendingTransactions("Miner");

        System.out.println("\nAfter second block:");
        blockchain.printWallets();

        // Print blockchain
        blockchain.printChain();

        // Validate chain
        System.out.println("Blockchain valid: " + blockchain.isChainValid());

        // Show stats
        System.out.println("\nBlockchain stats:");
        System.out.println(blockchain.getBlockchainStats());

        // Show transaction history
        System.out.println("\nAlice's transaction history:");
        List<Transaction> aliceHistory = blockchain.getTransactionHistory("Alice");
        for (Transaction tx : aliceHistory) {
            System.out.println("  " + tx);
        }
    }
}
