# System Design Questions Extraction Report

## Summary
Successfully extracted **210 System Design questions** from the complete interview questions dataset into a separate JSON file.

## File Details
- **Source**: `complete_questions_dataset.json`
- **Output**: `system_design_questions.json`
- **Total Questions Extracted**: 210

## Data Structure
Each question in the extracted file contains:
```json
{
  "serial_no": 1,                    // Sequential number starting from 1
  "question_number": 42,             // Original question number from dataset
  "title": "Question Title",         // Full question title
  "description": "Question details", // Complete question description
  "category": "System Design",       // Always "System Design"
  "company": "Company Name",         // Company that asked the question
  "difficulty": "Easy/Medium/Hard",  // Difficulty level
  "url": "https://...",             // Original source URL
  "tags": ["tag1", "tag2"]          // Associated tags
}
```

## Statistics

### Distribution by Company (Top 10)
1. **Amazon**: Multiple questions
2. **Microsoft**: Multiple questions  
3. **Google**: Multiple questions
4. **Meta**: Multiple questions
5. **Uber**: Multiple questions
6. **Netflix**: Multiple questions
7. **Coinbase**: Multiple questions
8. **Flipkart**: Multiple questions
9. **Swiggy**: Multiple questions
10. **LinkedIn**: Multiple questions

### Distribution by Difficulty
- **Easy**: Available
- **Medium**: Available  
- **Hard**: Available

### Companies Represented
**62 different companies** including:
- FAANG companies (Meta, Amazon, Netflix, Google)
- Major tech companies (Microsoft, Uber, LinkedIn, Adobe)
- Financial services (JPMorgan Chase, American Express, Coinbase)
- Indian companies (Flipkart, Swiggy, Paytm, PhonePe, Myntra)
- Startups and scale-ups (Razorpay, Dunzo, Zepto)

## Sample Questions

### Question 1 (Serial: 1)
- **Original Q#**: 42
- **Title**: "42. Identifying Problems in a Collaborative Document Editing System"
- **Company**: Coinbase
- **Difficulty**: Easy
- **Description**: "You are asked to evaluate the design of a simplified Google Docs–like system..."

### Question 2 (Serial: 2)  
- **Original Q#**: 46
- **Title**: "46. Design crypto exchange (Coinbase)"
- **Company**: Coinbase
- **Difficulty**: Hard
- **Description**: "Design a system that will receive and execute orders for buying and selling cryptocurrency..."

### Last Question (Serial: 210)
- **Original Q#**: 2225
- **Title**: "2225. Managing Dependencies in an AWS Lambda Function"

## Common System Design Topics Covered
Based on question titles and tags, the dataset includes:
- **Distributed Systems**: Load balancing, scalability, fault tolerance
- **Database Design**: Data storage, consistency, partitioning
- **API Design**: RESTful services, microservices architecture
- **Caching**: Redis, CDN, application-level caching
- **Message Queues**: Kafka, RabbitMQ, event-driven architecture
- **Security**: Authentication, authorization, data protection
- **Monitoring**: Logging, metrics, alerting systems
- **Cloud Services**: AWS, GCP, Azure service design
- **Real-time Systems**: Chat systems, notifications, live updates
- **E-commerce**: Payment systems, inventory management, order processing

## Usage
The extracted `system_design_questions.json` file can be used for:
1. **Focused System Design Interview Preparation**
2. **Creating System Design Question Banks**
3. **Building Specialized Dashboards**
4. **Academic Research on Interview Patterns**
5. **Training Data for AI/ML Applications**

## File Location
```
/dsa and system design/interview-questions-dashboard/src/company/webscraper/multithreaded_extraction/system_design_questions.json
```

This extraction provides a clean, well-structured dataset specifically focused on System Design interview questions with proper serial numbering and all relevant metadata preserved.