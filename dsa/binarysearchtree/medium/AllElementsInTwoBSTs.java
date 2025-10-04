package binarysearchtree.medium;

import java.util.*;

/**
 * LeetCode 1305: All Elements in Two Binary Search Trees
 * https://leetcode.com/problems/all-elements-in-two-binary-search-trees/
 *
 * Description: Given two binary search trees root1 and root2, return a list
 * containing all the integers from both trees sorted in ascending order.
 * 
 * Constraints:
 * - The number of nodes in each tree is in the range [0, 5000]
 * - -10^5 <= Node.val <= 10^5
 *
 * Follow-up:
 * - Can you solve it without storing all values first?
 * 
 * Time Complexity: O(m + n)
 * Space Complexity: O(m + n)
 * 
 * Company Tags: Google, Facebook
 */
public class AllElementsInTwoBSTs {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    // Main optimized solution - Two stacks
    public List<Integer> getAllElements(TreeNode root1, TreeNode root2) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack1 = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();

        while (root1 != null || root2 != null || !stack1.isEmpty() || !stack2.isEmpty()) {
            // Push all left nodes to stacks
            while (root1 != null) {
                stack1.push(root1);
                root1 = root1.left;
            }
            while (root2 != null) {
                stack2.push(root2);
                root2 = root2.left;
            }

            // Compare tops and process smaller one
            if (stack2.isEmpty() || (!stack1.isEmpty() && stack1.peek().val <= stack2.peek().val)) {
                root1 = stack1.pop();
                result.add(root1.val);
                root1 = root1.right;
            } else {
                root2 = stack2.pop();
                result.add(root2.val);
                root2 = root2.right;
            }
        }

        return result;
    }

    // Alternative solution - Collect and merge
    public List<Integer> getAllElementsCollectMerge(TreeNode root1, TreeNode root2) {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        inorder(root1, list1);
        inorder(root2, list2);

        return merge(list1, list2);
    }

    private void inorder(TreeNode node, List<Integer> list) {
        if (node == null)
            return;

        inorder(node.left, list);
        list.add(node.val);
        inorder(node.right, list);
    }

    private List<Integer> merge(List<Integer> list1, List<Integer> list2) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < list1.size() && j < list2.size()) {
            if (list1.get(i) <= list2.get(j)) {
                result.add(list1.get(i++));
            } else {
                result.add(list2.get(j++));
            }
        }

        while (i < list1.size())
            result.add(list1.get(i++));
        while (j < list2.size())
            result.add(list2.get(j++));

        return result;
    }

    public static void main(String[] args) {
        AllElementsInTwoBSTs solution = new AllElementsInTwoBSTs();

        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(4);

        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(0);
        root2.right = new TreeNode(3);

        System.out.println(solution.getAllElements(root1, root2)); // Expected: [0,1,1,2,3,4]
    }
}
