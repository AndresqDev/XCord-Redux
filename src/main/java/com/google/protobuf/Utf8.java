package com.google.protobuf;

import java.nio.Buffer;
import java.nio.ByteBuffer;

final class Utf8 {
   private static final Utf8.Processor processor = (Utf8.Processor)(Utf8.UnsafeProcessor.isAvailable() && !Android.isOnAndroidDevice()
      ? new Utf8.UnsafeProcessor()
      : new Utf8.SafeProcessor());
   private static final long ASCII_MASK_LONG = -9187201950435737472L;
   static final int MAX_BYTES_PER_CHAR = 3;
   public static final int COMPLETE = 0;
   public static final int MALFORMED = -1;
   private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;

   public static boolean isValidUtf8(byte[] var0) {
      return processor.isValidUtf8(var0, 0, var0.length);
   }

   public static boolean isValidUtf8(byte[] var0, int var1, int var2) {
      return processor.isValidUtf8(var0, var1, var2);
   }

   public static int partialIsValidUtf8(int var0, byte[] var1, int var2, int var3) {
      return processor.partialIsValidUtf8(var0, var1, var2, var3);
   }

   private static int incompleteStateFor(int var0) {
      return var0 > -12 ? -1 : var0;
   }

   private static int incompleteStateFor(int var0, int var1) {
      return var0 <= -12 && var1 <= -65 ? var0 ^ var1 << 8 : -1;
   }

   private static int incompleteStateFor(int var0, int var1, int var2) {
      return var0 <= -12 && var1 <= -65 && var2 <= -65 ? var0 ^ var1 << 8 ^ var2 << 16 : -1;
   }

   private static int incompleteStateFor(byte[] var0, int var1, int var2) {
      byte var3 = var0[var1 - 1];
      switch (var2 - var1) {
         case 0:
            return incompleteStateFor(var3);
         case 1:
            return incompleteStateFor(var3, var0[var1]);
         case 2:
            return incompleteStateFor(var3, var0[var1], var0[var1 + 1]);
         default:
            throw new AssertionError();
      }
   }

   private static int incompleteStateFor(ByteBuffer var0, int var1, int var2, int var3) {
      switch (var3) {
         case 0:
            return incompleteStateFor(var1);
         case 1:
            return incompleteStateFor(var1, var0.get(var2));
         case 2:
            return incompleteStateFor(var1, var0.get(var2), var0.get(var2 + 1));
         default:
            throw new AssertionError();
      }
   }

   static int encodedLength(CharSequence var0) {
      int var1 = var0.length();
      int var2 = var1;
      int var3 = 0;

      while (var3 < var1 && var0.charAt(var3) < 128) {
         var3++;
      }

      while (var3 < var1) {
         char var4 = var0.charAt(var3);
         if (var4 >= 2048) {
            var2 += encodedLengthGeneral(var0, var3);
            break;
         }

         var2 += 127 - var4 >>> 31;
         var3++;
      }

      if (var2 < var1) {
         throw new IllegalArgumentException("UTF-8 length does not fit in int: " + ((long)var2 + 4294967296L));
      } else {
         return var2;
      }
   }

   private static int encodedLengthGeneral(CharSequence var0, int var1) {
      int var2 = var0.length();
      int var3 = 0;

      for (int var4 = var1; var4 < var2; var4++) {
         char var5 = var0.charAt(var4);
         if (var5 < 2048) {
            var3 += 127 - var5 >>> 31;
         } else {
            var3 += 2;
            if ('\ud800' <= var5 && var5 <= '\udfff') {
               int var6 = Character.codePointAt(var0, var4);
               if (var6 < 65536) {
                  throw new Utf8.UnpairedSurrogateException(var4, var2);
               }

               var4++;
            }
         }
      }

      return var3;
   }

   static int encode(CharSequence var0, byte[] var1, int var2, int var3) {
      return processor.encodeUtf8(var0, var1, var2, var3);
   }

   static boolean isValidUtf8(ByteBuffer var0) {
      return processor.isValidUtf8(var0, var0.position(), var0.remaining());
   }

   static int partialIsValidUtf8(int var0, ByteBuffer var1, int var2, int var3) {
      return processor.partialIsValidUtf8(var0, var1, var2, var3);
   }

   static String decodeUtf8(ByteBuffer var0, int var1, int var2) throws InvalidProtocolBufferException {
      return processor.decodeUtf8(var0, var1, var2);
   }

   static String decodeUtf8(byte[] var0, int var1, int var2) throws InvalidProtocolBufferException {
      return processor.decodeUtf8(var0, var1, var2);
   }

   static void encodeUtf8(CharSequence var0, ByteBuffer var1) {
      processor.encodeUtf8(var0, var1);
   }

   private static int estimateConsecutiveAscii(ByteBuffer var0, int var1, int var2) {
      int var3 = var1;
      int var4 = var2 - 7;

      while (var3 < var4 && (var0.getLong(var3) & -9187201950435737472L) == 0L) {
         var3 += 8;
      }

      return var3 - var1;
   }

   private Utf8() {
   }

   private static class DecodeUtil {
      private static boolean isOneByte(byte var0) {
         return var0 >= 0;
      }

      private static boolean isTwoBytes(byte var0) {
         return var0 < -32;
      }

      private static boolean isThreeBytes(byte var0) {
         return var0 < -16;
      }

      private static void handleOneByte(byte var0, char[] var1, int var2) {
         var1[var2] = (char)var0;
      }

      private static void handleTwoBytes(byte var0, byte var1, char[] var2, int var3) throws InvalidProtocolBufferException {
         if (var0 >= -62 && !isNotTrailingByte(var1)) {
            var2[var3] = (char)((var0 & 31) << 6 | trailingByteValue(var1));
         } else {
            throw InvalidProtocolBufferException.invalidUtf8();
         }
      }

      private static void handleThreeBytes(byte var0, byte var1, byte var2, char[] var3, int var4) throws InvalidProtocolBufferException {
         if (!isNotTrailingByte(var1) && (var0 != -32 || var1 >= -96) && (var0 != -19 || var1 < -96) && !isNotTrailingByte(var2)) {
            var3[var4] = (char)((var0 & 15) << 12 | trailingByteValue(var1) << 6 | trailingByteValue(var2));
         } else {
            throw InvalidProtocolBufferException.invalidUtf8();
         }
      }

      private static void handleFourBytes(byte var0, byte var1, byte var2, byte var3, char[] var4, int var5) throws InvalidProtocolBufferException {
         if (!isNotTrailingByte(var1) && (var0 << 28) + (var1 - -112) >> 30 == 0 && !isNotTrailingByte(var2) && !isNotTrailingByte(var3)) {
            int var6 = (var0 & 7) << 18 | trailingByteValue(var1) << 12 | trailingByteValue(var2) << 6 | trailingByteValue(var3);
            var4[var5] = highSurrogate(var6);
            var4[var5 + 1] = lowSurrogate(var6);
         } else {
            throw InvalidProtocolBufferException.invalidUtf8();
         }
      }

      private static boolean isNotTrailingByte(byte var0) {
         return var0 > -65;
      }

      private static int trailingByteValue(byte var0) {
         return var0 & 63;
      }

      private static char highSurrogate(int var0) {
         return (char)(55232 + (var0 >>> 10));
      }

      private static char lowSurrogate(int var0) {
         return (char)(56320 + (var0 & 1023));
      }
   }

   abstract static class Processor {
      final boolean isValidUtf8(byte[] var1, int var2, int var3) {
         return this.partialIsValidUtf8(0, var1, var2, var3) == 0;
      }

      abstract int partialIsValidUtf8(int var1, byte[] var2, int var3, int var4);

      final boolean isValidUtf8(ByteBuffer var1, int var2, int var3) {
         return this.partialIsValidUtf8(0, var1, var2, var3) == 0;
      }

      final int partialIsValidUtf8(int var1, ByteBuffer var2, int var3, int var4) {
         if (var2.hasArray()) {
            int var5 = var2.arrayOffset();
            return this.partialIsValidUtf8(var1, var2.array(), var5 + var3, var5 + var4);
         } else {
            return var2.isDirect() ? this.partialIsValidUtf8Direct(var1, var2, var3, var4) : this.partialIsValidUtf8Default(var1, var2, var3, var4);
         }
      }

      abstract int partialIsValidUtf8Direct(int var1, ByteBuffer var2, int var3, int var4);

      final int partialIsValidUtf8Default(int var1, ByteBuffer var2, int var3, int var4) {
         if (var1 != 0) {
            if (var3 >= var4) {
               return var1;
            }

            byte var5 = (byte)var1;
            if (var5 < -32) {
               if (var5 < -62 || var2.get(var3++) > -65) {
                  return -1;
               }
            } else if (var5 < -16) {
               byte var6 = (byte)(~(var1 >> 8));
               if (var6 == 0) {
                  var6 = var2.get(var3++);
                  if (var3 >= var4) {
                     return Utf8.incompleteStateFor(var5, var6);
                  }
               }

               if (var6 > -65 || var5 == -32 && var6 < -96 || var5 == -19 && var6 >= -96 || var2.get(var3++) > -65) {
                  return -1;
               }
            } else {
               byte var8 = (byte)(~(var1 >> 8));
               byte var7 = 0;
               if (var8 == 0) {
                  var8 = var2.get(var3++);
                  if (var3 >= var4) {
                     return Utf8.incompleteStateFor(var5, var8);
                  }
               } else {
                  var7 = (byte)(var1 >> 16);
               }

               if (var7 == 0) {
                  var7 = var2.get(var3++);
                  if (var3 >= var4) {
                     return Utf8.incompleteStateFor(var5, var8, var7);
                  }
               }

               if (var8 > -65 || (var5 << 28) + (var8 - -112) >> 30 != 0 || var7 > -65 || var2.get(var3++) > -65) {
                  return -1;
               }
            }
         }

         return partialIsValidUtf8(var2, var3, var4);
      }

      private static int partialIsValidUtf8(ByteBuffer var0, int var1, int var2) {
         var1 += Utf8.estimateConsecutiveAscii(var0, var1, var2);

         while (var1 < var2) {
            byte var3;
            if ((var3 = var0.get(var1++)) < 0) {
               if (var3 < -32) {
                  if (var1 >= var2) {
                     return var3;
                  }

                  if (var3 < -62 || var0.get(var1) > -65) {
                     return -1;
                  }

                  var1++;
               } else if (var3 < -16) {
                  if (var1 >= var2 - 1) {
                     return Utf8.incompleteStateFor(var0, var3, var1, var2 - var1);
                  }

                  byte var9 = var0.get(var1++);
                  if (var9 > -65 || var3 == -32 && var9 < -96 || var3 == -19 && var9 >= -96 || var0.get(var1) > -65) {
                     return -1;
                  }

                  var1++;
               } else {
                  if (var1 >= var2 - 2) {
                     return Utf8.incompleteStateFor(var0, var3, var1, var2 - var1);
                  }

                  byte var4 = var0.get(var1++);
                  if (var4 > -65 || (var3 << 28) + (var4 - -112) >> 30 != 0 || var0.get(var1++) > -65 || var0.get(var1++) > -65) {
                     return -1;
                  }
               }
            }
         }

         return 0;
      }

      abstract String decodeUtf8(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException;

      final String decodeUtf8(ByteBuffer var1, int var2, int var3) throws InvalidProtocolBufferException {
         if (var1.hasArray()) {
            int var4 = var1.arrayOffset();
            return this.decodeUtf8(var1.array(), var4 + var2, var3);
         } else {
            return var1.isDirect() ? this.decodeUtf8Direct(var1, var2, var3) : this.decodeUtf8Default(var1, var2, var3);
         }
      }

      abstract String decodeUtf8Direct(ByteBuffer var1, int var2, int var3) throws InvalidProtocolBufferException;

      final String decodeUtf8Default(ByteBuffer var1, int var2, int var3) throws InvalidProtocolBufferException {
         if ((var2 | var3 | var1.limit() - var2 - var3) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", var1.limit(), var2, var3));
         } else {
            int var4 = var2;
            int var5 = var2 + var3;
            char[] var6 = new char[var3];
            int var7 = 0;

            while (var4 < var5) {
               byte var8 = var1.get(var4);
               if (!Utf8.DecodeUtil.isOneByte(var8)) {
                  break;
               }

               var4++;
               Utf8.DecodeUtil.handleOneByte(var8, var6, var7++);
            }

            while (var4 < var5) {
               byte var14 = var1.get(var4++);
               if (Utf8.DecodeUtil.isOneByte(var14)) {
                  Utf8.DecodeUtil.handleOneByte(var14, var6, var7++);

                  while (var4 < var5) {
                     byte var9 = var1.get(var4);
                     if (!Utf8.DecodeUtil.isOneByte(var9)) {
                        break;
                     }

                     var4++;
                     Utf8.DecodeUtil.handleOneByte(var9, var6, var7++);
                  }
               } else if (Utf8.DecodeUtil.isTwoBytes(var14)) {
                  if (var4 >= var5) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleTwoBytes(var14, var1.get(var4++), var6, var7++);
               } else if (Utf8.DecodeUtil.isThreeBytes(var14)) {
                  if (var4 >= var5 - 1) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleThreeBytes(var14, var1.get(var4++), var1.get(var4++), var6, var7++);
               } else {
                  if (var4 >= var5 - 2) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleFourBytes(var14, var1.get(var4++), var1.get(var4++), var1.get(var4++), var6, var7++);
                  var7++;
               }
            }

            return new String(var6, 0, var7);
         }
      }

      abstract int encodeUtf8(CharSequence var1, byte[] var2, int var3, int var4);

      final void encodeUtf8(CharSequence var1, ByteBuffer var2) {
         if (var2.hasArray()) {
            int var3 = var2.arrayOffset();
            int var4 = Utf8.encode(var1, var2.array(), var3 + var2.position(), var2.remaining());
            ((Buffer)var2).position(var4 - var3);
         } else if (var2.isDirect()) {
            this.encodeUtf8Direct(var1, var2);
         } else {
            this.encodeUtf8Default(var1, var2);
         }
      }

      abstract void encodeUtf8Direct(CharSequence var1, ByteBuffer var2);

      final void encodeUtf8Default(CharSequence var1, ByteBuffer var2) {
         int var3 = var1.length();
         int var4 = var2.position();
         int var5 = 0;

         try {
            char var6;
            while (var5 < var3 && (var6 = var1.charAt(var5)) < 128) {
               var2.put(var4 + var5, (byte)var6);
               var5++;
            }

            if (var5 == var3) {
               ((Buffer)var2).position(var4 + var5);
            } else {
               for (var4 += var5; var5 < var3; var4++) {
                  var6 = var1.charAt(var5);
                  if (var6 < 128) {
                     var2.put(var4, (byte)var6);
                  } else if (var6 < 2048) {
                     var2.put(var4++, (byte)(192 | var6 >>> 6));
                     var2.put(var4, (byte)(128 | '?' & var6));
                  } else if (var6 >= '\ud800' && '\udfff' >= var6) {
                     char var11;
                     if (var5 + 1 == var3 || !Character.isSurrogatePair(var6, var11 = var1.charAt(++var5))) {
                        throw new Utf8.UnpairedSurrogateException(var5, var3);
                     }

                     int var8 = Character.toCodePoint(var6, var11);
                     var2.put(var4++, (byte)(240 | var8 >>> 18));
                     var2.put(var4++, (byte)(128 | 63 & var8 >>> 12));
                     var2.put(var4++, (byte)(128 | 63 & var8 >>> 6));
                     var2.put(var4, (byte)(128 | 63 & var8));
                  } else {
                     var2.put(var4++, (byte)(224 | var6 >>> '\f'));
                     var2.put(var4++, (byte)(128 | 63 & var6 >>> 6));
                     var2.put(var4, (byte)(128 | '?' & var6));
                  }

                  var5++;
               }

               ((Buffer)var2).position(var4);
            }
         } catch (IndexOutOfBoundsException var9) {
            int var7 = var2.position() + Math.max(var5, var4 - var2.position() + 1);
            throw new ArrayIndexOutOfBoundsException("Failed writing " + var1.charAt(var5) + " at index " + var7);
         }
      }
   }

   static final class SafeProcessor extends Utf8.Processor {
      @Override
      int partialIsValidUtf8(int var1, byte[] var2, int var3, int var4) {
         if (var1 != 0) {
            if (var3 >= var4) {
               return var1;
            }

            byte var5 = (byte)var1;
            if (var5 < -32) {
               if (var5 < -62 || var2[var3++] > -65) {
                  return -1;
               }
            } else if (var5 < -16) {
               byte var6 = (byte)(~(var1 >> 8));
               if (var6 == 0) {
                  var6 = var2[var3++];
                  if (var3 >= var4) {
                     return Utf8.incompleteStateFor(var5, var6);
                  }
               }

               if (var6 > -65 || var5 == -32 && var6 < -96 || var5 == -19 && var6 >= -96 || var2[var3++] > -65) {
                  return -1;
               }
            } else {
               byte var8 = (byte)(~(var1 >> 8));
               byte var7 = 0;
               if (var8 == 0) {
                  var8 = var2[var3++];
                  if (var3 >= var4) {
                     return Utf8.incompleteStateFor(var5, var8);
                  }
               } else {
                  var7 = (byte)(var1 >> 16);
               }

               if (var7 == 0) {
                  var7 = var2[var3++];
                  if (var3 >= var4) {
                     return Utf8.incompleteStateFor(var5, var8, var7);
                  }
               }

               if (var8 > -65 || (var5 << 28) + (var8 - -112) >> 30 != 0 || var7 > -65 || var2[var3++] > -65) {
                  return -1;
               }
            }
         }

         return partialIsValidUtf8(var2, var3, var4);
      }

      @Override
      int partialIsValidUtf8Direct(int var1, ByteBuffer var2, int var3, int var4) {
         return this.partialIsValidUtf8Default(var1, var2, var3, var4);
      }

      @Override
      String decodeUtf8(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException {
         if ((var2 | var3 | var1.length - var2 - var3) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", var1.length, var2, var3));
         } else {
            int var4 = var2;
            int var5 = var2 + var3;
            char[] var6 = new char[var3];
            int var7 = 0;

            while (var4 < var5) {
               byte var8 = var1[var4];
               if (!Utf8.DecodeUtil.isOneByte(var8)) {
                  break;
               }

               var4++;
               Utf8.DecodeUtil.handleOneByte(var8, var6, var7++);
            }

            while (var4 < var5) {
               byte var14 = var1[var4++];
               if (Utf8.DecodeUtil.isOneByte(var14)) {
                  Utf8.DecodeUtil.handleOneByte(var14, var6, var7++);

                  while (var4 < var5) {
                     byte var9 = var1[var4];
                     if (!Utf8.DecodeUtil.isOneByte(var9)) {
                        break;
                     }

                     var4++;
                     Utf8.DecodeUtil.handleOneByte(var9, var6, var7++);
                  }
               } else if (Utf8.DecodeUtil.isTwoBytes(var14)) {
                  if (var4 >= var5) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleTwoBytes(var14, var1[var4++], var6, var7++);
               } else if (Utf8.DecodeUtil.isThreeBytes(var14)) {
                  if (var4 >= var5 - 1) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleThreeBytes(var14, var1[var4++], var1[var4++], var6, var7++);
               } else {
                  if (var4 >= var5 - 2) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleFourBytes(var14, var1[var4++], var1[var4++], var1[var4++], var6, var7++);
                  var7++;
               }
            }

            return new String(var6, 0, var7);
         }
      }

      @Override
      String decodeUtf8Direct(ByteBuffer var1, int var2, int var3) throws InvalidProtocolBufferException {
         return this.decodeUtf8Default(var1, var2, var3);
      }

      @Override
      int encodeUtf8(CharSequence var1, byte[] var2, int var3, int var4) {
         int var5 = var1.length();
         int var6 = var3;
         int var7 = 0;

         int var8;
         char var9;
         for (var8 = var3 + var4; var7 < var5 && var7 + var6 < var8 && (var9 = var1.charAt(var7)) < 128; var7++) {
            var2[var6 + var7] = (byte)var9;
         }

         if (var7 == var5) {
            return var6 + var5;
         } else {
            for (var6 += var7; var7 < var5; var7++) {
               var9 = var1.charAt(var7);
               if (var9 < 128 && var6 < var8) {
                  var2[var6++] = (byte)var9;
               } else if (var9 < 2048 && var6 <= var8 - 2) {
                  var2[var6++] = (byte)(960 | var9 >>> 6);
                  var2[var6++] = (byte)(128 | '?' & var9);
               } else if ((var9 < '\ud800' || '\udfff' < var9) && var6 <= var8 - 3) {
                  var2[var6++] = (byte)(480 | var9 >>> '\f');
                  var2[var6++] = (byte)(128 | 63 & var9 >>> 6);
                  var2[var6++] = (byte)(128 | '?' & var9);
               } else {
                  if (var6 > var8 - 4) {
                     if ('\ud800' > var9 || var9 > '\udfff' || var7 + 1 != var1.length() && Character.isSurrogatePair(var9, var1.charAt(var7 + 1))) {
                        throw new ArrayIndexOutOfBoundsException("Failed writing " + var9 + " at index " + var6);
                     }

                     throw new Utf8.UnpairedSurrogateException(var7, var5);
                  }

                  char var10;
                  if (var7 + 1 == var1.length() || !Character.isSurrogatePair(var9, var10 = var1.charAt(++var7))) {
                     throw new Utf8.UnpairedSurrogateException(var7 - 1, var5);
                  }

                  int var11 = Character.toCodePoint(var9, var10);
                  var2[var6++] = (byte)(240 | var11 >>> 18);
                  var2[var6++] = (byte)(128 | 63 & var11 >>> 12);
                  var2[var6++] = (byte)(128 | 63 & var11 >>> 6);
                  var2[var6++] = (byte)(128 | 63 & var11);
               }
            }

            return var6;
         }
      }

      @Override
      void encodeUtf8Direct(CharSequence var1, ByteBuffer var2) {
         this.encodeUtf8Default(var1, var2);
      }

      private static int partialIsValidUtf8(byte[] var0, int var1, int var2) {
         while (var1 < var2 && var0[var1] >= 0) {
            var1++;
         }

         return var1 >= var2 ? 0 : partialIsValidUtf8NonAscii(var0, var1, var2);
      }

      private static int partialIsValidUtf8NonAscii(byte[] var0, int var1, int var2) {
         while (var1 < var2) {
            byte var3;
            if ((var3 = var0[var1++]) < 0) {
               if (var3 < -32) {
                  if (var1 >= var2) {
                     return var3;
                  }

                  if (var3 < -62 || var0[var1++] > -65) {
                     return -1;
                  }
               } else if (var3 < -16) {
                  if (var1 >= var2 - 1) {
                     return Utf8.incompleteStateFor(var0, var1, var2);
                  }

                  byte var8;
                  if ((var8 = var0[var1++]) > -65 || var3 == -32 && var8 < -96 || var3 == -19 && var8 >= -96 || var0[var1++] > -65) {
                     return -1;
                  }
               } else {
                  if (var1 >= var2 - 2) {
                     return Utf8.incompleteStateFor(var0, var1, var2);
                  }

                  byte var4;
                  if ((var4 = var0[var1++]) > -65 || (var3 << 28) + (var4 - -112) >> 30 != 0 || var0[var1++] > -65 || var0[var1++] > -65) {
                     return -1;
                  }
               }
            }
         }

         return 0;
      }
   }

   static class UnpairedSurrogateException extends IllegalArgumentException {
      UnpairedSurrogateException(int var1, int var2) {
         super("Unpaired surrogate at index " + var1 + " of " + var2);
      }
   }

   static final class UnsafeProcessor extends Utf8.Processor {
      static boolean isAvailable() {
         return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      @Override
      int partialIsValidUtf8(int var1, byte[] var2, int var3, int var4) {
         if ((var3 | var4 | var2.length - var4) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("Array length=%d, index=%d, limit=%d", var2.length, var3, var4));
         } else {
            long var5 = (long)var3;
            long var7 = (long)var4;
            if (var1 != 0) {
               if (var5 >= var7) {
                  return var1;
               }

               byte var9 = (byte)var1;
               if (var9 < -32) {
                  if (var9 < -62 || UnsafeUtil.getByte(var2, var5++) > -65) {
                     return -1;
                  }
               } else if (var9 < -16) {
                  byte var10 = (byte)(~(var1 >> 8));
                  if (var10 == 0) {
                     var10 = UnsafeUtil.getByte(var2, var5++);
                     if (var5 >= var7) {
                        return Utf8.incompleteStateFor(var9, var10);
                     }
                  }

                  if (var10 > -65 || var9 == -32 && var10 < -96 || var9 == -19 && var10 >= -96 || UnsafeUtil.getByte(var2, var5++) > -65) {
                     return -1;
                  }
               } else {
                  byte var12 = (byte)(~(var1 >> 8));
                  byte var11 = 0;
                  if (var12 == 0) {
                     var12 = UnsafeUtil.getByte(var2, var5++);
                     if (var5 >= var7) {
                        return Utf8.incompleteStateFor(var9, var12);
                     }
                  } else {
                     var11 = (byte)(var1 >> 16);
                  }

                  if (var11 == 0) {
                     var11 = UnsafeUtil.getByte(var2, var5++);
                     if (var5 >= var7) {
                        return Utf8.incompleteStateFor(var9, var12, var11);
                     }
                  }

                  if (var12 > -65 || (var9 << 28) + (var12 - -112) >> 30 != 0 || var11 > -65 || UnsafeUtil.getByte(var2, var5++) > -65) {
                     return -1;
                  }
               }
            }

            return partialIsValidUtf8(var2, var5, (int)(var7 - var5));
         }
      }

      @Override
      int partialIsValidUtf8Direct(int var1, ByteBuffer var2, int var3, int var4) {
         if ((var3 | var4 | var2.limit() - var4) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", var2.limit(), var3, var4));
         } else {
            long var5 = UnsafeUtil.addressOffset(var2) + (long)var3;
            long var7 = var5 + (long)(var4 - var3);
            if (var1 != 0) {
               if (var5 >= var7) {
                  return var1;
               }

               byte var9 = (byte)var1;
               if (var9 < -32) {
                  if (var9 < -62 || UnsafeUtil.getByte(var5++) > -65) {
                     return -1;
                  }
               } else if (var9 < -16) {
                  byte var10 = (byte)(~(var1 >> 8));
                  if (var10 == 0) {
                     var10 = UnsafeUtil.getByte(var5++);
                     if (var5 >= var7) {
                        return Utf8.incompleteStateFor(var9, var10);
                     }
                  }

                  if (var10 > -65 || var9 == -32 && var10 < -96 || var9 == -19 && var10 >= -96 || UnsafeUtil.getByte(var5++) > -65) {
                     return -1;
                  }
               } else {
                  byte var12 = (byte)(~(var1 >> 8));
                  byte var11 = 0;
                  if (var12 == 0) {
                     var12 = UnsafeUtil.getByte(var5++);
                     if (var5 >= var7) {
                        return Utf8.incompleteStateFor(var9, var12);
                     }
                  } else {
                     var11 = (byte)(var1 >> 16);
                  }

                  if (var11 == 0) {
                     var11 = UnsafeUtil.getByte(var5++);
                     if (var5 >= var7) {
                        return Utf8.incompleteStateFor(var9, var12, var11);
                     }
                  }

                  if (var12 > -65 || (var9 << 28) + (var12 - -112) >> 30 != 0 || var11 > -65 || UnsafeUtil.getByte(var5++) > -65) {
                     return -1;
                  }
               }
            }

            return partialIsValidUtf8(var5, (int)(var7 - var5));
         }
      }

      @Override
      String decodeUtf8(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException {
         if ((var2 | var3 | var1.length - var2 - var3) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", var1.length, var2, var3));
         } else {
            int var4 = var2;
            int var5 = var2 + var3;
            char[] var6 = new char[var3];
            int var7 = 0;

            while (var4 < var5) {
               byte var8 = UnsafeUtil.getByte(var1, (long)var4);
               if (!Utf8.DecodeUtil.isOneByte(var8)) {
                  break;
               }

               var4++;
               Utf8.DecodeUtil.handleOneByte(var8, var6, var7++);
            }

            while (var4 < var5) {
               byte var14 = UnsafeUtil.getByte(var1, (long)(var4++));
               if (Utf8.DecodeUtil.isOneByte(var14)) {
                  Utf8.DecodeUtil.handleOneByte(var14, var6, var7++);

                  while (var4 < var5) {
                     byte var9 = UnsafeUtil.getByte(var1, (long)var4);
                     if (!Utf8.DecodeUtil.isOneByte(var9)) {
                        break;
                     }

                     var4++;
                     Utf8.DecodeUtil.handleOneByte(var9, var6, var7++);
                  }
               } else if (Utf8.DecodeUtil.isTwoBytes(var14)) {
                  if (var4 >= var5) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleTwoBytes(var14, UnsafeUtil.getByte(var1, (long)(var4++)), var6, var7++);
               } else if (Utf8.DecodeUtil.isThreeBytes(var14)) {
                  if (var4 >= var5 - 1) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleThreeBytes(var14, UnsafeUtil.getByte(var1, (long)(var4++)), UnsafeUtil.getByte(var1, (long)(var4++)), var6, var7++);
               } else {
                  if (var4 >= var5 - 2) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleFourBytes(
                     var14,
                     UnsafeUtil.getByte(var1, (long)(var4++)),
                     UnsafeUtil.getByte(var1, (long)(var4++)),
                     UnsafeUtil.getByte(var1, (long)(var4++)),
                     var6,
                     var7++
                  );
                  var7++;
               }
            }

            return new String(var6, 0, var7);
         }
      }

      @Override
      String decodeUtf8Direct(ByteBuffer var1, int var2, int var3) throws InvalidProtocolBufferException {
         if ((var2 | var3 | var1.limit() - var2 - var3) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", var1.limit(), var2, var3));
         } else {
            long var4 = UnsafeUtil.addressOffset(var1) + (long)var2;
            long var6 = var4 + (long)var3;
            char[] var8 = new char[var3];
            int var9 = 0;

            while (var4 < var6) {
               byte var10 = UnsafeUtil.getByte(var4);
               if (!Utf8.DecodeUtil.isOneByte(var10)) {
                  break;
               }

               var4++;
               Utf8.DecodeUtil.handleOneByte(var10, var8, var9++);
            }

            while (var4 < var6) {
               byte var16 = UnsafeUtil.getByte(var4++);
               if (Utf8.DecodeUtil.isOneByte(var16)) {
                  Utf8.DecodeUtil.handleOneByte(var16, var8, var9++);

                  while (var4 < var6) {
                     byte var11 = UnsafeUtil.getByte(var4);
                     if (!Utf8.DecodeUtil.isOneByte(var11)) {
                        break;
                     }

                     var4++;
                     Utf8.DecodeUtil.handleOneByte(var11, var8, var9++);
                  }
               } else if (Utf8.DecodeUtil.isTwoBytes(var16)) {
                  if (var4 >= var6) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleTwoBytes(var16, UnsafeUtil.getByte(var4++), var8, var9++);
               } else if (Utf8.DecodeUtil.isThreeBytes(var16)) {
                  if (var4 >= var6 - 1L) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleThreeBytes(var16, UnsafeUtil.getByte(var4++), UnsafeUtil.getByte(var4++), var8, var9++);
               } else {
                  if (var4 >= var6 - 2L) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleFourBytes(var16, UnsafeUtil.getByte(var4++), UnsafeUtil.getByte(var4++), UnsafeUtil.getByte(var4++), var8, var9++);
                  var9++;
               }
            }

            return new String(var8, 0, var9);
         }
      }

      @Override
      int encodeUtf8(CharSequence var1, byte[] var2, int var3, int var4) {
         long var5 = (long)var3;
         long var7 = var5 + (long)var4;
         int var9 = var1.length();
         if (var9 <= var4 && var2.length - var4 >= var3) {
            int var10;
            char var11;
            for (var10 = 0; var10 < var9 && (var11 = var1.charAt(var10)) < 128; var10++) {
               UnsafeUtil.putByte(var2, var5++, (byte)var11);
            }

            if (var10 == var9) {
               return (int)var5;
            } else {
               for (; var10 < var9; var10++) {
                  var11 = var1.charAt(var10);
                  if (var11 < 128 && var5 < var7) {
                     UnsafeUtil.putByte(var2, var5++, (byte)var11);
                  } else if (var11 < 2048 && var5 <= var7 - 2L) {
                     UnsafeUtil.putByte(var2, var5++, (byte)(960 | var11 >>> 6));
                     UnsafeUtil.putByte(var2, var5++, (byte)(128 | '?' & var11));
                  } else if ((var11 < '\ud800' || '\udfff' < var11) && var5 <= var7 - 3L) {
                     UnsafeUtil.putByte(var2, var5++, (byte)(480 | var11 >>> '\f'));
                     UnsafeUtil.putByte(var2, var5++, (byte)(128 | 63 & var11 >>> 6));
                     UnsafeUtil.putByte(var2, var5++, (byte)(128 | '?' & var11));
                  } else {
                     if (var5 > var7 - 4L) {
                        if ('\ud800' > var11 || var11 > '\udfff' || var10 + 1 != var9 && Character.isSurrogatePair(var11, var1.charAt(var10 + 1))) {
                           throw new ArrayIndexOutOfBoundsException("Failed writing " + var11 + " at index " + var5);
                        }

                        throw new Utf8.UnpairedSurrogateException(var10, var9);
                     }

                     char var12;
                     if (var10 + 1 == var9 || !Character.isSurrogatePair(var11, var12 = var1.charAt(++var10))) {
                        throw new Utf8.UnpairedSurrogateException(var10 - 1, var9);
                     }

                     int var13 = Character.toCodePoint(var11, var12);
                     UnsafeUtil.putByte(var2, var5++, (byte)(240 | var13 >>> 18));
                     UnsafeUtil.putByte(var2, var5++, (byte)(128 | 63 & var13 >>> 12));
                     UnsafeUtil.putByte(var2, var5++, (byte)(128 | 63 & var13 >>> 6));
                     UnsafeUtil.putByte(var2, var5++, (byte)(128 | 63 & var13));
                  }
               }

               return (int)var5;
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("Failed writing " + var1.charAt(var9 - 1) + " at index " + (var3 + var4));
         }
      }

      @Override
      void encodeUtf8Direct(CharSequence var1, ByteBuffer var2) {
         long var3 = UnsafeUtil.addressOffset(var2);
         long var5 = var3 + (long)var2.position();
         long var7 = var3 + (long)var2.limit();
         int var9 = var1.length();
         if ((long)var9 > var7 - var5) {
            throw new ArrayIndexOutOfBoundsException("Failed writing " + var1.charAt(var9 - 1) + " at index " + var2.limit());
         } else {
            int var10;
            char var11;
            for (var10 = 0; var10 < var9 && (var11 = var1.charAt(var10)) < 128; var10++) {
               UnsafeUtil.putByte(var5++, (byte)var11);
            }

            if (var10 == var9) {
               ((Buffer)var2).position((int)(var5 - var3));
            } else {
               for (; var10 < var9; var10++) {
                  var11 = var1.charAt(var10);
                  if (var11 < 128 && var5 < var7) {
                     UnsafeUtil.putByte(var5++, (byte)var11);
                  } else if (var11 < 2048 && var5 <= var7 - 2L) {
                     UnsafeUtil.putByte(var5++, (byte)(960 | var11 >>> 6));
                     UnsafeUtil.putByte(var5++, (byte)(128 | '?' & var11));
                  } else if ((var11 < '\ud800' || '\udfff' < var11) && var5 <= var7 - 3L) {
                     UnsafeUtil.putByte(var5++, (byte)(480 | var11 >>> '\f'));
                     UnsafeUtil.putByte(var5++, (byte)(128 | 63 & var11 >>> 6));
                     UnsafeUtil.putByte(var5++, (byte)(128 | '?' & var11));
                  } else {
                     if (var5 > var7 - 4L) {
                        if ('\ud800' > var11 || var11 > '\udfff' || var10 + 1 != var9 && Character.isSurrogatePair(var11, var1.charAt(var10 + 1))) {
                           throw new ArrayIndexOutOfBoundsException("Failed writing " + var11 + " at index " + var5);
                        }

                        throw new Utf8.UnpairedSurrogateException(var10, var9);
                     }

                     char var12;
                     if (var10 + 1 == var9 || !Character.isSurrogatePair(var11, var12 = var1.charAt(++var10))) {
                        throw new Utf8.UnpairedSurrogateException(var10 - 1, var9);
                     }

                     int var13 = Character.toCodePoint(var11, var12);
                     UnsafeUtil.putByte(var5++, (byte)(240 | var13 >>> 18));
                     UnsafeUtil.putByte(var5++, (byte)(128 | 63 & var13 >>> 12));
                     UnsafeUtil.putByte(var5++, (byte)(128 | 63 & var13 >>> 6));
                     UnsafeUtil.putByte(var5++, (byte)(128 | 63 & var13));
                  }
               }

               ((Buffer)var2).position((int)(var5 - var3));
            }
         }
      }

      private static int unsafeEstimateConsecutiveAscii(byte[] var0, long var1, int var3) {
         if (var3 < 16) {
            return 0;
         } else {
            for (int var4 = 0; var4 < var3; var4++) {
               if (UnsafeUtil.getByte(var0, var1++) < 0) {
                  return var4;
               }
            }

            return var3;
         }
      }

      private static int unsafeEstimateConsecutiveAscii(long var0, int var2) {
         if (var2 < 16) {
            return 0;
         } else {
            int var4 = 8 - ((int)var0 & 7);

            for (int var5 = var4; var5 > 0; var5--) {
               if (UnsafeUtil.getByte(var0++) < 0) {
                  return var4 - var5;
               }
            }

            int var3;
            for (var3 = var2 - var4; var3 >= 8 && (UnsafeUtil.getLong(var0) & -9187201950435737472L) == 0L; var3 -= 8) {
               var0 += 8L;
            }

            return var2 - var3;
         }
      }

      private static int partialIsValidUtf8(byte[] var0, long var1, int var3) {
         int var4 = unsafeEstimateConsecutiveAscii(var0, var1, var3);
         var3 -= var4;
         var1 += (long)var4;

         while (true) {
            byte var5 = 0;

            while (var3 > 0 && (var5 = UnsafeUtil.getByte(var0, var1++)) >= 0) {
               var3--;
            }

            if (var3 == 0) {
               return 0;
            }

            var3--;
            if (var5 < -32) {
               if (var3 == 0) {
                  return var5;
               }

               var3--;
               if (var5 < -62 || UnsafeUtil.getByte(var0, var1++) > -65) {
                  return -1;
               }
            } else if (var5 < -16) {
               if (var3 < 2) {
                  return unsafeIncompleteStateFor(var0, var5, var1, var3);
               }

               var3 -= 2;
               byte var13;
               if ((var13 = UnsafeUtil.getByte(var0, var1++)) > -65
                  || var5 == -32 && var13 < -96
                  || var5 == -19 && var13 >= -96
                  || UnsafeUtil.getByte(var0, var1++) > -65) {
                  return -1;
               }
            } else {
               if (var3 < 3) {
                  return unsafeIncompleteStateFor(var0, var5, var1, var3);
               }

               var3 -= 3;
               byte var6;
               if ((var6 = UnsafeUtil.getByte(var0, var1++)) > -65
                  || (var5 << 28) + (var6 - -112) >> 30 != 0
                  || UnsafeUtil.getByte(var0, var1++) > -65
                  || UnsafeUtil.getByte(var0, var1++) > -65) {
                  return -1;
               }
            }
         }
      }

      private static int partialIsValidUtf8(long var0, int var2) {
         int var3 = unsafeEstimateConsecutiveAscii(var0, var2);
         var0 += (long)var3;
         var2 -= var3;

         while (true) {
            byte var4 = 0;

            while (var2 > 0 && (var4 = UnsafeUtil.getByte(var0++)) >= 0) {
               var2--;
            }

            if (var2 == 0) {
               return 0;
            }

            var2--;
            if (var4 < -32) {
               if (var2 == 0) {
                  return var4;
               }

               var2--;
               if (var4 < -62 || UnsafeUtil.getByte(var0++) > -65) {
                  return -1;
               }
            } else if (var4 < -16) {
               if (var2 < 2) {
                  return unsafeIncompleteStateFor(var0, var4, var2);
               }

               var2 -= 2;
               byte var12 = UnsafeUtil.getByte(var0++);
               if (var12 > -65 || var4 == -32 && var12 < -96 || var4 == -19 && var12 >= -96 || UnsafeUtil.getByte(var0++) > -65) {
                  return -1;
               }
            } else {
               if (var2 < 3) {
                  return unsafeIncompleteStateFor(var0, var4, var2);
               }

               var2 -= 3;
               byte var5 = UnsafeUtil.getByte(var0++);
               if (var5 > -65 || (var4 << 28) + (var5 - -112) >> 30 != 0 || UnsafeUtil.getByte(var0++) > -65 || UnsafeUtil.getByte(var0++) > -65) {
                  return -1;
               }
            }
         }
      }

      private static int unsafeIncompleteStateFor(byte[] var0, int var1, long var2, int var4) {
         switch (var4) {
            case 0:
               return Utf8.incompleteStateFor(var1);
            case 1:
               return Utf8.incompleteStateFor(var1, UnsafeUtil.getByte(var0, var2));
            case 2:
               return Utf8.incompleteStateFor(var1, UnsafeUtil.getByte(var0, var2), UnsafeUtil.getByte(var0, var2 + 1L));
            default:
               throw new AssertionError();
         }
      }

      private static int unsafeIncompleteStateFor(long var0, int var2, int var3) {
         switch (var3) {
            case 0:
               return Utf8.incompleteStateFor(var2);
            case 1:
               return Utf8.incompleteStateFor(var2, UnsafeUtil.getByte(var0));
            case 2:
               return Utf8.incompleteStateFor(var2, UnsafeUtil.getByte(var0), UnsafeUtil.getByte(var0 + 1L));
            default:
               throw new AssertionError();
         }
      }
   }
}
