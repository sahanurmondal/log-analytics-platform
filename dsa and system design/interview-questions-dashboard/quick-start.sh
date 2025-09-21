#!/bin/bash

# 🚀 Quick Start Script for Interview Questions Dashboard
# This script downloads and runs the dashboard with all data files

echo "🚀 Setting up Interview Questions Dashboard..."
echo "================================================"

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    echo "❌ Python3 is required but not installed."
    echo "Please install Python3 and try again."
    exit 1
fi

# Create temp directory for dashboard
TEMP_DIR="/tmp/interview-dashboard"
rm -rf "$TEMP_DIR" 2>/dev/null
mkdir -p "$TEMP_DIR"
cd "$TEMP_DIR"

echo "📥 Downloading dashboard and data files..."

# Download main dashboard
curl -L -o dashboard.html "https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/dsa%20and%20system%20design/interview-questions-dashboard/dashboard.html"

# Create directory structure for data files
mkdir -p src/company/webscraper/{leetcode_company_wise,leetcode_problem_solution,enginebogie}

# Download data files
echo "📊 Downloading data files..."
curl -L -o "src/company/webscraper/leetcode_company_wise/leetcode_enhanced_complete.json" "https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/dsa%20and%20system%20design/interview-questions-dashboard/src/company/webscraper/leetcode_company_wise/leetcode_enhanced_complete.json"

curl -L -o "src/company/webscraper/leetcode_problem_solution/leetcode_ca_search_enhanced.json" "https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/dsa%20and%20system%20design/interview-questions-dashboard/src/company/webscraper/leetcode_problem_solution/leetcode_ca_search_enhanced.json"

curl -L -o "src/company/webscraper/enginebogie/enginebogie_answer.json" "https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/dsa%20and%20system%20design/interview-questions-dashboard/src/company/webscraper/enginebogie/enginebogie_answer.json"

# Check if all files downloaded successfully
if [ -f "dashboard.html" ] && [ -f "src/company/webscraper/leetcode_company_wise/leetcode_enhanced_complete.json" ] && [ -f "src/company/webscraper/leetcode_problem_solution/leetcode_ca_search_enhanced.json" ] && [ -f "src/company/webscraper/enginebogie/enginebogie_answer.json" ]; then
    echo "✅ All files downloaded successfully!"
    echo "🌐 Starting local server..."
    echo ""
    echo "🎯 Dashboard will be available at: http://localhost:8080/dashboard.html"
    echo "📊 All data files loaded - full functionality available!"
    echo "Press Ctrl+C to stop the server"
    echo "================================================"
    python3 -m http.server 8080
else
    echo "❌ Some files failed to download"
    echo "Please check your internet connection and try again"
    echo "Files downloaded:"
    ls -la
    exit 1
fi