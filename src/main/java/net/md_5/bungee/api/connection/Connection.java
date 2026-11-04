package net.md_5.bungee.api.connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.protocol.DefinedPacket;

public interface Connection {
   @Deprecated
   InetSocketAddress getAddress();

   SocketAddress getSocketAddress();

   @Deprecated
   void disconnect(String var1);

   void disconnect(BaseComponent... var1);

   void disconnect(BaseComponent var1);

   boolean isConnected();

   Connection.Unsafe unsafe();

   public interface Unsafe {
      void sendPacket(DefinedPacket var1);
   }
}
