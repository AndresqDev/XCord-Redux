package io.netty.resolver.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsRecordDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

abstract class DnsResolveContext<T> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsResolveContext.class);
   private static final RuntimeException NXDOMAIN_QUERY_FAILED_EXCEPTION = DnsResolveContext.DnsResolveContextException.newStatic(
      "No answer found and NXDOMAIN response code returned", DnsResolveContext.class, "onResponse(..)"
   );
   private static final RuntimeException CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION = DnsResolveContext.DnsResolveContextException.newStatic(
      "No matching CNAME record found", DnsResolveContext.class, "onResponseCNAME(..)"
   );
   private static final RuntimeException NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION = DnsResolveContext.DnsResolveContextException.newStatic(
      "No matching record type found", DnsResolveContext.class, "onResponseAorAAAA(..)"
   );
   private static final RuntimeException UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION = DnsResolveContext.DnsResolveContextException.newStatic(
      "Response type was unrecognized", DnsResolveContext.class, "onResponse(..)"
   );
   private static final RuntimeException NAME_SERVERS_EXHAUSTED_EXCEPTION = DnsResolveContext.DnsResolveContextException.newStatic(
      "No name servers returned an answer", DnsResolveContext.class, "tryToFinishResolve(..)"
   );
   final DnsNameResolver parent;
   private final Promise<?> originalPromise;
   private final DnsServerAddressStream nameServerAddrs;
   private final String hostname;
   private final int dnsClass;
   private final DnsRecordType[] expectedTypes;
   final DnsRecord[] additionals;
   private final Set<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> queriesInProgress = Collections.newSetFromMap(new IdentityHashMap<>());
   private List<T> finalResult;
   private int allowedQueries;
   private boolean triedCNAME;
   private boolean completeEarly;

   DnsResolveContext(
      DnsNameResolver var1, Promise<?> var2, String var3, int var4, DnsRecordType[] var5, DnsRecord[] var6, DnsServerAddressStream var7, int var8
   ) {
      assert var5.length > 0;

      this.parent = var1;
      this.originalPromise = var2;
      this.hostname = var3;
      this.dnsClass = var4;
      this.expectedTypes = var5;
      this.additionals = var6;
      this.nameServerAddrs = ObjectUtil.checkNotNull(var7, "nameServerAddrs");
      this.allowedQueries = var8;
   }

   DnsCache resolveCache() {
      return this.parent.resolveCache();
   }

   DnsCnameCache cnameCache() {
      return this.parent.cnameCache();
   }

   AuthoritativeDnsServerCache authoritativeDnsServerCache() {
      return this.parent.authoritativeDnsServerCache();
   }

   abstract DnsResolveContext<T> newResolverContext(
      DnsNameResolver var1, Promise<?> var2, String var3, int var4, DnsRecordType[] var5, DnsRecord[] var6, DnsServerAddressStream var7, int var8
   );

   abstract T convertRecord(DnsRecord var1, String var2, DnsRecord[] var3, EventLoop var4);

   abstract List<T> filterResults(List<T> var1);

   abstract boolean isCompleteEarly(T var1);

   abstract boolean isDuplicateAllowed();

   abstract void cache(String var1, DnsRecord[] var2, DnsRecord var3, T var4);

   abstract void cache(String var1, DnsRecord[] var2, UnknownHostException var3);

   void resolve(final Promise<List<T>> var1) {
      final String[] var2 = this.parent.searchDomains();
      if (var2.length != 0 && this.parent.ndots() != 0 && !StringUtil.endsWith(this.hostname, '.')) {
         final boolean var3 = this.hasNDots();
         String var4 = var3 ? this.hostname : this.hostname + '.' + var2[0];
         final int var5 = var3 ? 0 : 1;
         Promise var6 = this.parent.executor().newPromise();
         var6.addListener(new FutureListener<List<T>>() {
            private int searchDomainIdx = var5;

            @Override
            public void operationComplete(Future<List<T>> var1x) {
               Throwable var2x = var1x.cause();
               if (var2x == null) {
                  List var3x = (List)var1x.getNow();
                  if (!var1.trySuccess(var3x)) {
                     for (Object var5x : var3x) {
                        ReferenceCountUtil.safeRelease(var5x);
                     }
                  }
               } else if (DnsNameResolver.isTransportOrTimeoutError(var2x)) {
                  var1.tryFailure(new DnsResolveContext.SearchDomainUnknownHostException(var2x, DnsResolveContext.this.hostname, var2));
               } else if (this.searchDomainIdx < var2.length) {
                  Promise var6 = DnsResolveContext.this.parent.executor().newPromise();
                  var6.addListener(this);
                  DnsResolveContext.this.doSearchDomainQuery(DnsResolveContext.this.hostname + '.' + var2[this.searchDomainIdx++], var6);
               } else if (!var3) {
                  DnsResolveContext.this.internalResolve(DnsResolveContext.this.hostname, var1);
               } else {
                  var1.tryFailure(new DnsResolveContext.SearchDomainUnknownHostException(var2x, DnsResolveContext.this.hostname, var2));
               }
            }
         });
         this.doSearchDomainQuery(var4, var6);
      } else {
         this.internalResolve(this.hostname, var1);
      }
   }

   private boolean hasNDots() {
      int var1 = this.hostname.length() - 1;

      for (int var2 = 0; var1 >= 0; var1--) {
         if (this.hostname.charAt(var1) == '.') {
            if (++var2 >= this.parent.ndots()) {
               return true;
            }
         }
      }

      return false;
   }

   void doSearchDomainQuery(String var1, Promise<List<T>> var2) {
      DnsResolveContext var3 = this.newResolverContext(
         this.parent, this.originalPromise, var1, this.dnsClass, this.expectedTypes, this.additionals, this.nameServerAddrs, this.parent.maxQueriesPerResolve()
      );
      var3.internalResolve(var1, var2);
   }

   private static String hostnameWithDot(String var0) {
      return StringUtil.endsWith(var0, '.') ? var0 : var0 + '.';
   }

   static String cnameResolveFromCache(DnsCnameCache var0, String var1) throws UnknownHostException {
      String var2 = var0.get(hostnameWithDot(var1));
      if (var2 == null) {
         return var1;
      } else {
         String var3 = var0.get(hostnameWithDot(var2));
         if (var3 == null) {
            return var2;
         } else {
            checkCnameLoop(var1, var2, var3);
            return cnameResolveFromCacheLoop(var0, var1, var2, var3);
         }
      }
   }

   private static String cnameResolveFromCacheLoop(DnsCnameCache var0, String var1, String var2, String var3) throws UnknownHostException {
      boolean var4 = false;

      String var5;
      for (var5 = var3; (var3 = var0.get(hostnameWithDot(var5))) != null; var4 = !var4) {
         checkCnameLoop(var1, var2, var3);
         var5 = var3;
         if (var4) {
            var2 = var0.get(var2);
         }
      }

      return var5;
   }

   private static void checkCnameLoop(String var0, String var1, String var2) throws UnknownHostException {
      if (var1.equals(var2)) {
         throw new UnknownHostException("CNAME loop detected for '" + var0 + '\'');
      }
   }

   private void internalResolve(String var1, Promise<List<T>> var2) {
      try {
         var1 = cnameResolveFromCache(this.cnameCache(), var1);
      } catch (Throwable var9) {
         var2.tryFailure(var9);
         return;
      }

      try {
         DnsServerAddressStream var3 = this.getNameServers(var1);
         int var4 = this.expectedTypes.length - 1;

         for (int var5 = 0; var5 < var4; var5++) {
            if (!this.query(var1, this.expectedTypes[var5], var3.duplicate(), false, var2)) {
               return;
            }
         }

         this.query(var1, this.expectedTypes[var4], var3, false, var2);
      } finally {
         this.parent.flushQueries();
      }
   }

   private DnsServerAddressStream getNameServersFromCache(String var1) {
      int var2 = var1.length();
      if (var2 == 0) {
         return null;
      } else {
         if (var1.charAt(var2 - 1) != '.') {
            var1 = var1 + ".";
         }

         int var3 = var1.indexOf(46);
         if (var3 == var1.length() - 1) {
            return null;
         } else {
            DnsServerAddressStream var5;
            do {
               var1 = var1.substring(var3 + 1);
               int var4 = var1.indexOf(46);
               if (var4 <= 0 || var4 == var1.length() - 1) {
                  return null;
               }

               var3 = var4;
               var5 = this.authoritativeDnsServerCache().get(var1);
            } while (var5 == null);

            return var5;
         }
      }
   }

   private void query(
      final DnsServerAddressStream var1,
      final int var2,
      final DnsQuestion var3,
      final DnsQueryLifecycleObserver var4,
      boolean var5,
      final Promise<List<T>> var6,
      Throwable var7
   ) {
      if (!this.completeEarly && var2 < var1.size() && this.allowedQueries != 0 && !this.originalPromise.isCancelled() && !var6.isCancelled()) {
         this.allowedQueries--;
         InetSocketAddress var8 = var1.next();
         if (var8.isUnresolved()) {
            this.queryUnresolvedNameServer(var8, var1, var2, var3, var4, var6, var7);
         } else {
            ChannelPromise var9 = this.parent.ch.newPromise();
            Promise var10 = this.parent.ch.eventLoop().newPromise();
            Future var11 = this.parent.query0(var8, var3, this.additionals, var5, var9, var10);
            this.queriesInProgress.add(var11);
            var4.queryWritten(var8, var9);
            var11.addListener(new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
               @Override
               public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> var1x) {
                  DnsResolveContext.this.queriesInProgress.remove(var1x);
                  if (!var6.isDone() && !var1x.isCancelled()) {
                     Throwable var6x = var1x.cause();

                     try {
                        if (var6x == null) {
                           DnsResolveContext.this.onResponse(var1, var2, var3, (AddressedEnvelope<DnsResponse, InetSocketAddress>)var1x.getNow(), var4, var6);
                        } else {
                           var4.queryFailed(var6x);
                           DnsResolveContext.this.query(var1, var2 + 1, var3, DnsResolveContext.this.newDnsQueryLifecycleObserver(var3), true, var6, var6x);
                        }
                     } finally {
                        DnsResolveContext.this.tryToFinishResolve(var1, var2, var3, NoopDnsQueryLifecycleObserver.INSTANCE, var6, var6x);
                     }
                  } else {
                     var4.queryCancelled(DnsResolveContext.this.allowedQueries);
                     AddressedEnvelope var2x = (AddressedEnvelope)var1x.getNow();
                     if (var2x != null) {
                        var2x.release();
                     }
                  }
               }
            });
         }
      } else {
         this.tryToFinishResolve(var1, var2, var3, var4, var6, var7);
      }
   }

   private void queryUnresolvedNameServer(
      final InetSocketAddress var1,
      final DnsServerAddressStream var2,
      final int var3,
      final DnsQuestion var4,
      final DnsQueryLifecycleObserver var5,
      final Promise<List<T>> var6,
      final Throwable var7
   ) {
      String var8 = PlatformDependent.javaVersion() >= 7 ? var1.getHostString() : var1.getHostName();

      assert var8 != null;

      final Future var9 = this.parent.executor().newSucceededFuture(null);
      this.queriesInProgress.add(var9);
      Promise var10 = this.parent.executor().newPromise();
      var10.addListener(new FutureListener<List<InetAddress>>() {
         @Override
         public void operationComplete(Future<List<InetAddress>> var1x) {
            DnsResolveContext.this.queriesInProgress.remove(var9);
            if (var1x.isSuccess()) {
               List var2x = (List)var1x.getNow();
               DnsResolveContext.CombinedDnsServerAddressStream var3x = DnsResolveContext.this.new CombinedDnsServerAddressStream(var1, var2x, var2);
               DnsResolveContext.this.query(var3x, var3, var4, var5, true, var6, var7);
            } else {
               DnsResolveContext.this.query(var2, var3 + 1, var4, var5, true, var6, var7);
            }
         }
      });
      DnsCache var11 = this.resolveCache();
      if (!DnsNameResolver.doResolveAllCached(var8, this.additionals, var10, var11, this.parent.resolvedInternetProtocolFamiliesUnsafe())) {
         new DnsAddressResolveContext(
               this.parent,
               this.originalPromise,
               var8,
               this.additionals,
               this.parent.newNameServerAddressStream(var8),
               this.allowedQueries,
               var11,
               redirectAuthoritativeDnsServerCache(this.authoritativeDnsServerCache()),
               false
            )
            .resolve(var10);
      }
   }

   private static AuthoritativeDnsServerCache redirectAuthoritativeDnsServerCache(AuthoritativeDnsServerCache var0) {
      return (AuthoritativeDnsServerCache)(var0 instanceof DnsResolveContext.RedirectAuthoritativeDnsServerCache
         ? var0
         : new DnsResolveContext.RedirectAuthoritativeDnsServerCache(var0));
   }

   private void onResponse(
      DnsServerAddressStream var1,
      int var2,
      DnsQuestion var3,
      AddressedEnvelope<DnsResponse, InetSocketAddress> var4,
      DnsQueryLifecycleObserver var5,
      Promise<List<T>> var6
   ) {
      try {
         DnsResponse var7 = (DnsResponse)var4.content();
         DnsResponseCode var8 = var7.code();
         if (var8 != DnsResponseCode.NOERROR) {
            if (var8 != DnsResponseCode.NXDOMAIN) {
               this.query(var1, var2 + 1, var3, var5.queryNoAnswer(var8), true, var6, null);
            } else {
               var5.queryFailed(NXDOMAIN_QUERY_FAILED_EXCEPTION);
               if (!var7.isAuthoritativeAnswer()) {
                  this.query(var1, var2 + 1, var3, this.newDnsQueryLifecycleObserver(var3), true, var6, null);
                  return;
               }
            }
         } else if (!this.handleRedirect(var3, var4, var5, var6)) {
            DnsRecordType var9 = var3.type();
            if (var9 == DnsRecordType.CNAME) {
               this.onResponseCNAME(var3, buildAliasMap((DnsResponse)var4.content(), this.cnameCache(), this.parent.executor()), var5, var6);
            } else {
               for (DnsRecordType var13 : this.expectedTypes) {
                  if (var9 == var13) {
                     this.onExpectedResponse(var3, var4, var5, var6);
                     return;
                  }
               }

               var5.queryFailed(UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION);
            }
         }
      } finally {
         ReferenceCountUtil.safeRelease(var4);
      }
   }

   private boolean handleRedirect(
      DnsQuestion var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4
   ) {
      DnsResponse var5 = (DnsResponse)var2.content();
      if (var5.count(DnsSection.ANSWER) == 0) {
         DnsResolveContext.AuthoritativeNameServerList var6 = extractAuthoritativeNameServers(var1.name(), var5);
         if (var6 != null) {
            int var7 = var5.count(DnsSection.ADDITIONAL);
            AuthoritativeDnsServerCache var8 = this.authoritativeDnsServerCache();

            for (int var9 = 0; var9 < var7; var9++) {
               DnsRecord var10 = var5.recordAt(DnsSection.ADDITIONAL, var9);
               if ((var10.type() != DnsRecordType.A || this.parent.supportsARecords())
                  && (var10.type() != DnsRecordType.AAAA || this.parent.supportsAAAARecords())) {
                  var6.handleWithAdditional(this.parent, var10, var8);
               }
            }

            var6.handleWithoutAdditionals(this.parent, this.resolveCache(), var8);
            List var11 = var6.addressList();
            DnsServerAddressStream var12 = this.parent.newRedirectDnsServerStream(var1.name(), var11);
            if (var12 != null) {
               this.query(var12, 0, var1, var3.queryRedirected(new DnsResolveContext.DnsAddressStreamList(var12)), true, var4, null);
               return true;
            }
         }
      }

      return false;
   }

   private static DnsResolveContext.AuthoritativeNameServerList extractAuthoritativeNameServers(String var0, DnsResponse var1) {
      int var2 = var1.count(DnsSection.AUTHORITY);
      if (var2 == 0) {
         return null;
      } else {
         DnsResolveContext.AuthoritativeNameServerList var3 = new DnsResolveContext.AuthoritativeNameServerList(var0);

         for (int var4 = 0; var4 < var2; var4++) {
            var3.add(var1.recordAt(DnsSection.AUTHORITY, var4));
         }

         return var3.isEmpty() ? null : var3;
      }
   }

   private void onExpectedResponse(
      DnsQuestion var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4
   ) {
      DnsResponse var5 = (DnsResponse)var2.content();
      Map var6 = buildAliasMap(var5, this.cnameCache(), this.parent.executor());
      int var7 = var5.count(DnsSection.ANSWER);
      boolean var8 = false;
      boolean var9 = this.completeEarly;

      for (int var10 = 0; var10 < var7; var10++) {
         DnsRecord var11 = var5.recordAt(DnsSection.ANSWER, var10);
         DnsRecordType var12 = var11.type();
         boolean var13 = false;

         for (DnsRecordType var17 : this.expectedTypes) {
            if (var12 == var17) {
               var13 = true;
               break;
            }
         }

         if (var13) {
            String var23 = var1.name().toLowerCase(Locale.US);
            String var24 = var11.name().toLowerCase(Locale.US);
            if (!var24.equals(var23)) {
               HashMap var25 = new HashMap(var6);
               String var27 = var23;

               do {
                  var27 = (String)var25.remove(var27);
               } while (!var24.equals(var27) && var27 != null);

               if (var27 == null) {
                  assert var23.isEmpty() || var23.charAt(var23.length() - 1) == '.';

                  for (String var21 : this.parent.searchDomains()) {
                     if (!var21.isEmpty()) {
                        String var22;
                        if (var21.charAt(var21.length() - 1) == '.') {
                           var22 = var23 + var21;
                        } else {
                           var22 = var23 + var21 + '.';
                        }

                        if (var24.equals(var22)) {
                           var27 = var24;
                           break;
                        }
                     }
                  }

                  if (var27 == null) {
                     if (logger.isDebugEnabled()) {
                        logger.debug(
                           "Ignoring record {} as it contains a different name than the question name [{}]. Cnames: {}, Search domains: {}",
                           var11.toString(),
                           var23,
                           var6,
                           this.parent.searchDomains()
                        );
                     }
                     continue;
                  }
               }
            }

            Object var26 = this.convertRecord(var11, this.hostname, this.additionals, this.parent.executor());
            if (var26 == null) {
               if (logger.isDebugEnabled()) {
                  logger.debug(
                     "Ignoring record {} as the converted record is null. hostname [{}], Additionals: {}", var11.toString(), this.hostname, this.additionals
                  );
               }
            } else {
               boolean var28 = false;
               if (!var9) {
                  var9 = this.isCompleteEarly((T)var26);
               }

               if (this.finalResult == null) {
                  this.finalResult = new ArrayList<>(8);
                  this.finalResult.add((T)var26);
               } else if (!this.isDuplicateAllowed() && this.finalResult.contains(var26)) {
                  var28 = true;
               } else {
                  this.finalResult.add((T)var26);
               }

               this.cache(this.hostname, this.additionals, var11, (T)var26);
               var8 = true;
               if (var28) {
                  ReferenceCountUtil.release(var26);
               }
            }
         }
      }

      if (var6.isEmpty()) {
         if (var8) {
            if (var9) {
               this.completeEarly = true;
            }

            var3.querySucceed();
            return;
         }

         var3.queryFailed(NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION);
      } else {
         var3.querySucceed();
         this.onResponseCNAME(var1, var6, this.newDnsQueryLifecycleObserver(var1), var4);
      }
   }

   private void onResponseCNAME(DnsQuestion var1, Map<String, String> var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4) {
      String var5 = var1.name().toLowerCase(Locale.US);
      boolean var6 = false;

      while (!var2.isEmpty()) {
         String var7 = (String)var2.remove(var5);
         if (var7 == null) {
            break;
         }

         var6 = true;
         var5 = var7;
      }

      if (var6) {
         this.followCname(var1, var5, var3, var4);
      } else {
         var3.queryFailed(CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION);
      }
   }

   private static Map<String, String> buildAliasMap(DnsResponse var0, DnsCnameCache var1, EventLoop var2) {
      int var3 = var0.count(DnsSection.ANSWER);
      HashMap var4 = null;

      for (int var5 = 0; var5 < var3; var5++) {
         DnsRecord var6 = var0.recordAt(DnsSection.ANSWER, var5);
         DnsRecordType var7 = var6.type();
         if (var7 == DnsRecordType.CNAME && var6 instanceof DnsRawRecord) {
            ByteBuf var8 = ((ByteBufHolder)var6).content();
            String var9 = decodeDomainName(var8);
            if (var9 != null) {
               if (var4 == null) {
                  var4 = new HashMap(Math.min(8, var3));
               }

               String var10 = var6.name().toLowerCase(Locale.US);
               String var11 = var9.toLowerCase(Locale.US);
               String var12 = hostnameWithDot(var10);
               String var13 = hostnameWithDot(var11);
               if (!var12.equalsIgnoreCase(var13)) {
                  var1.cache(var12, var13, var6.timeToLive(), var2);
                  var4.put(var10, var11);
               }
            }
         }
      }

      return (Map<String, String>)(var4 != null ? var4 : Collections.emptyMap());
   }

   private void tryToFinishResolve(
      DnsServerAddressStream var1, int var2, DnsQuestion var3, DnsQueryLifecycleObserver var4, Promise<List<T>> var5, Throwable var6
   ) {
      if (!this.completeEarly && !this.queriesInProgress.isEmpty()) {
         var4.queryCancelled(this.allowedQueries);
      } else {
         if (this.finalResult == null) {
            if (var2 < var1.size()) {
               if (var4 == NoopDnsQueryLifecycleObserver.INSTANCE) {
                  this.query(var1, var2 + 1, var3, this.newDnsQueryLifecycleObserver(var3), true, var5, var6);
               } else {
                  this.query(var1, var2 + 1, var3, var4, true, var5, var6);
               }

               return;
            }

            var4.queryFailed(NAME_SERVERS_EXHAUSTED_EXCEPTION);
            if (var6 == null && !this.triedCNAME && (var3.type() == DnsRecordType.A || var3.type() == DnsRecordType.AAAA)) {
               this.triedCNAME = true;
               this.query(this.hostname, DnsRecordType.CNAME, this.getNameServers(this.hostname), true, var5);
               return;
            }
         } else {
            var4.queryCancelled(this.allowedQueries);
         }

         this.finishResolve(var5, var6);
      }
   }

   private void finishResolve(Promise<List<T>> var1, Throwable var2) {
      if (!this.completeEarly && !this.queriesInProgress.isEmpty()) {
         Iterator var3 = this.queriesInProgress.iterator();

         while (var3.hasNext()) {
            Future var4 = (Future)var3.next();
            var3.remove();
            var4.cancel(false);
         }
      }

      if (this.finalResult == null) {
         int var8 = this.parent.maxQueriesPerResolve();
         int var10 = var8 - this.allowedQueries;
         StringBuilder var11 = new StringBuilder(64);
         var11.append("Failed to resolve '").append(this.hostname).append('\'');
         if (var10 > 1) {
            if (var10 < var8) {
               var11.append(" after ").append(var10).append(" queries ");
            } else {
               var11.append(". Exceeded max queries per resolve ").append(var8).append(' ');
            }
         }

         UnknownHostException var6 = new UnknownHostException(var11.toString());
         if (var2 == null) {
            this.cache(this.hostname, this.additionals, var6);
         } else {
            var6.initCause(var2);
         }

         var1.tryFailure(var6);
      } else {
         if (!var1.isDone()) {
            List var7 = this.filterResults(this.finalResult);
            if (!DnsNameResolver.trySuccess(var1, var7)) {
               for (Object var5 : var7) {
                  ReferenceCountUtil.safeRelease(var5);
               }
            }
         }
      }
   }

   static String decodeDomainName(ByteBuf var0) {
      var0.markReaderIndex();

      Object var2;
      try {
         return DefaultDnsRecordDecoder.decodeName(var0);
      } catch (CorruptedFrameException var6) {
         var2 = null;
      } finally {
         var0.resetReaderIndex();
      }

      return (String)var2;
   }

   private DnsServerAddressStream getNameServers(String var1) {
      DnsServerAddressStream var2 = this.getNameServersFromCache(var1);
      if (var2 == null) {
         return var1.equals(this.hostname) ? this.nameServerAddrs.duplicate() : this.parent.newNameServerAddressStream(var1);
      } else {
         return var2;
      }
   }

   private void followCname(DnsQuestion var1, String var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4) {
      DefaultDnsQuestion var5;
      DnsServerAddressStream var6;
      try {
         var2 = cnameResolveFromCache(this.cnameCache(), var2);
         var6 = this.getNameServers(var2);
         var5 = new DefaultDnsQuestion(var2, var1.type(), this.dnsClass);
      } catch (Throwable var8) {
         var3.queryFailed(var8);
         PlatformDependent.throwException(var8);
         return;
      }

      this.query(var6, 0, var5, var3.queryCNAMEd(var5), true, var4, null);
   }

   private boolean query(String var1, DnsRecordType var2, DnsServerAddressStream var3, boolean var4, Promise<List<T>> var5) {
      DefaultDnsQuestion var6;
      try {
         var6 = new DefaultDnsQuestion(var1, var2, this.dnsClass);
      } catch (Throwable var8) {
         var5.tryFailure(new IllegalArgumentException("Unable to create DNS Question for: [" + var1 + ", " + var2 + ']', var8));
         return false;
      }

      this.query(var3, 0, var6, this.newDnsQueryLifecycleObserver(var6), var4, var5, null);
      return true;
   }

   private DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion var1) {
      return this.parent.dnsQueryLifecycleObserverFactory().newDnsQueryLifecycleObserver(var1);
   }

   private static final class AuthoritativeNameServer {
      private final int dots;
      private final String domainName;
      final boolean isCopy;
      final String nsName;
      private long ttl;
      private InetSocketAddress address;
      DnsResolveContext.AuthoritativeNameServer next;

      AuthoritativeNameServer(int var1, long var2, String var4, String var5) {
         this.dots = var1;
         this.ttl = var2;
         this.nsName = var5;
         this.domainName = var4;
         this.isCopy = false;
      }

      AuthoritativeNameServer(DnsResolveContext.AuthoritativeNameServer var1) {
         this.dots = var1.dots;
         this.ttl = var1.ttl;
         this.nsName = var1.nsName;
         this.domainName = var1.domainName;
         this.isCopy = true;
      }

      boolean isRootServer() {
         return this.dots == 1;
      }

      void update(InetSocketAddress var1, long var2) {
         assert this.address == null || this.address.isUnresolved();

         this.address = var1;
         this.ttl = Math.min(this.ttl, var2);
      }

      void update(InetSocketAddress var1) {
         this.update(var1, Long.MAX_VALUE);
      }
   }

   private static final class AuthoritativeNameServerList {
      private final String questionName;
      private DnsResolveContext.AuthoritativeNameServer head;
      private int nameServerCount;

      AuthoritativeNameServerList(String var1) {
         this.questionName = var1.toLowerCase(Locale.US);
      }

      void add(DnsRecord var1) {
         if (var1.type() == DnsRecordType.NS && var1 instanceof DnsRawRecord) {
            if (this.questionName.length() >= var1.name().length()) {
               String var2 = var1.name().toLowerCase(Locale.US);
               int var3 = 0;
               int var4 = var2.length() - 1;

               for (int var5 = this.questionName.length() - 1; var4 >= 0; var5--) {
                  char var6 = var2.charAt(var4);
                  if (this.questionName.charAt(var5) != var6) {
                     return;
                  }

                  if (var6 == '.') {
                     var3++;
                  }

                  var4--;
               }

               if (this.head == null || this.head.dots <= var3) {
                  ByteBuf var7 = ((ByteBufHolder)var1).content();
                  String var8 = DnsResolveContext.decodeDomainName(var7);
                  if (var8 != null) {
                     if (this.head == null || this.head.dots < var3) {
                        this.nameServerCount = 1;
                        this.head = new DnsResolveContext.AuthoritativeNameServer(var3, var1.timeToLive(), var2, var8);
                     } else if (this.head.dots == var3) {
                        DnsResolveContext.AuthoritativeNameServer var9 = this.head;

                        while (var9.next != null) {
                           var9 = var9.next;
                        }

                        var9.next = new DnsResolveContext.AuthoritativeNameServer(var3, var1.timeToLive(), var2, var8);
                        this.nameServerCount++;
                     }
                  }
               }
            }
         }
      }

      void handleWithAdditional(DnsNameResolver var1, DnsRecord var2, AuthoritativeDnsServerCache var3) {
         DnsResolveContext.AuthoritativeNameServer var4 = this.head;
         String var5 = var2.name();
         InetAddress var6 = DnsAddressDecoder.decodeAddress(var2, var5, var1.isDecodeIdn());
         if (var6 != null) {
            while (var4 != null) {
               if (var4.nsName.equalsIgnoreCase(var5)) {
                  if (var4.address != null) {
                     while (var4.next != null && var4.next.isCopy) {
                        var4 = var4.next;
                     }

                     DnsResolveContext.AuthoritativeNameServer var7 = new DnsResolveContext.AuthoritativeNameServer(var4);
                     var7.next = var4.next;
                     var4.next = var7;
                     var4 = var7;
                     this.nameServerCount++;
                  }

                  var4.update(var1.newRedirectServerAddress(var6), var2.timeToLive());
                  cache(var4, var3, var1.executor());
                  return;
               }

               var4 = var4.next;
            }
         }
      }

      void handleWithoutAdditionals(DnsNameResolver var1, DnsCache var2, AuthoritativeDnsServerCache var3) {
         for (DnsResolveContext.AuthoritativeNameServer var4 = this.head; var4 != null; var4 = var4.next) {
            if (var4.address == null) {
               cacheUnresolved(var4, var3, var1.executor());
               List var5 = var2.get(var4.nsName, null);
               if (var5 != null && !var5.isEmpty()) {
                  InetAddress var6 = ((DnsCacheEntry)var5.get(0)).address();
                  if (var6 != null) {
                     var4.update(var1.newRedirectServerAddress(var6));

                     for (int var7 = 1; var7 < var5.size(); var7++) {
                        var6 = ((DnsCacheEntry)var5.get(var7)).address();

                        assert var6 != null : "Cache returned a cached failure, should never return anything else";

                        DnsResolveContext.AuthoritativeNameServer var8 = new DnsResolveContext.AuthoritativeNameServer(var4);
                        var8.next = var4.next;
                        var4.next = var8;
                        var4 = var8;
                        var8.update(var1.newRedirectServerAddress(var6));
                        this.nameServerCount++;
                     }
                  }
               }
            }
         }
      }

      private static void cacheUnresolved(DnsResolveContext.AuthoritativeNameServer var0, AuthoritativeDnsServerCache var1, EventLoop var2) {
         var0.address = InetSocketAddress.createUnresolved(var0.nsName, 53);
         cache(var0, var1, var2);
      }

      private static void cache(DnsResolveContext.AuthoritativeNameServer var0, AuthoritativeDnsServerCache var1, EventLoop var2) {
         if (!var0.isRootServer()) {
            var1.cache(var0.domainName, var0.address, var0.ttl, var2);
         }
      }

      boolean isEmpty() {
         return this.nameServerCount == 0;
      }

      List<InetSocketAddress> addressList() {
         ArrayList var1 = new ArrayList(this.nameServerCount);

         for (DnsResolveContext.AuthoritativeNameServer var2 = this.head; var2 != null; var2 = var2.next) {
            if (var2.address != null) {
               var1.add(var2.address);
            }
         }

         return var1;
      }
   }

   private final class CombinedDnsServerAddressStream implements DnsServerAddressStream {
      private final InetSocketAddress replaced;
      private final DnsServerAddressStream originalStream;
      private final List<InetAddress> resolvedAddresses;
      private Iterator<InetAddress> resolved;

      CombinedDnsServerAddressStream(InetSocketAddress var2, List<InetAddress> var3, DnsServerAddressStream var4) {
         this.replaced = var2;
         this.resolvedAddresses = var3;
         this.originalStream = var4;
         this.resolved = var3.iterator();
      }

      @Override
      public InetSocketAddress next() {
         if (this.resolved.hasNext()) {
            return this.nextResolved0();
         } else {
            InetSocketAddress var1 = this.originalStream.next();
            if (var1.equals(this.replaced)) {
               this.resolved = this.resolvedAddresses.iterator();
               return this.nextResolved0();
            } else {
               return var1;
            }
         }
      }

      private InetSocketAddress nextResolved0() {
         return DnsResolveContext.this.parent.newRedirectServerAddress(this.resolved.next());
      }

      @Override
      public int size() {
         return this.originalStream.size() + this.resolvedAddresses.size() - 1;
      }

      @Override
      public DnsServerAddressStream duplicate() {
         return DnsResolveContext.this.new CombinedDnsServerAddressStream(this.replaced, this.resolvedAddresses, this.originalStream.duplicate());
      }
   }

   private static final class DnsAddressStreamList extends AbstractList<InetSocketAddress> {
      private final DnsServerAddressStream duplicate;
      private List<InetSocketAddress> addresses;

      DnsAddressStreamList(DnsServerAddressStream var1) {
         this.duplicate = var1.duplicate();
      }

      public InetSocketAddress get(int var1) {
         if (this.addresses == null) {
            DnsServerAddressStream var2 = this.duplicate.duplicate();
            this.addresses = new ArrayList<>(this.size());

            for (int var3 = 0; var3 < var2.size(); var3++) {
               this.addresses.add(var2.next());
            }
         }

         return this.addresses.get(var1);
      }

      @Override
      public int size() {
         return this.duplicate.size();
      }

      @Override
      public Iterator<InetSocketAddress> iterator() {
         return new Iterator<InetSocketAddress>() {
            private final DnsServerAddressStream stream = DnsAddressStreamList.this.duplicate.duplicate();
            private int i;

            @Override
            public boolean hasNext() {
               return this.i < this.stream.size();
            }

            public InetSocketAddress next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.i++;
                  return this.stream.next();
               }
            }

            @Override
            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }
   }

   static final class DnsResolveContextException extends RuntimeException {
      private static final long serialVersionUID = 1209303419266433003L;

      private DnsResolveContextException(String var1) {
         super(var1);
      }

      @SuppressJava6Requirement(
         reason = "uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks"
      )
      private DnsResolveContextException(String var1, boolean var2) {
         super(var1, null, false, true);

         assert var2;
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }

      static DnsResolveContext.DnsResolveContextException newStatic(String var0, Class<?> var1, String var2) {
         DnsResolveContext.DnsResolveContextException var3;
         if (PlatformDependent.javaVersion() >= 7) {
            var3 = new DnsResolveContext.DnsResolveContextException(var0, true);
         } else {
            var3 = new DnsResolveContext.DnsResolveContextException(var0);
         }

         return ThrowableUtil.unknownStackTrace(var3, var1, var2);
      }
   }

   private static final class RedirectAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache {
      private final AuthoritativeDnsServerCache wrapped;

      RedirectAuthoritativeDnsServerCache(AuthoritativeDnsServerCache var1) {
         this.wrapped = var1;
      }

      @Override
      public DnsServerAddressStream get(String var1) {
         return null;
      }

      @Override
      public void cache(String var1, InetSocketAddress var2, long var3, EventLoop var5) {
         this.wrapped.cache(var1, var2, var3, var5);
      }

      @Override
      public void clear() {
         this.wrapped.clear();
      }

      @Override
      public boolean clear(String var1) {
         return this.wrapped.clear(var1);
      }
   }

   private static final class SearchDomainUnknownHostException extends UnknownHostException {
      private static final long serialVersionUID = -8573510133644997085L;

      SearchDomainUnknownHostException(Throwable var1, String var2, String[] var3) {
         super("Failed to resolve '" + var2 + "' and search domain query for configured domains failed as well: " + Arrays.toString((Object[])var3));
         this.setStackTrace(var1.getStackTrace());
         this.initCause(var1.getCause());
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }
}
