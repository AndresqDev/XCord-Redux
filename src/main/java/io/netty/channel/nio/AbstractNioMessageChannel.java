package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel extends AbstractNioChannel {
   boolean inputShutdown;

   protected AbstractNioMessageChannel(Channel var1, SelectableChannel var2, int var3) {
      super(var1, var2, var3);
   }

   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
      return new AbstractNioMessageChannel.NioMessageUnsafe();
   }

   @Override
   protected void doBeginRead() throws Exception {
      if (!this.inputShutdown) {
         super.doBeginRead();
      }
   }

   protected boolean continueReading(RecvByteBufAllocator.Handle var1) {
      return var1.continueReading();
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      SelectionKey var2 = this.selectionKey();
      int var3 = var2.interestOps();
      int var4 = this.maxMessagesPerWrite();

      while (var4 > 0) {
         Object var5 = var1.current();
         if (var5 == null) {
            break;
         }

         try {
            boolean var6 = false;

            for (int var7 = this.config().getWriteSpinCount() - 1; var7 >= 0; var7--) {
               if (this.doWriteMessage(var5, var1)) {
                  var6 = true;
                  break;
               }
            }

            if (!var6) {
               break;
            }

            var4--;
            var1.remove();
         } catch (Exception var8) {
            if (!this.continueOnWriteError()) {
               throw var8;
            }

            var4--;
            var1.remove(var8);
         }
      }

      if (var1.isEmpty()) {
         if ((var3 & 4) != 0) {
            var2.interestOps(var3 & -5);
         }
      } else if ((var3 & 4) == 0) {
         var2.interestOps(var3 | 4);
      }
   }

   protected boolean continueOnWriteError() {
      return false;
   }

   protected boolean closeOnReadError(Throwable var1) {
      if (!this.isActive()) {
         return true;
      } else if (var1 instanceof PortUnreachableException) {
         return false;
      } else {
         return var1 instanceof IOException ? !(this instanceof ServerChannel) : true;
      }
   }

   protected abstract int doReadMessages(List<Object> var1) throws Exception;

   protected abstract boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception;

   private final class NioMessageUnsafe extends AbstractNioChannel.AbstractNioUnsafe {
      private final List<Object> readBuf = new ArrayList<>();

      private NioMessageUnsafe() {
      }

      @Override
      public void read() {
         assert AbstractNioMessageChannel.this.eventLoop().inEventLoop();

         ChannelConfig var1 = AbstractNioMessageChannel.this.config();
         ChannelPipeline var2 = AbstractNioMessageChannel.this.pipeline();
         RecvByteBufAllocator.Handle var3 = AbstractNioMessageChannel.this.unsafe().recvBufAllocHandle();
         var3.reset(var1);
         boolean var4 = false;
         Throwable var5 = null;

         try {
            try {
               do {
                  int var6 = AbstractNioMessageChannel.this.doReadMessages(this.readBuf);
                  if (var6 == 0) {
                     break;
                  }

                  if (var6 < 0) {
                     var4 = true;
                     break;
                  }

                  var3.incMessagesRead(var6);
               } while (AbstractNioMessageChannel.this.continueReading(var3));
            } catch (Throwable var11) {
               var5 = var11;
            }

            int var13 = this.readBuf.size();

            for (int var7 = 0; var7 < var13; var7++) {
               AbstractNioMessageChannel.this.readPending = false;
               var2.fireChannelRead(this.readBuf.get(var7));
            }

            this.readBuf.clear();
            var3.readComplete();
            var2.fireChannelReadComplete();
            if (var5 != null) {
               var4 = AbstractNioMessageChannel.this.closeOnReadError(var5);
               var2.fireExceptionCaught(var5);
            }

            if (var4) {
               AbstractNioMessageChannel.this.inputShutdown = true;
               if (AbstractNioMessageChannel.this.isOpen()) {
                  this.close(this.voidPromise());
               }
            }
         } finally {
            if (!AbstractNioMessageChannel.this.readPending && !var1.isAutoRead()) {
               this.removeReadOp();
            }
         }
      }
   }
}
