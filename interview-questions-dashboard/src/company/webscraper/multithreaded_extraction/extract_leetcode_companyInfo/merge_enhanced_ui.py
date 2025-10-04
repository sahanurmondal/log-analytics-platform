#!/usr/bin/env python3
"""
Enhanced LeetCode Data Merger for UI - with LeetCode URL, Solution URL, Problem Number

Merges company data with LeetCode problem metadata to create a comprehensive dataset
optimized for UI operations with complete problem information.

Features:
- Converts titleSlug to title_slug for consistency
- Adds LeetCode URL, solution URL, problem number
- Fixes difficulty and paidOnly fields from reference data
- Optimized for search, filter, and sort operations
"""

import json
import os
import time
from collections import defaultdict
from typing import Dict, List, Set, Optional
import logging
from pathlib import Path

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def load_leetcode_reference_data(reference_file: str) -> Dict[str, Dict]:
    """Load LeetCode reference data and create title_slug lookup"""
    logger.info(f"📚 Loading LeetCode reference data from {reference_file}")
    
    try:
        with open(reference_file, 'r', encoding='utf-8') as f:
            reference_data = json.load(f)
        
        # Create lookup by title_slug
        title_slug_lookup = {}
        for problem in reference_data:
            title_slug = problem.get('title_slug', '')
            if title_slug:
                title_slug_lookup[title_slug] = {
                    'problem_no': problem.get('frontend_id', problem.get('id', '')),
                    'difficulty': problem.get('difficulty', 'Unknown'),
                    'paid_only': problem.get('paid_only', False),
                    'leetcode_url': problem.get('url', f"https://leetcode.com/problems/{title_slug}/"),
                    'has_solution': problem.get('has_solution', False),
                    'has_video_solution': problem.get('has_video_solution', False)
                }
        
        logger.info(f"✅ Loaded {len(title_slug_lookup)} problems from reference data")
        return title_slug_lookup
        
    except Exception as e:
        logger.error(f"❌ Error loading reference data: {e}")
        return {}

def convert_difficulty_to_number(difficulty_str: str) -> int:
    """Convert difficulty string to number"""
    difficulty_map = {
        'Easy': 1,
        'Medium': 2,
        'Hard': 3
    }
    return difficulty_map.get(difficulty_str, 1)

def merge_enhanced_leetcode_data(extract_dir: str, output_dir: str, reference_file: str, test_companies: Optional[List[str]] = None):
    """
    Merge all LeetCode data with enhanced metadata for UI optimization
    
    Args:
        extract_dir: Directory containing time period folders
        output_dir: Directory to save merged results
        reference_file: Path to leetcode_questions.json reference file
        test_companies: List of company names to test with (None for all)
    """
    
    print("🚀 ENHANCED LEETCODE DATA MERGER FOR UI")
    print("=" * 60)
    
    # Load reference data
    reference_lookup = load_leetcode_reference_data(reference_file)
    if not reference_lookup:
        logger.error("❌ Failed to load reference data. Exiting.")
        return
    
    # Create output directory
    os.makedirs(output_dir, exist_ok=True)
    
    # Time periods to process
    time_periods = ['thirty-days', 'three-months', 'six-months', 'more-than-six-months', 'all']
    
    # Main data structures for UI optimization
    problems_db = {}  # title_slug -> problem data
    companies_index = defaultdict(set)  # company -> set of title_slugs
    tags_index = defaultdict(set)  # tag -> set of title_slugs
    difficulty_index = defaultdict(set)  # difficulty -> set of title_slugs
    time_period_index = defaultdict(set)  # time_period -> set of title_slugs
    frequency_index = defaultdict(list)  # frequency_range -> list of title_slugs
    paid_only_index = defaultdict(set)  # paid_status -> set of title_slugs
    
    # Statistics
    stats = {
        'total_problems': 0,
        'total_companies': 0,
        'enhanced_problems': 0,
        'missing_reference': 0,
        'companies_processed': set(),
        'processing_time': 0
    }
    
    start_time = time.time()
    
    print(f"📁 Processing time periods: {', '.join(time_periods)}")
    if test_companies:
        print(f"🧪 Testing with companies: {', '.join(test_companies)}")
    
    # Process each time period
    for time_period in time_periods:
        period_dir = os.path.join(extract_dir, time_period)
        if not os.path.exists(period_dir):
            print(f"⚠️  Skipping {time_period} - directory not found")
            continue
        
        print(f"\n📅 Processing {time_period}...")
        companies_in_period = []
        
        # Get companies in this time period
        for company_folder in os.listdir(period_dir):
            company_path = os.path.join(period_dir, company_folder)
            if os.path.isdir(company_path):
                companies_in_period.append(company_folder)
        
        # Filter to test companies if specified
        if test_companies:
            companies_in_period = [c for c in companies_in_period 
                                 if c.lower() in [tc.lower() for tc in test_companies]]
        
        print(f"   📊 Found {len(companies_in_period)} companies")
        
        # Process each company
        for company_name in companies_in_period:
            company_path = os.path.join(period_dir, company_name)
            stats['companies_processed'].add(company_name)
            
            # Process all JSON files for this company
            for filename in os.listdir(company_path):
                if filename.endswith('.json'):
                    file_path = os.path.join(company_path, filename)
                    
                    try:
                        with open(file_path, 'r', encoding='utf-8') as f:
                            page_data = json.load(f)
                        
                        if 'problems' not in page_data:
                            continue
                        
                        # Process each problem
                        for problem in page_data['problems']:
                            title_slug = problem.get('titleSlug', '')
                            if not title_slug:
                                continue
                            
                            # Get reference data for this problem
                            ref_data = reference_lookup.get(title_slug, {})
                            
                            # Initialize or update problem in database
                            if title_slug not in problems_db:
                                # Create enhanced problem entry
                                problems_db[title_slug] = {
                                    'title': problem.get('title', ''),
                                    'title_slug': title_slug,  # Changed from titleSlug
                                    'problem_no': ref_data.get('problem_no', ''),
                                    'tags': problem.get('tags', []),
                                    'difficulty': convert_difficulty_to_number(ref_data.get('difficulty', 'Easy')),
                                    'difficulty_text': ref_data.get('difficulty', 'Easy'),
                                    'paid_only': ref_data.get('paid_only', problem.get('paidOnly', False)),
                                    'leetcode_url': ref_data.get('leetcode_url', f"https://leetcode.com/problems/{title_slug}/"),
                                    'solution_url': f"https://leetcode.com/problems/{title_slug}/solutions/",
                                    'has_solution': ref_data.get('has_solution', False),
                                    'has_video_solution': ref_data.get('has_video_solution', False),
                                    'companies': {},
                                    'maxFrequency': 0,
                                    'totalFrequency': 0,
                                    'companiesCount': 0,
                                    'timePeriods': set()
                                }
                                
                                if ref_data:
                                    stats['enhanced_problems'] += 1
                                else:
                                    stats['missing_reference'] += 1
                                    logger.warning(f"⚠️  No reference data found for: {title_slug}")
                            
                            prob_data = problems_db[title_slug]
                            frequency = problem.get('frequency', 0)
                            
                            # Update company data for this problem
                            if company_name not in prob_data['companies']:
                                prob_data['companies'][company_name] = {
                                    'frequencies': {},
                                    'timePeriods': [],
                                    'maxFrequency': 0
                                }
                            
                            company_data = prob_data['companies'][company_name]
                            company_data['frequencies'][time_period] = frequency
                            if time_period not in company_data['timePeriods']:
                                company_data['timePeriods'].append(time_period)
                            
                            # Update max frequency for company
                            if frequency > company_data['maxFrequency']:
                                company_data['maxFrequency'] = frequency
                            
                            # Update global problem data
                            if frequency > prob_data['maxFrequency']:
                                prob_data['maxFrequency'] = frequency
                            prob_data['totalFrequency'] += frequency
                            prob_data['timePeriods'].add(time_period)
                            prob_data['companiesCount'] = len(prob_data['companies'])
                            
                            # Update indexes for fast searching
                            companies_index[company_name].add(title_slug)
                            time_period_index[time_period].add(title_slug)
                            difficulty_index[prob_data['difficulty']].add(title_slug)
                            paid_only_index[prob_data['paid_only']].add(title_slug)
                            
                            # Tag indexing
                            for tag in prob_data['tags']:
                                tags_index[tag].add(title_slug)
                            
                            # Frequency range indexing for fast filtering
                            freq_range = get_frequency_range(frequency)
                            frequency_index[freq_range].append(title_slug)
                    
                    except Exception as e:
                        logger.error(f"❌ Error processing {file_path}: {e}")
        
        print(f"   ✅ Completed {time_period}")
    
    # Convert sets to lists for JSON serialization
    for prob_data in problems_db.values():
        prob_data['timePeriods'] = sorted(list(prob_data['timePeriods']))
    
    # Create final enhanced structure
    enhanced_data = {
        'metadata': {
            'generatedAt': time.strftime('%Y-%m-%d %H:%M:%S'),
            'version': '2.0',
            'totalProblems': len(problems_db),
            'totalCompanies': len(stats['companies_processed']),
            'enhancedProblems': stats['enhanced_problems'],
            'missingReference': stats['missing_reference'],
            'totalTags': len(tags_index),
            'timePeriods': time_periods,
            'difficultyLevels': [
                {'value': 1, 'label': 'Easy'},
                {'value': 2, 'label': 'Medium'},
                {'value': 3, 'label': 'Hard'}
            ],
            'frequencyRanges': [
                {'value': 'very-high', 'label': 'Very High (80+)', 'min': 80},
                {'value': 'high', 'label': 'High (60-79)', 'min': 60},
                {'value': 'medium', 'label': 'Medium (40-59)', 'min': 40},
                {'value': 'low', 'label': 'Low (20-39)', 'min': 20},
                {'value': 'very-low', 'label': 'Very Low (0-19)', 'min': 0}
            ],
            'processingTime': time.time() - start_time
        },
        'problems': list(problems_db.values()),
        'indexes': {
            'companies': {company: list(problems) for company, problems in companies_index.items()},
            'tags': {tag: list(problems) for tag, problems in tags_index.items()},
            'difficulty': {str(diff): list(problems) for diff, problems in difficulty_index.items()},
            'timePeriods': {period: list(problems) for period, problems in time_period_index.items()},
            'frequencyRanges': {range_name: list(set(problems)) for range_name, problems in frequency_index.items()},
            'paidOnly': {str(paid): list(problems) for paid, problems in paid_only_index.items()}
        },
        'analytics': {
            'topCompanies': get_top_companies(companies_index, 30),
            'topTags': get_top_tags(tags_index, 50),
            'difficultyDistribution': get_difficulty_distribution(difficulty_index),
            'frequencyDistribution': get_frequency_distribution(problems_db),
            'paidOnlyDistribution': get_paid_only_distribution(problems_db)
        }
    }
    
    # Save different formats for different use cases
    save_enhanced_files(enhanced_data, output_dir, test_companies is not None)
    
    # Print final statistics
    print_enhanced_stats(enhanced_data, stats)

def get_frequency_range(frequency: int) -> str:
    """Categorize frequency into ranges for fast filtering"""
    if frequency >= 80:
        return 'very-high'
    elif frequency >= 60:
        return 'high'
    elif frequency >= 40:
        return 'medium'
    elif frequency >= 20:
        return 'low'
    else:
        return 'very-low'

def get_top_companies(companies_index: Dict, limit: int) -> List[Dict]:
    """Get top companies by problem count"""
    company_counts = [(company, len(problems)) for company, problems in companies_index.items()]
    company_counts.sort(key=lambda x: x[1], reverse=True)
    return [{'name': company, 'problemCount': count} for company, count in company_counts[:limit]]

def get_top_tags(tags_index: Dict, limit: int) -> List[Dict]:
    """Get top tags by problem count"""
    tag_counts = [(tag, len(problems)) for tag, problems in tags_index.items()]
    tag_counts.sort(key=lambda x: x[1], reverse=True)
    return [{'name': tag, 'problemCount': count} for tag, count in tag_counts[:limit]]

def get_difficulty_distribution(difficulty_index: Dict) -> Dict:
    """Get distribution of problems by difficulty"""
    return {
        'easy': len(difficulty_index.get(1, [])),
        'medium': len(difficulty_index.get(2, [])),
        'hard': len(difficulty_index.get(3, []))
    }

def get_frequency_distribution(problems_db: Dict) -> Dict:
    """Get distribution of problems by frequency ranges"""
    distribution = {'very-high': 0, 'high': 0, 'medium': 0, 'low': 0, 'very-low': 0}
    for problem in problems_db.values():
        range_name = get_frequency_range(problem['maxFrequency'])
        distribution[range_name] += 1
    return distribution

def get_paid_only_distribution(problems_db: Dict) -> Dict:
    """Get distribution of paid vs free problems"""
    distribution = {'free': 0, 'paid': 0}
    for problem in problems_db.values():
        if problem['paid_only']:
            distribution['paid'] += 1
        else:
            distribution['free'] += 1
    return distribution

def save_enhanced_files(data: Dict, output_dir: str, is_test: bool):
    """Save different file formats optimized for different use cases"""
    
    prefix = "test_" if is_test else ""
    
    # 1. Complete enhanced dataset for full-featured UI
    complete_file = os.path.join(output_dir, f"{prefix}leetcode_enhanced_complete.json")
    with open(complete_file, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    file_size = os.path.getsize(complete_file) / (1024 * 1024)  # MB
    print(f"📁 Saved enhanced complete dataset: {complete_file} ({file_size:.1f} MB)")
    
    # 2. Compact version without indexes (smaller file size)
    compact_data = {
        'metadata': data['metadata'],
        'problems': data['problems'],
        'analytics': data['analytics']
    }
    compact_file = os.path.join(output_dir, f"{prefix}leetcode_enhanced_compact.json")
    with open(compact_file, 'w', encoding='utf-8') as f:
        json.dump(compact_data, f, separators=(',', ':'), ensure_ascii=False)
    file_size = os.path.getsize(compact_file) / (1024 * 1024)  # MB
    print(f"📁 Saved enhanced compact dataset: {compact_file} ({file_size:.1f} MB)")
    
    # 3. Index-only file for fast filtering
    index_file = os.path.join(output_dir, f"{prefix}leetcode_enhanced_indexes.json")
    with open(index_file, 'w', encoding='utf-8') as f:
        json.dump(data['indexes'], f, separators=(',', ':'), ensure_ascii=False)
    file_size = os.path.getsize(index_file) / (1024 * 1024)  # MB
    print(f"📁 Saved enhanced indexes: {index_file} ({file_size:.1f} MB)")
    
    # 4. Analytics summary
    analytics_file = os.path.join(output_dir, f"{prefix}leetcode_enhanced_analytics.json")
    with open(analytics_file, 'w', encoding='utf-8') as f:
        json.dump(data['analytics'], f, indent=2, ensure_ascii=False)
    print(f"📁 Saved enhanced analytics: {analytics_file}")
    
    # 5. Problems-only array for simple UI integration
    problems_only_file = os.path.join(output_dir, f"{prefix}leetcode_problems_only.json")
    with open(problems_only_file, 'w', encoding='utf-8') as f:
        json.dump(data['problems'], f, separators=(',', ':'), ensure_ascii=False)
    file_size = os.path.getsize(problems_only_file) / (1024 * 1024)  # MB
    print(f"📁 Saved problems-only dataset: {problems_only_file} ({file_size:.1f} MB)")

def print_enhanced_stats(data: Dict, stats: Dict):
    """Print final processing statistics"""
    print("\n" + "="*70)
    print("🎯 ENHANCED LEETCODE DATA MERGER REPORT")
    print("="*70)
    
    metadata = data['metadata']
    print(f"\n📊 DATASET SUMMARY:")
    print(f"   Total Problems: {metadata['totalProblems']:,}")
    print(f"   Enhanced Problems: {metadata['enhancedProblems']:,}")
    print(f"   Missing Reference: {metadata['missingReference']:,}")
    print(f"   Total Companies: {metadata['totalCompanies']:,}")
    print(f"   Total Tags: {metadata['totalTags']:,}")
    print(f"   Time Periods: {len(metadata['timePeriods'])}")
    print(f"   Processing Time: {metadata['processingTime']:.2f} seconds")
    
    analytics = data['analytics']
    print(f"\n🏆 TOP COMPANIES:")
    for company in analytics['topCompanies'][:10]:
        print(f"   {company['name']}: {company['problemCount']} problems")
    
    print(f"\n🏷️  TOP TAGS:")
    for tag in analytics['topTags'][:10]:
        print(f"   {tag['name']}: {tag['problemCount']} problems")
    
    print(f"\n📈 DIFFICULTY DISTRIBUTION:")
    diff_dist = analytics['difficultyDistribution']
    print(f"   Easy: {diff_dist['easy']:,} problems")
    print(f"   Medium: {diff_dist['medium']:,} problems")
    print(f"   Hard: {diff_dist['hard']:,} problems")
    
    print(f"\n💰 PAID/FREE DISTRIBUTION:")
    paid_dist = analytics['paidOnlyDistribution']
    print(f"   Free: {paid_dist['free']:,} problems")
    print(f"   Paid: {paid_dist['paid']:,} problems")
    
    print(f"\n🎯 FREQUENCY DISTRIBUTION:")
    freq_dist = analytics['frequencyDistribution']
    for range_name, count in freq_dist.items():
        print(f"   {range_name.replace('-', ' ').title()}: {count:,} problems")
    
    print(f"\n🎉 Enhanced merge complete! Files saved in output directory.")

def main():
    """Main function with test and full run options"""
    
    extract_dir = "extract_leetcode_companyInfo"
    output_dir = "merged_enhanced"
    reference_file = "../leetcode_questions.json"
    
    # Check if reference file exists
    if not os.path.exists(reference_file):
        print(f"❌ Reference file not found: {reference_file}")
        print("Please ensure leetcode_questions.json exists in the parent directory.")
        return
    
    print("🤔 Choose processing mode:")
    print("1. Test with 2 companies (fast)")
    print("2. Process all companies (comprehensive)")
    
    choice = input("Enter choice (1 or 2): ").strip()
    
    if choice == "1":
        print("\n🧪 Running in TEST MODE with 2 companies...")
        test_companies = ['google', 'apple']  # Test with these companies
        merge_enhanced_leetcode_data(extract_dir, output_dir, reference_file, test_companies)
    elif choice == "2":
        print("\n🚀 Running FULL PROCESSING for all companies...")
        merge_enhanced_leetcode_data(extract_dir, output_dir, reference_file)
    else:
        print("❌ Invalid choice. Exiting.")
        return
    
    print(f"\n✅ Enhanced processing complete! Check the '{output_dir}' directory for results.")

if __name__ == "__main__":
    main()