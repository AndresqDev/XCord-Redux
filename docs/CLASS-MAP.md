# Class Map

This map applies to the reconstruction of **XCord 1.56**. The decompilation and
deobfuscation process was only tested and validated on version 1.56.

The reconstruction renames 86 classes, 362 fields, and 371 methods. Inner
classes retain their outer class mapping.

| Reconstructed | Original | Purpose |
| --- | --- | --- |
| `me.minebuilders.proxyfork.antiproxy.AntiProxyManager` | `me.minebuilders.proxyfork.antiproxy.a` | Checks proxies and VPNs and caches results. |
| `me.minebuilders.proxyfork.antiproxy.ProxyCheckResult` | `me.minebuilders.proxyfork.antiproxy.c` | Result data: country, ASN, timestamp. |
| `me.minebuilders.proxyfork.antiproxy.ProxyResult` | `me.minebuilders.proxyfork.antiproxy.b` | Anti-proxy check result. |
| `xcord.antibot.AntiBotManager` | `B` | Bot-attack detection, per-IP counters, temporary whitelist. |
| `xcord.network.AntiDdosFrameDecoder` | `o` | Frame decoder with per-second anti-DDoS limits. |
| `xcord.service.AsyncJob` | `am` | Asynchronous job interface. |
| `xcord.service.AsyncJobService` | `f` | Asynchronous job queue and pool. |
| `xcord.antivpn.BlackBoxApi` | `G` | Anti-proxy provider for `blackbox.ipinfo.app`. |
| `xcord.blacklist.Blacklist` | `K` | Blacklist interface: add, contains, purge, isEmpty. |
| `xcord.blacklist.BlacklistManager` | `r` | Per-IP blacklist management using IPSet or memory. |
| `xcord.antibot.BotCheckHandler` | `U` | Advanced verification: fake lobby, keepalive, position, gravity. |
| `xcord.antibot.BotCheckHandler$1` | `U$1` | Anonymous `DBP_1_0_1` transformer. |
| `xcord.antibot.BotHandler` | `S` | VerifyV2 verification handler. |
| `xcord.antibot.CheckType` | `C` | Check type enum: verify, speedy-login, timeouts, and others. |
| `xcord.network.CompressionDecoder` | `ag` | Zlib decoder with a compression threshold. |
| `xcord.network.CompressionEncoder` | `ah` | Zlib encoder with a compression threshold. |
| `xcord.config.ConfigUpdater` | `A` | Updates `xcord.yml` by inserting new keys. |
| `xcord.antibot.ConnectionStats` | `E` | Per-IP statistics and violation counters. |
| `xcord.country.CountryBlockService` | `j` | Service that applies country blocking. |
| `xcord.country.CountryBlocklist` | `L` | ISO country blocking using ipdeny.com zones. |
| `xcord.service.DelayedJob` | `al` | Delayed job. |
| `xcord.network.DnsResolver` | `ar` | DNS resolver with a TCP fallback. |
| `xcord.blacklist.DosFilterBlacklist` | `k` | DoS-filter blacklist loaded from local `dosblacklist.txt`. |
| `xcord.blacklist.DosFilterIPSet` | `Q` | IPSet variant for the DoS filter. |
| `xcord.network.EpollSocketCheck` | `W` | TCP checks (MSS/options/RTT) over epoll. |
| `xcord.network.ExploitMonitor` | `R` | Per-tick byte/packet limits for exploit protection. |
| `xcord.blacklist.FileBlacklist` | `M` | Blacklist interface loadable from a file. |
| `xcord.blacklist.GlobalBlacklist` | `m` | Global blacklist loaded from local `blacklistv2.txt`. |
| `xcord.blacklist.GlobalBlacklist$DownloadTask` | `m$a` | Original remote download task; removed from the current build. |
| `xcord.antivpn.IPHubApi` | `H` | Anti-proxy provider for `iphub.info`. |
| `xcord.blacklist.IPSetBlacklist` | `N` | IPSet/iptables-backed blacklist. |
| `xcord.blacklist.IPSetWriter` | `n` | Batched IPSet writer using add/restore. |
| `xcord.Inject` | `a` | Relocated `javax.inject` `@Inject` annotation. |
| `xcord.blacklist.IpIntBlacklist` | `P` | IPv4 range blacklist backed by a TIntSet. |
| `xcord.antibot.LegacyBotHandler` | `T` | Legacy verification handler. |
| `xcord.logging.Log4jFilter` | `ao` | Log4jShell filter for `${...}` patterns. |
| `xcord.logging.LogFilter` | `l` | Configurable log filter. |
| `xcord.blacklist.MemoryBlacklist` | `O` | In-memory blacklist backed by a `ConcurrentHashMap`. |
| `xcord.command.MemoryCommand` | `Z` | `/testmemory` command and memory profiler. |
| `xcord.command.MemoryCommand$1` | `Z$1` | Synthetic command switch. |
| `xcord.command.MemoryCommand$MemoryStatsTask` | `Z$a` | Memory statistics task. |
| `xcord.network.MergedCompressionEncoder` | `ai` | Zlib encoder with a merged packet length. |
| `xcord.message.MessageTemplate` | `h` | Precompiled message: JSON, components, and legacy text. |
| `xcord.message.Messages` | `z` | Messages from `xcord-messages.yml`, including kick messages. |
| `xcord.antibot.NameValidator` | `aq` | Player-name character validation. |
| `xcord.Named` | `b` | `@Named` annotation. |
| `xcord.network.NioSocketCheck` | `X` | Socket checks for NIO without TCP information. |
| `xcord.network.OptimizedReadTimeoutHandler` | `p` | Optimized `ReadTimeoutHandler`. |
| `xcord.network.OptimizedReadTimeoutHandler$TimeoutTask` | `p$a` | Timeout task. |
| `xcord.protocol.PacketRegistry` | `an` | Packet registry keyed by protocol version. |
| `xcord.protocol.PacketTransformer` | `i` | Packet transformer for CBP/DBP. |
| `xcord.storage.PersistentMap` | `aj` | Serializable map persisted to disk. |
| `xcord.util.ProcessUtils` | `as` | IP and process-execution utilities. |
| `xcord.Provider` | `c` | `Provider<T>` interface. |
| `xcord.antivpn.ProxyCheckApi` | `J` | Anti-proxy provider for `proxycheck.io`. |
| `xcord.antivpn.ProxyCheckJob` | `ak` | Asynchronous anti-proxy check job. |
| `xcord.antivpn.ProxyCheckProvider` | `I` | Anti-proxy provider interface. |
| `xcord.Qualifier` | `d` | `@Qualifier` annotation. |
| `xcord.service.ReloadableService` | `q` | Reloadable service interface. |
| `xcord.util.SilentException` | `D` | Exception without a stack trace. |
| `xcord.antibot.SimilarityCache` | `F` | Levenshtein similarity cache for similar-name detection. |
| `xcord.antibot.SimilarityCache$Match` | `F$a` | Similarity result: score and value. |
| `xcord.Singleton` | `e` | `@Singleton` annotation. |
| `xcord.network.SocketCheck` | `V` | Socket check interface. |
| `xcord.network.SocketCheckDispatcher` | `Y` | Epoll/NIO check dispatcher. |
| `xcord.network.SpoofedServerConnection` | `s` | Fake server connection for the verification lobby. |
| `xcord.network.SpoofedServerConnection$1` | `s$1` | Empty channel wrapper for the fake connection. |
| `xcord.util.StringInterner` | `t` | String interning helper. |
| `xcord.command.SubCommand` | `aa` | Base subcommand. |
| `xcord.command.SubCommandDispatcher` | `ac` | `/xvpn` subcommand dispatcher. |
| `xcord.network.TcpDnsResolver` | `u` | TCP DNS resolver using `8.8.8.8`. |
| `xcord.network.TcpDnsResolver$1` | `u$1` | DNS channel initializer. |
| `xcord.network.TcpDnsResolver$1$1` | `u$1$1` | DNS response handler. |
| `xcord.network.TcpDnsResolver$1$2` | `u$1$2` | DNS exception handler. |
| `xcord.service.TickableService` | `v` | Tickable service interface. |
| `xcord.command.TimedEventsCommand` | `ab` | `/timedelayedevents` command and timing event bus. |
| `xcord.command.TimedEventsCommand$TimingEventBus` | `ab$a` | Event bus decorated with timing data. |
| `xcord.protocol.VarIntByteProcessor` | `x` | Stateful VarInt byte processor. |
| `xcord.protocol.VarIntByteProcessor$State` | `x$a` | VarInt read states. |
| `xcord.protocol.VarIntFrameDecoder` | `w` | Base Minecraft VarInt frame decoder. |
| `xcord.protocol.VarInts` | `ap` | Static VarInt utilities: read, write, size. |
| `xcord.command.WhitelistAddCommand` | `af` | `/xvpn whitelist <ip-or-user>`. |
| `xcord.command.WhitelistListCommand` | `ad` | `/xvpn listwhitelisted`. |
| `xcord.command.WhitelistRemoveCommand` | `ae` | `/xvpn unwhitelist <ip-or-user>`. |
| `xcord.XCord` | `g` | Core singleton: startup, configuration, services, and lifecycle. |
| `xcord.config.XCordConfig` | `y` | `xcord.yml` parser with roughly 150 typed option getters. |

## Mapping files

`mapping.txt` contains the raw remapping entries used by `tools/remap.sh`:

```text
C <old/internal/Name> <new/internal/Name>
F <owner> <fieldName> <descriptor> <newFieldName>
M <owner> <methodName> <descriptor> <newMethodName>
```

`map-config.txt` contains the `XCordConfig` field names derived from the
`xcord.yml` keys. Regenerate both files with:

```bash
python3 tools/gen_config.py
python3 tools/gen_map.py
```
