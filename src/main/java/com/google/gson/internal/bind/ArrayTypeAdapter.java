package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public final class ArrayTypeAdapter<E> extends TypeAdapter<Object> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         Type var3 = var2.getType();
         if (var3 instanceof GenericArrayType || var3 instanceof Class && ((Class)var3).isArray()) {
            Type var4 = $Gson$Types.getArrayComponentType(var3);
            TypeAdapter var5 = var1.getAdapter(TypeToken.get(var4));
            return new ArrayTypeAdapter(var1, var5, (Class<E>)$Gson$Types.getRawType(var4));
         } else {
            return null;
         }
      }
   };
   private final Class<E> componentType;
   private final TypeAdapter<E> componentTypeAdapter;

   public ArrayTypeAdapter(Gson var1, TypeAdapter<E> var2, Class<E> var3) {
      this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(var1, var2, var3);
      this.componentType = var3;
   }

   @Override
   public Object read(JsonReader var1) throws IOException {
      if (var1.peek() == JsonToken.NULL) {
         var1.nextNull();
         return null;
      } else {
         ArrayList var2 = new ArrayList();
         var1.beginArray();

         while (var1.hasNext()) {
            Object var3 = this.componentTypeAdapter.read(var1);
            var2.add(var3);
         }

         var1.endArray();
         int var6 = var2.size();
         Object var4 = Array.newInstance(this.componentType, var6);

         for (int var5 = 0; var5 < var6; var5++) {
            Array.set(var4, var5, var2.get(var5));
         }

         return var4;
      }
   }

   @Override
   public void write(JsonWriter var1, Object var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         var1.beginArray();
         int var3 = 0;

         for (int var4 = Array.getLength(var2); var3 < var4; var3++) {
            Object var5 = Array.get(var2, var3);
            this.componentTypeAdapter.write(var1, (E)var5);
         }

         var1.endArray();
      }
   }
}
