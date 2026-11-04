package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

public final class EpollDatagramChannelConfig extends EpollChannelConfig implements DatagramChannelConfig {
   private boolean activeOnOpen;
   private volatile int maxDatagramSize;
   private volatile boolean gro;

   EpollDatagramChannelConfig(EpollDatagramChannel var1) {
      super(var1);
      this.setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048));
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(
         super.getOptions(),
         new ChannelOption[]{
            ChannelOption.SO_BROADCAST,
            ChannelOption.SO_RCVBUF,
            ChannelOption.SO_SNDBUF,
            ChannelOption.SO_REUSEADDR,
            ChannelOption.IP_MULTICAST_LOOP_DISABLED,
            ChannelOption.IP_MULTICAST_ADDR,
            ChannelOption.IP_MULTICAST_IF,
            ChannelOption.IP_MULTICAST_TTL,
            ChannelOption.IP_TOS,
            ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION,
            EpollChannelOption.SO_REUSEPORT,
            EpollChannelOption.IP_FREEBIND,
            EpollChannelOption.IP_TRANSPARENT,
            EpollChannelOption.IP_RECVORIGDSTADDR,
            EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE,
            EpollChannelOption.UDP_GRO
         }
      );
   }

   @Override
   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == ChannelOption.SO_BROADCAST) {
         return (T)this.isBroadcast();
      } else if (var1 == ChannelOption.SO_RCVBUF) {
         return (T)this.getReceiveBufferSize();
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         return (T)this.getSendBufferSize();
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         return (T)this.isReuseAddress();
      } else if (var1 == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
         return (T)this.isLoopbackModeDisabled();
      } else if (var1 == ChannelOption.IP_MULTICAST_ADDR) {
         return (T)this.getInterface();
      } else if (var1 == ChannelOption.IP_MULTICAST_IF) {
         return (T)this.getNetworkInterface();
      } else if (var1 == ChannelOption.IP_MULTICAST_TTL) {
         return (T)this.getTimeToLive();
      } else if (var1 == ChannelOption.IP_TOS) {
         return (T)this.getTrafficClass();
      } else if (var1 == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         return (T)this.activeOnOpen;
      } else if (var1 == EpollChannelOption.SO_REUSEPORT) {
         return (T)this.isReusePort();
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         return (T)this.isIpTransparent();
      } else if (var1 == EpollChannelOption.IP_FREEBIND) {
         return (T)this.isFreeBind();
      } else if (var1 == EpollChannelOption.IP_RECVORIGDSTADDR) {
         return (T)this.isIpRecvOrigDestAddr();
      } else if (var1 == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) {
         return (T)this.getMaxDatagramPayloadSize();
      } else {
         return (T)(var1 == EpollChannelOption.UDP_GRO ? this.isUdpGro() : super.getOption(var1));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.SO_BROADCAST) {
         this.setBroadcast((Boolean)var2);
      } else if (var1 == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress((Boolean)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
         this.setLoopbackModeDisabled((Boolean)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_ADDR) {
         this.setInterface((InetAddress)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_IF) {
         this.setNetworkInterface((NetworkInterface)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_TTL) {
         this.setTimeToLive((Integer)var2);
      } else if (var1 == ChannelOption.IP_TOS) {
         this.setTrafficClass((Integer)var2);
      } else if (var1 == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         this.setActiveOnOpen((Boolean)var2);
      } else if (var1 == EpollChannelOption.SO_REUSEPORT) {
         this.setReusePort((Boolean)var2);
      } else if (var1 == EpollChannelOption.IP_FREEBIND) {
         this.setFreeBind((Boolean)var2);
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         this.setIpTransparent((Boolean)var2);
      } else if (var1 == EpollChannelOption.IP_RECVORIGDSTADDR) {
         this.setIpRecvOrigDestAddr((Boolean)var2);
      } else if (var1 == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) {
         this.setMaxDatagramPayloadSize((Integer)var2);
      } else {
         if (var1 != EpollChannelOption.UDP_GRO) {
            return super.setOption(var1, var2);
         }

         this.setUdpGro((Boolean)var2);
      }

      return true;
   }

   private void setActiveOnOpen(boolean var1) {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("Can only changed before channel was registered");
      } else {
         this.activeOnOpen = var1;
      }
   }

   boolean getActiveOnOpen() {
      return this.activeOnOpen;
   }

   public EpollDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   @Deprecated
   public EpollDatagramChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   @Deprecated
   public EpollDatagramChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   public EpollDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollDatagramChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   public EpollDatagramChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   public EpollDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollDatagramChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollDatagramChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollDatagramChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   @Deprecated
   public EpollDatagramChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   @Override
   public int getSendBufferSize() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getSendBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setSendBufferSize(int var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setSendBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getReceiveBufferSize() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getReceiveBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReceiveBufferSize(int var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setReceiveBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getTrafficClass() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getTrafficClass();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setTrafficClass(int var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setTrafficClass(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public boolean isReuseAddress() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isReuseAddress();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReuseAddress(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setReuseAddress(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public boolean isBroadcast() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isBroadcast();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setBroadcast(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setBroadcast(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public boolean isLoopbackModeDisabled() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isLoopbackModeDisabled();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public DatagramChannelConfig setLoopbackModeDisabled(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setLoopbackModeDisabled(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getTimeToLive() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getTimeToLive();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setTimeToLive(int var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setTimeToLive(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public InetAddress getInterface() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getInterface();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setInterface(InetAddress var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setInterface(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public NetworkInterface getNetworkInterface() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getNetworkInterface();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setNetworkInterface(NetworkInterface var1) {
      try {
         EpollDatagramChannel var2 = (EpollDatagramChannel)this.channel;
         var2.socket.setNetworkInterface(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollDatagramChannelConfig setEpollMode(EpollMode var1) {
      super.setEpollMode(var1);
      return this;
   }

   public boolean isReusePort() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isReusePort();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReusePort(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setReusePort(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpTransparent() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isIpTransparent();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setIpTransparent(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setIpTransparent(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isFreeBind() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isIpFreeBind();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setFreeBind(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setIpFreeBind(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpRecvOrigDestAddr() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isIpRecvOrigDestAddr();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setIpRecvOrigDestAddr(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setIpRecvOrigDestAddr(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollDatagramChannelConfig setMaxDatagramPayloadSize(int var1) {
      this.maxDatagramSize = ObjectUtil.checkPositiveOrZero(var1, "maxDatagramSize");
      return this;
   }

   public int getMaxDatagramPayloadSize() {
      return this.maxDatagramSize;
   }

   public EpollDatagramChannelConfig setUdpGro(boolean var1) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setUdpGro(var1);
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }

      this.gro = var1;
      return this;
   }

   public boolean isUdpGro() {
      return this.gro;
   }

   public EpollDatagramChannelConfig setMaxMessagesPerWrite(int var1) {
      super.setMaxMessagesPerWrite(var1);
      return this;
   }
}
