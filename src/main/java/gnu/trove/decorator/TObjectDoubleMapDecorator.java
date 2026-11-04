package gnu.trove.decorator;

import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
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

public class TObjectDoubleMapDecorator<K> extends AbstractMap<K, Double> implements Externalizable, Cloneable, Map<K, Double> {
   static final long serialVersionUID = 1L;
   protected TObjectDoubleMap<K> _map;

   public TObjectDoubleMapDecorator() {
   }

   public TObjectDoubleMapDecorator(TObjectDoubleMap<K> var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TObjectDoubleMap<K> getMap() {
      return this._map;
   }

   public Double put(K var1, Double var2) {
      return var2 == null
         ? this.wrapValue(this._map.put((K)var1, this._map.getNoEntryValue()))
         : this.wrapValue(this._map.put((K)var1, this.unwrapValue(var2)));
   }

   public Double get(Object var1) {
      double var2 = this._map.get(var1);
      return var2 == this._map.getNoEntryValue() ? null : this.wrapValue(var2);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Double remove(Object var1) {
      double var2 = this._map.remove(var1);
      return var2 == this._map.getNoEntryValue() ? null : this.wrapValue(var2);
   }

   @Override
   public Set<Entry<K, Double>> entrySet() {
      return new AbstractSet<Entry<K, Double>>() {
         @Override
         public int size() {
            return TObjectDoubleMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TObjectDoubleMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TObjectDoubleMapDecorator.this.containsKey(var2) && TObjectDoubleMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<K, Double>> iterator() {
            return new Iterator<Entry<K, Double>>() {
               private final TObjectDoubleIterator<K> it = TObjectDoubleMapDecorator.this._map.iterator();

               public Entry<K, Double> next() {
                  this.it.advance();
                  final Object var1 = this.it.key();
                  final Double var2 = TObjectDoubleMapDecorator.this.wrapValue(this.it.value());
                  return new Entry<K, Double>() {
                     private Double val = var2;

                     @Override
                     public boolean equals(Object var1x) {
                        return var1x instanceof Entry && ((Entry)var1x).getKey().equals(var1) && ((Entry)var1x).getValue().equals(this.val);
                     }

                     @Override
                     public K getKey() {
                        return (K)var1;
                     }

                     public Double getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var1.hashCode() + this.val.hashCode();
                     }

                     public Double setValue(Double var1x) {
                        this.val = var1x;
                        return TObjectDoubleMapDecorator.this.put(var1, var1x);
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

         public boolean add(Entry<K, Double> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Object var3 = ((Entry)var1).getKey();
               TObjectDoubleMapDecorator.this._map.remove(var3);
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<K, Double>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TObjectDoubleMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Double && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return this._map.containsKey(var1);
   }

   @Override
   public int size() {
      return this._map.size();
   }

   @Override
   public boolean isEmpty() {
      return this._map.size() == 0;
   }

   @Override
   public void putAll(Map<? extends K, ? extends Double> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((K)var4.getKey(), (Double)var4.getValue());
      }
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
      this._map = (TObjectDoubleMap<K>)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
