package jent.fun_with_interviews.singleton;

public class LazzySingleton {
    private static LazzySingleton instance;
    private LazzySingleton(){}

    /**
     * Not Thread Safe.  Do not do that.
     * @return LazzySingleton
     */
    public static LazzySingleton getNotSafeLazzySingleton() {
        if (instance == null) {
            instance = new LazzySingleton();
        }
        return instance;
    }

    /**
     * This is Thread Safe but very innefficient as each time the singleton is needed it will call the synchronized method.
     * @return LazzySingleton
     */
    public static synchronized LazzySingleton getNotEfficientSingletonButSafe() {
        if (instance == null) {
            instance = new LazzySingleton();
        }
        return instance;
    }

    /**
     * This is Thread Safe and efficient as the synchronized block will be call at most once (more but only
     * for concurrent calls - but should be very rare.).  Once the singleton has been initialized, it will never
     * get to the synchronized block.
     * @return
     */
    public static LazzySingleton getInstance() {
        if (instance == null) {
            synchronized (LazzySingleton.class) {
                if (instance == null) {
                    instance = new LazzySingleton();
                }
            }
        }
        return instance;
    }
}
