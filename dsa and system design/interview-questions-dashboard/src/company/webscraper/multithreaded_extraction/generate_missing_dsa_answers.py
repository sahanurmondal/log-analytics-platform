import json
import re
from pathlib import Path

class DSADescriptionAnswerGenerator:
    def __init__(self, questions_file: str, comprehensive_answers_file: str):
        self.questions_file = questions_file
        self.comprehensive_answers_file = comprehensive_answers_file
        self.questions = []
        self.comprehensive_answers = []
        
    def load_data(self):
        """Load questions and existing comprehensive answers"""
        with open(self.questions_file, 'r', encoding='utf-8') as f:
            self.questions = json.load(f)
        
        with open(self.comprehensive_answers_file, 'r', encoding='utf-8') as f:
            self.comprehensive_answers = json.load(f)
            
        print(f"Loaded {len(self.questions)} questions")
        print(f"Loaded {len(self.comprehensive_answers)} comprehensive answers")
    
    def find_incomplete_dsa_questions(self):
        """Find DSA questions that don't have proper answers (no local_solution)"""
        incomplete_questions = []
        
        for answer in self.comprehensive_answers:
            if (answer.get('category') == 'DSA' and 
                (not answer.get('references', {}).get('local_solution') or 
                 answer.get('references', {}).get('local_solution') == "")):
                
                # Find corresponding question
                question_num = answer['question_number']
                question = None
                for q in self.questions:
                    if q['question_number'] == question_num:
                        question = q
                        break
                
                if question:
                    incomplete_questions.append((answer, question))
        
        print(f"Found {len(incomplete_questions)} DSA questions without proper answers")
        return incomplete_questions
    
    def extract_problem_details(self, description: str):
        """Extract problem understanding, examples, and constraints from description"""
        if not description:
            return {
                'problem_understanding': 'This is a coding problem that requires algorithmic thinking.',
                'examples': [],
                'constraints': [],
                'input_format': '',
                'output_format': ''
            }
        
        lines = description.split('\n')
        
        problem_understanding = ""
        examples = []
        constraints = []
        input_format = ""
        output_format = ""
        
        current_section = "problem"
        current_example = {}
        
        for line in lines:
            line = line.strip()
            if not line:
                continue
                
            line_lower = line.lower()
            
            # Identify sections
            if 'input format' in line_lower:
                current_section = "input_format"
                continue
            elif 'output format' in line_lower:
                current_section = "output_format"
                continue
            elif 'example' in line_lower and ':' in line:
                current_section = "example"
                current_example = {'input': '', 'output': '', 'explanation': ''}
                continue
            elif 'constraints' in line_lower:
                current_section = "constraints"
                continue
            elif line.startswith('Input:') or line.startswith('input:'):
                if current_section == "example":
                    current_example['input'] = line.replace('Input:', '').replace('input:', '').strip()
                continue
            elif line.startswith('Output:') or line.startswith('output:'):
                if current_section == "example":
                    current_example['output'] = line.replace('Output:', '').replace('output:', '').strip()
                continue
            elif line.startswith('Explanation'):
                if current_section == "example":
                    current_example['explanation'] = line.replace('Explanation', '').replace(':', '').strip()
                    if current_example['input'] or current_example['output']:
                        examples.append(current_example.copy())
                continue
            
            # Process content based on current section
            if current_section == "problem" and not problem_understanding:
                if not line.startswith(('Input', 'Output', 'Example', 'Constraint')):
                    problem_understanding = line
            elif current_section == "input_format":
                input_format = line
            elif current_section == "output_format":
                output_format = line
            elif current_section == "constraints":
                if line.startswith(('-', '•', '*')) or re.match(r'^\d+', line):
                    constraints.append(line)
        
        return {
            'problem_understanding': problem_understanding or 'This is a coding problem that requires algorithmic thinking.',
            'examples': examples,
            'constraints': constraints,
            'input_format': input_format,
            'output_format': output_format
        }
    
    def determine_algorithm_type(self, title: str, description: str):
        """Determine the type of algorithm based on title and description"""
        text = (title + " " + description).lower()
        
        # Pattern matching for algorithm types
        if any(word in text for word in ['sort', 'merge', 'quick', 'heap']):
            return 'sorting'
        elif any(word in text for word in ['tree', 'binary tree', 'bst', 'traversal']):
            return 'trees'
        elif any(word in text for word in ['graph', 'dfs', 'bfs', 'path', 'connected']):
            return 'graphs'
        elif any(word in text for word in ['dynamic programming', 'dp', 'optimal', 'minimum', 'maximum']):
            return 'dynamic_programming'
        elif any(word in text for word in ['array', 'subarray', 'rotate', 'merge']):
            return 'arrays'
        elif any(word in text for word in ['string', 'substring', 'palindrome', 'anagram']):
            return 'strings'
        elif any(word in text for word in ['linked list', 'node', 'cycle']):
            return 'linked_lists'
        elif any(word in text for word in ['stack', 'queue', 'bracket', 'parenthes']):
            return 'stacks_queues'
        elif any(word in text for word in ['hash', 'map', 'frequency', 'count']):
            return 'hashing'
        else:
            return 'general'
    
    def generate_approaches_for_type(self, algorithm_type: str, title: str, description: str):
        """Generate appropriate approaches based on algorithm type"""
        approaches = []
        
        if algorithm_type == 'arrays':
            approaches = [
                {
                    "name": "Brute Force Approach",
                    "time_complexity": "O(n²)",
                    "space_complexity": "O(1)",
                    "description": "Simple nested loop approach to solve the problem",
                    "code": f"// Brute force solution for {title}\npublic int bruteForceSolution(int[] arr) {{\n    // Iterate through all elements\n    for (int i = 0; i < arr.length; i++) {{\n        for (int j = i + 1; j < arr.length; j++) {{\n            // Process elements\n        }}\n    }}\n    return result;\n}}"
                },
                {
                    "name": "Optimized Approach",
                    "time_complexity": "O(n)",
                    "space_complexity": "O(1)",
                    "description": "Efficient single-pass solution using two pointers or sliding window",
                    "code": f"// Optimized solution for {title}\npublic int optimizedSolution(int[] arr) {{\n    int left = 0, right = arr.length - 1;\n    while (left < right) {{\n        // Process with two pointers\n        left++;\n        right--;\n    }}\n    return result;\n}}"
                }
            ]
        
        elif algorithm_type == 'strings':
            approaches = [
                {
                    "name": "Character-by-Character Approach",
                    "time_complexity": "O(n)",
                    "space_complexity": "O(1)",
                    "description": "Process string character by character",
                    "code": f"// String processing solution for {title}\npublic String processString(String s) {{\n    StringBuilder result = new StringBuilder();\n    for (char c : s.toCharArray()) {{\n        // Process each character\n        result.append(c);\n    }}\n    return result.toString();\n}}"
                },
                {
                    "name": "Hash Map Approach",
                    "time_complexity": "O(n)",
                    "space_complexity": "O(k)",
                    "description": "Use HashMap to track character frequencies or patterns",
                    "code": f"// HashMap solution for {title}\npublic int hashMapSolution(String s) {{\n    Map<Character, Integer> map = new HashMap<>();\n    for (char c : s.toCharArray()) {{\n        map.put(c, map.getOrDefault(c, 0) + 1);\n    }}\n    return result;\n}}"
                }
            ]
        
        elif algorithm_type == 'trees':
            approaches = [
                {
                    "name": "Recursive DFS",
                    "time_complexity": "O(n)",
                    "space_complexity": "O(h)",
                    "description": "Recursive depth-first traversal of the tree",
                    "code": f"// Recursive DFS solution for {title}\npublic int dfsRecursive(TreeNode root) {{\n    if (root == null) return 0;\n    \n    int left = dfsRecursive(root.left);\n    int right = dfsRecursive(root.right);\n    \n    return process(root.val, left, right);\n}}"
                },
                {
                    "name": "Iterative BFS",
                    "time_complexity": "O(n)",
                    "space_complexity": "O(w)",
                    "description": "Level-order traversal using queue",
                    "code": f"// Iterative BFS solution for {title}\npublic int bfsIterative(TreeNode root) {{\n    if (root == null) return 0;\n    \n    Queue<TreeNode> queue = new LinkedList<>();\n    queue.offer(root);\n    \n    while (!queue.isEmpty()) {{\n        TreeNode node = queue.poll();\n        // Process current node\n        if (node.left != null) queue.offer(node.left);\n        if (node.right != null) queue.offer(node.right);\n    }}\n    return result;\n}}"
                }
            ]
        
        elif algorithm_type == 'dynamic_programming':
            approaches = [
                {
                    "name": "Recursive with Memoization",
                    "time_complexity": "O(n²)",
                    "space_complexity": "O(n²)",
                    "description": "Top-down approach with memoization",
                    "code": f"// Memoization solution for {title}\npublic int dpMemo(int n, Map<String, Integer> memo) {{\n    String key = generateKey(n);\n    if (memo.containsKey(key)) return memo.get(key);\n    \n    int result = computeResult(n);\n    memo.put(key, result);\n    return result;\n}}"
                },
                {
                    "name": "Bottom-up DP",
                    "time_complexity": "O(n²)",
                    "space_complexity": "O(n)",
                    "description": "Bottom-up tabulation approach",
                    "code": f"// Bottom-up DP solution for {title}\npublic int dpBottomUp(int n) {{\n    int[] dp = new int[n + 1];\n    dp[0] = baseCase;\n    \n    for (int i = 1; i <= n; i++) {{\n        dp[i] = computeFromPrevious(dp, i);\n    }}\n    return dp[n];\n}}"
                }
            ]
        
        elif algorithm_type == 'graphs':
            approaches = [
                {
                    "name": "DFS Traversal",
                    "time_complexity": "O(V + E)",
                    "space_complexity": "O(V)",
                    "description": "Depth-first search traversal",
                    "code": f"// DFS solution for {title}\npublic void dfs(int[][] graph, int node, boolean[] visited) {{\n    visited[node] = true;\n    \n    for (int neighbor : graph[node]) {{\n        if (!visited[neighbor]) {{\n            dfs(graph, neighbor, visited);\n        }}\n    }}\n}}"
                },
                {
                    "name": "BFS Traversal",
                    "time_complexity": "O(V + E)",
                    "space_complexity": "O(V)",
                    "description": "Breadth-first search traversal",
                    "code": f"// BFS solution for {title}\npublic void bfs(int[][] graph, int start) {{\n    Queue<Integer> queue = new LinkedList<>();\n    boolean[] visited = new boolean[graph.length];\n    \n    queue.offer(start);\n    visited[start] = true;\n    \n    while (!queue.isEmpty()) {{\n        int node = queue.poll();\n        for (int neighbor : graph[node]) {{\n            if (!visited[neighbor]) {{\n                visited[neighbor] = true;\n                queue.offer(neighbor);\n            }}\n        }}\n    }}\n}}"
                }
            ]
        
        else:  # general
            approaches = [
                {
                    "name": "Brute Force",
                    "time_complexity": "O(n²)",
                    "space_complexity": "O(1)",
                    "description": "Simple brute force approach to solve the problem",
                    "code": f"// Brute force solution for {title}\npublic int solve(int[] input) {{\n    // Simple approach\n    int result = 0;\n    for (int i = 0; i < input.length; i++) {{\n        // Process each element\n        result += process(input[i]);\n    }}\n    return result;\n}}"
                },
                {
                    "name": "Optimized Approach",
                    "time_complexity": "O(n log n)",
                    "space_complexity": "O(n)",
                    "description": "More efficient approach using appropriate data structures",
                    "code": f"// Optimized solution for {title}\npublic int optimizedSolve(int[] input) {{\n    // Use appropriate data structure\n    Arrays.sort(input); // or other optimization\n    \n    int result = 0;\n    for (int val : input) {{\n        result += efficientProcess(val);\n    }}\n    return result;\n}}"
                }
            ]
        
        return approaches
    
    def generate_interview_tips_for_type(self, algorithm_type: str):
        """Generate algorithm-type specific interview tips"""
        tips_map = {
            'arrays': [
                "Consider two-pointer technique for array problems",
                "Think about sorting the array if order doesn't matter",
                "Use hash map for O(1) lookups when needed",
                "Watch out for edge cases: empty array, single element"
            ],
            'strings': [
                "Consider character frequency counting for string problems",
                "Think about sliding window for substring problems",
                "Use StringBuilder for string concatenation in Java",
                "Consider two-pointer approach for palindrome problems"
            ],
            'trees': [
                "Clarify if it's a binary tree or binary search tree",
                "Consider both recursive and iterative approaches",
                "Think about tree traversal: inorder, preorder, postorder",
                "Handle null nodes carefully"
            ],
            'graphs': [
                "Clarify if graph is directed or undirected",
                "Choose between DFS and BFS based on the problem",
                "Consider using visited array to avoid cycles",
                "Think about adjacency list vs adjacency matrix representation"
            ],
            'dynamic_programming': [
                "Identify optimal substructure and overlapping subproblems",
                "Start with recursive solution, then add memoization",
                "Consider space optimization in tabulation",
                "Think about the recurrence relation clearly"
            ]
        }
        
        return tips_map.get(algorithm_type, [
            "Break down the problem into smaller subproblems",
            "Start with brute force, then optimize",
            "Consider time vs space complexity trade-offs",
            "Test with edge cases and examples"
        ])
    
    def generate_common_mistakes_for_type(self, algorithm_type: str):
        """Generate algorithm-type specific common mistakes"""
        mistakes_map = {
            'arrays': [
                "Off-by-one errors in array indexing",
                "Not handling empty array or single element cases",
                "Integer overflow for large array sums",
                "Modifying array while iterating"
            ],
            'strings': [
                "Not handling empty or null strings",
                "Case sensitivity issues",
                "Unicode character handling",
                "String immutability in Java (use StringBuilder)"
            ],
            'trees': [
                "Not checking for null nodes",
                "Infinite recursion due to improper base cases",
                "Confusing left and right subtrees",
                "Stack overflow for deep trees"
            ],
            'graphs': [
                "Not handling disconnected components",
                "Infinite loops due to cycles",
                "Not initializing visited array properly",
                "Forgetting to mark nodes as visited"
            ],
            'dynamic_programming': [
                "Not identifying the correct state representation",
                "Incorrect base cases",
                "Wrong order of computation in tabulation",
                "Not optimizing space complexity when possible"
            ]
        }
        
        return mistakes_map.get(algorithm_type, [
            "Not understanding the problem requirements clearly",
            "Rushing to code without proper planning",
            "Not testing with edge cases",
            "Incorrect complexity analysis"
        ])
    
    def generate_answer_from_description(self, question: dict, existing_answer: dict):
        """Generate comprehensive answer from question description"""
        title = question['title']
        description = question.get('description', '')
        
        # Extract problem details
        problem_details = self.extract_problem_details(description)
        
        # Determine algorithm type
        algorithm_type = self.determine_algorithm_type(title, description)
        
        # Generate approaches
        approaches = self.generate_approaches_for_type(algorithm_type, title, description)
        
        # Generate key insights based on problem details
        key_insights = [
            f"This is a {algorithm_type.replace('_', ' ')} problem",
        ]
        
        if problem_details['constraints']:
            key_insights.append("Pay attention to the given constraints for optimization opportunities")
        
        if problem_details['examples']:
            key_insights.append("Work through the provided examples to understand the pattern")
        
        key_insights.extend([
            "Consider multiple approaches: brute force first, then optimize",
            "Think about edge cases and boundary conditions"
        ])
        
        # Create enhanced answer
        enhanced_answer = existing_answer.copy()
        enhanced_answer['answer'] = {
            "problem_understanding": problem_details['problem_understanding'],
            "key_insights": key_insights,
            "approaches": approaches,
            "optimization_notes": f"For {algorithm_type.replace('_', ' ')} problems, consider using appropriate data structures and algorithms to optimize time and space complexity.",
            "interview_tips": self.generate_interview_tips_for_type(algorithm_type),
            "common_mistakes": self.generate_common_mistakes_for_type(algorithm_type),
            "follow_up_questions": [
                "What if the input size is very large?",
                "Can you optimize the space complexity?",
                "How would you handle edge cases?",
                "What about different input constraints?"
            ]
        }
        
        # Add examples if available
        if problem_details['examples']:
            enhanced_answer['answer']['examples'] = problem_details['examples']
        
        # Enhance references
        enhanced_answer['references']['leetcode'] = self.generate_leetcode_link(title)
        enhanced_answer['references']['similar_problems'] = self.generate_similar_problems(algorithm_type)
        enhanced_answer['references']['blog_links'] = [
            "https://www.geeksforgeeks.org/",
            "https://leetcode.com/discuss/",
            "https://www.interviewbit.com/"
        ]
        
        return enhanced_answer
    
    def generate_leetcode_link(self, title: str):
        """Generate probable LeetCode link"""
        title_lower = title.lower()
        
        # Common LeetCode problem mappings
        if 'two sum' in title_lower:
            return "https://leetcode.com/problems/two-sum/"
        elif 'reverse' in title_lower and 'linked list' in title_lower:
            return "https://leetcode.com/problems/reverse-linked-list/"
        elif 'valid parenthes' in title_lower:
            return "https://leetcode.com/problems/valid-parentheses/"
        elif 'maximum subarray' in title_lower:
            return "https://leetcode.com/problems/maximum-subarray/"
        elif 'climbing stairs' in title_lower:
            return "https://leetcode.com/problems/climbing-stairs/"
        
        return ""
    
    def generate_similar_problems(self, algorithm_type: str):
        """Generate similar problems based on algorithm type"""
        similar_map = {
            'arrays': ["Two Sum", "Three Sum", "Maximum Subarray", "Merge Intervals"],
            'strings': ["Valid Anagram", "Longest Substring", "Palindrome Check", "String Rotation"],
            'trees': ["Tree Traversal", "Maximum Depth", "Symmetric Tree", "Path Sum"],
            'graphs': ["Number of Islands", "Course Schedule", "Clone Graph", "Word Ladder"],
            'dynamic_programming': ["Coin Change", "Longest Common Subsequence", "Edit Distance", "House Robber"],
            'linked_lists': ["Reverse Linked List", "Merge Two Lists", "Detect Cycle", "Remove Nth Node"],
            'stacks_queues': ["Valid Parentheses", "Implement Queue using Stacks", "Min Stack", "Next Greater Element"]
        }
        
        return similar_map.get(algorithm_type, ["Related algorithmic problems"])
    
    def update_incomplete_answers(self):
        """Update DSA questions that don't have proper answers"""
        incomplete_questions = self.find_incomplete_dsa_questions()
        updated_count = 0
        
        for existing_answer, question in incomplete_questions:
            try:
                # Generate enhanced answer from description
                enhanced_answer = self.generate_answer_from_description(question, existing_answer)
                
                # Find and update in comprehensive_answers
                for i, answer in enumerate(self.comprehensive_answers):
                    if answer['question_number'] == enhanced_answer['question_number']:
                        self.comprehensive_answers[i] = enhanced_answer
                        updated_count += 1
                        break
                
                print(f"Generated answer for question {question['question_number']}: {question['title']}")
                
            except Exception as e:
                print(f"Error generating answer for question {question['question_number']}: {e}")
        
        print(f"Updated {updated_count} DSA answers from descriptions")
        return updated_count
    
    def save_updated_answers(self):
        """Save updated comprehensive answers"""
        with open(self.comprehensive_answers_file, 'w', encoding='utf-8') as f:
            json.dump(self.comprehensive_answers, f, indent=2, ensure_ascii=False)
        print(f"Saved updated answers to {self.comprehensive_answers_file}")
    
    def run(self):
        """Run the description-based answer generation process"""
        print("Starting DSA description-based answer generation...")
        self.load_data()
        updated_count = self.update_incomplete_answers()
        if updated_count > 0:
            self.save_updated_answers()
        print("DSA description-based answer generation complete!")
        return updated_count

def main():
    questions_file = "complete_questions_dataset.json"
    comprehensive_answers_file = "comprehensive_answers.json"
    
    generator = DSADescriptionAnswerGenerator(questions_file, comprehensive_answers_file)
    updated_count = generator.run()
    
    print(f"\nCompleted! Generated {updated_count} answers from question descriptions.")

if __name__ == "__main__":
    main()