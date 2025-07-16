#!/bin/bash
# Fix common Launch4j issues
# This script ensures Launch4j scripts have proper line endings for Unix systems

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Fixing Launch4j line endings..."

# Fix line endings for Launch4j scripts
if [ -f "$PROJECT_DIR/launch4j/launch4j" ]; then
    sed -i 's/\r$//' "$PROJECT_DIR/launch4j/launch4j"
    chmod +x "$PROJECT_DIR/launch4j/launch4j"
    echo "Fixed launch4j script"
fi

if [ -f "$PROJECT_DIR/launch4j/launch4jc" ]; then
    sed -i 's/\r$//' "$PROJECT_DIR/launch4j/launch4jc"
    chmod +x "$PROJECT_DIR/launch4j/launch4jc"
    echo "Fixed launch4jc script"
fi

echo "Launch4j fix completed"
