# French Property Investment Analyzer

A command-line tool for analyzing French rental property investments with comprehensive financial modeling.

[![Build](https://github.com/yourusername/french-property-investment/actions/workflows/release.yml/badge.svg)](https://github.com/yourusername/french-property-investment/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Features

- **French Mortgage Calculations**: Includes French-specific costs
  - Life insurance (0.4% annually)
  - Arrangement fees (1% of loan)
  - Registration fees (1.5% of loan)
  - Survey fees (€750 fixed)

- **Year-by-Year Projections**: Track your investment over time
  - Rental income (with vacancy and rent increases)
  - All expenses (mortgage, taxes, HOA, maintenance, management)
  - Net cash flow and cumulative cash flow
  - Equity build-up from principal paydown

- **Financial Metrics**:
  - ROI (Return on Investment)
  - Cash-on-cash return
  - Break-even analysis
  - Net profit calculation

- **Flexible I/O**:
  - CLI arguments or JSON input
  - JSON output (for automation) or formatted tables (for humans)
  - Perfect for integration with other tools or Claude Code skills

## Installation

### Homebrew (macOS/Linux)

```bash
brew tap yourusername/tap
brew install french-property-investment
```

### Manual Installation

```bash
curl -fsSL https://raw.githubusercontent.com/yourusername/french-property-investment/main/scripts/install.sh | bash
```

Or download the JAR from [Releases](https://github.com/yourusername/french-property-investment/releases):

```bash
wget https://github.com/yourusername/french-property-investment/releases/latest/download/french-property-investment.jar
java -jar french-property-investment.jar --help
```

## Quick Start

### Basic Analysis

```bash
french-property-investment \
  --property-price 300000 \
  --down-payment 60000 \
  --interest-rate 3.5 \
  --loan-term 20 \
  --monthly-rent 1500 \
  --holding-period 10
```

### Full Analysis with All Parameters

```bash
french-property-investment \
  --property-price 300000 \
  --down-payment 60000 \
  --interest-rate 3.5 \
  --loan-term 20 \
  --monthly-rent 1500 \
  --holding-period 10 \
  --property-tax-annual 2400 \
  --hoa-monthly 150 \
  --maintenance-percent 1.0 \
  --management-fee-percent 8.0 \
  --vacancy-rate 5.0 \
  --rent-increase-annual 2.0 \
  --format table
```

### JSON Input (for automation)

```bash
echo '{
  "propertyPrice": 300000,
  "downPayment": 60000,
  "interestRate": 3.5,
  "loanTermYears": 20,
  "monthlyRent": 1500,
  "holdingPeriodYears": 10
}' | french-property-investment --json-input
```

## Example Output

### Table Format

```
================================================================================
FRENCH PROPERTY INVESTMENT ANALYSIS
================================================================================

INVESTMENT OVERVIEW
--------------------------------------------------------------------------------
Property Price:        €300,000.00
Down Payment:          €60,000.00
Loan Amount:           €240,000.00
Initial Investment:    €66,750.00
Interest Rate:         3.50%
Loan Term:             20 years
Holding Period:        10 years

YEARLY PROJECTIONS
--------------------------------------------------------------------------------
Year   Rent         Expenses     Net Cash     Cumulative    Equity
--------------------------------------------------------------------------------
1      €18,000.00   €20,662.84   €-2,662.84   €-69,412.84   €69,412.89
2      €18,000.00   €20,662.84   €-2,662.84   €-72,075.68   €79,160.56
...

SUMMARY
--------------------------------------------------------------------------------
Total Rental Income:   €180,000.00
Total Expenses:        €206,628.40
Total Cash Flow:       €-26,628.40
Principal Paid:        €110,716.03
Final Equity:          €170,716.03
Net Profit:            €77,337.63
ROI:                   115.86%
Avg Cash-on-Cash:      -3.99%
================================================================================
```

### JSON Format

Perfect for parsing by other programs or Claude Code:

```json
{
  "input": { ... },
  "mortgage": {
    "monthlyPayment": 1471.90,
    "totalInterest": 113256.80,
    "totalPayments": 353256.80
  },
  "yearlyProjections": [ ... ],
  "summary": {
    "roi": 115.86,
    "netProfit": 77337.63,
    ...
  }
}
```

## Parameters

### Required

- `--property-price`, `-p`: Property purchase price (EUR)
- `--down-payment`, `-d`: Down payment (EUR)
- `--interest-rate`, `-i`: Annual interest rate (%)
- `--loan-term`, `-l`: Loan term (years)
- `--monthly-rent`, `-r`: Monthly rental income (EUR)
- `--holding-period`, `-h`: Investment holding period (years)

### Optional

- `--property-tax-annual`: Annual property tax (EUR, default: 0)
- `--hoa-monthly`: Monthly HOA fees (EUR, default: 0)
- `--maintenance-percent`: Maintenance as % of property value (default: 1.0)
- `--management-fee-percent`: Management fee as % of rent (default: 0)
- `--vacancy-rate`: Vacancy rate % (default: 0)
- `--rent-increase-annual`: Annual rent increase % (default: 0)
- `--format`, `-f`: Output format (json|table, default: json)
- `--json-input`: Read JSON from stdin

## Use Cases

### Property Comparison

Compare multiple properties by running the tool multiple times:

```bash
# Property A
french-property-investment --property-price 300000 --monthly-rent 1500 ... > property-a.json

# Property B
french-property-investment --property-price 250000 --monthly-rent 1300 ... > property-b.json

# Compare ROI
jq '.summary.roi' property-a.json property-b.json
```

### Sensitivity Analysis

Test different scenarios:

```bash
# Scenario 1: Base case
french-property-investment --property-price 300000 --monthly-rent 1500 ...

# Scenario 2: Higher rent
french-property-investment --property-price 300000 --monthly-rent 1600 ...

# Scenario 3: With rent increases
french-property-investment --property-price 300000 --monthly-rent 1500 --rent-increase-annual 2 ...
```

### Integration with Claude Code

Use as a Claude Code skill for conversational property analysis. See [CLAUDE_SKILL.md](CLAUDE_SKILL.md) for setup instructions.

### Automation & Scripting

Integrate into investment pipelines:

```bash
#!/bin/bash
# Batch analyze properties from a CSV

while IFS=, read -r price rent; do
  echo "Analyzing €${price} property..."
  french-property-investment \
    --property-price "$price" \
    --down-payment $((price / 5)) \
    --interest-rate 3.5 \
    --loan-term 20 \
    --monthly-rent "$rent" \
    --holding-period 10 \
    --format table
  echo ""
done < properties.csv
```

## Documentation

- [Quick Start Guide](QUICKSTART.md)
- [Distribution Guide](DISTRIBUTION.md) - For maintainers
- [Claude Skill Integration](CLAUDE_SKILL.md) - Use with Claude Code

## Requirements

- Java 11 or higher
- Works on macOS, Linux, Windows (via WSL)

## Building from Source

```bash
# Clone repository
git clone https://github.com/yourusername/french-property-investment
cd french-property-investment

# Build
./gradlew shadowJar

# Run
java -jar build/libs/french-property-investment.jar --help
```

## Testing

```bash
./gradlew test
```

All tests use [Kotest](https://kotest.io/) framework.

## How It Works

### French Mortgage Calculations

Uses standard amortization formula with French-specific additions:

```
Monthly Payment = P × [r(1+r)^n] / [(1+r)^n - 1]
                + (Loan Amount × 0.004) / 12  // Life insurance
```

Where:
- P = Loan principal
- r = Monthly interest rate
- n = Number of payments

### Initial Investment

Includes French transaction costs:
- Down payment
- Arrangement fee: 1% of loan
- Registration fee: 1.5% of loan
- Survey fee: €750

### Yearly Projections

For each year:
1. Calculate rental income (adjusted for vacancy and rent increases)
2. Calculate expenses (mortgage, taxes, HOA, maintenance, management)
3. Net cash flow = Income - Expenses
4. Track cumulative cash flow and equity build-up

### Financial Metrics

- **ROI**: (Net Profit / Initial Investment) × 100
- **Cash-on-Cash**: (Annual Cash Flow / Initial Investment) × 100
- **Net Profit**: Total Cash Flow + Final Equity - Initial Investment
- **Break-even**: First year where cumulative cash flow ≥ 0

## Technology

- **Kotlin 2.0.0**: Modern JVM language
- **Clikt 4.2.2**: Command-line interface framework
- **kotlinx.serialization**: JSON I/O
- **Kotest 5.8.0**: Testing framework
- **Gradle 8.13**: Build system

## Contributing

Contributions welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass: `./gradlew test`
5. Submit a pull request

See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/french-property-investment/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/french-property-investment/discussions)

## Acknowledgments

Built with ❤️ for French property investors.

Part of the [Rentamap](https://github.com/yourusername/rentamap-site) project.

## Roadmap

- [ ] Add property appreciation modeling
- [ ] Include French tax benefits (Pinel, etc.)
- [ ] Support for commercial properties
- [ ] Multi-currency support
- [ ] Web interface
- [ ] GraalVM native binary for faster startup
- [ ] Property comparison mode
- [ ] Export to Excel/PDF
