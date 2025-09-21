#!/usr/bin/env python3
"""
LeetCode Company Data Extractor

Multi-threaded script to extract LeetCode problems for each company from 
the LeetCode Wizard API. Extracts all pages of data using pagination and 
saves clean JSON files for each company.

API URL: https://api.leetcodewizard.io/api/v1/problem-database/problems
Parameters: company={company_uuid}&timePer    # Initialize extractor
    extractor = LeetCodeCompanyExtractor(max_workers=3)  # Reduced for testing
    
    # Run extraction for first 3 companies only
    print(f"\n📊 Found {len(companies)} companies")
    print("🧪 Testing with first 3 companies...")
    extractor.extract_all_companies(companies, sample_size=3)&page={pageNo}&limit=50

Features:
- Multi-threading for concurrent company data extraction
- Mozilla User-Agent for web requests
- Automatic pagination handling
- Clean JSON output with selected fields only
- Error handling and retry logic
"""

import json
import requests
import time
import random
import os
from concurrent.futures import ThreadPoolExecutor, as_completed
from threading import Lock
import logging
from typing import Dict, List, Any, Optional

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('leetcode_extraction.log', mode='w'),  # Override log file each run
        logging.StreamHandler()
    ],
    force=True  # Force reconfiguration
)
logger = logging.getLogger(__name__)

class LeetCodeCompanyExtractor:
    """Extract LeetCode problems for companies using multi-threading"""
    
    def __init__(self, output_dir: str = "extract_leetcode_companyInfo", max_workers: Optional[int] = None, time_period: str = "all"):
        self.base_output_dir = output_dir
        self.time_period = time_period  # New parameter for time period filtering
        # Create time-period specific output directory
        self.output_dir = os.path.join(output_dir, time_period)
        
        # Use max available threads - 2 if not specified
        if max_workers is None:
            import multiprocessing
            max_workers = max(1, multiprocessing.cpu_count() - 2)
        self.max_workers = max_workers
        
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'application/json',
            'Accept-Language': 'en-US,en;q=0.9',
            'Connection': 'keep-alive'
        })
        
        self.base_url = "https://api.leetcodewizard.io/api/v1/problem-database/problems"
        self.lock = Lock()
        self.stats = {
            'total_companies': 0,
            'processed_companies': 0,
            'skipped_companies': 0,
            'total_problems': 0,
            'failed_companies': [],
            'successful_companies': [],
            'skipped_companies_list': []
        }
        
        # Ensure output directory exists
        os.makedirs(self.output_dir, exist_ok=True)
    
    def fetch_company_page(self, company_uuid: str, page: int, retries: int = 5) -> Optional[Dict]:
        """Fetch a single page of problems for a company with enhanced error handling"""
        # Build URL with configurable time period
        if self.time_period:
            url = f"{self.base_url}?company={company_uuid}&page={page}&limit=50&timePeriod={self.time_period}"
        else:
            url = f"{self.base_url}?company={company_uuid}&page={page}&limit=50"
        
        # Log the URL being hit
        logger.info(f"🌐 Fetching: {url}")
        
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
            'Accept': 'application/json, text/plain, */*',
            'Accept-Language': 'en-US,en;q=0.9',
            'Accept-Encoding': 'identity',  # Disable compression to avoid issues
            'DNT': '1',
            'Connection': 'keep-alive',
            'Cache-Control': 'no-cache',
            'Pragma': 'no-cache'
        }
        
        for attempt in range(retries):
            try:
                # Progressive delay for retries
                delay = 0.5 * (2 ** attempt) + random.uniform(0.1, 0.3)
                time.sleep(delay)
                
                # Use fresh request instead of session to avoid any session issues
                response = requests.get(url, headers=headers, timeout=30)
                
                logger.info(f"📡 Response: HTTP {response.status_code} for {url}")
                
                if response.status_code == 200:
                    try:
                        json_data = response.json()
                        # Log response structure
                        if 'data' in json_data and 'meta' in json_data:
                            meta = json_data['meta']
                            logger.info(f"📊 Data found: {len(json_data['data'])} items, page {meta.get('page')}/{meta.get('totalPages')}, total: {meta.get('totalItems')}")
                        else:
                            logger.warning(f"⚠️  Unexpected response structure: {list(json_data.keys()) if isinstance(json_data, dict) else type(json_data)}")
                        return json_data
                    except json.JSONDecodeError as e:
                        logger.error(f"📄 JSON Parse Error: {e}")
                        logger.error(f"📄 Response headers: {dict(response.headers)}")
                        logger.error(f"📄 Response content (first 200 chars): {response.text[:200]}")
                        if attempt < retries - 1:
                            continue
                        
                elif response.status_code == 429:  # Rate limited
                    retry_after = int(response.headers.get('Retry-After', 60))
                    wait_time = max(retry_after, 2 ** attempt)
                    logger.warning(f"⏳ Rate limited for {url}. Waiting {wait_time}s...")
                    time.sleep(wait_time)
                    continue
                    
                elif response.status_code == 403:  # Forbidden - might be CORS
                    logger.warning(f"🚫 Access forbidden for {url}. Trying with minimal headers...")
                    # Try with minimal headers
                    minimal_headers = {'User-Agent': headers['User-Agent']}
                    minimal_response = requests.get(url, headers=minimal_headers, timeout=30)
                    if minimal_response.status_code == 200:
                        try:
                            return minimal_response.json()
                        except json.JSONDecodeError:
                            continue
                    else:
                        logger.error(f"❌ Still forbidden with minimal headers: HTTP {minimal_response.status_code}")
                        if attempt < retries - 1:
                            continue
                            
                elif response.status_code in [502, 503, 504]:  # Server errors
                    wait_time = 10 * (2 ** attempt)
                    logger.warning(f"🔧 Server error {response.status_code} for {url}. Waiting {wait_time}s...")
                    time.sleep(wait_time)
                    continue
                    
                else:
                    logger.error(f"❌ HTTP {response.status_code} for {url}")
                    # Log response content for debugging
                    try:
                        content = response.text[:500]  # First 500 chars
                        logger.error(f"📄 Response content: {content}")
                    except:
                        logger.error("📄 Could not read response content")
                    
                    if attempt < retries - 1:
                        continue
                    
            except requests.exceptions.Timeout:
                logger.warning(f"⏱️  Timeout for {url}, attempt {attempt + 1}")
                if attempt < retries - 1:
                    time.sleep(5)
                    continue
                    
            except requests.exceptions.ConnectionError as e:
                logger.warning(f"🔌 Connection error for {url}: {e}")
                if attempt < retries - 1:
                    time.sleep(10)  # Longer wait for connection issues
                    continue
                    
            except requests.exceptions.RequestException as e:
                logger.error(f"🔌 Request error for {url}: {e}")
                if attempt < retries - 1:
                    time.sleep(2 ** attempt)
                    continue
                    
        logger.error(f"❌ Failed to fetch {url} after {retries} attempts")
        return None
    
    def extract_company_data(self, company_name: str, company_uuid: str) -> Dict[str, Any]:
        """Extract all problems for a single company, saving each page separately"""
        logger.info(f"🔍 Extracting data for {company_name} ({company_uuid})")
        
        page = 1
        total_pages = None
        total_problems_saved = 0
        pages_saved = []
        
        try:
            # Create company directory
            company_dir = os.path.join(self.output_dir, company_name.replace(' ', '_').lower())
            os.makedirs(company_dir, exist_ok=True)
            
            # Fetch first page to get pagination info
            first_page_data = self.fetch_company_page(company_uuid, page)
            
            if not first_page_data:
                logger.error(f"❌ Failed to fetch first page for {company_name}")
                # Remove empty directory since no data was found
                try:
                    os.rmdir(company_dir)
                except OSError:
                    pass  # Directory might not be empty or might not exist
                return {'success': False, 'company': company_name, 'pages_saved': []}
            
            # Extract and save problems from first page
            if 'data' in first_page_data:
                problems = self.transform_problems(first_page_data['data'])
                
                # Get total pages from meta
                if 'meta' in first_page_data:
                    total_pages = first_page_data['meta'].get('totalPages', 1)
                    total_items = first_page_data['meta'].get('totalItems', 0)
                    
                    # If no data found on first page, don't process further and remove directory
                    if total_items == 0 or not problems:
                        logger.info(f"⚠️  {company_name}: No problems found, skipping...")
                        # Remove empty directory since no data was found
                        try:
                            os.rmdir(company_dir)
                        except OSError:
                            pass  # Directory might not be empty or might not exist
                        return {'success': False, 'company': company_name, 'pages_saved': [], 'reason': 'no_data'}
                    
                    logger.info(f"📊 {company_name}: {total_items} problems across {total_pages} pages")
                
                # Save first page
                if problems:
                    page_filename = f"{company_name.replace(' ', '_').lower()}_page_{page}.json"
                    page_filepath = os.path.join(company_dir, page_filename)
                    
                    page_data = {
                        'company': company_name,
                        'company_uuid': company_uuid,
                        'page': page,
                        'total_pages': total_pages,
                        'problems_count': len(problems),
                        'problems': problems,
                        'extracted_at': time.strftime('%Y-%m-%d %H:%M:%S')
                    }
                    
                    with open(page_filepath, 'w', encoding='utf-8') as f:
                        json.dump(page_data, f, indent=2, ensure_ascii=False)
                    
                    total_problems_saved += len(problems)
                    pages_saved.append(page_filename)
                    logger.info(f"💾 Saved page {page} for {company_name}: {len(problems)} problems -> {page_filename}")
            else:
                # No 'data' section found in response, remove empty directory
                logger.info(f"⚠️  {company_name}: No data section in response, skipping...")
                try:
                    os.rmdir(company_dir)
                except OSError:
                    pass  # Directory might not be empty or might not exist
                return {'success': False, 'company': company_name, 'pages_saved': [], 'reason': 'no_data'}
            
            # Fetch and save remaining pages if there are any
            if total_pages and total_pages > 1:
                logger.info(f"📄 Fetching {total_pages - 1} additional pages for {company_name}")
                
                for page_num in range(2, total_pages + 1):
                    page_data_response = self.fetch_company_page(company_uuid, page_num)
                    
                    if page_data_response and 'data' in page_data_response:
                        problems = self.transform_problems(page_data_response['data'])
                        
                        if problems:
                            page_filename = f"{company_name.replace(' ', '_').lower()}_page_{page_num}.json"
                            page_filepath = os.path.join(company_dir, page_filename)
                            
                            page_data = {
                                'company': company_name,
                                'company_uuid': company_uuid,
                                'page': page_num,
                                'total_pages': total_pages,
                                'problems_count': len(problems),
                                'problems': problems,
                                'extracted_at': time.strftime('%Y-%m-%d %H:%M:%S')
                            }
                            
                            with open(page_filepath, 'w', encoding='utf-8') as f:
                                json.dump(page_data, f, indent=2, ensure_ascii=False)
                            
                            total_problems_saved += len(problems)
                            pages_saved.append(page_filename)
                            logger.info(f"💾 Saved page {page_num} for {company_name}: {len(problems)} problems -> {page_filename}")
                        else:
                            logger.warning(f"⚠️  No problems found on page {page_num} for {company_name}")
                    else:
                        logger.warning(f"⚠️  Failed to fetch page {page_num} for {company_name}")
                    
                    # Small delay between requests
                    time.sleep(0.2)
            
            logger.info(f"✅ Completed {company_name}: {total_problems_saved} total problems saved across {len(pages_saved)} pages")
            
            return {
                'success': True,
                'company': company_name,
                'pages_saved': pages_saved,
                'total_pages': total_pages or 1,
                'total_problems': total_problems_saved
            }
            
        except Exception as e:
            logger.error(f"❌ Error extracting data for {company_name}: {e}")
            # Remove empty directory if it was created but error occurred
            try:
                company_dir = os.path.join(self.output_dir, company_name.replace(' ', '_').lower())
                if os.path.exists(company_dir) and not os.listdir(company_dir):  # Directory exists and is empty
                    os.rmdir(company_dir)
            except OSError:
                pass  # Directory might not be empty or might not exist
            return {'success': False, 'company': company_name, 'pages_saved': []}
    
    def transform_problems(self, problems_data: List[Dict]) -> List[Dict]:
        """Transform API response to keep only required fields"""
        transformed = []
        
        for problem in problems_data:
            transformed_problem = {
                'title': problem.get('title', ''),
                'titleSlug': problem.get('titleSlug', ''),
                'tags': problem.get('tags', []),
                'difficulty': problem.get('difficulty', 0),
                'paidOnly': problem.get('paidOnly', False),
                'frequency': problem.get('frequency', 0)
            }
            transformed.append(transformed_problem)
        
        return transformed
    
    def process_company(self, company_name: str, company_uuid: str) -> Dict[str, Any]:
        """Process a single company (to be used in thread pool)"""
        try:
            # Extract data (now saves pages automatically)
            result = self.extract_company_data(company_name, company_uuid)
            
            if result['success'] and result['pages_saved']:
                with self.lock:
                    self.stats['processed_companies'] += 1
                    self.stats['total_problems'] += result['total_problems']
                    self.stats['successful_companies'].append(company_name)
                
                return {
                    'status': 'success',
                    'company': company_name,
                    'problems_count': result['total_problems'],
                    'pages': result.get('total_pages', 1),
                    'pages_saved': result['pages_saved']
                }
            else:
                with self.lock:
                    if result.get('reason') == 'no_data':
                        self.stats['skipped_companies'] += 1
                        self.stats['skipped_companies_list'].append(company_name)
                        status = 'skipped_no_data'
                    else:
                        self.stats['failed_companies'].append({
                            'company': company_name,
                            'error': 'Extraction failed'
                        })
                        status = 'failed'
                
                return {
                    'status': status,
                    'company': company_name,
                    'problems_count': 0,
                    'pages': 0,
                    'pages_saved': []
                }
                
        except Exception as e:
            logger.error(f"❌ Error processing {company_name}: {e}")
            with self.lock:
                self.stats['failed_companies'].append({
                    'company': company_name,
                    'error': str(e)
                })
            
            return {
                'status': 'error',
                'company': company_name,
                'problems_count': 0,
                'pages': 0,
                'error': str(e),
                'pages_saved': []
            }
    
    def extract_all_companies(self, companies: Dict[str, str], sample_size: Optional[int] = None):
        """Extract data for all companies using multi-threading"""
        
        if sample_size:
            # Take only a sample for testing
            companies_items = list(companies.items())[:sample_size]
            logger.info(f"🧪 SAMPLE MODE: Processing {sample_size} companies")
        else:
            companies_items = list(companies.items())
        
        self.stats['total_companies'] = len(companies_items)
        
        logger.info(f"🚀 Starting extraction for {len(companies_items)} companies using {self.max_workers} threads")
        
        start_time = time.time()
        
        # Process companies using thread pool
        with ThreadPoolExecutor(max_workers=self.max_workers) as executor:
            # Submit all tasks
            future_to_company = {
                executor.submit(self.process_company, company_name, company_uuid): company_name
                for company_name, company_uuid in companies_items
            }
            
            # Process completed tasks
            for future in as_completed(future_to_company):
                company_name = future_to_company[future]
                try:
                    result = future.result()
                    if result['status'] == 'success':
                        logger.info(f"✅ {company_name}: {result['problems_count']} problems ({result['pages']} pages)")
                    elif result['status'] == 'skipped':
                        with self.lock:
                            self.stats['skipped_companies'] += 1
                            self.stats['skipped_companies_list'].append(company_name)
                        logger.info(f"📭 {company_name}: No data - skipped")
                    else:
                        logger.error(f"❌ {company_name}: {result.get('error', 'Unknown error')}")
                        
                except Exception as e:
                    logger.error(f"❌ {company_name}: Exception - {e}")
        
        end_time = time.time()
        duration = end_time - start_time
        
        # Print final report
        self.print_final_report(duration)
    
    def print_final_report(self, duration: float):
        """Print extraction summary report"""
        print("\n" + "="*80)
        print("🎯 LEETCODE COMPANY DATA EXTRACTION REPORT")
        print("="*80)
        
        print(f"\n📊 SUMMARY:")
        print(f"   Total Companies: {self.stats['total_companies']}")
        print(f"   Successfully Processed: {self.stats['processed_companies']}")
        print(f"   Skipped (No Data): {self.stats['skipped_companies']}")
        print(f"   Failed: {len(self.stats['failed_companies'])}")
        print(f"   Total Problems Extracted: {self.stats['total_problems']:,}")
        print(f"   Duration: {duration:.2f} seconds")
        print(f"   Average: {duration/self.stats['total_companies']:.2f}s per company")
        
        if self.stats['successful_companies']:
            print(f"\n✅ SUCCESSFUL COMPANIES ({len(self.stats['successful_companies'])}):")
            for company in sorted(self.stats['successful_companies'][:10]):
                print(f"   ✓ {company}")
            if len(self.stats['successful_companies']) > 10:
                print(f"   ... and {len(self.stats['successful_companies']) - 10} more")
        
        if self.stats['skipped_companies_list']:
            print(f"\n📭 SKIPPED COMPANIES (No Data) ({len(self.stats['skipped_companies_list'])}):")
            for company in sorted(self.stats['skipped_companies_list'][:10]):
                print(f"   ○ {company}")
            if len(self.stats['skipped_companies_list']) > 10:
                print(f"   ... and {len(self.stats['skipped_companies_list']) - 10} more")
        
        if self.stats['failed_companies']:
            print(f"\n❌ FAILED COMPANIES ({len(self.stats['failed_companies'])}):")
            for company in self.stats['failed_companies'][:10]:
                print(f"   ✗ {company}")
            if len(self.stats['failed_companies']) > 10:
                print(f"   ... and {len(self.stats['failed_companies']) - 10} more")
        
        print(f"\n📁 Output Directory: {self.output_dir}")
        print("🎉 Extraction Complete!")

def load_companies_from_wizard_json(filepath: str) -> Dict[str, str]:
    """Load companies from leetcode_wizard.json"""
    try:
        with open(filepath, 'r') as f:
            companies = json.load(f)
        
        logger.info(f"📂 Loaded {len(companies)} companies from {filepath}")
        return companies
        
    except Exception as e:
        logger.error(f"❌ Error loading companies from {filepath}: {e}")
        return {}

def main():
    """Main extraction function"""
    print("🚀 LeetCode Company Data Extractor")
    print("=" * 50)
    
    # Load companies
    wizard_file = "../leetcode_wizard.json"
    companies = load_companies_from_wizard_json(wizard_file)
    
    if not companies:
        logger.error("❌ No companies loaded. Exiting.")
        return
    
    # Initialize extractor
    extractor = LeetCodeCompanyExtractor(max_workers=8)
    
    # Run extraction for all companies
    print(f"\n📊 Found {len(companies)} companies")
    print("� Starting full extraction for all companies...")
    extractor.extract_all_companies(companies)

if __name__ == "__main__":
    main()