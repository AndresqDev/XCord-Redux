package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingPumpReader;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.ShutdownHooks;
import org.jline.utils.Signals;
import org.jline.utils.WriterOutputStream;

public abstract class AbstractWindowsTerminal extends AbstractTerminal {
   public static final String TYPE_WINDOWS = "windows";
   public static final String TYPE_WINDOWS_256_COLOR = "windows-256color";
   public static final String TYPE_WINDOWS_CONEMU = "windows-conemu";
   public static final String TYPE_WINDOWS_VTP = "windows-vtp";
   public static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
   private static final int UTF8_CODE_PAGE = 65001;
   protected static final int ENABLE_PROCESSED_INPUT = 1;
   protected static final int ENABLE_LINE_INPUT = 2;
   protected static final int ENABLE_ECHO_INPUT = 4;
   protected static final int ENABLE_WINDOW_INPUT = 8;
   protected static final int ENABLE_MOUSE_INPUT = 16;
   protected static final int ENABLE_INSERT_MODE = 32;
   protected static final int ENABLE_QUICK_EDIT_MODE = 64;
   protected final Writer slaveInputPipe;
   protected final NonBlockingInputStream input;
   protected final OutputStream output;
   protected final NonBlockingReader reader;
   protected final PrintWriter writer;
   protected final Map<Terminal.Signal, Object> nativeHandlers = new HashMap<>();
   protected final ShutdownHooks.Task closer;
   protected final Attributes attributes = new Attributes();
   protected final int originalConsoleMode;
   protected final Object lock = new Object();
   protected boolean paused = true;
   protected Thread pump;
   protected Terminal.MouseTracking tracking = Terminal.MouseTracking.Off;
   protected boolean focusTracking = false;
   private volatile boolean closing;
   static final int SHIFT_FLAG = 1;
   static final int ALT_FLAG = 2;
   static final int CTRL_FLAG = 4;
   static final int RIGHT_ALT_PRESSED = 1;
   static final int LEFT_ALT_PRESSED = 2;
   static final int RIGHT_CTRL_PRESSED = 4;
   static final int LEFT_CTRL_PRESSED = 8;
   static final int SHIFT_PRESSED = 16;
   static final int NUMLOCK_ON = 32;
   static final int SCROLLLOCK_ON = 64;
   static final int CAPSLOCK_ON = 128;

   public AbstractWindowsTerminal(Writer var1, String var2, String var3, Charset var4, int var5, boolean var6, Terminal.SignalHandler var7) throws IOException {
      super(var2, var3, selectCharset(var4, var5), var7);
      NonBlockingPumpReader var8 = NonBlocking.nonBlockingPumpReader();
      this.slaveInputPipe = var8.getWriter();
      this.reader = var8;
      this.input = NonBlocking.nonBlockingStream(var8, this.encoding());
      this.writer = new PrintWriter(var1);
      this.output = new WriterOutputStream(var1, this.encoding());
      this.parseInfoCmp();
      this.originalConsoleMode = this.getConsoleMode();
      this.attributes.setLocalFlag(Attributes.LocalFlag.ISIG, true);
      this.attributes.setControlChar(Attributes.ControlChar.VINTR, this.ctrl('C'));
      this.attributes.setControlChar(Attributes.ControlChar.VEOF, this.ctrl('D'));
      this.attributes.setControlChar(Attributes.ControlChar.VSUSP, this.ctrl('Z'));
      if (var6) {
         for (Terminal.Signal var12 : Terminal.Signal.values()) {
            if (var7 == Terminal.SignalHandler.SIG_DFL) {
               this.nativeHandlers.put(var12, Signals.registerDefault(var12.name()));
            } else {
               this.nativeHandlers.put(var12, Signals.register(var12.name(), () -> this.raise(var12)));
            }
         }
      }

      this.closer = this::close;
      ShutdownHooks.add(this.closer);
      if ("windows-conemu".equals(this.getType()) && !Boolean.getBoolean("org.jline.terminal.conemu.disable-activate")) {
         var1.write("\u001b[9999E");
         var1.flush();
      }
   }

   private static Charset selectCharset(Charset var0, int var1) {
      if (var0 != null) {
         return var0;
      } else {
         return var1 >= 0 ? getCodepageCharset(var1) : StandardCharsets.UTF_8;
      }
   }

   private static Charset getCodepageCharset(int var0) {
      if (var0 == 65001) {
         return StandardCharsets.UTF_8;
      } else {
         String var1 = "ms" + var0;
         if (Charset.isSupported(var1)) {
            return Charset.forName(var1);
         } else {
            String var2 = "cp" + var0;
            return Charset.isSupported(var2) ? Charset.forName(var2) : Charset.defaultCharset();
         }
      }
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
   public Attributes getAttributes() {
      int var1 = this.getConsoleMode();
      if ((var1 & 4) != 0) {
         this.attributes.setLocalFlag(Attributes.LocalFlag.ECHO, true);
      }

      if ((var1 & 2) != 0) {
         this.attributes.setLocalFlag(Attributes.LocalFlag.ICANON, true);
      }

      return new Attributes(this.attributes);
   }

   @Override
   public void setAttributes(Attributes var1) {
      this.attributes.copy(var1);
      this.updateConsoleMode();
   }

   protected void updateConsoleMode() {
      byte var1 = 8;
      if (this.attributes.getLocalFlag(Attributes.LocalFlag.ECHO)) {
         var1 |= 4;
      }

      if (this.attributes.getLocalFlag(Attributes.LocalFlag.ICANON)) {
         var1 |= 2;
      }

      if (this.tracking != Terminal.MouseTracking.Off) {
         var1 |= 16;
      }

      this.setConsoleMode(var1);
   }

   protected int ctrl(char var1) {
      return Character.toUpperCase(var1) & 31;
   }

   @Override
   public void setSize(Size var1) {
      throw new UnsupportedOperationException("Can not resize windows terminal");
   }

   @Override
   public void close() throws IOException {
      super.close();
      this.closing = true;
      this.pump.interrupt();
      ShutdownHooks.remove(this.closer);

      for (Entry var2 : this.nativeHandlers.entrySet()) {
         Signals.unregister(((Terminal.Signal)var2.getKey()).name(), var2.getValue());
      }

      this.reader.close();
      this.writer.close();
      this.setConsoleMode(this.originalConsoleMode);
   }

   protected void processKeyEvent(boolean var1, short var2, char var3, int var4) throws IOException {
      boolean var5 = (var4 & 12) > 0;
      boolean var6 = (var4 & 3) > 0;
      boolean var7 = (var4 & 16) > 0;
      if (var1 && var3 != 3) {
         if (var3 != 0 && (var4 & 31) == 9) {
            this.processInputChar(var3);
         } else {
            String var8 = this.getEscapeSequence(var2, (var5 ? 4 : 0) + (var6 ? 2 : 0) + (var7 ? 1 : 0));
            if (var8 != null) {
               for (char var12 : var8.toCharArray()) {
                  this.processInputChar(var12);
               }

               return;
            }

            if (var3 > 0) {
               if (var6) {
                  this.processInputChar('\u001b');
               }

               if (var5 && var3 != ' ' && var3 != '\n' && var3 != 127) {
                  this.processInputChar((char)(var3 == '?' ? 127 : Character.toUpperCase(var3) & 31));
               } else {
                  this.processInputChar(var3);
               }
            } else if (var5) {
               if (var2 >= 65 && var2 <= 90) {
                  var3 = (char)(var2 - 64);
               } else if (var2 == 191) {
                  var3 = 127;
               }

               if (var3 > 0) {
                  if (var6) {
                     this.processInputChar('\u001b');
                  }

                  this.processInputChar(var3);
               }
            }
         }
      } else if (var1 && var3 == 3) {
         this.processInputChar('\u0003');
      } else if (var2 == 18 && var3 > 0) {
         this.processInputChar(var3);
      }
   }

   protected String getEscapeSequence(short var1, int var2) {
      Object var3 = null;
      switch (var1) {
         case 8:
            var3 = (var2 & 2) > 0 ? "\\E^H" : this.getRawSequence(InfoCmp.Capability.key_backspace);
            break;
         case 9:
            var3 = (var2 & 1) > 0 ? this.getRawSequence(InfoCmp.Capability.key_btab) : null;
            break;
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 41:
         case 42:
         case 43:
         case 44:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         default:
            return null;
         case 33:
            var3 = this.getRawSequence(InfoCmp.Capability.key_ppage);
            break;
         case 34:
            var3 = this.getRawSequence(InfoCmp.Capability.key_npage);
            break;
         case 35:
            var3 = var2 > 0 ? "\\E[1;%p1%dF" : this.getRawSequence(InfoCmp.Capability.key_end);
            break;
         case 36:
            var3 = var2 > 0 ? "\\E[1;%p1%dH" : this.getRawSequence(InfoCmp.Capability.key_home);
            break;
         case 37:
            var3 = var2 > 0 ? "\\E[1;%p1%dD" : this.getRawSequence(InfoCmp.Capability.key_left);
            break;
         case 38:
            var3 = var2 > 0 ? "\\E[1;%p1%dA" : this.getRawSequence(InfoCmp.Capability.key_up);
            break;
         case 39:
            var3 = var2 > 0 ? "\\E[1;%p1%dC" : this.getRawSequence(InfoCmp.Capability.key_right);
            break;
         case 40:
            var3 = var2 > 0 ? "\\E[1;%p1%dB" : this.getRawSequence(InfoCmp.Capability.key_down);
            break;
         case 45:
            var3 = this.getRawSequence(InfoCmp.Capability.key_ic);
            break;
         case 46:
            var3 = this.getRawSequence(InfoCmp.Capability.key_dc);
            break;
         case 112:
            var3 = var2 > 0 ? "\\E[1;%p1%dP" : this.getRawSequence(InfoCmp.Capability.key_f1);
            break;
         case 113:
            var3 = var2 > 0 ? "\\E[1;%p1%dQ" : this.getRawSequence(InfoCmp.Capability.key_f2);
            break;
         case 114:
            var3 = var2 > 0 ? "\\E[1;%p1%dR" : this.getRawSequence(InfoCmp.Capability.key_f3);
            break;
         case 115:
            var3 = var2 > 0 ? "\\E[1;%p1%dS" : this.getRawSequence(InfoCmp.Capability.key_f4);
            break;
         case 116:
            var3 = var2 > 0 ? "\\E[15;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f5);
            break;
         case 117:
            var3 = var2 > 0 ? "\\E[17;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f6);
            break;
         case 118:
            var3 = var2 > 0 ? "\\E[18;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f7);
            break;
         case 119:
            var3 = var2 > 0 ? "\\E[19;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f8);
            break;
         case 120:
            var3 = var2 > 0 ? "\\E[20;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f9);
            break;
         case 121:
            var3 = var2 > 0 ? "\\E[21;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f10);
            break;
         case 122:
            var3 = var2 > 0 ? "\\E[23;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f11);
            break;
         case 123:
            var3 = var2 > 0 ? "\\E[24;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f12);
      }

      return Curses.tputs((String)var3, var2 + 1);
   }

   protected String getRawSequence(InfoCmp.Capability var1) {
      return this.strings.get(var1);
   }

   @Override
   public boolean hasFocusSupport() {
      return true;
   }

   @Override
   public boolean trackFocus(boolean var1) {
      this.focusTracking = var1;
      return true;
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
         var2 = this.pump;
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
         if (this.pump == null) {
            this.pump = new Thread(this::pump, "WindowsStreamPump");
            this.pump.setDaemon(true);
            this.pump.start();
         }
      }
   }

   @Override
   public boolean paused() {
      synchronized (this.lock) {
         return this.paused;
      }
   }

   protected void pump() {
      try {
         while (!this.closing) {
            synchronized (this.lock) {
               if (this.paused) {
                  this.pump = null;
                  break;
               }
            }

            if (this.processConsoleInput()) {
               this.slaveInputPipe.flush();
            }
         }
      } catch (IOException var18) {
         if (!this.closing) {
            Log.warn("Error in WindowsStreamPump", var18);

            try {
               this.close();
            } catch (IOException var16) {
               Log.warn("Error closing terminal", var18);
            }
         }
      } finally {
         synchronized (this.lock) {
            this.pump = null;
         }
      }
   }

   public void processInputChar(char var1) throws IOException {
      if (this.attributes.getLocalFlag(Attributes.LocalFlag.ISIG)) {
         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VINTR)) {
            this.raise(Terminal.Signal.INT);
            return;
         }

         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VQUIT)) {
            this.raise(Terminal.Signal.QUIT);
            return;
         }

         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VSUSP)) {
            this.raise(Terminal.Signal.TSTP);
            return;
         }

         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VSTATUS)) {
            this.raise(Terminal.Signal.INFO);
         }
      }

      if (var1 == '\r') {
         if (this.attributes.getInputFlag(Attributes.InputFlag.IGNCR)) {
            return;
         }

         if (this.attributes.getInputFlag(Attributes.InputFlag.ICRNL)) {
            var1 = '\n';
         }
      } else if (var1 == '\n' && this.attributes.getInputFlag(Attributes.InputFlag.INLCR)) {
         var1 = '\r';
      }

      this.slaveInputPipe.write(var1);
   }

   @Override
   public boolean trackMouse(Terminal.MouseTracking var1) {
      this.tracking = var1;
      this.updateConsoleMode();
      return true;
   }

   protected abstract int getConsoleOutputCP();

   protected abstract int getConsoleMode();

   protected abstract void setConsoleMode(int var1);

   protected abstract boolean processConsoleInput() throws IOException;
}
