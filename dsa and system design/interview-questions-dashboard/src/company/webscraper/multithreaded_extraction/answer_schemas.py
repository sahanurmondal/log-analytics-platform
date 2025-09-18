"""
Answer Schema Templates for Different Question Categories
Based on analysis of categories in the questions dataset
"""

# DSA Category Schema (827 questions)
DSA_SCHEMA = {
    "question_number": 0,
    "title": "",
    "category": "DSA",
    "difficulty": "",  # Easy/Medium/Hard
    "answer": {
        "problem_understanding": "",
        "key_insights": [],
        "approaches": [
            {
                "name": "",
                "time_complexity": "",
                "space_complexity": "",
                "description": "",
                "code": "",
                "pros": [],
                "cons": []
            }
        ],
        "optimization_notes": "",
        "interview_tips": [],
        "common_mistakes": [],
        "follow_up_questions": []
    },
    "references": {
        "leetcode": "",
        "hackerrank": "",
        "local_solution": "",
        "similar_problems": [],
        "blog_links": [],
        "video_tutorials": []
    }
}

# System Design Schema (210 questions)
SYSTEM_DESIGN_SCHEMA = {
    "question_number": 0,
    "title": "",
    "category": "System Design",
    "scale": "",  # Small/Medium/Large scale
    "answer": {
        "problem_understanding": "",
        "functional_requirements": [],
        "non_functional_requirements": [],
        "capacity_estimation": {
            "users": "",
            "requests_per_second": "",
            "storage": "",
            "bandwidth": ""
        },
        "high_level_design": {
            "components": [],
            "data_flow": [],
            "api_design": []
        },
        "detailed_design": {
            "database_design": "",
            "system_components": [],
            "algorithms": []
        },
        "scalability_considerations": [],
        "trade_offs": [],
        "monitoring_and_alerting": [],
        "interview_tips": []
    },
    "references": {
        "system_design_primer": "",
        "blog_links": [],
        "similar_systems": [],
        "real_world_examples": []
    }
}

# Behavioral Questions Schema (103 questions)
BEHAVIORAL_SCHEMA = {
    "question_number": 0,
    "title": "",
    "category": "Behavioral",
    "question_type": "",  # Leadership, Conflict, Challenge, etc.
    "answer": {
        "question_analysis": "",
        "star_framework": {
            "situation": "",
            "task": "",
            "action": "",
            "result": ""
        },
        "key_points_to_highlight": [],
        "alternative_examples": [],
        "follow_up_responses": [],
        "interview_tips": [],
        "common_mistakes": []
    },
    "references": {
        "behavioral_guide": "",
        "blog_links": [],
        "similar_questions": []
    }
}

# Low Level Design Schema (53 questions)
LLD_SCHEMA = {
    "question_number": 0,
    "title": "",
    "category": "LLD",
    "complexity": "",  # Simple/Medium/Complex
    "answer": {
        "problem_understanding": "",
        "requirements_gathering": [],
        "design_patterns_used": [],
        "class_diagram": {
            "classes": [],
            "relationships": [],
            "interfaces": []
        },
        "implementation_approach": {
            "core_classes": [],
            "key_methods": [],
            "data_structures": []
        },
        "code_snippets": [],
        "extensibility": [],
        "trade_offs": [],
        "interview_tips": []
    },
    "references": {
        "design_patterns": "",
        "local_solution": "",
        "blog_links": [],
        "similar_designs": []
    }
}

# Programming Language Specific Schema (JAVA, Javascript, etc.)
PROGRAMMING_LANGUAGE_SCHEMA = {
    "question_number": 0,
    "title": "",
    "category": "",  # JAVA, Javascript, etc.
    "topic": "",  # OOP, Collections, Async, etc.
    "answer": {
        "concept_explanation": "",
        "syntax_examples": [],
        "use_cases": [],
        "best_practices": [],
        "common_pitfalls": [],
        "related_concepts": [],
        "interview_tips": []
    },
    "references": {
        "official_docs": "",
        "tutorial_links": [],
        "practice_problems": []
    }
}

# Database Questions Schema (11 questions)
DATABASE_SCHEMA = {
    "question_number": 0,
    "title": "",
    "category": "Databases",
    "db_type": "",  # SQL, NoSQL, etc.
    "answer": {
        "concept_explanation": "",
        "sql_examples": [],
        "optimization_techniques": [],
        "indexing_strategies": [],
        "normalization": "",
        "transaction_handling": "",
        "interview_tips": []
    },
    "references": {
        "sql_tutorial": "",
        "database_design": "",
        "blog_links": []
    }
}

# General/Other Questions Schema
GENERAL_SCHEMA = {
    "question_number": 0,
    "title": "",
    "category": "",
    "answer": {
        "direct_answer": "",
        "detailed_explanation": "",
        "examples": [],
        "key_points": [],
        "interview_tips": []
    },
    "references": {
        "documentation": "",
        "blog_links": [],
        "related_topics": []
    }
}

# Category mapping to schema
CATEGORY_SCHEMA_MAP = {
    "DSA": DSA_SCHEMA,
    "System Design": SYSTEM_DESIGN_SCHEMA,
    "Behavioral": BEHAVIORAL_SCHEMA,
    "LLD": LLD_SCHEMA,
    "JAVA": PROGRAMMING_LANGUAGE_SCHEMA,
    "Javascript": PROGRAMMING_LANGUAGE_SCHEMA,
    "React": PROGRAMMING_LANGUAGE_SCHEMA,
    "Frontend": PROGRAMMING_LANGUAGE_SCHEMA,
    "C++": PROGRAMMING_LANGUAGE_SCHEMA,
    "C": PROGRAMMING_LANGUAGE_SCHEMA,
    "Databases": DATABASE_SCHEMA,
    "Computer Networks": GENERAL_SCHEMA,
    "Multithreading": GENERAL_SCHEMA,
    "Testing": GENERAL_SCHEMA,
    "Aws": GENERAL_SCHEMA,
    "Other": GENERAL_SCHEMA
}

def get_schema_for_category(category: str):
    """Get the appropriate schema for a given category"""
    return CATEGORY_SCHEMA_MAP.get(category, GENERAL_SCHEMA)

def create_answer_template(question_data: dict):
    """Create an answer template based on question category"""
    category = question_data.get("category", "Other")
    schema = get_schema_for_category(category)
    
    # Deep copy the schema and fill in known values
    import copy
    answer_template = copy.deepcopy(schema)
    answer_template["question_number"] = question_data.get("question_number", 0)
    answer_template["title"] = question_data.get("title", "")
    answer_template["category"] = category
    
    # Add difficulty if available
    if "difficulty" in question_data:
        if "difficulty" in answer_template:
            answer_template["difficulty"] = question_data["difficulty"]
    
    return answer_template

if __name__ == "__main__":
    # Test the schema creation
    test_question = {
        "question_number": 7,
        "title": "7. Rotate Array by K Steps",
        "category": "DSA",
        "difficulty": "Easy"
    }
    
    template = create_answer_template(test_question)
    print("DSA Template created:")
    print(f"Question: {template['title']}")
    print(f"Category: {template['category']}")
    print(f"Schema keys: {list(template['answer'].keys())}")