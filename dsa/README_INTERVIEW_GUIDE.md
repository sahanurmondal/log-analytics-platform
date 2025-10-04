# 📚 LeetCode Interview Preparation - File Guide

This directory contains comprehensive interview preparation materials organized for optimal study efficiency.

## 📋 File Overview

### 🎯 Main Study Files

| File | Purpose | Best Used For |
|------|---------|---------------|
| `SDE_INTERVIEW_MUST_DO_PROBLEMS.md` | **Comprehensive 150+ problem list** | Complete interview preparation |
| `SDE_INTERVIEW_QUICK_REFERENCE.md` | **Condensed study guide** | Last-minute review, pattern templates |
| `PACKAGE_WISE_MUST_DO_MAPPING.md` | **Maps problems to your code** | Navigating existing implementations |

### 🛠️ Automation Scripts

| File | Purpose | Usage |
|------|---------|-------|
| `../generate_package_readmes.sh` | Generate README files for all packages | `./generate_package_readmes.sh` |
| `../update_package_readme.sh` | Update individual package READMEs | `./update_package_readme.sh trees` |
| `../README_SCRIPTS.md` | Documentation for scripts | Reference guide |

## 🚀 Quick Start Guide

### For Interview Preparation (8-10 weeks)
```bash
# 1. Read the comprehensive guide
open SDE_INTERVIEW_MUST_DO_PROBLEMS.md

# 2. Use package mapping to find your code
open PACKAGE_WISE_MUST_DO_MAPPING.md

# 3. Keep quick reference handy
open SDE_INTERVIEW_QUICK_REFERENCE.md
```

### For Last-Minute Review (24-48 hours)
```bash
# Focus on quick reference only
open SDE_INTERVIEW_QUICK_REFERENCE.md
```

### For Maintaining Documentation
```bash
# Generate/update all package READMEs
./generate_package_readmes.sh

# Update specific package
./update_package_readme.sh arrays
```

## 📊 Study Plan Summary

### Phase 1: Foundation (Weeks 1-2)
- **Focus**: Arrays, Strings, LinkedList, TwoPointers
- **Problems**: 35-40 problems
- **Goal**: Master basic data structure manipulation

### Phase 2: Core Algorithms (Weeks 3-4)  
- **Focus**: Trees, Stacks, BinarySearch, SlidingWindow
- **Problems**: 30-35 problems
- **Goal**: Understand recursive and search patterns

### Phase 3: Advanced Topics (Weeks 5-6)
- **Focus**: DP, Graphs, Backtracking, Intervals
- **Problems**: 35-40 problems  
- **Goal**: Master optimization and exploration algorithms

### Phase 4: Specialized (Weeks 7-8)
- **Focus**: Design, Greedy, Heap, Math, BitManipulation
- **Problems**: 25-30 problems
- **Goal**: Handle system design and special patterns

### Phase 5: Integration (Weeks 9-10)
- **Focus**: Mock interviews, hard problems, review
- **Problems**: Problem variations and edge cases
- **Goal**: Interview readiness and confidence

## 🎯 Problem Priority Legend

| Symbol | Priority | Description |
|--------|----------|-------------|
| 🔥 | **Critical** | Must know for any SDE interview |
| 🎯 | **High** | Frequently asked, high impact |
| 🏆 | **Medium** | Good to know, company specific |

## 📈 Success Metrics

### Weekly Targets
- **Week 1-2**: 4-5 problems/day (Easy focus)
- **Week 3-4**: 4-5 problems/day (Medium focus)  
- **Week 5-6**: 3-4 problems/day (Hard introduction)
- **Week 7-8**: 3-4 problems/day (Mixed difficulty)
- **Week 9-10**: 2-3 problems/day (Review focus)

### Mastery Indicators
- ✅ Can solve Easy problems in 10-15 minutes
- ✅ Can solve Medium problems in 20-25 minutes
- ✅ Can explain approach before coding
- ✅ Can identify patterns quickly
- ✅ Can handle follow-up questions

## 🏢 Company-Specific Focus

### Amazon (Backend Heavy)
```
Priority Packages: arrays/, trees/, graphs/, design/
Key Patterns: DFS/BFS, System Design, Optimization
Must Practice: Leadership Principles questions
```

### Google (Algorithm Heavy)
```
Priority Packages: dp/, graphs/, math/, backtracking/
Key Patterns: Complex algorithms, Mathematical thinking
Must Practice: Problem solving explanation
```

### Facebook/Meta (Balanced)
```
Priority Packages: trees/, strings/, arrays/, graphs/
Key Patterns: Social network problems, Scale questions
Must Practice: Product thinking
```

### Microsoft (Practical)
```
Priority Packages: arrays/, linkedlist/, trees/, design/
Key Patterns: Practical implementations, Clean code
Must Practice: Real-world applications
```

### Startups (Full-Stack)
```
Priority Packages: All packages (broader coverage)
Key Patterns: Versatility, Quick learning
Must Practice: Multiple language familiarity
```

## 🛠️ Tools and Resources

### Coding Environment
```bash
# Your local setup
IDE: VS Code with Java extensions
Build: javac -cp src -d bin src/package/ClassName.java
Run: java -cp bin package.ClassName
```

### Practice Platforms
- **LeetCode**: Primary platform for problems
- **HackerRank**: Additional practice
- **InterviewBit**: Structured courses
- **Pramp/Interviewing.io**: Mock interviews

### Time Management
```
Problem Analysis: 2-3 minutes
Approach Discussion: 3-5 minutes
Coding: 15-20 minutes  
Testing: 3-5 minutes
Optimization: 2-5 minutes
Total: 25-35 minutes per problem
```

## 📝 Progress Tracking

### Weekly Checklist Template
```
Week X Goals:
□ Package Focus: ________
□ Problems Completed: __ / __
□ Patterns Mastered: ________
□ Weak Areas Identified: ________
□ Mock Interview: __ / __
```

### Daily Log Template
```
Date: ________
Problems Solved: 
1. [Problem Name] - [Time] - [Difficulty] - [Pattern]
2. [Problem Name] - [Time] - [Difficulty] - [Pattern]

Today's Learning:
- Pattern: ________
- Key Insight: ________
- Mistake Made: ________
- Tomorrow's Focus: ________
```

## 🎯 Interview Day Checklist

### 24 Hours Before
- [ ] Review `SDE_INTERVIEW_QUICK_REFERENCE.md`
- [ ] Practice 2-3 medium problems
- [ ] Review your project experiences
- [ ] Prepare STAR format stories

### Morning Of
- [ ] Review top 20 critical problems
- [ ] Practice explaining solutions aloud
- [ ] Review complexity analysis
- [ ] Calm mindset preparation

### During Interview
- [ ] Ask clarifying questions
- [ ] Start with brute force
- [ ] Think aloud
- [ ] Test with examples
- [ ] Discuss optimizations

## 📚 Additional Resources

### Books
- "Cracking the Coding Interview" by Gayle McDowell
- "Elements of Programming Interviews" by Aziz, Prakash, Lee
- "Algorithm Design Manual" by Skiena

### Online Courses
- Grokking the Coding Interview (educative.io)
- AlgoExpert (algoexpert.io)
- Leetcode Explore Cards

### YouTube Channels
- NeetCode
- Tech With Tim
- Back To Back SWE

## 🎉 Success Stories

### Timeline Examples
```
Beginner (No CS background): 12-16 weeks
CS Graduate: 8-10 weeks  
Experienced Developer: 6-8 weeks
Career Changer: 10-12 weeks
```

### Success Indicators
- Consistent problem solving in time limits
- Pattern recognition improvement
- Code clarity and correctness
- Communication skills development

---

## 🚀 Getting Started

1. **Assess Your Level**: Take a few problems from each difficulty
2. **Choose Your Timeline**: Based on your background and target
3. **Follow the Plan**: Use the structured weekly approach
4. **Track Progress**: Use the templates provided
5. **Stay Consistent**: Daily practice is key to success

---

*Remember: The goal is not to memorize solutions, but to understand patterns and develop problem-solving intuition. Focus on building a strong foundation and the complex problems will become manageable.*

**Good luck with your interview preparation! 🎯**
