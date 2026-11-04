package org.fusesource.jansi;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Ansi {
   private static final char FIRST_ESC_CHAR = '\u001b';
   private static final char SECOND_ESC_CHAR = '[';
   public static final String DISABLE = Ansi.class.getName() + ".disable";
   private static Callable<Boolean> detector = new Callable<Boolean>() {
      public Boolean call() throws Exception {
         return !Boolean.getBoolean(Ansi.DISABLE);
      }
   };
   private static final InheritableThreadLocal<Boolean> holder = new InheritableThreadLocal<Boolean>() {
      protected Boolean initialValue() {
         return Ansi.isDetected();
      }
   };
   private final StringBuilder builder;
   private final ArrayList<Integer> attributeOptions = new ArrayList<>(5);

   public static void setDetector(Callable<Boolean> var0) {
      if (var0 == null) {
         throw new IllegalArgumentException();
      } else {
         detector = var0;
      }
   }

   public static boolean isDetected() {
      try {
         return detector.call();
      } catch (Exception var1) {
         return true;
      }
   }

   public static void setEnabled(boolean var0) {
      holder.set(Boolean.valueOf(var0));
   }

   public static boolean isEnabled() {
      return holder.get();
   }

   public static Ansi ansi() {
      return (Ansi)(isEnabled() ? new Ansi() : new Ansi.NoAnsi());
   }

   public static Ansi ansi(StringBuilder var0) {
      return (Ansi)(isEnabled() ? new Ansi(var0) : new Ansi.NoAnsi(var0));
   }

   public static Ansi ansi(int var0) {
      return (Ansi)(isEnabled() ? new Ansi(var0) : new Ansi.NoAnsi(var0));
   }

   public Ansi() {
      this(new StringBuilder());
   }

   public Ansi(Ansi var1) {
      this(new StringBuilder(var1.builder));
      this.attributeOptions.addAll(var1.attributeOptions);
   }

   public Ansi(int var1) {
      this(new StringBuilder(var1));
   }

   public Ansi(StringBuilder var1) {
      this.builder = var1;
   }

   public Ansi fg(Ansi.Color var1) {
      this.attributeOptions.add(var1.fg());
      return this;
   }

   public Ansi fgBlack() {
      return this.fg(Ansi.Color.BLACK);
   }

   public Ansi fgBlue() {
      return this.fg(Ansi.Color.BLUE);
   }

   public Ansi fgCyan() {
      return this.fg(Ansi.Color.CYAN);
   }

   public Ansi fgDefault() {
      return this.fg(Ansi.Color.DEFAULT);
   }

   public Ansi fgGreen() {
      return this.fg(Ansi.Color.GREEN);
   }

   public Ansi fgMagenta() {
      return this.fg(Ansi.Color.MAGENTA);
   }

   public Ansi fgRed() {
      return this.fg(Ansi.Color.RED);
   }

   public Ansi fgYellow() {
      return this.fg(Ansi.Color.YELLOW);
   }

   public Ansi bg(Ansi.Color var1) {
      this.attributeOptions.add(var1.bg());
      return this;
   }

   public Ansi bgCyan() {
      return this.fg(Ansi.Color.CYAN);
   }

   public Ansi bgDefault() {
      return this.bg(Ansi.Color.DEFAULT);
   }

   public Ansi bgGreen() {
      return this.bg(Ansi.Color.GREEN);
   }

   public Ansi bgMagenta() {
      return this.bg(Ansi.Color.MAGENTA);
   }

   public Ansi bgRed() {
      return this.bg(Ansi.Color.RED);
   }

   public Ansi bgYellow() {
      return this.bg(Ansi.Color.YELLOW);
   }

   public Ansi fgBright(Ansi.Color var1) {
      this.attributeOptions.add(var1.fgBright());
      return this;
   }

   public Ansi fgBrightBlack() {
      return this.fgBright(Ansi.Color.BLACK);
   }

   public Ansi fgBrightBlue() {
      return this.fgBright(Ansi.Color.BLUE);
   }

   public Ansi fgBrightCyan() {
      return this.fgBright(Ansi.Color.CYAN);
   }

   public Ansi fgBrightDefault() {
      return this.fgBright(Ansi.Color.DEFAULT);
   }

   public Ansi fgBrightGreen() {
      return this.fgBright(Ansi.Color.GREEN);
   }

   public Ansi fgBrightMagenta() {
      return this.fgBright(Ansi.Color.MAGENTA);
   }

   public Ansi fgBrightRed() {
      return this.fgBright(Ansi.Color.RED);
   }

   public Ansi fgBrightYellow() {
      return this.fgBright(Ansi.Color.YELLOW);
   }

   public Ansi bgBright(Ansi.Color var1) {
      this.attributeOptions.add(var1.bgBright());
      return this;
   }

   public Ansi bgBrightCyan() {
      return this.fgBright(Ansi.Color.CYAN);
   }

   public Ansi bgBrightDefault() {
      return this.bgBright(Ansi.Color.DEFAULT);
   }

   public Ansi bgBrightGreen() {
      return this.bgBright(Ansi.Color.GREEN);
   }

   public Ansi bgBrightMagenta() {
      return this.bg(Ansi.Color.MAGENTA);
   }

   public Ansi bgBrightRed() {
      return this.bgBright(Ansi.Color.RED);
   }

   public Ansi bgBrightYellow() {
      return this.bgBright(Ansi.Color.YELLOW);
   }

   public Ansi a(Ansi.Attribute var1) {
      this.attributeOptions.add(var1.value());
      return this;
   }

   public Ansi cursor(int var1, int var2) {
      return this.appendEscapeSequence('H', var1, var2);
   }

   public Ansi cursorToColumn(int var1) {
      return this.appendEscapeSequence('G', var1);
   }

   public Ansi cursorUp(int var1) {
      return this.appendEscapeSequence('A', var1);
   }

   public Ansi cursorDown(int var1) {
      return this.appendEscapeSequence('B', var1);
   }

   public Ansi cursorRight(int var1) {
      return this.appendEscapeSequence('C', var1);
   }

   public Ansi cursorLeft(int var1) {
      return this.appendEscapeSequence('D', var1);
   }

   public Ansi cursorDownLine() {
      return this.appendEscapeSequence('E');
   }

   public Ansi cursorDownLine(int var1) {
      return this.appendEscapeSequence('E', var1);
   }

   public Ansi cursorUpLine() {
      return this.appendEscapeSequence('F');
   }

   public Ansi cursorUpLine(int var1) {
      return this.appendEscapeSequence('F', var1);
   }

   public Ansi eraseScreen() {
      return this.appendEscapeSequence('J', Ansi.Erase.ALL.value());
   }

   public Ansi eraseScreen(Ansi.Erase var1) {
      return this.appendEscapeSequence('J', var1.value());
   }

   public Ansi eraseLine() {
      return this.appendEscapeSequence('K');
   }

   public Ansi eraseLine(Ansi.Erase var1) {
      return this.appendEscapeSequence('K', var1.value());
   }

   public Ansi scrollUp(int var1) {
      return this.appendEscapeSequence('S', var1);
   }

   public Ansi scrollDown(int var1) {
      return this.appendEscapeSequence('T', var1);
   }

   public Ansi saveCursorPosition() {
      return this.appendEscapeSequence('s');
   }

   @Deprecated
   public Ansi restorCursorPosition() {
      return this.appendEscapeSequence('u');
   }

   public Ansi restoreCursorPosition() {
      return this.appendEscapeSequence('u');
   }

   public Ansi reset() {
      return this.a(Ansi.Attribute.RESET);
   }

   public Ansi bold() {
      return this.a(Ansi.Attribute.INTENSITY_BOLD);
   }

   public Ansi boldOff() {
      return this.a(Ansi.Attribute.INTENSITY_BOLD_OFF);
   }

   public Ansi a(String var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(boolean var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(char var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(char[] var1, int var2, int var3) {
      this.flushAttributes();
      this.builder.append(var1, var2, var3);
      return this;
   }

   public Ansi a(char[] var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(CharSequence var1, int var2, int var3) {
      this.flushAttributes();
      this.builder.append(var1, var2, var3);
      return this;
   }

   public Ansi a(CharSequence var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(double var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(float var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(int var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(long var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(Object var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi a(StringBuffer var1) {
      this.flushAttributes();
      this.builder.append(var1);
      return this;
   }

   public Ansi newline() {
      this.flushAttributes();
      this.builder.append(System.getProperty("line.separator"));
      return this;
   }

   public Ansi format(String var1, Object... var2) {
      this.flushAttributes();
      this.builder.append(String.format(var1, var2));
      return this;
   }

   public Ansi render(String var1) {
      this.a(AnsiRenderer.render(var1));
      return this;
   }

   public Ansi render(String var1, Object... var2) {
      this.a(String.format(AnsiRenderer.render(var1), var2));
      return this;
   }

   @Override
   public String toString() {
      this.flushAttributes();
      return this.builder.toString();
   }

   private Ansi appendEscapeSequence(char var1) {
      this.flushAttributes();
      this.builder.append('\u001b');
      this.builder.append('[');
      this.builder.append(var1);
      return this;
   }

   private Ansi appendEscapeSequence(char var1, int var2) {
      this.flushAttributes();
      this.builder.append('\u001b');
      this.builder.append('[');
      this.builder.append(var2);
      this.builder.append(var1);
      return this;
   }

   private Ansi appendEscapeSequence(char var1, Object... var2) {
      this.flushAttributes();
      return this._appendEscapeSequence(var1, var2);
   }

   private void flushAttributes() {
      if (!this.attributeOptions.isEmpty()) {
         if (this.attributeOptions.size() == 1 && this.attributeOptions.get(0) == 0) {
            this.builder.append('\u001b');
            this.builder.append('[');
            this.builder.append('m');
         } else {
            this._appendEscapeSequence('m', this.attributeOptions.toArray());
         }

         this.attributeOptions.clear();
      }
   }

   private Ansi _appendEscapeSequence(char var1, Object... var2) {
      this.builder.append('\u001b');
      this.builder.append('[');
      int var3 = var2.length;

      for (int var4 = 0; var4 < var3; var4++) {
         if (var4 != 0) {
            this.builder.append(';');
         }

         if (var2[var4] != null) {
            this.builder.append(var2[var4]);
         }
      }

      this.builder.append(var1);
      return this;
   }

   public static enum Attribute {
      RESET(0, "RESET"),
      INTENSITY_BOLD(1, "INTENSITY_BOLD"),
      INTENSITY_FAINT(2, "INTENSITY_FAINT"),
      ITALIC(3, "ITALIC_ON"),
      UNDERLINE(4, "UNDERLINE_ON"),
      BLINK_SLOW(5, "BLINK_SLOW"),
      BLINK_FAST(6, "BLINK_FAST"),
      NEGATIVE_ON(7, "NEGATIVE_ON"),
      CONCEAL_ON(8, "CONCEAL_ON"),
      STRIKETHROUGH_ON(9, "STRIKETHROUGH_ON"),
      UNDERLINE_DOUBLE(21, "UNDERLINE_DOUBLE"),
      INTENSITY_BOLD_OFF(22, "INTENSITY_BOLD_OFF"),
      ITALIC_OFF(23, "ITALIC_OFF"),
      UNDERLINE_OFF(24, "UNDERLINE_OFF"),
      BLINK_OFF(25, "BLINK_OFF"),
      NEGATIVE_OFF(27, "NEGATIVE_OFF"),
      CONCEAL_OFF(28, "CONCEAL_OFF"),
      STRIKETHROUGH_OFF(29, "STRIKETHROUGH_OFF");

      private final int value;
      private final String name;

      private Attribute(int var3, String var4) {
         this.value = var3;
         this.name = var4;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public int value() {
         return this.value;
      }
   }

   public static enum Color {
      BLACK(0, "BLACK"),
      RED(1, "RED"),
      GREEN(2, "GREEN"),
      YELLOW(3, "YELLOW"),
      BLUE(4, "BLUE"),
      MAGENTA(5, "MAGENTA"),
      CYAN(6, "CYAN"),
      WHITE(7, "WHITE"),
      DEFAULT(9, "DEFAULT");

      private final int value;
      private final String name;

      private Color(int var3, String var4) {
         this.value = var3;
         this.name = var4;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public int value() {
         return this.value;
      }

      public int fg() {
         return this.value + 30;
      }

      public int bg() {
         return this.value + 40;
      }

      public int fgBright() {
         return this.value + 90;
      }

      public int bgBright() {
         return this.value + 100;
      }
   }

   public static enum Erase {
      FORWARD(0, "FORWARD"),
      BACKWARD(1, "BACKWARD"),
      ALL(2, "ALL");

      private final int value;
      private final String name;

      private Erase(int var3, String var4) {
         this.value = var3;
         this.name = var4;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public int value() {
         return this.value;
      }
   }

   private static class NoAnsi extends Ansi {
      public NoAnsi() {
      }

      public NoAnsi(int var1) {
         super(var1);
      }

      public NoAnsi(StringBuilder var1) {
         super(var1);
      }

      @Override
      public Ansi fg(Ansi.Color var1) {
         return this;
      }

      @Override
      public Ansi bg(Ansi.Color var1) {
         return this;
      }

      @Override
      public Ansi fgBright(Ansi.Color var1) {
         return this;
      }

      @Override
      public Ansi bgBright(Ansi.Color var1) {
         return this;
      }

      @Override
      public Ansi a(Ansi.Attribute var1) {
         return this;
      }

      @Override
      public Ansi cursor(int var1, int var2) {
         return this;
      }

      @Override
      public Ansi cursorToColumn(int var1) {
         return this;
      }

      @Override
      public Ansi cursorUp(int var1) {
         return this;
      }

      @Override
      public Ansi cursorRight(int var1) {
         return this;
      }

      @Override
      public Ansi cursorDown(int var1) {
         return this;
      }

      @Override
      public Ansi cursorLeft(int var1) {
         return this;
      }

      @Override
      public Ansi cursorDownLine() {
         return this;
      }

      @Override
      public Ansi cursorDownLine(int var1) {
         return this;
      }

      @Override
      public Ansi cursorUpLine() {
         return this;
      }

      @Override
      public Ansi cursorUpLine(int var1) {
         return this;
      }

      @Override
      public Ansi eraseScreen() {
         return this;
      }

      @Override
      public Ansi eraseScreen(Ansi.Erase var1) {
         return this;
      }

      @Override
      public Ansi eraseLine() {
         return this;
      }

      @Override
      public Ansi eraseLine(Ansi.Erase var1) {
         return this;
      }

      @Override
      public Ansi scrollUp(int var1) {
         return this;
      }

      @Override
      public Ansi scrollDown(int var1) {
         return this;
      }

      @Override
      public Ansi saveCursorPosition() {
         return this;
      }

      @Deprecated
      @Override
      public Ansi restorCursorPosition() {
         return this;
      }

      @Override
      public Ansi restoreCursorPosition() {
         return this;
      }

      @Override
      public Ansi reset() {
         return this;
      }
   }
}
