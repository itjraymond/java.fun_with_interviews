package jent.fun_with_interviews.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionDoubleBraceInitialization {

    public static void main(String[] args) {
        Set<String> ss = new HashSet<>() {{
            add("One");
            add("Two");
            add("Three");
        }};

        List<Integer> is = new ArrayList<>() {{
            add(1);
            add(2);
            add(3);
        }};

        Map<String,Integer> ms = new HashMap<>() {{
            put("One", 1);
            put("Two", 2);
            put("Three", 3);
        }};

        String[] as = new String[]{"one","two"}; // see ArrayInitialization.java
    }
}
