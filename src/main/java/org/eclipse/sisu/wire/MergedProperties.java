package org.eclipse.sisu.wire;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

final class MergedProperties extends AbstractMap<Object, Object> {
   private transient volatile Set<Entry<Object, Object>> entrySet;
   final Map<?, ?>[] properties;

   MergedProperties(List<Map<?, ?>> var1) {
      this.properties = var1.toArray(new Map[var1.size()]);
   }

   @Override
   public Object get(Object var1) {
      for (Map var2 : this.properties) {
         Object var6 = var2.get(var1);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }

   @Override
   public boolean containsKey(Object var1) {
      for (Map var2 : this.properties) {
         if (var2.containsKey(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Set<Entry<Object, Object>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new MergedProperties.MergedEntries();
      }

      return this.entrySet;
   }

   final class MergedEntries extends AbstractSet<Entry<Object, Object>> {
      @Override
      public Iterator<Entry<Object, Object>> iterator() {
         return new Iterator<Entry<Object, Object>>() {
            private Iterator<? extends Entry> itr;
            private int index;

            @Override
            public boolean hasNext() {
               while (this.itr == null || !this.itr.hasNext()) {
                  if (this.index >= MergedProperties.this.properties.length) {
                     return false;
                  }

                  this.itr = MergedProperties.this.properties[this.index++].entrySet().iterator();
               }

               return true;
            }

            public Entry<Object, Object> next() {
               if (this.hasNext()) {
                  return (Entry<Object, Object>)this.itr.next();
               } else {
                  throw new NoSuchElementException();
               }
            }

            @Override
            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }

      @Override
      public int size() {
         int var1 = 0;

         for (Map var2 : MergedProperties.this.properties) {
            var1 += var2.size();
         }

         return var1;
      }
   }
}
