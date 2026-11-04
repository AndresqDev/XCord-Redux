package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.Status;

public abstract class AbstractTerminal implements Terminal {
   protected final String name;
   protected final String type;
   protected final Charset encoding;
   protected final Map<Terminal.Signal, Terminal.SignalHandler> handlers = new HashMap<>();
   protected final Set<InfoCmp.Capability> bools = new HashSet<>();
   protected final Map<InfoCmp.Capability, Integer> ints = new HashMap<>();
   protected final Map<InfoCmp.Capability, String> strings = new HashMap<>();
   protected Status status;
   private MouseEvent lastMouseEvent = new MouseEvent(MouseEvent.Type.Moved, MouseEvent.Button.NoButton, EnumSet.noneOf(MouseEvent.Modifier.class), 0, 0);

   public AbstractTerminal(String var1, String var2) throws IOException {
      this(var1, var2, null, Terminal.SignalHandler.SIG_DFL);
   }

   public AbstractTerminal(String var1, String var2, Charset var3, Terminal.SignalHandler var4) throws IOException {
      this.name = var1;
      this.type = var2;
      this.encoding = var3 != null ? var3 : Charset.defaultCharset();

      for (Terminal.Signal var8 : Terminal.Signal.values()) {
         this.handlers.put(var8, var4);
      }
   }

   public Status getStatus() {
      return this.getStatus(true);
   }

   public Status getStatus(boolean var1) {
      if (this.status == null && var1) {
         this.status = new Status(this);
      }

      return this.status;
   }

   @Override
   public Terminal.SignalHandler handle(Terminal.Signal var1, Terminal.SignalHandler var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return this.handlers.put(var1, var2);
   }

   @Override
   public void raise(Terminal.Signal var1) {
      Objects.requireNonNull(var1);
      Terminal.SignalHandler var2 = this.handlers.get(var1);
      if (var2 != Terminal.SignalHandler.SIG_DFL && var2 != Terminal.SignalHandler.SIG_IGN) {
         var2.handle(var1);
      }

      if (this.status != null && var1 == Terminal.Signal.WINCH) {
         this.status.resize();
      }
   }

   @Override
   public void close() throws IOException {
      if (this.status != null) {
         this.status.update(null);
         this.flush();
      }
   }

   protected void echoSignal(Terminal.Signal var1) {
      Attributes.ControlChar var2 = null;
      switch (var1) {
         case INT:
            var2 = Attributes.ControlChar.VINTR;
            break;
         case QUIT:
            var2 = Attributes.ControlChar.VQUIT;
            break;
         case TSTP:
            var2 = Attributes.ControlChar.VSUSP;
      }

      if (var2 != null) {
         int var3 = this.getAttributes().getControlChar(var2);
         if (var3 > 0 && var3 < 32) {
            this.writer().write(new char[]{'^', (char)(var3 + 64)}, 0, 2);
         }
      }
   }

   @Override
   public Attributes enterRawMode() {
      Attributes var1 = this.getAttributes();
      Attributes var2 = new Attributes(var1);
      var2.setLocalFlags(EnumSet.of(Attributes.LocalFlag.ICANON, Attributes.LocalFlag.ECHO, Attributes.LocalFlag.IEXTEN), false);
      var2.setInputFlags(EnumSet.of(Attributes.InputFlag.IXON, Attributes.InputFlag.ICRNL, Attributes.InputFlag.INLCR), false);
      var2.setControlChar(Attributes.ControlChar.VMIN, 0);
      var2.setControlChar(Attributes.ControlChar.VTIME, 1);
      this.setAttributes(var2);
      return var1;
   }

   @Override
   public boolean echo() {
      return this.getAttributes().getLocalFlag(Attributes.LocalFlag.ECHO);
   }

   @Override
   public boolean echo(boolean var1) {
      Attributes var2 = this.getAttributes();
      boolean var3 = var2.getLocalFlag(Attributes.LocalFlag.ECHO);
      if (var3 != var1) {
         var2.setLocalFlag(Attributes.LocalFlag.ECHO, var1);
         this.setAttributes(var2);
      }

      return var3;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getType() {
      return this.type;
   }

   public String getKind() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Charset encoding() {
      return this.encoding;
   }

   @Override
   public void flush() {
      this.writer().flush();
   }

   @Override
   public boolean puts(InfoCmp.Capability var1, Object... var2) {
      String var3 = this.getStringCapability(var1);
      if (var3 == null) {
         return false;
      } else {
         Curses.tputs(this.writer(), var3, var2);
         return true;
      }
   }

   @Override
   public boolean getBooleanCapability(InfoCmp.Capability var1) {
      return this.bools.contains(var1);
   }

   @Override
   public Integer getNumericCapability(InfoCmp.Capability var1) {
      return this.ints.get(var1);
   }

   @Override
   public String getStringCapability(InfoCmp.Capability var1) {
      return this.strings.get(var1);
   }

   protected void parseInfoCmp() {
      String var1 = null;
      if (this.type != null) {
         try {
            var1 = InfoCmp.getInfoCmp(this.type);
         } catch (Exception var3) {
            Log.warn("Unable to retrieve infocmp for type " + this.type, var3);
         }
      }

      if (var1 == null) {
         var1 = InfoCmp.getLoadedInfoCmp("ansi");
      }

      InfoCmp.parseInfoCmp(var1, this.bools, this.ints, this.strings);
   }

   @Override
   public Cursor getCursorPosition(IntConsumer var1) {
      return null;
   }

   @Override
   public boolean hasMouseSupport() {
      return MouseSupport.hasMouseSupport(this);
   }

   @Override
   public boolean trackMouse(Terminal.MouseTracking var1) {
      return MouseSupport.trackMouse(this, var1);
   }

   @Override
   public MouseEvent readMouseEvent() {
      return this.lastMouseEvent = MouseSupport.readMouse(this, this.lastMouseEvent);
   }

   @Override
   public MouseEvent readMouseEvent(IntSupplier var1) {
      return this.lastMouseEvent = MouseSupport.readMouse(var1, this.lastMouseEvent);
   }

   @Override
   public boolean hasFocusSupport() {
      return this.type != null && this.type.startsWith("xterm");
   }

   @Override
   public boolean trackFocus(boolean var1) {
      if (this.hasFocusSupport()) {
         this.writer().write(var1 ? "\u001b[?1004h" : "\u001b[?1004l");
         this.writer().flush();
         return true;
      } else {
         return false;
      }
   }

   protected void checkInterrupted() throws InterruptedIOException {
      if (Thread.interrupted()) {
         throw new InterruptedIOException();
      }
   }

   @Override
   public boolean canPauseResume() {
      return false;
   }

   @Override
   public void pause() {
   }

   @Override
   public void pause(boolean var1) throws InterruptedException {
   }

   @Override
   public void resume() {
   }

   @Override
   public boolean paused() {
      return false;
   }
}
