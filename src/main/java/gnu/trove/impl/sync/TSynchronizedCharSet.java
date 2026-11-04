package gnu.trove.impl.sync;

import gnu.trove.set.TCharSet;

public class TSynchronizedCharSet extends TSynchronizedCharCollection implements TCharSet {
   private static final long serialVersionUID = 487447009682186044L;

   public TSynchronizedCharSet(TCharSet var1) {
      super(var1);
   }

   public TSynchronizedCharSet(TCharSet var1, Object var2) {
      super(var1, var2);
   }

   @Override
   public boolean equals(Object var1) {
      synchronized (this.mutex) {
         return this.c.equals(var1);
      }
   }

   @Override
   public int hashCode() {
      synchronized (this.mutex) {
         return this.c.hashCode();
      }
   }
}
