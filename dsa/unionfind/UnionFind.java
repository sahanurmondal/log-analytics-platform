package unionfind;

public class UnionFind {
    private int[] parent;
    private int[] rank;
    private int components;

    // Constructor for static problems (all nodes exist from start)
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        components = n;
    }

    // Constructor for dynamic problems (nodes added incrementally)
    public UnionFind(int n, boolean dynamic) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = -1; // Mark as not yet active
        }
        components = 0;
    }

    public int find(int x) {
        if (parent[x] < 0) return -1; // Not yet active
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX < 0 || rootY < 0 || rootX == rootY)
            return false;

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

    // Add/activate a component (for dynamic problems)
    public void addComponent(int x) {
        if (parent[x] < 0) { // Not yet active
            parent[x] = x;
            rank[x] = 0;
            components++;
        }
    }

    // Check if a node is active (for dynamic problems)
    public boolean isActive(int x) {
        return parent[x] >= 0;
    }

    public void setComponents(int components) {
        this.components = components;
    }

    public int getComponents() {
        return components;
    }

    public boolean connected(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        return rootX >= 0 && rootY >= 0 && rootX == rootY;
    }
}
