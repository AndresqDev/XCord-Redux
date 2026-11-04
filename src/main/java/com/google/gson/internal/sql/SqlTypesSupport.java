package com.google.gson.internal.sql;

import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import java.sql.Timestamp;
import java.util.Date;

public final class SqlTypesSupport {
   public static final boolean SUPPORTS_SQL_TYPES;
   public static final DefaultDateTypeAdapter.DateType<? extends Date> DATE_DATE_TYPE;
   public static final DefaultDateTypeAdapter.DateType<? extends Date> TIMESTAMP_DATE_TYPE;
   public static final TypeAdapterFactory DATE_FACTORY;
   public static final TypeAdapterFactory TIME_FACTORY;
   public static final TypeAdapterFactory TIMESTAMP_FACTORY;

   private SqlTypesSupport() {
   }

   static {
      boolean var0;
      try {
         Class.forName("java.sql.Date");
         var0 = true;
      } catch (ClassNotFoundException var2) {
         var0 = false;
      }

      SUPPORTS_SQL_TYPES = var0;
      if (SUPPORTS_SQL_TYPES) {
         DATE_DATE_TYPE = new DefaultDateTypeAdapter.DateType<java.sql.Date>(java.sql.Date.class) {
            protected java.sql.Date deserialize(Date var1) {
               return new java.sql.Date(var1.getTime());
            }
         };
         TIMESTAMP_DATE_TYPE = new DefaultDateTypeAdapter.DateType<Timestamp>(Timestamp.class) {
            protected Timestamp deserialize(Date var1) {
               return new Timestamp(var1.getTime());
            }
         };
         DATE_FACTORY = SqlDateTypeAdapter.FACTORY;
         TIME_FACTORY = SqlTimeTypeAdapter.FACTORY;
         TIMESTAMP_FACTORY = SqlTimestampTypeAdapter.FACTORY;
      } else {
         DATE_DATE_TYPE = null;
         TIMESTAMP_DATE_TYPE = null;
         DATE_FACTORY = null;
         TIME_FACTORY = null;
         TIMESTAMP_FACTORY = null;
      }
   }
}
