package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class Count implements Serializable {
   private int value;

   Count(int var1) {
      this.value = var1;
   }

   public int get() {
      return this.value;
   }

   public void add(int var1) {
      this.value += var1;
   }

   public int addAndGet(int var1) {
      return this.value += var1;
   }

   public void set(int var1) {
      this.value = var1;
   }

   public int getAndSet(int var1) {
      int var2 = this.value;
      this.value = var1;
      return var2;
   }

   @Override
   public int hashCode() {
      return this.value;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Count && ((Count)var1).value == this.value;
   }

   @Override
   public String toString() {
      return Integer.toString(this.value);
   }
}
