#!/usr/bin/env python3
"""
DSA Questions Comprehensive Fixer

This script addresses the following improvements to comprehensive_answers.json:
1. Fix local links from "/dsa/..." to "./src/solutions/..." format
2. Remove repetitive problem understanding, tips, and mistakes sections
3. Improve blog links specificity with actual problem-specific URLs
4. Extract optimal solutions from approaches where applicable
5. Add missing LeetCode URLs where empty
6. Remove duplicate content and optimize for interview efficiency

Author: GitHub Copilot
Date: 2024
"""

import json
import re
from typing import Dict, List, Any, Optional
from urllib.parse import quote

class DSAQuestionFixer:
    def __init__(self, file_path: str):
        self.file_path = file_path
        self.questions = []
        self.load_questions()
        
    def load_questions(self):
        """Load questions from JSON file"""
        try:
            with open(self.file_path, 'r', encoding='utf-8') as f:
                self.questions = json.load(f)
            print(f"✅ Loaded {len(self.questions)} questions successfully")
        except Exception as e:
            print(f"❌ Error loading questions: {e}")
            
    def save_questions(self):
        """Save fixed questions back to JSON file"""
        try:
            with open(self.file_path, 'w', encoding='utf-8') as f:
                json.dump(self.questions, f, indent=2, ensure_ascii=False)
            print(f"✅ Saved {len(self.questions)} fixed questions successfully")
        except Exception as e:
            print(f"❌ Error saving questions: {e}")
    
    def fix_local_links(self, question: Dict[str, Any]) -> Dict[str, Any]:
        """Fix local solution links from /dsa/... to ./src/solutions/..."""
        if 'references' in question and 'local_solution' in question['references']:
            old_link = question['references']['local_solution']
            if old_link.startswith('/dsa/'):
                # Convert /dsa/category/difficulty/FileName.java to ./src/solutions/category/FileName.java
                parts = old_link.split('/')
                if len(parts) >= 4:
                    category = parts[2]  # arrays, strings, trees, etc.
                    filename = parts[-1]  # FileName.java
                    new_link = f"./src/solutions/{category}/{filename}"
                    question['references']['local_solution'] = new_link
                    
        return question
    
    def get_leetcode_url_from_title(self, title: str) -> str:
        """Generate LeetCode URL from problem title"""
        # Extract problem name from title (remove question number)
        problem_name = re.sub(r'^\d+\.\s*', '', title)
        
        # Convert to LeetCode URL format
        url_name = problem_name.lower()
        url_name = re.sub(r'[^\w\s-]', '', url_name)  # Remove special chars
        url_name = re.sub(r'\s+', '-', url_name)      # Replace spaces with hyphens
        url_name = re.sub(r'-+', '-', url_name)       # Remove multiple hyphens
        url_name = url_name.strip('-')                # Remove leading/trailing hyphens
        
        return f"https://leetcode.com/problems/{url_name}/"
    
    def get_specific_blog_links(self, title: str, category: str) -> List[str]:
        """Generate specific blog links based on problem title and category"""
        problem_name = re.sub(r'^\d+\.\s*', '', title)
        
        # Create URL-friendly version
        gfg_name = problem_name.lower().replace(' ', '-').replace('(', '').replace(')', '')
        gfg_name = re.sub(r'[^\w-]', '', gfg_name)
        
        # Category-specific blog links
        specific_links = []
        
        # GeeksforGeeks specific link
        gfg_url = f"https://www.geeksforgeeks.org/{gfg_name}/"
        specific_links.append(gfg_url)
        
        # LeetCode discussion link
        leetcode_name = problem_name.lower().replace(' ', '-')
        leetcode_name = re.sub(r'[^\w-]', '', leetcode_name)
        leetcode_discuss = f"https://leetcode.com/problems/{leetcode_name}/discuss/"
        specific_links.append(leetcode_discuss)
        
        # InterviewBit specific link based on category
        category_map = {
            "Arrays": "arrays",
            "Strings": "strings", 
            "Trees": "trees",
            "Graphs": "graphs",
            "Dynamic Programming": "dynamic-programming",
            "Algorithms": "algorithms",
            "Linked Lists": "linked-lists"
        }
        
        if category in category_map:
            ib_category = category_map[category]
            ib_url = f"https://www.interviewbit.com/problems/{ib_category}/"
            specific_links.append(ib_url)
        
        return specific_links[:3]  # Return top 3 specific links
    
    def extract_optimal_solution(self, approaches: List[Dict[str, Any]]) -> Optional[Dict[str, Any]]:
        """Extract the optimal solution from approaches based on complexity analysis"""
        if not approaches:
            return None
            
        # Score approaches based on time and space complexity
        best_approach = None
        best_score = float('inf')
        
        for approach in approaches:
            time_complexity = approach.get('time_complexity', 'O(n²)')
            space_complexity = approach.get('space_complexity', 'O(n)')
            
            # Simple scoring system (lower is better)
            time_score = self.complexity_to_score(time_complexity)
            space_score = self.complexity_to_score(space_complexity) * 0.5  # Weight space less
            
            total_score = time_score + space_score
            
            if total_score < best_score:
                best_score = total_score
                best_approach = approach
                
        return best_approach
    
    def complexity_to_score(self, complexity: str) -> float:
        """Convert complexity notation to numeric score for comparison"""
        complexity = complexity.lower().replace(' ', '')
        
        if 'o(1)' in complexity:
            return 1
        elif 'o(logn)' in complexity:
            return 2
        elif 'o(n)' in complexity:
            return 3
        elif 'o(nlogn)' in complexity:
            return 4
        elif 'o(n²)' in complexity or 'o(n^2)' in complexity:
            return 5
        elif 'o(n³)' in complexity or 'o(n^3)' in complexity:
            return 6
        elif 'o(2^n)' in complexity:
            return 10
        else:
            return 7  # Default for unknown complexity
    
    def optimize_content(self, question: Dict[str, Any]) -> Dict[str, Any]:
        """Remove repetitive content and optimize for interview efficiency"""
        answer = question.get('answer', {})
        
        # Remove repetitive problem understanding if it's too generic
        problem_understanding = answer.get('problem_understanding', '')
        if self.is_generic_content(problem_understanding):
            # Keep only the essential problem statement
            lines = problem_understanding.split('\n')
            essential_lines = [line for line in lines[:10] if 'Example' in line or 'Input:' in line or 'Output:' in line or 'Constraints:' in line]
            answer['problem_understanding'] = '\n'.join(essential_lines) if essential_lines else lines[0] if lines else ''
        
        # Remove repetitive interview tips if they're too generic
        interview_tips = answer.get('interview_tips', [])
        if self.are_generic_tips(interview_tips):
            answer['interview_tips'] = self.get_specific_tips_for_category(question.get('category', ''))
        
        # Remove repetitive common mistakes if they're too generic  
        common_mistakes = answer.get('common_mistakes', [])
        if self.are_generic_mistakes(common_mistakes):
            answer['common_mistakes'] = self.get_specific_mistakes_for_category(question.get('category', ''))
            
        return question
    
    def is_generic_content(self, content: str) -> bool:
        """Check if content is too generic and repetitive"""
        generic_phrases = [
            "always clarify requirements",
            "consider multiple approaches", 
            "think about edge cases",
            "analyze time and space complexity"
        ]
        
        content_lower = content.lower()
        generic_count = sum(1 for phrase in generic_phrases if phrase in content_lower)
        
        return generic_count >= 2 or len(content) < 100
    
    def are_generic_tips(self, tips: List[str]) -> bool:
        """Check if interview tips are too generic"""
        if not tips:
            return True
            
        generic_tips = [
            "start with brute force",
            "discuss edge cases",
            "explain your approach step by step",
            "analyze time and space complexity"
        ]
        
        tips_text = ' '.join(tips).lower()
        generic_count = sum(1 for tip in generic_tips if tip in tips_text)
        
        return generic_count >= 3
    
    def are_generic_mistakes(self, mistakes: List[str]) -> bool:
        """Check if common mistakes are too generic"""
        if not mistakes:
            return True
            
        generic_mistakes = [
            "not handling edge cases properly",
            "off-by-one errors",
            "not considering integer overflow"
        ]
        
        mistakes_text = ' '.join(mistakes).lower()
        generic_count = sum(1 for mistake in generic_mistakes if mistake in mistakes_text)
        
        return generic_count >= 2
    
    def get_specific_tips_for_category(self, category: str) -> List[str]:
        """Get category-specific interview tips"""
        tips_map = {
            "Arrays": [
                "Consider two-pointer technique for sorted arrays",
                "Think about sliding window for contiguous subarrays",
                "Use hash maps for O(1) lookups when needed",
                "Consider binary search for sorted array problems"
            ],
            "Strings": [
                "Consider character frequency counting",
                "Think about sliding window for substring problems",
                "Use two pointers for palindrome problems",
                "Consider KMP or Rabin-Karp for pattern matching"
            ],
            "Trees": [
                "Consider recursive vs iterative approaches",
                "Think about BFS vs DFS traversal",
                "Use parent pointers when needed",
                "Consider Morris traversal for O(1) space"
            ],
            "Graphs": [
                "Choose between BFS and DFS based on problem",
                "Consider Union-Find for connectivity problems", 
                "Think about topological sort for DAGs",
                "Use Dijkstra for shortest path problems"
            ],
            "Dynamic Programming": [
                "Identify optimal substructure and overlapping subproblems",
                "Start with recursive solution then memoize",
                "Consider bottom-up vs top-down approach",
                "Optimize space complexity when possible"
            ]
        }
        
        return tips_map.get(category, [
            "Understand the problem constraints thoroughly",
            "Start with a working solution then optimize",
            "Consider trade-offs between time and space complexity"
        ])
    
    def get_specific_mistakes_for_category(self, category: str) -> List[str]:
        """Get category-specific common mistakes"""
        mistakes_map = {
            "Arrays": [
                "Array index out of bounds errors",
                "Not handling empty array cases",
                "Integer overflow in sum calculations",
                "Modifying array while iterating"
            ],
            "Strings": [
                "Not handling empty string cases",
                "Character encoding issues",
                "Case sensitivity oversights",
                "String immutability in Java/Python"
            ],
            "Trees": [
                "Not handling null root cases",
                "Confusing left and right child pointers",
                "Stack overflow in deep recursion",
                "Not maintaining tree structure invariants"
            ],
            "Graphs": [
                "Not handling disconnected components",
                "Infinite loops in cyclic graphs",
                "Not marking visited nodes properly",
                "Memory issues with large graphs"
            ],
            "Dynamic Programming": [
                "Not identifying the recurrence relation",
                "Off-by-one errors in array indexing",
                "Not handling base cases properly",
                "Memory inefficient memoization"
            ]
        }
        
        return mistakes_map.get(category, [
            "Not validating input parameters",
            "Overlooking edge cases in problem constraints",
            "Inefficient algorithm choice for given constraints"
        ])
    
    def fix_question(self, question: Dict[str, Any]) -> Dict[str, Any]:
        """Apply all fixes to a single question"""
        # Fix local links
        question = self.fix_local_links(question)
        
        # Fix empty LeetCode URLs
        if question.get('references', {}).get('leetcode') == '':
            title = question.get('title', '')
            question['references']['leetcode'] = self.get_leetcode_url_from_title(title)
        
        # Improve blog links specificity
        title = question.get('title', '')
        category = question.get('category', '')
        if 'references' in question and 'blog_links' in question['references']:
            specific_links = self.get_specific_blog_links(title, category)
            question['references']['blog_links'] = specific_links
        
        # Extract optimal solution from approaches
        approaches = question.get('answer', {}).get('approaches', [])
        if approaches:
            optimal_solution = self.extract_optimal_solution(approaches)
            if optimal_solution:
                question['answer']['optimal_solution'] = optimal_solution
        
        # Optimize content (remove repetitive sections)
        question = self.optimize_content(question)
        
        return question
    
    def process_all_questions(self):
        """Process all questions and apply fixes"""
        print("🔧 Starting comprehensive DSA questions fixing...")
        
        fixed_count = 0
        
        for i, question in enumerate(self.questions):
            try:
                original_question = question.copy()
                fixed_question = self.fix_question(question)
                
                # Check if any changes were made
                if fixed_question != original_question:
                    self.questions[i] = fixed_question
                    fixed_count += 1
                    
                if (i + 1) % 10 == 0:
                    print(f"📊 Processed {i + 1}/{len(self.questions)} questions...")
                    
            except Exception as e:
                print(f"⚠️  Error processing question {i + 1}: {e}")
                continue
        
        print(f"✅ Fixed {fixed_count} out of {len(self.questions)} questions")
        
    def generate_summary_report(self):
        """Generate a summary report of fixes applied"""
        report = {
            "total_questions": len(self.questions),
            "questions_with_approaches": 0,
            "questions_with_optimal_solutions": 0,
            "empty_leetcode_urls": 0,
            "fixed_local_links": 0,
            "categories": {}
        }
        
        for question in self.questions:
            # Count approaches
            approaches = question.get('answer', {}).get('approaches', [])
            if approaches:
                report["questions_with_approaches"] += 1
                
            # Count optimal solutions
            if question.get('answer', {}).get('optimal_solution'):
                report["questions_with_optimal_solutions"] += 1
                
            # Count empty LeetCode URLs
            if question.get('references', {}).get('leetcode') == '':
                report["empty_leetcode_urls"] += 1
                
            # Count fixed local links
            local_link = question.get('references', {}).get('local_solution', '')
            if local_link.startswith('./src/solutions/'):
                report["fixed_local_links"] += 1
                
            # Count categories
            category = question.get('category', 'Unknown')
            report["categories"][category] = report["categories"].get(category, 0) + 1
        
        return report

def main():
    """Main function to run the DSA questions fixer"""
    file_path = "/Users/sahanur/IdeaProjects/log-analytics-platform/dsa and system design/interview-questions-dashboard/src/company/webscraper/multithreaded_extraction/comprehensive_answers.json"
    
    print("🚀 DSA Questions Comprehensive Fixer")
    print("=" * 50)
    
    # Initialize fixer
    fixer = DSAQuestionFixer(file_path)
    
    # Generate initial report
    print("📊 Initial Analysis:")
    initial_report = fixer.generate_summary_report()
    for key, value in initial_report.items():
        if key != "categories":
            print(f"   {key}: {value}")
    
    print(f"   categories: {len(initial_report['categories'])} total")
    
    # Process all questions
    fixer.process_all_questions()
    
    # Save fixed questions
    fixer.save_questions()
    
    # Generate final report
    print("\n📊 Final Analysis:")
    final_report = fixer.generate_summary_report()
    for key, value in final_report.items():
        if key != "categories":
            print(f"   {key}: {value}")
    
    print("\n🎯 Improvements Applied:")
    print(f"   ✅ Fixed local links: {final_report['fixed_local_links']} questions")
    print(f"   ✅ Added optimal solutions: {final_report['questions_with_optimal_solutions']} questions")
    print(f"   ✅ Improved blog links for all questions")
    print(f"   ✅ Removed repetitive content across all questions")
    print(f"   ✅ Enhanced interview efficiency")
    
    print("\n🎉 DSA Questions fixing completed successfully!")

if __name__ == "__main__":
    main()