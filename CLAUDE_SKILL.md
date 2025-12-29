# Claude Skill Integration

This document explains how to integrate the French Property Investment Analyzer as a Claude Code skill.

## Prerequisites

1. **Install the tool** via Homebrew or manually
2. **Verify it works**: `french-property-investment --help`

## Skill Definition

Create the skill configuration file:

**`.claude/skills/french-property-investment/skill.json`**:
```json
{
  "name": "french-property-investment",
  "version": "1.0.0",
  "description": "Analyze French rental property investments with comprehensive financial modeling including cash flow projections, ROI calculations, and break-even analysis",
  "executable": {
    "command": "french-property-investment",
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
  },
  "examples": [
    {
      "description": "Basic property analysis",
      "input": {
        "propertyPrice": 300000,
        "downPayment": 60000,
        "interestRate": 3.5,
        "loanTermYears": 20,
        "monthlyRent": 1500,
        "holdingPeriodYears": 10
      }
    },
    {
      "description": "Full analysis with all parameters",
      "input": {
        "propertyPrice": 300000,
        "downPayment": 60000,
        "interestRate": 3.5,
        "loanTermYears": 20,
        "monthlyRent": 1500,
        "holdingPeriodYears": 10,
        "propertyTaxAnnual": 2400,
        "hoaMonthly": 150,
        "maintenancePercent": 1.0,
        "managementFeePercent": 8.0,
        "vacancyRate": 5.0,
        "rentIncreaseAnnual": 2.0
      }
    }
  ]
}
```

## Setup Instructions

### Option 1: Homebrew Installation

If you distributed via Homebrew:

```bash
# User installs
brew tap yourusername/tap
brew install french-property-investment

# Create skill directory
mkdir -p ~/.claude/skills/french-property-investment

# Create skill.json (use content above)
cat > ~/.claude/skills/french-property-investment/skill.json <<'EOF'
{
  "name": "french-property-investment",
  "executable": {
    "command": "french-property-investment",
    "timeout": 10000
  },
  "input": "json-stdin",
  "output": "json"
}
EOF
```

### Option 2: Local JAR Installation

If using the JAR directly:

```bash
# Create skill directory
mkdir -p ~/.claude/skills/french-property-investment

# Copy JAR
cp build/libs/french-property-investment.jar ~/.claude/skills/french-property-investment/

# Create wrapper script
cat > ~/.claude/skills/french-property-investment/run.sh <<'EOF'
#!/bin/bash
DIR="$(cd "$(dirname "$0")" && pwd)"
java -jar "$DIR/french-property-investment.jar" --json-input
EOF

chmod +x ~/.claude/skills/french-property-investment/run.sh

# Create skill.json
cat > ~/.claude/skills/french-property-investment/skill.json <<'EOF'
{
  "name": "french-property-investment",
  "executable": {
    "command": "~/.claude/skills/french-property-investment/run.sh",
    "timeout": 10000
  },
  "input": "json-stdin",
  "output": "json"
}
EOF
```

## Usage with Claude

Once installed, you can ask Claude to analyze properties:

### Example Conversations

**User**: "Analyze a 300k EUR property in France with 60k down payment, 3.5% interest rate, 20-year mortgage, renting for 1500/month. I plan to hold it for 10 years."

**Claude** will:
1. Recognize this matches the skill
2. Construct JSON input:
   ```json
   {
     "propertyPrice": 300000,
     "downPayment": 60000,
     "interestRate": 3.5,
     "loanTermYears": 20,
     "monthlyRent": 1500,
     "holdingPeriodYears": 10
   }
   ```
3. Execute: `echo '{"propertyPrice":300000,...}' | french-property-investment --json-input`
4. Parse the JSON response
5. Present results conversationally:
   - "This property would require an initial investment of €66,750..."
   - "Monthly mortgage payment: €1,471.90"
   - "Over 10 years, you'd generate €180,000 in rental income..."
   - "ROI: 115.86%"
   - etc.

**User**: "What if I add property tax of 2400/year and HOA fees of 150/month?"

**Claude** will:
1. Re-run with updated parameters
2. Show the difference in cash flow and ROI

**User**: "Compare this to a 250k property renting for 1300/month"

**Claude** will:
1. Run analysis for both properties
2. Present side-by-side comparison
3. Highlight which has better ROI, cash flow, etc.

## Testing the Skill

Test that Claude can invoke the skill:

```bash
# Manually test the command Claude will run
echo '{
  "propertyPrice": 300000,
  "downPayment": 60000,
  "interestRate": 3.5,
  "loanTermYears": 20,
  "monthlyRent": 1500,
  "holdingPeriodYears": 10
}' | french-property-investment --json-input
```

You should get JSON output with:
- `input`: Echo of parameters
- `mortgage`: Payment details
- `yearlyProjections`: Year-by-year breakdown
- `summary`: ROI, total costs, etc.

## Input Schema

The tool accepts these parameters via JSON:

```typescript
{
  // Required
  propertyPrice: number,        // Property purchase price (EUR)
  downPayment: number,          // Down payment (EUR)
  interestRate: number,         // Annual interest rate (%)
  loanTermYears: number,        // Loan term (years)
  monthlyRent: number,          // Monthly rental income (EUR)
  holdingPeriodYears: number,   // Investment holding period (years)

  // Optional (defaults to 0 if not provided)
  propertyTaxAnnual?: number,      // Annual property tax (EUR)
  hoaMonthly?: number,             // Monthly HOA/copropriété fees (EUR)
  maintenancePercent?: number,     // Maintenance as % of property value (default: 1.0)
  managementFeePercent?: number,   // Management fee as % of rent (default: 0)
  vacancyRate?: number,            // Vacancy rate % (default: 0)
  rentIncreaseAnnual?: number      // Annual rent increase % (default: 0)
}
```

## Output Schema

The tool returns:

```typescript
{
  input: { /* echo of input parameters */ },

  mortgage: {
    monthlyPayment: number,      // Monthly payment including insurance
    totalInterest: number,       // Total interest over loan term
    totalPayments: number        // Total paid over loan term
  },

  yearlyProjections: [
    {
      year: number,
      rentalIncome: number,
      expenses: {
        mortgage: number,
        propertyTax: number,
        hoa: number,
        maintenance: number,
        management: number,
        insurance: number,
        total: number
      },
      netCashFlow: number,
      cumulativeCashFlow: number,
      principalPaydown: number,
      totalEquity: number,
      remainingBalance: number
    },
    // ... one entry per year
  ],

  summary: {
    initialInvestment: number,
    loanAmount: number,
    totalRentalIncome: number,
    totalExpenses: number,
    totalCashFlow: number,
    totalPrincipalPaydown: number,
    finalEquity: number,
    netProfit: number,
    roi: number,
    avgCashOnCashReturn: number,
    breakEvenYear: number | null
  }
}
```

## Advanced: Custom Prompts

You can enhance Claude's understanding by adding custom prompts to your Claude configuration:

**`~/.claude/prompts/property-analysis.md`**:
```markdown
When analyzing French rental properties:

1. Always include French-specific costs:
   - Arrangement fees (1% of loan)
   - Registration fees (1.5% of loan)
   - Survey fees (€750)
   - Life insurance (included in monthly payment)

2. Key metrics to highlight:
   - Initial investment (down payment + fees)
   - Monthly cash flow (positive or negative)
   - Break-even year (if reached)
   - ROI over holding period
   - Total equity built

3. Red flags to mention:
   - Negative cash flow throughout holding period
   - No break-even within holding period
   - Low ROI (< 5% typically poor)
   - High expense ratios

4. Suggestions for improvement:
   - Increase down payment to lower monthly costs
   - Longer holding period for better ROI
   - Higher rent or lower expenses
```

## Troubleshooting

### Skill Not Found

If Claude can't find the skill:
1. Check skill.json is in correct location: `~/.claude/skills/french-property-investment/skill.json`
2. Verify executable is in PATH: `which french-property-investment`
3. Test manually: `echo '{"propertyPrice":300000,...}' | french-property-investment --json-input`

### Timeout Errors

If calculations timeout:
1. Increase timeout in skill.json: `"timeout": 30000`
2. Check Java is installed: `java -version`
3. Test execution time manually

### Invalid JSON Output

If Claude reports invalid JSON:
1. Test the tool manually and check output
2. Ensure you're using `--json-input` flag (not CLI args)
3. Check for error messages in stderr

### Permission Denied

If you get permission errors:
1. Check script is executable: `chmod +x ~/.claude/skills/french-property-investment/run.sh`
2. Verify JAR is readable: `ls -la ~/.claude/skills/french-property-investment/`

## Uninstalling

To remove the skill:

```bash
# Remove skill directory
rm -rf ~/.claude/skills/french-property-investment

# Uninstall tool (Homebrew)
brew uninstall french-property-investment
brew untap yourusername/tap

# Or remove manual installation
rm ~/.local/bin/french-property-investment
rm ~/.local/bin/french-property-investment.jar
```

## Next Steps

- Add more sophisticated modeling (appreciation, tax benefits, etc.)
- Create skills for other property types
- Integrate with web scraping to fetch property data automatically
- Build comparison tools for multiple properties
