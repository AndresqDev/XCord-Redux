package net.md_5.bungee.conf;

import com.google.common.base.Charsets;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.error.YAMLException;
import xcord.ProcessUtils;
import xcord.XCord;

public class YamlConfig implements ConfigurationAdapter {
   private final Yaml yaml;
   private Map<String, Object> config;
   private final File file;

   public YamlConfig() {
      this(new File("config.yml"));
   }

   public YamlConfig(File var1) {
      this.file = var1;
      DumperOptions var2 = new DumperOptions();
      var2.setDefaultFlowStyle(FlowStyle.BLOCK);
      this.yaml = new Yaml(var2);
   }

   @Override
   public void load() {
      this.load(true);
   }

   public void load(boolean var1) {
      try {
         this.file.createNewFile();
         FileInputStream var2 = new FileInputStream(this.file);

         try {
            try {
               this.config = (Map<String, Object>)this.yaml.load(var2);
            } catch (YAMLException var6) {
               throw new RuntimeException(
                  "Invalid configuration encountered - this is a configuration error and NOT a bug! Please attempt to fix the error or see https://www.spigotmc.org/ for help.",
                  var6
               );
            }
         } catch (Throwable var7) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var7.addSuppressed(var5);
            }

            throw var7;
         }

         var2.close();
         if (this.config == null) {
            this.config = new CaseInsensitiveMap();
         } else {
            this.config = new CaseInsensitiveMap<Object>(this.config);
         }
      } catch (IOException var8) {
         throw new RuntimeException("Could not load configuration!", var8);
      }

      if (var1) {
         Map var9 = this.get("permissions", null);
         if (var9 == null) {
            this.set("permissions.default", Arrays.asList("bungeecord.command.server", "bungeecord.command.list"));
            this.set(
               "permissions.admin", Arrays.asList("bungeecord.command.alert", "bungeecord.command.end", "bungeecord.command.ip", "bungeecord.command.reload")
            );
         }

         Map var3 = this.get("groups", null);
         if (var3 == null) {
            this.set("groups.md_5", Collections.singletonList("admin"));
         }
      }
   }

   private <T> T get(String var1, T var2) {
      return this.get(var1, (T)var2, this.config);
   }

   private <T> T get(String var1, T var2, Map var3) {
      int var4 = var1.indexOf(46);
      if (var4 == -1) {
         Object var8 = var3.get(var1);
         if (var8 == null && var2 != null) {
            var8 = var2;
            var3.put(var1, var2);
            this.save();
         }

         return (T)var8;
      } else {
         String var5 = var1.substring(0, var4);
         String var6 = var1.substring(var4 + 1, var1.length());
         Object var7 = (Map)var3.get(var5);
         if (var7 == null) {
            var7 = new LinkedHashMap();
            var3.put(var5, var7);
         }

         return this.get(var6, (T)var2, (Map)var7);
      }
   }

   private void set(String var1, Object var2) {
      this.set(var1, var2, this.config);
   }

   private void set(String var1, Object var2, Map var3) {
      int var4 = var1.indexOf(46);
      if (var4 == -1) {
         if (var2 == null) {
            var3.remove(var1);
         } else {
            var3.put(var1, var2);
         }

         this.save();
      } else {
         String var5 = var1.substring(0, var4);
         String var6 = var1.substring(var4 + 1, var1.length());
         Object var7 = (Map)var3.get(var5);
         if (var7 == null) {
            var7 = new LinkedHashMap();
            var3.put(var5, var7);
         }

         this.set(var6, var2, (Map)var7);
      }
   }

   private void save() {
      try {
         OutputStreamWriter var1 = new OutputStreamWriter(new FileOutputStream(this.file), Charsets.UTF_8);

         try {
            this.yaml.dump(this.config, var1);
         } catch (Throwable var5) {
            try {
               var1.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         var1.close();
      } catch (IOException var6) {
         ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not save config", (Throwable)var6);
      }
   }

   @Override
   public int getInt(String var1, int var2) {
      return this.get(var1, var2);
   }

   public double getDouble(String var1, double var2) {
      return this.get(var1, var2);
   }

   @Override
   public String getString(String var1, String var2) {
      return this.get(var1, var2);
   }

   @Override
   public boolean getBoolean(String var1, boolean var2) {
      return this.get(var1, var2);
   }

   @Override
   public Map<String, ServerInfo> getServers() {
      Map var1 = this.get("servers", Collections.singletonMap("lobby", new HashMap()));
      HashMap var2 = new HashMap();

      for (Entry var4 : var1.entrySet()) {
         Map var5 = (Map)var4.getValue();
         String var6 = (String)var4.getKey();
         String var7 = this.get("address", "localhost:25565", var5);
         String var8 = ChatColor.translateAlternateColorCodes('&', this.get("motd", "&1Just another XCord - Forced Host", var5));
         boolean var9 = this.get("restricted", false, var5);
         SocketAddress var10 = Util.getAddr(
            var7, XCord.getConfig().getGeneralDynamicallyResolveServerAddresses() && !var7.contains("localhost") && !ProcessUtils.isValidIp(var7.split(":")[0])
         );
         ServerInfo var11 = ProxyServer.getInstance().constructServerInfo(var6, var10, var8, var9);
         var2.put(var6, var11);
      }

      return var2;
   }

   @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
   @Override
   public Collection<ListenerInfo> getListeners() {
      Collection var1 = this.get("listeners", Arrays.asList(new HashMap()));
      HashMap var2 = new HashMap();
      var2.put("pvp.md-5.net", "pvp");
      HashSet var3 = new HashSet();

      for (Map var5 : var1) {
         String var6 = this.get("motd", "&1Another XCord server", var5);
         var6 = ChatColor.translateAlternateColorCodes('&', var6);
         int var7 = this.get("max_players", 1, var5);
         boolean var8 = this.get("force_default_server", false, var5);
         String var9 = this.get("host", "0.0.0.0:25577", var5);
         int var10 = this.get("tab_size", 60, var5);
         SocketAddress var11 = Util.getAddr(var9);
         CaseInsensitiveMap var12 = new CaseInsensitiveMap(this.get("forced_hosts", var2, var5));
         String var13 = this.get("tab_list", "GLOBAL_PING", var5);
         YamlConfig.DefaultTabList var14 = YamlConfig.DefaultTabList.valueOf(var13.toUpperCase(Locale.ROOT));
         if (var14 == null) {
            var14 = YamlConfig.DefaultTabList.GLOBAL_PING;
         }

         boolean var15 = this.get("bind_local_address", true, var5);
         boolean var16 = this.get("ping_passthrough", false, var5);
         boolean var17 = this.get("query_enabled", false, var5);
         int var18 = this.get("query_port", 25577, var5);
         boolean var19 = this.get("proxy_protocol", false, var5);
         ArrayList var20 = new ArrayList(this.get("priorities", Collections.EMPTY_LIST, var5));
         String var21 = this.get("default_server", null, var5);
         String var22 = this.get("fallback_server", null, var5);
         if (var21 != null) {
            var20.add(var21);
            this.set("default_server", null, var5);
         }

         if (var22 != null) {
            var20.add(var22);
            this.set("fallback_server", null, var5);
         }

         if (var20.isEmpty()) {
            var20.add("lobby");
         }

         this.set("priorities", var20, var5);
         ListenerInfo var23 = new ListenerInfo(var11, var6, var7, var10, var20, var8, var12, var14.toString(), var15, var16, var18, var17, var19);
         var3.add(var23);
      }

      return var3;
   }

   @Override
   public Collection<String> getGroups(String var1) {
      Map var2 = this.get("groups", Collections.emptyMap());
      Collection var3 = (Collection)var2.get(var1);
      HashSet var4 = var3 == null ? new HashSet() : new HashSet(var3);
      var4.add("default");
      return var4;
   }

   @Override
   public Collection<?> getList(String var1, Collection<?> var2) {
      return this.get(var1, var2);
   }

   @Override
   public Collection<String> getPermissions(String var1) {
      Collection var2 = this.get("permissions." + var1, null);
      return (Collection<String>)(var2 == null ? Collections.EMPTY_SET : var2);
   }

   private static enum DefaultTabList {
      GLOBAL,
      GLOBAL_PING,
      SERVER;
   }
}
