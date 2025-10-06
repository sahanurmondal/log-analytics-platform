# Graph Problems

## 📚 Graph Algorithms Guide

### 🎯 When to Use Graph Algorithms
Use graphs when:
- Problem involves **relationships** or **connections** between entities
- Need to find **paths**, **cycles**, or **connected components**
- Problem involves **networks**, **dependencies**, or **social connections**
- Data can be represented as **nodes (vertices)** and **edges**

### 🔑 Graph Algorithms & Time Complexities

#### 1️⃣ **Depth-First Search (DFS)** - O(V + E)
**When to use**: 
- Explore all paths, detect cycles, topological sort
- Find connected components
- Path existence problems

**Implementation**: Recursion or Stack
```java
void dfs(int node, boolean[] visited) {
    visited[node] = true;
    for (int neighbor : graph[node]) {
        if (!visited[neighbor]) dfs(neighbor, visited);
    }
}
```
**Use cases**: Islands, cycle detection, path finding
**Space**: O(V) for recursion stack

#### 2️⃣ **Breadth-First Search (BFS)** - O(V + E)
**When to use**:
- Find **shortest path** in unweighted graphs
- Level-order traversal
- Minimum steps/distance problems

**Implementation**: Queue
```java
void bfs(int start) {
    Queue<Integer> queue = new LinkedList<>();
    queue.offer(start);
    while (!queue.isEmpty()) {
        int node = queue.poll();
        for (int neighbor : graph[node]) {
            queue.offer(neighbor);
        }
    }
}
```
**Use cases**: Shortest path, level order, spreading problems
**Space**: O(V) for queue

#### 3️⃣ **Dijkstra's Algorithm** - O((V + E) log V)
**When to use**:
- Shortest path in **weighted graph** with **non-negative weights**
- Single source to all destinations

**Implementation**: Priority Queue (Min Heap)
```java
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
// pq stores [node, distance]
```
**Use cases**: Network routing, GPS navigation, flight paths
**Space**: O(V)
**Note**: Fails with negative edge weights

#### 4️⃣ **Bellman-Ford Algorithm** - O(V × E)
**When to use**:
- Shortest path with **negative edge weights**
- Detect **negative cycles**
- Single source to all destinations

**Implementation**: Relax all edges V-1 times
```java
for (int i = 0; i < V - 1; i++) {
    for (Edge edge : edges) {
        relax(edge);
    }
}
```
**Use cases**: Currency arbitrage, graphs with negative weights
**Space**: O(V)

#### 5️⃣ **Floyd-Warshall Algorithm** - O(V³)
**When to use**:
- **All-pairs shortest paths**
- Dense graphs
- Transitive closure

**Implementation**: 3 nested loops (k, i, j)
```java
for (int k = 0; k < n; k++)
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j]);
```
**Use cases**: All pairs distances, transitive closure
**Space**: O(V²)

#### 6️⃣ **Topological Sort** - O(V + E)
**When to use**:
- Order tasks with dependencies (DAG only)
- Course scheduling
- Build systems

**Implementation**: DFS or Kahn's Algorithm (BFS with indegree)
```java
// Kahn's Algorithm
Queue<Integer> queue = new LinkedList<>();
for (int i = 0; i < V; i++)
    if (indegree[i] == 0) queue.offer(i);
```
**Use cases**: Task scheduling, compilation order, prerequisite chains
**Space**: O(V)
**Note**: Only works on Directed Acyclic Graphs (DAG)

#### 7️⃣ **Union-Find (Disjoint Set)** - O(α(n)) ≈ O(1)
**When to use**:
- Find **connected components**
- Check if two nodes are connected
- Dynamic connectivity

**Implementation**: Path compression + Union by rank
```java
int find(int x) {
    if (parent[x] != x) parent[x] = find(parent[x]); // path compression
    return parent[x];
}
void union(int x, int y) {
    int rootX = find(x), rootY = find(y);
    if (rank[rootX] > rank[rootY]) parent[rootY] = rootX;
    else parent[rootX] = rootY;
}
```
**Use cases**: Network connectivity, Kruskal's MST, redundant connections
**Space**: O(V)

#### 8️⃣ **Kruskal's Algorithm (MST)** - O(E log E)
**When to use**:
- Find **Minimum Spanning Tree**
- Connect all nodes with minimum total edge weight
- Sparse graphs

**Implementation**: Sort edges + Union-Find
```java
Arrays.sort(edges, (a, b) -> a[2] - b[2]); // sort by weight
for (int[] edge : edges) {
    if (find(edge[0]) != find(edge[1])) {
        union(edge[0], edge[1]);
        mst.add(edge);
    }
}
```
**Use cases**: Network design, clustering
**Space**: O(V + E)

#### 9️⃣ **Prim's Algorithm (MST)** - O(E log V)
**When to use**:
- Find **Minimum Spanning Tree**
- Dense graphs
- Connected graph required

**Implementation**: Priority Queue
```java
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
// Start from any node, keep adding minimum weight edge
```
**Use cases**: Network design, cable laying
**Space**: O(V)

#### 🔟 **A\* Search Algorithm** - O(E) with good heuristic
**When to use**:
- Shortest path with **heuristic** (informed search)
- Game pathfinding
- GPS navigation

**Implementation**: Priority Queue with f(n) = g(n) + h(n)
```java
// g(n) = cost from start, h(n) = heuristic to goal
PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> 
    (a.g + a.h) - (b.g + b.h));
```
**Use cases**: Game AI, robotics, navigation
**Space**: O(V)

#### 1️⃣1️⃣ **Tarjan's Algorithm** - O(V + E)
**When to use**:
- Find **Strongly Connected Components** (SCC)
- Find **bridges** and **articulation points**
- Critical connections

**Implementation**: DFS with low-link values
**Use cases**: Network resilience, social network analysis
**Space**: O(V)

#### 1️⃣2️⃣ **Kosaraju's Algorithm** - O(V + E)
**When to use**:
- Find **Strongly Connected Components** (SCC)
- Simpler than Tarjan's

**Implementation**: Two DFS passes (original + transposed graph)
**Use cases**: Web crawling, dependency analysis
**Space**: O(V)

### 🎨 Graph Representations

#### **Adjacency List** - O(V + E) space
```java
List<List<Integer>> graph = new ArrayList<>();
// Efficient for sparse graphs
```
**Best for**: Most problems, sparse graphs
**DFS/BFS**: O(V + E)

#### **Adjacency Matrix** - O(V²) space
```java
int[][] graph = new int[V][V];
// graph[i][j] = 1 if edge exists
```
**Best for**: Dense graphs, quick edge lookup O(1)
**DFS/BFS**: O(V²)

#### **Edge List** - O(E) space
```java
List<int[]> edges = new ArrayList<>();
// Each int[] = {from, to, weight}
```
**Best for**: Kruskal's MST, sorting edges
**Less efficient** for traversal

### 🚀 Graph Problem-Solving Patterns

1. **Connected Components**: Use DFS/BFS or Union-Find
2. **Shortest Path (Unweighted)**: BFS
3. **Shortest Path (Weighted, No Negative)**: Dijkstra
4. **Shortest Path (Negative Weights)**: Bellman-Ford
5. **All Pairs Shortest Path**: Floyd-Warshall
6. **Minimum Spanning Tree**: Kruskal's or Prim's
7. **Cycle Detection**: DFS (with recursion stack) or Union-Find
8. **Topological Sort**: DFS or Kahn's (only for DAG)
9. **Strongly Connected Components**: Tarjan's or Kosaraju's
10. **Bipartite Check**: BFS/DFS with 2-coloring

### ⚡ Common Optimizations
- **Bidirectional BFS**: Meet in the middle - O(b^(d/2))
- **0-1 BFS**: Use deque for graphs with 0/1 weights
- **Multi-source BFS**: Start from multiple sources simultaneously
- **Early termination**: Stop when target found
- **Visited array**: Avoid revisiting nodes

## Problem List (Grouped by Pattern/Algorithm)

### Traversal (DFS/BFS)
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Number of Islands | [LeetCode 200](https://leetcode.com/problems/number-of-islands/) | [NumberOfIslands.java](./medium/NumberOfIslands.java) |
| Max Area of Island | [LeetCode 695](https://leetcode.com/problems/max-area-of-island/) | [MaxAreaOfIsland.java](./easy/MaxAreaOfIsland.java) |
| Flood Fill | [LeetCode 733](https://leetcode.com/problems/flood-fill/) | [FloodFill.java](./easy/FloodFill.java) |
| Rotting Oranges | [LeetCode 994](https://leetcode.com/problems/rotting-oranges/) | [RottingOranges.java](./medium/RottingOranges.java) |
| Pacific Atlantic Water Flow | [LeetCode 417](https://leetcode.com/problems/pacific-atlantic-water-flow/) | [PacificAtlanticWaterFlow.java](./medium/PacificAtlanticWaterFlow.java) |
| Find If Path Exists | [LeetCode 1971](https://leetcode.com/problems/find-if-path-exists-in-graph/) | [FindIfPathExists.java](./easy/FindIfPathExists.java) |
| Clone Graph | [LeetCode 133](https://leetcode.com/problems/clone-graph/) | [CloneGraph.java](./easy/CloneGraph.java) |
| Shortest Path in Maze | [LeetCode 1091](https://leetcode.com/problems/shortest-path-in-binary-matrix/) | [ShortestPathInMaze.java](./easy/ShortestPathInMaze.java) |
| Find Center of Star Graph | [LeetCode 1791](https://leetcode.com/problems/find-center-of-star-graph/) | [FindCenterOfStarGraph.java](./easy/FindCenterOfStarGraph.java) |
| Find Connected Components | [LeetCode 323](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | [FindConnectedComponents.java](./medium/FindConnectedComponents.java) |
| Count Components | [LeetCode 323](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | [CountComponents.java](./easy/CountComponents.java) |
| Number of Provinces | [LeetCode 547](https://leetcode.com/problems/number-of-provinces/) | [NumberOfProvinces.java](./easy/NumberOfProvinces.java) |
| Word Ladder | [LeetCode 127](https://leetcode.com/problems/word-ladder/) | [WordLadder.java](./medium/WordLadder.java) |

### Shortest Path
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Dijkstra's Shortest Path | [LeetCode 743](https://leetcode.com/problems/network-delay-time/) | [DijkstraShortestPath.java](./medium/DijkstraShortestPath.java) |
| Network Delay Time | [LeetCode 743](https://leetcode.com/problems/network-delay-time/) | [NetworkDelayTime.java](./medium/NetworkDelayTime.java) |
| Bellman-Ford Shortest Path | [LeetCode 787](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | [BellmanFordShortestPath.java](./medium/BellmanFordShortestPath.java) |
| A* Shortest Path | [A* Algorithm](https://en.wikipedia.org/wiki/A*_search_algorithm) | [AStarShortestPath.java](./medium/AStarShortestPath.java) |
| Shortest Path with Alternating Colors | [LeetCode 1129](https://leetcode.com/problems/shortest-path-with-alternating-colors/) | [ShortestPathWithAlternatingColors.java](./hard/ShortestPathWithAlternatingColors.java) |
| Find Longest Path in DAG | [LeetCode 1857](https://leetcode.com/problems/largest-color-value-in-a-directed-graph/) | [FindLongestPathInDAG.java](./hard/FindLongestPathInDAG.java) |
| Find The City With The Smallest Number Of Neighbors | [LeetCode 1334](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/) | [FindTheCityWithTheSmallestNumberOfNeighbors.java](./hard/FindTheCityWithTheSmallestNumberOfNeighbors.java) |
| Minimum Cost to Connect All Points | [LeetCode 1584](https://leetcode.com/problems/min-cost-to-connect-all-points/) | [MinimumCostToConnectAllPoints.java](./hard/MinimumCostToConnectAllPoints.java) |
| Shortest Path in Binary Matrix | [LeetCode 1091](https://leetcode.com/problems/shortest-path-in-binary-matrix/) | [ShortestPathInBinaryMatrix.java](./medium/ShortestPathInBinaryMatrix.java) |
| Shortest Path Dijkstra | [LeetCode 743](https://leetcode.com/problems/network-delay-time/) | [ShortestPathDijkstra.java](./medium/ShortestPathDijkstra.java) |
| Shortest Path with Negative Weights | [LeetCode 787](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | [ShortestPathWithNegativeWeights.java](./hard/ShortestPathWithNegativeWeights.java) |
| All Pairs Shortest Path (Floyd-Warshall) | [Floyd-Warshall Algorithm](https://en.wikipedia.org/wiki/Floyd–Warshall_algorithm) | [AllPairsShortestPathFloydWarshall.java](./hard/AllPairsShortestPathFloydWarshall.java) |

### Topological Sort / Cycle Detection
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Course Schedule | [LeetCode 207](https://leetcode.com/problems/course-schedule/) | [DetectCycleDirectedGraph.java](./medium/DetectCycleDirectedGraph.java) |
| Course Schedule II | [LeetCode 210](https://leetcode.com/problems/course-schedule-ii/) | [TopologicalSort.java](./medium/TopologicalSort.java) |
| Alien Dictionary | [LeetCode 269](https://leetcode.com/problems/alien-dictionary/) | [AlienDictionary.java](./hard/AlienDictionary.java) |
| All Topological Sorts | [Backtracking](https://www.geeksforgeeks.org/all-topological-sorts-of-a-directed-acyclic-graph/) | [AllTopologicalSorts.java](./hard/AllTopologicalSorts.java) |
| Detect Cycle in Undirected Graph | [LeetCode 261](https://leetcode.com/problems/graph-valid-tree/) | [DetectCycleInUndirectedGraph.java](./medium/DetectCycleInUndirectedGraph.java) |
| Course Schedule | [LeetCode 207](https://leetcode.com/problems/course-schedule/) | [CourseSchedule.java](./medium/CourseSchedule.java) |
| Course Schedule II | [LeetCode 210](https://leetcode.com/problems/course-schedule-ii/) | [CourseScheduleII.java](./medium/CourseScheduleII.java) |
| Detect Cycle in Directed Graph | [LeetCode 207](https://leetcode.com/problems/course-schedule/) | [DetectCycleInDirectedGraph.java](./medium/DetectCycleInDirectedGraph.java) |
| Graph Valid Tree | [LeetCode 261](https://leetcode.com/problems/graph-valid-tree/) | [GraphValidTree.java](./medium/GraphValidTree.java) |

### Union Find
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Number of Islands | [LeetCode 200](https://leetcode.com/problems/number-of-islands/) | [NumberOfIslands.java](./medium/NumberOfIslands.java) |
| Redundant Connection | [LeetCode 684](https://leetcode.com/problems/redundant-connection/) | [RedundantConnection.java](./medium/RedundantConnection.java) |

### Minimum Spanning Tree
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Kruskal Minimum Spanning Tree | [LeetCode 1584](https://leetcode.com/problems/min-cost-to-connect-all-points/) | [KruskalMinimumSpanningTree.java](./medium/KruskalMinimumSpanningTree.java) |
| Prim Minimum Spanning Tree | [LeetCode 1584](https://leetcode.com/problems/min-cost-to-connect-all-points/) | [PrimMinimumSpanningTree.java](./medium/PrimMinimumSpanningTree.java) |
| Minimum Height Trees | [LeetCode 310](https://leetcode.com/problems/minimum-height-trees/) | [MinimumHeightTrees.java](./medium/MinimumHeightTrees.java) |
| Minimum Spanning Tree Kruskal | [LeetCode 1135](https://leetcode.com/problems/connecting-cities-with-minimum-cost/) | [MinimumSpanningTreeKruskal.java](./medium/MinimumSpanningTreeKruskal.java) |
| Minimum Spanning Tree | [LeetCode 1584](https://leetcode.com/problems/min-cost-to-connect-all-points/) | [MinimumSpanningTree.java](./hard/MinimumSpanningTree.java) |

### Advanced Graph Algorithms
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Strongly Connected Components | [Tarjan/Kosaraju](https://en.wikipedia.org/wiki/Strongly_connected_component) | [StronglyConnectedComponents.java](./hard/StronglyConnectedComponents.java) |
| Articulation Points | [Tarjan](https://en.wikipedia.org/wiki/Biconnected_component) | [FindArticulationPoints.java](./hard/FindArticulationPoints.java) |
| Bridges in Graph | [Tarjan](https://en.wikipedia.org/wiki/Bridge_(graph_theory)) | [FindBridgesInGraph.java](./hard/FindBridgesInGraph.java) |
| Biconnected Components | [Tarjan](https://en.wikipedia.org/wiki/Biconnected_component) | [FindBiconnectedComponents.java](./hard/FindBiconnectedComponents.java) |
| Eulerian Path | [Hierholzer](https://en.wikipedia.org/wiki/Eulerian_path) | [FindEulerianPath.java](./hard/FindEulerianPath.java) |
| Hamiltonian Path | [Backtracking/DP](https://en.wikipedia.org/wiki/Hamiltonian_path) | [FindHamiltonianPath.java](./hard/FindHamiltonianPath.java) |
| Find All Cycles in Graph | [Johnson's Algorithm](https://en.wikipedia.org/wiki/Johnson%27s_algorithm) | [FindAllCyclesInGraph.java](./hard/FindAllCyclesInGraph.java) |
| Find Minimum Cut | [Edmonds-Karp/Stoer-Wagner](https://en.wikipedia.org/wiki/Minimum_cut) | [FindMinimumCut.java](./hard/FindMinimumCut.java) |
| Find Maximum Flow | [Edmonds-Karp/Dinic's](https://en.wikipedia.org/wiki/Maximum_flow_problem) | [FindMaximumFlow.java](./hard/FindMaximumFlow.java) |
| Find Eventual Safe States | [LeetCode 802](https://leetcode.com/problems/find-eventual-safe-states/) | [FindEventualSafeStates.java](./medium/FindEventualSafeStates.java) |
| Critical Connections in a Network | [LeetCode 1192](https://leetcode.com/problems/critical-connections-in-a-network/) | [FindCriticalConnections.java](./hard/FindCriticalConnections.java) |
| Find Bridges in Graph | [LeetCode 1192](https://leetcode.com/problems/critical-connections-in-a-network/) | [FindBridgesInGraph.java](./medium/FindBridgesInGraph.java) |
| Find Articulation Points in Graph | [Tarjan Algorithm](https://en.wikipedia.org/wiki/Biconnected_component) | [FindArticulationPointsInGraph.java](./medium/FindArticulationPointsInGraph.java) |

### Miscellaneous
| Problem Name | LeetCode Link | Code Link |
|--------------|--------------|-----------|
| Find The Town Judge | [LeetCode 997](https://leetcode.com/problems/find-the-town-judge/) | [FindTheTownJudge.java](./medium/FindTheTownJudge.java) |
| Find Center of Star Graph | [LeetCode 1791](https://leetcode.com/problems/find-center-of-star-graph/) | [FindCenterOfStarGraph.java](./easy/FindCenterOfStarGraph.java) |

## Company Tags

- **Google**: 200, 207, 210, 743, 787, 1192, 1334
- **Facebook**: 200, 207, 721, 994, 1192
- **Amazon**: 200, 207, 994, 1192, 1584
- **Microsoft**: 200, 207, 743, 1192

## Patterns

- Traversal: DFS, BFS, Bidirectional BFS
- Shortest Path: Dijkstra, Bellman-Ford, Floyd-Warshall, A*
- Topological Sort: Kahn's, DFS, Backtracking
- Union Find: Islands, Accounts Merge, Redundant Connection
- Minimum Spanning Tree: Kruskal, Prim
- Advanced: SCC, Articulation Points, Bridges, Network Flow, Eulerian/Hamiltonian Path, Minimum Cut
