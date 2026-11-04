package gnu.trove.decorator;

import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
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

public class TLongFloatMapDecorator extends AbstractMap<Long, Float> implements Externalizable, Cloneable, Map<Long, Float> {
   static final long serialVersionUID = 1L;
   protected TLongFloatMap _map;

   public TLongFloatMapDecorator() {
   }

   public TLongFloatMapDecorator(TLongFloatMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TLongFloatMap getMap() {
      return this._map;
   }

   public Float put(Long var1, Float var2) {
      long var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      float var5;
      if (var2 == null) {
         var5 = this._map.getNoEntryValue();
      } else {
         var5 = this.unwrapValue(var2);
      }

      float var6 = this._map.put(var3, var5);
      return var6 == this._map.getNoEntryValue() ? null : this.wrapValue(var6);
   }

   public Float get(Object var1) {
      long var2;
      if (var1 != null) {
         if (!(var1 instanceof Long)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      float var4 = this._map.get(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Float remove(Object var1) {
      long var2;
      if (var1 != null) {
         if (!(var1 instanceof Long)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      float var4 = this._map.remove(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public Set<Entry<Long, Float>> entrySet() {
      return new AbstractSet<Entry<Long, Float>>() {
         @Override
         public int size() {
            return TLongFloatMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TLongFloatMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TLongFloatMapDecorator.this.containsKey(var2) && TLongFloatMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Long, Float>> iterator() {
            return new Iterator<Entry<Long, Float>>() {
               private final TLongFloatIterator it = TLongFloatMapDecorator.this._map.iterator();

               public Entry<Long, Float> next() {
                  this.it.advance();
                  long var1 = this.it.key();
                  final Long var3 = var1 == TLongFloatMapDecorator.this._map.getNoEntryKey() ? null : TLongFloatMapDecorator.this.wrapKey(var1);
                  float var4 = this.it.value();
                  final Float var5 = var4 == TLongFloatMapDecorator.this._map.getNoEntryValue() ? null : TLongFloatMapDecorator.this.wrapValue(var4);
                  return new Entry<Long, Float>() {
                     private Float val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var3) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Long getKey() {
                        return var3;
                     }

                     public Float getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var3.hashCode() + this.val.hashCode();
                     }

                     public Float setValue(Float var1) {
                        this.val = var1;
                        return TLongFloatMapDecorator.this.put(var3, var1);
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

         public boolean add(Entry<Long, Float> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Long var3 = (Long)((Entry)var1).getKey();
               TLongFloatMapDecorator.this._map.remove(TLongFloatMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Long, Float>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TLongFloatMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Float && this._map.containsValue(this.unwrapValue(var1));
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
   public void putAll(Map<? extends Long, ? extends Float> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Long)var4.getKey(), (Float)var4.getValue());
      }
   }

   protected Long wrapKey(long var1) {
      return var1;
   }

   protected long unwrapKey(Object var1) {
      return (Long)var1;
   }

   protected Float wrapValue(float var1) {
      return var1;
   }

   protected float unwrapValue(Object var1) {
      return (Float)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TLongFloatMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
