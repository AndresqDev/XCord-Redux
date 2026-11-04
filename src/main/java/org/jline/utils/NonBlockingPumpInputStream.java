package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class NonBlockingPumpInputStream extends NonBlockingInputStream {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private final ByteBuffer readBuffer;
   private final ByteBuffer writeBuffer;
   private final OutputStream output;
   private boolean closed;
   private IOException ioException;

   public NonBlockingPumpInputStream() {
      this(4096);
   }

   public NonBlockingPumpInputStream(int var1) {
      byte[] var2 = new byte[var1];
      this.readBuffer = ByteBuffer.wrap(var2);
      this.writeBuffer = ByteBuffer.wrap(var2);
      this.output = new NonBlockingPumpInputStream.NbpOutputStream();
      ((Buffer)this.readBuffer).limit(0);
   }

   public OutputStream getOutputStream() {
      return this.output;
   }

   private int wait(ByteBuffer var1, long var2) throws IOException {
      boolean var4 = var2 <= 0L;
      long var5 = 0L;
      if (!var4) {
         var5 = System.currentTimeMillis() + var2;
      }

      while (!this.closed && !var1.hasRemaining() && (var4 || var2 > 0L)) {
         this.notifyAll();

         try {
            this.wait(var2);
            this.checkIoException();
         } catch (InterruptedException var8) {
            this.checkIoException();
            throw new InterruptedIOException();
         }

         if (!var4) {
            var2 = var5 - System.currentTimeMillis();
         }
      }

      return var1.hasRemaining() ? 0 : (this.closed ? -1 : -2);
   }

   private static boolean rewind(ByteBuffer var0, ByteBuffer var1) {
      if (var0.position() > var1.position()) {
         ((Buffer)var1).limit(var0.position());
      }

      if (var0.position() == var0.capacity()) {
         ((Buffer)var0).rewind();
         ((Buffer)var0).limit(var1.position());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public synchronized int available() {
      int var1 = this.readBuffer.remaining();
      if (this.writeBuffer.position() < this.readBuffer.position()) {
         var1 += this.writeBuffer.position();
      }

      return var1;
   }

   @Override
   public synchronized int read(long var1, boolean var3) throws IOException {
      this.checkIoException();
      int var4 = this.wait(this.readBuffer, var1);
      if (var4 >= 0) {
         var4 = this.readBuffer.get() & 255;
      }

      rewind(this.readBuffer, this.writeBuffer);
      return var4;
   }

   public synchronized void setIoException(IOException var1) {
      this.ioException = var1;
      this.notifyAll();
   }

   protected synchronized void checkIoException() throws IOException {
      if (this.ioException != null) {
         throw this.ioException;
      }
   }

   synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      while (var3 > 0) {
         if (this.wait(this.writeBuffer, 0L) == -1) {
            throw new ClosedException();
         }

         int var4 = Math.min(var3, this.writeBuffer.remaining());
         this.writeBuffer.put(var1, var2, var4);
         var2 += var4;
         var3 -= var4;
         rewind(this.writeBuffer, this.readBuffer);
      }
   }

   synchronized void flush() {
      if (this.readBuffer.hasRemaining()) {
         this.notifyAll();
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this.closed = true;
      this.notifyAll();
   }

   private class NbpOutputStream extends OutputStream {
      private NbpOutputStream() {
      }

      @Override
      public void write(int var1) throws IOException {
         NonBlockingPumpInputStream.this.write(new byte[]{(byte)var1}, 0, 1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         NonBlockingPumpInputStream.this.write(var1, var2, var3);
      }

      @Override
      public void flush() throws IOException {
         NonBlockingPumpInputStream.this.flush();
      }

      @Override
      public void close() throws IOException {
         NonBlockingPumpInputStream.this.close();
      }
   }
}
