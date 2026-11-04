package org.apache.http.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.TokenParser;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class URLEncodedUtils {
   public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
   private static final char QP_SEP_A = '&';
   private static final char QP_SEP_S = ';';
   private static final String NAME_VALUE_SEPARATOR = "=";
   private static final char PATH_SEPARATOR = '/';
   private static final BitSet PATH_SEPARATORS = new BitSet(256);
   private static final BitSet UNRESERVED = new BitSet(256);
   private static final BitSet PUNCT = new BitSet(256);
   private static final BitSet USERINFO = new BitSet(256);
   private static final BitSet PATHSAFE = new BitSet(256);
   private static final BitSet URIC = new BitSet(256);
   private static final BitSet RESERVED = new BitSet(256);
   private static final BitSet URLENCODER = new BitSet(256);
   private static final BitSet PATH_SPECIAL = new BitSet(256);
   private static final int RADIX = 16;

   @Deprecated
   public static List<NameValuePair> parse(URI var0, String var1) {
      return parse(var0, var1 != null ? Charset.forName(var1) : null);
   }

   public static List<NameValuePair> parse(URI var0, Charset var1) {
      Args.notNull(var0, "URI");
      String var2 = var0.getRawQuery();
      return var2 != null && !var2.isEmpty() ? parse(var2, var1) : createEmptyList();
   }

   public static List<NameValuePair> parse(HttpEntity var0) throws IOException {
      Args.notNull(var0, "HTTP entity");
      ContentType var1 = ContentType.get(var0);
      if (var1 != null && var1.getMimeType().equalsIgnoreCase("application/x-www-form-urlencoded")) {
         long var2 = var0.getContentLength();
         Args.check(var2 <= 2147483647L, "HTTP entity is too large");
         Charset var4 = var1.getCharset() != null ? var1.getCharset() : HTTP.DEF_CONTENT_CHARSET;
         InputStream var5 = var0.getContent();
         if (var5 == null) {
            return createEmptyList();
         } else {
            CharArrayBuffer var6;
            try {
               var6 = new CharArrayBuffer(var2 > 0L ? (int)var2 : 1024);
               InputStreamReader var7 = new InputStreamReader(var5, var4);
               char[] var8 = new char[1024];

               int var9;
               while ((var9 = var7.read(var8)) != -1) {
                  var6.append(var8, 0, var9);
               }
            } finally {
               var5.close();
            }

            return var6.isEmpty() ? createEmptyList() : parse(var6, var4, '&');
         }
      } else {
         return createEmptyList();
      }
   }

   public static boolean isEncoded(HttpEntity var0) {
      Args.notNull(var0, "HTTP entity");
      Header var1 = var0.getContentType();
      if (var1 != null) {
         HeaderElement[] var2 = var1.getElements();
         if (var2.length > 0) {
            String var3 = var2[0].getName();
            return var3.equalsIgnoreCase("application/x-www-form-urlencoded");
         }
      }

      return false;
   }

   @Deprecated
   public static void parse(List<NameValuePair> var0, Scanner var1, String var2) {
      parse(var0, var1, "[&;]", var2);
   }

   @Deprecated
   public static void parse(List<NameValuePair> var0, Scanner var1, String var2, String var3) {
      var1.useDelimiter(var2);

      while (var1.hasNext()) {
         String var6 = var1.next();
         int var7 = var6.indexOf("=");
         String var4;
         String var5;
         if (var7 != -1) {
            var4 = decodeFormFields(var6.substring(0, var7).trim(), var3);
            var5 = decodeFormFields(var6.substring(var7 + 1).trim(), var3);
         } else {
            var4 = decodeFormFields(var6.trim(), var3);
            var5 = null;
         }

         var0.add(new BasicNameValuePair(var4, var5));
      }
   }

   public static List<NameValuePair> parse(String var0, Charset var1) {
      if (var0 == null) {
         return createEmptyList();
      } else {
         CharArrayBuffer var2 = new CharArrayBuffer(var0.length());
         var2.append(var0);
         return parse(var2, var1, '&', ';');
      }
   }

   public static List<NameValuePair> parse(String var0, Charset var1, char... var2) {
      if (var0 == null) {
         return createEmptyList();
      } else {
         CharArrayBuffer var3 = new CharArrayBuffer(var0.length());
         var3.append(var0);
         return parse(var3, var1, var2);
      }
   }

   public static List<NameValuePair> parse(CharArrayBuffer var0, Charset var1, char... var2) {
      Args.notNull(var0, "Char array buffer");
      TokenParser var3 = TokenParser.INSTANCE;
      BitSet var4 = new BitSet();

      for (char var8 : var2) {
         var4.set(var8);
      }

      ParserCursor var10 = new ParserCursor(0, var0.length());
      ArrayList var11 = new ArrayList();

      while (!var10.atEnd()) {
         var4.set(61);
         String var12 = var3.parseToken(var0, var10, var4);
         String var13 = null;
         if (!var10.atEnd()) {
            char var9 = var0.charAt(var10.getPos());
            var10.updatePos(var10.getPos() + 1);
            if (var9 == '=') {
               var4.clear(61);
               var13 = var3.parseToken(var0, var10, var4);
               if (!var10.atEnd()) {
                  var10.updatePos(var10.getPos() + 1);
               }
            }
         }

         if (!var12.isEmpty()) {
            var11.add(new BasicNameValuePair(decodeFormFields(var12, var1), decodeFormFields(var13, var1)));
         }
      }

      return var11;
   }

   static List<String> splitSegments(CharSequence var0, BitSet var1) {
      ParserCursor var2 = new ParserCursor(0, var0.length());
      if (var2.atEnd()) {
         return Collections.emptyList();
      } else {
         if (var1.get(var0.charAt(var2.getPos()))) {
            var2.updatePos(var2.getPos() + 1);
         }

         ArrayList var3 = new ArrayList();

         StringBuilder var4;
         for (var4 = new StringBuilder(); !var2.atEnd(); var2.updatePos(var2.getPos() + 1)) {
            char var5 = var0.charAt(var2.getPos());
            if (var1.get(var5)) {
               var3.add(var4.toString());
               var4.setLength(0);
            } else {
               var4.append(var5);
            }
         }

         var3.add(var4.toString());
         return var3;
      }
   }

   static List<String> splitPathSegments(CharSequence var0) {
      return splitSegments(var0, PATH_SEPARATORS);
   }

   public static List<String> parsePathSegments(CharSequence var0, Charset var1) {
      Args.notNull(var0, "Char sequence");
      List var2 = splitPathSegments(var0);

      for (int var3 = 0; var3 < var2.size(); var3++) {
         var2.set(var3, urlDecode((String)var2.get(var3), var1 != null ? var1 : Consts.UTF_8, false));
      }

      return var2;
   }

   public static List<String> parsePathSegments(CharSequence var0) {
      return parsePathSegments(var0, Consts.UTF_8);
   }

   public static String formatSegments(Iterable<String> var0, Charset var1) {
      Args.notNull(var0, "Segments");
      StringBuilder var2 = new StringBuilder();

      for (String var4 : var0) {
         var2.append('/').append(urlEncode(var4, var1, PATHSAFE, false));
      }

      return var2.toString();
   }

   public static String formatSegments(String... var0) {
      return formatSegments(Arrays.asList(var0), Consts.UTF_8);
   }

   public static String format(List<? extends NameValuePair> var0, String var1) {
      return format(var0, '&', var1);
   }

   public static String format(List<? extends NameValuePair> var0, char var1, String var2) {
      StringBuilder var3 = new StringBuilder();

      for (NameValuePair var5 : var0) {
         String var6 = encodeFormFields(var5.getName(), var2);
         String var7 = encodeFormFields(var5.getValue(), var2);
         if (var3.length() > 0) {
            var3.append(var1);
         }

         var3.append(var6);
         if (var7 != null) {
            var3.append("=");
            var3.append(var7);
         }
      }

      return var3.toString();
   }

   public static String format(Iterable<? extends NameValuePair> var0, Charset var1) {
      return format(var0, '&', var1);
   }

   public static String format(Iterable<? extends NameValuePair> var0, char var1, Charset var2) {
      Args.notNull(var0, "Parameters");
      StringBuilder var3 = new StringBuilder();

      for (NameValuePair var5 : var0) {
         String var6 = encodeFormFields(var5.getName(), var2);
         String var7 = encodeFormFields(var5.getValue(), var2);
         if (var3.length() > 0) {
            var3.append(var1);
         }

         var3.append(var6);
         if (var7 != null) {
            var3.append("=");
            var3.append(var7);
         }
      }

      return var3.toString();
   }

   private static List<NameValuePair> createEmptyList() {
      return new ArrayList<>(0);
   }

   private static String urlEncode(String var0, Charset var1, BitSet var2, boolean var3) {
      if (var0 == null) {
         return null;
      } else {
         StringBuilder var4 = new StringBuilder();
         ByteBuffer var5 = var1.encode(var0);

         while (var5.hasRemaining()) {
            int var6 = var5.get() & 255;
            if (var2.get(var6)) {
               var4.append((char)var6);
            } else if (var3 && var6 == 32) {
               var4.append('+');
            } else {
               var4.append("%");
               char var7 = Character.toUpperCase(Character.forDigit(var6 >> 4 & 15, 16));
               char var8 = Character.toUpperCase(Character.forDigit(var6 & 15, 16));
               var4.append(var7);
               var4.append(var8);
            }
         }

         return var4.toString();
      }
   }

   private static String urlDecode(String var0, Charset var1, boolean var2) {
      if (var0 == null) {
         return null;
      } else {
         ByteBuffer var3 = ByteBuffer.allocate(var0.length());
         CharBuffer var4 = CharBuffer.wrap(var0);

         while (var4.hasRemaining()) {
            char var5 = var4.get();
            if (var5 == '%' && var4.remaining() >= 2) {
               char var6 = var4.get();
               char var7 = var4.get();
               int var8 = Character.digit(var6, 16);
               int var9 = Character.digit(var7, 16);
               if (var8 != -1 && var9 != -1) {
                  var3.put((byte)((var8 << 4) + var9));
               } else {
                  var3.put((byte)37);
                  var3.put((byte)var6);
                  var3.put((byte)var7);
               }
            } else if (var2 && var5 == '+') {
               var3.put((byte)32);
            } else {
               var3.put((byte)var5);
            }
         }

         ((Buffer)var3).flip();
         return var1.decode(var3).toString();
      }
   }

   private static String decodeFormFields(String var0, String var1) {
      return var0 == null ? null : urlDecode(var0, var1 != null ? Charset.forName(var1) : Consts.UTF_8, true);
   }

   private static String decodeFormFields(String var0, Charset var1) {
      return var0 == null ? null : urlDecode(var0, var1 != null ? var1 : Consts.UTF_8, true);
   }

   private static String encodeFormFields(String var0, String var1) {
      return var0 == null ? null : urlEncode(var0, var1 != null ? Charset.forName(var1) : Consts.UTF_8, URLENCODER, true);
   }

   private static String encodeFormFields(String var0, Charset var1) {
      return var0 == null ? null : urlEncode(var0, var1 != null ? var1 : Consts.UTF_8, URLENCODER, true);
   }

   static String encUserInfo(String var0, Charset var1) {
      return urlEncode(var0, var1, USERINFO, false);
   }

   static String encUric(String var0, Charset var1) {
      return urlEncode(var0, var1, URIC, false);
   }

   static String encPath(String var0, Charset var1) {
      return urlEncode(var0, var1, PATH_SPECIAL, false);
   }

   static {
      PATH_SEPARATORS.set(47);

      for (int var0 = 97; var0 <= 122; var0++) {
         UNRESERVED.set(var0);
      }

      for (int var1 = 65; var1 <= 90; var1++) {
         UNRESERVED.set(var1);
      }

      for (int var2 = 48; var2 <= 57; var2++) {
         UNRESERVED.set(var2);
      }

      UNRESERVED.set(95);
      UNRESERVED.set(45);
      UNRESERVED.set(46);
      UNRESERVED.set(42);
      URLENCODER.or(UNRESERVED);
      UNRESERVED.set(33);
      UNRESERVED.set(126);
      UNRESERVED.set(39);
      UNRESERVED.set(40);
      UNRESERVED.set(41);
      PUNCT.set(44);
      PUNCT.set(59);
      PUNCT.set(58);
      PUNCT.set(36);
      PUNCT.set(38);
      PUNCT.set(43);
      PUNCT.set(61);
      USERINFO.or(UNRESERVED);
      USERINFO.or(PUNCT);
      PATHSAFE.or(UNRESERVED);
      PATHSAFE.set(59);
      PATHSAFE.set(58);
      PATHSAFE.set(64);
      PATHSAFE.set(38);
      PATHSAFE.set(61);
      PATHSAFE.set(43);
      PATHSAFE.set(36);
      PATHSAFE.set(44);
      PATH_SPECIAL.or(PATHSAFE);
      PATH_SPECIAL.set(47);
      RESERVED.set(59);
      RESERVED.set(47);
      RESERVED.set(63);
      RESERVED.set(58);
      RESERVED.set(64);
      RESERVED.set(38);
      RESERVED.set(61);
      RESERVED.set(43);
      RESERVED.set(36);
      RESERVED.set(44);
      RESERVED.set(91);
      RESERVED.set(93);
      URIC.or(RESERVED);
      URIC.or(UNRESERVED);
   }
}
