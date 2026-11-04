package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class InterpolationFilterReader extends FilterReader {
   private String replaceData = null;
   private int replaceIndex = -1;
   private int previousIndex = -1;
   private Map<?, Object> variables = new HashMap<>();
   private String beginToken;
   private String endToken;
   private int beginTokenLength;
   private int endTokenLength;
   private static final String DEFAULT_BEGIN_TOKEN = "${";
   private static final String DEFAULT_END_TOKEN = "}";

   public InterpolationFilterReader(Reader var1, Map<?, Object> var2, String var3, String var4) {
      super(var1);
      this.variables = var2;
      this.beginToken = var3;
      this.endToken = var4;
      this.beginTokenLength = var3.length();
      this.endTokenLength = var4.length();
   }

   public InterpolationFilterReader(Reader var1, Map<String, Object> var2) {
      this(var1, var2, "${", "}");
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
         char var8 = this.replaceData.charAt(this.replaceIndex++);
         if (this.replaceIndex >= this.replaceData.length()) {
            this.replaceIndex = -1;
         }

         return var8;
      } else {
         int var1;
         if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
            var1 = this.endToken.charAt(this.previousIndex++);
         } else {
            var1 = this.in.read();
         }

         if (var1 != this.beginToken.charAt(0)) {
            return var1;
         } else {
            StringBuilder var2 = new StringBuilder();
            int var3 = 1;

            do {
               if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
                  var1 = this.endToken.charAt(this.previousIndex++);
               } else {
                  var1 = this.in.read();
               }

               if (var1 == -1) {
                  break;
               }

               var2.append((char)var1);
               if (var3 < this.beginTokenLength && var1 != this.beginToken.charAt(var3++)) {
                  var1 = -1;
                  break;
               }
            } while (var1 != this.endToken.charAt(0));

            if (var1 != -1 && this.endTokenLength > 1) {
               int var4 = 1;

               do {
                  if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
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
               } while (var4 < this.endTokenLength);
            }

            if (var1 == -1) {
               this.replaceData = var2.toString();
               this.replaceIndex = 0;
               return this.beginToken.charAt(0);
            } else {
               String var9 = var2.substring(this.beginTokenLength - 1, var2.length() - this.endTokenLength);
               Object var5 = this.variables.get(var9);
               if (var5 != null) {
                  String var6 = var5.toString();
                  if (var6.length() != 0) {
                     this.replaceData = var6;
                     this.replaceIndex = 0;
                  }

                  return this.read();
               } else {
                  this.previousIndex = 0;
                  this.replaceData = var2.substring(0, var2.length() - this.endTokenLength);
                  this.replaceIndex = 0;
                  return this.beginToken.charAt(0);
               }
            }
         }
      }
   }
}
