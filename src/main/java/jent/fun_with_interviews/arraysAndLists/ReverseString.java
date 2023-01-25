package jent.fun_with_interviews.arraysAndLists;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * NOTE: List.of(...) creates an unmutable list (as of JDK 11?).  Hence cannot use such immutable list with the
 * Collections.reverse(immutableList).
 * If want to use Collections.reverse(mutableList), we need to create a new ArrayList<String> from the immutable one
 * see below.
 *
 * Mostly taken from: https://www.techiedelight.com/reverse-sequential-stream-java/
 */
public class ReverseString {

    public static void main(String[] args) {
        ReverseString r = new ReverseString();
        // Check if the string is a palindrom
        String p = "abcdefgfedcba";
        String s = "abcdefabcdef";

        String pr = r.reverseUsingStringBuilder(p);
        System.out.println("Is string '" + p + "' a palindrom: " + p.equals(pr));

        String sr = r.reverseUsingStringBuilder(s);
        System.out.println("Is string '" + s + "' a palindrom: " + s.equals(sr));

        String pr2 = r.reverseUinsgCollectorOf(p);
        System.out.println("Is string '" + p + "' a palindrom: " + p.equals(pr2));

        String sr2 = r.reverseUinsgCollectorOf(s);
        System.out.println("Is string '" + s + "' a palindrom: " + s.equals(sr2));

        // for testing if a String is a palindrom, it can be done in one line.
        String palindrom = "palindrom mordnilap";
        System.out.println("Is '" + palindrom + "' a palindrom? " + new StringBuilder(palindrom).reverse().toString().equals(palindrom));

        String revStr = Arrays.stream(palindrom.split("")).sorted(Collections.reverseOrder()).collect(Collectors.joining());
        System.out.println("The reversed palindrome is: " + revStr); // NOPE, this will sort each chars in reverse order: zyx...

    }

    /**
     * Using the StringBuilder is the easiest.
     */
    public String reverseUsingStringBuilder(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    /**
     * Using Stream and Collector
     * First, create a generic reverse for stream
     * () -> Collector<T, ?, Stream<T>>
     */
    public static <T> Collector<T, ?, Stream<T>> rev() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    Collections.reverse(list);
                    return list.stream();
                }
        );
    }

    /**
     * Then implement the reverse using above.
     */
    public String reverseUsingCollector(String s) {

        List<String> ss = Arrays.stream(s.split("")).toList();
        return ss.stream().collect(rev()).collect(Collectors.joining());
    }


    /**
     * Using LinkedList
     * First, create a generic reverse for stream
     * Stream<T> -> Stream<T>
     */
    public static <T> Stream<T> revLL(Stream<T> stream) {
        LinkedList<T> stack = new LinkedList<>();
        stream.forEach(stack::push);
        return stack.stream();
    }
    /**
     * Usage of revLL
     */
    public String reverseUsingLinkedList(String s) {
        // Create a stream of characters (as individual String)
        Stream<String> stream =Arrays.stream(s.split("")).toList().stream();
        return revLL(stream).collect(Collectors.joining());
    }

    /**
     * Using Iterator and LinkedList
     * Since LinkedList supports insertion at the front (head), it provides descending iterators.
     */
    public static <T> Iterator<T> revIt(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(LinkedList::new)).descendingIterator();
    }
    /**
     * Usage of revIt
     */
    public String reverseUsingIterator(String s) {
        Stream<String> stream = Arrays.stream(s.split("")).toList().stream();
        String str = "";
        Iterator<String> it = revIt(stream);
        while (it.hasNext()) {
            str += it.next();
        }
        return str;
    }

    /**
     * Using Collector.of
     */
    public static <T> Stream<T> revCof(Stream<T> stream) {
        return stream.collect(
                Collector.of(
                        () -> new ArrayDeque<T>(), ArrayDeque::addFirst, (a,b) -> a
                )
        ).stream();
    }
    /**
     * Usage of revCof
     */
    public String reverseUinsgCollectorOf(String s) {
        Stream<String> stream = Arrays.stream(s.split("")).toList().stream();
        return revCof(stream).collect(Collectors.joining());
    }
}


