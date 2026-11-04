package gnu.trove.decorator;

import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.map.TByteFloatMap;
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

public class TByteFloatMapDecorator extends AbstractMap<Byte, Float> implements Externalizable, Cloneable, Map<Byte, Float> {
   static final long serialVersionUID = 1L;
   protected TByteFloatMap _map;

   public TByteFloatMapDecorator() {
   }

   public TByteFloatMapDecorator(TByteFloatMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TByteFloatMap getMap() {
      return this._map;
   }

   public Float put(Byte var1, Float var2) {
      byte var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      float var4;
      if (var2 == null) {
         var4 = this._map.getNoEntryValue();
      } else {
         var4 = this.unwrapValue(var2);
      }

      float var5 = this._map.put(var3, var4);
      return var5 == this._map.getNoEntryValue() ? null : this.wrapValue(var5);
   }

   public Float get(Object var1) {
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      float var3 = this._map.get(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Float remove(Object var1) {
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      float var3 = this._map.remove(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public Set<Entry<Byte, Float>> entrySet() {
      return new AbstractSet<Entry<Byte, Float>>() {
         @Override
         public int size() {
            return TByteFloatMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TByteFloatMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TByteFloatMapDecorator.this.containsKey(var2) && TByteFloatMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Byte, Float>> iterator() {
            return new Iterator<Entry<Byte, Float>>() {
               private final TByteFloatIterator it = TByteFloatMapDecorator.this._map.iterator();

               public Entry<Byte, Float> next() {
                  this.it.advance();
                  byte var1 = this.it.key();
                  final Byte var2 = var1 == TByteFloatMapDecorator.this._map.getNoEntryKey() ? null : TByteFloatMapDecorator.this.wrapKey(var1);
                  float var3 = this.it.value();
                  final Float var4 = var3 == TByteFloatMapDecorator.this._map.getNoEntryValue() ? null : TByteFloatMapDecorator.this.wrapValue(var3);
                  return new Entry<Byte, Float>() {
                     private Float val = var4;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Byte getKey() {
                        return var2;
                     }

                     public Float getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     public Float setValue(Float var1) {
                        this.val = var1;
                        return TByteFloatMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Byte, Float> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Byte var3 = (Byte)((Entry)var1).getKey();
               TByteFloatMapDecorator.this._map.remove(TByteFloatMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Byte, Float>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TByteFloatMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Float && this._map.containsValue(this.unwrapValue(var1));
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
   public void putAll(Map<? extends Byte, ? extends Float> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Byte)var4.getKey(), (Float)var4.getValue());
      }
   }

   protected Byte wrapKey(byte var1) {
      return var1;
   }

   protected byte unwrapKey(Object var1) {
      return (Byte)var1;
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
      this._map = (TByteFloatMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
