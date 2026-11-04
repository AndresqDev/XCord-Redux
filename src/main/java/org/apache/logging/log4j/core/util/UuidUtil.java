package org.apache.logging.log4j.core.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class UuidUtil {
   private static final long[] EMPTY_LONG_ARRAY = new long[0];
   public static final String UUID_SEQUENCE = "org.apache.logging.log4j.uuidSequence";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String ASSIGNED_SEQUENCES = "org.apache.logging.log4j.assignedSequences";
   private static final AtomicInteger COUNT = new AtomicInteger(0);
   private static final long TYPE1 = 4096L;
   private static final byte VARIANT = -128;
   private static final int SEQUENCE_MASK = 16383;
   private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 122192928000000000L;
   private static final long INITIAL_UUID_SEQNO = PropertiesUtil.getProperties().getLongProperty("org.apache.logging.log4j.uuidSequence", 0L);
   private static final long LOW_MASK = 4294967295L;
   private static final long MID_MASK = 281470681743360L;
   private static final long HIGH_MASK = 1152640029630136320L;
   private static final int NODE_SIZE = 8;
   private static final int SHIFT_2 = 16;
   private static final int SHIFT_4 = 32;
   private static final int SHIFT_6 = 48;
   private static final int HUNDRED_NANOS_PER_MILLI = 10000;
   private static final long LEAST = initialize(NetUtils.getMacAddress());

   private UuidUtil() {
   }

   static long initialize(byte[] var0) {
      SecureRandom var1 = new SecureRandom();
      if (var0 == null || var0.length == 0) {
         var0 = new byte[6];
         var1.nextBytes(var0);
      }

      int var2 = var0.length >= 6 ? 6 : var0.length;
      int var3 = var0.length >= 6 ? var0.length - 6 : 0;
      byte[] var4 = new byte[8];
      var4[0] = -128;
      var4[1] = 0;

      for (int var5 = 2; var5 < 8; var5++) {
         var4[var5] = 0;
      }

      System.arraycopy(var0, var3, var4, 2, var2);
      ByteBuffer var16 = ByteBuffer.wrap(var4);
      long var6 = INITIAL_UUID_SEQNO;
      String var8 = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.assignedSequences");
      long[] var9;
      if (var8 == null) {
         var9 = EMPTY_LONG_ARRAY;
      } else {
         String[] var10 = var8.split(Patterns.COMMA_SEPARATOR);
         var9 = new long[var10.length];
         int var11 = 0;

         for (String var15 : var10) {
            var9[var11] = Long.parseLong(var15);
            var11++;
         }
      }

      if (var6 == 0L) {
         var6 = var1.nextLong();
      }

      var6 &= 16383L;

      boolean var19;
      do {
         var19 = false;

         for (long var23 : var9) {
            if (var23 == var6) {
               var19 = true;
               break;
            }
         }

         if (var19) {
            var6 = var6 + 1L & 16383L;
         }
      } while (var19);

      var8 = var8 == null ? Long.toString(var6) : var8 + ',' + Long.toString(var6);
      System.setProperty("org.apache.logging.log4j.assignedSequences", var8);
      return var16.getLong() | var6 << 48;
   }

   public static UUID getTimeBasedUuid() {
      long var0 = System.currentTimeMillis() * 10000L + 122192928000000000L + (long)(COUNT.incrementAndGet() % 10000);
      long var2 = (var0 & 4294967295L) << 32;
      long var4 = (var0 & 281470681743360L) >> 16;
      long var6 = (var0 & 1152640029630136320L) >> 48;
      long var8 = var2 | var4 | 4096L | var6;
      return new UUID(var8, LEAST);
   }
}
