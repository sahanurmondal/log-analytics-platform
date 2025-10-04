package graphs.hard;

/**
 * Variation: Find Maximum Flow (Edmonds-Karp/Dinic's)
 *
 * Description:
 * Find the maximum flow from source to sink in a directed graph.
 *
 * Constraints:
 * - 1 <= n <= 10^4
 * - 0 <= edges.length <= 2*10^5
 *
 * Follow-up:
 * - Can you optimize for large graphs?
 * - Can you handle both directed and undirected graphs?
 */
public class FindMaximumFlow {
    // Approach 1: Edmonds-Karp (BFS-based Ford-Fulkerson) - O(VE^2)
    public int maxFlow(int n, int[][] edges, int source, int sink) {
        int[][] capacity = new int[n][n];
        for (int[] e : edges) {
            capacity[e[0]][e[1]] += e[2]; // handle multiple edges
        }
        int flow = 0;
        int[] parent = new int[n];
        while (bfs(capacity, parent, source, sink)) {
            int pathFlow = Integer.MAX_VALUE;
            int v = sink;
            while (v != source) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v]);
                v = u;
            }
            v = sink;
            while (v != source) {
                int u = parent[v];
                capacity[u][v] -= pathFlow;
                capacity[v][u] += pathFlow;
                v = u;
            }
            flow += pathFlow;
        }
        return flow;
    }

    private boolean bfs(int[][] capacity, int[] parent, int source, int sink) {
        int n = capacity.length;
        boolean[] visited = new boolean[n];
        java.util.Queue<Integer> q = new java.util.LinkedList<>();
        q.offer(source);
        visited[source] = true;
        parent[source] = -1;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < n; v++) {
                if (!visited[v] && capacity[u][v] > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    if (v == sink)
                        return true;
                    q.offer(v);
                }
            }
        }
        return false;
    }

    // Approach 2: Dinic's Algorithm (optimized for large graphs) - O(EV^2) worst,
    // O(E√V) for unit capacity
    public int maxFlowDinic(int n, int[][] edges, int source, int sink) {
        Dinic dinic = new Dinic(n, source, sink);
        for (int[] e : edges)
            dinic.addEdge(e[0], e[1], e[2]);
        return dinic.maxFlow();
    }

    static class Dinic {
        int n, s, t;
        java.util.List<Edge>[] adj;
        int[] level, ptr;

        static class Edge {
            int to, rev;
            int cap;

            Edge(int to, int rev, int cap) {
                this.to = to;
                this.rev = rev;
                this.cap = cap;
            }
        }

        Dinic(int n, int s, int t) {
            this.n = n;
            this.s = s;
            this.t = t;
            adj = new java.util.ArrayList[n];
            for (int i = 0; i < n; i++)
                adj[i] = new java.util.ArrayList<>();
        }

        void addEdge(int u, int v, int cap) {
            adj[u].add(new Edge(v, adj[v].size(), cap));
            adj[v].add(new Edge(u, adj[u].size() - 1, 0)); // reverse edge
        }

        boolean bfs() {
            level = new int[n];
            java.util.Arrays.fill(level, -1);
            java.util.Queue<Integer> q = new java.util.LinkedList<>();
            q.offer(s);
            level[s] = 0;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (Edge e : adj[u]) {
                    if (e.cap > 0 && level[e.to] == -1) {
                        level[e.to] = level[u] + 1;
                        q.offer(e.to);
                    }
                }
            }
            return level[t] != -1;
        }

        int dfs(int u, int pushed) {
            if (pushed == 0)
                return 0;
            if (u == t)
                return pushed;
            for (; ptr[u] < adj[u].size(); ++ptr[u]) {
                Edge e = adj[u].get(ptr[u]);
                if (level[e.to] == level[u] + 1 && e.cap > 0) {
                    int tr = dfs(e.to, Math.min(pushed, e.cap));
                    if (tr > 0) {
                        e.cap -= tr;
                        adj[e.to].get(e.rev).cap += tr;
                        return tr;
                    }
                }
            }
            return 0;
        }

        int maxFlow() {
            int flow = 0;
            while (bfs()) {
                ptr = new int[n];
                int pushed;
                while ((pushed = dfs(s, Integer.MAX_VALUE)) > 0) {
                    flow += pushed;
                }
            }
            return flow;
        }
    }

    public static void main(String[] args) {
        FindMaximumFlow mf = new FindMaximumFlow();
        System.out.println(mf.maxFlow(4, new int[][] { { 0, 1, 10 }, { 1, 2, 5 }, { 2, 3, 10 }, { 0, 2, 15 } }, 0, 3)); // 15
        System.out.println(mf.maxFlow(4, new int[][] { { 0, 1, 10 }, { 2, 3, 5 } }, 0, 3)); // 0
        System.out.println(mf.maxFlow(1, new int[][] {}, 0, 0)); // 0

        // Dinic's algorithm tests
        System.out.println(
                mf.maxFlowDinic(4, new int[][] { { 0, 1, 10 }, { 1, 2, 5 }, { 2, 3, 10 }, { 0, 2, 15 } }, 0, 3)); // 15
        System.out.println(mf.maxFlowDinic(4, new int[][] { { 0, 1, 10 }, { 2, 3, 5 } }, 0, 3)); // 0
        System.out.println(mf.maxFlowDinic(1, new int[][] {}, 0, 0)); // 0

        // Undirected graph (add both directions)
        int[][] undirected = { { 0, 1, 10 }, { 1, 0, 10 }, { 1, 2, 5 }, { 2, 1, 5 }, { 2, 3, 10 }, { 3, 2, 10 },
                { 0, 2, 15 }, { 2, 0, 15 } };
        System.out.println(mf.maxFlow(4, undirected, 0, 3)); // 15
        System.out.println(mf.maxFlowDinic(4, undirected, 0, 3)); // 15
    }
}
