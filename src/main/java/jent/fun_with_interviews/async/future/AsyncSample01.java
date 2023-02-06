package jent.fun_with_interviews.async.future;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static jent.fun_with_interviews.async.future.CarUtil.Car;
import static jent.fun_with_interviews.async.future.CarUtil.CarService;
import static jent.fun_with_interviews.async.future.NotificationUtil.NotificationService;

public class AsyncSample01 {

    public static void main(String[] args) {
        CarService carService = new CarService();
        NotificationService notificationService = new NotificationService();

        long start = System.currentTimeMillis();
        System.out.println("Starting at: " + start);

        List<Car> cs = CompletableFuture.supplyAsync(() -> carService.getCarBrands())
                .thenApply(
                        brands -> carService.getCarInventory()
                                .stream()
                                .filter(car -> car.brand() == brands[ThreadLocalRandom.current().nextInt(brands.length)])
                                .toList()
                ).join();

        cs.forEach(System.out::println);

        long ending = (System.currentTimeMillis() - start);
        System.out.println("Taken " + ending);
    }
}
