package jent.fun_with_interviews.arraysAndLists;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Input: some kind of array:  int[], String[], T[]
 * Output: List<Integer>, List<String>, List<T>
 *
 *     in other words:
 *     int[] -> List<Integer>
 *     String[] -> List<String>
 *     T[] -> List<T>
 */
public class ArrayToList {

    public static void main(String[] args) {
        ArrayToList al = new ArrayToList();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        class Muffin {
            String kind;
            int size;
            @Override
            public String toString() {
                return "{ 'Kind': " + this.kind + ", 'Size': " + this.size + "}";
            }
        }
        Muffin[] muffins = new Muffin[] {
                new Muffin("brand", 5),
                new Muffin("blueberry", 8),
                new Muffin("rasberry", 3),
                new Muffin("strawberry", 2),
        };

        List<Muffin> ms = al.mapTArrayToList(muffins);
        // but you didn't have to re-define a T[] -> List<T> since the Stream API does it
        List<Muffin> allMuffins = Arrays.stream(muffins).toList();

        ms.stream().forEach(System.out::println);

        // check this out
        int i[] = {0}; // works
        int y[] = {0,2,4};
        // istead of long way...
        int z[] = new int[]{0,2,4};
        for (int k=0; k<y.length; k++) {
            System.out.println("y[" + k + "] = " + y[k]);
        }

    }

    /**
     * int[] -> List<Integer>
     */
    //     public List<Integer> mapIntArrayToList(int[] xs) {
    //        return Arrays.stream(xs).mapToObj(Integer::valueOf).toList();
    //    } // SAME AS BELOW (for primitive)
    public List<Integer> mapIntArrayToList(int[] xs) {
        return Arrays.stream(xs).boxed().toList();
    }
    public List<Double> mapDoublePrimitiveArrayToListOfDouble(double[] xs) {
        return Arrays.stream(xs).boxed().toList();
    }

    /**
     * String[] -> List<String>
     */
    public List<String> mapStringArrayToList(String[] ss) {
        return Arrays.stream(ss).toList();
    }

    /**
     * T[] -> List<T> (see above as this was not necessary) Just for show
     */
    public <T> List<T> mapTArrayToList(T[] ts) {
        return Arrays.stream(ts).toList();
    }

}
