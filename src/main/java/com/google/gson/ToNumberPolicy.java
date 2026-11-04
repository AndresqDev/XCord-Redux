package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.math.BigDecimal;

public enum ToNumberPolicy implements ToNumberStrategy {
   DOUBLE {
      public Double readNumber(JsonReader var1) throws IOException {
         return var1.nextDouble();
      }
   },
   LAZILY_PARSED_NUMBER {
      @Override
      public Number readNumber(JsonReader var1) throws IOException {
         return new LazilyParsedNumber(var1.nextString());
      }
   },
   LONG_OR_DOUBLE {
      @Override
      public Number readNumber(JsonReader var1) throws IOException, JsonParseException {
         String var2 = var1.nextString();

         try {
            return Long.parseLong(var2);
         } catch (NumberFormatException var6) {
            try {
               Double var4 = Double.valueOf(var2);
               if ((var4.isInfinite() || var4.isNaN()) && !var1.isLenient()) {
                  throw new MalformedJsonException("JSON forbids NaN and infinities: " + var4 + "; at path " + var1.getPath());
               } else {
                  return var4;
               }
            } catch (NumberFormatException var5) {
               throw new JsonParseException("Cannot parse " + var2 + "; at path " + var1.getPath(), var5);
            }
         }
      }
   },
   BIG_DECIMAL {
      public BigDecimal readNumber(JsonReader var1) throws IOException {
         String var2 = var1.nextString();

         try {
            return new BigDecimal(var2);
         } catch (NumberFormatException var4) {
            throw new JsonParseException("Cannot parse " + var2 + "; at path " + var1.getPath(), var4);
         }
      }
   };

   private ToNumberPolicy() {
   }
}
