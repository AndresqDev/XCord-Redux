package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public final class CharEscaperBuilder {
   private final Map<Character, String> map;
   private int max = -1;

   public CharEscaperBuilder() {
      this.map = new HashMap<>();
   }

   @CanIgnoreReturnValue
   public CharEscaperBuilder addEscape(char var1, String var2) {
      this.map.put(var1, Preconditions.checkNotNull(var2));
      if (var1 > this.max) {
         this.max = var1;
      }

      return this;
   }

   @CanIgnoreReturnValue
   public CharEscaperBuilder addEscapes(char[] var1, String var2) {
      Preconditions.checkNotNull(var2);

      for (char var6 : var1) {
         this.addEscape(var6, var2);
      }

      return this;
   }

   public char[][] toArray() {
      char[][] var1 = new char[this.max + 1][];

      for (Entry var3 : this.map.entrySet()) {
         var1[var3.getKey()] = ((String)var3.getValue()).toCharArray();
      }

      return var1;
   }

   public Escaper toEscaper() {
      return new CharEscaperBuilder.CharArrayDecorator(this.toArray());
   }

   private static class CharArrayDecorator extends CharEscaper {
      private final char[][] replacements;
      private final int replaceLength;

      CharArrayDecorator(char[][] var1) {
         this.replacements = var1;
         this.replaceLength = var1.length;
      }

      @Override
      public String escape(String var1) {
         int var2 = var1.length();

         for (int var3 = 0; var3 < var2; var3++) {
            char var4 = var1.charAt(var3);
            if (var4 < this.replacements.length && this.replacements[var4] != null) {
               return this.escapeSlow(var1, var3);
            }
         }

         return var1;
      }

      @Override
      protected char[] escape(char var1) {
         return var1 < this.replaceLength ? this.replacements[var1] : null;
      }
   }
}
