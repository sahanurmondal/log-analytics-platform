package unionfind;

import java.util.Arrays;

public class UnionFind2D {
    int[] parent;
    int[] rank;
    int[] size;
    int rows, cols;
    int componentCount;

    public UnionFind2D(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        int n = rows * cols;
        parent = new int[n];
        rank = new int[n];
        size = new int[n];
        componentCount = 0;
        for (int i = 0; i < n; i++) {
            parent[i] = -1; // Mark as not yet active
            size[i] = 1;
        }
    }

    // Convert 2D coordinates to 1D index
    int getIndex(int r, int c) {
        return r * cols + c;
    }

    // Activate a cell (for dynamic grid problems)
    public void activate(int r, int c) {
        int idx = getIndex(r, c);
        if (parent[idx] < 0) { // Not yet active
            parent[idx] = idx;
//            rank[idx] = 0;
            componentCount++;
        }
    }

    public boolean isActive(int r, int c) {
        return parent[getIndex(r, c)] >= 0;
    }

    int find(int x) {
        if (parent[x] < 0) return -1; // Not yet active
        if (parent[x] != x)
            parent[x] = find(parent[x]);
        return parent[x];
    }

    // Union only if both cells are active
    public void union(int r1, int c1, int r2, int c2) {
        int idx1 = getIndex(r1, c1);
        int idx2 = getIndex(r2, c2);
        if (parent[idx1] < 0 || parent[idx2] < 0) return; // Check if inactive
        union(idx1, idx2);
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX < 0 || rootY < 0 || rootX == rootY) return;

        // Union by rank, update size
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
            size[rootX] += size[rootY];
        }
        componentCount--;
    }

    public boolean connected(int r1, int c1, int r2, int c2) {
        int idx1 = getIndex(r1, c1);
        int idx2 = getIndex(r2, c2);
        if (parent[idx1] < 0 || parent[idx2] < 0) return false; // Check if inactive
        return find(idx1) == find(idx2);
    }

    public int getSize(int r, int c) {
        int idx = getIndex(r, c);
        if (parent[idx] < 0) return 0; // Check if inactive
        return size[find(idx)];
    }

    public int getComponentCount() {
        return componentCount;
    }

    // Optional: reset all cells (for dynamic problems)
    void reset() {
        Arrays.fill(rank, 0);
        Arrays.fill(size, 1);
        for (int i = 0; i < parent.length; i++) parent[i] = -1; // Mark as inactive
        componentCount = 0;
    }
}
