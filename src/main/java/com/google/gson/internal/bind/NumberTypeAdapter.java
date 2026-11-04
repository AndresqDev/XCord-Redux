package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public final class NumberTypeAdapter extends TypeAdapter<Number> {
   private static final TypeAdapterFactory LAZILY_PARSED_NUMBER_FACTORY = newFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER);
   private final ToNumberStrategy toNumberStrategy;

   private NumberTypeAdapter(ToNumberStrategy var1) {
      this.toNumberStrategy = var1;
   }

   private static TypeAdapterFactory newFactory(ToNumberStrategy var0) {
      final NumberTypeAdapter var1 = new NumberTypeAdapter(var0);
      return new TypeAdapterFactory() {
         @Override
         public <T> TypeAdapter<T> create(Gson var1x, TypeToken<T> var2) {
            return var2.getRawType() == Number.class ? var1 : null;
         }
      };
   }

   public static TypeAdapterFactory getFactory(ToNumberStrategy var0) {
      return var0 == ToNumberPolicy.LAZILY_PARSED_NUMBER ? LAZILY_PARSED_NUMBER_FACTORY : newFactory(var0);
   }

   public Number read(JsonReader var1) throws IOException {
      JsonToken var2 = var1.peek();
      switch (var2) {
         case NULL:
            var1.nextNull();
            return null;
         case NUMBER:
         case STRING:
            return this.toNumberStrategy.readNumber(var1);
         default:
            throw new JsonSyntaxException("Expecting number, got: " + var2);
      }
   }

   public void write(JsonWriter var1, Number var2) throws IOException {
      var1.value(var2);
   }
}
