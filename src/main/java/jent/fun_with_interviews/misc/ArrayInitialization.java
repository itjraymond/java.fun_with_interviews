package jent.fun_with_interviews.misc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ArrayInitialization {

    public static void main(String[] args) {

        String[] ss = new String[] {"One", "Two", "Three"};

        Integer[] is = new Integer[] {1,2,3};

        int[] pis = new int[] {1,2,3};

        Ballon[] bs = new Ballon[] {
                new Ballon(5,"red"),
                new Ballon(4,"blue"),
                new Ballon(8,"pink")
        };

        // Here is a cool way to create a String[]
        String[] cool = "One,two,three,four,five,six".split(",");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ballon {
        private int size;
        private String color;
    }
}
