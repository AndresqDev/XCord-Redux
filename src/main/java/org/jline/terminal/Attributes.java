package org.jline.terminal;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Attributes {
   final EnumSet<Attributes.InputFlag> iflag = EnumSet.noneOf(Attributes.InputFlag.class);
   final EnumSet<Attributes.OutputFlag> oflag = EnumSet.noneOf(Attributes.OutputFlag.class);
   final EnumSet<Attributes.ControlFlag> cflag = EnumSet.noneOf(Attributes.ControlFlag.class);
   final EnumSet<Attributes.LocalFlag> lflag = EnumSet.noneOf(Attributes.LocalFlag.class);
   final EnumMap<Attributes.ControlChar, Integer> cchars = new EnumMap<>(Attributes.ControlChar.class);

   public Attributes() {
   }

   public Attributes(Attributes var1) {
      this.copy(var1);
   }

   public EnumSet<Attributes.InputFlag> getInputFlags() {
      return this.iflag;
   }

   public void setInputFlags(EnumSet<Attributes.InputFlag> var1) {
      this.iflag.clear();
      this.iflag.addAll(var1);
   }

   public boolean getInputFlag(Attributes.InputFlag var1) {
      return this.iflag.contains(var1);
   }

   public void setInputFlags(EnumSet<Attributes.InputFlag> var1, boolean var2) {
      if (var2) {
         this.iflag.addAll(var1);
      } else {
         this.iflag.removeAll(var1);
      }
   }

   public void setInputFlag(Attributes.InputFlag var1, boolean var2) {
      if (var2) {
         this.iflag.add(var1);
      } else {
         this.iflag.remove(var1);
      }
   }

   public EnumSet<Attributes.OutputFlag> getOutputFlags() {
      return this.oflag;
   }

   public void setOutputFlags(EnumSet<Attributes.OutputFlag> var1) {
      this.oflag.clear();
      this.oflag.addAll(var1);
   }

   public boolean getOutputFlag(Attributes.OutputFlag var1) {
      return this.oflag.contains(var1);
   }

   public void setOutputFlags(EnumSet<Attributes.OutputFlag> var1, boolean var2) {
      if (var2) {
         this.oflag.addAll(var1);
      } else {
         this.oflag.removeAll(var1);
      }
   }

   public void setOutputFlag(Attributes.OutputFlag var1, boolean var2) {
      if (var2) {
         this.oflag.add(var1);
      } else {
         this.oflag.remove(var1);
      }
   }

   public EnumSet<Attributes.ControlFlag> getControlFlags() {
      return this.cflag;
   }

   public void setControlFlags(EnumSet<Attributes.ControlFlag> var1) {
      this.cflag.clear();
      this.cflag.addAll(var1);
   }

   public boolean getControlFlag(Attributes.ControlFlag var1) {
      return this.cflag.contains(var1);
   }

   public void setControlFlags(EnumSet<Attributes.ControlFlag> var1, boolean var2) {
      if (var2) {
         this.cflag.addAll(var1);
      } else {
         this.cflag.removeAll(var1);
      }
   }

   public void setControlFlag(Attributes.ControlFlag var1, boolean var2) {
      if (var2) {
         this.cflag.add(var1);
      } else {
         this.cflag.remove(var1);
      }
   }

   public EnumSet<Attributes.LocalFlag> getLocalFlags() {
      return this.lflag;
   }

   public void setLocalFlags(EnumSet<Attributes.LocalFlag> var1) {
      this.lflag.clear();
      this.lflag.addAll(var1);
   }

   public boolean getLocalFlag(Attributes.LocalFlag var1) {
      return this.lflag.contains(var1);
   }

   public void setLocalFlags(EnumSet<Attributes.LocalFlag> var1, boolean var2) {
      if (var2) {
         this.lflag.addAll(var1);
      } else {
         this.lflag.removeAll(var1);
      }
   }

   public void setLocalFlag(Attributes.LocalFlag var1, boolean var2) {
      if (var2) {
         this.lflag.add(var1);
      } else {
         this.lflag.remove(var1);
      }
   }

   public EnumMap<Attributes.ControlChar, Integer> getControlChars() {
      return this.cchars;
   }

   public void setControlChars(EnumMap<Attributes.ControlChar, Integer> var1) {
      this.cchars.clear();
      this.cchars.putAll(var1);
   }

   public int getControlChar(Attributes.ControlChar var1) {
      Integer var2 = this.cchars.get(var1);
      return var2 != null ? var2 : -1;
   }

   public void setControlChar(Attributes.ControlChar var1, int var2) {
      this.cchars.put(var1, var2);
   }

   public void copy(Attributes var1) {
      this.setControlFlags(var1.getControlFlags());
      this.setInputFlags(var1.getInputFlags());
      this.setLocalFlags(var1.getLocalFlags());
      this.setOutputFlags(var1.getOutputFlags());
      this.setControlChars(var1.getControlChars());
   }

   @Override
   public String toString() {
      return "Attributes[lflags: "
         + this.append(this.lflag)
         + ", iflags: "
         + this.append(this.iflag)
         + ", oflags: "
         + this.append(this.oflag)
         + ", cflags: "
         + this.append(this.cflag)
         + ", cchars: "
         + this.append(EnumSet.allOf(Attributes.ControlChar.class), this::display)
         + "]";
   }

   private String display(Attributes.ControlChar var1) {
      int var3 = this.getControlChar(var1);
      String var2;
      if (var1 == Attributes.ControlChar.VMIN || var1 == Attributes.ControlChar.VTIME) {
         var2 = Integer.toString(var3);
      } else if (var3 < 0) {
         var2 = "<undef>";
      } else if (var3 < 32) {
         var2 = "^" + (char)(var3 + 65 - 1);
      } else if (var3 == 127) {
         var2 = "^?";
      } else if (var3 >= 128) {
         var2 = String.format("\\u%04x", var3);
      } else {
         var2 = String.valueOf((char)var3);
      }

      return var1.name().toLowerCase().substring(1) + "=" + var2;
   }

   private <T extends Enum<T>> String append(EnumSet<T> var1) {
      return this.append(var1, var0 -> var0.name().toLowerCase());
   }

   private <T extends Enum<T>> String append(EnumSet<T> var1, Function<T, String> var2) {
      return var1.stream().<CharSequence>map(var2).collect(Collectors.joining(" "));
   }

   public static enum ControlChar {
      VEOF,
      VEOL,
      VEOL2,
      VERASE,
      VWERASE,
      VKILL,
      VREPRINT,
      VINTR,
      VQUIT,
      VSUSP,
      VDSUSP,
      VSTART,
      VSTOP,
      VLNEXT,
      VDISCARD,
      VMIN,
      VTIME,
      VSTATUS;
   }

   public static enum ControlFlag {
      CIGNORE,
      CS5,
      CS6,
      CS7,
      CS8,
      CSTOPB,
      CREAD,
      PARENB,
      PARODD,
      HUPCL,
      CLOCAL,
      CCTS_OFLOW,
      CRTS_IFLOW,
      CDTR_IFLOW,
      CDSR_OFLOW,
      CCAR_OFLOW;
   }

   public static enum InputFlag {
      IGNBRK,
      BRKINT,
      IGNPAR,
      PARMRK,
      INPCK,
      ISTRIP,
      INLCR,
      IGNCR,
      ICRNL,
      IXON,
      IXOFF,
      IXANY,
      IMAXBEL,
      IUTF8;
   }

   public static enum LocalFlag {
      ECHOKE,
      ECHOE,
      ECHOK,
      ECHO,
      ECHONL,
      ECHOPRT,
      ECHOCTL,
      ISIG,
      ICANON,
      ALTWERASE,
      IEXTEN,
      EXTPROC,
      TOSTOP,
      FLUSHO,
      NOKERNINFO,
      PENDIN,
      NOFLSH;
   }

   public static enum OutputFlag {
      OPOST,
      ONLCR,
      OXTABS,
      ONOEOT,
      OCRNL,
      ONOCR,
      ONLRET,
      OFILL,
      NLDLY,
      TABDLY,
      CRDLY,
      FFDLY,
      BSDLY,
      VTDLY,
      OFDEL;
   }
}
