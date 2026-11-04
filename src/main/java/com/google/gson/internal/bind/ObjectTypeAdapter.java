package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class ObjectTypeAdapter extends TypeAdapter<Object> {
   private static final TypeAdapterFactory DOUBLE_FACTORY = newFactory(ToNumberPolicy.DOUBLE);
   private final Gson gson;
   private final ToNumberStrategy toNumberStrategy;

   private ObjectTypeAdapter(Gson var1, ToNumberStrategy var2) {
      this.gson = var1;
      this.toNumberStrategy = var2;
   }

   private static TypeAdapterFactory newFactory(final ToNumberStrategy var0) {
      return new TypeAdapterFactory() {
         @Override
         public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
            return var2.getRawType() == Object.class ? new ObjectTypeAdapter(var1, var0) : null;
         }
      };
   }

   public static TypeAdapterFactory getFactory(ToNumberStrategy var0) {
      return var0 == ToNumberPolicy.DOUBLE ? DOUBLE_FACTORY : newFactory(var0);
   }

   @Override
   public Object read(JsonReader var1) throws IOException {
      JsonToken var2 = var1.peek();
      switch (var2) {
         case BEGIN_ARRAY:
            ArrayList var3 = new ArrayList();
            var1.beginArray();

            while (var1.hasNext()) {
               var3.add(this.read(var1));
            }

            var1.endArray();
            return var3;
         case BEGIN_OBJECT:
            LinkedTreeMap var4 = new LinkedTreeMap();
            var1.beginObject();

            while (var1.hasNext()) {
               var4.put(var1.nextName(), this.read(var1));
            }

            var1.endObject();
            return var4;
         case STRING:
            return var1.nextString();
         case NUMBER:
            return this.toNumberStrategy.readNumber(var1);
         case BOOLEAN:
            return var1.nextBoolean();
         case NULL:
            var1.nextNull();
            return null;
         default:
            throw new IllegalStateException();
      }
   }

   @Override
   public void write(JsonWriter var1, Object var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         TypeAdapter var3 = this.gson.getAdapter(var2.getClass());
         if (var3 instanceof ObjectTypeAdapter) {
            var1.beginObject();
            var1.endObject();
         } else {
            var3.write(var1, var2);
         }
      }
   }
}
