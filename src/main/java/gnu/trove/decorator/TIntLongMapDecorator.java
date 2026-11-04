package gnu.trove.decorator;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
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

public class TIntLongMapDecorator extends AbstractMap<Integer, Long> implements Externalizable, Cloneable, Map<Integer, Long> {
   static final long serialVersionUID = 1L;
   protected TIntLongMap _map;

   public TIntLongMapDecorator() {
   }

   public TIntLongMapDecorator(TIntLongMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TIntLongMap getMap() {
      return this._map;
   }

   public Long put(Integer var1, Long var2) {
      int var3;
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
      int var2;
      if (var1 != null) {
         if (!(var1 instanceof Integer)) {
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
      int var2;
      if (var1 != null) {
         if (!(var1 instanceof Integer)) {
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
   public Set<Entry<Integer, Long>> entrySet() {
      return new AbstractSet<Entry<Integer, Long>>() {
         @Override
         public int size() {
            return TIntLongMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TIntLongMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TIntLongMapDecorator.this.containsKey(var2) && TIntLongMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Integer, Long>> iterator() {
            return new Iterator<Entry<Integer, Long>>() {
               private final TIntLongIterator it = TIntLongMapDecorator.this._map.iterator();

               public Entry<Integer, Long> next() {
                  this.it.advance();
                  int var1 = this.it.key();
                  final Integer var2 = var1 == TIntLongMapDecorator.this._map.getNoEntryKey() ? null : TIntLongMapDecorator.this.wrapKey(var1);
                  long var3 = this.it.value();
                  final Long var5 = var3 == TIntLongMapDecorator.this._map.getNoEntryValue() ? null : TIntLongMapDecorator.this.wrapValue(var3);
                  return new Entry<Integer, Long>() {
                     private Long val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Integer getKey() {
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
                        return TIntLongMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Integer, Long> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Integer var3 = (Integer)((Entry)var1).getKey();
               TIntLongMapDecorator.this._map.remove(TIntLongMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Integer, Long>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TIntLongMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Long && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Integer && this._map.containsKey(this.unwrapKey(var1));
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
   public void putAll(Map<? extends Integer, ? extends Long> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Integer)var4.getKey(), (Long)var4.getValue());
      }
   }

   protected Integer wrapKey(int var1) {
      return var1;
   }

   protected int unwrapKey(Object var1) {
      return (Integer)var1;
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
      this._map = (TIntLongMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
