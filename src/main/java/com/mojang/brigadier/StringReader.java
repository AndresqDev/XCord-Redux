package com.mojang.brigadier;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class StringReader implements ImmutableStringReader {
   private static final char SYNTAX_ESCAPE = '\\';
   private static final char SYNTAX_QUOTE = '"';
   private final String string;
   private int cursor;

   public StringReader(StringReader var1) {
      this.string = var1.string;
      this.cursor = var1.cursor;
   }

   public StringReader(String var1) {
      this.string = var1;
   }

   @Override
   public String getString() {
      return this.string;
   }

   public void setCursor(int var1) {
      this.cursor = var1;
   }

   @Override
   public int getRemainingLength() {
      return this.string.length() - this.cursor;
   }

   @Override
   public int getTotalLength() {
      return this.string.length();
   }

   @Override
   public int getCursor() {
      return this.cursor;
   }

   @Override
   public String getRead() {
      return this.string.substring(0, this.cursor);
   }

   @Override
   public String getRemaining() {
      return this.string.substring(this.cursor);
   }

   @Override
   public boolean canRead(int var1) {
      return this.cursor + var1 <= this.string.length();
   }

   @Override
   public boolean canRead() {
      return this.canRead(1);
   }

   @Override
   public char peek() {
      return this.string.charAt(this.cursor);
   }

   @Override
   public char peek(int var1) {
      return this.string.charAt(this.cursor + var1);
   }

   public char read() {
      return this.string.charAt(this.cursor++);
   }

   public void skip() {
      this.cursor++;
   }

   public static boolean isAllowedNumber(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 == '.' || var0 == '-';
   }

   public void skipWhitespace() {
      while (this.canRead() && Character.isWhitespace(this.peek())) {
         this.skip();
      }
   }

   public int readInt() throws CommandSyntaxException {
      int var1 = this.cursor;

      while (this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext(this);
      } else {
         try {
            return Integer.parseInt(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(this, var2);
         }
      }
   }

   public long readLong() throws CommandSyntaxException {
      int var1 = this.cursor;

      while (this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(this);
      } else {
         try {
            return Long.parseLong(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(this, var2);
         }
      }
   }

   public double readDouble() throws CommandSyntaxException {
      int var1 = this.cursor;

      while (this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(this);
      } else {
         try {
            return Double.parseDouble(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(this, var2);
         }
      }
   }

   public float readFloat() throws CommandSyntaxException {
      int var1 = this.cursor;

      while (this.canRead() && isAllowedNumber(this.peek())) {
         this.skip();
      }

      String var2 = this.string.substring(var1, this.cursor);
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext(this);
      } else {
         try {
            return Float.parseFloat(var2);
         } catch (NumberFormatException var4) {
            this.cursor = var1;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(this, var2);
         }
      }
   }

   public static boolean isAllowedInUnquotedString(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z' || var0 == '_' || var0 == '-' || var0 == '.' || var0 == '+';
   }

   public String readUnquotedString() {
      int var1 = this.cursor;

      while (this.canRead() && isAllowedInUnquotedString(this.peek())) {
         this.skip();
      }

      return this.string.substring(var1, this.cursor);
   }

   public String readQuotedString() throws CommandSyntaxException {
      if (!this.canRead()) {
         return "";
      } else if (this.peek() != '"') {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(this);
      } else {
         this.skip();
         StringBuilder var1 = new StringBuilder();
         boolean var2 = false;

         while (this.canRead()) {
            char var3 = this.read();
            if (var2) {
               if (var3 != '"' && var3 != '\\') {
                  this.setCursor(this.getCursor() - 1);
                  throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape().createWithContext(this, String.valueOf(var3));
               }

               var1.append(var3);
               var2 = false;
            } else if (var3 == '\\') {
               var2 = true;
            } else {
               if (var3 == '"') {
                  return var1.toString();
               }

               var1.append(var3);
            }
         }

         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(this);
      }
   }

   public String readString() throws CommandSyntaxException {
      return this.canRead() && this.peek() == '"' ? this.readQuotedString() : this.readUnquotedString();
   }

   public boolean readBoolean() throws CommandSyntaxException {
      int var1 = this.cursor;
      String var2 = this.readString();
      if (var2.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(this);
      } else if (var2.equals("true")) {
         return true;
      } else if (var2.equals("false")) {
         return false;
      } else {
         this.cursor = var1;
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().createWithContext(this, var2);
      }
   }

   public void expect(char var1) throws CommandSyntaxException {
      if (this.canRead() && this.peek() == var1) {
         this.skip();
      } else {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(this, String.valueOf(var1));
      }
   }
}
