package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

public final class BiDnsQueryLifecycleObserver implements DnsQueryLifecycleObserver {
   private final DnsQueryLifecycleObserver a;
   private final DnsQueryLifecycleObserver b;

   public BiDnsQueryLifecycleObserver(DnsQueryLifecycleObserver var1, DnsQueryLifecycleObserver var2) {
      this.a = ObjectUtil.checkNotNull(var1, "a");
      this.b = ObjectUtil.checkNotNull(var2, "b");
   }

   @Override
   public void queryWritten(InetSocketAddress var1, ChannelFuture var2) {
      try {
         this.a.queryWritten(var1, var2);
      } finally {
         this.b.queryWritten(var1, var2);
      }
   }

   @Override
   public void queryCancelled(int var1) {
      try {
         this.a.queryCancelled(var1);
      } finally {
         this.b.queryCancelled(var1);
      }
   }

   @Override
   public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> var1) {
      try {
         this.a.queryRedirected(var1);
      } finally {
         this.b.queryRedirected(var1);
      }

      return this;
   }

   @Override
   public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion var1) {
      try {
         this.a.queryCNAMEd(var1);
      } finally {
         this.b.queryCNAMEd(var1);
      }

      return this;
   }

   @Override
   public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode var1) {
      try {
         this.a.queryNoAnswer(var1);
      } finally {
         this.b.queryNoAnswer(var1);
      }

      return this;
   }

   @Override
   public void queryFailed(Throwable var1) {
      try {
         this.a.queryFailed(var1);
      } finally {
         this.b.queryFailed(var1);
      }
   }

   @Override
   public void querySucceed() {
      try {
         this.a.querySucceed();
      } finally {
         this.b.querySucceed();
      }
   }
}
