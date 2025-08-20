package design.hard;

import java.util.*;

/**
 * LeetCode 1912: Design Movie Rental System (Enhanced)
 * Design a comprehensive data structure supporting multiple operations
 * 
 * Description: Design a data structure that efficiently supports:
 * - Add/Remove items with multiple attributes
 * - Query items by different criteria
 * - Maintain rankings and sorted views
 * 
 * Constraints:
 * - 1 <= items <= 10^5
 * - Multiple query types
 * - Efficient updates required
 *
 * Follow-up:
 * - Can you support complex queries efficiently?
 * 
 * Time Complexity: O(log n) for most operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook
 */
public class DesignDataStructure {

    class Item implements Comparable<Item> {
        int id;
        int category;
        int priority;
        String name;

        Item(int id, int category, int priority, String name) {
            this.id = id;
            this.category = category;
            this.priority = priority;
            this.name = name;
        }

        @Override
        public int compareTo(Item other) {
            if (this.priority != other.priority) {
                return other.priority - this.priority; // Higher priority first
            }
            if (this.category != other.category) {
                return this.category - other.category;
            }
            return this.name.compareTo(other.name);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Item))
                return false;
            Item other = (Item) obj;
            return id == other.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private Map<Integer, Item> items; // id -> item
    private Map<Integer, TreeSet<Item>> categoryItems; // category -> sorted items
    private TreeSet<Item> allItems; // All items sorted by priority
    private Map<String, TreeSet<Item>> nameIndex; // name -> items with that name

    public DesignDataStructure() {
        items = new HashMap<>();
        categoryItems = new HashMap<>();
        allItems = new TreeSet<>();
        nameIndex = new HashMap<>();
    }

    public void addItem(int id, int category, int priority, String name) {
        // Remove existing item if present
        if (items.containsKey(id)) {
            removeItem(id);
        }

        Item item = new Item(id, category, priority, name);

        // Add to all data structures
        items.put(id, item);
        categoryItems.computeIfAbsent(category, k -> new TreeSet<>()).add(item);
        allItems.add(item);
        nameIndex.computeIfAbsent(name, k -> new TreeSet<>()).add(item);
    }

    public boolean removeItem(int id) {
        Item item = items.remove(id);
        if (item == null) {
            return false;
        }

        // Remove from all data structures
        categoryItems.get(item.category).remove(item);
        if (categoryItems.get(item.category).isEmpty()) {
            categoryItems.remove(item.category);
        }

        allItems.remove(item);

        TreeSet<Item> nameItems = nameIndex.get(item.name);
        nameItems.remove(item);
        if (nameItems.isEmpty()) {
            nameIndex.remove(item.name);
        }

        return true;
    }

    public Item getItem(int id) {
        return items.get(id);
    }

    public List<Item> getTopItems(int k) {
        List<Item> result = new ArrayList<>();
        int count = 0;

        for (Item item : allItems) {
            if (count >= k)
                break;
            result.add(item);
            count++;
        }

        return result;
    }

    public List<Item> getTopItemsByCategory(int category, int k) {
        List<Item> result = new ArrayList<>();
        TreeSet<Item> categorySet = categoryItems.get(category);

        if (categorySet == null) {
            return result;
        }

        int count = 0;
        for (Item item : categorySet) {
            if (count >= k)
                break;
            result.add(item);
            count++;
        }

        return result;
    }

    public List<Item> searchByName(String name) {
        TreeSet<Item> nameItems = nameIndex.get(name);
        return nameItems == null ? new ArrayList<>() : new ArrayList<>(nameItems);
    }

    public void updatePriority(int id, int newPriority) {
        Item item = items.get(id);
        if (item == null) {
            return;
        }

        // Remove from sorted structures
        categoryItems.get(item.category).remove(item);
        allItems.remove(item);
        nameIndex.get(item.name).remove(item);

        // Update priority
        item.priority = newPriority;

        // Re-add to sorted structures
        categoryItems.get(item.category).add(item);
        allItems.add(item);
        nameIndex.get(item.name).add(item);
    }

    public int getItemCount() {
        return items.size();
    }

    public int getCategoryCount(int category) {
        TreeSet<Item> categorySet = categoryItems.get(category);
        return categorySet == null ? 0 : categorySet.size();
    }

    public static void main(String[] args) {
        DesignDataStructure ds = new DesignDataStructure();

        ds.addItem(1, 1, 100, "Item1");
        ds.addItem(2, 1, 90, "Item2");
        ds.addItem(3, 2, 95, "Item3");
        ds.addItem(4, 2, 85, "Item1"); // Same name, different category

        System.out.println("Top 2 items overall:");
        List<Item> topItems = ds.getTopItems(2);
        for (Item item : topItems) {
            System.out.println("ID: " + item.id + ", Priority: " + item.priority + ", Name: " + item.name);
        }

        System.out.println("\nTop items in category 1:");
        List<Item> categoryItems = ds.getTopItemsByCategory(1, 5);
        for (Item item : categoryItems) {
            System.out.println("ID: " + item.id + ", Priority: " + item.priority);
        }

        System.out.println("\nItems with name 'Item1':");
        List<Item> nameItems = ds.searchByName("Item1");
        for (Item item : nameItems) {
            System.out.println("ID: " + item.id + ", Category: " + item.category);
        }

        // Update priority
        ds.updatePriority(2, 105);
        System.out.println("\nAfter updating item 2 priority to 105:");
        topItems = ds.getTopItems(2);
        for (Item item : topItems) {
            System.out.println("ID: " + item.id + ", Priority: " + item.priority);
        }

        System.out.println("\nTotal items: " + ds.getItemCount());
        System.out.println("Items in category 1: " + ds.getCategoryCount(1));
    }
}
