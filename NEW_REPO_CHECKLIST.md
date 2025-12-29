# New Repository Checklist

Follow these steps to extract the French Property Investment Analyzer into its own repository.

## Preparation (in current repo)

- [x] Build and test the tool: `../../backend/gradlew build`
- [x] All files created for distribution
- [ ] Run preparation script: `./scripts/prepare-standalone-repo.sh`
- [ ] Review all generated files

## Copy to New Location

```bash
# Create new directory
mkdir -p ~/french-property-investment
cd ~/french-property-investment

# Copy all files (from rentamap-site/cli-tools/french-property-investment/)
cp -r /path/to/rentamap-site/cli-tools/french-property-investment/* .

# Don't copy build artifacts
rm -rf build/ .gradle/
```

## Update Configuration Files

### 1. Update URLs in Formula/french-property-investment.rb

Replace `yourusername` with your GitHub username:

```ruby
homepage "https://github.com/YOURUSERNAME/french-property-investment"
url "https://github.com/YOURUSERNAME/french-property-investment/releases/download/v1.0.0/french-property-investment.jar"
```

### 2. Update URLs in README.md

Search and replace all instances of:
- `yourusername` → your GitHub username
- Update badge URLs
- Update installation instructions

### 3. Update URLs in .github/workflows/release.yml

```yaml
# Update all GitHub URLs and username references
```

### 4. Update URLs in CLAUDE_SKILL.md

```bash
sed -i '' 's/yourusername/YOUR_ACTUAL_USERNAME/g' CLAUDE_SKILL.md
```

### 5. Update URLs in DISTRIBUTION.md

```bash
sed -i '' 's/yourusername/YOUR_ACTUAL_USERNAME/g' DISTRIBUTION.md
```

### 6. Update scripts/install.sh

```bash
sed -i '' 's/yourusername/YOUR_ACTUAL_USERNAME/g' scripts/install.sh
```

## Initialize Git Repository

```bash
# Initialize
git init

# Add all files
git add .

# Review what will be committed
git status

# Make initial commit
git commit -m "Initial commit: French Property Investment Analyzer v1.0.0

- Command-line tool for French rental property analysis
- Comprehensive financial modeling with French mortgage specifics
- JSON and table output formats
- Full test coverage with Kotest
- Ready for Homebrew distribution and Claude Code integration"
```

## Create GitHub Repository

### Option 1: Using GitHub CLI (recommended)

```bash
# Create public repository
gh repo create french-property-investment \
  --public \
  --description "Analyze French rental property investments with comprehensive cash flow projections" \
  --homepage "https://yourusername.github.io/french-property-investment" \
  --source=. \
  --push

# Add topics
gh repo edit --add-topic kotlin,cli,property-investment,france,mortgage,real-estate,financial-analysis
```

### Option 2: Using GitHub Web Interface

1. Go to https://github.com/new
2. Repository name: `french-property-investment`
3. Description: "Analyze French rental property investments with comprehensive cash flow projections"
4. Public
5. **Don't** initialize with README, .gitignore, or license (we have them)
6. Create repository
7. Push:
   ```bash
   git remote add origin git@github.com:YOURUSERNAME/french-property-investment.git
   git branch -M main
   git push -u origin main
   ```

## First Release

### 1. Build the JAR

```bash
./gradlew clean shadowJar
```

### 2. Calculate SHA256

```bash
shasum -a 256 build/libs/french-property-investment.jar
# Save this for the formula
```

### 3. Create and Push Tag

```bash
git tag -a v1.0.0 -m "Release v1.0.0

Initial release with:
- French mortgage calculations
- Year-by-year cash flow projections
- ROI and break-even analysis
- JSON and table output
- 13 passing tests
- Homebrew formula
- Claude Code skill integration"

git push origin v1.0.0
```

### 4. Create GitHub Release

```bash
gh release create v1.0.0 \
  build/libs/french-property-investment.jar \
  --title "v1.0.0 - Initial Release" \
  --notes "$(cat <<EOF
## French Property Investment Analyzer v1.0.0

First stable release! 🎉

### Features

- 🏠 French mortgage calculations with life insurance
- 📊 Year-by-year cash flow projections
- 💰 ROI and break-even analysis
- 📋 JSON and table output formats
- ✅ Comprehensive test coverage
- 🍺 Homebrew installation support
- 🤖 Claude Code skill integration

### Installation

**Homebrew:**
\`\`\`bash
brew tap YOURUSERNAME/tap
brew install french-property-investment
\`\`\`

**Manual:**
\`\`\`bash
wget https://github.com/YOURUSERNAME/french-property-investment/releases/download/v1.0.0/french-property-investment.jar
java -jar french-property-investment.jar --help
\`\`\`

### Quick Start

\`\`\`bash
french-property-investment \\
  --property-price 300000 \\
  --down-payment 60000 \\
  --interest-rate 3.5 \\
  --loan-term 20 \\
  --monthly-rent 1500 \\
  --holding-period 10
\`\`\`

See [README.md](README.md) for full documentation.
EOF
)"
```

Or if GitHub Actions is set up, it will auto-release when you push the tag!

### 5. Update Formula with Real SHA256

```bash
# Get the SHA256 from the release
SHA256=$(shasum -a 256 build/libs/french-property-investment.jar | awk '{print $1}')

# Update formula
sed -i '' "s/PUT_SHA256_HERE/${SHA256}/" Formula/french-property-investment.rb

# Commit updated formula
git add Formula/french-property-investment.rb
git commit -m "Update formula with v1.0.0 SHA256"
git push
```

## Create Homebrew Tap

### 1. Create Tap Repository

```bash
gh repo create homebrew-tap \
  --public \
  --description "Homebrew tap for French Property Investment Analyzer and other tools"
```

### 2. Clone and Setup

```bash
cd ..
git clone git@github.com:YOURUSERNAME/homebrew-tap.git
cd homebrew-tap

mkdir Formula
cp ../french-property-investment/Formula/french-property-investment.rb Formula/

git add Formula/
git commit -m "Add french-property-investment v1.0.0"
git push
```

### 3. Test Installation

```bash
# Tap the repository
brew tap YOURUSERNAME/tap

# Install
brew install french-property-investment

# Test
french-property-investment --help

# Verify version
french-property-investment --property-price 300000 \
  --down-payment 60000 \
  --interest-rate 3.5 \
  --loan-term 20 \
  --monthly-rent 1500 \
  --holding-period 1 \
  --format table
```

## Claude Code Skill Setup

### 1. Create Skill Directory

```bash
mkdir -p ~/.claude/skills/french-property-investment
```

### 2. Create skill.json

```bash
cat > ~/.claude/skills/french-property-investment/skill.json <<'EOF'
{
  "name": "french-property-investment",
  "version": "1.0.0",
  "description": "Analyze French rental property investments with cash flow projections and ROI",
  "executable": {
    "command": "french-property-investment",
    "args": ["--json-input"],
    "timeout": 10000
  },
  "input": "json-stdin",
  "output": "json"
}
EOF
```

### 3. Test with Claude

Ask Claude:
> "Analyze a 300k EUR property in France with 60k down payment, 3.5% interest, 20-year loan, renting for 1500/month over 10 years"

Claude should invoke the skill and provide analysis!

## Documentation

### Update GitHub Repository Settings

1. Go to Settings → General
2. Features:
   - ✅ Issues
   - ✅ Discussions
   - ✅ Wiki (optional)
3. Social Preview: Add a nice preview image
4. Topics: Add relevant tags

### Create GitHub Pages (Optional)

```bash
# Enable Pages
gh repo edit --enable-pages --pages-branch main --pages-path /

# Or use docs/ folder
mkdir docs
# Add landing page to docs/index.html
```

### Add Badges to README

Already included in README_STANDALONE.md:
- Build status
- License
- Version (add after first release)

## Post-Release

- [ ] Announce on Twitter/LinkedIn
- [ ] Post on Reddit r/realestateinvesting
- [ ] Share in property investment communities
- [ ] Update your portfolio
- [ ] Write a blog post about the tool

## Maintenance

### Regular Updates

1. Fix bugs as reported
2. Add requested features
3. Update dependencies: `./gradlew dependencyUpdates`
4. Keep formula up to date

### Release Process

For future releases:

```bash
# Update version
# Build and test
./gradlew clean build

# Tag and release
./scripts/update-formula.sh X.Y.Z
git add Formula/
git commit -m "Bump version to vX.Y.Z"
git tag vX.Y.Z
git push && git push --tags

# GitHub Actions will create release automatically
# Or manually:
gh release create vX.Y.Z build/libs/french-property-investment.jar

# Update tap
cd ../homebrew-tap
cp ../french-property-investment/Formula/french-property-investment.rb Formula/
git add Formula/
git commit -m "Update french-property-investment to vX.Y.Z"
git push
```

## Troubleshooting

### Build Fails

```bash
# Clean build
./gradlew clean
rm -rf .gradle/ build/
./gradlew build
```

### Formula SHA256 Mismatch

```bash
# Recalculate
shasum -a 256 build/libs/french-property-investment.jar

# Update formula manually
```

### Can't Push to GitHub

```bash
# Check remote
git remote -v

# Set if needed
git remote set-url origin git@github.com:YOURUSERNAME/french-property-investment.git
```

## Success Criteria

- [x] Repository created on GitHub
- [ ] First release (v1.0.0) published
- [ ] JAR uploaded to release
- [ ] Homebrew tap created
- [ ] Formula published
- [ ] Successfully installed via brew
- [ ] Claude Code skill working
- [ ] README badges showing green
- [ ] All links in documentation working

## Resources

- [Homebrew Formula Cookbook](https://docs.brew.sh/Formula-Cookbook)
- [GitHub Releases](https://docs.github.com/en/repositories/releasing-projects-on-github)
- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)

---

**Questions?** See [DISTRIBUTION.md](DISTRIBUTION.md) or open an issue!
