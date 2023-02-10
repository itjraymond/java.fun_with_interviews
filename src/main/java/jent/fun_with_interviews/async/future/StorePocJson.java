package jent.fun_with_interviews.async.future;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;
import jent.fun_with_interviews.async.future.Store.FEATURE;

import java.time.Year;
import java.util.List;

import static jent.fun_with_interviews.async.future.Store.*;

public class StorePocJson {

    record Feature(
            FEATURE feature,
            Price price
    ){};

    record Features(
            List<Feature> features
    ){};

    record Trim(
            Price price,
            TRIM trim,
            Features features
    ){};

    record Trims(
            List<Trim> trims
    ){};

    record Model(
            MODEL model,
            Trims trims
    ){};

    record Models(
            List<Model> models
    ){};

    record Brand(
            BRAND brand,
            Models models
    ){};

    record Brands(
            List<Brand> brands
    ){};

    record YearMake(
            @JsonSerialize(using = YearSerializer.class)
            @JsonDeserialize(using = YearDeserializer.class)
            Year year,
            Brands brands
    ){};

    record YearMakes(
            List<YearMake> yearMakes
    ){};



    public static void main(String[] args) {

        Feature f1 = new Feature(FEATURE.BLIND_SPOT_MONITORING, new Price(123.45));
        Feature f2 = new Feature(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(234.56));
        Feature f3 = new Feature(FEATURE.COLLISION_WARNING, new Price(534.93));
        Features fs1 = new Features(List.of(f1,f2,f3));
        Features fs2 = new Features(List.of(f2,f3));
        Trim t1 = new Trim(new Price(32589.83), TRIM.SE, fs1);
        Trim t2 = new Trim(new Price(43200.36), TRIM.LE, fs2);
        Trims ts1 = new Trims(List.of(t1,t2));
        Model m1 = new Model(MODEL.EDGE, ts1);
        Models ms1 = new Models(List.of(m1));
        Brand b1 = new Brand(BRAND.FORD, ms1);
        Brands bs1 = new Brands(List.of(b1));
        YearMake y1 = new YearMake(Year.of(2021), bs1);
        YearMakes ys1 = new YearMakes(List.of(y1));

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Year.class, YearSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Year.class, YearDeserializer.INSTANCE);
        mapper.registerModule(javaTimeModule);

        try {
            String sModel = mapper.writeValueAsString(ys1);
            System.out.println("sModel = " + sModel);

        } catch (Exception e) {
            System.out.println("e = " + e);
        }

        // This will give the following:
        /*
{
  "yearMakes": [
    {
      "year": 2021,
      "brands": {
        "brands": [
          {
            "brand": "FORD",
            "models": {
              "models": [
                {
                  "model": "EDGE",
                  "trims": {
                    "trims": [
                      {
                        "price": {
                          "price": 32589.83
                        },
                        "trim": "SE",
                        "features": {
                          "features": [
                            {
                              "feature": "BLIND_SPOT_MONITORING",
                              "price": {
                                "price": 123.45
                              }
                            },
                            {
                              "feature": "ADAPTIVE_CRUISE_CONTROL",
                              "price": {
                                "price": 234.56
                              }
                            },
                            {
                              "feature": "COLLISION_WARNING",
                              "price": {
                                "price": 534.93
                              }
                            }
                          ]
                        }
                      },
                      {
                        "price": {
                          "price": 43200.36
                        },
                        "trim": "LE",
                        "features": {
                          "features": [
                            {
                              "feature": "ADAPTIVE_CRUISE_CONTROL",
                              "price": {
                                "price": 234.56
                              }
                            },
                            {
                              "feature": "COLLISION_WARNING",
                              "price": {
                                "price": 534.93
                              }
                            }
                          ]
                        }
                      }
                    ]
                  }
                }
              ]
            }
          }
        ]
      }
    }
  ]
}

         */


    }
}
