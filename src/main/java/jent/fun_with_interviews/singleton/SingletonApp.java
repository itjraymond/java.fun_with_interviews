package jent.fun_with_interviews.singleton;

import static jent.fun_with_interviews.singleton.NestedStaticSingleton.*;

public class SingletonApp {

    public static void main(String[] args) {
        EagerSingleton eagerSingleton = EagerSingleton.getInstance();

        LazzySingleton lazzySingleton = LazzySingleton.getInstance();

        NestedSingleton nestedSingleton = getInstance();

        EnumSingleton enumSingleton = EnumSingleton.INSTANCE;
    }
}
