package io.netty.channel.local;

import io.netty.channel.Channel;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

public final class LocalAddress extends SocketAddress implements Comparable<LocalAddress> {
   private static final long serialVersionUID = 4644331421130916435L;
   public static final LocalAddress ANY = new LocalAddress("ANY");
   private final String id;
   private final String strVal;

   LocalAddress(Channel var1) {
      StringBuilder var2 = new StringBuilder(16);
      var2.append("local:E");
      var2.append(Long.toHexString((long)var1.hashCode() & 4294967295L | 4294967296L));
      var2.setCharAt(7, ':');
      this.id = var2.substring(6);
      this.strVal = var2.toString();
   }

   public LocalAddress(String var1) {
      this.id = ObjectUtil.checkNonEmptyAfterTrim(var1, "id").toLowerCase();
      this.strVal = "local:" + this.id;
   }

   public String id() {
      return this.id;
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof LocalAddress) ? false : this.id.equals(((LocalAddress)var1).id);
   }

   public int compareTo(LocalAddress var1) {
      return this.id.compareTo(var1.id);
   }

   @Override
   public String toString() {
      return this.strVal;
   }
}
