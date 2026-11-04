package net.md_5.bungee;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ServerConnection implements Server {
   private final ChannelWrapper ch;
   private final BungeeServerInfo info;
   private boolean isObsolete;
   private final boolean forgeServer = false;
   private final Queue<ServerConnection.KeepAliveData> keepAlives = new ArrayDeque<>();
   private final Connection.Unsafe unsafe = new Connection.Unsafe() {
      @Override
      public void sendPacket(DefinedPacket var1) {
         ServerConnection.this.ch.write(var1);
      }
   };

   @Override
   public void sendData(String var1, byte[] var2) {
      this.unsafe().sendPacket(new PluginMessage(var1, var2, false));
   }

   @Override
   public void disconnect(String var1) {
      this.disconnect();
   }

   @Override
   public void disconnect(BaseComponent... var1) {
      Preconditions.checkArgument(var1.length == 0, "Server cannot have disconnect reason");
      this.ch.close();
   }

   public void forceDisconnect() {
      this.ch.forceClose();
   }

   @Override
   public void disconnect(BaseComponent var1) {
      this.disconnect();
   }

   @Override
   public InetSocketAddress getAddress() {
      return (InetSocketAddress)this.getSocketAddress();
   }

   @Override
   public SocketAddress getSocketAddress() {
      return this.getInfo().getAddress();
   }

   @Override
   public boolean isConnected() {
      return !this.ch.isClosed();
   }

   @Override
   public Connection.Unsafe unsafe() {
      return this.unsafe;
   }

   public ServerConnection(ChannelWrapper var1, BungeeServerInfo var2) {
      this.ch = var1;
      this.info = var2;
   }

   public ChannelWrapper getCh() {
      return this.ch;
   }

   public BungeeServerInfo getInfo() {
      return this.info;
   }

   public boolean isObsolete() {
      return this.isObsolete;
   }

   public void setObsolete(boolean var1) {
      this.isObsolete = var1;
   }

   public boolean isForgeServer() {
      return false;
   }

   public Queue<ServerConnection.KeepAliveData> getKeepAlives() {
      return this.keepAlives;
   }

   public static class KeepAliveData {
      private final long id;
      private final long time;

      public KeepAliveData(long var1, long var3) {
         this.id = var1;
         this.time = var3;
      }

      public long getId() {
         return this.id;
      }

      public long getTime() {
         return this.time;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ServerConnection.KeepAliveData)) {
            return false;
         } else {
            ServerConnection.KeepAliveData var2 = (ServerConnection.KeepAliveData)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               return this.getId() != var2.getId() ? false : this.getTime() == var2.getTime();
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof ServerConnection.KeepAliveData;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         long var3 = this.getId();
         var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
         long var5 = this.getTime();
         return var2 * 59 + (int)(var5 >>> 32 ^ var5);
      }

      @Override
      public String toString() {
         return "ServerConnection.KeepAliveData(id=" + this.getId() + ", time=" + this.getTime() + ")";
      }
   }
}
