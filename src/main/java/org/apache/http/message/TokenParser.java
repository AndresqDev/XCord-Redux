package org.apache.http.message;

import java.util.BitSet;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class TokenParser {
   public static final char CR = '\r';
   public static final char LF = '\n';
   public static final char SP = ' ';
   public static final char HT = '\t';
   public static final char DQUOTE = '"';
   public static final char ESCAPE = '\\';
   public static final TokenParser INSTANCE = new TokenParser();

   public static BitSet INIT_BITSET(int... var0) {
      BitSet var1 = new BitSet();

      for (int var5 : var0) {
         var1.set(var5);
      }

      return var1;
   }

   public static boolean isWhitespace(char var0) {
      return var0 == ' ' || var0 == '\t' || var0 == '\r' || var0 == '\n';
   }

   public String parseToken(CharArrayBuffer var1, ParserCursor var2, BitSet var3) {
      StringBuilder var4 = new StringBuilder();
      boolean var5 = false;

      while (!var2.atEnd()) {
         char var6 = var1.charAt(var2.getPos());
         if (var3 != null && var3.get(var6)) {
            break;
         }

         if (isWhitespace(var6)) {
            this.skipWhiteSpace(var1, var2);
            var5 = true;
         } else {
            if (var5 && var4.length() > 0) {
               var4.append(' ');
            }

            this.copyContent(var1, var2, var3, var4);
            var5 = false;
         }
      }

      return var4.toString();
   }

   public String parseValue(CharArrayBuffer var1, ParserCursor var2, BitSet var3) {
      StringBuilder var4 = new StringBuilder();
      boolean var5 = false;

      while (!var2.atEnd()) {
         char var6 = var1.charAt(var2.getPos());
         if (var3 != null && var3.get(var6)) {
            break;
         }

         if (isWhitespace(var6)) {
            this.skipWhiteSpace(var1, var2);
            var5 = true;
         } else if (var6 == '"') {
            if (var5 && var4.length() > 0) {
               var4.append(' ');
            }

            this.copyQuotedContent(var1, var2, var4);
            var5 = false;
         } else {
            if (var5 && var4.length() > 0) {
               var4.append(' ');
            }

            this.copyUnquotedContent(var1, var2, var3, var4);
            var5 = false;
         }
      }

      return var4.toString();
   }

   public void skipWhiteSpace(CharArrayBuffer var1, ParserCursor var2) {
      int var3 = var2.getPos();
      int var4 = var2.getPos();
      int var5 = var2.getUpperBound();

      for (int var6 = var4; var6 < var5; var6++) {
         char var7 = var1.charAt(var6);
         if (!isWhitespace(var7)) {
            break;
         }

         var3++;
      }

      var2.updatePos(var3);
   }

   public void copyContent(CharArrayBuffer var1, ParserCursor var2, BitSet var3, StringBuilder var4) {
      int var5 = var2.getPos();
      int var6 = var2.getPos();
      int var7 = var2.getUpperBound();

      for (int var8 = var6; var8 < var7; var8++) {
         char var9 = var1.charAt(var8);
         if (var3 != null && var3.get(var9) || isWhitespace(var9)) {
            break;
         }

         var5++;
         var4.append(var9);
      }

      var2.updatePos(var5);
   }

   public void copyUnquotedContent(CharArrayBuffer var1, ParserCursor var2, BitSet var3, StringBuilder var4) {
      int var5 = var2.getPos();
      int var6 = var2.getPos();
      int var7 = var2.getUpperBound();

      for (int var8 = var6; var8 < var7; var8++) {
         char var9 = var1.charAt(var8);
         if (var3 != null && var3.get(var9) || isWhitespace(var9) || var9 == '"') {
            break;
         }

         var5++;
         var4.append(var9);
      }

      var2.updatePos(var5);
   }

   public void copyQuotedContent(CharArrayBuffer var1, ParserCursor var2, StringBuilder var3) {
      if (!var2.atEnd()) {
         int var4 = var2.getPos();
         int var5 = var2.getPos();
         int var6 = var2.getUpperBound();
         char var7 = var1.charAt(var4);
         if (var7 == '"') {
            var4++;
            var5++;
            boolean var8 = false;

            for (int var9 = var5; var9 < var6; var4++) {
               var7 = var1.charAt(var9);
               if (var8) {
                  if (var7 != '"' && var7 != '\\') {
                     var3.append('\\');
                  }

                  var3.append(var7);
                  var8 = false;
               } else {
                  if (var7 == '"') {
                     var4++;
                     break;
                  }

                  if (var7 == '\\') {
                     var8 = true;
                  } else if (var7 != '\r' && var7 != '\n') {
                     var3.append(var7);
                  }
               }

               var9++;
            }

            var2.updatePos(var4);
         }
      }
   }
}
