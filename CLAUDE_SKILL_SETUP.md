# Claude Code Skill Setup

This guide shows how to set up the French Property Investment Analyzer as a Claude Code skill with automatic installation.

## Quick Setup

Run this command to set up the skill:

```bash
mkdir -p ~/.claude/skills/french-property-investment
cd ~/.claude/skills/french-property-investment

# Download skill files
curl -o skill.json https://raw.githubusercontent.com/jordanterry/french-mortgage-cli/main/.claude/skills/french-property-investment/skill.json
curl -o install.sh https://raw.githubusercontent.com/jordanterry/french-mortgage-cli/main/.claude/skills/french-property-investment/install.sh
chmod +x install.sh
```

## What Gets Installed

The skill includes:

1. **skill.json** - Claude skill configuration with:
   - Automatic setup script reference
   - Input/output schema
   - Example usage patterns

2. **install.sh** - Auto-installation script that:
   - Checks if `french-property-investment` is installed
   - If not, taps `jordanterry/tap` via Homebrew
   - Installs the tool automatically
   - Requires Homebrew to be installed

3. **README.md** - Documentation for the skill

## Manual Setup (Alternative)

If you prefer to set up manually:

### 1. Install the tool via Homebrew

```bash
brew tap jordanterry/tap
brew install french-property-investment
```

### 2. Create skill directory

```bash
mkdir -p ~/.claude/skills/french-property-investment
```

### 3. Create skill.json

Create `~/.claude/skills/french-property-investment/skill.json`:

```json
{
  "name": "french-property-investment",
  "version": "1.0.0",
  "description": "Analyze French rental property investments with comprehensive financial modeling",
  "executable": {
    "command": "french-property-investment",
    "args": ["--json-input"],
    "timeout": 10000
  },
  "input": {
    "type": "json-stdin",
    "required": [
      "propertyPrice",
      "downPayment",
      "interestRate",
      "loanTermYears",
      "monthlyRent",
      "holdingPeriodYears"
    ],
    "optional": [
      "propertyTaxAnnual",
      "hoaMonthly",
      "maintenancePercent",
      "managementFeePercent",
      "vacancyRate",
      "rentIncreaseAnnual"
    ]
  },
  "output": {
    "type": "json"
  }
}
```

## Usage with Claude

Once installed, ask Claude questions like:

**Basic Analysis:**
> "Analyze a 300k EUR property in France with 60k down payment, 3.5% interest rate, 20-year mortgage, renting for 1500/month over 10 years"

**Detailed Analysis:**
> "What's the ROI on a French property costing 250k EUR with 50k down, 3.5% interest, 15-year loan, renting for 1300/month with 2400/year property tax, 150/month HOA fees, 5% vacancy rate, and 2% annual rent increases over 10 years?"

**Property Comparison:**
> "Compare two French properties: Property A is 300k with 1500/month rent, Property B is 250k with 1300/month rent. Both with 20% down and 3.5% interest over 20 years."

Claude will automatically invoke the skill and provide detailed financial analysis including:
- Initial investment breakdown (down payment + French fees)
- Monthly mortgage payment (with life insurance)
- Year-by-year cash flow projections
- ROI, cash-on-cash return, and break-even analysis
- Equity build-up over time

## Verification

Test that the skill is working:

```bash
# Verify the tool is installed
french-property-investment --help

# Test JSON input (what Claude uses)
echo '{
  "propertyPrice": 300000,
  "downPayment": 60000,
  "interestRate": 3.5,
  "loanTermYears": 20,
  "monthlyRent": 1500,
  "holdingPeriodYears": 10
}' | french-property-investment --json-input
```

## Requirements

- **Homebrew** - Package manager for macOS/Linux
- **Java 17+** - Automatically installed as dependency via Homebrew
- **Claude Code** - Claude CLI tool

## Troubleshooting

### Skill not found
- Ensure the skill directory is at `~/.claude/skills/french-property-investment/`
- Check that `skill.json` is present and valid JSON

### Command not found
- Run the install script: `~/.claude/skills/french-property-investment/install.sh`
- Or manually install: `brew tap jordanterry/tap && brew install french-property-investment`

### Homebrew not installed
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### Java version issues
The tool requires Java 17+. Homebrew automatically installs `openjdk@17` as a dependency.

## Uninstalling

To remove the skill and tool:

```bash
# Remove skill
rm -rf ~/.claude/skills/french-property-investment

# Uninstall tool
brew uninstall french-property-investment
brew untap jordanterry/tap
```

## Links

- **Main Repository:** https://github.com/jordanterry/french-mortgage-cli
- **Homebrew Tap:** https://github.com/jordanterry/homebrew-tap
- **Releases:** https://github.com/jordanterry/french-mortgage-cli/releases

## License

MIT License - see repository for details
