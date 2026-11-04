package io.netty.channel.embedded;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class EmbeddedChannel extends AbstractChannel {
   private static final SocketAddress LOCAL_ADDRESS = new EmbeddedSocketAddress();
   private static final SocketAddress REMOTE_ADDRESS = new EmbeddedSocketAddress();
   private static final ChannelHandler[] EMPTY_HANDLERS = new ChannelHandler[0];
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
   private static final ChannelMetadata METADATA_NO_DISCONNECT = new ChannelMetadata(false);
   private static final ChannelMetadata METADATA_DISCONNECT = new ChannelMetadata(true);
   private final EmbeddedEventLoop loop = new EmbeddedEventLoop();
   private final ChannelFutureListener recordExceptionListener = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture var1) throws Exception {
         EmbeddedChannel.this.recordException(var1);
      }
   };
   private final ChannelMetadata metadata;
   private final ChannelConfig config;
   private Queue<Object> inboundMessages;
   private Queue<Object> outboundMessages;
   private Throwable lastException;
   private EmbeddedChannel.State state;

   public EmbeddedChannel() {
      this(EMPTY_HANDLERS);
   }

   public EmbeddedChannel(ChannelId var1) {
      this(var1, EMPTY_HANDLERS);
   }

   public EmbeddedChannel(ChannelHandler... var1) {
      this(EmbeddedChannelId.INSTANCE, var1);
   }

   public EmbeddedChannel(boolean var1, ChannelHandler... var2) {
      this(EmbeddedChannelId.INSTANCE, var1, var2);
   }

   public EmbeddedChannel(boolean var1, boolean var2, ChannelHandler... var3) {
      this(EmbeddedChannelId.INSTANCE, var1, var2, var3);
   }

   public EmbeddedChannel(ChannelId var1, ChannelHandler... var2) {
      this(var1, false, var2);
   }

   public EmbeddedChannel(ChannelId var1, boolean var2, ChannelHandler... var3) {
      this(var1, true, var2, var3);
   }

   public EmbeddedChannel(ChannelId var1, boolean var2, boolean var3, ChannelHandler... var4) {
      this(null, var1, var2, var3, var4);
   }

   public EmbeddedChannel(Channel var1, ChannelId var2, boolean var3, boolean var4, ChannelHandler... var5) {
      super(var1, var2);
      this.metadata = metadata(var4);
      this.config = new DefaultChannelConfig(this);
      this.setup(var3, var5);
   }

   public EmbeddedChannel(ChannelId var1, boolean var2, ChannelConfig var3, ChannelHandler... var4) {
      super(null, var1);
      this.metadata = metadata(var2);
      this.config = ObjectUtil.checkNotNull(var3, "config");
      this.setup(true, var4);
   }

   private static ChannelMetadata metadata(boolean var0) {
      return var0 ? METADATA_DISCONNECT : METADATA_NO_DISCONNECT;
   }

   private void setup(boolean var1, final ChannelHandler... var2) {
      ObjectUtil.checkNotNull(var2, "handlers");
      ChannelPipeline var3 = this.pipeline();
      var3.addLast(new ChannelInitializer<Channel>() {
         @Override
         protected void initChannel(Channel var1) throws Exception {
            ChannelPipeline var2x = var1.pipeline();

            for (ChannelHandler var6 : var2) {
               if (var6 == null) {
                  break;
               }

               var2x.addLast(var6);
            }
         }
      });
      if (var1) {
         ChannelFuture var4 = this.loop.register(this);

         assert var4.isDone();
      }
   }

   public void register() throws Exception {
      ChannelFuture var1 = this.loop.register(this);

      assert var1.isDone();

      Throwable var2 = var1.cause();
      if (var2 != null) {
         PlatformDependent.throwException(var2);
      }
   }

   @Override
   protected final DefaultChannelPipeline newChannelPipeline() {
      return new EmbeddedChannel.EmbeddedChannelPipeline(this);
   }

   @Override
   public ChannelMetadata metadata() {
      return this.metadata;
   }

   @Override
   public ChannelConfig config() {
      return this.config;
   }

   @Override
   public boolean isOpen() {
      return this.state != EmbeddedChannel.State.CLOSED;
   }

   @Override
   public boolean isActive() {
      return this.state == EmbeddedChannel.State.ACTIVE;
   }

   public Queue<Object> inboundMessages() {
      if (this.inboundMessages == null) {
         this.inboundMessages = new ArrayDeque<>();
      }

      return this.inboundMessages;
   }

   @Deprecated
   public Queue<Object> lastInboundBuffer() {
      return this.inboundMessages();
   }

   public Queue<Object> outboundMessages() {
      if (this.outboundMessages == null) {
         this.outboundMessages = new ArrayDeque<>();
      }

      return this.outboundMessages;
   }

   @Deprecated
   public Queue<Object> lastOutboundBuffer() {
      return this.outboundMessages();
   }

   public <T> T readInbound() {
      Object var1 = poll(this.inboundMessages);
      if (var1 != null) {
         ReferenceCountUtil.touch(var1, "Caller of readInbound() will handle the message from this point");
      }

      return (T)var1;
   }

   public <T> T readOutbound() {
      Object var1 = poll(this.outboundMessages);
      if (var1 != null) {
         ReferenceCountUtil.touch(var1, "Caller of readOutbound() will handle the message from this point.");
      }

      return (T)var1;
   }

   public boolean writeInbound(Object... var1) {
      this.ensureOpen();
      if (var1.length == 0) {
         return isNotEmpty(this.inboundMessages);
      } else {
         ChannelPipeline var2 = this.pipeline();

         for (Object var6 : var1) {
            var2.fireChannelRead(var6);
         }

         this.flushInbound(false, this.voidPromise());
         return isNotEmpty(this.inboundMessages);
      }
   }

   public ChannelFuture writeOneInbound(Object var1) {
      return this.writeOneInbound(var1, this.newPromise());
   }

   public ChannelFuture writeOneInbound(Object var1, ChannelPromise var2) {
      if (this.checkOpen(true)) {
         this.pipeline().fireChannelRead(var1);
      }

      return this.checkException(var2);
   }

   public EmbeddedChannel flushInbound() {
      this.flushInbound(true, this.voidPromise());
      return this;
   }

   private ChannelFuture flushInbound(boolean var1, ChannelPromise var2) {
      if (this.checkOpen(var1)) {
         this.pipeline().fireChannelReadComplete();
         this.runPendingTasks();
      }

      return this.checkException(var2);
   }

   public boolean writeOutbound(Object... var1) {
      this.ensureOpen();
      if (var1.length == 0) {
         return isNotEmpty(this.outboundMessages);
      } else {
         RecyclableArrayList var2 = RecyclableArrayList.newInstance(var1.length);

         boolean var12;
         try {
            for (Object var6 : var1) {
               if (var6 == null) {
                  break;
               }

               var2.add(this.write(var6));
            }

            this.flushOutbound0();
            int var10 = var2.size();

            for (int var11 = 0; var11 < var10; var11++) {
               ChannelFuture var13 = (ChannelFuture)var2.get(var11);
               if (var13.isDone()) {
                  this.recordException(var13);
               } else {
                  var13.addListener(this.recordExceptionListener);
               }
            }

            this.checkException();
            var12 = isNotEmpty(this.outboundMessages);
         } finally {
            var2.recycle();
         }

         return var12;
      }
   }

   public ChannelFuture writeOneOutbound(Object var1) {
      return this.writeOneOutbound(var1, this.newPromise());
   }

   public ChannelFuture writeOneOutbound(Object var1, ChannelPromise var2) {
      return this.checkOpen(true) ? this.write(var1, var2) : this.checkException(var2);
   }

   public EmbeddedChannel flushOutbound() {
      if (this.checkOpen(true)) {
         this.flushOutbound0();
      }

      this.checkException(this.voidPromise());
      return this;
   }

   private void flushOutbound0() {
      this.runPendingTasks();
      this.flush();
   }

   public boolean finish() {
      return this.finish(false);
   }

   public boolean finishAndReleaseAll() {
      return this.finish(true);
   }

   private boolean finish(boolean var1) {
      this.close();

      boolean var2;
      try {
         this.checkException();
         var2 = isNotEmpty(this.inboundMessages) || isNotEmpty(this.outboundMessages);
      } finally {
         if (var1) {
            releaseAll(this.inboundMessages);
            releaseAll(this.outboundMessages);
         }
      }

      return var2;
   }

   public boolean releaseInbound() {
      return releaseAll(this.inboundMessages);
   }

   public boolean releaseOutbound() {
      return releaseAll(this.outboundMessages);
   }

   private static boolean releaseAll(Queue<Object> var0) {
      if (!isNotEmpty(var0)) {
         return false;
      } else {
         while (true) {
            Object var1 = var0.poll();
            if (var1 == null) {
               return true;
            }

            ReferenceCountUtil.release(var1);
         }
      }
   }

   private void finishPendingTasks(boolean var1) {
      this.runPendingTasks();
      if (var1) {
         this.embeddedEventLoop().cancelScheduledTasks();
      }
   }

   @Override
   public final ChannelFuture close() {
      return this.close(this.newPromise());
   }

   @Override
   public final ChannelFuture disconnect() {
      return this.disconnect(this.newPromise());
   }

   @Override
   public final ChannelFuture close(ChannelPromise var1) {
      this.runPendingTasks();
      ChannelFuture var2 = super.close(var1);
      this.finishPendingTasks(true);
      return var2;
   }

   @Override
   public final ChannelFuture disconnect(ChannelPromise var1) {
      ChannelFuture var2 = super.disconnect(var1);
      this.finishPendingTasks(!this.metadata.hasDisconnect());
      return var2;
   }

   private static boolean isNotEmpty(Queue<Object> var0) {
      return var0 != null && !var0.isEmpty();
   }

   private static Object poll(Queue<Object> var0) {
      return var0 != null ? var0.poll() : null;
   }

   public void runPendingTasks() {
      try {
         this.embeddedEventLoop().runTasks();
      } catch (Exception var3) {
         this.recordException(var3);
      }

      try {
         this.embeddedEventLoop().runScheduledTasks();
      } catch (Exception var2) {
         this.recordException(var2);
      }
   }

   public boolean hasPendingTasks() {
      return this.embeddedEventLoop().hasPendingNormalTasks() || this.embeddedEventLoop().nextScheduledTask() == 0L;
   }

   public long runScheduledPendingTasks() {
      try {
         return this.embeddedEventLoop().runScheduledTasks();
      } catch (Exception var2) {
         this.recordException(var2);
         return this.embeddedEventLoop().nextScheduledTask();
      }
   }

   private void recordException(ChannelFuture var1) {
      if (!var1.isSuccess()) {
         this.recordException(var1.cause());
      }
   }

   private void recordException(Throwable var1) {
      if (this.lastException == null) {
         this.lastException = var1;
      } else {
         logger.warn("More than one exception was raised. Will report only the first one and log others.", var1);
      }
   }

   public void advanceTimeBy(long var1, TimeUnit var3) {
      this.embeddedEventLoop().advanceTimeBy(var3.toNanos(var1));
   }

   public void freezeTime() {
      this.embeddedEventLoop().freezeTime();
   }

   public void unfreezeTime() {
      this.embeddedEventLoop().unfreezeTime();
   }

   private ChannelFuture checkException(ChannelPromise var1) {
      Throwable var2 = this.lastException;
      if (var2 != null) {
         this.lastException = null;
         if (var1.isVoid()) {
            PlatformDependent.throwException(var2);
         }

         return var1.setFailure(var2);
      } else {
         return var1.setSuccess();
      }
   }

   public void checkException() {
      this.checkException(this.voidPromise());
   }

   private boolean checkOpen(boolean var1) {
      if (!this.isOpen()) {
         if (var1) {
            this.recordException(new ClosedChannelException());
         }

         return false;
      } else {
         return true;
      }
   }

   private EmbeddedEventLoop embeddedEventLoop() {
      return this.isRegistered() ? (EmbeddedEventLoop)super.eventLoop() : this.loop;
   }

   protected final void ensureOpen() {
      if (!this.checkOpen(true)) {
         this.checkException();
      }
   }

   @Override
   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof EmbeddedEventLoop;
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.isActive() ? LOCAL_ADDRESS : null;
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return this.isActive() ? REMOTE_ADDRESS : null;
   }

   @Override
   protected void doRegister() throws Exception {
      this.state = EmbeddedChannel.State.ACTIVE;
   }

   @Override
   protected void doBind(SocketAddress var1) throws Exception {
   }

   @Override
   protected void doDisconnect() throws Exception {
      if (!this.metadata.hasDisconnect()) {
         this.doClose();
      }
   }

   @Override
   protected void doClose() throws Exception {
      this.state = EmbeddedChannel.State.CLOSED;
   }

   @Override
   protected void doBeginRead() throws Exception {
   }

   @Override
   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new EmbeddedChannel.EmbeddedUnsafe();
   }

   @Override
   public Channel.Unsafe unsafe() {
      return ((EmbeddedChannel.EmbeddedUnsafe)super.unsafe()).wrapped;
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      while (true) {
         Object var2 = var1.current();
         if (var2 == null) {
            return;
         }

         ReferenceCountUtil.retain(var2);
         this.handleOutboundMessage(var2);
         var1.remove();
      }
   }

   protected void handleOutboundMessage(Object var1) {
      this.outboundMessages().add(var1);
   }

   protected void handleInboundMessage(Object var1) {
      this.inboundMessages().add(var1);
   }

   private final class EmbeddedChannelPipeline extends DefaultChannelPipeline {
      EmbeddedChannelPipeline(EmbeddedChannel var2) {
         super(var2);
      }

      @Override
      protected void onUnhandledInboundException(Throwable var1) {
         EmbeddedChannel.this.recordException(var1);
      }

      @Override
      protected void onUnhandledInboundMessage(ChannelHandlerContext var1, Object var2) {
         EmbeddedChannel.this.handleInboundMessage(var2);
      }
   }

   private final class EmbeddedUnsafe extends AbstractChannel.AbstractUnsafe {
      final Channel.Unsafe wrapped = new Channel.Unsafe() {
         @Override
         public RecvByteBufAllocator.Handle recvBufAllocHandle() {
            return EmbeddedUnsafe.this.recvBufAllocHandle();
         }

         @Override
         public SocketAddress localAddress() {
            return EmbeddedUnsafe.this.localAddress();
         }

         @Override
         public SocketAddress remoteAddress() {
            return EmbeddedUnsafe.this.remoteAddress();
         }

         @Override
         public void register(EventLoop var1, ChannelPromise var2) {
            EmbeddedUnsafe.this.register(var1, var2);
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void bind(SocketAddress var1, ChannelPromise var2) {
            EmbeddedUnsafe.this.bind(var1, var2);
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
            EmbeddedUnsafe.this.connect(var1, var2, var3);
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void disconnect(ChannelPromise var1) {
            EmbeddedUnsafe.this.disconnect(var1);
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void close(ChannelPromise var1) {
            EmbeddedUnsafe.this.close(var1);
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void closeForcibly() {
            EmbeddedUnsafe.this.closeForcibly();
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void deregister(ChannelPromise var1) {
            EmbeddedUnsafe.this.deregister(var1);
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void beginRead() {
            EmbeddedUnsafe.this.beginRead();
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void write(Object var1, ChannelPromise var2) {
            EmbeddedUnsafe.this.write(var1, var2);
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public void flush() {
            EmbeddedUnsafe.this.flush();
            EmbeddedChannel.this.runPendingTasks();
         }

         @Override
         public ChannelPromise voidPromise() {
            return EmbeddedUnsafe.this.voidPromise();
         }

         @Override
         public ChannelOutboundBuffer outboundBuffer() {
            return EmbeddedUnsafe.this.outboundBuffer();
         }
      };

      private EmbeddedUnsafe() {
      }

      @Override
      public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         this.safeSetSuccess(var3);
      }
   }

   private static enum State {
      OPEN,
      ACTIVE,
      CLOSED;
   }
}
