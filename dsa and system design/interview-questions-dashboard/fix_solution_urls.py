#!/usr/bin/env python3
"""
Solution URL Fixer Script

This script fixes the solution_url fields in the company-wise LeetCode files 
by mapping them correctly using title_slug from leetcode_ca_search_enhanced.json

Files to fix:
1. leetcode_enhanced_compact.json
2. leetcode_enhanced_complete.json  
3. leetcode_problems_only.json

Source for correct mapping:
- leetcode_ca_search_enhanced.json
"""

import json
import os
import time
from datetime import datetime

def load_solution_url_mapping(enhanced_file_path):
    """Load the correct title_slug and problem_no to solution_url mapping"""
    print("📁 Loading solution URL mapping from enhanced file...")
    
    with open(enhanced_file_path, 'r', encoding='utf-8') as f:
        enhanced_data = json.load(f)
    
    # Build mapping dictionaries
    slug_to_solution_url = {}
    problem_no_to_solution_url = {}
    
    for entry in enhanced_data:
        title_slug = entry.get('title_slug', '')
        solution_url = entry.get('solution_url', '')
        leetcode_problem_no = entry.get('leetcode_problem_no', '')
        
        # Map by title_slug
        if title_slug and solution_url:
            slug_to_solution_url[title_slug] = solution_url
        
        # Map by leetcode_problem_no (as backup)
        if leetcode_problem_no and solution_url:
            problem_no_to_solution_url[str(leetcode_problem_no)] = solution_url
    
    print(f"✅ Built mapping for {len(slug_to_solution_url)} title_slug entries")
    print(f"✅ Built mapping for {len(problem_no_to_solution_url)} problem_no entries")
    
    return slug_to_solution_url, problem_no_to_solution_url

def fix_compact_file(file_path, slug_mapping, problem_no_mapping):
    """Fix leetcode_enhanced_compact.json"""
    print(f"\n🔧 Fixing {os.path.basename(file_path)}...")
    
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    fixed_count = 0
    fallback_count = 0
    
    # Fix problems array
    if 'problems' in data:
        for problem in data['problems']:
            title_slug = problem.get('title_slug', '')
            leetcode_problem_no = problem.get('leetcode_problem_no', '')
            old_url = problem.get('solution_url', '')
            new_url = None
            
            # Try title_slug first
            if title_slug in slug_mapping:
                new_url = slug_mapping[title_slug]
            # Fallback to problem number mapping
            elif leetcode_problem_no and str(leetcode_problem_no) in problem_no_mapping:
                new_url = problem_no_mapping[str(leetcode_problem_no)]
                fallback_count += 1
            
            if new_url and old_url != new_url:
                problem['solution_url'] = new_url
                fixed_count += 1
    
    # Update metadata
    if 'metadata' in data:
        data['metadata']['last_solution_url_fix'] = datetime.now().isoformat()
        data['metadata']['solution_url_fixes'] = fixed_count
        data['metadata']['fallback_fixes'] = fallback_count
    
    # Create backup
    backup_path = f"{file_path}.backup_{int(time.time())}"
    import shutil
    shutil.copy2(file_path, backup_path)
    
    # Save fixed data
    with open(file_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print(f"✅ Fixed {fixed_count} solution URLs in {os.path.basename(file_path)}")
    if fallback_count > 0:
        print(f"📝 Used problem_no fallback for {fallback_count} entries")
    print(f"📁 Backup created: {os.path.basename(backup_path)}")
    
    return fixed_count

def fix_complete_file(file_path, slug_mapping, problem_no_mapping):
    """Fix leetcode_enhanced_complete.json"""
    print(f"\n🔧 Fixing {os.path.basename(file_path)}...")
    
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    fixed_count = 0
    fallback_count = 0
    
    # Fix problems array
    if 'problems' in data:
        for problem in data['problems']:
            title_slug = problem.get('title_slug', '')
            leetcode_problem_no = problem.get('leetcode_problem_no', '')
            old_url = problem.get('solution_url', '')
            new_url = None
            
            # Try title_slug first
            if title_slug in slug_mapping:
                new_url = slug_mapping[title_slug]
            # Fallback to problem number mapping
            elif leetcode_problem_no and str(leetcode_problem_no) in problem_no_mapping:
                new_url = problem_no_mapping[str(leetcode_problem_no)]
                fallback_count += 1
            
            if new_url and old_url != new_url:
                problem['solution_url'] = new_url
                fixed_count += 1
    
    # Update metadata
    if 'metadata' in data:
        data['metadata']['last_solution_url_fix'] = datetime.now().isoformat()
        data['metadata']['solution_url_fixes'] = fixed_count
        data['metadata']['fallback_fixes'] = fallback_count
    
    # Create backup
    backup_path = f"{file_path}.backup_{int(time.time())}"
    import shutil
    shutil.copy2(file_path, backup_path)
    
    # Save fixed data
    with open(file_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print(f"✅ Fixed {fixed_count} solution URLs in {os.path.basename(file_path)}")
    if fallback_count > 0:
        print(f"📝 Used problem_no fallback for {fallback_count} entries")
    print(f"📁 Backup created: {os.path.basename(backup_path)}")
    
    return fixed_count

def fix_problems_only_file(file_path, slug_mapping, problem_no_mapping):
    """Fix leetcode_problems_only.json"""
    print(f"\n🔧 Fixing {os.path.basename(file_path)}...")
    
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    fixed_count = 0
    fallback_count = 0
    
    # This is an array of problems directly
    if isinstance(data, list):
        for problem in data:
            title_slug = problem.get('title_slug', '')
            leetcode_problem_no = problem.get('leetcode_problem_no', '')
            old_url = problem.get('solution_url', '')
            new_url = None
            
            # Try title_slug first
            if title_slug in slug_mapping:
                new_url = slug_mapping[title_slug]
            # Fallback to problem number mapping
            elif leetcode_problem_no and str(leetcode_problem_no) in problem_no_mapping:
                new_url = problem_no_mapping[str(leetcode_problem_no)]
                fallback_count += 1
            
            if new_url and old_url != new_url:
                problem['solution_url'] = new_url
                fixed_count += 1
    
    # Create backup
    backup_path = f"{file_path}.backup_{int(time.time())}"
    import shutil
    shutil.copy2(file_path, backup_path)
    
    # Save fixed data
    with open(file_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print(f"✅ Fixed {fixed_count} solution URLs in {os.path.basename(file_path)}")
    if fallback_count > 0:
        print(f"📝 Used problem_no fallback for {fallback_count} entries")
    print(f"📁 Backup created: {os.path.basename(backup_path)}")
    
    return fixed_count

def main():
    """Main execution"""
    print("🔧 SOLUTION URL FIXER")
    print("=" * 40)
    
    # File paths
    script_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Source file with correct mappings
    enhanced_file_path = os.path.join(script_dir, 
        "src/company/webscraper/leetcode_problem_solution/leetcode_ca_search_enhanced.json")
    
    # Target files to fix
    target_files = [
        os.path.join(script_dir, "src/company/webscraper/leetcode_company_wise/leetcode_enhanced_compact.json"),
        os.path.join(script_dir, "src/company/webscraper/leetcode_company_wise/leetcode_enhanced_complete.json"),
        os.path.join(script_dir, "src/company/webscraper/leetcode_company_wise/leetcode_problems_only.json")
    ]
    
    # Verify files exist
    if not os.path.exists(enhanced_file_path):
        print(f"❌ Source file not found: {enhanced_file_path}")
        return
    
    for file_path in target_files:
        if not os.path.exists(file_path):
            print(f"❌ Target file not found: {file_path}")
            return
    
    print(f"📂 Source: {os.path.basename(enhanced_file_path)}")
    print(f"📂 Target files: {len(target_files)}")
    
    start_time = time.time()
    
    try:
        # Load correct mappings
        slug_mapping, problem_no_mapping = load_solution_url_mapping(enhanced_file_path)
        
        total_fixes = 0
        
        # Fix each target file
        for file_path in target_files:
            if 'compact' in file_path:
                fixes = fix_compact_file(file_path, slug_mapping, problem_no_mapping)
            elif 'complete' in file_path:
                fixes = fix_complete_file(file_path, slug_mapping, problem_no_mapping)
            elif 'problems_only' in file_path:
                fixes = fix_problems_only_file(file_path, slug_mapping, problem_no_mapping)
            else:
                print(f"⚠️ Unknown file type: {file_path}")
                continue
            
            total_fixes += fixes
        
        elapsed = time.time() - start_time
        
        print(f"\n🎉 SOLUTION URL FIXING COMPLETE!")
        print(f"⏱️ Total time: {elapsed:.2f} seconds")
        print(f"🔧 Total fixes applied: {total_fixes}")
        print(f"📁 All files backed up before modification")
        
        # Verification sample
        print(f"\n🔍 VERIFICATION SAMPLE:")
        print("Checking a few fixed entries...")
        
        # Quick verification
        for file_path in target_files[:1]:  # Just check first file
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
            
            problems = data.get('problems', data) if isinstance(data, dict) else data
            if isinstance(problems, list):
                for i, problem in enumerate(problems[:3]):
                    title_slug = problem.get('title_slug', '')
                    solution_url = problem.get('solution_url', '')
                    if title_slug and solution_url:
                        print(f"  {i+1}. {title_slug} → {solution_url}")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()