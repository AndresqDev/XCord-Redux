package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.dns.AbstractDnsOptPseudoRrRecord;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

abstract class DnsQueryContext implements FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsQueryContext.class);
   private final DnsNameResolver parent;
   private final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise;
   private final int id;
   private final DnsQuestion question;
   private final DnsRecord[] additionals;
   private final DnsRecord optResource;
   private final InetSocketAddress nameServerAddr;
   private final boolean recursionDesired;
   private volatile Future<?> timeoutFuture;

   DnsQueryContext(
      DnsNameResolver var1, InetSocketAddress var2, DnsQuestion var3, DnsRecord[] var4, Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> var5
   ) {
      this.parent = ObjectUtil.checkNotNull(var1, "parent");
      this.nameServerAddr = ObjectUtil.checkNotNull(var2, "nameServerAddr");
      this.question = ObjectUtil.checkNotNull(var3, "question");
      this.additionals = ObjectUtil.checkNotNull(var4, "additionals");
      this.promise = ObjectUtil.checkNotNull(var5, "promise");
      this.recursionDesired = var1.isRecursionDesired();
      this.id = var1.queryContextManager.add(this);
      var5.addListener(this);
      if (var1.isOptResourceEnabled() && !hasOptRecord(var4)) {
         this.optResource = new AbstractDnsOptPseudoRrRecord(var1.maxPayloadSize(), 0, 0) {
         };
      } else {
         this.optResource = null;
      }
   }

   private static boolean hasOptRecord(DnsRecord[] var0) {
      if (var0 != null && var0.length > 0) {
         for (DnsRecord var4 : var0) {
            if (var4.type() == DnsRecordType.OPT) {
               return true;
            }
         }
      }

      return false;
   }

   InetSocketAddress nameServerAddr() {
      return this.nameServerAddr;
   }

   DnsQuestion question() {
      return this.question;
   }

   DnsNameResolver parent() {
      return this.parent;
   }

   protected abstract DnsQuery newQuery(int var1);

   protected abstract Channel channel();

   protected abstract String protocol();

   void query(boolean var1, ChannelPromise var2) {
      DnsQuestion var3 = this.question();
      InetSocketAddress var4 = this.nameServerAddr();
      DnsQuery var5 = this.newQuery(this.id);
      var5.setRecursionDesired(this.recursionDesired);
      var5.addRecord(DnsSection.QUESTION, var3);

      for (DnsRecord var9 : this.additionals) {
         var5.addRecord(DnsSection.ADDITIONAL, var9);
      }

      if (this.optResource != null) {
         var5.addRecord(DnsSection.ADDITIONAL, this.optResource);
      }

      if (logger.isDebugEnabled()) {
         logger.debug("{} WRITE: {}, [{}: {}], {}", this.channel(), this.protocol(), this.id, var4, var3);
      }

      this.sendQuery(var5, var1, var2);
   }

   private void sendQuery(final DnsQuery var1, boolean var2, final ChannelPromise var3) {
      if (this.parent.channelReadyPromise.isSuccess()) {
         this.writeQuery(var1, var2, var3);
      } else {
         Throwable var4 = this.parent.channelReadyPromise.cause();
         if (var4 != null) {
            this.failQuery(var1, var4, var3);
         } else {
            this.parent.channelReadyPromise.addListener(new GenericFutureListener<Future<? super Channel>>() {
               @Override
               public void operationComplete(Future<? super Channel> var1x) {
                  if (var1x.isSuccess()) {
                     DnsQueryContext.this.writeQuery(var1, true, var3);
                  } else {
                     Throwable var2 = var1x.cause();
                     DnsQueryContext.this.failQuery(var1, var2, var3);
                  }
               }
            });
         }
      }
   }

   private void failQuery(DnsQuery var1, Throwable var2, ChannelPromise var3) {
      try {
         this.promise.tryFailure(var2);
         var3.setFailure(var2);
      } finally {
         ReferenceCountUtil.release(var1);
      }
   }

   private void writeQuery(DnsQuery var1, boolean var2, ChannelPromise var3) {
      final ChannelFuture var4 = var2 ? this.channel().writeAndFlush(var1, var3) : this.channel().write(var1, var3);
      if (var4.isDone()) {
         this.onQueryWriteCompletion(var4);
      } else {
         var4.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) {
               DnsQueryContext.this.onQueryWriteCompletion(var4);
            }
         });
      }
   }

   private void onQueryWriteCompletion(ChannelFuture var1) {
      if (!var1.isSuccess()) {
         this.tryFailure("failed to send a query via " + this.protocol(), var1.cause(), false);
      } else {
         final long var2 = this.parent.queryTimeoutMillis();
         if (var2 > 0L) {
            this.timeoutFuture = this.parent.ch.eventLoop().schedule(new Runnable() {
               @Override
               public void run() {
                  if (!DnsQueryContext.this.promise.isDone()) {
                     DnsQueryContext.this.tryFailure("query via " + DnsQueryContext.this.protocol() + " timed out after " + var2 + " milliseconds", null, true);
                  }
               }
            }, var2, TimeUnit.MILLISECONDS);
         }
      }
   }

   void finish(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> var1) {
      DnsResponse var2 = (DnsResponse)var1.content();
      if (var2.count(DnsSection.QUESTION) != 1) {
         logger.warn("Received a DNS response with invalid number of questions: {}", var1);
      } else if (!this.question().equals(var2.recordAt(DnsSection.QUESTION))) {
         logger.warn("Received a mismatching DNS response: {}", var1);
      } else if (this.trySuccess(var1)) {
         return;
      }

      var1.release();
   }

   private boolean trySuccess(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> var1) {
      return this.promise.trySuccess(var1);
   }

   boolean tryFailure(String var1, Throwable var2, boolean var3) {
      if (this.promise.isDone()) {
         return false;
      } else {
         InetSocketAddress var4 = this.nameServerAddr();
         StringBuilder var5 = new StringBuilder(var1.length() + 64);
         var5.append('[').append(var4).append("] ").append(var1).append(" (no stack trace available)");
         Object var6;
         if (var3) {
            var6 = new DnsNameResolverTimeoutException(var4, this.question(), var5.toString());
         } else {
            var6 = new DnsNameResolverException(var4, this.question(), var5.toString(), var2);
         }

         return this.promise.tryFailure((Throwable)var6);
      }
   }

   @Override
   public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> var1) {
      Future var2 = this.timeoutFuture;
      if (var2 != null) {
         this.timeoutFuture = null;
         var2.cancel(false);
      }

      this.parent.queryContextManager.remove(this.nameServerAddr, this.id);
   }
}
