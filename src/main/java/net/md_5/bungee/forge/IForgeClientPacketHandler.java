package net.md_5.bungee.forge;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.packet.PluginMessage;

public interface IForgeClientPacketHandler<S> {
   S handle(PluginMessage var1, UserConnection var2);

   S send(PluginMessage var1, UserConnection var2);
}
