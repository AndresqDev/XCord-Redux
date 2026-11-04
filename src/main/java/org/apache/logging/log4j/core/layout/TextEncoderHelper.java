package org.apache.logging.log4j.core.layout;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class TextEncoderHelper {
   private TextEncoderHelper() {
   }

   static void encodeTextFallBack(Charset var0, StringBuilder var1, ByteBufferDestination var2) {
      byte[] var3 = var1.toString().getBytes(var0);
      var2.writeBytes(var3, 0, var3.length);
   }

   static void encodeText(CharsetEncoder var0, CharBuffer var1, ByteBuffer var2, StringBuilder var3, ByteBufferDestination var4) throws CharacterCodingException {
      var0.reset();
      if (var3.length() > var1.capacity()) {
         encodeChunkedText(var0, var1, var2, var3, var4);
      } else {
         ((Buffer)var1).clear();
         var3.getChars(0, var3.length(), var1.array(), var1.arrayOffset());
         ((Buffer)var1).limit(var3.length());
         CoderResult var5 = var0.encode(var1, var2, true);
         writeEncodedText(var0, var1, var2, var4, var5);
      }
   }

   private static void writeEncodedText(CharsetEncoder var0, CharBuffer var1, ByteBuffer var2, ByteBufferDestination var3, CoderResult var4) {
      if (!var4.isUnderflow()) {
         writeChunkedEncodedText(var0, var1, var3, var2, var4);
      } else {
         var4 = var0.flush(var2);
         if (!var4.isUnderflow()) {
            synchronized (var3) {
               flushRemainingBytes(var0, var3, var2);
            }
         } else {
            if (var2 != var3.getByteBuffer()) {
               ((Buffer)var2).flip();
               var3.writeBytes(var2);
               ((Buffer)var2).clear();
            }
         }
      }
   }

   private static void writeChunkedEncodedText(CharsetEncoder var0, CharBuffer var1, ByteBufferDestination var2, ByteBuffer var3, CoderResult var4) {
      synchronized (var2) {
         var3 = writeAndEncodeAsMuchAsPossible(var0, var1, true, var2, var3, var4);
         flushRemainingBytes(var0, var2, var3);
      }
   }

   private static void encodeChunkedText(CharsetEncoder var0, CharBuffer var1, ByteBuffer var2, StringBuilder var3, ByteBufferDestination var4) {
      int var5 = 0;
      CoderResult var6 = CoderResult.UNDERFLOW;
      boolean var7 = false;

      while (!var7 && var6.isUnderflow()) {
         ((Buffer)var1).clear();
         int var8 = copy(var3, var5, var1);
         var5 += var8;
         var7 = var5 >= var3.length();
         ((Buffer)var1).flip();
         var6 = var0.encode(var1, var2, var7);
      }

      if (var7) {
         writeEncodedText(var0, var1, var2, var4, var6);
      } else {
         synchronized (var4) {
            var2 = writeAndEncodeAsMuchAsPossible(var0, var1, var7, var4, var2, var6);

            while (!var7) {
               for (var6 = CoderResult.UNDERFLOW; !var7 && var6.isUnderflow(); var6 = var0.encode(var1, var2, var7)) {
                  ((Buffer)var1).clear();
                  int var9 = copy(var3, var5, var1);
                  var5 += var9;
                  var7 = var5 >= var3.length();
                  ((Buffer)var1).flip();
               }

               var2 = writeAndEncodeAsMuchAsPossible(var0, var1, var7, var4, var2, var6);
            }

            flushRemainingBytes(var0, var4, var2);
         }
      }
   }

   @Deprecated
   public static void encodeText(CharsetEncoder var0, CharBuffer var1, ByteBufferDestination var2) {
      var0.reset();
      synchronized (var2) {
         ByteBuffer var4 = var2.getByteBuffer();
         var4 = encodeAsMuchAsPossible(var0, var1, true, var2, var4);
         flushRemainingBytes(var0, var2, var4);
      }
   }

   private static ByteBuffer writeAndEncodeAsMuchAsPossible(
      CharsetEncoder var0, CharBuffer var1, boolean var2, ByteBufferDestination var3, ByteBuffer var4, CoderResult var5
   ) {
      while (true) {
         var4 = drainIfByteBufferFull(var3, var4, var5);
         if (!var5.isOverflow()) {
            if (!var5.isUnderflow()) {
               throwException(var5);
            }

            return var4;
         }

         var5 = var0.encode(var1, var4, var2);
      }
   }

   private static void throwException(CoderResult var0) {
      try {
         var0.throwException();
      } catch (CharacterCodingException var2) {
         throw new IllegalStateException(var2);
      }
   }

   private static ByteBuffer encodeAsMuchAsPossible(CharsetEncoder var0, CharBuffer var1, boolean var2, ByteBufferDestination var3, ByteBuffer var4) {
      CoderResult var5;
      do {
         var5 = var0.encode(var1, var4, var2);
         var4 = drainIfByteBufferFull(var3, var4, var5);
      } while (var5.isOverflow());

      if (!var5.isUnderflow()) {
         throwException(var5);
      }

      return var4;
   }

   private static ByteBuffer drainIfByteBufferFull(ByteBufferDestination var0, ByteBuffer var1, CoderResult var2) {
      if (var2.isOverflow()) {
         synchronized (var0) {
            ByteBuffer var4 = var0.getByteBuffer();
            if (var4 != var1) {
               ((Buffer)var1).flip();
               ByteBufferDestinationHelper.writeToUnsynchronized(var1, var0);
               ((Buffer)var1).clear();
               return var0.getByteBuffer();
            } else {
               return var0.drain(var4);
            }
         }
      } else {
         return var1;
      }
   }

   private static void flushRemainingBytes(CharsetEncoder var0, ByteBufferDestination var1, ByteBuffer var2) {
      CoderResult var3;
      do {
         var3 = var0.flush(var2);
         var2 = drainIfByteBufferFull(var1, var2, var3);
      } while (var3.isOverflow());

      if (!var3.isUnderflow()) {
         throwException(var3);
      }

      if (var2.remaining() > 0 && var2 != var1.getByteBuffer()) {
         ((Buffer)var2).flip();
         ByteBufferDestinationHelper.writeToUnsynchronized(var2, var1);
         ((Buffer)var2).clear();
      }
   }

   static int copy(StringBuilder var0, int var1, CharBuffer var2) {
      int var3 = Math.min(var0.length() - var1, var2.remaining());
      char[] var4 = var2.array();
      int var5 = var2.position();
      var0.getChars(var1, var1 + var3, var4, var2.arrayOffset() + var5);
      ((Buffer)var2).position(var5 + var3);
      return var3;
   }
}
