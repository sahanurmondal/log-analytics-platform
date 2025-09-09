package miscellaneous.recent;

/**
 * Recent Problem: Binary Tree Cameras with Minimum Cost
 * 
 * Description:
 * You are given the root of a binary tree. Install cameras to monitor all
 * nodes.
 * Each camera can monitor its parent, itself, and direct children.
 * Find minimum number of cameras needed.
 * 
 * Companies: Google, Facebook, Amazon
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class BinaryTreeCameras {

    class TreeNode {
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

    private int cameras = 0;

    public int minCameraCover(TreeNode root) {
        cameras = 0;
        return dfs(root) == 0 ? cameras + 1 : cameras;
    }

    // Return values: 0 = not covered, 1 = covered, 2 = has camera
    private int dfs(TreeNode node) {
        if (node == null)
            return 1; // null nodes are considered covered

        int left = dfs(node.left);
        int right = dfs(node.right);

        // If either child is not covered, current node must have camera
        if (left == 0 || right == 0) {
            cameras++;
            return 2;
        }

        // If either child has camera, current node is covered
        if (left == 2 || right == 2) {
            return 1;
        }

        // Both children are covered but don't have cameras
        return 0;
    }

    // Alternative DP approach
    public int minCameraCoverDP(TreeNode root) {
        int[] result = dfsDP(root);
        return Math.min(result[1], result[2]);
    }

    // Returns [not_covered, covered_no_camera, covered_with_camera]
    private int[] dfsDP(TreeNode node) {
        if (node == null) {
            return new int[] { 0, 0, Integer.MAX_VALUE };
        }

        int[] left = dfsDP(node.left);
        int[] right = dfsDP(node.right);

        int notCovered = left[1] + right[1];
        int coveredNoCamera = Math.min(
                left[2] + Math.min(right[1], right[2]),
                right[2] + Math.min(left[1], left[2]));
        int coveredWithCamera = 1 + Math.min(left[0], Math.min(left[1], left[2])) +
                Math.min(right[0], Math.min(right[1], right[2]));

        return new int[] { notCovered, coveredNoCamera, coveredWithCamera };
    }

    public static void main(String[] args) {
        BinaryTreeCameras solution = new BinaryTreeCameras();

        // Create test tree: [0,0,null,0,0]
        TreeNode root = solution.new TreeNode(0);
        root.left = solution.new TreeNode(0);
        root.left.left = solution.new TreeNode(0);
        root.left.right = solution.new TreeNode(0);

        System.out.println(solution.minCameraCover(root)); // Expected: 1
        System.out.println(solution.minCameraCoverDP(root)); // Expected: 1
    }
}
