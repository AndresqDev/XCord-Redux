package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;
import org.jline.utils.ClosedException;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingReader;

public class PosixPtyTerminal extends AbstractPosixTerminal {
   private final InputStream in;
   private final OutputStream out;
   private final InputStream masterInput;
   private final OutputStream masterOutput;
   private final NonBlockingInputStream input;
   private final OutputStream output;
   private final NonBlockingReader reader;
   private final PrintWriter writer;
   private final Object lock = new Object();
   private Thread inputPumpThread;
   private Thread outputPumpThread;
   private boolean paused = true;

   public PosixPtyTerminal(String var1, String var2, Pty var3, InputStream var4, OutputStream var5, Charset var6) throws IOException {
      this(var1, var2, var3, var4, var5, var6, Terminal.SignalHandler.SIG_DFL);
   }

   public PosixPtyTerminal(String var1, String var2, Pty var3, InputStream var4, OutputStream var5, Charset var6, Terminal.SignalHandler var7) throws IOException {
      this(var1, var2, var3, var4, var5, var6, var7, false);
   }

   public PosixPtyTerminal(String var1, String var2, Pty var3, InputStream var4, OutputStream var5, Charset var6, Terminal.SignalHandler var7, boolean var8) throws IOException {
      super(var1, var2, var3, var6, var7);
      this.in = Objects.requireNonNull(var4);
      this.out = Objects.requireNonNull(var5);
      this.masterInput = var3.getMasterInput();
      this.masterOutput = var3.getMasterOutput();
      this.input = new PosixPtyTerminal.InputStreamWrapper(NonBlocking.nonBlocking(var1, var3.getSlaveInput()));
      this.output = var3.getSlaveOutput();
      this.reader = NonBlocking.nonBlocking(var1, this.input, this.encoding());
      this.writer = new PrintWriter(new OutputStreamWriter(this.output, this.encoding()));
      this.parseInfoCmp();
      if (!var8) {
         this.resume();
      }
   }

   @Override
   public InputStream input() {
      return this.input;
   }

   @Override
   public NonBlockingReader reader() {
      return this.reader;
   }

   @Override
   public OutputStream output() {
      return this.output;
   }

   @Override
   public PrintWriter writer() {
      return this.writer;
   }

   @Override
   public void close() throws IOException {
      super.close();
      this.reader.close();
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
      Thread var3;
      synchronized (this.lock) {
         this.paused = true;
         var2 = this.inputPumpThread;
         var3 = this.outputPumpThread;
      }

      if (var2 != null) {
         var2.interrupt();
      }

      if (var3 != null) {
         var3.interrupt();
      }

      if (var2 != null) {
         var2.join();
      }

      if (var3 != null) {
         var3.join();
      }
   }

   @Override
   public void resume() {
      synchronized (this.lock) {
         this.paused = false;
         if (this.inputPumpThread == null) {
            this.inputPumpThread = new Thread(this::pumpIn, this.toString() + " input pump thread");
            this.inputPumpThread.setDaemon(true);
            this.inputPumpThread.start();
         }

         if (this.outputPumpThread == null) {
            this.outputPumpThread = new Thread(this::pumpOut, this.toString() + " output pump thread");
            this.outputPumpThread.setDaemon(true);
            this.outputPumpThread.start();
         }
      }
   }

   @Override
   public boolean paused() {
      synchronized (this.lock) {
         return this.paused;
      }
   }

   private void pumpIn() {
      while (true) {
         try {
            synchronized (this.lock) {
               if (this.paused) {
                  this.inputPumpThread = null;
                  return;
               }
            }

            int var21 = this.in.read();
            if (var21 < 0) {
               this.input.close();
               break;
            }

            this.masterOutput.write(var21);
            this.masterOutput.flush();
         } catch (IOException var19) {
            var19.printStackTrace();
            break;
         } finally {
            synchronized (this.lock) {
               this.inputPumpThread = null;
            }
         }
      }
   }

   private void pumpOut() {
      try {
         while (true) {
            synchronized (this.lock) {
               if (this.paused) {
                  this.outputPumpThread = null;
                  return;
               }
            }

            int var23 = this.masterInput.read();
            if (var23 < 0) {
               this.input.close();
               break;
            }

            this.out.write(var23);
            this.out.flush();
         }
      } catch (IOException var21) {
         var21.printStackTrace();
      } finally {
         synchronized (this.lock) {
            this.outputPumpThread = null;
         }
      }

      try {
         this.close();
      } catch (Throwable var19) {
      }
   }

   private class InputStreamWrapper extends NonBlockingInputStream {
      private final NonBlockingInputStream in;
      private final AtomicBoolean closed = new AtomicBoolean();

      protected InputStreamWrapper(NonBlockingInputStream var2) {
         this.in = var2;
      }

      @Override
      public int read(long var1, boolean var3) throws IOException {
         if (this.closed.get()) {
            throw new ClosedException();
         } else {
            return this.in.read(var1, var3);
         }
      }

      @Override
      public void close() throws IOException {
         this.closed.set(true);
      }
   }
}
