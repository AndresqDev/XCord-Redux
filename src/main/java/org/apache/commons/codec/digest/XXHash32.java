package org.apache.commons.codec.digest;

import java.util.zip.Checksum;

public class XXHash32 implements Checksum {
   private static final int BUF_SIZE = 16;
   private static final int ROTATE_BITS = 13;
   private static final int PRIME1 = -1640531535;
   private static final int PRIME2 = -2048144777;
   private static final int PRIME3 = -1028477379;
   private static final int PRIME4 = 668265263;
   private static final int PRIME5 = 374761393;
   private final byte[] oneByte = new byte[1];
   private final int[] state = new int[4];
   private final byte[] buffer = new byte[16];
   private final int seed;
   private int totalLen;
   private int pos;

   public XXHash32() {
      this(0);
   }

   public XXHash32(int var1) {
      this.seed = var1;
      this.initializeState();
   }

   @Override
   public void reset() {
      this.initializeState();
      this.totalLen = 0;
      this.pos = 0;
   }

   @Override
   public void update(int var1) {
      this.oneByte[0] = (byte)(var1 & 0xFF);
      this.update(this.oneByte, 0, 1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (var3 > 0) {
         this.totalLen += var3;
         int var4 = var2 + var3;
         if (this.pos + var3 < 16) {
            System.arraycopy(var1, var2, this.buffer, this.pos, var3);
            this.pos += var3;
         } else {
            if (this.pos > 0) {
               int var5 = 16 - this.pos;
               System.arraycopy(var1, var2, this.buffer, this.pos, var5);
               this.process(this.buffer, 0);
               var2 += var5;
            }

            for (int var6 = var4 - 16; var2 <= var6; var2 += 16) {
               this.process(var1, var2);
            }

            if (var2 < var4) {
               this.pos = var4 - var2;
               System.arraycopy(var1, var2, this.buffer, 0, this.pos);
            }
         }
      }
   }

   @Override
   public long getValue() {
      int var1;
      if (this.totalLen > 16) {
         var1 = Integer.rotateLeft(this.state[0], 1)
            + Integer.rotateLeft(this.state[1], 7)
            + Integer.rotateLeft(this.state[2], 12)
            + Integer.rotateLeft(this.state[3], 18);
      } else {
         var1 = this.state[2] + 374761393;
      }

      var1 += this.totalLen;
      int var2 = 0;

      for (int var3 = this.pos - 4; var2 <= var3; var2 += 4) {
         var1 = Integer.rotateLeft(var1 + getInt(this.buffer, var2) * -1028477379, 17) * 668265263;
      }

      while (var2 < this.pos) {
         var1 = Integer.rotateLeft(var1 + (this.buffer[var2++] & 255) * 374761393, 11) * -1640531535;
      }

      var1 ^= var1 >>> 15;
      var1 *= -2048144777;
      var1 ^= var1 >>> 13;
      var1 *= -1028477379;
      var1 ^= var1 >>> 16;
      return (long)var1 & 4294967295L;
   }

   private static int getInt(byte[] var0, int var1) {
      return (int)(fromLittleEndian(var0, var1, 4) & 4294967295L);
   }

   private void initializeState() {
      this.state[0] = this.seed + -1640531535 + -2048144777;
      this.state[1] = this.seed + -2048144777;
      this.state[2] = this.seed;
      this.state[3] = this.seed - -1640531535;
   }

   private void process(byte[] var1, int var2) {
      int var3 = this.state[0];
      int var4 = this.state[1];
      int var5 = this.state[2];
      int var6 = this.state[3];
      var3 = Integer.rotateLeft(var3 + getInt(var1, var2) * -2048144777, 13) * -1640531535;
      var4 = Integer.rotateLeft(var4 + getInt(var1, var2 + 4) * -2048144777, 13) * -1640531535;
      var5 = Integer.rotateLeft(var5 + getInt(var1, var2 + 8) * -2048144777, 13) * -1640531535;
      var6 = Integer.rotateLeft(var6 + getInt(var1, var2 + 12) * -2048144777, 13) * -1640531535;
      this.state[0] = var3;
      this.state[1] = var4;
      this.state[2] = var5;
      this.state[3] = var6;
      this.pos = 0;
   }

   private static long fromLittleEndian(byte[] var0, int var1, int var2) {
      if (var2 > 8) {
         throw new IllegalArgumentException("can't read more than eight bytes into a long value");
      } else {
         long var3 = 0L;

         for (int var5 = 0; var5 < var2; var5++) {
            var3 |= ((long)var0[var1 + var5] & 255L) << 8 * var5;
         }

         return var3;
      }
   }
}
