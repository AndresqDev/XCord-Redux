package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AddressResolverGroup<T extends SocketAddress> implements Closeable {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
   private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap<>();
   private final Map<EventExecutor, GenericFutureListener<Future<Object>>> executorTerminationListeners = new IdentityHashMap<>();

   protected AddressResolverGroup() {
   }

   public AddressResolver<T> getResolver(final EventExecutor var1) {
      ObjectUtil.checkNotNull(var1, "executor");
      if (var1.isShuttingDown()) {
         throw new IllegalStateException("executor not accepting a task");
      } else {
         synchronized (this.resolvers) {
            AddressResolver var2 = this.resolvers.get(var1);
            if (var2 == null) {
               final AddressResolver var4;
               try {
                  var4 = this.newResolver(var1);
               } catch (Exception var7) {
                  throw new IllegalStateException("failed to create a new resolver", var7);
               }

               this.resolvers.put(var1, var4);
               FutureListener var5 = new FutureListener<Object>() {
                  @Override
                  public void operationComplete(Future<Object> var1x) {
                     synchronized (AddressResolverGroup.this.resolvers) {
                        AddressResolverGroup.this.resolvers.remove(var1);
                        AddressResolverGroup.this.executorTerminationListeners.remove(var1);
                     }

                     var4.close();
                  }
               };
               this.executorTerminationListeners.put(var1, var5);
               var1.terminationFuture().addListener(var5);
               var2 = var4;
            }

            return var2;
         }
      }
   }

   protected abstract AddressResolver<T> newResolver(EventExecutor var1) throws Exception;

   @Override
   public void close() {
      AddressResolver[] var1;
      Entry[] var2;
      synchronized (this.resolvers) {
         var1 = this.resolvers.values().toArray(new AddressResolver[0]);
         this.resolvers.clear();
         var2 = this.executorTerminationListeners.entrySet().toArray(new Entry[0]);
         this.executorTerminationListeners.clear();
      }

      for (Entry var6 : var2) {
         ((EventExecutor)var6.getKey()).terminationFuture().removeListener((GenericFutureListener<? extends Future<?>>)var6.getValue());
      }

      for (AddressResolver var14 : var1) {
         try {
            var14.close();
         } catch (Throwable var8) {
            logger.warn("Failed to close a resolver:", var8);
         }
      }
   }
}
