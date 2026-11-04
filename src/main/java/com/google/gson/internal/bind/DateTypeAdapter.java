package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JavaVersion;
import com.google.gson.internal.PreJava9DateFormatProvider;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DateTypeAdapter extends TypeAdapter<Date> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         return var2.getRawType() == Date.class ? new DateTypeAdapter() : null;
      }
   };
   private final List<DateFormat> dateFormats = new ArrayList<>();

   public DateTypeAdapter() {
      this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2));
      }

      if (JavaVersion.isJava9OrLater()) {
         this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(2, 2));
      }
   }

   public Date read(JsonReader var1) throws IOException {
      if (var1.peek() == JsonToken.NULL) {
         var1.nextNull();
         return null;
      } else {
         return this.deserializeToDate(var1.nextString());
      }
   }

   private synchronized Date deserializeToDate(String var1) {
      for (DateFormat var3 : this.dateFormats) {
         try {
            return var3.parse(var1);
         } catch (ParseException var6) {
         }
      }

      try {
         return ISO8601Utils.parse(var1, new ParsePosition(0));
      } catch (ParseException var5) {
         throw new JsonSyntaxException(var1, var5);
      }
   }

   public synchronized void write(JsonWriter var1, Date var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         String var3 = this.dateFormats.get(0).format(var2);
         var1.value(var3);
      }
   }
}
