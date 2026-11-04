package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FileDescriptor {
   private static final AtomicIntegerFieldUpdater<FileDescriptor> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(FileDescriptor.class, "state");
   private static final int STATE_CLOSED_MASK = 1;
   private static final int STATE_INPUT_SHUTDOWN_MASK = 2;
   private static final int STATE_OUTPUT_SHUTDOWN_MASK = 4;
   private static final int STATE_ALL_MASK = 7;
   volatile int state;
   final int fd;

   public FileDescriptor(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "fd");
      this.fd = var1;
   }

   public final int intValue() {
      return this.fd;
   }

   protected boolean markClosed() {
      int var1;
      do {
         var1 = this.state;
         if (isClosed(var1)) {
            return false;
         }
      } while (!this.casState(var1, var1 | 7));

      return true;
   }

   public void close() throws IOException {
      if (this.markClosed()) {
         int var1 = close(this.fd);
         if (var1 < 0) {
            throw Errors.newIOException("close", var1);
         }
      }
   }

   public boolean isOpen() {
      return !isClosed(this.state);
   }

   public final int write(ByteBuffer var1, int var2, int var3) throws IOException {
      int var4 = write(this.fd, var1, var2, var3);
      return var4 >= 0 ? var4 : Errors.ioResult("write", var4);
   }

   public final int writeAddress(long var1, int var3, int var4) throws IOException {
      int var5 = writeAddress(this.fd, var1, var3, var4);
      return var5 >= 0 ? var5 : Errors.ioResult("writeAddress", var5);
   }

   public final long writev(ByteBuffer[] var1, int var2, int var3, long var4) throws IOException {
      long var6 = writev(this.fd, var1, var2, Math.min(Limits.IOV_MAX, var3), var4);
      return var6 >= 0L ? var6 : (long)Errors.ioResult("writev", (int)var6);
   }

   public final long writevAddresses(long var1, int var3) throws IOException {
      long var4 = writevAddresses(this.fd, var1, var3);
      return var4 >= 0L ? var4 : (long)Errors.ioResult("writevAddresses", (int)var4);
   }

   public final int read(ByteBuffer var1, int var2, int var3) throws IOException {
      int var4 = read(this.fd, var1, var2, var3);
      if (var4 > 0) {
         return var4;
      } else {
         return var4 == 0 ? -1 : Errors.ioResult("read", var4);
      }
   }

   public final int readAddress(long var1, int var3, int var4) throws IOException {
      int var5 = readAddress(this.fd, var1, var3, var4);
      if (var5 > 0) {
         return var5;
      } else {
         return var5 == 0 ? -1 : Errors.ioResult("readAddress", var5);
      }
   }

   @Override
   public String toString() {
      return "FileDescriptor{fd=" + this.fd + '}';
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof FileDescriptor) ? false : this.fd == ((FileDescriptor)var1).fd;
      }
   }

   @Override
   public int hashCode() {
      return this.fd;
   }

   public static FileDescriptor from(String var0) throws IOException {
      int var1 = open(ObjectUtil.checkNotNull(var0, "path"));
      if (var1 < 0) {
         throw Errors.newIOException("open", var1);
      } else {
         return new FileDescriptor(var1);
      }
   }

   public static FileDescriptor from(File var0) throws IOException {
      return from(ObjectUtil.checkNotNull(var0, "file").getPath());
   }

   public static FileDescriptor[] pipe() throws IOException {
      long var0 = newPipe();
      if (var0 < 0L) {
         throw Errors.newIOException("newPipe", (int)var0);
      } else {
         return new FileDescriptor[]{new FileDescriptor((int)(var0 >>> 32)), new FileDescriptor((int)var0)};
      }
   }

   final boolean casState(int var1, int var2) {
      return stateUpdater.compareAndSet(this, var1, var2);
   }

   static boolean isClosed(int var0) {
      return (var0 & 1) != 0;
   }

   static boolean isInputShutdown(int var0) {
      return (var0 & 2) != 0;
   }

   static boolean isOutputShutdown(int var0) {
      return (var0 & 4) != 0;
   }

   static int inputShutdown(int var0) {
      return var0 | 2;
   }

   static int outputShutdown(int var0) {
      return var0 | 4;
   }

   private static native int open(String var0);

   private static native int close(int var0);

   private static native int write(int var0, ByteBuffer var1, int var2, int var3);

   private static native int writeAddress(int var0, long var1, int var3, int var4);

   private static native long writev(int var0, ByteBuffer[] var1, int var2, int var3, long var4);

   private static native long writevAddresses(int var0, long var1, int var3);

   private static native int read(int var0, ByteBuffer var1, int var2, int var3);

   private static native int readAddress(int var0, long var1, int var3, int var4);

   private static native long newPipe();
}
