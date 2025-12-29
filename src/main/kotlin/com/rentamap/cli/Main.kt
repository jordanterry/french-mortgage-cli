package com.rentamap.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

class FrenchPropertyInvestment : CliktCommand(
    name = "french-property-investment",
    help = """
        Analyze French rental property investments with detailed cash flow projections.

        This tool calculates:
        - Monthly mortgage payments (including French life insurance)
        - Yearly cash flow (rental income - all expenses)
        - Return on investment (ROI)
        - Break-even analysis
        - Equity build-up over time

        Examples:
          # Basic analysis
          french-property-investment --property-price 300000 --down-payment 60000 \\
            --interest-rate 3.5 --loan-term 20 --monthly-rent 1500 --holding-period 10

          # With full parameters
          french-property-investment --property-price 300000 --down-payment 60000 \\
            --interest-rate 3.5 --loan-term 20 --monthly-rent 1500 --holding-period 10 \\
            --property-tax-annual 2400 --hoa-monthly 150 --maintenance-percent 1.0 \\
            --management-fee-percent 8.0 --vacancy-rate 5.0 --rent-increase-annual 2.0

          # JSON input
          echo '{"propertyPrice":300000,"downPayment":60000,...}' | french-property-investment --json-input

          # Human-readable table output
          french-property-investment <params> --format table
    """.trimIndent()
) {
    private val propertyPrice by option("--property-price", "-p")
        .double()
        .help("Property purchase price in EUR")

    private val downPayment by option("--down-payment", "-d")
        .double()
        .help("Down payment in EUR")

    private val interestRate by option("--interest-rate", "-i")
        .double()
        .help("Annual interest rate (%)")

    private val loanTermYears by option("--loan-term", "-l")
        .int()
        .help("Loan term in years")

    private val monthlyRent by option("--monthly-rent", "-r")
        .double()
        .help("Monthly rental income in EUR")

    private val holdingPeriodYears by option("--holding-period", "-h")
        .int()
        .help("Investment holding period in years")

    // Optional parameters with defaults
    private val propertyTaxAnnual by option("--property-tax-annual")
        .double()
        .default(0.0)
        .help("Annual property tax (taxe foncière) in EUR")

    private val hoaMonthly by option("--hoa-monthly")
        .double()
        .default(0.0)
        .help("Monthly HOA/copropriété fees in EUR")

    private val maintenancePercent by option("--maintenance-percent")
        .double()
        .default(1.0)
        .help("Annual maintenance cost as % of property value (default: 1.0)")

    private val managementFeePercent by option("--management-fee-percent")
        .double()
        .default(0.0)
        .help("Property management fee as % of monthly rent (default: 0.0)")

    private val vacancyRate by option("--vacancy-rate")
        .double()
        .default(0.0)
        .help("Expected vacancy rate as % (default: 0.0)")

    private val rentIncreaseAnnual by option("--rent-increase-annual")
        .double()
        .default(0.0)
        .help("Annual rent increase as % (default: 0.0)")

    private val jsonInput by option("--json-input")
        .flag(default = false)
        .help("Read JSON input from stdin")

    private val format by option("--format", "-f")
        .default("json")
        .help("Output format: json (default) or table")

    override fun run() {
        val input = try {
            if (jsonInput) {
                // Read JSON from stdin
                val jsonString = generateSequence(::readLine).joinToString("\n")
                Json.decodeFromString<PropertyInvestmentInput>(jsonString)
            } else {
                // Validate required parameters
                requireNotNull(propertyPrice) { "Missing required option --property-price" }
                requireNotNull(downPayment) { "Missing required option --down-payment" }
                requireNotNull(interestRate) { "Missing required option --interest-rate" }
                requireNotNull(loanTermYears) { "Missing required option --loan-term" }
                requireNotNull(monthlyRent) { "Missing required option --monthly-rent" }
                requireNotNull(holdingPeriodYears) { "Missing required option --holding-period" }

                PropertyInvestmentInput(
                    propertyPrice = propertyPrice!!,
                    downPayment = downPayment!!,
                    interestRate = interestRate!!,
                    loanTermYears = loanTermYears!!,
                    monthlyRent = monthlyRent!!,
                    holdingPeriodYears = holdingPeriodYears!!,
                    propertyTaxAnnual = propertyTaxAnnual,
                    hoaMonthly = hoaMonthly,
                    maintenancePercent = maintenancePercent,
                    managementFeePercent = managementFeePercent,
                    vacancyRate = vacancyRate,
                    rentIncreaseAnnual = rentIncreaseAnnual
                )
            }
        } catch (e: Exception) {
            echo("Error parsing input: ${e.message}", err = true)
            exitProcess(1)
        }

        val calculator = FrenchPropertyInvestmentCalculator()

        val result = try {
            calculator.analyze(input)
        } catch (e: IllegalArgumentException) {
            echo("Validation error: ${e.message}", err = true)
            exitProcess(1)
        } catch (e: Exception) {
            echo("Calculation error: ${e.message}", err = true)
            exitProcess(1)
        }

        when (format) {
            "json" -> printJson(result)
            "table" -> printTable(result)
        }
    }

    private fun printJson(result: PropertyInvestmentResult) {
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        echo(json.encodeToString(result))
    }

    private fun printTable(result: PropertyInvestmentResult) {
        echo("\n" + "=".repeat(80))
        echo("FRENCH PROPERTY INVESTMENT ANALYSIS")
        echo("=".repeat(80))

        echo("\nINVESTMENT OVERVIEW")
        echo("-".repeat(80))
        echo("Property Price:        €${formatNumber(result.input.propertyPrice)}")
        echo("Down Payment:          €${formatNumber(result.input.downPayment)}")
        echo("Loan Amount:           €${formatNumber(result.summary.loanAmount)}")
        echo("Initial Investment:    €${formatNumber(result.summary.initialInvestment)}")
        echo("Interest Rate:         ${formatNumber(result.input.interestRate)}%")
        echo("Loan Term:             ${result.input.loanTermYears} years")
        echo("Holding Period:        ${result.input.holdingPeriodYears} years")

        echo("\nMORTGAGE DETAILS")
        echo("-".repeat(80))
        echo("Monthly Payment:       €${formatNumber(result.mortgage.monthlyPayment)}")
        echo("Total Interest:        €${formatNumber(result.mortgage.totalInterest)}")
        echo("Total Payments:        €${formatNumber(result.mortgage.totalPayments)}")

        echo("\nRENTAL PARAMETERS")
        echo("-".repeat(80))
        echo("Monthly Rent:          €${formatNumber(result.input.monthlyRent)}")
        echo("Vacancy Rate:          ${formatNumber(result.input.vacancyRate)}%")
        echo("Rent Increase:         ${formatNumber(result.input.rentIncreaseAnnual)}% annually")
        echo("Management Fee:        ${formatNumber(result.input.managementFeePercent)}%")

        echo("\nYEARLY PROJECTIONS")
        echo("-".repeat(80))
        echo(String.format("%-6s %-12s %-12s %-12s %-14s %-12s",
            "Year", "Rent", "Expenses", "Net Cash", "Cumulative", "Equity"))
        echo("-".repeat(80))

        for (projection in result.yearlyProjections) {
            echo(String.format("%-6d €%-11s €%-11s €%-11s €%-13s €%-11s",
                projection.year,
                formatNumber(projection.rentalIncome),
                formatNumber(projection.expenses.total),
                formatNumber(projection.netCashFlow),
                formatNumber(projection.cumulativeCashFlow),
                formatNumber(projection.totalEquity)
            ))
        }

        echo("\nSUMMARY")
        echo("-".repeat(80))
        echo("Total Rental Income:   €${formatNumber(result.summary.totalRentalIncome)}")
        echo("Total Expenses:        €${formatNumber(result.summary.totalExpenses)}")
        echo("Total Cash Flow:       €${formatNumber(result.summary.totalCashFlow)}")
        echo("Principal Paid:        €${formatNumber(result.summary.totalPrincipalPaydown)}")
        echo("Final Equity:          €${formatNumber(result.summary.finalEquity)}")
        echo("Net Profit:            €${formatNumber(result.summary.netProfit)}")
        echo("ROI:                   ${formatNumber(result.summary.roi)}%")
        echo("Avg Cash-on-Cash:      ${formatNumber(result.summary.avgCashOnCashReturn)}%")

        if (result.summary.breakEvenYear != null) {
            echo("Break-even Year:       ${result.summary.breakEvenYear}")
        } else {
            echo("Break-even Year:       Not reached in ${result.input.holdingPeriodYears} years")
        }

        echo("=".repeat(80) + "\n")
    }

    private fun formatNumber(value: Double): String {
        return String.format("%,.2f", value)
    }
}

fun main(args: Array<String>) = FrenchPropertyInvestment().main(args)
