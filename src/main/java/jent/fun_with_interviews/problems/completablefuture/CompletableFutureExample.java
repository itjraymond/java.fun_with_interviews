package jent.fun_with_interviews.problems.completablefuture;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureExample {

    @SneakyThrows
    public static void main(String[] args) {
        AvailabilityService availabilityService = new AvailabilityService();
        NotificationService notificationService = new NotificationService();

        CompletableFuture<Boolean> isNotificationSend = CompletableFuture.supplyAsync(availabilityService::getAvailability)
                .thenApply(availList -> notificationService.notifyUser());
        System.out.println("Processing availabilities...");

        isNotificationSend.join();
        System.out.println("Notification was sent? " + isNotificationSend.get());
    }
}
