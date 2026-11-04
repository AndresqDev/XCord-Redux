package gnu.trove.decorator;

import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
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

public class TCharDoubleMapDecorator extends AbstractMap<Character, Double> implements Externalizable, Cloneable, Map<Character, Double> {
   static final long serialVersionUID = 1L;
   protected TCharDoubleMap _map;

   public TCharDoubleMapDecorator() {
   }

   public TCharDoubleMapDecorator(TCharDoubleMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TCharDoubleMap getMap() {
      return this._map;
   }

   public Double put(Character var1, Double var2) {
      char var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      double var4;
      if (var2 == null) {
         var4 = this._map.getNoEntryValue();
      } else {
         var4 = this.unwrapValue(var2);
      }

      double var6 = this._map.put(var3, var4);
      return var6 == this._map.getNoEntryValue() ? null : this.wrapValue(var6);
   }

   public Double get(Object var1) {
      char var2;
      if (var1 != null) {
         if (!(var1 instanceof Character)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      double var3 = this._map.get(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Double remove(Object var1) {
      char var2;
      if (var1 != null) {
         if (!(var1 instanceof Character)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      double var3 = this._map.remove(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public Set<Entry<Character, Double>> entrySet() {
      return new AbstractSet<Entry<Character, Double>>() {
         @Override
         public int size() {
            return TCharDoubleMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TCharDoubleMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TCharDoubleMapDecorator.this.containsKey(var2) && TCharDoubleMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Character, Double>> iterator() {
            return new Iterator<Entry<Character, Double>>() {
               private final TCharDoubleIterator it = TCharDoubleMapDecorator.this._map.iterator();

               public Entry<Character, Double> next() {
                  this.it.advance();
                  char var1 = this.it.key();
                  final Character var2 = var1 == TCharDoubleMapDecorator.this._map.getNoEntryKey() ? null : TCharDoubleMapDecorator.this.wrapKey(var1);
                  double var3 = this.it.value();
                  final Double var5 = var3 == TCharDoubleMapDecorator.this._map.getNoEntryValue() ? null : TCharDoubleMapDecorator.this.wrapValue(var3);
                  return new Entry<Character, Double>() {
                     private Double val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Character getKey() {
                        return var2;
                     }

                     public Double getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     public Double setValue(Double var1) {
                        this.val = var1;
                        return TCharDoubleMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Character, Double> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Character var3 = (Character)((Entry)var1).getKey();
               TCharDoubleMapDecorator.this._map.remove(TCharDoubleMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Character, Double>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TCharDoubleMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Double && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Character && this._map.containsKey(this.unwrapKey(var1));
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
   public void putAll(Map<? extends Character, ? extends Double> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Character)var4.getKey(), (Double)var4.getValue());
      }
   }

   protected Character wrapKey(char var1) {
      return var1;
   }

   protected char unwrapKey(Object var1) {
      return (Character)var1;
   }

   protected Double wrapValue(double var1) {
      return var1;
   }

   protected double unwrapValue(Object var1) {
      return (Double)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TCharDoubleMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
