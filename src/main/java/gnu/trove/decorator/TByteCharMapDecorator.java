package gnu.trove.decorator;

import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.map.TByteCharMap;
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

public class TByteCharMapDecorator extends AbstractMap<Byte, Character> implements Externalizable, Cloneable, Map<Byte, Character> {
   static final long serialVersionUID = 1L;
   protected TByteCharMap _map;

   public TByteCharMapDecorator() {
   }

   public TByteCharMapDecorator(TByteCharMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TByteCharMap getMap() {
      return this._map;
   }

   public Character put(Byte var1, Character var2) {
      byte var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      char var4;
      if (var2 == null) {
         var4 = this._map.getNoEntryValue();
      } else {
         var4 = this.unwrapValue(var2);
      }

      char var5 = this._map.put(var3, var4);
      return var5 == this._map.getNoEntryValue() ? null : this.wrapValue(var5);
   }

   public Character get(Object var1) {
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      char var3 = this._map.get(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Character remove(Object var1) {
      byte var2;
      if (var1 != null) {
         if (!(var1 instanceof Byte)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      char var3 = this._map.remove(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public Set<Entry<Byte, Character>> entrySet() {
      return new AbstractSet<Entry<Byte, Character>>() {
         @Override
         public int size() {
            return TByteCharMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TByteCharMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TByteCharMapDecorator.this.containsKey(var2) && TByteCharMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Byte, Character>> iterator() {
            return new Iterator<Entry<Byte, Character>>() {
               private final TByteCharIterator it = TByteCharMapDecorator.this._map.iterator();

               public Entry<Byte, Character> next() {
                  this.it.advance();
                  byte var1 = this.it.key();
                  final Byte var2 = var1 == TByteCharMapDecorator.this._map.getNoEntryKey() ? null : TByteCharMapDecorator.this.wrapKey(var1);
                  char var3 = this.it.value();
                  final Character var4 = var3 == TByteCharMapDecorator.this._map.getNoEntryValue() ? null : TByteCharMapDecorator.this.wrapValue(var3);
                  return new Entry<Byte, Character>() {
                     private Character val = var4;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Byte getKey() {
                        return var2;
                     }

                     public Character getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     public Character setValue(Character var1) {
                        this.val = var1;
                        return TByteCharMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Byte, Character> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Byte var3 = (Byte)((Entry)var1).getKey();
               TByteCharMapDecorator.this._map.remove(TByteCharMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Byte, Character>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TByteCharMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Character && this._map.containsValue(this.unwrapValue(var1));
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
   public void putAll(Map<? extends Byte, ? extends Character> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Byte)var4.getKey(), (Character)var4.getValue());
      }
   }

   protected Byte wrapKey(byte var1) {
      return var1;
   }

   protected byte unwrapKey(Object var1) {
      return (Byte)var1;
   }

   protected Character wrapValue(char var1) {
      return var1;
   }

   protected char unwrapValue(Object var1) {
      return (Character)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TByteCharMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
