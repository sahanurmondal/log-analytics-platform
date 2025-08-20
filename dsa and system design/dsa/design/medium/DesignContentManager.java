package design.medium;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Design Content Management System
 *
 * Description: Design a CMS that supports:
 * - Content creation, editing, and publishing
 * - Version control and workflow management
 * - User roles and permissions
 * - Content scheduling and categorization
 * 
 * Constraints:
 * - Support multiple content types
 * - Handle concurrent editing
 * - Maintain content history
 *
 * Follow-up:
 * - How to handle media files?
 * - Caching strategies?
 * 
 * Time Complexity: O(1) for most operations, O(log n) for search
 * Space Complexity: O(content + versions + users)
 * 
 * Company Tags: WordPress, Drupal, Adobe
 */
public class DesignContentManager {

    enum ContentType {
        ARTICLE, PAGE, BLOG_POST, MEDIA, DOCUMENT
    }

    enum ContentStatus {
        DRAFT, PENDING_REVIEW, APPROVED, PUBLISHED, ARCHIVED, DELETED
    }

    enum UserRole {
        ADMIN, EDITOR, AUTHOR, CONTRIBUTOR, VIEWER
    }

    enum Permission {
        CREATE, READ, UPDATE, DELETE, PUBLISH, MANAGE_USERS
    }

    class User {
        String userId;
        String username;
        String email;
        Set<UserRole> roles;
        Set<Permission> permissions;
        long createdAt;
        long lastLogin;

        User(String userId, String username, String email) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.roles = new HashSet<>();
            this.permissions = new HashSet<>();
            this.createdAt = System.currentTimeMillis();
            this.lastLogin = 0;
        }

        boolean hasPermission(Permission permission) {
            if (permissions.contains(permission)) {
                return true;
            }

            // Check role-based permissions
            for (UserRole role : roles) {
                if (getRolePermissions(role).contains(permission)) {
                    return true;
                }
            }

            return false;
        }

        void addRole(UserRole role) {
            roles.add(role);
        }

        void removeRole(UserRole role) {
            roles.remove(role);
        }
    }

    class ContentVersion {
        String versionId;
        String contentId;
        String title;
        String body;
        Map<String, String> metadata;
        String authorId;
        long createdAt;
        String comment;

        ContentVersion(String contentId, String title, String body, String authorId, String comment) {
            this.versionId = UUID.randomUUID().toString();
            this.contentId = contentId;
            this.title = title;
            this.body = body;
            this.authorId = authorId;
            this.comment = comment;
            this.metadata = new HashMap<>();
            this.createdAt = System.currentTimeMillis();
        }
    }

    class Content {
        String contentId;
        String title;
        String body;
        ContentType type;
        ContentStatus status;
        String authorId;
        String categoryId;
        Set<String> tags;
        Map<String, String> metadata;
        List<ContentVersion> versions;
        String currentVersionId;
        long createdAt;
        long lastModified;
        long publishedAt;
        long scheduledAt;
        String slug;

        Content(String title, String body, ContentType type, String authorId) {
            this.contentId = UUID.randomUUID().toString();
            this.title = title;
            this.body = body;
            this.type = type;
            this.status = ContentStatus.DRAFT;
            this.authorId = authorId;
            this.tags = new HashSet<>();
            this.metadata = new HashMap<>();
            this.versions = new ArrayList<>();
            this.createdAt = System.currentTimeMillis();
            this.lastModified = createdAt;
            this.slug = generateSlug(title);

            // Create initial version
            ContentVersion initialVersion = new ContentVersion(contentId, title, body, authorId, "Initial version");
            versions.add(initialVersion);
            currentVersionId = initialVersion.versionId;
        }

        private String generateSlug(String title) {
            return title.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("^-+|-+$", "");
        }

        void updateContent(String newTitle, String newBody, String editorId, String comment) {
            this.title = newTitle;
            this.body = newBody;
            this.lastModified = System.currentTimeMillis();
            this.slug = generateSlug(newTitle);

            ContentVersion newVersion = new ContentVersion(contentId, newTitle, newBody, editorId, comment);
            versions.add(newVersion);
            currentVersionId = newVersion.versionId;
        }

        ContentVersion getCurrentVersion() {
            return versions.stream()
                    .filter(v -> v.versionId.equals(currentVersionId))
                    .findFirst()
                    .orElse(null);
        }

        boolean canBeEditedBy(String userId) {
            return authorId.equals(userId) || status == ContentStatus.DRAFT;
        }
    }

    class Category {
        String categoryId;
        String name;
        String description;
        String parentId;
        Set<String> childrenIds;
        int contentCount;

        Category(String name, String description, String parentId) {
            this.categoryId = UUID.randomUUID().toString();
            this.name = name;
            this.description = description;
            this.parentId = parentId;
            this.childrenIds = new HashSet<>();
            this.contentCount = 0;
        }
    }

    class Workflow {
        String workflowId;
        String name;
        List<ContentStatus> steps;
        Map<ContentStatus, Set<UserRole>> requiredRoles;

        Workflow(String name) {
            this.workflowId = UUID.randomUUID().toString();
            this.name = name;
            this.steps = new ArrayList<>();
            this.requiredRoles = new HashMap<>();
        }

        void addStep(ContentStatus status, Set<UserRole> roles) {
            steps.add(status);
            requiredRoles.put(status, new HashSet<>(roles));
        }

        ContentStatus getNextStatus(ContentStatus current) {
            int currentIndex = steps.indexOf(current);
            if (currentIndex >= 0 && currentIndex < steps.size() - 1) {
                return steps.get(currentIndex + 1);
            }
            return current;
        }

        boolean canTransition(ContentStatus from, ContentStatus to, Set<UserRole> userRoles) {
            Set<UserRole> requiredForTransition = requiredRoles.get(to);
            if (requiredForTransition == null)
                return false;

            return userRoles.stream().anyMatch(requiredForTransition::contains);
        }
    }

    private Map<String, User> users;
    private Map<String, Content> contents;
    private Map<String, Category> categories;
    private Map<String, Workflow> workflows;
    private Map<ContentType, String> defaultWorkflows;

    public DesignContentManager() {
        users = new HashMap<>();
        contents = new HashMap<>();
        categories = new HashMap<>();
        workflows = new HashMap<>();
        defaultWorkflows = new HashMap<>();

        initializeDefaultWorkflows();
    }

    private void initializeDefaultWorkflows() {
        // Create default workflow
        Workflow defaultWorkflow = new Workflow("Default Workflow");
        defaultWorkflow.addStep(ContentStatus.DRAFT, Set.of(UserRole.AUTHOR, UserRole.CONTRIBUTOR));
        defaultWorkflow.addStep(ContentStatus.PENDING_REVIEW, Set.of(UserRole.AUTHOR, UserRole.CONTRIBUTOR));
        defaultWorkflow.addStep(ContentStatus.APPROVED, Set.of(UserRole.EDITOR, UserRole.ADMIN));
        defaultWorkflow.addStep(ContentStatus.PUBLISHED, Set.of(UserRole.EDITOR, UserRole.ADMIN));

        workflows.put(defaultWorkflow.workflowId, defaultWorkflow);

        // Set as default for all content types
        for (ContentType type : ContentType.values()) {
            defaultWorkflows.put(type, defaultWorkflow.workflowId);
        }
    }

    private Set<Permission> getRolePermissions(UserRole role) {
        switch (role) {
            case ADMIN:
                return Set.of(Permission.CREATE, Permission.READ, Permission.UPDATE,
                        Permission.DELETE, Permission.PUBLISH, Permission.MANAGE_USERS);
            case EDITOR:
                return Set.of(Permission.CREATE, Permission.READ, Permission.UPDATE,
                        Permission.DELETE, Permission.PUBLISH);
            case AUTHOR:
                return Set.of(Permission.CREATE, Permission.READ, Permission.UPDATE);
            case CONTRIBUTOR:
                return Set.of(Permission.CREATE, Permission.READ);
            case VIEWER:
                return Set.of(Permission.READ);
            default:
                return new HashSet<>();
        }
    }

    public String createUser(String username, String email, Set<UserRole> roles) {
        User user = new User(UUID.randomUUID().toString(), username, email);
        user.roles.addAll(roles);
        users.put(user.userId, user);
        return user.userId;
    }

    public String createContent(String title, String body, ContentType type, String authorId) {
        User author = users.get(authorId);
        if (author == null || !author.hasPermission(Permission.CREATE)) {
            throw new IllegalArgumentException("User cannot create content");
        }

        Content content = new Content(title, body, type, authorId);
        contents.put(content.contentId, content);

        return content.contentId;
    }

    public boolean updateContent(String contentId, String title, String body, String editorId, String comment) {
        Content content = contents.get(contentId);
        User editor = users.get(editorId);

        if (content == null || editor == null) {
            return false;
        }

        if (!editor.hasPermission(Permission.UPDATE) || !content.canBeEditedBy(editorId)) {
            return false;
        }

        content.updateContent(title, body, editorId, comment);
        return true;
    }

    public boolean changeContentStatus(String contentId, ContentStatus newStatus, String userId) {
        Content content = contents.get(contentId);
        User user = users.get(userId);

        if (content == null || user == null) {
            return false;
        }

        String workflowId = defaultWorkflows.get(content.type);
        Workflow workflow = workflows.get(workflowId);

        if (workflow != null && !workflow.canTransition(content.status, newStatus, user.roles)) {
            return false;
        }

        content.status = newStatus;
        content.lastModified = System.currentTimeMillis();

        if (newStatus == ContentStatus.PUBLISHED) {
            content.publishedAt = System.currentTimeMillis();
        }

        return true;
    }

    public boolean publishContent(String contentId, String publisherId) {
        User publisher = users.get(publisherId);
        if (publisher == null || !publisher.hasPermission(Permission.PUBLISH)) {
            return false;
        }

        return changeContentStatus(contentId, ContentStatus.PUBLISHED, publisherId);
    }

    public boolean scheduleContent(String contentId, long scheduledTime, String schedulerId) {
        Content content = contents.get(contentId);
        User scheduler = users.get(schedulerId);

        if (content == null || scheduler == null || !scheduler.hasPermission(Permission.PUBLISH)) {
            return false;
        }

        content.scheduledAt = scheduledTime;
        return true;
    }

    public String createCategory(String name, String description, String parentId) {
        Category category = new Category(name, description, parentId);
        categories.put(category.categoryId, category);

        if (parentId != null) {
            Category parent = categories.get(parentId);
            if (parent != null) {
                parent.childrenIds.add(category.categoryId);
            }
        }

        return category.categoryId;
    }

    public void categorizeContent(String contentId, String categoryId) {
        Content content = contents.get(contentId);
        Category category = categories.get(categoryId);

        if (content != null && category != null) {
            // Remove from old category
            if (content.categoryId != null) {
                Category oldCategory = categories.get(content.categoryId);
                if (oldCategory != null) {
                    oldCategory.contentCount--;
                }
            }

            // Add to new category
            content.categoryId = categoryId;
            category.contentCount++;
        }
    }

    public void tagContent(String contentId, Set<String> tags) {
        Content content = contents.get(contentId);
        if (content != null) {
            content.tags.clear();
            content.tags.addAll(tags);
        }
    }

    public List<Content> searchContent(String query, ContentType type, ContentStatus status, String categoryId) {
        return contents.values().stream()
                .filter(c -> status == null || c.status == status)
                .filter(c -> type == null || c.type == type)
                .filter(c -> categoryId == null || categoryId.equals(c.categoryId))
                .filter(c -> query == null || query.isEmpty() ||
                        c.title.toLowerCase().contains(query.toLowerCase()) ||
                        c.body.toLowerCase().contains(query.toLowerCase()) ||
                        c.tags.stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))
                .sorted((a, b) -> Long.compare(b.lastModified, a.lastModified))
                .collect(Collectors.toList());
    }

    public List<Content> getContentByAuthor(String authorId) {
        return contents.values().stream()
                .filter(c -> c.authorId.equals(authorId))
                .sorted((a, b) -> Long.compare(b.lastModified, a.lastModified))
                .collect(Collectors.toList());
    }

    public List<Content> getPublishedContent(int limit) {
        return contents.values().stream()
                .filter(c -> c.status == ContentStatus.PUBLISHED)
                .sorted((a, b) -> Long.compare(b.publishedAt, a.publishedAt))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<ContentVersion> getContentHistory(String contentId) {
        Content content = contents.get(contentId);
        if (content != null) {
            return new ArrayList<>(content.versions);
        }
        return new ArrayList<>();
    }

    public boolean revertToVersion(String contentId, String versionId, String userId) {
        Content content = contents.get(contentId);
        User user = users.get(userId);

        if (content == null || user == null || !user.hasPermission(Permission.UPDATE)) {
            return false;
        }

        ContentVersion targetVersion = content.versions.stream()
                .filter(v -> v.versionId.equals(versionId))
                .findFirst()
                .orElse(null);

        if (targetVersion != null) {
            content.updateContent(targetVersion.title, targetVersion.body, userId, "Reverted to version " + versionId);
            return true;
        }

        return false;
    }

    public Map<String, Object> getContentStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalContent", contents.size());
        stats.put("totalUsers", users.size());
        stats.put("totalCategories", categories.size());

        Map<ContentStatus, Long> statusCounts = contents.values().stream()
                .collect(Collectors.groupingBy(c -> c.status, Collectors.counting()));
        stats.put("contentByStatus", statusCounts);

        Map<ContentType, Long> typeCounts = contents.values().stream()
                .collect(Collectors.groupingBy(c -> c.type, Collectors.counting()));
        stats.put("contentByType", typeCounts);

        return stats;
    }

    public static void main(String[] args) {
        DesignContentManager cms = new DesignContentManager();

        // Create users
        String adminId = cms.createUser("admin", "admin@example.com", Set.of(UserRole.ADMIN));
        String editorId = cms.createUser("editor", "editor@example.com", Set.of(UserRole.EDITOR));
        String authorId = cms.createUser("author", "author@example.com", Set.of(UserRole.AUTHOR));

        // Create categories
        String techCategoryId = cms.createCategory("Technology", "Tech articles", null);
        String javaCategoryId = cms.createCategory("Java", "Java programming", techCategoryId);

        // Create content
        String articleId = cms.createContent("Introduction to Java",
                "Java is a versatile programming language...",
                ContentType.ARTICLE, authorId);

        cms.categorizeContent(articleId, javaCategoryId);
        cms.tagContent(articleId, Set.of("java", "programming", "tutorial"));

        // Update content
        cms.updateContent(articleId, "Complete Guide to Java Programming",
                "Java is a powerful and versatile programming language...",
                authorId, "Updated title and content");

        // Change status workflow
        cms.changeContentStatus(articleId, ContentStatus.PENDING_REVIEW, authorId);
        cms.changeContentStatus(articleId, ContentStatus.APPROVED, editorId);
        cms.publishContent(articleId, editorId);

        // Search content
        System.out.println("Published content:");
        List<Content> publishedContent = cms.getPublishedContent(10);
        for (Content content : publishedContent) {
            System.out.println("- " + content.title + " by " + content.authorId);
        }

        // Show content history
        System.out.println("\nContent history:");
        List<ContentVersion> history = cms.getContentHistory(articleId);
        for (ContentVersion version : history) {
            System.out.println("- Version " + version.versionId.substring(0, 8) +
                    ": " + version.title + " (" + version.comment + ")");
        }

        // Show stats
        System.out.println("\nCMS Statistics:");
        System.out.println(cms.getContentStats());
    }
}
