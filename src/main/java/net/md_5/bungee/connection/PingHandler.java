package net.md_5.bungee.connection;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.util.BufUtil;
import net.md_5.bungee.util.QuietException;
import xcord.XCord;

public class PingHandler extends PacketHandler {
   private final ServerInfo target;
   private final Callback<ServerPing> callback;
   private final int protocol;
   private ChannelWrapper channel;

   @Override
   public void connected(ChannelWrapper var1) throws Exception {
      this.channel = var1;
      MinecraftEncoder var2 = new MinecraftEncoder(Protocol.HANDSHAKE, false, this.protocol);
      var1.getHandle()
         .pipeline()
         .addAfter(
            "frame-decoder",
            "packet-decoder",
            new MinecraftDecoder(
               Protocol.STATUS, false, ProxyServer.getInstance().getProtocolVersion(), XCord.getConfig().getPerformanceOptimizationsDontCopyPackets()
            )
         );
      var1.getHandle().pipeline().addAfter("frame-prepender", "packet-encoder", var2);
      var1.write(new Handshake(this.protocol, this.target.getAddress().getHostString(), this.target.getAddress().getPort(), 1));
      var2.setProtocol(Protocol.STATUS);
      var1.write(new StatusRequest());
   }

   @Override
   public void exception(Throwable var1) throws Exception {
      this.callback.done(null, var1);
   }

   @Override
   public void handle(PacketWrapper var1) throws Exception {
      if (var1.packet == null) {
         throw new QuietException("Unexpected packet received during ping process! " + BufUtil.dump(var1.buf, 16));
      }
   }

   @SuppressFBWarnings({"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"})
   @Override
   public void handle(StatusResponse var1) throws Exception {
      Gson var2 = this.protocol == 4 ? BungeeCord.getInstance().gsonLegacy : BungeeCord.getInstance().gson;
      ServerPing var3 = (ServerPing)var2.fromJson(var1.getResponse(), ServerPing.class);
      ((BungeeServerInfo)this.target).cachePing(var3);
      this.callback.done(var3, null);
      this.channel.close();
   }

   @Override
   public String toString() {
      return "[Ping Handler] -> " + this.target.getName();
   }

   public PingHandler(ServerInfo var1, Callback<ServerPing> var2, int var3) {
      this.target = var1;
      this.callback = var2;
      this.protocol = var3;
   }
}
