package jent.fun_with_interviews.problems.palindrome;

import java.math.BigInteger;
import java.util.stream.Stream;

public class FibonacciRecusiveAndStream {


    public static void main(String[] args) {

        FibonacciRecusiveAndStream f = new FibonacciRecusiveAndStream();
        f.fibUsingStreamAndPrint(8);

        System.out.println("Fib of 8 is " + f.fibUsingStream(8));

        //f.fibUsingStreamAndPrint(8);
//        f.fibUsingStreamAndPrint(0);
//        f.fibUsingStreamAndPrint(1);
//        f.fibUsingStreamAndPrint(2);
//        f.fibUsingStreamAndPrint(3);
//        f.fibUsingStreamAndPrint(5);
//        f.fibUsingStreamAndPrint(6);
//
//        f.fibUsingStreamAndPrint2(0);
//        f.fibUsingStreamAndPrint2(1);
//        f.fibUsingStreamAndPrint2(2);
//        f.fibUsingStreamAndPrint2(3);
//        f.fibUsingStreamAndPrint2(5);
//        f.fibUsingStreamAndPrint2(6);

        long n = 200;
        System.out.println("FIB of " + n + " is: " + f.fibUsingStream(n));
        System.out.println("FIB of " + n + " is: " + f.fibUsingStreamBigInt(n));
    }

    /**
     * Stream
     */
    public long fibUsingStreamAndPrint(long n) {
        System.out.println("///////////////////");
        Stream.iterate(
                new long[] {1,1},
                p -> new long[] {p[1], p[0] + p[1]}
        ).limit(n).forEach(p -> System.out.println("n=" + n + " : [" + p[0] + "," + p[1] + "]"));
        System.out.println("\\\\\\\\\\\\\\\\\\\\");
        return 0;
    }

    public long fibUsingStreamAndPrint2(long n) {
        System.out.println("-------------------");
        Stream.iterate(
                new long[] {0,1},
                p -> new long[] {p[1], p[0] + p[1]}
        ).limit(n).forEach(p -> System.out.println("n=" + n + " : [" + p[0] + "," + p[1] + "]"));
        System.out.println("******************");
        return 0;
    }

    public long fibUsingStream(long n) {
        long[] nth = Stream.iterate(
                new long[]{1, 1},
                p -> new long[]{p[1], p[0] + p[1]}
        ).limit(n).skip(n - 1).findFirst().get();

        return nth[0];
    }

    public BigInteger fibUsingStreamBigInt(long n) {
        BigInteger[] nth = Stream.iterate(
                new BigInteger[] {BigInteger.ZERO, BigInteger.ONE},
                p -> new BigInteger[] {p[1], p[0].add(p[1])}
        ).limit(n).skip(n - 1).findFirst().get();

        return nth[0];
    }
}
