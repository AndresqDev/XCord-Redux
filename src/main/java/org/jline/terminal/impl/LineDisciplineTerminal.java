package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Objects;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingPumpInputStream;
import org.jline.utils.NonBlockingReader;

public class LineDisciplineTerminal extends AbstractTerminal {
   private static final String DEFAULT_TERMINAL_ATTRIBUTES = "speed 9600 baud; 24 rows; 80 columns;\nlflags: icanon isig iexten echo echoe -echok echoke -echonl echoctl\n\t-echoprt -altwerase -noflsh -tostop -flusho pendin -nokerninfo\n\t-extproc\niflags: -istrip icrnl -inlcr -igncr ixon -ixoff ixany imaxbel iutf8\n\t-ignbrk brkint -inpck -ignpar -parmrk\noflags: opost onlcr -oxtabs -onocr -onlret\ncflags: cread cs8 -parenb -parodd hupcl -clocal -cstopb -crtscts -dsrflow\n\t-dtrflow -mdmbuf\ncchars: discard = ^O; dsusp = ^Y; eof = ^D; eol = <undef>;\n\teol2 = <undef>; erase = ^?; intr = ^C; kill = ^U; lnext = ^V;\n\tmin = 1; quit = ^\\; reprint = ^R; start = ^Q; status = ^T;\n\tstop = ^S; susp = ^Z; time = 0; werase = ^W;\n";
   private static final int PIPE_SIZE = 1024;
   protected final OutputStream masterOutput;
   protected final OutputStream slaveInputPipe;
   protected final NonBlockingPumpInputStream slaveInput;
   protected final NonBlockingReader slaveReader;
   protected final PrintWriter slaveWriter;
   protected final OutputStream slaveOutput;
   protected final Attributes attributes;
   protected final Size size;

   public LineDisciplineTerminal(String var1, String var2, OutputStream var3, Charset var4) throws IOException {
      this(var1, var2, var3, var4, Terminal.SignalHandler.SIG_DFL);
   }

   public LineDisciplineTerminal(String var1, String var2, OutputStream var3, Charset var4, Terminal.SignalHandler var5) throws IOException {
      super(var1, var2, var4, var5);
      NonBlockingPumpInputStream var6 = NonBlocking.nonBlockingPumpInputStream(1024);
      this.slaveInputPipe = var6.getOutputStream();
      this.slaveInput = var6;
      this.slaveReader = NonBlocking.nonBlocking(this.getName(), this.slaveInput, this.encoding());
      this.slaveOutput = new LineDisciplineTerminal.FilteringOutputStream();
      this.slaveWriter = new PrintWriter(new OutputStreamWriter(this.slaveOutput, this.encoding()));
      this.masterOutput = var3;
      this.attributes = ExecPty.doGetAttr(
         "speed 9600 baud; 24 rows; 80 columns;\nlflags: icanon isig iexten echo echoe -echok echoke -echonl echoctl\n\t-echoprt -altwerase -noflsh -tostop -flusho pendin -nokerninfo\n\t-extproc\niflags: -istrip icrnl -inlcr -igncr ixon -ixoff ixany imaxbel iutf8\n\t-ignbrk brkint -inpck -ignpar -parmrk\noflags: opost onlcr -oxtabs -onocr -onlret\ncflags: cread cs8 -parenb -parodd hupcl -clocal -cstopb -crtscts -dsrflow\n\t-dtrflow -mdmbuf\ncchars: discard = ^O; dsusp = ^Y; eof = ^D; eol = <undef>;\n\teol2 = <undef>; erase = ^?; intr = ^C; kill = ^U; lnext = ^V;\n\tmin = 1; quit = ^\\; reprint = ^R; start = ^Q; status = ^T;\n\tstop = ^S; susp = ^Z; time = 0; werase = ^W;\n"
      );
      this.size = new Size(160, 50);
      this.parseInfoCmp();
   }

   @Override
   public NonBlockingReader reader() {
      return this.slaveReader;
   }

   @Override
   public PrintWriter writer() {
      return this.slaveWriter;
   }

   @Override
   public InputStream input() {
      return this.slaveInput;
   }

   @Override
   public OutputStream output() {
      return this.slaveOutput;
   }

   @Override
   public Attributes getAttributes() {
      Attributes var1 = new Attributes();
      var1.copy(this.attributes);
      return var1;
   }

   @Override
   public void setAttributes(Attributes var1) {
      this.attributes.copy(var1);
   }

   @Override
   public Size getSize() {
      Size var1 = new Size();
      var1.copy(this.size);
      return var1;
   }

   @Override
   public void setSize(Size var1) {
      this.size.copy(var1);
   }

   @Override
   public void raise(Terminal.Signal var1) {
      Objects.requireNonNull(var1);
      this.echoSignal(var1);
      super.raise(var1);
   }

   public void processInputByte(int var1) throws IOException {
      boolean var2 = this.doProcessInputByte(var1);
      this.slaveInputPipe.flush();
      if (var2) {
         this.masterOutput.flush();
      }
   }

   public void processInputBytes(byte[] var1) throws IOException {
      this.processInputBytes(var1, 0, var1.length);
   }

   public void processInputBytes(byte[] var1, int var2, int var3) throws IOException {
      boolean var4 = false;

      for (int var5 = 0; var5 < var3; var5++) {
         var4 |= this.doProcessInputByte(var1[var2 + var5]);
      }

      this.slaveInputPipe.flush();
      if (var4) {
         this.masterOutput.flush();
      }
   }

   protected boolean doProcessInputByte(int var1) throws IOException {
      if (this.attributes.getLocalFlag(Attributes.LocalFlag.ISIG)) {
         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VINTR)) {
            this.raise(Terminal.Signal.INT);
            return false;
         }

         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VQUIT)) {
            this.raise(Terminal.Signal.QUIT);
            return false;
         }

         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VSUSP)) {
            this.raise(Terminal.Signal.TSTP);
            return false;
         }

         if (var1 == this.attributes.getControlChar(Attributes.ControlChar.VSTATUS)) {
            this.raise(Terminal.Signal.INFO);
         }
      }

      if (var1 == 13) {
         if (this.attributes.getInputFlag(Attributes.InputFlag.IGNCR)) {
            return false;
         }

         if (this.attributes.getInputFlag(Attributes.InputFlag.ICRNL)) {
            var1 = 10;
         }
      } else if (var1 == 10 && this.attributes.getInputFlag(Attributes.InputFlag.INLCR)) {
         var1 = 13;
      }

      boolean var2 = false;
      if (this.attributes.getLocalFlag(Attributes.LocalFlag.ECHO)) {
         this.processOutputByte(var1);
         var2 = true;
      }

      this.slaveInputPipe.write(var1);
      return var2;
   }

   protected void processOutputByte(int var1) throws IOException {
      if (this.attributes.getOutputFlag(Attributes.OutputFlag.OPOST) && var1 == 10 && this.attributes.getOutputFlag(Attributes.OutputFlag.ONLCR)) {
         this.masterOutput.write(13);
         this.masterOutput.write(10);
      } else {
         this.masterOutput.write(var1);
      }
   }

   protected void processIOException(IOException var1) {
      this.slaveInput.setIoException(var1);
   }

   @Override
   public void close() throws IOException {
      super.close();

      try {
         this.slaveReader.close();
      } finally {
         try {
            this.slaveInputPipe.close();
         } finally {
            this.slaveWriter.close();
         }
      }
   }

   private class FilteringOutputStream extends OutputStream {
      private FilteringOutputStream() {
      }

      @Override
      public void write(int var1) throws IOException {
         LineDisciplineTerminal.this.processOutputByte(var1);
         this.flush();
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var2 < 0 || var2 > var1.length || var3 < 0 || var2 + var3 > var1.length || var2 + var3 < 0) {
            throw new IndexOutOfBoundsException();
         } else if (var3 != 0) {
            for (int var4 = 0; var4 < var3; var4++) {
               LineDisciplineTerminal.this.processOutputByte(var1[var2 + var4]);
            }

            this.flush();
         }
      }

      @Override
      public void flush() throws IOException {
         LineDisciplineTerminal.this.masterOutput.flush();
      }

      @Override
      public void close() throws IOException {
         LineDisciplineTerminal.this.masterOutput.close();
      }
   }
}
