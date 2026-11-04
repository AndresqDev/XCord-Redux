package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelPipeline implements ChannelPipeline {
   static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
   private static final String HEAD_NAME = generateName0(DefaultChannelPipeline.HeadContext.class);
   private static final String TAIL_NAME = generateName0(DefaultChannelPipeline.TailContext.class);
   private static final FastThreadLocal<Map<Class<?>, String>> nameCaches = new FastThreadLocal<Map<Class<?>, String>>() {
      protected Map<Class<?>, String> initialValue() {
         return new WeakHashMap<>();
      }
   };
   private static final AtomicReferenceFieldUpdater<DefaultChannelPipeline, MessageSizeEstimator.Handle> ESTIMATOR = AtomicReferenceFieldUpdater.newUpdater(
      DefaultChannelPipeline.class, MessageSizeEstimator.Handle.class, "estimatorHandle"
   );
   final AbstractChannelHandlerContext head;
   final AbstractChannelHandlerContext tail;
   private final Channel channel;
   private final ChannelFuture succeededFuture;
   private final VoidChannelPromise voidPromise;
   private final boolean touch = ResourceLeakDetector.isEnabled();
   private Map<EventExecutorGroup, EventExecutor> childExecutors;
   private volatile MessageSizeEstimator.Handle estimatorHandle;
   private boolean firstRegistration = true;
   private DefaultChannelPipeline.PendingHandlerCallback pendingHandlerCallbackHead;
   private boolean registered;

   protected DefaultChannelPipeline(Channel var1) {
      this.channel = ObjectUtil.checkNotNull(var1, "channel");
      this.succeededFuture = new SucceededChannelFuture(var1, null);
      this.voidPromise = new VoidChannelPromise(var1, true);
      this.tail = new DefaultChannelPipeline.TailContext(this);
      this.head = new DefaultChannelPipeline.HeadContext(this);
      this.head.next = this.tail;
      this.tail.prev = this.head;
   }

   final MessageSizeEstimator.Handle estimatorHandle() {
      MessageSizeEstimator.Handle var1 = this.estimatorHandle;
      if (var1 == null) {
         var1 = this.channel.config().getMessageSizeEstimator().newHandle();
         if (!ESTIMATOR.compareAndSet(this, null, var1)) {
            var1 = this.estimatorHandle;
         }
      }

      return var1;
   }

   final Object touch(Object var1, AbstractChannelHandlerContext var2) {
      return this.touch ? ReferenceCountUtil.touch(var1, var2) : var1;
   }

   private AbstractChannelHandlerContext newContext(EventExecutorGroup var1, String var2, ChannelHandler var3) {
      return new DefaultChannelHandlerContext(this, this.childExecutor(var1), var2, var3);
   }

   private EventExecutor childExecutor(EventExecutorGroup var1) {
      if (var1 == null) {
         return null;
      } else {
         Boolean var2 = this.channel.config().getOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
         if (var2 != null && !var2) {
            return var1.next();
         } else {
            Object var3 = this.childExecutors;
            if (var3 == null) {
               var3 = this.childExecutors = new IdentityHashMap<>(4);
            }

            EventExecutor var4 = (EventExecutor)var3.get(var1);
            if (var4 == null) {
               var4 = var1.next();
               var3.put(var1, var4);
            }

            return var4;
         }
      }
   }

   @Override
   public final Channel channel() {
      return this.channel;
   }

   @Override
   public final ChannelPipeline addFirst(String var1, ChannelHandler var2) {
      return this.addFirst(null, var1, var2);
   }

   @Override
   public final ChannelPipeline addFirst(EventExecutorGroup var1, String var2, ChannelHandler var3) {
      AbstractChannelHandlerContext var4;
      synchronized (this) {
         checkMultiplicity(var3);
         var2 = this.filterName(var2, var3);
         var4 = this.newContext(var1, var2, var3);
         this.addFirst0(var4);
         if (!this.registered) {
            var4.setAddPending();
            this.callHandlerCallbackLater(var4, true);
            return this;
         }

         EventExecutor var6 = var4.executor();
         if (!var6.inEventLoop()) {
            this.callHandlerAddedInEventLoop(var4, var6);
            return this;
         }
      }

      this.callHandlerAdded0(var4);
      return this;
   }

   private void addFirst0(AbstractChannelHandlerContext var1) {
      AbstractChannelHandlerContext var2 = this.head.next;
      var1.prev = this.head;
      var1.next = var2;
      this.head.next = var1;
      var2.prev = var1;
   }

   @Override
   public final ChannelPipeline addLast(String var1, ChannelHandler var2) {
      return this.addLast(null, var1, var2);
   }

   @Override
   public final ChannelPipeline addLast(EventExecutorGroup var1, String var2, ChannelHandler var3) {
      AbstractChannelHandlerContext var4;
      synchronized (this) {
         checkMultiplicity(var3);
         var4 = this.newContext(var1, this.filterName(var2, var3), var3);
         this.addLast0(var4);
         if (!this.registered) {
            var4.setAddPending();
            this.callHandlerCallbackLater(var4, true);
            return this;
         }

         EventExecutor var6 = var4.executor();
         if (!var6.inEventLoop()) {
            this.callHandlerAddedInEventLoop(var4, var6);
            return this;
         }
      }

      this.callHandlerAdded0(var4);
      return this;
   }

   private void addLast0(AbstractChannelHandlerContext var1) {
      AbstractChannelHandlerContext var2 = this.tail.prev;
      var1.prev = var2;
      var1.next = this.tail;
      var2.next = var1;
      this.tail.prev = var1;
   }

   @Override
   public final ChannelPipeline addBefore(String var1, String var2, ChannelHandler var3) {
      return this.addBefore(null, var1, var2, var3);
   }

   @Override
   public final ChannelPipeline addBefore(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4) {
      AbstractChannelHandlerContext var5;
      synchronized (this) {
         checkMultiplicity(var4);
         var3 = this.filterName(var3, var4);
         AbstractChannelHandlerContext var6 = this.getContextOrDie(var2);
         var5 = this.newContext(var1, var3, var4);
         addBefore0(var6, var5);
         if (!this.registered) {
            var5.setAddPending();
            this.callHandlerCallbackLater(var5, true);
            return this;
         }

         EventExecutor var8 = var5.executor();
         if (!var8.inEventLoop()) {
            this.callHandlerAddedInEventLoop(var5, var8);
            return this;
         }
      }

      this.callHandlerAdded0(var5);
      return this;
   }

   private static void addBefore0(AbstractChannelHandlerContext var0, AbstractChannelHandlerContext var1) {
      var1.prev = var0.prev;
      var1.next = var0;
      var0.prev.next = var1;
      var0.prev = var1;
   }

   private String filterName(String var1, ChannelHandler var2) {
      if (var1 == null) {
         return this.generateName(var2);
      } else {
         this.checkDuplicateName(var1);
         return var1;
      }
   }

   @Override
   public final ChannelPipeline addAfter(String var1, String var2, ChannelHandler var3) {
      return this.addAfter(null, var1, var2, var3);
   }

   @Override
   public final ChannelPipeline addAfter(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4) {
      AbstractChannelHandlerContext var5;
      synchronized (this) {
         checkMultiplicity(var4);
         var3 = this.filterName(var3, var4);
         AbstractChannelHandlerContext var6 = this.getContextOrDie(var2);
         var5 = this.newContext(var1, var3, var4);
         addAfter0(var6, var5);
         if (!this.registered) {
            var5.setAddPending();
            this.callHandlerCallbackLater(var5, true);
            return this;
         }

         EventExecutor var8 = var5.executor();
         if (!var8.inEventLoop()) {
            this.callHandlerAddedInEventLoop(var5, var8);
            return this;
         }
      }

      this.callHandlerAdded0(var5);
      return this;
   }

   private static void addAfter0(AbstractChannelHandlerContext var0, AbstractChannelHandlerContext var1) {
      var1.prev = var0;
      var1.next = var0.next;
      var0.next.prev = var1;
      var0.next = var1;
   }

   public final ChannelPipeline addFirst(ChannelHandler var1) {
      return this.addFirst(null, var1);
   }

   @Override
   public final ChannelPipeline addFirst(ChannelHandler... var1) {
      return this.addFirst(null, var1);
   }

   @Override
   public final ChannelPipeline addFirst(EventExecutorGroup var1, ChannelHandler... var2) {
      ObjectUtil.checkNotNull(var2, "handlers");
      if (var2.length != 0 && var2[0] != null) {
         int var3 = 1;

         while (var3 < var2.length && var2[var3] != null) {
            var3++;
         }

         for (int var4 = var3 - 1; var4 >= 0; var4--) {
            ChannelHandler var5 = var2[var4];
            this.addFirst(var1, null, var5);
         }

         return this;
      } else {
         return this;
      }
   }

   public final ChannelPipeline addLast(ChannelHandler var1) {
      return this.addLast(null, var1);
   }

   @Override
   public final ChannelPipeline addLast(ChannelHandler... var1) {
      return this.addLast(null, var1);
   }

   @Override
   public final ChannelPipeline addLast(EventExecutorGroup var1, ChannelHandler... var2) {
      ObjectUtil.checkNotNull(var2, "handlers");

      for (ChannelHandler var6 : var2) {
         if (var6 == null) {
            break;
         }

         this.addLast(var1, null, var6);
      }

      return this;
   }

   private String generateName(ChannelHandler var1) {
      Map var2 = nameCaches.get();
      Class var3 = var1.getClass();
      String var4 = (String)var2.get(var3);
      if (var4 == null) {
         var4 = generateName0(var3);
         var2.put(var3, var4);
      }

      if (this.context0(var4) != null) {
         String var5 = var4.substring(0, var4.length() - 1);
         int var6 = 1;

         while (true) {
            String var7 = var5 + var6;
            if (this.context0(var7) == null) {
               var4 = var7;
               break;
            }

            var6++;
         }
      }

      return var4;
   }

   private static String generateName0(Class<?> var0) {
      return StringUtil.simpleClassName(var0) + "#0";
   }

   @Override
   public final ChannelPipeline remove(ChannelHandler var1) {
      this.remove(this.getContextOrDie(var1));
      return this;
   }

   @Override
   public final ChannelHandler remove(String var1) {
      return this.remove(this.getContextOrDie(var1)).handler();
   }

   @Override
   public final <T extends ChannelHandler> T remove(Class<T> var1) {
      return (T)this.remove(this.getContextOrDie(var1)).handler();
   }

   public final <T extends ChannelHandler> T removeIfExists(String var1) {
      return this.removeIfExists(this.context(var1));
   }

   public final <T extends ChannelHandler> T removeIfExists(Class<T> var1) {
      return this.removeIfExists(this.context(var1));
   }

   public final <T extends ChannelHandler> T removeIfExists(ChannelHandler var1) {
      return this.removeIfExists(this.context(var1));
   }

   private <T extends ChannelHandler> T removeIfExists(ChannelHandlerContext var1) {
      return (T)(var1 == null ? null : this.remove((AbstractChannelHandlerContext)var1).handler());
   }

   private AbstractChannelHandlerContext remove(final AbstractChannelHandlerContext var1) {
      assert var1 != this.head && var1 != this.tail;

      synchronized (this) {
         this.atomicRemoveFromHandlerList(var1);
         if (!this.registered) {
            this.callHandlerCallbackLater(var1, false);
            return var1;
         }

         EventExecutor var3 = var1.executor();
         if (!var3.inEventLoop()) {
            var3.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.callHandlerRemoved0(var1);
               }
            });
            return var1;
         }
      }

      this.callHandlerRemoved0(var1);
      return var1;
   }

   private synchronized void atomicRemoveFromHandlerList(AbstractChannelHandlerContext var1) {
      AbstractChannelHandlerContext var2 = var1.prev;
      AbstractChannelHandlerContext var3 = var1.next;
      var2.next = var3;
      var3.prev = var2;
   }

   @Override
   public final ChannelHandler removeFirst() {
      if (this.head.next == this.tail) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.head.next).handler();
      }
   }

   @Override
   public final ChannelHandler removeLast() {
      if (this.head.next == this.tail) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.tail.prev).handler();
      }
   }

   @Override
   public final ChannelPipeline replace(ChannelHandler var1, String var2, ChannelHandler var3) {
      this.replace(this.getContextOrDie(var1), var2, var3);
      return this;
   }

   @Override
   public final ChannelHandler replace(String var1, String var2, ChannelHandler var3) {
      return this.replace(this.getContextOrDie(var1), var2, var3);
   }

   @Override
   public final <T extends ChannelHandler> T replace(Class<T> var1, String var2, ChannelHandler var3) {
      return (T)this.replace(this.getContextOrDie(var1), var2, var3);
   }

   private ChannelHandler replace(final AbstractChannelHandlerContext var1, String var2, ChannelHandler var3) {
      assert var1 != this.head && var1 != this.tail;

      final AbstractChannelHandlerContext var4;
      synchronized (this) {
         checkMultiplicity(var3);
         if (var2 == null) {
            var2 = this.generateName(var3);
         } else {
            boolean var6 = var1.name().equals(var2);
            if (!var6) {
               this.checkDuplicateName(var2);
            }
         }

         var4 = this.newContext(var1.executor, var2, var3);
         replace0(var1, var4);
         if (!this.registered) {
            this.callHandlerCallbackLater(var4, true);
            this.callHandlerCallbackLater(var1, false);
            return var1.handler();
         }

         EventExecutor var9 = var1.executor();
         if (!var9.inEventLoop()) {
            var9.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.callHandlerAdded0(var4);
                  DefaultChannelPipeline.this.callHandlerRemoved0(var1);
               }
            });
            return var1.handler();
         }
      }

      this.callHandlerAdded0(var4);
      this.callHandlerRemoved0(var1);
      return var1.handler();
   }

   private static void replace0(AbstractChannelHandlerContext var0, AbstractChannelHandlerContext var1) {
      AbstractChannelHandlerContext var2 = var0.prev;
      AbstractChannelHandlerContext var3 = var0.next;
      var1.prev = var2;
      var1.next = var3;
      var2.next = var1;
      var3.prev = var1;
      var0.prev = var1;
      var0.next = var1;
   }

   private static void checkMultiplicity(ChannelHandler var0) {
      if (var0 instanceof ChannelHandlerAdapter) {
         ChannelHandlerAdapter var1 = (ChannelHandlerAdapter)var0;
         if (!var1.isSharable() && var1.added) {
            throw new ChannelPipelineException(var1.getClass().getName() + " is not a @Sharable handler, so can't be added or removed multiple times.");
         }

         var1.added = true;
      }
   }

   private void callHandlerAdded0(AbstractChannelHandlerContext var1) {
      try {
         var1.callHandlerAdded();
      } catch (Throwable var6) {
         boolean var3 = false;

         try {
            this.atomicRemoveFromHandlerList(var1);
            var1.callHandlerRemoved();
            var3 = true;
         } catch (Throwable var5) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to remove a handler: " + var1.name(), var5);
            }
         }

         if (var3) {
            this.fireExceptionCaught(
               new ChannelPipelineException(var1.handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed.", var6)
            );
         } else {
            this.fireExceptionCaught(
               new ChannelPipelineException(var1.handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove.", var6)
            );
         }
      }
   }

   private void callHandlerRemoved0(AbstractChannelHandlerContext var1) {
      try {
         var1.callHandlerRemoved();
      } catch (Throwable var3) {
         this.fireExceptionCaught(new ChannelPipelineException(var1.handler().getClass().getName() + ".handlerRemoved() has thrown an exception.", var3));
      }
   }

   final void invokeHandlerAddedIfNeeded() {
      assert this.channel.eventLoop().inEventLoop();

      if (this.firstRegistration) {
         this.firstRegistration = false;
         this.callHandlerAddedForAllHandlers();
      }
   }

   @Override
   public final ChannelHandler first() {
      ChannelHandlerContext var1 = this.firstContext();
      return var1 == null ? null : var1.handler();
   }

   @Override
   public final ChannelHandlerContext firstContext() {
      AbstractChannelHandlerContext var1 = this.head.next;
      return var1 == this.tail ? null : this.head.next;
   }

   @Override
   public final ChannelHandler last() {
      AbstractChannelHandlerContext var1 = this.tail.prev;
      return var1 == this.head ? null : var1.handler();
   }

   @Override
   public final ChannelHandlerContext lastContext() {
      AbstractChannelHandlerContext var1 = this.tail.prev;
      return var1 == this.head ? null : var1;
   }

   @Override
   public final ChannelHandler get(String var1) {
      ChannelHandlerContext var2 = this.context(var1);
      return var2 == null ? null : var2.handler();
   }

   @Override
   public final <T extends ChannelHandler> T get(Class<T> var1) {
      ChannelHandlerContext var2 = this.context(var1);
      return (T)(var2 == null ? null : var2.handler());
   }

   @Override
   public final ChannelHandlerContext context(String var1) {
      return this.context0(ObjectUtil.checkNotNull(var1, "name"));
   }

   @Override
   public final ChannelHandlerContext context(ChannelHandler var1) {
      ObjectUtil.checkNotNull(var1, "handler");

      for (AbstractChannelHandlerContext var2 = this.head.next; var2 != null; var2 = var2.next) {
         if (var2.handler() == var1) {
            return var2;
         }
      }

      return null;
   }

   @Override
   public final ChannelHandlerContext context(Class<? extends ChannelHandler> var1) {
      ObjectUtil.checkNotNull(var1, "handlerType");

      for (AbstractChannelHandlerContext var2 = this.head.next; var2 != null; var2 = var2.next) {
         if (var1.isAssignableFrom(var2.handler().getClass())) {
            return var2;
         }
      }

      return null;
   }

   @Override
   public final List<String> names() {
      ArrayList var1 = new ArrayList();

      for (AbstractChannelHandlerContext var2 = this.head.next; var2 != null; var2 = var2.next) {
         var1.add(var2.name());
      }

      return var1;
   }

   @Override
   public final Map<String, ChannelHandler> toMap() {
      LinkedHashMap var1 = new LinkedHashMap();

      for (AbstractChannelHandlerContext var2 = this.head.next; var2 != this.tail; var2 = var2.next) {
         var1.put(var2.name(), var2.handler());
      }

      return var1;
   }

   @Override
   public final Iterator<Entry<String, ChannelHandler>> iterator() {
      return this.toMap().entrySet().iterator();
   }

   @Override
   public final String toString() {
      StringBuilder var1 = new StringBuilder().append(StringUtil.simpleClassName(this)).append('{');
      AbstractChannelHandlerContext var2 = this.head.next;

      while (var2 != this.tail) {
         var1.append('(').append(var2.name()).append(" = ").append(var2.handler().getClass().getName()).append(')');
         var2 = var2.next;
         if (var2 == this.tail) {
            break;
         }

         var1.append(", ");
      }

      var1.append('}');
      return var1.toString();
   }

   @Override
   public final ChannelPipeline fireChannelRegistered() {
      AbstractChannelHandlerContext.invokeChannelRegistered(this.head);
      return this;
   }

   @Override
   public final ChannelPipeline fireChannelUnregistered() {
      AbstractChannelHandlerContext.invokeChannelUnregistered(this.head);
      return this;
   }

   private synchronized void destroy() {
      this.destroyUp(this.head.next, false);
   }

   private void destroyUp(final AbstractChannelHandlerContext var1, boolean var2) {
      Thread var3 = Thread.currentThread();
      AbstractChannelHandlerContext var4 = this.tail;

      while (true) {
         if (var1 == var4) {
            this.destroyDown(var3, var4.prev, var2);
            break;
         }

         EventExecutor var5 = var1.executor();
         if (!var2 && !var5.inEventLoop(var3)) {
            var5.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.destroyUp(var1, true);
               }
            });
            break;
         }

         var1 = var1.next;
         var2 = false;
      }
   }

   private void destroyDown(Thread var1, final AbstractChannelHandlerContext var2, boolean var3) {
      AbstractChannelHandlerContext var4 = this.head;

      while (var2 != var4) {
         EventExecutor var5 = var2.executor();
         if (!var3 && !var5.inEventLoop(var1)) {
            var5.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.destroyDown(Thread.currentThread(), var2, true);
               }
            });
            break;
         }

         this.atomicRemoveFromHandlerList(var2);
         this.callHandlerRemoved0(var2);
         var2 = var2.prev;
         var3 = false;
      }
   }

   @Override
   public final ChannelPipeline fireChannelActive() {
      AbstractChannelHandlerContext.invokeChannelActive(this.head);
      return this;
   }

   @Override
   public final ChannelPipeline fireChannelInactive() {
      AbstractChannelHandlerContext.invokeChannelInactive(this.head);
      return this;
   }

   @Override
   public final ChannelPipeline fireExceptionCaught(Throwable var1) {
      AbstractChannelHandlerContext.invokeExceptionCaught(this.head, var1);
      return this;
   }

   @Override
   public final ChannelPipeline fireUserEventTriggered(Object var1) {
      AbstractChannelHandlerContext.invokeUserEventTriggered(this.head, var1);
      return this;
   }

   @Override
   public final ChannelPipeline fireChannelRead(Object var1) {
      AbstractChannelHandlerContext.invokeChannelRead(this.head, var1);
      return this;
   }

   @Override
   public final ChannelPipeline fireChannelReadComplete() {
      AbstractChannelHandlerContext.invokeChannelReadComplete(this.head);
      return this;
   }

   @Override
   public final ChannelPipeline fireChannelWritabilityChanged() {
      AbstractChannelHandlerContext.invokeChannelWritabilityChanged(this.head);
      return this;
   }

   @Override
   public final ChannelFuture bind(SocketAddress var1) {
      return this.tail.bind(var1);
   }

   @Override
   public final ChannelFuture connect(SocketAddress var1) {
      return this.tail.connect(var1);
   }

   @Override
   public final ChannelFuture connect(SocketAddress var1, SocketAddress var2) {
      return this.tail.connect(var1, var2);
   }

   @Override
   public final ChannelFuture disconnect() {
      return this.tail.disconnect();
   }

   @Override
   public final ChannelFuture close() {
      return this.tail.close();
   }

   @Override
   public final ChannelFuture deregister() {
      return this.tail.deregister();
   }

   @Override
   public final ChannelPipeline flush() {
      this.tail.flush();
      return this;
   }

   @Override
   public final ChannelFuture bind(SocketAddress var1, ChannelPromise var2) {
      return this.tail.bind(var1, var2);
   }

   @Override
   public final ChannelFuture connect(SocketAddress var1, ChannelPromise var2) {
      return this.tail.connect(var1, var2);
   }

   @Override
   public final ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
      return this.tail.connect(var1, var2, var3);
   }

   @Override
   public final ChannelFuture disconnect(ChannelPromise var1) {
      return this.tail.disconnect(var1);
   }

   @Override
   public final ChannelFuture close(ChannelPromise var1) {
      return this.tail.close(var1);
   }

   @Override
   public final ChannelFuture deregister(ChannelPromise var1) {
      return this.tail.deregister(var1);
   }

   public final ChannelPipeline read() {
      this.tail.read();
      return this;
   }

   @Override
   public final ChannelFuture write(Object var1) {
      return this.tail.write(var1);
   }

   @Override
   public final ChannelFuture write(Object var1, ChannelPromise var2) {
      return this.tail.write(var1, var2);
   }

   @Override
   public final ChannelFuture writeAndFlush(Object var1, ChannelPromise var2) {
      return this.tail.writeAndFlush(var1, var2);
   }

   @Override
   public final ChannelFuture writeAndFlush(Object var1) {
      return this.tail.writeAndFlush(var1);
   }

   @Override
   public final ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel);
   }

   @Override
   public final ChannelProgressivePromise newProgressivePromise() {
      return new DefaultChannelProgressivePromise(this.channel);
   }

   @Override
   public final ChannelFuture newSucceededFuture() {
      return this.succeededFuture;
   }

   @Override
   public final ChannelFuture newFailedFuture(Throwable var1) {
      return new FailedChannelFuture(this.channel, null, var1);
   }

   @Override
   public final ChannelPromise voidPromise() {
      return this.voidPromise;
   }

   private void checkDuplicateName(String var1) {
      if (this.context0(var1) != null) {
         throw new IllegalArgumentException("Duplicate handler name: " + var1);
      }
   }

   private AbstractChannelHandlerContext context0(String var1) {
      for (AbstractChannelHandlerContext var2 = this.head.next; var2 != this.tail; var2 = var2.next) {
         if (var2.name().equals(var1)) {
            return var2;
         }
      }

      return null;
   }

   private AbstractChannelHandlerContext getContextOrDie(String var1) {
      AbstractChannelHandlerContext var2 = (AbstractChannelHandlerContext)this.context(var1);
      if (var2 == null) {
         throw new NoSuchElementException(var1);
      } else {
         return var2;
      }
   }

   private AbstractChannelHandlerContext getContextOrDie(ChannelHandler var1) {
      AbstractChannelHandlerContext var2 = (AbstractChannelHandlerContext)this.context(var1);
      if (var2 == null) {
         throw new NoSuchElementException(var1.getClass().getName());
      } else {
         return var2;
      }
   }

   private AbstractChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> var1) {
      AbstractChannelHandlerContext var2 = (AbstractChannelHandlerContext)this.context(var1);
      if (var2 == null) {
         throw new NoSuchElementException(var1.getName());
      } else {
         return var2;
      }
   }

   private void callHandlerAddedForAllHandlers() {
      DefaultChannelPipeline.PendingHandlerCallback var1;
      synchronized (this) {
         assert !this.registered;

         this.registered = true;
         var1 = this.pendingHandlerCallbackHead;
         this.pendingHandlerCallbackHead = null;
      }

      for (DefaultChannelPipeline.PendingHandlerCallback var2 = var1; var2 != null; var2 = var2.next) {
         var2.execute();
      }
   }

   private void callHandlerCallbackLater(AbstractChannelHandlerContext var1, boolean var2) {
      assert !this.registered;

      Object var3 = var2 ? new DefaultChannelPipeline.PendingHandlerAddedTask(var1) : new DefaultChannelPipeline.PendingHandlerRemovedTask(var1);
      DefaultChannelPipeline.PendingHandlerCallback var4 = this.pendingHandlerCallbackHead;
      if (var4 == null) {
         this.pendingHandlerCallbackHead = (DefaultChannelPipeline.PendingHandlerCallback)var3;
      } else {
         while (var4.next != null) {
            var4 = var4.next;
         }

         var4.next = (DefaultChannelPipeline.PendingHandlerCallback)var3;
      }
   }

   private void callHandlerAddedInEventLoop(final AbstractChannelHandlerContext var1, EventExecutor var2) {
      var1.setAddPending();
      var2.execute(new Runnable() {
         @Override
         public void run() {
            DefaultChannelPipeline.this.callHandlerAdded0(var1);
         }
      });
   }

   protected void onUnhandledInboundException(Throwable var1) {
      try {
         logger.warn(
            "An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.",
            var1
         );
      } finally {
         ReferenceCountUtil.release(var1);
      }
   }

   protected void onUnhandledInboundChannelActive() {
   }

   protected void onUnhandledInboundChannelInactive() {
   }

   protected void onUnhandledInboundMessage(Object var1) {
      try {
         logger.debug("Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", var1);
      } finally {
         ReferenceCountUtil.release(var1);
      }
   }

   protected void onUnhandledInboundMessage(ChannelHandlerContext var1, Object var2) {
      this.onUnhandledInboundMessage(var2);
      if (logger.isDebugEnabled()) {
         logger.debug("Discarded message pipeline : {}. Channel : {}.", var1.pipeline().names(), var1.channel());
      }
   }

   protected void onUnhandledInboundChannelReadComplete() {
   }

   protected void onUnhandledInboundUserEventTriggered(Object var1) {
      ReferenceCountUtil.release(var1);
   }

   protected void onUnhandledChannelWritabilityChanged() {
   }

   protected void incrementPendingOutboundBytes(long var1) {
      ChannelOutboundBuffer var3 = this.channel.unsafe().outboundBuffer();
      if (var3 != null) {
         var3.incrementPendingOutboundBytes(var1);
      }
   }

   protected void decrementPendingOutboundBytes(long var1) {
      ChannelOutboundBuffer var3 = this.channel.unsafe().outboundBuffer();
      if (var3 != null) {
         var3.decrementPendingOutboundBytes(var1);
      }
   }

   final class HeadContext extends AbstractChannelHandlerContext implements ChannelInboundHandler, ChannelOutboundHandler {
      private final Channel.Unsafe unsafe;

      HeadContext(DefaultChannelPipeline var2) {
         super(var2, null, DefaultChannelPipeline.HEAD_NAME, DefaultChannelPipeline.HeadContext.class);
         this.unsafe = var2.channel().unsafe();
         this.setAddComplete();
      }

      @Override
      public ChannelHandler handler() {
         return this;
      }

      @Override
      public void handlerAdded(ChannelHandlerContext var1) {
      }

      @Override
      public void handlerRemoved(ChannelHandlerContext var1) {
      }

      @Override
      public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) {
         this.unsafe.bind(var2, var3);
      }

      @Override
      public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) {
         this.unsafe.connect(var2, var3, var4);
      }

      @Override
      public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) {
         this.unsafe.disconnect(var2);
      }

      @Override
      public void close(ChannelHandlerContext var1, ChannelPromise var2) {
         this.unsafe.close(var2);
      }

      @Override
      public void deregister(ChannelHandlerContext var1, ChannelPromise var2) {
         this.unsafe.deregister(var2);
      }

      @Override
      public void read(ChannelHandlerContext var1) {
         this.unsafe.beginRead();
      }

      @Override
      public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) {
         this.unsafe.write(var2, var3);
      }

      @Override
      public void flush(ChannelHandlerContext var1) {
         this.unsafe.flush();
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
         var1.fireExceptionCaught(var2);
      }

      @Override
      public void channelRegistered(ChannelHandlerContext var1) {
         DefaultChannelPipeline.this.invokeHandlerAddedIfNeeded();
         var1.fireChannelRegistered();
      }

      @Override
      public void channelUnregistered(ChannelHandlerContext var1) {
         var1.fireChannelUnregistered();
         if (!DefaultChannelPipeline.this.channel.isOpen()) {
            DefaultChannelPipeline.this.destroy();
         }
      }

      @Override
      public void channelActive(ChannelHandlerContext var1) {
         var1.fireChannelActive();
         this.readIfIsAutoRead();
      }

      @Override
      public void channelInactive(ChannelHandlerContext var1) {
         var1.fireChannelInactive();
      }

      @Override
      public void channelRead(ChannelHandlerContext var1, Object var2) {
         var1.fireChannelRead(var2);
      }

      @Override
      public void channelReadComplete(ChannelHandlerContext var1) {
         var1.fireChannelReadComplete();
         this.readIfIsAutoRead();
      }

      private void readIfIsAutoRead() {
         if (DefaultChannelPipeline.this.channel.config().isAutoRead()) {
            DefaultChannelPipeline.this.channel.read();
         }
      }

      @Override
      public void userEventTriggered(ChannelHandlerContext var1, Object var2) {
         var1.fireUserEventTriggered(var2);
      }

      @Override
      public void channelWritabilityChanged(ChannelHandlerContext var1) {
         var1.fireChannelWritabilityChanged();
      }
   }

   private final class PendingHandlerAddedTask extends DefaultChannelPipeline.PendingHandlerCallback {
      PendingHandlerAddedTask(AbstractChannelHandlerContext var2) {
         super(var2);
      }

      @Override
      public void run() {
         DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
      }

      @Override
      void execute() {
         EventExecutor var1 = this.ctx.executor();
         if (var1.inEventLoop()) {
            DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
         } else {
            try {
               var1.execute(this);
            } catch (RejectedExecutionException var3) {
               if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                  DefaultChannelPipeline.logger
                     .warn("Can't invoke handlerAdded() as the EventExecutor {} rejected it, removing handler {}.", var1, this.ctx.name(), var3);
               }

               DefaultChannelPipeline.this.atomicRemoveFromHandlerList(this.ctx);
               this.ctx.setRemoved();
            }
         }
      }
   }

   private abstract static class PendingHandlerCallback implements Runnable {
      final AbstractChannelHandlerContext ctx;
      DefaultChannelPipeline.PendingHandlerCallback next;

      PendingHandlerCallback(AbstractChannelHandlerContext var1) {
         this.ctx = var1;
      }

      abstract void execute();
   }

   private final class PendingHandlerRemovedTask extends DefaultChannelPipeline.PendingHandlerCallback {
      PendingHandlerRemovedTask(AbstractChannelHandlerContext var2) {
         super(var2);
      }

      @Override
      public void run() {
         DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
      }

      @Override
      void execute() {
         EventExecutor var1 = this.ctx.executor();
         if (var1.inEventLoop()) {
            DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
         } else {
            try {
               var1.execute(this);
            } catch (RejectedExecutionException var3) {
               if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                  DefaultChannelPipeline.logger
                     .warn("Can't invoke handlerRemoved() as the EventExecutor {} rejected it, removing handler {}.", var1, this.ctx.name(), var3);
               }

               this.ctx.setRemoved();
            }
         }
      }
   }

   final class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {
      TailContext(DefaultChannelPipeline var2) {
         super(var2, null, DefaultChannelPipeline.TAIL_NAME, DefaultChannelPipeline.TailContext.class);
         this.setAddComplete();
      }

      @Override
      public ChannelHandler handler() {
         return this;
      }

      @Override
      public void channelRegistered(ChannelHandlerContext var1) {
      }

      @Override
      public void channelUnregistered(ChannelHandlerContext var1) {
      }

      @Override
      public void channelActive(ChannelHandlerContext var1) {
         DefaultChannelPipeline.this.onUnhandledInboundChannelActive();
      }

      @Override
      public void channelInactive(ChannelHandlerContext var1) {
         DefaultChannelPipeline.this.onUnhandledInboundChannelInactive();
      }

      @Override
      public void channelWritabilityChanged(ChannelHandlerContext var1) {
         DefaultChannelPipeline.this.onUnhandledChannelWritabilityChanged();
      }

      @Override
      public void handlerAdded(ChannelHandlerContext var1) {
      }

      @Override
      public void handlerRemoved(ChannelHandlerContext var1) {
      }

      @Override
      public void userEventTriggered(ChannelHandlerContext var1, Object var2) {
         DefaultChannelPipeline.this.onUnhandledInboundUserEventTriggered(var2);
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
         DefaultChannelPipeline.this.onUnhandledInboundException(var2);
      }

      @Override
      public void channelRead(ChannelHandlerContext var1, Object var2) {
         DefaultChannelPipeline.this.onUnhandledInboundMessage(var1, var2);
      }

      @Override
      public void channelReadComplete(ChannelHandlerContext var1) {
         DefaultChannelPipeline.this.onUnhandledInboundChannelReadComplete();
      }
   }
}
