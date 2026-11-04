package io.github.waterfallmc.waterfall.utils;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import java.util.UUID;

public class UUIDUtils {
   private UUIDUtils() {
   }

   public static String undash(String var0) {
      return new StringBuilder(32).append(var0, 0, 8).append(var0, 9, 13).append(var0, 14, 18).append(var0, 19, 23).append(var0, 24, 36).toString();
   }

   public static String toMojangString(UUID var0) {
      Preconditions.checkNotNull(var0, "Null id");
      return Hex.encodeString(toBytes(var0));
   }

   public static UUID fromString(String var0) {
      Preconditions.checkNotNull(var0, "Null string");
      if (var0.length() == 36) {
         var0 = undash(var0);
      } else if (var0.length() != 32) {
         throw new IllegalArgumentException("Invalid UUID: " + var0);
      }

      return fromMojangString0(var0);
   }

   public static UUID fromMojangString(String var0) {
      Preconditions.checkNotNull(var0, "Null string");
      if (var0.length() != 32) {
         throw new IllegalArgumentException("UUID not in mojang format: " + var0);
      } else {
         return fromMojangString0(var0);
      }
   }

   private static UUID fromMojangString0(String var0) {
      assert var0 != null : "Null string";

      assert var0.length() == 32 : "invalid length: " + var0;

      try {
         return fromBytes(Hex.decode(var0));
      } catch (IllegalArgumentException var2) {
         throw new IllegalArgumentException("Invalid UUID: " + var0);
      }
   }

   public static byte[] toBytes(UUID var0) {
      Preconditions.checkNotNull(var0, "Null id");
      byte[] var1 = new byte[16];
      long var2 = var0.getLeastSignificantBits();

      for (int var4 = 15; var4 >= 8; var4--) {
         var1[var4] = (byte)((int)(var2 & 255L));
         var2 >>= 8;
      }

      long var7 = var0.getMostSignificantBits();

      for (int var6 = 7; var6 >= 0; var6--) {
         var1[var6] = (byte)((int)(var7 & 255L));
         var7 >>= 8;
      }

      return var1;
   }

   public static UUID fromBytes(byte[] var0) {
      Preconditions.checkNotNull(var0, "Null bytes");
      if (var0.length != 16) {
         throw new IllegalArgumentException("Invalid length: " + var0.length);
      } else {
         long var1 = Longs.fromBytes(var0[0], var0[1], var0[2], var0[3], var0[4], var0[5], var0[6], var0[7]);
         long var3 = Longs.fromBytes(var0[8], var0[9], var0[10], var0[11], var0[12], var0[13], var0[14], var0[15]);
         return new UUID(var1, var3);
      }
   }
}
