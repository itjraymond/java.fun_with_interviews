package jent.fun_with_interviews.async.future;

import lombok.SneakyThrows;

public class NotificationUtil {
    /**
     * Define a Notification service
     */
    public static class NotificationService {
        @SneakyThrows
        public void send(Event event, Target target) {
            Thread.sleep(2000);
            System.out.printf("Sending Event %s to %s %n", event.payload, target.targetName);
        }
    }

    public static record Event(String payload) {}
    public static record Target(String targetName) {}

}
