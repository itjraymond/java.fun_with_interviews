package jent.fun_with_interviews.streams;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamFun {

    private static Stream<String> s1 = Stream.of("1","2","3"); // CANNOT have "var s1 ..." here
    private static Stream<String> s2 = List.of("4","5","6").stream();
    // private static var ss = "string"; // Does not compile


    public static void main(String[] args) {
        var stream = Stream.of("1");

        // CONCAT 2 streams
        var s3 = Stream.concat(s1,s2);

        // Integer stream
        IntStream range = IntStream.range(0, 10);

        // Stream with random ints (GENERATOR means infinite stream)
        Stream.generate(() -> ThreadLocalRandom.current().nextInt()).limit(10);

        // Stream iterate (Also known as infinite stream)
        Stream.iterate(BigDecimal.TEN, a -> a.pow(2)).limit(5);
    }
}
