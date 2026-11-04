package io.netty.handler.flush;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.Future;

public class FlushConsolidationHandler extends ChannelDuplexHandler {
   private final int explicitFlushAfterFlushes;
   private final boolean consolidateWhenNoReadInProgress;
   private final Runnable flushTask;
   private int flushPendingCount;
   private boolean readInProgress;
   private ChannelHandlerContext ctx;
   private Future<?> nextScheduledFlush;
   public static final int DEFAULT_EXPLICIT_FLUSH_AFTER_FLUSHES = 256;

   public FlushConsolidationHandler() {
      this(256, false);
   }

   public FlushConsolidationHandler(int var1) {
      this(var1, false);
   }

   public FlushConsolidationHandler(int var1, boolean var2) {
      this.explicitFlushAfterFlushes = ObjectUtil.checkPositive(var1, "explicitFlushAfterFlushes");
      this.consolidateWhenNoReadInProgress = var2;
      this.flushTask = var2 ? new Runnable() {
         @Override
         public void run() {
            if (FlushConsolidationHandler.this.flushPendingCount > 0 && !FlushConsolidationHandler.this.readInProgress) {
               FlushConsolidationHandler.this.flushPendingCount = 0;
               FlushConsolidationHandler.this.nextScheduledFlush = null;
               FlushConsolidationHandler.this.ctx.flush();
            }
         }
      } : null;
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   @Override
   public void flush(ChannelHandlerContext var1) throws Exception {
      if (this.readInProgress) {
         if (++this.flushPendingCount == this.explicitFlushAfterFlushes) {
            this.flushNow(var1);
         }
      } else if (this.consolidateWhenNoReadInProgress) {
         if (++this.flushPendingCount == this.explicitFlushAfterFlushes) {
            this.flushNow(var1);
         } else {
            this.scheduleFlush(var1);
         }
      } else {
         this.flushNow(var1);
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.fireChannelReadComplete();
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      this.readInProgress = true;
      var1.fireChannelRead(var2);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.fireExceptionCaught(var2);
   }

   @Override
   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.disconnect(var2);
   }

   @Override
   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.close(var2);
   }

   @Override
   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      if (!var1.channel().isWritable()) {
         this.flushIfNeeded(var1);
      }

      var1.fireChannelWritabilityChanged();
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.flushIfNeeded(var1);
   }

   private void resetReadAndFlushIfNeeded(ChannelHandlerContext var1) {
      this.readInProgress = false;
      this.flushIfNeeded(var1);
   }

   private void flushIfNeeded(ChannelHandlerContext var1) {
      if (this.flushPendingCount > 0) {
         this.flushNow(var1);
      }
   }

   private void flushNow(ChannelHandlerContext var1) {
      this.cancelScheduledFlush();
      this.flushPendingCount = 0;
      var1.flush();
   }

   private void scheduleFlush(ChannelHandlerContext var1) {
      if (this.nextScheduledFlush == null) {
         this.nextScheduledFlush = var1.channel().eventLoop().submit(this.flushTask);
      }
   }

   private void cancelScheduledFlush() {
      if (this.nextScheduledFlush != null) {
         this.nextScheduledFlush.cancel(false);
         this.nextScheduledFlush = null;
      }
   }
}
