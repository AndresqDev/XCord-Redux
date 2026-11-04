package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractMemoryHttpData extends AbstractHttpData {
   private ByteBuf byteBuf = Unpooled.EMPTY_BUFFER;
   private int chunkPosition;

   protected AbstractMemoryHttpData(String var1, Charset var2, long var3) {
      super(var1, var2, var3);
   }

   @Override
   public void setContent(ByteBuf var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "buffer");
      long var2 = (long)var1.readableBytes();

      try {
         this.checkSize(var2);
      } catch (IOException var5) {
         var1.release();
         throw var5;
      }

      if (this.definedSize > 0L && this.definedSize < var2) {
         var1.release();
         throw new IOException("Out of size: " + var2 + " > " + this.definedSize);
      } else {
         if (this.byteBuf != null) {
            this.byteBuf.release();
         }

         this.byteBuf = var1;
         this.size = var2;
         this.setCompleted();
      }
   }

   @Override
   public void setContent(InputStream var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "inputStream");
      byte[] var2 = new byte[16384];
      ByteBuf var3 = Unpooled.buffer();
      int var4 = 0;

      try {
         for (int var5 = var1.read(var2); var5 > 0; var5 = var1.read(var2)) {
            var3.writeBytes(var2, 0, var5);
            var4 += var5;
            this.checkSize((long)var4);
         }
      } catch (IOException var6) {
         var3.release();
         throw var6;
      }

      this.size = (long)var4;
      if (this.definedSize > 0L && this.definedSize < this.size) {
         var3.release();
         throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
      } else {
         if (this.byteBuf != null) {
            this.byteBuf.release();
         }

         this.byteBuf = var3;
         this.setCompleted();
      }
   }

   @Override
   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      if (var1 != null) {
         long var3 = (long)var1.readableBytes();

         try {
            this.checkSize(this.size + var3);
         } catch (IOException var6) {
            var1.release();
            throw var6;
         }

         if (this.definedSize > 0L && this.definedSize < this.size + var3) {
            var1.release();
            throw new IOException("Out of size: " + (this.size + var3) + " > " + this.definedSize);
         }

         this.size += var3;
         if (this.byteBuf == null) {
            this.byteBuf = var1;
         } else if (var3 == 0L) {
            var1.release();
         } else if (this.byteBuf.readableBytes() == 0) {
            this.byteBuf.release();
            this.byteBuf = var1;
         } else if (this.byteBuf instanceof CompositeByteBuf) {
            CompositeByteBuf var5 = (CompositeByteBuf)this.byteBuf;
            var5.addComponent(true, var1);
         } else {
            CompositeByteBuf var7 = Unpooled.compositeBuffer(Integer.MAX_VALUE);
            var7.addComponents(true, this.byteBuf, var1);
            this.byteBuf = var7;
         }
      }

      if (var2) {
         this.setCompleted();
      } else {
         ObjectUtil.checkNotNull(var1, "buffer");
      }
   }

   @Override
   public void setContent(File var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "file");
      long var2 = var1.length();
      if (var2 > 2147483647L) {
         throw new IllegalArgumentException("File too big to be loaded in memory");
      } else {
         this.checkSize(var2);
         RandomAccessFile var4 = new RandomAccessFile(var1, "r");

         ByteBuffer var5;
         try {
            FileChannel var6 = var4.getChannel();

            try {
               byte[] var7 = new byte[(int)var2];
               var5 = ByteBuffer.wrap(var7);
               int var8 = 0;

               while ((long)var8 < var2) {
                  var8 += var6.read(var5);
               }
            } finally {
               var6.close();
            }
         } finally {
            var4.close();
         }

         ((Buffer)var5).flip();
         if (this.byteBuf != null) {
            this.byteBuf.release();
         }

         this.byteBuf = Unpooled.wrappedBuffer(Integer.MAX_VALUE, var5);
         this.size = var2;
         this.setCompleted();
      }
   }

   @Override
   public void delete() {
      if (this.byteBuf != null) {
         this.byteBuf.release();
         this.byteBuf = null;
      }
   }

   @Override
   public byte[] get() {
      if (this.byteBuf == null) {
         return Unpooled.EMPTY_BUFFER.array();
      } else {
         byte[] var1 = new byte[this.byteBuf.readableBytes()];
         this.byteBuf.getBytes(this.byteBuf.readerIndex(), var1);
         return var1;
      }
   }

   @Override
   public String getString() {
      return this.getString(HttpConstants.DEFAULT_CHARSET);
   }

   @Override
   public String getString(Charset var1) {
      if (this.byteBuf == null) {
         return "";
      } else {
         if (var1 == null) {
            var1 = HttpConstants.DEFAULT_CHARSET;
         }

         return this.byteBuf.toString(var1);
      }
   }

   @Override
   public ByteBuf getByteBuf() {
      return this.byteBuf;
   }

   @Override
   public ByteBuf getChunk(int var1) throws IOException {
      if (this.byteBuf != null && var1 != 0 && this.byteBuf.readableBytes() != 0) {
         int var2 = this.byteBuf.readableBytes() - this.chunkPosition;
         if (var2 == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
         } else {
            int var3 = var1;
            if (var2 < var1) {
               var3 = var2;
            }

            ByteBuf var4 = this.byteBuf.retainedSlice(this.chunkPosition, var3);
            this.chunkPosition += var3;
            return var4;
         }
      } else {
         this.chunkPosition = 0;
         return Unpooled.EMPTY_BUFFER;
      }
   }

   @Override
   public boolean isInMemory() {
      return true;
   }

   @Override
   public boolean renameTo(File var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "dest");
      if (this.byteBuf == null) {
         if (!var1.createNewFile()) {
            throw new IOException("file exists already: " + var1);
         } else {
            return true;
         }
      } else {
         int var2 = this.byteBuf.readableBytes();
         long var3 = 0L;
         RandomAccessFile var5 = new RandomAccessFile(var1, "rw");

         try {
            FileChannel var6 = var5.getChannel();

            try {
               if (this.byteBuf.nioBufferCount() == 1) {
                  ByteBuffer var16 = this.byteBuf.nioBuffer();

                  while (var3 < (long)var2) {
                     var3 += (long)var6.write(var16);
                  }
               } else {
                  ByteBuffer[] var7 = this.byteBuf.nioBuffers();

                  while (var3 < (long)var2) {
                     var3 += var6.write(var7);
                  }
               }

               var6.force(false);
            } finally {
               var6.close();
            }
         } finally {
            var5.close();
         }

         return var3 == (long)var2;
      }
   }

   @Override
   public File getFile() throws IOException {
      throw new IOException("Not represented by a file");
   }

   @Override
   public HttpData touch() {
      return this.touch(null);
   }

   @Override
   public HttpData touch(Object var1) {
      if (this.byteBuf != null) {
         this.byteBuf.touch(var1);
      }

      return this;
   }
}
