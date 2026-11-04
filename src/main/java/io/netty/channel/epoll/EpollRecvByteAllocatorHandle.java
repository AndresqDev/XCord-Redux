package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;

class EpollRecvByteAllocatorHandle extends RecvByteBufAllocator.DelegatingHandle implements RecvByteBufAllocator.ExtendedHandle {
   private final PreferredDirectByteBufAllocator preferredDirectByteBufAllocator = new PreferredDirectByteBufAllocator();
   private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
      @Override
      public boolean get() {
         return EpollRecvByteAllocatorHandle.this.maybeMoreDataToRead();
      }
   };
   private boolean isEdgeTriggered;
   private boolean receivedRdHup;

   EpollRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle var1) {
      super(var1);
   }

   final void receivedRdHup() {
      this.receivedRdHup = true;
   }

   final boolean isReceivedRdHup() {
      return this.receivedRdHup;
   }

   boolean maybeMoreDataToRead() {
      return this.isEdgeTriggered && this.lastBytesRead() > 0 || !this.isEdgeTriggered && this.lastBytesRead() == this.attemptedBytesRead();
   }

   final void edgeTriggered(boolean var1) {
      this.isEdgeTriggered = var1;
   }

   final boolean isEdgeTriggered() {
      return this.isEdgeTriggered;
   }

   @Override
   public final ByteBuf allocate(ByteBufAllocator var1) {
      this.preferredDirectByteBufAllocator.updateAllocator(var1);
      return this.delegate().allocate(this.preferredDirectByteBufAllocator);
   }

   @Override
   public final boolean continueReading(UncheckedBooleanSupplier var1) {
      return ((RecvByteBufAllocator.ExtendedHandle)this.delegate()).continueReading(var1);
   }

   @Override
   public final boolean continueReading() {
      return this.continueReading(this.defaultMaybeMoreDataSupplier);
   }
}
