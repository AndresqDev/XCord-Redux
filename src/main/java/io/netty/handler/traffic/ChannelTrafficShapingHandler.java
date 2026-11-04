package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

public class ChannelTrafficShapingHandler extends AbstractTrafficShapingHandler {
   private final ArrayDeque<ChannelTrafficShapingHandler.ToSend> messagesQueue = new ArrayDeque<>();
   private long queueSize;

   public ChannelTrafficShapingHandler(long var1, long var3, long var5, long var7) {
      super(var1, var3, var5, var7);
   }

   public ChannelTrafficShapingHandler(long var1, long var3, long var5) {
      super(var1, var3, var5);
   }

   public ChannelTrafficShapingHandler(long var1, long var3) {
      super(var1, var3);
   }

   public ChannelTrafficShapingHandler(long var1) {
      super(var1);
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      TrafficCounter var2 = new TrafficCounter(this, var1.executor(), "ChannelTC" + var1.channel().hashCode(), this.checkInterval);
      this.setTrafficCounter(var2);
      var2.start();
      super.handlerAdded(var1);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.trafficCounter.stop();
      synchronized (this) {
         if (var1.channel().isActive()) {
            for (ChannelTrafficShapingHandler.ToSend var10 : this.messagesQueue) {
               long var5 = this.calculateSize(var10.toSend);
               this.trafficCounter.bytesRealWriteFlowControl(var5);
               this.queueSize -= var5;
               var1.write(var10.toSend, var10.promise);
            }
         } else {
            for (ChannelTrafficShapingHandler.ToSend var4 : this.messagesQueue) {
               if (var4.toSend instanceof ByteBuf) {
                  ((ByteBuf)var4.toSend).release();
               }
            }
         }

         this.messagesQueue.clear();
      }

      this.releaseWriteSuspended(var1);
      this.releaseReadSuspended(var1);
      super.handlerRemoved(var1);
   }

   @Override
   void submitWrite(final ChannelHandlerContext var1, Object var2, long var3, long var5, long var7, ChannelPromise var9) {
      ChannelTrafficShapingHandler.ToSend var10;
      synchronized (this) {
         if (var5 == 0L && this.messagesQueue.isEmpty()) {
            this.trafficCounter.bytesRealWriteFlowControl(var3);
            var1.write(var2, var9);
            return;
         }

         var10 = new ChannelTrafficShapingHandler.ToSend(var5 + var7, var2, var9);
         this.messagesQueue.addLast(var10);
         this.queueSize += var3;
         this.checkWriteSuspend(var1, var5, this.queueSize);
      }

      final long var11 = var10.relativeTimeAction;
      var1.executor().schedule(new Runnable() {
         @Override
         public void run() {
            ChannelTrafficShapingHandler.this.sendAllValid(var1, var11);
         }
      }, var5, TimeUnit.MILLISECONDS);
   }

   private void sendAllValid(ChannelHandlerContext var1, long var2) {
      synchronized (this) {
         for (ChannelTrafficShapingHandler.ToSend var5 = this.messagesQueue.pollFirst(); var5 != null; var5 = this.messagesQueue.pollFirst()) {
            if (var5.relativeTimeAction > var2) {
               this.messagesQueue.addFirst(var5);
               break;
            }

            long var6 = this.calculateSize(var5.toSend);
            this.trafficCounter.bytesRealWriteFlowControl(var6);
            this.queueSize -= var6;
            var1.write(var5.toSend, var5.promise);
         }

         if (this.messagesQueue.isEmpty()) {
            this.releaseWriteSuspended(var1);
         }
      }

      var1.flush();
   }

   public long queueSize() {
      return this.queueSize;
   }

   private static final class ToSend {
      final long relativeTimeAction;
      final Object toSend;
      final ChannelPromise promise;

      private ToSend(long var1, Object var3, ChannelPromise var4) {
         this.relativeTimeAction = var1;
         this.toSend = var3;
         this.promise = var4;
      }
   }
}
