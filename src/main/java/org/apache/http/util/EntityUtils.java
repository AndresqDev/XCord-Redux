package org.apache.http.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

public final class EntityUtils {
   private static final int DEFAULT_BUFFER_SIZE = 4096;

   private EntityUtils() {
   }

   public static void consumeQuietly(HttpEntity var0) {
      try {
         consume(var0);
      } catch (IOException var2) {
      }
   }

   public static void consume(HttpEntity var0) throws IOException {
      if (var0 != null) {
         if (var0.isStreaming()) {
            InputStream var1 = var0.getContent();
            if (var1 != null) {
               var1.close();
            }
         }
      }
   }

   public static void updateEntity(HttpResponse var0, HttpEntity var1) throws IOException {
      Args.notNull(var0, "Response");
      consume(var0.getEntity());
      var0.setEntity(var1);
   }

   public static byte[] toByteArray(HttpEntity var0) throws IOException {
      Args.notNull(var0, "Entity");
      InputStream var1 = var0.getContent();
      if (var1 == null) {
         return null;
      } else {
         byte[] var6;
         try {
            Args.check(var0.getContentLength() <= 2147483647L, "HTTP entity too large to be buffered in memory");
            int var2 = (int)var0.getContentLength();
            if (var2 < 0) {
               var2 = 4096;
            }

            ByteArrayBuffer var3 = new ByteArrayBuffer(var2);
            byte[] var4 = new byte[4096];

            int var5;
            while ((var5 = var1.read(var4)) != -1) {
               var3.append(var4, 0, var5);
            }

            var6 = var3.toByteArray();
         } finally {
            var1.close();
         }

         return var6;
      }
   }

   @Deprecated
   public static String getContentCharSet(HttpEntity var0) throws ParseException {
      Args.notNull(var0, "Entity");
      String var1 = null;
      if (var0.getContentType() != null) {
         HeaderElement[] var2 = var0.getContentType().getElements();
         if (var2.length > 0) {
            NameValuePair var3 = var2[0].getParameterByName("charset");
            if (var3 != null) {
               var1 = var3.getValue();
            }
         }
      }

      return var1;
   }

   @Deprecated
   public static String getContentMimeType(HttpEntity var0) throws ParseException {
      Args.notNull(var0, "Entity");
      String var1 = null;
      if (var0.getContentType() != null) {
         HeaderElement[] var2 = var0.getContentType().getElements();
         if (var2.length > 0) {
            var1 = var2[0].getName();
         }
      }

      return var1;
   }

   private static String toString(HttpEntity var0, ContentType var1) throws IOException {
      InputStream var2 = var0.getContent();
      if (var2 == null) {
         return null;
      } else {
         String var9;
         try {
            Args.check(var0.getContentLength() <= 2147483647L, "HTTP entity too large to be buffered in memory");
            int var3 = (int)var0.getContentLength();
            if (var3 < 0) {
               var3 = 4096;
            }

            Charset var4 = null;
            if (var1 != null) {
               var4 = var1.getCharset();
               if (var4 == null) {
                  ContentType var5 = ContentType.getByMimeType(var1.getMimeType());
                  var4 = var5 != null ? var5.getCharset() : null;
               }
            }

            if (var4 == null) {
               var4 = HTTP.DEF_CONTENT_CHARSET;
            }

            InputStreamReader var13 = new InputStreamReader(var2, var4);
            CharArrayBuffer var6 = new CharArrayBuffer(var3);
            char[] var7 = new char[1024];

            int var8;
            while ((var8 = var13.read(var7)) != -1) {
               var6.append(var7, 0, var8);
            }

            var9 = var6.toString();
         } finally {
            var2.close();
         }

         return var9;
      }
   }

   public static String toString(HttpEntity var0, Charset var1) throws IOException, ParseException {
      Args.notNull(var0, "Entity");
      ContentType var2 = null;

      try {
         var2 = ContentType.get(var0);
      } catch (UnsupportedCharsetException var4) {
         if (var1 == null) {
            throw new UnsupportedEncodingException(var4.getMessage());
         }
      }

      if (var2 != null) {
         if (var2.getCharset() == null) {
            var2 = var2.withCharset(var1);
         }
      } else {
         var2 = ContentType.DEFAULT_TEXT.withCharset(var1);
      }

      return toString(var0, var2);
   }

   public static String toString(HttpEntity var0, String var1) throws IOException, ParseException {
      return toString(var0, var1 != null ? Charset.forName(var1) : null);
   }

   public static String toString(HttpEntity var0) throws IOException, ParseException {
      Args.notNull(var0, "Entity");
      return toString(var0, ContentType.get(var0));
   }
}
