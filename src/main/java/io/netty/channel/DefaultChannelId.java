package io.netty.channel;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.MacAddressUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultChannelId implements ChannelId {
   private static final long serialVersionUID = 3884076183504074063L;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelId.class);
   private static final byte[] MACHINE_ID;
   private static final int PROCESS_ID_LEN = 4;
   private static final int PROCESS_ID;
   private static final int SEQUENCE_LEN = 4;
   private static final int TIMESTAMP_LEN = 8;
   private static final int RANDOM_LEN = 4;
   private static final AtomicInteger nextSequence = new AtomicInteger();
   private final byte[] data;
   private final int hashCode;
   private transient String shortValue;
   private transient String longValue;

   public static DefaultChannelId newInstance() {
      return new DefaultChannelId();
   }

   static int processHandlePid(ClassLoader var0) {
      byte var1 = -1;
      if (PlatformDependent.javaVersion() >= 9) {
         Long var2;
         try {
            Class var3 = Class.forName("java.lang.ProcessHandle", true, var0);
            Method var4 = var3.getMethod("current");
            Object var5 = var4.invoke(null);
            Method var6 = var3.getMethod("pid");
            var2 = (Long)var6.invoke(var5);
         } catch (Exception var7) {
            logger.debug("Could not invoke ProcessHandle.current().pid();", (Throwable)var7);
            return var1;
         }

         if (var2 <= 2147483647L && var2 >= -2147483648L) {
            return var2.intValue();
         } else {
            throw new IllegalStateException("Current process ID exceeds int range: " + var2);
         }
      } else {
         return var1;
      }
   }

   static int jmxPid(ClassLoader var0) {
      String var1;
      try {
         Class var2 = Class.forName("java.lang.management.ManagementFactory", true, var0);
         Class var11 = Class.forName("java.lang.management.RuntimeMXBean", true, var0);
         Method var13 = var2.getMethod("getRuntimeMXBean", EmptyArrays.EMPTY_CLASSES);
         Object var5 = var13.invoke(null, EmptyArrays.EMPTY_OBJECTS);
         Method var6 = var11.getMethod("getName", EmptyArrays.EMPTY_CLASSES);
         var1 = (String)var6.invoke(var5, EmptyArrays.EMPTY_OBJECTS);
      } catch (Throwable var9) {
         logger.debug("Could not invoke ManagementFactory.getRuntimeMXBean().getName(); Android?", var9);

         try {
            Class var3 = Class.forName("android.os.Process", true, var0);
            Method var4 = var3.getMethod("myPid", EmptyArrays.EMPTY_CLASSES);
            var1 = var4.invoke(null, EmptyArrays.EMPTY_OBJECTS).toString();
         } catch (Throwable var8) {
            logger.debug("Could not invoke Process.myPid(); not Android?", var8);
            var1 = "";
         }
      }

      int var10 = var1.indexOf(64);
      if (var10 >= 0) {
         var1 = var1.substring(0, var10);
      }

      int var12;
      try {
         var12 = Integer.parseInt(var1);
      } catch (NumberFormatException var7) {
         var12 = -1;
      }

      if (var12 < 0) {
         var12 = PlatformDependent.threadLocalRandom().nextInt();
         logger.warn("Failed to find the current process ID from '{}'; using a random value: {}", var1, var12);
      }

      return var12;
   }

   static int defaultProcessId() {
      ClassLoader var0 = PlatformDependent.getClassLoader(DefaultChannelId.class);
      int var1 = processHandlePid(var0);
      return var1 != -1 ? var1 : jmxPid(var0);
   }

   private DefaultChannelId() {
      this.data = new byte[MACHINE_ID.length + 4 + 4 + 8 + 4];
      int var1 = 0;
      System.arraycopy(MACHINE_ID, 0, this.data, var1, MACHINE_ID.length);
      var1 += MACHINE_ID.length;
      var1 = this.writeInt(var1, PROCESS_ID);
      var1 = this.writeInt(var1, nextSequence.getAndIncrement());
      var1 = this.writeLong(var1, Long.reverse(System.nanoTime()) ^ System.currentTimeMillis());
      int var2 = PlatformDependent.threadLocalRandom().nextInt();
      var1 = this.writeInt(var1, var2);

      assert var1 == this.data.length;

      this.hashCode = Arrays.hashCode(this.data);
   }

   private int writeInt(int var1, int var2) {
      this.data[var1++] = (byte)(var2 >>> 24);
      this.data[var1++] = (byte)(var2 >>> 16);
      this.data[var1++] = (byte)(var2 >>> 8);
      this.data[var1++] = (byte)var2;
      return var1;
   }

   private int writeLong(int var1, long var2) {
      this.data[var1++] = (byte)((int)(var2 >>> 56));
      this.data[var1++] = (byte)((int)(var2 >>> 48));
      this.data[var1++] = (byte)((int)(var2 >>> 40));
      this.data[var1++] = (byte)((int)(var2 >>> 32));
      this.data[var1++] = (byte)((int)(var2 >>> 24));
      this.data[var1++] = (byte)((int)(var2 >>> 16));
      this.data[var1++] = (byte)((int)(var2 >>> 8));
      this.data[var1++] = (byte)((int)var2);
      return var1;
   }

   @Override
   public String asShortText() {
      String var1 = this.shortValue;
      if (var1 == null) {
         this.shortValue = var1 = ByteBufUtil.hexDump(this.data, this.data.length - 4, 4);
      }

      return var1;
   }

   @Override
   public String asLongText() {
      String var1 = this.longValue;
      if (var1 == null) {
         this.longValue = var1 = this.newLongValue();
      }

      return var1;
   }

   private String newLongValue() {
      StringBuilder var1 = new StringBuilder(2 * this.data.length + 5);
      int var2 = 0;
      var2 = this.appendHexDumpField(var1, var2, MACHINE_ID.length);
      var2 = this.appendHexDumpField(var1, var2, 4);
      var2 = this.appendHexDumpField(var1, var2, 4);
      var2 = this.appendHexDumpField(var1, var2, 8);
      var2 = this.appendHexDumpField(var1, var2, 4);

      assert var2 == this.data.length;

      return var1.substring(0, var1.length() - 1);
   }

   private int appendHexDumpField(StringBuilder var1, int var2, int var3) {
      var1.append(ByteBufUtil.hexDump(this.data, var2, var3));
      var1.append('-');
      return var2 + var3;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   public int compareTo(ChannelId var1) {
      if (this == var1) {
         return 0;
      } else if (var1 instanceof DefaultChannelId) {
         byte[] var2 = ((DefaultChannelId)var1).data;
         int var3 = this.data.length;
         int var4 = var2.length;
         int var5 = Math.min(var3, var4);

         for (int var6 = 0; var6 < var5; var6++) {
            byte var7 = this.data[var6];
            byte var8 = var2[var6];
            if (var7 != var8) {
               return (var7 & 0xFF) - (var8 & 0xFF);
            }
         }

         return var3 - var4;
      } else {
         return this.asLongText().compareTo(var1.asLongText());
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DefaultChannelId)) {
         return false;
      } else {
         DefaultChannelId var2 = (DefaultChannelId)var1;
         return this.hashCode == var2.hashCode && Arrays.equals(this.data, var2.data);
      }
   }

   @Override
   public String toString() {
      return this.asShortText();
   }

   static {
      int var0 = -1;
      String var1 = SystemPropertyUtil.get("io.netty.processId");
      if (var1 != null) {
         try {
            var0 = Integer.parseInt(var1);
         } catch (NumberFormatException var6) {
         }

         if (var0 < 0) {
            var0 = -1;
            logger.warn("-Dio.netty.processId: {} (malformed)", var1);
         } else if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.processId: {} (user-set)", var0);
         }
      }

      if (var0 < 0) {
         var0 = defaultProcessId();
         if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.processId: {} (auto-detected)", var0);
         }
      }

      PROCESS_ID = var0;
      byte[] var2 = null;
      String var3 = SystemPropertyUtil.get("io.netty.machineId");
      if (var3 != null) {
         try {
            var2 = MacAddressUtil.parseMAC(var3);
         } catch (Exception var5) {
            logger.warn("-Dio.netty.machineId: {} (malformed)", var3, var5);
         }

         if (var2 != null) {
            logger.debug("-Dio.netty.machineId: {} (user-set)", var3);
         }
      }

      if (var2 == null) {
         var2 = MacAddressUtil.defaultMachineId();
         if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.machineId: {} (auto-detected)", MacAddressUtil.formatAddress(var2));
         }
      }

      MACHINE_ID = var2;
   }
}
