package jent.fun_with_interviews.problems.dates;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class Dates {

    public static void main(String[] args) {

    }

    // instant -> String
    static Function<String, Function<Instant,String>> formatInstant = format -> date -> DateTimeFormatter.ofPattern(format).format(date);

}
