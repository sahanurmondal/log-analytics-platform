package design.hard;

import java.util.*;

/**
 * Design Advanced Task Scheduler
 *
 * Description: Design a task scheduler that supports:
 * - Schedule tasks with priorities and dependencies
 * - Execute tasks based on priority and availability
 * - Handle task failures and retries
 * - Support periodic tasks
 * 
 * Constraints:
 * - Tasks have priorities (1-10, higher = more important)
 * - Dependencies must be resolved before execution
 * - Support up to 10^4 tasks
 *
 * Follow-up:
 * - How to handle distributed scheduling?
 * - Fault tolerance mechanisms?
 * 
 * Time Complexity: O(log n) for scheduling, O(1) for execution
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon, Microsoft
 */
public class DesignTaskScheduler {

    enum TaskStatus {
        PENDING, READY, RUNNING, COMPLETED, FAILED
    }

    class Task {
        int taskId;
        int priority;
        String description;
        List<Integer> dependencies;
        TaskStatus status;
        long scheduledTime;
        int retryCount;
        int maxRetries;
        boolean isPeriodic;
        long period; // for periodic tasks
        Runnable execution;

        Task(int taskId, int priority, String description) {
            this.taskId = taskId;
            this.priority = priority;
            this.description = description;
            this.dependencies = new ArrayList<>();
            this.status = TaskStatus.PENDING;
            this.scheduledTime = System.currentTimeMillis();
            this.retryCount = 0;
            this.maxRetries = 3;
            this.isPeriodic = false;
        }
    }

    private Map<Integer, Task> tasks;
    private PriorityQueue<Task> readyQueue;
    private Set<Integer> runningTasks;
    private Queue<Task> completedTasks;
    private Timer scheduler;
    private int nextTaskId;

    public DesignTaskScheduler() {
        tasks = new HashMap<>();
        readyQueue = new PriorityQueue<>((a, b) -> {
            if (a.priority != b.priority) {
                return b.priority - a.priority; // Higher priority first
            }
            return Long.compare(a.scheduledTime, b.scheduledTime); // Earlier scheduled first
        });
        runningTasks = new HashSet<>();
        completedTasks = new LinkedList<>();
        scheduler = new Timer();
        nextTaskId = 1;
    }

    public int scheduleTask(int priority, String description, List<Integer> dependencies) {
        Task task = new Task(nextTaskId++, priority, description);
        if (dependencies != null) {
            task.dependencies.addAll(dependencies);
        }

        tasks.put(task.taskId, task);
        updateTaskStatus(task);

        return task.taskId;
    }

    public int schedulePeriodicTask(int priority, String description, long period) {
        Task task = new Task(nextTaskId++, priority, description);
        task.isPeriodic = true;
        task.period = period;

        tasks.put(task.taskId, task);
        updateTaskStatus(task);

        return task.taskId;
    }

    public void setTaskExecution(int taskId, Runnable execution) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.execution = execution;
        }
    }

    public void setMaxRetries(int taskId, int maxRetries) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.maxRetries = maxRetries;
        }
    }

    private void updateTaskStatus(Task task) {
        if (task.status == TaskStatus.COMPLETED || task.status == TaskStatus.RUNNING) {
            return;
        }

        // Check if all dependencies are completed
        boolean dependenciesReady = true;
        for (int depId : task.dependencies) {
            Task dependency = tasks.get(depId);
            if (dependency == null || dependency.status != TaskStatus.COMPLETED) {
                dependenciesReady = false;
                break;
            }
        }

        if (dependenciesReady && task.status == TaskStatus.PENDING) {
            task.status = TaskStatus.READY;
            readyQueue.offer(task);
        }
    }

    public Task getNextTask() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        Task task = readyQueue.poll();
        task.status = TaskStatus.RUNNING;
        runningTasks.add(task.taskId);

        return task;
    }

    public void completeTask(int taskId, boolean success) {
        Task task = tasks.get(taskId);
        if (task == null || !runningTasks.contains(taskId)) {
            return;
        }

        runningTasks.remove(taskId);

        if (success) {
            task.status = TaskStatus.COMPLETED;
            completedTasks.offer(task);

            // Update dependent tasks
            for (Task t : tasks.values()) {
                if (t.dependencies.contains(taskId)) {
                    updateTaskStatus(t);
                }
            }

            // Reschedule periodic task
            if (task.isPeriodic) {
                schedulePeriodicExecution(task);
            }
        } else {
            task.retryCount++;
            if (task.retryCount < task.maxRetries) {
                task.status = TaskStatus.READY;
                task.scheduledTime = System.currentTimeMillis() + (1000L * task.retryCount); // Exponential backoff

                scheduler.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        readyQueue.offer(task);
                    }
                }, 1000L * task.retryCount);
            } else {
                task.status = TaskStatus.FAILED;
            }
        }
    }

    private void schedulePeriodicExecution(Task task) {
        Task newTask = new Task(nextTaskId++, task.priority, task.description);
        newTask.isPeriodic = true;
        newTask.period = task.period;
        newTask.execution = task.execution;
        newTask.maxRetries = task.maxRetries;
        newTask.scheduledTime = System.currentTimeMillis() + task.period;

        tasks.put(newTask.taskId, newTask);

        scheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTaskStatus(newTask);
            }
        }, task.period);
    }

    public void executeTask(Task task) {
        if (task.execution != null) {
            try {
                task.execution.run();
                completeTask(task.taskId, true);
            } catch (Exception e) {
                System.err.println("Task " + task.taskId + " failed: " + e.getMessage());
                completeTask(task.taskId, false);
            }
        } else {
            // Default execution - just mark as completed
            completeTask(task.taskId, true);
        }
    }

    public void cancelTask(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null && task.status != TaskStatus.RUNNING && task.status != TaskStatus.COMPLETED) {
            readyQueue.remove(task);
            tasks.remove(taskId);
        }
    }

    public TaskStatus getTaskStatus(int taskId) {
        Task task = tasks.get(taskId);
        return task == null ? null : task.status;
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.status == status) {
                result.add(task);
            }
        }
        return result;
    }

    public Map<String, Integer> getTaskStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            stats.put(status.name(), 0);
        }

        for (Task task : tasks.values()) {
            stats.put(task.status.name(), stats.get(task.status.name()) + 1);
        }

        return stats;
    }

    public void shutdown() {
        scheduler.cancel();
    }

    public static void main(String[] args) {
        DesignTaskScheduler scheduler = new DesignTaskScheduler();

        // Schedule tasks with dependencies
        int task1 = scheduler.scheduleTask(5, "Initialize system", null);
        int task2 = scheduler.scheduleTask(3, "Load configuration", Arrays.asList(task1));
        int task3 = scheduler.scheduleTask(7, "Start services", Arrays.asList(task1, task2));

        // Schedule periodic task
        int periodicTask = scheduler.schedulePeriodicTask(2, "Health check", 5000);

        // Set task executions
        scheduler.setTaskExecution(task1, () -> {
            System.out.println("Executing task 1: Initialize system");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        });

        scheduler.setTaskExecution(periodicTask, () -> {
            System.out.println("Executing periodic task: Health check at " + new Date());
        });

        // Execute tasks
        System.out.println("Task statistics: " + scheduler.getTaskStatistics());

        // Process ready tasks
        for (int i = 0; i < 5; i++) {
            Task task = scheduler.getNextTask();
            if (task != null) {
                System.out.println("Executing task " + task.taskId + ": " + task.description);
                scheduler.executeTask(task);
            } else {
                break;
            }
        }

        System.out.println("Final statistics: " + scheduler.getTaskStatistics());

        // Cleanup
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        scheduler.shutdown();
    }
}
