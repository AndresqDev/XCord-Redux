package io.netty.channel.epoll;

import io.netty.channel.ChannelException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public final class LinuxSocket extends Socket {
   static final InetAddress INET6_ANY = unsafeInetAddrByName("::");
   private static final InetAddress INET_ANY = unsafeInetAddrByName("0.0.0.0");
   private static final long MAX_UINT32_T = 4294967295L;

   LinuxSocket(int var1) {
      super(var1);
   }

   InternetProtocolFamily family() {
      return this.ipv6 ? InternetProtocolFamily.IPv6 : InternetProtocolFamily.IPv4;
   }

   int sendmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] var1, int var2, int var3) throws IOException {
      return Native.sendmmsg(this.intValue(), this.ipv6, var1, var2, var3);
   }

   int recvmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] var1, int var2, int var3) throws IOException {
      return Native.recvmmsg(this.intValue(), this.ipv6, var1, var2, var3);
   }

   int recvmsg(NativeDatagramPacketArray.NativeDatagramPacket var1) throws IOException {
      return Native.recvmsg(this.intValue(), this.ipv6, var1);
   }

   void setTimeToLive(int var1) throws IOException {
      setTimeToLive(this.intValue(), var1);
   }

   void setInterface(InetAddress var1) throws IOException {
      NativeInetAddress var2 = NativeInetAddress.newInstance(var1);
      setInterface(this.intValue(), this.ipv6, var2.address(), var2.scopeId(), interfaceIndex(var1));
   }

   void setNetworkInterface(NetworkInterface var1) throws IOException {
      InetAddress var2 = deriveInetAddress(var1, this.family() == InternetProtocolFamily.IPv6);
      if (var2.equals(this.family() == InternetProtocolFamily.IPv4 ? INET_ANY : INET6_ANY)) {
         throw new IOException("NetworkInterface does not support " + this.family());
      } else {
         NativeInetAddress var3 = NativeInetAddress.newInstance(var2);
         setInterface(this.intValue(), this.ipv6, var3.address(), var3.scopeId(), interfaceIndex(var1));
      }
   }

   InetAddress getInterface() throws IOException {
      NetworkInterface var1 = this.getNetworkInterface();
      if (var1 != null) {
         Enumeration var2 = SocketUtils.addressesFromNetworkInterface(var1);
         if (var2.hasMoreElements()) {
            return (InetAddress)var2.nextElement();
         }
      }

      return null;
   }

   NetworkInterface getNetworkInterface() throws IOException {
      int var1 = getInterface(this.intValue(), this.ipv6);
      if (this.ipv6) {
         return PlatformDependent.javaVersion() >= 7 ? NetworkInterface.getByIndex(var1) : null;
      } else {
         InetAddress var2 = inetAddress(var1);
         return var2 != null ? NetworkInterface.getByInetAddress(var2) : null;
      }
   }

   private static InetAddress inetAddress(int var0) {
      byte[] var1 = new byte[]{(byte)(var0 >>> 24 & 0xFF), (byte)(var0 >>> 16 & 0xFF), (byte)(var0 >>> 8 & 0xFF), (byte)(var0 & 0xFF)};

      try {
         return InetAddress.getByAddress(var1);
      } catch (UnknownHostException var3) {
         return null;
      }
   }

   void joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) throws IOException {
      NativeInetAddress var4 = NativeInetAddress.newInstance(var1);
      boolean var5 = var1 instanceof Inet6Address;
      NativeInetAddress var6 = NativeInetAddress.newInstance(deriveInetAddress(var2, var5));
      if (var3 != null) {
         if (var3.getClass() != var1.getClass()) {
            throw new IllegalArgumentException("Source address is different type to group");
         }

         NativeInetAddress var7 = NativeInetAddress.newInstance(var3);
         joinSsmGroup(this.intValue(), this.ipv6 && var5, var4.address(), var6.address(), var4.scopeId(), interfaceIndex(var2), var7.address());
      } else {
         joinGroup(this.intValue(), this.ipv6 && var5, var4.address(), var6.address(), var4.scopeId(), interfaceIndex(var2));
      }
   }

   void leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) throws IOException {
      NativeInetAddress var4 = NativeInetAddress.newInstance(var1);
      boolean var5 = var1 instanceof Inet6Address;
      NativeInetAddress var6 = NativeInetAddress.newInstance(deriveInetAddress(var2, var5));
      if (var3 != null) {
         if (var3.getClass() != var1.getClass()) {
            throw new IllegalArgumentException("Source address is different type to group");
         }

         NativeInetAddress var7 = NativeInetAddress.newInstance(var3);
         leaveSsmGroup(this.intValue(), this.ipv6 && var5, var4.address(), var6.address(), var4.scopeId(), interfaceIndex(var2), var7.address());
      } else {
         leaveGroup(this.intValue(), this.ipv6 && var5, var4.address(), var6.address(), var4.scopeId(), interfaceIndex(var2));
      }
   }

   private static int interfaceIndex(NetworkInterface var0) {
      return PlatformDependent.javaVersion() >= 7 ? var0.getIndex() : -1;
   }

   private static int interfaceIndex(InetAddress var0) throws IOException {
      if (PlatformDependent.javaVersion() >= 7) {
         NetworkInterface var1 = NetworkInterface.getByInetAddress(var0);
         if (var1 != null) {
            return var1.getIndex();
         }
      }

      return -1;
   }

   void setTcpDeferAccept(int var1) throws IOException {
      setTcpDeferAccept(this.intValue(), var1);
   }

   void setTcpQuickAck(boolean var1) throws IOException {
      setTcpQuickAck(this.intValue(), var1 ? 1 : 0);
   }

   void setTcpCork(boolean var1) throws IOException {
      setTcpCork(this.intValue(), var1 ? 1 : 0);
   }

   void setSoBusyPoll(int var1) throws IOException {
      setSoBusyPoll(this.intValue(), var1);
   }

   void setTcpNotSentLowAt(long var1) throws IOException {
      if (var1 >= 0L && var1 <= 4294967295L) {
         setTcpNotSentLowAt(this.intValue(), (int)var1);
      } else {
         throw new IllegalArgumentException("tcpNotSentLowAt must be a uint32_t");
      }
   }

   void setTcpFastOpen(int var1) throws IOException {
      setTcpFastOpen(this.intValue(), var1);
   }

   void setTcpKeepIdle(int var1) throws IOException {
      setTcpKeepIdle(this.intValue(), var1);
   }

   void setTcpKeepIntvl(int var1) throws IOException {
      setTcpKeepIntvl(this.intValue(), var1);
   }

   void setTcpKeepCnt(int var1) throws IOException {
      setTcpKeepCnt(this.intValue(), var1);
   }

   void setTcpUserTimeout(int var1) throws IOException {
      setTcpUserTimeout(this.intValue(), var1);
   }

   void setIpFreeBind(boolean var1) throws IOException {
      setIpFreeBind(this.intValue(), var1 ? 1 : 0);
   }

   void setIpTransparent(boolean var1) throws IOException {
      setIpTransparent(this.intValue(), var1 ? 1 : 0);
   }

   void setIpRecvOrigDestAddr(boolean var1) throws IOException {
      setIpRecvOrigDestAddr(this.intValue(), var1 ? 1 : 0);
   }

   int getTimeToLive() throws IOException {
      return getTimeToLive(this.intValue());
   }

   void getTcpInfo(EpollTcpInfo var1) throws IOException {
      getTcpInfo(this.intValue(), var1.info);
   }

   void setTcpMd5Sig(InetAddress var1, byte[] var2) throws IOException {
      NativeInetAddress var3 = NativeInetAddress.newInstance(var1);
      setTcpMd5Sig(this.intValue(), this.ipv6, var3.address(), var3.scopeId(), var2);
   }

   boolean isTcpCork() throws IOException {
      return isTcpCork(this.intValue()) != 0;
   }

   int getSoBusyPoll() throws IOException {
      return getSoBusyPoll(this.intValue());
   }

   int getTcpDeferAccept() throws IOException {
      return getTcpDeferAccept(this.intValue());
   }

   boolean isTcpQuickAck() throws IOException {
      return isTcpQuickAck(this.intValue()) != 0;
   }

   long getTcpNotSentLowAt() throws IOException {
      return (long)getTcpNotSentLowAt(this.intValue()) & 4294967295L;
   }

   int getTcpKeepIdle() throws IOException {
      return getTcpKeepIdle(this.intValue());
   }

   int getTcpKeepIntvl() throws IOException {
      return getTcpKeepIntvl(this.intValue());
   }

   int getTcpKeepCnt() throws IOException {
      return getTcpKeepCnt(this.intValue());
   }

   int getTcpUserTimeout() throws IOException {
      return getTcpUserTimeout(this.intValue());
   }

   boolean isIpFreeBind() throws IOException {
      return isIpFreeBind(this.intValue()) != 0;
   }

   boolean isIpTransparent() throws IOException {
      return isIpTransparent(this.intValue()) != 0;
   }

   boolean isIpRecvOrigDestAddr() throws IOException {
      return isIpRecvOrigDestAddr(this.intValue()) != 0;
   }

   PeerCredentials getPeerCredentials() throws IOException {
      return getPeerCredentials(this.intValue());
   }

   boolean isLoopbackModeDisabled() throws IOException {
      return getIpMulticastLoop(this.intValue(), this.ipv6) == 0;
   }

   void setLoopbackModeDisabled(boolean var1) throws IOException {
      setIpMulticastLoop(this.intValue(), this.ipv6, var1 ? 0 : 1);
   }

   boolean isUdpGro() throws IOException {
      return isUdpGro(this.intValue()) != 0;
   }

   void setUdpGro(boolean var1) throws IOException {
      setUdpGro(this.intValue(), var1 ? 1 : 0);
   }

   long sendFile(DefaultFileRegion var1, long var2, long var4, long var6) throws IOException {
      var1.open();
      long var8 = sendFile(this.intValue(), var1, var2, var4, var6);
      return var8 >= 0L ? var8 : (long)Errors.ioResult("sendfile", (int)var8);
   }

   private static InetAddress deriveInetAddress(NetworkInterface var0, boolean var1) {
      InetAddress var2 = var1 ? INET6_ANY : INET_ANY;
      if (var0 != null) {
         Enumeration var3 = var0.getInetAddresses();

         while (var3.hasMoreElements()) {
            InetAddress var4 = (InetAddress)var3.nextElement();
            boolean var5 = var4 instanceof Inet6Address;
            if (var5 == var1) {
               return var4;
            }
         }
      }

      return var2;
   }

   public static LinuxSocket newSocketStream(boolean var0) {
      return new LinuxSocket(newSocketStream0(var0));
   }

   public static LinuxSocket newSocketStream(InternetProtocolFamily var0) {
      return new LinuxSocket(newSocketStream0(var0));
   }

   public static LinuxSocket newSocketStream() {
      return newSocketStream(isIPv6Preferred());
   }

   public static LinuxSocket newSocketDgram(boolean var0) {
      return new LinuxSocket(newSocketDgram0(var0));
   }

   public static LinuxSocket newSocketDgram(InternetProtocolFamily var0) {
      return new LinuxSocket(newSocketDgram0(var0));
   }

   public static LinuxSocket newSocketDgram() {
      return newSocketDgram(isIPv6Preferred());
   }

   public static LinuxSocket newSocketDomain() {
      return new LinuxSocket(newSocketDomain0());
   }

   public static LinuxSocket newSocketDomainDgram() {
      return new LinuxSocket(newSocketDomainDgram0());
   }

   private static InetAddress unsafeInetAddrByName(String var0) {
      try {
         return InetAddress.getByName(var0);
      } catch (UnknownHostException var2) {
         throw new ChannelException(var2);
      }
   }

   private static native void joinGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void joinSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

   private static native void leaveGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void leaveSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

   private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

   private static native int getTcpDeferAccept(int var0) throws IOException;

   private static native int isTcpQuickAck(int var0) throws IOException;

   private static native int isTcpCork(int var0) throws IOException;

   private static native int getSoBusyPoll(int var0) throws IOException;

   private static native int getTcpNotSentLowAt(int var0) throws IOException;

   private static native int getTcpKeepIdle(int var0) throws IOException;

   private static native int getTcpKeepIntvl(int var0) throws IOException;

   private static native int getTcpKeepCnt(int var0) throws IOException;

   private static native int getTcpUserTimeout(int var0) throws IOException;

   private static native int getTimeToLive(int var0) throws IOException;

   private static native int isIpFreeBind(int var0) throws IOException;

   private static native int isIpTransparent(int var0) throws IOException;

   private static native int isIpRecvOrigDestAddr(int var0) throws IOException;

   private static native void getTcpInfo(int var0, long[] var1) throws IOException;

   private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

   private static native void setTcpDeferAccept(int var0, int var1) throws IOException;

   private static native void setTcpQuickAck(int var0, int var1) throws IOException;

   private static native void setTcpCork(int var0, int var1) throws IOException;

   private static native void setSoBusyPoll(int var0, int var1) throws IOException;

   private static native void setTcpNotSentLowAt(int var0, int var1) throws IOException;

   private static native void setTcpFastOpen(int var0, int var1) throws IOException;

   private static native void setTcpKeepIdle(int var0, int var1) throws IOException;

   private static native void setTcpKeepIntvl(int var0, int var1) throws IOException;

   private static native void setTcpKeepCnt(int var0, int var1) throws IOException;

   private static native void setTcpUserTimeout(int var0, int var1) throws IOException;

   private static native void setIpFreeBind(int var0, int var1) throws IOException;

   private static native void setIpTransparent(int var0, int var1) throws IOException;

   private static native void setIpRecvOrigDestAddr(int var0, int var1) throws IOException;

   private static native void setTcpMd5Sig(int var0, boolean var1, byte[] var2, int var3, byte[] var4) throws IOException;

   private static native void setInterface(int var0, boolean var1, byte[] var2, int var3, int var4) throws IOException;

   private static native int getInterface(int var0, boolean var1);

   private static native int getIpMulticastLoop(int var0, boolean var1) throws IOException;

   private static native void setIpMulticastLoop(int var0, boolean var1, int var2) throws IOException;

   private static native void setTimeToLive(int var0, int var1) throws IOException;

   private static native int isUdpGro(int var0) throws IOException;

   private static native void setUdpGro(int var0, int var1) throws IOException;
}
