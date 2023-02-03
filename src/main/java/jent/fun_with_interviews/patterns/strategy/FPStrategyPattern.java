package jent.fun_with_interviews.patterns.strategy;

import java.util.function.Function;
import java.util.function.Supplier;

public class FPStrategyPattern {

    static Function<String, String> plainTextFormatter = Function.identity();
    static Function<String, String> errorTextFormatter = s -> String.format("ERROR - %s", s.toUpperCase());
    static Function<String, String> shortTextFormatter = s -> String.format("SHORTHENED - %s...", s);

    // Could this be an improvement?  This mean we don't have to pass the s1 string twice because it is closed over s
    static Function<String, Supplier<String>> errorTextFormatterExecutor = s -> () -> errorTextFormatter.apply(s);
    static Function<String, Supplier<String>> shortTextFormatterExecutor = s -> () -> shortTextFormatter.apply(s);
    static Function<String, Supplier<String>> plainTextFormatterExecutor = s -> () -> plainTextFormatter.apply(s);

    // FYI, we could do this in one:
    static Function<String, Supplier<String>> error = s -> () -> String.format("ERROR - %s", s.toUpperCase());

    static Function<String, String> getStrategy(String s) {
        if (s.toUpperCase().contains("ERROR")) return errorTextFormatter;
        if (s.length() > 20) return shortTextFormatter;
        return plainTextFormatter;
    }

    static Supplier<String> getStrategyExecutor(String s) {
        if (s.toUpperCase().contains("ERROR")) return errorTextFormatterExecutor.apply(s);
        if (s.length() > 20) return shortTextFormatterExecutor.apply(s);
        return plainTextFormatterExecutor.apply(s);
    }

    public static void main(String[] args) {
        String s1 = "just a message";
        System.out.println(getStrategy(s1).apply(s1));

        // Second way (better?)
        String s2 = "just an Error";
        System.out.println(getStrategyExecutor(s2).get());
    }
}
