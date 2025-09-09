package design.medium;

/**
 * Design Hash Function for Distributed Systems
 *
 * Description: Design a hash function that distributes keys uniformly across
 * multiple buckets.
 * Support operations: hash, addBucket, removeBucket
 * 
 * Constraints:
 * - 1 <= buckets <= 1000
 * - Keys are strings of length 1-100
 * - At most 10^4 operations
 *
 * Follow-up:
 * - Can you handle dynamic bucket resizing?
 * - What about consistent hashing?
 * 
 * Time Complexity: O(1) for hash, O(n) for resize operations
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Amazon, Facebook
 */
public class DesignHashFunction {

    private int bucketCount;
    private long prime;
    private long base;

    public DesignHashFunction(int bucketCount) {
        this.bucketCount = bucketCount;
        this.prime = 1000000007L;
        this.base = 31L;
    }

    public int hash(String key) {
        long hashValue = 0;
        long powerOfBase = 1;

        for (char c : key.toCharArray()) {
            hashValue = (hashValue + (c - 'a' + 1) * powerOfBase) % prime;
            powerOfBase = (powerOfBase * base) % prime;
        }

        return (int) (hashValue % bucketCount);
    }

    public void addBucket() {
        bucketCount++;
    }

    public void removeBucket() {
        if (bucketCount > 1) {
            bucketCount--;
        }
    }

    public int getBucketCount() {
        return bucketCount;
    }

    // Alternative: Simple polynomial rolling hash
    public int simpleHash(String key) {
        int hash = 0;
        for (char c : key.toCharArray()) {
            hash = hash * 31 + c;
        }
        return Math.abs(hash % bucketCount);
    }

    // Distribution analysis
    public void analyzeDistribution(String[] keys) {
        int[] buckets = new int[bucketCount];

        for (String key : keys) {
            buckets[hash(key)]++;
        }

        System.out.println("Distribution analysis:");
        for (int i = 0; i < bucketCount; i++) {
            System.out.println("Bucket " + i + ": " + buckets[i] + " keys");
        }
    }

    public static void main(String[] args) {
        DesignHashFunction hashFunc = new DesignHashFunction(5);

        String[] testKeys = { "apple", "banana", "cherry", "date", "elderberry",
                "fig", "grape", "honeydew", "kiwi", "lemon" };

        System.out.println("Hash values:");
        for (String key : testKeys) {
            System.out.println(key + " -> bucket " + hashFunc.hash(key));
        }

        hashFunc.analyzeDistribution(testKeys);

        // Test bucket operations
        hashFunc.addBucket();
        System.out.println("\nAfter adding bucket (count: " + hashFunc.getBucketCount() + "):");
        for (String key : testKeys) {
            System.out.println(key + " -> bucket " + hashFunc.hash(key));
        }
    }
}
