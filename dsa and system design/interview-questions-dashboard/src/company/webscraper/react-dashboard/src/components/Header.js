import React from 'react';
import './Header.css';

const Header = ({ totalQuestions, filteredQuestions }) => {
    return (
        <div className="header">
            <h1>🎯 Interview Questions Dashboard</h1>
            <p>Comprehensive collection organized by company and category</p>
            <div className="header-stats">
                <span className="stat">
                    📝 {totalQuestions.toLocaleString()} Total Questions
                </span>
                {filteredQuestions !== totalQuestions && (
                    <span className="stat filtered">
                        🔍 {filteredQuestions.toLocaleString()} Filtered Results
                    </span>
                )}
                <span className="stat">
                    🌐 Source: enginebogie.com
                </span>
            </div>
            <p className="timestamp">
                Last updated: {new Date().toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                })}
            </p>
        </div>
    );
};

export default Header;