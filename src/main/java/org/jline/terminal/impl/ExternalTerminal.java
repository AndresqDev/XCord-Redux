package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;

public class ExternalTerminal extends LineDisciplineTerminal {
   protected final AtomicBoolean closed = new AtomicBoolean();
   protected final InputStream masterInput;
   protected final Object lock = new Object();
   protected boolean paused = true;
   protected Thread pumpThread;

   public ExternalTerminal(String var1, String var2, InputStream var3, OutputStream var4, Charset var5) throws IOException {
      this(var1, var2, var3, var4, var5, Terminal.SignalHandler.SIG_DFL);
   }

   public ExternalTerminal(String var1, String var2, InputStream var3, OutputStream var4, Charset var5, Terminal.SignalHandler var6) throws IOException {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   public ExternalTerminal(String var1, String var2, InputStream var3, OutputStream var4, Charset var5, Terminal.SignalHandler var6, boolean var7) throws IOException {
      super(var1, var2, var4, var5, var6);
      this.masterInput = var3;
      if (!var7) {
         this.resume();
      }
   }

   @Override
   public void close() throws IOException {
      if (this.closed.compareAndSet(false, true)) {
         this.pause();
         super.close();
      }
   }

   @Override
   public boolean canPauseResume() {
      return true;
   }

   @Override
   public void pause() {
      synchronized (this.lock) {
         this.paused = true;
      }
   }

   @Override
   public void pause(boolean var1) throws InterruptedException {
      Thread var2;
      synchronized (this.lock) {
         this.paused = true;
         var2 = this.pumpThread;
      }

      if (var2 != null) {
         var2.interrupt();
         var2.join();
      }
   }

   @Override
   public void resume() {
      synchronized (this.lock) {
         this.paused = false;
         if (this.pumpThread == null) {
            this.pumpThread = new Thread(this::pump, this.toString() + " input pump thread");
            this.pumpThread.setDaemon(true);
            this.pumpThread.start();
         }
      }
   }

   @Override
   public boolean paused() {
      synchronized (this.lock) {
         return this.paused;
      }
   }

   public void pump() {
      try {
         byte[] var1 = new byte[1024];

         while (true) {
            int var2 = this.masterInput.read(var1);
            if (var2 >= 0) {
               this.processInputBytes(var1, 0, var2);
            }

            if (var2 < 0 || this.closed.get()) {
               break;
            }

            synchronized (this.lock) {
               if (this.paused) {
                  this.pumpThread = null;
                  return;
               }
            }
         }
      } catch (IOException var23) {
         this.processIOException(var23);
      } finally {
         synchronized (this.lock) {
            this.pumpThread = null;
         }
      }

      try {
         this.slaveInput.close();
      } catch (IOException var21) {
      }
   }

   @Override
   public Cursor getCursorPosition(IntConsumer var1) {
      return CursorSupport.getCursorPosition(this, var1);
   }
}
