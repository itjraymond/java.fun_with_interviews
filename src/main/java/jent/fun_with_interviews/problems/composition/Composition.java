package jent.fun_with_interviews.problems.composition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class Composition {

    public static void main(String[] args) {
        List<Car> cars = carStore.get();
        System.out.println(findBlackCarWithLowestMillage.apply(cars));
        findAllNewYellowFord.apply(cars).forEach(System.out::println);
        findAllNewWhiteFord.apply(cars).forEach(System.out::println);
        System.out.println(findCheapestCarByBrand.apply(CAR_BRAND.FORD).apply(cars));
        findCheapestCarByBrandAndColor.apply(CAR_BRAND.KIA).apply(Color.RED).apply(cars).forEach(System.out::println);
        System.out.println(findCheapestCarByBrandAndType.apply(CAR_BRAND.CHEVRELET).apply(CAR_TYPE.NEW).apply(cars));
        System.out.println(findMostExpensiveCar.apply(cars));
        System.out.println(findSecondMostExpensiveCar.apply(cars));
    }

    static Function<Color, Function<List<Car>,List<Car>>> byColor = color -> cars -> cars.stream().filter(c -> c.getColor() == color).toList();
    static BinaryOperator<Car> reduceByLowestMillage = (car1, car2) -> car1.getMillage().compareTo(car2.getMillage()) > 0 ? car2 : car1;
    static Function<List<Car>, Car> findCarWithLowestMillage = cars -> cars.stream().reduce(reduceByLowestMillage).orElse(null);
    static Function<CAR_BRAND, Function<List<Car>, List<Car>>> byBrand = brand -> cars -> cars.stream().filter(car -> car.getCarBrand() == brand).toList();
    static Function<List<Car>, List<Car>> byToyota = cars -> cars.stream().filter(car -> car.getCarBrand() == CAR_BRAND.TOYOTA).toList();
    // OR expressed in terms of another basic function
    static Function<List<Car>, List<Car>> byChevrelet = cars -> byBrand.apply(CAR_BRAND.CHEVRELET).apply(cars);
    static Function<CAR_TYPE, Function<List<Car>, List<Car>>> byCarType = type -> cars -> cars.stream().filter(car -> car.getCarType() == type).toList();
    static Function<List<Car>, List<Car>> byNewCar = cars -> cars.stream().filter(car -> car.getCarType() == CAR_TYPE.NEW).toList();
    // OR expressed in terms of another basic function
    static Function<List<Car>, List<Car>> byUsedCar = cars -> byCarType.apply(CAR_TYPE.USED).apply(cars);
    static Function<List<Car>, Car> findCheapestCar = cars -> cars.stream().reduce( (car1, car2) -> car1.getPrice().compareTo(car2.getPrice()) < 0 ? car1 : car2).orElse(null);
    // Some interesting function composition
    static Function<CAR_BRAND, Function<List<Car>, Car>> findCheapestCarByBrand = brand -> cars -> byBrand.apply(brand).andThen(findCheapestCar).apply(cars);
    static Function<CAR_BRAND, Function<Color, Function<List<Car>, List<Car>>>> findCheapestCarByBrandAndColor = brand -> color -> cars -> byBrand.apply(brand).andThen(byColor.apply(color)).apply(cars);
    static Function<CAR_BRAND, Function<CAR_TYPE, Function<List<Car>, Car>>> findCheapestCarByBrandAndType = brand -> type -> cars -> byBrand.apply(brand).andThen(byCarType.apply(type)).andThen(findCheapestCar).apply(cars);
    static Function<List<Car>, Car> findMostExpensiveCar = cars -> cars.stream().reduce( (c1, c2) -> c1.getPrice().compareTo(c2.getPrice()) > 0 ? c1 : c2).orElse(null);
    static Function<List<Car>, Car> findSecondMostExpensiveCar = cars -> cars.stream().sorted( (c1, c2) -> c2.getPrice().compareTo(c1.getPrice())).skip(1).toList().get(0);

    // Composition: Find any black car with highest millage
    static Function<List<Car>, Car> findBlackCarWithLowestMillage = cars -> byColor.apply(Color.BLACK).andThen(findCarWithLowestMillage).apply(cars);

    // Composition: Find all New YELLOW Ford
    static Function<List<Car>, List<Car>> findAllNewYellowFord = cars -> byCarType.apply(CAR_TYPE.NEW).andThen(byBrand.apply(CAR_BRAND.FORD)).andThen(byColor.apply(Color.YELLOW)).apply(cars);
    static Function<List<Car>, List<Car>> findAllNewWhiteFord = cars -> byCarType.apply(CAR_TYPE.NEW).andThen(byBrand.apply(CAR_BRAND.FORD)).andThen(byColor.apply(Color.WHITE)).apply(cars);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Car {
        private CAR_BRAND carBrand;
        private CAR_TYPE carType;
        private Double millage;
        private Color color;
        private Double price;

        @Override
        public String toString(){
            return "{ car: { brand: " + getCarBrand() + ", type: " + getCarType() + ", millage: " + getMillage() + ", color: " + getColor() + ", price: " + getPrice() + " }}";
        }
    }

    public enum CAR_BRAND {
        TOYOTA,
        FORD,
        KIA,
        CHEVRELET,
    }

    public enum CAR_TYPE {
        NEW,
        ALMOST_NEW,
        USED,
        OLD
    }

    public static Supplier<List<Car>> carStore = () -> getRandomCars();
//        return List.of(
//                new Car(CAR_BRAND.TOYOTA, CAR_TYPE.NEW, 0D, Color.BLACK, 50589.0),
//                new Car(CAR_BRAND.CHEVRELET, CAR_TYPE.NEW, 0D, Color.WHITE, 39400.0),
//                new Car(CAR_BRAND.KIA, CAR_TYPE.NEW, 0D, Color.RED, 42000.0),
//                new Car(CAR_BRAND.TOYOTA, CAR_TYPE.ALMOST_NEW, 500.0D, Color.RED, 34500.0),
//                new Car(CAR_BRAND.CHEVRELET, CAR_TYPE.ALMOST_NEW, 1500.0D, Color.YELLOW, 28900.0),
//                new Car(CAR_BRAND.FORD, CAR_TYPE.NEW, 0D, Color.WHITE, 62500.0),
//                new Car(CAR_BRAND.KIA, CAR_TYPE.USED, 100_000.0D, Color.GRAY, 15000.0),
//                new Car(CAR_BRAND.CHEVRELET, CAR_TYPE.OLD, 350_000.0D, Color.GRAY, 4000.0),
//                new Car(CAR_BRAND.FORD, CAR_TYPE.NEW, 0D, Color.YELLOW, 48000.0),
//                new Car(CAR_BRAND.KIA, CAR_TYPE.NEW, 0D, Color.BLACK, 43050.0),
//                new Car(CAR_BRAND.FORD, CAR_TYPE.NEW, 0D, Color.WHITE, 59000.0),
//                new Car(CAR_BRAND.TOYOTA, CAR_TYPE.NEW, 0D, Color.BLUE, 72000.0),
//                new Car(CAR_BRAND.KIA, CAR_TYPE.ALMOST_NEW, 500.0D, Color.RED, 18000.0),
//                new Car(CAR_BRAND.FORD, CAR_TYPE.ALMOST_NEW, 1500.0D, Color.YELLOW, 21000.0),
//                new Car(CAR_BRAND.TOYOTA, CAR_TYPE.OLD, 200_000.0D, Color.WHITE, 11000.0),
//                new Car(CAR_BRAND.FORD, CAR_TYPE.USED, 100_000.0D, Color.GRAY, 11500.0),
//                new Car(CAR_BRAND.KIA, CAR_TYPE.OLD, 350_000.0D, Color.DARK_GRAY, 9000.0),
//                new Car(CAR_BRAND.KIA, CAR_TYPE.NEW, 0D, Color.YELLOW, 39500.0)
//        );
//    }

    public static List<Car> getRandomCars() {
        List<Car> cars = new ArrayList<>(2000);
        for (int i=0; i<2000; i++) {
            CAR_TYPE type = supplyRandomType();
            cars.add(new Car(
                supplyRandomBrand(),
                type,
                supplyRandomMillage(type),
                supplyRandomColor(),
                supplyRandomPrice(type)
            ));
        }
        return cars;
    }

   static final Random random = new Random();
   static final CAR_BRAND[] brands = CAR_BRAND.values();
   static final CAR_TYPE[] types = CAR_TYPE.values();
   static final Color[] colors = {Color.BLACK, Color.BLUE, Color.RED, Color.WHITE, Color.YELLOW, Color.GRAY};

    public static CAR_BRAND supplyRandomBrand() {
        return brands[random.nextInt(brands.length)];
    }

    public static CAR_TYPE supplyRandomType() {
        return types[random.nextInt(types.length)];
    }
    public static Color supplyRandomColor() {
        return colors[random.nextInt(colors.length)];
    }

    public static double supplyRandomMillage(CAR_TYPE type) {
        return switch (type) {
            case NEW ->        Math.round(random.nextDouble(0D, 20_000.0D) * 100) / 100.0D;
            case ALMOST_NEW -> Math.round(random.nextDouble(20_000.0D, 80_000.0D) * 100) / 100.0D;
            case USED ->       Math.round(random.nextDouble(80_000.0D, 500_000.0D) * 100) / 100.0D;
            case OLD ->        Math.round(random.nextDouble(300_000.0D, 600_000.0D) * 100) / 100.0D;
        };
    }

    public static double supplyRandomPrice(CAR_TYPE type) {
        return switch (type) {
            case NEW ->        Math.round(random.nextDouble(45_000.0D, 150_000.0D) * 100) / 100.0D;
            case ALMOST_NEW -> Math.round(random.nextDouble(18_000.0D, 50_000.0D) * 100) / 100.0D;
            case USED ->       Math.round(random.nextDouble(5_000.0D, 22_000.0D) * 100) / 100.0D;
            case OLD ->        Math.round(random.nextDouble(1_000.0D, 8_000.0D) * 100) / 100.0D;
        };
    }
}
