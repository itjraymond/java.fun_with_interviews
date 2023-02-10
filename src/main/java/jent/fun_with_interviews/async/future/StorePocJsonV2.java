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

import static jent.fun_with_interviews.async.future.Store.BRAND;
import static jent.fun_with_interviews.async.future.Store.MODEL;
import static jent.fun_with_interviews.async.future.Store.Price;
import static jent.fun_with_interviews.async.future.Store.TRIM;

public class StorePocJsonV2 {

    record Feature(
            FEATURE feature,
            Price price
    ){};

    record Trim(
            Price price,
            TRIM trim,
            List<Feature> features
    ){};

    record Model(
            MODEL model,
            List<Trim> trims
    ){};

    record Brand(
            BRAND brand,
            List<Model> models
    ){};

    record YearMake(
            @JsonSerialize(using = YearSerializer.class)
            @JsonDeserialize(using = YearDeserializer.class)
            Year year,
            List<Brand> brands
    ){};

    record YearMakes(
            List<YearMake> yearMakes
    ){};



    public static void main(String[] args) {

        Feature f1 = new Feature(FEATURE.BLIND_SPOT_MONITORING, new Price(123.45));
        Feature f2 = new Feature(FEATURE.ADAPTIVE_CRUISE_CONTROL, new Price(234.56));
        Feature f3 = new Feature(FEATURE.COLLISION_WARNING, new Price(534.93));
        Trim t1 = new Trim(new Price(32589.83), TRIM.SE, List.of(f1,f2,f3));
        Trim t2 = new Trim(new Price(43200.36), TRIM.LE, List.of(f2,f3));
        Model m1 = new Model(MODEL.EDGE, List.of(t1,t2));
        Brand b1 = new Brand(BRAND.FORD, List.of(m1));
        YearMake y1 = new YearMake(Year.of(2021), List.of(b1));
        YearMakes ys1 = new YearMakes(List.of(y1));

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Year.class, YearSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Year.class, YearDeserializer.INSTANCE);
        mapper.registerModule(javaTimeModule);

        try {
            String sModel = mapper.writeValueAsString(ys1);
            System.out.println("String Model = " + sModel);  // see output example at end of file below

            YearMakes model = mapper.readValue(sModel, YearMakes.class);
            Year y = model.yearMakes().get(0).year();
            System.out.println("year = " + y);

            String sModel2 = mapper.writeValueAsString(model);

            System.out.println("Are the two models same after serialization, deserialization and reserialzation: " + sModel.equals(sModel2));

        } catch (Exception e) {
            System.out.println("e = " + e);
        }

    }
}


// serialization: This will give the following json model: MUCH BETTER
/*
{
  "yearMakes": [
    {
      "year": 2021,
      "brands": [
        {
          "brand": "FORD",
          "models": [
            {
              "model": "EDGE",
              "trims": [
                {
                  "price": {
                    "price": 32589.83
                  },
                  "trim": "SE",
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
                },
                {
                  "price": {
                    "price": 43200.36
                  },
                  "trim": "LE",
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
              ]
            }
          ]
        }
      ]
    }
  ]
}

*/



