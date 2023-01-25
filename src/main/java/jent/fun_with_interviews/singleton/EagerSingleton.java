package jent.fun_with_interviews.singleton;

public class EagerSingleton {
    private static EagerSingleton instance = new EagerSingleton();
    private EagerSingleton(){}

    public static EagerSingleton getInstance() {
        return instance;
    }
}
