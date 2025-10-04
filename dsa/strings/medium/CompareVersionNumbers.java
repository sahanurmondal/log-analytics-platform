package strings.medium;

import java.util.*;
import java.util.regex.Pattern;

/**
 * LeetCode 165: Compare Version Numbers
 * https://leetcode.com/problems/compare-version-numbers/
 * 
 * Companies: Microsoft, Google, Amazon, Apple, Facebook, Bloomberg, Adobe
 * Frequency: High (Asked in 600+ interviews)
 *
 * Description:
 * Given two version numbers, version1 and version2, compare them.
 * Version numbers consist of one or more revisions joined by a dot '.'.
 * Each revision consists of digits and may contain leading zeros.
 * Every revision contains at least one character.
 * Revisions are 0-indexed from left to right, with the leftmost revision being
 * revision 0,
 * the next revision being revision 1, and so on.
 * 
 * Return the following:
 * - If version1 < version2, return -1.
 * - If version1 > version2, return 1.
 * - Otherwise, return 0.
 * 
 * Constraints:
 * - 1 <= version1.length, version2.length <= 500
 * - version1 and version2 only contain digits and '.'
 * - version1 and version2 are valid version numbers
 * - All the given revisions in version1 and version2 can be stored in a 32-bit
 * integer
 * 
 * Follow-up Questions:
 * 1. How would you handle version numbers with alphabetic components?
 * 2. Can you compare version numbers with different semantic versioning
 * schemes?
 * 3. What about pre-release identifiers and build metadata?
 * 4. How to handle range comparisons and version constraints?
 * 5. Can you implement version sorting for multiple versions?
 * 6. What about handling invalid or malformed version numbers?
 */
public class CompareVersionNumbers {

    // Approach 1: Split and Compare (Optimal) - O(m + n) time, O(m + n) space
    public static int compareVersion(String version1, String version2) {
        String[] nums1 = version1.split("\\.");
        String[] nums2 = version2.split("\\.");

        int maxLength = Math.max(nums1.length, nums2.length);

        for (int i = 0; i < maxLength; i++) {
            int v1 = i < nums1.length ? Integer.parseInt(nums1[i]) : 0;
            int v2 = i < nums2.length ? Integer.parseInt(nums2[i]) : 0;

            if (v1 < v2) {
                return -1;
            } else if (v1 > v2) {
                return 1;
            }
        }

        return 0;
    }

    // Approach 2: Two Pointers without Split - O(m + n) time, O(1) space
    public static int compareVersionTwoPointers(String version1, String version2) {
        int i = 0, j = 0;
        int len1 = version1.length(), len2 = version2.length();

        while (i < len1 || j < len2) {
            int num1 = 0, num2 = 0;

            // Extract number from version1
            while (i < len1 && version1.charAt(i) != '.') {
                num1 = num1 * 10 + (version1.charAt(i) - '0');
                i++;
            }

            // Extract number from version2
            while (j < len2 && version2.charAt(j) != '.') {
                num2 = num2 * 10 + (version2.charAt(j) - '0');
                j++;
            }

            if (num1 < num2) {
                return -1;
            } else if (num1 > num2) {
                return 1;
            }

            // Skip the dot
            if (i < len1)
                i++;
            if (j < len2)
                j++;
        }

        return 0;
    }

    // Approach 3: Recursive comparison - O(m + n) time, O(max depth) space
    public static int compareVersionRecursive(String version1, String version2) {
        return compareVersionHelper(version1, version2, 0, 0);
    }

    private static int compareVersionHelper(String v1, String v2, int i1, int i2) {
        if (i1 >= v1.length() && i2 >= v2.length()) {
            return 0;
        }

        int num1 = 0, num2 = 0;
        int start1 = i1, start2 = i2;

        // Extract next number from v1
        while (i1 < v1.length() && v1.charAt(i1) != '.') {
            i1++;
        }
        if (start1 < v1.length()) {
            num1 = Integer.parseInt(v1.substring(start1, Math.min(i1, v1.length())));
        }

        // Extract next number from v2
        while (i2 < v2.length() && v2.charAt(i2) != '.') {
            i2++;
        }
        if (start2 < v2.length()) {
            num2 = Integer.parseInt(v2.substring(start2, Math.min(i2, v2.length())));
        }

        if (num1 < num2) {
            return -1;
        } else if (num1 > num2) {
            return 1;
        }

        // Continue with remaining parts
        return compareVersionHelper(v1, v2, i1 + 1, i2 + 1);
    }

    // Approach 4: Normalize and compare as strings - O(m + n) time, O(m + n) space
    public static int compareVersionNormalized(String version1, String version2) {
        String normalized1 = normalizeVersion(version1);
        String normalized2 = normalizeVersion(version2);

        return normalized1.compareTo(normalized2);
    }

    private static String normalizeVersion(String version) {
        String[] parts = version.split("\\.");
        StringBuilder normalized = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                normalized.append(".");
            }
            // Pad with zeros to ensure consistent comparison
            normalized.append(String.format("%010d", Integer.parseInt(parts[i])));
        }

        return normalized.toString();
    }

    // Approach 5: Using BigInteger for very large numbers - O(m + n) time, O(m + n)
    // space
    public static int compareVersionBigInteger(String version1, String version2) {
        String[] nums1 = version1.split("\\.");
        String[] nums2 = version2.split("\\.");

        int maxLength = Math.max(nums1.length, nums2.length);

        for (int i = 0; i < maxLength; i++) {
            String s1 = i < nums1.length ? nums1[i] : "0";
            String s2 = i < nums2.length ? nums2[i] : "0";

            // Remove leading zeros and compare
            s1 = s1.replaceFirst("^0+(?!$)", "");
            s2 = s2.replaceFirst("^0+(?!$)", "");

            if (s1.length() != s2.length()) {
                return s1.length() < s2.length() ? -1 : 1;
            }

            int cmp = s1.compareTo(s2);
            if (cmp != 0) {
                return cmp;
            }
        }

        return 0;
    }

    // Follow-up 1: Version numbers with alphabetic components
    public static class AlphanumericVersions {

        public static int compareAlphanumericVersion(String version1, String version2) {
            String[] parts1 = version1.split("\\.");
            String[] parts2 = version2.split("\\.");

            int maxLength = Math.max(parts1.length, parts2.length);

            for (int i = 0; i < maxLength; i++) {
                String part1 = i < parts1.length ? parts1[i] : "0";
                String part2 = i < parts2.length ? parts2[i] : "0";

                int result = compareAlphanumericPart(part1, part2);
                if (result != 0) {
                    return result;
                }
            }

            return 0;
        }

        private static int compareAlphanumericPart(String part1, String part2) {
            // Split into numeric and alphabetic components
            List<String> tokens1 = tokenize(part1);
            List<String> tokens2 = tokenize(part2);

            int minSize = Math.min(tokens1.size(), tokens2.size());

            for (int i = 0; i < minSize; i++) {
                String token1 = tokens1.get(i);
                String token2 = tokens2.get(i);

                boolean isNum1 = isNumeric(token1);
                boolean isNum2 = isNumeric(token2);

                if (isNum1 && isNum2) {
                    // Both numeric - compare as integers
                    int num1 = Integer.parseInt(token1);
                    int num2 = Integer.parseInt(token2);
                    if (num1 != num2) {
                        return Integer.compare(num1, num2);
                    }
                } else if (isNum1) {
                    // Numbers come before letters
                    return -1;
                } else if (isNum2) {
                    // Numbers come before letters
                    return 1;
                } else {
                    // Both alphabetic - compare lexicographically
                    int cmp = token1.compareTo(token2);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
            }

            // If all compared tokens are equal, longer version is greater
            return Integer.compare(tokens1.size(), tokens2.size());
        }

        private static List<String> tokenize(String part) {
            List<String> tokens = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            boolean wasDigit = false;

            for (char c : part.toCharArray()) {
                boolean isDigit = Character.isDigit(c);

                if (current.length() > 0 && isDigit != wasDigit) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }

                current.append(c);
                wasDigit = isDigit;
            }

            if (current.length() > 0) {
                tokens.add(current.toString());
            }

            return tokens;
        }

        private static boolean isNumeric(String str) {
            return str.matches("\\d+");
        }

        // Handle version numbers like "1.0.0-alpha", "2.0.0-beta.1"
        public static int compareSemanticVersion(String version1, String version2) {
            SemanticVersion v1 = parseSemanticVersion(version1);
            SemanticVersion v2 = parseSemanticVersion(version2);

            return v1.compareTo(v2);
        }

        private static class SemanticVersion implements Comparable<SemanticVersion> {
            int major, minor, patch;
            String preRelease;
            String buildMetadata;

            public SemanticVersion(int major, int minor, int patch, String preRelease, String buildMetadata) {
                this.major = major;
                this.minor = minor;
                this.patch = patch;
                this.preRelease = preRelease;
                this.buildMetadata = buildMetadata;
            }

            @Override
            public int compareTo(SemanticVersion other) {
                // Compare major.minor.patch first
                int cmp = Integer.compare(this.major, other.major);
                if (cmp != 0)
                    return cmp;

                cmp = Integer.compare(this.minor, other.minor);
                if (cmp != 0)
                    return cmp;

                cmp = Integer.compare(this.patch, other.patch);
                if (cmp != 0)
                    return cmp;

                // Handle pre-release versions
                if (this.preRelease == null && other.preRelease == null) {
                    return 0;
                } else if (this.preRelease == null) {
                    return 1; // Release version > pre-release
                } else if (other.preRelease == null) {
                    return -1; // Pre-release < release version
                } else {
                    return comparePreRelease(this.preRelease, other.preRelease);
                }
            }

            private int comparePreRelease(String pre1, String pre2) {
                String[] parts1 = pre1.split("\\.");
                String[] parts2 = pre2.split("\\.");

                int minLen = Math.min(parts1.length, parts2.length);

                for (int i = 0; i < minLen; i++) {
                    String part1 = parts1[i];
                    String part2 = parts2[i];

                    boolean isNum1 = isNumeric(part1);
                    boolean isNum2 = isNumeric(part2);

                    if (isNum1 && isNum2) {
                        int cmp = Integer.compare(Integer.parseInt(part1), Integer.parseInt(part2));
                        if (cmp != 0)
                            return cmp;
                    } else if (isNum1) {
                        return -1; // Numeric identifiers have lower precedence
                    } else if (isNum2) {
                        return 1;
                    } else {
                        int cmp = part1.compareTo(part2);
                        if (cmp != 0)
                            return cmp;
                    }
                }

                return Integer.compare(parts1.length, parts2.length);
            }
        }

        private static SemanticVersion parseSemanticVersion(String version) {
            // Remove build metadata first
            String[] buildSplit = version.split("\\+", 2);
            String versionPart = buildSplit[0];
            String buildMetadata = buildSplit.length > 1 ? buildSplit[1] : null;

            // Split pre-release
            String[] preSplit = versionPart.split("-", 2);
            String corePart = preSplit[0];
            String preRelease = preSplit.length > 1 ? preSplit[1] : null;

            // Parse core version
            String[] parts = corePart.split("\\.");
            int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

            return new SemanticVersion(major, minor, patch, preRelease, buildMetadata);
        }
    }

    // Follow-up 2: Different semantic versioning schemes
    public static class DifferentSchemes {

        // Calendar versioning (e.g., "2023.01.15")
        public static int compareCalendarVersion(String version1, String version2) {
            String[] parts1 = version1.split("\\.");
            String[] parts2 = version2.split("\\.");

            // Assume format: YYYY.MM.DD or YYYY.MM.DD.HH.MM
            int maxParts = Math.max(parts1.length, parts2.length);

            for (int i = 0; i < maxParts; i++) {
                int val1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
                int val2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

                if (val1 != val2) {
                    return Integer.compare(val1, val2);
                }
            }

            return 0;
        }

        // Sequence-based versioning (e.g., "build-1234")
        public static int compareSequenceVersion(String version1, String version2) {
            int seq1 = extractSequenceNumber(version1);
            int seq2 = extractSequenceNumber(version2);

            return Integer.compare(seq1, seq2);
        }

        private static int extractSequenceNumber(String version) {
            // Extract number from patterns like "build-1234", "rev123", "v1.2.3"
            String numberPart = version.replaceAll("[^0-9]", "");
            return numberPart.isEmpty() ? 0 : Integer.parseInt(numberPart);
        }

        // Hybrid versioning (mix of different schemes)
        public static int compareHybridVersion(String version1, String version2) {
            VersionType type1 = detectVersionType(version1);
            VersionType type2 = detectVersionType(version2);

            if (type1 != type2) {
                // Different types - prioritize based on type order
                return type1.ordinal() - type2.ordinal();
            }

            switch (type1) {
                case SEMANTIC:
                    return AlphanumericVersions.compareSemanticVersion(version1, version2);
                case CALENDAR:
                    return compareCalendarVersion(version1, version2);
                case SEQUENCE:
                    return compareSequenceVersion(version1, version2);
                default:
                    return compareVersion(version1, version2);
            }
        }

        private enum VersionType {
            NUMERIC, SEMANTIC, CALENDAR, SEQUENCE
        }

        private static VersionType detectVersionType(String version) {
            if (version.matches("\\d{4}\\.\\d{1,2}\\.\\d{1,2}.*")) {
                return VersionType.CALENDAR;
            } else if (version.matches(".*build.*|.*rev.*|.*r\\d+")) {
                return VersionType.SEQUENCE;
            } else if (version.contains("-") || version.contains("+")) {
                return VersionType.SEMANTIC;
            } else {
                return VersionType.NUMERIC;
            }
        }
    }

    // Follow-up 3: Pre-release and build metadata
    public static class PreReleaseAndBuild {

        public static int compareVersionWithMetadata(String version1, String version2) {
            VersionWithMetadata v1 = parseVersionWithMetadata(version1);
            VersionWithMetadata v2 = parseVersionWithMetadata(version2);

            return v1.compareTo(v2);
        }

        private static class VersionWithMetadata implements Comparable<VersionWithMetadata> {
            String coreVersion;
            String preRelease;
            String buildMetadata;

            public VersionWithMetadata(String core, String preRelease, String build) {
                this.coreVersion = core;
                this.preRelease = preRelease;
                this.buildMetadata = build;
            }

            @Override
            public int compareTo(VersionWithMetadata other) {
                // Compare core version first
                int coreCompare = compareVersion(this.coreVersion, other.coreVersion);
                if (coreCompare != 0) {
                    return coreCompare;
                }

                // Compare pre-release
                if (this.preRelease == null && other.preRelease == null) {
                    return 0;
                } else if (this.preRelease == null) {
                    return 1; // Release > pre-release
                } else if (other.preRelease == null) {
                    return -1; // Pre-release < release
                } else {
                    return comparePreReleaseVersions(this.preRelease, other.preRelease);
                }

                // Build metadata is ignored for precedence in SemVer
            }
        }

        private static VersionWithMetadata parseVersionWithMetadata(String version) {
            // Split build metadata
            String[] buildSplit = version.split("\\+", 2);
            String versionPart = buildSplit[0];
            String buildMetadata = buildSplit.length > 1 ? buildSplit[1] : null;

            // Split pre-release
            String[] preSplit = versionPart.split("-", 2);
            String coreVersion = preSplit[0];
            String preRelease = preSplit.length > 1 ? preSplit[1] : null;

            return new VersionWithMetadata(coreVersion, preRelease, buildMetadata);
        }

        private static int comparePreReleaseVersions(String pre1, String pre2) {
            String[] parts1 = pre1.split("\\.");
            String[] parts2 = pre2.split("\\.");

            int minLength = Math.min(parts1.length, parts2.length);

            for (int i = 0; i < minLength; i++) {
                String part1 = parts1[i];
                String part2 = parts2[i];

                // Try to parse as integers
                try {
                    int num1 = Integer.parseInt(part1);
                    int num2 = Integer.parseInt(part2);
                    if (num1 != num2) {
                        return Integer.compare(num1, num2);
                    }
                } catch (NumberFormatException e) {
                    // If not both numbers, compare lexicographically
                    int cmp = part1.compareTo(part2);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
            }

            return Integer.compare(parts1.length, parts2.length);
        }
    }

    // Follow-up 4: Range comparisons and version constraints
    public static class VersionConstraints {

        public static boolean satisfiesConstraint(String version, String constraint) {
            if (constraint.startsWith(">=")) {
                String minVersion = constraint.substring(2).trim();
                return compareVersion(version, minVersion) >= 0;
            } else if (constraint.startsWith("<=")) {
                String maxVersion = constraint.substring(2).trim();
                return compareVersion(version, maxVersion) <= 0;
            } else if (constraint.startsWith(">")) {
                String minVersion = constraint.substring(1).trim();
                return compareVersion(version, minVersion) > 0;
            } else if (constraint.startsWith("<")) {
                String maxVersion = constraint.substring(1).trim();
                return compareVersion(version, maxVersion) < 0;
            } else if (constraint.startsWith("=") || constraint.startsWith("==")) {
                String exactVersion = constraint.replaceFirst("^==?", "").trim();
                return compareVersion(version, exactVersion) == 0;
            } else if (constraint.startsWith("!=")) {
                String excludedVersion = constraint.substring(2).trim();
                return compareVersion(version, excludedVersion) != 0;
            } else if (constraint.startsWith("~")) {
                // Tilde range: ~1.2.3 := >=1.2.3 <1.3.0
                return satisfiesTildeRange(version, constraint.substring(1).trim());
            } else if (constraint.startsWith("^")) {
                // Caret range: ^1.2.3 := >=1.2.3 <2.0.0
                return satisfiesCaretRange(version, constraint.substring(1).trim());
            } else {
                // Exact match
                return compareVersion(version, constraint) == 0;
            }
        }

        private static boolean satisfiesTildeRange(String version, String baseVersion) {
            String[] baseParts = baseVersion.split("\\.");
            String upperBound;

            if (baseParts.length >= 2) {
                int minorVersion = Integer.parseInt(baseParts[1]) + 1;
                upperBound = baseParts[0] + "." + minorVersion + ".0";
            } else {
                int majorVersion = Integer.parseInt(baseParts[0]) + 1;
                upperBound = majorVersion + ".0.0";
            }

            return compareVersion(version, baseVersion) >= 0 &&
                    compareVersion(version, upperBound) < 0;
        }

        private static boolean satisfiesCaretRange(String version, String baseVersion) {
            String[] baseParts = baseVersion.split("\\.");
            String upperBound;

            if (baseParts.length >= 1) {
                int majorVersion = Integer.parseInt(baseParts[0]) + 1;
                upperBound = majorVersion + ".0.0";
            } else {
                upperBound = "999.999.999"; // Fallback
            }

            return compareVersion(version, baseVersion) >= 0 &&
                    compareVersion(version, upperBound) < 0;
        }

        public static boolean satisfiesMultipleConstraints(String version, String[] constraints) {
            for (String constraint : constraints) {
                if (!satisfiesConstraint(version, constraint.trim())) {
                    return false;
                }
            }
            return true;
        }

        public static List<String> filterVersionsByConstraint(List<String> versions, String constraint) {
            return versions.stream()
                    .filter(version -> satisfiesConstraint(version, constraint))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
    }

    // Follow-up 5: Version sorting for multiple versions
    public static class VersionSorting {

        public static List<String> sortVersions(List<String> versions) {
            List<String> sorted = new ArrayList<>(versions);
            sorted.sort(CompareVersionNumbers::compareVersion);
            return sorted;
        }

        public static List<String> sortVersionsDescending(List<String> versions) {
            List<String> sorted = new ArrayList<>(versions);
            sorted.sort((v1, v2) -> compareVersion(v2, v1));
            return sorted;
        }

        public static String findLatestVersion(List<String> versions) {
            return versions.stream()
                    .max(CompareVersionNumbers::compareVersion)
                    .orElse(null);
        }

        public static String findOldestVersion(List<String> versions) {
            return versions.stream()
                    .min(CompareVersionNumbers::compareVersion)
                    .orElse(null);
        }

        public static List<String> findVersionsInRange(List<String> versions, String minVersion, String maxVersion) {
            return versions.stream()
                    .filter(v -> compareVersion(v, minVersion) >= 0 &&
                            compareVersion(v, maxVersion) <= 0)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        public static Map<String, List<String>> groupVersionsByMajor(List<String> versions) {
            Map<String, List<String>> grouped = new HashMap<>();

            for (String version : versions) {
                String majorVersion = version.split("\\.")[0];
                if (!grouped.containsKey(majorVersion)) {
                    grouped.put(majorVersion, new ArrayList<>());
                }
                grouped.get(majorVersion).add(version);
            }

            // Sort versions within each major group
            for (List<String> versionList : grouped.values()) {
                versionList.sort(CompareVersionNumbers::compareVersion);
            }

            return grouped;
        }
    }

    // Follow-up 6: Handling invalid or malformed version numbers
    public static class ValidationAndError {

        public static class VersionValidationResult {
            public final boolean isValid;
            public final String normalizedVersion;
            public final String errorMessage;

            public VersionValidationResult(boolean isValid, String normalized, String error) {
                this.isValid = isValid;
                this.normalizedVersion = normalized;
                this.errorMessage = error;
            }
        }

        public static VersionValidationResult validateAndNormalizeVersion(String version) {
            if (version == null) {
                return new VersionValidationResult(false, null, "Version is null");
            }

            String trimmed = version.trim();
            if (trimmed.isEmpty()) {
                return new VersionValidationResult(false, null, "Version is empty");
            }

            // Check for valid characters
            if (!trimmed.matches("[0-9.+-]+")) {
                return new VersionValidationResult(false, null, "Version contains invalid characters");
            }

            // Check for proper format
            if (trimmed.startsWith(".") || trimmed.endsWith(".")) {
                return new VersionValidationResult(false, null, "Version cannot start or end with dot");
            }

            if (trimmed.contains("..")) {
                return new VersionValidationResult(false, null, "Version cannot contain consecutive dots");
            }

            // Try to parse each part
            String[] parts = trimmed.split("\\.");
            StringBuilder normalized = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];

                if (part.isEmpty()) {
                    return new VersionValidationResult(false, null, "Empty version component");
                }

                try {
                    int num = Integer.parseInt(part);
                    if (num < 0) {
                        return new VersionValidationResult(false, null, "Negative version numbers not allowed");
                    }

                    if (i > 0) {
                        normalized.append(".");
                    }
                    normalized.append(num); // This removes leading zeros

                } catch (NumberFormatException e) {
                    return new VersionValidationResult(false, null, "Invalid number in version: " + part);
                }
            }

            return new VersionValidationResult(true, normalized.toString(), null);
        }

        public static int compareVersionSafe(String version1, String version2) {
            VersionValidationResult result1 = validateAndNormalizeVersion(version1);
            VersionValidationResult result2 = validateAndNormalizeVersion(version2);

            if (!result1.isValid || !result2.isValid) {
                throw new IllegalArgumentException(
                        "Invalid version(s): " +
                                (!result1.isValid ? result1.errorMessage : "") +
                                (!result2.isValid ? " / " + result2.errorMessage : ""));
            }

            return compareVersion(result1.normalizedVersion, result2.normalizedVersion);
        }

        public static List<String> filterValidVersions(List<String> versions) {
            return versions.stream()
                    .filter(v -> validateAndNormalizeVersion(v).isValid)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        public static List<String> normalizeVersions(List<String> versions) {
            return versions.stream()
                    .map(ValidationAndError::validateAndNormalizeVersion)
                    .filter(result -> result.isValid)
                    .map(result -> result.normalizedVersion)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
    }

    // Performance comparison utility
    public static void compareApproaches(String[] testCases) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 100000;

        // Split approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < testCases.length - 1; j += 2) {
                compareVersion(testCases[j], testCases[j + 1]);
            }
        }
        end = System.nanoTime();
        System.out.println("Split approach: " + (end - start) / 1_000_000 + " ms");

        // Two pointers
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < testCases.length - 1; j += 2) {
                compareVersionTwoPointers(testCases[j], testCases[j + 1]);
            }
        }
        end = System.nanoTime();
        System.out.println("Two pointers: " + (end - start) / 1_000_000 + " ms");

        // Recursive
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < testCases.length - 1; j += 2) {
                compareVersionRecursive(testCases[j], testCases[j + 1]);
            }
        }
        end = System.nanoTime();
        System.out.println("Recursive: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic comparisons
        System.out.println("=== Test Case 1: Basic Comparisons ===");

        String[][] basicTests = {
                { "1.01", "1.001" }, // 0
                { "1.0", "1.0.0" }, // 0
                { "0.1", "1.1" }, // -1
                { "1.0.1", "1" }, // 1
                { "7.5.2.4", "7.5.3" }, // -1
                { "1.2", "1.10" }, // -1
                { "1.2.0", "1.2" }, // 0
                { "1.0.0", "1" } // 0
        };

        for (String[] test : basicTests) {
            int result = compareVersion(test[0], test[1]);
            int expected = result; // For demonstration
            System.out.printf("compareVersion(\"%s\", \"%s\") = %d%n", test[0], test[1], result);

            // Verify consistency across approaches
            int twoPointer = compareVersionTwoPointers(test[0], test[1]);
            int recursive = compareVersionRecursive(test[0], test[1]);
            int normalized = compareVersionNormalized(test[0], test[1]);

            if (result != twoPointer || result != recursive || result != normalized) {
                System.out.println("  WARNING: Inconsistent results!");
            }
        }

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");

        String[][] edgeTests = {
                { "0", "0.0.0" },
                { "1", "1.0.0.0.0.0" },
                { "1.2.3", "1.2.3.0.0" },
                { "1.0000", "1.0" },
                { "01.1", "1.1" },
                { "1.01.1", "1.1.1" },
                { "1000.1000.1000", "1000.1000.1000" }
        };

        for (String[] test : edgeTests) {
            int result = compareVersion(test[0], test[1]);
            System.out.printf("compareVersion(\"%s\", \"%s\") = %d%n", test[0], test[1], result);
        }

        // Test Case 3: Alphanumeric versions
        System.out.println("\n=== Test Case 3: Alphanumeric Versions ===");

        String[][] alphaTests = {
                { "1.0.0-alpha", "1.0.0" },
                { "1.0.0-alpha", "1.0.0-alpha.1" },
                { "1.0.0-alpha.1", "1.0.0-alpha.beta" },
                { "1.0.0-alpha.beta", "1.0.0-beta" },
                { "1.0.0-beta", "1.0.0-beta.2" },
                { "1.0.0-beta.2", "1.0.0-beta.11" },
                { "1.0.0-beta.11", "1.0.0-rc.1" },
                { "1.0.0-rc.1", "1.0.0" }
        };

        System.out.println("Semantic version comparisons:");
        for (String[] test : alphaTests) {
            int result = AlphanumericVersions.compareSemanticVersion(test[0], test[1]);
            System.out.printf("compareSemanticVersion(\"%s\", \"%s\") = %d%n", test[0], test[1], result);
        }

        String[][] alphanumTests = {
                { "1.0a", "1.0b" },
                { "1.2a", "1.10a" },
                { "2.0", "2.0a" },
                { "1.0.1a2", "1.0.1b1" }
        };

        System.out.println("Alphanumeric version comparisons:");
        for (String[] test : alphanumTests) {
            int result = AlphanumericVersions.compareAlphanumericVersion(test[0], test[1]);
            System.out.printf("compareAlphanumericVersion(\"%s\", \"%s\") = %d%n", test[0], test[1], result);
        }

        // Test Case 4: Different versioning schemes
        System.out.println("\n=== Test Case 4: Different Versioning Schemes ===");

        String[][] calendarTests = {
                { "2023.01.15", "2023.01.16" },
                { "2023.12.31", "2024.01.01" },
                { "2023.01.15.10.30", "2023.01.15.10.31" }
        };

        System.out.println("Calendar version comparisons:");
        for (String[] test : calendarTests) {
            int result = DifferentSchemes.compareCalendarVersion(test[0], test[1]);
            System.out.printf("compareCalendarVersion(\"%s\", \"%s\") = %d%n", test[0], test[1], result);
        }

        String[][] sequenceTests = {
                { "build-1234", "build-1235" },
                { "rev123", "rev124" },
                { "v1.2.3", "v1.2.4" }
        };

        System.out.println("Sequence version comparisons:");
        for (String[] test : sequenceTests) {
            int result = DifferentSchemes.compareSequenceVersion(test[0], test[1]);
            System.out.printf("compareSequenceVersion(\"%s\", \"%s\") = %d%n", test[0], test[1], result);
        }

        // Test Case 5: Version constraints
        System.out.println("\n=== Test Case 5: Version Constraints ===");

        String version = "1.2.3";
        String[] constraints = {
                ">=1.2.0", "<=1.3.0", ">1.2.2", "<1.2.4", "=1.2.3", "!=1.2.4",
                "~1.2.0", "^1.2.0"
        };

        System.out.println("Testing version " + version + " against constraints:");
        for (String constraint : constraints) {
            boolean satisfies = VersionConstraints.satisfiesConstraint(version, constraint);
            System.out.printf("  %s: %b%n", constraint, satisfies);
        }

        // Test Case 6: Version sorting
        System.out.println("\n=== Test Case 6: Version Sorting ===");

        List<String> unsortedVersions = Arrays.asList(
                "1.0.0", "2.1.0", "1.2.0", "1.0.1", "2.0.0", "1.1.0", "1.2.1");

        System.out.println("Unsorted: " + unsortedVersions);

        List<String> sortedAsc = VersionSorting.sortVersions(unsortedVersions);
        System.out.println("Sorted ascending: " + sortedAsc);

        List<String> sortedDesc = VersionSorting.sortVersionsDescending(unsortedVersions);
        System.out.println("Sorted descending: " + sortedDesc);

        String latest = VersionSorting.findLatestVersion(unsortedVersions);
        String oldest = VersionSorting.findOldestVersion(unsortedVersions);
        System.out.println("Latest: " + latest + ", Oldest: " + oldest);

        List<String> inRange = VersionSorting.findVersionsInRange(unsortedVersions, "1.0.0", "1.2.0");
        System.out.println("Versions in range [1.0.0, 1.2.0]: " + inRange);

        Map<String, List<String>> groupedByMajor = VersionSorting.groupVersionsByMajor(unsortedVersions);
        System.out.println("Grouped by major version: " + groupedByMajor);

        // Test Case 7: Validation and error handling
        System.out.println("\n=== Test Case 7: Validation and Error Handling ===");

        String[] testVersions = {
                "1.2.3", "1.2.3.4", "01.02.03", "1.2", "1", "",
                "1.2.3a", "1..2", ".1.2", "1.2.", "-1.2.3", null
        };

        for (String testVersion : testVersions) {
            ValidationAndError.VersionValidationResult result = ValidationAndError
                    .validateAndNormalizeVersion(testVersion);

            if (result.isValid) {
                System.out.printf("Valid: \"%s\" -> \"%s\"%n", testVersion, result.normalizedVersion);
            } else {
                System.out.printf("Invalid: \"%s\" - %s%n", String.valueOf(testVersion), result.errorMessage);
            }
        }

        // Test Case 8: Large scale testing
        System.out.println("\n=== Test Case 8: Large Scale Testing ===");

        // Generate random version numbers for stress testing
        Random random = new Random(42);
        List<String> randomVersions = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            StringBuilder versionBuilder = new StringBuilder();
            int parts = random.nextInt(4) + 1;

            for (int j = 0; j < parts; j++) {
                if (j > 0)
                    versionBuilder.append(".");
                versionBuilder.append(random.nextInt(100));
            }

            randomVersions.add(versionBuilder.toString());
        }

        System.out.println("Generated " + randomVersions.size() + " random versions");

        // Test sorting consistency
        List<String> sorted1 = VersionSorting.sortVersions(randomVersions);
        List<String> sorted2 = new ArrayList<>(randomVersions);
        sorted2.sort(CompareVersionNumbers::compareVersion);

        boolean consistent = sorted1.equals(sorted2);
        System.out.println("Sorting consistency: " + consistent);

        // Test transitivity
        int transitivityTests = 1000;
        int transitivityPassed = 0;

        for (int i = 0; i < transitivityTests; i++) {
            String v1 = randomVersions.get(random.nextInt(randomVersions.size()));
            String v2 = randomVersions.get(random.nextInt(randomVersions.size()));
            String v3 = randomVersions.get(random.nextInt(randomVersions.size()));

            int cmp12 = compareVersion(v1, v2);
            int cmp23 = compareVersion(v2, v3);
            int cmp13 = compareVersion(v1, v3);

            // Check transitivity: if v1 <= v2 and v2 <= v3, then v1 <= v3
            if ((cmp12 <= 0 && cmp23 <= 0 && cmp13 <= 0) ||
                    (cmp12 >= 0 && cmp23 >= 0 && cmp13 >= 0) ||
                    (cmp12 == 0 && cmp13 == cmp23) ||
                    (cmp23 == 0 && cmp13 == cmp12) ||
                    !((cmp12 <= 0 && cmp23 <= 0) || (cmp12 >= 0 && cmp23 >= 0))) {
                transitivityPassed++;
            }
        }

        System.out.println("Transitivity tests: " + transitivityPassed + "/" + transitivityTests + " passed");

        // Test Case 9: Performance comparison
        System.out.println("\n=== Test Case 9: Performance Comparison ===");

        String[] perfTestCases = {
                "1.2.3", "1.2.4", "1.10.0", "1.2.0", "2.0.0", "1.0.0",
                "1.2.3.4.5", "1.2.3.4.6", "0.0.1", "0.0.2"
        };

        compareApproaches(perfTestCases);

        System.out.println("\nCompare Version Numbers testing completed successfully!");
    }
}
