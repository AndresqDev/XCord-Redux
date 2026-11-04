package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class Cache<E> {
   private static final AtomicReferenceFieldUpdater<Cache.Entries, ScheduledFuture> FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      Cache.Entries.class, ScheduledFuture.class, "expirationFuture"
   );
   private static final ScheduledFuture<?> CANCELLED = new ScheduledFuture<Object>() {
      @Override
      public boolean cancel(boolean var1) {
         return false;
      }

      @Override
      public long getDelay(TimeUnit var1) {
         return Long.MIN_VALUE;
      }

      public int compareTo(Delayed var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isCancelled() {
         return true;
      }

      @Override
      public boolean isDone() {
         return true;
      }

      @Override
      public Object get() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object get(long var1, TimeUnit var3) {
         throw new UnsupportedOperationException();
      }
   };
   static final int MAX_SUPPORTED_TTL_SECS = (int)TimeUnit.DAYS.toSeconds(730L);
   private final ConcurrentMap<String, Cache<E>.Entries> resolveCache = PlatformDependent.newConcurrentHashMap();

   final void clear() {
      while (!this.resolveCache.isEmpty()) {
         Iterator var1 = this.resolveCache.entrySet().iterator();

         while (var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            var1.remove();
            ((Cache.Entries)var2.getValue()).clearAndCancel();
         }
      }
   }

   final boolean clear(String var1) {
      Cache.Entries var2 = this.resolveCache.remove(var1);
      return var2 != null && var2.clearAndCancel();
   }

   final List<? extends E> get(String var1) {
      Cache.Entries var2 = this.resolveCache.get(var1);
      return var2 == null ? null : var2.get();
   }

   final void cache(String var1, E var2, int var3, EventLoop var4) {
      Cache.Entries var5 = this.resolveCache.get(var1);
      if (var5 == null) {
         var5 = new Cache.Entries(var1);
         Cache.Entries var6 = this.resolveCache.putIfAbsent(var1, var5);
         if (var6 != null) {
            var5 = var6;
         }
      }

      var5.add((int)var2, var3, var4);
   }

   final int size() {
      return this.resolveCache.size();
   }

   protected abstract boolean shouldReplaceAll(E var1);

   protected void sortEntries(String var1, List<E> var2) {
   }

   protected abstract boolean equals(E var1, E var2);

   private final class Entries extends AtomicReference<List<E>> implements Runnable {
      private final String hostname;
      volatile ScheduledFuture<?> expirationFuture;

      Entries(String var2) {
         super(Collections.emptyList());
         this.hostname = var2;
      }

      void add(E var1, int var2, EventLoop var3) {
         if (Cache.this.shouldReplaceAll((E)var1)) {
            this.set(Collections.singletonList((E)var1));
            this.scheduleCacheExpirationIfNeeded(var2, var3);
         } else {
            while (true) {
               List var4 = this.get();
               if (!var4.isEmpty()) {
                  Object var5 = var4.get(0);
                  if (Cache.this.shouldReplaceAll((E)var5)) {
                     assert var4.size() == 1;

                     if (this.compareAndSet(var4, Collections.singletonList((E)var1))) {
                        this.scheduleCacheExpirationIfNeeded(var2, var3);
                        return;
                     }
                  } else {
                     ArrayList var6 = new ArrayList(var4.size() + 1);
                     int var7 = 0;
                     Object var8 = null;

                     while (true) {
                        Object var9 = var4.get(var7);
                        if (!Cache.this.equals((E)var1, (E)var9)) {
                           var6.add(var9);
                           if (++var7 < var4.size()) {
                              continue;
                           }
                        } else {
                           var8 = var9;
                           var6.add(var1);
                           var7++;

                           while (var7 < var4.size()) {
                              var6.add(var4.get(var7));
                              var7++;
                           }
                        }

                        if (var8 == null) {
                           var6.add(var1);
                        }

                        Cache.this.sortEntries(this.hostname, var6);
                        if (this.compareAndSet(var4, Collections.unmodifiableList(var6))) {
                           this.scheduleCacheExpirationIfNeeded(var2, var3);
                           return;
                        }
                        break;
                     }
                  }
               } else if (this.compareAndSet(var4, Collections.singletonList((E)var1))) {
                  this.scheduleCacheExpirationIfNeeded(var2, var3);
                  return;
               }
            }
         }
      }

      private void scheduleCacheExpirationIfNeeded(int var1, EventLoop var2) {
         while (true) {
            ScheduledFuture var3 = Cache.FUTURE_UPDATER.get(this);
            if (var3 != null && var3.getDelay(TimeUnit.SECONDS) <= (long)var1) {
               break;
            }

            io.netty.util.concurrent.ScheduledFuture var4 = var2.schedule(this, (long)var1, TimeUnit.SECONDS);
            if (Cache.FUTURE_UPDATER.compareAndSet(this, var3, var4)) {
               if (var3 != null) {
                  var3.cancel(true);
               }
               break;
            }

            var4.cancel(true);
         }
      }

      boolean clearAndCancel() {
         List var1 = this.getAndSet(Collections.emptyList());
         if (var1.isEmpty()) {
            return false;
         } else {
            ScheduledFuture var2 = Cache.FUTURE_UPDATER.getAndSet(this, Cache.CANCELLED);
            if (var2 != null) {
               var2.cancel(false);
            }

            return true;
         }
      }

      @Override
      public void run() {
         Cache.this.resolveCache.remove(this.hostname, this);
         this.clearAndCancel();
      }
   }
}
