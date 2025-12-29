#!/bin/bash

set -e

SKILL_NAME="french-property-investment"
SKILL_DIR="$HOME/.claude/skills/$SKILL_NAME"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=========================================="
echo "Installing French Property Investment Skill"
echo "=========================================="
echo ""

echo "Step 1: Checking Python installation..."
if ! command -v python3 &> /dev/null; then
    echo "Error: Python 3 is not installed. Please install Python 3.8 or later."
    exit 1
fi
PYTHON_VERSION=$(python3 --version | awk '{print $2}')
echo "✓ Python $PYTHON_VERSION found"
echo ""

echo "Step 2: Creating skill directory..."
mkdir -p "$SKILL_DIR"
echo "✓ Directory created at $SKILL_DIR"
echo ""

echo "Step 3: Copying skill files..."
cp "$SCRIPT_DIR/$SKILL_NAME/french_mortgage.py" "$SKILL_DIR/"
cp "$SCRIPT_DIR/$SKILL_NAME/skill.json" "$SKILL_DIR/"
cp "$SCRIPT_DIR/$SKILL_NAME/README.md" "$SKILL_DIR/"
echo "✓ Files copied"
echo ""

echo "Step 4: Setting permissions..."
chmod +x "$SKILL_DIR/french_mortgage.py"
echo "✓ Permissions set"
echo ""

echo "Step 5: Testing skill..."
TEST_INPUT='{"propertyPrice":300000,"downPayment":60000,"interestRate":3.5,"loanTermYears":20,"monthlyRent":1500,"holdingPeriodYears":10}'
if echo "$TEST_INPUT" | python3 "$SKILL_DIR/french_mortgage.py" --json-input > /dev/null 2>&1; then
    echo "✓ Skill test passed"
else
    echo "⚠ Warning: Skill test failed, but installation completed"
fi
echo ""

echo "=========================================="
echo "Installation Complete! 🎉"
echo "=========================================="
echo ""
echo "The French Property Investment skill has been installed to:"
echo "  $SKILL_DIR"
echo ""
echo "You can now use it with Claude Code by asking questions like:"
echo "  'Analyze a 300k EUR property in France with 60k down payment...'"
echo ""
echo "For manual testing, try:"
echo "  cd $SKILL_DIR"
echo "  python3 french_mortgage.py --help"
echo ""
