package org.jline.terminal.impl;

import java.io.IOError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.IntConsumer;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;

public abstract class AbstractPosixTerminal extends AbstractTerminal {
   protected final Pty pty;
   protected final Attributes originalAttributes;

   public AbstractPosixTerminal(String var1, String var2, Pty var3) throws IOException {
      this(var1, var2, var3, null, Terminal.SignalHandler.SIG_DFL);
   }

   public AbstractPosixTerminal(String var1, String var2, Pty var3, Charset var4, Terminal.SignalHandler var5) throws IOException {
      super(var1, var2, var4, var5);
      Objects.requireNonNull(var3);
      this.pty = var3;
      this.originalAttributes = this.pty.getAttr();
   }

   public Pty getPty() {
      return this.pty;
   }

   @Override
   public Attributes getAttributes() {
      try {
         return this.pty.getAttr();
      } catch (IOException var2) {
         throw new IOError(var2);
      }
   }

   @Override
   public void setAttributes(Attributes var1) {
      try {
         this.pty.setAttr(var1);
      } catch (IOException var3) {
         throw new IOError(var3);
      }
   }

   @Override
   public Size getSize() {
      try {
         return this.pty.getSize();
      } catch (IOException var2) {
         throw new IOError(var2);
      }
   }

   @Override
   public void setSize(Size var1) {
      try {
         this.pty.setSize(var1);
      } catch (IOException var3) {
         throw new IOError(var3);
      }
   }

   @Override
   public void close() throws IOException {
      super.close();
      this.pty.setAttr(this.originalAttributes);
      this.pty.close();
   }

   @Override
   public Cursor getCursorPosition(IntConsumer var1) {
      return CursorSupport.getCursorPosition(this, var1);
   }
}
