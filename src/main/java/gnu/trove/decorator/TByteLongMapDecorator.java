package gnu.trove.decorator;

import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
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

public class TByteLongMapDecorator extends AbstractMap<Byte, Long> implements Externalizable, Cloneable, Map<Byte, Long> {
   static final long serialVersionUID = 1L;
   protected TByteLongMap _map;

   public TByteLongMapDecorator() {
   }

   public TByteLongMapDecorator(TByteLongMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TByteLongMap getMap() {
      return this._map;
   }

   public Long put(Byte var1, Long var2) {
      byte var3;
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
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
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
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
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
   public Set<Entry<Byte, Long>> entrySet() {
      return new AbstractSet<Entry<Byte, Long>>() {
         @Override
         public int size() {
            return TByteLongMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TByteLongMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TByteLongMapDecorator.this.containsKey(var2) && TByteLongMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Byte, Long>> iterator() {
            return new Iterator<Entry<Byte, Long>>() {
               private final TByteLongIterator it = TByteLongMapDecorator.this._map.iterator();

               public Entry<Byte, Long> next() {
                  this.it.advance();
                  byte var1 = this.it.key();
                  final Byte var2 = var1 == TByteLongMapDecorator.this._map.getNoEntryKey() ? null : TByteLongMapDecorator.this.wrapKey(var1);
                  long var3 = this.it.value();
                  final Long var5 = var3 == TByteLongMapDecorator.this._map.getNoEntryValue() ? null : TByteLongMapDecorator.this.wrapValue(var3);
                  return new Entry<Byte, Long>() {
                     private Long val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Byte getKey() {
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
                        return TByteLongMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Byte, Long> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Byte var3 = (Byte)((Entry)var1).getKey();
               TByteLongMapDecorator.this._map.remove(TByteLongMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Byte, Long>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TByteLongMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Long && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Byte && this._map.containsKey(this.unwrapKey(var1));
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
   public void putAll(Map<? extends Byte, ? extends Long> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Byte)var4.getKey(), (Long)var4.getValue());
      }
   }

   protected Byte wrapKey(byte var1) {
      return var1;
   }

   protected byte unwrapKey(Object var1) {
      return (Byte)var1;
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
      this._map = (TByteLongMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
