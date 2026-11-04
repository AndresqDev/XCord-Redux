package com.sun.jna;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTypeMapper implements TypeMapper {
   private List<DefaultTypeMapper.Entry> toNativeConverters = new ArrayList<>();
   private List<DefaultTypeMapper.Entry> fromNativeConverters = new ArrayList<>();

   private Class<?> getAltClass(Class<?> var1) {
      if (var1 == Boolean.class) {
         return boolean.class;
      } else if (var1 == boolean.class) {
         return Boolean.class;
      } else if (var1 == Byte.class) {
         return byte.class;
      } else if (var1 == byte.class) {
         return Byte.class;
      } else if (var1 == Character.class) {
         return char.class;
      } else if (var1 == char.class) {
         return Character.class;
      } else if (var1 == Short.class) {
         return short.class;
      } else if (var1 == short.class) {
         return Short.class;
      } else if (var1 == Integer.class) {
         return int.class;
      } else if (var1 == int.class) {
         return Integer.class;
      } else if (var1 == Long.class) {
         return long.class;
      } else if (var1 == long.class) {
         return Long.class;
      } else if (var1 == Float.class) {
         return float.class;
      } else if (var1 == float.class) {
         return Float.class;
      } else if (var1 == Double.class) {
         return double.class;
      } else {
         return var1 == double.class ? Double.class : null;
      }
   }

   public void addToNativeConverter(Class<?> var1, ToNativeConverter var2) {
      this.toNativeConverters.add(new DefaultTypeMapper.Entry(var1, var2));
      Class var3 = this.getAltClass(var1);
      if (var3 != null) {
         this.toNativeConverters.add(new DefaultTypeMapper.Entry(var3, var2));
      }
   }

   public void addFromNativeConverter(Class<?> var1, FromNativeConverter var2) {
      this.fromNativeConverters.add(new DefaultTypeMapper.Entry(var1, var2));
      Class var3 = this.getAltClass(var1);
      if (var3 != null) {
         this.fromNativeConverters.add(new DefaultTypeMapper.Entry(var3, var2));
      }
   }

   public void addTypeConverter(Class<?> var1, TypeConverter var2) {
      this.addFromNativeConverter(var1, var2);
      this.addToNativeConverter(var1, var2);
   }

   private Object lookupConverter(Class<?> var1, Collection<? extends DefaultTypeMapper.Entry> var2) {
      for (DefaultTypeMapper.Entry var4 : var2) {
         if (var4.type.isAssignableFrom(var1)) {
            return var4.converter;
         }
      }

      return null;
   }

   @Override
   public FromNativeConverter getFromNativeConverter(Class<?> var1) {
      return (FromNativeConverter)this.lookupConverter(var1, this.fromNativeConverters);
   }

   @Override
   public ToNativeConverter getToNativeConverter(Class<?> var1) {
      return (ToNativeConverter)this.lookupConverter(var1, this.toNativeConverters);
   }

   private static class Entry {
      public Class<?> type;
      public Object converter;

      public Entry(Class<?> var1, Object var2) {
         this.type = var1;
         this.converter = var2;
      }
   }
}
