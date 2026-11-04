package io.netty.channel.unix;

public abstract class GenericUnixChannelOption<T> extends UnixChannelOption<T> {
   private final int level;
   private final int optname;

   GenericUnixChannelOption(String var1, int var2, int var3) {
      super(var1);
      this.level = var2;
      this.optname = var3;
   }

   public int level() {
      return this.level;
   }

   public int optname() {
      return this.optname;
   }
}
