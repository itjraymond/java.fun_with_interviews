package jent.fun_with_interviews.patterns.strategy;

public class OOPRecordStrategyPattern {

    sealed interface TextFormatter  permits PlainTextFormatter, ErrorTextFormatter, ShortTextFormatter {
        boolean test(String s);
        String publish(String s);
    }

    record PlainTextFormatter() implements TextFormatter {

        @Override
        public boolean test(String s) {
            return true;
        }

        @Override
        public String publish(String s) {
            return s;
        }
    }

    record ErrorTextFormatter() implements TextFormatter {
        @Override
        public boolean test(String s) {
            return s.toUpperCase().contains("ERROR");
        }

        @Override
        public String publish(String s) {
            return String.format("ERROR - %s", s.toUpperCase());
        }
    }

    record ShortTextFormatter() implements TextFormatter {

        private static final int boundary = 20;

        @Override
        public boolean test(String s) {
            return s.length() > boundary;
        }

        @Override
        public String publish(String s) {
            return String.format("SHORTENED - %s...", s.substring(0,boundary));
        }
    }

    public static TextFormatter plainTextFormatter = new PlainTextFormatter();
    public static TextFormatter errorTextFormatter = new ErrorTextFormatter();
    public static TextFormatter shortTextFormatter = new ShortTextFormatter();

    /**
     * Only one strategy is usually executed/needed
     */
    public  static TextFormatter getStrategy(String s) {
        if (errorTextFormatter.test(s)) return errorTextFormatter;
        if (shortTextFormatter.test(s)) return shortTextFormatter;
        return plainTextFormatter;
    }

    public static void main(String[] args) {
        String s1 = "Once uppon a time there was an error";
        TextFormatter textFormatter = getStrategy(s1);
        System.out.println(textFormatter.publish(s1));
    }

}
