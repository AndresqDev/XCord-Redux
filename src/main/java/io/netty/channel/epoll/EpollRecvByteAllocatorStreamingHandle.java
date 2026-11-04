package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;

final class EpollRecvByteAllocatorStreamingHandle extends EpollRecvByteAllocatorHandle {
   EpollRecvByteAllocatorStreamingHandle(RecvByteBufAllocator.ExtendedHandle var1) {
      super(var1);
   }

   @Override
   boolean maybeMoreDataToRead() {
      return this.lastBytesRead() == this.attemptedBytesRead() || this.isReceivedRdHup();
   }
}
