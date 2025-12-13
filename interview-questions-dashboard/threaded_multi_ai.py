#!/usr/bin/env python3
"""
Enhanced Multi-AI Solution Generator with Threading and Resume Support

Features:
- Multi-threading for faster processing
- Resumable processing (network failure recovery)
- Progress checkpointing
- Thread-safe operations
- Rate limiting per provider
- Comprehensive error handling
"""

import json
import requests
import time
import logging
import argparse
import os
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
from typing import Dict, List, Optional, Tuple
from datetime import datetime
import queue
import random

def load_env_file():
    """Load environment variables from .env file if it exists."""
    # Look for .env in the script's directory first, then in current directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    env_locations = [
        os.path.join(script_dir, '.env'),  # Same directory as script
        '.env'  # Current working directory
    ]
    
    for env_file in env_locations:
        if os.path.exists(env_file):
            print(f"Loading environment from: {env_file}")
            with open(env_file, 'r') as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith('#') and '=' in line:
                        key, value = line.split('=', 1)
                        os.environ[key.strip()] = value.strip()
            return  # Stop after finding first .env file

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - [Thread-%(thread)d] - %(message)s',
    handlers=[
        logging.FileHandler('multi_ai_generator_threaded.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

class ThreadSafeCounter:
    """Thread-safe counter for tracking progress."""
    def __init__(self):
        self._value = 0
        self._lock = threading.Lock()
    
    def increment(self):
        with self._lock:
            self._value += 1
            return self._value
    
    @property
    def value(self):
        with self._lock:
            return self._value

class RateLimiter:
    """Thread-safe rate limiter."""
    def __init__(self, max_requests_per_minute=20):
        self.max_requests = max_requests_per_minute
        self.requests = []
        self.lock = threading.Lock()
    
    def wait_if_needed(self):
        with self.lock:
            now = time.time()
            # Remove requests older than 1 minute
            self.requests = [req_time for req_time in self.requests if now - req_time < 60]
            
            if len(self.requests) >= self.max_requests:
                # Wait until we can make another request
                sleep_time = 60 - (now - self.requests[0]) + 1
                if sleep_time > 0:
                    logger.info(f"Rate limit reached, waiting {sleep_time:.1f} seconds...")
                    time.sleep(sleep_time)
                    # Clean up old requests again
                    now = time.time()
                    self.requests = [req_time for req_time in self.requests if now - req_time < 60]
            
            self.requests.append(now)

class ThreadedMultiAI:
    """Enhanced multi-AI provider with threading support."""
    
    def __init__(self, max_threads=4):
        self.providers = self.setup_providers()
        self.stats = {provider: {
            'requests': 0, 'successes': 0, 'failures': 0, 'cost': 0.0
        } for provider in self.providers}
        self.rate_limiter = RateLimiter(max_requests_per_minute=15)  # Conservative rate limiting
        self.progress_counter = ThreadSafeCounter()
        self.stats_lock = threading.Lock()
        self.max_threads = max_threads
        
    def setup_providers(self) -> Dict:
        """Setup available AI providers - Gemini 2.0 Flash primary, 1.5 Flash backup."""
        providers = {}
        
        # Google Gemini models with automatic fallback
        gemini_key = os.getenv('GEMINI_API_KEY') or os.getenv('GOOGLE_API_KEY')
        if gemini_key:
            try:
                import google.generativeai as genai
                genai.configure(api_key=gemini_key)
                
                # Primary: Gemini 2.0 Flash Experimental (newest, fastest)
                providers['gemini-2.0-flash'] = {
                    'model': genai.GenerativeModel('gemini-2.0-flash-exp'),
                    'name': 'Google Gemini 2.0 Flash Experimental',
                    'cost_per_1k': 0.0
                }
                logger.info("✅ Google Gemini 2.0 Flash Experimental initialized (primary)")
                
                # Backup: Gemini 2.5 Flash - Latest stable version with higher quota
                providers['gemini-2.5-flash'] = {
                    'model': genai.GenerativeModel('gemini-2.5-flash'),
                    'name': 'Google Gemini 2.5 Flash (1500 RPD backup)',
                    'cost_per_1k': 0.0
                }
                logger.info("✅ Google Gemini 2.5 Flash initialized (backup - 1500 RPD)")
                
            except ImportError:
                logger.error("❌ Google Generative AI library not installed")
                logger.error("   Install with: pip install google-generativeai")
                raise ValueError("google-generativeai package required")
        
        if not providers:
            raise ValueError("No AI providers configured! Set GEMINI_API_KEY environment variable")
        
        logger.info(f"✅ Initialized {len(providers)} Gemini models with fallback")
        return providers
    
    def update_stats(self, provider: str, success: bool, cost: float = 0.0):
        """Thread-safe stats update."""
        with self.stats_lock:
            self.stats[provider]['requests'] += 1
            if success:
                self.stats[provider]['successes'] += 1
            else:
                self.stats[provider]['failures'] += 1
            self.stats[provider]['cost'] += cost
    
    def generate_with_gemini(self, prompt: str, model_key: str) -> Optional[str]:
        """Generate solution with specified Gemini model."""
        try:
            if model_key not in self.providers:
                return None
            
            self.rate_limiter.wait_if_needed()
            
            response = self.providers[model_key]['model'].generate_content(prompt)
            
            if response and response.text:
                # Estimate cost (free for both models)
                tokens = len(prompt.split()) + len(response.text.split())
                cost = (tokens / 1000) * self.providers[model_key]['cost_per_1k']
                self.update_stats(model_key, True, cost)
                return response.text
            
            self.update_stats(model_key, False)
            return None
            
        except Exception as e:
            logger.error(f"Gemini {model_key} error: {str(e)}")
            self.update_stats(model_key, False)
            return None
    
    def generate_solution(self, prompt: str, preferred_provider: Optional[str] = None) -> Tuple[Optional[str], str]:
        """Generate solution with automatic fallback: 2.0 Flash -> 2.5 Flash."""
        # Try Gemini 2.0 Flash Exp first, then fall back to 2.5 Flash
        gemini_models = ['gemini-2.0-flash', 'gemini-2.5-flash']
        
        for model_key in gemini_models:
            if model_key not in self.providers:
                continue
            
            provider_name = self.providers[model_key]['name']
            logger.info(f"Trying {provider_name}...")
            
            result = self.generate_with_gemini(prompt, model_key)
            
            if result:
                logger.info(f"✅ Success with {provider_name}")
                return result, model_key
            
            # Short delay between attempts
            time.sleep(1)
        
        logger.error("❌ All Gemini models failed")
        return None, 'failed'

class ThreadedSolutionGenerator:
    """Main threaded solution generator."""
    
    def __init__(self, max_threads=4):
        self.ai = ThreadedMultiAI(max_threads)
        self.stats = {
            'total_processed': 0,
            'successful_generations': 0,
            'errors': 0,
            'skipped': 0
        }
        self.max_threads = max_threads
        self.checkpoint_file = 'progress_checkpoint.json'
        
    def save_checkpoint(self, processed_indices: List[int], total: int):
        """Save progress checkpoint for resumability."""
        checkpoint = {
            'processed_indices': processed_indices,
            'total': total,
            'timestamp': datetime.now().isoformat(),
            'stats': self.stats.copy()
        }
        with open(self.checkpoint_file, 'w') as f:
            json.dump(checkpoint, f, indent=2)
    
    def load_checkpoint(self) -> Optional[Dict]:
        """Load previous checkpoint if exists."""
        if os.path.exists(self.checkpoint_file):
            try:
                with open(self.checkpoint_file, 'r') as f:
                    return json.load(f)
            except Exception as e:
                logger.warning(f"Could not load checkpoint: {e}")
        return None
    
    def create_optimized_prompt(self, problem: Dict, provider: Optional[str] = None) -> str:
        """Create optimized prompt for solution generation - handles DSA, System Design, and General Interview Questions."""
        # Use description field if problem field is empty
        problem_text = problem.get('problem') or problem.get('description', 'Not specified')
        category = problem.get('category', '').strip().upper()
        
        # Analyze question type
        description_lower = problem_text.lower()
        
        # Check if it's a coding problem first (higher priority for LLD/DSA)
        coding_categories = ['DSA', 'ALGORITHMS', 'DATA STRUCTURES', 'LLD', 'LOW LEVEL DESIGN']
        is_coding_category = category in coding_categories
        
        # Strong coding indicators
        strong_coding_keywords = ['implement', 'write a function', 'write a method', 'write code',
                                 'solve', 'find the', 'calculate', 'return', 'given an array', 
                                 'given a string', 'given a list', 'write an algorithm']
        has_strong_coding_signal = any(keyword in description_lower for keyword in strong_coding_keywords)
        
        # LLD with "implement" or "write" is definitely coding
        is_coding = is_coding_category or has_strong_coding_signal
        
        # System Design indicators (check after coding to avoid false positives)
        is_system_design_category = category == 'SYSTEM DESIGN'
        system_design_keywords = ['design a system', 'design an architecture', 'system design', 
                                 'scalability', 'distributed system', 'how would you design',
                                 'high-level design', 'hld', 'design twitter', 'design uber',
                                 'design instagram', 'design netflix']
        has_system_design_signal = any(keyword in description_lower for keyword in system_design_keywords)
        
        # For "design X" questions, check if it's asking for code implementation or system architecture
        if 'design and implement' in description_lower or 'implement a' in description_lower:
            # "Design and implement" with data structure/algorithm = coding problem
            is_coding = True
            is_system_design = False
        else:
            is_system_design = is_system_design_category or has_system_design_signal
        
        if is_system_design and not is_coding:
            # System Design prompt
            prompt = f"""
Provide a comprehensive System Design solution for this question:

Question: {problem_text}
Category: {problem.get('category', 'Not specified')}
Company: {problem.get('company', 'Not specified')}

IMPORTANT: Structure your answer with the following sections:

1. FUNCTIONAL REQUIREMENTS (FRs):
   - List all key functional requirements
   - What features/capabilities should the system provide?

2. NON-FUNCTIONAL REQUIREMENTS (NFRs):
   - Scalability, Availability, Consistency, Latency requirements
   - Performance goals, Security considerations

3. CAPACITY ESTIMATION (if applicable):
   - Traffic estimates (DAU, QPS, etc.)
   - Storage estimates (data volume, growth rate)
   - Bandwidth requirements
   - Memory/Cache requirements

4. CORE COMPONENTS:
   a) Actors: Who are the users/services?
   b) Entities: What are the main data models/objects?
   c) APIs: Key API endpoints with request/response
   d) Flows: Main workflows/sequences (use ASCII diagrams if helpful)

5. HIGH-LEVEL DESIGN (HLD):
   - Draw ASCII architecture diagram showing main components
   - Explain component responsibilities
   - Show data flow between components
   - Include Load Balancers, Databases, Caches, Message Queues, etc.

6. DEEP DIVE & TRADE-OFFS:
   - Database choice (SQL vs NoSQL) and why
   - Caching strategy (Redis, CDN, etc.) and why
   - Consistency vs Availability trade-offs (CAP theorem)
   - Scalability considerations (horizontal vs vertical)
   - Bottlenecks and how to address them
   - Data partitioning/sharding strategy if needed
   - Replication strategy if needed

7. FUTURE IMPROVEMENTS & EDGE CASES:
   - What could be enhanced?
   - How to handle failures/edge cases?
   - Monitoring and alerting strategy

Make the answer thorough, interview-ready, and production-focused.
Use clear ASCII diagrams where helpful.
Be specific about technology choices and explain WHY.
"""
        elif is_coding:
            # Regular DSA/Coding prompt
            prompt = f"""
Generate an optimized Java solution for this coding problem:

Problem: {problem_text}
Category: {problem.get('category', 'Not specified')}
Company: {problem.get('company', 'Not specified')}

Requirements:
1. Provide a complete, optimized Java solution
2. Include comprehensive test cases with edge cases
3. Add detailed time and space complexity analysis
4. Use proper class structure and method naming
5. Include explanatory comments for the algorithm
6. Handle edge cases appropriately
7. Provide multiple test cases demonstrating the solution

Format the response as a complete Java class with:
- Main solution method
- Helper methods if needed
- Comprehensive main method with test cases
- Time/Space complexity comments
- Clear variable naming and structure

Make the solution production-ready and interview-quality.
Focus on clean, efficient code with clear explanations.
"""
        else:
            # General Interview Question (Behavioral, Theoretical, Conceptual, etc.)
            prompt = f"""
Provide a comprehensive and structured answer to this interview question:

Question: {problem_text}
Category: {problem.get('category', 'Not specified')}
Company: {problem.get('company', 'Not specified')}

IMPORTANT: Provide a thorough, interview-ready answer with the following structure:

1. ANSWER OVERVIEW:
   - Start with a concise, direct answer to the question
   - Provide context and background if needed

2. DETAILED EXPLANATION:
   - Break down the topic into logical sections
   - Explain key concepts clearly with examples
   - Use real-world scenarios to illustrate points

3. KEY POINTS TO REMEMBER:
   - Highlight the most important takeaways
   - List critical facts, best practices, or principles
   - Include common patterns or approaches

4. PRACTICAL EXAMPLES:
   - Provide 2-3 concrete examples
   - Show real-world applications
   - Include code snippets or diagrams if relevant (ASCII format)

5. COMMON PITFALLS & CONSIDERATIONS:
   - What mistakes to avoid
   - Edge cases or special scenarios to consider
   - Trade-offs and limitations

6. INTERVIEW TIPS:
   - How to approach this topic in an interview
   - What interviewers typically look for
   - Follow-up questions you might encounter

7. RELATED CONCEPTS:
   - Connected topics worth mentioning
   - How this fits into the bigger picture
   - Additional resources or areas to explore

Guidelines:
- Be thorough but concise
- Use bullet points for clarity
- Include examples wherever possible
- Explain technical terms clearly
- Structure the answer logically
- Make it interview-ready and memorable
- If it's a "What is..." question, explain definition, use cases, and examples
- If it's a "How..." question, provide step-by-step approach
- If it's a "When..." question, discuss scenarios and decision criteria
- If it's a comparison question, use a table or structured comparison
- If it's about patterns/best practices, explain WHY they matter

Make the answer comprehensive, professional, and suitable for senior-level interviews.
"""
        
        return prompt
    
    def should_process_problem(self, problem: Dict) -> bool:
        """Check if problem should be processed."""
        # Skip if already has AI-generated answer
        if problem.get('ai_provider_used') and problem.get('answer'):
            return False
        
        # Skip if no problem statement in either field
        if not (problem.get('problem') or problem.get('description')):
            return False
        
        return True
    
    def process_single_problem(self, problem: Dict, index: int, preferred_provider: Optional[str] = None) -> Tuple[bool, str, str]:
        """Process a single problem (thread-safe)."""
        try:
            if not self.should_process_problem(problem):
                return False, 'skipped', 'already_processed'
            
            prompt = self.create_optimized_prompt(problem, preferred_provider)
            solution, provider_used = self.ai.generate_solution(prompt, preferred_provider)
            
            if solution:
                # Update problem with solution
                problem['answer'] = solution
                problem['ai_provider_used'] = provider_used
                problem['ai_generation_timestamp'] = datetime.now().isoformat()
                
                logger.info(f"✅ Generated solution using {provider_used}")
                return True, provider_used, 'success'
            else:
                logger.warning(f"❌ Failed: generation_failed")
                return False, 'failed', 'generation_failed'
                
        except Exception as e:
            logger.error(f"❌ Error processing problem {index}: {str(e)}")
            return False, 'error', str(e)
    
    def process_category_threaded(self, problems: List[Dict], category: str, 
                                 limit: Optional[int] = None,
                                 preferred_provider: Optional[str] = None,
                                 resume: bool = True) -> List[Dict]:
        """Process problems with threading support."""
        
        # Filter problems by category
        filtered_problems = []
        problem_indices = []
        
        for i, problem in enumerate(problems):
            if problem.get('category', '').upper() == category.upper():
                filtered_problems.append(problem)
                problem_indices.append(i)
        
        if limit:
            filtered_problems = filtered_problems[:limit]
            problem_indices = problem_indices[:limit]
        
        logger.info(f"Processing {len(filtered_problems)} problems in category: {category}")
        
        # Load checkpoint if resuming
        processed_indices = set()
        if resume:
            checkpoint = self.load_checkpoint()
            if checkpoint:
                processed_indices = set(checkpoint.get('processed_indices', []))
                logger.info(f"Resuming from checkpoint: {len(processed_indices)} problems already processed")
        
        # Create work queue for unprocessed problems
        work_items = []
        for i, (problem, orig_index) in enumerate(zip(filtered_problems, problem_indices)):
            if orig_index not in processed_indices:
                work_items.append((problem, orig_index, i + 1))
        
        if not work_items:
            logger.info("All problems already processed!")
            return problems
        
        logger.info(f"Processing {len(work_items)} remaining problems with {self.max_threads} threads")
        
        # Process with thread pool
        successful = 0
        failed = 0
        skipped = 0
        
        def process_worker(item):
            problem, orig_index, display_index = item
            total_problems = len(filtered_problems)
            
            logger.info(f"Processing {display_index}/{total_problems}: {problem.get('problem', 'Unknown')}")
            
            success, provider, status = self.process_single_problem(problem, orig_index, preferred_provider)
            
            return {
                'success': success,
                'provider': provider,
                'status': status,
                'orig_index': orig_index,
                'display_index': display_index
            }
        
        # Use ThreadPoolExecutor for concurrent processing
        with ThreadPoolExecutor(max_workers=self.max_threads) as executor:
            # Submit all work items
            future_to_item = {executor.submit(process_worker, item): item for item in work_items}
            
            completed_indices = list(processed_indices)
            
            for future in as_completed(future_to_item):
                try:
                    result = future.result()
                    
                    if result['success']:
                        successful += 1
                    elif result['status'] == 'skipped':
                        skipped += 1
                    else:
                        failed += 1
                    
                    completed_indices.append(result['orig_index'])
                    
                    # Save checkpoint every 10 completions
                    if len(completed_indices) % 10 == 0:
                        self.save_checkpoint(completed_indices, len(filtered_problems))
                    
                    # Log progress
                    total_completed = len(completed_indices)
                    if total_completed % 10 == 0:
                        logger.info(f"Progress: {total_completed}/{len(filtered_problems)} "
                                  f"({total_completed/len(filtered_problems)*100:.1f}%) - "
                                  f"Success: {successful}, Failed: {failed}, Skipped: {skipped}")
                
                except Exception as e:
                    logger.error(f"Worker thread error: {e}")
                    failed += 1
        
        # Save final checkpoint
        self.save_checkpoint(completed_indices, len(filtered_problems))
        
        # Update stats
        self.stats['total_processed'] += len(work_items)
        self.stats['successful_generations'] += successful
        self.stats['errors'] += failed
        self.stats['skipped'] += skipped
        
        logger.info(f"Category {category} completed: {successful} successful, {failed} failed, {skipped} skipped")
        
        return problems
    
    def print_final_stats(self):
        """Print final processing statistics."""
        logger.info(f"\n{'='*80}")
        logger.info(f"FINAL PROCESSING STATISTICS")
        logger.info(f"{'='*80}")
        logger.info(f"Total Processed: {self.stats['total_processed']}")
        logger.info(f"Successful: {self.stats['successful_generations']}")
        logger.info(f"Failed: {self.stats['errors']}")
        logger.info(f"Skipped: {self.stats['skipped']}")
        
        if self.stats['total_processed'] > 0:
            success_rate = (self.stats['successful_generations'] / self.stats['total_processed']) * 100
            logger.info(f"Success Rate: {success_rate:.2f}%")
        
        # Provider performance
        logger.info(f"\nAI PROVIDER PERFORMANCE:")
        logger.info("-" * 50)
        
        total_cost = 0
        with self.ai.stats_lock:
            for provider, stats in self.ai.stats.items():
                if stats['requests'] > 0:
                    success_rate = (stats['successes'] / stats['requests']) * 100
                    logger.info(f"{provider.upper()}: {stats['requests']} requests, "
                               f"{stats['successes']} successes ({success_rate:.1f}%), "
                               f"${stats['cost']:.4f}")
                    total_cost += stats['cost']
        
        logger.info(f"\nTOTAL ESTIMATED COST: ${total_cost:.4f}")

def main():
    # Load environment variables from .env file
    load_env_file()
    
    parser = argparse.ArgumentParser(description='Threaded Gemini Solution Generator (DSA + System Design + General Interview) - FREE tier: 2.0 Flash (50 RPD) + 2.5 Flash (1500 RPD)')
    parser.add_argument('--input', '-i', required=True, help='Input JSON file path')
    parser.add_argument('--output', '-o', help='Output JSON file path (default: input file)')
    parser.add_argument('--category', '-c', help='Category to process (DSA, System Design, Behavioral, etc.)')
    parser.add_argument('--limit', '-l', type=int, help='Limit number of problems')
    parser.add_argument('--threads', '-t', type=int, default=4, help='Number of threads (default: 4)')
    parser.add_argument('--dry-run', action='store_true', help='Show what would be processed')
    parser.add_argument('--backup', action='store_true', help='Create backup before processing')
    parser.add_argument('--no-resume', action='store_true', help='Don\'t resume from checkpoint')
    
    args = parser.parse_args()
    
    try:
        # Load data
        with open(args.input, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        logger.info(f"Loaded {len(data)} problems from {args.input}")
        
        # Initialize generator
        generator = ThreadedSolutionGenerator(max_threads=args.threads)
        logger.info(f"Using {args.threads} threads for processing")
        
        if args.dry_run:
            processable = [p for p in data if generator.should_process_problem(p)]
            if args.category:
                processable = [p for p in processable 
                             if p.get('category', '').upper() == args.category.upper()]
            
            # Count different question types
            system_design_count = 0
            coding_count = 0
            general_count = 0
            
            coding_categories = ['DSA', 'ALGORITHMS', 'DATA STRUCTURES', 'LLD', 'LOW LEVEL DESIGN']
            
            for p in processable:
                cat = p.get('category', '').upper()
                desc = (p.get('problem') or p.get('description', '')).lower()
                
                if cat == 'SYSTEM DESIGN' or any(kw in desc for kw in ['design a', 'design an', 'architecture']):
                    system_design_count += 1
                elif cat in coding_categories or any(kw in desc for kw in ['implement', 'algorithm', 'code']):
                    coding_count += 1
                else:
                    general_count += 1
            
            logger.info(f"Would process {len(processable)} problems total:")
            if system_design_count > 0:
                logger.info(f"  - {system_design_count} System Design questions (comprehensive HLD/LLD answers)")
            if coding_count > 0:
                logger.info(f"  - {coding_count} Coding/DSA questions (Java solutions with tests)")
            if general_count > 0:
                logger.info(f"  - {general_count} General Interview questions (structured answers with examples)")
            logger.info(f"Will use Gemini 2.0 Flash (50 RPD) → 2.5 Flash (1500 RPD) fallback")
            
            return
        
        # Create backup if requested
        if args.backup:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            backup_path = f"backup_{timestamp}.json"
            with open(backup_path, 'w', encoding='utf-8') as f:
                json.dump(data, f, indent=2, ensure_ascii=False)
            logger.info(f"Backup created: {backup_path}")
        
        # Process data
        resume = not args.no_resume
        if args.category:
            updated_data = generator.process_category_threaded(
                data, args.category, args.limit, None, resume
            )
        else:
            logger.info("Processing all categories...")
            updated_data = data
            categories = sorted(set(p.get('category', '') for p in data if p.get('category')))
            
            for category in categories:
                if category.upper() not in ['SYSTEM DESIGN', 'SYSTEM_DESIGN']:
                    logger.info(f"\nProcessing category: {category}")
                    updated_data = generator.process_category_threaded(
                        updated_data, category, args.limit, None, resume
                    )
        
        # Save results
        output_path = args.output or args.input
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(updated_data, f, indent=2, ensure_ascii=False)
        
        logger.info(f"✅ Results saved to: {output_path}")
        
        # Clean up checkpoint
        if os.path.exists(generator.checkpoint_file):
            os.remove(generator.checkpoint_file)
            logger.info("✅ Checkpoint file cleaned up")
        
        # Print final statistics
        generator.print_final_stats()
        
    except KeyboardInterrupt:
        logger.info("\n⚠️ Process interrupted by user")
        if 'generator' in locals():
            generator.print_final_stats()
            logger.info("💾 Progress saved in checkpoint file for resuming later")
    except Exception as e:
        logger.error(f"❌ Error: {str(e)}")

if __name__ == "__main__":
    main()