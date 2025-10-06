#!/usr/bin/env python3
"""
Progress Monitor for Multi-AI Solution Generator
"""

import json
import time
import os
from datetime import datetime

def check_progress():
    """Check the current progress of solution generation."""
    try:
        with open('src/company/webscraper/enginebogie/enginebogie_answer.json', 'r') as f:
            data = json.load(f)
        
        # Count problems with AI-generated solutions
        ai_generated = 0
        total_dsa = 0
        
        for problem in data:
            if problem.get('category', '').upper() == 'DSA':
                total_dsa += 1
                if problem.get('ai_provider_used'):
                    ai_generated += 1
        
        print(f"📊 DSA Progress: {ai_generated}/{total_dsa} problems processed")
        
        if ai_generated > 0:
            percentage = (ai_generated / total_dsa) * 100
            estimated_cost = ai_generated * 0.0004
            print(f"✅ {percentage:.1f}% complete")
            print(f"💰 Estimated cost so far: ${estimated_cost:.4f}")
            
            if ai_generated < total_dsa:
                remaining = total_dsa - ai_generated
                eta_minutes = remaining * 0.1  # roughly 6 seconds per problem
                print(f"⏱️  ETA: ~{eta_minutes:.1f} minutes remaining")
        
        return ai_generated, total_dsa
        
    except Exception as e:
        print(f"❌ Error checking progress: {e}")
        return 0, 0

if __name__ == "__main__":
    print("🚀 Multi-AI Progress Monitor Started")
    print("=" * 50)
    
    try:
        while True:
            current_time = datetime.now().strftime("%H:%M:%S")
            print(f"\n[{current_time}] Checking progress...")
            
            completed, total = check_progress()
            
            if completed >= total and total > 0:
                print("\n🎉 ALL DSA PROBLEMS COMPLETED! 🎉")
                final_cost = completed * 0.0004
                print(f"📈 Final Stats: {completed}/{total} problems")
                print(f"💰 Total Cost: ${final_cost:.4f}")
                break
            
            print("⏳ Waiting 60 seconds for next check...")
            time.sleep(60)
            
    except KeyboardInterrupt:
        print("\n⚠️ Monitoring stopped by user")
        final_completed, final_total = check_progress()
        print(f"📊 Current progress: {final_completed}/{final_total}")