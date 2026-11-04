package io.netty.handler.flow;

import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;

public class FlowControlHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(FlowControlHandler.class);
   private final boolean releaseMessages;
   private FlowControlHandler.RecyclableArrayDeque queue;
   private ChannelConfig config;
   private boolean shouldConsume;

   public FlowControlHandler() {
      this(true);
   }

   public FlowControlHandler(boolean var1) {
      this.releaseMessages = var1;
   }

   boolean isQueueEmpty() {
      return this.queue == null || this.queue.isEmpty();
   }

   private void destroy() {
      if (this.queue != null) {
         if (!this.queue.isEmpty()) {
            logger.trace("Non-empty queue: {}", this.queue);
            Object var1;
            if (this.releaseMessages) {
               while ((var1 = this.queue.poll()) != null) {
                  ReferenceCountUtil.safeRelease(var1);
               }
            }
         }

         this.queue.recycle();
         this.queue = null;
      }
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.config = var1.channel().config();
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      super.handlerRemoved(var1);
      if (!this.isQueueEmpty()) {
         this.dequeue(var1, this.queue.size());
      }

      this.destroy();
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.destroy();
      var1.fireChannelInactive();
   }

   @Override
   public void read(ChannelHandlerContext var1) throws Exception {
      if (this.dequeue(var1, 1) == 0) {
         this.shouldConsume = true;
         var1.read();
      }
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (this.queue == null) {
         this.queue = FlowControlHandler.RecyclableArrayDeque.newInstance();
      }

      this.queue.offer(var2);
      int var3 = this.shouldConsume ? 1 : 0;
      this.shouldConsume = false;
      this.dequeue(var1, var3);
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if (this.isQueueEmpty()) {
         var1.fireChannelReadComplete();
      }
   }

   private int dequeue(ChannelHandlerContext var1, int var2) {
      int var3 = 0;

      while (this.queue != null && (var3 < var2 || this.config.isAutoRead())) {
         Object var4 = this.queue.poll();
         if (var4 == null) {
            break;
         }

         var3++;
         var1.fireChannelRead(var4);
      }

      if (this.queue != null && this.queue.isEmpty()) {
         this.queue.recycle();
         this.queue = null;
         if (var3 > 0) {
            var1.fireChannelReadComplete();
         }
      }

      return var3;
   }

   private static final class RecyclableArrayDeque extends ArrayDeque<Object> {
      private static final long serialVersionUID = 0L;
      private static final int DEFAULT_NUM_ELEMENTS = 2;
      private static final ObjectPool<FlowControlHandler.RecyclableArrayDeque> RECYCLER = ObjectPool.newPool(
         new ObjectPool.ObjectCreator<FlowControlHandler.RecyclableArrayDeque>() {
            public FlowControlHandler.RecyclableArrayDeque newObject(ObjectPool.Handle<FlowControlHandler.RecyclableArrayDeque> var1) {
               return new FlowControlHandler.RecyclableArrayDeque(2, var1);
            }
         }
      );
      private final ObjectPool.Handle<FlowControlHandler.RecyclableArrayDeque> handle;

      public static FlowControlHandler.RecyclableArrayDeque newInstance() {
         return RECYCLER.get();
      }

      private RecyclableArrayDeque(int var1, ObjectPool.Handle<FlowControlHandler.RecyclableArrayDeque> var2) {
         super(var1);
         this.handle = var2;
      }

      public void recycle() {
         this.clear();
         this.handle.recycle(this);
      }
   }
}
