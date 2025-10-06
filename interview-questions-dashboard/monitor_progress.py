#!/usr/bin/env python3
"""
Real-time Batch Progress Monitor
Monitors the batch processing in real-time
"""

import json
import time
import os
from datetime import datetime

def monitor_batch(batch_file, interval=10):
    """Monitor batch processing progress."""
    
    print("🔍 Batch Progress Monitor")
    print("=" * 70)
    print(f"Monitoring: {batch_file}")
    print(f"Update interval: {interval} seconds")
    print(f"Started: {datetime.now().strftime('%H:%M:%S')}")
    print("=" * 70)
    print()
    
    start_time = time.time()
    last_count = 0
    
    try:
        while True:
            if not os.path.exists(batch_file):
                print(f"⏳ Waiting for {batch_file} to be created...")
                time.sleep(interval)
                continue
            
            with open(batch_file, 'r', encoding='utf-8') as f:
                data = json.load(f)
            
            total = len(data)
            with_answers = sum(1 for p in data if p.get('answer', '').strip())
            remaining = total - with_answers
            progress_pct = (with_answers / total * 100) if total > 0 else 0
            
            # Calculate rate
            elapsed = time.time() - start_time
            if elapsed > 0:
                problems_per_min = (with_answers / elapsed) * 60
                eta_seconds = (remaining / problems_per_min * 60) if problems_per_min > 0 else 0
                eta_minutes = eta_seconds / 60
            else:
                problems_per_min = 0
                eta_minutes = 0
            
            # Calculate incremental progress
            increment = with_answers - last_count
            last_count = with_answers
            
            # Clear screen and display progress
            os.system('clear' if os.name == 'posix' else 'cls')
            
            print("🚀 BATCH PROCESSING PROGRESS")
            print("=" * 70)
            print(f"File: {batch_file}")
            print(f"Time: {datetime.now().strftime('%H:%M:%S')}")
            print(f"Elapsed: {elapsed/60:.1f} minutes")
            print("=" * 70)
            print()
            
            # Progress bar
            bar_length = 50
            filled = int(bar_length * progress_pct / 100)
            bar = '█' * filled + '░' * (bar_length - filled)
            print(f"Progress: [{bar}] {progress_pct:.1f}%")
            print()
            
            # Statistics
            print(f"📊 Statistics:")
            print(f"   Completed:  {with_answers}/{total} problems")
            print(f"   Remaining:  {remaining} problems")
            print(f"   Rate:       {problems_per_min:.1f} problems/minute")
            if increment > 0:
                print(f"   Recent:     +{increment} in last {interval}s")
            print()
            
            # ETA
            if remaining > 0 and problems_per_min > 0:
                print(f"⏱️  Estimated Time Remaining: {eta_minutes:.0f} minutes")
            else:
                print(f"⏱️  Calculating ETA...")
            print()
            
            # Completion check
            if with_answers >= total:
                print("=" * 70)
                print("✅ BATCH COMPLETE!")
                print(f"Total time: {elapsed/60:.1f} minutes")
                print(f"Average: {elapsed/total:.1f} seconds per problem")
                print("=" * 70)
                break
            
            time.sleep(interval)
            
    except KeyboardInterrupt:
        print("\n\n⚠️  Monitor stopped by user")
        print(f"Progress at stop: {with_answers}/{total} ({progress_pct:.1f}%)")
    except Exception as e:
        print(f"\n❌ Error: {e}")

if __name__ == "__main__":
    import sys
    
    if len(sys.argv) > 1:
        batch_file = sys.argv[1]
    else:
        batch_file = "batch_1_gemini.json"
    
    interval = int(sys.argv[2]) if len(sys.argv) > 2 else 10
    
    monitor_batch(batch_file, interval)
