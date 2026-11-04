package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
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

public class TDoubleObjectMapDecorator<V> extends AbstractMap<Double, V> implements Externalizable, Cloneable, Map<Double, V> {
   static final long serialVersionUID = 1L;
   protected TDoubleObjectMap<V> _map;

   public TDoubleObjectMapDecorator() {
   }

   public TDoubleObjectMapDecorator(TDoubleObjectMap<V> var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TDoubleObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Double var1, V var2) {
      double var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      return this._map.put(var3, (V)var2);
   }

   @Override
   public V get(Object var1) {
      double var2;
      if (var1 != null) {
         if (!(var1 instanceof Double)) {
            return null;
         }

         var2 = this.unwrapKey((Double)var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      return this._map.get(var2);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   @Override
   public V remove(Object var1) {
      double var2;
      if (var1 != null) {
         if (!(var1 instanceof Double)) {
            return null;
         }

         var2 = this.unwrapKey((Double)var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      return this._map.remove(var2);
   }

   @Override
   public Set<Entry<Double, V>> entrySet() {
      return new AbstractSet<Entry<Double, V>>() {
         @Override
         public int size() {
            return TDoubleObjectMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TDoubleObjectMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TDoubleObjectMapDecorator.this.containsKey(var2) && TDoubleObjectMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Double, V>> iterator() {
            return new Iterator<Entry<Double, V>>() {
               private final TDoubleObjectIterator<V> it = TDoubleObjectMapDecorator.this._map.iterator();

               public Entry<Double, V> next() {
                  this.it.advance();
                  double var1 = this.it.key();
                  final Double var3 = var1 == TDoubleObjectMapDecorator.this._map.getNoEntryKey() ? null : TDoubleObjectMapDecorator.this.wrapKey(var1);
                  final Object var4 = this.it.value();
                  return new Entry<Double, V>() {
                     private V val = (V)var4;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var3) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Double getKey() {
                        return var3;
                     }

                     @Override
                     public V getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var3.hashCode() + this.val.hashCode();
                     }

                     @Override
                     public V setValue(V var1) {
                        this.val = (V)var1;
                        return (V)TDoubleObjectMapDecorator.this.put(var3, var1);
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

         public boolean add(Entry<Double, V> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Double var3 = (Double)((Entry)var1).getKey();
               TDoubleObjectMapDecorator.this._map.remove(TDoubleObjectMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Double, V>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TDoubleObjectMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return this._map.containsValue(var1);
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Double && this._map.containsKey((Double)var1);
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
   public void putAll(Map<? extends Double, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Double)var4.getKey(), (V)var4.getValue());
      }
   }

   protected Double wrapKey(double var1) {
      return var1;
   }

   protected double unwrapKey(Double var1) {
      return var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TDoubleObjectMap<V>)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
