package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.Args;

public class EofSensorInputStream extends InputStream implements ConnectionReleaseTrigger {
   protected InputStream wrappedStream;
   private boolean selfClosed;
   private final EofSensorWatcher eofWatcher;

   public EofSensorInputStream(InputStream var1, EofSensorWatcher var2) {
      Args.notNull(var1, "Wrapped stream");
      this.wrappedStream = var1;
      this.selfClosed = false;
      this.eofWatcher = var2;
   }

   boolean isSelfClosed() {
      return this.selfClosed;
   }

   InputStream getWrappedStream() {
      return this.wrappedStream;
   }

   protected boolean isReadAllowed() throws IOException {
      if (this.selfClosed) {
         throw new IOException("Attempted read on closed stream.");
      } else {
         return this.wrappedStream != null;
      }
   }

   @Override
   public int read() throws IOException {
      int var1 = -1;
      if (this.isReadAllowed()) {
         try {
            var1 = this.wrappedStream.read();
            this.checkEOF(var1);
         } catch (IOException var3) {
            this.checkAbort();
            throw var3;
         }
      }

      return var1;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = -1;
      if (this.isReadAllowed()) {
         try {
            var4 = this.wrappedStream.read(var1, var2, var3);
            this.checkEOF(var4);
         } catch (IOException var6) {
            this.checkAbort();
            throw var6;
         }
      }

      return var4;
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   @Override
   public int available() throws IOException {
      int var1 = 0;
      if (this.isReadAllowed()) {
         try {
            var1 = this.wrappedStream.available();
         } catch (IOException var3) {
            this.checkAbort();
            throw var3;
         }
      }

      return var1;
   }

   @Override
   public void close() throws IOException {
      this.selfClosed = true;
      this.checkClose();
   }

   protected void checkEOF(int var1) throws IOException {
      InputStream var2 = this.wrappedStream;
      if (var2 != null && var1 < 0) {
         try {
            boolean var3 = true;
            if (this.eofWatcher != null) {
               var3 = this.eofWatcher.eofDetected(var2);
            }

            if (var3) {
               var2.close();
            }
         } finally {
            this.wrappedStream = null;
         }
      }
   }

   protected void checkClose() throws IOException {
      InputStream var1 = this.wrappedStream;
      if (var1 != null) {
         try {
            boolean var2 = true;
            if (this.eofWatcher != null) {
               var2 = this.eofWatcher.streamClosed(var1);
            }

            if (var2) {
               var1.close();
            }
         } finally {
            this.wrappedStream = null;
         }
      }
   }

   protected void checkAbort() throws IOException {
      InputStream var1 = this.wrappedStream;
      if (var1 != null) {
         try {
            boolean var2 = true;
            if (this.eofWatcher != null) {
               var2 = this.eofWatcher.streamAbort(var1);
            }

            if (var2) {
               var1.close();
            }
         } finally {
            this.wrappedStream = null;
         }
      }
   }

   @Override
   public void releaseConnection() throws IOException {
      this.close();
   }

   @Override
   public void abortConnection() throws IOException {
      this.selfClosed = true;
      this.checkAbort();
   }
}
