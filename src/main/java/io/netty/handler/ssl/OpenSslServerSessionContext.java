package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import java.util.concurrent.locks.Lock;

public final class OpenSslServerSessionContext extends OpenSslSessionContext {
   OpenSslServerSessionContext(ReferenceCountedOpenSslContext var1, OpenSslKeyMaterialProvider var2) {
      super(var1, var2, SSL.SSL_SESS_CACHE_SERVER, new OpenSslSessionCache(var1.engineMap));
   }

   public boolean setSessionIdContext(byte[] var1) {
      Lock var2 = this.context.ctxLock.writeLock();
      var2.lock();

      boolean var3;
      try {
         var3 = SSLContext.setSessionIdContext(this.context.ctx, var1);
      } finally {
         var2.unlock();
      }

      return var3;
   }
}
