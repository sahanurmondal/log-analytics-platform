package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Design TinyURL
 * 
 * Description:
 * Design a URL shortener like TinyURL. Implement encode() and decode() methods.
 * 
 * Companies: Amazon, Google, Facebook
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class DesignTinyURL {

    private Map<String, String> longToShort = new HashMap<>();
    private Map<String, String> shortToLong = new HashMap<>();
    private String baseUrl = "http://tinyurl.com/";
    private String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private Random random = new Random();

    public String encode(String longUrl) {
        if (longToShort.containsKey(longUrl)) {
            return longToShort.get(longUrl);
        }

        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (shortToLong.containsKey(shortCode));

        String shortUrl = baseUrl + shortCode;
        longToShort.put(longUrl, shortUrl);
        shortToLong.put(shortCode, longUrl);

        return shortUrl;
    }

    public String decode(String shortUrl) {
        String shortCode = shortUrl.replace(baseUrl, "");
        return shortToLong.getOrDefault(shortCode, "");
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DesignTinyURL tinyUrl = new DesignTinyURL();

        String longUrl = "https://leetcode.com/problems/design-tinyurl";
        String shortUrl = tinyUrl.encode(longUrl);
        System.out.println("Short URL: " + shortUrl);

        String decoded = tinyUrl.decode(shortUrl);
        System.out.println("Decoded URL: " + decoded);
        System.out.println("Match: " + longUrl.equals(decoded));
    }
}
