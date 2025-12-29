package com.rentamap.cli

import kotlinx.serialization.Serializable

@Serializable
data class PropertyInvestmentInput(
    val propertyPrice: Double,
    val downPayment: Double,
    val interestRate: Double,
    val loanTermYears: Int,
    val monthlyRent: Double,
    val holdingPeriodYears: Int,
    val propertyTaxAnnual: Double = 0.0,
    val hoaMonthly: Double = 0.0,
    val maintenancePercent: Double = 1.0,
    val managementFeePercent: Double = 0.0,
    val vacancyRate: Double = 0.0,
    val rentIncreaseAnnual: Double = 0.0
)

@Serializable
data class MonthlyExpenses(
    val mortgage: Double,
    val propertyTax: Double,
    val hoa: Double,
    val maintenance: Double,
    val management: Double,
    val total: Double
)

@Serializable
data class YearlyProjection(
    val year: Int,
    val rentalIncome: Double,
    val expenses: YearlyExpenses,
    val netCashFlow: Double,
    val cumulativeCashFlow: Double,
    val principalPaydown: Double,
    val totalEquity: Double,
    val remainingBalance: Double
)

@Serializable
data class YearlyExpenses(
    val mortgage: Double,
    val propertyTax: Double,
    val hoa: Double,
    val maintenance: Double,
    val management: Double,
    val insurance: Double,
    val total: Double
)

@Serializable
data class InvestmentSummary(
    val initialInvestment: Double,
    val loanAmount: Double,
    val totalRentalIncome: Double,
    val totalExpenses: Double,
    val totalCashFlow: Double,
    val totalPrincipalPaydown: Double,
    val finalEquity: Double,
    val netProfit: Double,
    val roi: Double,
    val avgCashOnCashReturn: Double,
    val breakEvenYear: Int?
)

@Serializable
data class MortgageDetails(
    val monthlyPayment: Double,
    val totalInterest: Double,
    val totalPayments: Double
)

@Serializable
data class PropertyInvestmentResult(
    val input: PropertyInvestmentInput,
    val mortgage: MortgageDetails,
    val yearlyProjections: List<YearlyProjection>,
    val summary: InvestmentSummary
)
