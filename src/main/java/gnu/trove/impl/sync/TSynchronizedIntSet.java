package gnu.trove.impl.sync;

import gnu.trove.set.TIntSet;

public class TSynchronizedIntSet extends TSynchronizedIntCollection implements TIntSet {
   private static final long serialVersionUID = 487447009682186044L;

   public TSynchronizedIntSet(TIntSet var1) {
      super(var1);
   }

   public TSynchronizedIntSet(TIntSet var1, Object var2) {
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
