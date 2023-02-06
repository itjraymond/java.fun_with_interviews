package jent.fun_with_interviews.async.future;

import lombok.SneakyThrows;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CarUtil {
    /**
     * Define an entity that can have many e.g. Car and List<Car>
     */
    public static record Car(Brand brand, Model model, Price price, Millage millage, Color color) {}

    enum Brand {
        TOYOTA,
        FORD,
        KIA,
        CHEVROLET,
        CHRYSLER,
        TESLA
    }

    enum Model {
        SEDAN,
        SUV
    }

    public static Color[] colors = {Color.BLACK, Color.BLUE, Color.WHITE, Color.DARK_GRAY};

    public static class Price extends BigDecimal { // Cheap type aliasing
        public Price(double val) {
            super(val);
        }
    }

    public static record Millage(Double value){}

    // Service
    public static class CarService {
        @SneakyThrows
        public Brand[] getCarBrands() {
            Thread.sleep(2000);
            return Brand.values();
        }
        @SneakyThrows
        public Collection<Car> getCarInventory(){
            Thread.sleep(2000);
            return inventory;
        }
    }

    public static Supplier<Car> carGenerator = () -> new Car(
            Brand.values()[ThreadLocalRandom.current().nextInt(Brand.values().length)],
            Model.values()[ThreadLocalRandom.current().nextInt(Model.values().length)],
            new Price(ThreadLocalRandom.current().nextDouble(10_000.0, 100_000.0)),
            new Millage(ThreadLocalRandom.current().nextDouble(500_000.0)),
            colors[ThreadLocalRandom.current().nextInt(colors.length)]
    );


    public static Collection<Car> inventory = Stream.generate(carGenerator).limit(500).toList();


}
