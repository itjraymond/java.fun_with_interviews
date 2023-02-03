package jent.fun_with_interviews.problems.completablefuture;

import lombok.SneakyThrows;

import java.util.concurrent.ThreadLocalRandom;

public class NotificationService {

    @SneakyThrows
    public boolean notifyUser() {
        System.out.println("Trying to send notification...");
        Thread.sleep(2000);
        return ThreadLocalRandom.current().nextBoolean();
    }
}
