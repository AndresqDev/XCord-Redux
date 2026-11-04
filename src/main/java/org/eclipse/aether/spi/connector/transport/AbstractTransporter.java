package org.eclipse.aether.spi.connector.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.aether.transfer.TransferCancelledException;

public abstract class AbstractTransporter implements Transporter {
   private final AtomicBoolean closed = new AtomicBoolean();

   protected AbstractTransporter() {
   }

   @Override
   public void peek(PeekTask var1) throws Exception {
      this.failIfClosed(var1);
      this.implPeek(var1);
   }

   protected abstract void implPeek(PeekTask var1) throws Exception;

   @Override
   public void get(GetTask var1) throws Exception {
      this.failIfClosed(var1);
      this.implGet(var1);
   }

   protected abstract void implGet(GetTask var1) throws Exception;

   protected void utilGet(GetTask var1, InputStream var2, boolean var3, long var4, boolean var6) throws IOException, TransferCancelledException {
      OutputStream var7 = null;

      try {
         var7 = var1.newOutputStream(var6);
         var1.getListener().transportStarted(var6 ? var1.getResumeOffset() : 0L, var4);
         copy(var7, var2, var1.getListener());
         var7.close();
         var7 = null;
         if (var3) {
            var2.close();
            var2 = null;
         }
      } finally {
         try {
            if (var7 != null) {
               var7.close();
            }
         } catch (IOException var43) {
         } finally {
            try {
               if (var3 && var2 != null) {
                  var2.close();
               }
            } catch (IOException var42) {
            }
         }
      }
   }

   @Override
   public void put(PutTask var1) throws Exception {
      this.failIfClosed(var1);
      this.implPut(var1);
   }

   protected abstract void implPut(PutTask var1) throws Exception;

   protected void utilPut(PutTask var1, OutputStream var2, boolean var3) throws IOException, TransferCancelledException {
      InputStream var4 = null;

      try {
         var1.getListener().transportStarted(0L, var1.getDataLength());
         var4 = var1.newInputStream();
         copy(var2, var4, var1.getListener());
         if (var3) {
            var2.close();
         } else {
            var2.flush();
         }

         var2 = null;
         var4.close();
         var4 = null;
      } finally {
         try {
            if (var3 && var2 != null) {
               var2.close();
            }
         } catch (IOException var40) {
         } finally {
            try {
               if (var4 != null) {
                  var4.close();
               }
            } catch (IOException var39) {
            }
         }
      }
   }

   @Override
   public void close() {
      if (this.closed.compareAndSet(false, true)) {
         this.implClose();
      }
   }

   protected abstract void implClose();

   private void failIfClosed(TransportTask var1) {
      if (this.closed.get()) {
         throw new IllegalStateException("transporter closed, cannot execute task " + var1);
      }
   }

   private static void copy(OutputStream var0, InputStream var1, TransportListener var2) throws IOException, TransferCancelledException {
      ByteBuffer var3 = ByteBuffer.allocate(32768);
      byte[] var4 = var3.array();

      for (int var5 = var1.read(var4); var5 >= 0; var5 = var1.read(var4)) {
         var0.write(var4, 0, var5);
         ((Buffer)var3).rewind();
         ((Buffer)var3).limit(var5);
         var2.transportProgressed(var3);
      }
   }
}
