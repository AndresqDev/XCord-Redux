package com.sun.jna.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.StringArray;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;

public class W32APITypeMapper extends DefaultTypeMapper {
   public static final TypeMapper UNICODE = new W32APITypeMapper(true);
   public static final TypeMapper ASCII = new W32APITypeMapper(false);
   public static final TypeMapper DEFAULT = Boolean.getBoolean("w32.ascii") ? ASCII : UNICODE;

   protected W32APITypeMapper(boolean var1) {
      if (var1) {
         TypeConverter var2 = new TypeConverter() {
            @Override
            public Object toNative(Object var1, ToNativeContext var2) {
               if (var1 == null) {
                  return null;
               } else {
                  return var1 instanceof String[] ? new StringArray((String[])var1, true) : new WString(var1.toString());
               }
            }

            @Override
            public Object fromNative(Object var1, FromNativeContext var2) {
               return var1 == null ? null : var1.toString();
            }

            @Override
            public Class<?> nativeType() {
               return WString.class;
            }
         };
         this.addTypeConverter(String.class, var2);
         this.addToNativeConverter(String[].class, var2);
      }

      TypeConverter var3 = new TypeConverter() {
         @Override
         public Object toNative(Object var1, ToNativeContext var2) {
            return Boolean.TRUE.equals(var1) ? 1 : 0;
         }

         @Override
         public Object fromNative(Object var1, FromNativeContext var2) {
            return (Integer)var1 != 0 ? Boolean.TRUE : Boolean.FALSE;
         }

         @Override
         public Class<?> nativeType() {
            return Integer.class;
         }
      };
      this.addTypeConverter(Boolean.class, var3);
   }
}
