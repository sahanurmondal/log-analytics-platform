package company.banking.kotakbank;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kotak Bank SDE3 Interview Question #7
 * 
 * Problem: Banking Queue Management System
 * LeetCode Equivalent: Task Scheduler + Priority Queue + Resource Management
 * Source: Banking Operations + TryExponent System Design
 * 
 * Banking Context:
 * Design a queue management system for bank branches that efficiently
 * assigns customers to available tellers based on service type priorities,
 * customer categories (VIP, Senior Citizen, Regular), and estimated
 * service times. The system should minimize overall waiting time and
 * ensure fair service distribution.
 * 
 * Interview Focus:
 * - Priority queue implementations
 * - Resource allocation algorithms
 * - Real-time queue optimization
 * - Service level agreement (SLA) management
 * 
 * Difficulty: Medium-Hard
 * Expected Time: 40-50 minutes
 * 
 * Follow-up Questions:
 * 1. How would you handle emergency situations or VIP customer arrivals?
 * 2. How would you predict and prevent queue bottlenecks?
 * 3. How would you integrate with mobile apps for virtual queuing?
 * 4. How would you optimize teller allocation during peak hours?
 */
public class BankingQueueManager {

    /**
     * Service types with different priorities and time requirements
     */
    public enum ServiceType {
        EMERGENCY(1, 5, "Emergency banking services"),
        ACCOUNT_OPENING(2, 15, "New account opening"),
        LOAN_INQUIRY(3, 20, "Loan consultation"),
        INVESTMENT_ADVISORY(4, 25, "Investment planning"),
        CASH_DEPOSIT(5, 3, "Cash deposit transaction"),
        CASH_WITHDRAWAL(6, 2, "Cash withdrawal"),
        GENERAL_INQUIRY(7, 8, "General banking inquiry"),
        STATEMENT_REQUEST(8, 5, "Statement and document requests");

        private final int priority;
        private final int estimatedTimeMinutes;
        private final String description;

        ServiceType(int priority, int estimatedTimeMinutes, String description) {
            this.priority = priority;
            this.estimatedTimeMinutes = estimatedTimeMinutes;
            this.description = description;
        }

        public int getPriority() {
            return priority;
        }

        public int getEstimatedTimeMinutes() {
            return estimatedTimeMinutes;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Customer categories affecting service priority
     */
    public enum CustomerCategory {
        VIP(100, "VIP Premium Banking Customer"),
        SENIOR_CITIZEN(50, "Senior Citizen (60+ years)"),
        DISABLED(75, "Customer with disabilities"),
        PREGNANT_MOTHER(60, "Expecting mothers"),
        REGULAR(0, "Regular customer");

        private final int priorityBonus;
        private final String description;

        CustomerCategory(int priorityBonus, String description) {
            this.priorityBonus = priorityBonus;
            this.description = description;
        }

        public int getPriorityBonus() {
            return priorityBonus;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Customer representation in the queue
     */
    public static class Customer {
        String customerId;
        String name;
        CustomerCategory category;
        ServiceType serviceType;
        LocalDateTime arrivalTime;
        LocalDateTime serviceStartTime;
        LocalDateTime serviceEndTime;
        int tokenNumber;
        String specialRequirements;

        public Customer(String customerId, String name, CustomerCategory category,
                ServiceType serviceType, String specialRequirements) {
            this.customerId = customerId;
            this.name = name;
            this.category = category;
            this.serviceType = serviceType;
            this.arrivalTime = LocalDateTime.now();
            this.specialRequirements = specialRequirements;
        }

        /**
         * Calculate effective priority (lower number = higher priority)
         */
        public int getEffectivePriority() {
            return Math.max(1, serviceType.getPriority() - category.getPriorityBonus());
        }

        /**
         * Calculate waiting time in minutes
         */
        public long getWaitingTimeMinutes() {
            LocalDateTime endTime = serviceStartTime != null ? serviceStartTime : LocalDateTime.now();
            return java.time.Duration.between(arrivalTime, endTime).toMinutes();
        }

        @Override
        public String toString() {
            return String.format("🎫 Token #%d - %s (%s)\n" +
                    "   Service: %s | Category: %s | Priority: %d\n" +
                    "   Arrival: %s | Wait Time: %d min",
                    tokenNumber, name, customerId, serviceType.getDescription(),
                    category.getDescription(), getEffectivePriority(),
                    arrivalTime.toString().substring(11, 19), getWaitingTimeMinutes());
        }
    }

    /**
     * Teller representation with specialization and current status
     */
    public static class Teller {
        String tellerId;
        String name;
        Set<ServiceType> specializations;
        boolean isAvailable;
        Customer currentCustomer;
        LocalDateTime availableFrom;
        int customersServedToday;
        double averageServiceTime;

        public Teller(String tellerId, String name, Set<ServiceType> specializations) {
            this.tellerId = tellerId;
            this.name = name;
            this.specializations = specializations;
            this.isAvailable = true;
            this.customersServedToday = 0;
            this.averageServiceTime = 0.0;
        }

        public boolean canHandle(ServiceType serviceType) {
            return specializations.contains(serviceType) ||
                    specializations.contains(ServiceType.GENERAL_INQUIRY);
        }

        public void startServing(Customer customer) {
            this.isAvailable = false;
            this.currentCustomer = customer;
            customer.serviceStartTime = LocalDateTime.now();

            // Estimate when teller will be available
            int estimatedMinutes = customer.serviceType.getEstimatedTimeMinutes();
            this.availableFrom = LocalDateTime.now().plusMinutes(estimatedMinutes);
        }

        public void finishServing() {
            if (currentCustomer != null) {
                currentCustomer.serviceEndTime = LocalDateTime.now();

                // Update statistics
                customersServedToday++;
                long actualServiceTime = java.time.Duration.between(
                        currentCustomer.serviceStartTime, currentCustomer.serviceEndTime).toMinutes();

                averageServiceTime = ((averageServiceTime * (customersServedToday - 1)) + actualServiceTime)
                        / customersServedToday;

                currentCustomer = null;
            }
            this.isAvailable = true;
            this.availableFrom = LocalDateTime.now();
        }

        @Override
        public String toString() {
            String status = isAvailable ? "✅ Available"
                    : String.format("🔄 Serving %s (until %s)",
                            currentCustomer.name,
                            availableFrom.toString().substring(11, 19));

            return String.format("👨‍💼 %s (%s) - %s\n" +
                    "   Specializations: %s\n" +
                    "   Customers Served Today: %d | Avg Service Time: %.1f min",
                    name, tellerId, status, specializations,
                    customersServedToday, averageServiceTime);
        }
    }

    /**
     * Queue management system with multiple priority queues
     */
    private final Map<ServiceType, PriorityQueue<Customer>> serviceQueues;
    private final List<Teller> tellers;
    private final Map<String, Customer> customerMap;
    private int nextTokenNumber;
    private final Object queueLock = new Object();

    // Queue management statistics
    private int totalCustomersServed;
    private double averageWaitTime;
    private Map<ServiceType, Integer> serviceTypeCount;

    public BankingQueueManager() {
        this.serviceQueues = new ConcurrentHashMap<>();
        this.tellers = new ArrayList<>();
        this.customerMap = new ConcurrentHashMap<>();
        this.nextTokenNumber = 1;
        this.serviceTypeCount = new ConcurrentHashMap<>();

        // Initialize queues for each service type
        for (ServiceType serviceType : ServiceType.values()) {
            serviceQueues.put(serviceType, new PriorityQueue<>(
                    Comparator.comparingInt(Customer::getEffectivePriority)
                            .thenComparingInt(c -> c.tokenNumber)));
            serviceTypeCount.put(serviceType, 0);
        }

        initializeTellers();
    }

    /**
     * Initialize bank tellers with their specializations
     */
    private void initializeTellers() {
        // Specialized tellers
        tellers.add(new Teller("T001", "Rajesh Kumar",
                Set.of(ServiceType.ACCOUNT_OPENING, ServiceType.LOAN_INQUIRY, ServiceType.GENERAL_INQUIRY)));

        tellers.add(new Teller("T002", "Priya Sharma",
                Set.of(ServiceType.INVESTMENT_ADVISORY, ServiceType.LOAN_INQUIRY, ServiceType.GENERAL_INQUIRY)));

        tellers.add(new Teller("T003", "Amit Patel",
                Set.of(ServiceType.CASH_DEPOSIT, ServiceType.CASH_WITHDRAWAL, ServiceType.GENERAL_INQUIRY)));

        tellers.add(new Teller("T004", "Sunita Singh",
                Set.of(ServiceType.STATEMENT_REQUEST, ServiceType.GENERAL_INQUIRY, ServiceType.EMERGENCY)));

        // General purpose teller
        tellers.add(new Teller("T005", "Ramesh Gupta",
                Set.of(ServiceType.values())));
    }

    /**
     * Add customer to appropriate queue
     * Time Complexity: O(log n) for priority queue insertion
     */
    public Customer addCustomer(String customerId, String name, CustomerCategory category,
            ServiceType serviceType, String specialRequirements) {
        synchronized (queueLock) {
            Customer customer = new Customer(customerId, name, category, serviceType, specialRequirements);
            customer.tokenNumber = nextTokenNumber++;

            // Add to appropriate service queue
            serviceQueues.get(serviceType).offer(customer);
            customerMap.put(customerId, customer);
            serviceTypeCount.put(serviceType, serviceTypeCount.get(serviceType) + 1);

            System.out.printf("✅ Customer %s added to queue (Token #%d)\n",
                    customer.name, customer.tokenNumber);

            // Try to immediately assign if teller available
            assignNextCustomer();

            return customer;
        }
    }

    /**
     * Assign next customer to available teller using intelligent matching
     * Time Complexity: O(s * t) where s = service types, t = tellers
     */
    public boolean assignNextCustomer() {
        synchronized (queueLock) {
            // Find best customer-teller match
            Customer bestCustomer = null;
            Teller bestTeller = null;
            ServiceType bestServiceType = null;
            int bestPriority = Integer.MAX_VALUE;

            // Check all service types in priority order
            for (ServiceType serviceType : ServiceType.values()) {
                PriorityQueue<Customer> queue = serviceQueues.get(serviceType);
                if (queue.isEmpty())
                    continue;

                Customer customer = queue.peek();

                // Find available teller who can handle this service
                for (Teller teller : tellers) {
                    if (teller.isAvailable && teller.canHandle(serviceType)) {
                        // Priority: service priority + customer category priority
                        int totalPriority = customer.getEffectivePriority();

                        if (totalPriority < bestPriority) {
                            bestCustomer = customer;
                            bestTeller = teller;
                            bestServiceType = serviceType;
                            bestPriority = totalPriority;
                        }
                    }
                }
            }

            // Assign best match
            if (bestCustomer != null && bestTeller != null) {
                serviceQueues.get(bestServiceType).poll();
                bestTeller.startServing(bestCustomer);

                System.out.printf("🎯 Assigned %s to %s for %s\n",
                        bestCustomer.name, bestTeller.name,
                        bestServiceType.getDescription());
                return true;
            }

            return false;
        }
    }

    /**
     * Complete service for a customer and make teller available
     */
    public void completeService(String tellerId) {
        synchronized (queueLock) {
            Teller teller = tellers.stream()
                    .filter(t -> t.tellerId.equals(tellerId))
                    .findFirst()
                    .orElse(null);

            if (teller != null && !teller.isAvailable) {
                Customer customer = teller.currentCustomer;
                teller.finishServing();

                // Update global statistics
                totalCustomersServed++;
                long waitTime = customer.getWaitingTimeMinutes();
                averageWaitTime = ((averageWaitTime * (totalCustomersServed - 1)) + waitTime) / totalCustomersServed;

                System.out.printf("✅ Service completed for %s (Wait: %d min, Service: %d min)\n",
                        customer.name, waitTime,
                        java.time.Duration.between(customer.serviceStartTime, customer.serviceEndTime).toMinutes());

                // Try to assign next customer
                assignNextCustomer();
            }
        }
    }

    /**
     * Get current queue status and statistics
     */
    public void displayQueueStatus() {
        synchronized (queueLock) {
            System.out.println("\n🏦 BANK QUEUE MANAGEMENT SYSTEM STATUS");
            System.out.println("=".repeat(60));

            // Overall statistics
            System.out.printf("📊 Overall Stats: %d customers served today | Avg wait: %.1f min\n\n",
                    totalCustomersServed, averageWaitTime);

            // Teller status
            System.out.println("👥 TELLER STATUS:");
            System.out.println("-".repeat(40));
            for (Teller teller : tellers) {
                System.out.println(teller);
                System.out.println();
            }

            // Queue status by service type
            System.out.println("📋 QUEUE STATUS BY SERVICE TYPE:");
            System.out.println("-".repeat(40));
            for (ServiceType serviceType : ServiceType.values()) {
                PriorityQueue<Customer> queue = serviceQueues.get(serviceType);
                System.out.printf("%-20s: %2d waiting | %2d served today\n",
                        serviceType.name(), queue.size(), serviceTypeCount.get(serviceType));
            }

            // Current waiting customers
            System.out.println("\n⏳ CUSTOMERS CURRENTLY WAITING:");
            System.out.println("-".repeat(40));
            boolean hasWaitingCustomers = false;

            for (ServiceType serviceType : ServiceType.values()) {
                PriorityQueue<Customer> queue = serviceQueues.get(serviceType);
                if (!queue.isEmpty()) {
                    hasWaitingCustomers = true;
                    System.out.println("🔹 " + serviceType.getDescription() + ":");
                    queue.forEach(customer -> System.out.println("   " + customer));
                    System.out.println();
                }
            }

            if (!hasWaitingCustomers) {
                System.out.println("🎉 No customers currently waiting!");
            }
        }
    }

    /**
     * Predict queue wait times for new customers
     */
    public Map<ServiceType, Integer> predictWaitTimes() {
        Map<ServiceType, Integer> waitTimes = new HashMap<>();

        for (ServiceType serviceType : ServiceType.values()) {
            int queueSize = serviceQueues.get(serviceType).size();
            int estimatedServiceTime = serviceType.getEstimatedTimeMinutes();

            // Count available tellers for this service
            long availableTellers = tellers.stream()
                    .filter(t -> t.isAvailable && t.canHandle(serviceType))
                    .count();

            // Calculate estimated wait time
            if (availableTellers > 0) {
                waitTimes.put(serviceType, (queueSize * estimatedServiceTime) / (int) availableTellers);
            } else {
                // Find next available teller
                long minWaitForTeller = tellers.stream()
                        .filter(t -> t.canHandle(serviceType))
                        .mapToLong(t -> t.isAvailable ? 0
                                : java.time.Duration.between(LocalDateTime.now(), t.availableFrom).toMinutes())
                        .min()
                        .orElse(30); // Default 30 min if no suitable teller

                waitTimes.put(serviceType, (int) minWaitForTeller + (queueSize * estimatedServiceTime));
            }
        }

        return waitTimes;
    }

    /**
     * Generate queue optimization recommendations
     */
    public void generateOptimizationRecommendations() {
        System.out.println("\n💡 QUEUE OPTIMIZATION RECOMMENDATIONS");
        System.out.println("=".repeat(60));

        Map<ServiceType, Integer> waitTimes = predictWaitTimes();

        // Identify bottlenecks
        List<ServiceType> bottlenecks = waitTimes.entrySet().stream()
                .filter(entry -> entry.getValue() > 15) // More than 15 min wait
                .map(Map.Entry::getKey)
                .toList();

        if (!bottlenecks.isEmpty()) {
            System.out.println("⚠️  IDENTIFIED BOTTLENECKS:");
            for (ServiceType serviceType : bottlenecks) {
                System.out.printf("   - %s: %d min estimated wait\n",
                        serviceType.getDescription(), waitTimes.get(serviceType));
            }

            System.out.println("\n🎯 RECOMMENDATIONS:");
            System.out.println("   1. Consider adding specialist tellers for high-wait services");
            System.out.println("   2. Implement appointment booking for complex services");
            System.out.println("   3. Use mobile app notifications to spread customer arrivals");
            System.out.println("   4. Cross-train tellers for multiple service types");
        } else {
            System.out.println("✅ All service types have acceptable wait times (<15 min)");
        }

        // Teller utilization analysis
        double avgUtilization = tellers.stream()
                .mapToDouble(t -> t.isAvailable ? 0.0 : 1.0)
                .average()
                .orElse(0.0);

        System.out.printf("\n📈 TELLER UTILIZATION: %.1f%%\n", avgUtilization * 100);

        if (avgUtilization > 0.8) {
            System.out.println("   ⚠️  High utilization - consider adding more tellers");
        } else if (avgUtilization < 0.4) {
            System.out.println("   ℹ️  Low utilization - good customer service capacity");
        }
    }

    /**
     * Test the queue management system
     */
    public void testQueueManagement() {
        System.out.println("🏦 TESTING BANK QUEUE MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));

        // Add various customers
        addCustomer("C001", "VIP Client Ambani", CustomerCategory.VIP, ServiceType.INVESTMENT_ADVISORY,
                "Private banking consultation");
        addCustomer("C002", "Senior Mr. Gupta", CustomerCategory.SENIOR_CITIZEN, ServiceType.CASH_WITHDRAWAL,
                "Joint account holder");
        addCustomer("C003", "Regular Customer Priya", CustomerCategory.REGULAR, ServiceType.ACCOUNT_OPENING,
                "Salary account opening");
        addCustomer("C004", "Pregnant Mrs. Sharma", CustomerCategory.PREGNANT_MOTHER, ServiceType.GENERAL_INQUIRY,
                "Maternity benefits inquiry");
        addCustomer("C005", "Emergency Mr. Patel", CustomerCategory.REGULAR, ServiceType.EMERGENCY,
                "Blocked card emergency");
        addCustomer("C006", "Student Rahul", CustomerCategory.REGULAR, ServiceType.LOAN_INQUIRY,
                "Education loan consultation");

        // Display initial status
        displayQueueStatus();

        // Simulate service completion
        System.out.println("\n⏳ SIMULATING SERVICE COMPLETION...\n");

        try {
            Thread.sleep(2000); // Simulate time passage
            completeService("T001"); // Complete account opening

            Thread.sleep(1000);
            completeService("T004"); // Complete emergency service

            Thread.sleep(1000);
            completeService("T002"); // Complete investment advisory

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Display final status
        displayQueueStatus();
        generateOptimizationRecommendations();
    }

    public static void main(String[] args) {
        System.out.println("🏦 KOTAK BANK SDE3 INTERVIEW QUESTION");
        System.out.println("📋 Problem: Banking Queue Management System");
        System.out.println("🔗 Domain: Operations Research + Priority Queues + Resource Allocation");
        System.out.println();

        BankingQueueManager queueManager = new BankingQueueManager();
        queueManager.testQueueManagement();

        System.out.println("\n💡 INTERVIEW DISCUSSION POINTS:");
        System.out.println("1. Priority queue implementation and customer categorization");
        System.out.println("2. Real-time resource allocation and load balancing");
        System.out.println("3. Service time prediction and queue optimization");
        System.out.println("4. Integration with mobile apps for virtual queuing");
        System.out.println("5. Handling emergency situations and SLA management");
        System.out.println("6. Analytics and reporting for operational insights");
        System.out.println("7. Scalability across multiple branches");
        System.out.println("8. Machine learning for demand forecasting");
    }
}
