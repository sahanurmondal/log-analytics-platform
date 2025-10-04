package tries.hard;

import java.util.*;

/**
 * LeetCode 588: Design In-Memory File System
 * https://leetcode.com/problems/design-in-memory-file-system/
 *
 * Description:
 * Design a data structure that simulates an in-memory file system.
 *
 * Constraints:
 * - 1 <= path.length, filePath.length <= 100
 * - path and filePath are absolute paths which begin with '/' and do not end
 * with '/' except that the path is just "/"
 * - You can assume that all directory names and file names only contain
 * lowercase letters, and the same names won't exist in the same directory
 * - The value of content is between 1 and 50
 *
 * Follow-up:
 * - Can you implement it using a trie-like structure?
 * - Can you optimize for memory usage?
 * - Can you add file permissions and timestamps?
 */
public class DesignInMemoryFileSystem {

    class FileNode {
        String name;
        boolean isFile;
        String content;
        Map<String, FileNode> children;
        long timestamp;
        String permissions;

        public FileNode(String name, boolean isFile) {
            this.name = name;
            this.isFile = isFile;
            this.content = "";
            this.children = new HashMap<>();
            this.timestamp = System.currentTimeMillis();
            this.permissions = "rwx";
        }
    }

    class FileSystem {
        private FileNode root;

        public FileSystem() {
            root = new FileNode("/", false);
        }

        public List<String> ls(String path) {
            FileNode node = getNode(path);
            if (node == null)
                return new ArrayList<>();

            if (node.isFile) {
                return Arrays.asList(node.name);
            }

            List<String> result = new ArrayList<>(node.children.keySet());
            Collections.sort(result);
            return result;
        }

        public void mkdir(String path) {
            String[] parts = path.split("/");
            FileNode current = root;

            for (int i = 1; i < parts.length; i++) {
                String dirName = parts[i];
                if (!current.children.containsKey(dirName)) {
                    current.children.put(dirName, new FileNode(dirName, false));
                }
                current = current.children.get(dirName);
            }
        }

        public void addContentToFile(String filePath, String content) {
            String[] parts = filePath.split("/");
            FileNode current = root;

            // Navigate to parent directory
            for (int i = 1; i < parts.length - 1; i++) {
                String dirName = parts[i];
                if (!current.children.containsKey(dirName)) {
                    current.children.put(dirName, new FileNode(dirName, false));
                }
                current = current.children.get(dirName);
            }

            // Handle file
            String fileName = parts[parts.length - 1];
            if (!current.children.containsKey(fileName)) {
                current.children.put(fileName, new FileNode(fileName, true));
            }

            FileNode file = current.children.get(fileName);
            file.isFile = true;
            file.content += content;
            file.timestamp = System.currentTimeMillis();
        }

        public String readContentFromFile(String filePath) {
            FileNode node = getNode(filePath);
            return (node != null && node.isFile) ? node.content : "";
        }

        private FileNode getNode(String path) {
            if (path.equals("/"))
                return root;

            String[] parts = path.split("/");
            FileNode current = root;

            for (int i = 1; i < parts.length; i++) {
                String name = parts[i];
                if (!current.children.containsKey(name)) {
                    return null;
                }
                current = current.children.get(name);
            }

            return current;
        }

        // Follow-up: Delete file or directory
        public boolean delete(String path) {
            if (path.equals("/"))
                return false;

            String[] parts = path.split("/");
            FileNode parent = root;

            // Navigate to parent
            for (int i = 1; i < parts.length - 1; i++) {
                parent = parent.children.get(parts[i]);
                if (parent == null)
                    return false;
            }

            String name = parts[parts.length - 1];
            return parent.children.remove(name) != null;
        }

        // Follow-up: Copy file or directory
        public boolean copy(String srcPath, String destPath) {
            FileNode srcNode = getNode(srcPath);
            if (srcNode == null)
                return false;

            String[] destParts = destPath.split("/");
            FileNode destParent = root;

            // Navigate to destination parent
            for (int i = 1; i < destParts.length - 1; i++) {
                String dirName = destParts[i];
                if (!destParent.children.containsKey(dirName)) {
                    destParent.children.put(dirName, new FileNode(dirName, false));
                }
                destParent = destParent.children.get(dirName);
            }

            String destName = destParts[destParts.length - 1];
            FileNode copy = deepCopy(srcNode);
            copy.name = destName;
            destParent.children.put(destName, copy);

            return true;
        }

        private FileNode deepCopy(FileNode node) {
            FileNode copy = new FileNode(node.name, node.isFile);
            copy.content = node.content;
            copy.permissions = node.permissions;

            for (Map.Entry<String, FileNode> entry : node.children.entrySet()) {
                copy.children.put(entry.getKey(), deepCopy(entry.getValue()));
            }

            return copy;
        }

        // Follow-up: Get file/directory info
        public Map<String, Object> getInfo(String path) {
            FileNode node = getNode(path);
            if (node == null)
                return null;

            Map<String, Object> info = new HashMap<>();
            info.put("name", node.name);
            info.put("isFile", node.isFile);
            info.put("timestamp", node.timestamp);
            info.put("permissions", node.permissions);

            if (node.isFile) {
                info.put("size", node.content.length());
            } else {
                info.put("childCount", node.children.size());
            }

            return info;
        }

        // Follow-up: Set permissions
        public void setPermissions(String path, String permissions) {
            FileNode node = getNode(path);
            if (node != null) {
                node.permissions = permissions;
            }
        }

        // Follow-up: Find files by pattern
        public List<String> find(String pattern) {
            List<String> result = new ArrayList<>();
            findRecursive(root, "/", pattern, result);
            return result;
        }

        private void findRecursive(FileNode node, String currentPath, String pattern, List<String> result) {
            if (node.name.contains(pattern)) {
                result.add(currentPath.equals("/") ? "/" + node.name : currentPath);
            }

            for (Map.Entry<String, FileNode> entry : node.children.entrySet()) {
                String childPath = currentPath.equals("/") ? "/" + entry.getKey() : currentPath + "/" + entry.getKey();
                findRecursive(entry.getValue(), childPath, pattern, result);
            }
        }
    }

    public static void main(String[] args) {
        DesignInMemoryFileSystem solution = new DesignInMemoryFileSystem();
        FileSystem fs = solution.new FileSystem();

        // Test basic operations
        System.out.println(fs.ls("/")); // []

        fs.mkdir("/a/b/c");
        fs.addContentToFile("/a/b/c/d", "hello");
        System.out.println(fs.ls("/")); // ["a"]
        System.out.println(fs.readContentFromFile("/a/b/c/d")); // "hello"

        fs.addContentToFile("/a/b/c/d", " world");
        System.out.println(fs.readContentFromFile("/a/b/c/d")); // "hello world"

        // Test additional features
        fs.addContentToFile("/a/b/test.txt", "test content");
        System.out.println("File info: " + fs.getInfo("/a/b/test.txt"));

        fs.copy("/a/b", "/backup");
        System.out.println("Backup contents: " + fs.ls("/backup"));

        System.out.println("Find 'test': " + fs.find("test"));

        fs.setPermissions("/a/b/test.txt", "r--");
        System.out.println("Updated permissions: " + fs.getInfo("/a/b/test.txt"));

        fs.delete("/a/b/test.txt");
        System.out.println("After delete: " + fs.ls("/a/b"));
    }
}
