package gnu.trove.decorator;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TIntSetDecorator extends AbstractSet<Integer> implements Externalizable, Set<Integer> {
   static final long serialVersionUID = 1L;
   protected TIntSet _set;

   public TIntSetDecorator() {
   }

   public TIntSetDecorator(TIntSet var1) {
      Objects.requireNonNull(var1);
      this._set = var1;
   }

   public TIntSet getSet() {
      return this._set;
   }

   public boolean add(Integer var1) {
      return var1 != null && this._set.add(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this._set.equals(var1)) {
         return true;
      } else if (var1 instanceof Set) {
         Set var2 = (Set)var1;
         if (var2.size() != this._set.size()) {
            return false;
         } else {
            Iterator var3 = var2.iterator();
            int var4 = var2.size();

            while (var4-- > 0) {
               Object var5 = var3.next();
               if (!(var5 instanceof Integer)) {
                  return false;
               }

               int var6 = (Integer)var5;
               if (!this._set.contains(var6)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void clear() {
      this._set.clear();
   }

   @Override
   public boolean remove(Object var1) {
      return var1 instanceof Integer && this._set.remove((Integer)var1);
   }

   @Override
   public Iterator<Integer> iterator() {
      return new Iterator<Integer>() {
         private final TIntIterator it = TIntSetDecorator.this._set.iterator();

         public Integer next() {
            return this.it.next();
         }

         @Override
         public boolean hasNext() {
            return this.it.hasNext();
         }

         @Override
         public void remove() {
            this.it.remove();
         }
      };
   }

   @Override
   public int size() {
      return this._set.size();
   }

   @Override
   public boolean isEmpty() {
      return this._set.size() == 0;
   }

   @Override
   public boolean contains(Object var1) {
      return !(var1 instanceof Integer) ? false : this._set.contains((Integer)var1);
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._set = (TIntSet)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._set);
   }
}
