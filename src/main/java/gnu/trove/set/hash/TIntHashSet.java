package gnu.trove.set.hash;

import gnu.trove.TIntCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

public class TIntHashSet extends TIntHash implements TIntSet, Externalizable {
   static final long serialVersionUID = 1L;

   public TIntHashSet() {
   }

   public TIntHashSet(int var1) {
      super(var1);
   }

   public TIntHashSet(int var1, float var2) {
      super(var1, var2);
   }

   public TIntHashSet(int var1, float var2, int var3) {
      super(var1, var2, var3);
      if (var3 != 0) {
         Arrays.fill(this._set, var3);
      }
   }

   public TIntHashSet(Collection<? extends Integer> var1) {
      this(Math.max(var1.size(), 10));
      this.addAll(var1);
   }

   public TIntHashSet(TIntCollection var1) {
      this(Math.max(var1.size(), 10));
      if (var1 instanceof TIntHashSet) {
         TIntHashSet var2 = (TIntHashSet)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil(10.0 / (double)this._loadFactor)));
      }

      this.addAll(var1);
   }

   public TIntHashSet(int[] var1) {
      this(Math.max(var1.length, 10));
      this.addAll(var1);
   }

   @Override
   public TIntIterator iterator() {
      return new TIntHashSet.TIntHashIterator(this);
   }

   @Override
   public int[] toArray() {
      return this.toArray(new int[this._size]);
   }

   @Override
   public int[] toArray(int[] var1) {
      if (var1.length < this._size) {
         var1 = new int[this._size];
      }

      int[] var2 = this._set;
      byte[] var3 = this._states;
      int var4 = var3.length;
      int var5 = 0;

      while (var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      if (var1.length > this._size) {
         var1[this._size] = this.no_entry_value;
      }

      return var1;
   }

   @Override
   public boolean add(int var1) {
      int var2 = this.insertKey(var1);
      if (var2 < 0) {
         return false;
      } else {
         this.postInsertHook(this.consumeFreeSlot);
         return true;
      }
   }

   @Override
   public boolean remove(int var1) {
      int var2 = this.index(var1);
      if (var2 >= 0) {
         this.removeAt(var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      for (Object var3 : var1) {
         if (!(var3 instanceof Integer)) {
            return false;
         }

         int var4 = (Integer)var3;
         if (!this.contains(var4)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(TIntCollection var1) {
      TIntIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         int var3 = var2.next();
         if (!this.contains(var3)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(int[] var1) {
      int var2 = var1.length;

      while (var2-- > 0) {
         if (!this.contains(var1[var2])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean addAll(Collection<? extends Integer> var1) {
      boolean var2 = false;

      for (Integer var4 : var1) {
         int var5 = var4;
         if (this.add(var5)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean addAll(TIntCollection var1) {
      boolean var2 = false;
      TIntIterator var3 = var1.iterator();

      while (var3.hasNext()) {
         int var4 = var3.next();
         if (this.add(var4)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean addAll(int[] var1) {
      boolean var2 = false;
      int var3 = var1.length;

      while (var3-- > 0) {
         if (this.add(var1[var3])) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      boolean var2 = false;
      TIntIterator var3 = this.iterator();

      while (var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(TIntCollection var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         TIntIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   @Override
   public boolean retainAll(int[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      int[] var3 = this._set;
      byte[] var4 = this._states;
      this._autoCompactTemporaryDisable = true;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
            this.removeAt(var5);
            var2 = true;
         }
      }

      this._autoCompactTemporaryDisable = false;
      return var2;
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      boolean var2 = false;

      for (Object var4 : var1) {
         if (var4 instanceof Integer) {
            int var5 = (Integer)var4;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   @Override
   public boolean removeAll(TIntCollection var1) {
      boolean var2 = false;
      TIntIterator var3 = var1.iterator();

      while (var3.hasNext()) {
         int var4 = var3.next();
         if (this.remove(var4)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean removeAll(int[] var1) {
      boolean var2 = false;
      int var3 = var1.length;

      while (var3-- > 0) {
         if (this.remove(var1[var3])) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public void clear() {
      super.clear();
      int[] var1 = this._set;
      byte[] var2 = this._states;

      for (int var3 = var1.length; var3-- > 0; var2[var3] = 0) {
         var1[var3] = this.no_entry_value;
      }
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      int[] var3 = this._set;
      byte[] var4 = this._states;
      this._set = new int[var1];
      this._states = new byte[var1];
      int var5 = var2;

      while (var5-- > 0) {
         if (var4[var5] == 1) {
            int var6 = var3[var5];
            int var7 = this.insertKey(var6);
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof TIntSet)) {
         return false;
      } else {
         TIntSet var2 = (TIntSet)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            int var3 = this._states.length;

            while (var3-- > 0) {
               if (this._states[var3] == 1 && !var2.contains(this._set[var3])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      int var2 = this._states.length;

      while (var2-- > 0) {
         if (this._states[var2] == 1) {
            var1 += HashFunctions.hash(this._set[var2]);
         }
      }

      return var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(this._size * 2 + 2);
      var1.append("{");
      int var2 = this._states.length;
      int var3 = 1;

      while (var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.append(this._set[var2]);
            if (var3++ < this._size) {
               var1.append(",");
            }
         }
      }

      var1.append("}");
      return var1.toString();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(1);
      super.writeExternal(var1);
      var1.writeInt(this._size);
      var1.writeFloat(this._loadFactor);
      var1.writeInt(this.no_entry_value);
      int var2 = this._states.length;

      while (var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.writeInt(this._set[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      byte var2 = var1.readByte();
      super.readExternal(var1);
      int var3 = var1.readInt();
      if (var2 >= 1) {
         this._loadFactor = var1.readFloat();
         this.no_entry_value = var1.readInt();
         if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
         }
      }

      this.setUp(var3);

      while (var3-- > 0) {
         int var4 = var1.readInt();
         this.add(var4);
      }
   }

   class TIntHashIterator extends THashPrimitiveIterator implements TIntIterator {
      private final TIntHash _hash;

      public TIntHashIterator(TIntHash var2) {
         super(var2);
         this._hash = var2;
      }

      @Override
      public int next() {
         this.moveToNextIndex();
         return this._hash._set[this._index];
      }
   }
}
