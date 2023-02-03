package jent.fun_with_interviews.patterns.strategy;

/**
 * Strategy pattern
 *
 * The strategy implies an action of some sort.  In brief, it implies a function.
 *
 * It is understood that only and only one strategy is meant to be executed and that is the intent.
 * The strategy to be executed always depend on a predicate.
 */
public class OOPStrategyPattern {

    interface TextFormater {
        boolean filter(String text);
        String format(String text);
    }

    // different strtegy for formatting text

    static class PlainTextFormatter implements TextFormater {

        @Override
        public boolean filter(String text) {
            return true;
        }

        @Override
        public String format(String text) {
            return text;
        }
    }

    static class ErrorTextFormatter implements TextFormater {

        @Override
        public boolean filter(String text) {
            return text.toUpperCase().contains("ERROR");
        }

        @Override
        public String format(String text) {
            return String.format("ERROR - %s", text.toUpperCase());
        }
    }

    static class ShortenTextFormatter implements TextFormater {
        int boundary = 20;
        @Override
        public boolean filter(String text) {
            return text.length() > boundary;
        }

        @Override
        public String format(String text) {
            return String.format("SHORTENED - %s...",text.substring(0,boundary));
        }
    }

    // Note: This should be the object where we inject (constructor) with the strategy. TODO
    static class TextEditor {
        private final TextFormater textFormater;

        public TextEditor(TextFormater textFormater) {
            this.textFormater = textFormater;
        }

        public void publishText(String s) {
            if (textFormater.filter(s)) {
                System.out.println(textFormater.format(s));
            }
        }
    }

    public static TextFormater plainTextFormatter = new PlainTextFormatter();
    public static TextFormater errorTextFormatter = new ErrorTextFormatter();
    public static TextFormater shortTextFormatter = new ShortenTextFormatter();

    public static TextFormater getStrategy(String s) {
        if (errorTextFormatter.filter(s)) return errorTextFormatter;
        if (shortTextFormatter.filter(s)) return shortTextFormatter;
        return plainTextFormatter;
    }

    public static void main(String[] args) {
        String s1 = "This is a long message with an error in it";
        TextFormater textFormater = getStrategy(s1);
        System.out.println(textFormater.format(s1));

        String s2 = "This is a long message with an something in it";
        textFormater = getStrategy(s2);
        System.out.println(textFormater.format(s2));

        String s3 = "just a message";
        textFormater = getStrategy(s3);
        System.out.println(textFormater.format(s3));

    }


}
