#!/usr/bin/env python3
"""
Fast Semantic Matcher - Optimized for Speed

Simple and fast implementation:
1. Exact title matching only for high confidence
2. Basic text similarity for descriptions (no heavy AI)
3. Clear incorrect data when no match found
4. Process DSA and System Design categories
5. Maximum speed with all CPU cores
"""

import json
import os
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime
import time
import re
from dataclasses import dataclass
from typing import List, Dict, Optional, Tuple
import multiprocessing
from difflib import SequenceMatcher

@dataclass
class FastMatchResult:
    """Simple match result"""
    enginebogie_idx: int
    match_type: str
    leetcode_data: Optional[Dict]
    similarity_score: float
    reason: str

class FastSemanticMatcher:
    """Fast matcher without heavy AI dependencies"""
    
    def __init__(self, enginebogie_path: str, leetcode_path: str):
        self.enginebogie_path = enginebogie_path
        self.leetcode_path = leetcode_path
        
        self.enginebogie_data = []
        self.leetcode_data = []
        self.target_questions = []
        
        # Performance settings
        self.max_workers = multiprocessing.cpu_count()
        self.batch_size = 50  # Larger batches for simple processing
        
        # Progress tracking
        self.progress_lock = threading.Lock()
        self.processed_count = 0
        
        print(f"🚀 Fast Semantic Matcher - {self.max_workers} CPU cores")
    
    def load_data(self):
        """Load datasets quickly"""
        print("📁 Loading datasets...")
        
        with open(self.enginebogie_path, 'r', encoding='utf-8') as f:
            self.enginebogie_data = json.load(f)
        
        with open(self.leetcode_path, 'r', encoding='utf-8') as f:
            self.leetcode_data = json.load(f)
        
        # Filter DSA and System Design questions
        self.target_questions = [
            (i, q) for i, q in enumerate(self.enginebogie_data) 
            if q.get('category') in ['DSA', 'System Design']
        ]
        
        print(f"📊 {len(self.target_questions)} target questions (DSA + System Design)")
        print(f"📊 {len(self.leetcode_data)} LeetCode problems")
    
    def normalize_title(self, title: str) -> str:
        """Fast title normalization"""
        if not title:
            return ""
        
        # Remove numbers and normalize
        title = re.sub(r'^\d+\.\s*', '', title)
        title = title.lower().strip()
        
        # Remove articles and punctuation
        title = re.sub(r'\b(a|an|the)\b', '', title)
        title = re.sub(r'[^\w\s]', ' ', title)
        title = re.sub(r'\s+', ' ', title)
        
        return title.strip()
    
    def is_exact_title_match(self, title1: str, title2: str) -> bool:
        """Check for exact title match"""
        norm1 = self.normalize_title(title1)
        norm2 = self.normalize_title(title2)
        
        return norm1 == norm2 and len(norm1) > 3
    
    def clean_text(self, text: str) -> str:
        """Simple text cleaning"""
        if not text:
            return ""
        
        # Remove HTML and normalize
        text = re.sub(r'<[^>]+>', ' ', text)
        text = re.sub(r'\s+', ' ', text).strip()
        
        # Remove common prefixes
        text = re.sub(r'^(given|you are given|write|implement|design)', '', text, flags=re.IGNORECASE)
        
        return text
    
    def calculate_text_similarity(self, text1: str, text2: str) -> float:
        """Fast text similarity without AI"""
        clean1 = self.clean_text(text1)
        clean2 = self.clean_text(text2)
        
        if len(clean1) < 20 or len(clean2) < 20:
            return 0.0
        
        # Use sequence matcher for similarity
        similarity = SequenceMatcher(None, clean1.lower(), clean2.lower()).ratio()
        
        # Boost similarity for keyword matches
        words1 = set(clean1.lower().split())
        words2 = set(clean2.lower().split())
        
        # Remove common stop words
        stop_words = {'the', 'a', 'an', 'and', 'or', 'but', 'in', 'on', 'at', 'to', 'for', 'of', 'with'}
        words1 -= stop_words
        words2 -= stop_words
        
        if words1 and words2:
            keyword_overlap = len(words1.intersection(words2)) / len(words1.union(words2))
            # Combine sequence similarity with keyword overlap
            similarity = 0.6 * similarity + 0.4 * keyword_overlap
        
        return similarity
    
    def find_match(self, eng_question: Dict) -> FastMatchResult:
        """Find best match for a question"""
        eng_title = eng_question.get('title', '')
        eng_description = eng_question.get('description', '')
        
        # Strategy 1: Exact title match
        for leet_q in self.leetcode_data:
            leet_title = leet_q.get('problem_name', '') or leet_q.get('title', '')
            
            if self.is_exact_title_match(eng_title, leet_title):
                return FastMatchResult(
                    enginebogie_idx=-1,
                    match_type="exact_title_match",
                    leetcode_data=leet_q,
                    similarity_score=1.0,
                    reason=f"Exact title match"
                )
        
        # Strategy 2: Description similarity (if description exists)
        if len(eng_description) < 30:
            return FastMatchResult(
                enginebogie_idx=-1,
                match_type="cleared_incorrect_data", 
                leetcode_data=None,
                similarity_score=0.0,
                reason="Insufficient description"
            )
        
        best_match = None
        best_score = 0.0
        
        # Compare descriptions
        for leet_q in self.leetcode_data:
            leet_description = leet_q.get('description_text', '') or leet_q.get('description', '')
            
            if len(leet_description) < 30:
                continue
            
            score = self.calculate_text_similarity(eng_description, leet_description)
            
            if score > best_score:
                best_score = score
                best_match = leet_q
        
        # Determine match type based on score
        if best_score >= 0.8:
            match_type = "high_similarity_match"
            reason = f"High similarity: {best_score:.3f}"
        elif best_score >= 0.6:
            match_type = "medium_similarity_match"
            reason = f"Medium similarity: {best_score:.3f}"
        elif best_score >= 0.4:
            match_type = "low_similarity_match"
            reason = f"Low similarity: {best_score:.3f}"
        else:
            match_type = "cleared_incorrect_data"
            best_match = None
            reason = f"No confident match: {best_score:.3f}"
        
        return FastMatchResult(
            enginebogie_idx=-1,
            match_type=match_type,
            leetcode_data=best_match,
            similarity_score=best_score,
            reason=reason
        )
    
    def process_question(self, eng_idx: int, eng_question: Dict) -> FastMatchResult:
        """Process single question"""
        result = self.find_match(eng_question)
        result.enginebogie_idx = eng_idx
        
        # Update progress
        with self.progress_lock:
            self.processed_count += 1
            if self.processed_count % 100 == 0:
                progress = (self.processed_count / len(self.target_questions)) * 100
                print(f"⚡ Progress: {self.processed_count}/{len(self.target_questions)} ({progress:.1f}%)")
        
        return result
    
    def process_batch(self, batch: List[Tuple[int, Dict]]) -> List[FastMatchResult]:
        """Process batch of questions"""
        results = []
        for eng_idx, eng_question in batch:
            result = self.process_question(eng_idx, eng_question)
            results.append(result)
        return results
    
    def process_all_questions(self) -> List[FastMatchResult]:
        """Process all questions with maximum speed"""
        print(f"🚀 Starting fast processing with {self.max_workers} threads...")
        
        # Create batches
        batches = []
        for i in range(0, len(self.target_questions), self.batch_size):
            batch = self.target_questions[i:i + self.batch_size]
            batches.append(batch)
        
        print(f"📦 {len(batches)} batches of {self.batch_size} questions")
        
        all_results = []
        self.processed_count = 0
        
        # Process with all CPU cores
        with ThreadPoolExecutor(max_workers=self.max_workers) as executor:
            future_to_batch = {
                executor.submit(self.process_batch, batch): i
                for i, batch in enumerate(batches)
            }
            
            for future in as_completed(future_to_batch):
                try:
                    batch_results = future.result()
                    all_results.extend(batch_results)
                except Exception as e:
                    batch_id = future_to_batch[future]
                    print(f"❌ Error in batch {batch_id}: {e}")
        
        return all_results
    
    def analyze_results(self, results: List[FastMatchResult]) -> Dict[str, int]:
        """Analyze results"""
        print("\n" + "="*50)
        print("⚡ FAST SEMANTIC ANALYSIS REPORT")
        print("="*50)
        
        # Count by type
        exact_matches = [r for r in results if r.match_type == "exact_title_match"]
        high_sim = [r for r in results if r.match_type == "high_similarity_match"]
        medium_sim = [r for r in results if r.match_type == "medium_similarity_match"]
        low_sim = [r for r in results if r.match_type == "low_similarity_match"]
        cleared = [r for r in results if r.match_type == "cleared_incorrect_data"]
        
        total = len(results)
        
        stats = {
            'total': total,
            'exact_title': len(exact_matches),
            'high_similarity': len(high_sim),
            'medium_similarity': len(medium_sim),
            'low_similarity': len(low_sim),
            'cleared': len(cleared)
        }
        
        print(f"📊 Results:")
        print(f"   Total: {total}")
        print(f"   Exact Title: {stats['exact_title']} ({stats['exact_title']/total*100:.1f}%)")
        print(f"   High Similarity: {stats['high_similarity']} ({stats['high_similarity']/total*100:.1f}%)")
        print(f"   Medium Similarity: {stats['medium_similarity']} ({stats['medium_similarity']/total*100:.1f}%)")
        print(f"   Low Similarity: {stats['low_similarity']} ({stats['low_similarity']/total*100:.1f}%)")
        print(f"   Cleared: {stats['cleared']} ({stats['cleared']/total*100:.1f}%)")
        
        # Show examples
        quality_matches = exact_matches + high_sim
        if quality_matches:
            print(f"\n🎯 Quality Matches (Examples):")
            for i, result in enumerate(quality_matches[:5]):
                eng_q = self.enginebogie_data[result.enginebogie_idx]
                if result.leetcode_data:
                    print(f"{i+1}. \"{eng_q.get('title', '')}\"")
                    print(f"   → #{result.leetcode_data.get('leetcode_problem_no', '')}: {result.leetcode_data.get('problem_name', '')}")
                    print(f"   Type: {result.match_type}, Score: {result.similarity_score:.3f}")
        
        return stats
    
    def update_dataset(self, results: List[FastMatchResult]):
        """Update dataset with results"""
        print("💾 Updating dataset...")
        
        updated = 0
        cleared = 0
        
        for result in results:
            question = self.enginebogie_data[result.enginebogie_idx]
            
            if result.leetcode_data and result.match_type != "cleared_incorrect_data":
                # Update with LeetCode data
                question['problem_name'] = result.leetcode_data.get('problem_name', '')
                question['leetcode_problem_no'] = result.leetcode_data.get('leetcode_problem_no', '')
                question['leetcode_url'] = result.leetcode_data.get('leetcode_url', '')
                question['title_slug'] = result.leetcode_data.get('title_slug', '')
                question['solution_url'] = result.leetcode_data.get('solution_url', '')
                
                question['match_type'] = result.match_type
                question['similarity_score'] = result.similarity_score
                question['match_reason'] = result.reason
                question['last_updated'] = datetime.now().isoformat()
                question['matching_method'] = 'fast_semantic'
                
                updated += 1
            else:
                # Clear incorrect data
                question['problem_name'] = ''
                question['leetcode_problem_no'] = ''
                question['leetcode_url'] = ''
                question['title_slug'] = ''
                question['solution_url'] = ''
                
                question['match_type'] = 'cleared_incorrect_data'
                question['match_reason'] = result.reason
                question['last_updated'] = datetime.now().isoformat()
                question['matching_method'] = 'fast_semantic'
                
                cleared += 1
        
        # Backup and save
        backup_path = f"{self.enginebogie_path}.fast_backup_{int(time.time())}"
        import shutil
        shutil.copy2(self.enginebogie_path, backup_path)
        
        with open(self.enginebogie_path, 'w', encoding='utf-8') as f:
            json.dump(self.enginebogie_data, f, indent=2, ensure_ascii=False)
        
        print(f"✅ Updated: {updated}, Cleared: {cleared}")
        return {'updated': updated, 'cleared': cleared}

def main():
    """Main execution"""
    print("⚡ FAST SEMANTIC MATCHER")
    print("=" * 30)
    
    # File paths
    script_dir = os.path.dirname(os.path.abspath(__file__))
    enginebogie_path = os.path.join(script_dir, 
        "src/company/webscraper/enginebogie/enginebogie_answer.json")
    leetcode_path = os.path.join(script_dir, 
        "src/company/webscraper/leetcode_problem_solution/leetcode_ca_search_enhanced.json")
    
    # Check files
    for path, name in [(enginebogie_path, "Enginebogie"), (leetcode_path, "LeetCode")]:
        if not os.path.exists(path):
            print(f"❌ {name} file not found")
            return
    
    start_time = time.time()
    
    try:
        matcher = FastSemanticMatcher(enginebogie_path, leetcode_path)
        matcher.load_data()
        
        results = matcher.process_all_questions()
        stats = matcher.analyze_results(results)
        updates = matcher.update_dataset(results)
        
        elapsed = time.time() - start_time
        print(f"\n⚡ COMPLETE! Time: {elapsed:.1f}s")
        print(f"Speed: {elapsed/len(results):.3f}s per question")
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    main()