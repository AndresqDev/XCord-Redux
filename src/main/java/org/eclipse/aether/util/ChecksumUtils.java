package org.eclipse.aether.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class ChecksumUtils {
   private ChecksumUtils() {
   }

   public static String read(File var0) throws IOException {
      String var1 = "";

      try (BufferedReader var2 = new BufferedReader(new InputStreamReader(new FileInputStream(var0), StandardCharsets.UTF_8), 512)) {
         while (true) {
            String var4 = var2.readLine();
            if (var4 == null) {
               break;
            }

            var4 = var4.trim();
            if (var4.length() > 0) {
               var1 = var4;
               break;
            }
         }
      }

      if (var1.matches(".+= [0-9A-Fa-f]+")) {
         int var14 = var1.lastIndexOf(32);
         var1 = var1.substring(var14 + 1);
      } else {
         int var15 = var1.indexOf(32);
         if (var15 != -1) {
            var1 = var1.substring(0, var15);
         }
      }

      return var1;
   }

   public static Map<String, Object> calc(File var0, Collection<String> var1) throws IOException {
      return calc(new FileInputStream(var0), var1);
   }

   public static Map<String, Object> calc(byte[] var0, Collection<String> var1) throws IOException {
      return calc(new ByteArrayInputStream(var0), var1);
   }

   private static Map<String, Object> calc(InputStream var0, Collection<String> var1) throws IOException {
      LinkedHashMap var2 = new LinkedHashMap();
      LinkedHashMap var3 = new LinkedHashMap();

      for (String var5 : var1) {
         try {
            var3.put(var5, MessageDigest.getInstance(var5));
         } catch (NoSuchAlgorithmException var18) {
            var2.put(var5, var18);
         }
      }

      try (InputStream var21 = var0) {
         byte[] var6 = new byte[32768];

         while (true) {
            int var7 = var21.read(var6);
            if (var7 < 0) {
               break;
            }

            for (MessageDigest var9 : var3.values()) {
               var9.update(var6, 0, var7);
            }
         }
      }

      for (Entry var24 : var3.entrySet()) {
         byte[] var25 = ((MessageDigest)var24.getValue()).digest();
         var2.put(var24.getKey(), toHexString(var25));
      }

      return var2;
   }

   public static String toHexString(byte[] var0) {
      if (var0 == null) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder(var0.length * 2);

         for (byte var5 : var0) {
            int var6 = var5 & 255;
            if (var6 < 16) {
               var1.append('0');
            }

            var1.append(Integer.toHexString(var6));
         }

         return var1.toString();
      }
   }
}
