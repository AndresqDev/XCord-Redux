package net.md_5.bungee.query;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.waterfallmc.waterfall.QueryResult;
import io.github.waterfallmc.waterfall.event.ProxyQueryEvent;
import io.github.waterfallmc.waterfall.utils.FastException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;

public class QueryHandler extends SimpleChannelInboundHandler<DatagramPacket> {
   private final ProxyServer bungee;
   private final ListenerInfo listener;
   private final Random random = new Random();
   private final Cache<InetAddress, QueryHandler.QuerySession> sessions = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build();
   private static FastException cachedNoSessionException = new FastException("No Session!");

   private void writeShort(ByteBuf var1, int var2) {
      var1.writeShortLE(var2);
   }

   private void writeNumber(ByteBuf var1, int var2) {
      this.writeString(var1, Integer.toString(var2));
   }

   private void writeString(ByteBuf var1, String var2) {
      for (char var6 : var2.toCharArray()) {
         var1.writeByte(var6);
      }

      var1.writeByte(0);
   }

   protected void channelRead0(ChannelHandlerContext var1, DatagramPacket var2) throws Exception {
      try {
         this.handleMessage(var1, var2);
      } catch (Throwable var4) {
         this.bungee.getLogger().log(Level.WARNING, "Error whilst handling query packet from " + var2.sender(), var4);
      }
   }

   private void handleMessage(ChannelHandlerContext var1, DatagramPacket var2) {
      ByteBuf var3 = (ByteBuf)var2.content();
      if (var3.readUnsignedByte() == 254 && var3.readUnsignedByte() == 253) {
         ByteBuf var4 = var1.alloc().buffer();
         DatagramPacket var5 = new DatagramPacket(var4, (InetSocketAddress)var2.sender());
         byte var6 = var3.readByte();
         int var7 = var3.readInt();
         if (var6 == 9) {
            var4.writeByte(9);
            var4.writeInt(var7);
            int var8 = this.random.nextInt();
            this.sessions.put(((InetSocketAddress)var2.sender()).getAddress(), new QueryHandler.QuerySession(var8, System.currentTimeMillis()));
            this.writeNumber(var4, var8);
         }

         if (var6 == 0) {
            int var16 = var3.readInt();
            QueryHandler.QuerySession var9 = (QueryHandler.QuerySession)this.sessions.getIfPresent(((InetSocketAddress)var2.sender()).getAddress());
            if (var9 == null || var9.getToken() != var16) {
               throw cachedNoSessionException;
            }

            List var10 = this.bungee.getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList());
            ProxyQueryEvent var11 = new ProxyQueryEvent(
               this.listener,
               new QueryResult(
                  this.listener.getMotd(),
                  "SMP",
                  "Waterfall_Proxy",
                  this.bungee.getOnlineCount(),
                  this.listener.getMaxPlayers(),
                  this.listener.getHost().getPort(),
                  this.listener.getHost().getHostString(),
                  "MINECRAFT",
                  var10,
                  this.bungee.getGameVersion()
               )
            );
            QueryResult var12 = ((ProxyQueryEvent)this.bungee.getPluginManager().callEvent(var11)).getResult();
            var4.writeByte(0);
            var4.writeInt(var7);
            if (var3.readableBytes() == 0) {
               this.writeString(var4, var12.getMotd());
               this.writeString(var4, var12.getGameType());
               this.writeString(var4, var12.getWorldName());
               this.writeNumber(var4, var12.getOnlinePlayers());
               this.writeNumber(var4, var12.getMaxPlayers());
               this.writeShort(var4, var12.getPort());
               this.writeString(var4, var12.getAddress());
            } else {
               if (var3.readableBytes() != 4) {
                  throw new IllegalStateException("Invalid data request packet");
               }

               var4.writeBytes(new byte[]{115, 112, 108, 105, 116, 110, 117, 109, 0, -128, 0});
               LinkedHashMap var13 = new LinkedHashMap();
               var13.put("hostname", var12.getMotd());
               var13.put("gametype", var12.getGameType());
               var13.put("game_id", var12.getGameId());
               var13.put("version", var12.getVersion());
               var13.put("plugins", "");
               var13.put("map", var12.getWorldName());
               var13.put("numplayers", Integer.toString(var12.getOnlinePlayers()));
               var13.put("maxplayers", Integer.toString(var12.getMaxPlayers()));
               var13.put("hostport", Integer.toString(var12.getPort()));
               var13.put("hostip", var12.getAddress());

               for (Entry var15 : var13.entrySet()) {
                  this.writeString(var4, (String)var15.getKey());
                  this.writeString(var4, (String)var15.getValue());
               }

               var4.writeByte(0);
               this.writeString(var4, "\u0001player_\u0000");
               var12.getPlayers().stream().forEach(var2x -> this.writeString(var4, var2x));
               var4.writeByte(0);
            }
         }

         var1.writeAndFlush(var5);
      } else {
         this.bungee.getLogger().log(Level.WARNING, "Query - Incorrect magic!: {0}", var2.sender());
      }
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      this.bungee.getLogger().log(Level.WARNING, "Error whilst handling query packet from " + var1.channel().remoteAddress(), var2);
   }

   public QueryHandler(ProxyServer var1, ListenerInfo var2) {
      this.bungee = var1;
      this.listener = var2;
   }

   private static class QuerySession {
      private final int token;
      private final long time;

      public QuerySession(int var1, long var2) {
         this.token = var1;
         this.time = var2;
      }

      public int getToken() {
         return this.token;
      }

      public long getTime() {
         return this.time;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof QueryHandler.QuerySession)) {
            return false;
         } else {
            QueryHandler.QuerySession var2 = (QueryHandler.QuerySession)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               return this.getToken() != var2.getToken() ? false : this.getTime() == var2.getTime();
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof QueryHandler.QuerySession;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.getToken();
         long var3 = this.getTime();
         return var2 * 59 + (int)(var3 >>> 32 ^ var3);
      }

      @Override
      public String toString() {
         return "QueryHandler.QuerySession(token=" + this.getToken() + ", time=" + this.getTime() + ")";
      }
   }
}
