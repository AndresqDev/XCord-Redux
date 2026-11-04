package xcord;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.MutedCorruptedFrameException;

public final class VarInts {
   private VarInts() {
   }

   private static final MutedCorruptedFrameException BAD_VARINT = new MutedCorruptedFrameException("Bad VarInt decoded");
   private static final boolean packetDecodeLogging = Boolean.getBoolean("waterfall.packet-decode-logging");
   private static final int[] VARINT_SIZES = new int[33];

   public static int readVarInt(ByteBuf var0) {
      int var1 = readVarIntRaw(var0);
      if (var1 == Integer.MIN_VALUE) {
         throw packetDecodeLogging ? new MutedCorruptedFrameException("Bad VarInt decoded") : BAD_VARINT;
      } else {
         return var1;
      }
   }

   public static int readVarIntRaw(ByteBuf var0) {
      int var1 = 0;
      int var2 = Math.min(5, var0.readableBytes());

      for (int var3 = 0; var3 < var2; var3++) {
         byte var4 = var0.readByte();
         var1 |= (var4 & 127) << var3 * 7;
         if ((var4 & 128) != 128) {
            return var1;
         }
      }

      return Integer.MIN_VALUE;
   }

   public static int varIntSize(int var0) {
      return VARINT_SIZES[Integer.numberOfLeadingZeros(var0)];
   }

   public static void writeVarInt(ByteBuf var0, int var1) {
      if ((var1 & -128) == 0) {
         var0.writeByte(var1);
      } else if ((var1 & -16384) == 0) {
         int var2 = (var1 & 127 | 128) << 8 | var1 >>> 7;
         var0.writeShort(var2);
      } else {
         writeVarIntFull(var0, var1);
      }
   }

   private static void writeVarIntFull(ByteBuf var0, int var1) {
      if ((var1 & -128) == 0) {
         var0.writeByte(var1);
      } else if ((var1 & -16384) == 0) {
         int var2 = (var1 & 127 | 128) << 8 | var1 >>> 7;
         var0.writeShort(var2);
      } else if ((var1 & -2097152) == 0) {
         int var3 = (var1 & 127 | 128) << 16 | (var1 >>> 7 & 127 | 128) << 8 | var1 >>> 14;
         var0.writeMedium(var3);
      } else if ((var1 & -268435456) == 0) {
         int var4 = (var1 & 127 | 128) << 24 | (var1 >>> 7 & 127 | 128) << 16 | (var1 >>> 14 & 127 | 128) << 8 | var1 >>> 21;
         var0.writeInt(var4);
      } else {
         int var5 = (var1 & 127 | 128) << 24 | (var1 >>> 7 & 127 | 128) << 16 | (var1 >>> 14 & 127 | 128) << 8 | var1 >>> 21 & 127 | 128;
         var0.writeInt(var5);
         var0.writeByte(var1 >>> 28);
      }
   }

   public static void writeVarInt3(ByteBuf var0, int var1) {
      int var2 = (var1 & 127 | 128) << 16 | (var1 >>> 7 & 127 | 128) << 8 | var1 >>> 14;
      var0.writeMedium(var2);
   }

   static {
      for (int var0 = 0; var0 <= 32; var0++) {
         VARINT_SIZES[var0] = (int)Math.ceil((31.0 - (double)(var0 - 1)) / 7.0);
      }

      VARINT_SIZES[32] = 1;
   }
}
