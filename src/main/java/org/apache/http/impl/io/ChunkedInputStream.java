package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.MalformedChunkCodingException;
import org.apache.http.TruncatedChunkException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class ChunkedInputStream extends InputStream {
   private static final int CHUNK_LEN = 1;
   private static final int CHUNK_DATA = 2;
   private static final int CHUNK_CRLF = 3;
   private static final int CHUNK_INVALID = Integer.MAX_VALUE;
   private static final int BUFFER_SIZE = 2048;
   private final SessionInputBuffer in;
   private final CharArrayBuffer buffer;
   private final MessageConstraints constraints;
   private int state;
   private long chunkSize;
   private long pos;
   private boolean eof = false;
   private boolean closed = false;
   private Header[] footers = new Header[0];

   public ChunkedInputStream(SessionInputBuffer var1, MessageConstraints var2) {
      this.in = Args.notNull(var1, "Session input buffer");
      this.pos = 0L;
      this.buffer = new CharArrayBuffer(16);
      this.constraints = var2 != null ? var2 : MessageConstraints.DEFAULT;
      this.state = 1;
   }

   public ChunkedInputStream(SessionInputBuffer var1) {
      this(var1, null);
   }

   @Override
   public int available() throws IOException {
      if (this.in instanceof BufferInfo) {
         int var1 = ((BufferInfo)this.in).length();
         return (int)Math.min((long)var1, this.chunkSize - this.pos);
      } else {
         return 0;
      }
   }

   @Override
   public int read() throws IOException {
      if (this.closed) {
         throw new IOException("Attempted read from closed stream.");
      } else if (this.eof) {
         return -1;
      } else {
         if (this.state != 2) {
            this.nextChunk();
            if (this.eof) {
               return -1;
            }
         }

         int var1 = this.in.read();
         if (var1 != -1) {
            this.pos++;
            if (this.pos >= this.chunkSize) {
               this.state = 3;
            }
         }

         return var1;
      }
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         throw new IOException("Attempted read from closed stream.");
      } else if (this.eof) {
         return -1;
      } else {
         if (this.state != 2) {
            this.nextChunk();
            if (this.eof) {
               return -1;
            }
         }

         int var4 = this.in.read(var1, var2, (int)Math.min((long)var3, this.chunkSize - this.pos));
         if (var4 != -1) {
            this.pos += (long)var4;
            if (this.pos >= this.chunkSize) {
               this.state = 3;
            }

            return var4;
         } else {
            this.eof = true;
            throw new TruncatedChunkException("Truncated chunk (expected size: %,d; actual size: %,d)", this.chunkSize, this.pos);
         }
      }
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   private void nextChunk() throws IOException {
      if (this.state == Integer.MAX_VALUE) {
         throw new MalformedChunkCodingException("Corrupt data stream");
      } else {
         try {
            this.chunkSize = this.getChunkSize();
            if (this.chunkSize < 0L) {
               throw new MalformedChunkCodingException("Negative chunk size");
            } else {
               this.state = 2;
               this.pos = 0L;
               if (this.chunkSize == 0L) {
                  this.eof = true;
                  this.parseTrailerHeaders();
               }
            }
         } catch (MalformedChunkCodingException var2) {
            this.state = Integer.MAX_VALUE;
            throw var2;
         }
      }
   }

   private long getChunkSize() throws IOException {
      int var1 = this.state;
      switch (var1) {
         case 3:
            this.buffer.clear();
            int var2 = this.in.readLine(this.buffer);
            if (var2 == -1) {
               throw new MalformedChunkCodingException("CRLF expected at end of chunk");
            } else if (!this.buffer.isEmpty()) {
               throw new MalformedChunkCodingException("Unexpected content at the end of chunk");
            } else {
               this.state = 1;
            }
         case 1:
            this.buffer.clear();
            int var3 = this.in.readLine(this.buffer);
            if (var3 == -1) {
               throw new ConnectionClosedException("Premature end of chunk coded message body: closing chunk expected");
            } else {
               int var4 = this.buffer.indexOf(59);
               if (var4 < 0) {
                  var4 = this.buffer.length();
               }

               String var5 = this.buffer.substringTrimmed(0, var4);

               try {
                  return Long.parseLong(var5, 16);
               } catch (NumberFormatException var7) {
                  throw new MalformedChunkCodingException("Bad chunk header: " + var5);
               }
            }
         default:
            throw new IllegalStateException("Inconsistent codec state");
      }
   }

   private void parseTrailerHeaders() throws IOException {
      try {
         this.footers = AbstractMessageParser.parseHeaders(this.in, this.constraints.getMaxHeaderCount(), this.constraints.getMaxLineLength(), null);
      } catch (HttpException var3) {
         MalformedChunkCodingException var2 = new MalformedChunkCodingException("Invalid footer: " + var3.getMessage());
         var2.initCause(var3);
         throw var2;
      }
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         try {
            if (!this.eof && this.state != Integer.MAX_VALUE) {
               byte[] var1 = new byte[2048];

               while (this.read(var1) >= 0) {
               }
            }
         } finally {
            this.eof = true;
            this.closed = true;
         }
      }
   }

   public Header[] getFooters() {
      return (Header[])this.footers.clone();
   }
}
