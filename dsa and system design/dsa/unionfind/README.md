# Union-Find (Disjoint Set Union) Problems

Union-Find is a data structure that efficiently handles dynamic connectivity queries and supports two main operations:
- **Find**: Determine which set an element belongs to
- **Union**: Merge two sets together

## Core Concepts

### Path Compression
Flattens the tree structure during find operations to improve future queries.

### Union by Rank/Size
Always attach the smaller tree under the root of the larger tree to keep trees balanced.

## Time Complexity
- **Find**: O(α(n)) - Nearly constant time due to inverse Ackermann function
- **Union**: O(α(n)) - Nearly constant time
- **Space**: O(n) - Linear space for parent and rank arrays

## Problem Categories

### 1. Basic Union-Find (Easy)
- Number of Connected Components
- Find if Path Exists in Graph

### 2. Graph Connectivity (Medium)
- Accounts Merge
- Most Stones Removed with Same Row or Column
- Redundant Connection
- Satisfiability of Equality Equations

### 3. Grid Problems (Medium)
- Number of Islands II
- Regions Cut By Slashes
- Surrounded Regions

### 4. Dynamic Connectivity (Medium/Hard)
- Friend Circles
- Number of Operations to Make Network Connected
- Smallest String With Swaps

### 5. Advanced Applications (Hard)
- Minimize Malware Spread
- Number of Good Paths
- Checking Existence of Edge Length Limited Paths

## Template Code

```java
class UnionFind {
    private int[] parent;
    private int[] rank;
    private int components;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }
    
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) return false;
        
        // Union by rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        
        components--;
        return true;
    }
    
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
    
    public int getComponents() {
        return components;
    }
}
```

## Visual Examples

### Basic Union-Find Operations
```
Initial: [0] [1] [2] [3] [4]
Union(0,1): [0,1] [2] [3] [4]
Union(2,3): [0,1] [2,3] [4]
Union(1,3): [0,1,2,3] [4]
```

### Path Compression
```
Before:     After find(4):
   0           0
   |           |\
   1           1 2
   |           | |
   2           3 4
   |
   3
   |
   4
```

## Key Insights

1. **When to Use Union-Find**: Dynamic connectivity, grouping, cycle detection
2. **Optimization**: Always use path compression and union by rank
3. **Common Patterns**: 
   - Convert 2D coordinates to 1D: `row * cols + col`
   - Virtual nodes for boundary conditions
   - Reverse thinking for "remove" operations

## Interview Tips

- Always implement with path compression and union by rank
- Consider edge cases: self-loops, already connected nodes
- Think about the problem in terms of "groups" or "components"
- For grid problems, consider 4-directional or 8-directional connectivity

