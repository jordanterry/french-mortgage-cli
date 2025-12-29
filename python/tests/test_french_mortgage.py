#!/usr/bin/env python3
"""Comprehensive tests for French Property Investment Analyzer."""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

import unittest
from french_mortgage import (
    FrenchPropertyInvestmentCalculator,
    PropertyInvestmentInput
)


class TestMortgageCalculations(unittest.TestCase):
    """Test mortgage amortization calculations."""

    def setUp(self):
        self.calculator = FrenchPropertyInvestmentCalculator()

    def test_monthly_payment_formula(self):
        """Monthly payment should match expected amortization formula."""
        input_data = PropertyInvestmentInput(
            property_price=240000.0,
            down_payment=0.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=1
        )

        result = self.calculator.analyze(input_data)

        principal = 240000.0
        monthly_rate = 3.5 / 100.0 / 12.0
        num_payments = 20 * 12
        base_payment = principal * (monthly_rate * ((1 + monthly_rate) ** num_payments)) / \
                      (((1 + monthly_rate) ** num_payments) - 1)
        life_insurance = (principal * 0.004) / 12.0
        expected_monthly_payment = base_payment + life_insurance

        self.assertAlmostEqual(result.mortgage.monthly_payment, expected_monthly_payment, places=2)

    def test_interest_decreases_over_time(self):
        """Interest payments should decrease each year."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=4.0,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        loan_amount = 240000.0
        monthly_rate = 4.0 / 100.0 / 12.0
        monthly_payment = result.mortgage.monthly_payment

        previous_year_interest = float('inf')

        for i in range(10):
            year = result.yearly_projections[i]
            balance = loan_amount - sum(p.principal_paydown for p in result.yearly_projections[:i])
            yearly_interest = 0.0

            for month in range(12):
                if balance <= 0:
                    break
                interest_payment = balance * monthly_rate
                yearly_interest += interest_payment
                principal_payment = monthly_payment - interest_payment
                balance -= principal_payment

            if i > 0:
                self.assertLess(yearly_interest, previous_year_interest)
            previous_year_interest = yearly_interest

    def test_principal_increases_over_time(self):
        """Principal payments should increase each year."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=4.0,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        for i in range(1, len(result.yearly_projections)):
            current_year = result.yearly_projections[i]
            previous_year = result.yearly_projections[i - 1]

            if current_year.principal_paydown > 0 and previous_year.principal_paydown > 0:
                self.assertGreater(current_year.principal_paydown, previous_year.principal_paydown)

    def test_principal_sums_to_loan_amount(self):
        """Over full loan term, total principal should equal loan amount."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=15,
            monthly_rent=1500.0,
            holding_period_years=15
        )

        result = self.calculator.analyze(input_data)

        loan_amount = 240000.0
        total_principal_paid = result.summary.total_principal_paydown

        self.assertGreater(total_principal_paid, loan_amount - 100.0)
        self.assertLess(total_principal_paid, loan_amount + 1000.0)

    def test_total_payments_equals_principal_plus_interest(self):
        """Total payments should equal principal plus total interest."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=20
        )

        result = self.calculator.analyze(input_data)

        loan_amount = 240000.0
        expected_total_payments = loan_amount + result.mortgage.total_interest

        self.assertAlmostEqual(result.mortgage.total_payments, expected_total_payments, places=2)

    def test_remaining_balance_decreases(self):
        """Remaining balance should decrease each year."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        previous_balance = 240000.0
        for year in result.yearly_projections:
            self.assertLess(year.remaining_balance, previous_balance)
            previous_balance = year.remaining_balance


class TestStoredMortgageValues(unittest.TestCase):
    """Test against stored reference values for mortgage calculations."""

    def setUp(self):
        self.calculator = FrenchPropertyInvestmentCalculator()

    def test_240k_loan_at_3_5_percent_20_years(self):
        """Stored values for 240k loan at 3.5% over 20 years."""
        input_data = PropertyInvestmentInput(
            property_price=240000.0,
            down_payment=0.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=20
        )

        result = self.calculator.analyze(input_data)

        self.assertAlmostEqual(result.mortgage.monthly_payment, 1471.90, delta=1.0)
        self.assertAlmostEqual(result.mortgage.total_interest, 113256.80, delta=200.0)
        self.assertAlmostEqual(result.mortgage.total_payments, 353256.80, delta=200.0)

        self.assertAlmostEqual(result.yearly_projections[0].principal_paydown, 9412.89, delta=20.0)
        self.assertAlmostEqual(result.yearly_projections[9].principal_paydown, 12892.18, delta=20.0)

        self.assertAlmostEqual(result.summary.total_principal_paydown, 240000.0, delta=1000.0)

    def test_300k_loan_at_4_percent_15_years(self):
        """Stored values for 300k loan at 4.0% over 15 years."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=0.0,
            interest_rate=4.0,
            loan_term_years=15,
            monthly_rent=2000.0,
            holding_period_years=15
        )

        result = self.calculator.analyze(input_data)

        self.assertAlmostEqual(result.mortgage.monthly_payment, 2319.06, delta=1.0)
        self.assertAlmostEqual(result.mortgage.total_interest, 117431.48, delta=200.0)
        self.assertAlmostEqual(result.mortgage.total_payments, 417431.48, delta=200.0)

        self.assertAlmostEqual(result.yearly_projections[0].principal_paydown, 16122.21, delta=20.0)
        self.assertAlmostEqual(result.yearly_projections[7].principal_paydown, 21321.84, delta=20.0)

        self.assertAlmostEqual(result.summary.total_principal_paydown, 300000.0, delta=1500.0)

    def test_180k_loan_at_3_percent_25_years(self):
        """Stored values for 180k loan at 3.0% over 25 years."""
        input_data = PropertyInvestmentInput(
            property_price=180000.0,
            down_payment=0.0,
            interest_rate=3.0,
            loan_term_years=25,
            monthly_rent=1200.0,
            holding_period_years=25
        )

        result = self.calculator.analyze(input_data)

        self.assertAlmostEqual(result.mortgage.monthly_payment, 913.58, delta=1.0)
        self.assertAlmostEqual(result.mortgage.total_interest, 94074.11, delta=200.0)
        self.assertAlmostEqual(result.mortgage.total_payments, 274074.11, delta=200.0)

        self.assertAlmostEqual(result.yearly_projections[0].principal_paydown, 5640.10, delta=20.0)
        self.assertAlmostEqual(result.yearly_projections[12].principal_paydown, 8080.48, delta=20.0)

        self.assertAlmostEqual(result.summary.total_principal_paydown, 180000.0, delta=300.0)


class TestPropertyInvestmentAnalysis(unittest.TestCase):
    """Test overall property investment analysis."""

    def setUp(self):
        self.calculator = FrenchPropertyInvestmentCalculator()

    def test_basic_property_investment(self):
        """Basic property investment calculation."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        self.assertEqual(result.summary.loan_amount, 240000.0)
        self.assertGreater(result.mortgage.monthly_payment, 0.0)
        self.assertEqual(len(result.yearly_projections), 10)
        self.assertGreater(result.summary.initial_investment, 60000.0)
        self.assertLess(result.summary.initial_investment, 70000.0)

    def test_french_fees_included(self):
        """French fees should be included in initial investment."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        loan_amount = 240000.0
        arrangement_fee = loan_amount * 0.01
        registration_fee = loan_amount * 0.015
        survey_fee = 750.0
        expected_initial = 60000.0 + arrangement_fee + registration_fee + survey_fee

        self.assertAlmostEqual(result.summary.initial_investment, expected_initial, places=2)

    def test_life_insurance_included(self):
        """Life insurance should be included in expenses."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=1
        )

        result = self.calculator.analyze(input_data)

        loan_amount = 240000.0
        expected_annual_insurance = loan_amount * 0.004

        self.assertAlmostEqual(
            result.yearly_projections[0].expenses.insurance,
            expected_annual_insurance,
            delta=expected_annual_insurance * 0.1
        )

    def test_rent_increases_annually(self):
        """Rental income should increase with rent_increase_annual."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=5,
            rent_increase_annual=2.0
        )

        result = self.calculator.analyze(input_data)

        for i in range(1, len(result.yearly_projections)):
            self.assertGreater(
                result.yearly_projections[i].rental_income,
                result.yearly_projections[i - 1].rental_income
            )

    def test_vacancy_rate_reduces_income(self):
        """Vacancy rate should reduce rental income."""
        input_without_vacancy = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=1,
            vacancy_rate=0.0
        )

        input_with_vacancy = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=1,
            vacancy_rate=10.0
        )

        result_without = self.calculator.analyze(input_without_vacancy)
        result_with = self.calculator.analyze(input_with_vacancy)

        self.assertLess(
            result_with.yearly_projections[0].rental_income,
            result_without.yearly_projections[0].rental_income
        )

    def test_equity_builds_over_time(self):
        """Equity should increase each year due to principal paydown."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        for i in range(1, len(result.yearly_projections)):
            self.assertGreater(
                result.yearly_projections[i].total_equity,
                result.yearly_projections[i - 1].total_equity
            )

        expected_final_equity = result.input.down_payment + result.summary.total_principal_paydown
        self.assertAlmostEqual(result.summary.final_equity, expected_final_equity, places=2)

    def test_cumulative_cash_flow_calculation(self):
        """Cumulative cash flow should be calculated correctly."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=3
        )

        result = self.calculator.analyze(input_data)

        expected_cumulative = -result.summary.initial_investment
        for projection in result.yearly_projections:
            expected_cumulative += projection.net_cash_flow
            self.assertAlmostEqual(projection.cumulative_cash_flow, expected_cumulative, places=2)

    def test_roi_calculation(self):
        """ROI should be calculated correctly."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        expected_roi = (result.summary.net_profit / result.summary.initial_investment) * 100.0
        self.assertAlmostEqual(result.summary.roi, expected_roi, places=2)

    def test_all_expense_categories(self):
        """All expense categories should be included."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=1,
            property_tax_annual=2400.0,
            hoa_monthly=150.0,
            maintenance_percent=1.0,
            management_fee_percent=8.0
        )

        result = self.calculator.analyze(input_data)
        first_year = result.yearly_projections[0].expenses

        self.assertGreater(first_year.mortgage, 0.0)
        self.assertEqual(first_year.property_tax, 2400.0)
        self.assertEqual(first_year.hoa, 150.0 * 12)
        self.assertEqual(first_year.maintenance, 300000.0 * 0.01)
        self.assertEqual(first_year.management, 1500.0 * 12 * 0.08)
        self.assertGreater(first_year.insurance, 0.0)

        expected_total = (first_year.mortgage + first_year.property_tax +
                         first_year.hoa + first_year.maintenance + first_year.management)
        self.assertAlmostEqual(first_year.total, expected_total, places=2)

    def test_break_even_detection(self):
        """Break-even year should be detected when cumulative cash flow becomes positive."""
        input_data = PropertyInvestmentInput(
            property_price=200000.0,
            down_payment=40000.0,
            interest_rate=3.0,
            loan_term_years=20,
            monthly_rent=2000.0,
            holding_period_years=15
        )

        result = self.calculator.analyze(input_data)

        self.assertIsNotNone(result.summary.break_even_year)


class TestInputValidation(unittest.TestCase):
    """Test input validation."""

    def setUp(self):
        self.calculator = FrenchPropertyInvestmentCalculator()

    def test_zero_property_price(self):
        """Zero property price should raise error."""
        with self.assertRaises(ValueError):
            self.calculator.analyze(PropertyInvestmentInput(
                property_price=0.0,
                down_payment=60000.0,
                interest_rate=3.5,
                loan_term_years=20,
                monthly_rent=1500.0,
                holding_period_years=10
            ))

    def test_down_payment_exceeds_price(self):
        """Down payment exceeding price should raise error."""
        with self.assertRaises(ValueError):
            self.calculator.analyze(PropertyInvestmentInput(
                property_price=300000.0,
                down_payment=350000.0,
                interest_rate=3.5,
                loan_term_years=20,
                monthly_rent=1500.0,
                holding_period_years=10
            ))

    def test_invalid_vacancy_rate(self):
        """Vacancy rate over 100% should raise error."""
        with self.assertRaises(ValueError):
            self.calculator.analyze(PropertyInvestmentInput(
                property_price=300000.0,
                down_payment=60000.0,
                interest_rate=3.5,
                loan_term_years=20,
                monthly_rent=1500.0,
                holding_period_years=10,
                vacancy_rate=150.0
            ))

    def test_net_profit_calculation(self):
        """Net profit should be total cash flow + equity - initial investment."""
        input_data = PropertyInvestmentInput(
            property_price=300000.0,
            down_payment=60000.0,
            interest_rate=3.5,
            loan_term_years=20,
            monthly_rent=1500.0,
            holding_period_years=10
        )

        result = self.calculator.analyze(input_data)

        expected_net_profit = (result.summary.total_cash_flow +
                              result.summary.final_equity -
                              result.summary.initial_investment)

        self.assertAlmostEqual(result.summary.net_profit, expected_net_profit, places=2)


if __name__ == '__main__':
    unittest.main()
