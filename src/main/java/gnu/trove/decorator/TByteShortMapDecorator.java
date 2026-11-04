package gnu.trove.decorator;

import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
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

public class TByteShortMapDecorator extends AbstractMap<Byte, Short> implements Externalizable, Cloneable, Map<Byte, Short> {
   static final long serialVersionUID = 1L;
   protected TByteShortMap _map;

   public TByteShortMapDecorator() {
   }

   public TByteShortMapDecorator(TByteShortMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TByteShortMap getMap() {
      return this._map;
   }

   public Short put(Byte var1, Short var2) {
      byte var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      short var4;
      if (var2 == null) {
         var4 = this._map.getNoEntryValue();
      } else {
         var4 = this.unwrapValue(var2);
      }

      short var5 = this._map.put(var3, var4);
      return var5 == this._map.getNoEntryValue() ? null : this.wrapValue(var5);
   }

   public Short get(Object var1) {
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      short var3 = this._map.get(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Short remove(Object var1) {
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      short var3 = this._map.remove(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public Set<Entry<Byte, Short>> entrySet() {
      return new AbstractSet<Entry<Byte, Short>>() {
         @Override
         public int size() {
            return TByteShortMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TByteShortMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TByteShortMapDecorator.this.containsKey(var2) && TByteShortMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Byte, Short>> iterator() {
            return new Iterator<Entry<Byte, Short>>() {
               private final TByteShortIterator it = TByteShortMapDecorator.this._map.iterator();

               public Entry<Byte, Short> next() {
                  this.it.advance();
                  byte var1 = this.it.key();
                  final Byte var2 = var1 == TByteShortMapDecorator.this._map.getNoEntryKey() ? null : TByteShortMapDecorator.this.wrapKey(var1);
                  short var3 = this.it.value();
                  final Short var4 = var3 == TByteShortMapDecorator.this._map.getNoEntryValue() ? null : TByteShortMapDecorator.this.wrapValue(var3);
                  return new Entry<Byte, Short>() {
                     private Short val = var4;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Byte getKey() {
                        return var2;
                     }

                     public Short getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     public Short setValue(Short var1) {
                        this.val = var1;
                        return TByteShortMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Byte, Short> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Byte var3 = (Byte)((Entry)var1).getKey();
               TByteShortMapDecorator.this._map.remove(TByteShortMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Byte, Short>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TByteShortMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Short && this._map.containsValue(this.unwrapValue(var1));
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
   public void putAll(Map<? extends Byte, ? extends Short> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Byte)var4.getKey(), (Short)var4.getValue());
      }
   }

   protected Byte wrapKey(byte var1) {
      return var1;
   }

   protected byte unwrapKey(Object var1) {
      return (Byte)var1;
   }

   protected Short wrapValue(short var1) {
      return var1;
   }

   protected short unwrapValue(Object var1) {
      return (Short)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TByteShortMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
