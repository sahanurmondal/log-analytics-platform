#!/bin/bash

# Fix package declarations for all DP files

# Linear DP
find ./linear/basic -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.linear.basic;/g' {} \;
find ./linear/optimization -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.linear.optimization;/g' {} \;
find ./linear/sequence -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.linear.sequence;/g' {} \;

# Grid DP
find ./grid/path_counting -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.grid.path_counting;/g' {} \;
find ./grid/optimization -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.grid.optimization;/g' {} \;

# String DP
find ./string/subsequence -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.string.subsequence;/g' {} \;
find ./string/matching -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.string.matching;/g' {} \;
find ./string/palindrome -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.string.palindrome;/g' {} \;

# Knapsack DP
find ./knapsack/subset_sum -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.knapsack.subset_sum;/g' {} \;
find ./knapsack/unbounded -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.knapsack.unbounded;/g' {} \;
find ./knapsack -maxdepth 1 -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.knapsack;/g' {} \;

# Other patterns
find ./game_theory -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.game_theory;/g' {} \;
find ./stock_trading -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.stock_trading;/g' {} \;
find ./interval -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.interval;/g' {} \;
find ./state_machine -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.state_machine;/g' {} \;
find ./mathematical -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.mathematical;/g' {} \;
find ./advanced -name "*.java" -exec perl -i -pe 's/package dp\.(easy|medium|hard);/package dp.advanced;/g' {} \;

echo "Package declarations fixed!"
