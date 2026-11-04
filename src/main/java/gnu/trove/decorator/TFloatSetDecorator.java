package gnu.trove.decorator;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TFloatSetDecorator extends AbstractSet<Float> implements Externalizable, Set<Float> {
   static final long serialVersionUID = 1L;
   protected TFloatSet _set;

   public TFloatSetDecorator() {
   }

   public TFloatSetDecorator(TFloatSet var1) {
      Objects.requireNonNull(var1);
      this._set = var1;
   }

   public TFloatSet getSet() {
      return this._set;
   }

   public boolean add(Float var1) {
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
               if (!(var5 instanceof Float)) {
                  return false;
               }

               float var6 = (Float)var5;
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
      return var1 instanceof Float && this._set.remove((Float)var1);
   }

   @Override
   public Iterator<Float> iterator() {
      return new Iterator<Float>() {
         private final TFloatIterator it = TFloatSetDecorator.this._set.iterator();

         public Float next() {
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
      return !(var1 instanceof Float) ? false : this._set.contains((Float)var1);
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._set = (TFloatSet)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._set);
   }
}
