package jent.fun_with_interviews.misc;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WordCount {

    public static void main(String[] args) {
        // Note 'red' and 'red.' are counted separatly because we would need to add a filter function to remove special chars.
        String s = "Once upon a time, there was a red ridding hood who loved red.";
        System.out.println("Word count: " + wordCount(s)); // 13

        String s1 = "If you only new, I would have told you!!? However, sometime things are like that; otherwise it is not. #told-you-so!";
        System.out.println("Word count without punct: " + wordCountWithoutPunct.apply(s1));

        Map<String,Long> wordCounts = countWordsOccurence(s);
        wordCounts.forEach((word, count) -> System.out.println("'" + word + "' occurs " + count + " times."));
        // OR
        wordCounts.forEach((key, value) -> System.out.println("'" + key + "' occurs " + value + " times."));
        // OR
        wordCounts.entrySet().forEach(e -> System.out.println("'" + e.getKey() + "' occurs " + e.getValue() + " times."));
        // OR
        wordCounts.entrySet().stream().forEach(e -> System.out.println("'" + e.getKey() + "' occurs " + e.getValue() + " times."));

    }

    public static int wordCount(String s) {
        return s.split(" ").length;
    }

    public static Function<String, Long> wordCountFn = s -> (long) s.split(" ").length;
    public static Function<String, String> removePunctFn = s -> s.replaceAll("\\p{Punct}", "");
    public static Function<String, Long> wordCountWithoutPunct = s -> removePunctFn.andThen(wordCountFn).apply(s);

    public static Map<String, Long> countWordsOccurence(String s) {
        return Arrays.stream(s.replaceAll("\\p{Punct}","").split(" ")).collect(
                Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                )
        );
    }
}
