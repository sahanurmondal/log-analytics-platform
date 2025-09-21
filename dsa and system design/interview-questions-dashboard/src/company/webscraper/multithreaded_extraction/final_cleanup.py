#!/usr/bin/env python3
"""
Final cleanup script for comprehensive_answers_enhanced.json
- Remove all approaches sections (including nested ones in answer.approaches)
- Fix category to DSA for all coding questions
- Fix question_type to original category value for coding questions
- Sort all fields alphabetically
"""

import json
import os
from collections import OrderedDict

def load_json_file(filepath):
    """Load JSON data from file"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            return json.load(f)
    except Exception as e:
        print(f"❌ Error loading {filepath}: {e}")
        return None

def save_json_file(filepath, data):
    """Save JSON data to file"""
    try:
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        return True
    except Exception as e:
        print(f"❌ Error saving {filepath}: {e}")
        return False

def sort_dict_fields(obj):
    """Recursively sort dictionary fields alphabetically"""
    if isinstance(obj, dict):
        # Sort the dictionary keys
        sorted_dict = OrderedDict()
        for key in sorted(obj.keys()):
            sorted_dict[key] = sort_dict_fields(obj[key])
        return sorted_dict
    elif isinstance(obj, list):
        # For lists, sort each item if it's a dictionary
        return [sort_dict_fields(item) for item in obj]
    else:
        return obj

def remove_unwanted_sections(obj):
    """Recursively remove unwanted sections from nested objects"""
    if isinstance(obj, dict):
        # Create a new dict without unwanted keys
        cleaned_dict = {}
        for key, value in obj.items():
            if key not in ['approaches', 'references', 'source_metadata']:
                cleaned_dict[key] = remove_unwanted_sections(value)
        return cleaned_dict
    elif isinstance(obj, list):
        # Clean each item in the list
        return [remove_unwanted_sections(item) for item in obj]
    else:
        return obj

def final_cleanup_and_restructure():
    """Main function to do final cleanup and restructure the answers"""
    
    print("🚀 FINAL CLEANUP AND RESTRUCTURING")
    print("=" * 70)
    
    # Load the enhanced answers file
    print("📚 Loading enhanced answers...")
    answers_data = load_json_file("comprehensive_answers_enhanced.json")
    if not answers_data:
        return False
    
    print(f"📊 Loaded {len(answers_data)} answers")
    
    # Create backup
    print("💾 Creating backup...")
    if not save_json_file("comprehensive_answers_enhanced_final_backup.json", answers_data):
        return False
    
    # Process each answer
    print("🔄 Processing answers...")
    approaches_removed = 0
    coding_questions_fixed = 0
    
    for i, answer in enumerate(answers_data):
        # Count approaches sections before removal
        if isinstance(answer, dict) and 'answer' in answer and isinstance(answer['answer'], dict):
            if 'approaches' in answer['answer']:
                approaches_removed += 1
        
        # Remove all unwanted sections recursively
        cleaned_answer = remove_unwanted_sections(answer)
        
        # Fix category and question_type for coding questions
        if isinstance(cleaned_answer, dict) and cleaned_answer.get('question_type') == 'coding':
            original_category = cleaned_answer.get('category', '')
            cleaned_answer['category'] = 'DSA'
            cleaned_answer['question_type'] = original_category if original_category else 'Algorithms'
            coding_questions_fixed += 1
        
        # Sort all fields alphabetically
        answers_data[i] = sort_dict_fields(cleaned_answer)
        
        # Progress indicator
        if (i + 1) % 100 == 0:
            print(f"   ✅ Processed {i + 1}/{len(answers_data)} answers")
    
    # Save cleaned and restructured data
    print("💾 Saving final cleaned data...")
    if not save_json_file("comprehensive_answers_enhanced.json", answers_data):
        return False
    
    print("\n" + "=" * 70)
    print("🎯 FINAL CLEANUP SUMMARY")
    print("=" * 70)
    print(f"📊 Total answers processed: {len(answers_data)}")
    print(f"🧹 Approaches sections removed: {approaches_removed}")
    print(f"🔧 Coding questions fixed: {coding_questions_fixed}")
    print(f"📁 Backup saved as: comprehensive_answers_enhanced_final_backup.json")
    print(f"📁 Final file: comprehensive_answers_enhanced.json")
    
    # Show some examples of cleaned entries
    print("\n🔍 Sample of fixed coding questions:")
    count = 0
    for answer in answers_data:
        if answer.get('category') == 'DSA' and count < 3:
            print(f"   {answer.get('question_number', 'N/A')}. {answer.get('problem_name', answer.get('title', 'N/A'))}")
            print(f"      Category: {answer.get('category', 'N/A')}")
            print(f"      Question Type: {answer.get('question_type', 'N/A')}")
            print(f"      Has Answer.approaches: {'approaches' in answer.get('answer', {})}")
            print()
            count += 1
    
    # Show field structure of a sample entry
    if answers_data:
        print("🔍 Sample field structure (sorted alphabetically):")
        sample_fields = list(answers_data[0].keys())
        print(f"   Top-level fields: {', '.join(sample_fields)}")
        
        # Check answer structure
        if 'answer' in answers_data[0] and isinstance(answers_data[0]['answer'], dict):
            answer_fields = list(answers_data[0]['answer'].keys())
            print(f"   Answer fields: {', '.join(answer_fields)}")
    
    return True

if __name__ == "__main__":
    success = final_cleanup_and_restructure()
    if success:
        print("\n✅ Final cleanup completed successfully!")
    else:
        print("\n❌ Final cleanup failed!")