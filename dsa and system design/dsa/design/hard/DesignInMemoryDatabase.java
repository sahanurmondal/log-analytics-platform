package design.hard;

import java.util.*;

/**
 * Design In-Memory Database with Transaction Support
 *
 * Description: Design a database that supports:
 * - SET key value: Set key to value
 * - GET key: Get value of key
 * - DELETE key: Delete key
 * - COUNT value: Count keys with given value
 * - BEGIN: Start transaction
 * - COMMIT: Commit transaction
 * - ROLLBACK: Rollback transaction
 * 
 * Constraints:
 * - Support nested transactions
 * - Efficient operations
 * - ACID properties
 *
 * Follow-up:
 * - How to support concurrent transactions?
 * 
 * Time Complexity: O(1) for most operations, O(n) for COUNT
 * Space Complexity: O(n * transactions)
 * 
 * Company Tags: Google, Amazon, Facebook
 */
public class DesignInMemoryDatabase {

    private Stack<Map<String, String>> transactions;
    private Map<String, Integer> valueCounts;
    private Map<String, String> database;
    private boolean inTransaction;

    public DesignInMemoryDatabase() {
        transactions = new Stack<>();
        valueCounts = new HashMap<>();
        database = new HashMap<>();
        inTransaction = false;
    }

    public void set(String key, String value) {
        String oldValue = null;

        if (inTransaction) {
            // Check transaction stack for existing value
            for (int i = transactions.size() - 1; i >= 0; i--) {
                if (transactions.get(i).containsKey(key)) {
                    oldValue = transactions.get(i).get(key);
                    break;
                }
            }
            if (oldValue == null) {
                oldValue = database.get(key);
            }

            // Set in current transaction
            if (transactions.isEmpty()) {
                begin();
            }
            transactions.peek().put(key, value);
        } else {
            oldValue = database.get(key);
            database.put(key, value);
        }

        // Update value counts
        if (oldValue != null) {
            updateValueCount(oldValue, -1);
        }
        if (value != null) {
            updateValueCount(value, 1);
        }
    }

    public String get(String key) {
        if (inTransaction) {
            // Check transaction stack
            for (int i = transactions.size() - 1; i >= 0; i--) {
                if (transactions.get(i).containsKey(key)) {
                    return transactions.get(i).get(key);
                }
            }
        }
        return database.get(key);
    }

    public void delete(String key) {
        String oldValue = get(key);
        if (oldValue != null) {
            set(key, null);
        }
    }

    public int count(String value) {
        return valueCounts.getOrDefault(value, 0);
    }

    public void begin() {
        transactions.push(new HashMap<>());
        inTransaction = true;
    }

    public void commit() {
        if (transactions.isEmpty()) {
            return;
        }

        Map<String, String> currentTransaction = transactions.pop();

        if (transactions.isEmpty()) {
            // Commit to database
            for (Map.Entry<String, String> entry : currentTransaction.entrySet()) {
                if (entry.getValue() == null) {
                    database.remove(entry.getKey());
                } else {
                    database.put(entry.getKey(), entry.getValue());
                }
            }
            inTransaction = false;
        } else {
            // Merge with parent transaction
            Map<String, String> parentTransaction = transactions.peek();
            for (Map.Entry<String, String> entry : currentTransaction.entrySet()) {
                parentTransaction.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void rollback() {
        if (transactions.isEmpty()) {
            return;
        }

        Map<String, String> currentTransaction = transactions.pop();

        // Restore value counts
        for (Map.Entry<String, String> entry : currentTransaction.entrySet()) {
            String key = entry.getKey();
            String newValue = entry.getValue();
            String oldValue = null;

            // Find old value
            for (int i = transactions.size() - 1; i >= 0; i--) {
                if (transactions.get(i).containsKey(key)) {
                    oldValue = transactions.get(i).get(key);
                    break;
                }
            }
            if (oldValue == null) {
                oldValue = database.get(key);
            }

            // Restore counts
            if (newValue != null) {
                updateValueCount(newValue, -1);
            }
            if (oldValue != null) {
                updateValueCount(oldValue, 1);
            }
        }

        if (transactions.isEmpty()) {
            inTransaction = false;
        }
    }

    private void updateValueCount(String value, int delta) {
        if (value == null)
            return;

        int newCount = valueCounts.getOrDefault(value, 0) + delta;
        if (newCount <= 0) {
            valueCounts.remove(value);
        } else {
            valueCounts.put(value, newCount);
        }
    }

    public void printDatabase() {
        System.out.println("Database state:");
        Map<String, String> currentState = new HashMap<>(database);

        // Apply transaction changes
        for (Map<String, String> transaction : transactions) {
            for (Map.Entry<String, String> entry : transaction.entrySet()) {
                if (entry.getValue() == null) {
                    currentState.remove(entry.getKey());
                } else {
                    currentState.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, String> entry : currentState.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("Value counts: " + valueCounts);
    }

    public static void main(String[] args) {
        DesignInMemoryDatabase db = new DesignInMemoryDatabase();

        // Basic operations
        db.set("a", "foo");
        db.set("b", "foo");
        System.out.println("Count of 'foo': " + db.count("foo")); // Expected: 2
        System.out.println("Get 'a': " + db.get("a")); // Expected: foo

        // Transaction operations
        db.begin();
        db.set("a", "bar");
        System.out.println("In transaction - Get 'a': " + db.get("a")); // Expected: bar
        System.out.println("In transaction - Count of 'bar': " + db.count("bar")); // Expected: 1

        db.rollback();
        System.out.println("After rollback - Get 'a': " + db.get("a")); // Expected: foo
        System.out.println("After rollback - Count of 'bar': " + db.count("bar")); // Expected: 0

        // Nested transactions
        db.begin();
        db.set("c", "baz");
        db.begin();
        db.set("c", "qux");
        System.out.println("Nested transaction - Get 'c': " + db.get("c")); // Expected: qux

        db.commit();
        System.out.println("After inner commit - Get 'c': " + db.get("c")); // Expected: qux

        db.commit();
        System.out.println("After outer commit - Get 'c': " + db.get("c")); // Expected: qux

        db.printDatabase();
    }
}
