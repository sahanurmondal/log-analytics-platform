#!/bin/bash
# Add TreeNode import to files in subdirectories

for dir in trees/medium trees/easy; do
    for file in $(find $dir -name "*.java" -exec grep -l "TreeNode" {} \;); do
        # Check if import already exists
        if ! grep -q "import trees.TreeNode;" "$file"; then
            echo "Adding import to $file"
            # Add import after package declaration
            sed -i '' '/^package /a\
import trees.TreeNode;
' "$file"
        fi
    done
done
