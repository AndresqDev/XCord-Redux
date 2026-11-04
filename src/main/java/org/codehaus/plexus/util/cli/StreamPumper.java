package org.codehaus.plexus.util.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StreamPumper extends AbstractStreamHandler {
   private final BufferedReader in;
   private final StreamConsumer consumer;
   private final PrintWriter out;
   private volatile Exception exception = null;
   private static final int SIZE = 1024;

   public StreamPumper(InputStream var1) {
      this(var1, (StreamConsumer)null);
   }

   public StreamPumper(InputStream var1, StreamConsumer var2) {
      this(var1, null, var2);
   }

   public StreamPumper(InputStream var1, PrintWriter var2) {
      this(var1, var2, null);
   }

   public StreamPumper(InputStream var1, PrintWriter var2, StreamConsumer var3) {
      this.in = new BufferedReader(new InputStreamReader(var1), 1024);
      this.out = var2;
      this.consumer = var3;
   }

   @Override
   public void run() {
      boolean var1 = this.out != null ? this.out.checkError() : false;

      try {
         for (String var2 = this.in.readLine(); var2 != null; var2 = this.in.readLine()) {
            try {
               if (this.exception == null && this.consumer != null && !this.isDisabled()) {
                  this.consumer.consumeLine(var2);
               }
            } catch (Exception var20) {
               this.exception = var20;
            }

            if (this.out != null && !var1) {
               this.out.println(var2);
               this.out.flush();
               if (this.out.checkError()) {
                  var1 = true;

                  try {
                     throw new IOException(String.format("Failure printing line '%s'.", var2));
                  } catch (IOException var22) {
                     this.exception = var22;
                  }
               }
            }
         }
      } catch (IOException var23) {
         this.exception = var23;
      } finally {
         try {
            this.in.close();
         } catch (IOException var21) {
            if (this.exception == null) {
               this.exception = var21;
            }
         }

         synchronized (this) {
            this.setDone();
            this.notifyAll();
         }
      }
   }

   public void flush() {
      if (this.out != null) {
         this.out.flush();
         if (this.out.checkError() && this.exception == null) {
            try {
               throw new IOException("Failure flushing output.");
            } catch (IOException var2) {
               this.exception = var2;
            }
         }
      }
   }

   public void close() {
      if (this.out != null) {
         this.out.close();
         if (this.out.checkError() && this.exception == null) {
            try {
               throw new IOException("Failure closing output.");
            } catch (IOException var2) {
               this.exception = var2;
            }
         }
      }
   }

   public Exception getException() {
      return this.exception;
   }
}
