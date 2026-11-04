package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

public class TDoubleCharMapDecorator extends AbstractMap<Double, Character> implements Externalizable, Cloneable, Map<Double, Character> {
   static final long serialVersionUID = 1L;
   protected TDoubleCharMap _map;

   public TDoubleCharMapDecorator() {
   }

   public TDoubleCharMapDecorator(TDoubleCharMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TDoubleCharMap getMap() {
      return this._map;
   }

   public Character put(Double var1, Character var2) {
      double var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      char var5;
      if (var2 == null) {
         var5 = this._map.getNoEntryValue();
      } else {
         var5 = this.unwrapValue(var2);
      }

      char var6 = this._map.put(var3, var5);
      return var6 == this._map.getNoEntryValue() ? null : this.wrapValue(var6);
   }

   public Character get(Object var1) {
      double var2;
      if (var1 != null) {
         if (!(var1 instanceof Double)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      char var4 = this._map.get(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Character remove(Object var1) {
      double var2;
      if (var1 != null) {
         if (!(var1 instanceof Double)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      char var4 = this._map.remove(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public Set<Entry<Double, Character>> entrySet() {
      return new AbstractSet<Entry<Double, Character>>() {
         @Override
         public int size() {
            return TDoubleCharMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TDoubleCharMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TDoubleCharMapDecorator.this.containsKey(var2) && TDoubleCharMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Double, Character>> iterator() {
            return new Iterator<Entry<Double, Character>>() {
               private final TDoubleCharIterator it = TDoubleCharMapDecorator.this._map.iterator();

               public Entry<Double, Character> next() {
                  this.it.advance();
                  double var1 = this.it.key();
                  final Double var3 = var1 == TDoubleCharMapDecorator.this._map.getNoEntryKey() ? null : TDoubleCharMapDecorator.this.wrapKey(var1);
                  char var4 = this.it.value();
                  final Character var5 = var4 == TDoubleCharMapDecorator.this._map.getNoEntryValue() ? null : TDoubleCharMapDecorator.this.wrapValue(var4);
                  return new Entry<Double, Character>() {
                     private Character val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var3) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Double getKey() {
                        return var3;
                     }

                     public Character getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var3.hashCode() + this.val.hashCode();
                     }

                     public Character setValue(Character var1) {
                        this.val = var1;
                        return TDoubleCharMapDecorator.this.put(var3, var1);
                     }
                  };
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

         public boolean add(Entry<Double, Character> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Double var3 = (Double)((Entry)var1).getKey();
               TDoubleCharMapDecorator.this._map.remove(TDoubleCharMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Double, Character>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TDoubleCharMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Character && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Double && this._map.containsKey(this.unwrapKey(var1));
   }

   @Override
   public int size() {
      return this._map.size();
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Character> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Double)var4.getKey(), (Character)var4.getValue());
      }
   }

   protected Double wrapKey(double var1) {
      return var1;
   }

   protected double unwrapKey(Object var1) {
      return (Double)var1;
   }

   protected Character wrapValue(char var1) {
      return var1;
   }

   protected char unwrapValue(Object var1) {
      return (Character)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TDoubleCharMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
