package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class FileBackedOutputStream extends OutputStream {
   private final int fileThreshold;
   private final boolean resetOnFinalize;
   private final ByteSource source;
   private final File parentDirectory;
   @GuardedBy("this")
   private OutputStream out;
   @GuardedBy("this")
   private FileBackedOutputStream.MemoryOutput memory;
   @GuardedBy("this")
   private File file;

   @VisibleForTesting
   synchronized File getFile() {
      return this.file;
   }

   public FileBackedOutputStream(int var1) {
      this(var1, false);
   }

   public FileBackedOutputStream(int var1, boolean var2) {
      this(var1, var2, null);
   }

   private FileBackedOutputStream(int var1, boolean var2, File var3) {
      this.fileThreshold = var1;
      this.resetOnFinalize = var2;
      this.parentDirectory = var3;
      this.memory = new FileBackedOutputStream.MemoryOutput();
      this.out = this.memory;
      if (var2) {
         this.source = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
               return FileBackedOutputStream.this.openInputStream();
            }

            @Override
            protected void finalize() {
               try {
                  FileBackedOutputStream.this.reset();
               } catch (Throwable var2) {
                  var2.printStackTrace(System.err);
               }
            }
         };
      } else {
         this.source = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
               return FileBackedOutputStream.this.openInputStream();
            }
         };
      }
   }

   public ByteSource asByteSource() {
      return this.source;
   }

   private synchronized InputStream openInputStream() throws IOException {
      if (this.file != null) {
         return new FileInputStream(this.file);
      } else {
         Objects.requireNonNull(this.memory);
         return new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount());
      }
   }

   public synchronized void reset() throws IOException {
      try {
         this.close();
      } finally {
         if (this.memory == null) {
            this.memory = new FileBackedOutputStream.MemoryOutput();
         } else {
            this.memory.reset();
         }

         this.out = this.memory;
         if (this.file != null) {
            File var4 = this.file;
            this.file = null;
            if (!var4.delete()) {
               String var5 = String.valueOf(var4);
               throw new IOException(new StringBuilder(18 + String.valueOf(var5).length()).append("Could not delete: ").append(var5).toString());
            }
         }
      }
   }

   @Override
   public synchronized void write(int var1) throws IOException {
      this.update(1);
      this.out.write(var1);
   }

   @Override
   public synchronized void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   @Override
   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      this.update(var3);
      this.out.write(var1, var2, var3);
   }

   @Override
   public synchronized void close() throws IOException {
      this.out.close();
   }

   @Override
   public synchronized void flush() throws IOException {
      this.out.flush();
   }

   @GuardedBy("this")
   private void update(int var1) throws IOException {
      if (this.memory != null && this.memory.getCount() + var1 > this.fileThreshold) {
         File var2 = File.createTempFile("FileBackedOutputStream", null, this.parentDirectory);
         if (this.resetOnFinalize) {
            var2.deleteOnExit();
         }

         try {
            FileOutputStream var3 = new FileOutputStream(var2);
            var3.write(this.memory.getBuffer(), 0, this.memory.getCount());
            var3.flush();
            this.out = var3;
         } catch (IOException var4) {
            var2.delete();
            throw var4;
         }

         this.file = var2;
         this.memory = null;
      }
   }

   private static class MemoryOutput extends ByteArrayOutputStream {
      private MemoryOutput() {
      }

      byte[] getBuffer() {
         return this.buf;
      }

      int getCount() {
         return this.count;
      }
   }
}
