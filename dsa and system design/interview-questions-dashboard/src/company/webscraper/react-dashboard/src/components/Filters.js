import React from 'react';
import { Search, Filter, X } from 'lucide-react';
import './Filters.css';

const Filters = ({ filters, onFilterChange, onClearFilters, statistics }) => {
    const { companyDistribution, categoryDistribution } = statistics;

    const companies = companyDistribution.map(([company]) => company);
    const categories = categoryDistribution.map(([category]) => category);
    const difficulties = ['Easy', 'Medium', 'Hard'];

    const hasActiveFilters = Object.values(filters).some(filter => filter !== '');

    return (
        <div className="filters">
            <div className="filters-header">
                <h3>
                    <Filter size={20} />
                    Filter Questions
                </h3>
                {hasActiveFilters && (
                    <button className="clear-filters" onClick={onClearFilters}>
                        <X size={16} />
                        Clear All
                    </button>
                )}
            </div>

            <div className="filters-grid">
                <div className="filter-group">
                    <label htmlFor="company">Company</label>
                    <select
                        id="company"
                        value={filters.company}
                        onChange={(e) => onFilterChange('company', e.target.value)}
                    >
                        <option value="">All Companies</option>
                        {companies.map(company => (
                            <option key={company} value={company}>{company}</option>
                        ))}
                    </select>
                </div>

                <div className="filter-group">
                    <label htmlFor="category">Category</label>
                    <select
                        id="category"
                        value={filters.category}
                        onChange={(e) => onFilterChange('category', e.target.value)}
                    >
                        <option value="">All Categories</option>
                        {categories.map(category => (
                            <option key={category} value={category}>{category}</option>
                        ))}
                    </select>
                </div>

                <div className="filter-group">
                    <label htmlFor="difficulty">Difficulty</label>
                    <select
                        id="difficulty"
                        value={filters.difficulty}
                        onChange={(e) => onFilterChange('difficulty', e.target.value)}
                    >
                        <option value="">All Difficulties</option>
                        {difficulties.map(difficulty => (
                            <option key={difficulty} value={difficulty}>{difficulty}</option>
                        ))}
                    </select>
                </div>

                <div className="filter-group">
                    <label htmlFor="questionNumber">Question Number</label>
                    <input
                        id="questionNumber"
                        type="text"
                        placeholder="e.g., 1-100 or 50"
                        value={filters.questionNumber}
                        onChange={(e) => onFilterChange('questionNumber', e.target.value)}
                    />
                </div>

                <div className="filter-group search-group">
                    <label htmlFor="search">Search</label>
                    <div className="search-input">
                        <Search size={18} />
                        <input
                            id="search"
                            type="text"
                            placeholder="Search questions, descriptions, tags..."
                            value={filters.search}
                            onChange={(e) => onFilterChange('search', e.target.value)}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Filters;