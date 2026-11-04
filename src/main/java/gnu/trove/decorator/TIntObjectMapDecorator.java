package gnu.trove.decorator;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
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

public class TIntObjectMapDecorator<V> extends AbstractMap<Integer, V> implements Externalizable, Cloneable, Map<Integer, V> {
   static final long serialVersionUID = 1L;
   protected TIntObjectMap<V> _map;

   public TIntObjectMapDecorator() {
   }

   public TIntObjectMapDecorator(TIntObjectMap<V> var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TIntObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Integer var1, V var2) {
      int var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      return this._map.put(var3, (V)var2);
   }

   @Override
   public V get(Object var1) {
      int var2;
      if (var1 != null) {
         if (!(var1 instanceof Integer)) {
            return null;
         }

         var2 = this.unwrapKey((Integer)var1);
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
      int var2;
      if (var1 != null) {
         if (!(var1 instanceof Integer)) {
            return null;
         }

         var2 = this.unwrapKey((Integer)var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      return this._map.remove(var2);
   }

   @Override
   public Set<Entry<Integer, V>> entrySet() {
      return new AbstractSet<Entry<Integer, V>>() {
         @Override
         public int size() {
            return TIntObjectMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TIntObjectMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TIntObjectMapDecorator.this.containsKey(var2) && TIntObjectMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Integer, V>> iterator() {
            return new Iterator<Entry<Integer, V>>() {
               private final TIntObjectIterator<V> it = TIntObjectMapDecorator.this._map.iterator();

               public Entry<Integer, V> next() {
                  this.it.advance();
                  int var1 = this.it.key();
                  final Integer var2 = var1 == TIntObjectMapDecorator.this._map.getNoEntryKey() ? null : TIntObjectMapDecorator.this.wrapKey(var1);
                  final Object var3 = this.it.value();
                  return new Entry<Integer, V>() {
                     private V val = (V)var3;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Integer getKey() {
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
                        return (V)TIntObjectMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Integer, V> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Integer var3 = (Integer)((Entry)var1).getKey();
               TIntObjectMapDecorator.this._map.remove(TIntObjectMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Integer, V>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TIntObjectMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return this._map.containsValue(var1);
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Integer && this._map.containsKey((Integer)var1);
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
   public void putAll(Map<? extends Integer, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Integer)var4.getKey(), (V)var4.getValue());
      }
   }

   protected Integer wrapKey(int var1) {
      return var1;
   }

   protected int unwrapKey(Integer var1) {
      return var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TIntObjectMap<V>)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
