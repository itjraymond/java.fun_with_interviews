package jent.fun_with_interviews.problems.pangrams;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * if a string contains all the 26 letters of the alphabet.
 */
public class Pangrams {

    public static void main(String[] args) {

        String s = "The quick brown fox jumps over the lazy dog";
        System.out.println("is the sentence '" + s + "' an Pangrams: " + isPangrams(s));
        System.out.println("'" + s + "' is a perfect pangram? " + isPerfectPangrams(s));
        s = "Two driven jocks help fax my big quiz";
        System.out.println("is the sentence '" + s + "' an Pangrams: " + isPangrams(s));
        System.out.println("'" + s + "' is a perfect pangram? " + isPerfectPangrams(s));
        s = "aBcDeFFffGhijKlmMnoPPPqrssstuUvwxyz";
        System.out.println("is the sentence '" + s + "' an Pangrams: " + isPangrams(s));
        System.out.println("'" + s + "' is a perfect pangram? " + isPerfectPangrams(s));
        s = "aBcDeFGhijKlMnoPqrstuvwxyz";
        System.out.println("is the sentence '" + s + "' an Pangrams: " + isPangrams(s));
        System.out.println("'" + s + "' is a perfect pangram? " + isPerfectPangrams(s));
    }

    public static boolean isPangrams(String s) {
        return s.toUpperCase()
                .chars()
                .filter(character -> character >= 'A' && character <= 'Z')
                .mapToObj(c -> (char) c)
                .collect(Collectors.toSet())
                .stream().count() == 26L;
    }

    public static boolean isPerfectPangrams(String s) {
        Map<Character, Long> alphas = s.toUpperCase()
                .chars()
                .filter(character -> character >= 'A' && character <= 'Z')
                .mapToObj(c -> (char) c)
                .collect(groupingBy(Function.identity(), counting()));

        boolean all26Alphas = alphas.size() == 26;
        boolean allOccursOnce = alphas.values().stream().allMatch(c -> c == 1);
        return all26Alphas && allOccursOnce;
    }


}
