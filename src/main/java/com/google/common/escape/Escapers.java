package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public final class Escapers {
   private static final Escaper NULL_ESCAPER = new CharEscaper() {
      @Override
      public String escape(String var1) {
         return Preconditions.checkNotNull(var1);
      }

      @Override
      protected char[] escape(char var1) {
         return null;
      }
   };

   private Escapers() {
   }

   public static Escaper nullEscaper() {
      return NULL_ESCAPER;
   }

   public static Escapers.Builder builder() {
      return new Escapers.Builder();
   }

   static UnicodeEscaper asUnicodeEscaper(Escaper var0) {
      Preconditions.checkNotNull(var0);
      if (var0 instanceof UnicodeEscaper) {
         return (UnicodeEscaper)var0;
      } else if (var0 instanceof CharEscaper) {
         return wrap((CharEscaper)var0);
      } else {
         String var10003 = String.valueOf(var0.getClass().getName());
         throw new IllegalArgumentException(
            var10003.length() != 0 ? "Cannot create a UnicodeEscaper from: ".concat(var10003) : new String("Cannot create a UnicodeEscaper from: ")
         );
      }
   }

   public static String computeReplacement(CharEscaper var0, char var1) {
      return stringOrNull(var0.escape(var1));
   }

   public static String computeReplacement(UnicodeEscaper var0, int var1) {
      return stringOrNull(var0.escape(var1));
   }

   private static String stringOrNull(char[] var0) {
      return var0 == null ? null : new String(var0);
   }

   private static UnicodeEscaper wrap(final CharEscaper var0) {
      return new UnicodeEscaper() {
         @Override
         protected char[] escape(int var1) {
            if (var1 < 65536) {
               return var0.escape((char)var1);
            } else {
               char[] var2 = new char[2];
               Character.toChars(var1, var2, 0);
               char[] var3 = var0.escape(var2[0]);
               char[] var4 = var0.escape(var2[1]);
               if (var3 == null && var4 == null) {
                  return null;
               } else {
                  int var5 = var3 != null ? var3.length : 1;
                  int var6 = var4 != null ? var4.length : 1;
                  char[] var7 = new char[var5 + var6];
                  if (var3 != null) {
                     for (int var8 = 0; var8 < var3.length; var8++) {
                        var7[var8] = var3[var8];
                     }
                  } else {
                     var7[0] = var2[0];
                  }

                  if (var4 != null) {
                     for (int var9 = 0; var9 < var4.length; var9++) {
                        var7[var5 + var9] = var4[var9];
                     }
                  } else {
                     var7[var5] = var2[1];
                  }

                  return var7;
               }
            }
         }
      };
   }

   @Beta
   public static final class Builder {
      private final Map<Character, String> replacementMap = new HashMap<>();
      private char safeMin = 0;
      private char safeMax = '\uffff';
      private String unsafeReplacement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public Escapers.Builder setSafeRange(char var1, char var2) {
         this.safeMin = var1;
         this.safeMax = var2;
         return this;
      }

      @CanIgnoreReturnValue
      public Escapers.Builder setUnsafeReplacement(@Nullable String var1) {
         this.unsafeReplacement = var1;
         return this;
      }

      @CanIgnoreReturnValue
      public Escapers.Builder addEscape(char var1, String var2) {
         Preconditions.checkNotNull(var2);
         this.replacementMap.put(var1, var2);
         return this;
      }

      public Escaper build() {
         return new ArrayBasedCharEscaper(this.replacementMap, this.safeMin, this.safeMax) {
            private final char[] replacementChars = Builder.this.unsafeReplacement != null ? Builder.this.unsafeReplacement.toCharArray() : null;

            @Override
            protected char[] escapeUnsafe(char var1) {
               return this.replacementChars;
            }
         };
      }
   }
}
