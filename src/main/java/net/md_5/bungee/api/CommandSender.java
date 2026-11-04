package net.md_5.bungee.api;

import java.util.Collection;
import net.md_5.bungee.api.chat.BaseComponent;

public interface CommandSender {
   String getName();

   @Deprecated
   void sendMessage(String var1);

   @Deprecated
   void sendMessages(String... var1);

   void sendMessage(BaseComponent... var1);

   void sendMessage(BaseComponent var1);

   Collection<String> getGroups();

   void addGroups(String... var1);

   void removeGroups(String... var1);

   boolean hasPermission(String var1);

   void setPermission(String var1, boolean var2);

   Collection<String> getPermissions();
}
