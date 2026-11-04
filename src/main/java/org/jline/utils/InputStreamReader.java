package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;

public class InputStreamReader extends Reader {
   private InputStream in;
   private static final int BUFFER_SIZE = 4;
   private boolean endOfInput = false;
   CharsetDecoder decoder;
   ByteBuffer bytes = ByteBuffer.allocate(4);
   char pending = '\uffff';

   public InputStreamReader(InputStream var1) {
      super(var1);
      this.in = var1;
      this.decoder = Charset.defaultCharset().newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      ((Buffer)this.bytes).limit(0);
   }

   public InputStreamReader(InputStream var1, String var2) throws UnsupportedEncodingException {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.in = var1;

         try {
            this.decoder = Charset.forName(var2).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         } catch (IllegalArgumentException var4) {
            throw (UnsupportedEncodingException)new UnsupportedEncodingException(var2).initCause(var4);
         }

         ((Buffer)this.bytes).limit(0);
      }
   }

   public InputStreamReader(InputStream var1, CharsetDecoder var2) {
      super(var1);
      var2.averageCharsPerByte();
      this.in = var1;
      this.decoder = var2;
      ((Buffer)this.bytes).limit(0);
   }

   public InputStreamReader(InputStream var1, Charset var2) {
      super(var1);
      this.in = var1;
      this.decoder = var2.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      ((Buffer)this.bytes).limit(0);
   }

   @Override
   public void close() throws IOException {
      synchronized (this.lock) {
         this.decoder = null;
         if (this.in != null) {
            this.in.close();
            this.in = null;
         }
      }
   }

   public String getEncoding() {
      return !this.isOpen() ? null : this.decoder.charset().name();
   }

   @Override
   public int read() throws IOException {
      synchronized (this.lock) {
         if (!this.isOpen()) {
            throw new ClosedException("InputStreamReader is closed.");
         } else if (this.pending != '\uffff') {
            char var6 = this.pending;
            this.pending = '\uffff';
            return var6;
         } else {
            char[] var2 = new char[2];
            int var3 = this.read(var2, 0, 2);
            if (var3 == 2) {
               this.pending = var2[1];
            }

            return var3 > 0 ? var2[0] : -1;
         }
      }
   }

   @Override
   public int read(char[] var1, int var2, int var3) throws IOException {
      synchronized (this.lock) {
         if (!this.isOpen()) {
            throw new IOException("InputStreamReader is closed.");
         } else if (var2 < 0 || var2 > var1.length - var3 || var3 < 0) {
            throw new IndexOutOfBoundsException();
         } else if (var3 == 0) {
            return 0;
         } else {
            CharBuffer var5 = CharBuffer.wrap(var1, var2, var3);
            CoderResult var6 = CoderResult.UNDERFLOW;

            for (boolean var7 = !this.bytes.hasRemaining(); var5.position() == var2; var7 = true) {
               if (var7) {
                  try {
                     if (this.in.available() == 0 && var5.position() > var2) {
                        break;
                     }
                  } catch (IOException var11) {
                  }

                  int var8 = this.bytes.arrayOffset() + this.bytes.limit();
                  int var9 = this.in.read(this.bytes.array(), var8, 1);
                  if (var9 == -1) {
                     this.endOfInput = true;
                     break;
                  }

                  if (var9 == 0) {
                     break;
                  }

                  ((Buffer)this.bytes).limit(this.bytes.limit() + var9);
               }

               var6 = this.decoder.decode(this.bytes, var5, false);
               if (!var6.isUnderflow()) {
                  break;
               }

               if (this.bytes.limit() == this.bytes.capacity()) {
                  this.bytes.compact();
                  ((Buffer)this.bytes).limit(this.bytes.position());
                  ((Buffer)this.bytes).position(0);
               }
            }

            if (var6 == CoderResult.UNDERFLOW && this.endOfInput) {
               var6 = this.decoder.decode(this.bytes, var5, true);
               this.decoder.flush(var5);
               this.decoder.reset();
            }

            if (var6.isMalformed()) {
               throw new MalformedInputException(var6.length());
            } else if (var6.isUnmappable()) {
               throw new UnmappableCharacterException(var6.length());
            } else {
               return var5.position() - var2 == 0 ? -1 : var5.position() - var2;
            }
         }
      }
   }

   private boolean isOpen() {
      return this.in != null;
   }

   @Override
   public boolean ready() throws IOException {
      synchronized (this.lock) {
         if (this.in == null) {
            throw new IOException("InputStreamReader is closed.");
         } else {
            boolean var10000;
            try {
               var10000 = this.bytes.hasRemaining() || this.in.available() > 0;
            } catch (IOException var4) {
               return false;
            }

            return var10000;
         }
      }
   }
}
