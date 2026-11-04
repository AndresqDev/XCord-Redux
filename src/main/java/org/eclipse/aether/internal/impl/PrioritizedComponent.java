package org.eclipse.aether.internal.impl;

final class PrioritizedComponent<T> implements Comparable<PrioritizedComponent<?>> {
   private final T component;
   private final Class<?> type;
   private final float priority;
   private final int index;

   PrioritizedComponent(T var1, Class<?> var2, float var3, int var4) {
      this.component = (T)var1;
      this.type = var2;
      this.priority = var3;
      this.index = var4;
   }

   public T getComponent() {
      return this.component;
   }

   public Class<?> getType() {
      return this.type;
   }

   public float getPriority() {
      return this.priority;
   }

   public boolean isDisabled() {
      return Float.isNaN(this.priority);
   }

   public int compareTo(PrioritizedComponent<?> var1) {
      int var2 = (this.isDisabled() ? 1 : 0) - (var1.isDisabled() ? 1 : 0);
      if (var2 == 0) {
         var2 = Float.compare(var1.priority, this.priority);
         if (var2 == 0) {
            var2 = this.index - var1.index;
         }
      }

      return var2;
   }

   @Override
   public String toString() {
      return this.priority + " (#" + this.index + "): " + this.component;
   }
}
