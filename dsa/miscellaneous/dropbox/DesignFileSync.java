package miscellaneous.dropbox;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Question: Design Dropbox File Synchronization System
 * 
 * Description:
 * Design a file synchronization system that supports:
 * - Real-time file sync across devices
 * - Conflict resolution
 * - Version control
 * - Bandwidth optimization
 * - Offline support
 * 
 * Company: Dropbox
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignFileSync {

    enum SyncStatus {
        SYNCED, PENDING, CONFLICT, ERROR
    }

    class FileMetadata {
        String fileId;
        String fileName;
        String filePath;
        long size;
        String hash;
        long lastModified;
        String deviceId;
        int version;
        SyncStatus status;

        FileMetadata(String fileId, String fileName, String filePath, long size, String hash, String deviceId) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.filePath = filePath;
            this.size = size;
            this.hash = hash;
            this.deviceId = deviceId;
            this.lastModified = System.currentTimeMillis();
            this.version = 1;
            this.status = SyncStatus.PENDING;
        }
    }

    class Device {
        String deviceId;
        String deviceName;
        String userId;
        Map<String, FileMetadata> localFiles;
        boolean isOnline;

        Device(String deviceId, String deviceName, String userId) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.userId = userId;
            this.localFiles = new HashMap<>();
            this.isOnline = true;
        }
    }

    class SyncEngine {
        public void syncFile(String fileId, String deviceId) {
            FileMetadata localFile = getLocalFile(deviceId, fileId);
            FileMetadata remoteFile = getRemoteFile(fileId);

            if (localFile == null && remoteFile == null) {
                return; // File doesn't exist
            }

            if (localFile == null) {
                // Download file to device
                downloadFile(deviceId, remoteFile);
            } else if (remoteFile == null) {
                // Upload file to remote
                uploadFile(localFile);
            } else {
                // Both exist, check for conflicts
                if (localFile.hash.equals(remoteFile.hash)) {
                    localFile.status = SyncStatus.SYNCED;
                } else if (localFile.lastModified > remoteFile.lastModified) {
                    // Local is newer, upload
                    uploadFile(localFile);
                } else if (localFile.lastModified < remoteFile.lastModified) {
                    // Remote is newer, download
                    downloadFile(deviceId, remoteFile);
                } else {
                    // Conflict - same timestamp, different content
                    handleConflict(localFile, remoteFile);
                }
            }
        }

        private void handleConflict(FileMetadata localFile, FileMetadata remoteFile) {
            // Create conflicted copy
            String conflictedName = localFile.fileName + " (conflicted copy " +
                    new Date(localFile.lastModified) + ")";

            FileMetadata conflictedFile = new FileMetadata(
                    UUID.randomUUID().toString(),
                    conflictedName,
                    localFile.filePath,
                    localFile.size,
                    localFile.hash,
                    localFile.deviceId);

            remoteFiles.put(conflictedFile.fileId, conflictedFile);

            // Download remote version
            downloadFile(localFile.deviceId, remoteFile);
        }

        private void uploadFile(FileMetadata file) {
            file.version++;
            file.status = SyncStatus.SYNCED;
            remoteFiles.put(file.fileId, file);

            // Notify other devices
            notifyOtherDevices(file);
        }

        private void downloadFile(String deviceId, FileMetadata file) {
            Device device = devices.get(deviceId);
            if (device != null) {
                device.localFiles.put(file.fileId, file);
                file.status = SyncStatus.SYNCED;
            }
        }

        private void notifyOtherDevices(FileMetadata file) {
            for (Device device : devices.values()) {
                if (!device.deviceId.equals(file.deviceId) && device.isOnline) {
                    syncFile(file.fileId, device.deviceId);
                }
            }
        }
    }

    class DeltaSync {
        public void syncDelta(String fileId, String deviceId, List<FileDelta> deltas) {
            FileMetadata file = getLocalFile(deviceId, fileId);
            if (file == null)
                return;

            // Apply deltas
            for (FileDelta delta : deltas) {
                applyDelta(file, delta);
            }

            file.version++;
            file.lastModified = System.currentTimeMillis();
            file.status = SyncStatus.PENDING;

            // Sync with remote
            syncEngine.syncFile(fileId, deviceId);
        }

        private void applyDelta(FileMetadata file, FileDelta delta) {
            // Apply delta changes to file
            file.hash = calculateNewHash(file.hash, delta);
            file.size += delta.sizeChange;
        }

        private String calculateNewHash(String currentHash, FileDelta delta) {
            // Simplified hash calculation
            return currentHash + delta.toString().hashCode();
        }
    }

    class FileDelta {
        long offset;
        byte[] data;
        int length;
        long sizeChange;

        FileDelta(long offset, byte[] data, int length, long sizeChange) {
            this.offset = offset;
            this.data = data;
            this.length = length;
            this.sizeChange = sizeChange;
        }

        @Override
        public String toString() {
            return "FileDelta{offset=" + offset + ", length=" + length + ", sizeChange=" + sizeChange + "}";
        }
    }

    private Map<String, Device> devices = new HashMap<>();
    private Map<String, FileMetadata> remoteFiles = new HashMap<>();
    private SyncEngine syncEngine = new SyncEngine();
    private DeltaSync deltaSync = new DeltaSync();

    public void addDevice(String deviceId, String deviceName, String userId) {
        devices.put(deviceId, new Device(deviceId, deviceName, userId));
    }

    public void addFile(String deviceId, String fileName, String filePath, long size, String hash) {
        Device device = devices.get(deviceId);
        if (device != null) {
            String fileId = UUID.randomUUID().toString();
            FileMetadata file = new FileMetadata(fileId, fileName, filePath, size, hash, deviceId);
            device.localFiles.put(fileId, file);

            // Trigger sync
            syncEngine.syncFile(fileId, deviceId);
        }
    }

    public void syncAllFiles(String deviceId) {
        Device device = devices.get(deviceId);
        if (device != null) {
            for (String fileId : device.localFiles.keySet()) {
                syncEngine.syncFile(fileId, deviceId);
            }
        }
    }

    private FileMetadata getLocalFile(String deviceId, String fileId) {
        Device device = devices.get(deviceId);
        return device != null ? device.localFiles.get(fileId) : null;
    }

    private FileMetadata getRemoteFile(String fileId) {
        return remoteFiles.get(fileId);
    }

    public static void main(String[] args) {
        DesignFileSync dropbox = new DesignFileSync();

        // Add devices
        dropbox.addDevice("device1", "MacBook Pro", "user1");
        dropbox.addDevice("device2", "iPhone", "user1");

        // Add file to device1
        dropbox.addFile("device1", "document.txt", "/Documents/document.txt", 1024, "hash123");

        // Sync all files
        dropbox.syncAllFiles("device1");
        dropbox.syncAllFiles("device2");

        System.out.println("File sync completed");
    }
}
