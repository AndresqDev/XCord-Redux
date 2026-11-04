package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.layout.ByteBufferDestinationHelper;
import org.apache.logging.log4j.core.util.Constants;

public class OutputStreamManager extends AbstractManager implements ByteBufferDestination {
   protected final Layout<?> layout;
   protected ByteBuffer byteBuffer;
   private volatile OutputStream outputStream;
   private boolean skipFooter;

   protected OutputStreamManager(OutputStream var1, String var2, Layout<?> var3, boolean var4) {
      this(var1, var2, var3, var4, Constants.ENCODER_BYTE_BUFFER_SIZE);
   }

   protected OutputStreamManager(OutputStream var1, String var2, Layout<?> var3, boolean var4, int var5) {
      this(var1, var2, var3, var4, ByteBuffer.wrap(new byte[var5]));
   }

   @Deprecated
   protected OutputStreamManager(OutputStream var1, String var2, Layout<?> var3, boolean var4, ByteBuffer var5) {
      super(null, var2);
      this.outputStream = var1;
      this.layout = var3;
      if (var4) {
         this.writeHeader(var1);
      }

      this.byteBuffer = Objects.requireNonNull(var5, "byteBuffer");
   }

   protected OutputStreamManager(
      LoggerContext var1, OutputStream var2, String var3, boolean var4, Layout<? extends Serializable> var5, boolean var6, ByteBuffer var7
   ) {
      super(var1, var3);
      if (var4 && var2 != null) {
         LOGGER.error("Invalid OutputStreamManager configuration for '{}': You cannot both set the OutputStream and request on-demand.", var3);
      }

      this.layout = var5;
      this.byteBuffer = Objects.requireNonNull(var7, "byteBuffer");
      this.outputStream = var2;
      if (var6) {
         this.writeHeader(var2);
      }
   }

   public static <T> OutputStreamManager getManager(String var0, T var1, ManagerFactory<? extends OutputStreamManager, T> var2) {
      return AbstractManager.getManager(var0, var2, var1);
   }

   protected OutputStream createOutputStream() throws IOException {
      throw new IllegalStateException(this.getClass().getCanonicalName() + " must implement createOutputStream()");
   }

   public void skipFooter(boolean var1) {
      this.skipFooter = var1;
   }

   @Override
   public boolean releaseSub(long var1, TimeUnit var3) {
      this.writeFooter();
      return this.closeOutputStream();
   }

   protected void writeHeader(OutputStream var1) {
      if (this.layout != null && var1 != null) {
         byte[] var2 = this.layout.getHeader();
         if (var2 != null) {
            try {
               var1.write(var2, 0, var2.length);
            } catch (IOException var4) {
               this.logError("Unable to write header", var4);
            }
         }
      }
   }

   protected void writeFooter() {
      if (this.layout != null && !this.skipFooter) {
         byte[] var1 = this.layout.getFooter();
         if (var1 != null) {
            this.write(var1);
         }
      }
   }

   public boolean isOpen() {
      return this.getCount() > 0;
   }

   public boolean hasOutputStream() {
      return this.outputStream != null;
   }

   protected OutputStream getOutputStream() throws IOException {
      if (this.outputStream == null) {
         this.outputStream = this.createOutputStream();
      }

      return this.outputStream;
   }

   protected void setOutputStream(OutputStream var1) {
      this.outputStream = var1;
   }

   protected void write(byte[] var1) {
      this.write(var1, 0, var1.length, false);
   }

   protected void write(byte[] var1, boolean var2) {
      this.write(var1, 0, var1.length, var2);
   }

   @Override
   public void writeBytes(byte[] var1, int var2, int var3) {
      this.write(var1, var2, var3, false);
   }

   protected void write(byte[] var1, int var2, int var3) {
      this.writeBytes(var1, var2, var3);
   }

   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      if (var4 && this.byteBuffer.position() == 0) {
         this.writeToDestination(var1, var2, var3);
         this.flushDestination();
      } else {
         if (var3 >= this.byteBuffer.capacity()) {
            this.flush();
            this.writeToDestination(var1, var2, var3);
         } else {
            if (var3 > this.byteBuffer.remaining()) {
               this.flush();
            }

            this.byteBuffer.put(var1, var2, var3);
         }

         if (var4) {
            this.flush();
         }
      }
   }

   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      try {
         this.getOutputStream().write(var1, var2, var3);
      } catch (IOException var5) {
         throw new AppenderLoggingException("Error writing to stream " + this.getName(), var5);
      }
   }

   protected synchronized void flushDestination() {
      OutputStream var1 = this.outputStream;
      if (var1 != null) {
         try {
            var1.flush();
         } catch (IOException var3) {
            throw new AppenderLoggingException("Error flushing stream " + this.getName(), var3);
         }
      }
   }

   protected synchronized void flushBuffer(ByteBuffer var1) {
      ((Buffer)var1).flip();

      try {
         if (var1.remaining() > 0) {
            this.writeToDestination(var1.array(), var1.arrayOffset() + var1.position(), var1.remaining());
         }
      } finally {
         ((Buffer)var1).clear();
      }
   }

   public synchronized void flush() {
      this.flushBuffer(this.byteBuffer);
      this.flushDestination();
   }

   protected synchronized boolean closeOutputStream() {
      this.flush();
      OutputStream var1 = this.outputStream;
      if (var1 != null && var1 != System.out && var1 != System.err) {
         try {
            var1.close();
            LOGGER.debug("OutputStream closed");
            return true;
         } catch (IOException var3) {
            this.logError("Unable to close stream", var3);
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   @Override
   public ByteBuffer drain(ByteBuffer var1) {
      this.flushBuffer(var1);
      return var1;
   }

   @Override
   public void writeBytes(ByteBuffer var1) {
      if (var1.remaining() != 0) {
         synchronized (this) {
            ByteBufferDestinationHelper.writeToUnsynchronized(var1, this);
         }
      }
   }
}
