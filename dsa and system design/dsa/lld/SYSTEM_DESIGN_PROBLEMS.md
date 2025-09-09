# System Design Problems Collection - 50 Recent Interview Problems

## Overview
This collection contains 50 real system design problems frequently asked in MAANG+ interviews (2023-2024). Each problem includes detailed requirements, constraints, high-level design diagrams, NFRs, core entities, APIs, deep dive sections, and database choices.

---

## 1. Design WhatsApp/Messaging Service

**Problem**: Design a real-time messaging platform supporting 2B+ users

**Functional Requirements**:
- Send/receive messages in real-time
- Group chats (up to 256 members)
- Media sharing (images, videos, documents)
- Message delivery status (sent, delivered, read)
- End-to-end encryption
- User presence (online/offline/last seen)

**Non-Functional Requirements (NFRs)**:
- Availability: 99.99% uptime
- Latency: <100ms message delivery
- Throughput: 100B messages/day
- Consistency: Eventually consistent for message ordering
- Security: End-to-end encryption, data privacy
- Scalability: Auto-scale to handle traffic spikes

**Scale**: 2B users, 100B messages/day, 1M concurrent connections

**Core Entities**:
```java
class User {
    String userId;
    String phoneNumber;
    String displayName;
    UserStatus status; // ONLINE, OFFLINE, AWAY
    long lastSeen;
    byte[] publicKey;
}

class Message {
    String messageId;
    String senderId;
    String recipientId; // or groupId
    MessageType type; // TEXT, IMAGE, VIDEO, DOCUMENT
    String content;
    byte[] encryptedContent;
    long timestamp;
    MessageStatus status; // SENT, DELIVERED, READ
}

class Group {
    String groupId;
    String name;
    String description;
    List<String> memberIds;
    String adminId;
    long createdAt;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/status

// Messaging
POST /api/v1/messages/send
GET /api/v1/messages/{userId}/history
PUT /api/v1/messages/{messageId}/status

// Groups
POST /api/v1/groups/create
POST /api/v1/groups/{groupId}/members
GET /api/v1/groups/{userId}/list

// WebSocket Events
WS /ws/connect
Event: message_received, user_status_changed, typing_indicator
```

**HLD**:
```
[Mobile Apps] --> [Load Balancer] --> [API Gateway]
                                          |
                    [Message Service] --> [WebSocket Gateway]
                           |                      |
                    [User Service]        [Real-time Engine]
                           |                      |
                    [Message DB]          [Redis Cluster]
                           |                      |
                    [Media Storage]       [Notification Service]
```

**Deep Dive - Message Delivery**:
```
1. Client sends message to Message Service
2. Message Service encrypts and stores in Message DB
3. Message queued in Redis for real-time delivery
4. WebSocket Gateway pushes to recipient if online
5. If offline, Notification Service sends push notification
6. Delivery status updated when recipient receives message
```

**Database Choices**:
- **Message DB**: Cassandra (write-heavy, time-series data)
- **User DB**: PostgreSQL (ACID properties for user data)
- **Cache**: Redis (real-time presence, message queues)
- **Media Storage**: S3/GCS (blob storage for files)
- **Search**: Elasticsearch (message search functionality)

**Key Design Decisions**:
- WebSocket connections for real-time messaging
- Message queues for reliable delivery
- Database sharding by user_id
- CDN for media files
- Redis for caching and presence

---

## 2. Design Netflix/Video Streaming

**Problem**: Design a global video streaming platform

**Functional Requirements**:
- Stream videos to millions of users simultaneously
- Multiple video qualities (240p to 4K)
- Content recommendation system
- User profiles and watchlists
- Global content delivery
- Offline downloads

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <2s video start time globally
- Throughput: 1B hours watched daily
- Consistency: Eventual consistency for recommendations
- Security: DRM protection, secure streaming
- Scalability: Global CDN distribution

**Scale**: 200M users, 1B hours watched daily, 100TB new content daily

**Core Entities**:
```java
class Video {
    String videoId;
    String title;
    String description;
    List<String> genres;
    int duration;
    Map<Quality, String> streamUrls;
    String thumbnailUrl;
    VideoMetadata metadata;
    long uploadTime;
}

class User {
    String userId;
    String email;
    SubscriptionPlan plan;
    List<String> watchHistory;
    List<String> watchlist;
    Map<String, Integer> preferences;
}

class ViewingSession {
    String sessionId;
    String userId;
    String videoId;
    long startTime;
    long currentPosition;
    Quality currentQuality;
    String deviceType;
}
```

**APIs**:
```java
// Content Management
GET /api/v1/videos/{videoId}/metadata
GET /api/v1/videos/{videoId}/stream?quality={quality}
GET /api/v1/videos/search?query={query}

// User Management
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/watchlist
GET /api/v1/users/{userId}/recommendations

// Analytics
POST /api/v1/analytics/viewing-session
PUT /api/v1/analytics/viewing-session/{sessionId}/progress
```

**HLD**:
```
[Client Apps] --> [CDN Network] --> [Edge Servers]
                      |                    |
               [Content Service] --> [Recommendation Engine]
                      |                    |
               [User Service]       [Analytics Pipeline]
                      |                    |
               [Video Metadata DB]   [ML Models]
                      |                    |
               [Video Storage]      [A/B Testing Platform]
```

**Deep Dive - Video Streaming**:
```
1. User requests video → CDN edge server
2. If not cached, fetch from origin storage
3. Adaptive bitrate streaming based on bandwidth
4. Analytics collected for viewing patterns
5. ML models update recommendations
6. A/B testing for UI/UX improvements
```

**Database Choices**:
- **Video Metadata**: MongoDB (flexible schema for metadata)
- **User Data**: PostgreSQL (transactional user data)
- **Analytics**: ClickHouse (time-series analytics data)
- **Recommendations**: Neo4j (graph-based relationships)
- **Cache**: Redis (frequently accessed metadata)
- **Video Storage**: S3/GCS (object storage for video files)

**Key Design Decisions**:
- Global CDN with edge caching
- Adaptive bitrate streaming
- Microservices architecture
- ML-based recommendations
- Content pre-positioning based on popularity

---

## 3. Design Uber/Ride Sharing

**Problem**: Design a ride-sharing platform matching drivers and riders

**Functional Requirements**:
- Real-time driver-rider matching
- Dynamic pricing based on demand
- Route optimization and ETA calculation
- Driver tracking and location updates
- Payment processing
- Rating and feedback system

**Non-Functional Requirements (NFRs)**:
- Availability: 99.99% uptime (critical for safety)
- Latency: <3s for ride matching
- Throughput: 15M rides/day globally
- Consistency: Strong consistency for payments
- Security: Location privacy, secure payments
- Scalability: Handle city-wide surge events

**Scale**: 100M users, 15M rides/day, 5M drivers globally

**Core Entities**:
```java
class Driver {
    String driverId;
    String name;
    Location currentLocation;
    DriverStatus status; // AVAILABLE, BUSY, OFFLINE
    VehicleInfo vehicle;
    double rating;
    int totalTrips;
}

class Rider {
    String riderId;
    String name;
    PaymentMethod paymentMethod;
    double rating;
    List<String> tripHistory;
}

class Trip {
    String tripId;
    String riderId;
    String driverId;
    Location pickupLocation;
    Location dropoffLocation;
    TripStatus status;
    double fare;
    long requestTime;
    long startTime;
    long endTime;
}

class Location {
    double latitude;
    double longitude;
    String address;
    long timestamp;
}
```

**APIs**:
```java
// Driver APIs
POST /api/v1/drivers/{driverId}/location
PUT /api/v1/drivers/{driverId}/status
GET /api/v1/drivers/{driverId}/trips

// Rider APIs
POST /api/v1/riders/{riderId}/request-ride
GET /api/v1/riders/{riderId}/trip/{tripId}/status
POST /api/v1/riders/{riderId}/trip/{tripId}/cancel

// Trip Management
GET /api/v1/trips/{tripId}/route
PUT /api/v1/trips/{tripId}/status
POST /api/v1/trips/{tripId}/payment

// Real-time Updates
WS /ws/driver/{driverId}/location-updates
WS /ws/rider/{riderId}/trip-updates
```

**HLD**:
```
[Driver App] --> [Location Service] --> [Matching Service]
     |                |                       |
[Rider App]    [Geospatial DB]         [Pricing Engine]
     |                |                       |
[Trip Service] --> [Route Service] --> [Payment Service]
     |                |                       |
[Trip DB]        [Maps API]            [Notification Service]
```

**Deep Dive - Driver Matching**:
```
1. Rider requests ride with pickup/dropoff locations
2. Location Service finds nearby available drivers
3. Matching Service ranks drivers by distance, rating
4. Pricing Engine calculates fare based on demand
5. Trip request sent to best-matched driver
6. Driver accepts/rejects within timeout
7. If rejected, try next driver in ranking
8. Real-time location tracking during trip
```

**Database Choices**:
- **Location Data**: Redis with geospatial indexing
- **Trip Data**: PostgreSQL (ACID for financial data)
- **Driver/Rider Profiles**: MongoDB (flexible user data)
- **Analytics**: ClickHouse (trip analytics, pricing)
- **Route Cache**: Redis (frequently used routes)
- **Payment Audit**: PostgreSQL (financial compliance)

**Key Design Decisions**:
- Geospatial indexing for location queries
- Real-time driver tracking with GPS
- Supply-demand based pricing
- Distributed trip state management
- Event-driven architecture for notifications

---

## 4. Design Twitter/Social Media Feed

**Problem**: Design a social media platform with timeline feeds

**Functional Requirements**:
- Post tweets (text, images, videos)
- Follow/unfollow users
- Home timeline generation
- Trending topics
- Search functionality
- Real-time notifications

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms timeline loading
- Throughput: 500M tweets/day, 300K reads/sec
- Consistency: Eventually consistent timelines
- Security: Content moderation, spam detection
- Scalability: Handle viral content spikes

**Scale**: 400M users, 500M tweets/day, 300K reads/sec

**Core Entities**:
```java
class User {
    String userId;
    String username;
    String displayName;
    String bio;
    int followersCount;
    int followingCount;
    boolean verified;
    long joinDate;
}

class Tweet {
    String tweetId;
    String userId;
    String content;
    List<String> hashtags;
    List<String> mentions;
    String mediaUrl;
    int likeCount;
    int retweetCount;
    int replyCount;
    long timestamp;
}

class Follow {
    String followerId;
    String followeeId;
    long followDate;
}

class Timeline {
    String userId;
    List<String> tweetIds;
    long lastUpdated;
}
```

**APIs**:
```java
// Tweet Management
POST /api/v1/tweets/create
GET /api/v1/tweets/{tweetId}
DELETE /api/v1/tweets/{tweetId}
POST /api/v1/tweets/{tweetId}/like

// User Management
POST /api/v1/users/{userId}/follow
DELETE /api/v1/users/{userId}/unfollow
GET /api/v1/users/{userId}/profile

// Timeline
GET /api/v1/users/{userId}/timeline?limit={limit}
GET /api/v1/tweets/trending

// Search
GET /api/v1/search/tweets?query={query}
GET /api/v1/search/users?query={query}
```

**HLD**:
```
[Client Apps] --> [Load Balancer] --> [Tweet Service]
                                          |
                  [Timeline Service] --> [Fan-out Service]
                          |                     |
                  [User Service]        [Timeline Cache]
                          |                     |
                  [User DB]             [Tweet DB]
                          |                     |
                  [Media Storage]       [Search Service]
```

**Deep Dive - Timeline Generation**:
```
Push Model (for regular users):
1. User posts tweet
2. Fan-out service gets follower list
3. Tweet added to each follower's timeline cache
4. Real-time updates via WebSocket

Pull Model (for celebrities):
1. User requests timeline
2. Timeline service queries recent tweets from following
3. Merge and rank tweets by timestamp/relevance
4. Cache result for subsequent requests

Hybrid Model:
- Push for users with <1M followers
- Pull for celebrities with >1M followers
```

**Database Choices**:
- **Tweet Storage**: Cassandra (time-series, write-heavy)
- **User Profiles**: PostgreSQL (structured user data)
- **Timeline Cache**: Redis (fast timeline retrieval)
- **Search Index**: Elasticsearch (tweet search)
- **Graph Data**: Neo4j (follower relationships)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Push vs Pull model for timeline generation
- Fan-out strategies for celebrities
- Cache-heavy architecture
- Elasticsearch for search
- Async processing for scalability

---

## 5. Design YouTube/Video Platform

**Problem**: Design a video hosting and sharing platform

**Functional Requirements**:
- Video upload and transcoding
- Video streaming with multiple qualities
- Comments and likes
- Video recommendations
- Channel subscriptions
- Live streaming capability

**Non-Functional Requirements (NFRs)**:
- Availability: 99.95% uptime
- Latency: <2s video start time
- Throughput: 500 hours uploaded/minute
- Consistency: Eventually consistent for view counts
- Security: Copyright protection, content moderation
- Scalability: Global distribution, auto-scaling

**Scale**: 2B users, 500 hours uploaded/minute, 1B hours watched daily

**Core Entities**:
```java
class Video {
    String videoId;
    String channelId;
    String title;
    String description;
    List<String> tags;
    int duration;
    Map<Quality, String> streamUrls;
    VideoStatus status; // PROCESSING, PUBLISHED, PRIVATE
    long viewCount;
    int likeCount;
    int dislikeCount;
    long uploadTime;
}

class Channel {
    String channelId;
    String userId;
    String name;
    String description;
    int subscriberCount;
    List<String> videoIds;
    boolean verified;
    long createdAt;
}

class Comment {
    String commentId;
    String videoId;
    String userId;
    String content;
    String parentCommentId; // for replies
    int likeCount;
    long timestamp;
}

class User {
    String userId;
    String email;
    List<String> subscribedChannels;
    List<String> watchHistory;
    Map<String, Object> preferences;
}
```

**APIs**:
```java
// Video Management
POST /api/v1/videos/upload
GET /api/v1/videos/{videoId}/metadata
GET /api/v1/videos/{videoId}/stream?quality={quality}
PUT /api/v1/videos/{videoId}/metadata

// Channel Management
GET /api/v1/channels/{channelId}/videos
POST /api/v1/channels/{channelId}/subscribe
GET /api/v1/channels/{channelId}/analytics

// Interaction
POST /api/v1/videos/{videoId}/like
POST /api/v1/videos/{videoId}/comments
GET /api/v1/videos/{videoId}/comments?limit={limit}

// Recommendations
GET /api/v1/users/{userId}/recommendations
GET /api/v1/videos/trending
```

**HLD**:
```
[Upload Service] --> [Transcoding Pipeline] --> [Video Storage]
       |                      |                       |
[Metadata Service] --> [Recommendation Engine] --> [CDN]
       |                      |                       |
[User Service]         [Analytics Service]    [Streaming Service]
       |                      |                       |
[User DB]              [View Logs]            [Comment Service]
```

**Deep Dive - Video Upload & Processing**:
```
1. User uploads video to Upload Service
2. Video stored in temporary storage
3. Transcoding pipeline processes video:
   - Extract metadata (duration, resolution)
   - Generate multiple quality versions
   - Create thumbnails at different timestamps
   - Extract audio for accessibility
4. Processed videos stored in distributed storage
5. Metadata updated in database
6. CDN populated with video files
7. Video becomes available for streaming
8. Analytics pipeline starts tracking views
```

**Database Choices**:
- **Video Metadata**: MongoDB (flexible video metadata)
- **User Data**: PostgreSQL (user profiles, subscriptions)
- **View Analytics**: ClickHouse (time-series view data)
- **Comments**: Cassandra (high write volume)
- **Search Index**: Elasticsearch (video search)
- **Cache**: Redis (trending videos, recommendations)
- **Video Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Distributed video transcoding
- Multi-tier storage (hot/warm/cold)
- ML-based recommendations
- Global CDN with regional optimization
- Eventual consistency for view counts

---

## 6. Design Instagram/Photo Sharing

**Problem**: Design a photo and video sharing social platform

**Functional Requirements**:
- Photo/video upload with filters
- News feed with posts from followed users
- Stories feature (24-hour expiry)
- Direct messaging
- Explore page with recommendations
- Real-time likes and comments

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms feed loading
- Throughput: 100M photos/day, 4.2B likes daily
- Consistency: Eventual consistency for feeds
- Security: Content moderation, data privacy
- Scalability: Handle celebrity account spikes

**Scale**: 1B users, 100M photos/day, 4.2B likes daily

**Core Entities**:
```java
class User {
    String userId;
    String phoneNumber;
    String displayName;
    String bio;
    int followersCount;
    int followingCount;
    boolean verified;
    long joinDate;
}

class Post {
    String postId;
    String userId;
    String content;
    List<String> mediaUrls;
    List<String> tags;
    int likeCount;
    int commentCount;
    long timestamp;
}

class Story {
    String storyId;
    String userId;
    List<String> mediaUrls;
    long expiryTime;
}

class Comment {
    String commentId;
    String postId;
    String userId;
    String content;
    int likeCount;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/status

// Post Management
POST /api/v1/posts/create
GET /api/v1/posts/{postId}
DELETE /api/v1/posts/{postId}
POST /api/v1/posts/{postId}/like

// Story Management
POST /api/v1/stories/create
GET /api/v1/stories/{userId}/active
GET /api/v1/stories/expired

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{postId}?limit={limit}
```

**HLD**:
```
[Mobile Apps] --> [Media Upload Service] --> [Image Processing]
                           |                        |
                  [Feed Generation] --> [Content Storage/CDN]
                           |                        |
                  [User Graph Service] --> [Recommendation Engine]
                           |                        |
                  [Activity Service]       [Search Service]
                           |                        |
                  [User/Post DB]          [Analytics Pipeline]
```

**Deep Dive - Feed Generation**:
```
1. User follows another user
2. Followee's new post triggers fan-out to followers' feeds
3. Feed service adds post to each follower's feed
4. Real-time updates via WebSocket for online users
5. Offline users receive push notifications
6. User opens app → Feed API returns cached feed
7. Feed cache refreshed in the background
```

**Database Choices**:
- **User Profiles**: PostgreSQL (structured user data)
- **Post Storage**: Cassandra (time-series, write-heavy)
- **Comment Storage**: DynamoDB (high write volume, scalability)
- **Feed Cache**: Redis (fast feed retrieval)
- **Search Index**: Elasticsearch (user and post search)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Separate read/write paths
- Image optimization and CDN
- Graph database for social connections
- Cache-first architecture
- Event-driven feed updates

---

## 7. Design Slack/Team Collaboration

**Problem**: Design a team communication and collaboration platform

**Functional Requirements**:
- Real-time messaging in channels
- File sharing and integrations
- Video/voice calls
- Thread conversations
- Search across messages
- Workspace management

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <150ms message delivery
- Throughput: 1B messages/day
- Consistency: Strong consistency for direct messages
- Security: Data encryption, access controls
- Scalability: Support for large workspaces

**Scale**: 10M daily active users, 1B messages/day

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String displayName;
    String passwordHash;
    UserStatus status; // ACTIVE, INVITED, SUSPENDED
    List<String> workspaceIds;
}

class Workspace {
    String workspaceId;
    String name;
    String ownerId;
    List<String> memberIds;
    List<String> channelIds;
}

class Channel {
    String channelId;
    String workspaceId;
    String name;
    ChannelType type; // PUBLIC, PRIVATE
    List<String> memberIds;
}

class Message {
    String messageId;
    String channelId;
    String senderId;
    String content;
    long timestamp;
    MessageType type; // TEXT, FILE, LINK
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/status

// Workspace Management
POST /api/v1/workspaces/create
GET /api/v1/workspaces/{workspaceId}
POST /api/v1/workspaces/{workspaceId}/members

// Channel Management
POST /api/v1/channels/create
GET /api/v1/channels/{channelId}
POST /api/v1/channels/{channelId}/members

// Messaging
POST /api/v1/messages/send
GET /api/v1/messages/{channelId}/history
PUT /api/v1/messages/{messageId}/status
```

**HLD**:
```
[Client Apps] --> [WebSocket Gateway] --> [Message Service]
                          |                      |
                  [Channel Service] --> [Search Service]
                          |                      |
                  [User Service]         [File Service]
                          |                      |
                  [Workspace DB]         [Message DB]
                          |                      |
                  [Authentication]       [Integration Hub]
```

**Deep Dive - Real-time Messaging**:
```
1. User sends message in a channel
2. Message Service receives and stores the message
3. WebSocket Gateway pushes the message to all channel members
4. Offline members receive the message in the next sync
5. Message status updated to SENT, DELIVERED, READ
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, workspaces)
- **Workspace/Channel DB**: MongoDB (flexible schema for channels)
- **Message DB**: Cassandra (high write throughput)
- **Search Index**: Elasticsearch (message and channel search)
- **Cache**: Redis (recent messages, user sessions)
- **File Storage**: S3/GCS (shared files, attachments)

**Key Design Decisions**:
- WebSocket for real-time communication
- Message sharding by workspace
- Elasticsearch for message search
- Microservices for different features
- OAuth for third-party integrations

---

## 8. Design Zoom/Video Conferencing

**Problem**: Design a video conferencing platform

**Functional Requirements**:
- HD video/audio calls for up to 1000 participants
- Screen sharing and recording
- Chat during meetings
- Meeting scheduling and calendar integration
- Mobile and web clients
- Global low-latency infrastructure

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <150ms audio/video sync
- Throughput: 3B meeting minutes/day
- Consistency: Strong consistency for meeting state
- Security: End-to-end encryption, secure access
- Scalability: Support for large webinars

**Scale**: 300M daily meeting participants, 3B meeting minutes/day

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String displayName;
    String passwordHash;
    UserStatus status; // ACTIVE, INVITED, SUSPENDED
}

class Meeting {
    String meetingId;
    String hostId;
    List<String> participantIds;
    MeetingStatus status; // SCHEDULED, IN_PROGRESS, ENDED
    long startTime;
    long endTime;
    String topic;
}

class Recording {
    String recordingId;
    String meetingId;
    String userId;
    String fileUrl;
    long fileSize;
    String transcriptionUrl;
}

class ChatMessage {
    String messageId;
    String meetingId;
    String senderId;
    String content;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/status

// Meeting Management
POST /api/v1/meetings/create
GET /api/v1/meetings/{meetingId}
PUT /api/v1/meetings/{meetingId}/status

// Recording Management
GET /api/v1/recordings/{meetingId}
POST /api/v1/recordings/{recordingId}/transcribe

// Chat Management
POST /api/v1/chat/send
GET /api/v1/chat/{meetingId}/history
```

**HLD**:
```
[Client Apps] --> [Load Balancer] --> [Signaling Server]
                                          |
                  [Media Router] --> [Recording Service]
                          |                  |
                  [TURN/STUN Servers] --> [Storage Service]
                          |                  |
                  [Meeting Service]    [Streaming Service]
                          |                  |
                  [User DB]            [CDN Network]
```

**Deep Dive - Meeting Setup & Join**:
```
1. Host schedules a meeting → Meeting Service
2. Meeting Service creates a meeting ID, sends back to host
3. Host shares meeting ID with participants
4. Participants join using the meeting ID
5. Signaling Server authenticates and connects users
6. TURN/STUN servers assist in NAT traversal
7. Media Router establishes peer-to-peer connections
8. Recording Service starts recording if enabled
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Meeting DB**: MongoDB (flexible meeting data)
- **Recording Metadata**: DynamoDB (high write throughput)
- **Chat Messages**: Cassandra (real-time messaging)
- **Search Index**: Elasticsearch (meeting and user search)
- **Cache**: Redis (active meetings, user sessions)
- **File Storage**: S3/GCS (recordings, transcripts)

**Key Design Decisions**:
- WebRTC for peer-to-peer communication
- Media servers for large meetings
- Global edge network for low latency
- Adaptive video quality based on bandwidth
- Horizontal scaling of media servers

---

## 9. Design Airbnb/Booking Platform

**Problem**: Design a vacation rental booking platform

**Functional Requirements**:
- Property listing and search
- Booking management with calendar
- Payment processing with escrow
- Review and rating system
- Host and guest communication
- Price optimization

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms search results
- Throughput: 500M stays/year
- Consistency: Strong consistency for bookings
- Security: Data protection, secure payments
- Scalability: Handle peak travel seasons

**Scale**: 4M hosts, 150M users, 500M stays/year

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    UserType type; // GUEST, HOST
    List<String> bookingIds;
    List<String> reviewIds;
}

class Property {
    String propertyId;
    String hostId;
    String title;
    String description;
    List<String> amenities;
    double price;
    Location location;
    PropertyStatus status; // AVAILABLE, BOOKED, UNAVAILABLE
}

class Booking {
    String bookingId;
    String propertyId;
    String guestId;
    Date startDate;
    Date endDate;
    double totalPrice;
    BookingStatus status; // PENDING, CONFIRMED, CANCELLED
}

class Review {
    String reviewId;
    String userId;
    String propertyId;
    int rating;
    String comment;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/type

// Property Management
POST /api/v1/properties/create
GET /api/v1/properties/{propertyId}
GET /api/v1/properties/search?location={location}&checkin={checkin}&checkout={checkout}

// Booking Management
POST /api/v1/bookings/create
GET /api/v1/bookings/{bookingId}
PUT /api/v1/bookings/{bookingId}/status

// Review Management
POST /api/v1/reviews/create
GET /api/v1/reviews/{propertyId}?limit={limit}
```

**HLD**:
```
[Web/Mobile Apps] --> [Search Service] --> [Property Service]
                             |                    |
                     [Booking Service] --> [Payment Service]
                             |                    |
                     [Calendar Service] --> [Review Service]
                             |                    |
                     [Property DB]        [User Service]
                             |                    |
                     [Elasticsearch]      [Notification Service]
```

**Deep Dive - Property Booking**:
```
1. User searches for properties → Search Service
2. Search Service queries Elasticsearch for available properties
3. Results returned to user, showing price, availability
4. User selects property, initiates booking
5. Booking Service checks calendar, creates booking record
6. Payment Service processes payment, holds in escrow
7. Confirmation sent to user and host
8. Reminder notifications sent before check-in
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Property DB**: MongoDB (flexible property data)
- **Booking DB**: Cassandra (high write throughput)
- **Review DB**: DynamoDB (scalable review storage)
- **Search Index**: Elasticsearch (property search)
- **Cache**: Redis (recent searches, property details)
- **File Storage**: S3/GCS (property images)

**Key Design Decisions**:
- Geospatial search with filters
- Inventory management with calendar blocking
- Two-phase commit for bookings
- Elasticsearch for property search
- Event-driven architecture for notifications

---

## 10. Design Spotify/Music Streaming

**Problem**: Design a music streaming platform

**Functional Requirements**:
- Stream music to millions of users
- Playlist creation and sharing
- Music recommendation engine
- Offline download capability
- Social features (follow artists/friends)
- Podcast support

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms song start time
- Throughput: 100M premium subscribers
- Consistency: Eventual consistency for playlists
- Security: DRM protection, secure payments
- Scalability: Global distribution, auto-scaling

**Scale**: 400M users, 100M premium subscribers, 70M songs

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    List<String> playlistIds;
    List<String> followedArtistIds;
}

class Song {
    String songId;
    String title;
    String artistId;
    String albumId;
    int duration;
    String genre;
    String lyrics;
    Map<Quality, String> streamUrls;
}

class Playlist {
    String playlistId;
    String userId;
    String name;
    List<String> songIds;
    boolean collaborative;
}

class Artist {
    String artistId;
    String name;
    String bio;
    List<String> songIds;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/follow-artist

// Song Management
GET /api/v1/songs/{songId}
GET /api/v1/songs/search?query={query}

// Playlist Management
POST /api/v1/playlists/create
GET /api/v1/playlists/{playlistId}
PUT /api/v1/playlists/{playlistId}/add-song

// Recommendation
GET /api/v1/users/{userId}/recommendations
```

**HLD**:
```
[Client Apps] --> [Streaming Service] --> [Content CDN]
                          |                    |
                  [Recommendation Engine] --> [Music Metadata]
                          |                    |
                  [Playlist Service]     [User Service]
                          |                    |
                  [Music Storage]        [Social Service]
                          |                    |
                  [Analytics Pipeline]   [Search Service]
```

**Deep Dive - Music Recommendation**:
```
1. User listens to a song
2. Analytics pipeline captures listening data
3. ML model updates user preference profile
4. Recommendation Engine queries similar songs
5. Playlist Service updates user playlists
6. User notified of new song additions
```

**Database Choices**:
- **User Data**: PostgreSQL (user profiles, playlists)
- **Song Metadata**: MongoDB (flexible song metadata)
- **Playlist Data**: Cassandra (high write throughput)
- **Artist Data**: DynamoDB (scalable artist storage)
- **Recommendation Model**: Neo4j (graph-based recommendations)
- **Cache**: Redis (recently played songs, recommendations)
- **Music Storage**: S3/GCS (audio files)

**Key Design Decisions**:
- Multiple audio quality streams
- ML-based music recommendations
- Global CDN for content delivery
- Caching for popular content
- Real-time listening analytics

---

## 11. Design TikTok/Short Video Platform

**Problem**: Design a short-form video sharing platform

**Functional Requirements**:
- Video upload with effects and filters
- Personalized video feed (For You Page)
- Video recommendations using ML
- Social interactions (likes, comments, shares)
- Live streaming capability
- Content moderation

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms video start time
- Throughput: 1B videos watched daily
- Consistency: Eventual consistency for likes/comments
- Security: Content moderation, data privacy
- Scalability: Handle viral content spikes

**Scale**: 1B users, 1B videos watched daily

**Core Entities**:
```java
class User {
    String userId;
    String phoneNumber;
    String displayName;
    String bio;
    int followersCount;
    int followingCount;
    boolean verified;
    long joinDate;
}

class Video {
    String videoId;
    String userId;
    String title;
    String description;
    List<String> tags;
    int duration;
    Map<Quality, String> streamUrls;
    VideoStatus status; // PROCESSING, PUBLISHED, PRIVATE
    long viewCount;
    int likeCount;
    int commentCount;
    long uploadTime;
}

class Comment {
    String commentId;
    String videoId;
    String userId;
    String content;
    int likeCount;
    long timestamp;
}

class LiveStream {
    String streamId;
    String userId;
    String title;
    String description;
    StreamStatus status; // LIVE, ENDED, RECORDED
    long startTime;
    long endTime;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/status

// Video Management
POST /api/v1/videos/upload
GET /api/v1/videos/{videoId}/metadata
GET /api/v1/videos/{videoId}/stream?quality={quality}

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{videoId}?limit={limit}

// Live Stream Management
POST /api/v1/streams/create
GET /api/v1/streams/{streamId}
PUT /api/v1/streams/{streamId}/status
```

**HLD**:
```
[Mobile Apps] --> [Video Upload Service] --> [Video Processing]
                          |                        |
                  [Feed Generation] --> [Recommendation Engine]
                          |                        |
                  [User Service]         [Content Moderation]
                          |                        |
                  [Video Storage/CDN]    [Analytics Service]
                          |                        |
                  [Social Graph]         [ML Pipeline]
```

**Deep Dive - Video Processing & Recommendation**:
```
1. User uploads video
2. Video Processing extracts metadata, generates thumbnails
3. Video stored in multiple qualities
4. ML model analyzes video for content understanding
5. User watches video → Analytics captures engagement data
6. ML model updates user preferences
7. Recommendation Engine updates For You Page
```

**Database Choices**:
- **User Profiles**: PostgreSQL (user data, preferences)
- **Video Metadata**: MongoDB (flexible video metadata)
- **Comment Data**: Cassandra (high write volume)
- **Live Stream Data**: DynamoDB (scalable stream data)
- **Recommendation Model**: Neo4j (graph-based recommendations)
- **Cache**: Redis (recently watched videos, recommendations)
- **Video Storage**: S3/GCS (video files)

**Key Design Decisions**:
- AI-driven content recommendations
- Real-time video processing
- Global CDN with edge caching
- Graph-based user interactions
- Automated content moderation

---

## 12. Design LinkedIn/Professional Network

**Problem**: Design a professional networking platform

**Functional Requirements**:
- User profiles and connections
- Job postings and applications
- Professional content feed
- Messaging and networking
- Company pages
- Skill endorsements and recommendations

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms profile loading
- Throughput: 800M users, 50M job postings
- Consistency: Strong consistency for connections
- Security: Data privacy, secure messaging
- Scalability: Handle peak usage during job hunts

**Scale**: 800M users, 50M companies, 20M job postings

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    String headline;
    String location;
    List<String> skills;
    List<String> connectionIds;
    List<String> jobIds;
}

class Job {
    String jobId;
    String title;
    String description;
    String companyId;
    String location;
    String employmentType; // FULL_TIME, PART_TIME, CONTRACT
    double salary;
    List<String> requiredSkills;
}

class Company {
    String companyId;
    String name;
    String industry;
    String location;
    List<String> employeeIds;
    List<String> jobIds;
}

class Connection {
    String userId1;
    String userId2;
    ConnectionStatus status; // PENDING, ACCEPTED, BLOCKED
    long connectedAt;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/connections

// Job Management
POST /api/v1/jobs/create
GET /api/v1/jobs/{jobId}
GET /api/v1/jobs/search?query={query}

// Company Management
GET /api/v1/companies/{companyId}
GET /api/v1/companies/search?query={query}

// Connection Management
POST /api/v1/connections/request
POST /api/v1/connections/accept
POST /api/v1/connections/block
```

**HLD**:
```
[Web/Mobile Apps] --> [User Service] --> [Profile Service]
                             |                    |
                     [Job Service] --> [Recommendation Engine]
                             |                    |
                     [Company Service]         [Search Service]
                             |                    |
                     [Message Service]      [Analytics Pipeline]
                             |                    |
                     [User DB]              [Job DB]
```

**Deep Dive - Connection Management**:
```
1. User A sends connection request to User B
2. Connection Service creates a pending connection record
3. User B receives notification of new connection request
4. User B accepts the request
5. Connection Service updates status to ACCEPTED
6. Both users can now message and see each other's profiles
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Job DB**: MongoDB (flexible job data)
- **Company DB**: DynamoDB (scalable company data)
- **Connection Data**: Neo4j (graph-based connections)
- **Search Index**: Elasticsearch (job and user search)
- **Cache**: Redis (recently viewed profiles, job recommendations)
- **Message Storage**: Cassandra (high write throughput)

**Key Design Decisions**:
- Graph database for professional networks
- ML for job and connection recommendations
- Content ranking algorithms
- Professional search with filters
- Privacy controls for professional data

---

## 13. Design Dropbox/Cloud Storage

**Problem**: Design a cloud file storage and synchronization service

**Functional Requirements**:
- File upload, download, and synchronization
- File sharing with permissions
- Version control and file history
- Real-time collaboration
- Cross-platform support
- File search and organization

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms file access
- Throughput: 500M files uploaded daily
- Consistency: Strong consistency for file updates
- Security: Data encryption, access controls
- Scalability: Handle petabytes of data

**Scale**: 700M users, 500M files uploaded daily

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> fileIds;
}

class File {
    String fileId;
    String userId;
    String fileName;
    String fileType;
    long fileSize;
    String checksum;
    FileStatus status; // UPLOADED, SHARED, DELETED
    long createdAt;
    long updatedAt;
}

class Folder {
    String folderId;
    String userId;
    String name;
    List<String> fileIds;
    List<String> subFolderIds;
}

class Share {
    String shareId;
    String fileId;
    String userId;
    ShareStatus status; // PENDING, ACCEPTED, REJECTED
    long sharedAt;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// File Management
POST /api/v1/files/upload
GET /api/v1/files/{fileId}/metadata
GET /api/v1/files/{fileId}/download

// Folder Management
POST /api/v1/folders/create
GET /api/v1/folders/{folderId}
PUT /api/v1/folders/{folderId}/add-file

// Sharing Management
POST /api/v1/shares/create
GET /api/v1/shares/{fileId}/status
```

**HLD**:
```
[Client Apps] --> [File Upload Service] --> [Metadata Service]
                          |                       |
                  [Sync Service] --> [Block Storage]
                          |                       |
                  [Version Control] --> [File Processing]
                          |                       |
                  [Sharing Service]      [Search Service]
                          |                       |
                  [User DB]              [CDN Network]
```

**Deep Dive - File Upload & Sync**:
```
1. User uploads file → File Upload Service
2. File divided into chunks, each chunk uploaded separately
3. Metadata Service stores file info, user info, permissions
4. Block Storage stores file chunks, ensures durability
5. Version Control creates a new version entry
6. Sync Service updates all user devices with new file info
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **File Metadata**: MongoDB (flexible file metadata)
- **Version History**: Cassandra (high write throughput)
- **Share Data**: DynamoDB (scalable share data)
- **Search Index**: Elasticsearch (file and folder search)
- **Cache**: Redis (recently accessed files, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- File chunking and deduplication
- Delta sync for efficiency
- Distributed block storage
- Conflict resolution for concurrent edits
- Client-side caching

---

## 14. Design Google Drive/Document Collaboration

**Problem**: Design a collaborative document editing platform

**Functional Requirements**:
- Real-time collaborative editing
- Document version history
- Comments and suggestions
- File sharing and permissions
- Multiple document formats
- Offline editing capability

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms editing response
- Throughput: 2B documents created monthly
- Consistency: Strong consistency for document edits
- Security: Data encryption, access controls
- Scalability: Support for large documents and teams

**Scale**: 1B users, 2B documents created monthly

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> documentIds;
}

class Document {
    String documentId;
    String ownerId;
    String title;
    String content;
    List<String> collaboratorIds;
    DocumentStatus status; // DRAFT, PUBLISHED, ARCHIVED
    long createdAt;
    long updatedAt;
}

class Comment {
    String commentId;
    String documentId;
    String userId;
    String content;
    long timestamp;
}

class Permission {
    String documentId;
    String userId;
    AccessLevel accessLevel; // VIEW, COMMENT, EDIT
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Document Management
POST /api/v1/documents/create
GET /api/v1/documents/{documentId}
PUT /api/v1/documents/{documentId}/edit

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{documentId}?limit={limit}

// Permission Management
POST /api/v1/permissions/grant
POST /api/v1/permissions/revoke
```

**HLD**:
```
[Client Apps] --> [Collaboration Service] --> [Operational Transform]
                          |                        |
                  [Document Service] --> [Version Control]
                          |                        |
                  [Permission Service] --> [Storage Service]
                          |                        |
                  [User Service]           [Sync Service]
                          |                        |
                  [User DB]                [Document Storage]
```

**Deep Dive - Real-time Collaborative Editing**:
```
1. User opens document → Document Service
2. Document Service loads document and version history
3. Operational Transform synchronizes edits in real-time
4. Version Control creates a new version on each save
5. Permission Service checks access rights for each user
6. Document changes propagated to all collaborators
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Document DB**: MongoDB (flexible document schema)
- **Version Control**: Cassandra (high write throughput)
- **Comment Data**: DynamoDB (scalable comment storage)
- **Permission Data**: Neo4j (graph-based permission management)
- **Cache**: Redis (recently accessed documents, metadata)
- **Document Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Operational Transform for real-time editing
- Conflict-free replicated data types (CRDTs)
- Fine-grained permission system
- Document format conversion
- WebSocket for real-time updates

---

## 15. Design Discord/Gaming Communication

**Problem**: Design a communication platform for gaming communities

**Functional Requirements**:
- Voice and text chat in servers
- Low-latency voice communication
- Screen sharing and video
- Bot integrations
- Server management with roles
- Rich media sharing

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms voice chat
- Throughput: 19B messages/month
- Consistency: Strong consistency for direct messages
- Security: Data encryption, access controls
- Scalability: Support for large servers

**Scale**: 150M monthly active users, 19B messages/month

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    UserStatus status; // ONLINE, OFFLINE, AWAY
    long lastSeen;
}

class Server {
    String serverId;
    String ownerId;
    String name;
    List<String> memberIds;
    List<String> channelIds;
}

class Channel {
    String channelId;
    String serverId;
    String name;
    ChannelType type; // TEXT, VOICE
    List<String> memberIds;
}

class Message {
    String messageId;
    String channelId;
    String senderId;
    String content;
    long timestamp;
    MessageType type; // TEXT, FILE, LINK
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile
PUT /api/v1/users/{userId}/status

// Server Management
POST /api/v1/servers/create
GET /api/v1/servers/{serverId}
POST /api/v1/servers/{serverId}/members

// Channel Management
POST /api/v1/channels/create
GET /api/v1/channels/{channelId}
POST /api/v1/channels/{channelId}/members

// Messaging
POST /api/v1/messages/send
GET /api/v1/messages/{channelId}/history
PUT /api/v1/messages/{messageId}/status
```

**HLD**:
```
[Client Apps] --> [Gateway Service] --> [Voice Service]
                          |                   |
                  [Text Service] --> [Media Service]
                          |                   |
                  [Server Service]    [Bot Platform]
                          |                   |
                  [User Service]      [Permission Service]
                          |                   |
                  [Message DB]        [Voice Infrastructure]
```

**Deep Dive - Voice Communication**:
```
1. User joins voice channel
2. Gateway Service authenticates and authorizes user
3. TURN/STUN servers assist in NAT traversal
4. Media Service establishes peer-to-peer connections
5. Voice data sent over WebRTC
6. Recording Service captures and stores audio
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Server/Channel DB**: MongoDB (flexible server/channel data)
- **Message DB**: Cassandra (high write throughput)
- **Voice Metadata**: DynamoDB (scalable voice metadata)
- **Search Index**: Elasticsearch (user and server search)
- **Cache**: Redis (recently accessed channels, users)
- **Media Storage**: S3/GCS (audio files)

**Key Design Decisions**:
- WebRTC for voice communication
- Real-time text messaging
- Hierarchical server structure
- Bot API ecosystem
- Global voice server distribution

---

## 16. Design Reddit/Discussion Platform

**Problem**: Design a social news aggregation and discussion platform

**Functional Requirements**:
- Subreddit creation and management
- Post submission and voting
- Comment threading
- Content moderation
- User karma system
- Real-time discussions

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms post/comment loading
- Throughput: 430M monthly active users, 130K active communities
- Consistency: Eventual consistency for votes
- Security: Content moderation, spam detection
- Scalability: Handle viral content spikes

**Scale**: 430M monthly active users, 130K active communities

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    int karmaPoints;
    List<String> subscribedSubreddits;
}

class Subreddit {
    String subredditId;
    String name;
    String description;
    String ownerId;
    List<String> moderatorIds;
    List<String> postIds;
}

class Post {
    String postId;
    String subredditId;
    String userId;
    String title;
    String content;
    List<String> tags;
    int upvoteCount;
    int downvoteCount;
    long timestamp;
}

class Comment {
    String commentId;
    String postId;
    String userId;
    String content;
    int upvoteCount;
    int downvoteCount;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Subreddit Management
POST /api/v1/subreddits/create
GET /api/v1/subreddits/{subredditId}
POST /api/v1/subreddits/{subredditId}/moderators

// Post Management
POST /api/v1/posts/create
GET /api/v1/posts/{postId}
POST /api/v1/posts/{postId}/vote

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{postId}?limit={limit}
```

**HLD**:
```
[Web/Mobile Apps] --> [Post Service] --> [Voting Service]
                           |                |
                   [Comment Service] --> [Ranking Algorithm]
                           |                |
                   [Subreddit Service] --> [Moderation Service]
                           |                |
                   [User Service]     [Search Service]
                           |                |
                   [Content DB]       [Analytics Pipeline]
```

**Deep Dive - Post Submission & Voting**:
```
1. User submits a post to a subreddit
2. Post Service validates and stores the post
3. Moderation queue updated for review
4. Users can upvote/downvote the post
5. Voting Service updates vote counts
6. Ranking Algorithm recalculates post ranking
7. Moderators review posts in the queue
8. Approved posts are published, notifications sent
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Subreddit DB**: MongoDB (flexible subreddit data)
- **Post DB**: Cassandra (time-series, write-heavy)
- **Comment DB**: DynamoDB (scalable comment storage)
- **Vote Data**: Redis (real-time vote counting)
- **Search Index**: Elasticsearch (post and user search)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Hierarchical comment structure
- Real-time vote aggregation
- Content ranking algorithms
- Distributed moderation tools
- Caching for popular content

---

## 17. Design Pinterest/Visual Discovery

**Problem**: Design a visual discovery and idea platform

**Functional Requirements**:
- Image upload and pin creation
- Board organization
- Visual search capability
- Personalized recommendations
- Shopping integration
- Rich Pin metadata

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms pin loading
- Throughput: 400M monthly active users, 240B pins
- Consistency: Eventual consistency for pin updates
- Security: Data protection, secure payments
- Scalability: Handle peak usage during holidays

**Scale**: 400M monthly active users, 240B pins

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> boardIds;
    List<String> savedSearches;
}

class Pin {
    String pinId;
    String userId;
    String title;
    String description;
    List<String> imageUrls;
    List<String> tags;
    int likeCount;
    int commentCount;
    long timestamp;
}

class Board {
    String boardId;
    String userId;
    String name;
    String description;
    List<String> pinIds;
}

class Comment {
    String commentId;
    String pinId;
    String userId;
    String content;
    int likeCount;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Pin Management
POST /api/v1/pins/create
GET /api/v1/pins/{pinId}
GET /api/v1/pins/search?query={query}

// Board Management
POST /api/v1/boards/create
GET /api/v1/boards/{boardId}
PUT /api/v1/boards/{boardId}/add-pin

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{pinId}?limit={limit}
```

**HLD**:
```
[Client Apps] --> [Pin Service] --> [Image Processing]
                         |                |
                 [Board Service] --> [Visual Search]
                         |                |
                 [Recommendation Engine] --> [Shopping Service]
                         |                |
                 [User Service]      [Content DB]
                         |                |
                 [Analytics Pipeline] --> [ML Models]
```

**Deep Dive - Visual Search & Recommendations**:
```
1. User uploads an image for search
2. Image Processing extracts features, generates descriptor
3. Visual Search queries similar pins using descriptor
4. Recommendation Engine suggests related pins
5. User interacts with pins → Analytics captures engagement
6. ML Models update user preferences and improve search
```

**Database Choices**:
- **User Profiles**: PostgreSQL (user data, preferences)
- **Pin Metadata**: MongoDB (flexible pin metadata)
- **Board Data**: Cassandra (high write throughput)
- **Comment Data**: DynamoDB (scalable comment storage)
- **Visual Search Index**: Elasticsearch (image search)
- **Cache**: Redis (recently viewed pins, recommendations)
- **Image Storage**: S3/GCS (image files)

**Key Design Decisions**:
- Computer vision for image analysis
- Graph-based recommendations
- Visual similarity search
- E-commerce integration
- Image optimization and CDN

---

## 18. Design Twitch/Live Streaming

**Problem**: Design a live video streaming platform for gaming

**Functional Requirements**:
- Live video streaming with low latency
- Real-time chat during streams
- Stream recording and highlights
- Subscription and monetization
- Stream discovery and recommendations
- Mobile streaming support

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms video/audio sync
- Throughput: 30M daily active users, 9M streamers monthly
- Consistency: Strong consistency for subscription status
- Security: DRM protection, secure payments
- Scalability: Global distribution, auto-scaling

**Scale**: 30M daily active users, 9M streamers monthly

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> subscribedChannelIds;
}

class Stream {
    String streamId;
    String userId;
    String title;
    String description;
    StreamStatus status; // LIVE, ENDED, RECORDED
    long startTime;
    long endTime;
}

class Clip {
    String clipId;
    String streamId;
    String userId;
    String title;
    String description;
    long startTime;
    long endTime;
}

class Subscription {
    String subscriptionId;
    String userId;
    String channelId;
    SubscriptionStatus status; // ACTIVE, CANCELLED
    long startTime;
    long endTime;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Stream Management
POST /api/v1/streams/create
GET /api/v1/streams/{streamId}
PUT /api/v1/streams/{streamId}/status

// Clip Management
POST /api/v1/clips/create
GET /api/v1/clips/{clipId}

// Subscription Management
POST /api/v1/subscriptions/create
GET /api/v1/subscriptions/{userId}/active
```

**HLD**:
```
[Streaming Apps] --> [Ingest Service] --> [Transcoding Pipeline]
                            |                     |
                    [Chat Service] --> [CDN Network]
                            |                     |
                    [Stream Service] --> [Storage Service]
                            |                     |
                    [User Service]       [Analytics Service]
                            |                     |
                    [User DB]            [Recommendation Engine]
```

**Deep Dive - Live Streaming & Chat**:
```
1. Streamer starts a live stream
2. Ingest Service authenticates and authorizes streamer
3. Transcoding Pipeline processes and optimizes video
4. Stream is published to CDN for global distribution
5. Chat Service establishes WebSocket connection for real-time chat
6. Viewers join the stream, receive video and chat updates
7. Analytics Service tracks viewer engagement, stream quality
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Stream Metadata**: MongoDB (flexible stream data)
- **Clip Data**: Cassandra (high write throughput)
- **Subscription Data**: DynamoDB (scalable subscription data)
- **Analytics**: ClickHouse (real-time analytics)
- **Cache**: Redis (active streams, user sessions)
- **Media Storage**: S3/GCS (video files)

**Key Design Decisions**:
- Low-latency video streaming
- Real-time chat scaling
- Global CDN distribution
- Stream quality adaptation
- Monetization and subscription system

---

## 19. Design Medium/Publishing Platform

**Problem**: Design a content publishing and reading platform

**Functional Requirements**:
- Article writing with rich text editor
- Publication and distribution
- Reading recommendations
- Comment and clap system
- Subscription model
- Content moderation

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms article loading
- Throughput: 100M monthly readers, 4M writers
- Consistency: Eventual consistency for clap counts
- Security: Data protection, secure payments
- Scalability: Handle peak usage during breaking news

**Scale**: 100M monthly readers, 4M writers

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> articleIds;
    List<String> subscriptionIds;
}

class Article {
    String articleId;
    String title;
    String content;
    String authorId;
    List<String> tagIds;
    int clapCount;
    long publishTime;
    ArticleStatus status; // DRAFT, PUBLISHED, ARCHIVED
}

class Comment {
    String commentId;
    String articleId;
    String userId;
    String content;
    int likeCount;
    long timestamp;
}

class Tag {
    String tagId;
    String name;
    List<String> articleIds;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Article Management
POST /api/v1/articles/create
GET /api/v1/articles/{articleId}
GET /api/v1/articles/search?query={query}

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{articleId}?limit={limit}

// Tag Management
GET /api/v1/tags/{tagId}
GET /api/v1/tags/popular
```

**HLD**:
```
[Web Apps] --> [Publishing Service] --> [Content Service]
                       |                       |
               [Reading Service] --> [Recommendation Engine]
                       |                       |
               [User Service]          [Search Service]
                       |                       |
               [Content DB]            [Analytics Pipeline]
                       |                       |
               [CDN]                   [Moderation Service]
```

**Deep Dive - Article Publishing & Clapping**:
```
1. User writes article in rich text editor
2. Article saved as draft, versioned in database
3. User previews article, makes edits
4. Article submitted for publication
5. Moderation service reviews, approves, or requests changes
6. On approval, article status set to PUBLISHED
7. Article distributed via CDN, becomes publicly accessible
8. Readers can clap for the article, increasing clap count
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Article DB**: MongoDB (flexible article schema)
- **Comment DB**: DynamoDB (scalable comment storage)
- **Tag Data**: Neo4j (graph-based tag-article relationships)
- **Search Index**: Elasticsearch (article and user search)
- **Cache**: Redis (recently viewed articles, metadata)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Rich text editor with collaborative features
- Content recommendation algorithms
- Reading analytics and personalization
- Subscription and paywall management
- SEO optimization for articles

---

## 20. Design Clubhouse/Audio Social

**Problem**: Design an audio-based social networking platform

**Functional Requirements**:
- Live audio rooms with multiple speakers
- Real-time audience participation
- Room discovery and recommendations
- User profiles and following
- Moderation tools
- Recording and playback

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms audio sync
- Throughput: 10M weekly active users, 300K rooms created daily
- Consistency: Strong consistency for room state
- Security: Data encryption, secure access
- Scalability: Support for large rooms and audiences

**Scale**: 10M weekly active users, 300K rooms created daily

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> roomIds;
    List<String> followingUserIds;
}

class Room {
    String roomId;
    String creatorId;
    String title;
    String description;
    List<String> speakerIds;
    List<String> listenerIds;
    RoomStatus status; // SCHEDULED, LIVE, ENDED
    long startTime;
    long endTime;
}

class Recording {
    String recordingId;
    String roomId;
    String userId;
    String fileUrl;
    long fileSize;
}

class Follow {
    String followerId;
    String followeeId;
    long followDate;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Room Management
POST /api/v1/rooms/create
GET /api/v1/rooms/{roomId}
PUT /api/v1/rooms/{roomId}/status

// Recording Management
GET /api/v1/recordings/{roomId}
POST /api/v1/recordings/{recordingId}/transcribe

// Follow Management
POST /api/v1/follows/create
DELETE /api/v1/follows/{followeeId}
```

**HLD**:
```
[Mobile Apps] --> [Audio Gateway] --> [Room Service]
                         |                  |
                 [Audio Processing] --> [User Service]
                         |                  |
                 [Broadcasting Service] --> [Discovery Service]
                         |                  |
                 [Recording Service]    [Moderation Service]
                         |                  |
                 [Audio Storage]        [Analytics Pipeline]
```

**Deep Dive - Live Audio Room**:
```
1. User creates a room, invites speakers
2. Room Service authenticates and authorizes users
3. Audio Processing sets up WebRTC connections
4. Broadcasting Service streams audio to all listeners
5. Moderation Service manages speaker permissions
6. Recording Service captures audio if enabled
7. Analytics tracks listener engagement, audio quality
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Room Metadata**: MongoDB (flexible room data)
- **Recording Metadata**: DynamoDB (scalable recording data)
- **Follow Data**: Neo4j (graph-based follow relationships)
- **Analytics**: ClickHouse (real-time analytics)
- **Cache**: Redis (active rooms, user sessions)
- **Media Storage**: S3/GCS (audio files)

**Key Design Decisions**:
- Real-time audio streaming
- Room state management
- Audio quality optimization
- Scalable broadcasting infrastructure
- Social graph for recommendations

---

## 21. Design Quora/Q&A Platform

**Problem**: Design a question and answer knowledge-sharing platform

**Functional Requirements**:
- Question posting and answering
- Answer ranking and voting
- Expert identification
- Topic organization
- Search functionality
- Content moderation

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms question/answer loading
- Throughput: 300M monthly users, 1M questions asked monthly
- Consistency: Strong consistency for votes
- Security: Data protection, content moderation
- Scalability: Handle peak usage during trending topics

**Scale**: 300M monthly users, 1M questions asked monthly

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    int reputationPoints;
    List<String> questionIds;
    List<String> answerIds;
}

class Question {
    String questionId;
    String title;
    String content;
    String authorId;
    List<String> tagIds;
    int viewCount;
    int upvoteCount;
    int downvoteCount;
    long timestamp;
}

class Answer {
    String answerId;
    String questionId;
    String userId;
    String content;
    int upvoteCount;
    int downvoteCount;
    long timestamp;
}

class Tag {
    String tagId;
    String name;
    List<String> questionIds;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Question Management
POST /api/v1/questions/ask
GET /api/v1/questions/{questionId}
GET /api/v1/questions/search?query={query}

// Answer Management
POST /api/v1/answers/create
GET /api/v1/answers/{questionId}?limit={limit}

// Tag Management
GET /api/v1/tags/{tagId}
GET /api/v1/tags/popular
```

**HLD**:
```
[Web Apps] --> [Question Service] --> [Answer Service]
                      |                     |
              [Ranking Service] --> [Search Service]
                      |                     |
              [User Service]        [Tag Service]
                      |                     |
              [Content DB]          [Moderation Service]
                      |                     |
              [Analytics Pipeline] --> [ML Models]
```

**Deep Dive - Question Posting & Answer Ranking**:
```
1. User posts a question
2. Question Service validates and stores the question
3. Tags are extracted and stored
4. User is notified when someone answers
5. Answer ranking algorithm updates answer order
6. Top answers are featured, based on votes and recency
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Question DB**: MongoDB (flexible question schema)
- **Answer DB**: Cassandra (high write throughput)
- **Tag Data**: Neo4j (graph-based tag-question relationships)
- **Search Index**: Elasticsearch (question and user search)
- **Cache**: Redis (recently viewed questions, metadata)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Answer quality ranking algorithms
- Expert identification system
- Topic clustering and organization
- Search with semantic understanding
- Community moderation tools

---

## 22. Design Hacker News/Tech News

**Problem**: Design a technology news aggregation platform

**Functional Requirements**:
- Story submission and voting
- Comment threading
- User karma system
- Real-time ranking
- Simple, fast interface
- Community moderation

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms story loading
- Throughput: 5M monthly users, 500K daily page views
- Consistency: Eventual consistency for votes
- Security: Data protection, content moderation
- Scalability: Handle peak usage during breaking news

**Scale**: 5M monthly users, 500K daily page views

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    int karmaPoints;
    List<String> submittedStoryIds;
}

class Story {
    String storyId;
    String title;
    String url;
    String authorId;
    int upvoteCount;
    int downvoteCount;
    long timestamp;
}

class Comment {
    String commentId;
    String storyId;
    String userId;
    String content;
    int likeCount;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Story Management
POST /api/v1/stories/submit
GET /api/v1/stories/{storyId}
GET /api/v1/stories/popular

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{storyId}?limit={limit}
```

**HLD**:
```
[Web Interface] --> [Story Service] --> [Voting Service]
                           |                   |
                   [Comment Service] --> [Ranking Algorithm]
                           |                   |
                   [User Service]        [Moderation Service]
                           |                   |
                   [Content DB]          [Cache Layer]
```

**Deep Dive - Story Submission & Voting**:
```
1. User submits a story with a title and URL
2. Story Service validates and stores the story
3. User is notified when the story is upvoted/downvoted
4. Voting Service updates the karma points for the user
5. Ranking Algorithm recalculates story ranking
6. Moderation queue is updated for new submissions
7. Approved stories are published, notifications sent
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Story DB**: MongoDB (flexible story schema)
- **Comment DB**: DynamoDB (scalable comment storage)
- **Vote Data**: Redis (real-time vote counting)
- **Search Index**: Elasticsearch (story and user search)
- **Cache**: Redis (recently viewed stories, metadata)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Time-decay ranking algorithm
- Hierarchical comment structure
- Karma-based user reputation
- Simple, minimalist interface
- Community-driven moderation

---

## 23. Design Pastebin/Code Sharing

**Problem**: Design a platform for sharing code snippets and text

**Functional Requirements**:
- Text/code snippet storage
- Syntax highlighting
- Expiration dates
- Privacy controls
- Simple sharing URLs
- API access

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms snippet retrieval
- Throughput: 1M pastes created daily, 100M monthly views
- Consistency: Strong consistency for snippet updates
- Security: Data encryption, access controls
- Scalability: Handle peak usage during exams/contests

**Scale**: 1M pastes created daily, 100M monthly views

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
}

class Paste {
    String pasteId;
    String userId;
    String content;
    String syntaxHighlighting;
    long expirationDate;
    PasteStatus status; // ACTIVE, EXPIRED, DELETED
    long createdAt;
}

class AccessControl {
    String pasteId;
    String userId;
    AccessLevel accessLevel; // VIEW, EDIT, DELETE
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Paste Management
POST /api/v1/pastes/create
GET /api/v1/pastes/{pasteId}
DELETE /api/v1/pastes/{pasteId}

// Access Control
POST /api/v1/access/grant
DELETE /api/v1/access/revoke
```

**HLD**:
```
[Web Interface] --> [Paste Service] --> [Storage Service]
                           |                   |
                   [URL Generator] --> [Syntax Highlighter]
                           |                   |
                   [Cache Layer]       [Cleanup Service]
                           |                   |
                   [Database]          [CDN]
```

**Deep Dive - Paste Creation & Access Control**:
```
1. User creates a paste with syntax highlighting
2. Paste Service validates and stores the paste
3. URL Generator creates a unique URL for the paste
4. Access Control checks if the user can view/edit/delete
5. Syntax Highlighter applies language-specific formatting
6. Cleanup Service deletes expired pastes based on policy
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Paste DB**: MongoDB (flexible paste schema)
- **Access Control**: Redis (real-time access checks)
- **Search Index**: Elasticsearch (paste search)
- **Cache**: Redis (recently accessed pastes, metadata)
- **File Storage**: S3/GCS (large pastes, backups)

**Key Design Decisions**:
- Simple URL generation
- Efficient text storage
- Automatic cleanup of expired pastes
- CDN for fast global access
- API for programmatic access

---

## 24. Design GitHub/Code Repository

**Problem**: Design a distributed version control hosting platform

**Functional Requirements**:
- Git repository hosting
- Code collaboration features
- Issue and project management
- Pull request workflow
- CI/CD integration
- Code search

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms repo access
- Throughput: 73M developers, 200M repositories
- Consistency: Strong consistency for code changes
- Security: Data encryption, access controls
- Scalability: Handle peak usage during releases

**Scale**: 73M developers, 200M repositories

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> repoIds;
}

class Repository {
    String repoId;
    String ownerId;
    String name;
    String description;
    RepoVisibility visibility; // PUBLIC, PRIVATE
    List<String> collaboratorIds;
}

class Commit {
    String commitId;
    String repoId;
    String authorId;
    String message;
    List<String> changedFileIds;
    long timestamp;
}

class PullRequest {
    String prId;
    String repoId;
    String sourceBranch;
    String targetBranch;
    String authorId;
    PullRequestStatus status; // OPEN, CLOSED, MERGED
    long createdAt;
    long updatedAt;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Repository Management
POST /api/v1/repos/create
GET /api/v1/repos/{repoId}
PUT /api/v1/repos/{repoId}/collaborators

// Commit Management
POST /api/v1/commits/create
GET /api/v1/commits/{repoId}/history

// Pull Request Management
POST /api/v1/pulls/create
GET /api/v1/pulls/{prId}
PUT /api/v1/pulls/{prId}/status
```

**HLD**:
```
[Git Clients] --> [Git Service] --> [Repository Storage]
                        |                   |
                [Collaboration Service] --> [Search Service]
                        |                   |
                [Issue Service]       [CI/CD Pipeline]
                        |                   |
                [User Service]        [Analytics Service]
                        |                   |
                [Database]            [File Storage]
```

**Deep Dive - Code Collaboration & CI/CD**:
```
1. Developer pushes code to repository
2. Git Service authenticates and authorizes user
3. Code is stored in Repository Storage
4. CI/CD Pipeline is triggered for build/test
5. Collaboration Service manages pull requests and code reviews
6. Notifications are sent for code reviews, build status
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Repository DB**: MongoDB (flexible repo metadata)
- **Commit Data**: Cassandra (high write throughput)
- **Pull Request Data**: DynamoDB (scalable PR data)
- **Search Index**: Elasticsearch (code and issue search)
- **Cache**: Redis (recently accessed repos, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Distributed Git architecture
- Code review and collaboration tools
- Scalable repository storage
- Code search indexing
- Integration ecosystem

---

## 25. Design Jira/Project Management

**Problem**: Design a project management and issue tracking platform

**Functional Requirements**:
- Issue creation and tracking
- Project and sprint management
- Workflow customization
- Reporting and analytics
- Team collaboration
- Integration capabilities

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms issue loading
- Throughput: 10M users, 100M issues created annually
- Consistency: Strong consistency for issue updates
- Security: Data encryption, access controls
- Scalability: Handle peak usage during releases

**Scale**: 10M users, 100M issues created annually

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> projectIds;
}

class Project {
    String projectId;
    String name;
    String description;
    String ownerId;
    List<String> memberIds;
    List<String> issueIds;
}

class Issue {
    String issueId;
    String projectId;
    String reporterId;
    String assigneeId;
    String title;
    String description;
    IssueStatus status; // OPEN, IN_PROGRESS, CLOSED
    int priority;
    long createdAt;
    long updatedAt;
}

class Sprint {
    String sprintId;
    String projectId;
    String name;
    long startDate;
    long endDate;
    List<String> issueIds;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Project Management
POST /api/v1/projects/create
GET /api/v1/projects/{projectId}
PUT /api/v1/projects/{projectId}/members

// Issue Management
POST /api/v1/issues/create
GET /api/v1/issues/{issueId}
PUT /api/v1/issues/{issueId}/status

// Sprint Management
POST /api/v1/sprints/create
GET /api/v1/sprints/{sprintId}
PUT /api/v1/sprints/{sprintId}/status
```

**HLD**:
```
[Web Apps] --> [Issue Service] --> [Workflow Engine]
                      |                   |
              [Project Service] --> [Notification Service]
                      |                   |
              [User Service]        [Reporting Service]
                      |                   |
              [Database]            [Integration Hub]
                      |                   |
              [Search Service]      [Analytics Pipeline]
```

**Deep Dive - Issue Tracking & Reporting**:
```
1. User creates a new issue
2. Issue Service validates and stores the issue
3. Workflow Engine assigns issue to the appropriate user
4. Notifications sent for new assignments, comments
5. Reporting Service generates reports based on issue data
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Project DB**: MongoDB (flexible project data)
- **Issue DB**: Cassandra (high write throughput)
- **Sprint Data**: DynamoDB (scalable sprint data)
- **Search Index**: Elasticsearch (issue and project search)
- **Cache**: Redis (recently viewed issues, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Flexible workflow management
- Custom field system
- Real-time collaboration features
- Comprehensive reporting
- Extensive integration support

---

## 26. Design Stripe/Payment Processing

**Problem**: Design a payment processing platform

**Functional Requirements**:
- Payment transaction processing
- Multi-currency support
- Fraud detection
- Subscription billing
- API for developers
- Compliance and security

**Non-Functional Requirements (NFRs)**:
- Availability: 99.99% uptime
- Latency: <200ms transaction processing
- Throughput: Millions of businesses, billions in transactions
- Consistency: Strong consistency for financial data
- Security: PCI DSS compliance, data encryption
- Scalability: Handle peak usage during sales/events

**Scale**: Millions of businesses, billions in transactions

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> paymentMethodIds;
}

class PaymentMethod {
    String paymentMethodId;
    String userId;
    PaymentType type; // CARD, BANK_ACCOUNT, PAYPAL
    String provider;
    String accountNumber;
    String routingNumber;
}

class Transaction {
    String transactionId;
    String userId;
    String paymentMethodId;
    double amount;
    Currency currency;
    TransactionStatus status; // PENDING, COMPLETED, FAILED
    long timestamp;
}

class Subscription {
    String subscriptionId;
    String userId;
    String planId;
    SubscriptionStatus status; // ACTIVE, CANCELED, EXPIRED
    long startDate;
    long endDate;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Payment Method Management
POST /api/v1/payment-methods/add
GET /api/v1/payment-methods/{userId}

// Transaction Management
POST /api/v1/transactions/create
GET /api/v1/transactions/{transactionId}

// Subscription Management
POST /api/v1/subscriptions/create
GET /api/v1/subscriptions/{userId}/active
```

**HLD**:
```
[API Clients] --> [Payment Gateway] --> [Payment Processor]
                         |                      |
                 [Fraud Detection] --> [Risk Engine]
                         |                      |
                 [Billing Service]      [Compliance Service]
                         |                      |
                 [Database]             [Audit Logs]
                         |                      |
                 [Analytics]            [Security Layer]
```

**Deep Dive - Payment Processing Workflow**:
```
1. User initiates a payment → Payment Gateway
2. Payment Gateway authenticates and authorizes user
3. Payment Processor communicates with the bank/payment provider
4. Fraud Detection checks for suspicious activity
5. Risk Engine assesses transaction risk
6. Billing Service creates a transaction record
7. Compliance Service ensures regulatory compliance
8. Success or failure response sent to the user
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Payment Method DB**: MongoDB (flexible payment method data)
- **Transaction DB**: Cassandra (high write throughput)
- **Subscription DB**: DynamoDB (scalable subscription data)
- **Audit Logs**: Elasticsearch (searchable audit trails)
- **Cache**: Redis (recent transactions, user sessions)
- **File Storage**: S3/GCS (transaction receipts)

**Key Design Decisions**:
- PCI DSS compliance
- Real-time fraud detection
- Multi-payment method support
- Developer-friendly APIs
- Global payment processing

---

## 27. Design Zoom Webinar/Live Events

**Problem**: Design a large-scale webinar and live event platform

**Functional Requirements**:
- Support for 10K+ attendees
- HD video streaming
- Interactive features (Q&A, polls)
- Registration management
- Recording and playback
- Analytics and reporting

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <150ms audio/video sync
- Throughput: 1M concurrent viewers, 100K events monthly
- Consistency: Strong consistency for registration status
- Security: Data encryption, secure access
- Scalability: Support for large webinars and events

**Scale**: 1M concurrent viewers, 100K events monthly

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> registeredEventIds;
}

class Event {
    String eventId;
    String hostId;
    String title;
    String description;
    List<String> speakerIds;
    List<String> attendeeIds;
    EventStatus status; // SCHEDULED, LIVE, ENDED
    long startTime;
    long endTime;
}

class Recording {
    String recordingId;
    String eventId;
    String userId;
    String fileUrl;
    long fileSize;
}

class Question {
    String questionId;
    String eventId;
    String userId;
    String content;
    boolean answered;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Event Management
POST /api/v1/events/create
GET /api/v1/events/{eventId}
PUT /api/v1/events/{eventId}/status

// Recording Management
GET /api/v1/recordings/{eventId}
POST /api/v1/recordings/{recordingId}/transcribe

// Question Management
POST /api/v1/questions/ask
GET /api/v1/questions/{eventId}
```

**HLD**:
```
[Client Apps] --> [Registration Service] --> [Streaming Service]
                          |                        |
                  [Interaction Service] --> [Recording Service]
                          |                        |
                  [Analytics Service]       [CDN Network]
                          |                        |
                  [Database]                [Storage Service]
```

**Deep Dive - Webinar Setup & Live Streaming**:
```
1. Host schedules a webinar → Registration Service
2. Registration Service creates an event ID, sends back to host
3. Host shares event ID with speakers and attendees
4. Speakers join the webinar, authenticate via JWT
5. Streaming Service establishes WebRTC connections
6. Attendees join the webinar, receive video and chat updates
7. Recording Service captures webinar if enabled
8. Analytics tracks viewer engagement, stream quality
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Event Metadata**: MongoDB (flexible event data)
- **Recording Metadata**: DynamoDB (scalable recording data)
- **Question Data**: Neo4j (graph-based question management)
- **Analytics**: ClickHouse (real-time analytics)
- **Cache**: Redis (active events, user sessions)
- **Media Storage**: S3/GCS (video files)

**Key Design Decisions**:
- Scalable video streaming architecture
- Interactive feature integration
- Global CDN distribution
- Real-time analytics
- High-availability design

---

## 28. Design Shopify/E-commerce Platform

**Problem**: Design an e-commerce platform for businesses

**Functional Requirements**:
- Online store creation
- Product catalog management
- Order processing
- Payment integration
- Inventory management
- Mobile commerce

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms product search
- Throughput: 1.7M merchants, $175B in sales annually
- Consistency: Strong consistency for order processing
- Security: Data protection, secure payments
- Scalability: Handle peak usage during sales

**Scale**: 1.7M merchants, $175B in sales annually

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> orderIds;
}

class Product {
    String productId;
    String name;
    String description;
    double price;
    int stock;
    List<String> categoryIds;
}

class Order {
    String orderId;
    String userId;
    List<String> productIds;
    double totalAmount;
    OrderStatus status; // PENDING, COMPLETED, CANCELED
    long orderDate;
}

class Category {
    String categoryId;
    String name;
    List<String> productIds;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Product Management
POST /api/v1/products/create
GET /api/v1/products/{productId}
GET /api/v1/products/search?query={query}

// Order Management
POST /api/v1/orders/create
GET /api/v1/orders/{orderId}
PUT /api/v1/orders/{orderId}/status

// Category Management
GET /api/v1/categories/{categoryId}
GET /api/v1/categories/all
```

**HLD**:
```
[Store Frontend] --> [Catalog Service] --> [Inventory Service]
                            |                      |
                    [Order Service] --> [Payment Service]
                            |                      |
                    [User Service]          [Shipping Service]
                            |                      |
                    [Database]              [Analytics Service]
                            |                      |
                    [CDN]                   [Integration Hub]
```

**Deep Dive - Order Processing**:
```
1. User adds product to cart, initiates checkout
2. Order Service validates cart, creates order record
3. Payment Service processes payment, holds in escrow
4. Inventory Service reserves products, updates stock
5. Shipping Service prepares shipment, updates tracking
6. Order confirmation sent to user, with tracking details
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Product DB**: MongoDB (flexible product metadata)
- **Order DB**: Cassandra (high write throughput)
- **Category Data**: Neo4j (graph-based category-product relationships)
- **Analytics**: ClickHouse (sales analytics, reporting)
- **Cache**: Redis (recently viewed products, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Multi-tenant architecture
- Customizable storefront themes
- Payment gateway integrations
- Scalable order processing
- Mobile-first design

---

## 29. Design Calendly/Scheduling Platform

**Problem**: Design an appointment scheduling platform

**Functional Requirements**:
- Calendar integration
- Availability management
- Automated scheduling
- Time zone handling
- Reminder notifications
- Meeting integration

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms availability fetching
- Throughput: 10M monthly users, 50M meetings scheduled annually
- Consistency: Strong consistency for event updates
- Security: Data encryption, secure access
- Scalability: Handle peak usage during business hours

**Scale**: 10M monthly users, 50M meetings scheduled annually

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> eventIds;
}

class Event {
    String eventId;
    String userId;
    String title;
    String description;
    Date startTime;
    Date endTime;
    String location;
    List<String> attendeeIds;
}

class Availability {
    String userId;
    List<TimeSlot> availableTimeSlots;
}

class TimeSlot {
    Date startTime;
    Date endTime;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Event Management
POST /api/v1/events/create
GET /api/v1/events/{eventId}
PUT /api/v1/events/{eventId}/status

// Availability Management
POST /api/v1/availability/set
GET /api/v1/availability/{userId}
```

**HLD**:
```
[Web Apps] --> [Scheduling Service] --> [Calendar Integration]
                       |                        |
               [Availability Service] --> [Notification Service]
                       |                        |
               [User Service]             [Meeting Service]
                       |                        |
               [Database]                 [Time Zone Service]
```

**Deep Dive - Event Scheduling**:
```
1. User selects a time slot → Scheduling Service
2. Availability Service checks if the slot is free
3. Meeting Service creates a new event
4. Notification Service sends confirmation to user
5. Calendar Integration updates user's calendar
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Event DB**: MongoDB (flexible event schema)
- **Availability Data**: Redis (real-time availability checks)
- **Time Zone Data**: PostgreSQL (time zone conversions)
- **Cache**: Redis (recently accessed events, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Calendar provider integrations
- Intelligent conflict resolution
- Time zone normalization
- Automated reminder system
- Meeting platform integrations

---

## 30. Design Notion/Collaborative Workspace

**Problem**: Design an all-in-one workspace for notes, tasks, and collaboration

**Functional Requirements**:
- Block-based content editing
- Real-time collaboration
- Database functionality
- Template system
- Sharing and permissions
- Mobile and web support

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms block loading
- Throughput: 20M users, 1B blocks created
- Consistency: Strong consistency for block updates
- Security: Data encryption, access controls
- Scalability: Support for large workspaces

**Scale**: 20M users, 1B blocks created

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> workspaceIds;
}

class Workspace {
    String workspaceId;
    String name;
    String ownerId;
    List<String> memberIds;
    List<String> blockIds;
}

class Block {
    String blockId;
    String workspaceId;
    String userId;
    String content;
    BlockType type; // TEXT, IMAGE, VIDEO, DATABASE
    List<String> childBlockIds;
}

class Comment {
    String commentId;
    String blockId;
    String userId;
    String content;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Workspace Management
POST /api/v1/workspaces/create
GET /api/v1/workspaces/{workspaceId}
PUT /api/v1/workspaces/{workspaceId}/members

// Block Management
POST /api/v1/blocks/create
GET /api/v1/blocks/{blockId}
PUT /api/v1/blocks/{blockId}/content

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{blockId}?limit={limit}
```

**HLD**:
```
[Client Apps] --> [Editor Service] --> [Collaboration Engine]
                         |                      |
                 [Block Service] --> [Database Service]
                         |                      |
                 [Template Service]     [Permission Service]
                         |                      |
                 [User Service]         [Search Service]
                         |                      |
                 [Storage]              [Sync Service]
```

**Deep Dive - Block-based Editing & Collaboration**:
```
1. User creates a new block in the editor
2. Block Service validates and stores the block
3. Collaboration Engine synchronizes block state in real-time
4. User invites others to collaborate on the block
5. Permission Service manages access rights
6. Changes are saved automatically, versioned in the database
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Workspace DB**: MongoDB (flexible workspace data)
- **Block DB**: Cassandra (high write throughput)
- **Comment Data**: DynamoDB (scalable comment storage)
- **Search Index**: Elasticsearch (block and user search)
- **Cache**: Redis (recently accessed blocks, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Block-based content structure
- Real-time operational transforms
- Flexible database system
- Hierarchical permissions
- Cross-platform synchronization

---

## 31. Design Figma/Design Collaboration

**Problem**: Design a collaborative interface design platform

**Functional Requirements**:
- Real-time design collaboration
- Vector graphics editing
- Component system
- Version control
- Design handoff tools
- Plugin ecosystem

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms editing response
- Throughput: 4M monthly active users, collaborative design sessions
- Consistency: Strong consistency for design updates
- Security: Data encryption, access controls
- Scalability: Support for large design files and teams

**Scale**: 4M monthly active users, collaborative design sessions

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> designFileIds;
}

class DesignFile {
    String fileId;
    String ownerId;
    String name;
    String description;
    List<String> collaboratorIds;
    List<String> componentIds;
    FileStatus status; // DRAFT, PUBLISHED, ARCHIVED
    long createdAt;
    long updatedAt;
}

class Component {
    String componentId;
    String fileId;
    String name;
    String type; // BUTTON, INPUT, CARD
    String properties; // JSON string of properties
}

class Comment {
    String commentId;
    String fileId;
    String userId;
    String content;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Design File Management
POST /api/v1/files/create
GET /api/v1/files/{fileId}
PUT /api/v1/files/{fileId}/status

// Component Management
POST /api/v1/components/create
GET /api/v1/components/{componentId}

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{fileId}?limit={limit}
```

**HLD**:
```
[Design Apps] --> [Collaboration Service] --> [Graphics Engine]
                          |                        |
                  [Component Service] --> [Version Control]
                          |                        |
                  [File Service]          [Plugin Platform]
                          |                        |
                  [User Service]          [Export Service]
                          |                        |
                  [Storage]               [CDN]
```

**Deep Dive - Real-time Design Collaboration**:
```
1. User opens a design file
2. Collaboration Service loads file and component data
3. Real-time operational transforms synchronize edits
4. Version Control creates a new version on each save
5. User invites others to collaborate
6. Changes propagated to all collaborators in real-time
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Design File DB**: MongoDB (flexible design file schema)
- **Component Data**: Cassandra (high write throughput)
- **Comment Data**: DynamoDB (scalable comment storage)
- **Search Index**: Elasticsearch (design file and user search)
- **Cache**: Redis (recently accessed files, metadata)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Real-time vector graphics collaboration
- Component-based design system
- Cloud-native architecture
- Plugin ecosystem support
- Design-to-code workflow

---

## 32. Design Miro/Whiteboard Collaboration

**Problem**: Design a collaborative online whiteboard platform

**Functional Requirements**:
- Infinite canvas with real-time collaboration
- Drawing and diagramming tools
- Template library
- Video conferencing integration
- Content organization
- Mobile support

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms drawing response
- Throughput: 30M users, real-time collaboration sessions
- Consistency: Strong consistency for drawing updates
- Security: Data encryption, access controls
- Scalability: Support for large boards and teams

**Scale**: 30M users, real-time collaboration sessions

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> boardIds;
}

class Board {
    String boardId;
    String ownerId;
    String name;
    String description;
    List<String> memberIds;
    List<String> shapeIds;
}

class Shape {
    String shapeId;
    String boardId;
    String userId;
    String type; // CIRCLE, SQUARE, TEXT
    String properties; // JSON string of properties
    List<String> childShapeIds;
}

class Comment {
    String commentId;
    String shapeId;
    String userId;
    String content;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Board Management
POST /api/v1/boards/create
GET /api/v1/boards/{boardId}
PUT /api/v1/boards/{boardId}/members

// Shape Management
POST /api/v1/shapes/create
GET /api/v1/shapes/{shapeId}

// Comment Management
POST /api/v1/comments/create
GET /api/v1/comments/{shapeId}?limit={limit}
```

**HLD**:
```
[Client Apps] --> [Canvas Service] --> [Collaboration Engine]
                         |                     |
                 [Drawing Service] --> [Template Service]
                         |                     |
                 [Video Service]       [Storage Service]
                         |                     |
                 [User Service]        [Sync Service]
                         |                     |
                 [Database]            [CDN]
```

**Deep Dive - Infinite Canvas Collaboration**:
```
1. User opens a blank board
2. Board Service creates a new board record
3. User adds shapes, drawings, comments
4. Collaboration Engine synchronizes changes in real-time
5. Template Service provides pre-defined templates
6. Video Service integrates video conferencing
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Board DB**: MongoDB (flexible board data)
- **Shape Data**: Cassandra (high write throughput)
- **Comment Data**: DynamoDB (scalable comment storage)
- **Search Index**: Elasticsearch (board and user search)
- **Cache**: Redis (recently accessed boards, metadata)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Infinite canvas architecture
- Real-time drawing synchronization
- Video integration
- Template management system
- Cross-device compatibility

---

## 33. Design Airtable/Database Platform

**Problem**: Design a collaborative database platform with spreadsheet interface

**Functional Requirements**:
- Relational database functionality
- Spreadsheet-like interface
- Real-time collaboration
- API access
- Automation workflows
- Mobile access

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms data access
- Throughput: 5M users, millions of records managed
- Consistency: Strong consistency for data updates
- Security: Data encryption, access controls
- Scalability: Support for large databases and teams

**Scale**: 5M users, millions of records managed

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> databaseIds;
}

class Database {
    String databaseId;
    String name;
    String ownerId;
    List<String> tableIds;
}

class Table {
    String tableId;
    String databaseId;
    String name;
    List<String> columnIds;
    List<String> recordIds;
}

class Record {
    String recordId;
    String tableId;
    Map<String, Object> fields; // columnId -> value
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Database Management
POST /api/v1/databases/create
GET /api/v1/databases/{databaseId}

// Table Management
POST /api/v1/tables/create
GET /api/v1/tables/{tableId}

// Record Management
POST /api/v1/records/create
GET /api/v1/records/{recordId}
PUT /api/v1/records/{recordId}/update
```

**HLD**:
```
[Client Apps] --> [Database Service] --> [Table Service]
                         |                     |
                 [Record Service]     [Search Service]
                         |                     |
                 [User Service]          [Analytics Service]
                         |                     |
                 [Database]            [Integration Hub]
                         |                     |
                 [Cache]              [Notification Service]
```

**Deep Dive - Record Creation & Automation**:
```
1. User creates a new record in the table
2. Record Service validates and stores the record
3. Automation Engine triggers based on record changes
4. Notifications sent for important updates
5. User accesses the record, sees real-time updates
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Database Metadata**: MongoDB (flexible database schema)
- **Table Data**: Cassandra (high write throughput)
- **Record Data**: DynamoDB (scalable record storage)
- **Search Index**: Elasticsearch (record and user search)
- **Cache**: Redis (recently accessed records, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Flexible schema management
- Real-time data synchronization
- RESTful API design
- Workflow automation engine
- Query optimization

---

## 34. Design Asana/Task Management

**Problem**: Design a team task and project management platform

**Functional Requirements**:
- Task creation and assignment
- Project organization
- Team collaboration
- Progress tracking
- Goal setting
- Reporting and analytics

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms task loading
- Throughput: 100M tasks created annually, millions of users
- Consistency: Strong consistency for task updates
- Security: Data encryption, access controls
- Scalability: Handle peak usage during sprints

**Scale**: 100M tasks created annually, millions of users

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> taskIds;
}

class Task {
    String taskId;
    String projectId;
    String assigneeId;
    String title;
    String description;
    TaskStatus status; // TODO, IN_PROGRESS, DONE
    int priority;
    long createdAt;
    long updatedAt;
}

class Project {
    String projectId;
    String name;
    String description;
    String ownerId;
    List<String> memberIds;
    List<String> taskIds;
}

class Goal {
    String goalId;
    String userId;
    String title;
    String description;
    List<String> taskIds;
    GoalStatus status; // ACTIVE, COMPLETED, ARCHIVED
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Task Management
POST /api/v1/tasks/create
GET /api/v1/tasks/{taskId}
PUT /api/v1/tasks/{taskId}/status

// Project Management
POST /api/v1/projects/create
GET /api/v1/projects/{projectId}

// Goal Management
POST /api/v1/goals/create
GET /api/v1/goals/{userId}/active
```

**HLD**:
```
[Client Apps] --> [Task Service] --> [Project Service]
                         |                  |
                 [Team Service] --> [Goal Service]
                         |                  |
                 [User Service]     [Reporting Service]
                         |                  |
                 [Database]         [Analytics Pipeline]
                         |                  |
                 [Search Service]   [Notification Service]
```

**Deep Dive - Task Creation & Reporting**:
```
1. User creates a new task
2. Task Service validates and stores the task
3. Project Service updates project with new task
4. User is notified of task assignment
5. Reporting Service generates reports based on task data
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Task DB**: MongoDB (flexible task schema)
- **Project DB**: Cassandra (high write throughput)
- **Goal Data**: DynamoDB (scalable goal data)
- **Search Index**: Elasticsearch (task and user search)
- **Cache**: Redis (recently viewed tasks, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Hierarchical task organization
- Real-time collaboration features
- Goal tracking and OKRs
- Comprehensive reporting
- Extensive integration support

---

## 35. Design Canva/Design Platform

**Problem**: Design a user-friendly graphic design platform

**Functional Requirements**:
- Drag-and-drop design interface
- Template library
- Asset management
- Team collaboration
- Brand kit management
- Export in multiple formats

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <100ms design element loading
- Throughput: 60M monthly users, 13B designs created
- Consistency: Strong consistency for design updates
- Security: Data encryption, access controls
- Scalability: Support for large design files and teams

**Scale**: 60M monthly users, 13B designs created

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> designFileIds;
}

class DesignFile {
    String fileId;
    String ownerId;
    String name;
    String description;
    List<String> collaboratorIds;
    List<String> componentIds;
    FileStatus status; // DRAFT, PUBLISHED, ARCHIVED
    long createdAt;
    long updatedAt;
}

class Component {
    String componentId;
    String fileId;
    String name;
    String type; // BUTTON, INPUT, CARD
    String properties; // JSON string of properties
}

class BrandKit {
    String kitId;
    String userId;
    String name;
    List<String> logoIds;
    List<String> colorPalette;
    List<String> fontStyles;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Design File Management
POST /api/v1/files/create
GET /api/v1/files/{fileId}
PUT /api/v1/files/{fileId}/status

// Component Management
POST /api/v1/components/create
GET /api/v1/components/{componentId}

// Brand Kit Management
POST /api/v1/brand-kits/create
GET /api/v1/brand-kits/{userId}
```

**HLD**:
```
[Design Apps] --> [Design Service] --> [Template Service]
                         |                    |
                 [Asset Service] --> [Collaboration Service]
                         |                    |
                 [Brand Service]      [Export Service]
                         |                    |
                 [User Service]       [CDN]
                         |                    |
                 [Database]           [Storage Service]
```

**Deep Dive - Drag-and-Drop Design & Collaboration**:
```
1. User starts a new design file
2. Design Service loads the file and its components
3. User drags and drops elements onto the canvas
4. Collaboration Service synchronizes changes in real-time
5. User can invite others to view/edit the design
6. Changes are saved automatically, versioned in the database
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Design File DB**: MongoDB (flexible design file schema)
- **Component Data**: Cassandra (high write throughput)
- **Brand Kit Data**: DynamoDB (scalable brand kit data)
- **Search Index**: Elasticsearch (design file and user search)
- **Cache**: Redis (recently accessed files, metadata)
- **Media Storage**: S3/GCS (images, videos)

**Key Design Decisions**:
- Browser-based design editor
- Extensive template library
- Asset marketplace integration
- Team collaboration features
- Multi-format export system

---

## 36. Design Mailchimp/Email Marketing

**Problem**: Design an email marketing and automation platform

**Functional Requirements**:
- Email campaign creation
- Contact list management
- Automation workflows
- Analytics and reporting
- A/B testing
- Deliverability optimization

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms campaign loading
- Throughput: 12M customers, billions of emails sent monthly
- Consistency: Strong consistency for contact data
- Security: Data encryption, access controls
- Scalability: Handle peak usage during promotions

**Scale**: 12M customers, billions of emails sent monthly

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> campaignIds;
}

class Campaign {
    String campaignId;
    String name;
    String subject;
    String content;
    String status; // DRAFT, SCHEDULED, SENT
    List<String> recipientListIds;
    long scheduledTime;
}

class RecipientList {
    String listId;
    String userId;
    List<String> emailIds;
}

class Email {
    String emailId;
    String campaignId;
    String recipientId;
    String status; // PENDING, SENT, FAILED
    long sentTime;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Campaign Management
POST /api/v1/campaigns/create
GET /api/v1/campaigns/{campaignId}
PUT /api/v1/campaigns/{campaignId}/status

// Recipient List Management
POST /api/v1/lists/create
GET /api/v1/lists/{listId}

// Email Management
GET /api/v1/emails/{campaignId}/status
```

**HLD**:
```
[Web Apps] --> [Campaign Service] --> [Email Service]
                      |                     |
              [Recipient Service] --> [Automation Engine]
                      |                     |
              [Analytics Service]   [Delivery Service]
                      |                     |
              [User Service]        [Template Service]
                      |                     |
              [Database]            [SMTP Infrastructure]
```

**Deep Dive - Campaign Creation & Email Delivery**:
```
1. User creates a new campaign
2. Campaign Service validates and stores the campaign
3. Recipient List Service manages subscriber lists
4. Email Service prepares and sends emails
5. Automation Engine triggers based on user behavior
6. Delivery Service optimizes email delivery
7. Analytics tracks open rates, click rates
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Campaign DB**: MongoDB (flexible campaign data)
- **Recipient List DB**: Cassandra (high write throughput)
- **Email Status**: DynamoDB (scalable email status tracking)
- **Analytics**: ClickHouse (email campaign analytics)
- **Cache**: Redis (recently accessed campaigns, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Scalable email delivery infrastructure
- Advanced segmentation capabilities
- Automation workflow engine
- Deliverability optimization
- Comprehensive analytics

---

## 37. Design Zoom Phone/VoIP Service

**Problem**: Design a cloud-based phone system

**Functional Requirements**:
- Voice calling and routing
- Voicemail and transcription
- Call analytics
- Integration with other services
- Mobile and desktop apps
- Global phone numbers

**Non-Functional Requirements (NFRs)**:
- Availability: 99.99% uptime
- Latency: <200ms call setup
- Throughput: Millions of phone calls daily
- Consistency: Strong consistency for call state
- Security: Data encryption, secure access
- Scalability: Handle peak usage during business hours

**Scale**: Millions of phone calls daily

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> phoneNumberIds;
}

class PhoneNumber {
    String numberId;
    String userId;
    String countryCode;
    String number;
    boolean isPrimary;
}

class Call {
    String callId;
    String fromNumberId;
    String toNumberId;
    CallStatus status; // INITIATED, RINGING, IN_PROGRESS, ENDED
    long startTime;
    long endTime;
    String recordingUrl;
}

class Voicemail {
    String voicemailId;
    String userId;
    String recordingUrl;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Phone Number Management
POST /api/v1/phone-numbers/add
GET /api/v1/phone-numbers/{userId}

// Call Management
POST /api/v1/calls/create
GET /api/v1/calls/{callId}
PUT /api/v1/calls/{callId}/status

// Voicemail Management
GET /api/v1/voicemails/{userId}
POST /api/v1/voicemails/{voicemailId}/transcribe
```

**HLD**:
```
[Client Apps] --> [Call Routing Service] --> [SIP Gateway]
                          |                      |
                  [Voicemail Service] --> [Transcription Service]
                          |                      |
                  [Analytics Service]     [PSTN Gateway]
                          |                      |
                  [User DB]            [Recording Service]
```

**Deep Dive - Call Setup & Voicemail**:
```
1. User initiates a call → Call Routing Service
2. Call Routing Service authenticates and authorizes user
3. SIP Gateway establishes a connection between caller and callee
4. Voicemail Service records the message if callee is unavailable
5. Transcription Service converts voicemail to text
6. Analytics Service tracks call metrics, voicemail usage
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Phone Number DB**: MongoDB (flexible phone number data)
- **Call Metadata**: Cassandra (high write throughput)
- **Voicemail Metadata**: DynamoDB (scalable voicemail data)
- **Analytics**: ClickHouse (call analytics, reporting)
- **Cache**: Redis (active calls, user sessions)
- **Media Storage**: S3/GCS (audio files)

**Key Design Decisions**:
- SIP-based architecture
- Global carrier integrations
- Real-time call routing
- Voice transcription AI
- Call quality optimization

---

## 38. Design DocuSign/Digital Signatures

**Problem**: Design a digital document signing platform

**Functional Requirements**:
- Document upload and preparation
- Electronic signature workflow
- Authentication and verification
- Legal compliance
- Audit trail
- Integration capabilities

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms document loading
- Throughput: 1B documents processed annually
- Consistency: Strong consistency for signature status
- Security: Data encryption, secure access
- Scalability: Handle peak usage during contract negotiations

**Scale**: 1B documents processed annually

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
}

class Document {
    String documentId;
    String title;
    String content;
    String ownerId;
    List<String> signerIds;
    DocumentStatus status; // DRAFT, SENT, SIGNED, COMPLETED
    long createdAt;
    long updatedAt;
}

class Signature {
    String signatureId;
    String documentId;
    String userId;
    String signatureValue;
    long timestamp;
}

class AuditTrail {
    String auditId;
    String documentId;
    String action; // SENT, SIGNED, VIEWED
    String userId;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Document Management
POST /api/v1/documents/upload
GET /api/v1/documents/{documentId}
PUT /api/v1/documents/{documentId}/status

// Signature Management
POST /api/v1/signatures/create
GET /api/v1/signatures/{documentId}/status

// Audit Trail Management
GET /api/v1/audit-trails/{documentId}
```

**HLD**:
```
[Client Apps] --> [Document Service] --> [Signature Service]
                          |                      |
                  [Workflow Service] --> [Authentication Service]
                          |                      |
                  [Compliance Service]   [Audit Service]
                          |                      |
                  [User Service]         [Storage Service]
                          |                      |
                  [Database]             [Integration Hub]
```

**Deep Dive - Document Signing Workflow**:
```
1. User uploads a document for signing
2. Document Service prepares the document, sends for signature
3. Signature Service generates a signature request
4. User signs the document using a secure interface
5. Signature is verified and stored
6. Document status is updated to SIGNED
7. Audit trail is created for compliance
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Document DB**: MongoDB (flexible document schema)
- **Signature Data**: Cassandra (high write throughput)
- **Audit Trail**: Elasticsearch (searchable audit trails)
- **Cache**: Redis (recently accessed documents, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Legal compliance framework
- Multi-factor authentication
- Immutable audit trails
- Document security measures
- API-first integration approach

---

## 39. Design Zendesk/Customer Support

**Problem**: Design a customer support and helpdesk platform

**Functional Requirements**:
- Ticket management system
- Multi-channel support
- Knowledge base
- Live chat functionality
- Agent routing
- Analytics and reporting

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms ticket loading
- Throughput: 170K customers, millions of tickets annually
- Consistency: Strong consistency for ticket updates
- Security: Data encryption, access controls
- Scalability: Handle peak usage during outages

**Scale**: 170K customers, millions of tickets annually

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
}

class Ticket {
    String ticketId;
    String userId;
    String subject;
    String description;
    TicketStatus status; // OPEN, PENDING, CLOSED
    int priority;
    long createdAt;
    long updatedAt;
}

class Article {
    String articleId;
    String title;
    String content;
    String category;
    long createdAt;
    long updatedAt;
}

class Chat {
    String chatId;
    String ticketId;
    String userId;
    String agentId;
    List<String> messageIds;
    long startedAt;
    long endedAt;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Ticket Management
POST /api/v1/tickets/create
GET /api/v1/tickets/{ticketId}
PUT /api/v1/tickets/{ticketId}/status

// Article Management
GET /api/v1/articles/{articleId}
GET /api/v1/articles/search?query={query}

// Chat Management
POST /api/v1/chats/create
GET /api/v1/chats/{ticketId}
```

**HLD**:
```
[Customer Apps] --> [Ticket Service] --> [Routing Engine]
                           |                   |
                   [Chat Service] --> [Knowledge Base]
                           |                   |
                   [Agent Service]     [Analytics Service]
                           |                   |
                   [User Service]      [Notification Service]
                           |                   |
                   [Database]          [Search Service]
```

**Deep Dive - Ticket Management & Chat**:
```
1. User submits a ticket
2. Ticket Service validates and stores the ticket
3. Routing Engine assigns the ticket to an available agent
4. Agent receives a notification of a new ticket
5. Agent responds to the ticket, user is notified
6. Chat Service establishes a real-time chat session
7. Knowledge Base is queried for suggested articles
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Ticket DB**: MongoDB (flexible ticket schema)
- **Article DB**: Cassandra (high write throughput)
- **Chat Data**: DynamoDB (scalable chat data)
- **Search Index**: Elasticsearch (ticket and article search)
- **Cache**: Redis (recently viewed tickets, metadata)
- **Media Storage**: S3/GCS (attachments)

**Key Design Decisions**:
- Omnichannel ticket aggregation
- Intelligent agent routing
- Self-service knowledge base
- Real-time chat infrastructure
- Performance analytics

---

## 40. Design Salesforce/CRM Platform

**Problem**: Design a customer relationship management platform

**Functional Requirements**:
- Contact and lead management
- Sales pipeline tracking
- Reporting and analytics
- Workflow automation
- Custom applications
- Integration ecosystem

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms data access
- Throughput: 150K customers, millions of users
- Consistency: Strong consistency for sales data
- Security: Data encryption, access controls
- Scalability: Handle peak usage during sales campaigns

**Scale**: 150K customers, millions of users

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> contactIds;
    List<String> opportunityIds;
}

class Contact {
    String contactId;
    String userId;
    String firstName;
    String lastName;
    String email;
    String phone;
    String company;
}

class Opportunity {
    String opportunityId;
    String userId;
    String contactId;
    String stage; // QUALIFIED, PROPOSAL, CLOSED_WON, CLOSED_LOST
    double amount;
    long closeDate;
}

class Activity {
    String activityId;
    String userId;
    String contactId;
    String type; // CALL, EMAIL, MEETING
    String notes;
    long scheduledTime;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Contact Management
POST /api/v1/contacts/create
GET /api/v1/contacts/{contactId}

// Opportunity Management
POST /api/v1/opportunities/create
GET /api/v1/opportunities/{opportunityId}

// Activity Management
POST /api/v1/activities/create
GET /api/v1/activities/{userId}/upcoming
```

**HLD**:
```
[Client Apps] --> [CRM Service] --> [Contact Service]
                         |                 |
                 [Sales Service] --> [Analytics Service]
                         |                 |
                 [Automation Engine] --> [Reporting Service]
                         |                 |
                 [User Service]      [Integration Platform]
                         |                 |
                 [Database]          [Custom App Platform]
```

**Deep Dive - Sales Pipeline Tracking**:
```
1. User creates a new opportunity
2. Sales Service validates and stores the opportunity
3. Opportunity is assigned to a sales representative
4. Sales rep updates the opportunity stage as it progresses
5. Automation Engine triggers follow-up tasks and reminders
6. Reporting Service generates sales pipeline reports
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Contact DB**: MongoDB (flexible contact data)
- **Opportunity DB**: Cassandra (high write throughput)
- **Activity Data**: DynamoDB (scalable activity data)
- **Search Index**: Elasticsearch (contact and opportunity search)
- **Cache**: Redis (recently viewed contacts, opportunities)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Multi-tenant architecture
- Customizable data models
- Workflow automation engine
- Extensive integration capabilities
- Platform-as-a-service model

---

## 41. Design HubSpot/Marketing Platform

**Problem**: Design an inbound marketing and sales platform

**Functional Requirements**:
- Website and landing page builder
- Lead capture and nurturing
- Email marketing automation
- CRM integration
- Analytics and reporting
- Content management

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms page loading
- Throughput: 100K customers, marketing automation at scale
- Consistency: Strong consistency for contact and lead data
- Security: Data encryption, access controls
- Scalability: Handle peak usage during marketing campaigns

**Scale**: 100K customers, marketing automation at scale

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> contactIds;
    List<String> campaignIds;
}

class Contact {
    String contactId;
    String userId;
    String firstName;
    String lastName;
    String email;
    String phone;
    String company;
    List<String> activityIds;
}

class Campaign {
    String campaignId;
    String userId;
    String name;
    String type; // EMAIL, SOCIAL, PPC
    String status; // ACTIVE, PAUSED, COMPLETED
    long startDate;
    long endDate;
}

class Activity {
    String activityId;
    String contactId;
    String type; // EMAIL_OPEN, LINK_CLICK, FORM_SUBMIT
    String details;
    long timestamp;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Contact Management
POST /api/v1/contacts/create
GET /api/v1/contacts/{contactId}

// Campaign Management
POST /api/v1/campaigns/create
GET /api/v1/campaigns/{campaignId}

// Activity Management
GET /api/v1/activities/{contactId}?limit={limit}
```

**HLD**:
```
[Web Builder] --> [Lead Service] --> [Automation Engine]
                        |                   |
                [Email Service] --> [CRM Service]
                        |                   |
                [Analytics Service] --> [Content Service]
                        |                   |
                [User Service]      [Integration Hub]
                        |                   |
                [Database]          [Tracking Service]
```

**Deep Dive - Lead Capture & Nurturing**:
```
1. User fills out a form on the landing page
2. Lead Service captures and stores the lead data
3. Automation Engine triggers a welcome email
4. Lead is added to the CRM for tracking
5. User is notified of new lead assignment
6. Analytics tracks lead engagement, campaign performance
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Contact DB**: MongoDB (flexible contact data)
- **Campaign DB**: Cassandra (high write throughput)
- **Activity Data**: DynamoDB (scalable activity data)
- **Analytics**: ClickHouse (marketing analytics, reporting)
- **Cache**: Redis (recently viewed contacts, campaigns)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Integrated marketing suite
- Lead scoring algorithms
- Marketing automation workflows
- Multi-channel attribution
- Growth-driven design tools

---

## 42. Design Intercom/Customer Messaging

**Problem**: Design a customer messaging and engagement platform

**Functional Requirements**:
- Live chat and messaging
- Customer onboarding flows
- Help desk functionality
- Product tours and tutorials
- Customer health scoring
- Multi-platform support

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms message loading
- Throughput: 30K businesses, billions of messages
- Consistency: Strong consistency for direct messages
- Security: Data encryption, access controls
- Scalability: Handle peak usage during product launches

**Scale**: 30K businesses, billions of messages

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> conversationIds;
}

class Message {
    String messageId;
    String conversationId;
    String senderId;
    String content;
    long timestamp;
    MessageType type; // TEXT, FILE, LINK
}

class Conversation {
    String conversationId;
    String userId;
    List<String> messageIds;
    long startedAt;
    long updatedAt;
}

class Tour {
    String tourId;
    String productId;
    List<String> stepIds;
}

class Step {
    String stepId;
    String tourId;
    String title;
    String content;
    String elementSelector;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Message Management
POST /api/v1/messages/send
GET /api/v1/messages/{conversationId}/history
PUT /api/v1/messages/{messageId}/status

// Conversation Management
GET /api/v1/conversations/{userId}/list
GET /api/v1/conversations/{conversationId}

// Tour Management
POST /api/v1/tours/create
GET /api/v1/tours/{tourId}
```

**HLD**:
```
[Client Apps] --> [Messaging Service] --> [Chat Engine]
                          |                     |
                  [Conversation Service]     [Tour Service]
                          |                     |
                  [User Service]          [Analytics Service]
                          |                     |
                  [Database]            [Integration Hub]
```

**Deep Dive - Live Chat & Product Tours**:
```
1. User initiates a chat
2. Messaging Service authenticates and authorizes user
3. Chat Engine establishes a WebSocket connection
4. Messages are sent and received in real-time
5. Conversation Service manages chat history and state
6. Tour Service provides guided tours for onboarding
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Message DB**: Cassandra (high write throughput)
- **Conversation DB**: MongoDB (flexible conversation data)
- **Tour Data**: DynamoDB (scalable tour data)
- **Analytics**: ClickHouse (message and engagement analytics)
- **Cache**: Redis (active conversations, user sessions)
- **Media Storage**: S3/GCS (attachments)

**Key Design Decisions**:
- Real-time messaging infrastructure
- Behavioral trigger system
- Customer journey mapping
- Multi-channel communication
- Customer health analytics

---

## 43. Design Typeform/Form Builder

**Problem**: Design an interactive form and survey builder

**Functional Requirements**:
- Drag-and-drop form builder
- Interactive question types
- Response collection and analysis
- Integration capabilities
- Custom branding
- Mobile optimization

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms form loading
- Throughput: 150M responses collected annually
- Consistency: Strong consistency for response data
- Security: Data encryption, access controls
- Scalability: Handle peak usage during surveys

**Scale**: 150M responses collected annually

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> formIds;
}

class Form {
    String formId;
    String userId;
    String title;
    List<Question> questions;
    FormStatus status; // DRAFT, PUBLISHED, ARCHIVED
    long createdAt;
    long updatedAt;
}

class Question {
    String questionId;
    String formId;
    String content;
    QuestionType type; // TEXT, MULTIPLE_CHOICE, RATING
    List<String> options;
}

class Response {
    String responseId;
    String formId;
    String userId;
    Map<String, Object> answers; // questionId -> answer
    long submittedAt;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Form Management
POST /api/v1/forms/create
GET /api/v1/forms/{formId}
PUT /api/v1/forms/{formId}/status

// Response Management
POST /api/v1/responses/submit
GET /api/v1/responses/{formId}?limit={limit}
```

**HLD**:
```
[Form Builder] --> [Form Service] --> [Response Service]
                         |                   |
                 [Question Engine] --> [Analytics Service]
                         |                   |
                 [Template Service]     [Integration Hub]
                         |                   |
                 [User Service]       [Storage Service]
                         |                   |
                 [Database]            [CDN]
```

**Deep Dive - Form Submission & Analysis**:
```
1. User creates a form with questions
2. Form Service validates and stores the form
3. User shares the form link with respondents
4. Responses are submitted and stored in real-time
5. Analytics Service provides response analysis and reporting
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Form Metadata**: MongoDB (flexible form metadata)
- **Response Data**: Cassandra (high write throughput)
- **Analytics**: ClickHouse (response analytics, reporting)
- **Cache**: Redis (recently accessed forms, metadata)
- **File Storage**: Distributed object storage (S3/GCS)

**Key Design Decisions**:
- Interactive form experience
- Real-time response processing
- Advanced question logic
- Mobile-first design
- Data visualization tools

---

## 44. Design Zapier/Automation Platform

**Problem**: Design a workflow automation platform connecting apps

**Functional Requirements**:
- App integration marketplace
- Workflow creation (triggers and actions)
- Data transformation
- Error handling and monitoring
- Scalable execution engine
- Developer platform

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms workflow execution
- Throughput: 5M workflows, 5K+ app integrations
- Consistency: Eventual consistency for data updates
- Security: Data encryption, access controls
- Scalability: Handle peak usage during business hours

**Scale**: 5M workflows, 5K+ app integrations

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String name;
    List<String> workflowIds;
}

class Workflow {
    String workflowId;
    String userId;
    String name;
    List<Trigger> triggers;
    List<Action> actions;
    WorkflowStatus status; // ACTIVE, PAUSED, DISABLED
}

class Trigger {
    String triggerId;
    String workflowId;
    String type; // EVENT, SCHEDULE, API_CALL
    String source; // APP_NAME, WEBHOOK_URL
}

class Action {
    String actionId;
    String workflowId;
    String type; // EMAIL, SMS, HTTP_REQUEST
    Map<String, String> parameters; // key-value parameters
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Workflow Management
POST /api/v1/workflows/create
GET /api/v1/workflows/{workflowId}
PUT /api/v1/workflows/{workflowId}/status

// Trigger Management
POST /api/v1/triggers/create
GET /api/v1/triggers/{workflowId}

// Action Management
POST /api/v1/actions/create
GET /api/v1/actions/{workflowId}
```

**HLD**:
```
[Web Interface] --> [Workflow Engine] --> [Integration Hub]
                           |                    |
                   [Trigger Service] --> [Action Service]
                           |                    |
                   [Data Transform]     [Error Handling]
                           |                    |
                   [User Service]       [Monitoring Service]
                           |                    |
                   [Database]           [Queue System]
```

**Deep Dive - Workflow Execution & Monitoring**:
```
1. User creates a new workflow
2. Workflow Engine validates and stores the workflow
3. Trigger Service listens for trigger events
4. Action Service performs actions based on trigger events
5. Data Transform modifies data formats as needed
6. Error Handling manages any errors during execution
7. Monitoring Service tracks workflow performance
```

**Database Choices**:
- **User DB**: PostgreSQL (user profiles, authentication)
- **Workflow Metadata**: MongoDB (flexible workflow schema)
- **Trigger Data**: Cassandra (high write throughput)
- **Action Data**: DynamoDB (scalable action data)
- **Monitoring Data**: ClickHouse (real-time monitoring)
- **Cache**: Redis (active workflows, user sessions)
- **Media Storage**: S3/GCS (logs, backups)

**Key Design Decisions**:
- Event-driven architecture
- Scalable workflow execution
- Extensive API integrations
- Data transformation engine
- Developer-friendly platform

---

## 45. Design Monday.com/Work Management

**Problem**: Design a work management platform for teams

**Functional Requirements**:
- Customizable workflows
- Project and task management
- Team collaboration
- Time tracking
- Reporting and analytics
- Automation capabilities

**Non-Functional Requirements (NFRs)**:
- Availability: 99.9% uptime
- Latency: <200ms task loading
- Throughput: 150K customers, millions of users
- Consistency: Strong consistency for task updates
- Security: Data encryption, access controls
- Scalability: Handle peak usage during sprints

**Scale**: 150K customers, millions of users

**Core Entities**:
```java
class User {
    String userId;
    String email;
    String passwordHash;
    String displayName;
    List<String> taskIds;
}

class Task {
    String taskId;
    String projectId;
    String assigneeId;
    String title;
    String description;
    TaskStatus status; // TODO, IN_PROGRESS, DONE
    int priority;
    long createdAt;
    long updatedAt;
}

class Project {
    String projectId;
    String name;
    String description;
    String ownerId;
    List<String> memberIds;
    List<String> taskIds;
}

class Report {
    String reportId;
    String projectId;
    String userId;
    String content;
    long createdAt;
}
```

**APIs**:
```java
// User Management
POST /api/v1/users/register
GET /api/v1/users/{userId}/profile

// Task Management
POST /api/v1/tasks/create
GET /api/v1/tasks/{taskId}
PUT /api/v1/tasks/{taskId}/status

// Project Management
POST /api/v1/projects/create
GET /api/v1/projects/{projectId}

// Report Management
POST /api/v1/reports/create
GET /api/v1/reports/{projectId}
```

**HLD**:
```
[Client Apps] --> [Task Service] --> [Project Service]
                         |                     |
                 [Team Service] --> [Report Service]
                         |                     |
                 [User Service]      [Analytics Pipeline]
                         |                     |
                 [Database]         [Notification Service]
                         |