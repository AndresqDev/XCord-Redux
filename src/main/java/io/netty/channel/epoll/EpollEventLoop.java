package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

class EpollEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
   private static final long EPOLL_WAIT_MILLIS_THRESHOLD = SystemPropertyUtil.getLong("io.netty.channel.epoll.epollWaitThreshold", 10L);
   private final FileDescriptor epollFd;
   private final FileDescriptor eventFd;
   private final FileDescriptor timerFd;
   private final IntObjectMap<AbstractEpollChannel> channels = new IntObjectHashMap<>(4096);
   private final boolean allowGrowing;
   private final EpollEventArray events;
   private IovArray iovArray;
   private NativeDatagramPacketArray datagramPacketArray;
   private final SelectStrategy selectStrategy;
   private final IntSupplier selectNowSupplier = new IntSupplier() {
      @Override
      public int get() throws Exception {
         return EpollEventLoop.this.epollWaitNow();
      }
   };
   private static final long AWAKE = -1L;
   private static final long NONE = Long.MAX_VALUE;
   private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
   private boolean pendingWakeup;
   private volatile int ioRatio = 50;
   private static final long MAX_SCHEDULED_TIMERFD_NS = 999999999L;

   EpollEventLoop(
      EventLoopGroup var1,
      Executor var2,
      int var3,
      SelectStrategy var4,
      RejectedExecutionHandler var5,
      EventLoopTaskQueueFactory var6,
      EventLoopTaskQueueFactory var7
   ) {
      super(var1, var2, false, newTaskQueue(var6), newTaskQueue(var7), var5);
      this.selectStrategy = ObjectUtil.checkNotNull(var4, "strategy");
      if (var3 == 0) {
         this.allowGrowing = true;
         this.events = new EpollEventArray(4096);
      } else {
         this.allowGrowing = false;
         this.events = new EpollEventArray(var3);
      }

      boolean var8 = false;
      FileDescriptor var9 = null;
      FileDescriptor var10 = null;
      FileDescriptor var11 = null;

      try {
         this.epollFd = var9 = Native.newEpollCreate();
         this.eventFd = var10 = Native.newEventFd();

         try {
            Native.epollCtlAdd(var9.intValue(), var10.intValue(), Native.EPOLLIN | Native.EPOLLET);
         } catch (IOException var28) {
            throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", var28);
         }

         this.timerFd = var11 = Native.newTimerFd();

         try {
            Native.epollCtlAdd(var9.intValue(), var11.intValue(), Native.EPOLLIN | Native.EPOLLET);
         } catch (IOException var27) {
            throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", var27);
         }

         var8 = true;
      } finally {
         if (!var8) {
            if (var9 != null) {
               try {
                  var9.close();
               } catch (Exception var26) {
               }
            }

            if (var10 != null) {
               try {
                  var10.close();
               } catch (Exception var25) {
               }
            }

            if (var11 != null) {
               try {
                  var11.close();
               } catch (Exception var24) {
               }
            }
         }
      }
   }

   private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory var0) {
      return var0 == null ? newTaskQueue0(DEFAULT_MAX_PENDING_TASKS) : var0.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
   }

   IovArray cleanIovArray() {
      if (this.iovArray == null) {
         this.iovArray = new IovArray();
      } else {
         this.iovArray.clear();
      }

      return this.iovArray;
   }

   NativeDatagramPacketArray cleanDatagramPacketArray() {
      if (this.datagramPacketArray == null) {
         this.datagramPacketArray = new NativeDatagramPacketArray();
      } else {
         this.datagramPacketArray.clear();
      }

      return this.datagramPacketArray;
   }

   @Override
   protected void wakeup(boolean var1) {
      if (!var1 && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
         Native.eventFdWrite(this.eventFd.intValue(), 1L);
      }
   }

   @Override
   protected boolean beforeScheduledTaskSubmitted(long var1) {
      return var1 < this.nextWakeupNanos.get();
   }

   @Override
   protected boolean afterScheduledTaskSubmitted(long var1) {
      return var1 < this.nextWakeupNanos.get();
   }

   void add(AbstractEpollChannel var1) throws IOException {
      assert this.inEventLoop();

      int var2 = var1.socket.intValue();
      Native.epollCtlAdd(this.epollFd.intValue(), var2, var1.flags);
      AbstractEpollChannel var3 = this.channels.put(var2, var1);

      assert var3 == null || !var3.isOpen();
   }

   void modify(AbstractEpollChannel var1) throws IOException {
      assert this.inEventLoop();

      Native.epollCtlMod(this.epollFd.intValue(), var1.socket.intValue(), var1.flags);
   }

   void remove(AbstractEpollChannel var1) throws IOException {
      assert this.inEventLoop();

      int var2 = var1.socket.intValue();
      AbstractEpollChannel var3 = this.channels.remove(var2);
      if (var3 != null && var3 != var1) {
         this.channels.put(var2, var3);

         assert !var1.isOpen();
      } else if (var1.isOpen()) {
         Native.epollCtlDel(this.epollFd.intValue(), var2);
      }
   }

   @Override
   protected Queue<Runnable> newTaskQueue(int var1) {
      return newTaskQueue0(var1);
   }

   private static Queue<Runnable> newTaskQueue0(int var0) {
      return var0 == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(var0);
   }

   public int getIoRatio() {
      return this.ioRatio;
   }

   public void setIoRatio(int var1) {
      if (var1 > 0 && var1 <= 100) {
         this.ioRatio = var1;
      } else {
         throw new IllegalArgumentException("ioRatio: " + var1 + " (expected: 0 < ioRatio <= 100)");
      }
   }

   @Override
   public int registeredChannels() {
      return this.channels.size();
   }

   @Override
   public Iterator<Channel> registeredChannelsIterator() {
      assert this.inEventLoop();

      IntObjectMap var1 = this.channels;
      return (Iterator<Channel>)(var1.isEmpty()
         ? SingleThreadEventLoop.ChannelsReadOnlyIterator.empty()
         : new SingleThreadEventLoop.ChannelsReadOnlyIterator(var1.values()));
   }

   private long epollWait(long var1) throws IOException {
      if (var1 == Long.MAX_VALUE) {
         return Native.epollWait(this.epollFd, this.events, this.timerFd, Integer.MAX_VALUE, 0, EPOLL_WAIT_MILLIS_THRESHOLD);
      } else {
         long var3 = deadlineToDelayNanos(var1);
         int var5 = (int)Math.min(var3 / 1000000000L, 2147483647L);
         int var6 = (int)Math.min(var3 - (long)var5 * 1000000000L, 999999999L);
         return Native.epollWait(this.epollFd, this.events, this.timerFd, var5, var6, EPOLL_WAIT_MILLIS_THRESHOLD);
      }
   }

   private int epollWaitNoTimerChange() throws IOException {
      return Native.epollWait(this.epollFd, this.events, false);
   }

   private int epollWaitNow() throws IOException {
      return Native.epollWait(this.epollFd, this.events, true);
   }

   private int epollBusyWait() throws IOException {
      return Native.epollBusyWait(this.epollFd, this.events);
   }

   private int epollWaitTimeboxed() throws IOException {
      return Native.epollWait(this.epollFd, this.events, 1000);
   }

   @Override
   protected void run() {
      long var1 = Long.MAX_VALUE;

      while (true) {
         try {
            int var3 = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
            switch (var3) {
               case -3:
                  var3 = this.epollBusyWait();
                  break;
               case -2:
                  continue;
               case -1:
                  label459: {
                     if (this.pendingWakeup) {
                        var3 = this.epollWaitTimeboxed();
                        if (var3 != 0) {
                           break label459;
                        }

                        logger.warn("Missed eventfd write (not seen after > 1 second)");
                        this.pendingWakeup = false;
                        if (this.hasTasks()) {
                           break label459;
                        }
                     }

                     long var4 = this.nextScheduledTaskDeadlineNanos();
                     if (var4 == -1L) {
                        var4 = Long.MAX_VALUE;
                     }

                     this.nextWakeupNanos.set(var4);

                     try {
                        if (!this.hasTasks()) {
                           if (var4 == var1) {
                              var3 = this.epollWaitNoTimerChange();
                           } else {
                              long var6 = this.epollWait(var4);
                              var3 = Native.epollReady(var6);
                              var1 = Native.epollTimerWasUsed(var6) ? var4 : Long.MAX_VALUE;
                           }
                        }
                     } finally {
                        if (this.nextWakeupNanos.get() == -1L || this.nextWakeupNanos.getAndSet(-1L) == -1L) {
                           this.pendingWakeup = true;
                        }
                     }
                  }
            }

            int var61 = this.ioRatio;
            if (var61 == 100) {
               try {
                  if (var3 > 0 && this.processReady(this.events, var3)) {
                     var1 = Long.MAX_VALUE;
                  }
               } finally {
                  this.runAllTasks();
               }
            } else if (var3 > 0) {
               long var5 = System.nanoTime();

               try {
                  if (this.processReady(this.events, var3)) {
                     var1 = Long.MAX_VALUE;
                  }
               } finally {
                  long var11 = System.nanoTime() - var5;
                  this.runAllTasks(var11 * (long)(100 - var61) / (long)var61);
               }
            } else {
               this.runAllTasks(0L);
            }

            if (this.allowGrowing && var3 == this.events.length()) {
               this.events.increase();
            }
         } catch (Error var58) {
            throw var58;
         } catch (Throwable var59) {
            this.handleLoopException(var59);
         } finally {
            try {
               if (this.isShuttingDown()) {
                  this.closeAll();
                  if (this.confirmShutdown()) {
                     return;
                  }
               }
            } catch (Error var55) {
               throw var55;
            } catch (Throwable var56) {
               this.handleLoopException(var56);
            }
         }
      }
   }

   void handleLoopException(Throwable var1) {
      logger.warn("Unexpected exception in the selector loop.", var1);

      try {
         Thread.sleep(1000L);
      } catch (InterruptedException var3) {
      }
   }

   private void closeAll() {
      AbstractEpollChannel[] var1 = this.channels.values().toArray(new AbstractEpollChannel[0]);

      for (AbstractEpollChannel var5 : var1) {
         var5.unsafe().close(var5.unsafe().voidPromise());
      }
   }

   private boolean processReady(EpollEventArray var1, int var2) {
      boolean var3 = false;

      for (int var4 = 0; var4 < var2; var4++) {
         int var5 = var1.fd(var4);
         if (var5 == this.eventFd.intValue()) {
            this.pendingWakeup = false;
         } else if (var5 == this.timerFd.intValue()) {
            var3 = true;
         } else {
            long var6 = (long)var1.events(var4);
            AbstractEpollChannel var8 = this.channels.get(var5);
            if (var8 != null) {
               AbstractEpollChannel.AbstractEpollUnsafe var9 = (AbstractEpollChannel.AbstractEpollUnsafe)var8.unsafe();
               if ((var6 & (long)(Native.EPOLLERR | Native.EPOLLOUT)) != 0L) {
                  var9.epollOutReady();
               }

               if ((var6 & (long)(Native.EPOLLERR | Native.EPOLLIN)) != 0L) {
                  var9.epollInReady();
               }

               if ((var6 & (long)Native.EPOLLRDHUP) != 0L) {
                  var9.epollRdHupReady();
               }
            } else {
               try {
                  Native.epollCtlDel(this.epollFd.intValue(), var5);
               } catch (IOException var10) {
               }
            }
         }
      }

      return var3;
   }

   @Override
   protected void cleanup() {
      try {
         label100:
         while (this.pendingWakeup) {
            try {
               int var1 = this.epollWaitTimeboxed();
               if (var1 != 0) {
                  for (int var2 = 0; var2 < var1; var2++) {
                     if (this.events.fd(var2) == this.eventFd.intValue()) {
                        this.pendingWakeup = false;
                        continue label100;
                     }
                  }
                  continue;
               }
               break;
            } catch (IOException var12) {
            }
         }

         try {
            this.eventFd.close();
         } catch (IOException var11) {
            logger.warn("Failed to close the event fd.", (Throwable)var11);
         }

         try {
            this.timerFd.close();
         } catch (IOException var10) {
            logger.warn("Failed to close the timer fd.", (Throwable)var10);
         }

         try {
            this.epollFd.close();
         } catch (IOException var9) {
            logger.warn("Failed to close the epoll fd.", (Throwable)var9);
         }
      } finally {
         if (this.iovArray != null) {
            this.iovArray.release();
            this.iovArray = null;
         }

         if (this.datagramPacketArray != null) {
            this.datagramPacketArray.release();
            this.datagramPacketArray = null;
         }

         this.events.free();
      }
   }

   static {
      Epoll.ensureAvailability();
   }
}
