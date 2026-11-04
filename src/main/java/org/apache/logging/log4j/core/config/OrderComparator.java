package org.apache.logging.log4j.core.config;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class OrderComparator implements Serializable, Comparator<Class<?>> {
   private static final long serialVersionUID = 1L;
   private static final Comparator<Class<?>> INSTANCE = new OrderComparator();

   public static Comparator<Class<?>> getInstance() {
      return INSTANCE;
   }

   public int compare(Class<?> var1, Class<?> var2) {
      Order var3 = Objects.requireNonNull(var1, "lhs").getAnnotation(Order.class);
      Order var4 = Objects.requireNonNull(var2, "rhs").getAnnotation(Order.class);
      if (var3 == null && var4 == null) {
         return 0;
      } else if (var4 == null) {
         return -1;
      } else {
         return var3 == null ? 1 : Integer.signum(var4.value() - var3.value());
      }
   }
}
