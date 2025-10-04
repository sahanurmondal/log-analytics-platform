package miscellaneous.recent;

/**
 * Recent Problem: Validate IP Address (IPv4 and IPv6)
 * 
 * Description:
 * Given a string queryIP, return "IPv4" if IP is a valid IPv4 address,
 * "IPv6" if IP is a valid IPv6 address or "Neither" if IP is not a correct IP.
 * 
 * Companies: Microsoft, Amazon, Facebook
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class ValidateIPAddress {

    public String validIPAddress(String queryIP) {
        if (queryIP.contains(".")) {
            return isValidIPv4(queryIP) ? "IPv4" : "Neither";
        } else if (queryIP.contains(":")) {
            return isValidIPv6(queryIP) ? "IPv6" : "Neither";
        }
        return "Neither";
    }

    private boolean isValidIPv4(String ip) {
        String[] parts = ip.split("\\.", -1);
        if (parts.length != 4)
            return false;

        for (String part : parts) {
            if (!isValidIPv4Part(part))
                return false;
        }
        return true;
    }

    private boolean isValidIPv4Part(String part) {
        if (part.length() == 0 || part.length() > 3)
            return false;
        if (part.charAt(0) == '0' && part.length() > 1)
            return false;

        for (char c : part.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }

        int num = Integer.parseInt(part);
        return num >= 0 && num <= 255;
    }

    private boolean isValidIPv6(String ip) {
        String[] parts = ip.split(":", -1);
        if (parts.length != 8)
            return false;

        for (String part : parts) {
            if (!isValidIPv6Part(part))
                return false;
        }
        return true;
    }

    private boolean isValidIPv6Part(String part) {
        if (part.length() == 0 || part.length() > 4)
            return false;

        for (char c : part.toCharArray()) {
            if (!Character.isDigit(c) &&
                    !(c >= 'a' && c <= 'f') &&
                    !(c >= 'A' && c <= 'F')) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ValidateIPAddress solution = new ValidateIPAddress();
        System.out.println(solution.validIPAddress("172.16.254.1")); // "IPv4"
        System.out.println(solution.validIPAddress("2001:0db8:85a3:0:0:8A2E:0370:7334")); // "IPv6"
        System.out.println(solution.validIPAddress("256.256.256.256")); // "Neither"
    }
}
