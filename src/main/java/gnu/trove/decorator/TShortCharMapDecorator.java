package gnu.trove.decorator;

import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
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

public class TShortCharMapDecorator extends AbstractMap<Short, Character> implements Externalizable, Cloneable, Map<Short, Character> {
   static final long serialVersionUID = 1L;
   protected TShortCharMap _map;

   public TShortCharMapDecorator() {
   }

   public TShortCharMapDecorator(TShortCharMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TShortCharMap getMap() {
      return this._map;
   }

   public Character put(Short var1, Character var2) {
      short var3;
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
      short var2;
      if (var1 != null) {
         if (!(var1 instanceof Short)) {
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
      short var2;
      if (var1 != null) {
         if (!(var1 instanceof Short)) {
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
   public Set<Entry<Short, Character>> entrySet() {
      return new AbstractSet<Entry<Short, Character>>() {
         @Override
         public int size() {
            return TShortCharMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TShortCharMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TShortCharMapDecorator.this.containsKey(var2) && TShortCharMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Short, Character>> iterator() {
            return new Iterator<Entry<Short, Character>>() {
               private final TShortCharIterator it = TShortCharMapDecorator.this._map.iterator();

               public Entry<Short, Character> next() {
                  this.it.advance();
                  short var1 = this.it.key();
                  final Short var2 = var1 == TShortCharMapDecorator.this._map.getNoEntryKey() ? null : TShortCharMapDecorator.this.wrapKey(var1);
                  char var3 = this.it.value();
                  final Character var4 = var3 == TShortCharMapDecorator.this._map.getNoEntryValue() ? null : TShortCharMapDecorator.this.wrapValue(var3);
                  return new Entry<Short, Character>() {
                     private Character val = var4;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Short getKey() {
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
                        return TShortCharMapDecorator.this.put(var2, var1);
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

         public boolean add(Entry<Short, Character> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Short var3 = (Short)((Entry)var1).getKey();
               TShortCharMapDecorator.this._map.remove(TShortCharMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Short, Character>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TShortCharMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Character && this._map.containsValue(this.unwrapValue(var1));
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
   public void putAll(Map<? extends Short, ? extends Character> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Short)var4.getKey(), (Character)var4.getValue());
      }
   }

   protected Short wrapKey(short var1) {
      return var1;
   }

   protected short unwrapKey(Object var1) {
      return (Short)var1;
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
      this._map = (TShortCharMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
