import json

def extract_system_design_questions():
    """Extract all System Design questions from the complete dataset and create a separate JSON file"""
    
    # Load the complete questions dataset
    with open('complete_questions_dataset.json', 'r', encoding='utf-8') as f:
        all_questions = json.load(f)
    
    # Filter System Design questions
    system_design_questions = [
        question for question in all_questions 
        if question.get('category') == 'System Design'
    ]
    
    print(f"Found {len(system_design_questions)} System Design questions")
    
    # Create the formatted output with serial numbers starting from 1
    formatted_questions = []
    
    for index, question in enumerate(system_design_questions, 1):
        formatted_question = {
            "serial_no": index,
            "question_number": question.get('question_number'),
            "title": question.get('title', ''),
            "description": question.get('description', ''),
            "category": question.get('category', 'System Design'),
            "company": question.get('company', ''),
            "difficulty": question.get('difficulty', ''),
            "url": question.get('url', ''),
            "tags": question.get('tags', [])
        }
        formatted_questions.append(formatted_question)
    
    # Save to a new JSON file
    output_filename = 'system_design_questions.json'
    with open(output_filename, 'w', encoding='utf-8') as f:
        json.dump(formatted_questions, f, indent=2, ensure_ascii=False)
    
    print(f"Successfully extracted {len(formatted_questions)} System Design questions to {output_filename}")
    
    # Print some statistics
    companies = set(q['company'] for q in formatted_questions if q['company'])
    difficulties = set(q['difficulty'] for q in formatted_questions if q['difficulty'])
    
    print(f"\nStatistics:")
    print(f"- Total System Design Questions: {len(formatted_questions)}")
    print(f"- Companies represented: {len(companies)}")
    print(f"- Difficulty levels: {len(difficulties)}")
    print(f"- Companies: {sorted(companies)}")
    print(f"- Difficulties: {sorted(difficulties)}")
    
    # Show first few questions as preview
    print(f"\nFirst 3 System Design Questions:")
    for i in range(min(3, len(formatted_questions))):
        q = formatted_questions[i]
        print(f"{i+1}. Serial: {q['serial_no']}, Q#{q['question_number']}: {q['title']}")
        print(f"   Company: {q['company']}, Difficulty: {q['difficulty']}")
        if q['description']:
            description_preview = q['description'][:100] + "..." if len(q['description']) > 100 else q['description']
            print(f"   Description: {description_preview}")
        print()
    
    return formatted_questions

if __name__ == "__main__":
    extract_system_design_questions()