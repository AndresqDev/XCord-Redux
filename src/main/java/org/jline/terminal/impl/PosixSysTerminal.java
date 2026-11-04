package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.ShutdownHooks;
import org.jline.utils.Signals;

public class PosixSysTerminal extends AbstractPosixTerminal {
   protected final NonBlockingInputStream input;
   protected final OutputStream output;
   protected final NonBlockingReader reader;
   protected final PrintWriter writer;
   protected final Map<Terminal.Signal, Object> nativeHandlers = new HashMap<>();
   protected final ShutdownHooks.Task closer;

   public PosixSysTerminal(String var1, String var2, Pty var3, Charset var4, boolean var5, Terminal.SignalHandler var6) throws IOException {
      super(var1, var2, var3, var4, var6);
      this.input = NonBlocking.nonBlocking(this.getName(), var3.getSlaveInput());
      this.output = var3.getSlaveOutput();
      this.reader = NonBlocking.nonBlocking(this.getName(), this.input, this.encoding());
      this.writer = new PrintWriter(new OutputStreamWriter(this.output, this.encoding()));
      this.parseInfoCmp();
      if (var5) {
         for (Terminal.Signal var10 : Terminal.Signal.values()) {
            if (var6 == Terminal.SignalHandler.SIG_DFL) {
               this.nativeHandlers.put(var10, Signals.registerDefault(var10.name()));
            } else {
               this.nativeHandlers.put(var10, Signals.register(var10.name(), () -> this.raise(var10)));
            }
         }
      }

      this.closer = this::close;
      ShutdownHooks.add(this.closer);
   }

   @Override
   public Terminal.SignalHandler handle(Terminal.Signal var1, Terminal.SignalHandler var2) {
      Terminal.SignalHandler var3 = super.handle(var1, var2);
      if (var3 != var2) {
         if (var2 == Terminal.SignalHandler.SIG_DFL) {
            Signals.registerDefault(var1.name());
         } else {
            Signals.register(var1.name(), () -> this.raise(var1));
         }
      }

      return var3;
   }

   @Override
   public NonBlockingReader reader() {
      return this.reader;
   }

   @Override
   public PrintWriter writer() {
      return this.writer;
   }

   @Override
   public InputStream input() {
      return this.input;
   }

   @Override
   public OutputStream output() {
      return this.output;
   }

   @Override
   public void close() throws IOException {
      ShutdownHooks.remove(this.closer);

      for (Entry var2 : this.nativeHandlers.entrySet()) {
         Signals.unregister(((Terminal.Signal)var2.getKey()).name(), var2.getValue());
      }

      super.close();
      this.reader.shutdown();
   }
}
