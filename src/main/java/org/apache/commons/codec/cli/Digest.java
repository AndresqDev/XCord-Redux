package org.apache.commons.codec.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

public class Digest {
   private final String algorithm;
   private final String[] args;
   private final String[] inputs;

   public static void main(String[] var0) throws IOException {
      new Digest(var0).run();
   }

   private Digest(String[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("args");
      } else if (var1.length == 0) {
         throw new IllegalArgumentException(String.format("Usage: java %s [algorithm] [FILE|DIRECTORY|string] ...", Digest.class.getName()));
      } else {
         this.args = var1;
         this.algorithm = var1[0];
         if (var1.length <= 1) {
            this.inputs = null;
         } else {
            this.inputs = new String[var1.length - 1];
            System.arraycopy(var1, 1, this.inputs, 0, this.inputs.length);
         }
      }
   }

   private void println(String var1, byte[] var2) {
      this.println(var1, var2, null);
   }

   private void println(String var1, byte[] var2, String var3) {
      System.out.println(var1 + Hex.encodeHexString(var2) + (var3 != null ? "  " + var3 : ""));
   }

   private void run() throws IOException {
      if (!this.algorithm.equalsIgnoreCase("ALL") && !this.algorithm.equals("*")) {
         MessageDigest var1 = DigestUtils.getDigest(this.algorithm, null);
         if (var1 != null) {
            this.run("", var1);
         } else {
            this.run("", DigestUtils.getDigest(this.algorithm.toUpperCase(Locale.ROOT)));
         }
      } else {
         this.run(MessageDigestAlgorithms.values());
      }
   }

   private void run(String[] var1) throws IOException {
      for (String var5 : var1) {
         if (DigestUtils.isAvailable(var5)) {
            this.run(var5 + " ", var5);
         }
      }
   }

   private void run(String var1, MessageDigest var2) throws IOException {
      if (this.inputs == null) {
         this.println(var1, DigestUtils.digest(var2, System.in));
      } else {
         for (String var6 : this.inputs) {
            File var7 = new File(var6);
            if (var7.isFile()) {
               this.println(var1, DigestUtils.digest(var2, var7), var6);
            } else if (var7.isDirectory()) {
               File[] var8 = var7.listFiles();
               if (var8 != null) {
                  this.run(var1, var2, var8);
               }
            } else {
               byte[] var9 = var6.getBytes(Charset.defaultCharset());
               this.println(var1, DigestUtils.digest(var2, var9));
            }
         }
      }
   }

   private void run(String var1, MessageDigest var2, File[] var3) throws IOException {
      for (File var7 : var3) {
         if (var7.isFile()) {
            this.println(var1, DigestUtils.digest(var2, var7), var7.getName());
         }
      }
   }

   private void run(String var1, String var2) throws IOException {
      this.run(var1, DigestUtils.getDigest(var2));
   }

   @Override
   public String toString() {
      return String.format("%s %s", super.toString(), Arrays.toString((Object[])this.args));
   }
}
