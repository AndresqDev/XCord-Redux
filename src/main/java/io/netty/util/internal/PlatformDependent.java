package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscChunkedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.SpscLinkedAtomicQueue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlatformDependent {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
   private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
   private static final boolean MAYBE_SUPER_USER;
   private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !isAndroid();
   private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE = unsafeUnavailabilityCause0();
   private static final boolean DIRECT_BUFFER_PREFERRED;
   private static final long MAX_DIRECT_MEMORY = estimateMaxDirectMemory();
   private static final int MPSC_CHUNK_SIZE = 1024;
   private static final int MIN_MAX_MPSC_CAPACITY = 2048;
   private static final int MAX_ALLOWED_MPSC_CAPACITY = 1073741824;
   private static final long BYTE_ARRAY_BASE_OFFSET = byteArrayBaseOffset0();
   private static final File TMPDIR = tmpdir0();
   private static final int BIT_MODE = bitMode0();
   private static final String NORMALIZED_ARCH = normalizeArch(SystemPropertyUtil.get("os.arch", ""));
   private static final String NORMALIZED_OS = normalizeOs(SystemPropertyUtil.get("os.name", ""));
   private static final String[] ALLOWED_LINUX_OS_CLASSIFIERS = new String[]{"fedora", "suse", "arch"};
   private static final Set<String> LINUX_OS_CLASSIFIERS;
   private static final boolean IS_WINDOWS = isWindows0();
   private static final boolean IS_OSX = isOsx0();
   private static final boolean IS_J9_JVM = isJ9Jvm0();
   private static final boolean IS_IVKVM_DOT_NET = isIkvmDotNet0();
   private static final int ADDRESS_SIZE = addressSize0();
   private static final boolean USE_DIRECT_BUFFER_NO_CLEANER;
   private static final AtomicLong DIRECT_MEMORY_COUNTER;
   private static final long DIRECT_MEMORY_LIMIT;
   private static final PlatformDependent.ThreadLocalRandomProvider RANDOM_PROVIDER;
   private static final Cleaner CLEANER;
   private static final int UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD;
   private static final String[] OS_RELEASE_FILES = new String[]{"/etc/os-release", "/usr/lib/os-release"};
   private static final String LINUX_ID_PREFIX = "ID=";
   private static final String LINUX_ID_LIKE_PREFIX = "ID_LIKE=";
   public static final boolean BIG_ENDIAN_NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
   private static final Cleaner NOOP = new Cleaner() {
      @Override
      public void freeDirectBuffer(ByteBuffer var1) {
      }
   };

   static void addFilesystemOsClassifiers(final Set<String> var0, final Set<String> var1) {
      for (final String var5 : OS_RELEASE_FILES) {
         final File var6 = new File(var5);
         boolean var7 = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
               try {
                  if (var6.exists()) {
                     BufferedReader var1x = null;

                     try {
                        var1x = new BufferedReader(new InputStreamReader(new FileInputStream(var6), CharsetUtil.UTF_8));

                        String var2;
                        while ((var2 = var1x.readLine()) != null) {
                           if (var2.startsWith("ID=")) {
                              String var3 = PlatformDependent.normalizeOsReleaseVariableValue(var2.substring("ID=".length()));
                              PlatformDependent.addClassifier(var0, var1, var3);
                           } else if (var2.startsWith("ID_LIKE=")) {
                              var2 = PlatformDependent.normalizeOsReleaseVariableValue(var2.substring("ID_LIKE=".length()));
                              PlatformDependent.addClassifier(var0, var1, var2.split("[ ]+"));
                           }
                        }
                     } catch (SecurityException var15) {
                        PlatformDependent.logger.debug("Unable to read {}", var5, var15);
                     } catch (IOException var16) {
                        PlatformDependent.logger.debug("Error while reading content of {}", var5, var16);
                     } finally {
                        if (var1x != null) {
                           try {
                              var1x.close();
                           } catch (IOException var14) {
                           }
                        }
                     }

                     return true;
                  }
               } catch (SecurityException var18) {
                  PlatformDependent.logger.debug("Unable to check if {} exists", var5, var18);
               }

               return false;
            }
         });
         if (var7) {
            break;
         }
      }
   }

   static boolean addPropertyOsClassifiers(Set<String> var0, Set<String> var1) {
      String var2 = "io.netty.osClassifiers";
      String var3 = SystemPropertyUtil.get(var2);
      if (var3 == null) {
         return false;
      } else if (var3.isEmpty()) {
         return true;
      } else {
         String[] var4 = var3.split(",");
         if (var4.length == 0) {
            throw new IllegalArgumentException(var2 + " property is not empty, but contains no classifiers: " + var3);
         } else if (var4.length > 2) {
            throw new IllegalArgumentException(var2 + " property contains more than 2 classifiers: " + var3);
         } else {
            for (String var8 : var4) {
               addClassifier(var0, var1, var8);
            }

            return true;
         }
      }
   }

   public static long byteArrayBaseOffset() {
      return BYTE_ARRAY_BASE_OFFSET;
   }

   public static boolean hasDirectBufferNoCleanerConstructor() {
      return PlatformDependent0.hasDirectBufferNoCleanerConstructor();
   }

   public static byte[] allocateUninitializedArray(int var0) {
      return UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD >= 0 && UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD <= var0
         ? PlatformDependent0.allocateUninitializedArray(var0)
         : new byte[var0];
   }

   public static boolean isAndroid() {
      return PlatformDependent0.isAndroid();
   }

   public static boolean isWindows() {
      return IS_WINDOWS;
   }

   public static boolean isOsx() {
      return IS_OSX;
   }

   public static boolean maybeSuperUser() {
      return MAYBE_SUPER_USER;
   }

   public static int javaVersion() {
      return PlatformDependent0.javaVersion();
   }

   public static boolean canEnableTcpNoDelayByDefault() {
      return CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
   }

   public static boolean hasUnsafe() {
      return UNSAFE_UNAVAILABILITY_CAUSE == null;
   }

   public static Throwable getUnsafeUnavailabilityCause() {
      return UNSAFE_UNAVAILABILITY_CAUSE;
   }

   public static boolean isUnaligned() {
      return PlatformDependent0.isUnaligned();
   }

   public static boolean directBufferPreferred() {
      return DIRECT_BUFFER_PREFERRED;
   }

   public static long maxDirectMemory() {
      return DIRECT_MEMORY_LIMIT;
   }

   public static long usedDirectMemory() {
      return DIRECT_MEMORY_COUNTER != null ? DIRECT_MEMORY_COUNTER.get() : -1L;
   }

   public static File tmpdir() {
      return TMPDIR;
   }

   public static int bitMode() {
      return BIT_MODE;
   }

   public static int addressSize() {
      return ADDRESS_SIZE;
   }

   public static long allocateMemory(long var0) {
      return PlatformDependent0.allocateMemory(var0);
   }

   public static void freeMemory(long var0) {
      PlatformDependent0.freeMemory(var0);
   }

   public static long reallocateMemory(long var0, long var2) {
      return PlatformDependent0.reallocateMemory(var0, var2);
   }

   public static void throwException(Throwable var0) {
      if (hasUnsafe()) {
         PlatformDependent0.throwException(var0);
      } else {
         throwException0(var0);
      }
   }

   private static <E extends Throwable> void throwException0(Throwable var0) throws E {
      throw var0;
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
      return new ConcurrentHashMap<>();
   }

   public static LongCounter newLongCounter() {
      return (LongCounter)(javaVersion() >= 8 ? new LongAdderCounter() : new PlatformDependent.AtomicLongCounter());
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int var0) {
      return new ConcurrentHashMap<>(var0);
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int var0, float var1) {
      return new ConcurrentHashMap<>(var0, var1);
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int var0, float var1, int var2) {
      return new ConcurrentHashMap<>(var0, var1, var2);
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> var0) {
      return new ConcurrentHashMap<>(var0);
   }

   public static void freeDirectBuffer(ByteBuffer var0) {
      CLEANER.freeDirectBuffer(var0);
   }

   public static long directBufferAddress(ByteBuffer var0) {
      return PlatformDependent0.directBufferAddress(var0);
   }

   public static ByteBuffer directBuffer(long var0, int var2) {
      if (PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
         return PlatformDependent0.newDirectBuffer(var0, var2);
      } else {
         throw new UnsupportedOperationException("sun.misc.Unsafe or java.nio.DirectByteBuffer.<init>(long, int) not available");
      }
   }

   public static Object getObject(Object var0, long var1) {
      return PlatformDependent0.getObject(var0, var1);
   }

   public static int getInt(Object var0, long var1) {
      return PlatformDependent0.getInt(var0, var1);
   }

   static void safeConstructPutInt(Object var0, long var1, int var3) {
      PlatformDependent0.safeConstructPutInt(var0, var1, var3);
   }

   public static int getIntVolatile(long var0) {
      return PlatformDependent0.getIntVolatile(var0);
   }

   public static void putIntOrdered(long var0, int var2) {
      PlatformDependent0.putIntOrdered(var0, var2);
   }

   public static byte getByte(long var0) {
      return PlatformDependent0.getByte(var0);
   }

   public static short getShort(long var0) {
      return PlatformDependent0.getShort(var0);
   }

   public static int getInt(long var0) {
      return PlatformDependent0.getInt(var0);
   }

   public static long getLong(long var0) {
      return PlatformDependent0.getLong(var0);
   }

   public static byte getByte(byte[] var0, int var1) {
      return PlatformDependent0.getByte(var0, var1);
   }

   public static byte getByte(byte[] var0, long var1) {
      return PlatformDependent0.getByte(var0, var1);
   }

   public static short getShort(byte[] var0, int var1) {
      return PlatformDependent0.getShort(var0, var1);
   }

   public static int getInt(byte[] var0, int var1) {
      return PlatformDependent0.getInt(var0, var1);
   }

   public static int getInt(int[] var0, long var1) {
      return PlatformDependent0.getInt(var0, var1);
   }

   public static long getLong(byte[] var0, int var1) {
      return PlatformDependent0.getLong(var0, var1);
   }

   public static long getLong(long[] var0, long var1) {
      return PlatformDependent0.getLong(var0, var1);
   }

   private static long getLongSafe(byte[] var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER
         ? (long)var0[var1] << 56
            | ((long)var0[var1 + 1] & 255L) << 48
            | ((long)var0[var1 + 2] & 255L) << 40
            | ((long)var0[var1 + 3] & 255L) << 32
            | ((long)var0[var1 + 4] & 255L) << 24
            | ((long)var0[var1 + 5] & 255L) << 16
            | ((long)var0[var1 + 6] & 255L) << 8
            | (long)var0[var1 + 7] & 255L
         : (long)var0[var1] & 255L
            | ((long)var0[var1 + 1] & 255L) << 8
            | ((long)var0[var1 + 2] & 255L) << 16
            | ((long)var0[var1 + 3] & 255L) << 24
            | ((long)var0[var1 + 4] & 255L) << 32
            | ((long)var0[var1 + 5] & 255L) << 40
            | ((long)var0[var1 + 6] & 255L) << 48
            | (long)var0[var1 + 7] << 56;
   }

   private static int getIntSafe(byte[] var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER
         ? var0[var1] << 24 | (var0[var1 + 1] & 0xFF) << 16 | (var0[var1 + 2] & 0xFF) << 8 | var0[var1 + 3] & 0xFF
         : var0[var1] & 0xFF | (var0[var1 + 1] & 0xFF) << 8 | (var0[var1 + 2] & 0xFF) << 16 | var0[var1 + 3] << 24;
   }

   private static short getShortSafe(byte[] var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER ? (short)(var0[var1] << 8 | var0[var1 + 1] & 255) : (short)(var0[var1] & 255 | var0[var1 + 1] << 8);
   }

   private static int hashCodeAsciiCompute(CharSequence var0, int var1, int var2) {
      return BIG_ENDIAN_NATIVE_ORDER
         ? var2 * -862048943 + hashCodeAsciiSanitizeInt(var0, var1 + 4) * 461845907 + hashCodeAsciiSanitizeInt(var0, var1)
         : var2 * -862048943 + hashCodeAsciiSanitizeInt(var0, var1) * 461845907 + hashCodeAsciiSanitizeInt(var0, var1 + 4);
   }

   private static int hashCodeAsciiSanitizeInt(CharSequence var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER
         ? var0.charAt(var1 + 3) & 31 | (var0.charAt(var1 + 2) & 31) << 8 | (var0.charAt(var1 + 1) & 31) << 16 | (var0.charAt(var1) & 31) << 24
         : (var0.charAt(var1 + 3) & 31) << 24 | (var0.charAt(var1 + 2) & 31) << 16 | (var0.charAt(var1 + 1) & 31) << 8 | var0.charAt(var1) & 31;
   }

   private static int hashCodeAsciiSanitizeShort(CharSequence var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER ? var0.charAt(var1 + 1) & 31 | (var0.charAt(var1) & 31) << 8 : (var0.charAt(var1 + 1) & 31) << 8 | var0.charAt(var1) & 31;
   }

   private static int hashCodeAsciiSanitizeByte(char var0) {
      return var0 & 31;
   }

   public static void putByte(long var0, byte var2) {
      PlatformDependent0.putByte(var0, var2);
   }

   public static void putShort(long var0, short var2) {
      PlatformDependent0.putShort(var0, var2);
   }

   public static void putInt(long var0, int var2) {
      PlatformDependent0.putInt(var0, var2);
   }

   public static void putLong(long var0, long var2) {
      PlatformDependent0.putLong(var0, var2);
   }

   public static void putByte(byte[] var0, int var1, byte var2) {
      PlatformDependent0.putByte(var0, var1, var2);
   }

   public static void putByte(Object var0, long var1, byte var3) {
      PlatformDependent0.putByte(var0, var1, var3);
   }

   public static void putShort(byte[] var0, int var1, short var2) {
      PlatformDependent0.putShort(var0, var1, var2);
   }

   public static void putInt(byte[] var0, int var1, int var2) {
      PlatformDependent0.putInt(var0, var1, var2);
   }

   public static void putLong(byte[] var0, int var1, long var2) {
      PlatformDependent0.putLong(var0, var1, var2);
   }

   public static void putObject(Object var0, long var1, Object var3) {
      PlatformDependent0.putObject(var0, var1, var3);
   }

   public static long objectFieldOffset(Field var0) {
      return PlatformDependent0.objectFieldOffset(var0);
   }

   public static void copyMemory(long var0, long var2, long var4) {
      PlatformDependent0.copyMemory(var0, var2, var4);
   }

   public static void copyMemory(byte[] var0, int var1, long var2, long var4) {
      PlatformDependent0.copyMemory(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, null, var2, var4);
   }

   public static void copyMemory(byte[] var0, int var1, byte[] var2, int var3, long var4) {
      PlatformDependent0.copyMemory(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2, BYTE_ARRAY_BASE_OFFSET + (long)var3, var4);
   }

   public static void copyMemory(long var0, byte[] var2, int var3, long var4) {
      PlatformDependent0.copyMemory(null, var0, var2, BYTE_ARRAY_BASE_OFFSET + (long)var3, var4);
   }

   public static void setMemory(byte[] var0, int var1, long var2, byte var4) {
      PlatformDependent0.setMemory(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2, var4);
   }

   public static void setMemory(long var0, long var2, byte var4) {
      PlatformDependent0.setMemory(var0, var2, var4);
   }

   public static ByteBuffer allocateDirectNoCleaner(int var0) {
      assert USE_DIRECT_BUFFER_NO_CLEANER;

      incrementMemoryCounter(var0);

      try {
         return PlatformDependent0.allocateDirectNoCleaner(var0);
      } catch (Throwable var2) {
         decrementMemoryCounter(var0);
         throwException(var2);
         return null;
      }
   }

   public static ByteBuffer reallocateDirectNoCleaner(ByteBuffer var0, int var1) {
      assert USE_DIRECT_BUFFER_NO_CLEANER;

      int var2 = var1 - var0.capacity();
      incrementMemoryCounter(var2);

      try {
         return PlatformDependent0.reallocateDirectNoCleaner(var0, var1);
      } catch (Throwable var4) {
         decrementMemoryCounter(var2);
         throwException(var4);
         return null;
      }
   }

   public static void freeDirectNoCleaner(ByteBuffer var0) {
      assert USE_DIRECT_BUFFER_NO_CLEANER;

      int var1 = var0.capacity();
      PlatformDependent0.freeMemory(PlatformDependent0.directBufferAddress(var0));
      decrementMemoryCounter(var1);
   }

   public static boolean hasAlignDirectByteBuffer() {
      return hasUnsafe() || PlatformDependent0.hasAlignSliceMethod();
   }

   public static ByteBuffer alignDirectBuffer(ByteBuffer var0, int var1) {
      if (!var0.isDirect()) {
         throw new IllegalArgumentException("Cannot get aligned slice of non-direct byte buffer.");
      } else if (PlatformDependent0.hasAlignSliceMethod()) {
         return PlatformDependent0.alignSlice(var0, var1);
      } else if (hasUnsafe()) {
         long var2 = directBufferAddress(var0);
         long var4 = align(var2, var1);
         ((Buffer)var0).position((int)(var4 - var2));
         return var0.slice();
      } else {
         throw new UnsupportedOperationException("Cannot align direct buffer. Needs either Unsafe or ByteBuffer.alignSlice method available.");
      }
   }

   public static long align(long var0, int var2) {
      return Pow2.align(var0, var2);
   }

   private static void incrementMemoryCounter(int var0) {
      if (DIRECT_MEMORY_COUNTER != null) {
         long var1 = DIRECT_MEMORY_COUNTER.addAndGet((long)var0);
         if (var1 > DIRECT_MEMORY_LIMIT) {
            DIRECT_MEMORY_COUNTER.addAndGet((long)(-var0));
            throw new OutOfDirectMemoryError(
               "failed to allocate " + var0 + " byte(s) of direct memory (used: " + (var1 - (long)var0) + ", max: " + DIRECT_MEMORY_LIMIT + ')'
            );
         }
      }
   }

   private static void decrementMemoryCounter(int var0) {
      if (DIRECT_MEMORY_COUNTER != null) {
         long var1 = DIRECT_MEMORY_COUNTER.addAndGet((long)(-var0));

         assert var1 >= 0L;
      }
   }

   public static boolean useDirectBufferNoCleaner() {
      return USE_DIRECT_BUFFER_NO_CLEANER;
   }

   public static boolean equals(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess()
         ? PlatformDependent0.equals(var0, var1, var2, var3, var4)
         : equalsSafe(var0, var1, var2, var3, var4);
   }

   public static boolean isZero(byte[] var0, int var1, int var2) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess() ? PlatformDependent0.isZero(var0, var1, var2) : isZeroSafe(var0, var1, var2);
   }

   public static int equalsConstantTime(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess()
         ? PlatformDependent0.equalsConstantTime(var0, var1, var2, var3, var4)
         : ConstantTimeUtils.equalsConstantTime(var0, var1, var2, var3, var4);
   }

   public static int hashCodeAscii(byte[] var0, int var1, int var2) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess() ? PlatformDependent0.hashCodeAscii(var0, var1, var2) : hashCodeAsciiSafe(var0, var1, var2);
   }

   public static int hashCodeAscii(CharSequence var0) {
      int var1 = var0.length();
      int var2 = var1 & 7;
      int var3 = -1028477387;
      if (var1 >= 32) {
         for (int var4 = var1 - 8; var4 >= var2; var4 -= 8) {
            var3 = hashCodeAsciiCompute(var0, var4, var3);
         }
      } else if (var1 >= 8) {
         var3 = hashCodeAsciiCompute(var0, var1 - 8, var3);
         if (var1 >= 16) {
            var3 = hashCodeAsciiCompute(var0, var1 - 16, var3);
            if (var1 >= 24) {
               var3 = hashCodeAsciiCompute(var0, var1 - 24, var3);
            }
         }
      }

      if (var2 == 0) {
         return var3;
      } else {
         byte var5 = 0;
         if (var2 != 2 & var2 != 4 & var2 != 6) {
            var3 = var3 * -862048943 + hashCodeAsciiSanitizeByte(var0.charAt(0));
            var5 = 1;
         }

         if (var2 != 1 & var2 != 4 & var2 != 5) {
            var3 = var3 * (var5 == 0 ? -862048943 : 461845907) + PlatformDependent0.hashCodeAsciiSanitize(hashCodeAsciiSanitizeShort(var0, var5));
            var5 += 2;
         }

         return var2 >= 4 ? var3 * (var5 == 0 | var5 == 3 ? -862048943 : 461845907) + hashCodeAsciiSanitizeInt(var0, var5) : var3;
      }
   }

   public static <T> Queue<T> newMpscQueue() {
      return PlatformDependent.Mpsc.newMpscQueue();
   }

   public static <T> Queue<T> newMpscQueue(int var0) {
      return PlatformDependent.Mpsc.newMpscQueue(var0);
   }

   public static <T> Queue<T> newMpscQueue(int var0, int var1) {
      return PlatformDependent.Mpsc.newChunkedMpscQueue(var0, var1);
   }

   public static <T> Queue<T> newSpscQueue() {
      return (Queue<T>)(hasUnsafe() ? new SpscLinkedQueue<>() : new SpscLinkedAtomicQueue<>());
   }

   public static <T> Queue<T> newFixedMpscQueue(int var0) {
      return (Queue<T>)(hasUnsafe() ? new MpscArrayQueue<>(var0) : new MpscAtomicArrayQueue<>(var0));
   }

   public static ClassLoader getClassLoader(Class<?> var0) {
      return PlatformDependent0.getClassLoader(var0);
   }

   public static ClassLoader getContextClassLoader() {
      return PlatformDependent0.getContextClassLoader();
   }

   public static ClassLoader getSystemClassLoader() {
      return PlatformDependent0.getSystemClassLoader();
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   public static <C> Deque<C> newConcurrentDeque() {
      return (Deque<C>)(javaVersion() < 7 ? new LinkedBlockingDeque<>() : new ConcurrentLinkedDeque<>());
   }

   public static Random threadLocalRandom() {
      return RANDOM_PROVIDER.current();
   }

   private static boolean isWindows0() {
      boolean var0 = "windows".equals(NORMALIZED_OS);
      if (var0) {
         logger.debug("Platform: Windows");
      }

      return var0;
   }

   private static boolean isOsx0() {
      boolean var0 = "osx".equals(NORMALIZED_OS);
      if (var0) {
         logger.debug("Platform: MacOS");
      }

      return var0;
   }

   private static boolean maybeSuperUser0() {
      String var0 = SystemPropertyUtil.get("user.name");
      return isWindows() ? "Administrator".equals(var0) : "root".equals(var0) || "toor".equals(var0);
   }

   private static Throwable unsafeUnavailabilityCause0() {
      if (isAndroid()) {
         logger.debug("sun.misc.Unsafe: unavailable (Android)");
         return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (Android)");
      } else if (isIkvmDotNet()) {
         logger.debug("sun.misc.Unsafe: unavailable (IKVM.NET)");
         return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (IKVM.NET)");
      } else {
         Throwable var0 = PlatformDependent0.getUnsafeUnavailabilityCause();
         if (var0 != null) {
            return var0;
         } else {
            try {
               boolean var1 = PlatformDependent0.hasUnsafe();
               logger.debug("sun.misc.Unsafe: {}", var1 ? "available" : "unavailable");
               return var1 ? null : PlatformDependent0.getUnsafeUnavailabilityCause();
            } catch (Throwable var2) {
               logger.trace("Could not determine if Unsafe is available", var2);
               return new UnsupportedOperationException("Could not determine if Unsafe is available", var2);
            }
         }
      }
   }

   public static boolean isJ9Jvm() {
      return IS_J9_JVM;
   }

   private static boolean isJ9Jvm0() {
      String var0 = SystemPropertyUtil.get("java.vm.name", "").toLowerCase();
      return var0.startsWith("ibm j9") || var0.startsWith("eclipse openj9");
   }

   public static boolean isIkvmDotNet() {
      return IS_IVKVM_DOT_NET;
   }

   private static boolean isIkvmDotNet0() {
      String var0 = SystemPropertyUtil.get("java.vm.name", "").toUpperCase(Locale.US);
      return var0.equals("IKVM.NET");
   }

   public static long estimateMaxDirectMemory() {
      long var0 = 0L;
      ClassLoader var2 = null;

      try {
         var2 = getSystemClassLoader();
         String var3 = SystemPropertyUtil.get("java.vm.name", "").toLowerCase();
         if (!var3.startsWith("ibm j9") && !var3.startsWith("eclipse openj9")) {
            Class var4 = Class.forName("sun.misc.VM", true, var2);
            Method var5 = var4.getDeclaredMethod("maxDirectMemory");
            var0 = ((Number)var5.invoke(null)).longValue();
         }
      } catch (Throwable var9) {
      }

      if (var0 > 0L) {
         return var0;
      } else {
         try {
            Class var11 = Class.forName("java.lang.management.ManagementFactory", true, var2);
            Class var12 = Class.forName("java.lang.management.RuntimeMXBean", true, var2);
            Object var13 = var11.getDeclaredMethod("getRuntimeMXBean").invoke(null);
            List var6 = (List)var12.getDeclaredMethod("getInputArguments").invoke(var13);

            label44:
            for (int var7 = var6.size() - 1; var7 >= 0; var7--) {
               Matcher var8 = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher((CharSequence)var6.get(var7));
               if (var8.matches()) {
                  var0 = Long.parseLong(var8.group(1));
                  switch (var8.group(2).charAt(0)) {
                     case 'G':
                     case 'g':
                        var0 *= 1073741824L;
                        break label44;
                     case 'K':
                     case 'k':
                        var0 *= 1024L;
                        break label44;
                     case 'M':
                     case 'm':
                        var0 *= 1048576L;
                     default:
                        break label44;
                  }
               }
            }
         } catch (Throwable var10) {
         }

         if (var0 <= 0L) {
            var0 = Runtime.getRuntime().maxMemory();
            logger.debug("maxDirectMemory: {} bytes (maybe)", var0);
         } else {
            logger.debug("maxDirectMemory: {} bytes", var0);
         }

         return var0;
      }
   }

   private static File tmpdir0() {
      try {
         File var0 = toDirectory(SystemPropertyUtil.get("io.netty.tmpdir"));
         if (var0 != null) {
            logger.debug("-Dio.netty.tmpdir: {}", var0);
            return var0;
         }

         var0 = toDirectory(SystemPropertyUtil.get("java.io.tmpdir"));
         if (var0 != null) {
            logger.debug("-Dio.netty.tmpdir: {} (java.io.tmpdir)", var0);
            return var0;
         }

         if (isWindows()) {
            var0 = toDirectory(System.getenv("TEMP"));
            if (var0 != null) {
               logger.debug("-Dio.netty.tmpdir: {} (%TEMP%)", var0);
               return var0;
            }

            String var1 = System.getenv("USERPROFILE");
            if (var1 != null) {
               var0 = toDirectory(var1 + "\\AppData\\Local\\Temp");
               if (var0 != null) {
                  logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\AppData\\Local\\Temp)", var0);
                  return var0;
               }

               var0 = toDirectory(var1 + "\\Local Settings\\Temp");
               if (var0 != null) {
                  logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\Local Settings\\Temp)", var0);
                  return var0;
               }
            }
         } else {
            var0 = toDirectory(System.getenv("TMPDIR"));
            if (var0 != null) {
               logger.debug("-Dio.netty.tmpdir: {} ($TMPDIR)", var0);
               return var0;
            }
         }
      } catch (Throwable var2) {
      }

      File var8;
      if (isWindows()) {
         var8 = new File("C:\\Windows\\Temp");
      } else {
         var8 = new File("/tmp");
      }

      logger.warn("Failed to get the temporary directory; falling back to: {}", var8);
      return var8;
   }

   private static File toDirectory(String var0) {
      if (var0 == null) {
         return null;
      } else {
         File var1 = new File(var0);
         var1.mkdirs();
         if (!var1.isDirectory()) {
            return null;
         } else {
            try {
               return var1.getAbsoluteFile();
            } catch (Exception var3) {
               return var1;
            }
         }
      }
   }

   private static int bitMode0() {
      int var0 = SystemPropertyUtil.getInt("io.netty.bitMode", 0);
      if (var0 > 0) {
         logger.debug("-Dio.netty.bitMode: {}", var0);
         return var0;
      } else {
         var0 = SystemPropertyUtil.getInt("sun.arch.data.model", 0);
         if (var0 > 0) {
            logger.debug("-Dio.netty.bitMode: {} (sun.arch.data.model)", var0);
            return var0;
         } else {
            var0 = SystemPropertyUtil.getInt("com.ibm.vm.bitmode", 0);
            if (var0 > 0) {
               logger.debug("-Dio.netty.bitMode: {} (com.ibm.vm.bitmode)", var0);
               return var0;
            } else {
               String var1 = SystemPropertyUtil.get("os.arch", "").toLowerCase(Locale.US).trim();
               if ("amd64".equals(var1) || "x86_64".equals(var1)) {
                  var0 = 64;
               } else if ("i386".equals(var1) || "i486".equals(var1) || "i586".equals(var1) || "i686".equals(var1)) {
                  var0 = 32;
               }

               if (var0 > 0) {
                  logger.debug("-Dio.netty.bitMode: {} (os.arch: {})", var0, var1);
               }

               String var2 = SystemPropertyUtil.get("java.vm.name", "").toLowerCase(Locale.US);
               Pattern var3 = Pattern.compile("([1-9][0-9]+)-?bit");
               Matcher var4 = var3.matcher(var2);
               return var4.find() ? Integer.parseInt(var4.group(1)) : 64;
            }
         }
      }
   }

   private static int addressSize0() {
      return !hasUnsafe() ? -1 : PlatformDependent0.addressSize();
   }

   private static long byteArrayBaseOffset0() {
      return !hasUnsafe() ? -1L : PlatformDependent0.byteArrayBaseOffset();
   }

   private static boolean equalsSafe(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      int var5 = var1 + var4;

      while (var1 < var5) {
         if (var0[var1] != var2[var3]) {
            return false;
         }

         var1++;
         var3++;
      }

      return true;
   }

   private static boolean isZeroSafe(byte[] var0, int var1, int var2) {
      for (int var3 = var1 + var2; var1 < var3; var1++) {
         if (var0[var1] != 0) {
            return false;
         }
      }

      return true;
   }

   static int hashCodeAsciiSafe(byte[] var0, int var1, int var2) {
      int var3 = -1028477387;
      int var4 = var2 & 7;
      int var5 = var1 + var4;

      for (int var6 = var1 - 8 + var2; var6 >= var5; var6 -= 8) {
         var3 = PlatformDependent0.hashCodeAsciiCompute(getLongSafe(var0, var6), var3);
      }

      switch (var4) {
         case 1:
            return var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1]);
         case 2:
            return var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1));
         case 3:
            return (var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1])) * 461845907
               + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1 + 1));
         case 4:
            return var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1));
         case 5:
            return (var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1])) * 461845907
               + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1 + 1));
         case 6:
            return (var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1))) * 461845907
               + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1 + 2));
         case 7:
            return (
                     (var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1])) * 461845907
                        + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1 + 1))
                  )
                  * -862048943
               + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1 + 3));
         default:
            return var3;
      }
   }

   public static String normalizedArch() {
      return NORMALIZED_ARCH;
   }

   public static String normalizedOs() {
      return NORMALIZED_OS;
   }

   public static Set<String> normalizedLinuxClassifiers() {
      return LINUX_OS_CLASSIFIERS;
   }

   @SuppressJava6Requirement(
      reason = "Guarded by version check"
   )
   public static File createTempFile(String var0, String var1, File var2) throws IOException {
      if (javaVersion() >= 7) {
         return var2 == null ? Files.createTempFile(var0, var1).toFile() : Files.createTempFile(var2.toPath(), var0, var1).toFile();
      } else {
         File var3;
         if (var2 == null) {
            var3 = File.createTempFile(var0, var1);
         } else {
            var3 = File.createTempFile(var0, var1, var2);
         }

         if (!var3.setReadable(false, false)) {
            throw new IOException("Failed to set permissions on temporary file " + var3);
         } else if (!var3.setReadable(true, true)) {
            throw new IOException("Failed to set permissions on temporary file " + var3);
         } else {
            return var3;
         }
      }
   }

   private static void addClassifier(Set<String> var0, Set<String> var1, String... var2) {
      for (String var6 : var2) {
         if (var0.contains(var6)) {
            var1.add(var6);
         }
      }
   }

   private static String normalizeOsReleaseVariableValue(String var0) {
      return var0.trim().replaceAll("[\"']", "");
   }

   private static String normalize(String var0) {
      return var0.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
   }

   private static String normalizeArch(String var0) {
      var0 = normalize(var0);
      if (var0.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
         return "x86_64";
      } else if (var0.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
         return "x86_32";
      } else if (var0.matches("^(ia64|itanium64)$")) {
         return "itanium_64";
      } else if (var0.matches("^(sparc|sparc32)$")) {
         return "sparc_32";
      } else if (var0.matches("^(sparcv9|sparc64)$")) {
         return "sparc_64";
      } else if (var0.matches("^(arm|arm32)$")) {
         return "arm_32";
      } else if ("aarch64".equals(var0)) {
         return "aarch_64";
      } else if (var0.matches("^(ppc|ppc32)$")) {
         return "ppc_32";
      } else if ("ppc64".equals(var0)) {
         return "ppc_64";
      } else if ("ppc64le".equals(var0)) {
         return "ppcle_64";
      } else if ("s390".equals(var0)) {
         return "s390_32";
      } else if ("s390x".equals(var0)) {
         return "s390_64";
      } else {
         return "loongarch64".equals(var0) ? "loongarch_64" : "unknown";
      }
   }

   private static String normalizeOs(String var0) {
      var0 = normalize(var0);
      if (var0.startsWith("aix")) {
         return "aix";
      } else if (var0.startsWith("hpux")) {
         return "hpux";
      } else if (!var0.startsWith("os400") || var0.length() > 5 && Character.isDigit(var0.charAt(5))) {
         if (var0.startsWith("linux")) {
            return "linux";
         } else if (var0.startsWith("macosx") || var0.startsWith("osx") || var0.startsWith("darwin")) {
            return "osx";
         } else if (var0.startsWith("freebsd")) {
            return "freebsd";
         } else if (var0.startsWith("openbsd")) {
            return "openbsd";
         } else if (var0.startsWith("netbsd")) {
            return "netbsd";
         } else if (var0.startsWith("solaris") || var0.startsWith("sunos")) {
            return "sunos";
         } else {
            return var0.startsWith("windows") ? "windows" : "unknown";
         }
      } else {
         return "os400";
      }
   }

   private PlatformDependent() {
   }

   static {
      if (javaVersion() >= 7) {
         RANDOM_PROVIDER = new PlatformDependent.ThreadLocalRandomProvider() {
            @SuppressJava6Requirement(
               reason = "Usage guarded by java version check"
            )
            @Override
            public Random current() {
               return java.util.concurrent.ThreadLocalRandom.current();
            }
         };
      } else {
         RANDOM_PROVIDER = new PlatformDependent.ThreadLocalRandomProvider() {
            @Override
            public Random current() {
               return ThreadLocalRandom.current();
            }
         };
      }

      long var0 = SystemPropertyUtil.getLong("io.netty.maxDirectMemory", -1L);
      if (var0 != 0L && hasUnsafe() && PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
         USE_DIRECT_BUFFER_NO_CLEANER = true;
         if (var0 < 0L) {
            var0 = MAX_DIRECT_MEMORY;
            if (var0 <= 0L) {
               DIRECT_MEMORY_COUNTER = null;
            } else {
               DIRECT_MEMORY_COUNTER = new AtomicLong();
            }
         } else {
            DIRECT_MEMORY_COUNTER = new AtomicLong();
         }
      } else {
         USE_DIRECT_BUFFER_NO_CLEANER = false;
         DIRECT_MEMORY_COUNTER = null;
      }

      logger.debug("-Dio.netty.maxDirectMemory: {} bytes", var0);
      DIRECT_MEMORY_LIMIT = var0 >= 1L ? var0 : MAX_DIRECT_MEMORY;
      int var2 = SystemPropertyUtil.getInt("io.netty.uninitializedArrayAllocationThreshold", 1024);
      UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD = javaVersion() >= 9 && PlatformDependent0.hasAllocateArrayMethod() ? var2 : -1;
      logger.debug("-Dio.netty.uninitializedArrayAllocationThreshold: {}", UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD);
      MAYBE_SUPER_USER = maybeSuperUser0();
      if (!isAndroid()) {
         if (javaVersion() >= 9) {
            CLEANER = (Cleaner)(CleanerJava9.isSupported() ? new CleanerJava9() : NOOP);
         } else {
            CLEANER = (Cleaner)(CleanerJava6.isSupported() ? new CleanerJava6() : NOOP);
         }
      } else {
         CLEANER = NOOP;
      }

      DIRECT_BUFFER_PREFERRED = CLEANER != NOOP && !SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false);
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noPreferDirect: {}", !DIRECT_BUFFER_PREFERRED);
      }

      if (CLEANER == NOOP && !PlatformDependent0.isExplicitNoUnsafe()) {
         logger.info(
            "Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system instability."
         );
      }

      Set var3 = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ALLOWED_LINUX_OS_CLASSIFIERS)));
      LinkedHashSet var4 = new LinkedHashSet();
      if (!addPropertyOsClassifiers(var3, var4)) {
         addFilesystemOsClassifiers(var3, var4);
      }

      LINUX_OS_CLASSIFIERS = Collections.unmodifiableSet(var4);
   }

   private static final class AtomicLongCounter extends AtomicLong implements LongCounter {
      private static final long serialVersionUID = 4074772784610639305L;

      private AtomicLongCounter() {
      }

      @Override
      public void add(long var1) {
         this.addAndGet(var1);
      }

      @Override
      public void increment() {
         this.incrementAndGet();
      }

      @Override
      public void decrement() {
         this.decrementAndGet();
      }

      @Override
      public long value() {
         return this.get();
      }
   }

   private static final class Mpsc {
      private static final boolean USE_MPSC_CHUNKED_ARRAY_QUEUE;

      static <T> Queue<T> newMpscQueue(int var0) {
         int var1 = Math.max(Math.min(var0, 1073741824), 2048);
         return newChunkedMpscQueue(1024, var1);
      }

      static <T> Queue<T> newChunkedMpscQueue(int var0, int var1) {
         return (Queue<T>)(USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscChunkedArrayQueue<>(var0, var1) : new MpscChunkedAtomicArrayQueue<>(var0, var1));
      }

      static <T> Queue<T> newMpscQueue() {
         return (Queue<T>)(USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscUnboundedArrayQueue<>(1024) : new MpscUnboundedAtomicArrayQueue<>(1024));
      }

      static {
         Object var0 = null;
         if (PlatformDependent.hasUnsafe()) {
            var0 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  return UnsafeAccess.UNSAFE;
               }
            });
         }

         if (var0 == null) {
            PlatformDependent.logger.debug("org.jctools-core.MpscChunkedArrayQueue: unavailable");
            USE_MPSC_CHUNKED_ARRAY_QUEUE = false;
         } else {
            PlatformDependent.logger.debug("org.jctools-core.MpscChunkedArrayQueue: available");
            USE_MPSC_CHUNKED_ARRAY_QUEUE = true;
         }
      }
   }

   private interface ThreadLocalRandomProvider {
      Random current();
   }
}
