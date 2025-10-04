package design.medium;

import java.util.*;

/**
 * LeetCode 535: Encode and Decode TinyURL
 * https://leetcode.com/problems/encode-and-decode-tinyurl/
 *
 * Description: TinyURL is a URL shortening service where you enter a URL such
 * as https://leetcode.com/problems/design-tinyurl
 * and it returns a short URL such as http://tinyurl.com/4e9iAk.
 * 
 * Constraints:
 * - 1 <= url.length <= 10^4
 * - url is guaranteed to be a valid URL
 *
 * Follow-up:
 * - How would you design a system for millions of URLs?
 * - What about custom aliases?
 * 
 * Time Complexity: O(1) for encode/decode
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class DesignTinyURL {

    private Map<String, String> codeToUrl;
    private Map<String, String> urlToCode;
    private String baseUrl;
    private String chars;
    private Random random;
    private int codeLength;

    public DesignTinyURL() {
        codeToUrl = new HashMap<>();
        urlToCode = new HashMap<>();
        baseUrl = "http://tinyurl.com/";
        chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        random = new Random();
        codeLength = 6;
    }

    public String encode(String longUrl) {
        if (urlToCode.containsKey(longUrl)) {
            return baseUrl + urlToCode.get(longUrl);
        }

        String code;
        do {
            code = generateCode();
        } while (codeToUrl.containsKey(code));

        codeToUrl.put(code, longUrl);
        urlToCode.put(longUrl, code);

        return baseUrl + code;
    }

    public String decode(String shortUrl) {
        String code = shortUrl.replace(baseUrl, "");
        return codeToUrl.getOrDefault(code, "");
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Alternative approach - Counter-based
    static class TinyURLCounter {
        private Map<Integer, String> idToUrl;
        private Map<String, Integer> urlToId;
        private String baseUrl;
        private int counter;

        public TinyURLCounter() {
            idToUrl = new HashMap<>();
            urlToId = new HashMap<>();
            baseUrl = "http://tinyurl.com/";
            counter = 1;
        }

        public String encode(String longUrl) {
            if (urlToId.containsKey(longUrl)) {
                return baseUrl + urlToId.get(longUrl);
            }

            int id = counter++;
            idToUrl.put(id, longUrl);
            urlToId.put(longUrl, id);

            return baseUrl + id;
        }

        public String decode(String shortUrl) {
            String idStr = shortUrl.replace(baseUrl, "");
            int id = Integer.parseInt(idStr);
            return idToUrl.getOrDefault(id, "");
        }
    }

    // Custom alias support
    public String encodeWithAlias(String longUrl, String alias) {
        if (alias != null && !alias.isEmpty()) {
            if (codeToUrl.containsKey(alias)) {
                return ""; // Alias already exists
            }

            codeToUrl.put(alias, longUrl);
            urlToCode.put(longUrl, alias);
            return baseUrl + alias;
        }

        return encode(longUrl);
    }

    public static void main(String[] args) {
        DesignTinyURL codec = new DesignTinyURL();

        String url = "https://leetcode.com/problems/design-tinyurl";
        String encoded = codec.encode(url);
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded: " + codec.decode(encoded));

        // Test with same URL
        String encoded2 = codec.encode(url);
        System.out.println("Encoded again: " + encoded2);
        System.out.println("Same encoding: " + encoded.equals(encoded2));

        // Test custom alias
        String customEncoded = codec.encodeWithAlias("https://google.com", "google");
        System.out.println("Custom encoded: " + customEncoded);
        System.out.println("Custom decoded: " + codec.decode(customEncoded));
    }
}
