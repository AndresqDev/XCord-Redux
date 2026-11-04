package xcord;

import com.google.common.collect.Lists;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.Chat;

public class XCordConfig {
   private final int antiBotDisableChecksTime;
   private final boolean antiBotPrePEnabled;
   private final boolean antiBotLinuxCheck;
   private final boolean antiBotChatCheck;
   private final boolean antiBotDnsCheck;
   private final boolean antiBotDnsCheckIsStrict;
   private final boolean antiBotDnsCheckIsStrict1;
   private final int antiBotChatForceDelay;
   private final int generalBaseTick;
   private final int antiBotWhitelistSurviveTime;
   private final int antiBotWhitelistPurgeInterval;
   private final int antiBotTimeToWhitelist;
   private final int antiBotTimeGroupingTick;
   private final int antiBotAntiBotDeactivateDelay;
   private final int antiBotAntiBotActivateConnections;
   private final int antiBotIpTrackingTimeout;
   private final int antiBotSpeedyLoginMaxLoginsPerTick;
   private final int antiBotSpeedyLoginBlacklistViolations;
   private final int antiBotSpeedyConnectionMaxConnectionsPerTick;
   private final int antiBotSpeedyConnectionBlacklistViolations;
   private final int antiBotPrePDelay;
   private final int antiBotPrePBlacklistViolations;
   private final int antiBotLoginCheckTimeoutTime;
   private final int antiBotLoginCheckStrangeConnectionDelay;
   private final int securityManagerBlacklistResetTime;
   private final boolean securityManagerLogBlacklist;
   private final int exploitMonitorMaxPacketBytes;
   private final int exploitMonitorPacketBytesPerTickLimit;
   private final int exploitMonitorPacketsPerTickLimit;
   private final int exploitMonitorPayloadBytesPerTickLimit;
   private final boolean exploitMonitorEnabled;
   private final int generalInitialTimeout;
   private final int generalKickTimeout;
   private final int generalCompressionStrength;
   private final boolean debugDebugMode;
   private final int antiBotBootDelaySeconds;
   private final boolean antiBotUnstableCheckEnabled;
   private final int antiBotUnstableCheckLatencyThreshold;
   private final boolean antiBotUnstableCheckDelayKickForReason;
   private final int antiBotUnstableCheckBlacklistViolations;
   private final boolean antiBotStrangeNameEnabled;
   private final int antiBotStrangeNameRandomNumberCount;
   private final int antiBotStrangeNameRandomCapitalCount;
   private final int antiBotStrangeNameBlacklistViolations;
   private final Set<String> ignoredAddresses = new HashSet<>(50);
   private boolean securityManagerIgnoreAddressesEnabled;
   private final boolean debugOutputDownstreamErrors;
   private final int loginTickLengthMs;
   private final int loginThreadPoolSize;
   private final boolean loginAntiVpnEnabled;
   private final Set<String> blockedAsns = new HashSet<>();
   private final Set<String> blockedCountries = new HashSet<>();
   private final Set<String> filteredLogMessages = new HashSet<>();
   private final boolean loginAntiVpnProxycheckerioApiEnabled;
   private final String loginAntiVpnProxycheckerioApiKey;
   private final String proxyCheckArguments;
   private final boolean loginAntiVpnIphubApiEnabled;
   private final String loginAntiVpnIphubApiKey;
   private final boolean loginAntiVpnLogNonCacheDetection;
   private final int loginAntiVpnSaveInterval;
   private final int loginAntiVpnPurgeInterval;
   private final long loginAntiVpnPurgeAge;
   private final boolean antiBotVerifyV2Enabled;
   private final boolean antiBotVerifyV2BlockPotentialProxiesproxyCheck1Enabled;
   private final boolean antiBotVerifyV2BlockPotentialProxiesproxyCheck2Enabled;
   private final boolean antiBotVerifyV2IgnoreGeyser;
   private final boolean antiBotVerifyV2VerifyPackets;
   private final boolean antiBotVerifyV2VerifyBrand;
   private final int antiBotVerifyV2BlockPotentialProxiesproxyCheck1Leniency;
   private final int antiBotVerifyV2BlockPotentialProxiesproxyCheck2MaxAttempts;
   private final int antiBotVerifyV2BlockPotentialProxiesproxyCheck2Leniency;
   private final boolean antiBotVerifyV2Ignore116PlusVersions;
   private final int antiBotVerifyV2MaxPing;
   private final int antiBotVerifyV2MaxCheckTime;
   private final int generalNewConnectionSpeedThreshold;
   private final boolean logFilterEnabled;
   private final int generalMinimumNameLength;
   private final List<String> blockedIsoCodesRaw = new ArrayList<>();
   private final boolean securityManagerIpsetBlacklisting;
   private final boolean loginAntiVpnInitialConnectionCheckenabled;
   private final boolean loginAntiVpnInitialConnectionCheckcacheOnly;
   private final boolean loginAntiVpnInitialConnectionCheckonlyDuringBotAttack;
   private final boolean loginAntiVpnBlacklistOnDetection;
   private final boolean loginAntiVpnOnlyBlacklistOnDetectionDuringAttack;
   private final boolean antiBotTimeoutFloodEnabled;
   private final int antiBotTimeoutFloodBlacklistViolations;
   private final long antiBotTimeoutFloodTimeBetweenTimeouts;
   private final boolean antiBotNamePatternEnabled;
   private final double antiBotNamePatternMinimumMatchPercent;
   private final int antiBotNamePatternMinimumNameLength;
   private final int antiBotNamePatternNameHistoryLength;
   private final int antiBotNamePatternBlacklistViolations;
   private final boolean antiBotNamePatternSameLengthOnly;
   private final int antiBotAntiHangForceTimeoutTime;
   private final boolean antiBotAntiHangEnabled;
   private final boolean antiBotAntiHangOnlyDuringBotAttack;
   private final boolean antiDdosDuringLoginEnabled;
   private final boolean antiDdosDuringGameplayEnabled;
   private final int antiDdosDuringLoginMaxBytesPerSecond;
   private final int antiDdosDuringLoginInitialBufferLimitBytes;
   private final int antiDdosDuringLoginMaxPacketsPerSecond;
   private final int antiDdosDuringGameplayMaxBytesPerSecond;
   private final int antiDdosDuringGameplayMaxPacketsPerSecond;
   private final boolean antiDdosBlacklist;
   private final int antiDdosImprobableBufferMaxViolations;
   private final boolean antiBotPrintConnectionStats;
   private final boolean generalOnlyBlacklistPacketErrorsDuringAttack;
   private final boolean sharedGlobalBlacklistEnabled;
   private final int sharedGlobalBlacklistUpdateDatabaseIntervalSeconds;
   private final boolean sharedGlobalBlacklistUseIpset;
   private final boolean ipSpooferSpoofListedUserIps;
   private final Set<String> spoofedUsers = new HashSet<>();
   private final String ipSpoofer7222928185;
   private final boolean ipSpooferLogOnSpoof;
   private final boolean performanceOptimizationsUseMergedEncodingPipeline;
   private final boolean performanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi;
   private final boolean performanceOptimizationsDontCopyPackets;
   private final boolean antiBotVerifyV2AdvancedLobbyCheckEnabled;
   private final DefinedPacket verifyingPacket;
   private final DefinedPacket verifiedPacket;
   private final int antiBotVerifyV2AdvancedLobbyCheckMaxAttempts;
   private final int antiBotVerifyV2MaxViolationsForBlacklist;
   private final boolean antiBotVerifyV2AdvancedLobbyCheckTestCollision;
   private final boolean fixesFix17KickWhen18Joins;
   private final boolean antiBotPassthroughVerifyThenReconnectEnabled;
   private final BaseComponent[] successfulVerifyKickMessage;
   private final String generalXCord;
   private final boolean antiBotNullPingAndPingSpamCachePingMotd;
   private final boolean antiBotNullPingAndPingSpamOnlyCacheDuringBotAttack;
   private final long antiBotNullPingAndPingSpamCacheMilliseconds;
   private final boolean antiBotNullPingAndPingSpamDisableLegacyPings;
   private final boolean loginAntiVpnBlackboxApiEnabled;
   private final boolean securityManagerAutoWhitelistHaproxyAddresses;
   private final boolean performanceOptimizationsUseTcpFastOpenOnServers;
   private final boolean performanceOptimizationsUseTcpFastConnectOnClients;
   private final int compatibilityPayloadChannelLimit;
   private final boolean compatibilityAppearAsWaterfall;
   private final boolean generalAlwaysCheckAllowedNameCharacters;
   private final boolean kernelLevelIpsetCountryBlockerEnabled;
   private final boolean antiDdosDosIpFilterEnabled;
   private final int antiDdosDosIpFilterUpdateDatabaseIntervalSeconds;
   private final boolean antiDdosDosIpFilterUseIpset;
   private final int antiDdosDosIpFilterEnableAtThisConnectionsPerSecond;
   private final String[] blockedIsoCodes;
   private final boolean generalDynamicallyResolveServerAddresses;
   private final boolean performanceOptimizationsUseOptimizedTimeoutHandler;
   private final boolean compatibilityAlwaysFireQuitEvent;
   private final boolean antiBotLinuxOnlySocketCheckEnabled;
   private final boolean antiBotLinuxOnlySocketCheckEnableDuringBotAttackOnly;
   private final int antiBotLinuxOnlySocketCheckRequiredConnectionRate;
   private final boolean antiBotLinuxOnlySocketCheckDetectSuspiciousMss;
   private final boolean antiBotLinuxOnlySocketCheckDetectProxiesAndVps;
   private final boolean antiBotLinuxOnlySocketCheckDetectSuspicious;
   private final boolean antiBotLinuxOnlySocketCheckDetectNonWindows;
   private final boolean antiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist;
   private final YamlConfig xcordYml = new YamlConfig(new File("xcord.yml"));

   public XCordConfig() {
      ConfigUpdater var1 = new ConfigUpdater(
         "# Learn about proper configuration setup through the official XCord discord",
         "# In the 'xcord-config-help' channel",
         "# Maximizing XCord's full potential will require some configuration since all servers are a bit different"
      );
      this.xcordYml.load(false);
      String var2 = "general.";
      boolean var3 = false;

      try {
         var3 = ProcessUtils.execute(new ProcessBuilder("sudo", "ipset").redirectErrorStream(true), 2000).contains("No command specified");
         if (!var3) {
            System.out.println("Warning: IPSet is not installed or XCord has no root access. Install it manually or disable the IPSet options.");
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }
      this.generalXCord = this.xcordYml.getString(var2 + "f3-display-name", "XCord");
      this.generalAlwaysCheckAllowedNameCharacters = this.xcordYml.getBoolean(var2 + "always-check-allowed-name-characters", true);
      this.generalDynamicallyResolveServerAddresses = this.xcordYml.getBoolean(var2 + "dynamically-resolve-server-addresses", true);
      this.generalInitialTimeout = this.xcordYml.getInt(var2 + "initial-timeout", 12000);
      this.generalBaseTick = this.xcordYml.getInt(var2 + "base-tick", 1000);
      this.generalKickTimeout = this.xcordYml.getInt(var2 + "kick-timeout", 6000);
      this.generalCompressionStrength = this.xcordYml.getInt(var2 + "compression-strength", -1);
      this.generalNewConnectionSpeedThreshold = this.xcordYml.getInt(var2 + "new-connection-speed-threshold", 50000);
      this.generalMinimumNameLength = this.xcordYml.getInt(var2 + "minimum-name-length", 2);
      this.generalOnlyBlacklistPacketErrorsDuringAttack = this.xcordYml.getBoolean("only-blacklist-packet-errors-during-attack", true);
      var2 = "performance-optimizations.";
      var1.addReplacement("performance-optimizations:", "#Some of these options may be incompatible with plugins such as ViaVersion, Protocolize, etc");
      this.performanceOptimizationsUseMergedEncodingPipeline = this.xcordYml.getBoolean(var2 + "use-merged-encoding-pipeline", false);
      this.performanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi = this.xcordYml
         .getBoolean(var2 + "ignore-scoreboards-and-disable-scoreboard-api", false);
      this.performanceOptimizationsDontCopyPackets = this.xcordYml.getBoolean(var2 + "dont-copy-packets", false);
      this.performanceOptimizationsUseTcpFastOpenOnServers = this.xcordYml.getBoolean(var2 + "use-tcp-fast-open-on-servers", true);
      this.performanceOptimizationsUseTcpFastConnectOnClients = this.xcordYml.getBoolean(var2 + "use-tcp-fast-connect-on-clients", true);
      this.performanceOptimizationsUseOptimizedTimeoutHandler = this.xcordYml.getBoolean(var2 + "use-optimized-timeout-handler", true);
      var2 = "shared-global-blacklist.";
      this.sharedGlobalBlacklistEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.sharedGlobalBlacklistUpdateDatabaseIntervalSeconds = this.xcordYml.getInt(var2 + "update-database-interval-seconds", 3600);
      this.sharedGlobalBlacklistUseIpset = this.xcordYml.getBoolean(var2 + "use-ipset", var3);
      var2 = "anti-ddos.";
      this.antiDdosBlacklist = this.xcordYml.getBoolean(var2 + "blacklist", true);
      this.antiDdosImprobableBufferMaxViolations = this.xcordYml.getInt(var2 + "improbable-buffer-max-violations", 10);
      var2 = "anti-ddos.during-login.";
      this.antiDdosDuringLoginEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.antiDdosDuringLoginInitialBufferLimitBytes = this.xcordYml.getInt(var2 + "initial-buffer-limit-bytes", 2000);
      this.antiDdosDuringLoginMaxBytesPerSecond = this.xcordYml.getInt(var2 + "max-bytes-per-second", 50000);
      this.antiDdosDuringLoginMaxPacketsPerSecond = this.xcordYml.getInt(var2 + "max-packets-per-second", 500);
      var2 = "anti-ddos.during-gameplay.";
      var1.addReplacement("during-gameplay:", "#Very similar to the exploit-monitor. If you enable this and get false kicks, raise each field by 1.5x");
      this.antiDdosDuringGameplayEnabled = this.xcordYml.getBoolean(var2 + "enabled", false);
      this.antiDdosDuringGameplayMaxBytesPerSecond = this.xcordYml.getInt(var2 + "max-bytes-per-second", 250000);
      this.antiDdosDuringGameplayMaxPacketsPerSecond = this.xcordYml.getInt(var2 + "max-packets-per-second", 1250);
      var2 = "anti-ddos.dos-ip-filter.";
      this.antiDdosDosIpFilterEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.antiDdosDosIpFilterUpdateDatabaseIntervalSeconds = this.xcordYml.getInt(var2 + "update-database-interval-seconds", 3600);
      this.antiDdosDosIpFilterUseIpset = this.xcordYml.getBoolean(var2 + "use-ipset", var3);
      this.antiDdosDosIpFilterEnableAtThisConnectionsPerSecond = this.xcordYml.getInt(var2 + "enable-at-this-connections-per-second", 100);
      var2 = "anti-bot.";
      this.antiBotPrintConnectionStats = this.xcordYml.getBoolean(var2 + "print-connection-stats", true);
      var2 = "anti-bot.passthrough-verify-then-reconnect.";
      this.antiBotPassthroughVerifyThenReconnectEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.successfulVerifyKickMessage = TextComponent.fromLegacyText(
         ChatColor.translateAlternateColorCodes(
            '&', this.xcordYml.getString(var2 + "successful-verify-kick-message", "&aYour connection has been verified! Please re-connect.")
         )
      );
      var2 = "anti-bot.verifyV2.";
      var1.addReplacement(
         "verifyV2:",
         "#Powerful, highly configurable, spoofed lobby check where",
         "#XCord performs various checks to validate whether the user is a bot or not"
      );
      this.antiBotVerifyV2Enabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.antiBotVerifyV2IgnoreGeyser = this.xcordYml.getBoolean(var2 + "ignore-geyser", true);
      this.antiBotVerifyV2VerifyPackets = this.xcordYml.getBoolean(var2 + "verify-packets", true);
      this.antiBotVerifyV2VerifyBrand = this.xcordYml.getBoolean(var2 + "verify-brand", true);
      this.antiBotVerifyV2MaxViolationsForBlacklist = this.xcordYml.getInt(var2 + "max-violations-for-blacklist", 3);
      this.antiBotVerifyV2MaxPing = this.xcordYml.getInt(var2 + "max-ping", 2000);
      this.antiBotVerifyV2MaxCheckTime = this.xcordYml.getInt(var2 + "max-check-time", 30000);
      this.antiBotVerifyV2Ignore116PlusVersions = this.xcordYml.getBoolean(var2 + "ignore-1-16-plus-versions", false);
      var2 = "anti-bot.verifyV2.block-potential-proxies";
      var1.addReplacement(
         "block-potential-proxies:", "#XCord will use special networking techniques to figure out whether a connection may be a proxy (No database)"
      );
      var1.addReplacement("proxy-check-1:", "#Powerful check, but may false on phone connections");
      var1.addReplacement("proxy-check-2:", "#Picks up most proxies, but may false-kick super laggy players (Rare, but they can always re-join!)");
      this.antiBotVerifyV2BlockPotentialProxiesproxyCheck1Enabled = this.xcordYml.getBoolean(var2 + "proxy-check-1.enabled", false);
      this.antiBotVerifyV2BlockPotentialProxiesproxyCheck1Leniency = this.xcordYml.getInt(var2 + "proxy-check-1.leniency", 127);
      this.antiBotVerifyV2BlockPotentialProxiesproxyCheck2Enabled = this.xcordYml.getBoolean(var2 + "proxy-check-2.enabled", true);
      this.antiBotVerifyV2BlockPotentialProxiesproxyCheck2Leniency = this.xcordYml.getInt(var2 + "proxy-check-2.leniency", 50);
      this.antiBotVerifyV2BlockPotentialProxiesproxyCheck2MaxAttempts = this.xcordYml.getInt(var2 + "proxy-check-2.max-attempts", 10);
      var2 = "anti-bot.verifyV2.advanced-lobby-check.";
      var1.addReplacement("advanced-lobby-check:", "#Fall + Gravity + Collision check. Very good against almost all bots");
      this.antiBotVerifyV2AdvancedLobbyCheckEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.verifyingPacket = this.buildChatPacket(
         this.xcordYml
            .getList(var2 + "verifying-message", new ArrayList<>(Collections.singletonList("&f&lXCORD&7» &6Verifying your Minecraft client...")))
            .toArray(new String[0])
      );
      this.verifiedPacket = this.buildChatPacket(
         this.xcordYml
            .getList(var2 + "verified-message", new ArrayList<>(Collections.singletonList("&f&lXCORD&7» &aYou've been successfully verified!")))
            .toArray(new String[0])
      );
      this.antiBotVerifyV2AdvancedLobbyCheckMaxAttempts = this.xcordYml.getInt(var2 + "max-attempts", 2);
      this.antiBotVerifyV2AdvancedLobbyCheckTestCollision = this.xcordYml.getBoolean(var2 + "test-collision", true);
      var2 = "anti-bot.";
      this.antiBotBootDelaySeconds = this.xcordYml.getInt(var2 + "boot-delay-seconds", 0);
      this.antiBotDisableChecksTime = this.xcordYml.getInt(var2 + "disable-checks-time", 60000);
      var1.addReplacement("linux-check:", "#Kicks users that are connecting quickly, and appear to be on Linux");
      this.antiBotLinuxCheck = this.xcordYml.getBoolean(var2 + "linux-check", true);
      var1.addReplacement("chat-check:", "#Kicks users who chat/command too quickly after logging in");
      this.antiBotChatCheck = this.xcordYml.getBoolean(var2 + "chat-check", true);
      var1.addReplacement("dns-check:", "#Only during bot-attack, blacklist people who connect directly to your IP (Not domain)");
      this.antiBotDnsCheck = this.xcordYml.getBoolean(var2 + "dns-check", true);
      this.antiBotDnsCheckIsStrict = this.xcordYml.getBoolean(var2 + "dns-check-is-strict", true);
      this.antiBotDnsCheckIsStrict1 = false;
      this.antiBotIpTrackingTimeout = this.xcordYml.getInt(var2 + "ip-tracking-timeout", 40000);
      this.antiBotChatForceDelay = this.xcordYml.getInt(var2 + "chat-force-delay", 800);
      var1.addReplacement("whitelist-survive-time:", "#Time in milliseconds to retain a player's whitelist status (Max is 2147483647)");
      this.antiBotWhitelistSurviveTime = this.xcordYml.getInt(var2 + "whitelist-survive-time", 172800000);
      this.antiBotWhitelistPurgeInterval = this.xcordYml.getInt(var2 + "whitelist-purge-interval", 43200000);
      this.antiBotTimeToWhitelist = this.xcordYml.getInt(var2 + "time-to-whitelist", 300000);
      this.antiBotTimeGroupingTick = this.xcordYml.getInt(var2 + "time-grouping-tick", 5000);
      var1.addReplacement("anti-bot-deactivate-delay:", "#Time in seconds until the anti-bot deactivates after a bot attack seems over");
      this.antiBotAntiBotDeactivateDelay = this.xcordYml.getInt(var2 + "anti-bot-deactivate-delay", 60);
      var1.addReplacement("anti-bot-activate-connections:", "#How many connections in a second until the anti-bot kicks on");
      this.antiBotAntiBotActivateConnections = this.xcordYml.getInt(var2 + "anti-bot-activate-connections", 40);
      this.antiBotSpeedyLoginMaxLoginsPerTick = this.xcordYml.getInt(var2 + "speedy-login.max-logins-per-tick", 4);
      this.antiBotSpeedyLoginBlacklistViolations = this.xcordYml.getInt(var2 + "speedy-login.blacklist-violations", 3);
      this.antiBotSpeedyConnectionMaxConnectionsPerTick = this.xcordYml.getInt(var2 + "speedy-connection.max-connections-per-tick", 10);
      this.antiBotSpeedyConnectionBlacklistViolations = this.xcordYml.getInt(var2 + "speedy-connection.blacklist-violations", 3);
      this.antiBotLoginCheckTimeoutTime = this.xcordYml.getInt(var2 + "login-check.timeout-time", 20000);
      this.antiBotLoginCheckStrangeConnectionDelay = this.xcordYml.getInt(var2 + "login-check.strange-connection-delay", 30000);
      this.antiBotUnstableCheckEnabled = this.xcordYml.getBoolean(var2 + "unstable-check.enabled", false);
      this.antiBotUnstableCheckLatencyThreshold = this.xcordYml.getInt(var2 + "unstable-check.latency-threshold", 500);
      this.antiBotUnstableCheckDelayKickForReason = this.xcordYml.getBoolean(var2 + "unstable-check.delay-kick-for-reason", false);
      this.antiBotUnstableCheckBlacklistViolations = this.xcordYml.getInt(var2 + "unstable-check.blacklist-violations", 5);
      this.antiBotStrangeNameEnabled = this.xcordYml.getBoolean(var2 + "strange-name.enabled", true);
      this.antiBotStrangeNameRandomCapitalCount = this.xcordYml.getInt(var2 + "strange-name.random-capital-count", 3);
      this.antiBotStrangeNameRandomNumberCount = this.xcordYml.getInt(var2 + "strange-name.random-number-count", 3);
      this.antiBotStrangeNameBlacklistViolations = this.xcordYml.getInt(var2 + "strange-name.blacklist-violations", 2);
      this.antiBotPrePEnabled = this.xcordYml.getBoolean(var2 + "pre-p.enabled", true);
      this.antiBotPrePDelay = this.xcordYml.getInt(var2 + "pre-p.delay", 3);
      this.antiBotPrePBlacklistViolations = this.xcordYml.getInt(var2 + "pre-p.blacklist-violations", 2);
      this.antiBotTimeoutFloodEnabled = this.xcordYml.getBoolean(var2 + "timeout-flood.enabled", false);
      this.antiBotTimeoutFloodTimeBetweenTimeouts = (long)this.xcordYml.getInt(var2 + "timeout-flood.time-between-timeouts", 10000);
      this.antiBotTimeoutFloodBlacklistViolations = this.xcordYml.getInt(var2 + "timeout-flood.blacklist-violations", 3);
      var2 = "anti-bot.linux-only-socket-check.";
      this.antiBotLinuxOnlySocketCheckEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.antiBotLinuxOnlySocketCheckEnableDuringBotAttackOnly = this.xcordYml.getBoolean(var2 + "enable-during-bot-attack-only", true);
      this.antiBotLinuxOnlySocketCheckRequiredConnectionRate = this.xcordYml.getInt(var2 + "required-connection-rate", 100);
      this.antiBotLinuxOnlySocketCheckDetectSuspiciousMss = this.xcordYml.getBoolean(var2 + "detect-suspicious-mss", true);
      this.antiBotLinuxOnlySocketCheckDetectNonWindows = this.xcordYml.getBoolean(var2 + "detect-non-windows", true);
      this.antiBotLinuxOnlySocketCheckDetectSuspicious = this.xcordYml.getBoolean(var2 + "detect-suspicious", true);
      this.antiBotLinuxOnlySocketCheckDetectProxiesAndVps = this.xcordYml.getBoolean(var2 + "detect-proxies-and-vps", true);
      this.antiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist = this.xcordYml
         .getBoolean(var2 + "lower-thresholds-of-bot-checks-instead-of-blacklist", false);
      var2 = "anti-bot.name-pattern.";
      this.antiBotNamePatternEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.antiBotNamePatternMinimumNameLength = this.xcordYml.getInt(var2 + "minimum-name-length", 5);
      this.antiBotNamePatternNameHistoryLength = this.xcordYml.getInt(var2 + "name-history-length", 2);
      this.antiBotNamePatternSameLengthOnly = this.xcordYml.getBoolean(var2 + "same-length-only", false);
      this.antiBotNamePatternMinimumMatchPercent = this.xcordYml.getDouble(var2 + "minimum-match-percent", 0.4);
      this.antiBotNamePatternBlacklistViolations = this.xcordYml.getInt(var2 + "blacklist-violations", 2);
      var2 = "anti-bot.anti-hang.";
      this.antiBotAntiHangEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.antiBotAntiHangOnlyDuringBotAttack = this.xcordYml.getBoolean(var2 + "only-during-bot-attack", true);
      this.antiBotAntiHangForceTimeoutTime = this.xcordYml.getInt(var2 + "force-timeout-time", 2000);
      var2 = "anti-bot.null-ping-and-ping-spam.";
      this.antiBotNullPingAndPingSpamCachePingMotd = this.xcordYml.getBoolean(var2 + "cache-ping-motd", true);
      this.antiBotNullPingAndPingSpamOnlyCacheDuringBotAttack = this.xcordYml.getBoolean(var2 + "only-cache-during-bot-attack", true);
      this.antiBotNullPingAndPingSpamCacheMilliseconds = (long)this.xcordYml.getInt(var2 + "cache-milliseconds", 1000);
      this.antiBotNullPingAndPingSpamDisableLegacyPings = this.xcordYml.getBoolean(var2 + "disable-legacy-pings", false);
      var2 = "security-manager.";
      this.securityManagerBlacklistResetTime = this.xcordYml.getInt(var2 + "blacklist-reset-time", 60);
      this.securityManagerLogBlacklist = this.xcordYml.getBoolean(var2 + "log-blacklist", true);
      this.securityManagerIgnoreAddressesEnabled = this.xcordYml.getBoolean(var2 + "ignore-addresses-enabled", false);
      this.securityManagerIpsetBlacklisting = this.xcordYml.getBoolean(var2 + "ipset-blacklisting", var3);
      this.securityManagerAutoWhitelistHaproxyAddresses = this.xcordYml.getBoolean(var2 + "auto-whitelist-haproxy-addresses", true);

      for (Object var6 : this.xcordYml.getList(var2 + "ignored-addresses", new ArrayList(1))) {
         if (var6 instanceof String) {
            this.ignoredAddresses.add((String)var6);
         }
      }

      this.ignoredAddresses.add(BungeeCord.serverAddress);
      var2 = "exploit-monitor.";
      var1.addReplacement("exploit-monitor:", "#Anti-exploit, increase each by 1.5x if they false (1.16.5+ known to false)");
      this.exploitMonitorEnabled = this.xcordYml.getBoolean(var2 + "enabled", false);
      this.exploitMonitorMaxPacketBytes = this.xcordYml.getInt(var2 + "max-packet-bytes", 16000);
      this.exploitMonitorPacketBytesPerTickLimit = this.xcordYml.getInt(var2 + "packet-bytes-per-tick-limit", 160000);
      this.exploitMonitorPacketsPerTickLimit = this.xcordYml.getInt(var2 + "packets-per-tick-limit", 1250);
      this.exploitMonitorPayloadBytesPerTickLimit = this.xcordYml.getInt(var2 + "payload-bytes-per-tick-limit", 33000);
      var2 = "debug.";
      this.debugOutputDownstreamErrors = this.xcordYml.getBoolean(var2 + "output-downstream-errors", false);
      this.debugDebugMode = this.xcordYml.getBoolean(var2 + "debug-mode", false);
      var2 = "login.";
      this.loginTickLengthMs = this.xcordYml.getInt(var2 + "tick-length-ms", 100);
      this.loginThreadPoolSize = this.xcordYml.getInt(var2 + "thread-pool-size", 5);
      var2 = "login.anti-vpn.";
      this.loginAntiVpnEnabled = this.xcordYml.getBoolean(var2 + "enabled", false);
      this.loginAntiVpnLogNonCacheDetection = this.xcordYml.getBoolean(var2 + "log-non-cache-detection", false);

      for (Object var47 : this.xcordYml.getList(var2 + "blocked-asn", new ArrayList())) {
         this.blockedAsns.add(var47.toString());
      }

      for (Object var48 : this.xcordYml.getList(var2 + "blocked-country", new ArrayList())) {
         this.blockedCountries.add(var48.toString());
      }

      this.loginAntiVpnSaveInterval = this.xcordYml.getInt(var2 + "save-interval", 3600);
      this.loginAntiVpnPurgeInterval = this.xcordYml.getInt(var2 + "purge-interval", 86400);
      this.loginAntiVpnPurgeAge = (long)this.xcordYml.getInt(var2 + "purge-age", 2592000);
      this.loginAntiVpnBlacklistOnDetection = this.xcordYml.getBoolean(var2 + "blacklist-on-detection", false);
      this.loginAntiVpnOnlyBlacklistOnDetectionDuringAttack = this.xcordYml.getBoolean(var2 + "only-blacklist-on-detection-during-attack", true);
      var2 = "login.anti-vpn.initial-connection-check";
      this.loginAntiVpnInitialConnectionCheckenabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      this.loginAntiVpnInitialConnectionCheckonlyDuringBotAttack = this.xcordYml.getBoolean(var2 + "only-during-bot-attack", true);
      this.loginAntiVpnInitialConnectionCheckcacheOnly = this.xcordYml.getBoolean(var2 + "cache-only", true);
      var2 = "login.anti-vpn.proxycheckerio-api.";
      this.loginAntiVpnProxycheckerioApiEnabled = this.xcordYml.getBoolean(var2 + "enabled", false);
      this.proxyCheckArguments = this.xcordYml.getString(var2 + "arguments", "&risk=1&vpn=1&asn=1&tag={name}");
      this.loginAntiVpnProxycheckerioApiKey = this.xcordYml.getString(var2 + "key", "");
      var2 = "login.anti-vpn.iphub-api.";
      this.loginAntiVpnIphubApiEnabled = this.xcordYml.getBoolean(var2 + "enabled", false);
      this.loginAntiVpnIphubApiKey = this.xcordYml.getString(var2 + "key", "");
      var2 = "login.anti-vpn.blackbox-api.";
      this.loginAntiVpnBlackboxApiEnabled = this.xcordYml.getBoolean(var2 + "enabled", true);
      var2 = "log-filter.";
      this.logFilterEnabled = this.xcordYml.getBoolean(var2 + "enabled", false);

      for (Object var49 : this.xcordYml.getList(var2 + "filtered", Lists.newArrayList("BotHandler", "ping", "ServerConnector", "DownstreamBridge"))) {
         this.blockedIsoCodesRaw.add(var49.toString());
      }

      var2 = "ip-spoofer.";
      this.ipSpooferSpoofListedUserIps = this.xcordYml.getBoolean(var2 + "spoof-listed-user-ips", false);
      this.ipSpooferLogOnSpoof = this.xcordYml.getBoolean(var2 + "log-on-spoof", true);
      this.ipSpoofer7222928185 = this.xcordYml.getString(var2 + "change-ip-to-this", "203.0.113.25");

      for (Object var50 : this.xcordYml.getList(var2 + "spoofed-users-case-sensitive", Lists.newArrayList("bob7l", "Notch"))) {
         this.spoofedUsers.add(var50.toString());
      }

      var2 = "fixes.";
      this.fixesFix17KickWhen18Joins = this.xcordYml.getBoolean(var2 + "fix-1_7-kick-when-1_8-joins", false);
      var2 = "compatibility.";
      this.compatibilityPayloadChannelLimit = this.xcordYml.getInt(var2 + "payload-channel-limit", 128);
      this.compatibilityAppearAsWaterfall = this.xcordYml.getBoolean(var2 + "appear-as-waterfall", false);
      this.compatibilityAlwaysFireQuitEvent = this.xcordYml.getBoolean(var2 + "always-fire-quit-event", true);
      var2 = "kernel-level-ipset-country-blocker.";
      this.kernelLevelIpsetCountryBlockerEnabled = this.xcordYml.getBoolean(var2 + "enabled", false);
      Collection var46 = this.xcordYml.getList(var2 + "blocked-iso-codes", Lists.newArrayList("CN", "IN", "RU", "UA", "HK", "GE", "ID"));
      if (this.kernelLevelIpsetCountryBlockerEnabled) {
         String[] var51 = new String[var46.size()];
         int var7 = 0;

         for (Object var9 : var46) {
            var51[var7++] = var9.toString().toLowerCase(Locale.ROOT);
         }

         this.blockedIsoCodes = var51;
      } else {
         this.blockedIsoCodes = new String[0];
      }

      try {
         var1.update(new File("xcord.yml"));
      } catch (Exception var10) {
         var10.printStackTrace();
      }
   }

   private DefinedPacket buildChatPacket(String... var1) {
      ArrayList var2 = new ArrayList();
      boolean var3 = true;

      for (String var7 : var1) {
         var2.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', (!var3 ? "\n" : "") + var7))));
         var3 = false;
      }

      return new Chat(ComponentSerializer.toString(var2.toArray(new BaseComponent[0])));
   }

   public int getAntiBotVerifyV2BlockPotentialProxiesproxyCheck1Leniency() {
      return this.antiBotVerifyV2BlockPotentialProxiesproxyCheck1Leniency;
   }

   public int getAntiBotVerifyV2BlockPotentialProxiesproxyCheck2MaxAttempts() {
      return this.antiBotVerifyV2BlockPotentialProxiesproxyCheck2MaxAttempts;
   }

   public int getAntiBotVerifyV2BlockPotentialProxiesproxyCheck2Leniency() {
      return this.antiBotVerifyV2BlockPotentialProxiesproxyCheck2Leniency;
   }

   public boolean getAntiBotVerifyV2BlockPotentialProxiesproxyCheck1Enabled() {
      return this.antiBotVerifyV2BlockPotentialProxiesproxyCheck1Enabled;
   }

   public boolean getAntiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist() {
      return this.antiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist;
   }

   public boolean getAntiBotLinuxOnlySocketCheckEnabled() {
      return this.antiBotLinuxOnlySocketCheckEnabled;
   }

   public boolean getAntiBotLinuxOnlySocketCheckEnableDuringBotAttackOnly() {
      return this.antiBotLinuxOnlySocketCheckEnableDuringBotAttackOnly;
   }

   public int getAntiBotLinuxOnlySocketCheckRequiredConnectionRate() {
      return this.antiBotLinuxOnlySocketCheckRequiredConnectionRate;
   }

   public boolean getAntiBotLinuxOnlySocketCheckDetectSuspiciousMss() {
      return this.antiBotLinuxOnlySocketCheckDetectSuspiciousMss;
   }

   public boolean getAntiBotLinuxOnlySocketCheckDetectProxiesAndVps() {
      return this.antiBotLinuxOnlySocketCheckDetectProxiesAndVps;
   }

   public boolean getAntiBotLinuxOnlySocketCheckDetectSuspicious() {
      return this.antiBotLinuxOnlySocketCheckDetectSuspicious;
   }

   public boolean getAntiBotLinuxOnlySocketCheckDetectNonWindows() {
      return this.antiBotLinuxOnlySocketCheckDetectNonWindows;
   }

   public int getAntiDdosDuringLoginInitialBufferLimitBytes() {
      return this.antiDdosDuringLoginInitialBufferLimitBytes;
   }

   public boolean getCompatibilityAlwaysFireQuitEvent() {
      return this.compatibilityAlwaysFireQuitEvent;
   }

   public boolean getPerformanceOptimizationsUseOptimizedTimeoutHandler() {
      return this.performanceOptimizationsUseOptimizedTimeoutHandler;
   }

   public boolean getGeneralDynamicallyResolveServerAddresses() {
      return this.generalDynamicallyResolveServerAddresses;
   }

   public boolean getAntiDdosDosIpFilterEnabled() {
      return this.antiDdosDosIpFilterEnabled;
   }

   public int getAntiDdosDosIpFilterUpdateDatabaseIntervalSeconds() {
      return this.antiDdosDosIpFilterUpdateDatabaseIntervalSeconds;
   }

   public boolean getAntiDdosDosIpFilterUseIpset() {
      return this.antiDdosDosIpFilterUseIpset;
   }

   public boolean getKernelLevelIpsetCountryBlockerEnabled() {
      return this.kernelLevelIpsetCountryBlockerEnabled;
   }

   public String[] getBlockedIsoCodes() {
      return this.blockedIsoCodes;
   }

   public boolean getAntiBotVerifyV2IgnoreGeyser() {
      return this.antiBotVerifyV2IgnoreGeyser;
   }

   public int getExploitMonitorPayloadBytesPerTickLimit() {
      return this.exploitMonitorPayloadBytesPerTickLimit;
   }

   public boolean getCompatibilityAppearAsWaterfall() {
      return this.compatibilityAppearAsWaterfall;
   }

   public int getCompatibilityPayloadChannelLimit() {
      return this.compatibilityPayloadChannelLimit;
   }

   public boolean getPerformanceOptimizationsUseTcpFastOpenOnServers() {
      return this.performanceOptimizationsUseTcpFastOpenOnServers;
   }

   public boolean getPerformanceOptimizationsUseTcpFastConnectOnClients() {
      return this.performanceOptimizationsUseTcpFastConnectOnClients;
   }

   public boolean getSecurityManagerAutoWhitelistHaproxyAddresses() {
      return this.securityManagerAutoWhitelistHaproxyAddresses;
   }

   public boolean getLoginAntiVpnBlackboxApiEnabled() {
      return this.loginAntiVpnBlackboxApiEnabled;
   }

   public boolean getAntiBotNullPingAndPingSpamDisableLegacyPings() {
      return this.antiBotNullPingAndPingSpamDisableLegacyPings;
   }

   public boolean getAntiBotNullPingAndPingSpamCachePingMotd() {
      return this.antiBotNullPingAndPingSpamCachePingMotd;
   }

   public boolean getAntiBotNullPingAndPingSpamOnlyCacheDuringBotAttack() {
      return this.antiBotNullPingAndPingSpamOnlyCacheDuringBotAttack;
   }

   public long getAntiBotNullPingAndPingSpamCacheMilliseconds() {
      return this.antiBotNullPingAndPingSpamCacheMilliseconds;
   }

   public int getAntiDdosImprobableBufferMaxViolations() {
      return this.antiDdosImprobableBufferMaxViolations;
   }

   public String getGeneralXCord() {
      return this.generalXCord;
   }

   public boolean getAntiBotPassthroughVerifyThenReconnectEnabled() {
      return this.antiBotPassthroughVerifyThenReconnectEnabled;
   }

   public BaseComponent[] getSuccessfulVerifyKickMessage() {
      return this.successfulVerifyKickMessage;
   }

   public boolean getFixesFix17KickWhen18Joins() {
      return this.fixesFix17KickWhen18Joins;
   }

   public boolean getPerformanceOptimizationsDontCopyPackets() {
      return this.performanceOptimizationsDontCopyPackets;
   }

   public boolean getPerformanceOptimizationsUseMergedEncodingPipeline() {
      return this.performanceOptimizationsUseMergedEncodingPipeline;
   }

   public boolean getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi() {
      return this.performanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi;
   }

   public boolean getAntiBotVerifyV2Ignore116PlusVersions() {
      return this.antiBotVerifyV2Ignore116PlusVersions;
   }

   public boolean getIpSpooferLogOnSpoof() {
      return this.ipSpooferLogOnSpoof;
   }

   public boolean getIpSpooferSpoofListedUserIps() {
      return this.ipSpooferSpoofListedUserIps;
   }

   public Set<String> getSpoofedUsers() {
      return this.spoofedUsers;
   }

   public String getIpSpoofer7222928185() {
      return this.ipSpoofer7222928185;
   }

   public boolean getSharedGlobalBlacklistEnabled() {
      return this.sharedGlobalBlacklistEnabled;
   }

   public boolean getSharedGlobalBlacklistUseIpset() {
      return this.sharedGlobalBlacklistUseIpset;
   }

   public int getSharedGlobalBlacklistUpdateDatabaseIntervalSeconds() {
      return this.sharedGlobalBlacklistUpdateDatabaseIntervalSeconds;
   }

   public boolean getGeneralOnlyBlacklistPacketErrorsDuringAttack() {
      return this.generalOnlyBlacklistPacketErrorsDuringAttack;
   }

   public boolean getAntiBotPrintConnectionStats() {
      return this.antiBotPrintConnectionStats;
   }

   public boolean getAntiDdosBlacklist() {
      return this.antiDdosBlacklist;
   }

   public boolean getAntiDdosDuringLoginEnabled() {
      return this.antiDdosDuringLoginEnabled;
   }

   public boolean getAntiDdosDuringGameplayEnabled() {
      return this.antiDdosDuringGameplayEnabled;
   }

   public int getAntiDdosDuringLoginMaxBytesPerSecond() {
      return this.antiDdosDuringLoginMaxBytesPerSecond;
   }

   public int getAntiDdosDuringLoginMaxPacketsPerSecond() {
      return this.antiDdosDuringLoginMaxPacketsPerSecond;
   }

   public int getAntiDdosDuringGameplayMaxBytesPerSecond() {
      return this.antiDdosDuringGameplayMaxBytesPerSecond;
   }

   public int getAntiDdosDuringGameplayMaxPacketsPerSecond() {
      return this.antiDdosDuringGameplayMaxPacketsPerSecond;
   }

   public int getAntiBotAntiHangForceTimeoutTime() {
      return this.antiBotAntiHangForceTimeoutTime;
   }

   public boolean getAntiBotAntiHangEnabled() {
      return this.antiBotAntiHangEnabled;
   }

   public boolean getAntiBotAntiHangOnlyDuringBotAttack() {
      return this.antiBotAntiHangOnlyDuringBotAttack;
   }

   public boolean getLoginAntiVpnLogNonCacheDetection() {
      return this.loginAntiVpnLogNonCacheDetection;
   }

   public boolean getAntiBotNamePatternEnabled() {
      return this.antiBotNamePatternEnabled;
   }

   public double getAntiBotNamePatternMinimumMatchPercent() {
      return this.antiBotNamePatternMinimumMatchPercent;
   }

   public int getAntiBotNamePatternMinimumNameLength() {
      return this.antiBotNamePatternMinimumNameLength;
   }

   public int getAntiBotNamePatternNameHistoryLength() {
      return this.antiBotNamePatternNameHistoryLength;
   }

   public boolean getAntiBotVerifyV2BlockPotentialProxiesproxyCheck2Enabled() {
      return this.antiBotVerifyV2BlockPotentialProxiesproxyCheck2Enabled;
   }

   public int getAntiBotNamePatternBlacklistViolations() {
      return this.antiBotNamePatternBlacklistViolations;
   }

   public boolean getAntiBotNamePatternSameLengthOnly() {
      return this.antiBotNamePatternSameLengthOnly;
   }

   public boolean getAntiBotTimeoutFloodEnabled() {
      return this.antiBotTimeoutFloodEnabled;
   }

   public int getAntiBotTimeoutFloodBlacklistViolations() {
      return this.antiBotTimeoutFloodBlacklistViolations;
   }

   public long getAntiBotTimeoutFloodTimeBetweenTimeouts() {
      return this.antiBotTimeoutFloodTimeBetweenTimeouts;
   }

   public boolean getLoginAntiVpnInitialConnectionCheckenabled() {
      return this.loginAntiVpnInitialConnectionCheckenabled;
   }

   public boolean getLoginAntiVpnInitialConnectionCheckcacheOnly() {
      return this.loginAntiVpnInitialConnectionCheckcacheOnly;
   }

   public boolean getLoginAntiVpnInitialConnectionCheckonlyDuringBotAttack() {
      return this.loginAntiVpnInitialConnectionCheckonlyDuringBotAttack;
   }

   public boolean getLoginAntiVpnBlacklistOnDetection() {
      return this.loginAntiVpnBlacklistOnDetection;
   }

   public boolean getLoginAntiVpnOnlyBlacklistOnDetectionDuringAttack() {
      return this.loginAntiVpnOnlyBlacklistOnDetectionDuringAttack;
   }

   public boolean getSecurityManagerIpsetBlacklisting() {
      return this.securityManagerIpsetBlacklisting;
   }

   public int getGeneralMinimumNameLength() {
      return this.generalMinimumNameLength;
   }

   public boolean getLogFilterEnabled() {
      return this.logFilterEnabled;
   }

   public Collection<String> getBlockedIsoCodesList() {
      return this.blockedIsoCodesRaw;
   }

   public int getAntiBotVerifyV2MaxCheckTime() {
      return this.antiBotVerifyV2MaxCheckTime;
   }

   public int getGeneralNewConnectionSpeedThreshold() {
      return this.generalNewConnectionSpeedThreshold;
   }

   public boolean getAntiBotVerifyV2VerifyPackets() {
      return this.antiBotVerifyV2VerifyPackets;
   }

   public boolean getAntiBotVerifyV2VerifyBrand() {
      return this.antiBotVerifyV2VerifyBrand;
   }

   public int getAntiBotVerifyV2MaxPing() {
      return this.antiBotVerifyV2MaxPing;
   }

   public int getLoginAntiVpnSaveInterval() {
      return this.loginAntiVpnSaveInterval;
   }

   public int getLoginAntiVpnPurgeInterval() {
      return this.loginAntiVpnPurgeInterval;
   }

   public long getLoginAntiVpnPurgeAge() {
      return this.loginAntiVpnPurgeAge;
   }

   public boolean getAntiBotVerifyV2Enabled() {
      return this.antiBotVerifyV2Enabled;
   }

   public boolean getLoginAntiVpnEnabled() {
      return this.loginAntiVpnEnabled;
   }

   public Set<String> getBlockedAsns() {
      return this.blockedAsns;
   }

   public Set<String> getBlockedCountries() {
      return this.blockedCountries;
   }

   public boolean getLoginAntiVpnProxycheckerioApiEnabled() {
      return this.loginAntiVpnProxycheckerioApiEnabled;
   }

   public String getLoginAntiVpnProxycheckerioApiKey() {
      return this.loginAntiVpnProxycheckerioApiKey;
   }

   public String getProxyCheckArguments() {
      return this.proxyCheckArguments;
   }

   public boolean getLoginAntiVpnIphubApiEnabled() {
      return this.loginAntiVpnIphubApiEnabled;
   }

   public String getLoginAntiVpnIphubApiKey() {
      return this.loginAntiVpnIphubApiKey;
   }

   public int getLoginThreadPoolSize() {
      return this.loginThreadPoolSize;
   }

   public int getLoginTickLengthMs() {
      return this.loginTickLengthMs;
   }

   public boolean getDebugOutputDownstreamErrors() {
      return this.debugOutputDownstreamErrors;
   }

   public boolean isIgnoredAddress(String var1) {
      return this.securityManagerIgnoreAddressesEnabled && this.ignoredAddresses.contains(var1);
   }

   public boolean isIgnoredAddress(InetAddress var1) {
      return !PipelineUtils.isPublicIP(var1) ? true : this.securityManagerIgnoreAddressesEnabled && this.ignoredAddresses.contains(var1.getHostAddress());
   }

   public boolean addIgnoredAddress(String var1) {
      this.securityManagerIgnoreAddressesEnabled = true;
      return this.ignoredAddresses.add(var1);
   }

   public int getAntiBotUnstableCheckBlacklistViolations() {
      return this.antiBotUnstableCheckBlacklistViolations;
   }

   public int getAntiBotStrangeNameBlacklistViolations() {
      return this.antiBotStrangeNameBlacklistViolations;
   }

   public boolean getAntiBotUnstableCheckDelayKickForReason() {
      return this.antiBotUnstableCheckDelayKickForReason;
   }

   public boolean getAntiBotUnstableCheckEnabled() {
      return this.antiBotUnstableCheckEnabled;
   }

   public int getAntiBotUnstableCheckLatencyThreshold() {
      return this.antiBotUnstableCheckLatencyThreshold;
   }

   public boolean getAntiBotStrangeNameEnabled() {
      return this.antiBotStrangeNameEnabled;
   }

   public int getAntiBotStrangeNameRandomNumberCount() {
      return this.antiBotStrangeNameRandomNumberCount;
   }

   public int getAntiBotStrangeNameRandomCapitalCount() {
      return this.antiBotStrangeNameRandomCapitalCount;
   }

   public boolean getDebugDebugMode() {
      return this.debugDebugMode;
   }

   public int getGeneralCompressionStrength() {
      return this.generalCompressionStrength;
   }

   public int getGeneralKickTimeout() {
      return this.generalKickTimeout;
   }

   public int getGeneralInitialTimeout() {
      return this.generalInitialTimeout;
   }

   public boolean getExploitMonitorEnabled() {
      return this.exploitMonitorEnabled;
   }

   public int getExploitMonitorMaxPacketBytes() {
      return this.exploitMonitorMaxPacketBytes;
   }

   public int getExploitMonitorPacketBytesPerTickLimit() {
      return this.exploitMonitorPacketBytesPerTickLimit;
   }

   public int getExploitMonitorPacketsPerTickLimit() {
      return this.exploitMonitorPacketsPerTickLimit;
   }

   public boolean getAntiBotDnsCheckIsStrict1() {
      return this.antiBotDnsCheckIsStrict1;
   }

   public int getAntiBotIpTrackingTimeout() {
      return this.antiBotIpTrackingTimeout;
   }

   public int getAntiBotDisableChecksTime() {
      return this.antiBotDisableChecksTime;
   }

   public boolean getAntiBotLinuxCheck() {
      return this.antiBotLinuxCheck;
   }

   public boolean getAntiBotChatCheck() {
      return this.antiBotChatCheck;
   }

   public boolean getAntiBotDnsCheck() {
      return this.antiBotDnsCheck;
   }

   public boolean getAntiBotDnsCheckIsStrict() {
      return this.antiBotDnsCheckIsStrict;
   }

   public int getAntiBotChatForceDelay() {
      return this.antiBotChatForceDelay;
   }

   public int getGeneralBaseTick() {
      return this.generalBaseTick;
   }

   public int getAntiBotWhitelistSurviveTime() {
      return this.antiBotWhitelistSurviveTime;
   }

   public int getAntiBotWhitelistPurgeInterval() {
      return this.antiBotWhitelistPurgeInterval;
   }

   public int getAntiBotTimeToWhitelist() {
      return this.antiBotTimeToWhitelist;
   }

   public int getAntiBotTimeGroupingTick() {
      return this.antiBotTimeGroupingTick;
   }

   public int getAntiBotAntiBotDeactivateDelay() {
      return this.antiBotAntiBotDeactivateDelay;
   }

   public int getAntiBotAntiBotActivateConnections() {
      return this.antiBotAntiBotActivateConnections;
   }

   public int getAntiBotSpeedyLoginMaxLoginsPerTick() {
      return this.antiBotSpeedyLoginMaxLoginsPerTick;
   }

   public int getAntiBotSpeedyLoginBlacklistViolations() {
      return this.antiBotSpeedyLoginBlacklistViolations;
   }

   public int getAntiBotSpeedyConnectionMaxConnectionsPerTick() {
      return this.antiBotSpeedyConnectionMaxConnectionsPerTick;
   }

   public int getAntiBotSpeedyConnectionBlacklistViolations() {
      return this.antiBotSpeedyConnectionBlacklistViolations;
   }

   public int getAntiBotLoginCheckTimeoutTime() {
      return this.antiBotLoginCheckTimeoutTime;
   }

   public int getAntiBotLoginCheckStrangeConnectionDelay() {
      return this.antiBotLoginCheckStrangeConnectionDelay;
   }

   public int getSecurityManagerBlacklistResetTime() {
      return this.securityManagerBlacklistResetTime;
   }

   public boolean getSecurityManagerLogBlacklist() {
      return this.securityManagerLogBlacklist;
   }

   public boolean getGeneralAlwaysCheckAllowedNameCharacters() {
      return this.generalAlwaysCheckAllowedNameCharacters;
   }

   public boolean getAntiBotVerifyV2AdvancedLobbyCheckEnabled() {
      return this.antiBotVerifyV2AdvancedLobbyCheckEnabled;
   }

   public DefinedPacket getVerifyingPacket() {
      return this.verifyingPacket;
   }

   public DefinedPacket getVerifiedPacket() {
      return this.verifiedPacket;
   }

   public int getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts() {
      return this.antiBotVerifyV2AdvancedLobbyCheckMaxAttempts;
   }

   public boolean getAntiBotVerifyV2AdvancedLobbyCheckTestCollision() {
      return this.antiBotVerifyV2AdvancedLobbyCheckTestCollision;
   }

   public int getAntiDdosDosIpFilterEnableAtThisConnectionsPerSecond() {
      return this.antiDdosDosIpFilterEnableAtThisConnectionsPerSecond;
   }
}
