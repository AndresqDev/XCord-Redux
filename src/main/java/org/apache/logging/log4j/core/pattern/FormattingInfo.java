package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public final class FormattingInfo {
   private static final char[] SPACES = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
   private static final char[] ZEROS = new char[]{'0', '0', '0', '0', '0', '0', '0', '0'};
   private static final FormattingInfo DEFAULT = new FormattingInfo(false, 0, Integer.MAX_VALUE, true);
   private final int minLength;
   private final int maxLength;
   private final boolean leftAlign;
   private final boolean leftTruncate;
   private final boolean zeroPad;

   public FormattingInfo(boolean var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, false);
   }

   public FormattingInfo(boolean var1, int var2, int var3, boolean var4, boolean var5) {
      this.leftAlign = var1;
      this.minLength = var2;
      this.maxLength = var3;
      this.leftTruncate = var4;
      this.zeroPad = var5;
   }

   public static FormattingInfo getDefault() {
      return DEFAULT;
   }

   public boolean isLeftAligned() {
      return this.leftAlign;
   }

   public boolean isLeftTruncate() {
      return this.leftTruncate;
   }

   public boolean isZeroPad() {
      return this.zeroPad;
   }

   public int getMinLength() {
      return this.minLength;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void format(int var1, StringBuilder var2) {
      int var3 = var2.length() - var1;
      if (var3 > this.maxLength) {
         if (this.leftTruncate) {
            var2.delete(var1, var2.length() - this.maxLength);
         } else {
            var2.delete(var1 + this.maxLength, var1 + var2.length());
         }
      } else if (var3 < this.minLength) {
         if (this.leftAlign) {
            int var4 = var2.length();
            var2.setLength(var1 + this.minLength);

            for (int var5 = var4; var5 < var2.length(); var5++) {
               var2.setCharAt(var5, ' ');
            }
         } else {
            int var6 = this.minLength - var3;

            char[] var7;
            for (var7 = this.zeroPad ? ZEROS : SPACES; var6 > var7.length; var6 -= var7.length) {
               var2.insert(var1, var7);
            }

            var2.insert(var1, var7, 0, var6);
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString());
      var1.append("[leftAlign=");
      var1.append(this.leftAlign);
      var1.append(", maxLength=");
      var1.append(this.maxLength);
      var1.append(", minLength=");
      var1.append(this.minLength);
      var1.append(", leftTruncate=");
      var1.append(this.leftTruncate);
      var1.append(", zeroPad=");
      var1.append(this.zeroPad);
      var1.append(']');
      return var1.toString();
   }
}
