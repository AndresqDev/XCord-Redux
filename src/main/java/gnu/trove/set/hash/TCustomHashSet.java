package gnu.trove.set.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.array.ToObjectArrayProceedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TCustomHashSet<E> extends TCustomObjectHash<E> implements Externalizable, Iterable<E>, Set<E> {
   static final long serialVersionUID = 1L;

   public TCustomHashSet() {
   }

   public TCustomHashSet(HashingStrategy<? super E> var1) {
      super(var1);
   }

   public TCustomHashSet(HashingStrategy<? super E> var1, int var2) {
      super(var1, var2);
   }

   public TCustomHashSet(HashingStrategy<? super E> var1, int var2, float var3) {
      super(var1, var2, var3);
   }

   public TCustomHashSet(HashingStrategy<? super E> var1, Collection<? extends E> var2) {
      this(var1, var2.size());
      this.addAll(var2);
   }

   @Override
   public boolean add(E var1) {
      int var2 = this.insertKey((E)var1);
      if (var2 < 0) {
         return false;
      } else {
         this.postInsertHook(this.consumeFreeSlot);
         return true;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Set)) {
         return false;
      } else {
         Set var2 = (Set)var1;
         return var2.size() != this.size() ? false : this.containsAll(var2);
      }
   }

   @Override
   public int hashCode() {
      TCustomHashSet.HashProcedure var1 = new TCustomHashSet.HashProcedure();
      this.forEach(var1);
      return var1.getHashCode();
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      int var3 = this.size();
      Object[] var4 = this._set;
      this._set = new Object[var1];
      Arrays.fill(this._set, FREE);
      int var5 = var2;

      while (var5-- > 0) {
         Object var6 = var4[var5];
         if (var6 != FREE && var6 != REMOVED) {
            int var7 = this.insertKey((E)var6);
            if (var7 < 0) {
               this.throwObjectContractViolation(this._set[-var7 - 1], var6, this.size(), var3, var4);
            }
         }
      }
   }

   @Override
   public Object[] toArray() {
      Object[] var1 = new Object[this.size()];
      this.forEach(new ToObjectArrayProceedure<>((E[])var1));
      return var1;
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), var2);
      }

      this.forEach(new ToObjectArrayProceedure<>(var1));
      if (var1.length > var2) {
         var1[var2] = null;
      }

      return (T[])var1;
   }

   @Override
   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, FREE);
   }

   @Override
   public boolean remove(Object var1) {
      int var2 = this.index(var1);
      if (var2 >= 0) {
         this.removeAt(var2);
         return true;
      } else {
         return false;
      }
   }

   public TObjectHashIterator<E> iterator() {
      return new TObjectHashIterator<>(this);
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      Iterator var2 = var1.iterator();

      while (var2.hasNext()) {
         if (!this.contains(var2.next())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      boolean var2 = false;
      int var3 = var1.size();
      this.ensureCapacity(var3);
      Iterator var4 = var1.iterator();

      while (var3-- > 0) {
         if (this.add((E)var4.next())) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      boolean var2 = false;
      int var3 = var1.size();
      Iterator var4 = var1.iterator();

      while (var3-- > 0) {
         if (this.remove(var4.next())) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      boolean var2 = false;
      int var3 = this.size();
      TObjectHashIterator var4 = this.iterator();

      while (var3-- > 0) {
         if (!var1.contains(var4.next())) {
            var4.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");
      this.forEach(new TObjectProcedure<E>() {
         private boolean first = true;

         @Override
         public boolean execute(Object var1x) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
            }

            var1.append(var1x);
            return true;
         }
      });
      var1.append("}");
      return var1.toString();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(1);
      super.writeExternal(var1);
      var1.writeInt(this._size);
      int var2 = this._set.length;

      while (var2-- > 0) {
         if (this._set[var2] != REMOVED && this._set[var2] != FREE) {
            var1.writeObject(this._set[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      byte var2 = var1.readByte();
      if (var2 != 0) {
         super.readExternal(var1);
      }

      int var3 = var1.readInt();
      this.setUp(var3);

      while (var3-- > 0) {
         Object var4 = var1.readObject();
         this.add((E)var4);
      }
   }

   private final class HashProcedure implements TObjectProcedure<E> {
      private int h = 0;

      private HashProcedure() {
      }

      public int getHashCode() {
         return this.h;
      }

      @Override
      public final boolean execute(E var1) {
         this.h = this.h + HashFunctions.hash(var1);
         return true;
      }
   }
}
