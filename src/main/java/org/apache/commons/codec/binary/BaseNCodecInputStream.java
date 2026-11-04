package org.apache.commons.codec.binary;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BaseNCodecInputStream extends FilterInputStream {
   private final BaseNCodec baseNCodec;
   private final boolean doEncode;
   private final byte[] singleByte = new byte[1];
   private final BaseNCodec.Context context = new BaseNCodec.Context();

   protected BaseNCodecInputStream(InputStream var1, BaseNCodec var2, boolean var3) {
      super(var1);
      this.doEncode = var3;
      this.baseNCodec = var2;
   }

   @Override
   public int available() throws IOException {
      return this.context.eof ? 0 : 1;
   }

   @Override
   public synchronized void mark(int var1) {
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public int read() throws IOException {
      int var1 = this.read(this.singleByte, 0, 1);

      while (var1 == 0) {
         var1 = this.read(this.singleByte, 0, 1);
      }

      if (var1 > 0) {
         byte var2 = this.singleByte[0];
         return var2 < 0 ? 256 + var2 : var2;
      } else {
         return -1;
      }
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0) {
         if (var2 <= var1.length && var2 + var3 <= var1.length) {
            if (var3 == 0) {
               return 0;
            } else {
               int var4 = 0;

               while (var4 == 0) {
                  if (!this.baseNCodec.hasData(this.context)) {
                     byte[] var5 = new byte[this.doEncode ? 4096 : 8192];
                     int var6 = this.in.read(var5);
                     if (this.doEncode) {
                        this.baseNCodec.encode(var5, 0, var6, this.context);
                     } else {
                        this.baseNCodec.decode(var5, 0, var6, this.context);
                     }
                  }

                  var4 = this.baseNCodec.readResults(var1, var2, var3, this.context);
               }

               return var4;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public synchronized void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   @Override
   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Negative skip length: " + var1);
      } else {
         byte[] var3 = new byte[512];
         long var4 = var1;

         while (var4 > 0L) {
            int var6 = (int)Math.min((long)var3.length, var4);
            var6 = this.read(var3, 0, var6);
            if (var6 == -1) {
               break;
            }

            var4 -= (long)var6;
         }

         return var1 - var4;
      }
   }
}
