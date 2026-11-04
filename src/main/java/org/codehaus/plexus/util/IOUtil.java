package org.codehaus.plexus.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channel;

public final class IOUtil {
   private static final int DEFAULT_BUFFER_SIZE = 16384;

   private IOUtil() {
   }

   public static void copy(InputStream var0, OutputStream var1) throws IOException {
      copy(var0, var1, 16384);
   }

   public static void copy(InputStream var0, OutputStream var1, int var2) throws IOException {
      byte[] var3 = new byte[var2];
      int var4 = 0;

      while (0 <= (var4 = var0.read(var3))) {
         var1.write(var3, 0, var4);
      }
   }

   public static void copy(Reader var0, Writer var1) throws IOException {
      copy(var0, var1, 16384);
   }

   public static void copy(Reader var0, Writer var1, int var2) throws IOException {
      char[] var3 = new char[var2];
      int var4 = 0;

      while (0 <= (var4 = var0.read(var3))) {
         var1.write(var3, 0, var4);
      }

      var1.flush();
   }

   public static void copy(InputStream var0, Writer var1) throws IOException {
      copy(var0, var1, 16384);
   }

   public static void copy(InputStream var0, Writer var1, int var2) throws IOException {
      InputStreamReader var3 = new InputStreamReader(var0);
      copy(var3, var1, var2);
   }

   public static void copy(InputStream var0, Writer var1, String var2) throws IOException {
      InputStreamReader var3 = new InputStreamReader(var0, var2);
      copy(var3, var1);
   }

   public static void copy(InputStream var0, Writer var1, String var2, int var3) throws IOException {
      InputStreamReader var4 = new InputStreamReader(var0, var2);
      copy(var4, var1, var3);
   }

   public static String toString(InputStream var0) throws IOException {
      return toString(var0, 16384);
   }

   public static String toString(InputStream var0, int var1) throws IOException {
      StringWriter var2 = new StringWriter();
      copy(var0, var2, var1);
      return var2.toString();
   }

   public static String toString(InputStream var0, String var1) throws IOException {
      return toString(var0, var1, 16384);
   }

   public static String toString(InputStream var0, String var1, int var2) throws IOException {
      StringWriter var3 = new StringWriter();
      copy(var0, var3, var1, var2);
      return var3.toString();
   }

   public static byte[] toByteArray(InputStream var0) throws IOException {
      return toByteArray(var0, 16384);
   }

   public static byte[] toByteArray(InputStream var0, int var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      copy(var0, var2, var1);
      return var2.toByteArray();
   }

   public static void copy(Reader var0, OutputStream var1) throws IOException {
      copy(var0, var1, 16384);
   }

   public static void copy(Reader var0, OutputStream var1, int var2) throws IOException {
      OutputStreamWriter var3 = new OutputStreamWriter(var1);
      copy(var0, var3, var2);
      var3.flush();
   }

   public static String toString(Reader var0) throws IOException {
      return toString(var0, 16384);
   }

   public static String toString(Reader var0, int var1) throws IOException {
      StringWriter var2 = new StringWriter();
      copy(var0, var2, var1);
      return var2.toString();
   }

   public static byte[] toByteArray(Reader var0) throws IOException {
      return toByteArray(var0, 16384);
   }

   public static byte[] toByteArray(Reader var0, int var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      copy(var0, var2, var1);
      return var2.toByteArray();
   }

   public static void copy(String var0, OutputStream var1) throws IOException {
      copy(var0, var1, 16384);
   }

   public static void copy(String var0, OutputStream var1, int var2) throws IOException {
      StringReader var3 = new StringReader(var0);
      OutputStreamWriter var4 = new OutputStreamWriter(var1);
      copy(var3, var4, var2);
      var4.flush();
   }

   public static void copy(String var0, Writer var1) throws IOException {
      var1.write(var0);
   }

      public static void bufferedCopy(InputStream var0, OutputStream var1) throws IOException {
      BufferedInputStream var2 = new BufferedInputStream(var0);
      BufferedOutputStream var3 = new BufferedOutputStream(var1);
      copy(var2, var3);
      var3.flush();
   }

   public static byte[] toByteArray(String var0) throws IOException {
      return toByteArray(var0, 16384);
   }

   public static byte[] toByteArray(String var0, int var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      copy(var0, var2, var1);
      return var2.toByteArray();
   }

   public static void copy(byte[] var0, Writer var1) throws IOException {
      copy(var0, var1, 16384);
   }

   public static void copy(byte[] var0, Writer var1, int var2) throws IOException {
      ByteArrayInputStream var3 = new ByteArrayInputStream(var0);
      copy(var3, var1, var2);
   }

   public static void copy(byte[] var0, Writer var1, String var2) throws IOException {
      ByteArrayInputStream var3 = new ByteArrayInputStream(var0);
      copy(var3, var1, var2);
   }

   public static void copy(byte[] var0, Writer var1, String var2, int var3) throws IOException {
      ByteArrayInputStream var4 = new ByteArrayInputStream(var0);
      copy(var4, var1, var2, var3);
   }

   public static String toString(byte[] var0) throws IOException {
      return toString(var0, 16384);
   }

   public static String toString(byte[] var0, int var1) throws IOException {
      StringWriter var2 = new StringWriter();
      copy(var0, var2, var1);
      return var2.toString();
   }

   public static String toString(byte[] var0, String var1) throws IOException {
      return toString(var0, var1, 16384);
   }

   public static String toString(byte[] var0, String var1, int var2) throws IOException {
      StringWriter var3 = new StringWriter();
      copy(var0, var3, var1, var2);
      return var3.toString();
   }

   public static void copy(byte[] var0, OutputStream var1) throws IOException {
      copy(var0, var1, 16384);
   }

   public static void copy(byte[] var0, OutputStream var1, int var2) throws IOException {
      var1.write(var0);
   }

   public static boolean contentEquals(InputStream var0, InputStream var1) throws IOException {
      BufferedInputStream var2 = new BufferedInputStream(var0);
      BufferedInputStream var3 = new BufferedInputStream(var1);

      for (int var4 = var2.read(); 0 <= var4; var4 = var2.read()) {
         int var5 = var3.read();
         if (var4 != var5) {
            return false;
         }
      }

      int var6 = var3.read();
      return 0 > var6;
   }

   public static void close(InputStream var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }
   }

   public static void close(Channel var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }
   }

   public static void close(OutputStream var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }
   }

   public static void close(Reader var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }
   }

   public static void close(Writer var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }
   }
}
