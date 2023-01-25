package jent.fun_with_interviews.problems.fizzbuzz;

import java.util.stream.IntStream;

/**
 * Problem description
 * 1. Loop 1 to 100 and print the number on a new line.
 * Example:
 *   > 1
 *   > 2
 *   > 3...
 *
 * 2. Print "Fizz" instead of the number if the number is a multiple of 3
 * Example:
 *   > 1
 *   > 2
 *   > Fizz
 *   > 4...
 *
 * 3. Print "Buzz" instead of the number if the number is a multiple of 5
 * Example:
 *   > 1
 *   > 2
 *   > Fizz
 *   > 4
 *   > Buzz
 *   > Fizz
 *   > 7...
 *
 * 4. Print "FizzBuzz" instead of the number if the number is a multiple of 3 and 5
 *   > 1... you got the idea :-)
 */
public class FizzBuzz {


    public static void main(String[] args) {
//        for (int i = 0; i<100; i++) {
//            System.out.println(getOutput(i));
//        }

        IntStream.iterate(0, i -> i + 1)
                .limit(100)
                .mapToObj(FizzBuzz::getOutput)
                .forEach(System.out::println);

    }

    public static boolean isMultipleOf3(int n) {
        return n % 3 == 0;
    }

    public static boolean isMultipleOf5(int n) {
        return n % 5 == 0;
    }

    public static boolean isMultipleOf3And5(int n) {
        return isMultipleOf3(n) && isMultipleOf5(n);
    }

    public static String getOutput(int n) {
        if (isMultipleOf3And5(n)) {
            return "FizzBuzz";
        } else if (isMultipleOf3(n)) {
            return "Fizz";
        } else  if (isMultipleOf5(n)) {
            return "Buzz";
        } else {
            return Integer.toString(n);
        }
    }
}
