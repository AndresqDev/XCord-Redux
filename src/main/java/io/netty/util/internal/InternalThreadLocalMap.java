package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class InternalThreadLocalMap extends UnpaddedInternalThreadLocalMap {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(InternalThreadLocalMap.class);
   private static final ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = new ThreadLocal<>();
   private static final AtomicInteger nextIndex = new AtomicInteger();
   private static final int DEFAULT_ARRAY_LIST_INITIAL_CAPACITY = 8;
   private static final int ARRAY_LIST_CAPACITY_EXPAND_THRESHOLD = 1073741824;
   private static final int ARRAY_LIST_CAPACITY_MAX_SIZE = 2147483639;
   private static final int STRING_BUILDER_INITIAL_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalMap.stringBuilder.initialSize", 1024);
   private static final int STRING_BUILDER_MAX_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalMap.stringBuilder.maxSize", 4096);
   private static final int HANDLER_SHARABLE_CACHE_INITIAL_CAPACITY = 4;
   private static final int INDEXED_VARIABLE_TABLE_INITIAL_SIZE = 32;
   public static final Object UNSET = new Object();
   private Object[] indexedVariables = newIndexedVariableTable();
   private int futureListenerStackDepth;
   private int localChannelReaderStackDepth;
   private Map<Class<?>, Boolean> handlerSharableCache;
   private IntegerHolder counterHashCode;
   private ThreadLocalRandom random;
   private Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache;
   private Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache;
   private StringBuilder stringBuilder;
   private Map<Charset, CharsetEncoder> charsetEncoderCache;
   private Map<Charset, CharsetDecoder> charsetDecoderCache;
   private ArrayList<Object> arrayList;
   private BitSet cleanerFlags;
      public long rp1;
      public long rp2;
      public long rp3;
      public long rp4;
      public long rp5;
      public long rp6;
      public long rp7;
      public long rp8;

   public static InternalThreadLocalMap getIfSet() {
      Thread var0 = Thread.currentThread();
      return var0 instanceof FastThreadLocalThread ? ((FastThreadLocalThread)var0).threadLocalMap() : slowThreadLocalMap.get();
   }

   public static InternalThreadLocalMap get() {
      Thread var0 = Thread.currentThread();
      return var0 instanceof FastThreadLocalThread ? fastGet((FastThreadLocalThread)var0) : slowGet();
   }

   private static InternalThreadLocalMap fastGet(FastThreadLocalThread var0) {
      InternalThreadLocalMap var1 = var0.threadLocalMap();
      if (var1 == null) {
         var0.setThreadLocalMap(var1 = new InternalThreadLocalMap());
      }

      return var1;
   }

   private static InternalThreadLocalMap slowGet() {
      InternalThreadLocalMap var0 = slowThreadLocalMap.get();
      if (var0 == null) {
         var0 = new InternalThreadLocalMap();
         slowThreadLocalMap.set(var0);
      }

      return var0;
   }

   public static void remove() {
      Thread var0 = Thread.currentThread();
      if (var0 instanceof FastThreadLocalThread) {
         ((FastThreadLocalThread)var0).setThreadLocalMap(null);
      } else {
         slowThreadLocalMap.remove();
      }
   }

   public static void destroy() {
      slowThreadLocalMap.remove();
   }

   public static int nextVariableIndex() {
      int var0 = nextIndex.getAndIncrement();
      if (var0 < 2147483639 && var0 >= 0) {
         return var0;
      } else {
         nextIndex.set(2147483639);
         throw new IllegalStateException("too many thread-local indexed variables");
      }
   }

   public static int lastVariableIndex() {
      return nextIndex.get() - 1;
   }

   private InternalThreadLocalMap() {
   }

   private static Object[] newIndexedVariableTable() {
      Object[] var0 = new Object[32];
      Arrays.fill(var0, UNSET);
      return var0;
   }

   public int size() {
      int var1 = 0;
      if (this.futureListenerStackDepth != 0) {
         var1++;
      }

      if (this.localChannelReaderStackDepth != 0) {
         var1++;
      }

      if (this.handlerSharableCache != null) {
         var1++;
      }

      if (this.counterHashCode != null) {
         var1++;
      }

      if (this.random != null) {
         var1++;
      }

      if (this.typeParameterMatcherGetCache != null) {
         var1++;
      }

      if (this.typeParameterMatcherFindCache != null) {
         var1++;
      }

      if (this.stringBuilder != null) {
         var1++;
      }

      if (this.charsetEncoderCache != null) {
         var1++;
      }

      if (this.charsetDecoderCache != null) {
         var1++;
      }

      if (this.arrayList != null) {
         var1++;
      }

      for (Object var5 : this.indexedVariables) {
         if (var5 != UNSET) {
            var1++;
         }
      }

      return var1 - 1;
   }

   public StringBuilder stringBuilder() {
      StringBuilder var1 = this.stringBuilder;
      if (var1 == null) {
         return this.stringBuilder = new StringBuilder(STRING_BUILDER_INITIAL_SIZE);
      } else {
         if (var1.capacity() > STRING_BUILDER_MAX_SIZE) {
            var1.setLength(STRING_BUILDER_INITIAL_SIZE);
            var1.trimToSize();
         }

         var1.setLength(0);
         return var1;
      }
   }

   public Map<Charset, CharsetEncoder> charsetEncoderCache() {
      Object var1 = this.charsetEncoderCache;
      if (var1 == null) {
         this.charsetEncoderCache = (Map<Charset, CharsetEncoder>)(var1 = new IdentityHashMap());
      }

      return (Map<Charset, CharsetEncoder>)var1;
   }

   public Map<Charset, CharsetDecoder> charsetDecoderCache() {
      Object var1 = this.charsetDecoderCache;
      if (var1 == null) {
         this.charsetDecoderCache = (Map<Charset, CharsetDecoder>)(var1 = new IdentityHashMap());
      }

      return (Map<Charset, CharsetDecoder>)var1;
   }

   public <E> ArrayList<E> arrayList() {
      return this.arrayList(8);
   }

   public <E> ArrayList<E> arrayList(int var1) {
      ArrayList var2 = this.arrayList;
      if (var2 == null) {
         this.arrayList = new ArrayList<>(var1);
         return (ArrayList<E>)this.arrayList;
      } else {
         var2.clear();
         var2.ensureCapacity(var1);
         return var2;
      }
   }

   public int futureListenerStackDepth() {
      return this.futureListenerStackDepth;
   }

   public void setFutureListenerStackDepth(int var1) {
      this.futureListenerStackDepth = var1;
   }

   public ThreadLocalRandom random() {
      ThreadLocalRandom var1 = this.random;
      if (var1 == null) {
         this.random = var1 = new ThreadLocalRandom();
      }

      return var1;
   }

   public Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache() {
      Object var1 = this.typeParameterMatcherGetCache;
      if (var1 == null) {
         this.typeParameterMatcherGetCache = (Map<Class<?>, TypeParameterMatcher>)(var1 = new IdentityHashMap());
      }

      return (Map<Class<?>, TypeParameterMatcher>)var1;
   }

   public Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache() {
      Object var1 = this.typeParameterMatcherFindCache;
      if (var1 == null) {
         this.typeParameterMatcherFindCache = (Map<Class<?>, Map<String, TypeParameterMatcher>>)(var1 = new IdentityHashMap());
      }

      return (Map<Class<?>, Map<String, TypeParameterMatcher>>)var1;
   }

   @Deprecated
   public IntegerHolder counterHashCode() {
      return this.counterHashCode;
   }

   @Deprecated
   public void setCounterHashCode(IntegerHolder var1) {
      this.counterHashCode = var1;
   }

   public Map<Class<?>, Boolean> handlerSharableCache() {
      Object var1 = this.handlerSharableCache;
      if (var1 == null) {
         this.handlerSharableCache = (Map<Class<?>, Boolean>)(var1 = new WeakHashMap(4));
      }

      return (Map<Class<?>, Boolean>)var1;
   }

   public int localChannelReaderStackDepth() {
      return this.localChannelReaderStackDepth;
   }

   public void setLocalChannelReaderStackDepth(int var1) {
      this.localChannelReaderStackDepth = var1;
   }

   public Object indexedVariable(int var1) {
      Object[] var2 = this.indexedVariables;
      return var1 < var2.length ? var2[var1] : UNSET;
   }

   public boolean setIndexedVariable(int var1, Object var2) {
      Object[] var3 = this.indexedVariables;
      if (var1 < var3.length) {
         Object var4 = var3[var1];
         var3[var1] = var2;
         return var4 == UNSET;
      } else {
         this.expandIndexedVariableTableAndSet(var1, var2);
         return true;
      }
   }

   private void expandIndexedVariableTableAndSet(int var1, Object var2) {
      Object[] var3 = this.indexedVariables;
      int var4 = var3.length;
      int var10;
      if (var1 < 1073741824) {
         var10 = var1 | var1 >>> 1;
         var10 |= var10 >>> 2;
         var10 |= var10 >>> 4;
         var10 |= var10 >>> 8;
         var10 |= var10 >>> 16;
         var10++;
      } else {
         var10 = 2147483639;
      }

      Object[] var6 = Arrays.copyOf(var3, var10);
      Arrays.fill(var6, var4, var6.length, UNSET);
      var6[var1] = var2;
      this.indexedVariables = var6;
   }

   public Object removeIndexedVariable(int var1) {
      Object[] var2 = this.indexedVariables;
      if (var1 < var2.length) {
         Object var3 = var2[var1];
         var2[var1] = UNSET;
         return var3;
      } else {
         return UNSET;
      }
   }

   public boolean isIndexedVariableSet(int var1) {
      Object[] var2 = this.indexedVariables;
      return var1 < var2.length && var2[var1] != UNSET;
   }

   public boolean isCleanerFlagSet(int var1) {
      return this.cleanerFlags != null && this.cleanerFlags.get(var1);
   }

   public void setCleanerFlag(int var1) {
      if (this.cleanerFlags == null) {
         this.cleanerFlags = new BitSet();
      }

      this.cleanerFlags.set(var1);
   }

   static {
      logger.debug("-Dio.netty.threadLocalMap.stringBuilder.initialSize: {}", STRING_BUILDER_INITIAL_SIZE);
      logger.debug("-Dio.netty.threadLocalMap.stringBuilder.maxSize: {}", STRING_BUILDER_MAX_SIZE);
   }
}
