# French Property Investment Analyzer

A CLI tool for analyzing French rental property investments with comprehensive financial modeling.

## Features

- **French Mortgage Calculations**: Includes French-specific costs (arrangement fees, registration fees, life insurance)
- **Cash Flow Projections**: Year-by-year rental income and expense tracking
- **ROI Analysis**: Return on investment, cash-on-cash return, break-even analysis
- **Equity Tracking**: Principal paydown and equity build-up over time
- **Multiple Output Formats**: JSON for programmatic use, table for human reading

## Installation

### Build from Source

```bash
cd cli-tools/french-property-investment
./gradlew shadowJar
```

The executable JAR will be created at `build/libs/french-property-investment.jar`

### Run

```bash
java -jar build/libs/french-property-investment.jar <options>
```

## Usage

### Basic Example

```bash
java -jar build/libs/french-property-investment.jar \
  --property-price 300000 \
  --down-payment 60000 \
  --interest-rate 3.5 \
  --loan-term 20 \
  --monthly-rent 1500 \
  --holding-period 10
```

### With All Parameters

```bash
java -jar build/libs/french-property-investment.jar \
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

### JSON Input

```bash
echo '{
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
}' | java -jar build/libs/french-property-investment.jar --json-input
```

### Output Formats

**JSON (default)**:
```bash
--format json
```
Outputs structured JSON suitable for parsing by other programs.

**Table**:
```bash
--format table
```
Outputs human-readable formatted tables.

## Parameters

### Required Parameters

- `--property-price`, `-p`: Property purchase price in EUR
- `--down-payment`, `-d`: Down payment in EUR
- `--interest-rate`, `-i`: Annual interest rate (%)
- `--loan-term`, `-l`: Loan term in years
- `--monthly-rent`, `-r`: Monthly rental income in EUR
- `--holding-period`, `-h`: Investment holding period in years

### Optional Parameters

- `--property-tax-annual`: Annual property tax (taxe foncière) in EUR (default: 0)
- `--hoa-monthly`: Monthly HOA/copropriété fees in EUR (default: 0)
- `--maintenance-percent`: Annual maintenance cost as % of property value (default: 1.0)
- `--management-fee-percent`: Property management fee as % of monthly rent (default: 0)
- `--vacancy-rate`: Expected vacancy rate as % (default: 0)
- `--rent-increase-annual`: Annual rent increase as % (default: 0)

### Input/Output Options

- `--json-input`: Read JSON input from stdin
- `--format`, `-f`: Output format (json|table, default: json)

## French Mortgage Specifics

The calculator includes French-specific mortgage costs:

1. **Arrangement Fee**: 1% of loan amount
2. **Registration Fee**: 1.5% of loan amount
3. **Survey Fee**: Fixed €750
4. **Life Insurance**: 0.4% of loan amount annually (included in monthly payment)

These fees are added to the initial investment calculation.

## Calculations

### Monthly Mortgage Payment

Uses standard amortization formula:
```
M = P × [r(1+r)^n] / [(1+r)^n - 1]
```
Plus French mortgage life insurance (0.4% annually).

### Yearly Projections

For each year:
- **Rental Income**: Base rent × (1 + rent increase)^year × (1 - vacancy rate)
- **Expenses**: Mortgage + property tax + HOA + maintenance + management fees
- **Net Cash Flow**: Rental income - expenses
- **Cumulative Cash Flow**: Sum of all net cash flows (including initial investment)
- **Equity**: Down payment + principal paid to date

### Summary Metrics

- **ROI**: (Net profit / Initial investment) × 100
- **Cash-on-Cash Return**: (Total cash flow / holding period / initial investment) × 100
- **Net Profit**: Total cash flow + final equity - initial investment
- **Break-even Year**: First year where cumulative cash flow becomes positive

## Testing

Run the test suite:

```bash
./gradlew test
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
1      €17,100.00   €25,308.00   €-8,208.00   €-74,958.00   €65,832.00
2      €17,442.00   €25,308.00   €-7,866.00   €-82,824.00   €71,985.00
...

SUMMARY
--------------------------------------------------------------------------------
Total Rental Income:   €185,670.00
Total Expenses:        €253,080.00
Total Cash Flow:       €-67,410.00
Principal Paid:        €72,450.00
Final Equity:          €132,450.00
Net Profit:            €-1,710.00
ROI:                   -2.56%
Avg Cash-on-Cash:      -10.10%
Break-even Year:       Not reached in 10 years
================================================================================
```

## License

Part of the Rentamap project.
