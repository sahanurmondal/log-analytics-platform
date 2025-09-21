#!/usr/bin/env python3
"""
Add custom_solution field to enginebogie_answer.json for user/copilot solution updates
"""

import json
import os
from datetime import datetime

def add_custom_solution_field_to_enginebogie():
    """Add custom_solution field to each problem in the enginebogie_answer.json"""
    
    # File paths
    input_file = "enginebogie_answer.json"
    backup_file = f"enginebogie_answer_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    
    if not os.path.exists(input_file):
        print(f"❌ Input file {input_file} not found!")
        return False
    
    print(f"📁 Reading {input_file}...")
    try:
        with open(input_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
    except Exception as e:
        print(f"❌ Error reading file: {e}")
        return False
    
    # Create backup
    print(f"💾 Creating backup: {backup_file}")
    try:
        with open(backup_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
    except Exception as e:
        print(f"❌ Error creating backup: {e}")
        return False
    
    # Add custom_solution field to each problem
    print("🔧 Adding custom_solution field...")
    enhanced_count = 0
    
    for problem in data:
        if 'custom_solution' not in problem:
            problem['custom_solution'] = {
                "status": "pending",  # pending, in_progress, completed
                "solution_code": "",
                "solution_language": "python",  # python, java, cpp, javascript, etc.
                "explanation": "",
                "time_complexity": "",
                "space_complexity": "",
                "notes": "",
                "last_updated": "",
                "updated_by": "",  # user or copilot
                "is_system_design": problem.get("title", "").lower().find("design") != -1 or 
                                  problem.get("title", "").lower().find("system") != -1,
                "interview_type": "coding"  # coding, system_design, behavioral, theoretical
            }
            enhanced_count += 1
    
    print(f"✅ Enhanced {enhanced_count} problems with custom_solution field")
    
    # Write enhanced data back
    print(f"💾 Writing enhanced data to {input_file}...")
    try:
        with open(input_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
    except Exception as e:
        print(f"❌ Error writing enhanced file: {e}")
        return False
    
    print("🎉 Successfully added custom_solution field to all problems!")
    print(f"📊 Total problems processed: {len(data)}")
    print(f"📊 Problems enhanced: {enhanced_count}")
    print(f"💾 Backup saved as: {backup_file}")
    
    return True

if __name__ == "__main__":
    print("🚀 Adding custom_solution field to enginebogie_answer.json")
    print("=" * 60)
    
    success = add_custom_solution_field_to_enginebogie()
    
    if success:
        print("\n✅ Enhancement completed successfully!")
    else:
        print("\n❌ Enhancement failed!")