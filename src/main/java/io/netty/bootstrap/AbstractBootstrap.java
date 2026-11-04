package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> implements Cloneable {
   private static final Entry<ChannelOption<?>, Object>[] EMPTY_OPTION_ARRAY = new Entry[0];
   private static final Entry<AttributeKey<?>, Object>[] EMPTY_ATTRIBUTE_ARRAY = new Entry[0];
   volatile EventLoopGroup group;
   private volatile ChannelFactory<? extends C> channelFactory;
   private volatile SocketAddress localAddress;
   private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> attrs = new ConcurrentHashMap<>();
   private volatile ChannelHandler handler;

   AbstractBootstrap() {
   }

   AbstractBootstrap(AbstractBootstrap<B, C> var1) {
      this.group = var1.group;
      this.channelFactory = var1.channelFactory;
      this.handler = var1.handler;
      this.localAddress = var1.localAddress;
      synchronized (var1.options) {
         this.options.putAll(var1.options);
      }

      this.attrs.putAll(var1.attrs);
   }

   public B group(EventLoopGroup var1) {
      ObjectUtil.checkNotNull(var1, "group");
      if (this.group != null) {
         throw new IllegalStateException("group set already");
      } else {
         this.group = var1;
         return this.self();
      }
   }

   private B self() {
      return (B)this;
   }

   public B channel(Class<? extends C> var1) {
      return this.channelFactory(new ReflectiveChannelFactory<>(ObjectUtil.checkNotNull(var1, "channelClass")));
   }

   @Deprecated
   public B channelFactory(ChannelFactory<? extends C> var1) {
      ObjectUtil.checkNotNull(var1, "channelFactory");
      if (this.channelFactory != null) {
         throw new IllegalStateException("channelFactory set already");
      } else {
         this.channelFactory = var1;
         return this.self();
      }
   }

   public B channelFactory(io.netty.channel.ChannelFactory<? extends C> var1) {
      return this.channelFactory(var1);
   }

   public B localAddress(SocketAddress var1) {
      this.localAddress = var1;
      return this.self();
   }

   public B localAddress(int var1) {
      return this.localAddress(new InetSocketAddress(var1));
   }

   public B localAddress(String var1, int var2) {
      return this.localAddress(SocketUtils.socketAddress(var1, var2));
   }

   public B localAddress(InetAddress var1, int var2) {
      return this.localAddress(new InetSocketAddress(var1, var2));
   }

   public <T> B option(ChannelOption<T> var1, T var2) {
      ObjectUtil.checkNotNull(var1, "option");
      synchronized (this.options) {
         if (var2 == null) {
            this.options.remove(var1);
         } else {
            this.options.put(var1, var2);
         }
      }

      return this.self();
   }

   public <T> B attr(AttributeKey<T> var1, T var2) {
      ObjectUtil.checkNotNull(var1, "key");
      if (var2 == null) {
         this.attrs.remove(var1);
      } else {
         this.attrs.put(var1, var2);
      }

      return this.self();
   }

   public B validate() {
      if (this.group == null) {
         throw new IllegalStateException("group not set");
      } else if (this.channelFactory == null) {
         throw new IllegalStateException("channel or channelFactory not set");
      } else {
         return this.self();
      }
   }

   public abstract B clone();

   public ChannelFuture register() {
      this.validate();
      return this.initAndRegister();
   }

   public ChannelFuture bind() {
      this.validate();
      SocketAddress var1 = this.localAddress;
      if (var1 == null) {
         throw new IllegalStateException("localAddress not set");
      } else {
         return this.doBind(var1);
      }
   }

   public ChannelFuture bind(int var1) {
      return this.bind(new InetSocketAddress(var1));
   }

   public ChannelFuture bind(String var1, int var2) {
      return this.bind(SocketUtils.socketAddress(var1, var2));
   }

   public ChannelFuture bind(InetAddress var1, int var2) {
      return this.bind(new InetSocketAddress(var1, var2));
   }

   public ChannelFuture bind(SocketAddress var1) {
      this.validate();
      return this.doBind(ObjectUtil.checkNotNull(var1, "localAddress"));
   }

   private ChannelFuture doBind(final SocketAddress var1) {
      final ChannelFuture var2 = this.initAndRegister();
      final Channel var3 = var2.channel();
      if (var2.cause() != null) {
         return var2;
      } else if (var2.isDone()) {
         ChannelPromise var5 = var3.newPromise();
         doBind0(var2, var3, var1, var5);
         return var5;
      } else {
         final AbstractBootstrap.PendingRegistrationPromise var4 = new AbstractBootstrap.PendingRegistrationPromise(var3);
         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               Throwable var2x = var1x.cause();
               if (var2x != null) {
                  var4.setFailure(var2x);
               } else {
                  var4.registered();
                  AbstractBootstrap.doBind0(var2, var3, var1, var4);
               }
            }
         });
         return var4;
      }
   }

   final ChannelFuture initAndRegister() {
      Channel var1 = null;

      try {
         var1 = this.channelFactory.newChannel();
         this.init(var1);
      } catch (Throwable var3) {
         if (var1 != null) {
            var1.unsafe().closeForcibly();
            return new DefaultChannelPromise(var1, GlobalEventExecutor.INSTANCE).setFailure(var3);
         }

         return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(var3);
      }

      ChannelFuture var2 = this.config().group().register(var1);
      if (var2.cause() != null) {
         if (var1.isRegistered()) {
            var1.close();
         } else {
            var1.unsafe().closeForcibly();
         }
      }

      return var2;
   }

   abstract void init(Channel var1) throws Exception;

   private static void doBind0(final ChannelFuture var0, final Channel var1, final SocketAddress var2, final ChannelPromise var3) {
      var1.eventLoop().execute(new Runnable() {
         @Override
         public void run() {
            if (var0.isSuccess()) {
               var1.bind(var2, var3).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
               var3.setFailure(var0.cause());
            }
         }
      });
   }

   public B handler(ChannelHandler var1) {
      this.handler = ObjectUtil.checkNotNull(var1, "handler");
      return this.self();
   }

   @Deprecated
   public final EventLoopGroup group() {
      return this.group;
   }

   public abstract AbstractBootstrapConfig<B, C> config();

   final Entry<ChannelOption<?>, Object>[] newOptionsArray() {
      return newOptionsArray(this.options);
   }

   static Entry<ChannelOption<?>, Object>[] newOptionsArray(Map<ChannelOption<?>, Object> var0) {
      synchronized (var0) {
         return new LinkedHashMap(var0).entrySet().toArray(EMPTY_OPTION_ARRAY);
      }
   }

   final Entry<AttributeKey<?>, Object>[] newAttributesArray() {
      return newAttributesArray(this.attrs0());
   }

   static Entry<AttributeKey<?>, Object>[] newAttributesArray(Map<AttributeKey<?>, Object> var0) {
      return var0.entrySet().toArray(EMPTY_ATTRIBUTE_ARRAY);
   }

   final Map<ChannelOption<?>, Object> options0() {
      return this.options;
   }

   final Map<AttributeKey<?>, Object> attrs0() {
      return this.attrs;
   }

   final SocketAddress localAddress() {
      return this.localAddress;
   }

   final ChannelFactory<? extends C> channelFactory() {
      return this.channelFactory;
   }

   final ChannelHandler handler() {
      return this.handler;
   }

   final Map<ChannelOption<?>, Object> options() {
      synchronized (this.options) {
         return copiedMap(this.options);
      }
   }

   final Map<AttributeKey<?>, Object> attrs() {
      return copiedMap(this.attrs);
   }

   static <K, V> Map<K, V> copiedMap(Map<K, V> var0) {
      return var0.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<>(var0));
   }

   static void setAttributes(Channel var0, Entry<AttributeKey<?>, Object>[] var1) {
      for (Entry var5 : var1) {
         AttributeKey var6 = (AttributeKey)var5.getKey();
         var0.<Object>attr(var6).set(var5.getValue());
      }
   }

   static void setChannelOptions(Channel var0, Entry<ChannelOption<?>, Object>[] var1, InternalLogger var2) {
      for (Entry var6 : var1) {
         setChannelOption(var0, (ChannelOption<?>)var6.getKey(), var6.getValue(), var2);
      }
   }

   private static void setChannelOption(Channel var0, ChannelOption<?> var1, Object var2, InternalLogger var3) {
      try {
         if (!var0.config().setOption(var1, var2)) {
            var3.warn("Unknown channel option '{}' for channel '{}'", var1, var0);
         }
      } catch (Throwable var5) {
         var3.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", var1, var2, var0, var5);
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder().append(StringUtil.simpleClassName(this)).append('(').append(this.config()).append(')');
      return var1.toString();
   }

   static final class PendingRegistrationPromise extends DefaultChannelPromise {
      private volatile boolean registered;

      PendingRegistrationPromise(Channel var1) {
         super(var1);
      }

      void registered() {
         this.registered = true;
      }

      @Override
      protected EventExecutor executor() {
         return (EventExecutor)(this.registered ? super.executor() : GlobalEventExecutor.INSTANCE);
      }
   }
}
