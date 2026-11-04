package io.netty.channel.epoll;

import io.netty.channel.DefaultFileRegion;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.Unix;
import io.netty.util.internal.ClassInitializerUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;

public final class Native {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Native.class);
   public static final int EPOLLIN;
   public static final int EPOLLOUT;
   public static final int EPOLLRDHUP;
   public static final int EPOLLET;
   public static final int EPOLLERR;
   public static final boolean IS_SUPPORTING_SENDMMSG;
   static final boolean IS_SUPPORTING_RECVMMSG;
   static final boolean IS_SUPPORTING_UDP_SEGMENT;
   private static final int TFO_ENABLED_CLIENT_MASK = 1;
   private static final int TFO_ENABLED_SERVER_MASK = 2;
   private static final int TCP_FASTOPEN_MODE;
   static final boolean IS_SUPPORTING_TCP_FASTOPEN_CLIENT;
   static final boolean IS_SUPPORTING_TCP_FASTOPEN_SERVER;
   @Deprecated
   public static final boolean IS_SUPPORTING_TCP_FASTOPEN;
   public static final int TCP_MD5SIG_MAXKEYLEN;
   public static final String KERNEL_VERSION;

   private static native int registerUnix();

   public static FileDescriptor newEventFd() {
      return new FileDescriptor(eventFd());
   }

   public static FileDescriptor newTimerFd() {
      return new FileDescriptor(timerFd());
   }

   private static native boolean isSupportingUdpSegment();

   private static native int eventFd();

   private static native int timerFd();

   public static native void eventFdWrite(int var0, long var1);

   public static native void eventFdRead(int var0);

   public static FileDescriptor newEpollCreate() {
      return new FileDescriptor(epollCreate());
   }

   private static native int epollCreate();

   @Deprecated
   public static int epollWait(FileDescriptor var0, EpollEventArray var1, FileDescriptor var2, int var3, int var4) throws IOException {
      long var5 = epollWait(var0, var1, var2, var3, var4, -1L);
      return epollReady(var5);
   }

   static long epollWait(FileDescriptor var0, EpollEventArray var1, FileDescriptor var2, int var3, int var4, long var5) throws IOException {
      if (var3 == 0 && var4 == 0) {
         return (long)epollWait(var0, var1, 0) << 32;
      } else {
         if (var3 == Integer.MAX_VALUE) {
            var3 = 0;
            var4 = 0;
         }

         long var7 = epollWait0(var0.intValue(), var1.memoryAddress(), var1.length(), var2.intValue(), var3, var4, var5);
         int var9 = epollReady(var7);
         if (var9 < 0) {
            throw Errors.newIOException("epoll_wait", var9);
         } else {
            return var7;
         }
      }
   }

   static int epollReady(long var0) {
      return (int)(var0 >> 32);
   }

   static boolean epollTimerWasUsed(long var0) {
      return (var0 & 255L) != 0L;
   }

   static int epollWait(FileDescriptor var0, EpollEventArray var1, boolean var2) throws IOException {
      return epollWait(var0, var1, var2 ? 0 : -1);
   }

   static int epollWait(FileDescriptor var0, EpollEventArray var1, int var2) throws IOException {
      int var3 = epollWait(var0.intValue(), var1.memoryAddress(), var1.length(), var2);
      if (var3 < 0) {
         throw Errors.newIOException("epoll_wait", var3);
      } else {
         return var3;
      }
   }

   public static int epollBusyWait(FileDescriptor var0, EpollEventArray var1) throws IOException {
      int var2 = epollBusyWait0(var0.intValue(), var1.memoryAddress(), var1.length());
      if (var2 < 0) {
         throw Errors.newIOException("epoll_wait", var2);
      } else {
         return var2;
      }
   }

   private static native long epollWait0(int var0, long var1, int var3, int var4, int var5, int var6, long var7);

   private static native int epollWait(int var0, long var1, int var3, int var4);

   private static native int epollBusyWait0(int var0, long var1, int var3);

   public static void epollCtlAdd(int var0, int var1, int var2) throws IOException {
      int var3 = epollCtlAdd0(var0, var1, var2);
      if (var3 < 0) {
         throw Errors.newIOException("epoll_ctl", var3);
      }
   }

   private static native int epollCtlAdd0(int var0, int var1, int var2);

   public static void epollCtlMod(int var0, int var1, int var2) throws IOException {
      int var3 = epollCtlMod0(var0, var1, var2);
      if (var3 < 0) {
         throw Errors.newIOException("epoll_ctl", var3);
      }
   }

   private static native int epollCtlMod0(int var0, int var1, int var2);

   public static void epollCtlDel(int var0, int var1) throws IOException {
      int var2 = epollCtlDel0(var0, var1);
      if (var2 < 0) {
         throw Errors.newIOException("epoll_ctl", var2);
      }
   }

   private static native int epollCtlDel0(int var0, int var1);

   public static int splice(int var0, long var1, int var3, long var4, long var6) throws IOException {
      int var8 = splice0(var0, var1, var3, var4, var6);
      return var8 >= 0 ? var8 : Errors.ioResult("splice", var8);
   }

   private static native int splice0(int var0, long var1, int var3, long var4, long var6);

   @Deprecated
   public static int sendmmsg(int var0, NativeDatagramPacketArray.NativeDatagramPacket[] var1, int var2, int var3) throws IOException {
      return sendmmsg(var0, Socket.isIPv6Preferred(), var1, var2, var3);
   }

   static int sendmmsg(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4) throws IOException {
      int var5 = sendmmsg0(var0, var1, var2, var3, var4);
      return var5 >= 0 ? var5 : Errors.ioResult("sendmmsg", var5);
   }

   private static native int sendmmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4);

   static int recvmmsg(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4) throws IOException {
      int var5 = recvmmsg0(var0, var1, var2, var3, var4);
      return var5 >= 0 ? var5 : Errors.ioResult("recvmmsg", var5);
   }

   private static native int recvmmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4);

   static int recvmsg(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket var2) throws IOException {
      int var3 = recvmsg0(var0, var1, var2);
      return var3 >= 0 ? var3 : Errors.ioResult("recvmsg", var3);
   }

   private static native int recvmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket var2);

   public static native int sizeofEpollEvent();

   public static native int offsetofEpollData();

   private static void loadNativeLibrary() {
      String var0 = PlatformDependent.normalizedOs();
      if (!"linux".equals(var0)) {
         throw new IllegalStateException("Only supported on Linux");
      } else {
         String var1 = "netty_transport_native_epoll";
         String var2 = var1 + '_' + PlatformDependent.normalizedArch();
         ClassLoader var3 = PlatformDependent.getClassLoader(Native.class);

         try {
            NativeLibraryLoader.load(var2, var3);
         } catch (UnsatisfiedLinkError var7) {
            UnsatisfiedLinkError var4 = var7;

            try {
               NativeLibraryLoader.load(var1, var3);
               logger.debug("Failed to load {}", var2, var4);
            } catch (UnsatisfiedLinkError var6) {
               ThrowableUtil.addSuppressed(var7, var6);
               throw var7;
            }
         }
      }
   }

   private Native() {
   }

   static {
      Selector var0 = null;

      try {
         var0 = Selector.open();
      } catch (IOException var13) {
      }

      ClassInitializerUtil.tryLoadClasses(
         Native.class,
         PeerCredentials.class,
         DefaultFileRegion.class,
         FileChannel.class,
         java.io.FileDescriptor.class,
         NativeDatagramPacketArray.NativeDatagramPacket.class
      );

      try {
         offsetofEpollData();
      } catch (UnsatisfiedLinkError var11) {
         loadNativeLibrary();
      } finally {
         try {
            if (var0 != null) {
               var0.close();
            }
         } catch (IOException var10) {
         }
      }

      Unix.registerInternal(new Runnable() {
         @Override
         public void run() {
            Native.registerUnix();
         }
      });
      EPOLLIN = NativeStaticallyReferencedJniMethods.epollin();
      EPOLLOUT = NativeStaticallyReferencedJniMethods.epollout();
      EPOLLRDHUP = NativeStaticallyReferencedJniMethods.epollrdhup();
      EPOLLET = NativeStaticallyReferencedJniMethods.epollet();
      EPOLLERR = NativeStaticallyReferencedJniMethods.epollerr();
      IS_SUPPORTING_SENDMMSG = NativeStaticallyReferencedJniMethods.isSupportingSendmmsg();
      IS_SUPPORTING_RECVMMSG = NativeStaticallyReferencedJniMethods.isSupportingRecvmmsg();
      IS_SUPPORTING_UDP_SEGMENT = isSupportingUdpSegment();
      TCP_FASTOPEN_MODE = NativeStaticallyReferencedJniMethods.tcpFastopenMode();
      IS_SUPPORTING_TCP_FASTOPEN_CLIENT = (TCP_FASTOPEN_MODE & 1) == 1;
      IS_SUPPORTING_TCP_FASTOPEN_SERVER = (TCP_FASTOPEN_MODE & 2) == 2;
      IS_SUPPORTING_TCP_FASTOPEN = IS_SUPPORTING_TCP_FASTOPEN_CLIENT || IS_SUPPORTING_TCP_FASTOPEN_SERVER;
      TCP_MD5SIG_MAXKEYLEN = NativeStaticallyReferencedJniMethods.tcpMd5SigMaxKeyLen();
      KERNEL_VERSION = NativeStaticallyReferencedJniMethods.kernelVersion();
   }
}
