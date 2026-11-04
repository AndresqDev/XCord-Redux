package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginMessageException extends ProxyPluginException {
   private final ProxiedPlayer player;
   private final String channel;
   private final byte[] data;

   public ProxyPluginMessageException(String var1, Throwable var2, Plugin var3, ProxiedPlayer var4, String var5, byte[] var6) {
      super(var1, var2, var3);
      this.player = (ProxiedPlayer)Preconditions.checkNotNull(var4, "player");
      this.channel = (String)Preconditions.checkNotNull(var5, "channel");
      this.data = (byte[])Preconditions.checkNotNull(var6, "data");
   }

   public ProxyPluginMessageException(Throwable var1, Plugin var2, ProxiedPlayer var3, String var4, byte[] var5) {
      super(var1, var2);
      this.player = (ProxiedPlayer)Preconditions.checkNotNull(var3, "player");
      this.channel = (String)Preconditions.checkNotNull(var4, "channel");
      this.data = (byte[])Preconditions.checkNotNull(var5, "data");
   }

   protected ProxyPluginMessageException(String var1, Throwable var2, boolean var3, boolean var4, Plugin var5, ProxiedPlayer var6, String var7, byte[] var8) {
      super(var1, var2, var3, var4, var5);
      this.player = (ProxiedPlayer)Preconditions.checkNotNull(var6, "player");
      this.channel = (String)Preconditions.checkNotNull(var7, "channel");
      this.data = (byte[])Preconditions.checkNotNull(var8, "data");
   }

   public String getChannel() {
      return this.channel;
   }

   public byte[] getData() {
      return this.data;
   }

   public ProxiedPlayer getPlayer() {
      return this.player;
   }
}
