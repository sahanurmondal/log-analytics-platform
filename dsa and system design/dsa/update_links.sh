#!/bin/bash

# Script to update links in PACKAGE_WISE_MUST_DO_MAPPING.md

FILE="PACKAGE_WISE_MUST_DO_MAPPING.md"

# Create backup
cp "$FILE" "${FILE}.backup"

# Function to update links for a specific package
update_package_links() {
    local package=$1
    echo "Updating links for $package package..."
    
    # Update file paths and leetcode links using sed
    # Pattern: | Priority | Problem | `file.java` | number | 
    # Replace with: | Priority | Problem | [`file.java`](../package/file.java) | [number](https://leetcode.com/problems/slug/) |
    
    # First, let's update the file paths
    sed -i.tmp "s|\`\([^/]*\)/\([^.]*\)\.java\`|\[\`\1/\2.java\`\](../$package/\1/\2.java)|g" "$FILE"
    
    # Then update leetcode numbers to links (this is more complex, we'll do specific ones)
}

# Start with arrays package
echo "Starting link updates..."

# Let's do specific replacements for arrays section
sed -i.tmp 's/| 1 | Easy |/| [1](https:\/\/leetcode.com\/problems\/two-sum\/) | Easy |/g' "$FILE"
sed -i.tmp 's/| 53 | Easy |/| [53](https:\/\/leetcode.com\/problems\/maximum-subarray\/) | Easy |/g' "$FILE"
sed -i.tmp 's/| 121 | Easy |/| [121](https:\/\/leetcode.com\/problems\/best-time-to-buy-and-sell-stock\/) | Easy |/g' "$FILE"

echo "Link updates completed for sample entries"
echo "Original file backed up as ${FILE}.backup"
