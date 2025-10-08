#!/usr/bin/env python3
"""
Merge Batch Results Back to Main File

Merges all processed batch files back into the main enginebogie_answer.json file.
"""

import json
import os
from datetime import datetime

def merge_batches():
    """Merge all batch results back into main file."""
    
    # Get script directory to handle relative paths correctly
    script_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Files to merge (use absolute paths)
    source_file = os.path.join(script_dir, "src/company/webscraper/enginebogie/enginebogie_answer.json")
    batch_files = [
        os.path.join(script_dir, "test_gemini_10.json"),
        os.path.join(script_dir, "batch_1_gemini.json"),
        os.path.join(script_dir, "batch_2_gemini.json"),
        os.path.join(script_dir, "batch_3_gemini.json"),
        os.path.join(script_dir, "batch_4_gemini.json"),
        os.path.join(script_dir, "batch_5_gemini.json")
    ]
    
    print("🔄 Merging Batch Results")
    print("=" * 60)
    
    # Load main file
    print(f"\n📖 Loading main file: {source_file}")
    with open(source_file, 'r', encoding='utf-8') as f:
        main_data = json.load(f)
    
    print(f"   Total problems in main file: {len(main_data)}")
    
    # Create mapping of main data by ID or description
    main_map = {}
    for i, problem in enumerate(main_data):
        key = problem.get('id') or problem.get('description', '')[:100]
        main_map[key] = i
    
    # Process each batch
    total_updated = 0
    for batch_file in batch_files:
        if not os.path.exists(batch_file):
            print(f"\n⚠️  Skipping {batch_file} (not found)")
            continue
        
        print(f"\n📦 Processing {batch_file}")
        with open(batch_file, 'r', encoding='utf-8') as f:
            batch_data = json.load(f)
        
        updated = 0
        for problem in batch_data:
            if problem.get('answer', '').strip():
                # Find matching problem in main data
                key = problem.get('id') or problem.get('description', '')[:100]
                
                if key in main_map:
                    main_index = main_map[key]
                    # Update answer if batch has one and main doesn't
                    if not main_data[main_index].get('answer', '').strip():
                        main_data[main_index]['answer'] = problem['answer']
                        updated += 1
        
        print(f"   ✅ Updated {updated} problems from this batch")
        total_updated += updated
    
    # Create backup
    backup_dir = os.path.join(script_dir, "src/company/webscraper/enginebogie")
    backup_file = os.path.join(backup_dir, f"enginebogie_answer_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json")
    print(f"\n💾 Creating backup: {os.path.basename(backup_file)}")
    with open(backup_file, 'w', encoding='utf-8') as f:
        json.dump(json.load(open(source_file, 'r', encoding='utf-8')), f, indent=2, ensure_ascii=False)
    
    # Save updated main file
    print(f"\n💾 Saving updated main file...")
    with open(source_file, 'w', encoding='utf-8') as f:
        json.dump(main_data, f, indent=2, ensure_ascii=False)
    
    # Statistics
    print(f"\n" + "=" * 60)
    print(f"📊 MERGE COMPLETE")
    print(f"=" * 60)
    print(f"Total problems updated: {total_updated}")
    
    # Count remaining empty
    empty_count = sum(1 for p in main_data if not p.get('answer', '').strip() and p.get('category', '').upper() == 'DSA')
    print(f"Remaining problems without answers: {empty_count}")
    
    if empty_count == 0:
        print(f"\n🎉 SUCCESS! All DSA problems now have answers!")
    else:
        print(f"\n⚠️  Note: {empty_count} problems still need answers")
    
    print(f"\nBackup saved to: {backup_file}")
    print(f"Main file updated: {source_file}")

def verify_merge():
    """Verify the merge was successful."""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    source_file = os.path.join(script_dir, "src/company/webscraper/enginebogie/enginebogie_answer.json")
    
    print(f"\n🔍 Verification Report")
    print("=" * 60)
    
    with open(source_file, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    dsa_problems = [p for p in data if p.get('category', '').upper() == 'DSA']
    dsa_with_answers = [p for p in dsa_problems if p.get('answer', '').strip()]
    dsa_without_answers = [p for p in dsa_problems if not p.get('answer', '').strip()]
    
    print(f"\nTotal DSA problems: {len(dsa_problems)}")
    print(f"With answers: {len(dsa_with_answers)} ({len(dsa_with_answers)*100/len(dsa_problems):.1f}%)")
    print(f"Without answers: {len(dsa_without_answers)} ({len(dsa_without_answers)*100/len(dsa_problems):.1f}%)")
    
    # Sample check
    if dsa_with_answers:
        print(f"\n✅ Sample answer length: {len(dsa_with_answers[0].get('answer', '')):.0f} characters")
    
    return len(dsa_without_answers) == 0

if __name__ == "__main__":
    try:
        merge_batches()
        
        # Verify
        success = verify_merge()
        
        if success:
            print(f"\n" + "=" * 60)
            print(f"✅ All done! Ready for dashboard deployment.")
            print(f"=" * 60)
        
    except Exception as e:
        print(f"\n❌ Error during merge: {e}")
        import traceback
        traceback.print_exc()
