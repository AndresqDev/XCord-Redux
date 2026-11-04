package io.netty.handler.codec;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class EmptyHeaders<K, V, T extends Headers<K, V, T>> implements Headers<K, V, T> {
   @Override
   public V get(K var1) {
      return null;
   }

   @Override
   public V get(K var1, V var2) {
      return (V)var2;
   }

   @Override
   public V getAndRemove(K var1) {
      return null;
   }

   @Override
   public V getAndRemove(K var1, V var2) {
      return (V)var2;
   }

   @Override
   public List<V> getAll(K var1) {
      return Collections.emptyList();
   }

   @Override
   public List<V> getAllAndRemove(K var1) {
      return Collections.emptyList();
   }

   @Override
   public Boolean getBoolean(K var1) {
      return null;
   }

   @Override
   public boolean getBoolean(K var1, boolean var2) {
      return var2;
   }

   @Override
   public Byte getByte(K var1) {
      return null;
   }

   @Override
   public byte getByte(K var1, byte var2) {
      return var2;
   }

   @Override
   public Character getChar(K var1) {
      return null;
   }

   @Override
   public char getChar(K var1, char var2) {
      return var2;
   }

   @Override
   public Short getShort(K var1) {
      return null;
   }

   @Override
   public short getShort(K var1, short var2) {
      return var2;
   }

   @Override
   public Integer getInt(K var1) {
      return null;
   }

   @Override
   public int getInt(K var1, int var2) {
      return var2;
   }

   @Override
   public Long getLong(K var1) {
      return null;
   }

   @Override
   public long getLong(K var1, long var2) {
      return var2;
   }

   @Override
   public Float getFloat(K var1) {
      return null;
   }

   @Override
   public float getFloat(K var1, float var2) {
      return var2;
   }

   @Override
   public Double getDouble(K var1) {
      return null;
   }

   @Override
   public double getDouble(K var1, double var2) {
      return var2;
   }

   @Override
   public Long getTimeMillis(K var1) {
      return null;
   }

   @Override
   public long getTimeMillis(K var1, long var2) {
      return var2;
   }

   @Override
   public Boolean getBooleanAndRemove(K var1) {
      return null;
   }

   @Override
   public boolean getBooleanAndRemove(K var1, boolean var2) {
      return var2;
   }

   @Override
   public Byte getByteAndRemove(K var1) {
      return null;
   }

   @Override
   public byte getByteAndRemove(K var1, byte var2) {
      return var2;
   }

   @Override
   public Character getCharAndRemove(K var1) {
      return null;
   }

   @Override
   public char getCharAndRemove(K var1, char var2) {
      return var2;
   }

   @Override
   public Short getShortAndRemove(K var1) {
      return null;
   }

   @Override
   public short getShortAndRemove(K var1, short var2) {
      return var2;
   }

   @Override
   public Integer getIntAndRemove(K var1) {
      return null;
   }

   @Override
   public int getIntAndRemove(K var1, int var2) {
      return var2;
   }

   @Override
   public Long getLongAndRemove(K var1) {
      return null;
   }

   @Override
   public long getLongAndRemove(K var1, long var2) {
      return var2;
   }

   @Override
   public Float getFloatAndRemove(K var1) {
      return null;
   }

   @Override
   public float getFloatAndRemove(K var1, float var2) {
      return var2;
   }

   @Override
   public Double getDoubleAndRemove(K var1) {
      return null;
   }

   @Override
   public double getDoubleAndRemove(K var1, double var2) {
      return var2;
   }

   @Override
   public Long getTimeMillisAndRemove(K var1) {
      return null;
   }

   @Override
   public long getTimeMillisAndRemove(K var1, long var2) {
      return var2;
   }

   @Override
   public boolean contains(K var1) {
      return false;
   }

   @Override
   public boolean contains(K var1, V var2) {
      return false;
   }

   @Override
   public boolean containsObject(K var1, Object var2) {
      return false;
   }

   @Override
   public boolean containsBoolean(K var1, boolean var2) {
      return false;
   }

   @Override
   public boolean containsByte(K var1, byte var2) {
      return false;
   }

   @Override
   public boolean containsChar(K var1, char var2) {
      return false;
   }

   @Override
   public boolean containsShort(K var1, short var2) {
      return false;
   }

   @Override
   public boolean containsInt(K var1, int var2) {
      return false;
   }

   @Override
   public boolean containsLong(K var1, long var2) {
      return false;
   }

   @Override
   public boolean containsFloat(K var1, float var2) {
      return false;
   }

   @Override
   public boolean containsDouble(K var1, double var2) {
      return false;
   }

   @Override
   public boolean containsTimeMillis(K var1, long var2) {
      return false;
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public Set<K> names() {
      return Collections.emptySet();
   }

   @Override
   public T add(K var1, V var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T add(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T add(K var1, V... var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addObject(K var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addObject(K var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addObject(K var1, Object... var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addBoolean(K var1, boolean var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addByte(K var1, byte var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addChar(K var1, char var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addShort(K var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addInt(K var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addLong(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addFloat(K var1, float var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addDouble(K var1, double var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T addTimeMillis(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T add(Headers<? extends K, ? extends V, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T set(K var1, V var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T set(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T set(K var1, V... var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setObject(K var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setObject(K var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setObject(K var1, Object... var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setBoolean(K var1, boolean var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setByte(K var1, byte var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setChar(K var1, char var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setShort(K var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setInt(K var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setLong(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setFloat(K var1, float var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setDouble(K var1, double var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setTimeMillis(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T set(Headers<? extends K, ? extends V, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public T setAll(Headers<? extends K, ? extends V, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public boolean remove(K var1) {
      return false;
   }

   @Override
   public T clear() {
      return this.thisT();
   }

   public Iterator<V> valueIterator(K var1) {
      List var2 = Collections.emptyList();
      return var2.iterator();
   }

   @Override
   public Iterator<Entry<K, V>> iterator() {
      List var1 = Collections.emptyList();
      return var1.iterator();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Headers)) {
         return false;
      } else {
         Headers var2 = (Headers)var1;
         return this.isEmpty() && var2.isEmpty();
      }
   }

   @Override
   public int hashCode() {
      return -1028477387;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + '[' + ']';
   }

   private T thisT() {
      return (T)this;
   }
}
