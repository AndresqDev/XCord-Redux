package org.jline.reader.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;

public class DefaultParser implements Parser {
   private char[] quoteChars = new char[]{'\'', '"'};
   private char[] escapeChars = new char[]{'\\'};
   private boolean eofOnUnclosedQuote;
   private boolean eofOnEscapedNewLine;
   private char[] openingBrackets = null;
   private char[] closingBrackets = null;

   public DefaultParser quoteChars(char[] var1) {
      this.quoteChars = var1;
      return this;
   }

   public DefaultParser escapeChars(char[] var1) {
      this.escapeChars = var1;
      return this;
   }

   public DefaultParser eofOnUnclosedQuote(boolean var1) {
      this.eofOnUnclosedQuote = var1;
      return this;
   }

   public DefaultParser eofOnUnclosedBracket(DefaultParser.Bracket... var1) {
      this.setEofOnUnclosedBracket(var1);
      return this;
   }

   public DefaultParser eofOnEscapedNewLine(boolean var1) {
      this.eofOnEscapedNewLine = var1;
      return this;
   }

   public void setQuoteChars(char[] var1) {
      this.quoteChars = var1;
   }

   public char[] getQuoteChars() {
      return this.quoteChars;
   }

   public void setEscapeChars(char[] var1) {
      this.escapeChars = var1;
   }

   public char[] getEscapeChars() {
      return this.escapeChars;
   }

   public void setEofOnUnclosedQuote(boolean var1) {
      this.eofOnUnclosedQuote = var1;
   }

   public boolean isEofOnUnclosedQuote() {
      return this.eofOnUnclosedQuote;
   }

   public void setEofOnEscapedNewLine(boolean var1) {
      this.eofOnEscapedNewLine = var1;
   }

   public boolean isEofOnEscapedNewLine() {
      return this.eofOnEscapedNewLine;
   }

   public void setEofOnUnclosedBracket(DefaultParser.Bracket... var1) {
      if (var1 == null) {
         this.openingBrackets = null;
         this.closingBrackets = null;
      } else {
         HashSet var2 = new HashSet<>(Arrays.asList(var1));
         this.openingBrackets = new char[var2.size()];
         this.closingBrackets = new char[var2.size()];
         int var3 = 0;

         for (DefaultParser.Bracket var5 : var2) {
            switch (var5) {
               case ROUND:
                  this.openingBrackets[var3] = '(';
                  this.closingBrackets[var3] = ')';
                  break;
               case CURLY:
                  this.openingBrackets[var3] = '{';
                  this.closingBrackets[var3] = '}';
                  break;
               case SQUARE:
                  this.openingBrackets[var3] = '[';
                  this.closingBrackets[var3] = ']';
                  break;
               case ANGLE:
                  this.openingBrackets[var3] = '<';
                  this.closingBrackets[var3] = '>';
            }

            var3++;
         }
      }
   }

   @Override
   public ParsedLine parse(String var1, int var2, Parser.ParseContext var3) {
      LinkedList var4 = new LinkedList();
      StringBuilder var5 = new StringBuilder();
      int var6 = -1;
      int var7 = -1;
      int var8 = -1;
      int var9 = -1;
      int var10 = -1;
      int var11 = 0;
      DefaultParser.BracketChecker var12 = new DefaultParser.BracketChecker();
      boolean var13 = false;

      for (int var14 = 0; var1 != null && var14 < var1.length(); var14++) {
         if (var14 == var2) {
            var7 = var4.size();
            var6 = var5.length();
            var9 = var14 - var11;
         }

         if (var8 < 0 && this.isQuoteChar(var1, var14)) {
            var8 = var14;
            if (var5.length() == 0) {
               var13 = true;
            } else {
               var5.append(var1.charAt(var14));
            }
         } else if (var8 >= 0 && var1.charAt(var8) == var1.charAt(var14) && !this.isEscaped(var1, var14)) {
            if (!var13) {
               var5.append(var1.charAt(var14));
            } else if (var9 >= 0 && var10 < 0) {
               var10 = var14 - var11 + 1;
            }

            var8 = -1;
            var13 = false;
         } else if (var8 < 0 && this.isDelimiter(var1, var14)) {
            if (var5.length() > 0) {
               var4.add(var5.toString());
               var5.setLength(0);
               if (var9 >= 0 && var10 < 0) {
                  var10 = var14 - var11;
               }
            }

            var11 = var14 + 1;
         } else if (!this.isEscapeChar(var1, var14)) {
            var5.append(var1.charAt(var14));
            if (var8 < 0) {
               var12.check(var1, var14);
            }
         }
      }

      if (var5.length() > 0 || var2 == var1.length()) {
         var4.add(var5.toString());
         if (var9 >= 0 && var10 < 0) {
            var10 = var1.length() - var11;
         }
      }

      if (var2 == var1.length()) {
         var7 = var4.size() - 1;
         var6 = ((String)var4.get(var4.size() - 1)).length();
         var9 = var2 - var11;
         var10 = var9;
      }

      if (var3 != Parser.ParseContext.COMPLETE) {
         if (this.eofOnEscapedNewLine && this.isEscapeChar(var1, var1.length() - 1)) {
            throw new EOFError(-1, -1, "Escaped new line", "newline");
         }

         if (this.eofOnUnclosedQuote && var8 >= 0) {
            throw new EOFError(-1, -1, "Missing closing quote", var1.charAt(var8) == '\'' ? "quote" : "dquote");
         }

         if (var12.isOpeningBracketMissing()) {
            throw new EOFError(-1, -1, "Missing opening bracket", "missing: " + var12.getMissingOpeningBracket());
         }

         if (var12.isClosingBracketMissing()) {
            throw new EOFError(-1, -1, "Missing closing brackets", "add: " + var12.getMissingClosingBrackets());
         }
      }

      String var15 = var13 ? var1.substring(var8, var8 + 1) : null;
      return new DefaultParser.ArgumentList(var1, var4, var7, var6, var2, var15, var9, var10);
   }

   public boolean isDelimiter(CharSequence var1, int var2) {
      return !this.isQuoted(var1, var2) && !this.isEscaped(var1, var2) && this.isDelimiterChar(var1, var2);
   }

   public boolean isQuoted(CharSequence var1, int var2) {
      return false;
   }

   public boolean isQuoteChar(CharSequence var1, int var2) {
      if (var2 < 0) {
         return false;
      } else {
         if (this.quoteChars != null) {
            for (char var6 : this.quoteChars) {
               if (var6 == var1.charAt(var2)) {
                  return !this.isEscaped(var1, var2);
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean isEscapeChar(char var1) {
      if (this.escapeChars != null) {
         for (char var5 : this.escapeChars) {
            if (var5 == var1) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isEscapeChar(CharSequence var1, int var2) {
      if (var2 < 0) {
         return false;
      } else {
         char var3 = var1.charAt(var2);
         return this.isEscapeChar(var3) && !this.isEscaped(var1, var2);
      }
   }

   public boolean isEscaped(CharSequence var1, int var2) {
      return var2 <= 0 ? false : this.isEscapeChar(var1, var2 - 1);
   }

   public boolean isDelimiterChar(CharSequence var1, int var2) {
      return Character.isWhitespace(var1.charAt(var2));
   }

   private boolean isRawEscapeChar(char var1) {
      if (this.escapeChars != null) {
         for (char var5 : this.escapeChars) {
            if (var5 == var1) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isRawQuoteChar(char var1) {
      if (this.quoteChars != null) {
         for (char var5 : this.quoteChars) {
            if (var5 == var1) {
               return true;
            }
         }
      }

      return false;
   }

   public class ArgumentList implements CompletingParsedLine, ParsedLine {
      private final String line;
      private final List<String> words;
      private final int wordIndex;
      private final int wordCursor;
      private final int cursor;
      private final String openingQuote;
      private final int rawWordCursor;
      private final int rawWordLength;

      @Deprecated
      public ArgumentList(String var2, List<String> var3, int var4, int var5, int var6) {
         this(var2, var3, var4, var5, var6, null, var5, ((String)var3.get(var4)).length());
      }

      public ArgumentList(String var2, List<String> var3, int var4, int var5, int var6, String var7, int var8, int var9) {
         this.line = var2;
         this.words = Collections.unmodifiableList(Objects.requireNonNull(var3));
         this.wordIndex = var4;
         this.wordCursor = var5;
         this.cursor = var6;
         this.openingQuote = var7;
         this.rawWordCursor = var8;
         this.rawWordLength = var9;
      }

      @Override
      public int wordIndex() {
         return this.wordIndex;
      }

      @Override
      public String word() {
         return this.wordIndex >= 0 && this.wordIndex < this.words.size() ? this.words.get(this.wordIndex) : "";
      }

      @Override
      public int wordCursor() {
         return this.wordCursor;
      }

      @Override
      public List<String> words() {
         return this.words;
      }

      @Override
      public int cursor() {
         return this.cursor;
      }

      @Override
      public String line() {
         return this.line;
      }

      @Override
      public CharSequence escape(CharSequence var1, boolean var2) {
         StringBuilder var3 = new StringBuilder(var1);
         String var5 = this.openingQuote;
         boolean var6 = false;
         if (this.openingQuote == null) {
            for (int var7 = 0; var7 < var3.length(); var7++) {
               if (DefaultParser.this.isQuoteChar(var3, var7)) {
                  var6 = true;
                  break;
               }
            }
         }

         if (DefaultParser.this.escapeChars != null) {
            Predicate var4;
            if (this.openingQuote != null) {
               var4 = var2x -> DefaultParser.this.isRawEscapeChar(var3.charAt(var2x)) || String.valueOf(var3.charAt(var2x)).equals(this.openingQuote);
            } else if (var6) {
               var4 = var2x -> DefaultParser.this.isRawEscapeChar(var3.charAt(var2x));
            } else {
               var4 = var2x -> DefaultParser.this.isDelimiterChar(var3, var2x)
                     || DefaultParser.this.isRawEscapeChar(var3.charAt(var2x))
                     || DefaultParser.this.isRawQuoteChar(var3.charAt(var2x));
            }

            for (int var8 = 0; var8 < var3.length(); var8++) {
               if (var4.test(var8)) {
                  var3.insert(var8++, DefaultParser.this.escapeChars[0]);
               }
            }
         } else if (this.openingQuote == null && !var6) {
            for (int var9 = 0; var9 < var3.length(); var9++) {
               if (DefaultParser.this.isDelimiterChar(var3, var9)) {
                  var5 = "'";
                  break;
               }
            }
         }

         if (var5 != null) {
            var3.insert(0, var5);
            if (var2) {
               var3.append(var5);
            }
         }

         return var3;
      }

      @Override
      public int rawWordCursor() {
         return this.rawWordCursor;
      }

      @Override
      public int rawWordLength() {
         return this.rawWordLength;
      }
   }

   public static enum Bracket {
      ROUND,
      CURLY,
      SQUARE,
      ANGLE;
   }

   private class BracketChecker {
      private int missingOpeningBracket = -1;
      private List<Integer> nested = new ArrayList<>();

      public BracketChecker() {
      }

      public void check(CharSequence var1, int var2) {
         if (DefaultParser.this.openingBrackets != null && var2 >= 0) {
            int var3 = this.bracketId(DefaultParser.this.openingBrackets, var1, var2);
            if (var3 >= 0) {
               this.nested.add(var3);
            } else {
               var3 = this.bracketId(DefaultParser.this.closingBrackets, var1, var2);
               if (var3 >= 0) {
                  if (!this.nested.isEmpty() && var3 == this.nested.get(this.nested.size() - 1)) {
                     this.nested.remove(this.nested.size() - 1);
                  } else {
                     this.missingOpeningBracket = var3;
                  }
               }
            }
         }
      }

      public boolean isOpeningBracketMissing() {
         return this.missingOpeningBracket != -1;
      }

      public String getMissingOpeningBracket() {
         return !this.isOpeningBracketMissing() ? null : Character.toString(DefaultParser.this.openingBrackets[this.missingOpeningBracket]);
      }

      public boolean isClosingBracketMissing() {
         return !this.nested.isEmpty();
      }

      public String getMissingClosingBrackets() {
         if (!this.isClosingBracketMissing()) {
            return null;
         } else {
            StringBuilder var1 = new StringBuilder();

            for (int var2 = this.nested.size() - 1; var2 > -1; var2--) {
               var1.append(DefaultParser.this.closingBrackets[this.nested.get(var2)]);
            }

            return var1.toString();
         }
      }

      private int bracketId(char[] var1, CharSequence var2, int var3) {
         for (int var4 = 0; var4 < var1.length; var4++) {
            if (var2.charAt(var3) == var1[var4]) {
               return var4;
            }
         }

         return -1;
      }
   }
}
