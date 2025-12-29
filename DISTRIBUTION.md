# Distribution Guide

This guide explains how to package and distribute the French Property Investment Analyzer.

## Prerequisites

- GitHub account
- GitHub CLI (`gh`) installed
- Gradle wrapper (included)

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

#### Release Process

1. **Build the JAR**:
   ```bash
   ./gradlew clean shadowJar
   ```

2. **Update the formula** with version and SHA256:
   ```bash
   ./scripts/update-formula.sh 1.0.0
   ```

3. **Create GitHub release**:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0

   # Create release with JAR
   gh release create v1.0.0 \
     build/libs/french-property-investment.jar \
     --title "v1.0.0" \
     --notes "Initial release"
   ```

4. **Update Homebrew tap** with the new formula:
   ```bash
   cd ../homebrew-tap
   cp ../french-property-investment/Formula/french-property-investment.rb Formula/
   git add Formula/
   git commit -m "Update french-property-investment to v1.0.0"
   git push
   ```

#### User Installation

Users can then install via:
```bash
brew tap yourusername/tap
brew install french-property-investment
```

#### Automated Releases (Optional)

The included GitHub Actions workflow (`.github/workflows/release.yml`) automatically:
- Builds the JAR when you push a tag
- Creates a GitHub release
- Uploads the JAR
- Calculates SHA256

To use:
1. Push the workflow to your repo
2. Create and push a tag: `git tag v1.0.0 && git push origin v1.0.0`
3. GitHub Actions will handle the rest

---

### Method 2: Direct Download + Install Script

Users can install manually:

```bash
curl -fsSL https://raw.githubusercontent.com/yourusername/french-property-investment/main/scripts/install.sh | bash
```

Or with a specific version:
```bash
curl -fsSL https://raw.githubusercontent.com/yourusername/french-property-investment/main/scripts/install.sh | bash -s 1.0.0
```

---

### Method 3: GitHub Releases Only

Users download the JAR directly:

```bash
# Download latest release
gh release download --repo yourusername/french-property-investment --pattern "*.jar"

# Run directly
java -jar french-property-investment.jar --help
```

---

## Version Updates

### Semantic Versioning

Follow [semver](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: New features, backwards compatible
- **PATCH**: Bug fixes

### Update Checklist

- [ ] Update version in `build.gradle.kts` (if applicable)
- [ ] Run tests: `./gradlew test`
- [ ] Build JAR: `./gradlew shadowJar`
- [ ] Update formula: `./scripts/update-formula.sh X.Y.Z`
- [ ] Commit changes
- [ ] Create and push tag: `git tag vX.Y.Z && git push origin vX.Y.Z`
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
