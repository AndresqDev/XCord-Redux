package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamFeeder extends AbstractStreamHandler {
   private InputStream input;
   private OutputStream output;
   private volatile Throwable exception = null;

   public StreamFeeder(InputStream var1, OutputStream var2) {
      this.input = var1;
      this.output = var2;
   }

   @Override
   public void run() {
      try {
         this.feed();
      } catch (Throwable var13) {
         if (this.exception == null) {
            this.exception = var13;
         }
      } finally {
         this.close();
         synchronized (this) {
            this.setDone();
            this.notifyAll();
         }
      }
   }

   public void close() {
      if (this.input != null) {
         synchronized (this.input) {
            try {
               this.input.close();
            } catch (IOException var7) {
               if (this.exception == null) {
                  this.exception = var7;
               }
            }

            this.input = null;
         }
      }

      if (this.output != null) {
         synchronized (this.output) {
            try {
               this.output.close();
            } catch (IOException var5) {
               if (this.exception == null) {
                  this.exception = var5;
               }
            }

            this.output = null;
         }
      }
   }

   public Throwable getException() {
      return this.exception;
   }

   private void feed() throws IOException {
      boolean var1 = false;
      int var2 = this.input.read();

      while (!this.isDone() && var2 != -1) {
         synchronized (this.output) {
            if (!this.isDisabled()) {
               this.output.write(var2);
               var1 = true;
            }

            var2 = this.input.read();
         }
      }

      if (var1) {
         this.output.flush();
      }
   }
}
