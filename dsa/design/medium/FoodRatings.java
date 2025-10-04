package design.medium;

import java.util.*;

/**
 * LeetCode 2353: Design a Food Rating System
 * https://leetcode.com/problems/design-a-food-rating-system/
 *
 * Description: Design a food rating system that can do the following:
 * - Modify the rating of a food item listed in the system.
 * - Return the highest-rated food item for a type of cuisine in the system.
 * 
 * Constraints:
 * - 1 <= n <= 2 * 10^4
 * - n == foods.length == cuisines.length == ratings.length
 * - 1 <= foods[i].length, cuisines[i].length <= 10
 * - foods[i], cuisines[i] consist of lowercase English letters
 * - 1 <= ratings[i] <= 10^8
 * - All the strings in foods are distinct
 * - changeRating and highestRated will be called at most 2 * 10^4 times in
 * total
 *
 * Follow-up:
 * - Can you handle this efficiently?
 * 
 * Time Complexity: O(log n) for changeRating/highestRated
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class FoodRatings {

    class Food implements Comparable<Food> {
        String name;
        int rating;

        Food(String name, int rating) {
            this.name = name;
            this.rating = rating;
        }

        @Override
        public int compareTo(Food other) {
            if (this.rating != other.rating) {
                return other.rating - this.rating; // Higher rating first
            }
            return this.name.compareTo(other.name); // Lexicographically smaller first
        }
    }

    private Map<String, String> foodToCuisine;
    private Map<String, Integer> foodToRating;
    private Map<String, TreeSet<Food>> cuisineToFoods;

    public FoodRatings(String[] foods, String[] cuisines, int[] ratings) {
        foodToCuisine = new HashMap<>();
        foodToRating = new HashMap<>();
        cuisineToFoods = new HashMap<>();

        for (int i = 0; i < foods.length; i++) {
            String food = foods[i];
            String cuisine = cuisines[i];
            int rating = ratings[i];

            foodToCuisine.put(food, cuisine);
            foodToRating.put(food, rating);

            cuisineToFoods.computeIfAbsent(cuisine, k -> new TreeSet<>())
                    .add(new Food(food, rating));
        }
    }

    public void changeRating(String food, int newRating) {
        String cuisine = foodToCuisine.get(food);
        int oldRating = foodToRating.get(food);

        // Remove old food entry
        cuisineToFoods.get(cuisine).remove(new Food(food, oldRating));

        // Update rating
        foodToRating.put(food, newRating);

        // Add new food entry
        cuisineToFoods.get(cuisine).add(new Food(food, newRating));
    }

    public String highestRated(String cuisine) {
        return cuisineToFoods.get(cuisine).first().name;
    }

    public static void main(String[] args) {
        FoodRatings foodRatings = new FoodRatings(
                new String[] { "kimchi", "miso", "sushi", "moussaka", "ramen", "bulgogi" },
                new String[] { "korean", "japanese", "japanese", "greek", "japanese", "korean" },
                new int[] { 9, 12, 8, 15, 14, 7 });

        System.out.println(foodRatings.highestRated("korean")); // Expected: "kimchi"
        System.out.println(foodRatings.highestRated("japanese")); // Expected: "ramen"
        foodRatings.changeRating("sushi", 16);
        System.out.println(foodRatings.highestRated("japanese")); // Expected: "sushi"
        foodRatings.changeRating("ramen", 16);
        System.out.println(foodRatings.highestRated("japanese")); // Expected: "ramen"
    }
}
