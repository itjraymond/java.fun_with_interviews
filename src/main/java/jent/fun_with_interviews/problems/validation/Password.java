package jent.fun_with_interviews.problems.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Write a function that takes a String and applies a sequence of validation.
 * <p>
 * Password Validation:
 * 1. The String password must contain at least one upper case letter (one Capital letter). Char range: [65-90]
 * 1b. The String password must contain at least one lower case letter. Char range: [97-122]
 * 2. The String password must contain at least one number [0-9]. Char range: [48-57]
 * 3. The String password must contain at least one punctuation i.e. \\p{P} or Char range: [33-47] or [58-64] or [91-96] or [123-126]
 * 4. The String must have a length greater or equal than 8 chars
 * 5. The String must have a length less or equal than 32 chars
 * 6. The String cannot have a chars that appears twice in a row i.e. no two identical subsequent chars. Case sensitive i.e. J != j
 */
public class Password {

    public static void main(String[] args) {
        String password = "abc";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "abcdefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aBcdefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aB2cdefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aB2!cdefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aB2!cdeefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aaB2!cdefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aB2!cdefghijj";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aB2!cdefghiJj";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aB2#cdefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));
        password = "aB2+cdefghij";
        System.out.println("Password " + password + " is valid? " + isPasswordValid(password));


        String pwd = "abcd";
        System.out.println("Password " + pwd + " is valid? " + isValid.apply(new Validation(pwd, true)).getIsValid());
        pwd = "aBcd";
        System.out.println("Password " + pwd + " is valid? " + isValid.apply(new Validation(pwd, true)).getIsValid());
        pwd = "aB2cd";
        System.out.println("Password " + pwd + " is valid? " + isValid.apply(new Validation(pwd, true)).getIsValid());
        pwd = "ab2cd";
        System.out.println("Password " + pwd + " is valid? " + isValid.apply(new Validation(pwd, true)).getIsValid());
        pwd = "2Joss";
        System.out.println("Password " + pwd + " is valid? " + isValid.apply(new Validation(pwd, true)).getIsValid());
        pwd = "Password123";
        System.out.println("Password " + pwd + " is valid? " + isValid.apply(new Validation(pwd, true)).getIsValid());
        pwd = "Passw0rd123";
        System.out.println("Password " + pwd + " is valid? " + isValid.apply(new Validation(pwd, true)).getIsValid());

        // using a better api
        pwd = "Password123";
        System.out.println("Password " + pwd + "is valid? " + isValidPwd.apply(pwd));
        pwd = "Passw0rd123";
        System.out.println("Password " + pwd + "is valid? " + isValidPwd.apply(pwd));

    }

    public static boolean isPasswordValid(String password) {
        return Stream.of(
                        isWithinAcceptableLength,
                        containsAtLeastOneCapitalLetter,
                        containsAtLeastOneLowerCaseLetter,
                        containsAtLeastOneDigit,
                        containsAtLeastOnePunctuationCharUsingRegX,
                        containsNoIdenticalSubsequentChar
                ).map(f -> f.apply(password))
                .filter(v -> !v)
                .findFirst() // find first broken rule
                .orElse(true); // if no rules are broken then we are good
    }

    private static Function<String, Boolean> containsAtLeastOneCapitalLetter = s -> hasCharInRange(s, 65, 90);

    private static Function<String, Boolean> containsAtLeastOneLowerCaseLetter = s -> hasCharInRange(s, 97, 122);

    private static Function<String, Boolean> containsAtLeastOneDigit = s -> hasCharInRange(s, 48, 57);

    private static Function<String, Boolean> containsAtLeastOnePunctuationCharUsingASCIIrange = s ->
            hasCharInRange(s, 33, 47) ||
                    hasCharInRange(s, 58, 64) ||
                    hasCharInRange(s, 91, 96) ||
                    hasCharInRange(s, 123, 126);

    private static Function<String, Boolean> containsAtLeastOnePunctuationCharUsingRegX = s ->
            s.length() != s.replaceFirst("\\p{Punct}", "").length();

    private static Function<String, Boolean> isWithinAcceptableLength = s -> s.length() >= 8 && s.length() <= 32;

    private static Function<String, Boolean> containsNoIdenticalSubsequentChar = s -> {
        char[] cs = s.toCharArray();
        for (int i=1; i<cs.length; i++) {
            if (cs[i-1] == cs[i]) return false;
        }
        return true;
    };

    private static boolean hasCharInRange(String s, int lowerbound, int upperbound) {
        char[] cs = s.toCharArray();
        for (char c : cs) {
            if (c >= lowerbound && c <= upperbound)
                return true;
        }
        return false;
    }

    /**
     * Using Function composition.  I find it not as clear as the above.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class
    Validation {
        private String stringToValidate;
        private Boolean isValid;
    }

    private static Function<Validation, Validation> containsAtLeastOneUpperCaseChar = sv -> new Validation(
            sv.stringToValidate,
            sv.isValid && hasCharInRange(sv.stringToValidate, 65, 90)
    );

    private static Function<Validation, Validation> containsAtLeastOneLowerCaseChar = sv -> new Validation(
            sv.stringToValidate,
            sv.isValid && hasCharInRange(sv.stringToValidate, 97, 122)
    );

    private static Function<Validation, Validation> containsAtLeastOneDigitChar = sv -> new Validation(
            sv.stringToValidate,
            sv.isValid && hasCharInRange(sv.stringToValidate, 48, 57)
    );

    private static Function<String[], Function<Validation, Validation>> cannotContainWords = words -> sv -> new Validation(
            sv.stringToValidate,
            sv.isValid && Arrays.stream(words).noneMatch(invalidStr -> sv.stringToValidate.toLowerCase().contains(invalidStr))
    );

    // Lets try to compose
    private static Function<Validation,Validation> isValid = sv ->
            containsAtLeastOneLowerCaseChar
            .andThen(containsAtLeastOneUpperCaseChar)
            .andThen(containsAtLeastOneDigitChar)
            .andThen(cannotContainWords.apply(new String[]{"password","joss"}))
            .apply(sv);

    // Could make the interface a little better
    private static Function<String,Boolean> isValidPwd = s -> isValid.apply(new Validation(s,true)).getIsValid();

}
