package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public interface ListeningScheduledExecutorService extends ListeningExecutorService, ScheduledExecutorService {
   ListenableScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

   default ListenableScheduledFuture<?> schedule(Runnable var1, Duration var2) {
      return this.schedule(var1, Internal.toNanosSaturated(var2), TimeUnit.NANOSECONDS);
   }

   <V> ListenableScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4);

   default <V> ListenableScheduledFuture<V> schedule(Callable<V> var1, Duration var2) {
      return this.schedule(var1, Internal.toNanosSaturated(var2), TimeUnit.NANOSECONDS);
   }

   ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

   default ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable var1, Duration var2, Duration var3) {
      return this.scheduleAtFixedRate(var1, Internal.toNanosSaturated(var2), Internal.toNanosSaturated(var3), TimeUnit.NANOSECONDS);
   }

   ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6);

   default ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, Duration var2, Duration var3) {
      return this.scheduleWithFixedDelay(var1, Internal.toNanosSaturated(var2), Internal.toNanosSaturated(var3), TimeUnit.NANOSECONDS);
   }
}
