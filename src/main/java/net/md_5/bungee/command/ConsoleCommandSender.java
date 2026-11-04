package net.md_5.bungee.command;

import java.util.Collection;
import java.util.Collections;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;

public final class ConsoleCommandSender implements CommandSender {
   private static final ConsoleCommandSender instance = new ConsoleCommandSender();

   @Override
   public void sendMessage(String var1) {
      ProxyServer.getInstance().getLogger().info(var1);
   }

   @Override
   public void sendMessages(String... var1) {
      for (String var5 : var1) {
         this.sendMessage(var5);
      }
   }

   @Override
   public void sendMessage(BaseComponent... var1) {
      this.sendMessage(BaseComponent.toLegacyText(var1));
   }

   @Override
   public void sendMessage(BaseComponent var1) {
      this.sendMessage(var1.toLegacyText());
   }

   @Override
   public String getName() {
      return "CONSOLE";
   }

   @Override
   public Collection<String> getGroups() {
      return Collections.emptySet();
   }

   @Override
   public void addGroups(String... var1) {
      throw new UnsupportedOperationException("Console may not have groups");
   }

   @Override
   public void removeGroups(String... var1) {
      throw new UnsupportedOperationException("Console may not have groups");
   }

   @Override
   public boolean hasPermission(String var1) {
      return true;
   }

   @Override
   public void setPermission(String var1, boolean var2) {
      throw new UnsupportedOperationException("Console has all permissions");
   }

   @Override
   public Collection<String> getPermissions() {
      return Collections.emptySet();
   }

   private ConsoleCommandSender() {
   }

   public static ConsoleCommandSender getInstance() {
      return instance;
   }
}
