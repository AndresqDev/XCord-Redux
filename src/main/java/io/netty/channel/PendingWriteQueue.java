package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class PendingWriteQueue {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PendingWriteQueue.class);
   private static final int PENDING_WRITE_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.pendingWriteSizeOverhead", 64);
   private final ChannelOutboundInvoker invoker;
   private final EventExecutor executor;
   private final PendingBytesTracker tracker;
   private PendingWriteQueue.PendingWrite head;
   private PendingWriteQueue.PendingWrite tail;
   private int size;
   private long bytes;

   public PendingWriteQueue(ChannelHandlerContext var1) {
      this.tracker = PendingBytesTracker.newTracker(var1.channel());
      this.invoker = var1;
      this.executor = var1.executor();
   }

   public PendingWriteQueue(Channel var1) {
      this.tracker = PendingBytesTracker.newTracker(var1);
      this.invoker = var1;
      this.executor = var1.eventLoop();
   }

   public boolean isEmpty() {
      assert this.executor.inEventLoop();

      return this.head == null;
   }

   public int size() {
      assert this.executor.inEventLoop();

      return this.size;
   }

   public long bytes() {
      assert this.executor.inEventLoop();

      return this.bytes;
   }

   private int size(Object var1) {
      int var2 = this.tracker.size(var1);
      if (var2 < 0) {
         var2 = 0;
      }

      return var2 + PENDING_WRITE_OVERHEAD;
   }

   public void add(Object var1, ChannelPromise var2) {
      assert this.executor.inEventLoop();

      ObjectUtil.checkNotNull(var1, "msg");
      ObjectUtil.checkNotNull(var2, "promise");
      int var3 = this.size(var1);
      PendingWriteQueue.PendingWrite var4 = PendingWriteQueue.PendingWrite.newInstance(var1, var3, var2);
      PendingWriteQueue.PendingWrite var5 = this.tail;
      if (var5 == null) {
         this.tail = this.head = var4;
      } else {
         var5.next = var4;
         this.tail = var4;
      }

      this.size++;
      this.bytes += (long)var3;
      this.tracker.incrementPendingOutboundBytes(var4.size);
   }

   public ChannelFuture removeAndWriteAll() {
      assert this.executor.inEventLoop();

      if (this.isEmpty()) {
         return null;
      } else {
         ChannelPromise var1 = this.invoker.newPromise();
         PromiseCombiner var2 = new PromiseCombiner(this.executor);

         try {
            for (PendingWriteQueue.PendingWrite var3 = this.head; var3 != null; var3 = this.head) {
               this.head = this.tail = null;
               this.size = 0;
               this.bytes = 0L;

               while (var3 != null) {
                  PendingWriteQueue.PendingWrite var4 = var3.next;
                  Object var5 = var3.msg;
                  ChannelPromise var6 = var3.promise;
                  this.recycle(var3, false);
                  if (!(var6 instanceof VoidChannelPromise)) {
                     var2.add((Promise)var6);
                  }

                  this.invoker.write(var5, var6);
                  var3 = var4;
               }
            }

            var2.finish(var1);
         } catch (Throwable var7) {
            var1.setFailure(var7);
         }

         this.assertEmpty();
         return var1;
      }
   }

   public void removeAndFailAll(Throwable var1) {
      assert this.executor.inEventLoop();

      ObjectUtil.checkNotNull(var1, "cause");

      for (PendingWriteQueue.PendingWrite var2 = this.head; var2 != null; var2 = this.head) {
         this.head = this.tail = null;
         this.size = 0;
         this.bytes = 0L;

         while (var2 != null) {
            PendingWriteQueue.PendingWrite var3 = var2.next;
            ReferenceCountUtil.safeRelease(var2.msg);
            ChannelPromise var4 = var2.promise;
            this.recycle(var2, false);
            safeFail(var4, var1);
            var2 = var3;
         }
      }

      this.assertEmpty();
   }

   public void removeAndFail(Throwable var1) {
      assert this.executor.inEventLoop();

      ObjectUtil.checkNotNull(var1, "cause");
      PendingWriteQueue.PendingWrite var2 = this.head;
      if (var2 != null) {
         ReferenceCountUtil.safeRelease(var2.msg);
         ChannelPromise var3 = var2.promise;
         safeFail(var3, var1);
         this.recycle(var2, true);
      }
   }

   private void assertEmpty() {
      assert this.tail == null && this.head == null && this.size == 0;
   }

   public ChannelFuture removeAndWrite() {
      assert this.executor.inEventLoop();

      PendingWriteQueue.PendingWrite var1 = this.head;
      if (var1 == null) {
         return null;
      } else {
         Object var2 = var1.msg;
         ChannelPromise var3 = var1.promise;
         this.recycle(var1, true);
         return this.invoker.write(var2, var3);
      }
   }

   public ChannelPromise remove() {
      assert this.executor.inEventLoop();

      PendingWriteQueue.PendingWrite var1 = this.head;
      if (var1 == null) {
         return null;
      } else {
         ChannelPromise var2 = var1.promise;
         ReferenceCountUtil.safeRelease(var1.msg);
         this.recycle(var1, true);
         return var2;
      }
   }

   public Object current() {
      assert this.executor.inEventLoop();

      PendingWriteQueue.PendingWrite var1 = this.head;
      return var1 == null ? null : var1.msg;
   }

   private void recycle(PendingWriteQueue.PendingWrite var1, boolean var2) {
      PendingWriteQueue.PendingWrite var3 = var1.next;
      long var4 = var1.size;
      if (var2) {
         if (var3 == null) {
            this.head = this.tail = null;
            this.size = 0;
            this.bytes = 0L;
         } else {
            this.head = var3;
            this.size--;
            this.bytes -= var4;

            assert this.size > 0 && this.bytes >= 0L;
         }
      }

      var1.recycle();
      this.tracker.decrementPendingOutboundBytes(var4);
   }

   private static void safeFail(ChannelPromise var0, Throwable var1) {
      if (!(var0 instanceof VoidChannelPromise) && !var0.tryFailure(var1)) {
         logger.warn("Failed to mark a promise as failure because it's done already: {}", var0, var1);
      }
   }

   static final class PendingWrite {
      private static final ObjectPool<PendingWriteQueue.PendingWrite> RECYCLER = ObjectPool.newPool(
         new ObjectPool.ObjectCreator<PendingWriteQueue.PendingWrite>() {
            public PendingWriteQueue.PendingWrite newObject(ObjectPool.Handle<PendingWriteQueue.PendingWrite> var1) {
               return new PendingWriteQueue.PendingWrite(var1);
            }
         }
      );
      private final ObjectPool.Handle<PendingWriteQueue.PendingWrite> handle;
      private PendingWriteQueue.PendingWrite next;
      private long size;
      private ChannelPromise promise;
      private Object msg;

      private PendingWrite(ObjectPool.Handle<PendingWriteQueue.PendingWrite> var1) {
         this.handle = var1;
      }

      static PendingWriteQueue.PendingWrite newInstance(Object var0, int var1, ChannelPromise var2) {
         PendingWriteQueue.PendingWrite var3 = RECYCLER.get();
         var3.size = (long)var1;
         var3.msg = var0;
         var3.promise = var2;
         return var3;
      }

      private void recycle() {
         this.size = 0L;
         this.next = null;
         this.msg = null;
         this.promise = null;
         this.handle.recycle(this);
      }
   }
}
