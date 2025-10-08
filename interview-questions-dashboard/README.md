# 🎯 Enhanced Interview Questions Dashboard

**Live Dashboard**: [View Here](https://sahanurmondal.github.io/log-analytics-platform/interview-questions-dashboard/dashboard.html)

## 📊 Overview
Comprehensive interview preparation dashboard with **1,299 DSA questions** featuring AI-generated solutions with syntax highlighting and code execution simulation.

### 🚀 Quick Commands for Contributors

**Setup Gemini API (Required for AI generation):**
```bash
export GEMINI_API_KEY='your-api-key-here'
```

**Run Batch 2 Processing:**
```bash
cd /Users/sahanur/IdeaProjects/log-analytics-platform
python3 interview-questions-dashboard/threaded_multi_ai.py \
  -i interview-questions-dashboard/batch_2_gemini.json \
  -c DSA -p gemini --backup --threads 4
```

Get your Gemini API key: https://makersuite.google.com/app/apikey

### ✨ Enhanced Features
- 🤖 **193 AI-Generated Solutions** (14.9% coverage) powered by Google Gemini 2.0 Flash
- 💻 **IDE-Like Code Editor** with VS Code dark theme and Prism.js syntax highlighting
- ▶️ **Code Execution Simulation** - Run Java code with test case parsing and output display
- 🔍 **Advanced Filtering**: Company, category, difficulty, AI solution status, question type
- 📱 **Modern Responsive Design**: Clean, professional interface on all devices
- 🎨 **Professional UI**: Dark theme, scrollable code blocks, copy-to-clipboard
- � **Smart Cache-Busting**: Always loads fresh data (no stale cache issues)
- � **Real-time Stats**: Track problems with/without AI solutions

### 🚀 Quick Start
**Option 1: One-Command Setup** (Recommended)
```bash
# Downloads dashboard + all data files and starts server
curl -s "https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/interview-questions-dashboard/quick-start.sh" | bash
```
⭐ **This fixes the "Error loading questions data" issue by downloading all required files**

**Option 2: Manual Setup**
1. **Download**: [dashboard.html](https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/interview-questions-dashboard/dashboard.html) (Right-click → Save As)
2. **⚠️ Important**: Also download the data files or you'll get loading errors
3. **Run Server**: `python3 -m http.server 8080` in the same folder
4. **Open**: `http://localhost:8080/dashboard.html`

**Option 3: Try GitHub Pages**
- [Enhanced Dashboard](https://sahanurmondal.github.io/log-analytics-platform/interview-questions-dashboard/dashboard.html)

### 📱 Access Methods
- **Setup Page**: [Interactive Setup Guide](https://sahanurmondal.github.io/log-analytics-platform/interview-questions-dashboard/setup.html)
- **Quick Script**: `curl -s "https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/interview-questions-dashboard/quick-start.sh" | bash`
- **Direct Download**: [dashboard.html](https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/interview-questions-dashboard/dashboard.html)
- **GitHub Pages**: [dashboard.html](https://sahanurmondal.github.io/log-analytics-platform/interview-questions-dashboard/dashboard.html)
- **Local File**: Double-click downloaded `dashboard.html` (limited functionality)
- **Mobile Browser**: Works perfectly with local server setup

### 🛠️ Technical Details
- **Enhanced Content**: 818 coding questions with optimal solutions and detailed explanations
- **System Design**: 10 complete markdown answer files with learning resources
- **Modern UI**: Responsive design with advanced filtering and search capabilities
- **Data Sources**: JSON files with comprehensive answers and problem-specific tips
- **Performance**: Optimized for both local and GitHub Pages deployment
- **Compatibility**: Works across all modern browsers and devices

### 🤖 AI Solution Generation (For Contributors)

#### **Setup Gemini API**
To generate AI solutions for interview questions, you need a Google Gemini API key:

```bash
# Get your API key from: https://makersuite.google.com/app/apikey
export GEMINI_API_KEY='your-api-key-here'

# Or add to .env file in interview-questions-dashboard directory
echo "GEMINI_API_KEY=your-api-key-here" > .env
```

#### **Generate Solutions with Threading**
```bash
# Navigate to project directory
cd /Users/sahanur/IdeaProjects/log-analytics-platform

# Test with 10 problems first
python3 interview-questions-dashboard/threaded_multi_ai.py \
  -i interview-questions-dashboard/test_gemini_10.json \
  -c DSA -p gemini

# Process batch 2 (180 problems) - RECOMMENDED STARTING POINT
python3 interview-questions-dashboard/threaded_multi_ai.py \
  -i interview-questions-dashboard/batch_2_gemini.json \
  -c DSA -p gemini --backup --threads 4

# Process all batches sequentially
python3 interview-questions-dashboard/threaded_multi_ai.py \
  -i interview-questions-dashboard/batch_1_gemini.json \
  -c DSA -p gemini --backup --threads 4

python3 interview-questions-dashboard/threaded_multi_ai.py \
  -i interview-questions-dashboard/batch_3_gemini.json \
  -c DSA -p gemini --backup --threads 4

python3 interview-questions-dashboard/threaded_multi_ai.py \
  -i interview-questions-dashboard/batch_4_gemini.json \
  -c DSA -p gemini --backup --threads 4

python3 interview-questions-dashboard/threaded_multi_ai.py \
  -i interview-questions-dashboard/batch_5_gemini.json \
  -c DSA -p gemini --backup --threads 4
```

**Command Options:**
- `-i, --input`: Input JSON file path (required)
- `-c, --category`: Category to process (e.g., DSA)
- `-p, --provider`: AI provider (gemini, openai, claude)
- `--backup`: Create backup before processing
- `--threads`: Number of threads (default: 4)
- `--no-resume`: Don't resume from checkpoint
- `--dry-run`: Show what would be processed without running

#### **Cost Estimates (Gemini 2.0 Flash)**
- **Cost**: $0.0005 per 1K tokens (~$0.001 per problem)
- **Test (10 problems)**: ~$0.01
- **Batch (180 problems)**: ~$0.18
- **Full dataset (1,088 problems)**: ~$1.09
- **Daily Limit**: ~1,500 requests

#### **Features**
- ✅ Multi-threaded processing (4 threads default)
- ✅ Auto-resume on network failure (checkpoint system)
- ✅ Backup creation before processing
- ✅ Rate limiting (15 requests/minute)
- ✅ Progress tracking and statistics
- ✅ Only processes empty answers

### 🔧 Troubleshooting

#### **🔄 GitHub Pages Shows Old Data (Cached JSON)**
**Problem**: Browser/CDN caching causes old JSON data to load even after updates.

**Solution - Force Fresh Data** (Already implemented in dashboard):
```javascript
// Dashboard uses aggressive cache-busting:
// - Timestamp + Random string on every load
// - No-cache HTTP headers
// - Unique URL: file.json?v=1759727XXX&r=abc123
```

**How to Verify Fresh Data:**
1. Open DevTools Console (F12)
2. Look for these logs:
   ```
   ✅ Loaded interview prep data: 1299 problems
   📊 Problems with AI answers: 193 (14.9%)
   🔄 Cache buster: v=1759727XXX&r=abc123  ← Should change on each reload
   ⏰ Latest AI solution: 10/6/2025, 10:30:45 AM
   ```
3. **Each page refresh** should show a DIFFERENT cache buster value

**If Still Seeing Old Data:**
- **Hard Refresh**: `Ctrl+Shift+R` (Windows) or `Cmd+Shift+R` (Mac)
- **Incognito Mode**: Test in private/incognito window
- **Wait 10-15 min**: GitHub Pages CDN needs time to propagate
- **Clear All Cache**: DevTools → Network tab → Disable cache checkbox

#### **⚠️ Console Warnings (Safe to Ignore)**
You may see these development warnings - they're **harmless**:
```
❌ runtime.lastError: The message port closed
   → Browser extension interference (safe to ignore)

⚠️  ReactDOM.render is no longer supported in React 18
   → Development warning only (app works fine)

⚠️  You are using in-browser Babel transformer
   → Expected for single-file HTML app
```

**To Hide These Warnings**: Just close the Console tab - they don't affect functionality.

#### **🚀 Immediate Solutions:**
1. **Download & Run Locally** (Recommended):
   ```bash
   # Downloads dashboard + all data files automatically
   curl -s "https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/interview-questions-dashboard/quick-start.sh" | bash
   ```

2. **Direct File Download**: 
   - [Download dashboard.html](https://raw.githubusercontent.com/sahanurmondal/log-analytics-platform/main/interview-questions-dashboard/dashboard.html)
   - Right-click → Save As → Open in browser

3. **Clone Repository**:
   ```bash
   git clone https://github.com/sahanurmondal/log-analytics-platform.git
   cd "log-analytics-platform/interview-questions-dashboard"
   python3 -m http.server 8080
   ```

#### **❌ "Error loading questions data" Fix:**
This error occurs when data files are missing. Solutions:
- **Use Quick Start Script** (downloads everything automatically)
- **Run with Server** (not just double-clicking HTML file)
- **Check File Structure** (src/company/webscraper/ folders must exist)

#### **✅ Verified Working Method:**
**Local Server** (100% reliable):
1. Use the quick-start script above, or
2. Download dashboard.html + data files and run `python3 -m http.server 8080`
3. Open `http://localhost:8080/dashboard.html`

### 📊 Question Database
- **Total DSA Questions**: 1,299 from EngineBogie platform
- **AI Solutions**: 193 complete solutions (14.9% coverage, growing!)
- **AI Provider**: Google Gemini 2.0 Flash API
- **Solution Quality**: Production-ready code with explanations, complexity analysis, and examples
- **Companies**: Top tech companies (Google, Meta, Amazon, Microsoft, Netflix, etc.)
- **Categories**: Arrays, Trees, DP, Graphs, Strings, System Design, and more
- **Features**: Syntax highlighting, code execution simulation, test case parsing

---
*Enhanced dashboard with comprehensive interview preparation resources*
