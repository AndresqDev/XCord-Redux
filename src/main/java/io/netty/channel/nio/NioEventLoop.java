package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public final class NioEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
   private static final int CLEANUP_INTERVAL = 256;
   private static final boolean DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
   private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
   private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
   private final IntSupplier selectNowSupplier = new IntSupplier() {
      @Override
      public int get() throws Exception {
         return NioEventLoop.this.selectNow();
      }
   };
   private Selector selector;
   private Selector unwrappedSelector;
   private SelectedSelectionKeySet selectedKeys;
   private final SelectorProvider provider;
   private static final long AWAKE = -1L;
   private static final long NONE = Long.MAX_VALUE;
   private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
   private final SelectStrategy selectStrategy;
   private volatile int ioRatio = 50;
   private int cancelledKeys;
   private boolean needsToSelectAgain;

   NioEventLoop(
      NioEventLoopGroup var1,
      Executor var2,
      SelectorProvider var3,
      SelectStrategy var4,
      RejectedExecutionHandler var5,
      EventLoopTaskQueueFactory var6,
      EventLoopTaskQueueFactory var7
   ) {
      super(var1, var2, false, newTaskQueue(var6), newTaskQueue(var7), var5);
      this.provider = ObjectUtil.checkNotNull(var3, "selectorProvider");
      this.selectStrategy = ObjectUtil.checkNotNull(var4, "selectStrategy");
      NioEventLoop.SelectorTuple var8 = this.openSelector();
      this.selector = var8.selector;
      this.unwrappedSelector = var8.unwrappedSelector;
   }

   private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory var0) {
      return var0 == null ? newTaskQueue0(DEFAULT_MAX_PENDING_TASKS) : var0.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
   }

   private NioEventLoop.SelectorTuple openSelector() {
      final AbstractSelector var1;
      try {
         var1 = this.provider.openSelector();
      } catch (IOException var7) {
         throw new ChannelException("failed to open a new selector", var7);
      }

      if (DISABLE_KEY_SET_OPTIMIZATION) {
         return new NioEventLoop.SelectorTuple(var1);
      } else {
         Object var2 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
               } catch (Throwable var2) {
                  return var2;
               }
            }
         });
         if (var2 instanceof Class && ((Class)var2).isAssignableFrom(var1.getClass())) {
            final Class var8 = (Class)var2;
            final SelectedSelectionKeySet var4 = new SelectedSelectionKeySet();
            Object var5 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Field var1x = var8.getDeclaredField("selectedKeys");
                     Field var2 = var8.getDeclaredField("publicSelectedKeys");
                     if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                        long var3 = PlatformDependent.objectFieldOffset(var1x);
                        long var5 = PlatformDependent.objectFieldOffset(var2);
                        if (var3 != -1L && var5 != -1L) {
                           PlatformDependent.putObject(var1, var3, var4);
                           PlatformDependent.putObject(var1, var5, var4);
                           return null;
                        }
                     }

                     Throwable var9 = ReflectionUtil.trySetAccessible(var1x, true);
                     if (var9 != null) {
                        return var9;
                     } else {
                        var9 = ReflectionUtil.trySetAccessible(var2, true);
                        if (var9 != null) {
                           return var9;
                        } else {
                           var1x.set(var1, var4);
                           var2.set(var1, var4);
                           return null;
                        }
                     }
                  } catch (NoSuchFieldException var7) {
                     return var7;
                  } catch (IllegalAccessException var8x) {
                     return var8x;
                  }
               }
            });
            if (var5 instanceof Exception) {
               this.selectedKeys = null;
               Exception var6 = (Exception)var5;
               logger.trace("failed to instrument a special java.util.Set into: {}", var1, var6);
               return new NioEventLoop.SelectorTuple(var1);
            } else {
               this.selectedKeys = var4;
               logger.trace("instrumented a special java.util.Set into: {}", var1);
               return new NioEventLoop.SelectorTuple(var1, new SelectedSelectionKeySetSelector(var1, var4));
            }
         } else {
            if (var2 instanceof Throwable) {
               Throwable var3 = (Throwable)var2;
               logger.trace("failed to instrument a special java.util.Set into: {}", var1, var3);
            }

            return new NioEventLoop.SelectorTuple(var1);
         }
      }
   }

   public SelectorProvider selectorProvider() {
      return this.provider;
   }

   @Override
   protected Queue<Runnable> newTaskQueue(int var1) {
      return newTaskQueue0(var1);
   }

   private static Queue<Runnable> newTaskQueue0(int var0) {
      return var0 == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(var0);
   }

   public void register(final SelectableChannel var1, final int var2, final NioTask<?> var3) {
      ObjectUtil.checkNotNull(var1, "ch");
      if (var2 == 0) {
         throw new IllegalArgumentException("interestOps must be non-zero.");
      } else if ((var2 & ~var1.validOps()) != 0) {
         throw new IllegalArgumentException("invalid interestOps: " + var2 + "(validOps: " + var1.validOps() + ')');
      } else {
         ObjectUtil.checkNotNull(var3, "task");
         if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
         } else {
            if (this.inEventLoop()) {
               this.register0(var1, var2, var3);
            } else {
               try {
                  this.submit(new Runnable() {
                     @Override
                     public void run() {
                        NioEventLoop.this.register0(var1, var2, var3);
                     }
                  }).sync();
               } catch (InterruptedException var5) {
                  Thread.currentThread().interrupt();
               }
            }
         }
      }
   }

   private void register0(SelectableChannel var1, int var2, NioTask<?> var3) {
      try {
         var1.register(this.unwrappedSelector, var2, var3);
      } catch (Exception var5) {
         throw new EventLoopException("failed to register a channel", var5);
      }
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

   public void rebuildSelector() {
      if (!this.inEventLoop()) {
         this.execute(new Runnable() {
            @Override
            public void run() {
               NioEventLoop.this.rebuildSelector0();
            }
         });
      } else {
         this.rebuildSelector0();
      }
   }

   @Override
   public int registeredChannels() {
      return this.selector.keys().size() - this.cancelledKeys;
   }

   @Override
   public Iterator<Channel> registeredChannelsIterator() {
      assert this.inEventLoop();

      final Set var1 = this.selector.keys();
      return var1.isEmpty() ? SingleThreadEventLoop.ChannelsReadOnlyIterator.empty() : new Iterator<Channel>() {
         final Iterator<SelectionKey> selectionKeyIterator = ObjectUtil.checkNotNull(var1, "selectionKeys").iterator();
         Channel next;
         boolean isDone;

         @Override
         public boolean hasNext() {
            if (this.isDone) {
               return false;
            } else {
               Channel var1x = this.next;
               if (var1x == null) {
                  var1x = this.next = this.nextOrDone();
                  return var1x != null;
               } else {
                  return true;
               }
            }
         }

         public Channel next() {
            if (this.isDone) {
               throw new NoSuchElementException();
            } else {
               Channel var1x = this.next;
               if (var1x == null) {
                  var1x = this.nextOrDone();
                  if (var1x == null) {
                     throw new NoSuchElementException();
                  }
               }

               this.next = this.nextOrDone();
               return var1x;
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("remove");
         }

         private Channel nextOrDone() {
            Iterator var1x = this.selectionKeyIterator;

            while (var1x.hasNext()) {
               SelectionKey var2 = (SelectionKey)var1x.next();
               if (var2.isValid()) {
                  Object var3 = var2.attachment();
                  if (var3 instanceof AbstractNioChannel) {
                     return (AbstractNioChannel)var3;
                  }
               }
            }

            this.isDone = true;
            return null;
         }
      };
   }

   private void rebuildSelector0() {
      Selector var1 = this.selector;
      if (var1 != null) {
         NioEventLoop.SelectorTuple var2;
         try {
            var2 = this.openSelector();
         } catch (Exception var9) {
            logger.warn("Failed to create a new Selector.", (Throwable)var9);
            return;
         }

         int var3 = 0;

         for (SelectionKey var5 : var1.keys()) {
            Object var6 = var5.attachment();

            try {
               if (var5.isValid() && var5.channel().keyFor(var2.unwrappedSelector) == null) {
                  int var7 = var5.interestOps();
                  var5.cancel();
                  SelectionKey var13 = var5.channel().register(var2.unwrappedSelector, var7, var6);
                  if (var6 instanceof AbstractNioChannel) {
                     ((AbstractNioChannel)var6).selectionKey = var13;
                  }

                  var3++;
               }
            } catch (Exception var11) {
               logger.warn("Failed to re-register a Channel to the new Selector.", (Throwable)var11);
               if (var6 instanceof AbstractNioChannel) {
                  AbstractNioChannel var12 = (AbstractNioChannel)var6;
                  var12.unsafe().close(var12.unsafe().voidPromise());
               } else {
                  NioTask var8 = (NioTask)var6;
                  invokeChannelUnregistered(var8, var5, var11);
               }
            }
         }

         this.selector = var2.selector;
         this.unwrappedSelector = var2.unwrappedSelector;

         try {
            var1.close();
         } catch (Throwable var10) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to close the old Selector.", var10);
            }
         }

         if (logger.isInfoEnabled()) {
            logger.info("Migrated " + var3 + " channel(s) to the new Selector.");
         }
      }
   }

   @Override
   protected void run() {
      int var1 = 0;

      while (true) {
         label508:
         while (true) {
            label506:
            while (true) {
               label504:
               while (true) {
                  while (true) {
                     Throwable var4;
                     boolean var34;
                     try {
                        label512: {
                           int var2;
                           try {
                              var34 = false;
                              var34 = true;
                              var34 = false;
                              var2 = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                              switch (var2) {
                                 case -3:
                                 case -1:
                                    long var3 = this.nextScheduledTaskDeadlineNanos();
                                    if (var3 == -1L) {
                                       var3 = Long.MAX_VALUE;
                                    }

                                    this.nextWakeupNanos.set(var3);

                                    try {
                                       if (!this.hasTasks()) {
                                          var2 = this.select(var3);
                                       }
                                       break;
                                    } finally {
                                       this.nextWakeupNanos.lazySet(-1L);
                                    }
                                 case -2:
                                    break label508;
                              }
                           } catch (IOException var110) {
                              this.rebuildSelector0();
                              var1 = 0;
                              handleLoopException(var110);
                              var34 = false;
                              break label512;
                           }

                           var1++;
                           this.cancelledKeys = 0;
                           this.needsToSelectAgain = false;
                           int var115 = this.ioRatio;
                           if (var115 == 100) {
                              try {
                                 if (var2 > 0) {
                                    this.processSelectedKeys();
                                 }
                              } finally {
                                 var4 = this.runAllTasks();
                              }
                           } else if (var2 > 0) {
                              long var5 = System.nanoTime();

                              try {
                                 this.processSelectedKeys();
                              } finally {
                                 long var10 = System.nanoTime() - var5;
                                 var4 = this.runAllTasks(var10 * (long)(100 - var115) / (long)var115);
                              }
                           } else {
                              var4 = this.runAllTasks(0L);
                           }

                           if (var4 == false && var2 <= 0) {
                              if (this.unexpectedSelectorWakeup(var1)) {
                                 var1 = 0;
                                 var34 = false;
                              } else {
                                 var34 = false;
                              }
                              break label506;
                           }

                           if (var1 > 3 && logger.isDebugEnabled()) {
                              logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", var1 - 1, this.selector);
                           }

                           var1 = 0;
                           var34 = false;
                           break label506;
                        }
                     } catch (CancelledKeyException var111) {
                        if (logger.isDebugEnabled()) {
                           logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", this.selector, var111);
                           var34 = false;
                        } else {
                           var34 = false;
                        }
                        break;
                     } catch (Error var112) {
                        throw var112;
                     } catch (Throwable var113) {
                        handleLoopException(var113);
                        var34 = false;
                        break label504;
                     } finally {
                        if (var34) {
                           try {
                              if (this.isShuttingDown()) {
                                 this.closeAll();
                                 if (this.confirmShutdown()) {
                                    return;
                                 }
                              }
                           } catch (Error var95) {
                              throw var95;
                           } catch (Throwable var96) {
                              handleLoopException(var96);
                           }
                        }
                     }

                     try {
                        if (this.isShuttingDown()) {
                           this.closeAll();
                           if (this.confirmShutdown()) {
                              return;
                           }
                        }
                     } catch (Error var107) {
                        throw var107;
                     } catch (Throwable var108) {
                        var4 = var108;
                        handleLoopException(var108);
                     }
                  }

                  try {
                     if (this.isShuttingDown()) {
                        this.closeAll();
                        if (this.confirmShutdown()) {
                           return;
                        }
                     }
                  } catch (Error var101) {
                     throw var101;
                  } catch (Throwable var102) {
                     handleLoopException(var102);
                  }
               }

               try {
                  if (this.isShuttingDown()) {
                     this.closeAll();
                     if (this.confirmShutdown()) {
                        return;
                     }
                  }
               } catch (Error var99) {
                  throw var99;
               } catch (Throwable var100) {
                  handleLoopException(var100);
               }
            }

            try {
               if (this.isShuttingDown()) {
                  this.closeAll();
                  if (this.confirmShutdown()) {
                     return;
                  }
               }
            } catch (Error var103) {
               throw var103;
            } catch (Throwable var104) {
               handleLoopException(var104);
            }
         }

         try {
            if (this.isShuttingDown()) {
               this.closeAll();
               if (this.confirmShutdown()) {
                  return;
               }
            }
         } catch (Error var97) {
            throw var97;
         } catch (Throwable var98) {
            handleLoopException(var98);
         }
      }
   }

   private boolean unexpectedSelectorWakeup(int var1) {
      if (Thread.interrupted()) {
         if (logger.isDebugEnabled()) {
            logger.debug(
               "Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop."
            );
         }

         return true;
      } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && var1 >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
         logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", var1, this.selector);
         this.rebuildSelector();
         return true;
      } else {
         return false;
      }
   }

   private static void handleLoopException(Throwable var0) {
      logger.warn("Unexpected exception in the selector loop.", var0);

      try {
         Thread.sleep(1000L);
      } catch (InterruptedException var2) {
      }
   }

   private void processSelectedKeys() {
      if (this.selectedKeys != null) {
         this.processSelectedKeysOptimized();
      } else {
         this.processSelectedKeysPlain(this.selector.selectedKeys());
      }
   }

   @Override
   protected void cleanup() {
      try {
         this.selector.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a selector.", (Throwable)var2);
      }
   }

   void cancel(SelectionKey var1) {
      var1.cancel();
      this.cancelledKeys++;
      if (this.cancelledKeys >= 256) {
         this.cancelledKeys = 0;
         this.needsToSelectAgain = true;
      }
   }

   private void processSelectedKeysPlain(Set<SelectionKey> var1) {
      if (!var1.isEmpty()) {
         Iterator var2 = var1.iterator();

         while (true) {
            SelectionKey var3 = (SelectionKey)var2.next();
            Object var4 = var3.attachment();
            var2.remove();
            if (var4 instanceof AbstractNioChannel) {
               this.processSelectedKey(var3, (AbstractNioChannel)var4);
            } else {
               NioTask var5 = (NioTask)var4;
               processSelectedKey(var3, var5);
            }

            if (!var2.hasNext()) {
               break;
            }

            if (this.needsToSelectAgain) {
               this.selectAgain();
               var1 = this.selector.selectedKeys();
               if (var1.isEmpty()) {
                  break;
               }

               var2 = var1.iterator();
            }
         }
      }
   }

   private void processSelectedKeysOptimized() {
      for (int var1 = 0; var1 < this.selectedKeys.size; var1++) {
         SelectionKey var2 = this.selectedKeys.keys[var1];
         this.selectedKeys.keys[var1] = null;
         Object var3 = var2.attachment();
         if (var3 instanceof AbstractNioChannel) {
            this.processSelectedKey(var2, (AbstractNioChannel)var3);
         } else {
            NioTask var4 = (NioTask)var3;
            processSelectedKey(var2, var4);
         }

         if (this.needsToSelectAgain) {
            this.selectedKeys.reset(var1 + 1);
            this.selectAgain();
            var1 = -1;
         }
      }
   }

   private void processSelectedKey(SelectionKey var1, AbstractNioChannel var2) {
      AbstractNioChannel.NioUnsafe var3 = var2.unsafe();
      if (!var1.isValid()) {
         NioEventLoop var8;
         try {
            var8 = var2.eventLoop();
         } catch (Throwable var6) {
            return;
         }

         if (var8 == this) {
            var3.close(var3.voidPromise());
         }
      } else {
         try {
            int var4 = var1.readyOps();
            if ((var4 & 8) != 0) {
               int var5 = var1.interestOps();
               var5 &= -9;
               var1.interestOps(var5);
               var3.finishConnect();
            }

            if ((var4 & 4) != 0) {
               var2.unsafe().forceFlush();
            }

            if ((var4 & 17) != 0 || var4 == 0) {
               var3.read();
            }
         } catch (CancelledKeyException var7) {
            var3.close(var3.voidPromise());
         }
      }
   }

   private static void processSelectedKey(SelectionKey var0, NioTask<SelectableChannel> var1) {
      byte var2 = 0;

      try {
         var1.channelReady(var0.channel(), var0);
         var2 = 1;
      } catch (Exception var7) {
         var0.cancel();
         invokeChannelUnregistered(var1, var0, var7);
         var2 = 2;
      } finally {
         switch (var2) {
            case 0:
               var0.cancel();
               invokeChannelUnregistered(var1, var0, null);
               break;
            case 1:
               if (!var0.isValid()) {
                  invokeChannelUnregistered(var1, var0, null);
               }
         }
      }
   }

   private void closeAll() {
      this.selectAgain();
      Set var1 = this.selector.keys();
      ArrayList var2 = new ArrayList(var1.size());

      for (SelectionKey var4 : var1) {
         Object var5 = var4.attachment();
         if (var5 instanceof AbstractNioChannel) {
            var2.add((AbstractNioChannel)var5);
         } else {
            var4.cancel();
            NioTask var6 = (NioTask)var5;
            invokeChannelUnregistered(var6, var4, null);
         }
      }

      for (AbstractNioChannel var8 : var2) {
         var8.unsafe().close(var8.unsafe().voidPromise());
      }
   }

   private static void invokeChannelUnregistered(NioTask<SelectableChannel> var0, SelectionKey var1, Throwable var2) {
      try {
         var0.channelUnregistered(var1.channel(), var2);
      } catch (Exception var4) {
         logger.warn("Unexpected exception while running NioTask.channelUnregistered()", (Throwable)var4);
      }
   }

   @Override
   protected void wakeup(boolean var1) {
      if (!var1 && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
         this.selector.wakeup();
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

   Selector unwrappedSelector() {
      return this.unwrappedSelector;
   }

   int selectNow() throws IOException {
      return this.selector.selectNow();
   }

   private int select(long var1) throws IOException {
      if (var1 == Long.MAX_VALUE) {
         return this.selector.select();
      } else {
         long var3 = deadlineToDelayNanos(var1 + 995000L) / 1000000L;
         return var3 <= 0L ? this.selector.selectNow() : this.selector.select(var3);
      }
   }

   private void selectAgain() {
      this.needsToSelectAgain = false;

      try {
         this.selector.selectNow();
      } catch (Throwable var2) {
         logger.warn("Failed to update SelectionKeys.", var2);
      }
   }

   static {
      if (PlatformDependent.javaVersion() < 7) {
         String var0 = "sun.nio.ch.bugLevel";
         String var1 = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
         if (var1 == null) {
            try {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     System.setProperty("sun.nio.ch.bugLevel", "");
                     return null;
                  }
               });
            } catch (SecurityException var3) {
               logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", (Throwable)var3);
            }
         }
      }

      int var4 = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
      if (var4 < 3) {
         var4 = 0;
      }

      SELECTOR_AUTO_REBUILD_THRESHOLD = var4;
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noKeySetOptimization: {}", DISABLE_KEY_SET_OPTIMIZATION);
         logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", SELECTOR_AUTO_REBUILD_THRESHOLD);
      }
   }

   private static final class SelectorTuple {
      final Selector unwrappedSelector;
      final Selector selector;

      SelectorTuple(Selector var1) {
         this.unwrappedSelector = var1;
         this.selector = var1;
      }

      SelectorTuple(Selector var1, Selector var2) {
         this.unwrappedSelector = var1;
         this.selector = var2;
      }
   }
}
