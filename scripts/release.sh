#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

VERSION_FILE="version.properties"
FORMULA_FILE="Formula/french-property-investment.rb"
PYTHON_SCRIPT="python/src/french_mortgage.py"

usage() {
    cat <<EOF
Usage: $0 <new-version>

Automated release script for french-property-investment

Arguments:
  new-version    Semantic version (e.g., 2.0.0, 2.1.0, 3.0.0)

Steps performed:
  1. Update version in version.properties
  2. Run Python tests
  3. Calculate SHA256 of release tarball
  4. Update Homebrew formula
  5. Commit changes
  6. Create and push git tag
  7. Create GitHub release
  8. Update Homebrew tap repository

Example:
  $0 2.0.0

EOF
    exit 1
}

if [ $# -ne 1 ]; then
    usage
fi

NEW_VERSION="$1"

if ! [[ "$NEW_VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Error: Version must be in semver format (e.g., 1.0.1)"
    exit 1
fi

echo "=========================================="
echo "Release Process for v${NEW_VERSION}"
echo "=========================================="
echo ""

echo "Step 1: Checking working directory is clean..."
if ! git diff-index --quiet HEAD --; then
    echo "Error: Working directory has uncommitted changes. Please commit or stash them first."
    exit 1
fi
echo "✓ Working directory is clean"
echo ""

echo "Step 2: Updating version in ${VERSION_FILE}..."
echo "version=${NEW_VERSION}" > "$VERSION_FILE"
echo "✓ Version updated to ${NEW_VERSION}"
echo ""

echo "Step 3: Running Python tests..."
if [ ! -f "$PYTHON_SCRIPT" ]; then
    echo "Error: Python script not found at $PYTHON_SCRIPT"
    exit 1
fi
python3 python/tests/test_french_mortgage.py
echo "✓ Tests passed"
echo ""

echo "Step 4: Calculating SHA256 of GitHub tarball..."
TARBALL_URL="https://github.com/jordanterry/french-mortgage-cli/archive/refs/tags/v${NEW_VERSION}.tar.gz"
echo "Note: SHA256 will need to be calculated after GitHub release is created"
echo "The formula will be updated in step 10 after the release exists"
echo ""

echo "Step 5: Updating Homebrew formula version..."
sed -i '' "s/version \".*\"/version \"${NEW_VERSION}\"/" "$FORMULA_FILE"
sed -i '' "s|archive/refs/tags/v.*.tar.gz|archive/refs/tags/v${NEW_VERSION}.tar.gz|" "$FORMULA_FILE"
echo "✓ Formula version updated (SHA256 will be updated after release)"
echo ""

echo "Step 6: Committing changes..."
git add "$VERSION_FILE" "$FORMULA_FILE"
git commit -m "Release v${NEW_VERSION}

- Update version to ${NEW_VERSION}
- Update Homebrew formula with new SHA256

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>"
echo "✓ Changes committed"
echo ""

echo "Step 7: Creating git tag..."
git tag -a "v${NEW_VERSION}" -m "Release v${NEW_VERSION}

See release notes at https://github.com/jordanterry/french-mortgage-cli/releases/tag/v${NEW_VERSION}

🤖 Generated with [Claude Code](https://claude.com/claude-code)"
echo "✓ Tag created: v${NEW_VERSION}"
echo ""

echo "Step 8: Pushing to GitHub..."
git push origin main
git push origin "v${NEW_VERSION}"
echo "✓ Pushed to GitHub"
echo ""

echo "Step 9: Creating GitHub release..."
gh release create "v${NEW_VERSION}" \
    --title "v${NEW_VERSION}" \
    --notes "## Release v${NEW_VERSION}

Python-based French Property Investment Calculator

### Installation

**Homebrew (recommended):**
\`\`\`bash
brew tap jordanterry/tap
brew install french-property-investment
\`\`\`

Or upgrade:
\`\`\`bash
brew update
brew upgrade french-property-investment
\`\`\`

**Manual:**
\`\`\`bash
wget https://github.com/jordanterry/french-mortgage-cli/archive/refs/tags/v${NEW_VERSION}.tar.gz
tar -xzf v${NEW_VERSION}.tar.gz
cd french-mortgage-cli-${NEW_VERSION}
python3 python/src/french_mortgage.py --help
\`\`\`

🤖 Generated with [Claude Code](https://claude.com/claude-code)"
echo "✓ GitHub release created"
echo ""

echo "Step 10: Calculating SHA256 of release tarball..."
sleep 2
TEMP_TAR=$(mktemp)
curl -sL "$TARBALL_URL" -o "$TEMP_TAR"
SHA256=$(shasum -a 256 "$TEMP_TAR" | awk '{print $1}')
rm "$TEMP_TAR"
echo "✓ SHA256: $SHA256"
echo ""

echo "Step 11: Updating formula with SHA256..."
sed -i '' "s/sha256 \".*\"/sha256 \"${SHA256}\"/" "$FORMULA_FILE"
git add "$FORMULA_FILE"
git commit --amend --no-edit
git push -f origin main
git push -f origin "v${NEW_VERSION}"
echo "✓ Formula updated with SHA256"
echo ""

echo "Step 12: Updating Homebrew tap..."
TAP_DIR="../homebrew-tap"
if [ ! -d "$TAP_DIR" ]; then
    echo "Warning: Homebrew tap directory not found at $TAP_DIR"
    echo "Please update manually:"
    echo "  cd ../homebrew-tap"
    echo "  cp ../french-mortgage-cli/Formula/french-property-investment.rb Formula/"
    echo "  git add Formula/"
    echo "  git commit -m 'Update french-property-investment to v${NEW_VERSION}'"
    echo "  git push"
else
    cd "$TAP_DIR"
    cp "$PROJECT_DIR/$FORMULA_FILE" Formula/
    git add Formula/
    git commit -m "Update french-property-investment to v${NEW_VERSION}

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>"
    git push
    cd "$PROJECT_DIR"
    echo "✓ Homebrew tap updated"
fi
echo ""

echo "=========================================="
echo "Release v${NEW_VERSION} Complete! 🎉"
echo "=========================================="
echo ""
echo "Release URL: https://github.com/jordanterry/french-mortgage-cli/releases/tag/v${NEW_VERSION}"
echo ""
echo "Users can now install with:"
echo "  brew update && brew upgrade french-property-investment"
echo ""
