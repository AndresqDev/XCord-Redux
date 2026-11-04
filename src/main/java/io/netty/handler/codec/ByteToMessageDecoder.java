package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ByteToMessageDecoder extends ChannelInboundHandlerAdapter {
   public static final ByteToMessageDecoder.Cumulator MERGE_CUMULATOR = new ByteToMessageDecoder.Cumulator() {
      @Override
      public ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
         if (!var2.isReadable() && var3.isContiguous()) {
            var2.release();
            return var3;
         } else {
            ByteBuf var5;
            try {
               int var4 = var3.readableBytes();
               if (var4 <= var2.maxWritableBytes() && (var4 <= var2.maxFastWritableBytes() || var2.refCnt() <= 1) && !var2.isReadOnly()) {
                  var2.writeBytes(var3, var3.readerIndex(), var4);
                  var3.readerIndex(var3.writerIndex());
                  return var2;
               }

               var5 = ByteToMessageDecoder.expandCumulation(var1, var2, var3);
            } finally {
               var3.release();
            }

            return var5;
         }
      }
   };
   public static final ByteToMessageDecoder.Cumulator COMPOSITE_CUMULATOR = new ByteToMessageDecoder.Cumulator() {
      @Override
      public ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
         if (!var2.isReadable()) {
            var2.release();
            return var3;
         } else {
            CompositeByteBuf var4 = null;

            CompositeByteBuf var5;
            try {
               if (var2 instanceof CompositeByteBuf && var2.refCnt() == 1) {
                  var4 = (CompositeByteBuf)var2;
                  if (var4.writerIndex() != var4.capacity()) {
                     var4.capacity(var4.writerIndex());
                  }
               } else {
                  var4 = var1.compositeBuffer(Integer.MAX_VALUE).addFlattenedComponents(true, var2);
               }

               var4.addFlattenedComponents(true, var3);
               var3 = null;
               var5 = var4;
            } finally {
               if (var3 != null) {
                  var3.release();
                  if (var4 != null && var4 != var2) {
                     var4.release();
                  }
               }
            }

            return var5;
         }
      }
   };
   private static final byte STATE_INIT = 0;
   private static final byte STATE_CALLING_CHILD_DECODE = 1;
   private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
   ByteBuf cumulation;
   private ByteToMessageDecoder.Cumulator cumulator = MERGE_CUMULATOR;
   private boolean singleDecode;
   private boolean first;
   private boolean firedChannelRead;
   private boolean selfFiredChannelRead;
   private byte decodeState = 0;
   private int discardAfterReads = 16;
   private int numReads;

   protected ByteToMessageDecoder() {
      this.ensureNotSharable();
   }

   public void setSingleDecode(boolean var1) {
      this.singleDecode = var1;
   }

   public boolean isSingleDecode() {
      return this.singleDecode;
   }

   public void setCumulator(ByteToMessageDecoder.Cumulator var1) {
      this.cumulator = ObjectUtil.checkNotNull(var1, "cumulator");
   }

   public void setDiscardAfterReads(int var1) {
      ObjectUtil.checkPositive(var1, "discardAfterReads");
      this.discardAfterReads = var1;
   }

   protected int actualReadableBytes() {
      return this.internalBuffer().readableBytes();
   }

   protected ByteBuf internalBuffer() {
      return this.cumulation != null ? this.cumulation : Unpooled.EMPTY_BUFFER;
   }

   @Override
   public final void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      if (this.decodeState == 1) {
         this.decodeState = 2;
      } else {
         ByteBuf var2 = this.cumulation;
         if (var2 != null) {
            this.cumulation = null;
            this.numReads = 0;
            int var3 = var2.readableBytes();
            if (var3 > 0) {
               var1.fireChannelRead(var2);
               var1.fireChannelReadComplete();
            } else {
               var2.release();
            }
         }

         this.handlerRemoved0(var1);
      }
   }

   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof ByteBuf) {
         this.selfFiredChannelRead = true;
         CodecOutputList var3 = CodecOutputList.newInstance();

         try {
            this.first = this.cumulation == null;
            this.cumulation = this.cumulator.cumulate(var1.alloc(), this.first ? Unpooled.EMPTY_BUFFER : this.cumulation, (ByteBuf)var2);
            this.callDecode(var1, this.cumulation, var3);
         } catch (DecoderException var31) {
            throw var31;
         } catch (Exception var32) {
            throw new DecoderException(var32);
         } finally {
            try {
               if (this.cumulation != null && !this.cumulation.isReadable()) {
                  this.numReads = 0;

                  try {
                     this.cumulation.release();
                  } catch (IllegalReferenceCountException var30) {
                     throw new IllegalReferenceCountException(
                        this.getClass().getSimpleName()
                           + "#decode() might have released its input buffer, or passed it down the pipeline without a retain() call, which is not allowed.",
                        var30
                     );
                  }

                  this.cumulation = null;
               } else if (++this.numReads >= this.discardAfterReads) {
                  this.numReads = 0;
                  this.discardSomeReadBytes();
               }

               int var7 = var3.size();
               this.firedChannelRead = this.firedChannelRead | var3.insertSinceRecycled();
               fireChannelRead(var1, var3, var7);
            } finally {
               var3.recycle();
            }
         }
      } else {
         var1.fireChannelRead(var2);
      }
   }

   static void fireChannelRead(ChannelHandlerContext var0, List<Object> var1, int var2) {
      if (var1 instanceof CodecOutputList) {
         fireChannelRead(var0, (CodecOutputList)var1, var2);
      } else {
         for (int var3 = 0; var3 < var2; var3++) {
            var0.fireChannelRead(var1.get(var3));
         }
      }
   }

   static void fireChannelRead(ChannelHandlerContext var0, CodecOutputList var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         var0.fireChannelRead(var1.getUnsafe(var3));
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.numReads = 0;
      this.discardSomeReadBytes();
      if (this.selfFiredChannelRead && !this.firedChannelRead && !var1.channel().config().isAutoRead()) {
         var1.read();
      }

      this.firedChannelRead = false;
      var1.fireChannelReadComplete();
   }

   protected final void discardSomeReadBytes() {
      if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
         this.cumulation.discardSomeReadBytes();
      }
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.channelInputClosed(var1, true);
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof ChannelInputShutdownEvent) {
         this.channelInputClosed(var1, false);
      }

      super.userEventTriggered(var1, var2);
   }

   private void channelInputClosed(ChannelHandlerContext var1, boolean var2) {
      CodecOutputList var3 = CodecOutputList.newInstance();

      try {
         this.channelInputClosed(var1, var3);
      } catch (DecoderException var24) {
         throw var24;
      } catch (Exception var25) {
         throw new DecoderException(var25);
      } finally {
         try {
            if (this.cumulation != null) {
               this.cumulation.release();
               this.cumulation = null;
            }

            int var7 = var3.size();
            fireChannelRead(var1, var3, var7);
            if (var7 > 0) {
               var1.fireChannelReadComplete();
            }

            if (var2) {
               var1.fireChannelInactive();
            }
         } finally {
            var3.recycle();
         }
      }
   }

   void channelInputClosed(ChannelHandlerContext var1, List<Object> var2) throws Exception {
      if (this.cumulation != null) {
         this.callDecode(var1, this.cumulation, var2);
         if (!var1.isRemoved()) {
            ByteBuf var3 = this.cumulation == null ? Unpooled.EMPTY_BUFFER : this.cumulation;
            this.decodeLast(var1, var3, var2);
         }
      } else {
         this.decodeLast(var1, Unpooled.EMPTY_BUFFER, var2);
      }
   }

   protected void callDecode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      try {
         while (var2.isReadable()) {
            int var4 = var3.size();
            if (var4 > 0) {
               fireChannelRead(var1, var3, var4);
               var3.clear();
               if (var1.isRemoved()) {
                  break;
               }
            }

            int var5 = var2.readableBytes();
            this.decodeRemovalReentryProtection(var1, var2, var3);
            if (!var1.isRemoved()) {
               if (var3.isEmpty()) {
                  if (var5 != var2.readableBytes()) {
                     continue;
                  }
               } else {
                  if (var5 == var2.readableBytes()) {
                     throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                  }

                  if (!this.isSingleDecode()) {
                     continue;
                  }
               }
            }
            break;
         }
      } catch (DecoderException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new DecoderException(var7);
      }
   }

   protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

   final void decodeRemovalReentryProtection(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      this.decodeState = 1;

      try {
         this.decode(var1, var2, var3);
      } finally {
         boolean var6 = this.decodeState == 2;
         this.decodeState = 0;
         if (var6) {
            fireChannelRead(var1, var3, var3.size());
            var3.clear();
            this.handlerRemoved(var1);
         }
      }
   }

   protected void decodeLast(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.isReadable()) {
         this.decodeRemovalReentryProtection(var1, var2, var3);
      }
   }

   static ByteBuf expandCumulation(ByteBufAllocator var0, ByteBuf var1, ByteBuf var2) {
      int var3 = var1.readableBytes();
      int var4 = var2.readableBytes();
      int var5 = var3 + var4;
      ByteBuf var6 = var0.buffer(var0.calculateNewCapacity(var5, Integer.MAX_VALUE));
      ByteBuf var7 = var6;

      ByteBuf var8;
      try {
         var6.setBytes(0, var1, var1.readerIndex(), var3).setBytes(var3, var2, var2.readerIndex(), var4).writerIndex(var5);
         var2.readerIndex(var2.writerIndex());
         var7 = var1;
         var8 = var6;
      } finally {
         var7.release();
      }

      return var8;
   }

   public interface Cumulator {
      ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);
   }
}
