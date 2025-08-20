package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Variation: Design Distributed Scheduler With Job Dependencies
 *
 * Description:
 * Design a distributed scheduler supporting job submission, cancellation,
 * status query, and job dependencies.
 *
 * Constraints:
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for load balancing?
 * - Can you support cyclic dependencies?
 * 
 * Time Complexity:
 * - submitJob: O(V + E) for cycle detection, where V is jobs, E is dependencies
 * - cancelJob: O(V) to update dependent jobs
 * - getJobStatus: O(1)
 * Space Complexity: O(V + E) for storing job dependency graph
 * 
 * Company Tags: System Design, Job Scheduling, Distributed Systems
 */
public class DesignDistributedSchedulerWithDependencies {

    // Job states
    public enum JobStatus {
        PENDING, // Waiting for dependencies
        READY, // Ready to execute (dependencies satisfied)
        RUNNING, // Currently executing
        COMPLETED, // Successfully completed
        FAILED, // Failed during execution
        CANCELLED // Cancelled by user or due to dependency failure
    }

    // Job representation
    private static class Job {
        String jobId;
        Set<String> dependencies;
        Set<String> dependents; // Jobs that depend on this job
        JobStatus status;
        long submitTime;
        long startTime;
        long endTime;
        String failureReason;
        int retryCount;

        Job(String jobId, String[] dependencies) {
            this.jobId = jobId;
            this.dependencies = new HashSet<>(Arrays.asList(dependencies));
            this.dependents = new HashSet<>();
            this.status = JobStatus.PENDING;
            this.submitTime = System.currentTimeMillis();
            this.retryCount = 0;
        }
    }

    // Worker node for distributed execution
    private static class WorkerNode {
        String nodeId;
        int capacity;
        int currentLoad;
        boolean isHealthy;
        long lastHeartbeat;

        WorkerNode(String nodeId, int capacity) {
            this.nodeId = nodeId;
            this.capacity = capacity;
            this.currentLoad = 0;
            this.isHealthy = true;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        boolean canAcceptJob() {
            return isHealthy && currentLoad < capacity;
        }
    }

    private final Map<String, Job> jobs;
    private final Map<String, WorkerNode> workers;
    private final ExecutorService executorService;
    private final ScheduledExecutorService schedulerService;
    private final int maxRetries;
    private final long heartbeatInterval;

    public DesignDistributedSchedulerWithDependencies() {
        this.jobs = new ConcurrentHashMap<>();
        this.workers = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
        this.schedulerService = Executors.newScheduledThreadPool(4);
        this.maxRetries = 3;
        this.heartbeatInterval = 10000; // 10 seconds

        // Initialize some worker nodes
        for (int i = 0; i < 3; i++) {
            String workerId = "worker-" + i;
            workers.put(workerId, new WorkerNode(workerId, 5));
        }

        // Start background processes
        startJobProcessor();
        startHealthChecker();
    }

    public void submitJob(String jobId, String[] dependencies) {
        if (jobs.containsKey(jobId)) {
            throw new IllegalArgumentException("Job " + jobId + " already exists");
        }

        // Check for cyclic dependencies
        if (hasCyclicDependency(jobId, dependencies)) {
            throw new IllegalArgumentException("Cyclic dependency detected for job " + jobId);
        }

        Job job = new Job(jobId, dependencies);

        // Build dependency graph
        for (String depId : dependencies) {
            Job depJob = jobs.get(depId);
            if (depJob != null) {
                depJob.dependents.add(jobId);
            }
        }

        jobs.put(jobId, job);

        // Check if job is ready to run
        updateJobReadiness(job);

        System.out.println("Submitted job: " + jobId + " with dependencies: " + Arrays.toString(dependencies));
    }

    public boolean cancelJob(String jobId) {
        Job job = jobs.get(jobId);
        if (job == null) {
            return false;
        }

        synchronized (job) {
            if (job.status == JobStatus.COMPLETED || job.status == JobStatus.CANCELLED) {
                return false;
            }

            job.status = JobStatus.CANCELLED;
            job.endTime = System.currentTimeMillis();

            // Cancel all dependent jobs
            cancelDependentJobs(jobId);
        }

        System.out.println("Cancelled job: " + jobId);
        return true;
    }

    public String getJobStatus(String jobId) {
        Job job = jobs.get(jobId);
        if (job == null) {
            return null;
        }

        return job.status.toString();
    }

    // Additional methods for comprehensive functionality

    public Map<String, Object> getJobDetails(String jobId) {
        Job job = jobs.get(jobId);
        if (job == null) {
            return null;
        }

        Map<String, Object> details = new HashMap<>();
        details.put("jobId", job.jobId);
        details.put("status", job.status.toString());
        details.put("dependencies", new ArrayList<>(job.dependencies));
        details.put("dependents", new ArrayList<>(job.dependents));
        details.put("submitTime", job.submitTime);
        details.put("startTime", job.startTime);
        details.put("endTime", job.endTime);
        details.put("retryCount", job.retryCount);

        if (job.failureReason != null) {
            details.put("failureReason", job.failureReason);
        }

        return details;
    }

    public List<String> getJobsByStatus(JobStatus status) {
        return jobs.values().stream()
                .filter(job -> job.status == status)
                .map(job -> job.jobId)
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getClusterStatus() {
        Map<String, Integer> status = new HashMap<>();
        for (JobStatus jobStatus : JobStatus.values()) {
            status.put(jobStatus.toString(), getJobsByStatus(jobStatus).size());
        }
        return status;
    }

    public void addWorkerNode(String nodeId, int capacity) {
        workers.put(nodeId, new WorkerNode(nodeId, capacity));
        System.out.println("Added worker node: " + nodeId + " with capacity: " + capacity);
    }

    public boolean removeWorkerNode(String nodeId) {
        WorkerNode removed = workers.remove(nodeId);
        if (removed != null) {
            System.out.println("Removed worker node: " + nodeId);
            return true;
        }
        return false;
    }

    // Private helper methods

    private boolean hasCyclicDependency(String jobId, String[] dependencies) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        // Add the new job temporarily to check for cycles
        Map<String, Set<String>> tempGraph = new HashMap<>();
        for (Job job : jobs.values()) {
            tempGraph.put(job.jobId, new HashSet<>(job.dependencies));
        }
        tempGraph.put(jobId, new HashSet<>(Arrays.asList(dependencies)));

        return hasCycleDFS(jobId, tempGraph, visited, recursionStack);
    }

    private boolean hasCycleDFS(String node, Map<String, Set<String>> graph,
            Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(node)) {
            return true; // Back edge found, cycle detected
        }

        if (visited.contains(node)) {
            return false; // Already processed
        }

        visited.add(node);
        recursionStack.add(node);

        Set<String> dependencies = graph.get(node);
        if (dependencies != null) {
            for (String dep : dependencies) {
                if (hasCycleDFS(dep, graph, visited, recursionStack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(node);
        return false;
    }

    private void updateJobReadiness(Job job) {
        if (job.status != JobStatus.PENDING) {
            return;
        }

        boolean allDependenciesCompleted = true;
        for (String depId : job.dependencies) {
            Job depJob = jobs.get(depId);
            if (depJob == null || depJob.status != JobStatus.COMPLETED) {
                allDependenciesCompleted = false;
                break;
            }
        }

        if (allDependenciesCompleted) {
            job.status = JobStatus.READY;
            System.out.println("Job " + job.jobId + " is now ready to execute");
        }
    }

    private void cancelDependentJobs(String jobId) {
        Job job = jobs.get(jobId);
        if (job == null)
            return;

        for (String dependentId : job.dependents) {
            Job dependent = jobs.get(dependentId);
            if (dependent != null && dependent.status != JobStatus.COMPLETED &&
                    dependent.status != JobStatus.CANCELLED) {
                dependent.status = JobStatus.CANCELLED;
                dependent.endTime = System.currentTimeMillis();
                dependent.failureReason = "Dependency " + jobId + " was cancelled";
                System.out.println("Cancelled dependent job: " + dependentId);

                // Recursively cancel dependents
                cancelDependentJobs(dependentId);
            }
        }
    }

    private void startJobProcessor() {
        schedulerService.scheduleWithFixedDelay(() -> {
            try {
                processReadyJobs();
            } catch (Exception e) {
                System.err.println("Error processing jobs: " + e.getMessage());
            }
        }, 1, 2, TimeUnit.SECONDS);
    }

    private void processReadyJobs() {
        List<Job> readyJobs = jobs.values().stream()
                .filter(job -> job.status == JobStatus.READY)
                .collect(Collectors.toList());

        for (Job job : readyJobs) {
            WorkerNode worker = selectWorkerNode();
            if (worker != null) {
                executeJob(job, worker);
            }
        }
    }

    private WorkerNode selectWorkerNode() {
        // Simple load balancing: select worker with lowest load
        return workers.values().stream()
                .filter(WorkerNode::canAcceptJob)
                .min(Comparator.comparingInt(w -> w.currentLoad))
                .orElse(null);
    }

    private void executeJob(Job job, WorkerNode worker) {
        synchronized (job) {
            if (job.status != JobStatus.READY) {
                return;
            }

            job.status = JobStatus.RUNNING;
            job.startTime = System.currentTimeMillis();
            worker.currentLoad++;
        }

        executorService.submit(() -> {
            try {
                System.out.println("Executing job " + job.jobId + " on worker " + worker.nodeId);

                // Simulate job execution
                Thread.sleep(1000 + (int) (Math.random() * 2000)); // 1-3 seconds

                // Simulate random failures (10% chance)
                if (Math.random() < 0.1 && job.retryCount < maxRetries) {
                    throw new RuntimeException("Simulated job failure");
                }

                synchronized (job) {
                    job.status = JobStatus.COMPLETED;
                    job.endTime = System.currentTimeMillis();
                }

                System.out.println("Job " + job.jobId + " completed successfully");

                // Update dependent jobs
                for (String dependentId : job.dependents) {
                    Job dependent = jobs.get(dependentId);
                    if (dependent != null) {
                        updateJobReadiness(dependent);
                    }
                }

            } catch (Exception e) {
                synchronized (job) {
                    job.retryCount++;
                    if (job.retryCount < maxRetries) {
                        job.status = JobStatus.READY; // Retry
                        System.out.println("Job " + job.jobId + " failed, retrying (" +
                                job.retryCount + "/" + maxRetries + ")");
                    } else {
                        job.status = JobStatus.FAILED;
                        job.endTime = System.currentTimeMillis();
                        job.failureReason = e.getMessage();
                        System.out.println("Job " + job.jobId + " failed permanently: " + e.getMessage());

                        // Cancel dependent jobs
                        cancelDependentJobs(job.jobId);
                    }
                }
            } finally {
                worker.currentLoad--;
            }
        });
    }

    private void startHealthChecker() {
        schedulerService.scheduleWithFixedDelay(() -> {
            long currentTime = System.currentTimeMillis();
            for (WorkerNode worker : workers.values()) {
                if (currentTime - worker.lastHeartbeat > heartbeatInterval * 2) {
                    worker.isHealthy = false;
                    System.out.println("Worker " + worker.nodeId + " marked as unhealthy");
                } else {
                    worker.isHealthy = true;
                    worker.lastHeartbeat = currentTime; // Simulate heartbeat
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void shutdown() {
        schedulerService.shutdown();
        executorService.shutdown();
        try {
            if (!schedulerService.awaitTermination(5, TimeUnit.SECONDS)) {
                schedulerService.shutdownNow();
            }
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            schedulerService.shutdownNow();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Distributed Scheduler With Dependencies Tests ===");

        DesignDistributedSchedulerWithDependencies scheduler = new DesignDistributedSchedulerWithDependencies();

        System.out.println("\n--- Basic Dependency Test ---");
        scheduler.submitJob("job1", new String[] {});
        scheduler.submitJob("job2", new String[] { "job1" });
        scheduler.submitJob("job3", new String[] { "job1", "job2" });

        Thread.sleep(3000); // Wait for jobs to execute

        System.out.println("Job1 status: " + scheduler.getJobStatus("job1"));
        System.out.println("Job2 status: " + scheduler.getJobStatus("job2"));
        System.out.println("Job3 status: " + scheduler.getJobStatus("job3"));

        System.out.println("\n--- Cancellation Test ---");
        scheduler.submitJob("job4", new String[] {});
        scheduler.submitJob("job5", new String[] { "job4" });
        scheduler.submitJob("job6", new String[] { "job5" });

        Thread.sleep(1000);
        System.out.println("Cancelling job4...");
        System.out.println("Cancel job4: " + scheduler.cancelJob("job4"));

        Thread.sleep(2000);
        System.out.println("Job4 status: " + scheduler.getJobStatus("job4"));
        System.out.println("Job5 status: " + scheduler.getJobStatus("job5"));
        System.out.println("Job6 status: " + scheduler.getJobStatus("job6"));

        System.out.println("\n--- Edge Cases Test ---");
        // Cancel non-existent job
        System.out.println("Cancel non-existent job: " + scheduler.cancelJob("nonexistent"));

        // Query status for non-existent job
        System.out.println("Status of non-existent job: " + scheduler.getJobStatus("nonexistent"));

        // Test cyclic dependency detection
        try {
            scheduler.submitJob("cyclic1", new String[] { "cyclic2" });
            scheduler.submitJob("cyclic2", new String[] { "cyclic1" });
            System.out.println("ERROR: Cyclic dependency not detected!");
        } catch (IllegalArgumentException e) {
            System.out.println("Cyclic dependency correctly detected: " + e.getMessage());
        }

        System.out.println("\n--- Cluster Status ---");
        Map<String, Integer> clusterStatus = scheduler.getClusterStatus();
        clusterStatus.forEach((status, count) -> System.out.println(status + ": " + count + " jobs"));

        System.out.println("\n--- Complex Dependency Chain Test ---");
        scheduler.submitJob("A", new String[] {});
        scheduler.submitJob("B", new String[] { "A" });
        scheduler.submitJob("C", new String[] { "A" });
        scheduler.submitJob("D", new String[] { "B", "C" });
        scheduler.submitJob("E", new String[] { "D" });

        Thread.sleep(8000); // Wait for execution

        System.out.println("Final job statuses:");
        for (String jobId : Arrays.asList("A", "B", "C", "D", "E")) {
            System.out.println(jobId + ": " + scheduler.getJobStatus(jobId));
        }

        System.out.println("\n--- Worker Management Test ---");
        scheduler.addWorkerNode("worker-custom", 10);
        Thread.sleep(1000);
        scheduler.removeWorkerNode("worker-0");

        // Submit more jobs to test load balancing
        for (int i = 7; i <= 10; i++) {
            scheduler.submitJob("job" + i, new String[] {});
        }

        Thread.sleep(5000);

        System.out.println("\nFinal cluster status:");
        Map<String, Integer> finalStatus = scheduler.getClusterStatus();
        finalStatus.forEach((status, count) -> System.out.println(status + ": " + count + " jobs"));

        scheduler.shutdown();
        System.out.println("\nScheduler shutdown complete.");
    }
}
