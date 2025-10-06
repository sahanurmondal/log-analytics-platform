#!/usr/bin/env python3
"""
Solution Validator and Data Safety Utilities

This script provides validation, backup, and safety features for the solution generator.
"""

import json
import os
import shutil
import hashlib
from datetime import datetime
from typing import Dict, List, Tuple
import logging

logger = logging.getLogger(__name__)

class SolutionValidator:
    """Validates generated solutions and ensures data integrity."""
    
    def __init__(self):
        self.validation_errors = []
        self.warnings = []
    
    def validate_solution_format(self, solution: str) -> Tuple[bool, List[str]]:
        """Validate that a solution has proper format."""
        errors = []
        warnings = []
        
        if not solution or not solution.strip():
            errors.append("Solution is empty")
            return False, errors
        
        # Check for Java solution format
        if self._is_java_solution(solution):
            java_errors, java_warnings = self._validate_java_solution(solution)
            errors.extend(java_errors)
            warnings.extend(java_warnings)
        
        # Check for Python solution format
        elif self._is_python_solution(solution):
            python_errors, python_warnings = self._validate_python_solution(solution)
            errors.extend(python_errors)
            warnings.extend(python_warnings)
        
        # General validation
        if len(solution) < 50:
            warnings.append("Solution seems very short")
        
        if "TODO" in solution or "FIXME" in solution:
            warnings.append("Solution contains TODO/FIXME comments")
        
        self.warnings.extend(warnings)
        return len(errors) == 0, errors
    
    def _is_java_solution(self, solution: str) -> bool:
        """Check if solution appears to be Java code."""
        java_indicators = [
            'public class', 'private class', 'class ',
            'import java.', 'public static void main',
            'System.out.println', 'public ', 'private '
        ]
        return any(indicator in solution for indicator in java_indicators)
    
    def _is_python_solution(self, solution: str) -> bool:
        """Check if solution appears to be Python code."""
        python_indicators = [
            'def ', 'import ', 'from ', 'class ',
            'if __name__ == "__main__":', 'print(',
            'return ', 'elif ', 'except:'
        ]
        return any(indicator in solution for indicator in python_indicators)
    
    def _validate_java_solution(self, solution: str) -> Tuple[List[str], List[str]]:
        """Validate Java-specific aspects."""
        errors = []
        warnings = []
        
        # Check for basic Java structure
        if 'public class' not in solution and 'class ' not in solution:
            errors.append("Java solution missing class declaration")
        
        # Check for main method
        if 'public static void main' not in solution:
            warnings.append("Java solution missing main method for testing")
        
        # Check for imports (if using collections, etc.)
        if ('HashMap' in solution or 'ArrayList' in solution or 
            'TreeMap' in solution) and 'import java.util' not in solution:
            warnings.append("Java solution might be missing import statements")
        
        # Check for proper brackets
        open_braces = solution.count('{')
        close_braces = solution.count('}')
        if open_braces != close_braces:
            errors.append(f"Mismatched braces: {open_braces} opening, {close_braces} closing")
        
        return errors, warnings
    
    def _validate_python_solution(self, solution: str) -> Tuple[List[str], List[str]]:
        """Validate Python-specific aspects."""
        errors = []
        warnings = []
        
        # Check for basic Python structure
        if 'def ' not in solution and 'class ' not in solution:
            warnings.append("Python solution missing function or class definition")
        
        # Check for proper indentation (basic check)
        lines = solution.split('\n')
        has_indentation = any(line.startswith('    ') or line.startswith('\t') for line in lines)
        if not has_indentation and len(lines) > 3:
            warnings.append("Python solution might have indentation issues")
        
        return errors, warnings

class DataSafetyManager:
    """Manages backups and data integrity for the solution generator."""
    
    def __init__(self, backup_dir: str = "./backups"):
        self.backup_dir = backup_dir
        os.makedirs(backup_dir, exist_ok=True)
    
    def create_backup(self, data: List[Dict], description: str = "") -> str:
        """Create a timestamped backup of the data."""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        if description:
            filename = f"backup_{timestamp}_{description}.json"
        else:
            filename = f"backup_{timestamp}.json"
        
        backup_path = os.path.join(self.backup_dir, filename)
        
        try:
            with open(backup_path, 'w', encoding='utf-8') as f:
                json.dump(data, f, indent=2, ensure_ascii=False)
            
            # Create metadata file
            metadata = {
                "timestamp": timestamp,
                "description": description,
                "total_problems": len(data),
                "checksum": self._calculate_checksum(backup_path)
            }
            
            metadata_path = backup_path.replace('.json', '_metadata.json')
            with open(metadata_path, 'w', encoding='utf-8') as f:
                json.dump(metadata, f, indent=2)
            
            logger.info(f"Backup created: {backup_path}")
            return backup_path
            
        except Exception as e:
            logger.error(f"Failed to create backup: {str(e)}")
            raise
    
    def verify_backup(self, backup_path: str) -> bool:
        """Verify backup integrity using checksum."""
        try:
            metadata_path = backup_path.replace('.json', '_metadata.json')
            if not os.path.exists(metadata_path):
                logger.warning(f"Metadata file not found for {backup_path}")
                return False
            
            with open(metadata_path, 'r', encoding='utf-8') as f:
                metadata = json.load(f)
            
            current_checksum = self._calculate_checksum(backup_path)
            stored_checksum = metadata.get('checksum', '')
            
            if current_checksum == stored_checksum:
                logger.info(f"Backup verification successful: {backup_path}")
                return True
            else:
                logger.error(f"Backup verification failed: checksum mismatch")
                return False
                
        except Exception as e:
            logger.error(f"Error verifying backup: {str(e)}")
            return False
    
    def list_backups(self) -> List[Dict]:
        """List all available backups with metadata."""
        backups = []
        
        for filename in os.listdir(self.backup_dir):
            if filename.endswith('_metadata.json'):
                metadata_path = os.path.join(self.backup_dir, filename)
                backup_path = metadata_path.replace('_metadata.json', '.json')
                
                if os.path.exists(backup_path):
                    try:
                        with open(metadata_path, 'r', encoding='utf-8') as f:
                            metadata = json.load(f)
                        
                        metadata['backup_path'] = backup_path
                        metadata['size_mb'] = os.path.getsize(backup_path) / (1024 * 1024)
                        backups.append(metadata)
                        
                    except Exception as e:
                        logger.warning(f"Error reading metadata for {filename}: {str(e)}")
        
        return sorted(backups, key=lambda x: x['timestamp'], reverse=True)
    
    def restore_backup(self, backup_path: str, target_path: str) -> bool:
        """Restore data from a backup file."""
        try:
            if not self.verify_backup(backup_path):
                logger.error("Backup verification failed, aborting restore")
                return False
            
            # Create backup of current file before restore
            if os.path.exists(target_path):
                current_backup = self.create_backup(
                    self._load_json(target_path), 
                    "before_restore"
                )
                logger.info(f"Current data backed up to: {current_backup}")
            
            # Copy backup to target location
            shutil.copy2(backup_path, target_path)
            logger.info(f"Data restored from {backup_path} to {target_path}")
            return True
            
        except Exception as e:
            logger.error(f"Error restoring backup: {str(e)}")
            return False
    
    def _calculate_checksum(self, file_path: str) -> str:
        """Calculate MD5 checksum of a file."""
        hash_md5 = hashlib.md5()
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                hash_md5.update(chunk)
        return hash_md5.hexdigest()
    
    def _load_json(self, file_path: str) -> List[Dict]:
        """Load JSON data from file."""
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)

class ProgressTracker:
    """Track and report progress during solution generation."""
    
    def __init__(self, total_items: int):
        self.total_items = total_items
        self.processed = 0
        self.successful = 0
        self.failed = 0
        self.skipped = 0
        self.start_time = datetime.now()
    
    def update(self, success: bool = True, skipped: bool = False):
        """Update progress counters."""
        self.processed += 1
        
        if skipped:
            self.skipped += 1
        elif success:
            self.successful += 1
        else:
            self.failed += 1
    
    def get_progress_report(self) -> str:
        """Generate a progress report string."""
        elapsed = datetime.now() - self.start_time
        
        if self.processed > 0:
            avg_time = elapsed.total_seconds() / self.processed
            remaining = self.total_items - self.processed
            eta_seconds = remaining * avg_time
            eta = f"{int(eta_seconds // 60)}m {int(eta_seconds % 60)}s"
        else:
            eta = "Unknown"
        
        percent = (self.processed / self.total_items) * 100 if self.total_items > 0 else 0
        
        return f"""
Progress: {self.processed}/{self.total_items} ({percent:.1f}%)
Successful: {self.successful} | Failed: {self.failed} | Skipped: {self.skipped}
Elapsed: {elapsed} | ETA: {eta}
        """.strip()

def validate_json_structure(data: List[Dict]) -> Tuple[bool, List[str]]:
    """Validate the overall JSON structure."""
    errors = []
    
    if not isinstance(data, list):
        errors.append("Data must be a list of problem objects")
        return False, errors
    
    required_fields = ['question_number', 'title', 'description', 'category']
    
    for i, problem in enumerate(data[:100]):  # Check first 100 for efficiency
        if not isinstance(problem, dict):
            errors.append(f"Problem {i} is not a dictionary")
            continue
        
        for field in required_fields:
            if field not in problem:
                errors.append(f"Problem {i} missing required field: {field}")
    
    return len(errors) == 0, errors

if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description='Solution validation and data safety utilities')
    parser.add_argument('--input', '-i', required=True, help='Input JSON file path')
    parser.add_argument('--validate', action='store_true', help='Validate data structure')
    parser.add_argument('--backup', action='store_true', help='Create backup')
    parser.add_argument('--list-backups', action='store_true', help='List available backups')
    parser.add_argument('--restore', help='Restore from backup file')
    parser.add_argument('--description', '-d', help='Backup description')
    
    args = parser.parse_args()
    
    # Setup logging
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
    
    # Initialize managers
    safety_manager = DataSafetyManager()
    validator = SolutionValidator()
    
    if args.validate:
        with open(args.input, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        is_valid, errors = validate_json_structure(data)
        print(f"Data structure valid: {is_valid}")
        if errors:
            print("Errors found:")
            for error in errors:
                print(f"  - {error}")
    
    elif args.backup:
        with open(args.input, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        backup_path = safety_manager.create_backup(data, args.description or "manual_backup")
        print(f"Backup created: {backup_path}")
    
    elif args.list_backups:
        backups = safety_manager.list_backups()
        print("\nAvailable backups:")
        for backup in backups:
            print(f"  {backup['timestamp']} - {backup['description']} "
                  f"({backup['total_problems']} problems, {backup['size_mb']:.1f} MB)")
    
    elif args.restore:
        target_path = args.input
        success = safety_manager.restore_backup(args.restore, target_path)
        print(f"Restore {'successful' if success else 'failed'}")
    
    else:
        print("Please specify an action: --validate, --backup, --list-backups, or --restore")