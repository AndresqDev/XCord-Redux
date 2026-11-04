package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public class ReflectionToStringBuilder extends ToStringBuilder {
   private boolean appendStatics = false;
   private boolean appendTransients = false;
   private boolean excludeNullValues;
   protected String[] excludeFieldNames;
   private Class<?> upToClass = null;

   public static String toString(Object var0) {
      return toString(var0, null, false, false, null);
   }

   public static String toString(Object var0, ToStringStyle var1) {
      return toString(var0, var1, false, false, null);
   }

   public static String toString(Object var0, ToStringStyle var1, boolean var2) {
      return toString(var0, var1, var2, false, null);
   }

   public static String toString(Object var0, ToStringStyle var1, boolean var2, boolean var3) {
      return toString(var0, var1, var2, var3, null);
   }

   public static <T> String toString(T var0, ToStringStyle var1, boolean var2, boolean var3, Class<? super T> var4) {
      return new ReflectionToStringBuilder(var0, var1, null, var4, var2, var3).toString();
   }

   public static <T> String toString(T var0, ToStringStyle var1, boolean var2, boolean var3, boolean var4, Class<? super T> var5) {
      return new ReflectionToStringBuilder(var0, var1, null, var5, var2, var3, var4).toString();
   }

   public static String toStringExclude(Object var0, Collection<String> var1) {
      return toStringExclude(var0, toNoNullStringArray(var1));
   }

   static String[] toNoNullStringArray(Collection<String> var0) {
      return var0 == null ? ArrayUtils.EMPTY_STRING_ARRAY : toNoNullStringArray(var0.toArray());
   }

   static String[] toNoNullStringArray(Object[] var0) {
      ArrayList var1 = new ArrayList(var0.length);

      for (Object var5 : var0) {
         if (var5 != null) {
            var1.add(var5.toString());
         }
      }

      return var1.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
   }

   public static String toStringExclude(Object var0, String... var1) {
      return new ReflectionToStringBuilder(var0).setExcludeFieldNames(var1).toString();
   }

   private static Object checkNotNull(Object var0) {
      Validate.isTrue(var0 != null, "The Object passed in should not be null.");
      return var0;
   }

   public ReflectionToStringBuilder(Object var1) {
      super(checkNotNull(var1));
   }

   public ReflectionToStringBuilder(Object var1, ToStringStyle var2) {
      super(checkNotNull(var1), var2);
   }

   public ReflectionToStringBuilder(Object var1, ToStringStyle var2, StringBuffer var3) {
      super(checkNotNull(var1), var2, var3);
   }

   public <T> ReflectionToStringBuilder(T var1, ToStringStyle var2, StringBuffer var3, Class<? super T> var4, boolean var5, boolean var6) {
      super(checkNotNull(var1), var2, var3);
      this.setUpToClass(var4);
      this.setAppendTransients(var5);
      this.setAppendStatics(var6);
   }

   public <T> ReflectionToStringBuilder(T var1, ToStringStyle var2, StringBuffer var3, Class<? super T> var4, boolean var5, boolean var6, boolean var7) {
      super(checkNotNull(var1), var2, var3);
      this.setUpToClass(var4);
      this.setAppendTransients(var5);
      this.setAppendStatics(var6);
      this.setExcludeNullValues(var7);
   }

   protected boolean accept(Field var1) {
      if (var1.getName().indexOf(36) != -1) {
         return false;
      } else if (Modifier.isTransient(var1.getModifiers()) && !this.isAppendTransients()) {
         return false;
      } else if (Modifier.isStatic(var1.getModifiers()) && !this.isAppendStatics()) {
         return false;
      } else {
         return this.excludeFieldNames != null && Arrays.binarySearch(this.excludeFieldNames, var1.getName()) >= 0
            ? false
            : !var1.isAnnotationPresent(ToStringExclude.class);
      }
   }

   protected void appendFieldsIn(Class<?> var1) {
      if (var1.isArray()) {
         this.reflectionAppendArray(this.getObject());
      } else {
         Field[] var2 = var1.getDeclaredFields();
         AccessibleObject.setAccessible(var2, true);

         for (Field var6 : var2) {
            String var7 = var6.getName();
            if (this.accept(var6)) {
               try {
                  Object var8 = this.getValue(var6);
                  if (!this.excludeNullValues || var8 != null) {
                     this.append(var7, var8, !var6.isAnnotationPresent(ToStringSummary.class));
                  }
               } catch (IllegalAccessException var9) {
                  throw new InternalError("Unexpected IllegalAccessException: " + var9.getMessage());
               }
            }
         }
      }
   }

   public String[] getExcludeFieldNames() {
      return (String[])this.excludeFieldNames.clone();
   }

   public Class<?> getUpToClass() {
      return this.upToClass;
   }

   protected Object getValue(Field var1) throws IllegalAccessException {
      return var1.get(this.getObject());
   }

   public boolean isAppendStatics() {
      return this.appendStatics;
   }

   public boolean isAppendTransients() {
      return this.appendTransients;
   }

   public boolean isExcludeNullValues() {
      return this.excludeNullValues;
   }

   public ReflectionToStringBuilder reflectionAppendArray(Object var1) {
      this.getStyle().reflectionAppendArrayDetail(this.getStringBuffer(), null, var1);
      return this;
   }

   public void setAppendStatics(boolean var1) {
      this.appendStatics = var1;
   }

   public void setAppendTransients(boolean var1) {
      this.appendTransients = var1;
   }

   public void setExcludeNullValues(boolean var1) {
      this.excludeNullValues = var1;
   }

   public ReflectionToStringBuilder setExcludeFieldNames(String... var1) {
      if (var1 == null) {
         this.excludeFieldNames = null;
      } else {
         this.excludeFieldNames = toNoNullStringArray(var1);
         Arrays.sort((Object[])this.excludeFieldNames);
      }

      return this;
   }

   public void setUpToClass(Class<?> var1) {
      if (var1 != null) {
         Object var2 = this.getObject();
         if (var2 != null && !var1.isInstance(var2)) {
            throw new IllegalArgumentException("Specified class is not a superclass of the object");
         }
      }

      this.upToClass = var1;
   }

   @Override
   public String toString() {
      if (this.getObject() == null) {
         return this.getStyle().getNullText();
      } else {
         Class var1 = this.getObject().getClass();
         this.appendFieldsIn(var1);

         while (var1.getSuperclass() != null && var1 != this.getUpToClass()) {
            var1 = var1.getSuperclass();
            this.appendFieldsIn(var1);
         }

         return super.toString();
      }
   }
}
