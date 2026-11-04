package org.jline.reader;

public class Reference implements Binding {
   private final String name;

   public Reference(String var1) {
      this.name = var1;
   }

   public String name() {
      return this.name;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Reference var2 = (Reference)var1;
         return this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public String toString() {
      return "Reference[" + this.name + ']';
   }
}
