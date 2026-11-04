package io.netty.handler.codec.http;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public class QueryStringEncoder {
   private final Charset charset;
   private final StringBuilder uriBuilder;
   private boolean hasParams;
   private static final byte WRITE_UTF_UNKNOWN = 63;
   private static final char[] CHAR_MAP = "0123456789ABCDEF".toCharArray();

   public QueryStringEncoder(String var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringEncoder(String var1, Charset var2) {
      ObjectUtil.checkNotNull(var2, "charset");
      this.uriBuilder = new StringBuilder(var1);
      this.charset = CharsetUtil.UTF_8.equals(var2) ? null : var2;
   }

   public void addParam(String var1, String var2) {
      ObjectUtil.checkNotNull(var1, "name");
      if (this.hasParams) {
         this.uriBuilder.append('&');
      } else {
         this.uriBuilder.append('?');
         this.hasParams = true;
      }

      this.encodeComponent(var1);
      if (var2 != null) {
         this.uriBuilder.append('=');
         this.encodeComponent(var2);
      }
   }

   private void encodeComponent(CharSequence var1) {
      if (this.charset == null) {
         this.encodeUtf8Component(var1);
      } else {
         this.encodeNonUtf8Component(var1);
      }
   }

   public URI toUri() throws URISyntaxException {
      return new URI(this.toString());
   }

   @Override
   public String toString() {
      return this.uriBuilder.toString();
   }

   private void encodeNonUtf8Component(CharSequence var1) {
      char[] var2 = null;
      int var3 = 0;
      int var4 = var1.length();

      while (var3 < var4) {
         char var5 = var1.charAt(var3);
         if (dontNeedEncoding(var5)) {
            this.uriBuilder.append(var5);
            var3++;
         } else {
            int var6 = 0;
            if (var2 == null) {
               var2 = new char[var1.length() - var3];
            }

            do {
               var2[var6] = var5;
               var6++;
               var3++;
            } while (var3 < var1.length() && !dontNeedEncoding(var5 = var1.charAt(var3)));

            byte[] var7 = new String(var2, 0, var6).getBytes(this.charset);

            for (byte var11 : var7) {
               this.appendEncoded(var11);
            }
         }
      }
   }

   private void encodeUtf8Component(CharSequence var1) {
      int var2 = 0;

      for (int var3 = var1.length(); var2 < var3; var2++) {
         char var4 = var1.charAt(var2);
         if (!dontNeedEncoding(var4)) {
            this.encodeUtf8Component(var1, var2, var3);
            return;
         }
      }

      this.uriBuilder.append(var1);
   }

   private void encodeUtf8Component(CharSequence var1, int var2, int var3) {
      if (var2 > 0) {
         this.uriBuilder.append(var1, 0, var2);
      }

      this.encodeUtf8ComponentSlow(var1, var2, var3);
   }

   private void encodeUtf8ComponentSlow(CharSequence var1, int var2, int var3) {
      for (int var4 = var2; var4 < var3; var4++) {
         char var5 = var1.charAt(var4);
         if (var5 < 128) {
            if (dontNeedEncoding(var5)) {
               this.uriBuilder.append(var5);
            } else {
               this.appendEncoded(var5);
            }
         } else if (var5 < 2048) {
            this.appendEncoded(192 | var5 >> 6);
            this.appendEncoded(128 | var5 & '?');
         } else if (StringUtil.isSurrogate(var5)) {
            if (!Character.isHighSurrogate(var5)) {
               this.appendEncoded(63);
            } else {
               if (++var4 == var1.length()) {
                  this.appendEncoded(63);
                  break;
               }

               this.writeUtf8Surrogate(var5, var1.charAt(var4));
            }
         } else {
            this.appendEncoded(224 | var5 >> '\f');
            this.appendEncoded(128 | var5 >> 6 & 63);
            this.appendEncoded(128 | var5 & '?');
         }
      }
   }

   private void writeUtf8Surrogate(char var1, char var2) {
      if (!Character.isLowSurrogate(var2)) {
         this.appendEncoded(63);
         this.appendEncoded(Character.isHighSurrogate(var2) ? 63 : var2);
      } else {
         int var3 = Character.toCodePoint(var1, var2);
         this.appendEncoded(240 | var3 >> 18);
         this.appendEncoded(128 | var3 >> 12 & 63);
         this.appendEncoded(128 | var3 >> 6 & 63);
         this.appendEncoded(128 | var3 & 63);
      }
   }

   private void appendEncoded(int var1) {
      this.uriBuilder.append('%').append(forDigit(var1 >> 4)).append(forDigit(var1));
   }

   private static char forDigit(int var0) {
      return CHAR_MAP[var0 & 15];
   }

   private static boolean dontNeedEncoding(char var0) {
      return var0 >= 'a' && var0 <= 'z'
         || var0 >= 'A' && var0 <= 'Z'
         || var0 >= '0' && var0 <= '9'
         || var0 == '-'
         || var0 == '_'
         || var0 == '.'
         || var0 == '*'
         || var0 == '~';
   }
}
