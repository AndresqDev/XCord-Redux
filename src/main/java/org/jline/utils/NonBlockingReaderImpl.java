package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;

public class NonBlockingReaderImpl extends NonBlockingReader {
   public static final int READ_EXPIRED = -2;
   private Reader in;
   private int ch = -2;
   private String name;
   private boolean threadIsReading = false;
   private IOException exception = null;
   private long threadDelay = 60000L;
   private Thread thread;

   public NonBlockingReaderImpl(String var1, Reader var2) {
      this.in = var2;
      this.name = var1;
   }

   private synchronized void startReadingThreadIfNeeded() {
      if (this.thread == null) {
         this.thread = new Thread(this::run);
         this.thread.setName(this.name + " non blocking reader thread");
         this.thread.setDaemon(true);
         this.thread.start();
      }
   }

   @Override
   public synchronized void shutdown() {
      if (this.thread != null) {
         this.notify();
      }
   }

   @Override
   public void close() throws IOException {
      this.in.close();
      this.shutdown();
   }

   @Override
   public synchronized boolean ready() throws IOException {
      return this.ch >= 0 || this.in.ready();
   }

   @Override
   protected synchronized int read(long var1, boolean var3) throws IOException {
      if (this.exception != null) {
         assert this.ch == -2;

         IOException var10 = this.exception;
         if (!var3) {
            this.exception = null;
         }

         throw var10;
      } else {
         if (this.ch >= -1) {
            assert this.exception == null;
         } else if (!var3 && var1 <= 0L && !this.threadIsReading) {
            this.ch = this.in.read();
         } else {
            if (!this.threadIsReading) {
               this.threadIsReading = true;
               this.startReadingThreadIfNeeded();
               this.notifyAll();
            }

            boolean var4 = var1 <= 0L;

            while (var4 || var1 > 0L) {
               long var5 = System.currentTimeMillis();

               try {
                  if (Thread.interrupted()) {
                     throw new InterruptedException();
                  }

                  this.wait(var1);
               } catch (InterruptedException var8) {
                  this.exception = (IOException)new InterruptedIOException().initCause(var8);
               }

               if (this.exception != null) {
                  assert this.ch == -2;

                  IOException var7 = this.exception;
                  if (!var3) {
                     this.exception = null;
                  }

                  throw var7;
               }

               if (this.ch >= -1) {
                  assert this.exception == null;
                  break;
               }

               if (!var4) {
                  var1 -= System.currentTimeMillis() - var5;
               }
            }
         }

         int var9 = this.ch;
         if (!var3) {
            this.ch = -2;
         }

         return var9;
      }
   }

   private void run() {
      Log.debug("NonBlockingReader start");

      try {
         while (true) {
            synchronized (this) {
               boolean var1 = this.threadIsReading;

               try {
                  if (!var1) {
                     this.wait(this.threadDelay);
                  }
               } catch (InterruptedException var21) {
               }

               var1 = this.threadIsReading;
               if (!var1) {
                  return;
               }
            }

            int var2 = -2;
            IOException var3 = null;

            try {
               var2 = this.in.read();
            } catch (IOException var23) {
               var3 = var23;
            }

            synchronized (this) {
               this.exception = var3;
               this.ch = var2;
               this.threadIsReading = false;
               this.notify();
            }
         }
      } catch (Throwable var25) {
         Log.warn("Error in NonBlockingReader thread", var25);
      } finally {
         Log.debug("NonBlockingReader shutdown");
         synchronized (this) {
            this.thread = null;
            this.threadIsReading = false;
         }
      }
   }

   public synchronized void clear() throws IOException {
      while (this.ready()) {
         this.read();
      }
   }
}
