package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.net.SocketAddress;

public class DomainSocketAddress extends SocketAddress {
   private static final long serialVersionUID = -6934618000832236893L;
   private final String socketPath;

   public DomainSocketAddress(String var1) {
      this.socketPath = ObjectUtil.checkNotNull(var1, "socketPath");
   }

   public DomainSocketAddress(File var1) {
      this(var1.getPath());
   }

   public String path() {
      return this.socketPath;
   }

   @Override
   public String toString() {
      return this.path();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof DomainSocketAddress) ? false : ((DomainSocketAddress)var1).socketPath.equals(this.socketPath);
      }
   }

   @Override
   public int hashCode() {
      return this.socketPath.hashCode();
   }
}
