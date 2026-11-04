package org.jline.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StyleResolver {
   private static final Logger log = Logger.getLogger(StyleResolver.class.getName());
   private final Function<String, String> source;

   public StyleResolver(Function<String, String> var1) {
      this.source = Objects.requireNonNull(var1);
   }

   private static Integer color(String var0) {
      byte var1 = 0;
      var0 = var0.toLowerCase(Locale.US);
      if (var0.charAt(0) == '!') {
         var0 = var0.substring(1, var0.length());
         var1 = 8;
      } else if (var0.startsWith("bright-")) {
         var0 = var0.substring(7, var0.length());
         var1 = 8;
      } else if (var0.charAt(0) == '~') {
         try {
            var0 = var0.substring(1, var0.length());
            return Colors.rgbColor(var0);
         } catch (IllegalArgumentException var4) {
            log.warning("Invalid style-color name: " + var0);
            return null;
         }
      }

      switch (var0) {
         case "black":
         case "k":
            return var1 + 0;
         case "red":
         case "r":
            return var1 + 1;
         case "green":
         case "g":
            return var1 + 2;
         case "yellow":
         case "y":
            return var1 + 3;
         case "blue":
         case "b":
            return var1 + 4;
         case "magenta":
         case "m":
            return var1 + 5;
         case "cyan":
         case "c":
            return var1 + 6;
         case "white":
         case "w":
            return var1 + 7;
         default:
            return null;
      }
   }

   public AttributedStyle resolve(String var1) {
      Objects.requireNonNull(var1);
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Resolve: " + var1);
      }

      int var2 = var1.indexOf(":-");
      if (var2 != -1) {
         String[] var3 = var1.split(":-");
         return this.resolve(var3[0].trim(), var3[1].trim());
      } else {
         return this.apply(AttributedStyle.DEFAULT, var1);
      }
   }

   public AttributedStyle resolve(String var1, String var2) {
      Objects.requireNonNull(var1);
      if (log.isLoggable(Level.FINEST)) {
         log.finest(String.format("Resolve: %s; default: %s", var1, var2));
      }

      AttributedStyle var3 = this.apply(AttributedStyle.DEFAULT, var1);
      if (var3 == AttributedStyle.DEFAULT && var2 != null) {
         var3 = this.apply(var3, var2);
      }

      return var3;
   }

   private AttributedStyle apply(AttributedStyle var1, String var2) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply: " + var2);
      }

      for (String var6 : var2.split(",")) {
         var6 = var6.trim();
         if (!var6.isEmpty()) {
            if (var6.startsWith(".")) {
               var1 = this.applyReference(var1, var6);
            } else if (var6.contains(":")) {
               var1 = this.applyColor(var1, var6);
            } else if (var6.matches("[0-9]+(;[0-9]+)*")) {
               var1 = this.applyAnsi(var1, var6);
            } else {
               var1 = this.applyNamed(var1, var6);
            }
         }
      }

      return var1;
   }

   private AttributedStyle applyAnsi(AttributedStyle var1, String var2) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-ansi: " + var2);
      }

      return new AttributedStringBuilder().style(var1).ansiAppend("\u001b[" + var2 + "m").style();
   }

   private AttributedStyle applyReference(AttributedStyle var1, String var2) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-reference: " + var2);
      }

      if (var2.length() == 1) {
         log.warning("Invalid style-reference; missing discriminator: " + var2);
      } else {
         String var3 = var2.substring(1, var2.length());
         String var4 = this.source.apply(var3);
         if (var4 != null) {
            return this.apply(var1, var4);
         }
      }

      return var1;
   }

   private AttributedStyle applyNamed(AttributedStyle var1, String var2) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-named: " + var2);
      }

      String var3 = var2.toLowerCase(Locale.US);
      switch (var3) {
         case "default":
            return AttributedStyle.DEFAULT;
         case "bold":
            return var1.bold();
         case "faint":
            return var1.faint();
         case "italic":
            return var1.italic();
         case "underline":
            return var1.underline();
         case "blink":
            return var1.blink();
         case "inverse":
            return var1.inverse();
         case "inverse-neg":
         case "inverseneg":
            return var1.inverseNeg();
         case "conceal":
            return var1.conceal();
         case "crossed-out":
         case "crossedout":
            return var1.crossedOut();
         case "hidden":
            return var1.hidden();
         default:
            log.warning("Unknown style: " + var2);
            return var1;
      }
   }

   private AttributedStyle applyColor(AttributedStyle var1, String var2) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-color: " + var2);
      }

      String[] var3 = var2.split(":", 2);
      String var4 = var3[0].trim();
      String var5 = var3[1].trim();
      Integer var6 = color(var5);
      if (var6 == null) {
         log.warning("Invalid color-name: " + var5);
      } else {
         String var7 = var4.toLowerCase(Locale.US);
         switch (var7) {
            case "foreground":
            case "fg":
            case "f":
               return var1.foreground(var6);
            case "background":
            case "bg":
            case "b":
               return var1.background(var6);
            default:
               log.warning("Invalid color-mode: " + var4);
         }
      }

      return var1;
   }
}
