import React from 'react';
import { BarChart, Building, Tag, Star } from 'lucide-react';
import './Statistics.css';

const Statistics = ({ statistics }) => {
    const {
        total,
        companies,
        categories,
        companyDistribution,
        categoryDistribution,
        difficultyDistribution
    } = statistics;

    return (
        <div className="statistics">
            <div className="stats-grid">
                <div className="stat-card">
                    <div className="stat-icon">
                        <BarChart size={32} />
                    </div>
                    <h3>{total.toLocaleString()}</h3>
                    <p>Total Questions</p>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">
                        <Building size={32} />
                    </div>
                    <h3>{companies}</h3>
                    <p>Companies</p>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">
                        <Tag size={32} />
                    </div>
                    <h3>{categories}</h3>
                    <p>Categories</p>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">
                        <Star size={32} />
                    </div>
                    <h3>{Math.round((total / 2225) * 100)}%</h3>
                    <p>Coverage (Q1-Q2225)</p>
                </div>
            </div>

            <div className="distribution-grid">
                <div className="distribution-card">
                    <h4>🏢 Top Companies</h4>
                    <div className="distribution-list">
                        {companyDistribution.slice(0, 5).map(([company, count]) => (
                            <div key={company} className="distribution-item">
                                <span className="name">{company}</span>
                                <span className="count">{count}</span>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="distribution-card">
                    <h4>📁 Top Categories</h4>
                    <div className="distribution-list">
                        {categoryDistribution.slice(0, 5).map(([category, count]) => (
                            <div key={category} className="distribution-item">
                                <span className="name">{category}</span>
                                <span className="count">{count}</span>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="distribution-card">
                    <h4>⚡ Difficulty</h4>
                    <div className="distribution-list">
                        {difficultyDistribution.map(([difficulty, count]) => (
                            <div key={difficulty} className="distribution-item">
                                <span className={`name difficulty-${difficulty.toLowerCase()}`}>
                                    {difficulty}
                                </span>
                                <span className="count">{count}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Statistics;