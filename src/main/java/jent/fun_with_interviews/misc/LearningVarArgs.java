package jent.fun_with_interviews.misc;

import java.util.Arrays;

public class LearningVarArgs {

    // ss is of type String[] hence why we need to wrap it into Arrays.stream(ss) to convert it into a Stream<String>.
    public static void passMeVarArgs(String... ss) {
        Arrays.stream(ss).forEach(System.out::println);
    }

    public static void main(String[] args) {
        var ss = new String[]{"one","two","three"};
        passMeVarArgs(ss);
        // OR
        passMeVarArgs("one", "two", "three");
    }
}


