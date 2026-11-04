package io.netty.channel;

import java.net.SocketAddress;

public abstract class AbstractServerChannel extends AbstractChannel implements ServerChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);

   protected AbstractServerChannel() {
      super(null);
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public SocketAddress remoteAddress() {
      return null;
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return null;
   }

   @Override
   protected void doDisconnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new AbstractServerChannel.DefaultServerUnsafe();
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected final Object filterOutboundMessage(Object var1) {
      throw new UnsupportedOperationException();
   }

   private final class DefaultServerUnsafe extends AbstractChannel.AbstractUnsafe {
      private DefaultServerUnsafe() {
      }

      @Override
      public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         this.safeSetFailure(var3, new UnsupportedOperationException());
      }
   }
}
