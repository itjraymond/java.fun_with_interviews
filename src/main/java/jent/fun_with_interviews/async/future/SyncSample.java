package jent.fun_with_interviews.async.future;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static jent.fun_with_interviews.async.future.CarUtil.Brand;
import static jent.fun_with_interviews.async.future.CarUtil.CarService;
import static jent.fun_with_interviews.async.future.NotificationUtil.Event;
import static jent.fun_with_interviews.async.future.NotificationUtil.NotificationService;
import static jent.fun_with_interviews.async.future.NotificationUtil.Target;

public class SyncSample {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println("Starting at: " + start);
        CarService carService = new CarService();
        NotificationService notificationService = new NotificationService();

        Arrays.stream(carService.getCarBrands())
                .skip(ThreadLocalRandom.current().nextInt(Brand.values().length))
                .findFirst()
                .map(brand ->  carService.getCarInventory().stream().filter(car -> car.brand() == brand).toList())
                .ifPresent(cars -> cars.forEach(System.out::println));
        notificationService.send(new Event("carListRetrieved"), new Target("customer"));
        long ending = (System.currentTimeMillis() - start);
        System.out.println("Taken " + ending);
    }
}