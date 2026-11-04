package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import java.security.cert.Certificate;
import java.util.Map.Entry;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;

public abstract class OpenSslContext extends ReferenceCountedOpenSslContext {
   OpenSslContext(
      Iterable<String> var1,
      CipherSuiteFilter var2,
      ApplicationProtocolConfig var3,
      int var4,
      Certificate[] var5,
      ClientAuth var6,
      String[] var7,
      boolean var8,
      boolean var9,
      Entry<SslContextOption<?>, Object>... var10
   ) throws SSLException {
      super(var1, var2, toNegotiator(var3), var4, var5, var6, var7, var8, var9, false, var10);
   }

   OpenSslContext(
      Iterable<String> var1,
      CipherSuiteFilter var2,
      OpenSslApplicationProtocolNegotiator var3,
      int var4,
      Certificate[] var5,
      ClientAuth var6,
      String[] var7,
      boolean var8,
      boolean var9,
      Entry<SslContextOption<?>, Object>... var10
   ) throws SSLException {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9, false, var10);
   }

   @Override
   final SSLEngine newEngine0(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      return new OpenSslEngine(this, var1, var2, var3, var4);
   }

   @Override
   protected final void finalize() throws Throwable {
      super.finalize();
      OpenSsl.releaseIfNeeded(this);
   }
}
