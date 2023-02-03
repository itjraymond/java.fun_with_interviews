package jent.fun_with_interviews.problems.completablefuture;

import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AvailabilityService {

    @SneakyThrows
    public List<LocalDateTime> getAvailability() {

        System.out.println("Searching for availability...");
        Thread.sleep(2000);
        boolean flag = ThreadLocalRandom.current().nextBoolean();
        // OR
        boolean f = Math.random() > 0.5;

        if (flag) {
            return List.of();
        } else {
            return List.of(
                    LocalDateTime.of(2023,3,24,13,0,0),
                    LocalDateTime.of(2023,4,16,9,30,0)
            );
        }
    }
}
