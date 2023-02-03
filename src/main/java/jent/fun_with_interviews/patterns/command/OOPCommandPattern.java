package jent.fun_with_interviews.patterns.command;

import java.util.List;

public class OOPCommandPattern {

    interface Command {
        void run();
    }

    static class Logger implements Command {
        private final String message;

        Logger(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.printf("Logging operation with message: %s%n", message);
        }
    }

    static class FileSaver implements Command {
        private final String message;

        FileSaver(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.printf("FileSaver operation with message: %s%n", message);
        }
    }

    static class Mailer implements Command {
        private final String message;

        Mailer(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.printf("Mailer operation with message: %s%n", message);
        }
    }

    static class CommandExecutor {
        public void execute(List<Command> tasks) {
            for (Command task : tasks) {
                task.run();
            }
        }
    }

    public static void main(String[] args) {
        List<Command> tasks = List.of(
                new Logger(" just logging along."),
                new FileSaver("saving away"),
                new Mailer("mailing it")
        );
        new CommandExecutor().execute(tasks);
    }

}
