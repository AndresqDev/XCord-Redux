package org.eclipse.sisu.wire;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeConverter;
import java.util.Map;
import org.eclipse.sisu.Parameters;
import org.eclipse.sisu.inject.BeanLocator;
import xcord.Inject;

final class PlaceholderBeanProvider<V> implements Provider<V> {
   private static final int EXPRESSION_RECURSION_LIMIT = 8;
   @Inject
   @Parameters
   private Map properties;
   @Inject
   private TypeConverterCache converterCache;
   private final Provider<BeanLocator> locator;
   private final Key<V> placeholderKey;

   PlaceholderBeanProvider(Provider<BeanLocator> var1, Key<V> var2) {
      this.locator = var1;
      this.placeholderKey = var2;
   }

   public V get() {
      String var1 = ((Named)this.placeholderKey.getAnnotation()).value();
      TypeLiteral var2 = this.placeholderKey.getTypeLiteral();
      Class var3 = var2.getRawType();
      Object var4 = this.interpolate(var1, var3);
      if (!(var4 instanceof String)) {
         return (V)var4;
      } else {
         Key var5 = Key.get(var2, Names.named((String)var4));
         if (String.class != var3) {
            Object var6 = this.lookup(var5);
            if (var6 != null) {
               return (V)var6;
            }
         }

         if (var1 == var4) {
            var4 = nullify(this.lookup(var5.ofType(String.class)));
         }

         if (var4 != null && String.class != var3) {
            TypeConverter var7 = this.converterCache.getTypeConverter(var2);
            return (V)(var7 != null ? var7.convert((String)var4, var2) : null);
         } else {
            return (V)var4;
         }
      }
   }

   private <T> T lookup(Key<T> var1) {
      return BeanProviders.firstOf(((BeanLocator)this.locator.get()).locate(var1));
   }

   private static String nullify(String var0) {
      return "null".equals(var0) ? null : var0;
   }

   private Object interpolate(String var1, Class<?> var2) {
      StringBuilder var3;
      if (var1.contains("${")) {
         var3 = new StringBuilder(var1);
      } else {
         if (!this.properties.containsKey(var1)) {
            return var1;
         }

         var3 = new StringBuilder("${").append(var1).append('}');
      }

      int var4 = 0;
      int var6 = 0;
      int var7 = 0;

      int var5;
      while ((var4 = var3.indexOf("${", var4)) >= 0 && (var5 = var3.indexOf("}", var4) + 1) > 0) {
         if (var5 > var6) {
            var7 = 0;
            var6 = var5;
         }

         String var8 = var3.substring(var4 + 2, var5 - 1);
         int var9 = var8.indexOf(":-");
         Object var10 = this.properties.get(var9 < 0 ? var8 : var8.substring(0, var9));
         if (var10 == null && var9 >= 0) {
            var10 = var8.substring(var9 + 2);
         }

         if (var7++ >= 8) {
            throw new ProvisionException("Recursive configuration: " + var1 + " stopped at: " + var3);
         }

         int var11 = var3.length();
         if (var4 == 0 && var11 == var5 && String.class != var2 && var2.isInstance(var10)) {
            return var10;
         }

         var3.replace(var4, var5, String.valueOf(var10));
         var6 += var3.length() - var11;
      }

      return nullify(var3.toString());
   }
}
