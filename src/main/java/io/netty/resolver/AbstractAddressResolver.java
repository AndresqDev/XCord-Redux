package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.net.SocketAddress;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.Collections;
import java.util.List;

public abstract class AbstractAddressResolver<T extends SocketAddress> implements AddressResolver<T> {
   private final EventExecutor executor;
   private final TypeParameterMatcher matcher;

   protected AbstractAddressResolver(EventExecutor var1) {
      this.executor = ObjectUtil.checkNotNull(var1, "executor");
      this.matcher = TypeParameterMatcher.find(this, AbstractAddressResolver.class, "T");
   }

   protected AbstractAddressResolver(EventExecutor var1, Class<? extends T> var2) {
      this.executor = ObjectUtil.checkNotNull(var1, "executor");
      this.matcher = TypeParameterMatcher.get(var2);
   }

   protected EventExecutor executor() {
      return this.executor;
   }

   @Override
   public boolean isSupported(SocketAddress var1) {
      return this.matcher.match(var1);
   }

   @Override
   public final boolean isResolved(SocketAddress var1) {
      if (!this.isSupported(var1)) {
         throw new UnsupportedAddressTypeException();
      } else {
         return this.doIsResolved((T)var1);
      }
   }

   protected abstract boolean doIsResolved(T var1);

   @Override
   public final Future<T> resolve(SocketAddress var1) {
      if (!this.isSupported(ObjectUtil.checkNotNull(var1, "address"))) {
         return this.executor().newFailedFuture(new UnsupportedAddressTypeException());
      } else if (this.isResolved(var1)) {
         return this.executor.newSucceededFuture((T)var1);
      } else {
         try {
            Promise var3 = this.executor().newPromise();
            this.doResolve((T)var1, var3);
            return var3;
         } catch (Exception var4) {
            return this.executor().newFailedFuture(var4);
         }
      }
   }

   @Override
   public final Future<T> resolve(SocketAddress var1, Promise<T> var2) {
      ObjectUtil.checkNotNull(var1, "address");
      ObjectUtil.checkNotNull(var2, "promise");
      if (!this.isSupported(var1)) {
         return var2.setFailure(new UnsupportedAddressTypeException());
      } else if (this.isResolved(var1)) {
         return (Future<T>)var2.setSuccess(var1);
      } else {
         try {
            this.doResolve((T)var1, var2);
            return var2;
         } catch (Exception var4) {
            return var2.setFailure(var4);
         }
      }
   }

   @Override
   public final Future<List<T>> resolveAll(SocketAddress var1) {
      if (!this.isSupported(ObjectUtil.checkNotNull(var1, "address"))) {
         return this.executor().newFailedFuture(new UnsupportedAddressTypeException());
      } else if (this.isResolved(var1)) {
         return this.executor.newSucceededFuture(Collections.singletonList((T)var1));
      } else {
         try {
            Promise var3 = this.executor().newPromise();
            this.doResolveAll((T)var1, var3);
            return var3;
         } catch (Exception var4) {
            return this.executor().newFailedFuture(var4);
         }
      }
   }

   @Override
   public final Future<List<T>> resolveAll(SocketAddress var1, Promise<List<T>> var2) {
      ObjectUtil.checkNotNull(var1, "address");
      ObjectUtil.checkNotNull(var2, "promise");
      if (!this.isSupported(var1)) {
         return var2.setFailure(new UnsupportedAddressTypeException());
      } else if (this.isResolved(var1)) {
         return var2.setSuccess(Collections.singletonList((T)var1));
      } else {
         try {
            this.doResolveAll((T)var1, var2);
            return var2;
         } catch (Exception var4) {
            return var2.setFailure(var4);
         }
      }
   }

   protected abstract void doResolve(T var1, Promise<T> var2) throws Exception;

   protected abstract void doResolveAll(T var1, Promise<List<T>> var2) throws Exception;

   @Override
   public void close() {
   }
}
