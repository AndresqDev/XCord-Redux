# Class Map

The reconstruction renames 86 classes, 362 fields, and 371 methods. Inner
classes retain their outer class mapping.

| Reconstructed | Original | Purpose |
| --- | --- | --- |
| `me.minebuilders.proxyfork.antiproxy.AntiProxyManager` | `me.minebuilders.proxyfork.antiproxy.a` | Checks proxies and VPNs and caches results. |
| `me.minebuilders.proxyfork.antiproxy.ProxyCheckResult` | `me.minebuilders.proxyfork.antiproxy.c` | Result data: country, ASN, timestamp. |
| `me.minebuilders.proxyfork.antiproxy.ProxyResult` | `me.minebuilders.proxyfork.antiproxy.b` | Anti-proxy check result. |
| `xcord.AntiBotManager` | `B` | Bot-attack detection, per-IP counters, temporary whitelist. |
| `xcord.AntiDdosFrameDecoder` | `o` | Frame decoder with per-second anti-DDoS limits. |
| `xcord.AsyncJob` | `am` | Asynchronous job interface. |
| `xcord.AsyncJobService` | `f` | Asynchronous job queue and pool. |
| `xcord.BlackBoxApi` | `G` | Anti-proxy provider for `blackbox.ipinfo.app`. |
| `xcord.Blacklist` | `K` | Blacklist interface: add, contains, purge, isEmpty. |
| `xcord.BlacklistManager` | `r` | Per-IP blacklist management using IPSet or memory. |
| `xcord.BotCheckHandler` | `U` | Advanced verification: fake lobby, keepalive, position, gravity. |
| `xcord.BotCheckHandler$1` | `U$1` | Anonymous `DBP_1_0_1` transformer. |
| `xcord.BotHandler` | `S` | VerifyV2 verification handler. |
| `xcord.CheckType` | `C` | Check type enum: verify, speedy-login, timeouts, and others. |
| `xcord.CompressionDecoder` | `ag` | Zlib decoder with a compression threshold. |
| `xcord.CompressionEncoder` | `ah` | Zlib encoder with a compression threshold. |
| `xcord.ConfigUpdater` | `A` | Updates `xcord.yml` by inserting new keys. |
| `xcord.ConnectionStats` | `E` | Per-IP statistics and violation counters. |
| `xcord.CountryBlockService` | `j` | Service that applies country blocking. |
| `xcord.CountryBlocklist` | `L` | ISO country blocking using ipdeny.com zones. |
| `xcord.DelayedJob` | `al` | Delayed job. |
| `xcord.DnsResolver` | `ar` | DNS resolver with a TCP fallback. |
| `xcord.DosFilterBlacklist` | `k` | DoS-filter blacklist loaded from local `dosblacklist.txt`. |
| `xcord.DosFilterIPSet` | `Q` | IPSet variant for the DoS filter. |
| `xcord.EpollSocketCheck` | `W` | TCP checks (MSS/options/RTT) over epoll. |
| `xcord.ExploitMonitor` | `R` | Per-tick byte/packet limits for exploit protection. |
| `xcord.FileBlacklist` | `M` | Blacklist interface loadable from a file. |
| `xcord.GlobalBlacklist` | `m` | Global blacklist loaded from local `blacklistv2.txt`. |
| `xcord.GlobalBlacklist$DownloadTask` | `m$a` | Original remote download task; removed from the current build. |
| `xcord.IPHubApi` | `H` | Anti-proxy provider for `iphub.info`. |
| `xcord.IPSetBlacklist` | `N` | IPSet/iptables-backed blacklist. |
| `xcord.IPSetWriter` | `n` | Batched IPSet writer using add/restore. |
| `xcord.Inject` | `a` | Relocated `javax.inject` `@Inject` annotation. |
| `xcord.IpIntBlacklist` | `P` | IPv4 range blacklist backed by a TIntSet. |
| `xcord.LegacyBotHandler` | `T` | Legacy verification handler. |
| `xcord.Log4jFilter` | `ao` | Log4jShell filter for `${...}` patterns. |
| `xcord.LogFilter` | `l` | Configurable log filter. |
| `xcord.MemoryBlacklist` | `O` | In-memory blacklist backed by a `ConcurrentHashMap`. |
| `xcord.MemoryCommand` | `Z` | `/testmemory` command and memory profiler. |
| `xcord.MemoryCommand$1` | `Z$1` | Synthetic command switch. |
| `xcord.MemoryCommand$MemoryStatsTask` | `Z$a` | Memory statistics task. |
| `xcord.MergedCompressionEncoder` | `ai` | Zlib encoder with a merged packet length. |
| `xcord.MessageTemplate` | `h` | Precompiled message: JSON, components, and legacy text. |
| `xcord.Messages` | `z` | Messages from `xcord-messages.yml`, including kick messages. |
| `xcord.NameValidator` | `aq` | Player-name character validation. |
| `xcord.Named` | `b` | `@Named` annotation. |
| `xcord.NioSocketCheck` | `X` | Socket checks for NIO without TCP information. |
| `xcord.OptimizedReadTimeoutHandler` | `p` | Optimized `ReadTimeoutHandler`. |
| `xcord.OptimizedReadTimeoutHandler$TimeoutTask` | `p$a` | Timeout task. |
| `xcord.PacketRegistry` | `an` | Packet registry keyed by protocol version. |
| `xcord.PacketTransformer` | `i` | Packet transformer for CBP/DBP. |
| `xcord.PersistentMap` | `aj` | Serializable map persisted to disk. |
| `xcord.ProcessUtils` | `as` | IP and process-execution utilities. |
| `xcord.Provider` | `c` | `Provider<T>` interface. |
| `xcord.ProxyCheckApi` | `J` | Anti-proxy provider for `proxycheck.io`. |
| `xcord.ProxyCheckJob` | `ak` | Asynchronous anti-proxy check job. |
| `xcord.ProxyCheckProvider` | `I` | Anti-proxy provider interface. |
| `xcord.Qualifier` | `d` | `@Qualifier` annotation. |
| `xcord.ReloadableService` | `q` | Reloadable service interface. |
| `xcord.SilentException` | `D` | Exception without a stack trace. |
| `xcord.SimilarityCache` | `F` | Levenshtein similarity cache for similar-name detection. |
| `xcord.SimilarityCache$Match` | `F$a` | Similarity result: score and value. |
| `xcord.Singleton` | `e` | `@Singleton` annotation. |
| `xcord.SocketCheck` | `V` | Socket check interface. |
| `xcord.SocketCheckDispatcher` | `Y` | Epoll/NIO check dispatcher. |
| `xcord.SpoofedServerConnection` | `s` | Fake server connection for the verification lobby. |
| `xcord.SpoofedServerConnection$1` | `s$1` | Empty channel wrapper for the fake connection. |
| `xcord.StringInterner` | `t` | String interning helper. |
| `xcord.SubCommand` | `aa` | Base subcommand. |
| `xcord.SubCommandDispatcher` | `ac` | `/xvpn` subcommand dispatcher. |
| `xcord.TcpDnsResolver` | `u` | TCP DNS resolver using `8.8.8.8`. |
| `xcord.TcpDnsResolver$1` | `u$1` | DNS channel initializer. |
| `xcord.TcpDnsResolver$1$1` | `u$1$1` | DNS response handler. |
| `xcord.TcpDnsResolver$1$2` | `u$1$2` | DNS exception handler. |
| `xcord.TickableService` | `v` | Tickable service interface. |
| `xcord.TimedEventsCommand` | `ab` | `/timedelayedevents` command and timing event bus. |
| `xcord.TimedEventsCommand$TimingEventBus` | `ab$a` | Event bus decorated with timing data. |
| `xcord.VarIntByteProcessor` | `x` | Stateful VarInt byte processor. |
| `xcord.VarIntByteProcessor$State` | `x$a` | VarInt read states. |
| `xcord.VarIntFrameDecoder` | `w` | Base Minecraft VarInt frame decoder. |
| `xcord.VarInts` | `ap` | Static VarInt utilities: read, write, size. |
| `xcord.WhitelistAddCommand` | `af` | `/xvpn whitelist <ip-or-user>`. |
| `xcord.WhitelistListCommand` | `ad` | `/xvpn listwhitelisted`. |
| `xcord.WhitelistRemoveCommand` | `ae` | `/xvpn unwhitelist <ip-or-user>`. |
| `xcord.XCord` | `g` | Core singleton: startup, configuration, services, and lifecycle. |
| `xcord.XCordConfig` | `y` | `xcord.yml` parser with roughly 150 typed option getters. |

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
