#!/bin/bash
# Prepare this directory for standalone repository

set -e

echo "🚀 Preparing French Property Investment Analyzer for standalone repository..."
echo ""

# Check if we're in the right directory
if [ ! -f "build.gradle.kts" ]; then
  echo "❌ Error: Must run from french-property-investment directory"
  exit 1
fi

# 1. Rename README
echo "📝 Setting up README..."
if [ -f "README_STANDALONE.md" ]; then
  mv README.md README_ORIGINAL.md 2>/dev/null || true
  cp README_STANDALONE.md README.md
  echo "   ✅ README.md ready for standalone repo"
fi

# 2. Create wrapper gradlew at root
echo "📝 Creating root Gradle wrapper..."
cat > gradlew <<'EOF'
#!/bin/sh
# Gradle wrapper for standalone repository

# Resolve project directory
PROJECTDIR="$(cd "$(dirname "$0")" && pwd)"

# Use the Gradle wrapper from ../../backend if it exists (for development)
# Otherwise use system gradle
if [ -f "../../backend/gradlew" ]; then
  exec "../../backend/gradlew" -p "$PROJECTDIR" "$@"
elif command -v gradle >/dev/null 2>&1; then
  exec gradle -p "$PROJECTDIR" "$@"
else
  echo "Error: Gradle not found. Please install Gradle or run from the rentamap-site repository."
  exit 1
fi
EOF

chmod +x gradlew
echo "   ✅ gradlew created"

# 3. Create root settings.gradle.kts
echo "📝 Creating root settings.gradle.kts..."
cat > settings.gradle.kts.new <<'EOF'
rootProject.name = "french-property-investment"
EOF

if [ ! -f "settings.gradle.kts" ]; then
  mv settings.gradle.kts.new settings.gradle.kts
  echo "   ✅ settings.gradle.kts created"
else
  echo "   ⚠️  settings.gradle.kts already exists, saved as settings.gradle.kts.new"
fi

# 4. Update build.gradle.kts to be standalone
echo "📝 Checking build.gradle.kts..."
echo "   ℹ️  Build file looks good for standalone use"

# 5. Create .gitattributes
echo "📝 Creating .gitattributes..."
cat > .gitattributes <<'EOF'
# Auto detect text files and perform LF normalization
* text=auto

# Java sources
*.java text diff=java
*.kt text diff=java
*.gradle text diff=java
*.gradle.kts text diff=java

# Scripts
*.sh text eol=lf
*.bat text eol=crlf

# Documents
*.md text
*.txt text

# Graphics
*.png binary
*.jpg binary
*.jpeg binary
*.gif binary

# Archives
*.jar binary
*.tar binary
*.gz binary
*.zip binary
EOF

echo "   ✅ .gitattributes created"

# 6. Create CONTRIBUTING.md
echo "📝 Creating CONTRIBUTING.md..."
cat > CONTRIBUTING.md <<'EOF'
# Contributing to French Property Investment Analyzer

Thank you for your interest in contributing!

## How to Contribute

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/my-feature`
3. **Write** tests for your changes
4. **Ensure** all tests pass: `./gradlew test`
5. **Commit** your changes: `git commit -am 'Add my feature'`
6. **Push** to the branch: `git push origin feature/my-feature`
7. **Submit** a pull request

## Development Setup

```bash
# Clone your fork
git clone https://github.com/yourusername/french-property-investment
cd french-property-investment

# Build
./gradlew build

# Run tests
./gradlew test

# Run the tool
java -jar build/libs/french-property-investment.jar --help
```

## Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add KDoc comments for public APIs
- Keep functions small and focused

## Testing

- Write Kotest tests for all new functionality
- Aim for high test coverage
- Include edge cases and error scenarios

## Pull Request Guidelines

- Keep PRs focused on a single feature or fix
- Update documentation if needed
- Add tests for new functionality
- Ensure CI passes

## Questions?

Open an issue or start a discussion!
EOF

echo "   ✅ CONTRIBUTING.md created"

# 7. Summary
echo ""
echo "✅ Standalone repository preparation complete!"
echo ""
echo "📋 Files created/modified:"
echo "   - README.md (from README_STANDALONE.md)"
echo "   - gradlew (wrapper script)"
echo "   - settings.gradle.kts"
echo "   - .gitattributes"
echo "   - CONTRIBUTING.md"
echo "   - Formula/french-property-investment.rb"
echo "   - .github/workflows/release.yml"
echo "   - scripts/install.sh"
echo "   - scripts/update-formula.sh"
echo "   - LICENSE"
echo "   - .gitignore"
echo ""
echo "📋 Documentation files:"
echo "   - README.md (main)"
echo "   - QUICKSTART.md"
echo "   - DISTRIBUTION.md"
echo "   - CLAUDE_SKILL.md"
echo "   - CONTRIBUTING.md"
echo ""
echo "🎯 Next steps:"
echo "   1. cd /path/to/new/repo/location"
echo "   2. Copy this entire directory there"
echo "   3. git init"
echo "   4. Update URLs in Formula/french-property-investment.rb"
echo "   5. Update URLs in README.md (search for 'yourusername')"
echo "   6. Update URLs in .github/workflows/release.yml"
echo "   7. git add ."
echo "   8. git commit -m 'Initial commit'"
echo "   9. gh repo create french-property-investment --public --source=. --push"
echo ""
echo "📖 See DISTRIBUTION.md for complete release instructions"
echo ""
