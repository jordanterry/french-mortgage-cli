#!/bin/bash

set -e

SKILL_NAME="french-property-investment"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_DIR="$SCRIPT_DIR"
ZIP_FILE="$OUTPUT_DIR/${SKILL_NAME}-skill.zip"

echo "=========================================="
echo "Packaging French Property Investment Skill"
echo "=========================================="
echo ""

echo "Step 1: Cleaning up old zip file..."
if [ -f "$ZIP_FILE" ]; then
    rm "$ZIP_FILE"
    echo "✓ Removed old zip file"
else
    echo "✓ No old zip file to remove"
fi
echo ""

echo "Step 2: Creating zip archive..."
cd "$SCRIPT_DIR/$SKILL_NAME"

zip -r "$ZIP_FILE" \
    french_mortgage.py \
    skill.json \
    README.md

cd "$SCRIPT_DIR"
echo "✓ Zip archive created"
echo ""

echo "Step 3: Verifying archive contents..."
unzip -l "$ZIP_FILE"
echo ""

echo "Step 4: Calculating file size..."
FILE_SIZE=$(du -h "$ZIP_FILE" | awk '{print $1}')
echo "✓ File size: $FILE_SIZE"
echo ""

echo "=========================================="
echo "Packaging Complete! 🎉"
echo "=========================================="
echo ""
echo "Skill package created at:"
echo "  $ZIP_FILE"
echo ""
echo "To install in Claude Code:"
echo "  1. Extract the zip to ~/.claude/skills/$SKILL_NAME/"
echo "  2. Or use: unzip -d ~/.claude/skills/$SKILL_NAME $ZIP_FILE"
echo ""
echo "For testing, you can also run:"
echo "  ./install.sh"
echo ""
