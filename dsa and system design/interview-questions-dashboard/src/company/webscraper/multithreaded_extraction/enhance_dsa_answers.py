import json
import os
import re
from pathlib import Path
from answer_schemas import get_schema_for_category, create_answer_template

class EnhancedDSAAnswerGenerator:
    def __init__(self, questions_file: str, dsa_folder: str, comprehensive_answers_file: str):
        self.questions_file = questions_file
        self.dsa_folder = Path(dsa_folder)
        self.comprehensive_answers_file = comprehensive_answers_file
        self.questions = []
        self.comprehensive_answers = []
        self.dsa_files_map = {}
        
    def load_data(self):
        """Load questions and existing comprehensive answers"""
        with open(self.questions_file, 'r', encoding='utf-8') as f:
            self.questions = json.load(f)
        
        with open(self.comprehensive_answers_file, 'r', encoding='utf-8') as f:
            self.comprehensive_answers = json.load(f)
            
        print(f"Loaded {len(self.questions)} questions")
        print(f"Loaded {len(self.comprehensive_answers)} comprehensive answers")
        
    def build_dsa_files_map(self):
        """Build a mapping of problem patterns to actual DSA files"""
        self.dsa_files_map = {}
        
        # Search all Java files in DSA folder
        for java_file in self.dsa_folder.rglob("*.java"):
            if java_file.is_file():
                file_content = self.read_java_file(java_file)
                if file_content:
                    # Extract problem name and keywords
                    filename = java_file.stem
                    keywords = self.extract_keywords_from_filename(filename)
                    
                    # Store file info
                    relative_path = java_file.relative_to(self.dsa_folder.parent)
                    self.dsa_files_map[filename.lower()] = {
                        'file_path': str(relative_path),
                        'content': file_content,
                        'keywords': keywords,
                        'difficulty': self.get_difficulty_from_path(java_file)
                    }
        
        print(f"Mapped {len(self.dsa_files_map)} DSA solution files")
        
    def read_java_file(self, file_path: Path) -> str:
        """Read Java file content"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return f.read()
        except Exception as e:
            print(f"Error reading {file_path}: {e}")
            return ""
    
    def extract_keywords_from_filename(self, filename: str) -> list:
        """Extract keywords from filename for matching"""
        # Split camelCase and convert to keywords
        keywords = re.findall(r'[A-Z][a-z]*|[a-z]+', filename)
        return [kw.lower() for kw in keywords]
    
    def get_difficulty_from_path(self, file_path: Path) -> str:
        """Determine difficulty from file path"""
        path_str = str(file_path).lower()
        if '/easy/' in path_str:
            return 'Easy'
        elif '/medium/' in path_str:
            return 'Medium'
        elif '/hard/' in path_str:
            return 'Hard'
        return 'Medium'  # Default
    
    def find_matching_dsa_file(self, question_title: str):
        """Find the best matching DSA file for a question"""
        title_lower = question_title.lower()
        title_words = re.findall(r'[a-z]+', title_lower)
        
        best_match = None
        best_score = 0
        
        for filename, file_info in self.dsa_files_map.items():
            score = 0
            
            # Check if filename words appear in title
            for keyword in file_info['keywords']:
                if keyword in title_words:
                    score += 2
                    
            # Check if title words appear in filename
            for word in title_words:
                if word in filename:
                    score += 1
                    
            # Special keyword matching
            if 'rotate' in title_lower and 'rotate' in filename:
                score += 5
            if 'array' in title_lower and 'array' in filename:
                score += 3
            if 'coin' in title_lower and 'coin' in filename:
                score += 5
            if 'change' in title_lower and 'change' in filename:
                score += 3
                
            if score > best_score:
                best_score = score
                best_match = file_info
                
        return best_match if best_score >= 2 else None
    
    def extract_methods_from_java(self, java_content: str) -> list:
        """Extract method implementations from Java file"""
        methods = []
        
        # Find all public methods with comments
        method_pattern = r'/\*\*\s*\n.*?\*/\s*\n\s*public\s+[\w<>\[\]]+\s+(\w+)\s*\([^)]*\)\s*\{[^}]*(?:\{[^}]*\}[^}]*)*\}'
        matches = re.finditer(method_pattern, java_content, re.DOTALL)
        
        for match in matches:
            full_method = match.group(0)
            method_name = match.group(1)
            
            # Extract comment for description
            comment_match = re.search(r'/\*\*(.*?)\*/', full_method, re.DOTALL)
            description = ""
            if comment_match:
                comment_text = comment_match.group(1)
                # Clean up comment
                description = re.sub(r'\s*\*\s*', ' ', comment_text).strip()
                
            # Extract time/space complexity from comment
            time_complexity = self.extract_complexity(description, 'time')
            space_complexity = self.extract_complexity(description, 'space')
            
            # Clean method code
            method_code = full_method.replace('/**', '//').replace('*/', '')
            
            methods.append({
                'name': self.get_method_display_name(method_name, description),
                'method_name': method_name,
                'time_complexity': time_complexity,
                'space_complexity': space_complexity,
                'description': description,
                'code': method_code.strip()
            })
            
        return methods
    
    def extract_complexity(self, text: str, comp_type: str) -> str:
        """Extract time or space complexity from text"""
        pattern = f'{comp_type}[:\\s]*O\\([^)]+\\)'
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            complexity_match = re.search(r'O\([^)]+\)', match.group(0))
            if complexity_match:
                return complexity_match.group(0)
        
        # Default complexities
        if comp_type.lower() == 'time':
            return 'O(n)'
        else:
            return 'O(1)'
    
    def get_method_display_name(self, method_name: str, description: str) -> str:
        """Get a display name for the method"""
        name_map = {
            'rotateExtra': 'Extra Space Approach',
            'rotate': 'Reverse Method (Optimal)',
            'rotateCyclic': 'Cyclic Replacements',
            'coinChange': 'Dynamic Programming',
            'coinChangeDFS': 'DFS with Memoization',
            'twoSum': 'Hash Map Approach',
            'twoSumTwoPointer': 'Two Pointer Approach'
        }
        
        if method_name in name_map:
            return name_map[method_name]
            
        # Generate name from method name
        words = re.findall(r'[A-Z][a-z]*|[a-z]+', method_name)
        return ' '.join(words).title()
    
    def generate_enhanced_dsa_answer(self, question: dict, dsa_file_info: dict) -> dict:
        """Generate enhanced DSA answer using actual code"""
        
        # Extract methods from Java file
        methods = self.extract_methods_from_java(dsa_file_info['content'])
        
        # Create the answer structure
        answer = {
            "question_number": question['question_number'],
            "title": question['title'],
            "difficulty": dsa_file_info.get('difficulty', 'Medium'),
            "question_type": "coding",
            "category": self.get_dsa_category(question['title'], dsa_file_info['file_path']),
            "answer": {
                "problem_understanding": self.extract_problem_understanding(question.get('description', '')),
                "key_insights": self.generate_key_insights(question['title'], dsa_file_info['content']),
                "approaches": [],
                "interview_tips": self.generate_interview_tips(question['title']),
                "common_mistakes": self.generate_common_mistakes(question['title'])
            },
            "references": {
                "leetcode": self.generate_leetcode_link(question['title']),
                "local_solution": f"/{dsa_file_info['file_path']}",
                "similar_problems": self.generate_similar_problems(question['title']),
                "blog_links": self.generate_blog_links(question['title'])
            }
        }
        
        # Add approaches from extracted methods
        for method in methods:
            approach = {
                "name": method['name'],
                "time_complexity": method['time_complexity'],
                "space_complexity": method['space_complexity'],
                "description": method['description'],
                "code": method['code']
            }
            answer["answer"]["approaches"].append(approach)
            
        return answer
    
    def get_dsa_category(self, title: str, file_path: str) -> str:
        """Determine DSA category from title and file path"""
        path_lower = file_path.lower()
        title_lower = title.lower()
        
        if 'arrays' in path_lower or 'array' in title_lower:
            return 'Arrays'
        elif 'linkedlist' in path_lower or 'linked' in title_lower:
            return 'Linked Lists'
        elif 'trees' in path_lower or 'tree' in title_lower:
            return 'Trees'
        elif 'graphs' in path_lower or 'graph' in title_lower:
            return 'Graphs'
        elif 'dp' in path_lower or 'dynamic' in title_lower:
            return 'Dynamic Programming'
        elif 'strings' in path_lower or 'string' in title_lower:
            return 'Strings'
        elif 'stacks' in path_lower or 'stack' in title_lower:
            return 'Stacks'
        elif 'queues' in path_lower or 'queue' in title_lower:
            return 'Queues'
        else:
            return 'Algorithms'
    
    def extract_problem_understanding(self, description: str) -> str:
        """Extract problem understanding from description"""
        if not description:
            return "This is a coding problem that requires algorithmic thinking and implementation skills."
        
        lines = description.split('\\n')
        for line in lines:
            line = line.strip()
            if line and not line.startswith('Input') and not line.startswith('Output') and not line.startswith('Example'):
                return line
        return "This is a coding problem that requires algorithmic thinking and implementation skills."
    
    def generate_key_insights(self, title: str, java_content: str) -> list:
        """Generate key insights from title and code"""
        insights = []
        title_lower = title.lower()
        content_lower = java_content.lower()
        
        # Extract insights from comments
        follow_up_pattern = r'follow-up[s]?[:\-]([^*]*)'
        follow_ups = re.findall(follow_up_pattern, content_lower, re.IGNORECASE)
        for follow_up in follow_ups:
            clean_follow_up = re.sub(r'[/*\-]+', '', follow_up).strip()
            if clean_follow_up:
                insights.append(clean_follow_up)
        
        # Add general insights based on title
        if 'array' in title_lower:
            insights.append("Consider edge cases: empty array, single element")
            insights.append("Think about in-place vs extra space trade-offs")
        elif 'rotate' in title_lower:
            insights.append("Handle edge cases: k > array length using k = k % n")
            insights.append("Multiple approaches exist: extra space, reverse method, and cyclic replacements")
        
        if not insights:
            insights.append("Always clarify requirements and constraints first")
            insights.append("Consider multiple approaches and their trade-offs")
            
        return insights
    
    def generate_interview_tips(self, title: str) -> list:
        """Generate interview tips"""
        return [
            "Start with brute force (extra space) then optimize to O(1) space",
            "Discuss edge cases and constraints clearly",
            "Explain your approach step by step with examples",
            "Analyze time and space complexity for each approach"
        ]
    
    def generate_common_mistakes(self, title: str) -> list:
        """Generate common mistakes"""
        mistakes = [
            "Not handling edge cases properly",
            "Off-by-one errors in array indexing",
            "Not considering integer overflow for large inputs"
        ]
        
        title_lower = title.lower()
        if 'rotate' in title_lower:
            mistakes.extend([
                "Forgetting to handle k > array.length case",
                "Not handling null or empty array cases"
            ])
            
        return mistakes
    
    def generate_leetcode_link(self, title: str) -> str:
        """Generate LeetCode link"""
        title_lower = title.lower()
        
        link_map = {
            'rotate array': 'https://leetcode.com/problems/rotate-array/',
            'coin change': 'https://leetcode.com/problems/coin-change/',
            'two sum': 'https://leetcode.com/problems/two-sum/',
            'best time to buy': 'https://leetcode.com/problems/best-time-to-buy-and-sell-stock/',
            'contains duplicate': 'https://leetcode.com/problems/contains-duplicate/',
            'majority element': 'https://leetcode.com/problems/majority-element/'
        }
        
        for key, link in link_map.items():
            if key in title_lower:
                return link
                
        return ""
    
    def generate_similar_problems(self, title: str) -> list:
        """Generate similar problems"""
        title_lower = title.lower()
        
        if 'rotate array' in title_lower:
            return ["Rotate List (LinkedList)", "Rotate Image (2D Array)"]
        elif 'coin change' in title_lower:
            return ["Coin Change 2 (Number of ways)", "Perfect Squares", "Minimum Cost For Tickets"]
        else:
            return []
    
    def generate_blog_links(self, title: str) -> list:
        """Generate blog links"""
        return [
            "https://www.geeksforgeeks.org/",
            "https://leetcode.com/discuss/",
            "https://www.interviewbit.com/"
        ]
    
    def update_dsa_answers(self):
        """Update all DSA answers in comprehensive_answers"""
        updated_count = 0
        
        for i, answer in enumerate(self.comprehensive_answers):
            if answer.get('category') == 'DSA':
                question_num = answer['question_number']
                
                # Find corresponding question
                question = None
                for q in self.questions:
                    if q['question_number'] == question_num:
                        question = q
                        break
                
                if question:
                    # Find matching DSA file
                    dsa_file_info = self.find_matching_dsa_file(question['title'])
                    
                    if dsa_file_info:
                        # Generate enhanced answer
                        enhanced_answer = self.generate_enhanced_dsa_answer(question, dsa_file_info)
                        
                        # Update the comprehensive answer
                        self.comprehensive_answers[i] = enhanced_answer
                        updated_count += 1
                        
                        if updated_count % 10 == 0:
                            print(f"Updated {updated_count} DSA answers...")
                    else:
                        print(f"No DSA file found for question {question_num}: {question['title']}")
        
        print(f"Updated {updated_count} DSA answers with actual code implementations")
    
    def save_updated_answers(self):
        """Save updated comprehensive answers"""
        with open(self.comprehensive_answers_file, 'w', encoding='utf-8') as f:
            json.dump(self.comprehensive_answers, f, indent=2, ensure_ascii=False)
        print(f"Saved updated answers to {self.comprehensive_answers_file}")
    
    def run(self):
        """Run the enhancement process"""
        print("Starting DSA answer enhancement...")
        self.load_data()
        self.build_dsa_files_map()
        self.update_dsa_answers()
        self.save_updated_answers()
        print("DSA answer enhancement complete!")

def main():
    questions_file = "complete_questions_dataset.json"
    dsa_folder = "/Users/sahanur/IdeaProjects/log-analytics-platform/dsa and system design/dsa"
    comprehensive_answers_file = "comprehensive_answers.json"
    
    enhancer = EnhancedDSAAnswerGenerator(questions_file, dsa_folder, comprehensive_answers_file)
    enhancer.run()

if __name__ == "__main__":
    main()