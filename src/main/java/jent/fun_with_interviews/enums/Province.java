package jent.fun_with_interviews.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableMap;

public enum Province {
    AB("Alberta"),
    BC("British Columbia"),
    MA("Manitoba"),
    NB("New Brunswick"),
    NF("Newfoundland and Labrador"),
    NT("Northwest Territories"),
    NS("Nova Scotia"),
    NU("Nunavut"),
    ON("Ontario"),
    PE("Prince Edward Island"),
    QC("Québec"),
    SK("Saskatchewan"),
    YT("Yukon")
    ;

    private final String provinceName;

    Province(String name) {
        this.provinceName = name;
    }

    public String getProvinceName() {
        return provinceName;
    }

    /**
     * fromCod "is" a hashmap which will be "filled" when Province is loaded by the class loader.
     *
     * Usage: An outside user provide the province code "AB" - a String
     * Province.fromCode.getOrDefault("AB",null); // return Province.AB
     */
    public static final Map<String, Province> fromCode = Arrays.stream(Province.values()).collect(toUnmodifiableMap(
            Province::name,     // e.g. AB
            Function.identity() // e.g. Province.AB
    ));

    /**
     * We can also provide a mapping for the long province name to Province enum type.
     * Usage:
     * Province p = Province.fromName.getOrDefault("British Columbia", null); // return Province.BC
     */
    public static final Map<String,Province> fromName = Arrays.stream(Province.values()).collect(toUnmodifiableMap(
            Province::getProvinceName, // e.g. "British Columbia"
            Function.identity()
    ));
}
