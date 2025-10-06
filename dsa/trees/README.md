# Binary Tree Problems

Binary tree problems including traversal, construction, manipulation, and tree-based algorithms.

---

## 🎯 When to Use Tree Algorithms

Tree algorithms are fundamental for hierarchical data structures and are essential for solving problems involving:
- **Hierarchical Relationships**: File systems, organizational structures, XML/HTML DOM
- **Searching**: Binary search trees for efficient lookup, insertion, deletion
- **Expression Evaluation**: Parse trees, abstract syntax trees
- **Decision Making**: Decision trees, game trees (minimax)
- **Network Routing**: Spanning trees in networks
- **Database Indexing**: B-trees, B+ trees
- **Compression**: Huffman coding trees
- **Range Queries**: Segment trees, Fenwick trees

---

## 🔑 Tree Algorithms & Their Use Cases

### 1. **Depth-First Search (DFS) - Inorder, Preorder, Postorder**
**When to Use**: Need to visit all nodes, process tree structure, serialize/deserialize trees  
**Time Complexity**: O(n)  
**Space Complexity**: O(h) for recursion stack, where h is tree height  

**Inorder (Left → Root → Right)**:
- **Use Case**: Get sorted sequence from BST
- **Applications**: BST validation, finding kth smallest element
```java
void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    inorder(root.left, result);
    result.add(root.val);
    inorder(root.right, result);
}
```

**Preorder (Root → Left → Right)**:
- **Use Case**: Tree serialization, creating copy of tree
- **Applications**: Prefix expressions, tree cloning
```java
void preorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    result.add(root.val);
    preorder(root.left, result);
    preorder(root.right, result);
}
```

**Postorder (Left → Right → Root)**:
- **Use Case**: Deletion of tree, calculating tree properties
- **Applications**: Postfix expressions, tree deletion, calculating subtree sums
```java
void postorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    postorder(root.left, result);
    postorder(root.right, result);
    result.add(root.val);
}
```

### 2. **Breadth-First Search (BFS) - Level Order Traversal**
**When to Use**: Need to process tree level by level, find shortest path in unweighted tree  
**Time Complexity**: O(n)  
**Space Complexity**: O(w) where w is maximum width of tree  

**Use Cases**:
- Level order traversal
- Finding minimum depth
- Zigzag traversal
- Right/left side view of tree
- Level-wise processing

```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> level = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

### 3. **Binary Search Tree (BST) Operations**
**When to Use**: Need efficient searching, insertion, deletion with ordered data  
**Time Complexity**: O(log n) average, O(n) worst case for unbalanced tree  
**Space Complexity**: O(h) for recursion  

**Operations**:
- **Search**: O(log n) average
- **Insert**: O(log n) average
- **Delete**: O(log n) average
- **Min/Max**: O(h)
- **Inorder Successor/Predecessor**: O(h)

```java
// BST Search
TreeNode search(TreeNode root, int val) {
    if (root == null || root.val == val) return root;
    return val < root.val ? search(root.left, val) : search(root.right, val);
}

// BST Insert
TreeNode insert(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    if (val < root.val) root.left = insert(root.left, val);
    else if (val > root.val) root.right = insert(root.right, val);
    return root;
}

// BST Validation
boolean isValidBST(TreeNode root, long min, long max) {
    if (root == null) return true;
    if (root.val <= min || root.val >= max) return false;
    return isValidBST(root.left, min, root.val) && 
           isValidBST(root.right, root.val, max);
}
```

### 4. **Tree Dynamic Programming**
**When to Use**: Problems involving optimal subtree selection, path sums, tree properties  
**Time Complexity**: O(n)  
**Space Complexity**: O(h) for recursion  

**Common Patterns**:
- **Maximum Path Sum**: Track max through each node
- **House Robber III**: Choose optimal nodes (non-adjacent)
- **Diameter of Tree**: Longest path between any two nodes
- **Height Balanced Check**: Check balance at each node

```java
// Maximum Path Sum
int maxPathSum = Integer.MIN_VALUE;

int maxPathSumHelper(TreeNode root) {
    if (root == null) return 0;
    
    int left = Math.max(0, maxPathSumHelper(root.left));
    int right = Math.max(0, maxPathSumHelper(root.right));
    
    maxPathSum = Math.max(maxPathSum, root.val + left + right);
    return root.val + Math.max(left, right);
}

// House Robber III (Rob or Skip)
int[] robHelper(TreeNode root) {
    if (root == null) return new int[]{0, 0};
    
    int[] left = robHelper(root.left);
    int[] right = robHelper(root.right);
    
    int rob = root.val + left[1] + right[1];  // Rob this node
    int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
    
    return new int[]{rob, skip};
}
```

### 5. **Lowest Common Ancestor (LCA)**
**When to Use**: Find common ancestor of two nodes  
**Time Complexity**: O(n) for binary tree, O(log n) for BST  
**Space Complexity**: O(h)  

**Binary Tree LCA**:
```java
TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);
    
    if (left != null && right != null) return root;
    return left != null ? left : right;
}
```

**BST LCA** (More efficient):
```java
TreeNode lowestCommonAncestorBST(TreeNode root, TreeNode p, TreeNode q) {
    if (p.val < root.val && q.val < root.val) 
        return lowestCommonAncestorBST(root.left, p, q);
    if (p.val > root.val && q.val > root.val) 
        return lowestCommonAncestorBST(root.right, p, q);
    return root;
}
```

### 6. **Tree Construction from Traversals**
**When to Use**: Rebuild tree from serialized format or traversal arrays  
**Time Complexity**: O(n)  
**Space Complexity**: O(n)  

**From Inorder + Preorder**:
```java
Map<Integer, Integer> inorderMap = new HashMap<>();
int preIndex = 0;

TreeNode buildTree(int[] preorder, int[] inorder) {
    for (int i = 0; i < inorder.length; i++) {
        inorderMap.put(inorder[i], i);
    }
    return build(preorder, 0, inorder.length - 1);
}

TreeNode build(int[] preorder, int inStart, int inEnd) {
    if (inStart > inEnd) return null;
    
    int rootVal = preorder[preIndex++];
    TreeNode root = new TreeNode(rootVal);
    
    int inIndex = inorderMap.get(rootVal);
    root.left = build(preorder, inStart, inIndex - 1);
    root.right = build(preorder, inIndex + 1, inEnd);
    
    return root;
}
```

### 7. **Morris Traversal (Constant Space)**
**When to Use**: Need O(1) space traversal (no recursion/stack)  
**Time Complexity**: O(n)  
**Space Complexity**: O(1)  

**Inorder Morris Traversal**:
```java
List<Integer> morrisInorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    TreeNode current = root;
    
    while (current != null) {
        if (current.left == null) {
            result.add(current.val);
            current = current.right;
        } else {
            TreeNode predecessor = current.left;
            while (predecessor.right != null && predecessor.right != current) {
                predecessor = predecessor.right;
            }
            
            if (predecessor.right == null) {
                predecessor.right = current;
                current = current.left;
            } else {
                predecessor.right = null;
                result.add(current.val);
                current = current.right;
            }
        }
    }
    return result;
}
```

### 8. **Tree Serialization & Deserialization**
**When to Use**: Save tree to disk, transmit over network, deep copy  
**Time Complexity**: O(n)  
**Space Complexity**: O(n)  

```java
// Serialize using preorder
String serialize(TreeNode root) {
    if (root == null) return "null,";
    return root.val + "," + serialize(root.left) + serialize(root.right);
}

// Deserialize
TreeNode deserialize(String data) {
    Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
    return deserializeHelper(queue);
}

TreeNode deserializeHelper(Queue<String> queue) {
    String val = queue.poll();
    if (val.equals("null")) return null;
    TreeNode root = new TreeNode(Integer.parseInt(val));
    root.left = deserializeHelper(queue);
    root.right = deserializeHelper(queue);
    return root;
}
```

### 9. **Path Problems (Root to Leaf, Any Path)**
**When to Use**: Find paths with specific sum, maximum path, all paths  
**Time Complexity**: O(n)  
**Space Complexity**: O(h) for path tracking  

**Path Sum II (All Root-to-Leaf Paths)**:
```java
List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> result = new ArrayList<>();
    findPaths(root, targetSum, new ArrayList<>(), result);
    return result;
}

void findPaths(TreeNode node, int remaining, List<Integer> path, 
               List<List<Integer>> result) {
    if (node == null) return;
    
    path.add(node.val);
    
    if (node.left == null && node.right == null && remaining == node.val) {
        result.add(new ArrayList<>(path));
    }
    
    findPaths(node.left, remaining - node.val, path, result);
    findPaths(node.right, remaining - node.val, path, result);
    
    path.remove(path.size() - 1);  // Backtrack
}
```

### 10. **Tree Views (Top, Bottom, Left, Right Side)**
**When to Use**: Get boundary view, side views of tree  
**Time Complexity**: O(n)  
**Space Complexity**: O(w) for queue, O(n) for result  

**Right Side View** (BFS approach):
```java
List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            if (i == levelSize - 1) result.add(node.val);  // Rightmost
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }
    return result;
}
```

### 11. **Tree Balancing & Rotations**
**When to Use**: Self-balancing trees (AVL, Red-Black), maintain O(log n) operations  
**Time Complexity**: O(log n) per operation  
**Space Complexity**: O(log n)  

**AVL Tree Rotations**:
```java
// Right Rotation (LL case)
TreeNode rotateRight(TreeNode y) {
    TreeNode x = y.left;
    TreeNode T2 = x.right;
    x.right = y;
    y.left = T2;
    updateHeight(y);
    updateHeight(x);
    return x;
}

// Left Rotation (RR case)
TreeNode rotateLeft(TreeNode x) {
    TreeNode y = x.right;
    TreeNode T2 = y.left;
    y.left = x;
    x.right = T2;
    updateHeight(x);
    updateHeight(y);
    return y;
}

int getBalance(TreeNode node) {
    return node == null ? 0 : height(node.left) - height(node.right);
}
```

### 12. **Vertical Order Traversal**
**When to Use**: Print tree nodes column-wise  
**Time Complexity**: O(n log n) due to sorting  
**Space Complexity**: O(n)  

```java
List<List<Integer>> verticalOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Map<Integer, List<Integer>> map = new TreeMap<>();
    Queue<Pair<TreeNode, Integer>> queue = new LinkedList<>();
    queue.offer(new Pair<>(root, 0));
    
    while (!queue.isEmpty()) {
        Pair<TreeNode, Integer> pair = queue.poll();
        TreeNode node = pair.getKey();
        int col = pair.getValue();
        
        map.computeIfAbsent(col, k -> new ArrayList<>()).add(node.val);
        
        if (node.left != null) queue.offer(new Pair<>(node.left, col - 1));
        if (node.right != null) queue.offer(new Pair<>(node.right, col + 1));
    }
    
    result.addAll(map.values());
    return result;
}
```

---

## 📋 Common Tree Problem Patterns

### Pattern 1: Tree Traversal Variations
- **Problems**: Inorder/Preorder/Postorder, Level Order, Zigzag, Spiral
- **Approach**: Choose DFS (recursion/stack) or BFS (queue)
- **Key**: Understand when to use each traversal type

### Pattern 2: Tree Properties & Validation
- **Problems**: Height, Diameter, Balanced Check, BST Validation
- **Approach**: Post-order DFS, return properties from subtrees
- **Key**: Combine information from left and right subtrees

### Pattern 3: Path Finding
- **Problems**: Path Sum, All Paths, Maximum Path Sum, Diameter
- **Approach**: DFS with backtracking or return values
- **Key**: Track path during traversal, backtrack when needed

### Pattern 4: Tree Construction
- **Problems**: Build from traversals, Clone, Serialize/Deserialize
- **Approach**: Use HashMap for index lookup, recursion
- **Key**: Identify root position, divide and conquer

### Pattern 5: Lowest Common Ancestor
- **Problems**: LCA in Binary Tree, LCA in BST
- **Approach**: Recursive search, check both subtrees
- **Key**: Different approaches for BST vs general tree

### Pattern 6: Tree Modification
- **Problems**: Flatten to Linked List, Invert, Prune
- **Approach**: Post-order traversal, modify after children
- **Key**: Process children before parent

### Pattern 7: Tree Views
- **Problems**: Right View, Left View, Top View, Bottom View
- **Approach**: BFS with level tracking or DFS with depth tracking
- **Key**: Track first/last node at each level/column

### Pattern 8: Subtree Problems
- **Problems**: Subtree of Another Tree, Duplicate Subtrees, Maximum BST Subtree
- **Approach**: Serialize subtrees, use hashing
- **Key**: Convert tree to comparable format

### Pattern 9: Distance & Nearest Problems
- **Problems**: Nodes at Distance K, Nearest Leaf, Cousins
- **Approach**: BFS for shortest path, DFS with distance tracking
- **Key**: Convert tree to graph if needed

### Pattern 10: Tree DP Optimization
- **Problems**: House Robber III, Binary Tree Cameras, Sum of Distances
- **Approach**: Return multiple values (rob/skip, covered/not covered)
- **Key**: Memoize subtree results

---

## 🎓 Problem-Solving Steps for Tree Problems

1. **Identify Tree Type**
   - Binary tree vs BST vs N-ary tree
   - Complete vs balanced vs skewed

2. **Choose Traversal Method**
   - DFS (Inorder/Preorder/Postorder) for depth-first
   - BFS (Level Order) for level-wise
   - Morris for constant space

3. **Determine Information Flow**
   - Top-down: Pass information from parent to children
   - Bottom-up: Gather information from children to parent
   - Both: Combination (e.g., LCA)

4. **Handle Edge Cases**
   - Empty tree (root == null)
   - Single node tree
   - Skewed tree (all left or all right)
   - Duplicate values

5. **Optimize Space**
   - Use iteration instead of recursion if stack space is concern
   - Morris traversal for O(1) space
   - Process nodes level by level for BFS

---

## 🚀 Optimization Techniques

1. **Memoization**: Cache subtree results for repeated computations
2. **Early Termination**: Stop when answer found (e.g., path exists)
3. **Parent Pointers**: Add parent reference for upward traversal
4. **Level Tracking**: Track depth/level to avoid redundant calculations
5. **Morris Traversal**: O(1) space alternative to recursive traversal

---

## 🔍 Algorithm Selection Guide

| Problem Type | Best Algorithm | Time | Space |
|-------------|---------------|------|-------|
| Sorted sequence from BST | Inorder DFS | O(n) | O(h) |
| Level-wise processing | BFS | O(n) | O(w) |
| Tree cloning/serialization | Preorder DFS | O(n) | O(h) |
| Subtree deletion | Postorder DFS | O(n) | O(h) |
| Shortest path/min depth | BFS | O(n) | O(w) |
| BST search/insert/delete | BST Operations | O(log n) avg | O(h) |
| Maximum path sum | Tree DP | O(n) | O(h) |
| Find ancestor | LCA | O(n) | O(h) |
| Rebuild tree | Construction | O(n) | O(n) |
| Constant space traversal | Morris Traversal | O(n) | O(1) |
| Side views | BFS + Level Tracking | O(n) | O(w) |
| Tree balancing | AVL/Red-Black Rotations | O(log n) | O(log n) |

**Legend**: n = nodes, h = height, w = max width

---

## Problem List (Grouped by Difficulty)

### Easy
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Binary Tree Inorder Traversal | [LeetCode Problem](https://leetcode.com/problems/binary-tree-inorder-traversal/) | [BinaryTreeInorderTraversal.java](./easy/BinaryTreeInorderTraversal.java) |
| Binary Tree Paths | [LeetCode Problem](https://leetcode.com/problems/binary-tree-paths/) | [BinaryTreePaths.java](./easy/BinaryTreePaths.java) |
| Same Tree | [LeetCode Problem](https://leetcode.com/problems/same-tree/) | [SameTree.java](./easy/SameTree.java) |
| Subtree Of Another Tree | [LeetCode Problem](https://leetcode.com/problems/subtree-of-another-tree/) | [SubtreeOfAnotherTree.java](./easy/SubtreeOfAnotherTree.java) |

### Medium
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Binary Tree Level Order Traversal | [LeetCode Problem](https://leetcode.com/problems/binary-tree-level-order-traversal/) | [BinaryTreeLevelOrderTraversal.java](./medium/BinaryTreeLevelOrderTraversal.java) |
| Binary Tree Postorder Traversal | [LeetCode Problem](https://leetcode.com/problems/binary-tree-postorder-traversal/) | [BinaryTreePostorderTraversal.java](./medium/BinaryTreePostorderTraversal.java) |
| Binary Tree Right Side View | [LeetCode Problem](https://leetcode.com/problems/binary-tree-right-side-view/) | [BinaryTreeRightSideView.java](./medium/BinaryTreeRightSideView.java) |
| Binary Tree Zigzag Level Order Traversal | [LeetCode Problem](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/) | [BinaryTreeZigzagLevelOrderTraversal.java](./medium/BinaryTreeZigzagLevelOrderTraversal.java) |
| Construct Binary Tree From Inorder And Postorder Traversal | [LeetCode Problem](https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/) | [ConstructBinaryTreeFromInorderAndPostorderTraversal.java](./medium/ConstructBinaryTreeFromInorderAndPostorderTraversal.java) |
| Construct Binary Tree From Preorder And Inorder Traversal | [LeetCode Problem](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) | [ConstructBinaryTreeFromPreorderAndInorderTraversal.java](./medium/ConstructBinaryTreeFromPreorderAndInorderTraversal.java) |
| Construct Quad Tree | [LeetCode Problem](https://leetcode.com/problems/construct-quad-tree/) | [ConstructQuadTree.java](./medium/ConstructQuadTree.java) |
| Convert Sorted Array To Binary Search Tree | [LeetCode Problem](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/) | [ConvertSortedArrayToBinarySearchTree.java](./medium/ConvertSortedArrayToBinarySearchTree.java) |
| Convert Sorted List To Binary Search Tree | [LeetCode Problem](https://leetcode.com/problems/convert-sorted-list-to-binary-search-tree/) | [ConvertSortedListToBinarySearchTree.java](./medium/ConvertSortedListToBinarySearchTree.java) |
| Count Complete Tree Nodes | [LeetCode Problem](https://leetcode.com/problems/count-complete-tree-nodes/) | [CountCompleteTreeNodes.java](./medium/CountCompleteTreeNodes.java) |
| Find All Duplicate Subtrees | [LeetCode Problem](https://leetcode.com/problems/find-all-duplicate-subtrees/) | [FindAllDuplicateSubtrees.java](./medium/FindAllDuplicateSubtrees.java) |
| Find All Nodes Distance KIn Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-all-nodes-distance-kin-binary-tree/) | [FindAllNodesDistanceKInBinaryTree.java](./medium/FindAllNodesDistanceKInBinaryTree.java) |
| Find Bottom Left Tree Value | [LeetCode Problem](https://leetcode.com/problems/find-bottom-left-tree-value/) | [FindBottomLeftTreeValue.java](./medium/FindBottomLeftTreeValue.java) |
| Find Closest Leaf In Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-closest-leaf-in-binary-tree/) | [FindClosestLeafInBinaryTree.java](./medium/FindClosestLeafInBinaryTree.java) |
| Find Diameter Of Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-diameter-of-binary-tree/) | [FindDiameterOfBinaryTree.java](./medium/FindDiameterOfBinaryTree.java) |
| Find Duplicate Subtrees | [LeetCode Problem](https://leetcode.com/problems/find-duplicate-subtrees/) | [FindDuplicateSubtrees.java](./medium/FindDuplicateSubtrees.java) |
| Find Largest Value In Each Tree Row | [LeetCode Problem](https://leetcode.com/problems/find-largest-value-in-each-tree-row/) | [FindLargestValueInEachTreeRow.java](./medium/FindLargestValueInEachTreeRow.java) |
| Find LCAOf Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-lcaof-binary-tree/) | [FindLCAOfBinaryTree.java](./medium/FindLCAOfBinaryTree.java) |
| Find Leaves Of Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-leaves-of-binary-tree/) | [FindLeavesOfBinaryTree.java](./medium/FindLeavesOfBinaryTree.java) |
| Find Longest Path With Same Value | [LeetCode Problem](https://leetcode.com/problems/find-longest-path-with-same-value/) | [FindLongestPathWithSameValue.java](./medium/FindLongestPathWithSameValue.java) |
| Find Maximum Average Subtree | [LeetCode Problem](https://leetcode.com/problems/find-maximum-average-subtree/) | [FindMaximumAverageSubtree.java](./medium/FindMaximumAverageSubtree.java) |
| Find Maximum Depth Of Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-maximum-depth-of-binary-tree/) | [FindMaximumDepthOfBinaryTree.java](./medium/FindMaximumDepthOfBinaryTree.java) |
| Find Maximum Depth Of Nary Tree | [LeetCode Problem](https://leetcode.com/problems/find-maximum-depth-of-nary-tree/) | [FindMaximumDepthOfNaryTree.java](./medium/FindMaximumDepthOfNaryTree.java) |
| Find Maximum Level Sum Of Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-maximum-level-sum-of-binary-tree/) | [FindMaximumLevelSumOfBinaryTree.java](./medium/FindMaximumLevelSumOfBinaryTree.java) |
| Find Maximum Path Sum Between Two Leaves | [LeetCode Problem](https://leetcode.com/problems/find-maximum-path-sum-between-two-leaves/) | [FindMaximumPathSumBetweenTwoLeaves.java](./medium/FindMaximumPathSumBetweenTwoLeaves.java) |
| Find Maximum Product Of Splitted Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-maximum-product-of-splitted-binary-tree/) | [FindMaximumProductOfSplittedBinaryTree.java](./medium/FindMaximumProductOfSplittedBinaryTree.java) |
| Find Maximum Sum BSTIn Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-maximum-sum-bstin-binary-tree/) | [FindMaximumSumBSTInBinaryTree.java](./medium/FindMaximumSumBSTInBinaryTree.java) |
| Find Maximum Sum Of Non Adjacent Nodes | [LeetCode Problem](https://leetcode.com/problems/find-maximum-sum-of-non-adjacent-nodes/) | [FindMaximumSumOfNonAdjacentNodes.java](./medium/FindMaximumSumOfNonAdjacentNodes.java) |
| Find Maximum Sum Of Root To Leaf Path | [LeetCode Problem](https://leetcode.com/problems/find-maximum-sum-of-root-to-leaf-path/) | [FindMaximumSumOfRootToLeafPath.java](./medium/FindMaximumSumOfRootToLeafPath.java) |
| Find Maximum Width Of Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-maximum-width-of-binary-tree/) | [FindMaximumWidthOfBinaryTree.java](./medium/FindMaximumWidthOfBinaryTree.java) |
| Find Minimum Absolute Difference In BST | [LeetCode Problem](https://leetcode.com/problems/find-minimum-absolute-difference-in-bst/) | [FindMinimumAbsoluteDifferenceInBST.java](./medium/FindMinimumAbsoluteDifferenceInBST.java) |
| Find Minimum Depth Of Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-minimum-depth-of-binary-tree/) | [FindMinimumDepthOfBinaryTree.java](./medium/FindMinimumDepthOfBinaryTree.java) |
| Find Minimum Depth Of Nary Tree | [LeetCode Problem](https://leetcode.com/problems/find-minimum-depth-of-nary-tree/) | [FindMinimumDepthOfNaryTree.java](./medium/FindMinimumDepthOfNaryTree.java) |
| Find Minimum Time To Collect All Apples | [LeetCode Problem](https://leetcode.com/problems/find-minimum-time-to-collect-all-apples/) | [FindMinimumTimeToCollectAllApples.java](./medium/FindMinimumTimeToCollectAllApples.java) |
| Find Mode In Binary Search Tree | [LeetCode Problem](https://leetcode.com/problems/find-mode-in-binary-search-tree/) | [FindModeInBinarySearchTree.java](./medium/FindModeInBinarySearchTree.java) |
| Find Path Sum III | [LeetCode Problem](https://leetcode.com/problems/find-path-sum-iii/) | [FindPathSumIII.java](./medium/FindPathSumIII.java) |
| Find Second Minimum Value In Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-second-minimum-value-in-binary-tree/) | [FindSecondMinimumValueInBinaryTree.java](./medium/FindSecondMinimumValueInBinaryTree.java) |
| Find Sum Of Left Leaves | [LeetCode Problem](https://leetcode.com/problems/find-sum-of-left-leaves/) | [FindSumOfLeftLeaves.java](./medium/FindSumOfLeftLeaves.java) |
| Find Sum Of Right Leaves | [LeetCode Problem](https://leetcode.com/problems/find-sum-of-right-leaves/) | [FindSumOfRightLeaves.java](./medium/FindSumOfRightLeaves.java) |
| Find Tilt Of Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-tilt-of-binary-tree/) | [FindTiltOfBinaryTree.java](./medium/FindTiltOfBinaryTree.java) |
| Find Tree Diameter | [LeetCode Problem](https://leetcode.com/problems/find-tree-diameter/) | [FindTreeDiameter.java](./medium/FindTreeDiameter.java) |
| Invert Binary Tree | [LeetCode Problem](https://leetcode.com/problems/invert-binary-tree/) | [InvertBinaryTree.java](./medium/InvertBinaryTree.java) |
| Lowest Common Ancestor Of BST | [LeetCode Problem](https://leetcode.com/problems/lowest-common-ancestor-of-bst/) | [LowestCommonAncestorOfBST.java](./medium/LowestCommonAncestorOfBST.java) |
| Path Sum II | [LeetCode Problem](https://leetcode.com/problems/path-sum-ii/) | [PathSumII.java](./medium/PathSumII.java) |
| Populating Next Right Pointers In Each Node | [LeetCode Problem](https://leetcode.com/problems/populating-next-right-pointers-in-each-node/) | [PopulatingNextRightPointersInEachNode.java](./medium/PopulatingNextRightPointersInEachNode.java) |
| Quad Tree Intersection | [LeetCode Problem](https://leetcode.com/problems/quad-tree-intersection/) | [QuadTreeIntersection.java](./medium/QuadTreeIntersection.java) |
| Sum Root To Leaf Numbers | [LeetCode Problem](https://leetcode.com/problems/sum-root-to-leaf-numbers/) | [SumRootToLeafNumbers.java](./medium/SumRootToLeafNumbers.java) |
| Symmetric Tree | [LeetCode Problem](https://leetcode.com/problems/symmetric-tree/) | [SymmetricTree.java](./medium/SymmetricTree.java) |
| Validate Binary Search Tree | [LeetCode Problem](https://leetcode.com/problems/validate-binary-search-tree/) | [ValidateBinarySearchTree.java](./medium/ValidateBinarySearchTree.java) |
| Zigzag Level Order Traversal | [LeetCode Problem](https://leetcode.com/problems/zigzag-level-order-traversal/) | [ZigzagLevelOrderTraversal.java](./medium/ZigzagLevelOrderTraversal.java) |

### Hard
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Binary Tree Maximum Path Sum | [LeetCode Problem](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | [BinaryTreeMaximumPathSum.java](./hard/BinaryTreeMaximumPathSum.java) |
| Count Paths With Sum | [LeetCode Problem](https://leetcode.com/problems/count-paths-with-sum/) | [CountPathsWithSum.java](./hard/CountPathsWithSum.java) |
| Count Unique BSTs | [LeetCode Problem](https://leetcode.com/problems/count-unique-bsts/) | [CountUniqueBSTs.java](./hard/CountUniqueBSTs.java) |
| Find Largest BSTSubtree | [LeetCode Problem](https://leetcode.com/problems/find-largest-bstsubtree/) | [FindLargestBSTSubtree.java](./hard/FindLargestBSTSubtree.java) |
| Find Maximum Sum Of Non Adjacent Nodes Hard | [LeetCode Problem](https://leetcode.com/problems/find-maximum-sum-of-non-adjacent-nodes-hard/) | [FindMaximumSumOfNonAdjacentNodesHard.java](./hard/FindMaximumSumOfNonAdjacentNodesHard.java) |
| Find Maximum Sum Path Between Leaves | [LeetCode Problem](https://leetcode.com/problems/find-maximum-sum-path-between-leaves/) | [FindMaximumSumPathBetweenLeaves.java](./hard/FindMaximumSumPathBetweenLeaves.java) |
| Find Minimum Number Of Nodes To Remove For Full Binary Tree | [LeetCode Problem](https://leetcode.com/problems/find-minimum-number-of-nodes-to-remove-for-full-binary-tree/) | [FindMinimumNumberOfNodesToRemoveForFullBinaryTree.java](./hard/FindMinimumNumberOfNodesToRemoveForFullBinaryTree.java) |
| Longest Zig Zag Path In Binary Tree | [LeetCode Problem](https://leetcode.com/problems/longest-zig-zag-path-in-binary-tree/) | [LongestZigZagPathInBinaryTree.java](./hard/LongestZigZagPathInBinaryTree.java) |
| Maximum Binary Tree | [LeetCode Problem](https://leetcode.com/problems/maximum-binary-tree/) | [MaximumBinaryTree.java](./hard/MaximumBinaryTree.java) |
| Maximum Binary Tree II | [LeetCode Problem](https://leetcode.com/problems/maximum-binary-tree-ii/) | [MaximumBinaryTreeII.java](./hard/MaximumBinaryTreeII.java) |
| Maximum Sum Of Three Non Overlapping Subtrees | [LeetCode Problem](https://leetcode.com/problems/maximum-sum-of-three-non-overlapping-subtrees/) | [MaximumSumOfThreeNonOverlappingSubtrees.java](./hard/MaximumSumOfThreeNonOverlappingSubtrees.java) |
| Maximum Width Ramp In Binary Tree | [LeetCode Problem](https://leetcode.com/problems/maximum-width-ramp-in-binary-tree/) | [MaximumWidthRampInBinaryTree.java](./hard/MaximumWidthRampInBinaryTree.java) |
| Minimum Cost To Merge Stones | [LeetCode Problem](https://leetcode.com/problems/minimum-cost-to-merge-stones/) | [MinimumCostToMergeStones.java](./hard/MinimumCostToMergeStones.java) |
| Minimum Number Of Nodes To Remove For Full Binary Tree | [LeetCode Problem](https://leetcode.com/problems/minimum-number-of-nodes-to-remove-for-full-binary-tree/) | [MinimumNumberOfNodesToRemoveForFullBinaryTree.java](./hard/MinimumNumberOfNodesToRemoveForFullBinaryTree.java) |
| Recover Binary Search Tree | [LeetCode Problem](https://leetcode.com/problems/recover-binary-search-tree/) | [RecoverBinarySearchTree.java](./hard/RecoverBinarySearchTree.java) |
| Serialize And Deserialize Binary Tree | [LeetCode Problem](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) | [SerializeAndDeserializeBinaryTree.java](./hard/SerializeAndDeserializeBinaryTree.java) |
| Unique BSTII | [LeetCode Problem](https://leetcode.com/problems/unique-bstii/) | [UniqueBSTII.java](./hard/UniqueBSTII.java) |

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
