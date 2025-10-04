package design.hard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Design Search Engine
 *
 * Description: Design a search engine that supports:
 * - Document indexing and full-text search
 * - Ranking algorithms (TF-IDF, PageRank)
 * - Auto-complete and spell correction
 * - Real-time search suggestions
 * 
 * Constraints:
 * - Handle millions of documents
 * - Support complex queries with operators
 * - Fast search response times
 *
 * Follow-up:
 * - How to handle distributed indexing?
 * - Personalized search results?
 * 
 * Time Complexity: O(log n) for search, O(n) for indexing
 * Space Complexity: O(vocabulary * documents)
 * 
 * Company Tags: Google, Bing, Elasticsearch
 */
public class DesignSearchEngine {

    class Document {
        String docId;
        String title;
        String content;
        String url;
        Map<String, String> metadata;
        Map<String, Integer> termFrequency;
        int wordCount;
        double pageRank;
        long lastModified;

        Document(String docId, String title, String content, String url) {
            this.docId = docId;
            this.title = title;
            this.content = content;
            this.url = url;
            this.metadata = new HashMap<>();
            this.termFrequency = new HashMap<>();
            this.pageRank = 1.0;
            this.lastModified = System.currentTimeMillis();
            calculateTermFrequency();
        }

        private void calculateTermFrequency() {
            String[] words = (title + " " + content).toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .split("\\s+");

            wordCount = words.length;

            for (String word : words) {
                if (!word.isEmpty() && !isStopWord(word)) {
                    termFrequency.put(word, termFrequency.getOrDefault(word, 0) + 1);
                }
            }
        }

        double getTfIdf(String term, Map<String, Integer> documentFrequency, int totalDocs) {
            int tf = termFrequency.getOrDefault(term.toLowerCase(), 0);
            if (tf == 0)
                return 0.0;

            int df = documentFrequency.getOrDefault(term.toLowerCase(), 1);
            double idf = Math.log((double) totalDocs / df);

            return (1.0 + Math.log(tf)) * idf;
        }
    }

    class SearchResult {
        String docId;
        String title;
        String snippet;
        String url;
        double score;
        Map<String, Object> highlights;

        SearchResult(Document doc, String query, double score) {
            this.docId = doc.docId;
            this.title = doc.title;
            this.url = doc.url;
            this.score = score;
            this.highlights = new HashMap<>();
            this.snippet = generateSnippet(doc, query);
        }

        private String generateSnippet(Document doc, String query) {
            String[] queryTerms = query.toLowerCase().split("\\s+");
            String content = doc.content.toLowerCase();

            // Find the best snippet containing query terms
            int bestStart = 0;
            int maxMatches = 0;

            String[] words = content.split("\\s+");
            int snippetLength = 30;

            for (int i = 0; i <= words.length - snippetLength; i++) {
                int matches = 0;
                for (int j = i; j < i + snippetLength && j < words.length; j++) {
                    for (String term : queryTerms) {
                        if (words[j].contains(term)) {
                            matches++;
                            break;
                        }
                    }
                }

                if (matches > maxMatches) {
                    maxMatches = matches;
                    bestStart = i;
                }
            }

            StringBuilder snippet = new StringBuilder();
            int end = Math.min(bestStart + snippetLength, words.length);

            for (int i = bestStart; i < end; i++) {
                if (i > bestStart)
                    snippet.append(" ");

                String word = words[i];
                boolean highlighted = false;

                for (String term : queryTerms) {
                    if (word.contains(term)) {
                        snippet.append("<b>").append(word).append("</b>");
                        highlighted = true;
                        break;
                    }
                }

                if (!highlighted) {
                    snippet.append(word);
                }
            }

            return snippet.toString();
        }
    }

    class InvertedIndex {
        Map<String, Set<String>> termToDocuments;
        Map<String, Integer> documentFrequency;

        InvertedIndex() {
            termToDocuments = new HashMap<>();
            documentFrequency = new HashMap<>();
        }

        void addDocument(Document doc) {
            for (String term : doc.termFrequency.keySet()) {
                termToDocuments.computeIfAbsent(term, k -> new HashSet<>()).add(doc.docId);
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }

        void removeDocument(Document doc) {
            for (String term : doc.termFrequency.keySet()) {
                Set<String> docs = termToDocuments.get(term);
                if (docs != null) {
                    docs.remove(doc.docId);
                    if (docs.isEmpty()) {
                        termToDocuments.remove(term);
                        documentFrequency.remove(term);
                    } else {
                        documentFrequency.put(term, documentFrequency.get(term) - 1);
                    }
                }
            }
        }

        Set<String> getDocuments(String term) {
            return termToDocuments.getOrDefault(term.toLowerCase(), new HashSet<>());
        }
    }

    class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;
        Set<String> suggestions;

        TrieNode() {
            children = new HashMap<>();
            suggestions = new HashSet<>();
        }
    }

    private Map<String, Document> documents;
    private InvertedIndex invertedIndex;
    private TrieNode autoCompleteRoot;
    private Set<String> stopWords;

    public DesignSearchEngine() {
        documents = new HashMap<>();
        invertedIndex = new InvertedIndex();
        autoCompleteRoot = new TrieNode();
        initializeStopWords();
    }

    private void initializeStopWords() {
        stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at",
                "to", "for", "of", "with", "by", "is", "are", "was", "were", "be", "been", "have", "has", "had");
    }

    private boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }

    public void indexDocument(String docId, String title, String content, String url, Map<String, String> metadata) {
        // Remove existing document if it exists
        if (documents.containsKey(docId)) {
            removeDocument(docId);
        }

        Document doc = new Document(docId, title, content, url);
        if (metadata != null) {
            doc.metadata.putAll(metadata);
        }

        documents.put(docId, doc);
        invertedIndex.addDocument(doc);

        // Add terms to autocomplete trie
        for (String term : doc.termFrequency.keySet()) {
            addToAutoComplete(term);
        }
    }

    public void removeDocument(String docId) {
        Document doc = documents.remove(docId);
        if (doc != null) {
            invertedIndex.removeDocument(doc);
        }
    }

    private void addToAutoComplete(String term) {
        TrieNode current = autoCompleteRoot;

        for (char c : term.toCharArray()) {
            current.children.computeIfAbsent(c, k -> new TrieNode());
            current = current.children.get(c);
            current.suggestions.add(term);

            // Limit suggestions per node
            if (current.suggestions.size() > 10) {
                current.suggestions = current.suggestions.stream()
                        .limit(10)
                        .collect(Collectors.toSet());
            }
        }

        current.isEndOfWord = true;
    }

    public List<SearchResult> search(String query, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] queryTerms = query.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .split("\\s+");

        // Find candidate documents
        Set<String> candidateDocIds = new HashSet<>();

        for (String term : queryTerms) {
            if (!isStopWord(term)) {
                Set<String> docs = invertedIndex.getDocuments(term);
                if (candidateDocIds.isEmpty()) {
                    candidateDocIds.addAll(docs);
                } else {
                    candidateDocIds.retainAll(docs); // AND operation
                }
            }
        }

        // If no documents match all terms, use OR operation
        if (candidateDocIds.isEmpty()) {
            for (String term : queryTerms) {
                if (!isStopWord(term)) {
                    candidateDocIds.addAll(invertedIndex.getDocuments(term));
                }
            }
        }

        // Calculate scores and create results
        List<SearchResult> results = new ArrayList<>();

        for (String docId : candidateDocIds) {
            Document doc = documents.get(docId);
            if (doc != null) {
                double score = calculateRelevanceScore(doc, queryTerms);
                if (score > 0) {
                    results.add(new SearchResult(doc, query, score));
                }
            }
        }

        // Sort by score (descending) and return top results
        return results.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    private double calculateRelevanceScore(Document doc, String[] queryTerms) {
        double score = 0.0;
        int totalDocs = documents.size();

        // TF-IDF score
        for (String term : queryTerms) {
            if (!isStopWord(term)) {
                double tfIdf = doc.getTfIdf(term, invertedIndex.documentFrequency, totalDocs);
                score += tfIdf;
            }
        }

        // Title boost
        String titleLower = doc.title.toLowerCase();
        for (String term : queryTerms) {
            if (titleLower.contains(term)) {
                score *= 1.5; // 50% boost for title matches
            }
        }

        // PageRank boost
        score *= (1.0 + Math.log(doc.pageRank));

        // Freshness boost
        long ageInDays = (System.currentTimeMillis() - doc.lastModified) / (1000 * 60 * 60 * 24);
        double freshnessFactor = Math.exp(-ageInDays / 365.0); // Decay over a year
        score *= (0.8 + 0.2 * freshnessFactor);

        return score;
    }

    public List<String> getAutoCompleteSuggestions(String prefix, int maxSuggestions) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }

        TrieNode current = autoCompleteRoot;
        String prefixLower = prefix.toLowerCase();

        for (char c : prefixLower.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return new ArrayList<>();
            }
        }

        return current.suggestions.stream()
                .limit(maxSuggestions)
                .collect(Collectors.toList());
    }

    public List<String> getSpellingSuggestions(String word, int maxSuggestions) {
        if (word == null || word.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Integer> candidates = new HashMap<>();
        String wordLower = word.toLowerCase();

        // Find words with edit distance <= 2
        for (String term : invertedIndex.termToDocuments.keySet()) {
            int distance = calculateEditDistance(wordLower, term);
            if (distance <= 2 && distance > 0) {
                candidates.put(term, distance);
            }
        }

        return candidates.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .thenComparing(e -> invertedIndex.documentFrequency.getOrDefault(e.getKey(), 0),
                                Collections.reverseOrder()))
                .limit(maxSuggestions)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private int calculateEditDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }

        return dp[m][n];
    }

    public void updatePageRank(String docId, double pageRank) {
        Document doc = documents.get(docId);
        if (doc != null) {
            doc.pageRank = pageRank;
        }
    }

    public Map<String, Object> getSearchStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalDocuments", documents.size());
        stats.put("totalTerms", invertedIndex.termToDocuments.size());
        stats.put("averageDocumentLength",
                documents.values().stream().mapToInt(d -> d.wordCount).average().orElse(0.0));

        // Most frequent terms
        Map<String, Integer> topTerms = invertedIndex.documentFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        stats.put("topTerms", topTerms);

        return stats;
    }

    public static void main(String[] args) {
        DesignSearchEngine searchEngine = new DesignSearchEngine();

        // Index some documents
        searchEngine.indexDocument("doc1", "Java Programming Guide",
                "Java is a powerful programming language used for enterprise applications",
                "http://example.com/java", null);

        searchEngine.indexDocument("doc2", "Python Tutorial",
                "Python is a versatile programming language perfect for beginners",
                "http://example.com/python", null);

        searchEngine.indexDocument("doc3", "Web Development with JavaScript",
                "JavaScript is essential for web development and frontend programming",
                "http://example.com/javascript", null);

        searchEngine.indexDocument("doc4", "Machine Learning with Python",
                "Python is widely used in machine learning and data science applications",
                "http://example.com/ml-python", null);

        // Search
        System.out.println("Search results for 'programming language':");
        List<SearchResult> results = searchEngine.search("programming language", 5);
        for (SearchResult result : results) {
            System.out.println("- " + result.title + " (score: " + String.format("%.2f", result.score) + ")");
            System.out.println("  " + result.snippet);
            System.out.println();
        }

        // Auto-complete
        System.out.println("Auto-complete suggestions for 'prog':");
        List<String> suggestions = searchEngine.getAutoCompleteSuggestions("prog", 5);
        for (String suggestion : suggestions) {
            System.out.println("- " + suggestion);
        }

        // Spell check
        System.out.println("\nSpelling suggestions for 'programing':");
        List<String> spellSuggestions = searchEngine.getSpellingSuggestions("programing", 3);
        for (String suggestion : spellSuggestions) {
            System.out.println("- " + suggestion);
        }

        // Search stats
        System.out.println("\nSearch engine statistics:");
        System.out.println(searchEngine.getSearchStats());
    }
}
