package miscellaneous.microsoft;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design a Distributed File System
 * 
 * Description:
 * Design a file system that supports:
 * - File upload/download
 * - Directory operations (create, delete, list)
 * - File versioning
 * - Permissions (read, write, execute)
 * - File sharing with expiration
 * 
 * Company: Microsoft
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignFileSystem {

    enum Permission {
        READ, WRITE, EXECUTE
    }

    enum FileType {
        FILE, DIRECTORY
    }

    class FileNode {
        String name;
        String path;
        FileType type;
        String content;
        long size;
        long createdTime;
        long modifiedTime;
        String ownerId;
        Set<Permission> permissions;
        Map<String, Set<Permission>> userPermissions;
        List<FileVersion> versions;

        FileNode(String name, String path, FileType type, String ownerId) {
            this.name = name;
            this.path = path;
            this.type = type;
            this.ownerId = ownerId;
            this.createdTime = System.currentTimeMillis();
            this.modifiedTime = this.createdTime;
            this.permissions = EnumSet.allOf(Permission.class);
            this.userPermissions = new HashMap<>();
            this.versions = new ArrayList<>();
        }
    }

    class FileVersion {
        int version;
        String content;
        long timestamp;
        String userId;

        FileVersion(int version, String content, String userId) {
            this.version = version;
            this.content = content;
            this.userId = userId;
            this.timestamp = System.currentTimeMillis();
        }
    }

    class ShareLink {
        String linkId;
        String filePath;
        long expirationTime;
        Set<Permission> permissions;

        ShareLink(String linkId, String filePath, long expirationTime, Set<Permission> permissions) {
            this.linkId = linkId;
            this.filePath = filePath;
            this.expirationTime = expirationTime;
            this.permissions = permissions;
        }
    }

    private Map<String, FileNode> fileSystem = new HashMap<>();
    private Map<String, ShareLink> shareLinks = new HashMap<>();

    public boolean createFile(String path, String content, String userId) {
        if (fileSystem.containsKey(path)) {
            return false;
        }

        String parentPath = getParentPath(path);
        if (!parentPath.equals("/") && !fileSystem.containsKey(parentPath)) {
            return false;
        }

        FileNode file = new FileNode(getFileName(path), path, FileType.FILE, userId);
        file.content = content;
        file.size = content.length();
        file.versions.add(new FileVersion(1, content, userId));

        fileSystem.put(path, file);
        return true;
    }

    public boolean createDirectory(String path, String userId) {
        if (fileSystem.containsKey(path)) {
            return false;
        }

        String parentPath = getParentPath(path);
        if (!parentPath.equals("/") && !fileSystem.containsKey(parentPath)) {
            return false;
        }

        FileNode directory = new FileNode(getFileName(path), path, FileType.DIRECTORY, userId);
        fileSystem.put(path, directory);
        return true;
    }

    public String readFile(String path, String userId) {
        FileNode file = fileSystem.get(path);
        if (file == null || file.type != FileType.FILE) {
            return null;
        }

        if (!hasPermission(file, userId, Permission.READ)) {
            return null;
        }

        return file.content;
    }

    public boolean writeFile(String path, String content, String userId) {
        FileNode file = fileSystem.get(path);
        if (file == null || file.type != FileType.FILE) {
            return false;
        }

        if (!hasPermission(file, userId, Permission.WRITE)) {
            return false;
        }

        file.content = content;
        file.size = content.length();
        file.modifiedTime = System.currentTimeMillis();

        int nextVersion = file.versions.size() + 1;
        file.versions.add(new FileVersion(nextVersion, content, userId));

        return true;
    }

    public List<String> listDirectory(String path, String userId) {
        FileNode directory = fileSystem.get(path);
        if (directory == null || directory.type != FileType.DIRECTORY) {
            return new ArrayList<>();
        }

        if (!hasPermission(directory, userId, Permission.READ)) {
            return new ArrayList<>();
        }

        List<String> files = new ArrayList<>();
        for (String filePath : fileSystem.keySet()) {
            if (filePath.startsWith(path + "/") && !filePath.substring(path.length() + 1).contains("/")) {
                files.add(getFileName(filePath));
            }
        }

        return files;
    }

    public String createShareLink(String path, String userId, long expirationMinutes, Set<Permission> permissions) {
        FileNode file = fileSystem.get(path);
        if (file == null || !hasPermission(file, userId, Permission.READ)) {
            return null;
        }

        String linkId = UUID.randomUUID().toString();
        long expirationTime = System.currentTimeMillis() + expirationMinutes * 60 * 1000;

        ShareLink shareLink = new ShareLink(linkId, path, expirationTime, permissions);
        shareLinks.put(linkId, shareLink);

        return linkId;
    }

    public String accessSharedFile(String linkId) {
        ShareLink shareLink = shareLinks.get(linkId);
        if (shareLink == null || shareLink.expirationTime < System.currentTimeMillis()) {
            return null;
        }

        FileNode file = fileSystem.get(shareLink.filePath);
        if (file == null || file.type != FileType.FILE) {
            return null;
        }

        if (!shareLink.permissions.contains(Permission.READ)) {
            return null;
        }

        return file.content;
    }

    public List<FileVersion> getFileVersions(String path, String userId) {
        FileNode file = fileSystem.get(path);
        if (file == null || file.type != FileType.FILE) {
            return new ArrayList<>();
        }

        if (!hasPermission(file, userId, Permission.READ)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(file.versions);
    }

    private boolean hasPermission(FileNode file, String userId, Permission permission) {
        if (file.ownerId.equals(userId)) {
            return true;
        }

        Set<Permission> userPerms = file.userPermissions.get(userId);
        return userPerms != null && userPerms.contains(permission);
    }

    private String getParentPath(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash == 0 ? "/" : path.substring(0, lastSlash);
    }

    private String getFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return path.substring(lastSlash + 1);
    }

    public static void main(String[] args) {
        DesignFileSystem fs = new DesignFileSystem();

        // Create root directory
        fs.createDirectory("/", "system");

        // Create file
        fs.createFile("/test.txt", "Hello World", "user1");

        // Read file
        String content = fs.readFile("/test.txt", "user1");
        System.out.println("File content: " + content);

        // Create share link
        String linkId = fs.createShareLink("/test.txt", "user1", 60, Set.of(Permission.READ));
        System.out.println("Share link created: " + linkId);
    }
}
