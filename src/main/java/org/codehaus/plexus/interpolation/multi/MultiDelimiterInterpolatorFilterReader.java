package org.codehaus.plexus.interpolation.multi;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashSet;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

public class MultiDelimiterInterpolatorFilterReader extends FilterReader {
   private Interpolator interpolator;
   private RecursionInterceptor recursionInterceptor;
   private String replaceData = null;
   private int replaceIndex = -1;
   private int previousIndex = -1;
   public static final String DEFAULT_BEGIN_TOKEN = "${";
   public static final String DEFAULT_END_TOKEN = "}";
   private boolean interpolateWithPrefixPattern = true;
   private String escapeString;
   private boolean useEscape = false;
   private boolean preserveEscapeString = false;
   private LinkedHashSet<DelimiterSpecification> delimiters = new LinkedHashSet<>();
   private DelimiterSpecification currentSpec;
   private String beginToken;
   private String originalBeginToken;
   private String endToken;

   public MultiDelimiterInterpolatorFilterReader(Reader var1, Interpolator var2) {
      this(var1, var2, new SimpleRecursionInterceptor());
   }

   public MultiDelimiterInterpolatorFilterReader(Reader var1, Interpolator var2, RecursionInterceptor var3) {
      super(var1);
      this.interpolator = var2;
      this.interpolator.setCacheAnswers(true);
      this.recursionInterceptor = var3;
      this.delimiters.add(DelimiterSpecification.DEFAULT_SPEC);
   }

   public MultiDelimiterInterpolatorFilterReader addDelimiterSpec(String var1) {
      if (var1 == null) {
         return this;
      } else {
         this.delimiters.add(DelimiterSpecification.parse(var1));
         return this;
      }
   }

   public boolean removeDelimiterSpec(String var1) {
      return var1 == null ? false : this.delimiters.remove(DelimiterSpecification.parse(var1));
   }

   public MultiDelimiterInterpolatorFilterReader setDelimiterSpecs(LinkedHashSet<String> var1) {
      this.delimiters.clear();

      for (String var3 : var1) {
         if (var3 != null) {
            this.delimiters.add(DelimiterSpecification.parse(var3));
         }
      }

      return this;
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
         char var11 = this.replaceData.charAt(this.replaceIndex++);
         if (this.replaceIndex >= this.replaceData.length()) {
            this.replaceIndex = -1;
         }

         return var11;
      } else {
         int var1 = -1;
         if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
            var1 = this.endToken.charAt(this.previousIndex++);
         } else {
            var1 = this.in.read();
         }

         boolean var2 = false;
         if (!(var2 = this.useEscape && var1 == this.escapeString.charAt(0)) && !this.reselectDelimiterSpec(var1)) {
            return var1;
         } else {
            StringBuilder var3 = new StringBuilder();
            var3.append((char)var1);
            boolean var4 = false;
            if (var2) {
               for (int var5 = 0; var5 < this.escapeString.length() - 1; var5++) {
                  var1 = this.in.read();
                  if (var1 == -1) {
                     var4 = true;
                     break;
                  }

                  var3.append((char)var1);
               }

               if (!var4) {
                  var1 = this.in.read();
                  if (!this.reselectDelimiterSpec(var1)) {
                     this.replaceData = var3.toString();
                     this.replaceIndex = 1;
                     return this.replaceData.charAt(0);
                  }

                  var3.append((char)var1);
               }
            }

            int var13 = 1;

            while (!var4) {
               if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
                  var1 = this.endToken.charAt(this.previousIndex++);
               } else {
                  var1 = this.in.read();
               }

               if (var1 == -1) {
                  break;
               }

               var3.append((char)var1);
               if (var13 < this.originalBeginToken.length() && var1 != this.originalBeginToken.charAt(var13)) {
                  var1 = -1;
                  break;
               }

               var13++;
               if (var1 == this.endToken.charAt(0)) {
                  break;
               }
            }

            if (var1 != -1 && this.endToken.length() > 1) {
               int var6 = 1;

               do {
                  if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
                     var1 = this.endToken.charAt(this.previousIndex++);
                  } else {
                     var1 = this.in.read();
                  }

                  if (var1 == -1) {
                     break;
                  }

                  var3.append((char)var1);
                  if (var1 != this.endToken.charAt(var6++)) {
                     var1 = -1;
                     break;
                  }
               } while (var6 < this.endToken.length());
            }

            if (var1 == -1) {
               this.replaceData = var3.toString();
               this.replaceIndex = 1;
               return this.replaceData.charAt(0);
            } else {
               String var14 = null;

               try {
                  boolean var7 = false;
                  if (this.useEscape && var3.toString().startsWith(this.beginToken)) {
                     String var15 = var3.toString();
                     if (!this.preserveEscapeString) {
                        var14 = var15.substring(this.escapeString.length(), var15.length());
                     } else {
                        var14 = var15;
                     }

                     var7 = true;
                  }

                  if (!var7) {
                     if (this.interpolateWithPrefixPattern) {
                        var14 = this.interpolator.interpolate(var3.toString(), "", this.recursionInterceptor);
                     } else {
                        var14 = this.interpolator.interpolate(var3.toString(), this.recursionInterceptor);
                     }
                  }
               } catch (InterpolationException var9) {
                  IllegalArgumentException var8 = new IllegalArgumentException(var9.getMessage());
                  var8.initCause(var9);
                  throw var8;
               }

               if (var14 != null) {
                  if (var14.length() != 0) {
                     this.replaceData = var14;
                     this.replaceIndex = 0;
                  }

                  return this.read();
               } else {
                  this.previousIndex = 0;
                  this.replaceData = var3.substring(0, var3.length() - this.endToken.length());
                  this.replaceIndex = 0;
                  return this.beginToken.charAt(0);
               }
            }
         }
      }
   }

   private boolean reselectDelimiterSpec(int var1) {
      for (DelimiterSpecification var3 : this.delimiters) {
         if (var1 == var3.getBegin().charAt(0)) {
            this.currentSpec = var3;
            this.originalBeginToken = this.currentSpec.getBegin();
            this.beginToken = this.useEscape ? this.escapeString + this.originalBeginToken : this.originalBeginToken;
            this.endToken = this.currentSpec.getEnd();
            return true;
         }
      }

      return false;
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

   public MultiDelimiterInterpolatorFilterReader setRecursionInterceptor(RecursionInterceptor var1) {
      this.recursionInterceptor = var1;
      return this;
   }
}
