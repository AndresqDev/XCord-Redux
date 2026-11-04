package io.netty.channel.unix;

import io.netty.channel.ChannelException;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

public class Socket extends FileDescriptor {
   private static volatile boolean isIpv6Preferred;
   @Deprecated
   public static final int UDS_SUN_PATH_SIZE = 100;
   protected final boolean ipv6;

   public Socket(int var1) {
      super(var1);
      this.ipv6 = isIPv6(var1);
   }

   private boolean useIpv6(InetAddress var1) {
      return useIpv6(this, var1);
   }

   protected static boolean useIpv6(Socket var0, InetAddress var1) {
      return var0.ipv6 || var1 instanceof Inet6Address;
   }

   public final void shutdown() throws IOException {
      this.shutdown(true, true);
   }

   public final void shutdown(boolean var1, boolean var2) throws IOException {
      int var3;
      int var4;
      do {
         var3 = this.state;
         if (isClosed(var3)) {
            throw new ClosedChannelException();
         }

         var4 = var3;
         if (var1 && !isInputShutdown(var3)) {
            var4 = inputShutdown(var3);
         }

         if (var2 && !isOutputShutdown(var4)) {
            var4 = outputShutdown(var4);
         }

         if (var4 == var3) {
            return;
         }
      } while (!this.casState(var3, var4));

      var3 = shutdown(this.fd, var1, var2);
      if (var3 < 0) {
         Errors.ioResult("shutdown", var3);
      }
   }

   public final boolean isShutdown() {
      int var1 = this.state;
      return isInputShutdown(var1) && isOutputShutdown(var1);
   }

   public final boolean isInputShutdown() {
      return isInputShutdown(this.state);
   }

   public final boolean isOutputShutdown() {
      return isOutputShutdown(this.state);
   }

   public final int sendTo(ByteBuffer var1, int var2, int var3, InetAddress var4, int var5) throws IOException {
      return this.sendTo(var1, var2, var3, var4, var5, false);
   }

   public final int sendTo(ByteBuffer var1, int var2, int var3, InetAddress var4, int var5, boolean var6) throws IOException {
      byte[] var7;
      int var8;
      if (var4 instanceof Inet6Address) {
         var7 = var4.getAddress();
         var8 = ((Inet6Address)var4).getScopeId();
      } else {
         var8 = 0;
         var7 = NativeInetAddress.ipv4MappedIpv6Address(var4.getAddress());
      }

      int var9 = var6 ? msgFastopen() : 0;
      int var10 = sendTo(this.fd, this.useIpv6(var4), var1, var2, var3, var7, var8, var5, var9);
      if (var10 >= 0) {
         return var10;
      } else if (var10 == Errors.ERRNO_EINPROGRESS_NEGATIVE && var6) {
         return 0;
      } else if (var10 == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendTo failed");
      } else {
         return Errors.ioResult("sendTo", var10);
      }
   }

   public final int sendToDomainSocket(ByteBuffer var1, int var2, int var3, byte[] var4) throws IOException {
      int var5 = sendToDomainSocket(this.fd, var1, var2, var3, var4);
      return var5 >= 0 ? var5 : Errors.ioResult("sendToDomainSocket", var5);
   }

   public final int sendToAddress(long var1, int var3, int var4, InetAddress var5, int var6) throws IOException {
      return this.sendToAddress(var1, var3, var4, var5, var6, false);
   }

   public final int sendToAddress(long var1, int var3, int var4, InetAddress var5, int var6, boolean var7) throws IOException {
      byte[] var8;
      int var9;
      if (var5 instanceof Inet6Address) {
         var8 = var5.getAddress();
         var9 = ((Inet6Address)var5).getScopeId();
      } else {
         var9 = 0;
         var8 = NativeInetAddress.ipv4MappedIpv6Address(var5.getAddress());
      }

      int var10 = var7 ? msgFastopen() : 0;
      int var11 = sendToAddress(this.fd, this.useIpv6(var5), var1, var3, var4, var8, var9, var6, var10);
      if (var11 >= 0) {
         return var11;
      } else if (var11 == Errors.ERRNO_EINPROGRESS_NEGATIVE && var7) {
         return 0;
      } else if (var11 == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendToAddress failed");
      } else {
         return Errors.ioResult("sendToAddress", var11);
      }
   }

   public final int sendToAddressDomainSocket(long var1, int var3, int var4, byte[] var5) throws IOException {
      int var6 = sendToAddressDomainSocket(this.fd, var1, var3, var4, var5);
      return var6 >= 0 ? var6 : Errors.ioResult("sendToAddressDomainSocket", var6);
   }

   public final int sendToAddresses(long var1, int var3, InetAddress var4, int var5) throws IOException {
      return this.sendToAddresses(var1, var3, var4, var5, false);
   }

   public final int sendToAddresses(long var1, int var3, InetAddress var4, int var5, boolean var6) throws IOException {
      byte[] var7;
      int var8;
      if (var4 instanceof Inet6Address) {
         var7 = var4.getAddress();
         var8 = ((Inet6Address)var4).getScopeId();
      } else {
         var8 = 0;
         var7 = NativeInetAddress.ipv4MappedIpv6Address(var4.getAddress());
      }

      int var9 = var6 ? msgFastopen() : 0;
      int var10 = sendToAddresses(this.fd, this.useIpv6(var4), var1, var3, var7, var8, var5, var9);
      if (var10 >= 0) {
         return var10;
      } else if (var10 == Errors.ERRNO_EINPROGRESS_NEGATIVE && var6) {
         return 0;
      } else if (var10 == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendToAddresses failed");
      } else {
         return Errors.ioResult("sendToAddresses", var10);
      }
   }

   public final int sendToAddressesDomainSocket(long var1, int var3, byte[] var4) throws IOException {
      int var5 = sendToAddressesDomainSocket(this.fd, var1, var3, var4);
      return var5 >= 0 ? var5 : Errors.ioResult("sendToAddressesDomainSocket", var5);
   }

   public final DatagramSocketAddress recvFrom(ByteBuffer var1, int var2, int var3) throws IOException {
      return recvFrom(this.fd, var1, var2, var3);
   }

   public final DatagramSocketAddress recvFromAddress(long var1, int var3, int var4) throws IOException {
      return recvFromAddress(this.fd, var1, var3, var4);
   }

   public final DomainDatagramSocketAddress recvFromDomainSocket(ByteBuffer var1, int var2, int var3) throws IOException {
      return recvFromDomainSocket(this.fd, var1, var2, var3);
   }

   public final DomainDatagramSocketAddress recvFromAddressDomainSocket(long var1, int var3, int var4) throws IOException {
      return recvFromAddressDomainSocket(this.fd, var1, var3, var4);
   }

   public int recv(ByteBuffer var1, int var2, int var3) throws IOException {
      int var4 = recv(this.intValue(), var1, var2, var3);
      if (var4 > 0) {
         return var4;
      } else {
         return var4 == 0 ? -1 : Errors.ioResult("recv", var4);
      }
   }

   public int recvAddress(long var1, int var3, int var4) throws IOException {
      int var5 = recvAddress(this.intValue(), var1, var3, var4);
      if (var5 > 0) {
         return var5;
      } else {
         return var5 == 0 ? -1 : Errors.ioResult("recvAddress", var5);
      }
   }

   public int send(ByteBuffer var1, int var2, int var3) throws IOException {
      int var4 = send(this.intValue(), var1, var2, var3);
      return var4 >= 0 ? var4 : Errors.ioResult("send", var4);
   }

   public int sendAddress(long var1, int var3, int var4) throws IOException {
      int var5 = sendAddress(this.intValue(), var1, var3, var4);
      return var5 >= 0 ? var5 : Errors.ioResult("sendAddress", var5);
   }

   public final int recvFd() throws IOException {
      int var1 = recvFd(this.fd);
      if (var1 > 0) {
         return var1;
      } else if (var1 == 0) {
         return -1;
      } else if (var1 != Errors.ERRNO_EAGAIN_NEGATIVE && var1 != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("recvFd", var1);
      } else {
         return 0;
      }
   }

   public final int sendFd(int var1) throws IOException {
      int var2 = sendFd(this.fd, var1);
      if (var2 >= 0) {
         return var2;
      } else if (var2 != Errors.ERRNO_EAGAIN_NEGATIVE && var2 != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("sendFd", var2);
      } else {
         return -1;
      }
   }

   public final boolean connect(SocketAddress var1) throws IOException {
      int var2;
      if (var1 instanceof InetSocketAddress) {
         InetSocketAddress var3 = (InetSocketAddress)var1;
         InetAddress var4 = var3.getAddress();
         NativeInetAddress var5 = NativeInetAddress.newInstance(var4);
         var2 = connect(this.fd, this.useIpv6(var4), var5.address, var5.scopeId, var3.getPort());
      } else {
         if (!(var1 instanceof DomainSocketAddress)) {
            throw new Error("Unexpected SocketAddress implementation " + var1);
         }

         DomainSocketAddress var6 = (DomainSocketAddress)var1;
         var2 = connectDomainSocket(this.fd, var6.path().getBytes(CharsetUtil.UTF_8));
      }

      return var2 < 0 ? Errors.handleConnectErrno("connect", var2) : true;
   }

   public final boolean finishConnect() throws IOException {
      int var1 = finishConnect(this.fd);
      return var1 < 0 ? Errors.handleConnectErrno("finishConnect", var1) : true;
   }

   public final void disconnect() throws IOException {
      int var1 = disconnect(this.fd, this.ipv6);
      if (var1 < 0) {
         Errors.handleConnectErrno("disconnect", var1);
      }
   }

   public final void bind(SocketAddress var1) throws IOException {
      if (var1 instanceof InetSocketAddress) {
         InetSocketAddress var2 = (InetSocketAddress)var1;
         InetAddress var3 = var2.getAddress();
         NativeInetAddress var4 = NativeInetAddress.newInstance(var3);
         int var5 = bind(this.fd, this.useIpv6(var3), var4.address, var4.scopeId, var2.getPort());
         if (var5 < 0) {
            throw Errors.newIOException("bind", var5);
         }
      } else {
         if (!(var1 instanceof DomainSocketAddress)) {
            throw new Error("Unexpected SocketAddress implementation " + var1);
         }

         DomainSocketAddress var6 = (DomainSocketAddress)var1;
         int var7 = bindDomainSocket(this.fd, var6.path().getBytes(CharsetUtil.UTF_8));
         if (var7 < 0) {
            throw Errors.newIOException("bind", var7);
         }
      }
   }

   public final void listen(int var1) throws IOException {
      int var2 = listen(this.fd, var1);
      if (var2 < 0) {
         throw Errors.newIOException("listen", var2);
      }
   }

   public final int accept(byte[] var1) throws IOException {
      int var2 = accept(this.fd, var1);
      if (var2 >= 0) {
         return var2;
      } else if (var2 != Errors.ERRNO_EAGAIN_NEGATIVE && var2 != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("accept", var2);
      } else {
         return -1;
      }
   }

   public final InetSocketAddress remoteAddress() {
      byte[] var1 = remoteAddress(this.fd);
      return var1 == null ? null : NativeInetAddress.address(var1, 0, var1.length);
   }

   public final InetSocketAddress localAddress() {
      byte[] var1 = localAddress(this.fd);
      return var1 == null ? null : NativeInetAddress.address(var1, 0, var1.length);
   }

   public final int getReceiveBufferSize() throws IOException {
      return getReceiveBufferSize(this.fd);
   }

   public final int getSendBufferSize() throws IOException {
      return getSendBufferSize(this.fd);
   }

   public final boolean isKeepAlive() throws IOException {
      return isKeepAlive(this.fd) != 0;
   }

   public final boolean isTcpNoDelay() throws IOException {
      return isTcpNoDelay(this.fd) != 0;
   }

   public final boolean isReuseAddress() throws IOException {
      return isReuseAddress(this.fd) != 0;
   }

   public final boolean isReusePort() throws IOException {
      return isReusePort(this.fd) != 0;
   }

   public final boolean isBroadcast() throws IOException {
      return isBroadcast(this.fd) != 0;
   }

   public final int getSoLinger() throws IOException {
      return getSoLinger(this.fd);
   }

   public final int getSoError() throws IOException {
      return getSoError(this.fd);
   }

   public final int getTrafficClass() throws IOException {
      return getTrafficClass(this.fd, this.ipv6);
   }

   public final void setKeepAlive(boolean var1) throws IOException {
      setKeepAlive(this.fd, var1 ? 1 : 0);
   }

   public final void setReceiveBufferSize(int var1) throws IOException {
      setReceiveBufferSize(this.fd, var1);
   }

   public final void setSendBufferSize(int var1) throws IOException {
      setSendBufferSize(this.fd, var1);
   }

   public final void setTcpNoDelay(boolean var1) throws IOException {
      setTcpNoDelay(this.fd, var1 ? 1 : 0);
   }

   public final void setSoLinger(int var1) throws IOException {
      setSoLinger(this.fd, var1);
   }

   public final void setReuseAddress(boolean var1) throws IOException {
      setReuseAddress(this.fd, var1 ? 1 : 0);
   }

   public final void setReusePort(boolean var1) throws IOException {
      setReusePort(this.fd, var1 ? 1 : 0);
   }

   public final void setBroadcast(boolean var1) throws IOException {
      setBroadcast(this.fd, var1 ? 1 : 0);
   }

   public final void setTrafficClass(int var1) throws IOException {
      setTrafficClass(this.fd, this.ipv6, var1);
   }

   public void setIntOpt(int var1, int var2, int var3) throws IOException {
      setIntOpt(this.fd, var1, var2, var3);
   }

   public void setRawOpt(int var1, int var2, ByteBuffer var3) throws IOException {
      int var4 = var3.limit();
      if (var3.isDirect()) {
         setRawOptAddress(this.fd, var1, var2, Buffer.memoryAddress(var3) + (long)var3.position(), var3.remaining());
      } else if (var3.hasArray()) {
         setRawOptArray(this.fd, var1, var2, var3.array(), var3.arrayOffset() + var3.position(), var3.remaining());
      } else {
         byte[] var5 = new byte[var3.remaining()];
         var3.duplicate().get(var5);
         setRawOptArray(this.fd, var1, var2, var5, 0, var5.length);
      }

      ((java.nio.Buffer)var3).position(var4);
   }

   public int getIntOpt(int var1, int var2) throws IOException {
      return getIntOpt(this.fd, var1, var2);
   }

   public void getRawOpt(int var1, int var2, ByteBuffer var3) throws IOException {
      if (var3.isDirect()) {
         getRawOptAddress(this.fd, var1, var2, Buffer.memoryAddress(var3) + (long)var3.position(), var3.remaining());
      } else if (var3.hasArray()) {
         getRawOptArray(this.fd, var1, var2, var3.array(), var3.position() + var3.arrayOffset(), var3.remaining());
      } else {
         byte[] var4 = new byte[var3.remaining()];
         getRawOptArray(this.fd, var1, var2, var4, 0, var4.length);
         var3.put(var4);
      }

      ((java.nio.Buffer)var3).position(var3.limit());
   }

   public static boolean isIPv6Preferred() {
      return isIpv6Preferred;
   }

   public static boolean shouldUseIpv6(InternetProtocolFamily var0) {
      return var0 == null ? isIPv6Preferred() : var0 == InternetProtocolFamily.IPv6;
   }

   private static native boolean isIPv6Preferred0(boolean var0);

   private static native boolean isIPv6(int var0);

   @Override
   public String toString() {
      return "Socket{fd=" + this.fd + '}';
   }

   public static Socket newSocketStream() {
      return new Socket(newSocketStream0());
   }

   public static Socket newSocketDgram() {
      return new Socket(newSocketDgram0());
   }

   public static Socket newSocketDomain() {
      return new Socket(newSocketDomain0());
   }

   public static Socket newSocketDomainDgram() {
      return new Socket(newSocketDomainDgram0());
   }

   public static void initialize() {
      isIpv6Preferred = isIPv6Preferred0(NetUtil.isIpV4StackPreferred());
   }

   protected static int newSocketStream0() {
      return newSocketStream0(isIPv6Preferred());
   }

   protected static int newSocketStream0(InternetProtocolFamily var0) {
      return newSocketStream0(shouldUseIpv6(var0));
   }

   protected static int newSocketStream0(boolean var0) {
      int var1 = newSocketStreamFd(var0);
      if (var1 < 0) {
         throw new ChannelException(Errors.newIOException("newSocketStream", var1));
      } else {
         return var1;
      }
   }

   protected static int newSocketDgram0() {
      return newSocketDgram0(isIPv6Preferred());
   }

   protected static int newSocketDgram0(InternetProtocolFamily var0) {
      return newSocketDgram0(shouldUseIpv6(var0));
   }

   protected static int newSocketDgram0(boolean var0) {
      int var1 = newSocketDgramFd(var0);
      if (var1 < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDgram", var1));
      } else {
         return var1;
      }
   }

   protected static int newSocketDomain0() {
      int var0 = newSocketDomainFd();
      if (var0 < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDomain", var0));
      } else {
         return var0;
      }
   }

   protected static int newSocketDomainDgram0() {
      int var0 = newSocketDomainDgramFd();
      if (var0 < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDomainDgram", var0));
      } else {
         return var0;
      }
   }

   private static native int shutdown(int var0, boolean var1, boolean var2);

   private static native int connect(int var0, boolean var1, byte[] var2, int var3, int var4);

   private static native int connectDomainSocket(int var0, byte[] var1);

   private static native int finishConnect(int var0);

   private static native int disconnect(int var0, boolean var1);

   private static native int bind(int var0, boolean var1, byte[] var2, int var3, int var4);

   private static native int bindDomainSocket(int var0, byte[] var1);

   private static native int listen(int var0, int var1);

   private static native int accept(int var0, byte[] var1);

   private static native byte[] remoteAddress(int var0);

   private static native byte[] localAddress(int var0);

   private static native int send(int var0, ByteBuffer var1, int var2, int var3);

   private static native int sendAddress(int var0, long var1, int var3, int var4);

   private static native int recv(int var0, ByteBuffer var1, int var2, int var3);

   private static native int recvAddress(int var0, long var1, int var3, int var4);

   private static native int sendTo(int var0, boolean var1, ByteBuffer var2, int var3, int var4, byte[] var5, int var6, int var7, int var8);

   private static native int sendToAddress(int var0, boolean var1, long var2, int var4, int var5, byte[] var6, int var7, int var8, int var9);

   private static native int sendToAddresses(int var0, boolean var1, long var2, int var4, byte[] var5, int var6, int var7, int var8);

   private static native int sendToDomainSocket(int var0, ByteBuffer var1, int var2, int var3, byte[] var4);

   private static native int sendToAddressDomainSocket(int var0, long var1, int var3, int var4, byte[] var5);

   private static native int sendToAddressesDomainSocket(int var0, long var1, int var3, byte[] var4);

   private static native DatagramSocketAddress recvFrom(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

   private static native DatagramSocketAddress recvFromAddress(int var0, long var1, int var3, int var4) throws IOException;

   private static native DomainDatagramSocketAddress recvFromDomainSocket(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

   private static native DomainDatagramSocketAddress recvFromAddressDomainSocket(int var0, long var1, int var3, int var4) throws IOException;

   private static native int recvFd(int var0);

   private static native int sendFd(int var0, int var1);

   private static native int msgFastopen();

   private static native int newSocketStreamFd(boolean var0);

   private static native int newSocketDgramFd(boolean var0);

   private static native int newSocketDomainFd();

   private static native int newSocketDomainDgramFd();

   private static native int isReuseAddress(int var0) throws IOException;

   private static native int isReusePort(int var0) throws IOException;

   private static native int getReceiveBufferSize(int var0) throws IOException;

   private static native int getSendBufferSize(int var0) throws IOException;

   private static native int isKeepAlive(int var0) throws IOException;

   private static native int isTcpNoDelay(int var0) throws IOException;

   private static native int isBroadcast(int var0) throws IOException;

   private static native int getSoLinger(int var0) throws IOException;

   private static native int getSoError(int var0) throws IOException;

   private static native int getTrafficClass(int var0, boolean var1) throws IOException;

   private static native void setReuseAddress(int var0, int var1) throws IOException;

   private static native void setReusePort(int var0, int var1) throws IOException;

   private static native void setKeepAlive(int var0, int var1) throws IOException;

   private static native void setReceiveBufferSize(int var0, int var1) throws IOException;

   private static native void setSendBufferSize(int var0, int var1) throws IOException;

   private static native void setTcpNoDelay(int var0, int var1) throws IOException;

   private static native void setSoLinger(int var0, int var1) throws IOException;

   private static native void setBroadcast(int var0, int var1) throws IOException;

   private static native void setTrafficClass(int var0, boolean var1, int var2) throws IOException;

   private static native void setIntOpt(int var0, int var1, int var2, int var3) throws IOException;

   private static native void setRawOptArray(int var0, int var1, int var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void setRawOptAddress(int var0, int var1, int var2, long var3, int var5) throws IOException;

   private static native int getIntOpt(int var0, int var1, int var2) throws IOException;

   private static native void getRawOptArray(int var0, int var1, int var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void getRawOptAddress(int var0, int var1, int var2, long var3, int var5) throws IOException;
}
