package com.google.protobuf;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.Map.Entry;

public final class Internal {
   static final Charset UTF_8 = Charset.forName("UTF-8");
   static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
   public static final CodedInputStream EMPTY_CODED_INPUT_STREAM = CodedInputStream.newInstance(EMPTY_BYTE_ARRAY);

   private Internal() {
   }

   static <T> T checkNotNull(T var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return (T)var0;
      }
   }

   static <T> T checkNotNull(T var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException(var1);
      } else {
         return (T)var0;
      }
   }

   public static String stringDefaultValue(String var0) {
      return new String(var0.getBytes(ISO_8859_1), UTF_8);
   }

   public static ByteString bytesDefaultValue(String var0) {
      return ByteString.copyFrom(var0.getBytes(ISO_8859_1));
   }

   public static byte[] byteArrayDefaultValue(String var0) {
      return var0.getBytes(ISO_8859_1);
   }

   public static ByteBuffer byteBufferDefaultValue(String var0) {
      return ByteBuffer.wrap(byteArrayDefaultValue(var0));
   }

   public static ByteBuffer copyByteBuffer(ByteBuffer var0) {
      ByteBuffer var1 = var0.duplicate();
      ((Buffer)var1).clear();
      ByteBuffer var2 = ByteBuffer.allocate(var1.capacity());
      var2.put(var1);
      ((Buffer)var2).clear();
      return var2;
   }

   public static boolean isValidUtf8(ByteString var0) {
      return var0.isValidUtf8();
   }

   public static boolean isValidUtf8(byte[] var0) {
      return Utf8.isValidUtf8(var0);
   }

   public static byte[] toByteArray(String var0) {
      return var0.getBytes(UTF_8);
   }

   public static String toStringUtf8(byte[] var0) {
      return new String(var0, UTF_8);
   }

   public static int hashLong(long var0) {
      return (int)(var0 ^ var0 >>> 32);
   }

   public static int hashBoolean(boolean var0) {
      return var0 ? 1231 : 1237;
   }

   public static int hashEnum(Internal.EnumLite var0) {
      return var0.getNumber();
   }

   public static int hashEnumList(List<? extends Internal.EnumLite> var0) {
      int var1 = 1;

      for (Internal.EnumLite var3 : var0) {
         var1 = 31 * var1 + hashEnum(var3);
      }

      return var1;
   }

   public static boolean equals(List<byte[]> var0, List<byte[]> var1) {
      if (var0.size() != var1.size()) {
         return false;
      } else {
         for (int var2 = 0; var2 < var0.size(); var2++) {
            if (!Arrays.equals((byte[])var0.get(var2), (byte[])var1.get(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static int hashCode(List<byte[]> var0) {
      int var1 = 1;

      for (byte[] var3 : var0) {
         var1 = 31 * var1 + hashCode(var3);
      }

      return var1;
   }

   public static int hashCode(byte[] var0) {
      return hashCode(var0, 0, var0.length);
   }

   static int hashCode(byte[] var0, int var1, int var2) {
      int var3 = partialHash(var2, var0, var1, var2);
      return var3 == 0 ? 1 : var3;
   }

   static int partialHash(int var0, byte[] var1, int var2, int var3) {
      for (int var4 = var2; var4 < var2 + var3; var4++) {
         var0 = var0 * 31 + var1[var4];
      }

      return var0;
   }

   public static boolean equalsByteBuffer(ByteBuffer var0, ByteBuffer var1) {
      return var0.capacity() != var1.capacity() ? false : ((Buffer)var0.duplicate()).clear().equals(((Buffer)var1.duplicate()).clear());
   }

   public static boolean equalsByteBuffer(List<ByteBuffer> var0, List<ByteBuffer> var1) {
      if (var0.size() != var1.size()) {
         return false;
      } else {
         for (int var2 = 0; var2 < var0.size(); var2++) {
            if (!equalsByteBuffer((ByteBuffer)var0.get(var2), (ByteBuffer)var1.get(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static int hashCodeByteBuffer(List<ByteBuffer> var0) {
      int var1 = 1;

      for (ByteBuffer var3 : var0) {
         var1 = 31 * var1 + hashCodeByteBuffer(var3);
      }

      return var1;
   }

   public static int hashCodeByteBuffer(ByteBuffer var0) {
      if (var0.hasArray()) {
         int var6 = partialHash(var0.capacity(), var0.array(), var0.arrayOffset(), var0.capacity());
         return var6 == 0 ? 1 : var6;
      } else {
         int var1 = var0.capacity() > 4096 ? 4096 : var0.capacity();
         byte[] var2 = new byte[var1];
         ByteBuffer var3 = var0.duplicate();
         ((Buffer)var3).clear();
         int var4 = var0.capacity();

         while (var3.remaining() > 0) {
            int var5 = var3.remaining() <= var1 ? var3.remaining() : var1;
            var3.get(var2, 0, var5);
            var4 = partialHash(var4, var2, 0, var5);
         }

         return var4 == 0 ? 1 : var4;
      }
   }

   public static <T extends MessageLite> T getDefaultInstance(Class<T> var0) {
      try {
         java.lang.reflect.Method var1 = var0.getMethod("getDefaultInstance");
         return (T)var1.invoke(var1);
      } catch (Exception var2) {
         throw new RuntimeException("Failed to get default instance for " + var0, var2);
      }
   }

   static Object mergeMessage(Object var0, Object var1) {
      return ((MessageLite)var0).toBuilder().mergeFrom((MessageLite)var1).buildPartial();
   }

   public interface BooleanList extends Internal.ProtobufList<Boolean> {
      boolean getBoolean(int var1);

      void addBoolean(boolean var1);

      boolean setBoolean(int var1, boolean var2);

      Internal.BooleanList mutableCopyWithCapacity(int var1);
   }

   public interface DoubleList extends Internal.ProtobufList<Double> {
      double getDouble(int var1);

      void addDouble(double var1);

      double setDouble(int var1, double var2);

      Internal.DoubleList mutableCopyWithCapacity(int var1);
   }

   public interface EnumLite {
      int getNumber();
   }

   public interface EnumLiteMap<T extends Internal.EnumLite> {
      T findValueByNumber(int var1);
   }

   public interface EnumVerifier {
      boolean isInRange(int var1);
   }

   public interface FloatList extends Internal.ProtobufList<Float> {
      float getFloat(int var1);

      void addFloat(float var1);

      float setFloat(int var1, float var2);

      Internal.FloatList mutableCopyWithCapacity(int var1);
   }

   public interface IntList extends Internal.ProtobufList<Integer> {
      int getInt(int var1);

      void addInt(int var1);

      int setInt(int var1, int var2);

      Internal.IntList mutableCopyWithCapacity(int var1);
   }

   public static class ListAdapter<F, T> extends AbstractList<T> {
      private final List<F> fromList;
      private final Internal.ListAdapter.Converter<F, T> converter;

      public ListAdapter(List<F> var1, Internal.ListAdapter.Converter<F, T> var2) {
         this.fromList = var1;
         this.converter = var2;
      }

      @Override
      public T get(int var1) {
         return this.converter.convert(this.fromList.get(var1));
      }

      @Override
      public int size() {
         return this.fromList.size();
      }

      public interface Converter<F, T> {
         T convert(F var1);
      }
   }

   public interface LongList extends Internal.ProtobufList<Long> {
      long getLong(int var1);

      void addLong(long var1);

      long setLong(int var1, long var2);

      Internal.LongList mutableCopyWithCapacity(int var1);
   }

   public static class MapAdapter<K, V, RealValue> extends AbstractMap<K, V> {
      private final Map<K, RealValue> realMap;
      private final Internal.MapAdapter.Converter<RealValue, V> valueConverter;

      public static <T extends Internal.EnumLite> Internal.MapAdapter.Converter<Integer, T> newEnumConverter(final Internal.EnumLiteMap<T> var0, final T var1) {
         return new Internal.MapAdapter.Converter<Integer, T>() {
            public T doForward(Integer var1x) {
               Internal.EnumLite var2 = var0.findValueByNumber(var1x);
               return (T)(var2 == null ? var1 : var2);
            }

            public Integer doBackward(T var1x) {
               return var1x.getNumber();
            }
         };
      }

      public MapAdapter(Map<K, RealValue> var1, Internal.MapAdapter.Converter<RealValue, V> var2) {
         this.realMap = var1;
         this.valueConverter = var2;
      }

      @Override
      public V get(Object var1) {
         Object var2 = this.realMap.get(var1);
         return var2 == null ? null : this.valueConverter.doForward((RealValue)var2);
      }

      @Override
      public V put(K var1, V var2) {
         Object var3 = this.realMap.put((K)var1, this.valueConverter.doBackward((V)var2));
         return var3 == null ? null : this.valueConverter.doForward((RealValue)var3);
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return new Internal.MapAdapter.SetAdapter(this.realMap.entrySet());
      }

      public interface Converter<A, B> {
         B doForward(A var1);

         A doBackward(B var1);
      }

      private class EntryAdapter implements Entry<K, V> {
         private final Entry<K, RealValue> realEntry;

         public EntryAdapter(Entry<K, RealValue> var2) {
            this.realEntry = var2;
         }

         @Override
         public K getKey() {
            return this.realEntry.getKey();
         }

         @Override
         public V getValue() {
            return MapAdapter.this.valueConverter.doForward(this.realEntry.getValue());
         }

         @Override
         public V setValue(V var1) {
            Object var2 = this.realEntry.setValue(MapAdapter.this.valueConverter.doBackward((V)var1));
            return var2 == null ? null : MapAdapter.this.valueConverter.doForward((RealValue)var2);
         }

         @Override
         public boolean equals(Object var1) {
            if (var1 == this) {
               return true;
            } else if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Entry var2 = (Entry)var1;
               return this.getKey().equals(var2.getKey()) && this.getValue().equals(this.getValue());
            }
         }

         @Override
         public int hashCode() {
            return this.realEntry.hashCode();
         }
      }

      private class IteratorAdapter implements Iterator<Entry<K, V>> {
         private final Iterator<Entry<K, RealValue>> realIterator;

         public IteratorAdapter(Iterator<Entry<K, RealValue>> var2) {
            this.realIterator = var2;
         }

         @Override
         public boolean hasNext() {
            return this.realIterator.hasNext();
         }

         public Entry<K, V> next() {
            return MapAdapter.this.new EntryAdapter(this.realIterator.next());
         }

         @Override
         public void remove() {
            this.realIterator.remove();
         }
      }

      private class SetAdapter extends AbstractSet<Entry<K, V>> {
         private final Set<Entry<K, RealValue>> realSet;

         public SetAdapter(Set<Entry<K, RealValue>> var2) {
            this.realSet = var2;
         }

         @Override
         public Iterator<Entry<K, V>> iterator() {
            return MapAdapter.this.new IteratorAdapter(this.realSet.iterator());
         }

         @Override
         public int size() {
            return this.realSet.size();
         }
      }
   }

   public interface ProtobufList<E> extends List<E>, RandomAccess {
      void makeImmutable();

      boolean isModifiable();

      Internal.ProtobufList<E> mutableCopyWithCapacity(int var1);
   }
}
