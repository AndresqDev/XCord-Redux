package org.codehaus.plexus.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class CachedMap implements Map {
   private final FastMap _backingFastMap;
   private final Map _backingMap;
   private final FastMap _keysMap;
   private final int _mask;
   private final Object[] _keys;
   private final Object[] _values;

   public CachedMap() {
      this(256, new FastMap());
   }

   public CachedMap(int var1) {
      this(var1, new FastMap(var1));
   }

   public CachedMap(int var1, Map var2) {
      byte var3 = 1;

      while (var3 < var1) {
         var3 <<= 1;
      }

      this._keys = new Object[var3];
      this._values = new Object[var3];
      this._mask = var3 - 1;
      if (var2 instanceof FastMap) {
         this._backingFastMap = (FastMap)var2;
         this._backingMap = this._backingFastMap;
         this._keysMap = null;
      } else {
         this._backingFastMap = null;
         this._backingMap = var2;
         this._keysMap = new FastMap(var2.size());

         for (Object var5 : var2.keySet()) {
            this._keysMap.put(var5, var5);
         }
      }
   }

   public int getCacheSize() {
      return this._keys.length;
   }

   public Map getBackingMap() {
      return (Map)(this._backingFastMap != null ? this._backingFastMap : this._backingMap);
   }

   public void flush() {
      for (int var1 = 0; var1 < this._keys.length; var1++) {
         this._keys[var1] = null;
         this._values[var1] = null;
      }

      if (this._keysMap != null) {
         for (Object var2 : this._backingMap.keySet()) {
            this._keysMap.put(var2, var2);
         }
      }
   }

   @Override
   public Object get(Object var1) {
      int var2 = var1.hashCode() & this._mask;
      return var1.equals(this._keys[var2]) ? this._values[var2] : this.getCacheMissed(var1, var2);
   }

   private Object getCacheMissed(Object var1, int var2) {
      if (this._backingFastMap != null) {
         Entry var5 = this._backingFastMap.getEntry(var1);
         if (var5 != null) {
            this._keys[var2] = var5.getKey();
            Object var6 = var5.getValue();
            this._values[var2] = var6;
            return var6;
         } else {
            return null;
         }
      } else {
         Object var3 = this._keysMap.get(var1);
         if (var3 != null) {
            this._keys[var2] = var3;
            Object var4 = this._backingMap.get(var1);
            this._values[var2] = var4;
            return var4;
         } else {
            return null;
         }
      }
   }

   @Override
   public Object put(Object var1, Object var2) {
      int var3 = var1.hashCode() & this._mask;
      if (var1.equals(this._keys[var3])) {
         this._values[var3] = var2;
      } else if (this._keysMap != null) {
         this._keysMap.put(var1, var1);
      }

      return this._backingMap.put(var1, var2);
   }

   @Override
   public Object remove(Object var1) {
      int var2 = var1.hashCode() & this._mask;
      if (var1.equals(this._keys[var2])) {
         this._keys[var2] = null;
      }

      if (this._keysMap != null) {
         this._keysMap.remove(var1);
      }

      return this._backingMap.remove(var1);
   }

   @Override
   public boolean containsKey(Object var1) {
      int var2 = var1.hashCode() & this._mask;
      return var1.equals(this._keys[var2]) ? true : this._backingMap.containsKey(var1);
   }

   @Override
   public int size() {
      return this._backingMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this._backingMap.isEmpty();
   }

   @Override
   public boolean containsValue(Object var1) {
      return this._backingMap.containsValue(var1);
   }

   @Override
   public void putAll(Map var1) {
      this._backingMap.putAll(var1);
      this.flush();
   }

   @Override
   public void clear() {
      this._backingMap.clear();
      this.flush();
   }

   @Override
   public Set keySet() {
      return Collections.unmodifiableSet(this._backingMap.keySet());
   }

   @Override
   public Collection values() {
      return Collections.unmodifiableCollection(this._backingMap.values());
   }

   @Override
   public Set entrySet() {
      return Collections.unmodifiableSet(this._backingMap.entrySet());
   }

   @Override
   public boolean equals(Object var1) {
      return this._backingMap.equals(var1);
   }

   @Override
   public int hashCode() {
      return this._backingMap.hashCode();
   }
}
