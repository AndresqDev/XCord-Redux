package gnu.trove.decorator;

import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
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

public class TFloatByteMapDecorator extends AbstractMap<Float, Byte> implements Externalizable, Cloneable, Map<Float, Byte> {
   static final long serialVersionUID = 1L;
   protected TFloatByteMap _map;

   public TFloatByteMapDecorator() {
   }

   public TFloatByteMapDecorator(TFloatByteMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TFloatByteMap getMap() {
      return this._map;
   }

   public Byte put(Float var1, Byte var2) {
      float var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      byte var4;
      if (var2 == null) {
         var4 = this._map.getNoEntryValue();
      } else {
         var4 = this.unwrapValue(var2);
      }

      byte var5 = this._map.put(var3, var4);
      return var5 == this._map.getNoEntryValue() ? null : this.wrapValue(var5);
   }

   public Byte get(Object var1) {
      float var2;
      if (var1 != null) {
         if (!(var1 instanceof Float)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      byte var3 = this._map.get(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Byte remove(Object var1) {
      float var2;
      if (var1 != null) {
         if (!(var1 instanceof Float)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      byte var3 = this._map.remove(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public Set<Entry<Float, Byte>> entrySet() {
      return new AbstractSet<Entry<Float, Byte>>() {
         @Override
         public int size() {
            return TFloatByteMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TFloatByteMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TFloatByteMapDecorator.this.containsKey(var2) && TFloatByteMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Float, Byte>> iterator() {
            return new Iterator<Entry<Float, Byte>>() {
               private final TFloatByteIterator it = TFloatByteMapDecorator.this._map.iterator();

               public Entry<Float, Byte> next() {
                  this.it.advance();
                  float var1 = this.it.key();
                  final Float var2 = var1 == TFloatByteMapDecorator.this._map.getNoEntryKey() ? null : TFloatByteMapDecorator.this.wrapKey(var1);
                  byte var3 = this.it.value();
                  final Byte var4 = var3 == TFloatByteMapDecorator.this._map.getNoEntryValue() ? null : TFloatByteMapDecorator.this.wrapValue(var3);
                  return new Entry<Float, Byte>() {
                     private Byte val = var4;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Float getKey() {
                        return var2;
                     }

                     public Byte getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     public Byte setValue(Byte var1) {
                        this.val = var1;
                        return TFloatByteMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Float, Byte> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Float var3 = (Float)((Entry)var1).getKey();
               TFloatByteMapDecorator.this._map.remove(TFloatByteMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Float, Byte>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TFloatByteMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Byte && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Float && this._map.containsKey(this.unwrapKey(var1));
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
   public void putAll(Map<? extends Float, ? extends Byte> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Float)var4.getKey(), (Byte)var4.getValue());
      }
   }

   protected Float wrapKey(float var1) {
      return var1;
   }

   protected float unwrapKey(Object var1) {
      return (Float)var1;
   }

   protected Byte wrapValue(byte var1) {
      return var1;
   }

   protected byte unwrapValue(Object var1) {
      return (Byte)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TFloatByteMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
