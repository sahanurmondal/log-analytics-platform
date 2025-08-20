package design.hard;

import java.util.*;

/**
 * LeetCode 588: Design In-Memory File System
 * https://leetcode.com/problems/design-in-memory-file-system/
 *
 * Description: Design a data structure that simulates an in-memory file system.
 * 
 * Constraints:
 * - 1 <= path.length, filePath.length <= 100
 * - path and filePath are absolute paths which begin with '/' and do not end
 * with '/'
 * - You can assume that all directory names and file names only contain
 * lowercase letters
 * - At most 300 calls will be made to ls, mkdir, addContentToFile, and
 * readContentFromFile
 *
 * Follow-up:
 * - Can you implement it efficiently?
 * 
 * Time Complexity: O(m + n + klogk) for ls, O(m) for others where m is path
 * length
 * Space Complexity: O(total content + total paths)
 * 
 * Company Tags: Google, Amazon
 */
public class FileSystem {

    class FileNode {
        boolean isFile;
        Map<String, FileNode> children;
        StringBuilder content;

        FileNode() {
            isFile = false;
            children = new HashMap<>();
            content = new StringBuilder();
        }
    }

    private FileNode root;

    public FileSystem() {
        root = new FileNode();
    }

    public List<String> ls(String path) {
        FileNode node = traverseToNode(path);
        List<String> result = new ArrayList<>();

        if (node.isFile) {
            String[] parts = path.split("/");
            result.add(parts[parts.length - 1]);
        } else {
            result.addAll(node.children.keySet());
            Collections.sort(result);
        }

        return result;
    }

    public void mkdir(String path) {
        traverseToNode(path);
    }

    public void addContentToFile(String filePath, String content) {
        FileNode node = traverseToNode(filePath);
        node.isFile = true;
        node.content.append(content);
    }

    public String readContentFromFile(String filePath) {
        FileNode node = traverseToNode(filePath);
        return node.content.toString();
    }

    private FileNode traverseToNode(String path) {
        String[] parts = path.split("/");
        FileNode current = root;

        for (String part : parts) {
            if (part.isEmpty())
                continue;

            current.children.putIfAbsent(part, new FileNode());
            current = current.children.get(part);
        }

        return current;
    }

    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        System.out.println(fs.ls("/")); // Expected: []
        fs.mkdir("/a/b/c");
        fs.addContentToFile("/a/b/c/d", "hello");
        System.out.println(fs.ls("/")); // Expected: ["a"]
        System.out.println(fs.readContentFromFile("/a/b/c/d")); // Expected: "hello"
    }
}
