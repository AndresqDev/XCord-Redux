package io.netty.channel;

import io.netty.util.internal.ObjectUtil;

public final class ChannelMetadata {
   private final boolean hasDisconnect;
   private final int defaultMaxMessagesPerRead;

   public ChannelMetadata(boolean var1) {
      this(var1, 1);
   }

   public ChannelMetadata(boolean var1, int var2) {
      ObjectUtil.checkPositive(var2, "defaultMaxMessagesPerRead");
      this.hasDisconnect = var1;
      this.defaultMaxMessagesPerRead = var2;
   }

   public boolean hasDisconnect() {
      return this.hasDisconnect;
   }

   public int defaultMaxMessagesPerRead() {
      return this.defaultMaxMessagesPerRead;
   }
}
