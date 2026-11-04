package org.jline.terminal.impl;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import org.jline.terminal.Attributes;
import org.jline.terminal.spi.Pty;
import org.jline.utils.NonBlockingInputStream;

public abstract class AbstractPty implements Pty {
   private Attributes current;

   @Override
   public void setAttr(Attributes var1) throws IOException {
      this.current = new Attributes(var1);
      this.doSetAttr(var1);
   }

   @Override
   public InputStream getSlaveInput() throws IOException {
      InputStream var1 = this.doGetSlaveInput();
      return (InputStream)(Boolean.parseBoolean(System.getProperty("org.jline.terminal.pty.nonBlockingReads", "true"))
         ? new AbstractPty.PtyInputStream(var1)
         : var1);
   }

   protected abstract void doSetAttr(Attributes var1) throws IOException;

   protected abstract InputStream doGetSlaveInput() throws IOException;

   protected void checkInterrupted() throws InterruptedIOException {
      if (Thread.interrupted()) {
         throw new InterruptedIOException();
      }
   }

   class PtyInputStream extends NonBlockingInputStream {
      final InputStream in;
      int c = 0;

      PtyInputStream(InputStream var2) {
         this.in = var2;
      }

      @Override
      public int read(long var1, boolean var3) throws IOException {
         AbstractPty.this.checkInterrupted();
         if (this.c != 0) {
            int var9 = this.c;
            if (!var3) {
               this.c = 0;
            }

            return var9;
         } else {
            this.setNonBlocking();
            long var4 = System.currentTimeMillis();

            long var7;
            do {
               int var6 = this.in.read();
               if (var6 >= 0) {
                  if (var3) {
                     this.c = var6;
                  }

                  return var6;
               }

               AbstractPty.this.checkInterrupted();
               var7 = System.currentTimeMillis();
            } while (var1 <= 0L || var7 - var4 <= var1);

            return -2;
         }
      }

      private void setNonBlocking() {
         if (AbstractPty.this.current == null
            || AbstractPty.this.current.getControlChar(Attributes.ControlChar.VMIN) != 0
            || AbstractPty.this.current.getControlChar(Attributes.ControlChar.VTIME) != 1) {
            try {
               Attributes var1 = AbstractPty.this.getAttr();
               var1.setControlChar(Attributes.ControlChar.VMIN, 0);
               var1.setControlChar(Attributes.ControlChar.VTIME, 1);
               AbstractPty.this.setAttr(var1);
            } catch (IOException var2) {
               throw new IOError(var2);
            }
         }
      }
   }
}
