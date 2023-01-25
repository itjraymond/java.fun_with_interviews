package jent.fun_with_interviews.problems.anagrams;

import java.util.Arrays;

/**
 * Given two words (or two string), check if they are anogram i.e. they both have the same letters exactly
 * e.g. listen and silent, binary and brainy
 */
public class Anagrams {
    public static void main(String[] args) {
        String s1 = "listen";
        String s2 = "silent";
        System.out.println(s1 + " and " + s2 + " are anagrams? " + isAnagram(s1, s2));
        s1 = "binary";
        s2 = "brainy";
        System.out.println(s1 + " and " + s2 + " are anagrams? " + isAnagram(s1, s2));

        s1 = "binaryah";
        s2 = "brainyac";
        System.out.println(s1 + " and " + s2 + " are anagrams? " + isAnagram(s1, s2));

        s1 = "meats";
        s2 = "steam";
        System.out.println(s1 + " and " + s2 + " are anagrams? " + isAnagram(s1, s2));

        s1 = "bin";
        s2 = "ban";
        System.out.println(s1 + " and " + s2 + " are anagrams? " + isAnagram(s1, s2));

    }

    public static boolean isAnagram(String s1, String s2) {
        char[] cs1 = s1.toCharArray();
        Arrays.sort(cs1);
        char[] cs2 = s2.toCharArray();
        Arrays.sort(cs2);
        int comparison = Arrays.compare(cs1, cs2);
        return comparison == 0;
    }
}

