package jent.fun_with_interviews.problems.palindrome;

import java.util.Arrays;

public class PalindromeWithRecursion {

    public static void main(String[] args) {

        String p = "abcdefgfedcba";
        String notp = "abcdefgfedcbaa";
        // one liner
        System.out.println("Is '" + p + "' a palindrome? " + new StringBuilder(p).reverse().toString().equals(p));
        System.out.println("Is '" + notp + "' a palindrome? " + new StringBuilder(notp).reverse().toString().equals(notp));


        // Usage of char[] reverse(char[] cs) (see below)
        PalindromeWithRecursion pp = new PalindromeWithRecursion();
        char[] cs = new char[]{'a','b','c','d'};
        char[] rcs = pp.reverse(cs);
        String scs = new StringBuilder().append(cs).toString();
        String srcs = new StringBuilder().append(rcs).toString();

        System.out.println(scs + " has been reversed as " + srcs);
    }

    /**
     * In an interview, they may specify that they want you to use recursion i.e. they want to assess your recursive thinking ability
     */
    /**
     * This will nicely show your ability to recursion.  However, in Java, this is dangerous.  You need to point out
     * that because Java compiler is not able to compile TCE (Tail Call Elimination), we should never use recursion in Java.
     */
    private static String reverse(String s) {
        if (s == null) return null;
        if (s.isBlank()) return "";
        String head = s.substring(0,1);
        String tail = s.substring(1);
        return reverse(tail) + head;
    }

    /**
     * This will also show your skill ability for recursion but is "harder" to remember.
     */
    private char[] reverse(char[] cs) {
        if (cs == null) return null;
        if (cs.length == 0) return cs;
        char head = cs[0];
        char[] tail = Arrays.copyOfRange(cs, 1, cs.length);
        return concat(reverse(tail), head);
    }
    // [abcd]
    //  concat(reverse([bcd]) 'a')
    //  concat(concat(reverse([cd], 'b'), 'a')
    //  concat(concat(concat(reverse([d], 'c'), 'b'), 'a')
    //  concat(concat(concat(concat(reverse([]), 'd'), 'c'), 'b'), 'a')

    private char[] concat(char[] tail, char head) {
        return new StringBuilder().append(tail).append(head).toString().toCharArray();
    }
}
