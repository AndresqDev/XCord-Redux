package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class Recycler<T> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
   private static final Recycler.Handle<?> NOOP_HANDLE = new Recycler.Handle<Object>() {
      @Override
      public void recycle(Object var1) {
      }

      @Override
      public String toString() {
         return "NOOP_HANDLE";
      }
   };
   private static final int DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD = 4096;
   private static final int DEFAULT_MAX_CAPACITY_PER_THREAD;
   private static final int RATIO;
   private static final int DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD;
   private static final boolean BLOCKING_POOL;
   private final int maxCapacityPerThread;
   private final int interval;
   private final int chunkSize;
   private final FastThreadLocal<Recycler.LocalPool<T>> threadLocal = new FastThreadLocal<Recycler.LocalPool<T>>() {
      protected Recycler.LocalPool<T> initialValue() {
         return new Recycler.LocalPool<>(Recycler.this.maxCapacityPerThread, Recycler.this.interval, Recycler.this.chunkSize);
      }

      protected void onRemoval(Recycler.LocalPool<T> var1) throws Exception {
         super.onRemoval(var1);
         MessagePassingQueue var2 = var1.pooledHandles;
         var1.pooledHandles = null;
         var2.clear();
      }
   };

   protected Recycler() {
      this(DEFAULT_MAX_CAPACITY_PER_THREAD);
   }

   protected Recycler(int var1) {
      this(var1, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   @Deprecated
   protected Recycler(int var1, int var2) {
      this(var1, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   @Deprecated
   protected Recycler(int var1, int var2, int var3, int var4) {
      this(var1, var3, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   @Deprecated
   protected Recycler(int var1, int var2, int var3, int var4, int var5) {
      this(var1, var3, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   protected Recycler(int var1, int var2, int var3) {
      this.interval = Math.max(0, var2);
      if (var1 <= 0) {
         this.maxCapacityPerThread = 0;
         this.chunkSize = 0;
      } else {
         this.maxCapacityPerThread = Math.max(4, var1);
         this.chunkSize = Math.max(2, Math.min(var3, this.maxCapacityPerThread >> 1));
      }
   }

   public final T get() {
      if (this.maxCapacityPerThread == 0) {
         return this.newObject((Recycler.Handle<T>)NOOP_HANDLE);
      } else {
         Recycler.LocalPool var1 = this.threadLocal.get();
         Recycler.DefaultHandle var2 = var1.claim();
         Object var3;
         if (var2 == null) {
            var2 = var1.newHandle();
            if (var2 != null) {
               var3 = this.newObject(var2);
               var2.set(var3);
            } else {
               var3 = this.newObject((Recycler.Handle<T>)NOOP_HANDLE);
            }
         } else {
            var3 = var2.get();
         }

         return (T)var3;
      }
   }

   @Deprecated
   public final boolean recycle(T var1, Recycler.Handle<T> var2) {
      if (var2 == NOOP_HANDLE) {
         return false;
      } else {
         var2.recycle(var1);
         return true;
      }
   }

   final int threadLocalSize() {
      return this.threadLocal.get().pooledHandles.size();
   }

   protected abstract T newObject(Recycler.Handle<T> var1);

   static {
      int var0 = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacityPerThread", SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity", 4096));
      if (var0 < 0) {
         var0 = 4096;
      }

      DEFAULT_MAX_CAPACITY_PER_THREAD = var0;
      DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD = SystemPropertyUtil.getInt("io.netty.recycler.chunkSize", 32);
      RATIO = Math.max(0, SystemPropertyUtil.getInt("io.netty.recycler.ratio", 8));
      BLOCKING_POOL = SystemPropertyUtil.getBoolean("io.netty.recycler.blocking", false);
      if (logger.isDebugEnabled()) {
         if (DEFAULT_MAX_CAPACITY_PER_THREAD == 0) {
            logger.debug("-Dio.netty.recycler.maxCapacityPerThread: disabled");
            logger.debug("-Dio.netty.recycler.ratio: disabled");
            logger.debug("-Dio.netty.recycler.chunkSize: disabled");
            logger.debug("-Dio.netty.recycler.blocking: disabled");
         } else {
            logger.debug("-Dio.netty.recycler.maxCapacityPerThread: {}", DEFAULT_MAX_CAPACITY_PER_THREAD);
            logger.debug("-Dio.netty.recycler.ratio: {}", RATIO);
            logger.debug("-Dio.netty.recycler.chunkSize: {}", DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
            logger.debug("-Dio.netty.recycler.blocking: {}", BLOCKING_POOL);
         }
      }
   }

   private static final class BlockingMessageQueue<T> implements MessagePassingQueue<T> {
      private final Queue<T> deque;
      private final int maxCapacity;

      BlockingMessageQueue(int var1) {
         this.maxCapacity = var1;
         this.deque = new ArrayDeque<>();
      }

      @Override
      public synchronized boolean offer(T var1) {
         return this.deque.size() == this.maxCapacity ? false : this.deque.offer((T)var1);
      }

      @Override
      public synchronized T poll() {
         return this.deque.poll();
      }

      @Override
      public synchronized T peek() {
         return this.deque.peek();
      }

      @Override
      public synchronized int size() {
         return this.deque.size();
      }

      @Override
      public synchronized void clear() {
         this.deque.clear();
      }

      @Override
      public synchronized boolean isEmpty() {
         return this.deque.isEmpty();
      }

      @Override
      public int capacity() {
         return this.maxCapacity;
      }

      @Override
      public boolean relaxedOffer(T var1) {
         return this.offer((T)var1);
      }

      @Override
      public T relaxedPoll() {
         return this.poll();
      }

      @Override
      public T relaxedPeek() {
         return this.peek();
      }

      @Override
      public int drain(MessagePassingQueue.Consumer<T> var1, int var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int fill(MessagePassingQueue.Supplier<T> var1, int var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int drain(MessagePassingQueue.Consumer<T> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int fill(MessagePassingQueue.Supplier<T> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void drain(MessagePassingQueue.Consumer<T> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void fill(MessagePassingQueue.Supplier<T> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
         throw new UnsupportedOperationException();
      }
   }

   private static final class DefaultHandle<T> implements Recycler.Handle<T> {
      private static final int STATE_CLAIMED = 0;
      private static final int STATE_AVAILABLE = 1;
      private static final AtomicIntegerFieldUpdater<Recycler.DefaultHandle<?>> STATE_UPDATER;
      private volatile int state;
      private final Recycler.LocalPool<T> localPool;
      private T value;

      DefaultHandle(Recycler.LocalPool<T> var1) {
         this.localPool = var1;
      }

      @Override
      public void recycle(Object var1) {
         if (var1 != this.value) {
            throw new IllegalArgumentException("object does not belong to handle");
         } else {
            this.localPool.release(this);
         }
      }

      T get() {
         return this.value;
      }

      void set(T var1) {
         this.value = (T)var1;
      }

      boolean availableToClaim() {
         return this.state != 1 ? false : STATE_UPDATER.compareAndSet(this, 1, 0);
      }

      void toAvailable() {
         int var1 = STATE_UPDATER.getAndSet(this, 1);
         if (var1 == 1) {
            throw new IllegalStateException("Object has been recycled already.");
         }
      }

      static {
         AtomicIntegerFieldUpdater var0 = AtomicIntegerFieldUpdater.newUpdater(Recycler.DefaultHandle.class, "state");
         STATE_UPDATER = var0;
      }
   }

   public interface Handle<T> extends ObjectPool.Handle<T> {
   }

   private static final class LocalPool<T> {
      private final int ratioInterval;
      private volatile MessagePassingQueue<Recycler.DefaultHandle<T>> pooledHandles;
      private int ratioCounter;

      LocalPool(int var1, int var2, int var3) {
         this.ratioInterval = var2;
         if (Recycler.BLOCKING_POOL) {
            this.pooledHandles = new Recycler.BlockingMessageQueue<>(var1);
         } else {
            this.pooledHandles = (MessagePassingQueue<Recycler.DefaultHandle<T>>)PlatformDependent.<Recycler.DefaultHandle<T>>newMpscQueue(var3, var1);
         }

         this.ratioCounter = var2;
      }

      Recycler.DefaultHandle<T> claim() {
         MessagePassingQueue var1 = this.pooledHandles;
         if (var1 == null) {
            return null;
         } else {
            Recycler.DefaultHandle var2;
            do {
               var2 = (Recycler.DefaultHandle)var1.relaxedPoll();
            } while (var2 != null && !var2.availableToClaim());

            return var2;
         }
      }

      void release(Recycler.DefaultHandle<T> var1) {
         MessagePassingQueue var2 = this.pooledHandles;
         var1.toAvailable();
         if (var2 != null) {
            var2.relaxedOffer(var1);
         }
      }

      Recycler.DefaultHandle<T> newHandle() {
         if (++this.ratioCounter >= this.ratioInterval) {
            this.ratioCounter = 0;
            return new Recycler.DefaultHandle<>(this);
         } else {
            return null;
         }
      }
   }
}
