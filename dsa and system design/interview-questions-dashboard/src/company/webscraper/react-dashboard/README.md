# Interview Questions Dashboard

A modern, GitHub-compatible React dashboard for browsing and managing interview questions scraped from EngineBogie.com.

## Features

- **Advanced Filtering**: Filter by company, category, difficulty, question number ranges, and search text
- **Statistics Dashboard**: View totals, distributions, and top companies/categories
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Pagination**: Efficiently browse through questions with pagination
- **Answer Support**: Built-in placeholder system for future answer integration
- **GitHub Compatible**: Lightweight React build suitable for GitHub Pages deployment

## Data Overview

- **Total Questions**: 1,299 questions extracted
- **Question Range**: Q7 - Q2225 (with gaps identified)
- **Source**: All questions from EngineBogie.com
- **Categories**: Multiple categories including Arrays, Trees, DP, Graphs, etc.
- **Companies**: Questions from various companies

## Project Structure

```
react-dashboard/
├── public/
│   └── index.html          # HTML template
├── src/
│   ├── components/
│   │   ├── Header.js       # Dashboard header with stats
│   │   ├── Statistics.js   # Statistics cards and distributions
│   │   ├── Filters.js      # Advanced filtering interface
│   │   ├── QuestionsList.js # Questions display with pagination
│   │   └── LoadingSpinner.js # Loading component
│   ├── data/
│   │   └── questions.json  # Complete questions dataset
│   ├── App.js              # Main application component
│   ├── index.js            # React entry point
│   └── index.css           # Global styles
├── package.json            # Dependencies and scripts
└── README.md              # This file
```

## Getting Started

### Prerequisites
- Node.js (v14 or higher)
- npm or yarn

### Installation

1. Navigate to the dashboard directory:
   ```bash
   cd src/company/webscraper/react-dashboard
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. Open your browser and navigate to:
   ```
   http://localhost:3000
   ```

### Building for Production

To create a production build for GitHub Pages or other hosting:

```bash
npm run build
```

This creates a `build/` directory with optimized static files ready for deployment.

## Usage

### Filtering Questions
- **Company Filter**: Select specific companies to view their questions
- **Category Filter**: Filter by problem categories (Arrays, Trees, etc.)
- **Difficulty Filter**: Filter by Easy, Medium, Hard difficulty levels
- **Question Number Range**: Set minimum and maximum question numbers
- **Search**: Full-text search across question titles and descriptions

### Statistics
- View total counts for questions, companies, and categories
- See difficulty and category distributions
- Identify top companies and categories by question count

### Question Cards
- Expandable descriptions for detailed problem statements
- Quick access to original question URLs
- Answer placeholder sections for future implementation
- Tags and metadata for easy categorization

## Future Enhancements

- **Answer Integration**: Add solution explanations and code examples
- **User Favorites**: Bookmark frequently referenced questions
- **Progress Tracking**: Mark questions as completed/practiced
- **Export Features**: Export filtered question sets
- **Advanced Search**: Regular expression and tag-based search
- **Theme Support**: Dark/light mode toggle

## Technical Details

- **Framework**: React 18 with Hooks
- **Styling**: CSS Grid/Flexbox with modern gradients and animations
- **Icons**: Lucide React icon library
- **State Management**: React useState and useEffect hooks
- **Performance**: Pagination and lazy loading for optimal performance

## GitHub Deployment

This dashboard is designed to be GitHub-compatible and can be easily deployed to GitHub Pages:

1. Build the production version: `npm run build`
2. Deploy the `build/` folder to GitHub Pages
3. The dashboard will be accessible via your GitHub Pages URL

## Data Source

All questions were extracted using the MultithreadedEngineBogieScraper from EngineBogie.com with the following process:

1. **Full Site Scan**: Scraped pages 1-130 (10 questions per page)
2. **Question Number Extraction**: Extracted numeric IDs from URLs
3. **Gap Analysis**: Identified missing questions in the sequence
4. **Data Processing**: Added question numbers, source tracking, and sorting
5. **JSON Export**: Generated complete dataset with 1,299 questions

## License

This project is part of the LeetCode practice repository for educational purposes.