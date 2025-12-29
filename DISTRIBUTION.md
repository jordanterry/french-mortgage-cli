# Distribution Guide

This guide explains how to package and distribute the French Property Investment Analyzer.

## Prerequisites

- GitHub account
- GitHub CLI (`gh`) installed
- Python 3.8 or later

## Distribution Methods

### Method 1: Homebrew Tap (Recommended for macOS)

#### Initial Setup

1. **Create a new GitHub repository** for the tool:
   ```bash
   # Copy this directory to a new repo
   git init
   git add .
   git commit -m "Initial commit"
   gh repo create french-property-investment --public --source=. --remote=origin --push
   ```

2. **Create Homebrew tap repository**:
   ```bash
   gh repo create homebrew-tap --public
   git clone https://github.com/yourusername/homebrew-tap
   cd homebrew-tap
   cp /path/to/Formula/french-property-investment.rb Formula/
   git add Formula/
   git commit -m "Add french-property-investment formula"
   git push
   ```

#### Automated Release Process

Use the included release script for a fully automated release:

```bash
./scripts/release.sh 2.0.0
```

This script automatically:
1. Updates version in `version.properties`
2. Runs Python tests
3. Commits changes
4. Creates and pushes git tag
5. Creates GitHub release
6. Downloads release tarball and calculates SHA256
7. Updates Homebrew formula with correct SHA256
8. Updates Homebrew tap repository

#### Manual Release Process

If you prefer manual control:

1. **Run tests**:
   ```bash
   python3 python/tests/test_french_mortgage.py
   ```

2. **Update version** in `version.properties`:
   ```
   version=2.0.0
   ```

3. **Create GitHub release**:
   ```bash
   git add version.properties
   git commit -m "Release v2.0.0"
   git tag v2.0.0
   git push origin main v2.0.0

   gh release create v2.0.0 --title "v2.0.0" --notes "Python release"
   ```

4. **Calculate SHA256** of tarball:
   ```bash
   curl -sL https://github.com/jordanterry/french-mortgage-cli/archive/refs/tags/v2.0.0.tar.gz | shasum -a 256
   ```

5. **Update Homebrew formula** with version and SHA256 in `Formula/french-property-investment.rb`

6. **Update Homebrew tap**:
   ```bash
   cd ../homebrew-tap
   cp ../french-mortgage-cli/Formula/french-property-investment.rb Formula/
   git add Formula/
   git commit -m "Update french-property-investment to v2.0.0"
   git push
   ```

#### User Installation

Users can then install via:
```bash
brew tap jordanterry/tap
brew install french-property-investment
```

---

### Method 2: Claude Code Skill

Distribute as a Claude Code skill for conversational property analysis:

1. **Users clone/download the repository**:
   ```bash
   git clone https://github.com/jordanterry/french-mortgage-cli
   cd french-mortgage-cli
   ```

2. **Run installation script**:
   ```bash
   ./skills/install.sh
   ```

This installs the skill to `~/.claude/skills/french-property-investment/` allowing users to analyze properties conversationally with Claude.

Alternatively, users can manually download just the skill:
```bash
# Download skill files
mkdir -p ~/.claude/skills/french-property-investment
curl -o ~/.claude/skills/french-property-investment/french_mortgage.py \
  https://raw.githubusercontent.com/jordanterry/french-mortgage-cli/main/skills/french-property-investment/french_mortgage.py
curl -o ~/.claude/skills/french-property-investment/skill.json \
  https://raw.githubusercontent.com/jordanterry/french-mortgage-cli/main/skills/french-property-investment/skill.json
```

---

### Method 3: Direct Python Script

Users can download and run the Python script directly:

```bash
# Download the script
curl -o french_mortgage.py \
  https://raw.githubusercontent.com/jordanterry/french-mortgage-cli/main/python/src/french_mortgage.py

# Run it
python3 french_mortgage.py --help
```

---

## Version Updates

### Semantic Versioning

Follow [semver](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: New features, backwards compatible
- **PATCH**: Bug fixes

### Update Checklist

- [ ] Update version in `version.properties`
- [ ] Run tests: `python3 python/tests/test_french_mortgage.py`
- [ ] Run automated release: `./scripts/release.sh X.Y.Z` OR
- [ ] Manual: Commit, tag, and update formula manually (see above)
- [ ] Create GitHub release
- [ ] Update Homebrew tap
- [ ] Announce release

---

## Testing the Distribution

### Test Homebrew Formula Locally

```bash
# Install from local formula
brew install --build-from-source Formula/french-property-investment.rb

# Test
french-property-investment --help

# Uninstall
brew uninstall french-property-investment
```

### Test Install Script

```bash
# Test locally
./scripts/install.sh 1.0.0

# Verify
~/.local/bin/french-property-investment --help
```

---

## Troubleshooting

### Formula SHA256 Mismatch

If users get SHA256 errors:
1. Download the JAR from the release
2. Calculate SHA256: `shasum -a 256 french-property-investment.jar`
3. Update formula with correct hash
4. Commit and push to tap repo

### Java Version Issues

The tool requires Java 11+. Users without it:

**Homebrew formula handles this** by declaring `depends_on "openjdk@11"`

**For manual installs**, users need:
```bash
brew install openjdk@11
```

### Binary Not in PATH

For manual installs, users may need to add to PATH:
```bash
echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

---

## Alternative: Native Binary (Advanced)

For faster startup and no Java requirement, compile to native binary with GraalVM:

1. **Add GraalVM Native plugin** to `build.gradle.kts`
2. **Build**: `./gradlew nativeCompile`
3. **Distribute** platform-specific binaries

See [GraalVM Native Image docs](https://www.graalvm.org/latest/reference-manual/native-image/) for details.

---

## Questions?

- File issues: https://github.com/yourusername/french-property-investment/issues
- Discussions: https://github.com/yourusername/french-property-investment/discussions
