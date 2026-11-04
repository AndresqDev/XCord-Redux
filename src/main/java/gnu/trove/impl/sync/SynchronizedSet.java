package gnu.trove.impl.sync;

import java.util.Set;

class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
   private static final long serialVersionUID = 487447009682186044L;

   SynchronizedSet(Set<E> var1, Object var2) {
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
