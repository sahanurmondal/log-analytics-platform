package design.hard;

import java.util.*;

/**
 * LeetCode 1756: Design Movie Rental System
 * https://leetcode.com/problems/design-most-recently-used-queue/
 *
 * Description: You have a movie renting company consisting of n shops.
 * 
 * Constraints:
 * - 1 <= n <= 3 * 10^5
 * - 1 <= entries.length <= 10^5
 * - 0 <= shop_i <= n - 1
 * - 1 <= movie_i, price_i <= 10^4
 * - movie_i are unique in entries
 * - 1 <= movie <= 10^4
 * - 0 <= shop <= n - 1
 * - At most 10^5 calls in total will be made to search, rent, drop, and report
 *
 * Follow-up:
 * - Can you implement all operations efficiently?
 * 
 * Time Complexity: O(log n) for most operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class MovieRentingSystem {

    class Entry implements Comparable<Entry> {
        int shop, movie, price;

        Entry(int shop, int movie, int price) {
            this.shop = shop;
            this.movie = movie;
            this.price = price;
        }

        @Override
        public int compareTo(Entry other) {
            if (this.price != other.price) {
                return this.price - other.price;
            }
            if (this.shop != other.shop) {
                return this.shop - other.shop;
            }
            return this.movie - other.movie;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Entry))
                return false;
            Entry other = (Entry) obj;
            return shop == other.shop && movie == other.movie && price == other.price;
        }

        @Override
        public int hashCode() {
            return Objects.hash(shop, movie, price);
        }
    }

    private Map<Integer, TreeSet<Entry>> unrentedMovies; // movie -> available entries
    private TreeSet<Entry> rentedMovies; // all rented movies sorted by price/shop
    private Map<String, Integer> shopMoviePrice; // shop+movie -> price

    public MovieRentingSystem(int n, int[][] entries) {
        unrentedMovies = new HashMap<>();
        rentedMovies = new TreeSet<>();
        shopMoviePrice = new HashMap<>();

        for (int[] entry : entries) {
            int shop = entry[0];
            int movie = entry[1];
            int price = entry[2];

            Entry e = new Entry(shop, movie, price);
            unrentedMovies.computeIfAbsent(movie, k -> new TreeSet<>()).add(e);
            shopMoviePrice.put(shop + "," + movie, price);
        }
    }

    public List<Integer> search(int movie) {
        List<Integer> result = new ArrayList<>();
        TreeSet<Entry> entries = unrentedMovies.get(movie);

        if (entries != null) {
            int count = 0;
            for (Entry entry : entries) {
                if (count >= 5)
                    break;
                result.add(entry.shop);
                count++;
            }
        }

        return result;
    }

    public void rent(int shop, int movie) {
        int price = shopMoviePrice.get(shop + "," + movie);
        Entry entry = new Entry(shop, movie, price);

        unrentedMovies.get(movie).remove(entry);
        rentedMovies.add(entry);
    }

    public void drop(int shop, int movie) {
        int price = shopMoviePrice.get(shop + "," + movie);
        Entry entry = new Entry(shop, movie, price);

        rentedMovies.remove(entry);
        unrentedMovies.get(movie).add(entry);
    }

    public List<List<Integer>> report() {
        List<List<Integer>> result = new ArrayList<>();
        int count = 0;

        for (Entry entry : rentedMovies) {
            if (count >= 5)
                break;
            result.add(Arrays.asList(entry.shop, entry.movie));
            count++;
        }

        return result;
    }

    public static void main(String[] args) {
        MovieRentingSystem movieRentingSystem = new MovieRentingSystem(3, new int[][] {
                { 0, 1, 5 }, { 0, 2, 6 }, { 0, 3, 7 }, { 1, 1, 4 }, { 1, 2, 7 }, { 2, 1, 5 }
        });

        System.out.println(movieRentingSystem.search(1)); // Expected: [1, 0, 2]
        movieRentingSystem.rent(0, 1);
        movieRentingSystem.rent(1, 2);
        System.out.println(movieRentingSystem.report()); // Expected: [[0, 1], [1, 2]]
        movieRentingSystem.drop(1, 2);
        System.out.println(movieRentingSystem.search(2)); // Expected: [0, 1]
    }
}
