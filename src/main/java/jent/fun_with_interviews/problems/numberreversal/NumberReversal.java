package jent.fun_with_interviews.problems.numberreversal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 230.056 -> 650.032
 * 230.05 -> 50.032 or 500.32?
 */
public class NumberReversal {

    public static void main(String[] args) {

        Double d = 230.05;
        String s = d.toString();
        System.out.println(s);

        StringBuilder reverse = new StringBuilder(s).reverse();
        System.out.println(reverse);

        int indexOfDot = s.indexOf(".");
        String s2 = s.replaceFirst("\\.", "");
        StringBuilder s2Rev = new StringBuilder(s2).reverse();
        s2Rev.insert(indexOfDot, '.');
        System.out.println(s2Rev);

        // SWAP left and right side of dot:  "230.05" -> "05.230"
        String[] split = s.split("\\.");
        String pLeft = split[0];
        String pRigth = split[1];
        System.out.println(pRigth + "." + pLeft);
        // then reverse each part and re-construct it

        // Find highest Product of two number in array: (here 10 x 9 = 90)
        int[] numbers = new int[]{5,3,2,10,1,9,4};

        Arrays.sort(numbers);
        int max1 = numbers[numbers.length - 1];
        int max2 = numbers[numbers.length - 2];
        System.out.println("Product: " + max1*max2); // Product: 90
    }

}
