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
    env_file = '.env'
    if os.path.exists(env_file):
        with open(env_file, 'r') as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith('#') and '=' in line:
                    key, value = line.split('=', 1)
                    os.environ[key.strip()] = value.strip()

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
        # Add huggingface stats even though it's not in providers dict (free, no API key)
        self.stats['huggingface'] = {'requests': 0, 'successes': 0, 'failures': 0, 'cost': 0.0}
        self.rate_limiter = RateLimiter(max_requests_per_minute=15)  # Conservative rate limiting
        self.progress_counter = ThreadSafeCounter()
        self.stats_lock = threading.Lock()
        self.max_threads = max_threads
        
    def setup_providers(self) -> Dict:
        """Setup available AI providers."""
        providers = {}
        
        # Google Gemini
        gemini_key = os.getenv('GEMINI_API_KEY') or os.getenv('GOOGLE_API_KEY')
        if gemini_key:
            try:
                import google.generativeai as genai
                genai.configure(api_key=gemini_key)
                providers['gemini'] = {
                    'model': genai.GenerativeModel('gemini-2.0-flash'),
                    'name': 'Google Gemini 2.0 Flash',
                    'cost_per_1k': 0.0005
                }
                logger.info("✅ Google Gemini 2.0 Flash initialized")
            except ImportError:
                logger.warning("❌ Google Generative AI library not installed")
        
        # OpenAI GPT
        if os.getenv('OPENAI_API_KEY'):
            # Try to use the most cost-effective model available
            # gpt-4o-mini is currently the cheapest and most capable
            providers['openai'] = {
                'api_key': os.getenv('OPENAI_API_KEY'),
                'model': 'gpt-4o-mini',  # Most cost-effective OpenAI model
                'name': 'OpenAI GPT-4o-mini (Cost-effective)',
                'cost_per_1k': 0.00015  # Very affordable pricing
            }
            logger.info("✅ OpenAI GPT-4o-mini initialized (cost-effective)")
        
        # Claude
        if os.getenv('CLAUDE_API_KEY'):
            providers['claude'] = {
                'api_key': os.getenv('CLAUDE_API_KEY'),
                'model': 'claude-3-haiku-20240307',
                'name': 'Anthropic Claude 3 Haiku',
                'cost_per_1k': 0.0025
            }
            logger.info("✅ Anthropic Claude 3 Haiku initialized")
        
        # Hugging Face Free Inference API (No API key needed for some models)
        try:
            providers['huggingface'] = {
                'model': 'microsoft/phi-2',  # Free, good for coding
                'name': 'Hugging Face Phi-2 (Free)',
                'cost_per_1k': 0.0,
                'api_url': 'https://api-inference.huggingface.co/models/microsoft/phi-2'
            }
            logger.info("✅ Hugging Face Phi-2 initialized (Free)")
        except Exception as e:
            logger.warning(f"Could not initialize Hugging Face: {e}")
        
        # DeepSeek Coder (Free API)
        try:
            providers['deepseek'] = {
                'model': 'deepseek-coder',
                'name': 'DeepSeek Coder (Free)',
                'cost_per_1k': 0.0,
                'api_url': 'https://api.deepseek.com/v1/chat/completions'
            }
            logger.info("✅ DeepSeek Coder initialized (Free)")
        except Exception as e:
            logger.warning(f"Could not initialize DeepSeek: {e}")
        
        if not providers:
            raise ValueError("No AI providers configured! Set GEMINI_API_KEY, OPENAI_API_KEY, or CLAUDE_API_KEY")
        
        logger.info(f"Initialized {len(providers)} AI providers")
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
    
    def generate_with_gemini(self, prompt: str) -> Optional[str]:
        """Generate solution with Gemini."""
        try:
            if 'gemini' not in self.providers:
                return None
            
            self.rate_limiter.wait_if_needed()
            
            response = self.providers['gemini']['model'].generate_content(prompt)
            
            if response and response.text:
                # Estimate cost (rough approximation)
                tokens = len(prompt.split()) + len(response.text.split())
                cost = (tokens / 1000) * self.providers['gemini']['cost_per_1k']
                self.update_stats('gemini', True, cost)
                return response.text
            
            self.update_stats('gemini', False)
            return None
            
        except Exception as e:
            logger.error(f"Gemini error: {str(e)}")
            self.update_stats('gemini', False)
            return None
    
    def generate_with_openai(self, prompt: str) -> Optional[str]:
        """Generate solution with OpenAI GPT."""
        try:
            if 'openai' not in self.providers:
                return None
            
            self.rate_limiter.wait_if_needed()
            
            headers = {
                'Authorization': f'Bearer {self.providers["openai"]["api_key"]}',
                'Content-Type': 'application/json'
            }
            
            payload = {
                'model': self.providers['openai']['model'],
                'messages': [
                    {'role': 'user', 'content': prompt}
                ],
                'max_tokens': 2500,
                'temperature': 0.7
            }
            
            response = requests.post(
                'https://api.openai.com/v1/chat/completions',
                headers=headers,
                json=payload,
                timeout=60
            )
            
            if response.status_code == 200:
                result = response.json()
                content = result['choices'][0]['message']['content']
                
                # Calculate cost
                total_tokens = result.get('usage', {}).get('total_tokens', 0)
                cost = (total_tokens / 1000) * self.providers['openai']['cost_per_1k']
                self.update_stats('openai', True, cost)
                return content
            else:
                logger.error(f"OpenAI API error: {response.status_code}")
                self.update_stats('openai', False)
                return None
                
        except Exception as e:
            logger.error(f"OpenAI error: {str(e)}")
            self.update_stats('openai', False)
            return None
    
    def generate_with_claude(self, prompt: str) -> Optional[str]:
        """Generate solution with Claude."""
        try:
            if 'claude' not in self.providers:
                return None
            
            self.rate_limiter.wait_if_needed()
            
            headers = {
                'x-api-key': self.providers['claude']['api_key'],
                'Content-Type': 'application/json',
                'anthropic-version': '2023-06-01'
            }
            
            payload = {
                'model': self.providers['claude']['model'],
                'max_tokens': 2500,
                'messages': [
                    {'role': 'user', 'content': prompt}
                ]
            }
            
            response = requests.post(
                'https://api.anthropic.com/v1/messages',
                headers=headers,
                json=payload,
                timeout=60
            )
            
            if response.status_code == 200:
                result = response.json()
                content = result['content'][0]['text']
                
                # Estimate cost
                input_tokens = result.get('usage', {}).get('input_tokens', 0)
                output_tokens = result.get('usage', {}).get('output_tokens', 0)
                cost = ((input_tokens + output_tokens) / 1000) * self.providers['claude']['cost_per_1k']
                self.update_stats('claude', True, cost)
                return content
            else:
                logger.error(f"Claude API error: {response.status_code}")
                self.update_stats('claude', False)
                return None
                
        except Exception as e:
            logger.error(f"Claude error: {str(e)}")
            self.update_stats('claude', False)
            return None
    
    def generate_with_huggingface(self, prompt: str) -> Optional[str]:
        """Generate solution with Hugging Face (Free)."""
        try:
            if 'huggingface' not in self.providers:
                return None
            
            self.rate_limiter.wait_if_needed()
            
            headers = {'Content-Type': 'application/json'}
            payload = {
                'inputs': prompt,
                'parameters': {
                    'max_new_tokens': 2000,
                    'temperature': 0.7,
                    'return_full_text': False
                }
            }
            
            response = requests.post(
                self.providers['huggingface']['api_url'],
                headers=headers,
                json=payload,
                timeout=120  # Longer timeout for free tier
            )
            
            if response.status_code == 200:
                result = response.json()
                content = result[0]['generated_text'] if isinstance(result, list) else result.get('generated_text', '')
                self.update_stats('huggingface', True, 0.0)
                return content
            else:
                logger.error(f"Hugging Face API error: {response.status_code}")
                self.update_stats('huggingface', False)
                return None
                
        except Exception as e:
            logger.error(f"Hugging Face error: {str(e)}")
            self.update_stats('huggingface', False)
            return None
    
    def generate_with_deepseek(self, prompt: str) -> Optional[str]:
        """Generate solution with DeepSeek Coder (Free)."""
        try:
            if 'deepseek' not in self.providers:
                return None
            
            self.rate_limiter.wait_if_needed()
            
            headers = {'Content-Type': 'application/json'}
            payload = {
                'model': 'deepseek-coder',
                'messages': [{'role': 'user', 'content': prompt}],
                'max_tokens': 2500,
                'temperature': 0.7
            }
            
            response = requests.post(
                self.providers['deepseek']['api_url'],
                headers=headers,
                json=payload,
                timeout=90
            )
            
            if response.status_code == 200:
                result = response.json()
                content = result['choices'][0]['message']['content']
                self.update_stats('deepseek', True, 0.0)
                return content
            else:
                logger.error(f"DeepSeek API error: {response.status_code}")
                self.update_stats('deepseek', False)
                return None
                
        except Exception as e:
            logger.error(f"DeepSeek error: {str(e)}")
            self.update_stats('deepseek', False)
            return None
    
    def generate_with_huggingface_free(self, prompt: str) -> Optional[str]:
        """Generate solution with Hugging Face free inference API (no API key needed)."""
        try:
            # Using Qwen2.5-Coder-32B-Instruct - excellent for code generation, free tier
            api_url = "https://api-inference.huggingface.co/models/Qwen/Qwen2.5-Coder-32B-Instruct"
            
            headers = {"Content-Type": "application/json"}
            
            payload = {
                "inputs": prompt,
                "parameters": {
                    "max_new_tokens": 2500,
                    "temperature": 0.7,
                    "return_full_text": False
                }
            }
            
            self.rate_limiter.wait_if_needed()
            
            response = requests.post(api_url, headers=headers, json=payload, timeout=90)
            
            if response.status_code == 200:
                result = response.json()
                if isinstance(result, list) and len(result) > 0:
                    content = result[0].get('generated_text', '')
                    if content:
                        self.update_stats('huggingface', True, 0.0)  # Free
                        return content
            else:
                logger.error(f"HuggingFace API error: {response.status_code}")
            
            self.update_stats('huggingface', False)
            return None
            
        except Exception as e:
            logger.error(f"HuggingFace error: {str(e)}")
            self.update_stats('huggingface', False)
            return None

    def generate_solution(self, prompt: str, preferred_provider: Optional[str] = None) -> Tuple[Optional[str], str]:
        """Generate solution with automatic fallback - Gemini first, then free providers."""
        providers_to_try = []
        
        # Priority order: Gemini first (we have ~170 quota left), then free alternatives
        if 'gemini' in self.providers:
            providers_to_try.append('gemini')
        
        # Add free provider as backup
        providers_to_try.append('huggingface')  # Free, no API key needed
        
        # Then add other paid providers if configured
        if preferred_provider and preferred_provider in self.providers and preferred_provider not in providers_to_try:
            providers_to_try.append(preferred_provider)
        
        for provider in ['openai', 'claude']:
            if provider in self.providers and provider not in providers_to_try:
                providers_to_try.append(provider)
        
        for provider in providers_to_try:
            provider_name = self.providers.get(provider, {}).get('name', provider.upper()) if provider in self.providers else "Hugging Face Free"
            logger.info(f"Trying {provider_name}...")
            
            result = None
            if provider == 'gemini':
                result = self.generate_with_gemini(prompt)
            elif provider == 'openai':
                result = self.generate_with_openai(prompt)
            elif provider == 'claude':
                result = self.generate_with_claude(prompt)
            elif provider == 'huggingface':
                result = self.generate_with_huggingface_free(prompt)
            
            if result:
                logger.info(f"✅ Success with {provider_name}")
                return result, provider
            
            # Add delay between provider attempts
            time.sleep(3)
        
        logger.error("❌ All providers failed")
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
        """Create optimized prompt for solution generation."""
        # Use description field if problem field is empty
        problem_text = problem.get('problem') or problem.get('description', 'Not specified')
        
        base_prompt = f"""
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
"""
        
        if provider == 'gemini':
            return base_prompt + "\n\nFocus on clean, efficient code with clear explanations."
        elif provider == 'openai':
            return base_prompt + "\n\nEmphasize best practices and comprehensive testing."
        elif provider == 'claude':
            return base_prompt + "\n\nProvide detailed reasoning and multiple approaches if applicable."
        
        return base_prompt
    
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
    
    parser = argparse.ArgumentParser(description='Threaded Multi-AI Solution Generator')
    parser.add_argument('--input', '-i', required=True, help='Input JSON file path')
    parser.add_argument('--output', '-o', help='Output JSON file path (default: input file)')
    parser.add_argument('--category', '-c', help='Category to process')
    parser.add_argument('--limit', '-l', type=int, help='Limit number of problems')
    parser.add_argument('--provider', '-p', choices=['gemini', 'openai', 'claude'], 
                        help='Preferred AI provider')
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
            logger.info(f"Would process {len(processable)} problems")
            
            if args.provider:
                logger.info(f"Would use preferred provider: {args.provider}")
            
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
                data, args.category, args.limit, args.provider, resume
            )
        else:
            logger.info("Processing all categories...")
            updated_data = data
            categories = sorted(set(p.get('category', '') for p in data if p.get('category')))
            
            for category in categories:
                if category.upper() not in ['SYSTEM DESIGN', 'SYSTEM_DESIGN']:
                    logger.info(f"\nProcessing category: {category}")
                    updated_data = generator.process_category_threaded(
                        updated_data, category, args.limit, args.provider, resume
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