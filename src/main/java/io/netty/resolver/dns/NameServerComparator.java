package io.netty.resolver.dns;

import io.netty.util.internal.ObjectUtil;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Comparator;

public final class NameServerComparator implements Serializable, Comparator<InetSocketAddress> {
   private static final long serialVersionUID = 8372151874317596185L;
   private final Class<? extends InetAddress> preferredAddressType;

   public NameServerComparator(Class<? extends InetAddress> var1) {
      this.preferredAddressType = ObjectUtil.checkNotNull(var1, "preferredAddressType");
   }

   public int compare(InetSocketAddress var1, InetSocketAddress var2) {
      if (var1.equals(var2)) {
         return 0;
      } else if (!var1.isUnresolved() && !var2.isUnresolved()) {
         if (var1.getAddress().getClass() == var2.getAddress().getClass()) {
            return 0;
         } else {
            return this.preferredAddressType.isAssignableFrom(var1.getAddress().getClass()) ? -1 : 1;
         }
      } else if (var1.isUnresolved() && var2.isUnresolved()) {
         return 0;
      } else {
         return var1.isUnresolved() ? 1 : -1;
      }
   }
}
