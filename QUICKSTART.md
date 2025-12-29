# French Property Investment Analyzer - Quick Start

## Build

```bash
cd cli-tools/french-property-investment
../../backend/gradlew build
```

The executable JAR will be at: `build/libs/french-property-investment.jar`

## Run

```bash
java -jar build/libs/french-property-investment.jar [OPTIONS]
```

## Example 1: Basic Analysis

```bash
java -jar build/libs/french-property-investment.jar \
  --property-price 300000 \
  --down-payment 60000 \
  --interest-rate 3.5 \
  --loan-term 20 \
  --monthly-rent 1500 \
  --holding-period 10
```

## Example 2: Full Parameters with Table Output

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

## Example 3: JSON Input (for Claude)

```bash
echo '{
  "propertyPrice": 300000,
  "downPayment": 60000,
  "interestRate": 3.5,
  "loanTermYears": 20,
  "monthlyRent": 1500,
  "holdingPeriodYears": 10
}' | java -jar build/libs/french-property-investment.jar --json-input
```

## Help

```bash
java -jar build/libs/french-property-investment.jar --help
```

## Tests

```bash
../../backend/gradlew test
```

All 13 tests passing ✓
