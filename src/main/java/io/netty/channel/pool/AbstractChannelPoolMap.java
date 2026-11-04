package io.netty.channel.pool;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannelPoolMap<K, P extends ChannelPool> implements ChannelPoolMap<K, P>, Closeable, Iterable<Entry<K, P>> {
   private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();

   @Override
   public final P get(K var1) {
      ChannelPool var2 = this.map.get(ObjectUtil.checkNotNull(var1, "key"));
      if (var2 == null) {
         var2 = this.newPool((K)var1);
         ChannelPool var3 = this.map.putIfAbsent((K)var1, (P)var2);
         if (var3 != null) {
            poolCloseAsyncIfSupported(var2);
            var2 = var3;
         }
      }

      return (P)var2;
   }

   public final boolean remove(K var1) {
      ChannelPool var2 = this.map.remove(ObjectUtil.checkNotNull(var1, "key"));
      if (var2 != null) {
         poolCloseAsyncIfSupported(var2);
         return true;
      } else {
         return false;
      }
   }

   private Future<Boolean> removeAsyncIfSupported(K var1) {
      ChannelPool var2 = this.map.remove(ObjectUtil.checkNotNull(var1, "key"));
      if (var2 != null) {
         final Promise var3 = GlobalEventExecutor.INSTANCE.newPromise();
         poolCloseAsyncIfSupported(var2).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> var1) throws Exception {
               if (var1.isSuccess()) {
                  var3.setSuccess(Boolean.TRUE);
               } else {
                  var3.setFailure(var1.cause());
               }
            }
         });
         return var3;
      } else {
         return GlobalEventExecutor.INSTANCE.newSucceededFuture(Boolean.FALSE);
      }
   }

   private static Future<Void> poolCloseAsyncIfSupported(ChannelPool var0) {
      if (var0 instanceof SimpleChannelPool) {
         return ((SimpleChannelPool)var0).closeAsync();
      } else {
         try {
            var0.close();
            return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
         } catch (Exception var2) {
            return GlobalEventExecutor.INSTANCE.newFailedFuture(var2);
         }
      }
   }

   @Override
   public final Iterator<Entry<K, P>> iterator() {
      return new ReadOnlyIterator<>(this.map.entrySet().iterator());
   }

   public final int size() {
      return this.map.size();
   }

   public final boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public final boolean contains(K var1) {
      return this.map.containsKey(ObjectUtil.checkNotNull(var1, "key"));
   }

   protected abstract P newPool(K var1);

   @Override
   public final void close() {
      for (Object var2 : this.map.keySet()) {
         this.removeAsyncIfSupported((K)var2).syncUninterruptibly();
      }
   }
}
