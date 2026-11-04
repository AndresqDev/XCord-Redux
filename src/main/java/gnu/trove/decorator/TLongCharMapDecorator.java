package gnu.trove.decorator;

import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
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

public class TLongCharMapDecorator extends AbstractMap<Long, Character> implements Externalizable, Cloneable, Map<Long, Character> {
   static final long serialVersionUID = 1L;
   protected TLongCharMap _map;

   public TLongCharMapDecorator() {
   }

   public TLongCharMapDecorator(TLongCharMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TLongCharMap getMap() {
      return this._map;
   }

   public Character put(Long var1, Character var2) {
      long var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      char var5;
      if (var2 == null) {
         var5 = this._map.getNoEntryValue();
      } else {
         var5 = this.unwrapValue(var2);
      }

      char var6 = this._map.put(var3, var5);
      return var6 == this._map.getNoEntryValue() ? null : this.wrapValue(var6);
   }

   public Character get(Object var1) {
      long var2;
      if (var1 != null) {
         if (!(var1 instanceof Long)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      char var4 = this._map.get(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Character remove(Object var1) {
      long var2;
      if (var1 != null) {
         if (!(var1 instanceof Long)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      char var4 = this._map.remove(var2);
      return var4 == this._map.getNoEntryValue() ? null : this.wrapValue(var4);
   }

   @Override
   public Set<Entry<Long, Character>> entrySet() {
      return new AbstractSet<Entry<Long, Character>>() {
         @Override
         public int size() {
            return TLongCharMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TLongCharMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TLongCharMapDecorator.this.containsKey(var2) && TLongCharMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Long, Character>> iterator() {
            return new Iterator<Entry<Long, Character>>() {
               private final TLongCharIterator it = TLongCharMapDecorator.this._map.iterator();

               public Entry<Long, Character> next() {
                  this.it.advance();
                  long var1 = this.it.key();
                  final Long var3 = var1 == TLongCharMapDecorator.this._map.getNoEntryKey() ? null : TLongCharMapDecorator.this.wrapKey(var1);
                  char var4 = this.it.value();
                  final Character var5 = var4 == TLongCharMapDecorator.this._map.getNoEntryValue() ? null : TLongCharMapDecorator.this.wrapValue(var4);
                  return new Entry<Long, Character>() {
                     private Character val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var3) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Long getKey() {
                        return var3;
                     }

                     public Character getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var3.hashCode() + this.val.hashCode();
                     }

                     public Character setValue(Character var1) {
                        this.val = var1;
                        return TLongCharMapDecorator.this.put(var3, var1);
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

         public boolean add(Entry<Long, Character> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Long var3 = (Long)((Entry)var1).getKey();
               TLongCharMapDecorator.this._map.remove(TLongCharMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Long, Character>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TLongCharMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Character && this._map.containsValue(this.unwrapValue(var1));
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
   public void putAll(Map<? extends Long, ? extends Character> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Long)var4.getKey(), (Character)var4.getValue());
      }
   }

   protected Long wrapKey(long var1) {
      return var1;
   }

   protected long unwrapKey(Object var1) {
      return (Long)var1;
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
      this._map = (TLongCharMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
