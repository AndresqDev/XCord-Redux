package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import io.github.waterfallmc.waterfall.exception.ProxyCommandException;
import io.github.waterfallmc.waterfall.exception.ProxyEventException;
import io.github.waterfallmc.waterfall.exception.ProxyPluginEnableDisableException;
import io.github.waterfallmc.waterfall.exception.ProxyTabCompleteException;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.event.EventBus;
import net.md_5.bungee.event.EventHandlerMethod;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public final class PluginManager {
   private final ProxyServer proxy;
   private final Yaml yaml;
   private EventBus eventBus;
   private final Map<String, Plugin> plugins = new LinkedHashMap<>();
   private final MutableGraph<String> dependencyGraph = GraphBuilder.directed().build();
   private final LibraryLoader libraryLoader;
   private final Map<String, Command> commandMap = new HashMap<>();
   private Map<String, PluginDescription> toLoad = new HashMap<>();
   private final Multimap<Plugin, Command> commandsByPlugin = ArrayListMultimap.create();
   private final Multimap<Plugin, Listener> listenersByPlugin = ArrayListMultimap.create();

   public PluginManager(ProxyServer var1) {
      this.proxy = var1;
      Constructor var2 = new Constructor();
      PropertyUtils var3 = var2.getPropertyUtils();
      var3.setSkipMissingProperties(true);
      var2.setPropertyUtils(var3);
      this.yaml = new Yaml(var2);
      this.eventBus = new EventBus(var1.getLogger());
      LibraryLoader var4 = null;

      try {
         var4 = new LibraryLoader(var1.getLogger());
      } catch (NoClassDefFoundError var6) {
         var1.getLogger().warning("Could not initialize LibraryLoader (missing dependencies?)");
      }

      this.libraryLoader = var4;
   }

   public void registerCommand(Plugin var1, Command var2) {
      this.commandMap.put(var2.getName().toLowerCase(Locale.ROOT), var2);

      for (String var6 : var2.getAliases()) {
         this.commandMap.put(var6.toLowerCase(Locale.ROOT), var2);
      }

      this.commandsByPlugin.put(var1, var2);
   }

   public void unregisterCommand(Command var1) {
      while (this.commandMap.values().remove(var1)) {
      }

      this.commandsByPlugin.values().remove(var1);
   }

   public void unregisterCommands(Plugin var1) {
      Iterator var2 = this.commandsByPlugin.get(var1).iterator();

      while (var2.hasNext()) {
         Command var3 = (Command)var2.next();

         while (this.commandMap.values().remove(var3)) {
         }

         var2.remove();
      }
   }

   private Command getCommandIfEnabled(String var1, CommandSender var2) {
      String var3 = var1.toLowerCase(Locale.ROOT);
      return var2 instanceof ProxiedPlayer && this.proxy.getDisabledCommands().contains(var3) ? null : this.commandMap.get(var3);
   }

   public boolean isExecutableCommand(String var1, CommandSender var2) {
      return this.getCommandIfEnabled(var1, var2) != null;
   }

   public boolean dispatchCommand(CommandSender var1, String var2) {
      return this.dispatchCommand(var1, var2, null);
   }

   public boolean dispatchCommand(CommandSender var1, String var2, List<String> var3) {
      String[] var4 = var2.split(" ", -1);
      if (var4.length != 0 && !var4[0].isEmpty()) {
         Command var5 = this.getCommandIfEnabled(var4[0], var1);
         if (var5 == null) {
            return false;
         } else if (!var5.hasPermission(var1)) {
            if (var3 == null) {
               var1.sendMessage(var5.getPermissionMessage() == null ? this.proxy.getTranslation("no_permission") : var5.getPermissionMessage());
            }

            return true;
         } else {
            String[] var6 = Arrays.copyOfRange(var4, 1, var4.length);
            if (var3 == null) {
               try {
                  if (this.proxy.getConfig().isLogCommands()) {
                     this.proxy.getLogger().log(Level.INFO, "{0} executed command: /{1}", new Object[]{var1.getName(), var2});
                  }

                  var5.execute(var1, var6);
               } catch (Exception var9) {
                  var1.sendMessage(ChatColor.RED + "An internal error occurred whilst executing this command, please check the console log for details.");
                  ProxyServer.getInstance().getLogger().log(Level.WARNING, "Error in dispatching command", (Throwable)var9);
                  this.callEvent(new ProxyExceptionEvent(new ProxyCommandException(var9, var5, var1, var6)));
               }
            } else if (var2.contains(" ") && var5 instanceof TabExecutor) {
               try {
                  for (String var8 : ((TabExecutor)var5).onTabComplete(var1, var6)) {
                     var3.add(var8);
                  }
               } catch (Exception var10) {
                  var1.sendMessage(ChatColor.RED + "An internal error occurred whilst executing this command, please check the console log for details.");
                  ProxyServer.getInstance().getLogger().log(Level.WARNING, "Error in dispatching command", (Throwable)var10);
                  this.callEvent(new ProxyExceptionEvent(new ProxyTabCompleteException(var10, var5, var1, var6)));
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public List<String> tabCompleteCommand(CommandSender var1, String var2) {
      ArrayList var3 = new ArrayList();
      if (var2.indexOf(32) == -1) {
         for (Command var5 : this.commandMap.values()) {
            if (var5.getName().startsWith(var2)) {
               String var6 = var5.getPermission();
               if (var6 == null || var6.isEmpty() || var1.hasPermission(var6)) {
                  var3.add(var5.getName());
               }
            }
         }
      } else {
         this.dispatchCommand(var1, var2, var3);
      }

      return var3;
   }

   public Collection<Plugin> getPlugins() {
      return this.plugins.values();
   }

   public Plugin getPlugin(String var1) {
      return this.plugins.get(var1);
   }

   public void loadPlugins() {
      HashMap var1 = new HashMap();

      for (Entry var3 : this.toLoad.entrySet()) {
         PluginDescription var4 = (PluginDescription)var3.getValue();
         if (!this.enablePlugin(var1, new Stack<>(), var4)) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Failed to enable {0}", var3.getKey());
         }
      }

      this.toLoad.clear();
      this.toLoad = null;
   }

   public void enablePlugins() {
      for (Plugin var2 : this.plugins.values()) {
         try {
            var2.onEnable();
            ProxyServer.getInstance()
               .getLogger()
               .log(
                  Level.INFO,
                  "Enabled plugin {0} version {1} by {2}",
                  new Object[]{var2.getDescription().getName(), var2.getDescription().getVersion(), var2.getDescription().getAuthor()}
               );
         } catch (Throwable var5) {
            String var4 = "Exception encountered when loading plugin: " + var2.getDescription().getName();
            ProxyServer.getInstance().getLogger().log(Level.WARNING, var4, var5);
            this.callEvent(new ProxyExceptionEvent(new ProxyPluginEnableDisableException(var4, var5, var2)));
         }
      }
   }

   private boolean enablePlugin(Map<PluginDescription, Boolean> var1, Stack<PluginDescription> var2, PluginDescription var3) {
      if (var1.containsKey(var3)) {
         return (Boolean)var1.get(var3);
      } else {
         HashSet var4 = new HashSet();
         var4.addAll(var3.getDepends());
         var4.addAll(var3.getSoftDepends());
         boolean var5 = true;

         for (String var7 : var4) {
            PluginDescription var8 = this.toLoad.get(var7);
            Boolean var9 = var8 != null ? (Boolean)var1.get(var8) : Boolean.FALSE;
            if (var9 == null) {
               if (var2.contains(var8)) {
                  StringBuilder var10 = new StringBuilder();

                  for (PluginDescription var12 : var2) {
                     var10.append(var12.getName()).append(" -> ");
                  }

                  var10.append(var3.getName()).append(" -> ").append(var7);
                  ProxyServer.getInstance().getLogger().log(Level.WARNING, "Circular dependency detected: {0}", var10);
                  var5 = false;
               } else {
                  var2.push(var3);
                  var9 = this.enablePlugin(var1, var2, var8);
                  var2.pop();
               }
            }

            if (var9 == Boolean.FALSE && var3.getDepends().contains(var7)) {
               ProxyServer.getInstance()
                  .getLogger()
                  .log(Level.WARNING, "{0} (required by {1}) is unavailable", new Object[]{String.valueOf(var7), var3.getName()});
               var5 = false;
            }

            this.dependencyGraph.putEdge(var3.getName(), var7);
            if (!var5) {
               break;
            }
         }

         if (var5) {
            try {
               PluginClassloader var14 = new PluginClassloader(
                  this.proxy, var3, var3.getFile(), this.libraryLoader != null ? this.libraryLoader.createLoader(var3) : null
               );
               Class var15 = var14.loadClass(var3.getMain());
               Plugin var16 = (Plugin)var15.getDeclaredConstructor().newInstance();
               this.plugins.put(var3.getName(), var16);
               var16.onLoad();
               ProxyServer.getInstance()
                  .getLogger()
                  .log(Level.INFO, "Loaded plugin {0} version {1} by {2}", new Object[]{var3.getName(), var3.getVersion(), var3.getAuthor()});
            } catch (Throwable var13) {
               this.proxy.getLogger().log(Level.WARNING, "Error loading plugin " + var3.getName(), var13);
            }
         }

         var1.put(var3, var5);
         return var5;
      }
   }

   public void detectPlugins(File var1) {
      Preconditions.checkNotNull(var1, "folder");
      Preconditions.checkArgument(var1.isDirectory(), "Must load from a directory");

      for (File var5 : var1.listFiles()) {
         if (var5.isFile() && var5.getName().endsWith(".jar")) {
            try {
               JarFile var6 = new JarFile(var5);

               try {
                  JarEntry var7 = var6.getJarEntry("bungee.yml");
                  if (var7 == null) {
                     var7 = var6.getJarEntry("plugin.yml");
                  }

                  Preconditions.checkNotNull(var7, "Plugin must have a plugin.yml or bungee.yml");
                  InputStream var8 = var6.getInputStream(var7);

                  try {
                     PluginDescription var9 = (PluginDescription)this.yaml.loadAs(var8, PluginDescription.class);
                     Preconditions.checkNotNull(var9.getName(), "Plugin from %s has no name", var5);
                     Preconditions.checkNotNull(var9.getMain(), "Plugin from %s has no main", var5);
                     var9.setFile(var5);
                     this.toLoad.put(var9.getName(), var9);
                  } catch (Throwable var13) {
                     if (var8 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var12) {
                           var13.addSuppressed(var12);
                        }
                     }

                     throw var13;
                  }

                  if (var8 != null) {
                     var8.close();
                  }
               } catch (Throwable var14) {
                  try {
                     var6.close();
                  } catch (Throwable var11) {
                     var14.addSuppressed(var11);
                  }

                  throw var14;
               }

               var6.close();
            } catch (Exception var15) {
               ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load plugin from file " + var5, (Throwable)var15);
            }
         }
      }
   }

   public <T extends Event> T callEvent(T var1) {
      Preconditions.checkNotNull(var1, "event");
      long var2 = System.nanoTime();
      this.eventBus.post(var1, this::handleEventException);
      var1.postCall();
      long var4 = System.nanoTime() - var2;
      if (var4 > 250000000L) {
         ProxyServer.getInstance().getLogger().log(Level.WARNING, "Event {0} took {1}ms to process!", new Object[]{var1, var4 / 1000000L});
      }

      return (T)var1;
   }

   public EventBus getEventBus() {
      return this.eventBus;
   }

   public void setEventBus(EventBus var1) {
      this.eventBus = var1;
   }

   private <T extends Event> void handleEventException(String var1, T var2, EventHandlerMethod var3, Throwable var4) {
      if (!(var2 instanceof ProxyExceptionEvent)) {
         this.callEvent((T)(new ProxyExceptionEvent(new ProxyEventException(var1, var4, (Listener)var3.getListener(), var2))));
      }
   }

   public void registerListener(Plugin var1, Listener var2) {
      for (Method var6 : var2.getClass().getDeclaredMethods()) {
         Preconditions.checkArgument(
            !var6.isAnnotationPresent(Subscribe.class),
            "Listener %s has registered using deprecated subscribe annotation! Please update to @EventHandler.",
            var2
         );
      }

      this.eventBus.register(var2);
      this.listenersByPlugin.put(var1, var2);
   }

   public void unregisterListener(Listener var1) {
      this.eventBus.unregister(var1);
      this.listenersByPlugin.values().remove(var1);
   }

   public void unregisterListeners(Plugin var1) {
      Iterator var2 = this.listenersByPlugin.get(var1).iterator();

      while (var2.hasNext()) {
         this.eventBus.unregister(var2.next());
         var2.remove();
      }
   }

   public Collection<Entry<String, Command>> getCommands() {
      return Collections.unmodifiableCollection(this.commandMap.entrySet());
   }

   boolean isTransitiveDepend(PluginDescription var1, PluginDescription var2) {
      Preconditions.checkArgument(var1 != null, "plugin");
      Preconditions.checkArgument(var2 != null, "depend");
      return this.dependencyGraph.nodes().contains(var1.getName()) && Graphs.reachableNodes(this.dependencyGraph, var1.getName()).contains(var2.getName());
   }

   public PluginManager(ProxyServer var1, Yaml var2, LibraryLoader var3) {
      this.proxy = var1;
      this.yaml = var2;
      this.libraryLoader = var3;
   }
}
