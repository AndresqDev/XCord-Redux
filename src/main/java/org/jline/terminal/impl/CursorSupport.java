package org.jline.terminal.impl;

import java.io.IOError;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;

public class CursorSupport {
   public static Cursor getCursorPosition(Terminal var0, IntConsumer var1) {
      try {
         String var2 = var0.getStringCapability(InfoCmp.Capability.user6);
         String var3 = var0.getStringCapability(InfoCmp.Capability.user7);
         if (var2 != null && var3 != null) {
            boolean var4 = false;
            StringBuilder var5 = new StringBuilder();
            int var6 = 0;

            while (var6 < var2.length()) {
               char var7;
               switch (var7 = var2.charAt(var6++)) {
                  case '%':
                     var7 = var2.charAt(var6++);
                     switch (var7) {
                        case '%':
                           var5.append('%');
                           continue;
                        case 'd':
                           var5.append("([0-9]+)");
                           continue;
                        case 'i':
                           var4 = true;
                           continue;
                        default:
                           throw new IllegalArgumentException();
                     }
                  case '\\':
                     switch (var2.charAt(var6++)) {
                        case 'E':
                        case 'e':
                           var5.append("\\x1b");
                           continue;
                        default:
                           throw new IllegalArgumentException();
                     }
                  default:
                     switch (var7) {
                        case '[':
                           var5.append('\\');
                        default:
                           var5.append(var7);
                     }
               }
            }

            Pattern var17 = Pattern.compile(var5.toString());
            Curses.tputs(var0.writer(), var3);
            var0.flush();
            StringBuilder var8 = new StringBuilder();
            int var9 = 0;

            while (true) {
               int var10 = var0.reader().read();
               if (var10 < 0) {
                  return null;
               }

               var8.append((char)var10);
               Matcher var11 = var17.matcher(var8.substring(var9));
               if (var11.matches()) {
                  int var12 = Integer.parseInt(var11.group(1));
                  int var13 = Integer.parseInt(var11.group(2));
                  if (var4) {
                     var13--;
                     var12--;
                  }

                  if (var1 != null) {
                     for (int var14 = 0; var14 < var9; var14++) {
                        var1.accept(var8.charAt(var14));
                     }
                  }

                  return new Cursor(var13, var12);
               }

               if (!var11.hitEnd()) {
                  var9++;
               }
            }
         } else {
            return null;
         }
      } catch (IOException var15) {
         throw new IOError(var15);
      }
   }
}
