package design.hard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Design Operating System Components
 *
 * Description: Design core OS components:
 * - Process scheduler and management
 * - Memory management with paging
 * - File system operations
 * - Inter-process communication
 * 
 * Constraints:
 * - Handle concurrent processes
 * - Manage limited resources
 * - Ensure fairness and efficiency
 *
 * Follow-up:
 * - How to handle deadlocks?
 * - Virtual memory implementation?
 * 
 * Time Complexity: O(log n) for scheduling, O(1) for memory access
 * Space Complexity: O(processes + memory_pages)
 * 
 * Company Tags: Microsoft, Apple, Linux Foundation
 */
public class DesignOperatingSystem {

    enum ProcessState {
        NEW, READY, RUNNING, WAITING, TERMINATED
    }

    enum SchedulingAlgorithm {
        FCFS, SJF, ROUND_ROBIN, PRIORITY
    }

    class Process {
        int pid;
        String name;
        ProcessState state;
        int priority;
        int burstTime;
        int remainingTime;
        int arrivalTime;
        int waitingTime;
        int turnaroundTime;
        Map<String, Object> memory;
        Set<Integer> allocatedPages;

        Process(int pid, String name, int priority, int burstTime) {
            this.pid = pid;
            this.name = name;
            this.priority = priority;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
            this.state = ProcessState.NEW;
            this.arrivalTime = (int) System.currentTimeMillis();
            this.memory = new HashMap<>();
            this.allocatedPages = new HashSet<>();
        }

        void execute(int timeSlice) {
            state = ProcessState.RUNNING;
            int executionTime = Math.min(timeSlice, remainingTime);

            try {
                Thread.sleep(executionTime * 10); // Simulate execution
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            remainingTime -= executionTime;

            if (remainingTime <= 0) {
                state = ProcessState.TERMINATED;
            } else {
                state = ProcessState.READY;
            }
        }

        @Override
        public String toString() {
            return String.format("Process[%d:%s] State:%s Priority:%d Remaining:%d",
                    pid, name, state, priority, remainingTime);
        }
    }

    class ProcessScheduler {
        private Queue<Process> readyQueue;
        private Process currentProcess;
        private SchedulingAlgorithm algorithm;
        private int timeQuantum;
        private int currentTime;

        ProcessScheduler(SchedulingAlgorithm algorithm, int timeQuantum) {
            this.algorithm = algorithm;
            this.timeQuantum = timeQuantum;
            this.currentTime = 0;
            initializeQueue();
        }

        private void initializeQueue() {
            switch (algorithm) {
                case FCFS:
                    readyQueue = new LinkedList<>();
                    break;
                case SJF:
                    readyQueue = new PriorityQueue<>((a, b) -> a.remainingTime - b.remainingTime);
                    break;
                case PRIORITY:
                    readyQueue = new PriorityQueue<>((a, b) -> b.priority - a.priority);
                    break;
                case ROUND_ROBIN:
                    readyQueue = new LinkedList<>();
                    break;
                default:
                    readyQueue = new LinkedList<>();
            }
        }

        void addProcess(Process process) {
            process.state = ProcessState.READY;
            readyQueue.offer(process);
        }

        Process getNextProcess() {
            return readyQueue.poll();
        }

        void executeNextProcess() {
            if (currentProcess == null) {
                currentProcess = getNextProcess();
            }

            if (currentProcess != null) {
                int executeTime = algorithm == SchedulingAlgorithm.ROUND_ROBIN ? timeQuantum
                        : currentProcess.remainingTime;

                currentProcess.execute(executeTime);
                currentTime += executeTime;

                if (currentProcess.state == ProcessState.TERMINATED) {
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    currentProcess = null;
                } else if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
                    // Put back in queue for round-robin
                    readyQueue.offer(currentProcess);
                    currentProcess = null;
                }
            }
        }

        boolean hasProcesses() {
            return currentProcess != null || !readyQueue.isEmpty();
        }

        Map<String, Object> getStats() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("algorithm", algorithm);
            stats.put("timeQuantum", timeQuantum);
            stats.put("currentTime", currentTime);
            stats.put("readyQueueSize", readyQueue.size());
            stats.put("currentProcess", currentProcess != null ? currentProcess.name : "None");
            return stats;
        }
    }

    class MemoryManager {
        private int totalPages;
        private boolean[] pageTable;
        private Map<Integer, Integer> processPages; // pid -> allocated pages count
        private Queue<Integer> freePages;

        MemoryManager(int totalPages) {
            this.totalPages = totalPages;
            this.pageTable = new boolean[totalPages];
            this.processPages = new HashMap<>();
            this.freePages = new LinkedList<>();

            // Initialize free pages
            for (int i = 0; i < totalPages; i++) {
                freePages.offer(i);
            }
        }

        Set<Integer> allocatePages(int pid, int numPages) {
            if (freePages.size() < numPages) {
                return null; // Not enough free pages
            }

            Set<Integer> allocatedPages = new HashSet<>();
            for (int i = 0; i < numPages && !freePages.isEmpty(); i++) {
                int pageNum = freePages.poll();
                pageTable[pageNum] = true;
                allocatedPages.add(pageNum);
            }

            processPages.put(pid, processPages.getOrDefault(pid, 0) + numPages);
            return allocatedPages;
        }

        void deallocatePages(int pid, Set<Integer> pages) {
            for (int pageNum : pages) {
                if (pageNum < totalPages && pageTable[pageNum]) {
                    pageTable[pageNum] = false;
                    freePages.offer(pageNum);
                }
            }

            int currentPages = processPages.getOrDefault(pid, 0);
            processPages.put(pid, Math.max(0, currentPages - pages.size()));
        }

        int getFreePages() {
            return freePages.size();
        }

        double getMemoryUtilization() {
            return (double) (totalPages - freePages.size()) / totalPages * 100;
        }

        Map<String, Object> getStats() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPages", totalPages);
            stats.put("freePages", freePages.size());
            stats.put("usedPages", totalPages - freePages.size());
            stats.put("utilization", getMemoryUtilization());
            stats.put("processAllocations", new HashMap<>(processPages));
            return stats;
        }
    }

    class FileSystem {
        class FileNode {
            String name;
            boolean isDirectory;
            Map<String, FileNode> children;
            String content;
            long size;
            long created;
            long modified;

            FileNode(String name, boolean isDirectory) {
                this.name = name;
                this.isDirectory = isDirectory;
                this.children = isDirectory ? new HashMap<>() : null;
                this.content = isDirectory ? null : "";
                this.size = 0;
                long now = System.currentTimeMillis();
                this.created = now;
                this.modified = now;
            }
        }

        private FileNode root;
        private FileNode currentDirectory;

        FileSystem() {
            root = new FileNode("/", true);
            currentDirectory = root;
        }

        boolean createFile(String path, String content) {
            String[] parts = path.split("/");
            FileNode parent = navigateToParent(parts);

            if (parent == null || !parent.isDirectory) {
                return false;
            }

            String fileName = parts[parts.length - 1];
            if (parent.children.containsKey(fileName)) {
                return false; // File already exists
            }

            FileNode file = new FileNode(fileName, false);
            file.content = content;
            file.size = content.length();
            parent.children.put(fileName, file);

            return true;
        }

        boolean createDirectory(String path) {
            String[] parts = path.split("/");
            FileNode parent = navigateToParent(parts);

            if (parent == null || !parent.isDirectory) {
                return false;
            }

            String dirName = parts[parts.length - 1];
            if (parent.children.containsKey(dirName)) {
                return false; // Directory already exists
            }

            FileNode dir = new FileNode(dirName, true);
            parent.children.put(dirName, dir);

            return true;
        }

        String readFile(String path) {
            FileNode file = navigateToFile(path);
            return (file != null && !file.isDirectory) ? file.content : null;
        }

        boolean writeFile(String path, String content) {
            FileNode file = navigateToFile(path);
            if (file != null && !file.isDirectory) {
                file.content = content;
                file.size = content.length();
                file.modified = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        boolean deleteFile(String path) {
            String[] parts = path.split("/");
            FileNode parent = navigateToParent(parts);

            if (parent == null || !parent.isDirectory) {
                return false;
            }

            String fileName = parts[parts.length - 1];
            return parent.children.remove(fileName) != null;
        }

        List<String> listDirectory(String path) {
            FileNode dir = path.equals(".") ? currentDirectory : navigateToFile(path);

            if (dir == null || !dir.isDirectory) {
                return new ArrayList<>();
            }

            return new ArrayList<>(dir.children.keySet());
        }

        private FileNode navigateToFile(String path) {
            if (path.startsWith("/")) {
                return navigateFromRoot(path.substring(1).split("/"));
            } else {
                return navigateFromCurrent(path.split("/"));
            }
        }

        private FileNode navigateToParent(String[] parts) {
            if (parts.length <= 1) {
                return root;
            }

            String[] parentParts = Arrays.copyOf(parts, parts.length - 1);
            return navigateFromRoot(parentParts);
        }

        private FileNode navigateFromRoot(String[] parts) {
            FileNode current = root;

            for (String part : parts) {
                if (part.isEmpty())
                    continue;

                if (!current.isDirectory || !current.children.containsKey(part)) {
                    return null;
                }
                current = current.children.get(part);
            }

            return current;
        }

        private FileNode navigateFromCurrent(String[] parts) {
            FileNode current = currentDirectory;

            for (String part : parts) {
                if (part.isEmpty() || part.equals("."))
                    continue;

                if (part.equals("..")) {
                    // Navigate to parent (simplified)
                    continue;
                }

                if (!current.isDirectory || !current.children.containsKey(part)) {
                    return null;
                }
                current = current.children.get(part);
            }

            return current;
        }
    }

    private ProcessScheduler scheduler;
    private MemoryManager memoryManager;
    private FileSystem fileSystem;
    private Map<Integer, Process> processes;
    private int nextPid;

    public DesignOperatingSystem(SchedulingAlgorithm algorithm, int timeQuantum, int totalMemoryPages) {
        scheduler = new ProcessScheduler(algorithm, timeQuantum);
        memoryManager = new MemoryManager(totalMemoryPages);
        fileSystem = new FileSystem();
        processes = new HashMap<>();
        nextPid = 1;
    }

    public int createProcess(String name, int priority, int burstTime, int memoryPages) {
        Process process = new Process(nextPid++, name, priority, burstTime);

        // Allocate memory
        Set<Integer> allocatedPages = memoryManager.allocatePages(process.pid, memoryPages);
        if (allocatedPages == null) {
            System.out.println("Failed to create process: insufficient memory");
            return -1;
        }

        process.allocatedPages = allocatedPages;
        processes.put(process.pid, process);
        scheduler.addProcess(process);

        return process.pid;
    }

    public void executeProcesses() {
        System.out.println("Starting process execution...");

        while (scheduler.hasProcesses()) {
            scheduler.executeNextProcess();

            // Clean up terminated processes
            cleanupTerminatedProcesses();
        }

        System.out.println("All processes completed");
    }

    private void cleanupTerminatedProcesses() {
        Iterator<Process> iterator = processes.values().iterator();
        while (iterator.hasNext()) {
            Process process = iterator.next();
            if (process.state == ProcessState.TERMINATED) {
                memoryManager.deallocatePages(process.pid, process.allocatedPages);
                iterator.remove();
                System.out.println("Process " + process.name + " terminated and cleaned up");
            }
        }
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("scheduler", scheduler.getStats());
        stats.put("memory", memoryManager.getStats());
        stats.put("activeProcesses", processes.size());

        // Process states
        Map<ProcessState, Long> stateCount = processes.values().stream()
                .collect(Collectors.groupingBy(p -> p.state, Collectors.counting()));
        stats.put("processStates", stateCount);

        return stats;
    }

    public ProcessScheduler getScheduler() {
        return scheduler;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public static void main(String[] args) {
        DesignOperatingSystem os = new DesignOperatingSystem(
                SchedulingAlgorithm.ROUND_ROBIN, 2, 100);

        // Create processes
        os.createProcess("Process1", 1, 5, 10);
        os.createProcess("Process2", 2, 3, 8);
        os.createProcess("Process3", 1, 7, 12);
        os.createProcess("Process4", 3, 2, 6);

        System.out.println("Initial system stats:");
        System.out.println(os.getSystemStats());

        // Test file system
        FileSystem fs = os.getFileSystem();
        fs.createDirectory("/home");
        fs.createDirectory("/home/user");
        fs.createFile("/home/user/test.txt", "Hello World!");
        fs.createFile("/home/user/data.txt", "Some data content");

        System.out.println("\nFile system test:");
        System.out.println("Files in /home/user: " + fs.listDirectory("/home/user"));
        System.out.println("Content of test.txt: " + fs.readFile("/home/user/test.txt"));

        // Execute processes
        os.executeProcesses();

        System.out.println("\nFinal system stats:");
        System.out.println(os.getSystemStats());
    }
}
