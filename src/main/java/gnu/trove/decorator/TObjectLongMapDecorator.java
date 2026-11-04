package gnu.trove.decorator;

import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
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

public class TObjectLongMapDecorator<K> extends AbstractMap<K, Long> implements Externalizable, Cloneable, Map<K, Long> {
   static final long serialVersionUID = 1L;
   protected TObjectLongMap<K> _map;

   public TObjectLongMapDecorator() {
   }

   public TObjectLongMapDecorator(TObjectLongMap<K> var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TObjectLongMap<K> getMap() {
      return this._map;
   }

   public Long put(K var1, Long var2) {
      return var2 == null
         ? this.wrapValue(this._map.put((K)var1, this._map.getNoEntryValue()))
         : this.wrapValue(this._map.put((K)var1, this.unwrapValue(var2)));
   }

   public Long get(Object var1) {
      long var2 = this._map.get(var1);
      return var2 == this._map.getNoEntryValue() ? null : this.wrapValue(var2);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Long remove(Object var1) {
      long var2 = this._map.remove(var1);
      return var2 == this._map.getNoEntryValue() ? null : this.wrapValue(var2);
   }

   @Override
   public Set<Entry<K, Long>> entrySet() {
      return new AbstractSet<Entry<K, Long>>() {
         @Override
         public int size() {
            return TObjectLongMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TObjectLongMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TObjectLongMapDecorator.this.containsKey(var2) && TObjectLongMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<K, Long>> iterator() {
            return new Iterator<Entry<K, Long>>() {
               private final TObjectLongIterator<K> it = TObjectLongMapDecorator.this._map.iterator();

               public Entry<K, Long> next() {
                  this.it.advance();
                  final Object var1 = this.it.key();
                  final Long var2 = TObjectLongMapDecorator.this.wrapValue(this.it.value());
                  return new Entry<K, Long>() {
                     private Long val = var2;

                     @Override
                     public boolean equals(Object var1x) {
                        return var1x instanceof Entry && ((Entry)var1x).getKey().equals(var1) && ((Entry)var1x).getValue().equals(this.val);
                     }

                     @Override
                     public K getKey() {
                        return (K)var1;
                     }

                     public Long getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var1.hashCode() + this.val.hashCode();
                     }

                     public Long setValue(Long var1x) {
                        this.val = var1x;
                        return TObjectLongMapDecorator.this.put(var1, var1x);
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

         public boolean add(Entry<K, Long> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Object var3 = ((Entry)var1).getKey();
               TObjectLongMapDecorator.this._map.remove(var3);
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<K, Long>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TObjectLongMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Long && this._map.containsValue(this.unwrapValue(var1));
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
   public void putAll(Map<? extends K, ? extends Long> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((K)var4.getKey(), (Long)var4.getValue());
      }
   }

   protected Long wrapValue(long var1) {
      return var1;
   }

   protected long unwrapValue(Object var1) {
      return (Long)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TObjectLongMap<K>)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
