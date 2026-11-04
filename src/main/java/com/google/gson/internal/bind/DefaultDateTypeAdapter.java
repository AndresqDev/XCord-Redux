package com.google.gson.internal.bind;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.JavaVersion;
import com.google.gson.internal.PreJava9DateFormatProvider;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {
   private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
   private final DefaultDateTypeAdapter.DateType<T> dateType;
   private final List<DateFormat> dateFormats = new ArrayList<>();

   private DefaultDateTypeAdapter(DefaultDateTypeAdapter.DateType<T> var1, String var2) {
      this.dateType = $Gson$Preconditions.checkNotNull(var1);
      this.dateFormats.add(new SimpleDateFormat(var2, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(new SimpleDateFormat(var2));
      }
   }

   private DefaultDateTypeAdapter(DefaultDateTypeAdapter.DateType<T> var1, int var2) {
      this.dateType = $Gson$Preconditions.checkNotNull(var1);
      this.dateFormats.add(DateFormat.getDateInstance(var2, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(DateFormat.getDateInstance(var2));
      }

      if (JavaVersion.isJava9OrLater()) {
         this.dateFormats.add(PreJava9DateFormatProvider.getUSDateFormat(var2));
      }
   }

   private DefaultDateTypeAdapter(DefaultDateTypeAdapter.DateType<T> var1, int var2, int var3) {
      this.dateType = $Gson$Preconditions.checkNotNull(var1);
      this.dateFormats.add(DateFormat.getDateTimeInstance(var2, var3, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(DateFormat.getDateTimeInstance(var2, var3));
      }

      if (JavaVersion.isJava9OrLater()) {
         this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(var2, var3));
      }
   }

   public void write(JsonWriter var1, Date var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         synchronized (this.dateFormats) {
            String var4 = this.dateFormats.get(0).format(var2);
            var1.value(var4);
         }
      }
   }

   public T read(JsonReader var1) throws IOException {
      if (var1.peek() == JsonToken.NULL) {
         var1.nextNull();
         return null;
      } else {
         Date var2 = this.deserializeToDate(var1.nextString());
         return this.dateType.deserialize(var2);
      }
   }

   private Date deserializeToDate(String var1) {
      synchronized (this.dateFormats) {
         for (DateFormat var4 : this.dateFormats) {
            Date var10000;
            try {
               var10000 = var4.parse(var1);
            } catch (ParseException var8) {
               continue;
            }

            return var10000;
         }
      }

      try {
         return ISO8601Utils.parse(var1, new ParsePosition(0));
      } catch (ParseException var7) {
         throw new JsonSyntaxException(var1, var7);
      }
   }

   @Override
   public String toString() {
      DateFormat var1 = this.dateFormats.get(0);
      return var1 instanceof SimpleDateFormat
         ? "DefaultDateTypeAdapter(" + ((SimpleDateFormat)var1).toPattern() + ')'
         : "DefaultDateTypeAdapter(" + var1.getClass().getSimpleName() + ')';
   }

   public abstract static class DateType<T extends Date> {
      public static final DefaultDateTypeAdapter.DateType<Date> DATE = new DefaultDateTypeAdapter.DateType<Date>(Date.class) {
         @Override
         protected Date deserialize(Date var1) {
            return var1;
         }
      };
      private final Class<T> dateClass;

      protected DateType(Class<T> var1) {
         this.dateClass = var1;
      }

      protected abstract T deserialize(Date var1);

      private final TypeAdapterFactory createFactory(DefaultDateTypeAdapter<T> var1) {
         return TypeAdapters.newFactory(this.dateClass, var1);
      }

      public final TypeAdapterFactory createAdapterFactory(String var1) {
         return this.createFactory(new DefaultDateTypeAdapter<>(this, var1));
      }

      public final TypeAdapterFactory createAdapterFactory(int var1) {
         return this.createFactory(new DefaultDateTypeAdapter<>(this, var1));
      }

      public final TypeAdapterFactory createAdapterFactory(int var1, int var2) {
         return this.createFactory(new DefaultDateTypeAdapter<>(this, var1, var2));
      }

      public final TypeAdapterFactory createDefaultsAdapterFactory() {
         return this.createFactory(new DefaultDateTypeAdapter<>(this, 2, 2));
      }
   }
}
