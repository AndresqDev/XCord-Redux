package io.netty.channel.local;

import io.netty.channel.DefaultEventLoopGroup;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class LocalEventLoopGroup extends DefaultEventLoopGroup {
   public LocalEventLoopGroup() {
   }

   public LocalEventLoopGroup(int var1) {
      super(var1);
   }

   public LocalEventLoopGroup(ThreadFactory var1) {
      super(0, var1);
   }

   public LocalEventLoopGroup(int var1, ThreadFactory var2) {
      super(var1, var2);
   }
}
