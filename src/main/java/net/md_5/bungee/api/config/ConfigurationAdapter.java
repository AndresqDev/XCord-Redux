package net.md_5.bungee.api.config;

import java.util.Collection;
import java.util.Map;

public interface ConfigurationAdapter {
   void load();

   int getInt(String var1, int var2);

   String getString(String var1, String var2);

   boolean getBoolean(String var1, boolean var2);

   Collection<?> getList(String var1, Collection<?> var2);

   Map<String, ServerInfo> getServers();

   Collection<ListenerInfo> getListeners();

   Collection<String> getGroups(String var1);

   Collection<String> getPermissions(String var1);
}
