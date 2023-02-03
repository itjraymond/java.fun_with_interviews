package jent.fun_with_interviews.comparison;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparisonApp {

    public static void main(String[] args) {
        List<Ball> balls = Arrays.asList(new Ball(5,"red"), null, new Ball(3,"blue"), new Ball(8,"black"), null, new Ball(1, "green"));
        List<Ball> bs = new ArrayList<Ball>(balls.stream().filter(b -> b != null).toList());

        // Collections.sort(bs) cannot have the List<Ball> (i.e. bs) to contains null; otherwise throw exception
        // Also, bs "has to" be mutable
        Collections.sort(bs);

        bs.stream().forEach(b -> System.out.println(b.getSize()));

        var ballons = Arrays.asList(new Ballon(5, "red"), null, new Ballon(3, "blue"), new Ballon(8, "black"), null, new Ballon(1, "green"));
        var ballonsss = new ArrayList<Ballon>(){{
            add(new Ballon(5, "red"));
            add(null);
            add(new Ballon(3, "blue"));
            add(new Ballon(8, "black"));
            add(null);
            add(new Ballon(1, "green"));
        }};

        List<Ballon> ballonss = new ArrayList<Ballon>(ballons.stream().filter(b -> b != null).toList());

        // The Comparator is passed as a lambda.
        Collections.sort(ballonss, (Ballon b1, Ballon b2) -> b1.getSize() - b2.getSize());

        // same as above but with anonymous class.
        Collections.sort(
                ballonss,
                new Comparator<Ballon>() {
                    @Override
                    public int compare(Ballon o1, Ballon o2) {
                        return o1.getSize() - o2.getSize();
                    }
                }
        );

        class SizeComparator implements Comparator<Ballon> {
            @Override
            public int compare(Ballon b1, Ballon b2) {
                return b1.getSize() - b2.getSize();
            }
        }

        Collections.sort(
                ballonss,
                new SizeComparator()
        );

        // OR we could ...
        Comparator<Ballon> colorComparator = new Comparator<Ballon>() {
            @Override
            public int compare(Ballon o1, Ballon o2) {
                return o1.getColor().compareTo(o2.getColor());
            }
        };

        Collections.sort(
                ballonss,
                colorComparator
        );

        // Collections.sort mutate the original "list"
        // Let see a different sort which does not mutate the original but return a new sorted list
        Arrays.asList(5,8,4,7,1,9,3,2,6).stream().sorted().forEach(System.out::println); // sort natural order 1,2,3,...

        // we could also use immutable list, lets add reverse order
        List.of(5,8,4,7,1,9,3,2,6).stream().sorted(Collections.reverseOrder()).forEach(System.out::println); // sort natural reverse order

        // Using a stream of Ballon with comparable and sort by size
        List.of(
                new Ballon(5, "red"),
                new Ballon(3, "bleu"),
                new Ballon(9, "green"),
                new Ballon(4, "black")
        ).stream().sorted((b1, b2) -> b1.getSize() - b2.getSize()).forEach(System.out::println);

        // Using a stream of Ballon with comparable and sort by color
        List.of(
                new Ballon(5, "red"),
                new Ballon(3, "bleu"),
                new Ballon(9, "green"),
                new Ballon(4, "black")
        ).stream().sorted((b1, b2) -> b1.getColor().compareTo(b2.getColor())).forEach(System.out::println);

    }

    /**
     * Some observation:  Because the comparison involve "this" pointer, having an ArrayList<Ball> cannot contains null entries.
     * See above filtering the null out. i.e. b1 and b2 cannot be null otherwise bx.getColor() will result in NPE.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Ball implements Comparable<Ball> {
        private int size;
        private String color;

        @Override
        public int compareTo(Ball ball) {
            if (ball == null) {
                return 1; // return null first
            }
            return this.size - ball.getSize();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Ballon {
        private int size;
        private String color;
        @Override
        public String toString() {
            return "{ballon: { size: " + this.size + ", color: " + this.color + "}}";
        }
    }


}
