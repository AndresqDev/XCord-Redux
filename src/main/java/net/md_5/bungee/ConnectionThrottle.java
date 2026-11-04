package net.md_5.bungee;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionThrottle {
   private final LoadingCache<InetAddress, AtomicInteger> throttle;
   private final int throttleLimit;

   public ConnectionThrottle(int var1, int var2) {
      this(Ticker.systemTicker(), var1, var2);
   }

   @VisibleForTesting
   ConnectionThrottle(Ticker var1, int var2, int var3) {
      this.throttle = CacheBuilder.newBuilder()
         .ticker(var1)
         .concurrencyLevel(Runtime.getRuntime().availableProcessors())
         .initialCapacity(100)
         .expireAfterWrite((long)var2, TimeUnit.MILLISECONDS)
         .build(new CacheLoader<InetAddress, AtomicInteger>() {
            public AtomicInteger load(InetAddress var1) throws Exception {
               return new AtomicInteger();
            }
         });
      this.throttleLimit = var3;
   }

   public void unthrottle(SocketAddress var1) {
      if (var1 instanceof InetSocketAddress) {
         InetAddress var2 = ((InetSocketAddress)var1).getAddress();
         AtomicInteger var3 = (AtomicInteger)this.throttle.getIfPresent(var2);
         if (var3 != null) {
            var3.decrementAndGet();
         }
      }
   }

   public boolean throttle(SocketAddress var1) {
      if (!(var1 instanceof InetSocketAddress)) {
         return false;
      } else {
         InetAddress var2 = ((InetSocketAddress)var1).getAddress();
         int var3 = ((AtomicInteger)this.throttle.getUnchecked(var2)).incrementAndGet();
         return var3 > this.throttleLimit;
      }
   }
}
