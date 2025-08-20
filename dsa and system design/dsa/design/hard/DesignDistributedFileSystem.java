package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Distributed File System
 *
 * Description: Design a distributed file system that supports:
 * - File storage across multiple nodes
 * - Replication and fault tolerance
 * - Metadata management and indexing
 * - Hierarchical namespace
 * 
 * Constraints:
 * - Support large files with chunking
 * - Handle node failures gracefully
 * - Maintain consistency across replicas
 *
 * Follow-up:
 * - How to handle concurrent writes?
 * - File versioning and snapshots?
 * 
 * Time Complexity: O(log n) for file operations
 * Space Complexity: O(files * replication_factor)
 * 
 * Company Tags: Google File System, HDFS, Amazon S3
 */
public class DesignDistributedFileSystem {

    enum NodeType {
        MASTER, CHUNK_SERVER
    }

    enum FileOperation {
        CREATE, READ, WRITE, DELETE, APPEND
    }

    class FileChunk {
        String chunkId;
        String fileId;
        int chunkIndex;
        byte[] data;
        Set<String> replicaNodes;
        long version;
        String checksum;

        FileChunk(String chunkId, String fileId, int chunkIndex, byte[] data) {
            this.chunkId = chunkId;
            this.fileId = fileId;
            this.chunkIndex = chunkIndex;
            this.data = data != null ? Arrays.copyOf(data, data.length) : new byte[0];
            this.replicaNodes = new HashSet<>();
            this.version = 1;
            this.checksum = calculateChecksum(this.data);
        }

        private String calculateChecksum(byte[] data) {
            return Integer.toHexString(Arrays.hashCode(data));
        }

        boolean isValid() {
            return checksum.equals(calculateChecksum(data));
        }
    }

    class FileMetadata {
        String fileId;
        String fileName;
        String parentDir;
        long fileSize;
        long createdTime;
        long modifiedTime;
        List<String> chunkIds;
        Map<String, String> attributes;
        boolean isDirectory;

        FileMetadata(String fileId, String fileName, String parentDir, boolean isDirectory) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.parentDir = parentDir;
            this.isDirectory = isDirectory;
            this.fileSize = 0;
            this.createdTime = System.currentTimeMillis();
            this.modifiedTime = createdTime;
            this.chunkIds = new ArrayList<>();
            this.attributes = new HashMap<>();
        }
    }

    class MasterNode {
        String nodeId;
        Map<String, FileMetadata> fileIndex;
        Map<String, String> pathToFileId;
        Map<String, FileChunk> chunkIndex;
        Map<String, Set<String>> chunkToNodes;
        Set<String> availableChunkServers;

        MasterNode(String nodeId) {
            this.nodeId = nodeId;
            this.fileIndex = new ConcurrentHashMap<>();
            this.pathToFileId = new ConcurrentHashMap<>();
            this.chunkIndex = new ConcurrentHashMap<>();
            this.chunkToNodes = new ConcurrentHashMap<>();
            this.availableChunkServers = ConcurrentHashMap.newKeySet();
        }

        String createFile(String filePath, boolean isDirectory) {
            String[] pathParts = filePath.split("/");
            String fileName = pathParts[pathParts.length - 1];
            String parentDir = filePath.substring(0, Math.max(0, filePath.lastIndexOf('/')));

            if (pathToFileId.containsKey(filePath)) {
                return null; // File already exists
            }

            String fileId = UUID.randomUUID().toString();
            FileMetadata metadata = new FileMetadata(fileId, fileName, parentDir, isDirectory);

            fileIndex.put(fileId, metadata);
            pathToFileId.put(filePath, fileId);

            return fileId;
        }

        boolean deleteFile(String filePath) {
            String fileId = pathToFileId.remove(filePath);
            if (fileId == null)
                return false;

            FileMetadata metadata = fileIndex.remove(fileId);
            if (metadata != null) {
                // Remove all chunks
                for (String chunkId : metadata.chunkIds) {
                    chunkIndex.remove(chunkId);
                    chunkToNodes.remove(chunkId);
                }
                return true;
            }
            return false;
        }

        List<String> allocateChunks(String fileId, int numChunks) {
            List<String> chunkIds = new ArrayList<>();
            List<String> serverList = new ArrayList<>(availableChunkServers);

            for (int i = 0; i < numChunks; i++) {
                String chunkId = UUID.randomUUID().toString();
                chunkIds.add(chunkId);

                // Assign to replica nodes
                Set<String> replicaNodes = new HashSet<>();
                int replicationFactor = Math.min(3, serverList.size());

                for (int r = 0; r < replicationFactor; r++) {
                    String nodeId = serverList.get((i * replicationFactor + r) % serverList.size());
                    replicaNodes.add(nodeId);
                }

                chunkToNodes.put(chunkId, replicaNodes);
            }

            return chunkIds;
        }

        FileMetadata getFileMetadata(String filePath) {
            String fileId = pathToFileId.get(filePath);
            return fileId != null ? fileIndex.get(fileId) : null;
        }
    }

    class ChunkServer {
        String nodeId;
        Map<String, FileChunk> localChunks;
        long availableSpace;
        long totalSpace;

        ChunkServer(String nodeId, long totalSpace) {
            this.nodeId = nodeId;
            this.localChunks = new ConcurrentHashMap<>();
            this.totalSpace = totalSpace;
            this.availableSpace = totalSpace;
        }

        boolean storeChunk(FileChunk chunk) {
            if (availableSpace < chunk.data.length) {
                return false;
            }

            localChunks.put(chunk.chunkId, chunk);
            availableSpace -= chunk.data.length;
            return true;
        }

        FileChunk readChunk(String chunkId) {
            return localChunks.get(chunkId);
        }

        boolean deleteChunk(String chunkId) {
            FileChunk chunk = localChunks.remove(chunkId);
            if (chunk != null) {
                availableSpace += chunk.data.length;
                return true;
            }
            return false;
        }

        double getUsageRatio() {
            return (double) (totalSpace - availableSpace) / totalSpace;
        }
    }

    private MasterNode masterNode;
    private Map<String, ChunkServer> chunkServers;
    private final int CHUNK_SIZE = 64 * 1024; // 64KB chunks
    private final int REPLICATION_FACTOR = 3;

    public DesignDistributedFileSystem(int numChunkServers, long chunkServerCapacity) {
        masterNode = new MasterNode("master-node");
        chunkServers = new ConcurrentHashMap<>();

        // Initialize chunk servers
        for (int i = 0; i < numChunkServers; i++) {
            String nodeId = "chunk-server-" + i;
            ChunkServer server = new ChunkServer(nodeId, chunkServerCapacity);
            chunkServers.put(nodeId, server);
            masterNode.availableChunkServers.add(nodeId);
        }
    }

    public boolean createFile(String filePath) {
        return masterNode.createFile(filePath, false) != null;
    }

    public boolean createDirectory(String dirPath) {
        return masterNode.createFile(dirPath, true) != null;
    }

    public boolean writeFile(String filePath, byte[] data) {
        FileMetadata metadata = masterNode.getFileMetadata(filePath);
        if (metadata == null) {
            // Create file if it doesn't exist
            String fileId = masterNode.createFile(filePath, false);
            if (fileId == null)
                return false;
            metadata = masterNode.fileIndex.get(fileId);
        }

        if (metadata.isDirectory)
            return false;

        // Split data into chunks
        List<byte[]> chunks = splitIntoChunks(data);
        List<String> chunkIds = masterNode.allocateChunks(metadata.fileId, chunks.size());

        // Store chunks on chunk servers
        for (int i = 0; i < chunks.size(); i++) {
            String chunkId = chunkIds.get(i);
            byte[] chunkData = chunks.get(i);

            FileChunk chunk = new FileChunk(chunkId, metadata.fileId, i, chunkData);
            Set<String> replicaNodes = masterNode.chunkToNodes.get(chunkId);

            // Store on replica nodes
            for (String nodeId : replicaNodes) {
                ChunkServer server = chunkServers.get(nodeId);
                if (server != null) {
                    server.storeChunk(chunk);
                    chunk.replicaNodes.add(nodeId);
                }
            }

            masterNode.chunkIndex.put(chunkId, chunk);
        }

        // Update metadata
        metadata.chunkIds = chunkIds;
        metadata.fileSize = data.length;
        metadata.modifiedTime = System.currentTimeMillis();

        return true;
    }

    public byte[] readFile(String filePath) {
        FileMetadata metadata = masterNode.getFileMetadata(filePath);
        if (metadata == null || metadata.isDirectory) {
            return null;
        }

        List<byte[]> chunkDataList = new ArrayList<>();
        int totalSize = 0;

        // Read all chunks
        for (String chunkId : metadata.chunkIds) {
            FileChunk chunkInfo = masterNode.chunkIndex.get(chunkId);
            if (chunkInfo == null)
                continue;

            // Try to read from any replica
            byte[] chunkData = null;
            for (String nodeId : chunkInfo.replicaNodes) {
                ChunkServer server = chunkServers.get(nodeId);
                if (server != null) {
                    FileChunk chunk = server.readChunk(chunkId);
                    if (chunk != null && chunk.isValid()) {
                        chunkData = chunk.data;
                        break;
                    }
                }
            }

            if (chunkData != null) {
                chunkDataList.add(chunkData);
                totalSize += chunkData.length;
            }
        }

        // Combine chunks
        byte[] result = new byte[totalSize];
        int offset = 0;
        for (byte[] chunkData : chunkDataList) {
            System.arraycopy(chunkData, 0, result, offset, chunkData.length);
            offset += chunkData.length;
        }

        return result;
    }

    public boolean deleteFile(String filePath) {
        FileMetadata metadata = masterNode.getFileMetadata(filePath);
        if (metadata == null)
            return false;

        // Delete chunks from chunk servers
        for (String chunkId : metadata.chunkIds) {
            Set<String> replicaNodes = masterNode.chunkToNodes.get(chunkId);
            if (replicaNodes != null) {
                for (String nodeId : replicaNodes) {
                    ChunkServer server = chunkServers.get(nodeId);
                    if (server != null) {
                        server.deleteChunk(chunkId);
                    }
                }
            }
        }

        return masterNode.deleteFile(filePath);
    }

    public List<String> listDirectory(String dirPath) {
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : masterNode.pathToFileId.entrySet()) {
            String path = entry.getKey();
            if (path.startsWith(dirPath) && !path.equals(dirPath)) {
                String relativePath = path.substring(dirPath.length());
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }

                // Only direct children
                if (!relativePath.contains("/")) {
                    result.add(relativePath);
                }
            }
        }

        return result;
    }

    private List<byte[]> splitIntoChunks(byte[] data) {
        List<byte[]> chunks = new ArrayList<>();
        int offset = 0;

        while (offset < data.length) {
            int chunkSize = Math.min(CHUNK_SIZE, data.length - offset);
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(data, offset, chunk, 0, chunkSize);
            chunks.add(chunk);
            offset += chunkSize;
        }

        return chunks;
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalFiles", masterNode.fileIndex.size());
        stats.put("totalChunks", masterNode.chunkIndex.size());
        stats.put("totalChunkServers", chunkServers.size());
        stats.put("replicationFactor", REPLICATION_FACTOR);
        stats.put("chunkSize", CHUNK_SIZE);

        long totalCapacity = chunkServers.values().stream()
                .mapToLong(server -> server.totalSpace)
                .sum();

        long usedCapacity = chunkServers.values().stream()
                .mapToLong(server -> server.totalSpace - server.availableSpace)
                .sum();

        stats.put("totalCapacity", totalCapacity);
        stats.put("usedCapacity", usedCapacity);
        stats.put("utilizationPercentage", (double) usedCapacity / totalCapacity * 100);

        return stats;
    }

    public static void main(String[] args) {
        DesignDistributedFileSystem dfs = new DesignDistributedFileSystem(5, 1024 * 1024); // 1MB per server

        System.out.println("Initial stats: " + dfs.getSystemStats());

        // Create directories
        dfs.createDirectory("/home");
        dfs.createDirectory("/home/user");

        // Create and write files
        String testData = "Hello, Distributed File System! This is a test file with some content.";
        dfs.writeFile("/home/user/test.txt", testData.getBytes());

        String largeData = "Large file content: " + "x".repeat(1000);
        dfs.writeFile("/home/user/large.txt", largeData.getBytes());

        // Read files
        byte[] readData = dfs.readFile("/home/user/test.txt");
        System.out.println("Read data: " + new String(readData));

        // List directory
        System.out.println("Files in /home/user: " + dfs.listDirectory("/home/user"));

        // Delete file
        boolean deleted = dfs.deleteFile("/home/user/test.txt");
        System.out.println("File deleted: " + deleted);

        System.out.println("Files after deletion: " + dfs.listDirectory("/home/user"));

        System.out.println("Final stats: " + dfs.getSystemStats());
    }
}
