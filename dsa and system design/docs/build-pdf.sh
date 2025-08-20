#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

INPUT="system-design-roadmap.md"
OUTPUT="system-design-roadmap.pdf"

if command -v pandoc >/dev/null 2>&1; then
  # Try with xelatex if pdflatex is missing
  if command -v xelatex >/dev/null 2>&1; then
    pandoc "$INPUT" -o "$OUTPUT" \
      -V geometry:margin=1in \
      -V mainfont="DejaVu Sans" \
      --from gfm \
      --pdf-engine=xelatex
    echo "Generated: $OUTPUT"
  else
    echo "Neither pdflatex nor xelatex found."
    echo "Install: macOS 'brew install --cask mactex-no-gui' or Ubuntu 'sudo apt-get install texlive-xetex'"
    echo "Or install pdflatex: Ubuntu 'sudo apt-get install texlive-latex-base'"
    echo "Then run: bash docs/build-pdf.sh"
    exit 1
  fi
else
  echo "pandoc not found."
  echo "Install: macOS 'brew install pandoc' or Ubuntu 'sudo apt-get install pandoc'"
  echo "Then run: bash docs/build-pdf.sh"
fi
