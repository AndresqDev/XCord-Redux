package io.netty.channel;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class DefaultFileRegion extends AbstractReferenceCounted implements FileRegion {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
   private final File f;
   private final long position;
   private final long count;
   private long transferred;
   private FileChannel file;

   public DefaultFileRegion(FileChannel var1, long var2, long var4) {
      this.file = ObjectUtil.checkNotNull(var1, "file");
      this.position = ObjectUtil.checkPositiveOrZero(var2, "position");
      this.count = ObjectUtil.checkPositiveOrZero(var4, "count");
      this.f = null;
   }

   public DefaultFileRegion(File var1, long var2, long var4) {
      this.f = ObjectUtil.checkNotNull(var1, "f");
      this.position = ObjectUtil.checkPositiveOrZero(var2, "position");
      this.count = ObjectUtil.checkPositiveOrZero(var4, "count");
   }

   public boolean isOpen() {
      return this.file != null;
   }

   public void open() throws IOException {
      if (!this.isOpen() && this.refCnt() > 0) {
         this.file = new RandomAccessFile(this.f, "r").getChannel();
      }
   }

   @Override
   public long position() {
      return this.position;
   }

   @Override
   public long count() {
      return this.count;
   }

   @Deprecated
   @Override
   public long transfered() {
      return this.transferred;
   }

   @Override
   public long transferred() {
      return this.transferred;
   }

   @Override
   public long transferTo(WritableByteChannel var1, long var2) throws IOException {
      long var4 = this.count - var2;
      if (var4 < 0L || var2 < 0L) {
         throw new IllegalArgumentException("position out of range: " + var2 + " (expected: 0 - " + (this.count - 1L) + ')');
      } else if (var4 == 0L) {
         return 0L;
      } else if (this.refCnt() == 0) {
         throw new IllegalReferenceCountException(0);
      } else {
         this.open();
         long var6 = this.file.transferTo(this.position + var2, var4, var1);
         if (var6 > 0L) {
            this.transferred += var6;
         } else if (var6 == 0L) {
            validate(this, var2);
         }

         return var6;
      }
   }

   @Override
   protected void deallocate() {
      FileChannel var1 = this.file;
      if (var1 != null) {
         this.file = null;

         try {
            var1.close();
         } catch (IOException var3) {
            logger.warn("Failed to close a file.", (Throwable)var3);
         }
      }
   }

   @Override
   public FileRegion retain() {
      super.retain();
      return this;
   }

   @Override
   public FileRegion retain(int var1) {
      super.retain(var1);
      return this;
   }

   @Override
   public FileRegion touch() {
      return this;
   }

   @Override
   public FileRegion touch(Object var1) {
      return this;
   }

   static void validate(DefaultFileRegion var0, long var1) throws IOException {
      long var3 = var0.file.size();
      long var5 = var0.count - var1;
      if (var0.position + var5 + var1 > var3) {
         throw new IOException("Underlying file size " + var3 + " smaller then requested count " + var0.count);
      }
   }
}
