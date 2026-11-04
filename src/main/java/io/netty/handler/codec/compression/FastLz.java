package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;

final class FastLz {
   private static final int MAX_DISTANCE = 8191;
   private static final int MAX_FARDISTANCE = 73725;
   private static final int HASH_LOG = 13;
   private static final int HASH_SIZE = 8192;
   private static final int HASH_MASK = 8191;
   private static final int MAX_COPY = 32;
   private static final int MAX_LEN = 264;
   private static final int MIN_RECOMENDED_LENGTH_FOR_LEVEL_2 = 65536;
   static final int MAGIC_NUMBER = 4607066;
   static final byte BLOCK_TYPE_NON_COMPRESSED = 0;
   static final byte BLOCK_TYPE_COMPRESSED = 1;
   static final byte BLOCK_WITHOUT_CHECKSUM = 0;
   static final byte BLOCK_WITH_CHECKSUM = 16;
   static final int OPTIONS_OFFSET = 3;
   static final int CHECKSUM_OFFSET = 4;
   static final int MAX_CHUNK_LENGTH = 65535;
   static final int MIN_LENGTH_TO_COMPRESSION = 32;
   static final int LEVEL_AUTO = 0;
   static final int LEVEL_1 = 1;
   static final int LEVEL_2 = 2;

   static int calculateOutputBufferLength(int var0) {
      int var1 = (int)((double)var0 * 1.06);
      return Math.max(var1, 66);
   }

   static int compress(ByteBuf var0, int var1, int var2, ByteBuf var3, int var4, int var5) {
      int var6;
      if (var5 == 0) {
         var6 = var2 < 65536 ? 1 : 2;
      } else {
         var6 = var5;
      }

      int var31 = 0;
      int var8 = var31 + var2 - 2;
      int var9 = var31 + var2 - 12;
      int var10 = 0;
      int[] var11 = new int[8192];
      if (var2 < 4) {
         if (var2 == 0) {
            return 0;
         } else {
            var3.setByte(var4 + var10++, (byte)(var2 - 1));
            var8++;

            while (var31 <= var8) {
               var3.setByte(var4 + var10++, var0.getByte(var1 + var31++));
            }

            return var2 + 1;
         }
      } else {
         for (int var12 = 0; var12 < 8192; var12++) {
            var11[var12] = var31;
         }

         int var14 = 2;
         var3.setByte(var4 + var10++, 31);
         var3.setByte(var4 + var10++, var0.getByte(var1 + var31++));
         var3.setByte(var4 + var10++, var0.getByte(var1 + var31++));

         while (var31 < var9) {
            int var57 = 0;
            long var16 = 0L;
            int var18 = 3;
            int var19 = var31;
            boolean var20 = false;
            if (var6 == 2 && var0.getByte(var1 + var31) == var0.getByte(var1 + var31 - 1) && readU16(var0, var1 + var31 - 1) == readU16(var0, var1 + var31 + 1)
               )
             {
               var16 = 1L;
               var31 += 3;
               var57 = var19 + 2;
               var20 = true;
            }

            if (!var20) {
               int var13 = hashFunction(var0, var1 + var31);
               var57 = var11[var13];
               var16 = (long)(var19 - var57);
               var11[var13] = var19;
               if (var16 == 0L
                  || (var6 == 1 ? var16 >= 8191L : var16 >= 73725L)
                  || var0.getByte(var1 + var57++) != var0.getByte(var1 + var31++)
                  || var0.getByte(var1 + var57++) != var0.getByte(var1 + var31++)
                  || var0.getByte(var1 + var57++) != var0.getByte(var1 + var31++)) {
                  var3.setByte(var4 + var10++, var0.getByte(var1 + var19++));
                  var31 = var19;
                  if (++var14 == 32) {
                     var14 = 0;
                     var3.setByte(var4 + var10++, 31);
                  }
                  continue;
               }

               if (var6 == 2 && var16 >= 8191L) {
                  if (var0.getByte(var1 + var31++) != var0.getByte(var1 + var57++) || var0.getByte(var1 + var31++) != var0.getByte(var1 + var57++)) {
                     var3.setByte(var4 + var10++, var0.getByte(var1 + var19++));
                     var31 = var19;
                     if (++var14 == 32) {
                        var14 = 0;
                        var3.setByte(var4 + var10++, 31);
                     }
                     continue;
                  }

                  var18 += 2;
               }
            }

            var31 = var19 + var18;
            if (--var16 == 0L) {
               byte var21 = var0.getByte(var1 + var31 - 1);

               while (var31 < var8 && var0.getByte(var1 + var57++) == var21) {
                  var31++;
               }
            } else {
               boolean var69 = false;

               for (int var22 = 0; var22 < 8; var22++) {
                  if (var0.getByte(var1 + var57++) != var0.getByte(var1 + var31++)) {
                     var69 = true;
                     break;
                  }
               }

               if (!var69) {
                  while (var31 < var8 && var0.getByte(var1 + var57++) == var0.getByte(var1 + var31++)) {
                  }
               }
            }

            if (var14 != 0) {
               var3.setByte(var4 + var10 - var14 - 1, (byte)(var14 - 1));
            } else {
               var10--;
            }

            var14 = 0;
            var31 -= 3;
            var18 = var31 - var19;
            if (var6 == 2) {
               if (var16 < 8191L) {
                  if (var18 < 7) {
                     var3.setByte(var4 + var10++, (byte)((int)((long)(var18 << 5) + (var16 >>> 8))));
                     var3.setByte(var4 + var10++, (byte)((int)(var16 & 255L)));
                  } else {
                     var3.setByte(var4 + var10++, (byte)((int)(224L + (var16 >>> 8))));

                     for (var18 -= 7; var18 >= 255; var18 -= 255) {
                        var3.setByte(var4 + var10++, -1);
                     }

                     var3.setByte(var4 + var10++, (byte)var18);
                     var3.setByte(var4 + var10++, (byte)((int)(var16 & 255L)));
                  }
               } else if (var18 < 7) {
                  var16 -= 8191L;
                  var3.setByte(var4 + var10++, (byte)((var18 << 5) + 31));
                  var3.setByte(var4 + var10++, -1);
                  var3.setByte(var4 + var10++, (byte)((int)(var16 >>> 8)));
                  var3.setByte(var4 + var10++, (byte)((int)(var16 & 255L)));
               } else {
                  var16 -= 8191L;
                  var3.setByte(var4 + var10++, -1);

                  for (var18 -= 7; var18 >= 255; var18 -= 255) {
                     var3.setByte(var4 + var10++, -1);
                  }

                  var3.setByte(var4 + var10++, (byte)var18);
                  var3.setByte(var4 + var10++, -1);
                  var3.setByte(var4 + var10++, (byte)((int)(var16 >>> 8)));
                  var3.setByte(var4 + var10++, (byte)((int)(var16 & 255L)));
               }
            } else {
               if (var18 > 262) {
                  while (var18 > 262) {
                     var3.setByte(var4 + var10++, (byte)((int)(224L + (var16 >>> 8))));
                     var3.setByte(var4 + var10++, -3);
                     var3.setByte(var4 + var10++, (byte)((int)(var16 & 255L)));
                     var18 -= 262;
                  }
               }

               if (var18 < 7) {
                  var3.setByte(var4 + var10++, (byte)((int)((long)(var18 << 5) + (var16 >>> 8))));
                  var3.setByte(var4 + var10++, (byte)((int)(var16 & 255L)));
               } else {
                  var3.setByte(var4 + var10++, (byte)((int)(224L + (var16 >>> 8))));
                  var3.setByte(var4 + var10++, (byte)(var18 - 7));
                  var3.setByte(var4 + var10++, (byte)((int)(var16 & 255L)));
               }
            }

            int var55 = hashFunction(var0, var1 + var31);
            var11[var55] = var31++;
            var55 = hashFunction(var0, var1 + var31);
            var11[var55] = var31++;
            var3.setByte(var4 + var10++, 31);
         }

         var8++;

         while (var31 <= var8) {
            var3.setByte(var4 + var10++, var0.getByte(var1 + var31++));
            if (++var14 == 32) {
               var14 = 0;
               var3.setByte(var4 + var10++, 31);
            }
         }

         if (var14 != 0) {
            var3.setByte(var4 + var10 - var14 - 1, (byte)(var14 - 1));
         } else {
            var10--;
         }

         if (var6 == 2) {
            var3.setByte(var4, var3.getByte(var4) | 32);
         }

         return var10;
      }
   }

   static int decompress(ByteBuf var0, int var1, int var2, ByteBuf var3, int var4, int var5) {
      int var6 = (var0.getByte(var1) >> 5) + 1;
      if (var6 != 1 && var6 != 2) {
         throw new DecompressionException(String.format("invalid level: %d (expected: %d or %d)", var6, 1, 2));
      } else {
         int var7 = 0;
         int var8 = 0;
         long var9 = (long)(var0.getByte(var1 + var7++) & 31);
         boolean var11 = true;

         do {
            long var13 = var9 >> 5;
            long var15 = (var9 & 31L) << 8;
            if (var9 >= 32L) {
               var13--;
               int var12 = (int)((long)var8 - var15);
               if (var13 == 6L) {
                  short var17;
                  if (var6 == 1) {
                     var13 += (long)var0.getUnsignedByte(var1 + var7++);
                  } else {
                     do {
                        var17 = var0.getUnsignedByte(var1 + var7++);
                        var13 += (long)var17;
                     } while (var17 == 255);
                  }
               }

               if (var6 == 1) {
                  var12 -= var0.getUnsignedByte(var1 + var7++);
               } else {
                  short var34 = var0.getUnsignedByte(var1 + var7++);
                  var12 -= var34;
                  if (var34 == 255 && var15 == 7936L) {
                     var15 = (long)(var0.getUnsignedByte(var1 + var7++) << 8);
                     var15 += (long)var0.getUnsignedByte(var1 + var7++);
                     var12 = (int)((long)var8 - var15 - 8191L);
                  }
               }

               if ((long)var8 + var13 + 3L > (long)var5) {
                  return 0;
               }

               if (var12 - 1 < 0) {
                  return 0;
               }

               if (var7 < var2) {
                  var9 = (long)var0.getUnsignedByte(var1 + var7++);
               } else {
                  var11 = false;
               }

               if (var12 == var8) {
                  byte var18 = var3.getByte(var4 + var12 - 1);
                  var3.setByte(var4 + var8++, var18);
                  var3.setByte(var4 + var8++, var18);
                  var3.setByte(var4 + var8++, var18);

                  while (var13 != 0L) {
                     var3.setByte(var4 + var8++, var18);
                     var13--;
                  }
               } else {
                  var12--;
                  var3.setByte(var4 + var8++, var3.getByte(var4 + var12++));
                  var3.setByte(var4 + var8++, var3.getByte(var4 + var12++));
                  var3.setByte(var4 + var8++, var3.getByte(var4 + var12++));

                  while (var13 != 0L) {
                     var3.setByte(var4 + var8++, var3.getByte(var4 + var12++));
                     var13--;
                  }
               }
            } else {
               if ((long)var8 + ++var9 > (long)var5) {
                  return 0;
               }

               if ((long)var7 + var9 > (long)var2) {
                  return 0;
               }

               var3.setByte(var4 + var8++, var0.getByte(var1 + var7++));
               var9--;

               while (var9 != 0L) {
                  var3.setByte(var4 + var8++, var0.getByte(var1 + var7++));
                  var9--;
               }

               var11 = var7 < var2;
               if (var11) {
                  var9 = (long)var0.getUnsignedByte(var1 + var7++);
               }
            }
         } while (var11);

         return var8;
      }
   }

   private static int hashFunction(ByteBuf var0, int var1) {
      int var2 = readU16(var0, var1);
      var2 ^= readU16(var0, var1 + 1) ^ var2 >> 3;
      return var2 & 8191;
   }

   private static int readU16(ByteBuf var0, int var1) {
      return var1 + 1 >= var0.readableBytes() ? var0.getUnsignedByte(var1) : var0.getUnsignedByte(var1 + 1) << 8 | var0.getUnsignedByte(var1);
   }

   private FastLz() {
   }
}
