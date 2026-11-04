package org.jline.utils;

import java.io.Flushable;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Stack;

public final class Curses {
   private static Object[] sv = new Object[26];
   private static Object[] dv = new Object[26];
   private static final int IFTE_NONE = 0;
   private static final int IFTE_IF = 1;
   private static final int IFTE_THEN = 2;
   private static final int IFTE_ELSE = 3;

   private Curses() {
   }

   public static String tputs(String var0, Object... var1) {
      if (var0 != null) {
         StringWriter var2 = new StringWriter();
         tputs(var2, var0, var1);
         return var2.toString();
      } else {
         return null;
      }
   }

   public static void tputs(Appendable var0, String var1, Object... var2) {
      try {
         doTputs(var0, var1, var2);
      } catch (Exception var4) {
         throw new IOError(var4);
      }
   }

   private static void doTputs(Appendable var0, String var1, Object... var2) throws IOException {
      int var3 = 0;
      int var4 = var1.length();
      byte var5 = 0;
      boolean var6 = true;
      Stack var7 = new Stack();

      while (var3 < var4) {
         char var8 = var1.charAt(var3++);
         switch (var8) {
            case '$':
               if (var1.charAt(var3) != '<') {
                  if (var6) {
                     var0.append(var8);
                  }
               } else {
                  int var25 = 0;

                  while ((var8 = var1.charAt(++var3)) != '>') {
                     if (var8 >= '0' && var8 <= '9') {
                        var25 = var25 * 10 + (var8 - '0');
                     } else if (var8 != '*' && var8 == '/') {
                     }
                  }

                  var3++;

                  try {
                     if (var0 instanceof Flushable) {
                        ((Flushable)var0).flush();
                     }

                     Thread.sleep((long)var25);
                  } catch (InterruptedException var12) {
                  }
               }
               break;
            case '%':
               var8 = var1.charAt(var3++);
               switch (var8) {
                  case '!':
                     if (var6) {
                        int var41 = toInteger(var7.pop());
                        var7.push(var41 == 0);
                     }
                     continue;
                  case '"':
                  case '#':
                  case '$':
                  case '(':
                  case ')':
                  case ',':
                  case '.':
                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                  case ':':
                  case '@':
                  case 'B':
                  case 'C':
                  case 'D':
                  case 'E':
                  case 'F':
                  case 'G':
                  case 'H':
                  case 'I':
                  case 'J':
                  case 'K':
                  case 'L':
                  case 'M':
                  case 'N':
                  case 'Q':
                  case 'R':
                  case 'S':
                  case 'T':
                  case 'U':
                  case 'V':
                  case 'W':
                  case 'X':
                  case 'Y':
                  case 'Z':
                  case '[':
                  case '\\':
                  case ']':
                  case '_':
                  case '`':
                  case 'a':
                  case 'b':
                  case 'c':
                  case 'f':
                  case 'h':
                  case 'j':
                  case 'k':
                  case 'n':
                  case 'o':
                  case 'q':
                  case 'r':
                  case 's':
                  case 'u':
                  case 'v':
                  case 'w':
                  case 'x':
                  case 'y':
                  case 'z':
                  case '}':
                  default:
                     throw new UnsupportedOperationException();
                  case '%':
                     if (var6) {
                        var0.append('%');
                     }
                     continue;
                  case '&':
                     if (var6) {
                        int var40 = toInteger(var7.pop());
                        int var53 = toInteger(var7.pop());
                        var7.push(var53 & var40);
                     }
                     continue;
                  case '\'':
                     var8 = var1.charAt(var3++);
                     if (var6) {
                        var7.push(Integer.valueOf(var8));
                     }

                     var8 = var1.charAt(var3++);
                     if (var8 != '\'') {
                        throw new IllegalArgumentException();
                     }
                     continue;
                  case '*':
                     if (var6) {
                        int var39 = toInteger(var7.pop());
                        int var52 = toInteger(var7.pop());
                        var7.push(var52 * var39);
                     }
                     continue;
                  case '+':
                     if (var6) {
                        int var38 = toInteger(var7.pop());
                        int var51 = toInteger(var7.pop());
                        var7.push(var51 + var38);
                     }
                     continue;
                  case '-':
                     if (var6) {
                        int var37 = toInteger(var7.pop());
                        int var50 = toInteger(var7.pop());
                        var7.push(var50 - var37);
                     }
                     continue;
                  case '/':
                     if (var6) {
                        int var36 = toInteger(var7.pop());
                        int var49 = toInteger(var7.pop());
                        var7.push(var49 / var36);
                     }
                     continue;
                  case ';':
                     if (var5 != 0 && var5 != 1) {
                        var5 = 0;
                        var6 = true;
                        continue;
                     }

                     throw new IllegalArgumentException();
                  case '<':
                     if (var6) {
                        int var35 = toInteger(var7.pop());
                        int var48 = toInteger(var7.pop());
                        var7.push(var48 < var35);
                     }
                     continue;
                  case '=':
                     if (var6) {
                        int var34 = toInteger(var7.pop());
                        int var47 = toInteger(var7.pop());
                        var7.push(var47 == var34);
                     }
                     continue;
                  case '>':
                     if (var6) {
                        int var33 = toInteger(var7.pop());
                        int var46 = toInteger(var7.pop());
                        var7.push(var46 > var33);
                     }
                     continue;
                  case '?':
                     if (var5 != 0) {
                        throw new IllegalArgumentException();
                     }

                     var5 = 1;
                     continue;
                  case 'A':
                     if (var6) {
                        int var32 = toInteger(var7.pop());
                        int var45 = toInteger(var7.pop());
                        var7.push(var45 != 0 && var32 != 0);
                     }
                     continue;
                  case 'O':
                     if (var6) {
                        int var31 = toInteger(var7.pop());
                        int var44 = toInteger(var7.pop());
                        var7.push(var44 != 0 || var31 != 0);
                     }
                     continue;
                  case 'P':
                     var8 = var1.charAt(var3++);
                     if (var8 >= 'a' && var8 <= 'z') {
                        if (var6) {
                           dv[var8 - 'a'] = var7.pop();
                        }
                        continue;
                     } else {
                        if (var8 >= 'A' && var8 <= 'Z') {
                           if (var6) {
                              sv[var8 - 'A'] = var7.pop();
                           }
                           continue;
                        }

                        throw new IllegalArgumentException();
                     }
                  case '^':
                     if (var6) {
                        int var30 = toInteger(var7.pop());
                        int var43 = toInteger(var7.pop());
                        var7.push(var43 ^ var30);
                     }
                     continue;
                  case 'd':
                     var0.append(Integer.toString(toInteger(var7.pop())));
                     continue;
                  case 'e':
                     if (var5 != 2) {
                        throw new IllegalArgumentException();
                     }

                     var5 = 3;
                     var6 = !var6;
                     continue;
                  case 'g':
                     var8 = var1.charAt(var3++);
                     if (var8 >= 'a' && var8 <= 'z') {
                        if (var6) {
                           var7.push(dv[var8 - 'a']);
                        }
                        continue;
                     } else {
                        if (var8 >= 'A' && var8 <= 'Z') {
                           if (var6) {
                              var7.push(sv[var8 - 'A']);
                           }
                           continue;
                        }

                        throw new IllegalArgumentException();
                     }
                  case 'i':
                     if (var2.length >= 1) {
                        var2[0] = toInteger(var2[0]) + 1;
                     }

                     if (var2.length >= 2) {
                        var2[1] = toInteger(var2[1]) + 1;
                     }
                     continue;
                  case 'l':
                     if (var6) {
                        var7.push(var7.pop().toString().length());
                     }
                     continue;
                  case 'm':
                     if (var6) {
                        int var29 = toInteger(var7.pop());
                        int var42 = toInteger(var7.pop());
                        var7.push(var42 % var29);
                     }
                     continue;
                  case 'p':
                     var8 = var1.charAt(var3++);
                     if (var6) {
                        var7.push(var2[var8 - '1']);
                     }
                     continue;
                  case 't':
                     if (var5 != 1 && var5 != 3) {
                        throw new IllegalArgumentException();
                     }

                     var5 = 2;
                     var6 = toInteger(var7.pop()) != 0;
                     continue;
                  case '{':
                     int var24 = var3;

                     while (var1.charAt(var3++) != '}') {
                     }

                     if (var6) {
                        int var28 = Integer.valueOf(var1.substring(var24, var3 - 1));
                        var7.push(var28);
                     }
                     continue;
                  case '|':
                     if (var6) {
                        int var27 = toInteger(var7.pop());
                        int var11 = toInteger(var7.pop());
                        var7.push(var11 | var27);
                     }
                     continue;
                  case '~':
                     if (var6) {
                        int var26 = toInteger(var7.pop());
                        var7.push(~var26);
                     }
                     continue;
               }
            case '\\':
               var8 = var1.charAt(var3++);
               if (var8 >= '0' && var8 <= '7') {
                  int var9 = var8 - '0';

                  for (int var10 = 0; var10 < 2; var10++) {
                     var8 = var1.charAt(var3++);
                     if (var8 < '0' || var8 > '7') {
                        throw new IllegalStateException();
                     }

                     var9 = var9 * 8 + (var8 - '0');
                  }

                  var0.append((char)var9);
                  break;
               } else {
                  switch (var8) {
                     case ':':
                     case '\\':
                     case '^':
                        if (var6) {
                           var0.append(var8);
                        }
                        continue;
                     case 'E':
                     case 'e':
                        if (var6) {
                           var0.append('\u001b');
                        }
                        continue;
                     case 'b':
                        if (var6) {
                           var0.append('\b');
                        }
                        continue;
                     case 'f':
                        if (var6) {
                           var0.append('\f');
                        }
                        continue;
                     case 'n':
                        var0.append('\n');
                        continue;
                     case 'r':
                        if (var6) {
                           var0.append('\r');
                        }
                        continue;
                     case 's':
                        if (var6) {
                           var0.append(' ');
                        }
                        continue;
                     case 't':
                        if (var6) {
                           var0.append('\t');
                        }
                        continue;
                     default:
                        throw new IllegalArgumentException();
                  }
               }
            case '^':
               var8 = var1.charAt(var3++);
               if (var6) {
                  var0.append((char)(var8 - '@'));
               }
               break;
            default:
               if (var6) {
                  var0.append(var8);
               }
         }
      }
   }

   private static int toInteger(Object var0) {
      if (var0 instanceof Number) {
         return ((Number)var0).intValue();
      } else if (var0 instanceof Boolean) {
         return (Boolean)var0 ? 1 : 0;
      } else {
         return Integer.valueOf(var0.toString());
      }
   }
}
