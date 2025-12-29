#!/bin/bash
# Update Homebrew formula with new version and SHA256

set -e

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi

VERSION="$1"
JAR_PATH="build/libs/french-property-investment.jar"
FORMULA_PATH="Formula/french-property-investment.rb"

if [ ! -f "$JAR_PATH" ]; then
  echo "Error: JAR not found at $JAR_PATH"
  echo "Run './gradlew shadowJar' first"
  exit 1
fi

# Calculate SHA256
SHA256=$(shasum -a 256 "$JAR_PATH" | awk '{print $1}')

echo "Version: $VERSION"
echo "SHA256:  $SHA256"
echo ""

# Update formula
sed -i.bak \
  -e "s|url \".*\"|url \"https://github.com/yourusername/french-property-investment/releases/download/v${VERSION}/french-property-investment.jar\"|" \
  -e "s|sha256 \".*\"|sha256 \"${SHA256}\"|" \
  -e "s|version \".*\"|version \"${VERSION}\"|" \
  "$FORMULA_PATH"

rm "${FORMULA_PATH}.bak"

echo "✅ Formula updated!"
echo ""
echo "Next steps:"
echo "1. Review the changes in $FORMULA_PATH"
echo "2. Commit and push"
echo "3. Create release: gh release create v${VERSION} build/libs/french-property-investment.jar"
echo ""
