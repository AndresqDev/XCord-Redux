package org.eclipse.sisu.space;

final class NamedClass<T> extends AbstractDeferredClass<T> {
   private final ClassSpace space;
   private final String name;

   NamedClass(ClassSpace var1, String var2) {
      this.space = var1;
      this.name = var2;
   }

   @Override
   public Class<T> load() {
      return (Class<T>)this.space.loadClass(this.name);
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return (527 + this.name.hashCode()) * 31 + this.space.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof NamedClass) {
         NamedClass var2 = (NamedClass)var1;
         return this.name.equals(var2.name) && this.space.equals(var2.space);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "Deferred " + this.name + " from " + this.space;
   }
}
