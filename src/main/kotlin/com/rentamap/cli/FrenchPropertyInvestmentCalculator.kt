package com.rentamap.cli

import kotlin.math.pow

class FrenchPropertyInvestmentCalculator {

    fun analyze(input: PropertyInvestmentInput): PropertyInvestmentResult {
        // Validate input
        validateInput(input)

        val loanAmount = input.propertyPrice - input.downPayment

        // Calculate mortgage details
        val monthlyRate = input.interestRate / 100.0 / 12.0
        val numPayments = input.loanTermYears * 12

        val monthlyMortgagePayment = calculateMonthlyPayment(
            loanAmount,
            monthlyRate,
            numPayments
        )

        // Calculate French mortgage life insurance (0.25% - 0.70% of loan amount annually)
        // Using middle estimate of 0.40% for non-smoker
        val monthlyLifeInsurance = (loanAmount * 0.004) / 12.0
        val totalMonthlyMortgage = monthlyMortgagePayment + monthlyLifeInsurance

        // Calculate total interest over loan term
        val totalMortgagePayments = totalMonthlyMortgage * numPayments
        val totalInterest = totalMortgagePayments - loanAmount

        val mortgageDetails = MortgageDetails(
            monthlyPayment = totalMonthlyMortgage,
            totalInterest = totalInterest,
            totalPayments = totalMortgagePayments
        )

        // Calculate initial investment (down payment + French upfront costs)
        val arrangementFee = loanAmount * 0.01  // 1% of loan
        val registrationFee = loanAmount * 0.015  // 1.5% of loan
        val surveyFee = 750.0
        val initialInvestment = input.downPayment + arrangementFee + registrationFee + surveyFee

        // Generate yearly projections
        val yearlyProjections = mutableListOf<YearlyProjection>()
        var cumulativeCashFlow = -initialInvestment
        var remainingBalance = loanAmount
        var breakEvenYear: Int? = null

        for (year in 1..input.holdingPeriodYears) {
            // Calculate rental income for this year (with rent increases)
            val rentMultiplier = (1 + input.rentIncreaseAnnual / 100.0).pow(year - 1)
            val baseMonthlyRent = input.monthlyRent * rentMultiplier
            val effectiveMonthlyRent = baseMonthlyRent * (1 - input.vacancyRate / 100.0)
            val annualRentalIncome = effectiveMonthlyRent * 12

            // Calculate expenses for this year
            val annualMortgage = totalMonthlyMortgage * 12
            val annualPropertyTax = input.propertyTaxAnnual
            val annualHoa = input.hoaMonthly * 12
            val annualMaintenance = input.propertyPrice * (input.maintenancePercent / 100.0)
            val annualManagement = baseMonthlyRent * 12 * (input.managementFeePercent / 100.0)
            val annualInsurance = loanAmount * 0.004  // Life insurance included in mortgage

            val totalExpenses = annualMortgage + annualPropertyTax + annualHoa +
                               annualMaintenance + annualManagement

            // Calculate principal paydown for this year
            val principalPaydown = calculateYearlyPrincipalPaydown(
                remainingBalance,
                monthlyRate,
                totalMonthlyMortgage,
                year,
                input.loanTermYears
            )

            remainingBalance -= principalPaydown

            // Net cash flow for this year
            val netCashFlow = annualRentalIncome - totalExpenses
            cumulativeCashFlow += netCashFlow

            // Check for break-even
            if (breakEvenYear == null && cumulativeCashFlow >= 0) {
                breakEvenYear = year
            }

            // Total equity = down payment + cumulative principal paydown
            val totalEquity = input.downPayment + (loanAmount - remainingBalance)

            yearlyProjections.add(
                YearlyProjection(
                    year = year,
                    rentalIncome = annualRentalIncome,
                    expenses = YearlyExpenses(
                        mortgage = annualMortgage,
                        propertyTax = annualPropertyTax,
                        hoa = annualHoa,
                        maintenance = annualMaintenance,
                        management = annualManagement,
                        insurance = annualInsurance,
                        total = totalExpenses
                    ),
                    netCashFlow = netCashFlow,
                    cumulativeCashFlow = cumulativeCashFlow,
                    principalPaydown = principalPaydown,
                    totalEquity = totalEquity,
                    remainingBalance = remainingBalance
                )
            )
        }

        // Calculate summary metrics
        val totalRentalIncome = yearlyProjections.sumOf { it.rentalIncome }
        val totalExpenses = yearlyProjections.sumOf { it.expenses.total }
        val totalCashFlow = totalRentalIncome - totalExpenses
        val totalPrincipalPaydown = loanAmount - remainingBalance
        val finalEquity = input.downPayment + totalPrincipalPaydown
        val netProfit = totalCashFlow + finalEquity - initialInvestment
        val roi = (netProfit / initialInvestment) * 100.0
        val avgCashOnCashReturn = (totalCashFlow / input.holdingPeriodYears / initialInvestment) * 100.0

        val summary = InvestmentSummary(
            initialInvestment = initialInvestment,
            loanAmount = loanAmount,
            totalRentalIncome = totalRentalIncome,
            totalExpenses = totalExpenses,
            totalCashFlow = totalCashFlow,
            totalPrincipalPaydown = totalPrincipalPaydown,
            finalEquity = finalEquity,
            netProfit = netProfit,
            roi = roi,
            avgCashOnCashReturn = avgCashOnCashReturn,
            breakEvenYear = breakEvenYear
        )

        return PropertyInvestmentResult(
            input = input,
            mortgage = mortgageDetails,
            yearlyProjections = yearlyProjections,
            summary = summary
        )
    }

    private fun calculateMonthlyPayment(principal: Double, monthlyRate: Double, numPayments: Int): Double {
        if (monthlyRate == 0.0) {
            return principal / numPayments
        }
        return principal * (monthlyRate * (1 + monthlyRate).pow(numPayments)) /
               ((1 + monthlyRate).pow(numPayments) - 1)
    }

    private fun calculateYearlyPrincipalPaydown(
        startingBalance: Double,
        monthlyRate: Double,
        monthlyPayment: Double,
        currentYear: Int,
        loanTermYears: Int
    ): Double {
        // Don't calculate beyond loan term
        if (currentYear > loanTermYears) {
            return 0.0
        }

        var balance = startingBalance
        var yearlyPrincipal = 0.0

        // Calculate principal for 12 months
        for (month in 1..12) {
            if (balance <= 0) break

            val interestPayment = balance * monthlyRate
            val principalPayment = monthlyPayment - interestPayment
            yearlyPrincipal += principalPayment
            balance -= principalPayment
        }

        return yearlyPrincipal
    }

    private fun validateInput(input: PropertyInvestmentInput) {
        require(input.propertyPrice > 0) { "Property price must be positive" }
        require(input.downPayment >= 0) { "Down payment cannot be negative" }
        require(input.downPayment < input.propertyPrice) { "Down payment must be less than property price" }
        require(input.interestRate > 0) { "Interest rate must be positive" }
        require(input.loanTermYears > 0) { "Loan term must be positive" }
        require(input.monthlyRent >= 0) { "Monthly rent cannot be negative" }
        require(input.holdingPeriodYears > 0) { "Holding period must be positive" }
        require(input.propertyTaxAnnual >= 0) { "Property tax cannot be negative" }
        require(input.hoaMonthly >= 0) { "HOA fees cannot be negative" }
        require(input.maintenancePercent >= 0) { "Maintenance percentage cannot be negative" }
        require(input.managementFeePercent >= 0) { "Management fee percentage cannot be negative" }
        require(input.vacancyRate >= 0 && input.vacancyRate <= 100) { "Vacancy rate must be between 0 and 100" }
        require(input.rentIncreaseAnnual >= -100) { "Rent increase must be greater than -100%" }
    }
}
