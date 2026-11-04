package org.eclipse.aether.connector.basic;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicy;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.util.ChecksumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ChecksumValidator {
   private static final Logger LOGGER = LoggerFactory.getLogger(ChecksumValidator.class);
   private final File dataFile;
   private final Collection<File> tempFiles;
   private final FileProcessor fileProcessor;
   private final ChecksumValidator.ChecksumFetcher checksumFetcher;
   private final ChecksumPolicy checksumPolicy;
   private final Collection<RepositoryLayout.Checksum> checksums;
   private final Map<File, Object> checksumFiles;

   ChecksumValidator(File var1, FileProcessor var2, ChecksumValidator.ChecksumFetcher var3, ChecksumPolicy var4, Collection<RepositoryLayout.Checksum> var5) {
      this.dataFile = var1;
      this.tempFiles = new HashSet<>();
      this.fileProcessor = var2;
      this.checksumFetcher = var3;
      this.checksumPolicy = var4;
      this.checksums = var5;
      this.checksumFiles = new HashMap<>();
   }

   public ChecksumCalculator newChecksumCalculator(File var1) {
      return this.checksumPolicy != null ? ChecksumCalculator.newInstance(var1, this.checksums) : null;
   }

   public void validate(Map<String, ?> var1, Map<String, ?> var2) throws ChecksumFailureException {
      if (this.checksumPolicy != null) {
         if (var2 == null || !this.validateInlinedChecksums(var1, var2)) {
            if (!this.validateExternalChecksums(var1)) {
               this.checksumPolicy.onNoMoreChecksums();
            }
         }
      }
   }

   private boolean validateInlinedChecksums(Map<String, ?> var1, Map<String, ?> var2) throws ChecksumFailureException {
      for (Entry var4 : var2.entrySet()) {
         String var5 = (String)var4.getKey();
         Object var6 = var1.get(var5);
         if (var6 instanceof String) {
            String var7 = String.valueOf(var6);
            String var8 = var4.getValue().toString();
            this.checksumFiles.put(this.getChecksumFile(var5), var8);
            if (!isEqualChecksum(var8, var7)) {
               this.checksumPolicy.onChecksumMismatch(var5, 1, new ChecksumFailureException(var8, var7));
            } else if (this.checksumPolicy.onChecksumMatch(var5, 1)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean validateExternalChecksums(Map<String, ?> var1) throws ChecksumFailureException {
      for (RepositoryLayout.Checksum var3 : this.checksums) {
         String var4 = var3.getAlgorithm();
         Object var5 = var1.get(var4);
         if (var5 instanceof Exception) {
            this.checksumPolicy.onChecksumError(var4, 0, new ChecksumFailureException((Exception)var5));
         } else {
            try {
               File var6 = this.getChecksumFile(var3.getAlgorithm());
               File var7 = this.createTempFile(var6);

               try {
                  if (!this.checksumFetcher.fetchChecksum(var3.getLocation(), var7)) {
                     continue;
                  }
               } catch (Exception var10) {
                  this.checksumPolicy.onChecksumError(var4, 0, new ChecksumFailureException(var10));
                  continue;
               }

               String var8 = String.valueOf(var5);
               String var9 = ChecksumUtils.read(var7);
               this.checksumFiles.put(var6, var7);
               if (!isEqualChecksum(var9, var8)) {
                  this.checksumPolicy.onChecksumMismatch(var4, 0, new ChecksumFailureException(var9, var8));
               } else if (this.checksumPolicy.onChecksumMatch(var4, 0)) {
                  return true;
               }
            } catch (IOException var11) {
               this.checksumPolicy.onChecksumError(var4, 0, new ChecksumFailureException(var11));
            }
         }
      }

      return false;
   }

   private static boolean isEqualChecksum(String var0, String var1) {
      return var0.equalsIgnoreCase(var1);
   }

   private File getChecksumFile(String var1) {
      String var2 = var1.replace("-", "").toLowerCase(Locale.ENGLISH);
      return new File(this.dataFile.getPath() + '.' + var2);
   }

   private File createTempFile(File var1) throws IOException {
      File var2 = File.createTempFile(var1.getName() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8), ".tmp", var1.getParentFile());
      this.tempFiles.add(var2);
      return var2;
   }

   private void clearTempFiles() {
      for (File var2 : this.tempFiles) {
         if (!var2.delete() && var2.exists()) {
            LOGGER.debug("Could not delete temporary file {}", var2);
         }
      }

      this.tempFiles.clear();
   }

   public void retry() {
      this.checksumPolicy.onTransferRetry();
      this.checksumFiles.clear();
      this.clearTempFiles();
   }

   public boolean handle(ChecksumFailureException var1) {
      return this.checksumPolicy.onTransferChecksumFailure(var1);
   }

   public void commit() {
      for (Entry var2 : this.checksumFiles.entrySet()) {
         File var3 = (File)var2.getKey();
         Object var4 = var2.getValue();

         try {
            if (var4 instanceof File) {
               this.fileProcessor.move((File)var4, var3);
               this.tempFiles.remove(var4);
            } else {
               this.fileProcessor.write(var3, String.valueOf(var4));
            }
         } catch (IOException var6) {
            LOGGER.debug("Failed to write checksum file {}", var3, var6);
         }
      }

      this.checksumFiles.clear();
   }

   public void close() {
      this.clearTempFiles();
   }

   interface ChecksumFetcher {
      boolean fetchChecksum(URI var1, File var2) throws Exception;
   }
}
