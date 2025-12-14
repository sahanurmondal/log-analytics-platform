package strings.medium;

/**
 * LeetCode 271: Encode and Decode Strings
 *
 * Design an algorithm to encode a list of strings to a string. The encoded string is then
 * sent over the network and is decoded back to the original list of strings.
 *
 * Example:
 * Input: ["lint","code","love","you"]
 * Output: ["lint","code","love","you"]
 *
 * Encode: "lint:code:love:you"
 * Decode: Split and reconstruct
 *
 * Challenge: Handle strings with special characters
 */
public class EncodeDecodeStrings {

    /**
     * Encode: Convert list of strings to single string
     * Strategy: Use length + delimiter
     *
     * Format: "4#lint4#code4#love3#you"
     * Where length#string repeats
     *
     * Time: O(n), Space: O(n)
     */
    public String encode(String[] strs) {
        StringBuilder sb = new StringBuilder();

        for (String str : strs) {
            sb.append(str.length()).append("#").append(str);
        }

        return sb.toString();
    }

    /**
     * Decode: Convert encoded string back to list of strings
     * Time: O(n), Space: O(n)
     */
    public String[] decode(String s) {
        // First pass: count number of strings
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '#') {
                count++;
            }
        }

        String[] result = new String[count];
        int idx = 0;
        int i = 0;

        while (i < s.length()) {
            // Find the '#'
            int j = i;
            while (j < s.length() && s.charAt(j) != '#') {
                j++;
            }

            // Extract length
            int length = Integer.parseInt(s.substring(i, j));

            // Extract string
            String str = s.substring(j + 1, j + 1 + length);
            result[idx++] = str;

            i = j + 1 + length;
        }

        return result;
    }

    /**
     * Alternative: Using delimiter with escaping
     * Format: "lint","code","love","you"
     * Handle quotes by escaping
     */
    public String encodeV2(String[] strs) {
        StringBuilder sb = new StringBuilder();

        for (String str : strs) {
            // Escape quotes
            String escaped = str.replace("\"", "\\\"");
            sb.append("\"").append(escaped).append("\"");
            sb.append(",");
        }

        return sb.toString();
    }

    public String[] decodeV2(String s) {
        if (s.isEmpty()) return new String[0];

        java.util.List<String> list = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (escaped) {
                current.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                continue;
            }

            if (c == ',' && !inString) {
                if (current.length() > 0) {
                    list.add(current.toString());
                    current = new StringBuilder();
                }
                continue;
            }

            if (inString) {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            list.add(current.toString());
        }

        return list.toArray(new String[0]);
    }

    public static void main(String[] args) {
        EncodeDecodeStrings codec = new EncodeDecodeStrings();

        // Test case 1
        String[] strs1 = {"lint", "code", "love", "you"};
        String encoded1 = codec.encode(strs1);
        System.out.println("Encoded: " + encoded1);
        String[] decoded1 = codec.decode(encoded1);
        System.out.println("Decoded: " + java.util.Arrays.toString(decoded1));

        // Test case 2: Strings with special characters
        String[] strs2 = {"a#b", "c,d", "ef"};
        String encoded2 = codec.encode(strs2);
        String[] decoded2 = codec.decode(encoded2);
        System.out.println("Decoded special: " + java.util.Arrays.toString(decoded2));

        // Test case 3: Empty string
        String[] strs3 = {"", "a", ""};
        String encoded3 = codec.encode(strs3);
        String[] decoded3 = codec.decode(encoded3);
        System.out.println("Decoded with empty: " + java.util.Arrays.toString(decoded3));
    }
}

