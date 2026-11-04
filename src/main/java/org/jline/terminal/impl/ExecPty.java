package org.jline.terminal.impl;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.spi.Pty;
import org.jline.utils.ExecHelper;
import org.jline.utils.OSUtils;

public class ExecPty extends AbstractPty implements Pty {
   private final String name;
   private final boolean system;

   public static Pty current() throws IOException {
      try {
         String var0 = ExecHelper.exec(true, OSUtils.TTY_COMMAND);
         return new ExecPty(var0.trim(), true);
      } catch (IOException var1) {
         throw new IOException("Not a tty", var1);
      }
   }

   protected ExecPty(String var1, boolean var2) {
      this.name = var1;
      this.system = var2;
   }

   @Override
   public void close() throws IOException {
   }

   public String getName() {
      return this.name;
   }

   @Override
   public InputStream getMasterInput() {
      throw new UnsupportedOperationException();
   }

   @Override
   public OutputStream getMasterOutput() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected InputStream doGetSlaveInput() throws IOException {
      return this.system ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.getName());
   }

   @Override
   public OutputStream getSlaveOutput() throws IOException {
      return this.system ? new FileOutputStream(FileDescriptor.out) : new FileOutputStream(this.getName());
   }

   @Override
   public Attributes getAttr() throws IOException {
      String var1 = this.doGetConfig();
      return doGetAttr(var1);
   }

   @Override
   protected void doSetAttr(Attributes var1) throws IOException {
      List var2 = this.getFlagsToSet(var1, this.getAttr());
      if (!var2.isEmpty()) {
         var2.add(0, OSUtils.STTY_COMMAND);
         if (!this.system) {
            var2.add(1, OSUtils.STTY_F_OPTION);
            var2.add(2, this.getName());
         }

         try {
            ExecHelper.exec(this.system, var2.toArray(new String[var2.size()]));
         } catch (IOException var4) {
            if (!var4.toString().contains("unable to perform all requested operations")) {
               throw var4;
            }

            var2 = this.getFlagsToSet(var1, this.getAttr());
            if (!var2.isEmpty()) {
               throw new IOException("Could not set the following flags: " + String.join(", ", var2), var4);
            }
         }
      }
   }

   protected List<String> getFlagsToSet(Attributes var1, Attributes var2) {
      ArrayList var3 = new ArrayList();

      for (Attributes.InputFlag var7 : Attributes.InputFlag.values()) {
         if (var1.getInputFlag(var7) != var2.getInputFlag(var7)) {
            var3.add((var1.getInputFlag(var7) ? var7.name() : "-" + var7.name()).toLowerCase());
         }
      }

      for (Attributes.OutputFlag var23 : Attributes.OutputFlag.values()) {
         if (var1.getOutputFlag(var23) != var2.getOutputFlag(var23)) {
            var3.add((var1.getOutputFlag(var23) ? var23.name() : "-" + var23.name()).toLowerCase());
         }
      }

      for (Attributes.ControlFlag var24 : Attributes.ControlFlag.values()) {
         if (var1.getControlFlag(var24) != var2.getControlFlag(var24)) {
            var3.add((var1.getControlFlag(var24) ? var24.name() : "-" + var24.name()).toLowerCase());
         }
      }

      for (Attributes.LocalFlag var25 : Attributes.LocalFlag.values()) {
         if (var1.getLocalFlag(var25) != var2.getLocalFlag(var25)) {
            var3.add((var1.getLocalFlag(var25) ? var25.name() : "-" + var25.name()).toLowerCase());
         }
      }

      String var14 = System.getProperty("os.name").toLowerCase().startsWith("hp") ? "^-" : "undef";

      for (Attributes.ControlChar var8 : Attributes.ControlChar.values()) {
         int var9 = var1.getControlChar(var8);
         if (var9 >= 0 && var9 != var2.getControlChar(var8)) {
            String var10 = "";
            var3.add(var8.name().toLowerCase().substring(1));
            if (var8 == Attributes.ControlChar.VMIN || var8 == Attributes.ControlChar.VTIME) {
               var3.add(Integer.toString(var9));
            } else if (var9 == 0) {
               var3.add(var14);
            } else {
               if (var9 >= 128) {
                  var9 -= 128;
                  var10 = var10 + "M-";
               }

               if (var9 < 32 || var9 == 127) {
                  var9 ^= 64;
                  var10 = var10 + "^";
               }

               var10 = var10 + (char)var9;
               var3.add(var10);
            }
         }
      }

      return var3;
   }

   @Override
   public Size getSize() throws IOException {
      String var1 = this.doGetConfig();
      return doGetSize(var1);
   }

   protected String doGetConfig() throws IOException {
      return this.system
         ? ExecHelper.exec(true, OSUtils.STTY_COMMAND, "-a")
         : ExecHelper.exec(false, OSUtils.STTY_COMMAND, OSUtils.STTY_F_OPTION, this.getName(), "-a");
   }

   static Attributes doGetAttr(String var0) throws IOException {
      Attributes var1 = new Attributes();

      for (Attributes.InputFlag var5 : Attributes.InputFlag.values()) {
         Boolean var6 = doGetFlag(var0, var5);
         if (var6 != null) {
            var1.setInputFlag(var5, var6);
         }
      }

      for (Attributes.OutputFlag var20 : Attributes.OutputFlag.values()) {
         Boolean var24 = doGetFlag(var0, var20);
         if (var24 != null) {
            var1.setOutputFlag(var20, var24);
         }
      }

      for (Attributes.ControlFlag var21 : Attributes.ControlFlag.values()) {
         Boolean var25 = doGetFlag(var0, var21);
         if (var25 != null) {
            var1.setControlFlag(var21, var25);
         }
      }

      for (Attributes.LocalFlag var22 : Attributes.LocalFlag.values()) {
         Boolean var26 = doGetFlag(var0, var22);
         if (var26 != null) {
            var1.setLocalFlag(var22, var26);
         }
      }

      for (Attributes.ControlChar var23 : Attributes.ControlChar.values()) {
         String var27 = var23.name().toLowerCase().substring(1);
         if ("reprint".endsWith(var27)) {
            var27 = "(?:reprint|rprnt)";
         }

         Matcher var7 = Pattern.compile("[\\s;]" + var27 + "\\s*=\\s*(.+?)[\\s;]").matcher(var0);
         if (var7.find()) {
            var1.setControlChar(var23, parseControlChar(var7.group(1).toUpperCase()));
         }
      }

      return var1;
   }

   private static Boolean doGetFlag(String var0, Enum<?> var1) {
      Matcher var2 = Pattern.compile("(?:^|[\\s;])(\\-?" + var1.name().toLowerCase() + ")(?:[\\s;]|$)").matcher(var0);
      return var2.find() ? !var2.group(1).startsWith("-") : null;
   }

   static int parseControlChar(String var0) {
      if ("<UNDEF>".equals(var0)) {
         return -1;
      } else if ("DEL".equalsIgnoreCase(var0)) {
         return 127;
      } else if (var0.charAt(0) == '0') {
         return Integer.parseInt(var0, 8);
      } else if (var0.charAt(0) >= '1' && var0.charAt(0) <= '9') {
         return Integer.parseInt(var0, 10);
      } else if (var0.charAt(0) == '^') {
         return var0.charAt(1) == 63 ? 127 : var0.charAt(1) - 64;
      } else if (var0.charAt(0) != 'M' || var0.charAt(1) != '-') {
         return var0.charAt(0);
      } else if (var0.charAt(2) != '^') {
         return var0.charAt(2) + 128;
      } else {
         return var0.charAt(3) == 63 ? 255 : var0.charAt(3) - 64 + 128;
      }
   }

   static Size doGetSize(String var0) throws IOException {
      return new Size(doGetInt("columns", var0), doGetInt("rows", var0));
   }

   static int doGetInt(String var0, String var1) throws IOException {
      String[] var2 = new String[]{"\\b([0-9]+)\\s+" + var0 + "\\b", "\\b" + var0 + "\\s+([0-9]+)\\b", "\\b" + var0 + "\\s*=\\s*([0-9]+)\\b"};

      for (String var6 : var2) {
         Matcher var7 = Pattern.compile(var6).matcher(var1);
         if (var7.find()) {
            return Integer.parseInt(var7.group(1));
         }
      }

      throw new IOException("Unable to parse " + var0);
   }

   @Override
   public void setSize(Size var1) throws IOException {
      if (this.system) {
         ExecHelper.exec(true, OSUtils.STTY_COMMAND, "columns", Integer.toString(var1.getColumns()), "rows", Integer.toString(var1.getRows()));
      } else {
         ExecHelper.exec(
            false,
            OSUtils.STTY_COMMAND,
            OSUtils.STTY_F_OPTION,
            this.getName(),
            "columns",
            Integer.toString(var1.getColumns()),
            "rows",
            Integer.toString(var1.getRows())
         );
      }
   }

   @Override
   public String toString() {
      return "ExecPty[" + this.getName() + (this.system ? ", system]" : "]");
   }
}
