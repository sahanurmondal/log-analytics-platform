import React, { useState } from 'react';
import { ExternalLink, ChevronDown, ChevronUp, MessageSquare } from 'lucide-react';
import './QuestionsList.css';

const QuestionCard = ({ question }) => {
    const [isExpanded, setIsExpanded] = useState(false);
    const [showAnswer, setShowAnswer] = useState(false);

    const {
        question_number,
        title,
        description,
        company,
        category,
        difficulty,
        tags = [],
        url,
        source
    } = question;

    const difficultyClass = `difficulty-${difficulty?.toLowerCase() || 'unknown'}`;
    const shortDescription = description?.slice(0, 200) + (description?.length > 200 ? '...' : '');

    return (
        <div className="question-card">
            <div className="question-header">
                <div className="question-title">
                    <span className="question-number">Q{question_number}</span>
                    <h3>{title}</h3>
                </div>
                <div className="question-meta">
                    <span className={`meta-tag ${difficultyClass}`}>
                        {difficulty || 'Unknown'}
                    </span>
                    <span className="meta-tag category">
                        {category || 'General'}
                    </span>
                    <span className="meta-tag company">
                        🏢 {company || 'Unknown'}
                    </span>
                    <span className="meta-tag source">
                        📡 {source || 'Unknown'}
                    </span>
                </div>
            </div>

            <div className="question-description">
                <p>{isExpanded ? description : shortDescription}</p>
                {description && description.length > 200 && (
                    <button
                        className="expand-btn"
                        onClick={() => setIsExpanded(!isExpanded)}
                    >
                        {isExpanded ? (
                            <>
                                <ChevronUp size={16} />
                                Show Less
                            </>
                        ) : (
                            <>
                                <ChevronDown size={16} />
                                Show More
                            </>
                        )}
                    </button>
                )}
            </div>

            {tags.length > 0 && (
                <div className="question-tags">
                    {tags.slice(0, 6).map((tag, index) => (
                        <span key={index} className="tag">
                            {tag}
                        </span>
                    ))}
                    {tags.length > 6 && (
                        <span className="tag more">+{tags.length - 6} more</span>
                    )}
                </div>
            )}

            <div className="question-actions">
                <button
                    className="answer-btn"
                    onClick={() => setShowAnswer(!showAnswer)}
                >
                    <MessageSquare size={16} />
                    {showAnswer ? 'Hide Answer' : 'Show Answer'}
                </button>

                {url && (
                    <a
                        href={url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="view-original"
                    >
                        <ExternalLink size={16} />
                        View Original
                    </a>
                )}
            </div>

            {showAnswer && (
                <div className="answer-section">
                    <div className="answer-placeholder">
                        <MessageSquare size={24} />
                        <p>Answer section coming soon...</p>
                        <small>This feature will be implemented to show detailed solutions and explanations.</small>
                    </div>
                </div>
            )}
        </div>
    );
};

const QuestionsList = ({ questions, totalQuestions }) => {
    const [currentPage, setCurrentPage] = useState(1);
    const questionsPerPage = 20;

    const totalPages = Math.ceil(questions.length / questionsPerPage);
    const startIndex = (currentPage - 1) * questionsPerPage;
    const endIndex = startIndex + questionsPerPage;
    const currentQuestions = questions.slice(startIndex, endIndex);

    const goToPage = (page) => {
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const generatePageNumbers = () => {
        const pages = [];
        const maxVisible = 5;

        if (totalPages <= maxVisible) {
            for (let i = 1; i <= totalPages; i++) {
                pages.push(i);
            }
        } else {
            if (currentPage <= 3) {
                pages.push(1, 2, 3, 4, '...', totalPages);
            } else if (currentPage >= totalPages - 2) {
                pages.push(1, '...', totalPages - 3, totalPages - 2, totalPages - 1, totalPages);
            } else {
                pages.push(1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages);
            }
        }

        return pages;
    };

    if (questions.length === 0) {
        return (
            <div className="questions-list">
                <div className="no-results">
                    <h3>No questions found</h3>
                    <p>Try adjusting your filters to see more results.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="questions-list">
            <div className="list-header">
                <h2>
                    📝 Questions
                    <span className="count">
                        ({questions.length.toLocaleString()} of {totalQuestions.toLocaleString()})
                    </span>
                </h2>
                <div className="pagination-info">
                    Showing {startIndex + 1}-{Math.min(endIndex, questions.length)} of {questions.length.toLocaleString()}
                </div>
            </div>

            <div className="questions-grid">
                {currentQuestions.map((question, index) => (
                    <QuestionCard key={question.url || index} question={question} />
                ))}
            </div>

            {totalPages > 1 && (
                <div className="pagination">
                    <button
                        onClick={() => goToPage(currentPage - 1)}
                        disabled={currentPage === 1}
                        className="page-btn"
                    >
                        Previous
                    </button>

                    {generatePageNumbers().map((page, index) => (
                        <button
                            key={index}
                            onClick={() => typeof page === 'number' && goToPage(page)}
                            className={`page-btn ${page === currentPage ? 'active' : ''} ${typeof page !== 'number' ? 'dots' : ''}`}
                            disabled={typeof page !== 'number'}
                        >
                            {page}
                        </button>
                    ))}

                    <button
                        onClick={() => goToPage(currentPage + 1)}
                        disabled={currentPage === totalPages}
                        className="page-btn"
                    >
                        Next
                    </button>
                </div>
            )}
        </div>
    );
};

export default QuestionsList;