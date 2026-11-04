package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, ServerChannel> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
   private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> childAttrs = new ConcurrentHashMap<>();
   private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
   private volatile EventLoopGroup childGroup;
   private volatile ChannelHandler childHandler;

   public ServerBootstrap() {
   }

   private ServerBootstrap(ServerBootstrap var1) {
      super(var1);
      this.childGroup = var1.childGroup;
      this.childHandler = var1.childHandler;
      synchronized (var1.childOptions) {
         this.childOptions.putAll(var1.childOptions);
      }

      this.childAttrs.putAll(var1.childAttrs);
   }

   public ServerBootstrap group(EventLoopGroup var1) {
      return this.group(var1, var1);
   }

   public ServerBootstrap group(EventLoopGroup var1, EventLoopGroup var2) {
      super.group(var1);
      if (this.childGroup != null) {
         throw new IllegalStateException("childGroup set already");
      } else {
         this.childGroup = ObjectUtil.checkNotNull(var2, "childGroup");
         return this;
      }
   }

   public <T> ServerBootstrap childOption(ChannelOption<T> var1, T var2) {
      ObjectUtil.checkNotNull(var1, "childOption");
      synchronized (this.childOptions) {
         if (var2 == null) {
            this.childOptions.remove(var1);
         } else {
            this.childOptions.put(var1, var2);
         }

         return this;
      }
   }

   public <T> ServerBootstrap childAttr(AttributeKey<T> var1, T var2) {
      ObjectUtil.checkNotNull(var1, "childKey");
      if (var2 == null) {
         this.childAttrs.remove(var1);
      } else {
         this.childAttrs.put(var1, var2);
      }

      return this;
   }

   public ServerBootstrap childHandler(ChannelHandler var1) {
      this.childHandler = ObjectUtil.checkNotNull(var1, "childHandler");
      return this;
   }

   @Override
   void init(Channel var1) {
      setChannelOptions(var1, this.newOptionsArray(), logger);
      setAttributes(var1, this.newAttributesArray());
      ChannelPipeline var2 = var1.pipeline();
      final EventLoopGroup var3 = this.childGroup;
      final ChannelHandler var4 = this.childHandler;
      final Entry[] var5 = newOptionsArray(this.childOptions);
      final Entry[] var6 = newAttributesArray(this.childAttrs);
      var2.addLast(new ChannelInitializer<Channel>() {
         @Override
         public void initChannel(final Channel var1) {
            final ChannelPipeline var2 = var1.pipeline();
            ChannelHandler var3x = ServerBootstrap.this.config.handler();
            if (var3x != null) {
               var2.addLast(var3x);
            }

            var1.eventLoop().execute(new Runnable() {
               @Override
               public void run() {
                  var2.addLast(new ServerBootstrap.ServerBootstrapAcceptor(var1, var3, var4, var5, var6));
               }
            });
         }
      });
   }

   public ServerBootstrap validate() {
      super.validate();
      if (this.childHandler == null) {
         throw new IllegalStateException("childHandler not set");
      } else {
         if (this.childGroup == null) {
            logger.warn("childGroup is not set. Using parentGroup instead.");
            this.childGroup = this.config.group();
         }

         return this;
      }
   }

   public ServerBootstrap clone() {
      return new ServerBootstrap(this);
   }

   @Deprecated
   public EventLoopGroup childGroup() {
      return this.childGroup;
   }

   final ChannelHandler childHandler() {
      return this.childHandler;
   }

   final Map<ChannelOption<?>, Object> childOptions() {
      synchronized (this.childOptions) {
         return copiedMap(this.childOptions);
      }
   }

   final Map<AttributeKey<?>, Object> childAttrs() {
      return copiedMap(this.childAttrs);
   }

   public final ServerBootstrapConfig config() {
      return this.config;
   }

   private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {
      private final EventLoopGroup childGroup;
      private final ChannelHandler childHandler;
      private final Entry<ChannelOption<?>, Object>[] childOptions;
      private final Entry<AttributeKey<?>, Object>[] childAttrs;
      private final Runnable enableAutoReadTask;

      ServerBootstrapAcceptor(
         final Channel var1, EventLoopGroup var2, ChannelHandler var3, Entry<ChannelOption<?>, Object>[] var4, Entry<AttributeKey<?>, Object>[] var5
      ) {
         this.childGroup = var2;
         this.childHandler = var3;
         this.childOptions = var4;
         this.childAttrs = var5;
         this.enableAutoReadTask = new Runnable() {
            @Override
            public void run() {
               var1.config().setAutoRead(true);
            }
         };
      }

      @Override
      public void channelRead(ChannelHandlerContext var1, Object var2) {
         final Channel var3 = (Channel)var2;
         var3.pipeline().addLast(this.childHandler);
         AbstractBootstrap.setChannelOptions(var3, this.childOptions, ServerBootstrap.logger);
         AbstractBootstrap.setAttributes(var3, this.childAttrs);

         try {
            this.childGroup.register(var3).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1) throws Exception {
                  if (!var1.isSuccess()) {
                     ServerBootstrap.ServerBootstrapAcceptor.forceClose(var3, var1.cause());
                  }
               }
            });
         } catch (Throwable var5) {
            forceClose(var3, var5);
         }
      }

      private static void forceClose(Channel var0, Throwable var1) {
         var0.unsafe().closeForcibly();
         ServerBootstrap.logger.warn("Failed to register an accepted channel: {}", var0, var1);
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
         ChannelConfig var3 = var1.channel().config();
         if (var3.isAutoRead()) {
            var3.setAutoRead(false);
            var1.channel().eventLoop().schedule(this.enableAutoReadTask, 1L, TimeUnit.SECONDS);
         }

         var1.fireExceptionCaught(var2);
      }
   }
}
