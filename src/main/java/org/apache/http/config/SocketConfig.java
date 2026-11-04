package org.apache.http.config;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class SocketConfig implements Cloneable {
   public static final SocketConfig DEFAULT = new SocketConfig.Builder().build();
   private final int soTimeout;
   private final boolean soReuseAddress;
   private final int soLinger;
   private final boolean soKeepAlive;
   private final boolean tcpNoDelay;
   private final int sndBufSize;
   private final int rcvBufSize;
   private final int backlogSize;

   SocketConfig(int var1, boolean var2, int var3, boolean var4, boolean var5, int var6, int var7, int var8) {
      this.soTimeout = var1;
      this.soReuseAddress = var2;
      this.soLinger = var3;
      this.soKeepAlive = var4;
      this.tcpNoDelay = var5;
      this.sndBufSize = var6;
      this.rcvBufSize = var7;
      this.backlogSize = var8;
   }

   public int getSoTimeout() {
      return this.soTimeout;
   }

   public boolean isSoReuseAddress() {
      return this.soReuseAddress;
   }

   public int getSoLinger() {
      return this.soLinger;
   }

   public boolean isSoKeepAlive() {
      return this.soKeepAlive;
   }

   public boolean isTcpNoDelay() {
      return this.tcpNoDelay;
   }

   public int getSndBufSize() {
      return this.sndBufSize;
   }

   public int getRcvBufSize() {
      return this.rcvBufSize;
   }

   public int getBacklogSize() {
      return this.backlogSize;
   }

   protected SocketConfig clone() throws CloneNotSupportedException {
      return (SocketConfig)super.clone();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[soTimeout=")
         .append(this.soTimeout)
         .append(", soReuseAddress=")
         .append(this.soReuseAddress)
         .append(", soLinger=")
         .append(this.soLinger)
         .append(", soKeepAlive=")
         .append(this.soKeepAlive)
         .append(", tcpNoDelay=")
         .append(this.tcpNoDelay)
         .append(", sndBufSize=")
         .append(this.sndBufSize)
         .append(", rcvBufSize=")
         .append(this.rcvBufSize)
         .append(", backlogSize=")
         .append(this.backlogSize)
         .append("]");
      return var1.toString();
   }

   public static SocketConfig.Builder custom() {
      return new SocketConfig.Builder();
   }

   public static SocketConfig.Builder copy(SocketConfig var0) {
      Args.notNull(var0, "Socket config");
      return new SocketConfig.Builder()
         .setSoTimeout(var0.getSoTimeout())
         .setSoReuseAddress(var0.isSoReuseAddress())
         .setSoLinger(var0.getSoLinger())
         .setSoKeepAlive(var0.isSoKeepAlive())
         .setTcpNoDelay(var0.isTcpNoDelay())
         .setSndBufSize(var0.getSndBufSize())
         .setRcvBufSize(var0.getRcvBufSize())
         .setBacklogSize(var0.getBacklogSize());
   }

   public static class Builder {
      private int soTimeout;
      private boolean soReuseAddress;
      private int soLinger = -1;
      private boolean soKeepAlive;
      private boolean tcpNoDelay = true;
      private int sndBufSize;
      private int rcvBufSize;
      private int backlogSize;

      Builder() {
      }

      public SocketConfig.Builder setSoTimeout(int var1) {
         this.soTimeout = var1;
         return this;
      }

      public SocketConfig.Builder setSoReuseAddress(boolean var1) {
         this.soReuseAddress = var1;
         return this;
      }

      public SocketConfig.Builder setSoLinger(int var1) {
         this.soLinger = var1;
         return this;
      }

      public SocketConfig.Builder setSoKeepAlive(boolean var1) {
         this.soKeepAlive = var1;
         return this;
      }

      public SocketConfig.Builder setTcpNoDelay(boolean var1) {
         this.tcpNoDelay = var1;
         return this;
      }

      public SocketConfig.Builder setSndBufSize(int var1) {
         this.sndBufSize = var1;
         return this;
      }

      public SocketConfig.Builder setRcvBufSize(int var1) {
         this.rcvBufSize = var1;
         return this;
      }

      public SocketConfig.Builder setBacklogSize(int var1) {
         this.backlogSize = var1;
         return this;
      }

      public SocketConfig build() {
         return new SocketConfig(
            this.soTimeout, this.soReuseAddress, this.soLinger, this.soKeepAlive, this.tcpNoDelay, this.sndBufSize, this.rcvBufSize, this.backlogSize
         );
      }
   }
}
