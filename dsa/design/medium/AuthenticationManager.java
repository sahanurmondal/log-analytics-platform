package design.medium;

import java.util.*;

/**
 * LeetCode 1797: Design Authentication Manager
 * https://leetcode.com/problems/design-authentication-manager/
 *
 * Description: There is an authentication system that works with authentication
 * tokens.
 * 
 * Constraints:
 * - 1 <= timeToLive <= 10^8
 * - 1 <= currentTime <= 10^8
 * - 1 <= tokenId.length <= 5
 * - tokenId consists only of lowercase letters
 * - All calls to generate will contain unique values of tokenId
 * - At most 2000 calls will be made to all functions combined
 *
 * Follow-up:
 * - Can you make it efficient for frequent expiry checks?
 * 
 * Time Complexity: O(1) amortized for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class AuthenticationManager {

    private int timeToLive;
    private Map<String, Integer> tokens;
    private LinkedHashMap<Integer, Set<String>> timeToTokens;

    public AuthenticationManager(int timeToLive) {
        this.timeToLive = timeToLive;
        this.tokens = new HashMap<>();
        this.timeToTokens = new LinkedHashMap<>();
    }

    public void generate(String tokenId, int currentTime) {
        int expiryTime = currentTime + timeToLive;
        tokens.put(tokenId, expiryTime);
        timeToTokens.computeIfAbsent(expiryTime, k -> new HashSet<>()).add(tokenId);
    }

    public void renew(String tokenId, int currentTime) {
        if (!tokens.containsKey(tokenId) || tokens.get(tokenId) <= currentTime) {
            return;
        }

        // Remove from old expiry time
        int oldExpiryTime = tokens.get(tokenId);
        timeToTokens.get(oldExpiryTime).remove(tokenId);
        if (timeToTokens.get(oldExpiryTime).isEmpty()) {
            timeToTokens.remove(oldExpiryTime);
        }

        // Add to new expiry time
        int newExpiryTime = currentTime + timeToLive;
        tokens.put(tokenId, newExpiryTime);
        timeToTokens.computeIfAbsent(newExpiryTime, k -> new HashSet<>()).add(tokenId);
    }

    public int countUnexpiredTokens(int currentTime) {
        // Clean up expired tokens
        Iterator<Map.Entry<Integer, Set<String>>> iterator = timeToTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = iterator.next();
            if (entry.getKey() <= currentTime) {
                for (String tokenId : entry.getValue()) {
                    tokens.remove(tokenId);
                }
                iterator.remove();
            } else {
                break; // LinkedHashMap maintains insertion order
            }
        }

        return tokens.size();
    }

    public static void main(String[] args) {
        AuthenticationManager authManager = new AuthenticationManager(5);
        authManager.renew("aaa", 1); // No effect as token doesn't exist
        authManager.generate("aaa", 2);
        System.out.println(authManager.countUnexpiredTokens(6)); // Expected: 1
        authManager.generate("bbb", 7);
        authManager.renew("aaa", 8);
        authManager.renew("bbb", 10);
        System.out.println(authManager.countUnexpiredTokens(15)); // Expected: 0
    }
}
