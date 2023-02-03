package jent.fun_with_interviews.arraysAndLists;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReverseListOrArray {

    public static void main(String[] args) {
        List<String> l1 = List.of("Walking", "Running", "Eating"); // immutable
        List<String> l2 = new ArrayList<>() {{add("Walking"); add("Running"); add("Eating");}}; // mutable
        String[] l3 = "Walking,Running,Eating".split(",");
        Stream<String> l4 = Stream.of("Walking", "Running", "Eating");

        new LinkedList<>(l1).addFirst("Waiting"); // see because addFirst(...) is void, not very useful in some scenarios

        String[] a1 = new String[]{"1","2","3","4","5"};
        String[] a2 = reverse(a1);

        for (int i=0; i<a1.length;i++) {
            System.out.println(a1[i] + " -> " + a2[i]);
        }

        // Runtime exception "l1" is immutable
        // Collections.reverse(l1);
        // l1.stream().forEach(System.out::println);

        // Works because l2 is mutable
//        System.out.println("List<String> mutable --------------");
//        Collections.reverse(l2);
//        l2.stream().forEach(System.out::println);

        // Does not even compile because Collections.reverse takes List type not Array [] type.
        // Collections.reverse(l3);
        // l3.stream().forEach(System.out::println);

        // The "toList()" returns an immutable list hence Runtime exception
        // Collections.reverse(l4.toList());

        System.out.println("List<String> immutable--------------");
        reverse(l1.stream()).forEachRemaining(System.out::println);
        System.out.println("List<String> mutable--------------");
        reverse(l2.stream()).forEachRemaining(System.out::println);
        System.out.println("String[]--------------");
        reverse(Arrays.stream(l3)).forEachRemaining(System.out::println);
        System.out.println("Stream<String>--------------");
        reverse(l4).forEachRemaining(System.out::println);
    }


    /**
     * This is probably the easiest and relatively efficient way of reversing any Stream
     */
    public static Iterator<?> reverse(Stream<?> stream) {
        return stream.collect(Collectors.toCollection(LinkedList::new)).descendingIterator();
    }


    /**
     * Efficient way to reverse an Array without modifying the original.
     */
    public static <T> T[] reverse(T[] array) {
//        T[] a = (T[]) Array.newInstance(clazz,array.length);
        T[] clone = array.clone();
        int size = clone.length;
        for (int i=0; i < size / 2; i++) {
            T t = clone[i];
            clone[i] = clone[size-1-i];
            clone[size-1-i] = t;
        }
        return clone;
    }

    /**
     * This will be more efficient that the Stream reverse above.
     * Essentially we create a new ArrayList which internally is backed by an Array structure
     * and we simply loop in reverse order to copy each element.
     */
    public static <T> List<T> reverse(List<T> list) {
        final int size = list.size();
        var rlist = new ArrayList<T>(size);

        for (int i = size-1; i>=0; i++){
            rlist.add(list.get(i));
        }
        return rlist;
    }
}
