package io.netty.channel;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
   private final Set<ChannelHandlerContext> initMap = Collections.newSetFromMap(new ConcurrentHashMap<>());

   protected abstract void initChannel(C var1) throws Exception;

   @Override
   public final void channelRegistered(ChannelHandlerContext var1) throws Exception {
      if (this.initChannel(var1)) {
         var1.pipeline().fireChannelRegistered();
         this.removeState(var1);
      } else {
         var1.fireChannelRegistered();
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (logger.isWarnEnabled()) {
         logger.warn("Failed to initialize a channel. Closing: " + var1.channel(), var2);
      }

      var1.close();
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      if (var1.channel().isRegistered() && this.initChannel(var1)) {
         this.removeState(var1);
      }
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.initMap.remove(var1);
   }

   private boolean initChannel(ChannelHandlerContext var1) throws Exception {
      if (this.initMap.add(var1)) {
         try {
            this.initChannel((C)var1.channel());
         } catch (Throwable var6) {
            this.exceptionCaught(var1, var6);
         } finally {
            if (!var1.isRemoved()) {
               var1.pipeline().remove(this);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void removeState(final ChannelHandlerContext var1) {
      if (var1.isRemoved()) {
         this.initMap.remove(var1);
      } else {
         var1.executor().execute(new Runnable() {
            @Override
            public void run() {
               ChannelInitializer.this.initMap.remove(var1);
            }
         });
      }
   }
}
