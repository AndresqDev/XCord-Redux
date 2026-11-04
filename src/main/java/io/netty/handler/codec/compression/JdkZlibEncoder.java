package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class JdkZlibEncoder extends ZlibEncoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkZlibEncoder.class);
   private static final int MAX_INITIAL_OUTPUT_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.jdkzlib.encoder.maxInitialOutputBufferSize", 65536);
   private static final int MAX_INPUT_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.jdkzlib.encoder.maxInputBufferSize", 65536);
   private final ZlibWrapper wrapper;
   private final Deflater deflater;
   private volatile boolean finished;
   private volatile ChannelHandlerContext ctx;
   private final CRC32 crc = new CRC32();
   private static final byte[] gzipHeader = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0};
   private boolean writeHeader = true;
   private static final int THREAD_POOL_DELAY_SECONDS = 10;

   public JdkZlibEncoder() {
      this(6);
   }

   public JdkZlibEncoder(int var1) {
      this(ZlibWrapper.ZLIB, var1);
   }

   public JdkZlibEncoder(ZlibWrapper var1) {
      this(var1, 6);
   }

   public JdkZlibEncoder(ZlibWrapper var1, int var2) {
      ObjectUtil.checkInRange(var2, 0, 9, "compressionLevel");
      ObjectUtil.checkNotNull(var1, "wrapper");
      if (var1 == ZlibWrapper.ZLIB_OR_NONE) {
         throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not allowed for compression.");
      } else {
         this.wrapper = var1;
         this.deflater = new Deflater(var2, var1 != ZlibWrapper.ZLIB);
      }
   }

   public JdkZlibEncoder(byte[] var1) {
      this(6, var1);
   }

   public JdkZlibEncoder(int var1, byte[] var2) {
      ObjectUtil.checkInRange(var1, 0, 9, "compressionLevel");
      ObjectUtil.checkNotNull(var2, "dictionary");
      this.wrapper = ZlibWrapper.ZLIB;
      this.deflater = new Deflater(var1);
      this.deflater.setDictionary(var2);
   }

   @Override
   public ChannelFuture close() {
      return this.close(this.ctx().newPromise());
   }

   @Override
   public ChannelFuture close(final ChannelPromise var1) {
      ChannelHandlerContext var2 = this.ctx();
      EventExecutor var3 = var2.executor();
      if (var3.inEventLoop()) {
         return this.finishEncode(var2, var1);
      } else {
         final ChannelPromise var4 = var2.newPromise();
         var3.execute(new Runnable() {
            @Override
            public void run() {
               ChannelFuture var1x = JdkZlibEncoder.this.finishEncode(JdkZlibEncoder.this.ctx(), var4);
               PromiseNotifier.cascade(var1x, var1);
            }
         });
         return var4;
      }
   }

   private ChannelHandlerContext ctx() {
      ChannelHandlerContext var1 = this.ctx;
      if (var1 == null) {
         throw new IllegalStateException("not added to a pipeline");
      } else {
         return var1;
      }
   }

   @Override
   public boolean isClosed() {
      return this.finished;
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      if (this.finished) {
         var3.writeBytes(var2);
      } else {
         int var4 = var2.readableBytes();
         if (var4 != 0) {
            if (var2.hasArray()) {
               this.encodeSome(var2, var3);
            } else {
               int var5 = Math.min(var4, MAX_INPUT_BUFFER_SIZE);
               ByteBuf var6 = var1.alloc().heapBuffer(var5, var5);

               try {
                  while (var2.isReadable()) {
                     var2.readBytes(var6, Math.min(var6.writableBytes(), var2.readableBytes()));
                     this.encodeSome(var6, var3);
                     var6.clear();
                  }
               } finally {
                  var6.release();
               }
            }

            this.deflater.setInput(EmptyArrays.EMPTY_BYTES);
         }
      }
   }

   private void encodeSome(ByteBuf var1, ByteBuf var2) {
      byte[] var3 = var1.array();
      int var4 = var1.arrayOffset() + var1.readerIndex();
      if (this.writeHeader) {
         this.writeHeader = false;
         if (this.wrapper == ZlibWrapper.GZIP) {
            var2.writeBytes(gzipHeader);
         }
      }

      int var5 = var1.readableBytes();
      if (this.wrapper == ZlibWrapper.GZIP) {
         this.crc.update(var3, var4, var5);
      }

      this.deflater.setInput(var3, var4, var5);

      while (true) {
         this.deflate(var2);
         if (!var2.isWritable()) {
            var2.ensureWritable(var2.writerIndex());
         } else if (this.deflater.needsInput()) {
            var1.skipBytes(var5);
            return;
         }
      }
   }

   protected final ByteBuf allocateBuffer(ChannelHandlerContext var1, ByteBuf var2, boolean var3) throws Exception {
      int var4 = (int)Math.ceil((double)var2.readableBytes() * 1.001) + 12;
      if (this.writeHeader) {
         switch (this.wrapper) {
            case GZIP:
               var4 += gzipHeader.length;
               break;
            case ZLIB:
               var4 += 2;
         }
      }

      return var4 >= 0 && var4 <= MAX_INITIAL_OUTPUT_BUFFER_SIZE ? var1.alloc().heapBuffer(var4) : var1.alloc().heapBuffer(MAX_INITIAL_OUTPUT_BUFFER_SIZE);
   }

   @Override
   public void close(final ChannelHandlerContext var1, final ChannelPromise var2) throws Exception {
      ChannelFuture var3 = this.finishEncode(var1, var1.newPromise());
      var3.addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1x) throws Exception {
            var1.close(var2);
         }
      });
      if (!var3.isDone()) {
         var1.executor().schedule(new Runnable() {
            @Override
            public void run() {
               var1.close(var2);
            }
         }, 10L, TimeUnit.SECONDS);
      }
   }

   private ChannelFuture finishEncode(ChannelHandlerContext var1, ChannelPromise var2) {
      if (this.finished) {
         var2.setSuccess();
         return var2;
      } else {
         this.finished = true;
         ByteBuf var3 = var1.alloc().heapBuffer();
         if (this.writeHeader && this.wrapper == ZlibWrapper.GZIP) {
            this.writeHeader = false;
            var3.writeBytes(gzipHeader);
         }

         this.deflater.finish();

         while (!this.deflater.finished()) {
            this.deflate(var3);
            if (!var3.isWritable()) {
               var1.write(var3);
               var3 = var1.alloc().heapBuffer();
            }
         }

         if (this.wrapper == ZlibWrapper.GZIP) {
            int var4 = (int)this.crc.getValue();
            int var5 = this.deflater.getTotalIn();
            var3.writeByte(var4);
            var3.writeByte(var4 >>> 8);
            var3.writeByte(var4 >>> 16);
            var3.writeByte(var4 >>> 24);
            var3.writeByte(var5);
            var3.writeByte(var5 >>> 8);
            var3.writeByte(var5 >>> 16);
            var3.writeByte(var5 >>> 24);
         }

         this.deflater.end();
         return var1.writeAndFlush(var3, var2);
      }
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   private void deflate(ByteBuf var1) {
      if (PlatformDependent.javaVersion() < 7) {
         this.deflateJdk6(var1);
      }

      int var2;
      do {
         int var3 = var1.writerIndex();
         var2 = this.deflater.deflate(var1.array(), var1.arrayOffset() + var3, var1.writableBytes(), 2);
         var1.writerIndex(var3 + var2);
      } while (var2 > 0);
   }

   private void deflateJdk6(ByteBuf var1) {
      int var2;
      do {
         int var3 = var1.writerIndex();
         var2 = this.deflater.deflate(var1.array(), var1.arrayOffset() + var3, var1.writableBytes());
         var1.writerIndex(var3 + var2);
      } while (var2 > 0);
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   static {
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.jdkzlib.encoder.maxInitialOutputBufferSize={}", MAX_INITIAL_OUTPUT_BUFFER_SIZE);
         logger.debug("-Dio.netty.jdkzlib.encoder.maxInputBufferSize={}", MAX_INPUT_BUFFER_SIZE);
      }
   }
}
