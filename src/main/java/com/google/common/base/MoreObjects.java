package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class MoreObjects {
   public static <T> T firstNonNull(T var0, T var1) {
      if (var0 != null) {
         return (T)var0;
      } else if (var1 != null) {
         return (T)var1;
      } else {
         throw new NullPointerException("Both parameters are null");
      }
   }

   public static MoreObjects.ToStringHelper toStringHelper(Object var0) {
      return new MoreObjects.ToStringHelper(var0.getClass().getSimpleName());
   }

   public static MoreObjects.ToStringHelper toStringHelper(Class<?> var0) {
      return new MoreObjects.ToStringHelper(var0.getSimpleName());
   }

   public static MoreObjects.ToStringHelper toStringHelper(String var0) {
      return new MoreObjects.ToStringHelper(var0);
   }

   private MoreObjects() {
   }

   public static final class ToStringHelper {
      private final String className;
      private final MoreObjects.ToStringHelper.ValueHolder holderHead = new MoreObjects.ToStringHelper.ValueHolder();
      private MoreObjects.ToStringHelper.ValueHolder holderTail = this.holderHead;
      private boolean omitNullValues = false;
      private boolean omitEmptyValues = false;

      private ToStringHelper(String var1) {
         this.className = Preconditions.checkNotNull(var1);
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper omitNullValues() {
         this.omitNullValues = true;
         return this;
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper add(String var1, Object var2) {
         return this.addHolder(var1, var2);
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper add(String var1, boolean var2) {
         return this.addUnconditionalHolder(var1, String.valueOf(var2));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper add(String var1, char var2) {
         return this.addUnconditionalHolder(var1, String.valueOf(var2));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper add(String var1, double var2) {
         return this.addUnconditionalHolder(var1, String.valueOf(var2));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper add(String var1, float var2) {
         return this.addUnconditionalHolder(var1, String.valueOf(var2));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper add(String var1, int var2) {
         return this.addUnconditionalHolder(var1, String.valueOf(var2));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper add(String var1, long var2) {
         return this.addUnconditionalHolder(var1, String.valueOf(var2));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper addValue(Object var1) {
         return this.addHolder(var1);
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper addValue(boolean var1) {
         return this.addUnconditionalHolder(String.valueOf(var1));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper addValue(char var1) {
         return this.addUnconditionalHolder(String.valueOf(var1));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper addValue(double var1) {
         return this.addUnconditionalHolder(String.valueOf(var1));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper addValue(float var1) {
         return this.addUnconditionalHolder(String.valueOf(var1));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper addValue(int var1) {
         return this.addUnconditionalHolder(String.valueOf(var1));
      }

      @CanIgnoreReturnValue
      public MoreObjects.ToStringHelper addValue(long var1) {
         return this.addUnconditionalHolder(String.valueOf(var1));
      }

      private static boolean isEmpty(Object var0) {
         if (var0 instanceof CharSequence) {
            return ((CharSequence)var0).length() == 0;
         } else if (var0 instanceof Collection) {
            return ((Collection)var0).isEmpty();
         } else if (var0 instanceof Map) {
            return ((Map)var0).isEmpty();
         } else if (var0 instanceof java.util.Optional) {
            return !((java.util.Optional)var0).isPresent();
         } else if (var0 instanceof OptionalInt) {
            return !((OptionalInt)var0).isPresent();
         } else if (var0 instanceof OptionalLong) {
            return !((OptionalLong)var0).isPresent();
         } else if (var0 instanceof OptionalDouble) {
            return !((OptionalDouble)var0).isPresent();
         } else if (var0 instanceof Optional) {
            return !((Optional)var0).isPresent();
         } else {
            return var0.getClass().isArray() ? Array.getLength(var0) == 0 : false;
         }
      }

      @Override
      public String toString() {
         boolean var1 = this.omitNullValues;
         boolean var2 = this.omitEmptyValues;
         String var3 = "";
         StringBuilder var4 = new StringBuilder(32).append(this.className).append('{');

         for (MoreObjects.ToStringHelper.ValueHolder var5 = this.holderHead.next; var5 != null; var5 = var5.next) {
            Object var6 = var5.value;
            if (var5 instanceof MoreObjects.ToStringHelper.UnconditionalValueHolder || (var6 == null ? !var1 : !var2 || !isEmpty(var6))) {
               var4.append(var3);
               var3 = ", ";
               if (var5.name != null) {
                  var4.append(var5.name).append('=');
               }

               if (var6 != null && var6.getClass().isArray()) {
                  Object[] var7 = new Object[]{var6};
                  String var8 = Arrays.deepToString(var7);
                  var4.append(var8, 1, var8.length() - 1);
               } else {
                  var4.append(var6);
               }
            }
         }

         return var4.append('}').toString();
      }

      private MoreObjects.ToStringHelper.ValueHolder addHolder() {
         MoreObjects.ToStringHelper.ValueHolder var1 = new MoreObjects.ToStringHelper.ValueHolder();
         this.holderTail = this.holderTail.next = var1;
         return var1;
      }

      private MoreObjects.ToStringHelper addHolder(Object var1) {
         MoreObjects.ToStringHelper.ValueHolder var2 = this.addHolder();
         var2.value = var1;
         return this;
      }

      private MoreObjects.ToStringHelper addHolder(String var1, Object var2) {
         MoreObjects.ToStringHelper.ValueHolder var3 = this.addHolder();
         var3.value = var2;
         var3.name = Preconditions.checkNotNull(var1);
         return this;
      }

      private MoreObjects.ToStringHelper.UnconditionalValueHolder addUnconditionalHolder() {
         MoreObjects.ToStringHelper.UnconditionalValueHolder var1 = new MoreObjects.ToStringHelper.UnconditionalValueHolder();
         this.holderTail = this.holderTail.next = var1;
         return var1;
      }

      private MoreObjects.ToStringHelper addUnconditionalHolder(Object var1) {
         MoreObjects.ToStringHelper.UnconditionalValueHolder var2 = this.addUnconditionalHolder();
         var2.value = var1;
         return this;
      }

      private MoreObjects.ToStringHelper addUnconditionalHolder(String var1, Object var2) {
         MoreObjects.ToStringHelper.UnconditionalValueHolder var3 = this.addUnconditionalHolder();
         var3.value = var2;
         var3.name = Preconditions.checkNotNull(var1);
         return this;
      }

      private static final class UnconditionalValueHolder extends MoreObjects.ToStringHelper.ValueHolder {
         private UnconditionalValueHolder() {
         }
      }

      private static class ValueHolder {
         String name;
         Object value;
         MoreObjects.ToStringHelper.ValueHolder next;

         private ValueHolder() {
         }
      }
   }
}
