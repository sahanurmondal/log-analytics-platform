# Advanced Java SDE Interview Prep Guide

This guide covers 200+ advanced Java interview questions with detailed explanations and code examples. Topics include collections internals, concurrency, core Java, JVM, libraries, and patterns essential for SDE interviews.

---

## 1. Java Collections Framework

### HashMap, HashSet, LinkedHashMap, TreeMap, ConcurrentHashMap

**Q1: How does HashMap work internally?**  
*A:* HashMap uses an array of buckets. Each bucket is a linked list or tree (Java 8+). The key's `hashCode()` determines the bucket index (`hash & (n-1)`). Collisions are handled by chaining (linked list) or treeification (red-black tree if >8 elements).  
*Example:*  
```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1); // "a".hashCode() determines bucket
```

**Q2: What is the load factor in HashMap?**  
*A:* The load factor (default 0.75) determines when to resize. When size exceeds capacity * load factor, the table is doubled and all entries are rehashed.  
*Example:*  
```java
Map<Integer, String> map = new HashMap<>(16, 0.5f); // custom load factor
```

**Q3: How does HashSet work internally?**  
*A:* HashSet is backed by a HashMap. The set elements are stored as keys in the map, with a dummy value (`PRESENT`).  
*Example:*  
```java
Set<String> set = new HashSet<>();
set.add("foo"); // Internally: map.put("foo", PRESENT)
```

**Q4: What is the difference between HashMap and LinkedHashMap?**  
*A:* LinkedHashMap maintains insertion order using a doubly-linked list. HashMap does not.  
*Example:*  
```java
Map<Integer, String> map = new LinkedHashMap<>();
map.put(2, "b"); map.put(1, "a");
System.out.println(map.keySet()); // [2, 1]
```

**Q5: How does TreeMap maintain order?**  
*A:* TreeMap uses a Red-Black tree. Keys must be Comparable or a Comparator must be provided. Entries are always sorted.  
*Example:*  
```java
Map<String, Integer> map = new TreeMap<>();
map.put("b", 2); map.put("a", 1);
System.out.println(map.keySet()); // [a, b]
```

**Q6: What is the time complexity of get/put in HashMap and TreeMap?**  
*A:* HashMap: O(1) average, O(n) worst-case (all keys collide). TreeMap: O(log n) for all operations.  
*Example:*  
```java
map.get("key"); // O(1) in HashMap, O(log n) in TreeMap
```

**Q7: How does ConcurrentHashMap achieve thread safety?**  
*A:* Uses lock striping (Java 7) or CAS and synchronized blocks on bins (Java 8+). No global lock, so multiple threads can update different buckets concurrently.  
*Example:*  
```java
ConcurrentHashMap<String, Integer> cmap = new ConcurrentHashMap<>();
cmap.put("x", 1);
```

**Q8: Why are null keys/values not allowed in ConcurrentHashMap?**  
*A:* To avoid ambiguity between a null value and absence of a key, and to prevent NullPointerException in concurrent operations.

**Q9: What is the difference between HashTable and ConcurrentHashMap?**  
*A:* HashTable synchronizes every method (coarse-grained), ConcurrentHashMap uses fine-grained locking for better concurrency. HashTable allows neither null keys nor values.

**Q10: How does CopyOnWriteArrayList work?**  
*A:* On mutation, it copies the entire array. Reads are fast and thread-safe; writes are expensive. Best for read-heavy, write-light scenarios.  
*Example:*  
```java
CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
list.add(1); // creates a new copy of the array
```

---

## 2. ArrayList, LinkedList, Stack, Queue, Deque

**Q11: How does ArrayList grow?**  
*A:* When full, it creates a new array of 1.5x size and copies elements.  
*Example:*  
```java
ArrayList<Integer> list = new ArrayList<>();
for (int i = 0; i < 20; i++) list.add(i); // triggers resize
```

**Q12: What is the difference between ArrayList and Vector?**  
*A:* Vector is synchronized, ArrayList is not. Vector doubles size, ArrayList grows by 50%. Vector is legacy.  
*Example:*  
```java
Vector<Integer> v = new Vector<>();
ArrayList<Integer> a = new ArrayList<>();
```

**Q13: When to use LinkedList over ArrayList?**  
*A:* When frequent insertions/deletions at head/tail are needed. Random access is slow (O(n)).  
*Example:*  
```java
LinkedList<Integer> ll = new LinkedList<>();
ll.addFirst(1); ll.addLast(2);
```

**Q14: How is Stack implemented in Java?**  
*A:* Stack extends Vector. It is synchronized and considered legacy. Use Deque for stack operations in modern code.  
*Example:*  
```java
Stack<Integer> stack = new Stack<>();
stack.push(1); stack.pop();
```

**Q15: What is Deque?**  
*A:* Double-ended queue. Implemented by ArrayDeque and LinkedList. Supports insertion/removal at both ends.  
*Example:*  
```java
Deque<Integer> dq = new ArrayDeque<>();
dq.addFirst(1); dq.addLast(2);
```

**Q16: How does PriorityQueue work?**  
*A:* Backed by a binary heap. Elements are ordered by natural order or Comparator.  
*Example:*  
```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.add(3); pq.add(1); pq.add(2);
System.out.println(pq.poll()); // 1
```

**Q17: Can you remove elements during iteration?**  
*A:* Use Iterator's remove() method to avoid ConcurrentModificationException.  
*Example:*  
```java
Iterator<Integer> it = list.iterator();
while (it.hasNext()) {
  if (it.next() == 2) it.remove();
}
```

**Q18: What is fail-fast vs fail-safe iterator?**  
*A:* Fail-fast throws ConcurrentModificationException on modification (e.g., ArrayList). Fail-safe works on a copy (e.g., CopyOnWriteArrayList), so no exception but changes are not seen by the iterator.

---

## 3. Java Concurrency & Multithreading

### Thread, Runnable, Callable, Future

**Q19: How do you create a thread in Java?**  
*A:* Extend Thread or implement Runnable/Callable.  
*Example:*  
```java
new Thread(() -> System.out.println("Hello")).start();
```

**Q20: What is the difference between Runnable and Callable?**  
*A:* Runnable returns void, cannot throw checked exceptions. Callable returns a value and can throw checked exceptions.  
*Example:*  
```java
Callable<Integer> c = () -> 42;
```

**Q21: What is Future?**  
*A:* Represents the result of an async computation. Use get() to retrieve result, which blocks if not ready.  
*Example:*  
```java
Future<Integer> f = executor.submit(() -> 1+1);
Integer result = f.get();
```

**Q22: What is ExecutorService?**  
*A:* Manages a pool of threads. Use submit() for tasks, shutdown() to stop.  
*Example:*  
```java
ExecutorService es = Executors.newFixedThreadPool(2);
es.submit(() -> System.out.println("Task"));
es.shutdown();
```

**Q23: What is ThreadPoolExecutor?**  
*A:* Core implementation of ExecutorService. Allows tuning of pool size, queue, rejection policy.  
*Example:*  
```java
ThreadPoolExecutor tpe = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
```

**Q24: What is ScheduledExecutorService?**  
*A:* Schedules tasks to run after a delay or periodically.  
*Example:*  
```java
ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
ses.schedule(() -> System.out.println("Delayed"), 1, TimeUnit.SECONDS);
```

**Q25: What is ForkJoinPool?**  
*A:* Executes tasks using work-stealing. Used for parallel streams and recursive tasks.  
*Example:*  
```java
ForkJoinPool.commonPool().submit(() -> System.out.println("ForkJoin"));
```

**Q26: What is the difference between start() and run()?**  
*A:* start() creates a new thread; run() executes in the current thread.  
*Example:*  
```java
Thread t = new Thread(() -> {});
t.start(); // new thread
t.run();   // runs in current thread
```

**Q27: What is thread starvation?**  
*A:* When threads are unable to gain regular access to resources and can't make progress, often due to thread priorities or locks.

**Q28: What is thread priority?**  
*A:* Hint to the scheduler. Not guaranteed to be honored.  
*Example:*  
```java
Thread t = new Thread();
t.setPriority(Thread.MAX_PRIORITY);
```

---

### Synchronization, Locks, Volatile, Atomic

**Q29: What does the synchronized keyword do?**  
*A:* Acquires a monitor lock on an object or class. Only one thread can execute a synchronized block/method at a time for the same object.  
*Example:*  
```java
synchronized(this) { /* critical section */ }
```

**Q30: What is intrinsic lock?**  
*A:* The lock associated with every Java object, used by synchronized. Also called monitor lock.

**Q31: What is volatile?**  
*A:* Ensures visibility of changes to variables across threads. Does not guarantee atomicity.  
*Example:*  
```java
volatile boolean running = true;
```

**Q32: What is the difference between synchronized and volatile?**  
*A:* synchronized provides mutual exclusion and visibility; volatile only visibility. Use volatile for flags, synchronized for compound actions.

**Q33: What is ReentrantLock?**  
*A:* A lock that can be acquired multiple times by the same thread. Supports fairness, tryLock, and condition variables.  
*Example:*  
```java
ReentrantLock lock = new ReentrantLock();
lock.lock();
try { /* critical section */ } finally { lock.unlock(); }
```

**Q34: What is ReadWriteLock?**  
*A:* Allows multiple readers or one writer at a time.  
*Example:*  
```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();
rwLock.readLock().lock();
```

**Q35: What is StampedLock?**  
*A:* Supports optimistic, read, and write locks for better concurrency.  
*Example:*  
```java
StampedLock lock = new StampedLock();
long stamp = lock.readLock();
```

**Q36: What are atomic classes?**  
*A:* Classes like AtomicInteger, AtomicReference use CAS for lock-free thread-safe operations.  
*Example:*  
```java
AtomicInteger ai = new AtomicInteger(0);
ai.incrementAndGet();
```

**Q37: What is CAS (Compare-And-Swap)?**  
*A:* An atomic instruction to update a variable if it matches an expected value. Used in atomic classes for lock-free concurrency.

**Q38: What is deadlock?**  
*A:* Two or more threads waiting forever for each other to release locks.  
*Example:*  
```java
// Thread 1: lock(a), lock(b)
// Thread 2: lock(b), lock(a)
```

**Q39: How to prevent deadlock?**  
*A:* Lock ordering, lock timeout, avoiding nested locks, using tryLock.

**Q40: What is livelock?**  
*A:* Threads keep changing state in response to each other but make no progress (e.g., both threads repeatedly yielding).

**Q41: What is starvation?**  
*A:* A thread never gets CPU or resources, often due to thread priorities or unfair locks.

**Q42: What is a race condition?**  
*A:* Two threads access shared data and try to change it at the same time, leading to inconsistent results.

**Q43: What is ThreadLocal?**  
*A:* Provides thread-local variables. Each thread has its own value.  
*Example:*  
```java
ThreadLocal<Integer> tl = ThreadLocal.withInitial(() -> 0);
```

**Q44: What is the difference between wait() and sleep()?**  
*A:* wait() releases the lock and waits for notify; sleep() just pauses the thread and does not release any locks.

**Q45: What is notify() vs notifyAll()?**  
*A:* notify() wakes one waiting thread; notifyAll() wakes all waiting threads on the object's monitor.

**Q46: What is a daemon thread?**  
*A:* Runs in the background. JVM exits when only daemon threads remain.  
*Example:*  
```java
Thread t = new Thread();
t.setDaemon(true);
```

**Q47: How to stop a thread safely?**  
*A:* Use a volatile boolean flag or interrupt mechanism. Avoid using Thread.stop().

**Q48: What is the difference between interrupt() and stop()?**  
*A:* interrupt() sets the interrupt flag; stop() is unsafe and deprecated as it can leave objects in inconsistent state.

---

### Blocking Queues, Concurrent Collections

**Q49: What is BlockingQueue?**  
*A:* Thread-safe queue supporting operations that wait for the queue to become non-empty/full. Used in producer-consumer scenarios.  
*Example:*  
```java
BlockingQueue<Integer> q = new ArrayBlockingQueue<>(10);
```

**Q50: Name implementations of BlockingQueue.**  
*A:* ArrayBlockingQueue, LinkedBlockingQueue, PriorityBlockingQueue, DelayQueue, SynchronousQueue.

**Q51: What is SynchronousQueue?**  
*A:* A queue with zero capacity. Each insert must wait for a remove. Used for handoff designs.

**Q52: What is DelayQueue?**  
*A:* Elements become available after a delay. Used for scheduling tasks.

**Q53: What is ConcurrentLinkedQueue?**  
*A:* A lock-free, thread-safe queue based on linked nodes. Suitable for high-concurrency scenarios.

**Q54: What is ConcurrentSkipListMap?**  
*A:* A concurrent, sorted map based on skip list. Provides O(log n) operations and thread safety.

**Q55: What is CopyOnWriteArrayList?**  
*A:* Thread-safe list where all mutative operations make a fresh copy. Good for read-heavy, write-light use cases.

---

## 4. Core Java Concepts

**Q56: What is the difference between == and equals()?**  
*A:* == compares references (memory addresses); equals() compares values/content.  
*Example:*  
```java
String a = "foo", b = new String("foo");
System.out.println(a == b); // false
System.out.println(a.equals(b)); // true
```

**Q57: What is hashCode()?**  
*A:* Returns an int hash code. Equal objects must have equal hash codes. Used in hash-based collections.

**Q58: What is the contract between equals() and hashCode()?**  
*A:* If two objects are equal, they must have the same hash code. If hash codes differ, objects are not equal.

**Q59: What is the difference between abstract class and interface?**  
*A:* Abstract class can have state (fields) and implemented methods; interface cannot (until Java 8 default and static methods). A class can implement multiple interfaces but only extend one class.

**Q60: What is a functional interface?**  
*A:* An interface with a single abstract method. Used for lambdas and method references.  
*Example:*  
```java
@FunctionalInterface
interface MyFunc { void run(); }
```

**Q61: What is marker interface?**  
*A:* An interface with no methods (e.g., Serializable, Cloneable). Used to mark classes for special treatment by JVM or libraries.

**Q62: What is the difference between checked and unchecked exceptions?**  
*A:* Checked must be declared or caught; unchecked (RuntimeException) do not.  
*Example:*  
```java
try { throw new IOException(); } catch (IOException e) {}
```

**Q63: What is try-with-resources?**  
*A:* Automatically closes resources implementing AutoCloseable.  
*Example:*  
```java
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) { ... }
```

**Q64: What is the difference between final, finally, and finalize()?**  
*A:* final: constant or non-overridable; finally: block after try-catch; finalize(): called by GC before object is reclaimed (deprecated).

**Q65: What is the difference between static and instance methods?**  
*A:* static belongs to the class; instance to the object. Static methods cannot access instance variables.

**Q66: What is method overloading and overriding?**  
*A:* Overloading: same method name, different parameters (compile-time). Overriding: subclass redefines superclass method (runtime).

**Q67: What is covariant return type?**  
*A:* Overridden method can return a subtype of the original return type.

**Q68: What is the difference between shallow and deep copy?**  
*A:* Shallow: copies references; deep: copies objects recursively.  
*Example:*  
```java
List<Integer> a = new ArrayList<>(List.of(1,2));
List<Integer> b = new ArrayList<>(a); // shallow copy
```

**Q69: What is serialization?**  
*A:* Converting an object to a byte stream for storage or transmission.

**Q70: What is transient keyword?**  
*A:* Prevents a field from being serialized.  
*Example:*  
```java
transient int temp;
```

**Q71: What is the difference between String, StringBuilder, and StringBuffer?**  
*A:* String is immutable; StringBuilder is mutable and not thread-safe; StringBuffer is mutable and thread-safe.

**Q72: What is the string pool?**  
*A:* JVM maintains a pool of unique string literals for memory efficiency.  
*Example:*  
```java
String a = "foo"; String b = "foo"; // a == b is true
```

**Q73: What is autoboxing and unboxing?**  
*A:* Automatic conversion between primitives and wrapper classes.  
*Example:*  
```java
Integer i = 5; // autoboxing
int j = i;     // unboxing
```

**Q74: What is the difference between == and equals() for Integer?**  
*A:* == compares references; equals() compares values. Integer values between -128 and 127 are cached, so == may be true for small values.

**Q75: What is enum in Java?**  
*A:* Special class representing a group of constants.  
*Example:*  
```java
enum Color { RED, GREEN, BLUE }
```

---

## 5. Java 8+ Features

**Q76: What are lambda expressions?**  
*A:* Anonymous functions for functional programming.  
*Example:*  
```java
Runnable r = () -> System.out.println("Hello");
```

**Q77: What is a stream?**  
*A:* Sequence of elements supporting aggregate operations (map, filter, reduce).  
*Example:*  
```java
List<Integer> l = List.of(1,2,3);
l.stream().map(x -> x*2).forEach(System.out::println);
```

**Q78: What is Optional?**  
*A:* Container for nullable values to avoid NullPointerException.  
*Example:*  
```java
Optional<String> o = Optional.ofNullable(null);
o.ifPresent(System.out::println);
```

**Q79: What is method reference?**  
*A:* Shorthand for lambda expressions calling a method.  
*Example:*  
```java
list.forEach(System.out::println);
```

**Q80: What is a default method in interface?**  
*A:* Method with a body in an interface (Java 8+).  
*Example:*  
```java
interface I { default void foo() { System.out.println("foo"); } }
```

**Q81: What is a static method in interface?**  
*A:* Static method defined in an interface.  
*Example:*  
```java
interface I { static void bar() { System.out.println("bar"); } }
```

**Q82: What is the difference between map and flatMap?**  
*A:* map transforms each element; flatMap flattens nested structures.  
*Example:*  
```java
List<List<Integer>> l = List.of(List.of(1,2), List.of(3));
l.stream().flatMap(List::stream).forEach(System.out::println);
```

**Q83: How do you filter a stream?**  
*A:* Use filter(predicate).  
*Example:*  
```java
list.stream().filter(x -> x > 1).forEach(System.out::println);
```

**Q84: How do you sort a stream?**  
*A:* Use sorted() or sorted(Comparator).  
*Example:*  
```java
list.stream().sorted().forEach(System.out::println);
```

**Q85: What is Collectors.toList()?**  
*A:* Collects stream elements into a List.  
*Example:*  
```java
List<Integer> out = list.stream().collect(Collectors.toList());
```

**Q86: What is parallelStream()?**  
*A:* Processes stream in parallel using ForkJoinPool.  
*Example:*  
```java
list.parallelStream().forEach(System.out::println);
```

**Q87: What is the difference between findFirst and findAny?**  
*A:* findFirst returns the first element; findAny may return any element (useful in parallel streams).

**Q88: What is the purpose of forEachOrdered()?**  
*A:* Maintains encounter order in parallel streams.

**Q89: What is the difference between reduce and collect?**  
*A:* reduce combines elements into a single result; collect is for mutable reduction (e.g., collecting into a collection).

---

## 6. JVM Internals

**Q90: What is the JVM?**  
*A:* Java Virtual Machine, runs Java bytecode. Platform-independent execution.

**Q91: What is JIT compilation?**  
*A:* Just-In-Time compiler translates bytecode to native code at runtime for performance.

**Q92: What is the difference between stack and heap memory?**  
*A:* Stack: method frames, local variables (thread-local). Heap: objects (shared).

**Q93: What are JVM garbage collectors?**  
*A:* Serial, Parallel, CMS, G1, ZGC, Shenandoah. Each has different trade-offs for throughput, latency, and pause times.

**Q94: What is the Young and Old Generation?**  
*A:* Young: new objects, frequent GC. Old: long-lived objects, less frequent GC.

**Q95: What is a memory leak in Java?**  
*A:* Unused objects are still referenced, so GC can't reclaim them, leading to OutOfMemoryError.

**Q96: What is PermGen/Metaspace?**  
*A:* PermGen (pre-Java 8) stored class metadata. Metaspace (Java 8+) replaces it, grows dynamically.

**Q97: What are class loaders?**  
*A:* Load classes into JVM. Types: Bootstrap, Extension, Application. Custom class loaders enable dynamic loading.

**Q98: What is the difference between ClassNotFoundException and NoClassDefFoundError?**  
*A:* ClassNotFoundException: class not found at runtime. NoClassDefFoundError: class was present at compile time but missing at runtime.

**Q99: What is reflection?**  
*A:* Inspect and modify classes, methods, fields at runtime.  
*Example:*  
```java
Class<?> c = Class.forName("java.lang.String");
Method m = c.getMethod("length");
```

**Q100: What is annotation processing?**  
*A:* Processing annotations at compile time or runtime, e.g., for code generation or validation.

---

## 7. Design Patterns and Libraries

**Q101: What is the Singleton pattern?**  
*A:* Ensures only one instance of a class exists.  
*Example:*  
```java
public enum Singleton { INSTANCE; }
```

**Q102: How do you implement thread-safe Singleton?**  
*A:* Use enum, double-checked locking, or static inner class.  
*Example:*  
```java
class S {
  private static volatile S instance;
  public static S getInstance() {
    if (instance == null) {
      synchronized(S.class) {
        if (instance == null) instance = new S();
      }
    }
    return instance;
  }
}
```

**Q103: What is the Factory pattern?**  
*A:* Creates objects without exposing instantiation logic.  
*Example:*  
```java
interface Shape { }
class Circle implements Shape { }
class ShapeFactory { Shape create(String t) { return new Circle(); } }
```

**Q104: What is the Builder pattern?**  
*A:* Builds complex objects step by step.  
*Example:*  
```java
class Person {
  static class Builder { /* ... */ }
}
```

**Q105: What is the Observer pattern?**  
*A:* Notifies observers of state changes.  
*Example:*  
```java
Observable obs = new Observable();
obs.addObserver((o,arg) -> System.out.println(arg));
```

**Q106: What is the Strategy pattern?**  
*A:* Selects algorithm at runtime.  
*Example:*  
```java
interface SortStrategy { void sort(List<Integer> l); }
```

**Q107: What is the Decorator pattern?**  
*A:* Adds behavior to objects dynamically.  
*Example:*  
```java
InputStream in = new BufferedInputStream(new FileInputStream("f"));
```

**Q108: What is the Adapter pattern?**  
*A:* Converts interface of a class into another interface.

**Q109: What is the Proxy pattern?**  
*A:* Provides a surrogate for another object, controlling access.

**Q110: What is the Template Method pattern?**  
*A:* Defines skeleton of an algorithm, deferring steps to subclasses.

**Q111: What is the Command pattern?**  
*A:* Encapsulates a request as an object.

**Q112: What is the Dependency Injection pattern?**  
*A:* Injects dependencies rather than creating them. Used in frameworks like Spring.

**Q113: What is the difference between composition and inheritance?**  
*A:* Composition: has-a relationship; Inheritance: is-a relationship.

**Q114: What is the difference between aggregation and composition?**  
*A:* Composition: strong ownership; Aggregation: weak association.

**Q115: What is the difference between shallow and deep copy in design patterns?**  
*A:* Shallow: copies references; Deep: copies objects recursively.

---

## 8. Java Libraries and APIs

**Q116: What is the Java Stream API?**  
*A:* Provides functional-style operations on streams of elements.

**Q117: What is the Java NIO package?**  
*A:* Non-blocking I/O, channels, selectors, buffers for scalable I/O.

**Q118: What is the difference between FileInputStream and FileReader?**  
*A:* FileInputStream reads bytes; FileReader reads characters.

**Q119: What is the difference between InputStream and Reader?**  
*A:* InputStream: byte streams; Reader: character streams.

**Q120: What is the Java Reflection API?**  
*A:* Allows inspection and modification of classes at runtime.

**Q121: What is the Java Serialization API?**  
*A:* Converts objects to byte streams and vice versa.

**Q122: What is the Java Logging API?**  
*A:* Provides logging capabilities (java.util.logging).

**Q123: What is the Java Regular Expression API?**  
*A:* Pattern and Matcher classes for regex.

**Q124: What is the Java Date and Time API (java.time)?**  
*A:* Modern date/time API introduced in Java 8.

**Q125: What is the difference between LocalDate, LocalTime, and LocalDateTime?**  
*A:* LocalDate: date only; LocalTime: time only; LocalDateTime: both.

**Q126: What is the Java Optional class?**  
*A:* Container for nullable values.

**Q127: What is the Java CompletableFuture API?**  
*A:* Asynchronous programming with futures and callbacks.

**Q128: What is the Java ForkJoinPool?**  
*A:* Pool for parallel task execution.

**Q129: What is the Java ServiceLoader?**  
*A:* Loads service providers at runtime.

**Q130: What is the Java SPI (Service Provider Interface)?**  
*A:* Allows pluggable implementations.

---

## 9. Advanced Concurrency Models

**Q131: What is the actor model?**  
*A:* Concurrency model where actors communicate via messages.

**Q132: What is the reactive programming model?**  
*A:* Asynchronous data streams and propagation of change.

**Q133: What is the difference between blocking and non-blocking I/O?**  
*A:* Blocking waits for operation to complete; non-blocking returns immediately.

**Q134: What is the difference between parallelism and concurrency?**  
*A:* Parallelism: tasks run simultaneously; Concurrency: tasks make progress independently.

**Q135: What is the difference between thread-safe and immutable?**  
*A:* Thread-safe: safe for concurrent use; Immutable: state cannot change after creation.

**Q136: What is the difference between optimistic and pessimistic locking?**  
*A:* Optimistic: assumes no conflict, checks at commit; Pessimistic: locks resources up front.

**Q137: What is the difference between busy-waiting and blocking?**  
*A:* Busy-waiting: repeatedly checks a condition; Blocking: waits for a condition.

**Q138: What is the difference between semaphore and mutex?**  
*A:* Semaphore: permits multiple threads; Mutex: only one thread.

**Q139: What is a countdown latch?**  
*A:* Synchronization aid that allows threads to wait until a set of operations complete.

**Q140: What is a cyclic barrier?**  
*A:* Allows a set of threads to wait for each other to reach a common barrier point.

**Q141: What is Phaser?**  
*A:* More flexible barrier for synchronizing threads in phases.

**Q142: What is Exchanger?**  
*A:* Allows two threads to exchange objects at a synchronization point.

**Q143: What is the difference between ThreadPoolExecutor and ScheduledThreadPoolExecutor?**  
*A:* ScheduledThreadPoolExecutor supports delayed and periodic tasks.

**Q144: What is the difference between invokeAll and invokeAny?**  
*A:* invokeAll waits for all tasks; invokeAny returns when one completes.

**Q145: What is the difference between Future and CompletableFuture?**  
*A:* CompletableFuture supports chaining and non-blocking callbacks.

---

## 10. Java Memory Model and Performance

**Q146: What is the Java Memory Model (JMM)?**  
*A:* Defines how threads interact through memory and what behaviors are allowed.

**Q147: What is false sharing?**  
*A:* Multiple threads modify variables on the same cache line, causing performance degradation.

**Q148: What is escape analysis?**  
*A:* JVM optimization to allocate objects on the stack if they don't escape the method.

**Q149: What is object pooling?**  
*A:* Reusing objects to reduce allocation overhead.

**Q150: What is the difference between strong, soft, weak, and phantom references?**  
*A:* Strong: normal reference; Soft: GC when memory is low; Weak: GC at next cycle; Phantom: used for cleanup after GC.

**Q151: What is OutOfMemoryError?**  
*A:* Thrown when JVM cannot allocate memory.

**Q152: What is StackOverflowError?**  
*A:* Thrown when stack memory is exhausted (e.g., deep recursion).

**Q153: What is the difference between heap dump and thread dump?**  
*A:* Heap dump: snapshot of memory; Thread dump: snapshot of thread states.

**Q154: What is the use of -Xmx and -Xms JVM options?**  
*A:* Set max and initial heap size.

**Q155: What is the use of -XX:+UseG1GC?**  
*A:* Enables G1 garbage collector.

**Q156: What is the use of -XX:MaxPermSize?**  
*A:* Sets max PermGen size (pre-Java 8).

**Q157: What is the use of -XX:MetaspaceSize?**  
*A:* Sets initial Metaspace size (Java 8+).

**Q158: What is the use of -XX:+PrintGCDetails?**  
*A:* Prints detailed GC logs.

**Q159: What is the use of -XX:+HeapDumpOnOutOfMemoryError?**  
*A:* Dumps heap on OOM.

**Q160: What is the use of jvisualvm/jconsole?**  
*A:* JVM monitoring and profiling tools.

---

## 11. Miscellaneous and Best Practices

**Q161: What is the difference between shallow and deep immutability?**  
*A:* Shallow: only top-level fields are final; Deep: all nested objects are immutable.

**Q162: What is defensive copying?**  
*A:* Copying mutable objects before exposing them.

**Q163: What is the difference between composition and inheritance?**  
*A:* Composition: has-a; Inheritance: is-a.

**Q164: What is the difference between static and dynamic binding?**  
*A:* Static: resolved at compile time; Dynamic: at runtime.

**Q165: What is method hiding?**  
*A:* Static methods with same signature in subclass hide superclass methods.

**Q166: What is the diamond problem?**  
*A:* Ambiguity from multiple inheritance (resolved in Java by interfaces).

**Q167: What is the difference between public, protected, private, and default access?**  
*A:* public: everywhere; protected: package + subclass; private: class only; default: package only.

**Q168: What is the difference between package-private and protected?**  
*A:* Package-private: only package; Protected: package + subclass.

**Q169: What is the difference between static import and normal import?**  
*A:* static import imports static members.

**Q170: What is the difference between assert and if?**  
*A:* assert is for debugging; if is for logic.

**Q171: What is the difference between System.out and System.err?**  
*A:* out: standard output; err: standard error.

**Q172: What is the difference between process and thread?**  
*A:* Process: independent program; Thread: unit of execution within a process.

**Q173: What is the difference between JVM, JRE, and JDK?**  
*A:* JVM: runs bytecode; JRE: JVM + libraries; JDK: JRE + development tools.

**Q174: What is the difference between compile-time and runtime polymorphism?**  
*A:* Compile-time: method overloading; Runtime: method overriding.

**Q175: What is the difference between upcasting and downcasting?**  
*A:* Upcasting: subclass to superclass; Downcasting: superclass to subclass.

**Q176: What is the difference between instanceof and getClass()?**  
*A:* instanceof checks type hierarchy; getClass() checks exact class.

**Q177: What is the difference between this and super?**  
*A:* this: current object; super: superclass.

**Q178: What is the difference between static block and instance block?**  
*A:* static: runs once when class loads; instance: runs before constructor.

**Q179: What is the difference between constructor and factory method?**  
*A:* Constructor: creates new instance; Factory: can return existing or subclass instance.

**Q180: What is the difference between equals() and compareTo()?**  
*A:* equals(): equality; compareTo(): ordering.

**Q181: What is the difference between Comparator and Comparable?**  
*A:* Comparable: natural order; Comparator: custom order.

**Q182: What is the difference between hashCode() and identityHashCode()?**  
*A:* hashCode(): overridden; identityHashCode(): default object hash.

**Q183: What is the difference between finalize() and AutoCloseable?**  
*A:* finalize(): called by GC; AutoCloseable: explicit resource cleanup.

**Q184: What is the difference between try-catch-finally and try-with-resources?**  
*A:* try-with-resources auto-closes resources.

**Q185: What is the difference between throw and throws?**  
*A:* throw: throws an exception; throws: declares exception.

**Q186: What is the difference between error and exception?**  
*A:* Error: serious problems (e.g., OOM); Exception: application errors.

**Q187: What is the difference between checked and unchecked exception?**  
*A:* Checked: must be handled; Unchecked: runtime.

**Q188: What is the difference between stack trace and cause?**  
*A:* Stack trace: call stack; cause: underlying exception.

**Q189: What is the difference between process builder and runtime exec?**  
*A:* ProcessBuilder is preferred for starting external processes.

**Q190: What is the difference between Class.forName and ClassLoader.loadClass?**  
*A:* Class.forName initializes class; loadClass does not.

**Q191: What is the difference between soft reference and weak reference?**  
*A:* Soft: GC when memory low; Weak: GC at next cycle.

**Q192: What is the difference between PhantomReference and WeakReference?**  
*A:* Phantom: enqueued after GC, cannot get referent; Weak: can get referent until GC.

**Q193: What is the difference between ArrayList and Arrays.asList()?**  
*A:* Arrays.asList returns a fixed-size list backed by array.

**Q194: What is the difference between Collections.unmodifiableList and List.of()?**  
*A:* Both are immutable, but List.of() is more efficient and null-hostile.  
*Explanation:* `Collections.unmodifiableList(list)` wraps an existing list to prevent modification, but changes to the underlying list are still reflected. `List.of()` creates a truly immutable list that does not allow nulls and is more memory-efficient.  
*Example:*  
```java
List<Integer> l1 = Collections.unmodifiableList(new ArrayList<>(List.of(1,2)));
List<Integer> l2 = List.of(1,2);
```

**Q195: What is the difference between Optional.of and Optional.ofNullable?**  
*A:* of: non-null; ofNullable: allows null.  
*Explanation:* `Optional.of(value)` throws NullPointerException if value is null, enforcing non-null contracts. `Optional.ofNullable(value)` returns an empty Optional if value is null, making it safer for uncertain values.  
*Example:*  
```java
Optional<String> o1 = Optional.of("foo"); // OK
Optional<String> o2 = Optional.ofNullable(null); // Optional.empty
```

**Q196: What is the difference between map and flatMap in Optional?**  
*A:* map: transforms value; flatMap: flattens nested Optionals.  
*Explanation:* `map` applies a function to the value if present and wraps the result in an Optional. `flatMap` is used when the function itself returns an Optional, avoiding nested Optionals.  
*Example:*  
```java
Optional<String> o = Optional.of("foo");
Optional<Integer> len = o.map(String::length); // Optional<Integer>
Optional<String> upper = o.flatMap(s -> Optional.of(s.toUpperCase()));
```

**Q197: What is the difference between filter and removeIf?**  
*A:* filter: streams; removeIf: collections.  
*Explanation:* `filter` is a non-destructive operation on streams that returns a new stream with elements matching a predicate. `removeIf` modifies the collection in place by removing elements that match the predicate.  
*Example:*  
```java
list.stream().filter(x -> x > 1); // returns a new stream
list.removeIf(x -> x > 1); // modifies the list
```

**Q198: What is the difference between peek and map in streams?**  
*A:* peek: for debugging; map: transforms elements.  
*Explanation:* `peek` is an intermediate operation used for debugging or performing actions without modifying the stream elements. `map` transforms each element to another value.  
*Example:*  
```java
list.stream().peek(System.out::println).map(x -> x*2);
```

**Q199: What is the difference between collect and reduce?**  
*A:* collect: mutable reduction; reduce: combines elements.  
*Explanation:* `reduce` combines stream elements into a single result using an associative function (e.g., sum). `collect` is used for mutable reduction, such as accumulating elements into a collection.  
*Example:*  
```java
int sum = list.stream().reduce(0, Integer::sum);
List<Integer> out = list.stream().collect(Collectors.toList());
```

**Q200: What is the difference between parallelStream and stream?**  
*A:* parallelStream uses multiple threads; stream is sequential.  
*Explanation:* `stream()` processes elements sequentially in a single thread, while `parallelStream()` splits the workload across multiple threads using the ForkJoinPool, potentially improving performance for large datasets but with overhead and non-deterministic order.  
*Example:*  
```java
list.parallelStream().forEach(System.out::println);
```

---

## 12. Advanced Multithreading and Concurrency (Additional)

**Q201: How does ForkJoinPool work internally?**  
*A:* ForkJoinPool uses a work-stealing algorithm where each worker thread has its own deque (double-ended queue) of tasks. When a thread runs out of tasks, it "steals" tasks from other threads' deques, improving load balancing and parallelism. It's optimized for tasks that can be recursively split into subtasks (fork) and then joined for results.  
*Example:*  
```java
ForkJoinPool pool = new ForkJoinPool();
pool.submit(() -> IntStream.range(0, 1000).parallel().sum());
```

**Q202: What is the difference between ForkJoinTask, RecursiveTask, and RecursiveAction?**  
*A:* `ForkJoinTask` is the base class for tasks executed in ForkJoinPool. `RecursiveTask` returns a result, while `RecursiveAction` does not. Both are used for divide-and-conquer parallelism.  
*Example:*  
```java
class SumTask extends RecursiveTask<Integer> { ... }
```

**Q203: What is ThreadLocal and how does it work internally?**  
*A:* `ThreadLocal` provides variables that are isolated to the current thread. Internally, each thread maintains a map of ThreadLocal variables, ensuring no cross-thread interference. Useful for user sessions, database connections, or per-thread caches.  
*Example:*  
```java
ThreadLocal<Integer> tl = ThreadLocal.withInitial(() -> 0);
tl.set(42); // only visible to current thread
```

**Q204: How does ThreadPoolExecutor manage threads and tasks?**  
*A:* `ThreadPoolExecutor` maintains a pool of worker threads and a task queue. When a task is submitted, it's either executed immediately (if threads are available), queued, or rejected (if the queue is full and max threads reached). It supports core/max pool size, keep-alive time, and rejection policies.  
*Example:*  
```java
ThreadPoolExecutor tpe = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
```

**Q205: What is the internal structure of ConcurrentHashMap in Java 8+?**  
*A:* In Java 8+, `ConcurrentHashMap` uses an array of bins, where each bin is a linked list or tree. Updates to bins use CAS (Compare-And-Swap) for the first node and synchronized blocks for further nodes. This allows high concurrency with minimal locking.  
*Explanation:* The map avoids global locking, so multiple threads can update different bins concurrently. Tree bins are used for high-collision scenarios for better performance.

**Q206: How does CopyOnWriteArrayList ensure thread safety?**  
*A:* On every write (add, set, remove), it creates a new copy of the underlying array, so readers never see a partially updated array. This makes reads very fast and thread-safe, but writes are expensive.  
*Use case:* Best for scenarios with many reads and few writes.

**Q207: How does ConcurrentLinkedQueue work internally?**  
*A:* It is a lock-free, thread-safe queue based on a linked node structure. It uses CAS operations to update head and tail pointers, allowing multiple threads to enqueue and dequeue concurrently without locks.

**Q208: How does LinkedBlockingQueue work internally?**  
*A:* It uses separate locks for put and take operations, allowing concurrent insertion and removal. Internally, it is a linked list with a capacity limit, and uses conditions to block threads when the queue is full or empty.

**Q209: What is the difference between SynchronousQueue and other BlockingQueues?**  
*A:* `SynchronousQueue` has no capacity; each put must wait for a take and vice versa. It's used for direct handoff between threads, unlike other queues which can buffer elements.

**Q210: What is the difference between fair and non-fair locks in Java?**  
*A:* Fair locks grant access to threads in the order they requested it (FIFO), preventing starvation but reducing throughput. Non-fair locks may allow threads to "barge" ahead, increasing throughput but risking starvation.  
*Example:*  
```java
ReentrantLock fairLock = new ReentrantLock(true); // fair
ReentrantLock unfairLock = new ReentrantLock();   // non-fair
```

**Q211: What is the difference between invokeAll and invokeAny in ExecutorService?**  
*A:* `invokeAll` submits a collection of tasks and waits for all to complete, returning a list of Futures. `invokeAny` returns as soon as one task completes successfully, cancelling the rest.

**Q212: How does the work-stealing algorithm in ForkJoinPool improve performance?**  
*A:* Idle threads "steal" tasks from the tail of other threads' deques, balancing the workload and reducing idle time, which leads to better CPU utilization in parallel computations.

**Q213: What is the difference between Thread.yield(), Thread.sleep(), and Object.wait()?**  
*A:* `yield()` hints to the scheduler to switch threads but doesn't block. `sleep()` pauses the thread for a specified time. `wait()` releases the object's monitor and waits for notify/notifyAll.

**Q214: What is the difference between Thread.interrupt() and setting a volatile flag?**  
*A:* `interrupt()` sets the thread's interrupt status, which can be checked with `isInterrupted()` and can cause blocking methods to throw InterruptedException. A volatile flag is a custom mechanism for cooperative thread termination.

**Q215: What is the difference between a fixed thread pool and a cached thread pool?**  
*A:* Fixed thread pool has a fixed number of threads and a bounded/unbounded queue. Cached thread pool creates new threads as needed and reuses idle threads, suitable for short-lived asynchronous tasks.

**Q216: How does the internal queue in ThreadPoolExecutor affect task scheduling?**  
*A:* The choice of queue (e.g., LinkedBlockingQueue, SynchronousQueue) affects how tasks are buffered and when new threads are created. A bounded queue can limit memory usage but may cause task rejection if full.

**Q217: What is the difference between shutdown() and shutdownNow() in ExecutorService?**  
*A:* `shutdown()` initiates an orderly shutdown, allowing running tasks to finish. `shutdownNow()` attempts to stop all running tasks immediately and returns a list of tasks that were awaiting execution.

**Q218: How does ThreadLocalRandom differ from Random?**  
*A:* `ThreadLocalRandom` provides better performance in multithreaded environments by avoiding contention, as each thread has its own random number generator.

**Q219: What is the difference between a daemon thread and a user thread?**  
*A:* Daemon threads run in the background and do not prevent JVM shutdown. User threads keep the JVM alive until they finish.

**Q220: How does the internal structure of ConcurrentSkipListMap provide concurrency?**  
*A:* It uses a skip list data structure, allowing concurrent reads and updates without locking the entire map. Updates use CAS and fine-grained locking on nodes.

---

*For each topic, review the official Java documentation and practice with code examples for mastery.*
