package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.decoder.DecoderJNI.Wrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;

public final class BrotliDecoder extends ByteToMessageDecoder {
   private final int inputBufferSize;
   private Wrapper decoder;
   private boolean destroyed;

   public BrotliDecoder() {
      this(8192);
   }

   public BrotliDecoder(int var1) {
      this.inputBufferSize = ObjectUtil.checkPositive(var1, "inputBufferSize");
   }

   private ByteBuf pull(ByteBufAllocator var1) {
      ByteBuffer var2 = this.decoder.pull();
      ByteBuf var3 = var1.buffer(var2.remaining());
      var3.writeBytes(var2);
      return var3;
   }

   private BrotliDecoder.State decompress(ByteBuf var1, List<Object> var2, ByteBufAllocator var3) {
      while (true) {
         switch (this.decoder.getStatus()) {
            case DONE:
               return BrotliDecoder.State.DONE;
            case OK:
               this.decoder.push(0);
               break;
            case NEEDS_MORE_INPUT:
               if (this.decoder.hasOutput()) {
                  var2.add(this.pull(var3));
               }

               if (!var1.isReadable()) {
                  return BrotliDecoder.State.NEEDS_MORE_INPUT;
               }

               ByteBuffer var4 = this.decoder.getInputBuffer();
               ((Buffer)var4).clear();
               int var5 = readBytes(var1, var4);
               this.decoder.push(var5);
               break;
            case NEEDS_MORE_OUTPUT:
               var2.add(this.pull(var3));
               break;
            default:
               return BrotliDecoder.State.ERROR;
         }
      }
   }

   private static int readBytes(ByteBuf var0, ByteBuffer var1) {
      int var2 = Math.min(var0.readableBytes(), var1.remaining());
      ByteBuffer var3 = var1.slice();
      ((Buffer)var3).limit(var2);
      var0.readBytes(var3);
      ((Buffer)var1).position(var1.position() + var2);
      return var2;
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.decoder = new Wrapper(this.inputBufferSize);
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.destroyed) {
         var2.skipBytes(var2.readableBytes());
      } else if (var2.isReadable()) {
         try {
            BrotliDecoder.State var4 = this.decompress(var2, var3, var1.alloc());
            if (var4 == BrotliDecoder.State.DONE) {
               this.destroy();
            } else if (var4 == BrotliDecoder.State.ERROR) {
               throw new DecompressionException("Brotli stream corrupted");
            }
         } catch (Exception var5) {
            this.destroy();
            throw var5;
         }
      }
   }

   private void destroy() {
      if (!this.destroyed) {
         this.destroyed = true;
         this.decoder.destroy();
      }
   }

   @Override
   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      try {
         this.destroy();
      } finally {
         super.handlerRemoved0(var1);
      }
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      try {
         this.destroy();
      } finally {
         super.channelInactive(var1);
      }
   }

   static {
      try {
         Brotli.ensureAvailability();
      } catch (Throwable var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }

   private static enum State {
      DONE,
      NEEDS_MORE_INPUT,
      ERROR;
   }
}
