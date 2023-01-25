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

        Map<String,Long> wordCounts = countWordsOccurence(s);
        wordCounts.entrySet().stream().forEach(e -> System.out.println("'" + e.getKey() + "' occurs " + e.getValue() + " times."));
    }

    public static int wordCount(String s) {
        return s.split(" ").length;
    }

    public static Map<String, Long> countWordsOccurence(String s) {
        Map<String, Long> collect = Arrays.stream(s.split(" ")).collect(
                Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                )
        );
        return collect;
    }
}
