# MAANG-Oriented Design Patterns & Principles Compendium

Goal: Rapid, deep mastery + interview storytelling. Each pattern: "Explain like I'm 5", Core Idea, When To Use, Java Scenarios (2–3), Pitfalls, Resources, Upgrade (advanced twist).

Sections:
1. Core Principles (SOLID, DRY, KISS, YAGNI, LoD, Cohesion/Coupling, Composition > Inheritance, Immutability, Idempotency)
2. Creational Patterns
3. Structural Patterns
4. Behavioral Patterns
5. Concurrency / Resilience Patterns
6. Architectural & Integration Patterns (selected high-signal)
7. Comparison Cheat Sheets (When to use what)
8. Interview Story Hooks

---
## 1. Core Principles

### 1.1 SOLID Principles (Bundle)
**ELI5**: Building with toy blocks where each block does one simple job, blocks snap together without glue, and you can always add new blocks without breaking old ones.

**Core Idea**: Five principles that make code easier to understand, maintain, and extend.

#### Single Responsibility Principle (SRP)
**ELI5**: One job per toy—a car shouldn't also be a phone.
**Rule**: A class should have only one reason to change.

**Java Scenario 1** (E-commerce Invoice):
```java
// BAD: Multiple responsibilities
class Invoice {
    private List<Item> items;
    private Customer customer;
    
    public double calculateTotal() { /* business logic */ }
    public void saveToDatabase() { /* persistence */ }
    public void sendEmail() { /* notification */ }
    public String formatForPrint() { /* presentation */ }
}

// GOOD: Single responsibilities
class Invoice {
    private List<Item> items;
    private Customer customer;
    public double calculateTotal() { /* only calculation */ }
}

class InvoiceRepository {
    public void save(Invoice invoice) { /* only persistence */ }
}

class InvoiceEmailService {
    public void sendInvoice(Invoice invoice, String email) { /* only email */ }
}

class InvoicePrinter {
    public String format(Invoice invoice) { /* only formatting */ }
}
```

**Java Scenario 2** (User Authentication):
```java
// BAD: God class
class UserManager {
    public void register(String email, String password) { /* registration */ }
    public boolean authenticate(String email, String password) { /* auth */ }
    public void sendWelcomeEmail(String email) { /* email */ }
    public void logActivity(String action) { /* logging */ }
    public void validateEmail(String email) { /* validation */ }
}

// GOOD: Separated concerns
class UserRegistrationService {
    private final EmailValidator validator;
    private final PasswordHasher hasher;
    private final UserRepository repository;
    
    public void register(String email, String password) {
        validator.validate(email);
        String hashedPassword = hasher.hash(password);
        User user = new User(email, hashedPassword);
        repository.save(user);
    }
}

class AuthenticationService {
    public boolean authenticate(String email, String password) {
        // Only handles authentication logic
    }
}
```

#### Open/Closed Principle (OCP)
**ELI5**: You can add new features to your toy box without breaking existing toys.
**Rule**: Classes should be open for extension, closed for modification.

**Java Scenario 1** (Payment Processing):
```java
// BAD: Must modify existing code for new payment types
class PaymentProcessor {
    public void processPayment(String type, double amount) {
        if (type.equals("CREDIT_CARD")) {
            // credit card logic
        } else if (type.equals("PAYPAL")) {
            // PayPal logic
        } else if (type.equals("BITCOIN")) { // NEW: breaks OCP
            // Bitcoin logic - had to modify existing class!
        }
    }
}

// GOOD: Extension without modification
interface PaymentMethod {
    void processPayment(double amount);
}

class CreditCardPayment implements PaymentMethod {
    public void processPayment(double amount) {
        // credit card specific logic
    }
}

class PayPalPayment implements PaymentMethod {
    public void processPayment(double amount) {
        // PayPal specific logic
    }
}

class BitcoinPayment implements PaymentMethod { // NEW: no existing code touched
    public void processPayment(double amount) {
        // Bitcoin specific logic
    }
}

class PaymentProcessor {
    public void processPayment(PaymentMethod method, double amount) {
        method.processPayment(amount); // works with any implementation
    }
}
```

**Java Scenario 2** (Notification System):
```java
// Strategy pattern enabling OCP
interface NotificationStrategy {
    void send(String message, String recipient);
}

class EmailNotification implements NotificationStrategy {
    public void send(String message, String recipient) {
        // email implementation
    }
}

class SMSNotification implements NotificationStrategy {
    public void send(String message, String recipient) {
        // SMS implementation
    }
}

class SlackNotification implements NotificationStrategy { // Easy to add new types
    public void send(String message, String recipient) {
        // Slack implementation
    }
}

class NotificationService {
    private List<NotificationStrategy> strategies = new ArrayList<>();
    
    public void addStrategy(NotificationStrategy strategy) {
        strategies.add(strategy);
    }
    
    public void notifyAll(String message, String recipient) {
        strategies.forEach(strategy -> strategy.send(message, recipient));
    }
}
```

#### Liskov Substitution Principle (LSP)
**ELI5**: If you can use a toy car, you should be able to use any type of toy car (race car, truck) the same way.
**Rule**: Objects of a superclass should be replaceable with objects of its subclasses without breaking the application.

**Java Scenario 1** (Shape Calculator):
```java
// BAD: LSP violation
class Rectangle {
    protected int width, height;
    
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Violates LSP - unexpected behavior
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height; // Violates LSP - unexpected behavior
    }
}

// This breaks with Square:
void testRectangle(Rectangle rect) {
    rect.setWidth(5);
    rect.setHeight(4);
    assert rect.getArea() == 20; // Fails for Square!
}

// GOOD: LSP compliant
abstract class Shape {
    public abstract int getArea();
}

class Rectangle extends Shape {
    private final int width, height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getArea() { return width * height; }
}

class Square extends Shape {
    private final int side;
    
    public Square(int side) { this.side = side; }
    
    public int getArea() { return side * side; }
}
```

**Java Scenario 2** (Bird Hierarchy):
```java
// BAD: LSP violation
interface Bird {
    void fly();
    void eat();
}

class Sparrow implements Bird {
    public void fly() { /* flying logic */ }
    public void eat() { /* eating logic */ }
}

class Ostrich implements Bird {
    public void fly() { 
        throw new UnsupportedOperationException("Ostriches can't fly!"); // LSP violation
    }
    public void eat() { /* eating logic */ }
}

// GOOD: LSP compliant design
interface Bird {
    void eat();
    void move();
}

interface FlyingBird extends Bird {
    void fly();
}

interface SwimmingBird extends Bird {
    void swim();
}

class Sparrow implements FlyingBird {
    public void eat() { /* eating logic */ }
    public void move() { fly(); }
    public void fly() { /* flying logic */ }
}

class Ostrich implements Bird {
    public void eat() { /* eating logic */ }
    public void move() { /* running logic */ }
}

class Penguin implements SwimmingBird {
    public void eat() { /* eating logic */ }
    public void move() { swim(); }
    public void swim() { /* swimming logic */ }
}
```

#### Interface Segregation Principle (ISP)
**ELI5**: Don't force someone to use a remote control with 100 buttons when they only need 3.
**Rule**: No client should be forced to depend on methods it does not use.

**Java Scenario 1** (Multi-function Printer):
```java
// BAD: Fat interface
interface MultiFunctionDevice {
    void print(Document doc);
    void scan(Document doc);
    void fax(Document doc);
    void copy(Document doc);
}

class SimplePrinter implements MultiFunctionDevice {
    public void print(Document doc) { /* printing logic */ }
    
    // Forced to implement methods it doesn't need
    public void scan(Document doc) { throw new UnsupportedOperationException(); }
    public void fax(Document doc) { throw new UnsupportedOperationException(); }
    public void copy(Document doc) { throw new UnsupportedOperationException(); }
}

// GOOD: Segregated interfaces
interface Printer {
    void print(Document doc);
}

interface Scanner {
    void scan(Document doc);
}

interface FaxMachine {
    void fax(Document doc);
}

interface Copier {
    void copy(Document doc);
}

class SimplePrinter implements Printer {
    public void print(Document doc) { /* printing logic */ }
}

class AllInOnePrinter implements Printer, Scanner, FaxMachine, Copier {
    public void print(Document doc) { /* printing logic */ }
    public void scan(Document doc) { /* scanning logic */ }
    public void fax(Document doc) { /* fax logic */ }
    public void copy(Document doc) { /* copy logic */ }
}
```

**Java Scenario 2** (Worker Interfaces):
```java
// BAD: Bloated interface
interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void writeCode();
    void designUI();
    void testSoftware();
}

// GOOD: Specific interfaces
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

interface Developer extends Workable {
    void writeCode();
}

interface Designer extends Workable {
    void designUI();
}

interface Tester extends Workable {
    void testSoftware();
}

class SoftwareDeveloper implements Developer, Eatable, Sleepable {
    public void work() { writeCode(); }
    public void writeCode() { /* coding logic */ }
    public void eat() { /* eating logic */ }
    public void sleep() { /* sleeping logic */ }
}
```

#### Dependency Inversion Principle (DIP)
**ELI5**: Instead of hardwiring your toy to one specific battery, make it work with any battery that fits.
**Rule**: High-level modules should not depend on low-level modules. Both should depend on abstractions.

**Java Scenario 1** (Email Service):
```java
// BAD: High-level depends on low-level
class EmailService {
    private SMTPClient smtpClient = new SMTPClient(); // Hard dependency
    
    public void sendEmail(String message) {
        smtpClient.send(message); // Tightly coupled to SMTP
    }
}

// GOOD: Both depend on abstraction
interface EmailClient {
    void send(String message);
}

class SMTPClient implements EmailClient {
    public void send(String message) {
        // SMTP implementation
    }
}

class SendGridClient implements EmailClient {
    public void send(String message) {
        // SendGrid implementation
    }
}

class EmailService {
    private final EmailClient emailClient;
    
    public EmailService(EmailClient emailClient) { // Dependency injection
        this.emailClient = emailClient;
    }
    
    public void sendEmail(String message) {
        emailClient.send(message); // Works with any implementation
    }
}
```

**Java Scenario 2** (Order Processing):
```java
// BAD: High-level depends on concrete implementations
class OrderProcessor {
    private MySQLDatabase database = new MySQLDatabase(); // Tight coupling
    private SMSNotifier notifier = new SMSNotifier(); // Tight coupling
    
    public void processOrder(Order order) {
        database.save(order);
        notifier.sendConfirmation(order.getCustomerPhone());
    }
}

// GOOD: Dependency inversion
interface Database {
    void save(Order order);
}

interface Notifier {
    void sendConfirmation(String contact);
}

class MySQLDatabase implements Database {
    public void save(Order order) { /* MySQL logic */ }
}

class PostgreSQLDatabase implements Database {
    public void save(Order order) { /* PostgreSQL logic */ }
}

class SMSNotifier implements Notifier {
    public void sendConfirmation(String phone) { /* SMS logic */ }
}

class EmailNotifier implements Notifier {
    public void sendConfirmation(String email) { /* Email logic */ }
}

class OrderProcessor {
    private final Database database;
    private final Notifier notifier;
    
    public OrderProcessor(Database database, Notifier notifier) {
        this.database = database;
        this.notifier = notifier;
    }
    
    public void processOrder(Order order) {
        database.save(order);
        notifier.sendConfirmation(order.getCustomerContact());
    }
}

// Usage with Dependency Injection
public class Main {
    public static void main(String[] args) {
        Database db = new PostgreSQLDatabase();
        Notifier notifier = new EmailNotifier();
        OrderProcessor processor = new OrderProcessor(db, notifier);
        
        // Easy to swap implementations for testing or different environments
    }
}
```

**When to Use SOLID**:
- SRP: When classes are becoming too complex or changing for multiple reasons
- OCP: When you frequently add new features or behaviors
- LSP: When designing inheritance hierarchies
- ISP: When clients are forced to implement unused methods
- DIP: When you want testable, flexible code

**Common Pitfalls**:
- Over-applying principles leading to unnecessary complexity
- Creating too many interfaces too early (YAGNI violation)
- Confusing LSP with simple inheritance
- Making interfaces too granular (ISP taken too far)

**MAANG Interview Impact**: Shows architectural thinking, maintainability awareness, and ability to design extensible systems.

**Resources**:
- https://blog.cleancoder.com/uncle-bob/2020/10/18/Solid-Relevance.html (Uncle Bob's SOLID)
- https://martinfowler.com/articles/dipInTheWild.html (DIP examples)
- https://www.baeldung.com/solid-principles (Java-specific SOLID)
- https://stackify.com/solid-design-principles/ (Practical examples)
- Clean Architecture by Robert Martin (comprehensive coverage)

### 1.2 DRY (Don't Repeat Yourself)
**ELI5**: Don't copy-paste the same Lego castle instructions—write once, use everywhere.
**Core Idea**: Every piece of knowledge must have a single, unambiguous representation.

**Java Scenario 1** (Validation Logic):
```java
// BAD: Repeated validation
class UserRegistrationService {
    public void registerUser(String email, String password) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password too short");
        }
        // registration logic
    }
}

class UserUpdateService {
    public void updateEmail(String email) {
        if (email == null || !email.contains("@")) { // DUPLICATE!
            throw new IllegalArgumentException("Invalid email");
        }
        // update logic
    }
}

class PasswordResetService {
    public void resetPassword(String password) {
        if (password == null || password.length() < 8) { // DUPLICATE!
            throw new IllegalArgumentException("Password too short");
        }
        // reset logic
    }
}

// GOOD: Single source of truth
class UserValidator {
    public static void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
    
    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password too short");
        }
    }
}

class UserRegistrationService {
    public void registerUser(String email, String password) {
        UserValidator.validateEmail(email);
        UserValidator.validatePassword(password);
        // registration logic
    }
}

class UserUpdateService {
    public void updateEmail(String email) {
        UserValidator.validateEmail(email); // Reused
        // update logic
    }
}
```

**Java Scenario 2** (Database Connection):
```java
// BAD: Repeated connection logic
class UserDAO {
    public void saveUser(User user) {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String username = "user";
        String password = "pass";
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            // save user
        }
    }
}

class OrderDAO {
    public void saveOrder(Order order) {
        String url = "jdbc:mysql://localhost:3306/mydb"; // DUPLICATE!
        String username = "user"; // DUPLICATE!
        String password = "pass"; // DUPLICATE!
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            // save order
        }
    }
}

// GOOD: Centralized connection management
class DatabaseConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

class UserDAO {
    public void saveUser(User user) {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            // save user
        }
    }
}

class OrderDAO {
    public void saveOrder(Order order) {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            // save order
        }
    }
}
```

**When NOT to Apply DRY**: 
- Test code (tests can be explicit/verbose)
- Accidental duplication (similar looking but different purpose)
- Constants that happen to have same value but different meaning

**Pitfall**: Over-DRY can create complex abstractions for coincidental duplication.
**Resources**: 
- https://www.artima.com/articles/orthogonality-and-the-dry-principle
- https://verraes.net/2014/08/dry-is-about-knowledge/

### 1.3 KISS (Keep It Simple, Stupid)
**ELI5**: Use a simple key, not a complex puzzle box, to open a door.
**Core Idea**: Simplicity should be a key goal in design, and unnecessary complexity should be avoided.

**Java Scenario 1** (Date Formatting):
```java
// COMPLEX: Over-engineered
class DateFormatterFactory {
    private static final Map<String, DateFormatterStrategy> strategies = new HashMap<>();
    
    static {
        strategies.put("ISO", new ISODateFormatterStrategy());
        strategies.put("US", new USDateFormatterStrategy());
        // ... more strategies
    }
    
    public static DateFormatterStrategy getFormatter(String type) {
        return strategies.get(type);
    }
}

interface DateFormatterStrategy {
    String format(Date date);
}

class ISODateFormatterStrategy implements DateFormatterStrategy {
    public String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}

// SIMPLE: Direct approach
class DateFormatter {
    public static String formatISO(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
    public static String formatUS(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }
}
```

**Java Scenario 2** (User Search):
```java
// COMPLEX: Over-abstracted
interface SearchCriteria {}
class NameCriteria implements SearchCriteria { String name; }
class AgeCriteria implements SearchCriteria { int minAge, maxAge; }

class UserSearchEngine {
    public List<User> search(List<SearchCriteria> criteria) {
        // complex composition logic
    }
}

// SIMPLE: Clear and direct
class UserService {
    public List<User> findByName(String name) {
        return users.stream()
            .filter(user -> user.getName().contains(name))
            .collect(toList());
    }
    
    public List<User> findByAgeRange(int minAge, int maxAge) {
        return users.stream()
            .filter(user -> user.getAge() >= minAge && user.getAge() <= maxAge)
            .collect(toList());
    }
}
```

**Metric**: Cyclomatic complexity ≤ 10, Lines per method ≤ 20
**Resources**: 
- https://www.infoq.com/articles/kiss-principle/
- https://blog.codinghorror.com/kiss-and-yagni/

### 1.4 YAGNI (You Aren't Gonna Need It)
**ELI5**: Don't pack winter clothes for a summer vacation "just in case."
**Core Idea**: Don't implement functionality until you actually need it.

**Java Scenario 1** (User Management):
```java
// BAD: Implementing features you don't need yet
class User {
    private String id;
    private String email;
    private String password;
    
    // Features not needed yet but implemented "just in case"
    private List<String> favoriteColors; // No requirement for this
    private Map<String, Object> customAttributes; // No use case yet
    private List<User> friends; // Social features not in scope
    private GeolocationData location; // Location features not planned
    private SubscriptionTier tier; // Subscription not implemented
    
    // Complex audit trail for future compliance (not needed now)
    private List<AuditEvent> auditTrail;
    private EncryptionManager encryptionManager;
}

// GOOD: Only what's needed now
class User {
    private String id;
    private String email;
    private String password;
    
    // Add features when actually needed
}
```

**Java Scenario 2** (Configuration System):
```java
// BAD: Over-engineering for future flexibility
interface ConfigurationSource {
    String get(String key);
}

class DatabaseConfigurationSource implements ConfigurationSource {
    // Complex database reading logic for config
}

class RemoteConfigurationSource implements ConfigurationSource {
    // Complex remote service integration
}

class ConfigurationManager {
    private List<ConfigurationSource> sources;
    private CacheStrategy cacheStrategy;
    private ValidationStrategy validationStrategy;
    // Complex priority and fallback mechanisms
}

// GOOD: Simple solution for current needs
class Configuration {
    private static final Properties props = new Properties();
    
    static {
        try {
            props.load(Configuration.class.getResourceAsStream("/app.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    public static String get(String key) {
        return props.getProperty(key);
    }
}
```

**Anti-Pattern**: Building plugin architectures, complex configuration systems, or extensibility points before you have multiple implementations.

**Resources**:
- https://martinfowler.com/bliki/Yagni.html
- https://www.c2.com/cgi/wiki?YouArentGonnaNeedIt

### 1.5 Law of Demeter (LoD) / Principle of Least Knowledge
**ELI5**: Only talk to your immediate friends, not their friends' friends.
**Core Idea**: An object should only communicate with its immediate neighbors.

**Java Scenario 1** (Order Processing):
```java
// BAD: Violates Law of Demeter
class OrderProcessor {
    public void processOrder(Order order) {
        String city = order.getCustomer().getAddress().getCity(); // Chain of calls
        double tax = order.getCustomer().getAddress().getState().getTaxRate(); // Deep navigation
        
        // Update customer's address's country's statistics
        order.getCustomer().getAddress().getCountry().updateOrderStatistics();
    }
}

// GOOD: Follows Law of Demeter
class OrderProcessor {
    public void processOrder(Order order) {
        String city = order.getCustomerCity(); // Order provides direct access
        double tax = order.getTaxRate(); // Order calculates and returns tax rate
        
        order.updateCountryStatistics(); // Order handles the complexity
    }
}

class Order {
    private Customer customer;
    private List<Item> items;
    
    public String getCustomerCity() {
        return customer.getCity(); // Order manages customer access
    }
    
    public double getTaxRate() {
        return customer.getTaxRate(); // Order delegates to customer
    }
    
    public void updateCountryStatistics() {
        customer.updateCountryStatistics(); // Order delegates to customer
    }
}

class Customer {
    private Address address;
    
    public String getCity() {
        return address.getCity(); // Customer manages address access
    }
    
    public double getTaxRate() {
        return address.getTaxRate();
    }
    
    public void updateCountryStatistics() {
        address.updateCountryStatistics();
    }
}
```

**Java Scenario 2** (UI Components):
```java
// BAD: Deep navigation
class PaymentForm {
    public void validatePayment() {
        boolean isValid = this.getParent().getWindow().getApplication()
            .getConfigurationManager().getPaymentConfig().isValidationEnabled();
        
        if (isValid) {
            // validation logic
        }
    }
}

// GOOD: Direct dependencies
class PaymentForm {
    private final PaymentValidator validator;
    
    public PaymentForm(PaymentValidator validator) {
        this.validator = validator;
    }
    
    public void validatePayment() {
        if (validator.isEnabled()) {
            validator.validate(this.getPaymentData());
        }
    }
}
```

**Benefits**: Reduced coupling, easier testing, better encapsulation
**Resources**:
- https://www.ccs.neu.edu/home/lieber/LoD.html
- https://martinfowler.com/bliki/Fluent.html (when to break LoD)

### 1.6 Cohesion & Coupling
**ELI5**: 
- **High Cohesion**: All toys in a box belong together (all car parts in car box)
- **Low Coupling**: Easy to move toy boxes without affecting other boxes

**Core Idea**: 
- **Cohesion**: How closely related elements within a module are
- **Coupling**: How much modules depend on each other

**Java Scenario 1** (High Cohesion):
```java
// BAD: Low cohesion - unrelated responsibilities
class UserUtils {
    public void validateEmail(String email) { /* validation */ }
    public void sendEmail(String email, String message) { /* email sending */ }
    public void calculateTax(double amount) { /* tax calculation */ }
    public void formatDate(Date date) { /* date formatting */ }
    public void compressImage(byte[] image) { /* image processing */ }
}

// GOOD: High cohesion - related responsibilities grouped
class EmailService {
    public void validateEmail(String email) { /* validation */ }
    public void sendEmail(String email, String message) { /* sending */ }
    public void formatEmailTemplate(String template, Map<String, Object> data) { /* formatting */ }
}

class TaxCalculator {
    public double calculateSalesTax(double amount, String state) { /* sales tax */ }
    public double calculateIncomeTax(double income, String bracket) { /* income tax */ }
    public double calculateVAT(double amount, String country) { /* VAT */ }
}

class ImageProcessor {
    public byte[] compress(byte[] image) { /* compression */ }
    public byte[] resize(byte[] image, int width, int height) { /* resizing */ }
    public byte[] applyFilter(byte[] image, Filter filter) { /* filtering */ }
}
```

**Java Scenario 2** (Low Coupling):
```java
// BAD: High coupling - direct dependencies
class OrderService {
    private MySQLDatabase database = new MySQLDatabase(); // Tight coupling
    private SMTPEmailService emailService = new SMTPEmailService(); // Tight coupling
    private PayPalPaymentGateway paymentGateway = new PayPalPaymentGateway(); // Tight coupling
    
    public void processOrder(Order order) {
        database.save(order); // Can't change database easily
        emailService.sendConfirmation(order.getCustomerEmail()); // Can't change email provider
        paymentGateway.charge(order.getTotal()); // Can't change payment provider
    }
}

// GOOD: Low coupling - dependency injection
class OrderService {
    private final Database database;
    private final EmailService emailService;
    private final PaymentGateway paymentGateway;
    
    public OrderService(Database database, EmailService emailService, PaymentGateway paymentGateway) {
        this.database = database;
        this.emailService = emailService;
        this.paymentGateway = paymentGateway;
    }
    
    public void processOrder(Order order) {
        database.save(order); // Can inject any database implementation
        emailService.sendConfirmation(order.getCustomerEmail()); // Can inject any email service
        paymentGateway.charge(order.getTotal()); // Can inject any payment gateway
    }
}
```

**Metrics**: 
- Cohesion: LCOM (Lack of Cohesion of Methods) < 0.5
- Coupling: Afferent/Efferent coupling ratio
- Heuristic: Module should depend on < 5 external modules

**Resources**:
- https://www.geeksforgeeks.org/software-engineering-coupling-and-cohesion/
- https://martinfowler.com/bliki/CouplingVersusLinking.html

### 1.3 KISS (Keep It Simple, Stupid)
ELI5: Use a small ladder, not a rocket, to reach a shelf.
Metric: Cyclomatic complexity <= 10.

### 1.4 YAGNI (You Aren’t Gonna Need It)
ELI5: Don’t bring an umbrella if it’s sunny now and forecast is clear.
Anti-Pattern: Building plugin system before a second implementation exists.

### 1.5 Law of Demeter (LoD)
ELI5: Ask your friend, not your friend’s cousin’s dog.
Bad:
```java
order.getCustomer().getAddress().getCity();
```
Better: `order.city()` facade method.

### 1.6 Cohesion & Coupling
High cohesion = same shelf items. Low coupling = easy to move shelf.
Heuristic: Module depends on < 5 external modules.

### 1.7 Composition over Inheritance
ELI5: Add gadgets to a robot instead of making a tall family tree of robots.
Example:
```java
class Notifier { void send(String m){} }
class SlackDecorator extends Notifier { /* inheritance layering */ } // vs
class CompositeNotifier { List<Notifier> outs; void sendAll(String m){ outs.forEach(n->n.send(m)); }}
```

### 1.8 Immutability
ELI5: A frozen popsicle you can lick but not reshape.
Java:
```java
public record Money(BigDecimal amount, String currency) {}
```

### 1.9 Idempotency
ELI5: Press elevator button many times; still one elevator comes.
Example: `PUT /user/123` sets full state; safe to retry.
Implementation snippet:
```java
if (processedIds.add(requestId)) process(); else ignore();
```
---
## 2. Creational Patterns

### 2.1 Singleton Pattern
**ELI5**: There's only one President in the country - no matter how many times you ask "Who's the President?", you get the same person.

**Core Idea**: Ensure a class has only one instance and provide global access to it.

**When to Use**: 
- Configuration objects
- Logger instances
- Database connection pools
- Cache managers
- Thread pools

**Java Implementation 1** (Thread-Safe Lazy Initialization):
```java
public class DatabaseConnectionManager {
    private static volatile DatabaseConnectionManager instance;
    private DataSource dataSource;
    
    private DatabaseConnectionManager() {
        // Expensive initialization
        this.dataSource = createDataSource();
    }
    
    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) { // Double-checked locking
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    private DataSource createDataSource() {
        // Complex setup logic
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setUsername("user");
        config.setPassword("password");
        config.setMaximumPoolSize(20);
        return new HikariDataSource(config);
    }
}

// Usage
public class UserDAO {
    public void saveUser(User user) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            // Database operations
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Java Implementation 2** (Initialization-on-demand Holder - Recommended):
```java
public class Logger {
    private final String logLevel;
    private final PrintWriter writer;
    
    private Logger() {
        this.logLevel = System.getProperty("log.level", "INFO");
        try {
            this.writer = new PrintWriter(new FileWriter("app.log", true));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize logger", e);
        }
    }
    
    // Bill Pugh Singleton Pattern - Thread-safe without synchronization
    private static class LoggerHolder {
        private static final Logger INSTANCE = new Logger();
    }
    
    public static Logger getInstance() {
        return LoggerHolder.INSTANCE;
    }
    
    public void log(String level, String message) {
        if (shouldLog(level)) {
            writer.println(LocalDateTime.now() + " [" + level + "] " + message);
            writer.flush();
        }
    }
    
    private boolean shouldLog(String level) {
        // Log level comparison logic
        return true;
    }
    
    public void info(String message) { log("INFO", message); }
    public void warn(String message) { log("WARN", message); }
    public void error(String message) { log("ERROR", message); }
}

// Usage across the application
public class UserService {
    private static final Logger logger = Logger.getInstance();
    
    public void createUser(User user) {
        logger.info("Creating user: " + user.getEmail());
        try {
            // User creation logic
            logger.info("User created successfully");
        } catch (Exception e) {
            logger.error("Failed to create user: " + e.getMessage());
            throw e;
        }
    }
}
```

**Java Implementation 3** (Enum Singleton - Best for serialization):
```java
public enum ConfigurationManager {
    INSTANCE;
    
    private final Properties properties;
    
    ConfigurationManager() {
        properties = new Properties();
        loadConfiguration();
    }
    
    private void loadConfiguration() {
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}

// Usage
public class EmailService {
    private final String smtpHost;
    private final int smtpPort;
    
    public EmailService() {
        ConfigurationManager config = ConfigurationManager.INSTANCE;
        this.smtpHost = config.getProperty("smtp.host", "localhost");
        this.smtpPort = config.getIntProperty("smtp.port", 587);
    }
}
```

**Real-World Scenario** (Cache Manager):
```java
public class CacheManager {
    private static volatile CacheManager instance;
    private final Map<String, Object> cache;
    private final long maxSize;
    private final long ttlMillis;
    
    private CacheManager() {
        this.cache = new ConcurrentHashMap<>();
        this.maxSize = 10000;
        this.ttlMillis = 3600000; // 1 hour
        startCleanupTask();
    }
    
    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }
    
    public void put(String key, Object value) {
        if (cache.size() >= maxSize) {
            evictOldest();
        }
        cache.put(key, new CacheEntry(value, System.currentTimeMillis()));
    }
    
    public Object get(String key) {
        CacheEntry entry = (CacheEntry) cache.get(key);
        if (entry != null && !entry.isExpired(ttlMillis)) {
            return entry.getValue();
        }
        cache.remove(key);
        return null;
    }
    
    private void evictOldest() {
        // LRU eviction logic
        cache.entrySet().stream()
            .min(Map.Entry.comparingByValue(
                (e1, e2) -> Long.compare(((CacheEntry)e1).getTimestamp(), ((CacheEntry)e2).getTimestamp())))
            .ifPresent(entry -> cache.remove(entry.getKey()));
    }
    
    private void startCleanupTask() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            cache.entrySet().removeIf(entry -> 
                ((CacheEntry)entry.getValue()).isExpired(ttlMillis));
        }, 1, 1, TimeUnit.HOURS);
    }
    
    private static class CacheEntry {
        private final Object value;
        private final long timestamp;
        
        CacheEntry(Object value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
        
        Object getValue() { return value; }
        long getTimestamp() { return timestamp; }
        
        boolean isExpired(long ttlMillis) {
            return System.currentTimeMillis() - timestamp > ttlMillis;
        }
    }
}
```

**Common Pitfalls**:
1. **Testing Difficulties**: Hard to mock or test in isolation
2. **Hidden Dependencies**: Classes secretly depend on singleton state
3. **Thread Safety**: Improper synchronization can create multiple instances
4. **Serialization Issues**: Can break singleton guarantee during deserialization

**Testing Anti-Pattern**:
```java
// BAD: Tests become interdependent
public class UserServiceTest {
    @Test
    public void testCreateUser() {
        Logger.getInstance().info("Starting test"); // Shared state!
        // test logic
    }
    
    @Test 
    public void testDeleteUser() {
        Logger.getInstance().info("Starting test"); // Same instance as above
        // This test might be affected by previous test's logging
    }
}

// BETTER: Use dependency injection for testing
public class UserService {
    private final Logger logger;
    
    public UserService(Logger logger) {
        this.logger = logger;
    }
    
    // In production: new UserService(Logger.getInstance())
    // In tests: new UserService(mockLogger)
}
```

**When NOT to Use Singleton**:
- When you need multiple instances (even in future)
- When the class might need to be subclassed
- In multi-tenant applications
- When it makes testing harder

**Modern Alternative - Dependency Injection**:
```java
@Component
@Scope("singleton") // Spring manages the singleton
public class ConfigurationService {
    // Spring ensures only one instance
}
```

**MAANG Interview Talking Points**:
- "I'd implement Singleton for [specific reason], but consider DI container for better testability"
- "Double-checked locking prevents race conditions while minimizing synchronization overhead"
- "Enum implementation handles serialization edge cases automatically"
- "In microservices, might prefer stateless design over singleton state"

**Resources**:
- https://refactoring.guru/design-patterns/singleton/java/example
- https://www.baeldung.com/java-singleton
- Effective Java by Joshua Bloch (Item 3 & 89)
- https://www.javacodegurus.com/2015/07/singleton-design-pattern-best-practices.html

### 2.2 Factory Method
**ELI5**: Ask a kitchen to bake a cake; you get cake without knowing the recipe or ingredients.

**Core Idea**: Create objects without specifying their exact class, delegating instantiation to subclasses.

**When to Use**:
- When you don't know beforehand the exact types of objects your code should work with
- When you want to provide a library/framework for extending its internal components
- When you want to reuse existing objects instead of rebuilding them each time
- When you need to decouple object creation from usage

**Java Implementation 1** (Cross-Platform UI):
```java
// Product interface
interface Button {
    void render();
    void onClick();
}

// Concrete products
class WindowsButton implements Button {
    public void render() {
        System.out.println("Rendering Windows-style button");
        // Windows-specific rendering logic
    }
    
    public void onClick() {
        System.out.println("Windows button clicked - opening native dialog");
    }
}

class MacButton implements Button {
    public void render() {
        System.out.println("Rendering Mac-style button");
        // Mac-specific rendering logic
    }
    
    public void onClick() {
        System.out.println("Mac button clicked - opening Cocoa dialog");
    }
}

class WebButton implements Button {
    public void render() {
        System.out.println("Rendering HTML button");
        // Web-specific rendering logic
    }
    
    public void onClick() {
        System.out.println("Web button clicked - JavaScript event");
    }
}

// Creator (Factory Method)
abstract class Dialog {
    // Factory method - subclasses override this
    public abstract Button createButton();
    
    // Template method using factory method
    public void render() {
        Button okButton = createButton();
        okButton.onClick();
        okButton.render();
    }
}

// Concrete creators
class WindowsDialog extends Dialog {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }
}

class MacDialog extends Dialog {
    @Override
    public Button createButton() {
        return new MacButton();
    }
}

class WebDialog extends Dialog {
    @Override
    public Button createButton() {
        return new WebButton();
    }
}

// Client code
public class Application {
    private Dialog dialog;
    
    public void initialize() {
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("windows")) {
            dialog = new WindowsDialog();
        } else if (osName.contains("mac")) {
            dialog = new MacDialog();
        } else {
            dialog = new WebDialog();
        }
    }
    
    public void main() {
        initialize();
        dialog.render(); // Works regardless of platform
    }
}
```

**Java Implementation 2** (Logger Factory):
```java
// Product interface
interface Logger {
    void log(String level, String message);
    void setLevel(String level);
}

// Concrete products
class FileLogger implements Logger {
    private String filePath;
    private String currentLevel = "INFO";
    
    public FileLogger(String filePath) {
        this.filePath = filePath;
    }
    
    public void log(String level, String message) {
        if (shouldLog(level)) {
            // Write to file
            System.out.println("[FILE:" + filePath + "] " + level + ": " + message);
        }
    }
    
    public void setLevel(String level) { this.currentLevel = level; }
    
    private boolean shouldLog(String level) { return true; } // Simplified
}

class DatabaseLogger implements Logger {
    private String connectionString;
    private String currentLevel = "INFO";
    
    public DatabaseLogger(String connectionString) {
        this.connectionString = connectionString;
    }
    
    public void log(String level, String message) {
        if (shouldLog(level)) {
            // Insert into database
            System.out.println("[DB:" + connectionString + "] " + level + ": " + message);
        }
    }
    
    public void setLevel(String level) { this.currentLevel = level; }
    
    private boolean shouldLog(String level) { return true; } // Simplified
}

class ConsoleLogger implements Logger {
    private String currentLevel = "INFO";
    
    public void log(String level, String message) {
        if (shouldLog(level)) {
            System.out.println("[CONSOLE] " + level + ": " + message);
        }
    }
    
    public void setLevel(String level) { this.currentLevel = level; }
    
    private boolean shouldLog(String level) { return true; } // Simplified
}

// Creator
abstract class LoggerFactory {
    // Factory method
    public abstract Logger createLogger();
    
    // Common configuration logic
    public Logger createConfiguredLogger() {
        Logger logger = createLogger();
        logger.setLevel(getDefaultLevel());
        return logger;
    }
    
    protected String getDefaultLevel() {
        return System.getProperty("log.level", "INFO");
    }
}

// Concrete creators
class FileLoggerFactory extends LoggerFactory {
    private String logDirectory;
    
    public FileLoggerFactory(String logDirectory) {
        this.logDirectory = logDirectory;
    }
    
    @Override
    public Logger createLogger() {
        String fileName = logDirectory + "/app-" + System.currentTimeMillis() + ".log";
        return new FileLogger(fileName);
    }
}

class DatabaseLoggerFactory extends LoggerFactory {
    private String connectionString;
    
    public DatabaseLoggerFactory(String connectionString) {
        this.connectionString = connectionString;
    }
    
    @Override
    public Logger createLogger() {
        return new DatabaseLogger(connectionString);
    }
}

class ConsoleLoggerFactory extends LoggerFactory {
    @Override
    public Logger createLogger() {
        return new ConsoleLogger();
    }
}

// Usage
public class LoggingService {
    private Logger logger;
    
    public LoggingService(LoggerFactory factory) {
        this.logger = factory.createConfiguredLogger();
    }
    
    public void processOrder(String orderId) {
        logger.log("INFO", "Processing order: " + orderId);
        // Business logic
        logger.log("INFO", "Order processed successfully: " + orderId);
    }
}

// Configuration
public class ApplicationBootstrap {
    public static void main(String[] args) {
        LoggerFactory factory;
        
        String environment = System.getProperty("env", "development");
        switch (environment) {
            case "production":
                factory = new DatabaseLoggerFactory("jdbc:postgresql://prod-db:5432/logs");
                break;
            case "staging":
                factory = new FileLoggerFactory("/var/log/myapp");
                break;
            default:
                factory = new ConsoleLoggerFactory();
        }
        
        LoggingService service = new LoggingService(factory);
        service.processOrder("ORDER-123");
    }
}
```

**Java Implementation 3** (Document Processor):
```java
// Product interface
interface DocumentProcessor {
    void parse(String content);
    void validate();
    String export();
}

// Concrete products
class PDFProcessor implements DocumentProcessor {
    private String content;
    
    public void parse(String content) {
        this.content = content;
        System.out.println("Parsing PDF content using PDF library");
    }
    
    public void validate() {
        System.out.println("Validating PDF structure and metadata");
    }
    
    public String export() {
        return "Exported PDF with " + content.length() + " characters";
    }
}

class WordProcessor implements DocumentProcessor {
    private String content;
    
    public void parse(String content) {
        this.content = content;
        System.out.println("Parsing Word document using Apache POI");
    }
    
    public void validate() {
        System.out.println("Validating Word document structure");
    }
    
    public String export() {
        return "Exported Word document with " + content.length() + " characters";
    }
}

class ExcelProcessor implements DocumentProcessor {
    private String content;
    
    public void parse(String content) {
        this.content = content;
        System.out.println("Parsing Excel spreadsheet");
    }
    
    public void validate() {
        System.out.println("Validating Excel formulas and data types");
    }
    
    public String export() {
        return "Exported Excel with " + content.length() + " characters";
    }
}

// Creator
abstract class DocumentFactory {
    // Factory method
    public abstract DocumentProcessor createProcessor();
    
    // Template method that uses factory method
    public String processDocument(String content) {
        DocumentProcessor processor = createProcessor();
        processor.parse(content);
        processor.validate();
        return processor.export();
    }
}

// Concrete creators
class PDFFactory extends DocumentFactory {
    @Override
    public DocumentProcessor createProcessor() {
        return new PDFProcessor();
    }
}

class WordFactory extends DocumentFactory {
    @Override
    public DocumentProcessor createProcessor() {
        return new WordProcessor();
    }
}

class ExcelFactory extends DocumentFactory {
    @Override
    public DocumentProcessor createProcessor() {
        return new ExcelProcessor();
    }
}

// Usage with strategy-like selection
public class DocumentService {
    public String processFile(String fileName, String content) {
        DocumentFactory factory = getFactoryForFile(fileName);
        return factory.processDocument(content);
    }
    
    private DocumentFactory getFactoryForFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        
        switch (extension) {
            case "pdf":
                return new PDFFactory();
            case "doc":
            case "docx":
                return new WordFactory();
            case "xls":
            case "xlsx":
                return new ExcelFactory();
            default:
                throw new UnsupportedOperationException("Unsupported file type: " + extension);
        }
    }
}
```

**Common Pitfalls**:
1. **Over-abstraction**: Creating factories when simple direct instantiation suffices
2. **Too Many Factories**: Each product having its own factory when one parameterized factory could work
3. **Leaky Abstraction**: Factory method revealing implementation details
4. **Confusion with Abstract Factory**: Factory Method creates one product, Abstract Factory creates families

**When NOT to Use Factory Method**:
- When you have only one implementation and no plans for others
- When object creation is simple and doesn't require configuration
- When the cost of abstraction outweighs the benefits
- In simple applications where flexibility isn't needed

**Comparison with Other Patterns**:
```java
// Factory Method vs Simple Factory (Static Factory)
class SimpleButtonFactory {
    public static Button createButton(String type) {
        switch (type) {
            case "windows": return new WindowsButton();
            case "mac": return new MacButton();
            default: return new WebButton();
        }
    }
}

// Factory Method vs Abstract Factory
// Factory Method: Single product creation method
// Abstract Factory: Multiple related products creation methods
```

**MAANG Interview Talking Points**:
- "Factory Method provides flexibility for extending product types without modifying existing code"
- "Separates object creation from business logic, following Single Responsibility Principle"
- "Enables easy testing by allowing mock implementations to be injected"
- "Common in frameworks - Spring's BeanFactory, JDBC DriverManager"

**Resources**:
- https://refactoring.guru/design-patterns/factory-method/java/example
- https://www.baeldung.com/java-factory-pattern
- Head First Design Patterns (Factory chapter)
- https://www.journaldev.com/1392/factory-design-pattern-in-java

### 2.3 Abstract Factory
**ELI5**: A kit that gives you matching chair + table + sofa set, but different kits give you different styles (modern vs vintage).

**Core Idea**: Provide an interface for creating families of related or dependent objects without specifying their concrete classes.

**When to Use**:
- When you need to create families of related products
- When you want to ensure products from the same family are used together
- When you want to provide a class library of products and reveal only interfaces
- When you need to configure your system with one of multiple families of products

**Java Implementation 1** (UI Theme System):
```java
// Abstract product interfaces
interface Button {
    void render();
    void click();
}

interface Checkbox {
    void render();
    void check();
}

interface TextField {
    void render();
    void setText(String text);
    String getText();
}

// Dark theme product family
class DarkButton implements Button {
    public void render() {
        System.out.println("Rendering dark button with #333 background");
    }
    
    public void click() {
        System.out.println("Dark button clicked with subtle animation");
    }
}

class DarkCheckbox implements Checkbox {
    private boolean checked = false;
    
    public void render() {
        System.out.println("Rendering dark checkbox with white checkmark");
    }
    
    public void check() {
        checked = !checked;
        System.out.println("Dark checkbox " + (checked ? "checked" : "unchecked"));
    }
}

class DarkTextField implements TextField {
    private String text = "";
    
    public void render() {
        System.out.println("Rendering dark text field with #444 background");
    }
    
    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
}

// Light theme product family
class LightButton implements Button {
    public void render() {
        System.out.println("Rendering light button with #FFF background");
    }
    
    public void click() {
        System.out.println("Light button clicked with bounce animation");
    }
}

class LightCheckbox implements Checkbox {
    private boolean checked = false;
    
    public void render() {
        System.out.println("Rendering light checkbox with blue checkmark");
    }
    
    public void check() {
        checked = !checked;
        System.out.println("Light checkbox " + (checked ? "checked" : "unchecked"));
    }
}

class LightTextField implements TextField {
    private String text = "";
    
    public void render() {
        System.out.println("Rendering light text field with white background");
    }
    
    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
}

// High contrast accessibility theme
class HighContrastButton implements Button {
    public void render() {
        System.out.println("Rendering high contrast button with bold borders");
    }
    
    public void click() {
        System.out.println("High contrast button clicked with audio feedback");
    }
}

class HighContrastCheckbox implements Checkbox {
    private boolean checked = false;
    
    public void render() {
        System.out.println("Rendering high contrast checkbox with thick borders");
    }
    
    public void check() {
        checked = !checked;
        System.out.println("High contrast checkbox " + (checked ? "checked" : "unchecked"));
    }
}

class HighContrastTextField implements TextField {
    private String text = "";
    
    public void render() {
        System.out.println("Rendering high contrast text field with thick borders");
    }
    
    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
}

// Abstract factory interface
interface UIFactory {
    Button createButton();
    Checkbox createCheckbox();
    TextField createTextField();
}

// Concrete factories
class DarkThemeFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new DarkButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new DarkCheckbox();
    }
    
    @Override
    public TextField createTextField() {
        return new DarkTextField();
    }
}

class LightThemeFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new LightButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new LightCheckbox();
    }
    
    @Override
    public TextField createTextField() {
        return new LightTextField();
    }
}

class HighContrastFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new HighContrastButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new HighContrastCheckbox();
    }
    
    @Override
    public TextField createTextField() {
        return new HighContrastTextField();
    }
}

// Client code
class LoginForm {
    private Button loginButton;
    private TextField usernameField;
    private TextField passwordField;
    private Checkbox rememberMeCheckbox;
    
    public LoginForm(UIFactory factory) {
        // All components will be from the same theme family
        this.loginButton = factory.createButton();
        this.usernameField = factory.createTextField();
        this.passwordField = factory.createTextField();
        this.rememberMeCheckbox = factory.createCheckbox();
    }
    
    public void render() {
        System.out.println("=== Rendering Login Form ===");
        usernameField.render();
        passwordField.render();
        rememberMeCheckbox.render();
        loginButton.render();
    }
    
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        loginButton.click();
        System.out.println("Login attempted for: " + username);
    }
}

// Application configuration
public class ThemeApplication {
    private UIFactory factory;
    
    public void configureTheme(String themeName) {
        switch (themeName.toLowerCase()) {
            case "dark":
                factory = new DarkThemeFactory();
                break;
            case "light":
                factory = new LightThemeFactory();
                break;
            case "high-contrast":
                factory = new HighContrastFactory();
                break;
            default:
                factory = new LightThemeFactory(); // Default
        }
    }
    
    public void createUI() {
        LoginForm loginForm = new LoginForm(factory);
        loginForm.render();
    }
    
    public static void main(String[] args) {
        ThemeApplication app = new ThemeApplication();
        
        // Theme can be set based on user preference, system settings, etc.
        String userTheme = System.getProperty("user.theme", "light");
        app.configureTheme(userTheme);
        app.createUI();
    }
}
```

**Java Implementation 2** (Database Vendor Families):
```java
// Abstract products
interface Connection {
    void connect();
    void disconnect();
    String getConnectionString();
}

interface Query {
    void prepare(String sql);
    void execute();
    String getDialect();
}

interface Transaction {
    void begin();
    void commit();
    void rollback();
}

// MySQL family
class MySQLConnection implements Connection {
    private String host;
    
    public MySQLConnection(String host) { this.host = host; }
    
    public void connect() {
        System.out.println("Connecting to MySQL at " + host);
    }
    
    public void disconnect() {
        System.out.println("Disconnecting from MySQL");
    }
    
    public String getConnectionString() {
        return "jdbc:mysql://" + host + ":3306/mydb";
    }
}

class MySQLQuery implements Query {
    private String sql;
    
    public void prepare(String sql) {
        this.sql = sql;
        System.out.println("Preparing MySQL query: " + sql);
    }
    
    public void execute() {
        System.out.println("Executing MySQL query with MyISAM optimizations");
    }
    
    public String getDialect() { return "MySQL"; }
}

class MySQLTransaction implements Transaction {
    public void begin() {
        System.out.println("BEGIN; -- MySQL transaction started");
    }
    
    public void commit() {
        System.out.println("COMMIT; -- MySQL transaction committed");
    }
    
    public void rollback() {
        System.out.println("ROLLBACK; -- MySQL transaction rolled back");
    }
}

// PostgreSQL family
class PostgreSQLConnection implements Connection {
    private String host;
    
    public PostgreSQLConnection(String host) { this.host = host; }
    
    public void connect() {
        System.out.println("Connecting to PostgreSQL at " + host);
    }
    
    public void disconnect() {
        System.out.println("Disconnecting from PostgreSQL");
    }
    
    public String getConnectionString() {
        return "jdbc:postgresql://" + host + ":5432/mydb";
    }
}

class PostgreSQLQuery implements Query {
    private String sql;
    
    public void prepare(String sql) {
        this.sql = sql;
        System.out.println("Preparing PostgreSQL query: " + sql);
    }
    
    public void execute() {
        System.out.println("Executing PostgreSQL query with advanced indexing");
    }
    
    public String getDialect() { return "PostgreSQL"; }
}

class PostgreSQLTransaction implements Transaction {
    public void begin() {
        System.out.println("BEGIN; -- PostgreSQL transaction started with MVCC");
    }
    
    public void commit() {
        System.out.println("COMMIT; -- PostgreSQL transaction committed");
    }
    
    public void rollback() {
        System.out.println("ROLLBACK; -- PostgreSQL transaction rolled back");
    }
}

// Abstract factory
interface DatabaseFactory {
    Connection createConnection(String host);
    Query createQuery();
    Transaction createTransaction();
}

// Concrete factories
class MySQLFactory implements DatabaseFactory {
    @Override
    public Connection createConnection(String host) {
        return new MySQLConnection(host);
    }
    
    @Override
    public Query createQuery() {
        return new MySQLQuery();
    }
    
    @Override
    public Transaction createTransaction() {
        return new MySQLTransaction();
    }
}

class PostgreSQLFactory implements DatabaseFactory {
    @Override
    public Connection createConnection(String host) {
        return new PostgreSQLConnection(host);
    }
    
    @Override
    public Query createQuery() {
        return new PostgreSQLQuery();
    }
    
    @Override
    public Transaction createTransaction() {
        return new PostgreSQLTransaction();
    }
}

// Client code
class DatabaseService {
    private final DatabaseFactory factory;
    private Connection connection;
    
    public DatabaseService(DatabaseFactory factory) {
        this.factory = factory;
    }
    
    public void executeInTransaction(String host, String sql) {
        // All components are guaranteed to be from the same database family
        connection = factory.createConnection(host);
        Transaction transaction = factory.createTransaction();
        Query query = factory.createQuery();
        
        try {
            connection.connect();
            transaction.begin();
            
            query.prepare(sql);
            query.execute();
            
            transaction.commit();
            System.out.println("Transaction completed successfully");
            
        } catch (Exception e) {
            transaction.rollback();
            System.out.println("Transaction failed, rolled back");
        } finally {
            connection.disconnect();
        }
    }
}

// Configuration
public class DatabaseApplication {
    public static void main(String[] args) {
        DatabaseFactory factory;
        
        String dbType = System.getProperty("db.type", "postgresql");
        switch (dbType.toLowerCase()) {
            case "mysql":
                factory = new MySQLFactory();
                break;
            case "postgresql":
                factory = new PostgreSQLFactory();
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
        
        DatabaseService service = new DatabaseService(factory);
        service.executeInTransaction("localhost", "SELECT * FROM users");
    }
}
```

**Common Pitfalls**:
1. **Overuse**: Creating abstract factories when Factory Method would suffice
2. **Product Explosion**: Too many product interfaces making the system complex
3. **Tight Coupling**: Products depending on specific implementations rather than interfaces
4. **Extending Families**: Adding new products requires changing all factory implementations

**When NOT to Use Abstract Factory**:
- When you only have one product family
- When products don't need to work together as a family
- When the cost of abstraction exceeds the benefit
- In simple applications without multiple platform/theme support

**Comparison with Factory Method**:
```java
// Factory Method - Single product creation
abstract class DocumentFactory {
    abstract Document createDocument(); // One product
}

// Abstract Factory - Family of products creation
interface OfficeFactory {
    Document createDocument();   // Multiple related
    Spreadsheet createSpreadsheet(); // products that
    Presentation createPresentation(); // work together
}
```

**Real-World Examples**:
- Java Swing/AWT Look and Feel
- Database driver families (JDBC)
- GUI toolkit themes
- Cross-platform mobile development frameworks

**MAANG Interview Talking Points**:
- "Abstract Factory ensures product family consistency - all UI components match the selected theme"
- "Provides isolation between product families, making it easy to switch implementations"
- "Follows Open/Closed Principle - can add new families without modifying existing code"
- "Trade-off: Initial complexity vs future flexibility when multiple product families are likely"

**Resources**:
- https://refactoring.guru/design-patterns/abstract-factory/java/example
- https://www.baeldung.com/java-abstract-factory-pattern
- Gang of Four Design Patterns book (original source)
- https://www.tutorialspoint.com/design_pattern/abstract_factory_pattern.htm

### 2.4 Builder Pattern
**ELI5**: Like building a custom pizza - you choose toppings one by one, then bake it all together at the end.

**Core Idea**: Construct complex objects step by step, allowing different representations of the same construction process.

**When to Use**:
- Objects with many optional parameters (>4-5 parameters)
- Complex object creation with validation
- Immutable objects that need step-by-step construction
- When constructor has too many parameters (telescoping constructor problem)

**Java Implementation 1** (Classic Builder - SQL Query):
```java
public class SqlQuery {
    private final String select;
    private final String from;
    private final String where;
    private final String orderBy;
    private final String groupBy;
    private final String having;
    private final Integer limit;
    
    private SqlQuery(Builder builder) {
        this.select = builder.select;
        this.from = builder.from;
        this.where = builder.where;
        this.orderBy = builder.orderBy;
        this.groupBy = builder.groupBy;
        this.having = builder.having;
        this.limit = builder.limit;
    }
    
    public String toSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(select);
        sql.append(" FROM ").append(from);
        
        if (where != null) {
            sql.append(" WHERE ").append(where);
        }
        if (groupBy != null) {
            sql.append(" GROUP BY ").append(groupBy);
        }
        if (having != null) {
            sql.append(" HAVING ").append(having);
        }
        if (orderBy != null) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }
        
        return sql.toString();
    }
    
    public static class Builder {
        private String select;
        private String from;
        private String where;
        private String orderBy;
        private String groupBy;
        private String having;
        private Integer limit;
        
        public Builder select(String select) {
            this.select = select;
            return this;
        }
        
        public Builder from(String from) {
            this.from = from;
            return this;
        }
        
        public Builder where(String where) {
            this.where = where;
            return this;
        }
        
        public Builder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }
        
        public Builder groupBy(String groupBy) {
            this.groupBy = groupBy;
            return this;
        }
        
        public Builder having(String having) {
            this.having = having;
            return this;
        }
        
        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }
        
        public SqlQuery build() {
            if (select == null || from == null) {
                throw new IllegalStateException("SELECT and FROM are required");
            }
            return new SqlQuery(this);
        }
    }
}

// Usage examples
public class DatabaseService {
    public List<User> findActiveUsers() {
        String sql = new SqlQuery.Builder()
            .select("id, name, email")
            .from("users")
            .where("status = 'ACTIVE'")
            .orderBy("name ASC")
            .build()
            .toSql();
        
        return executeQuery(sql);
    }
    
    public List<Order> getTopOrdersByValue() {
        String sql = new SqlQuery.Builder()
            .select("customer_id, SUM(total) as total_value")
            .from("orders")
            .where("status = 'COMPLETED'")
            .groupBy("customer_id")
            .having("SUM(total) > 1000")
            .orderBy("total_value DESC")
            .limit(10)
            .build()
            .toSql();
            
        return executeQuery(sql);
    }
}
```

**Java Implementation 2** (HTTP Request Builder):
```java
public class HttpRequest {
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;
    private final int timeoutSeconds;
    private final boolean followRedirects;
    
    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
        this.queryParams = Collections.unmodifiableMap(new HashMap<>(builder.queryParams));
        this.body = builder.body;
        this.timeoutSeconds = builder.timeoutSeconds;
        this.followRedirects = builder.followRedirects;
    }
    
    // Getters
    public String getUrl() { return url; }
    public String getMethod() { return method; }
    public Map<String, String> getHeaders() { return headers; }
    public Map<String, String> getQueryParams() { return queryParams; }
    public String getBody() { return body; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public boolean shouldFollowRedirects() { return followRedirects; }
    
    public String getFullUrl() {
        if (queryParams.isEmpty()) {
            return url;
        }
        
        StringBuilder fullUrl = new StringBuilder(url);
        fullUrl.append("?");
        queryParams.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .forEach(param -> fullUrl.append(param).append("&"));
        
        return fullUrl.substring(0, fullUrl.length() - 1); // Remove last &
    }
    
    public static class Builder {
        private String url;
        private String method = "GET";
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> queryParams = new HashMap<>();
        private String body;
        private int timeoutSeconds = 30;
        private boolean followRedirects = true;
        
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        public Builder get() {
            this.method = "GET";
            return this;
        }
        
        public Builder post() {
            this.method = "POST";
            return this;
        }
        
        public Builder put() {
            this.method = "PUT";
            return this;
        }
        
        public Builder delete() {
            this.method = "DELETE";
            return this;
        }
        
        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }
        
        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }
        
        public Builder queryParam(String name, String value) {
            this.queryParams.put(name, value);
            return this;
        }
        
        public Builder queryParams(Map<String, String> params) {
            this.queryParams.putAll(params);
            return this;
        }
        
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        
        public Builder jsonBody(Object obj) {
            this.body = JsonUtils.toJson(obj); // Assume utility exists
            this.headers.put("Content-Type", "application/json");
            return this;
        }
        
        public Builder timeout(int seconds) {
            this.timeoutSeconds = seconds;
            return this;
        }
        
        public Builder followRedirects(boolean follow) {
            this.followRedirects = follow;
            return this;
        }
        
        public HttpRequest build() {
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalStateException("URL is required");
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                throw new IllegalArgumentException("URL must start with http:// or https://");
            }
            if (timeoutSeconds <= 0) {
                throw new IllegalArgumentException("Timeout must be positive");
            }
            
            return new HttpRequest(this);
        }
    }
}

// Usage examples
public class ApiClient {
    public String getUserData(String userId) {
        HttpRequest request = new HttpRequest.Builder()
            .url("https://api.example.com/users")
            .get()
            .queryParam("id", userId)
            .header("Authorization", "Bearer " + getToken())
            .header("Accept", "application/json")
            .timeout(10)
            .build();
            
        return executeRequest(request);
    }
    
    public String createUser(User user) {
        HttpRequest request = new HttpRequest.Builder()
            .url("https://api.example.com/users")
            .post()
            .jsonBody(user)
            .header("Authorization", "Bearer " + getToken())
            .timeout(15)
            .followRedirects(false)
            .build();
            
        return executeRequest(request);
    }
    
    public String uploadFile(String filePath, byte[] fileData) {
        HttpRequest request = new HttpRequest.Builder()
            .url("https://api.example.com/upload")
            .post()
            .header("Content-Type", "multipart/form-data")
            .header("X-File-Name", filePath)
            .body(Base64.getEncoder().encodeToString(fileData))
            .timeout(60) // Longer timeout for file upload
            .build();
            
        return executeRequest(request);
    }
}
```

**Java Implementation 3** (Configuration Object):
```java
public class DatabaseConfiguration {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int maxPoolSize;
    private final int minPoolSize;
    private final long connectionTimeout;
    private final long idleTimeout;
    private final boolean autoCommit;
    private final String isolationLevel;
    private final Map<String, String> properties;
    
    private DatabaseConfiguration(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;
        this.maxPoolSize = builder.maxPoolSize;
        this.minPoolSize = builder.minPoolSize;
        this.connectionTimeout = builder.connectionTimeout;
        this.idleTimeout = builder.idleTimeout;
        this.autoCommit = builder.autoCommit;
        this.isolationLevel = builder.isolationLevel;
        this.properties = Collections.unmodifiableMap(new HashMap<>(builder.properties));
    }
    
    // Getters omitted for brevity
    
    public static class Builder {
        // Required parameters
        private final String host;
        private final String database;
        private final String username;
        private final String password;
        
        // Optional parameters with defaults
        private int port = 5432;
        private int maxPoolSize = 10;
        private int minPoolSize = 1;
        private long connectionTimeout = 30000;
        private long idleTimeout = 600000;
        private boolean autoCommit = true;
        private String isolationLevel = "READ_COMMITTED";
        private Map<String, String> properties = new HashMap<>();
        
        // Required parameters in constructor
        public Builder(String host, String database, String username, String password) {
            this.host = host;
            this.database = database;
            this.username = username;
            this.password = password;
        }
        
        public Builder port(int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535");
            }
            this.port = port;
            return this;
        }
        
        public Builder maxPoolSize(int maxPoolSize) {
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("Max pool size must be positive");
            }
            this.maxPoolSize = maxPoolSize;
            return this;
        }
        
        public Builder minPoolSize(int minPoolSize) {
            if (minPoolSize < 0) {
                throw new IllegalArgumentException("Min pool size cannot be negative");
            }
            this.minPoolSize = minPoolSize;
            return this;
        }
        
        public Builder connectionTimeout(long timeoutMs) {
            if (timeoutMs <= 0) {
                throw new IllegalArgumentException("Connection timeout must be positive");
            }
            this.connectionTimeout = timeoutMs;
            return this;
        }
        
        public Builder idleTimeout(long timeoutMs) {
            if (timeoutMs <= 0) {
                throw new IllegalArgumentException("Idle timeout must be positive");
            }
            this.idleTimeout = timeoutMs;
            return this;
        }
        
        public Builder autoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }
        
        public Builder isolationLevel(String level) {
            Set<String> validLevels = Set.of("READ_UNCOMMITTED", "READ_COMMITTED", 
                                           "REPEATABLE_READ", "SERIALIZABLE");
            if (!validLevels.contains(level)) {
                throw new IllegalArgumentException("Invalid isolation level: " + level);
            }
            this.isolationLevel = level;
            return this;
        }
        
        public Builder property(String key, String value) {
            this.properties.put(key, value);
            return this;
        }
        
        public Builder properties(Map<String, String> properties) {
            this.properties.putAll(properties);
            return this;
        }
        
        public DatabaseConfiguration build() {
            if (minPoolSize > maxPoolSize) {
                throw new IllegalStateException("Min pool size cannot be greater than max pool size");
            }
            
            return new DatabaseConfiguration(this);
        }
    }
}

// Usage
public class DatabaseConnectionFactory {
    public DataSource createProductionDataSource() {
        DatabaseConfiguration config = new DatabaseConfiguration.Builder(
                "prod-db.company.com", 
                "ecommerce", 
                "app_user", 
                "secure_password")
            .port(5432)
            .maxPoolSize(20)
            .minPoolSize(5)
            .connectionTimeout(30000)
            .idleTimeout(300000)
            .autoCommit(false)
            .isolationLevel("READ_COMMITTED")
            .property("ssl", "true")
            .property("sslmode", "require")
            .build();
            
        return createDataSource(config);
    }
    
    public DataSource createTestDataSource() {
        DatabaseConfiguration config = new DatabaseConfiguration.Builder(
                "localhost", 
                "test_db", 
                "test_user", 
                "test_password")
            .maxPoolSize(5)
            .autoCommit(true)
            .build(); // Uses defaults for other values
            
        return createDataSource(config);
    }
}
```

**Comparison with Telescoping Constructor Anti-Pattern**:
```java
// BAD: Telescoping Constructor Problem
public class User {
    public User(String email) { /* */ }
    public User(String email, String name) { /* */ }
    public User(String email, String name, int age) { /* */ }
    public User(String email, String name, int age, String address) { /* */ }
    public User(String email, String name, int age, String address, String phone) { /* */ }
    public User(String email, String name, int age, String address, String phone, boolean newsletter) { /* */ }
    // Becomes unmanageable!
}

// GOOD: Builder Pattern
User user = new User.Builder("john@example.com")
    .name("John Doe")
    .age(30)
    .address("123 Main St")
    .phone("555-1234")
    .newsletter(true)
    .build();
```

**Common Pitfalls**:
1. **Overuse**: Don't use for simple objects with few parameters
2. **Mutable Builders**: Ensure builder state isn't shared between builds
3. **Missing Validation**: Validate in build() method, not setters
4. **Inheritance Complexity**: Builders don't inherit well

**When NOT to Use Builder**:
- Objects with few (<3-4) parameters
- Simple value objects
- When all parameters are required
- Frequently created objects (performance sensitive)

**Modern Alternatives**:
```java
// Java Records (Java 14+) for simple cases
public record User(String email, String name, int age) {
    // Automatic constructor, getters, equals, hashCode, toString
}

// Lombok @Builder annotation
@Builder
public class User {
    private String email;
    private String name;
    private int age;
    // Builder generated automatically
}
```

**MAANG Interview Talking Points**:
- "Builder solves telescoping constructor problem and provides immutable objects"
- "Fluent interface improves readability for complex object construction"
- "Build-time validation ensures object consistency"
- "Consider factory methods for simple cases, Builder for complex configuration"

**Resources**:
- https://refactoring.guru/design-patterns/builder/java/example
- https://www.baeldung.com/java-builder-pattern
- Effective Java by Joshua Bloch (Item 2)
- https://www.javacodegurus.com/2015/07/builder-design-pattern-in-java.html
```
Scenario: Complex optional params; immutable objects.
Pitfall: Overuse for simple 2-field objects.

### 2.5 Prototype
**ELI5**: Use a magic stamp to copy your favorite toy instead of building it from scratch each time.

**Core Idea**: Create new objects by cloning existing instances rather than creating new instances from scratch.

**When to Use**:
- When object creation is expensive (complex initialization, database calls, network requests)
- When you need to create many similar objects with slight variations
- When you want to avoid the overhead of initializing an object multiple times
- When the number of product classes is large and you want to avoid parallel class hierarchies

**Java Implementation 1** (Game Character System):
```java
// Prototype interface
interface GameCharacter extends Cloneable {
    GameCharacter cloneCharacter();
    void display();
    void setPosition(int x, int y);
    void setLevel(int level);
}

// Concrete prototype - Warrior
class Warrior implements GameCharacter {
    private String name;
    private int health;
    private int strength;
    private int level;
    private Position position;
    private List<String> equipment;
    private Map<String, Integer> stats;
    
    public Warrior(String name) {
        this.name = name;
        this.health = 100;
        this.strength = 80;
        this.level = 1;
        this.position = new Position(0, 0);
        this.equipment = new ArrayList<>();
        this.stats = new HashMap<>();
        
        // Expensive initialization
        initializeDefaultEquipment();
        calculateBaseStats();
    }
    
    // Copy constructor for cloning
    private Warrior(Warrior original) {
        this.name = original.name;
        this.health = original.health;
        this.strength = original.strength;
        this.level = original.level;
        this.position = new Position(original.position.x, original.position.y);
        this.equipment = new ArrayList<>(original.equipment);
        this.stats = new HashMap<>(original.stats);
    }
    
    @Override
    public GameCharacter cloneCharacter() {
        return new Warrior(this);
    }
    
    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
    }
    
    public void setLevel(int level) {
        this.level = level;
        recalculateStats();
    }
    
    public void display() {
        System.out.println("Warrior " + name + " [Level " + level + "] at (" + 
                         position.x + "," + position.y + ") - Health: " + health + 
                         ", Strength: " + strength);
    }
    
    private void initializeDefaultEquipment() {
        equipment.add("Iron Sword");
        equipment.add("Leather Armor");
        equipment.add("Basic Shield");
    }
    
    private void calculateBaseStats() {
        stats.put("attack", strength + 10);
        stats.put("defense", health / 5);
        stats.put("speed", 50);
    }
    
    private void recalculateStats() {
        // Complex stat calculation based on level
        int levelBonus = level * 5;
        stats.put("attack", strength + 10 + levelBonus);
        stats.put("defense", health / 5 + levelBonus);
    }
    
    static class Position {
        int x, y;
        Position(int x, int y) { this.x = x; this.y = y; }
    }
}

// Concrete prototype - Mage
class Mage implements GameCharacter {
    private String name;
    private int health;
    private int mana;
    private int intelligence;
    private int level;
    private Position position;
    private List<String> spells;
    private Map<String, Integer> stats;
    
    public Mage(String name) {
        this.name = name;
        this.health = 60;
        this.mana = 150;
        this.intelligence = 90;
        this.level = 1;
        this.position = new Position(0, 0);
        this.spells = new ArrayList<>();
        this.stats = new HashMap<>();
        
        // Expensive initialization
        initializeDefaultSpells();
        calculateBaseStats();
    }
    
    private Mage(Mage original) {
        this.name = original.name;
        this.health = original.health;
        this.mana = original.mana;
        this.intelligence = original.intelligence;
        this.level = original.level;
        this.position = new Position(original.position.x, original.position.y);
        this.spells = new ArrayList<>(original.spells);
        this.stats = new HashMap<>(original.stats);
    }
    
    @Override
    public GameCharacter cloneCharacter() {
        return new Mage(this);
    }
    
    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
    }
    
    public void setLevel(int level) {
        this.level = level;
        recalculateStats();
    }
    
    public void display() {
        System.out.println("Mage " + name + " [Level " + level + "] at (" + 
                         position.x + "," + position.y + ") - Health: " + health + 
                         ", Mana: " + mana + ", Intelligence: " + intelligence);
    }
    
    private void initializeDefaultSpells() {
        spells.add("Fireball");
        spells.add("Heal");
        spells.add("Magic Missile");
    }
    
    private void calculateBaseStats() {
        stats.put("spellPower", intelligence + 20);
        stats.put("defense", health / 8);
        stats.put("speed", 40);
    }
    
    private void recalculateStats() {
        int levelBonus = level * 3;
        stats.put("spellPower", intelligence + 20 + levelBonus);
        stats.put("defense", health / 8 + levelBonus);
    }
    
    static class Position {
        int x, y;
        Position(int x, int y) { this.x = x; this.y = y; }
    }
}

// Character registry for prototype management
class CharacterRegistry {
    private Map<String, GameCharacter> prototypes = new HashMap<>();
    
    public void registerPrototype(String key, GameCharacter prototype) {
        prototypes.put(key, prototype);
    }
    
    public GameCharacter createCharacter(String key) {
        GameCharacter prototype = prototypes.get(key);
        if (prototype != null) {
            return prototype.cloneCharacter();
        }
        throw new IllegalArgumentException("Unknown character type: " + key);
    }
    
    public void initializePrototypes() {
        // Create and register base prototypes
        registerPrototype("warrior", new Warrior("Base Warrior"));
        registerPrototype("mage", new Mage("Base Mage"));
    }
}

// Game usage
public class GameWorld {
    private CharacterRegistry registry;
    
    public GameWorld() {
        registry = new CharacterRegistry();
        registry.initializePrototypes();
    }
    
    public void spawnCharacters() {
        // Create multiple characters efficiently using prototypes
        GameCharacter warrior1 = registry.createCharacter("warrior");
        warrior1.setPosition(10, 20);
        warrior1.setLevel(5);
        
        GameCharacter warrior2 = registry.createCharacter("warrior");
        warrior2.setPosition(30, 40);
        warrior2.setLevel(3);
        
        GameCharacter mage1 = registry.createCharacter("mage");
        mage1.setPosition(50, 60);
        mage1.setLevel(7);
        
        // Display all characters
        warrior1.display();
        warrior2.display();
        mage1.display();
    }
    
    public static void main(String[] args) {
        GameWorld game = new GameWorld();
        game.spawnCharacters();
    }
}
```

**Java Implementation 2** (Document Template System):
```java
// Document prototype
abstract class Document implements Cloneable {
    protected String title;
    protected String author;
    protected List<Section> sections;
    protected Map<String, String> metadata;
    protected DocumentFormat format;
    
    public Document(String title, String author) {
        this.title = title;
        this.author = author;
        this.sections = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.format = new DocumentFormat();
    }
    
    // Deep clone implementation
    @Override
    public Document clone() {
        try {
            Document cloned = (Document) super.clone();
            cloned.sections = new ArrayList<>();
            for (Section section : this.sections) {
                cloned.sections.add(section.clone());
            }
            cloned.metadata = new HashMap<>(this.metadata);
            cloned.format = this.format.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    public void addSection(Section section) {
        sections.add(section);
    }
    
    public void setMetadata(String key, String value) {
        metadata.put(key, value);
    }
    
    public abstract void generate();
}

// Concrete document types
class ReportDocument extends Document {
    private String reportType;
    
    public ReportDocument(String title, String author, String reportType) {
        super(title, author);
        this.reportType = reportType;
        initializeReportTemplate();
    }
    
    private void initializeReportTemplate() {
        // Expensive initialization with default sections
        addSection(new Section("Executive Summary", ""));
        addSection(new Section("Introduction", ""));
        addSection(new Section("Analysis", ""));
        addSection(new Section("Conclusions", ""));
        addSection(new Section("Recommendations", ""));
        
        setMetadata("type", "report");
        setMetadata("template", "corporate-report-v2");
        format.setFont("Arial");
        format.setFontSize(12);
    }
    
    @Override
    public void generate() {
        System.out.println("Generating " + reportType + " report: " + title);
        System.out.println("Author: " + author);
        System.out.println("Sections: " + sections.size());
    }
}

class ProposalDocument extends Document {
    private double budgetAmount;
    
    public ProposalDocument(String title, String author, double budgetAmount) {
        super(title, author);
        this.budgetAmount = budgetAmount;
        initializeProposalTemplate();
    }
    
    private void initializeProposalTemplate() {
        addSection(new Section("Project Overview", ""));
        addSection(new Section("Objectives", ""));
        addSection(new Section("Methodology", ""));
        addSection(new Section("Timeline", ""));
        addSection(new Section("Budget", ""));
        addSection(new Section("Team", ""));
        
        setMetadata("type", "proposal");
        setMetadata("template", "business-proposal-v3");
        format.setFont("Times New Roman");
        format.setFontSize(11);
    }
    
    @Override
    public void generate() {
        System.out.println("Generating proposal: " + title);
        System.out.println("Author: " + author);
        System.out.println("Budget: $" + budgetAmount);
        System.out.println("Sections: " + sections.size());
    }
}

// Supporting classes
class Section implements Cloneable {
    private String title;
    private String content;
    
    public Section(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    @Override
    public Section clone() {
        try {
            return (Section) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // getters and setters
}

class DocumentFormat implements Cloneable {
    private String font;
    private int fontSize;
    private String color;
    
    public DocumentFormat() {
        this.font = "Arial";
        this.fontSize = 12;
        this.color = "black";
    }
    
    @Override
    public DocumentFormat clone() {
        try {
            return (DocumentFormat) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // getters and setters
    public void setFont(String font) { this.font = font; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
}

// Document factory using prototypes
class DocumentFactory {
    private Map<String, Document> prototypes = new HashMap<>();
    
    public void registerPrototype(String key, Document prototype) {
        prototypes.put(key, prototype);
    }
    
    public Document createDocument(String type, String title, String author) {
        Document prototype = prototypes.get(type);
        if (prototype != null) {
            Document clone = prototype.clone();
            clone.title = title;
            clone.author = author;
            return clone;
        }
        throw new IllegalArgumentException("Unknown document type: " + type);
    }
    
    public void initializePrototypes() {
        registerPrototype("report", new ReportDocument("Template Report", "Template Author", "Monthly"));
        registerPrototype("proposal", new ProposalDocument("Template Proposal", "Template Author", 0.0));
    }
}

// Usage
public class DocumentGenerationService {
    private DocumentFactory factory;
    
    public DocumentGenerationService() {
        factory = new DocumentFactory();
        factory.initializePrototypes();
    }
    
    public void generateDocuments() {
        // Create documents quickly using prototypes
        Document report1 = factory.createDocument("report", "Q3 Sales Report", "John Smith");
        Document report2 = factory.createDocument("report", "Marketing Analysis", "Jane Doe");
        Document proposal1 = factory.createDocument("proposal", "New Product Launch", "Bob Johnson");
        
        report1.generate();
        report2.generate();
        proposal1.generate();
    }
    
    public static void main(String[] args) {
        DocumentGenerationService service = new DocumentGenerationService();
        service.generateDocuments();
    }
}
```

**Common Pitfalls**:
1. **Shallow vs Deep Clone**: Forgetting to clone mutable fields leads to shared references
2. **Clone() Exception Handling**: Not properly handling CloneNotSupportedException
3. **Overuse**: Using prototype when simple instantiation would suffice
4. **Circular References**: Complex object graphs can cause infinite loops during cloning

**Deep Clone Implementation Tips**:
```java
// BAD: Shallow clone
public GameCharacter cloneCharacter() {
    return (GameCharacter) super.clone(); // equipment list is shared!
}

// GOOD: Deep clone
public GameCharacter cloneCharacter() {
    try {
        Warrior cloned = (Warrior) super.clone();
        cloned.equipment = new ArrayList<>(this.equipment);
        cloned.stats = new HashMap<>(this.stats);
        cloned.position = new Position(this.position.x, this.position.y);
        return cloned;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```

**When NOT to Use Prototype**:
- When object creation is simple and fast
- When you need different initialization logic for each instance
- When objects have many mutable references making deep cloning complex
- When serialization-based cloning would be simpler

**Modern Alternatives**:
```java
// Copy constructor approach
public class User {
    private String name;
    private List<String> roles;
    
    public User(String name, List<String> roles) {
        this.name = name;
        this.roles = new ArrayList<>(roles);
    }
    
    // Copy constructor
    public User(User other) {
        this.name = other.name;
        this.roles = new ArrayList<>(other.roles);
    }
}

// Builder pattern for variations
User template = new User.Builder("John").role("admin").build();
User copy = new User.Builder(template).name("Jane").build();
```

**MAANG Interview Talking Points**:
- "Prototype pattern is useful when object creation is expensive - like loading configuration from database"
- "Important to implement deep cloning correctly to avoid shared mutable state"
- "Registry pattern often used with Prototype to manage different prototype instances"
- "Consider copy constructors or builders as modern alternatives to Cloneable interface"

**Resources**:
- https://refactoring.guru/design-patterns/prototype/java/example
- https://www.baeldung.com/java-prototype-pattern
- Effective Java by Joshua Bloch (Item 13: Override clone judiciously)
- https://www.geeksforgeeks.org/prototype-design-pattern/

### 2.6 Object Pool
ELI5: Library of reusable bikes instead of buying new each ride.
Java Sketch:
```java
class ConnectionPool { BlockingQueue<Conn> q; Conn acquire() throws InterruptedException { return q.take(); } void release(Conn c){ q.offer(c);} }
```
Scenario: DB connections; thread pools.
Pitfall: Leaks when not returned; contention.

---
## 3. Structural Patterns

### 3.1 Adapter
**ELI5**: Like using a plug adapter to fit your US phone charger into a European wall socket - the phone and socket don't change, but the adapter makes them work together.

**Core Idea**: Allow incompatible interfaces to work together by creating a bridge between them without modifying the existing code.

**When to Use**:
- When you need to use an existing class with an incompatible interface
- When integrating third-party libraries with different APIs
- When you want to reuse legacy code in new systems
- When you need to make two existing classes work together

**Java Implementation 1** (Payment Gateway Integration):
```java
// Target interface - what our application expects
interface PaymentProcessor {
    PaymentResult processPayment(String merchantId, double amount, String currency, PaymentDetails details);
    boolean validatePayment(PaymentDetails details);
    String getProcessorName();
}

// Our application's payment details format
class PaymentDetails {
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardholderName;
    private String billingAddress;
    
    public PaymentDetails(String cardNumber, String expiryDate, String cvv, String cardholderName, String billingAddress) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardholderName = cardholderName;
        this.billingAddress = billingAddress;
    }
    
    // Getters
    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
    public String getCardholderName() { return cardholderName; }
    public String getBillingAddress() { return billingAddress; }
}

class PaymentResult {
    private boolean success;
    private String transactionId;
    private String errorMessage;
    
    public PaymentResult(boolean success, String transactionId, String errorMessage) {
        this.success = success;
        this.transactionId = transactionId;
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getErrorMessage() { return errorMessage; }
}

// Legacy Stripe API - incompatible interface (Adaptee)
class LegacyStripeGateway {
    public StripeResponse chargeCard(StripeRequest request) {
        System.out.println("Processing Stripe payment for $" + request.getAmountCents() / 100.0);
        
        // Stripe's validation logic
        if (request.getCardToken() == null || request.getCardToken().isEmpty()) {
            return new StripeResponse(false, null, "Invalid card token");
        }
        
        if (request.getAmountCents() <= 0) {
            return new StripeResponse(false, null, "Invalid amount");
        }
        
        // Simulate Stripe processing
        boolean success = Math.random() > 0.1; // 90% success rate
        if (success) {
            String transactionId = "ch_" + System.currentTimeMillis();
            return new StripeResponse(true, transactionId, null);
        } else {
            return new StripeResponse(false, null, "Card declined");
        }
    }
    
    public boolean validateCardToken(String cardToken) {
        return cardToken != null && cardToken.startsWith("tok_") && cardToken.length() > 10;
    }
    
    private String createCardToken(String cardNumber, String expiry, String cvc) {
        // Simulate Stripe tokenization
        if (cardNumber != null && cardNumber.length() >= 13 && expiry != null && cvc != null) {
            return "tok_" + Math.abs(cardNumber.hashCode());
        }
        return null;
    }
}

// Stripe's request/response formats
class StripeRequest {
    private String cardToken;
    private long amountCents;
    private String currency;
    private String description;
    
    public StripeRequest(String cardToken, long amountCents, String currency, String description) {
        this.cardToken = cardToken;
        this.amountCents = amountCents;
        this.currency = currency;
        this.description = description;
    }
    
    public String getCardToken() { return cardToken; }
    public long getAmountCents() { return amountCents; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
}

class StripeResponse {
    private boolean successful;
    private String chargeId;
    private String errorMessage;
    
    public StripeResponse(boolean successful, String chargeId, String errorMessage) {
        this.successful = successful;
        this.chargeId = chargeId;
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccessful() { return successful; }
    public String getChargeId() { return chargeId; }
    public String getErrorMessage() { return errorMessage; }
}

// Adapter implementation
class StripePaymentAdapter implements PaymentProcessor {
    private final LegacyStripeGateway stripeGateway;
    
    public StripePaymentAdapter(LegacyStripeGateway stripeGateway) {
        this.stripeGateway = stripeGateway;
    }
    
    @Override
    public PaymentResult processPayment(String merchantId, double amount, String currency, PaymentDetails details) {
        try {
            // Convert our format to Stripe's format
            String cardToken = createStripeCardToken(details);
            if (cardToken == null) {
                return new PaymentResult(false, null, "Failed to tokenize card");
            }
            
            long amountCents = Math.round(amount * 100); // Convert dollars to cents
            String description = "Payment for merchant: " + merchantId;
            
            StripeRequest stripeRequest = new StripeRequest(cardToken, amountCents, currency, description);
            
            // Call legacy Stripe API
            StripeResponse stripeResponse = stripeGateway.chargeCard(stripeRequest);
            
            // Convert Stripe response back to our format
            return new PaymentResult(
                stripeResponse.isSuccessful(),
                stripeResponse.getChargeId(),
                stripeResponse.getErrorMessage()
            );
            
        } catch (Exception e) {
            return new PaymentResult(false, null, "Adapter error: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validatePayment(PaymentDetails details) {
        String cardToken = createStripeCardToken(details);
        return cardToken != null && stripeGateway.validateCardToken(cardToken);
    }
    
    @Override
    public String getProcessorName() {
        return "Stripe (via Adapter)";
    }
    
    private String createStripeCardToken(PaymentDetails details) {
        // Convert our card details to Stripe token format
        // In real implementation, this would call Stripe's tokenization API
        if (details.getCardNumber() == null || details.getExpiryDate() == null || details.getCvv() == null) {
            return null;
        }
        
        // Simulate tokenization
        return "tok_" + Math.abs(details.getCardNumber().hashCode());
    }
}

// Third-party PayPal API - different incompatible interface
class PayPalGateway {
    public PayPalResult executePayment(PayPalPayment payment) {
        System.out.println("Processing PayPal payment for $" + payment.getAmount());
        
        if (payment.getPayerEmail() == null || !payment.getPayerEmail().contains("@")) {
            return new PayPalResult("FAILED", null, "Invalid payer email");
        }
        
        // Simulate PayPal processing
        boolean success = Math.random() > 0.05; // 95% success rate
        if (success) {
            String paymentId = "PAYID-" + System.currentTimeMillis();
            return new PayPalResult("COMPLETED", paymentId, null);
        } else {
            return new PayPalResult("FAILED", null, "Payment not approved");
        }
    }
    
    public boolean verifyPayerAccount(String email) {
        return email != null && email.contains("@") && email.endsWith(".com");
    }
}

class PayPalPayment {
    private String payerEmail;
    private double amount;
    private String currency;
    private String intent;
    
    public PayPalPayment(String payerEmail, double amount, String currency, String intent) {
        this.payerEmail = payerEmail;
        this.amount = amount;
        this.currency = currency;
        this.intent = intent;
    }
    
    public String getPayerEmail() { return payerEmail; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getIntent() { return intent; }
}

class PayPalResult {
    private String status;
    private String paymentId;
    private String errorDetail;
    
    public PayPalResult(String status, String paymentId, String errorDetail) {
        this.status = status;
        this.paymentId = paymentId;
        this.errorDetail = errorDetail;
    }
    
    public String getStatus() { return status; }
    public String getPaymentId() { return paymentId; }
    public String getErrorDetail() { return errorDetail; }
    public boolean isCompleted() { return "COMPLETED".equals(status); }
}

// PayPal Adapter
class PayPalPaymentAdapter implements PaymentProcessor {
    private final PayPalGateway paypalGateway;
    
    public PayPalPaymentAdapter(PayPalGateway paypalGateway) {
        this.paypalGateway = paypalGateway;
    }
    
    @Override
    public PaymentResult processPayment(String merchantId, double amount, String currency, PaymentDetails details) {
        try {
            // For PayPal, we'll use the billing address as email (simplified)
            String payerEmail = extractEmailFromBillingAddress(details.getBillingAddress());
            if (payerEmail == null) {
                return new PaymentResult(false, null, "No valid email found in billing address");
            }
            
            PayPalPayment payment = new PayPalPayment(payerEmail, amount, currency, "sale");
            PayPalResult result = paypalGateway.executePayment(payment);
            
            return new PaymentResult(
                result.isCompleted(),
                result.getPaymentId(),
                result.getErrorDetail()
            );
            
        } catch (Exception e) {
            return new PaymentResult(false, null, "PayPal adapter error: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validatePayment(PaymentDetails details) {
        String email = extractEmailFromBillingAddress(details.getBillingAddress());
        return email != null && paypalGateway.verifyPayerAccount(email);
    }
    
    @Override
    public String getProcessorName() {
        return "PayPal (via Adapter)";
    }
    
    private String extractEmailFromBillingAddress(String billingAddress) {
        // Simplified email extraction from billing address
        if (billingAddress != null && billingAddress.contains("@")) {
            String[] parts = billingAddress.split("\\s+");
            for (String part : parts) {
                if (part.contains("@")) {
                    return part;
                }
            }
        }
        return null;
    }
}

// Client code - works with any payment processor through the common interface
class PaymentService {
    private PaymentProcessor processor;
    
    public PaymentService(PaymentProcessor processor) {
        this.processor = processor;
    }
    
    public PaymentResult processOrderPayment(String orderId, String merchantId, double amount, PaymentDetails details) {
        System.out.println("Processing payment for order " + orderId + " using " + processor.getProcessorName());
        
        // Validate before processing
        if (!processor.validatePayment(details)) {
            return new PaymentResult(false, null, "Payment validation failed");
        }
        
        // Process payment
        PaymentResult result = processor.processPayment(merchantId, amount, "USD", details);
        
        if (result.isSuccess()) {
            System.out.println("Payment successful! Transaction ID: " + result.getTransactionId());
        } else {
            System.out.println("Payment failed: " + result.getErrorMessage());
        }
        
        return result;
    }
    
    public void switchProcessor(PaymentProcessor newProcessor) {
        this.processor = newProcessor;
        System.out.println("Switched to payment processor: " + newProcessor.getProcessorName());
    }
}

// Usage example
public class PaymentGatewayIntegration {
    public static void main(String[] args) {
        // Create payment details
        PaymentDetails creditCardDetails = new PaymentDetails(
            "4532123456789012", 
            "12/25", 
            "123", 
            "John Doe", 
            "123 Main St, john.doe@example.com"
        );
        
        PaymentDetails paypalDetails = new PaymentDetails(
            null, 
            null, 
            null, 
            "Jane Smith", 
            "456 Oak Ave, jane.smith@paypal.com"
        );
        
        // Create adapters for different payment gateways
        PaymentProcessor stripeAdapter = new StripePaymentAdapter(new LegacyStripeGateway());
        PaymentProcessor paypalAdapter = new PayPalPaymentAdapter(new PayPalGateway());
        
        // Use payment service with different processors
        PaymentService paymentService = new PaymentService(stripeAdapter);
        
        // Process payment with Stripe
        paymentService.processOrderPayment("ORDER-001", "MERCHANT-123", 99.99, creditCardDetails);
        
        // Switch to PayPal and process another payment
        paymentService.switchProcessor(paypalAdapter);
        paymentService.processOrderPayment("ORDER-002", "MERCHANT-123", 149.99, paypalDetails);
        
        // Switch back to Stripe
        paymentService.switchProcessor(stripeAdapter);
        paymentService.processOrderPayment("ORDER-003", "MERCHANT-123", 79.99, creditCardDetails);
    }
}
```

**Common Pitfalls**:
1. **Data Loss**: Losing information during interface translation
2. **Performance Overhead**: Multiple conversions can impact performance  
3. **Complex Mappings**: When interfaces are very different, adapter becomes complex
4. **Leaky Abstraction**: Exposing adaptee-specific details through the adapter

**When NOT to Use Adapter**:
- When you can modify the existing classes to be compatible
- When the interfaces are so different that adaptation is complex and error-prone
- When performance overhead is unacceptable
- When a simple wrapper or facade would suffice

**Object Adapter vs Class Adapter**:
```java
// Object Adapter (Composition - Preferred in Java)
class ObjectAdapter implements Target {
    private Adaptee adaptee;
    
    public ObjectAdapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    
    public void request() {
        adaptee.specificRequest(); // Delegation
    }
}

// Class Adapter (Inheritance - Limited in Java due to single inheritance)
class ClassAdapter extends Adaptee implements Target {
    public void request() {
        specificRequest(); // Inheritance
    }
}
```

**Real-World Examples**:
- JDBC drivers adapting database-specific APIs to standard JDBC interface
- SLF4J adapting different logging frameworks (Logback, Log4j) to unified API
- Spring framework adapters for different view technologies
- Media format converters

**MAANG Interview Talking Points**:
- "Adapter pattern enables integration without modifying existing stable code"
- "Composition-based object adapter is preferred over inheritance-based class adapter"
- "Essential for integrating third-party libraries with incompatible interfaces"
- "Consider two-way adapters when bidirectional communication is needed"

**Resources**:
- https://refactoring.guru/design-patterns/adapter/java/example
- https://www.baeldung.com/java-adapter-pattern
- Head First Design Patterns (Adapter chapter)
- https://www.tutorialspoint.com/design_pattern/adapter_pattern.htm

### 3.2 Bridge
ELI5: Separate remote control and TV so you can mix & match.
Java:
```java
interface Renderer { void drawCircle(int r); }
abstract class Shape { protected Renderer r; Shape(Renderer r){this.r=r;} abstract void draw(); }
class Circle extends Shape { int radius; Circle(Renderer r,int radius){super(r);this.radius=radius;} void draw(){ r.drawCircle(radius);} }
```
Scenario: Vary abstraction & implementation independently (e.g., shapes × APIs).
Pitfall: Over-engineering when simple inheritance suffices.

### 3.3 Composite
ELI5: A box can hold toys or other boxes.
Java:
```java
interface FileNode { long size(); }
class FileLeaf implements FileNode { long bytes; public long size(){ return bytes; }}
class Dir implements FileNode { List<FileNode> children; public long size(){ return children.stream().mapToLong(FileNode::size).sum(); }}
```
Scenario: UI scene graph; file systems.
Pitfall: Hard to enforce constraints across heterogenous nodes.

### 3.4 Decorator
**ELI5**: Like wrapping your ice cream with sprinkles, then chocolate sauce, then whipped cream - each wrapper adds something new without changing the ice cream itself.

**Core Idea**: Attach additional responsibilities to objects dynamically without altering their structure, providing a flexible alternative to subclassing.

**When to Use**:
- When you want to add behavior to objects without changing their interface
- When you need to add responsibilities dynamically and transparently
- When subclassing would result in an explosion of classes
- When you want to combine multiple behaviors flexibly

**Java Implementation 1** (Data Processing Pipeline):
```java
// Component interface
interface DataProcessor {
    String process(String data);
    String getDescription();
}

// Concrete component - basic data processor
class BaseDataProcessor implements DataProcessor {
    @Override
    public String process(String data) {
        // Basic processing - just return the data as-is
        return data;
    }
    
    @Override
    public String getDescription() {
        return "Base Data Processor";
    }
}

// Abstract decorator
abstract class DataProcessorDecorator implements DataProcessor {
    protected DataProcessor wrappedProcessor;
    
    public DataProcessorDecorator(DataProcessor processor) {
        this.wrappedProcessor = processor;
    }
    
    @Override
    public String process(String data) {
        return wrappedProcessor.process(data);
    }
    
    @Override
    public String getDescription() {
        return wrappedProcessor.getDescription();
    }
}

// Concrete decorators
class EncryptionDecorator extends DataProcessorDecorator {
    private String encryptionAlgorithm;
    
    public EncryptionDecorator(DataProcessor processor, String algorithm) {
        super(processor);
        this.encryptionAlgorithm = algorithm;
    }
    
    @Override
    public String process(String data) {
        String processed = super.process(data);
        return encrypt(processed);
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " -> " + encryptionAlgorithm + " Encryption";
    }
    
    private String encrypt(String data) {
        // Simulate encryption
        System.out.println("Encrypting data with " + encryptionAlgorithm);
        return "[ENCRYPTED:" + encryptionAlgorithm + ":" + data + "]";
    }
}

class CompressionDecorator extends DataProcessorDecorator {
    private String compressionType;
    private double compressionRatio;
    
    public CompressionDecorator(DataProcessor processor, String compressionType) {
        super(processor);
        this.compressionType = compressionType;
        this.compressionRatio = switch (compressionType.toLowerCase()) {
            case "gzip" -> 0.6;
            case "lz4" -> 0.8;
            case "brotli" -> 0.5;
            default -> 0.7;
        };
    }
    
    @Override
    public String process(String data) {
        String processed = super.process(data);
        return compress(processed);
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " -> " + compressionType + " Compression";
    }
    
    private String compress(String data) {
        System.out.println("Compressing data with " + compressionType + 
                          " (ratio: " + (int)(compressionRatio * 100) + "%)");
        int compressedLength = (int) (data.length() * compressionRatio);
        return "[COMPRESSED:" + compressionType + ":" + compressedLength + "bytes:" + data + "]";
    }
}

class ValidationDecorator extends DataProcessorDecorator {
    private String validationRule;
    
    public ValidationDecorator(DataProcessor processor, String validationRule) {
        super(processor);
        this.validationRule = validationRule;
    }
    
    @Override
    public String process(String data) {
        if (!validate(data)) {
            throw new IllegalArgumentException("Data validation failed: " + validationRule);
        }
        return super.process(data);
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " -> " + validationRule + " Validation";
    }
    
    private boolean validate(String data) {
        System.out.println("Validating data with rule: " + validationRule);
        
        return switch (validationRule.toLowerCase()) {
            case "not-null" -> data != null;
            case "not-empty" -> data != null && !data.trim().isEmpty();
            case "json" -> data != null && data.trim().startsWith("{") && data.trim().endsWith("}");
            case "xml" -> data != null && data.trim().startsWith("<") && data.trim().endsWith(">");
            case "email" -> data != null && data.contains("@") && data.contains(".");
            default -> true; // Default validation passes
        };
    }
}

class LoggingDecorator extends DataProcessorDecorator {
    private String logLevel;
    private long startTime;
    
    public LoggingDecorator(DataProcessor processor, String logLevel) {
        super(processor);
        this.logLevel = logLevel;
    }
    
    @Override
    public String process(String data) {
        logBefore(data);
        startTime = System.currentTimeMillis();
        
        try {
            String result = super.process(data);
            logAfter(data, result, true);
            return result;
        } catch (Exception e) {
            logAfter(data, null, false);
            throw e;
        }
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " -> " + logLevel + " Logging";
    }
    
    private void logBefore(String data) {
        System.out.println("[" + logLevel + "] Processing started - Input length: " + 
                          (data != null ? data.length() : 0) + " characters");
    }
    
    private void logAfter(String input, String output, boolean success) {
        long duration = System.currentTimeMillis() - startTime;
        if (success) {
            System.out.println("[" + logLevel + "] Processing completed in " + duration + 
                              "ms - Output length: " + (output != null ? output.length() : 0) + " characters");
        } else {
            System.out.println("[" + logLevel + "] Processing failed after " + duration + "ms");
        }
    }
}

class CachingDecorator extends DataProcessorDecorator {
    private Map<String, String> cache;
    private int maxCacheSize;
    private int hitCount;
    private int missCount;
    
    public CachingDecorator(DataProcessor processor, int maxCacheSize) {
        super(processor);
        this.cache = new LinkedHashMap<String, String>(maxCacheSize + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > maxCacheSize;
            }
        };
        this.maxCacheSize = maxCacheSize;
        this.hitCount = 0;
        this.missCount = 0;
    }
    
    @Override
    public String process(String data) {
        String cacheKey = generateCacheKey(data);
        
        // Check cache first
        if (cache.containsKey(cacheKey)) {
            hitCount++;
            System.out.println("Cache HIT - returning cached result");
            return cache.get(cacheKey);
        }
        
        // Cache miss - process and cache result
        missCount++;
        System.out.println("Cache MISS - processing and caching result");
        String result = super.process(data);
        cache.put(cacheKey, result);
        
        return result;
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " -> Caching (hits: " + hitCount + 
               ", misses: " + missCount + ", size: " + cache.size() + "/" + maxCacheSize + ")";
    }
    
    private String generateCacheKey(String data) {
        // Simple hash-based cache key
        return "key_" + Math.abs(data.hashCode());
    }
    
    public void clearCache() {
        cache.clear();
        hitCount = 0;
        missCount = 0;
        System.out.println("Cache cleared");
    }
}

// Usage examples
public class DataPipelineExample {
    public static void main(String[] args) {
        // Example 1: Simple processing chain
        System.out.println("=== Example 1: Basic Chain ===");
        DataProcessor basicProcessor = new BaseDataProcessor();
        basicProcessor = new ValidationDecorator(basicProcessor, "not-empty");
        basicProcessor = new LoggingDecorator(basicProcessor, "INFO");
        
        String result1 = basicProcessor.process("Hello World");
        System.out.println("Result: " + result1);
        System.out.println("Description: " + basicProcessor.getDescription());
        
        System.out.println("\n=== Example 2: Complex Processing Chain ===");
        // Create a complex processing pipeline
        DataProcessor complexProcessor = new BaseDataProcessor();
        
        // Add validation layer
        complexProcessor = new ValidationDecorator(complexProcessor, "not-empty");
        
        // Add logging layer
        complexProcessor = new LoggingDecorator(complexProcessor, "DEBUG");
        
        // Add caching layer
        complexProcessor = new CachingDecorator(complexProcessor, 100);
        
        // Add compression layer
        complexProcessor = new CompressionDecorator(complexProcessor, "gzip");
        
        // Add encryption layer
        complexProcessor = new EncryptionDecorator(complexProcessor, "AES-256");
        
        // Process data
        String jsonData = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        System.out.println("Processing: " + jsonData);
        String result2 = complexProcessor.process(jsonData);
        System.out.println("Final Result: " + result2);
        System.out.println("Pipeline Description: " + complexProcessor.getDescription());
        
        System.out.println("\n=== Example 3: Dynamic Decoration ===");
        // Demonstrate dynamic behavior changes
        DataProcessor dynamicProcessor = new BaseDataProcessor();
        
        // Start with basic logging
        dynamicProcessor = new LoggingDecorator(dynamicProcessor, "INFO");
        
        String testData = "Test data for processing";
        System.out.println("Step 1 - Basic logging:");
        dynamicProcessor.process(testData);
        
        // Add validation
        dynamicProcessor = new ValidationDecorator(dynamicProcessor, "not-null");
        System.out.println("\nStep 2 - Added validation:");
        dynamicProcessor.process(testData);
        
        // Add caching
        CachingDecorator cachingProcessor = new CachingDecorator(dynamicProcessor, 10);
        System.out.println("\nStep 3 - Added caching:");
        cachingProcessor.process(testData); // First call - cache miss
        cachingProcessor.process(testData); // Second call - cache hit
        
        System.out.println("\nFinal pipeline: " + cachingProcessor.getDescription());
        
        System.out.println("\n=== Example 4: Error Handling ===");
        try {
            DataProcessor errorProcessor = new BaseDataProcessor();
            errorProcessor = new ValidationDecorator(errorProcessor, "json");
            errorProcessor = new LoggingDecorator(errorProcessor, "ERROR");
            
            errorProcessor.process("This is not JSON"); // Should fail validation
        } catch (Exception e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
    }
}
```

**Java Implementation 2** (HTTP Request/Response Enhancement):
```java
// Component interface
interface HttpClient {
    HttpResponse execute(HttpRequest request);
    String getClientInfo();
}

// Basic HTTP client
class BasicHttpClient implements HttpClient {
    @Override
    public HttpResponse execute(HttpRequest request) {
        // Simulate basic HTTP execution
        System.out.println("Executing HTTP " + request.getMethod() + " " + request.getUrl());
        
        // Simulate network delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate response
        boolean success = Math.random() > 0.1; // 90% success rate
        int statusCode = success ? 200 : 500;
        String body = success ? "{\"status\":\"success\"}" : "{\"error\":\"server error\"}";
        
        return new HttpResponse(statusCode, body, System.currentTimeMillis());
    }
    
    @Override
    public String getClientInfo() {
        return "Basic HTTP Client";
    }
}

// Abstract decorator for HTTP client
abstract class HttpClientDecorator implements HttpClient {
    protected HttpClient wrappedClient;
    
    public HttpClientDecorator(HttpClient client) {
        this.wrappedClient = client;
    }
    
    @Override
    public HttpResponse execute(HttpRequest request) {
        return wrappedClient.execute(request);
    }
    
    @Override
    public String getClientInfo() {
        return wrappedClient.getClientInfo();
    }
}

// Retry decorator
class RetryDecorator extends HttpClientDecorator {
    private int maxRetries;
    private long retryDelayMs;
    
    public RetryDecorator(HttpClient client, int maxRetries, long retryDelayMs) {
        super(client);
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }
    
    @Override
    public HttpResponse execute(HttpRequest request) {
        int attempt = 1;
        HttpResponse response = null;
        
        while (attempt <= maxRetries) {
            System.out.println("Attempt " + attempt + "/" + maxRetries);
            response = super.execute(request);
            
            if (response.isSuccessful() || !isRetryableError(response)) {
                break;
            }
            
            if (attempt < maxRetries) {
                System.out.println("Request failed, retrying in " + retryDelayMs + "ms...");
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            attempt++;
        }
        
        return response;
    }
    
    @Override
    public String getClientInfo() {
        return super.getClientInfo() + " -> Retry(max:" + maxRetries + ", delay:" + retryDelayMs + "ms)";
    }
    
    private boolean isRetryableError(HttpResponse response) {
        int status = response.getStatusCode();
        return status >= 500 || status == 408 || status == 429; // Server errors, timeout, rate limit
    }
}

// Circuit breaker decorator
class CircuitBreakerDecorator extends HttpClientDecorator {
    private int failureThreshold;
    private long timeoutMs;
    private int failureCount;
    private long lastFailureTime;
    private boolean circuitOpen;
    
    public CircuitBreakerDecorator(HttpClient client, int failureThreshold, long timeoutMs) {
        super(client);
        this.failureThreshold = failureThreshold;
        this.timeoutMs = timeoutMs;
        this.failureCount = 0;
        this.circuitOpen = false;
    }
    
    @Override
    public HttpResponse execute(HttpRequest request) {
        if (circuitOpen) {
            if (System.currentTimeMillis() - lastFailureTime < timeoutMs) {
                System.out.println("Circuit breaker OPEN - request blocked");
                return new HttpResponse(503, "{\"error\":\"Circuit breaker open\"}", System.currentTimeMillis());
            } else {
                System.out.println("Circuit breaker attempting to close - trying request");
                circuitOpen = false;
                failureCount = 0;
            }
        }
        
        HttpResponse response = super.execute(request);
        
        if (response.isSuccessful()) {
            failureCount = 0; // Reset on success
        } else {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            
            if (failureCount >= failureThreshold) {
                circuitOpen = true;
                System.out.println("Circuit breaker OPENED after " + failureCount + " failures");
            }
        }
        
        return response;
    }
    
    @Override
    public String getClientInfo() {
        String state = circuitOpen ? "OPEN" : "CLOSED";
        return super.getClientInfo() + " -> CircuitBreaker(state:" + state + 
               ", failures:" + failureCount + "/" + failureThreshold + ")";
    }
}

// Request/Response timing decorator
class TimingDecorator extends HttpClientDecorator {
    private long totalRequests;
    private long totalTime;
    
    public TimingDecorator(HttpClient client) {
        super(client);
        this.totalRequests = 0;
        this.totalTime = 0;
    }
    
    @Override
    public HttpResponse execute(HttpRequest request) {
        long startTime = System.currentTimeMillis();
        HttpResponse response = super.execute(request);
        long duration = System.currentTimeMillis() - startTime;
        
        totalRequests++;
        totalTime += duration;
        
        System.out.printf("Request completed in %dms (avg: %.1fms)%n", 
                         duration, (double) totalTime / totalRequests);
        
        return response;
    }
    
    @Override
    public String getClientInfo() {
        return super.getClientInfo() + " -> Timing(requests:" + totalRequests + 
               ", avg:" + String.format("%.1f", (double) totalTime / Math.max(1, totalRequests)) + "ms)";
    }
}

// Supporting classes
class HttpRequest {
    private String method;
    private String url;
    private Map<String, String> headers;
    private String body;
    
    public HttpRequest(String method, String url) {
        this.method = method;
        this.url = url;
        this.headers = new HashMap<>();
    }
    
    public String getMethod() { return method; }
    public String getUrl() { return url; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
    
    public HttpRequest addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
    
    public HttpRequest setBody(String body) {
        this.body = body;
        return this;
    }
}

class HttpResponse {
    private int statusCode;
    private String body;
    private long timestamp;
    
    public HttpResponse(int statusCode, String body, long timestamp) {
        this.statusCode = statusCode;
        this.body = body;
        this.timestamp = timestamp;
    }
    
    public int getStatusCode() { return statusCode; }
    public String getBody() { return body; }
    public long getTimestamp() { return timestamp; }
    public boolean isSuccessful() { return statusCode >= 200 && statusCode < 300; }
}

// Usage example
public class HttpClientExample {
    public static void main(String[] args) {
        // Build a resilient HTTP client with multiple decorators
        HttpClient client = new BasicHttpClient();
        
        // Add timing capabilities
        client = new TimingDecorator(client);
        
        // Add retry logic
        client = new RetryDecorator(client, 3, 1000);
        
        // Add circuit breaker protection
        client = new CircuitBreakerDecorator(client, 5, 30000);
        
        System.out.println("HTTP Client Configuration: " + client.getClientInfo());
        
        // Simulate API calls
        HttpRequest request = new HttpRequest("GET", "https://api.example.com/users")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "MyApp/1.0");
        
        // Make several requests to demonstrate decorator behavior
        for (int i = 1; i <= 10; i++) {
            System.out.println("\n--- Request #" + i + " ---");
            HttpResponse response = client.execute(request);
            System.out.println("Response: " + response.getStatusCode() + " - " + response.getBody());
        }
        
        System.out.println("\nFinal client state: " + client.getClientInfo());
    }
}
```

**Common Pitfalls**:
1. **Deep Decorator Chains**: Too many decorators can impact performance and debugging
2. **Order Dependency**: Some decorators must be applied in specific order
3. **Interface Leakage**: Exposing decorator-specific methods breaks the pattern
4. **Memory Overhead**: Each decorator adds object overhead

**When NOT to Use Decorator**:
- When you have a simple, static set of behaviors
- When the core object interface changes frequently
- When performance is critical and decorator overhead is significant
- When composition relationships are complex and hard to manage

**Decorator vs Inheritance**:
```java
// Inheritance approach - inflexible
class EncryptedCompressedLogger extends CompressedLogger { /* fixed combination */ }

// Decorator approach - flexible
Logger logger = new EncryptionDecorator(new CompressionDecorator(new BasicLogger()));
// Can easily change: new CompressionDecorator(new EncryptionDecorator(new BasicLogger()));
```

**MAANG Interview Talking Points**:
- "Decorator enables adding cross-cutting concerns like logging, caching, security without modifying core logic"
- "Used extensively in Java I/O streams, Spring AOP, HTTP middleware chains"
- "Provides runtime composition vs compile-time inheritance, following composition over inheritance principle"
- "Important to consider decorator order and performance implications in high-throughput systems"

**Resources**:
- https://refactoring.guru/design-patterns/decorator/java/example
- https://www.baeldung.com/java-decorator-pattern
- Java I/O Design Patterns: https://docs.oracle.com/javase/tutorial/essential/io/streams.html
- Spring AOP documentation: https://docs.spring.io/spring-framework/reference/core/aop.html

### 3.5 Facade
ELI5: One friendly desk instead of visiting 5 departments.
Java:
```java
class VideoConverterFacade { void convert(String file, String format){ /* orchestrate codec, ffmpeg calls */ } }
```
Scenario: Simplify complex subsystem for clients.
Pitfall: Becoming god-object if not thin.

### 3.6 Flyweight
**ELI5**: Like sharing one recipe book among many cooks instead of each cook having their own copy - save memory by sharing common data.

**Core Idea**: Use sharing to support large numbers of fine-grained objects efficiently by separating intrinsic (shared) state from extrinsic (context-specific) state.

**When to Use**:
- When you need to create a large number of similar objects
- When the cost of storing all objects is prohibitively high
- When most object state can be made extrinsic
- When groups of objects can be replaced by relatively few shared objects

**Java Implementation** (Text Rendering System):
```java
// Flyweight interface
interface CharacterFlyweight {
    void render(int x, int y, String color, int fontSize);
}

// Concrete flyweight - stores intrinsic state only
class Character implements CharacterFlyweight {
    private final char symbol; // Intrinsic state (shared)
    private final String fontFamily; // Intrinsic state (shared)
    
    public Character(char symbol, String fontFamily) {
        this.symbol = symbol;
        this.fontFamily = fontFamily;
        System.out.println("Creating flyweight for character: " + symbol + " in " + fontFamily);
    }
    
    @Override
    public void render(int x, int y, String color, int fontSize) {
        // Use both intrinsic and extrinsic state
        System.out.printf("Rendering '%c' at (%d, %d) in %s, size %d, color %s%n", 
                         symbol, x, y, fontFamily, fontSize, color);
    }
    
    public char getSymbol() { return symbol; }
    public String getFontFamily() { return fontFamily; }
}

// Flyweight factory
class CharacterFactory {
    private static final Map<String, CharacterFlyweight> flyweights = new HashMap<>();
    private static int flyweightCount = 0;
    
    public static CharacterFlyweight getCharacter(char symbol, String fontFamily) {
        String key = symbol + ":" + fontFamily;
        
        CharacterFlyweight flyweight = flyweights.get(key);
        if (flyweight == null) {
            flyweight = new Character(symbol, fontFamily);
            flyweights.put(key, flyweight);
            flyweightCount++;
        }
        
        return flyweight;
    }
    
    public static int getFlyweightCount() {
        return flyweightCount;
    }
    
    public static void printStatistics() {
        System.out.println("Total flyweights created: " + flyweightCount);
        System.out.println("Flyweight instances in memory: " + flyweights.size());
    }
}

// Context class - stores extrinsic state
class TextCharacter {
    private final CharacterFlyweight flyweight;
    private final int x, y; // Extrinsic state (position)
    private final String color; // Extrinsic state (color)
    private final int fontSize; // Extrinsic state (size)
    
    public TextCharacter(char symbol, String fontFamily, int x, int y, String color, int fontSize) {
        this.flyweight = CharacterFactory.getCharacter(symbol, fontFamily);
        this.x = x;
        this.y = y;
        this.color = color;
        this.fontSize = fontSize;
    }
    
    public void render() {
        flyweight.render(x, y, color, fontSize);
    }
}

// Document class that uses many characters
class Document {
    private List<TextCharacter> characters;
    private String title;
    
    public Document(String title) {
        this.title = title;
        this.characters = new ArrayList<>();
    }
    
    public void addCharacter(char symbol, String fontFamily, int x, int y, String color, int fontSize) {
        TextCharacter character = new TextCharacter(symbol, fontFamily, x, y, color, fontSize);
        characters.add(character);
    }
    
    public void addText(String text, String fontFamily, int startX, int startY, String color, int fontSize) {
        int x = startX;
        for (char c : text.toCharArray()) {
            if (c != ' ') { // Skip spaces for simplicity
                addCharacter(c, fontFamily, x, startY, color, fontSize);
            }
            x += fontSize; // Simple character spacing
        }
    }
    
    public void render() {
        System.out.println("Rendering document: " + title);
        System.out.println("Total characters: " + characters.size());
        
        for (TextCharacter character : characters) {
            character.render();
        }
        
        System.out.println();
        CharacterFactory.printStatistics();
    }
    
    public int getCharacterCount() {
        return characters.size();
    }
}

// Usage example
public class FlyweightExample {
    public static void main(String[] args) {
        Document document = new Document("Sample Document");
        
        // Add text with different properties
        document.addText("Hello", "Arial", 10, 50, "black", 12);
        document.addText("World", "Arial", 100, 50, "red", 12);
        document.addText("Java", "Times", 10, 80, "blue", 14);
        document.addText("Design", "Arial", 100, 80, "green", 12);
        document.addText("Patterns", "Times", 10, 110, "purple", 16);
        
        // Add more text to demonstrate sharing
        document.addText("Hello", "Arial", 10, 140, "black", 12); // Reuses flyweights
        document.addText("Again", "Arial", 100, 140, "black", 12);
        
        System.out.println("=== Document Rendering ===");
        document.render();
        
        System.out.println("\n=== Memory Efficiency Demo ===");
        System.out.println("Total characters in document: " + document.getCharacterCount());
        System.out.println("Unique flyweight objects created: " + CharacterFactory.getFlyweightCount());
        System.out.println("Memory savings: " + 
                          (document.getCharacterCount() - CharacterFactory.getFlyweightCount()) + 
                          " fewer objects created");
    }
}
```

### 3.7 Proxy
**ELI5**: Like a personal assistant who stands in for you - they can screen calls, handle simple requests, and only bother you when necessary.

**Core Idea**: Provide a placeholder or surrogate for another object to control access to it.

**Java Implementation** (Virtual Proxy for Image Loading):
```java
// Subject interface
interface Image {
    void display();
    String getInfo();
    int getWidth();
    int getHeight();
}

// Real subject - expensive to create
class RealImage implements Image {
    private String filename;
    private byte[] imageData;
    private int width;
    private int height;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadImageFromDisk();
    }
    
    private void loadImageFromDisk() {
        System.out.println("Loading large image from disk: " + filename);
        
        // Simulate expensive loading operation
        try {
            Thread.sleep(2000); // Simulate 2-second load time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate image data loading
        this.imageData = new byte[1024 * 1024]; // 1MB of image data
        this.width = 1920;
        this.height = 1080;
        
        System.out.println("Image loaded: " + filename + " (" + width + "x" + height + ")");
    }
    
    @Override
    public void display() {
        System.out.println("Displaying image: " + filename + " [" + width + "x" + height + "]");
        // Actual image rendering logic would go here
    }
    
    @Override
    public String getInfo() {
        return "RealImage: " + filename + " (" + width + "x" + height + ") - " + 
               (imageData.length / 1024) + "KB";
    }
    
    @Override
    public int getWidth() { return width; }
    
    @Override
    public int getHeight() { return height; }
}

// Virtual proxy - delays creation of real object
class ImageProxy implements Image {
    private String filename;
    private RealImage realImage;
    private boolean isLoaded = false;
    
    // Cached metadata to avoid loading for simple queries
    private int estimatedWidth = 1920;
    private int estimatedHeight = 1080;
    
    public ImageProxy(String filename) {
        this.filename = filename;
        System.out.println("ImageProxy created for: " + filename);
    }
    
    @Override
    public void display() {
        if (realImage == null) {
            System.out.println("Proxy: Loading real image on first display...");
            realImage = new RealImage(filename);
            isLoaded = true;
        }
        realImage.display();
    }
    
    @Override
    public String getInfo() {
        if (realImage == null) {
            return "ImageProxy: " + filename + " (not loaded yet)";
        }
        return realImage.getInfo();
    }
    
    @Override
    public int getWidth() {
        if (realImage == null) {
            return estimatedWidth; // Return estimated value without loading
        }
        return realImage.getWidth();
    }
    
    @Override
    public int getHeight() {
        if (realImage == null) {
            return estimatedHeight; // Return estimated value without loading
        }
        return realImage.getHeight();
    }
    
    public boolean isImageLoaded() {
        return isLoaded;
    }
}

// Protection proxy example
interface DocumentAccess {
    void read();
    void write(String content);
    void delete();
    String getMetadata();
}

class SecureDocument implements DocumentAccess {
    private String content;
    private String filename;
    
    public SecureDocument(String filename, String content) {
        this.filename = filename;
        this.content = content;
    }
    
    @Override
    public void read() {
        System.out.println("Reading document: " + filename);
        System.out.println("Content: " + content);
    }
    
    @Override
    public void write(String content) {
        System.out.println("Writing to document: " + filename);
        this.content = content;
    }
    
    @Override
    public void delete() {
        System.out.println("Deleting document: " + filename);
        this.content = null;
    }
    
    @Override
    public String getMetadata() {
        return "Document: " + filename + ", Length: " + 
               (content != null ? content.length() : 0) + " characters";
    }
}

class DocumentSecurityProxy implements DocumentAccess {
    private SecureDocument document;
    private String userRole;
    private String username;
    
    public DocumentSecurityProxy(SecureDocument document, String username, String userRole) {
        this.document = document;
        this.username = username;
        this.userRole = userRole;
    }
    
    @Override
    public void read() {
        if (hasReadPermission()) {
            System.out.println("Access granted for " + username + " to read document");
            document.read();
        } else {
            System.out.println("Access denied: " + username + " cannot read this document");
        }
    }
    
    @Override
    public void write(String content) {
        if (hasWritePermission()) {
            System.out.println("Access granted for " + username + " to write document");
            document.write(content);
        } else {
            System.out.println("Access denied: " + username + " cannot write to this document");
        }
    }
    
    @Override
    public void delete() {
        if (hasDeletePermission()) {
            System.out.println("Access granted for " + username + " to delete document");
            document.delete();
        } else {
            System.out.println("Access denied: " + username + " cannot delete this document");
        }
    }
    
    @Override
    public String getMetadata() {
        // Metadata is generally accessible to all users
        return document.getMetadata() + " [accessed by: " + username + "]";
    }
    
    private boolean hasReadPermission() {
        return userRole.equals("admin") || userRole.equals("editor") || userRole.equals("viewer");
    }
    
    private boolean hasWritePermission() {
        return userRole.equals("admin") || userRole.equals("editor");
    }
    
    private boolean hasDeletePermission() {
        return userRole.equals("admin");
    }
}

// Caching proxy example
class CachingWebServiceProxy {
    private WebService realService;
    private Map<String, String> cache;
    private long cacheTimeout;
    private Map<String, Long> cacheTimestamps;
    
    public CachingWebServiceProxy(WebService realService, long cacheTimeoutMs) {
        this.realService = realService;
        this.cache = new HashMap<>();
        this.cacheTimestamps = new HashMap<>();
        this.cacheTimeout = cacheTimeoutMs;
    }
    
    public String getData(String key) {
        long currentTime = System.currentTimeMillis();
        
        // Check if data is in cache and not expired
        if (cache.containsKey(key)) {
            long cacheTime = cacheTimestamps.get(key);
            if (currentTime - cacheTime < cacheTimeout) {
                System.out.println("Cache HIT for key: " + key);
                return cache.get(key);
            } else {
                System.out.println("Cache EXPIRED for key: " + key);
                cache.remove(key);
                cacheTimestamps.remove(key);
            }
        }
        
        // Cache miss or expired - fetch from real service
        System.out.println("Cache MISS for key: " + key + " - fetching from service");
        String data = realService.fetchData(key);
        
        // Cache the result
        cache.put(key, data);
        cacheTimestamps.put(key, currentTime);
        
        return data;
    }
    
    public void clearCache() {
        cache.clear();
        cacheTimestamps.clear();
        System.out.println("Cache cleared");
    }
}

class WebService {
    public String fetchData(String key) {
        // Simulate network delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "Data for " + key + " (fetched at " + System.currentTimeMillis() + ")";
    }
}

// Usage examples
public class ProxyExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Virtual Proxy Example ===");
        
        // Create image proxies
        Image image1 = new ImageProxy("vacation_photo.jpg");
        Image image2 = new ImageProxy("family_portrait.png");
        
        // Check metadata without loading images
        System.out.println("Image1 dimensions: " + image1.getWidth() + "x" + image1.getHeight());
        System.out.println("Image1 info: " + image1.getInfo());
        
        // First display triggers loading
        System.out.println("\nFirst display:");
        image1.display();
        
        // Second display uses already loaded image
        System.out.println("\nSecond display:");
        image1.display();
        
        System.out.println("\n=== Protection Proxy Example ===");
        
        SecureDocument document = new SecureDocument("confidential.txt", "Top secret information");
        
        // Different users with different permissions
        DocumentAccess adminAccess = new DocumentSecurityProxy(document, "alice", "admin");
        DocumentAccess editorAccess = new DocumentSecurityProxy(document, "bob", "editor");
        DocumentAccess viewerAccess = new DocumentSecurityProxy(document, "charlie", "viewer");
        
        System.out.println("\nAdmin operations:");
        adminAccess.read();
        adminAccess.write("Updated secret information");
        adminAccess.delete();
        
        System.out.println("\nEditor operations:");
        editorAccess.read();
        editorAccess.write("Editor's changes");
        editorAccess.delete(); // Should be denied
        
        System.out.println("\nViewer operations:");
        viewerAccess.read();
        viewerAccess.write("Viewer's attempt"); // Should be denied
        
        System.out.println("\n=== Caching Proxy Example ===");
        
        WebService service = new WebService();
        CachingWebServiceProxy proxy = new CachingWebServiceProxy(service, 5000); // 5-second cache
        
        // First call - cache miss
        String data1 = proxy.getData("user123");
        System.out.println("Received: " + data1);
        
        // Second call - cache hit
        String data2 = proxy.getData("user123");
        System.out.println("Received: " + data2);
        
        // Wait for cache to expire
        Thread.sleep(6000);
        
        // Third call - cache expired
        String data3 = proxy.getData("user123");
        System.out.println("Received: " + data3);
    }
}
```

---
## 4. Behavioral Patterns

### 4.1 Strategy
**ELI5**: Like having different ways to solve a math problem - you pick the method that works best for each situation.

**Core Idea**: Define a family of algorithms, encapsulate each one, and make them interchangeable at runtime.

**When to Use**:
- When you have multiple ways to perform a task
- When you want to switch algorithms dynamically
- When you want to avoid long if-else or switch statements
- When algorithms have different trade-offs (speed vs memory, accuracy vs performance)

**Java Implementation 1** (Payment Processing):
```java
// Strategy interface
interface PaymentStrategy {
    PaymentResult processPayment(double amount, PaymentDetails details);
    boolean isAvailable();
    String getPaymentMethod();
}

// Strategy implementations
class CreditCardStrategy implements PaymentStrategy {
    private final String processorEndpoint;
    private final int maxRetries;
    
    public CreditCardStrategy(String processorEndpoint) {
        this.processorEndpoint = processorEndpoint;
        this.maxRetries = 3;
    }
    
    @Override
    public PaymentResult processPayment(double amount, PaymentDetails details) {
        // Credit card specific processing
        System.out.println("Processing $" + amount + " via Credit Card");
        
        // Validate credit card details
        if (!validateCreditCard(details.getCardNumber())) {
            return new PaymentResult(false, "Invalid credit card number");
        }
        
        // Process with retry logic
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Simulate API call to payment processor
                boolean success = callPaymentProcessor(amount, details);
                if (success) {
                    return new PaymentResult(true, "Payment successful via credit card");
                }
            } catch (Exception e) {
                System.out.println("Credit card attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == maxRetries) {
                    return new PaymentResult(false, "Credit card payment failed after " + maxRetries + " attempts");
                }
            }
        }
        
        return new PaymentResult(false, "Credit card payment failed");
    }
    
    @Override
    public boolean isAvailable() {
        // Check if credit card processor is available
        return checkProcessorHealth(processorEndpoint);
    }
    
    @Override
    public String getPaymentMethod() {
        return "Credit Card";
    }
    
    private boolean validateCreditCard(String cardNumber) {
        return cardNumber != null && cardNumber.length() >= 13;
    }
    
    private boolean callPaymentProcessor(double amount, PaymentDetails details) {
        // Simulate payment processing
        return Math.random() > 0.1; // 90% success rate
    }
    
    private boolean checkProcessorHealth(String endpoint) {
        return Math.random() > 0.05; // 95% uptime
    }
}

class PayPalStrategy implements PaymentStrategy {
    private final String apiKey;
    private final String secretKey;
    
    public PayPalStrategy(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }
    
    @Override
    public PaymentResult processPayment(double amount, PaymentDetails details) {
        System.out.println("Processing $" + amount + " via PayPal");
        
        if (!validatePayPalAccount(details.getEmail())) {
            return new PaymentResult(false, "Invalid PayPal account");
        }
        
        try {
            // PayPal-specific API call
            String token = authenticateWithPayPal();
            boolean success = processPayPalPayment(token, amount, details);
            
            if (success) {
                return new PaymentResult(true, "Payment successful via PayPal");
            } else {
                return new PaymentResult(false, "PayPal payment declined");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, "PayPal payment error: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isAvailable() {
        return authenticateWithPayPal() != null;
    }
    
    @Override
    public String getPaymentMethod() {
        return "PayPal";
    }
    
    private boolean validatePayPalAccount(String email) {
        return email != null && email.contains("@");
    }
    
    private String authenticateWithPayPal() {
        // Simulate PayPal authentication
        return "paypal_token_" + System.currentTimeMillis();
    }
    
    private boolean processPayPalPayment(String token, double amount, PaymentDetails details) {
        return Math.random() > 0.05; // 95% success rate
    }
}

class CryptocurrencyStrategy implements PaymentStrategy {
    private final String walletAddress;
    private final String cryptoType;
    
    public CryptocurrencyStrategy(String walletAddress, String cryptoType) {
        this.walletAddress = walletAddress;
        this.cryptoType = cryptoType;
    }
    
    @Override
    public PaymentResult processPayment(double amount, PaymentDetails details) {
        System.out.println("Processing $" + amount + " via " + cryptoType);
        
        try {
            // Convert USD to cryptocurrency
            double cryptoAmount = convertToCrypto(amount, cryptoType);
            
            // Process blockchain transaction
            String transactionId = processBlockchainTransaction(cryptoAmount, details.getWalletAddress());
            
            if (transactionId != null) {
                return new PaymentResult(true, "Crypto payment successful. TX: " + transactionId);
            } else {
                return new PaymentResult(false, "Blockchain transaction failed");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, "Cryptocurrency payment error: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isAvailable() {
        // Check blockchain network status
        return checkNetworkStatus();
    }
    
    @Override
    public String getPaymentMethod() {
        return cryptoType + " Cryptocurrency";
    }
    
    private double convertToCrypto(double usdAmount, String cryptoType) {
        // Simulate crypto conversion rates
        double rate = switch (cryptoType.toLowerCase()) {
            case "bitcoin" -> 0.000023; // 1 USD = 0.000023 BTC
            case "ethereum" -> 0.00041;  // 1 USD = 0.00041 ETH
            default -> 1.0;
        };
        return usdAmount * rate;
    }
    
    private String processBlockchainTransaction(double amount, String toAddress) {
        // Simulate blockchain processing
        if (Math.random() > 0.15) { // 85% success rate (lower due to network congestion)
            return "0x" + Long.toHexString(System.currentTimeMillis());
        }
        return null;
    }
    
    private boolean checkNetworkStatus() {
        return Math.random() > 0.1; // 90% network availability
    }
}

// Context class
class PaymentProcessor {
    private PaymentStrategy strategy;
    private List<PaymentStrategy> fallbackStrategies;
    
    public PaymentProcessor() {
        this.fallbackStrategies = new ArrayList<>();
    }
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void addFallbackStrategy(PaymentStrategy strategy) {
        fallbackStrategies.add(strategy);
    }
    
    public PaymentResult processPayment(double amount, PaymentDetails details) {
        // Try primary strategy
        if (strategy != null && strategy.isAvailable()) {
            PaymentResult result = strategy.processPayment(amount, details);
            if (result.isSuccessful()) {
                return result;
            }
            System.out.println("Primary payment method failed: " + result.getMessage());
        }
        
        // Try fallback strategies
        for (PaymentStrategy fallback : fallbackStrategies) {
            if (fallback.isAvailable()) {
                System.out.println("Trying fallback: " + fallback.getPaymentMethod());
                PaymentResult result = fallback.processPayment(amount, details);
                if (result.isSuccessful()) {
                    return result;
                }
            }
        }
        
        return new PaymentResult(false, "All payment methods failed");
    }
    
    public List<String> getAvailablePaymentMethods() {
        List<String> available = new ArrayList<>();
        if (strategy != null && strategy.isAvailable()) {
            available.add(strategy.getPaymentMethod());
        }
        for (PaymentStrategy fallback : fallbackStrategies) {
            if (fallback.isAvailable()) {
                available.add(fallback.getPaymentMethod());
            }
        }
        return available;
    }
}

// Supporting classes
class PaymentDetails {
    private String cardNumber;
    private String email;
    private String walletAddress;
    
    public PaymentDetails(String cardNumber, String email, String walletAddress) {
        this.cardNumber = cardNumber;
        this.email = email;
        this.walletAddress = walletAddress;
    }
    
    // getters
    public String getCardNumber() { return cardNumber; }
    public String getEmail() { return email; }
    public String getWalletAddress() { return walletAddress; }
}

class PaymentResult {
    private final boolean successful;
    private final String message;
    
    public PaymentResult(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }
    
    public boolean isSuccessful() { return successful; }
    public String getMessage() { return message; }
}

// Usage example
public class ECommerceCheckout {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();
        
        // Configure payment strategies
        processor.setPaymentStrategy(new CreditCardStrategy("https://api.creditprocessor.com"));
        processor.addFallbackStrategy(new PayPalStrategy("paypal_key", "paypal_secret"));
        processor.addFallbackStrategy(new CryptocurrencyStrategy("wallet123", "Bitcoin"));
        
        // Process payment
        PaymentDetails details = new PaymentDetails("4532123456789012", "user@example.com", "crypto_wallet_456");
        PaymentResult result = processor.processPayment(99.99, details);
        
        System.out.println("Payment result: " + result.getMessage());
        System.out.println("Available methods: " + processor.getAvailablePaymentMethods());
    }
}
```

**Java Implementation 2** (Data Compression):
```java
// Strategy interface
interface CompressionStrategy {
    byte[] compress(byte[] data);
    byte[] decompress(byte[] compressedData);
    String getAlgorithmName();
    CompressionMetrics getMetrics();
}

// Strategy implementations
class ZipCompressionStrategy implements CompressionStrategy {
    private int compressionLevel;
    
    public ZipCompressionStrategy(int compressionLevel) {
        this.compressionLevel = Math.max(1, Math.min(9, compressionLevel));
    }
    
    @Override
    public byte[] compress(byte[] data) {
        System.out.println("Compressing with ZIP (level " + compressionLevel + ")");
        
        // Simulate ZIP compression
        long startTime = System.nanoTime();
        byte[] compressed = simulateZipCompression(data);
        long endTime = System.nanoTime();
        
        updateMetrics(data.length, compressed.length, endTime - startTime);
        return compressed;
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) {
        System.out.println("Decompressing ZIP data");
        return simulateZipDecompression(compressedData);
    }
    
    @Override
    public String getAlgorithmName() {
        return "ZIP-" + compressionLevel;
    }
    
    @Override
    public CompressionMetrics getMetrics() {
        return new CompressionMetrics(0.6, 150, 200); // 60% compression, fast
    }
    
    private byte[] simulateZipCompression(byte[] data) {
        // Simulate compression ratio based on level
        double ratio = 0.9 - (compressionLevel * 0.05); // Higher level = better compression
        int compressedSize = (int) (data.length * ratio);
        return new byte[compressedSize];
    }
    
    private byte[] simulateZipDecompression(byte[] compressed) {
        // Simulate decompression
        return new byte[compressed.length * 2]; // Approximate original size
    }
    
    private void updateMetrics(int originalSize, int compressedSize, long timeNanos) {
        // Update compression metrics
    }
}

class LZ4CompressionStrategy implements CompressionStrategy {
    @Override
    public byte[] compress(byte[] data) {
        System.out.println("Compressing with LZ4 (optimized for speed)");
        
        long startTime = System.nanoTime();
        byte[] compressed = simulateLZ4Compression(data);
        long endTime = System.nanoTime();
        
        return compressed;
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) {
        System.out.println("Decompressing LZ4 data");
        return simulateLZ4Decompression(compressedData);
    }
    
    @Override
    public String getAlgorithmName() {
        return "LZ4";
    }
    
    @Override
    public CompressionMetrics getMetrics() {
        return new CompressionMetrics(0.75, 50, 80); // 75% compression, very fast
    }
    
    private byte[] simulateLZ4Compression(byte[] data) {
        // LZ4 is optimized for speed, not compression ratio
        int compressedSize = (int) (data.length * 0.75);
        return new byte[compressedSize];
    }
    
    private byte[] simulateLZ4Decompression(byte[] compressed) {
        return new byte[(int) (compressed.length * 1.33)];
    }
}

class BrotliCompressionStrategy implements CompressionStrategy {
    @Override
    public byte[] compress(byte[] data) {
        System.out.println("Compressing with Brotli (optimized for web content)");
        
        long startTime = System.nanoTime();
        byte[] compressed = simulateBrotliCompression(data);
        long endTime = System.nanoTime();
        
        return compressed;
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) {
        System.out.println("Decompressing Brotli data");
        return simulateBrotliDecompression(compressedData);
    }
    
    @Override
    public String getAlgorithmName() {
        return "Brotli";
    }
    
    @Override
    public CompressionMetrics getMetrics() {
        return new CompressionMetrics(0.45, 300, 250); // 45% compression, slower but better ratio
    }
    
    private byte[] simulateBrotliCompression(byte[] data) {
        // Brotli achieves better compression for text/web content
        int compressedSize = (int) (data.length * 0.45);
        return new byte[compressedSize];
    }
    
    private byte[] simulateBrotliDecompression(byte[] compressed) {
        return new byte[(int) (compressed.length * 2.22)];
    }
}

// Context class
class DataCompressor {
    private CompressionStrategy strategy;
    
    public DataCompressor(CompressionStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setCompressionStrategy(CompressionStrategy strategy) {
        this.strategy = strategy;
    }
    
    public byte[] compressData(byte[] data) {
        if (strategy == null) {
            throw new IllegalStateException("No compression strategy set");
        }
        
        System.out.println("Using " + strategy.getAlgorithmName() + " compression");
        return strategy.compress(data);
    }
    
    public byte[] decompressData(byte[] compressedData) {
        if (strategy == null) {
            throw new IllegalStateException("No compression strategy set");
        }
        
        return strategy.decompress(compressedData);
    }
    
    public CompressionMetrics getStrategyMetrics() {
        return strategy != null ? strategy.getMetrics() : null;
    }
}

// Strategy selector based on requirements
class CompressionStrategySelector {
    public static CompressionStrategy selectOptimalStrategy(CompressionRequirements requirements) {
        if (requirements.prioritizeSpeed()) {
            return new LZ4CompressionStrategy();
        } else if (requirements.prioritizeSize()) {
            return new BrotliCompressionStrategy();
        } else {
            // Balanced approach
            return new ZipCompressionStrategy(6);
        }
    }
    
    public static CompressionStrategy selectByDataType(String dataType) {
        return switch (dataType.toLowerCase()) {
            case "text", "html", "css", "js" -> new BrotliCompressionStrategy();
            case "log", "realtime" -> new LZ4CompressionStrategy();
            case "archive", "backup" -> new ZipCompressionStrategy(9);
            default -> new ZipCompressionStrategy(6);
        };
    }
}

// Supporting classes
class CompressionMetrics {
    private final double compressionRatio;
    private final long compressionTimeMs;
    private final long decompressionTimeMs;
    
    public CompressionMetrics(double compressionRatio, long compressionTimeMs, long decompressionTimeMs) {
        this.compressionRatio = compressionRatio;
        this.compressionTimeMs = compressionTimeMs;
        this.decompressionTimeMs = decompressionTimeMs;
    }
    
    // getters
    public double getCompressionRatio() { return compressionRatio; }
    public long getCompressionTimeMs() { return compressionTimeMs; }
    public long getDecompressionTimeMs() { return decompressionTimeMs; }
}

class CompressionRequirements {
    private final boolean prioritizeSpeed;
    private final boolean prioritizeSize;
    
    public CompressionRequirements(boolean prioritizeSpeed, boolean prioritizeSize) {
        this.prioritizeSpeed = prioritizeSpeed;
        this.prioritizeSize = prioritizeSize;
    }
    
    public boolean prioritizeSpeed() { return prioritizeSpeed; }
    public boolean prioritizeSize() { return prioritizeSize; }
}

// Usage example
public class FileCompressionService {
    public static void main(String[] args) {
        DataCompressor compressor = new DataCompressor(new ZipCompressionStrategy(6));
        
        // Simulate different data types and requirements
        byte[] textData = "This is some text data to compress".getBytes();
        byte[] logData = "LOG: 2023-12-01 10:30:45 INFO User logged in".getBytes();
        
        // Dynamically select strategy based on data type
        CompressionStrategy textStrategy = CompressionStrategySelector.selectByDataType("text");
        compressor.setCompressionStrategy(textStrategy);
        byte[] compressedText = compressor.compressData(textData);
        
        // Select strategy based on requirements
        CompressionRequirements speedRequirements = new CompressionRequirements(true, false);
        CompressionStrategy speedStrategy = CompressionStrategySelector.selectOptimalStrategy(speedRequirements);
        compressor.setCompressionStrategy(speedStrategy);
        byte[] compressedLog = compressor.compressData(logData);
        
        System.out.println("Text compressed with: " + textStrategy.getAlgorithmName());
        System.out.println("Log compressed with: " + speedStrategy.getAlgorithmName());
    }
}
```

**Common Pitfalls**:
1. **Strategy Explosion**: Creating too many strategies for minor variations
2. **Complex Strategy Selection**: Making the strategy selection logic too complex
3. **Stateful Strategies**: Strategies should generally be stateless and reusable
4. **Performance Overhead**: Don't use strategy pattern for simple, static algorithms

**When NOT to Use Strategy**:
- When you have only one algorithm and no plans for others
- When the algorithm never changes
- When the cost of abstraction exceeds the benefits
- When if-else logic is simple and unlikely to grow

**Strategy vs State Pattern**:
```java
// Strategy: Algorithms that don't depend on object state
class SortingContext {
    void sort(List<Integer> data, SortingStrategy strategy) {
        strategy.sort(data); // Strategy doesn't depend on context state
    }
}

// State: Behavior changes based on object's internal state
class VendingMachine {
    private State currentState = new WaitingForCoinState();
    
    void insertCoin() {
        currentState.insertCoin(this); // State depends on machine's state
    }
}
```

**MAANG Interview Talking Points**:
- "Strategy pattern enables runtime algorithm selection, perfect for A/B testing different approaches"
- "Eliminates conditional logic and makes code follow Open/Closed Principle"
- "Used in Java Collections.sort() with Comparator, Spring Security authentication strategies"
- "Consider combining with Factory pattern for automatic strategy selection based on context"

**Resources**:
- https://refactoring.guru/design-patterns/strategy/java/example
- https://www.baeldung.com/java-strategy-pattern
- Head First Design Patterns (Strategy chapter)
- https://www.journaldev.com/1754/strategy-design-pattern-in-java-example-tutorial

### 4.2 Command
**ELI5**: Like having a magic spell written on paper - you can cast it now, save it for later, undo it, or even give it to someone else to cast.

**Core Idea**: Encapsulate a request as an object, allowing you to parameterize clients with different requests, queue operations, and support undo functionality.

**When to Use**:
- When you want to parameterize objects with operations
- When you need to queue, schedule, or log operations
- When you want to support undo/redo functionality
- When you need to decouple the object that invokes the operation from the one that performs it

**Java Implementation** (Smart Home Automation):
```java
// Command interface
interface Command {
    void execute();
    void undo();
    String getDescription();
}

// Receiver classes - the actual devices
class Light {
    private String location;
    private boolean isOn = false;
    private int brightness = 100;
    
    public Light(String location) {
        this.location = location;
    }
    
    public void turnOn() {
        isOn = true;
        System.out.println(location + " light is ON (brightness: " + brightness + "%)");
    }
    
    public void turnOff() {
        isOn = false;
        System.out.println(location + " light is OFF");
    }
    
    public void setBrightness(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
        if (isOn) {
            System.out.println(location + " light brightness set to " + this.brightness + "%");
        }
    }
    
    public boolean isOn() { return isOn; }
    public int getBrightness() { return brightness; }
    public String getLocation() { return location; }
}

class Fan {
    private String location;
    private boolean isOn = false;
    private int speed = 1; // 1-5
    
    public Fan(String location) {
        this.location = location;
    }
    
    public void turnOn() {
        isOn = true;
        System.out.println(location + " fan is ON (speed: " + speed + ")");
    }
    
    public void turnOff() {
        isOn = false;
        System.out.println(location + " fan is OFF");
    }
    
    public void setSpeed(int speed) {
        this.speed = Math.max(1, Math.min(5, speed));
        if (isOn) {
            System.out.println(location + " fan speed set to " + this.speed);
        }
    }
    
    public boolean isOn() { return isOn; }
    public int getSpeed() { return speed; }
}

class Thermostat {
    private int temperature = 72;
    private String mode = "AUTO";
    
    public void setTemperature(int temp) {
        this.temperature = temp;
        System.out.println("Thermostat set to " + temperature + "°F (" + mode + " mode)");
    }
    
    public void setMode(String mode) {
        this.mode = mode;
        System.out.println("Thermostat mode set to " + mode);
    }
    
    public int getTemperature() { return temperature; }
    public String getMode() { return mode; }
}

// Concrete commands
class LightOnCommand implements Command {
    private Light light;
    
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOn();
    }
    
    @Override
    public void undo() {
        light.turnOff();
    }
    
    @Override
    public String getDescription() {
        return "Turn on " + light.getLocation() + " light";
    }
}

class LightOffCommand implements Command {
    private Light light;
    
    public LightOffCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOff();
    }
    
    @Override
    public void undo() {
        light.turnOn();
    }
    
    @Override
    public String getDescription() {
        return "Turn off " + light.getLocation() + " light";
    }
}

class DimLightCommand implements Command {
    private Light light;
    private int newBrightness;
    private int previousBrightness;
    
    public DimLightCommand(Light light, int brightness) {
        this.light = light;
        this.newBrightness = brightness;
    }
    
    @Override
    public void execute() {
        previousBrightness = light.getBrightness();
        light.setBrightness(newBrightness);
    }
    
    @Override
    public void undo() {
        light.setBrightness(previousBrightness);
    }
    
    @Override
    public String getDescription() {
        return "Dim " + light.getLocation() + " light to " + newBrightness + "%";
    }
}

class FanOnCommand implements Command {
    private Fan fan;
    private int speed;
    
    public FanOnCommand(Fan fan, int speed) {
        this.fan = fan;
        this.speed = speed;
    }
    
    @Override
    public void execute() {
        fan.turnOn();
        fan.setSpeed(speed);
    }
    
    @Override
    public void undo() {
        fan.turnOff();
    }
    
    @Override
    public String getDescription() {
        return "Turn on " + fan.getLocation() + " fan at speed " + speed;
    }
}

class ThermostatCommand implements Command {
    private Thermostat thermostat;
    private int newTemperature;
    private int previousTemperature;
    
    public ThermostatCommand(Thermostat thermostat, int temperature) {
        this.thermostat = thermostat;
        this.newTemperature = temperature;
    }
    
    @Override
    public void execute() {
        previousTemperature = thermostat.getTemperature();
        thermostat.setTemperature(newTemperature);
    }
    
    @Override
    public void undo() {
        thermostat.setTemperature(previousTemperature);
    }
    
    @Override
    public String getDescription() {
        return "Set thermostat to " + newTemperature + "°F";
    }
}

// Macro command - executes multiple commands
class MacroCommand implements Command {
    private List<Command> commands;
    private String description;
    
    public MacroCommand(List<Command> commands, String description) {
        this.commands = new ArrayList<>(commands);
        this.description = description;
    }
    
    @Override
    public void execute() {
        System.out.println("Executing macro: " + description);
        for (Command command : commands) {
            command.execute();
        }
    }
    
    @Override
    public void undo() {
        System.out.println("Undoing macro: " + description);
        // Undo in reverse order
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
    
    @Override
    public String getDescription() {
        return "Macro: " + description + " (" + commands.size() + " commands)";
    }
}

// Null object pattern for empty command
class NoCommand implements Command {
    @Override
    public void execute() {
        // Do nothing
    }
    
    @Override
    public void undo() {
        // Do nothing
    }
    
    @Override
    public String getDescription() {
        return "No command assigned";
    }
}

// Invoker - remote control
class SmartHomeRemote {
    private Command[] commands;
    private Stack<Command> undoStack;
    private int currentSlot = 0;
    
    public SmartHomeRemote(int numSlots) {
        commands = new Command[numSlots];
        undoStack = new Stack<>();
        
        // Initialize with no-op commands
        Command noCommand = new NoCommand();
        for (int i = 0; i < numSlots; i++) {
            commands[i] = noCommand;
        }
    }
    
    public void setCommand(int slot, Command command) {
        if (slot >= 0 && slot < commands.length) {
            commands[slot] = command;
            System.out.println("Slot " + slot + " set to: " + command.getDescription());
        }
    }
    
    public void pressButton(int slot) {
        if (slot >= 0 && slot < commands.length) {
            System.out.println("Pressing button " + slot + ": " + commands[slot].getDescription());
            commands[slot].execute();
            undoStack.push(commands[slot]);
        }
    }
    
    public void pressUndoButton() {
        if (!undoStack.isEmpty()) {
            Command lastCommand = undoStack.pop();
            System.out.println("Undoing: " + lastCommand.getDescription());
            lastCommand.undo();
        } else {
            System.out.println("Nothing to undo");
        }
    }
    
    public void showCommands() {
        System.out.println("\n=== Remote Control Commands ===");
        for (int i = 0; i < commands.length; i++) {
            System.out.printf("Slot %d: %s%n", i, commands[i].getDescription());
        }
        System.out.println("Undo stack size: " + undoStack.size());
    }
}

// Command scheduler for automation
class CommandScheduler {
    private Queue<ScheduledCommand> scheduledCommands;
    
    public CommandScheduler() {
        this.scheduledCommands = new PriorityQueue<>(
            Comparator.comparing(ScheduledCommand::getExecutionTime)
        );
    }
    
    public void scheduleCommand(Command command, long delayMs) {
        long executionTime = System.currentTimeMillis() + delayMs;
        scheduledCommands.offer(new ScheduledCommand(command, executionTime));
        System.out.println("Scheduled: " + command.getDescription() + 
                          " to execute in " + delayMs + "ms");
    }
    
    public void executeScheduledCommands() {
        long currentTime = System.currentTimeMillis();
        
        while (!scheduledCommands.isEmpty() && 
               scheduledCommands.peek().getExecutionTime() <= currentTime) {
            ScheduledCommand scheduled = scheduledCommands.poll();
            System.out.println("Auto-executing: " + scheduled.getCommand().getDescription());
            scheduled.getCommand().execute();
        }
    }
    
    private static class ScheduledCommand {
        private Command command;
        private long executionTime;
        
        public ScheduledCommand(Command command, long executionTime) {
            this.command = command;
            this.executionTime = executionTime;
        }
        
        public Command getCommand() { return command; }
        public long getExecutionTime() { return executionTime; }
    }
}

// Usage example
public class CommandExample {
    public static void main(String[] args) throws InterruptedException {
        // Create devices
        Light livingRoomLight = new Light("Living Room");
        Light kitchenLight = new Light("Kitchen");
        Fan bedroomFan = new Fan("Bedroom");
        Thermostat thermostat = new Thermostat();
        
        // Create commands
        Command livingRoomLightOn = new LightOnCommand(livingRoomLight);
        Command livingRoomLightOff = new LightOffCommand(livingRoomLight);
        Command kitchenLightOn = new LightOnCommand(kitchenLight);
        Command dimLivingRoom = new DimLightCommand(livingRoomLight, 30);
        Command fanOn = new FanOnCommand(bedroomFan, 3);
        Command setTemp = new ThermostatCommand(thermostat, 68);
        
        // Create macro commands
        List<Command> eveningRoutine = Arrays.asList(
            livingRoomLightOn,
            new DimLightCommand(livingRoomLight, 50),
            kitchenLightOn,
            new ThermostatCommand(thermostat, 70)
        );
        Command eveningMacro = new MacroCommand(eveningRoutine, "Evening Routine");
        
        List<Command> sleepRoutine = Arrays.asList(
            livingRoomLightOff,
            new LightOffCommand(kitchenLight),
            new ThermostatCommand(thermostat, 65)
        );
        Command sleepMacro = new MacroCommand(sleepRoutine, "Sleep Routine");
        
        // Set up remote control
        SmartHomeRemote remote = new SmartHomeRemote(8);
        remote.setCommand(0, livingRoomLightOn);
        remote.setCommand(1, livingRoomLightOff);
        remote.setCommand(2, kitchenLightOn);
        remote.setCommand(3, dimLivingRoom);
        remote.setCommand(4, fanOn);
        remote.setCommand(5, setTemp);
        remote.setCommand(6, eveningMacro);
        remote.setCommand(7, sleepMacro);
        
        remote.showCommands();
        
        // Execute commands
        System.out.println("\n=== Manual Control ===");
        remote.pressButton(6); // Evening routine
        
        Thread.sleep(1000);
        
        remote.pressButton(3); // Dim living room
        remote.pressButton(4); // Turn on fan
        
        System.out.println("\n=== Undo Operations ===");
        remote.pressUndoButton(); // Undo fan
        remote.pressUndoButton(); // Undo dim
        remote.pressUndoButton(); // Undo evening routine
        
        System.out.println("\n=== Scheduled Commands ===");
        CommandScheduler scheduler = new CommandScheduler();
        
        // Schedule commands
        scheduler.scheduleCommand(new LightOnCommand(kitchenLight), 2000);
        scheduler.scheduleCommand(new ThermostatCommand(thermostat, 72), 3000);
        scheduler.scheduleCommand(sleepMacro, 5000);
        
        // Simulate time passing and execute scheduled commands
        for (int i = 0; i < 6; i++) {
            Thread.sleep(1000);
            scheduler.executeScheduledCommands();
        }
    }
}
```

**Common Pitfalls**:
1. **Overkill for Simple Cases**: Don't use Command pattern for simple one-to-one method calls
2. **Memory Overhead**: Storing many command objects can consume significant memory
3. **Incomplete Undo Logic**: Not properly storing previous state can make undo impossible
4. **Complex State Management**: Commands with side effects can be difficult to undo properly

**Thread Safety Considerations**:
```java
// Thread-safe command queue for concurrent execution
class ThreadSafeCommandQueue {
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    
    public void submitCommand(Command command) {
        commandQueue.offer(command);
    }
    
    public void processCommands() {
        executor.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Command command = commandQueue.take(); // Blocking call
                    command.execute();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

**When NOT to Use Command**:
- When operations are simple and don't need undo/logging/queuing
- When the overhead of creating command objects is significant
- When direct method calls are sufficient and clearer

**Modern Alternatives**:
```java
// Functional interfaces (Java 8+)
Runnable command = () -> light.turnOn();
Function<Light, Boolean> toggle = light -> { light.toggle(); return light.isOn(); };

// CompletableFuture for async command execution
CompletableFuture<Void> asyncCommand = CompletableFuture.runAsync(() -> heavyOperation());

// Method references
consumer.accept(Light::turnOn);
```

**MAANG Interview Talking Points**:
- "Command pattern is fundamental to implementing undo/redo functionality in applications"
- "Widely used in GUI frameworks, databases (transaction logs), and distributed systems"
- "Key to implementing macro commands, task scheduling, and request logging"
- "Consider memory implications when storing many commands for undo functionality"

**Resources**:
- Head First Design Patterns (Command chapter)
- https://refactoring.guru/design-patterns/command/java/example
- https://www.baeldung.com/java-command-pattern

### 4.3 Observer
**ELI5**: Like a school bell - when it rings, all students know it's time for lunch without the principal calling each one individually.

**Core Idea**: Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified automatically.

**When to Use**:
- When changes to one object require updating multiple dependent objects
- When you want loose coupling between subjects and observers
- When you need to broadcast events to multiple listeners
- When the number of observers can vary dynamically

**Java Implementation 1** (Stock Price Monitoring):
```java
// Subject interface
interface StockSubject {
    void addObserver(StockObserver observer);
    void removeObserver(StockObserver observer);
    void notifyObservers();
}

// Observer interface
interface StockObserver {
    void update(String symbol, double price, double change, long timestamp);
    String getObserverName();
}

// Concrete subject
class StockPrice implements StockSubject {
    private String symbol;
    private double currentPrice;
    private double previousPrice;
    private long lastUpdated;
    private List<StockObserver> observers;
    
    public StockPrice(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.currentPrice = initialPrice;
        this.previousPrice = initialPrice;
        this.lastUpdated = System.currentTimeMillis();
        this.observers = new CopyOnWriteArrayList<>(); // Thread-safe for concurrent access
    }
    
    @Override
    public void addObserver(StockObserver observer) {
        observers.add(observer);
        System.out.println("Added observer: " + observer.getObserverName() + " for " + symbol);
        
        // Send current state to new observer
        double change = currentPrice - previousPrice;
        observer.update(symbol, currentPrice, change, lastUpdated);
    }
    
    @Override
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
        System.out.println("Removed observer: " + observer.getObserverName() + " for " + symbol);
    }
    
    @Override
    public void notifyObservers() {
        double change = currentPrice - previousPrice;
        System.out.println("Notifying " + observers.size() + " observers of " + symbol + " price change");
        
        for (StockObserver observer : observers) {
            try {
                observer.update(symbol, currentPrice, change, lastUpdated);
            } catch (Exception e) {
                System.err.println("Error notifying observer " + observer.getObserverName() + ": " + e.getMessage());
                // Continue notifying other observers even if one fails
            }
        }
    }
    
    public void setPrice(double newPrice) {
        if (newPrice != currentPrice) {
            this.previousPrice = currentPrice;
            this.currentPrice = newPrice;
            this.lastUpdated = System.currentTimeMillis();
            
            // Only notify if there's a significant change (avoid noise)
            double changePercent = Math.abs((newPrice - previousPrice) / previousPrice) * 100;
            if (changePercent >= 0.01) { // 0.01% threshold
                notifyObservers();
            }
        }
    }
    
    // Getters
    public String getSymbol() { return symbol; }
    public double getCurrentPrice() { return currentPrice; }
    public double getPreviousPrice() { return previousPrice; }
}

// Concrete observers
class TradingAlgorithm implements StockObserver {
    private String algorithmName;
    private double buyThreshold;
    private double sellThreshold;
    private Map<String, Integer> positions; // symbol -> quantity
    
    public TradingAlgorithm(String name, double buyThreshold, double sellThreshold) {
        this.algorithmName = name;
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.positions = new HashMap<>();
    }
    
    @Override
    public void update(String symbol, double price, double change, long timestamp) {
        double changePercent = (change / (price - change)) * 100;
        
        System.out.printf("[TRADING ALGO %s] %s: $%.2f (%.2f%%)%n", 
                         algorithmName, symbol, price, changePercent);
        
        // Trading logic
        if (changePercent <= -buyThreshold) {
            // Price dropped significantly - buy signal
            int currentPosition = positions.getOrDefault(symbol, 0);
            int buyQuantity = calculateBuyQuantity(symbol, price);
            positions.put(symbol, currentPosition + buyQuantity);
            
            System.out.printf("[TRADING ALGO %s] BUY %d shares of %s at $%.2f%n", 
                             algorithmName, buyQuantity, symbol, price);
                             
        } else if (changePercent >= sellThreshold) {
            // Price increased significantly - sell signal
            int currentPosition = positions.getOrDefault(symbol, 0);
            if (currentPosition > 0) {
                int sellQuantity = Math.min(currentPosition, calculateSellQuantity(symbol, price));
                positions.put(symbol, currentPosition - sellQuantity);
                
                System.out.printf("[TRADING ALGO %s] SELL %d shares of %s at $%.2f%n", 
                                 algorithmName, sellQuantity, symbol, price);
            }
        }
    }
    
    @Override
    public String getObserverName() {
        return "TradingAlgorithm-" + algorithmName;
    }
    
    private int calculateBuyQuantity(String symbol, double price) {
        // Simplified calculation based on available capital
        return (int) Math.min(100, 10000 / price);
    }
    
    private int calculateSellQuantity(String symbol, double price) {
        // Simplified - sell 50% of position on strong upward movement
        return positions.getOrDefault(symbol, 0) / 2;
    }
}

class PriceAlertSystem implements StockObserver {
    private String userId;
    private Map<String, PriceAlert> alerts; // symbol -> alert
    
    public PriceAlertSystem(String userId) {
        this.userId = userId;
        this.alerts = new HashMap<>();
    }
    
    public void addAlert(String symbol, double targetPrice, boolean isAbove) {
        alerts.put(symbol, new PriceAlert(targetPrice, isAbove));
        System.out.printf("[ALERT SYSTEM] Added alert for %s: %s $%.2f%n", 
                         symbol, isAbove ? "above" : "below", targetPrice);
    }
    
    @Override
    public void update(String symbol, double price, double change, long timestamp) {
        PriceAlert alert = alerts.get(symbol);
        if (alert != null && alert.shouldTrigger(price)) {
            sendAlert(symbol, price, alert);
            alerts.remove(symbol); // Remove one-time alert
        }
    }
    
    @Override
    public String getObserverName() {
        return "PriceAlertSystem-" + userId;
    }
    
    private void sendAlert(String symbol, double price, PriceAlert alert) {
        String direction = alert.isAbove ? "exceeded" : "dropped below";
        String message = String.format("ALERT: %s has %s your target price of $%.2f (current: $%.2f)", 
                                      symbol, direction, alert.targetPrice, price);
        
        System.out.println("[ALERT SYSTEM] " + message);
        // In real system: send email, SMS, push notification, etc.
        sendNotification(userId, message);
    }
    
    private void sendNotification(String userId, String message) {
        // Simulate notification sending
        System.out.println("[NOTIFICATION] Sent to user " + userId + ": " + message);
    }
    
    private static class PriceAlert {
        double targetPrice;
        boolean isAbove; // true for "alert when above", false for "alert when below"
        
        PriceAlert(double targetPrice, boolean isAbove) {
            this.targetPrice = targetPrice;
            this.isAbove = isAbove;
        }
        
        boolean shouldTrigger(double currentPrice) {
            return isAbove ? currentPrice >= targetPrice : currentPrice <= targetPrice;
        }
    }
}

class PortfolioTracker implements StockObserver {
    private String portfolioName;
    private Map<String, PortfolioPosition> positions;
    private double totalValue;
    
    public PortfolioTracker(String portfolioName) {
        this.portfolioName = portfolioName;
        this.positions = new HashMap<>();
        this.totalValue = 0.0;
    }
    
    public void addPosition(String symbol, int quantity, double averageCost) {
        positions.put(symbol, new PortfolioPosition(quantity, averageCost));
        System.out.printf("[PORTFOLIO %s] Added position: %d shares of %s at avg cost $%.2f%n", 
                         portfolioName, quantity, symbol, averageCost);
    }
    
    @Override
    public void update(String symbol, double price, double change, long timestamp) {
        PortfolioPosition position = positions.get(symbol);
        if (position != null) {
            double previousValue = position.getCurrentValue();
            position.updateCurrentPrice(price);
            double newValue = position.getCurrentValue();
            double valueChange = newValue - previousValue;
            
            updateTotalValue(valueChange);
            
            double gainLoss = newValue - (position.quantity * position.averageCost);
            double gainLossPercent = (gainLoss / (position.quantity * position.averageCost)) * 100;
            
            System.out.printf("[PORTFOLIO %s] %s: %d shares @ $%.2f, Value: $%.2f, P&L: $%.2f (%.2f%%)%n", 
                             portfolioName, symbol, position.quantity, price, newValue, gainLoss, gainLossPercent);
        }
    }
    
    @Override
    public String getObserverName() {
        return "PortfolioTracker-" + portfolioName;
    }
    
    private void updateTotalValue(double change) {
        totalValue += change;
        System.out.printf("[PORTFOLIO %s] Total portfolio value: $%.2f%n", portfolioName, totalValue);
    }
    
    private static class PortfolioPosition {
        int quantity;
        double averageCost;
        double currentPrice;
        
        PortfolioPosition(int quantity, double averageCost) {
            this.quantity = quantity;
            this.averageCost = averageCost;
            this.currentPrice = averageCost;
        }
        
        void updateCurrentPrice(double price) {
            this.currentPrice = price;
        }
        
        double getCurrentValue() {
            return quantity * currentPrice;
        }
    }
}

// Usage example
public class StockMarketSimulator {
    public static void main(String[] args) throws InterruptedException {
        // Create stock price subjects
        StockPrice appleStock = new StockPrice("AAPL", 150.00);
        StockPrice googleStock = new StockPrice("GOOGL", 2500.00);
        
        // Create observers
        TradingAlgorithm momentumAlgo = new TradingAlgorithm("Momentum", 2.0, 3.0);
        TradingAlgorithm contraryAlgo = new TradingAlgorithm("Contrary", 1.5, 2.5);
        
        PriceAlertSystem userAlerts = new PriceAlertSystem("user123");
        userAlerts.addAlert("AAPL", 155.00, true);  // Alert when AAPL goes above $155
        userAlerts.addAlert("GOOGL", 2450.00, false); // Alert when GOOGL goes below $2450
        
        PortfolioTracker portfolio = new PortfolioTracker("MyPortfolio");
        portfolio.addPosition("AAPL", 100, 145.00);
        portfolio.addPosition("GOOGL", 10, 2600.00);
        
        // Subscribe observers to subjects
        appleStock.addObserver(momentumAlgo);
        appleStock.addObserver(contraryAlgo);
        appleStock.addObserver(userAlerts);
        appleStock.addObserver(portfolio);
        
        googleStock.addObserver(momentumAlgo);
        googleStock.addObserver(userAlerts);
        googleStock.addObserver(portfolio);
        
        // Simulate price changes
        System.out.println("\n=== Market Simulation Starting ===\n");
        
        Thread.sleep(1000);
        appleStock.setPrice(152.50); // +1.67% increase
        
        Thread.sleep(1000);
        googleStock.setPrice(2475.00); // -1% decrease
        
        Thread.sleep(1000);
        appleStock.setPrice(156.25); // +4.17% total increase - should trigger alert
        
        Thread.sleep(1000);
        googleStock.setPrice(2440.00); // -2.4% total decrease - should trigger alert
        
        // Demonstrate dynamic subscription/unsubscription
        System.out.println("\n=== Removing Contrary Algorithm ===\n");
        appleStock.removeObserver(contraryAlgo);
        
        Thread.sleep(1000);
        appleStock.setPrice(148.75); // -4.8% decrease from peak
    }
}
```

**Common Pitfalls**:
1. **Memory Leaks**: Forgetting to unsubscribe observers can cause memory leaks
2. **Notification Storms**: Too many fine-grained notifications can overwhelm observers
3. **Observer Ordering**: Depending on notification order can lead to brittle code
4. **Exception Handling**: One failing observer can break the notification chain
5. **Synchronization Issues**: Concurrent modifications during notification

**Thread Safety Considerations**:
```java
// BAD: Not thread-safe
class UnsafeSubject {
    private List<Observer> observers = new ArrayList<>(); // Can cause ConcurrentModificationException
    
    public void notifyObservers() {
        for (Observer obs : observers) { // Unsafe during concurrent modifications
            obs.update();
        }
    }
}

// GOOD: Thread-safe implementation
class SafeSubject {
    private final List<Observer> observers = new CopyOnWriteArrayList<>(); // Thread-safe
    
    public void notifyObservers() {
        // CopyOnWriteArrayList handles concurrent modifications safely
        observers.forEach(Observer::update);
    }
}
```

**When NOT to Use Observer**:
- When there are very few, static relationships between objects
- When notification logic is complex and specific to each observer
- When performance is critical and observer overhead is significant
- When simple callbacks or dependency injection would suffice

**Modern Alternatives**:
```java
// Event Bus pattern (using libraries like Guava EventBus)
@EventBus
public class ModernEventSystem {
    private final EventBus eventBus = new EventBus();
    
    public void register(Object listener) {
        eventBus.register(listener);
    }
    
    public void publishPriceUpdate(PriceUpdateEvent event) {
        eventBus.post(event);
    }
}

class ModernPriceListener {
    @Subscribe
    public void handlePriceUpdate(PriceUpdateEvent event) {
        // Handle price update
    }
}

// Reactive Streams (using RxJava, Project Reactor)
Observable<StockPrice> priceStream = Observable.fromIterable(stockPrices);
priceStream
    .filter(price -> price.getChange() > 0.05)
    .subscribe(price -> System.out.println("Significant price change: " + price));
```

**Observer vs Pub/Sub**:
- **Observer**: Direct coupling between subject and observers, synchronous
- **Pub/Sub**: Indirect coupling through message broker, can be asynchronous

**MAANG Interview Talking Points**:
- "Observer pattern is the foundation of MVC architecture and event-driven systems"
- "Used extensively in UI frameworks (button clicks, data binding) and reactive programming"
- "Important to consider thread safety and memory management in concurrent environments"
- "Modern implementations often use event buses or reactive streams for better decoupling"

**Resources**:
- https://refactoring.guru/design-patterns/observer/java/example
- https://www.baeldung.com/java-observer-pattern
- Reactive Streams Specification: https://www.reactive-streams.org/
- RxJava documentation: https://github.com/ReactiveX/RxJava

### 4.4 Mediator
ELI5: Teacher passes messages between kids so they don’t all shout.
Java:
```java
class ChatRoom { List<User> users; void send(User from,String msg){ users.stream().filter(u->u!=from).forEach(u->u.receive(msg)); }}
```
Scenario: Reduce many-to-many coupling (UI widgets interactions).
Pitfall: Mediator bloats to god-object.

### 4.5 State
**ELI5**: Like a smartphone that behaves differently when it's locked vs unlocked vs in airplane mode - same device, different behaviors based on current state.

**Core Idea**: Allow an object to alter its behavior when its internal state changes, appearing as if the object changed its class.

**When to Use**:
- When an object's behavior depends on its state and must change at runtime
- When operations have large conditional statements depending on object state
- When state transitions are complex and need to be managed centrally
- When you have a state machine with clearly defined states and transitions

**Java Implementation** (Vending Machine):
```java
// State interface
interface VendingMachineState {
    void insertCoin(VendingMachine machine, int amount);
    void selectProduct(VendingMachine machine, String product);
    void dispenseProduct(VendingMachine machine);
    void cancelTransaction(VendingMachine machine);
    String getStateName();
}

// Context class - Vending Machine
class VendingMachine {
    private VendingMachineState currentState;
    private int balance;
    private Map<String, Product> inventory;
    private String selectedProduct;
    
    // States
    private final VendingMachineState idleState;
    private final VendingMachineState hasMoneyState;
    private final VendingMachineState productSelectedState;
    private final VendingMachineState dispensingState;
    private final VendingMachineState outOfStockState;
    
    public VendingMachine() {
        this.balance = 0;
        this.inventory = new HashMap<>();
        this.selectedProduct = null;
        
        // Initialize states
        this.idleState = new IdleState();
        this.hasMoneyState = new HasMoneyState();
        this.productSelectedState = new ProductSelectedState();
        this.dispensingState = new DispensingState();
        this.outOfStockState = new OutOfStockState();
        
        // Set initial state
        this.currentState = idleState;
        
        // Initialize inventory
        initializeInventory();
    }
    
    private void initializeInventory() {
        inventory.put("COKE", new Product("Coca Cola", 150, 5));
        inventory.put("PEPSI", new Product("Pepsi", 150, 3));
        inventory.put("WATER", new Product("Water", 100, 10));
        inventory.put("CHIPS", new Product("Chips", 200, 7));
        inventory.put("CANDY", new Product("Candy", 120, 0)); // Out of stock
    }
    
    // Context methods that delegate to current state
    public void insertCoin(int amount) {
        System.out.println("Inserting coin: " + amount + " cents");
        currentState.insertCoin(this, amount);
    }
    
    public void selectProduct(String product) {
        System.out.println("Selecting product: " + product);
        currentState.selectProduct(this, product);
    }
    
    public void dispenseProduct() {
        System.out.println("Dispensing product...");
        currentState.dispenseProduct(this);
    }
    
    public void cancelTransaction() {
        System.out.println("Cancelling transaction...");
        currentState.cancelTransaction(this);
    }
    
    // State management
    public void setState(VendingMachineState state) {
        System.out.println("State changed from " + currentState.getStateName() + 
                          " to " + state.getStateName());
        this.currentState = state;
    }
    
    // Getters and setters
    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }
    public void addBalance(int amount) { this.balance += amount; }
    public void deductBalance(int amount) { this.balance -= amount; }
    
    public String getSelectedProduct() { return selectedProduct; }
    public void setSelectedProduct(String selectedProduct) { this.selectedProduct = selectedProduct; }
    
    public Product getProduct(String productCode) {
        return inventory.get(productCode);
    }
    
    public boolean isProductAvailable(String productCode) {
        Product product = inventory.get(productCode);
        return product != null && product.getQuantity() > 0;
    }
    
    public void reduceProductQuantity(String productCode) {
        Product product = inventory.get(productCode);
        if (product != null && product.getQuantity() > 0) {
            product.setQuantity(product.getQuantity() - 1);
        }
    }
    
    public void returnChange() {
        if (balance > 0) {
            System.out.println("Returning change: " + balance + " cents");
            balance = 0;
        }
    }
    
    // State getters
    public VendingMachineState getIdleState() { return idleState; }
    public VendingMachineState getHasMoneyState() { return hasMoneyState; }
    public VendingMachineState getProductSelectedState() { return productSelectedState; }
    public VendingMachineState getDispensingState() { return dispensingState; }
    public VendingMachineState getOutOfStockState() { return outOfStockState; }
    
    public void showStatus() {
        System.out.println("\n=== Vending Machine Status ===");
        System.out.println("Current State: " + currentState.getStateName());
        System.out.println("Balance: " + balance + " cents");
        System.out.println("Selected Product: " + (selectedProduct != null ? selectedProduct : "None"));
        System.out.println("Inventory:");
        for (Map.Entry<String, Product> entry : inventory.entrySet()) {
            Product p = entry.getValue();
            System.out.println("  " + entry.getKey() + ": " + p.getName() + 
                             " ($" + p.getPrice()/100.0 + ") - Qty: " + p.getQuantity());
        }
        System.out.println("===============================");
    }
}

// Product class
class Product {
    private String name;
    private int price; // in cents
    private int quantity;
    
    public Product(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

// Concrete States
class IdleState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, int amount) {
        if (amount > 0) {
            machine.addBalance(amount);
            System.out.println("Coin accepted. Balance: " + machine.getBalance() + " cents");
            machine.setState(machine.getHasMoneyState());
        } else {
            System.out.println("Invalid coin amount!");
        }
    }
    
    @Override
    public void selectProduct(VendingMachine machine, String product) {
        System.out.println("Please insert money first!");
    }
    
    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Please insert money and select a product first!");
    }
    
    @Override
    public void cancelTransaction(VendingMachine machine) {
        System.out.println("No transaction to cancel.");
    }
    
    @Override
    public String getStateName() {
        return "IDLE";
    }
}

class HasMoneyState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, int amount) {
        if (amount > 0) {
            machine.addBalance(amount);
            System.out.println("Additional coin accepted. Balance: " + machine.getBalance() + " cents");
        } else {
            System.out.println("Invalid coin amount!");
        }
    }
    
    @Override
    public void selectProduct(VendingMachine machine, String product) {
        Product selectedProduct = machine.getProduct(product);
        
        if (selectedProduct == null) {
            System.out.println("Product not found!");
            return;
        }
        
        if (!machine.isProductAvailable(product)) {
            System.out.println("Product out of stock!");
            machine.setState(machine.getOutOfStockState());
            return;
        }
        
        if (machine.getBalance() >= selectedProduct.getPrice()) {
            machine.setSelectedProduct(product);
            System.out.println("Product selected: " + selectedProduct.getName());
            machine.setState(machine.getProductSelectedState());
        } else {
            int needed = selectedProduct.getPrice() - machine.getBalance();
            System.out.println("Insufficient funds! Need " + needed + " more cents.");
        }
    }
    
    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Please select a product first!");
    }
    
    @Override
    public void cancelTransaction(VendingMachine machine) {
        machine.returnChange();
        machine.setState(machine.getIdleState());
        System.out.println("Transaction cancelled.");
    }
    
    @Override
    public String getStateName() {
        return "HAS_MONEY";
    }
}

class ProductSelectedState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, int amount) {
        if (amount > 0) {
            machine.addBalance(amount);
            System.out.println("Additional coin accepted. Balance: " + machine.getBalance() + " cents");
        }
    }
    
    @Override
    public void selectProduct(VendingMachine machine, String product) {
        Product newProduct = machine.getProduct(product);
        
        if (newProduct == null) {
            System.out.println("Product not found!");
            return;
        }
        
        if (!machine.isProductAvailable(product)) {
            System.out.println("Product out of stock!");
            machine.setState(machine.getOutOfStockState());
            return;
        }
        
        if (machine.getBalance() >= newProduct.getPrice()) {
            machine.setSelectedProduct(product);
            System.out.println("Product changed to: " + newProduct.getName());
        } else {
            int needed = newProduct.getPrice() - machine.getBalance();
            System.out.println("Insufficient funds for " + newProduct.getName() + "! Need " + needed + " more cents.");
        }
    }
    
    @Override
    public void dispenseProduct(VendingMachine machine) {
        String productCode = machine.getSelectedProduct();
        Product product = machine.getProduct(productCode);
        
        // Deduct money
        machine.deductBalance(product.getPrice());
        
        // Reduce inventory
        machine.reduceProductQuantity(productCode);
        
        // Dispense
        System.out.println("Dispensing: " + product.getName());
        machine.setState(machine.getDispensingState());
        
        // Simulate dispensing delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Product dispensed successfully!");
                
                // Return change if any
                machine.returnChange();
                
                // Reset selection
                machine.setSelectedProduct(null);
                
                // Return to idle state
                machine.setState(machine.getIdleState());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    @Override
    public void cancelTransaction(VendingMachine machine) {
        machine.setSelectedProduct(null);
        machine.returnChange();
        machine.setState(machine.getIdleState());
        System.out.println("Transaction cancelled.");
    }
    
    @Override
    public String getStateName() {
        return "PRODUCT_SELECTED";
    }
}

class DispensingState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, int amount) {
        System.out.println("Please wait, dispensing in progress...");
    }
    
    @Override
    public void selectProduct(VendingMachine machine, String product) {
        System.out.println("Please wait, dispensing in progress...");
    }
    
    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Already dispensing...");
    }
    
    @Override
    public void cancelTransaction(VendingMachine machine) {
        System.out.println("Cannot cancel, dispensing in progress...");
    }
    
    @Override
    public String getStateName() {
        return "DISPENSING";
    }
}

class OutOfStockState implements VendingMachineState {
    @Override
    public void insertCoin(VendingMachine machine, int amount) {
        System.out.println("Selected product is out of stock. Please select another product.");
    }
    
    @Override
    public void selectProduct(VendingMachine machine, String product) {
        Product selectedProduct = machine.getProduct(product);
        
        if (selectedProduct == null) {
            System.out.println("Product not found!");
            return;
        }
        
        if (!machine.isProductAvailable(product)) {
            System.out.println("This product is also out of stock!");
            return;
        }
        
        if (machine.getBalance() >= selectedProduct.getPrice()) {
            machine.setSelectedProduct(product);
            System.out.println("Product selected: " + selectedProduct.getName());
            machine.setState(machine.getProductSelectedState());
        } else {
            machine.setState(machine.getHasMoneyState());
            int needed = selectedProduct.getPrice() - machine.getBalance();
            System.out.println("Insufficient funds! Need " + needed + " more cents.");
        }
    }
    
    @Override
    public void dispenseProduct(VendingMachine machine) {
        System.out.println("Cannot dispense - out of stock!");
    }
    
    @Override
    public void cancelTransaction(VendingMachine machine) {
        machine.setSelectedProduct(null);
        machine.returnChange();
        machine.setState(machine.getIdleState());
        System.out.println("Transaction cancelled.");
    }
    
    @Override
    public String getStateName() {
        return "OUT_OF_STOCK";
    }
}

// Usage example
public class StateExample {
    public static void main(String[] args) throws InterruptedException {
        VendingMachine machine = new VendingMachine();
        
        machine.showStatus();
        
        // Test normal flow
        System.out.println("\n=== Test 1: Normal Purchase ===");
        machine.insertCoin(100);
        machine.insertCoin(50);
        machine.selectProduct("COKE");
        machine.dispenseProduct();
        
        Thread.sleep(3000); // Wait for dispensing to complete
        
        machine.showStatus();
        
        // Test insufficient funds
        System.out.println("\n=== Test 2: Insufficient Funds ===");
        machine.insertCoin(50);
        machine.selectProduct("CHIPS"); // Costs 200 cents
        machine.insertCoin(100);
        machine.selectProduct("CHIPS");
        machine.insertCoin(60);
        machine.selectProduct("CHIPS");
        machine.dispenseProduct();
        
        Thread.sleep(3000);
        
        // Test out of stock
        System.out.println("\n=== Test 3: Out of Stock ===");
        machine.insertCoin(150);
        machine.selectProduct("CANDY"); // Out of stock
        machine.selectProduct("WATER"); // Available
        machine.dispenseProduct();
        
        Thread.sleep(3000);
        
        // Test cancellation
        System.out.println("\n=== Test 4: Cancellation ===");
        machine.insertCoin(200);
        machine.selectProduct("PEPSI");
        machine.cancelTransaction();
        
        machine.showStatus();
    }
}
```

**Common Pitfalls**:
1. **State Explosion**: Too many states can make the system complex and hard to maintain
2. **Shared State Data**: States sharing mutable data can lead to inconsistencies
3. **Missing Transitions**: Forgetting to handle certain state transitions
4. **State Creation Overhead**: Creating new state objects for each transition can be expensive

**When NOT to Use State**:
- When state changes are simple and can be handled with boolean flags or enums
- When there are only a few states with simple transitions
- When the state logic is straightforward and doesn't warrant the complexity

**Thread Safety Considerations**:
```java
// Thread-safe state machine
class ThreadSafeStateMachine {
    private volatile State currentState;
    private final Object stateLock = new Object();
    
    public void transitionTo(State newState) {
        synchronized (stateLock) {
            currentState = newState;
        }
    }
    
    public void handleEvent(Event event) {
        synchronized (stateLock) {
            currentState.handle(event, this);
        }
    }
}
```

**Modern Alternatives**:
```java
// Enum-based state machine (simpler for basic cases)
enum TrafficLightState {
    RED {
        @Override
        public TrafficLightState next() { return GREEN; }
    },
    GREEN {
        @Override
        public TrafficLightState next() { return YELLOW; }
    },
    YELLOW {
        @Override
        public TrafficLightState next() { return RED; }
    };
    
    public abstract TrafficLightState next();
}

// State machine libraries (Spring State Machine, etc.)
@EnableStateMachine
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<States, Events> {
    // Configuration for complex state machines
}
```

**MAANG Interview Talking Points**:
- "State pattern is fundamental to implementing finite state machines and workflow engines"
- "Used extensively in game development, protocol implementations, and UI state management"
- "Consider enum-based approaches for simpler state machines before implementing full State pattern"
- "Important to clearly define state transitions and handle edge cases"

**Resources**:
- Head First Design Patterns (State chapter)
- https://refactoring.guru/design-patterns/state/java/example
- https://www.baeldung.com/java-state-design-pattern

### 4.6 Chain of Responsibility
**ELI5**: Like a customer support escalation system - if the first-level support can't help you, they pass you to their supervisor, who might pass you to a specialist, until someone can solve your problem.

**Core Idea**: Pass requests along a chain of handlers until one of them handles the request, avoiding coupling between sender and receiver.

**When to Use**:
- When more than one object can handle a request, and the handler isn't known beforehand
- When you want to issue requests without specifying the receiver explicitly
- When the set of handlers can change dynamically
- When you want to decouple request senders from receivers

**Java Implementation** (Support Ticket System):
```java
// Request class
class SupportTicket {
    private String id;
    private Priority priority;
    private String issue;
    private String details;
    private String assignedTo;
    private boolean resolved;
    
    public enum Priority {
        LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);
        
        private final int level;
        Priority(int level) { this.level = level; }
        public int getLevel() { return level; }
    }
    
    public SupportTicket(String id, Priority priority, String issue, String details) {
        this.id = id;
        this.priority = priority;
        this.issue = issue;
        this.details = details;
        this.resolved = false;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public Priority getPriority() { return priority; }
    public String getIssue() { return issue; }
    public String getDetails() { return details; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    
    @Override
    public String toString() {
        return String.format("Ticket[%s] Priority:%s Issue:'%s' AssignedTo:%s Resolved:%s", 
                           id, priority, issue, assignedTo, resolved);
    }
}

// Abstract handler
abstract class SupportHandler {
    protected SupportHandler nextHandler;
    protected String handlerName;
    protected SupportTicket.Priority maxPriority;
    
    public SupportHandler(String handlerName, SupportTicket.Priority maxPriority) {
        this.handlerName = handlerName;
        this.maxPriority = maxPriority;
    }
    
    // Fluent interface for building chain
    public SupportHandler setNext(SupportHandler handler) {
        this.nextHandler = handler;
        return handler;
    }
    
    // Template method that implements the chain logic
    public final boolean handleTicket(SupportTicket ticket) {
        System.out.println("\n" + handlerName + " reviewing ticket: " + ticket.getId());
        
        if (canHandle(ticket)) {
            return processTicket(ticket);
        } else if (nextHandler != null) {
            System.out.println(handlerName + " cannot handle this ticket, escalating...");
            return nextHandler.handleTicket(ticket);
        } else {
            System.out.println("No handler available for ticket: " + ticket.getId());
            return false;
        }
    }
    
    // Default implementation - can be overridden
    protected boolean canHandle(SupportTicket ticket) {
        return ticket.getPriority().getLevel() <= maxPriority.getLevel();
    }
    
    // Abstract method - must be implemented by concrete handlers
    protected abstract boolean processTicket(SupportTicket ticket);
    
    protected void logHandling(SupportTicket ticket, String action) {
        System.out.println(handlerName + " " + action + " ticket " + ticket.getId() + 
                          " (Priority: " + ticket.getPriority() + ")");
    }
}

// Concrete handlers
class Level1SupportHandler extends SupportHandler {
    private List<String> commonIssues;
    
    public Level1SupportHandler() {
        super("Level 1 Support", SupportTicket.Priority.LOW);
        this.commonIssues = Arrays.asList(
            "password reset", "account locked", "login issue", "basic setup"
        );
    }
    
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        // Level 1 can handle low priority tickets with common issues
        return super.canHandle(ticket) && isCommonIssue(ticket.getIssue());
    }
    
    private boolean isCommonIssue(String issue) {
        return commonIssues.stream().anyMatch(common -> 
            issue.toLowerCase().contains(common.toLowerCase()));
    }
    
    @Override
    protected boolean processTicket(SupportTicket ticket) {
        logHandling(ticket, "processing");
        
        // Simulate processing time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ticket.setAssignedTo("Level 1 Agent");
        ticket.setResolved(true);
        
        System.out.println("✓ " + handlerName + " resolved ticket: " + ticket.getId());
        return true;
    }
}

class Level2SupportHandler extends SupportHandler {
    private List<String> technicalIssues;
    
    public Level2SupportHandler() {
        super("Level 2 Support", SupportTicket.Priority.MEDIUM);
        this.technicalIssues = Arrays.asList(
            "software bug", "configuration", "integration", "api issue", "database"
        );
    }
    
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return super.canHandle(ticket) && 
               (isTechnicalIssue(ticket.getIssue()) || ticket.getPriority() == SupportTicket.Priority.MEDIUM);
    }
    
    private boolean isTechnicalIssue(String issue) {
        return technicalIssues.stream().anyMatch(tech -> 
            issue.toLowerCase().contains(tech.toLowerCase()));
    }
    
    @Override
    protected boolean processTicket(SupportTicket ticket) {
        logHandling(ticket, "analyzing");
        
        // Simulate analysis time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ticket.setAssignedTo("Level 2 Technician");
        
        // Level 2 might need to escalate complex issues
        if (ticket.getDetails().toLowerCase().contains("critical") || 
            ticket.getDetails().toLowerCase().contains("urgent")) {
            System.out.println("Issue complexity detected, escalating to Level 3...");
            return nextHandler != null && nextHandler.handleTicket(ticket);
        }
        
        ticket.setResolved(true);
        System.out.println("✓ " + handlerName + " resolved ticket: " + ticket.getId());
        return true;
    }
}

class Level3SupportHandler extends SupportHandler {
    private List<String> expertiseAreas;
    
    public Level3SupportHandler() {
        super("Level 3 Support", SupportTicket.Priority.HIGH);
        this.expertiseAreas = Arrays.asList(
            "security", "performance", "architecture", "data recovery", "system crash"
        );
    }
    
    @Override
    protected boolean canHandle(SupportTicket ticket) {
        return super.canHandle(ticket) && 
               (requiresExpertise(ticket.getIssue()) || ticket.getPriority() == SupportTicket.Priority.HIGH);
    }
    
    private boolean requiresExpertise(String issue) {
        return expertiseAreas.stream().anyMatch(area -> 
            issue.toLowerCase().contains(area.toLowerCase()));
    }
    
    @Override
    protected boolean processTicket(SupportTicket ticket) {
        logHandling(ticket, "investigating");
        
        // Simulate investigation time
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ticket.setAssignedTo("Level 3 Specialist");
        ticket.setResolved(true);
        
        System.out.println("✓ " + handlerName + " resolved complex ticket: " + ticket.getId());
        return true;
    }
}

class ManagerHandler extends SupportHandler {
    public ManagerHandler() {
        super("Manager", SupportTicket.Priority.CRITICAL);
    }
    
    @Override
    protected boolean processTicket(SupportTicket ticket) {
        logHandling(ticket, "reviewing critical issue");
        
        // Manager handles all critical issues and makes decisions
        System.out.println("Manager decision: Assigning to emergency response team");
        
        ticket.setAssignedTo("Emergency Response Team");
        ticket.setResolved(true);
        
        System.out.println("✓ " + handlerName + " escalated critical ticket: " + ticket.getId());
        return true;
    }
}

// Support system that uses the chain
class SupportSystem {
    private SupportHandler handlerChain;
    private List<SupportTicket> ticketHistory;
    
    public SupportSystem() {
        this.ticketHistory = new ArrayList<>();
        setupHandlerChain();
    }
    
    private void setupHandlerChain() {
        // Build the chain of responsibility
        SupportHandler level1 = new Level1SupportHandler();
        SupportHandler level2 = new Level2SupportHandler();
        SupportHandler level3 = new Level3SupportHandler();
        SupportHandler manager = new ManagerHandler();
        
        // Link the chain: Level1 -> Level2 -> Level3 -> Manager
        level1.setNext(level2).setNext(level3).setNext(manager);
        
        this.handlerChain = level1;
    }
    
    public boolean submitTicket(SupportTicket ticket) {
        System.out.println("\n=== Processing Support Ticket ===");
        System.out.println("Submitting: " + ticket);
        
        boolean handled = handlerChain.handleTicket(ticket);
        ticketHistory.add(ticket);
        
        System.out.println("Final Status: " + ticket);
        return handled;
    }
    
    public void showStatistics() {
        System.out.println("\n=== Support System Statistics ===");
        System.out.println("Total tickets processed: " + ticketHistory.size());
        
        Map<String, Long> assignmentStats = ticketHistory.stream()
            .filter(t -> t.getAssignedTo() != null)
            .collect(Collectors.groupingBy(
                SupportTicket::getAssignedTo, 
                Collectors.counting()
            ));
        
        System.out.println("Tickets by handler:");
        assignmentStats.forEach((handler, count) -> 
            System.out.println("  " + handler + ": " + count + " tickets"));
        
        long resolvedCount = ticketHistory.stream()
            .mapToLong(t -> t.isResolved() ? 1 : 0)
            .sum();
        
        System.out.println("Resolution rate: " + resolvedCount + "/" + ticketHistory.size() + 
                          " (" + (resolvedCount * 100 / ticketHistory.size()) + "%)");
    }
}

// Usage example
public class ChainOfResponsibilityExample {
    public static void main(String[] args) throws InterruptedException {
        SupportSystem support = new SupportSystem();
        
        // Test various types of tickets
        List<SupportTicket> tickets = Arrays.asList(
            new SupportTicket("T001", SupportTicket.Priority.LOW, 
                            "password reset", "User forgot their password"),
            
            new SupportTicket("T002", SupportTicket.Priority.MEDIUM, 
                            "software bug", "Application crashes on startup"),
            
            new SupportTicket("T003", SupportTicket.Priority.HIGH, 
                            "security breach", "Unauthorized access detected"),
            
            new SupportTicket("T004", SupportTicket.Priority.CRITICAL, 
                            "system crash", "Production database is down"),
            
            new SupportTicket("T005", SupportTicket.Priority.LOW, 
                            "account locked", "User account is locked after multiple login attempts"),
            
            new SupportTicket("T006", SupportTicket.Priority.MEDIUM, 
                            "api issue with critical urgency", "API returning 500 errors - critical for production"),
            
            new SupportTicket("T007", SupportTicket.Priority.HIGH, 
                            "performance issues", "System response time is very slow")
        );
        
        // Process each ticket
        for (SupportTicket ticket : tickets) {
            support.submitTicket(ticket);
            Thread.sleep(500); // Brief pause between tickets
        }
        
        // Show final statistics
        support.showStatistics();
    }
}
```

**Advanced Chain Example** (HTTP Request Processing):
```java
// HTTP Request processing chain
interface RequestHandler {
    void setNext(RequestHandler handler);
    boolean handle(HttpRequest request, HttpResponse response);
}

class AuthenticationHandler implements RequestHandler {
    private RequestHandler next;
    
    @Override
    public void setNext(RequestHandler handler) {
        this.next = handler;
    }
    
    @Override
    public boolean handle(HttpRequest request, HttpResponse response) {
        if (!isAuthenticated(request)) {
            response.setStatus(401);
            response.setBody("Unauthorized");
            return false;
        }
        
        return next != null && next.handle(request, response);
    }
    
    private boolean isAuthenticated(HttpRequest request) {
        return request.getHeader("Authorization") != null;
    }
}

class ValidationHandler implements RequestHandler {
    private RequestHandler next;
    
    @Override
    public void setNext(RequestHandler handler) {
        this.next = handler;
    }
    
    @Override
    public boolean handle(HttpRequest request, HttpResponse response) {
        if (!isValid(request)) {
            response.setStatus(400);
            response.setBody("Bad Request");
            return false;
        }
        
        return next != null && next.handle(request, response);
    }
    
    private boolean isValid(HttpRequest request) {
        // Validation logic
        return request.getBody() != null && !request.getBody().isEmpty();
    }
}

class BusinessLogicHandler implements RequestHandler {
    private RequestHandler next;
    
    @Override
    public void setNext(RequestHandler handler) {
        this.next = handler;
    }
    
    @Override
    public boolean handle(HttpRequest request, HttpResponse response) {
        // Process business logic
        response.setStatus(200);
        response.setBody("Success");
        return true;
    }
}
```

**Common Pitfalls**:
1. **Performance Impact**: Long chains can impact performance due to traversal overhead
2. **Chain Configuration**: Incorrect order or missing links can break functionality
3. **Debugging Difficulty**: Hard to trace which handler processed the request
4. **Handler State**: Handlers with state can cause issues in concurrent environments

**When NOT to Use Chain of Responsibility**:
- When you know exactly which handler should process the request
- When the chain is very long and performance is critical
- When request-handler mapping is simple and static
- When handlers need to communicate with each other

**Thread Safety Considerations**:
```java
// Thread-safe handler with concurrent processing
class ThreadSafeHandler extends SupportHandler {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    protected boolean processTicket(SupportTicket ticket) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            // Process ticket asynchronously
            return doProcessTicket(ticket);
        }, executor);
        
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            return false;
        }
    }
    
    private boolean doProcessTicket(SupportTicket ticket) {
        // Actual processing logic
        return true;
    }
}
```

**Modern Alternatives**:
```java
// Functional approach with stream processing
List<Function<Request, Optional<Response>>> handlers = Arrays.asList(
    authHandler::process,
    validationHandler::process,
    businessHandler::process
);

Optional<Response> result = handlers.stream()
    .map(handler -> handler.apply(request))
    .filter(Optional::isPresent)
    .map(Optional::get)
    .findFirst();

// Using Spring's HandlerInterceptor
@Component
public class MyHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        // Pre-processing logic
        return true;
    }
}
```

**MAANG Interview Talking Points**:
- "Chain of Responsibility is fundamental to middleware systems and request processing pipelines"
- "Used extensively in web frameworks, logging systems, and event processing"
- "Consider performance implications of long chains and implement circuit breakers if needed"
- "Modern functional approaches can provide cleaner alternatives for simple chains"

**Resources**:
- Head First Design Patterns (Chain of Responsibility chapter)
- https://refactoring.guru/design-patterns/chain-of-responsibility/java/example
- https://www.baeldung.com/chain-of-responsibility-pattern

### 4.7 Template Method
**ELI5**: Like a recipe for making cookies - the basic steps are always the same (mix ingredients, bake, cool), but you can customize the ingredients and timing to make different types of cookies.

**Core Idea**: Define the skeleton of an algorithm in a base class, letting subclasses override specific steps without changing the algorithm's structure.

**When to Use**:
- When multiple classes have similar algorithms with slight variations
- When you want to control which parts of an algorithm can be extended
- When you have common behavior that should be centralized
- When you want to avoid code duplication in similar algorithms

**Java Implementation** (Data Processing Pipeline):
```java
// Abstract template class
abstract class DataProcessor {
    
    // Template method - defines the algorithm skeleton
    public final ProcessingResult processData(String inputFile) {
        System.out.println("=== Starting Data Processing Pipeline ===");
        
        long startTime = System.currentTimeMillis();
        ProcessingResult result = new ProcessingResult();
        
        try {
            // Step 1: Validate input
            if (!validateInput(inputFile)) {
                result.setSuccess(false);
                result.setError("Input validation failed");
                return result;
            }
            
            // Step 2: Read data
            RawData rawData = readData(inputFile);
            result.setRecordsRead(rawData.getRecordCount());
            
            // Step 3: Transform data (abstract - must be implemented)
            TransformedData transformedData = transformData(rawData);
            result.setRecordsTransformed(transformedData.getRecordCount());
            
            // Step 4: Validate transformed data (hook - optional override)
            if (!validateTransformedData(transformedData)) {
                result.setSuccess(false);
                result.setError("Transformed data validation failed");
                return result;
            }
            
            // Step 5: Write data (may be overridden)
            String outputFile = writeData(transformedData, inputFile);
            result.setOutputFile(outputFile);
            
            // Step 6: Optional cleanup (hook method)
            cleanup();
            
            result.setSuccess(true);
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError("Processing failed: " + e.getMessage());
            System.err.println("Error during processing: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        result.setProcessingTime(endTime - startTime);
        
        logResult(result);
        return result;
    }
    
    // Concrete method with default implementation
    protected boolean validateInput(String inputFile) {
        if (inputFile == null || inputFile.trim().isEmpty()) {
            System.out.println("❌ Invalid input file path");
            return false;
        }
        
        System.out.println("✓ Input validation passed: " + inputFile);
        return true;
    }
    
    // Concrete method - same for all subclasses
    protected RawData readData(String inputFile) {
        System.out.println("📖 Reading data from: " + inputFile);
        
        // Simulate reading data
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate mock data based on file type
        int recordCount = inputFile.endsWith(".csv") ? 1000 : 
                         inputFile.endsWith(".json") ? 500 : 100;
        
        RawData data = new RawData(inputFile, recordCount);
        System.out.println("✓ Read " + recordCount + " records");
        
        return data;
    }
    
    // Abstract method - must be implemented by subclasses
    protected abstract TransformedData transformData(RawData rawData);
    
    // Hook method - default implementation, can be overridden
    protected boolean validateTransformedData(TransformedData data) {
        if (data.getRecordCount() < 0) {
            System.out.println("❌ Invalid record count in transformed data");
            return false;
        }
        
        System.out.println("✓ Transformed data validation passed");
        return true;
    }
    
    // Concrete method with default implementation - can be overridden
    protected String writeData(TransformedData data, String inputFile) {
        String outputFile = generateOutputFileName(inputFile);
        
        System.out.println("💾 Writing " + data.getRecordCount() + " records to: " + outputFile);
        
        // Simulate writing
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✓ Data written successfully");
        return outputFile;
    }
    
    // Hook method - empty default implementation
    protected void cleanup() {
        System.out.println("🧹 Cleanup completed");
    }
    
    // Helper method
    private String generateOutputFileName(String inputFile) {
        String baseName = inputFile.substring(0, inputFile.lastIndexOf('.'));
        return baseName + "_processed_" + System.currentTimeMillis() + ".out";
    }
    
    // Final method for logging - cannot be overridden
    private final void logResult(ProcessingResult result) {
        System.out.println("\n=== Processing Summary ===");
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Records Read: " + result.getRecordsRead());
        System.out.println("Records Transformed: " + result.getRecordsTransformed());
        System.out.println("Processing Time: " + result.getProcessingTime() + "ms");
        if (!result.isSuccess()) {
            System.out.println("Error: " + result.getError());
        }
        if (result.getOutputFile() != null) {
            System.out.println("Output File: " + result.getOutputFile());
        }
        System.out.println("==========================\n");
    }
}

// Data classes
class RawData {
    private String source;
    private int recordCount;
    private List<String> data;
    
    public RawData(String source, int recordCount) {
        this.source = source;
        this.recordCount = recordCount;
        this.data = generateMockData(recordCount);
    }
    
    private List<String> generateMockData(int count) {
        List<String> mockData = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            mockData.add("Record_" + i);
        }
        return mockData;
    }
    
    public String getSource() { return source; }
    public int getRecordCount() { return recordCount; }
    public List<String> getData() { return data; }
}

class TransformedData {
    private int recordCount;
    private List<String> transformedRecords;
    
    public TransformedData(int recordCount, List<String> transformedRecords) {
        this.recordCount = recordCount;
        this.transformedRecords = transformedRecords;
    }
    
    public int getRecordCount() { return recordCount; }
    public List<String> getTransformedRecords() { return transformedRecords; }
}

class ProcessingResult {
    private boolean success;
    private String error;
    private int recordsRead;
    private int recordsTransformed;
    private long processingTime;
    private String outputFile;
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public int getRecordsRead() { return recordsRead; }
    public void setRecordsRead(int recordsRead) { this.recordsRead = recordsRead; }
    public int getRecordsTransformed() { return recordsTransformed; }
    public void setRecordsTransformed(int recordsTransformed) { this.recordsTransformed = recordsTransformed; }
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    public String getOutputFile() { return outputFile; }
    public void setOutputFile(String outputFile) { this.outputFile = outputFile; }
}

// Concrete implementations
class CSVProcessor extends DataProcessor {
    
    @Override
    protected TransformedData transformData(RawData rawData) {
        System.out.println("🔄 Transforming CSV data...");
        
        // Simulate CSV-specific transformation
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<String> transformed = rawData.getData().stream()
            .map(record -> record.toUpperCase() + "_CSV_PROCESSED")
            .collect(Collectors.toList());
        
        System.out.println("✓ CSV transformation completed");
        return new TransformedData(transformed.size(), transformed);
    }
    
    @Override
    protected boolean validateTransformedData(TransformedData data) {
        // CSV-specific validation
        boolean baseValidation = super.validateTransformedData(data);
        
        if (!baseValidation) return false;
        
        // Check if all records have CSV suffix
        boolean hasCSVSuffix = data.getTransformedRecords().stream()
            .allMatch(record -> record.endsWith("_CSV_PROCESSED"));
        
        if (!hasCSVSuffix) {
            System.out.println("❌ CSV validation failed: Missing CSV processing suffix");
            return false;
        }
        
        System.out.println("✓ CSV-specific validation passed");
        return true;
    }
}

class JSONProcessor extends DataProcessor {
    
    @Override
    protected TransformedData transformData(RawData rawData) {
        System.out.println("🔄 Transforming JSON data...");
        
        // Simulate JSON-specific transformation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<String> transformed = rawData.getData().stream()
            .map(record -> "{\"data\":\"" + record + "\",\"processed\":true}")
            .collect(Collectors.toList());
        
        System.out.println("✓ JSON transformation completed");
        return new TransformedData(transformed.size(), transformed);
    }
    
    @Override
    protected String writeData(TransformedData data, String inputFile) {
        // Override to use JSON-specific output format
        String outputFile = inputFile.replace(".json", "_processed.json");
        
        System.out.println("💾 Writing JSON data to: " + outputFile);
        System.out.println("📝 Using JSON-specific formatting...");
        
        // Simulate JSON writing
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✓ JSON data written successfully");
        return outputFile;
    }
    
    @Override
    protected void cleanup() {
        super.cleanup();
        System.out.println("🧹 JSON-specific cleanup: Clearing JSON cache");
    }
}

class XMLProcessor extends DataProcessor {
    
    @Override
    protected boolean validateInput(String inputFile) {
        // Add XML-specific validation
        if (!super.validateInput(inputFile)) {
            return false;
        }
        
        if (!inputFile.endsWith(".xml")) {
            System.out.println("❌ XML processor requires .xml file");
            return false;
        }
        
        System.out.println("✓ XML-specific validation passed");
        return true;
    }
    
    @Override
    protected TransformedData transformData(RawData rawData) {
        System.out.println("🔄 Transforming XML data...");
        
        // Simulate complex XML transformation
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<String> transformed = rawData.getData().stream()
            .map(record -> "<record><data>" + record + "</data><processed>true</processed></record>")
            .collect(Collectors.toList());
        
        System.out.println("✓ XML transformation completed");
        return new TransformedData(transformed.size(), transformed);
    }
    
    @Override
    protected boolean validateTransformedData(TransformedData data) {
        boolean baseValidation = super.validateTransformedData(data);
        
        if (!baseValidation) return false;
        
        // XML-specific validation - check for well-formed XML
        boolean wellFormed = data.getTransformedRecords().stream()
            .allMatch(record -> record.startsWith("<record>") && record.endsWith("</record>"));
        
        if (!wellFormed) {
            System.out.println("❌ XML validation failed: Malformed XML detected");
            return false;
        }
        
        System.out.println("✓ XML validation passed");
        return true;
    }
    
    @Override
    protected void cleanup() {
        super.cleanup();
        System.out.println("🧹 XML-specific cleanup: Validating XML schema compliance");
    }
}

// Usage example
public class TemplateMethodExample {
    public static void main(String[] args) {
        List<DataProcessor> processors = Arrays.asList(
            new CSVProcessor(),
            new JSONProcessor(),
            new XMLProcessor()
        );
        
        List<String> testFiles = Arrays.asList(
            "sales_data.csv",
            "user_profiles.json",
            "inventory.xml",
            "invalid_file.txt"  // This will fail for XMLProcessor
        );
        
        for (DataProcessor processor : processors) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Testing " + processor.getClass().getSimpleName());
            System.out.println("=".repeat(60));
            
            for (String file : testFiles) {
                System.out.println("\n--- Processing: " + file + " ---");
                
                ProcessingResult result = processor.processData(file);
                
                if (!result.isSuccess()) {
                    System.out.println("❌ Processing failed for " + file);
                } else {
                    System.out.println("✅ Successfully processed " + file);
                }
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
```

**Alternative Template Method Example** (Algorithm Framework):
```java
// Sorting algorithm template
abstract class SortingAlgorithm {
    
    // Template method
    public final long sort(int[] array) {
        long startTime = System.currentTimeMillis();
        
        // Pre-processing hook
        preProcess(array);
        
        // Main sorting logic (abstract)
        performSort(array);
        
        // Post-processing hook
        postProcess(array);
        
        return System.currentTimeMillis() - startTime;
    }
    
    // Hook methods
    protected void preProcess(int[] array) {
        System.out.println("Pre-processing array of size: " + array.length);
    }
    
    protected void postProcess(int[] array) {
        System.out.println("Post-processing completed");
    }
    
    // Abstract method
    protected abstract void performSort(int[] array);
}

class QuickSort extends SortingAlgorithm {
    @Override
    protected void performSort(int[] array) {
        System.out.println("Performing QuickSort...");
        quickSort(array, 0, array.length - 1);
    }
    
    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
    
    private int partition(int[] arr, int low, int high) {
        // QuickSort partitioning logic
        return high; // Simplified
    }
}
```

**Common Pitfalls**:
1. **Inheritance Rigidity**: Changes to base class can break all subclasses
2. **Overuse of hooks**: Too many hook methods can make the template complex
3. **LSP Violations**: Subclasses that don't properly implement the contract
4. **Testing Complexity**: Need to test both template and all concrete implementations

**When NOT to Use Template Method**:
- When algorithms are completely different and share no common structure
- When you need more flexibility than inheritance provides
- When composition would be more appropriate
- When the template becomes too complex with many conditional branches

**Modern Alternatives**:
```java
// Strategy pattern with functional interfaces
public class DataProcessorStrategy {
    private final Function<RawData, TransformedData> transformer;
    private final Function<TransformedData, String> writer;
    
    public DataProcessorStrategy(Function<RawData, TransformedData> transformer,
                               Function<TransformedData, String> writer) {
        this.transformer = transformer;
        this.writer = writer;
    }
    
    public ProcessingResult process(RawData data) {
        TransformedData transformed = transformer.apply(data);
        String output = writer.apply(transformed);
        return new ProcessingResult(true, output);
    }
}

// Usage with lambda expressions
DataProcessorStrategy csvStrategy = new DataProcessorStrategy(
    data -> transformCSV(data),
    data -> writeCSV(data)
);
```

**Composition over Inheritance Alternative**:
```java
public class FlexibleDataProcessor {
    private final DataReader reader;
    private final DataTransformer transformer;
    private final DataWriter writer;
    
    public FlexibleDataProcessor(DataReader reader, 
                               DataTransformer transformer,
                               DataWriter writer) {
        this.reader = reader;
        this.transformer = transformer;
        this.writer = writer;
    }
    
    public ProcessingResult process(String input) {
        RawData raw = reader.read(input);
        TransformedData transformed = transformer.transform(raw);
        String output = writer.write(transformed);
        return new ProcessingResult(true, output);
    }
}
```

**MAANG Interview Talking Points**:
- "Template Method is fundamental to framework design and defining algorithm skeletons"
- "Used in frameworks like Spring, Android lifecycle, and testing frameworks"
- "Consider composition-based alternatives for better flexibility and testability"
- "Important to balance between providing structure and allowing customization"

**Resources**:
- Head First Design Patterns (Template Method chapter)
- https://refactoring.guru/design-patterns/template-method/java/example
- https://www.baeldung.com/java-template-method-pattern

### 4.8 Iterator
ELI5: A finger pointing to each candy one by one.
Java: Built-in `Iterator`.
Scenario: Custom tree traversal.
Pitfall: Exposing internal structure if not encapsulated.

### 4.9 Memento
ELI5: Save game checkpoint to rewind.
Java:
```java
record EditorSnapshot(String text,int cursor){}
class Editor { String text; int cursor; EditorSnapshot save(){ return new EditorSnapshot(text,cursor);} void restore(EditorSnapshot s){ text=s.text(); cursor=s.cursor(); }}
```
Scenario: Undo operations.
Pitfall: Memory heavy; partial snapshots needed.

### 4.10 Visitor
ELI5: Different guests bring different gifts to each type of animal.
Java:
```java
interface Node { void accept(Visitor v); }
class NumberNode implements Node { int v; public void accept(Visitor v){ v.visit(this); }}
interface Visitor { void visit(NumberNode n); }
```
Scenario: Operations across stable object structure (AST, schemas).
Pitfall: Adding new node type requires all visitor updates.

### 4.11 Interpreter (Brief)
ELI5: Tiny language translator.
Scenario: DSL parsing (rules engine). Consider alternating with parser generators.

### 4.12 Null Object
ELI5: A silent friend who never crashes the game.
Java:
```java
interface Logger { void log(String m); }
class NoOpLogger implements Logger { public void log(String m){} }
```

---
## 5. Concurrency / Resilience Patterns

### 5.1 Producer–Consumer
ELI5: Bakers make bread, shoppers take bread from shelf.
Java:
```java
BlockingQueue<Task> q = new LinkedBlockingQueue<>();
// producers: q.put(task); consumers: while(true) process(q.take());
```
Pitfall: Unbounded queue = memory risk.

### 5.2 Thread Pool
ELI5: Few chefs cooking many orders.
Java: `ExecutorService pool = Executors.newFixedThreadPool(8);`
Pitfall: Blocking tasks starve threads.

### 5.3 Future / CompletableFuture
ELI5: Promise to deliver toy later.
Java:
```java
CompletableFuture.supplyAsync(() -> fetch()).thenApply(this::transform).join();
```
Pitfall: ForkJoin pool blocking.

### 5.4 Circuit Breaker
ELI5: Flip switch off when toy overheats.
Pseudo:
```java
if(open && now < retryAt) failFast(); if(callFails) track(); if(failureRate>threshold) open();
```
Use libs: Resilience4j.

### 5.5 Bulkhead
ELI5: Ship compartments so one leak doesn’t sink all.
Implementation: Separate thread pools or semaphores per dependency.

### 5.6 Retry with Backoff + Jitter
ELI5: Knock on door, wait a bit longer each time, but slightly random.

### 5.7 Idempotent Consumer
ELI5: Check sticker; if already opened, don’t open again.
Store processed event IDs.

### 5.8 Event Sourcing
ELI5: Keep every Lego piece change, rebuild final castle when needed.
Pitfall: Snapshotting required to avoid replay cost.

### 5.9 CQRS
ELI5: One door for writing stories, another door for reading compiled book.
Pair with Event Sourcing for scalability.

### 5.10 Saga
ELI5: Series of chores; if one fails, undo previous chores.
Use orchestrator or choreography via events.

### 5.11 Rate Limiter / Token Bucket
ELI5: Only certain number of candies per minute.

### 5.12 Leader Election
ELI5: One kid chosen to hold the flag.
Implementation: ZK ephemeral nodes, Raft consensus.

---
## 6. Architectural & Integration Patterns

### 6.1 Layered / Hexagonal Architecture
ELI5: Core castle inside walls; outside world changes without touching inner.
Ports (interfaces) & Adapters (infra implementation).

### 6.2 Microservices vs Modular Monolith
ELI5: Many small toys vs one big transformer toy with clear parts.
Trade-off: Deployment flexibility vs complexity.

### 6.3 API Gateway + BFF
ELI5: One waiter for kitchen vs custom lunch tray per table.

### 6.4 Event-Driven (Pub/Sub)
ELI5: Shout in playground; all who care listen.
Pitfall: Hidden coupling via event schema evolution.

### 6.5 Outbox Pattern
ELI5: Put outgoing letters in a box so mailman delivers reliably.
Ensure atomic DB write + message via polling or CDC.

### 6.6 Cache Patterns (Aside / Read-Through / Write-Behind)
ELI5: Peek in lunchbox first before going to kitchen.

### 6.7 Sharding
ELI5: Split crayons among boxes so easier to find color.
Hash userId -> partition.

### 6.8 Blue-Green / Canary Deployments
ELI5: Taste small spoon before serving whole pot.

### 6.9 Feature Flags
ELI5: Light switches to turn new toys on/off easily.
Pitfall: Flag debt; cleanup.

### 6.10 API Idempotency Keys
ELI5: Ticket number so same request not double charged.

### 6.11 Data Lake vs Warehouse
ELI5: Lake = all water raw; bottle plant = cleaned, structured.

### 6.12 Backpressure
ELI5: Slow eater signals others to slow feeding.

### 6.13 Distributed Tracing
ELI5: Breadcrumb trail all the way home.
Attach traceId/spanId through calls.

---
## 7. Comparison Cheat Sheets

| Topic | Choose A | Over | Choose B | Rationale |
|-------|----------|------|----------|-----------|
| Strategy vs State | Strategy | When algorithms differ independently of object state | State | When object behavior changes with transitions |
| Factory Method vs Abstract Factory | Factory Method | Single product hierarchy | Abstract Factory | Families of related products |
| Builder vs Abstract Factory | Builder | Stepwise complex assembly | Abstract Factory | Fixed set of products created immediately |
| Adapter vs Proxy | Adapter | Interface shape change | Proxy | Control access / add lazy / remote |
| Proxy vs Decorator | Decorator | Add cross-cut feature layering | Proxy | Access, lazy, remote, protection |
| Facade vs Adapter | Facade | Simplify multiple interfaces | Adapter | One interface translated to another |
| Observer vs Mediator | Observer | Broadcast events loosely | Mediator | Centralizing complex interaction logic |
| Command vs Strategy | Command | Encapsulate invocation + undo | Strategy | Swap algorithm, stateless usually |
| Template vs Strategy | Strategy | Composition + runtime swap | Template | Controlled steps w/ inheritance |
| Prototype vs Builder | Prototype | Clone heavy object cheaply | Builder | Controlled new assembly config |
| Singleton vs DI Container | DI Container | Testability / decouple | Singleton | Genuine single instance, simple |
| Event Sourcing vs CRUD | Event Sourcing | Audit & time travel critical | CRUD | Simpler, snapshot sufficient |
| CQRS vs Single Model | Single Model | Simplicity outweighs scale | CQRS | Read & write scaling diverge |
| Saga vs 2PC | Saga | Availability & scalability | 2PC | Strong immediate consistency |
| Circuit Breaker vs Retry | Circuit Breaker | Prevent cascading failure | Retry | Transient fault with fast recovery |
| Cache Aside vs Read-Through | Read-Through | Library mediates fetch | Cache Aside | Manual control, simplicity |
| Flyweight vs Pool | Flyweight | Many identical immutables | Pool | Expensive stateful objects |
| Microservices vs Modular Monolith | Modular Monolith | Early stage, local team | Microservices | Team autonomy, scale |

Decision Heuristic Pattern Selection (Mnemonic): ACTOR FACES
- A: Access Control → Proxy
- C: Construction complexity → Builder
- T: Transform interface → Adapter
- O: Object state-driven behavior → State
- R: Reversible actions (undo) → Command + Memento
- F: Families of products → Abstract Factory
- A: Algorithm swap → Strategy
- C: Cross-cut features → Decorator
- E: Event notification → Observer
- S: Sequence pipeline with fallback → Chain of Responsibility

---
## 8. Interview Story Hooks
Use pattern + impact + metric.
- Decorator: Added caching decorator; reduced p95 latency 35%.
- Circuit Breaker: Introduced breaker + fallback; cut cascading errors Sev1→Sev2 frequency -60%.
- Saga: Replaced partial 2PC; improved order throughput +25%, reduced lock wait.
- Event Sourcing: Enabled retroactive reconciliation saving 6 engineer-days/month.

Format: Situation → Constraint → Pattern Applied → Result (quant) → Lesson.

---
## Resource Index (Aggregated)
- Refactoring Guru (pattern visuals): https://refactoring.guru/design-patterns
- GoF Patterns Reference: https://en.wikipedia.org/wiki/Design_Patterns
- Effective Java (Bloch) topics
- Martin Fowler Articles: https://martinfowler.com/
- Microservices Patterns (Richardson) concepts
- Designing Data-Intensive Applications (Kleppmann) for event sourcing/CQRS
- Resilience4j docs: https://resilience4j.readme.io/
- AWS Builders Library: https://aws.amazon.com/builders-library/
- Google SRE Book: https://sre.google/

Each section had core resources; use this index for quick recall.

---
## Practice Drill Prompts
1. Replace inheritance misuse with composition for a payment processor (Strategy + Decorator). Identify complexity delta metrics.
2. Given latency spike from remote dependency, propose triad: Circuit Breaker + Bulkhead + Timeout; diagram sequence.
3. Convert CRUD order service to Event Sourcing + CQRS; define snapshot cadence & read model rebuild.
4. Distinguish handling for adding new widget theme across Abstract Factory vs Builder vs Prototype.

---
## Quick Pattern → Anti-Pattern Mapping
- Singleton → God Object / Hidden Global State
- Observer → Event Storm (unbounded fan-out)
- Chain → Lava Flow if chain order undocumented
- Prototype → Cloning Hell with deep cycles
- Decorator → Wrapper Soup (monitoring complexity)
- Strategy → Parameter Object might suffice if too fine-grained

---
## Rapid Recall Table
| Category | Top 3 talked patterns (frequency) | Key Metric Impact |
|----------|-----------------------------------|-------------------|
| Creational | Builder, Factory, Singleton | Config clarity, testability |
| Structural | Decorator, Adapter, Proxy | Latency, extensibility |
| Behavioral | Strategy, Observer, Command | Flexibility, decoupling |
| Concurrency | Circuit Breaker, Bulkhead, Retry | Availability, error rate |
| Architecture | Event Sourcing, CQRS, Saga | Consistency model, audit |

---
## How To Expand Further
- Add full UML sketches.
- Add complexity & runtime impact per pattern.
- Add concurrency safety table (thread-safe vs need external sync).
- Generate flashcards: Pattern → ELI5; Pattern → Use-case; Pattern vs Similar.

---
End of Compendium.
