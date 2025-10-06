#!/usr/bin/env python3
"""
Gemini AI Solution Generator with Batching

Tests with 10 answers first, then processes remaining empty answers in batches of 180.
"""

import os
import json
import sys
from datetime import datetime

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

def check_api_keys():
    """Check if Gemini API key is available."""
    providers = {}
    
    # Check Gemini only
    if os.getenv('GEMINI_API_KEY'):
        try:
            import google.generativeai as genai
            genai.configure(api_key=os.getenv('GEMINI_API_KEY'))
            providers['gemini'] = {
                'available': True,
                'cost_per_1k': 0.0005,
                'name': 'Google Gemini Pro',
                'daily_limit': 1500  # Conservative limit
            }
        except ImportError:
            providers['gemini'] = {
                'available': False,
                'error': 'Package not installed (pip install google-generativeai)'
            }
    
    return providers

def estimate_costs(providers, num_problems=1088):
    """Estimate costs for Gemini."""
    estimates = {}
    avg_tokens_per_problem = 2000  # Rough estimate
    
    if 'gemini' in providers and providers['gemini'].get('available'):
        cost = (num_problems * avg_tokens_per_problem / 1000) * providers['gemini']['cost_per_1k']
        estimates['gemini'] = cost
    
    return estimates

def calculate_batches(total_problems, batch_size=180, test_size=10):
    """Calculate batch distribution."""
    remaining = total_problems - test_size
    num_batches = (remaining + batch_size - 1) // batch_size  # Ceiling division
    
    return {
        'test_size': test_size,
        'batch_size': batch_size,
        'num_batches': num_batches,
        'total_problems': total_problems,
        'problems_per_batch': [min(batch_size, remaining - i * batch_size) for i in range(num_batches)]
    }

def recommend_strategy(providers):
    """Recommend Gemini batch strategy."""
    if 'gemini' not in providers or not providers['gemini'].get('available'):
        return "Gemini API key not configured! Please set GEMINI_API_KEY"
    
    return {
        'name': 'Gemini Batch Processing',
        'provider': 'gemini',
        'description': 'Test with 10 answers, then process 180 per batch',
        'steps': [
            '1. Test run: 10 problems',
            '2. Batch processing: 180 problems per run',
            '3. Only processes empty answers',
            '4. Saves progress after each batch'
        ]
    }

def create_test_dataset(size=10):
    """Create test dataset with specified number of problems without answers."""
    try:
        source_file = "src/company/webscraper/enginebogie/enginebogie_answer.json"
        with open(source_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Find problems without answers
        test_problems = []
        for problem in data:
            if (problem.get('category', '').upper() == 'DSA' and 
                not problem.get('answer', '').strip() and
                problem.get('description', '').strip() and
                len(problem.get('description', '')) > 50):
                
                test_problems.append(problem)
                if len(test_problems) >= size:
                    break
        
        if test_problems:
            output_file = f'test_gemini_{size}.json'
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(test_problems, f, indent=2, ensure_ascii=False)
            
            return True, len(test_problems), output_file
        
        return False, 0, None
        
    except Exception as e:
        return False, str(e), None

def create_batch_datasets(batch_size=180):
    """Create batch datasets for processing remaining empty answers."""
    try:
        source_file = "src/company/webscraper/enginebogie/enginebogie_answer.json"
        with open(source_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Find all problems without answers
        empty_problems = []
        for problem in data:
            if (problem.get('category', '').upper() == 'DSA' and 
                not problem.get('answer', '').strip() and
                problem.get('description', '').strip()):
                empty_problems.append(problem)
        
        total_empty = len(empty_problems)
        
        # Skip first 10 (for testing)
        remaining = empty_problems[10:]
        
        # Create batches
        batches = []
        for i in range(0, len(remaining), batch_size):
            batch = remaining[i:i+batch_size]
            batch_num = (i // batch_size) + 1
            output_file = f'batch_{batch_num}_gemini.json'
            
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(batch, f, indent=2, ensure_ascii=False)
            
            batches.append({
                'file': output_file,
                'size': len(batch),
                'start_index': i + 10,  # +10 for test problems
                'end_index': i + 10 + len(batch)
            })
        
        return True, total_empty, batches
        
    except Exception as e:
        return False, str(e), []

def print_setup_report():
    """Print comprehensive setup report."""
    print("🚀 GEMINI AI SOLUTION GENERATOR - BATCH PROCESSING")
    print("=" * 70)
    print(f"Date: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # Check Gemini
    providers = check_api_keys()
    print(f"\n🔑 GEMINI API STATUS:")
    print("-" * 35)
    
    if 'gemini' not in providers:
        print("❌ Gemini API key not configured!")
        print("\nPlease set your API key:")
        print("export GEMINI_API_KEY='your-key'")
        print("\nOr add to .env file:")
        print("GEMINI_API_KEY=your-key")
        return
    
    gemini_config = providers['gemini']
    if gemini_config.get('available'):
        print(f"✅ {gemini_config['name']}: Ready")
        print(f"   Cost: ${gemini_config['cost_per_1k']:.4f} per 1K tokens")
        print(f"   Daily Limit: ~{gemini_config['daily_limit']} requests")
    else:
        print(f"❌ GEMINI: {gemini_config.get('error', 'Not available')}")
        return
    
    # Cost estimates
    estimates = estimate_costs(providers)
    if estimates:
        print(f"\n💰 COST ESTIMATES:")
        print("-" * 35)
        print(f"Full dataset (1,088 problems): ${estimates['gemini']:.2f}")
        print(f"Test run (10 problems): ${estimates['gemini'] * 10 / 1088:.2f}")
        print(f"Per batch (180 problems): ${estimates['gemini'] * 180 / 1088:.2f}")
    
    # Strategy
    strategy = recommend_strategy(providers)
    if isinstance(strategy, str):
        print(f"\n⚠️ {strategy}")
        return
    
    print(f"\n🎯 PROCESSING STRATEGY:")
    print("-" * 35)
    print(f"{strategy['name']}")
    print(f"{strategy['description']}")
    print(f"\nSteps:")
    for step in strategy['steps']:
        print(f"  {step}")
    
    # Create test dataset
    print(f"\n🧪 TEST DATASET CREATION:")
    print("-" * 35)
    
    success, count, filename = create_test_dataset(10)
    if success:
        print(f"✅ Created: {filename} ({count} problems)")
        print(f"\nTest command:")
        print(f"python simple_multi_ai.py -i {filename} -c DSA -p gemini")
    else:
        print(f"❌ Failed to create test dataset: {count}")
        return
    
    # Create batch datasets
    print(f"\n📦 BATCH DATASETS CREATION:")
    print("-" * 35)
    
    success, total, batches = create_batch_datasets(180)
    if success:
        print(f"✅ Total problems without answers: {total}")
        print(f"✅ Created {len(batches)} batch files:")
        print()
        for i, batch in enumerate(batches, 1):
            print(f"  Batch {i}: {batch['file']}")
            print(f"    Problems: {batch['size']} (indices {batch['start_index']}-{batch['end_index']})")
            print(f"    Command: python simple_multi_ai.py -i {batch['file']} -c DSA -p gemini")
            print()
    else:
        print(f"❌ Failed to create batches: {total}")
    
    # Next steps
    print(f"\n🚀 RECOMMENDED WORKFLOW:")
    print("-" * 35)
    print("1. Run test with 10 problems")
    print("2. Review generated solutions")
    print("3. If quality is good, run batch 1")
    print("4. Continue with remaining batches")
    print("5. Monitor API quota (1500/day limit)")
    
    # Batch timing
    print(f"\n⏱️  TIMING ESTIMATES:")
    print("-" * 35)
    print("Test (10 problems): ~2-3 minutes")
    print("Batch (180 problems): ~30-40 minutes")
    print(f"Total batches needed: {len(batches)}")
    print(f"Total estimated time: ~{len(batches) * 35} minutes")
    
    # Dependencies check
    print(f"\n📦 DEPENDENCIES:")
    print("-" * 35)
    
    try:
        import google.generativeai
        print("✅ google-generativeai: Installed")
    except ImportError:
        print("❌ google-generativeai: Missing")
        print("   Install: pip install google-generativeai")
    
    try:
        import requests
        print("✅ requests: Installed")
    except ImportError:
        print("❌ requests: Missing")
        print("   Install: pip install requests")

def main():
    # Load environment variables from .env file if it exists
    load_env_file()
    
    print_setup_report()
    
    # Interactive setup assistance
    try:
        print(f"\n" + "=" * 70)
        response = input(f"\nWould you like to install missing dependencies? (y/n): ").lower().strip()
        if response == 'y':
            import subprocess
            
            packages = ['google-generativeai', 'requests']
            
            print(f"\nInstalling packages: {', '.join(packages)}")
            subprocess.run([sys.executable, "-m", "pip", "install"] + packages)
            
            print(f"\n✅ Installation complete!")
            print("Please set your GEMINI_API_KEY and run this script again.")
        
        print(f"\n" + "=" * 70)
        print(f"\n💡 QUICK START:")
        print("-" * 35)
        print("1. Set API key: export GEMINI_API_KEY='your-key'")
        print("2. Run test: python simple_multi_ai.py -i test_gemini_10.json -c DSA -p gemini")
        print("3. Check results in updated JSON file")
        print("4. If good, run: python simple_multi_ai.py -i batch_1_gemini.json -c DSA -p gemini")
        print(f"\n" + "=" * 70)
            
    except KeyboardInterrupt:
        print(f"\n\n👋 Setup complete! Happy coding!")

if __name__ == "__main__":
    main()