#!/usr/bin/env python3
"""
Full LeetCode Company Data Extraction
Run for all companies across all time periods
"""

import json
import time
import os
import multiprocessing
from leetcode_company_extractor import LeetCodeCompanyExtractor

def main():
    """Run full extraction for all companies across all time periods"""
    print("🚀 FULL LEETCODE COMPANY EXTRACTION (ALL TIME PERIODS)")
    print("=" * 70)
    
    # Define all time periods to extract
    time_periods = [
        'thirty-days',
        'three-months', 
        'six-months',
        'more-than-six-months',
        'all'
    ]
    
    # Load all companies
    with open('../leetcode_wizard.json', 'r') as f:
        all_companies = json.load(f)
    
    # Calculate optimal thread count (max available - 1)
    max_threads = max(1, multiprocessing.cpu_count() - 1)
    
    print(f"📊 Total companies: {len(all_companies)}")
    print(f"⏰ Time periods: {', '.join(time_periods)}")
    print(f"📁 Output structure: extract_leetcode_companyInfo/{'{time_period}'}/{'{company}'}/*.json")
    print(f"📄 Page size: 50 problems per page")
    print(f"🧵 Workers: {max_threads} concurrent threads (CPU cores: {multiprocessing.cpu_count()})")
    print("=" * 70)
    
    # Check existing extraction status for each time period
    base_output_dir = "extract_leetcode_companyInfo"
    total_extractions_needed = len(all_companies) * len(time_periods)
    total_completed = 0
    
    for time_period in time_periods:
        period_dir = os.path.join(base_output_dir, time_period)
        existing_companies = set()
        
        if os.path.exists(period_dir):
            for item in os.listdir(period_dir):
                item_path = os.path.join(period_dir, item)
                if os.path.isdir(item_path):
                    existing_companies.add(item)
        
        completed_for_period = len(existing_companies)
        total_completed += completed_for_period
        
        print(f"� {time_period}: {completed_for_period}/{len(all_companies)} companies extracted")
        if existing_companies and len(existing_companies) < 10:  # Only show if manageable list
            print(f"   ✅ Completed: {', '.join(sorted(list(existing_companies)))}")
    
    remaining_extractions = total_extractions_needed - total_completed
    print(f"\n🎯 Progress: {total_completed}/{total_extractions_needed} extractions completed")
    print(f"� Remaining: {remaining_extractions} extractions needed")
    
    if remaining_extractions == 0:
        print("🎉 All companies already extracted for all time periods! Nothing to do.")
        return
    
    # Ask for confirmation
    response = input(f"\n🤔 Continue with {remaining_extractions} remaining extractions? (y/N): ").strip().lower()
    if response != 'y':
        print("❌ Extraction cancelled.")
        return
    
    print(f"\n🏁 Starting extraction...")
    overall_start_time = time.time()
    
    # Extract for each time period
    for i, time_period in enumerate(time_periods, 1):
        print(f"\n{'='*50}")
        print(f"📅 TIME PERIOD {i}/{len(time_periods)}: {time_period.upper()}")
        print(f"{'='*50}")
        
        # Check which companies need extraction for this time period
        period_dir = os.path.join(base_output_dir, time_period)
        existing_companies = set()
        
        if os.path.exists(period_dir):
            for item in os.listdir(period_dir):
                item_path = os.path.join(period_dir, item)
                if os.path.isdir(item_path):
                    existing_companies.add(item)
        
        # Filter out existing companies for this time period
        remaining_companies = {
            company: uuid for company, uuid in all_companies.items()
            if company.replace(' ', '_').lower() not in existing_companies
        }
        
        if not remaining_companies:
            print(f"✅ All companies already extracted for {time_period}")
            continue
        
        print(f"🔄 Extracting {len(remaining_companies)} companies for {time_period}...")
        
        # Initialize extractor for this time period
        extractor = LeetCodeCompanyExtractor(max_workers=max_threads, time_period=time_period)
        
        period_start_time = time.time()
        
        # Run extraction for remaining companies in this time period
        extractor.extract_all_companies(remaining_companies)
        
        period_end_time = time.time()
        period_duration = period_end_time - period_start_time
        
        print(f"✅ Completed {time_period} in {period_duration:.2f} seconds ({period_duration/60:.1f} minutes)")
    
    overall_end_time = time.time()
    overall_duration = overall_end_time - overall_start_time
    
    print(f"\n🎯 FULL EXTRACTION COMPLETED!")
    print(f"⏱️  Total duration: {overall_duration:.2f} seconds ({overall_duration/60:.1f} minutes)")
    print(f"📊 Total companies: {len(all_companies)}")
    print(f"📅 Time periods: {len(time_periods)}")
    print(f"📁 Total extractions: {len(all_companies) * len(time_periods)}")
    print(f"📁 Check the 'extract_leetcode_companyInfo' directory structure:")
    for time_period in time_periods:
        print(f"   📂 {time_period}/")
        print(f"      └── {'{company_name}'}/*.json")

if __name__ == "__main__":
    main()