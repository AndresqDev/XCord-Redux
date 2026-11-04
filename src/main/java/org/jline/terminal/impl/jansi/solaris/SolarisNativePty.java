package org.jline.terminal.impl.jansi.solaris;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import org.fusesource.jansi.internal.CLibrary;
import org.jline.terminal.Attributes;
import org.jline.terminal.impl.jansi.JansiNativePty;

public class SolarisNativePty extends JansiNativePty {
   private static final int VINTR = 0;
   private static final int VQUIT = 1;
   private static final int VERASE = 2;
   private static final int VKILL = 3;
   private static final int VEOF = 4;
   private static final int VTIME = 5;
   private static final int VMIN = 6;
   private static final int VSWTC = 7;
   private static final int VSTART = 8;
   private static final int VSTOP = 9;
   private static final int VSUSP = 10;
   private static final int VEOL = 11;
   private static final int VREPRINT = 12;
   private static final int VDISCARD = 13;
   private static final int VWERASE = 14;
   private static final int VLNEXT = 15;
   private static final int VEOL2 = 16;
   private static final int IGNBRK = 1;
   private static final int BRKINT = 2;
   private static final int IGNPAR = 4;
   private static final int PARMRK = 16;
   private static final int INPCK = 32;
   private static final int ISTRIP = 64;
   private static final int INLCR = 256;
   private static final int IGNCR = 512;
   private static final int ICRNL = 1024;
   private static final int IUCLC = 4096;
   private static final int IXON = 8192;
   private static final int IXANY = 16384;
   private static final int IXOFF = 65536;
   private static final int IMAXBEL = 131072;
   private static final int IUTF8 = 262144;
   private static final int OPOST = 1;
   private static final int OLCUC = 2;
   private static final int ONLCR = 4;
   private static final int OCRNL = 16;
   private static final int ONOCR = 32;
   private static final int ONLRET = 64;
   private static final int OFILL = 256;
   private static final int OFDEL = 512;
   private static final int NLDLY = 1024;
   private static final int NL0 = 0;
   private static final int NL1 = 1024;
   private static final int CRDLY = 12288;
   private static final int CR0 = 0;
   private static final int CR1 = 4096;
   private static final int CR2 = 8192;
   private static final int CR3 = 12288;
   private static final int TABDLY = 81920;
   private static final int TAB0 = 0;
   private static final int TAB1 = 16384;
   private static final int TAB2 = 65536;
   private static final int TAB3 = 81920;
   private static final int XTABS = 81920;
   private static final int BSDLY = 131072;
   private static final int BS0 = 0;
   private static final int BS1 = 131072;
   private static final int VTDLY = 262144;
   private static final int VT0 = 0;
   private static final int VT1 = 262144;
   private static final int FFDLY = 1048576;
   private static final int FF0 = 0;
   private static final int FF1 = 1048576;
   private static final int CBAUD = 65559;
   private static final int B0 = 0;
   private static final int B50 = 1;
   private static final int B75 = 2;
   private static final int B110 = 3;
   private static final int B134 = 4;
   private static final int B150 = 5;
   private static final int B200 = 6;
   private static final int B300 = 7;
   private static final int B600 = 16;
   private static final int B1200 = 17;
   private static final int B1800 = 18;
   private static final int B2400 = 19;
   private static final int B4800 = 20;
   private static final int B9600 = 21;
   private static final int B19200 = 22;
   private static final int B38400 = 23;
   private static final int EXTA = 11637248;
   private static final int EXTB = 11764736;
   private static final int CSIZE = 96;
   private static final int CS5 = 0;
   private static final int CS6 = 32;
   private static final int CS7 = 64;
   private static final int CS8 = 96;
   private static final int CSTOPB = 256;
   private static final int CREAD = 512;
   private static final int PARENB = 1024;
   private static final int PARODD = 4096;
   private static final int HUPCL = 8192;
   private static final int CLOCAL = 16384;
   private static final int ISIG = 1;
   private static final int ICANON = 2;
   private static final int XCASE = 4;
   private static final int ECHO = 16;
   private static final int ECHOE = 32;
   private static final int ECHOK = 64;
   private static final int ECHONL = 256;
   private static final int NOFLSH = 512;
   private static final int TOSTOP = 1024;
   private static final int ECHOCTL = 4096;
   private static final int ECHOPRT = 8192;
   private static final int ECHOKE = 16384;
   private static final int FLUSHO = 65536;
   private static final int PENDIN = 262144;
   private static final int IEXTEN = 1048576;
   private static final int EXTPROC = 2097152;

   public static SolarisNativePty current() throws IOException {
      try {
         String var0 = ttyname();
         return new SolarisNativePty(-1, null, 0, FileDescriptor.in, 1, FileDescriptor.out, var0);
      } catch (IOException var1) {
         throw new IOException("Not a tty", var1);
      }
   }

   public SolarisNativePty(int var1, FileDescriptor var2, int var3, FileDescriptor var4, String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public SolarisNativePty(int var1, FileDescriptor var2, int var3, FileDescriptor var4, int var5, FileDescriptor var6, String var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   protected CLibrary.Termios toTermios(Attributes var1) {
      CLibrary.Termios var2 = new CLibrary.Termios();
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IGNBRK), 1L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.BRKINT), 2L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IGNPAR), 4L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.PARMRK), 16L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.INPCK), 32L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.ISTRIP), 64L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.INLCR), 256L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IGNCR), 512L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.ICRNL), 1024L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IXON), 8192L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IXOFF), 65536L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IXANY), 16384L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IMAXBEL), 131072L, var2.c_iflag);
      var2.c_iflag = setFlag(var1.getInputFlag(Attributes.InputFlag.IUTF8), 262144L, var2.c_iflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.OPOST), 1L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.ONLCR), 4L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.OCRNL), 16L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.ONOCR), 32L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.ONLRET), 64L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.OFILL), 256L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.NLDLY), 1024L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.TABDLY), 81920L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.CRDLY), 12288L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.FFDLY), 1048576L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.BSDLY), 131072L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.VTDLY), 262144L, var2.c_oflag);
      var2.c_oflag = setFlag(var1.getOutputFlag(Attributes.OutputFlag.OFDEL), 512L, var2.c_oflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.CS5), 0L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.CS6), 32L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.CS7), 64L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.CS8), 96L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.CSTOPB), 256L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.CREAD), 512L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.PARENB), 1024L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.PARODD), 4096L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.HUPCL), 8192L, var2.c_cflag);
      var2.c_cflag = setFlag(var1.getControlFlag(Attributes.ControlFlag.CLOCAL), 16384L, var2.c_cflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ECHOKE), 16384L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ECHOE), 32L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ECHOK), 64L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ECHO), 16L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ECHONL), 256L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ECHOPRT), 8192L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ECHOCTL), 4096L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ISIG), 1L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.ICANON), 2L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.IEXTEN), 1048576L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.EXTPROC), 2097152L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.TOSTOP), 1024L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.FLUSHO), 65536L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.PENDIN), 262144L, var2.c_lflag);
      var2.c_lflag = setFlag(var1.getLocalFlag(Attributes.LocalFlag.NOFLSH), 512L, var2.c_lflag);
      var2.c_cc[4] = (byte)var1.getControlChar(Attributes.ControlChar.VEOF);
      var2.c_cc[11] = (byte)var1.getControlChar(Attributes.ControlChar.VEOL);
      var2.c_cc[16] = (byte)var1.getControlChar(Attributes.ControlChar.VEOL2);
      var2.c_cc[2] = (byte)var1.getControlChar(Attributes.ControlChar.VERASE);
      var2.c_cc[14] = (byte)var1.getControlChar(Attributes.ControlChar.VWERASE);
      var2.c_cc[3] = (byte)var1.getControlChar(Attributes.ControlChar.VKILL);
      var2.c_cc[12] = (byte)var1.getControlChar(Attributes.ControlChar.VREPRINT);
      var2.c_cc[0] = (byte)var1.getControlChar(Attributes.ControlChar.VINTR);
      var2.c_cc[1] = (byte)var1.getControlChar(Attributes.ControlChar.VQUIT);
      var2.c_cc[10] = (byte)var1.getControlChar(Attributes.ControlChar.VSUSP);
      var2.c_cc[8] = (byte)var1.getControlChar(Attributes.ControlChar.VSTART);
      var2.c_cc[9] = (byte)var1.getControlChar(Attributes.ControlChar.VSTOP);
      var2.c_cc[15] = (byte)var1.getControlChar(Attributes.ControlChar.VLNEXT);
      var2.c_cc[13] = (byte)var1.getControlChar(Attributes.ControlChar.VDISCARD);
      var2.c_cc[6] = (byte)var1.getControlChar(Attributes.ControlChar.VMIN);
      var2.c_cc[5] = (byte)var1.getControlChar(Attributes.ControlChar.VTIME);
      return var2;
   }

   @Override
   protected Attributes toAttributes(CLibrary.Termios var1) {
      Attributes var2 = new Attributes();
      EnumSet var3 = var2.getInputFlags();
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IGNBRK, 1);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IGNBRK, 1);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.BRKINT, 2);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IGNPAR, 4);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.PARMRK, 16);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.INPCK, 32);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.ISTRIP, 64);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.INLCR, 256);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IGNCR, 512);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.ICRNL, 1024);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IXON, 8192);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IXOFF, 65536);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IXANY, 16384);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IMAXBEL, 131072);
      addFlag(var1.c_iflag, var3, Attributes.InputFlag.IUTF8, 262144);
      EnumSet var4 = var2.getOutputFlags();
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.OPOST, 1);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.ONLCR, 4);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.OCRNL, 16);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.ONOCR, 32);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.ONLRET, 64);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.OFILL, 256);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.NLDLY, 1024);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.TABDLY, 81920);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.CRDLY, 12288);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.FFDLY, 1048576);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.BSDLY, 131072);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.VTDLY, 262144);
      addFlag(var1.c_oflag, var4, Attributes.OutputFlag.OFDEL, 512);
      EnumSet var5 = var2.getControlFlags();
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.CS5, 0);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.CS6, 32);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.CS7, 64);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.CS8, 96);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.CSTOPB, 256);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.CREAD, 512);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.PARENB, 1024);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.PARODD, 4096);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.HUPCL, 8192);
      addFlag(var1.c_cflag, var5, Attributes.ControlFlag.CLOCAL, 16384);
      EnumSet var6 = var2.getLocalFlags();
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ECHOKE, 16384);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ECHOE, 32);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ECHOK, 64);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ECHO, 16);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ECHONL, 256);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ECHOPRT, 8192);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ECHOCTL, 4096);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ISIG, 1);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.ICANON, 2);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.IEXTEN, 1048576);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.EXTPROC, 2097152);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.TOSTOP, 1024);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.FLUSHO, 65536);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.PENDIN, 262144);
      addFlag(var1.c_lflag, var6, Attributes.LocalFlag.NOFLSH, 512);
      EnumMap var7 = var2.getControlChars();
      var7.put(Attributes.ControlChar.VEOF, Integer.valueOf(var1.c_cc[4]));
      var7.put(Attributes.ControlChar.VEOL, Integer.valueOf(var1.c_cc[11]));
      var7.put(Attributes.ControlChar.VEOL2, Integer.valueOf(var1.c_cc[16]));
      var7.put(Attributes.ControlChar.VERASE, Integer.valueOf(var1.c_cc[2]));
      var7.put(Attributes.ControlChar.VWERASE, Integer.valueOf(var1.c_cc[14]));
      var7.put(Attributes.ControlChar.VKILL, Integer.valueOf(var1.c_cc[3]));
      var7.put(Attributes.ControlChar.VREPRINT, Integer.valueOf(var1.c_cc[12]));
      var7.put(Attributes.ControlChar.VINTR, Integer.valueOf(var1.c_cc[0]));
      var7.put(Attributes.ControlChar.VQUIT, Integer.valueOf(var1.c_cc[1]));
      var7.put(Attributes.ControlChar.VSUSP, Integer.valueOf(var1.c_cc[10]));
      var7.put(Attributes.ControlChar.VSTART, Integer.valueOf(var1.c_cc[8]));
      var7.put(Attributes.ControlChar.VSTOP, Integer.valueOf(var1.c_cc[9]));
      var7.put(Attributes.ControlChar.VLNEXT, Integer.valueOf(var1.c_cc[15]));
      var7.put(Attributes.ControlChar.VDISCARD, Integer.valueOf(var1.c_cc[13]));
      var7.put(Attributes.ControlChar.VMIN, Integer.valueOf(var1.c_cc[6]));
      var7.put(Attributes.ControlChar.VTIME, Integer.valueOf(var1.c_cc[5]));
      return var2;
   }

   private static long setFlag(boolean var0, long var1, long var3) {
      return var0 ? var3 | var1 : var3;
   }

   private static <T extends Enum<T>> void addFlag(long var0, EnumSet<T> var2, T var3, int var4) {
      if ((var0 & (long)var4) != 0L) {
         var2.add(var3);
      }
   }
}
