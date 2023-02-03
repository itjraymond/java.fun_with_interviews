package jent.fun_with_interviews.misc;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Numbers {

    public static void main(String[] args) {
        Long n = 35823013L;
        System.out.println("Digits sorted for " + n + " is: " + orderDigitsOfNumber.apply(n));

        System.out.println(dosomething.apply(5, 12).apply("hello", String::toUpperCase));

        System.out.println(fn.apply(5).apply(12).apply("hello").apply(String::toUpperCase));


    }

    // long -> long
    // example: 42145 -> 54421 (take each individual digits and sort it descending); return null if n is -negative
    static Function<Long, Long> orderDigitsOfNumber = n -> {
        if (n<=0) return null;
        var ds = new ArrayList<Long>();
        while (n > 0) {
            ds.add(n%10);
            n /= 10;
        }
        ds.sort( (a,b) -> b.compareTo(a)); // descending
        ds.forEach(System.out::println);
        return ds.stream().reduce((a, b) -> a * 10 + b).orElse(null);
    };

    static BiFunction<Integer, Integer, BiFunction<String, UnaryOperator<String>, String>> dosomething = (cycle, size) -> (s, op) -> {
        StringBuilder s1 = new StringBuilder();
        for (int i=0; i<(size*cycle); i++) {
            s1.append(s).append(" ").append(i).append(" on cycle ").append(i%cycle).append("\n");
        }
        return s1.toString();
    };

    static Function<Integer, Function<Integer, Function<String, Function<UnaryOperator<String>, String>>>> fn =
            cycle -> size -> s -> op -> {
                StringBuilder b = new StringBuilder();
                for (int i=0; i<cycle*size; i++) {
                    switch (i%cycle) {
                        case 0: b.append("at cycle 0 on iteration").append(i).append("\n"); break;
                        case 1: b.append("at cycle 1 on iteration").append(i).append("\n"); break;
                        case 2: b.append("at cycle 2 on iteration").append(i).append("\n"); break;
                        case 3: b.append("at cycle 3 on iteration").append(i).append("\n"); break;
                        case 4: b.append("at cycle 4 on iteration").append(i).append("\n"); break;
                        default:b.append("default\n");
                    }
                }
                return  b.toString();
            };

}
