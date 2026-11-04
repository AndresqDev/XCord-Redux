package org.apache.commons.lang3.concurrent;

import java.util.Objects;

public class ConstantInitializer<T> implements ConcurrentInitializer<T> {
   private static final String FMT_TO_STRING = "ConstantInitializer@%d [ object = %s ]";
   private final T object;

   public ConstantInitializer(T var1) {
      this.object = (T)var1;
   }

   public final T getObject() {
      return this.object;
   }

   @Override
   public T get() throws ConcurrentException {
      return this.getObject();
   }

   @Override
   public int hashCode() {
      return this.getObject() != null ? this.getObject().hashCode() : 0;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConstantInitializer)) {
         return false;
      } else {
         ConstantInitializer var2 = (ConstantInitializer)var1;
         return Objects.equals(this.getObject(), var2.getObject());
      }
   }

   @Override
   public String toString() {
      return String.format("ConstantInitializer@%d [ object = %s ]", System.identityHashCode(this), String.valueOf(this.getObject()));
   }
}
