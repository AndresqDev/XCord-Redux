package io.netty.handler.codec;

import io.netty.util.HashingStrategy;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class DefaultHeaders<K, V, T extends Headers<K, V, T>> implements Headers<K, V, T> {
   static final int HASH_CODE_SEED = -1028477387;
   private final DefaultHeaders.HeaderEntry<K, V>[] entries;
   protected final DefaultHeaders.HeaderEntry<K, V> head;
   private final byte hashMask;
   private final ValueConverter<V> valueConverter;
   private final DefaultHeaders.NameValidator<K> nameValidator;
   private final HashingStrategy<K> hashingStrategy;
   int size;

   public DefaultHeaders(ValueConverter<V> var1) {
      this(HashingStrategy.JAVA_HASHER, var1);
   }

   public DefaultHeaders(ValueConverter<V> var1, DefaultHeaders.NameValidator<K> var2) {
      this(HashingStrategy.JAVA_HASHER, var1, var2);
   }

   public DefaultHeaders(HashingStrategy<K> var1, ValueConverter<V> var2) {
      this(var1, var2, DefaultHeaders.NameValidator.NOT_NULL);
   }

   public DefaultHeaders(HashingStrategy<K> var1, ValueConverter<V> var2, DefaultHeaders.NameValidator<K> var3) {
      this(var1, var2, var3, 16);
   }

   public DefaultHeaders(HashingStrategy<K> var1, ValueConverter<V> var2, DefaultHeaders.NameValidator<K> var3, int var4) {
      this.valueConverter = ObjectUtil.checkNotNull(var2, "valueConverter");
      this.nameValidator = ObjectUtil.checkNotNull(var3, "nameValidator");
      this.hashingStrategy = ObjectUtil.checkNotNull(var1, "nameHashingStrategy");
      this.entries = new DefaultHeaders.HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(var4, 128)))];
      this.hashMask = (byte)(this.entries.length - 1);
      this.head = new DefaultHeaders.HeaderEntry<>();
   }

   @Override
   public V get(K var1) {
      ObjectUtil.checkNotNull(var1, "name");
      int var2 = this.hashingStrategy.hashCode((K)var1);
      int var3 = this.index(var2);
      DefaultHeaders.HeaderEntry var4 = this.entries[var3];

      Object var5;
      for (var5 = null; var4 != null; var4 = var4.next) {
         if (var4.hash == var2 && this.hashingStrategy.equals((K)var1, var4.key)) {
            var5 = var4.value;
         }
      }

      return (V)var5;
   }

   @Override
   public V get(K var1, V var2) {
      Object var3 = this.get((K)var1);
      return (V)(var3 == null ? var2 : var3);
   }

   @Override
   public V getAndRemove(K var1) {
      int var2 = this.hashingStrategy.hashCode((K)var1);
      return this.remove0(var2, this.index(var2), ObjectUtil.checkNotNull((K)var1, "name"));
   }

   @Override
   public V getAndRemove(K var1, V var2) {
      Object var3 = this.getAndRemove((K)var1);
      return (V)(var3 == null ? var2 : var3);
   }

   @Override
   public List<V> getAll(K var1) {
      ObjectUtil.checkNotNull(var1, "name");
      LinkedList var2 = new LinkedList();
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);

      for (DefaultHeaders.HeaderEntry var5 = this.entries[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && this.hashingStrategy.equals((K)var1, var5.key)) {
            var2.addFirst(var5.getValue());
         }
      }

      return var2;
   }

   public Iterator<V> valueIterator(K var1) {
      return new DefaultHeaders.ValueIterator(var1);
   }

   @Override
   public List<V> getAllAndRemove(K var1) {
      List var2 = this.getAll((K)var1);
      this.remove((K)var1);
      return var2;
   }

   @Override
   public boolean contains(K var1) {
      return this.get((K)var1) != null;
   }

   @Override
   public boolean containsObject(K var1, Object var2) {
      return this.contains((K)var1, this.fromObject((K)var1, var2));
   }

   @Override
   public boolean containsBoolean(K var1, boolean var2) {
      return this.contains((K)var1, this.fromBoolean((K)var1, var2));
   }

   @Override
   public boolean containsByte(K var1, byte var2) {
      return this.contains((K)var1, this.fromByte((K)var1, var2));
   }

   @Override
   public boolean containsChar(K var1, char var2) {
      return this.contains((K)var1, this.fromChar((K)var1, var2));
   }

   @Override
   public boolean containsShort(K var1, short var2) {
      return this.contains((K)var1, this.fromShort((K)var1, var2));
   }

   @Override
   public boolean containsInt(K var1, int var2) {
      return this.contains((K)var1, this.fromInt((K)var1, var2));
   }

   @Override
   public boolean containsLong(K var1, long var2) {
      return this.contains((K)var1, this.fromLong((K)var1, var2));
   }

   @Override
   public boolean containsFloat(K var1, float var2) {
      return this.contains((K)var1, this.fromFloat((K)var1, var2));
   }

   @Override
   public boolean containsDouble(K var1, double var2) {
      return this.contains((K)var1, this.fromDouble((K)var1, var2));
   }

   @Override
   public boolean containsTimeMillis(K var1, long var2) {
      return this.contains((K)var1, this.fromTimeMillis((K)var1, var2));
   }

   @Override
   public boolean contains(K var1, V var2) {
      return this.contains((K)var1, (V)var2, HashingStrategy.JAVA_HASHER);
   }

   public final boolean contains(K var1, V var2, HashingStrategy<? super V> var3) {
      ObjectUtil.checkNotNull(var1, "name");
      int var4 = this.hashingStrategy.hashCode((K)var1);
      int var5 = this.index(var4);

      for (DefaultHeaders.HeaderEntry var6 = this.entries[var5]; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && this.hashingStrategy.equals((K)var1, var6.key) && var3.equals(var2, var6.value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.head == this.head.after;
   }

   @Override
   public Set<K> names() {
      if (this.isEmpty()) {
         return Collections.emptySet();
      } else {
         LinkedHashSet var1 = new LinkedHashSet(this.size());

         for (DefaultHeaders.HeaderEntry var2 = this.head.after; var2 != this.head; var2 = var2.after) {
            var1.add(var2.getKey());
         }

         return var1;
      }
   }

   @Override
   public T add(K var1, V var2) {
      this.nameValidator.validateName((K)var1);
      ObjectUtil.checkNotNull(var2, "value");
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);
      this.add0(var3, var4, (K)var1, (V)var2);
      return this.thisT();
   }

   @Override
   public T add(K var1, Iterable<? extends V> var2) {
      this.nameValidator.validateName((K)var1);
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);

      for (Object var6 : var2) {
         this.add0(var3, var4, (K)var1, (V)var6);
      }

      return this.thisT();
   }

   @Override
   public T add(K var1, V... var2) {
      this.nameValidator.validateName((K)var1);
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);

      for (Object var8 : var2) {
         this.add0(var3, var4, (K)var1, (V)var8);
      }

      return this.thisT();
   }

   @Override
   public T addObject(K var1, Object var2) {
      return this.add((K)var1, this.fromObject((K)var1, var2));
   }

   @Override
   public T addObject(K var1, Iterable<?> var2) {
      for (Object var4 : var2) {
         this.addObject((K)var1, var4);
      }

      return this.thisT();
   }

   @Override
   public T addObject(K var1, Object... var2) {
      for (Object var6 : var2) {
         this.addObject((K)var1, var6);
      }

      return this.thisT();
   }

   @Override
   public T addInt(K var1, int var2) {
      return this.add((K)var1, this.fromInt((K)var1, var2));
   }

   @Override
   public T addLong(K var1, long var2) {
      return this.add((K)var1, this.fromLong((K)var1, var2));
   }

   @Override
   public T addDouble(K var1, double var2) {
      return this.add((K)var1, this.fromDouble((K)var1, var2));
   }

   @Override
   public T addTimeMillis(K var1, long var2) {
      return this.add((K)var1, this.fromTimeMillis((K)var1, var2));
   }

   @Override
   public T addChar(K var1, char var2) {
      return this.add((K)var1, this.fromChar((K)var1, var2));
   }

   @Override
   public T addBoolean(K var1, boolean var2) {
      return this.add((K)var1, this.fromBoolean((K)var1, var2));
   }

   @Override
   public T addFloat(K var1, float var2) {
      return this.add((K)var1, this.fromFloat((K)var1, var2));
   }

   @Override
   public T addByte(K var1, byte var2) {
      return this.add((K)var1, this.fromByte((K)var1, var2));
   }

   @Override
   public T addShort(K var1, short var2) {
      return this.add((K)var1, this.fromShort((K)var1, var2));
   }

   @Override
   public T add(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 == this) {
         throw new IllegalArgumentException("can't add to itself.");
      } else {
         this.addImpl(var1);
         return this.thisT();
      }
   }

   protected void addImpl(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 instanceof DefaultHeaders) {
         DefaultHeaders var2 = (DefaultHeaders)var1;
         DefaultHeaders.HeaderEntry var3 = var2.head.after;
         if (var2.hashingStrategy == this.hashingStrategy && var2.nameValidator == this.nameValidator) {
            while (var3 != var2.head) {
               this.add0(var3.hash, this.index(var3.hash), var3.key, var3.value);
               var3 = var3.after;
            }
         } else {
            while (var3 != var2.head) {
               this.add(var3.key, var3.value);
               var3 = var3.after;
            }
         }
      } else {
         for (Entry var5 : var1) {
            this.add((K)var5.getKey(), (V)var5.getValue());
         }
      }
   }

   @Override
   public T set(K var1, V var2) {
      this.nameValidator.validateName((K)var1);
      ObjectUtil.checkNotNull(var2, "value");
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, (K)var1);
      this.add0(var3, var4, (K)var1, (V)var2);
      return this.thisT();
   }

   @Override
   public T set(K var1, Iterable<? extends V> var2) {
      this.nameValidator.validateName((K)var1);
      ObjectUtil.checkNotNull(var2, "values");
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, (K)var1);

      for (Object var6 : var2) {
         if (var6 == null) {
            break;
         }

         this.add0(var3, var4, (K)var1, (V)var6);
      }

      return this.thisT();
   }

   @Override
   public T set(K var1, V... var2) {
      this.nameValidator.validateName((K)var1);
      ObjectUtil.checkNotNull(var2, "values");
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, (K)var1);

      for (Object var8 : var2) {
         if (var8 == null) {
            break;
         }

         this.add0(var3, var4, (K)var1, (V)var8);
      }

      return this.thisT();
   }

   @Override
   public T setObject(K var1, Object var2) {
      Object var3 = ObjectUtil.checkNotNull(this.fromObject((K)var1, var2), "convertedValue");
      return this.set((K)var1, (V)var3);
   }

   @Override
   public T setObject(K var1, Iterable<?> var2) {
      this.nameValidator.validateName((K)var1);
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, (K)var1);

      for (Object var6 : var2) {
         if (var6 == null) {
            break;
         }

         this.add0(var3, var4, (K)var1, this.fromObject((K)var1, var6));
      }

      return this.thisT();
   }

   @Override
   public T setObject(K var1, Object... var2) {
      this.nameValidator.validateName((K)var1);
      int var3 = this.hashingStrategy.hashCode((K)var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, (K)var1);

      for (Object var8 : var2) {
         if (var8 == null) {
            break;
         }

         this.add0(var3, var4, (K)var1, this.fromObject((K)var1, var8));
      }

      return this.thisT();
   }

   @Override
   public T setInt(K var1, int var2) {
      return this.set((K)var1, this.fromInt((K)var1, var2));
   }

   @Override
   public T setLong(K var1, long var2) {
      return this.set((K)var1, this.fromLong((K)var1, var2));
   }

   @Override
   public T setDouble(K var1, double var2) {
      return this.set((K)var1, this.fromDouble((K)var1, var2));
   }

   @Override
   public T setTimeMillis(K var1, long var2) {
      return this.set((K)var1, this.fromTimeMillis((K)var1, var2));
   }

   @Override
   public T setFloat(K var1, float var2) {
      return this.set((K)var1, this.fromFloat((K)var1, var2));
   }

   @Override
   public T setChar(K var1, char var2) {
      return this.set((K)var1, this.fromChar((K)var1, var2));
   }

   @Override
   public T setBoolean(K var1, boolean var2) {
      return this.set((K)var1, this.fromBoolean((K)var1, var2));
   }

   @Override
   public T setByte(K var1, byte var2) {
      return this.set((K)var1, this.fromByte((K)var1, var2));
   }

   @Override
   public T setShort(K var1, short var2) {
      return this.set((K)var1, this.fromShort((K)var1, var2));
   }

   @Override
   public T set(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 != this) {
         this.clear();
         this.addImpl(var1);
      }

      return this.thisT();
   }

   @Override
   public T setAll(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 != this) {
         for (Object var3 : var1.names()) {
            this.remove((K)var3);
         }

         this.addImpl(var1);
      }

      return this.thisT();
   }

   @Override
   public boolean remove(K var1) {
      return this.getAndRemove((K)var1) != null;
   }

   @Override
   public T clear() {
      Arrays.fill(this.entries, null);
      this.head.before = this.head.after = this.head;
      this.size = 0;
      return this.thisT();
   }

   @Override
   public Iterator<Entry<K, V>> iterator() {
      return new DefaultHeaders.HeaderIterator();
   }

   @Override
   public Boolean getBoolean(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toBoolean((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public boolean getBoolean(K var1, boolean var2) {
      Boolean var3 = this.getBoolean((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Byte getByte(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toByte((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public byte getByte(K var1, byte var2) {
      Byte var3 = this.getByte((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Character getChar(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toChar((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public char getChar(K var1, char var2) {
      Character var3 = this.getChar((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Short getShort(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toShort((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public short getShort(K var1, short var2) {
      Short var3 = this.getShort((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Integer getInt(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toInt((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public int getInt(K var1, int var2) {
      Integer var3 = this.getInt((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Long getLong(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toLong((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getLong(K var1, long var2) {
      Long var4 = this.getLong((K)var1);
      return var4 != null ? var4 : var2;
   }

   @Override
   public Float getFloat(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toFloat((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public float getFloat(K var1, float var2) {
      Float var3 = this.getFloat((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Double getDouble(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toDouble((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public double getDouble(K var1, double var2) {
      Double var4 = this.getDouble((K)var1);
      return var4 != null ? var4 : var2;
   }

   @Override
   public Long getTimeMillis(K var1) {
      Object var2 = this.get((K)var1);

      try {
         return var2 != null ? this.toTimeMillis((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getTimeMillis(K var1, long var2) {
      Long var4 = this.getTimeMillis((K)var1);
      return var4 != null ? var4 : var2;
   }

   @Override
   public Boolean getBooleanAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toBoolean((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public boolean getBooleanAndRemove(K var1, boolean var2) {
      Boolean var3 = this.getBooleanAndRemove((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Byte getByteAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toByte((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public byte getByteAndRemove(K var1, byte var2) {
      Byte var3 = this.getByteAndRemove((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Character getCharAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toChar((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public char getCharAndRemove(K var1, char var2) {
      Character var3 = this.getCharAndRemove((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Short getShortAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toShort((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public short getShortAndRemove(K var1, short var2) {
      Short var3 = this.getShortAndRemove((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Integer getIntAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toInt((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public int getIntAndRemove(K var1, int var2) {
      Integer var3 = this.getIntAndRemove((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Long getLongAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toLong((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getLongAndRemove(K var1, long var2) {
      Long var4 = this.getLongAndRemove((K)var1);
      return var4 != null ? var4 : var2;
   }

   @Override
   public Float getFloatAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toFloat((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public float getFloatAndRemove(K var1, float var2) {
      Float var3 = this.getFloatAndRemove((K)var1);
      return var3 != null ? var3 : var2;
   }

   @Override
   public Double getDoubleAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toDouble((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public double getDoubleAndRemove(K var1, double var2) {
      Double var4 = this.getDoubleAndRemove((K)var1);
      return var4 != null ? var4 : var2;
   }

   @Override
   public Long getTimeMillisAndRemove(K var1) {
      Object var2 = this.getAndRemove((K)var1);

      try {
         return var2 != null ? this.toTimeMillis((K)var1, (V)var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   @Override
   public long getTimeMillisAndRemove(K var1, long var2) {
      Long var4 = this.getTimeMillisAndRemove((K)var1);
      return var4 != null ? var4 : var2;
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof Headers) ? false : this.equals((Headers<K, V, ?>)var1, HashingStrategy.JAVA_HASHER);
   }

   @Override
   public int hashCode() {
      return this.hashCode(HashingStrategy.JAVA_HASHER);
   }

   public final boolean equals(Headers<K, V, ?> var1, HashingStrategy<V> var2) {
      if (var1.size() != this.size()) {
         return false;
      } else if (this == var1) {
         return true;
      } else {
         for (Object var4 : this.names()) {
            List var5 = var1.getAll(var4);
            List var6 = this.getAll((K)var4);
            if (var5.size() != var6.size()) {
               return false;
            }

            for (int var7 = 0; var7 < var5.size(); var7++) {
               if (!var2.equals(var5.get(var7), var6.get(var7))) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public final int hashCode(HashingStrategy<V> var1) {
      int var2 = -1028477387;

      for (Object var4 : this.names()) {
         var2 = 31 * var2 + this.hashingStrategy.hashCode((K)var4);
         List var5 = this.getAll((K)var4);

         for (int var6 = 0; var6 < var5.size(); var6++) {
            var2 = 31 * var2 + var1.hashCode(var5.get(var6));
         }
      }

      return var2;
   }

   @Override
   public String toString() {
      return HeadersUtils.toString(this.getClass(), this.iterator(), this.size());
   }

   protected DefaultHeaders.HeaderEntry<K, V> newHeaderEntry(int var1, K var2, V var3, DefaultHeaders.HeaderEntry<K, V> var4) {
      return new DefaultHeaders.HeaderEntry<>(var1, (K)var2, (V)var3, var4, this.head);
   }

   protected ValueConverter<V> valueConverter() {
      return this.valueConverter;
   }

   private int index(int var1) {
      return var1 & this.hashMask;
   }

   private void add0(int var1, int var2, K var3, V var4) {
      this.entries[var2] = this.newHeaderEntry(var1, (K)var3, (V)var4, this.entries[var2]);
      this.size++;
   }

   private V remove0(int var1, int var2, K var3) {
      DefaultHeaders.HeaderEntry var4 = this.entries[var2];
      if (var4 == null) {
         return null;
      } else {
         Object var5 = null;

         for (DefaultHeaders.HeaderEntry var6 = var4.next; var6 != null; var6 = var4.next) {
            if (var6.hash == var1 && this.hashingStrategy.equals((K)var3, var6.key)) {
               var5 = var6.value;
               var4.next = var6.next;
               var6.remove();
               this.size--;
            } else {
               var4 = var6;
            }
         }

         var4 = this.entries[var2];
         if (var4.hash == var1 && this.hashingStrategy.equals((K)var3, var4.key)) {
            if (var5 == null) {
               var5 = var4.value;
            }

            this.entries[var2] = var4.next;
            var4.remove();
            this.size--;
         }

         return (V)var5;
      }
   }

   DefaultHeaders.HeaderEntry<K, V> remove0(DefaultHeaders.HeaderEntry<K, V> var1, DefaultHeaders.HeaderEntry<K, V> var2) {
      int var3 = this.index(var1.hash);
      DefaultHeaders.HeaderEntry var4 = this.entries[var3];
      if (var4 == var1) {
         this.entries[var3] = var1.next;
         var2 = this.entries[var3];
      } else if (var2 == null) {
         var2 = var4;

         DefaultHeaders.HeaderEntry var5;
         for (var5 = var4.next; var5 != null && var5 != var1; var5 = var5.next) {
            var2 = var5;
         }

         assert var5 != null : "Entry not found in its hash bucket: " + var1;

         var2.next = var1.next;
      } else {
         var2.next = var1.next;
      }

      var1.remove();
      this.size--;
      return var2;
   }

   private T thisT() {
      return (T)this;
   }

   private V fromObject(K var1, Object var2) {
      try {
         return this.valueConverter.convertObject(ObjectUtil.checkNotNull(var2, "value"));
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert object value for header '" + var1 + '\'', var4);
      }
   }

   private V fromBoolean(K var1, boolean var2) {
      try {
         return this.valueConverter.convertBoolean(var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert boolean value for header '" + var1 + '\'', var4);
      }
   }

   private V fromByte(K var1, byte var2) {
      try {
         return this.valueConverter.convertByte(var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert byte value for header '" + var1 + '\'', var4);
      }
   }

   private V fromChar(K var1, char var2) {
      try {
         return this.valueConverter.convertChar(var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert char value for header '" + var1 + '\'', var4);
      }
   }

   private V fromShort(K var1, short var2) {
      try {
         return this.valueConverter.convertShort(var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert short value for header '" + var1 + '\'', var4);
      }
   }

   private V fromInt(K var1, int var2) {
      try {
         return this.valueConverter.convertInt(var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert int value for header '" + var1 + '\'', var4);
      }
   }

   private V fromLong(K var1, long var2) {
      try {
         return this.valueConverter.convertLong(var2);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Failed to convert long value for header '" + var1 + '\'', var5);
      }
   }

   private V fromFloat(K var1, float var2) {
      try {
         return this.valueConverter.convertFloat(var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert float value for header '" + var1 + '\'', var4);
      }
   }

   private V fromDouble(K var1, double var2) {
      try {
         return this.valueConverter.convertDouble(var2);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Failed to convert double value for header '" + var1 + '\'', var5);
      }
   }

   private V fromTimeMillis(K var1, long var2) {
      try {
         return this.valueConverter.convertTimeMillis(var2);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Failed to convert millsecond value for header '" + var1 + '\'', var5);
      }
   }

   private boolean toBoolean(K var1, V var2) {
      try {
         return this.valueConverter.convertToBoolean((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to boolean for header '" + var1 + '\'');
      }
   }

   private byte toByte(K var1, V var2) {
      try {
         return this.valueConverter.convertToByte((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to byte for header '" + var1 + '\'');
      }
   }

   private char toChar(K var1, V var2) {
      try {
         return this.valueConverter.convertToChar((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to char for header '" + var1 + '\'');
      }
   }

   private short toShort(K var1, V var2) {
      try {
         return this.valueConverter.convertToShort((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to short for header '" + var1 + '\'');
      }
   }

   private int toInt(K var1, V var2) {
      try {
         return this.valueConverter.convertToInt((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to int for header '" + var1 + 39);
      }
   }

   private long toLong(K var1, V var2) {
      try {
         return this.valueConverter.convertToLong((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to long for header '" + var1 + '\'');
      }
   }

   private float toFloat(K var1, V var2) {
      try {
         return this.valueConverter.convertToFloat((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to float for header '" + var1 + '\'');
      }
   }

   private double toDouble(K var1, V var2) {
      try {
         return this.valueConverter.convertToDouble((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to double for header '" + var1 + '\'');
      }
   }

   private long toTimeMillis(K var1, V var2) {
      try {
         return this.valueConverter.convertToTimeMillis((V)var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Failed to convert header value to millsecond for header '" + var1 + '\'');
      }
   }

   public DefaultHeaders<K, V, T> copy() {
      DefaultHeaders var1 = new DefaultHeaders<>(this.hashingStrategy, this.valueConverter, this.nameValidator, this.entries.length);
      var1.addImpl(this);
      return var1;
   }

   protected static class HeaderEntry<K, V> implements Entry<K, V> {
      protected final int hash;
      protected final K key;
      protected V value;
      protected DefaultHeaders.HeaderEntry<K, V> next;
      protected DefaultHeaders.HeaderEntry<K, V> before;
      protected DefaultHeaders.HeaderEntry<K, V> after;

      protected HeaderEntry(int var1, K var2) {
         this.hash = var1;
         this.key = (K)var2;
      }

      HeaderEntry(int var1, K var2, V var3, DefaultHeaders.HeaderEntry<K, V> var4, DefaultHeaders.HeaderEntry<K, V> var5) {
         this.hash = var1;
         this.key = (K)var2;
         this.value = (V)var3;
         this.next = var4;
         this.after = var5;
         this.before = var5.before;
         this.pointNeighborsToThis();
      }

      HeaderEntry() {
         this.hash = -1;
         this.key = null;
         this.before = this.after = this;
      }

      protected final void pointNeighborsToThis() {
         this.before.after = this;
         this.after.before = this;
      }

      public final DefaultHeaders.HeaderEntry<K, V> before() {
         return this.before;
      }

      public final DefaultHeaders.HeaderEntry<K, V> after() {
         return this.after;
      }

      protected void remove() {
         this.before.after = this.after;
         this.after.before = this.before;
      }

      @Override
      public final K getKey() {
         return this.key;
      }

      @Override
      public final V getValue() {
         return this.value;
      }

      @Override
      public final V setValue(V var1) {
         ObjectUtil.checkNotNull(var1, "value");
         Object var2 = this.value;
         this.value = (V)var1;
         return (V)var2;
      }

      @Override
      public final String toString() {
         return this.key.toString() + '=' + this.value.toString();
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            return (this.getKey() == null ? var2.getKey() == null : this.getKey().equals(var2.getKey()))
               && (this.getValue() == null ? var2.getValue() == null : this.getValue().equals(var2.getValue()));
         }
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }
   }

   private final class HeaderIterator implements Iterator<Entry<K, V>> {
      private DefaultHeaders.HeaderEntry<K, V> current = DefaultHeaders.this.head;

      private HeaderIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.current.after != DefaultHeaders.this.head;
      }

      public Entry<K, V> next() {
         this.current = this.current.after;
         if (this.current == DefaultHeaders.this.head) {
            throw new NoSuchElementException();
         } else {
            return this.current;
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("read only");
      }
   }

   public interface NameValidator<K> {
      DefaultHeaders.NameValidator NOT_NULL = new DefaultHeaders.NameValidator() {
         @Override
         public void validateName(Object var1) {
            ObjectUtil.checkNotNull(var1, "name");
         }
      };

      void validateName(K var1);
   }

   private final class ValueIterator implements Iterator<V> {
      private final K name;
      private final int hash;
      private DefaultHeaders.HeaderEntry<K, V> removalPrevious;
      private DefaultHeaders.HeaderEntry<K, V> previous;
      private DefaultHeaders.HeaderEntry<K, V> next;

      ValueIterator(K var2) {
         this.name = ObjectUtil.checkNotNull((K)var2, "name");
         this.hash = DefaultHeaders.this.hashingStrategy.hashCode((K)var2);
         this.calculateNext(DefaultHeaders.this.entries[DefaultHeaders.this.index(this.hash)]);
      }

      @Override
      public boolean hasNext() {
         return this.next != null;
      }

      @Override
      public V next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            if (this.previous != null) {
               this.removalPrevious = this.previous;
            }

            this.previous = this.next;
            this.calculateNext(this.next.next);
            return this.previous.value;
         }
      }

      @Override
      public void remove() {
         if (this.previous == null) {
            throw new IllegalStateException();
         } else {
            this.removalPrevious = DefaultHeaders.this.remove0(this.previous, this.removalPrevious);
            this.previous = null;
         }
      }

      private void calculateNext(DefaultHeaders.HeaderEntry<K, V> var1) {
         while (var1 != null) {
            if (var1.hash == this.hash && DefaultHeaders.this.hashingStrategy.equals(this.name, var1.key)) {
               this.next = var1;
               return;
            }

            var1 = var1.next;
         }

         this.next = null;
      }
   }
}
