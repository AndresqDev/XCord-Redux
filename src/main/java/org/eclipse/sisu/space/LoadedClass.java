package org.eclipse.sisu.space;

public final class LoadedClass<T> extends AbstractDeferredClass<T> {
   private final Class<T> clazz;

   public LoadedClass(Class<? extends T> var1) {
      this.clazz = var1;
   }

   @Override
   public Class<T> load() {
      return this.clazz;
   }

   @Override
   public String getName() {
      return this.clazz.getName();
   }

   @Override
   public int hashCode() {
      return this.clazz.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof LoadedClass ? this.clazz == ((LoadedClass)var1).clazz : false;
      }
   }

   @Override
   public String toString() {
      String var1 = "Loaded " + this.clazz;
      ClassLoader var2 = this.clazz.getClassLoader();
      return var2 != null ? var1 + " from " + var2 : var1;
   }
}
