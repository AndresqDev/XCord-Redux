package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

public final class RouteTracker implements Cloneable, RouteInfo {
   private final HttpHost targetHost;
   private final InetAddress localAddress;
   private boolean connected;
   private HttpHost[] proxyChain;
   private RouteInfo.TunnelType tunnelled;
   private RouteInfo.LayerType layered;
   private boolean secure;

   public RouteTracker(HttpHost var1, InetAddress var2) {
      Args.notNull(var1, "Target host");
      this.targetHost = var1;
      this.localAddress = var2;
      this.tunnelled = RouteInfo.TunnelType.PLAIN;
      this.layered = RouteInfo.LayerType.PLAIN;
   }

   public void reset() {
      this.connected = false;
      this.proxyChain = null;
      this.tunnelled = RouteInfo.TunnelType.PLAIN;
      this.layered = RouteInfo.LayerType.PLAIN;
      this.secure = false;
   }

   public RouteTracker(HttpRoute var1) {
      this(var1.getTargetHost(), var1.getLocalAddress());
   }

   public final void connectTarget(boolean var1) {
      Asserts.check(!this.connected, "Already connected");
      this.connected = true;
      this.secure = var1;
   }

   public final void connectProxy(HttpHost var1, boolean var2) {
      Args.notNull(var1, "Proxy host");
      Asserts.check(!this.connected, "Already connected");
      this.connected = true;
      this.proxyChain = new HttpHost[]{var1};
      this.secure = var2;
   }

   public final void tunnelTarget(boolean var1) {
      Asserts.check(this.connected, "No tunnel unless connected");
      Asserts.notNull(this.proxyChain, "No tunnel without proxy");
      this.tunnelled = RouteInfo.TunnelType.TUNNELLED;
      this.secure = var1;
   }

   public final void tunnelProxy(HttpHost var1, boolean var2) {
      Args.notNull(var1, "Proxy host");
      Asserts.check(this.connected, "No tunnel unless connected");
      Asserts.notNull(this.proxyChain, "No tunnel without proxy");
      HttpHost[] var3 = new HttpHost[this.proxyChain.length + 1];
      System.arraycopy(this.proxyChain, 0, var3, 0, this.proxyChain.length);
      var3[var3.length - 1] = var1;
      this.proxyChain = var3;
      this.secure = var2;
   }

   public final void layerProtocol(boolean var1) {
      Asserts.check(this.connected, "No layered protocol unless connected");
      this.layered = RouteInfo.LayerType.LAYERED;
      this.secure = var1;
   }

   @Override
   public final HttpHost getTargetHost() {
      return this.targetHost;
   }

   @Override
   public final InetAddress getLocalAddress() {
      return this.localAddress;
   }

   @Override
   public final int getHopCount() {
      int var1 = 0;
      if (this.connected) {
         if (this.proxyChain == null) {
            var1 = 1;
         } else {
            var1 = this.proxyChain.length + 1;
         }
      }

      return var1;
   }

   @Override
   public final HttpHost getHopTarget(int var1) {
      Args.notNegative(var1, "Hop index");
      int var2 = this.getHopCount();
      Args.check(var1 < var2, "Hop index exceeds tracked route length");
      HttpHost var3 = null;
      if (var1 < var2 - 1) {
         var3 = this.proxyChain[var1];
      } else {
         var3 = this.targetHost;
      }

      return var3;
   }

   @Override
   public final HttpHost getProxyHost() {
      return this.proxyChain == null ? null : this.proxyChain[0];
   }

   public final boolean isConnected() {
      return this.connected;
   }

   @Override
   public final RouteInfo.TunnelType getTunnelType() {
      return this.tunnelled;
   }

   @Override
   public final boolean isTunnelled() {
      return this.tunnelled == RouteInfo.TunnelType.TUNNELLED;
   }

   @Override
   public final RouteInfo.LayerType getLayerType() {
      return this.layered;
   }

   @Override
   public final boolean isLayered() {
      return this.layered == RouteInfo.LayerType.LAYERED;
   }

   @Override
   public final boolean isSecure() {
      return this.secure;
   }

   public final HttpRoute toRoute() {
      return !this.connected ? null : new HttpRoute(this.targetHost, this.localAddress, this.proxyChain, this.secure, this.tunnelled, this.layered);
   }

   @Override
   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof RouteTracker)) {
         return false;
      } else {
         RouteTracker var2 = (RouteTracker)var1;
         return this.connected == var2.connected
            && this.secure == var2.secure
            && this.tunnelled == var2.tunnelled
            && this.layered == var2.layered
            && LangUtils.equals(this.targetHost, var2.targetHost)
            && LangUtils.equals(this.localAddress, var2.localAddress)
            && LangUtils.equals(this.proxyChain, var2.proxyChain);
      }
   }

   @Override
   public final int hashCode() {
      int var1 = 17;
      var1 = LangUtils.hashCode(var1, this.targetHost);
      var1 = LangUtils.hashCode(var1, this.localAddress);
      if (this.proxyChain != null) {
         for (HttpHost var5 : this.proxyChain) {
            var1 = LangUtils.hashCode(var1, var5);
         }
      }

      var1 = LangUtils.hashCode(var1, this.connected);
      var1 = LangUtils.hashCode(var1, this.secure);
      var1 = LangUtils.hashCode(var1, this.tunnelled);
      return LangUtils.hashCode(var1, this.layered);
   }

   @Override
   public final String toString() {
      StringBuilder var1 = new StringBuilder(50 + this.getHopCount() * 30);
      var1.append("RouteTracker[");
      if (this.localAddress != null) {
         var1.append(this.localAddress);
         var1.append("->");
      }

      var1.append('{');
      if (this.connected) {
         var1.append('c');
      }

      if (this.tunnelled == RouteInfo.TunnelType.TUNNELLED) {
         var1.append('t');
      }

      if (this.layered == RouteInfo.LayerType.LAYERED) {
         var1.append('l');
      }

      if (this.secure) {
         var1.append('s');
      }

      var1.append("}->");
      if (this.proxyChain != null) {
         for (HttpHost var5 : this.proxyChain) {
            var1.append(var5);
            var1.append("->");
         }
      }

      var1.append(this.targetHost);
      var1.append(']');
      return var1.toString();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
