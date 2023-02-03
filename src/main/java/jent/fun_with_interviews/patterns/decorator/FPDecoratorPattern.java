package jent.fun_with_interviews.patterns.decorator;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

/**
 * Since Decorator pattern is  essentially function composition, we will create a function for
 * each type of TaxCalculator.  All of them is double -> double hence Function<Double,Double>
 *     or DoubleUnaryOperator
 *
 * Essentially, the decorator pattern simply gives a chance to a variety of function to apply a change to the value.
 */
public class FPDecoratorPattern {

    static DoubleUnaryOperator deductGeneralTax = amt -> amt - amt * 0.1; // 10%
    static DoubleUnaryOperator deductRegionalTax = amt -> amt - amt * 0.04; // 4%
    static DoubleUnaryOperator deductHealthInsurance = amt -> amt - amt * 0.07; // 7%

    static DoubleUnaryOperator deductRrspContribution = amt -> amt - 100.0;

    static DoubleUnaryOperator montlySalary = grossYearlySalary -> grossYearlySalary / 12;

    static DoubleUnaryOperator taxCalculator(DoubleUnaryOperator... fs) {
        return Stream.of(fs).reduce(DoubleUnaryOperator.identity(), DoubleUnaryOperator::andThen);
    };

    public static void main(String[] args) {
        double montlySalaryAfterDeductions = taxCalculator(
                montlySalary,
                deductGeneralTax,
                deductRegionalTax,
                deductHealthInsurance,
                deductRrspContribution
        ).applyAsDouble(30000.0);
        System.out.println("Monthly salary after all deductions: " + montlySalaryAfterDeductions);
    }
}
