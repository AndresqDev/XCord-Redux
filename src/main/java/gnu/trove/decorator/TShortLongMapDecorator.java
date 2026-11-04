package gnu.trove.decorator;

import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
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

public class TShortLongMapDecorator extends AbstractMap<Short, Long> implements Externalizable, Cloneable, Map<Short, Long> {
   static final long serialVersionUID = 1L;
   protected TShortLongMap _map;

   public TShortLongMapDecorator() {
   }

   public TShortLongMapDecorator(TShortLongMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TShortLongMap getMap() {
      return this._map;
   }

   public Long put(Short var1, Long var2) {
      short var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      long var4;
      if (var2 == null) {
         var4 = this._map.getNoEntryValue();
      } else {
         var4 = this.unwrapValue(var2);
      }

      long var6 = this._map.put(var3, var4);
      return var6 == this._map.getNoEntryValue() ? null : this.wrapValue(var6);
   }

   public Long get(Object var1) {
      short var2;
      if (var1 != null) {
         if (!(var1 instanceof Short)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      long var3 = this._map.get(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Long remove(Object var1) {
      short var2;
      if (var1 != null) {
         if (!(var1 instanceof Short)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      long var3 = this._map.remove(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public Set<Entry<Short, Long>> entrySet() {
      return new AbstractSet<Entry<Short, Long>>() {
         @Override
         public int size() {
            return TShortLongMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TShortLongMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TShortLongMapDecorator.this.containsKey(var2) && TShortLongMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Short, Long>> iterator() {
            return new Iterator<Entry<Short, Long>>() {
               private final TShortLongIterator it = TShortLongMapDecorator.this._map.iterator();

               public Entry<Short, Long> next() {
                  this.it.advance();
                  short var1 = this.it.key();
                  final Short var2 = var1 == TShortLongMapDecorator.this._map.getNoEntryKey() ? null : TShortLongMapDecorator.this.wrapKey(var1);
                  long var3 = this.it.value();
                  final Long var5 = var3 == TShortLongMapDecorator.this._map.getNoEntryValue() ? null : TShortLongMapDecorator.this.wrapValue(var3);
                  return new Entry<Short, Long>() {
                     private Long val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Short getKey() {
                        return var2;
                     }

                     public Long getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     public Long setValue(Long var1) {
                        this.val = var1;
                        return TShortLongMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Short, Long> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Short var3 = (Short)((Entry)var1).getKey();
               TShortLongMapDecorator.this._map.remove(TShortLongMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Short, Long>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TShortLongMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Long && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Short && this._map.containsKey(this.unwrapKey(var1));
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
   public void putAll(Map<? extends Short, ? extends Long> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Short)var4.getKey(), (Long)var4.getValue());
      }
   }

   protected Short wrapKey(short var1) {
      return var1;
   }

   protected short unwrapKey(Object var1) {
      return (Short)var1;
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
      this._map = (TShortLongMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
