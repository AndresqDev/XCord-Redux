package org.yaml.snakeyaml.error;

import java.io.Serializable;
import org.yaml.snakeyaml.scanner.Constant;

public final class Mark implements Serializable {
   private String name;
   private int index;
   private int line;
   private int column;
   private int[] buffer;
   private int pointer;

   private static int[] toCodePoints(char[] var0) {
      int[] var1 = new int[Character.codePointCount(var0, 0, var0.length)];
      int var2 = 0;

      for (int var3 = 0; var2 < var0.length; var3++) {
         int var4 = Character.codePointAt(var0, var2);
         var1[var3] = var4;
         var2 += Character.charCount(var4);
      }

      return var1;
   }

   public Mark(String var1, int var2, int var3, int var4, char[] var5, int var6) {
      this(var1, var2, var3, var4, toCodePoints(var5), var6);
   }

   @Deprecated
   public Mark(String var1, int var2, int var3, int var4, String var5, int var6) {
      this(var1, var2, var3, var4, var5.toCharArray(), var6);
   }

   public Mark(String var1, int var2, int var3, int var4, int[] var5, int var6) {
      this.name = var1;
      this.index = var2;
      this.line = var3;
      this.column = var4;
      this.buffer = var5;
      this.pointer = var6;
   }

   private boolean isLineBreak(int var1) {
      return Constant.NULL_OR_LINEBR.has(var1);
   }

   public String get_snippet(int var1, int var2) {
      float var3 = (float)var2 / 2.0F - 1.0F;
      int var4 = this.pointer;
      String var5 = "";

      while (var4 > 0 && !this.isLineBreak(this.buffer[var4 - 1])) {
         if ((float)(this.pointer - --var4) > var3) {
            var5 = " ... ";
            var4 += 5;
            break;
         }
      }

      String var6 = "";
      int var7 = this.pointer;

      while (var7 < this.buffer.length && !this.isLineBreak(this.buffer[var7])) {
         if ((float)(++var7 - this.pointer) > var3) {
            var6 = " ... ";
            var7 -= 5;
            break;
         }
      }

      StringBuilder var8 = new StringBuilder();

      for (int var9 = 0; var9 < var1; var9++) {
         var8.append(" ");
      }

      var8.append(var5);

      for (int var10 = var4; var10 < var7; var10++) {
         var8.appendCodePoint(this.buffer[var10]);
      }

      var8.append(var6);
      var8.append("\n");

      for (int var11 = 0; var11 < var1 + this.pointer - var4 + var5.length(); var11++) {
         var8.append(" ");
      }

      var8.append("^");
      return var8.toString();
   }

   public String get_snippet() {
      return this.get_snippet(4, 75);
   }

   @Override
   public String toString() {
      String var1 = this.get_snippet();
      StringBuilder var2 = new StringBuilder(" in ");
      var2.append(this.name);
      var2.append(", line ");
      var2.append(this.line + 1);
      var2.append(", column ");
      var2.append(this.column + 1);
      var2.append(":\n");
      var2.append(var1);
      return var2.toString();
   }

   public String getName() {
      return this.name;
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.column;
   }

   public int getIndex() {
      return this.index;
   }

   public int[] getBuffer() {
      return this.buffer;
   }

   public int getPointer() {
      return this.pointer;
   }
}
