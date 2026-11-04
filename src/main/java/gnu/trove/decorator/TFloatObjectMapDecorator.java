package gnu.trove.decorator;

import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
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

public class TFloatObjectMapDecorator<V> extends AbstractMap<Float, V> implements Externalizable, Cloneable, Map<Float, V> {
   static final long serialVersionUID = 1L;
   protected TFloatObjectMap<V> _map;

   public TFloatObjectMapDecorator() {
   }

   public TFloatObjectMapDecorator(TFloatObjectMap<V> var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TFloatObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Float var1, V var2) {
      float var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      return this._map.put(var3, (V)var2);
   }

   @Override
   public V get(Object var1) {
      float var2;
      if (var1 != null) {
         if (!(var1 instanceof Float)) {
            return null;
         }

         var2 = this.unwrapKey((Float)var1);
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
      float var2;
      if (var1 != null) {
         if (!(var1 instanceof Float)) {
            return null;
         }

         var2 = this.unwrapKey((Float)var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      return this._map.remove(var2);
   }

   @Override
   public Set<Entry<Float, V>> entrySet() {
      return new AbstractSet<Entry<Float, V>>() {
         @Override
         public int size() {
            return TFloatObjectMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TFloatObjectMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TFloatObjectMapDecorator.this.containsKey(var2) && TFloatObjectMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Float, V>> iterator() {
            return new Iterator<Entry<Float, V>>() {
               private final TFloatObjectIterator<V> it = TFloatObjectMapDecorator.this._map.iterator();

               public Entry<Float, V> next() {
                  this.it.advance();
                  float var1 = this.it.key();
                  final Float var2 = var1 == TFloatObjectMapDecorator.this._map.getNoEntryKey() ? null : TFloatObjectMapDecorator.this.wrapKey(var1);
                  final Object var3 = this.it.value();
                  return new Entry<Float, V>() {
                     private V val = (V)var3;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Float getKey() {
                        return var2;
                     }

                     @Override
                     public V getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     @Override
                     public V setValue(V var1) {
                        this.val = (V)var1;
                        return (V)TFloatObjectMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Float, V> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Float var3 = (Float)((Entry)var1).getKey();
               TFloatObjectMapDecorator.this._map.remove(TFloatObjectMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Float, V>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TFloatObjectMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return this._map.containsValue(var1);
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Float && this._map.containsKey((Float)var1);
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
   public void putAll(Map<? extends Float, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Float)var4.getKey(), (V)var4.getValue());
      }
   }

   protected Float wrapKey(float var1) {
      return var1;
   }

   protected float unwrapKey(Float var1) {
      return var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TFloatObjectMap<V>)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
