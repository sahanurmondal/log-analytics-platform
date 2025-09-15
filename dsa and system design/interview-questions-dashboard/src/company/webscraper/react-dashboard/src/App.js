import React, { useState, useEffect, useMemo } from 'react';
import Header from './components/Header';
import Statistics from './components/Statistics';
import Filters from './components/Filters';
import QuestionsList from './components/QuestionsList';
import LoadingSpinner from './components/LoadingSpinner';
import questionsData from './data/questions.json';

function App() {
    const [questions, setQuestions] = useState([]);
    const [filteredQuestions, setFilteredQuestions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({
        company: '',
        category: '',
        difficulty: '',
        questionNumber: '',
        search: ''
    });

    // Load and process questions data
    useEffect(() => {
        const loadQuestions = async () => {
            try {
                setQuestions(questionsData);
                setFilteredQuestions(questionsData);
                setLoading(false);
            } catch (error) {
                console.error('Error loading questions:', error);
                setLoading(false);
            }
        };

        loadQuestions();
    }, []);

    // Filter questions based on current filters
    const applyFilters = useMemo(() => {
        let filtered = [...questions];

        if (filters.company) {
            filtered = filtered.filter(q =>
                q.company?.toLowerCase().includes(filters.company.toLowerCase())
            );
        }

        if (filters.category) {
            filtered = filtered.filter(q =>
                q.category?.toLowerCase().includes(filters.category.toLowerCase())
            );
        }

        if (filters.difficulty) {
            filtered = filtered.filter(q =>
                q.difficulty?.toLowerCase() === filters.difficulty.toLowerCase()
            );
        }

        if (filters.questionNumber) {
            const questionNum = filters.questionNumber.trim();
            if (questionNum.includes('-')) {
                const [start, end] = questionNum.split('-').map(num => parseInt(num.trim()));
                if (!isNaN(start) && !isNaN(end)) {
                    filtered = filtered.filter(q =>
                        q.question_number >= start && q.question_number <= end
                    );
                }
            } else {
                const single = parseInt(questionNum);
                if (!isNaN(single)) {
                    filtered = filtered.filter(q => q.question_number === single);
                }
            }
        }

        if (filters.search) {
            const searchTerm = filters.search.toLowerCase();
            filtered = filtered.filter(q =>
                q.title?.toLowerCase().includes(searchTerm) ||
                q.description?.toLowerCase().includes(searchTerm) ||
                q.tags?.some(tag => tag.toLowerCase().includes(searchTerm))
            );
        }

        return filtered;
    }, [questions, filters]);

    useEffect(() => {
        setFilteredQuestions(applyFilters);
    }, [applyFilters]);

    // Calculate statistics
    const statistics = useMemo(() => {
        const companies = [...new Set(questions.map(q => q.company).filter(Boolean))];
        const categories = [...new Set(questions.map(q => q.category).filter(Boolean))];

        const companyDistribution = {};
        const categoryDistribution = {};
        const difficultyDistribution = {};

        questions.forEach(q => {
            if (q.company) {
                companyDistribution[q.company] = (companyDistribution[q.company] || 0) + 1;
            }
            if (q.category) {
                categoryDistribution[q.category] = (categoryDistribution[q.category] || 0) + 1;
            }
            if (q.difficulty) {
                difficultyDistribution[q.difficulty] = (difficultyDistribution[q.difficulty] || 0) + 1;
            }
        });

        return {
            total: questions.length,
            companies: companies.length,
            categories: categories.length,
            companyDistribution: Object.entries(companyDistribution)
                .sort(([, a], [, b]) => b - a)
                .slice(0, 10),
            categoryDistribution: Object.entries(categoryDistribution)
                .sort(([, a], [, b]) => b - a),
            difficultyDistribution: Object.entries(difficultyDistribution)
                .sort(([, a], [, b]) => b - a),
            filteredCount: filteredQuestions.length
        };
    }, [questions, filteredQuestions]);

    const handleFilterChange = (filterType, value) => {
        setFilters(prev => ({
            ...prev,
            [filterType]: value
        }));
    };

    const clearFilters = () => {
        setFilters({
            company: '',
            category: '',
            difficulty: '',
            questionNumber: '',
            search: ''
        });
    };

    if (loading) {
        return <LoadingSpinner />;
    }

    return (
        <div className="container">
            <Header
                totalQuestions={statistics.total}
                filteredQuestions={statistics.filteredCount}
            />

            <Statistics statistics={statistics} />

            <Filters
                filters={filters}
                onFilterChange={handleFilterChange}
                onClearFilters={clearFilters}
                statistics={statistics}
            />

            <QuestionsList
                questions={filteredQuestions}
                totalQuestions={statistics.total}
            />
        </div>
    );
}

export default App;