package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

public class NonBlocking {
   public static NonBlockingPumpReader nonBlockingPumpReader() {
      return new NonBlockingPumpReader();
   }

   public static NonBlockingPumpReader nonBlockingPumpReader(int var0) {
      return new NonBlockingPumpReader(var0);
   }

   public static NonBlockingPumpInputStream nonBlockingPumpInputStream() {
      return new NonBlockingPumpInputStream();
   }

   public static NonBlockingPumpInputStream nonBlockingPumpInputStream(int var0) {
      return new NonBlockingPumpInputStream(var0);
   }

   public static NonBlockingInputStream nonBlockingStream(NonBlockingReader var0, Charset var1) {
      return new NonBlocking.NonBlockingReaderInputStream(var0, var1);
   }

   public static NonBlockingInputStream nonBlocking(String var0, InputStream var1) {
      return (NonBlockingInputStream)(var1 instanceof NonBlockingInputStream ? (NonBlockingInputStream)var1 : new NonBlockingInputStreamImpl(var0, var1));
   }

   public static NonBlockingReader nonBlocking(String var0, Reader var1) {
      return (NonBlockingReader)(var1 instanceof NonBlockingReader ? (NonBlockingReader)var1 : new NonBlockingReaderImpl(var0, var1));
   }

   public static NonBlockingReader nonBlocking(String var0, InputStream var1, Charset var2) {
      return new NonBlocking.NonBlockingInputStreamReader(nonBlocking(var0, var1), var2);
   }

   private static class NonBlockingInputStreamReader extends NonBlockingReader {
      private final NonBlockingInputStream input;
      private final CharsetDecoder decoder;
      private final ByteBuffer bytes;
      private final CharBuffer chars;

      public NonBlockingInputStreamReader(NonBlockingInputStream var1, Charset var2) {
         this(
            var1,
            (var2 != null ? var2 : Charset.defaultCharset())
               .newDecoder()
               .onMalformedInput(CodingErrorAction.REPLACE)
               .onUnmappableCharacter(CodingErrorAction.REPLACE)
         );
      }

      public NonBlockingInputStreamReader(NonBlockingInputStream var1, CharsetDecoder var2) {
         this.input = var1;
         this.decoder = var2;
         this.bytes = ByteBuffer.allocate(4);
         this.chars = CharBuffer.allocate(2);
         ((Buffer)this.bytes).limit(0);
         ((Buffer)this.chars).limit(0);
      }

      @Override
      protected int read(long var1, boolean var3) throws IOException {
         boolean var4 = var1 <= 0L;

         while (!this.chars.hasRemaining() && (var4 || var1 > 0L)) {
            long var5 = 0L;
            if (!var4) {
               var5 = System.currentTimeMillis();
            }

            int var7 = this.input.read(var1);
            if (var7 == -1) {
               return -1;
            }

            if (var7 >= 0) {
               if (!this.bytes.hasRemaining()) {
                  ((Buffer)this.bytes).position(0);
                  ((Buffer)this.bytes).limit(0);
               }

               int var8 = this.bytes.limit();
               this.bytes.array()[this.bytes.arrayOffset() + var8] = (byte)var7;
               ((Buffer)this.bytes).limit(var8 + 1);
               ((Buffer)this.chars).clear();
               this.decoder.decode(this.bytes, this.chars, false);
               ((Buffer)this.chars).flip();
            }

            if (!var4) {
               var1 -= System.currentTimeMillis() - var5;
            }
         }

         if (this.chars.hasRemaining()) {
            return var3 ? this.chars.get(this.chars.position()) : this.chars.get();
         } else {
            return -2;
         }
      }

      @Override
      public void shutdown() {
         this.input.shutdown();
      }

      @Override
      public void close() throws IOException {
         this.input.close();
      }
   }

   private static class NonBlockingReaderInputStream extends NonBlockingInputStream {
      private final NonBlockingReader reader;
      private final CharsetEncoder encoder;
      private final ByteBuffer bytes;
      private final CharBuffer chars;

      private NonBlockingReaderInputStream(NonBlockingReader var1, Charset var2) {
         this.reader = var1;
         this.encoder = var2.newEncoder().onUnmappableCharacter(CodingErrorAction.REPLACE).onMalformedInput(CodingErrorAction.REPLACE);
         this.bytes = ByteBuffer.allocate(4);
         this.chars = CharBuffer.allocate(2);
         ((Buffer)this.bytes).limit(0);
         ((Buffer)this.chars).limit(0);
      }

      @Override
      public int available() {
         return (int)((float)this.reader.available() * this.encoder.averageBytesPerChar()) + this.bytes.remaining();
      }

      @Override
      public void close() throws IOException {
         this.reader.close();
      }

      @Override
      public int read(long var1, boolean var3) throws IOException {
         boolean var4 = var1 <= 0L;

         while (!this.bytes.hasRemaining() && (var4 || var1 > 0L)) {
            long var5 = 0L;
            if (!var4) {
               var5 = System.currentTimeMillis();
            }

            int var7 = this.reader.read(var1);
            if (var7 == -1) {
               return -1;
            }

            if (var7 >= 0) {
               if (!this.chars.hasRemaining()) {
                  ((Buffer)this.chars).position(0);
                  ((Buffer)this.chars).limit(0);
               }

               int var8 = this.chars.limit();
               this.chars.array()[this.chars.arrayOffset() + var8] = (char)var7;
               ((Buffer)this.chars).limit(var8 + 1);
               ((Buffer)this.bytes).clear();
               this.encoder.encode(this.chars, this.bytes, false);
               ((Buffer)this.bytes).flip();
            }

            if (!var4) {
               var1 -= System.currentTimeMillis() - var5;
            }
         }

         if (this.bytes.hasRemaining()) {
            return var3 ? this.bytes.get(this.bytes.position()) : this.bytes.get();
         } else {
            return -2;
         }
      }
   }
}
