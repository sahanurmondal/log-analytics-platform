package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Distributed Scheduler
 * 
 * Related LeetCode Problems:
 * - Similar to: Task Scheduler (621), Design Task Scheduler
 * - No direct LeetCode equivalent (System Design)
 * 
 * Company Tags: Uber, Airbnb, Google, Microsoft, Quartz, Jenkins
 * Difficulty: Hard
 * 
 * Description:
 * Design a distributed scheduler that supports:
 * 1. submitJob(jobId) - Submit a job for scheduling
 * 2. cancelJob(jobId) - Cancel a scheduled job
 * 3. getJobStatus(jobId) - Get current job status
 * 
 * The system should handle:
 * - Job distribution across multiple nodes
 * - Load balancing
 * - Job dependencies
 * - Failure recovery
 * 
 * Constraints:
 * - At most 10^5 operations
 * - Support job priorities
 * - Handle node failures gracefully
 * 
 * Follow-ups:
 * 1. Load balancing optimization
 * 2. Job dependency support
 * 3. Cron-like scheduling
 * 4. Dead node detection and recovery
 */
public class DesignDistributedScheduler {
    private final Map<String, Job> jobs;
    private final List<WorkerNode> workers;
    private final PriorityQueue<Job> jobQueue;
    private final Map<String, Set<String>> dependencies;
    private final Map<String, Long> recurringJobs;
    private final ScheduledExecutorService executor;
    private final Random random;

    // Job states
    public enum JobStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }

    // Job class with metadata
    private static class Job {
        String jobId;
        JobStatus status;
        int priority;
        long submitTime;
        long startTime;
        long endTime;
        int assignedWorker;
        int retryCount;
        String errorMessage;
        Set<String> dependencies;

        Job(String jobId, int priority) {
            this.jobId = jobId;
            this.status = JobStatus.PENDING;
            this.priority = priority;
            this.submitTime = System.currentTimeMillis();
            this.startTime = 0;
            this.endTime = 0;
            this.assignedWorker = -1;
            this.retryCount = 0;
            this.dependencies = new HashSet<>();
        }
    }

    // Worker node representation
    private static class WorkerNode {
        int nodeId;
        boolean isAlive;
        int currentLoad;
        int maxCapacity;
        Set<String> runningJobs;
        long lastHeartbeat;

        WorkerNode(int nodeId, int maxCapacity) {
            this.nodeId = nodeId;
            this.isAlive = true;
            this.currentLoad = 0;
            this.maxCapacity = maxCapacity;
            this.runningJobs = new HashSet<>();
            this.lastHeartbeat = System.currentTimeMillis();
        }

        boolean canAcceptJob() {
            return isAlive && currentLoad < maxCapacity;
        }

        void assignJob(String jobId) {
            runningJobs.add(jobId);
            currentLoad++;
        }

        void completeJob(String jobId) {
            runningJobs.remove(jobId);
            currentLoad--;
        }
    }

    /**
     * Constructor - Initialize distributed scheduler
     * Time: O(n), Space: O(n)
     */
    public DesignDistributedScheduler() {
        this.jobs = new ConcurrentHashMap<>();
        this.workers = new ArrayList<>();
        this.jobQueue = new PriorityQueue<>((a, b) -> {
            // Higher priority first, then FIFO
            if (a.priority != b.priority) {
                return Integer.compare(a.priority, b.priority);
            }
            return Long.compare(a.submitTime, b.submitTime);
        });
        this.dependencies = new ConcurrentHashMap<>();
        this.recurringJobs = new ConcurrentHashMap<>();
        this.executor = Executors.newScheduledThreadPool(10);
        this.random = new Random();

        // Initialize some worker nodes
        for (int i = 0; i < 5; i++) {
            workers.add(new WorkerNode(i, 10)); // Each worker can handle 10 jobs
        }

        // Start job processing and health monitoring
        startJobProcessor();
        startHealthMonitor();
    }

    /**
     * Submit job for scheduling
     * Time: O(log n), Space: O(1)
     */
    public void submitJob(String jobId) {
        submitJobWithPriority(jobId, 5); // Default priority
    }

    public void submitJobWithPriority(String jobId, int priority) {
        if (jobs.containsKey(jobId)) {
            return; // Job already exists
        }

        Job job = new Job(jobId, priority);
        jobs.put(jobId, job);

        synchronized (jobQueue) {
            jobQueue.offer(job);
            jobQueue.notifyAll();
        }
    }

    /**
     * Cancel scheduled job
     * Time: O(1), Space: O(1)
     */
    public boolean cancelJob(String jobId) {
        Job job = jobs.get(jobId);
        if (job == null) {
            return false;
        }

        if (job.status == JobStatus.RUNNING) {
            // Stop running job
            WorkerNode worker = workers.get(job.assignedWorker);
            worker.completeJob(jobId);
        }

        job.status = JobStatus.CANCELLED;
        job.endTime = System.currentTimeMillis();

        synchronized (jobQueue) {
            jobQueue.remove(job);
        }

        return true;
    }

    /**
     * Get job status
     * Time: O(1), Space: O(1)
     */
    public String getJobStatus(String jobId) {
        Job job = jobs.get(jobId);
        if (job == null) {
            return null;
        }

        return job.status.toString() +
                (job.assignedWorker >= 0 ? " on worker " + job.assignedWorker : "");
    }

    /**
     * Start job processing thread
     */
    private void startJobProcessor() {
        executor.submit(() -> {
            while (true) {
                try {
                    Job job = null;
                    synchronized (jobQueue) {
                        while (jobQueue.isEmpty()) {
                            jobQueue.wait();
                        }
                        job = jobQueue.poll();
                    }

                    if (job != null && job.status == JobStatus.PENDING) {
                        // Check if dependencies are satisfied
                        if (areDependenciesSatisfied(job.jobId)) {
                            assignJobToWorker(job);
                        } else {
                            // Put back in queue if dependencies not satisfied
                            synchronized (jobQueue) {
                                jobQueue.offer(job);
                            }
                            Thread.sleep(1000); // Wait before retrying
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Assign job to available worker (load balancing)
     * Time: O(n), Space: O(1)
     */
    private void assignJobToWorker(Job job) {
        WorkerNode bestWorker = null;
        int minLoad = Integer.MAX_VALUE;

        // Find worker with minimum load
        for (WorkerNode worker : workers) {
            if (worker.canAcceptJob() && worker.currentLoad < minLoad) {
                minLoad = worker.currentLoad;
                bestWorker = worker;
            }
        }

        if (bestWorker != null) {
            job.status = JobStatus.RUNNING;
            job.startTime = System.currentTimeMillis();
            job.assignedWorker = bestWorker.nodeId;
            bestWorker.assignJob(job.jobId);

            // Simulate job execution
            executor.schedule(() -> {
                completeJob(job.jobId);
            }, random.nextInt(5000) + 1000, TimeUnit.MILLISECONDS);
        } else {
            // No available workers, put back in queue
            synchronized (jobQueue) {
                jobQueue.offer(job);
            }
        }
    }

    /**
     * Complete job execution
     * Time: O(1), Space: O(1)
     */
    private void completeJob(String jobId) {
        Job job = jobs.get(jobId);
        if (job != null && job.status == JobStatus.RUNNING) {
            WorkerNode worker = workers.get(job.assignedWorker);
            worker.completeJob(jobId);

            // Simulate success/failure
            if (random.nextDouble() < 0.9) { // 90% success rate
                job.status = JobStatus.COMPLETED;
            } else {
                job.status = JobStatus.FAILED;
                job.errorMessage = "Random failure simulation";

                // Retry failed jobs up to 3 times
                if (job.retryCount < 3) {
                    job.retryCount++;
                    job.status = JobStatus.PENDING;
                    job.assignedWorker = -1;
                    synchronized (jobQueue) {
                        jobQueue.offer(job);
                    }
                }
            }

            job.endTime = System.currentTimeMillis();
        }
    }

    /**
     * Start health monitoring for workers
     */
    private void startHealthMonitor() {
        executor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();

            for (WorkerNode worker : workers) {
                // Simulate heartbeat (in real system, workers would send heartbeats)
                if (random.nextDouble() < 0.95) { // 95% availability
                    worker.lastHeartbeat = currentTime;
                    worker.isAlive = true;
                } else {
                    // Mark worker as dead if no heartbeat for 30 seconds
                    if (currentTime - worker.lastHeartbeat > 30000) {
                        worker.isAlive = false;
                        rescheduleJobsFromDeadWorker(worker);
                    }
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Reschedule jobs from dead worker
     * Time: O(m), Space: O(1) where m is jobs on dead worker
     */
    private void rescheduleJobsFromDeadWorker(WorkerNode deadWorker) {
        for (String jobId : new HashSet<>(deadWorker.runningJobs)) {
            Job job = jobs.get(jobId);
            if (job != null) {
                job.status = JobStatus.PENDING;
                job.assignedWorker = -1;
                deadWorker.completeJob(jobId);

                synchronized (jobQueue) {
                    jobQueue.offer(job);
                }
            }
        }
    }

    // Follow-up 1: Add job dependency
    public void addJobDependency(String jobId, String dependencyJobId) {
        dependencies.computeIfAbsent(jobId, k -> new HashSet<>()).add(dependencyJobId);

        Job job = jobs.get(jobId);
        if (job != null) {
            job.dependencies.add(dependencyJobId);
        }
    }

    // Follow-up 2: Check if job dependencies are satisfied
    private boolean areDependenciesSatisfied(String jobId) {
        Set<String> deps = dependencies.get(jobId);
        if (deps == null || deps.isEmpty()) {
            return true;
        }

        for (String depJobId : deps) {
            Job depJob = jobs.get(depJobId);
            if (depJob == null || depJob.status != JobStatus.COMPLETED) {
                return false;
            }
        }

        return true;
    }

    // Follow-up 3: Schedule recurring job (cron-like)
    public void scheduleRecurringJob(String jobId, long intervalMs) {
        recurringJobs.put(jobId, intervalMs);

        executor.scheduleAtFixedRate(() -> {
            if (recurringJobs.containsKey(jobId)) {
                String recurringJobId = jobId + "_" + System.currentTimeMillis();
                submitJob(recurringJobId);
            }
        }, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    // Follow-up 4: Get worker statistics
    public Map<Integer, String> getWorkerStats() {
        Map<Integer, String> stats = new HashMap<>();

        for (WorkerNode worker : workers) {
            String status = String.format("Load: %d/%d, Alive: %s, Jobs: %s",
                    worker.currentLoad, worker.maxCapacity, worker.isAlive, worker.runningJobs);
            stats.put(worker.nodeId, status);
        }

        return stats;
    }

    // Follow-up 5: Get job execution statistics
    public Map<String, Object> getJobStats() {
        Map<String, Object> stats = new HashMap<>();
        long completed = jobs.values().stream().mapToLong(j -> j.status == JobStatus.COMPLETED ? 1 : 0).sum();
        long failed = jobs.values().stream().mapToLong(j -> j.status == JobStatus.FAILED ? 1 : 0).sum();
        long running = jobs.values().stream().mapToLong(j -> j.status == JobStatus.RUNNING ? 1 : 0).sum();
        long pending = jobs.values().stream().mapToLong(j -> j.status == JobStatus.PENDING ? 1 : 0).sum();

        stats.put("total", jobs.size());
        stats.put("completed", completed);
        stats.put("failed", failed);
        stats.put("running", running);
        stats.put("pending", pending);

        return stats;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Design Distributed Scheduler Test ===");

        // Test Case 1: Basic job submission and status
        DesignDistributedScheduler scheduler = new DesignDistributedScheduler();

        scheduler.submitJob("job1");
        scheduler.submitJob("job2");
        scheduler.submitJob("job3");

        System.out.println("Job1 status: " + scheduler.getJobStatus("job1"));
        System.out.println("Job2 status: " + scheduler.getJobStatus("job2"));

        // Test Case 2: Job cancellation
        boolean cancelled = scheduler.cancelJob("job3");
        System.out.println("Job3 cancelled: " + cancelled);
        System.out.println("Job3 status: " + scheduler.getJobStatus("job3"));

        // Test Case 3: Non-existent job
        System.out.println("Non-existent job status: " + scheduler.getJobStatus("nonExistent"));
        System.out.println("Cancel non-existent job: " + scheduler.cancelJob("nonExistent"));

        // Test Case 4: Job priorities (Follow-up)
        System.out.println("\n=== Priority Jobs ===");
        scheduler.submitJobWithPriority("highPriority", 1);
        scheduler.submitJobWithPriority("lowPriority", 10);

        // Test Case 5: Job dependencies (Follow-up)
        System.out.println("\n=== Job Dependencies ===");
        scheduler.submitJob("parentJob");
        scheduler.submitJob("childJob");
        scheduler.addJobDependency("childJob", "parentJob");

        System.out.println("Child job status: " + scheduler.getJobStatus("childJob"));

        // Test Case 6: Recurring jobs (Follow-up)
        System.out.println("\n=== Recurring Jobs ===");
        scheduler.scheduleRecurringJob("recurringJob", 5000); // Every 5 seconds

        // Wait a bit to see some job processing
        Thread.sleep(3000);

        // Test Case 7: Worker and job statistics (Follow-up)
        System.out.println("\n=== Statistics ===");
        System.out.println("Worker stats: " + scheduler.getWorkerStats());
        System.out.println("Job stats: " + scheduler.getJobStats());

        // Performance test
        System.out.println("\n=== Performance Test ===");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            scheduler.submitJob("perf_job_" + i);
        }

        Thread.sleep(2000); // Let some jobs process

        Map<String, Object> finalStats = scheduler.getJobStats();
        long endTime = System.currentTimeMillis();

        System.out.println("Final job stats after " + (endTime - startTime) + "ms: " + finalStats);

        // Cleanup
        scheduler.executor.shutdown();
    }
}
