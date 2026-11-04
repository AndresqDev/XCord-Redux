package io.netty.util;

public interface HashingStrategy<T> {
   HashingStrategy JAVA_HASHER = new HashingStrategy() {
      @Override
      public int hashCode(Object var1) {
         return var1 != null ? var1.hashCode() : 0;
      }

      @Override
      public boolean equals(Object var1, Object var2) {
         return var1 == var2 || var1 != null && var1.equals(var2);
      }
   };

   int hashCode(T var1);

   boolean equals(T var1, T var2);
}
