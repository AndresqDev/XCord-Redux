package com.google.protobuf;

import java.util.Arrays;

public final class TextFormatParseLocation {
   public static final TextFormatParseLocation EMPTY = new TextFormatParseLocation(-1, -1);
   private final int line;
   private final int column;

   static TextFormatParseLocation create(int var0, int var1) {
      if (var0 == -1 && var1 == -1) {
         return EMPTY;
      } else if (var0 >= 0 && var1 >= 0) {
         return new TextFormatParseLocation(var0, var1);
      } else {
         throw new IllegalArgumentException(String.format("line and column values must be >= 0: line %d, column: %d", var0, var1));
      }
   }

   private TextFormatParseLocation(int var1, int var2) {
      this.line = var1;
      this.column = var2;
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.column;
   }

   @Override
   public String toString() {
      return String.format("ParseLocation{line=%d, column=%d}", this.line, this.column);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TextFormatParseLocation)) {
         return false;
      } else {
         TextFormatParseLocation var2 = (TextFormatParseLocation)var1;
         return this.line == var2.getLine() && this.column == var2.getColumn();
      }
   }

   @Override
   public int hashCode() {
      int[] var1 = new int[]{this.line, this.column};
      return Arrays.hashCode(var1);
   }
}
