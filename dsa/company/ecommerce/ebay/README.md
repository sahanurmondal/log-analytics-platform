# eBay Interview Preparation Guide (2023-2025)

## 📋 Overview

This comprehensive guide covers eBay technical interviews for both **DSA (Data Structures & Algorithms)** and **System Design** rounds, with recent hiring patterns from 2023-2025.

**Interview Structure**:
- 🔄 **Round 1**: Online Assessment (LeetCode-style, 90 minutes)
- 🗣️ **Round 2**: DSA Technical Interview (60 minutes)
- 🏗️ **Round 3**: System Design Interview (75 minutes)
- 💬 **Round 4**: Behavioral Interview (45 minutes)

---

## 📚 Documentation Files

### 1. **DSA Interview Questions** 
📄 [`EBAY_DSA_INTERVIEW_QUESTIONS.md`](./EBAY_DSA_INTERVIEW_QUESTIONS.md)

**Coverage**: 60+ problems from 2023-2025 hiring season

**Sections**:
- ✅ [Arrays](#arrays) - 15 problems (Easy to Hard)
- ✅ [Linked Lists](#linked-list) - 8 problems
- ✅ [Stacks](#stacks) - 3 problems
- ✅ [Queues](#queues) - 3 problems
- ✅ [Trees](#trees) - 8 problems
- ✅ [Hash Maps](#hash-maps) - 4 problems
- ✅ [Sorting](#sorting) - 2 problems
- ✅ [Binary Search](#binary-search) - 4 problems
- ✅ [Two Pointers](#two-pointers) - 3 problems
- ✅ [Sliding Window](#sliding-window) - 2 problems
- ✅ [Dynamic Programming](#dynamic-programming) - 4 problems
- ✅ [Strings](#strings-manipulation) - 3 problems
- ✅ [Graphs](#graph-algorithms) - 3 problems
- ✅ [Union Find](#union-find) - 1 problem
- ✅ [Hashing](#hashing) - 1 problem
- ✅ [Intervals](#intervals-scheduling) - 1 problem
- ✅ [E-Commerce Domain Problems](#ecommerce-domain) - 6 problems

**For Each Problem**:
- LeetCode link
- Difficulty level
- Asked at eBay frequency
- Topic area
- Link to your code (if exists) or pseudocode
- E-Commerce context/relevance

---

### 2. **System Design Questions**
📄 [`EBAY_SYSTEM_DESIGN_QUESTIONS.md`](./EBAY_SYSTEM_DESIGN_QUESTIONS.md)

**Coverage**: Core eBay architectures and design patterns (2023-2025)

**Sections**:
- ✅ [Monolithic vs Microservices](#monolithic-vs-microservices)
- ✅ [Event-Driven Architecture](#event-driven-architecture)
- ✅ [Microservices Patterns](#microservices-architecture)
- ✅ [Data Layer](#data-layer-architecture)
- ✅ [E-Commerce Platform Design](#ecommerce-platform)
- ✅ [Inventory System](#inventory-system)
- ✅ [Payment Processing](#payment-processing)
- ✅ [Shipping & Logistics](#shipping--logistics)
- ✅ [Search Service](#search-service)
- ✅ [Recommendation Engine](#recommendation-engine)
- ✅ [Auction System](#auction--bidding-system)
- ✅ [Scalability Strategies](#scalability-strategies)
- ✅ [Consistency Models](#consistency-models)
- ✅ [Security](#security-considerations)
- ✅ [Reliability & Resilience](#reliability--resilience)
- ✅ [Interview Questions (Easy/Medium/Hard)](#interview-questions-by-difficulty)
- ✅ [Common Mistakes](#common-mistakes)

**For Each Topic**:
- Requirements clarification
- High-level architecture diagrams (ASCII)
- API design details
- Trade-off analysis
- Real eBay context

---

## 🎯 Quick Start Guide

### If You Have 1 Week

**Day 1-2**: Arrays & Hash Maps (Most Frequent)
- Two Sum (variations)
- Container With Most Water
- Product of Array Except Self
- Subarray Sum Equals K
- Group Anagrams

**Day 3-4**: Trees & Graphs (Common)
- Binary Tree Level Order Traversal
- Number of Islands
- Course Schedule
- Clone Graph
- LRU Cache

**Day 5-6**: System Design (Warm-up)
- Simple problems (URL Shortener, Parking Lot)
- eBay Search Service
- eBay Order System

**Day 7**: Mock Interview
- Practice 1 DSA problem under time pressure
- Practice 1 System Design problem (45 minutes)

---

### If You Have 2 Weeks

**Week 1**: DSA Foundation
- Arrays: 10 problems
- Linked Lists: 5 problems
- Hash Maps: 4 problems
- Stacks: 2 problems

**Week 2**: Advanced DSA + System Design
- Trees: 8 problems
- Graphs: 5 problems
- DP: 4 problems
- System Design: 5 designs
- Mock interviews: 2

---

### If You Have 1 Month

**Week 1**: Core Data Structures
- Arrays (15 problems)
- Linked Lists (8 problems)
- Stacks & Queues (6 problems)

**Week 2**: Advanced Structures
- Trees (8 problems)
- Graphs (5 problems)
- Hash Tables (4 problems)

**Week 3**: Algorithms & Patterns
- Dynamic Programming (8 problems)
- Sliding Window (4 problems)
- Two Pointers (3 problems)
- Binary Search (4 problems)

**Week 4**: System Design + Practice
- Complete all System Design topics
- eBay-specific designs (5+)
- Mock interviews (4+)
- LeetCode discuss section review

---

## 📊 Problem Distribution by Recent Hiring (2023-2025)

### DSA Round Frequency

| Problem Type | Frequency | Examples |
|--------------|-----------|----------|
| Arrays | 🔥 Very High | Two Sum, Container, Product of Array Except Self |
| Hash Maps | 🔥 Very High | Group Anagrams, LRU Cache |
| Linked Lists | ✅ High | Reverse, Merge Two Lists, Add Two Numbers |
| Trees | ✅ High | Level Order, Path Sum, LCA |
| Graphs | ✅ High | Islands, Course Schedule, Clone Graph |
| Dynamic Programming | ⚠️ Medium | Climbing Stairs, Coin Change |
| Strings | ⚠️ Medium | Longest Substring, Palindrome |
| Stacks | ⚠️ Medium | Valid Parentheses, Evaluate RPN |
| Binary Search | ✅ High | Search Rotated Array, Find Position |
| Two Pointers | ⚠️ Medium | 3Sum, Valid Palindrome |
| Sliding Window | ⚠️ Medium | Longest Substring, Min Window |

### System Design Round Frequency

| Topic | Frequency | Examples |
|-------|-----------|----------|
| E-Commerce Platform | 🔥 Always | Complete eBay design |
| Microservices | 🔥 Always | Service decomposition |
| Data Consistency | ✅ High | ACID vs Eventual |
| Payment Processing | ✅ High | PCI compliance, idempotency |
| Search Service | ✅ High | Elasticsearch design |
| Inventory System | ✅ High | Stock tracking, overselling |
| Notifications | ✅ High | Multi-channel delivery |
| Recommendations | ✅ High | Collaborative filtering |
| Database Sharding | ✅ High | Partition strategies |
| Caching | ✅ High | Multi-tier caching |

---

## 🎓 Learning Resources

### LeetCode Discuss Section
**Links by Topic**:
- [Two Sum Variations](https://leetcode.com/discuss/general-discussion/460599/binary-search-vs-other-approaches)
- [System Design](https://leetcode.com/discuss/general-discussion/558201)
- [Interview Experience](https://leetcode.com/discuss/interview-experience)

### Interview Platforms
- **LeetCode**: https://leetcode.com/ (Must-do: 100+ problems)
- **InterviewBit**: https://www.interviewbit.com/ (Company-specific)
- **GeeksforGeeks**: System design tutorials
- **Educative.io**: Grokking System Design

### eBay-Specific Resources
- **eBay Tech Blog**: https://innovation.ebaytech.com/tech-blog/
- **eBay Engineering Talks** (YouTube): Architecture deep-dives
- **Glassdoor**: eBay interview experiences
- **YouTube**: "eBay system design interview" playlists

### Books
- **"Cracking the Coding Interview"** by Gayle Laakmann McDowell
- **"System Design Interview"** (2 volumes) by Alex Xu
- **"Designing Data-Intensive Applications"** by Martin Kleppmann

---

## 💡 eBay-Specific Tips

### Why eBay Emphasizes These Topics

**1. Arrays & Hash Maps** (40% of DSA round)
- Product catalog search
- Price comparisons
- Inventory tracking
- Deduplication

**2. Graphs & Topological Sort** (20% of DSA round)
- Dependency management
- Category hierarchies
- Recommendation chains

**3. System Design** (50% of technical interviews)
- E-Commerce is inherently distributed
- Scale: 180M active users, 1B+ items
- Real-time consistency challenges
- Fault tolerance critical

**4. Payment/Financial Systems** (25% of system design)
- eBay's core business
- Regulatory requirements
- Fraud prevention
- Audit trails

---

## 🚀 Study Strategy

### Active Learning (NOT passive reading)

**For DSA Problems**:
1. ✅ Read problem statement
2. ❌ Don't look at solutions
3. 💭 Think 5-10 minutes
4. 🖊️ Write pseudocode
5. 💻 Code without reference
6. 🔍 Test edge cases
7. ⏱️ Time yourself (LeetCode medium = 20 mins)
8. 📝 Review optimal solution only if stuck >20 mins

**For System Design**:
1. ✅ Ask clarifying questions
2. 📊 Draw architecture (not text)
3. 🔍 Deep dive 1-2 components
4. ⚖️ Discuss trade-offs
5. 🛡️ Talk about reliability
6. 🎯 Justify every technology choice
7. 🔄 Be ready to pivot on new requirements

---

## 📈 Progress Tracking

### DSA Progress Checklist

```
[ ] Arrays (15/15 problems)
    [ ] Easy (5/5)
    [ ] Medium (7/7)
    [ ] Hard (3/3)

[ ] Linked Lists (8/8 problems)
    [ ] Easy (3/3)
    [ ] Medium (4/4)
    [ ] Hard (1/1)

[ ] Hash Maps (4/4 problems)
    [ ] Easy (1/1)
    [ ] Medium (3/3)

[ ] Trees (8/8 problems)
    [ ] Easy (3/3)
    [ ] Medium (4/4)
    [ ] Hard (1/1)

[Continue for all topics...]
```

### System Design Progress Checklist

```
[ ] Monolithic vs Microservices
[ ] Event-Driven Architecture
[ ] Microservices Patterns
[ ] E-Commerce Platform
[ ] Search Service
[ ] Payment System
[ ] Inventory Management
[ ] Notification Service
[ ] Recommendation Engine
[ ] Auction System
[ ] Database Sharding
[ ] Caching Strategies
[ ] Consistency Models
[ ] Security & Compliance
[ ] Reliability & Disaster Recovery
```

---

## 🎬 Mock Interview Checklist

### Before Mock Interview

- [ ] Choose a quiet environment
- [ ] Set timer for correct duration (60 min DSA, 75 min System Design)
- [ ] Have whiteboard/paper ready
- [ ] Disable notifications
- [ ] Have water nearby

### DSA Mock (60 minutes)

- [ ] **0-5 min**: Understand problem, ask clarifying questions
- [ ] **5-30 min**: Design solution, write code
- [ ] **30-55 min**: Optimize, add comments, handle edge cases
- [ ] **55-60 min**: Test with examples, discuss complexity

**Questions to Ask Yourself**:
- [ ] Is brute force too slow?
- [ ] Can I use a hash map for O(1) lookup?
- [ ] Can I optimize space?
- [ ] Are there any edge cases?

### System Design Mock (75 minutes)

- [ ] **0-5 min**: Clarify requirements and constraints
- [ ] **5-10 min**: Propose high-level design (draw boxes)
- [ ] **10-40 min**: Deep dive into 1-2 components
- [ ] **40-60 min**: Discuss scalability and reliability
- [ ] **60-75 min**: Handle questions, defend choices

**Questions to Ask**:
- [ ] How many users/products/orders?
- [ ] Read-heavy or write-heavy?
- [ ] Consistency requirements?
- [ ] Latency P99?
- [ ] Availability requirements?

---

## ⚠️ Common Mistakes to Avoid

### DSA Round

❌ **Writing code without planning**
✅ Talk through approach first, then code

❌ **Not testing edge cases**
✅ Test: empty input, single element, duplicates, negative numbers

❌ **Ignoring time/space complexity**
✅ Always calculate and optimize

❌ **Not asking clarifying questions**
✅ Ask: "Can array be negative? Can it be empty? Duplicates allowed?"

### System Design Round

❌ **Jumping to solution without clarification**
✅ Ask: "How many users? How much data? What's the peak QPS?"

❌ **Using buzzwords without understanding**
✅ Know why you're recommending PostgreSQL vs MongoDB

❌ **Ignoring single points of failure**
✅ Discuss: "What if database is down? Payment gateway fails?"

❌ **No monitoring/logging**
✅ Include: Datadog, CloudWatch, ELK stack, distributed tracing

---

## 🔗 Navigation

**Jump to specific topics**:

### DSA Topics
- [Arrays](./EBAY_DSA_INTERVIEW_QUESTIONS.md#arrays) (15 problems)
- [Linked Lists](./EBAY_DSA_INTERVIEW_QUESTIONS.md#linked-list) (8 problems)
- [Stacks](./EBAY_DSA_INTERVIEW_QUESTIONS.md#stacks) (3 problems)
- [Queues](./EBAY_DSA_INTERVIEW_QUESTIONS.md#queues) (3 problems)
- [Trees](./EBAY_DSA_INTERVIEW_QUESTIONS.md#trees) (8 problems)
- [Hash Maps](./EBAY_DSA_INTERVIEW_QUESTIONS.md#hash-maps) (4 problems)
- [Binary Search](./EBAY_DSA_INTERVIEW_QUESTIONS.md#binary-search) (4 problems)
- [Dynamic Programming](./EBAY_DSA_INTERVIEW_QUESTIONS.md#dynamic-programming) (4 problems)
- [Graphs](./EBAY_DSA_INTERVIEW_QUESTIONS.md#graph-algorithms) (3 problems)

### System Design Topics
- [Architecture Patterns](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#core-architectural-patterns)
  - [Monolithic vs Microservices](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#1-monolithic-vs-microservices)
  - [Event-Driven](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#2-event-driven-architecture)
  - [Microservices](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#3-microservices-architecture)
  
- [Domain-Specific](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#domain-specific-designs)
  - [E-Commerce Platform](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#ecommerce-platform)
  - [Inventory System](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#2-inventory-system)
  - [Payment Processing](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#3-payment-processing)
  - [Shipping](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#4-shipping--logistics)
  
- [Advanced Topics](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#advanced-designs)
  - [Search Service](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#search-service)
  - [Recommendation Engine](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#recommendation-engine)
  - [Auction System](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#auction--bidding-system)
  
- [Non-Functional Requirements](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#scalability-strategies)
  - [Scalability](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#scalability-strategies)
  - [Consistency](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#consistency-models)
  - [Security](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#security-considerations)
  - [Reliability](./EBAY_SYSTEM_DESIGN_QUESTIONS.md#reliability--resilience)

---

## 📞 Interview Tips from Recent eBay Interviewers

**Based on Glassdoor/LeetCode Discuss (2023-2025)**:

1. **Communicate constantly** - Don't code silently for 20 minutes
2. **Start simple** - Brute force first, then optimize
3. **Ask for hints** - "Should I use a hash map here?" is smart
4. **Discuss complexity** - O(n²) is sometimes acceptable
5. **Test as you code** - Catch bugs before final review
6. **For System Design**:
   - Draw pictures (ASCII is fine)
   - Explain your technology choices
   - Discuss trade-offs honestly
   - Be ready to change approach
   - Focus on consistency & reliability

---

## 📅 Recommended Timeline

**8 Week Study Plan**:

```
Week 1: Arrays, Hash Maps (20 problems)
Week 2: Linked Lists, Stacks, Queues (14 problems)
Week 3: Trees, Graphs (13 problems)
Week 4: Binary Search, Sliding Window, Two Pointers (11 problems)
Week 5: Dynamic Programming, Strings (7 problems)
Week 6: System Design Foundations (5 designs)
Week 7: eBay-Specific System Designs (5 designs)
Week 8: Mock Interviews & Review (8+ mocks)
```

**Per Day**:
- Morning: 2 DSA problems (1.5 hours)
- Afternoon: Review + 1 system design (1 hour)
- Evening: Reading + theory (1 hour)
- Total: 3.5 hours/day

---

## 🎁 Bonus Resources

### Curated Problem Lists
- **Top 50 Coding Interview Questions**: LeetCode premium
- **eBay On-Site Curated List**: Glassdoor reviews
- **Company-Specific Patterns**: LeetCode discuss

### Video Resources
- System Design YouTube channels
- eBay engineering talks
- Mock interview videos (Interviewing.io)

### Community
- LeetCode Discuss (search "eBay")
- Glassdoor (search "eBay interview")
- Reddit r/cscareerquestions
- Interview prep discord servers

---

## ✅ Final Checklist Before Interview

**Week Before**:
- [ ] Completed all 60+ DSA problems
- [ ] Completed all System Design topics
- [ ] Done 4+ mock DSA interviews
- [ ] Done 4+ mock system design interviews
- [ ] Reviewed own code from this repo

**Day Before**:
- [ ] Light review of weak areas only
- [ ] Get good sleep (8+ hours)
- [ ] Prepare laptop, whiteboard, pens
- [ ] Check network connection

**Day Of**:
- [ ] Eat healthy breakfast
- [ ] Arrive 10 minutes early (if in-person)
- [ ] Calm breathing exercises
- [ ] Remember: Interviewers want you to succeed!

---

## 📞 Interview Day Tips

1. **Stay calm** - It's just a conversation
2. **Think out loud** - Silence is bad
3. **Ask questions** - Clarify before solving
4. **Be honest** - "I'm not sure, can I think about it?"
5. **Show your work** - Explain your reasoning
6. **Handle mistakes** - Correct gracefully
7. **Be friendly** - You might work with them!

---

**Good luck! 🚀**

*Last updated: December 2025*
*All information based on 2023-2025 hiring season*

