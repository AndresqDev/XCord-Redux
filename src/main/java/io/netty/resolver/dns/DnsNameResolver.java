package io.netty.resolver.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.resolver.DefaultHostsFileEntriesResolver;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.InetNameResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.util.NetUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DnsNameResolver extends InetNameResolver {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsNameResolver.class);
   private static final String LOCALHOST = "localhost";
   private static final String WINDOWS_HOST_NAME;
   private static final InetAddress LOCALHOST_ADDRESS;
   private static final DnsRecord[] EMPTY_ADDITIONALS = new DnsRecord[0];
   private static final DnsRecordType[] IPV4_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.A};
   private static final InternetProtocolFamily[] IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv4};
   private static final DnsRecordType[] IPV4_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.A, DnsRecordType.AAAA};
   private static final InternetProtocolFamily[] IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{
      InternetProtocolFamily.IPv4, InternetProtocolFamily.IPv6
   };
   private static final DnsRecordType[] IPV6_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.AAAA};
   private static final InternetProtocolFamily[] IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv6};
   private static final DnsRecordType[] IPV6_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.AAAA, DnsRecordType.A};
   private static final InternetProtocolFamily[] IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{
      InternetProtocolFamily.IPv6, InternetProtocolFamily.IPv4
   };
   static final ResolvedAddressTypes DEFAULT_RESOLVE_ADDRESS_TYPES;
   static final String[] DEFAULT_SEARCH_DOMAINS;
   private static final UnixResolverOptions DEFAULT_OPTIONS;
   private static final DatagramDnsResponseDecoder DATAGRAM_DECODER;
   private static final DatagramDnsQueryEncoder DATAGRAM_ENCODER;
   private static final TcpDnsQueryEncoder TCP_ENCODER;
   final Promise<Channel> channelReadyPromise;
   final Channel ch;
   private final Comparator<InetSocketAddress> nameServerComparator;
   final DnsQueryContextManager queryContextManager = new DnsQueryContextManager();
   private final DnsCache resolveCache;
   private final AuthoritativeDnsServerCache authoritativeDnsServerCache;
   private final DnsCnameCache cnameCache;
   private final FastThreadLocal<DnsServerAddressStream> nameServerAddrStream = new FastThreadLocal<DnsServerAddressStream>() {
      protected DnsServerAddressStream initialValue() {
         return DnsNameResolver.this.dnsServerAddressStreamProvider.nameServerAddressStream("");
      }
   };
   private final long queryTimeoutMillis;
   private final int maxQueriesPerResolve;
   private final ResolvedAddressTypes resolvedAddressTypes;
   private final InternetProtocolFamily[] resolvedInternetProtocolFamilies;
   private final boolean recursionDesired;
   private final int maxPayloadSize;
   private final boolean optResourceEnabled;
   private final HostsFileEntriesResolver hostsFileEntriesResolver;
   private final DnsServerAddressStreamProvider dnsServerAddressStreamProvider;
   private final String[] searchDomains;
   private final int ndots;
   private final boolean supportsAAAARecords;
   private final boolean supportsARecords;
   private final InternetProtocolFamily preferredAddressType;
   private final DnsRecordType[] resolveRecordTypes;
   private final boolean decodeIdn;
   private final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory;
   private final boolean completeOncePreferredResolved;
   private final ChannelFactory<? extends SocketChannel> socketChannelFactory;

   private static boolean anyInterfaceSupportsIpV6() {
      try {
         Enumeration var0 = NetworkInterface.getNetworkInterfaces();

         while (var0.hasMoreElements()) {
            NetworkInterface var1 = (NetworkInterface)var0.nextElement();
            Enumeration var2 = var1.getInetAddresses();

            while (var2.hasMoreElements()) {
               InetAddress var3 = (InetAddress)var2.nextElement();
               if (var3 instanceof Inet6Address && !var3.isAnyLocalAddress() && !var3.isLoopbackAddress() && !var3.isLinkLocalAddress()) {
                  return true;
               }
            }
         }
      } catch (SocketException var4) {
         logger.debug("Unable to detect if any interface supports IPv6, assuming IPv4-only", (Throwable)var4);
      }

      return false;
   }

   private static List<String> getSearchDomainsHack() throws Exception {
      if (PlatformDependent.javaVersion() < 9) {
         Class var0 = Class.forName("sun.net.dns.ResolverConfiguration");
         Method var1 = var0.getMethod("open");
         Method var2 = var0.getMethod("searchlist");
         Object var3 = var1.invoke(null);
         return (List<String>)var2.invoke(var3);
      } else {
         return Collections.emptyList();
      }
   }

   @Deprecated
   public DnsNameResolver(
      EventLoop var1,
      ChannelFactory<? extends DatagramChannel> var2,
      DnsCache var3,
      DnsCache var4,
      DnsQueryLifecycleObserverFactory var5,
      long var6,
      ResolvedAddressTypes var8,
      boolean var9,
      int var10,
      boolean var11,
      int var12,
      boolean var13,
      HostsFileEntriesResolver var14,
      DnsServerAddressStreamProvider var15,
      String[] var16,
      int var17,
      boolean var18
   ) {
      this(
         var1, var2, var3, new AuthoritativeDnsServerCacheAdapter(var4), var5, var6, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18
      );
   }

   @Deprecated
   public DnsNameResolver(
      EventLoop var1,
      ChannelFactory<? extends DatagramChannel> var2,
      DnsCache var3,
      AuthoritativeDnsServerCache var4,
      DnsQueryLifecycleObserverFactory var5,
      long var6,
      ResolvedAddressTypes var8,
      boolean var9,
      int var10,
      boolean var11,
      int var12,
      boolean var13,
      HostsFileEntriesResolver var14,
      DnsServerAddressStreamProvider var15,
      String[] var16,
      int var17,
      boolean var18
   ) {
      this(
         var1, var2, null, var3, NoopDnsCnameCache.INSTANCE, var4, var5, var6, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, false
      );
   }

   DnsNameResolver(
      EventLoop var1,
      ChannelFactory<? extends DatagramChannel> var2,
      ChannelFactory<? extends SocketChannel> var3,
      DnsCache var4,
      DnsCnameCache var5,
      AuthoritativeDnsServerCache var6,
      DnsQueryLifecycleObserverFactory var7,
      long var8,
      ResolvedAddressTypes var10,
      boolean var11,
      int var12,
      boolean var13,
      int var14,
      boolean var15,
      HostsFileEntriesResolver var16,
      DnsServerAddressStreamProvider var17,
      String[] var18,
      int var19,
      boolean var20,
      boolean var21
   ) {
      this(var1, var2, var3, var4, var5, var6, null, var7, var8, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21);
   }

   DnsNameResolver(
      EventLoop var1,
      ChannelFactory<? extends DatagramChannel> var2,
      ChannelFactory<? extends SocketChannel> var3,
      final DnsCache var4,
      final DnsCnameCache var5,
      final AuthoritativeDnsServerCache var6,
      SocketAddress var7,
      DnsQueryLifecycleObserverFactory var8,
      long var9,
      ResolvedAddressTypes var11,
      boolean var12,
      int var13,
      boolean var14,
      int var15,
      boolean var16,
      HostsFileEntriesResolver var17,
      DnsServerAddressStreamProvider var18,
      String[] var19,
      int var20,
      boolean var21,
      boolean var22
   ) {
      super(var1);
      this.queryTimeoutMillis = var9 > 0L ? var9 : TimeUnit.SECONDS.toMillis((long)DEFAULT_OPTIONS.timeout());
      this.resolvedAddressTypes = var11 != null ? var11 : DEFAULT_RESOLVE_ADDRESS_TYPES;
      this.recursionDesired = var12;
      this.maxQueriesPerResolve = var13 > 0 ? var13 : DEFAULT_OPTIONS.attempts();
      this.maxPayloadSize = ObjectUtil.checkPositive(var15, "maxPayloadSize");
      this.optResourceEnabled = var16;
      this.hostsFileEntriesResolver = ObjectUtil.checkNotNull(var17, "hostsFileEntriesResolver");
      this.dnsServerAddressStreamProvider = ObjectUtil.checkNotNull(var18, "dnsServerAddressStreamProvider");
      this.resolveCache = ObjectUtil.checkNotNull(var4, "resolveCache");
      this.cnameCache = ObjectUtil.checkNotNull(var5, "cnameCache");
      this.dnsQueryLifecycleObserverFactory = (DnsQueryLifecycleObserverFactory)(var14
         ? (
            var8 instanceof NoopDnsQueryLifecycleObserverFactory
               ? new LoggingDnsQueryLifeCycleObserverFactory()
               : new BiDnsQueryLifecycleObserverFactory(new LoggingDnsQueryLifeCycleObserverFactory(), var8)
         )
         : ObjectUtil.checkNotNull(var8, "dnsQueryLifecycleObserverFactory"));
      this.searchDomains = var19 != null ? (String[])var19.clone() : DEFAULT_SEARCH_DOMAINS;
      this.ndots = var20 >= 0 ? var20 : DEFAULT_OPTIONS.ndots();
      this.decodeIdn = var21;
      this.completeOncePreferredResolved = var22;
      this.socketChannelFactory = var3;
      switch (this.resolvedAddressTypes) {
         case IPV4_ONLY:
            this.supportsAAAARecords = false;
            this.supportsARecords = true;
            this.resolveRecordTypes = IPV4_ONLY_RESOLVED_RECORD_TYPES;
            this.resolvedInternetProtocolFamilies = IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
            break;
         case IPV4_PREFERRED:
            this.supportsAAAARecords = true;
            this.supportsARecords = true;
            this.resolveRecordTypes = IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
            this.resolvedInternetProtocolFamilies = IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
            break;
         case IPV6_ONLY:
            this.supportsAAAARecords = true;
            this.supportsARecords = false;
            this.resolveRecordTypes = IPV6_ONLY_RESOLVED_RECORD_TYPES;
            this.resolvedInternetProtocolFamilies = IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
            break;
         case IPV6_PREFERRED:
            this.supportsAAAARecords = true;
            this.supportsARecords = true;
            this.resolveRecordTypes = IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
            this.resolvedInternetProtocolFamilies = IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
            break;
         default:
            throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + var11);
      }

      this.preferredAddressType = preferredAddressType(this.resolvedAddressTypes);
      this.authoritativeDnsServerCache = ObjectUtil.checkNotNull(var6, "authoritativeDnsServerCache");
      this.nameServerComparator = new NameServerComparator(this.preferredAddressType.addressType());
      Bootstrap var23 = new Bootstrap();
      var23.group(this.executor());
      var23.channelFactory(var2);
      this.channelReadyPromise = this.executor().newPromise();
      final DnsNameResolver.DnsResponseHandler var24 = new DnsNameResolver.DnsResponseHandler(this.channelReadyPromise);
      var23.handler(new ChannelInitializer<DatagramChannel>() {
         protected void initChannel(DatagramChannel var1) {
            var1.pipeline().addLast(DnsNameResolver.DATAGRAM_ENCODER, DnsNameResolver.DATAGRAM_DECODER, var24);
         }
      });
      ChannelFuture var25;
      if (var7 == null) {
         var23.option(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, Boolean.valueOf(true));
         var25 = var23.register();
      } else {
         var25 = var23.bind(var7);
      }

      if (var25.isDone()) {
         Throwable var26 = var25.cause();
         if (var26 != null) {
            if (var26 instanceof RuntimeException) {
               throw (RuntimeException)var26;
            }

            if (var26 instanceof Error) {
               throw (Error)var26;
            }

            throw new IllegalStateException("Unable to create / register Channel", var26);
         }
      } else {
         var25.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) {
               Throwable var2 = var1.cause();
               if (var2 != null) {
                  DnsNameResolver.this.channelReadyPromise.tryFailure(var2);
               }
            }
         });
      }

      this.ch = var25.channel();
      this.ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(var15));
      this.ch.closeFuture().addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) {
            var4.clear();
            var5.clear();
            var6.clear();
         }
      });
   }

   static InternetProtocolFamily preferredAddressType(ResolvedAddressTypes var0) {
      switch (var0) {
         case IPV4_ONLY:
         case IPV4_PREFERRED:
            return InternetProtocolFamily.IPv4;
         case IPV6_ONLY:
         case IPV6_PREFERRED:
            return InternetProtocolFamily.IPv6;
         default:
            throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + var0);
      }
   }

   InetSocketAddress newRedirectServerAddress(InetAddress var1) {
      return new InetSocketAddress(var1, 53);
   }

   final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory() {
      return this.dnsQueryLifecycleObserverFactory;
   }

   protected DnsServerAddressStream newRedirectDnsServerStream(String var1, List<InetSocketAddress> var2) {
      DnsServerAddressStream var3 = this.authoritativeDnsServerCache().get(var1);
      if (var3 != null && var3.size() != 0) {
         return var3;
      } else {
         Collections.sort(var2, this.nameServerComparator);
         return new SequentialDnsServerAddressStream(var2, 0);
      }
   }

   public DnsCache resolveCache() {
      return this.resolveCache;
   }

   public DnsCnameCache cnameCache() {
      return this.cnameCache;
   }

   public AuthoritativeDnsServerCache authoritativeDnsServerCache() {
      return this.authoritativeDnsServerCache;
   }

   public long queryTimeoutMillis() {
      return this.queryTimeoutMillis;
   }

   public ResolvedAddressTypes resolvedAddressTypes() {
      return this.resolvedAddressTypes;
   }

   InternetProtocolFamily[] resolvedInternetProtocolFamiliesUnsafe() {
      return this.resolvedInternetProtocolFamilies;
   }

   final String[] searchDomains() {
      return this.searchDomains;
   }

   final int ndots() {
      return this.ndots;
   }

   final boolean supportsAAAARecords() {
      return this.supportsAAAARecords;
   }

   final boolean supportsARecords() {
      return this.supportsARecords;
   }

   final InternetProtocolFamily preferredAddressType() {
      return this.preferredAddressType;
   }

   final DnsRecordType[] resolveRecordTypes() {
      return this.resolveRecordTypes;
   }

   final boolean isDecodeIdn() {
      return this.decodeIdn;
   }

   public boolean isRecursionDesired() {
      return this.recursionDesired;
   }

   public int maxQueriesPerResolve() {
      return this.maxQueriesPerResolve;
   }

   public int maxPayloadSize() {
      return this.maxPayloadSize;
   }

   public boolean isOptResourceEnabled() {
      return this.optResourceEnabled;
   }

   public HostsFileEntriesResolver hostsFileEntriesResolver() {
      return this.hostsFileEntriesResolver;
   }

   @Override
   public void close() {
      if (this.ch.isOpen()) {
         this.ch.close();
      }
   }

   protected EventLoop executor() {
      return (EventLoop)super.executor();
   }

   private InetAddress resolveHostsFileEntry(String var1) {
      if (this.hostsFileEntriesResolver == null) {
         return null;
      } else {
         InetAddress var2 = this.hostsFileEntriesResolver.address(var1, this.resolvedAddressTypes);
         return var2 == null && isLocalWindowsHost(var1) ? LOCALHOST_ADDRESS : var2;
      }
   }

   private List<InetAddress> resolveHostsFileEntries(String var1) {
      if (this.hostsFileEntriesResolver == null) {
         return null;
      } else {
         List var2;
         if (this.hostsFileEntriesResolver instanceof DefaultHostsFileEntriesResolver) {
            var2 = ((DefaultHostsFileEntriesResolver)this.hostsFileEntriesResolver).addresses(var1, this.resolvedAddressTypes);
         } else {
            InetAddress var3 = this.hostsFileEntriesResolver.address(var1, this.resolvedAddressTypes);
            var2 = var3 != null ? Collections.singletonList(var3) : null;
         }

         return var2 == null && isLocalWindowsHost(var1) ? Collections.singletonList(LOCALHOST_ADDRESS) : var2;
      }
   }

   private static boolean isLocalWindowsHost(String var0) {
      return PlatformDependent.isWindows() && ("localhost".equalsIgnoreCase(var0) || WINDOWS_HOST_NAME != null && WINDOWS_HOST_NAME.equalsIgnoreCase(var0));
   }

   public final Future<InetAddress> resolve(String var1, Iterable<DnsRecord> var2) {
      return this.resolve(var1, var2, this.executor().newPromise());
   }

   public final Future<InetAddress> resolve(String var1, Iterable<DnsRecord> var2, Promise<InetAddress> var3) {
      ObjectUtil.checkNotNull(var3, "promise");
      DnsRecord[] var4 = toArray(var2, true);

      try {
         this.doResolve(var1, var4, var3, this.resolveCache);
         return var3;
      } catch (Exception var6) {
         return var3.setFailure(var6);
      }
   }

   public final Future<List<InetAddress>> resolveAll(String var1, Iterable<DnsRecord> var2) {
      return this.resolveAll(var1, var2, this.executor().newPromise());
   }

   public final Future<List<InetAddress>> resolveAll(String var1, Iterable<DnsRecord> var2, Promise<List<InetAddress>> var3) {
      ObjectUtil.checkNotNull(var3, "promise");
      DnsRecord[] var4 = toArray(var2, true);

      try {
         this.doResolveAll(var1, var4, var3, this.resolveCache);
         return var3;
      } catch (Exception var6) {
         return var3.setFailure(var6);
      }
   }

   @Override
   protected void doResolve(String var1, Promise<InetAddress> var2) throws Exception {
      this.doResolve(var1, EMPTY_ADDITIONALS, var2, this.resolveCache);
   }

   public final Future<List<DnsRecord>> resolveAll(DnsQuestion var1) {
      return this.resolveAll(var1, EMPTY_ADDITIONALS, this.executor().newPromise());
   }

   public final Future<List<DnsRecord>> resolveAll(DnsQuestion var1, Iterable<DnsRecord> var2) {
      return this.resolveAll(var1, var2, this.executor().newPromise());
   }

   public final Future<List<DnsRecord>> resolveAll(DnsQuestion var1, Iterable<DnsRecord> var2, Promise<List<DnsRecord>> var3) {
      DnsRecord[] var4 = toArray(var2, true);
      return this.resolveAll(var1, var4, var3);
   }

   private Future<List<DnsRecord>> resolveAll(DnsQuestion var1, DnsRecord[] var2, Promise<List<DnsRecord>> var3) {
      ObjectUtil.checkNotNull(var1, "question");
      ObjectUtil.checkNotNull(var3, "promise");
      DnsRecordType var4 = var1.type();
      String var5 = var1.name();
      if (var4 == DnsRecordType.A || var4 == DnsRecordType.AAAA) {
         List var6 = this.resolveHostsFileEntries(var5);
         if (var6 != null) {
            ArrayList var7 = new ArrayList();

            for (InetAddress var9 : var6) {
               ByteBuf var10 = null;
               if (var9 instanceof Inet4Address) {
                  if (var4 == DnsRecordType.A) {
                     var10 = Unpooled.wrappedBuffer(var9.getAddress());
                  }
               } else if (var9 instanceof Inet6Address && var4 == DnsRecordType.AAAA) {
                  var10 = Unpooled.wrappedBuffer(var9.getAddress());
               }

               if (var10 != null) {
                  var7.add(new DefaultDnsRawRecord(var5, var4, 86400L, var10));
               }
            }

            if (!var7.isEmpty()) {
               trySuccess(var3, var7);
               return var3;
            }
         }
      }

      DnsServerAddressStream var11 = this.dnsServerAddressStreamProvider.nameServerAddressStream(var5);
      new DnsRecordResolveContext(this, var3, var1, var2, var11, this.maxQueriesPerResolve).resolve(var3);
      return var3;
   }

   private static DnsRecord[] toArray(Iterable<DnsRecord> var0, boolean var1) {
      ObjectUtil.checkNotNull(var0, "additionals");
      if (var0 instanceof Collection) {
         Collection var5 = (Collection)var0;

         for (DnsRecord var7 : var0) {
            validateAdditional(var7, var1);
         }

         return var5.toArray(new DnsRecord[var5.size()]);
      } else {
         Iterator var2 = var0.iterator();
         if (!var2.hasNext()) {
            return EMPTY_ADDITIONALS;
         } else {
            ArrayList var3 = new ArrayList();

            do {
               DnsRecord var4 = (DnsRecord)var2.next();
               validateAdditional(var4, var1);
               var3.add(var4);
            } while (var2.hasNext());

            return var3.toArray(new DnsRecord[var3.size()]);
         }
      }
   }

   private static void validateAdditional(DnsRecord var0, boolean var1) {
      ObjectUtil.checkNotNull(var0, "record");
      if (var1 && var0 instanceof DnsRawRecord) {
         throw new IllegalArgumentException("DnsRawRecord implementations not allowed: " + var0);
      }
   }

   private InetAddress loopbackAddress() {
      return this.preferredAddressType().localhost();
   }

   protected void doResolve(String var1, DnsRecord[] var2, Promise<InetAddress> var3, DnsCache var4) throws Exception {
      if (var1 != null && !var1.isEmpty()) {
         InetAddress var5 = NetUtil.createInetAddressFromIpAddressString(var1);
         if (var5 != null) {
            var3.setSuccess(var5);
         } else {
            String var6 = hostname(var1);
            InetAddress var7 = this.resolveHostsFileEntry(var6);
            if (var7 != null) {
               var3.setSuccess(var7);
            } else {
               if (!this.doResolveCached(var6, var2, var3, var4)) {
                  this.doResolveUncached(var6, var2, var3, var4, true);
               }
            }
         }
      } else {
         var3.setSuccess(this.loopbackAddress());
      }
   }

   private boolean doResolveCached(String var1, DnsRecord[] var2, Promise<InetAddress> var3, DnsCache var4) {
      List var5 = var4.get(var1, var2);
      if (var5 != null && !var5.isEmpty()) {
         Throwable var6 = ((DnsCacheEntry)var5.get(0)).cause();
         if (var6 != null) {
            tryFailure(var3, var6);
            return true;
         } else {
            int var7 = var5.size();

            for (InternetProtocolFamily var11 : this.resolvedInternetProtocolFamilies) {
               for (int var12 = 0; var12 < var7; var12++) {
                  DnsCacheEntry var13 = (DnsCacheEntry)var5.get(var12);
                  if (var11.addressType().isInstance(var13.address())) {
                     trySuccess(var3, var13.address());
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   static <T> boolean trySuccess(Promise<T> var0, T var1) {
      boolean var2 = var0.trySuccess(var1);
      if (!var2) {
         logger.trace("Failed to notify success ({}) to a promise: {}", var1, var0);
      }

      return var2;
   }

   private static void tryFailure(Promise<?> var0, Throwable var1) {
      if (!var0.tryFailure(var1)) {
         logger.trace("Failed to notify failure to a promise: {}", var0, var1);
      }
   }

   private void doResolveUncached(String var1, DnsRecord[] var2, final Promise<InetAddress> var3, DnsCache var4, boolean var5) {
      Promise var6 = this.executor().newPromise();
      this.doResolveAllUncached(var1, var2, var3, var6, var4, true);
      var6.addListener(new FutureListener<List<InetAddress>>() {
         @Override
         public void operationComplete(Future<List<InetAddress>> var1) {
            if (var1.isSuccess()) {
               DnsNameResolver.trySuccess(var3, ((List)var1.getNow()).get(0));
            } else {
               DnsNameResolver.tryFailure(var3, var1.cause());
            }
         }
      });
   }

   @Override
   protected void doResolveAll(String var1, Promise<List<InetAddress>> var2) throws Exception {
      this.doResolveAll(var1, EMPTY_ADDITIONALS, var2, this.resolveCache);
   }

   protected void doResolveAll(String var1, DnsRecord[] var2, Promise<List<InetAddress>> var3, DnsCache var4) throws Exception {
      if (var1 != null && !var1.isEmpty()) {
         InetAddress var5 = NetUtil.createInetAddressFromIpAddressString(var1);
         if (var5 != null) {
            var3.setSuccess(Collections.singletonList(var5));
         } else {
            String var6 = hostname(var1);
            List var7 = this.resolveHostsFileEntries(var6);
            if (var7 != null) {
               var3.setSuccess(var7);
            } else {
               if (!doResolveAllCached(var6, var2, var3, var4, this.resolvedInternetProtocolFamilies)) {
                  this.doResolveAllUncached(var6, var2, var3, var3, var4, this.completeOncePreferredResolved);
               }
            }
         }
      } else {
         var3.setSuccess(Collections.singletonList(this.loopbackAddress()));
      }
   }

   static boolean doResolveAllCached(String var0, DnsRecord[] var1, Promise<List<InetAddress>> var2, DnsCache var3, InternetProtocolFamily[] var4) {
      List var5 = var3.get(var0, var1);
      if (var5 != null && !var5.isEmpty()) {
         Throwable var6 = ((DnsCacheEntry)var5.get(0)).cause();
         if (var6 != null) {
            tryFailure(var2, var6);
            return true;
         } else {
            ArrayList var7 = null;
            int var8 = var5.size();

            for (InternetProtocolFamily var12 : var4) {
               for (int var13 = 0; var13 < var8; var13++) {
                  DnsCacheEntry var14 = (DnsCacheEntry)var5.get(var13);
                  if (var12.addressType().isInstance(var14.address())) {
                     if (var7 == null) {
                        var7 = new ArrayList(var8);
                     }

                     var7.add(var14.address());
                  }
               }
            }

            if (var7 != null) {
               trySuccess(var2, var7);
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void doResolveAllUncached(
      final String var1, final DnsRecord[] var2, final Promise<?> var3, final Promise<List<InetAddress>> var4, final DnsCache var5, final boolean var6
   ) {
      EventLoop var7 = this.executor();
      if (var7.inEventLoop()) {
         this.doResolveAllUncached0(var1, var2, var3, var4, var5, var6);
      } else {
         var7.execute(new Runnable() {
            @Override
            public void run() {
               DnsNameResolver.this.doResolveAllUncached0(var1, var2, var3, var4, var5, var6);
            }
         });
      }
   }

   private void doResolveAllUncached0(String var1, DnsRecord[] var2, Promise<?> var3, Promise<List<InetAddress>> var4, DnsCache var5, boolean var6) {
      assert this.executor().inEventLoop();

      DnsServerAddressStream var7 = this.dnsServerAddressStreamProvider.nameServerAddressStream(var1);
      new DnsAddressResolveContext(this, var3, var1, var2, var7, this.maxQueriesPerResolve, var5, this.authoritativeDnsServerCache, var6).resolve(var4);
   }

   private static String hostname(String var0) {
      String var1 = IDN.toASCII(var0);
      if (StringUtil.endsWith(var0, '.') && !StringUtil.endsWith(var1, '.')) {
         var1 = var1 + ".";
      }

      return var1;
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion var1) {
      return this.query(this.nextNameServerAddress(), var1);
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion var1, Iterable<DnsRecord> var2) {
      return this.query(this.nextNameServerAddress(), var1, var2);
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(
      DnsQuestion var1, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var2
   ) {
      return this.query(this.nextNameServerAddress(), var1, Collections.emptyList(), var2);
   }

   private InetSocketAddress nextNameServerAddress() {
      return this.nameServerAddrStream.get().next();
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress var1, DnsQuestion var2) {
      return this.query0(var1, var2, EMPTY_ADDITIONALS, true, this.ch.newPromise(), this.ch.eventLoop().newPromise());
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress var1, DnsQuestion var2, Iterable<DnsRecord> var3) {
      return this.query0(var1, var2, toArray(var3, false), true, this.ch.newPromise(), this.ch.eventLoop().newPromise());
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(
      InetSocketAddress var1, DnsQuestion var2, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var3
   ) {
      return this.query0(var1, var2, EMPTY_ADDITIONALS, true, this.ch.newPromise(), var3);
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(
      InetSocketAddress var1, DnsQuestion var2, Iterable<DnsRecord> var3, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var4
   ) {
      return this.query0(var1, var2, toArray(var3, false), true, this.ch.newPromise(), var4);
   }

   public static boolean isTransportOrTimeoutError(Throwable var0) {
      return var0 != null && var0.getCause() instanceof DnsNameResolverException;
   }

   public static boolean isTimeoutError(Throwable var0) {
      return var0 != null && var0.getCause() instanceof DnsNameResolverTimeoutException;
   }

   final void flushQueries() {
      this.ch.flush();
   }

   final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(
      InetSocketAddress var1,
      DnsQuestion var2,
      DnsRecord[] var3,
      boolean var4,
      ChannelPromise var5,
      Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var6
   ) {
      assert !var5.isVoid();

      Promise var7 = cast(ObjectUtil.checkNotNull(var6, "promise"));

      try {
         new DatagramDnsQueryContext(this, var1, var2, var3, var7).query(var4, var5);
         return var7;
      } catch (Exception var9) {
         return var7.setFailure(var9);
      }
   }

   private static Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> cast(Promise<?> var0) {
      return var0;
   }

   final DnsServerAddressStream newNameServerAddressStream(String var1) {
      return this.dnsServerAddressStreamProvider.nameServerAddressStream(var1);
   }

   static {
      if (NetUtil.isIpV4StackPreferred() || !anyInterfaceSupportsIpV6()) {
         DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_ONLY;
         LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
      } else if (NetUtil.isIpV6AddressesPreferred()) {
         DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV6_PREFERRED;
         LOCALHOST_ADDRESS = NetUtil.LOCALHOST6;
      } else {
         DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_PREFERRED;
         LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
      }

      String var0;
      try {
         var0 = PlatformDependent.isWindows() ? InetAddress.getLocalHost().getHostName() : null;
      } catch (Exception var5) {
         var0 = null;
      }

      WINDOWS_HOST_NAME = var0;

      try {
         List var1 = PlatformDependent.isWindows() ? getSearchDomainsHack() : UnixResolverDnsServerAddressStreamProvider.parseEtcResolverSearchDomains();
         var0 = (String[])var1.toArray(new String[0]);
      } catch (Exception var4) {
         var0 = EmptyArrays.EMPTY_STRINGS;
      }

      DEFAULT_SEARCH_DOMAINS = var0;

      UnixResolverOptions var7;
      try {
         var7 = UnixResolverDnsServerAddressStreamProvider.parseEtcResolverOptions();
      } catch (Exception var3) {
         var7 = UnixResolverOptions.newBuilder().build();
      }

      DEFAULT_OPTIONS = var7;
      DATAGRAM_DECODER = new DatagramDnsResponseDecoder() {
         @Override
         protected DnsResponse decodeResponse(ChannelHandlerContext var1, DatagramPacket var2) throws Exception {
            DnsResponse var3 = super.decodeResponse(var1, var2);
            if (var2.content().isReadable()) {
               var3.setTruncated(true);
               if (DnsNameResolver.logger.isDebugEnabled()) {
                  DnsNameResolver.logger
                     .debug(
                        "{} RECEIVED: UDP truncated packet received, consider adjusting maxPayloadSize for the {}.",
                        var1.channel(),
                        StringUtil.simpleClassName(DnsNameResolver.class)
                     );
               }
            }

            return var3;
         }
      };
      DATAGRAM_ENCODER = new DatagramDnsQueryEncoder();
      TCP_ENCODER = new TcpDnsQueryEncoder();
   }

   private static final class AddressedEnvelopeAdapter implements AddressedEnvelope<DnsResponse, InetSocketAddress> {
      private final InetSocketAddress sender;
      private final InetSocketAddress recipient;
      private final DnsResponse response;

      AddressedEnvelopeAdapter(InetSocketAddress var1, InetSocketAddress var2, DnsResponse var3) {
         this.sender = var1;
         this.recipient = var2;
         this.response = var3;
      }

      public DnsResponse content() {
         return this.response;
      }

      public InetSocketAddress sender() {
         return this.sender;
      }

      public InetSocketAddress recipient() {
         return this.recipient;
      }

      @Override
      public AddressedEnvelope<DnsResponse, InetSocketAddress> retain() {
         this.response.retain();
         return this;
      }

      @Override
      public AddressedEnvelope<DnsResponse, InetSocketAddress> retain(int var1) {
         this.response.retain(var1);
         return this;
      }

      @Override
      public AddressedEnvelope<DnsResponse, InetSocketAddress> touch() {
         this.response.touch();
         return this;
      }

      @Override
      public AddressedEnvelope<DnsResponse, InetSocketAddress> touch(Object var1) {
         this.response.touch(var1);
         return this;
      }

      @Override
      public int refCnt() {
         return this.response.refCnt();
      }

      @Override
      public boolean release() {
         return this.response.release();
      }

      @Override
      public boolean release(int var1) {
         return this.response.release(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof AddressedEnvelope)) {
            return false;
         } else {
            AddressedEnvelope var2 = (AddressedEnvelope)var1;
            if (this.sender() == null) {
               if (var2.sender() != null) {
                  return false;
               }
            } else if (!this.sender().equals(var2.sender())) {
               return false;
            }

            if (this.recipient() == null) {
               if (var2.recipient() != null) {
                  return false;
               }
            } else if (!this.recipient().equals(var2.recipient())) {
               return false;
            }

            return this.response.equals(var1);
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.response.hashCode();
         if (this.sender() != null) {
            var1 = var1 * 31 + this.sender().hashCode();
         }

         if (this.recipient() != null) {
            var1 = var1 * 31 + this.recipient().hashCode();
         }

         return var1;
      }
   }

   private final class DnsResponseHandler extends ChannelInboundHandlerAdapter {
      private final Promise<Channel> channelActivePromise;

      DnsResponseHandler(Promise<Channel> var2) {
         this.channelActivePromise = var2;
      }

      @Override
      public void channelRead(ChannelHandlerContext var1, Object var2) {
         final DatagramDnsResponse var3 = (DatagramDnsResponse)var2;
         final int var4 = var3.id();
         if (DnsNameResolver.logger.isDebugEnabled()) {
            DnsNameResolver.logger.debug("{} RECEIVED: UDP [{}: {}], {}", DnsNameResolver.this.ch, var4, var3.sender(), var3);
         }

         final DnsQueryContext var5 = DnsNameResolver.this.queryContextManager.get(var3.sender(), var4);
         if (var5 == null) {
            DnsNameResolver.logger.debug("Received a DNS response with an unknown ID: UDP [{}: {}]", DnsNameResolver.this.ch, var4);
            var3.release();
         } else if (var3.isTruncated() && DnsNameResolver.this.socketChannelFactory != null) {
            Bootstrap var6 = new Bootstrap();
            var6.option(ChannelOption.SO_REUSEADDR, Boolean.valueOf(true))
               .group(DnsNameResolver.this.executor())
               .channelFactory(DnsNameResolver.this.socketChannelFactory)
               .handler(DnsNameResolver.TCP_ENCODER);
            var6.connect(var3.sender())
               .addListener(
                  new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1) {
                        if (!var1.isSuccess()) {
                           if (DnsNameResolver.logger.isDebugEnabled()) {
                              DnsNameResolver.logger.debug("Unable to fallback to TCP [{}]", var4, var1.cause());
                           }

                           var5.finish(var3);
                        } else {
                           final Channel var2 = var1.channel();
                           Promise var3x = var2.eventLoop().newPromise();
                           final TcpDnsQueryContext var4x = new TcpDnsQueryContext(
                              DnsNameResolver.this, var2, (InetSocketAddress)var2.remoteAddress(), var5.question(), DnsNameResolver.EMPTY_ADDITIONALS, var3x
                           );
                           var2.pipeline().addLast(new TcpDnsResponseDecoder());
                           var2.pipeline()
                              .addLast(
                                 new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext var1, Object var2) {
                                       Channel var3x = var1.channel();
                                       DnsResponse var4xx = (DnsResponse)var2;
                                       int var5x = var4xx.id();
                                       if (DnsNameResolver.logger.isDebugEnabled()) {
                                          DnsNameResolver.logger.debug("{} RECEIVED: TCP [{}: {}], {}", var3x, var5x, var3x.remoteAddress(), var4xx);
                                       }

                                       DnsQueryContext var6 = DnsNameResolver.this.queryContextManager.get(var3.sender(), var5x);
                                       if (var6 == var4x) {
                                          var4x.finish(
                                             new DnsNameResolver.AddressedEnvelopeAdapter(
                                                (InetSocketAddress)var1.channel().remoteAddress(), (InetSocketAddress)var1.channel().localAddress(), var4xx
                                             )
                                          );
                                       } else {
                                          var4xx.release();
                                          var4x.tryFailure("Received TCP DNS response with unexpected ID", null, false);
                                          DnsNameResolver.logger.debug("Received a DNS response with an unexpected ID: TCP [{}: {}]", var3x, var5x);
                                       }
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
                                       if (var4x.tryFailure("TCP fallback error", var2, false) && DnsNameResolver.logger.isDebugEnabled()) {
                                          DnsNameResolver.logger
                                             .debug(
                                                "{} Error during processing response: TCP [{}: {}]", var1.channel(), var4, var1.channel().remoteAddress(), var2
                                             );
                                       }
                                    }
                                 }
                              );
                           var3x.addListener(new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
                              @Override
                              public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> var1) {
                                 var2.close();
                                 if (var1.isSuccess()) {
                                    var5.finish((AddressedEnvelope<? extends DnsResponse, InetSocketAddress>)var1.getNow());
                                    var3.release();
                                 } else {
                                    var5.finish(var3);
                                 }
                              }
                           });
                           var4x.query(true, var1.channel().newPromise());
                        }
                     }
                  }
               );
         } else {
            var5.finish(var3);
         }
      }

      @Override
      public void channelActive(ChannelHandlerContext var1) throws Exception {
         super.channelActive(var1);
         this.channelActivePromise.trySuccess(var1.channel());
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
         if (var2 instanceof CorruptedFrameException) {
            DnsNameResolver.logger.debug("Unable to decode DNS response: UDP [{}]", var1.channel(), var2);
         } else {
            DnsNameResolver.logger.warn("Unexpected exception: UDP [{}]", var1.channel(), var2);
         }
      }
   }
}
