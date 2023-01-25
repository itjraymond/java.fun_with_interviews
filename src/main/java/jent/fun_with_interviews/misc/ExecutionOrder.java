package jent.fun_with_interviews.misc;

public class ExecutionOrder {
    int i;
    static int j;

    {
        System.out.println("Some block.  Careful, this block is not declared static... i=" + i);
    }

    static {
        System.out.println("Some 'static' block.  Should print first when classs is loaded. j=" + j);
        m2();
    }

    {
        i = 5;
    }

    static {
        j = 10;
    }

    ExecutionOrder() {
        System.out.println("Default constructor called... j=" + j);
    }

    public static void main(String[] args) {
        ExecutionOrder eo = new ExecutionOrder();
    }

    public void m1() {
        System.out.println("m1 is called j=" + j);
    }

    static {
        System.out.println("Second 'static' block 2 j=" + j);
    }

    {
        System.out.println("Second block (not static) i=" + i);
    }

    public static void m2() {
        System.out.println("static m2 called.  j=" + j);
    }
}
