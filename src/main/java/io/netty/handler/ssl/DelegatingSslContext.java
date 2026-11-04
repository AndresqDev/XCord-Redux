package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;

public abstract class DelegatingSslContext extends SslContext {
   private final SslContext ctx;

   protected DelegatingSslContext(SslContext var1) {
      this.ctx = ObjectUtil.checkNotNull(var1, "ctx");
   }

   @Override
   public final boolean isClient() {
      return this.ctx.isClient();
   }

   @Override
   public final List<String> cipherSuites() {
      return this.ctx.cipherSuites();
   }

   @Override
   public final long sessionCacheSize() {
      return this.ctx.sessionCacheSize();
   }

   @Override
   public final long sessionTimeout() {
      return this.ctx.sessionTimeout();
   }

   @Override
   public final ApplicationProtocolNegotiator applicationProtocolNegotiator() {
      return this.ctx.applicationProtocolNegotiator();
   }

   @Override
   public final SSLEngine newEngine(ByteBufAllocator var1) {
      SSLEngine var2 = this.ctx.newEngine(var1);
      this.initEngine(var2);
      return var2;
   }

   @Override
   public final SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3) {
      SSLEngine var4 = this.ctx.newEngine(var1, var2, var3);
      this.initEngine(var4);
      return var4;
   }

   @Override
   protected final SslHandler newHandler(ByteBufAllocator var1, boolean var2) {
      SslHandler var3 = this.ctx.newHandler(var1, var2);
      this.initHandler(var3);
      return var3;
   }

   @Override
   protected final SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      SslHandler var5 = this.ctx.newHandler(var1, var2, var3, var4);
      this.initHandler(var5);
      return var5;
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator var1, boolean var2, Executor var3) {
      SslHandler var4 = this.ctx.newHandler(var1, var2, var3);
      this.initHandler(var4);
      return var4;
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4, Executor var5) {
      SslHandler var6 = this.ctx.newHandler(var1, var2, var3, var4, var5);
      this.initHandler(var6);
      return var6;
   }

   @Override
   public final SSLSessionContext sessionContext() {
      return this.ctx.sessionContext();
   }

   protected abstract void initEngine(SSLEngine var1);

   protected void initHandler(SslHandler var1) {
      this.initEngine(var1.engine());
   }
}
