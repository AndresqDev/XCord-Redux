package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;
import org.slf4j.LoggerFactory;

public class Plugin {
   private PluginDescription description;
   private ProxyServer proxy;
   private File file;
   private Logger logger;
   private ExecutorService service;

   public Plugin() {
      ClassLoader var1 = this.getClass().getClassLoader();
      Preconditions.checkState(var1 instanceof PluginClassloader, "Plugin requires " + PluginClassloader.class.getName());
      ((PluginClassloader)var1).init(this);
   }

   protected Plugin(ProxyServer var1, PluginDescription var2) {
      ClassLoader var3 = this.getClass().getClassLoader();
      Preconditions.checkState(!(var3 instanceof PluginClassloader), "Cannot use initialization constructor at runtime");
   }

   public org.slf4j.Logger getSLF4JLogger() {
      return LoggerFactory.getLogger(this.logger.getName());
   }

   public void onLoad() {
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public final File getDataFolder() {
      return new File(this.getProxy().getPluginsFolder(), this.getDescription().getName());
   }

   public final InputStream getResourceAsStream(String var1) {
      return this.getClass().getClassLoader().getResourceAsStream(var1);
   }

   final void init(ProxyServer var1, PluginDescription var2) {
      this.proxy = var1;
      this.description = var2;
      this.file = var2.getFile();
      this.logger = Logger.getLogger(var2.getName());
   }

   @Deprecated
   public ExecutorService getExecutorService() {
      if (this.service == null) {
         String var1 = this.getDescription() == null ? "unknown" : this.getDescription().getName();
         this.service = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat(var1 + " Pool Thread #%1$d").setThreadFactory(new GroupedThreadFactory(this, var1)).build()
         );
      }

      return this.service;
   }

   public PluginDescription getDescription() {
      return this.description;
   }

   public ProxyServer getProxy() {
      return this.proxy;
   }

   public File getFile() {
      return this.file;
   }

   public Logger getLogger() {
      return this.logger;
   }
}
