package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public abstract class MessageAggregator<I, S, C extends ByteBufHolder, O extends ByteBufHolder> extends MessageToMessageDecoder<I> {
   private static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
   private final int maxContentLength;
   private O currentMessage;
   private boolean handlingOversizedMessage;
   private int maxCumulationBufferComponents = 1024;
   private ChannelHandlerContext ctx;
   private ChannelFutureListener continueResponseWriteListener;
   private boolean aggregating;

   protected MessageAggregator(int var1) {
      validateMaxContentLength(var1);
      this.maxContentLength = var1;
   }

   protected MessageAggregator(int var1, Class<? extends I> var2) {
      super(var2);
      validateMaxContentLength(var1);
      this.maxContentLength = var1;
   }

   private static void validateMaxContentLength(int var0) {
      ObjectUtil.checkPositiveOrZero(var0, "maxContentLength");
   }

   @Override
   public boolean acceptInboundMessage(Object var1) throws Exception {
      if (!super.acceptInboundMessage(var1)) {
         return false;
      } else if (this.isAggregated((I)var1)) {
         return false;
      } else if (this.isStartMessage((I)var1)) {
         this.aggregating = true;
         return true;
      } else {
         return this.aggregating && this.isContentMessage((I)var1);
      }
   }

   protected abstract boolean isStartMessage(I var1) throws Exception;

   protected abstract boolean isContentMessage(I var1) throws Exception;

   protected abstract boolean isLastContentMessage(C var1) throws Exception;

   protected abstract boolean isAggregated(I var1) throws Exception;

   public final int maxContentLength() {
      return this.maxContentLength;
   }

   public final int maxCumulationBufferComponents() {
      return this.maxCumulationBufferComponents;
   }

   public final void setMaxCumulationBufferComponents(int var1) {
      if (var1 < 2) {
         throw new IllegalArgumentException("maxCumulationBufferComponents: " + var1 + " (expected: >= 2)");
      } else if (this.ctx == null) {
         this.maxCumulationBufferComponents = var1;
      } else {
         throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
      }
   }

   @Deprecated
   public final boolean isHandlingOversizedMessage() {
      return this.handlingOversizedMessage;
   }

   protected final ChannelHandlerContext ctx() {
      if (this.ctx == null) {
         throw new IllegalStateException("not added to a pipeline yet");
      } else {
         return this.ctx;
      }
   }

   @Override
   protected void decode(final ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception {
      assert this.aggregating;

      if (this.isStartMessage((I)var2)) {
         this.handlingOversizedMessage = false;
         if (this.currentMessage != null) {
            this.currentMessage.release();
            this.currentMessage = null;
            throw new MessageAggregationException();
         }

         Object var5 = this.newContinueResponse((S)var2, this.maxContentLength, var1.pipeline());
         if (var5 != null) {
            ChannelFutureListener var6 = this.continueResponseWriteListener;
            if (var6 == null) {
               this.continueResponseWriteListener = var6 = new ChannelFutureListener() {
                  public void operationComplete(ChannelFuture var1x) throws Exception {
                     if (!var1x.isSuccess()) {
                        var1.fireExceptionCaught(var1x.cause());
                     }
                  }
               };
            }

            boolean var7 = this.closeAfterContinueResponse(var5);
            this.handlingOversizedMessage = this.ignoreContentAfterContinueResponse(var5);
            ChannelFuture var8 = var1.writeAndFlush(var5).addListener(var6);
            if (var7) {
               var8.addListener(ChannelFutureListener.CLOSE);
               return;
            }

            if (this.handlingOversizedMessage) {
               return;
            }
         } else if (this.isContentLengthInvalid((S)var2, this.maxContentLength)) {
            this.invokeHandleOversizedMessage(var1, (S)var2);
            return;
         }

         if (var2 instanceof DecoderResultProvider && !((DecoderResultProvider)var2).decoderResult().isSuccess()) {
            ByteBufHolder var11;
            if (var2 instanceof ByteBufHolder) {
               var11 = this.beginAggregation((S)var2, ((ByteBufHolder)var2).content().retain());
            } else {
               var11 = this.beginAggregation((S)var2, Unpooled.EMPTY_BUFFER);
            }

            this.finishAggregation0((O)var11);
            var3.add(var11);
            return;
         }

         CompositeByteBuf var10 = var1.alloc().compositeBuffer(this.maxCumulationBufferComponents);
         if (var2 instanceof ByteBufHolder) {
            appendPartialContent(var10, ((ByteBufHolder)var2).content());
         }

         this.currentMessage = this.beginAggregation((S)var2, var10);
      } else {
         if (!this.isContentMessage((I)var2)) {
            throw new MessageAggregationException();
         }

         if (this.currentMessage == null) {
            return;
         }

         CompositeByteBuf var4 = (CompositeByteBuf)this.currentMessage.content();
         ByteBufHolder var9 = (ByteBufHolder)var2;
         if (var4.readableBytes() > this.maxContentLength - var9.content().readableBytes()) {
            ByteBufHolder var13 = this.currentMessage;
            this.invokeHandleOversizedMessage(var1, (S)var13);
            return;
         }

         appendPartialContent(var4, var9.content());
         this.aggregate(this.currentMessage, (C)var9);
         boolean var12;
         if (var9 instanceof DecoderResultProvider) {
            DecoderResult var14 = ((DecoderResultProvider)var9).decoderResult();
            if (!var14.isSuccess()) {
               if (this.currentMessage instanceof DecoderResultProvider) {
                  ((DecoderResultProvider)this.currentMessage).setDecoderResult(DecoderResult.failure(var14.cause()));
               }

               var12 = true;
            } else {
               var12 = this.isLastContentMessage((C)var9);
            }
         } else {
            var12 = this.isLastContentMessage((C)var9);
         }

         if (var12) {
            this.finishAggregation0(this.currentMessage);
            var3.add(this.currentMessage);
            this.currentMessage = null;
         }
      }
   }

   private static void appendPartialContent(CompositeByteBuf var0, ByteBuf var1) {
      if (var1.isReadable()) {
         var0.addComponent(true, var1.retain());
      }
   }

   protected abstract boolean isContentLengthInvalid(S var1, int var2) throws Exception;

   protected abstract Object newContinueResponse(S var1, int var2, ChannelPipeline var3) throws Exception;

   protected abstract boolean closeAfterContinueResponse(Object var1) throws Exception;

   protected abstract boolean ignoreContentAfterContinueResponse(Object var1) throws Exception;

   protected abstract O beginAggregation(S var1, ByteBuf var2) throws Exception;

   protected void aggregate(O var1, C var2) throws Exception {
   }

   private void finishAggregation0(O var1) throws Exception {
      this.aggregating = false;
      this.finishAggregation((O)var1);
   }

   protected void finishAggregation(O var1) throws Exception {
   }

   private void invokeHandleOversizedMessage(ChannelHandlerContext var1, S var2) throws Exception {
      this.handlingOversizedMessage = true;
      this.currentMessage = null;

      try {
         this.handleOversizedMessage(var1, (S)var2);
      } finally {
         ReferenceCountUtil.release(var2);
      }
   }

   protected void handleOversizedMessage(ChannelHandlerContext var1, S var2) throws Exception {
      var1.fireExceptionCaught(new TooLongFrameException("content length exceeded " + this.maxContentLength() + " bytes."));
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if (this.currentMessage != null && !var1.channel().config().isAutoRead()) {
         var1.read();
      }

      var1.fireChannelReadComplete();
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      try {
         super.channelInactive(var1);
      } finally {
         this.releaseCurrentMessage();
      }
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      try {
         super.handlerRemoved(var1);
      } finally {
         this.releaseCurrentMessage();
      }
   }

   private void releaseCurrentMessage() {
      if (this.currentMessage != null) {
         this.currentMessage.release();
         this.currentMessage = null;
         this.handlingOversizedMessage = false;
         this.aggregating = false;
      }
   }
}
