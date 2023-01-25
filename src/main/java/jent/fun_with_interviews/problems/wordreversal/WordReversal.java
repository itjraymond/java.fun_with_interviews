package jent.fun_with_interviews.problems.wordreversal;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Problem description
 * String -> String
 * For a given string made of words, return them in reverse order
 * Example:
 * "Dog wanted to go outside" -> "Outside go to wanted dog"
 */
public class WordReversal {

    static  String sentence = "The dog wanted, perhaps, to go outside!?";

    public static void main(String[] args) {
        String[] words = sentence.split(" ");
        String[] revWords = reverse(words);
        revWords[0] = revWords[0].substring(0,1).toUpperCase() + revWords[0].substring(1);
        revWords[revWords.length-1] = revWords[revWords.length-1].toLowerCase();
        for(String word : revWords) {
            System.out.println(word);
        }

        UnaryOperator<String[]> capitalizeFirstWord = sarr -> {
            sarr[0] = sarr[0].substring(0, 1).toUpperCase() + sarr[0].toLowerCase().substring(1);
            return sarr;
        };
        UnaryOperator<String[]> lowerCaseLastWord = sarr -> {
            sarr[sarr.length-1] = sarr[sarr.length-1].toLowerCase();
            return sarr;
        };
        UnaryOperator<String[]> removePunctuation = sarr -> {
            for(int i=0; i<sarr.length; i++) {
                sarr[i] = sarr[i].replaceAll("\\p{P}","");
            }
            return sarr;
        };
        String[] revertedWords = reverse(words, List.of(capitalizeFirstWord, lowerCaseLastWord, removePunctuation));
        for(String word : revertedWords) {
            System.out.println(word);
        }
        System.out.println(Arrays.stream(revertedWords).collect(Collectors.joining(" ")));
    }

    public static String[] reverse(String[] words) {
        String[] clone = words.clone();
        final int size = clone.length;
        for (int i=0; i<size/2; i++) {
            String s = clone[size-1-i];
            clone[size-1-i] = clone[i];
            clone[i] = s;
        }
        return clone;
    }

    public static String[] reverse(String[] words, List<UnaryOperator<String[]>> ops) {
        String[] clone = words.clone();
        final int size = clone.length;
        for (int i=0; i<size/2; i++) {
            String s = clone[size-1-i];
            clone[size-1-i] = clone[i];
            clone[i] = s;
        }
        ops.forEach( op -> op.apply(clone));
        return clone;
    }

}
