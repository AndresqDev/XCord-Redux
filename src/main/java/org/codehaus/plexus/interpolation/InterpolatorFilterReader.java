package org.codehaus.plexus.interpolation;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class InterpolatorFilterReader extends FilterReader {
   private Interpolator interpolator;
   private RecursionInterceptor recursionInterceptor;
   private String replaceData = null;
   private int replaceIndex = -1;
   private int previousIndex = -1;
   public static final String DEFAULT_BEGIN_TOKEN = "${";
   public static final String DEFAULT_END_TOKEN = "}";
   private String beginToken;
   private String orginalBeginToken;
   private String endToken;
   private boolean interpolateWithPrefixPattern = true;
   private String escapeString;
   private boolean useEscape = false;
   private boolean preserveEscapeString = false;

   public InterpolatorFilterReader(Reader var1, Interpolator var2) {
      this(var1, var2, "${", "}");
   }

   public InterpolatorFilterReader(Reader var1, Interpolator var2, String var3, String var4) {
      this(var1, var2, var3, var4, new SimpleRecursionInterceptor());
   }

   public InterpolatorFilterReader(Reader var1, Interpolator var2, RecursionInterceptor var3) {
      this(var1, var2, "${", "}", var3);
   }

   public InterpolatorFilterReader(Reader var1, Interpolator var2, String var3, String var4, RecursionInterceptor var5) {
      super(var1);
      this.interpolator = var2;
      this.beginToken = var3;
      this.endToken = var4;
      this.recursionInterceptor = var5;
      this.orginalBeginToken = this.beginToken;
   }

   @Override
   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         for (long var3 = 0L; var3 < var1; var3++) {
            if (this.read() == -1) {
               return var3;
            }
         }

         return var1;
      }
   }

   @Override
   public int read(char[] var1, int var2, int var3) throws IOException {
      for (int var4 = 0; var4 < var3; var4++) {
         int var5 = this.read();
         if (var5 == -1) {
            if (var4 == 0) {
               return -1;
            }

            return var4;
         }

         var1[var2 + var4] = (char)var5;
      }

      return var3;
   }

   @Override
   public int read() throws IOException {
      if (this.replaceIndex != -1 && this.replaceIndex < this.replaceData.length()) {
         char var10 = this.replaceData.charAt(this.replaceIndex++);
         if (this.replaceIndex >= this.replaceData.length()) {
            this.replaceIndex = -1;
         }

         return var10;
      } else {
         int var1 = -1;
         if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
            var1 = this.endToken.charAt(this.previousIndex++);
         } else {
            var1 = this.in.read();
         }

         if (var1 == this.beginToken.charAt(0) || this.useEscape && var1 == this.orginalBeginToken.charAt(0)) {
            StringBuilder var2 = new StringBuilder();
            var2.append((char)var1);
            int var3 = 1;

            do {
               if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
                  var1 = this.endToken.charAt(this.previousIndex++);
               } else {
                  var1 = this.in.read();
               }

               if (var1 == -1) {
                  break;
               }

               var2.append((char)var1);
               if (var3 < this.beginToken.length()
                  && var1 != this.beginToken.charAt(var3++)
                  && this.useEscape
                  && this.orginalBeginToken.length() > var3 - 1
                  && var1 != this.orginalBeginToken.charAt(var3 - 1)) {
                  var1 = -1;
                  break;
               }

               if (this.useEscape && this.orginalBeginToken == this.endToken && var2.toString().startsWith(this.beginToken)) {
                  var1 = this.in.read();
                  var2.append((char)var1);
               }
            } while (var1 != this.endToken.charAt(0));

            if (var1 != -1 && this.endToken.length() > 1) {
               int var4 = 1;

               do {
                  if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
                     var1 = this.endToken.charAt(this.previousIndex++);
                  } else {
                     var1 = this.in.read();
                  }

                  if (var1 == -1) {
                     break;
                  }

                  var2.append((char)var1);
                  if (var1 != this.endToken.charAt(var4++)) {
                     var1 = -1;
                     break;
                  }
               } while (var4 < this.endToken.length());
            }

            if (var1 == -1) {
               this.replaceData = var2.toString();
               this.replaceIndex = 1;
               return this.replaceData.charAt(0);
            } else {
               String var11 = null;

               try {
                  boolean var5 = false;
                  if (this.useEscape && var2.toString().startsWith(this.escapeString + this.orginalBeginToken)) {
                     String var12 = var2.toString();
                     if (!this.preserveEscapeString) {
                        var11 = var12.substring(this.escapeString.length(), var12.length());
                     } else {
                        var11 = var12;
                     }

                     var5 = true;
                  }

                  if (!var5) {
                     if (this.interpolateWithPrefixPattern) {
                        var11 = this.interpolator.interpolate(var2.toString(), "", this.recursionInterceptor);
                     } else {
                        var11 = this.interpolator.interpolate(var2.toString(), this.recursionInterceptor);
                     }
                  }
               } catch (InterpolationException var7) {
                  IllegalArgumentException var6 = new IllegalArgumentException(var7.getMessage());
                  var6.initCause(var7);
                  throw var6;
               }

               if (var11 != null) {
                  if (var11.length() != 0) {
                     this.replaceData = var11;
                     this.replaceIndex = 0;
                  }

                  return this.read();
               } else {
                  this.previousIndex = 0;
                  this.replaceData = var2.substring(0, var2.length() - this.endToken.length());
                  this.replaceIndex = 0;
                  return this.beginToken.charAt(0);
               }
            }
         } else {
            return var1;
         }
      }
   }

   public boolean isInterpolateWithPrefixPattern() {
      return this.interpolateWithPrefixPattern;
   }

   public void setInterpolateWithPrefixPattern(boolean var1) {
      this.interpolateWithPrefixPattern = var1;
   }

   public String getEscapeString() {
      return this.escapeString;
   }

   public void setEscapeString(String var1) {
      if (var1 != null && var1.length() >= 1) {
         this.escapeString = var1;
         this.orginalBeginToken = this.beginToken;
         this.beginToken = var1 + this.beginToken;
         this.useEscape = var1 != null && var1.length() >= 1;
      }
   }

   public boolean isPreserveEscapeString() {
      return this.preserveEscapeString;
   }

   public void setPreserveEscapeString(boolean var1) {
      this.preserveEscapeString = var1;
   }

   public RecursionInterceptor getRecursionInterceptor() {
      return this.recursionInterceptor;
   }

   public InterpolatorFilterReader setRecursionInterceptor(RecursionInterceptor var1) {
      this.recursionInterceptor = var1;
      return this;
   }
}
