package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {
   private static final int DEFAULT_MAX_PARAMS = 1024;
   private final Charset charset;
   private final String uri;
   private final int maxParams;
   private final boolean semicolonIsNormalChar;
   private int pathEndIdx;
   private String path;
   private Map<String, List<String>> params;

   public QueryStringDecoder(String var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringDecoder(String var1, boolean var2) {
      this(var1, HttpConstants.DEFAULT_CHARSET, var2);
   }

   public QueryStringDecoder(String var1, Charset var2) {
      this(var1, var2, true);
   }

   public QueryStringDecoder(String var1, Charset var2, boolean var3) {
      this(var1, var2, var3, 1024);
   }

   public QueryStringDecoder(String var1, Charset var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public QueryStringDecoder(String var1, Charset var2, boolean var3, int var4, boolean var5) {
      this.uri = ObjectUtil.checkNotNull(var1, "uri");
      this.charset = ObjectUtil.checkNotNull(var2, "charset");
      this.maxParams = ObjectUtil.checkPositive(var4, "maxParams");
      this.semicolonIsNormalChar = var5;
      this.pathEndIdx = var3 ? -1 : 0;
   }

   public QueryStringDecoder(URI var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringDecoder(URI var1, Charset var2) {
      this(var1, var2, 1024);
   }

   public QueryStringDecoder(URI var1, Charset var2, int var3) {
      this(var1, var2, var3, false);
   }

   public QueryStringDecoder(URI var1, Charset var2, int var3, boolean var4) {
      String var5 = var1.getRawPath();
      if (var5 == null) {
         var5 = "";
      }

      String var6 = var1.getRawQuery();
      this.uri = var6 == null ? var5 : var5 + '?' + var6;
      this.charset = ObjectUtil.checkNotNull(var2, "charset");
      this.maxParams = ObjectUtil.checkPositive(var3, "maxParams");
      this.semicolonIsNormalChar = var4;
      this.pathEndIdx = var5.length();
   }

   @Override
   public String toString() {
      return this.uri();
   }

   public String uri() {
      return this.uri;
   }

   public String path() {
      if (this.path == null) {
         this.path = decodeComponent(this.uri, 0, this.pathEndIdx(), this.charset, true);
      }

      return this.path;
   }

   public Map<String, List<String>> parameters() {
      if (this.params == null) {
         this.params = decodeParams(this.uri, this.pathEndIdx(), this.charset, this.maxParams, this.semicolonIsNormalChar);
      }

      return this.params;
   }

   public String rawPath() {
      return this.uri.substring(0, this.pathEndIdx());
   }

   public String rawQuery() {
      int var1 = this.pathEndIdx() + 1;
      return var1 < this.uri.length() ? this.uri.substring(var1) : "";
   }

   private int pathEndIdx() {
      if (this.pathEndIdx == -1) {
         this.pathEndIdx = findPathEndIndex(this.uri);
      }

      return this.pathEndIdx;
   }

   private static Map<String, List<String>> decodeParams(String var0, int var1, Charset var2, int var3, boolean var4) {
      int var5 = var0.length();
      if (var1 >= var5) {
         return Collections.emptyMap();
      } else {
         if (var0.charAt(var1) == '?') {
            var1++;
         }

         LinkedHashMap var6 = new LinkedHashMap();
         int var7 = var1;
         int var8 = -1;

         int var9;
         label42:
         for (var9 = var1; var9 < var5; var9++) {
            switch (var0.charAt(var9)) {
               case '#':
                  break label42;
               case ';':
                  if (var4) {
                     break;
                  }
               case '&':
                  if (addParam(var0, var7, var8, var9, var6, var2)) {
                     if (--var3 == 0) {
                        return var6;
                     }
                  }

                  var7 = var9 + 1;
                  break;
               case '=':
                  if (var7 == var9) {
                     var7 = var9 + 1;
                  } else if (var8 < var7) {
                     var8 = var9 + 1;
                  }
            }
         }

         addParam(var0, var7, var8, var9, var6, var2);
         return var6;
      }
   }

   private static boolean addParam(String var0, int var1, int var2, int var3, Map<String, List<String>> var4, Charset var5) {
      if (var1 >= var3) {
         return false;
      } else {
         if (var2 <= var1) {
            var2 = var3 + 1;
         }

         String var6 = decodeComponent(var0, var1, var2 - 1, var5, false);
         String var7 = decodeComponent(var0, var2, var3, var5, false);
         Object var8 = (List)var4.get(var6);
         if (var8 == null) {
            var8 = new ArrayList(1);
            var4.put(var6, var8);
         }

         var8.add(var7);
         return true;
      }
   }

   public static String decodeComponent(String var0) {
      return decodeComponent(var0, HttpConstants.DEFAULT_CHARSET);
   }

   public static String decodeComponent(String var0, Charset var1) {
      return var0 == null ? "" : decodeComponent(var0, 0, var0.length(), var1, false);
   }

   private static String decodeComponent(String var0, int var1, int var2, Charset var3, boolean var4) {
      int var5 = var2 - var1;
      if (var5 <= 0) {
         return "";
      } else {
         int var6 = -1;

         for (int var7 = var1; var7 < var2; var7++) {
            char var8 = var0.charAt(var7);
            if (var8 == '%' || var8 == '+' && !var4) {
               var6 = var7;
               break;
            }
         }

         if (var6 == -1) {
            return var0.substring(var1, var2);
         } else {
            int var13 = (var2 - var6) / 3;
            byte[] var14 = PlatformDependent.allocateUninitializedArray(var13);
            StringBuilder var10 = new StringBuilder(var5);
            var10.append(var0, var1, var6);

            label55:
            for (int var11 = var6; var11 < var2; var11++) {
               char var12 = var0.charAt(var11);
               if (var12 != '%') {
                  var10.append(var12 == '+' && !var4 ? ' ' : var12);
               } else {
                  int var9 = 0;

                  while (var11 + 3 <= var2) {
                     var14[var9++] = StringUtil.decodeHexByte(var0, var11 + 1);
                     var11 += 3;
                     if (var11 >= var2 || var0.charAt(var11) != '%') {
                        var11--;
                        var10.append(new String(var14, 0, var9, var3));
                        continue label55;
                     }
                  }

                  throw new IllegalArgumentException("unterminated escape sequence at index " + var11 + " of: " + var0);
               }
            }

            return var10.toString();
         }
      }
   }

   private static int findPathEndIndex(String var0) {
      int var1 = var0.length();

      for (int var2 = 0; var2 < var1; var2++) {
         char var3 = var0.charAt(var2);
         if (var3 == '?' || var3 == '#') {
            return var2;
         }
      }

      return var1;
   }
}
