package org.jline.reader;

public class Macro implements Binding {
   private final String sequence;

   public Macro(String var1) {
      this.sequence = var1;
   }

   public String getSequence() {
      return this.sequence;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Macro var2 = (Macro)var1;
         return this.sequence.equals(var2.sequence);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.sequence.hashCode();
   }

   @Override
   public String toString() {
      return "Macro[" + this.sequence + ']';
   }
}
