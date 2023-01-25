package jent.fun_with_interviews.singleton;

public class NestedStaticSingleton {

    /**
     * Because the inner static class are not loaded when the NestedStaticSingle class (above) is loaded,
     * then it is essentially lazzy.  It will be created/loaded the first time it is accessed.
     */
    public static final class NestedSingleton {
        private static final NestedSingleton instance = new NestedSingleton();
    }

    public static NestedSingleton getInstance() {
        return NestedSingleton.instance;
    }
}
