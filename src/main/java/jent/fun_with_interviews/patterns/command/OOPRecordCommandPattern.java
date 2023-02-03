package jent.fun_with_interviews.patterns.command;

import java.util.List;

public class OOPRecordCommandPattern {

     sealed interface Command permits Logger, FileSaver, Mailer {
         void run();
    }

    record Logger(String message) implements Command {
         public void run() {
             System.out.println(message);
         }
    }

    record FileSaver(String message) implements Command {
         public void run() {
             System.out.println(message);
         }
    }

    record Mailer(String message) implements Command {
         public void run() {
             System.out.println(message);
         }
    }

    static class CommandExecutor {
         public void execute(List<Command> tasks) {
             tasks.forEach(command -> command.run());
         }
    }

    public static void main(String[] args) {
        List<Command> tasks = List.of(
                new Logger("logging it..."),
                new FileSaver("saving it"),
                new Mailer("mailing it")
        );
        new CommandExecutor().execute(tasks);
    }

}
