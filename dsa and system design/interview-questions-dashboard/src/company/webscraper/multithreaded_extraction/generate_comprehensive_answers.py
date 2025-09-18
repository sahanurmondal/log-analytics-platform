import json
import copy
import os
from pathlib import Path
from answer_schemas import get_schema_for_category, create_answer_template

class AnswerGenerator:
    def __init__(self, questions_file: str, dsa_folder: str):
        self.questions_file = questions_file
        self.dsa_folder = Path(dsa_folder)
        self.questions = []
        self.answers = []
        
    def load_questions(self):
        """Load questions from JSON file"""
        with open(self.questions_file, 'r', encoding='utf-8') as f:
            self.questions = json.load(f)
        print(f"Loaded {len(self.questions)} questions")
        
    def find_local_solution(self, title: str, category: str) -> str:
        """Find local solution file for DSA questions"""
        if category != "DSA":
            return ""
            
        # Clean title for file search
        clean_title = title.lower()
        for keyword in ["rotate", "array", "coin", "change", "binary", "search", "tree", "graph"]:
            if keyword in clean_title:
                # Search for matching files
                for file_path in self.dsa_folder.rglob("*.java"):
                    if keyword in file_path.name.lower():
                        return str(file_path.relative_to(Path.cwd()))
        return ""
    
    def generate_dsa_answer(self, question: dict) -> dict:
        """Generate DSA-specific answer"""
        template = create_answer_template(question)
        
        # Extract problem info from description
        description = question.get("description", "")
        
        # Basic problem understanding
        template["answer"]["problem_understanding"] = self.extract_problem_understanding(description)
        
        # Generate approaches based on common DSA patterns
        approaches = self.generate_dsa_approaches(question["title"], description)
        template["answer"]["approaches"] = approaches
        
        # Add insights and tips
        template["answer"]["key_insights"] = self.generate_key_insights(question["title"])
        template["answer"]["interview_tips"] = self.generate_interview_tips("DSA")
        template["answer"]["common_mistakes"] = self.generate_common_mistakes("DSA")
        
        # Add references
        template["references"]["local_solution"] = self.find_local_solution(question["title"], "DSA")
        template["references"]["leetcode"] = self.generate_leetcode_link(question["title"])
        template["references"]["blog_links"] = self.generate_blog_links(question["title"], "DSA")
        
        return template
    
    def generate_system_design_answer(self, question: dict) -> dict:
        """Generate System Design answer"""
        template = create_answer_template(question)
        
        title = question["title"].lower()
        
        # Common system design components
        if "notification" in title:
            template["answer"]["functional_requirements"] = [
                "Send push notifications to mobile devices",
                "Send email notifications",
                "Send SMS notifications",
                "User preference management",
                "Template management"
            ]
            template["answer"]["high_level_design"]["components"] = [
                "API Gateway", "Notification Service", "Message Queue", 
                "Push Service", "Email Service", "SMS Service", "Database"
            ]
        elif "chat" in title or "messaging" in title:
            template["answer"]["functional_requirements"] = [
                "Send and receive messages",
                "Online/offline status",
                "Group chat support",
                "Message history",
                "File sharing"
            ]
        elif "url" in title or "shortener" in title:
            template["answer"]["functional_requirements"] = [
                "Shorten long URLs",
                "Redirect to original URL",
                "Custom aliases",
                "Analytics and metrics",
                "URL expiration"
            ]
        
        # Add common system design elements
        template["answer"]["scalability_considerations"] = [
            "Horizontal scaling with load balancers",
            "Database sharding and replication",
            "Caching strategies (Redis/Memcached)",
            "CDN for static content",
            "Microservices architecture"
        ]
        
        template["answer"]["interview_tips"] = self.generate_interview_tips("System Design")
        template["references"]["blog_links"] = self.generate_blog_links(question["title"], "System Design")
        
        return template
    
    def generate_behavioral_answer(self, question: dict) -> dict:
        """Generate Behavioral question answer"""
        template = create_answer_template(question)
        
        title = question["title"].lower()
        
        # Analyze question type
        if "conflict" in title or "disagreement" in title:
            template["answer"]["question_analysis"] = "This question assesses conflict resolution and interpersonal skills"
            template["answer"]["star_framework"]["situation"] = "Describe a specific workplace conflict situation"
            template["answer"]["star_framework"]["task"] = "Explain your role and what needed to be resolved"
            template["answer"]["star_framework"]["action"] = "Detail the steps you took to address the conflict"
            template["answer"]["star_framework"]["result"] = "Share the positive outcome and lessons learned"
        elif "leadership" in title or "lead" in title:
            template["answer"]["question_analysis"] = "This question evaluates leadership potential and team management skills"
        elif "challenge" in title or "difficult" in title:
            template["answer"]["question_analysis"] = "This question tests problem-solving and resilience under pressure"
        
        template["answer"]["interview_tips"] = [
            "Use the STAR method (Situation, Task, Action, Result)",
            "Choose examples that highlight relevant skills for the role",
            "Be specific with numbers and outcomes where possible",
            "Show learning and growth from the experience",
            "Keep examples recent and relevant"
        ]
        
        template["references"]["blog_links"] = [
            "https://www.themuse.com/advice/star-interview-method",
            "https://www.indeed.com/career-advice/interviewing/how-to-use-the-star-interview-response-technique"
        ]
        
        return template
    
    def generate_lld_answer(self, question: dict) -> dict:
        """Generate Low Level Design answer"""
        template = create_answer_template(question)
        
        title = question["title"].lower()
        
        # Common LLD patterns
        if "parking" in title:
            template["answer"]["design_patterns_used"] = ["Strategy", "Factory", "Observer"]
            template["answer"]["class_diagram"]["classes"] = [
                "ParkingLot", "ParkingSpot", "Vehicle", "Car", "Motorcycle", "ParkingTicket"
            ]
        elif "elevator" in title:
            template["answer"]["design_patterns_used"] = ["State", "Command", "Observer"]
            template["answer"]["class_diagram"]["classes"] = [
                "ElevatorSystem", "Elevator", "Floor", "Request", "ElevatorController"
            ]
        elif "library" in title:
            template["answer"]["design_patterns_used"] = ["Factory", "Observer", "Strategy"]
            template["answer"]["class_diagram"]["classes"] = [
                "Library", "Book", "Member", "Librarian", "BookItem", "Reservation"
            ]
        
        template["answer"]["interview_tips"] = self.generate_interview_tips("LLD")
        template["references"]["blog_links"] = self.generate_blog_links(question["title"], "LLD")
        
        return template
    
    def generate_programming_language_answer(self, question: dict) -> dict:
        """Generate programming language specific answer"""
        template = create_answer_template(question)
        
        category = question["category"]
        title = question["title"].lower()
        
        if category == "JAVA":
            if "collection" in title:
                template["answer"]["concept_explanation"] = "Java Collections Framework provides data structures and algorithms"
                template["answer"]["syntax_examples"] = [
                    "List<String> list = new ArrayList<>()",
                    "Set<Integer> set = new HashSet<>()",
                    "Map<String, Integer> map = new HashMap<>()"
                ]
            elif "thread" in title or "concurrent" in title:
                template["answer"]["concept_explanation"] = "Java multithreading and concurrency concepts"
        elif category == "Javascript":
            if "async" in title or "promise" in title:
                template["answer"]["concept_explanation"] = "Asynchronous JavaScript programming with Promises and async/await"
                template["answer"]["syntax_examples"] = [
                    "async function fetchData() { return await fetch('/api/data'); }",
                    "promise.then(result => console.log(result)).catch(error => console.error(error));"
                ]
        
        template["answer"]["interview_tips"] = self.generate_interview_tips(category)
        template["references"]["blog_links"] = self.generate_blog_links(question["title"], category)
        
        return template
    
    def generate_general_answer(self, question: dict) -> dict:
        """Generate general answer for other categories"""
        template = create_answer_template(question)
        
        # Basic structure for general questions
        template["answer"]["direct_answer"] = f"Answer for: {question['title']}"
        template["answer"]["detailed_explanation"] = f"Detailed explanation for {question['category']} question"
        template["answer"]["interview_tips"] = self.generate_interview_tips(question["category"])
        
        return template
    
    def extract_problem_understanding(self, description: str) -> str:
        """Extract problem understanding from description"""
        lines = description.split('\\n')
        for line in lines:
            if line.strip() and not line.startswith('Input') and not line.startswith('Output'):
                return line.strip()
        return "Problem statement needs to be analyzed"
    
    def generate_dsa_approaches(self, title: str, description: str) -> list:
        """Generate DSA approaches based on title patterns"""
        title_lower = title.lower()
        approaches = []
        
        if "array" in title_lower:
            if "rotate" in title_lower:
                approaches.append({
                    "name": "Reverse Method",
                    "time_complexity": "O(n)",
                    "space_complexity": "O(1)",
                    "description": "Reverse entire array, then reverse parts",
                    "code": "// Reverse method implementation",
                    "pros": ["In-place solution", "Optimal time complexity"],
                    "cons": ["May be harder to understand initially"]
                })
            else:
                approaches.append({
                    "name": "Two Pointers",
                    "time_complexity": "O(n)",
                    "space_complexity": "O(1)",
                    "description": "Use two pointers to solve efficiently",
                    "code": "// Two pointers implementation",
                    "pros": ["Space efficient", "Intuitive approach"],
                    "cons": ["Limited to certain problem types"]
                })
        elif "tree" in title_lower:
            approaches.append({
                "name": "Recursive DFS",
                "time_complexity": "O(n)",
                "space_complexity": "O(h)",
                "description": "Recursive depth-first traversal",
                "code": "// Recursive DFS implementation",
                "pros": ["Clean and readable", "Natural for tree problems"],
                "cons": ["Stack overflow risk for deep trees"]
            })
        elif "dynamic" in title_lower or "dp" in title_lower:
            approaches.append({
                "name": "Bottom-up DP",
                "time_complexity": "O(n²)",
                "space_complexity": "O(n)",
                "description": "Build solution from smaller subproblems",
                "code": "// Bottom-up DP implementation",
                "pros": ["Avoids recursion overhead", "Clear iteration"],
                "cons": ["May use more memory"]
            })
        
        if not approaches:
            approaches.append({
                "name": "Brute Force",
                "time_complexity": "O(n²)",
                "space_complexity": "O(1)",
                "description": "Simple brute force approach",
                "code": "// Brute force implementation",
                "pros": ["Easy to understand and implement"],
                "cons": ["Not optimal for large inputs"]
            })
        
        return approaches
    
    def generate_key_insights(self, title: str) -> list:
        """Generate key insights based on title"""
        insights = []
        title_lower = title.lower()
        
        if "array" in title_lower:
            insights.append("Consider edge cases: empty array, single element")
            insights.append("Think about in-place vs extra space trade-offs")
        elif "string" in title_lower:
            insights.append("Consider character frequency and pattern matching")
            insights.append("Think about case sensitivity and special characters")
        elif "tree" in title_lower:
            insights.append("Consider null nodes and tree structure validation")
            insights.append("Think about recursive vs iterative approaches")
        
        insights.append("Always clarify requirements and constraints first")
        return insights
    
    def generate_interview_tips(self, category: str) -> list:
        """Generate category-specific interview tips"""
        tips = {
            "DSA": [
                "Start with brute force, then optimize",
                "Explain your thought process clearly",
                "Test with edge cases",
                "Analyze time and space complexity"
            ],
            "System Design": [
                "Start with requirements gathering",
                "Think about scale and constraints",
                "Consider trade-offs between consistency and availability",
                "Discuss monitoring and failure scenarios"
            ],
            "Behavioral": [
                "Use STAR method for structured answers",
                "Choose relevant examples for the role",
                "Show learning and growth",
                "Be specific with metrics and outcomes"
            ],
            "LLD": [
                "Start with requirements and use cases",
                "Identify key entities and relationships",
                "Apply appropriate design patterns",
                "Consider extensibility and maintainability"
            ],
            "JAVA": [
                "Understand OOP principles deeply",
                "Know collections framework well",
                "Understand memory management",
                "Be familiar with concurrency concepts"
            ]
        }
        return tips.get(category, ["Prepare thoroughly", "Practice explaining concepts clearly"])
    
    def generate_common_mistakes(self, category: str) -> list:
        """Generate common mistakes for each category"""
        mistakes = {
            "DSA": [
                "Not handling edge cases",
                "Incorrect complexity analysis",
                "Off-by-one errors in loops",
                "Not considering integer overflow"
            ],
            "System Design": [
                "Not asking clarifying questions",
                "Jumping to implementation too quickly",
                "Ignoring non-functional requirements",
                "Not considering failure scenarios"
            ]
        }
        return mistakes.get(category, ["Not practicing enough", "Not explaining clearly"])
    
    def generate_leetcode_link(self, title: str) -> str:
        """Generate probable LeetCode link"""
        # This is a simplified mapping - in real scenario, you'd have a proper mapping
        title_lower = title.lower()
        if "rotate array" in title_lower:
            return "https://leetcode.com/problems/rotate-array/"
        elif "coin change" in title_lower:
            return "https://leetcode.com/problems/coin-change/"
        elif "two sum" in title_lower:
            return "https://leetcode.com/problems/two-sum/"
        return ""
    
    def generate_blog_links(self, title: str, category: str) -> list:
        """Generate relevant blog links"""
        links = []
        
        if category == "DSA":
            links.extend([
                "https://www.geeksforgeeks.org/",
                "https://leetcode.com/discuss/",
                "https://www.interviewbit.com/"
            ])
        elif category == "System Design":
            links.extend([
                "https://www.educative.io/courses/grokking-the-system-design-interview",
                "https://github.com/donnemartin/system-design-primer",
                "https://medium.com/system-design-blog"
            ])
        
        return links
    
    def generate_answer_for_question(self, question: dict) -> dict:
        """Generate answer based on question category"""
        category = question.get("category", "Other")
        
        if category == "DSA":
            return self.generate_dsa_answer(question)
        elif category == "System Design":
            return self.generate_system_design_answer(question)
        elif category == "Behavioral":
            return self.generate_behavioral_answer(question)
        elif category == "LLD":
            return self.generate_lld_answer(question)
        elif category in ["JAVA", "Javascript", "React", "Frontend", "C++", "C"]:
            return self.generate_programming_language_answer(question)
        else:
            return self.generate_general_answer(question)
    
    def generate_all_answers(self):
        """Generate answers for all questions"""
        print(f"Generating answers for {len(self.questions)} questions...")
        
        for i, question in enumerate(self.questions):
            try:
                answer = self.generate_answer_for_question(question)
                self.answers.append(answer)
                
                if (i + 1) % 100 == 0:
                    print(f"Generated {i + 1} answers...")
                    
            except Exception as e:
                print(f"Error generating answer for question {question.get('question_number', 'unknown')}: {e}")
                # Add a minimal answer to keep going
                template = create_answer_template(question)
                template["answer"]["direct_answer"] = f"Answer pending for: {question.get('title', 'Unknown')}"
                self.answers.append(template)
        
        print(f"Generated {len(self.answers)} answers total")
    
    def save_answers(self, output_file: str):
        """Save answers to JSON file"""
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(self.answers, f, indent=2, ensure_ascii=False)
        print(f"Answers saved to {output_file}")
    
    def run(self, output_file: str):
        """Run the complete answer generation process"""
        self.load_questions()
        self.generate_all_answers()
        self.save_answers(output_file)

def main():
    questions_file = "complete_questions_dataset.json"
    dsa_folder = "../../../dsa"
    output_file = "comprehensive_answers.json"
    
    generator = AnswerGenerator(questions_file, dsa_folder)
    generator.run(output_file)
    
    print(f"\\nAnswer generation complete!")
    print(f"Generated answers for {len(generator.answers)} questions")
    
    # Print category breakdown
    category_counts = {}
    for answer in generator.answers:
        cat = answer.get("category", "Unknown")
        category_counts[cat] = category_counts.get(cat, 0) + 1
    
    print("\\nAnswers by category:")
    for category, count in sorted(category_counts.items(), key=lambda x: x[1], reverse=True):
        print(f"  {category}: {count}")

if __name__ == "__main__":
    main()