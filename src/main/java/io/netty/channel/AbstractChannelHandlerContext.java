package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakHint;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractChannelHandlerContext implements ChannelHandlerContext, ResourceLeakHint {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
   volatile AbstractChannelHandlerContext next;
   volatile AbstractChannelHandlerContext prev;
   private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      AbstractChannelHandlerContext.class, "handlerState"
   );
   private static final int ADD_PENDING = 1;
   private static final int ADD_COMPLETE = 2;
   private static final int REMOVE_COMPLETE = 3;
   private static final int INIT = 0;
   private final DefaultChannelPipeline pipeline;
   private final String name;
   private final boolean ordered;
   private final int executionMask;
   final EventExecutor executor;
   private ChannelFuture succeededFuture;
   private AbstractChannelHandlerContext.Tasks invokeTasks;
   private volatile int handlerState = 0;

   AbstractChannelHandlerContext(DefaultChannelPipeline var1, EventExecutor var2, String var3, Class<? extends ChannelHandler> var4) {
      this.name = ObjectUtil.checkNotNull(var3, "name");
      this.pipeline = var1;
      this.executor = var2;
      this.executionMask = ChannelHandlerMask.mask(var4);
      this.ordered = var2 == null || var2 instanceof OrderedEventExecutor;
   }

   @Override
   public Channel channel() {
      return this.pipeline.channel();
   }

   @Override
   public ChannelPipeline pipeline() {
      return this.pipeline;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.channel().config().getAllocator();
   }

   @Override
   public EventExecutor executor() {
      return (EventExecutor)(this.executor == null ? this.channel().eventLoop() : this.executor);
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public ChannelHandlerContext fireChannelRegistered() {
      invokeChannelRegistered(this.findContextInbound(2));
      return this;
   }

   static void invokeChannelRegistered(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelRegistered();
      } else {
         var1.execute(new Runnable() {
            @Override
            public void run() {
               var0.invokeChannelRegistered();
            }
         });
      }
   }

   private void invokeChannelRegistered() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelRegistered(this);
         } catch (Throwable var2) {
            this.invokeExceptionCaught(var2);
         }
      } else {
         this.fireChannelRegistered();
      }
   }

   @Override
   public ChannelHandlerContext fireChannelUnregistered() {
      invokeChannelUnregistered(this.findContextInbound(4));
      return this;
   }

   static void invokeChannelUnregistered(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelUnregistered();
      } else {
         var1.execute(new Runnable() {
            @Override
            public void run() {
               var0.invokeChannelUnregistered();
            }
         });
      }
   }

   private void invokeChannelUnregistered() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelUnregistered(this);
         } catch (Throwable var2) {
            this.invokeExceptionCaught(var2);
         }
      } else {
         this.fireChannelUnregistered();
      }
   }

   @Override
   public ChannelHandlerContext fireChannelActive() {
      invokeChannelActive(this.findContextInbound(8));
      return this;
   }

   static void invokeChannelActive(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelActive();
      } else {
         var1.execute(new Runnable() {
            @Override
            public void run() {
               var0.invokeChannelActive();
            }
         });
      }
   }

   private void invokeChannelActive() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelActive(this);
         } catch (Throwable var2) {
            this.invokeExceptionCaught(var2);
         }
      } else {
         this.fireChannelActive();
      }
   }

   @Override
   public ChannelHandlerContext fireChannelInactive() {
      invokeChannelInactive(this.findContextInbound(16));
      return this;
   }

   static void invokeChannelInactive(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelInactive();
      } else {
         var1.execute(new Runnable() {
            @Override
            public void run() {
               var0.invokeChannelInactive();
            }
         });
      }
   }

   private void invokeChannelInactive() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelInactive(this);
         } catch (Throwable var2) {
            this.invokeExceptionCaught(var2);
         }
      } else {
         this.fireChannelInactive();
      }
   }

   @Override
   public ChannelHandlerContext fireExceptionCaught(Throwable var1) {
      invokeExceptionCaught(this.findContextInbound(1), var1);
      return this;
   }

   static void invokeExceptionCaught(final AbstractChannelHandlerContext var0, final Throwable var1) {
      ObjectUtil.checkNotNull(var1, "cause");
      EventExecutor var2 = var0.executor();
      if (var2.inEventLoop()) {
         var0.invokeExceptionCaught(var1);
      } else {
         try {
            var2.execute(new Runnable() {
               @Override
               public void run() {
                  var0.invokeExceptionCaught(var1);
               }
            });
         } catch (Throwable var4) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to submit an exceptionCaught() event.", var4);
               logger.warn("The exceptionCaught() event that was failed to submit was:", var1);
            }
         }
      }
   }

   private void invokeExceptionCaught(Throwable var1) {
      if (this.invokeHandler()) {
         try {
            this.handler().exceptionCaught(this, var1);
         } catch (Throwable var3) {
            if (logger.isDebugEnabled()) {
               logger.debug(
                  "An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:",
                  ThrowableUtil.stackTraceToString(var3),
                  var1
               );
            } else if (logger.isWarnEnabled()) {
               logger.warn(
                  "An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:",
                  var3,
                  var1
               );
            }
         }
      } else {
         this.fireExceptionCaught(var1);
      }
   }

   @Override
   public ChannelHandlerContext fireUserEventTriggered(Object var1) {
      invokeUserEventTriggered(this.findContextInbound(128), var1);
      return this;
   }

   static void invokeUserEventTriggered(final AbstractChannelHandlerContext var0, final Object var1) {
      ObjectUtil.checkNotNull(var1, "event");
      EventExecutor var2 = var0.executor();
      if (var2.inEventLoop()) {
         var0.invokeUserEventTriggered(var1);
      } else {
         var2.execute(new Runnable() {
            @Override
            public void run() {
               var0.invokeUserEventTriggered(var1);
            }
         });
      }
   }

   private void invokeUserEventTriggered(Object var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).userEventTriggered(this, var1);
         } catch (Throwable var3) {
            this.invokeExceptionCaught(var3);
         }
      } else {
         this.fireUserEventTriggered(var1);
      }
   }

   @Override
   public ChannelHandlerContext fireChannelRead(Object var1) {
      invokeChannelRead(this.findContextInbound(32), var1);
      return this;
   }

   static void invokeChannelRead(final AbstractChannelHandlerContext var0, Object var1) {
      final Object var2 = var0.pipeline.touch(ObjectUtil.checkNotNull(var1, "msg"), var0);
      EventExecutor var3 = var0.executor();
      if (var3.inEventLoop()) {
         var0.invokeChannelRead(var2);
      } else {
         var3.execute(new Runnable() {
            @Override
            public void run() {
               var0.invokeChannelRead(var2);
            }
         });
      }
   }

   private void invokeChannelRead(Object var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelRead(this, var1);
         } catch (Throwable var3) {
            this.invokeExceptionCaught(var3);
         }
      } else {
         this.fireChannelRead(var1);
      }
   }

   @Override
   public ChannelHandlerContext fireChannelReadComplete() {
      invokeChannelReadComplete(this.findContextInbound(64));
      return this;
   }

   static void invokeChannelReadComplete(AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelReadComplete();
      } else {
         AbstractChannelHandlerContext.Tasks var2 = var0.invokeTasks;
         if (var2 == null) {
            var0.invokeTasks = var2 = new AbstractChannelHandlerContext.Tasks(var0);
         }

         var1.execute(var2.invokeChannelReadCompleteTask);
      }
   }

   private void invokeChannelReadComplete() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelReadComplete(this);
         } catch (Throwable var2) {
            this.invokeExceptionCaught(var2);
         }
      } else {
         this.fireChannelReadComplete();
      }
   }

   @Override
   public ChannelHandlerContext fireChannelWritabilityChanged() {
      invokeChannelWritabilityChanged(this.findContextInbound(256));
      return this;
   }

   static void invokeChannelWritabilityChanged(AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelWritabilityChanged();
      } else {
         AbstractChannelHandlerContext.Tasks var2 = var0.invokeTasks;
         if (var2 == null) {
            var0.invokeTasks = var2 = new AbstractChannelHandlerContext.Tasks(var0);
         }

         var1.execute(var2.invokeChannelWritableStateChangedTask);
      }
   }

   private void invokeChannelWritabilityChanged() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelWritabilityChanged(this);
         } catch (Throwable var2) {
            this.invokeExceptionCaught(var2);
         }
      } else {
         this.fireChannelWritabilityChanged();
      }
   }

   @Override
   public ChannelFuture bind(SocketAddress var1) {
      return this.bind(var1, this.newPromise());
   }

   @Override
   public ChannelFuture connect(SocketAddress var1) {
      return this.connect(var1, this.newPromise());
   }

   @Override
   public ChannelFuture connect(SocketAddress var1, SocketAddress var2) {
      return this.connect(var1, var2, this.newPromise());
   }

   @Override
   public ChannelFuture disconnect() {
      return this.disconnect(this.newPromise());
   }

   @Override
   public ChannelFuture close() {
      return this.close(this.newPromise());
   }

   @Override
   public ChannelFuture deregister() {
      return this.deregister(this.newPromise());
   }

   @Override
   public ChannelFuture bind(final SocketAddress var1, final ChannelPromise var2) {
      ObjectUtil.checkNotNull(var1, "localAddress");
      if (this.isNotValidPromise(var2, false)) {
         return var2;
      } else {
         final AbstractChannelHandlerContext var3 = this.findContextOutbound(512);
         EventExecutor var4 = var3.executor();
         if (var4.inEventLoop()) {
            var3.invokeBind(var1, var2);
         } else {
            safeExecute(var4, new Runnable() {
               @Override
               public void run() {
                  var3.invokeBind(var1, var2);
               }
            }, var2, null, false);
         }

         return var2;
      }
   }

   private void invokeBind(SocketAddress var1, ChannelPromise var2) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).bind(this, var1, var2);
         } catch (Throwable var4) {
            notifyOutboundHandlerException(var4, var2);
         }
      } else {
         this.bind(var1, var2);
      }
   }

   @Override
   public ChannelFuture connect(SocketAddress var1, ChannelPromise var2) {
      return this.connect(var1, null, var2);
   }

   @Override
   public ChannelFuture connect(final SocketAddress var1, final SocketAddress var2, final ChannelPromise var3) {
      ObjectUtil.checkNotNull(var1, "remoteAddress");
      if (this.isNotValidPromise(var3, false)) {
         return var3;
      } else {
         final AbstractChannelHandlerContext var4 = this.findContextOutbound(1024);
         EventExecutor var5 = var4.executor();
         if (var5.inEventLoop()) {
            var4.invokeConnect(var1, var2, var3);
         } else {
            safeExecute(var5, new Runnable() {
               @Override
               public void run() {
                  var4.invokeConnect(var1, var2, var3);
               }
            }, var3, null, false);
         }

         return var3;
      }
   }

   private void invokeConnect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).connect(this, var1, var2, var3);
         } catch (Throwable var5) {
            notifyOutboundHandlerException(var5, var3);
         }
      } else {
         this.connect(var1, var2, var3);
      }
   }

   @Override
   public ChannelFuture disconnect(final ChannelPromise var1) {
      if (!this.channel().metadata().hasDisconnect()) {
         return this.close(var1);
      } else if (this.isNotValidPromise(var1, false)) {
         return var1;
      } else {
         final AbstractChannelHandlerContext var2 = this.findContextOutbound(2048);
         EventExecutor var3 = var2.executor();
         if (var3.inEventLoop()) {
            var2.invokeDisconnect(var1);
         } else {
            safeExecute(var3, new Runnable() {
               @Override
               public void run() {
                  var2.invokeDisconnect(var1);
               }
            }, var1, null, false);
         }

         return var1;
      }
   }

   private void invokeDisconnect(ChannelPromise var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).disconnect(this, var1);
         } catch (Throwable var3) {
            notifyOutboundHandlerException(var3, var1);
         }
      } else {
         this.disconnect(var1);
      }
   }

   @Override
   public ChannelFuture close(final ChannelPromise var1) {
      if (this.isNotValidPromise(var1, false)) {
         return var1;
      } else {
         final AbstractChannelHandlerContext var2 = this.findContextOutbound(4096);
         EventExecutor var3 = var2.executor();
         if (var3.inEventLoop()) {
            var2.invokeClose(var1);
         } else {
            safeExecute(var3, new Runnable() {
               @Override
               public void run() {
                  var2.invokeClose(var1);
               }
            }, var1, null, false);
         }

         return var1;
      }
   }

   private void invokeClose(ChannelPromise var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).close(this, var1);
         } catch (Throwable var3) {
            notifyOutboundHandlerException(var3, var1);
         }
      } else {
         this.close(var1);
      }
   }

   @Override
   public ChannelFuture deregister(final ChannelPromise var1) {
      if (this.isNotValidPromise(var1, false)) {
         return var1;
      } else {
         final AbstractChannelHandlerContext var2 = this.findContextOutbound(8192);
         EventExecutor var3 = var2.executor();
         if (var3.inEventLoop()) {
            var2.invokeDeregister(var1);
         } else {
            safeExecute(var3, new Runnable() {
               @Override
               public void run() {
                  var2.invokeDeregister(var1);
               }
            }, var1, null, false);
         }

         return var1;
      }
   }

   private void invokeDeregister(ChannelPromise var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).deregister(this, var1);
         } catch (Throwable var3) {
            notifyOutboundHandlerException(var3, var1);
         }
      } else {
         this.deregister(var1);
      }
   }

   @Override
   public ChannelHandlerContext read() {
      AbstractChannelHandlerContext var1 = this.findContextOutbound(16384);
      EventExecutor var2 = var1.executor();
      if (var2.inEventLoop()) {
         var1.invokeRead();
      } else {
         AbstractChannelHandlerContext.Tasks var3 = var1.invokeTasks;
         if (var3 == null) {
            var1.invokeTasks = var3 = new AbstractChannelHandlerContext.Tasks(var1);
         }

         var2.execute(var3.invokeReadTask);
      }

      return this;
   }

   private void invokeRead() {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).read(this);
         } catch (Throwable var2) {
            this.invokeExceptionCaught(var2);
         }
      } else {
         this.read();
      }
   }

   @Override
   public ChannelFuture write(Object var1) {
      return this.write(var1, this.newPromise());
   }

   @Override
   public ChannelFuture write(Object var1, ChannelPromise var2) {
      this.write(var1, false, var2);
      return var2;
   }

   void invokeWrite(Object var1, ChannelPromise var2) {
      if (this.invokeHandler()) {
         this.invokeWrite0(var1, var2);
      } else {
         this.write(var1, var2);
      }
   }

   private void invokeWrite0(Object var1, ChannelPromise var2) {
      try {
         ((ChannelOutboundHandler)this.handler()).write(this, var1, var2);
      } catch (Throwable var4) {
         notifyOutboundHandlerException(var4, var2);
      }
   }

   @Override
   public ChannelHandlerContext flush() {
      AbstractChannelHandlerContext var1 = this.findContextOutbound(65536);
      EventExecutor var2 = var1.executor();
      if (var2.inEventLoop()) {
         var1.invokeFlush();
      } else {
         AbstractChannelHandlerContext.Tasks var3 = var1.invokeTasks;
         if (var3 == null) {
            var1.invokeTasks = var3 = new AbstractChannelHandlerContext.Tasks(var1);
         }

         safeExecute(var2, var3.invokeFlushTask, this.channel().voidPromise(), null, false);
      }

      return this;
   }

   private void invokeFlush() {
      if (this.invokeHandler()) {
         this.invokeFlush0();
      } else {
         this.flush();
      }
   }

   private void invokeFlush0() {
      try {
         ((ChannelOutboundHandler)this.handler()).flush(this);
      } catch (Throwable var2) {
         this.invokeExceptionCaught(var2);
      }
   }

   @Override
   public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2) {
      this.write(var1, true, var2);
      return var2;
   }

   void invokeWriteAndFlush(Object var1, ChannelPromise var2) {
      if (this.invokeHandler()) {
         this.invokeWrite0(var1, var2);
         this.invokeFlush0();
      } else {
         this.writeAndFlush(var1, var2);
      }
   }

   private void write(Object var1, boolean var2, ChannelPromise var3) {
      ObjectUtil.checkNotNull(var1, "msg");

      try {
         if (this.isNotValidPromise(var3, true)) {
            ReferenceCountUtil.release(var1);
            return;
         }
      } catch (RuntimeException var8) {
         ReferenceCountUtil.release(var1);
         throw var8;
      }

      AbstractChannelHandlerContext var4 = this.findContextOutbound(var2 ? 98304 : '耀');
      Object var5 = this.pipeline.touch(var1, var4);
      EventExecutor var6 = var4.executor();
      if (var6.inEventLoop()) {
         if (var2) {
            var4.invokeWriteAndFlush(var5, var3);
         } else {
            var4.invokeWrite(var5, var3);
         }
      } else {
         AbstractChannelHandlerContext.WriteTask var7 = AbstractChannelHandlerContext.WriteTask.newInstance(var4, var5, var3, var2);
         if (!safeExecute(var6, var7, var3, var5, !var2)) {
            var7.cancel();
         }
      }
   }

   @Override
   public ChannelFuture writeAndFlush(Object var1) {
      return this.writeAndFlush(var1, this.newPromise());
   }

   private static void notifyOutboundHandlerException(Throwable var0, ChannelPromise var1) {
      PromiseNotificationUtil.tryFailure(var1, var0, var1 instanceof VoidChannelPromise ? null : logger);
   }

   @Override
   public ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel(), this.executor());
   }

   @Override
   public ChannelProgressivePromise newProgressivePromise() {
      return new DefaultChannelProgressivePromise(this.channel(), this.executor());
   }

   @Override
   public ChannelFuture newSucceededFuture() {
      Object var1 = this.succeededFuture;
      if (var1 == null) {
         this.succeededFuture = (ChannelFuture)(var1 = new SucceededChannelFuture(this.channel(), this.executor()));
      }

      return (ChannelFuture)var1;
   }

   @Override
   public ChannelFuture newFailedFuture(Throwable var1) {
      return new FailedChannelFuture(this.channel(), this.executor(), var1);
   }

   private boolean isNotValidPromise(ChannelPromise var1, boolean var2) {
      ObjectUtil.checkNotNull(var1, "promise");
      if (var1.isDone()) {
         if (var1.isCancelled()) {
            return true;
         } else {
            throw new IllegalArgumentException("promise already done: " + var1);
         }
      } else if (var1.channel() != this.channel()) {
         throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", var1.channel(), this.channel()));
      } else if (var1.getClass() == DefaultChannelPromise.class) {
         return false;
      } else if (!var2 && var1 instanceof VoidChannelPromise) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
      } else if (var1 instanceof AbstractChannel.CloseFuture) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
      } else {
         return false;
      }
   }

   private AbstractChannelHandlerContext findContextInbound(int var1) {
      AbstractChannelHandlerContext var2 = this;
      EventExecutor var3 = this.executor();

      do {
         var2 = var2.next;
      } while (skipContext(var2, var3, var1, 510));

      return var2;
   }

   private AbstractChannelHandlerContext findContextOutbound(int var1) {
      AbstractChannelHandlerContext var2 = this;
      EventExecutor var3 = this.executor();

      do {
         var2 = var2.prev;
      } while (skipContext(var2, var3, var1, 130560));

      return var2;
   }

   private static boolean skipContext(AbstractChannelHandlerContext var0, EventExecutor var1, int var2, int var3) {
      return (var0.executionMask & (var3 | var2)) == 0 || var0.executor() == var1 && (var0.executionMask & var2) == 0;
   }

   @Override
   public ChannelPromise voidPromise() {
      return this.channel().voidPromise();
   }

   final void setRemoved() {
      this.handlerState = 3;
   }

   final boolean setAddComplete() {
      int var1;
      do {
         var1 = this.handlerState;
         if (var1 == 3) {
            return false;
         }
      } while (!HANDLER_STATE_UPDATER.compareAndSet(this, var1, 2));

      return true;
   }

   final void setAddPending() {
      boolean var1 = HANDLER_STATE_UPDATER.compareAndSet(this, 0, 1);

      assert var1;
   }

   final void callHandlerAdded() throws Exception {
      if (this.setAddComplete()) {
         this.handler().handlerAdded(this);
      }
   }

   final void callHandlerRemoved() throws Exception {
      try {
         if (this.handlerState == 2) {
            this.handler().handlerRemoved(this);
         }
      } finally {
         this.setRemoved();
      }
   }

   private boolean invokeHandler() {
      int var1 = this.handlerState;
      return var1 == 2 || !this.ordered && var1 == 1;
   }

   @Override
   public boolean isRemoved() {
      return this.handlerState == 3;
   }

   @Override
   public <T> Attribute<T> attr(AttributeKey<T> var1) {
      return this.channel().attr(var1);
   }

   @Override
   public <T> boolean hasAttr(AttributeKey<T> var1) {
      return this.channel().hasAttr(var1);
   }

   private static boolean safeExecute(EventExecutor var0, Runnable var1, ChannelPromise var2, Object var3, boolean var4) {
      try {
         if (var4 && var0 instanceof AbstractEventExecutor) {
            ((AbstractEventExecutor)var0).lazyExecute(var1);
         } else {
            var0.execute(var1);
         }

         return true;
      } catch (Throwable var10) {
         try {
            if (var3 != null) {
               ReferenceCountUtil.release(var3);
            }
         } finally {
            var2.setFailure(var10);
         }

         return false;
      }
   }

   @Override
   public String toHintString() {
      return '\'' + this.name + "' will handle the message from this point.";
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + this.channel() + ')';
   }

   private static final class Tasks {
      private final AbstractChannelHandlerContext next;
      private final Runnable invokeChannelReadCompleteTask = new Runnable() {
         @Override
         public void run() {
            Tasks.this.next.invokeChannelReadComplete();
         }
      };
      private final Runnable invokeReadTask = new Runnable() {
         @Override
         public void run() {
            Tasks.this.next.invokeRead();
         }
      };
      private final Runnable invokeChannelWritableStateChangedTask = new Runnable() {
         @Override
         public void run() {
            Tasks.this.next.invokeChannelWritabilityChanged();
         }
      };
      private final Runnable invokeFlushTask = new Runnable() {
         @Override
         public void run() {
            Tasks.this.next.invokeFlush();
         }
      };

      Tasks(AbstractChannelHandlerContext var1) {
         this.next = var1;
      }
   }

   static final class WriteTask implements Runnable {
      private static final ObjectPool<AbstractChannelHandlerContext.WriteTask> RECYCLER = ObjectPool.newPool(
         new ObjectPool.ObjectCreator<AbstractChannelHandlerContext.WriteTask>() {
            public AbstractChannelHandlerContext.WriteTask newObject(ObjectPool.Handle<AbstractChannelHandlerContext.WriteTask> var1) {
               return new AbstractChannelHandlerContext.WriteTask(var1);
            }
         }
      );
      private static final boolean ESTIMATE_TASK_SIZE_ON_SUBMIT = SystemPropertyUtil.getBoolean("io.netty.transport.estimateSizeOnSubmit", true);
      private static final int WRITE_TASK_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.writeTaskSizeOverhead", 32);
      private final ObjectPool.Handle<AbstractChannelHandlerContext.WriteTask> handle;
      private AbstractChannelHandlerContext ctx;
      private Object msg;
      private ChannelPromise promise;
      private int size;

      static AbstractChannelHandlerContext.WriteTask newInstance(AbstractChannelHandlerContext var0, Object var1, ChannelPromise var2, boolean var3) {
         AbstractChannelHandlerContext.WriteTask var4 = RECYCLER.get();
         init(var4, var0, var1, var2, var3);
         return var4;
      }

      private WriteTask(ObjectPool.Handle<? extends AbstractChannelHandlerContext.WriteTask> var1) {
         this.handle = var1;
      }

      protected static void init(
         AbstractChannelHandlerContext.WriteTask var0, AbstractChannelHandlerContext var1, Object var2, ChannelPromise var3, boolean var4
      ) {
         var0.ctx = var1;
         var0.msg = var2;
         var0.promise = var3;
         if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
            var0.size = var1.pipeline.estimatorHandle().size(var2) + WRITE_TASK_OVERHEAD;
            var1.pipeline.incrementPendingOutboundBytes((long)var0.size);
         } else {
            var0.size = 0;
         }

         if (var4) {
            var0.size |= Integer.MIN_VALUE;
         }
      }

      @Override
      public void run() {
         try {
            this.decrementPendingOutboundBytes();
            if (this.size >= 0) {
               this.ctx.invokeWrite(this.msg, this.promise);
            } else {
               this.ctx.invokeWriteAndFlush(this.msg, this.promise);
            }
         } finally {
            this.recycle();
         }
      }

      void cancel() {
         try {
            this.decrementPendingOutboundBytes();
         } finally {
            this.recycle();
         }
      }

      private void decrementPendingOutboundBytes() {
         if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
            this.ctx.pipeline.decrementPendingOutboundBytes((long)(this.size & 2147483647));
         }
      }

      private void recycle() {
         this.ctx = null;
         this.msg = null;
         this.promise = null;
         this.handle.recycle(this);
      }
   }
}
