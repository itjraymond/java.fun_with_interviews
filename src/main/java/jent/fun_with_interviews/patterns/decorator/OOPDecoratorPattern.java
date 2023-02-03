package jent.fun_with_interviews.patterns.decorator;

/**
 * Decorator pattern
 *
 * Essentially, the decorator pattern is a small collection of functions (computation) that is selected to be
 * executed sequencially.  The collection and order can be determined at run time upon some predicate.
 * This pattern also kind of make the assumption that the output Type of one of those function must match the
 * input Type of the second one in the sequence.
 *
 * Note that in OOP, we need to wrap those function into objects. In FP, this pattern correspond to function composition.
 */
public class OOPDecoratorPattern {

    interface SalaryCalculator {
        double calculate(double grossAnnual);
    }
    public static abstract class AbstractTaxDecorator implements SalaryCalculator {
        private final SalaryCalculator salaryCalculator;

        // We want to make sure we don't use the default constructor. However, the compiler won't know and will required us to make sure salaryCalculator is not null.
        // Hence better not do this.
//        private AbstractTaxDecorator(){
//            this.salaryCalculator = null;
//        }

        public AbstractTaxDecorator(SalaryCalculator salaryCalculator) {
            this.salaryCalculator = salaryCalculator;
        }

        protected abstract double applyTax(double salary);

        @Override
        public final double calculate(double grossAnnual) {
             return applyTax(salaryCalculator.calculate(grossAnnual));
        }
    }

    public static class DefaultSalaryCalculator implements SalaryCalculator {
        @Override
        public double calculate(double grossAnnual) {
            return grossAnnual / 12;
        }
    }

    // Now we need few TaxDecorator
    public static class GeneralTaxDecorator extends AbstractTaxDecorator {
        // Let see if I can create a GeneralTaxDecorator using default constructor
        // public GeneralTaxDecorator(){}           // WON'T COMPILE
        // public GeneralTaxDecorator(){ super(); } // WON'T COMPILE
        // public GeneralTaxDecorator(){ super(new DefaultSalaryCalculator()); } // compile but have to provide some
                                                                                 // default salary calculator which makes
                                                                                 // no sense for the decorator pattern.

        public GeneralTaxDecorator(SalaryCalculator salaryCalculator) {
            super(salaryCalculator);
        }
        @Override
        protected double applyTax(double salary) {
            return salary - (salary * 0.10); // 10% general tax
        }
    }

    public static class RegionalTaxDecorator extends AbstractTaxDecorator {
        public RegionalTaxDecorator(SalaryCalculator salaryCalculator) {
            super(salaryCalculator);
        }

        @Override
        protected double applyTax(double salary) {
            return salary - (salary * 0.04); // 4% regional tax
        }
    }

    public static class HealthInsuranceDecorator extends AbstractTaxDecorator {
        public HealthInsuranceDecorator(SalaryCalculator salaryCalculator) {
            super(salaryCalculator);
        }

        @Override
        protected double applyTax(double salary) {
            return salary - (salary * 0.07); // 7% health insurance cost
        }
    }

    // The construction of the decorator pattern. Notice it is build in reverse order thus must read it in reverse.
    // but the execution is done starting with highest level i.e. HealthInsuranceDecorator in this case.
    // What will happen is that:
    // HealthInsuranceDecorator.calculate(30_000.00) is called which calls the applyTax of his i.e. 7% on the salaryCalculator.calculate(30_000.00)
    // Then RegionalTaxDecorator (WOW, confusing...)
    // The GeneralTaxDecorator
    public static void main(String[] args) {

        System.out.println("$" +
        new HealthInsuranceDecorator(
                new RegionalTaxDecorator(
                        new GeneralTaxDecorator(
                                new DefaultSalaryCalculator()
                        )
                )
        ).calculate(30000.00) + " per month");
    }
}
