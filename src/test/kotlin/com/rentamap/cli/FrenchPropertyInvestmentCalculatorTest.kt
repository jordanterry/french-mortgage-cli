package com.rentamap.cli

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FrenchPropertyInvestmentCalculatorTest : StringSpec({

    val calculator = FrenchPropertyInvestmentCalculator()

    "should calculate basic property investment correctly" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 10
        )

        val result = calculator.analyze(input)

        // Verify loan amount
        result.summary.loanAmount shouldBe 240000.0

        // Verify mortgage payment is positive
        result.mortgage.monthlyPayment shouldBeGreaterThan 0.0

        // Verify we have 10 yearly projections
        result.yearlyProjections.size shouldBe 10

        // Verify initial investment includes French fees
        // Down payment (60k) + arrangement (2.4k) + registration (3.6k) + survey (750)
        result.summary.initialInvestment shouldBeGreaterThan 60000.0
        result.summary.initialInvestment shouldBeLessThan 70000.0
    }

    "should calculate mortgage payment using standard amortization formula" {
        val input = PropertyInvestmentInput(
            propertyPrice = 240000.0,
            downPayment = 0.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 1
        )

        val result = calculator.analyze(input)

        // Monthly payment should be around 1,390 EUR (P&I) + ~80 EUR (insurance)
        result.mortgage.monthlyPayment shouldBeGreaterThan 1400.0
        result.mortgage.monthlyPayment shouldBeLessThan 1500.0
    }

    "should include French mortgage life insurance in monthly payment" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 1
        )

        val result = calculator.analyze(input)

        // Life insurance should be approximately 0.4% of loan amount annually
        // For 240k loan: ~960 EUR/year = ~80 EUR/month
        val loanAmount = 240000.0
        val expectedAnnualInsurance = loanAmount * 0.004

        result.yearlyProjections[0].expenses.insurance shouldBeGreaterThan expectedAnnualInsurance * 0.9
        result.yearlyProjections[0].expenses.insurance shouldBeLessThan expectedAnnualInsurance * 1.1
    }

    "should calculate yearly projections with increasing rent" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 5,
            rentIncreaseAnnual = 2.0
        )

        val result = calculator.analyze(input)

        // Rental income should increase each year
        for (i in 1 until result.yearlyProjections.size) {
            result.yearlyProjections[i].rentalIncome shouldBeGreaterThan
                result.yearlyProjections[i - 1].rentalIncome
        }
    }

    "should apply vacancy rate to rental income" {
        val inputWithoutVacancy = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 1,
            vacancyRate = 0.0
        )

        val inputWithVacancy = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 1,
            vacancyRate = 10.0
        )

        val resultWithoutVacancy = calculator.analyze(inputWithoutVacancy)
        val resultWithVacancy = calculator.analyze(inputWithVacancy)

        // Rental income should be 10% lower with vacancy
        resultWithVacancy.yearlyProjections[0].rentalIncome shouldBeLessThan
            resultWithoutVacancy.yearlyProjections[0].rentalIncome
    }

    "should calculate equity build-up over time" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 10
        )

        val result = calculator.analyze(input)

        // Equity should increase each year due to principal paydown
        for (i in 1 until result.yearlyProjections.size) {
            result.yearlyProjections[i].totalEquity shouldBeGreaterThan
                result.yearlyProjections[i - 1].totalEquity
        }

        // Final equity should be down payment + principal paid
        result.summary.finalEquity shouldBe
            input.downPayment + result.summary.totalPrincipalPaydown
    }

    "should calculate cumulative cash flow correctly" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 3
        )

        val result = calculator.analyze(input)

        // First year cumulative should be negative initial investment + year 1 net
        result.yearlyProjections[0].cumulativeCashFlow shouldBe
            -result.summary.initialInvestment + result.yearlyProjections[0].netCashFlow

        // Each year adds to cumulative
        var expectedCumulative = -result.summary.initialInvestment
        for (projection in result.yearlyProjections) {
            expectedCumulative += projection.netCashFlow
            projection.cumulativeCashFlow shouldBe expectedCumulative
        }
    }

    "should calculate ROI correctly" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 10
        )

        val result = calculator.analyze(input)

        // ROI = (net profit / initial investment) * 100
        val expectedRoi = (result.summary.netProfit / result.summary.initialInvestment) * 100.0
        result.summary.roi shouldBe expectedRoi
    }

    "should include all expense categories" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 1,
            propertyTaxAnnual = 2400.0,
            hoaMonthly = 150.0,
            maintenancePercent = 1.0,
            managementFeePercent = 8.0
        )

        val result = calculator.analyze(input)
        val firstYear = result.yearlyProjections[0].expenses

        // All expenses should be included
        firstYear.mortgage shouldBeGreaterThan 0.0
        firstYear.propertyTax shouldBe 2400.0
        firstYear.hoa shouldBe 150.0 * 12
        firstYear.maintenance shouldBe 300000.0 * 0.01
        firstYear.management shouldBe 1500.0 * 12 * 0.08
        firstYear.insurance shouldBeGreaterThan 0.0

        // Total should equal sum of all
        firstYear.total shouldBe firstYear.mortgage + firstYear.propertyTax +
            firstYear.hoa + firstYear.maintenance + firstYear.management
    }

    "should detect break-even year when cumulative cash flow becomes positive" {
        // Create scenario with high rent that should break even
        val input = PropertyInvestmentInput(
            propertyPrice = 200000.0,
            downPayment = 40000.0,
            interestRate = 3.0,
            loanTermYears = 20,
            monthlyRent = 2000.0,
            holdingPeriodYears = 15
        )

        val result = calculator.analyze(input)

        // Should eventually break even with high rent
        result.summary.breakEvenYear shouldNotBe null
    }

    "should validate input parameters" {
        shouldThrow<IllegalArgumentException> {
            calculator.analyze(PropertyInvestmentInput(
                propertyPrice = 0.0,
                downPayment = 60000.0,
                interestRate = 3.5,
                loanTermYears = 20,
                monthlyRent = 1500.0,
                holdingPeriodYears = 10
            ))
        }

        shouldThrow<IllegalArgumentException> {
            calculator.analyze(PropertyInvestmentInput(
                propertyPrice = 300000.0,
                downPayment = 350000.0,
                interestRate = 3.5,
                loanTermYears = 20,
                monthlyRent = 1500.0,
                holdingPeriodYears = 10
            ))
        }

        shouldThrow<IllegalArgumentException> {
            calculator.analyze(PropertyInvestmentInput(
                propertyPrice = 300000.0,
                downPayment = 60000.0,
                interestRate = 3.5,
                loanTermYears = 20,
                monthlyRent = 1500.0,
                holdingPeriodYears = 10,
                vacancyRate = 150.0
            ))
        }
    }

    "should calculate net profit as total cash flow plus equity minus initial investment" {
        val input = PropertyInvestmentInput(
            propertyPrice = 300000.0,
            downPayment = 60000.0,
            interestRate = 3.5,
            loanTermYears = 20,
            monthlyRent = 1500.0,
            holdingPeriodYears = 10
        )

        val result = calculator.analyze(input)

        val expectedNetProfit = result.summary.totalCashFlow +
                               result.summary.finalEquity -
                               result.summary.initialInvestment

        result.summary.netProfit shouldBe expectedNetProfit
    }
})
