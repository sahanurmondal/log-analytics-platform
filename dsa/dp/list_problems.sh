#!/bin/bash

echo "# Actual Problems Found in DP Collection"
echo ""

echo "## Linear DP"
echo "### Basic ($(find ./linear/basic -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./linear/basic/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "### Optimization ($(find ./linear/optimization -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./linear/optimization/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "### Sequence ($(find ./linear/sequence -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./linear/sequence/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## Grid DP"
echo "### Path Counting ($(find ./grid/path_counting -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./grid/path_counting/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "### Optimization ($(find ./grid/optimization -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./grid/optimization/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## String DP"
echo "### Subsequence ($(find ./string/subsequence -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./string/subsequence/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "### Matching ($(find ./string/matching -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./string/matching/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "### Palindrome ($(find ./string/palindrome -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./string/palindrome/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## Knapsack DP"
echo "### Subset Sum ($(find ./knapsack/subset_sum -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./knapsack/subset_sum/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "### Unbounded ($(find ./knapsack/unbounded -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./knapsack/unbounded/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

knapsack_root=$(find ./knapsack -maxdepth 1 -name "*.java" | wc -l | tr -d ' ')
if [ "$knapsack_root" -gt 0 ]; then
    echo "### Other Knapsack ($knapsack_root problems)"
    for file in ./knapsack/*.java; do
        if [ -f "$file" ]; then
            problem_name=$(basename "$file" .java)
            echo "- $problem_name"
        fi
    done
    echo ""
fi

echo "## Game Theory DP ($(find ./game_theory -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./game_theory/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## Stock Trading DP ($(find ./stock_trading -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./stock_trading/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## Interval DP ($(find ./interval -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./interval/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## State Machine DP ($(find ./state_machine -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./state_machine/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## Mathematical DP ($(find ./mathematical -name "*.java" | wc -l | tr -d ' ') problems)"
for file in ./mathematical/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
echo ""

echo "## Advanced Patterns ($(find ./advanced -name "*.java" | wc -l | tr -d ' ') problems)"
echo "Complex DP problems combining multiple patterns:"
for file in ./advanced/*.java; do
    if [ -f "$file" ]; then
        problem_name=$(basename "$file" .java)
        echo "- $problem_name"
    fi
done
