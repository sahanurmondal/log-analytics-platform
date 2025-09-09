#!/bin/bash
# Script to remove duplicate TreeNode class definitions

for file in $(find trees -name "*.java" -exec grep -l "^class TreeNode" {} \;); do
    echo "Fixing $file"
    # Create a temporary file
    temp_file=$(mktemp)
    
    # Process the file to remove the TreeNode class definition
    awk '
    /^class TreeNode \{/ {
        in_treenode = 1
        next
    }
    in_treenode && /^\}$/ {
        in_treenode = 0
        next
    }
    !in_treenode {
        print
    }
    ' "$file" > "$temp_file"
    
    # Replace the original file
    mv "$temp_file" "$file"
done
