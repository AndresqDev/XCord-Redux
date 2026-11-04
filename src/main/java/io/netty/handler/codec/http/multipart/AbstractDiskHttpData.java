package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractDiskHttpData extends AbstractHttpData {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
   private File file;
   private boolean isRenamed;
   private FileChannel fileChannel;

   protected AbstractDiskHttpData(String var1, Charset var2, long var3) {
      super(var1, var2, var3);
   }

   protected abstract String getDiskFilename();

   protected abstract String getPrefix();

   protected abstract String getBaseDirectory();

   protected abstract String getPostfix();

   protected abstract boolean deleteOnExit();

   private File tempFile() throws IOException {
      String var2 = this.getDiskFilename();
      String var1;
      if (var2 != null) {
         var1 = '_' + Integer.toString(var2.hashCode());
      } else {
         var1 = this.getPostfix();
      }

      File var3;
      if (this.getBaseDirectory() == null) {
         var3 = PlatformDependent.createTempFile(this.getPrefix(), var1, null);
      } else {
         var3 = PlatformDependent.createTempFile(this.getPrefix(), var1, new File(this.getBaseDirectory()));
      }

      if (this.deleteOnExit()) {
         DeleteFileOnExitHook.add(var3.getPath());
      }

      return var3;
   }

   @Override
   public void setContent(ByteBuf var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "buffer");

      try {
         this.size = (long)var1.readableBytes();
         this.checkSize(this.size);
         if (this.definedSize > 0L && this.definedSize < this.size) {
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
         }

         if (this.file == null) {
            this.file = this.tempFile();
         }

         if (var1.readableBytes() != 0) {
            RandomAccessFile var2 = new RandomAccessFile(this.file, "rw");

            try {
               var2.setLength(0L);
               FileChannel var3 = var2.getChannel();
               ByteBuffer var4 = var1.nioBuffer();
               int var5 = 0;

               while ((long)var5 < this.size) {
                  var5 += var3.write(var4);
               }

               var1.readerIndex(var1.readerIndex() + var5);
               var3.force(false);
            } finally {
               var2.close();
            }

            this.setCompleted();
            return;
         }

         if (this.file.createNewFile()) {
            return;
         }

         if (this.file.length() != 0L) {
            if (this.file.delete() && this.file.createNewFile()) {
               return;
            }

            throw new IOException("file exists already: " + this.file);
         }
      } finally {
         var1.release();
      }
   }

   @Override
   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      if (var1 != null) {
         try {
            int var3 = var1.readableBytes();
            this.checkSize(this.size + (long)var3);
            if (this.definedSize > 0L && this.definedSize < this.size + (long)var3) {
               throw new IOException("Out of size: " + (this.size + (long)var3) + " > " + this.definedSize);
            }

            if (this.file == null) {
               this.file = this.tempFile();
            }

            if (this.fileChannel == null) {
               RandomAccessFile var4 = new RandomAccessFile(this.file, "rw");
               this.fileChannel = var4.getChannel();
            }

            int var18 = var3;
            long var5 = this.fileChannel.position();
            int var7 = var1.readerIndex();

            while (var18 > 0) {
               int var8 = var1.getBytes(var7, this.fileChannel, var5, var18);
               if (var8 < 0) {
                  break;
               }

               var18 -= var8;
               var5 += (long)var8;
               var7 += var8;
            }

            this.fileChannel.position(var5);
            var1.readerIndex(var7);
            this.size += (long)(var3 - var18);
         } finally {
            var1.release();
         }
      }

      if (var2) {
         if (this.file == null) {
            this.file = this.tempFile();
         }

         if (this.fileChannel == null) {
            RandomAccessFile var17 = new RandomAccessFile(this.file, "rw");
            this.fileChannel = var17.getChannel();
         }

         try {
            this.fileChannel.force(false);
         } finally {
            this.fileChannel.close();
         }

         this.fileChannel = null;
         this.setCompleted();
      } else {
         ObjectUtil.checkNotNull(var1, "buffer");
      }
   }

   @Override
   public void setContent(File var1) throws IOException {
      long var2 = var1.length();
      this.checkSize(var2);
      this.size = var2;
      if (this.file != null) {
         this.delete();
      }

      this.file = var1;
      this.isRenamed = true;
      this.setCompleted();
   }

   @Override
   public void setContent(InputStream var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "inputStream");
      if (this.file != null) {
         this.delete();
      }

      this.file = this.tempFile();
      RandomAccessFile var2 = new RandomAccessFile(this.file, "rw");
      int var3 = 0;

      try {
         var2.setLength(0L);
         FileChannel var4 = var2.getChannel();
         byte[] var5 = new byte[16384];
         ByteBuffer var6 = ByteBuffer.wrap(var5);

         for (int var7 = var1.read(var5); var7 > 0; var7 = var1.read(var5)) {
            ((Buffer)var6).position(var7).flip();
            var3 += var4.write(var6);
            this.checkSize((long)var3);
         }

         var4.force(false);
      } finally {
         var2.close();
      }

      this.size = (long)var3;
      if (this.definedSize > 0L && this.definedSize < this.size) {
         if (!this.file.delete()) {
            logger.warn("Failed to delete: {}", this.file);
         }

         this.file = null;
         throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
      } else {
         this.isRenamed = true;
         this.setCompleted();
      }
   }

   @Override
   public void delete() {
      if (this.fileChannel != null) {
         try {
            this.fileChannel.force(false);
         } catch (IOException var10) {
            logger.warn("Failed to force.", (Throwable)var10);
         } finally {
            try {
               this.fileChannel.close();
            } catch (IOException var9) {
               logger.warn("Failed to close a file.", (Throwable)var9);
            }
         }

         this.fileChannel = null;
      }

      if (!this.isRenamed) {
         String var1 = null;
         if (this.file != null && this.file.exists()) {
            var1 = this.file.getPath();
            if (!this.file.delete()) {
               var1 = null;
               logger.warn("Failed to delete: {}", this.file);
            }
         }

         if (this.deleteOnExit() && var1 != null) {
            DeleteFileOnExitHook.remove(var1);
         }

         this.file = null;
      }
   }

   @Override
   public byte[] get() throws IOException {
      return this.file == null ? EmptyArrays.EMPTY_BYTES : readFrom(this.file);
   }

   @Override
   public ByteBuf getByteBuf() throws IOException {
      if (this.file == null) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         byte[] var1 = readFrom(this.file);
         return Unpooled.wrappedBuffer(var1);
      }
   }

   @Override
   public ByteBuf getChunk(int var1) throws IOException {
      if (this.file != null && var1 != 0) {
         if (this.fileChannel == null) {
            RandomAccessFile var2 = new RandomAccessFile(this.file, "r");
            this.fileChannel = var2.getChannel();
         }

         int var6 = 0;
         ByteBuffer var3 = ByteBuffer.allocate(var1);

         try {
            while (var6 < var1) {
               int var4 = this.fileChannel.read(var3);
               if (var4 == -1) {
                  this.fileChannel.close();
                  this.fileChannel = null;
                  break;
               }

               var6 += var4;
            }
         } catch (IOException var5) {
            this.fileChannel.close();
            this.fileChannel = null;
            throw var5;
         }

         if (var6 == 0) {
            return Unpooled.EMPTY_BUFFER;
         } else {
            ((Buffer)var3).flip();
            ByteBuf var7 = Unpooled.wrappedBuffer(var3);
            var7.readerIndex(0);
            var7.writerIndex(var6);
            return var7;
         }
      } else {
         return Unpooled.EMPTY_BUFFER;
      }
   }

   @Override
   public String getString() throws IOException {
      return this.getString(HttpConstants.DEFAULT_CHARSET);
   }

   @Override
   public String getString(Charset var1) throws IOException {
      if (this.file == null) {
         return "";
      } else if (var1 == null) {
         byte[] var3 = readFrom(this.file);
         return new String(var3, HttpConstants.DEFAULT_CHARSET.name());
      } else {
         byte[] var2 = readFrom(this.file);
         return new String(var2, var1.name());
      }
   }

   @Override
   public boolean isInMemory() {
      return false;
   }

   @Override
   public boolean renameTo(File var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "dest");
      if (this.file == null) {
         throw new IOException("No file defined so cannot be renamed");
      } else if (!this.file.renameTo(var1)) {
         IOException var2 = null;
         RandomAccessFile var3 = null;
         RandomAccessFile var4 = null;
         long var5 = 8196L;
         long var7 = 0L;

         try {
            var3 = new RandomAccessFile(this.file, "r");
            var4 = new RandomAccessFile(var1, "rw");
            FileChannel var9 = var3.getChannel();

            for (FileChannel var10 = var4.getChannel(); var7 < this.size; var7 += var9.transferTo(var7, var5, var10)) {
               if (var5 < this.size - var7) {
                  var5 = this.size - var7;
               }
            }
         } catch (IOException var23) {
            var2 = var23;
         } finally {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (IOException var22) {
                  if (var2 == null) {
                     var2 = var22;
                  } else {
                     logger.warn("Multiple exceptions detected, the following will be suppressed {}", (Throwable)var22);
                  }
               }
            }

            if (var4 != null) {
               try {
                  var4.close();
               } catch (IOException var21) {
                  if (var2 != null) {
                     logger.warn("Multiple exceptions detected, the following will be suppressed {}", (Throwable)var21);
                  }
               }
            }
         }

         if (var2 != null) {
            throw var2;
         } else if (var7 == this.size) {
            if (!this.file.delete()) {
               logger.warn("Failed to delete: {}", this.file);
            }

            this.file = var1;
            this.isRenamed = true;
            return true;
         } else {
            if (!var1.delete()) {
               logger.warn("Failed to delete: {}", var1);
            }

            return false;
         }
      } else {
         this.file = var1;
         this.isRenamed = true;
         return true;
      }
   }

   private static byte[] readFrom(File var0) throws IOException {
      long var1 = var0.length();
      if (var1 > 2147483647L) {
         throw new IllegalArgumentException("File too big to be loaded in memory");
      } else {
         RandomAccessFile var3 = new RandomAccessFile(var0, "r");
         byte[] var4 = new byte[(int)var1];

         try {
            FileChannel var5 = var3.getChannel();
            ByteBuffer var6 = ByteBuffer.wrap(var4);
            int var7 = 0;

            while ((long)var7 < var1) {
               var7 += var5.read(var6);
            }
         } finally {
            var3.close();
         }

         return var4;
      }
   }

   @Override
   public File getFile() throws IOException {
      return this.file;
   }

   @Override
   public HttpData touch() {
      return this;
   }

   @Override
   public HttpData touch(Object var1) {
      return this;
   }
}
