package jent.fun_with_interviews.async.future;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * Representation of a persistent store.
 * What do we store?  Root aggregate? Entity?
 * Let's try "Root aggregate" because it represent One Unit of Transaction.  We can however decompose it further but only
 * internally here and not tt be leaked out.
 *
 * Some DDD
 * CarProduct: Is just a virtual representation of an actual product.  In other word it is only the information
 * regarding the actual "physical" product.  When someone shows you a picture of a Car (from a brochure for instance)
 * they do show you just a Ford but rather they will show you a Ford Edge (SUV).  So, some aspect of the car in order
 * to make sense does not change.  For example a Ford Edge is different than a Ford 150.  The color can change without
 * chaning the "identity" of the product.  So a CarProduct will be made of BRAND and MODEL and TYPE (Sedan, SUV, Truck,
 * Van, MiniVan).  Usually an BRAND+MODEL determine automatically the TYPE.  What changes is the color, accessories,
 * features, millage i.e. those have no influence on the BRAND+MODEL+TYPE.
 * So we need for a given BRAND+MODEL+TYPE a list of possible color, a list of possible accessories, a list of possible
 * features.  "Arrangement" may also be important i.e. You cannot have side mirror sensors and not have detection sensor.
 * We also may simplify those arrangment as Basic Model, Mid-Model and Limited-Model.  Each have their own fixed arrangement.
 * Accessories: Jumper cables, Spare tire, Tire repair kit, special cup holder, ice scraper, dash cam
 * Features: Leather seats, heated seat, sunroof, backup camera, navigation system, bluetooth, remote start, blind spot
 * monitoring, forward collision warning, climate system, keyless entry, heated steering wheel, adaptive cruise control
 *
 * So, let look from a stand point of a brochure.  Typically, a vehicle brochure show some specific BRAND*MODEL*TYPE
 * (product type not sum type) although not a true cartesian product (e.g. you won't see Ford*Sianna*Minivan).
 * On the brochure we may display an example image of a particular vehicle e.g. Ford Edge SUV limited Edition.
 * On the same BRAND*MODEL*TYPE we may also have varieties such as Basic-Model, Limited-Edition.  Those are also known
 * as TRIM LEVEL.  For instance, Toyota RAV4 has lowest trim level LE. It also has LXL, SE (Sport edition), Limited
 * and Platinum.  So the brochure could essentially show, for a given BRAND*MODEL*TYPE, every trim level for this
 * particular BRAND*MODEL*TYPE along with which features they includes.  So could this form some kind of graph?
 *
 *                                 FORD*MODEL*TYPE
 *                                       |
 *                                   TRIM-LEVEL
 *                             /   /    |     \       \
 *                          LE   LXL   SE   Limited   Platinum
 *                          |
 *                        FEATURES (Set of)
 *                        |
 *                        |- heated seat
 *                        |- navigation system
 *                        |
 *                        ACCESSORIES (Set of)
 *                        |
 *                        |- Spare tire
 *                        |- Special cup holder
 *                        |
 *                        COLORS (Only one of)
 *                        |
 *                        |- White
 *                        |- Red
 *                        |- Black
 *                        PRICE
 *
 *  From the brochure, what is the manufacturer actually selling?  Does he sell a Ford Edge Minivan? No, Does he sell
 *  a Ford Edge Limited LXL with heated seat, spare tire? i.e. does the LXL make sense? No
 *  So what appear on the brochure has to correspond to an actual physical vehicle with whatever specifications it has.
 *  This correspond to any branch (above graph).
 *
 *  Root aggregate (more than one)
 *  - one to represent a BRAND*MODEL*TYPE + available TRIM
 *  - one to represent a TRIM + feature list, accessory list, available colors, price
 *  - one to represent an actual vehicle and its availability.
 *
 *  We will be asking things like "Do you have a Toyota RAV4 LE in Red?"  The result may be "We have 5 Toyota
 *  RAV4 LE in Red", "One has the following features: [...], accessories: [...] for the Price $x", "We have two with
 *  features: [...], accessories: [...] for the Price $y" and finally, we have 2 ..."
 *
 *
 *  Observations:
 *  Here is a short table of the cost of a particular feature according to TRIM, MODEL and BRAND
 *
 *  _______________________________________________________________________________
 *  | BRAND  |  MODEL  |  TRIM   |  YEAR   | FEATURE                     |  PRICE  |
 *  |--------|---------|---------|---------|-----------------------------|---------|
 *  | FORD   | EDGE    | LE      |   2021  | Collision Warning           | 565.50  |
 *  | FORD   | EDGE    | LE      |   2021  | Leather Seats               | 199.00  |
 *  | FORD   | EDGE    | LE      |   2021  | Heated Leather Seats        | 199.00  |
 *  | FORD   | EDGE    | LIMITED |   2020  | Collision Warning           | 572.00  |
 *  | FORD   | 150     | LE      |   2019  | Collision Warning           | 587.39  |
 *  ...
 *
 *  We see that the price of a feature "depends" on several criteria: BRAND*MODEL*TRIM.
 *  Not only that, the price also depends on "combinations" of feature (i.e. the price increases
 *  whenever you add a new feature).  This "combinations" is what we call the decorator pattern.
 *  Each addition of a feature "decorate the price".  The decorator is something that influence the
 *  value of a Type but does not change the Type.
 *  So how do we translate the above table into code? Type?
 *  We will use function and curried functions
 *  1. We see that the Price depends on the feature.  Hence this is a mapping (or function): Feature -> Price
 *     This can be expressed as a type: Function<Feature,Price>
 *
 *  2. We also see that the Feature*Price dependes on the TRIM.  Hence this is a mapping: Trim -> Function<Feature,Price>
 *     And thus expressed as a type: Function<Trim, Function<Feature,Price>>
 *
 *  3. Again, we also see that the Trim*Feature*Price depends on the MODEL.  Hence this is also a mapping:
 *     Model -> Function<Trim, Function<Feature,Price>>
 *     Hence expresed as a type: Function<Model, Function<Trim, Function<Feature,Price>>>
 *
 *  4. Last, we get the gist: Brand -> Function<Model, Function<Trim, Function<Feature,Price>>>
 *     Hence: Function<Brand, Function<Model, Function<Trim, Function<Feature,Price>>>>
 *
 *  Wow that is a mouth full.  The question now: is this going to be useful, readable, flexible etc...? (YES as we will see).
 */
public class Store {

    public record CarProduct(
            UUID id,
            BRAND brand,
            MODEL model,
            TRIM trim,
            Year year,
            Price price,
            Collection<FEATURE> includedFeatures,
            Collection<FEATURE> extraFeatures,
            Collection<COLOR> availableColors
    ){}

    public record CarInstance(
            UUID id,
            BRAND brand,
            MODEL model,
            TRIM trim,
            Year year,
            Price price,
            Collection<FEATURE> features,
            COLOR color,
            Millage millage
    ){}


    // The trick (seem to be): For each FEATURE, map it to a DoubleUnaryOperator function (i.e. Function<Double,Double>)
    // example of DoubleUnaryOperator: price -> price + f.apply(feature).price()
    // IMPORTANT: notice that the input price is the value we provide at then at end of the chain in the .apply(price)
    // This means we need to observe what is the type we will  be "applying" at then  end of the composition
    // and  thus that type (say T), means we must generate lambda: T -> T from another type such  as featues
    public static DoubleUnaryOperator composeCarFeaturesCost(Function<FEATURE, Price> f, FEATURE... features) {
        return Arrays.stream(features)
                .map( feature -> (DoubleUnaryOperator) price -> price + f.apply(feature).price())
                .reduce(DoubleUnaryOperator.identity(), DoubleUnaryOperator::andThen);
    }


    public static Price getPriceOfFeatures(Function<FEATURE,Price> fn, FEATURE... features) {
        Double sum = Arrays.stream(features)
                .map(fn::apply)
                .map(Price::price)
                .reduce(Double::sum)
                .orElse(0.0);
        return new Price(sum);
    }

    // Function<FEATURE, Price> priceByFeature = feature -> new Price(1.0);
    // Function<TRIM, Function<FEATURE, Price>> priceByTrim = trim -> feature -> new Price(1.0); // OR
    // Function<TRIM, Function<FEATURE, Price>> priceByTrimS = trim -> priceByFeature;
    // Function<MODEL, Function<TRIM, Function<FEATURE, Price>>> priceByModel = model -> priceByTrimS;
    // Function<BRAND, Function<MODEL, Function<TRIM, Function<FEATURE, Price>>>> priceByBrand = brand -> priceByModel; // OR
    public Function<BRAND, Function<MODEL, Function<TRIM, Function<FEATURE, Price>>>> pricingBy =
            brand -> model -> trim -> feature -> pricing.getOrDefault(brand.name()+model.name()+trim.name()+feature.name(), new Price(0.0));

    public record Price(Double price) {}

    public static class Amount extends BigDecimal {
        private Amount(String val) {
            super(val);
        }

        private Amount(double val) {
            super(val);
        }

        private Amount(BigInteger val) {
            super(val);
        }

        private Amount(int val) {
            super(val);
        }

        private Amount(long val) {
            super(val);
        }
        // Usage: Amount.val("1000.00");
        public static Amount val(String val) {
            return new Amount(val);
        }
        // Usage: Amount.val(1_000.0);
        public static Amount val(Double val) {
            return new Amount(val);
        }
    }

    public record Millage(Double price) {}

    public record Year(Integer year) {}

//    public record Year(Integer year) {
//        public Year{
//            Objects.requireNonNull(year);
//            if (year < 1900 || year > 2300) {
//                throw new IllegalArgumentException("Invalid year; allowed range: [1900-2300]");
//            }
//        }
//    }
    // How about using an Either, meaning either you have a good constructed record or not.  Then add some map, flatmap.

    public enum FEATURE {
        COLLISION_WARNING,
        STANDARD_SEAT,
        STANDARD_HEATED_SEAT,
        LEATHER_SEAT,
        LEATHER_HEATED_SEAT,
        HEATED_STEERING_WHEEL,
        STANDARD_SUN_ROOF,
        MOON_SUN_ROOF,
        STANDARD_BACKUP_CAMERA,
        CAMERA_360,
        ADAPTIVE_CRUISE_CONTROL,
        CLIMATE_SYSTEM,
        BLIND_SPOT_MONITORING,
        REMOTE_START,
        ELECTRIC_TRUNK,
        STANDARD_NAVIGATION_SYSTEM,
        ENHANCED_NAVIGATION_SYSTEM,
        KEYLESS_START,
        KEYLESS_ENTRY
    }

    public enum TRIM {
        CE, // Classic Edition
        DX, // Deluxe
        DL, // Deluxe Level
        EX, // Extra
        GL, // Grade Level
        GLE, // Grade Level Extra
        GT, // Grand Touring
        LX, // Luxury
        LE, // Luxury Edition
        LS, // Luxury Sport
        LT, // Luxury Touring
        LTD, // Limited
        LTZ, // Luxury Touring Special
        SE, // Sport Edition, Special Edition
        SL, // Standard Level
        SLE, // Standard Level Extra
        SLT, // Standard Level Touring
        SV, // Special Version
        XLT; // Extra Level Touring
    }

    public enum VEHICLE_TYPE {
        SEDAN,
        SUV,
        SPORT,
        TRUCK,
        MINIVAN,
        HATCHBACK,
        VAN;
    }
    public enum MODEL {
        MUSTANG(VEHICLE_TYPE.SEDAN),
        GT(VEHICLE_TYPE.SEDAN),
        F_150(VEHICLE_TYPE.TRUCK),
        ESCAPE(VEHICLE_TYPE.SUV),
        EDGE(VEHICLE_TYPE.SUV),
        EXPLORER(VEHICLE_TYPE.SUV),
        SEQUOIA(VEHICLE_TYPE.SUV),
        PRIUS(VEHICLE_TYPE.HATCHBACK),
        COROLLA(VEHICLE_TYPE.SEDAN),
        SIENNA(VEHICLE_TYPE.MINIVAN),
        CAMRY(VEHICLE_TYPE.SEDAN),
        TACOMA(VEHICLE_TYPE.TRUCK),
        HIGHLANDER(VEHICLE_TYPE.SUV),
        RAV4(VEHICLE_TYPE.SUV),
        TUNDRA(VEHICLE_TYPE.TRUCK),
        EQUINOX(VEHICLE_TYPE.HATCHBACK),
        SUBURBAN(VEHICLE_TYPE.SUV),
        COLORADO(VEHICLE_TYPE.TRUCK),
        SILVERADO(VEHICLE_TYPE.TRUCK),
        CAMARO(VEHICLE_TYPE.SPORT),
        CORVETTE(VEHICLE_TYPE.SPORT),
        MALIBU(VEHICLE_TYPE.SEDAN),
        CRUZE(VEHICLE_TYPE.SEDAN),
        ORLANDO(VEHICLE_TYPE.MINIVAN),
        EXPRESS(VEHICLE_TYPE.VAN),
        N400(VEHICLE_TYPE.VAN);

        private VEHICLE_TYPE type;

        MODEL(VEHICLE_TYPE type) {
            this.type = type;
        }
    }

    public enum BRAND {
        FORD,
        TOYOTA,
        CHEVROLET;
    }

    public enum COLOR {
        BLACK,
        WHITE,
        RED,
        BLUE,
        SILVER,
        CHARCOAL
    }

    public static record FeatureCost (FEATURE feature, Price price) {}
    public static record TrimCost (TRIM trim, Price cost, Collection<FeatureCost> features) {}
    public static record ModelLine (MODEL model, Collection<TrimCost> trims) {}
    public static record BrandLine (BRAND brand, Collection<ModelLine> models) {}
    public static record YearMake (Year year, Collection<BrandLine> brands) {}


    public static YearMake year2021 = new YearMake(new Year(2021), List.of(
            new BrandLine(BRAND.FORD, List.of(
                    new ModelLine(MODEL.EDGE, List.of(
                            new TrimCost(TRIM.SE, new Price(38_500.59), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(589.68)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(148.76)),
                                    new FeatureCost(FEATURE.STANDARD_SEAT, new Price(289.89))
                            ))
                    )),
                    new ModelLine(MODEL.EDGE, List.of(
                            new TrimCost(TRIM.SL, new Price(41_400.32), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(596.32)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(148.76)),
                                    new FeatureCost(FEATURE.LEATHER_SEAT, new Price(478.89))
                            ))
                    )),
                    new ModelLine(MODEL.ESCAPE , List.of(
                            new TrimCost(TRIM.SE, new Price(38_500.59), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(389.68)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(108.76)),
                                    new FeatureCost(FEATURE.STANDARD_SEAT, new Price(179.89))
                            ))
                    )),
                    new ModelLine(MODEL.ESCAPE, List.of(
                            new TrimCost(TRIM.SL, new Price(41_400.32), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(496.32)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(128.76)),
                                    new FeatureCost(FEATURE.LEATHER_SEAT, new Price(372.89))
                            ))
                    ))

            )),
            new BrandLine(BRAND.TOYOTA, List.of(
                    new ModelLine(MODEL.CAMRY, List.of(
                            new TrimCost(TRIM.CE, new Price(38_500.59), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(589.68)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(148.76)),
                                    new FeatureCost(FEATURE.STANDARD_SEAT, new Price(289.89))
                            ))
                    )),
                    new ModelLine(MODEL.CAMRY, List.of(
                            new TrimCost(TRIM.DL, new Price(41_400.32), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(596.32)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(148.76)),
                                    new FeatureCost(FEATURE.LEATHER_SEAT, new Price(478.89))
                            ))
                    )),
                    new ModelLine(MODEL.HIGHLANDER , List.of(
                            new TrimCost(TRIM.DX, new Price(38_500.59), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(389.68)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(108.76)),
                                    new FeatureCost(FEATURE.STANDARD_SEAT, new Price(179.89))
                            ))
                    )),
                    new ModelLine(MODEL.HIGHLANDER, List.of(
                            new TrimCost(TRIM.DL, new Price(41_400.32), List.of(
                                    new FeatureCost(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(496.32)),
                                    new FeatureCost(FEATURE.BLIND_SPOT_MONITORING, new Price(128.76)),
                                    new FeatureCost(FEATURE.LEATHER_SEAT, new Price(372.89))
                            ))
                    ))

            ))
    ));

    public static Map<TRIM, Price> FordEdgeTrimsCost = new HashMap<>() {{
        put(TRIM.SE, new Price(32_000.0));
        put(TRIM.SL, new Price(39_600.0));
        put(TRIM.LTD, new Price(45_300.0));
    }};

    public static Map<TRIM, Price> FordEscapeTrimsCost = new HashMap<>() {{
        put(TRIM.SE, new Price(26_000.0));
        put(TRIM.SL, new Price(30_900.0));
        put(TRIM.LTD, new Price(39_900.0));
    }};

    public static Collection<MODEL> fordModels = List.of(MODEL.EDGE, MODEL.ESCAPE, MODEL.EXPLORER, MODEL.F_150, MODEL.GT, MODEL.MUSTANG);
    public static Collection<TRIM> fordEdgeTrims = List.of(TRIM.SE, TRIM.SLE, TRIM.LT);
    public static Collection<FEATURE> fordEdgeFeatures = List.of(FEATURE.ADAPTIVE_CRUISE_CONTROL, FEATURE.BLIND_SPOT_MONITORING, FEATURE.COLLISION_WARNING, FEATURE.LEATHER_HEATED_SEAT);
    public static Collection<TRIM> fordEscapeTrims = List.of(TRIM.SE, TRIM.SLE, TRIM.LT);
    public static Collection<FEATURE> fordEscapeFeatures = List.of(FEATURE.ADAPTIVE_CRUISE_CONTROL, FEATURE.BLIND_SPOT_MONITORING, FEATURE.COLLISION_WARNING, FEATURE.LEATHER_HEATED_SEAT);
    public static Collection<TRIM> fordExplorerTrims = List.of(TRIM.SE, TRIM.SLE, TRIM.SLT, TRIM.LT);
    public static Collection<FEATURE> fordExplorerFeatures = List.of(FEATURE.ADAPTIVE_CRUISE_CONTROL, FEATURE.BLIND_SPOT_MONITORING, FEATURE.COLLISION_WARNING, FEATURE.LEATHER_HEATED_SEAT);
    public static Collection<TRIM> fordF150Trims = List.of(TRIM.SE, TRIM.SL, TRIM.SLE, TRIM.LT);
    public static Collection<FEATURE> fordF150Features = List.of(FEATURE.ADAPTIVE_CRUISE_CONTROL, FEATURE.BLIND_SPOT_MONITORING, FEATURE.COLLISION_WARNING, FEATURE.LEATHER_HEATED_SEAT);
    public static Collection<TRIM> fordGTTrims = List.of(TRIM.SE, TRIM.LTD);
    public static Collection<FEATURE> fordGTFeatures = List.of(FEATURE.ADAPTIVE_CRUISE_CONTROL, FEATURE.BLIND_SPOT_MONITORING, FEATURE.COLLISION_WARNING, FEATURE.LEATHER_HEATED_SEAT);
    public static Collection<TRIM> fordMustangTrims = List.of(TRIM.SE, TRIM.LTD);
    public static Collection<FEATURE> fordMustangFeatures = List.of(FEATURE.ADAPTIVE_CRUISE_CONTROL, FEATURE.BLIND_SPOT_MONITORING, FEATURE.COLLISION_WARNING, FEATURE.LEATHER_HEATED_SEAT);
    public static Collection<FEATURE> fordCommonFeatures = List.of(FEATURE.CLIMATE_SYSTEM, FEATURE.KEYLESS_START, FEATURE.REMOTE_START, FEATURE.STANDARD_BACKUP_CAMERA, FEATURE.STANDARD_NAVIGATION_SYSTEM, FEATURE.STANDARD_SEAT);

    public static Map<java.time.Year, Map<BRAND, Map<MODEL, Map<TRIM, Map<FEATURE,Price>>>>> featuresCost = new HashMap<>() {{
        put(java.time.Year.of(2021), new HashMap<BRAND, Map<MODEL, Map<TRIM, Map<FEATURE,Price>>>>() {{

            put(BRAND.FORD, new HashMap<MODEL, Map<TRIM, Map<FEATURE,Price>>>() {{
                put(MODEL.EDGE, new HashMap<TRIM, Map<FEATURE,Price>>() {{
                    put(TRIM.SE, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(820.00));
                        put(FEATURE.STANDARD_SEAT, new Price(350.00));
                        put(FEATURE.STANDARD_HEATED_SEAT, new Price(430.00));
                        put(FEATURE.ELECTRIC_TRUNK, new Price(120.00));
                        put(FEATURE.KEYLESS_START, new Price(89.00));
                        put(FEATURE.CLIMATE_SYSTEM, new Price(427.00));
                        put(FEATURE.REMOTE_START, new Price(38.00));
                        put(FEATURE.STANDARD_SUN_ROOF, new Price(210.00));
                    }});
                    put(TRIM.SL, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(123.00));
                    }});
                }});
                put(MODEL.F_150, new HashMap<TRIM, Map<FEATURE,Price>>() {{
                    put(TRIM.LS, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(920.00));
                        put(FEATURE.STANDARD_SEAT, new Price(450.00));
                        put(FEATURE.STANDARD_HEATED_SEAT, new Price(530.00));
                        put(FEATURE.ELECTRIC_TRUNK, new Price(220.00));
                        put(FEATURE.KEYLESS_START, new Price(99.00));
                        put(FEATURE.CLIMATE_SYSTEM, new Price(527.00));
                        put(FEATURE.REMOTE_START, new Price(48.00));
                        put(FEATURE.STANDARD_SUN_ROOF, new Price(310.00));
                    }});
                    put(TRIM.LE, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(223.00));
                    }});
                }});
            }});

            put(BRAND.TOYOTA, new HashMap<MODEL, Map<TRIM, Map<FEATURE,Price>>>() {{
                put(MODEL.CAMRY, new HashMap<TRIM, Map<FEATURE,Price>>() {{
                    put(TRIM.CE, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(820.00));
                        put(FEATURE.STANDARD_SEAT, new Price(350.00));
                        put(FEATURE.STANDARD_HEATED_SEAT, new Price(430.00));
                        put(FEATURE.ELECTRIC_TRUNK, new Price(120.00));
                        put(FEATURE.KEYLESS_START, new Price(89.00));
                        put(FEATURE.CLIMATE_SYSTEM, new Price(427.00));
                        put(FEATURE.REMOTE_START, new Price(38.00));
                        put(FEATURE.STANDARD_SUN_ROOF, new Price(210.00));
                    }});
                    put(TRIM.DL, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(123.00));
                    }});
                }});
                put(MODEL.HIGHLANDER, new HashMap<TRIM, Map<FEATURE,Price>>() {{
                    put(TRIM.DL, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(920.00));
                        put(FEATURE.STANDARD_SEAT, new Price(450.00));
                        put(FEATURE.STANDARD_HEATED_SEAT, new Price(530.00));
                        put(FEATURE.ELECTRIC_TRUNK, new Price(220.00));
                        put(FEATURE.KEYLESS_START, new Price(99.00));
                        put(FEATURE.CLIMATE_SYSTEM, new Price(527.00));
                        put(FEATURE.REMOTE_START, new Price(48.00));
                        put(FEATURE.STANDARD_SUN_ROOF, new Price(310.00));
                    }});
                    put(TRIM.DX, new HashMap<FEATURE, Price>() {{
                        put(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(223.00));
                    }});
                }});
            }});

        }});
    }};


    public static void main(String[] args) {
        Store store = new Store();

        try {
            Long start = System.currentTimeMillis();
            ObjectMapper mapper2 = new ObjectMapper();
            String costForYear2021 = mapper2.writeValueAsString(year2021);
            System.out.println("cost for year 2021 = " + costForYear2021 + " Took: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            ObjectMapper mapper = new ObjectMapper();
            String featuresCostStr = mapper.writeValueAsString(featuresCost);
            System.out.println("featuresCostStr = " + featuresCostStr + " Took: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            ObjectMapper mapper4 = new ObjectMapper();
            featuresCostStr = mapper4.writeValueAsString(featuresCost);
            System.out.println("featuresCostStr = " + featuresCostStr + " Took: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            ObjectMapper mapper3 = new ObjectMapper();
            costForYear2021 = mapper3.writeValueAsString(year2021);
            System.out.println("cost for year 2021 = " + costForYear2021 + " Took: " + (System.currentTimeMillis() - start));

            // ABOVE SHOW that both structure have approx the same efficiency.  However, the "featuresCostStr" structure is
            // better than the "costForYear2021" json structure. BUT what about de-serializing (the other way)
            //      Pattern  TypeReference<Map< _TYPE_, _typereference_ >> () {}

            TypeFactory typeFactory = mapper.getTypeFactory();
            MapType featurePriceMapType = typeFactory.constructMapType(HashMap.class, FEATURE.class, Price.class);
            JavaType trimType = typeFactory.constructType(TRIM.class);
            JavaType modelType = typeFactory.constructType(MODEL.class);
            JavaType brandType = typeFactory.constructType(BRAND.class);
            JavaType integerYearType = typeFactory.constructType(Integer.class);
            JavaType yearType = typeFactory.constructType(java.time.Year.class);

            MapType trimMapType = typeFactory.constructMapType(HashMap.class, trimType, featurePriceMapType);
            MapType modeleMapType = typeFactory.constructMapType(HashMap.class, modelType, trimMapType);
            MapType brandMapType = typeFactory.constructMapType(HashMap.class, brandType, modeleMapType);
//            MapType yearMapType = typeFactory.constructMapType(HashMap.class, integerYearType, brandMapType);
            MapType yearMapType = typeFactory.constructMapType(HashMap.class, yearType, brandMapType);

            Object o = mapper.readValue(featuresCostStr, yearMapType);
//            Map<Integer, Map<BRAND, Map<MODEL, Map<TRIM, Map<FEATURE, Price>>>>> mapCost = Collections.unmodifiableMap((Map<Integer, Map<BRAND, Map<MODEL, Map<TRIM, Map<FEATURE, Price>>>>>) o);
            Map<java.time.Year, Map<BRAND, Map<MODEL, Map<TRIM, Map<FEATURE, Price>>>>> mapCost = Collections.unmodifiableMap((Map<java.time.Year, Map<BRAND, Map<MODEL, Map<TRIM, Map<FEATURE, Price>>>>>) o);

            System.out.println("almost done");
        } catch (Exception e) {
            System.out.println("e = " + e);
        }


//        new TypeReference<Map<Year, HashMap<BRAND, HashMap<MODEL, HashMap<TRIM, HashMap<FEATURE, Price>>>>>> (){};
//
//        HashMap<Year, HashMap<BRAND, HashMap<MODEL, HashMap<TRIM, HashMap<FEATURE, Price>>>>> map = mapper.readValue(
//                featuresCostStr,
//                new TypeReference<HashMap<Year, HashMap<BRAND, HashMap<MODEL, HashMap<TRIM, HashMap<FEATURE, Price>>>>>> (){}
//        );
//        Map<BRAND, HashMap<MODEL, HashMap<TRIM, HashMap<FEATURE, Price>>>> brandMapMap = map.get(new Year(2021));

        System.out.println("done");
//        Year year = new Year(2020);

//        Double leatherHeatedSeatPrice = store.pricingBy
//                .apply(BRAND.FORD)
//                .apply(MODEL.EDGE)
//                .apply(TRIM.LTD)
//                .apply(FEATURE.LEATHER_HEATED_SEAT)
//                .price();
//        System.out.println("leatherHeatedSeatPrice = " + leatherHeatedSeatPrice);
//        // what if we want several prices i.e. decoration of several features for a given BRAND*MODEL*TRIM?
//        Function<FEATURE, Price> priceByToyotaCamryXLT = store.pricingBy
//                .apply(BRAND.TOYOTA)
//                .apply(MODEL.CAMRY)
//                .apply(TRIM.XLT);
//
//        // NOTE: getPriceOfFeatures does not compose functions but rather find the cost of each feature and sum them up.
//        System.out.println(
//                getPriceOfFeatures(
//                        priceByToyotaCamryXLT,
//                        FEATURE.ADAPTIVE_CRUISE_CONTROL,
//                        FEATURE.COLLISION_WARNING,
//                        FEATURE.LEATHER_HEATED_SEAT
//                )
//        );

        // ******************** COMPOSITION EXAMPLE - VERY DIFFICULT ********************************
//        Function<Price,Price> f1 = price -> new Price(price.price() + priceByToyotaCamryXLT.apply(FEATURE.LEATHER_HEATED_SEAT).price());
//        Function<Price,Price> f2 = price -> new Price(price.price() + priceByToyotaCamryXLT.apply(FEATURE.COLLISION_WARNING).price());
//        Function<Price, Price> newfunction = Stream.of(f1, f2).reduce(Function.identity(), Function<Price, Price>::andThen);
//        Price p = newfunction.apply(new Price(0.0));
//        System.out.println("price 1 = " + p); // p = Price[price=252.98]
        // ********************** COMPOSITION EXAMPLE END *************************************

        // Another way...
//        Function<Double,Double> f3 = price -> price + priceByToyotaCamryXLT.apply(FEATURE.LEATHER_HEATED_SEAT).price();
//        Function<Double,Double> f4 = price -> price + priceByToyotaCamryXLT.apply(FEATURE.COLLISION_WARNING).price();
//        Function<Double,Double> sumFn = f3.andThen(f4);
//        Double sum = sumFn.apply(0.0);
//        System.out.println("price 2 = " + sum); // price 2 = 252.98

        // Yet Another way...
//        DoubleUnaryOperator f5 = price -> price + priceByToyotaCamryXLT.apply(FEATURE.LEATHER_HEATED_SEAT).price();
//        DoubleUnaryOperator f6 = price -> price + priceByToyotaCamryXLT.apply(FEATURE.COLLISION_WARNING).price();
//        DoubleUnaryOperator sumFunct = f5.andThen(f6);
//        sum = sumFunct.applyAsDouble(0.0);
//        System.out.println("price 3 = " + sum); // price 3 = 252.98

        // Let see if we can compose any feature price
//        DoubleUnaryOperator calculateFeaturesCost = composeCarFeaturesCost(
//                priceByToyotaCamryXLT,
//                FEATURE.COLLISION_WARNING,
//                FEATURE.LEATHER_HEATED_SEAT
//        );
//        double cost = calculateFeaturesCost.applyAsDouble(0.0);
//        System.out.println("price 4 = " + cost); // price 4 = 252.98
//
//        calculateFeaturesCost = composeCarFeaturesCost(
//                store.pricingBy.apply(BRAND.FORD).apply(MODEL.EDGE).apply(TRIM.LTD),
//                FEATURE.LEATHER_HEATED_SEAT,
//                FEATURE.COLLISION_WARNING,
//                FEATURE.ADAPTIVE_CRUISE_CONTROL,
//                FEATURE.BLIND_SPOT_MONITORING,
//                FEATURE.CAMERA_360
//        );
//        double fordEdgeCost = calculateFeaturesCost.applyAsDouble(32000.0);
//        System.out.println("price 5 = " + fordEdgeCost); // price 5 = 32657.50

    }


    public static Map<String, Price> pricing = new HashMap<>() {{
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(89.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SL.name()  + FEATURE.COLLISION_WARNING.name(), new Price(89.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.LTD.name() + FEATURE.COLLISION_WARNING.name(), new Price(93.89));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(109.99));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SV.name()  + FEATURE.COLLISION_WARNING.name(), new Price(129.79));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.LTD.name() + FEATURE.COLLISION_WARNING.name(), new Price(139.50));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.SE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(125.69));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.LTD.name() + FEATURE.COLLISION_WARNING.name(), new Price(151.50));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.SE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(209.99));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.LX.name()  + FEATURE.COLLISION_WARNING.name(), new Price(229.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(79.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.LE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(89.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SLE.name() + FEATURE.COLLISION_WARNING.name(), new Price(99.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.XLT.name() + FEATURE.COLLISION_WARNING.name(), new Price(109.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.SLE.name() + FEATURE.COLLISION_WARNING.name(), new Price(119.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.XLT.name() + FEATURE.COLLISION_WARNING.name(), new Price(149.99));
        put(BRAND.TOYOTA.name() + MODEL.HIGHLANDER.name() + TRIM.XLT.name() + FEATURE.COLLISION_WARNING.name(), new Price(149.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(118.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SL.name()  + FEATURE.COLLISION_WARNING.name(), new Price(148.99));
        put(BRAND.TOYOTA.name() + MODEL.TUNDRA.name()     + TRIM.SE.name()  + FEATURE.COLLISION_WARNING.name(), new Price(158.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(52.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SL.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(53.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.LTD.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(73.89));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(92.99));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SV.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(109.79));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.LTD.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.SE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(79.69));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.LTD.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.SE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(158.99));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.LX.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(147.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(53.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.LE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(64.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SLE.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(77.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.XLT.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(88.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.SLE.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(91.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.XLT.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.HIGHLANDER.name() + TRIM.XLT.name() + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(123.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SL.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(111.99));
        put(BRAND.TOYOTA.name() + MODEL.TUNDRA.name()     + TRIM.SE.name()  + FEATURE.LEATHER_HEATED_SEAT.name(), new Price(135.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SE.name()  + FEATURE.CAMERA_360.name(), new Price(52.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SL.name()  + FEATURE.CAMERA_360.name(), new Price(53.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.LTD.name() + FEATURE.CAMERA_360.name(), new Price(73.89));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SE.name()  + FEATURE.CAMERA_360.name(), new Price(92.99));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SV.name()  + FEATURE.CAMERA_360.name(), new Price(109.79));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.LTD.name() + FEATURE.CAMERA_360.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.SE.name()  + FEATURE.CAMERA_360.name(), new Price(79.69));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.LTD.name() + FEATURE.CAMERA_360.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.SE.name()  + FEATURE.CAMERA_360.name(), new Price(158.99));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.LX.name()  + FEATURE.CAMERA_360.name(), new Price(147.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SE.name()  + FEATURE.CAMERA_360.name(), new Price(53.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.LE.name()  + FEATURE.CAMERA_360.name(), new Price(64.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SLE.name() + FEATURE.CAMERA_360.name(), new Price(77.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.XLT.name() + FEATURE.CAMERA_360.name(), new Price(88.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.SLE.name() + FEATURE.CAMERA_360.name(), new Price(91.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.XLT.name() + FEATURE.CAMERA_360.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.HIGHLANDER.name() + TRIM.XLT.name() + FEATURE.CAMERA_360.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SE.name()  + FEATURE.CAMERA_360.name(), new Price(123.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SL.name()  + FEATURE.CAMERA_360.name(), new Price(111.99));
        put(BRAND.TOYOTA.name() + MODEL.TUNDRA.name()     + TRIM.SE.name()  + FEATURE.CAMERA_360.name(), new Price(135.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(52.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SL.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(53.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.LTD.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(73.89));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(92.99));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SV.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(109.79));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.LTD.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.SE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(79.69));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.LTD.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.SE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(158.99));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.LX.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(147.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(53.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.LE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(64.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SLE.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(77.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.XLT.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(88.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.SLE.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(91.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.XLT.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.HIGHLANDER.name() + TRIM.XLT.name() + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(123.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SL.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(111.99));
        put(BRAND.TOYOTA.name() + MODEL.TUNDRA.name()     + TRIM.SE.name()  + FEATURE.BLIND_SPOT_MONITORING.name(), new Price(135.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(52.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.SL.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(53.99));
        put(BRAND.FORD.name()   + MODEL.ESCAPE.name()     + TRIM.LTD.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(73.89));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(92.99));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.SV.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(109.79));
        put(BRAND.FORD.name()   + MODEL.EDGE.name()       + TRIM.LTD.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.SE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(79.69));
        put(BRAND.FORD.name()   + MODEL.F_150.name()      + TRIM.LTD.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(129.50));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.SE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(158.99));
        put(BRAND.FORD.name()   + MODEL.EXPLORER.name()   + TRIM.LX.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(147.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(53.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.LE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(64.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.SLE.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(77.99));
        put(BRAND.TOYOTA.name() + MODEL.COROLLA.name()    + TRIM.XLT.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(88.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.SLE.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(91.99));
        put(BRAND.TOYOTA.name() + MODEL.CAMRY.name()      + TRIM.XLT.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.HIGHLANDER.name() + TRIM.XLT.name() + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(102.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(123.99));
        put(BRAND.TOYOTA.name() + MODEL.TACOMA.name()     + TRIM.SL.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(111.99));
        put(BRAND.TOYOTA.name() + MODEL.TUNDRA.name()     + TRIM.SE.name()  + FEATURE.ADAPTIVE_CRUISE_CONTROL.name(), new Price(135.99));
    }};
}
