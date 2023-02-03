package jent.fun_with_interviews.patterns.command;

import java.text.MessageFormat;
import java.util.List;
import java.util.function.Consumer;

public class FPCommandPattern {

    public static void log(String message) {
        System.out.printf("logging it - %s%n", message);
    }

    public static void saveFile(String message) {
        System.out.printf("saving it - %s%n", message);
    }

    public static void mail(String message) {
        System.out.printf("mailing it - %s%n", message);
    }

    // * OR *

    public static Consumer<String> logger = message -> System.out.println(MessageFormat.format("logging it {0}", message));
    public static Consumer<String> fileSaver = message -> System.out.println(MessageFormat.format("logging it {0}", message));
    public static Consumer<String> mailer = message -> System.out.println(MessageFormat.format("logging it {0}", message));

    public static void execute(List<Runnable> tasks) {
        tasks.forEach(Runnable::run);
    }

    public static void main(String[] args) {
        List<Runnable> tasks = List.of(
                () -> log("from logger"),
                () -> saveFile("from file saver"),
                () -> mail("from mailer")
        );
        execute(tasks);

        List<Runnable> ts = List.of(
                () -> logger.accept("from logger"),
                () -> fileSaver.accept("from file saver"),
                () -> mailer.accept("from mailer")
        );
        execute(ts);
    }

}
