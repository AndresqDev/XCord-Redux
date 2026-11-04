package org.eclipse.aether.connector.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.util.ChecksumUtils;

final class ChecksumCalculator {
   private final List<ChecksumCalculator.Checksum> checksums = new ArrayList<>();
   private final File targetFile;

   public static ChecksumCalculator newInstance(File var0, Collection<RepositoryLayout.Checksum> var1) {
      return var1 != null && !var1.isEmpty() ? new ChecksumCalculator(var0, var1) : null;
   }

   private ChecksumCalculator(File var1, Collection<RepositoryLayout.Checksum> var2) {
      HashSet var3 = new HashSet();

      for (RepositoryLayout.Checksum var5 : var2) {
         String var6 = var5.getAlgorithm();
         if (var3.add(var6)) {
            this.checksums.add(new ChecksumCalculator.Checksum(var6));
         }
      }

      this.targetFile = var1;
   }

   public void init(long var1) {
      for (ChecksumCalculator.Checksum var4 : this.checksums) {
         var4.reset();
      }

      if (var1 > 0L) {
         FileInputStream var19 = null;

         try {
            var19 = new FileInputStream(this.targetFile);
            long var21 = 0L;
            ByteBuffer var22 = ByteBuffer.allocate(32768);
            byte[] var7 = var22.array();

            while (var21 < var1) {
               int var8 = var19.read(var7);
               if (var8 < 0) {
                  throw new IOException(this.targetFile + " contains only " + var21 + " bytes, cannot resume download from offset " + var1);
               }

               var21 += (long)var8;
               if (var21 > var1) {
                  var8 = (int)((long)var8 - (var21 - var1));
               }

               ((Buffer)var22).rewind();
               ((Buffer)var22).limit(var8);
               this.update(var22);
            }

            var19.close();
            var19 = null;
         } catch (IOException var17) {
            IOException var20 = var17;

            for (ChecksumCalculator.Checksum var6 : this.checksums) {
               var6.error(var20);
            }
         } finally {
            try {
               if (var19 != null) {
                  var19.close();
               }
            } catch (IOException var16) {
            }
         }
      }
   }

   public void update(ByteBuffer var1) {
      for (ChecksumCalculator.Checksum var3 : this.checksums) {
         ((Buffer)var1).mark();
         var3.update(var1);
         ((Buffer)var1).reset();
      }
   }

   public Map<String, Object> get() {
      HashMap var1 = new HashMap();

      for (ChecksumCalculator.Checksum var3 : this.checksums) {
         var1.put(var3.algorithm, var3.get());
      }

      return var1;
   }

   static class Checksum {
      final String algorithm;
      final MessageDigest digest;
      Exception error;

      Checksum(String var1) {
         this.algorithm = var1;
         MessageDigest var2 = null;

         try {
            var2 = MessageDigest.getInstance(var1);
         } catch (NoSuchAlgorithmException var4) {
            this.error = var4;
         }

         this.digest = var2;
      }

      public void update(ByteBuffer var1) {
         if (this.digest != null) {
            this.digest.update(var1);
         }
      }

      public void reset() {
         if (this.digest != null) {
            this.digest.reset();
            this.error = null;
         }
      }

      public void error(Exception var1) {
         if (this.digest != null) {
            this.error = var1;
         }
      }

      public Object get() {
         return this.error != null ? this.error : ChecksumUtils.toHexString(this.digest.digest());
      }
   }
}
