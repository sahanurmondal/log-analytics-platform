# String Problems

String manipulation and processing problems including pattern matching, parsing, and string algorithms.

## 📚 String Algorithms Guide

### 🎯 When to Use String Algorithms
Use string algorithms when:
- Problem involves **text processing**, **pattern matching**, or **substring operations**
- Need to find **longest/shortest** substrings with constraints
- Dealing with **palindromes**, **anagrams**, or **subsequences**
- String **transformation** or **comparison** problems

### 🔑 String Algorithms & Time Complexities

#### 1️⃣ **Two Pointers** - O(n)
**When to use**:
- **Palindrome** checking
- **Reverse** operations
- **In-place** modifications
- Remove duplicates

**Implementation**: Start and end pointers moving towards each other
```java
int left = 0, right = s.length() - 1;
while (left < right) {
    // compare or swap
    left++;
    right--;
}
```
**Use cases**: Valid palindrome, reverse string, remove duplicates
**Space**: O(1)

#### 2️⃣ **Sliding Window** - O(n)
**When to use**:
- Find **longest/shortest substring** with constraint
- **Contiguous** substring problems
- **Character frequency** problems

**Implementation**: Expand window, then contract when invalid
```java
Map<Character, Integer> freq = new HashMap<>();
int left = 0;
for (int right = 0; right < s.length(); right++) {
    freq.put(s.charAt(right), freq.getOrDefault(s.charAt(right), 0) + 1);
    while (/* window invalid */) {
        freq.put(s.charAt(left), freq.get(s.charAt(left)) - 1);
        left++;
    }
    // update answer
}
```
**Use cases**: Longest substring without repeating chars, minimum window substring
**Space**: O(k) where k is alphabet size

#### 3️⃣ **KMP Algorithm** - O(n + m)
**When to use**:
- **Pattern matching** efficiently
- Find all occurrences of pattern
- Avoid redundant comparisons

**Implementation**: Build LPS (Longest Prefix Suffix) array
```java
int[] lps = buildLPS(pattern);
int i = 0, j = 0; // i for text, j for pattern
while (i < text.length()) {
    if (text.charAt(i) == pattern.charAt(j)) {
        i++; j++;
        if (j == pattern.length()) { /* found match */ }
    } else {
        if (j > 0) j = lps[j - 1];
        else i++;
    }
}
```
**Use cases**: String matching, repeated substring pattern
**Space**: O(m) for LPS array

#### 4️⃣ **Rabin-Karp (Rolling Hash)** - O(n + m) average
**When to use**:
- **Multiple pattern** matching
- Find duplicate substrings
- Compare substrings efficiently

**Implementation**: Hash with polynomial rolling hash
```java
long hash = 0, pow = 1;
for (int i = 0; i < m; i++) {
    hash = hash * BASE + s.charAt(i);
    if (i < m - 1) pow *= BASE;
}
// Roll: hash = (hash - s.charAt(i) * pow) * BASE + s.charAt(i + m);
```
**Use cases**: Longest duplicate substring, repeated DNA sequences
**Space**: O(1) for hash calculation

#### 5️⃣ **Trie (Prefix Tree)** - O(m) per operation
**When to use**:
- **Prefix** matching
- **Autocomplete** features
- Word dictionary operations
- Multiple string matching

**Implementation**: Tree with character nodes
```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd = false;
}
void insert(String word) {
    TrieNode curr = root;
    for (char c : word.toCharArray()) {
        if (curr.children[c - 'a'] == null) {
            curr.children[c - 'a'] = new TrieNode();
        }
        curr = curr.children[c - 'a'];
    }
    curr.isEnd = true;
}
```
**Use cases**: Word search II, replace words, autocomplete
**Space**: O(ALPHABET_SIZE × N × M) worst case

#### 6️⃣ **Dynamic Programming on Strings** - O(n × m)
**When to use**:
- **Longest Common Subsequence** (LCS)
- **Edit Distance** (Levenshtein)
- String **transformation** problems
- **Palindrome** problems

**Implementation**: 2D DP table
```java
int[][] dp = new int[n + 1][m + 1];
for (int i = 1; i <= n; i++) {
    for (int j = 1; j <= m; j++) {
        if (s1.charAt(i-1) == s2.charAt(j-1)) {
            dp[i][j] = dp[i-1][j-1] + 1; // LCS
        } else {
            dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
        }
    }
}
```
**Use cases**: Edit distance, LCS, distinct subsequences
**Space**: O(n × m), can optimize to O(min(n, m))

#### 7️⃣ **Manacher's Algorithm** - O(n)
**When to use**:
- Find **longest palindromic substring** optimally
- All palindrome substrings

**Implementation**: Expand around centers with optimization
```java
// Transform: "abc" -> "#a#b#c#"
String t = "#" + String.join("#", s.split("")) + "#";
int[] P = new int[t.length()]; // palindrome lengths
int C = 0, R = 0; // center and right boundary
for (int i = 0; i < t.length(); i++) {
    int mirror = 2 * C - i;
    if (i < R) P[i] = Math.min(R - i, P[mirror]);
    // Expand around i
    while (/* can expand */) { P[i]++; }
    if (i + P[i] > R) { C = i; R = i + P[i]; }
}
```
**Use cases**: Longest palindromic substring
**Space**: O(n)

#### 8️⃣ **Z-Algorithm** - O(n)
**When to use**:
- Pattern matching
- Find all occurrences
- Alternative to KMP

**Implementation**: Build Z-array (longest prefix matching suffix)
```java
int[] z = new int[n];
int l = 0, r = 0;
for (int i = 1; i < n; i++) {
    if (i > r) {
        l = r = i;
        while (r < n && s.charAt(r) == s.charAt(r - l)) r++;
        z[i] = r - l;
        r--;
    } else {
        // Use previously computed values
    }
}
```
**Use cases**: Pattern matching, string compression
**Space**: O(n)

#### 9️⃣ **Boyer-Moore Algorithm** - O(n/m) best case
**When to use**:
- Fast pattern matching in practice
- Skip multiple characters at once
- Large alphabet size

**Implementation**: Bad character and good suffix heuristics
**Use cases**: Text editors, grep-like tools
**Space**: O(ALPHABET_SIZE)

#### 🔟 **Suffix Array/Tree** - O(n log n) or O(n)
**When to use**:
- Multiple substring queries
- Longest repeated substring
- Pattern matching in multiple strings

**Implementation**: Sorted array of all suffixes
**Use cases**: Longest repeated substring, suffix queries
**Space**: O(n)

### 🎨 Common String Patterns

#### **Character Frequency Map**
```java
Map<Character, Integer> freq = new HashMap<>();
for (char c : s.toCharArray()) {
    freq.put(c, freq.getOrDefault(c, 0) + 1);
}
```

#### **Palindrome Check (Expand Around Center)**
```java
boolean isPalindrome(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        left--;
        right++;
    }
    return true;
}
```

#### **Anagram Check**
```java
// Sort and compare
Arrays.sort(s1.toCharArray());
Arrays.sort(s2.toCharArray());
return Arrays.equals(s1, s2);

// Or use frequency map
```

#### **StringBuilder for Mutations**
```java
StringBuilder sb = new StringBuilder();
// O(1) amortized append, O(n) string concatenation with +
```

### 🚀 String Problem-Solving Strategies

1. **Palindrome**: Expand around center or DP
2. **Substring with Constraint**: Sliding window
3. **Pattern Matching**: KMP, Rabin-Karp, or Z-algorithm
4. **Anagram**: Sort or frequency map
5. **Longest Common**: DP (LCS pattern)
6. **Transformation**: DP (Edit distance)
7. **Prefix Matching**: Trie
8. **Multiple Patterns**: Aho-Corasick or Trie
9. **Reverse/Rearrange**: Two pointers
10. **Counting Problems**: DP or combinatorics

### ⚡ Common Optimizations

- **Two Pointers**: Reduce space to O(1) for palindrome checks
- **Rolling Hash**: O(1) substring comparison after O(n) preprocessing
- **Sliding Window**: Single pass instead of nested loops
- **StringBuilder**: Use instead of string concatenation in loops
- **Character array**: Faster than String methods for many operations
- **Bit manipulation**: For tracking character presence (small alphabet)

### 📊 Algorithm Selection Guide

| Problem Type | Best Algorithm | Time | Space |
|-------------|----------------|------|-------|
| Pattern matching | KMP / Rabin-Karp | O(n+m) | O(m) |
| Longest palindrome | Manacher's | O(n) | O(n) |
| Substring with constraint | Sliding window | O(n) | O(k) |
| String comparison | DP (LCS/Edit) | O(n×m) | O(n×m) |
| Prefix matching | Trie | O(m) | O(N×M) |
| Anagram | Frequency map | O(n) | O(k) |
| Reverse/Palindrome | Two pointers | O(n) | O(1) |

## Problem List (Grouped by Difficulty)

### Easy
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Add Strings | [LeetCode Problem](https://leetcode.com/problems/add-strings/) | [AddStrings.java](./easy/AddStrings.java) |
| Reverse String | [LeetCode Problem](https://leetcode.com/problems/reverse-string/) | [ReverseString.java](./easy/ReverseString.java) |
| Valid Palindrome | [LeetCode Problem](https://leetcode.com/problems/valid-palindrome/) | [ValidPalindrome.java](./easy/ValidPalindrome.java) |

### Medium
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Check If Anagram | [LeetCode Problem](https://leetcode.com/problems/check-if-anagram/) | [CheckIfAnagram.java](./medium/CheckIfAnagram.java) |
| Compare Version Numbers | [LeetCode Problem](https://leetcode.com/problems/compare-version-numbers/) | [CompareVersionNumbers.java](./medium/CompareVersionNumbers.java) |
| Count And Say | [LeetCode Problem](https://leetcode.com/problems/count-and-say/) | [CountAndSay.java](./medium/CountAndSay.java) |
| Find All Duplicates In String | [LeetCode Problem](https://leetcode.com/problems/find-all-duplicates-in-string/) | [FindAllDuplicatesInString.java](./medium/FindAllDuplicatesInString.java) |
| Implement Str Str | [LeetCode Problem](https://leetcode.com/problems/implement-str-str/) | [ImplementStrStr.java](./medium/ImplementStrStr.java) |
| Longest Common Prefix | [LeetCode Problem](https://leetcode.com/problems/longest-common-prefix/) | [LongestCommonPrefix.java](./medium/LongestCommonPrefix.java) |
| Longest Palindromic Substring | [LeetCode Problem](https://leetcode.com/problems/longest-palindromic-substring/) | [LongestPalindromicSubstring.java](./medium/LongestPalindromicSubstring.java) |
| Longest Repeating Character Replacement | [LeetCode Problem](https://leetcode.com/problems/longest-repeating-character-replacement/) | [LongestRepeatingCharacterReplacement.java](./medium/LongestRepeatingCharacterReplacement.java) |
| Longest Substring Without Repeating Characters | [LeetCode Problem](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | [LongestSubstringWithoutRepeatingCharacters.java](./medium/LongestSubstringWithoutRepeatingCharacters.java) |
| Remove Duplicates | [LeetCode Problem](https://leetcode.com/problems/remove-duplicates/) | [RemoveDuplicates.java](./medium/RemoveDuplicates.java) |
| Reverse Words In AString | [LeetCode Problem](https://leetcode.com/problems/reverse-words-in-astring/) | [ReverseWordsInAString.java](./medium/ReverseWordsInAString.java) |
| Roman To Integer | [LeetCode Problem](https://leetcode.com/problems/roman-to-integer/) | [RomanToInteger.java](./medium/RomanToInteger.java) |
| String To Integer | [LeetCode Problem](https://leetcode.com/problems/string-to-integer/) | [StringToInteger.java](./medium/StringToInteger.java) |

### Hard
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Count Different Palindromic Subsequences | [LeetCode Problem](https://leetcode.com/problems/count-different-palindromic-subsequences/) | [CountDifferentPalindromicSubsequences.java](./hard/CountDifferentPalindromicSubsequences.java) |
| Distinct Subsequences | [LeetCode Problem](https://leetcode.com/problems/distinct-subsequences/) | [DistinctSubsequences.java](./hard/DistinctSubsequences.java) |
| Edit Distance | [LeetCode Problem](https://leetcode.com/problems/edit-distance/) | [EditDistance.java](./hard/EditDistance.java) |
| Find The Longest Substring Containing Vowels In Even Counts | [LeetCode Problem](https://leetcode.com/problems/find-the-longest-substring-containing-vowels-in-even-counts/) | [FindTheLongestSubstringContainingVowelsInEvenCounts.java](./hard/FindTheLongestSubstringContainingVowelsInEvenCounts.java) |
| Integer To English Words | [LeetCode Problem](https://leetcode.com/problems/integer-to-english-words/) | [IntegerToEnglishWords.java](./hard/IntegerToEnglishWords.java) |
| KMPString Matching | [LeetCode Problem](https://leetcode.com/problems/kmpstring-matching/) | [KMPStringMatching.java](./hard/KMPStringMatching.java) |
| Longest Palindromic Substring Hard | [LeetCode Problem](https://leetcode.com/problems/longest-palindromic-substring-hard/) | [LongestPalindromicSubstringHard.java](./hard/LongestPalindromicSubstringHard.java) |
| Scramble String | [LeetCode Problem](https://leetcode.com/problems/scramble-string/) | [ScrambleString.java](./hard/ScrambleString.java) |
| Shortest Palindrome | [LeetCode Problem](https://leetcode.com/problems/shortest-palindrome/) | [ShortestPalindrome.java](./hard/ShortestPalindrome.java) |
| Substring With Concatenation Of All Words | [LeetCode Problem](https://leetcode.com/problems/substring-with-concatenation-of-all-words/) | [SubstringWithConcatenationOfAllWords.java](./hard/SubstringWithConcatenationOfAllWords.java) |
| Text Justification | [LeetCode Problem](https://leetcode.com/problems/text-justification/) | [TextJustification.java](./hard/TextJustification.java) |
| Valid Number | [LeetCode Problem](https://leetcode.com/problems/valid-number/) | [ValidNumber.java](./hard/ValidNumber.java) |

## Core Algorithms & Techniques

### Key Patterns Used
- Pattern identification and implementation details
- Time and space complexity analysis
- Common approaches and optimizations

### Algorithm Categories
- **Time Complexity**: Various complexity patterns from O(1) to O(n²)
- **Space Complexity**: In-place vs auxiliary space solutions
- **Approach Types**: Iterative, recursive, and hybrid solutions

## Implementation Features

### Each Problem Includes:
- ✅ Multiple solution approaches when applicable
- ✅ Comprehensive test cases with edge cases
- ✅ Time and space complexity analysis
- ✅ Detailed comments and explanations
- ✅ Follow-up questions and variations

### Code Quality Standards:
- Clean, readable implementations
- Proper error handling
- Edge case coverage
- Performance optimizations
- Interview-ready format

## Study Recommendations

### Difficulty Progression:
1. **Easy**: Master fundamental concepts and basic implementations
2. **Medium**: Learn advanced techniques and optimization strategies  
3. **Hard**: Practice complex algorithms and edge case handling

### Key Focus Areas:
- Understanding core algorithms and data structures
- Pattern recognition and template usage
- Time/space complexity optimization
- Edge case identification and handling

## Notes
- Each implementation includes detailed explanations
- Focus on understanding patterns rather than memorizing solutions
- Practice multiple approaches for comprehensive understanding
- Test with various input scenarios for robust solutions
