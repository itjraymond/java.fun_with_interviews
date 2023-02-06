package jent.fun_with_interviews.async.future;

import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static jent.fun_with_interviews.async.future.CarUtil.*;
import static jent.fun_with_interviews.async.future.CarUtil.Car;
import static jent.fun_with_interviews.async.future.CarUtil.CarService;
import static jent.fun_with_interviews.async.future.NotificationUtil.NotificationService;

public class AsyncSample02 {

    @SneakyThrows
    public static void main(String[] args) {
        CarService carService = new CarService();
        NotificationService notificationService = new NotificationService();

        long start = System.currentTimeMillis();
        System.out.println("Starting at: " + start);

        CompletableFuture<Brand[]> brands = CompletableFuture.supplyAsync(() -> carService.getCarBrands());
        CompletableFuture<Collection<Car>> cars = CompletableFuture.supplyAsync(() -> carService.getCarInventory());

        CompletableFuture.allOf(brands,cars).get();
        Brand brand = brands.get()[ThreadLocalRandom.current().nextInt(brands.get().length)];
        List<Car> cs = cars.get()
                .stream()
                .filter(car -> car.brand() == brand)
                .toList();
        cs.forEach(System.out::println);

        long ending = (System.currentTimeMillis() - start);
        System.out.println("Taken " + ending);
    }
}
