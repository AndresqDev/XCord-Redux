package org.eclipse.aether.connector.basic;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PartialFile implements Closeable {
   static final String EXT_PART = ".part";
   static final String EXT_LOCK = ".lock";
   private final File partFile;
   private final PartialFile.LockFile lockFile;
   private final long threshold;
   private static final Logger LOGGER = LoggerFactory.getLogger(PartialFile.class);

   private PartialFile(File var1) {
      this(var1, null, 0L);
   }

   private PartialFile(File var1, PartialFile.LockFile var2, long var3) {
      this.partFile = var1;
      this.lockFile = var2;
      this.threshold = var3;
   }

   public File getFile() {
      return this.partFile;
   }

   public boolean isResume() {
      return this.lockFile != null && this.partFile.length() >= this.threshold;
   }

   @Override
   public void close() throws IOException {
      if (this.partFile.exists() && !this.isResume() && !this.partFile.delete() && this.partFile.exists()) {
         LOGGER.debug("Could not delete temporary file {}", this.partFile);
      }

      if (this.lockFile != null) {
         this.lockFile.close();
      }
   }

   @Override
   public String toString() {
      return String.valueOf(this.getFile());
   }

   static class Factory {
      private final boolean resume;
      private final long resumeThreshold;
      private final int requestTimeout;
      private static final Logger LOGGER = LoggerFactory.getLogger(PartialFile.Factory.class);

      Factory(boolean var1, long var2, int var4) {
         this.resume = var1;
         this.resumeThreshold = var2;
         this.requestTimeout = var4;
      }

      public PartialFile newInstance(File var1, PartialFile.RemoteAccessChecker var2) throws Exception {
         if (this.resume) {
            File var3 = new File(var1.getPath() + ".part");
            long var4 = System.currentTimeMillis();
            PartialFile.LockFile var6 = new PartialFile.LockFile(var3, this.requestTimeout, var2);
            if (var6.isConcurrent() && var1.lastModified() >= var4 - 100L) {
               var6.close();
               return null;
            }

            try {
               if (!var3.createNewFile() && !var3.isFile()) {
                  throw new IOException(var3.exists() ? "Path exists but is not a file" : "Unknown error");
               }

               return new PartialFile(var3, var6, this.resumeThreshold);
            } catch (IOException var8) {
               var6.close();
               LOGGER.debug("Cannot create resumable file {}", var3.getAbsolutePath(), var8);
            }
         }

         File var9 = File.createTempFile(var1.getName() + '-' + UUID.randomUUID().toString().replace("-", ""), ".tmp", var1.getParentFile());
         return new PartialFile(var9);
      }
   }

   static class LockFile {
      private final File lockFile;
      private final FileLock lock;
      private final AtomicBoolean concurrent;

      LockFile(File var1, int var2, PartialFile.RemoteAccessChecker var3) throws Exception {
         this.lockFile = new File(var1.getPath() + ".lock");
         this.concurrent = new AtomicBoolean(false);
         this.lock = lock(this.lockFile, var1, var2, var3, this.concurrent);
      }

      private static FileLock lock(File var0, File var1, int var2, PartialFile.RemoteAccessChecker var3, AtomicBoolean var4) throws Exception {
         boolean var5 = false;

         try {
            long var6 = -1L;
            long var8 = 0L;

            while (true) {
               FileLock var10 = tryLock(var0);
               if (var10 != null) {
                  return var10;
               }

               long var11 = var1.length();
               long var13 = System.currentTimeMillis();
               if (var11 != var6) {
                  if (var6 < 0L) {
                     var4.set(true);
                     var3.checkRemoteAccess();
                     PartialFile.LOGGER.debug("Concurrent download of {} in progress, awaiting completion", var1);
                  }

                  var6 = var11;
                  var8 = var13;
               } else if (var2 > 0 && var13 - var8 > (long)Math.max(var2, 3000)) {
                  throw new IOException("Timeout while waiting for concurrent download of " + var1 + " to progress");
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var19) {
                  var5 = true;
               }
            }
         } finally {
            if (var5) {
               Thread.currentThread().interrupt();
            }
         }
      }

      private static FileLock tryLock(File var0) throws IOException {
         RandomAccessFile var1 = null;
         FileLock var2 = null;

         try {
            var1 = new RandomAccessFile(var0, "rw");
            var2 = var1.getChannel().tryLock(0L, 1L, false);
            if (var2 == null) {
               var1.close();
               var1 = null;
            }
         } catch (OverlappingFileLockException var13) {
            close(var1);
            var1 = null;
            var2 = null;
         } catch (IOException | RuntimeException var14) {
            close(var1);
            var1 = null;
            if (!var0.delete()) {
               var0.deleteOnExit();
            }

            throw var14;
         } finally {
            try {
               if (var2 == null && var1 != null) {
                  var1.close();
               }
            } catch (IOException var12) {
            }
         }

         return var2;
      }

      private static void close(Closeable var0) {
         try {
            if (var0 != null) {
               var0.close();
            }
         } catch (IOException var2) {
         }
      }

      public boolean isConcurrent() {
         return this.concurrent.get();
      }

      public void close() throws IOException {
         FileChannel var1 = null;

         try {
            var1 = this.lock.channel();
            this.lock.release();
            var1.close();
            var1 = null;
         } finally {
            try {
               if (var1 != null) {
                  var1.close();
               }
            } catch (IOException var22) {
            } finally {
               if (!this.lockFile.delete()) {
                  this.lockFile.deleteOnExit();
               }
            }
         }
      }

      @Override
      public String toString() {
         return this.lockFile + " - " + this.lock.isValid();
      }
   }

   interface RemoteAccessChecker {
      void checkRemoteAccess() throws Exception;
   }
}
