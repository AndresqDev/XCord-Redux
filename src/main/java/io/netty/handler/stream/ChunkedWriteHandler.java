package io.netty.handler.stream;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ChunkedWriteHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
   private final Queue<ChunkedWriteHandler.PendingWrite> queue = new ArrayDeque<>();
   private volatile ChannelHandlerContext ctx;

   public ChunkedWriteHandler() {
   }

   @Deprecated
   public ChunkedWriteHandler(int var1) {
      ObjectUtil.checkPositive(var1, "maxPendingWrites");
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   public void resumeTransfer() {
      final ChannelHandlerContext var1 = this.ctx;
      if (var1 != null) {
         if (var1.executor().inEventLoop()) {
            this.resumeTransfer0(var1);
         } else {
            var1.executor().execute(new Runnable() {
               @Override
               public void run() {
                  ChunkedWriteHandler.this.resumeTransfer0(var1);
               }
            });
         }
      }
   }

   private void resumeTransfer0(ChannelHandlerContext var1) {
      try {
         this.doFlush(var1);
      } catch (Exception var3) {
         logger.warn("Unexpected exception while sending chunks.", (Throwable)var3);
      }
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      this.queue.add(new ChunkedWriteHandler.PendingWrite(var2, var3));
   }

   @Override
   public void flush(ChannelHandlerContext var1) throws Exception {
      this.doFlush(var1);
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.doFlush(var1);
      var1.fireChannelInactive();
   }

   @Override
   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      if (var1.channel().isWritable()) {
         this.doFlush(var1);
      }

      var1.fireChannelWritabilityChanged();
   }

   private void discard(Throwable var1) {
      while (true) {
         ChunkedWriteHandler.PendingWrite var2 = this.queue.poll();
         if (var2 == null) {
            return;
         }

         Object var3 = var2.msg;
         if (var3 instanceof ChunkedInput) {
            ChunkedInput var4 = (ChunkedInput)var3;

            boolean var5;
            long var6;
            try {
               var5 = var4.isEndOfInput();
               var6 = var4.length();
               closeInput(var4);
            } catch (Exception var9) {
               closeInput(var4);
               var2.fail(var9);
               if (logger.isWarnEnabled()) {
                  logger.warn(ChunkedInput.class.getSimpleName() + " failed", (Throwable)var9);
               }
               continue;
            }

            if (!var5) {
               if (var1 == null) {
                  var1 = new ClosedChannelException();
               }

               var2.fail((Throwable)var1);
            } else {
               var2.success(var6);
            }
         } else {
            if (var1 == null) {
               var1 = new ClosedChannelException();
            }

            var2.fail((Throwable)var1);
         }
      }
   }

   private void doFlush(ChannelHandlerContext var1) {
      Channel var2 = var1.channel();
      if (!var2.isActive()) {
         this.discard(null);
      } else {
         boolean var3 = true;
         ByteBufAllocator var4 = var1.alloc();

         while (var2.isWritable()) {
            final ChunkedWriteHandler.PendingWrite var5 = this.queue.peek();
            if (var5 == null) {
               break;
            }

            if (var5.promise.isDone()) {
               this.queue.remove();
            } else {
               Object var6 = var5.msg;
               if (var6 instanceof ChunkedInput) {
                  ChunkedInput var7 = (ChunkedInput)var6;
                  Object var10 = null;

                  boolean var8;
                  boolean var9;
                  try {
                     var10 = var7.readChunk(var4);
                     var8 = var7.isEndOfInput();
                     if (var10 == null) {
                        var9 = !var8;
                     } else {
                        var9 = false;
                     }
                  } catch (Throwable var13) {
                     this.queue.remove();
                     if (var10 != null) {
                        ReferenceCountUtil.release(var10);
                     }

                     closeInput(var7);
                     var5.fail(var13);
                     break;
                  }

                  if (var9) {
                     break;
                  }

                  if (var10 == null) {
                     var10 = Unpooled.EMPTY_BUFFER;
                  }

                  if (var8) {
                     this.queue.remove();
                  }

                  ChannelFuture var11 = var1.writeAndFlush(var10);
                  if (var8) {
                     if (var11.isDone()) {
                        handleEndOfInputFuture(var11, var5);
                     } else {
                        var11.addListener(new ChannelFutureListener() {
                           public void operationComplete(ChannelFuture var1) {
                              ChunkedWriteHandler.handleEndOfInputFuture(var1, var5);
                           }
                        });
                     }
                  } else {
                     final boolean var12 = !var2.isWritable();
                     if (var11.isDone()) {
                        this.handleFuture(var11, var5, var12);
                     } else {
                        var11.addListener(new ChannelFutureListener() {
                           public void operationComplete(ChannelFuture var1) {
                              ChunkedWriteHandler.this.handleFuture(var1, var5, var12);
                           }
                        });
                     }
                  }

                  var3 = false;
               } else {
                  this.queue.remove();
                  var1.write(var6, var5.promise);
                  var3 = true;
               }

               if (!var2.isActive()) {
                  this.discard(new ClosedChannelException());
                  break;
               }
            }
         }

         if (var3) {
            var1.flush();
         }
      }
   }

   private static void handleEndOfInputFuture(ChannelFuture var0, ChunkedWriteHandler.PendingWrite var1) {
      ChunkedInput var2 = (ChunkedInput)var1.msg;
      if (!var0.isSuccess()) {
         closeInput(var2);
         var1.fail(var0.cause());
      } else {
         long var3 = var2.progress();
         long var5 = var2.length();
         closeInput(var2);
         var1.progress(var3, var5);
         var1.success(var5);
      }
   }

   private void handleFuture(ChannelFuture var1, ChunkedWriteHandler.PendingWrite var2, boolean var3) {
      ChunkedInput var4 = (ChunkedInput)var2.msg;
      if (!var1.isSuccess()) {
         closeInput(var4);
         var2.fail(var1.cause());
      } else {
         var2.progress(var4.progress(), var4.length());
         if (var3 && var1.channel().isWritable()) {
            this.resumeTransfer();
         }
      }
   }

   private static void closeInput(ChunkedInput<?> var0) {
      try {
         var0.close();
      } catch (Throwable var2) {
         if (logger.isWarnEnabled()) {
            logger.warn("Failed to close a chunked input.", var2);
         }
      }
   }

   private static final class PendingWrite {
      final Object msg;
      final ChannelPromise promise;

      PendingWrite(Object var1, ChannelPromise var2) {
         this.msg = var1;
         this.promise = var2;
      }

      void fail(Throwable var1) {
         ReferenceCountUtil.release(this.msg);
         this.promise.tryFailure(var1);
      }

      void success(long var1) {
         if (!this.promise.isDone()) {
            this.progress(var1, var1);
            this.promise.trySuccess();
         }
      }

      void progress(long var1, long var3) {
         if (this.promise instanceof ChannelProgressivePromise) {
            ((ChannelProgressivePromise)this.promise).tryProgress(var1, var3);
         }
      }
   }
}
