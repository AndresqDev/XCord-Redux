package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import java.util.function.Function;
import java.util.function.Predicate;
import reactor.blockhound.BlockHound.Builder;
import reactor.blockhound.integration.BlockHoundIntegration;

class Hidden {
   @SuppressJava6Requirement(
      reason = "BlockHound is Java 8+, but this class is only loaded by it's SPI"
   )
   public static final class NettyBlockHoundIntegration implements BlockHoundIntegration {
      public void applyTo(Builder var1) {
         var1.allowBlockingCallsInside("io.netty.channel.nio.NioEventLoop", "handleLoopException");
         var1.allowBlockingCallsInside("io.netty.channel.kqueue.KQueueEventLoop", "handleLoopException");
         var1.allowBlockingCallsInside("io.netty.channel.epoll.EpollEventLoop", "handleLoopException");
         var1.allowBlockingCallsInside("io.netty.util.HashedWheelTimer", "start");
         var1.allowBlockingCallsInside("io.netty.util.HashedWheelTimer", "stop");
         var1.allowBlockingCallsInside("io.netty.util.HashedWheelTimer$Worker", "waitForNextTick");
         var1.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "confirmShutdown");
         var1.allowBlockingCallsInside("io.netty.buffer.PoolArena", "lock");
         var1.allowBlockingCallsInside("io.netty.buffer.PoolSubpage", "lock");
         var1.allowBlockingCallsInside("io.netty.buffer.PoolChunk", "allocateRun");
         var1.allowBlockingCallsInside("io.netty.buffer.PoolChunk", "free");
         var1.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "handshake");
         var1.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "runAllDelegatedTasks");
         var1.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "runDelegatedTasks");
         var1.allowBlockingCallsInside("io.netty.util.concurrent.GlobalEventExecutor", "takeTask");
         var1.allowBlockingCallsInside("io.netty.util.concurrent.GlobalEventExecutor", "addTask");
         var1.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "takeTask");
         var1.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "addTask");
         var1.allowBlockingCallsInside("io.netty.handler.ssl.ReferenceCountedOpenSslClientContext$ExtendedTrustManagerVerifyCallback", "verify");
         var1.allowBlockingCallsInside("io.netty.handler.ssl.JdkSslContext$Defaults", "init");
         var1.allowBlockingCallsInside("sun.security.ssl.SSLEngineImpl", "unwrap");
         var1.allowBlockingCallsInside("sun.security.ssl.SSLEngineImpl", "wrap");
         var1.allowBlockingCallsInside("io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider", "parse");
         var1.allowBlockingCallsInside("io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider", "parseEtcResolverSearchDomains");
         var1.allowBlockingCallsInside("io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider", "parseEtcResolverOptions");
         var1.allowBlockingCallsInside("io.netty.resolver.HostsFileEntriesProvider$ParserImpl", "parse");
         var1.allowBlockingCallsInside("io.netty.util.NetUtil$SoMaxConnAction", "run");
         var1.nonBlockingThreadPredicate(new Function<Predicate<Thread>, Predicate<Thread>>() {
            public Predicate<Thread> apply(final Predicate<Thread> var1) {
               return new Predicate<Thread>() {
                  @SuppressJava6Requirement(
                     reason = "Predicate#test"
                  )
                  public boolean test(Thread var1x) {
                     return var1.test(var1x) || var1x instanceof FastThreadLocalThread;
                  }
               };
            }
         });
      }

      public int compareTo(BlockHoundIntegration var1) {
         return 0;
      }
   }
}
