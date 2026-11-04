#!/usr/bin/env python3
import os
import re
from collections import defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MEMBERS = os.path.join(ROOT, "reference", "members.txt")

CLASSES = {
    "a": "Inject",
    "b": "Named",
    "c": "Provider",
    "d": "Qualifier",
    "e": "Singleton",
    "f": "AsyncJobService",
    "g": "XCord",
    "h": "MessageTemplate",
    "i": "PacketTransformer",
    "j": "CountryBlockService",
    "k": "DosFilterBlacklist",
    "l": "LogFilter",
    "m": "GlobalBlacklist",
    "n": "IPSetWriter",
    "o": "AntiDdosFrameDecoder",
    "p": "OptimizedReadTimeoutHandler",
    "q": "ReloadableService",
    "r": "BlacklistManager",
    "s": "SpoofedServerConnection",
    "t": "StringInterner",
    "u": "TcpDnsResolver",
    "v": "TickableService",
    "w": "VarIntFrameDecoder",
    "x": "VarIntByteProcessor",
    "y": "XCordConfig",
    "z": "Messages",
    "A": "ConfigUpdater",
    "B": "AntiBotManager",
    "C": "CheckType",
    "D": "SilentException",
    "E": "ConnectionStats",
    "F": "SimilarityCache",
    "G": "BlackBoxApi",
    "H": "IPHubApi",
    "I": "ProxyCheckProvider",
    "J": "ProxyCheckApi",
    "K": "Blacklist",
    "L": "CountryBlocklist",
    "M": "FileBlacklist",
    "N": "IPSetBlacklist",
    "O": "MemoryBlacklist",
    "P": "IpIntBlacklist",
    "Q": "DosFilterIPSet",
    "R": "ExploitMonitor",
    "S": "BotHandler",
    "T": "LegacyBotHandler",
    "U": "BotCheckHandler",
    "V": "SocketCheck",
    "W": "EpollSocketCheck",
    "X": "NioSocketCheck",
    "Y": "SocketCheckDispatcher",
    "Z": "MemoryCommand",
    "aa": "SubCommand",
    "ab": "TimedEventsCommand",
    "ac": "SubCommandDispatcher",
    "ad": "WhitelistListCommand",
    "ae": "WhitelistRemoveCommand",
    "af": "WhitelistAddCommand",
    "ag": "CompressionDecoder",
    "ah": "CompressionEncoder",
    "ai": "MergedCompressionEncoder",
    "aj": "PersistentMap",
    "ak": "ProxyCheckJob",
    "al": "DelayedJob",
    "am": "AsyncJob",
    "an": "PacketRegistry",
    "ao": "Log4jFilter",
    "ap": "VarInts",
    "aq": "NameValidator",
    "ar": "DnsResolver",
    "as": "ProcessUtils",
    "F$a": "SimilarityCache$Match",
    "m$a": "GlobalBlacklist$DownloadTask",
    "p$a": "OptimizedReadTimeoutHandler$TimeoutTask",
    "u$1": "TcpDnsResolver$1",
    "u$1$1": "TcpDnsResolver$1$1",
    "u$1$2": "TcpDnsResolver$1$2",
    "ab$a": "TimedEventsCommand$TimingEventBus",
    "s$1": "SpoofedServerConnection$1",
    "Z$a": "MemoryCommand$MemoryStatsTask",
    "Z$1": "MemoryCommand$1",
    "U$1": "BotCheckHandler$1",
    "x$a": "VarIntByteProcessor$State",
    "me/minebuilders/proxyfork/antiproxy/a": "me/minebuilders/proxyfork/antiproxy/AntiProxyManager",
    "me/minebuilders/proxyfork/antiproxy/b": "me/minebuilders/proxyfork/antiproxy/ProxyResult",
    "me/minebuilders/proxyfork/antiproxy/c": "me/minebuilders/proxyfork/antiproxy/ProxyCheckResult",
}

FIELDS = {
    "y": {
        ("a", "[Ljava/lang/String;"): "blockedIsoCodes",
        ("a", "[Lnet/md_5/bungee/api/chat/BaseComponent;"): "successfulVerifyKickMessage",
        ("a", "Lnet/md_5/bungee/protocol/DefinedPacket;"): "verifyingPacket",
        ("b", "Lnet/md_5/bungee/protocol/DefinedPacket;"): "verifiedPacket",
        ("a", "Ljava/util/List;"): "blockedIsoCodesRaw",
        ("a", "Ljava/util/Set;"): "ignoredAddresses",
        ("b", "Ljava/util/Set;"): "blockedAsns",
        ("c", "Ljava/util/Set;"): "blockedCountries",
        ("d", "Ljava/util/Set;"): "filteredLogMessages",
        ("e", "Ljava/util/Set;"): "spoofedUsers",
        ("b", "Ljava/lang/String;"): "proxyCheckArguments",
        ("d", "Ljava/lang/String;"): "changeIpToThis",
    },
    "g": {
        ("a", "Lg"): "instance",
        ("a", "Ly"): "config",
        ("a", "Lr"): "blacklistManager",
        ("a", "LB"): "antiBotManager",
        ("a", "Lz"): "messages",
        ("a", "Lf"): "asyncJobService",
        ("a", "Lme/minebuilders/proxyfork/antiproxy/a"): "antiProxyManager",
        ("a", "Lm"): "globalBlacklist",
        ("a", "Lk"): "dosFilterBlacklist",
        ("a", "Z"): "logFilterEnabled",
        ("a", "I"): "tick",
        ("a", "Ljava/util/List;"): "tickableServices",
        ("b", "Ljava/util/List;"): "reloadableServices",
    },
    "z": {
        ("a", "Lh"): "slowLogin",
        ("b", "Lh"): "failedConnection",
        ("c", "Lh"): "linuxCheckFail",
        ("d", "Lh"): "speedJoinFail",
        ("e", "Lh"): "shortName",
        ("f", "Lh"): "illegalName",
        ("g", "Lh"): "vpn",
        ("h", "Lh"): "highPing",
        ("i", "Lh"): "proxyLogicVerifyV2",
    },
    "h": {
        ("a", "Ljava/lang/String;"): "json",
        ("a", "[Lnet/md_5/bungee/api/chat/BaseComponent;"): "components",
        ("b", "Ljava/lang/String;"): "legacyText",
    },
    "B": {
        ("a", "Ljava/util/Map;"): "connectionStats",
        ("a", "Ljava/util/concurrent/ConcurrentHashMap;"): "whitelistRecords",
        ("a", "Ljava/util/concurrent/atomic/AtomicInteger;"): "connectionCounter",
        ("a", "Ljava/util/concurrent/atomic/AtomicBoolean;"): "attackActive",
        ("a", "I"): "tickCounter",
        ("b", "I"): "purgeCounter",
        ("c", "I"): "lastConnectionCount",
        ("a", "LF;"): "similarNameCache",
    },
    "E": {
        ("a", "Ljava/lang/String;"): "ip",
        ("a", "J"): "firstSeen",
        ("b", "J"): "lastSeen",
        ("c", "J"): "lastAction",
        ("a", "I"): "loginCounter",
        ("b", "I"): "connectionCounter",
        ("c", "I"): "actionValue",
        ("a", "Z"): "whitelisted",
        ("b", "Z"): "flagged",
        ("a", "[I"): "violations",
    },
    "F": {
        ("a", "Z"): "ignoreCase",
        ("a", "[Ljava/lang/String;"): "cache",
        ("a", "I"): "index",
    },
    "F$a": {
        ("a", "D"): "score",
        ("a", "Ljava/lang/String;"): "value",
    },
    "U": {
        ("a", "Lgnu/trove/map/TIntObjectMap;"): "LOGIN_DIMENSIONS",
        ("b", "Lan;"): "CBP_REGISTRY",
        ("a", "Lan;"): "DBP_REGISTRY",
        ("a", "I"): "checkId",
        ("a", "Lnet/md_5/bungee/UserConnection;"): "user",
        ("a", "Lnet/md_5/bungee/connection/InitialHandler;"): "initialHandler",
        ("a", "Ljava/lang/Runnable;"): "callback",
        ("a", "Z"): "verified",
        ("b", "Z"): "sentPayload",
        ("c", "Z"): "sentInitials",
        ("d", "I"): "ping",
        ("b", "I"): "protocolVersion",
        ("f", "Z"): "sentFakeLobby",
        ("e", "I"): "verifyAttempts",
        ("f", "I"): "fakeLobbyY",
        ("c", "I"): "keepAliveId",
        ("d", "Z"): "awaitingPosition",
        ("e", "Z"): "receivedKeepAlive",
        ("a", "D"): "lastY",
        ("b", "D"): "lastDeltaY",
        ("g", "I"): "fallCount",
        ("a", "J"): "lastPacketTime",
        ("g", "Z"): "advancedCheckEnabled",
        ("h", "Z"): "challengeSent",
        ("i", "Z"): "challengeAnswered",
        ("b", "J"): "challengeSentTime",
        ("h", "I"): "expectedKeepAliveId",
        ("i", "I"): "challengeAttempts",
        ("j", "Z"): "positionChecked",
        ("j", "I"): "invalidKeepAlives",
    },
    "S": {
        ("b", "Lnet/md_5/bungee/UserConnection;"): "user",
        ("f", "Z"): "sentRawData",
        ("a", "J"): "challengeTime",
    },
    "T": {
        ("f", "Z"): "sentKeepAlive",
        ("a", "J"): "challengeTime",
    },
    "R": {
        ("a", "I"): "largestPacket",
        ("b", "I"): "largestBytes",
        ("c", "I"): "largestPackets",
        ("a", "Ljava/lang/String;"): "playerName",
        ("d", "I"): "bytesThisTick",
        ("e", "I"): "packetsThisTick",
        ("f", "I"): "payloadBytesThisTick",
        ("g", "I"): "lastTick",
    },
    "m": {
        ("a", "Ljava/lang/String;"): "name",
        ("a", "LM;"): "backend",
        ("a", "I"): "nextUpdate",
        ("b", "Ljava/lang/String;"): "blacklistName",
    },
    "k": {
        ("a", "Z"): "active",
    },
    "r": {
        ("a", "I"): "tick",
        ("a", "LK;"): "blacklist",
    },
    "N": {
        ("a", "Ljava/util/concurrent/ExecutorService;"): "executor",
        ("a", "Ljava/lang/String;"): "tableName",
        ("b", "Ljava/lang/String;"): "hashType",
        ("a", "Z"): "failed",
        ("c", "Ljava/lang/String;"): "iptablesMatch",
        ("a", "Ln;"): "writer",
    },
    "O": {
        ("a", "Ljava/lang/Object;"): "value",
        ("a", "Ljava/util/Map;"): "entries",
    },
    "P": {
        ("a", "Lgnu/trove/set/TIntSet;"): "addresses",
    },
    "n": {
        ("a", "Ljava/io/File;"): "tempFile",
        ("a", "Ljava/util/Queue;"): "queue",
        ("a", "Ljava/util/concurrent/atomic/AtomicInteger;"): "pending",
        ("a", "Ljava/lang/String;"): "tableName",
        ("a", "I"): "intervalMs",
        ("b", "Ljava/lang/String;"): "addPrefix",
        ("c", "Ljava/lang/String;"): "ipsetName",
    },
    "Z": {
        ("a", "Ljava/util/Timer;"): "timer",
    },
    "Z$a": {
        ("a", "J"): "lastGcCount",
        ("b", "J"): "lastGcTime",
    },
    "aa": {
        ("a", "I"): "minArgs",
        ("a", "Ljava/lang/String;"): "usage",
        ("b", "Ljava/lang/String;"): "permission",
    },
    "ac": {
        ("a", "Ljava/util/Map;"): "subCommands",
    },
    "ad": {("a", "Lme/minebuilders/proxyfork/antiproxy/AntiProxyWhitelistManager;"): "whitelist"},
    "ae": {("a", "Lme/minebuilders/proxyfork/antiproxy/AntiProxyWhitelistManager;"): "whitelist"},
    "af": {("a", "Lme/minebuilders/proxyfork/antiproxy/AntiProxyWhitelistManager;"): "whitelist"},
    "ab$a": {("a", "Lnet/md_5/bungee/event/EventBus;"): "delegate"},
    "x": {
        ("a", "I"): "value",
        ("b", "I"): "bytesRead",
        ("a", "Lx$a;"): "state",
    },
    "p": {
        ("a", "J"): "timeoutMillis",
        ("a", "Lio/netty/util/concurrent/Future;"): "timeoutFuture",
        ("b", "J"): "lastReadMillis",
        ("a", "B"): "state",
        ("a", "Z"): "reading",
        ("b", "Z"): "timedOut",
    },
    "p$a": {("a", "Lio/netty/channel/ChannelHandlerContext;"): "context"},
    "w": {("a", "I"): "invalidFrames"},
    "o": {
        ("a", "I"): "packetCount",
        ("b", "I"): "bytesThisSecond",
        ("c", "I"): "lastTick",
        ("d", "I"): "maxBytesPerSecond",
        ("e", "I"): "maxPacketsPerSecond",
        ("a", "Z"): "firstPacket",
        ("a", "Lnet/md_5/bungee/protocol/OverflowPacketException;"): "TOO_MUCH_DATA",
    },
    "ag": {("a", "I"): "threshold", ("a", "Lnet/md_5/bungee/jni/zlib/BungeeZlib;"): "zlib"},
    "ah": {("a", "I"): "threshold", ("a", "Lnet/md_5/bungee/jni/zlib/BungeeZlib;"): "zlib"},
    "ai": {("a", "I"): "threshold", ("a", "Lnet/md_5/bungee/jni/zlib/BungeeZlib;"): "zlib"},
    "an": {("a", "Lio/netty/util/collection/IntObjectMap;"): "packets"},
    "ap": {("a", "[I"): "VARINT_SIZES"},
    "ar": {
        ("a", "Lio/netty/resolver/dns/DnsNameResolverBuilder;"): "BUILDER",
        ("a", "Lio/netty/resolver/dns/DnsAddressResolverGroup;"): "RESOLVER_GROUP",
    },
    "aj": {("a", "Ljava/io/File;"): "file", ("a", "Ljava/util/Map;"): "map"},
    "u$1": {("a", "Ljava/util/concurrent/CompletableFuture;"): "future"},
    "t": {("a", "Ljava/util/Map;"): "CACHE"},
    "A": {("a", "[Ljava/lang/String;"): "header", ("a", "Ljava/util/Map;"): "replacements"},
    "f": {("a", "Ljava/util/Queue;"): "queue", ("a", "Ljava/util/concurrent/ExecutorService;"): "executor"},
    "j": {("a", "LL;"): "blocklist"},
    "s": {("a", "Lnet/md_5/bungee/BungeeServerInfo;"): "SPOOFED_LOBBY"},
    "me/minebuilders/proxyfork/antiproxy/a": {
        ("a", "Ljava/util/List;"): "providers",
        ("a", "Laj;"): "cache",
        ("a", "Lme/minebuilders/proxyfork/antiproxy/AntiProxyWhitelistManager;"): "whitelist",
        ("a", "I"): "purgeInterval",
        ("b", "I"): "saveInterval",
    },
    "me/minebuilders/proxyfork/antiproxy/c": {
        ("a", "Lme/minebuilders/proxyfork/antiproxy/b;"): "result",
        ("a", "Ljava/lang/String;"): "countryCode",
        ("b", "Ljava/lang/String;"): "asn",
        ("a", "J"): "timestamp",
    },
    "me/minebuilders/proxyfork/antiproxy/b": {
        ("a", "Lme/minebuilders/proxyfork/antiproxy/b;"): "WHITELISTED",
        ("b", "Lme/minebuilders/proxyfork/antiproxy/b;"): "UNKNOWN",
        ("c", "Lme/minebuilders/proxyfork/antiproxy/b;"): "DETECTED",
        ("d", "Lme/minebuilders/proxyfork/antiproxy/b;"): "ERROR",
        ("a", "[Lme/minebuilders/proxyfork/antiproxy/b;"): "VALUES",
    },
    "C": {
        ("d", "LC;"): "VERIFY",
        ("e", "LC;"): "SPEEDY_LOGIN",
        ("f", "LC;"): "SPEEDY_CONNECTION",
        ("j", "LC;"): "LOGIN_TIMEOUT",
        ("k", "LC;"): "PACKET_TIMEOUT",
        ("a", "[LC;"): "VALUES",
    },
    "x$a": {
        ("a", "Lx$a;"): "VALUE",
        ("b", "Lx$a;"): "INCOMPLETE",
        ("c", "Lx$a;"): "TOO_LONG",
        ("d", "Lx$a;"): "ZERO",
        ("a", "[Lx$a;"): "VALUES",
    },
    "Y": {
        ("a", "LW;"): "epollChecker",
        ("a", "LX;"): "nioChecker",
    },
    "ap": {
        ("a", "[I"): "VARINT_SIZES",
        ("a", "Z"): "packetDecodeLogging",
        ("a", "[Lap;"): "VALUES",
        ("a", "Lnet/md_5/bungee/protocol/MutedCorruptedFrameException;"): "BAD_VARINT",
    },
    "al": {
        ("a", "J"): "executeAt",
        ("a", "Ljava/lang/Runnable;"): "task",
    },
    "ak": {
        ("a", "Ljava/lang/String;"): "name",
        ("b", "Ljava/lang/String;"): "ip",
        ("a", "Lnet/md_5/bungee/api/Callback;"): "callback",
    },
    "m$a": {
        ("a", "Ljava/lang/String;"): "url",
        ("b", "Ljava/lang/String;"): "name",
    },
}

for _old, _new in list(CLASSES.items()):
    if "/" not in _old:
        CLASSES[_old] = "xcord/" + _new

FIELDS = {
    cls: {(n, (d if (not d.startswith("L") or d.endswith(";")) else d + ";")): v
          for (n, d), v in mapping.items()}
    for cls, mapping in FIELDS.items()
}

METHODS = {
    "y": {
        ("a", "()[Ljava/lang/String;"): "getBlockedIsoCodes",
        ("a", "()[Lnet/md_5/bungee/api/chat/BaseComponent;"): "getSuccessfulVerifyKickMessage",
        ("a", "()Lnet/md_5/bungee/protocol/DefinedPacket;"): "getVerifyingPacket",
        ("b", "()Lnet/md_5/bungee/protocol/DefinedPacket;"): "getVerifiedPacket",
        ("a", "()Ljava/util/Set;"): "getSpoofedUsers",
        ("b", "()Ljava/util/Set;"): "getBlockedAsns",
        ("c", "()Ljava/util/Set;"): "getBlockedCountries",
        ("a", "()Ljava/util/Collection;"): "getBlockedIsoCodesList",
        ("a", "(Ljava/lang/String;)Z"): "isIgnoredAddress",
        ("a", "(Ljava/net/InetAddress;)Z"): "isIgnoredAddress",
        ("b", "(Ljava/lang/String;)Z"): "addIgnoredAddress",
        ("a", "([Ljava/lang/String;)Lnet/md_5/bungee/protocol/DefinedPacket;"): "buildChatPacket",
        ("e", "()Ljava/lang/String;"): "getProxyCheckArguments",
        ("a", "()Ljava/util/List;"): "getBlockedIsoCodesRaw",
    },
    "g": {
        ("a", "()Lg;"): "getInstance",
        ("a", "()V"): "reload",
        ("a", "()Lm;"): "getGlobalBlacklist",
        ("a", "()I"): "getTick",
        ("a", "()Lr;"): "getBlacklistManager",
        ("a", "()LB;"): "getAntiBotManager",
        ("a", "()Ly;"): "getConfig",
        ("a", "(Ly;)V"): "setConfig",
        ("a", "(Lz;)V"): "setMessages",
        ("a", "()Z"): "isLogFilterEnabled",
        ("a", "()Lz;"): "getMessages",
        ("a", "()Lf;"): "getAsyncJobService",
        ("a", "()Lme/minebuilders/proxyfork/antiproxy/a;"): "getAntiProxyManager",
        ("a", "()Lk;"): "getDosFilterBlacklist",
        ("a", "(Z)V"): "setLogFilterEnabled",
        ("a", "(Ljava/lang/String;[Ljava/lang/Object;)V"): "log",
        ("a", "(Ljava/lang/String;)V"): "log",
    },
    "z": {
        ("a", "()Lh;"): "getProxyLogicVerifyV2",
        ("b", "()Lh;"): "getHighPing",
        ("c", "()Lh;"): "getVpn",
        ("d", "()Lh;"): "getSlowLogin",
        ("e", "()Lh;"): "getFailedConnection",
        ("f", "()Lh;"): "getLinuxCheckFail",
        ("g", "()Lh;"): "getSpeedJoinFail",
        ("h", "()Lh;"): "getShortName",
    },
    "h": {
        ("a", "()Ljava/lang/String;"): "getJson",
        ("a", "()[Lnet/md_5/bungee/api/chat/BaseComponent;"): "getComponents",
        ("b", "()Ljava/lang/String;"): "getLegacyText",
    },
    "B": {
        ("a", "()V"): "reload",
        ("a", "(I)V"): "tick",
        ("a", "(Ljava/lang/String;Ljava/lang/String;)V"): "whitelist",
        ("a", "()LF;"): "getSimilarNameCache",
        ("a", "(LC;Lnet/md_5/bungee/netty/ChannelWrapper;IILjava/lang/String;)Z"): "checkViolation",
        ("a", "(Ljava/lang/String;)Z"): "isWhitelisted",
        ("a", "(Ljava/lang/String;Ljava/lang/String;)Z"): "isWhitelisted",
        ("a", "(Ljava/lang/String;)LE;"): "getStats",
        ("a", "()I"): "incrementConnections",
        ("a", "()Z"): "isAttackActive",
        ("b", "()I"): "getConnectionCount",
        ("c", "()I"): "getLastConnectionCount",
    },
    "E": {
        ("a", "(LC;)I"): "getViolations",
        ("a", "(LC;I)I"): "addViolations",
        ("a", "()J"): "getLastAction",
        ("a", "(Z)V"): "setWhitelisted",
        ("a", "()I"): "getLastActionInt",
        ("a", "(I)V"): "setLastAction",
        ("b", "()J"): "getFirstSeen",
        ("c", "()J"): "getLastSeen",
        ("a", "(Ljava/net/InetAddress;)Z"): "checkSpeedyLogin",
        ("b", "(Ljava/net/InetAddress;)Z"): "checkSpeedyConnection",
        ("b", "(Z)V"): "setFlagged",
    },
    "F": {
        ("a", "(Ljava/lang/String;)LF$a;"): "findClosest",
        ("a", "(Ljava/lang/String;Ljava/lang/String;)D"): "similarity",
        ("a", "(Ljava/lang/String;Ljava/lang/String;)I"): "levenshtein",
    },
    "F$a": {
        ("a", "()D"): "getScore",
        ("a", "()Ljava/lang/String;"): "getValue",
    },
    "U": {
        ("a", "(Lio/netty/channel/Channel;Lio/netty/buffer/ByteBuf;)V"): "writePacket",
        ("a", "(Z)V"): "setAdvancedCheck",
        ("a", "(I)V"): "handlePing",
        ("b", "()V"): "finishVerification",
        ("c", "()V"): "runCallback",
        ("d", "()V"): "sendChallenge",
        ("b", "(I)V"): "failVerification",
        ("a", "()Ljava/lang/Runnable;"): "getCallback",
        ("a", "()V"): "completeVerification",
        ("a", "()Z"): "isVerified",
        ("a", "()I"): "getPing",
        ("a", "()Ljava/lang/String;"): "getDebugInfo",
    },
    "S": {
        ("a", "()V"): "completeVerification",
        ("a", "()Z"): "isVerified",
    },
    "T": {
        ("a", "()V"): "completeVerification",
    },
    "m": {
        ("a", "(I)V"): "tick",
        ("a", "(Ljava/lang/String;)Z"): "isBlacklisted",
        ("a", "()V"): "reload",
    },
    "k": {
        ("a", "(I)V"): "tick",
        ("a", "()Z"): "isActive",
        ("a", "()V"): "reload",
    },
    "r": {
        ("a", "()V"): "reload",
        ("a", "(I)V"): "tick",
        ("b", "()V"): "resetTick",
        ("a", "(Ljava/lang/String;)Z"): "isBlacklisted",
        ("a", "(Ljava/net/InetAddress;Ljava/lang/String;)Z"): "isBlacklisted",
        ("a", "(Ljava/net/SocketAddress;Ljava/lang/String;)Z"): "isBlacklisted",
        ("a", "(Ljava/net/InetAddress;)Z"): "isBlacklisted",
        ("a", "(Ljava/net/InetAddress;Lh;)V"): "disconnect",
        ("a", "(Ljava/net/InetAddress;)V"): "forceClose",
        ("c", "()V"): "clear",
    },
    "N": {
        ("a", "(Ljava/lang/String;)Z"): "add",
        ("a", "()I"): "purge",
        ("a", "()V"): "destroy",
        ("a", "(Ljava/io/File;)V"): "restore",
        ("a", "(Ljava/io/File;Ljava/lang/String;)V"): "load",
        ("b", "()V"): "setup",
        ("a", "(Ljava/lang/String;)Ljava/lang/String;"): "exec",
        ("a", "(Ljava/lang/String;I)Ljava/lang/String;"): "exec",
        ("a", "([Ljava/lang/String;I)Ljava/lang/String;"): "exec",
        ("a", "(Ljava/lang/ProcessBuilder;I)Ljava/lang/String;"): "exec",
    },
    "O": {
        ("a", "(Ljava/lang/String;)Z"): "add",
        ("b", "(Ljava/lang/String;)Z"): "contains",
        ("a", "()I"): "purge",
        ("a", "()Z"): "isEmpty",
    },
    "P": {
        ("a", "(Ljava/lang/String;)Z"): "add",
        ("b", "(Ljava/lang/String;)Z"): "contains",
        ("a", "(Ljava/io/File;Ljava/lang/String;)V"): "load",
        ("a", "()I"): "purge",
        ("a", "()Z"): "isEmpty",
    },
    "Q": {
        ("c", "()V"): "enable",
        ("d", "()V"): "disable",
        ("b", "()V"): "setup",
    },
    "L": {
        ("a", "([Ljava/lang/String;)V"): "blockCountries",
        ("b", "([Ljava/lang/String;)V"): "blockCountriesAsync",
    },
    "K": {
        ("a", "(Ljava/lang/String;)Z"): "add",
        ("b", "(Ljava/lang/String;)Z"): "contains",
        ("a", "()I"): "purge",
        ("a", "()Z"): "isEmpty",
    },
    "M": {
        ("a", "(Ljava/io/File;Ljava/lang/String;)V"): "load",
    },
    "I": {
        ("a", "(Ljava/lang/String;Ljava/lang/String;)Lme/minebuilders/proxyfork/antiproxy/c;"): "check",
    },
    "G": {("a", "(Ljava/lang/String;Ljava/lang/String;)Lme/minebuilders/proxyfork/antiproxy/c;"): "check"},
    "H": {("a", "(Ljava/lang/String;Ljava/lang/String;)Lme/minebuilders/proxyfork/antiproxy/c;"): "check"},
    "J": {("a", "(Ljava/lang/String;Ljava/lang/String;)Lme/minebuilders/proxyfork/antiproxy/c;"): "check"},
    "V": {
        ("a", "(Lio/netty/channel/Channel;)V"): "close",
        ("a", "(Lio/netty/channel/Channel;)Z"): "check",
        ("a", "(Lio/netty/channel/Channel;)I"): "getPing",
        ("b", "(Lio/netty/channel/Channel;)Z"): "isSuspiciousMss",
    },
    "W": {
        ("a", "(Lio/netty/channel/epoll/EpollSocketChannel;)V"): "close",
        ("a", "(Lio/netty/channel/epoll/EpollSocketChannel;)I"): "getPing",
        ("a", "(Lio/netty/channel/epoll/EpollSocketChannel;)Z"): "check",
        ("b", "(Lio/netty/channel/epoll/EpollSocketChannel;)Z"): "isSuspiciousMss",
        ("a", "(Lio/netty/channel/Channel;)V"): "close",
        ("a", "(Lio/netty/channel/Channel;)I"): "getPing",
        ("a", "(Lio/netty/channel/Channel;)Z"): "check",
        ("b", "(Lio/netty/channel/Channel;)Z"): "isSuspiciousMss",
    },
    "X": {
        ("a", "(Lio/netty/channel/socket/nio/NioSocketChannel;)V"): "close",
        ("a", "(Lio/netty/channel/socket/nio/NioSocketChannel;)Z"): "check",
        ("a", "(Lio/netty/channel/socket/nio/NioSocketChannel;)I"): "getPing",
        ("b", "(Lio/netty/channel/socket/nio/NioSocketChannel;)Z"): "isSuspiciousMss",
        ("a", "(Lio/netty/channel/Channel;)V"): "close",
        ("a", "(Lio/netty/channel/Channel;)I"): "getPing",
        ("a", "(Lio/netty/channel/Channel;)Z"): "check",
        ("b", "(Lio/netty/channel/Channel;)Z"): "isSuspiciousMss",
    },
    "Y": {
        ("a", "(Lio/netty/channel/Channel;)V"): "close",
        ("a", "(Lio/netty/channel/Channel;)Z"): "check",
        ("a", "(Lio/netty/channel/Channel;)I"): "getPing",
        ("b", "(Lio/netty/channel/Channel;)Z"): "isSuspiciousMss",
    },
    "am": {("a", "()Z"): "isReady"},
    "al": {("a", "()Z"): "isReady"},
    "aa": {
        ("a", "(Lnet/md_5/bungee/api/CommandSender;[Ljava/lang/String;)V"): "execute",
        ("a", "()I"): "getMinArgs",
        ("a", "()Ljava/lang/String;"): "getUsage",
        ("b", "()Ljava/lang/String;"): "getPermission",
    },
    "ac": {
        ("a", "(Lnet/md_5/bungee/api/CommandSender;)V"): "sendHelp",
        ("a", "(Ljava/lang/String;Laa;)V"): "registerSubCommand",
    },
    "ad": {("a", "(Lnet/md_5/bungee/api/CommandSender;[Ljava/lang/String;)V"): "execute"},
    "ae": {("a", "(Lnet/md_5/bungee/api/CommandSender;[Ljava/lang/String;)V"): "execute"},
    "af": {("a", "(Lnet/md_5/bungee/api/CommandSender;[Ljava/lang/String;)V"): "execute"},
    "ab$a": {("a", "(Ljava/lang/Object;Lnet/md_5/bungee/event/EventExceptionHandler;)V"): "postSync"},
    "x": {
        ("a", "()I"): "getValue",
        ("b", "()I"): "getBytesRead",
        ("a", "()Lx$a;"): "getState",
    },
    "o": {("a", "(Z)Lo;"): "setFirstPacketCheck"},
    "ag": {("a", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V"): "decode"},
    "ah": {("a", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lio/netty/buffer/ByteBuf;)V"): "encode"},
    "ai": {("a", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lio/netty/buffer/ByteBuf;)V"): "encode"},
    "u$1": {("a", "(Lio/netty/channel/socket/SocketChannel;)V"): "initChannel"},
    "u$1$1": {("a", "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/handler/codec/dns/DefaultDnsResponse;)V"): "channelRead0"},
    "q": {("a", "()V"): "reload"},
    "v": {("a", "(I)V"): "tick"},
    "an": {
        ("a", "(ILnet/md_5/bungee/protocol/DefinedPacket;)V"): "put",
        ("a", "(Ljava/lang/Class;Lnet/md_5/bungee/protocol/ProtocolConstants$Direction;II[B)V"): "register",
        ("a", "(I)Lnet/md_5/bungee/protocol/DefinedPacket;"): "get",
    },
    "i": {
        ("a", "(Lio/netty/buffer/ByteBuf;Ljava/lang/Class;Lnet/md_5/bungee/protocol/ProtocolConstants$Direction;II)Lio/netty/buffer/ByteBuf;"): "transform",
        ("a", "(Ljava/lang/Class;Lnet/md_5/bungee/protocol/ProtocolConstants$Direction;II[B)V"): "register",
    },
    "ap": {
        ("a", "(Lio/netty/buffer/ByteBuf;)I"): "readVarInt",
        ("b", "(Lio/netty/buffer/ByteBuf;)I"): "readVarIntRaw",
        ("a", "(I)I"): "varIntSize",
        ("a", "(Lio/netty/buffer/ByteBuf;I)V"): "writeVarInt",
        ("c", "(Lio/netty/buffer/ByteBuf;I)V"): "writeVarIntFull",
        ("b", "(Lio/netty/buffer/ByteBuf;I)V"): "writeVarInt3",
    },
    "aq": {
        ("a", "(C)Z"): "isAllowedChar",
        ("a", "(CZ)Z"): "isAllowedChar",
        ("a", "(Ljava/lang/String;Z)Z"): "isValidName",
    },
    "ar": {
        ("a", "()Lio/netty/resolver/dns/DnsAddressResolverGroup;"): "getResolverGroup",
        ("a", "(Ljava/lang/String;)Ljava/net/InetAddress;"): "resolve",
    },
    "as": {
        ("a", "(Ljava/lang/String;)I"): "ipToInt",
        ("a", "(Ljava/lang/String;)Z"): "isValidIp",
        ("a", "(Ljava/lang/ProcessBuilder;I)Ljava/lang/String;"): "execute",
        ("a", "(ILjava/lang/Process;Ljava/lang/ProcessBuilder;)V"): "scheduleKill",
    },
    "aj": {
        ("a", "(Ljava/io/File;)V"): "setFile",
        ("a", "()Ljava/util/Map;"): "getMap",
        ("a", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"): "put",
        ("a", "(Ljava/lang/Object;)Ljava/lang/Object;"): "get",
        ("a", "()V"): "save",
    },
    "al": {("a", "()Z"): "isReady"},
    "t": {("a", "(Ljava/lang/String;)Ljava/lang/String;"): "intern"},
    "A": {
        ("a", "(Ljava/lang/String;[Ljava/lang/String;)LA;"): "addReplacement",
        ("a", "(Ljava/io/File;)V"): "update",
    },
    "f": {("a", "(Lam;)V"): "submit"},
    "j": {("a", "()V"): "reload"},
    "l": {("a", "()V"): "reload"},
    "me/minebuilders/proxyfork/antiproxy/a": {
        ("a", "()V"): "reload",
        ("a", "(Ljava/lang/String;Ljava/lang/String;Z)Lme/minebuilders/proxyfork/antiproxy/b;"): "check",
        ("a", "(I)V"): "tick",
        ("a", "()Lme/minebuilders/proxyfork/antiproxy/AntiProxyWhitelistManager;"): "getWhitelist",
        ("b", "()V"): "purgeCache",
        ("c", "()V"): "saveCache",
        ("a", "(Ljava/util/Map$Entry;)Z"): "isExpired",
    },
    "me/minebuilders/proxyfork/antiproxy/c": {
        ("a", "()J"): "getTimestamp",
        ("a", "()Ljava/lang/String;"): "getCountryCode",
        ("b", "()Ljava/lang/String;"): "getAsn",
        ("a", "()Lme/minebuilders/proxyfork/antiproxy/b;"): "getResult",
        ("a", "(Lme/minebuilders/proxyfork/antiproxy/b;)V"): "setResult",
        ("a", "(Ljava/lang/String;)V"): "setCountryCode",
        ("b", "(Ljava/lang/String;)V"): "setAsn",
    },
}


def parse_members():
    text = open(MEMBERS, encoding="utf-8", errors="replace").read().splitlines()
    cur = None
    fields = defaultdict(list)   # cls -> [(name, desc)]
    methods = defaultdict(list)  # cls -> [(name, desc)]
    for i, line in enumerate(text):
        if line.startswith("### "):
            cur = line[4:].replace(".class", "")
            continue
        if not line.startswith("  ") or i + 1 >= len(text) or "descriptor:" not in text[i + 1]:
            continue
        desc = text[i + 1].split("descriptor:")[1].strip()
        sig = line.strip()
        if sig.startswith("static {}"):
            continue
        m = re.match(r"(?:public|private|protected|static|final|abstract|native|synchronized|\s)+[\w$<>\[\].]+\s+([\w$]+)\(", sig)
        if m:
            methods[cur].append((m.group(1), desc))
        else:
            m = re.match(r"(?:public|private|protected|static|final|volatile|transient|\s)+[\w$<>\[\].]+\s+([\w$]+);", sig)
            if m:
                fields[cur].append((m.group(1), desc))
    return fields, methods

def type_tag(desc):
    tags = {
        "I": "int", "Z": "bool", "J": "long", "D": "double", "B": "byte",
        "S": "short", "C": "char", "F": "float",
        "Ljava/lang/String;": "String",
        "Ljava/util/Set;": "Set", "Ljava/util/List;": "List",
        "Ljava/util/Collection;": "Collection",
        "Ljava/util/Map;": "Map",
        "Ljava/util/concurrent/ConcurrentHashMap;": "ConcurrentHashMap",
        "Ljava/util/concurrent/atomic/AtomicInteger;": "AtomicInteger",
        "Ljava/util/concurrent/atomic/AtomicBoolean;": "AtomicBoolean",
        "Lnet/md_5/bungee/protocol/DefinedPacket;": "DefinedPacket",
        "[Lnet/md_5/bungee/api/chat/BaseComponent;": "BaseComponentArray",
        "[Ljava/lang/String;": "StringArray",
        "[I": "IntArray",
    }
    if desc in tags:
        return tags[desc]
    if desc.startswith("L") and desc.endswith(";"):
        return desc[1:-1].split("/")[-1]
    if desc.startswith("["):
        return "Array"
    return "X"

def unique(name, used):
    if name not in used:
        used.add(name)
        return name
    i = 2
    while f"{name}{i}" in used:
        i += 1
    used.add(f"{name}{i}")
    return f"{name}{i}"

def main():
    fields, methods = parse_members()
    out = []
    for old, new in CLASSES.items():
        out.append(f"C {old} {new}")

    for line in open(f"{ROOT}/map-config.txt", encoding="utf-8"):
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        out.append(line)
        parts = line.split()
        if parts[0] == "F" and len(parts) >= 5:
            FIELDS.setdefault(parts[1], {})[(parts[2], parts[3])] = parts[4]
        elif parts[0] == "M" and len(parts) >= 5:
            METHODS.setdefault(parts[1], {})[(parts[2], parts[3])] = parts[4]

    for cls, mapping in FIELDS.items():
        for (name, desc), new in mapping.items():
            out.append(f"F {cls} {name} {desc} {new}")

    for cls, mapping in METHODS.items():
        for (name, desc), new in mapping.items():
            out.append(f"M {cls} {name} {desc} {new}")

    for cls, flist in fields.items():
        used = {new for (n, d), new in FIELDS.get(cls, {}).items()}
        name_count = defaultdict(int)
        for n, d in flist:
            name_count[n] += 1
        for n, d in flist:
            if (n, d) in FIELDS.get(cls, {}):
                continue
            if name_count[n] > 1:
                out.append(f"F {cls} {n} {d} {unique(n + '_' + type_tag(d), used)}")

    for cls, mlist in methods.items():
        mapped = METHODS.get(cls, {})
        used = {new for new in mapped.values()}
        groups = defaultdict(list)
        for n, d in mlist:
            key = (n, d[:d.index(')') + 1])
            groups[key].append((n, d))
        for (n, params), entries in groups.items():
            if len(entries) < 2:
                continue
            for n2, d in entries:
                if (n2, d) in mapped:
                    continue
                ret = d[d.index(')') + 1:]
                out.append(f"M {cls} {n2} {d} {unique(n2 + '_' + type_tag(ret), used)}")

    with open(f"{ROOT}/mapping.txt", "w", encoding="utf-8") as fh:
        fh.write("\n".join(sorted(set(out))) + "\n")
    print(f"mapping lines: {len(set(out))}")

if __name__ == "__main__":
    main()
