package gnu.trove.decorator;

import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
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

public class TLongLongMapDecorator extends AbstractMap<Long, Long> implements Externalizable, Cloneable, Map<Long, Long> {
   static final long serialVersionUID = 1L;
   protected TLongLongMap _map;

   public TLongLongMapDecorator() {
   }

   public TLongLongMapDecorator(TLongLongMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TLongLongMap getMap() {
      return this._map;
   }

   public Long put(Long var1, Long var2) {
      long var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      long var5;
      if (var2 == null) {
         var5 = this._map.getNoEntryValue();
      } else {
         var5 = this.unwrapValue(var2);
      }

      long var7 = this._map.put(var3, var5);
      return var7 == this._map.getNoEntryValue() ? null : this.wrapValue(var7);
   }

   public Long get(Object var1) {
      long var2;
      if (var1 != null) {
         if (!(var1 instanceof Long)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      long var4 = this._map.get(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Long remove(Object var1) {
      long var2;
      if (var1 != null) {
         if (!(var1 instanceof Long)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      long var4 = this._map.remove(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public Set<Entry<Long, Long>> entrySet() {
      return new AbstractSet<Entry<Long, Long>>() {
         @Override
         public int size() {
            return TLongLongMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TLongLongMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TLongLongMapDecorator.this.containsKey(var2) && TLongLongMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Long, Long>> iterator() {
            return new Iterator<Entry<Long, Long>>() {
               private final TLongLongIterator it = TLongLongMapDecorator.this._map.iterator();

               public Entry<Long, Long> next() {
                  this.it.advance();
                  long var1 = this.it.key();
                  final Long var3 = var1 == TLongLongMapDecorator.this._map.getNoEntryKey() ? null : TLongLongMapDecorator.this.wrapKey(var1);
                  long var4 = this.it.value();
                  final Long var6 = var4 == TLongLongMapDecorator.this._map.getNoEntryValue() ? null : TLongLongMapDecorator.this.wrapValue(var4);
                  return new Entry<Long, Long>() {
                     private Long val = var6;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var3) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Long getKey() {
                        return var3;
                     }

                     public Long getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var3.hashCode() + this.val.hashCode();
                     }

                     public Long setValue(Long var1) {
                        this.val = var1;
                        return TLongLongMapDecorator.this.put(var3, var1);
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

         public boolean add(Entry<Long, Long> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Long var3 = (Long)((Entry)var1).getKey();
               TLongLongMapDecorator.this._map.remove(TLongLongMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Long, Long>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TLongLongMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Long && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Long && this._map.containsKey(this.unwrapKey(var1));
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
   public void putAll(Map<? extends Long, ? extends Long> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Long)var4.getKey(), (Long)var4.getValue());
      }
   }

   protected Long wrapKey(long var1) {
      return var1;
   }

   protected long unwrapKey(Object var1) {
      return (Long)var1;
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
      this._map = (TLongLongMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
