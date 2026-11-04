package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class Soundex implements StringEncoder {
   public static final char SILENT_MARKER = '-';
   public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
   private static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
   public static final Soundex US_ENGLISH = new Soundex();
   public static final Soundex US_ENGLISH_SIMPLIFIED = new Soundex("01230120022455012623010202", false);
   public static final Soundex US_ENGLISH_GENEALOGY = new Soundex("-123-12--22455-12623-1-2-2");
   @Deprecated
   private int maxLength = 4;
   private final char[] soundexMapping;
   private final boolean specialCaseHW;

   public Soundex() {
      this.soundexMapping = US_ENGLISH_MAPPING;
      this.specialCaseHW = true;
   }

   public Soundex(char[] var1) {
      this.soundexMapping = new char[var1.length];
      System.arraycopy(var1, 0, this.soundexMapping, 0, var1.length);
      this.specialCaseHW = !this.hasMarker(this.soundexMapping);
   }

   private boolean hasMarker(char[] var1) {
      for (char var5 : var1) {
         if (var5 == '-') {
            return true;
         }
      }

      return false;
   }

   public Soundex(String var1) {
      this.soundexMapping = var1.toCharArray();
      this.specialCaseHW = !this.hasMarker(this.soundexMapping);
   }

   public Soundex(String var1, boolean var2) {
      this.soundexMapping = var1.toCharArray();
      this.specialCaseHW = var2;
   }

   public int difference(String var1, String var2) throws EncoderException {
      return SoundexUtils.difference(this, var1, var2);
   }

   @Override
   public Object encode(Object var1) throws EncoderException {
      if (!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
      } else {
         return this.soundex((String)var1);
      }
   }

   @Override
   public String encode(String var1) {
      return this.soundex(var1);
   }

   @Deprecated
   public int getMaxLength() {
      return this.maxLength;
   }

   private char map(char var1) {
      int var2 = var1 - 'A';
      if (var2 >= 0 && var2 < this.soundexMapping.length) {
         return this.soundexMapping[var2];
      } else {
         throw new IllegalArgumentException("The character is not mapped: " + var1 + " (index=" + var2 + ")");
      }
   }

   @Deprecated
   public void setMaxLength(int var1) {
      this.maxLength = var1;
   }

   public String soundex(String var1) {
      if (var1 == null) {
         return null;
      } else {
         var1 = SoundexUtils.clean(var1);
         if (var1.length() == 0) {
            return var1;
         } else {
            char[] var2 = new char[]{'0', '0', '0', '0'};
            int var3 = 0;
            char var4 = var1.charAt(0);
            var2[var3++] = var4;
            char var5 = this.map(var4);

            for (int var6 = 1; var6 < var1.length() && var3 < var2.length; var6++) {
               char var7 = var1.charAt(var6);
               if (!this.specialCaseHW || var7 != 'H' && var7 != 'W') {
                  char var8 = this.map(var7);
                  if (var8 != '-') {
                     if (var8 != '0' && var8 != var5) {
                        var2[var3++] = var8;
                     }

                     var5 = var8;
                  }
               }
            }

            return new String(var2);
         }
      }
   }
}
