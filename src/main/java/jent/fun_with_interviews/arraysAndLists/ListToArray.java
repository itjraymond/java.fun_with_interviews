package jent.fun_with_interviews.arraysAndLists;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;

public class ListToArray {

    public static void main(String[] args) {
        ListToArray la = new ListToArray();
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        class Muffin {
            String kind;
            int size;
        }

        List<Muffin> muffins = List.of(
                new Muffin("brand", 5),
                new Muffin("blueberry", 2),
                new Muffin("rasberry", 8),
                new Muffin("carrot", 3)
        );

        Muffin[] ms = la.mapListOfT_toArrayOfT(muffins, Muffin.class);

        for (Muffin m : ms) {
            System.out.println(m.getKind() + " " + m.getSize());
        }
    }

    /**
     * List<Integer> -> int[]
     */
    public int[] mapIntArrayToListOfInteger(List<Integer> is) {
        return is.stream().mapToInt(Integer::valueOf).toArray();
    }

    /**
     * List<String> -> String[]
     */
    public String[] mapListOfStringToArray(List<String> ss) {
        IntFunction<String[]> generator = i -> new String[i]; // IntFunction is a function that takes an integer a returns the type of our choice.  The int is used as a "size" for generating the array in this case
        IntFunction<String[]> gen = String[]::new;            // Hence this also works
        return ss.stream().toArray(generator);
        // return ss.stream().toArray(String[]::new); // This is also a good alternative as it is direct
    }

    /**
     * List<T> -> T[]
     * We need to pass the Class<T> which is the Type.  Now an example instance of such Class<T> type is Muffin.class
     */
    public <T> T[] mapListOfT_toArrayOfT(List<T> ts, Class<T> clazz) {
        T[] arr = (T[]) Array.newInstance(clazz, ts.size());
        return ts.toArray(arr);
    }
}
