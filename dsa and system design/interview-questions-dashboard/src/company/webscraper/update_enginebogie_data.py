#!/usr/bin/env python3
"""
Script to update enginebogie_answer.json with enhanced data from complete_questions_dataset.json.
This script merges description, tags, company, and category fields based on question_number matching.
"""

import json
import os
from pathlib import Path
from datetime import datetime

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

def compare_fields(field_name, old_value, new_value):
    """Compare old and new field values and return update info."""
    if old_value == new_value:
        return False, "unchanged"
    
    # For lists (like tags), compare content
    if isinstance(old_value, list) and isinstance(new_value, list):
        if set(old_value) == set(new_value):
            return False, "unchanged"
        else:
            return True, f"tags updated: {len(old_value)} -> {len(new_value)} items"
    
    # For strings, check if significantly different
    if isinstance(old_value, str) and isinstance(new_value, str):
        if len(new_value) > len(old_value):
            return True, f"enhanced (length: {len(old_value)} -> {len(new_value)})"
        elif new_value != old_value:
            return True, "updated"
    
    return True, "updated"

def update_enginebogie_data():
    """Main function to update enginebogie_answer.json with complete_questions_dataset.json data."""
    # File paths
    base_dir = Path(__file__).parent
    source_file = base_dir / "multithreaded_extraction" / "complete_questions_dataset.json"
    target_file = base_dir / "enginebogie" / "enginebogie_answer.json"
    
    print("Starting enginebogie data update process...")
    print(f"Source file: {source_file}")
    print(f"Target file: {target_file}")
    
    # Load source data (complete questions dataset)
    print("\nLoading complete questions dataset...")
    source_data = load_json_file(source_file)
    if not source_data:
        return False
    
    # Load target data (enginebogie answer)
    print("Loading enginebogie answer data...")
    target_data = load_json_file(target_file)
    if not target_data:
        return False
    
    print(f"Loaded {len(source_data)} source questions")
    print(f"Loaded {len(target_data)} target questions")
    
    # Create backup
    print("\nCreating backup...")
    if not create_backup(target_file):
        return False
    
    # Create question_number to data mapping from source
    print("\nCreating question number mapping...")
    source_map = {}
    for question in source_data:
        question_num = question.get('question_number')
        if question_num:
            source_map[question_num] = question
    
    print(f"Created mapping for {len(source_map)} source questions")
    
    # Update target data with source data
    print("\nUpdating enginebogie data...")
    updates_count = {
        'total_processed': 0,
        'matched': 0,
        'not_found': 0,
        'description_updates': 0,
        'tags_updates': 0,
        'company_updates': 0,
        'category_updates': 0,
        'errors': 0
    }
    
    detailed_updates = []
    
    for i, target_question in enumerate(target_data):
        updates_count['total_processed'] += 1
        question_num = target_question.get('question_number')
        
        if question_num in source_map:
            updates_count['matched'] += 1
            source_question = source_map[question_num]
            
            question_updates = []
            
            # Update description
            if 'description' in source_question:
                updated, change_info = compare_fields('description', 
                                                    target_question.get('description', ''), 
                                                    source_question['description'])
                if updated:
                    target_question['description'] = source_question['description']
                    updates_count['description_updates'] += 1
                    question_updates.append(f"description: {change_info}")
            
            # Update tags
            if 'tags' in source_question:
                updated, change_info = compare_fields('tags', 
                                                    target_question.get('tags', []), 
                                                    source_question['tags'])
                if updated:
                    target_question['tags'] = source_question['tags']
                    updates_count['tags_updates'] += 1
                    question_updates.append(f"tags: {change_info}")
            
            # Update company
            if 'company' in source_question:
                updated, change_info = compare_fields('company', 
                                                    target_question.get('company', ''), 
                                                    source_question['company'])
                if updated:
                    target_question['company'] = source_question['company']
                    updates_count['company_updates'] += 1
                    question_updates.append(f"company: {change_info}")
            
            # Update category
            if 'category' in source_question:
                updated, change_info = compare_fields('category', 
                                                    target_question.get('category', ''), 
                                                    source_question['category'])
                if updated:
                    target_question['category'] = source_question['category']
                    updates_count['category_updates'] += 1
                    question_updates.append(f"category: {change_info}")
            
            # Add metadata about the update
            if question_updates:
                target_question['last_enhanced'] = datetime.now().isoformat()
                target_question['enhancement_source'] = 'complete_questions_dataset.json'
                detailed_updates.append({
                    'question_number': question_num,
                    'title': target_question.get('title', 'Unknown'),
                    'updates': question_updates
                })
        else:
            updates_count['not_found'] += 1
            if question_num:
                print(f"Warning: Question {question_num} not found in source data")
    
    # Save the updated data
    print("\nSaving updated data...")
    if save_json_file(target_data, target_file):
        print(f"Successfully updated {target_file}")
        
        # Show statistics
        print(f"\n📊 Update Statistics:")
        print(f"Total questions processed: {updates_count['total_processed']}")
        print(f"Successfully matched: {updates_count['matched']}")
        print(f"Not found in source: {updates_count['not_found']}")
        print(f"Description updates: {updates_count['description_updates']}")
        print(f"Tags updates: {updates_count['tags_updates']}")
        print(f"Company updates: {updates_count['company_updates']}")
        print(f"Category updates: {updates_count['category_updates']}")
        
        # Show detailed updates for first few questions
        if detailed_updates:
            print(f"\n📝 Sample Updates (first 10):")
            for update in detailed_updates[:10]:
                print(f"  Q{update['question_number']}: {update['title']}")
                for change in update['updates']:
                    print(f"    - {change}")
        
        match_percentage = (updates_count['matched'] / updates_count['total_processed']) * 100
        print(f"\n✅ Match rate: {match_percentage:.1f}%")
        
        return True
    else:
        print("Failed to save updated data")
        return False

if __name__ == "__main__":
    success = update_enginebogie_data()
    if success:
        print("\n🎉 Update completed successfully!")
    else:
        print("\n❌ Update failed!")
    
    exit(0 if success else 1)