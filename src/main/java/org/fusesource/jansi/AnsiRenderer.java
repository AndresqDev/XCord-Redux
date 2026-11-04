package org.fusesource.jansi;

import java.io.IOException;
import java.util.Locale;

public class AnsiRenderer {
   public static final String BEGIN_TOKEN = "@|";
   public static final String END_TOKEN = "|@";
   public static final String CODE_TEXT_SEPARATOR = " ";
   public static final String CODE_LIST_SEPARATOR = ",";
   private static final int BEGIN_TOKEN_LEN = 2;
   private static final int END_TOKEN_LEN = 2;

   public static String render(String var0) throws IllegalArgumentException {
      try {
         return render(var0, new StringBuilder()).toString();
      } catch (IOException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public static Appendable render(String var0, Appendable var1) throws IOException {
      int var2 = 0;

      while (true) {
         int var3 = var0.indexOf("@|", var2);
         if (var3 == -1) {
            if (var2 == 0) {
               var1.append(var0);
               return var1;
            }

            var1.append(var0.substring(var2, var0.length()));
            return var1;
         }

         var1.append(var0.substring(var2, var3));
         int var4 = var0.indexOf("|@", var3);
         if (var4 == -1) {
            var1.append(var0);
            return var1;
         }

         var3 += 2;
         String var5 = var0.substring(var3, var4);
         String[] var6 = var5.split(" ", 2);
         if (var6.length == 1) {
            var1.append(var0);
            return var1;
         }

         String var7 = render(var6[1], var6[0].split(","));
         var1.append(var7);
         var2 = var4 + 2;
      }
   }

   public static String render(String var0, String... var1) {
      return render(Ansi.ansi(), var1).a(var0).reset().toString();
   }

   public static String renderCodes(String... var0) {
      return render(Ansi.ansi(), var0).toString();
   }

   public static String renderCodes(String var0) {
      return renderCodes(var0.split("\\s"));
   }

   private static Ansi render(Ansi var0, String... var1) {
      for (String var5 : var1) {
         render(var0, var5);
      }

      return var0;
   }

   private static Ansi render(Ansi var0, String var1) {
      AnsiRenderer.Code var2 = AnsiRenderer.Code.valueOf(var1.toUpperCase(Locale.ENGLISH));
      if (var2.isColor()) {
         if (var2.isBackground()) {
            var0.bg(var2.getColor());
         } else {
            var0.fg(var2.getColor());
         }
      } else if (var2.isAttribute()) {
         var0.a(var2.getAttribute());
      }

      return var0;
   }

   public static boolean test(String var0) {
      return var0 != null && var0.contains("@|");
   }

   private AnsiRenderer() {
   }

   public static enum Code {
      BLACK(Ansi.Color.BLACK),
      RED(Ansi.Color.RED),
      GREEN(Ansi.Color.GREEN),
      YELLOW(Ansi.Color.YELLOW),
      BLUE(Ansi.Color.BLUE),
      MAGENTA(Ansi.Color.MAGENTA),
      CYAN(Ansi.Color.CYAN),
      WHITE(Ansi.Color.WHITE),
      FG_BLACK(Ansi.Color.BLACK, false),
      FG_RED(Ansi.Color.RED, false),
      FG_GREEN(Ansi.Color.GREEN, false),
      FG_YELLOW(Ansi.Color.YELLOW, false),
      FG_BLUE(Ansi.Color.BLUE, false),
      FG_MAGENTA(Ansi.Color.MAGENTA, false),
      FG_CYAN(Ansi.Color.CYAN, false),
      FG_WHITE(Ansi.Color.WHITE, false),
      BG_BLACK(Ansi.Color.BLACK, true),
      BG_RED(Ansi.Color.RED, true),
      BG_GREEN(Ansi.Color.GREEN, true),
      BG_YELLOW(Ansi.Color.YELLOW, true),
      BG_BLUE(Ansi.Color.BLUE, true),
      BG_MAGENTA(Ansi.Color.MAGENTA, true),
      BG_CYAN(Ansi.Color.CYAN, true),
      BG_WHITE(Ansi.Color.WHITE, true),
      RESET(Ansi.Attribute.RESET),
      INTENSITY_BOLD(Ansi.Attribute.INTENSITY_BOLD),
      INTENSITY_FAINT(Ansi.Attribute.INTENSITY_FAINT),
      ITALIC(Ansi.Attribute.ITALIC),
      UNDERLINE(Ansi.Attribute.UNDERLINE),
      BLINK_SLOW(Ansi.Attribute.BLINK_SLOW),
      BLINK_FAST(Ansi.Attribute.BLINK_FAST),
      BLINK_OFF(Ansi.Attribute.BLINK_OFF),
      NEGATIVE_ON(Ansi.Attribute.NEGATIVE_ON),
      NEGATIVE_OFF(Ansi.Attribute.NEGATIVE_OFF),
      CONCEAL_ON(Ansi.Attribute.CONCEAL_ON),
      CONCEAL_OFF(Ansi.Attribute.CONCEAL_OFF),
      UNDERLINE_DOUBLE(Ansi.Attribute.UNDERLINE_DOUBLE),
      UNDERLINE_OFF(Ansi.Attribute.UNDERLINE_OFF),
      BOLD(Ansi.Attribute.INTENSITY_BOLD),
      FAINT(Ansi.Attribute.INTENSITY_FAINT);

      private final Enum<?> n;
      private final boolean background;

      private Code(Enum<?> var3, boolean var4) {
         this.n = var3;
         this.background = var4;
      }

      private Code(Enum<?> var3) {
         this(var3, false);
      }

      public boolean isColor() {
         return this.n instanceof Ansi.Color;
      }

      public Ansi.Color getColor() {
         return (Ansi.Color)this.n;
      }

      public boolean isAttribute() {
         return this.n instanceof Ansi.Attribute;
      }

      public Ansi.Attribute getAttribute() {
         return (Ansi.Attribute)this.n;
      }

      public boolean isBackground() {
         return this.background;
      }
   }
}
