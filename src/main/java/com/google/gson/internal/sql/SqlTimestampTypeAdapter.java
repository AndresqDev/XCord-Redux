package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

class SqlTimestampTypeAdapter extends TypeAdapter<Timestamp> {
   static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         if (var2.getRawType() == Timestamp.class) {
            TypeAdapter var3 = var1.getAdapter(Date.class);
            return new SqlTimestampTypeAdapter(var3);
         } else {
            return null;
         }
      }
   };
   private final TypeAdapter<Date> dateTypeAdapter;

   private SqlTimestampTypeAdapter(TypeAdapter<Date> var1) {
      this.dateTypeAdapter = var1;
   }

   public Timestamp read(JsonReader var1) throws IOException {
      Date var2 = this.dateTypeAdapter.read(var1);
      return var2 != null ? new Timestamp(var2.getTime()) : null;
   }

   public void write(JsonWriter var1, Timestamp var2) throws IOException {
      this.dateTypeAdapter.write(var1, var2);
   }
}
