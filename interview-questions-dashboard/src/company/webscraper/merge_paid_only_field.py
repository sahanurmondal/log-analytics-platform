#!/usr/bin/env python3
"""
Script to merge paid_only field from leetcode_questions.json into leetcode_ca_search_enhanced.json
based on title_slug matching.
"""

import json
import os
from pathlib import Path

def load_json_file(file_path):
    """Load JSON file and return the data."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    except FileNotFoundError:
        print(f"Error: File {file_path} not found")
        return None
    except json.JSONDecodeError as e:
        print(f"Error: Invalid JSON in {file_path}: {e}")
        return None

def save_json_file(data, file_path):
    """Save data to JSON file."""
    try:
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        return True
    except Exception as e:
        print(f"Error saving file {file_path}: {e}")
        return False

def create_backup(file_path):
    """Create a backup of the original file."""
    backup_path = f"{file_path}.backup"
    try:
        with open(file_path, 'r', encoding='utf-8') as src:
            with open(backup_path, 'w', encoding='utf-8') as dst:
                dst.write(src.read())
        print(f"Backup created: {backup_path}")
        return True
    except Exception as e:
        print(f"Error creating backup: {e}")
        return False

def merge_paid_only_field():
    """Main function to merge paid_only field."""
    # File paths
    base_dir = Path(__file__).parent
    leetcode_questions_path = base_dir / "multithreaded_extraction" / "leetcode_questions.json"
    enhanced_problems_path = base_dir / "leetcode_problem_solution" / "leetcode_ca_search_enhanced.json"
    
    print("Starting paid_only field merge process...")
    print(f"Source file: {leetcode_questions_path}")
    print(f"Target file: {enhanced_problems_path}")
    
    # Load leetcode questions data
    print("\nLoading leetcode questions data...")
    leetcode_questions = load_json_file(leetcode_questions_path)
    if not leetcode_questions:
        return False
    
    # Load enhanced problems data
    print("Loading enhanced problems data...")
    enhanced_problems = load_json_file(enhanced_problems_path)
    if not enhanced_problems:
        return False
    
    print(f"Loaded {len(leetcode_questions)} leetcode questions")
    print(f"Loaded {len(enhanced_problems)} enhanced problems")
    
    # Create backup
    print("\nCreating backup...")
    if not create_backup(enhanced_problems_path):
        return False
    
    # Create title_slug to paid_only mapping
    print("\nCreating title_slug to paid_only mapping...")
    paid_only_map = {}
    for question in leetcode_questions:
        title_slug = question.get('title_slug', '')
        paid_only = question.get('paid_only', False)
        if title_slug:
            paid_only_map[title_slug] = paid_only
    
    print(f"Created mapping for {len(paid_only_map)} questions")
    
    # Merge paid_only field into enhanced problems
    print("\nMerging paid_only field...")
    merged_count = 0
    not_found_count = 0
    
    for problem in enhanced_problems:
        title_slug = problem.get('title_slug', '')
        if title_slug in paid_only_map:
            problem['paid_only'] = paid_only_map[title_slug]
            merged_count += 1
        else:
            # Default to False if not found
            problem['paid_only'] = False
            if title_slug:  # Only count as not found if title_slug exists
                not_found_count += 1
    
    print(f"Successfully merged {merged_count} problems")
    print(f"Not found in leetcode_questions: {not_found_count} problems")
    
    # Save the updated data
    print("\nSaving updated data...")
    if save_json_file(enhanced_problems, enhanced_problems_path):
        print(f"Successfully updated {enhanced_problems_path}")
        
        # Show some statistics
        premium_count = sum(1 for p in enhanced_problems if p.get('paid_only', False))
        free_count = len(enhanced_problems) - premium_count
        
        print(f"\nStatistics:")
        print(f"Total problems: {len(enhanced_problems)}")
        print(f"Premium problems: {premium_count}")
        print(f"Free problems: {free_count}")
        print(f"Premium percentage: {premium_count/len(enhanced_problems)*100:.1f}%")
        
        return True
    else:
        print("Failed to save updated data")
        return False

if __name__ == "__main__":
    success = merge_paid_only_field()
    if success:
        print("\n✅ Merge completed successfully!")
    else:
        print("\n❌ Merge failed!")
    
    exit(0 if success else 1)