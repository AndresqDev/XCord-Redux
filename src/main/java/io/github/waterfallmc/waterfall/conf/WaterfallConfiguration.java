package io.github.waterfallmc.waterfall.conf;

import com.google.common.base.Joiner;
import java.io.File;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.protocol.ProtocolConstants;

public class WaterfallConfiguration extends Configuration {
   private boolean logInitialHandlerConnections = true;
   private String gameVersion;
   private boolean useNettyDnsResolver = true;
   private int tabThrottle = 1000;
   private boolean disableModernTabLimiter = true;
   private boolean disableEntityMetadataRewrite = false;
   private boolean disableTabListRewrite = false;

   public void load() {
      super.load();
      YamlConfig var1 = new YamlConfig(new File("waterfall.yml"));
      var1.load(false);
      this.logInitialHandlerConnections = var1.getBoolean("log_initial_handler_connections", this.logInitialHandlerConnections);
      this.gameVersion = var1.getString("game_version", "").isEmpty()
         ? Joiner.on(", ").join(ProtocolConstants.SUPPORTED_VERSIONS)
         : var1.getString("game_version", "");
      this.useNettyDnsResolver = var1.getBoolean("use_netty_dns_resolver", this.useNettyDnsResolver);
      this.tabThrottle = var1.getInt("throttling.tab_complete", this.tabThrottle);
      this.disableModernTabLimiter = var1.getBoolean("disable_modern_tab_limiter", this.disableModernTabLimiter);
      this.disableEntityMetadataRewrite = var1.getBoolean("disable_entity_metadata_rewrite", this.disableEntityMetadataRewrite);
      this.disableTabListRewrite = var1.getBoolean("disable_tab_list_rewrite", this.disableTabListRewrite);
   }

   public boolean isLogInitialHandlerConnections() {
      return this.logInitialHandlerConnections;
   }

   public String getGameVersion() {
      return this.gameVersion;
   }

   public boolean isUseNettyDnsResolver() {
      return this.useNettyDnsResolver;
   }

   public int getTabThrottle() {
      return this.tabThrottle;
   }

   public boolean isDisableModernTabLimiter() {
      return this.disableModernTabLimiter;
   }

   public boolean isDisableEntityMetadataRewrite() {
      return this.disableEntityMetadataRewrite;
   }

   public boolean isDisableTabListRewrite() {
      return this.disableTabListRewrite;
   }
}
